<head>
    <meta http-equiv="X-UA-Compatible" content="IE=10" />
	<script language="JavaScript">
function setSize(){
window.resizeTo(1000,500);
}
</script>
</head>
<body onLoad="setSize();">


<jsp:useBean id="CMXWTHelper" class="ext.ipe.wtseeintegration.utils.CMXWTHelper" scope="request"/>
<jsp:useBean id="UtilHelper" class="ext.ipe.mbom.UtilHelper" scope="request"/>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@taglib uri="http://www.ptc.com/windchill/taglib/core" prefix="wc"%>
<%@taglib uri="http://www.ptc.com/windchill/taglib/auditing" prefix="auditing"%>
<%@taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@taglib uri="http://www.ptc.com/windchill/taglib/picker" prefix="p"%>
<%@taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@taglib prefix="wc" uri="http://www.ptc.com/windchill/taglib/core"%>
<%@taglib prefix="wctags" tagdir="/WEB-INF/tags"%>
<%@ page import="java.util.Vector" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.Collections" %>
<%@ page import="wt.part.WTPart" %>


<%@ include file="/netmarkets/jsp/util/beginPopup.jspf"%>


<%	
	String oid = request.getParameter("oid");
	String baseref = (new wt.httpgw.URLFactory()).getBaseHREF();
	Vector vector = CMXWTHelper.getCheckedOutWTPartChildren(oid);
	System.out.println("Checked-out: "+vector.size());
	String checkedout ="";
	if(vector.size()>0){
		checkedout+=vector.get(0);
		for(int i=1;i<vector.size();i++){
			checkedout+=", "+vector.get(i);
		}
	 %>
  <script type="text/javascript">
	var checkedout='<%=checkedout%>';
	alert("BOM cannot be updated. Components are checkout: "+checkedout);
	window.close();
	</script>
<%
	}
	else{
	oid= CMXWTHelper.updateClientStructure(oid);
  Hashtable structure = CMXWTHelper.getChangedStructure(oid);
 // WTPart wtprojectpart = CMXWTHelper.getWTPartAssociated(oid);
  WTPart wtprojectpart = (WTPart)new wt.fc.ReferenceFactory().getReference(oid).getObject();
	String configurable= "disabled readonly";
	boolean cliente =true;
  if (wtprojectpart.getGenericType().getDisplay(Locale.ENGLISH).equalsIgnoreCase("yes") || wtprojectpart.getGenericType().getDisplay().equalsIgnoreCase("sí")) {
	//configurable = "disabled=disabled readonly=readonly";
	cliente=false;
	configurable = "";
  }
  if(structure.isEmpty()){
  %>
  <script type="text/javascript">
	alert("Structure isn't modified. BOM hasn't been updated");
	 window.opener.location.reload();
	window.close();
	</script>
<%
  }
  else{
  Enumeration e =structure.keys();
  int size = Collections.list(e).size();
	e =structure.keys();
%>       
<br/>
<div class="x-panel-body x-panel-body-noheader x-panel-body-noborder" id="ext-gen311" style="width: 100%; height: auto;">
<div class="x-grid3-header" id=\"ext-gen50" align="center" style="width: auto;" style="border:1px solid #D0D0D0;">
<h1>ACTUALIZACION BOM ELECTRICO DEL SEE Expert</h1>
</div>
<%


        out.print("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" style=\"width: 100%;\"><thead><tr class=\"x-grid3-hd-row\">");   
		
        out.print("<td style=\"width:60%;\" class=\"x-grid3-hd x-grid3-cell x-grid3-td-attributesTableLabel\">");      
        out.print("<div unselectable=\"on\" class=\"x-grid3-header\">");
		out.print("<span id=\"x-auto-113\" class=\" x-component\">&nbsp;Identidad</span>");
        out.print("</td>");
		
		if(!cliente){
			out.print("<td style=\"width:10%;\" class=\"x-grid3-hd x-grid3-cell x-grid3-td-attributesTableLabel\">");   
			out.print("<div unselectable=\"on\" align=\"center\"  class=\"x-grid3-header\">");
			out.print("<span id=\"x-auto-113\" class=\" x-component\">Nuevo código</span>");
			out.print("</td>");
		}
		
		out.print("<td style=\"width:10%;\" class=\"x-grid3-hd x-grid3-cell x-grid3-td-attributesTableLabel\">");   
        out.print("<div unselectable=\"on\" align=\"center\"  class=\"x-grid3-header\">");
		out.print("<span id=\"x-auto-113\" class=\"x-component\">Conjunto modificado</span>");
        out.print("</td>");
		
		out.print("<td style=\"width:10%;\" class=\"x-grid3-hd x-grid3-cell x-grid3-td-attributesTableLabel\">");   
        out.print("<div unselectable=\"on\" align=\"center\"  class=\"x-grid3-header\">");
		out.print("<span id=\"x-auto-113\" class=\"x-component\">Nuevo en estructura</span>");
        out.print("</td>");
		
		out.print("<td style=\"width:10%;\" class=\"x-grid3-hd x-grid3-cell x-grid3-td-attributesTableLabel\">");   
        out.print("<div unselectable=\"on\" align=\"center\"  class=\"x-grid3-header\">");
		out.print("<span id=\"x-auto-113\" class=\"x-component\">Eliminado</span>");
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
			<td  align="left" bgcolor="white" class="class="x-grid3-col x-grid3-cell x-grid3-td-tableIcon x-grid-cell-first" unselectable="off" id="<%=cell%>" style="width:60%;height:15px;font-size:13px;" style="line-height:14px;padding-left:1px;padding-right:1px;">      
			<img src="<%=baseref%>/wtcore/jsp/ext/ipe/ecaddoc/images/expanded.gif" alt=""><img src="<%=UtilHelper.getIcon(wtprojectpart)%>" alt="ZCOJ"> 
               <%=wtprojectpart.getIdentity()%>
			</td>
			<%
			if(!cliente){
			%>   
			<td "vertical-align:middle" align="left" bgcolor="white" style="width:10%;font-size:13px;">
			</td>
			<%
			}
			%>
			<td "vertical-align:middle" align="left" bgcolor="white" style="width:10%;font-size:13px;">
			</td>
			</td>
			<td "vertical-align:middle" align="left" bgcolor="white" style="width:10%;font-size:13px;">
			</td>
			<td "vertical-align:middle" align="left" bgcolor="white" style="width:10%;font-size:13px;">
			</td>
  </tr>
		 <%
        while (e.hasMoreElements()){
            String part = (String)e.nextElement();
            String modified = (String)structure.get(part);
			System.out.println(part+", "+modified);
			//part= "EE_"+part+"_"+wtprojectpart.getName().replace("EE_", "");
			//String name = "EE_" + part + "_" + wtprojectpart.getName().replace("EE_", "");
			String name = "EE_" + part;
            if (wtprojectpart.getGenericType().getDisplay(Locale.ENGLISH).equalsIgnoreCase("yes") || wtprojectpart.getGenericType().getDisplay().equalsIgnoreCase("sí"))
				name = "EE_" + part;
			String identity=name;
			WTPart subpart =null;
			String icon = baseref+"/netmarkets/images/comexi/Zcoj.gif";
			if(CMXWTHelper.getLatestWTPartByName(part)!=null){
				subpart= CMXWTHelper.getLatestWTPartByName(part);
				icon = UtilHelper.getIcon(subpart);
				//identity=subpart.getIdentity();
			}
			name = name.replace("\"", "&quot;");
  %>
  
		 <tr style="width:auto">
                 
      <%       
            cell = "cell_"+name+"_1";
         %>          
            <td "vertical-align:middle" align="left" bgcolor="white" class="cellEditing" id="<%=cell%>" style="width:60%;font-size:13px;" style="line-height:14px;padding-left:1px;padding-right:1px;">      
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

			<img src=<%=icon%> alt"ZCOJ"> 
               <%=identity%>
			</td>
               <% 
      if (modified.equals("modified")){
			cell = name+"_newcode";
			if(!cliente){
        %>          
			<td "vertical-align:middle" align="center" bgcolor="white" style="width:10%;font-size:13px;">
			<input type="checkbox" class="checkboxclass"  id="<%=cell%>" onclick="unCheck('<%=name%>_newversion');"<%=configurable%>>
			</td>
			  
        <% 
			}
			cell = name+"_newversion";     
        %>          
			<td "vertical-align:middle" align="center" bgcolor="white" style="width:10%;font-size:13px;">
			<input type="checkbox"  class="checkboxclass" id="<%=cell%>" checked onclick="unCheck('<%=name%>_newcode');"<%=configurable%>>
			</td>
			  
        <% 
			cell = name+"_new";     
		%>          
			<td "vertical-align:middle" align="center" bgcolor="white" style="width:10%;font-size:13px;">
			<input type="checkbox"  class="checkboxclass" id="<%=cell%>" disabled readonly>
			</td>
			  
        <% 
			cell = name+"_removed";     
        %>          
			<td "vertical-align:middle" align="center" bgcolor="white" style="width:10%;font-size:13px;">
			<input type="checkbox"  class="checkboxclass" id="<%=cell%>" disabled readonly>
			</td>
			  
        <% 
		}	
		else if (modified.equals("false")){
			cell = name+"_newcode";  
			if(!cliente){			
        %>          
			<td "vertical-align:middle" align="center" bgcolor="white" style="width:10%;font-size:13px;">
			<input type="checkbox"  class="checkboxclass" id="<%=cell%>" disabled readonly>
			</td>
			  
        <% 
			}
			cell = name+"_newversion";     
        %>          
			<td "vertical-align:middle" align="center" bgcolor="white" style="width:10%;font-size:13px;">
			<input type="checkbox"  class="checkboxclass" id="<%=cell%>" disabled readonly>
			</td>
			  
        <% 
			cell = name+"_new";     
		%>          
			<td "vertical-align:middle" align="center" bgcolor="white" style="width:10%;font-size:13px;">
			<input type="checkbox"  class="checkboxclass" id="<%=cell%>" disabled readonly>
			</td>
			  
        <% 
			cell = name+"_removed";     
        %>          
			<td "vertical-align:middle" align="center" bgcolor="white" style="width:10%;font-size:13px;">
			<input type="checkbox"  class="checkboxclass" id="<%=cell%>" disabled readonly>
			</td>
			  
        <% 
		}	
		else if (modified.equals("removed")){
			cell = name+"_newcode";    
			if(!cliente){
        %>          
			<td "vertical-align:middle" align="center" bgcolor="white" style="width:10%;font-size:13px;">
			<input type="checkbox"  class="checkboxclass" id="<%=cell%>" disabled readonly>
			</td>
			  
        <% 
		}
			cell = name+"_newversion";     
        %>          
			<td "vertical-align:middle" align="center" bgcolor="white" style="width:10%;font-size:13px;">
			<input type="checkbox"  class="checkboxclass" id="<%=cell%>" disabled readonly>
			</td>
			  
        <% 
			cell = name+"_new";     
		%>          
			<td "vertical-align:middle" align="center" bgcolor="white" style="width:10%;font-size:13px;">
			<input type="checkbox"  class="checkboxclass" id="<%=cell%>" disabled readonly>
			</td>
			  
        <% 
			cell = name+"_removed";     
        %>          
			<td "vertical-align:middle" align="center" bgcolor="white" style="width:10%;font-size:13px;">
			<input type="checkbox"  class="checkboxclass" id="<%=cell%>" disabled readonly checked>
			</td>
			  
        <% 
		} 		
		else if (modified.equals("new")){
			cell = name+"_newcode";
			if(!cliente){			
        %>          
			<td "vertical-align:middle" align="center" bgcolor="white" style="width:10%;font-size:13px;">
			<input type="checkbox"  class="checkboxclass" id="<%=cell%>" disabled readonly>
			</td>
			  
        <%
			} 
			cell = name+"_newversion";     
        %>          
			<td "vertical-align:middle" align="center" bgcolor="white" style="width:10%;font-size:13px;">
			<input type="checkbox"  class="checkboxclass" id="<%=cell%>" disabled readonly>
			</td>
			  
        <% 
			cell = name+"_new";     
		%>          
			<td "vertical-align:middle" align="center" bgcolor="white" style="width:10%;font-size:13px;">
			<input type="checkbox"  class="checkboxclass" id="<%=cell%>" disabled readonly checked>
			</td>
			  
        <% 
			cell = name+"_removed";     
        %>          
			<td "vertical-align:middle" align="center" bgcolor="white" style="width:10%;font-size:13px;">
			<input type="checkbox"  class="checkboxclass" id="<%=cell%>" disabled readonly>
			</td>
			  
        <% 
		} 
      String cellvalue = "cell_"+name;
     %>
   
    </tr>
	
<% 

	 }%>
 </tbody></table>
	</div>
        </div>
   </div>  

<div class="x-grid3-header" id="ext-gen50" align="right" style="width: auto;" style="border:1px solid #D0D0D0;">
<%=size%>&nbsp;Elementos&nbsp;
</div>

</div>
</table>

<% 

	 }}%>

<script type="text/javascript">

function unCheck(id) {
	var check = document.getElementById(id);
//	alert(id +' '+ check.checked);
	check.checked = !check.checked;
}

function doOperation(){
	var oid='<%=oid%>';
	var nav = navigator.appName;
	var modified="";
	var x = document.getElementsByClassName("checkboxclass");
	var i;
	for (i = 0; i < x.length; i++) {
		if(x[i].checked){
		var iden=x[i].id;
			modified=iden+";"+modified;
			}
	}
	modified = fixedEncodeURIComponent(modified);
	if(nav == "Microsoft Internet Explorer"){
			location.href="upload_ebom.jsp?oid="+oid+"&modified="+modified;
		}
		else{
			location.href="wtcore/jsp/ext/ipe/ecaddoc/upload_ebom.jsp?oid="+oid+"&modified="+modified;
		}
}

function fixedEncodeURIComponent(str){
     return encodeURIComponent(str).replace(/[!'()]/g, escape).replace(/\*/g, "%2A").replace(/"/g,"\\\"");;
}

</script>

<%@include file="/netmarkets/jsp/util/end.jspf"%>

<%@include file="buttons.jsp"%>