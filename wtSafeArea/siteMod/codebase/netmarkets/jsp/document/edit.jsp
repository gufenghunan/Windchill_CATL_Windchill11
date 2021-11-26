<%@ page import="wt.content.ContentHolder" %>
<%@ page import="wt.access.AccessPermission" %>
<%@ page import="com.ptc.windchill.enterprise.attachments.server.AttachmentsHelper" %>

<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="wip" uri="http://www.ptc.com/windchill/taglib/workinprogress"%>

<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>


<%-- This code is checking if we have modify content access. This code should be removed and put into a
     validator on the attachments step --%>
	 
<%
    boolean bPermit = false;
    Object context = commandBean.getPageOid().getRef();
    bPermit = (context instanceof ContentHolder) && AttachmentsHelper.hasPermission((ContentHolder) context, AccessPermission.MODIFY_CONTENT);

    String userAgent = commandBean.getTextParameter("ua");
    String actionName = commandBean.getTextParameter("actionName");

    boolean useCheckinEdit = false;

    NmContext nmContextObject = nmcontext.getContext();
    NmContextItem ci = (NmContextItem) nmContextObject.getContextItems().lastElement();

    String titleDefault = NmActionServiceHelper.service.getAction(ci.getType(), ci.getAction()).getTitle();

    String titleCheckIn = NmActionServiceHelper.service.getAction("wip", "checkin").getTitle();

    if(userAgent != null && userAgent.equals("DTI") && 
        actionName != null && actionName.equals("checkin"))
        useCheckinEdit = true;
    
    if(useCheckinEdit){
%>
        <c:set var="buttonList" value="CheckInEditWizardButtons" scope="page"/>
        <c:set var="title" value="<%=titleCheckIn%>" scope="page"/>
<%
    } else {
%>
        <c:set var="buttonList" value="EditWizardButtons" scope="page"/>
        <c:set var="title" value="<%=titleDefault%>" scope="page"/>
<%
}
%>

<%-- This tag checks out the document and sets magical form inputs and data on the command bean. This
     makes sure that the command bean's get oid methods return the oid of the working copy. --%>
<wip:autoCheckOutItem />

<%-- sets up initial data for common components --%>
<jca:initializeItem operation="${createBean.edit}"/>

<%-- todo PROBLEM!!! we are reinitializing the item again --%>
<c:choose>
     <%-- sets up dti specific data --%>
     <c:when test='${param.externalFormData != null}'>
         <jca:initializeItem operation="${createBean.edit}" attributePopulatorClass="com.ptc.windchill.enterprise.nativeapp.msoi.forms.ExternalFormDataPopulator" />
     </c:when>

     <%-- default set up initial data for common components --%>
     <c:otherwise>
          <jca:initializeItem operation="${createBean.edit}"/>
     </c:otherwise>
</c:choose>

<%-- set up wizard steps. If we don't have modify access don't show attachments step. TODO fix this --%>
<% if (bPermit) { %>
<jca:wizard buttonList="${buttonList}"
            helpSelectorKey="DocMgmtDocEdit" title="${title}">
    <jca:wizardStep action="editAttributesWizStep" type="object"/>
    <jca:wizardStep action="attachments_step" type="attachments" />
</jca:wizard>
<% } else { %>
<jca:wizard buttonList="${buttonList}" title="${title}">
    <jca:wizardStep action="editAttributesWizStep" type="object"/>
</jca:wizard>
<% } // end if bPermit %>

<%--- If we are not DTI then add the applet for doing file browsing and file uploads --%>
<wctags:fileSelectionAndUploadAppletUnlessMSOI forceApplet='${param.addAttachments != null }'/>
<SCRIPT>
PTC.wizard.getContentAreaPaddingHeight = PTC.wizard.getContentAreaPaddingHeight.wrap(function(orig) {
return orig.call(this) + 12;
});
	setInterval(getValue,100);
	function getValue(){
		var subCategory = document.getElementById('subCategory');
		var value=subCategory.options[subCategory.selectedIndex].value;
		if(value==''){
			hideAttribute();
		}else{
			showAttribute();
			clearAttributeValue();
		}
	}
	
	function hideAttribute(){
		document.getElementById("Cell_Capacity").parentNode.parentNode.hide();
		document.getElementById("Group_Type").parentNode.parentNode.hide();
		document.getElementById("Cell_ModelNumber").parentNode.parentNode.hide();
		document.getElementById("CellModulePACK_weight").parentNode.parentNode.hide();
		//document.getElementById("Working_Condition").parentNode.parentNode.hide();
		document.getElementById("CellSize").parentNode.parentNode.hide();
		document.getElementById("MoudleType").parentNode.parentNode.hide();
		document.getElementById("AnalysisSoftware").parentNode.parentNode.hide();
	}
	  
	function showAttribute(){
  		var catl_DocType = document.getElementById('CATL_DocType');
  		var value=catl_DocType.options[catl_DocType.selectedIndex].value;
  		var subCategory = document.getElementById('subCategory');
		var subvalue=subCategory.options[subCategory.selectedIndex].value;
		//alert(subvalue);
		if(subvalue.indexOf("-MSM")>=0){
			document.getElementById("Group_Type").parentNode.parentNode.hide();
			document.getElementById("Cell_ModelNumber").parentNode.parentNode.show();
			document.getElementById("Cell_Capacity").parentNode.parentNode.show();
	  		document.getElementById("CellModulePACK_weight").parentNode.parentNode.hide();
	  		document.getElementById("CellSize").parentNode.parentNode.hide();
    		document.getElementById("MoudleType").parentNode.parentNode.show();
    		document.getElementById("AnalysisSoftware").parentNode.parentNode.show();
		}else if(subvalue.indexOf("-MSR")>=0){
			if(value=='cell'){
	  			//document.getElementById("Cell_ModelNumber").parentNode.parentNode.show();
	  			document.getElementById("Group_Type").parentNode.parentNode.hide();    	  			
	  		}else{
	  			//document.getElementById("Cell_ModelNumber").parentNode.parentNode.hide();
	  			document.getElementById("Group_Type").parentNode.parentNode.show();
	  		}
			document.getElementById("Cell_ModelNumber").parentNode.parentNode.show();
	  		document.getElementById("Cell_Capacity").parentNode.parentNode.show();
	  		document.getElementById("CellModulePACK_weight").parentNode.parentNode.show();
	  		document.getElementById("CellSize").parentNode.parentNode.hide();
    		document.getElementById("MoudleType").parentNode.parentNode.hide();
    		document.getElementById("AnalysisSoftware").parentNode.parentNode.hide();
    		
		}else if(subvalue.indexOf("-TMSR")>=0){
	  		
			document.getElementById("Group_Type").parentNode.parentNode.hide();
			document.getElementById("Cell_ModelNumber").parentNode.parentNode.hide();
			document.getElementById("Cell_Capacity").parentNode.parentNode.hide();
	  		document.getElementById("CellModulePACK_weight").parentNode.parentNode.hide();
	  		document.getElementById("CellSize").parentNode.parentNode.hide();
    		document.getElementById("MoudleType").parentNode.parentNode.hide();
    		document.getElementById("AnalysisSoftware").parentNode.parentNode.hide();
    		
		}else if(subvalue.indexOf("-TMSM")>=0){
  		
			document.getElementById("Group_Type").parentNode.parentNode.hide();
			document.getElementById("Cell_ModelNumber").parentNode.parentNode.hide();
			document.getElementById("Cell_Capacity").parentNode.parentNode.hide();
	  		document.getElementById("CellModulePACK_weight").parentNode.parentNode.hide();
	  		document.getElementById("CellSize").parentNode.parentNode.hide();
    		document.getElementById("MoudleType").parentNode.parentNode.hide();
    		document.getElementById("AnalysisSoftware").parentNode.parentNode.hide();
		}
  		
  		//document.getElementById("Working_Condition").parentNode.parentNode.show();
  	}
  	
  	function clearAttributeValue(){
  		var catl_DocType = document.getElementById('CATL_DocType');
  		var value=catl_DocType.options[catl_DocType.selectedIndex].value;
  		var subCategory = document.getElementById('subCategory');
		var subvalue=subCategory.options[subCategory.selectedIndex].value;
		//alert(subvalue);
		if(subvalue.indexOf("-MSM")>=0){
			document.getElementById("Group_Type").value='';
	  		document.getElementById("CellModulePACK_weight").value='';
	  		document.getElementById("CellSize").value='';
		}else if(subvalue.indexOf("-MSR")>=0){
			document.getElementById("CellSize").value='';
    		document.getElementById("MoudleType").value='';
    		document.getElementById("AnalysisSoftware").value='';
			if(value=='cell'){
	  			document.getElementById("Group_Type").value='';    	  			
	  		}
		}else if(subvalue.indexOf("-TMSR")>=0){
			document.getElementById("Group_Type").value='';
			document.getElementById("Cell_ModelNumber").value='';
			document.getElementById("Cell_Capacity").value='';
	  		document.getElementById("CellModulePACK_weight").value='';
	  		document.getElementById("CellSize").value='';
    		document.getElementById("MoudleType").value='';
    		document.getElementById("AnalysisSoftware").value='';
		}else if(subvalue.indexOf("-TMSM")>=0){
			document.getElementById("Group_Type").value='';
			document.getElementById("Cell_ModelNumber").value='';
			document.getElementById("Cell_Capacity").value='';
	  		document.getElementById("CellModulePACK_weight").value='';
	  		document.getElementById("CellSize").value='';
    		document.getElementById("MoudleType").value='';
    		document.getElementById("AnalysisSoftware").value='';
		}
  		
  	}  
	
	function loadHarnessVariant() {
        var xmlhttp;
        var carList=document.getElementById("subCategory");
        var model=carList.options[carList.selectedIndex].value;
        var modelList=document.getElementById("CATL_DocType");
		
        while (modelList.options.length) {
            modelList.remove(0);
        }
        if (window.XMLHttpRequest) {
        // code for IE7+, Firefox, Chrome, Opera, Safari
          xmlhttp=new XMLHttpRequest();
        }else {
          // code for IE6, IE5
          xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
        }
        
        xmlhttp.onreadystatechange=function() {
	
			if (xmlhttp.readyState==4 && xmlhttp.status==200) {
				var string = (xmlhttp.responseText).replace(/\s+/g,'');
				var cars = string.split(',');
				for (var i=0;i<cars.length;i++) {
					if (cars[i]=="") {
					  var dd=decodeURIComponent("空");
					  var car=new Option(" <NO TYPE> ","");  
					}else {
					  var car=new Option(cars[i],cars[i]);
					}
					modelList.options.add(car);
				}
            
            }
  }
  xmlhttp.open("GET","netmarkets/jsp/catl/doc/loadDocType.jsp?model="+encodeURI(encodeURI(model)),true);
  xmlhttp.send();
  //ææ¡£å¤±ææµç¨ 
  //showOrHideENW(carList);
      }
</SCRIPT>

<%@include file="/netmarkets/jsp/util/end.jspf"%>
