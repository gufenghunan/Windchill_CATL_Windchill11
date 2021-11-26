<%@ page language="java" session="true" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%><%@
include file="/netmarkets/jsp/components/includeWizBean.jspf"%><%@
page import="org.apache.commons.lang.StringUtils"%><%@
page import="com.ptc.xworks.windchill.util.ObjectTypeUtils" %><%@
page import="com.ptc.xworks.xmlobject.web.util.WebUtils" %><%@
page import="com.ptc.xworks.xmlobject.web.guifactory.guifactoryResource" %>
<%

%>
<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="com.ptc.xworks.xmlobject.web.guifactory.guifactoryResource"/>
<fmt:message var="titleOfWizard" key="SELECT_FOLDER" />

<c:set var="buttonList" value="NoStepsWizardButtons" />
<jca:wizard title="${titleOfWizard}" buttonList="DefaultWizardButtonsNoApply" wizardSelectedOnly="true">
   <jca:wizardStep action="folderPickerStep" type="xmlobject"/>
</jca:wizard>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>