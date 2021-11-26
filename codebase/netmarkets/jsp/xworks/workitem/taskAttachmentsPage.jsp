<%@page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@page import="wt.workflow.work.WorkItem"%>
<%@page import="com.ptc.netmarkets.model.NmOid" %>
<%@page import="wt.project.Role" %>
<%@page import="java.util.Set" %>
<%@page import="com.ptc.xworks.workflow.annotation.ActivityNodeDef"%>
<%@page import="com.ptc.xworks.workflow.annotation.AttachmentGroupDef"%>
<%@page import="com.ptc.xworks.workflow.annotation.UserOperation"%>
<%@page import="com.ptc.xworks.workflow.annotation.WorkflowTemplateDef"%>
<%@page import="com.ptc.xworks.workflow.template.ActivityNodeInfo"%>
<%@page import="com.ptc.xworks.workflow.template.WorkflowTemplateInfo"%>
<%@page import="com.ptc.xworks.util.XWorksHelper"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@taglib prefix="attachments" uri="http://www.ptc.com/windchill/taglib/attachments" %>

<jsp:useBean id="commandBean" class="com.ptc.netmarkets.util.beans.NmCommandBean" scope="request"/>
<%
String oid = request.getParameter("oid");
Object obj = null;
if (commandBean.getPageOid() != null) {
	obj = commandBean.getPageOid().getRefObject();
} else {
	if (oid != null) {
		obj = NmOid.newNmOid(oid).getRefObject();
	}
}

if (obj instanceof WorkItem) {
	WorkItem workitem = (WorkItem) obj;
	ActivityNodeInfo nodeInfo = XWorksHelper.getWorkflowTemplateInfoService().getActivityNodeInfo(workitem);
	if (nodeInfo != null) {
		String jspPath = nodeInfo.getActivityNodeDef().attachmentGroupJspPath();
		if (StringUtils.isBlank(jspPath)) { // 如果没有配置特别的JSP来显示附件分组,则使用本JSP来显示附件分组
			WorkflowTemplateDef templateDef = nodeInfo.getWorkflowTemplateInfo().getWorkflowTemplateDef();
			AttachmentGroupDef[] attachmentGroupDefs = templateDef.attachmentGroupDefs();
			Role role = workitem.getRole();
			for (AttachmentGroupDef attachmentGroupDef : attachmentGroupDefs){
				String groupId = attachmentGroupDef.groupId();			
				Set<UserOperation> allowedOperations = nodeInfo.getAttachmentUserOperation(groupId, role);
				if (allowedOperations.size() > 0) {
%>
					<jsp:include page="${mvc:getComponentURL('com.ptc.xworks.workflow.attachment.AttachmentGroupTableBuilder')}">
						<jsp:param name="groupId" value="<%=groupId %>"/>
						<jsp:param name="oid" value="<%=oid %>"/>
					</jsp:include>				
<%
				}
			}
		} else {				
			request.getRequestDispatcher(jspPath).forward(request, response);
		}
	}
} else {
	out.print("This page only supports WorkItem as ContextObject!");
}

%>

<script type="text/javascript">
function reloadAttachmentGroupTable(groupId, workitemOid, tableID){
	var params = {groupId : groupId, oid : workitemOid};
	PTC.jca.table.Utils.reload(tableID, params, true);
}
</script>

