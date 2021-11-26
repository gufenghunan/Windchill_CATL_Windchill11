<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@page import="wt.workflow.work.WfAssignmentState"%>
<%@page import="wt.fc.WTObject"%>
<%@page import="java.util.List"%>
<%@page import="ext.ptc.xworks.examples.taskform.ExampleApplicationForm1" %>
<%@page import="ext.ptc.xworks.examples.taskform.PromotionRequestDemoProcess1" %>
<%@page import="com.ptc.netmarkets.model.NmOid"%>
<%@page import="com.ptc.xworks.xmlobject.web.LayoutForView"%>
<%@page import="com.ptc.xworks.windchill.util.NetmarketsUtils"%>

<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ include file="/netmarkets/jsp/attachments/initAttachments.jspf"%>
<%@ include file="/netmarkets/jsp/xworks/workitem/initPTCAttachments.jspf"%>

<!-- attachments:fileSelectionAndUploadApplet/-->
<jsp:useBean id="commandBean" class="com.ptc.netmarkets.util.beans.NmCommandBean" scope="request"/>

<!-- 使用下面这个FormProcessorDelegate来保存ExampleApplicationForm1 -->
<input type="hidden" name="FormProcessorDelegate" value="com.ptc.xworks.xmlobject.web.form.XmlObjectExtractFormProcessorDelegate" />
<input type="hidden" name="FormProcessorDelegate" value="com.ptc.xworks.xmlobject.web.form.GetContentHolderFormProcessorDelegate" />
<input type="hidden" name="FormProcessorDelegate" value="ext.ptc.xworks.examples.taskform.ExampleApplicationForm1CreateFormProcessorDelegate" />
<jsp:include page="${mvc:getComponentURL('ext.ptc.xworks.examples.taskform.ExampleApplicationForm1AttributePanelBuilder')}">
	<jsp:param name="operationType" value="CREATE" />
	<jsp:param name="contextObjectClassName" value="ext.ptc.xworks.examples.taskform.ExampleApplicationForm1"/>
	<jsp:param name="componentId" value="ext.ptc.xworks.examples.taskform.ExampleApplicationForm1AttributePanelBuilder"/>
</jsp:include>