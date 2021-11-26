package com.catl.doc.maturityUpReport.commands;

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

public class MaturityUpReportCommands implements Serializable{
	
	private static final long serialVersionUID = 8375606324715256325L;

	public static FormResult pasteParts(NmCommandBean bean) throws WTException{
		FormResult result = new FormResult();
		Object obj = bean.getActionOid().getRefObject();
		if(obj instanceof WTDocument){
			WTDocument doc = (WTDocument)obj;
			if(MaturityUpReportHelper.isNFAEMaturityUp3Report(doc)){
				WTDocumentMaster docMaster = (WTDocumentMaster)doc.getMaster();
				Set<WTPart> parts = new HashSet<WTPart>();
				ArrayList<?> list = bean.getClipped();
				for (Object object : list) {
					NmOid nmoid = (NmOid)object;
					object = nmoid.getRefObject();
					if(object instanceof WTPart){
						WTPart part = PartUtil.getLastestWTPart((WTPart)object);
						if(MaturityUpReportHelper.allowAddPart(docMaster, part)){
							parts.add(part);
						}
					}
				}
				MaturityUpReportHelper.createNFAEMaturityUp3DocPartLink(docMaster, parts);
				result.setStatus(FormProcessingStatus.SUCCESS);
			}
		}
		return result;
	}
	
	public static FormResult removeParts(NmCommandBean bean) throws WTException{
		FormResult result = new FormResult();
		Object obj = bean.getActionOid().getRefObject();
		if(obj instanceof WTDocument){
			WTDocument doc = (WTDocument)obj;
			if(MaturityUpReportHelper.isNFAEMaturityUp3Report(doc)){
				StringBuilder errorMsg = new StringBuilder();
				WTHashSet set = new WTHashSet();
				WTDocumentMaster docMaster = (WTDocumentMaster)doc.getMaster();
				ArrayList<?> list = bean.getNmOidSelected();
				for (Object object : list) {
					NmSimpleOid nmOid = (NmSimpleOid)object;
					NFAEMaturityUp3DocPartLink link = (NFAEMaturityUp3DocPartLink)GenericUtil.getInstance(nmOid.getInternalName());
					WTPart part = PartUtil.getLastestWTPartByNumber(link.getPartMaster().getNumber());
					MaturityUpReportHelper.checkPartWhenRemove(doc, part, errorMsg);
					if(errorMsg.length() == 0){
						set.add(MaturityUpReportHelper.getNFAEMaturityUp3DocPartLink(docMaster, (WTPartMaster)part.getMaster()));
					}
				}
				if(errorMsg.length() > 0){
					throw new WTException(errorMsg.toString());
				}
				else {
					PersistenceHelper.manager.delete(set);
					result.setStatus(FormProcessingStatus.SUCCESS);
				}
			}
		}
		
		return result;
	}
	
	public static FormResult addParts(NmCommandBean bean) throws WTException{
		FormResult result = new FormResult();
		Object obj = bean.getActionOid().getRefObject();
		if(obj instanceof WTDocument){
			WTDocument doc = (WTDocument)obj;
			if(MaturityUpReportHelper.isNFAEMaturityUp3Report(doc)){
				StringBuilder errorMsg = new StringBuilder();
				Set<WTPart> parts = new HashSet<WTPart>();
				WTDocumentMaster docMaster = (WTDocumentMaster)doc.getMaster();
				ArrayList<?> list = bean.getNmOidSelected();
				for (Object object : list) {
					NmOid oid = (NmOid)object;
					WTPart part = (WTPart)oid.getRefObject();
					MaturityUpReportHelper.checkPartWhenAdd(docMaster, part, errorMsg);
					if(errorMsg.length() == 0){
						parts.add(part);
					}
				}
				
				if(errorMsg.length() > 0){
					throw new WTException(errorMsg.toString());
				}
				else {
					MaturityUpReportHelper.createNFAEMaturityUp3DocPartLink(docMaster, parts);
					result.setStatus(FormProcessingStatus.SUCCESS);
				}
			}
		}
		
		return result;
	}
}
