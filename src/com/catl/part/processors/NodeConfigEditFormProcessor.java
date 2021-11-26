package com.catl.part.processors;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.catl.part.classification.AttributeForFAE;
import com.catl.part.classification.ClassificationNodeConfig;
import com.catl.part.classification.NodeConfigHelper;
import com.catl.part.classification.resource.CATLNodeConfigRB;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultEditFormProcessor;
import com.ptc.core.components.forms.DynamicRefreshInfo;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import wt.fc.PersistenceHelper;
import wt.util.WTException;
import wt.util.WTMessage;
import wt.util.WTPropertyVetoException;

public class NodeConfigEditFormProcessor extends DefaultEditFormProcessor {

	@Override
	public FormResult doOperation(NmCommandBean nmcommandbean, List<ObjectBean> objectList) throws WTException {
		FormResult result = super.doOperation(nmcommandbean, objectList);
		Object obj = nmcommandbean.getActionOid().getRefObject();
		if(obj instanceof LWCStructEnumAttTemplate){
			LWCStructEnumAttTemplate node = (LWCStructEnumAttTemplate)obj;
			ClassificationNodeConfig nodeConfig = NodeConfigHelper.getNodeConfig(node);
			ObjectBean bean = objectList.get(0);
			Map<String, List<String>> map = bean.getComboBox();
			try {
				for(String key : map.keySet()){
					List<String> mapValue = map.get(key);
					if(!mapValue.isEmpty()){
						String value = mapValue.get(0);
						if(key.startsWith(ClassificationNodeConfig.NEED_FAE)){
							nodeConfig.setNeedFae(Boolean.valueOf(value));
						}
						else if(key.startsWith(ClassificationNodeConfig.NEED_NON_FAE_REPORT)){
							nodeConfig.setNeedNonFaeReport(Boolean.valueOf(value));
						}
						else if(key.startsWith(ClassificationNodeConfig.ATTRIBUTE_REF)){
							if(StringUtils.equals(value, AttributeForFAE.CUSTOMIZED.getStringValue())){
								nodeConfig.setAttributeRef(AttributeForFAE.CUSTOMIZED);
							}
							else if(StringUtils.equals(value, AttributeForFAE.SOURCE.getStringValue())){
								nodeConfig.setAttributeRef(AttributeForFAE.SOURCE);
							}
							else if(StringUtils.equals(value, AttributeForFAE.NONE.getStringValue())){
								nodeConfig.setAttributeRef(AttributeForFAE.NONE);
							}
						}
						else if(key.startsWith(ClassificationNodeConfig.MAKE_NEED_FAE)){
							nodeConfig.setMakeNeedFae(Boolean.valueOf(value));
						}
						else if(key.startsWith(ClassificationNodeConfig.BUY_NEED_FAE)){
							nodeConfig.setBuyNeedFae(Boolean.valueOf(value));
						}
						else if(key.startsWith(ClassificationNodeConfig.MAKE_BUY_NEED_FAE)){
							nodeConfig.setMakeBuyNeedFae(Boolean.valueOf(value));
						}
						else if(key.startsWith(ClassificationNodeConfig.CUSTOMER_NEED_FAE)){
							nodeConfig.setCustomerNeedFae(Boolean.valueOf(value));
						}
						else if(key.startsWith(ClassificationNodeConfig.VIRTUAL_NEED_FAE)){
							nodeConfig.setVirtualNeedFae(Boolean.valueOf(value));
						}
					}
				}
				PersistenceHelper.manager.save(nodeConfig);
			} catch (WTPropertyVetoException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
			
			FeedbackMessage feedbackmessage = new FeedbackMessage(FeedbackType.SUCCESS, bean.getLocale(), "", null,
                    new String[] {WTMessage.getLocalizedMessage("com.catl.part.classification.resource.CATLNodeConfigRB", CATLNodeConfigRB.ATTR_UPDATE_SUCCESSFUL)});
			result.setStatus(FormProcessingStatus.SUCCESS);
			result.addFeedbackMessage(feedbackmessage);
			result.addDynamicRefreshInfo(new DynamicRefreshInfo(node, node, DynamicRefreshInfo.Action.UPDATE));
		}
		return result;
	}

}
