<%@page import="com.catl.promotion.util.*"%>
<%@page import="wt.change2.WTChangeRequest2"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.org.WTPrincipal"%>
<%@page import="com.catl.change.filter.CatlEditChangeTasckValidation"%>
<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components" 
%><%@ taglib prefix="cwiz" uri="http://www.ptc.com/windchill/taglib/changeWizards" 
%><%@ taglib uri="http://www.ptc.com/windchill/taglib/attachments" prefix="attachments"
%><%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>

<%@include file="/netmarkets/jsp/components/includeWizBean.jspf"%> 
<%@include file="/netmarkets/jsp/change/propagationConfiguration.jspf"%>

<fmt:setLocale value="${localeBean.locale}" />
<fmt:setBundle basename="com.catl.change.resource.changeActionRB"/>
<fmt:message var="DCN_TITLE"  key="DCN_TITLE"/>

<%
	boolean isAdmin = false;
	WTPrincipal userPrincipal = SessionHelper.manager.getPrincipal();
	if(CatlEditChangeTasckValidation.isSiteAdmin(userPrincipal) || CatlEditChangeTasckValidation.isOrgAdministator(userPrincipal, "CATL")){
		isAdmin = true;
	}
	String isDcn = "ecn";
	//不在变更请求菜单中创建的变更通告都认为是DCN。
	//如果创建的是DCN，则在页面中设置创建的初始化类型为：wt.change2.WTChangeOrder2| com.CATLBattery.CATLDChangeNotice；否则设置为：wt.change2.WTChangeOrder2| com.CATLBattery.CATLChangeNotice
	Object obj = commandBean.getPageOid().getRefObject();
	if(obj instanceof WTChangeRequest2){
%>
	<jca:initializeItem baseTypeName="wt.change2.WTChangeOrder2|com.CATLBattery.CATLChangeNotice" operation="${createBean.create}" attributePopulatorClass="com.ptc.windchill.enterprise.change2.forms.populators.FlexibleChangeNoticeAttributePopulator" />
<%
	}else{
		isDcn = "dcn";
%>
	<jca:initializeItem baseTypeName="wt.change2.WTChangeOrder2|com.CATLBattery.CATLDChangeNotice" operation="${createBean.create}" attributePopulatorClass="com.ptc.windchill.enterprise.change2.forms.populators.FlexibleChangeNoticeAttributePopulator" />
<%
	}
%>

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
<%
	if(isDcn.equals("dcn") && !isAdmin){
%>
<jca:wizard helpSelectorKey="change_createChangeNotice" title="${DCN_TITLE}" buttonList="DefaultWizardButtonsWithSubmitPrompt" formProcessorController="com.ptc.windchill.enterprise.change2.forms.controllers.ChangeTaskTemplatedFormProcessorController" wizardSelectedOnly="true">
	<%-->Create Change Notice<--%>
 	<jca:wizardStep action="setChangeContextWizStep" type="change"/>	
	<jca:wizardStep action="defineItemAttributesWizStep" type="object"/>
	<jca:wizardStep action="securityLabelStep" type="securityLabels"/>
	<jca:wizardStep action="create_wizardImplementationPlanStep" type="changeNotice" />
</jca:wizard>
<%
	}else{
%>
<jca:wizard helpSelectorKey="change_createChangeNotice" buttonList="DefaultWizardButtonsWithSubmitPrompt" formProcessorController="com.ptc.windchill.enterprise.change2.forms.controllers.ChangeTaskTemplatedFormProcessorController" wizardSelectedOnly="true">
	<%-->Create Change Notice<--%>
 	<jca:wizardStep action="setChangeContextWizStep" type="change"/>	
	<jca:wizardStep action="defineItemAttributesWizStep" type="object"/>
	<jca:wizardStep action="securityLabelStep" type="securityLabels"/>
	<jca:wizardStep action="create_wizardImplementationPlanStep" type="changeNotice" />
	<jca:wizardStep action="attachments_step" type="attachments" />
	<jca:wizardStep action="associatedChangeRequestsStep" type="changeNotice" />
	<jca:wizardStep action="associatedChangeItemsStep" type="change" />
</jca:wizard>
<%
	}
%>
<attachments:fileSelectionAndUploadApplet/>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>