package com.catl.change.processors;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import wt.access.AccessControlHelper;
import wt.access.AccessPermission;
import wt.access.AdHocAccessKey;
import wt.access.AdHocControlled;
import wt.change2.WTChangeOrder2;
import wt.fc.ReferenceFactory;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.catl.change.ChangeUtil;
import com.catl.change.util.ChangeConst;
import com.catl.change.workflow.DcnWorkflowfuncion;
import com.catl.change.workflow.ECWorkflowUtil;
import com.catl.loadData.IBAUtility;
import com.catl.part.PartConstant;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.change2.forms.ChangeManagementFormProcessorHelper;
import com.ptc.windchill.enterprise.change2.forms.processors.EditChangeNoticeFormProcessor;

public class CatlEditChangeNoticeFormProcessor extends EditChangeNoticeFormProcessor{

    private static Logger log = Logger.getLogger(CatlEditChangeNoticeFormProcessor.class.getName());
    public FormResult postProcess(NmCommandBean nmcommandbean, List<ObjectBean> list)
        throws WTException
    {
        FormResult formresult = new FormResult();
        formresult.setStatus(FormProcessingStatus.SUCCESS);
        try
        {
            formresult = super.postProcess(nmcommandbean, list);
            handleSubmitNow(nmcommandbean, list);
        }
        catch(Exception exception)
        {
            formresult = ChangeManagementFormProcessorHelper.handleFormResultException(formresult, getLocale(), exception, getProcessorErrorMessage());
        }
        WTChangeOrder2 ecn=(WTChangeOrder2)list.get(0).getObject();
        log.debug("edit ecn ==========="+ecn);
        
        String ecotype = ChangeUtil.getStrSplit(ecn);
        if(ecotype.equals(ChangeConst.CHANGEORDER_TYPE_ECN)){
        	 CatlCreateChangeNoticeFormProcessor.checkECNAttachments(ecn);
        }else if(ecotype.equals(ChangeConst.CHANGEORDER_TYPE_DCN)){
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
        }
        return formresult;
    }
}
