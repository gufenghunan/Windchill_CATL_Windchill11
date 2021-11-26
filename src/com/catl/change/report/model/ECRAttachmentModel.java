package com.catl.change.report.model;

import java.util.Vector;

public class ECRAttachmentModel {

  private  String ecrnumber="";
  
  private  String ecrWNO="";
  
  private  String applicant="";
  
  private  String applicantiondepartment="";
  
  private  String applicantiondate="";
  
  private  String changeitem="";
  
  private  String changetype="";
  
  private  String changeorigin="";
  
  private  String changebackgrounddescription="";
  
  private  String initialchangesolution="";
  
  private  String ecrConclusion="";
  
  private  String departmentmanagerReview="";
  
  
  private  AffectedpersonnelModel affectedpersonnelModel=null;
  
  private  ChangepartsModel changepartsModel=null;
  
  private  String  SE_PE_Estimate="";
  
  private  String  se_pe_conclusion_proposal="";
  
  private  String application_dept_manager_approve="";
  
	private  String se_pe_conclusion_sign="";
	
	private  String se_pe_conclusion_date="";


	
	public String getSe_pe_conclusion_sign() {
		return se_pe_conclusion_sign;
	}




	public void setSe_pe_conclusion_sign(String se_pe_conclusion_sign) {
		this.se_pe_conclusion_sign = se_pe_conclusion_sign;
	}




	public String getSe_pe_conclusion_date() {
		return se_pe_conclusion_date;
	}




	public void setSe_pe_conclusion_date(String se_pe_conclusion_date) {
		this.se_pe_conclusion_date = se_pe_conclusion_date;
	}




	public String getApplication_dept_manager_sign() {
		return application_dept_manager_sign;
	}




	public void setApplication_dept_manager_sign(
			String application_dept_manager_sign) {
		this.application_dept_manager_sign = application_dept_manager_sign;
	}




	public String getApplication_dept_manager_date() {
		return application_dept_manager_date;
	}




	public void setApplication_dept_manager_date(
			String application_dept_manager_date) {
		this.application_dept_manager_date = application_dept_manager_date;
	}




	private  String application_dept_manager_sign="";
	
	private  String application_dept_manager_date="";


private  Vector<ChangepartsModel> changedPNVector=null;




public String getEcrnumber() {
	return ecrnumber;
}




public void setEcrnumber(String ecrnumber) {
	this.ecrnumber = ecrnumber;
}




public String getEcrWNO() {
	return ecrWNO;
}




public void setEcrWNO(String ecrWNO) {
	this.ecrWNO = ecrWNO;
}




public String getApplicant() {
	return applicant;
}




public void setApplicant(String applicant) {
	this.applicant = applicant;
}




public String getApplicantiondepartment() {
	return applicantiondepartment;
}




public void setApplicantiondepartment(String applicantiondepartment) {
	this.applicantiondepartment = applicantiondepartment;
}




public String getApplicantiondate() {
	return applicantiondate;
}




public void setApplicantiondate(String applicantiondate) {
	this.applicantiondate = applicantiondate;
}




public String getChangeitem() {
	return changeitem;
}




public void setChangeitem(String changeitem) {
	this.changeitem = changeitem;
}




public String getChangetype() {
	return changetype;
}




public void setChangetype(String changetype) {
	this.changetype = changetype;
}




public String getChangeorigin() {
	return changeorigin;
}




public void setChangeorigin(String changeorigin) {
	this.changeorigin = changeorigin;
}




public String getChangebackgrounddescription() {
	return changebackgrounddescription;
}




public void setChangebackgrounddescription(String changebackgrounddescription) {
	this.changebackgrounddescription = changebackgrounddescription;
}




public String getInitialchangesolution() {
	return initialchangesolution;
}




public void setInitialchangesolution(String initialchangesolution) {
	this.initialchangesolution = initialchangesolution;
}




public String getEcrConclusion() {
	return ecrConclusion;
}




public void setEcrConclusion(String ecrConclusion) {
	this.ecrConclusion = ecrConclusion;
}




public String getDepartmentmanagerReview() {
	return departmentmanagerReview;
}




public void setDepartmentmanagerReview(String departmentmanagerReview) {
	this.departmentmanagerReview = departmentmanagerReview;
}






public AffectedpersonnelModel getAffectedpersonnelModel() {
	return affectedpersonnelModel;
}




public void setAffectedpersonnelModel(
		AffectedpersonnelModel affectedpersonnelModel) {
	this.affectedpersonnelModel = affectedpersonnelModel;
}




public ChangepartsModel getChangepartsModel() {
	return changepartsModel;
}




public void setChangepartsModel(ChangepartsModel changepartsModel) {
	this.changepartsModel = changepartsModel;
}




public String getSE_PE_Estimate() {
	return SE_PE_Estimate;
}




public void setSE_PE_Estimate(String sE_PE_Estimate) {
	SE_PE_Estimate = sE_PE_Estimate;
}




public String getSe_pe_conclusion_proposal() {
	return se_pe_conclusion_proposal;
}




public void setSe_pe_conclusion_proposal(String se_pe_conclusion_proposal) {
	this.se_pe_conclusion_proposal = se_pe_conclusion_proposal;
}




public String getApplication_dept_manager_approve() {
	return application_dept_manager_approve;
}




public void setApplication_dept_manager_approve(
		String application_dept_manager_approve) {
	this.application_dept_manager_approve = application_dept_manager_approve;
}




public Vector<ChangepartsModel> getChangedPNVector() {
	return changedPNVector;
}




public void setChangedPNVector(Vector<ChangepartsModel> changedPNVector) {
	this.changedPNVector = changedPNVector;
}
  
  
  


}
