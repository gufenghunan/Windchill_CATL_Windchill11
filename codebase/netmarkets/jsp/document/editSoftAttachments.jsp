<%@page import="wt.util.WTException"%>
<%@page import="wt.vc.wip.WorkInProgressHelper"%>
<%@page import="com.ptc.netmarkets.util.misc.NetmarketURL"%>
<%@page import="com.ptc.netmarkets.model.NmOid"%>
<%@page import="wt.httpgw.URLFactory"%>
<%@page import="com.ptc.netmarkets.util.beans.NmURLFactoryBean"%>
<%@page import="com.ptc.core.components.util.OidHelper"%>
<%@page import="com.catl.ecad.utils.CommonUtil"%>
<%@page import="wt.vc.wip.Workable"%>
<%@ page import="wt.content.ContentHolder" %>
<%@ page import="wt.access.AccessPermission" %>
<%@ page import="com.ptc.windchill.enterprise.attachments.server.AttachmentsHelper" %>

<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="wip" uri="http://www.ptc.com/windchill/taglib/workinprogress"%>

<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>




<%-- This tag checks out the document and sets magical form inputs and data on the command bean. This
     makes sure that the command bean's get oid methods return the oid of the working copy. 
<wip:autoCheckOutItem />
--%>



<%
	String original_oid = commandBean.getElementContext().getPageOid().toString();
/* 	System.out.println("Testttttttttttttttt\t"+original_oid);
	Object obj = commandBean.getElementContext().getPageOid().getRefObject();
	if(obj instanceof Workable){
		Workable wk = CommonUtil.checkoutObject((Workable)obj);
		original_oid =OidHelper.getOidAsString(wk);
		System.out.println("Oidtttttttttttttt\t"+original_oid);
	} 
	
	NmURLFactoryBean arg18 = commandBean.getUrlFactoryBean();
	if (arg18 == null) {
		arg18 = new NmURLFactoryBean();
		arg18.setFactory(new URLFactory());
	}

	out.println("<script type=\'text/javascript\'>");
	NmOid arg13 = NmOid.newNmOid(original_oid);
	String arg14 = NetmarketURL.buildURL(arg18, "object", "view", arg13);
	out.println("getUpdateOpenerWindow().PTC.navigation.loadContent(\"" + arg14 + "\");");
	out.println("</script>");
	String original_oid1 = commandBean.getElementContext().getPageOid().toString();
	System.out.println("Testttttttttttttttt\t"+original_oid1); */
%>

<c:set var="buttonList" value="EditWizardButtons" scope="page"/>
<c:set var="title" value="Check In" scope="page"/>

<input type="hidden" name="original_oid" id="original_oid" value="<%=original_oid %>" />

<%-- sets up initial data for common components --%>
<jca:initializeItem operation="${createBean.edit}"/>


<%-- set up wizard steps. If we don't have modify access don't show attachments step. TODO fix this --%>


<jca:wizard title="Edit Soft Attachments" buttonList="${buttonList}">
	<jca:wizardStep action="editAttributesWizStep" type="object"/>
    <jca:wizardStep action="attachments_soft_step" type="attachments" />
</jca:wizard>




<%@include file="/netmarkets/jsp/util/end.jspf"%>
