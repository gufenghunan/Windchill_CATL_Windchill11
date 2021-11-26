<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>

<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<jca:wizard buttonList="DefaultWizardButtonsNoApply" title="维护放大倍数"> 
    <jca:wizardStep action="Magnification_step" type="CatlPromotion" />
</jca:wizard>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>