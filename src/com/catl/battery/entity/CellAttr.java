package com.catl.battery.entity;

import java.util.ArrayList;
import java.util.List;

public class CellAttr {
   private String region="";
   private Object value="";
   private String style="实数";
   private String dataformatstring="";
   private short dataformat=-1;
   private Object displayvalue="";
   private List datalist=new ArrayList();
   private String color;
   
    public String getColor() {
	return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public Object getDisplayvalue() {
	return displayvalue;
	}
	public void setDisplayvalue(Object displayvalue) {
		this.displayvalue = displayvalue;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
    public String getDataformatstring() {
	    return dataformatstring;
	}
	public void setDataformatstring(String dataformatstring) {
		this.dataformatstring = dataformatstring;
	}
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
    public Object getValue() {
	return value;
   }
   public void setValue(Object value) {
	this.value = value;
   }
	public short getDataformat() {
		return dataformat;
	}
	public void setDataformat(short dataformat) {
		this.dataformat = dataformat;
	}
    public List getDatalist() {
	    return datalist;
	}
	public void setDatalist(List datalist) {
		this.datalist = datalist;
	}
}
