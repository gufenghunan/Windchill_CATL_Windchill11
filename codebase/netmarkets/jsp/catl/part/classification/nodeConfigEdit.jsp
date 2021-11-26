<%@ page pageEncoding="UTF-8"%>
<%@taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<jca:initializeItem baseTypeName="com.catl.part.classification.ClassificationNodeConfig" operation="${createBean.edit}" attributePopulatorClass="com.ptc.core.components.forms.DefaultAttributePopulator"/>
<jca:wizard title="配置成熟度相关属性" buttonList="DefaultWizardButtonsNoApply">
<jca:wizardStep action="nodeConfigEditStep" type="catlnodeconfig"/>
</jca:wizard>
<%@include file="/netmarkets/jsp/util/end.jspf"%>