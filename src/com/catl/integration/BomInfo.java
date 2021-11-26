package com.catl.integration;

public class BomInfo {
	public String getParentPartNumber() {
		return parentPartNumber;
	}
	public void setParentPartNumber(String parentPartNumber) {
		this.parentPartNumber = parentPartNumber;
	}
	public String getChildPartNumber() {
		return childPartNumber;
	}
	public void setChildPartNumber(String childPartNumber) {
		this.childPartNumber = childPartNumber;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	public String getSubstitutePartNumber() {
		return substitutePartNumber;
	}
	public void setSubstitutePartNumber(String substitutePartNumber) {
		this.substitutePartNumber = substitutePartNumber;
	}
	public String getEcnNumber() {
		return ecnNumber;
	}
	public void setEcnNumber(String ecnNumber) {
		this.ecnNumber = ecnNumber;
	}
	public String getVersionBig() {
        return versionBig;
    }
    public void setVersionBig(String versionBig) {
        this.versionBig = versionBig;
    }
    public String getVersionSmall() {
        return versionSmall; 
    }
    public void setVersionSmall(String versionSmall) {
        this.versionSmall = versionSmall;
    }
    public String getOid() {
        return oid;
    }
    public void setOid(String oid) {
        this.oid = oid;
    }
    public double getSubQuantity() {
		return subQuantity;
	}
	public void setSubQuantity(double subQuantity) {
		this.subQuantity = subQuantity;
	}
	public int getMagnification() {
		return magnification;
	}
	public void setMagnification(int magnification) {
		this.magnification = magnification;
	}
	public int getSubMagnification() {
		return subMagnification;
	}
	public void setSubMagnification(int subMagnification) {
		this.subMagnification = subMagnification;
	}



	private String parentPartNumber;
	private String childPartNumber;
	private double quantity;
	private double subQuantity;
	private String substitutePartNumber;
	private String ecnNumber;
	private String versionBig;
	private String versionSmall;
	private int magnification;
	private int subMagnification;
	private String oid;
		

}
