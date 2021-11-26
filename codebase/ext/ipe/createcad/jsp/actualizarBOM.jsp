<head>
    <meta http-equiv="X-UA-Compatible" content="IE=10" />
	<script language="JavaScript">
function setSize(){
window.resizeTo(752,500);
}
</script>
</head>
<body onLoad="setSize();">

<%@ include file="/netmarkets/jsp/util/beginPopup.jspf"%>

<%@ page import="wt.part.WTPart" %>
<%@ page import="java.util.Map" %>
<%@ page import="ext.ipe.createcad.UtilCargaElectric" %>
<%@ page import="ext.ipe.createcad.Estructura" %>
<%@ page import="ext.ipe.createcad.Hijo" %>
<%@ page import="java.util.List" %>


<%!

public void mostrarEstructura(List<Estructura> estructuras,int nivel,JspWriter out){
	try{
		
		String baseref = (new wt.httpgw.URLFactory()).getBaseHREF();
		String urlImage=baseref+"/wtcore/images/part.gif";
		
		boolean esPlano=false;
		int contador=0;
		
		//Si es plano
		if(estructuras.size()==1){
			Estructura componentesElectricos=estructuras.get(0);
			if(componentesElectricos.getName().equals("Electrics Components ")){
				List<Hijo> hijos=componentesElectricos.getHijos();
				
				boolean modificado=false;
				
				for(Hijo hijo:hijos){
					if(hijo.getMarca()!=null && hijo.getMarca().equals("nuevo")){
						modificado=true;
					}
					if(hijo.getMarca()!=null && hijo.getMarca().equals("eliminado")){
						modificado=true;
					}
				}
				
				String soloLectura="";
				if(!modificado){
					soloLectura="readonly disabled";
				}
				
				String nombre=componentesElectricos.getName();
				String cell = nombre+"_newcode";
				
				out.println("<td \"vertical-align:middle\" align=\"center\" bgcolor=\"white\" style=\"width:10%;font-size:13px;\">");
				out.println("<input type=\"checkbox\" class=\"checkboxclass\"  id=\""+cell+"\" "+soloLectura+" onclick=\"unCheck('"+nombre+"_newversion');\" > ");
				out.println("</td>");
				
				cell = nombre+"_newversion";
				if(modificado){
					soloLectura="checked";
				}
				
				out.println("<td \"vertical-align:middle\" align=\"center\" bgcolor=\"white\" style=\"width:10%;font-size:13px;\">");
				out.println("<input type=\"checkbox\" class=\"checkboxclass\"  id=\""+cell+"\" "+soloLectura+" onclick=\"unCheck('"+nombre+"_newcode');\" > ");
				out.println("</td>");
				
				out.println("<td \"vertical-align:middle\" align=\"center\" bgcolor=\"white\" style=\"width:10%;font-size:13px;\">");
				out.println("<input type=\"checkbox\" class=\"checkboxclass\"  id=\""+cell+"\" readonly disabled> ");
				out.println("</td>");
				
				out.println("<td \"vertical-align:middle\" align=\"center\" bgcolor=\"white\" style=\"width:10%;font-size:13px;\">");
				out.println("<input type=\"checkbox\" class=\"checkboxclass\"  id=\""+cell+"\" readonly disabled> ");
				out.println("</td>");
				
				out.println("</tr>");
				
				esPlano=true;
			}
		}
		
		if(!esPlano){		
			//si tiene hijos eliminados o nuevos pongo primero los checkboxs
			boolean modificadoPadre=false;
			for(Estructura estructura:estructuras){
				if(estructura.getMarca()!=null && estructura.getMarca().equals("nuevo")){
					modificadoPadre=true;
				}
				if(estructura.getMarca()!=null && estructura.getMarca().equals("eliminado")){
					modificadoPadre=true;
				}				
			}

			if(modificadoPadre){
				String cell = "_parentnewcode";
				
				out.println("<td \"vertical-align:middle\" align=\"center\" bgcolor=\"white\" style=\"width:10%;font-size:13px;\">");
				out.println("<input type=\"checkbox\" class=\"checkboxclass\"  id=\""+cell+"\" onclick=\"unCheck('_parentnewversion');\" > ");
				out.println("</td>");
				
				cell = "_parentnewversion";
				
				out.println("<td \"vertical-align:middle\" align=\"center\" bgcolor=\"white\" style=\"width:10%;font-size:13px;\">");
				out.println("<input type=\"checkbox\" class=\"checkboxclass\" checked id=\""+cell+"\" onclick=\"unCheck('_parentnewcode');\" > ");
				out.println("</td>");
				
				out.println("<td \"vertical-align:middle\" align=\"center\" bgcolor=\"white\" style=\"width:10%;font-size:13px;\">");
				out.println("<input type=\"checkbox\" class=\"checkboxclass\" readonly disabled >");
				out.println("</td>");				
				
				out.println("<td \"vertical-align:middle\" align=\"center\" bgcolor=\"white\" style=\"width:10%;font-size:13px;\">");
				out.println("<input type=\"checkbox\" class=\"checkboxclass\" readonly disabled >");
				out.println("</td>");
				
			}
			for(Estructura estructura:estructuras){
				String nombre=estructura.getName();
				List<Hijo> hijos=estructura.getHijos();
				out.println("<tr style=\"width:auto\">");
				
				String cell = nombre+"_fila";		
				
				out.println("<td \"vertical-align:middle\" align=\"left\" bgcolor=\"white\" class=\"cellEditing\" id=\""+cell+"\" style=\"width:60%;font-size:13px;\" style=\"line-height:14px;padding-left:1px;padding-right:1px;\">");
				out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
				out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
				if(hijos!=null){
					if(hijos.size()>0)
						out.println("<img src=\""+baseref+"/wtcore/jsp/ext/ipe/ecaddoc/images/expanded.gif\" >");			
				}
				
				out.println("<img src=\""+urlImage+"\" alt\"ZCOJ\">");
				out.println(nombre);
				out.println("</td>");   
				
				boolean modificado=false;
				
				for(Hijo hijo:hijos){
					if(hijo.getMarca()!=null && hijo.getMarca().equals("nuevo")){
						modificado=true;
					}
					if(hijo.getMarca()!=null && hijo.getMarca().equals("eliminado")){
						modificado=true;
					}
				}
				
				String soloLectura="";
				if(!modificado){
					soloLectura="readonly disabled";
				}
				
				cell = nombre+"_newcode";
				
				out.println("<td \"vertical-align:middle\" align=\"center\" bgcolor=\"white\" style=\"width:10%;font-size:13px;\">");
				out.println("<input type=\"checkbox\" class=\"checkboxclass\"  id=\""+cell+"\" "+soloLectura+" onclick=\"unCheck('"+nombre+"_newversion');\" > ");
				out.println("</td>");
				
				cell = nombre+"_newversion";
				
				if(modificado){
					soloLectura="checked";
				}
				
				out.println("<td \"vertical-align:middle\" align=\"center\" bgcolor=\"white\" style=\"width:10%;font-size:13px;\">");
				out.println("<input type=\"checkbox\" class=\"checkboxclass\"  id=\""+cell+"\" "+soloLectura+" onclick=\"unCheck('"+nombre+"_newcode');\" > ");
				out.println("</td>");
					
				
				
				if(estructura.getMarca()!=null && estructura.getMarca().equals("eliminado")){					
					out.println("<td \"vertical-align:middle\" align=\"center\" bgcolor=\"white\" style=\"width:10%;font-size:13px;\">");
					out.println("<input type=\"checkbox\" class=\"checkboxclass\"  readonly disabled checked> ");
					out.println("</td>");
				}	
				else{
					out.println("<td \"vertical-align:middle\" align=\"center\" bgcolor=\"white\" style=\"width:10%;font-size:13px;\">");
					out.println("<input type=\"checkbox\" class=\"checkboxclass\"  readonly disabled > ");
					out.println("</td>");
				}				

				if(estructura.getMarca()!=null && estructura.getMarca().equals("nuevo")){					
					out.println("<td \"vertical-align:middle\" align=\"center\" bgcolor=\"white\" style=\"width:10%;font-size:13px;\">");
					out.println("<input type=\"checkbox\" class=\"checkboxclass\"  readonly disabled checked> ");
					out.println("</td>");
				}	
				else{					
					out.println("<td \"vertical-align:middle\" align=\"center\" bgcolor=\"white\" style=\"width:10%;font-size:13px;\">");
					out.println("<input type=\"checkbox\" class=\"checkboxclass\"  id=\""+cell+"\" readonly disabled ");
					out.println("</td>");
				}
					
				
				out.println("</tr>");

				contador++;
				
			}
		}
		
		out.println("</tbody></table>");
		out.println("</div>");
		out.println("</div>");
		out.println("</div>");

		out.println("<div class=\"x-grid3-header\" id=\"ext-gen50\" align=\"right\" style=\"width: auto;\" style=\"border:1px solid #D0D0D0;\">");
		out.println(contador+"&nbsp;Elements&nbsp;");
		out.println("</div>");

		out.println("</div>");
		out.println("</table>");
		
		
	}catch(Exception e){
		
	}
}
%>
<div id="contenido">
<%	
	String baseref = (new wt.httpgw.URLFactory()).getBaseHREF();
	String oid = request.getParameter("oid");
	WTPart part = (WTPart)new wt.fc.ReferenceFactory().getReference(oid).getObject();
	List<WTPart> hijosExtraidos=UtilCargaElectric.getHijosExtraidos(oid);
	//Compruebo que no haya ningun hijo extraido
	if(!hijosExtraidos.isEmpty()){
		String checkedout ="";
		checkedout+=hijosExtraidos.get(0).getNumber();
		for(int i=1;i<hijosExtraidos.size();i++){
			checkedout+=", "+hijosExtraidos.get(i).getNumber();
		}
		%>
		<script type="text/javascript">
			var checkedout='<%=checkedout%>';
			alert("The structure can not be updated. The following elements are checked-out: "+checkedout);
			window.close();
		</script>
		
		<%
	}
	else{		
		List<Estructura> estructura=UtilCargaElectric.getChangedStructure(oid);	
		boolean modificado=false;
		
		//Compruebo que haya cambios
		for(Estructura cadaEstructura:estructura){
			String marca=cadaEstructura.getMarca();
			if(marca!=null && !marca.trim().equals(""))
				modificado=true;
			List<Hijo> hijos=cadaEstructura.getHijos();	
			for(Hijo hijo:hijos){
				if(hijo.getMarca()!=null && hijo.getMarca().equals("nuevo")){
					modificado=true;
				}
				if(hijo.getMarca()!=null && hijo.getMarca().equals("eliminado")){
					modificado=true;
				}
			}
		}
		
		if(!modificado){
			%>
			<script type="text/javascript">
				alert("It's the same structure. BOM hasn't been updated");
				window.close();
			</script>
			
			<%
		}	
		
		else{
			out.print("<div class=\"x-panel-body x-panel-body-noheader x-panel-body-noborder\" id=\"ext-gen311\" style=\"width: 100%; height: auto;\">");
			out.print("<div class=\"x-grid3-header\" id=\"ext-gen50\" align=\"center\" style=\"width: auto;\" style=\"border:1px solid #D0D0D0;\">");
			out.print("<h1>Update Electric BOM</h1>");
			out.print("</div>");
			out.print("</div>");
			out.print("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" style=\"width: 100%;\"><thead><tr class=\"x-grid3-hd-row\">");   
			
			out.print("<td style=\"width:60%;\" class=\"x-grid3-hd x-grid3-cell x-grid3-td-attributesTableLabel\">");      
			out.print("<div unselectable=\"on\" class=\"x-grid3-header\">");
			out.print("<span id=\"x-auto-113\" class=\" x-component\">&nbsp;Identity</span>");
			out.print("</td>");	

			out.print("<td style=\"width:10%;\" class=\"x-grid3-hd x-grid3-cell x-grid3-td-attributesTableLabel\">");   
			out.print("<div unselectable=\"on\" align=\"center\"  class=\"x-grid3-header\">");
			out.print("<span id=\"x-auto-113\" class=\"x-component\">New code</span>");
			out.print("</td>");		
			
			out.print("<td style=\"width:10%;\" class=\"x-grid3-hd x-grid3-cell x-grid3-td-attributesTableLabel\">");   
			out.print("<div unselectable=\"on\" align=\"center\"  class=\"x-grid3-header\">");
			out.print("<span id=\"x-auto-113\" class=\"x-component\">New Iteration</span>");
			out.print("</td>");
		
			out.print("<td style=\"width:10%;\" class=\"x-grid3-hd x-grid3-cell x-grid3-td-attributesTableLabel\">");   
			out.print("<div unselectable=\"on\" align=\"center\"  class=\"x-grid3-header\">");
			out.print("<span id=\"x-auto-113\" class=\"x-component\">New in structure</span>");
			out.print("</td>");
			
			out.print("<td style=\"width:10%;\" class=\"x-grid3-hd x-grid3-cell x-grid3-td-attributesTableLabel\">");   
			out.print("<div unselectable=\"on\" align=\"center\"  class=\"x-grid3-header\">");
			out.print("<span id=\"x-auto-113\" class=\"x-component\">Removed</span>");
			out.print("</td>");
			out.print("</tr>");
			out.print("</thead></table>");
			out.print("</div>");
			out.print("<div class=\"x-clear\"></div>");
			out.print("</div>");
			out.print("<div style=\"width: 100%;\" class=\"x-grid3-body\" id=\"ext-gen53\">");
			
			out.print("<div style=\"width: 100%;\" class=\"x-grid3-row x-grid3-row-first\" id=\"ext-gen75\">");	
			out.print("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" style=\"width: 100%;height:15px;\" class=\"x-grid3-row-table\"><tbody>");
			  String cell = "cell_padre";
			 %>
			 <tr style="width:100%;height:15px;" valign="middle">
				<td  align="left" bgcolor="white" class="class="x-grid3-col x-grid3-cell x-grid3-td-tableIcon x-grid-cell-first" unselectable="on" id="<%=cell%>" style="width:60%;height:15px;font-size:13px;" style="line-height:14px;padding-left:1px;padding-right:1px;">      
				<img src="<%=baseref%>/wtcore/jsp/ext/ipe/ecaddoc/images/expanded.gif" alt=""><img src="<%=baseref%>/wtcore/images/part.gif" alt="WTPart"> 
				   <%=part.getName()%>
				</td>
				<td "vertical-align:middle" align="left" bgcolor="white" style="width:10%;font-size:13px;">
				</td>
				<td "vertical-align:middle" align="left" bgcolor="white" style="width:10%;font-size:13px;">
				</td>
				</td>
				<td "vertical-align:middle" align="left" bgcolor="white" style="width:10%;font-size:13px;">
				</td>
				<td "vertical-align:middle" align="left" bgcolor="white" style="width:10%;font-size:13px;">
				</td>
			</tr>
			
			<tr style="width:auto">
			<td "vertical-align:middle" align="left" bgcolor="white" class="cellEditing" id="<%=cell+"_electrico"%>" style="width:60%;font-size:13px;" style="line-height:14px;padding-left:1px;padding-right:1px;">
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<img src="<%=baseref%>/wtcore/jsp/ext/ipe/ecaddoc/images/expanded.gif" >	
				<img src="<%=baseref%>/wtcore/images/part.gif" alt"ZCOJ">
					Electrics Components
				</td>
			 <%		 
			
			mostrarEstructura(estructura,0,out);
			
		}
	}
%>

</div>

<script>
function doOperation(){
	var oid='<%=oid%>';

	var modified="";
	var x = document.getElementsByClassName("checkboxclass");	
	for (var i = 0; i < x.length; i++) {		
		if(x[i].checked){			
			var iden=x[i].id;
			if(iden.trim()!="")
				modified=iden+";"+modified;
		}
	}
	
	if(modified.trim()=="")
		alert("No se ha seleccionado nada");
	else{
		
		var contenido=document.body;
		contenido.innerHTML ="<p style='text-align:center;display:block;' > <img src='<%=wt.util.WTProperties.getLocalProperties().getProperty("wt.server.codebase")%>/ext/ipe/createcad/jsp/loader.gif' alt='Loading...'> </p>";
		contenido.innerHTML+="<div style='text-align:center;display:block;'><h1>Processing...</h1> </div>"
		
		modified = fixedEncodeURIComponent(modified);
		console.log(modified);
	
		location.href="<%=baseref%>/ext/ipe/createcad/jsp/newParts.jsp?oid="+oid+"&modified="+modified;
	}
}

function fixedEncodeURIComponent(str){
	 str=str.slice(0,-1);
     var devolver= encodeURIComponent(str).replace(/[!'()]/g, escape).replace(/\*/g, "%2A").replace(/"/g,"\\\"");	 
	 return devolver;
}

function unCheck(id) {
	var check = document.getElementById(id);
	if(check.checked==true)
		check.checked = false;
}
</script>

<%@include file="/netmarkets/jsp/util/end.jspf"%>
<%@include file="buttons.jsp"%>