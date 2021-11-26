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
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import com.catl.bom.workflow.BomWfUtil;
import com.catl.cadence.conf.InitSystemConfigContant;
import com.catl.cadence.util.NodeUtil;
import com.catl.cadence.util.PartReleaseToCadenceUtil;
import com.catl.change.DataUtility.CatlPropertyHelper;
import com.catl.common.constant.PartState;
import com.catl.common.constant.TypeName;
import com.catl.common.util.EpmUtil;
import com.catl.common.util.IBAUtil;
import com.catl.common.util.PartUtil;
import com.catl.doc.maturityUpReport.MaturityUpReportHelper;
import com.catl.ecad.bean.CadenceAttributeBean;
import com.catl.ecad.dbs.CadenceXmlObjectUtil;
import com.catl.ecad.load.LoadAccess;
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
import com.catl.part.CheckPDFConstants;
import com.catl.part.PartConstant;
import com.catl.promotion.bean.DesignDisabledXmlObjectBean;
import com.catl.promotion.dbs.DesignDisabledXmlObjectUtil;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
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
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.content.HolderToContent;
import wt.doc.DocumentMaster;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
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
import wt.epm.workspaces.EPMWorkspace;
import wt.epm.workspaces.EPMWorkspaceHelper;
import wt.fc.ObjectIdentifier;
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
import wt.fc.collections.WTSet;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.maturity.MaturityBaseline;
import wt.maturity.MaturityException;
import wt.maturity.MaturityHelper;
import wt.maturity.PromotionNotice;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.ownership.Ownership;
import wt.ownership.OwnershipHelper;
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
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.representation.Representation;
import wt.representation.RepresentationHelper;
import wt.representation.RepresentationType;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.team.Team;
import wt.team.TeamHelper;
import wt.team.TeamManaged;
import wt.team.TeamReference;
import wt.team.TeamTemplate;
import wt.team.TeamTemplateReference;
import wt.team.WTRoleHolder2;
import wt.type.TypeDefinitionReference;
import wt.util.WTException;
import wt.util.WTInvalidParameterException;
import wt.util.WTMessage;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;
import wt.vc.IterationInfo;
import wt.vc.VersionControlHelper;
import wt.vc.baseline.BaselineHelper;
import wt.vc.config.ConfigHelper;
import wt.vc.config.LatestConfigSpec;
import wt.vc.struct.StructHelper;
import wt.vc.wip.WorkInProgressHelper;
import wt.viewmarkup.DerivedImage;
import wt.workflow.definer.WfDefinerHelper;
import wt.workflow.definer.WfProcessDefinition;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfContainer;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WorkItem;

public class Test3 implements RemoteAccess {
	
	public final static String DesignDisabledXmlObjectBean = "DesignDisabledXmlObjectBean";

	public static void main(String[] args) throws RemoteException,
			InvocationTargetException, WTException {

		test();

	}

	
	public static void test() throws RemoteException,
			InvocationTargetException, WTException {
		//WTObject obj = (WorkItem) new ReferenceFactory().getReference("OR:wt.change2.WTChangeOrder2:177069863").getObject();
		RemoteMethodServer rm = RemoteMethodServer.getDefault();
		//rm.setUserName("60011102");
		//rm.setPassword("Su20100209..");
		rm.setUserName("wcadmin");
		rm.setPassword("wcadmin");
		SessionHelper.manager.setAuthenticatedPrincipal("wcadmin");

		rm.invoke("test1", Test3.class.getName(), null,
				null, null);
	}
	
	public static void test1() throws Exception {
		/*WTPart part = (WTPart) new ReferenceFactory().getReference(
				"VR:wt.part.WTPart:225557596").getObject();
		//PartReleaseToCadenceUtil.sendPartToCadence(part);
		StringBuilder message = new StringBuilder();
		checkPartPDFInfo(part, message);
		System.out.println(message);*/
		
		/*WTContainer container = LoadAccess.getContainer("设备开发产品库");
		ReferenceFactory fac = new ReferenceFactory();
		WTContainerRef ref = (WTContainerRef) fac.getReference(container);
		WTPrincipal principal = SessionHelper.getPrincipal();
		
		WTSet setWK = EPMWorkspaceHelper.manager.getWorkspaces(principal, ref);
		
		for(Object obj:setWK){
			ObjectReference objref = (ObjectReference) obj;
			
				EPMWorkspace wk = (EPMWorkspace) objref.getObject();
				System.out.println(wk.getName()+".....\t"+wk.getContainer().getName());
				if(container.equals(wk.getContainer())){
					System.out.println(wk.getName()+"\n"+wk.getPartFolder().getName());
				}
			
			System.out.println(obj);
		}*/
		WTDocument doc = CommonUtil.getLatestWTDocByNumber("MRGR-00000004");
		delRefLinks(doc);
	}

	public static void delRefLinks(WTDocument doc) throws WTException{
		if(doc != null){
			WTDocumentMaster master = (WTDocumentMaster) doc.getMaster();
			QueryResult qr = getReferenceLink(master);//StructHelper.service.navigateReferencedBy(master, WTPartReferenceLink.class, false);
			
			while(qr.hasMoreElements()){				
				WTPartReferenceLink refLink = (WTPartReferenceLink) qr.nextElement();
				System.out.println(refLink);
				PersistenceServerHelper.manager.remove(refLink);
			}
			
			WTArrayList list = PartDocServiceCommand.getAssociatedRefParts(doc);
			
			for(Object obj:list){
				System.out.println("PD\t"+obj);
			}
		}
	}
	
	private static QueryResult getReferenceLink(WTDocumentMaster docMaster) throws WTException {
		int[] index = { 0 };
		QuerySpec qs = new QuerySpec(WTPartReferenceLink.class);
		qs.appendWhere(new SearchCondition(WTPartReferenceLink.class,
				WTPartReferenceLink.ROLE_BOBJECT_REF + "." + ObjectReference.KEY, SearchCondition.EQUAL,
				getOid(docMaster)), index);
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		if (!qr.hasMoreElements()) {
			return null;
		}
		return qr;
	}
	
	public static ObjectIdentifier getOid(Object obj) {
		if (obj == null) {
			return null;
		} else if (obj instanceof Persistable) {
			return PersistenceHelper.getObjectIdentifier((Persistable) obj);
		} else if (obj instanceof ObjectReference) {
			return (ObjectIdentifier) ((ObjectReference) obj).getKey();
		} else {
			throw new WTRuntimeException("Class not handled: " + obj.getClass().getName());
		}
	}
	
	public static void testReleasePart() throws WTException{
		WTPart part = CommonUtil.getPartByNumber("310120-00389");
		
		List<CadenceAttributeBean> list = CadenceXmlObjectUtil
				.getCadenceAttributeBeanUtil((WTObject) part);
		System.out.println(list.size());
		for(int i= 0; i<list.size();i++) {
			Map<String,String> colAndValue = new HashMap();
			CadenceAttributeBean cadbean = list.get(i);
			System.out.println(cadbean.getIdentifier());
			colAndValue = cadbean.getNameAndValue();
			Set<String> keys = colAndValue.keySet();
			for(String key:keys){
				System.out.println(key+"\t000000000000000000\t"+colAndValue.get(key));
			}
		}
		
	}
	
	public static void checkPartPDFInfo(WTPart part, StringBuilder message)
			throws WTException {
		if (part != null) {
			String partNumber = part.getNumber();
			String checkStr = null;
			if (partNumber.length() > 6) {
				checkStr = part.getNumber().substring(0, 6);
			} else {
				checkStr = partNumber;
			}
			boolean need2dAutoCAD = false;
			int mnum = 0;
			String msg1 = null;
			String msg2 = null;

			// System.out.println("===checkStr:"+checkStr);
			Set<String> needCheckInfo = CheckPDFConstants.needCheckInfo;
			// System.out.println("===needCheckInfo:"+needCheckInfo.toString());
			if (needCheckInfo.contains(checkStr)) {
				mnum = 1;
				System.out.println("need autocad");
				if (!PartUtil.hasPDF(part)) {
					msg1 = WTMessage.formatLocalizedMessage(
							"零部件[{0}]必需有PDF图纸或者PDF图纸的命名有误！\n",
							new Object[] { partNumber });
					// message.append(WTMessage.formatLocalizedMessage("零部件[{0}]必需有PDF图纸或者PDF图纸的命名有误！\n",
					// new Object[]{partNumber}));
				} else {
					need2dAutoCAD = true;
					System.out.println("Need 2D is true");
				}
			}

			Set<String> needCheck3DInfo = CheckPDFConstants.needCheck3DInfo;
			if (needCheck3DInfo.contains(checkStr)) {
				Collection<EPMDocument> cn = EpmUtil.getRelatedEpmdoc(part);
				boolean flag = true;
				if (!cn.isEmpty()) {
					Iterator epmiteraIterator = cn.iterator();
					while (epmiteraIterator.hasNext()) {
						EPMDocument epmdoc = (EPMDocument) epmiteraIterator
								.next();

						if (epmdoc.getCADName().toUpperCase()
								.endsWith(".CATPART")
								|| epmdoc.getCADName().toUpperCase()
										.endsWith(".CATPRODUCT")) {
							flag = false;
						}
					}
				}
				if (flag) {
					message.append(WTMessage.formatLocalizedMessage(
							"零部件[{0}]必需有3D图纸！\n", new Object[] { partNumber }));
				}

			}

			if (!need2dAutoCAD) {
				Set<String> needCheck2DInfo = CheckPDFConstants.needCheck2DInfo;
				if (needCheck2DInfo.contains(checkStr)) {
					if (mnum == 1) {
						mnum = 3;
					} else {
						mnum = 2;
					}
					QueryResult qr =PartDocServiceCommand.getAssociatedCADDocuments(part);
					System.out.println(qr.size());
					while(qr.hasMoreElements()){
						Object obj = qr.nextElement();
						if(obj instanceof EPMDocument){
							EPMDocument epm1 = (EPMDocument) obj;
							System.out.println(epm1.getNumber());
						}
					}
					Collection<EPMDocument> cn = EpmUtil.getRelatedEpmdoc(part);
					System.out.println("cn\t"+cn.size());
					boolean flag = true;
					if (!cn.isEmpty()) {
						Iterator epmiteraIterator = cn.iterator();
						while (epmiteraIterator.hasNext()) {
							EPMDocument epmdoc = (EPMDocument) epmiteraIterator
									.next();
							System.out.println("EPMNumber \t"+epmdoc.getNumber());
							if (epmdoc.getCADName().toUpperCase()
									.endsWith(".CATDRAWING")) {
								flag = false;
							}
						}
					}
					if (flag) {
						msg2 = WTMessage.formatLocalizedMessage(
								"零部件[{0}]必需有2D图纸！\n",
								new Object[] { partNumber });
					} else {
						need2dAutoCAD = true;
					}

				}
				System.out.println("Need2d\t"+need2dAutoCAD+"\t"+mnum);
				if (!need2dAutoCAD) {
					if (mnum == 1) {
						message.append(msg1);
					} else if (mnum == 2) {
						message.append(msg2);
					} else if(mnum ==3){
						message.append(WTMessage.formatLocalizedMessage(
								"零部件[{0}]必需有2D图纸或者PDF图纸，或者PDF图纸的命名有误！\n",
								new Object[] { partNumber }));
					}
				}
			}
		}
	}
	
	public static void testgroup() throws WTException, WTPropertyVetoException, FileNotFoundException, IOException, JSONException {
		WTPart part = CommonUtil.getLatestWTpartByNumber("850000-00027");
		List<XmlObject> xmlObjs = getXmlObjectUtil(part);
		System.out.println("============xmlObjs\t"+xmlObjs.size());
		if(xmlObjs.size() > 0){
			String reason = ((DesignDisabledXmlObjectBean)xmlObjs.get(0)).getreason();
			System.out.println("===========Reasoin:\t"+reason);
			String state = CatlPropertyHelper.getDisableReasonERPMapPropertyValue(reason);
			System.out.println("===========State\t"+state);
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
}
