<%@page language="java" session="true" pageEncoding="UTF-8"%>
<%@page import="java.util.List,java.util.ArrayList" %>
<%@page import="com.ptc.xworks.util.XWorksHelper" %>
<%@page import="com.ptc.xworks.util.ObjectUtils" %>
<%@page import="com.ptc.xworks.workflow.annotation.SupportedType"%>
<%@page import="com.ptc.xworks.xmlobject.web.util.WebUtils" %>
<%@page import="com.ptc.xworks.workflow.relatedobject.AddRelatedObjectSearchResultTableBuilder" %>
<%@page import="com.ptc.xworks.workflow.ObjectSearchingConfigViewResolver" %>
<%@page import="com.ptc.xworks.workflow.ObjectSearchingConfigView" %>
<%@page import="org.apache.commons.lang.StringUtils" %>
<%@page import="com.ptc.xworks.windchill.util.ObjectTypeUtils" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/core" prefix="wc"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.ptc.com/infoengine/taglib/core" prefix="ie" %>
<%@ taglib prefix="mvc"  uri="http://www.ptc.com/windchill/taglib/mvc"%>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>

<ie:getService varName="vdb"/>
<c:set var="formGroup" value="${formGroup}" scope="request" />
<fmt:setLocale value="${localeBean.locale}" />
<fmt:setBundle basename="com.ptc.xworks.workflow.relatedobject.relatedObjectResource" />

<%
String resolverClass = request.getParameter("resolverClass");
if (StringUtils.isBlank(resolverClass)) {
	%>Parameter "resolverClass" (a concrete class of com.ptc.xworks.workflow.ObjectSearchingConfigViewResolver) not specified!<%
	return;
}
ObjectSearchingConfigViewResolver resovler = (ObjectSearchingConfigViewResolver) ObjectUtils.createNewInstance(resolverClass);
ObjectSearchingConfigView configView = resovler.getObjectSearchingConfigView(commandBean);
String searchCriteriaPanelJsp = configView.searchCriteriaJspPath();

List<String> objectTypes = new ArrayList<String>(); // 允许查询并添加的对象类型
List<String> defaultTypes = new ArrayList<String>(); // Type Picker默认选中的类型
for (String defaultType : configView.defaultTypes()) {
	defaultTypes.add(defaultType);
}
List<String> filterTypes = new ArrayList<String>();//需要在Type Picker中过滤排除掉的子类型
for (SupportedType type : configView.supportedTypes()) {
	objectTypes.add(type.type());
	if (!type.includeSubType()) {
		filterTypes.addAll(ObjectTypeUtils.getSubTypes(type.type(), true, true));
	}
}

if (objectTypes.isEmpty()) {
	%>ERROR: No any SupportedType is configured!<%
	return;
}
String baseWhereClause = configView.baseWhereClause();
request.setAttribute("baseWhereClause", baseWhereClause); // 在annotation中配置的InfoEngine查询条件
request.setAttribute("seedTypes", objectTypes);// Type Picker中允许选择的对象类型;
request.setAttribute("defaultTypes", defaultTypes);// Type Picker 默认选中的对象类型
request.setAttribute("filterTypes", filterTypes);// Type Picker中需要排除掉的对象类型;

String typeSelect = "single"; // Type Picker是否允许选择多个对象类型
if (configView.multiObjectType()) {
	typeSelect = "multi";
	//typeSelect = "single";
}
request.setAttribute("typeSelect", typeSelect);

String searchResultTableId = AddRelatedObjectSearchResultTableBuilder.TABLE_ID;
String searchResultTableIdSuffix = resovler.getSearchingResultTableIdSuffix(commandBean);

%>
<script type="text/javascript">

function clearSearchCriteriaCallback() {
	
}

// 如果返回true表示查询条件符合要求
function validateSearchCriteriaCallback() {
	return true;
}

//返回查询条件
function getSearchCriteriaCallback() {
	return {};
}

</script>

<input type="hidden" name="resolverClass" value="<%=resolverClass%>" />

<%
//if (!groupDef.instruction().key().equals("NONE")) {
//    String instruction = wt.util.WTMessage.getLocalizedMessage(groupDef.instruction().resourceBundle(), groupDef.instruction().key());
if (configView.instruction() != null) {
    String instruction = wt.util.WTMessage.getLocalizedMessage(configView.instruction().resourceBundle(), configView.instruction().key());
    if (StringUtils.isNotBlank(instruction)) {
%>
<div style="padding: 0pt 8px;">
	<div class="inlineHelpBox">
		<table>
			<tr>
				<td><img src="wtcore/images/tip.gif" border="0"></td>
				<td><%=instruction%></td>
			</tr>
		</table>
	</div>
</div>
<%
    }
}
%>
                      
<fieldset class="x-fieldset x-form-label-left" id="SearchCretriaPanel" style="margin:10px 5px 15px 10px;">
	<legend><fmt:message key="search_criteria.description"/></legend>
	
	<!-- 定制的搜索条件内容从这里开始  -->
	
	<jsp:include page="<%=searchCriteriaPanelJsp%>">
		<jsp:param name="defaultType" value="${defaultType}"/>
		<jsp:param name="filterType" value="${filterType}"/>
		<jsp:param name="seedType" value="${seedType}"/>
	</jsp:include>
	
	<!-- 定制的搜索条件内容从这里结束  -->
	<table width=100%>
		<tr>
			<td align="right" width=100%>
				<fmt:message var="button_search_description" key="button_search.description"/>
				<input type="button" id="button_search" name="button_search" value="&nbsp;${button_search_description}&nbsp;" onClick="doObjectSearching()"/>
				<fmt:message var="button_clear_description" key="button_clear.description"/>
				<input type="button" id="button_clear" name="button_clear" value="&nbsp;&nbsp;${button_clear_description}&nbsp;&nbsp;" onClick="clearSearchCriteriaCallback()"/>
			</td>
		</tr>
	</table>

</fieldset>

<script type="text/javascript">
function doObjectSearching() {
	if (!validateSearchCriteriaCallback()) {
		return;
	}
	
	var params = getSearchCriteriaCallback();
	
	params["doSearch"] = "true";
	
	var tableID = "<%=searchResultTableId%>|<%=searchResultTableIdSuffix%>";
	PTC.jca.table.Utils.reload(tableID, params, true);
}
</script>

<div id="searchResultTable">
	<jsp:include page="${mvc:getComponentURL('com.ptc.xworks.workflow.relatedobject.AddRelatedObjectSearchResultTableBuilder')}" />
</div>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>