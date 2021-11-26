package com.catl.promotion.processor;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import wt.log4j.LogR;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTMessage;
import wt.workflow.engine.WfProcess;
import com.catl.promotion.resource.promotionResource;
import com.catl.promotion.util.PromotionConst;
import com.catl.promotion.util.WorkflowUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class NotValidSuperstratumProcessor extends DefaultObjectFormProcessor {
	
	private static final String CLASSNAME = NotValidSuperstratumProcessor.class.getName();
	private static final Logger log = LogR.getLogger(CLASSNAME);
	
	@Override
    public FormResult doOperation(NmCommandBean clientData, List<ObjectBean> objectBeans) throws WTException {
		
		log.trace("Enter not valid superstratum bom materials report");
		FormResult formResult = super.doOperation(clientData, objectBeans);
		
		String workFlowName = "CATL_无有效上层BOM的物料报表输出流程";
		String OrgName = "CATL";
		WfProcess WfProcess = WorkflowUtil.startWorkFlowNullPBO(workFlowName, OrgName);
		String state = WfProcess.getState().toString();		
		ArrayList<Object> object = new ArrayList<Object>();
		object.add(WfProcess);
		
		String MSGRB = "com.ptc.core.ui.successMessagesRB";
        WTMessage msgTitle = new WTMessage(MSGRB, com.ptc.core.ui.successMessagesRB.OBJECT_CREATE_SUCCESSFUL_TITLE, (Object[]) null);
        WTMessage msgBody = new WTMessage(PromotionConst.RESOURCE, promotionResource.CREATE_SUCCEED_MESSAGE_1, (Object[]) null);
        if (!state.equals("OPEN_RUNNING")) {
        	msgBody = new WTMessage(PromotionConst.RESOURCE, promotionResource.CREATE_SUCCEED_MESSAGE_2, (Object[]) null);
        }
		FeedbackMessage fm = new FeedbackMessage(FeedbackType.SUCCESS, SessionHelper.getLocale(), msgTitle, (ArrayList<String>) null, object, false, msgBody);
		formResult.addFeedbackMessage(fm);
		
		log.trace("Exit not valid superstratum bom materials report");
		return formResult;		
	}
}
