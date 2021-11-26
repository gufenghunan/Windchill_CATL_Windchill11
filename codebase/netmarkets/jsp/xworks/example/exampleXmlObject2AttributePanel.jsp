<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%
String operationType = "VIEW";
String actionName = request.getParameter("actionName");
if ("createExampleXmlObject2".equals(actionName)) {
	operationType = "CREATE";
}
if ("editExampleXmlObject2".equals(actionName)) {
	operationType = "EDIT";
}
String oid = request.getParameter("oid");
%>
<input type="hidden" name="oid" value="<%=oid%>" />
<input type="hidden" name="ContentHolder" value="<%=StringUtils.trimToEmpty(request.getParameter("ContentHolder"))%>" />
<input type="hidden" name="FormProcessorDelegate" value="com.ptc.xworks.xmlobject.web.form.XmlObjectExtractFormProcessorDelegate" />
<input type="hidden" name="FormProcessorDelegate" value="com.ptc.xworks.xmlobject.web.form.GetContentHolderFormProcessorDelegate" />
<%if ("CREATE".equals(operationType)) {%>
	<input type="hidden" name="FormProcessorDelegate" value="ext.ptc.xworks.examples.taskform.ExampleXmlObject2CreateDelegate" />
<%} %>
<%if ("EDIT".equals(operationType)) {%>
	<input type="hidden" name="FormProcessorDelegate" value="ext.ptc.xworks.examples.taskform.ExampleXmlObject2EditDelegate" />
<%} %>
<jsp:include page="${mvc:getComponentURL('ext.ptc.xworks.examples.taskform.ExampleXmlObject2AttributePanelBuilder')}">
	<jsp:param name="operationType" value="<%=operationType%>" />
	<jsp:param name="xmlObjectOid" value="<%=oid%>" />
	<jsp:param name="contextObjectClassName" value="ext.ptc.xworks.examples.taskform.ExampleXmlObject2" />
</jsp:include>


<%@ include file="/netmarkets/jsp/util/end.jspf"%>