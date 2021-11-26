<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components" %>
<%@taglib prefix="attachments" uri="http://www.ptc.com/windchill/taglib/attachments" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%> 
<%@ include file="/netmarkets/jsp/attachments/initAttachments.jspf"%>
<%@ include file="/netmarkets/jsp/attachments/initPTCAttachments.jspf"%>
<attachments:fileSelectionAndUploadApplet/>

<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="ext.ptc.xworks.examples.taskform.examplesResource"/>
<c:set var="buttonList" value="NoStepsWizardButtons" />
<c:choose>
	<c:when test="${param.actionName == 'createExampleXmlObject2'}">
		<fmt:message var="titleOfWizard" key="taskformExample.createExampleXmlObject2.title" />
	</c:when>
	<c:when test="${param.actionName == 'editExampleXmlObject2'}">
		<fmt:message var="titleOfWizard" key="taskformExample.editExampleXmlObject2.title" />
	</c:when>
	<c:when test="${param.actionName == 'viewExampleXmlObject2'}">
		<fmt:message var="titleOfWizard" key="taskformExample.viewExampleXmlObject2.title" />
		<c:set var="buttonList" value="WizardButtonClose" />
	</c:when>
	<c:otherwise>
		<c:set var="titleOfWizard" value="ERROR: Cannot found a title for Wizard" />
	</c:otherwise>
</c:choose>

<jca:wizard title="${titleOfWizard}" buttonList="${buttonList}">
	<jca:wizardStep action="setExampleXmlObject2AttributesStep" type="taskformExample"/>
</jca:wizard>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>