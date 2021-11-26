package com.catl.integration.webservice.part;

import java.io.Serializable;

public class PartFaeInfo implements Serializable {

	private static final long serialVersionUID = 3023472866402481873L;

	private String partNumber;

	private String faeStatus;

	public String getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	public String getFaeStatus() {
		return faeStatus;
	}

	public void setFaeStatus(String faeStatus) {
		this.faeStatus = faeStatus;
	}

}
