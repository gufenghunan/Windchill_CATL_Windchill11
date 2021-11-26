<%@page import="com.catl.cad.BatchDownloadPDFUtil"%>
<%@page import="wt.epm.EPMDocument"%>
<%@page import="com.catl.cad.BatchDownloadPDFHelper"%>
<%@page import="com.ptc.netmarkets.model.NmOid"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
try{
	String oid=request.getParameter("oid");
	NmOid nmOid = NmOid.newNmOid(oid);
	EPMDocument epm = (EPMDocument)nmOid.getRefObject();
	BatchDownloadPDFHelper.downloadPDF(epm, response);
	out.clear();
    out = pageContext.pushBody();
}
catch(Exception e){
	e.printStackTrace();
	response.setContentType("text/html");
    out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
    out.println("<HTML>");
    out.println("  <HEAD><TITLE>Result</TITLE></HEAD>");
    out.println("  <BODY>");
    out.println("下载异常:");
    out.println("<BR>");
    out.println(e.getLocalizedMessage());
    out.println("  <BR>");
    out.println("  </BODY>");
    out.println("</HTML>");
} 

%>