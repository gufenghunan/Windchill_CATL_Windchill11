<%@ taglib uri="http://www.ptc.com/windchill/taglib/jcaMvc" prefix="mvc"%>
<%
//System.out.println(request.getAttribute("componentId"));
String componentId = (String) request.getAttribute("componentId");
if (componentId == null) {
	componentId = "attributePanel";
}
%>
<%@include file="/netmarkets/jsp/util/begin_comp.jspf"%>
<mvc:attributePanel compId="<%=componentId%>"/>
<%@ include file="/netmarkets/jsp/util/end_comp.jspf"%>
