package com.catl.integration.webservice;

import java.io.Serializable;

public class ItemResult implements Serializable {

	private static final long serialVersionUID = -1681177445219657987L;

	private String number;

	private String result;

	private String message;

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

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
