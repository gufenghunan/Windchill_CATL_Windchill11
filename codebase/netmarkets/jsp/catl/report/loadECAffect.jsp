<%@page import="com.catl.change.report.ecn.ExportECNAffectTargetsParent2Excel"%>
<%@page language="java" session="true" pageEncoding="UTF-8"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@ page import="java.io.*" %>

<%
 String oid = request.getParameter("oid");
  String   path  = request.getContextPath(); 
  System.out.println(path); 
  String   basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";  
  String  jsppath="netmarkets//jsp//catl//report//loadECAffectLoad.jsp?oid="+oid;
  System.out.println(basePath);  

 String url=basePath+jsppath;
 System.out.println("url=="+url);
%>
<script>

//lert(oid);
window.location="<%=url%>";
window.close;
</script>

<%
  //file.delete();
%>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>