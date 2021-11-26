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
	document.getElementById("CREATEDATE").value="";
	document.getElementById("CREATEDATE_END").value="";
	document.getElementById("APPROVEDDATE").value="";
	document.getElementById("APPROVEDDATE_END").value="";
});

var xmlHttp; 
var targetSelId; 
var conditionId;

var main_form = document.getElementById("mainform");

function doSubmit()
{	
	var ecrNumber = document.getElementById("ECRNUMBER").value;
	var status = document.getElementById("STATUS").value;
	var createDateFrom = document.getElementById("CREATEDATE").value;
	var createDateTo = document.getElementById("CREATEDATE_END").value;
	var approvedDateFrom = document.getElementById("APPROVEDDATE").value;
	var approvedDateTo = document.getElementById("APPROVEDDATE_END").value;
	var user = document.getElementById("USER").value;
	
	var url = "<%=request.getContextPath()%>/netmarkets/jsp/catl/report/ecrReportDownload.jsp?ecrNumber="+ecrNumber+"&status="+status+"&createDateFrom="+createDateFrom+"&createDateTo="+createDateTo+"&approvedDateFrom="+approvedDateFrom+"&approvedDateTo="+approvedDateTo+"&user="+user;
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
		<td height="30" class="STYLE3" nowrap="nowrap">PLM ECR ECN ECA统计报告</td>
	</tr>
</table>
<table id="selectTable">
<tr>
		<td><font>ECR 单号:</font></td>
		<td><input type="text" name="ECRNUMBER" id="ECRNUMBER"></td>
</tr>
<tr>
		<td><font>变更状态:</font></td>
		<td>
			<select name="STATUS" id="STATUS">
				<option id="*" value="*"></option>
				<option id="OPEN" value="OPEN">开启</option>
				<option id="UNDERREVIEW" value="UNDERREVIEW">正在审阅</option>
				<option id="IMPLEMENTATION" value="IMPLEMENTATION">实施</option>
				<option id="RESOLVED" value="RESOLVED">已解决</option>
				<option id="CANCELLED" value="CANCELLED">已取消</option>
				<option id="REWORK" value="REWORK">返工</option>
			</select>
		</td>
</tr>
 
<tr>
	<td><label for=""><font>申请日期:</font></label></td>
	<td>
	<span>			
			<div id="dateRangedateSince_id1" style="display: inline;">
				<label for="">从:</label>
				<w:dateInputComponent name="CREATEDATE" id="CREATEDATE" required="true" dateValueType="DATE_ONLY" />
				<label for="">到:</label>
				<w:dateInputComponent name="CREATEDATE_END" id="CREATEDATE_END" required="true" dateValueType="DATE_ONLY" />
			</div>
		</span>
	</td>
</tr>
<tr>
	<td><label for=""><font>批准日期:</font></label></td>
	<td>
	<span>			
			<div id="dateRangedateSince_id2" style="display: inline;">
				<label for="">从:</label>
				<w:dateInputComponent name="APPROVEDDATE" id="APPROVEDDATE" required="true" dateValueType="DATE_ONLY" />
				<label for="">到:</label>
				<w:dateInputComponent name="APPROVEDDATE_END" id="APPROVEDDATE_END" required="true" dateValueType="DATE_ONLY" />
			</div>
		</span>
	</td>
</tr>
<tr>

			<wctags:userPicker id="USER" pickerTitle="申请人" label="<font>申请人:</font>" multiSelect="false" displayAttribute="name" showUserType="ActiveOnly" readOnlyPickerTextBox="true"/>

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