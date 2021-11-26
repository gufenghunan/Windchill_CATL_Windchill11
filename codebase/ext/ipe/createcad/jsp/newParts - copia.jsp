<%@ include file="/netmarkets/jsp/util/beginPopup.jspf"%>
<%@ page import="ext.ipe.createcad.UtilCargaElectric" %>

<%
	String oid = request.getParameter("oid");
	String modified = request.getParameter("modified");
	
	UtilCargaElectric.modificarEstructura(oid,modified);
%>

<script>
	window.opener.location.reload();
	window.close();
</script>

<%@include file="/netmarkets/jsp/util/end.jspf"%>