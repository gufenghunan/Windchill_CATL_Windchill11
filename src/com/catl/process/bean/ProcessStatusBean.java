package com.catl.process.bean;

public class ProcessStatusBean {

    private String workState;

    private String activityName;

    private String roleName;

    private String workVote;// 路由

    private String workComments;// 意见

    private String assignee;// 负责人

    private String completedBy;// 完成人

    private String deadline;// 截止日期

    private String completedDate;// 完成日期

    public String getWorkState() {
        return workState;
    }

    public void setWorkState(String workState) {
        this.workState = workState;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getWorkVote() {
        return workVote;
    }

    public void setWorkVote(String workVote) {
        this.workVote = workVote;
    }

    public String getWorkComments() {
        return workComments;
    }

    public void setWorkComments(String workComments) {
        this.workComments = workComments;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getCompletedBy() {
        return completedBy;
    }

    public void setCompletedBy(String completedBy) {
        this.completedBy = completedBy;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(String completedDate) {
        this.completedDate = completedDate;
    }

    
}
