<%@page import="com.ptc.xworks.xmlobject.XmlObject"%>
<%@page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="attachments" uri="http://www.ptc.com/windchill/taglib/attachments" %>
<%@page import="wt.workflow.work.WfAssignmentState"%>
<%@page import="wt.fc.WTObject"%>
<%@page import="java.util.List"%>
<%@page import="com.catl.promotion.bean.SourceChangeAppFormBean" %>
<%@page import="com.catl.promotion.bean.SourceChangeXmlObjectBean"%>
<%@page import="com.catl.promotion.dbs.SourceChangeXmlObjectUtil" %>
<%@page import="wt.workflow.work.WorkItem"%>
<%@page import="com.ptc.netmarkets.model.NmOid"%>
<%@page import="com.catl.promotion.util.PromotionConst"%>
<jsp:useBean id="commandBean" class="com.ptc.netmarkets.util.beans.NmCommandBean" scope="request"/>
<%
	Object obj=null;
if(commandBean.getPageOid()!=null) {
	obj = commandBean.getPageOid().getRefObject();
} else {
	String oid = request.getParameter("oid");
	if (oid != null) {
		obj = NmOid.newNmOid(oid).getRefObject();
	}
}
WorkItem currentWorkItem = null;
long workItemOid = 0;
if (obj != null) {
	if (obj instanceof WorkItem) {
		String layoutFromClient = "pboId:[displayMode=HIDE]|partBranchId:[displayMode=HIDE]";
		String viewOnly = "false";
		String operationType = "EDIT";
		currentWorkItem = (WorkItem) obj;
		workItemOid = currentWorkItem.getPersistInfo().getObjectIdentifier().getId();
		String description = currentWorkItem.getDescription();
		String nodeid = "";
		if(description.indexOf(PromotionConst.source_change_submit) > 0){
			//调增采购类型
		    nodeid = PromotionConst.source_change_submit;		    
		}else if(description.indexOf(PromotionConst.source_change_pmc) > 0) {
			//PMC会签
			nodeid = PromotionConst.source_change_pmc;
		}else if(description.indexOf(PromotionConst.source_change_src) > 0) {
			//SRC会签
			nodeid = PromotionConst.source_change_src;
		}else if(description.indexOf(PromotionConst.source_change_ie) > 0) {
			//IE会签
			nodeid = PromotionConst.source_change_ie;
		}
		
		if (currentWorkItem.getStatus() == WfAssignmentState.COMPLETED
				|| description.indexOf(PromotionConst.source_change_ie) > 0
				|| description.indexOf(PromotionConst.source_change_pmc) > 0
				|| description.indexOf(PromotionConst.source_change_src) > 0) {
			operationType = "VIEW";
			viewOnly = "true";			
		}	
		
		Object pbo = currentWorkItem.getPrimaryBusinessObject().getObject();
		List<SourceChangeAppFormBean> result = SourceChangeXmlObjectUtil.getSourceChangeXmlObjectList((WTObject) pbo);
		if (result.size() > 0) {
			SourceChangeAppFormBean applicationForm = result.get(0);
%>
			<!-- 使用下面这个FormProcessorDelegate来进行XmlObject相关的Web表单解析 -->
			<input type="hidden" name="FormProcessorDelegate" value="com.ptc.xworks.xmlobject.web.form.XmlObjectExtractFormProcessorDelegate" />
			<input type="hidden" name="FormProcessorDelegate" value="com.ptc.xworks.xmlobject.web.form.GetContentHolderFormProcessorDelegate" />
						
			<!-- 使用下面这个FormProcessorDelegate来保存ExampleApplicationForm1 -->
			<%if ("EDIT".equals(operationType) || "CREATE".equals(operationType)) {//只有在编辑时，才需要使用FormProcessorDelegate去进行保存，否则可能造成数据丢失%>
			<input type="hidden" name="FormProcessorDelegate" value="com.catl.promotion.processors.SourceChangeProcessorDelegate" />
			<%} %>			
			
			<script type="text/javascript">
			// 此JS代码用来解决Table中表单可能只有部分行被提交的问题
			Ext.override(Ext.ux.grid.BufferView,{
				cacheSize : 1000,
				doClean: Ext.emptyFn
			});
			</script>	
			<%
			if (description.indexOf(PromotionConst.source_change_submit) > 0 
					|| description.indexOf(PromotionConst.source_change_ie) > 0
					|| description.indexOf(PromotionConst.source_change_pmc) > 0
					|| description.indexOf(PromotionConst.source_change_src) > 0) {
			%>		
			<!-- componentId的设置,可能导致动态刷新(创建时)出现问题 -->
			<c:set var="xmlObject" value="<%=applicationForm%>" scope="request" />
			<jsp:include page="${mvc:getComponentURL('com.catl.promotion.mvc.builders.SourceChangeTableBuilder')}">
				<jsp:param name="operationType" value="<%=operationType%>" />
				<jsp:param name="componentId" value="com.catl.promotion.mvc.builders.SourceChangeTableBuilder"/>
				<jsp:param name="applicationFormOid" value="<%=applicationForm.getIdentifier().toString() %>"/>
				<jsp:param name="viewOnly" value="<%=viewOnly%>"/>
				<jsp:param name="contextObjectClassName" value="com.catl.promotion.bean.SourceChangeXmlObjectBean" />
				<jsp:param name="layoutFromClient" value="<%=layoutFromClient %>" />
				<jsp:param name="nodeid" value="<%=nodeid%>"/>
			</jsp:include>
			<%
			} else {
			%>--NO APPLICATION FORM FOUND--<%
			}
		}
	}
}
%>
<script type="text/javascript">
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
	//window.alert(result);
	if (!result) {
		return result;
	}
	setXmlObjectFormResultNextAction("openAddPartWindow()");
	return true;
}

function openAddPartWindow() {
	popupAction('addSourceChangeObject_workitem', 'ptc1/catl/promotion/addSourceChangeObject', '','','context','comp\x24workitem\x24OR\x3Awt.workflow.work.WorkItem\x3A<%=workItemOid%>\x24|workitem\x24attributes_xworks\x24OR\x3Awt.workflow.work.WorkItem\x3A<%=workItemOid%>\x24','oid','OR\x3Awt.workflow.work.WorkItem\x3A<%=workItemOid%>','','','','','','','portlet', 'poppedup', '', '','', '', '', '', 'com.catl.promotion.processors.SourceChangeCommands', 'addSourceChangeObjects', 'width=1000,height=800', '','', false, '58493453632252', 'component', '',false, 'com.catl.promotion.mvc.builders.SourceChangeTableBuilder');
	setXmlObjectFormResultNextAction("");
}

function pasteFormAndSetNextAction() {
	var result = validateApplicationForm();
	if (!result) {
		return result;
	}
	setXmlObjectFormResultNextAction("pasteDisabledPartWindow()");
	return true;
}

function pasteDisabledPartWindow() {
	submitIt('pasteSourceChangeObject_workitem', '', '','','','','','','','','','','','','', '', '', '','', '', '', '', 'com.catl.promotion.processors.SourceChangeCommands', 'pasteSourceChangeObject', 'width=1000,height=800', '','', false, '58493453632252', 'row', '',false, 'com.catl.promotion.mvc.builders.SourceChangeTableBuilder');
	setXmlObjectFormResultNextAction("");
}

</script>
<input id="xmlobject_FormResultNextAction" type="hidden" name="xmlobject_FormResultNextAction" value=""/>

