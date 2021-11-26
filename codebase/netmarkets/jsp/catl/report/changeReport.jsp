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
	document.getElementById("APPROVEDATE").value="";
	document.getElementById("APPROVEDATE_END").value="";
	document.getElementById("RELEASEDDATE").value="";
	document.getElementById("RELEASEDDATE_END").value="";
});

var xmlHttp; 
var targetSelId; 
var conditionId;

var main_form = document.getElementById("mainform");

function doSubmit()
{	
	//document.getElementById("btn").disabled=true;
	var ecrNumber = document.getElementById("ECRNUMBER").value;
	if(ecrNumber == null || ecrNumber == ''){
		alert("DCN/ECN不能为空");
		return;
	}else if(ecrNumber.length == 4 && ecrNumber.indexOf('CN') != 0){
		alert("DCN/ECN输入4位必须cn开头，如CN16、CN17");
		return;
	}else if(ecrNumber.length < 4){
		alert("DCN/ECN长度不能小于4位");
		return;
	}
	var status = document.getElementById("STATUS").value;
	var createDateFrom = document.getElementById("CREATEDATE").value;
	var createDateTo = document.getElementById("CREATEDATE_END").value;
	var approveDateFrom = document.getElementById("APPROVEDATE").value;
	var approveDateTo = document.getElementById("APPROVEDATE_END").value;
	var releasedDateFrom = document.getElementById("RELEASEDDATE").value;
	var releasedDateTo = document.getElementById("RELEASEDDATE_END").value;
	
	var url = "<%=request.getContextPath()%>/netmarkets/jsp/catl/report/changeReportDownload.jsp?ecrNumber="+ecrNumber+"&status="+status
			+"&createDateFrom="+createDateFrom+"&createDateTo="+createDateTo
			+"&approveDateFrom="+approveDateFrom+"&approveDateTo="+approveDateTo
			+"&releasedDateFrom="+releasedDateFrom+"&releasedDateTo="+releasedDateTo;
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
		<td height="30" class="STYLE3" nowrap="nowrap">变更情况报表</td>
	</tr>
</table>
<table id="selectTable">
<tr>
		<td><font>DCN/ECN单号:</font></td>
		<td><input type="text" name="ECRNUMBER" id="ECRNUMBER"></td>
</tr>
<tr>
		<td><font>DCN/ECN状态:</font></td>
		<td>
			<select name="STATUS" id="STATUS">
				<option id="*" value="*"></option>
				<option id="OPEN" value="OPEN">开启</option>
				<option id="UNDERREVIEW" value="UNDERREVIEW">正在审阅</option>
				<option id="EVALUATION" value="EVALUATION">评估</option>
				<option id="IMPLEMENTATION" value="IMPLEMENTATION">实施</option>
				<option id="RESOLVED" value="RESOLVED">已解决</option>
				<option id="REWORK" value="REWORK">返工</option>
			</select>
		</td>
</tr>
<tr>
	<td><label for=""><font>创建日期:</font></label></td>
	<td>
	<span>			
			<div id="div_createdate" style="display: inline;">
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
			<div id="div_approvedate" style="display: inline;">
				<label for="">从:</label>
				<w:dateInputComponent name="APPROVEDATE" id="APPROVEDATE" required="true" dateValueType="DATE_ONLY" />
				<label for="">到:</label>
				<w:dateInputComponent name="APPROVEDATE_END" id="APPROVEDATE_END" required="true" dateValueType="DATE_ONLY" />
			</div>
		</span>
	</td>
</tr>
<tr>
	<td><label for=""><font>数据发布日期（传输ERP的时间）:</font></label></td>
	<td>
	<span>			
			<div id="div_releaseddate" style="display: inline;">
				<label for="">从:</label>
				<w:dateInputComponent name="RELEASEDDATE" id="RELEASEDDATE" required="true" dateValueType="DATE_ONLY" />
				<label for="">到:</label>
				<w:dateInputComponent name="RELEASEDDATE_END" id="RELEASEDDATE_END" required="true" dateValueType="DATE_ONLY" />
			</div>
		</span>
	</td>
</tr>
<td>
	<Input type="button" id="btn" value="导出报表" onclick="doSubmit();"/>
</td>
</tr>
</table>
</div>
<iframe id="exportFrame" name="exportFrame" height="0" width="0" ></iframe>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>