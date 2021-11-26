package com.catl.integration.webservice.rdm;

import java.io.Serializable;

public class DeliverableDocumentLinkInfo implements Serializable {

	private static final long serialVersionUID = -4282002942119539196L;

	private String deliverableId;

	private String docNumber;

	private String branchId;

	public String getDeliverableId() {
		return deliverableId;
	}

	public void setDeliverableId(String deliverableId) {
		this.deliverableId = deliverableId;
	}

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

}
