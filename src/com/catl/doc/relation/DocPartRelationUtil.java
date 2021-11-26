package com.catl.doc.relation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.fc.ObjectReference;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTCollection;
import wt.introspection.WTIntrospector;
import wt.org.WTPrincipal;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartReferenceLink;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;
import wt.vc.struct.StructHelper;

import com.catl.common.constant.Constant;
import com.catl.common.constant.DocState;
import com.catl.common.constant.PartState;
import com.catl.common.constant.TypeName;
import com.catl.common.util.ClassificationUtil;
import com.catl.common.util.CommonUtil;
import com.catl.common.util.DocUtil;
import com.catl.common.util.PropertiesUtil;
import com.catl.common.util.ZipDoc;
import com.catl.doc.EDatasheetDocUtil;
import com.catl.integration.DrawingInfo;
import com.catl.integration.DrawingSendERP;
import com.catl.integration.ErpResponse;
import com.catl.integration.PIService;
import com.catl.integration.ReleaseUtil;
import com.catl.integration.log.DrawingSendERPLog;
import com.catl.loadData.StrUtils;
import com.catl.part.classification.NodeConfigHelper;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;

public class DocPartRelationUtil {

	private static Logger log = Logger.getLogger(DocPartRelationUtil.class.getName());

	/**
	 * 检查权限
	 * 
	 * @param doc
	 * @param list
	 * @throws WTException
	 */
	public static void checkPermission(WTDocument doc, ArrayList<WTPart> list) throws WTException {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			WTPrincipal userPrincipal = SessionHelper.manager.getPrincipal();
			StringBuffer buffer = new StringBuffer();
			if (!CommonUtil.isSiteAdmin(userPrincipal)) {
				// 不是文档创建者 && 不是文档设计者
				if (!userPrincipal.getName().endsWith(doc.getCreatorName()) && !CommonUtil.checkifDesigner(doc, userPrincipal)) {
					for (WTPart part : list) {
						if (!userPrincipal.getName().endsWith(part.getCreatorName()) && !CommonUtil.checkifDesigner(part, userPrincipal)) {
							buffer.append("无法操作零部件" + part.getNumber() + "，因为你不是该零部件的创建者或设计者\n");
						}
					}
					if (buffer.length() > 0) {
						throw new WTException(buffer.toString());
					}
				}
			}
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}

	}

	/**
	 * 增加参考文档关联物料
	 * 
	 * @param doc
	 * @param list
	 * @throws WTException
	 */
	public static void addRef(WTDocument doc, ArrayList<WTPart> list) throws WTException {
		Transaction trx = new Transaction();
		try {
			trx.start();
			for (WTPart part : list) {
				WTPartReferenceLink referencelink = WTPartReferenceLink.newWTPartReferenceLink(part, (WTDocumentMaster) doc.getMaster());
				PersistenceServerHelper.manager.insert(referencelink);
			}
			trx.commit();
			trx = null;
		} finally {
			if (trx != null)
				trx.rollback();
		}
	}

	/**
	 * 删除参考文档关联物料
	 * 
	 * @param doc
	 * @param list
	 * @throws WTException
	 */
	public static void deleteRef(WTDocument doc, ArrayList<WTPart> list) throws WTException {
		Transaction trx = new Transaction();
		try {
			trx.start();
			for (WTPart part : list) {
				String REFERENCES_ROLE_OID = ((WTIntrospector.getLinkInfo(WTPartReferenceLink.class).isRoleA(WTPartReferenceLink.REFERENCES_ROLE)) ? WTPartReferenceLink.ROLE_AOBJECT_REF : WTPartReferenceLink.ROLE_BOBJECT_REF) + "." + ObjectReference.KEY;
				QuerySpec qs = new QuerySpec(WTDocumentMaster.class, WTPartReferenceLink.class);

				qs.appendWhere(new SearchCondition(WTPartReferenceLink.class, REFERENCES_ROLE_OID, SearchCondition.EQUAL, PersistenceHelper.getObjectIdentifier(doc.getMaster())), new int[] { 1 });

				String sql = qs.toString();
				log.info("handleDeleteRelation sql=" + sql);

				QueryResult qr = PersistenceServerHelper.manager.expand(part, WTPartReferenceLink.REFERENCES_ROLE, qs, false);
				while (qr.hasMoreElements()) {
					// delete reference links between parts and docs
					WTPartReferenceLink refLink = (WTPartReferenceLink) qr.nextElement();
					PersistenceServerHelper.manager.remove(refLink);
				}

			}
			trx.commit();
			trx = null;
		} finally {
			if (trx != null)
				trx.rollback();
		}
	}

	/**
	 * 增加描述文档关联物料
	 * 
	 * @param doc
	 * @param list
	 * @throws WTException
	 */
	public static void addDesc(WTDocument doc, ArrayList<WTPart> list) throws WTException {
		for (WTPart part : list) {
			WTPartDescribeLink describelink = WTPartDescribeLink.newWTPartDescribeLink(part, doc);
			PersistenceServerHelper.manager.insert(describelink);
			if (doc.getLifeCycleState().toString().equals(DocState.RELEASED) && part.getLifeCycleState().toString().equals(PartState.RELEASED)) {
				sendERPAddDesc(part, doc);
			}
		}
	}

	/**
	 * 删除描述文档关联物料
	 * 
	 * @param doc
	 * @param list
	 * @throws WTException
	 */
	public static void deleteDesc(WTDocument doc, ArrayList<WTPart> list) throws WTException {
		for (WTPart part : list) {
			QueryResult qr = StructHelper.service.navigateDescribedBy(part, WTPartDescribeLink.class, false);
			while (qr.hasMoreElements()) {
				// delete describe links between parts and docs
				WTPartDescribeLink link = (WTPartDescribeLink) qr.nextElement();
				if (PersistenceHelper.isEquivalent(doc, link.getDescribedBy())) {
					PersistenceServerHelper.manager.remove(link);
					if (doc.getLifeCycleState().toString().equals(DocState.RELEASED) && part.getLifeCycleState().toString().equals(PartState.RELEASED)) {
						sendERPDeleteDesc(part, doc);
					}
					break;
				}
			}
		}
	}

	/**
	 * 发送 删除文档关系与实际图纸 给ERP
	 * 
	 * @param part
	 * @param doc
	 *            doc_type_autocadDrawing doc_type_pcbaDrawing
	 *            doc_type_gerberDoc
	 * @throws WTException
	 */
	public static void sendERPDeleteDesc(WTPart part, WTDocument doc) throws WTException {
		TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(doc);
		String type = ti.getTypename();
		boolean autocad = type.contains(TypeName.doc_type_autocadDrawing);
		boolean pcbaANDgerber = type.contains(TypeName.doc_type_pcbaDrawing) || type.contains(TypeName.doc_type_gerberDoc);
		if (autocad || pcbaANDgerber) {
			PIService service = PIService.getInstance();
			List<DrawingInfo> drawList = new ArrayList<DrawingInfo>();
			DrawingInfo draw = new DrawingInfo();
			draw.setPartNumber(part.getNumber());
			draw.setDrawingNumber("");
			draw.setDrawingVersion("");
			drawList.add(draw);
			String errorMsg = "";
			try {
				ErpResponse response = service.sendDrawing(drawList, Constant.COMPANY);
				if (response.isSuccess() == false) {
					errorMsg = response.getMessage().get(0).getText();
				}

				DrawingSendERPLog dlog = new DrawingSendERPLog();
				dlog.setObjectInPromotionNumber(part.getNumber());
				dlog.setObjectInPromotionType(TypeIdentifierUtility.getTypeIdentifier(part).getTypename());
				dlog.setObjectInPromotionVersion(part.getVersionIdentifier().getValue());
				dlog.setObjectInPromotionIteration(part.getIterationIdentifier().getValue());
				dlog.setRelationObjectNumber(doc.getNumber());
				dlog.setRelationObjectType(type);
				dlog.setPartNumber(part.getNumber());
				PersistenceHelper.manager.save(dlog);

				DrawingSendERP drt = new DrawingSendERP();
				drt.setPartNumber(part.getNumber());
				drt.setAddress("");
				drt.setFileName("");
				String ret = service.sendFile(drt, Constant.COMPANY);
				if (ret != null)
					errorMsg = errorMsg + "\n" + ret;
			} catch (Exception e) {
				e.printStackTrace();
				throw new WTException("图纸发送SAP失败，请联系管理员！错误描述：" + e.getMessage());
			} finally {
				if (!StrUtils.isEmpty(errorMsg)) {
					throw new WTException("图纸发送SAP失败，请联系管理员！错误描述：" + errorMsg);
				}
			}
		}
	}

	/**
	 * 发送 添加文档关系与实际图纸 给ERP
	 * 
	 * @param part
	 * @param doc
	 *            doc_type_autocadDrawing doc_type_pcbaDrawing
	 *            doc_type_gerberDoc
	 * @throws WTException
	 */
	public static void sendERPAddDesc(WTPart part, WTDocument doc) throws WTException {
		TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(doc);
		String type = ti.getTypename();
		boolean autocad = type.contains(TypeName.doc_type_autocadDrawing);
		boolean pcbaANDgerber = type.contains(TypeName.doc_type_pcbaDrawing) || type.contains(TypeName.doc_type_gerberDoc);
		boolean softDoc = type.contains(TypeName.softwareDoc);
		if (autocad || pcbaANDgerber || softDoc) {
			PIService service = PIService.getInstance();
			List<DrawingInfo> drawList = new ArrayList<DrawingInfo>();
			DrawingInfo draw = new DrawingInfo();
			draw.setPartNumber(part.getNumber());
			draw.setDrawingNumber(doc.getNumber());
			draw.setDrawingVersion(doc.getVersionIdentifier().getValue());
			drawList.add(draw);
			String errorMsg = "";
			try {
				ErpResponse response = service.sendDrawing(drawList, Constant.COMPANY);
				if (response.isSuccess() == false) {
					errorMsg = response.getMessage().get(0).getText();
				}
				QueryResult qr;
				if (autocad){
					qr = ContentHelper.service.getContentsByRole(doc, ContentRoleType.SECONDARY);
				}else{
					qr = ContentHelper.service.getContentsByRole(doc, ContentRoleType.PRIMARY);
				}
				while (qr.hasMoreElements()) {
					ApplicationData fileContent = (ApplicationData) qr.nextElement();
					String strFileName = URLDecoder.decode(fileContent.getFileName(), "utf-8");
					if (autocad){
						if (!strFileName.equalsIgnoreCase(part.getNumber()+ReleaseUtil.RELEASE_Suffix + ReleaseUtil.PDF_Suffix)) {// 已盖章图纸
							continue;
						}
						String filename = ReleaseUtil.ROOT_DIRECTORY + strFileName;// 存放路径+文件名
						InputStream is = ContentServerHelper.service.findContentStream(fileContent);// 图纸流
						FileOutputStream fos = new FileOutputStream(new File(filename));
						byte[] buffer = new byte[1024];
						int byteread = 0; // 读取的字节数
						while ((byteread = is.read(buffer)) > 0) {
							fos.write(buffer, 0, byteread);
						}
						fos.flush();
						is.close();
						fos.close();
					}else if(pcbaANDgerber){
						strFileName = part.getNumber()+".zip";
						String filename = ReleaseUtil.ROOT_DIRECTORY + part.getNumber();// 存放路径+文件名
						
						QueryResult pcbdocs = PartDocServiceCommand.getAssociatedDescribeDocuments(part);
						pcbdocs = new LatestConfigSpec().process(pcbdocs);
						List<WTDocument> doclist = new ArrayList<WTDocument>();
						while(pcbdocs.hasMoreElements()){
							WTDocument pcbdoc = (WTDocument) pcbdocs.nextElement();
							TypeIdentifier pcbti = TypeIdentifierUtility.getTypeIdentifier(pcbdoc);
							String pcbtype = pcbti.getTypename();
							boolean isPcbaOrGerber = pcbtype.contains(TypeName.doc_type_pcbaDrawing) || pcbtype.contains(TypeName.doc_type_gerberDoc);
							if(isPcbaOrGerber){
								doclist.add(pcbdoc);
							}
						}
						
						ZipDoc.zipDocs(doclist, filename);
					}else if(softDoc){
						String filename = ReleaseUtil.ROOT_DIRECTORY + part.getNumber();// 存放路径+文件名
						InputStream is = ContentServerHelper.service.findContentStream(fileContent);// 图纸流
						FileOutputStream fos = new FileOutputStream(new File(filename));
						byte[] buffer = new byte[1024];
						int byteread = 0; // 读取的字节数
						while ((byteread = is.read(buffer)) > 0) {
							fos.write(buffer, 0, byteread);
						}
						fos.flush();
						is.close();
						fos.close();
					}
					

					DrawingSendERPLog dlog = new DrawingSendERPLog();
					dlog.setObjectInPromotionNumber(part.getNumber());
					dlog.setObjectInPromotionType(TypeIdentifierUtility.getTypeIdentifier(part).getTypename());
					dlog.setObjectInPromotionVersion(part.getVersionIdentifier().getValue());
					dlog.setObjectInPromotionIteration(part.getIterationIdentifier().getValue());
					dlog.setRelationObjectNumber(doc.getNumber());
					dlog.setRelationObjectType(type);
					dlog.setPartNumber(part.getNumber());
					dlog.setPartVersion(part.getVersionIdentifier().getValue());
					dlog.setPartIteration(part.getIterationIdentifier().getValue());
					dlog.setRootPath(ReleaseUtil.ROOT_DIRECTORY_MAPPING);
					dlog.setFileName(strFileName);
					PersistenceHelper.manager.save(dlog);

					DrawingSendERP drt = new DrawingSendERP();
					drt.setPartNumber(part.getNumber());
					drt.setAddress(ReleaseUtil.ROOT_DIRECTORY_MAPPING);
					drt.setFileName(strFileName);
					String ret = service.sendFile(drt, Constant.COMPANY);
					if (ret != null)
						errorMsg = errorMsg + "\n" + ret;
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new WTException("图纸发送SAP失败，请联系管理员！错误描述：" + e.getMessage());
			} finally {
				if (!StrUtils.isEmpty(errorMsg)) {
					throw new WTException("图纸发送SAP失败，请联系管理员！错误描述：" + errorMsg);
				}
			}
		}
	}

	public static int queryWTPartReferenceLinkSize(WTPart part) throws WTException {
		QuerySpec qs = new QuerySpec(WTPartReferenceLink.class);
		qs.appendWhere(new SearchCondition(WTPartReferenceLink.class, WTPartReferenceLink.ROLE_AOBJECT_REF + "." + ObjectReference.KEY, SearchCondition.EQUAL, PersistenceHelper.getObjectIdentifier(part)), new int[] { 0 });

		log.info("queryWTPartReferenceLinkSize sql=" + qs);
		QueryResult qr = PersistenceHelper.manager.find(qs);

		return qr.size();
	}

	public static String queryNodeName(WTPart part) throws WTException {
		LWCStructEnumAttTemplate node = NodeConfigHelper.getClassificationNode(part.getNumber().substring(0, 6));
		return ClassificationUtil.getDisplayName(node);
	}

	public static boolean isDatasheet(WTDocument doc){
		boolean flag = false;
		String doctype = TypeIdentifierUtility.getTypeIdentifier(doc).getTypename();
		if (doctype.endsWith(TypeName.doc_type_EDatasheetDoc)) {
			flag = true;
		}
		return flag;
	}
	public static boolean isNeedCheckDatasheet(WTPart part) throws WTException {
		boolean flag = false;
		if (!part.getState().toString().equalsIgnoreCase(PartState.WRITING) && !part.getState().toString().equalsIgnoreCase(PartState.MODIFICATION) && !part.getState().toString().equalsIgnoreCase(PartState.DISABLEDFORDESIGN)) {
			String datasheet = PropertiesUtil.getValueByKey("datasheet");
			if (datasheet != null) {
				String[] datasheetArr = datasheet.split(",");
				for (String cls : datasheetArr) {
					if (part.getNumber().startsWith(cls)) {
						flag = true;
						break;
					}
				}
			}
		}
		return flag;
	}
	
	public static StringBuffer docCheckDatasheet(WTDocument doc,List<WTPart> list) throws WTException{
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		StringBuffer buffer = new StringBuffer();
		try{
			if(isDatasheet(doc)){
				for(WTPart part : list){
					if(isNeedCheckDatasheet(part)){
						int refLinkSize = queryWTPartReferenceLinkSize(part);
						if (refLinkSize == 1)
							buffer.append("零部件" + part.getNumber() + "属于“" + DocPartRelationUtil.queryNodeName(part) + "分类”，必须要关联一份Datasheet文件，如果要移除，请先为该零部件关联一份正确的Datasheet文件后再进行移除\n");
					}
				}
			}
		}finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return buffer;
	}
	
	public static StringBuffer partCheckDatasheet(WTPart part,WTCollection docs,boolean isRefDoc) throws WTException{
		StringBuffer buffer = new StringBuffer();
		if (!isRefDoc)
			return buffer;
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		try{
			if(isNeedCheckDatasheet(part)){
				int refLinkSize = queryWTPartReferenceLinkSize(part);
				int datasheetSize = 0;
				for (Iterator it = docs.persistableIterator(); it.hasNext();){
					WTDocumentMaster docMaster = (WTDocumentMaster)it.next();
					WTDocument doc = DocUtil.getLatestWTDocument(docMaster.getNumber());
					if(isDatasheet(doc))
						datasheetSize++;
				}
				if(datasheetSize > 0 && refLinkSize <= datasheetSize){
					buffer.append("零部件" + part.getNumber() + "属于“" + DocPartRelationUtil.queryNodeName(part) + "分类”，必须要关联一份Datasheet文件，如果要移除，请先为该零部件关联一份正确的Datasheet文件后再进行移除\n");
				}
			}
		}finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return buffer;
	}
}
