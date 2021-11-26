package com.catl.change.report.privatepart;

import java.util.ArrayList;
import java.util.List;

import com.catl.common.util.PartUtil;

import wt.clients.vc.CheckInOutTaskLogic;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.util.WTException;
import wt.vc.wip.WorkInProgressHelper;

public class PartNode {

	String partNumber;

	boolean privatePart = true;

	boolean checkedPrivatePart = false;

	WTPart part;

	WTPartMaster partMaster;
	
	List<PartNode> childs = new ArrayList<PartNode>();
	
	public PartNode(String partNumber) throws WTException{
		super();
		this.part = PartUtil.getLasterPart(partNumber);
		this.partNumber = partNumber;
		this.partMaster = (WTPartMaster)part.getMaster();
	}
	
	public String getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	public boolean isPrivatePart() {
		return privatePart;
	}

	public void setPrivatePart(boolean privatePart) {
		this.privatePart = privatePart;
	}

	public boolean isCheckedPrivatePart() {
		return checkedPrivatePart;
	}

	public void setCheckedPrivatePart(boolean checkedPrivatePart) {
		this.checkedPrivatePart = checkedPrivatePart;
	}

	public WTPart getPart() {
		return part;
	}

	public void setPart(WTPart part) {
		this.part = part;
	}

	public WTPartMaster getPartMaster() {
		return partMaster;
	}

	public void setPartMaster(WTPartMaster partMaster) {
		this.partMaster = partMaster;
	}

	public List<PartNode> getChilds() {
		return childs;
	}

	public void setChilds(List<PartNode> childs) {
		this.childs = childs;
	}

	
}
