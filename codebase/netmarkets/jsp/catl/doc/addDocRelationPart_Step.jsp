<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ taglib prefix="mvc" uri="http://www.ptc.com/windchill/taglib/mvc"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<fieldset class="x-fieldset x-form-label-left"
                                id="Visualization_and_Attributes"
                                style="margin: 10px 5px 15px 10px;">
                                <legend>关联部件</legend>
	<table width="100%">
		<tr>
		    <td align="left" class="ppLabel"
				style="font-weight: bold">名称</td>
			<td>
			   <input id="partName" name="partName" type="text"/>
			 </td>			  
		</tr>
		
		<tr>
		    <td align="left" class="ppLabel"
				style="font-weight: bold">编号</td>
			<td>
			   <input id="partNumber" name="partNumber" type="text"/>
			   <input type="button" name="partSearch" id="partSearch" value="搜索" onClick="searchPartData()" />
			</td>
		</tr>
	</table>
</fieldset>
<jsp:include page="${mvc:getComponentURL('com.catl.doc.relation.AddDocRelationPartBuilder')}"/>
<script type="text/javascript">
var tableId = 'com.catl.doc.relation.AddDocRelationPartBuilder';
function preSelected(){
	var table = PTC.jca.table.Utils.getTable(tableId);
	Ext.grid.GridPanel.prototype.jcaSingleRowPreSelect(table);
}
function searchPartData() {
	var partNumber = document.getElementById("partNumber").value;
	
	var partName = document.getElementById("partName").value;
	
	if(partNumber=="" && partName==""){
		alert("请输入至少一个条件")
		return;
	}
	
	var params = {
			partNumber : partNumber,
			partName : partName,
       };
		
	PTC.jca.table.Utils.reload(tableId, params, true);
}
PTC.onReady(function(){
	var table = PTC.jca.table.Utils.getTable(tableId);
	var store = table.store;
	store.on("datachanged",preSelected);
})
</script>
<%@include file="/netmarkets/jsp/util/end.jspf"%>