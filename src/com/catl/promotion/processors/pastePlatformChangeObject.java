package com.catl.promotion.processors;

import java.util.ArrayList;
import java.util.Locale;

import wt.fc.Persistable;
import wt.fc.WTObject;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTMessage;
import wt.workflow.work.WorkItem;

import com.catl.promotion.dbs.PlatformChangeXmlObjectUtil;
import com.catl.promotion.resource.promotionResource;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.util.beans.NmClipboardBean;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.xworks.xmlobject.web.form.WindchillActionCommand;

public class pastePlatformChangeObject implements WindchillActionCommand {

	private static final long serialVersionUID = 3198111815946749997L;
	private static String RESOURCE = "com.catl.promotion.resource.promotionResource";

	@SuppressWarnings("rawtypes")
	@Override
	public FormResult executeCommand(NmCommandBean nmcommandbean) throws WTException {

		FormResult result = new FormResult(FormProcessingStatus.SUCCESS);

		NmClipboardBean clipboardBean = nmcommandbean.getClipboardBean();
		ArrayList objectList = clipboardBean.getClipped();
		Locale locale = SessionHelper.getLocale();
		if (objectList == null || objectList.isEmpty()) {
			String msg = WTMessage.getLocalizedMessage(RESOURCE, promotionResource.NO_CHOICE, null, locale);
			throw new WTException(msg);
		} else {
			try {
				Persistable p = nmcommandbean.getPrimaryOid().getWtRef().getObject();
				WorkItem workitem = (WorkItem) p;
				Persistable pn = workitem.getPrimaryBusinessObject().getObject();
				WTObject pbo = (WTObject) pn;
				PlatformChangeXmlObjectUtil.pastePlatformChangeObject(pbo, objectList);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

}
