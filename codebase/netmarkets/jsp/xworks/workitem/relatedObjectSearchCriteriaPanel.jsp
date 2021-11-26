<%@page language="java" session="true" pageEncoding="UTF-8"%>
<%@page import="com.ptc.mvc.util.ClientMessageSource" %>
<%@page import="com.ptc.mvc.util.ResourceBundleClientMessageSource" %>
<%@page import="com.ptc.xworks.util.XWorksHelper" %>
<%@page import="wt.lifecycle.State" %>
<%@page import="com.ptc.xworks.workflow.relatedobject.UsedLifecycleState" %>
<%@page import="java.util.List" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components"	prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/core" prefix="wc"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="mvc" uri="http://www.ptc.com/windchill/taglib/mvc"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/picker" prefix="p"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>

<fmt:setLocale value="${localeBean.locale}" />
<fmt:setBundle basename="com.ptc.xworks.workflow.relatedobject.relatedObjectResource" />
<%!

private static final ClientMessageSource messageSource = new ResourceBundleClientMessageSource("com.ptc.xworks.workflow.relatedobject.relatedObjectResource");

private String getMessage(String key) {
	return messageSource.getMessage(key);
}
%>
<%
String msgContext = getMessage("search_context.description");
String msgSelectContext = getMessage("search_please_select_context");
String msgObjectType = getMessage("search_object_type");
String msgCreateBy = getMessage("search_createBy");
String msgModifyBy = getMessage("search_modifyBy");
String msgInputCriteria = getMessage("PLEASE_INPUT_SEARCH_CRITERIA");

UsedLifecycleState usedLifecycleState = XWorksHelper.getUsedLifecycleState();
List<State> states = usedLifecycleState.getUsedLifeCycleState();

List<String> defaultTypes = (List<String>) request.getAttribute("defaultTypes");
List<String> filterTypes = (List<String>) request.getAttribute("filterTypes");
List<String> seedTypes = (List<String>) request.getAttribute("seedTypes");
%>
<script type="text/javascript">

function clearSearchCriteriaCallback() {
	var search_number = document.getElementById("search_number");
	search_number.value = "";
	var search_name = document.getElementById("search_name");
	search_name.value = "";
	
	var search_state = document.getElementById("search_state");
	search_state.value = "";
	
	var search_revision = document.getElementById("search_revision");
	search_revision.value = "LATEST";
	
	var search_context_label = document.getElementById("search_context$label$");
	search_context_label.value = "";
	var search_context = document.getElementById("search_context");
	search_context.value = "";

	var search_createBy_label = document.getElementById("search_createBy$label$");
	search_createBy_label.value = "";
	var search_createBy = document.getElementById("search_createBy");
	search_createBy.value = "";	
	
	var search_modifyBy_label = document.getElementById("search_modifyBy$label$");
	search_modifyBy_label.value = "";
	var search_modifyBy = document.getElementById("search_modifyBy");
	search_modifyBy.value = "";
	
	var createOncreateOn_SearchDateSinceCombo = document.getElementById("createOncreateOn_SearchDateSinceCombo");
	createOncreateOn_SearchDateSinceCombo.value = "2";
	var createOncreateOn_SearchDateFromDate = document.getElementById("createOncreateOn_SearchDateFromDate");
	createOncreateOn_SearchDateFromDate.value = "";
	var createOncreateOn_SearchDateToDate = document.getElementById("createOncreateOn_SearchDateToDate");
	createOncreateOn_SearchDateToDate.value = "";
	getSelectDateType(createOncreateOn_SearchDateSinceCombo);
	
	var lastModifiedlastModified_SearchDateSinceCombo = document.getElementById("lastModifiedlastModified_SearchDateSinceCombo");
	lastModifiedlastModified_SearchDateSinceCombo.value = "2";
	var lastModifiedlastModified_SearchDateFromDate = document.getElementById("lastModifiedlastModified_SearchDateFromDate");
	lastModifiedlastModified_SearchDateFromDate.value = "";
	var lastModifiedlastModified_SearchDateToDate = document.getElementById("lastModifiedlastModified_SearchDateToDate");
	lastModifiedlastModified_SearchDateToDate.value = "";
	getSelectDateType(lastModifiedlastModified_SearchDateSinceCombo);
}

// 如果返回true表示查询条件符合要求
function validateSearchCriteriaCallback() {
	//console.log("start call validateSearchCriteriaCallback() ...");
	var search_number = document.getElementById("search_number");
	//console.log("search_number length" + search_number.value.length);
	var search_name = document.getElementById("search_name");
	var search_createBy = document.getElementById("search_createBy");
	var search_modifyBy = document.getElementById("search_modifyBy");
	
	// 如果没有输入查询条件，则提示用户输入
	if (search_number.value.length < 4 && search_name.value.length < 4 && search_createBy.value.length < 1 && search_modifyBy.value.length < 1) {
		//JCAAlert("");
		window.alert("<%=wt.util.HTMLEncoder.encodeForJavascript(msgInputCriteria)%>");
		return false;
	}
	return true;
}

//返回查询条件
function getSearchCriteriaCallback() {
	return {};
}

</script>

<input id="baseWhereClause" type="hidden" name="baseWhereClause" value="${baseWhereClause}" />

<table>
	<tr>
		<td style="width: 70pt"></td>
		<td></td>	
	</tr>
	<tr>
		<wctags:contextPicker id="search_context" label="<%=msgContext%>" pickerTitle="" />		
	</tr>
	<tr>
		<p:typePicker id="search_objectType" label="<%=msgObjectType%>" mode="SEARCH">
			<p:pickerParam name="pickerTextBoxLength"   value="24" />
			<p:pickerParam name="format"   value="tree" />
	        <p:pickerParam name="select"   value="${typeSelect}" />
			<p:pickerParam name="displayHierarchy" value="true" />
			<p:pickerParam name="showRoot" value="true" />
			<c:forEach var="seedType" items="${seedTypes}">
			<p:pickerParam name="seedType" value="${seedType}" />
			</c:forEach>
			<c:forEach var="defaultType" items="${defaultTypes}">
			<p:pickerParam name="defaultType" value="${defaultType}" />
			</c:forEach>
			<c:forEach var="filterType" items="${filterTypes}">
			<p:pickerParam name="filterType" value="${filterType}" />
			</c:forEach>
			<p:pickerParam name="type" value="BOTH" />
			<p:pickerParam name="showInstantiable" value="false" />
		</p:typePicker>	
	</tr>
	<tr>
		<td class="ppLabel"><b><fmt:message key="search_number" /></b></td>
		<td><input type="textbox" id="search_number" name="search_number" value="" size="25"/></td>
	</tr>
	<tr>
		<td class="ppLabel"><b><fmt:message key="search_name" /></b></td>
		<td><input type="textbox" id="search_name" name="search_name" value="" size="25"/></td>	
	</tr>
	
	<tr>
		<td class="ppLabel"><b><fmt:message key="search_state" /></b></td>
		<td>
			<select id="search_state" name="search_state">
				<option value="" selected="true">&nbsp;</option>
				<%for (State state : states) { %>
				<option value="<%=state.toString() %>" ><%=state.getLocalizedMessage(wt.session.SessionHelper.getLocale()) %></option>
				<%} %>
			</select>
		</td>
	</tr>
	<tr>
		<td class="ppLabel"><b><fmt:message key="search_revision" /></b></td>
		<td>
			<select id="search_revision" name="search_revision">
				<option value="LATEST" selected="true"><fmt:message key="search_revision_latest" /></option>
				<option value="ALL" ><fmt:message key="search_revision_all" /></option>
			</select>
		</td>
	</tr>
	<tr>
		<wctags:userPicker id="search_createBy" label="<%=msgCreateBy %>" />
	</tr>
	<tr>
		<td><b><fmt:message key="search_createOn" /></b></td>	
		<td><wctags:searchDate uiID="createOn" attrID="createOn" /></td>
	</tr>	
	<tr>		
		<wctags:userPicker id="search_modifyBy" label="<%=msgModifyBy %>" />
	</tr>
	<tr>
		<td><b><fmt:message key="search_lastModified" /></b></td>	
		<td><wctags:searchDate uiID="lastModified" attrID="lastModified" /></td>
	</tr>

</table>

