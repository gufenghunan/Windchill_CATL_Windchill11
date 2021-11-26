package com.catl.integration.webservice.rdm;

import java.io.Serializable;

public class DocumentInfo implements Serializable {

	private static final long serialVersionUID = 2478987766195699354L;

	private String docNumber;

	private String branchId;

	private String docName;

	private String docVer;

	private String state;

	private String creatorName;

	public String getDocNumber() {
		return docNumber;
	}

	public void setDocNumber(String docNumber) {
		this.docNumber = docNumber;
	}

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public String getDocVer() {
		return docVer;
	}

	public void setDocVer(String docVer) {
		this.docVer = docVer;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}
	@Override
	public String toString(){
		return "docName="+this.getDocName()+",docNumber="+this.getDocNumber()+",branchId="+this.getBranchId()+",version="+this.getDocVer()+",createName="+this.getCreatorName()+",state="+this.getState();
	}
	@Override
	public boolean equals(Object obj) {
		if(obj.getClass() != getClass())
			return false;
		DocumentInfo info = (DocumentInfo)obj;
		if(this.docNumber.equals(info.getDocNumber()) && this.docName.equals(info.getDocName()) && this.branchId.equals(info.getBranchId()) && this.docVer.equals(info.getDocVer()) && this.getCreatorName().equals(info.getCreatorName()) && this.getState().equals(info.getState()))
			return true;
		
		return super.equals(obj);
	}
}
