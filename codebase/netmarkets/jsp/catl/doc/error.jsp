<%@ page import="java.util.HashMap,com.ptc.netmarkets.util.misc.NetmarketURL"%>
<%@ page import="java.util.HashMap"%>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/jcaMvc" prefix="mvc"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<%
String errorType = request.getParameter("errorType");
String message="";
if(errorType.equals("1")){
	message = WTMessage.getLocalizedMessage("com.catl.integration.rdm.resource.CatlRDMResource","selectType.error");
}else if(errorType.equals("2")){
	message = WTMessage.getLocalizedMessage("com.catl.integration.rdm.resource.CatlRDMResource","selectType.message");
}
%>
<body>

<div id="errorRedirect">
<%=message%>
</div>
<script>
	
</script>
</body>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>