package com.catl.pd.entity;

public class MaterialAttr {
	 private String name;
	 private Object value;
	 private String region;
	 private String style;
	 private String values;
	 public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
    public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	public String getValues() {
		return values;
	}
	public void setValues(String values) {
		this.values = values;
	}
}
