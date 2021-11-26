<%@ page import="wt.content.ContentHolder" %>
<%@ page import="wt.access.AccessPermission" %>
<%@ page import="com.ptc.windchill.enterprise.attachments.server.AttachmentsHelper" %>
<%@ page import="com.ptc.core.appsec.CSRFProtector" %>


<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="wip" uri="http://www.ptc.com/windchill/taglib/workinprogress"%>

<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<input type="hidden" name="<%=CSRFProtector.NONCE_KEY%>" value="<%=CSRFProtector.getNonce(request)%>" />

<%
    boolean bPermit = false;
    Object context = commandBean.getPageOid().getRef();
    bPermit = (context instanceof ContentHolder) && AttachmentsHelper.hasPermission((ContentHolder) context, AccessPermission.MODIFY_CONTENT);

    String userAgent = commandBean.getTextParameter("ua");
    String actionName = commandBean.getTextParameter("actionName");

    boolean useCheckinEdit = false;

    NmContext nmContextObject = nmcontext.getContext();
    NmContextItem ci = (NmContextItem) nmContextObject.getContextItems().lastElement();	
		
	if(request.getParameter("CSRF_NONCE")==null){	
		%>
			<script>
				window.location.href =window.location.href +"&CSRF_NONCE=<%=java.net.URLEncoder.encode(CSRFProtector.getNonce(request), "UTF-8")%>";
			</script>
		<%
		return ;
	}
	
%>

<%-- sets up initial data for common components --%>
<jca:initializeItem operation="${createBean.edit}"/>

<%-- This tag checks out the document and sets magical form inputs and data on the command bean. This
     makes sure that the command bean's get oid methods return the oid of the working copy. --%>
<wip:autoCheckOutItem />


<%-- set up wizard steps. If we don't have modify access don't show attachments step. TODO fix this --%>
<jca:wizard title="Edit ECAD Document" helpSelectorKey="DocMgmtDocCreate" buttonList="DefaultWizardButtonsNoApply">
    <jca:wizardStep action="editECADDocumentWizStep" type="TraceSoftwareActions" label="Editar Documento ECAD"/>
</jca:wizard>

  <%--- If we are not DTI then add the applet for doing file browsing and file uploads --%>
   <wctags:fileSelectionAndUploadAppletUnlessMSOI forceApplet='false'/>

        <%--- Vertical padding to take care of above applet, otherwise a vertical scroll will be visible, which dose not look logical --%>
        <SCRIPT>
      PTC.wizard.getContentAreaPaddingHeight = PTC.wizard.getContentAreaPaddingHeight.wrap(function(orig) {
      return orig.call(this) + 12;
      });
    </SCRIPT>


<%@include file="/netmarkets/jsp/util/end.jspf"%>
