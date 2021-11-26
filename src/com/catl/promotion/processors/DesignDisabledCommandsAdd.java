package com.catl.promotion.processors;

import java.util.Set;

import wt.fc.Persistable;
import wt.fc.WTObject;
import wt.util.WTException;
import wt.workflow.work.WorkItem;

import com.catl.promotion.dbs.DesignDisabledXmlObjectUtil;
import com.catl.promotion.util.PromotionUtil;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.xworks.xmlobject.web.form.WindchillActionCommand;

public class DesignDisabledCommandsAdd implements WindchillActionCommand {

	private static final long serialVersionUID = -7924490591029679922L;

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
			DesignDisabledXmlObjectUtil.addDesignDisabledObjects(pbo, soids);
		} catch (Exception e) {
			e.printStackTrace();
			throw new WTException(e);
		}

		return result;
	}

}
