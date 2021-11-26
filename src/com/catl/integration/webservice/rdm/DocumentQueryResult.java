package com.catl.integration.webservice.rdm;

import java.io.Serializable;
import java.util.List;

public class DocumentQueryResult implements Serializable {

	public static final String RESULT_SUCCESS = "0";
	
	public static final String RESULT_INPUT_PARAMS_ERROR = "1";

	private static final long serialVersionUID = 7700920923142373568L;

	private String result = RESULT_SUCCESS;

	private String message;

	private List<DocumentInfo> documents;

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

	public List<DocumentInfo> getDocuments() {
		return documents;
	}

	public void setDocuments(List<DocumentInfo> documents) {
		this.documents = documents;
	}

}
