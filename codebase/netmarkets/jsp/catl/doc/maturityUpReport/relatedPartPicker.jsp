<%@ page language="java" session="true" pageEncoding="UTF-8"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>
<wctags:itemPicker id="maturityUpReportAddParts" multiSelect="true" inline="true" 
	pickerTitle="关联零部件查询" 
	pickerCallback="doNothing" 
	componentId="impactedPicker"
	objectType="wt.part.WTPart|com.CATLBattery.CATLPart" />
<%@include file="/netmarkets/jsp/util/end.jspf" %>