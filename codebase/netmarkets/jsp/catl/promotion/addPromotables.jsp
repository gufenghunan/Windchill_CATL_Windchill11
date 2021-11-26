<%@taglib tagdir="/WEB-INF/tags" prefix="wctags"%>
<%@include file="/netmarkets/jsp/components/beginWizard.jspf"%>

<%@ page import="wt.fc.*"%>
<%@ page import="java.util.*"%>
<%@ page import="wt.maturity.PromotionNotice"%>

	<jsp:useBean id="pickerConfig" class="com.ptc.windchill.enterprise.maturity.search.PromotionObjectsPickerConfig" scope="page">
					<jsp:setProperty name="pickerConfig" property="isAutoSuggest"  value="false"/>   
	</jsp:useBean>

	<wctags:itemPicker id="promotablePicker" pickerConfig="${pickerConfig}"/>


<%@include file="/netmarkets/jsp/util/end.jspf" %>