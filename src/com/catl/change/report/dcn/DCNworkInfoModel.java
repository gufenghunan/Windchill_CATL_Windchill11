package com.catl.change.report.dcn;

import java.util.Date;

public class DCNworkInfoModel {
	private String ManagerContent = "";
	
	private String ManagerQM = "";
	
	private Date ManagerDate;
	
	private String SEContent = "";
	
	private String SEQM = "";
	
	private String SEDate = "";

	public String getManagerContent() {
		return ManagerContent;
	}

	public void setManagerContent(String managerContent) {
		ManagerContent = managerContent;
	}

	public String getManagerQM() {
		return ManagerQM;
	}

	public void setManagerQM(String managerQM) {
		ManagerQM = managerQM;
	}

	public Date getManagerDate() {
		return ManagerDate;
	}

	public void setManagerDate(Date managerDate) {
		ManagerDate = managerDate;
	}

	public String getSEContent() {
		return SEContent;
	}

	public void setSEContent(String sEContent) {
		SEContent = sEContent;
	}

	public String getSEQM() {
		return SEQM;
	}

	public void setSEQM(String sEQM) {
		SEQM = sEQM;
	}

	public String getSEDate() {
		return SEDate;
	}

	public void setSEDate(String sEDate) {
		SEDate = sEDate;
	}
	
}
