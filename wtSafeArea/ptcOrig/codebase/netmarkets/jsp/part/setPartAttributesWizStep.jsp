<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/partclient" prefix="partcomponent"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="wrap"%>


<%@ page import="com.ptc.windchill.enterprise.part.partResource" %>
<%@ page import="com.ptc.windchill.enterprise.part.PartConstants" %>

<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ include file="/netmarkets/jsp/components/createEditUIText.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<fmt:setBundle basename="com.ptc.windchill.enterprise.part.partResource"/>

<fmt:message var="classificationLabel" key="<%= partResource.CLASSIFICATION_LABEL %>" />
<fmt:message var="locationLabel" key="<%= partResource.LOCATION_LABEL %>" />
<fmt:message var="stopEffPropagationLabel" key="<%= partResource.STOP_EFFECTIVITY_PROPAGATION %>" />
<fmt:message var="typeNameLabel" key="<%= partResource.TYPE_NAME_LABEL %>" />

<fmt:message var="minimumRequiredLabel" key="<%= partResource.MINIMUM_REQUIRED_LABEL %>" />
<fmt:message var="maximumAllowedLabel" key="<%= partResource.MAXIMUM_ALLOWED_LABEL %>" />

<fmt:message var="createCADDocument" key="<%= partResource.CREATE_CAD_DOC_FROM_NEW_PART_LABEL %>" />

<partcomponent:multiPartWizardLaunch multiPart="isMultiPart"/>

<%
    String insertNumber = (String) request.getParameter(PartConstants.RequestParam.Names.INSERT_REVISION_NUMBER);
	boolean insertAction = false;
	if(insertNumber != null && insertNumber.length()>0)
	   insertAction = true;
	request.setAttribute("insertingPart", insertAction);
%>

<jca:describePropertyPanel var="attributesStepReadOnlyPanel" id="attributesStepReadOnlyPanel"
	scope="request" mode="CREATE" type="wt.part.WTPart">

	<jca:describeProperty id="containerName" label="${createBean.containerLabel}" mode="VIEW"/>
        <jca:describeProperty id="itemType" need="type" mode="VIEW"/>        
        <jca:describeProperty id="orgid" need="organization.id" mode="VIEW"/>
</jca:describePropertyPanel>

<%-->Build a table descriptor and assign it to page variable td<--%>
<jca:describeAttributesTable var="attributesTableDescriptor" scope="request"
	id="create.setAttributes" mode="CREATE"
	componentType="WIZARD_ATTRIBUTES_TABLE"
	type="wt.part.WTPart" label="${attributesTableHeader}">

   <%-->
      Include name and number only when the wizard is not "Multi-part Create" wizard.
   <--%>

   <c:choose>
      <c:when test="${isMultiPart != 'true'}">
         <jca:describeProperty id="number"/>
         <jca:describeProperty id="name"/>
      </c:when>
   </c:choose>

   <jca:describeProperty id="view.id"/>
   <jca:describeProperty id="partType"/>
   <jca:describeProperty id="classification.id" label="${classificationLabel}"/>
   <jca:describeProperty id="<%=PartConstants.ColumnIdentifiers.WADM_CONTRACT_NUMBER%>"/>
   <jca:describeProperty id="defaultTraceCode"/>
   <jca:describeProperty id="defaultUnit"/>
   <jca:describeProperty id="<%=PartConstants.ColumnIdentifiers.WADM_JOB_AUTH_NUMBER%>"/>
   <jca:describeProperty id="lifeCycle.id"/>
   <jca:describeProperty id="folder.id" label="${locationLabel}" />
   <jca:describeProperty id="<%=PartConstants.ColumnIdentifiers.WADM_PHASE%>"/>
   <jca:describeProperty id="source"/>
   <jca:describeProperty id="<%=PartConstants.ColumnIdentifiers.STOP_EFF_PROPAGATION%>" label="${stopEffPropagationLabel}" selectionListStyle="dropdown"/>
   <jca:describeProperty id="phantom" selectionListStyle="dropdown"/>
   <jca:describeProperty id="collapsible" selectionListStyle="dropdown"/>
   <jca:describeProperty id="teamTemplate.id"/>
    <jca:describeProperty id="minimumRequired" label="${minimumRequiredLabel}"/>
    <jca:describeProperty id="maximumAllowed" label="${maximumAllowedLabel}"/>
	
   <jca:describeProperty id="ALL_CUSTOM_HARD_ATTRIBUTES_FOR_INPUT_TYPE"/>
   <jca:describeProperty id="ALL_SOFT_NON_CLASSIFICATION_SCHEMA_ATTRIBUTES"/>

   <%--
      Display a data utility for revision label picker if insert revision action is executed
   --%>

   <c:if test="${insertingPart}">
      <jca:describeProperty id="revision" dataUtilityId="revisionPicker">
         <jca:setComponentProperty key="revisionMode" value="create"/>
      </jca:describeProperty>
   </c:if>

</jca:describeAttributesTable>

<%@ include file="/netmarkets/jsp/components/setAttributesWizStep.jspf"%>

<%-- Optionally display check box for New CAD Doc step --%>
<c:if test='${param.showNewCADDocStep == "true"}'>
    <wrap:checkBox name="createCADDocForPart" id="createCADDocForPart" label="${createCADDocument}" renderLabel="true" 
        renderLabelOnRight="true" checked="false" onchange="toggleCreateCADDocStep('part.defineCADDocument!~objectHandle~caddocHandle~!');" />
</c:if>
<br>
<c:if test='${param.invokedfrom != "workspace"}'>
    <%@ include file="/netmarkets/jsp/components/keepCheckedOutCheckbox.jspf"%>
</c:if>


<input id="selectedClfNodes" type="hidden" name="selectedClfNodes" >
<input id="selectedClfNodesDisplayName" type="hidden" name="selectedClfNodesDisplayName" >



<%@ include file="/netmarkets/jsp/util/end.jspf"%>