package com.catl.part;

import java.util.ArrayList;
import java.util.List;

public class PartMatchingCodeBean {
	public String matchingCode = "";
	
	public List<String> bmuVersion = new ArrayList<String>();
	
	public List<String> cscVersion = new ArrayList<String>();
	
	public List<String> hvbVersion = new ArrayList<String>();

	public String getMatchingCode() {
		return matchingCode;
	}

	public void setMatchingCode(String matchingCode) {
		this.matchingCode = matchingCode;
	}

	public List<String> getBmuVersion() {
		return bmuVersion;
	}

	public void setBmuVersion(List<String> bmuVersion) {
		this.bmuVersion = bmuVersion;
	}

	public List<String> getCscVersion() {
		return cscVersion;
	}

	public void setCscVersion(List<String> cscVersion) {
		this.cscVersion = cscVersion;
	}

	public List<String> getHvbVersion() {
		return hvbVersion;
	}

	public void setHvbVersion(List<String> hvbVersion) {
		this.hvbVersion = hvbVersion;
	}
	
	

}
