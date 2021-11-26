<%@ taglib tagdir="/WEB-INF/tags" prefix="wctags" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc" %>
<%@ include file="/netmarkets/jsp/util/begin.jspf" %>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf" %>
<div id="MessageComponent"></div>

<div id="selectPromotionState">
 
</div>

<wctags:collectItems tableId="promotionRequest.promotionObjects" collectorId="Promote" returnOrigCopy="true" pickerCallback="PTC.promotion.collectorPickerCallback"/>

<script>
	Ext.ComponentMgr.onAvailable("promotionRequest.promotionObjects", PTC.promotion.attachEvents, PTC.promotion);
</script>

<jsp:include page="${mvc:getComponentURL('CatlpromotionObjectsWizard')}"/>

<script>
	PTC.promotion.init("promotionRequest.promotionObjects");
</script>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>