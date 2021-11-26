<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ include file="/netmarkets/jsp/util/begin.jspf" %>
<%@ include file="/netmarkets/jsp/attachments/initAttachments.jspf"%>
<%@ include file="initPTCAttachments.jspf"%>

<%
// 在表格动态更新的情况下，不显示此部分
String dynamicUpdate = request.getParameter("dynamicUpdate");
if (!"true".equals(dynamicUpdate)) {
%>
<input type="hidden" name="formProcessorControllerName" value="com.ptc.core.components.forms.DefaultFormProcessorController"/>
<input type="hidden" name="FormProcessor" value="ext.ptc.china.gs.workflow.taskform.examples.ExampleXmlObject1CreateFormProcessor"/>
<script type="text/javascript">
// 这个函数用来验证申请单页面上的表单
var XmlObject = {};
XmlObject.validateValueByRegExp = function(inputElement, regexpStr, errorMessage) {
	//window.alert(a);
	if (inputElement.value == "" ) {
		return true;
	}
	//window.alert(regexpStr);
	//window.alert(errorMessage);
	var regexp = new RegExp(regexpStr);
	//regexp.compile();
	var value = inputElement.value;
	//window.alert(value);
	//window.alert(regexp);
	if (!regexp.test(value)) {
		window.alert(errorMessage);
		inputElement.focus();
		return false;
	}
	return true;
};

XmlObject.notifyEventToOtherAttributes = function(inputElement, eventName, attributeName, xmlObjectOid) {
	
	var toggleElement = function(elements) {
		for (var i = 0; i < elements.length; i++) {
			var e = elements[i];
			var id = e.getAttribute("id") + "";
			if (id.indexOf(xmlObjectOid) != -1) {
				var functionName = e.getAttribute("on_change_stringattr2");
				if (functionName.indexOf("enableAndRequiredForEdit") != -1) {
					var func = eval("enableAndRequiredForEdit");
					var enableWhenIsValue = functionName.substr(functionName.indexOf("|") + 1);
					func(inputElement, e, enableWhenIsValue);
				}
			}
		}
	};

	//查找属性名称为on_<eventName>_<attributeName> HTML元素, 并且ID为<xmlObjectOid>开头
	var htmlAttrName = "on_" + eventName + "_" + attributeName;
	htmlAttrName = htmlAttrName.toLowerCase();
	var selector = "textarea[" + htmlAttrName + "]";
	var elements = $$(selector);
	toggleElement(elements);
	
	selector = "input[" + htmlAttrName + "]";
	elements = $$(selector);
	toggleElement(elements);
	
	selector = "select[" + htmlAttrName + "]";
	elements = $$(selector);
	toggleElement(elements);

};

function enableAndRequiredForEdit(eventSourceElement, targetElement, enableWhenIsValue) {
	if (eventSourceElement.value == enableWhenIsValue) {
		targetElement.disabled = false;
		targetElement.readOnly = false;
		targetElement.addClassName("required");
	} else {
		targetElement.disabled = true;
		targetElement.readOnly = true;
		targetElement.removeClassName(("required"));
	}
}


function validateApplicationFormCallback() {
	//window.alert("validateApplicationFormCallback has been invoked!");
	return true;
}
</script>
<%
}
%>


<%
String pageUrl = "/netmarkets/jsp/xmlobject/taskform/example/example1.jsp";
%>
<div style="padding:0 8px 0 0">
	<table width="100%">
		<tr align="right">
			<td valign="middle" align="right" valign=top>
				<FONT class=wizardbuttonfont>
					<jsp:include page="completeButton.jsp"/>
				</FONT>
			</td>
		</tr>
	</table>
</div>
<jsp:include page="<%=pageUrl%>" />
<div style="padding:0 8px 0 0">
	<table width="100%">
		<tr align="right">
			<td valign="middle" align="right" valign=top>
				<FONT class=wizardbuttonfont>
					<jsp:include page="completeButton.jsp"/>
				</FONT>
			</td>
		</tr>
	</table>
</div>

<%
//String handleForCreate = com.ptc.xworks.xmlobject.web.util.WebUtils.generateHandleForCreate();
%>

	
<%@ include file="/netmarkets/jsp/util/end.jspf"%>

<script type="text/javascript">
var applicationFromLoadFinished = true;
</script>
