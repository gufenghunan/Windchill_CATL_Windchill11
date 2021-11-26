 <%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
 
 <%@ include file="/netmarkets/jsp/components/beginWizard.jspf" %> 
 <%@ include file="/netmarkets/jsp/components/includeWizBean.jspf" %> 

 <jca:wizard title="Copy Folder"> 
           <jca:wizardStep action="setFolderAttribute" type="catl"/> 
 </jca:wizard>
 
 <%@include file="/netmarkets/jsp/util/end.jspf"%> 
