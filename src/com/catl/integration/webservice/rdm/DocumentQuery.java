package com.catl.integration.webservice.rdm;

import java.io.Serializable;

public class DocumentQuery implements Serializable {

	private static final long serialVersionUID = 4840261092531994168L;

	private String projectCode;

	private String projectName;

	private String docNumber;

	private String docName;

	public String getProjectCode() {
		return projectCode;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getDocNumber() {
		return docNumber;
	}

	public void setDocNumber(String docNumber) {
		this.docNumber = docNumber;
	}

	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

}
