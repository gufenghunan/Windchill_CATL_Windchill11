package com.catl.doc.workflow;

public class ClsReult {

	 public Boolean getIscollater() {
		return iscollater;
	}

	public void setIscollater(Boolean iscollater) {
		this.iscollater = iscollater;
	}

	public Boolean getIscountersign() {
		return iscountersign;
	}

	public void setIscountersign(Boolean iscountersign) {
		this.iscountersign = iscountersign;
	}

	public Boolean iscollater;
	
	public Boolean isemail;
	 
	 public Boolean getIsemail() {
		return isemail;
	}

	public void setIsemail(Boolean isemail) {
		this.isemail = isemail;
	}

	public Boolean iscountersign;
	 
	 public Boolean isDocumentclsssify;
	 
	 public String collaterrole="";
	 
	 public String countersignrole="";
	 
	 public String emailrole="";

	public String getEmailrole() {
		return emailrole;
	}

	public void setEmailrole(String emailrole) {
		this.emailrole = emailrole;
	}

	public String getCollaterrole() {
		return collaterrole;
	}

	public void setCollaterrole(String collaterrole) {
		this.collaterrole = collaterrole;
	}

	public String getCountersignrole() {
		return countersignrole;
	}

	public void setCountersignrole(String countersignrole) {
		this.countersignrole = countersignrole;
	}

	public Boolean getIsDocumentclsssify() {
		return isDocumentclsssify;
	}

	public void setIsDocumentclsssify(Boolean isDocumentclsssify) {
		this.isDocumentclsssify = isDocumentclsssify;
	}
}
