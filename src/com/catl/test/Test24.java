package com.catl.test;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONException;
import org.json.JSONObject;

import com.catl.bom.workflow.BomWfUtil;
import com.catl.bom.workflow.ReleasedDateUtil;
import com.catl.cadence.conf.InitSystemConfigContant;
import com.catl.cadence.util.NodeUtil;
import com.catl.change.ChangeUtil;
import com.catl.change.DataUtility.CatlPropertyHelper;
import com.catl.common.constant.PartState;
import com.catl.common.constant.TypeName;
import com.catl.common.util.ClassificationUtil;
import com.catl.common.util.GenericUtil;
import com.catl.common.util.IBAUtil;
import com.catl.common.util.PartUtil;
import com.catl.common.util.WCLocationConstants;
import com.catl.doc.maturityUpReport.MaturityUpReportHelper;
import com.catl.doc.soft.util.CatlSoftDocUtil;
import com.catl.doc.soft.util.LoadSoftDocDataUtil;
import com.catl.ecad.bean.CadenceAttributeBean;
import com.catl.ecad.dbs.CadenceXmlObjectUtil;
import com.catl.ecad.utils.AccessControlUtil;
import com.catl.ecad.utils.BOMUtil;
import com.catl.ecad.utils.CommonUtil;
import com.catl.ecad.utils.ECADConst;
import com.catl.ecad.utils.ECADPartUtils;
import com.catl.ecad.utils.ECADutil;
import com.catl.ecad.utils.EPMUtil;
import com.catl.ecad.utils.FTPUitl;
import com.catl.ecad.utils.IBAUtility;
import com.catl.ecad.utils.MailUtil;
import com.catl.ecad.utils.WorkflowHelper;
import com.catl.integration.webservice.bms.BmsSoftwareCreateAndUpdate;
import com.catl.line.constant.ConstantLine;
import com.catl.line.exception.LineException;
import com.catl.line.util.WCUtil;
import com.catl.line.util.WTDocumentUtil;
import com.catl.loadData.util.ExcelReader;
import com.catl.part.PartConstant;
import com.catl.promotion.bean.DesignDisabledXmlObjectBean;
import com.catl.promotion.dbs.DesignDisabledXmlObjectUtil;
import com.catl.promotion.util.PromotionUtil;
import com.ptc.core.lwc.server.LWCNormalizedObject;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.common.TypeInstanceIdentifier;
import com.ptc.core.meta.common.UpdateOperationIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.netmarkets.org.NmOrganizationHelper;
import com.ptc.prolog.pub.RunTimeException;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;
import com.ptc.windchill.enterprise.team.server.TeamCCHelper;
import com.ptc.windchill.enterprise.wvs.repsAndMarkups.builders.RepsAndMarkupsBuilder;
import com.ptc.windchill.enterprise.wvs.repsAndMarkups.commands.RepsAndMarkupsCommands;
import com.ptc.windchill.enterprise.wvs.repsAndMarkups.dataUtilities.RepsAndMarkupsDateDataUtility;
import com.ptc.windchill.enterprise.wvs.repsAndMarkups.utils.RepsAndMarkupsHelper;
import com.ptc.wvs.server.loader.EDRContentHelper;
import com.ptc.wvs.server.loader.EDRHelper;
import com.ptc.wvs.server.ui.RepHelper;
import com.ptc.xworks.util.XWorksHelper;
import com.ptc.xworks.xmlobject.XmlObject;
import com.ptc.xworks.xmlobject.XmlObjectIdentifier;
import com.ptc.xworks.xmlobject.store.XmlObjectStoreException;
import com.ptc.xworks.xmlobject.store.XmlObjectStoreManager;
import com.ptc.xworks.xmlobject.store.StoreOptions.LoadOption;

import wt.access.AccessControlHelper;
import wt.access.AccessControlServerHelper;
import wt.access.AccessControlled;
import wt.access.AccessPermission;
import wt.access.AccessPermissionSet;
import wt.access.AdHocAccessKey;
import wt.access.AdHocControlled;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.container.batch.BatchContainer;
import wt.container.batch.BatchContainerFactory;
import wt.container.batch.TransactionContainer;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.content.FormatContentHolder;
import wt.content.HolderToContent;
import wt.csm.navigation.ClassificationNode;
import wt.doc.DocumentMaster;
import wt.doc.WTDocument;
import wt.doc.WTDocumentIDSeq;
import wt.epm.EPMApplicationType;
import wt.epm.EPMAuthoringAppType;
import wt.epm.EPMContextHelper;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.EPMDocumentType;
import wt.epm.build.EPMBuildHistory;
import wt.epm.build.EPMBuildRule;
import wt.epm.build.EPMBuildRuleSequence;
import wt.epm.modelitems.ModelItem;
import wt.epm.modelitems.ModelItemContainedIn;
import wt.epm.modelitems.ModelItemMaster;
import wt.epm.modelitems.ModelItemSubType;
import wt.epm.modelitems.ModelItemType;
import wt.epm.structure.EPMReferenceLink;
import wt.epm.structure.EPMReferenceType;
import wt.epm.structure.EPMStructureHelper;
import wt.fc.ObjectReference;
import wt.fc.PersistInfo;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.PersistentReference;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTCollection;
import wt.fc.collections.WTHashSet;
import wt.fc.collections.WTList;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.httpgw.URLFactory;
import wt.inf.container.OrgContainer;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.inf.library.WTLibrary;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.LifeCycleTemplateReference;
import wt.lifecycle.State;
import wt.maturity.MaturityBaseline;
import wt.maturity.MaturityException;
import wt.maturity.MaturityHelper;
import wt.maturity.PromotionNotice;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.DirectoryContextProvider;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTOrganization;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.ownership.Ownership;
import wt.ownership.OwnershipHelper;
import wt.part.QuantityUnit;
import wt.part.Source;
import wt.part.WTPart;
import wt.part.WTPartConfigSpec;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartReferenceLink;
import wt.part.WTPartStandardConfigSpec;
import wt.part.WTPartUsageLink;
import wt.pdmlink.PDMLinkProduct;
import wt.pds.StatementSpec;
import wt.pom.Transaction;
import wt.project.Role;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.representation.Representation;
import wt.representation.RepresentationHelper;
import wt.representation.RepresentationType;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.team.RoleHolder2;
import wt.team.Team;
import wt.team.TeamHelper;
import wt.team.TeamManaged;
import wt.team.TeamReference;
import wt.team.TeamTemplate;
import wt.team.TeamTemplateReference;
import wt.team.WTRoleHolder2;
import wt.type.TypeDefinitionReference;
import wt.type.TypedUtility;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTInvalidParameterException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;
import wt.vc.Iterated;
import wt.vc.IterationInfo;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;
import wt.vc.baseline.BaselineHelper;
import wt.vc.config.ConfigHelper;
import wt.vc.config.LatestConfigSpec;
import wt.vc.struct.StructHelper;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;
import wt.vc.views.ViewReference;
import wt.vc.wip.WorkInProgressHelper;
import wt.viewmarkup.DerivedImage;
import wt.workflow.definer.WfDefinerHelper;
import wt.workflow.definer.WfProcessDefinition;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfContainer;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WorkItem;

public class Test24 implements RemoteAccess {

	public final static String DesignDisabledXmlObjectBean = "DesignDisabledXmlObjectBean";

	public static void main(String[] args) throws RemoteException, InvocationTargetException, WTException {

		test();

	}

	public static void test() throws RemoteException, InvocationTargetException, WTException {
		// WTObject obj = (WorkItem) new
		// ReferenceFactory().getReference("OR:wt.change2.WTChangeOrder2:177069863").getObject();
		RemoteMethodServer rm = RemoteMethodServer.getDefault();
		// rm.setUserName("60011102");
		// rm.setPassword("Su20100209..");
		rm.setUserName("wcadmin");
		rm.setPassword("wcadmin");
		SessionHelper.manager.setAuthenticatedPrincipal("wcadmin");

		rm.invoke("testReleasePart", Test24.class.getName(), null, null, null);
	}

	public static void testReleasePart() throws Exception {
		// WTPart part = CommonUtil.getPartByNumber("CM-00000001");
		/*
		 * List list = getLatestPartOfAllVersion(part);
		 * System.out.println(list.size()); for (int i = 0; i < list.size();
		 * i++) { WTPart part1= (WTPart) list.get(i);
		 * System.out.println(part1.getNumber()+"\t"+VersionControlHelper.
		 * getIterationDisplayIdentifier(part1));
		 * 
		 * }
		 */
		/*
		 * WTPart prePart = (WTPart) CommonUtil.getPreVersionObject(part);
		 * System.out.println(VersionControlHelper.getIterationDisplayIdentifier
		 * (prePart));
		 * if(!WorkInProgressHelper.isWorkingCopy(prePart)&WorkInProgressHelper.
		 * isWorkingCopy(part)){ System.out.println("is work copy"); prePart =
		 * (WTPart) CommonUtil.getPreVersionObject(prePart);
		 * System.out.println(VersionControlHelper.getIterationDisplayIdentifier
		 * (prePart)); } WTChangeActivity2 dca =
		 * ChangeUtil.getEcaWithPersiser(prePart); if(dca != null){ QueryResult
		 * qc = ChangeHelper2.service.getChangeOrder(dca);
		 * while(qc.hasMoreElements()){ WTChangeOrder2 eco = (WTChangeOrder2)
		 * qc.nextElement();
		 * System.out.println(eco.getNumber()+"\n"+eco.getName()); } }
		 */
		// part = (WTPart) CommonUtil.checkoutObject(part);
		// BOMUtil.removeChildren(part);
		// CommonUtil.checkinObject(part, "Update Bom");
		/*
		 * List<CadenceAttributeBean> list = CadenceXmlObjectUtil
		 * .getCadenceAttributeBeanUtil((WTObject) part);
		 * System.out.println(list.size()); for(int i= 0; i<list.size();i++) {
		 * Map<String,String> colAndValue = new HashMap(); CadenceAttributeBean
		 * cadbean = list.get(i); System.out.println(cadbean.getIdentifier());
		 * colAndValue = cadbean.getNameAndValue(); Set<String> keys =
		 * colAndValue.keySet(); for(String key:keys){
		 * System.out.println(key+"\t000000000000000000\t"+colAndValue.get(key))
		 * ; } }
		 */
		/*
		 * EPMDocument epm =
		 * CommonUtil.getEPMDocumentByNumber("CK-00000010.PRT"); Folder folder =
		 * FolderHelper.getFolder(epm); String folderpath =
		 * folder.getFolderPath(); System.out.println(folderpath); String[] ss =
		 * folderpath.split("/"); String path = ""; if(ss.length>2){ for (int i
		 * = 1; i < 3; i++) { path =path+"/"+ss[i]; } path = path+"/"+"零部件";
		 * System.out.println(path); }else{ throw new
		 * WTException("图档"+epm.getNumber()+"所存放的文件夹路径不正确！"); }
		 */
		// WTPart part = createPart("CM-00000012", "teST",
		// "com.CATLBattery.CATLPart", "buy", "ea", "/Default/01Version
		// 1.0/零部件", "CM", "BMW");
		/*
		 * WTDocument doc = CommonUtil.getWTDocumentByNumber("500101-00005");
		 * WTList list = new WTArrayList(); list.add(doc); WTContainerRef
		 * containerRef = doc.getContainerReference();
		 * LifeCycleTemplateReference lctref = null; lctref =
		 * doc.getLifeCycleTemplate(); LifeCycleHelper.service.reassign(list,
		 * lctref, containerRef, true, "重新分配生命周期");
		 */

		// EPMDocument epm =
		// CommonUtil.getEPMDocumentByNumber("601012-00001.ASM");
		// ReleasedDateUtil.setReleasedDate(epm,"22");
		/*
		 * String softtype =
		 * "wt.change2.WTChangeOrder2|com.CATLBattery.CATLDChangeNotice"; String
		 * attrname = "changeFrom"; Set set =
		 * GenericUtil.getDiscreteSetVaules(softtype, attrname); for (Iterator
		 * iterator = set.iterator(); iterator.hasNext();) { Object object =
		 * (Object) iterator.next(); System.out.println(object); }
		 */

		/*
		 * EPMDocument epm =
		 * CommonUtil.getEPMDocumentByNumber("A3_GD&T.CATDRAWING"); IBAUtility
		 * iba = new IBAUtility(epm); iba.setIBAValue("PTC_WM_DESIGN", "张三");
		 * iba.setIBAValue("PTC_WM_DESIGN_DATE", "2017.12.26");
		 * iba.setIBAValue("PTC_WM_CHECK", "李四");
		 * iba.setIBAValue("PTC_WM_CHECK_DATE", "2017.12.26"); epm =
		 * (EPMDocument) iba.updateAttributeContainer(epm);
		 * iba.updateIBAHolder(epm);
		 */

		//WTDocument doc = CommonUtil.getLatestWTDocByNumber("MS-00000001");
		// getRefedPartByDoc(doc);
		// updatePrimary(doc, "E:\\1111111.txt");
		//EPMDocument epm = CommonUtil.getEPMDocumentByNumber("500101-00011.CATDRAWING");
		//updateAttachment(epm, "E:\\500101-00005.CATDrawing");

		/*
		 * WTPart part = CommonUtil.getLatestWTpartByNumber("CK-00000002");
		 * List<WTPart> parts = new ArrayList<>(); parts.add(part);
		 * System.out.println(checkChild(parts,parts,"C"));
		 * 
		 * Map<String,List<String>> map =getConfigMap(); Set<String> keys
		 * =map.keySet(); for(String key:keys){ StringBuilder sb = new
		 * StringBuilder(); List<String> list = map.get(key);
		 * sb.append(key).append("\t"); for (int i = 0; i < list.size(); i++) {
		 * String value = list.get(i); sb.append(i).append(". "
		 * ).append(value).append("\t"); } System.out.println(sb.toString()); }
		 */
		
		//WTDocument doc = CommonUtil.getLatestWTDocByNumber("SW-P-00000009");
		//CatlSoftDocUtil.createDocByDoc(doc);
	/*	String filePath = "E:\\datas1.xlsx";
		String swpath = "E:";
		XSSFWorkbook workbook =  LoadSoftDocDataUtil.readLineOffSoftDocData(filePath,swpath);
		FileOutputStream os = new FileOutputStream("E:\\result.xlsx");
		workbook.write(os);
		os.close();
		os.flush();*/
		/*String s = "{'swPN':'SW0300-00011','docName':'文档名称','swFileName':'xxx.zip','reportFileName':'xxx.docx','feature':'XXXX','partinfo':'cls:SW03,Hardware_Version:硬件版本22,Software_Version:软件版本22'}";
		BmsSoftwareCreateAndUpdate service = new BmsSoftwareCreateAndUpdate();
		System.out.println(s);
	    service.updateSoftware(s);*/
		//NmOrganizationHelper.service.
		/*WTPart part = CommonUtil.getLatestWTpartByNumber("800520-00002");
		List<WTPart> childParts = new ArrayList<>();
		getAllPart(part,childParts);
		
		System.out.println(childParts.size());
		for(int i = 0; i<childParts.size(); i++){
			WTPart tmp = childParts.get(i);
			System.out.println(tmp.getNumber());
		}
		
		 WfProcess obj =  (WfProcess) new ReferenceFactory().getReference("OR:wt.workflow.engine.WfProcess:816536").getObject();
		 Team team = (Team) obj.getTeamId().getObject();
		 Vector vector = TeamHelper.service.getMembers(team);
		 System.out.println(vector.size());
		 for(Object tmp:vector){
			 WTPrincipalReference ref = (WTPrincipalReference) tmp;
			 System.out.println(ref.getFullName());
		 }*/
		 URLFactory urlfactory = new URLFactory();
         String baseURL = urlfactory.getBaseURL().toExternalForm();
         System.out.println("==================================");
         System.out.println(baseURL);
		 ReferenceFactory factory =new ReferenceFactory();
		 
		 WTOrganization org = WTContainerHelper.service.getExchangeContainer().getOrganization();
		 OrgContainer orgContainer = WTContainerHelper.service.getOrgContainer(org);
		 WTContainerRef orgRef = (WTContainerRef) factory.getReference(orgContainer);
		 HashMap arg5 = OrganizationServicesHelper.manager.getRolePrincipalsForContext(orgRef, SessionHelper.getLocale(), false);
		 Set<String> keys = arg5.keySet();
		 for(String key:keys){
			 System.out.println(key+"\t"+arg5.get(key));
			 WTPrincipalReference arg11 = WTPrincipalReference.newWTPrincipalReference(OrganizationServicesHelper.manager
						.getRolePrincipal((String) arg5.get(key), orgRef, true, SessionHelper.getLocale()));
			 System.out.println(arg11);
		 }
		 
	}
	
	public static void getAllPart(WTPart part, List<WTPart> childParts) throws WTException{
		QueryResult qr = WTPartHelper.service.getUsesWTPartMasters(part);
		while (qr.hasMoreElements()) {
			WTPartUsageLink link = (WTPartUsageLink) qr.nextElement();
			WTPartMaster masterChild = link.getUses();
			WTPart childpart = (WTPart) PromotionUtil.getLatestVersionByMaster(masterChild);
			Set<WTPart> sparts = com.catl.common.util.BOMUtil.getSubstitutes(link);
			for(WTPart tmp:sparts){
				getAllPart(tmp,childParts);	
				childParts.add(tmp);
			}	
			
			getAllPart(childpart,childParts);	
			childParts.add(childpart);
		}
		System.out.println(part.getNumber());
	}

	 protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication("wcadmin", "wcadmin".toCharArray());
    }
	
	/**
	 * 文档，添加附件
	 * 
	 * @param doc
	 * @param filePath
	 * @throws WTException
	 * @throws FileNotFoundException
	 * @throws PropertyVetoException
	 * @throws IOException
	 * @modified: ☆joy_gb(2016年4月28日 上午1:59:04): <br>
	 */
	public static void updateAttachment(EPMDocument doc, String filePath)
			throws WTException, FileNotFoundException, PropertyVetoException, IOException {
		Transaction trx = new Transaction();
		try {
			trx.start();
			ContentHolder contentHolder = ContentHelper.service.getContents(doc);
			QueryResult contentitems = ContentHelper.service.getContentsByRole(contentHolder, ContentRoleType.PRIMARY);
			ApplicationData appData = null;
			while (contentitems.hasMoreElements()) {
				ContentItem contentitem = (ContentItem) contentitems.nextElement();
				if (contentitem instanceof ApplicationData) {
					appData = (ApplicationData) contentitem;
				}
			}
			ContentHolder ch = (ContentHolder) doc;
			// ApplicationData ap = ApplicationData.newApplicationData(ch);
			// ap.setRole(ContentRoleType.PRIMARY);
			if (appData != null) {
				appData = ContentServerHelper.service.updatePrimary((FormatContentHolder) ch, appData,
						new FileInputStream(filePath));
				ContentServerHelper.service.updateHolderFormat(doc);
			}

			trx.commit();
			trx = null;
		} catch (Exception u) {
			throw new WTException(u.getMessage());
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}
	}

	public static void updatePrimary(WTDocument aDocument, String path) throws WTException {
		Transaction trx = new Transaction();
		File fic = new File(path);

		try {
			trx.start();

			ContentHolder holderDocument = ContentHelper.service.getContents(aDocument);
			ContentItem primaryContent = ContentHelper.getPrimary((FormatContentHolder) holderDocument);
			ApplicationData newAppData = ApplicationData.newApplicationData((ContentHolder) aDocument);
			newAppData.setFileName(fic.getName());

			// Cleanup
			TransactionContainer transactionContainer = BatchContainerFactory.instantiateTransactionContainer();
			BatchContainer contentBatchContainer = BatchContainerFactory
					.instantiateGeneralBatchContainer(transactionContainer, "contents");
			transactionContainer.clearAll();

			if (PersistenceHelper.isPersistent(aDocument)) {
				aDocument = (WTDocument) ContentHelper.service.getContents(aDocument);

				java.util.Vector vector = ContentHelper.getContentList(aDocument);

				if (vector != null) {
					contentBatchContainer.populate(vector.elements());
				}

				Enumeration enumContents = vector.elements();

				while (enumContents.hasMoreElements())
					contentBatchContainer.remove(enumContents.nextElement());
			}

			aDocument = (WTDocument) ContentHelper.service.contentUpdateUpload(aDocument, transactionContainer);

			ContentServerHelper.service.updatePrimary((FormatContentHolder) aDocument, newAppData,
					new FileInputStream(fic));
			trx.commit();
			trx = null;
		} catch (Exception u) {
			throw new WTException(u.getMessage());
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}
	}

	public static String checkChild(List<WTPart> parent, List<WTPart> sourceList, String targetPhase)
			throws WTException {
		Map<Object, List> allChildren = BomWfUtil.getChildrenParts(parent);
		StringBuilder sb = new StringBuilder();
		Map<String, Integer> map = new HashMap<>();
		map.put("需求分析", 1);
		map.put("A", 1);
		map.put("B", 2);
		map.put("C", 3);
		map.put("验证", 4);
		map.put("发布", 5);
		for (Iterator i = allChildren.keySet().iterator(); i.hasNext();) {

			WTPart parentPart = (WTPart) i.next();
			String parentNum = parentPart.getNumber();
			// IBAUtility iba = new IBAUtility(parentPart);
			// String parentpp = iba.getIBAValue("ProductPhase");
			// if(parentNum.startsWith("77")||parentNum.startsWith("P")||parentNum.startsWith("99")){
			List childParts = allChildren.get(parentPart);
			for (int index = 0; index < childParts.size(); index++) {
				Object o = childParts.get(index);
				if (o instanceof WTPart) {
					WTPart childPart = (WTPart) o;
					String childNum = childPart.getNumber();
					// if(parentNum.startsWith("77")||parentNum.startsWith("P")||parentNum.startsWith("83")){
					IBAUtility childiba = new IBAUtility(childPart);
					String childpp = childiba.getIBAValue("ProductPhase");
					if (StringUtils.isBlank(childpp)) {
						childpp = "需求分析";
					}
					if (map.get(targetPhase) >= map.get(childpp) && !sourceList.contains(childPart)) {
						sb.append(parentNum).append("的子件").append(childNum).append("产品阶段还是").append(childpp)
								.append("\n");
					}

					// }// end if child is WTPart

				} // end if child is WTPart

			} // end for child loop
			sb.append(checkChild(childParts, sourceList, targetPhase));
			// } // end if parent Number is Satisfied

		} // end for parent part loop
		return sb.toString();
	}

	public static Map<String, List<String>> getConfigMap() {
		String filePathName = WCLocationConstants.WT_CODEBASE + File.separator + "config" + File.separator + "custom"
				+ File.separator + "config" + File.separator + "PartPhaseConfig.xlsx";
		Map<String, List<String>> configs = new HashMap<>();
		List<String> hasChild = new ArrayList<>();
		List<String> hasAttr = new ArrayList<>();
		List<String> phase = new ArrayList<>();
		List<String> line = new ArrayList<>();
		List<String> docClsPhaseMap = new ArrayList<>();
		List<String> docClsLineMap = new ArrayList<>();

		ExcelReader er = new ExcelReader(new File(filePathName));
		er.setSheetNum(0);
		try {
			er.open();
			int count = er.getRowCount();
			String[] cells = null;

			for (int i = 1; i <= count; i++) {
				cells = er.readExcelLine(i);
				if (cells == null || (StringUtils.isBlank(cells[0]) && StringUtils.isBlank(cells[1])
						&& StringUtils.isBlank(cells[2]) && StringUtils.isBlank(cells[3])
						&& StringUtils.isBlank(cells[4]) && StringUtils.isBlank(cells[5]))) {
					break;
				}
				if (!StringUtils.isBlank(cells[0])) {
					hasChild.add(cells[0]);
				}

				if (!StringUtils.isBlank(cells[1])) {
					hasAttr.add(cells[1]);
				}

				if (!StringUtils.isBlank(cells[2])) {
					phase.add(cells[2]);
				}

				if (!StringUtils.isBlank(cells[3])) {
					line.add(cells[3]);
				}

				if (!StringUtils.isBlank(cells[4])) {
					docClsPhaseMap.add(cells[4]);
				}

				if (!StringUtils.isBlank(cells[5])) {
					docClsLineMap.add(cells[5]);
				}

			}

			configs.put("hasChild", hasChild);
			configs.put("hasAttr", hasAttr);
			configs.put("phase", phase);
			configs.put("line", line);
			configs.put("docClsPhaseMap", docClsPhaseMap);
			configs.put("docClsLineMap", docClsLineMap);

			System.out.println("====RowCount:" + count);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return configs;
	}

	public static List<WTPart> getRefedPartByDoc(WTDocument doc) throws WTException {
		WTArrayList parts = new WTArrayList();
		parts = PartDocServiceCommand.getAssociatedRefParts(doc);
		for (int i = 0; i < parts.size(); i++) {
			ObjectReference or = (ObjectReference) parts.get(i);
			WTPart part = (WTPart) or.getObject();
			System.out.println(part.getNumber());
		}
		return parts;
	}
	
	 /**
     * 获取EPM文档的所有子模型
     * @param epmdoc
     * @return
     * @throws WTException
     */
    public static Set<EPMDocument> getAllChildrenByEPM(EPMDocument epmdoc) throws WTException{
    	boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try{
	    	Set<EPMDocument> set = new HashSet<EPMDocument>();
	    	QueryResult qr = EPMStructureHelper.service.navigateUsesToIteration(epmdoc, null, true, new LatestConfigSpec());
	    	while(qr.hasMoreElements()){
				EPMDocument epmUse = (EPMDocument)qr.nextElement();
				set.add(epmUse);
				set.addAll(getAllChildrenByEPM(epmUse));
	    	}
	    	return set;
	    }finally {
            SessionServerHelper.manager.setAccessEnforced(flag);
        }

    }

	public static List<WTPart> getLatestPartOfAllVersion(WTPart part) throws Exception {
		List<WTPart> list = new ArrayList<>();
		int i = 0;
		if (part != null) {
			WTPart releasedPart = PartUtil.getLatestReleasedPart(part.getMaster());
			System.out.println("part..........." + "\t" + VersionControlHelper.getIterationDisplayIdentifier(part));
			if (releasedPart != null) {
				list.add(releasedPart);
				WTPart preVersion = (WTPart) getPreVersionObject(releasedPart);
				System.out.println(
						"released.........." + "\t" + VersionControlHelper.getIterationDisplayIdentifier(releasedPart));
				while (preVersion != null) {
					i++;
					if (i == 10) {
						break;
					}
					System.out.println(preVersion + "\t" + i + "\t"
							+ VersionControlHelper.getIterationDisplayIdentifier(preVersion));
					if (PartUtil.isReleased(preVersion)) {
						list.add(preVersion);
					}
					preVersion = (WTPart) getPreVersionObject(preVersion);
				}
			}
		}

		return list;
	}

	public static WTObject getPreVersionObject(WTObject obj) throws Exception {
		WTObject prevVersionObj = null;
		String Version = VersionControlHelper.getVersionIdentifier((Versioned) obj).getValue();// 得到大版�??
		System.out.println("当前大版本:\t" + Version);
		boolean notGet = true;
		QueryResult allIterations = VersionControlHelper.service.allVersionsFrom((Versioned) obj);
		if (allIterations != null) {
			while (allIterations.hasMoreElements() && notGet) {
				prevVersionObj = (WTObject) allIterations.nextElement();
				boolean issame = VersionControlHelper.inSameBranch((Iterated) prevVersionObj, (Iterated) obj);
				String theVersion = VersionControlHelper.getVersionIdentifier((Versioned) prevVersionObj).getValue();
				if (issame) {
					prevVersionObj = null;
					continue;
				} else {
					notGet = false;
				}
			}
		}
		// System.out.println("前一个版本："+VersionControlHelper.getIterationDisplayIdentifier((Versioned)
		// prevVersionObj));
		return prevVersionObj;
	}

	public static void testgroup()
			throws WTException, WTPropertyVetoException, FileNotFoundException, IOException, JSONException {
		WTPart part = CommonUtil.getLatestWTpartByNumber("850000-00027");
		List<XmlObject> xmlObjs = getXmlObjectUtil(part);
		System.out.println("============xmlObjs\t" + xmlObjs.size());
		if (xmlObjs.size() > 0) {
			String reason = ((DesignDisabledXmlObjectBean) xmlObjs.get(0)).getreason();
			System.out.println("===========Reasoin:\t" + reason);
			String state = CatlPropertyHelper.getDisableReasonERPMapPropertyValue(reason);
			System.out.println("===========State\t" + state);
		}
	}

	public static List<XmlObject> getXmlObjectUtil(WTObject pbo) throws WTException {
		try {
			XmlObjectStoreManager storeManager = XWorksHelper.getXmlObjectStoreManager();
			XmlObjectIdentifier ownerOid = XmlObjectIdentifier.newXmlObjectIdentifier(pbo);
			List<XmlObject> xmlObjs = storeManager.navigate(ownerOid, DesignDisabledXmlObjectBean);

			return xmlObjs;
		} catch (XmlObjectStoreException e) {

			throw new WTException(e);
		}
	}

	/**
	 * 创建部件 存放指定文件夹
	 * 
	 * @param number
	 * @param name
	 * @param part
	 * @param type
	 * @throws Exception
	 */
	public static WTPart createPart(String number, String name, String type, String source, String unit,
			String folderpath, String clf, String containerName) throws Exception {
		WTPart part = WTPart.newWTPart();
		TypeDefinitionReference tdr = TypedUtility.getTypeDefinitionReference(type);// 设置软类型
		Folder folder = null;// (Folder) WCUtil.getWTObject(folderpath);
		WTContainer container = getContainer(containerName);
		ReferenceFactory factory = new ReferenceFactory();
		folder = FolderHelper.service.getFolder(folderpath, (WTContainerRef) factory.getReference(container));

		part.setName(name);
		part.setNumber(number);
		part.setTypeDefinitionReference(tdr);
		part.setDefaultUnit(QuantityUnit.toQuantityUnit(unit));

		part.setSource(Source.toSource(source));
		part.setContainer(container);
		View views = ViewHelper.service.getView("Design");
		ViewReference viewRef = ViewReference.newViewReference(views);
		part.setView(viewRef);
		FolderHelper.assignLocation(part, folder);
		WTPart spart = (WTPart) PersistenceHelper.manager.save(part);

		IBAUtility iba = new IBAUtility(spart);
		if (!StringUtils.isEmpty(clf)) {
			iba.setIBAValue("cls", clf);
		}

		iba.updateAttributeContainer(spart);
		iba.updateIBAHolder(spart);

		/*
		 * LWCStructEnumAttTemplate classif_ref
		 * =ClassificationUtil.getLWCStructEnumAttTemplateByName(clf);
		 * TypeInstanceIdentifier classif_TII =
		 * TypedUtilityServiceHelper.service
		 * .getTypeInstanceIdentifier(classif_ref);
		 * System.out.println(classif_TII); LWCNormalizedObject obj = new
		 * LWCNormalizedObject(part, null, Locale.US, new
		 * UpdateOperationIdentifier()); obj.load("classification.id");
		 * obj.set("classification.id", classif_TII); part = (WTPart)
		 * obj.apply(); PersistenceHelper.manager.modify(part);
		 */

		return spart;
	}

	private static Source getSource(String source) throws RunTimeException {
		Source[] set = Source.getSourceSet();
		for (int i = 0; i < set.length; i++) {
			Source s = set[i];
			if (source.equals(s.getDisplay(Locale.CHINA))) {
				return s;
			}
		}
		throw new LineException("获取不到" + source);
	}

	private static QuantityUnit getQuantityUnit(String unit) throws RunTimeException {
		QuantityUnit[] set = QuantityUnit.getQuantityUnitSet();
		for (int i = 0; i < set.length; i++) {
			QuantityUnit s = set[i];
			if (unit.equals(s.getDisplay(Locale.CHINA))) {
				return s;
			}
		}
		throw new LineException("获取不到" + unit);
	}

	/**
	 * 获取上下文
	 * 
	 * @param name
	 * @return
	 */
	public static WTContainer getContainer(String name) {
		WTContainer wtc = null;
		QuerySpec qs;
		try {
			qs = new QuerySpec(WTContainer.class);

			SearchCondition sc = new SearchCondition(PDMLinkProduct.class, PDMLinkProduct.NAME, SearchCondition.EQUAL,
					name);
			qs.appendWhere(sc);

			sc = new SearchCondition(WTLibrary.class, WTLibrary.NAME, SearchCondition.EQUAL, name);

			qs.appendOr();
			qs.appendWhere(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);

			if (qr.size() > 0) {
				wtc = (WTContainer) qr.nextElement();
				return wtc;
			}

		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}

		return wtc;
	}
}
