<%@page import="com.catl.change.report.others.ChangeReportUtil"%>
<%@page language="java" session="true" pageEncoding="UTF-8"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@ page import="java.io.*" %>

<%
  String oid = ChangeReportUtil.queryReportOidByName("Inventory_Disposition");
  String path  = request.getContextPath(); 
  String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";  
  String jsppath="wtcore/jsp/report/runReport.jsp?oid="+oid;
  System.out.println(basePath);  

  String url=basePath+jsppath;
  System.out.println("url===="+url);
%>

<script>

Ext.onReady(function() {
	var obj = document.getElementById("form1");
	obj.height= this.document.body.scrollHeight-120;
	
});
Ext.EventManager.onWindowResize(function(width,height){//改变窗口的时候会提示出窗口的宽高
	var obj = document.getElementById("form1");
	obj.height= this.document.body.scrollHeight-120;
});
</script>
<iframe id="form1" src="<%=url%>" height="100%" width="98%">

</iframe>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>