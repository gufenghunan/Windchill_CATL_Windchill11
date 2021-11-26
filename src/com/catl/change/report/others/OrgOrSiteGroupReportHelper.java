package com.catl.change.report.others;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.pdmlink.PDMLinkProduct;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.OrderByExpression;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.catl.change.report.Excel2007Handler;
import com.catl.common.util.WCLocationConstants;

public class OrgOrSiteGroupReportHelper
{
	private static Logger log = Logger.getLogger(OrgOrSiteGroupReportHelper.class.getName());
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	private static String EXCHANGECONTAINER = "wt.inf.container.ExchangeContainer";
	private static String ORGCONTAINER = "wt.inf.container.OrgContainer";
	
	public OrgOrSiteGroupReportHelper()
	{
	}

	public void generateReport(HttpServletResponse response) throws WTException, WTPropertyVetoException
	{
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
			String currentDate = dateFormat.format((Date) new Timestamp(System.currentTimeMillis()));
			String fileName = "PLM_OrgSiteGroup_" + currentDate + ".xlsx";
			
			Workbook wb = generateExcel(fileName);
			String filename = java.net.URLEncoder.encode(fileName, "UTF-8");
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
			
			OutputStream os = response.getOutputStream();
            wb.write(os);
            
            response.setStatus(HttpServletResponse.SC_OK);
			response.flushBuffer();
			
            os.flush();
            os.close();
			
		} catch (IOException e)
		{
			throw new WTException(e, e.getLocalizedMessage());
		} finally
		{
			SessionServerHelper.manager.setAccessEnforced(flag);
		}
	}

	private Workbook generateExcel(String fileName) throws WTException {
		
		String filePathName = WCLocationConstants.WT_CODEBASE+File.separator
				+"com"+File.separator+"catl"+File.separator
				+"checkPDFData"+File.separator+"orgOrSiteFroup_template.xlsx";
		
		Excel2007Handler excelHander;
		try {
			
			//获取系统中所有的用户
			List<WTUser> userlist = getAllUsers();
			
			excelHander = new Excel2007Handler(filePathName);
			
			List<WTGroup> grouplist = getWTGroup();
			
			int rowNum = 1;
			for (int i=0; i<grouplist.size(); i++){
				
				WTGroup group = grouplist.get(i);
	       		
	            int iCol = 0;
	            
	            String groupname = group.getName();
	            String local = group.getContainerName();
	            
	            if ("CATL".equals(groupname) && "站点".equals(local)){
	            	continue;
	            }
	            
	            Enumeration users = group.members();
	            
	            if (users.hasMoreElements()){
	            	
	            	while (users.hasMoreElements()) {
	            		
	            		iCol = 0;
	            		rowNum = rowNum + 1;
	    				WTUser principal = (WTUser) users.nextElement();
	    				
	    				excelHander.setStringValue(rowNum,iCol++, groupname);
	                	excelHander.setStringValue(rowNum,iCol++, local);
	                	excelHander.setStringValue(rowNum,iCol++, principal.getName());
	                	excelHander.setStringValue(rowNum,iCol++, principal.getFullName());
	                	excelHander.setStringValue(rowNum,iCol++, principal.getEMail());
	                	
	                	//将已经在组里面的用户剔除
	                	if (userlist.contains(principal)){
	                		
	                		userlist.remove(principal);
	                	}
	    			}
	            } else {
	            	
	            	rowNum = rowNum + 1;
	            	
	            	excelHander.setStringValue(rowNum,iCol++, groupname);
	            	excelHander.setStringValue(rowNum,iCol++, local);
	            	excelHander.setStringValue(rowNum,iCol++, "");
	            	excelHander.setStringValue(rowNum,iCol++, "");
	            	excelHander.setStringValue(rowNum,iCol++, "");
	            }
			}
			
			//用户没有在组里的情况
			if (userlist.size() > 0){
				
				for (WTUser user : userlist){
					
					int iCol = 0;
					rowNum = rowNum + 1;
					excelHander.setStringValue(rowNum,iCol++, "");
	            	excelHander.setStringValue(rowNum,iCol++, "");
	            	excelHander.setStringValue(rowNum,iCol++, user.getName());
	            	excelHander.setStringValue(rowNum,iCol++, user.getFullName());
	            	excelHander.setStringValue(rowNum,iCol++, user.getEMail());
				}
				
			}
			return excelHander.getWorkbook();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	public static List<WTGroup> getWTGroup() throws WTException {
    	
		List<WTGroup> grouplist = new ArrayList<WTGroup>();
		QuerySpec qs= new QuerySpec(WTGroup.class);
		
		qs.appendOpenParen();
		qs.appendSearchCondition(new SearchCondition(WTGroup.class, WTGroup.CONTAINER_REFERENCE + ".key.classname", SearchCondition.EQUAL, EXCHANGECONTAINER));
		qs.appendOr();
        qs.appendSearchCondition(new SearchCondition(WTGroup.class, WTGroup.CONTAINER_REFERENCE + ".key.classname", SearchCondition.EQUAL, ORGCONTAINER));
        qs.appendCloseParen();
        
        qs.appendAnd();
        qs.appendSearchCondition(new SearchCondition(WTGroup.class, WTGroup.INTERNAL, SearchCondition.IS_FALSE));
        qs.appendAnd();
        qs.appendSearchCondition(new SearchCondition(WTGroup.class, WTGroup.DISABLED, SearchCondition.IS_FALSE));
        
        qs.appendOrderBy(WTGroup.class, WTGroup.CONTAINER_REFERENCE + ".key.classname", true);
        qs.appendOrderBy(WTGroup.class, WTGroup.NAME, false);
        
//        ClassAttribute clsAttr = new ClassAttribute(PDMLinkProduct.class, PDMLinkProduct.MODIFY_TIMESTAMP);
//        OrderBy order = new OrderBy((OrderByExpression) clsAttr, true);
//        qus.appendOrderBy(order);
        
		QueryResult qr = PersistenceHelper.manager.find(qs);
		while(qr.hasMoreElements()){
			
			WTGroup group = (WTGroup) qr.nextElement();
			grouplist.add(group);
			
		}
		return grouplist;
	}
	
	public static List<WTUser> getAllUsers() throws WTException {
		
    	List<WTUser> userlist = new ArrayList<WTUser>();
    	Enumeration enumPrin = OrganizationServicesHelper.manager.allUsers();

		while (enumPrin.hasMoreElements()) {
			WTUser principal = (WTUser) enumPrin.nextElement();
			userlist.add(principal);
		}
		return userlist;
	}
}
