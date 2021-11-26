package com.catl.change.report.model;

public class ChangepartsModel {
	
	private  String partnumber="";
	
	private  String partname="";
	
	private  String affectedparentPN="";
	
	private  String affectedparentName="";

	public String getPartnumber() {
		return partnumber;
	}

	public void setPartnumber(String partnumber) {
		this.partnumber = partnumber;
	}

	public String getPartname() {
		return partname;
	}

	public void setPartname(String partname) {
		this.partname = partname;
	}

	public String getAffectedparentPN() {
		return affectedparentPN;
	}

	public void setAffectedparentPN(String affectedparentPN) {
		this.affectedparentPN = affectedparentPN;
	}

	public String getAffectedparentName() {
		return affectedparentName;
	}

	public void setAffectedparentName(String affectedparentName) {
		this.affectedparentName = affectedparentName;
	}

	
}
