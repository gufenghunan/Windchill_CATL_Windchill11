package com.catl.test;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;

import com.catl.cadence.util.ConfigTableUtil;
import com.catl.cadence.util.NodeUtil;
import com.catl.cadence.util.PartReleaseToCadenceUtil;
import com.catl.change.DataUtility.CatlPropertyHelper;
import com.catl.common.constant.DocState;
import com.catl.common.constant.PartState;
import com.catl.common.toolbox.data.UpdateAllPartSpec;
import com.catl.ecad.load.LoadDataUtils;
import com.catl.ecad.load.ObjectTypeUtil1;
import com.catl.ecad.utils.AccessControlUtil;
import com.catl.ecad.utils.BOMUtil;
import com.catl.ecad.utils.CommonUtil;
import com.catl.ecad.utils.ECADConst;
import com.catl.ecad.utils.ECADutil;
import com.catl.ecad.utils.EPMUtil;
import com.catl.ecad.utils.FTPUitl;
import com.catl.ecad.utils.HistoryUtils;
import com.catl.ecad.utils.IBAUtility;
import com.catl.ecad.utils.WorkflowHelper;
import com.ptc.core.components.forms.EditWorkableFormProcessor;
import com.ptc.core.meta.type.mgmt.server.impl.WTTypeDefinition;
import com.ptc.core.meta.type.mgmt.server.impl.WTTypeDefinitionMaster;
import com.ptc.wvs.server.loader.EDRHelper;
import com.ptc.wvs.server.ui.RepHelper;

import wt.access.AccessControlHelper;
import wt.access.AccessPermission;
import wt.admin.AdminDomainRef;
import wt.admin.AdministrativeDomain;
import wt.admin.AdministrativeDomainHelper;
import wt.content.ApplicationData;
import wt.content.ContentException;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.content.HolderToContent;
import wt.doc.WTDocument;
import wt.epm.EPMApplicationType;
import wt.epm.EPMAuthoringAppType;
import wt.epm.EPMContextHelper;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.EPMDocumentType;
import wt.epm.build.EPMBuildRule;
import wt.epm.build.EPMBuildRuleSequence;
import wt.epm.structure.EPMReferenceLink;
import wt.epm.structure.EPMReferenceType;
import wt.facade.persistedcollection.PersistedCollectionHelper;
import wt.facade.persistedcollection.PersistedCollectionHolder;
import wt.fc.ObjectReference;
import wt.fc.ObjectVector;
import wt.fc.PersistInfo;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTCollection;
import wt.fc.collections.WTHashSet;
import wt.folder.Cabinet;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.folder.FolderNotFoundException;
import wt.folder.SubFolder;
import wt.iba.definition.StringDefinition;
import wt.iba.value.StringValue;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
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
import wt.part.WTPartMaster;
import wt.part.WTPartReferenceLink;
import wt.pds.DatabaseInfoUtilities;
import wt.pds.StatementSpec;
import wt.pom.Transaction;
import wt.project.Role;
import wt.query.FromClause;
import wt.query.NavigateSpec;
import wt.query.OrderBy;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.TableColumn;
import wt.queue.ProcessingQueue;
import wt.queue.QueueHelper;
import wt.queue.ScheduleQueue;
import wt.queue.WtQueue;
import wt.representation.Representation;
import wt.representation.RepresentationHelper;
import wt.scheduler.ScheduleItem;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.team.TeamHelper;
import wt.team.TeamManaged;
import wt.team.TeamReference;
import wt.team.TeamTemplate;
import wt.team.TeamTemplateReference;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.util.WTInvalidParameterException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;
import wt.vc.IterationInfo;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;
import wt.vc.config.ConfigSpec;
import wt.vc.config.LatestConfigSpec;
import wt.vc.struct.StructHelper;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;
import wt.workflow.definer.WfDefinerHelper;
import wt.workflow.definer.WfProcessDefinition;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfContainer;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WorkItem;

public class Test7 implements RemoteAccess {

	private static Logger logger=Logger.getLogger(Test7.class.getName());
	public static void main(String[] args) throws RemoteException, InvocationTargetException, WTException {
		
		test();
		//com.ptc.xworks.workflow.attachment.UploadAttachmentAttributePanelBuilder
		//com.ptc.xworks.xmlobject.web.form.GetContentHolderFormProcessorDelegate
		//com.ptc.xworks.workflow.attachment.GroupedAttachmentInfo
		//com.ptc.xworks.workflow.attachment.AddFileAttachmentProcessor
		//com.ptc.windchill.enterprise.attachments.commands.AttachmentCommands
		//com.ptc.windchill.enterprise.attachments.mvc.builders.AttachmentsTableBuilder
		//com.ptc.windchill.enterprise.servlets.AttachmentsEditServlet
		//com.ptc.windchill.enterprise.wip.tags.AutoCheckOutObjectTag
		//EditWorkableFormProcessor
		//com.ptc.core.components.tags.components.InitializeItemTag
		//com.ptc.windchill.enterprise.wip.DisableForWorkingCopyWorkInProgressFilter
		//com.ptc.windchill.enterprise.requirement.validators.NewTraceabilityLinkValidator
		//com.ptc.windchill.enterprise.attachments.forms.SecondaryAttachmentsSubFormProcessor
		//com.ptc.xworks.xmlobject.store.oracle.OracleSequenceIdGenerator
		//com.ptc.xworks.xmlobject.store.oracle.BaseXmlObjectStoreMapper
		
	}
	
	public static void test() throws RemoteException, InvocationTargetException, WTException {
		RemoteMethodServer rm = RemoteMethodServer.getDefault();
		rm.setUserName("wcadmin");
		rm.setPassword("wcadmin");
		SessionHelper.manager.setAuthenticatedPrincipal("wcadmin");

		rm.invoke("getLastedDocByStringIBAValue", Test7.class.getName(), null, null, null);
	}
	
	public static Set<WTPart> getLastedDocByStringIBAValue() {
		String iba ="CATL_SoftwareRiskPoint";
		String ibaValue="2";

		Set<WTPart> ret = new HashSet<WTPart>();

		QuerySpec qs;
		try {
			qs = new QuerySpec();
			qs.setAdvancedQueryEnabled(true);
			int index0 = qs.appendClassList(WTPart.class, true);
			int index1 = qs.appendClassList(StringValue.class, false);
			int index2 = qs.appendClassList(StringDefinition.class, false);

			SearchCondition join = new SearchCondition(WTPart.class, WTAttributeNameIfc.ID_NAME, StringValue.class, StringValue.IBAHOLDER_REFERENCE + "." + WTAttributeNameIfc.REF_OBJECT_ID);
			SearchCondition join1 = new SearchCondition(StringValue.class, StringValue.DEFINITION_REFERENCE + "." + WTAttributeNameIfc.REF_OBJECT_ID, StringDefinition.class, WTAttributeNameIfc.ID_NAME);

			qs.appendWhere(join, new int[] { index0, index1 });
			qs.appendAnd();
			qs.appendWhere(join1, new int[] { index1, index2 });

			/*if (excludeNumber != null) {
				qs.appendAnd();
				qs.appendOpenParen();
				qs.appendOpenParen();
				qs.appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL, excludeNumber), new int[] { index0 });
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(WTPart.class, "versionInfo.identifier.versionId", SearchCondition.NOT_EQUAL, version), new int[] { index0 });
				qs.appendCloseParen();
				qs.appendOr();
				qs.appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.NOT_EQUAL, excludeNumber), new int[] { index0 });
				qs.appendCloseParen();
			}*/
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTPart.class, WTPart.LATEST_ITERATION, SearchCondition.IS_TRUE), new int[] { index0 });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTPart.class, WTPart.LIFE_CYCLE_STATE, SearchCondition.NOT_EQUAL, PartState.DISABLEDFORDESIGN), new int[] { index0 });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(StringValue.class, StringValue.VALUE, SearchCondition.LIKE, ibaValue.toUpperCase()+"%"), new int[] { index1 });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(StringValue.class, StringValue.IBAHOLDER_REFERENCE + "." + WTAttributeNameIfc.REF_CLASSNAME, SearchCondition.EQUAL, WTPart.class.getName()), new int[] { index1 });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(StringDefinition.class, StringDefinition.NAME, SearchCondition.EQUAL, iba), new int[] { index2 });

			System.out.println("==QuerySQL:" + qs.toString());
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
			Persistable[] p = null;
			while (qr.hasMoreElements()) {
				p = (Persistable[]) qr.nextElement();
				ret.add((WTPart) p[0]);
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}

		System.out.println("Size is:\t" + ret.size());
		for(WTPart tmp:ret){
			System.out.println(tmp.getNumber());
		}
		return ret;
	}
	
	
	public static Map allStateMap() throws WTException{
		State[] states = LifeCycleHelper.service.allStates();
		Map<String,String> map = new HashMap<>();
		for(State state:states){
			map.put(state.getDisplay(Locale.CHINA), state.toString());
			System.out.println(state.getDisplay(Locale.CHINA)+"\t"+state.toString());
		}
		return map;
	}

	public static void createEPM() throws Exception {
		EPMContextHelper.setApplication(EPMApplicationType.toEPMApplicationType("EPM-ECAD"));
		EPMAuthoringAppType authApp = EPMAuthoringAppType.toEPMAuthoringAppType("ORCAD_SCH");
		System.out.println(authApp.getFullDisplay());
		EPMDocument epm = EPMDocument.newEPMDocument("000011", "EPM-SCH-Test", authApp,
				EPMDocumentType.toEPMDocumentType("ECAD-SCHEMATIC"), "000011");
		WTContainer container = (WTContainer) new ReferenceFactory().getReference("OR:wt.pdmlink.PDMLinkProduct:126060")
				.getObject();
		epm.setContainer(container);
		PersistenceHelper.manager.save(epm);

		ContentHolder contentHolder// ��ȡ�ı����ݵĶ���d
		= ContentHelper.service.getContents(epm);
		IBAUtility iba = new IBAUtility(epm);
		iba.setIBAValue("ECAD_DESIGN_ITEM", "TestSCH.DSN");
		iba.updateAttributeContainer(epm);
		iba.updateIBAHolder(epm);

		// ContentHolder
		ApplicationData appdata = ApplicationData.newApplicationData(contentHolder);
		appdata.setRole(ContentRoleType.MULTI_PRIMARY);// ������Ҫ���Ǵ�Ҫ�ļ�ͨ��ApplicationData
														// ȥ�������ĵ������ơ���С���Լ��ļ��ϴ�·����.
		appdata.setCategory("NATIVE_DESIGN");
		File file = new File("E:\\000010.zip");
		appdata.setFileName("{$CAD_NAME}.zip");
		appdata.setUploadedFromPath("ECAD_SET_DLD_ON_SYNCUP");
		appdata.setClientData("");
		FileInputStream is = new FileInputStream(file);

		/*
		 * ApplicationData appdata1 =
		 * ApplicationData.newApplicationData(contentHolder);
		 * appdata1.setRole(ContentRoleType.MULTI_PRIMARY);//������Ҫ���Ǵ�Ҫ�ļ�ͨ��
		 * ApplicationData ȥ�������ĵ������ơ���С���Լ��ļ��ϴ�·����.
		 * appdata1.setCategory("GENERAL"); File file1 = new
		 * File("E:\\000005.dld"); appdata1.setFileName("{$CAD_NAME}.dld");
		 * appdata1.setClientData(""); FileInputStream is1 = new
		 * FileInputStream(file1);
		 */

		ApplicationData appdata1 = ApplicationData.newApplicationData(contentHolder);
		appdata1.setRole(ContentRoleType.SECONDARY);// ������Ҫ���Ǵ�Ҫ�ļ�ͨ��ApplicationData
													// ȥ�������ĵ������ơ���С���Լ��ļ��ϴ�·����.
		appdata1.setCategory("GENERAL");
		File file1 = new File("E:\\Allegro-000010.zip");
		appdata1.setFileName(file1.getName());
		FileInputStream is1 = new FileInputStream(file1);

		
		/*ApplicationData appdata2 = ApplicationData.newApplicationData(contentHolder);
		appdata2.setRole(ContentRoleType.SECONDARY);
		appdata2.setCategory("GENERAL");
		File file2 = new File("E:\\thumbl.jpg");
		appdata2.setFileName(file2.getName());
		FileInputStream is2 = new FileInputStream(file2);*/
		
        
		
		Transaction tx = new Transaction();
		tx.start();
		ContentServerHelper.service.updateContent(contentHolder, appdata, is);// ���ĵ������ɹ�����fileStream��Ҫ�ϴ����ļ���������
		ContentServerHelper.service.updateContent(contentHolder, appdata1, is1);
		//ContentServerHelper.service.updateContent(contentHolder, appdata2, is2);
		createRep(epm);
		
		String s13 = "thumb.jpg";
        QueryResult qr = RepresentationHelper.service.getRepresentations(epm);
        while (qr.hasMoreElements()) {
            Representation representation = (Representation) qr.nextElement();
            System.out.println(representation);
            EDRHelper.addFile(representation, "E:\\", s13, s13, null, false,
                     true, false);
        }
		
		tx.commit();
		tx = null;
	}

	public static void createPCB() throws WTInvalidParameterException, WTException, WTPropertyVetoException {
		EPMContextHelper.setApplication(EPMApplicationType.toEPMApplicationType("EPM-ECAD"));
		EPMAuthoringAppType authApp = EPMAuthoringAppType.toEPMAuthoringAppType("CADENCE");
		System.out.println(authApp.getFullDisplay());
		EPMDocument epm = EPMDocument.newEPMDocument("000008", "EPM-PCB-Test", authApp,
				EPMDocumentType.toEPMDocumentType("ECAD-BOARD"), "000008");
		WTContainer container = (WTContainer) new ReferenceFactory().getReference("OR:wt.pdmlink.PDMLinkProduct:126060")
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
		boolean result = RepHelper.loadRepresentation(strPdfFileDir, strRepresentableOID, republishable, "TESTSCH",
				null, isDefaultRepresentation, createThumbnail, storeEDZ);

	}

	
	
	public static QueryResult getContentsByRole(ContentHolder arg0, ContentRoleType arg1, boolean arg2) throws WTException {
		
			NavigateSpec arg3 = PersistenceHelper.buildNavigateSpec(arg0, "theContentItem", HolderToContent.class,
					false);
			if (arg1 != null) {
				arg3.appendWhere(new SearchCondition(ContentItem.class, "role", "=", arg1), new int[] { 0 });
			}

			FromClause arg4 = arg3.getFromClause();
			int arg5 = arg4.getPosition(ContentItem.class);
			String arg6 = arg4.getAliasAt(arg5);
			String arg7 = DatabaseInfoUtilities.getPersistableColumnName(ContentItem.class,
					"thePersistInfo.theObjectIdentifier.id");
			TableColumn arg8 = new TableColumn(arg6, arg7);
			OrderBy arg9 = new OrderBy(arg8, false);
			arg3.appendOrderBy(arg9, new int[0]);
			arg3.setAdvancedQueryEnabled(true);
			System.out.println(arg3);
			QueryResult arg10 = PersistenceHelper.manager.find(arg3);

			ObjectVector arg11;
			ContentItem arg13;
			for (arg11 = new ObjectVector(new Vector()); arg10.hasMoreElements(); arg11.addElement(arg13)) {
				Object[] arg12 = (Object[]) ((Object[]) arg10.nextElement());
				arg13 = (ContentItem) arg12[0];
				HolderToContent arg14 = (HolderToContent) arg12[1];

				try {
					arg13.setHolderLink(arg14);
				} catch (PropertyVetoException arg16) {
					throw new ContentException(arg16);
				}
			}

			QueryResult arg17 = new QueryResult(arg11);
			return arg17;
		
	}
	
	public static void testPN() throws Exception{
		//PromotionNotice pn = (PromotionNotice) new ReferenceFactory().getReference("OR:wt.maturity.PromotionNotice:434038").getObject();
		//ECADPartUtils.createSCHOrPCB(pn);
		//EPMDocument schepm = CommonUtil.getEPMDocumentByNumber("000005");
		//getContentsByRole(schepm, ContentRoleType.MULTI_PRIMARY, false);
		//schepm = (EPMDocument) CommonUtil.checkoutObject(schepm);
		//ConfigTableUtil.createSynonym();
		//Ownership ownership = schepm.getOwnership();
		//System.out.println("wwwwwwww\t"+ownership);
		//System.out.println("OOOOOOOO\t"+OwnershipHelper.getOwner(schepm));
		//System.out.println(OwnershipHelper.isOwnedBy(schepm, OrganizationServicesHelper.manager.getPrincipal("wcadmin")));
		//EPMDocument pcbepm = CommonUtil.getEPMDocumentByNumber("000002");
		//QueryResult qr = PersistenceHelper.manager.find(EPMReferenceLink.class, pcbepm, EPMReferenceLink.REFERENCED_BY_ROLE, schepm.getMaster());
		//QueryResult qr1 = PersistenceHelper.manager.find(EPMReferenceLink.class, pcbepm, EPMReferenceLink.REFERENCES_ROLE, schepm.getMaster());
		//System.out.println("BY\t"+qr.size());
		//System.out.println("REFERENCES\t"+qr1.size());
		//Role role[] = Role.getRoleSet();
		//for (int i = 0; i < role.length; i++) {
			//System.out.println(role[i].getDisplay(Locale.CHINA)+"\t"+role[i].toString());
		//}
		//ArrayList list1 = TeamCCHelper.service.getUsedRoles(NmOid.newNmOid(SessionHelper.manager.getAdministrator().getPersistInfo().getObjectIdentifier()));
		//com.ptc.windchill.enterprise.team.commands.TeamCommands
		 /*String schepmoid = "OR:wt.part.WTPart:448008";
		WTPart part = (WTPart) new
				 ReferenceFactory().getReference(schepmoid).getObject();//CommonUtil.getLatestWTpartByNumber("560220-00003");
		WTPartMaster master = part.getMaster();
		QueryResult qr = VersionControlHelper.service.allIterationsOf(master);
		Versioned previousVersion =null;
		
		Versioned latestVersion = (Versioned) qr.nextElement(); // latestversion
		while(!part.equals(latestVersion)){
			latestVersion = (Versioned) qr.nextElement();
		}
		previousVersion = (Versioned) qr.nextElement();
		System.out.println("Enter is " +
                previousVersion.getMaster().getIdentity() + " "                    
                 + previousVersion.getVersionIdentifier().getValue() + "." + 
                 previousVersion.getIterationIdentifier().getValue());
		getPreviousVersion(part);*/
		//com.ptc.wvs.server.publish.CadConvertCATIAV5
		//PartReleaseToCadenceUtil.updateStateToCadence(part);
		//WTUser user = OrganizationServicesHelper.manager.getUser("catladmin");
		//EPMDocument epm = CommonUtil.getEPMDocumentByNumber("");
		//setCreator(part, user);
		
		//PartReleaseToCadenceUtil.sendPartToCadence(part);
		
		//CatlPropertyHelper.getDcaPropertyValue("物控与计划控制更改任务");
		//setParentSW_HW(part);
		//ObjectTypeUtil.getAllTypeDefinitionMap();
		//ProcessingQueue pq = QueueHelper.manager.getQueue("CatiaTransfer");
		//ScheduleQueue si= (ScheduleQueue) QueueHelper.manager.getQueue("TestScheduleItem",ScheduleQueue.class);
		//QueueHelper.manager.deleteEntries(arg0);
		//if(si != null){
		//	QueueHelper.manager.deleteQueue(si);
		//}
		WTPart part = CommonUtil.getLatestWTpartByNumber("CK-00000002");
		//getAllChilds(part);
		System.out.println(UpdateAllPartSpec.getAllAttr(part));
	}
	
	public static List<WTPart> getAllChilds(WTPart part) throws WTException{
		List<WTPart> childParts = new ArrayList<>();
		System.out.println("222222222222222222222222222");
		QueryResult qr = StructHelper.service.navigateUses(part);
		while(qr.hasMoreElements()){
			WTPartMaster master = (WTPartMaster) qr.nextElement();
			WTPart childPart = (WTPart) CommonUtil.getLatestVersionOf(master);
			
			getAllChilds(childPart);
			//System.out.println(childPart.getNumber());
		}
		System.out.println(part.getNumber());
		return null;
		
	}
	
	/**
     * 获取对象上一版本
     * 
     * @param currentVersion
     * @return
     * @throws WTException
     */
    public static Versioned getPreviousVersion(Versioned currentVersion) throws WTException {
      
            System.out.println("Enter into getPreviousVersion  Current object is " +
                                 currentVersion.getMaster().getIdentity() + " "                    
                     + currentVersion.getVersionIdentifier().getValue() + "." + 
                       currentVersion.getIterationIdentifier().getValue());
        
        Versioned previousVersion = null;
        try {
        	
            QueryResult allVersions = wt.vc.VersionControlHelper.service.allVersionsOf((Versioned) currentVersion);
            if (allVersions.size() <= 1)
                return null;
            Versioned latestVersion = (Versioned) allVersions.nextElement(); // latest
                                                                             // version
            previousVersion = (Versioned) allVersions.nextElement(); // previous
                                                                     // version
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
           System.out.println("Enter into getPreviousVersion  previous object is " +
                    previousVersion.getMaster().getIdentity() + " "                    
                     + previousVersion.getVersionIdentifier().getValue() + "." + 
                     previousVersion.getIterationIdentifier().getValue());
        
        
        
        
        return previousVersion;
    }
	
	public static void setParentSW_HW(WTPart part) throws WTException, WTPropertyVetoException, RemoteException{
		if(isSWPart(part)){
			QueryResult qr = StructHelper.service.navigateUsedBy(part.getMaster());
			System.out.println(qr.size());
		
			IBAUtility childiba = new IBAUtility(part);
			String swversion = childiba.getIBAValue("Software_Version");
			String hwversion = childiba.getIBAValue("Hardware_Version");
			while(qr.hasMoreElements()){
				WTPart partparent = (WTPart) qr.nextElement();
			
				IBAUtility parentiba = new IBAUtility(partparent);
				parentiba.setIBAValue("Software_Version", swversion);
				parentiba.setIBAValue("Hardware_Version", hwversion);
				parentiba.updateAttributeContainer(partparent);
				parentiba.updateIBAHolder(partparent);
			
			System.out.println(partparent.getNumber()+"\t"+partparent.getName());
			}
		}
	}
	
	public static boolean isSWPart(WTPart part) throws WTException{
		if(part != null){
			IBAUtility iba = new IBAUtility(part);
			String cls = iba.getIBAValue("cls");
			String swcls = getSWClsPropertyValue("SWCls");
			if(swcls.contains(cls)){
				return true;
			}
		}
		return false;
	}
	
	
	public static String getSWClsPropertyValue(String key){
        String value = null;
        try {
        	logger.debug("new key=========="+key);
        	String nameString = key;
        	logger.debug("name string ==="+nameString);
            Properties customProperties = new Properties();
			WTProperties wtproperties = WTProperties.getLocalProperties();
			String codebase = wtproperties.getProperty("wt.codebase.location");
            customProperties.load(new FileInputStream(codebase+"/config/custom/PartProperties.properties"));
            nameString = new String(nameString.getBytes("GBK"), "ISO-8859-1");
            value = customProperties.getProperty(nameString);
            if(value == null)
            	return "";

            value = new String(value.trim().getBytes("ISO-8859-1"), "GBK"); // 处理中文乱码
            logger.debug("value======="+value);
        } catch (IOException e) {
        	e.printStackTrace(); 
        } catch(Exception e) {
        	value = "";
        	e.printStackTrace();
        }

        return value;
    }
	
	/**
	 * 新加处理ECA的任务和描述联动
	 * @author zyw2
	 * @param key
	 * @return
	 */
	public static String getDcaPropertyValue(String key){
        String value = null;
        try {
        	System.out.println("new key=========="+key);
        	String nameString = changeDcakey(key);
        	System.out.println("name string ==="+nameString);
            Properties customProperties = new Properties();
			WTProperties wtproperties = WTProperties.getLocalProperties();
			String codebase = wtproperties.getProperty("wt.codebase.location");
            customProperties.load(new FileInputStream(codebase+"/config/custom/DCATaskProperties.properties"));
            value = (String) customProperties.getProperty(nameString).trim();

            value = new String(value.getBytes("ISO-8859-1"), "GBK"); // 处理中文乱码
            System.out.println("value======="+value);
        } catch (IOException e) {
        	e.printStackTrace(); 
        } catch(Exception e) {
        	e.printStackTrace();
        }

        return value;
    }
	
	public static String changeDcakey(String key)
	{
		//研发更改任务,物控与计划控制更改任务
		String name="";
		if (key==null) {
			name=null;
		}else {
			if (key.endsWith("DCATaskDes")) {
				name="DCATaskDes";
			}	
			if (key.endsWith("DCATask")) {
				name="DCATask";
			}
			if (key.equals("")) {
                name="99";
            }
			if (key.endsWith("项目管理更改任务")) {
				name="11";
			}
			if (key.endsWith("品质管理更改任务")) {
				name="12";
			}
			if (key.endsWith("来料检验更改任务")) {
				name="13";
			}
			if (key.endsWith("研发更改任务")) {
				name="14";
			}
			if (key.endsWith("测试更改任务")) {
				name="15";
			}
			if (key.endsWith("总装工艺开发更改任务")) {
				name="16";
			}
			if (key.endsWith("设备开发更改任务")) {
				name="17";
			}
			if (key.endsWith("工业规划更改任务")) {
				name="18";
			}
			if (key.endsWith("生产制造更改任务")) {
				name="19";
			}
			if (key.endsWith("物控与计划控制更改任务")) {
				name="20";
			}
			if (key.endsWith("采购更改任务")) {
				name="21";
			}
			if (key.endsWith("财务更改任务")) {
				name="22";
			}
			if (key.endsWith("市场更改任务")) {
				name="23";
			}
			if (key.endsWith("售后更改任务")) {
				name="24";
			}
			if (key.endsWith("物流更改任务")) {
				name="25";
			}
			if (key.endsWith("知会客户更改任务")) {
				name="26";
			}
		}
		return name;
	}
	
	public static void setCreator(WTPart epm,WTPrincipal prinl) throws NoSuchMethodException, SecurityException, WTException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		epm.getIterationInfo().getCreator().setObject(prinl);

		PersistenceServerHelper.manager.update(epm);
	}
	
	/**
	 * 获取需修改的元器件数据信息
	 * @return Map
	 * @throws IOException
	 */
	public static  Map<String, Map<String,String>> getPartInfoByXls(String path) throws IOException{
		Map<String, Map<String,String>> partMap=new HashMap<>();
		FileInputStream fis=new FileInputStream(new File(path));
		@SuppressWarnings("resource")
		XSSFWorkbook workbook=new XSSFWorkbook(fis);
		XSSFSheet sheet=workbook.getSheetAt(0);
		XSSFRow headRow = sheet.getRow(0);
		List<String> heads = new ArrayList<>();
		if(headRow != null){
			System.out.println(headRow.getPhysicalNumberOfCells());
			for(int r = 0; r< headRow.getPhysicalNumberOfCells(); r++){
				Cell cell = headRow.getCell(r);
				String value = LoadDataUtils.getCellValue(cell);
				if(StringUtils.isNotBlank(value)){
					System.out.println(value);
					heads.add(value);
				}else{
					break;
				}
			}
		}
		
		for(int i=1;;i++){
			String partNumber=null;
			Map<String, String> ibaMap=new HashMap<>();
			XSSFRow row=sheet.getRow(i);
			if(row==null){
				break;
			}
			for(int j=0;j<heads.size();j++){
				String value=null;
				XSSFCell cell=row.getCell(j);
				if(cell==null){
					continue;
				}else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
					DecimalFormat df = new DecimalFormat("0");
					value=df.format(cell.getNumericCellValue());
				}else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
					value=cell.getStringCellValue();
				}else if(cell.getCellType() == Cell.CELL_TYPE_FORMULA){
					value = String.valueOf(cell.getNumericCellValue());
				}
				
				if(StringUtils.isNotBlank(value)){
					if(j==0){
						System.out.println("***********************\nPartNumber"+"\t"+value);
						partNumber=value;
					}else{
						System.out.println(heads.get(j)+"\t"+value);
						ibaMap.put(heads.get(j), value);
					}
				}
				/*if(value!=null){
					switch (j) {
					case 0:
						partNumber=value;
						break;
					case 1:ibaMap.put("Assembly_Type", value);
						break;
					case 2:ibaMap.put("Manufacturer_Part_Number", value);
						break;
					case 3:ibaMap.put("Grade", value);
						break;
					case 4:ibaMap.put("Component_Footprint", value);
						break;
					case 5:ibaMap.put("Component_Value", value);
						break;
					}
				}*/
			}
			if(partNumber==null){
				break;
			}else{
				partMap.put(partNumber, ibaMap);
			}
		}
		return partMap;
	}
	
	
	
	public static void users() throws WTException{
		WTPrincipal principal = OrganizationServicesHelper.manager.getPrincipal(ECADConst.ECADGROUP);
		System.out.println(principal);
	}

	public static void getPartsByCLS() throws Exception{
		Map map = HistoryUtils.getClfNumber();
		List<String> pcba = (List) map.get("PCBA");
		List<String> pcb = (List) map.get("PCB");
		for(String str: pcba){
			System.out.println("PCBA\t"+str);
		}
		
		for(String str:pcb){
			System.out.println("PCB\t"+str);
		}
		QueryResult qr = NodeUtil.getAllPartsByCLFNodesName("300112");
		System.out.println(qr.size());
		while(qr.hasMoreElements()){
			Object obj = qr.nextElement();
			if (obj instanceof WTPart) {
				WTPart part = (WTPart) obj;
				if (ECADutil.isPCBA(part)) {
					String number = part.getNumber();
					EPMDocument epm = CommonUtil.getEPMDocumentByNumber(number);
					if(epm != null){
						continue;
					}
					String name = part.getName();
					WTContainer container = part.getContainer();
					Folder folder = FolderHelper.getFolder(part);
					String path = folder.getLocation() + ECADConst.ECADLOCATION;
					Folder ecadFolder = FolderHelper.service.getFolder(path, part.getContainerReference());
					epm = EPMUtil.createSchEPM(number, name, container, ecadFolder);
					State state = part.getLifeCycleState();
					LifeCycleHelper.service.setLifeCycleState(epm, state);
				} else if (ECADutil.isPCB(part)) {
					String number = part.getNumber();
					EPMDocument epm = CommonUtil.getEPMDocumentByNumber(number);
					if(epm != null){
						continue;
					}
					String name = part.getName();
					WTContainer container = part.getContainer();
					Folder folder = FolderHelper.getFolder(part);
					String path = folder.getLocation() + ECADConst.ECADLOCATION;
					Folder ecadFolder = FolderHelper.service.getFolder(path, part.getContainerReference());
					epm = EPMUtil.createPCB(number, name, container, ecadFolder);
					State state = part.getLifeCycleState();
					LifeCycleHelper.service.setLifeCycleState(epm, state);
				}
			}
		}
	}
	
	public static QueryResult findAssemblyDoc() throws WTException {
		QuerySpec qs = new QuerySpec();
		int docIndex = qs.appendClassList(WTDocument.class, true);
		int typeDefIndex = qs.appendClassList(WTTypeDefinition.class, false);
		int typeDefMasterIndex = qs.addClassList(WTTypeDefinitionMaster.class, false);
		
		SearchCondition sc = new SearchCondition(WTDocument.class, "typeDefinitionReference.key.id" ,WTTypeDefinition.class, WTAttributeNameIfc.ID_NAME);
		qs.appendWhere(sc, new int[] { docIndex, typeDefIndex });
		
		sc = new SearchCondition(WTTypeDefinition.class, "masterReference.key.id" ,WTTypeDefinitionMaster.class, WTAttributeNameIfc.ID_NAME);
		qs.appendAnd();
		qs.appendWhere(sc, new int[] { typeDefIndex, typeDefMasterIndex });
		
		//sc = new SearchCondition(WTTypeDefinitionMaster.class, WTTypeDefinitionMaster.DESCRIPTION_KEY, SearchCondition.EQUAL, "器件封装文件");
		sc = new SearchCondition(WTTypeDefinitionMaster.class, WTTypeDefinitionMaster.DISPLAY_NAME_KEY, SearchCondition.EQUAL, "com.ptc.xworks.TestDocument01");
		qs.appendAnd();
		qs.appendWhere(sc, new int[] { typeDefMasterIndex });
		
		sc = new SearchCondition(WTDocument.class, WTAttributeNameIfc.LATEST_ITERATION, SearchCondition.IS_TRUE);
		qs.appendAnd();
		qs.appendWhere(sc, new int[] { docIndex });
		
		//System.out.println("qs::" + qs);
		QueryResult qr = null, qr1 = null;
		qr = PersistenceHelper.manager.find(qs);
		ObjectVector ov = new ObjectVector();
		while (qr.hasMoreElements()) {
			Object obj[] = (Object[]) qr.nextElement();
			ov.addElement(obj[0]);
		}
		qr1 = new QueryResult(ov);
		ConfigSpec cs = new LatestConfigSpec();
		qr1 = cs.process(qr1);
		return qr1;
		//return PersistenceHelper.manager.find((StatementSpec)qs);
	}
	
	public static void downloadSch() throws Exception {
		EPMDocument schepm = (EPMDocument) new ReferenceFactory().getReference("VR:wt.epm.EPMDocument:278014")
				.getObject();
		EPMUtil.downloadSchOrPCB(schepm);
	}
	
	public static void testupdate() throws Exception{
		//updateSchOrPCB("0000000001");
		//EPMUtil.createRefLink();
		//System.out.println(ECADutil.getCheckoutListInfo(ECADConst.SCHTOOL));
		
		/*QueryResult qr = findAssemblyDoc();
		System.out.println(qr.size());
		while(qr.hasMoreElements()){
			WTDocument doc = (WTDocument) qr.nextElement();
			HistoryUtils.renumberDoc(doc, "Gerber-".toUpperCase());
		}*/
		
		//WTPart part = CommonUtil.getLatestWTpartByNumber("0000000141");
		//ECADutil.removePCBADrawingLinks(part);
		//HistoryUtils.removeLinkOldVersion(part);
	}

	public static void testsch() throws Exception {
		/*WTContainer container = (WTContainer) new ReferenceFactory().getReference("OR:wt.pdmlink.PDMLinkProduct:126060")
				.getObject();
		Folder folder = FolderHelper.service.getFolder("Default",
				(WTContainerRef) new ReferenceFactory().getReference("OR:wt.pdmlink.PDMLinkProduct:126060"));
		EPMDocument epm = EPMUtil.createSchEPM("000007", "TestSCH0628", container, folder);

		EPMUtil.updateSchContent(epm, "TestSCH.DSN");*/
		
		WTPart part = (WTPart) new ReferenceFactory().getReference("VR:wt.part.WTPart:278150").getObject();
		System.out.println(part.getLocation());
		Folder folder = FolderHelper.getFolder(part);
		
		System.out.println(folder.getLocation());
		String path = folder.getLocation()+"/设计图档/ECAD图档";
		System.out.println(path);
		Folder ecadFolder = FolderHelper.service.getFolder(path, part.getContainerReference());
		System.out.println(ecadFolder);
		
		//WTPart cpart = (WTPart) new ReferenceFactory().getReference("VR:wt.part.WTPart:306070").getObject();
		//ECADutil.removeLinks(part);
		// downloadFile("000006");
		// RepsAndMarkupsDateDataUtility
		// RepsAndMarkupsBuilder
		//VR%3Awt.part.WTPart%3A306014
		/*
		 * DerivedImage di = (DerivedImage) new ReferenceFactory().getReference(
		 * "OR:wt.viewmarkup.DerivedImage:224037").getObject(); EPMDocument
		 * schepm = (EPMDocument)new
		 * ReferenceFactory().getReference("VR:wt.epm.EPMDocument:224018").
		 * getObject();
		 * di.setDerivedFromReference(ObjectReference.newObjectReference(schepm)
		 * ); PersistenceHelper.manager.save(di);
		 */
		//LWCStructEnumAttTemplate clf = NodeUtil.getLWCStructEnumAttTemplateByPart(part);
		//System.out.println(clf.getName());
		
	}

	// {"username":"用户名","password":"密码", "isGenBOM":true,"isNoticeECAD":false,
	// "schPn":"81022-00001","schPath":"原理图路径","netTablePath":"网表文件路径","bomPath":"BOM文件路径","viewPath":"可视化文件路径
	// ","Attrs":{"Attr1":"图框属性1","Attr2":"图框属性2"}}
	public static void updateSchOrPCB(String jsonstr) throws Exception {
		jsonstr = "{'username':'用户名','password':'密码', 'isGenBOM':true,'isNoticeECAD':false, 'schPn':'0000000001','designItem':'TestSCH.DSN','schPath':'原理图路径','netTablePath':'网表文件路径','bomPath':'BOM文件路径','viewPath':'可视化文件路径 ','Attrs':{'Attr1':'图框属性1','Attr2':'图框属性2'}}";
		JSONObject json = new JSONObject(jsonstr);
		String number = json.getString("schPn");
		boolean isGenBOM = json.getBoolean("isGenBOM");
		boolean isNoticeECAD = json.getBoolean("isNoticeECAD");
		String designItem = json.getString("designItem");
		
		EPMDocument epm = EPMUtil.getEPMByNumber(number);
		
		if(epm != null){
			ApplicationData appdata = EPMUtil.getPrimaryAppdata(epm);
			if(appdata == null){
				//首次检入EPM可以不是检出状态
				EPMUtil.firstCheckin(number);
				if(AccessControlUtil.hasModifyPermission(epm)){
					epm = (EPMDocument) CommonUtil.checkoutObject(epm);
					EPMUtil.updateSchContent(epm, designItem,true);
					if(isGenBOM){
						BOMUtil.createBOM(number);
					}
					epm = (EPMDocument) CommonUtil.checkinObject(epm, "Update EPM Content.");
				}				
			}else{
				//非首次检入，判断EPM是否为检出状态
				if(WorkInProgressHelper.isCheckedOut(epm)){
					epm = (EPMDocument) CommonUtil.checkoutObject(epm);
					if(AccessControlUtil.hasModifyPermission(epm)){	
						Ownership ownership = epm.getOwnership();
						if(ownership == null){
							throw new WTException("Owner ship is null");
						}else{
							WTPrincipalReference prf = ownership.getOwner();
							if(SessionHelper.manager.getPrincipalReference().equals(prf)){
								EPMUtil.updateSchContent(epm, designItem,true);
								if(isGenBOM){
									BOMUtil.createBOM(number);
								}
								epm = (EPMDocument) CommonUtil.checkinObject(epm, "Update EPM Content.");
							}
						}					
					}
				}else{
					throw new WTException("请检出原理图后再上传。");
				}
				
			}
		}else{
			throw new WTException("请检出原理图在系统中不存在。");
		}

	}

	/**
	 * 创建可视化
	 * 
	 * @param epm
	 * @throws Exception
	 */
	public static void downloadFile(String number) throws Exception {
		// String schepmoid = "VR:wt.epm.EPMDocument:164007";
		// EPMDocument schepm = (EPMDocument)new
		// ReferenceFactory().getReference(schepmoid).getObject();
		String path = CommonUtil.getWTHome();
		StringBuffer buffer = new StringBuffer(path);
		buffer.append(File.separator);
		buffer.append("ecadTemp");
		File dir = new File(buffer.toString());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		buffer.append(File.separator);
		buffer.append(number + ".eda");
		FTPUitl.downloadFile("ecad", number + ".eda", buffer.toString());

	}

	public static void testAccess(WTObject obj) throws WTException, WTPropertyVetoException {
		String schepmoid = "VR:wt.epm.EPMDocument:172660";
		EPMDocument schepm = (EPMDocument) new ReferenceFactory().getReference(schepmoid).getObject();
		obj = schepm;
		SessionHelper.manager.setPrincipal("szeng");
		if (AccessControlHelper.manager.hasAccess(SessionHelper.getPrincipal(), obj, AccessPermission.MODIFY_CONTENT)) {
			System.out.println(SessionHelper.getPrincipal().getName() + "\t" + "Has Modify Content Permissions");
			schepm = (EPMDocument) CommonUtil.checkoutObject(schepm);

		} else {
			System.out.println(SessionHelper.getPrincipal().getName() + "\t" + "Has no Modify Content Permissions");
		}

		if (AccessControlHelper.manager.hasAccess(SessionHelper.getPrincipal(), obj, AccessPermission.READ)) {
			System.out.println(SessionHelper.getPrincipal().getName() + "\t" + "Has Read Permissions");
		} else {
			System.out.println(SessionHelper.getPrincipal().getName() + "\t" + "Has no Read Permissions");
		}
	}

	public static void findCheckoutlist() throws WTException, WTPropertyVetoException {
		ArrayList<EPMDocument> checkouts = ECADutil.findCheckoutEPM("orcad");
		System.out.println(checkouts.size());
		for (EPMDocument epm : checkouts) {
			System.out.println(epm.getNumber() + "\t" + epm.getName());
			if (WorkInProgressHelper.isWorkingCopy(epm)) {
				System.out.println("Is work copy");
			}
			if (AccessControlHelper.manager.hasAccess(SessionHelper.getPrincipal(), epm,
					AccessPermission.MODIFY_CONTENT)) {
				System.out.println(SessionHelper.getPrincipal().getName() + "\t" + "Has Permissions");
			}
			testAccess(epm);
		}
	}

	public static void startWF() throws WTException {
		WTPart part = (WTPart) new ReferenceFactory().getReference("VR:wt.part.WTPart:188065").getObject();
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
		EPMDocument schepm = (EPMDocument) new ReferenceFactory().getReference(schepmoid).getObject();
		QuerySpec queryspec = new QuerySpec(ContentItem.class,

		HolderToContent.class);
		queryspec.appendWhere(new SearchCondition

		(wt.content.ContentItem.class, "role", "=", ContentRoleType.MULTI_PRIMARY));
		QueryResult queryresult = PersistenceHelper.manager.navigate

		(schepm, "theContentItem", queryspec, false);
		ApplicationData theContent = null;
		while (queryresult.hasMoreElements()) {
			HolderToContent holdercontent = (HolderToContent)

			queryresult.nextElement();
			theContent = (ApplicationData) holdercontent.getContentItem();
			System.out.println(theContent.getFileName() + "\tClient" + theContent.getClientData() + "End\t"
					+ theContent.getUploadedFromPath());
		}
	}

	public static void createRefLink() {
		String schepmoid = "VR:wt.epm.EPMDocument:180452";
		String pcbepmoid = "VR:wt.epm.EPMDocument:172660";
		//String schepmoid1 = "VR:wt.epm.EPMDocument:164007";
		EPMDocument pcbepm = null;
		try {
			EPMContextHelper.setApplication(EPMApplicationType.toEPMApplicationType("EPM-ECAD"));
			EPMDocument schepm = (EPMDocument) new ReferenceFactory().getReference(schepmoid).getObject();
			pcbepm = (EPMDocument) new ReferenceFactory().getReference(pcbepmoid).getObject();
			//EPMDocument schepm1 = (EPMDocument) new ReferenceFactory().getReference(schepmoid1).getObject();
			pcbepm = (EPMDocument) CommonUtil.checkoutObject(pcbepm);
			WTCollection objects = new WTArrayList();
			EPMReferenceLink epmRefLink = EPMReferenceLink.newEPMReferenceLink(pcbepm,
					(EPMDocumentMaster) schepm.getMaster());
			//EPMReferenceLink epmRefLink1 = EPMReferenceLink.newEPMReferenceLink(pcbepm,
			//		(EPMDocumentMaster) schepm1.getMaster());

			epmRefLink.setReferenceType(EPMReferenceType.toEPMReferenceType("RELATION"));
			epmRefLink.setDepType(-3005);

			//epmRefLink1.setReferenceType(EPMReferenceType.toEPMReferenceType("RELATION"));
			//epmRefLink1.setDepType(-3005);

			objects.add(epmRefLink);
			//objects.add(epmRefLink1);
			// epmRefLink.setAsStoredChildName(arg0);
			// epmRefLink.setTypeDefinitionReference(TypeDefinitionReference.newTypeDefinitionReference());
			// PersistenceServerHelper.manager.insert(epmRefLink);
			PersistenceHelper.manager.save(objects);
			pcbepm = (EPMDocument) CommonUtil.checkinObject(pcbepm, "");
		} catch (WTInvalidParameterException | WTPropertyVetoException | WTException e) {
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
		String partoid = "VR:wt.part.WTPart:278150";
		String epmoid = "VR:wt.epm.EPMDocument:230116";
		// System.out.println(ModelItemSubType.toModelItemSubType("ECAD_ASSEMBLY"));
		// System.out.println(ModelItemType.toModelItemType("COMPONENT"));
		try {
			EPMDocument epm = (EPMDocument) new ReferenceFactory().getReference(epmoid).getObject();
			WTPart part = (WTPart) new ReferenceFactory().getReference(partoid).getObject();
			QueryResult qr = PersistenceHelper.manager.navigate(epm, EPMBuildRule.BUILD_TARGET_ROLE, EPMBuildRule.class,
					false);

			EPMBuildRule epmbuildRule = null;
			if (qr != null && qr.hasMoreElements()) {
				epmbuildRule = (EPMBuildRule) qr.nextElement();
				// Object obj = qr.nextElement();
				// System.out.println(obj);
				// return;
			} else {
				EPMBuildRule epmbr = EPMBuildRule.newEPMBuildRule(epm, part, 7);
				String sq = PersistenceHelper.manager.getNextSequence(EPMBuildRuleSequence.class);
				epmbr.setUniqueID(Long.parseLong(sq));
				PersistenceServerHelper.manager.insert(epmbr);
				// createLinkEpmToPart(epm, part);

				QueryResult qr1 = PersistenceHelper.manager.navigate(epm, EPMBuildRule.BUILD_TARGET_ROLE,
						EPMBuildRule.class, false);

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
	public static WfProcess startWorkFlow(String workFlowName, Object pbo, HashMap variables) throws WTException {
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
			WORKFLOW_PRIORITY = Long.parseLong(wtproperties.getProperty("wt.lifecycle.defaultWfProcessPriority", "1"));
			WfProcessDefinition wfprocessDefinition = WfDefinerHelper.service.getProcessDefinition(workFlowName,
					containerRef);
			if (wfprocessDefinition == null) {
				System.out.println("Error to getWrokFlowTemplate," + workFlowName + " is null");
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
				TeamTemplate tt = TeamHelper.service.getTeamTemplate(containerRef, teamTemplateName);

				if (tt != null) {
					TeamTemplateReference teamTemplateRef = TeamTemplateReference.newTeamTemplateReference(tt);
					team_spec = teamTemplateRef;
				}
			}

			WfProcess wfprocess = WfEngineHelper.service.createProcess(wfprocessDefinition, team_spec, containerRef);

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
			wfprocess = WfEngineHelper.service.startProcessImmediate(wfprocess, processData, WORKFLOW_PRIORITY);
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

	private static WfContainer saveProcess(WfContainer container) throws WTException {
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

	public static void setECADNumber() throws WTException, WTPropertyVetoException, RemoteException {
		String attrName = "PTC_ECAD_ASSEMBLY_PART_NAME";
		String attrNumber = "PTC_ECAD_ASSEMBLY_PART_NUMBER";

		String epmoid = "VR:wt.epm.EPMDocument:164007";
		EPMDocument epm = (EPMDocument) new ReferenceFactory().getReference(epmoid).getObject();

		IBAUtility iba = new IBAUtility(epm);
		iba.setIBAValue(attrNumber, "0000000001");
		iba.setIBAValue(attrName, "TestWF");
		iba.updateAttributeContainer(epm);
		iba.updateIBAHolder(epm);
	}


	public static void removeLinks(WTPart parent) throws WTException {
    	
        QueryResult partUsageLinks = StructHelper.service.navigateReferences(parent, WTPartReferenceLink.class,false);
        if (partUsageLinks != null && partUsageLinks.size() > 0) {
            WTHashSet removeUsageLinkSet = new WTHashSet(partUsageLinks);
            PersistenceServerHelper.manager.remove(removeUsageLinkSet);
        }
        PersistenceHelper.manager.refresh(parent);
        
    }
	
	//根据用户名或者全名查找用户
		public static QueryResult getUserByName() throws WTException {
			QuerySpec qs = new QuerySpec(WTGroup.class);
			SearchCondition sc = new SearchCondition(WTGroup.class, WTGroup.NAME, SearchCondition.EQUAL, ECADConst.ECADGROUP);
			qs.appendWhere(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			System.out.println(qr.size());
			while(qr.hasMoreElements()){
				System.out.println(qr.nextElement());
			}
			return qr;
		}
		
	public static void testWorkitem() throws WTException{
		EPMDocument epm = CommonUtil.getEPMDocumentByNumber("000007");
		WTPrincipal pcl = PersistedCollectionHelper.service.getCheckoutPrincipal((PersistedCollectionHolder) epm);
		System.out.println(pcl);
		QueryResult qrProcess = WfEngineHelper.service
				.getAssociatedProcesses(epm, null, null);
		System.out.println(epm);
		WfProcess process1 = null;
		if (qrProcess.hasMoreElements()) {
			WfProcess process = (WfProcess) qrProcess.nextElement();
			String state = process.getState().toString();
			if (state != null && !"".equals(state)) {
				if ("OPEN_RUNNING".equalsIgnoreCase(state)) {
					process1= process;
				}
			}
		}
		
		System.out.println(process1);
		QueryResult qr = WorkflowHelper.getWorkItems(process1, true);
		System.out.println(qr.size());
		while(qr.hasMoreElements()){
			WorkItem item = (WorkItem) qr.nextElement();
			System.out.println(item.getIdentity()+"\t"+item.getCompletedBy());
		}
	}
	
	public static void getFolderByName() throws WTException, WTPropertyVetoException{
		QuerySpec qs = new QuerySpec(SubFolder.class);
		SearchCondition sc = new SearchCondition(SubFolder.class, SubFolder.NAME, SearchCondition.EQUAL, "设计图档");
		qs.appendWhere(sc);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		System.out.println(qr.size());
		while(qr.hasMoreElements()){
			SubFolder parentfolder = (SubFolder) qr.nextElement();
			WTContainerRef containerRef = parentfolder.getContainerReference();
			System.out.println(parentfolder.getFolderPath());
			//String path = folder.getFolderPath()+"/"+"ECAD图档";
			//AdministrativeDomain add = new AdministrativeDomain();
			//FolderHelper.service.createSubFolder(path, add.getDomainRef());
			
			
			String folderPath =parentfolder.getFolderPath()+"/"+"ECAD图档";
			String domainPath = "/Default/ECAD图档";

			if (!folderPath.startsWith("/")) {
				System.out.println("***Warning*** Invalid folder path:" + folderPath);
				continue;
			}

			if (!folderPath.startsWith("/Default")) {
				folderPath = "/Default" + folderPath;
			}

			if (!"/".equals(folderPath) && folderPath.endsWith("/")) {
				folderPath = folderPath.substring(0, folderPath.length() - 1);
			}

			if ("/Default".equals(folderPath)) {
				Cabinet cabinet = FolderHelper.service.getCabinet("Default", containerRef);

				// 获得指定的domain
				AdministrativeDomain domain = AdministrativeDomainHelper.manager.getDomain(domainPath, containerRef);

				if (domain == null) {
					// 如果指定的domain不存在，则创建新的domain
					domain = createDomain(domainPath, containerRef);
					FolderHelper.service.updateCabinet(cabinet, cabinet.getName(), AdminDomainRef.newAdminDomainRef(domain));
				} else {
					// 如果指定的domain存在，则比较指定的domain和现有的domain是否相同
					AdminDomainRef currentDomainRef = cabinet.getDomainRef();
					if (!currentDomainRef.equals(AdminDomainRef.newAdminDomainRef(domain))) {
						// 将Cabinet的domain更新为指定的domain
						FolderHelper.service.updateCabinet(cabinet, cabinet.getName(), AdminDomainRef.newAdminDomainRef(domain));
					}
				}
			} else {
				if(isContinue(folderPath, containerRef)){
					continue;
				}
				SubFolder folder = null;
				try {
					folder = (SubFolder) FolderHelper.service.getFolder(folderPath, containerRef);
				} catch (FolderNotFoundException folderNotFound) {
					//folderNotFound.printStackTrace();
					folder = FolderHelper.service.createSubFolder(folderPath, containerRef);
				}
//				if (folder == null) {
//					System.out.println("***Warning*** Cannot get folder by folder path:" + folderPath + " in container " + containerRef);
//					continue;
//				}

				// 获得指定的domain
				AdministrativeDomain domain = AdministrativeDomainHelper.manager.getDomain(domainPath, containerRef);

				if (domain == null) {
					// 如果指定的domain不存在，则创建新的domain
					domain = createDomain(domainPath, containerRef);
					// 将Folder的domain更新为新创建的domain
					FolderHelper.service.updateSubFolder(folder, folder.getName(), AdminDomainRef.newAdminDomainRef(domain), false);
				} else {
					AdminDomainRef currentDomainRef = folder.getDomainRef();
					if (!currentDomainRef.equals(AdminDomainRef.newAdminDomainRef(domain))) {
						// 将Folder的domain更新为指定的domain
						FolderHelper.service.updateSubFolder(folder, folder.getName(), AdminDomainRef.newAdminDomainRef(domain), false);
					}
				}

			}
			
		}
		
		//return qr;
	}
	
	private static boolean isContinue(String folderPath, WTContainerRef containerRef) throws WTException{
		if (folderPath.lastIndexOf('/') > 0) {
			String parentFolderPath = folderPath.substring(0, folderPath.lastIndexOf('/'));
			if(!"/Default".equals(parentFolderPath)){
				SubFolder folder = null;
				try {
					folder = (SubFolder) FolderHelper.service.getFolder(parentFolderPath, containerRef);
				} catch (FolderNotFoundException folderNotFound) {
					folderNotFound.printStackTrace();
				}
				if (folder == null) {
					System.out.println("***Warning*** Cannot get folder by folder path:" + parentFolderPath + " in container " + containerRef);
					return true;
				}
			}
		}
		return false;
	}

	private static AdministrativeDomain createDomain(String domainPath, WTContainerRef containerRef) throws WTException, WTPropertyVetoException {
		AdministrativeDomain domain = AdministrativeDomainHelper.manager.getDomain(domainPath, containerRef);
		if (domain != null) {
			return domain;
		}

		// /Default/domainA/domainB/domainC
		if (domainPath.lastIndexOf('/') > 0) {
			String parentDomainPath = domainPath.substring(0, domainPath.lastIndexOf('/'));
			String domainName = domainPath.substring(domainPath.lastIndexOf('/') + 1);
			AdministrativeDomain parentDomain = AdministrativeDomainHelper.manager.getDomain(parentDomainPath, containerRef);
			if (parentDomain == null) {
				parentDomain = createDomain(parentDomainPath, containerRef);

			}
			AdminDomainRef newDomainRef = AdministrativeDomainHelper.manager.createDomain(AdminDomainRef.newAdminDomainRef(parentDomain), domainName, null,
					containerRef);
			return (AdministrativeDomain) newDomainRef.getObject();
		} else {
			throw new WTException("Cannot create domain:" + domainPath + " in container " + containerRef);
		}

	}

}
