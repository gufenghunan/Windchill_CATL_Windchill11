package com.catl.change.report.ecr;

import java.io.File;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Vector;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Sheet;
import org.drools.core.util.StringUtils;

import com.ptc.core.lwc.server.PersistableAdapter;

import wt.change2.ChangeException2;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.lifecycle.LifeCycleException;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleHistory;
import wt.lifecycle.LifeCycleManaged;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.project.Role;
import wt.query.DateHelper;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.session.SessionServerHelper;
import wt.team.Team;
import wt.team.TeamHelper;
import wt.util.WTException;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;

public class ECRReportHelper
{
	String ecrNumber, status, createDateFrom, createDateTo, approvedDateFrom, approvedDateTo, user;
	private static Logger log = Logger.getLogger(ECRReportHelper.class.getName());
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	WTChangeRequest2 ecrObj;
	WTChangeOrder2 ecoObj;

	private static WTProperties wtProperties;
	boolean fromECR = true;

	static
	{
		try
		{
			wtProperties = WTProperties.getLocalProperties();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public ECRReportHelper()
	{
	}

	public ECRReportHelper(String ecrNumber, String status, String createDateFrom, String createDateTo, String approvedDateFrom, String approvedDateTo, String user)
	{
		this.ecrNumber = ecrNumber;
		this.status = status;
		this.createDateFrom = createDateFrom;
		this.createDateTo = createDateTo;
		this.approvedDateFrom = approvedDateFrom;
		this.approvedDateTo = approvedDateTo;
		this.user = user;
	}

	public void generateReport(HttpServletResponse response) throws WTException, WTPropertyVetoException
	{
		log.debug("generateReport ecrNumber:" + ecrNumber + "status:" + status + "createDateFrom:" + createDateFrom + "createDateTo:" + createDateTo + "approvedDateFrom:"
				+ approvedDateFrom + "approvedDateTo:" + approvedDateTo + "user:" + user);
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
			// get system current time add to the report name
			String currentDate = dateFormat.format((Date) new Timestamp(System.currentTimeMillis()));
			String fileName = "PLM_ECR_ECN_" + currentDate + ".xls";
			OutputStream os = null;
			InputStream input = null;
			String filePath = generateExcel(fileName);
			String filename = java.net.URLEncoder.encode(fileName, "UTF-8");
			os = response.getOutputStream();
			response.setContentType("application/x-msdownload; charset=UTF-8");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
			File temp = new File(filePath);
			input = new FileInputStream(temp);
			byte[] buff = new byte[512];
			int len = 0;
			while ((len = input.read(buff)) != -1)
			{
				os.write(buff, 0, len);
			}
			response.setStatus(HttpServletResponse.SC_OK);
			response.flushBuffer();

			os.flush();
			input.close();
			os.close();
		} catch (IOException e)
		{
			throw new WTException(e, e.getLocalizedMessage());
		} finally
		{
			SessionServerHelper.manager.setAccessEnforced(flag);
		}
	}

	private String generateExcel(String fileName) throws ChangeException2, WTException, IOException, WTPropertyVetoException
	{
		String filePath = wtProperties.getProperty("wt.temp") + fileName;
		log.debug(filePath);
		ArrayList<ECRReportBean> beanSet=new ArrayList<ECRReportBean>();
		ArrayList<WTChangeRequest2> ecrList = queryECR();
		for (WTChangeRequest2 ecr : ecrList)
		{
			ArrayList<WTChangeOrder2> ecnList = getRelatedECN(ecr);
			if(ecnList.size()==0){
			    ECRReportBean bean=new ECRReportBean(ecr,null,null,null);
			    if(!beanSet.contains(bean))
                {
                    beanSet.add(bean);
                }
			}
			for (WTChangeOrder2 ecn : ecnList)
			{
				ArrayList<WTChangeActivity2> ecaList = getRelatedECA(ecn);
				if(ecaList.size()==0){
	                ECRReportBean bean=new ECRReportBean(ecr,ecn,null,null);
	                if(!beanSet.contains(bean))
	                {
	                    beanSet.add(bean);
	                }
	            }
				for(WTChangeActivity2 eca:ecaList)
				{
					QueryResult qr2 = ChangeHelper2.service.getChangeablesBefore(eca);
					boolean isHavePart = false;
					while(qr2.hasMoreElements())
					{
						Object obj2=qr2.nextElement();
						if(obj2 instanceof WTPart)
						{
						    isHavePart = true;
							WTPart part=(WTPart)obj2;
							ECRReportBean bean=new ECRReportBean(ecr,ecn,eca,part);
							if(!beanSet.contains(bean))
							{
								beanSet.add(bean);
							}
						}
					}
					if(!isHavePart){
                        ECRReportBean bean=new ECRReportBean(ecr,ecn,eca,null);
                        if(!beanSet.contains(bean))
                        {
                            beanSet.add(bean);
                        }
                    }
				}
			}
		}
		
		ArrayList<WTChangeOrder2> dcnList = queryDCN();
		WTChangeRequest2 dcr = null;
		for (WTChangeOrder2 dcn : dcnList)
		{
		    
			ArrayList<WTChangeActivity2> ncaList = getRelatedECA(dcn);
			if(ncaList.size()==0){
                ECRReportBean bean1=new ECRReportBean(dcr,dcn,null,null);
                if(!beanSet.contains(bean1))
                {
                    beanSet.add(bean1);
                }
            }
			for(WTChangeActivity2 dca:ncaList)
			{
				QueryResult qr2 = ChangeHelper2.service.getChangeablesBefore(dca);
				boolean isHavePart = false;
				while(qr2.hasMoreElements())
				{
					Object obj2=qr2.nextElement();
					if(obj2 instanceof WTPart)
					{
					    isHavePart = true;
						WTPart part=(WTPart)obj2;
						ECRReportBean bean1=new ECRReportBean(dcr,dcn,dca,part);
						if(!beanSet.contains(bean1))
						{
							beanSet.add(bean1);
						}
					}
				}
				if(!isHavePart){
                    ECRReportBean bean1=new ECRReportBean(dcr,dcn,dca,null);
                    if(!beanSet.contains(bean1))
                    {
                        beanSet.add(bean1);
                    }
                }
			}
		}
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		Sheet sheet = workbook.createSheet("ECRECNECAReport");
		Row pRow = sheet.createRow(0);
		// title
		String pTitle[] = { "ECR No. ECR编号","ECR State	ECR状态","Change classification 变更分类","Change origin 变更来源","Change type 变更类型","Change Item 变更主题",
				"Change background description 变更背景描述","Initial change proposal 变更初步方案","ENW No. ENW单号","Application date 申请日期",
				"Applicant申请人","Application Dept. 申请部门","Approval date 批准日期",
				"ECN No. ECN单号","ECN creatorName ECN创建者工号","ECN creatorFullName ECN创建者姓名","ECN State ECN状态","ECN Stage 阶段", "Change reason 变更原因", "Change description 变更描述", "change from 变更来源", "change type 变更类型", "Change classification 变更分类",
				"Application date 申请日期", "Eff. Date 预计变更生效日期", "Conclusiong 评审结论", "Approval date 批准日期", 
				"ECA NO. ECA单号","ECA状态","计划完成时间","Assignment name 任务名称",	"Assignment description	任务描述",
				"Assignment remark 任务备注","Person in charge 责任人","Responsible dept. 责任部门",	"Approval date 批准日期","受影响对象编号","受影响数据名称","受影响数据位置" };
		for (int tCount = 0; tCount < pTitle.length; tCount++)
		{
			Cell tCell = pRow.createCell(tCount);
			tCell.setCellValue(pTitle[tCount]);
		}
		int p = 0;
		log.debug("beanSet:"+beanSet.size());
		for(ECRReportBean bean:beanSet)
		{
			Row row = sheet.createRow(++p);
			int c = 0;
			//ECR
			Cell cell = row.createCell(c++);
			cell.setCellValue(bean.getEcrNumber());

			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcrStatus());

			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcrChangeImpact());

			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcrChangeFrom());

			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcrChangeType());

			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcrChangeSubject());

			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcrDescription());
			
			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcrProposedSolution());
			
			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcrEnwNumber());
			
			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcrCreateDate());
			
			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcrCreator());
			
			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcrDepartment());
			
			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcrApprovedDate());
			
			//ECN
			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcnNumber());
			
			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcnCreatorName());
			
			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcnCreatorFullName());
			
			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcnStatus());
			
			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcnStage());
			
			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcnChangeReason());
			
			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcnDescription());
			
			//新增DCN变更来源、变更类型
			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcnChangeFrom());
			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcnChangeType());
			
			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcnChangeImpact());
			
			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcnCreateDate());
			
			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcnNeedDate());
			
			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcnReviewResult());
			
			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcnApprovedDate());
			
			//ECA
			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcaNumber());
			
			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcaStatus());
			
			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcaNeedDate());
			
			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcaName());
			
			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcaTaskDescription());
			
			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcaDescription());
			
			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcaAsigneeUser());
			
			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcaAsigneeUserDep());
			
			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcaApprovedDate());
			
			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcaAffectPartNumber());
			
			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcaAffectPartName());
			
			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcaAffectPartLocation());
		}
		
		FileOutputStream out = new FileOutputStream(filePath);
		workbook.write(out);
		out.flush();
		out.close();
		return filePath;
	}

	private ArrayList<WTChangeActivity2> getRelatedECA(WTChangeOrder2 ecn) throws ChangeException2, WTException
	{
		ArrayList<WTChangeActivity2> result = new ArrayList<WTChangeActivity2>();
		QueryResult qr1 = ChangeHelper2.service.getChangeActivities(ecn);
		while(qr1.hasMoreElements())
		{
			Object obj1=qr1.nextElement();
			if(obj1 instanceof WTChangeActivity2)
			{
				WTChangeActivity2 eca=(WTChangeActivity2)obj1;
				result.add(eca);
			}
		}
		return result;
	}

	private ArrayList<WTChangeOrder2> getRelatedECN(WTChangeRequest2 ecr) throws ChangeException2, WTException
	{
		ArrayList<WTChangeOrder2> result = new ArrayList<WTChangeOrder2>();
		QueryResult qr = ChangeHelper2.service.getChangeOrders(ecr);
		while (qr.hasMoreElements())
		{
			Object obj = qr.nextElement();
			if (obj instanceof WTChangeOrder2)
			{
				WTChangeOrder2 ecn = (WTChangeOrder2) obj;
				result.add(ecn);
			}
		}
		return result;
	}

	private ArrayList<WTChangeRequest2> queryECR() throws WTException, WTPropertyVetoException
	{
		ArrayList<WTChangeRequest2> result = new ArrayList<WTChangeRequest2>();

		QuerySpec qs = new QuerySpec();
		int index = qs.appendClassList(WTChangeRequest2.class, true);
		qs.setAdvancedQueryEnabled(true);
		
		
		if (status.indexOf("*") >= 0)
        {
            status = replace(status, "*", "%");
            qs.appendWhere(new SearchCondition(WTChangeRequest2.class, WTChangeRequest2.LIFE_CYCLE_STATE, SearchCondition.LIKE, status.trim()), index);
        }else
        {
            qs.appendWhere(new SearchCondition(WTChangeRequest2.class, WTChangeRequest2.LIFE_CYCLE_STATE, SearchCondition.EQUAL, status.trim()), index);
        }
		String number=this.ecrNumber;
		if (!isNullOrBlank(number))
		{
			qs.appendAnd();
			if (number.indexOf("*") >= 0)
			{
				number = replace(number, "*", "%");
				qs.appendWhere(new SearchCondition(WTChangeRequest2.class, WTChangeRequest2.NUMBER, SearchCondition.LIKE, number.trim().toUpperCase()),index);
			}else
			{
				qs.appendWhere(new SearchCondition(WTChangeRequest2.class, WTChangeRequest2.NUMBER, SearchCondition.EQUAL, number.trim().toUpperCase()),index);
			}
		}
		String createTimeStartStr = this.createDateFrom;
		if (!isNullOrBlank(createTimeStartStr))
		{
			Timestamp timestampStart = new Timestamp(new Date().getTime());
			DateHelper datehelper = new DateHelper(createTimeStartStr, "day", SessionHelper.getLocale());
			timestampStart = new Timestamp(datehelper.getDate().getTime());
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTChangeRequest2.class, WTChangeRequest2.CREATE_TIMESTAMP, SearchCondition.GREATER_THAN_OR_EQUAL, timestampStart), index);
		}

		String createTimeEndStr = this.createDateTo;
		if (!isNullOrBlank(createTimeEndStr))
		{
			Timestamp timestampEnd = new Timestamp(new Date().getTime());
			DateHelper datehelper = new DateHelper(createTimeEndStr, "day", SessionHelper.getLocale());
			timestampEnd = new Timestamp(datehelper.getDate().getTime());
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTChangeRequest2.class, WTChangeRequest2.CREATE_TIMESTAMP, SearchCondition.LESS_THAN_OR_EQUAL, timestampEnd), index);
		}
		
		String createUserid = this.user;
		if (!isNullOrBlank(createUserid))
		{
			qs.appendAnd();
			int classIndex1 = qs.appendClassList(WTUser.class, false);
			qs.appendWhere(new SearchCondition(WTUser.class, WTUser.NAME, SearchCondition.EQUAL, getUserName(createUserid)), classIndex1);
			qs.appendAnd();
			SearchCondition sc = new SearchCondition(
					WTChangeRequest2.class,
					"creator.key.id",
                    WTUser.class,
                    "thePersistInfo.theObjectIdentifier.id");
            qs.appendWhere(sc,index,classIndex1);
		}
		log.debug(qs);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		while(qr.hasMoreElements())
		{
			Persistable[] persist=(Persistable[]) qr.nextElement();
			WTChangeRequest2 ecr=(WTChangeRequest2) persist[0];
			String approvedTimeStartStr = this.approvedDateFrom;
			String approvedTimeEndStr = this.approvedDateTo;
			
            if (!isNullOrBlank(approvedTimeStartStr) || !isNullOrBlank(approvedTimeEndStr)){
                Enumeration histories = (Enumeration) LifeCycleHelper.service.getHistory(ecr);
                Timestamp changeTime=null;
                while(histories.hasMoreElements()){
                    LifeCycleHistory lcHistory = (LifeCycleHistory)histories.nextElement();
                    if(lcHistory.getState().toString().equals("IMPLEMENTATION")){
                        changeTime = lcHistory.getCreateTimestamp();
                    }
                }
                if(changeTime != null){
                    if(!isNullOrBlank(approvedTimeStartStr)){
                        DateHelper datehelper = new DateHelper(approvedTimeStartStr, "day", SessionHelper.getLocale());
                        if(datehelper.getDate().after(changeTime)){
                            continue;
                        }
                    }
                    if(!isNullOrBlank(approvedTimeEndStr)){
                        DateHelper datehelper = new DateHelper(approvedTimeEndStr, "day", SessionHelper.getLocale());
                        if(datehelper.getDate().before(changeTime)){
                            continue;
                        }
                    }
                }else{
                    continue;
                }
            }
			//log.debug("add ecr:"+ecr);
			if(!result.contains(ecr))
				result.add(ecr);
		} 
		
		return result;
	}

	private String getUserName(String createUserid)
	{
		String uid = createUserid.split(",")[0].split("=")[1];
		if("wcadmin".equals(uid))
			uid = "Administrator";
		return uid;
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
	class ECRReportBean
	{
		// ecr attr.
		String ecrNumber, ecrStatus, ecrChangeImpact, ecrChangeFrom, ecrChangeType, ecrChangeSubject, ecrDescription, ecrProposedSolution, ecrEnwNumber, ecrCreateDate, ecrCreator,
				ecrDepartment, ecrApprovedDate;
		// ecn attr.
		String ecnNumber,ecnCreatorName,ecnCreatorFullName, ecnStatus,ecnStage, ecnChangeReason, ecnDescription, ecnChangeImpact, ecnCreateDate, ecnNeedDate, ecnReviewResult, ecnApprovedDate, ecnChangeType, ecnChangeFrom;
		// eca attr.
		String ecaNumber, ecaStatus, ecaNeedDate, ecaName, ecaTaskDescription, ecaDescription, ecaAsigneeUser, ecaAsigneeUserDep, ecaApprovedDate, ecaAffectPartNumber,
				ecaAffectPartName,ecaAffectPartLocation;

		public ECRReportBean(WTChangeRequest2 ecr, WTChangeOrder2 ecn, WTChangeActivity2 eca, WTPart part) throws WTException
		{
			//ECR
			if (ecr != null){
				PersistableAdapter ecrAttr = new PersistableAdapter(ecr, null, null, null);

				this.ecrNumber = ecr.getNumber();
				this.ecrStatus =  ecr.getState().getState().getDisplay(Locale.CHINA);
				ecrAttr.load("changeImpact");
				this.ecrChangeImpact = getAttr(ecrAttr.get("changeImpact"));
				
				ecrAttr.load("changeFrom");
				this.ecrChangeFrom = (String) ecrAttr.get("changeFrom");
				
				ecrAttr.load("changeType");
				Object ecrChangeType = ecrAttr.get("changeType");
				if(ecrChangeType instanceof String)
				{
					this.ecrChangeType = (String) ecrChangeType;
				}else if(ecrChangeType instanceof Object[])
				{
					Object[] ecrChangeTypes = (Object[]) ecrChangeType;
					String s = "";
					for(int i=0;i<ecrChangeTypes.length;i++)
					{
						s=s+","+ecrChangeTypes[i];
					}
					if(s.startsWith(","))
					{
						s=s.substring(1, s.length());
					}
					this.ecrChangeType = s;
				}
				
				ecrAttr.load("changeSubject");
				this.ecrChangeSubject = getAttr(ecrAttr.get("changeSubject"));
				
				ecrAttr.load("proposedSolution");
				this.ecrProposedSolution = getAttr( ecrAttr.get("proposedSolution"));
				
				ecrAttr.load("enwNumber");
				this.ecrEnwNumber = getAttr( ecrAttr.get("enwNumber"));
				
				this.ecrCreateDate = sdf.format(new Timestamp(ecr.getCreateTimestamp().getTime()+28800000));
				this.ecrCreator = ecr.getCreatorFullName();
				
				ecrAttr.load("department");
				this.ecrDepartment = getAttr( ecrAttr.get("department"));
				
				this.ecrApprovedDate = getApprovedDate(ecr);
			}
			
			//ECN
			if(ecn != null){
			    PersistableAdapter ecnAttr = new PersistableAdapter(ecn, null, null, null);
	            this.ecnNumber = ecn.getNumber();
	            this.ecnCreatorName = ecn.getCreatorName();
	            this.ecnCreatorFullName = ecn.getCreatorFullName();
	            this.ecnStatus = ecn.getState().getState().getDisplay(Locale.CHINA);
	            
	            ecnAttr.load("stage");
	            this.ecnStage = getAttr( ecnAttr.get("stage"));
	            
	            ecnAttr.load("changeReason");
	            this.ecnChangeReason = getAttr( ecnAttr.get("changeReason"));
	            
	            ecnAttr.load("description");
	            this.ecnDescription = getAttr( ecnAttr.get("description"));
	            
	            ecnAttr.load("changeImpact");
	            this.ecnChangeImpact = getAttr( ecnAttr.get("changeImpact"));
	            
	            this.ecnCreateDate = sdf.format(new Timestamp(ecn.getCreateTimestamp().getTime()+28800000));
	            
	            ecnAttr.load("needDate");
	            this.ecnNeedDate = getAttr( ecnAttr.get("needDate"));
	            
	            ecnAttr.load("reviewResult");
	            this.ecnReviewResult = getAttr( ecnAttr.get("reviewResult"));
	            
	            //新增变更类型、变更来源
	            ecnAttr.load("changeType");
	            this.ecnChangeType = getAttr( ecnAttr.get("changeType"));
	            ecnAttr.load("changeFrom");
	            this.ecnChangeFrom = getAttr( ecnAttr.get("changeFrom"));
	            
	            this.ecnApprovedDate = getApprovedDate(ecn);
			}
			
			//ECA
			if(eca != null){
			    PersistableAdapter ecaAttr = new PersistableAdapter(eca, null, null, null);
			    this.ecaNumber = eca.getNumber();
	            this.ecaStatus = eca.getState().getState().getDisplay(Locale.CHINA);
	            
	            ecaAttr.load("needDate");
	            this.ecaNeedDate = getAttr( ecaAttr.get("needDate"));
	            
	            this.ecaName = eca.getName();
	            
	            ecaAttr.load("taskDescription");
	            this.ecaTaskDescription = getAttr( ecaAttr.get("taskDescription"));
	            
	            ecaAttr.load("description");
	            this.ecaDescription = getAttr( ecaAttr.get("description"));
	            
	            String ecaAsignee="";
	            Team team = TeamHelper.service.getTeam(eca);
	            Vector allRoles = TeamHelper.service.findRoles(team);
	            HashMap rolePrincipalListMap = TeamHelper.service.findAllParticipantsByRole(team);
	            
	            for (int i = 0; allRoles != null && i < allRoles.size(); i++)
	            {
	                Role role = (Role) allRoles.get(i);
	                if("工作负责人".equals(role.getDisplay(Locale.SIMPLIFIED_CHINESE)))
	                {
	                    ArrayList principalList = (ArrayList) rolePrincipalListMap.get(role);
	                    for (int j = 0; principalList != null && j < principalList.size(); j++) 
	                    {
	                        WTPrincipal principal = ((WTPrincipalReference) principalList.get(j)).getPrincipal();
	                        if(principal instanceof WTUser)
	                        {
	                            WTUser user = (WTUser) principal;
	                            ecaAsignee = ecaAsignee+","+user.getFullName();
	                        }
	                    }
	                    break;
	                }
	            }
	            if(ecaAsignee.startsWith(","))
	            {
	                ecaAsignee=ecaAsignee.substring(1, ecaAsignee.length());
	            }
	            this.ecaAsigneeUser=ecaAsignee;
	            
	            ecaAttr.load("department");
	            this.ecaAsigneeUserDep = getAttr( ecaAttr.get("department"));
	            
	            this.ecaApprovedDate = getApprovedDate(eca);
			}
			//PART
			if(part != null){
			    this.ecaAffectPartNumber = part.getNumber();
	            this.ecaAffectPartName = part.getName();
	            this.ecaAffectPartLocation = part.getLocation();
			}
			
			toString();
		}

		private String getAttr(Object object)
		{
			if(object == null)
				return "";
			if(object instanceof Timestamp)
			{
				Timestamp t=(Timestamp)object;
				System.out.println(t+"---"+new Timestamp(t.getTime()+28800000));
				return new Timestamp(t.getTime()+28800000).toString().split(" ")[0];
			}
			return object.toString();
		}

		public String getEcrNumber()
		{
			return ecrNumber;
		}

		public void setEcrNumber(String ecrNumber)
		{
			this.ecrNumber = ecrNumber;
		}

		public String getEcrStatus()
		{
			return ecrStatus;
		}

		public void setEcrStatus(String ecrStatus)
		{
			this.ecrStatus = ecrStatus;
		}

		public String getEcrChangeImpact()
		{
			return ecrChangeImpact;
		}

		public void setEcrChangeImpact(String ecrChangeImpact)
		{
			this.ecrChangeImpact = ecrChangeImpact;
		}

		public String getEcrChangeFrom()
		{
			return ecrChangeFrom;
		}

		public void setEcrChangeFrom(String ecrChangeFrom)
		{
			this.ecrChangeFrom = ecrChangeFrom;
		}

		public String getEcrChangeType()
		{
			return ecrChangeType;
		}

		public void setEcrChangeType(String ecrChangeType)
		{
			this.ecrChangeType = ecrChangeType;
		}

		public String getEcrChangeSubject()
		{
			return ecrChangeSubject;
		}

		public void setEcrChangeSubject(String ecrChangeSubject)
		{
			this.ecrChangeSubject = ecrChangeSubject;
		}

		public String getEcrDescription()
		{
			return ecrDescription;
		}

		public void setEcrDescription(String ecrDescription)
		{
			this.ecrDescription = ecrDescription;
		}

		public String getEcrProposedSolution()
		{
			return ecrProposedSolution;
		}

		public void setEcrProposedSolution(String ecrProposedSolution)
		{
			this.ecrProposedSolution = ecrProposedSolution;
		}

		public String getEcrEnwNumber()
		{
			return ecrEnwNumber;
		}

		public void setEcrEnwNumber(String ecrEnwNumber)
		{
			this.ecrEnwNumber = ecrEnwNumber;
		}

		public String getEcrCreateDate()
		{
			return ecrCreateDate;
		}

		public void setEcrCreateDate(String ecrCreateDate)
		{
			this.ecrCreateDate = ecrCreateDate;
		}

		public String getEcrCreator()
		{
			return ecrCreator;
		}

		public void setEcrCreator(String ecrCreator)
		{
			this.ecrCreator = ecrCreator;
		}

		public String getEcrDepartment()
		{
			return ecrDepartment;
		}

		public void setEcrDepartment(String ecrDepartment)
		{
			this.ecrDepartment = ecrDepartment;
		}

		public String getEcrApprovedDate()
		{
			return ecrApprovedDate;
		}

		public void setEcrApprovedDate(String ecrApprovedDate)
		{
			this.ecrApprovedDate = ecrApprovedDate;
		}

		public String getEcnNumber()
		{
			return ecnNumber;
		}

		public void setEcnNumber(String ecnNumber)
		{
			this.ecnNumber = ecnNumber;
		}

		public String getEcnStatus()
		{
			return ecnStatus;
		}

		public void setEcnStatus(String ecnStatus)
		{
			this.ecnStatus = ecnStatus;
		}

		public String getEcnChangeReason()
		{
			return ecnChangeReason;
		}

		public void setEcnChangeReason(String ecnChangeReason)
		{
			this.ecnChangeReason = ecnChangeReason;
		}

		public String getEcnDescription()
		{
			return ecnDescription;
		}

		public void setEcnDescription(String ecnDescription)
		{
			this.ecnDescription = ecnDescription;
		}

		public String getEcnChangeImpact()
		{
			return ecnChangeImpact;
		}

		public void setEcnChangeImpact(String ecnChangeImpact)
		{
			this.ecnChangeImpact = ecnChangeImpact;
		}

		public String getEcnCreateDate()
		{
			return ecnCreateDate;
		}

		public void setEcnCreateDate(String ecnCreateDate)
		{
			this.ecnCreateDate = ecnCreateDate;
		}

		public String getEcnNeedDate()
		{
			return ecnNeedDate;
		}

		public void setEcnNeedDate(String ecnNeedDate)
		{
			this.ecnNeedDate = ecnNeedDate;
		}

		public String getEcnReviewResult()
		{
			return ecnReviewResult;
		}

		public void setEcnReviewResult(String ecnReviewResult)
		{
			this.ecnReviewResult = ecnReviewResult;
		}

		public String getEcnApprovedDate()
		{
			return ecnApprovedDate;
		}

		public String getEcnChangeType() {
			return ecnChangeType;
		}

		public void setEcnChangeType(String ecnChangeType) {
			this.ecnChangeType = ecnChangeType;
		}

		public String getEcnChangeFrom() {
			return ecnChangeFrom;
		}

		public void setEcnChangeFrom(String ecnChangeFrom) {
			this.ecnChangeFrom = ecnChangeFrom;
		}

		public void setEcnApprovedDate(String ecnApprovedDate)
		{
			this.ecnApprovedDate = ecnApprovedDate;
		}

		public String getEcaNumber()
		{
			return ecaNumber;
		}

		public void setEcaNumber(String ecaNumber)
		{
			this.ecaNumber = ecaNumber;
		}

		public String getEcaStatus()
		{
			return ecaStatus;
		}

		public void setEcaStatus(String ecaStatus)
		{
			this.ecaStatus = ecaStatus;
		}

		public String getEcaNeedDate()
		{
			return ecaNeedDate;
		}

		public void setEcaNeedDate(String ecaNeedDate)
		{
			this.ecaNeedDate = ecaNeedDate;
		}

		public String getEcaName()
		{
			return ecaName;
		}

		public void setEcaName(String ecaName)
		{
			this.ecaName = ecaName;
		}

		public String getEcaTaskDescription()
		{
			return ecaTaskDescription;
		}

		public void setEcaTaskDescription(String ecaTaskDescription)
		{
			this.ecaTaskDescription = ecaTaskDescription;
		}

		public String getEcaDescription()
		{
			return ecaDescription;
		}

		public void setEcaDescription(String ecaDescription)
		{
			this.ecaDescription = ecaDescription;
		}

		public String getEcaAsigneeUser()
		{
			return ecaAsigneeUser;
		}

		public void setEcaAsigneeUser(String ecaAsigneeUser)
		{
			this.ecaAsigneeUser = ecaAsigneeUser;
		}

		public String getEcaAsigneeUserDep()
		{
			return ecaAsigneeUserDep;
		}

		public void setEcaAsigneeUserDep(String ecaAsigneeUserDep)
		{
			this.ecaAsigneeUserDep = ecaAsigneeUserDep;
		}

		public String getEcaApprovedDate()
		{
			return ecaApprovedDate;
		}

		public void setEcaApprovedDate(String ecaApprovedDate)
		{
			this.ecaApprovedDate = ecaApprovedDate;
		}

		public String getEcaAffectPartNumber()
		{
			return ecaAffectPartNumber;
		}

		public void setEcaAffectPartNumber(String ecaAffectPartNumber)
		{
			this.ecaAffectPartNumber = ecaAffectPartNumber;
		}

		public String getEcaAffectPartName()
		{
			return ecaAffectPartName;
		}

		public void setEcaAffectPartName(String ecaAffectPartName)
		{
			this.ecaAffectPartName = ecaAffectPartName;
		}
		public String getEcnCreatorName() {
			return ecnCreatorName;
		}

		public void setEcnCreatorName(String ecnCreatorName) {
			this.ecnCreatorName = ecnCreatorName;
		}

		public String getEcnCreatorFullName() {
			return ecnCreatorFullName;
		}

		public void setEcnCreatorFullName(String ecnCreatorFullName) {
			this.ecnCreatorFullName = ecnCreatorFullName;
		}

		public String getEcaAffectPartLocation() {
			return ecaAffectPartLocation;
		}

		public void setEcaAffectPartLocation(String ecaAffectPartLocation) {
			this.ecaAffectPartLocation = ecaAffectPartLocation;
		}
		public String getEcnStage() {
			return ecnStage;
		}

		public void setEcnStage(String ecnStage) {
			this.ecnStage = ecnStage;
		}

		@Override
		public String toString()
		{
			String ecrAttr="ecrNumber:"+ecrNumber+",ecrStatus:"+ecrStatus+",ecrChangeImpact:"+ecrChangeImpact+
			",ecrChangeFrom:"+ecrChangeFrom+",ecrChangeType:"+ecrChangeType+",ecrChangeSubject:"+ecrChangeSubject+
			",ecrProposedSolution:"+ecrProposedSolution+",ecrEnwNumber:"+ecrEnwNumber+",ecrCreateDate:"+ecrCreateDate+
			",ecrCreator:"+ecrCreator+",ecrDepartment:"+ecrDepartment+",ecrApprovedDate:"+ecrApprovedDate;
			
			String ecnAttr="ecnNumber:"+ecnNumber+"ecnCreatorName:"+ecnCreatorName+"ecnCreatorFullName:"+ecnCreatorFullName+
			",ecnStatus:"+ecnStatus+",ecnStage:"+ecnStage+",ecnChangeReason:"+ecnChangeReason+
			",ecnDescription:"+ecnDescription+",ecnChangeImpact:"+ecnChangeImpact+",ecnCreateDate:"+ecnCreateDate+
			",ecnNeedDate:"+ecnNeedDate+",ecnReviewResult:"+ecnReviewResult+",ecnApprovedDate:"+ecnApprovedDate+",ecnChangeType:"+ecnChangeType+",ecnChangeFrom:"+ecnChangeFrom;
			
			String ecaAttr="ecaNumber:"+ecaNumber+",ecaStatus:"+ecaStatus+",ecaNeedDate:"+ecaNeedDate+",ecaName:"+ecaName+
			",ecaTaskDescription:"+ecaTaskDescription+",ecaDescription:"+ecaDescription+",ecaAsigneeUser:"+ecaAsigneeUser+
			",ecaAsigneeUserDep:"+ecaAsigneeUserDep+",ecaApprovedDate:"+ecaApprovedDate;
			
			String affectPartAttr="ecaAffectPartNumber:"+ecaAffectPartNumber+",ecaAffectPartName:"+ecaAffectPartName+",ecaAffectPartLocation:"+ecaAffectPartLocation;
			
			return "ecrAttr:"+ecrAttr+";ecnAttr:"+ecnAttr+";ecaAttr:"+ecaAttr+";affectPartAttr:"+affectPartAttr;
		}
		@Override
		public boolean equals(Object obj)
		{
			if(obj instanceof ECRReportBean)
			{
				ECRReportBean bean1=(ECRReportBean)obj;
				if(bean1.toString().equals(this.toString()))
					return true;
			}
			return false;
		}
	}
	public String getApprovedDate(LifeCycleManaged obj) throws LifeCycleException, WTException
	{
		Enumeration histories = (Enumeration) LifeCycleHelper.service.getHistory(obj);
		while(histories.hasMoreElements()) 
		{
			LifeCycleHistory lcHistory = (LifeCycleHistory)histories.nextElement();
			if(lcHistory.getState().toString().equals("IMPLEMENTATION")){
			    Timestamp changeTime = lcHistory.getCreateTimestamp();
			    return new Timestamp(changeTime.getTime()+28800000).toString().split(" ")[0];
			}
		}
		return "";
	}
	
	private ArrayList<WTChangeOrder2> queryDCN() throws WTException, WTPropertyVetoException {
		ArrayList<WTChangeOrder2> result = new ArrayList<WTChangeOrder2>();

		QuerySpec qs = new QuerySpec();
		int index = qs.appendClassList(WTChangeOrder2.class, true);
		qs.setAdvancedQueryEnabled(true);
		
		
		
		if (status.indexOf("*") >= 0 || "%".equals(status))
        {
            status = replace(status, "*", "%");
            qs.appendWhere(new SearchCondition(WTChangeOrder2.class, WTChangeOrder2.LIFE_CYCLE_STATE, SearchCondition.LIKE, status.trim()), index);
        }else
        {
            qs.appendWhere(new SearchCondition(WTChangeOrder2.class, WTChangeOrder2.LIFE_CYCLE_STATE, SearchCondition.EQUAL, status.trim()), index);
        }
		String number=this.ecrNumber;
		if (!isNullOrBlank(number))
		{
			qs.appendAnd();
			if (number.indexOf("*") >= 0)
			{
				number = replace(number, "*", "%");
				qs.appendWhere(new SearchCondition(WTChangeOrder2.class, WTChangeOrder2.NUMBER, SearchCondition.LIKE, number.trim().toUpperCase()),index);
			}else
			{
				qs.appendWhere(new SearchCondition(WTChangeOrder2.class, WTChangeOrder2.NUMBER, SearchCondition.EQUAL, number.trim().toUpperCase()),index);
			}
		}
		String createTimeStartStr = this.createDateFrom;
		if (!isNullOrBlank(createTimeStartStr))
		{
			Timestamp timestampStart = new Timestamp(new Date().getTime());
			DateHelper datehelper = new DateHelper(createTimeStartStr, "day", SessionHelper.getLocale());
			timestampStart = new Timestamp(datehelper.getDate().getTime());
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTChangeOrder2.class, WTChangeOrder2.CREATE_TIMESTAMP, SearchCondition.GREATER_THAN_OR_EQUAL, timestampStart), index);
		}

		String createTimeEndStr = this.createDateTo;
		if (!isNullOrBlank(createTimeEndStr))
		{
			Timestamp timestampEnd = new Timestamp(new Date().getTime());
			DateHelper datehelper = new DateHelper(createTimeEndStr, "day", SessionHelper.getLocale());
			timestampEnd = new Timestamp(datehelper.getDate().getTime());
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTChangeOrder2.class, WTChangeOrder2.CREATE_TIMESTAMP, SearchCondition.LESS_THAN_OR_EQUAL, timestampEnd), index);
		}
		
		String createUserid = this.user;
		if (!isNullOrBlank(createUserid))
		{
			qs.appendAnd();
			int classIndex1 = qs.appendClassList(WTUser.class, false);
			qs.appendWhere(new SearchCondition(WTUser.class, WTUser.NAME, SearchCondition.EQUAL, getUserName(createUserid)), classIndex1);
			qs.appendAnd();
			SearchCondition sc = new SearchCondition(
					WTChangeOrder2.class,
					"creator.key.id",
                    WTUser.class,
                    "thePersistInfo.theObjectIdentifier.id");
            qs.appendWhere(sc,index,classIndex1);
		}
		
		if (!StringUtils.isEmpty(status) || !StringUtils.isEmpty(number) || !StringUtils.isEmpty(createTimeStartStr) || !StringUtils.isEmpty(createTimeEndStr) || !StringUtils.isEmpty(createUserid)){
			qs.appendAnd();
		}
		
		qs.appendWhere(new SearchCondition(WTChangeOrder2.class, WTChangeOrder2.NUMBER, SearchCondition.LIKE, "DCN%"),index);
		log.debug(qs);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		while(qr.hasMoreElements())
		{
			Persistable[] persist=(Persistable[]) qr.nextElement();
			WTChangeOrder2 dcn=(WTChangeOrder2) persist[0];
			String approvedTimeStartStr = this.approvedDateFrom;
			String approvedTimeEndStr = this.approvedDateTo;
			
            if (!isNullOrBlank(approvedTimeStartStr) || !isNullOrBlank(approvedTimeEndStr)){
                Enumeration histories = (Enumeration) LifeCycleHelper.service.getHistory(dcn);
                Timestamp changeTime=null;
                while(histories.hasMoreElements()){
                    LifeCycleHistory lcHistory = (LifeCycleHistory)histories.nextElement();
                    if(lcHistory.getState().toString().equals("IMPLEMENTATION")){
                        changeTime = lcHistory.getCreateTimestamp();
                    }
                }
                if(changeTime != null){
                    if(!isNullOrBlank(approvedTimeStartStr)){
                        DateHelper datehelper = new DateHelper(approvedTimeStartStr, "day", SessionHelper.getLocale());
                        if(datehelper.getDate().after(changeTime)){
                            continue;
                        }
                    }
                    if(!isNullOrBlank(approvedTimeEndStr)){
                        DateHelper datehelper = new DateHelper(approvedTimeEndStr, "day", SessionHelper.getLocale());
                        if(datehelper.getDate().before(changeTime)){
                            continue;
                        }
                    }
                }else{
                    continue;
                }
            }
			//log.debug("add ecr:"+ecr);
			if(!result.contains(dcn))
				result.add(dcn);
		} 
		
		return result;
	}
}
