package com.catl.doc.maturityUpReport;

import com.catl.common.util.DocUtil;
import com.catl.common.util.PartUtil;
import com.ptc.netmarkets.model.NmObject;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.model.NmSimpleOid;

import wt.doc.WTDocument;
import wt.part.WTPart;
import wt.util.WTException;

public class NFAEMaturityReportLinkBean extends NmObject{
	
	private NmOid nmoid;
	
	private WTDocument doc;
	
	private WTPart part;
	
	private WTPart initialPart;
	
	public NFAEMaturityReportLinkBean(NFAEMaturityUp3DocPartLink link) throws WTException{
		nmoid = new NmSimpleOid(link.toString());
		doc = DocUtil.getLatestWTDocument(link.getDocMaster().getNumber());
		part = PartUtil.getLastestWTPartByNumber(link.getPartMaster().getNumber());	
		initialPart = (WTPart)link.getInitialPart().getObject();
	}

	public WTDocument getDoc() {
		return doc;
	}

	public void setDoc(WTDocument doc) {
		this.doc = doc;
	}

	public WTPart getPart() {
		return part;
	}

	public void setPart(WTPart part) {
		this.part = part;
	}

	public WTPart getInitialPart() {
		return initialPart;
	}

	public void setInitialPart(WTPart initialPart) {
		this.initialPart = initialPart;
	}

	@Override
	public NmOid getOid() {
		return nmoid;
	}

}
