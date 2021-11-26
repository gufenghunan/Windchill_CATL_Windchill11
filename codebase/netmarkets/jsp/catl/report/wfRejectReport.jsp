<%@ page pageEncoding="UTF-8"%>
<%@page import="org.apache.commons.beanutils.BeanComparator"%>
<%@page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>
<%@page import="wt.util.WTProperties"%>
<%@page import="wt.httpgw.URLFactory"%>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>
<%
	URLFactory urlFactory = new URLFactory();
	String strBaseURL = urlFactory.getBaseHREF();
	String contentType = request.getContentType();
%>

<script type="text/javascript">

Ext.onReady(function() {
	document.getElementById("REJECTDATEDDATE").value="";
	document.getElementById("REJECTDATEDDATE_END").value="";
});

var xmlHttp; 
var targetSelId; 
var conditionId;

var main_form = document.getElementById("mainform");

function doSubmit()
{	
	var processName = document.getElementById("PROCESSNAME").value;
	var rejectDateFrom = document.getElementById("REJECTDATEDDATE").value;
	var rejectDateTo = document.getElementById("REJECTDATEDDATE_END").value;
	
	var url = "<%=request.getContextPath()%>/netmarkets/jsp/catl/report/wfRejectReportDownload.jsp?processName="+processName+"&rejectDateFrom="+rejectDateFrom+"&rejectDateTo="+rejectDateTo;
	document.forms[0].enctype = "application/x-www-form-urlencoded";
    document.forms[0].encoding = "application/x-www-form-urlencoded";

	document.forms[0].action = url;
	document.forms[0].target = exportFrame;
	document.forms[0].submit();
}

</script>
<style type="text/css">
<!--
table {
	font-size: 14px;
}

font {
	font-weight: bolder;
}
.STYLE3 {
	color: #000000;
	font-size: 20px;
	font-weight: bold;
}
--> 
</style>
<div style="margin-left: 20px;">
<table width="500px">
	<tr>
		<td height="30" class="STYLE3" nowrap="nowrap">每月流程驳回报表</td>
	</tr>
</table>
<table id="wfRejectTable">
<tr>
	<td><font>路由名称:</font></td>
	<td><input type="text" name="PROCESSNAME" id="PROCESSNAME"></td>
</tr>

<tr>
	<td><label for=""><font>驳回日期:</font></label></td>
	<td>
		<span>			
			<div id="reject_daterange" style="display: inline;">
				<label for="">从:</label>
				<w:dateInputComponent name="REJECTDATEDDATE" id="REJECTDATEDDATE" required="true" dateValueType="DATE_ONLY" />
				<label for="">到:</label>
				<w:dateInputComponent name="REJECTDATEDDATE_END" id="REJECTDATEDDATE_END" required="true" dateValueType="DATE_ONLY" />
			</div>
		</span>
	</td>
</tr>
<td></td>
<td>

<Input type="button" value="导出报表" onclick="doSubmit();"/>

</td>
</tr>
</table>
</div>
<iframe id="exportFrame" name="exportFrame" height="0" width="0" ></iframe>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>