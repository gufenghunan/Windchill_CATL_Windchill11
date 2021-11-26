package com.catl.doc.maturityUpReport.workflow;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;

import com.catl.change.ChangeUtil;
import com.catl.change.report.Excel2007Handler;
import com.catl.common.constant.ChangeState;
import com.catl.common.constant.ContainerName;
import com.catl.common.constant.RoleName;
import com.catl.common.util.BOMUtil;
import com.catl.common.util.CatlFolderUtil;
import com.catl.common.util.DocUtil;
import com.catl.common.util.IBAUtil;
import com.catl.common.util.PartUtil;
import com.catl.common.util.WCLocationConstants;
import com.catl.doc.maturityUpReport.MaturityProcess;
import com.catl.doc.maturityUpReport.MaturityUpReportHelper;
import com.catl.doc.maturityUpReport.MaturityUpResult;
import com.catl.doc.maturityUpReport.NFAEMaturityUp3DocPartLink;
import com.catl.integration.PIService;
import com.catl.integration.pi.part.maturity.DTZEIVRCreateResponse;
import com.catl.integration.pi.part.maturity.DTZEIVRCreateResponse.TRETURN;
import com.catl.part.PartConstant;
import com.catl.part.classification.ClassificationNodeConfig;
import com.catl.part.classification.NodeConfigHelper;
import com.catl.part.maturity.PartMaturityChangeLogHelper;
import com.catl.promotion.util.WorkflowUtil;

import wt.change2.ChangeActivity2;
import wt.change2.RelevantRequestData2;
import wt.change2.WTChangeRequest2;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.fc.collections.WTArrayList;
import wt.inf.container.WTContainer;
import wt.inf.library.WTLibrary;
import wt.lifecycle.State;
import wt.org.WTPrincipalReference;
import wt.part.WTPart;
import wt.part.WTPartConfigSpec;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartStandardConfigSpec;
import wt.part.WTPartUsageLink;
import wt.pom.Transaction;
import wt.project.Role;
import wt.session.SessionServerHelper;
import wt.team.Team;
import wt.util.WTException;
import wt.util.WTMessage;
import wt.vc.wip.WorkInProgressHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;

public class WfUtil {
	
	public static final String FLAG_SUCCESS = "S";
	public static final String COMPANY = "CATL";
	private static Logger log = Logger.getLogger(WfUtil.class);
	
	public static void completeCheck(WTObject pbo, ObjectReference self, boolean neenPM) throws WTException{
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try{
			WTDocument doc = (WTDocument)pbo;
			if(WorkInProgressHelper.isCheckedOut(doc)){
				throw new WTException("非FAE物料成熟度3升级报告必须已检入！");
			}
			if(!hasAssociatedParts(doc)){
				throw new WTException("非FAE物料成熟度3升级报告必须至少关联一个部件！");
			}
			Set<String> validateRoles = new HashSet<String>();
			/*validateRoles.add(RoleName.PRODUCT_QUALITY_ENGINEER);
			if(neenPM){
				validateRoles.add(RoleName.PROJECT_MANAGER);
			}*/
			
			validateRoles.add(RoleName.PRODUCT_DATA_ENGINGEER);
			checkNeedRole(self, validateRoles);
		} finally {			
			SessionServerHelper.manager.setAccessEnforced(flag);
		}
	}
	
	private static boolean hasAssociatedParts(WTDocument doc) throws WTException{
		return (MaturityUpReportHelper.getNFAEMaturityUp3DocPartLink((WTDocumentMaster)doc.getMaster()).size() > 0);
	}
	
	public static void cancleHandle(WTObject pbo) throws WTException{
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try{
			WTDocument doc = (WTDocument)pbo;
			MaturityUpReportHelper.removeAllLinks((WTDocumentMaster)doc.getMaster());
		} finally {			
			SessionServerHelper.manager.setAccessEnforced(flag);
		}
	}
	
	public static boolean neenPM(WTObject pbo) throws WTException{
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try{
			WTDocument doc = (WTDocument)pbo;
			Set<String> numbers = MaturityUpReportHelper.getAllLinkPartNumbers((WTDocumentMaster)doc.getMaster());
			for (String number : numbers) {
				if(number.startsWith("P")){
					return true;
				}
			}
			return false;
		} finally {			
			SessionServerHelper.manager.setAccessEnforced(flag);
		}
		
	}
	
	public static void checkNeedRole(ObjectReference self,Set<String> validateRoles) throws WTException{
		WfAssignedActivity wfaa = (WfAssignedActivity)self.getObject();
		Set<String> noUserRoles = validateNeedRoles(wfaa, validateRoles);
		if(!noUserRoles.isEmpty()){
			String roles = StringUtils.join(noUserRoles, ",");
			throw new WTException(WTMessage.formatLocalizedMessage("下面的角色: \"{0}\", 必须设置至少一个用户!", new Object[]{roles}));
		}
	}

	@SuppressWarnings("rawtypes")
	public static Set<String> validateNeedRoles(WfAssignedActivity wfaa, Set<String> validateRoles) throws WTException {
        Set<String> noUsers = new HashSet<String>();
        WfProcess process = wfaa.getParentProcess();
		Locale locale = wt.session.SessionHelper.getLocale();
		
        Team team = (Team) process.getTeamId().getObject();
        Enumeration roles = process.getRoles();
        Enumeration enumPrin = null;
        while (roles.hasMoreElements()) {
            Role role = (Role) roles.nextElement();
            String roleName = role.toString();
            if(validateRoles.contains(roleName)){
                enumPrin = team.getPrincipalTarget(role);
                if (!enumPrin.hasMoreElements()) {
                    noUsers.add(role.getDisplay(locale));
                }   
            }
        }
        return noUsers;
    }
	
//	public static WTArrayList updatedMaturityParts(WTObject pbo) throws WTException{
//		WTArrayList list = new WTArrayList();
//		WTDocument doc = (WTDocument)pbo;
//		Set<String> partNumbers = MaturityUpReportHelper.getAllLinkPartNumbers((WTDocumentMaster)doc.getMaster());
//		for (String number : partNumbers) {
//			WTPart part = PartUtil.getLastestWTPartByNumber(number);
//			List<MaturityUpResult> results = new ArrayList<MaturityUpResult>();
//			if(upMaturityTo3(part, results)){
//				for (MaturityUpResult result : results) {
//					list.add(result.getMaster());
//				}
//			}
//		}
//		return list;
//	}
	
	public static MaturityProcess updatedMaturityParts(WTObject pbo, ObjectReference self) throws WTException{
		WTArrayList list = new WTArrayList();
		WTDocument doc = (WTDocument)pbo;
		
		HashMap<WTPart,Object[]> map =  new HashMap<WTPart,Object[]>();
		Set<String> partNumbers = MaturityUpReportHelper.getAllLinkPartNumbers((WTDocumentMaster)doc.getMaster());
		Transaction transaction = null;
		try{
			transaction = new Transaction();
			transaction.start();
			
			for (String number : partNumbers) {
				Object[] resultInfo = new Object[2];//0:是否升级成功；1：成熟度升级的详细信息
				WTPart part = PartUtil.getLastestWTPartByNumber(number);
				List<MaturityUpResult> results = new ArrayList<MaturityUpResult>();
				if(upMaturityTo3(part, results)){
					for (MaturityUpResult result : results) {
						if(result.isCheckPass()){
							list.add(result.getMaster());
						}
					}
					resultInfo[0] = true;
					addPartMaturityChangeLog(self, results, "1", "3");
				}
				else {
					resultInfo[0] = false;
				}
				resultInfo[1] = results;
				map.put(part, resultInfo);
			}
			
			String reportDownloadURL = WTMessage.formatLocalizedMessage("<a href='{0}' target='_blank'>点击下载报告</a>", new Object[]{addAttachment(map, self)});
			log.info("==reportDownloadURL:"+reportDownloadURL);
			
			transaction.commit();
			transaction = null;
			
			MaturityProcess maturityProcess = new MaturityProcess();
			maturityProcess.setWTArrayList(list);
			maturityProcess.setResult(getHTMLResult(map));
			maturityProcess.setUrl(reportDownloadURL);
			
			return maturityProcess;
		} finally {
			if (transaction != null) {
				transaction.rollback();
				transaction = null;
			}
		}
	}
	
	public static void addPartMaturityChangeLog(ObjectReference self, List<MaturityUpResult> results, String oldMaturity, String newMaturity) throws WTException{
		WfProcess process = (WfProcess) self.getObject();
		WTPrincipalReference operator = process.getCreator();
		for (MaturityUpResult result : results) {
			if(result.isCheckPass()){
				String partNumber = result.getMaster().getNumber();
				WTPart latestPart = PartUtil.getLastestWTPartByNumber(partNumber);
				PartMaturityChangeLogHelper.addPartMaturityChangeLog(operator, latestPart, oldMaturity, newMaturity);
			}
		}
	}
	
	private static String getHTMLResult(HashMap<WTPart,Object[]> map){
		StringBuilder dataListTabe = new StringBuilder("<table border=\"1\" cellspacing=\"0\" style=\"border-collapse:collapse;\">");
		String header = "<thead><tr><td><b>物料编号</b></td><td><b>物料名称</b></td><td><b>创建者</b></td><td><b>修改者</b></td><td><b>成熟度标识</b></td><td><b>升级结果</b></td></tr></thead>";
		dataListTabe.append(header).append("<tbody>");
		for(WTPart part : map.keySet()){
			dataListTabe.append(getRowInfoStr(part, (Boolean)map.get(part)[0]));
		}
		dataListTabe.append("</tbody></table>");
		log.info(dataListTabe.toString());
		return dataListTabe.toString();
	}
	
	private static String getRowInfoStr(WTPart part, boolean isSuccess){
		StringBuilder row = new StringBuilder("<tr>");
		row.append("<td>").append(part.getNumber()).append("</td>");
		row.append("<td>").append(part.getName()).append("</td>");
		row.append("<td>").append(part.getCreatorFullName()).append("</td>");
		row.append("<td>").append(part.getModifierFullName()).append("</td>");
		String maturity = (String)IBAUtil.getIBAValue(part.getMaster(), PartConstant.IBA_CATL_Maturity);
		row.append("<td>").append(maturity).append("</td>");
		if(isSuccess){
			row.append("<td>").append("升级成功").append("</td>");
		}
		else {
			row.append("<td>").append("升级失败").append("</td>");
		}
		row.append("</tr>");
		return row.toString();
	}
	
	private static String addAttachment(HashMap<WTPart,Object[]> map, ObjectReference self) throws WTException{
		try {
			WfProcess process = (WfProcess) self.getObject();
			Workbook xlsx = exportReport(map);
			ByteArrayOutputStream otputStream = new ByteArrayOutputStream();
			xlsx.write(otputStream);
			ByteArrayInputStream inputStream = new ByteArrayInputStream(otputStream.toByteArray());
			otputStream.close();
			return WorkflowUtil.replaceSecondaryContentWithNoCheckOut(process, "非FAE物料成熟度1升级3报告.xlsx", inputStream, "生成的报告上传到附件", null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
			throw new WTException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private static Workbook exportReport(HashMap<WTPart,Object[]> map) throws WTException {
		String filePathName = WCLocationConstants.WT_CODEBASE + File.separator + "com" + File.separator + "catl" + File.separator + "checkPDFData"
				+ File.separator + "MaturityOneToThree_template.xlsx";
		try {
			ArrayList<WTPart> keys = new ArrayList<WTPart>(map.keySet());
			Excel2007Handler excelHander = new Excel2007Handler(filePathName);
			Workbook xlsx = excelHander.getWorkbook();
			xlsx.setSheetName(0, keys.get(0).getNumber());
			for(int i=1; i<keys.size(); i++){
				xlsx.cloneSheet(0);
				xlsx.setSheetName(i, keys.get(i).getNumber());
			}
			for (WTPart part : keys) {
				Object[] objs = map.get(part);
				boolean isSuccess = (Boolean)objs[0];
				List<MaturityUpResult> list = (List<MaturityUpResult>)objs[1];
				exportReportSheet(excelHander, list, isSuccess, part.getNumber());
			}
			return xlsx;
		} catch (Exception e) {
			e.printStackTrace();
			throw new WTException(e.getLocalizedMessage());
		}
	}
	
	private static void exportReportSheet(Excel2007Handler excelHander, List<MaturityUpResult> list, boolean isSuccess, String sheetName) throws WTException{
		excelHander.switchCurrentSheet(sheetName);
		for (int i = 0; i < list.size(); i++) {
			int rowNum = i + 2;
			int iCol = 0;

			StringBuilder msg = new StringBuilder();
			String result = "";
			if (isSuccess) {
				result = "已升级";
			} else {
				result = "未升级";
				MaturityUpResult resultInfo = list.get(i);
				if (!resultInfo.isCheckPass()) {
					int j = 1;
					List<String> errorMsgs = resultInfo.getErrorMsgs();
					for (String errorMsg : errorMsgs) {
						msg.append(WTMessage.formatLocalizedMessage("{0}. {1}。\n", new Object[]{j++,errorMsg}));
					}
					msg.deleteCharAt(msg.lastIndexOf("\n"));
				}
			}
			
			WTPart latesedPart = PartUtil.getLastestWTPartByNumber(list.get(i).getMaster().getNumber());
			excelHander.setStringValue(rowNum, iCol++, latesedPart.getNumber());
			excelHander.setStringValue(rowNum, iCol++, latesedPart.getName());
			excelHander.setStringValue(rowNum, iCol++, result);
			excelHander.setStringValue(rowNum, iCol++, msg.toString());
			excelHander.setStringValue(rowNum, iCol++, latesedPart.getCreatorFullName());
			excelHander.setStringValue(rowNum, iCol++, latesedPart.getModifierFullName());
			excelHander.setStringValue(rowNum, iCol++, CatlFolderUtil.getLocationString(latesedPart));
		}
	}
	
	public static String sendERP(WTArrayList sendErpFailedParts, WTArrayList updatedMaturityParts){
		StringBuilder errorMsg = new StringBuilder();
		ArrayList<String[]> sendInfos = new ArrayList<String[]>(); 
		if(sendErpFailedParts.isEmpty()){
			for (Object object : updatedMaturityParts) {
				ObjectReference ref = (ObjectReference)object;
				String[] info = new String[2];
				info[0] = ((WTPartMaster)ref.getObject()).getNumber();//编码
				info[1] = "3";//成熟度
				sendInfos.add(info);
			}
		}
		else {
			for (Object object : sendErpFailedParts) {
				ObjectReference ref = (ObjectReference)object;
				String[] info = new String[2];
				info[0] = ((WTPartMaster)ref.getObject()).getNumber();//编码
				info[1] = "3";//成熟度
				sendInfos.add(info);
			}
		}
		if(sendInfos.size() > 0){
			try {
				PIService service = PIService.getInstance();
				DTZEIVRCreateResponse response = service.sendMaturityNFAE(COMPANY, sendInfos);
				if(!isSuccess(response.getEACKNOW().getResult())){
					List<TRETURN> result = response.getTRETURN();
					for (TRETURN tr : result) {
						WTPartMaster master = PartUtil.getWTPartMaster(tr.getMATNR());
						if(isSuccess(tr.getSTATUS())){
							if(sendErpFailedParts.contains(master)){
								sendErpFailedParts.remove(master);
							}
						}
						else {
							if(!sendErpFailedParts.contains(master)){
								sendErpFailedParts.add(master);
							}
							errorMsg.append(WTMessage.formatLocalizedMessage("部件[{0}]发送ERP失败，反馈消息：{1} \n", new Object[]{tr.getMATNR(),tr.getMESSAGE()}));
						}
					}
				}
				else {
					sendErpFailedParts.clear();
				}
			} catch (Throwable e) {
				e.printStackTrace();
				if(StringUtils.isNotBlank(e.getLocalizedMessage())){
					errorMsg.append(e.getLocalizedMessage());
				}
				else {
					errorMsg.append(e.toString());
				}
			}
		}
		return errorMsg.toString();
	}
	
	private static boolean isSuccess(String result){
		return StringUtils.equals(result, FLAG_SUCCESS);
	}
	
	public static boolean upMaturityTo3(WTPart part, List<MaturityUpResult> list) throws WTException{
		if(list == null){
			return false;
		}
		if(maturityIsOne(part)){
			boolean success = true;
			Set<WTPart> allPart = new HashSet<WTPart>();
			allPart.add(part);
			WTArrayList parents = new WTArrayList();
			parents.add(part);
			WTPartConfigSpec config = WTPartHelper.service.findWTPartConfigSpec();
			WTPartStandardConfigSpec standardConfig = config.getStandard();
			fetchAllChildren(allPart, parents, standardConfig);
			for (WTPart checkPart : allPart) {
				MaturityUpResult result = checkPart(checkPart);
				if(!result.isCheckPass()){
					success = false;
				}
				list.add(result);
			}
			if(success){
				WTArrayList updates = new WTArrayList();
				for (WTPart checkPart : allPart) {
					Persistable p = IBAUtil.setIBAVaue(checkPart.getMaster(), PartConstant.IBA_CATL_Maturity, "3");
					updates.add(p);
				}
				if(!updates.isEmpty()){
					PersistenceHelper.manager.save(updates);
				}
			}
			return success;
		}
		else {
			MaturityUpResult result = new MaturityUpResult();
			result.setMaster((WTPartMaster)part.getMaster());
			result.setCheckPass(false);
			list.add(result);
			return true;
		}
	}
	
	private static MaturityUpResult checkPart(WTPart part) throws WTException{
		MaturityUpResult result = new MaturityUpResult();
		result.setMaster((WTPartMaster)part.getMaster());
		List<String> errorMsgs = new ArrayList<String>();
		part = PartUtil.getLastestWTPartByNumber(part.getNumber());
		if(!State.RELEASED.equals(part.getLifeCycleState())){
			errorMsgs.add("状态不为已发布");
		}
		String faeStatus = (String) IBAUtil.getIBAValue(part.getMaster(), PartConstant.IBA_CATL_FAEStatus);
		if(!StringUtils.equals(faeStatus, PartConstant.CATL_FAEStatus_1)){
			errorMsgs.add(WTMessage.formatLocalizedMessage("当前FAE状态为“{0}”，需要先通过FAE流程完成成熟度升级", new Object[]{faeStatus}));
		}
		else if(needNonFAEReport(part) && !hashReleasedFAEReport(part)){
			errorMsgs.add("物料没有关联已发布的“非FAE物料成熟度3升级报告”");
		}
		String maturity = (String)IBAUtil.getIBAValue(part.getMaster(), PartConstant.IBA_CATL_Maturity);
		if(!StringUtils.equals(maturity, "1")){
			errorMsgs.add(WTMessage.formatLocalizedMessage("当前成熟度为“{0}”，必须为“1”", new Object[]{maturity}));
		}
		String undoneECRAD = getECRADUndone(part);
		if(StringUtils.isNotBlank(undoneECRAD)){
			errorMsgs.add(WTMessage.formatLocalizedMessage("部件正在变更中，已被加入ECR/ECA/DCA {0}的受影响对象列表中", new Object[]{undoneECRAD}));
		}
		if (isBatteryCellPart(part) && StringUtils.equals(maturity, "1")) {
			errorMsgs.add(WTMessage.formatLocalizedMessage("零部件为电芯且成熟度为1, 必须先由电芯件管理员升级到3或者6", new Object[]{}));
		}
		
		if(errorMsgs.isEmpty()){
			result.setCheckPass(true);
		}
		else {
			result.setCheckPass(false);
			result.setErrorMsgs(errorMsgs);
		}
		return result;
	}
	
	public static boolean isBatteryCellPart(WTPart part) {
		WTContainer container = part.getContainer();
		if(container instanceof WTLibrary){
			WTLibrary library = (WTLibrary)container;
			String containername = library.getName();
			if (containername.startsWith(ContainerName.BATTERY_LIBRARY_NAME)) {
				return true;
			}
		}
		return false;
	}
	
	private static void fetchAllChildren(Set<WTPart> allParts,WTArrayList parents,WTPartStandardConfigSpec standardConfig) throws WTException{
		WTArrayList children = new WTArrayList();
		Persistable[][][] linkInfos = WTPartHelper.service.getUsesWTParts(parents, standardConfig);
		for (Persistable[][] linkInfo : linkInfos) {
			if (linkInfo == null) {
				continue;
			}
			for (Persistable[] childInfo : linkInfo) {
				if(childInfo == null){
					continue;
				}
				WTPartUsageLink usageLink = (WTPartUsageLink)childInfo[0];
				for(WTPart substitute : BOMUtil.getSubstitutes(usageLink)){
					if(maturityIsOne(substitute)){
						allParts.add(substitute);
						children.add(substitute);
					}
				}
				WTPart child = (WTPart) childInfo[1];
				if(maturityIsOne(child)){
					allParts.add(child);
					children.add(child);
				}
				
			}
		}
		if (children.size() > 0) {
			fetchAllChildren(allParts, children, standardConfig);
		}
	}
	
	private static boolean maturityIsOne(WTPart part){
		String maturity = (String)IBAUtil.getIBAValue(part.getMaster(), PartConstant.IBA_CATL_Maturity);
		return StringUtils.equals(maturity, "1");
	}
	
	/**
	 * 部件被加入的未完成变更（ECR,ECA,DCA）的受影响对象中
	 * @throws WTException 
	 */
	private static String getECRADUndone(Persistable persistable) throws WTException {
		try {
			Set<String> numbers = new HashSet<String>();
			QueryResult qr = PersistenceHelper.manager.navigate(persistable, RelevantRequestData2.CHANGE_REQUEST2_ROLE, RelevantRequestData2.class, true);
			while (qr.hasMoreElements()) {
				WTChangeRequest2 cr = (WTChangeRequest2) qr.nextElement();
				if (!cr.getState().toString().equalsIgnoreCase(ChangeState.RESOLVED) && !cr.getState().toString().equalsIgnoreCase(ChangeState.CANCELLED)) {
					numbers.add(cr.getNumber());
				}
			}

			ChangeActivity2 eca = ChangeUtil.getEcaWithPersiser(persistable);
			if (eca != null) {
				numbers.add(eca.getNumber());
			}
			if(!numbers.isEmpty()){
				return numbers.toString();
			}
		} catch (WTException e) {
			e.printStackTrace();
			throw e;
		}
		return null;
	}
	
	private static boolean needNonFAEReport(WTPart part) throws WTException{
		String cls = (String)IBAUtil.getIBAValue(part, PartConstant.IBA_CLS);
		ClassificationNodeConfig nodeConfig = NodeConfigHelper.getNodeConfig(cls);
		if(nodeConfig != null){
			return nodeConfig.getNeedNonFaeReport();
		}
		return false;
	}
	
	private static boolean hashReleasedFAEReport(WTPart part) throws WTException{
		NFAEMaturityUp3DocPartLink link = MaturityUpReportHelper.getNFAEMaturityUp3DocPartLink((WTPartMaster)part.getMaster());
		if(link != null){
			WTDocumentMaster master = link.getDocMaster();
			WTDocument doc =DocUtil.getLatestWTDocument(master.getNumber());
			return State.RELEASED.equals(doc.getLifeCycleState());
		}
		return false;
	}
	
	/**
	 * 判断 流程下会签角色是否有参与者
	 * @param self
	 * @return
	 * @throws WTException
	 */
	public static boolean hasCPRoleByProcess(ObjectReference self) throws WTException{
		WfProcess process = null;
		Object obj = self.getObject();
		if(obj instanceof WfAssignedActivity){
			WfAssignedActivity activity = (WfAssignedActivity) obj;
			process = activity.getParentProcess();
		}else if(obj instanceof WfProcess){
			process = (WfProcess) obj;
		}
		
		Enumeration cp_member = process.getPrincipals(Role.toRole(RoleName.COUNTERSIGN_PEOPLE));
		
		if(cp_member.hasMoreElements()){
			return true;
		}
		
		return false;
	}
}
