<%@ taglib uri="http://java.sun.com/jsp/jstl/core"              prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt"        prefix="fmt"%>
<%@ taglib tagdir="/WEB-INF/tags"                               prefix="wctags"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" 	   prefix="mvc" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/changeComponents" prefix="chgComp"%>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<div style='height: 96%; width: 98%;'>
<jca:describePropertyPanel var="promotionProcessDescriptor" scope="request" mode="CREATE" type="wt.maturity.PromotionNotice">
	<jca:describeProperty id="promotionProcess" />
</jca:describePropertyPanel>

<jca:getModel var="promotionProcessModel" descriptor="${promotionProcessDescriptor}"
	serviceName="com.ptc.windchill.enterprise.maturity.commands.PromotionItemQueryCommands"
  	methodName="getObject">
</jca:getModel>

<jca:renderPropertyPanel>
	<jca:addPropertyPanel model="${promotionProcessModel}"/>
	<jca:addSeparator />
</jca:renderPropertyPanel>


<chgComp:wizardParticipant/>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>

