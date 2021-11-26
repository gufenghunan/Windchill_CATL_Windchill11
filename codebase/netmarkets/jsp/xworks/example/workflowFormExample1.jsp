<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@page import="wt.workflow.work.WfAssignmentState"%>
<%@page import="wt.fc.WTObject"%>
<%@page import="java.util.List"%>
<%@page import="ext.ptc.xworks.examples.taskform.ExampleApplicationForm1" %>
<%@page import="ext.ptc.xworks.examples.taskform.PromotionRequestDemoProcess1" %>
<%@page import="wt.workflow.work.WorkItem"%>
<%@page import="com.ptc.netmarkets.model.NmOid"%>
<%@page import="com.ptc.xworks.xmlobject.web.LayoutForView"%>

<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="attachments" uri="http://www.ptc.com/windchill/taglib/attachments" %>

<!-- attachments:fileSelectionAndUploadApplet/-->
<jsp:useBean id="commandBean" class="com.ptc.netmarkets.util.beans.NmCommandBean" scope="request"/>
<%
Object obj=null;
if(commandBean.getPageOid()!=null) {
	obj = commandBean.getPageOid().getRefObject();
} else {
	String oid = request.getParameter("oid");
	if (oid != null) {
		obj = NmOid.newNmOid(oid).getRefObject();
	}
}

Object pbo = obj;
List<ExampleApplicationForm1> result = PromotionRequestDemoProcess1.getExampleApplicationForm1((WTObject) pbo);
if (result.size() > 0) {
	ExampleApplicationForm1 applicationForm = result.get(0);
	
	String viewOnly = "true";
	String operationType = "VIEW";
	
	LayoutForView layoutForExampleXmlObjectTable = new LayoutForView();
	String contextPath = applicationForm.getIdentifier().toString() + "~exampleXmlObjects1";
%>
	<c:set var="xmlObject" value="<%=applicationForm%>" scope="request" />
	<jsp:include page="${mvc:getComponentURL('ext.ptc.xworks.examples.taskform.ExampleApplicationForm1AttributePanelBuilder')}">
		<jsp:param name="operationType" value="<%=operationType%>" />
		<jsp:param name="contextObjectClassName" value="ext.ptc.xworks.examples.taskform.ExampleApplicationForm1"/>
		<jsp:param name="componentId" value="ext.ptc.xworks.examples.taskform.ExampleApplicationForm1AttributePanelBuilder.inlineTaskformExample1"/>
	</jsp:include>
			
	<!-- componentId的设置,可能导致动态刷新(创建时)出现问题, componentId与ComponentBuilder中设置一致时不会出问题,不一致时，经测试，使用下划线可以正常运行 -->
	<jsp:include page="${mvc:getComponentURL('ext.ptc.xworks.examples.taskform.ExampleXmlObject1TableBuilder')}">
		<jsp:param name="operationType" value="VIEW" />
		<jsp:param name="componentId" value="ext.ptc.xworks.examples.taskform.ExampleXmlObject1TableBuilder"/>
		<jsp:param name="contextPath" value="<%=contextPath%>"/>
		<jsp:param name="applicationFormOid" value="<%=applicationForm.getIdentifier().toString() %>"/>
		<jsp:param name="viewOnly" value="<%=viewOnly%>"/>
		<jsp:param name="contextObjectClassName" value="ext.ptc.xworks.examples.taskform.ExampleXmlObject1" />
		<jsp:param name="layoutForView" value="<%=layoutForExampleXmlObjectTable %>" />
	</jsp:include>
<%
}
%>


