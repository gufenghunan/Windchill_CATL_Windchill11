<%@ taglib uri="http://www.ptc.com/windchill/taglib/jcaMvc" prefix="mvc"%>
<%
//System.out.println(request.getAttribute("componentId"));
String componentId = (String) request.getAttribute("componentId");
if (componentId == null) {
	componentId = "table";
}
%>
<%@include file="/netmarkets/jsp/util/begin_comp.jspf"%>
<mvc:table setPageTitle="true" compId="<%=componentId%>"/>
<%@ include file="/netmarkets/jsp/util/end_comp.jspf"%>
