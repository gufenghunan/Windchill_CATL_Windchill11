package com.catl.change.report.dcn;

import wt.type.Typed;

public class DCNAffectedReportodel {
	private Typed obj ;
	
	private String changedataNumber = "";
	
	private String changedataName = "";
	
	private String changedataversioninfo = "";

	private String changedatatype = "";
	
	public Typed getObj() {
		return obj;
	}

	public void setObj(Typed obj) {
		this.obj = obj;
	}

	public String getChangedatatype() {
		return changedatatype;
	}

	public void setChangedatatype(String changedatatype) {
		this.changedatatype = changedatatype;
	}

	public String getChangedataNumber() {
		return changedataNumber;
	}

	public void setChangedataNumber(String changedataNumber) {
		this.changedataNumber = changedataNumber;
	}

	public String getChangedataName() {
		return changedataName;
	}

	public void setChangedataName(String changedataName) {
		this.changedataName = changedataName;
	}

	public String getChangedataversioninfo() {
		return changedataversioninfo;
	}

	public void setChangedataversioninfo(String changedataversioninfo) {
		this.changedataversioninfo = changedataversioninfo;
	}

	
}
