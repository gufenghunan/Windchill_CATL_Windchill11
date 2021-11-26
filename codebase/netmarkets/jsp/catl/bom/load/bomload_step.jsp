<%@ page pageEncoding="UTF-8"%>
<%@page import="com.catl.bom.load.resource.BOMLoadRB"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<fmt:setBundle basename="com.catl.bom.load.resource.BOMLoadRB" />
<fmt:message var="loadFile" key="<%=BOMLoadRB.LOADFILE%>" />
<style>
.tb_btn td {
	padding-right:15px;
} 
.displayUserName:link {
	text-decoration:underline;
}
</style>


<div align="left">
<table width="95%" style="margin:0px;margin-left:5px;">
		<tr>
        	<td align="left" width="15%"></td>
			<td align="left" width="1%"></td>
			<td align="left" width="83%"></td>
        </tr>
			<tr>
			<td align="left">*${loadFile}:</td>
			<td align="left"></td>
			<td align="left"><input type="file" name="bomloadFile" id="bomloadFile"/></td>
		</tr>
</table>
</div>

<%@include file="/netmarkets/jsp/util/end.jspf"%>