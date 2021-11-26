<%@ taglib uri="http://www.ptc.com/windchill/taglib/jcaMvc" prefix="mvc"%>
<%
String componentId = (String) request.getAttribute("componentId");
if (componentId == null) {
	componentId = "attributePanel";
}
%>
<%@include file="/netmarkets/jsp/util/begin_comp.jspf"%>
<mvc:attributePanel compId="<%=componentId%>"/>
<%
String promotionNoticeFormJspPath = (String) request.getAttribute("promotionNoticeFormJspPath");
if (promotionNoticeFormJspPath != null) {
%><jsp:include page="<%=promotionNoticeFormJspPath%>" /><%
}%><%@ include file="/netmarkets/jsp/util/end_comp.jspf"%>
