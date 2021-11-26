package com.catl.doc.relation;

import java.util.ArrayList;
import java.util.List;

import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.part.WTPart;
import wt.util.WTException;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmContext;
import com.ptc.windchill.enterprise.part.forms.DocPartDescAddRelationFormProcessor;

public class CatlDocPartDescAddRelationFormProcessor extends DocPartDescAddRelationFormProcessor{
	@Override
	public FormResult doOperation(NmCommandBean paramNmCommandBean, List<ObjectBean> paramList) throws WTException {
		
		FormResult result = new FormResult();
		result.setStatus(FormProcessingStatus.SUCCESS);
		
		Object objOrig = paramNmCommandBean.getPrimaryOid().getWtRef().getObject();
		ArrayList<NmContext> selects = paramNmCommandBean.getSelected();
		ArrayList<WTPart> list = new ArrayList<WTPart>();
		for(NmContext nmContext : selects){
			Persistable persistable = nmContext.getTargetOid().getWtRef().getObject();
			list.add((WTPart)persistable);
		}

		if (objOrig instanceof WTDocument) {
			WTDocument doc = (WTDocument) objOrig;
			DocPartRelationUtil.checkPermission(doc, list);
			DocPartRelationUtil.addDesc(doc, list);
		}

		return result;
	}
}
