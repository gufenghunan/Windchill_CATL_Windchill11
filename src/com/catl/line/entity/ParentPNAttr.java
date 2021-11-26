package com.catl.line.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ParentPNAttr implements Serializable {
	private static final long serialVersionUID = 1L;
	private String partNumber;
	private String name;
	private String displayname;
	private List<String> valuelist = new ArrayList<String>();
	private String defaultvalue;
	private String type;
	private Object value;
	private boolean required;

	public ParentPNAttr(String partNumber, String name, String displayname, String defaultvalue, String type,
			Object value, boolean required) {
		super();
		this.partNumber = partNumber;
		this.name = name;
		this.displayname = displayname;
		this.defaultvalue = defaultvalue;
		this.type = type;
		this.value = value;
		this.required = required;
	}

	public String getPartNumber(){
		return partNumber;
	}

	public void setPartNumber(String partNumber){
		this.partNumber=partNumber;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayname() {
		return displayname;
	}

	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}

	public List<String> getValuelist() {
		return valuelist;
	}

	public void setValuelist(List<String> valuelist) {
		this.valuelist = valuelist;
	}

	public String getDefaultvalue() {
		return defaultvalue;
	}

	public void setDefaultvalue(String defaultvalue) {
		this.defaultvalue = defaultvalue;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

}
