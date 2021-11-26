<%@ include file="/netmarkets/jsp/util/beginPopup.jspf"%>
<%@ page import="wt.part.WTPart" %>
<%@ page import="ext.ipe.createcad.UtilCargaElectric" %>

<%
	String oid=request.getParameter("oid");
	String opcion=request.getParameter("opcion");
	if(oid!=null && opcion!=null){
		WTPart part=(WTPart)new wt.fc.ReferenceFactory().getReference(oid).getObject();  
		
		UtilCargaElectric.cargaPlano(part,opcion);
		
		%>
			<script>
				window.opener.location.reload();
				window.close();
			</script>
		
		<%
	}


%>


<%@include file="/netmarkets/jsp/util/end.jspf"%>
