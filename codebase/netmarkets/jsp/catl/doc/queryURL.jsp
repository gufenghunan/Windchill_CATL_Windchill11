<%@ page import="com.catl.integration.rdm.RdmIntegrationHelper"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Arrays"%>
<%@page import="com.ptc.core.components.rendering.guicomponents.ComboBox"%>
<%@page import="com.catl.change.DataUtility.CatlPropertyHelper"%>
<%
String docType = request.getParameter("docType");
String docTypeValue = request.getParameter("docTypeValue");
String projectCode = request.getParameter("projectCode");
String projectName = request.getParameter("projectName");
String deliverableId = request.getParameter("deliverableId");
String subCategory = request.getParameter("subCategory");
String[] ret = RdmIntegrationHelper.queryFolderOidAndContanierOid(projectCode, projectName,docType);
String basePath="http://"+request.getServerName()+request.getContextPath();
String url;
if(ret != null){
	url = basePath+"/ptc1/document/create?"+ret[0]+"&u8=1&unique_page_number=58493172163176_14&AjaxEnabled=row&wizardActionClass=com.catl.doc.processor.CatlDocCreateProcessor&wizardActionMethod=execute&tableID=table__folderbrowser_PDM_TABLE&actionName=create&portlet=poppedup&deliverableId="+deliverableId+"&"+ret[1]+"&docTypeValue="+docTypeValue+"&subCategory="+subCategory;
}else{
	url = basePath+"/netmarkets/jsp/catl/doc/error.jsp?errorType=1";
}

out.println(url);
	
%>