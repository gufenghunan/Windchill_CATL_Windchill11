<%@page import="wt.httpgw.URLFactory"%>
<%@page language="java" session="true" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/core" prefix="wc"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="mvc"  uri="http://www.ptc.com/windchill/taglib/mvc"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<fmt:setLocale value="${localeBean.locale}" />
<fmt:setBundle basename="com.ptc.xworks.workflow.relatedobject.relatedObjectResource" />
<fmt:message var="title" key="add_related_objects"/>

<jca:wizard title="${title}">
	<jca:wizardStep action="addRelatedObjectStep" type="workitem" />
</jca:wizard>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>