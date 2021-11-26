package com.catl.ecad.load;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.type.mgmt.server.impl.WTTypeDefinitionMaster;

import wt.access.AccessControlHelper;
import wt.access.AccessControlRule;
import wt.access.AccessPermission;
import wt.access.AccessPermissionSet;
import wt.admin.AdminDomainRef;
import wt.admin.AdministrativeDomain;
import wt.admin.AdministrativeDomainHelper;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.inf.library.WTLibrary;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.State;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.pdmlink.PDMLinkProduct;
import wt.project.Role;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.type.TypedUtility;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;


/**
 * 
 * 导入策略权限和导出策略权限
 *
 */
public class LoadAccess implements RemoteAccess {
	private final static String[] permissionNames = new String[] { "读取", "修改", "创建", "移动创建", "下载", "删除", "管理", "修订",
			"新建视图版本", "变更权限" };
	private final static AccessPermission[] permissions = new AccessPermission[] { AccessPermission.READ,
			AccessPermission.MODIFY, AccessPermission.CREATE, AccessPermission.CREATE_BY_MOVE,
			AccessPermission.DOWNLOAD, AccessPermission.DELETE, AccessPermission.ADMINISTRATIVE,
			AccessPermission.REVISE, AccessPermission.NEW_VIEW_VERSION, AccessPermission.CHANGE_PERMISSIONS };

	public static void main(String[] args) throws RemoteException, InvocationTargetException, WTException {
		RemoteMethodServer ms = RemoteMethodServer.getDefault();
		Scanner sc = new Scanner(System.in);
		System.out.println("1、请输入用户名:");
		String username = sc.nextLine();
		System.out.println("2、请输入密码:");
		String password = sc.nextLine();
		ms.setUserName(username);
		ms.setPassword(password);
		// com.ptc.windchill.enterprise.change2.forms.processors.CreateChangeNoticeFormProcessor
		SessionHelper.manager.setAuthenticatedPrincipal("wcadmin");
		String path = args[0];
		Class[] clazz= {String.class};
		Object[] params = {path};
		RemoteMethodServer.getDefault().invoke("importAccessRules", LoadAccess.class.getName(), null, clazz, params);
		System.out.println("Hello ,I'm here.");

	}



	/**
	 * 根据模板导入策略权限
	 * @param primaryBusinessObject
	 * @return
	 * @throws IOException
	 * @throws WTException
	 * @throws WTPropertyVetoException 
	 */
	public static String importAccessRules(String path) throws IOException, WTException, WTPropertyVetoException {
		//public static String importAccessRules(WTObject primaryBusinessObject) throws IOException, WTException {
		Map<String, String> typeMap = ObjectTypeUtil1.getAllTypeDefinitionMap();
		

		Map<String,State> mapState =allStateMap();
		Map<String,String> mapRole = allRoleMap();
		
		//WTDocument doc = null;

	/*	if (primaryBusinessObject instanceof WTDocument) {
			doc = (WTDocument) primaryBusinessObject;
		}*/

		InputStream in = new FileInputStream(path);
		//in = getPrimaryByDoc(doc);
		HSSFWorkbook wk = new HSSFWorkbook(in);
		HSSFSheet procducsheet = wk.getSheet("产品库清单");
		List products = getProductList(procducsheet);
		System.out.println(products.size());
		for (int n = 0; n <products.size(); n++) {
			System.out.println(products.get(n));
		HSSFSheet sheet = wk.getSheet("PLM系统权限表");
		//System.out.println(sheet.getSheetName());
		for (int i = 1; i <sheet.getPhysicalNumberOfRows(); i++) { 
            
			Row row = sheet.getRow(i);  
			String productname = "";
			String domainpath = "";
			String typename = "";
			String statename = "";
			String pricipalname = "";
			//String grants = "";
			//String denies = "";
			
			String fullControl = "";
			String read = "";
			String download = "";
			String modify = "";
			String modifierContent = "";
			String revise = "";
			String create = "";
			String moveCreate = "";
			String delete = "";
			String modId = "";
			String modSLabel = "";
			String setState = "";
			String createView = "";
			String changeDomain = "";
			String changeContext = "";
			String changeAccess = "";
			String manager = "";
			
			//Cell cellpn = row.getCell(0);
			Cell celldp = row.getCell(1);
			Cell celltn = row.getCell(2);
			Cell cellsn = row.getCell(3);
			Cell cellpl = row.getCell(4);
			//Cell cellgt = row.getCell(5);
			//Cell celldn = row.getCell(6);
			List list = Arrays.asList(permissionNames);
			AccessPermissionSet grantAccessPerSet = new AccessPermissionSet();
			AccessPermissionSet denyAccessPerSet = new AccessPermissionSet();
			AccessPermissionSet absAccessPerSet = new AccessPermissionSet();
			
			Cell cfullControl = row.getCell(5);
			if (cfullControl != null) {
				fullControl = cfullControl.getStringCellValue().trim();
				if(StringUtils.isNotBlank(fullControl)){
					if(fullControl.equalsIgnoreCase("YES")){
						grantAccessPerSet.add(AccessPermission.ALL);
					}else{
						denyAccessPerSet.add(AccessPermission.ALL);
					}
				}
			}
			Cell cread = row.getCell(6);
			if (cread != null) {
				read = cread.getStringCellValue().trim();
				if(StringUtils.isNotBlank(read)){
					if(read.equalsIgnoreCase("YES")){
						grantAccessPerSet.add(AccessPermission.READ);
					}else{
						denyAccessPerSet.add(AccessPermission.READ);
					}
				}
			}
			Cell cdownload = row.getCell(7);
			if (cdownload != null) {
				download = cdownload.getStringCellValue().trim();
				if(StringUtils.isNotBlank(download)){
					if(download.equalsIgnoreCase("YES")){
						grantAccessPerSet.add(AccessPermission.DOWNLOAD);
					}else{
						denyAccessPerSet.add(AccessPermission.DOWNLOAD);
					}
				}
			}
			Cell cmodify = row.getCell(8);
			if (cmodify != null) {
				modify = cmodify.getStringCellValue().trim();
				if(StringUtils.isNotBlank(modify)){
					if(modify.equalsIgnoreCase("YES")){
						grantAccessPerSet.add(AccessPermission.MODIFY);
					}else{
						denyAccessPerSet.add(AccessPermission.MODIFY);
					}
				}
			}
			Cell cmodifierContent = row.getCell(9);
			if (cmodifierContent != null) {
				modifierContent = cmodifierContent.getStringCellValue().trim();
				if(StringUtils.isNotBlank(modifierContent)){
					if(modifierContent.equalsIgnoreCase("YES")){
						grantAccessPerSet.add(AccessPermission.MODIFY_CONTENT);
					}else{
						denyAccessPerSet.add(AccessPermission.MODIFY_CONTENT);
					}
				}
			}
			Cell crevise = row.getCell(10);
			if (crevise != null) {
				revise = crevise.getStringCellValue().trim();
				if(StringUtils.isNotBlank(revise)){
					if(revise.equalsIgnoreCase("YES")){
						grantAccessPerSet.add(AccessPermission.REVISE);
					}else{
						denyAccessPerSet.add(AccessPermission.REVISE);
					}
				}
			}
			Cell ccreate = row.getCell(11);
			if (ccreate != null) {
				create = ccreate.getStringCellValue().trim();
				if(StringUtils.isNotBlank(create)){
					if(create.equalsIgnoreCase("YES")){
						grantAccessPerSet.add(AccessPermission.CREATE);
					}else{
						denyAccessPerSet.add(AccessPermission.CREATE);
					}
				}
			}
			Cell cmoveCreate = row.getCell(12);
			if (cmoveCreate != null) {
				moveCreate = cmoveCreate.getStringCellValue().trim();
				if(StringUtils.isNotBlank(moveCreate)){
					if(moveCreate.equalsIgnoreCase("YES")){
						grantAccessPerSet.add(AccessPermission.CREATE_BY_MOVE);
					}else{
						denyAccessPerSet.add(AccessPermission.CREATE_BY_MOVE);
					}
				}
			}
			Cell cdelete = row.getCell(13);
			if (cdelete != null) {
				delete = cdelete.getStringCellValue().trim();
				if(StringUtils.isNotBlank(delete)){
					if(delete.equalsIgnoreCase("YES")){
						grantAccessPerSet.add(AccessPermission.DELETE);
					}else{
						denyAccessPerSet.add(AccessPermission.DELETE);
					}
				}
			}
			Cell cmodId = row.getCell(14);
			if (cmodId != null) {
				modId = cmodId.getStringCellValue().trim();
				if(StringUtils.isNotBlank(modId)){
					if(modId.equalsIgnoreCase("YES")){
						grantAccessPerSet.add(AccessPermission.MODIFY_IDENTITY);
					}else{
						denyAccessPerSet.add(AccessPermission.MODIFY_IDENTITY);
					}
				}
			}
			Cell cmodSLabel = row.getCell(15);
			if (cmodSLabel != null) {
				modSLabel = cmodSLabel.getStringCellValue().trim();
				if(StringUtils.isNotBlank(modSLabel)){
					if(modSLabel.equalsIgnoreCase("YES")){
						grantAccessPerSet.add(AccessPermission.MODIFY_SECURITY_LABELS);
					}else{
						denyAccessPerSet.add(AccessPermission.MODIFY_SECURITY_LABELS);
					}
				}
			}
			Cell csetState = row.getCell(16);
			if (csetState != null) {
				setState = csetState.getStringCellValue().trim();
				if(StringUtils.isNotBlank(setState)){
					if(setState.equalsIgnoreCase("YES")){
						grantAccessPerSet.add(AccessPermission.SET_STATE);
					}else{
						denyAccessPerSet.add(AccessPermission.SET_STATE);
					}
				}
			}
			Cell ccreateView = row.getCell(17);
			if (ccreateView != null) {
				createView = ccreateView.getStringCellValue().trim();
				if(StringUtils.isNotBlank(createView)){
					if(createView.equalsIgnoreCase("YES")){
						grantAccessPerSet.add(AccessPermission.NEW_VIEW_VERSION);
					}else{
						denyAccessPerSet.add(AccessPermission.NEW_VIEW_VERSION);
					}
				}
			}
			Cell cchangeDomain = row.getCell(18);
			if (cchangeDomain != null) {
				changeDomain = cchangeDomain.getStringCellValue().trim();
				if(StringUtils.isNotBlank(changeDomain)){
					if(changeDomain.equalsIgnoreCase("YES")){
						grantAccessPerSet.add(AccessPermission.CHANGE_DOMAIN);
					}else{
						denyAccessPerSet.add(AccessPermission.CHANGE_DOMAIN);
					}
				}
			}
			Cell cchangeContext = row.getCell(19);
			if (cchangeContext != null) {
				changeContext = cchangeContext.getStringCellValue().trim();
				if(StringUtils.isNotBlank(changeContext)){
					if(changeContext.equalsIgnoreCase("YES")){
						grantAccessPerSet.add(AccessPermission.CHANGE_CONTEXT);
					}else{
						denyAccessPerSet.add(AccessPermission.CHANGE_CONTEXT);
					}
				}
			}
			Cell cchangeAccess = row.getCell(20);
			if (cchangeAccess != null) {
				changeAccess = cchangeAccess.getStringCellValue().trim();
				if(StringUtils.isNotBlank(changeAccess)){
					if(changeAccess.equalsIgnoreCase("YES")){
						grantAccessPerSet.add(AccessPermission.CHANGE_PERMISSIONS);
					}else{
						denyAccessPerSet.add(AccessPermission.CHANGE_PERMISSIONS);
					}
				}
			}
			Cell cmanager = row.getCell(21);
			if (cmanager != null) {
				manager = cmanager.getStringCellValue().trim();
				if(StringUtils.isNotBlank(manager)){
					if(manager.equalsIgnoreCase("YES")){
						grantAccessPerSet.add(AccessPermission.ADMINISTRATIVE);
					}else{
						denyAccessPerSet.add(AccessPermission.ADMINISTRATIVE);
					}
				}
			}
			
			/*if (cellpn != null) {
				productname = cellpn.getStringCellValue().trim();
				//System.out.println(productname);
			} else {
				throw new WTException("PLM系统权限表" +"第"+ i + "行，产品库名称为空！");
			}*/
			productname = (String) products.get(n);
			if (celldp != null) {
				domainpath = celldp.getStringCellValue().trim();
			} else {
				throw new WTException("产品库"+productname+"PLM系统权限表" +"第"+ i + "行，域路径为空！");
			}
			if (celltn != null) {
				typename = celltn.getStringCellValue().trim();
			} else {
				throw new WTException("产品库"+productname+"PLM系统权限表" +"第"+ i + "行，类型名称为空！");
			}
			if (cellsn != null) {
				statename = cellsn.getStringCellValue().trim();
			} else {
				throw new WTException("产品库"+productname+"PLM系统权限表" +"第"+ i + "行，生命周期状态为空！");
			}
			if (cellpl != null) {
				pricipalname = cellpl.getStringCellValue().trim();
			} else {
				throw new WTException("产品库"+productname+"PLM系统权限表" +"第"+ i + "行，承担者为空！");
			}
			
			ReferenceFactory factory = new ReferenceFactory();
			WTContainer container = getContainer(productname.trim());
			if (container==null) {
				throw new WTException("产品库"+productname+"PLM系统权限表" +"第"+ i + "行，产品库不存在");
			}
			WTContainerRef containerRef = (WTContainerRef) factory.getReference(container);
			if (!domainpath.startsWith("/")) {
				domainpath = "/" + domainpath;
			}
			//AdminDomainRef adr = getAdminDomainRef(containerRef, domainpath);
			AdminDomainRef adr =createDomain(domainpath, containerRef);
			if (adr==null) {
				throw new WTException("产品库"+productname+"PLM系统权限表" +"第"+ i + "行，域路径不存在");
			}
			String type = "";// checkObjectType(typename);

			if (typeMap.containsKey(typename)) {
				type = typeMap.get(typename);
				//System.out.println("typeMap:\t" + type);
				type = checkObjectType(type);
				//System.out.println("CheckObject:\t" + type);
			}
			if (type.isEmpty()) {
				throw new WTException("产品库"+productname+"PLM系统权限表" +"第"+ i + "行，系统不中存在类型【" + typename + "】");
			}
			State state = null;
			
			if(pricipalname ==null || pricipalname.isEmpty()){
				throw new WTException("产品库"+productname+"PLM系统权限表" +"第"+ i + "行，承担者为非法值！");
			}
			
			if(mapRole.containsKey(pricipalname)){
				pricipalname = mapRole.get(pricipalname);
			}
			WTPrincipalReference pref = checkPrincipal(container, pricipalname);
			if (pref==null) {
				throw new WTException("产品库"+productname+"PLM系统权限表" +"第"+ i + "行，承担者不存在！");
			}
			
			
			if (!("ALL".equalsIgnoreCase(statename) || "全部".equals(statename))) {
				if (mapState.containsKey(statename)) {
					state = mapState.get(statename);
				} else {
					throw new WTException("产品库"+productname+"PLM系统权限表" +"第"+ i + "行，生命周期状态为非法值！");
				}

			}
			
			AccessControlRule acr = AccessControlHelper.manager.getAccessControlRule(adr, type, state, pref, false);
			if (acr == null) {
				if(!grantAccessPerSet.isEmpty()){
					AccessControlHelper.manager.createAccessControlRule(adr, type, state, pref, false, grantAccessPerSet,
						denyAccessPerSet, absAccessPerSet);
				}
			} else {
				if(!grantAccessPerSet.isEmpty()||!denyAccessPerSet.isEmpty()){
					AccessControlHelper.manager.updateAccessControlRule(adr, type, state, pref, false, grantAccessPerSet,
						denyAccessPerSet, absAccessPerSet);
				}else{
					AccessControlHelper.manager.deleteAccessControlRule(adr, type, state, pref, false);
				}
			}
		}
		}
		return "";
	}

	/**
	 * 获取文档主内容
	 * @param doc
	 * @return
	 * @throws WTException
	 */
	public static InputStream getPrimaryByDoc(WTDocument doc) throws WTException {
		InputStream is = null;

		WTUser cuser = (WTUser) SessionHelper.manager.getPrincipal();
		SessionHelper.manager.setAdministrator();
		QueryResult qr = ContentHelper.service.getContentsByRole(doc, ContentRoleType.PRIMARY);
		ApplicationData ap = null;
		while (qr.hasMoreElements()) {
			Object o = qr.nextElement();
			if (o instanceof ApplicationData) {
				ap = (ApplicationData) o;
				qr = null;
				break;
			}
		}

		if (ap == null) {
			throw new WTException("无法获取文档的主内容");
		}
		String filename = ap.getFileName();
		//System.out.println("文档主内容文件名：\t" + filename);
		if (!filename.endsWith(".xls")) {
			throw new WTException("文件\t[" + filename + "]文件格式不正确！");
		}
		is = ContentServerHelper.service.findContentStream(ap);
		SessionHelper.manager.setPrincipal(cuser.getName());
		return is;
	}


	/**
	 * 检查域
	 * 
	 * @param mapDomain
	 * @param container
	 * @param containerName
	 * @param iRow
	 * @param domainPath
	 * @return
	 */
	private static String checkDomain(WTContainer container, String containerName, String domainPath) {

		try {
			WTContainerRef containerRef = WTContainerRef.newWTContainerRef(container);
			AdminDomainRef domainRef = getAdminDomainRef(containerRef, domainPath);

		} catch (WTException e) {
			e.printStackTrace();

		}

		return "";
	}

	/**
	 * 根据容器和域名得到域引用
	 * 
	 * @param containerRef
	 * @param sDomainName
	 * @return
	 * @throws WTException
	 */
	private static AdminDomainRef getAdminDomainRef(WTContainerRef containerRef, String sDomainName)
			throws WTException {

		AdministrativeDomain administrativedomain = AdministrativeDomainHelper.manager.getDomain(sDomainName,
				containerRef);
		if (administrativedomain == null)
			return null;

		return AdminDomainRef.newAdminDomainRef(administrativedomain);
	}

	/**
	 * 获取上下文
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

	/**
	 * 承担者检查
	 * 
	 * @param mapPrincipal
	 * @param iRow
	 * @param Principal
	 * @return
	 */
	private static WTPrincipalReference checkPrincipal(WTContainer container, String principal) {
		
		ReferenceFactory ref = new ReferenceFactory();

		WTPrincipalReference principalRef = null;
		if (principal.equals("OWNER")) {
			principalRef = WTPrincipalReference.OWNER;
		} else if (principal.equals("ALL")) {
			principalRef = WTPrincipalReference.ALL;
		} else {
			try {
				ContainerTeam team = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) container);
				Vector vector = team.getRoles();
				Role role = Role.toRole(principal);
				if(!vector.contains(role)){
					team.addPrincipal(Role.toRole(principal), null);
				}
				
				WTGroup group = ContainerTeamHelper.service.findContainerTeamGroup((ContainerTeamManaged) container,
						ContainerTeamHelper.ACCESS_GROUPS, principal);
				if (group == null) {
					group = (WTGroup) OrganizationServicesHelper.manager.getPrincipal(principal);
					//System.out.println(group);
					if (group == null) {

						System.out.println("Group is null");
						return principalRef;
					}
				}

				principalRef = (WTPrincipalReference) ref.getReference(group);

			} catch (WTException e) {
				e.printStackTrace();
			}
		}

		if (principalRef == null) {
			System.out.println("PrincipalRef is null.");
		} else {
			System.out.println(principalRef);
		}

		return principalRef;
	}

	/**
	 * 检查字符串中是否有中文
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isContainChinese(String str) {

		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(str);
		if (m.find()) {
			return true;
		}
		return false;
	}

	/**
	 * 对象类型检查
	 * 
	 * @param mapObjectType
	 * @param iRow
	 * @param type
	 * @return
	 */
	private static String checkObjectType(String type) {
		//

		String typeId = type;

		if (type.indexOf("|") < 0 && type.indexOf(".") < 0) {
			try {
				WTTypeDefinitionMaster typeMaster = getTypeDefinitionMaster(type);
				System.out.println(typeMaster);
				if (typeMaster != null) {
					typeId = typeMaster.getIntHid();

				}
			} catch (WTException e) {
				e.printStackTrace();
			}
		}
		// 增加前缀
		if (!typeId.startsWith("WCTYPE|")) {
			typeId = "WCTYPE|" + typeId;
		}

		String persisted_type = null;
		String external_type_id = TypedUtility.getExternalTypeIdentifier(typeId);

		if (external_type_id == null) {
			// type id is not a persisted type, see if it is a valid
			// external or logical type ID
			persisted_type = TypedUtility.getPersistedType(typeId);
		} else if (typeId.equals(TypedUtility.getPersistedType(external_type_id))) {
			// type id is a persisted type and type is not deleted
			persisted_type = typeId;
		}

		if (persisted_type == null) {
			System.out.println("persisted_type is null");
		} else {
			System.out.println(persisted_type);
		}

		return persisted_type;
	}

	/**
	 * 得到类型定义Master
	 * 
	 * @param type
	 * @return
	 * @throws WTException
	 */
	private static WTTypeDefinitionMaster getTypeDefinitionMaster(String type) throws WTException {
		QuerySpec qs = new QuerySpec(WTTypeDefinitionMaster.class);
		qs.appendSearchCondition(new SearchCondition(WTTypeDefinitionMaster.class,
				WTTypeDefinitionMaster.DISPLAY_NAME_KEY, SearchCondition.EQUAL, type));
		System.out.println(qs);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		while (qr.hasMoreElements()) {
			return (WTTypeDefinitionMaster) qr.nextElement();
		}

		return null;
	}

	/**
	 * 得到类型定义Master
	 * 
	 * @param type
	 * @return
	 * @throws WTException
	 */
	private static Map<String, String> getAllTypeDefinitionMaster() throws WTException {
		Map<String, String> typeDisplayInternalMap = new HashMap<>();
		QuerySpec qs = new QuerySpec(WTTypeDefinitionMaster.class);

		QueryResult qr = PersistenceHelper.manager.find(qs);
		while (qr.hasMoreElements()) {
			WTTypeDefinitionMaster typemaster = (WTTypeDefinitionMaster) qr.nextElement();
			String internalName = typemaster.getIntHid();

			TypeIdentifier typeIdentifier = TypedUtilityServiceHelper.service.getTypeIdentifier(internalName);

			String dispalyName = TypedUtility.getLocalizedTypeName(typeIdentifier, Locale.CHINA);
			typeDisplayInternalMap.put(dispalyName, internalName);
			// System.out.println(dispalyName + "\t" + internalName);
		}

		return typeDisplayInternalMap;
	}

	/**
	 * 获取全部产品库、存储库
	 * 
	 * @return
	 */
	public static List<WTContainer> getAllContainer() {
		List<WTContainer> result = new ArrayList<>();
		WTContainer wtc = null;
		QuerySpec qs;
		try {
			qs = new QuerySpec(WTContainer.class);

			QueryResult qr = PersistenceHelper.manager.find(qs);

			while (qr.hasMoreElements()) {
				wtc = (WTContainer) qr.nextElement();
				if (wtc instanceof WTLibrary || wtc instanceof PDMLinkProduct) {
					System.out.println(wtc.getName());
					result.add(wtc);
				}
				// return wtc;
			}

		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 根据上下文获取该上下文下所有的域
	 * 
	 * @param container
	 * @return
	 * @throws WTException
	 */
	public static ArrayList getChildDomainByContainer(WTContainer container) throws WTException {
		ArrayList result = null;
		ReferenceFactory ref = new ReferenceFactory();
		AdministrativeDomain ad = AdministrativeDomainHelper.manager.getDomain("/Default",
				(WTContainerRef) ref.getReference(container));
		// System.out.println(ad);
		if (ad == null) {
			return null;
		} else {
			result = AdministrativeDomainHelper.manager.getChildDomains(AdminDomainRef.newAdminDomainRef(ad));
			result.add(AdminDomainRef.newAdminDomainRef(ad));
		}
		return result;
	}

	private static AdminDomainRef createDomain(String domainPath, WTContainerRef containerRef) throws WTException, WTPropertyVetoException {
		AdministrativeDomain domain = AdministrativeDomainHelper.manager.getDomain(domainPath, containerRef);
		if (domain != null) {
			return AdminDomainRef.newAdminDomainRef(domain);
		}

		// /Default/domainA/domainB/domainC
		if (domainPath.lastIndexOf('/') > 0) {
			String parentDomainPath = domainPath.substring(0, domainPath.lastIndexOf('/'));
			String domainName = domainPath.substring(domainPath.lastIndexOf('/') + 1);
			AdministrativeDomain parentDomain = AdministrativeDomainHelper.manager.getDomain(parentDomainPath, containerRef);
			if (parentDomain == null) {
				parentDomain = (AdministrativeDomain) createDomain(parentDomainPath, containerRef).getObject();

			}
			AdminDomainRef newDomainRef = AdministrativeDomainHelper.manager.createDomain(AdminDomainRef.newAdminDomainRef(parentDomain), domainName, null,
					containerRef);
			return newDomainRef;
		} else {
			throw new WTException("Cannot create domain:" + domainPath + " in container " + containerRef);
		}

	}
	
	/**
	 * 导出策略权限
	 * @return
	 * @throws WTException
	 * @throws IOException
	 */
	public static HSSFWorkbook getAllAccessControlRules() throws WTException, IOException {
		// InputStream in;
		// in = new FileInputStream("E:\\access.xls");
		HSSFWorkbook wk = new HSSFWorkbook();
		HSSFSheet sheet = wk.createSheet();
		int rowNum = 0;

		Row headrow = sheet.createRow(rowNum);
		Cell headContainer = headrow.createCell(0);
		Cell headPath = headrow.createCell(1);
		Cell headType = headrow.createCell(2);
		Cell headState = headrow.createCell(3);
		Cell headPrincipal = headrow.createCell(4);
		Cell headGrant = headrow.createCell(5);
		Cell headDeny = headrow.createCell(6);

		headContainer.setCellValue("库位置");
		headPath.setCellValue("域");
		headType.setCellValue("类型");
		headState.setCellValue("状态");
		headPrincipal.setCellValue("参与者");
		headGrant.setCellValue("准予权限");
		headDeny.setCellValue("拒绝权限");

		rowNum++;

		List<WTContainer> containers = getAllContainer();
		for (WTContainer container : containers) {
			// System.out.println(container.getName());
			ArrayList domains = getChildDomainByContainer(container);
			// System.out.println(domains);
			if (domains != null) {
				// System.out.println(domains.size());
				for (int i = 0; i < domains.size(); i++) {

					AdminDomainRef ad = (AdminDomainRef) domains.get(i);
					System.out.println(ad.getName());
					Collection<AccessControlRule> accessControls = AccessControlHelper.manager
							.getAccessControlRules(ad);
					Iterator<AccessControlRule> it = accessControls.iterator();
					while (it.hasNext()) {
						AccessControlRule acr = it.next();
						String state = acr.getState() == null ? "All" : acr.getState().getDisplay(Locale.CHINA);
						AccessPermissionSet grants = acr.getGrantPermissions();
						AccessPermissionSet denys = acr.getDenyPermissions();
						String typename = TypedUtilityServiceHelper.service.getTypeIdentifier(acr.getTypeId())
								.getTypename();
						
						String internalName = acr.getTypeId();

						TypeIdentifier typeIdentifier = TypedUtilityServiceHelper.service.getTypeIdentifier(internalName);

						String dispalyName = TypedUtility.getLocalizedTypeName(typeIdentifier, Locale.CHINA);
						
//						System.out.println(state
//						 +"\t"+acr.getTypeId()+"\t"+acr.getPrincipalRef().getPrincipal().getPrincipalDisplayIdentifier()
//						 +"\t授予权限："+grants.getDisplay(Locale.CHINA));
						Row row = sheet.createRow(rowNum);
						Cell cellContainer = row.createCell(0);
						Cell cellPath = row.createCell(1);
						Cell cellType = row.createCell(2);
						Cell cellState = row.createCell(3);
						Cell cellPrincipal = row.createCell(4);
						Cell cellGrant = row.createCell(5);
						Cell cellDeny = row.createCell(6);

						cellContainer.setCellValue(container.getName());
						cellPath.setCellValue(AdministrativeDomainHelper.manager.getDisplayPathOfDomain(ad));
						cellType.setCellValue(dispalyName);
						cellState.setCellValue(state);
						cellPrincipal
								.setCellValue(acr.getPrincipalRef().getPrincipal().getPrincipalDisplayIdentifier());
						if (grants != null) {
							String grantName = grants.getDisplay(Locale.CHINA);
							cellGrant.setCellValue(grantName.substring(1, grantName.length() - 1));
						}

						if (denys != null) {
							String denyName = denys.getDisplay(Locale.CHINA);
							cellDeny.setCellValue(denyName.substring(1, denyName.length() - 1));
						}

						rowNum++;
					}
				}
			}
		}

		return wk;

		/*
		 * FileOutputStream os=new FileOutputStream("E:\\ExportAccess.xls");
		 * wk.write(os); os.flush(); os.close();
		 */
	}
	
	/**
	 * 获取系统中所有的生命周期状态显示名称与状态对象的Map
	 * @return
	 * @throws WTException
	 */
	public static Map<String, State> allStateMap() throws WTException{
		State[] states = LifeCycleHelper.service.allStates();
		Map<String,State> map = new HashMap<>();
		for(State state:states){
			map.put(state.getDisplay(Locale.CHINA), state);
			System.out.println(state.getDisplay(Locale.CHINA)+"\t"+state.toString());
		}
		return map;
	}
	
	/**
	 * 获取系统中所有角色的显示名称与内部名称的MAP
	 * @return
	 */
	public static Map<String,String> allRoleMap(){
		Map<String,String> map = new HashMap<>();
		Role roles[] = Role.getRoleSet();
		for (Role role:roles) {
			map.put(role.getDisplay(Locale.CHINA), role.toString());
			System.out.println(role.getDisplay(Locale.CHINA)+"\t"+role.toString());
		}
		return map;
	}
	
	public static List<String> getProductList(HSSFSheet sheet) throws WTException{
		List<String> producs = new ArrayList<>();
		if(sheet ==null){
			throw new WTException("找不到表产品库清单。");
		}
		for (int i = 1; i <sheet.getPhysicalNumberOfRows(); i++) {             
			Row row = sheet.getRow(i);  
			String productname = "";
			String isUpdate = "";
			Cell cproductName = row.getCell(0);
			Cell cisupdate = row.getCell(1);
			if (cproductName != null) {
				productname = cproductName.getStringCellValue().trim();				
			}
			
			if (cisupdate != null) {
				isUpdate = cisupdate.getStringCellValue().trim();				
			}
			if(StringUtils.isNotBlank(productname)){
				if(StringUtils.isNotBlank(isUpdate)){
					if(isUpdate.equalsIgnoreCase("是")){
						producs.add(productname);
					}
				}				
			}
		}
		return producs;		
	}
}
