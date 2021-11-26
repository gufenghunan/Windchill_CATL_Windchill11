<!-- bcwti
 *
 * Copyright (c) 2006 Parametric Technology Corporation (PTC). All Rights
 * Reserved.
 *
 * This software is the confidential and proprietary information of PTC.
 * You shall not disclose such confidential information and shall use it
 * only in accordance with the terms of the license agreement.
 *
 * ecwti
 * -->
<%@ page import="wt.content.ContentHolder" %>
<%@ page import="wt.access.AccessPermission" %>
<%@ page import="com.ptc.windchill.enterprise.attachments.server.AttachmentsHelper" %>

<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@ taglib prefix="wip" uri="http://www.ptc.com/windchill/taglib/workinprogress"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>
<%@ taglib  uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>


<%-->
PartHelper.js below is required dynamically insert/remove the classification step
<--%>
<script language="JavaScript" src="netmarkets/javascript/part/PartHelper.js"></script>

<fmt:setBundle basename="com.ptc.windchill.enterprise.part.partResource"/>

<%--> Reuse the same labels as for create part <--%>
<fmt:message var="editAttributesWizStepLabel" key="part.createPartWizard.SET_ATTRIBUTES_WIZ_STEP_LABEL" />

<%
if (InstalledProperties.isInstalled(InstalledProperties.PARTSLINK)) {
%>
<jca:initializeItem operation="${createBean.edit}" attributePopulatorClass="com.ptc.windchill.partslink.part.forms.EditPartAttributePopulator"/>
<%} else { %>
<jca:initializeItem operation="${createBean.edit}"/>
<% } %>

<% if (request.getParameter("newInWorkspace") == null) { %>
  <%--> The part is not new in workspace. Do autoCheckout <--%>
  <wip:autoCheckOutItem/>
<%  } %>

<% if ((request.getParameter("newInWorkspace") != null) || (request.getParameter("checkedOutInWorkspace") != null)) { %>
  <script language="Javascript">newOrCheckedOutInWorkspace=true</script>
<% } %>

<%
    boolean bPermit = false;
    Object context = commandBean.getPageOid().getRef();
    bPermit = (context instanceof ContentHolder) && AttachmentsHelper.hasPermission((ContentHolder) context, AccessPermission.MODIFY_CONTENT);

%>

<%-->If SoftwareLink, PartsLink, or PDMLink is installed then use button set with Back and Next buttons<--%>
<% if (InstalledProperties.isInstalled(InstalledProperties.PARTSLINK) ||
       InstalledProperties.isInstalled(InstalledProperties.PDMLINK) ){ %>
    <c:set var="buttonSet" value="EditWizardButtons"/>
<% } else { %>
    <c:set var="buttonSet" value="NoStepsEditWizardButtons"/>
<% } %>
<% if (bPermit) { %>
<jca:wizard buttonList="${buttonSet}" helpSelectorKey="PartEdit_help">

   <%-->
    The type for the editAttributes step below
    must be 'object' and not 'part'. This needs to be done so that
    we pick up the common component action definitions for this
    step.
    Updated the editAttributes step type to 'part' for adding attachments step. B-91021
    <--%>
    <jca:wizardStep action="editAttributesWizStep" label="${editAttributesWizStepLabel}" type="part"/>
    <jca:wizardStep action="setClassificationAttributesWizStep" type="part"/>
    <jca:wizardStep action="attachments_step" type="attachments" />
</jca:wizard>
<% } else { %>
<jca:wizard buttonList="${buttonSet}" helpSelectorKey="PartEdit_help">
    <jca:wizardStep action="editAttributesWizStep" label="${editAttributesWizStepLabel}" type="part"/>
    <jca:wizardStep action="setClassificationAttributesWizStep" type="part"/>
</jca:wizard>
<% } // end if bPermit %>


<%--- If we are not DTI then add the applet for doing file browsing and file uploads --%>
<wctags:fileSelectionAndUploadAppletUnlessMSOI forceApplet='${param.addAttachments != null }'/>

<input id="selectedClfNodes" type="hidden" name="selectedClfNodes" >
<input id="selectedClfNodesDisplayName" type="hidden" name="selectedClfNodesDisplayName" >
<input id="enforceClassificationNamingRule" type="hidden" name="enforceClassificationNamingRule" >
<input id="classificationNameOverride" type="hidden" name="classificationNameOverride" >

<% if (InstalledProperties.isInstalled(InstalledProperties.PARTSLINK)) { %>
	<script language="Javascript">partsLinkInstalled=true</script>
<% } %>

<%@include file="/netmarkets/jsp/util/end.jspf"%>
<script language="Javascript">
    PTC.onReady(onloadEditPartWizard);
	    
       $2 = function(id) {
	    var r = $(id);

	    if(!r) {
	        var ar = Ext.DomQuery.select("[name='"+id+"']");
	        if(ar.length > 0) r = ar[0]; //get the first element
	    }
	    return r;
	}

	function replaceFormAction(){
	    var processor = "com.catl.part.EditCatlPartProcessor";
	    var form = getMainForm();
	    
	    !$2('wizardActionClass') ? JCAappendFormHiddenInput(form, 'wizardActionClass' , processor) : $2('wizardActionClass').value= processor;
	    form.action = removeParamFromString("wizardActionClass",form.action);
	    form.action += '&wizardActionClass='+processor;
	}

	    setUserSubmitFunction(
		function() {
	    replaceFormAction();
	  });
	    
	    PTC.csm.classificationPickerCallBack = function(pickerObject, fieldId, attr, displayFieldId) {
		  	var isMultiPart = false;
		  	var clsnode;
		  	PTC.search.picker.structuredEnumerationCallBack(pickerObject, fieldId, attr, displayFieldId);
		  	PTC.attributePanel.resizeGroupWidth = PTC.attributePanel.resizeGroupWidth.wrap(function(proceed, group,
		  	            resizedWidth) {
		  	    PTC.csm.resizeNestedGroupWidth(group, resizedWidth);
		  	});
		  	insertStep("setClassificationAttributesWizStep" + partObjectHandle);
		  	setStepDirty("setClassificationAttributesWizStep" + partObjectHandle);

		  	// this is to update the DOM for the classification picker field.
		  	// PTC.wizard.saveTableData.callDomChangeListener(document.getElementById(displayFieldId));
		  	PTC.wizard.saveTableData.saveToStore(document.getElementById(displayFieldId));

		  	// delete form content of classification attributes step once the driver
		  	// attribute is changed.
		  	deleteDownstreamFormContent();

		  	//restrict autoNaming for multiPart wizard.
		  	if(document.getElementById("wizType")){
		  		if(document.getElementById("wizType").value == 'multiPart'){
		  				isMultiPart = true;
		  		}
		  	}
		  	PTC.csm.logger.debug(" isMultiPart :: " + isMultiPart);
		  	if(!isMultiPart){
		  		//newly selected value
		  		var currentPickedNode = pickerObject.pickedObject[0].name;  //display name
		  		var isRuleEnforcedLocal = pickerObject.pickedObject[0].enforceAutoNamingRule;
		  		PTC.csm.logger.debug(" processing picker :: " + fieldId);
		  		clsnode = pickerObject.pickedObject[0].internalName;

		  		if(currentPickedNode!=='Node name'){
		  			// process if not empty
		  			PTC.csm.nodeBean = new Object();
		  			// fill bean
		  			PTC.csm.nodeBean.selectedNode = currentPickedNode;
		  			PTC.csm.nodeBean.isRuleEnforced = isRuleEnforcedLocal;
		  			// fill map - it holds fieldId as a key and value as bean consists of {nodeName , isRuleEnforced}.
		  			PTC.csm.logger.debug(" added :: " + fieldId + " into map");
		  			PTC.csm.PickerAndValueMap[fieldId] = PTC.csm.nodeBean;
		  		}

		  		PTC.csm.logger.debug(" map size :: " + Object.keys(PTC.csm.PickerAndValueMap).length);
		  		//This is to decide whether to show (Generated) for name or not.
		  		PTC.csm.logger.debug(" Calling PTC.csm.shouldAutoGeneratePartName .. ");
		  		PTC.csm.shouldAutoGeneratePartName();
		  		
		  		//loadHarnessVariant(pickerObject.pickedObject[0].internalName);
		  		var xmlhttp;

		  		var sourceList=document.getElementById("source");      
		        
		        while (sourceList.options.length) {
		        	sourceList.remove(0);
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
		           // alert(string);
		            var namesource = string.split('qqqq;;;;');
		            if(namesource.length == 2){
		            	var names = namesource[0];
			            var source = namesource[1];
			            //alert(namesource[0]);
			            //alert(namesource[1]);
			            
			            var sources = source.split('|');
			            for (var i=0;i<sources.length;i++) {
			            	var car;
			            	if(i==0){
			            		var car0=new Option("","");
			            		sourceList.options.add(car0);
			            	}
			                if (sources[i]=="") {
			                  var dd=decodeURIComponent("NULL");
			                  car=new Option("","");  
			                }else {
			                  var sourcevalue = sources[i].split(',');
			                  car=new Option(sourcevalue[1],sourcevalue[0]);
			                }
			                sourceList.options.add(car);
			              }
		            }else{
		            	alert('\u8bf7\u8054\u7cfb\u7ba1\u7406\u5458\u68c0\u67e5\u7269\u6599\u7ec4\u540d\u79f0\u91c7\u8d2d\u7c7b\u578b\u56fa\u5316\u8868\u662f\u5426\u5df2\u7ecf\u7ef4\u62a4\uff01');
		            }
		            
		            
		          }
		  		}
		  		xmlhttp.open("GET","netmarkets/jsp/catl/part/loadPartNameSource.jsp?clsnode="+encodeURI(encodeURI(clsnode)),true);
		  		xmlhttp.send();
		  	}
		  };
</script>
