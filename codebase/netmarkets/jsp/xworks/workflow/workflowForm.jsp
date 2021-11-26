<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@
taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%><%@
taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%><%@
taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="comp"%><%@
taglib uri="http://www.ptc.com/windchill/taglib/core" prefix="wc"%><%@
taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%><%@
include file="/netmarkets/jsp/util/begin.jspf"%><%@
page import="com.ptc.netmarkets.model.NmOid,
			wt.type.Typed,
			com.ptc.xworks.windchill.util.ObjectTypeUtils,
			com.ptc.xworks.util.XWorksHelper,
			com.ptc.xworks.workflow.form.WorkflowFormConfigReader,
			com.ptc.xworks.workflow.annotation.WorkflowFormConfig,
			org.apache.commons.lang.StringUtils,
            wt.fc.Persistable"%><%

NmOid oid = commandBean.getPageOid();
Persistable contextObject = (Persistable) oid.getRefObject();
String internalName = ObjectTypeUtils.getInternalTypeName((Typed) contextObject);
WorkflowFormConfigReader configReader = XWorksHelper.getWorkflowFormConfigReader();
WorkflowFormConfig formConfig = configReader.getWorkflowFormConfig(internalName);
if (formConfig == null) {
	out.println("ERROR: Cannot get WorkflowFormConfig by internal type name :" + internalName);
} else {
	if (StringUtils.isNotBlank(formConfig.formJspPath())) {
		%><jsp:include page="<%=formConfig.formJspPath()%>" /><%
	} else {
		out.println("ERROR: formJspPath of  WorkflowFormConfig is blank! internal type name :" + internalName);
	}
}


%><%@include file="/netmarkets/jsp/util/end.jspf"%>