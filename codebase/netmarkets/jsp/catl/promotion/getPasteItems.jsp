<%@page import="com.ptc.netmarkets.model.NmOid"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.ptc.netmarkets.util.beans.NmClipboardBean"%>
<%@page import="com.catl.promotion.util.PromotionUtil"%>
<%@page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%> 
<jsp:useBean id="clipboardBean" class="com.ptc.netmarkets.util.beans.NmClipboardBean" scope="session"/> 
<%
out.print(PromotionUtil.getPasteItems(clipboardBean));
%>
