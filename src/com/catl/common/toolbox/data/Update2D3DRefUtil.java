package com.catl.common.toolbox.data;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
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

import com.catl.common.util.DocUtil;
import com.catl.ecad.load.ObjectTypeUtil1;
import com.catl.ecad.utils.CommonUtil;
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
import wt.epm.EPMApplicationType;
import wt.epm.EPMContextHelper;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.structure.EPMReferenceLink;
import wt.epm.structure.EPMReferenceType;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTCollection;
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
import wt.pom.PersistenceException;
import wt.project.Role;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.team.Team;
import wt.team.TeamHelper;
import wt.team.TeamManaged;
import wt.type.TypedUtility;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTInvalidParameterException;
import wt.util.WTPropertyVetoException;
import wt.vc.wip.WorkInProgressException;
import wt.vc.wip.WorkInProgressHelper;

/**
 * 
 * 更新产品库团队
 *
 */
public class Update2D3DRefUtil implements RemoteAccess {

	private final static String[] sheetnames = new String[] { "移除关系", "创建关系" };

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
		RemoteMethodServer.getDefault().invoke("updateTeam", Update2D3DRefUtil.class.getName(), null, clazz, params);
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

		InputStream in = new FileInputStream(path);
		// in = getPrimaryByDoc(doc);
		Workbook wk = null;// new Workbook(in);
		if (path.substring(path.lastIndexOf(".") + 1, path.length()).equals("xlsx")) {
			wk = new XSSFWorkbook(in);
		} else {
			wk = new HSSFWorkbook(new POIFSFileSystem(in));
		}

		for (String sheetname : sheetnames) {
			Sheet sheet = wk.getSheet(sheetname);

			if (sheet != null) {
				String operation = "";				
				
				for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {

					Row row = sheet.getRow(i);
					String d2 = "";
					
					String d3 = "";

					Cell d2cell = row.getCell(1);
					if (d2cell != null) {
						d2 = d2cell.getStringCellValue().trim().toUpperCase();
						if (StringUtils.isBlank(d2)) {
							continue;
						}
					}

					Cell d3Cell = row.getCell(0);
					if (d3Cell != null) {
						d3 = d3Cell.getStringCellValue().trim().toUpperCase();
						if (StringUtils.isBlank(d3)) {
							continue;
						}
					}
					if (sheet.getSheetName().equals("移除关系")) {
						operation = "r";
						doLoad(d3, d2, operation);
					}else if(sheet.getSheetName().equals("创建关系")){
						EPMDocument epm3d = CommonUtil.getEPMDocumentByNumber(d3);
						EPMDocument epm2d = CommonUtil.getEPMDocumentByNumber(d2);
						if(epm3d == null){
							throw new WTException("3D"+epm3d.getNumber()+"图档在系统中不存在，或者您没有权限访问该对象！");
						}
						if(epm2d == null){
							throw new WTException("2D"+epm2d.getNumber()+"图档在系统中不存在，或者您没有权限访问该对象！");
						}
						createRefLink(epm3d, epm2d);
					}
					
					
				}

			}

		}

		return "";
	}
	
	/**
	 * 创建EPM之间的参考关系
	 * @throws WTException 
	 * @throws PersistenceException 
	 * @throws WTPropertyVetoException 
	 * @throws WorkInProgressException 
	 */
	public static void createRefLink(EPMDocument epm3d, EPMDocument epm2d) throws WorkInProgressException, WTPropertyVetoException, PersistenceException, WTException {
		boolean enfored = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			EPMContextHelper.setApplication(EPMApplicationType.toEPMApplicationType("EPM"));
			QueryResult qr = PersistenceHelper.manager.find(EPMReferenceLink.class, epm2d, EPMReferenceLink.REFERENCED_BY_ROLE, epm3d.getMaster());
			if(qr.hasMoreElements()){
				return ;
			}
			epm2d = (EPMDocument) CommonUtil.checkoutObject(epm2d);
			WTCollection objects = new WTArrayList();
			EPMReferenceLink epmRefLink = EPMReferenceLink.newEPMReferenceLink(epm2d,
					(EPMDocumentMaster) epm3d.getMaster());
			epmRefLink.setReferenceType(EPMReferenceType.toEPMReferenceType("DRAWING"));
			epmRefLink.setDepType(4);

			objects.add(epmRefLink);

			PersistenceHelper.manager.save(objects);
			epm2d = (EPMDocument) CommonUtil.checkinObject(epm2d, "");
		} catch (WTInvalidParameterException | WTPropertyVetoException | WTException e) {
			
			WorkInProgressHelper.service.undoCheckout(epm2d);
			throw new WTException("建立3D"+epm3d.getNumber()+"与2D"+epm2d.getNumber()+"图参考关系时出错，请检查是否有权限修改2D图！");
		}finally{
			SessionServerHelper.manager.setAccessEnforced(enfored);
		}

	}
	
	public static void doLoad(String epm3DNumber,String epm2DNumber,String command) throws WTException, IOException{
    	FileWriter file = new FileWriter("/data/Delete2D3DReference.txt");
        BufferedWriter writer = new BufferedWriter(file);
        
    	EPMDocument epm3D = DocUtil.getLastestEPMDocumentByNumber(epm3DNumber);
    	List<EPMDocument> epm2DList = getAllEPMDocumentByNumber(epm2DNumber);
		try {
			QuerySpec queryspec = new QuerySpec(EPMReferenceLink.class);

			queryspec.appendWhere(new SearchCondition(EPMReferenceLink.class, "roleBObjectRef.key.id", SearchCondition.EQUAL, epm3D.getMaster().getPersistInfo().getObjectIdentifier().getId()));
			queryspec.appendAnd();
			queryspec.appendOpenParen();
			for(int i = 0; i<epm2DList.size(); i++){
				EPMDocument temp = epm2DList.get(i);
				queryspec.appendWhere(new SearchCondition(EPMReferenceLink.class, "roleAObjectRef.key.id", SearchCondition.EQUAL, temp.getPersistInfo().getObjectIdentifier().getId()));	    		
				if(i != epm2DList.size() -1)
					queryspec.appendOr();
			}
			queryspec.appendCloseParen();
			writer.write(queryspec.toString()+"\n");
			QueryResult queryresult = PersistenceHelper.manager.find(queryspec);
			System.out.println("size\t"+queryresult.size());
			while (queryresult.hasMoreElements()) {
				EPMReferenceLink link = (EPMReferenceLink) queryresult.nextElement();
				String msg = link.getPersistInfo().getObjectIdentifier().getId()+"|"+link.getAsStoredChildName();
				System.out.println(msg+"\t"+command);
				writer.write(msg+"\n");
				if(command.equals("r")){
					PersistenceServerHelper.manager.remove(link);
					writer.write(msg+"删除成功\n");
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}finally{
			writer.flush();
            writer.close();
            file.close();
		}
    }
    
    public static List<EPMDocument> getAllEPMDocumentByNumber(String numStr) {
		try {
			List<EPMDocument> retList = new ArrayList<EPMDocument>();
			QuerySpec queryspec = new QuerySpec(EPMDocument.class);

			queryspec.appendSearchCondition(new SearchCondition(EPMDocument.class, EPMDocument.NUMBER, SearchCondition.EQUAL, numStr));
			QueryResult queryresult = PersistenceHelper.manager.find(queryspec);
			while (queryresult.hasMoreElements()) {
				retList.add((EPMDocument)queryresult.nextElement());
			}
			return retList;
		} catch (WTException e) {
			e.printStackTrace();
		}
		return null;
	}

}
