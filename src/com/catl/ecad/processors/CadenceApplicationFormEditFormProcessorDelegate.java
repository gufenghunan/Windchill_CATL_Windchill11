package com.catl.ecad.processors;

import java.util.List;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.xworks.xmlobject.XmlObject;
import com.ptc.xworks.xmlobject.store.StoreOptions.SaveOption;
import com.ptc.xworks.xmlobject.workflow.WorkflowTaskFormProcessorDelegate;

import wt.util.WTException;

public class CadenceApplicationFormEditFormProcessorDelegate extends WorkflowTaskFormProcessorDelegate {

	@Override
	public FormResult validateFormWhenSave(NmCommandBean clientData, List<ObjectBean> objectBeans,
			List<XmlObject> xmlObjectList) throws WTException {
		return super.validateFormWhenSave(clientData, objectBeans, xmlObjectList);
	}
	
	@Override
	public XmlObject storeXmlObject(NmCommandBean clientData, List<ObjectBean> objectBeans, XmlObject xmlObj)
			throws Exception {
		System.out.println(xmlObj.getIdentifier());
		return this.getXmlObjectStoreManager().save(xmlObj, SaveOption.REPLACE_AND_UPDATE);
	}

}
