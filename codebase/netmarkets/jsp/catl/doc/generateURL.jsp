<%@page import="java.net.URLEncoder"%>
<%@ page import="com.catl.integration.rdm.RdmIntegrationHelper"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/jcaMvc" prefix="mvc"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<%
String projectCode = URLEncoder.encode("STUYBEV003","utf-8");
String projectName = URLEncoder.encode("ZZT-44.5kWh-6M加热版-1C-00473","utf-8");
String deliverableId = URLEncoder.encode("ZZZ","utf-8");
String docType = URLEncoder.encode("产品技术文件","utf-8");
String subCategory = URLEncoder.encode("控制计划-CP","utf-8");
String str = "http://nit-i63394-o.catlbattery.com/Windchill/netmarkets/jsp/catl/doc/createDocRDM.jsp?projectCode="+projectCode+"&projectName="+projectName+"&deliverableId="+deliverableId+"&docType="+docType+"&subCategory="+subCategory;
%>

<script language="javascript" type="text/javascript">
	//window.location.href=<%=str%>; 
</script>
<body>
	<%=str %>
</body>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>