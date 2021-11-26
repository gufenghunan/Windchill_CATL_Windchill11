<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="com.catl.bom.load.resource.BOMLoadRB"/>
<fmt:message var="TITLE_LOAD"  key="LOAD_SINGLE_LEVEL_BOM"/>
<jca:wizard title="${TITLE_LOAD}" buttonList="DefaultWizardButtonsNoApply" >
    <jca:wizardStep action="loadBOM_step" type="bomload"/>
</jca:wizard> 
<%@include file="/netmarkets/jsp/util/end.jspf"%>