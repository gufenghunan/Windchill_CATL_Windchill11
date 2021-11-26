package com.catl.integration;

public class PIResultMessage {

	public boolean isSuccess = false;
	
	public String exceptionMsg = "";
	
	public String resultInfo = "";
	
	public String sendFailList = "";

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public String getResultInfo() {
		return resultInfo;
	}

	public void setResultInfo(String resultInfo) {
		this.resultInfo = resultInfo;
	}

	public String getSendFailList() {
		return sendFailList;
	}

	public void setSendFailList(String sendFailList) {
		this.sendFailList = sendFailList;
	}

	public String getExceptionMsg() {
		return exceptionMsg;
	}

	public void setExceptionMsg(String exceptionMsg) {
		this.exceptionMsg = exceptionMsg;
	}
	
}
