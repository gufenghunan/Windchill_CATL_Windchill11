package com.catl.ecad.utils;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import com.ptc.wvs.server.loader.EDRHelper;
import com.ptc.wvs.server.ui.RepHelper;
import com.ptc.wvs.server.util.PublishUtils;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.content.FormatContentHolder;
import wt.content.HolderToContent;
import wt.content.Streamed;
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
import wt.fc.ObjectReference;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTCollection;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.fv.FvFileDoesNotExist;
import wt.inf.container.WTContainer;
import wt.method.RemoteAccess;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.ownership.Ownership;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.pom.PersistenceException;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.representation.Representation;
import wt.representation.RepresentationHelper;
import wt.representation.RepresentationType;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTInvalidParameterException;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;
import wt.vc.wip.WorkInProgressException;
import wt.vc.wip.WorkInProgressHelper;
import wt.viewmarkup.DerivationType;
import wt.viewmarkup.DerivedImage;

public class EPMUtil implements RemoteAccess {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/**
	 * 创建原理图EPM对象
	 * 
	 * @param number
	 * @param name
	 * @param container
	 * @param folder
	 * @throws Exception
	 */
	public static EPMDocument createSchEPM(String number, String name, WTContainer container, Folder folder)
			throws Exception {
		EPMContextHelper.setApplication(EPMApplicationType.toEPMApplicationType("EPM-ECAD"));
		EPMAuthoringAppType authApp = EPMAuthoringAppType.toEPMAuthoringAppType("ORCAD_SCH");
		System.out.println(authApp.getFullDisplay());
		EPMDocument epm = EPMDocument.newEPMDocument(number, name, authApp,
				EPMDocumentType.toEPMDocumentType("ECAD-SCHEMATIC"), number);
		// WTContainer container = (WTContainer) new
		// ReferenceFactory().getReference("OR:wt.pdmlink.PDMLinkProduct:126060").getObject();
		epm.setContainer(container);
		FolderHelper.assignFolder(epm, folder);
		epm = (EPMDocument) PersistenceHelper.manager.save(epm);

		WTPart part = CommonUtil.getLatestWTpartByNumber(number);

		// 关联PCBA与原理图
		if (epm != null && part != null) {
			WorkflowHelper.setTeamMember(epm, part.getCreator().getPrincipal());
			createLinkEpmToPart(epm, part, 6);
		}

		return epm;
	}

	/**
	 * 更新原理图内容
	 * 
	 * @param epm
	 *            原理图
	 * @param designItem
	 *            设计项名称
	 * @throws Exception
	 * @throws WTException
	 */
	public static void updateSchContent(EPMDocument epm, String designItem,boolean genVisual) throws Exception, WTException {
		Transaction tx = new Transaction();
		tx.start();
		ContentHolder contentHolder = ContentHelper.service.getContents(epm);
		IBAUtility iba = new IBAUtility(epm);
		iba.setIBAValue("ECAD_DESIGN_ITEM", designItem);
		iba.updateAttributeContainer(epm);
		iba.updateIBAHolder(epm);

		String number = epm.getNumber();

		// 更新主要内容
		ApplicationData appdata = getPrimaryAppdata(epm);
		if (appdata == null) {
			appdata = ApplicationData.newApplicationData(epm);
		}

		appdata.setRole(ContentRoleType.MULTI_PRIMARY);
		appdata.setCategory("NATIVE_DESIGN");
		// File file = new File("E:\\" + number + ".zip");
		appdata.setFileName("{$CAD_NAME}.zip");
		appdata.setUploadedFromPath("ECAD_SET_DLD_ON_SYNCUP");
		appdata.setClientData("");
		InputStream is = FTPUitl.getFileInputStream(FTPConfigProperties.getConfigFtpPath(), number + ".zip");// new
																												// FileInputStream(file);
		System.out.println("Primary InputStream\t" + is);
		if (is == null) {
			throw new WTException("原理图上传失败。");
		}
		// 移除所有附件(网表文件)
		epm = (EPMDocument) removeSecondaryContent(epm);

		// 创建在新版本上创建网表文件
		ApplicationData appdata1 = ApplicationData.newApplicationData(contentHolder);
		appdata1.setRole(ContentRoleType.SECONDARY);
		appdata1.setCategory("GENERAL");
		String allegroName = "Allegro-" + number + ".zip";
		// File file1 = new File("E:\\Allego-" + number + ".zip");
		appdata1.setFileName(allegroName);
		InputStream is1 = FTPUitl.getFileInputStream(FTPConfigProperties.getConfigFtpPath(), allegroName);// new
																											// FileInputStream(file1);
		System.out.println("Secondary InputStream\t" + is1);
		if (is1 == null) {
			throw new WTException("原理图相关的网表文件上传失败。");
		}
		ContentServerHelper.service.updateContent(contentHolder, appdata, is);
		ContentServerHelper.service.updateContent(contentHolder, appdata1, is1);
		if(genVisual){
			createRep(epm);
		}
		tx.commit();
		tx = null;
	}

	/**
	 * 创建PCB图
	 * 
	 * @param number
	 * @param name
	 * @param container
	 * @param folder
	 * @return
	 * @throws WTInvalidParameterException
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static EPMDocument createPCB(String number, String name, WTContainer container, Folder folder)
			throws WTInvalidParameterException, WTException, WTPropertyVetoException {
		EPMContextHelper.setApplication(EPMApplicationType.toEPMApplicationType("EPM-ECAD"));
		EPMAuthoringAppType authApp = EPMAuthoringAppType.toEPMAuthoringAppType("CADENCE");
		System.out.println(authApp.getFullDisplay());
		EPMDocument epm = EPMDocument.newEPMDocument(number, name, authApp,
				EPMDocumentType.toEPMDocumentType("ECAD-BOARD"), number);
		// WTContainer container = (WTContainer) new
		// ReferenceFactory().getReference("OR:wt.pdmlink.PDMLinkProduct:126060").getObject();
		epm.setContainer(container);
		FolderHelper.assignFolder(epm, folder);
		epm = (EPMDocument) PersistenceHelper.manager.save(epm);

		WTPart part = CommonUtil.getLatestWTpartByNumber(number);

		// 给PCB添加ECAD工程师组到团队成员
		WTPrincipal principal = ECADPartUtils.getECADGroup();// OrganizationServicesHelper.manager.getPrincipal(ECADConst.ECADGROUP);
		if (principal instanceof WTGroup) {
			if (epm != null && part != null) {
				WorkflowHelper.setTeamMember(epm, principal);
				WorkflowHelper.setTeamMember(part, principal);
				createLinkEpmToPart(epm, part, 7);
			}
		}

		return epm;

	}

	/**
	 * 更新原理图内容
	 * 
	 * @param epm
	 *            原理图
	 * @param designItem
	 *            设计项名称
	 * @throws Exception
	 * @throws WTException
	 */
	public static void updatePCBContent(EPMDocument epm, String designItem,boolean genVisual) throws Exception, WTException {
		// ContentHolder contentHolder = ContentHelper.service.getContents(epm);
		Transaction tx = new Transaction();
		tx.start();
		IBAUtility iba = new IBAUtility(epm);
		iba.setIBAValue("ECAD_DESIGN_ITEM", designItem);
		iba.updateAttributeContainer(epm);
		iba.updateIBAHolder(epm);

		String number = epm.getNumber();

		// ContentHolder
		ApplicationData appdata = getPrimaryAppdata(epm);
		if (appdata == null) {
			appdata = ApplicationData.newApplicationData(epm);
		}
		appdata.setRole(ContentRoleType.MULTI_PRIMARY);
		appdata.setCategory("NATIVE_DESIGN");
		// File file = new File("E:\\" + number + ".zip");
		appdata.setFileName("{$CAD_NAME}.zip");
		appdata.setUploadedFromPath("ECAD_SET_DLD_ON_SYNCUP");
		appdata.setClientData("");
		InputStream is = FTPUitl.getFileInputStream(FTPConfigProperties.getConfigFtpPath(), number + ".zip");// new
		if (is == null) {
			throw new WTException("PCB图上传失败。");
		} // FileInputStream(file);

		ContentServerHelper.service.updateContent(epm, appdata, is);
		if(genVisual){
			createRep(epm);
		}
		tx.commit();
		tx = null;
	}

	/**
	 * 获取原理图/PCB图主要内容
	 * 
	 * @param epm
	 * @return
	 * @throws WTException
	 * @throws PropertyVetoException
	 */
	public static ApplicationData getPrimaryAppdata(EPMDocument epm) throws WTException, PropertyVetoException {
		QuerySpec queryspec = new QuerySpec(ContentItem.class, HolderToContent.class);
		queryspec.appendWhere(new SearchCondition(ContentItem.class, "role", "=", ContentRoleType.MULTI_PRIMARY));
		QueryResult queryresult = PersistenceHelper.manager.navigate(epm, "theContentItem", queryspec, false);
		ApplicationData primary = null;
		while (queryresult.hasMoreElements()) {
			HolderToContent holdercontent = (HolderToContent) queryresult.nextElement();
			ApplicationData appdata = (ApplicationData) holdercontent.getContentItem();
			String filename = appdata.getFileName();
			if (filename != null && filename.endsWith(".zip")) {
				primary = (ApplicationData) holdercontent.getContentItem();
				return primary;
			}
		}
		return primary;
	}

	/**
	 * 移除所有附件
	 * 
	 * @param holder
	 * @return
	 * @throws WTException
	 * @throws PropertyVetoException
	 */
	public static FormatContentHolder removeSecondaryContent(FormatContentHolder holder)
			throws WTException, PropertyVetoException {
		QuerySpec scdQs = new QuerySpec(ContentItem.class, HolderToContent.class);
		scdQs.appendWhere(new SearchCondition(ContentItem.class, "role", "=", ContentRoleType.SECONDARY));
		QueryResult scdQr = PersistenceHelper.manager.navigate(holder, "theContentItem", scdQs, false);

		while (scdQr != null && scdQr.hasMoreElements()) {
			HolderToContent holdercontent = (HolderToContent) scdQr.nextElement();
			ApplicationData scdContent = (ApplicationData) holdercontent.getContentItem();
			ContentServerHelper.service.deleteContent(holder, scdContent);
			holder = ContentServerHelper.service.updateHolderFormat(holder);
		}

		return holder;
	}

	/**
	 * 创建可视化
	 * 
	 * @param epm
	 * @throws Exception
	 */
	public static void createRep(EPMDocument epm) throws Exception {
		String path = CommonUtil.getWTHome();
		String number = epm.getNumber();
		String thumbpath = path+ File.separator + "codebase" + File.separator + "com"
				+ File.separator + "catl" + File.separator + "ecad"
				+ File.separator + "utils";
		// String filepath = "";
		StringBuffer buffer = new StringBuffer(path);
		buffer.append(File.separator);
		buffer.append("ecadTemp");
		File dir = new File(buffer.toString());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		buffer.append(File.separator);

		buffer.append(number);

		File subdir = new File(buffer.toString());
		if (!subdir.exists()) {
			subdir.mkdirs();
		}
		buffer.append(File.separator);

		String pvppath = buffer.toString() + "wvspvp.pvp";
		path = buffer.append(number).toString();
		buffer.append(".eda");
		FTPUitl.downloadFile(FTPConfigProperties.getConfigFtpPath(), number + ".eda", buffer.toString());
		File edafile = new File(buffer.toString());

		String edaFileDir = edafile.getParent();

		boolean republishable = false;
		boolean isDefaultRepresentation = false;
		boolean createThumbnail = false;
		boolean storeEDZ = false;
		ReferenceFactory rf = new ReferenceFactory();
		String strRepresentableOID = rf.getReferenceString(epm);
		boolean result = RepHelper.loadRepresentation(edaFileDir, strRepresentableOID, republishable, "ECAD", null,
				isDefaultRepresentation, createThumbnail, storeEDZ);

		QueryResult dis = PublishUtils.getRepresentations(epm);
		while (dis.hasMoreElements()) {
			DerivedImage di = (DerivedImage) dis.nextElement();
			if (di.getName().equalsIgnoreCase("ECAD")) {
				di.setDerivedFromReference(ObjectReference.newObjectReference(epm));
				PersistenceHelper.manager.save(di);
			}
			
			String s13 = "thumb.jpg";	     
	        EDRHelper.addFile(di, thumbpath, s13, s13, null, false,true, false);	        
		}

		if (edafile.exists()) {
			edafile.delete();
		}

		File pvsfile = new File(path + "1.pvs");
		if (pvsfile.exists()) {
			pvsfile.delete();
		}

		File pvpfile = new File(pvppath);
		if (pvpfile.exists()) {
			pvpfile.delete();
		}

		if (subdir.exists()) {
			subdir.delete();
		}

	}

	/**
	 * 下载原理图/PCB到FTP
	 * 
	 * @param epm
	 * @throws Exception
	 */
	public static void downloadSchOrPCB(EPMDocument epm) throws Exception {
		ApplicationData appdata = getPrimaryAppdata(epm);
		if (appdata != null) {
			String filename = appdata.getFileName();
			// filename =
			// epm.getNumber()+VersionControlHelper.getVersionIdentifier(wtdoc).getValue()+"-"+filename;
			filename = epm.getNumber() + ".zip";
			System.out.println("文档主内容文件名：\t" + filename);
			InputStream inputstream = null;
			if (appdata != null) {
				try {
					Streamed sd = (Streamed) appdata.getStreamData().getObject();
					System.out.println(sd);
					inputstream = sd.retrieveStream();// ContentServerHelper.service.findContentStream(appdata);
					System.out.println(inputstream);
				} catch (FvFileDoesNotExist e) {
					System.out.println("系统电子仓库中找不到该文件：" + filename);
					inputstream = null;
				}
				// 发送文件内容到FTP
				if (inputstream != null) {
					FTPUitl.ftpUploadFile(FTPConfigProperties.getConfigFtpPath(), filename, inputstream);
				}
			}
		}
	}

	/**
	 * 获取最新版EPMDocument
	 * 
	 * @param docNumber
	 * @return
	 */
	public static EPMDocument getEPMByNumber(String docNumber) {
		EPMDocument doc = null;
		try {
			QuerySpec qs = new QuerySpec(EPMDocument.class);
			SearchCondition sc = new SearchCondition(EPMDocument.class, EPMDocument.NUMBER, SearchCondition.EQUAL,
					docNumber);
			qs.appendWhere(sc, new int[] { 0 });
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
			qr = new LatestConfigSpec().process(qr); // 过滤最新

			while (qr.hasMoreElements()) {
				doc = (EPMDocument) qr.nextElement();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}

	/**
	 * 检出原理图/PCB，并检验防呆规则
	 * 
	 * @param number
	 * @return
	 * @throws Exception
	 */
	public static String checkoutSchOrPCB(String number, String doCheckout) throws Exception {
		WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
		EPMDocument epm = getEPMByNumber(number);
		WTPart part = CommonUtil.getLatestWTpartByNumber(number);
		if (epm == null) {
			throw new WTException("编号为" + number + "的图纸在PLM系统中不存在");
		}

		if (part == null) {
			throw new WTException("编号为" + number + "的PN在PLM系统中不存在");
		}
		if ("true".equalsIgnoreCase(doCheckout)) {

		if (!AccessControlUtil.hasModifyPermission(epm)) {
			if (epm.getLifeCycleState().toString().equalsIgnoreCase(ECADConst.RELEASED_STATE)) {
				throw new WTException("图纸[" + number + "]是已发布状态，需要走变更流程后再修改检入系统！");
			} else if (!(epm.getLifeCycleState().toString().equalsIgnoreCase(ECADConst.DESIGN_STATE)
					|| epm.getLifeCycleState().toString().equalsIgnoreCase(ECADConst.DESIGNMODIFICATION_STATE))) {
				throw new WTException(
						"图纸[" + number + "]为[" + epm.getLifeCycleState().getDisplay(Locale.CHINA) + "]状态，不允许检出图纸。");
			}
			List<WTUser> users = WorkflowHelper.getRoleUsers(epm, ECADConst.DESIGNER);
			if (!users.contains(user)) {
				throw new WTException("您不是图纸" + number + "的团队成员，没有修改该图纸的权限。");
			}
		}

		if (!AccessControlUtil.hasModifyPermission(part)) {
			if (part.getLifeCycleState().toString().equalsIgnoreCase(ECADConst.RELEASED_STATE)) {
				throw new WTException("零部件[" + number + "]是已发布状态，需要走变更流程后再修改检入系统！");
			} else if (!(part.getLifeCycleState().toString().equalsIgnoreCase(ECADConst.DESIGN_STATE)
					|| part.getLifeCycleState().toString().equalsIgnoreCase(ECADConst.DESIGNMODIFICATION_STATE))) {
				throw new WTException(
						"零部件[" + number + "]为[" + part.getLifeCycleState().getDisplay(Locale.CHINA) + "]状态，不允许检出。");
			}
			List<WTUser> users = WorkflowHelper.getRoleUsers(part, ECADConst.DESIGNER);
			if (!users.contains(user)) {
				throw new WTException("您不是零部件" + number + "的团队成员，没有修改该PN的权限。");
			}
		}
		
			epm = (EPMDocument) CommonUtil.checkoutObject(epm);
		}
		downloadSchOrPCB(epm);

		return "success";
	}

	/**
	 * 原理图/PCB首次检入防呆检验
	 * 
	 * @param number
	 * @return
	 * @throws WTException
	 */
	public static boolean firstCheckin(String number) throws WTException {
		WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
		EPMDocument epm = getEPMByNumber(number);
		WTPart part = CommonUtil.getLatestWTpartByNumber(number);
		if (!AccessControlUtil.hasModifyPermission(epm)) {
			if (epm.getLifeCycleState().toString().equalsIgnoreCase(ECADConst.RELEASED_STATE)) {
				throw new WTException("图纸[" + number + "]是已发布状态，需要走变更流程后再修改检入系统！");
			} else if (!(epm.getLifeCycleState().toString().equalsIgnoreCase(ECADConst.DESIGN_STATE)
					|| epm.getLifeCycleState().toString().equalsIgnoreCase(ECADConst.DESIGNMODIFICATION_STATE))) {
				throw new WTException(
						"图纸[" + number + "]为[" + epm.getLifeCycleState().getDisplay(Locale.CHINA) + "]状态，不允许检出图纸。");
			}
			List<WTUser> users = WorkflowHelper.getRoleUsers(epm, ECADConst.DESIGNER);
			if (!users.contains(user)) {
				throw new WTException("您不是图纸" + number + "的团队成员，没有修改该图纸的权限。");
			}
		}

		if (!AccessControlUtil.hasModifyPermission(part)) {
			if (part.getLifeCycleState().toString().equalsIgnoreCase(ECADConst.RELEASED_STATE)) {
				throw new WTException("零部件[" + number + "]是已发布状态，需要走变更流程后再修改检入系统！");
			} else if (!(part.getLifeCycleState().toString().equalsIgnoreCase(ECADConst.DESIGN_STATE)
					|| part.getLifeCycleState().toString().equalsIgnoreCase(ECADConst.DESIGNMODIFICATION_STATE))) {
				throw new WTException(
						"零部件[" + number + "]为[" + epm.getLifeCycleState().getDisplay(Locale.CHINA) + "]状态，不允许检出。");
			}
			List<WTUser> users = WorkflowHelper.getRoleUsers(part, ECADConst.DESIGNER);
			if (!users.contains(user)) {
				throw new WTException("您不是零部件" + number + "的团队成员，没有修改该PN的权限。");
			}
		}

		return true;
	}

	/**
	 * 创建EPM之间的参考关系
	 * @throws WTException 
	 * @throws PersistenceException 
	 * @throws WTPropertyVetoException 
	 * @throws WorkInProgressException 
	 */
	public static void createRefLink(EPMDocument schepm, EPMDocument pcbepm) throws WorkInProgressException, WTPropertyVetoException, PersistenceException, WTException {
		boolean enfored = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			EPMContextHelper.setApplication(EPMApplicationType.toEPMApplicationType("EPM-ECAD"));
			QueryResult qr = PersistenceHelper.manager.find(EPMReferenceLink.class, pcbepm, EPMReferenceLink.REFERENCED_BY_ROLE, schepm.getMaster());
			if(qr.hasMoreElements()){
				return ;
			}
			pcbepm = (EPMDocument) CommonUtil.checkoutObject(pcbepm);
			WTCollection objects = new WTArrayList();
			EPMReferenceLink epmRefLink = EPMReferenceLink.newEPMReferenceLink(pcbepm,
					(EPMDocumentMaster) schepm.getMaster());
			epmRefLink.setReferenceType(EPMReferenceType.toEPMReferenceType("RELATION"));
			epmRefLink.setDepType(-3005);

			objects.add(epmRefLink);

			PersistenceHelper.manager.save(objects);
			pcbepm = (EPMDocument) CommonUtil.checkinObject(pcbepm, "");
		} catch (WTInvalidParameterException | WTPropertyVetoException | WTException e) {
			
			WorkInProgressHelper.service.undoCheckout(pcbepm);
			throw new WTException("建立原理图与PCB图参考关系时出错，请检查是否有权限修改PCB图！");
		}finally{
			SessionServerHelper.manager.setAccessEnforced(enfored);
		}

	}

	/**
	 * 创建部件与EPM的关联关系
	 * 
	 * @param epm
	 * @param part
	 * @param buildtype
	 *            7 所有者
	 * @throws WTException
	 */
	public static void createLinkEpmToPart(EPMDocument epm, WTPart part, int buildtype) throws WTException {
		// String partoid = "VR:wt.part.WTPart:338475";
		// String epmoid = "VR:wt.epm.EPMDocument:164007";
		// System.out.println(ModelItemSubType.toModelItemSubType("ECAD_ASSEMBLY"));
		// System.out.println(ModelItemType.toModelItemType("COMPONENT"));
		try {
			// EPMDocument epm = (EPMDocument) new
			// ReferenceFactory().getReference(epmoid).getObject();
			// WTPart part = (WTPart) new
			// ReferenceFactory().getReference(partoid).getObject();
			QueryResult qr = PersistenceHelper.manager.navigate(epm, EPMBuildRule.BUILD_TARGET_ROLE, EPMBuildRule.class,
					false);

			EPMBuildRule epmbuildRule = null;
			if (qr != null && qr.hasMoreElements()) {
				epmbuildRule = (EPMBuildRule) qr.nextElement();
				// Object obj = qr.nextElement();
				// System.out.println(obj);
				// return;
			} else {
				EPMBuildRule epmbr = EPMBuildRule.newEPMBuildRule(epm, part, buildtype);
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
	 * 更新原理图或PCB图内容
	 * @param jsonstr
	 * @return
	 * @throws Exception
	 */
	public static String updateSchOrPCB(String jsonstr) throws Exception {
		// jsonstr = "{'username':'用户名','password':'密码',
		// 'isGenBOM':true,'isNoticeECAD':false,
		// 'schPn':'0000000001','designItem':'TestSCH.DSN','schPath':'原理图路径','netTablePath':'网表文件路径','bomPath':'BOM文件路径','viewPath':'可视化文件路径
		// ','Attrs':{'Attr1':'图框属性1','Attr2':'图框属性2'}}";
		JSONObject json = new JSONObject(jsonstr);
		String number = json.getString("schPn");
		String softname = json.getString("softName");
		String designItem = json.getString("designItem");
		boolean genVisual = false;
		try{
			genVisual = json.getBoolean("isGenVisual");
		}catch(Exception e){
			genVisual = false;
		}

		EPMDocument epm = getEPMByNumber(number);

		if (epm != null) {
			ApplicationData appdata = getPrimaryAppdata(epm);
			if (appdata == null) {
				/*if (WorkInProgressHelper.isCheckedOut(epm)) {
					epm = (EPMDocument) CommonUtil.checkoutObject(epm);
					if (AccessControlUtil.hasModifyPermission(epm)) {
						Ownership ownership = epm.getOwnership();
						if (ownership == null) {
							throw new WTException("Owner ship is null");
						} else {
							WTPrincipalReference prf = ownership.getOwner();
							if (!SessionHelper.manager.getPrincipalReference().equals(prf)) {
								throw new WTException("图纸" + number + "已被用户"+prf.getFullName()+"检出。");
							}
						}
					}
				}*/
				// 首次检入EPM可以不是检出状态
				firstCheckin(number);
				if (AccessControlUtil.hasModifyPermission(epm)) {
					epm = (EPMDocument) CommonUtil.checkoutObject(epm);
					if (ECADConst.SCHTOOL.equalsIgnoreCase(softname)) {
						boolean isGenBOM = json.getBoolean("isGenBOM");
						boolean isNoticeECAD = json.getBoolean("isNoticeECAD");

						JSONObject attrs = json.getJSONObject("Attrs");
						String attr1 = attrs.getString("CATL_BusinessVersion");
						String attr2 = attrs.getString("CATL_PlatformName");
						
						if(StringUtils.isBlank(attr1)){
							throw new WTException("原理图属性【业务版本】为空");
						}
						
						if(StringUtils.isBlank(attr2)){
							throw new WTException("原理图属性【平台名称】为空");
						}

						Map<String, String> attrmap = new HashMap<>();// IBA属性
						attrmap.put("CATL_BusinessVersion", attr1);
						attrmap.put("CATL_PlatformName", attr2);

						//HashMap<String, String> hashMap = new HashMap<String, String>(); // 流程变量

						

						EPMUtil.updateSchContent(epm, designItem,genVisual);
						if (isGenBOM) {
							BOMUtil.createBOM(number);
						}

						epm = (EPMDocument) CommonUtil.checkinObject(epm, "Update EPM Content.");
						setAttrs(epm, attrmap);
						if (isNoticeECAD) {
							JSONObject variables = json.getJSONObject("Variables");
/*							String startDate = variables.getString("startDate");
							String endDate = variables.getString("endDate");
							String comment = variables.getString("comment");*/

							/*hashMap.put("startDate", startDate);
							hashMap.put("endDate", endDate);
							hashMap.put("comment", comment);*/
							
							
							System.out.println("原理图更新邮件通知");
							/*if (!WorkflowHelper.hasProcessRunning(epm)) {
								WorkflowHelper.startWorkFlow(ECADConst.PCBDESIGN_WF, epm, hashMap);
							}*/
							List<WTUser> users = new ArrayList<WTUser>();
							WTGroup group = ECADPartUtils.getECADGroup();
							WorkflowHelper.getGroupMemberUsers(group, users);
							MailUtil.sendMail_SCH(epm, users, variables);
						}
					} else if (ECADConst.PCBTOOL.equalsIgnoreCase(softname)) {
						EPMUtil.updatePCBContent(epm, designItem,genVisual);
						epm = (EPMDocument) CommonUtil.checkinObject(epm, "Update EPM Content.");
					} else {
						throw new WTException("无法识别的设计工具，请联系管理员！");
					}

				}
			} else {
				// 非首次检入，判断EPM是否为检出状态
				if (WorkInProgressHelper.isCheckedOut(epm)) {
					epm = (EPMDocument) CommonUtil.checkoutObject(epm);
					if (AccessControlUtil.hasModifyPermission(epm)) {
						Ownership ownership = epm.getOwnership();
						if (ownership == null) {
							throw new WTException("Owner ship is null");
						} else {
							WTPrincipalReference prf = ownership.getOwner();
							if (SessionHelper.manager.getPrincipalReference().equals(prf)) {

								if (ECADConst.SCHTOOL.equalsIgnoreCase(softname)) {
									boolean isGenBOM = json.getBoolean("isGenBOM");
									boolean isNoticeECAD = json.getBoolean("isNoticeECAD");

									
									JSONObject attrs = json.getJSONObject("Attrs");
									String attr1 = attrs.getString("CATL_BusinessVersion");
									String attr2 = attrs.getString("CATL_PlatformName");

									if(StringUtils.isBlank(attr1)){
										throw new WTException("原理图属性【业务版本】为空");
									}
									
									if(StringUtils.isBlank(attr2)){
										throw new WTException("原理图属性【平台名称】为空");
									}
									
									Map<String, String> attrmap = new HashMap<>();// IBA属性
									attrmap.put("CATL_BusinessVersion", attr1);
									attrmap.put("CATL_PlatformName", attr2);

									//HashMap<String, String> hashMap = new HashMap<String, String>(); // 流程变量


									/*hashMap.put("startDate", startDate);
									hashMap.put("endDate", endDate);
									hashMap.put("comment", comment);*/
									

									EPMUtil.updateSchContent(epm, designItem,genVisual);
									if (isGenBOM) {
										BOMUtil.createBOM(number);
									}
									epm = (EPMDocument) CommonUtil.checkinObject(epm, "Update EPM Content.");
									setAttrs(epm, attrmap);

									if (isNoticeECAD) {
										/*System.out.println("发启PCB设计流程");
										if (!WorkflowHelper.hasProcessRunning(epm)) {
											WorkflowHelper.startWorkFlow(ECADConst.PCBDESIGN_WF, epm, hashMap);
										}*/
										
										JSONObject variables = json.getJSONObject("Variables");

										/*hashMap.put("startDate", startDate);
										hashMap.put("endDate", endDate);
										hashMap.put("comment", comment);*/										
										
										List<WTUser> users = new ArrayList<WTUser>();
										WTGroup group = ECADPartUtils.getECADGroup();
										WorkflowHelper.getGroupMemberUsers(group, users);
										MailUtil.sendMail_SCH(epm, users, variables);
									}
								} else if (ECADConst.PCBTOOL.equalsIgnoreCase(softname)) {
									EPMUtil.updatePCBContent(epm, designItem,genVisual);
									epm = (EPMDocument) CommonUtil.checkinObject(epm, "Update EPM Content.");
								} else {
									throw new WTException("无法识别的设计工具，请联系管理员！");
								}
							}
						}
					}
				} else {
					throw new WTException("请检出图纸" + number + "后再上传。");
				}

			}
		} else {
			throw new WTException("图纸" + number + "在系统中不存在。");
		}
		return "success";
	}

	/**
	 * 设置原理图与PCBA的IBA属性
	 * 
	 * @param epm
	 * @param part
	 * @param attrs
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws RemoteException
	 */
	public static void setAttrs(EPMDocument epm, Map attrs)
			throws WTException, WTPropertyVetoException, RemoteException {
		IBAUtility ibaepm = new IBAUtility(epm);

		Set keys = attrs.keySet();
		if (keys.size() > 0) {
			for (Object obj : keys) {
				ibaepm.setIBAValue((String) obj, (String) attrs.get(obj));
			}
			ibaepm.updateAttributeContainer(epm);
			ibaepm.updateIBAHolder(epm);
		}

	}

}
