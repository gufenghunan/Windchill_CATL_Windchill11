<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ taglib prefix="mvc" uri="http://www.ptc.com/windchill/taglib/mvc"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<fieldset class="x-fieldset x-form-label-left"
                                id="Visualization_and_Attributes"
                                style="margin: 10px 5px 15px 10px;">
                                <legend>Relation Doc</legend>
	<table width="100%">
		<tr>
		    <td align="left" class="ppLabel"
				style="font-weight: bold">Name</td>
			<td>
			   <input id="docName" name="docName" type="text"/>
			 </td>			  
		</tr>
		
		<tr>
		    <td align="left" class="ppLabel"
				style="font-weight: bold">Number</td>
			<td>
			   <input id="docNumber" name="docNumber" type="text"/>
			</td>			   
		</tr>
		<tr>
			<td></td>
			<td></td>
			<td></td>
		</tr>
		<tr>
			<td></td>
			<td>
			<input type="button" name="docSearch" id="docSearch" value="Search" onClick="searchData()" />
			</td>
			<td></td>
		</tr>
	</table>
</fieldset>
<jsp:include page="${mvc:getComponentURL('com.catl.part.relation.AddPartRelationDocBuilder')}"/>
<script type="text/javascript">
var tableId = 'com.catl.part.relation.AddPartRelationDocBuilder';
function searchData() {

	var docNumber = document.getElementById("docNumber").value;
	var docName = document.getElementById("docName").value;
	
	var params = {
			docNumber : docNumber,
			docName : docName,
       };
		
	PTC.jca.table.Utils.reload(tableId, params, true);
}
</script>
<%@include file="/netmarkets/jsp/util/end.jspf"%>