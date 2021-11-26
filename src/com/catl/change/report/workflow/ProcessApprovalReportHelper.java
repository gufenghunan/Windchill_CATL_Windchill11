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

import wt.change2.ChangeException2;
import wt.method.MethodContext;
import wt.pom.WTConnection;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.catl.change.report.Excel2007Handler;
import com.catl.common.util.WCLocationConstants;

public class ProcessApprovalReportHelper
{
	String routerName, approvedDateFrom, approvedDateTo;
	private static Logger log = Logger.getLogger(ProcessApprovalReportHelper.class.getName());
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	public ProcessApprovalReportHelper()
	{
	}

	public ProcessApprovalReportHelper(String routerName, String approvedDateFrom, String approvedDateTo)
	{
		this.routerName = routerName;
		this.approvedDateFrom = approvedDateFrom;
		this.approvedDateTo = approvedDateTo;
	}

	public void generateReport(HttpServletResponse response) throws WTException, WTPropertyVetoException
	{
		log.debug("generateReport routerName:" + routerName + "approvedDateFrom:"+ approvedDateFrom + "approvedDateTo:" + approvedDateTo);
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
			// get system current time add to the report name
			String currentDate = dateFormat.format((Date) new Timestamp(System.currentTimeMillis()));
			String fileName = "PLM_ProcessApproval_" + currentDate + ".xlsx";
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

	private Workbook generateExcel(String fileName) throws ChangeException2, WTException, IOException, WTPropertyVetoException
	{
		
		String filePathName = WCLocationConstants.WT_CODEBASE + File.separator
				+ "com" + File.separator + "catl" + File.separator
				+ "checkPDFData" + File.separator
				+ "ProcessApproval_template.xlsx";

		Excel2007Handler excelHander;
		try {
			excelHander = new Excel2007Handler(filePathName);

			ArrayList<ProcessApprovalBean> beanSetList = new ArrayList<ProcessApprovalBean>();
			try {
				beanSetList = queryProcessApproval();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			int rowNum = 0;
			int iCol = 0;
			for (ProcessApprovalBean bean : beanSetList) {
				
				iCol = 0;
				rowNum++;
				excelHander.setStringValue(rowNum, iCol++, bean.getProcessName());
				excelHander.setStringValue(rowNum, iCol++, bean.getProcessStatus());
				excelHander.setStringValue(rowNum, iCol++, bean.getApplicantId());
				excelHander.setStringValue(rowNum, iCol++, bean.getApplicantName());
				excelHander.setStringValue(rowNum, iCol++, bean.getProcessStartTime());
				excelHander.setStringValue(rowNum, iCol++, bean.getApproverId());
				excelHander.setStringValue(rowNum, iCol++, bean.getApproverName());
				excelHander.setStringValue(rowNum, iCol++, bean.getLinkName());
				excelHander.setStringValue(rowNum, iCol++, bean.getApprovalStartTime());
				excelHander.setStringValue(rowNum, iCol++, bean.getApprovalEndTime());
				excelHander.setStringValue(rowNum, iCol++, bean.getApprovalTime()+"");
				
			}
			return excelHander.getWorkbook();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}


	private ArrayList<ProcessApprovalBean> queryProcessApproval() throws Exception
	{
		ArrayList<ProcessApprovalBean> result = new ArrayList<ProcessApprovalBean>();

		System.out.println("start queryProcessApproval.......................");
        MethodContext context = MethodContext.getContext();
        WTConnection wtConn = (WTConnection)context.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try{
        	String sql = "select distinct t.name as processName";						// 流程名称
        	sql += " ,t.state as proocessStatus";										// 流程状态
        	sql += " ,t4.name as applicantId";											// 流程申请人工号
        	sql += " ,t4.fullname as applicantName";									// 流程申请人姓名
        	sql += " ,trunc(t.starttime, 'mi') + 8 / 24 as processStartTime";			// 流程启动时间
        	sql += " ,t3.name as approverId";											// 审批人工号
        	sql += " ,t3.fullname as approverName";										// 审批人姓名
        	sql += " ,t1.name as linkName";												// 审批环节名称
        	sql += " ,trunc(t2.createstampa2, 'mi') + 8 / 24 as approvalStartTime";			// 审批开始时间
        	sql += " ,trunc(t2.updatestampa2, 'mi') + 8 / 24 as approvalEndTime";				// 审批结束时间
        	sql += " ,((trunc(t2.updatestampa2, 'mi') + 8 / 24)-(trunc(t2.createstampa2, 'mi') + 8 / 24))*24*60 as approvalTime";		// 审批耗时minute
            
            sql += " from wfassignedactivity t1, wfprocess t, workitem t2, wtuser t3, wtuser t4";
            sql += " where 1 = 1";
            sql += " and t1.ida3parentprocessref = t.ida2a2";
            sql += " and t2.ida3a4 = t1.ida2a2";
            sql += " and t2.ida3a2ownership = t3.ida2a2";
//            sql += " and t.state = 'CLOSED_COMPLETED_EXECUTED'";
            sql += " and t4.ida2a2=t.ida3b7";
            
            if (!isNullOrBlank(routerName)) {
    			if (routerName.indexOf("*") >= 0) {
    				routerName = replace(routerName, "*", "%");
    				sql += " and t.name like '" + routerName + "'";
    			} else {
    				sql += " and t.name = '" + routerName + "'";
    			}
    		}
            if (!isNullOrBlank(approvedDateFrom)) {
            	sql += " and t2.createstampa2+1/3 >= to_date('"+ approvedDateFrom +"', 'YYYY/MM/DD')";
            }
            
            if (!isNullOrBlank(approvedDateTo)) {
            	approvedDateTo = approvedDateTo + " 23:59:59";
            	sql += " and t2.updatestampa2+1/3 <= to_date('"+ approvedDateTo +"', 'YYYY/MM/DD hh24:mi:ss')";
            }
            
            sql += " order by processName";
            log.debug("queryProcessApproval sql=" + sql);
            statement = wtConn.prepareStatement(sql);
            resultSet = statement.executeQuery();
            while(resultSet.next()){
                ProcessApprovalBean bean = new ProcessApprovalBean();
                bean.setProcessName(resultSet.getString("processName"));
                
                // OPEN_RUNNING:正在运行	CLOSED_COMPLETED_EXECUTED:完成	 CLOSED_TERMINATED:手动终止
                String proocessStatus = resultSet.getString("proocessStatus");
                if(proocessStatus.equals("OPEN_RUNNING")){
                	bean.setProcessStatus("正在运行");
                }else if(proocessStatus.equals("CLOSED_COMPLETED_EXECUTED")){
                	bean.setProcessStatus("完成");
                }else if(proocessStatus.equals("CLOSED_TERMINATED")){
                	bean.setProcessStatus("终止");
                }else{
                	bean.setProcessStatus(proocessStatus);
                }
                	
                bean.setProcessStartTime(resultSet.getString("processStartTime"));
                bean.setApproverId(resultSet.getString("approverId"));
                bean.setApproverName(resultSet.getString("approverName"));
                bean.setLinkName(resultSet.getString("linkName"));
                bean.setApprovalStartTime(resultSet.getString("approvalStartTime"));
                bean.setApprovalEndTime(resultSet.getString("approvalEndTime"));
                bean.setApprovalTime(resultSet.getInt("approvalTime"));
                
                bean.setApplicantId(resultSet.getString("applicantId"));
                bean.setApplicantName(resultSet.getString("applicantName"));
                
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
        System.out.println("end queryProcessApproval.......................");
	
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
