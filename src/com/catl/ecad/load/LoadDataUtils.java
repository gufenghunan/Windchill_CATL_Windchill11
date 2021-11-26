package com.catl.ecad.load;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.catl.ecad.bean.CadenceAttributeBean;
import com.catl.ecad.dbs.CadenceXmlObjectUtil;
import com.catl.ecad.utils.CommonUtil;
import com.catl.ecad.utils.ECADConst;
import com.catl.ecad.utils.ECADutil;
import com.catl.ecad.utils.EPMUtil;
import com.catl.ecad.utils.HistoryUtils;
import com.catl.ecad.utils.IBAUtility;
import com.catl.cadence.conf.CadenceConfConstant;
import com.catl.cadence.util.NodeUtil;
import com.catl.cadence.util.PartReleaseToCadenceUtil;
import com.ptc.core.meta.type.mgmt.server.impl.WTTypeDefinition;
import com.ptc.core.meta.type.mgmt.server.impl.WTTypeDefinitionMaster;
import com.ptc.xworks.util.XWorksHelper;
import com.ptc.xworks.xmlobject.BaseXmlObjectLink;
import com.ptc.xworks.xmlobject.BaseXmlObjectRef;
import com.ptc.xworks.xmlobject.store.XmlObjectStoreManager;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.doc.WTDocumentMasterIdentity;
import wt.epm.EPMDocument;
import wt.fc.IdentityHelper;
import wt.fc.ObjectReference;
import wt.fc.ObjectVector;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.folder.FolderNotFoundException;
import wt.folder.SubFolder;
import wt.inf.container.WTContainer;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.State;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.pds.StatementSpec;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.config.ConfigSpec;
import wt.vc.config.LatestConfigSpec;
import wt.vc.wip.WorkInProgressHelper;

public class LoadDataUtils implements RemoteAccess {

	public static void main(String[] args) throws Exception {
		Scanner sc = new Scanner(System.in);
		System.out.println("1、请输入用户名:");
		String username = sc.nextLine();
		System.out.println("2、请输入密码:");
		String password = sc.nextLine();
		System.out
				.println("\n-----------菜单-------------\n\n\t0.退出\n\t1.修改PCBA装配图文档编号\n\t2.修改GERBER文件文档编号\n\t3.为系统中所有PCBA、PCB创建图档对象\n\t4.上传原理图PCB图\n\t5.更改PCBA装配图关联关系\n\t6.批量导入原理图属性\n\t7.推送PCB及其他元器件到Cadence库\n\t8.推送电子元器件到Cadence库\n\t9.推送电气元器件到Cadence库\n\t10.修改电子元器件属性值\n\t11.修改原理图PCB图创建者"
						+ "\n\n-----------注意-------------\n\t1:执行1、2、5项时请确认已调整Gerber文件、PCBA转配图的设计禁用状态，程序将忽略此状态文档！"
						+ "\n\t2:执行1项时请先执行5项，否则无法更改PCBA装配图文档编号(PCBA转配图若无关联PCB部件，程序将忽略此文档)！"
						+ "\n----------------------------");
		System.out.print("请选择：");
		//Scanner sc = new Scanner(System.in);
		int i = sc.nextInt();
		while (i > 0) {
			if (i == 1) {
				// 修改PCBA装配图文档编号
				renameAssembly(username,password);
			} else if (i == 2) {
				// 修改GERBER文件文档编号
				renameGerber(username,password);
			} else if (i == 3) {
				// 为系统中所有PCBA、PCB创建图档对象
				createAllSCHPCB(username,password);
			} else if (i == 4) {
				// 上传原理图PCB图
				loadSCHOrPCBs(username,password);
			} else if (i == 5) {
				// 更改PCBA装配图关联关系
				movePCBAToPCB(username,password);
			} else if (i == 6) {
				// 批量导入原理图属性
				loadSCHAttrs(username,password);
			} else if (i == 7) {
				// 批量推送PCB及其他元器件到Cadence
				loadPCBToCadence(username,password);
			} else if (i == 8) {
				// 批量推送电子元器件到Cadence
				loadEleToCadence(username,password);
			} else if (i == 9) {
				// 批量推送电气元器件到Cadence
				loadEletricalToCadence(username,password);
			} else if (i == 10) {
				setPartInfo(username,password);
			} else if (i == 11) {
				updateAllSCHPCB(username,password);
			}

			System.out
					.println("\n-----------菜单-------------\n\n\t0.退出\n\t1.修改PCBA装配图文档编号\n\t2.修改GERBER文件文档编号\n\t3.为系统中所有PCBA、PCB创建图档对象\n\t4.上传原理图PCB图\n\t5.更改PCBA装配图关联关系\n\t6.批量导入原理图属性\n\t7.推送PCB及其他元器件到Cadence库\n\t8.推送电子元器件到Cadence库\n\t9.推送电气元器件到Cadence库\n\t10.修改电子元器件属性值\n\t11.修改原理图PCB图创建者"
							+ "\n\n-----------注意-------------\n\t1:执行1、2、5项时请确认已调整Gerber文件、PCBA转配图的设计禁用状态，程序将忽略此状态文档！"
							+ "\n\t2:执行1项时请先执行5项，否则无法更改PCBA装配图文档编号(PCBA转配图若无关联PCB部件，程序将忽略此文档)！"
							+ "\n----------------------------");
			System.out.print("请选择：");
			Scanner sc1 = new Scanner(System.in);
			i = sc1.nextInt();
		}

	}

	/**
	 * 批量上传原理图或PCB图
	 * 
	 * @throws RemoteException
	 * @throws InvocationTargetException
	 * @throws WTException
	 */
	public static void loadSCHOrPCBs(String username, String password) throws RemoteException,
			InvocationTargetException, WTException {
		RemoteMethodServer rm = RemoteMethodServer.getDefault();
		rm.setUserName(username);
		rm.setPassword(password);
		SessionHelper.manager.setAuthenticatedPrincipal("wcadmin");

		Scanner sc = new Scanner(System.in);
		System.out.println("1、请输入文件夹路径:");
		String path = sc.nextLine();
		System.out.println("2、请输入类型（orcad 或 pcb）:");
		String type = sc.nextLine();

		Class[] clazz = { String.class, String.class };
		Object[] args = { path, type };
		rm.invoke("loadSCHOrPCB", LoadDataUtils.class.getName(), null, clazz,
				args);
	}

	/**
	 * 批量刷原理图IBA属性 平台名称、业务版本
	 * @param password 
	 * @param username 
	 * 
	 * @throws RemoteException
	 * @throws InvocationTargetException
	 * @throws WTException
	 */
	public static void loadSCHAttrs(String username, String password) throws RemoteException,
			InvocationTargetException, WTException {
		RemoteMethodServer rm = RemoteMethodServer.getDefault();
		rm.setUserName(username);
		rm.setPassword(password);
		SessionHelper.manager.setAuthenticatedPrincipal("wcadmin");

		Scanner sc = new Scanner(System.in);
		System.out.println("1、请输入原理图属性表文件路径:");
		String path = sc.nextLine();

		Class[] clazz = { String.class };
		Object[] args = { path };
		rm.invoke("loadSCHAttrs", LoadDataUtils.class.getName(), null, clazz,
				args);
	}

	/**
	 * 发布PCB及其他元器件到Cadence库
	 * @param password 
	 * @param username 
	 * 
	 * @throws RemoteException
	 * @throws InvocationTargetException
	 * @throws WTException
	 */
	public static void loadPCBToCadence(String username, String password) throws RemoteException,
			InvocationTargetException, WTException {
		RemoteMethodServer rm = RemoteMethodServer.getDefault();
		rm.setUserName(username);
		rm.setPassword(password);
		SessionHelper.manager.setAuthenticatedPrincipal("wcadmin");

		Scanner sc = new Scanner(System.in);
		System.out.println("1、请输入PCB元器件表文件路径:");
		String path = sc.nextLine();

		Class[] clazz = { String.class };
		Object[] args = { path };
		rm.invoke("loadPCBToCadence", LoadDataUtils.class.getName(), null,
				clazz, args);
	}

	/**
	 * 发布电子元器件到Cadence库
	 * @param password 
	 * @param username 
	 * 
	 * @throws RemoteException
	 * @throws InvocationTargetException
	 * @throws WTException
	 */
	public static void loadEleToCadence(String username, String password) throws RemoteException,
			InvocationTargetException, WTException {
		RemoteMethodServer rm = RemoteMethodServer.getDefault();
		rm.setUserName(username);
		rm.setPassword(password);
		SessionHelper.manager.setAuthenticatedPrincipal("wcadmin");

		Scanner sc = new Scanner(System.in);
		System.out.println("1、请输入电子元器件表文件路径:");
		String path = sc.nextLine();

		Class[] clazz = { String.class };
		Object[] args = { path };
		rm.invoke("loadEleToCadence", LoadDataUtils.class.getName(), null,
				clazz, args);
	}

	/**
	 * 发布电气元器件到Cadence库
	 * @param password 
	 * @param username 
	 * 
	 * @throws RemoteException
	 * @throws InvocationTargetException
	 * @throws WTException
	 */
	public static void loadEletricalToCadence(String username, String password) throws RemoteException,
			InvocationTargetException, WTException {
		RemoteMethodServer rm = RemoteMethodServer.getDefault();
		rm.setUserName(username);
		rm.setPassword(password);
		SessionHelper.manager.setAuthenticatedPrincipal("wcadmin");

		Scanner sc = new Scanner(System.in);
		System.out.println("1、请输入电气元器件表文件路径:");
		String path = sc.nextLine();

		Class[] clazz = { String.class };
		Object[] args = { path };
		rm.invoke("loadEletricalToCadence", LoadDataUtils.class.getName(),
				null, clazz, args);
	}

	public static void setPartInfo(String username, String password) throws WTException, RemoteException, InvocationTargetException {
		RemoteMethodServer rm = RemoteMethodServer.getDefault();
		rm.setUserName(username);
		rm.setPassword(password);
		SessionHelper.manager.setAuthenticatedPrincipal("wcadmin");

		Scanner sc = new Scanner(System.in);
		System.out.println("1、请输入电气元器件表文件路径:");
		String path = sc.nextLine();

		Class[] clazz = { String.class };
		Object[] args = { path };
		rm.invoke("setIBAValueForByPart", HistoryUtils.class.getName(),
				null, clazz, args);
	}

	/**
	 * 重新给历史PCBA装配图编码
	 * @param password 
	 * @param username 
	 * 
	 * @throws RemoteException
	 * @throws InvocationTargetException
	 * @throws WTException
	 */
	public static void renameAssembly(String username, String password) throws RemoteException,
			InvocationTargetException, WTException {
		RemoteMethodServer rm = RemoteMethodServer.getDefault();
		rm.setUserName(username);
		rm.setPassword(password);
		SessionHelper.manager.setAuthenticatedPrincipal("wcadmin");
		rm.invoke("renameAssemblyDocs", LoadDataUtils.class.getName(), null,
				null, null);
	}

	/**
	 * 重新给历史GERBER文件编码
	 * @param password 
	 * @param username 
	 * 
	 * @throws RemoteException
	 * @throws InvocationTargetException
	 * @throws WTException
	 */
	public static void renameGerber(String username, String password) throws RemoteException,
			InvocationTargetException, WTException {
		RemoteMethodServer rm = RemoteMethodServer.getDefault();
		rm.setUserName(username);
		rm.setPassword(password);
		SessionHelper.manager.setAuthenticatedPrincipal("wcadmin");
		rm.invoke("renameGerberDocs", LoadDataUtils.class.getName(), null,
				null, null);
	}

	/**
	 * 根据分类创建所有原理图和PCB图
	 * @param password 
	 * @param username 
	 * 
	 * @throws RemoteException
	 * @throws InvocationTargetException
	 * @throws WTException
	 */
	public static void createAllSCHPCB(String username, String password) throws RemoteException,
			InvocationTargetException, WTException {
		RemoteMethodServer rm = RemoteMethodServer.getDefault();
		rm.setUserName(username);
		rm.setPassword(password);
		SessionHelper.manager.setAuthenticatedPrincipal("wcadmin");
		rm.invoke("beatchCreateAllSCHOrPCB", LoadDataUtils.class.getName(),
				null, null, null);
	}

	/**
	 * 更改PCBA装配图关系 将PCBA装配图从基关联的PCBA上移动到PCB上
	 * @param password 
	 * @param username 
	 * 
	 * @throws RemoteException
	 * @throws InvocationTargetException
	 * @throws WTException
	 */
	public static void movePCBAToPCB(String username, String password) throws RemoteException,
			InvocationTargetException, WTException {
		RemoteMethodServer rm = RemoteMethodServer.getDefault();
		rm.setUserName(username);
		rm.setPassword(password);
		SessionHelper.manager.setAuthenticatedPrincipal("wcadmin");
		rm.invoke("updateReferenceLink", HistoryUtils.class.getName(), null,
				null, null);
	}

	/**
	 * 根据分类创建所有原理图和PCB图
	 * @param password 
	 * @param username 
	 * 
	 * @throws RemoteException
	 * @throws InvocationTargetException
	 * @throws WTException
	 */
	public static void updateAllSCHPCB(String username, String password) throws RemoteException,
			InvocationTargetException, WTException {
		RemoteMethodServer rm = RemoteMethodServer.getDefault();
		rm.setUserName(username);
		rm.setPassword(password);
		SessionHelper.manager.setAuthenticatedPrincipal("wcadmin");
		rm.invoke("updateAllSCHOrPCBCreator", LoadDataUtils.class.getName(),
				null, null, null);
	}
	
	/**
	 * 系统中所有PCBA装配图文档添加前缀“ASSEMBLY-”
	 * 
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void renameAssemblyDocs() throws WTException,
			WTPropertyVetoException {
		QueryResult qr = findAssemblyDocs();
		System.out.println(qr.size());
		while (qr.hasMoreElements()) {
			WTDocument doc = (WTDocument) qr.nextElement();
			if (ECADConst.DISABLE_FOR_DESIGN_STATE.equals(doc
					.getLifeCycleState().toString())) {
				continue;
			}
			
			WTPart part = getPartByDoc(doc);
			if (part == null) {
				continue;
			}
			String pn = part.getNumber();
			
			if (!doc.getNumber().equalsIgnoreCase("ASSEMBLY-" + pn)) {
				WTDocument docs = CommonUtil.getLatestWTDocByNumber("ASSEMBLY-" + pn);
				if(docs == null&& !WorkInProgressHelper.isCheckedOut(doc)){				
					renumberDoc(doc, "ASSEMBLY-" + pn);
					renameDocFileName(doc, doc.getNumber());
				}
			}
		}
	}

	/**
	 * 更新文档编号
	 * @param docNumber	文档编号
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void renumberDoc(WTDocument doc,String prefix) throws WTException, WTPropertyVetoException{
		
		//String docNumber = doc.getNumber();

		WTDocumentMaster master=(WTDocumentMaster) doc.getMaster();

		WTDocumentMasterIdentity identity=(WTDocumentMasterIdentity) master.getIdentificationObject();
		identity.setNumber(prefix);
		//identity.setName(prefix);

		master =(WTDocumentMaster) IdentityHelper.service.changeIdentity(master,identity);
	}
	
	/**
	 * 更新文档编号
	 * @param docNumber	文档编号
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void renameDocFileName(WTDocument doc,String prefix) throws WTException, WTPropertyVetoException{
		
		QueryResult qr = ContentHelper.service.getContentsByRole(doc, ContentRoleType.PRIMARY);
		if (qr.hasMoreElements()) {
			ApplicationData appdata = (ApplicationData) qr.nextElement();
			String filename = appdata.getFileName();
			filename = prefix+filename.substring(filename.lastIndexOf("."));
			appdata.setFileName(filename);
			PersistenceHelper.manager.save(appdata);
		}
	}
	
	/**
	 * 系统中所有GERBER文件文档添加前缀“GERBER-”
	 * 
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void renameGerberDocs() throws WTException,
			WTPropertyVetoException {
		QueryResult qr = findGerberDocs();
		System.out.println(qr.size());
		while (qr.hasMoreElements()) {
			WTDocument doc = (WTDocument) qr.nextElement();
			if (ECADConst.DISABLE_FOR_DESIGN_STATE.equals(doc
					.getLifeCycleState().toString())) {
				continue;
			}
			if (!doc.getNumber().startsWith("GERBER-")) {
				HistoryUtils.renumberDoc(doc, "GERBER-" + doc.getNumber());
				renameDocFileName(doc, doc.getNumber());
			}
		}
	}

	/**
	 * 将文件夹中的文件挂载到原理图或PCB图档对象上
	 * 
	 * @param path
	 *            文件夹路径
	 * @param type
	 *            原理图填orcad或者PCB图填pcb
	 * @throws Exception
	 */
	public static void loadSCHOrPCB(String path, String type) throws Exception {
		File file = new File(path);
		if (file != null && file.exists()) {
			if (file.isDirectory()) {
				File[] allfile = file.listFiles();
				for (File tmp : allfile) {
					if (tmp.isFile()) {
						String filename = tmp.getName();
						if (ECADConst.SCHTOOL.equals(type)) {
							if (filename.endsWith(".zip")) {
									//&& !filename.startsWith("Alligro-")) {
								//String alligropath = path + File.separator + "Alligro-" + filename;
								//File alligro = new File(alligropath);
								//if (alligro.exists() && alligro.isFile()) {
									EPMDocument epm = CommonUtil
											.getEPMDocumentByNumber(filename
													.substring(0, filename
															.indexOf(".zip")));
									
									if (epm == null) {
										continue;
									}
									ApplicationData appdata = EPMUtil
											.getPrimaryAppdata(epm);
									if (appdata != null) {
										continue;
									}
									if (ECADutil.isSCHEPM(epm)) {
										updateSchContent(epm, tmp, null);
										//updateSchContent(epm, tmp, alligro);
									}
								//}
							}
						} else if (ECADConst.PCBTOOL.equals(type)) {
							if (filename.endsWith(".zip")
									&& !filename.startsWith("Alligro-")) {

								EPMDocument epm = CommonUtil
										.getEPMDocumentByNumber(filename
												.substring(0, filename
														.indexOf(".zip")));
								if (epm == null) {
									continue;
								}
								ApplicationData appdata = EPMUtil
										.getPrimaryAppdata(epm);
								if (appdata != null) {
									continue;
								}
								if (ECADutil.isPCBEPM(epm)) {
									updatePCBContent(epm, tmp);
								}

							}
						}

					}
				}
			}
		}

	}

	/**
	 * 根据系统中的分类批量创建原理图或者PCB图图档对象
	 * 
	 * @throws Exception
	 */
	public static void beatchCreateAllSCHOrPCB() throws Exception {
		Map map = HistoryUtils.getClfNumber();
		List<String> pcba = (List) map.get("PCBA");
		List<String> pcb = (List) map.get("PCB");
		for (String str : pcba) {
			System.out.println("PCBA\t" + str);
			createSCHOrPCBByCLS(str);
		}

		for (String str : pcb) {
			System.out.println("PCB\t" + str);
			createSCHOrPCBByCLS(str);
		}
	}

	/**
	 * 根据分类名称批量创建原理图或者PCB图图档对象
	 * 
	 * @throws Exception
	 */
	public static void createSCHOrPCBByCLS(String clsName) throws Exception {
		QueryResult qr = NodeUtil.getAllPartsByCLFNodesName(clsName);
		System.out.println(qr.size());
		while (qr.hasMoreElements()) {
			Object obj = qr.nextElement();
			if (obj instanceof WTPart) {
				WTPart part = (WTPart) obj;
				if (ECADutil.isPCBA(part)) {
					State state = part.getLifeCycleState();
					if ("DESIGNMODIFICATION".equalsIgnoreCase(state.toString())
							|| "DESIGN".equalsIgnoreCase(state.toString())) {
						state = State.toState(ECADConst.DESIGN_STATE);
					} else if ("DESIGNREVIEW"
							.equalsIgnoreCase(state.toString())||"RELEASEDFAILED".equalsIgnoreCase(state
									.toString())) {
						state = State.toState(ECADConst.RELEASED_STATE);
					}else if (ECADConst.DISABLE_FOR_DESIGN_STATE
							.equalsIgnoreCase(state.toString())) {
						state = State.toState(ECADConst.DISABLE_FOR_DESIGN_STATE);
					} else if (ECADConst.RELEASED_STATE.equalsIgnoreCase(state
							.toString())) {

					} else {
						continue;
					}
					String number = part.getNumber();
					EPMDocument epm = CommonUtil.getEPMDocumentByNumber(number);
					if (epm != null) {
						continue;
					}
					String name = part.getName();
					WTContainer container = part.getContainer();
					Folder folder = FolderHelper.getFolder(part);
					String path = folder.getLocation() + "/设计图档";
					Folder ecadFolder = null;
					try {
						ecadFolder = (SubFolder) FolderHelper.service
								.getFolder(path, part.getContainerReference());
					} catch (FolderNotFoundException folderNotFound) {
						// folderNotFound.printStackTrace();
						ecadFolder = FolderHelper.service.createSubFolder(path,
								part.getContainerReference());
					}

					path = folder.getLocation() + ECADConst.ECADLOCATION;
					try {
						ecadFolder = (SubFolder) FolderHelper.service
								.getFolder(path, part.getContainerReference());
					} catch (FolderNotFoundException folderNotFound) {
						// folderNotFound.printStackTrace();
						ecadFolder = FolderHelper.service.createSubFolder(path,
								part.getContainerReference());
					}
					// 演练某个文件夹
					// if (path.indexOf("5.0 Version BMS开发") > 0) {
					// ecadFolder = FolderHelper.service.getFolder(path,
					// part.getContainerReference());
					String creator = part.getCreatorName();
					SessionHelper.manager.setPrincipal(creator);
					epm = EPMUtil.createSchEPM(number, name, container,
							ecadFolder);
					SessionHelper.manager.setAdministrator();
					LifeCycleHelper.service.setLifeCycleState(epm, state);
					// }
				} else if (ECADutil.isPCB(part)) {
					State state = part.getLifeCycleState();
					if ("DESIGNMODIFICATION".equalsIgnoreCase(state.toString())
							|| "DESIGN".equalsIgnoreCase(state.toString())) {
						state = State.toState(ECADConst.DESIGN_STATE);
					} else if ("DESIGNREVIEW"
							.equalsIgnoreCase(state.toString())||"RELEASEDFAILED".equalsIgnoreCase(state
									.toString())) {
						state = State.toState(ECADConst.RELEASED_STATE);
					}else if (ECADConst.DISABLE_FOR_DESIGN_STATE
							.equalsIgnoreCase(state.toString())) {
						state = State.toState(ECADConst.DISABLE_FOR_DESIGN_STATE);
					} else if (ECADConst.RELEASED_STATE.equalsIgnoreCase(state
							.toString())) {

					} else {
						continue;
					}
					String number = part.getNumber();
					EPMDocument epm = CommonUtil.getEPMDocumentByNumber(number);
					if (epm != null) {
						continue;
					}
					String name = part.getName();
					WTContainer container = part.getContainer();
					Folder folder = FolderHelper.getFolder(part);
					String path = folder.getLocation() + "/设计图档";
					Folder ecadFolder = null;
					try {
						ecadFolder = (SubFolder) FolderHelper.service
								.getFolder(path, part.getContainerReference());
					} catch (FolderNotFoundException folderNotFound) {
						// folderNotFound.printStackTrace();
						ecadFolder = FolderHelper.service.createSubFolder(path,
								part.getContainerReference());
					}

					path = folder.getLocation() + ECADConst.ECADLOCATION;
					try {
						ecadFolder = (SubFolder) FolderHelper.service
								.getFolder(path, part.getContainerReference());
					} catch (FolderNotFoundException folderNotFound) {
						// folderNotFound.printStackTrace();
						ecadFolder = FolderHelper.service.createSubFolder(path,
								part.getContainerReference());
					}
					// 演练某个文件夹
					// if (path.indexOf("5.0 Version BMS开发") > 0) {
					// Folder ecadFolder = FolderHelper.service.getFolder(path,
					// part.getContainerReference());
					String creator = part.getCreatorName();
					SessionHelper.manager.setPrincipal(creator);
					epm = EPMUtil
							.createPCB(number, name, container, ecadFolder);
					SessionHelper.manager.setAdministrator();
					LifeCycleHelper.service.setLifeCycleState(epm, state);
					// }
				}
			}
		}
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
	public static void updateSchContent(EPMDocument epm, File file, File alligro)
			throws Exception, WTException {
		Transaction tx = new Transaction();
		tx.start();
		ApplicationData appdata = EPMUtil.getPrimaryAppdata(epm);
		if (appdata != null) {
			tx = null;
			return;
		}
		// epm = (EPMDocument) CommonUtil.checkoutObject(epm);
		// epm = (EPMDocument) CommonUtil.checkinObject(epm, "Update Content");
		ContentHolder contentHolder = ContentHelper.service.getContents(epm);
		IBAUtility iba = new IBAUtility(epm);
		iba.setIBAValue("ECAD_DESIGN_ITEM", epm.getNumber() + ".DSN");
		iba.updateAttributeContainer(epm);
		iba.updateIBAHolder(epm);

		String number = epm.getNumber();

		// 更新主要内容

		if (appdata == null) {
			appdata = ApplicationData.newApplicationData(epm);
		} else {
			tx = null;
			return;
		}

		appdata.setRole(ContentRoleType.MULTI_PRIMARY);
		appdata.setCategory("NATIVE_DESIGN");
		// File file = new File("E:\\" + number + ".zip");
		appdata.setFileName("{$CAD_NAME}.zip");
		appdata.setUploadedFromPath("ECAD_SET_DLD_ON_SYNCUP");
		appdata.setClientData("");
		InputStream is = new FileInputStream(file);// new
													// FileInputStream(file);
		System.out.println("Primary InputStream\t" + is);
		if (is == null) {
			throw new WTException("原理图上传失败。");
		}
		// 移除所有附件(网表文件)
		epm = (EPMDocument) EPMUtil.removeSecondaryContent(epm);

		
		
		if(alligro != null){
			// 创建在新版本上创建网表文件
			ApplicationData appdata1 = ApplicationData
					.newApplicationData(contentHolder);
			appdata1.setRole(ContentRoleType.SECONDARY);
			appdata1.setCategory("GENERAL");
			String allegroName = "Allegro-" + number + ".zip";
			// File file1 = new File("E:\\Allego-" + number + ".zip");
			appdata1.setFileName(allegroName);
			
			InputStream is1 = new FileInputStream(alligro);// new
			// FileInputStream(file1);
			System.out.println("Secondary InputStream\t" + is1);
			if (is1 == null) {
				throw new WTException("原理图相关的网表文件上传失败。");
			}
			ContentServerHelper.service.updateContent(contentHolder, appdata1, is1);
		}
		
		ContentServerHelper.service.updateContent(contentHolder, appdata, is);
		
		// createRep(epm);

		tx.commit();
		is.close();
		//is1.close();
		file.delete();
		//alligro.delete();
		tx = null;
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
	public static void updatePCBContent(EPMDocument epm, File file)
			throws Exception, WTException {
		// ContentHolder contentHolder = ContentHelper.service.getContents(epm);
		Transaction tx = new Transaction();
		tx.start();
		ApplicationData appdata = EPMUtil.getPrimaryAppdata(epm);
		if (appdata != null) {
			tx = null;
			return;
		}
		// epm = (EPMDocument) CommonUtil.checkoutObject(epm);
		// epm = (EPMDocument) CommonUtil.checkinObject(epm, "Update Content");
		IBAUtility iba = new IBAUtility(epm);
		iba.setIBAValue("ECAD_DESIGN_ITEM", epm.getNumber() + ".BRD");
		iba.updateAttributeContainer(epm);
		iba.updateIBAHolder(epm);

		String number = epm.getNumber();

		// ContentHolder
		if (appdata == null) {
			appdata = ApplicationData.newApplicationData(epm);
		} else {
			tx = null;
			return;
		}
		appdata.setRole(ContentRoleType.MULTI_PRIMARY);
		appdata.setCategory("NATIVE_DESIGN");
		// File file = new File("E:\\" + number + ".zip");
		appdata.setFileName("{$CAD_NAME}.zip");
		appdata.setUploadedFromPath("ECAD_SET_DLD_ON_SYNCUP");
		appdata.setClientData("");
		InputStream is = new FileInputStream(file);// new
		if (is == null) {
			throw new WTException("PCB图上传失败。");
		} // FileInputStream(file);

		ContentServerHelper.service.updateContent(epm, appdata, is);

		tx.commit();
		is.close();
		file.delete();
		tx = null;
	}

	/**
	 * 查询所有PCBA装配图文档
	 * 
	 * @return
	 * @throws WTException
	 */
	public static QueryResult findAssemblyDocs() throws WTException {
		QuerySpec qs = new QuerySpec();
		int docIndex = qs.appendClassList(WTDocument.class, true);
		int typeDefIndex = qs.appendClassList(WTTypeDefinition.class, false);
		int typeDefMasterIndex = qs.addClassList(WTTypeDefinitionMaster.class,
				false);

		SearchCondition sc = new SearchCondition(WTDocument.class,
				"typeDefinitionReference.key.id", WTTypeDefinition.class,
				WTAttributeNameIfc.ID_NAME);
		qs.appendWhere(sc, new int[] { docIndex, typeDefIndex });

		sc = new SearchCondition(WTTypeDefinition.class,
				"masterReference.key.id", WTTypeDefinitionMaster.class,
				WTAttributeNameIfc.ID_NAME);
		qs.appendAnd();
		qs.appendWhere(sc, new int[] { typeDefIndex, typeDefMasterIndex });

		// sc = new SearchCondition(WTTypeDefinitionMaster.class,
		// WTTypeDefinitionMaster.DESCRIPTION_KEY, SearchCondition.EQUAL,
		// "器件封装文件");
		sc = new SearchCondition(WTTypeDefinitionMaster.class,
				WTTypeDefinitionMaster.DISPLAY_NAME_KEY, SearchCondition.EQUAL,
				ECADConst.ASSEMBLYDRAWING);
		qs.appendAnd();
		qs.appendWhere(sc, new int[] { typeDefMasterIndex });

		sc = new SearchCondition(WTDocument.class,
				WTAttributeNameIfc.LATEST_ITERATION, SearchCondition.IS_TRUE);
		qs.appendAnd();
		qs.appendWhere(sc, new int[] { docIndex });

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
	}

	/**
	 * 查询所有GERBER文件文档
	 * 
	 * @return
	 * @throws WTException
	 */
	public static QueryResult findGerberDocs() throws WTException {
		QuerySpec qs = new QuerySpec();
		int docIndex = qs.appendClassList(WTDocument.class, true);
		int typeDefIndex = qs.appendClassList(WTTypeDefinition.class, false);
		int typeDefMasterIndex = qs.addClassList(WTTypeDefinitionMaster.class,
				false);

		SearchCondition sc = new SearchCondition(WTDocument.class,
				"typeDefinitionReference.key.id", WTTypeDefinition.class,
				WTAttributeNameIfc.ID_NAME);
		qs.appendWhere(sc, new int[] { docIndex, typeDefIndex });

		sc = new SearchCondition(WTTypeDefinition.class,
				"masterReference.key.id", WTTypeDefinitionMaster.class,
				WTAttributeNameIfc.ID_NAME);
		qs.appendAnd();
		qs.appendWhere(sc, new int[] { typeDefIndex, typeDefMasterIndex });

		// sc = new SearchCondition(WTTypeDefinitionMaster.class,
		// WTTypeDefinitionMaster.DESCRIPTION_KEY, SearchCondition.EQUAL,
		// "器件封装文件");
		sc = new SearchCondition(WTTypeDefinitionMaster.class,
				WTTypeDefinitionMaster.DISPLAY_NAME_KEY, SearchCondition.EQUAL,
				ECADConst.GERBERTYPE);
		qs.appendAnd();
		qs.appendWhere(sc, new int[] { typeDefMasterIndex });

		sc = new SearchCondition(WTDocument.class,
				WTAttributeNameIfc.LATEST_ITERATION, SearchCondition.IS_TRUE);
		qs.appendAnd();
		qs.appendWhere(sc, new int[] { docIndex });

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
	}

	/**
	 * 根据PCBA转配图文档获取PCB部件
	 * 
	 * @param doc
	 * @return
	 * @throws WTException
	 */
	public static WTPart getPartByDoc(WTDocument doc) throws WTException {
		WTPart wtPart = null;
		QueryResult qr = WTPartHelper.service.getDescribesWTParts(doc);
		while (qr.hasMoreElements()) {
			Object obj = qr.nextElement();
			WTPart part = null;
			if (obj instanceof WTPart) {
				part = (WTPart) obj;
			}
			if (ECADutil.isPCB(part)) {
				wtPart = part;
			}
		}
		return wtPart;
	}

	public static void loadSCHAttrs(String path) throws IOException,
			WTException, WTPropertyVetoException {
		FileInputStream fis = new FileInputStream(new File(path));
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		XSSFSheet sheet = wb.getSheetAt(0);
		for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
			XSSFRow row = sheet.getRow(i);
			String schNum = "";
			String platName = "";
			String businessVersion = "";
			if (row != null) {
				String value = null;
				XSSFCell schNumcell = row.getCell(0);
				XSSFCell platNamecell = row.getCell(1);
				XSSFCell busiVersioncell = row.getCell(2);

				if (schNumcell == null) {
					continue;
				} else if (schNumcell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
					DecimalFormat df = new DecimalFormat("0");
					schNum = df.format(schNumcell.getNumericCellValue());
				} else if (schNumcell.getCellType() == Cell.CELL_TYPE_STRING) {
					schNum = schNumcell.getStringCellValue();
				}

				if (platNamecell == null) {
					continue;
				} else if (platNamecell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
					DecimalFormat df = new DecimalFormat("0");
					platName = df.format(platNamecell.getNumericCellValue());
				} else if (platNamecell.getCellType() == Cell.CELL_TYPE_STRING) {
					platName = platNamecell.getStringCellValue();
				}

				if (busiVersioncell == null) {
					continue;
				} else if (busiVersioncell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
					DecimalFormat df = new DecimalFormat("0");
					businessVersion = df.format(busiVersioncell
							.getNumericCellValue());
				} else if (busiVersioncell.getCellType() == Cell.CELL_TYPE_STRING) {
					businessVersion = busiVersioncell.getStringCellValue();
				}

				if (StringUtils.isNotBlank(schNum)) {
					EPMDocument schepm = CommonUtil
							.getEPMDocumentByNumber(schNum.toUpperCase());
					if (schepm != null) {
						IBAUtility iba = new IBAUtility(schepm);
						if (StringUtils.isNotBlank(platName)) {
							iba.setIBAValue("CATL_PlatformName", platName);
						}
						if (StringUtils.isNotBlank(businessVersion)) {
							iba.setIBAValue("CATL_BusinessVersion",
									businessVersion);
						}
						iba.updateAttributeContainer(schepm);
						iba.updateIBAHolder(schepm);
					}
				} else {
					System.out.println("第" + i + "行原理图编码为空");
				}
			}
		}
	}

	/**
	 * 推送PCB和其他元器件清单
	 * 
	 * @param path
	 * @throws Exception 
	 */
	public static void loadPCBToCadence(String path) throws Exception {
		FileInputStream fis = new FileInputStream(new File(path));
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		XSSFSheet sheet = wb.getSheetAt(0);
		for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
			XSSFRow row = sheet.getRow(i);
			String partNum = "";
			String Schematic_Part = "";
			String Old_Footprint = "";
			if (row != null) {
				XSSFCell partNumcell = row.getCell(1);
				XSSFCell schcell = row.getCell(2);
				XSSFCell pcbNumcell = row.getCell(3);

				if (partNumcell == null) {
					continue;
				} else if (partNumcell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
					DecimalFormat df = new DecimalFormat("0");
					partNum = df.format(partNumcell.getNumericCellValue());
				} else if (partNumcell.getCellType() == Cell.CELL_TYPE_STRING) {
					partNum = partNumcell.getStringCellValue();
				}

				if (schcell == null) {
					continue;
				} else if (schcell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
					DecimalFormat df = new DecimalFormat("0");
					Schematic_Part = df.format(schcell.getNumericCellValue());
				} else if (schcell.getCellType() == Cell.CELL_TYPE_STRING) {
					Schematic_Part = schcell.getStringCellValue();
				}

				if (pcbNumcell == null) {
					continue;
				} else if (pcbNumcell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
					DecimalFormat df = new DecimalFormat("0");
					Old_Footprint = df.format(pcbNumcell
							.getNumericCellValue());
				} else if (pcbNumcell.getCellType() == Cell.CELL_TYPE_STRING) {
					Old_Footprint = pcbNumcell.getStringCellValue();
				}
				
				Map<String, String> map = new HashMap<>();
				map.put(CadenceConfConstant.CADENCE_PLM_SCHEMATIC_PART, Schematic_Part);
				map.put(CadenceConfConstant.CADENCE_PLM_OLD_FOOTPRINT, Old_Footprint);
				
				if (StringUtils.isNotBlank(partNum)) {
					WTPart part = CommonUtil.getLatestWTpartByNumber(partNum
							.toUpperCase());
					if (part != null) {
						PartReleaseToCadenceUtil.sendPartToCadence(part,map,null);
						//PartReleaseToCadenceUtil.updateStateToCadence(part);
					}
				} else {
					System.out.println("第" + i + "行原理图编码为空");
				}
			}
		}
	}

	/**
	 * 推送电子元器件清单
	 * 
	 * @param path
	 * @throws Exception 
	 */
	public static void loadEleToCadence(String path) throws Exception {
		FileInputStream fis = new FileInputStream(new File(path));
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		XSSFSheet sheet = wb.getSheetAt(0);
		for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
			XSSFRow row = sheet.getRow(i);
			String partNum = "";
			String schematic_part = "";
			String old_Footprint = "";
			String alt_Symbols = "";
			String edadoc_Footprint = "";
			String ipc7351_Footprint_A_Maximum = "";
			String ipc7351_Footprint_B_Normal = "";
			String ipc7351_Footprint_C_Minimum = "";
			String vibration_Footprint = "";
			if (row != null) {
				XSSFCell partNumcell = row.getCell(1);

				XSSFCell schematic_part_cell = row.getCell(2);
				XSSFCell old_Footprint_cell = row.getCell(3);
				XSSFCell alt_Symbols_cell = row.getCell(4);
				XSSFCell edadoc_Footprint_cell = row.getCell(5);
				XSSFCell ipc7351_Footprint_A_Maximum_cell = row.getCell(6);
				XSSFCell ipc7351_Footprint_B_Normal_cell = row.getCell(7);
				XSSFCell ipc7351_Footprint_C_Minimum_cell = row.getCell(8);
				XSSFCell vibration_Footprint_cell = row.getCell(9);

				partNum = getCellValue(partNumcell);

				schematic_part = getCellValue(schematic_part_cell);
				old_Footprint = getCellValue(old_Footprint_cell);
				alt_Symbols = getCellValue(alt_Symbols_cell);
				edadoc_Footprint = getCellValue(edadoc_Footprint_cell);
				ipc7351_Footprint_A_Maximum = getCellValue(ipc7351_Footprint_A_Maximum_cell);
				ipc7351_Footprint_B_Normal = getCellValue(ipc7351_Footprint_B_Normal_cell);
				ipc7351_Footprint_C_Minimum = getCellValue(ipc7351_Footprint_C_Minimum_cell);
				vibration_Footprint = getCellValue(vibration_Footprint_cell);

				Map<String, String> map = new HashMap<>();
				map.put("schematic_part", schematic_part);
				map.put("old_Footprint", old_Footprint);
				map.put("alt_Symbols", alt_Symbols);
				map.put("edadoc_Footprint", edadoc_Footprint);
				map.put("ipc7351_Footprint_A_Maximum",
						ipc7351_Footprint_A_Maximum);
				map.put("ipc7351_Footprint_B_Normal",
						ipc7351_Footprint_B_Normal);
				map.put("ipc7351_Footprint_C_Minimum",
						ipc7351_Footprint_C_Minimum);
				map.put("vibration_Footprint", vibration_Footprint);

				if (StringUtils.isNotBlank(partNum)) {
					WTPart part = CommonUtil.getLatestWTpartByNumber(partNum
							.toUpperCase());
					if (part != null) {
						PartReleaseToCadenceUtil.sendPartToCadence(part, map,null);
						createCadenceAttributeBean(part, map);
						//PartReleaseToCadenceUtil.updateStateToCadence(part);
					}
				} else {
					System.out.println("第" + i + "行原理图编码为空");
				}
			}
		}
	}

	/**
	 * 推送电气元器件清单
	 * 
	 * @param path
	 * @throws Exception 
	 */
	public static void loadEletricalToCadence(String path) throws Exception {
		FileInputStream fis = new FileInputStream(new File(path));
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		XSSFSheet sheet = wb.getSheetAt(0);
		for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
			XSSFRow row = sheet.getRow(i);
			String partNum = "";
			String schematic_part = "";
			String old_Footprint = "";
			String alt_Symbols = "";
			String edadoc_Footprint = "";
			String ipc7351_Footprint_A_Maximum = "";
			String ipc7351_Footprint_B_Normal = "";
			String ipc7351_Footprint_C_Minimum = "";
			String vibration_Footprint = "";
			String grade = "";
			if (row != null) {
				XSSFCell partNumcell = row.getCell(1);

				XSSFCell schematic_part_cell = row.getCell(2);
				XSSFCell old_Footprint_cell = row.getCell(3);
				XSSFCell alt_Symbols_cell = row.getCell(4);
				XSSFCell edadoc_Footprint_cell = row.getCell(5);
				XSSFCell ipc7351_Footprint_A_Maximum_cell = row.getCell(6);
				XSSFCell ipc7351_Footprint_B_Normal_cell = row.getCell(7);
				XSSFCell ipc7351_Footprint_C_Minimum_cell = row.getCell(8);
				XSSFCell vibration_Footprint_cell = row.getCell(9);
				XSSFCell grade_cell = row.getCell(10);

				partNum = getCellValue(partNumcell);

				schematic_part = getCellValue(schematic_part_cell);
				old_Footprint = getCellValue(old_Footprint_cell);
				alt_Symbols = getCellValue(alt_Symbols_cell);
				edadoc_Footprint = getCellValue(edadoc_Footprint_cell);
				ipc7351_Footprint_A_Maximum = getCellValue(ipc7351_Footprint_A_Maximum_cell);
				ipc7351_Footprint_B_Normal = getCellValue(ipc7351_Footprint_B_Normal_cell);
				ipc7351_Footprint_C_Minimum = getCellValue(ipc7351_Footprint_C_Minimum_cell);
				vibration_Footprint = getCellValue(vibration_Footprint_cell);

				grade = getCellValue(grade_cell);

				Map<String, String> map = new HashMap<>();
				map.put("schematic_part", schematic_part);
				map.put("old_Footprint", old_Footprint);
				map.put("alt_Symbols", alt_Symbols);
				map.put("edadoc_Footprint", edadoc_Footprint);
				map.put("ipc7351_Footprint_A_Maximum",
						ipc7351_Footprint_A_Maximum);
				map.put("ipc7351_Footprint_B_Normal",
						ipc7351_Footprint_B_Normal);
				map.put("ipc7351_Footprint_C_Minimum",
						ipc7351_Footprint_C_Minimum);
				map.put("vibration_Footprint", vibration_Footprint);

				if (StringUtils.isNotBlank(partNum)) {
					WTPart part = CommonUtil.getLatestWTpartByNumber(partNum
							.toUpperCase());
					if (part != null) {
						PartReleaseToCadenceUtil.sendPartToCadence(part, map,grade);
						createCadenceAttributeBean(part, map);
						//PartReleaseToCadenceUtil.updateStateToCadence(part);
					}
				} else {
					System.out.println("第" + i + "行原理图编码为空");
				}
			}
		}
	}

	/**
	 * 根据单元格获取内容
	 * 
	 * @param cell
	 * @return
	 */
	public static String getCellValue(Cell cell) {
		String value = "";
		if (cell == null) {
			return null;
		} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			DecimalFormat df = new DecimalFormat("0");
			value = df.format(cell.getNumericCellValue());
		} else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
			value = cell.getStringCellValue();
		}
		return value;
	}

	
	/**
	 * pbo与CadenceAttributeBean关联 
	 * @throws WTException 
	 */
	public static void createCadenceAttributeBean(WTPart part,Map<String,String> map ) throws WTException{
		//WTPart part=(WTPart) pbo;
		String oid=ObjectReference.newObjectReference(part).toString();
		CadenceAttributeBean cadenceAttrbean = new CadenceAttributeBean();
		cadenceAttrbean.setPartOid(oid);
		cadenceAttrbean.setSchematic_part(map.get("schematic_part"));
		cadenceAttrbean.setOld_Footprint(map.get("old_Footprint"));
		cadenceAttrbean.setAlt_Symbols(map.get("alt_Symbols"));
		cadenceAttrbean.setEdadoc_Footprint(map.get("edadoc_Footprint"));
		cadenceAttrbean.setIpc7351_Footprint_A_Maximum(map.get("ipc7351_Footprint_A_Maximum"));
		cadenceAttrbean.setIpc7351_Footprint_B_Normal(map.get("ipc7351_Footprint_B_Normal"));
		cadenceAttrbean.setIpc7351_Footprint_C_Minimum(map.get("ipc7351_Footprint_C_Minimum"));
		cadenceAttrbean.setVibration_Footprint(map.get("vibration_Footprint"));
		
		XmlObjectStoreManager storeManager = XWorksHelper.getXmlObjectStoreManager();
		storeManager.save(cadenceAttrbean);
		BaseXmlObjectRef parentRef = BaseXmlObjectRef.newBaseXmlObjectRef(part.getMaster());
		BaseXmlObjectRef childRef = new BaseXmlObjectRef(cadenceAttrbean);
		BaseXmlObjectLink link = new BaseXmlObjectLink(parentRef, CadenceXmlObjectUtil.applicationFormOwner, childRef, "CadenceAttributeBean");
		storeManager.save(link);
	}

	/**
	 * 根据系统中的分类批量创建原理图或者PCB图图档对象
	 * 
	 * @throws Exception
	 */
	public static void updateAllSCHOrPCBCreator() throws Exception {
		Map map = HistoryUtils.getClfNumber();
		List<String> pcba = (List) map.get("PCBA");
		List<String> pcb = (List) map.get("PCB");
		for (String str : pcba) {
			System.out.println("PCBA\t" + str);
			updateSCHOrPCBCreatorByCLS(str);
		}

		for (String str : pcb) {
			System.out.println("PCB\t" + str);
			updateSCHOrPCBCreatorByCLS(str);
		}
	}

	/**
	 * 根据分类名称批量创建原理图或者PCB图图档对象
	 * 
	 * @throws Exception
	 */
	public static void updateSCHOrPCBCreatorByCLS(String clsName) throws Exception {
		QueryResult qr = NodeUtil.getAllPartsByCLFNodesName(clsName);
		System.out.println(qr.size());
		while (qr.hasMoreElements()) {
			Object obj = qr.nextElement();
			if (obj instanceof WTPart) {
				WTPart part = (WTPart) obj;
				if (ECADutil.isPCBA(part)) {					
					String number = part.getNumber();
					EPMDocument epm = CommonUtil.getEPMDocumentByNumber(number);
					if (epm != null) {
						
						epm.getIterationInfo().getCreator().setObject(part.getCreator().getObject());
						PersistenceServerHelper.manager.update(epm);
					}
					
				} else if (ECADutil.isPCB(part)) {
					
					String number = part.getNumber();
					EPMDocument epm = CommonUtil.getEPMDocumentByNumber(number);
					if (epm != null) {
						epm.getIterationInfo().getCreator().setObject(part.getCreator().getObject());
						PersistenceServerHelper.manager.update(epm);
					}
					
				}
			}
		}
	}

}
