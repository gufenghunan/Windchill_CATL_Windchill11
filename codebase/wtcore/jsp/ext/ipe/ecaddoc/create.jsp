<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" 
prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" 
prefix="fmt"%>
<%@taglib prefix="attachments" uri="http://www.ptc.com/windchill/taglib/attachments" %>
<%@ page import="com.ptc.windchill.enterprise.part.PartConstants"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>

<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<fmt:setBundle basename="com.ptc.windchill.enterprise.doc.documentResource"/>

<script language="JavaScript" src='netmarkets/javascript/util/revisionLabelPicker.js'></script>


<jca:initializeItem operation="${createBean.create}"
   baseTypeName="WCTYPE|wt.doc.WTDocument|com.integralplm.DocumentoECAD"/>
   
   <docmgnt:validateNameJSTag/>

<jca:wizard title="New ECAD Document" helpSelectorKey="DocMgmtDocCreate" buttonList="DefaultWizardButtonsNoApply">
	<jca:wizardStep action="createECADDocumentWizStep" type="TraceSoftwareActions" label="New ECAD Document"/>
</jca:wizard>

  <%--- If we are not DTI then add the applet for doing file browsing and file uploads --%>
   <wctags:fileSelectionAndUploadAppletUnlessMSOI forceApplet='false'/>

        <%--- Vertical padding to take care of above applet, otherwise a vertical scroll will be visible, which dose not look logical --%>
        <SCRIPT>
      PTC.wizard.getContentAreaPaddingHeight = PTC.wizard.getContentAreaPaddingHeight.wrap(function(orig) {
      return orig.call(this) + 12;
      });
	  
    </SCRIPT>


<%@ include file="/netmarkets/jsp/util/end.jspf"%>