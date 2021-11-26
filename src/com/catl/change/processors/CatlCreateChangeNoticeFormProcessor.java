package com.catl.change.processors;

import java.beans.PropertyVetoException;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import wt.access.AccessControlHelper;
import wt.access.AccessPermission;
import wt.access.AdHocAccessKey;
import wt.access.AdHocControlled;
import wt.change2.WTChangeOrder2;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.fc.ReferenceFactory;
import wt.iba.value.IBAHolder;
import wt.log4j.LogR;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.catl.change.ChangeUtil;
import com.catl.change.util.ChangeConst;
import com.catl.change.workflow.DcnWorkflowfuncion;
import com.catl.change.workflow.ECWorkflowUtil;
import com.catl.common.util.PropertiesUtil;
import com.catl.loadData.IBAUtility;
import com.catl.part.PartConstant;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.change2.forms.ChangeManagementFormProcessorHelper;
import com.ptc.windchill.enterprise.change2.forms.processors.CreateChangeNoticeFormProcessor;

public class CatlCreateChangeNoticeFormProcessor extends CreateChangeNoticeFormProcessor {
	private static final Logger log = LogR.getLogger(CatlCreateChangeNoticeFormProcessor.class.getName());

	public FormResult postProcess(NmCommandBean nmcommandbean, List<ObjectBean> list) throws WTException {
		log.debug("-----------------postProcess------------------------------>>>");
		FormResult formresult = new FormResult();

		formresult.setStatus(FormProcessingStatus.SUCCESS);
		try {
			formresult = super.postProcess(nmcommandbean, list);
			ChangeManagementFormProcessorHelper.processSubmitNow(nmcommandbean, list);
		} catch (Exception exception) {
			formresult = ChangeManagementFormProcessorHelper.handleFormResultException(formresult, getLocale(), exception, getProcessorErrorMessage());
		}
		
        WTChangeOrder2 ecn=(WTChangeOrder2)list.get(0).getObject();
        log.debug("ecn ==========="+ecn);
        
        String ecotype = ChangeUtil.getStrSplit(ecn);
        
        WTUser previous = (WTUser) SessionHelper.manager.getPrincipal();
        //如果是DCN启动流程,否则按原来逻辑处理
        if(ecotype.equals(ChangeConst.CHANGEORDER_TYPE_DCN)){
        	try{
        		IBAUtility iba = new IBAUtility(ecn);
				Vector vector = iba.getIBAValues("changeType");
				//System.out.println("size\t"+vector.size());
				if(vector.size()>1){
					if(vector.contains("开模图纸升级")){
						throw new WTException("变更类型已选择“开模图纸升级”，不允许再选择其他变更类型！");
					}else if(vector.contains("EBUS箱体间线束总成变更")){
						throw new WTException("变更类型已选择“EBUS箱体间线束总成变更”，不允许再选择其他变更类型！");
					}else if(vector.contains("ECAD图纸变更")){
						throw new WTException("变更类型已选择“ECAD图纸变更”，不允许再选择其他变更类型！");
					}else if(vector.contains("设备开发变更")){
						throw new WTException("变更类型已选择“设备开发变更”，不允许再选择其他变更类型！");
					}
				}
	        	//处理
				WTPrincipal wtadministrator = SessionHelper.manager.getAdministrator();
				SessionContext.setEffectivePrincipal(wtadministrator);
				WTPrincipalReference wtprincipalreference = (WTPrincipalReference) (new ReferenceFactory()).getReference(previous);
				AccessControlHelper.manager.addPermission((AdHocControlled) ecn, wtprincipalreference, AccessPermission.MODIFY_IDENTITY, AdHocAccessKey.WNC_ACCESS_CONTROL);
		        ECWorkflowUtil.CreateDCNNumber(ecn);
        	}finally{
				SessionContext.setEffectivePrincipal(previous);
			}
        	
	        //启动流程
        	DcnWorkflowfuncion.start_workflow(ecn,"1");
        	
        	ChangeUtil.setIBABooleanValue(ecn,PartConstant.CATL_Allow_Edit, true);
        	ChangeUtil.setIBAStringValue(ecn,PartConstant.CATL_Allowed_DCA, "A_B");
        }else{
		
			
			try {
				// 设置当前权限为管理员
				WTPrincipal wtadministrator = SessionHelper.manager.getAdministrator();
				// 取得当前用户
				SessionContext.setEffectivePrincipal(wtadministrator);
				WTPrincipalReference wtprincipalreference = (WTPrincipalReference) (new ReferenceFactory()).getReference(previous);
				AccessControlHelper.manager.addPermission((AdHocControlled) ecn, wtprincipalreference, AccessPermission.MODIFY_IDENTITY,
						AdHocAccessKey.WNC_ACCESS_CONTROL);
				ECWorkflowUtil.CreateEcoNumber(ecn);
			} finally {
				SessionContext.setEffectivePrincipal(previous);
			}
			
			checkECNAttachments(ecn);
        }
		return formresult;
	}

	public static void checkECNAttachments(WTChangeOrder2 ecn) throws WTException {

		ContentHolder doc = null;
		try {
			doc = ContentHelper.service.getContents(ecn);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		Vector vApplicationData = ContentHelper.getApplicationData(doc);
		log.debug("ecn attachments ===" + ecn.getNumber() + "attachements ===" + vApplicationData.size());
		if(vApplicationData.size() < Integer.valueOf(PropertiesUtil.getValueByKey("ECNAttachmentsCount"))){
			throw new WTException("您创建的变更通告必须上载变更报告");
		}
		/*boolean isPPT = false;
		for (int i = 0; i < vApplicationData.size(); i++) {
			ApplicationData data = (ApplicationData) vApplicationData.get(i);
			String fileName = data.getFileName();
			if (fileName.endsWith(".ppt") || fileName.endsWith(".pptx")) {
				isPPT = true;
			}
		}
		if (!isPPT) {
			throw new WTException("您创建的变更通告没有添加PPT格式的附件，请上传附件！");
		}*/
	}

}
