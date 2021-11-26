<%@ page import="org.json.JSONObject" %>
<%@ page import="com.catl.change.inventory.ChangeInventoryUtil" %>
<jsp:useBean id="commandBean" class="com.ptc.netmarkets.util.beans.NmCommandBean" scope="request"/>       

<%
	String oid = (String)request.getParameter("oid");
	String data = ChangeInventoryUtil.getDataForSearchPart(oid);
	JSONObject json = new JSONObject();
	json.put("result",data);
	out.println(json.toString());
%>