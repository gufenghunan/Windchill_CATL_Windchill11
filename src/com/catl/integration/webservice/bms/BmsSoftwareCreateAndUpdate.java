package com.catl.integration.webservice.bms;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.json.JSONException;
import org.json.JSONObject;

import com.catl.common.constant.PartState;
import com.catl.common.constant.TypeName;
import com.catl.common.toolbox.data.UpdateAllPartSpec;
import com.catl.common.util.PartUtil;
import com.catl.doc.soft.util.CatlSoftDocUtil;
import com.catl.ecad.utils.FTPUitl;
import com.catl.ecad.utils.IBAUtility;
import com.catl.line.constant.ConstantLine;
import com.catl.line.exception.LineException;
import com.catl.line.util.CommonUtil;
import com.catl.line.util.NodeUtil;
import com.catl.part.CatlPartNewNumber;
import com.catl.part.CreateCatlPartProcessor;
import com.catl.part.PartConstant;
import com.catl.part.PartLoadNameSourceUtil;
import com.catl.require.constant.ConstantRequire;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;

import wt.access.AccessControlHelper;
import wt.access.AccessPermission;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.content.FormatContentHolder;
import wt.doc.DocumentType;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.doc.WTDocumentMasterIdentity;
import wt.fc.IdentityHelper;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainer;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartHelper;
import wt.part.WTPartIDSeq;
import wt.part.WTPartMaster;
import wt.pom.PersistenceException;
import wt.pom.Transaction;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.type.TypeDefinitionReference;
import wt.type.TypedUtility;
import wt.util.WTPropertyVetoException;
import wt.vc.wip.WorkInProgressHelper;

@WebService(serviceName = "BmsSoftwareCreateAndUpdate")
public class BmsSoftwareCreateAndUpdate {
	private static final String CLASSNAME = BmsSoftwareCreateAndUpdate.class.getName();
	private static Logger log = Logger.getLogger(CLASSNAME);

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@WebMethod(operationName = "createSoftware")
	public String createSoftware(String jsonstr) {
		Transaction tx = new Transaction();
		try {
			tx.start();
			JSONObject json = new JSONObject(jsonstr);
			StringBuffer message = new StringBuffer();

			String docName = json.getString("docName");
			String swFilename = json.getString("swFileName");
			String reportFileName = json.getString("reportFileName");
			String parentPN = json.getString("parentPN");
			String filepath = "softDocument";
			String flag = "E";
			String swPn = "";

			if (StringUtils.isBlank(docName)) {
				message.append("文档名称为空！\n");
			}
			if (StringUtils.isBlank(parentPN)) {
				message.append("父件PN为空!\n");
			} else {
				// if(parentPN.startsWith("P")){
				WTPart parentPart = CommonUtil.getLatestWTpartByNumber(parentPN);
				if (parentPart != null) {

					WTPart part = createPartByInfo(json);
					if (part != null) {
						WTDocument doc = createDocByPart(part, docName, swFilename, reportFileName, filepath);
						if (doc != null) {
							WTPartDescribeLink describlink = WTPartDescribeLink.newWTPartDescribeLink(part, doc);
							PersistenceServerHelper.manager.insert(describlink);
							swPn = part.getNumber();
							flag = "S";
						} else {
							message.append("软件文档创建失败！\n");
						}
					} else {
						message.append("软件PN创建失败！\n");
					}
				} else {
					message.append("PN【").append(parentPN).append("】在系统中不存在或者您没有访问权限！\n");
				}
				// }else{
				// message.append("父件PN必须为P开头的物料！\n");
				// }
			}

			Element root = new Element("root");
			Document result = new Document(root);

			Element item = new Element("result");
			
			item.addContent(new Element("swPn").setText(swPn));
			item.addContent(new Element("flag").setText(flag));
			item.addContent(new Element("message").setText(message.toString()));
			
			root.addContent(item);
			// com.ptc.xworks.workflow.builders.ExtendedCommonTeamTableBuilder
			XMLOutputter xmlout = new XMLOutputter(Format.getPrettyFormat());
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			xmlout.output(result,bo);
			String xmlStr = bo.toString();
			
			log.debug(xmlStr);
			System.out.println(xmlStr);
			// com.ptc.windchill.enterprise.team.validators.TeamCCValidator
			tx.commit();
			tx = null;
			return xmlStr;
		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
				tx = null;
			}
			e.printStackTrace();
		}
		return null;

	}

	@WebMethod(operationName = "updateSoftware")
	public String updateSoftware(String jsonstr) {
		Transaction tx = new Transaction();
		try {
			tx.start();
			JSONObject json = new JSONObject(jsonstr);
			StringBuffer message = new StringBuffer();

			String docName = json.getString("docName");
			String swFilename = json.getString("swFileName");
			String reportFileName = json.getString("reportFileName");
			String swPN = json.getString("swPN");
			String filepath = "softDocument";
			String flag = "E";
			//String swPn = "";

			if (StringUtils.isBlank(docName)) {
				message.append("文档名称为空！\n");
			}
			if (StringUtils.isBlank(swPN)) {
				message.append("软件PN为空!\n");
			} else {
				WTPart swPart = CommonUtil.getLatestWTpartByNumber(swPN);
				if (swPart != null) {			
					if(swPart.getLifeCycleState().equals(State.toState(PartState.DESIGN))
							||swPart.getLifeCycleState().equals(State.toState(PartState.DESIGNMODIFICATION))){
						
						swPart = updatePartByInfo(json);
						WTDocument doc = CommonUtil.getLatestWTDocByNumber(swPN);

						if (doc != null) {
							message.append(updateDocByPart(doc, docName, swFilename, reportFileName, filepath));
							flag = "S";
						} else {
							message.append("软件文档在系统中不存在！\n");
						}
					}else{
						message.append("软件PN【").append(swPN).append("】不是设计或者设计修改状态，不允许修改！\n");
					}
					
				}else{
					message.append("软件PN【").append(swPN).append("】在系统中不存在或者您没有访问权限！\n");
				}
					
				 
			}

			Element root = new Element("root");
			Document result = new Document(root);

			Element item = new Element("result");
			item.addContent(new Element("swPn").setText(swPN));
			item.addContent(new Element("flag").setText(flag));
			item.addContent(new Element("message").setText(message.toString()));
			
			root.addContent(item);

			log.info(result.toString());
			XMLOutputter xmlout = new XMLOutputter(Format.getPrettyFormat());
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			xmlout.output(result,bo);
			String xmlStr = bo.toString();
			
			log.debug(xmlStr);
			System.out.println(xmlStr);
			// com.ptc.windchill.enterprise.team.validators.TeamCCValidator
			tx.commit();
			tx = null;
			return xmlStr;
		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
				tx = null;
			}
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 创建部件
	 * 
	 * @param json
	 * @return
	 * @throws Exception
	 */
	public static WTPart createPartByInfo(JSONObject json) throws Exception {
		String parentPN = json.getString("parentPN");
		WTPart part = null;
		WTPart parentpart = CommonUtil.getLatestWTpartByNumber(parentPN);
		if (parentpart == null) {
			log.error("父件PN" + parentPN + "在系统中不存在！");
			return null;
		} else {
			Map<String, String> clsnamesource = PartLoadNameSourceUtil.getPartClsNameSource();
			String docFolderPath = "/" + parentpart.getFolderPath().split("/")[1];
			docFolderPath = docFolderPath + "/" + parentpart.getFolderPath().split("/")[2] + "/零部件";
			Folder folder = CatlSoftDocUtil.getFolder(docFolderPath, parentpart.getContainer());

			// String cls = json.getString(PartConstant.IBA_CLS);
			String name = "";// json.getString("docName");
			String source = "";
			String unit = "";
			String oldnum = "";// json.getString("oldnum");
			String description = "";// json.getString("description");
			String uiforcecreate = "";// json.getString("forcecreate");
			String feature = json.getString("feature");
			String iscustomer = "否";// json.getString("iscustomer");
			String platform = "A";// json.getString("platform");
			String openMould = "";

			String partinfo = json.getString("partinfo");

			String[] partvalue = partinfo.split(",");

			Map<String, String> partmap = new HashMap<>();

			for (int i = 0; i < partvalue.length; i++) {
				String str = partvalue[i];
				String[] namevalue = str.split(":");
				partmap.put(namevalue[0], namevalue[1]);
			}

			for (String key : partmap.keySet()) {
				log.debug(key);
			}
			String cls = partmap.get("cls");

			// String namesource = clsnamesource.get(cls);
			if (clsnamesource.containsKey(cls)) {
				String sourcename = clsnamesource.get(cls);
				// System.out.println("Source and name:\t" + sourcename);
				String[] nsarray = sourcename.split("qqqq;;;;");
				if (nsarray.length == 4) {
					name = nsarray[0];
					source = nsarray[1].split(",")[1];
					unit = nsarray[2];
					openMould = nsarray[3];
					// System.out.println("Source:\t" + unit);
				}
			}

			if (StringUtils.isNotBlank(feature)) {
				name = name + "_" + feature;
			}

			String number = PersistenceHelper.manager.getNextSequence(WTPartIDSeq.class);
			part = CommonUtil.createPart(number, name, TypeName.CATLPart, source, unit, oldnum, uiforcecreate,
					description, folder.getIdentity(), parentpart.getContainer().toString());

			WTPartMaster partmaster = part.getMaster();
			IBAUtility ibafeature = new IBAUtility(part);
			IBAUtility ibamaster = new IBAUtility(partmaster);
			if (StringUtils.isNotBlank(feature)) {
				ibafeature.setIBAValue(PartConstant.CATL_Feature, feature);
			}
			if (StringUtils.isNotBlank(iscustomer)) {
				ibafeature.setIBAValue(PartConstant.Is_Customer, iscustomer);
			}
			if (StringUtils.isNotBlank(platform)) {
				ibamaster.setIBAValue(ConstantRequire.iba_CATL_Platform, platform);
			}

			if (StringUtils.isNotBlank(openMould)) {
				ibafeature.setIBAValue(PartConstant.OpenMould, openMould);
			}

			part = (WTPart) ibafeature.updateAttributeContainer(part);
			ibafeature.updateIBAHolder(part);

			ibamaster.setIBAValue(PartConstant.IBA_CATL_Maturity, "1");

			partmaster = (WTPartMaster) ibamaster.updateAttributeContainer(partmaster);
			ibamaster.updateIBAHolder(partmaster);

			if (!StringUtils.isEmpty(cls)) {
				LWCStructEnumAttTemplate lwc = null;
				// String lwcname = null;
				// 获取分类名称
				IBAUtility iba = new IBAUtility(part);

				iba.setIBAValue(PartConstant.IBA_CLS, cls);

				part = (WTPart) iba.updateAttributeContainer(part);
				// iba.updateIBAHolder(part);

				lwc = NodeUtil.getClfNodeByName(cls);

				for (String key : partmap.keySet()) {
					String ibaname = key;
					// String datatype=clfattrmap.getString("type");
					if (!ibaname.equals(ConstantLine.var_clf)) {// 不是分类
						String ibavalue = partmap.get(key);
						if (!StringUtils.isEmpty(ibavalue)) {
							iba.setIBAValue(ibaname, ibavalue);
						}
					}
					// 修改为新编码
				}

				part = (WTPart) iba.updateAttributeContainer(part);
				// iba.updateIBAHolder(part);

				// CreateCatlPartProcessor.setDefaultMaturity(part);
				// CreateCatlPartProcessor.updateSource(part);
				// CreateCatlPartProcessor.renamePart(part);
				// CreateCatlPartProcessor.updateFAEStatus(part);

				String newNum = CatlPartNewNumber.createPartNewnumber(lwc, part.getContainerName());
				WTPartMaster partMaster = (WTPartMaster) part.getMaster();
				try {
					WTPartHelper.service.changeWTPartMasterIdentity(partMaster, part.getName(), newNum,
							part.getOrganization());
				} catch (WTPropertyVetoException e) {
					e.printStackTrace();
				}

				String specification = UpdateAllPartSpec.getAllAttr(part);
				log.debug(specification);
				IBAUtility ibaspec = new IBAUtility(part);

				ibaspec.setIBAValue("specification", specification);

				ibaspec.updateAttributeContainer(part);
				ibaspec.updateIBAHolder(part);

				if (PartUtil.isSWPart(part)) {
					boolean enforced = SessionServerHelper.manager.setAccessEnforced(false);
					try {
						if (part.getLifeCycleState().equals(State.toState(PartState.WRITING))) {
							LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) part,
									State.toState(PartState.DESIGN));
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						SessionServerHelper.manager.setAccessEnforced(enforced);
					}
				}

			} else {
				throw new LineException("获取不到分类");
			}
		}
		return part;
	}

	/**
	 * 根据软件PN创建软件文档
	 * 
	 * @param part
	 * @param docName
	 * @param swFilename
	 * @param reportFileName
	 * @param filepath
	 * @return
	 * @throws Exception
	 */
	public static WTDocument createDocByPart(WTPart part, String docName, String swFilename, String reportFileName,
			String filepath) throws Exception {
		WTDocument doc = WTDocument.newWTDocument(part.getNumber(), docName, DocumentType.getDocumentTypeDefault());
		WTContainer container = part.getContainer();
		String docFolderPath = "/" + part.getFolderPath().split("/")[1];
		docFolderPath = docFolderPath + "/" + part.getFolderPath().split("/")[2] + "/设计图档/产品类软件";
		Folder folder = CatlSoftDocUtil.getFolder(docFolderPath, container);
		FolderHelper.assignLocation(doc, folder);
		TypeDefinitionReference tdr = TypedUtility.getTypeDefinitionReference(TypeName.softwareDoc);// 设置软类型
		doc.setTypeDefinitionReference(tdr);
		doc = (WTDocument) PersistenceHelper.manager.save(doc);

		ContentHolder ch = (ContentHolder) doc;
		if (StringUtils.isNotBlank(reportFileName)) {
			InputStream reportIS = FTPUitl.getFileInputStream(filepath, reportFileName);
			if (reportIS != null) {
				ApplicationData reportContent = ApplicationData.newApplicationData(ch);
				reportContent.setFileName(reportFileName);
				reportContent.setRole(ContentRoleType.PRIMARY);
				reportContent = ContentServerHelper.service.updateContent(ch, reportContent, reportIS);
				doc = (WTDocument) ContentServerHelper.service.updateHolderFormat(doc);
			}
		}
		// rjbin = FTPUitl.getFileInputStream(filePath, swFileName);
		// ContentHolder ch = (ContentHolder) doc;
		if (StringUtils.isNotBlank(swFilename)) {
			InputStream swis = FTPUitl.getFileInputStream(filepath, swFilename);
			if (swis != null) {
				ApplicationData swContent = ApplicationData.newApplicationData(ch);
				swContent.setFileName(swFilename);
				swContent.setRole(ContentRoleType.toContentRoleType("SOFTPACKAGE_ATTACHMENT"));
				swContent = ContentServerHelper.service.updateContent(ch, swContent, swis);
				doc = (WTDocument) ContentServerHelper.service.updateHolderFormat(doc);
			}

		}

		return doc;
	}

	/**
	 * 根据软件PN创建软件文档
	 * 
	 * @param part
	 * @param docName
	 * @param swFilename
	 * @param reportFileName
	 * @param filepath
	 * @return
	 * @throws Exception
	 */
	public static String updateDocByPart(WTDocument doc, String docName, String swFilename, String reportFileName,
			String filepath) throws Exception {

		if (!docName.equals(doc.getName())) {// 名称更新不需要判别版本
			WTDocumentMaster master = (WTDocumentMaster) doc.getMaster();
			WTDocumentMasterIdentity partmasteridentity = null;
			partmasteridentity = (WTDocumentMasterIdentity) master.getIdentificationObject();
			partmasteridentity.setName(docName);
			master = (WTDocumentMaster) IdentityHelper.service.changeIdentity(master, partmasteridentity);
		}
		if (WorkInProgressHelper.isCheckedOut(doc)) {
			return "对象【" + doc.getDisplayIdentifier() + "】的文档在PLM中被检出。";
		}
		String state = doc.getState().toString();
		if (!state.equals(PartState.DESIGN) && !state.equals(PartState.DESIGNMODIFICATION)) {
			return "对象【" + doc.getDisplayIdentifier() + "】状态为【" + doc.getLifeCycleState().getDisplay(Locale.CHINA)
					+ "】不能修改。";
		}

		doc = (WTDocument) CommonUtil.checkoutObject(doc);

		ContentHolder ch = (ContentHolder) doc;
		if (StringUtils.isNotBlank(reportFileName)) {
			InputStream reportIS = FTPUitl.getFileInputStream(filepath, reportFileName);
			if (reportIS != null) {
				ApplicationData reportContent = null;
				QueryResult qr = ContentHelper.service.getContentsByRole(doc, ContentRoleType.PRIMARY);
				if(qr.hasMoreElements()){
					reportContent = (ApplicationData) qr.nextElement();
				}
				
				//ContentServerHelper.service.deleteContent(ch, contentitem);
				if(reportContent == null){
					reportContent = ApplicationData.newApplicationData(ch);
				}
				//ApplicationData reportContent = ApplicationData.newApplicationData(ch);
				reportContent.setFileName(reportFileName);
				reportContent.setRole(ContentRoleType.PRIMARY);
				reportContent = ContentServerHelper.service.updateContent(ch, reportContent, reportIS);
				doc = (WTDocument) ContentServerHelper.service.updateHolderFormat(doc);
			}
		}

		if (StringUtils.isNotBlank(swFilename)) {
			InputStream swis = FTPUitl.getFileInputStream(filepath, swFilename);
			if (swis != null) {
				QueryResult qr = ContentHelper.service.getContentsByRole(ch,
						ContentRoleType.toContentRoleType("SOFTPACKAGE_ATTACHMENT"));
				while (qr.hasMoreElements()) {
					ApplicationData ap = (ApplicationData) qr.nextElement();
					ContentServerHelper.service.deleteContent(ch, ap);
				}
				ApplicationData swContent = ApplicationData.newApplicationData(ch);
				swContent.setFileName(swFilename);
				swContent.setRole(ContentRoleType.toContentRoleType("SOFTPACKAGE_ATTACHMENT"));
				swContent = ContentServerHelper.service.updateContent(ch, swContent, swis);
				doc = (WTDocument) ContentServerHelper.service.updateHolderFormat(doc);
			}

		}

		doc = (WTDocument) CommonUtil.checkinObject(doc, "更新软件包");

		return "S";
	}

	/**
	 * 创建部件
	 * 
	 * @param json
	 * @return
	 * @throws Exception
	 */
	public static WTPart updatePartByInfo(JSONObject json) throws Exception {
		String swPN = json.getString("swPN");
		WTPart swpart = CommonUtil.getLatestWTpartByNumber(swPN);
		if (swpart == null) {
			log.error("软件PN" + swPN + "在系统中不存在！");
			return null;
		} else {

			// String cls = json.getString(PartConstant.IBA_CLS);

			String feature = json.getString("feature");

			String partinfo = json.getString("partinfo");

			String[] partvalue = partinfo.split(",");

			Map<String, String> partmap = new HashMap<>();

			for (int i = 0; i < partvalue.length; i++) {
				String str = partvalue[i];
				String[] namevalue = str.split(":");
				partmap.put(namevalue[0], namevalue[1]);
			}

			for (String key : partmap.keySet()) {
				log.debug(key);
			}
			String cls = partmap.get("cls");

			swpart = (WTPart) CommonUtil.checkoutObject(swpart);
			
			IBAUtility ibafeature = new IBAUtility(swpart);

			if (StringUtils.isNotBlank(feature)) {
				ibafeature.setIBAValue(PartConstant.CATL_Feature, feature);
			}

			swpart = (WTPart) ibafeature.updateAttributeContainer(swpart);
			ibafeature.updateIBAHolder(swpart);

			if (!StringUtils.isEmpty(cls)) {
				LWCStructEnumAttTemplate lwc = null;
				// String lwcname = null;
				// 获取分类名称
				IBAUtility iba = new IBAUtility(swpart);

				iba.setIBAValue(PartConstant.IBA_CLS, cls);

				iba.updateAttributeContainer(swpart);
				iba.updateIBAHolder(swpart);

				lwc = NodeUtil.getClfNodeByName(cls);

				for (String key : partmap.keySet()) {
					String ibaname = key;
					// String datatype=clfattrmap.getString("type");
					if (!ibaname.equals(ConstantLine.var_clf)) {// 不是分类
						String ibavalue = partmap.get(key);
						if (!StringUtils.isEmpty(ibavalue)) {
							iba.setIBAValue(ibaname, ibavalue);
						}
					}
					// 修改为新编码
				}

				iba.updateAttributeContainer(swpart);
				iba.updateIBAHolder(swpart);

				//CreateCatlPartProcessor.renamePart(swpart);

				String specification = UpdateAllPartSpec.getAllAttr(swpart);
				log.debug(specification);
				IBAUtility ibaspec = new IBAUtility(swpart);

				ibaspec.setIBAValue("specification", specification);

				ibaspec.updateAttributeContainer(swpart);
				ibaspec.updateIBAHolder(swpart);
				
				swpart = (WTPart) CommonUtil.checkinObject(swpart, "软件更新");

				if (PartUtil.isSWPart(swpart)) {
					boolean enforced = SessionServerHelper.manager.setAccessEnforced(false);
					try {
						if (swpart.getLifeCycleState().equals(State.toState(PartState.WRITING))) {
							LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) swpart,
									State.toState(PartState.DESIGN));
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						SessionServerHelper.manager.setAccessEnforced(enforced);
					}
				}

			} else {
				throw new LineException("获取不到分类");
			}
		}
		return swpart;
	}
}
