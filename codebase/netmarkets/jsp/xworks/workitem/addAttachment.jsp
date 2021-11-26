<%@page import="java.util.ResourceBundle"%>
<%@page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>
<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>

<fmt:setLocale value="${commandBean.getLocale()}" />
<fmt:setBundle basename="com.ptc.xworks.workflow.attachment.attachmentGroupResource"/>
<fmt:message var="wizardLabel" key="addattach_desp.description" />
<jca:wizard  title="${wizardLabel}" buttonList="DefaultWizardButtonsNoApply">
	<jca:wizardStep action="addFileAttachmentStep" type="workitem" label="${wizardLabel}" />
</jca:wizard>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
