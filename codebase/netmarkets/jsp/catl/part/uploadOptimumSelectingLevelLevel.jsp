<%@ page contentType="text/html; charset=gb2312" language="java" %> 
<%@ page import="java.util.*,wt.util.WTContext,wt.util.WTMessage,wt.util.WTProperties,wt.httpgw.URLFactory"%>

<%
	URLFactory urlFactory = new URLFactory();
	String contextPath = urlFactory.getBaseHREF();
	WTProperties wtproperties = WTProperties.getLocalProperties();
	String codebase=wtproperties.getProperty("wt.server.codebase",	"/Windchill");
	System.out.println("contextPath:"+contextPath);
%>
<html>
	<head>
		<title></title>
		<style>
			.bg-body { background-color: #BEBEBE }	
		</style>
	</head>
<body class="bg-body">
	<form name="mainform1" action="<%=contextPath%>netmarkets/jsp/catl/part/doload.jsp" method="post" ENCTYPE="multipart/form-data">
	<table  width="100%" > 
	<td align="left" width="100%" title="ά����ѡ�ȼ�"  bgcolor="#30469D" height="40">
	<b><font face="verdana" color=white size="5" >&nbsp;����ά����ѡ�ȼ��ļ�</font></b></td>
	</table>

	<table>
	<tr>&nbsp;</tr>
	</table>
		<table width="100%" height="100">
	<td bgcolor=#D6D7CD>
<script>
	var flag = 1;
	function commit(){
		
		var fileName =document.getElementById("uploadFile").value;
		var fileNameTemp = fileName.split(".");
		if(!(fileNameTemp[fileNameTemp.length - 1] == "xls" || fileNameTemp[fileNameTemp.length - 1] == "xlsx")){
			alert("�ļ����ͱ�����excel��ʽ!");	
			return;
		}
		var f = document.getElementById("message_upload");
		var g = document.getElementById("message_wait");
		var e= document.getElementById("message_error");
		f.style.display = "";
		e.style.display = "";
		g.style.display = "";
		document.mainform1.submit();
	}
	
	function hiT(){
			var f = document.getElementById("message_upload");
			var e = document.getElementById("message_error");
			var g = document.getElementById("message_wait");

			if(flag == 1){
				flag = 2 ;
				f.style.display = "none";
				e.style.display = "";
				g.style.display = "";
				return;
			}else{
				flag = 1 ;
				f.style.display = "";
				e.style.display = "";
				g.style.display = "none";
			}
	}
	
</script>
<br>

<center>
		<div id="message_upload" style="display:" top="600">
			<table align="left" valign="middle" >
				
				<tr>
					<td><B><font face="verdana">�ļ�:</font></B></td><td><input type="FILE" name="loadFile" id="uploadFile"></td><td><div id="message_wait" style="display:none">
					<p>
<img src="<%=codebase+"/com/ptc/core/htmlcomp/images/animation_progress.gif"%>" align="left" style="margin:3px" alt="���ڵ���....." width="60" height="17"/><strong>���ڵ���...</strong></p></div></td>
				</tr>
				<tr><td></td></tr>
				<tr>
					<td></td><td></td><td align="right">
					<input  style="background-color: #E4D57D; margin-right:10px; margin-bottom:2px; margin-top:2px" type="button" value="����" onclick="commit()">
					<!--  <input style="background-color: #E4D57D; margin-right:10px; margin-bottom:2px; margin-top:2px" type="button" value="ȡ��" onclick="window.close()">--></td>
				</tr>
				
			</table>
		</div>
</center>

		<br><br><br><br><br>
	</td>
	
	
	</table>
	<div id="message_error" style="display:">	

					<%
						// Get Result of Precheck
						String error=(String)session.getAttribute("checkError");
						if("successful".equalsIgnoreCase(error)){
							error = "";
							%><script language='javascript'>alert("����ɹ�!");</script><%
						}else if(error!=null){
							error = "<h3><font color=\"#FF0000\">����ʧ�ܡ��������:</font></h3> <br/><br/>" + error; 
						}
					if(error!=null){out.print(error);}%>
					<%
						session.removeAttribute("checkError");
					%>
		</div>
</form>
</body>
</html>