<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@page import="wt.workflow.work.WfAssignmentState"%>
<%@page import="wt.fc.WTObject"%>
<%@page import="java.util.List"%>
<%@page import="ext.ptc.xworks.examples.taskform.ExampleApplicationForm1" %>
<%@page import="ext.ptc.xworks.examples.taskform.PromotionRequestDemoProcess1" %>
<%@page import="wt.workflow.work.WorkItem"%>
<%@page import="com.ptc.netmarkets.model.NmOid"%>
<%@page import="com.ptc.xworks.xmlobject.web.LayoutForView"%>
<%@page import="com.ptc.xworks.windchill.util.NetmarketsUtils"%>

<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="attachments" uri="http://www.ptc.com/windchill/taglib/attachments" %>

<!-- attachments:fileSelectionAndUploadApplet/-->
<jsp:useBean id="commandBean" class="com.ptc.netmarkets.util.beans.NmCommandBean" scope="request"/>
<% 
WorkItem workItem = (WorkItem) NetmarketsUtils.getContextObject(commandBean);
String workItemOidString = workItem.getPersistInfo().getObjectIdentifier().toString();
Object pbo = workItem.getPrimaryBusinessObject().getObject();

String operationType = "EDIT";

List<ExampleApplicationForm1> result = PromotionRequestDemoProcess1.getExampleApplicationForm1((WTObject) pbo);
if (result.size() == 0) {
	%>--ERROR: Cannot found ExampleApplicationForm1 from database --<%	
	return;
}

ExampleApplicationForm1 applicationForm = result.get(0);
String xmlObjectOid = applicationForm.getIdentifier().toString();
String contextPath = xmlObjectOid + "~exampleXmlObjects1";
String viewOnly = "false";

LayoutForView layoutForExamplePanel = new LayoutForView(); // 用于控制表单中的某些属性的显示不显示等
layoutForExamplePanel.attribute("suggestBox1").readWrite().label("Label for suggestBox1"); // 设置属性suggestBox可以读写，并且覆盖默认的Label

LayoutForView layoutForExampleXmlObjectTable = new LayoutForView();  // 用于控制表单中的某些属性的显示不显示等
layoutForExampleXmlObjectTable.attribute("stringAttr5").guiType("TEXT"); // 将属性stringAttr5的UI控件设置为使用文本显示

if (ExampleApplicationForm1.STATE_UNDER_REVIEW.equals(applicationForm.getState())) { // if state of AppForm is UNDER_REVIEW, then edit only
	operationType = "VIEW";
	viewOnly = "true";
	layoutForExampleXmlObjectTable.attribute("button2").hide(); // 如果是在审核任务节点，将表格中的编辑链接隐藏
}
if (ExampleApplicationForm1.STATE_NEW_CREATED.equals(applicationForm.getState())) { // if state of AppForm is NEW_CREATE, then create
	operationType = "EDIT";
	layoutForExampleXmlObjectTable.attribute("button2").readOnly();
}

if (workItem.isComplete()) {
	// 如果流程任务已经完成，则表单应该只允许查看
	operationType = "VIEW";
	viewOnly = "true";
	layoutForExampleXmlObjectTable.attribute("button2").hide(); // 如果任务已经完成，将表格中的编辑链接隐藏
}

%>
<!-- 在页面放置一个下面的HTML元素，防止表格内编辑时，行数过多时只提交部分行的数据的问题 -->
<input id="XWORKS_DISABLE_BUFFERVIEW_DOCLEAN" type="hidden" value="true" />
<!-- 在页面放置一个下面的HTML元素，当值为true时，表示忽略表单必需的属性的检查操作 -->
<input id="XWORKS_SKIP_REQUIRED_FIELD_VALIDATION" type="hidden" value="false" />

<!-- 下面的代码用于演示如何重新加载AttributePanel -->
<a href="javascript:reloadApplicationFrom()">Reload Application From</a>

<!-- 使用下面这个FormProcessorDelegate来进行XmlObject相关的Web表单解析 -->
<input type="hidden" name="FormProcessorDelegate" value="com.ptc.xworks.xmlobject.web.form.XmlObjectExtractFormProcessorDelegate" />
<input type="hidden" name="FormProcessorDelegate" value="com.ptc.xworks.xmlobject.web.form.GetContentHolderFormProcessorDelegate" />
<!-- 使用下面这个FormProcessor来检查某些情况下必须填写备注信息 -->
<input type="hidden" name="FormProcessor" value="ext.ptc.xworks.examples.taskform.ExampleWorkItemCommentsCheckingFormProcessor"/>

<!-- 使用下面这个FormProcessorDelegate来保存ExampleApplicationForm1 -->
<%if ("EDIT".equals(operationType) || "CREATE".equals(operationType)) {//只有在编辑时，才需要使用FormProcessorDelegate去进行保存，否则可能造成数据丢失%>
<input type="hidden" name="FormProcessorDelegate" value="ext.ptc.xworks.examples.taskform.ExampleApplicationForm1EditFormProcessorDelegate" />
<%} %>
<jsp:include page="${mvc:getComponentURL('ext.ptc.xworks.examples.taskform.ExampleApplicationForm1AttributePanelBuilder')}">
	<jsp:param name="xmlObjectOid" value="<%=xmlObjectOid%>" />
	<jsp:param name="operationType" value="<%=operationType%>" />
	<jsp:param name="contextObjectClassName" value="ext.ptc.xworks.examples.taskform.ExampleApplicationForm1"/>
	<jsp:param name="componentId" value="ext.ptc.xworks.examples.taskform.ExampleApplicationForm1AttributePanelBuilder|inlineTaskformExample1"/>
	<jsp:param name="layoutForView" value="<%=layoutForExamplePanel %>" />
</jsp:include>

<script type="text/javascript">
PTC.onAvailable("ext.ptc.xworks.examples.taskform.ExampleXmlObject1TableBuilder", function() {
	var table = PTC.jca.table.Utils.getTable("ext.ptc.xworks.examples.taskform.ExampleXmlObject1TableBuilder");
	//window.alert(table.domChangeEventListener);
	// 用这个函数，在某个组件加载完成时做某些逻辑处理
	console.log("start to toggleAllBooleanAttr3...");
	toggleAllBooleanAttr3();
	
}, "");
</script>			
<!-- componentId的设置,可能导致动态刷新(创建时)出现问题, componentId与ComponentBuilder中设置一致时不会出问题,不一致时，经测试，使用下划线可以正常运行 -->
<jsp:include page="${mvc:getComponentURL('ext.ptc.xworks.examples.taskform.ExampleXmlObject1TableBuilder')}">
	<jsp:param name="operationType" value="EDIT" />
	<jsp:param name="componentId" value="ext.ptc.xworks.examples.taskform.ExampleXmlObject1TableBuilder"/>
	<jsp:param name="contextPath" value="<%=contextPath%>"/>
	<jsp:param name="applicationFormOid" value="<%=xmlObjectOid%>"/>
	<jsp:param name="viewOnly" value="<%=viewOnly%>"/>
	<jsp:param name="contextObjectClassName" value="ext.ptc.xworks.examples.taskform.ExampleXmlObject1" />
	<jsp:param name="layoutForView" value="<%=layoutForExampleXmlObjectTable %>" />
	<jsp:param name="disableClassificationAttributeProvider" value="true" />
</jsp:include>

<!-- 使用下面的代码，将相关对象分组表格显示在这个位置 -->
<jsp:include page="${mvc:getComponentURL('com.ptc.xworks.workflow.relatedobject.RelatedObjectGroupTableBuilder')}">
	<jsp:param name="groupId" value="relatedObjectGroup02"/>
</jsp:include>	

<%

%>

<!-- 本页面所需的Javascript代码 -->
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

function reloadApplicationFrom() {
	// 如果没有明确的设置componentId, 则componentId=attributePanel
	var componentId = "ext.ptc.xworks.examples.taskform.ExampleApplicationForm1AttributePanelBuilder|inlineTaskformExample1";
	var attributePanelBuilder = "ext.ptc.xworks.examples.taskform.ExampleApplicationForm1AttributePanelBuilder";
	var extraParams = "operationType=<%=operationType%>&xmlObjectOid=<%=xmlObjectOid%>";
	XmlObject.reloadAttributePanel(attributePanelBuilder, componentId, extraParams, reloadAttributePanelCallback);
}

function reloadAttributePanelCallback() {
	//window.alert("reload finished!");
}

function toggleTableInputs(element, objectOid) {
	console.log(element.checked);
	var divs = $$("div[oid=" + objectOid + "][attributename^=long], div[oid=" + objectOid + "][attributename^=attachment], div[oid=" + objectOid + "][attributename^=int]");
	for (var i = 0; i < divs.length; i++) {
		if (element.checked) {
			//divs[i].style.visibility = "visible";
			divs[i].show();
			console.log("show");
		} else {
			//divs[i].style.visibility = "hidden";
			divs[i].hide();
			console.log("hide");
		}
		
	}
	var inputs = $$("div[oid=" + objectOid + "][attributename^=long] input[type=text], div[oid=" + objectOid + "][attributename^=attachment] [type=text], div[oid=" + objectOid + "][attributename^=int] [type=text]");
	console.log("inputs.length=" + inputs.length);
	for (var i = 0; i < inputs.length; i++) {
		// use thie function call to save the changed cell html to backend data store
		PTC.wizard.saveTableData.saveToStore(inputs[i]);
	}
}

function toggleAllBooleanAttr3() {
	var booleanAttr3s = $$("input[type=checkbox][attributename=booleanAttr3]");
	console.log("booleanAttr3s.length=" + booleanAttr3s.length);
	for (var i = 0; i < booleanAttr3s.length; i++) {
		toggleTableInputs(booleanAttr3s[i], booleanAttr3s[i].readAttribute("oid"));
	}
}

function validateApplicationFormCallback(element, event) {
	console.log("validateApplicationFormCallback.....");
	console.log(element.name);
	console.log(event);
	var skipRequiredValidation = false;
	if ("complete" == element.name) {
		if (XmlObject.isRoutingSelected("撤单")) {
			skipRequiredValidation = true;
		}
	} else if ("save" == element.name) {
		// 如果是保存，也略过必填验证
		skipRequiredValidation = true;
		console.log("skip required validation if do saving...");
	}
	if (skipRequiredValidation) {
		$("XWORKS_SKIP_REQUIRED_FIELD_VALIDATION").value = "true";
	} else {
		$("XWORKS_SKIP_REQUIRED_FIELD_VALIDATION").value = "false";
	}
	return true;
}

function setText1Value(text1HtmlId, value) {
	$(text1HtmlId).value = value;
}

//PTC.wizard.saveTableData.saveToStore(item);
</script>

<input id="xmlobject_FormResultNextAction" type="hidden" name="xmlobject_FormResultNextAction" value=""/>