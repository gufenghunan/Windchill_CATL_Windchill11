package com.catl.require.processors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.catl.common.util.GenericUtil;
import com.catl.common.util.PartUtil;
import com.catl.doc.maturityUpReport.MaturityUpReportHelper;
import com.catl.doc.maturityUpReport.NFAEMaturityUp3DocPartLink;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.model.NmSimpleOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.fc.PersistenceHelper;
import wt.fc.collections.WTHashSet;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.util.WTException;

public class AfterSaleCommands implements Serializable{
	
	private static final long serialVersionUID = 8375606324715212325L;

	public static FormResult pasteParts(NmCommandBean bean) throws WTException{
		FormResult result = new FormResult();
		result.setStatus(FormProcessingStatus.SUCCESS);
		return result;
	}
	
	public static FormResult removeParts(NmCommandBean bean) throws WTException{
		FormResult result = new FormResult();
		result.setStatus(FormProcessingStatus.SUCCESS);
		return result;
	}
	
	public static FormResult addParts(NmCommandBean bean) throws WTException{
		FormResult result = new FormResult();
		result.setStatus(FormProcessingStatus.SUCCESS);
		return result;
	}
}
