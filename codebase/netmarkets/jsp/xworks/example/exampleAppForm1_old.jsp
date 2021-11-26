<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@page import="wt.workflow.work.WfAssignmentState"%>
<%@page import="wt.fc.WTObject"%>
<%@page import="java.util.List"%>
<%@page import="ext.ptc.xworks.examples.taskform.ExampleApplicationForm1" %>
<%@page import="ext.ptc.xworks.examples.taskform.PromotionRequestDemoProcess1" %>
<%@page import="wt.workflow.work.WorkItem"%>
<%@page import="com.ptc.netmarkets.model.NmOid"%>
<%@page import="com.ptc.xworks.xmlobject.web.LayoutForView"%>

<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="attachments" uri="http://www.ptc.com/windchill/taglib/attachments" %>

<!-- attachments:fileSelectionAndUploadApplet/-->
<jsp:useBean id="commandBean" class="com.ptc.netmarkets.util.beans.NmCommandBean" scope="request"/>
<%
Object obj=null;
//System.out.println("oid=" + request.getParameter("oid"));
//System.out.println("PageOid=" + commandBean.getPageOid());
//System.out.println("getActionOid=" + commandBean.getActionOid());
//System.out.println("getPrimaryOid=" + commandBean.getPrimaryOid());
if(commandBean.getPageOid()!=null) {
	obj = commandBean.getPageOid().getRefObject();
} else {
	String oid = request.getParameter("oid");
	if (oid != null) {
		obj = NmOid.newNmOid(oid).getRefObject();
	}
}

//String dynamicUpdate = request.getParameter("dynamicUpdate");
//boolean isDynamicUpdate = "true".equals(dynamicUpdate);

//boolean accessEnforced = SessionServerHelper.manager.isAccessEnforced();
WorkItem currentWorkItem = null;
String workItemOidString = "";
if (obj != null) {
	if (obj instanceof WorkItem) {
		//String layoutForExampleXmlObjectTable = "";
		String viewOnly = "false";
		String operationType = "EDIT";
		currentWorkItem = (WorkItem) obj;
		workItemOidString = currentWorkItem.getPersistInfo().getObjectIdentifier().toString();
		
		LayoutForView layoutForExampleXmlObjectTable = new LayoutForView();
		layoutForExampleXmlObjectTable.attribute("actionGroup1").label("Label for actionGroup1");
		LayoutForView layoutForExamplePanel = new LayoutForView();
		layoutForExamplePanel.attribute("suggestBox1").displayMode("READ_WRITE").label("Label for suggestBox1");
		if (currentWorkItem.getStatus() == WfAssignmentState.COMPLETED) {
			operationType = "VIEW";
			viewOnly = "true";
			layoutForExampleXmlObjectTable.attribute("button2").displayMode("HIDE");
		}
		Object pbo = currentWorkItem.getPrimaryBusinessObject().getObject();
		List<ExampleApplicationForm1> result = PromotionRequestDemoProcess1.getExampleApplicationForm1((WTObject) pbo);
		if (result.size() > 0) {
			ExampleApplicationForm1 applicationForm = result.get(0);
			if (ExampleApplicationForm1.STATE_UNDER_REVIEW.equals(applicationForm.getState())) { // if state of AppForm is UNDER_REVIEW, then edit only
				operationType = "VIEW";
				viewOnly = "true";
				layoutForExampleXmlObjectTable.attribute("button2").displayMode("HIDE");
			}
			if (ExampleApplicationForm1.STATE_NEW_CREATED.equals(applicationForm.getState())) { // if state of AppForm is NEW_CREATE, then create
				operationType = "EDIT";
				layoutForExampleXmlObjectTable.attribute("button2").displayMode("READ_ONLY");
			}			
			//System.out.println(applicationForm.getIdentifier());
			//request.setAttribute("applicationForm", applicationForm);
			String contextPath = applicationForm.getIdentifier().toString() + "~exampleXmlObjects1";
		%>
			<!-- 使用下面这个FormProcessorDelegate来进行XmlObject相关的Web表单解析 -->
			<input type="hidden" name="FormProcessorDelegate" value="com.ptc.xworks.xmlobject.web.form.XmlObjectExtractFormProcessorDelegate" />
			<input type="hidden" name="FormProcessorDelegate" value="com.ptc.xworks.xmlobject.web.form.GetContentHolderFormProcessorDelegate" />
			
			<!-- 使用下面这个FormProcessorDelegate来保存ExampleApplicationForm1 -->
			<%if ("EDIT".equals(operationType) || "CREATE".equals(operationType)) {//只有在编辑时，才需要使用FormProcessorDelegate去进行保存，否则可能造成数据丢失%>
			<input type="hidden" name="FormProcessorDelegate" value="ext.ptc.xworks.examples.taskform.ExampleApplicationForm1EditFormProcessorDelegate" />
			<%} %>
			<c:set var="xmlObject" value="<%=applicationForm%>" scope="request" />
			<jsp:include page="${mvc:getComponentURL('ext.ptc.xworks.examples.taskform.ExampleApplicationForm1AttributePanelBuilder')}">
				<jsp:param name="operationType" value="<%=operationType%>" />
				<jsp:param name="contextObjectClassName" value="ext.ptc.xworks.examples.taskform.ExampleApplicationForm1"/>
				<jsp:param name="componentId" value="ext.ptc.xworks.examples.taskform.ExampleApplicationForm1AttributePanelBuilder.inlineTaskformExample1"/>
				<jsp:param name="layoutForView" value="<%=layoutForExamplePanel %>" />
			</jsp:include>
			<script type="text/javascript">
			// 此JS代码用来解决Table中表单可能只有部分行被提交的问题
			Ext.override(Ext.ux.grid.BufferView,{
				cacheSize : 1000,
				doClean: Ext.emptyFn
			});
			</script>			
			<!-- componentId的设置,可能导致动态刷新(创建时)出现问题, componentId与ComponentBuilder中设置一致时不会出问题,不一致时，经测试，使用下划线可以正常运行 -->
			<jsp:include page="${mvc:getComponentURL('ext.ptc.xworks.examples.taskform.ExampleXmlObject1TableBuilder')}">
				<jsp:param name="operationType" value="EDIT" />
				<jsp:param name="componentId" value="ext.ptc.xworks.examples.taskform.ExampleXmlObject1TableBuilder"/>
				<jsp:param name="contextPath" value="<%=contextPath%>"/>
				<jsp:param name="applicationFormOid" value="<%=applicationForm.getIdentifier().toString() %>"/>
				<jsp:param name="viewOnly" value="<%=viewOnly%>"/>
				<jsp:param name="contextObjectClassName" value="ext.ptc.xworks.examples.taskform.ExampleXmlObject1" />
				<jsp:param name="layoutForView" value="<%=layoutForExampleXmlObjectTable %>" />
				<jsp:param name="disableClassificationAttributeProvider" value="true" />
			</jsp:include>
		<%
		} else {
			%>--NO APPLICATION FORM FOUND--<%
		}
		%>
			<jsp:include page="${mvc:getComponentURL('com.ptc.xworks.workflow.relatedobject.RelatedObjectGroupTableBuilder')}">
				<jsp:param name="groupId" value="relatedObjectGroup02"/>
			</jsp:include>				
		<%
	}
}
%>
<script type="text/javascript">
function editExampleXmlObject1Row(xmlOid) {
	//setMainformForActionHandler(event);
	popupAction('editExampleXmlObject1_taskformExample', 'ptc1/xworks/example/createAndEditExampleXmlObject1', '','','','','','','','','','','','','portlet', 'poppedup', 'context', 'workitem\x24attributes_xmlobject\x24<%=workItemOidString%>\x24','oid', xmlOid, 'workitem\x24attributes_xmlobject\x24<%=workItemOidString%>\x24', 'none', 'ext.ptc.xworks.examples.taskform.ExampleXmlObject1EditFormProcessor', '', 'width=1000,height=800', '','', false, 'P122011669813533', 'row', '',false, null);
}

function setXmlObjectFormResultNextAction(nextAction) {
	var inputElem = $$("input[id=xmlobject_FormResultNextAction]");
	if (inputElem.length == 0) {
		return false;
	}
	for (var i = 0; i < inputElem.length; i++) {
		inputElem[i].setAttribute("value", nextAction);
	}
	return true;
}

function saveFormAndSetNextAction() {
	var result = validateApplicationForm();
	window.alert(result);
	if (!result) {
		return result;
	}
	//window.alert("open a new Action Window in this function!");
	setXmlObjectFormResultNextAction("openNewActionWindow()");
	return true;
}

function openNewActionWindow() {
	window.alert("open a new Action Window in this function!");
	setXmlObjectFormResultNextAction("");
}

</script>
<input id="xmlobject_FormResultNextAction" type="hidden" name="xmlobject_FormResultNextAction" value=""/>

