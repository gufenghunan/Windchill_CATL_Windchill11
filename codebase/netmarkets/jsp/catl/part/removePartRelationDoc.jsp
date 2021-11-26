<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<fmt:setBundle basename="com.catl.part.resource.CATLPartRB"/>

<jca:wizard title="RemovePartRelationDoc" buttonList="DefaultWizardButtonsNoApply" >
    <jca:wizardStep action="removePartRelationStep" type="catlpart"/>
</jca:wizard>

<%@include file="/netmarkets/jsp/util/end.jspf"%>