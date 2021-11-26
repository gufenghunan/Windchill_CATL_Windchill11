package com.catl.part.processors;

import com.catl.common.util.IBAUtil;
import com.catl.part.PartConstant;
import com.catl.part.maturity.PartMaturityChangeLogHelper;
import com.ptc.core.components.forms.DynamicRefreshInfo;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.session.SessionHelper;
import wt.util.WTException;

public class CATLPartMaturityUpProcessor {

	public static FormResult maturityUp3(NmCommandBean clientData) throws WTException {
		FormResult result = new FormResult();
		Object obj = clientData.getActionOid().getRefObject();
		if(obj instanceof WTPart){
			WTPart part = (WTPart)obj;
			FeedbackMessage feedbackmessage;
			try {
				maturityUp(part, "3");
				PartMaturityChangeLogHelper.addPartMaturityChangeLog(SessionHelper.manager.getPrincipalReference(), part, "1", "3");
			} catch (WTException e) {
				e.printStackTrace();
				feedbackmessage = new FeedbackMessage(FeedbackType.FAILURE, clientData.getLocale(), "", null,
	                    new String[] { "成熟度升级失败:"+e.getLocalizedMessage()});
				result.setStatus(FormProcessingStatus.FAILURE);
				result.addFeedbackMessage(feedbackmessage);
				return result;
			}
			
			feedbackmessage = new FeedbackMessage(FeedbackType.SUCCESS, clientData.getLocale(), "", null,
                    new String[] { "部件的成熟度已升级为3！"});
			result.setStatus(FormProcessingStatus.SUCCESS);
			result.addFeedbackMessage(feedbackmessage);
			result.addDynamicRefreshInfo(new DynamicRefreshInfo((Persistable)obj, (Persistable)obj, DynamicRefreshInfo.Action.UPDATE));
		}
		return result;
	}
	
	public static FormResult maturityUp6(NmCommandBean clientData) throws WTException {
		FormResult result = new FormResult();
		Object obj = clientData.getActionOid().getRefObject();
		if(obj instanceof WTPart){
			FeedbackMessage feedbackmessage;
			WTPart part = (WTPart)obj;
			try {
				maturityUp(part, "6");
				PartMaturityChangeLogHelper.addPartMaturityChangeLog(SessionHelper.manager.getPrincipalReference(), part, "3", "6");
			} catch (WTException e) {
				e.printStackTrace();
				e.printStackTrace();
				feedbackmessage = new FeedbackMessage(FeedbackType.FAILURE, clientData.getLocale(), "", null,
	                    new String[] { "成熟度升级失败:"+e.getLocalizedMessage()});
				result.setStatus(FormProcessingStatus.FAILURE);
				result.addFeedbackMessage(feedbackmessage);
				return result;
			}
			feedbackmessage = new FeedbackMessage(FeedbackType.SUCCESS, clientData.getLocale(), "", null,
                    new String[] { "部件的成熟度已升级为6！"});
			result.setStatus(FormProcessingStatus.SUCCESS);
			result.addFeedbackMessage(feedbackmessage);
			result.addDynamicRefreshInfo(new DynamicRefreshInfo((Persistable)obj, (Persistable)obj, DynamicRefreshInfo.Action.UPDATE));
		}
		return result;
	}
	
	private static void maturityUp(WTPart part, String maturity) throws WTException{
		WTPartMaster master = (WTPartMaster)part.getMaster();
		PersistenceHelper.manager.save(IBAUtil.setIBAVaue(master, PartConstant.IBA_CATL_Maturity, maturity));
	}
}
