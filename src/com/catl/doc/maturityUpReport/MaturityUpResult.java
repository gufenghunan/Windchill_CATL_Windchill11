package com.catl.doc.maturityUpReport;

import java.util.List;

import wt.part.WTPartMaster;

public class MaturityUpResult {
	
	private WTPartMaster master;
	private boolean checkPass;
	private List<String> errorMsgs;
	
	public WTPartMaster getMaster() {
		return master;
	}
	public void setMaster(WTPartMaster master) {
		this.master = master;
	}
	public boolean isCheckPass() {
		return checkPass;
	}
	public void setCheckPass(boolean checkPass) {
		this.checkPass = checkPass;
	}
	public List<String> getErrorMsgs() {
		return errorMsgs;
	}
	public void setErrorMsgs(List<String> errorMsgs) {
		this.errorMsgs = errorMsgs;
	}

}
