<%@taglib tagdir="/WEB-INF/tags" prefix="wctags"%>
<%@include file="/netmarkets/jsp/components/beginWizard.jspf"%>

<jsp:useBean id="pickerConfig" class="com.catl.promotion.mvc.builders.addSourceChangePartsConfig" scope="page">
   <jsp:setProperty name="pickerConfig" property="isAutoSuggest"  value="false"/>   
</jsp:useBean>
<%%>
<wctags:itemPicker id="addDerivePicker" pickerCallback="doNothing" pickerConfig="${pickerConfig}"/>

<%@include file="/netmarkets/jsp/util/end.jspf" %>