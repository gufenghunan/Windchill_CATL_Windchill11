<%@page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@page import="com.ptc.xworks.util.XWorksHelper"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.ptc.xworks.workflow.template.WorkflowTemplateInfoService"%>
<%@page import="com.ptc.xworks.workflow.template.WorkflowTemplateInfo"%>
<%@page import="com.ptc.xworks.workflow.annotation.ActivityNodeDef" %>
<%@page import="com.ptc.xworks.workflow.template.ActivityNodeInfo" %>
<%@page import="wt.session.SessionServerHelper"%>
<%@page import="wt.util.WTException"%>
<%@page import="wt.org.WTUser"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.preference.PreferenceHelper"%>
<%@page import="wt.workflow.definer.WfAssignedActivityTemplate"%>
<%@page import="wt.workflow.definer.WfTemplateObject"%>
<%@page import="wt.workflow.engine.WfActivity"%>
<%@page import="wt.workflow.work.WorkItem"%>
<%@page import="com.ptc.netmarkets.model.NmOid"%>
<%@page import="com.ptc.xworks.windchill.util.NetmarketsUtils"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/jcaMvc" prefix="mvc"%>
<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>


<jsp:useBean id="commandBean" class="com.ptc.netmarkets.util.beans.NmCommandBean" scope="request"/>
<%

Object obj = NetmarketsUtils.getContextObject(commandBean);

boolean accessEnforced = SessionServerHelper.manager.setAccessEnforced(false);
WorkItem currentWorkItem = null;

boolean isShowSaveComplete = false;
int renderer = 0;
try{
	SessionServerHelper.manager.setAccessEnforced(false);
	if(obj!=null )
	{
		if(obj instanceof WorkItem){
			currentWorkItem=(WorkItem)obj;
			WfActivity wActivity = (currentWorkItem.getSource().getObject() instanceof WfActivity) ? (WfActivity) (currentWorkItem.getSource().getObject()) : null;
	        WfTemplateObject templateObject= (WfTemplateObject) wActivity.getTemplate().getObject();
	        renderer = templateObject.getContextSignature().getRenderer();
		}		
	}

	Object pref = PreferenceHelper.service.getValue(commandBean.getContainerRef(), "wt.workflow.useTaskFormTemplates", "WINDCHILL", null);
	  
	if(renderer== WfAssignedActivityTemplate.TYPE_CONFIGURABLE){
		if(pref != null && !(Boolean)pref){
			isShowSaveComplete = true;;
		}
	} else {
		isShowSaveComplete = true;;
	}
} catch (WTException e) {
    e.printStackTrace();
} finally {
    SessionServerHelper.manager.setAccessEnforced(accessEnforced);
}

//在表格动态更新的情况下，不显示此部分
String dynamicUpdate = request.getParameter("dynamicUpdate");
String refreshTable = request.getParameter("refreshTable");
String viewchange = request.getParameter("viewchange");
String dynamicDataSource = request.getParameter("dynamicDataSource");
String refreshByAction = request.getParameter("refreshByAction");
boolean isDynamicUpdate = "true".equals(dynamicUpdate);
boolean isChangeTableView = StringUtils.isNotBlank(refreshTable) && "true".equals(viewchange);
boolean isSaveTableView = StringUtils.isNotBlank(refreshTable) && "true".equals(dynamicDataSource);
boolean isRefreshByAction = StringUtils.isNotBlank(refreshTable) && "true".equals(refreshByAction);

//是否显示AttributePanel,在动态更新Table或刷新Table时,不能显示AttributePanel,否则出错
boolean showAttributePanel = !isDynamicUpdate && !isChangeTableView && !isSaveTableView && !isRefreshByAction;
request.setAttribute("showAttributePanel", showAttributePanel);

if(isShowSaveComplete && showAttributePanel){
%>
<div style="padding:0 8px 0 0">
	<table width="100%">
		<tr align="right">
			<td valign="middle" align="right" valign=top>
				<FONT class=wizardbuttonfont>
					<jsp:include page="/netmarkets/jsp/xworks/workitem/completeButton.jsp"/>
				</FONT>
			</td>
		</tr>
	</table>
</div>
<%
	}

if (showAttributePanel) {
%>
<jsp:include page="/netmarkets/jsp/xworks/workitem/taskWorkflowFormHeader.jsp" />
<script type="text/javascript">
// default validateApplicationFormCallback() , this function can be overrided in customizaed JSP
function validateApplicationFormCallback(element, event) {
	console.log("XWorks default validateApplicationFormCallback() has been called. always return true.");
	return true;
};
</script>

<mvc:attributePanel compId="attributePanel"/>
<%
}

boolean applicationFormNeeded = false;// 是否启用了流程任务表单
// 通过读取配置信息，决定是否在详细页面显示一个表单JSP
ActivityNodeInfo nodeInfo = null;
if(obj instanceof WorkItem) {
	WorkflowTemplateInfoService templateInfoService = XWorksHelper.getWorkflowTemplateInfoService();
	nodeInfo = templateInfoService.getActivityNodeInfo((WorkItem) obj);
}
if (nodeInfo != null) {
	ActivityNodeDef activityNodeDef = nodeInfo.getActivityNodeDef();
	boolean appFormEnabled = activityNodeDef.enableApplicationForm();
	String appFormJspPath = activityNodeDef.applicationFormJspPath();
	if (appFormEnabled) {
		 if (StringUtils.isNotBlank(appFormJspPath)) {
			 applicationFormNeeded = true; // 确实启动了流程任务表单
	%>
			<jsp:include page="<%=appFormJspPath%>" />
			
			<%if (showAttributePanel) { %>
			<jsp:include page="/netmarkets/jsp/xworks/workitem/taskWorkflowFormFooter.jsp" />		
			<%}%>
	<%
		 } else {
			%><b style="font-color:red">-- ERROR:embeddedApplicationFormJspPath not defined ! --</b><%
		 }
	}
}
%>



<%
if(isShowSaveComplete && showAttributePanel){
	%>
	<div style="padding:0 8px 0 0">
	<table width="100%">
		<tr align="right">
			<td valign="middle" align="right" valign=top>
				<FONT class=wizardbuttonfont>
					<jsp:include page="/netmarkets/jsp/xworks/workitem/completeButton.jsp"/>
				</FONT>
			</td>
		</tr>
	</table>
</div>
<%
	}
%>

<%
if(showAttributePanel){
%>
<script>
	<%//Shital: This method will sync workitem comment with task assistant comments %>
	changeWorkItemComment();
</script>
<%
	// 如果启用流程表单，则输出此内容，用于标识已经完成客制化流程表单的加载
	if (applicationFormNeeded) {
%>
	<script type="text/javascript">
	applicationFormNeeded = true;
	applicationFromLoadFinished = true;
	</script>
<%
    }
}
%>
