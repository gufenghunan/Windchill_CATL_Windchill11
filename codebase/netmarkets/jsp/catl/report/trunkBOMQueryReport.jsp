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
	document.getElementById("PARTNUMBER_1").value="";
	document.getElementById("PARTNUMBER_2").value="";
	document.getElementById("PARTNUMBER_3").value="";
	document.getElementById("PARTNUMBER_4").value="";
	document.getElementById("PARTNUMBER_5").value="";
	document.getElementById("PARTNUMBER_6").value="";
	document.getElementById("PARTNUMBER_7").value="";
	document.getElementById("PARTNUMBER_8").value="";
	document.getElementById("PARTNUMBER_9").value="";
	document.getElementById("PARTNUMBER_10").value="";
});

var xmlHttp; 
var targetSelId; 
var conditionId;

var main_form = document.getElementById("mainform");

function doSubmit()
{	
	
	var PARTNUMBER_1 = document.getElementById("PARTNUMBER_1").value;
	var PARTNUMBER_2 = document.getElementById("PARTNUMBER_2").value;
	var PARTNUMBER_3 = document.getElementById("PARTNUMBER_3").value;
	var PARTNUMBER_4 = document.getElementById("PARTNUMBER_4").value;
	var PARTNUMBER_5 = document.getElementById("PARTNUMBER_5").value;
	var PARTNUMBER_6 = document.getElementById("PARTNUMBER_6").value;
	var PARTNUMBER_7 = document.getElementById("PARTNUMBER_7").value;
	var PARTNUMBER_8 = document.getElementById("PARTNUMBER_8").value;
	var PARTNUMBER_9 = document.getElementById("PARTNUMBER_9").value;
	var PARTNUMBER_10 = document.getElementById("PARTNUMBER_10").value;
	
	PARTNUMBER_1 = PARTNUMBER_1.trim();
	PARTNUMBER_2 = PARTNUMBER_2.trim();
	PARTNUMBER_3 = PARTNUMBER_3.trim();
	PARTNUMBER_4 = PARTNUMBER_4.trim();
	PARTNUMBER_5 = PARTNUMBER_5.trim();
	PARTNUMBER_6 = PARTNUMBER_6.trim();
	PARTNUMBER_7 = PARTNUMBER_7.trim();
	PARTNUMBER_8 = PARTNUMBER_8.trim();
	PARTNUMBER_9 = PARTNUMBER_9.trim();
	PARTNUMBER_10 = PARTNUMBER_10.trim();
	
	if (PARTNUMBER_1 == ''){
		alert("PN_Level 1为必输！");
		return;
	}
	
	var vars = PARTNUMBER_1.split(","); 

    if (vars.length > 200) {
    	alert("PN_Level 1 不能超过200个PN！");
		return;
    }
    
	if (PARTNUMBER_2.lastIndexOf("*") > -1){
		PARTNUMBER_2 = PARTNUMBER_2.substring (0, PARTNUMBER_2.length-1);
	}
	if (PARTNUMBER_2 == '' || PARTNUMBER_2.length <2){
		alert("PN_Level 2为必输且至少输入2位！");
		return;
	}
	
	for (var i=3; i<=10; i++){
		
		var PARTNUMBER = document.getElementById("PARTNUMBER_"+i).value;
		PARTNUMBER = PARTNUMBER.trim();
		if (PARTNUMBER.lastIndexOf("*") > -1){
			PARTNUMBER = PARTNUMBER.substring (0, PARTNUMBER.length-1);
		}
		if (PARTNUMBER != '' && PARTNUMBER.length <2){
			alert("PN_Level "+i+"至少输入2位！");
			return;
		}
	}
	
	var url = "<%=request.getContextPath()%>/netmarkets/jsp/catl/report/trunkBOMQueryReportDownload.jsp";
	url = url + "?PARTNUMBER_1="+PARTNUMBER_1;
	url = url + "&PARTNUMBER_2="+PARTNUMBER_2;
	url = url + "&PARTNUMBER_3="+PARTNUMBER_3;
	url = url + "&PARTNUMBER_4="+PARTNUMBER_4;
	url = url + "&PARTNUMBER_5="+PARTNUMBER_5;
	url = url + "&PARTNUMBER_6="+PARTNUMBER_6;
	url = url + "&PARTNUMBER_7="+PARTNUMBER_7;
	url = url + "&PARTNUMBER_8="+PARTNUMBER_8;
	url = url + "&PARTNUMBER_9="+PARTNUMBER_9;
	url = url + "&PARTNUMBER_10="+PARTNUMBER_10;
	
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
		<td height="30" class="STYLE3" nowrap="nowrap">主干BOM查询报表</td>
	</tr>
</table>
<table id="trunkBOMQueryTable">
	<tr>
		<td><font>*PN_Level 1:</font></td>
		<td><input type="text" name="PARTNUMBER_1" id="PARTNUMBER_1"></td>
	</tr>
	<tr>
		<td><font>*PN_Level 2:</font></td>
		<td><input type="text" name="PARTNUMBER_2" id="PARTNUMBER_2"></td>
	</tr>
	<tr>
		<td><font>&nbsp;PN_Level 3:</font></td>
		<td><input type="text" name="PARTNUMBER_3" id="PARTNUMBER_3"></td>
	</tr>
	<tr>
		<td><font>&nbsp;PN_Level 4:</font></td>
		<td><input type="text" name="PARTNUMBER_4" id="PARTNUMBER_4"></td>
	</tr>
	<tr>
		<td><font>&nbsp;PN_Level 5:</font></td>
		<td><input type="text" name="PARTNUMBER_5" id="PARTNUMBER_5"></td>
	</tr>
	<tr>
		<td><font>&nbsp;PN_Level 6:</font></td>
		<td><input type="text" name="PARTNUMBER_6" id="PARTNUMBER_6"></td>
	</tr>
	<tr>
		<td><font>&nbsp;PN_Level 7:</font></td>
		<td><input type="text" name="PARTNUMBER_7" id="PARTNUMBER_7"></td>
	</tr>
	<tr>
		<td><font>&nbsp;PN_Level 8:</font></td>
		<td><input type="text" name="PARTNUMBER_8" id="PARTNUMBER_8"></td>
	</tr>
	<tr>
		<td><font>&nbsp;PN_Level 9:</font></td>
		<td><input type="text" name="PARTNUMBER_9" id="PARTNUMBER_9"></td>
	</tr>
	<tr>
		<td><font>&nbsp;PN_Level 10:</font></td>
		<td><input type="text" name="PARTNUMBER_10" id="PARTNUMBER_10"></td>
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