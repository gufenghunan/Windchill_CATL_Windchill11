package com.catl.ecad.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.catl.cadence.util.NodeUtil;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;
import com.ptc.windchill.enterprise.wvs.repsAndMarkups.commands.RepsAndMarkupsCommands;
import com.ptc.windchill.enterprise.wvs.repsAndMarkups.utils.RepsAndMarkupsHelper;
import com.ptc.wvs.server.loader.EDRContentHelper;
import com.ptc.wvs.server.loader.EDRHelper;
import com.ptc.wvs.server.ui.RepHelper;

import wt.change2.ChangeException2;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.content.HolderToContent;
import wt.doc.Document;
import wt.doc.WTDocument;
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
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTCollection;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleManaged;
import wt.maturity.MaturityHelper;
import wt.maturity.PromotionNotice;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.pom.Transaction;
import wt.project.Role;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.representation.Representation;
import wt.representation.RepresentationHelper;
import wt.representation.RepresentationType;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.team.Team;
import wt.team.TeamHelper;
import wt.team.TeamManaged;
import wt.team.TeamReference;
import wt.team.TeamTemplate;
import wt.team.TeamTemplateReference;
import wt.type.TypeDefinitionReference;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTInvalidParameterException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;
import wt.vc.VersionControlHelper;
import wt.vc.struct.StructHelper;
import wt.vc.wip.WorkInProgressHelper;
import wt.workflow.definer.WfDefinerHelper;
import wt.workflow.definer.WfProcessDefinition;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfContainer;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;

public class TestEPM implements RemoteAccess {

	public static void main(String[] args) throws RemoteException,
			InvocationTargetException, WTException {

		test();
		// WTPart part=(WTPart) new
		// ReferenceFactory().getReference("VR:wt.part.WTPart:211340").getObject();
		// checkPCBA(part);
	}

	public static void createEPM() throws Exception {
		EPMContextHelper.setApplication(EPMApplicationType
				.toEPMApplicationType("EPM-ECAD"));
		EPMAuthoringAppType authApp = EPMAuthoringAppType
				.toEPMAuthoringAppType("ORCAD_SCH");
		System.out.println(authApp.getFullDisplay());
		EPMDocument epm = EPMDocument.newEPMDocument("000005", "EPM-SCH-Test4",
				authApp, EPMDocumentType.toEPMDocumentType("ECAD-SCHEMATIC"),
				"000005");
		WTContainer container = (WTContainer) new ReferenceFactory()
				.getReference("OR:wt.pdmlink.PDMLinkProduct:126060")
				.getObject();
		epm.setContainer(container);
		PersistenceHelper.manager.save(epm);

		ContentHolder contentHolder// ��ȡ�ı����ݵĶ���d
		= ContentHelper.service.getContents(epm);
		// IBAUtility iba = new IBAUtility(epm);
		// iba.setIBAValue("ECAD_DESIGN_ITEM", "TestSCH.DSN");
		// iba.updateAttributeContainer(epm);
		// iba.updateIBAHolder(epm);

		// ContentHolder
		ApplicationData appdata = ApplicationData
				.newApplicationData(contentHolder);
		appdata.setRole(ContentRoleType.MULTI_PRIMARY);// ������Ҫ���Ǵ�Ҫ�ļ�ͨ��ApplicationData
														// ȥ�������ĵ������ơ���С���Լ��ļ��ϴ�·����.
		appdata.setCategory("NATIVE_DESIGN");
		File file = new File("E:\\000005.zip");
		appdata.setFileName("{$CAD_NAME}.zip");
		appdata.setUploadedFromPath("ECAD_SET_DLD_ON_SYNCUP");
		appdata.setClientData("");
		FileInputStream is = new FileInputStream(file);

		/*
		 * ApplicationData appdata1 =
		 * ApplicationData.newApplicationData(contentHolder);
		 * appdata1.setRole(ContentRoleType
		 * .MULTI_PRIMARY);//������Ҫ���Ǵ�Ҫ�ļ�ͨ��ApplicationData
		 * ȥ�������ĵ������ơ���С���Լ��ļ��ϴ�·����.
		 * appdata1.setCategory("GENERAL"); File file1 = new
		 * File("E:\\000005.dld"); appdata1.setFileName("{$CAD_NAME}.dld");
		 * appdata1.setClientData(""); FileInputStream is1 = new
		 * FileInputStream(file1);
		 */

		ApplicationData appdata1 = ApplicationData
				.newApplicationData(contentHolder);
		appdata1.setRole(ContentRoleType.SECONDARY);// ������Ҫ���Ǵ�Ҫ�ļ�ͨ��ApplicationData
													// ȥ�������ĵ������ơ���С���Լ��ļ��ϴ�·����.
		appdata1.setCategory("GENERAL");
		File file1 = new File("E:\\Allego-000005.zip");
		appdata1.setFileName(file1.getName());
		FileInputStream is1 = new FileInputStream(file1);

		Transaction tx = new Transaction();
		tx.start();
		ContentServerHelper.service.updateContent(contentHolder, appdata, is);// ���ĵ������ɹ�����fileStream��Ҫ�ϴ����ļ���������
		ContentServerHelper.service.updateContent(contentHolder, appdata1, is1);
		createRep(epm);
		tx.commit();
		tx = null;
	}

	public static void createPCB() throws WTInvalidParameterException,
			WTException, WTPropertyVetoException {
		EPMContextHelper.setApplication(EPMApplicationType
				.toEPMApplicationType("EPM-ECAD"));
		EPMAuthoringAppType authApp = EPMAuthoringAppType
				.toEPMAuthoringAppType("CADENCE");
		System.out.println(authApp.getFullDisplay());
		EPMDocument epm = EPMDocument.newEPMDocument("000003", "EPM-PCB-Test4",
				authApp, EPMDocumentType.toEPMDocumentType("ECAD-BOARD"),
				"000002");
		WTContainer container = (WTContainer) new ReferenceFactory()
				.getReference("OR:wt.pdmlink.PDMLinkProduct:126060")
				.getObject();
		epm.setContainer(container);

		// ApplicationData appdata1 = new ApplicationData();
		// ContentRoleType.PRODUCT_VIEW_ED
		// appdata1.setRole(ContentRoleType.MULTI_PRIMARY);
		// ContentServerHelper.service.updateContent(epm, appdata1, null, true);
		// Representation rep =null;
		// RepresentationHelper.service.storeRepresentation(rep, epm, null,
		// null, RepresentationType.PRODUCT_VIEW);
		/*
		 * QueryResult qr =
		 * RepresentationHelper.service.getRepresentations(aepm); while
		 * (qr.hasMoreElements()) { Representation representation =
		 * (Representation) qr.nextElement(); EDRHelper.addFile(representation,
		 * directory, s13, s13, null, false, true, false); }
		 */
		epm = (EPMDocument) PersistenceHelper.manager.save(epm);

	}

	/**
	 * ����ECAD��ʾ
	 */
	public static void createRep(EPMDocument schepm) throws WTException {
		// String schepmoid = "VR:wt.epm.EPMDocument:164007";
		// EPMDocument schepm = (EPMDocument)new
		// ReferenceFactory().getReference(schepmoid).getObject();
		java.io.File objPdfFile = new java.io.File("E:\\pvs\\TESTSCH.eda");
		String strPdfFileDir = objPdfFile.getParent();
		boolean republishable = false;
		boolean isDefaultRepresentation = false;
		boolean createThumbnail = false;
		boolean storeEDZ = false;
		ReferenceFactory rf = new ReferenceFactory();
		String strRepresentableOID = rf.getReferenceString(schepm);
		boolean result = RepHelper.loadRepresentation(strPdfFileDir,
				strRepresentableOID, republishable, "TESTSCH", null,
				isDefaultRepresentation, createThumbnail, storeEDZ);

	}

	public static void test() throws RemoteException,
			InvocationTargetException, WTException {
		// OR:wt.change2.WTChangeIssue:215771

		RemoteMethodServer rm = RemoteMethodServer.getDefault();
		rm.setUserName("wcadmin");
		rm.setPassword("wcadmin");
		SessionHelper.manager.setAuthenticatedPrincipal("wcadmin");
		rm.invoke("checkPCBA", TestEPM.class.getName(), null, null, null);
	}

	public static void findCheckoutlist() throws WTException {
		ECADutil.findCheckoutEPM("orcad");
	}

	public static void startWF() throws WTException {
		WTPart part = (WTPart) new ReferenceFactory().getReference(
				"VR:wt.part.WTPart:188065").getObject();
		List<WTUser> list = WorkflowHelper.getRoleUsers(part, "REVIEWER");
		System.out.println(list.size());
		for (int i = 0; i < list.size(); i++) {
			WTUser wtUser = (WTUser) list.get(i);
			System.out.println(wtUser.getName());
		}
		startWorkFlow("CIS_Library_Apply_WF", part, null);
	}

	public static void getAppData() throws WTRuntimeException, WTException {
		String schepmoid = "VR:wt.epm.EPMDocument:168440";
		EPMDocument schepm = (EPMDocument) new ReferenceFactory().getReference(
				schepmoid).getObject();
		QuerySpec queryspec = new QuerySpec(ContentItem.class,

		HolderToContent.class);
		queryspec.appendWhere(new SearchCondition

		(wt.content.ContentItem.class, "role", "=",
				ContentRoleType.MULTI_PRIMARY));
		QueryResult queryresult = PersistenceHelper.manager.navigate

		(schepm, "theContentItem", queryspec, false);
		ApplicationData theContent = null;
		while (queryresult.hasMoreElements()) {
			HolderToContent holdercontent = (HolderToContent)

			queryresult.nextElement();
			theContent = (ApplicationData) holdercontent.getContentItem();
			System.out.println(theContent.getFileName() + "\tClient"
					+ theContent.getClientData() + "End\t"
					+ theContent.getUploadedFromPath());
		}
	}

	public static void createRefLink() {
		String schepmoid = "VR:wt.epm.EPMDocument:217880";
		String pcbepmoid = "VR:wt.epm.EPMDocument:217867";
		// String schepmoid1 = "VR:wt.epm.EPMDocument:164007";
		EPMDocument pcbepm = null;
		try {
			EPMContextHelper.setApplication(EPMApplicationType
					.toEPMApplicationType("EPM-ECAD"));
			EPMDocument schepm = (EPMDocument) new ReferenceFactory()
					.getReference(schepmoid).getObject();
			pcbepm = (EPMDocument) new ReferenceFactory().getReference(
					pcbepmoid).getObject();
			// EPMDocument schepm1 = (EPMDocument)new
			// ReferenceFactory().getReference(schepmoid1).getObject();
			pcbepm = (EPMDocument) CommonUtil.checkoutObject(pcbepm);
			WTCollection objects = new WTArrayList();
			EPMReferenceLink epmRefLink = EPMReferenceLink.newEPMReferenceLink(
					pcbepm, (EPMDocumentMaster) schepm.getMaster());
			// EPMReferenceLink epmRefLink1 =
			// EPMReferenceLink.newEPMReferenceLink(pcbepm,
			// (EPMDocumentMaster)schepm1.getMaster());

			epmRefLink.setReferenceType(EPMReferenceType
					.toEPMReferenceType("RELATION"));
			epmRefLink.setDepType(-3005);

			// epmRefLink1.setReferenceType(EPMReferenceType.toEPMReferenceType("RELATION"));
			// epmRefLink1.setDepType(-3005);

			objects.add(epmRefLink);
			// objects.add(epmRefLink1);
			// epmRefLink.setAsStoredChildName(arg0);
			// epmRefLink.setTypeDefinitionReference(TypeDefinitionReference.newTypeDefinitionReference());
			// PersistenceServerHelper.manager.insert(epmRefLink);
			PersistenceHelper.manager.save(objects);
			CommonUtil.checkinObject(pcbepm, "");
		} catch (WTInvalidParameterException | WTPropertyVetoException
				| WTException e) {
			try {
				WorkInProgressHelper.service.undoCheckout(pcbepm);
			} catch (WTPropertyVetoException | WTException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}

	}

	public static void createLinkEpmToPart() throws WTException {
		String partoid = "VR:wt.part.WTPart:215138";
		String epmoid = "VR:wt.epm.EPMDocument:215149";
		// System.out.println(ModelItemSubType.toModelItemSubType("ECAD_ASSEMBLY"));
		// System.out.println(ModelItemType.toModelItemType("COMPONENT"));
		try {
			EPMDocument epm = (EPMDocument) new ReferenceFactory()
					.getReference(epmoid).getObject();
			WTPart part = (WTPart) new ReferenceFactory().getReference(partoid)
					.getObject();
			QueryResult qr = PersistenceHelper.manager.navigate(epm,
					EPMBuildRule.BUILD_TARGET_ROLE, EPMBuildRule.class, false);

			EPMBuildRule epmbuildRule = null;
			if (qr != null && qr.hasMoreElements()) {
				epmbuildRule = (EPMBuildRule) qr.nextElement();
				// Object obj = qr.nextElement();
				// System.out.println(obj);
				// return;
			} else {
				EPMBuildRule epmbr = EPMBuildRule.newEPMBuildRule(epm, part, 7);
				String sq = PersistenceHelper.manager
						.getNextSequence(EPMBuildRuleSequence.class);
				epmbr.setUniqueID(Long.parseLong(sq));
				PersistenceServerHelper.manager.insert(epmbr);
				// createLinkEpmToPart(epm, part);

				QueryResult qr1 = PersistenceHelper.manager.navigate(epm,
						EPMBuildRule.BUILD_TARGET_ROLE, EPMBuildRule.class,
						false);

				if (qr1 != null && qr1.hasMoreElements()) {
					// Object obj = qr1.nextElement();
					// System.out.println(obj);
					// return;
					epmbuildRule = (EPMBuildRule) qr1.nextElement();
				}
			}

		} catch (WTRuntimeException | WTException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 启动工作�?
	 * 
	 * @param workFlowName
	 * @param pbo
	 * @param variables
	 * @return
	 * @throws WTException
	 */
	public static WfProcess startWorkFlow(String workFlowName, Object pbo,
			HashMap variables) throws WTException {
		long WORKFLOW_PRIORITY = 1;
		try {
			WTContainerRef containerRef = null;
			if (pbo instanceof WTContained) {
				WTContained contained = (WTContained) pbo;
				containerRef = contained.getContainerReference();
			} else {
				containerRef = WTContainerHelper.service.getExchangeRef();
			}
			WTProperties wtproperties = WTProperties.getLocalProperties();
			WORKFLOW_PRIORITY = Long.parseLong(wtproperties.getProperty(
					"wt.lifecycle.defaultWfProcessPriority", "1"));
			WfProcessDefinition wfprocessDefinition = WfDefinerHelper.service
					.getProcessDefinition(workFlowName, containerRef);
			if (wfprocessDefinition == null) {
				System.out.println("Error to getWrokFlowTemplate,"
						+ workFlowName + " is null");
			}

			Object team_spec = null;

			if (pbo != null && pbo instanceof TeamManaged) {
				TeamReference teamRef = ((TeamManaged) pbo).getTeamId();
				if (teamRef != null) {
					team_spec = teamRef;
				}
			}
			if (team_spec == null) {
				String teamTemplateName = "Default";
				TeamTemplate tt = TeamHelper.service.getTeamTemplate(
						containerRef, teamTemplateName);

				if (tt != null) {
					TeamTemplateReference teamTemplateRef = TeamTemplateReference
							.newTeamTemplateReference(tt);
					team_spec = teamTemplateRef;
				}
			}

			WfProcess wfprocess = WfEngineHelper.service.createProcess(
					wfprocessDefinition, team_spec, containerRef);

			ProcessData processData = wfprocess.getContext();
			processData.setValue(WfDefinerHelper.PRIMARY_BUSINESS_OBJECT, pbo);

			if (variables != null && !variables.isEmpty()) {
				Iterator keys = variables.keySet().iterator();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					processData.setValue(key, variables.get(key));
				}
			}
			try {
				LifeCycleManaged lcm = (LifeCycleManaged) pbo;
				System.out.println("LCM\t" + lcm);
				TeamTemplateReference ttr = lcm.getTeamTemplateId();
				System.out.println("TTR\t" + ttr);
				wfprocess.setTeamTemplateId(ttr);
				wfprocess = (WfProcess) saveProcess(wfprocess);
			} catch (WTPropertyVetoException e) {
				e.printStackTrace();
			}
			wfprocess = WfEngineHelper.service.startProcessImmediate(wfprocess,
					processData, WORKFLOW_PRIORITY);
			return wfprocess;
		} catch (IOException e) {
			e.printStackTrace();
			throw new WTException(e);
		} catch (WTException e) {
			e.printStackTrace();
			throw new WTException(e);
		} finally {
		}
	}

	private static WfContainer saveProcess(WfContainer container)
			throws WTException {
		SessionContext old_session = SessionContext.newContext();
		try {
			// Establish new identity for access control, ownership, etc.
			SessionHelper.manager.setAdministrator();
			container = (WfContainer) PersistenceHelper.manager.save(container);
		} finally {
			SessionContext.setContext(old_session);
		}
		return container;
	}

	public static void setECADNumber() throws WTException,
			WTPropertyVetoException, RemoteException {
		String attrName = "PTC_ECAD_ASSEMBLY_PART_NAME";
		String attrNumber = "PTC_ECAD_ASSEMBLY_PART_NUMBER";

		String epmoid = "VR:wt.epm.EPMDocument:164007";
		EPMDocument epm = (EPMDocument) new ReferenceFactory().getReference(
				epmoid).getObject();

		IBAUtility iba = new IBAUtility(epm);
		iba.setIBAValue(attrNumber, "0000000001");
		iba.setIBAValue(attrName, "TestWF");
		iba.updateAttributeContainer(epm);
		iba.updateIBAHolder(epm);
	}

	/**
	 * /** PCBA、PCB发布验证
	 * 
	 * @param wtobj
	 * @throws ChangeException2
	 * @throws WTException
	 */
	public static void checkPCBA() throws WTException {
		WTObject wtobj = (WTObject) new ReferenceFactory().getReference(
				"OR:wt.maturity.PromotionNotice:177183080").getObject();
		WTPart parentPart = null;
		WTPart childPart = null;
		EPMDocument PCBEpm = null;
		WTDocument gdoc = null;
		WTDocument zdoc = null;
		List<String> messageError = new ArrayList<>();

		// 获取PCBA、PCB物料组
		Map<String, List<String>> map = HistoryUtils.getClfNumber();
		List<String> listPCBA = map.get("PCBA");
		List<String> listPCB = map.get("PCB");

		// 获取受影响对象里的需校验对象
		PromotionNotice notice = (PromotionNotice) wtobj;
		QueryResult qr = MaturityHelper.service.getPromotionTargets(notice);
		while (qr.hasMoreElements()) {
			Object obj = qr.nextElement();
			if (obj instanceof WTPart) {
				WTPart part = (WTPart) obj;
				LWCStructEnumAttTemplate lwc = NodeUtil
						.getLWCStructEnumAttTemplateByPart(part);
				if (lwc == null) {
					continue;
				}
				String pn = lwc.getName();
				if (listPCBA.contains(pn)) {
					parentPart = part;
				} else if (listPCB.contains(pn)) {
					childPart = part;
				}
			} else if (obj instanceof EPMDocument) {
				EPMDocument epm = (EPMDocument) obj;
				if (ECADutil.isPCBEPM(epm)) {
					PCBEpm = epm;
				}
			} else if (obj instanceof WTDocument) {
				WTDocument document = (WTDocument) obj;
				if (ECADutil.isGerberDoc(document)) {
					gdoc = document;
				} else if (ECADutil.isPCBADrawing(document)) {
					zdoc = document;
				}
			}
		}

		// PCBA验证
		if (parentPart != null) {
			// 原理图是否已发布
			String msg = checkPCBAAndPCBEpm(parentPart, null);
			if (msg != null) {
				messageError.add(msg);
			}
		}

		// PCB验证
		if (childPart != null) {
			// PCB图是否已发布
			String msgError = checkPCBAAndPCBEpm(childPart, PCBEpm);
			if (msgError != null) {
				messageError.add(msgError);
			}
		}

		// PCB图验证
		if (PCBEpm != null) {
			// 是否已发布原理图
			String mesError = ECADutil.checkEPMPCBA(PCBEpm);
			if (mesError != null) {
				messageError.add(mesError);
			}
			// 是否已发布Gerber文件、装配图
			Map<String, Object> pmap = ECADutil.getPartByPCBEPM(PCBEpm);
			WTPart part = (WTPart) pmap.get("part");
			String msgError = (String) pmap.get("error");
			if (msgError != null) {
				messageError.add(msgError);
			}
			List<String> msgList = checkPCBDescribed2(part, gdoc, zdoc);
			messageError.addAll(msgList);
		}

		String mError = null;
		for (String error : messageError) {
			if (mError == null) {
				mError = error;
			} else {
				mError += error;
			}
		}
		if (mError != null) {
			throw new WTException(mError);
		}
	}

	/**
	 * /** PCBA、PCB设计/工程变更验证
	 * 
	 * @param wtobj
	 * @throws WTException
	 */
	public static void CheckPBCAForChange() throws WTException {
		WTObject wtobj = (WTObject) new ReferenceFactory().getReference(
				"VR:wt.change2.WTChangeOrder2:177100164").getObject();
		WTPart pcbaPart = null;
		WTPart pcbPart = null;
		EPMDocument PCBAEpm = null;
		EPMDocument PCBEpm = null;
		WTDocument gdoc = null;
		WTDocument zdoc = null;
		List<String> msgErrorList = new ArrayList<>();

		Map<String, List<String>> map = HistoryUtils.getClfNumber();
		List<String> listPCBA = map.get("PCBA");
		List<String> listPCB = map.get("PCB");

		// 获取受影响对象里的需校验对象
		QueryResult qr = null;
		if (wtobj instanceof WTChangeRequest2) {
			WTChangeRequest2 request2 = (WTChangeRequest2) wtobj;
			qr = ChangeHelper2.service.getChangeables(request2);
		} else if (wtobj instanceof WTChangeOrder2) {
			WTChangeOrder2 order2 = (WTChangeOrder2) wtobj;
			qr = ChangeHelper2.service.getChangeablesBefore(order2);
		}

		while (qr.hasMoreElements()) {
			Object obj = qr.nextElement();
			if (obj instanceof WTPart) {
				WTPart part = (WTPart) obj;
				LWCStructEnumAttTemplate lwc = NodeUtil
						.getLWCStructEnumAttTemplateByPart(part);
				if (lwc == null) {
					continue;
				}
				String pn = lwc.getName();
				if (listPCBA.contains(pn)) {
					pcbaPart = part;
				} else if (listPCB.contains(pn)) {
					pcbPart = part;
				}
			} else if (obj instanceof EPMDocument) {
				EPMDocument epm = (EPMDocument) obj;
				if (ECADutil.isSCHEPM(epm)) {
					PCBAEpm = epm;
				} else if (ECADutil.isPCBEPM(epm)) {
					PCBEpm = epm;
				}
			} else if (obj instanceof WTDocument) {
				WTDocument document = (WTDocument) obj;
				if (ECADutil.isGerberDoc(document)) {
					gdoc = document;
				} else if (ECADutil.isPCBADrawing(document)) {
					zdoc = document;
				}
			}
		}

		// PCBA原理图变更验证
		if (PCBAEpm != null) {
			// PCBA部件是否在同一流程
			Map<String, Object> checkMap = checkEPMPCB(PCBAEpm, pcbaPart);
			WTPart wtpart = (WTPart) checkMap.get("part");
			msgErrorList.addAll((List<String>) checkMap.get("error"));
			// PCB部件及相关文件是否在同一流程
			List<WTPart> parts = ECADutil.getAllChildPart(wtpart);
			if (parts.size() == 0) {
				msgErrorList.add("PCBA【" + wtpart.getNumber() + "】没有添加子件！");
			}
			for (WTPart part : parts) {
				LWCStructEnumAttTemplate lwc = NodeUtil
						.getLWCStructEnumAttTemplateByPart(part);
				if (lwc == null) {
					continue;
				}
				String pn = lwc.getName();
				if (listPCB.contains(pn)) {
					// PCB部件是否在同一流程
					String msg = checkPCBPart(part, pcbPart);
					if (msg != null) {
						msgErrorList.add(msg);
					}
					// PCB图是否在同一流程
					String msg2 = checkPCBEpm(part, PCBEpm);
					if (msg2 != null) {
						msgErrorList.add(msg2);
					}
					// Gerber文件、装配图是否在同一流程
					List<String> msgs = checkPCBDescribed(part, gdoc, zdoc);
					msgErrorList.addAll(msgs);
				}
			}
		}

		// PCB图变更验证
		if (PCBEpm != null) {
			// PCB部件是否在同一流程
			Map<String, Object> checkMap2 = checkEPMPCB(PCBEpm, pcbPart);
			WTPart part = (WTPart) checkMap2.get("part");
			msgErrorList.addAll((List<String>) checkMap2.get("error"));

			// Gerber文件、装配图是否在同一流程
			List<String> msgs = checkPCBDescribed(part, gdoc, zdoc);
			msgErrorList.addAll(msgs);
		}
		String msgError = null;
		for (String error : msgErrorList) {
			if (msgError == null) {
				msgError = error;
			} else {
				msgError += error;
			}
		}
		if (msgError != null) {
			throw new WTException(msgError);
		}
	}

	/**
	 * 原理图/PCB图是否已发布
	 * 
	 * @param part
	 * @param PCBAEpm
	 * @throws WTException
	 */
	public static String checkPCBAAndPCBEpm(WTPart part, EPMDocument epmDoc)
			throws WTException {
		String messageError = null;
		QueryResult qr = PartDocServiceCommand.getAssociatedCADDocuments(part);
		while (qr.hasMoreElements()) {
			EPMDocument epm = (EPMDocument) qr.nextElement();
			if (ECADutil.isSCHEPM(epm)
					&& !"RELEASED".equals(epm.getLifeCycleState().toString())) {
				messageError = "原理图" + epm.getNumber() + "必须先发布！";
			} else if (ECADutil.isPCBEPM(epm)
					&& !"RELEASED".equals(epm.getLifeCycleState().toString())) {
				if (epmDoc == null || epm.equals(epmDoc)) {
					messageError = "PCB图" + epm.getNumber()
							+ "必须先发布或同在升级列表一起发布！";
				}
			}
		}
		return messageError;
	}

	/**
	 * 原理图与PCBA是否在同一流程
	 * 
	 * @param part
	 * @param PCBAEpm
	 * @throws WTException
	 */
	public static void checkPCBAEpm(WTPart part, EPMDocument PCBAEpm)
			throws WTException {
		QueryResult qr = PartDocServiceCommand.getAssociatedCADDocuments(part);
		while (qr.hasMoreElements()) {
			EPMDocument epm = (EPMDocument) qr.nextElement();
			if (ECADutil.isSCHEPM(epm)
					&& !"RELEASED".equals(epm.getLifeCycleState().toString())) {
				if (!PCBAEpm.equals(epm)) {
					throw new WTException("PCBA原理图【" + epm.getNumber()
							+ "】未添加进受影响对象");
				}
			}
		}
	}

	/**
	 * PCB部件是否与PCBA部件在同一流程
	 * 
	 * @param part
	 * @param childPart
	 * @throws WTException
	 */
	public static String checkPCBPart(WTPart part, WTPart childPart)
			throws WTException {
		String msgError = null;
		if (childPart == null
				|| !childPart.getNumber().equals(part.getNumber())) {
			msgError = "PCB部件【" + part.getNumber() + "】未添加进受影响对象！";
		}
		return msgError;
	}

	/**
	 * PCB图是否与PCB部件在同一流程
	 * 
	 * @param part
	 * @param PCBEpm
	 * @throws WTException
	 */
	public static String checkPCBEpm(WTPart part, EPMDocument PCBEpm)
			throws WTException {
		String msgError = null;
		QueryResult qr = PartDocServiceCommand.getAssociatedCADDocuments(part);
		while (qr.hasMoreElements()) {
			EPMDocument epm = (EPMDocument) qr.nextElement();
			if (ECADutil.isPCBEPM(epm)) {
				if (PCBEpm == null || !PCBEpm.equals(epm)) {
					msgError = "PCB图【" + epm.getNumber() + "】未添加进受影响对象！";
				}
			}
		}
		return msgError;
	}

	/**
	 * PCB图、原理图变更时其关联部件是否一同变更
	 * 
	 * @param epm
	 *            PCB图、原理图
	 * @param wtpart
	 *            受影响对象
	 * @return PCB图、原理图关联part
	 * @throws WTException
	 */
	public static Map<String, Object> checkEPMPCB(EPMDocument epm, WTPart wtpart)
			throws WTException {
		Map<String, Object> map = new HashMap<>();
		List<String> errorList = new ArrayList<>();
		QueryResult qr = PersistenceHelper.manager.navigate(epm,
				EPMBuildRule.BUILD_TARGET_ROLE, EPMBuildRule.class, false);
		WTPart part = null;
		while (qr.hasMoreElements()) {
			EPMBuildRule buildRule = (EPMBuildRule) qr.nextElement();
			part = (WTPart) buildRule.getRoleBObject();
			break;
		}
		if (part == null) {
			errorList.add("图纸【" + epm.getNumber() + "】未关联部件!");
		}
		if (wtpart == null || !part.equals(wtpart)) {
			errorList.add("图纸【" + epm.getNumber() + "】关联的部件【"
					+ part.getNumber() + "】未添加进受影响对象！");
		}
		map.put("error", errorList);
		map.put("part", part);
		return map;
	}

	/**
	 * PCB图单独发布时其关联的原理图必须先发布
	 * 
	 * @param epm
	 *            PCB图
	 * @throws WTException
	 */
	public static void checkEPMPCBA(EPMDocument epm) throws WTException {
		QueryResult qr = PersistenceHelper.manager
				.navigate(epm, EPMReferenceLink.REFERENCES_ROLE,
						EPMReferenceLink.class, false);
		EPMDocument epmDocument = null;
		while (qr.hasMoreElements()) {
			EPMReferenceLink referenceLink = (EPMReferenceLink) qr
					.nextElement();
			EPMDocumentMaster master = (EPMDocumentMaster) referenceLink
					.getRoleBObject();
			QueryResult qr2 = VersionControlHelper.service
					.allVersionsOf(master);
			while (qr2.hasMoreElements()) {
				epmDocument = (EPMDocument) qr2.nextElement();
			}
		}
		if (!"RELEASED".equals(epmDocument.getLifeCycleState().toString())) {
			throw new WTException("原理图【" + epmDocument.getNumber() + "】必须先发布！");
		}
	}

	/**
	 * PCB Gerber文件、装配图与PCB是否在同一流程
	 * 
	 * @param part
	 *            PCB-Part
	 * @param gdoc
	 *            Gerber文件
	 * @param zdoc
	 *            装配图
	 * @throws WTException
	 */
	public static List<String> checkPCBDescribed(WTPart part, Document gdoc,
			Document zdoc) throws WTException {
		List<String> messageError = new ArrayList<>();
		List<WTDocument> documents = ECADutil.getDocByPart(part);
		if (documents.size() == 0) {
			messageError.add("PCB【" + part.getNumber() + "】未关联Gerber文件和装配图！");
		}
		boolean haveGerber = false;
		boolean havePCBADrawing = false;
		for (WTDocument document : documents) {
			if (ECADutil.isGerberDoc(document)) {
				if (gdoc == null || !gdoc.equals(document)) {
					String error = "Gerber文件【" + document.getNumber()
							+ "】未添加进受影响对象！";
					if (!messageError.contains(error)) {
						messageError.add(error);
					}

				}
				haveGerber = true;
			}
			if (ECADutil.isPCBADrawing(document)) {
				if (zdoc == null || !zdoc.equals(document)) {
					String error2 = "装配图【" + document.getNumber()
							+ "】未添加进受影响对象！";
					if (!messageError.contains(error2)) {
						messageError.add(error2);

					}
				}
				havePCBADrawing = true;
			}
		}
		if (!haveGerber && documents.size() != 0) {
			messageError.add("PCB【" + part.getNumber() + "】未关联Gerber文件！");
		} else if (!havePCBADrawing && documents.size() != 0) {
			messageError.add("PCB【" + part.getNumber() + "】未关联装配图文件！");
		}
		return messageError;
	}

	/**
	 * 获取部件说明文档
	 * 
	 * @param part
	 * @return String
	 * @throws WTException
	 */
	public static List<WTDocument> getDocByPart(WTPart part) throws WTException {
		WTDocument document = null;
		List<WTDocument> documents = new ArrayList<>();
		QueryResult qr = StructHelper.service.navigateDescribedBy(part);
		while (qr.hasMoreElements()) {
			WTDocument wtDocument = (WTDocument) qr.nextElement();
			QueryResult qr2 = VersionControlHelper.service
					.allVersionsOf(wtDocument);
			if (qr2.hasMoreElements()) {
				document = (WTDocument) qr2.nextElement();
				if (!documents.contains(document)) {
					documents.add(document);
				}
			}
		}
		return documents;
	}

	/**
	 * 发布PCB图必须先发布Gerber文件、装配图
	 * 
	 * @param part
	 * @throws WTException
	 */
	public static List<String> checkPCBDescribed2(WTPart part, WTDocument gdoc,
			WTDocument zdoc) throws WTException {
		List<String> msgerror = new ArrayList<String>();
		List<WTDocument> documents = getDocByPart(part);
		if (documents.size() == 0) {
			msgerror.add("PCB【" + part.getNumber() + "】未关联Gerber文件和装配图！");
		}
		boolean haveGerber = false;
		boolean havePCBADrawing = false;
		for (WTDocument document : documents) {
			if (ECADutil.isGerberDoc(document)) {
				System.out.println("gggggggggggggggggggggggg" + document);
				if (!ECADConst.RELEASED_STATE.equals(document
						.getLifeCycleState().toString())) {
					if (gdoc == null || !gdoc.equals(document)) {
						msgerror.add("Gerber文件【" + document.getNumber()
								+ "】必须先发布或同在升级列表，一起发布！");
					}
				}
				haveGerber = true;
			} else if (ECADutil.isPCBADrawing(document)) {
				System.out.println("zzzzzzzzzzzzzzzzzzzzzzzzz" + document);
				if (!ECADConst.RELEASED_STATE.equals(document
						.getLifeCycleState().toString())) {
					if (zdoc == null || !zdoc.equals(document)) {
						msgerror.add("PCBA装配图【" + document.getNumber()
								+ "】必须先发布或同在升级列表，一起发布！");
					}
				}
				havePCBADrawing = true;
			}
		}
		if (!haveGerber && documents.size() != 0) {
			msgerror.add("PCB【" + part.getNumber() + "】未关联Gerber文件！");
		} else if (!havePCBADrawing && documents.size() != 0) {
			msgerror.add("PCB【" + part.getNumber() + "】未关联装配图文件！");
		}
		return msgerror;
	}

	/**
	 * 电子物料提交时校对角色是否包含ECAD工程师组
	 * 
	 * @param self
	 * @throws WTException
	 */
	public static void checkRole(Object self, WTObject wtobj)
			throws WTException {
		if (wtobj instanceof PromotionNotice) {
			PromotionNotice notice = (PromotionNotice) wtobj;
			if (notice != null) {
				QueryResult qr = MaturityHelper.service
						.getPromotionTargets(notice);
				while (qr.hasMoreElements()) {
					Object obj = qr.nextElement();
					if (obj instanceof WTPart) {
						WTPart part = (WTPart) obj;
						IBAUtility utility = new IBAUtility(part);
						String assembleyType = utility
								.getIBAValue("Assembly_Type");
						if (assembleyType != null
								&& !assembleyType.equals("NA")) {
							boolean flag = false;
							if (self instanceof ObjectReference) {
								ObjectReference objectReference = (ObjectReference) self;
								flag = WorkflowHelper.ifRoleHasUsers(null,
										objectReference, ECADConst.ECAD);
							}
							if (!flag) {
								throw new WTException("升级列表包含电子料，请添加ECAD工程师成员！");
							}
						}
					}
				}
			}
		}
	}
}
