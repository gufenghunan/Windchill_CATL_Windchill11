<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="attachments" uri="http://www.ptc.com/windchill/taglib/attachments" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%> 
<%@ include file="/netmarkets/jsp/attachments/initAttachments.jspf"%>
<%@ include file="/netmarkets/jsp/attachments/initPTCAttachments.jspf"%>
<attachments:fileSelectionAndUploadApplet/>

<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="ext.ptc.xworks.examples.taskform.examplesResource"/>
<c:set var="buttonList" value="NoStepsWizardButtons" />
<c:choose>
	<c:when test="${param.actionName == 'createExampleXmlObject1'}">
		<fmt:message var="titleOfWizard" key="taskformExample.wizard.title.create" />
	</c:when>
	<c:when test="${param.actionName == 'editExampleXmlObject1'}">
		<fmt:message var="titleOfWizard" key="taskformExample.wizard.title.edit" />
	</c:when>
	<c:when test="${param.actionName == 'viewExampleXmlObject1'}">
		<fmt:message var="titleOfWizard" key="taskformExample.wizard.title.view" />
		<c:set var="buttonList" value="WizardButtonClose" />
	</c:when>
	<c:otherwise>
		<c:set var="titleOfWizard" value="ERROR: Cannot found a title for Wizard" />
	</c:otherwise>
</c:choose>

<jca:wizard title="${titleOfWizard}" buttonList="${buttonList}">
	<jca:wizardStep action="setExampleXmlObject1AttributesStep" type="taskformExample"/>
</jca:wizard>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>