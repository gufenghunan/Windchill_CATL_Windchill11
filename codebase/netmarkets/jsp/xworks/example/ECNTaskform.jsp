<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@page import="ext.ptc.xworks.examples.ecn.util.ChangeConst"%>
<%@page import="ext.ptc.xworks.examples.ecn.bean.ECBasicXmlObjectBean"%>
<%@page import="ext.ptc.xworks.examples.ecn.dbs.ECNBasicXmlObjectUtil"%>
<%@page import="com.ptc.xworks.xmlobject.annotation.GuiComponentType"%>
<%@page import="wt.change2.WTChangeRequest2"%>
<%@page import="wt.change2.WTChangeOrder2"%>
<%@page import="com.ptc.xworks.xmlobject.BaseXmlObjectRef"%>
<%@page import="com.ptc.xworks.windchill.util.NetmarketsUtils"%>
<%@page import="wt.workflow.work.WfAssignmentState"%>
<%@page import="wt.fc.WTObject"%>
<%@page import="java.util.List"%>
<%@page import="wt.workflow.work.WorkItem"%>
<%@page import="com.ptc.netmarkets.model.NmOid"%>
<%@page import="com.ptc.xworks.xmlobject.web.LayoutForView"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="attachments" uri="http://www.ptc.com/windchill/taglib/attachments" %>

<!-- attachments:fileSelectionAndUploadApplet/-->
<jsp:useBean id="commandBean" class="com.ptc.netmarkets.util.beans.NmCommandBean" scope="request"/>
<%
String operationType = "VIEW";
String xmlObjectOid = "";
LayoutForView layoutForECN = new LayoutForView();
WorkItem workItem = (WorkItem) NetmarketsUtils.getContextObject(commandBean);
String description = workItem.getDescription();
Object pbo = workItem.getPrimaryBusinessObject().getObject();
WTChangeOrder2 ecn = (WTChangeOrder2) pbo;
ECBasicXmlObjectBean ecBasicXmlObjectBean = ECNBasicXmlObjectUtil.getECBasicXmlObjectBean(ecn);
if (ecBasicXmlObjectBean != null) {
	xmlObjectOid = ecBasicXmlObjectBean.toString();
}
if (description.indexOf(ChangeConst.OPPO_ECN_SUBMIT) > 0) {
	if (!workItem.isComplete()) {		
		operationType = "EDIT";
	}
}
%>

<!-- 使用下面这个FormProcessorDelegate来进行XmlObject相关的Web表单解析 -->
<input type="hidden" name="FormProcessorDelegate" value="com.ptc.xworks.xmlobject.web.form.XmlObjectExtractFormProcessorDelegate" />
<input type="hidden" name="FormProcessorDelegate" value="com.ptc.xworks.xmlobject.web.form.GetContentHolderFormProcessorDelegate" />
<input type="hidden" name="FormProcessorDelegate" value="ext.ptc.xworks.examples.ecn.processors.ECNChangeNoticeProcessorDelegate" />

<jsp:include page="${mvc:getComponentURL('ext.ptc.xworks.examples.ecn.mvc.builder.ECBasicAttributePanelBuilder')}">
	<jsp:param name="operationType" value="<%=operationType %>" />
	<jsp:param name="xmlObjectOid" value="<%=xmlObjectOid %>" />
	<jsp:param name="contextObjectClassName" value="ext.ptc.xworks.examples.ecn.bean.ECBasicXmlObjectBean"/>
	<jsp:param name="componentId" value="ext.ptc.xworks.examples.ecn.mvc.builder.ECBasicAttributePanelBuilder"/>
	<jsp:param name="layoutForView" value="<%=layoutForECN %>" />
</jsp:include>

<jsp:include page="${mvc:getComponentURL('com.ptc.xworks.workflow.relatedobject.RelatedObjectGroupTableBuilder')}">
	<jsp:param name="groupId" value="relatedObjectGroup01"/>
</jsp:include>

<jsp:include page="${mvc:getComponentURL('com.ptc.xworks.workflow.relatedobject.RelatedObjectGroupTableBuilder')}">
	<jsp:param name="groupId" value="relatedObjectGroup02"/>
</jsp:include>