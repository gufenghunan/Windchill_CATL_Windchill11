<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ page import="com.ptc.netmarkets.util.beans.NmCommandBean" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt" %>
<%@ page import="ext.ptc.xworks.examples.taskform.examplesResource" %>

<fmt:setBundle basename="ext.ptc.xworks.examples.taskform.examplesResource" />
<fmt:message var="tableLabel" key="<%= examplesResource.taskformExample_itemPickerExample1_description%>" />

<%
String tableLabel = (String) pageContext.findAttribute("tableLabel");
request.setAttribute("tableLabel",tableLabel); 
%>
<wctags:itemPicker id="itemPickerExample1"
		multiSelect="true"
		inline="true"
		componentId="pickerSearch"
		objectType="wt.doc.WTDocument"
		singleSelectTypePicker="false"
        pickerCallback="doNothing"
        typePickerDefaultType="wt.doc.WTDocument,WCTYPE|wt.part.WTPart|com.ptc.xworks.TestPart|com.ptc.xworks.TestPart01,WCTYPE|wt.part.WTPart|com.ptc.xworks.TestPart|com.ptc.xworks.TestPart02"
        pickerTitle="${tableLabel}"
        baseWhereClause="()" />

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
