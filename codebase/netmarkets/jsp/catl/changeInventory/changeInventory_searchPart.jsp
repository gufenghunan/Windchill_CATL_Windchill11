<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>
<%response.setContentType("text/html; charset=UTF-8");%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>

<%@ page import="com.ptc.windchill.enterprise.part.partResource,
                 com.ptc.windchill.enterprise.partInstance.partInstanceClientResource,
                 com.ptc.netmarkets.util.beans.NmCommandBean,
                 com.ptc.netmarkets.util.misc.NmContextItem,
				 java.util.Stack "%>
				 
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt" %>

<%
	String objectType = "wt.part.WTPart";
	String tableLabel = "";
	request.setAttribute("objectType",objectType);
	request.setAttribute("tableLabel",tableLabel);
	String nodeName = request.getParameter("nodeName");
	//out.println("nodeName;;;"+nodeName);
%>

<input type="hidden" id="nodeName" name="nodeName" value="<%=nodeName %>"/>

<script language="javascript">

/**
 * Save an HTMLElement's value to the backing store in a wizard table.
 * @param {HTMLElement} item the HTMLElement that changed that needs to have its updated value saved to the store
 */
PTC.wizard.saveTableData.saveToStore = function(item){
    if (!item) {
        return;
    }
    var e;
    //alert('inin');
    // Just raise the appropriate browser event.
    //Note that Firefox will not throw a change event on a disabled input field.
    if (document.createEvent) { // W3C Standard
    	//alert('firefox');
        e = document.createEvent("HTMLEvents");
        e.initEvent("change", true /* bubbles */, true /* cancelable */);
        item.dispatchEvent(e);
    }
    else if (document.createEventObject) { //IE 
    	//alert('IE');
        e = document.createEventObject();
        //alert('IE e:::'+e);
		//SPR 2148822-02. In IE, Calendar pop-up was not closing when added as the only entry in multiple attribute entry table
		// Calling the fire event in the try block resolves the issue.
        try {
            //item.fireEvent("onclick", e);
            //add for catl 2015-10-26
            //alert('dd');
            item.fireEvent("onclick");
            //alert('222');
        } 
        catch (f) {
			PTC.wizard.saveTableData.log.debug("item id value is unspecified",item.id );
        }
    }
};
 
function addData(objects, pickerId){
	var nodeName = document.getElementById('nodeName').value;
	var dd = window.opener.document;
	var iii = dd.getElementsByName(nodeName)[0];
	var row = iii.parentNode.parentNode.parentNode;
	//var pNumbers = dd.all('partNumber');
	//var table = dd.getElementById('CatlChangeInventory');
	var myJSONObjects = objects.pickedObject;
	var oid = myJSONObjects[0]["oid"];
	var data = getData(oid);
	var values = data.split("|");
	var cells = row.childNodes;
	for(var i = 1; i < 3; i++){
		var cell = cells[i];
		var div = cell.childNodes[0];
		var inputs = div.childNodes;
		var input = div.childNodes[0];
		if(input){
			input.value = values[i-1];
		    //save data into store, so that when added new row, the old data will not be lost.
		    PTC.wizard.saveTableData.saveNewValueToCell(input, input.parentNode);
		    PTC.wizard.saveTableData.saveToStore(input);
		}
	}
}


function getData(oid) {
	var choice = "";
	var ajaxOptions = {
		asynchronous : false,
		method : "GET",
		onSuccess : function(result, options) {
			var jsonData = Ext.util.JSON.decode(result.responseText.trim());
			var resultMessage = jsonData.message;
			var result = jsonData.result;
			choice = result;
		},
		parameters : {
			oid : oid
		}
	};
	requestHandler.doRequest(
			"netmarkets/jsp/catl/changeInventory/changeInventory_getData.jsp", ajaxOptions);
	return choice
}

</script>

<wctags:itemPicker id="related_add_described_docpart" inline="true" pickerCallback="addData"
                      pickerTitle="${tableLabel}" multiSelect="false" 
                      componentId="RelatedObjectAddAssociation" 
                      typeComponentId="PDMLink.relatedPartSearch"
                      objectType="${objectType}" />
<%@ include file="/netmarkets/jsp/util/end.jspf"%>
