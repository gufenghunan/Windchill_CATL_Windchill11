<%@page import="com.ptc.xworks.xmlobject.XmlObjectIdentifier"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@page import="com.ptc.xworks.xmlobject.web.LayoutForView"%>
<%@page import="com.ptc.xworks.util.XWorksHelper"%>
<%@page import="ext.ptc.xworks.examples.taskform.ExampleXmlObject1"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.ptc.xworks.xmlobject.web.form.GetContentHolderFormProcessorDelegate" %>
<%@page import="com.ptc.xworks.windchill.util.ObjectReferenceUtils" %>
<%@page import="wt.content.ContentHolder" %>
<%@page import="com.ptc.netmarkets.util.beans.NmCommandBean" %>

<%
String operationType = "VIEW";
String oid = request.getParameter("oid");
String actionName = request.getParameter("actionName");
if ("createExampleXmlObject1".equals(actionName)) {
	operationType = "CREATE";
	oid = "ext.ptc.xworks.examples.taskform.ExampleXmlObject1:-122312";
}
if ("editExampleXmlObject1".equals(actionName)) {
	operationType = "EDIT";
}
String classificationNode = "";
if ("EDIT".equals(operationType) || "VIEW".equals(operationType)) {
	ExampleXmlObject1 exampleObject = (ExampleXmlObject1) XWorksHelper.getXmlObjectStoreManager().load(new XmlObjectIdentifier(oid));
	classificationNode = StringUtils.trimToEmpty(exampleObject.getClassficiation1().getValue());
}


LayoutForView layoutForView = new LayoutForView();
layoutForView.attribute("timestampAttr2").required(true).readWrite();
layoutForView.attribute("dateAttr1").required(true).readWrite();
layoutForView.attribute("stringAttr1").required(true).readWrite();
layoutForView.attribute("stringAttr2").required(true).readWrite();
layoutForView.attribute("stringAttr3").required(true).readWrite();
layoutForView.attribute("stringAttr4").required(true).readWrite();
layoutForView.attribute("stringAttr5").required(true).readWrite();
layoutForView.attribute("intAttr5").required(true).displayMode("READ_WRITE");
layoutForView.attribute("folder1").required(true);

// using label() to override label config by annotation
layoutForView.attribute("longAttr1").required(true).displayMode("READ_WRITE").label("Label for longAttr1");
%>
<input type="hidden" name="oid" value="<%=oid%>" />
<input type="hidden" name="FormProcessorDelegate" value="com.ptc.xworks.xmlobject.web.form.GetContentHolderFormProcessorDelegate" />

<jsp:include page="${mvc:getComponentURL('ext.ptc.xworks.examples.taskform.ExampleClassicationPanelAttributePanelBuilder')}">
	<jsp:param name="operationType" value="<%=operationType%>" />
	<jsp:param name="xmlObjectOid" value="<%=oid%>" />
	<jsp:param name="componentId" value="ext.ptc.xworks.examples.taskform.ExampleClassicationPanelAttributePanelBuilder" />
	<jsp:param name="contextObjectClassName" value="ext.ptc.xworks.examples.taskform.ExampleXmlObject1" />
	<jsp:param name="layoutForView" value="<%=layoutForView.toString()%>" />
	<jsp:param name="classificationNode" value="<%=classificationNode%>" />
</jsp:include>
<input id="xworks_classificationNode" type="hidden" name="classificationNode" value="<%=classificationNode%>" />

<jsp:include page="${mvc:getComponentURL('ext.ptc.xworks.examples.taskform.ExampleXmlObject1AttributePanelBuilder')}">
	<jsp:param name="operationType" value="<%=operationType%>" />
	<jsp:param name="xmlObjectOid" value="<%=oid%>" />
	<jsp:param name="contextObjectClassName" value="ext.ptc.xworks.examples.taskform.ExampleXmlObject1" />
	<jsp:param name="layoutForView" value="<%=layoutForView.toString()%>" />
	<jsp:param name="classificationNode" value="<%=classificationNode%>" />
</jsp:include>

<jsp:include page="${mvc:getComponentURL('ext.ptc.xworks.examples.taskform.ExampleXmlObject2CreationTableBuilder')}">
	<jsp:param name="operationType" value="VIEW" />
	<jsp:param name="parentOid" value="<%=oid%>" />
	<jsp:param name="parentOperationType" value="<%=operationType%>" />
	<jsp:param name="contextObjectClassName" value="ext.ptc.xworks.examples.taskform.ExampleXmlObject2" />
</jsp:include>


<script type="text/javascript">
function getClassificationNodeInternalValue() {
	var classificationNodeValueElements = $$("input[id$='~classficiation1}___textbox'][type=hidden]");
	for (var i = 0; i < classificationNodeValueElements.length; i++) {
		return classificationNodeValueElements[i].value;
	}
	return "";
}

//通过覆盖OOTB的classification的picker callback，实现在更改了分类之后重新刷新分类属性
PTC.search.picker.structuredEnumerationCallBack = PTC.search.picker.structuredEnumerationCallBack.wrap(function (ootbCallback, pickerObject, fieldId, attr, displayFieldId) {
	var updateClassification = function () {
		ootbCallback(pickerObject, fieldId, attr, displayFieldId);
		var attributePanelBuilder = "ext.ptc.xworks.examples.taskform.ExampleClassicationPanelAttributePanelBuilder";
		var componentId = "ext.ptc.xworks.examples.taskform.ExampleClassicationPanelAttributePanelBuilder";
		var extraParams = "operationType=<%=operationType%>&xmlObjectOid=<%=oid%>&classificationNode=" + newClassificationNode + "&contextObjectClassName=ext.ptc.xworks.examples.taskform.ExampleXmlObject1";
		var successCallback = function() {};
		XmlObject.reloadAttributePanel(attributePanelBuilder, componentId, extraParams, successCallback);
		document.getElementById("xworks_classificationNode").value = newClassificationNode;
	}
	
	console.log("PTC.search.picker.structuredEnumerationCallBack wrapped has been called...");
	if (pickerObject && pickerObject.pickedObject.length != 0) {
		var newClassificationNode = pickerObject.pickedObject[0].internalName;
		var currentClassificationNode = document.getElementById("xworks_classificationNode").value;
		if (getClassificationNodeInternalValue() != "") {
			if (getClassificationNodeInternalValue() != newClassificationNode) {
				// 原来有物料分类，且新的物料分类和原分类不同，则提示用户后让用户决定是否需要进行更新
				if (Ext.isIE || PTC_isIE11) {
					if (window.confirm("修改物料分类后，所有之前已填写的分类属性将会丢失，确认继续?")) {
						updateClassification();
					}
				} else {
					Ext.Msg.confirm("请确认", "修改物料分类后，所有之前已填写的分类属性将会丢失，确认继续?", function(button) {
						if (button == "yes") {
							updateClassification();
						}
					});
				}
			} else {
				//新老分类相同，不进行刷新操作
				return;
			}
		} else {
			// 原来没有选择物料分类，直接刷新
			updateClassification();
			return; // 选择的分类和新的分类相同，不需要刷新
		}

	}
});


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
		PTC.wizard.saveTableData.saveToStore(inputs[i]);
	}
}

<%
ContentHolder contentHolder = GetContentHolderFormProcessorDelegate.resolveContentHolder((NmCommandBean) request.getAttribute("commandBean"), null);
String contentHolderOid = ObjectReferenceUtils.getReferenceString(contentHolder);
%>
// 覆盖OOTB的popupAction,传递额外的参数，用于解决在编辑ExampleXmlObject2的页面中GetContentHolderFormProcessorDelegate无法获得ContentHolder的问题
popupAction = popupAction.wrap(function (ootbPopupAction, windowName, url, params0, params1, params2, params3, params4, params5, params6, params7, params8, params9, params10, params11, params12, params13, params14, params15, params16, params17, compContext, elemAddress, actionClass, actionMethod, moreInfo, alertMessage, noneMessage, isDCA, shortCut, isAjaxEnabled, ajaxClass, isSelectRequired, tableID) {
	var contentHolder = "<%=contentHolderOid%>";
	ootbPopupAction(windowName, url, params0, params1, "ContentHolder", contentHolder, params4, params5, params6, params7, params8, params9, params10, params11, params12, params13, params14, params15, params16, params17, compContext, elemAddress, actionClass, actionMethod, moreInfo, alertMessage, noneMessage, isDCA, shortCut, isAjaxEnabled, ajaxClass, isSelectRequired, tableID);	
});
</script>


<%@ include file="/netmarkets/jsp/util/end.jspf"%>