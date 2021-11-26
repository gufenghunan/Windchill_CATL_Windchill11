<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"
%><%@ taglib prefix="cwiz" uri="http://www.ptc.com/windchill/taglib/changeWizards"
%><%@ taglib uri="http://www.ptc.com/windchill/taglib/attachments" prefix="attachments"
%><%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@page import="wt.change2.WTChangeOrder2"%>
<%@page import="com.catl.change.ChangeUtil"%>
<%@page import="com.catl.change.util.*"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.org.WTPrincipal"%>
<%@page import="com.catl.change.filter.CatlEditChangeTasckValidation"%>

<%@include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<jca:initializeItem operation="${createBean.edit}"/>

<%@include file="/netmarkets/jsp/change/changeWizardConfig.jspf" %>
<%@include file="/netmarkets/jsp/attachments/initAttachments.jspf"%>

<cwiz:initializeChangeWizard changeMode="EDIT" annotationUIContext="change" changeItemClass="wt.change2.ChangeOrderIfc" />

<SCRIPT LANGUAGE="JavaScript">
	var storeIframes = true;
	var iframeTableId = "changeNotice.wizardImplementationPlan.table";
	var changeNotice = true;
	PTC.wizardIframes.initStoreIframes();
</SCRIPT>
<%
	boolean isAdmin = false;
	WTPrincipal userPrincipal = SessionHelper.manager.getPrincipal();
	if(CatlEditChangeTasckValidation.isSiteAdmin(userPrincipal) || CatlEditChangeTasckValidation.isOrgAdministator(userPrincipal, "CATL")){
		isAdmin = true;
	}
	String oid = request.getParameter("oid");
	ReferenceFactory rf = new ReferenceFactory();
	Object obj = rf.getReference(oid).getObject();
	
	boolean isCanEdit = true;

	WTChangeOrder2 eco = (WTChangeOrder2)obj;
	String ecotype = ChangeUtil.getStrSplit(eco);
	
%>
<%
	if(ecotype.equals(ChangeConst.CHANGEORDER_TYPE_DCN) && !isAdmin){
%>
<jca:wizard helpSelectorKey="change_editChangeNotice" buttonList="DefaultWizardButtonsWithSubmitPrompt" formProcessorController="com.ptc.windchill.enterprise.change2.forms.controllers.EffectivityAwareIframeFormProcessorController">
	<%-->Create Change Notice<--%>
	<jca:wizardStep action="editAttributesWizStep" type="object" />
	<jca:wizardStep action="edit_wizardImplementationPlanStep" type="changeNotice" />
</jca:wizard>
<%
	}else{
%>
<jca:wizard helpSelectorKey="change_editChangeNotice" buttonList="DefaultWizardButtonsWithSubmitPrompt" formProcessorController="com.ptc.windchill.enterprise.change2.forms.controllers.EffectivityAwareIframeFormProcessorController">
	<%-->Create Change Notice<--%>
	<jca:wizardStep action="editAttributesWizStep" type="object" />
	<jca:wizardStep action="edit_wizardImplementationPlanStep" type="changeNotice" />
	<jca:wizardStep action="attachments_step" type="attachments" />
	<jca:wizardStep action="associatedChangeRequestsStep" type="changeNotice" />
	<jca:wizardStep action="associatedChangeItemsStep" type="change" />
</jca:wizard>
<%
	}
%>
<cwiz:initializeConcurrentUpdateSupport callbackFunction="validateCount" />

<attachments:fileSelectionAndUploadApplet/>

<script language='Javascript'>
   change_postLoad();
</script>


<%@ include file="/netmarkets/jsp/util/end.jspf"%>




