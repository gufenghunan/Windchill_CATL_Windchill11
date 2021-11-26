<%@taglib tagdir="/WEB-INF/tags" prefix="wctags"%>
<%@include file="/netmarkets/jsp/components/beginWizard.jspf"%>

<jsp:useBean id="pickerConfig" class="com.catl.promotion.mvc.builders.addDesignDisadledPartsConfig" scope="page">
   <jsp:setProperty name="pickerConfig" property="isAutoSuggest"  value="false"/>   
</jsp:useBean>
<%%>
<wctags:itemPicker id="addDerivePicker" pickerCallback="addSearchResult" pickerConfig="${pickerConfig}"/>
<script>
function addSearchResult(objs){
	var arrayObj = new Array();
	var selections = objs.pickedObject;
	for(var i=0;i<selections.length;i++){
		arrayObj.push(selections[i].oid);
	}
	opener.addRows(arrayObj, "com.catl.promotion.mvc.builders.CatlPromotionObjects", false, true, true);
}
</script>
<%@include file="/netmarkets/jsp/util/end.jspf" %>