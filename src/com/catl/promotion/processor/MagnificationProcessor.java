package com.catl.promotion.processor;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import wt.part.WTPartUsageLink;
import wt.session.SessionServerHelper;
import wt.util.WTException;

import com.catl.line.util.IBAUtility;
import com.catl.line.util.WCUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class MagnificationProcessor extends DefaultObjectFormProcessor {

	@Override
	public FormResult doOperation(NmCommandBean nmcommandbean,
			List<ObjectBean> list) throws WTException {
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		FormResult formResult = new FormResult(FormProcessingStatus.SUCCESS);
		try{
			for (Object object : list) {
				if (object instanceof ObjectBean) {
					ObjectBean bean = (ObjectBean) object;
					Map<String, String> map=bean.getText();
					Set<Entry<String, String>> set = map.entrySet();
		        	for (Entry<String, String> entry : set) {
		        		String oid = entry.getKey().split("_col_")[0];
		        		String ibaname = entry.getKey().split("_col_")[1];
						String value = entry.getValue();
						WTPartUsageLink link=(WTPartUsageLink) WCUtil.getWTObject(oid);
						IBAUtility iba=new IBAUtility(link);
						iba.setIBAValue(ibaname, value);
						iba.updateAttributeContainer(link);
						iba.updateIBAHolder(link);
		        	}
				}
			}
			
		} catch (Exception e) {
            e.printStackTrace();
            String message = e.getLocalizedMessage();
            FeedbackMessage feedbackmessage = new FeedbackMessage(FeedbackType.FAILURE, nmcommandbean.getLocale(), "", null, new String[] { message });
            formResult.addFeedbackMessage(feedbackmessage);
            formResult.setStatus(FormProcessingStatus.FAILURE);
        } finally {
        	SessionServerHelper.manager.setAccessEnforced(flag);
        }
		return formResult;
	}

}
