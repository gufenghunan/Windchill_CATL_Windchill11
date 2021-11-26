package com.catl.part.processors;

import java.io.Serializable;

import com.catl.part.classification.NodeConfigHelper;
import com.catl.part.classification.RefreshFAEStatusUtil;
import com.catl.part.classification.resource.CATLNodeConfigRB;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import wt.util.WTException;
import wt.util.WTMessage;

public class RefreshPartFAEStatusCommands implements Serializable {
	
	private static final long serialVersionUID = -3129863975221253750L;
	
	public static FormResult refreshPartFAEStatus(NmCommandBean bean) throws WTException {
		FormResult result = new FormResult();
		Object obj = bean.getActionOid().getRefObject();
		if(obj instanceof LWCStructEnumAttTemplate){
			LWCStructEnumAttTemplate node = (LWCStructEnumAttTemplate)obj;
			if(NodeConfigHelper.instantiable(node)){
				RefreshFAEStatusUtil.startWF(node, bean.getLocale().toString());
				FeedbackMessage feedbackmessage = new FeedbackMessage(FeedbackType.SUCCESS, bean.getLocale(), "", null,
	                    new String[] {WTMessage.getLocalizedMessage("com.catl.part.classification.resource.CATLNodeConfigRB", CATLNodeConfigRB.REFRESH_FAE_WF_START_SUCCESSFUL)});
				result.setStatus(FormProcessingStatus.SUCCESS);
				result.addFeedbackMessage(feedbackmessage);
			}
			else {
				throw new WTException(WTMessage.getLocalizedMessage("com.catl.part.classification.resource.CATLNodeConfigRB",CATLNodeConfigRB.NODE_INSTANTIABLE_NOT));
			}
		}
		return result;
	}

}
