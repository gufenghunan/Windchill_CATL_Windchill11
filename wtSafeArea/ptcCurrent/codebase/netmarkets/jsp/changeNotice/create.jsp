<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components" 
%><%@ taglib prefix="cwiz" uri="http://www.ptc.com/windchill/taglib/changeWizards" 
%><%@ taglib uri="http://www.ptc.com/windchill/taglib/attachments" prefix="attachments"
%><%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>

<%@include file="/netmarkets/jsp/components/includeWizBean.jspf"%> 
<%@include file="/netmarkets/jsp/change/propagationConfiguration.jspf"%>

<jca:initializeItem operation="${createBean.create}" attributePopulatorClass="com.ptc.windchill.enterprise.change2.forms.populators.ChangeNoticeAttributePopulator" />

<%@include file="/netmarkets/jsp/change/changeWizardConfig.jspf"%>
<%@include file="/netmarkets/jsp/attachments/initAttachments.jspf"%>

<cwiz:initializeChangeWizard changeMode="CREATE" annotationUIContext="change" changeItemClass="wt.change2.ChangeOrderIfc" />
<cwiz:initializeSelectedItems />

<SCRIPT LANGUAGE="JavaScript">
	var storeIframes = true;
	var iframeTableId = "changeNotice.wizardImplementationPlan.table"; 
	var changeNotice = true;
	PTC.wizardIframes.initStoreIframes();
</SCRIPT>

<jca:wizard helpSelectorKey="change_createChangeNotice" buttonList="DefaultWizardButtonsWithSubmitPrompt" formProcessorController="com.ptc.windchill.enterprise.change2.forms.controllers.ChangeTaskTemplatedFormProcessorController" wizardSelectedOnly="true">
	<%-->Create Change Notice<--%>
 	<jca:wizardStep action="setChangeContextWizStep" type="change"/>	
	<jca:wizardStep action="defineItemAttributesWizStep" type="object"/>
	<jca:wizardStep action="securityLabelStep" type="securityLabels"/>
	<jca:wizardStep action="create_wizardImplementationPlanStep" type="changeNotice" />
	<jca:wizardStep action="attachments_step" type="attachments" />
	<jca:wizardStep action="associatedChangeRequestsStep" type="changeNotice" />
</jca:wizard>

<cwiz:validateChangeTaskTypes/>

<attachments:fileSelectionAndUploadApplet/>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>