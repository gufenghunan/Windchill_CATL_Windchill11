<!-- bcwti
 *
 * Copyright (c) 2006 Parametric Technology Corporation (PTC). All Rights
 * Reserved.
 *
 * This software is the confidential and proprietary information of PTC.
 * You shall not disclose such confidential information and shall use it
 * only in accordance with the terms of the license agreement.
 *
 * ecwti
 * -->
<%@ page import="wt.content.ContentHolder" %>
<%@ page import="wt.access.AccessPermission" %>
<%@ page import="com.ptc.windchill.enterprise.attachments.server.AttachmentsHelper" %>

<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@ taglib prefix="wip" uri="http://www.ptc.com/windchill/taglib/workinprogress"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>
<%@ taglib  uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>


<%-->
PartHelper.js below is required dynamically insert/remove the classification step
<--%>
<script language="JavaScript" src="netmarkets/javascript/part/PartHelper.js"></script>

<fmt:setBundle basename="com.ptc.windchill.enterprise.part.partResource"/>

<%--> Reuse the same labels as for create part <--%>
<fmt:message var="editAttributesWizStepLabel" key="part.createPartWizard.SET_ATTRIBUTES_WIZ_STEP_LABEL" />

<%
if (InstalledProperties.isInstalled(InstalledProperties.PARTSLINK)) {
%>
<jca:initializeItem operation="${createBean.edit}" attributePopulatorClass="com.ptc.windchill.partslink.part.forms.EditPartAttributePopulator"/>
<%} else { %>
<jca:initializeItem operation="${createBean.edit}"/>
<% } %>

<% if (request.getParameter("newInWorkspace") == null) { %>
  <%--> The part is not new in workspace. Do autoCheckout <--%>
  <wip:autoCheckOutItem/>
<%  } %>

<% if ((request.getParameter("newInWorkspace") != null) || (request.getParameter("checkedOutInWorkspace") != null)) { %>
  <script language="Javascript">newOrCheckedOutInWorkspace=true</script>
<% } %>

<%
    boolean bPermit = false;
    Object context = commandBean.getPageOid().getRef();
    bPermit = (context instanceof ContentHolder) && AttachmentsHelper.hasPermission((ContentHolder) context, AccessPermission.MODIFY_CONTENT);

%>

<%-->If SoftwareLink, PartsLink, or PDMLink is installed then use button set with Back and Next buttons<--%>
<% if (InstalledProperties.isInstalled(InstalledProperties.PARTSLINK) ||
       InstalledProperties.isInstalled(InstalledProperties.PDMLINK) ){ %>
    <c:set var="buttonSet" value="EditWizardButtons"/>
<% } else { %>
    <c:set var="buttonSet" value="NoStepsEditWizardButtons"/>
<% } %>
<% if (bPermit) { %>
<jca:wizard buttonList="${buttonSet}" helpSelectorKey="PartEdit_help">

   <%-->
    The type for the editAttributes step below
    must be 'object' and not 'part'. This needs to be done so that
    we pick up the common component action definitions for this
    step.
    Updated the editAttributes step type to 'part' for adding attachments step. B-91021
    <--%>
    <jca:wizardStep action="editAttributesWizStep" label="${editAttributesWizStepLabel}" type="part"/>
    <jca:wizardStep action="setClassificationAttributesWizStep" type="part"/>
    <jca:wizardStep action="attachments_step" type="attachments" />
</jca:wizard>
<% } else { %>
<jca:wizard buttonList="${buttonSet}" helpSelectorKey="PartEdit_help">
    <jca:wizardStep action="editAttributesWizStep" label="${editAttributesWizStepLabel}" type="part"/>
    <jca:wizardStep action="setClassificationAttributesWizStep" type="part"/>
</jca:wizard>
<% } // end if bPermit %>


<%--- If we are not DTI then add the applet for doing file browsing and file uploads --%>
<wctags:fileSelectionAndUploadAppletUnlessMSOI forceApplet='${param.addAttachments != null }'/>

<input id="selectedClfNodes" type="hidden" name="selectedClfNodes" >
<input id="selectedClfNodesDisplayName" type="hidden" name="selectedClfNodesDisplayName" >
<input id="enforceClassificationNamingRule" type="hidden" name="enforceClassificationNamingRule" >
<input id="classificationNameOverride" type="hidden" name="classificationNameOverride" >

<% if (InstalledProperties.isInstalled(InstalledProperties.PARTSLINK)) { %>
	<script language="Javascript">partsLinkInstalled=true</script>
<% } %>

<%@include file="/netmarkets/jsp/util/end.jspf"%>
<script language="Javascript">
    PTC.onReady(onloadEditPartWizard);
</script>
