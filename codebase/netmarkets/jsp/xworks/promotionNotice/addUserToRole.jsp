<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<!--TSB-->
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c-rt" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/core" prefix="wc"%>
<%@ taglib prefix="fmt" uri="http://www.ptc.com/windchill/taglib/fmt" %>

<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>

<%@ page import="java.util.ArrayList"
%><%@ page import="java.util.LinkedHashMap"
%><%@ page import="java.util.ResourceBundle"
%><%@ page import="com.ptc.netmarkets.model.NmSimpleOid"
%><%@ page import="com.ptc.netmarkets.util.misc.NmContext"
%><%@ page import="com.ptc.netmarkets.util.beans.NmCommandBean"
%><%@ page import="com.ptc.windchill.enterprise.team.teamResource"
%><%@ page import="com.ptc.netmarkets.model.NmOid"
%><%@ page import="com.ptc.netmarkets.work.NmWorkItemCommands"
%><%@ page import="wt.project.Role"
%><%@ page import="java.util.Set"
%><%@ page import="java.util.HashSet"
%><%@ page import="wt.workflow.work.WorkItem"
%><%@ page import="com.ptc.xworks.promotion.builders.PromotionNoticeHelper"
%><%@ page import="com.ptc.xworks.promotion.annotation.PromotionNoticeOptions"
%><%@ page import="com.ptc.xworks.promotion.annotation.RoleConfig"
%>


<%! private static final String RESOURCE = "wt.team.teamResource";%>

<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="com.ptc.windchill.enterprise.team.teamResource" />

<fmt:message var="associationLabel" key="ADD_TO_ROLE_LABEL" />

<%
ResourceBundle TeamRb = ResourceBundle.getBundle(RESOURCE, localeBean.getLocale());

NmCommandBean cb = new NmCommandBean();
cb.setCompContext(nmcontext.getContext().toString());
cb.setRequest(request);
String currentRole = null;	
currentRole = cb.getTextParameter("oid");
//WorkItem wi = (WorkItem) oid.getRefObject();
// For the toolbar, see if there are any roles selected.
// Use only the first role found and ignore users and groups.
ArrayList selected = cb.getSelectedInOpener();
// NmCommandBean returns NmContext, need to find out what kind of context:
for (int i = 0; i < selected.size(); i++) {
   NmOid target = null;
   Object nextObj = selected.get(i);

   if (nextObj instanceof NmContext) {
      target = ((NmContext)nextObj).getTargetOid();
   }
   else if (nextObj instanceof NmOid) {
      target = (NmOid)nextObj;
   }
   if (target instanceof NmSimpleOid) { // role
      currentRole = ((NmSimpleOid)target).getInternalName();
      break;
   }
}
//out.print(currentRole);
PromotionNoticeOptions option = PromotionNoticeHelper.getPromotionNoticeOptions(cb);

LinkedHashMap associationMap = new LinkedHashMap();
LinkedHashMap resourcePool = new LinkedHashMap();
//ArrayList teamRoles = NmWorkItemCommands.service.getSetupParticipantsRoles(cb,1);
ArrayList teamRoles = new ArrayList();
if(option.setupRoles().length == 0)
{%>
 <script language="javascript" type="text/javascript">
  wfalert("<%=TeamRb.getString(wt.team.teamResource.NO_ROLE_FOR_SETTING_PARTICIPANTS)%>");    
  wfWindowClose();
</script>
 <%
}

for (RoleConfig roleConfig : option.setupRoles()) {
	Role role = Role.toRole(roleConfig.role());
	associationMap.put(roleConfig.role(), role.getDisplay(localeBean.getLocale()));
}

//resourcePool = NmWorkItemCommands.service.getResourcePoolRoleMembers(cb,teamRoles);   
%>
<c-rt:set var="currentRole" value="<%= currentRole %>"/>
<c-rt:set var="associationMap" value="<%= associationMap %>"/>   
<c-rt:set var="resourcePool" value="<%= resourcePool %>"/>   
<c-rt:set var="participantType" value='<%= "U" %>'/><!-- U ~ User, G ~ WTGroup, O ~ Organization -->
<c:set var="emailAllowed" value="false"/>

<input type="hidden" id="XWORKS_CREATE_PROMOTION_NOTICE_ID" name="XWORKS_CREATE_PROMOTION_NOTICE_ID" value="<%=request.getParameter("createWindowId")%>" />
<script type="text/javascript">
handleSubmitResult = handleSubmitResult.wrap(
function(orig, status, nextAction, js, URL, dynamicRefreshInfo) {
	var tableId = "<%=request.getParameter("tableID")%>";
	opener.reloadSetupParticipantsTreeTable(tableId);
	//window.alert(tableId);
	orig(status, nextAction, js, URL, dynamicRefreshInfo); 
});
</script>
<!-- OOTB jca:participantPicker 
        actionClass="com.ptc.netmarkets.work.NmWorkItemCommands" 
        actionMethod="addUsersToRole" 
        participantType="${participantType}" 
        associationMap="${associationMap}" 
        associationLabel="${associationLabel}"
        emailAllowed="${emailAllowed}"
		resourcePool="${resourcePool}"
		defaultAssociation="${currentRole}"
-->
<jca:participantPicker 
        actionClass="com.ptc.xworks.promotion.builders.PromotionNoticeSetupParticipantsCommands" 
        actionMethod="addUserToRole" 
        participantType="${participantType}" 
        associationMap="${associationMap}" 
        associationLabel="${associationLabel}"
        emailAllowed="${emailAllowed}"
        singleParticipantType="true"
		defaultAssociation="${currentRole}"
>
</jca:participantPicker>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>    
   
