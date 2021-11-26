<%@ include file="/netmarkets/jsp/util/beginPopup.jspf"%>
<%@ page import="ext.ipe.createcad.UtilCargaElectric" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
	String oid=request.getParameter("oid");
	if(!UtilCargaElectric.seHaLanzado(oid)){
%>

<script>

window.resizeTo(500,300);

function doOperation(){
	
	var opcion="";
	var valores=document.getElementsByName("estructurado");
	for(var i=0;i<valores.length;i++){
		if(valores[i].checked==true)
			opcion=valores[i].value;
	}	
	if(opcion=="")
		alert("You have not selected any options");
	else{
		var contenido=document.getElementById("contenido");
		contenido.innerHTML ="<p style='text-align:center;display:block;' > <img src='<%=wt.util.WTProperties.getLocalProperties().getProperty("wt.server.codebase")%>/ext/ipe/createcad/jsp/loader.gif' alt='Loading...'> </p>";
		contenido.innerHTML+="<div style='text-align:center;display:block;'><h1>Processing...</h1> </div>"
		window.location.href= '<%=wt.util.WTProperties.getLocalProperties().getProperty("wt.server.codebase")%>/ext/ipe/createcad/jsp/cargaElectricEBOMStep.jsp?opcion='+opcion+"&oid=<%=request.getParameter("oid")%>";
	}
		

}
</script>
<br/>

<div id="contenido" style="padding-left: 16px">
<h1>Selecciona estructurado</h1>

<input type="radio" name="estructurado" value="localizacion" checked="checked">Structured by location<br>
<input type="radio" name="estructurado" value="funcion">Structured by function<br>
<input type="radio" name="estructurado" value="plano">Flat list

<%@include file="buttons.jsp"%>
</div>

<%
	}
	else{
		
		%>	
			<script>
				window.location.href='<%=wt.util.WTProperties.getLocalProperties().getProperty("wt.server.codebase")%>/ext/ipe/createcad/jsp/actualizarBOM.jsp?oid=<%=oid%>';
			</script>
		<%
		
	}
%>


<%@include file="/netmarkets/jsp/util/end.jspf"%>
