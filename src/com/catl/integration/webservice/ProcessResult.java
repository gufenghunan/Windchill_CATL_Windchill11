package com.catl.integration.webservice;

import java.io.Serializable;

public class ProcessResult implements Serializable {

	private static final long serialVersionUID = 688785381698547483L;

	private String result;

	private String message;

	private ItemResult[] itemResults;

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

	public ItemResult[] getItemResults() {
		return itemResults;
	}

	public void setItemResults(ItemResult[] itemResults) {
		this.itemResults = itemResults;
	}

}
