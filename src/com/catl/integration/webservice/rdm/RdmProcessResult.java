package com.catl.integration.webservice.rdm;

import java.io.Serializable;

public class RdmProcessResult implements Serializable {

	private static final long serialVersionUID = -8493093872954098901L;

	private String result;

	private String message;

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
