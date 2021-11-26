<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>

<wctags:collectItems tableId="changeTask_affectedItems_table" collectorId="CollectItemsFromChangeItem_AffectedItems" returnObjectReferences="false" returnOrigCopy="true" pickerCallback="PTC.change.changeable.collectorCallBack"/>
<wctags:collectItems tableId="changeTask_resultingItems_table" collectorId="CollectItemsFromChange_ResultingItems" returnObjectReferences="false" returnOrigCopy="true" pickerCallback="PTC.change.changeable.collectorCallBack"/>
<script src='netmarkets/javascript/hangingChanges/deferChange.js'></script>

<jsp:include page="${mvc:getComponentURL('affectedItemsTable')}" flush="true"/>

<BR>
<jsp:include page="${mvc:getTypeBasedComponentURL('changeTask.resultingItemsTable')}" flush="true"/>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>

