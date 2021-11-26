package com.catl.promotion.processors;

import java.util.Set;

import wt.fc.Persistable;
import wt.fc.WTObject;
import wt.util.WTException;
import wt.workflow.work.WorkItem;

import com.catl.promotion.dbs.PlatformChangeXmlObjectUtil;
import com.catl.promotion.dbs.SourceChangeXmlObjectUtil;
import com.catl.promotion.util.PromotionUtil;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.xworks.xmlobject.web.form.WindchillActionCommand;

public class SourceChangeCommandsRemove implements WindchillActionCommand {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6943067628006808496L;

	@SuppressWarnings("unchecked")
	@Override
	public FormResult executeCommand(NmCommandBean nmcommandbean) throws WTException {

		FormResult result = new FormResult(FormProcessingStatus.SUCCESS);

		try {
			Set<String> soids = PromotionUtil.getSelectedOid(nmcommandbean.getSelected());
			Persistable p = nmcommandbean.getPrimaryOid().getWtRef().getObject();
			WorkItem workitem = (WorkItem) p;
			Persistable pn = workitem.getPrimaryBusinessObject().getObject();
			WTObject pbo = (WTObject) pn;
			SourceChangeXmlObjectUtil.removeSourceChangeObject(pbo, soids);
			PlatformChangeXmlObjectUtil.removePlatformChangeObject(pbo, soids);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
