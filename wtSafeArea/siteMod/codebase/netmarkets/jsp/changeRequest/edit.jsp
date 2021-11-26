<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"
%><%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"
%><%@taglib prefix="cwiz" uri="http://www.ptc.com/windchill/taglib/changeWizards"
%><%@ taglib prefix="rwiz" uri="http://www.ptc.com/windchill/taglib/reservation"
%><%@ taglib uri="http://www.ptc.com/windchill/taglib/attachments" prefix="attachments"
%><%@include file="/netmarkets/jsp/components/beginWizard.jspf"%>

<%@include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<jca:initializeItem operation="${createBean.edit}"/>

<%@include file="/netmarkets/jsp/change/changeWizardConfig.jspf" %>
<%@include file="/netmarkets/jsp/annotation/wizardConfig.jspf" %>
<%@include file="/netmarkets/jsp/attachments/initAttachments.jspf"%>

<cwiz:initializeChangeWizard changeMode="EDIT" varianceEffectivity="false" annotationUIContext="change" changeItemClass="wt.change2.ChangeRequestIfc" />

<jca:wizard  helpSelectorKey="change_editChangeRequest" buttonList="DefaultWizardButtonsWithSubmitPrompt" formProcessorController="com.ptc.windchill.enterprise.change2.forms.controllers.ChangeItemFormProcessorController">
	<jca:wizardStep action="editAttributesWizStep" type="object" />
	<!-- jca:wizardStep action="affectedEndItemsStep" type="change" /-->
	<jca:wizardStep action="affectedDataStep" type="change" />
	<jca:wizardStep action="attachments_step" type="attachments" />
	<jca:wizardStep action="associatedChangeIssuesStep" type="changeRequest" />
	<jca:wizardStep action="associatedChangeItemsStep" type="change" />
</jca:wizard>

<rwiz:handleUpdateCount/>
<rwiz:configureReservation reservationType="modify" enforcedByService="true" workflowOverride="true"/>

<attachments:fileSelectionAndUploadApplet/>

<script language='Javascript'>
   change_postLoad();
</script>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
