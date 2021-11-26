<%@ page language="java" session="true" pageEncoding="UTF-8"%>
<%@ include file="/netmarkets/jsp/util/beginPopup.jspf"%>
<%@ taglib prefix="mvc" uri="http://www.ptc.com/windchill/taglib/mvc"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/partclient" prefix="partclient"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>
<%@ page import="java.util.*"%>
<%@page import="wt.fc.WTObject"%>
<%@page import="com.catl.change.*" %>
<%@page import="wt.workflow.work.WorkItem"%>
<%@page import="com.ptc.netmarkets.model.NmOid"%>

<%
	Object obj=null;
	String oid = request.getParameter("oid");
	if (oid != null) {
		obj = NmOid.newNmOid(oid).getRefObject();
	}
	WorkItem currentWorkItem = null;
	String flag = "";
	if (obj != null) {
		if (obj instanceof WorkItem) {
			currentWorkItem = (WorkItem) obj;
			Object pbo = currentWorkItem.getPrimaryBusinessObject().getObject();
			flag = ChangeUtil.isHasdocLinkByDcn(pbo);
		}
	}
	
%>

<%
	if(flag.equals("true")){
%>
<input type="hidden" name="FormProcessorDelegate" value="com.ptc.xworks.xmlobject.web.form.GetContentHolderFormProcessorDelegate" />
<input type="hidden" name="FormProcessorDelegate" value="com.catl.change.processors.DcnTableFromProcessDelegate" />
<div style="padding: 0pt 8px;">
	<div class="inlineHelpBox">
	    <table>
	        <tr>
	            <td><img src="wtcore/images/tip.gif" border="0"></td>
	            <td>请确认：以下待变更的物料已经包含了”非FAE成熟度3升级报告”，请按照非FAE成熟度3升级报告的管理规则执行<input type="checkBox" id="checktext" name="null___checktext___checktext"/></td>
	        </tr>
	    </table>
	</div>
</div>
<jsp:include page="${mvc:getComponentURL('com.catl.change.mvc.DcnDataInfoTableBuilder')}">
		<jsp:param name="operationType" value="VIEW" />
		<jsp:param name="componentId" value="com.catl.change.mvc.DcnDataInfoTableBuilder"/>
		<jsp:param name="contextPath" value=""/>
		<jsp:param name="applicationFormOid" value=""/>
		<jsp:param name="viewOnly" value="true"/>
		<jsp:param name="contextObjectClassName" value="" />
		<jsp:param name="layoutFromClient" value="" />
		<jsp:param name="nodeid" value="changeorder2_temp_submitdcr"/>
</jsp:include>
<%
	}
%>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
