package com.catl.change.report.workflow;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;

import wt.method.MethodContext;
import wt.pom.WTConnection;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.catl.change.report.Excel2007Handler;
import com.catl.common.util.WCLocationConstants;

public class RejectReportHelper
{
	String processName, rejectDateFrom, rejectDateTo;
	private static Logger log = Logger.getLogger(RejectReportHelper.class.getName());
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	public RejectReportHelper()
	{
	}

	public RejectReportHelper(String processName, String rejectDateFrom, String rejectDateTo)
	{
		this.processName = processName;
		this.rejectDateFrom = rejectDateFrom;
		this.rejectDateTo = rejectDateTo;
	}

	public void generateReport(HttpServletResponse response) throws WTException, WTPropertyVetoException {
		
		log.debug("generateReport processName:" + processName + "rejectDateFrom:"+ rejectDateFrom + "rejectDateTo:" + rejectDateTo);
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
			String currentDate = dateFormat.format((Date) new Timestamp(System.currentTimeMillis()));
			String fileName = "PLM_ProcessReject_" + currentDate + ".xlsx";

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

		} catch (IOException e) {
			throw new WTException(e, e.getLocalizedMessage());
		} finally {
			SessionServerHelper.manager.setAccessEnforced(flag);
		}
	}
	
	private Workbook generateExcel(String fileName) throws WTException {

		String filePathName = WCLocationConstants.WT_CODEBASE + File.separator
				+ "com" + File.separator + "catl" + File.separator
				+ "checkPDFData" + File.separator
				+ "ProcessReject_template.xlsx";

		Excel2007Handler excelHander;
		try {
			excelHander = new Excel2007Handler(filePathName);

			ArrayList<RejectReportBean> beanSetList = new ArrayList<RejectReportBean>();
			try {
				beanSetList = queryPLMProcessReject();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			int rowNum = 0;
			int iCol = 0;
			for (RejectReportBean bean : beanSetList) {
				
				iCol = 0;
				rowNum++;
				excelHander.setStringValue(rowNum, iCol++, bean.getProcessName());
				excelHander.setStringValue(rowNum, iCol++, bean.getRejectLink());
				excelHander.setStringValue(rowNum, iCol++, bean.getApprovalComment());
				excelHander.setStringValue(rowNum, iCol++, bean.getRejectComment());
				excelHander.setStringValue(rowNum, iCol++, bean.getRejectTime());
				excelHander.setStringValue(rowNum, iCol++, bean.getRejectPerson());
				excelHander.setStringValue(rowNum, iCol++, bean.getProcessApplicant());
				
			}
			return excelHander.getWorkbook();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	// PLM流程驳回统计
	private ArrayList<RejectReportBean> queryPLMProcessReject() throws Exception
	{
		ArrayList<RejectReportBean> result = new ArrayList<RejectReportBean>();

		System.out.println("start queryPLMProcessReject.......................");
        MethodContext context = MethodContext.getContext();
        WTConnection wtConn = (WTConnection)context.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try{
        	String sql = "select t.processname processName";						// 流程名称
        	sql += " ,t.activityname rejectLink";									// 驳回环节
        	sql += " ,t.eventlist approvalComment";									//审批意见
        	sql += " ,t.usercomment rejectComment";									// 驳回意见	
        	sql += " ,t.modifystampa2+1/3 rejectTime";								// 驳回时间
        	sql += " ,k.fullname rejectPerson";										// 驳回人
        	sql += " ,k1.fullname processApplicant";								// 流程申请人
            
            sql += " from wfvotingeventaudit t,wtuser k,wfprocess m,wtuser k1";
            sql += " where t.eventlist like '%驳回%'";
            sql += " and t.ida3a5=k.ida2a2  ";
            sql += " and m.wtkey=t.processkey ";
            sql += " and k1.ida2a2=m.ida3b7";
            
            if (!isNullOrBlank(processName)) {
    			if (processName.indexOf("*") >= 0) {
    				processName = replace(processName, "*", "%");
    				sql += " and t.processname like '" + processName + "'";
    			} else {
    				sql += " and t.processname = '" + processName + "'";
    			}
    		}
            if (!isNullOrBlank(rejectDateFrom)) {
            	sql += " and t.modifystampa2+1/3 >= to_date('"+ rejectDateFrom +"', 'YYYY/MM/DD')";
            }
            
            if (!isNullOrBlank(rejectDateTo)) {
            	rejectDateTo = rejectDateTo + " 23:59:59";
            	sql += " and t.modifystampa2+1/3 <= to_date('"+ rejectDateTo +"', 'YYYY/MM/DD hh24:mi:ss')";
            }
            sql += " order by processName";
            
            statement = wtConn.prepareStatement(sql);
            resultSet = statement.executeQuery();
            while(resultSet.next()){
            	RejectReportBean bean = new RejectReportBean();
                bean.setProcessName(resultSet.getString("processName"));
                bean.setRejectLink(resultSet.getString("rejectLink"));
                bean.setApprovalComment(resultSet.getString("approvalComment"));
                bean.setRejectComment(resultSet.getString("rejectComment"));
                bean.setRejectTime(resultSet.getString("rejectTime"));
                bean.setRejectPerson(resultSet.getString("rejectPerson"));
                bean.setProcessApplicant(resultSet.getString("processApplicant"));
                
                result.add(bean);
            }
            
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }finally{
            if(resultSet != null) resultSet.close();
            if(statement != null) statement.close();
            if(wtConn != null && wtConn.isActive()) wtConn.release();
        }
        System.out.println("end queryPLMProcessReject.......................");
	
		return result;
	}
	

	private boolean isNullOrBlank(String param)
	{
		return (param == null || param.trim().equals("") || param.trim().equals("null")) ? true : false;
	}

	private String replace(String mainString, String oldString, String newString)
	{
		if (mainString == null)
		{
			return null;
		}
		int i = mainString.lastIndexOf(oldString);
		if (i < 0)
		{
			return mainString;
		}
		StringBuffer mainSb = new StringBuffer(mainString);
		while (i >= 0)
		{
			mainSb.replace(i, i + oldString.length(), newString);
			i = mainSb.toString().lastIndexOf(oldString, i - 1);
		}
		return mainSb.toString();
	}
}
