<%@page import="wt.util.WTMessage"%>
<%@taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@include file="/netmarkets/jsp/components/beginWizard.jspf"%>

<%@include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@ page language="Java" pageEncoding="UTF-8"%>
<%@page import="com.ptc.core.components.util.RequestHelper"%>
<%@page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>

<jca:wizard title="新建售后再利用件"  buttonList="DefaultWizardButtonsNoApply" wizardSelectedOnly="true">
   <jca:wizardStep action="newaftersalefirststep" type="require"/>
</jca:wizard>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>