<%@ page language="java" pageEncoding="utf-8"%>
<%@page import="com.ptc.china.gs.applicationform.util.WorkflowUtil"%>
<% 
String msg = WorkflowUtil.checkDownloadAccess(request,response);
System.out.println("======"+msg);
if(msg != null && !"".equals(msg)){
%>
<script>	
	alert('<%=msg %>');
</script>
<%
}else{
	WorkflowUtil.downloadContent(request, response);
	out.clear(); 
	out=pageContext.pushBody();
}

%>
<script>
window.close();
</script>