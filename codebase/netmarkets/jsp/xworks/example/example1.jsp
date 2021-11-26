<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@taglib prefix="attachments" uri="http://www.ptc.com/windchill/taglib/attachments" %>
<%@page import="com.ptc.xworks.xmlobject.web.LayoutForView"%>

<!-- attachments:fileSelectionAndUploadApplet/-->

<jsp:include page="${mvc:getComponentURL('taskform.examples.ExampleXmlObject2AttributePanelBuilder')}">
	<jsp:param name="operationType" value="CREATE" />
	<jsp:param name="contextObjectClassName" value="ext.ptc.xworks.examples.taskform.ExampleXmlObject1" />
	<jsp:param name="layoutForView" value="stringAttr3:[displayMode=READ_ONLY,required=false]|booleanAttr2:[displayMode=READ_ONLY]" />
</jsp:include>

<jsp:include page="${mvc:getComponentURL('ext.ptc.xworks.examples.taskform.ExampleXmlObject1TableBuilder')}">
	<jsp:param name="operationType" value="VIEW" />
	<jsp:param value="builder111" name="componentId"/>
	<jsp:param name="contextObjectClassName" value="ext.ptc.xworks.examples.taskform.ExampleXmlObject1" />
	<jsp:param name="layoutForView" value="stringAttr1:[displayMode=READ_ONLY,required=true]|button2:[displayMode=READ_ONLY]" />
</jsp:include>

<jsp:include page="${mvc:getComponentURL('ext.ptc.xworks.examples.taskform.ExampleXmlObject1TableBuilder')}">
	<jsp:param name="operationType" value="VIEW" />
	<jsp:param value="builder222" name="componentId"/>
	<jsp:param name="contextObjectClassName" value="ext.ptc.xworks.examples.taskform.ExampleXmlObject1" />
	<jsp:param name="layoutForView" value="stringAttr1:[displayMode=READ_ONLY,required=true]|button2:[displayMode=READ_ONLY]" />
</jsp:include>

<jsp:include page="${mvc:getComponentURL('pet.list')}">
	<jsp:param name="operationType" value="VIEW" />
	<jsp:param name="contextObjectClassName" value="cext.ptc.xworks.examples.taskform.ExampleXmlObject1" />
	<jsp:param name="layoutForView" value="stringAttr1:[displayMode=READ_ONLY,required=true]" />
</jsp:include>

