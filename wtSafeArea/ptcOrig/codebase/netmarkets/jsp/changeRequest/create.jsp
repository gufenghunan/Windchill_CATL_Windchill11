<%@taglib   prefix="jca"         uri="http://www.ptc.com/windchill/taglib/components"
%><%@taglib prefix="c"           uri="http://java.sun.com/jsp/jstl/core"
%><%@taglib prefix="cwiz"        uri="http://www.ptc.com/windchill/taglib/changeWizards"
%><%@taglib prefix="attachments" uri="http://www.ptc.com/windchill/taglib/attachments"
%><%@include file="/netmarkets/jsp/components/beginWizard.jspf"%>

<%@include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@include file="/netmarkets/jsp/change/propagationConfiguration.jspf"%>

<jca:initializeItem operation="${createBean.create}" attributePopulatorClass="com.ptc.windchill.enterprise.change2.forms.populators.FlexibleChangeRequestAttributePopulator"/>


<%@include file="/netmarkets/jsp/change/changeWizardConfig.jspf" %>
<%@include file="/netmarkets/jsp/annotation/wizardConfig.jspf" %>
<%@include file="/netmarkets/jsp/attachments/initAttachments.jspf"%>

<cwiz:initializeChangeWizard changeMode="CREATE" varianceEffectivity="false" annotationUIContext="change" changeItemClass="wt.change2.ChangeRequestIfc" />

<jca:wizard helpSelectorKey="change_createChangeRequest" buttonList="DefaultWizardButtonsWithSubmitPrompt" formProcessorController="com.ptc.windchill.enterprise.change2.forms.controllers.ChangeItemFormProcessorController" wizardSelectedOnly="true">
 	<jca:wizardStep action="setChangeContextWizStep" type="change"/>
	<jca:wizardStep action="defineItemAttributesWizStep" type="object"/>
	<jca:wizardStep action="securityLabelStep" type="securityLabels"/>
	<jca:wizardStep action="affectedEndItemsStep" type="change" />
	<jca:wizardStep action="affectedDataStep" type="change" />
	<jca:wizardStep action="attachments_step" type="attachments" />
	<jca:wizardStep action="associatedChangeIssuesStep" type="changeRequest" />
	<jca:wizardStep action="associatedChangeItemsStep" type="change" />
</jca:wizard>

<attachments:fileSelectionAndUploadApplet/>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
