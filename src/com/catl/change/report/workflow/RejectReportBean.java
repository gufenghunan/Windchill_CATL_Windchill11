package com.catl.change.report.workflow;


public class RejectReportBean {
	// 每月流程驳回报表
	
	private String processName;				// 流程名称
	private String rejectLink;				// 驳回环节
	private String approvalComment;			// 审批意见
	private String rejectComment;			// 驳回意见
	private String rejectTime;				// 驳回时间
	private String rejectPerson;			// 驳回人
	private String processApplicant  ;		// 流程申请人

	public String getProcessName() {
		return processName;
	}
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	public String getRejectLink() {
		return rejectLink;
	}
	public void setRejectLink(String rejectLink) {
		this.rejectLink = rejectLink;
	}
	public String getApprovalComment() {
		return approvalComment;
	}
	public void setApprovalComment(String approvalComment) {
		this.approvalComment = approvalComment;
	}
	public String getRejectComment() {
		return rejectComment;
	}
	public void setRejectComment(String rejectComment) {
		this.rejectComment = rejectComment;
	}
	public String getRejectTime() {
		return rejectTime;
	}
	public void setRejectTime(String rejectTime) {
		this.rejectTime = rejectTime;
	}
	public String getRejectPerson() {
		return rejectPerson;
	}
	public void setRejectPerson(String rejectPerson) {
		this.rejectPerson = rejectPerson;
	}
	public String getProcessApplicant() {
		return processApplicant;
	}
	public void setProcessApplicant(String processApplicant) {
		this.processApplicant = processApplicant;
	}
}
