package com.catl.change.inventory.delegate;

import java.util.HashMap;
import java.util.List;

import wt.change2.WTChangeActivity2;
import wt.util.WTException;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormProcessorHelper;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.IframeFormProcessorHelper;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.change2.forms.delegates.AffectedAndResultingItemsFormDelegate;

public class ChangeInventoryDelegate extends AffectedAndResultingItemsFormDelegate{
	
	
	public FormResult preProcess(NmCommandBean paramTNmCommandBean, List<ObjectBean> paramTList) throws WTException{
		System.out.println("====>ChangeInventoryDelegate preProcess");
		paramTNmCommandBean.getInitialItems();
		paramTNmCommandBean.getAddedItems();
		paramTNmCommandBean.getAddedItemsByName("CatlChangeInventory");
		System.out.println("====>preProcess:::"+FormProcessorHelper.getCurrentObjectBean(paramTNmCommandBean).getObject());
		
		IframeFormProcessorHelper.getIframeKeyList(paramTNmCommandBean);
		HashMap localHashMap = paramTNmCommandBean.getRequestData().getParameterMap();
		Object[] arrayOfObject = localHashMap.keySet().toArray();
		
		System.out.println("====>hope 111 ");
		Object obj = paramTNmCommandBean.getRequestData().getParameterMap().get("1_popCreateWizard_defaultChangeTask");
		System.out.println("====>hope 222 "+obj);
		
		
		
		return new FormResult(FormProcessingStatus.SUCCESS);
	}

	@Override
	public FormResult postProcess(NmCommandBean paramNmCommandBean,
			List<ObjectBean> objectBeanList) throws WTException {
		// 
		System.out.println("====>ChangeInventoryDelegate postProcess in ======");
		System.out.println("====>postProcess:::"+objectBeanList.get(0).getObject());
		
		for (Object object : objectBeanList) {
			if (object instanceof ObjectBean) {
				ObjectBean bean = (ObjectBean) object;
				WTChangeActivity2 eca = (WTChangeActivity2) bean.getObject();
				System.out.println("getInitialItemsByName: "+bean.getInitialItemsByName("CatlChangeInventory"));
				System.out.println("getAddedItemsByName: "+bean.getAddedItemsByName("CatlChangeInventory"));
				System.out.println("getRemovedItemsByName: "+bean.getRemovedItemsByName("CatlChangeInventory"));
			}
        }
        
        System.out.println("====>ChangeInventoryDelegate postProcess out=====");
//		WTDocument doc = (WTDocument)(paramList.get(0).getObject());
//		Object oids = paramNmCommandBean.getParameterMap().get("oid");
//		String issueOid = ((String[])oids)[0];
		//String issueOid = (paramNmCommandBean.getPageOid().toString().split("~"))[0];
//		WTChangeIssue issue = (WTChangeIssue)GenericObjectService.getObjectByOid(issueOid);
//		ContainerRemoteUtil.addChangeableToProblemReport(issue, doc);
		return super.postProcess(paramNmCommandBean, objectBeanList);
	}

}
