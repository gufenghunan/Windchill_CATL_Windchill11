<%@page language="java" session="true" pageEncoding="UTF-8"%><%@
taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%><%@
taglib prefix="pwiz" uri="http://www.ptc.com/windchill/taglib/promotionRequestWizards"%><%@
include file="/netmarkets/jsp/components/beginWizard.jspf"%><%@
include file="/netmarkets/jsp/components/includeWizBean.jspf"%><%@
page import="com.ptc.windchill.enterprise.maturity.PromotionRequestHelper"%><%@
page import="org.apache.commons.lang.StringUtils"%><%@
page import="com.ptc.xworks.windchill.util.ObjectTypeUtils" %><%@
page import="com.ptc.xworks.promotion.builders.PromotionNoticeHelper" %><%@
page import="com.ptc.xworks.xmlobject.web.util.WebUtils" %><%

// set a cookie for this create PromotionNotice Windows
//wt.util.WTProperties props = wt.util.WTProperties.getLocalProperties();
String createWindowId = WebUtils.generateHandleForCreate();
//Cookie cookie = new Cookie("XWORKS_CREATE_PROMOTION_NOTICE_ID", createId);
//cookie.setPath("/" + props.getProperty("wt.webapp.name", "Windchill"));
//response.addCookie(cookie);
%>
<input type="hidden" id="XWORKS_CREATE_PROMOTION_NOTICE_ID" name="XWORKS_CREATE_PROMOTION_NOTICE_ID" value="<%=createWindowId%>" />

<script language='JavaScript' type="text/javascript" src='netmarkets/javascript/promotionRequest/promotionRequest.js' ></script>
<script language='JavaScript' type="text/javascript" src='netmarkets/javascript/wizardParticipant/participants.js' ></script>
<script language='JavaScript' type="text/javascript" src='netmarkets/javascript/promotionRequest/promotionParticipants.js' ></script>

<script type="text/javascript">
popupAction = popupAction.wrap(function (orig, windowName, url, params0, params1, params2, params3, params4, params5, params6, params7, params8, params9, params10, params11, params12, params13, params14, params15, params16, params17, compContext, elemAddress, actionClass, actionMethod, moreInfo, alertMessage, noneMessage, isDCA, shortCut, isAjaxEnabled, ajaxClass, isSelectRequired, tableID) {
	//window.alert(document.getElementById("createType").value);
	var createWindowId = "<%=createWindowId%>";
	orig(windowName, url, "createWindowId", createWindowId, params2, params3, params4, params5, params6, params7, params8, params9, params10, params11, params12, params13, params14, params15, params16, params17, compContext, elemAddress, actionClass, actionMethod, moreInfo, alertMessage, noneMessage, isDCA, shortCut, isAjaxEnabled, ajaxClass, isSelectRequired, tableID);	
});

//var ootbSubmitIt = submitIt;
//var submitIt = function(windowName, url, params0, params1, params2, params3, params4, params5, params6, params7, params8, params9, params10, params11, params12, params13, params14, params15, params16, params17, compContext, elemAddress, actionClass, actionMethod, moreInfo, alertMessage, noneMessage, isDCA, shortCut, isAjaxEnabled, ajaxClass, isSelectRequired, tableID) {
//	var createType = document.getElementById("createType").value;
//	ootbSubmitIt(windowName, url, params0, params1, params2, params3, params4, params5, params6, params7, params8, params9, params10, params11, params12, params13, params14, params15, "promotionNoticeType", createType, compContext, elemAddress, actionClass, actionMethod, moreInfo, alertMessage, noneMessage, isDCA, shortCut, isAjaxEnabled, ajaxClass, isSelectRequired, tableID);
//}

var promotionNoticeConfigs = <%=PromotionNoticeHelper.getOptionsJson()%>;

var toggleTabs = function(fullTypeName) {
	//window.alert(fullTypeName);
	var config = promotionNoticeConfigs[fullTypeName];
	
	// 如果流程申请单没有配置信息
	if (config == undefined) {
		//insertStep("promotionNotice.relatedObjectsTableStep");
		// 在Windchill 11.0 M010 中refreshStep会发生Javascript错误，而且经测试removeStep后再insertStep，
		// 经过此step时会重新请求此Step，因此无需使用代码自行进行刷新，因而注释下面一行的代码		
		//refreshStep("promotionNotice.relatedObjectsTableStep");
		removeStep("promotionNotice.relatedObjectsTableStep");
		removeStep("promotionNotice.setupParticipantsStep");
		removeStep("attachments.attachments_step");
		return false;
	} 

	//window.alert(config.enableRelatedObjectStep);
	if (config.enableRelatedObjectStep) {
		insertStep("promotionNotice.relatedObjectsTableStep");
		if (config.requireRelatedObjectStep) {
			setStepRequired("promotionNotice.relatedObjectsTableStep");
		} else {
			setStepOptional("promotionNotice.relatedObjectsTableStep");
		}
		// 在Windchill 11.0 M010 中refreshStep会发生Javascript错误，而且经测试removeStep后再insertStep，
		// 经过此step时会重新请求此Step，因此无需使用代码自行进行刷新，因而注释下面一行的代码
		//refreshStep("promotionNotice.relatedObjectsTableStep"); 
	} else {
		removeStep("promotionNotice.relatedObjectsTableStep");
	}
	
	if (config.enableSelectParticipantStep) {
		insertStep("promotionNotice.setupParticipantsStep");
		if (config.requireSelectParticipantStep) {
			setStepRequired("promotionNotice.setupParticipantsStep");
		} else {
			setStepOptional("promotionNotice.setupParticipantsStep");
		}
		// 在Windchill 11.0 M010 中refreshStep会发生Javascript错误，而且经测试removeStep后再insertStep，
		// 经过此step时会重新请求此Step，因此无需使用代码自行进行刷新，因而注释下面一行的代码		
		//refreshStep("promotionNotice.setupParticipantsStep");
	} else {
		removeStep("promotionNotice.setupParticipantsStep");
	}
	
	if (config.enableAttachmentStep) {
		insertStep("attachments.attachments_step");
		if (config.requireAttachmentStep) {
			setStepRequired("attachments.attachments_step");
		} else {
			setStepOptional("attachments.attachments_step");
		}
	} else {
		removeStep("attachments.attachments_step");
	}
	//if (promotionNoticeConfigs[fullTypeName])
	
	return true;
};

function reloadSetupParticipantsTreeTable(tableID) {
	//window.alert(tableID);
	var params = {"XWORKS_CREATE_PROMOTION_NOTICE_ID" : document.getElementById("XWORKS_CREATE_PROMOTION_NOTICE_ID").value};
	PTC.jca.table.Utils.reload(tableID, params, true);
}

pickerGo = pickerGo.wrap(function (ootbPickerGo, value, currentObjectHandle, template) {
	toggleTabs(value);
	ootbPickerGo(value, currentObjectHandle, template);
});

PTC.onReady(function() {
	// 初始化Tab
	var createTypeSelector = document.getElementById("createType");
	if (createTypeSelector != undefined) {
		toggleTabs(createTypeSelector.value);
	} else {
		toggleTabs("");
	}
	//window.alert(createTypeSelector.value);

});

onSubmit = onSubmit.wrap( function(ootbOnSubmit,skipAllValidation, skipRequiredFieldValidation) {
	// 检查当前选择的流程申请单类型是否已经有配置信息
	var createType = $("createType").value;
	var config = promotionNoticeConfigs[createType];
	if (config == undefined) {
		//window.alert("Cannot create this type application Form!");
		JCAAlert("com.ptc.xworks.promotion.promotionResource.cannot_create_this_type_application_form");
		return;
	}
	ootbOnSubmit(skipAllValidation, skipRequiredFieldValidation)
});

</script>
<%
String baseTypeName = request.getParameter("baseTypeName");
if (StringUtils.isBlank(baseTypeName)) {
	baseTypeName = "WCTYPE|wt.maturity.PromotionNotice";
}
baseTypeName = ObjectTypeUtils.getExternalTypeName(baseTypeName);
%>

<jca:initializeItem operation="${createBean.create}" baseTypeName="<%=baseTypeName%>" attributePopulatorClass="com.ptc.xworks.promotion.builders.PromotionNoticeNameAttributePopulator"/>

<jsp:setProperty name="createBean" property="contextPickerTypeComponentId" value="<%=PromotionRequestHelper.getPromotablePickerContainerTypeId()%>"/>
<%
// 使用下面的代码行，使类型选择的菜单有层次结构
createBean.setUseHierachicalTypeListInPicker("true", createBean.getCurrentObjectHandle());
%>

<pwiz:initializePromoteWizard />


<jca:wizard helpSelectorKey="maturity_createPromotionRequest" buttonList="DefaultWizardButtonsNoApply" wizardSelectedOnly="true">
   <jca:wizardStep action="setContextWizStep" type="object"/>
   <jca:wizardStep action="defineItemAttributesWizStep" type="object"/> 
   <jca:wizardStep action="relatedObjectsTableStep" type="promotionNotice" />
   <jca:wizardStep action="setupParticipantsStep" type="promotionNotice" />
   <!--jca:wizardStep action="workflowParticipantsStep" type="promotionRequest" /-->
   <jca:wizardStep action="attachments_step"  type="attachments" />
</jca:wizard>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>