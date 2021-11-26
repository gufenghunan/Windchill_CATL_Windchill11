<%@page language="java" session="true" pageEncoding="GBK"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@include file="/netmarkets/jsp/util/beginPopup.jspf" %>
<%@taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@page import="java.util.HashMap"%>
<%@page import="wt.part.WTPart"%>
<%@page import="wt.doc.WTDocument"%>
<jsp:include page="${mvc:getComponentURL('newaftersalebuilder')}"  />
<script type="text/javascript">
function addData(objs){
	var arrayObj = new Array();
	var selections = objs.pickedObject;
	for(var i=0;i<selections.length;i++){
		arrayObj.push(selections[i].oid);
	}
	addRows(arrayObj, "newaftersalebuilder", false, true, true);
}

function removeParts(){
	var table=PTC.jca.table.Utils.getTable("newaftersalebuilder");
	var selections=table.getSelectionModel().selections;
	var arrayObj = new Array();
	console.log(selections);
	for(var i=0;i<selections.getCount();i++){
		arrayObj.push(selections.itemAt(i).get("oid"));
	}
	PTC.jca.table.Utils.removeRows(table,arrayObj);
	console.log(arrayObj);
}

function pasteParts(){
	var items=getLatestClipboardItems();
	var oidstr=items.split(",");
	var arrayObj = new Array();
	for(var i=0;i<oidstr.length;i++){
		var coidstr=oidstr[i];
		var oid=coidstr.split("|")[0];
		arrayObj.push(oid);
	}
	addRows(arrayObj, "newaftersalebuilder", false, true, true);
}

function reloadOpenerTable(){
	if(window.opener){
		window.opener.location.reload();
	}
	window.close();
	
}
</script>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>