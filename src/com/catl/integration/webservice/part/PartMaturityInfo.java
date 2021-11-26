package com.catl.integration.webservice.part;

import java.io.Serializable;

public class PartMaturityInfo implements Serializable {

	private static final long serialVersionUID = 7396444916016161117L;

	private String partNumber;

	private String maturity;

	public String getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	public String getMaturity() {
		return maturity;
	}

	public void setMaturity(String maturity) {
		this.maturity = maturity;
	}

}
