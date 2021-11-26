package com.catl.integration;


public class Message{
	/**
	 * 发送Part时表示	物料编号
	 * 发送Bom时表示	父件编号
	 */
	private String number;
	/**
	 * 发送Bom时表示	子件编号
	 */
	private String childNumber;
	/**
	 * 发送Bom时表示	替代件编号
	 */
	private String stituteNumber;
	private String text;
	private boolean success;
	private String action;
	private String ecnNumber;
	private String drawingNumber;
	private String drawingVersion;
	public final static String PART_CREATE="调用part创建接口";
	public final static String STARTPPART_CHANGE="调用startppart创建接口";
	public final static String PART_CHANGE="调用part修改接口";
	public final static String BOM_CREATE="调用bom创建接口";
	public final static String BOM_CHANGE="调用bom修改接口";
	public final static String ECN="调用ecn接口";
	public final static String DCN="调用dcn接口";
	public final static String DRAWING="调用drawing接口";
	public final static String PROGRAM_EXCEPTION="程序异常";
	
	public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }
    public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getChildNumber() {
		return childNumber;
	}
	public void setChildNumber(String childNumber) {
		this.childNumber = childNumber;
	}
	public String getStituteNumber() {
		return stituteNumber;
	}
	public void setStituteNumber(String stituteNumber) {
		this.stituteNumber = stituteNumber;
	}
    public String getEcnNumber() {
        return ecnNumber;
    }
    public void setEcnNumber(String ecnNumber) {
        this.ecnNumber = ecnNumber;
    }
    public String getDrawingNumber() {
        return drawingNumber;
    }
    public void setDrawingNumber(String drawingNumber) {
        this.drawingNumber = drawingNumber;
    }
    public String getDrawingVersion() {
        return drawingVersion;
    }
    public void setDrawingVersion(String drawingVersion) {
        this.drawingVersion = drawingVersion;
    }
	
}