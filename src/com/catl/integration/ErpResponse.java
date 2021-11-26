package com.catl.integration;

import java.util.List;

public class ErpResponse {
	List<Message> message;
	boolean success;
	
	public List<Message> getMessage() {
		return message;
	}
	public void setMessage(List<Message> message) {
		this.message = message;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
}
