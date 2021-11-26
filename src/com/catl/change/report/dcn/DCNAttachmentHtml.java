package com.catl.change.report.dcn;

import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.log4j.Logger;

import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.clients.vc.CheckInOutTaskLogic;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.part.WTPart;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.wip.WorkInProgressHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfVotingEventAudit;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;

import com.catl.change.ChangeUtil;
import com.catl.change.inventory.ECAPartLink;
import com.catl.change.mvc.UsagePartTreesHandler;
import com.catl.change.report.ExportBomDataByPart;
import com.catl.change.report.model.RoleConstant;
import com.catl.change.util.ChangeConst;
import com.catl.change.workflow.DcnWorkflowfuncion;
import com.catl.common.util.ContentUtil;
import com.catl.common.util.DocUtil;
import com.catl.common.util.GenericUtil;
import com.catl.common.util.TypeUtil;
import com.catl.common.util.WorkflowUtil;
import com.ptc.xworks.util.XWorksHelper;

public class DCNAttachmentHtml {

	private static Logger log = Logger.getLogger(DCNAttachmentHtml.class.getName());
	private static String codebase = "";

	static {
		try {
			WTProperties wtproperties = WTProperties.getLocalProperties();
			codebase = wtproperties.getProperty("wt.codebase.location");
		} catch (Throwable t) {
			throw new ExceptionInInitializerError(t);
		}
	}
	
	public static WTObject addAttachment(WTObject object, String attchmentContext, String attachementName) throws IOException {
		
		if(!attchmentContext.equals("")){
			InputStream attSymbleInputStream = new ByteArrayInputStream(attchmentContext.getBytes("UTF-8"));
			
			if (object != null && attSymbleInputStream != null) {

				object = (WTObject) DocUtil.createSECONDARY((ContentHolder) object, attachementName, attSymbleInputStream);
			}
			
			try {
				object = (WTObject) PersistenceHelper.manager.save(object);
				object = (WTObject) PersistenceServerHelper.manager.restore(object);
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return object;
	}
	
	/**
	 * call com.catl.change.report.DCNAttachmentHtml.doCreateECNHtmlReport(wt.change2.WTChangeOrder2,self);
	 * @param dcn
	 * @param self
	 * @throws PropertyVetoException 
	 * @throws WTException 
	 * @throws IOException 
	 */
	public static void doCreateECNHtmlReport(WTChangeOrder2 dcn, ObjectReference self,String type) throws PropertyVetoException, WTException, IOException {
		String filepath = codebase + RoleConstant.defaultLocation + RoleConstant.dcnModelname;
		String newfilepaht = codebase + File.separator + "temp" + File.separator + dcn.getNumber() + "_DCN报告.html";
		log.debug("filepath===" + filepath);
		log.debug("newfilepath===" + newfilepaht);
		log.debug("ecn number==" + dcn.getNumber());
		log.debug("start create html file------------>");
		String attchmentContent ="";
		try {
			attchmentContent = JspToHtmlFile(filepath, newfilepaht, dcn, self,type);
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String filename = dcn.getNumber() + "_DCN报告.html";
		
		ContentHolder ch = (ContentHolder) ContentHelper.service.getContents(dcn);
		
		//移除附件组的附件
		ApplicationData appdate = getAttchmentByDcn(dcn,ChangeConst.H);
		if(appdate!=null){
			removeAttachmentToDcn(dcn,ch,appdate);
		}
		
		dcn = (WTChangeOrder2)PersistenceHelper.manager.refresh(dcn);
		
		//添加附件到对象
		dcn = (WTChangeOrder2)addAttachment(dcn, attchmentContent, filename);
		
		//添加最新附件到附件组
		addAttachmentToDcn(dcn,ch,getAttchmentByDcn(dcn,ChangeConst.H));
	}
	
	/**
	 * 获取对象的对应类型的附件
	 * @throws PropertyVetoException 
	 * @throws WTException 
	 * 1:html 2:excel受影响列表
	 */
	public static ApplicationData getAttchmentByDcn(WTChangeOrder2 dcn,String type) throws WTException, PropertyVetoException{
		Map<String,ApplicationData> attmap = getAttachmentName(dcn);
		ApplicationData appdata = null;
		for(String s : attmap.keySet()){
			if(type.equals(ChangeConst.H)){
				if(s.endsWith(ChangeConst.HTML)){
					appdata = attmap.get(s);
				}
			}else if(type.equals(ChangeConst.EXCEL)){
				if(s.contains(ChangeConst.SYXEXCLE)){
					appdata = attmap.get(s);
				}
			}
		}
		return appdata;
	}
	
	public static void removeAttachmentToDcn(WTChangeOrder2 dcn,ContentHolder ch,ApplicationData appdata) throws WTException, PropertyVetoException{
		Collection<ContentItem> col = new ArrayList<ContentItem>();
		col.add(appdata);
		XWorksHelper.getAttachmentGroupService().removeAttachment(ch, ChangeConst.ATTACHMENT_03, col);
	}
	
	public static void addAttachmentToDcn(WTChangeOrder2 dcn,ContentHolder ch,ApplicationData appdata) throws WTException, PropertyVetoException{
		Collection<ContentItem> col = new ArrayList<ContentItem>();
		col.add(appdata);
		XWorksHelper.getAttachmentGroupService().addAttachment(ch, ChangeConst.ATTACHMENT_03, col);
	}
	
	public static String JspToHtmlFile(String filePath, String HtmlFile, WTChangeOrder2 dcn, ObjectReference self,String type) throws WTException, PropertyVetoException, RemoteException {
		String str = "";
		
		//变更类型,生效日期
		Object values = GenericUtil.getObjectAttributeValue(dcn, "changeType") == null ? "" : GenericUtil.getObjectAttributeValue(dcn, "changeType");
		String changetype = "";
		if (values instanceof String) {
			changetype = (String) values;
		} else {
			Object[] v = (Object[]) values;
			for (int i = 0; i < v.length; i++) {
				String s = (String) v[i];
				changetype = changetype + s + " ";
			}
		}
		
		String effdate = "";
		DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
		if(dcn.getNeedDate()!=null){
			Timestamp time = fixTime(dcn.getNeedDate());
			effdate = format1.format(time);
		}
		
		
		//申请部门(多个DCA)SRC,
		String dept = "";
		int i = 0;
		List<WTChangeActivity2> listdca = ChangeUtil.getChangeActiveities(dcn);
		for(WTChangeActivity2 dca : listdca){
			String tment = GenericUtil.getObjectAttributeValue(dca, "department") == null ? "" : (String)GenericUtil.getObjectAttributeValue(dca, "department");
			if(i<listdca.size()-1){
				tment+=",";
			}
			dept+=tment;
			i++;
		}
		
		//阶段,ENW编号,变更来源,变更主题，变更原因
		String stage = GenericUtil.getObjectAttributeValue(dcn, "stage") == null ? "" : (String)GenericUtil.getObjectAttributeValue(dcn, "stage"); 
		String enwnum = GenericUtil.getObjectAttributeValue(dcn, "enwNumber") == null ? "" : (String)GenericUtil.getObjectAttributeValue(dcn, "enwNumber"); 
		String changefrom = GenericUtil.getObjectAttributeValue(dcn, "changeFrom") == null ? "" : (String)GenericUtil.getObjectAttributeValue(dcn, "changeFrom"); 
		String changeSubject = GenericUtil.getObjectAttributeValue(dcn, "changeSubject") == null ? "" : (String)GenericUtil.getObjectAttributeValue(dcn, "changeSubject"); 
		String changereason = GenericUtil.getObjectAttributeValue(dcn, "changeReason") == null ? "" : (String)GenericUtil.getObjectAttributeValue(dcn, "changeReason");
		
		//是否需要库存处理,是否需要验证计划
		String isneedpmc = "";
		String isneedplan = "";
		Object needpmc = GenericUtil.getObjectAttributeValue(dcn, "CATL_NeedPMC") == null ? "" : GenericUtil.getObjectAttributeValue(dcn, "CATL_NeedPMC");
		Object needplan = GenericUtil.getObjectAttributeValue(dcn, "CATL_Need_Verify") == null ? "" : GenericUtil.getObjectAttributeValue(dcn, "CATL_Need_Verify");
		if(needpmc instanceof Boolean){
			if((boolean)needpmc){
				isneedpmc = "需要";
			}else
				isneedpmc = "不需要";
		}
		if(needplan instanceof Boolean){
			if((boolean)needplan){
				isneedplan = "需要";
			}else
				isneedplan = "不需要";
		}
		
		try {
			String tempStr = "";
			InputStreamReader is = new InputStreamReader(new FileInputStream(new File(filePath)), "UTF-8"); // or
																											// GBK
			BufferedReader br = new BufferedReader(is);
			while ((tempStr = br.readLine()) != null) {
				str = str + tempStr;
			}
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
		str = str.replaceAll("dcrNumber", dcn.getNumber());     		 		//DCN编号
		str = str.replaceAll("ecnNO", enwnum);							 		//ENW编号
		str = str.replaceAll("changeapplicanter", dcn.getCreatorFullName());    //变更申请人
		str = str.replaceAll("changename", dcn.getName());   			 		//变更名称
		
		str = str.replaceAll("changedate", format1.format(dcn.getCreateTimestamp()));   //变更申请日期
		
		str = str.replaceAll("changeorigin", changefrom);       		 		//变更来源
		str = str.replaceAll("changetype", changetype);  				 		//变更类别
		
		str = str.replaceAll("projectphase", stage);       				 		//项目阶段
		str = str.replaceAll("effDate", effdate);		 				 		//(预计)变更生效日期
		str = str.replaceAll("changeitem", changeSubject);				 		//变更主题
		str = str.replaceAll("changereason", changereason);				 		//变更原因
		String dec = "";
		if(dcn.getDescription()!=null){
			dec = dcn.getDescription().toString();
		}
		str = str.replaceAll("changedescription",dec);	 						//变更描述
		str = str.replaceAll("needinventory", isneedpmc);	 					//是否需要库存处理
		str = str.replaceAll("needtestvalidation", isneedplan);					//是否需要验证计划
		
		//=================   Change Data/变更数据   ====================
		String htmlchangepart = "";
		List<DCNAffectedReportodel> afflist = getChangeDataByDcn(dcn);
		TreeMap<String,DCNAffectedReportodel> tmap = new TreeMap<String,DCNAffectedReportodel>();
		
		for(DCNAffectedReportodel model : afflist){
			Object objtype = model.getObj();
			String clistr ="";
			if(objtype instanceof WTPart){
				clistr = "!1_";
			}else if(objtype instanceof EPMDocument){
				clistr = "!2_";
			}else if(objtype instanceof WTPart){
				clistr = "!3_";
			}
			tmap.put(model.getChangedataNumber()+clistr+TypeUtil.getTypeInternalName(model.getObj()), model);
		}
		
		//NavigableMap<String,DCNAffectedReportodel> tempmap = tmap.descendingMap();
		
		for(DCNAffectedReportodel model :tmap.values()){
			htmlchangepart+= "<TR>"+
					"<TD  style='text-align:center' colspan=2>"+model.getChangedataNumber()+"</td>"+
					"<TD  style='text-align:center' colspan=2>"+model.getChangedataName()+"</td>"+
					"<TD  style='text-align:center' colspan=2>"+model.getChangedataversioninfo()+"</td>"+
					"<TD  style='text-align:center' colspan=4>"+model.getChangedatatype()+"</td>"
				+"</TR>";
		}
		
		str = str.replaceAll("ChangePartsData", htmlchangepart);   //Change Parts/变更物料
		
		//=================  Inventoty_disposition/库存处理   ================
		String htmlIventoty = "";
		List<ECAPartLink> ecaplinklist = getInventoty(dcn);
		for(ECAPartLink ecp : ecaplinklist){
			WTPart part = (WTPart)ecp.getRoleBObject();
			htmlIventoty+= "<TR>"+
					"<TD  style='text-align:center' colspan=2 >"+part.getNumber()+"</td>"+
					"<TD  style='text-align:center' colspan=3 >"+part.getName()+"</td>"+
					"<TD  style='text-align:center' colspan=2 >"+ecp.getQuantity()+"</td>"+
					"<TD  style='text-align:center' >"+ecp.getMaterialStatus().getDisplay(Locale.CHINA)+"</td>"+
					"<TD  style='text-align:center' >"+ecp.getDispositionOption().getDisplay(Locale.CHINA)+"</td>"+
					"<TD  style='text-align:center' >"+ format1.format(ecp.getDueDay())+"</td>"
				+"</TR>";
		}
		
		str = str.replaceAll("InventotyDispositionData", htmlIventoty);//Inventoty_disposition/库存处理
		
		//=================  Attachment List/附件列表   =====================
		Map<String,ApplicationData> attmap = getAttachmentName(dcn);
		
		String attname = "";
		for(String s : attmap.keySet()){
			if(!s.contains(ChangeConst.HTML)){
				ContentHolder ch = (ContentHolder) ContentHelper.service.getContents(dcn);
		        String url = ContentUtil.getDownloadUrl(ch, attmap.get(s));
				attname+="<a href='"+url+"' />"+s+"</br>";
			}
		}
		str = str.replaceAll("attachmentList", attname);//Attachment List/附件列表
		
		//=================  流程签审信息   ==================
		if(self != null){
			Persistable per = self.getObject();
			if(per instanceof WfProcess){
				WfProcess process = (WfProcess)per;
				long processOid = process.getPersistInfo().getObjectIdentifier().getId();			//获取流程id
				System.out.println("====processOid:"+processOid);
				if(type.equals("1")){
					//循环处理"FM审核节点的workItem"
					Map<WorkItem, WfVotingEventAudit> managerInfoMap = WorkflowUtil.getCompletedWorkItemInfo(processOid,ChangeConst.FM_SH, null);
					System.out.println("====managerInfoMap:"+managerInfoMap.size());
					
					//循环处理"SE&COST会签节点的workItem"
					Map<WorkItem, WfVotingEventAudit> seInfoMap = WorkflowUtil.getCompletedWorkItemInfo(processOid,ChangeConst.SE_SH, null);
					System.out.println("====seInfoMap:"+seInfoMap.size());
					if(seInfoMap.size() == 0){//兼容旧任务“SE审核”
						seInfoMap = WorkflowUtil.getCompletedWorkItemInfo(processOid,"SE审核", null);
					}
					
					//1.工作流节点“FM审核”各个人员的最后一轮的投票信息，使用下面的格式：投票人全名：<路由>，备注信息.如果有多个用户投票，多个用户的投票信息之间使用一个空行进行分隔
					//2.参与工作流节点“FM审核”的最后一轮的投票的人员的全名，如果有多个用户投票，每个用户的全名使用一个逗号进行分隔；
					//3.工作流节点“FM审核”WfAssignedActivity的完成时间endTime，格式为YYYY-MM-DD
					String managerMsg = "";
					String managerQm = "";
					String maDate = "";
					String seMsg = "";
					String seQm = "";
					String seDate = "";
					for(WorkItem fmitem : managerInfoMap.keySet()){
						Object obj = managerInfoMap.get(fmitem).getEventList();
						managerMsg += managerInfoMap.get(fmitem).getUserRef().getFullName()+":"+""+obj+","+fmitem.getContext().getTaskComments()+"\n";
						managerQm += managerInfoMap.get(fmitem).getUserRef().getFullName()+",";
						WfAssignedActivity fmactivity =  (WfAssignedActivity)fmitem.getSource().getObject();
						maDate = format1.format(fmactivity.getEndTime());
					}
					for(WorkItem cpitem : seInfoMap.keySet()){
						Object obj = seInfoMap.get(cpitem).getEventList();
						seMsg += seInfoMap.get(cpitem).getUserRef().getFullName()+":"+""+obj+","+cpitem.getContext().getTaskComments()+"\n";
						seQm += seInfoMap.get(cpitem).getUserRef().getFullName()+",";
						WfAssignedActivity fmactivity =  (WfAssignedActivity)cpitem.getSource().getObject();
						seDate = format1.format(fmactivity.getEndTime());
					}
					if(DcnWorkflowfuncion.getBranch(dcn).equals("Common")||DcnWorkflowfuncion.getBranch(dcn).equals("EquipECAD")){
						managerQm = managerQm.substring(0, managerQm.length()-1);
					}
					
					//seQm = seQm.substring(0, seQm.length()-1);
					
					//update by szeng 20171025 #REQ64
					/*if(!DcnWorkflowfuncion.isEquipOrECAD(dcn)){
						seQm = seQm.substring(0, seQm.length()-1);
					}*/
					
					if(DcnWorkflowfuncion.getBranch(dcn).equals("Common")){
						seQm = seQm.substring(0, seQm.length()-1);
					}
					
					str = str.replaceAll("mcpmanager", managerMsg);		//Manager结论及建议
					str = str.replaceAll("msmanager", managerQm);		//Manager签名
					str = str.replaceAll("madate", maDate);		     	//maDate/日期
					
					str = str.replaceAll("seproposal", seMsg); 	 		//SE结论及建议
					str = str.replaceAll("sedate", seDate);	 		 	//seDate/日期
					str = str.replaceAll("sesignature", seQm);	 	//SE签名
				}else{
					str = str.replaceAll("mcpmanager", "");		 		//Manager结论及建议
					str = str.replaceAll("msmanager", "");		 		//Manager签名
					str = str.replaceAll("madate", "");		     		//maDate/日期
					str = str.replaceAll("seproposal", ""); 	 		//SE结论及建议
					str = str.replaceAll("sedate", "");			 		//seDate/日期
					str = str.replaceAll("sesignature", "");	 		//SE签名
				}
			}else{
				str = str.replaceAll("mcpmanager", "");		 		//Manager结论及建议
				str = str.replaceAll("msmanager", "");		 		//Manager签名
				str = str.replaceAll("madate", "");		     		//maDate/日期
				str = str.replaceAll("seproposal", ""); 	 		//SE结论及建议
				str = str.replaceAll("sedate", "");			 		//seDate/日期
				str = str.replaceAll("sesignature", "");	 		//SE签名
			}
		}
		
		return str;
	}
	
	/**
	 * 获取DCN关联的库存变更DCA的信息
	 * @param dcn
	 * @return
	 * @throws WTException
	 */
	public static List<ECAPartLink> getInventoty(WTChangeOrder2 dcn) throws WTException{
		List<ECAPartLink> listiventoty = new ArrayList<ECAPartLink>();
		List<WTChangeActivity2> listdca = ChangeUtil.getChangeActiveities(dcn);
		for(WTChangeActivity2 dca : listdca){
			QueryResult queryResult = PersistenceHelper.manager.navigate(dca,ECAPartLink.PART_ROLE, ECAPartLink.class,false);
			while(queryResult.hasMoreElements()){
				Persistable per = (Persistable)queryResult.nextElement();
				if(per instanceof ECAPartLink){
					ECAPartLink ecaplink = (ECAPartLink)per;
					listiventoty.add(ecaplink);
				}
			}
		}
		
		return listiventoty;
	}
	
	/**
	 * 获取DCN的受影响对象
	 * @param 
	 * @return
	 * @throws WTException 
	 * @throws RemoteException 
	 */
	public static List<DCNAffectedReportodel> getChangeDataByDcn(WTChangeOrder2 dcn) throws WTException, RemoteException{
		List<DCNAffectedReportodel> dcnmodellist = new ArrayList<DCNAffectedReportodel>();
		
		List<Object> beforepartlist = ChangeUtil.getBeforeData(dcn).get(ChangeConst.BEFORE_PART);
		List<Object> beforedoclist = ChangeUtil.getBeforeData(dcn).get(ChangeConst.BEFORE_DOC);
		List<Object> beforeepmlist = ChangeUtil.getBeforeData(dcn).get(ChangeConst.BEFORE_EPM);
		for(Object partobj : beforepartlist){
			
			if(partobj instanceof WTPart){
				WTPart beforepart = (WTPart)partobj;
				
				DCNAffectedReportodel dcnmodel = new DCNAffectedReportodel();
				dcnmodel.setObj(beforepart);
				dcnmodel.setChangedataName(beforepart.getName());
				dcnmodel.setChangedataNumber(beforepart.getNumber());
				dcnmodel.setChangedataversioninfo(beforepart.getVersionIdentifier().getValue()+"."+beforepart.getIterationIdentifier().getValue());
				dcnmodel.setChangedatatype(TypeUtil.getTypeDisplayName(beforepart, Locale.CHINA));
				dcnmodellist.add(dcnmodel);
			}
		}
		
		for(Object docobj : beforedoclist){
			
			if(docobj instanceof WTDocument){
				WTDocument beforedoc = (WTDocument)docobj;
				
				DCNAffectedReportodel dcnmodel = new DCNAffectedReportodel();
				dcnmodel.setObj(beforedoc);
				dcnmodel.setChangedataName(beforedoc.getName());
				dcnmodel.setChangedataNumber(beforedoc.getNumber());
				dcnmodel.setChangedataversioninfo(beforedoc.getVersionIdentifier().getValue()+"."+beforedoc.getIterationIdentifier().getValue());
				dcnmodel.setChangedatatype(TypeUtil.getTypeDisplayName(beforedoc, Locale.CHINA));
				dcnmodellist.add(dcnmodel);
			}
		}
		
		for(Object epmobj : beforeepmlist){
			
			if(epmobj instanceof EPMDocument){
				EPMDocument beforeepm = (EPMDocument)epmobj;
				
				DCNAffectedReportodel dcnmodel = new DCNAffectedReportodel();
				dcnmodel.setObj(beforeepm);
				dcnmodel.setChangedataName(beforeepm.getName());
				dcnmodel.setChangedataNumber(beforeepm.getNumber());
				dcnmodel.setChangedataversioninfo(beforeepm.getVersionIdentifier().getValue()+"."+beforeepm.getIterationIdentifier().getValue());
				dcnmodel.setChangedatatype(TypeUtil.getTypeDisplayName(beforeepm, Locale.CHINA));
				dcnmodellist.add(dcnmodel);
			}
		}
		return dcnmodellist;
	}
	
	/**
	 * 获取对象所有附件的名称
	 * @param object
	 * @throws Exception
	 */
	public static Map<String,ApplicationData> getAttachmentName(WTObject object){
		Map<String,ApplicationData> map = new HashMap<String,ApplicationData>();
		try{
			object = (WTObject) PersistenceHelper.manager.refresh(object);
			ContentHolder contentHolder = ContentHelper.service.getContents((ContentHolder) object);
			Vector vData = ContentHelper.getApplicationData(contentHolder);
			log.debug("data size=="+vData.size());
			if (vData != null && vData.size() > 0) {
				for (int i = 0; i < vData.size(); i++){
			    	ApplicationData appData = (ApplicationData) vData.get(i);
			    	log.debug("file name==="+appData.getFileName());
			    	map.put(appData.getFileName(),appData);
		      	}
			}
		} catch (WTException e){
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		} finally{
		}
		return map;
	}
	
	public static Timestamp fixTime(Timestamp timestamp) {
		timestamp = new Timestamp(timestamp.getTime() + 28800000);
		return timestamp;
	}
	
	public static Boolean compareEventtime(WfVotingEventAudit event, Timestamp createtimestamp) {
		// compare activity time
		Boolean isset = false;
		Timestamp timestamp = event.getModifyTimestamp();
		log.debug("timestamp=========" + timestamp);
		log.debug("createtimestamp==========" + createtimestamp);
		if (createtimestamp != null) {
			if (timestamp.after(createtimestamp)) {
				createtimestamp = timestamp;
				isset = true;
			} else {
				isset = false;
			}
		} else {
			createtimestamp = timestamp;
			isset = true;
		}
		return isset;
	}
	
	
	
}
