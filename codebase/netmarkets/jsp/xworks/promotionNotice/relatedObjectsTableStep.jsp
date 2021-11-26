<%@page language="java" pageEncoding="utf-8"%>
<%@page import="com.ptc.xworks.promotion.builders.PromotionNoticeHelper" %>
<%@page import="com.ptc.xworks.util.XWorksHelper" %>
<%@page import="com.ptc.xworks.windchill.util.ObjectTypeUtils" %>
<%@page import="org.apache.commons.lang.StringUtils" %>
<%@page import="com.ptc.xworks.promotion.annotation.PromotionNoticeOptions" %>
<%@page import="com.ptc.xworks.promotion.PromotionNoticeOptionsReader" %>
<%@page import="wt.lifecycle.State" %>
<%@page import="wt.session.SessionHelper" %>

<%@ taglib tagdir="/WEB-INF/tags" prefix="wctags" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ include file="/netmarkets/jsp/util/begin.jspf" %>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf" %>

<fmt:setLocale value="${commandBean.getLocale()}" />
<fmt:setBundle basename="com.ptc.xworks.promotion.promotionResource"/>
<fmt:message var="targetState" key="promote_target_state" />

<div id="MessageComponent"></div>


<!-- wctags:collectItems tableId="promotionRequest.promotionObjects" collectorId="Promote" returnOrigCopy="true" pickerCallback="PTC.promotion.collectorPickerCallback"/ -->

<script type="text/javascript">
	//Ext.ComponentMgr.onAvailable("promotionRequest.promotionObjects", PTC.promotion.attachEvents, PTC.promotion);
</script>



<%
PromotionNoticeOptions option = PromotionNoticeHelper.getPromotionNoticeOptions(commandBean);
if (option != null) {
	String instruction = wt.util.WTMessage.getLocalizedMessage(option.instruction().resourceBundle(), option.instruction().key());
	if (StringUtils.isNotBlank(instruction)) {
%>
<div style="padding: 0pt 8px;">
	<div class="inlineHelpBox">
		<table>
			<tr>
				<td><img src="wtcore/images/tip.gif" border="0"></td>
				<td><%=instruction%></td>
			</tr>
		</table>
	</div>
</div>
<%
	}
	
	if (option.promoteTargetStates().length > 0) {
%>
		<div style="padding: 0pt 8px;margin: 3px 0px;">
		<span style="font-size: 10pt;font-weight: bold;">${targetState}:</span> 
		<select name="maturityState">
<%		for (String stateStr : option.promoteTargetStates()) {
			State state = State.toState(stateStr);
%>
			<option value="<%=state.toString()%>"><%=state.getDisplay(SessionHelper.getLocale())%></option>
<%		} %>
		</select>
		</div>
<%
	} else {
		%><input type="hidden" name="maturityState" value="RELEASED" /><%
	}
}
%>

<jsp:include page="${mvc:getComponentURL('com.ptc.xworks.promotion.builders.RelatedObjectsWizardTableBuilder')}"/>

<script type="text/javascript">
	//PTC.promotion.init("promotionRequest.promotionObjects");
</script>

<script type="text/javascript">


//添加相关对象的JavaScript回调函数
var addRelatedObjectsCallback = function(oidToAdd, tableId) {
	//window.alert(tableId);
	var oids = oidToAdd.oids;
	if (oids && oids.length == 0) {
		return;
	}
	
	rowHandler.addRows(oids, tableId, null, {doAjaxUpdate : true, preventDuplicates : true});	
};

var removeRelatedObjectsCallback = function(oidToRemove, tableId) {
	var oids = oidToRemove.oids;
	if (oids && oids.length == 0) {
		return;
	}
	// rowHandler.removeRows(rows, table_id, visually_remove)
	rowHandler.removeRows(oids, tableId, true);	
};

//使用AutoSuggestBox来添加对象到相关对象表格中的Callback
var addRelatedObjectPickerCallBack = function(objects, tableId) {
	//window.alert(tableId);
    var pickedObjects = objects.pickedObject;
    var list = [];
    for (var i = 0, l = pickedObjects.length; i < l; i++) {
        list.push(pickedObjects[i]["oid"]);
    }
    //window.alert(list);
    rowHandler.addRows(list, tableId, null, {doAjaxUpdate : true, preventDuplicates : true});    
};

</script>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>