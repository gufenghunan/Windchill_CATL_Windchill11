package com.catl.report;

public class ChangeReportBean implements Cloneable{

	private String ecnNumber;
	private String ecnName;
	private String ecnCreate;
	private String isRESOLVED;
	private String createDate;
	private String ecnApproveDate;
	private String needDate;
	private String ecaNumber;
	private String ecaState;
	private String ecaWorkOwner;
	private String workflowNumber;
	private String workflowName;
	private String affectObjectReleaseDate;
	private String affectObjectNumber;
	private String affectObjectName;
	private String affectObjectType;
	private String affectObjectVersion;
	private String affectObjectVersionAfter;
	private String bomCompareResultMsg;
	private String bomCompareResultObjectNumber;
	private String bomCompareResultObjectName;
	private String bomCompareResultObjectDosageNew;
	private String bomCompareResultObjectDosageOld;
	public String getEcnNumber() {
		return ecnNumber;
	}
	public void setEcnNumber(String ecnNumber) {
		this.ecnNumber = ecnNumber;
	}
	public String getEcnName() {
		return ecnName;
	}
	public void setEcnName(String ecnName) {
		this.ecnName = ecnName;
	}
	public String getIsRESOLVED() {
		return isRESOLVED;
	}
	public void setIsRESOLVED(String isRESOLVED) {
		this.isRESOLVED = isRESOLVED;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getNeedDate() {
		return needDate;
	}
	public void setNeedDate(String needDate) {
		this.needDate = needDate;
	}
	public String getAffectObjectReleaseDate() {
		return affectObjectReleaseDate;
	}
	public void setAffectObjectReleaseDate(String affectObjectReleaseDate) {
		this.affectObjectReleaseDate = affectObjectReleaseDate;
	}
	public String getWorkflowNumber() {
		return workflowNumber;
	}
	public void setWorkflowNumber(String workflowNumber) {
		this.workflowNumber = workflowNumber;
	}
	public String getWorkflowName() {
		return workflowName;
	}
	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}
	public String getAffectObjectNumber() {
		return affectObjectNumber;
	}
	public void setAffectObjectNumber(String affectObjectNumber) {
		this.affectObjectNumber = affectObjectNumber;
	}
	public String getAffectObjectName() {
		return affectObjectName;
	}
	public void setAffectObjectName(String affectObjectName) {
		this.affectObjectName = affectObjectName;
	}
	public String getAffectObjectType() {
		return affectObjectType;
	}
	public void setAffectObjectType(String affectObjectType) {
		this.affectObjectType = affectObjectType;
	}
	public String getAffectObjectVersion() {
		return affectObjectVersion;
	}
	public void setAffectObjectVersion(String affectObjectVersion) {
		this.affectObjectVersion = affectObjectVersion;
	}
	public String getAffectObjectVersionAfter() {
		return affectObjectVersionAfter;
	}
	public void setAffectObjectVersionAfter(String affectObjectVersionAfter) {
		this.affectObjectVersionAfter = affectObjectVersionAfter;
	}
	public String getBomCompareResultMsg() {
		return bomCompareResultMsg;
	}
	public void setBomCompareResultMsg(String bomCompareResultMsg) {
		this.bomCompareResultMsg = bomCompareResultMsg;
	}
	public String getBomCompareResultObjectNumber() {
		return bomCompareResultObjectNumber;
	}
	public void setBomCompareResultObjectNumber(String bomCompareResultObjectNumber) {
		this.bomCompareResultObjectNumber = bomCompareResultObjectNumber;
	}
	public String getBomCompareResultObjectName() {
		return bomCompareResultObjectName;
	}
	public void setBomCompareResultObjectName(String bomCompareResultObjectName) {
		this.bomCompareResultObjectName = bomCompareResultObjectName;
	}
	public String getBomCompareResultObjectDosageNew() {
		return bomCompareResultObjectDosageNew;
	}
	public void setBomCompareResultObjectDosageNew(String bomCompareResultObjectDosageNew) {
		this.bomCompareResultObjectDosageNew = bomCompareResultObjectDosageNew;
	}
	public String getBomCompareResultObjectDosageOld() {
		return bomCompareResultObjectDosageOld;
	}
	public void setBomCompareResultObjectDosageOld(String bomCompareResultObjectDosageOld) {
		this.bomCompareResultObjectDosageOld = bomCompareResultObjectDosageOld;
	}
	public String getEcnCreate() {
		return ecnCreate;
	}
	public void setEcnCreate(String ecnCreate) {
		this.ecnCreate = ecnCreate;
	}
	public String getEcnApproveDate() {
		return ecnApproveDate;
	}
	public void setEcnApproveDate(String ecnApproveDate) {
		this.ecnApproveDate = ecnApproveDate;
	}
	public String getEcaNumber() {
		return ecaNumber;
	}
	public void setEcaNumber(String ecaNumber) {
		this.ecaNumber = ecaNumber;
	}
	public String getEcaState() {
		return ecaState;
	}
	public void setEcaState(String ecaState) {
		this.ecaState = ecaState;
	}
	public String getEcaWorkOwner() {
		return ecaWorkOwner;
	}
	public void setEcaWorkOwner(String ecaWorkOwner) {
		this.ecaWorkOwner = ecaWorkOwner;
	}
	@Override
	protected Object clone(){
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
}
