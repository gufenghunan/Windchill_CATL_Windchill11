<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ page import="com.ptc.windchill.enterprise.part.partResource,
                 com.ptc.windchill.enterprise.partInstance.partInstanceClientResource,
                 com.ptc.netmarkets.util.beans.NmCommandBean,
                 com.ptc.netmarkets.util.misc.NmContextItem,
				 java.util.Stack,
				 java.util.Map, java.util.List,wt.part.WTPart,
				 com.ptc.windchill.enterprise.part.validators.PartToDocAssociationValidationHelper"%>
				 
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt" %>

<fmt:setBundle basename="com.ptc.windchill.enterprise.part.partResource" />
<fmt:message var="referencedByDocTableHeader" key="<%= partResource.REFERENCED_BY_PARTS_TABLE_HEADER%>" />
<fmt:message var="referencedByDocPITableHeader" key="<%= partInstanceClientResource.REFERENCED_BY_PART_INSTANCES_TABLE_HEADER%>" />

<%
	NmCommandBean cb = new NmCommandBean();
    cb.setCompContext(nmcontext.getContext().toString());
    cb.setRequest(request);
      
    boolean isPartInstance = false;
    Stack stk = cb.getElementContext().getContextItems();
    int stkSize = stk.size();
    for (int i=0;i<stkSize;i++) {
       NmContextItem ci = (NmContextItem)stk.get(i);
       if (ci.getAction().equalsIgnoreCase("relatedDocumentPartInstances")) {
      	  isPartInstance = true;
       }
    }
    
	String objectType = "";
	String filterType = "";
	String tableLabel = "";
	
	if (isPartInstance) {
		objectType = "wt.part.WTProductInstance2";
	    tableLabel = (String) pageContext.findAttribute("referencedByDocPITableHeader");
	} else {
	    Map<String, String> rootAndFilterTypes  = PartToDocAssociationValidationHelper.getPartTypesStringForGivenRoleBType(commandBean,"wt.part.WTPartReferenceLink");
	    objectType = rootAndFilterTypes.get("rootTypes");
		if(objectType == null || objectType.isEmpty()){
                  objectType = WTPart.class.getName();
           }

	    filterType = rootAndFilterTypes.get("filterTypes");
	    tableLabel = (String) pageContext.findAttribute("referencedByDocTableHeader");
	}
	
	request.setAttribute("tableLabel",tableLabel);
	request.setAttribute("filterType",filterType);
	request.setAttribute("objectType",objectType);
	System.out.println("objectType:"+objectType);
%>

<wctags:itemPicker id="related_add_references" inline="true" pickerCallback="doNothing"
                      pickerTitle="${tableLabel}" multiSelect="true" 
                      componentId="RelatedObjectAddAssociation"
                      typeComponentId="PDMLink.relatedPartSearch"
                      objectType="${objectType}"
                      excludeSubTypes="${filterType}" />                      
<%@ include file="/netmarkets/jsp/util/end.jspf"%>		
