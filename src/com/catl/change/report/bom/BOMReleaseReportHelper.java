package com.catl.change.report.bom;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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

public class BOMReleaseReportHelper
{
	String createdDateFrom, createdDateTo;
	private static Logger log = Logger.getLogger(BOMReleaseReportHelper.class.getName());
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	public BOMReleaseReportHelper()
	{
	}

	public BOMReleaseReportHelper(String createdDateFrom, String createdDateTo)
	{
		this.createdDateFrom = createdDateFrom;
		this.createdDateTo = createdDateTo;
	}

	public void generateReport(HttpServletResponse response) throws WTException, WTPropertyVetoException
	{
		log.debug("generateReport createdDateFrom:"+ createdDateFrom + "createdDateTo:" + createdDateTo);
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
			// get system current time add to the report name
			String currentDate = dateFormat.format((Date) new Timestamp(System.currentTimeMillis()));
			String fileName = "PLM_BOMRelease_" + currentDate + ".xlsx";
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
				+ "BOMRelease_template.xlsx";

		Excel2007Handler excelHander;
		try {
			excelHander = new Excel2007Handler(filePathName);

			ArrayList<BOMReleaseBean> beanSetList = new ArrayList<BOMReleaseBean>();
			try {
				beanSetList = queryBOMRelease();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			int rowNum = 0;
			int iCol = 0;
			for (BOMReleaseBean bean : beanSetList) {
				
				iCol = 0;
				rowNum++;
				excelHander.setStringValue(rowNum, iCol++, bean.getPartNumber());
				excelHander.setStringValue(rowNum, iCol++, bean.getPartName());
				excelHander.setStringValue(rowNum, iCol++, bean.getVersion());
				excelHander.setStringValue(rowNum, iCol++, bean.getCreator());
				excelHander.setStringValue(rowNum, iCol++, bean.getCreatedTime());
				excelHander.setStringValue(rowNum, iCol++, bean.getState());
				excelHander.setStringValue(rowNum, iCol++, bean.getSite());
			}
			return excelHander.getWorkbook();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}


	private ArrayList<BOMReleaseBean> queryBOMRelease() throws Exception
	{
		ArrayList<BOMReleaseBean> result = new ArrayList<BOMReleaseBean>();

		System.out.println("start queryBOMRelease.......................");
        MethodContext context = MethodContext.getContext();
        WTConnection wtConn = (WTConnection)context.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        // part state
        Map<String, String> stateMap = new HashMap<String, String>();
        stateMap.put("WRITING", "编制");					// 编制
        stateMap.put("MODIFICATION", "修改");				// 修改
        stateMap.put("REVIEW", "审阅");					// 审阅
        stateMap.put("DESIGN", "设计");					// 设计
        stateMap.put("DESIGNMODIFICATION", "设计修改");	// 设计修改
        stateMap.put("DESIGNREVIEW", "设计审阅");			// 设计修改
        stateMap.put("RELEASED", "已发布");				// 已发布
        try{
        	
        	String sql = "select pm.wtpartnumber partNumber";										// 物料编码
        	sql += " ,pm.name partName";															// 物料名称
        	sql += " ,p.versionida2versioninfo||'.'||p.iterationida2iterationinfo version";			// 版本
        	sql += " ,u.fullname creator";															// 创建者
        	sql += " ,min(plink.createstampa2+1/3) createdTime";									// 创建时间
        	sql += " ,p.statestate state";															// 状态
        	sql += " ,nvl(cl.namecontainerinfo,cp.namecontainerinfo) site";							// 上下文
        	sql += " from";
        	sql += " (select p.* from wtpart p ,";
        	sql += " 	(select max(p.versionida2versioninfo) versionida2versioninfo,p.ida3masterreference from wtpart p group by ida3masterreference) pp";
        	sql += " where p.latestiterationinfo=1 and p.ida3masterreference=pp.ida3masterreference and p.versionida2versioninfo=pp.versionida2versioninfo";
        	sql += " ) p left join wtpartmaster pm on p.ida3masterreference=pm.ida2a2 ";
        	sql += " left join WTPartUsageLink plink on p.ida2a2=plink.ida3a5 ";
        	sql += " left join wtuser u on p.ida3d2iterationinfo = u.ida2a2";
        	sql += " left join subfolder c on p.ida3b2folderinginfo=c.ida2a2";
        	sql += " left join SubFolder c1 on c.ida3b2folderinginfo=c1.ida2a2";
        	sql += " left join wtlibrary cl on p.ida3containerreference=cl.ida2a2";
        	sql += " left join pdmlinkproduct cp  on p.ida3containerreference=cp.ida2a2";
        	sql += " where plink.ida3a5 is not null";
        	
        	if (!isNullOrBlank(createdDateFrom)) {
        		
        		sql += " and plink.createstampa2+1/3 >= to_date('"+ createdDateFrom +"', 'YYYY/MM/DD')";
            }
            
            if (!isNullOrBlank(createdDateTo)) {
            	createdDateTo = createdDateTo + " 23:59:59";
            	sql += " and plink.createstampa2+1/3 <= to_date('"+ createdDateTo +"', 'YYYY/MM/DD hh24:mi:ss')";
            }
        	
        	sql += " group by p.ida2a2, pm.wtpartnumber,pm.name,u.fullname,p.statestate,";
        	sql += " p.versionida2versioninfo,p.iterationida2iterationinfo,nvl(cl.namecontainerinfo,cp.namecontainerinfo)";
        	sql += " order by nvl(cl.namecontainerinfo,cp.namecontainerinfo)";
        	
            statement = wtConn.prepareStatement(sql);
            resultSet = statement.executeQuery();
            while(resultSet.next()){
                BOMReleaseBean bean = new BOMReleaseBean();
                
                bean.setPartNumber(resultSet.getString("partNumber"));
                bean.setPartName(resultSet.getString("partName"));
                bean.setVersion(resultSet.getString("version"));
                bean.setCreator(resultSet.getString("creator"));
                bean.setCreatedTime(resultSet.getString("createdTime"));
                
                String state = resultSet.getString("state");
                String stateStr = stateMap.get(state) == null? state:stateMap.get(state);
                
                bean.setState(stateStr);
                bean.setSite(resultSet.getString("site"));
                
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
        System.out.println("end queryBOMRelease.......................");
	
		return result;
	}
	

	private boolean isNullOrBlank(String param)
	{
		return (param == null || param.trim().equals("") || param.trim().equals("null")) ? true : false;
	}
	
}
