<%@ page import="wt.content.ContentHolder" %>
<%@ page import="wt.access.AccessPermission" %>
<%@ page import="com.ptc.windchill.enterprise.attachments.server.AttachmentsHelper" %>

<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>

<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ include file="/netmarkets/jsp/components/createEditUIText.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<%-- To display Object type, organization,etc --%>
<%@ include file="/netmarkets/jsp/components/setAttributesReadOnlyPropertyPanel.jspf"%>

<jca:describeAttributesTable var="attributesTableDescriptor" id="editSetAttributes" mode="VIEW"
    componentType="WIZARD_ATTRIBUTES_TABLE" type="wt.change2.WTChangeActivity2" label="${attributesTableHeader}"
    scope="request">
   <jca:describeProperty id="number"/>
   <jca:describeProperty id="name"/>
   <jca:describeProperty id="needDate"/>
   <jca:describeProperty id="description"/>
   <jca:describeProperty id="ALL_CUSTOM_HARD_ATTRIBUTES_FOR_INPUT_TYPE"/>
   <jca:describeProperty id="ALL_SOFT_NON_CLASSIFICATION_SCHEMA_ATTRIBUTES"/>
   
</jca:describeAttributesTable>

<%-- renders the read only attributes panel which includes information about Object type, organization,etc --%>
<%@ include file="/netmarkets/jsp/components/getSetAttributesWizStepModels.jspf"%>

<c:if test="${requestScope.attributesStepReadOnlyPanel != null}">
   <jca:renderPropertyPanel model="${attributesStepReadOnlyPanelModel}"/>
</c:if>

<c:if test="${requestScope.attributesTableDescriptor != null}">
   <jca:renderTable model="${tableModel}" />
</c:if>

<input type="hidden" name="createType" value="${nmcontext.context.pageType}">

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
