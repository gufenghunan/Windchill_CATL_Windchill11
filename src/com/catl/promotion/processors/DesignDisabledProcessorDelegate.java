package com.catl.promotion.processors;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import wt.util.WTException;
import wt.workflow.work.WorkItem;

import com.catl.promotion.bean.DesignDisabledXmlObjectBean;
import com.catl.promotion.util.PromotionConst;
import com.catl.promotion.workflow.PromotionWorkflowHelper;
import com.ptc.xworks.util.XWorksHelper;
import com.ptc.xworks.xmlobject.XmlObject;
import com.ptc.xworks.xmlobject.store.XmlObjectStoreManager;
import com.ptc.xworks.xmlobject.store.StoreOptions.SaveOption;
import com.ptc.xworks.xmlobject.util.AttributeUtils;
import com.ptc.xworks.xmlobject.workflow.WorkflowTaskFormProcessorDelegate;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class DesignDisabledProcessorDelegate extends WorkflowTaskFormProcessorDelegate {

	private static Logger logger = Logger.getLogger(DesignDisabledProcessorDelegate.class);

	@Override
	public FormResult validateFormWhenSave(NmCommandBean clientData, List<ObjectBean> objectBeans, List<XmlObject> xmlObjectList) throws WTException {
		return super.validateFormWhenSave(clientData, objectBeans, xmlObjectList);
	}

	@Override
	public FormResult validateBeforeCompleteForRequiredCondition(NmCommandBean clientData, List<ObjectBean> objectBeans, List<XmlObject> xmlObjectList)
			throws WTException {
		FormResult result = new FormResult(FormProcessingStatus.SUCCESS);
		return result;
	}

	@Override
	public void copyAttributes(NmCommandBean clientData, List<ObjectBean> objectBeans, XmlObject xmlObjectFromWeb, XmlObject xmlObjectInDatabase)
			throws Exception {

		WorkItem currentWorkItem = PromotionWorkflowHelper.getWorkItem(clientData);
		String[] attributeNames = null;
		String description = currentWorkItem.getDescription();

		if (description.indexOf(PromotionConst.design_disable_submit) > 0 || description.indexOf(PromotionConst.design_disable_confirm) > 0) {
			attributeNames = new String[] { "requestor", "reason", "changeNo", "newPN", "comments" };
		}

		if (xmlObjectInDatabase instanceof DesignDisabledXmlObjectBean) {
			if (attributeNames != null) {
				AttributeUtils.copyAttributes(xmlObjectFromWeb, xmlObjectInDatabase, attributeNames);
			}
		}
	}

	@Override
	public XmlObject storeXmlObject(NmCommandBean clientData, List<ObjectBean> objectBeans, XmlObject xmlObj) throws Exception {
		XmlObjectStoreManager storeManager = XWorksHelper.getXmlObjectStoreManager();
		xmlObj = storeManager.save(xmlObj, SaveOption.APPEND_AND_UPDATE);
		return xmlObj;
	}

	@Override
	public FormResult handleFormResult(NmCommandBean clientData, List<ObjectBean> objectBeans, FormResult postResult, List<XmlObject> xmlObjectsFromWeb,
			List<XmlObject> savedXmlObjects) throws WTException {
		if (postResult.getStatus() == FormProcessingStatus.SUCCESS) {
			String nextAction = clientData.getTextParameter("xmlobject_FormResultNextAction");
			if (StringUtils.isNotBlank(nextAction)) {
				postResult.setNextAction(FormResultAction.JAVASCRIPT);
				postResult.setJavascript(nextAction);
				if (postResult.getDynamicRefreshInfo() != null) {
					postResult.getDynamicRefreshInfo().clear();// clear DynamicRefreshInfo, so javascript action can be executed.
				}
			}
		}
		return postResult;
	}
}
