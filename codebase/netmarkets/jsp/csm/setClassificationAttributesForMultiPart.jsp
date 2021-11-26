<%@page import="com.ptc.jca.mvc.components.JcaComponentParamsFactory, com.ptc.jca.mvc.components.JcaComponentParams"%>
<%@page import="com.ptc.core.meta.common.TypeIdentifier, com.ptc.jca.mvc.components.JcaComponentParamsUtils, wt.util.HTMLEncoder"%>
<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<%--
PartHelper.js below is required dynamically insert/remove the classification step
--%>
<script language="JavaScript" src="netmarkets/javascript/part/PartHelper.js"></script>

<%
String wizType = HTMLEncoder.encodeAndFormatForHTMLContent(request.getParameter("wizardType"), false);
%>
<input id="wizType" type="hidden" name="wizType" value="<%= wizType %>">


<!-- this field is being used partHelper.js to restrict autoNaming -->

<%
JcaComponentParamsFactory  jcaComponentParamsFactory = new JcaComponentParamsFactory();
JcaComponentParams params1 = (JcaComponentParams)jcaComponentParamsFactory.createComponentParams(request, response);
TypeIdentifier ti = JcaComponentParamsUtils.getInstance().getContextObjectTypeIdentifier(params1);
String typeName = ti.getTypename();
//if(typeName==null||typeName.equals("")){
	typeName="wt.part.WTPart|com.CATLBattery.CATLPart";
//}

%>

<fmt:setBundle basename="com.ptc.windchill.csm.client.csmClientResource"/>
<fmt:message var="titleNameHeader" key="part.setClassificationAttributesForMultiPart.title" />
<c:set var="buttonList" value="DefaultWizardButtonsNoApply" scope="page"/>


<jca:initializeItem operation="${createBean.create}" baseTypeName="<%=typeName %>" attributePopulatorClass="com.ptc.windchill.enterprise.part.forms.PartAttributePopulator" />

<jca:wizard helpSelectorKey="PartMultipleCreate" buttonList="${buttonList}" title="${titleNameHeader}" type="NEW" >
	<jca:wizardStep action="selectClassificationAttributesForMultiPartWizStep" type="part"/>
	<jca:wizardStep action="setClassificationAttributesWizStep" type="part"/>
</jca:wizard>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
