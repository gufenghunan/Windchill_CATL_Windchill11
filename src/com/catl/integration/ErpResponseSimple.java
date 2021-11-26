package com.catl.integration;

public class ErpResponseSimple {
	/**
	 * 记录返回消息
	 */
	public String message;
	/**
	 * 是否成功
	 */
	public boolean success;
	/**
	 * 记录成功的物料编号
	 */
	public String successPart;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getSuccessPart() {
		return successPart;
	}
	public void setSuccessPart(String successPart) {
		this.successPart = successPart;
	}
	
}
