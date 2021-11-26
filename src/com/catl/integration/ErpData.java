package com.catl.integration;

import java.util.List;

public class ErpData {
	
	private List<BomInfo>  boms;
	
	private List<DrawingInfo> drawings;
	
	public List<DrawingInfo> getDrawings() {
		return drawings;
	}
	public void setDrawings(List<DrawingInfo> drawings) {
		this.drawings = drawings;
	}
	public List<BomInfo> getBoms() {
		return boms;
	}
	public void setBoms(List<BomInfo> boms) {
		this.boms = boms;
	}
	public List<PartInfo> getParts() {
		return parts;
	}
	public void setParts(List<PartInfo> parts) {
		this.parts = parts;
	}
	public EcInfo getEcn() {
		return ecn;
	}
	public void setEcn(EcInfo ecn) {
		this.ecn = ecn;
	}
	private List<PartInfo>  parts;
	private EcInfo  ecn;
	

}
