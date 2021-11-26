package com.catl.promotion.processor;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.part.Source;
import wt.part.WTPart;
import wt.part.WTPartUsageLink;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTMessage;
import wt.vc.wip.WorkInProgressHelper;

import com.catl.bom.workflow.BomWfUtil;
import com.catl.common.util.CatlConstant;
import com.catl.common.util.GenericUtil;
import com.catl.part.CreateCatlPartProcessor;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class MaintenancePartSourceProcessor extends DefaultObjectFormProcessor {

	@Override
	public FormResult doOperation(NmCommandBean nmcommandbean,
			List<ObjectBean> list) throws WTException {
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		FormResult formResult = super.doOperation(nmcommandbean, list);
		try{
			ObjectBean bean = list.get(0);
        	Map<String, List<String>> map = bean.getComboBox();
        	Set<Entry<String, List<String>>> set = map.entrySet();
        	for (Entry<String, List<String>> entry : set) {
        		String oid = entry.getKey();
				List<String> values = entry.getValue();
				String value = "";
				if(values != null && !values.isEmpty()){
					value = values.get(0);
				}
				if(StringUtils.isNotBlank(value)){
					WTPart part = (WTPart)GenericUtil.getInstance(oid);
					if(WorkInProgressHelper.isCheckedOut(part)){
						throw new WTException(WTMessage.formatLocalizedMessage("[{0}]处于检出状态，无法维护源信息!", new Object[]{part.getNumber()}));
					}
					String msg = BomWfUtil.checkSource(part, value);
					if(msg != null && !msg.equals("")){
						throw new WTException(msg);
					}
					Source source = Source.toSource(value);
					if(!part.getSource().equals(source)){
						part.setSource(source);
						PersistenceServerHelper.manager.update(part);
						CreateCatlPartProcessor.updateFAEStatus(part);
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
