package com.catl.integration;

public class DrawingInfo {

	private String partNumber;
	private String drawingNumber;
	private String drawingVersion;
	private String oid;
	
	public String getOid() {
        return oid;
    }
    public void setOid(String oid) {
        this.oid = oid;
    }
    public String getPartNumber() {
		return partNumber;
	}
	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
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
