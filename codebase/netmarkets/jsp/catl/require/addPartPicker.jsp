<%@ page language="java" session="true" pageEncoding="UTF-8"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>
<wctags:itemPicker id="AddParts" multiSelect="true" inline="true" 
	pickerTitle="零部件查询" 
	pickerCallback="addData" 
	componentId="impactedPicker"
	objectType="wt.part.WTPart|com.CATLBattery.CATLPart"
	 />
<%@include file="/netmarkets/jsp/util/end.jspf" %>