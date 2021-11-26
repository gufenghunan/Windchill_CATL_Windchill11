<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"
%><%@ taglib prefix="cwiz" uri="http://www.ptc.com/windchill/taglib/changeWizards"
%><%@taglib uri="http://www.ptc.com/windchill/taglib/effectivity"  prefix="eff"
%><%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@ page import="com.catl.common.constant.ChangeState" %>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="com.catl.change.*"%>
<%@page import="com.catl.change.util.*"%>
<%@page import="wt.change2.WTChangeActivity2"%>

<jca:initializeItem operation="${createBean.edit}"/>

<%@include file="/netmarkets/jsp/change/changeWizardConfig.jspf" %>
<%@include file="/netmarkets/jsp/annotation/wizardConfig.jspf" %>

<cwiz:initializeChangeWizard changeMode="EDIT" annotationUIContext="change" changeItemClass="wt.change2.ChangeActivityIfc"/>

<eff:copyEffectivityLists />
<% 
	String oid = request.getParameter("oid");
	ReferenceFactory rf = new ReferenceFactory();
	Object obj = rf.getReference(oid).getObject();
	
 	boolean isCanEdit = true;

 	WTChangeActivity2 eca = (WTChangeActivity2)obj;
 	String dcatype = ChangeUtil.getStrSplit(eca);
   	
	String ecaState =eca.getState().toString();
  	if(ecaState.equalsIgnoreCase(ChangeState.IMPLEMENTATION))
      isCanEdit = false;
   	System.out.println("isCanEdit"+isCanEdit);
%>
<c:choose>
	<c:when test='<%=isCanEdit%>'>
		<jca:wizard helpSelectorKey="change_editChangeTask" buttonList="DefaultWizardButtonsNoApply" formProcessorController="com.ptc.windchill.enterprise.change2.forms.controllers.EffectivityAwareIframeFormProcessorController">
			<%-->Create Change Task<--%>
			<jca:wizardStep action="editAttributesWizStep" type="object" />
			<jca:wizardStep action="affectedAndResultingItemsStep" type="changeTask" />
			<jca:wizardStep action="changeInventoryStep" type="catlInventory" label="<%=WTMessage.getLocalizedMessage(\"com.catl.change.inventory.resource.ChangeInventoryRB\",\"changeInventory.tableName\") %>" />
    	<jca:wizardStep action="associatedChangeIssuesStep" type="changeTask" />
		</jca:wizard>
	</c:when>
	<c:otherwise>
		<jca:wizard helpSelectorKey="change_editChangeTask" buttonList="DefaultWizardButtonsNoApply" formProcessorController="com.ptc.windchill.enterprise.change2.forms.controllers.EffectivityAwareIframeFormProcessorController">
			<%-->Create Change Task<--%>
			<jca:wizardStep action="viewAttributesWizStep" type="changeTask" label="<%=WTMessage.getLocalizedMessage(\"com.catl.change.inventory.resource.ChangeInventoryRB\",\"changeTask.tabName\") %>" />
			<jca:wizardStep action="affectedAndResultingItemsStep" type="changeTask" />
			<jca:wizardStep action="changeInventoryStep" type="catlInventory" label="<%=WTMessage.getLocalizedMessage(\"com.catl.change.inventory.resource.ChangeInventoryRB\",\"changeInventory.tableName\") %>" />
    	<jca:wizardStep action="associatedChangeIssuesStep" type="changeTask" />
		</jca:wizard>
	</c:otherwise>
</c:choose>


<cwiz:initializeConcurrentUpdateSupport callbackFunction="validateCount" />

<%
	//DCA
	if(dcatype.equals(ChangeConst.CHANGETASK_TYPE_DCA)){
%>
<script language='Javascript'>
	
    PTC.onReady(changeTaskLoadHandler.edit);
    PTC.wizard.fireOnAfterAction = PTC.wizard.fireOnAfterAction.wrap(PTC.change.handleFormResult);
	PTC.onReady(function() {
		var caname=document.getElementById("change_name").value;
		if(caname.length == 11){
			insertStep("changeInventoryStep");
			removeStep("affectedAndResultingItems");
			
		}else{
			//affectedAndResultingItems!~objectHandle~task~!
			insertStep("changeInventoryStep");
			removeStep("changeInventoryStep");
		}
	});  
	
</script>

<%
	}else{
%>
<script language='Javascript'>
    PTC.onReady(changeTaskLoadHandler.edit);
    PTC.wizard.fireOnAfterAction = PTC.wizard.fireOnAfterAction.wrap(PTC.change.handleFormResult);
	PTC.onReady(function() {
		var carList=document.getElementById("change_name");
		
		if(carList.selectedIndex!=10){
			insertStep("affectedAndResultingItems");
			removeStep("changeInventoryStep");
		}else{
			insertStep("changeInventoryStep");
			removeStep("affectedAndResultingItems");
		}
	});    
</script>

<% 
	}
%>

<script type="text/javascript">
function loadHarnessVariant() {
	
	var xmlhttp;
	var carList=document.getElementById("change_name");
	var model=carList.options[carList.selectedIndex].value;
	var tb = document.getElementById("change_name_TB");

	var type ="loadDTaskDesNames";
	<%if("wt.change2.WTChangeActivity2|com.CATLBattery.CATLDChangeActivity2".equals(dcatype)){
		System.out.println("is here...................");
	%> 
		type = "loadDTaskDesNames";
	<%}else{%>
		type = "loadTaskDesNames";
	<%}
		System.out.println("is here...................");
	%>
	
	if(carList.selectedIndex!=10){
		insertStep("affectedAndResultingItems");
		removeStep("changeInventoryStep");
	}else{
	    insertStep("changeInventoryStep");
		removeStep("affectedAndResultingItems");
	}

	if(tb){
		tb.value=model;
	}
	var modelList=document.getElementById("taskDescription");
	
	while (modelList.options.length) {
		modelList.remove(0);
	}
	
	if (window.XMLHttpRequest) {
		// code for IE7+, Firefox, Chrome, Opera, Safari
		xmlhttp=new XMLHttpRequest();
	}
	else {
		// code for IE6, IE5
		xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	}
	
	xmlhttp.onreadystatechange=function() {

		if (xmlhttp.readyState==4 && xmlhttp.status==200) {
			var string = (xmlhttp.responseText).replace(/\s+/g,'');
			var cars = string.split(',');
			for (var i=0;i<cars.length;i++)	{
					var car=new Option(cars[i],cars[i]);
					modelList.options.add(car);
				}
			
		}
	}
	
	xmlhttp.open("GET","netmarkets/jsp/catl/changeTask/"+type+".jsp?model="+encodeURI(encodeURI(model)),true);
	xmlhttp.send();
}
</script>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>