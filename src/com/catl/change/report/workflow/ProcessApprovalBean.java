package com.catl.change.report.workflow;


public class ProcessApprovalBean {
	// 每月流程各环节审批具体时间统计报表
	
	private String processName;				// 流程名称
	private String processStatus;			// 流程状态
	private String processStartTime;		// 流程启动时间
	private String approverId;				// 审批人工号
	private String approverName;			// 审批人姓名
	private String linkName;				// 审批环节名称
	private String approvalStartTime;		// 审批开始时间
	private String approvalEndTime;			// 审批结束时间
	private int approvalTime;				// 审批耗时(minute)
	private String applicantId;				// 流程申请人工号
	private String applicantName;			// 流程申请人姓名

	
	public String getProcessName() {
		return processName;
	}
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	public String getProcessStatus() {
		return processStatus;
	}
	public void setProcessStatus(String processStatus) {
		this.processStatus = processStatus;
	}
	public String getProcessStartTime() {
		return processStartTime;
	}
	public void setProcessStartTime(String processStartTime) {
		this.processStartTime = processStartTime;
	}
	public String getApproverId() {
		return approverId;
	}
	public void setApproverId(String approverId) {
		this.approverId = approverId;
	}
	public String getApproverName() {
		return approverName;
	}
	public void setApproverName(String approverName) {
		this.approverName = approverName;
	}
	public String getLinkName() {
		return linkName;
	}
	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}
	public String getApprovalStartTime() {
		return approvalStartTime;
	}
	public void setApprovalStartTime(String approvalStartTime) {
		this.approvalStartTime = approvalStartTime;
	}
	public String getApprovalEndTime() {
		return approvalEndTime;
	}
	public void setApprovalEndTime(String approvalEndTime) {
		this.approvalEndTime = approvalEndTime;
	}
	public int getApprovalTime() {
		return approvalTime;
	}
	public void setApprovalTime(int approvalTime) {
		this.approvalTime = approvalTime;
	}
	public String getApplicantId() {
		return applicantId;
	}
	public void setApplicantId(String applicantId) {
		this.applicantId = applicantId;
	}
	public String getApplicantName() {
		return applicantName;
	}
	public void setApplicantName(String applicantName) {
		this.applicantName = applicantName;
	}

}
