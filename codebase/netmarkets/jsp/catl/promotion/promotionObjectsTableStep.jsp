<%@page import="com.catl.promotion.util.PromotionUtil"%>
<%@page import="wt.part.WTPart"%>
<%@page import="com.ptc.windchill.enterprise.maturity.commands.PromotionItemQueryCommands"%>
<%@page import="java.util.List"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="wctags" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc" %>
<%@ include file="/netmarkets/jsp/util/begin.jspf" %>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf" %>

<jsp:include page="${mvc:getComponentURL('com.catl.promotion.mvc.builders.CatlPromotionObjectsWizardTable')}"/>

<%
String selectedOids = PromotionUtil.getSelectedOids(commandBean);
%>
<script>
function initTable(){
	var arrayObj = new Array();
	var objects = "<%=selectedOids%>";
	if(objects != "" && objects != null){
		var soids = objects.split(",");
		for (var i = 0; i < soids.length; i++) {
	    	var oid = soids[i];
	    	arrayObj.push(oid);
	    }
		addRows(arrayObj, "com.catl.promotion.mvc.builders.CatlPromotionObjects" , false, true, true);
	}
}

function catlPasteItems(event){
	var tableId = tableUtils.findTableID(event);
	var objects = "";
	var ajaxOptions = {
		asynchronous : false,
		method : "post",
		onSuccess : function(result, options) {
			objects = result.responseText.trim();
		}
	};
	requestHandler.doRequest("netmarkets/jsp/catl/promotion/getPasteItems.jsp", ajaxOptions);
	if(objects != "" && objects != null){
		var arrayObj = new Array();
		var soids = objects.split(",");
		for (var i = 0; i < soids.length; i++) {
	    	var oid = soids[i];
	    	arrayObj.push(oid);
	    }
		addRows(arrayObj, "com.catl.promotion.mvc.builders.CatlPromotionObjects" , false, true, true);
	}
}

PTC.onReady(initTable);

</script>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>