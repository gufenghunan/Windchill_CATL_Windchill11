package com.catl.ri.entity;

import java.util.List;

public class MaterialDB {
 private String recipenumber;
 private String materialname;
 private String pn;
 private String loadding;
 private String clf;
 private String isPhantom;
 private String speicalkey="";
private List<MaterialAttr> attr;
    public String getRecipenumber() {
		return recipenumber;
	}
	public void setRecipenumber(String recipenumber) {
		this.recipenumber = recipenumber;
	}
	public String getMaterialname() {
		return materialname;
	}
	public void setMaterialname(String materialname) {
		this.materialname = materialname;
	}
	public String getPn() {
		return pn;
	}
	public void setPn(String pn) {
		this.pn = pn;
	}
	public String getLoadding() {
		return loadding;
	}
	public void setLoadding(String loadding) {
		this.loadding = loadding;
	}
	public List<MaterialAttr> getAttr() {
		return attr;
	}
	public void setAttr(List<MaterialAttr> attr) {
		this.attr = attr;
	}
	public String getClf() {
		return clf;
	}
	public void setClf(String clf) {
		this.clf = clf;
	}
	public String getIsPhantom() {
		return isPhantom;
	}
	public void setIsPhantom(String isPhantom) {
		this.isPhantom = isPhantom;
	}
	public String getSpeicalkey() {
			return speicalkey;
	}
	public void setSpeicalkey(String speicalkey) {
		this.speicalkey = speicalkey;
	}
}
