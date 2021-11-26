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
<%@ taglib uri="http://www.ptc.com/windchill/taglib/jcaMvc" prefix="mvc"%>
<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>


<jsp:useBean id="commandBean" class="com.ptc.netmarkets.util.beans.NmCommandBean" scope="request"/>
<%

Object obj=null;
if(commandBean.getPageOid()!=null) {
	obj = commandBean.getPageOid().getRefObject();
} else {
	String oid = request.getParameter("oid");
	if (oid != null) {
		obj = NmOid.newNmOid(oid).getRefObject();
	}
}

boolean accessEnforced = SessionServerHelper.manager.isAccessEnforced();
WorkItem currentWorkItem=null;

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
					<jsp:include page="/netmarkets/jsp/customtemplates/completeButton.jsp"/>
				</FONT>
			</td>
		</tr>
	</table>
</div>
<%
	}

if (showAttributePanel) {
%>
<mvc:attributePanel/>
<%
}

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
	%>
			<jsp:include page="<%=appFormJspPath%>" />
			<%if (showAttributePanel) { %>
			<div class=" x-panel attribute-apanel x-reset x-form-label-left">
				<div id="workitem_details_attributes_group_input_container_div" class=" x-form"></div>
			</div>
			<script type="text/javascript">
			var moveWorkItemCommentsDetailAttributeGroup = function() {
				//window.alert("onReady ...");
				var inputGroup = document.getElementById("workitem_details_attributes_group_input");//$("workitem_details_attributes_group_input");
				//window.alert(inputGroup);
				if (!inputGroup) {
					window.alert("workitem_details_attributes_group_input is " + inputGroup);
					//window.setTimeout(moveWorkItemCommentsDetailAttributeGroup, 100);
					return;
				}
				var inputGroupContainer = document.getElementById("workitem_details_attributes_group_input_container_div");//$("workitem_details_attributes_group_input_container_div");				
				if (!inputGroupContainer) {
					window.alert("workitem_details_attributes_group_input_container_div is " + inputGroupContainer);
					//window.setTimeout(moveWorkItemCommentsDetailAttributeGroup, 100);
					return;
				}
				//window.alert("before clone");
				var inputGroupClone = inputGroup.cloneNode();// cloneNode 这个方法在FireFox中也会发生错误 //document.createElement("fieldset");
				//window.alert(inputGroup);
				//inputGroup.hide(); // 这行在firefox中会出现错误，导致代码不再往下执行，使用下面代码替代
				//window.alert("before hide");
				inputGroup.style.display = "none";
				//window.alert("after hide");
				//inputGroupClone.setAttribute("id", "workitem_details_attributes_group_input_clone");
				//inputGroupClone.setAttribute("class", "x-fieldset x-form-label-left");
				//inputGroupClone.setAttribute("style", "width: auto; display: block;");
				var childs = inputGroup.childNodes;
				//window.alert(childs.length);
				var childsToRemove = new Array();
				//window.alert(childs.length);
				for (var i = 0; i < childs.length ; i++) {
					childsToRemove.push(childs.item(i));
					//var child = inputGroup.removeChild()
					//inputGroupClone.appendchild(child);
				}
				for (var i = 0; i< childsToRemove.length; i++) {
					inputGroup.removeChild(childsToRemove[i]);
					inputGroupClone.appendChild(childsToRemove[i]);
				}
				//window.alert(childsToRemove.length);
				inputGroupContainer.appendChild(inputGroupClone);
			};
			//PTC.onAvailable("workitem_details_attributes_group_input", function() {
			//	window.alert("onAvailable");
				//moveWorkItemCommentsDetailAttributeGroup();
			//});	
			PTC.onReady(function() {
				//window.alert("on ready");
				moveWorkItemCommentsDetailAttributeGroup();
			});
			</script>			
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
					<jsp:include page="/netmarkets/jsp/customtemplates/completeButton.jsp"/>
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
}
%>
