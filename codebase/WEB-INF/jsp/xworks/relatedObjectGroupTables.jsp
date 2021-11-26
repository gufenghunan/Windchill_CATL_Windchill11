<%@ taglib uri="http://www.ptc.com/windchill/taglib/jcaMvc" prefix="mvc"%>
<%@page import="java.util.List"%>
<%@include file="/netmarkets/jsp/util/begin_comp.jspf"%>
<%
List<String> allTableId = (List<String>) request.getAttribute("allTableId");
for (String tableId : allTableId) {
%>
	<mvc:table setPageTitle="true" compId="<%=tableId%>"/>
<%
}
%>
<%@ include file="/netmarkets/jsp/util/end_comp.jspf"%>
