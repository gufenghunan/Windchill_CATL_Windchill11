package com.catl.ecad.load;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
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
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.type.mgmt.server.impl.WTTypeDefinitionMaster;

import wt.access.AccessControlHelper;
import wt.access.AccessControlRule;
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
import wt.org.DirectoryContextProvider;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.pdmlink.PDMLinkProduct;
import wt.project.Role;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.team.Team;
import wt.team.TeamHelper;
import wt.team.TeamManaged;
import wt.type.TypedUtility;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * 
 * 更新产品库团队
 *
 */
public class LoadTeamMemberUtil implements RemoteAccess {

	private final static String[] sheetnames = new String[] { "1-角色-成员更新", "2-角色-组更新", "3-角色更新", "5-移除特定角色" };

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
		Class[] clazz = { String.class };
		Object[] params = { path };
		RemoteMethodServer.getDefault().invoke("updateTeam", LoadTeamMemberUtil.class.getName(), null, clazz,
				params);
		System.out.println("Hello ,I'm here.");

	}

	/**
	 * 根据模板更新产品库团队
	 * 
	 * @param primaryBusinessObject
	 * @return
	 * @throws IOException
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static String updateTeam(String path) throws IOException, WTException, WTPropertyVetoException {
		// public static String importAccessRules(WTObject
		// primaryBusinessObject) throws IOException, WTException {
		Map<String, String> typeMap = ObjectTypeUtil1.getAllTypeDefinitionMap();

		Map<String, Role> mapRole = allRoleMap();

		// WTDocument doc = null;

		/*
		 * if (primaryBusinessObject instanceof WTDocument) { doc = (WTDocument)
		 * primaryBusinessObject; }
		 */

		InputStream in = new FileInputStream(path);
		// in = getPrimaryByDoc(doc);
		Workbook wk = null;//new Workbook(in);
		if(path.substring(path.lastIndexOf(".")+1,path.length()).equals("xlsx")){
			wk = new XSSFWorkbook(in);
		}else{
			wk = new HSSFWorkbook(new POIFSFileSystem(in));
		}
		Sheet procducsheet = wk.getSheet("4-更新产品库");
		List products = getProductList(procducsheet);
		System.out.println(products.size());
		for (int n = 0; n < products.size(); n++) {
			System.out.println(products.get(n));
			for (String sheetname : sheetnames) {
				Sheet sheet = wk.getSheet(sheetname);
				// System.out.println(sheet.getSheetName());
				for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
					System.out.println(sheetname+"\t"+i+"row");

					Row row = sheet.getRow(i);
					String productname = "";
					String rolename = "";
					String operation = "";
					String members = "";
					Role role = null;
					Cell rolecell = row.getCell(0);
					if (rolecell != null) {
						rolename = rolecell.getStringCellValue().trim();
						if (StringUtils.isNotBlank(rolename)) {
							if (mapRole.containsKey(rolename)) {
								role = mapRole.get(rolename);
							}
						}
					}
					if (sheet.getSheetName().equals("3-角色更新")) {
						Cell opCell = row.getCell(1);
						if (opCell != null) {
							operation = opCell.getStringCellValue().trim();
							if (StringUtils.isBlank(operation)) {
								throw new WTException("操作单元格不能为空");
							}
						}
					} else {
						Cell opCell = row.getCell(2);
						if (opCell != null) {
							operation = opCell.getStringCellValue().trim();
							if (StringUtils.isBlank(operation)) {
								throw new WTException("操作单元格不能为空");
							}
						}

						Cell memberCell = row.getCell(1);
						if (memberCell != null) {
							if(memberCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
								members = String.valueOf(memberCell.getNumericCellValue());
							}else{
								members = memberCell.getStringCellValue().trim();
							}
							if (StringUtils.isBlank(members)) {
								throw new WTException("成员或组单元格不能为空");
							}
						}
					}

					productname = (String) products.get(n);
					System.out.println(productname + "_\t_" + rolename + "_\t_" + operation + "_\t_" + members);
					WTContainer container =getContainer(productname);
					if(container!=null){
						ContainerTeam team = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) container);
						if(sheetname.equals("1-角色-成员更新")){
							if("A".equals(operation)){
								addPrincipal(team, role, members);
							}else if("R".equals(operation)){
								deletePrincipal(team, role, members);
							}
						}else if(sheetname.equals("2-角色-组更新")){
							if("A".equals(operation)){
								addGroup(team, role, members);
							}else if("R".equals(operation)){
								deleteGroup(team, role, members);
							}
						}else if(sheetname.equals("3-角色更新")){
							if("A".equals(operation)){
								addRole(team, role);
							}else if("R".equals(operation)){
								deleteRole(team, role);
							}
						}else if(sheetname.equals("5-移除特定角色")){
							if (StringUtils.isNotBlank(rolename)) {
								if (!mapRole.containsKey(rolename)) {
									role = Role.toRole(rolename);
									deleteRole(team, role);
								}
							}
						}
					}else{
						throw new WTException("产品库【"+productname+"】在系统中不存在!");
					}
				}
			}
		}
		return "";
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
	 * 获取系统中所有角色的显示名称与内部名称的MAP
	 * 
	 * @return
	 */
	public static Map<String, Role> allRoleMap() {
		Map<String, Role> map = new HashMap<>();
		Role roles[] = Role.getRoleSet();
		for (Role role : roles) {
			map.put(role.getDisplay(Locale.CHINA), role);
			System.out.println(role.getDisplay(Locale.CHINA) + "\t" + role.toString());
		}
		return map;
	}

	/**
	 * 获取表格中产品库清单
	 * @param procducsheet
	 * @return
	 * @throws WTException
	 */
	public static List<String> getProductList(Sheet procducsheet) throws WTException {
		List<String> producs = new ArrayList<>();
		if (procducsheet == null) {
			throw new WTException("找不到表产品库清单。");
		}
		for (int i = 1; i < procducsheet.getPhysicalNumberOfRows(); i++) {
			Row row = procducsheet.getRow(i);
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
			if (StringUtils.isNotBlank(productname)) {
				if (StringUtils.isNotBlank(isUpdate)) {
					if (isUpdate.equalsIgnoreCase("是")) {
						producs.add(productname);
					}
				}
			}
		}
		return producs;
	}

	/**
	 * 添加人员到角色
	 * @param team
	 * @param role
	 * @param userStr
	 * @throws WTException
	 */
	public static void addPrincipal(ContainerTeam team, Role role, String userStr) throws WTException {
		if (StringUtils.isNotBlank(userStr)) {
			String[] users = userStr.split(",");
			for (String username : users) {
				WTPrincipal user = OrganizationServicesHelper.manager.getPrincipal(username);
				team.addPrincipal(role, user);
			}
		}
		PersistenceHelper.manager.refresh(team);
	}

	/**
	 * 从团队角色中删除人员
	 * @param team
	 * @param role
	 * @param userStr
	 * @throws WTException
	 */
	public static void deletePrincipal(ContainerTeam team, Role role, String userStr) throws WTException {
		if (StringUtils.isNotBlank(userStr)) {
			String[] users = userStr.split(",");
			for (String username : users) {
				WTPrincipal user = OrganizationServicesHelper.manager.getPrincipal(username);
				team.deletePrincipalTarget(role, user);
			}
		}
		PersistenceHelper.manager.refresh(team);
	}

	/**
	 * 添加团队角色
	 * @param team
	 * @param role
	 * @throws WTException
	 */
	public static void addRole(ContainerTeam team, Role role) throws WTException {
		if (team != null & role != null) {
			team.addPrincipal(role, null);
			PersistenceHelper.manager.refresh(team);
		}
	}

	/**
	 * 删除团队角色
	 * @param team
	 * @param role
	 * @throws WTException
	 */
	public static void deleteRole(ContainerTeam team, Role role) throws WTException {
		if (team != null & role != null) {
			Enumeration members = team.getPrincipalTarget(role);
			if (members.hasMoreElements()) {
				throw new WTException("角色【" + role.getDisplay(Locale.CHINA) + "】中尚有参与者，请移除所有参与者后再删除角色！");
			} else {
				team.deleteRole(role);
				PersistenceHelper.manager.refresh(team);
			}
		}
	}

	/**
	 * 给团队角色添加指定组
	 * @param team
	 * @param role
	 * @param groupStr
	 * @throws WTException
	 */
	public static void addGroup(ContainerTeam team, Role role, String groupStr) throws WTException {
		if (StringUtils.isNotBlank(groupStr)) {
			String[] users = groupStr.split(",");
			for (String username : users) {
				String[] defaultService = OrganizationServicesHelper.manager.getDirectoryServiceNames();
				DirectoryContextProvider dcp = OrganizationServicesHelper.manager.newDirectoryContextProvider(defaultService, new String[] { "subtree" });
				WTGroup group = OrganizationServicesHelper.manager.getGroup(username, dcp);
				team.addPrincipal(role, group);
			}
		}
		PersistenceHelper.manager.refresh(team);
	}

	/**
	 * 删除团队角色中的指定组
	 * @param team
	 * @param role
	 * @param groupStr
	 * @throws WTException
	 */
	public static void deleteGroup(ContainerTeam team, Role role, String groupStr) throws WTException {
		if (StringUtils.isNotBlank(groupStr)) {
			String[] users = groupStr.split(",");
			for (String username : users) {
				String[] defaultService = OrganizationServicesHelper.manager.getDirectoryServiceNames();
				DirectoryContextProvider dcp = OrganizationServicesHelper.manager.newDirectoryContextProvider(defaultService, new String[] { "subtree" });
				WTGroup group = OrganizationServicesHelper.manager.getGroup(username, dcp);
				team.deletePrincipalTarget(role, group);
			}
		}
		PersistenceHelper.manager.refresh(team);
	}
}
