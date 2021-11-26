package com.catl.line.entity;

import java.io.Serializable;

public class UploadMsg implements Serializable{
	private static final long serialVersionUID = 1L;
	private String number;
	private String name;
	private String msg;
	private boolean status;
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}

}
