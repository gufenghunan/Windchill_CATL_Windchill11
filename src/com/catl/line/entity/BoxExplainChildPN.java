package com.catl.line.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class BoxExplainChildPN  implements Serializable{

	public String number;
	public String name;
	public String quantity;
	public String packageAsk;
	public String getPackageAsk() {
		return packageAsk;
	}
	public void setPackageAsk(String packageAsk) {
		this.packageAsk = packageAsk;
	}
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
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	
	
}
