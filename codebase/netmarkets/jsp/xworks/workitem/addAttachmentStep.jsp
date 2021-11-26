<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@ include file="/netmarkets/jsp/attachments/initAttachments.jspf"%>
<%@ include file="/netmarkets/jsp/attachments/initPTCAttachments.jspf"%>
<%
String operationType = "CREATE";
%>
<input type="hidden" name="FormProcessorDelegate" value="com.ptc.xworks.xmlobject.web.form.GetContentHolderFormProcessorDelegate" />

<jsp:include page="${mvc:getComponentURL('com.ptc.xworks.workflow.attachment.UploadAttachmentAttributePanelBuilder')}">
	<jsp:param name="operationType" value="<%=operationType%>" />
	<jsp:param name="contextObjectClassName" value="com.ptc.xworks.workflow.attachment.GroupedAttachmentInfo" />
</jsp:include>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>