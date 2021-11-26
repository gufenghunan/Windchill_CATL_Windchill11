<%@ include file="/netmarkets/jsp/util/beginPopup.jspf"%>
<%@ page import="ext.ipe.createcad.UtilCargaElectric" %>

<%
	String oid = request.getParameter("oid");
	String modified = request.getParameter("modified");
	
	UtilCargaElectric.modificarEstructura(oid,modified);
%>

<script>
	window.opener.location.reload();
	var contenido=document.body;
		contenido.innerHTML ="<p style='text-align:center;display:block;' > <img src='<%=wt.util.WTProperties.getLocalProperties().getProperty("wt.server.codebase")%>/ext/ipe/createcad/jsp/loader.gif' alt='Loading...'> </p>";
		contenido.innerHTML+="<div style='text-align:center;display:block;'><h1>Loading...</h1> </div>"
	setTimeout(function() {		
		window.close();
	}, 10000);
	
</script>

<%@include file="/netmarkets/jsp/util/end.jspf"%>