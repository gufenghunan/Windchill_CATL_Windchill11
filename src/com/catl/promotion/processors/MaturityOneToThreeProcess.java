package com.catl.promotion.processors;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import wt.inf.container.WTContainerRef;
import wt.log4j.LogR;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTMessage;
import wt.workflow.engine.WfProcess;

import com.catl.promotion.resource.promotionResource;
import com.catl.promotion.util.PromotionConst;
import com.catl.promotion.util.WorkflowUtil;
import com.catl.promotion.workflow.DesignDisabledExprFunction;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class MaturityOneToThreeProcess extends DefaultObjectFormProcessor {

	private static final String CLASSNAME = MaturityOneToThreeProcess.class.getName();
	private static final Logger log = LogR.getLogger(CLASSNAME);

	@Override
	public FormResult doOperation(NmCommandBean clientData, List<ObjectBean> objectBeans) throws WTException {

		log.trace("Enter not valid superstratum bom materials report");
		FormResult formResult = super.doOperation(clientData, objectBeans);
		WTMessage msgBody = null;

		NmOid ActionId = clientData.getActionOid();
		Object obj = ActionId.getRefObject();
		ArrayList<Object> object = new ArrayList<Object>();
		if (obj instanceof WTPart) {
			WTPart part = (WTPart) obj;
			WTContainerRef containerRef = part.getContainerReference();
			String workFlowName = "CATL_非FAE物料成熟度1升级3流程";
			WfProcess WfProcess = WorkflowUtil.startWorkFlowNullPBO(workFlowName, containerRef);

			DesignDisabledExprFunction.setVariableValue(PromotionConst.Part, part, WfProcess);

			msgBody = new WTMessage(PromotionConst.RESOURCE, promotionResource.CREATE_SUCCEED_MESSAGE_4, new Object[] { part.getNumber() });
			String state = WfProcess.getState().toString();
			if (!state.equals("OPEN_RUNNING")) {
				msgBody = new WTMessage(PromotionConst.RESOURCE, promotionResource.CREATE_SUCCEED_MESSAGE_5, new Object[] { part.getNumber() });
			}
			object.add(WfProcess);
		}

		String MSGRB = "com.ptc.core.ui.successMessagesRB";
		WTMessage msgTitle = new WTMessage(MSGRB, com.ptc.core.ui.successMessagesRB.OBJECT_CREATE_SUCCESSFUL_TITLE, (Object[]) null);
		FeedbackMessage fm = new FeedbackMessage(FeedbackType.SUCCESS, SessionHelper.getLocale(), msgTitle, (ArrayList<String>) null, object, false, msgBody);
		formResult.addFeedbackMessage(fm);

		log.trace("Exit not valid superstratum bom materials report");
		return formResult;
	}
}
