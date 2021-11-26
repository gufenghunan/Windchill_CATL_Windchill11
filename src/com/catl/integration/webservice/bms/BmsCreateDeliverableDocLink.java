package com.catl.integration.webservice.bms;

import java.beans.PropertyVetoException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import com.catl.common.constant.TypeName;
import com.catl.common.util.PartUtil;
import com.catl.doc.CatlDocNewNumber;
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
import com.ptc.prolog.pub.RunTimeException;

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
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.folder.SubFolder;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartIDSeq;
import wt.part.WTPartMaster;
import wt.part.WTPartReferenceLink;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.applicationcontext.implementation.ServiceProperties;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.type.TypeDefinitionReference;
import wt.type.TypedUtility;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

@WebService(serviceName = "BmsCreateDeliverableDocLink")
public class BmsCreateDeliverableDocLink implements RemoteAccess {

	public static void main(String[] args) throws Exception {
		RemoteMethodServer rm = RemoteMethodServer.getDefault();
		rm.setUserName("wcadmin");
		rm.setPassword("wcadmin");
		rm.invoke("Test", BmsCreateDeliverableDocLink.class.getName(), null, null, null); // 远程调用
		
	}

	/**
	 * 远程调用测试方法
	 *
	 * @throws Exception
	 */
	public static void Test() throws Exception {
		String jsonstr = "{'parentPN':'PESV01-00061','docName':'文档名称','swFileName':'XXX.rar',"
				+ "'reportFileName':'XXXX.docx','feature':'XXXX','partinfo':'cls:SW03,Hardware_Version:硬件版本,Software_Version:软件版本'}";
		
		BmsCreateDeliverableDocLink bcd = new BmsCreateDeliverableDocLink();
		bcd.createPart(jsonstr, TypeName.CATLPart);
		//System.out.println("result= " + result);
	}

	@WebMethod(operationName = "createDeliverableDocLink")
	public String createDeliverableDocLink(String jsonstr) throws RunTimeException, IOException {
		Transaction trx = null;
		InputStream docin = null;
		InputStream rjbin = null;
		try {
			trx = new Transaction();
			trx.start();
			String PN = "";
			String docName = "";
			String docNumber = "";
			WTContainer wtContainer = null;
			String docFolderPath = "";
			String filePath = "softDocument";
			String SoftName = "";
			String SubCategory = "";
			String reportFileName = "";
			String swFileName = "";

			JSONObject json = new JSONObject(jsonstr);
			docName = json.getString("docName");
			PN = json.getString("parentPN");
			reportFileName = json.getString("reprotFileName");
			swFileName = json.getString("swFileName");
			// JSONArray docarray = json.getJSONArray("doclist");//文档
			// for (int i = 0; i < docarray.length(); i++) {
			// }
			if (PN.equals("")) {
				return "PN等于空";
			} else if (!PN.startsWith("P")) {
				return "PN等于【" + PN + "】必须是以【P】开头";
			} 
			
			WTPart part = //getLastestWTPartByNumber(PN);
			
			createPart(jsonstr, TypeName.CATLPart);
			if (part == null) {
				return "产品PN:【" + PN + "】在PLM系统中不存在，请重新填写";
			}
			if (WorkInProgressHelper.isCheckedOut(part)) {
				return "部件:【" + part.getNumber() + "】在plm中已经检出,请检入后再试";
			}

			QueryResult docs = WTPartHelper.service.getReferencesWTDocumentMasters(part);
			WTDocument document = null;
			int dces = 0;
			while (docs.hasMoreElements()) {
				Object object = (Object) docs.nextElement();
				if (object instanceof WTDocumentMaster) {
					WTDocumentMaster docMaster = (WTDocumentMaster) object;
					WTDocument doc = getLastestDocumentMaster(docMaster);
					String docType = TypedUtilityServiceHelper.service.getTypeIdentifier(doc).getTypename();
					if (docType.indexOf("com.CATLBattery.ProductSoftDoc") > 0) {
						document = doc;
						dces++;
					}
				}
			}
			if (dces > 1) {
				return "产品PN【" + part.getNumber() + "】关联多个软件文档请确认后重新提交";
			}
			docin = FTPUitl.getFileInputStream(filePath, reportFileName);
			rjbin = FTPUitl.getFileInputStream(filePath, swFileName);
			if (docin == null) {
				return "报告名称:【" + reportFileName + "】在ftp目录中不存在";
			} else if (rjbin == null) {
				return "软件包名称:【" + swFileName + "】在ftp目录中不存在";
			}
			if (document == null) {
				docFolderPath = "/" + part.getFolderPath().split("/")[1];
				docFolderPath = docFolderPath + "/" + part.getFolderPath().split("/")[2] + "/设计图档/产品类软件";

				// docNumber = "SW-P";
				/*docNumber = SubCategory.substring(SubCategory.indexOf("-") + 1, SubCategory.length());
				maxnumber = CatlDocNewNumber.queryMaxDocNumber(docNumber);
				String nextNumberString = String.valueOf(maxnumber);
				while (nextNumberString.length() < 8) {
					nextNumberString = "0" + nextNumberString;
				}*/
				docNumber = part.getNumber();
				wtContainer = part.getContainer();
				WTDocument doc = createDocument(docName, docNumber, wtContainer, docFolderPath, filePath, 
						docin, reportFileName, rjbin, swFileName);
				if (doc != null) {
					createReferenceLink(doc, part);
				} else {
					return "创建文档失败";
				}
			} else {
				String reult = updateDocument(docName,SoftName, document,
						docin, reportFileName, rjbin, swFileName);
				if (!reult.equals("success")) {
					return reult;
				}
			}
			trx.commit();
			trx = null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RunTimeException(e.getLocalizedMessage());
		} finally {
			if (trx != null) {
				trx.rollback();

			}
			if (docin != null) {
				docin.close();
			}
			if (rjbin != null) {
				rjbin.close();
			}
		}
		return "success";
	}

	
	
	/**
	 * 创建WTDocument类文档
	 * 
	 * @param map
	 * @throws WTException
	 * @throws PropertyVetoException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static WTDocument createDocument(String docName, String docNumber, WTContainer wtContainer, String docPath,
			String filePath, InputStream docin, String SwRepName, InputStream rjbin, String SwName)
					throws Exception {

		// 获取文档对象
		WTDocument wtDocument = WTDocument.newWTDocument(docNumber, docName, DocumentType.getDocumentTypeDefault());
		// 为文档设置容器
		wtDocument.setContainer(wtContainer);
		// 设置备注

		// 获取文件夹对象
		Folder folder = getFolder(docPath, wtContainer);
		FolderHelper.assignLocation(wtDocument, folder);
		TypeDefinitionReference tdr = TypedUtility.getTypeDefinitionReference(TypeName.softwareDoc);// 设置软类型
		wtDocument.setTypeDefinitionReference(tdr);
		wtDocument = (WTDocument) PersistenceHelper.manager.save(wtDocument);

		if (docin != null) {
			ContentHolder ch = (ContentHolder) wtDocument;
			ApplicationData theContent1 = ApplicationData.newApplicationData(ch);
			theContent1.setFileName(SwRepName);
			theContent1.setRole(ContentRoleType.PRIMARY);
			theContent1 = ContentServerHelper.service.updateContent(ch, theContent1, docin);
			wtDocument = (WTDocument) ContentServerHelper.service.updateHolderFormat(wtDocument);
		}
		if (rjbin != null) {
			ContentHolder ch = (ContentHolder) wtDocument;
			ApplicationData theContent1 = ApplicationData.newApplicationData(ch);
			theContent1.setFileName(SwName);
			theContent1.setRole(ContentRoleType.toContentRoleType("SOFTPACKAGE_ATTACHMENT"));
			theContent1 = ContentServerHelper.service.updateContent(ch, theContent1, rjbin);
			wtDocument = (WTDocument) ContentServerHelper.service.updateHolderFormat(wtDocument);
		}
		return wtDocument;
	}

	/**
	 * 更新指定文档
	 * 
	 * @param docNumber
	 *            文档编号
	 * @param docName
	 *            文档名称
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws RemoteException
	 */
	public static String updateDocument(String docName, String docSoftName,
			WTDocument doc, InputStream docin, String SwRepName,
			InputStream rjbin, String SwName) throws Exception {

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
		if (!state.equals("WRITING") && !state.equals("MODIFICATION")) {
			return "对象【" + doc.getDisplayIdentifier() + "】状态为【" + doc.getLifeCycleState().getDisplay(Locale.CHINA)
					+ "】不能修改。";
		}

		doc = (WTDocument) getWorkableByPersistable(doc);
		ContentHolder contentHolder = ContentHelper.service.getContents(doc);
		if (docin != null) {
			ContentItem contentitem = ContentHelper.getPrimary((FormatContentHolder) contentHolder);
			ContentServerHelper.service.deleteContent(contentHolder, contentitem);
			ApplicationData theContent1 = ApplicationData.newApplicationData(doc);
			theContent1.setFileName(SwRepName);
			theContent1.setRole(ContentRoleType.PRIMARY);
			theContent1 = ContentServerHelper.service.updateContent(doc, theContent1, docin);
			doc = (WTDocument) ContentServerHelper.service.updateHolderFormat(doc);
		}
		if (rjbin != null) {
			QueryResult qr = ContentHelper.service.getContentsByRole(contentHolder,
					ContentRoleType.toContentRoleType("SOFTPACKAGE_ATTACHMENT"));
			ApplicationData ap = null;
			while (qr.hasMoreElements()) {
				ap = (ApplicationData) qr.nextElement();
				ContentServerHelper.service.deleteContent(contentHolder, ap);
			}
			ap = ApplicationData.newApplicationData(doc);
			ap.setFileName(SwName);
			ap.setRole(ContentRoleType.toContentRoleType("SOFTPACKAGE_ATTACHMENT"));
			ap = ContentServerHelper.service.updateContent(doc, ap, rjbin);
			doc = (WTDocument) ContentServerHelper.service.updateHolderFormat(doc);
		}
		doc = (WTDocument) WorkInProgressHelper.service.checkin(doc, null);
		return "success";

	}

	/**
	 * 创建部件的参考方文档
	 * 
	 * @param document
	 * @param part
	 * @throws WTException
	 */
	public static void createReferenceLink(WTDocument document, WTPart part) throws WTException {
		WTDocumentMaster wtdocumentmaster = (WTDocumentMaster) document.getMaster();
		WTPartReferenceLink wtpartreferencelink = getPartReferenceLink(part, wtdocumentmaster);
		if (wtpartreferencelink == null) {
			WTPartReferenceLink wtpartreferencelink1 = WTPartReferenceLink.newWTPartReferenceLink(part,
					wtdocumentmaster);
			PersistenceServerHelper.manager.insert(wtpartreferencelink1);
			wtpartreferencelink1 = (WTPartReferenceLink) PersistenceHelper.manager.refresh(wtpartreferencelink1);
		}
	}

	public static WTPartReferenceLink getPartReferenceLink(WTPart wtpart, WTDocumentMaster wtdocumentmaster)
			throws WTException {
		QueryResult queryresult = PersistenceHelper.manager.find(wt.part.WTPartReferenceLink.class, wtpart,
				WTPartReferenceLink.REFERENCED_BY_ROLE, wtdocumentmaster);
		if (queryresult == null || queryresult.size() == 0)
			return null;
		else {
			WTPartReferenceLink wtpartreferencelink = (WTPartReferenceLink) queryresult.nextElement();
			return wtpartreferencelink;
		}
	}

	/**
	 * @param workable
	 * @return 获取工作副本对象
	 * @create ltang
	 * @modified: ☆xschen(2017年7月1日):
	 */
	public static Workable getWorkableByPersistable(Workable workable) {
		Workable wa = null;
		try {
			if (WorkInProgressHelper.isCheckedOut(workable)) {
				if (!WorkInProgressHelper.isWorkingCopy(workable)) {
					wa = WorkInProgressHelper.service.workingCopyOf(workable);
				}
			} else {
				wa = WorkInProgressHelper.service
						.checkout(workable, WorkInProgressHelper.service.getCheckoutFolder(), "").getWorkingCopy();
			}
		} catch (WTPropertyVetoException | WTException e) {
			e.printStackTrace();
		}
		return wa;
	}

	/**
	 * 得到文件夹结构,没有的则创建
	 * 
	 * @param folderPath
	 * @param wtcontainer
	 * @return
	 * @throws Exception
	 */
	public static Folder getFolder(String folderPath, WTContainer wtcontainer) throws Exception {
		if (folderPath == null || folderPath.equals("")) {
			return null;
		}
		Folder subfolder = null;
		String folderRef = "";

		if (!folderPath.startsWith("/")) {
			folderPath = "/" + folderPath;
		}

		if (!folderPath.equalsIgnoreCase("/Default") && !folderPath.startsWith("/Default")) {
			folderPath = "/Default" + folderPath;
		}

		String nextfolder[] = folderPath.split("/");
		// System.out.println("nextfolder[]==="+nextfolder.length);
		ArrayList list = new ArrayList();
		for (int p = 0; p < nextfolder.length; p++) {
			if (nextfolder[p] != null && !nextfolder[p].trim().equals("") && !nextfolder[p].trim().equals("Default")) {
				list.add(nextfolder[p]);
			}
		}
		// System.out.println("list==="+list);
		createMultiLevelDirectory(list, WTContainerRef.newWTContainerRef(wtcontainer));

		subfolder = FolderHelper.service.getFolder(folderPath, WTContainerRef.newWTContainerRef(wtcontainer));
		if (subfolder == null) {
			folderPath = "/Default";
			subfolder = FolderHelper.service.getFolder(folderPath, WTContainerRef.newWTContainerRef(wtcontainer));
		} else {
			ReferenceFactory rf = new ReferenceFactory();
			folderRef = rf.getReferenceString(ObjectReference
					.newObjectReference(((Persistable) subfolder).getPersistInfo().getObjectIdentifier()));
		}
		return subfolder;
	}

	/**
	 * create multi-level directory
	 * 
	 * @param list:
	 * @param wtContainerRef
	 * @return
	 */
	public static Folder createMultiLevelDirectory(List<String> list, WTContainerRef wtContainerRef) {
		Folder subFolder = null;
		String path = ((WTContainer) wtContainerRef.getObject()).getDefaultCabinet().getFolderPath();
		int size = list.size();
		for (int i = 0; i < size; i++) {
			Folder folder = null;
			try {
				folder = FolderHelper.service.getFolder(path, wtContainerRef);
				path = path + "/" + list.get(i);
				QueryResult result = FolderHelper.service.findSubFolders(folder);
				if (!checkFolderExits(result, list.get(i)))
					subFolder = FolderHelper.service.createSubFolder(path, wtContainerRef);
			} catch (WTException e) {
				e.printStackTrace();
			}
		}

		if (subFolder == null) {
			try {
				Folder folder = FolderHelper.service.getFolder(path, wtContainerRef);
				subFolder = (Folder) folder;
				// System.out.println(">>>>SubFolder'Name is:" +
				// subFolder.getName());
			} catch (WTException e) {
				e.printStackTrace();
			}
		}
		return subFolder;
	}

	private static boolean checkFolderExits(QueryResult result, String str) {
		if (result == null)
			return false;
		while (result.hasMoreElements()) {
			Object obj = result.nextElement();
			if (obj instanceof SubFolder) {
				SubFolder subFolder = (SubFolder) obj;
				if (subFolder.getName().equals(str))
					return true;
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * 查询最新的部件
	 * 
	 * @param numStr
	 * @return
	 */
	public static WTPart getLastestWTPartByNumber(String numStr) {
		try {
			QuerySpec queryspec = new QuerySpec(WTPart.class);

			queryspec.appendSearchCondition(
					new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL, numStr));
			QueryResult queryresult = PersistenceHelper.manager.find(queryspec);
			LatestConfigSpec cfg = new LatestConfigSpec();
			QueryResult qr = cfg.process(queryresult);
			if (qr.hasMoreElements()) {
				return (WTPart) qr.nextElement();
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 查询最新的Document
	 * 
	 * @param master
	 * @return
	 */
	public static WTDocument getLastestDocumentMaster(WTDocumentMaster master) {
		try {
			QueryResult queryresult = VersionControlHelper.service.allIterationsOf(master);
			LatestConfigSpec cfg = new LatestConfigSpec();
			QueryResult qr = cfg.process(queryresult);
			if (qr.hasMoreElements()) {
				return (WTDocument) qr.nextElement();
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 创建多个部件
	 * @return 
	 */
	public WTPart createPart(String jsonstr, String type) throws Exception {
		Transaction trx = null;
		List files = new ArrayList();
		WTPart part = null;
		try {
			trx = new Transaction();
			trx.start();
			JSONObject json = new JSONObject(jsonstr);
			String parentPN = json.getString("parentPN");
			List clfstr = new ArrayList();
			WTPart parentPart = CommonUtil.getLatestWTpartByNumber(parentPN);
			Folder folder = FolderHelper.getFolder(parentPart);
			System.out.println("folder\t" + folder.getName());

			Map<String,String> clsnamesource = PartLoadNameSourceUtil.getPartClsNameSource();
			
			
			String partinfo = json.getString("partinfo");

			String[] partvalue = partinfo.split(",");

			Map<String, String> partmap = new HashMap<>();

			for (int i = 0; i < partvalue.length; i++) {
				String str = partvalue[i];
				String[] namevalue = str.split(":");
				partmap.put(namevalue[0], namevalue[1]);
			}

			for(String key:partmap.keySet()){
				System.out.println(key);
			}
			String clf = partmap.get("cls");

			String name = "";
			String source = "";
			String unit = "";
			String oldnum = "";//json.getString("oldnum");
			String description = "";//json.getString("description");
			String uiforcecreate = "";//json.getString("forcecreate");
			String feature = json.getString("feature");
			String iscustomer = "否";
			String platform = "A";
			String openMould = "";
			
			String namesource = clsnamesource.get(clf);
			if(clsnamesource.containsKey(clf)){
				String sourcename = clsnamesource.get(clf);
				System.out.println("Source and name:\t"+sourcename);
				String[] nsarray = sourcename.split("qqqq;;;;");
				if(nsarray.length == 4){
					name = nsarray[0];
					source = nsarray[1].split(",")[0];
					unit = nsarray[2];
					openMould = nsarray[3];
					System.out.println("Source:\t"+unit);
				}
			}
			
			
			String number = PersistenceHelper.manager.getNextSequence(WTPartIDSeq.class);
			part = CommonUtil.createPart(number, name, type, source, unit, oldnum, uiforcecreate, description,
					folder.getIdentity(), parentPart.getContainer().toString());
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

			partmaster = (WTPartMaster) ibamaster.updateAttributeContainer(partmaster);
			ibafeature.updateIBAHolder(partmaster);
			SessionServerHelper.manager.setAccessEnforced(true);
			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			boolean flag = AccessControlHelper.manager.hasAccess(user, part, AccessPermission.CREATE);
			if (!flag) {
				throw new LineException("您没有创建部件权限");
			}
			SessionServerHelper.manager.setAccessEnforced(false);

			// 添加分类，分类属性 验证部件
			if (!StringUtils.isEmpty(clf)) {
				LWCStructEnumAttTemplate lwc = null;
				String lwcname = null;
				// 获取分类名称
				IBAUtility iba = new IBAUtility(part);

				lwcname = partmap.get("cls");
				iba.setIBAValue("cls", lwcname);

				lwc = NodeUtil.getClfNodeByName(lwcname);
				StringBuffer specification = new StringBuffer();

				for (String key : partmap.keySet()) {
					String ibaname = key;
					// String datatype=clfattrmap.getString("type");
					if (!ibaname.equals(ConstantLine.var_clf)) {// 不是分类
						String ibavalue = partmap.get(key);
						String ibadisplay = IBAUtility.getIBADisplayName(ibaname);
						if (specification.length() == 0) {
							specification.append(ibadisplay).append(":").append(ibavalue);
						} else {
							specification.append("_").append(ibadisplay).append(":").append(ibavalue);
						}
						if (!StringUtils.isEmpty(ibavalue)) {
							iba.setIBAValue(ibaname, ibavalue);
						}
					}
					// 修改为新编码
				}
				boolean forcecreate = false;
				if (uiforcecreate.equals("是")) {
					forcecreate = true;
				}
				if (!forcecreate) {
					if (clfstr.contains(clf)) {
						throw new LineException(
								"您创建的部件" + part.getName() + "物料规格与表格中其他部件物料规格重复，请选择规格重复时仍然创建为“是”后再试！\n");
					}
					Set parts = PartUtil.getLastedPartByStringIBAValue(part.getNumber(), "specification",
							specification.toString());
					if (parts.size() != 0) {
						Iterator ite = parts.iterator();
						WTPart exisPart = (WTPart) ite.next();
						throw new LineException("您创建的部件" + part.getName() + "物料规格与：\n" + exisPart.getNumber()
								+ "\n物料规格重复，请选择规格重复时仍然创建为“是”后再试！\n");
					}

				}
				clfstr.add(clf);

				if (specification.length() > 0) {
					iba.setIBAValue("specification", specification.toString());
				}
				iba.updateAttributeContainer(part);
				iba.updateIBAHolder(part);

				CreateCatlPartProcessor.updateSource(part);
				CreateCatlPartProcessor.renamePart(part);

				String newNum = CatlPartNewNumber.createPartNewnumber(lwc, part.getContainerName());
				WTPartMaster partMaster = (WTPartMaster) part.getMaster();
				try {
					WTPartHelper.service.changeWTPartMasterIdentity(partMaster, part.getName(), newNum,
							part.getOrganization());
				} catch (WTPropertyVetoException e) {
					e.printStackTrace();
				}

			} else {
				throw new LineException("获取不到分类");
			}

			trx.commit();
		} catch (Exception e) {
			trx.rollback();
			throw e;
		}
		return part;
	}
	
	
	/**
	 * 创建多个部件
	 * @return 
	 */
	public WTPart updatePart(String jsonstr, String type) throws Exception {
		Transaction trx = null;
		//List files = new ArrayList();
		WTPart part = null;
		try {
			trx = new Transaction();
			trx.start();
			JSONObject json = new JSONObject(jsonstr);
			String swPN = json.getString("swPN");
			//List clfstr = new ArrayList();
			part = CommonUtil.getLatestWTpartByNumber(swPN);
			part = (WTPart) CommonUtil.checkoutObject(part);
			//Folder folder = FolderHelper.getFolder(swPart);
			//System.out.println("folder\t" + folder.getName());
			part = (WTPart) CommonUtil.checkinObject(part, "软件PN更新");

			Map<String,String> clsnamesource = PartLoadNameSourceUtil.getPartClsNameSource();
			
			
			String partinfo = json.getString("partinfo");

			String[] partvalue = partinfo.split(",");

			Map<String, String> partmap = new HashMap<>();

			for (int i = 0; i < partvalue.length; i++) {
				String str = partvalue[i];
				String[] namevalue = str.split(":");
				partmap.put(namevalue[0], namevalue[1]);
			}

			for(String key:partmap.keySet()){
				System.out.println(key);
			}
			String clf = partmap.get("cls");

			String name = "";
			String source = "";
			String unit = "";
			String oldnum = "";//json.getString("oldnum");
			String description = "";//json.getString("description");
			String uiforcecreate = "";//json.getString("forcecreate");
			String feature = json.getString("feature");
			String iscustomer = "否";
			String platform = "A";
			String openMould = "";
			
			String namesource = clsnamesource.get(clf);
			if(clsnamesource.containsKey(clf)){
				String sourcename = clsnamesource.get(clf);
				System.out.println("Source and name:\t"+sourcename);
				String[] nsarray = sourcename.split("qqqq;;;;");
				if(nsarray.length == 4){
					name = nsarray[0];
					source = nsarray[1].split(",")[0];
					unit = nsarray[2];
					openMould = nsarray[3];
					System.out.println("Source:\t"+unit);
				}
			}
			
			
			//String number = PersistenceHelper.manager.getNextSequence(WTPartIDSeq.class);
			//part = CommonUtil.createPart(number, name, type, source, unit, oldnum, uiforcecreate, description,
			//		folder.getIdentity(), swPart.getContainer().toString());
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

			partmaster = (WTPartMaster) ibamaster.updateAttributeContainer(partmaster);
			ibafeature.updateIBAHolder(partmaster);
			SessionServerHelper.manager.setAccessEnforced(true);
			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			boolean flag = AccessControlHelper.manager.hasAccess(user, part, AccessPermission.MODIFY);
			if (!flag) {
				throw new LineException("您没有修改部件权限");
			}
			SessionServerHelper.manager.setAccessEnforced(false);

			// 添加分类，分类属性 验证部件
			if (!StringUtils.isEmpty(clf)) {
				LWCStructEnumAttTemplate lwc = null;
				String lwcname = null;
				// 获取分类名称
				IBAUtility iba = new IBAUtility(part);

				lwcname = partmap.get("cls");
				iba.setIBAValue("cls", lwcname);

				lwc = NodeUtil.getClfNodeByName(lwcname);
				StringBuffer specification = new StringBuffer();

				for (String key : partmap.keySet()) {
					String ibaname = key;
					// String datatype=clfattrmap.getString("type");
					if (!ibaname.equals(ConstantLine.var_clf)) {// 不是分类
						String ibavalue = partmap.get(key);
						String ibadisplay = IBAUtility.getIBADisplayName(ibaname);
						if (specification.length() == 0) {
							specification.append(ibadisplay).append(":").append(ibavalue);
						} else {
							specification.append("_").append(ibadisplay).append(":").append(ibavalue);
						}
						if (!StringUtils.isEmpty(ibavalue)) {
							iba.setIBAValue(ibaname, ibavalue);
						}
					}
					// 修改为新编码
				}
				boolean forcecreate = false;
				if (uiforcecreate.equals("是")) {
					forcecreate = true;
				}


				if (specification.length() > 0) {
					iba.setIBAValue("specification", specification.toString());
				}
				iba.updateAttributeContainer(part);
				iba.updateIBAHolder(part);

				CreateCatlPartProcessor.updateSource(part);
				CreateCatlPartProcessor.renamePart(part);

				String newNum = CatlPartNewNumber.createPartNewnumber(lwc, part.getContainerName());
				WTPartMaster partMaster = (WTPartMaster) part.getMaster();
				try {
					WTPartHelper.service.changeWTPartMasterIdentity(partMaster, part.getName(), newNum,
							part.getOrganization());
				} catch (WTPropertyVetoException e) {
					e.printStackTrace();
				}

			} else {
				throw new LineException("获取不到分类");
			}

			trx.commit();
		} catch (Exception e) {
			trx.rollback();
			throw e;
		}
		return part;
	}
}
