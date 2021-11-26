<%@page language="java" session="true" pageEncoding="UTF-8"%>
<%@page import="java.util.List,java.util.ArrayList" %>
<%@page import="com.ptc.xworks.util.XWorksHelper" %>
<%@page import="com.ptc.xworks.util.ObjectUtils" %>
<%@page import="com.ptc.xworks.xmlobject.web.util.WebUtils" %>
<%@page import="org.apache.commons.lang.StringUtils" %>
<%@page import="com.ptc.xworks.windchill.util.ObjectTypeUtils" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/core" prefix="wc"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="mvc"  uri="http://www.ptc.com/windchill/taglib/mvc"%>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>

<fmt:setLocale value="${localeBean.locale}" />
<fmt:setBundle basename="com.ptc.xworks.workflow.relatedobject.relatedObjectResource" />


<div id="searchResultTable">
	<jsp:include page="${mvc:getComponentURL('com.ptc.xworks.workflow.relatedobject.AnnotateRelatedObjectTableBuilder')}" />
</div>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
<script type="text/javascript">
function refreshOpenerRelatedObjectGroupTable(groupId, workItemOid, tableId) {
	if (window.opener) {
		window.opener.reloadRelatedObjectGroupTable(groupId, workItemOid, tableId);
	}
	window.close();
}
</script>