<%@page import="com.cwbc.promotion.resource.promotionResource"%>
<%@page import="wt.util.WTMessage"%>
<%@taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@taglib prefix="pwiz" uri="http://www.ptc.com/windchill/taglib/promotionRequestWizards"%>
<%@include file="/netmarkets/jsp/components/beginWizard.jspf"%>

<%@include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<%@page import="com.ptc.windchill.enterprise.maturity.PromotionRequestHelper"%>
<%@page import="com.ptc.core.components.util.RequestHelper"%>
<%@page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>

<script language='JavaScript' type="text/javascript" src='netmarkets/javascript/promotionRequest/promotionRequest.js' ></script>
<script language='JavaScript' type="text/javascript" src='netmarkets/javascript/wizardParticipant/participants.js' ></script>
<script language='JavaScript' type="text/javascript" src='netmarkets/javascript/promotionRequest/promotionParticipants.js' ></script>

<% 
	NmCommandBean clientData = RequestHelper.getCommandBean(request);
	String actionName = clientData.getTextParameter("actionName");
	String title = "";
	String protype = "";
	String attributePopulatorClass = "com.ptc.windchill.enterprise.maturity.forms.populators.PromotionNameAttributePopulator";
	if ("createDisabledForDesignPN".equals(actionName)) {
		title = WTMessage.getLocalizedMessage("com.catl.promotion.resource.promotionResource", promotionResource.PROMOTION_CONSTANT_17);
		protype = "com.CATLBattery.DisabledForDesignPN";
		attributePopulatorClass = "com.catl.promotion.populator.PNPromotionNameAttributePopulator";
	}else if ("createFAEMaterialsMaturity".equals(actionName)) {
		title = WTMessage.getLocalizedMessage("com.catl.promotion.resource.promotionResource", promotionResource.PROMOTION_CONSTANT_12);
		protype = "com.CATLBattery.UpgradeFaePartMaturityPN";
		attributePopulatorClass = "com.catl.promotion.populator.FAEPromotionNameAttributePopulator";
	}else if ("createSourceChangePN".equals(actionName)) {
		title = WTMessage.getLocalizedMessage("com.catl.promotion.resource.promotionResource", promotionResource.PROMOTION_CONSTANT_13);
		protype = "com.CATLBattery.SourceChangePN";
		attributePopulatorClass = "com.catl.promotion.populator.SCPromotionNameAttributePopulator";
	}else if ("createPlatformChangePN".equals(actionName)) {
		title = WTMessage.getLocalizedMessage("com.catl.promotion.resource.promotionResource", promotionResource.PROMOTION_CONSTANT_18);
		protype = "com.CATLBattery.PlatformChangePN";
	}else {
		protype = com.catl.common.util.CommonUtil.checkPromotionType(clientData);
	}
%>
<c:set var="protype" value="<%=protype %>"/>
<c:set var="titleValue" value="<%=title %>"/>
<jca:initializeItem  baseTypeName="wt.maturity.PromotionNotice|${protype}"  operation="${createBean.create}" attributePopulatorClass="<%=attributePopulatorClass %>"/>

<jsp:setProperty name="createBean" property="contextPickerTypeComponentId" value="<%=PromotionRequestHelper.getPromotablePickerContainerTypeId()%>"/>

<pwiz:initializePromoteWizard />

<% 
if(protype.equals("com.CATLBattery.UpgradeFaePartMaturityPN") || protype.equals("com.CATLBattery.DisabledForDesignPN") || protype.equals("com.CATLBattery.SourceChangePN") ){
%>
<jca:wizard helpSelectorKey="maturity_createPromotionRequest" title="${titleValue}" buttonList="DefaultWizardButtonsNoApply" wizardSelectedOnly="true">
   <jca:wizardStep action="setContextWizStep" type="object"/>
   <jca:wizardStep action="defineItemAttributesWizStep" type="object"/> 
   <jca:wizardStep action="promotionObjectsTableStep" type="faeMaturityAndDesignDisabledPromotion" />
</jca:wizard>
<%	
}
else {
%>
<jca:wizard helpSelectorKey="maturity_createPromotionRequest" buttonList="DefaultWizardButtonsNoApply" wizardSelectedOnly="true">
   <jca:wizardStep action="setContextWizStep" type="object"/>
   <jca:wizardStep action="defineItemAttributesWizStep" type="object"/> 
   <jca:wizardStep action="promotionObjectsTableStep" type="promotionRequest" />
   <jca:wizardStep action="workflowParticipantsStep" type="promotionRequest" />
</jca:wizard>
<%} %>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>