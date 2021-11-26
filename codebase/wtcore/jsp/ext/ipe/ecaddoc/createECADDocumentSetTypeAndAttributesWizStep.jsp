<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/jcaMvc" prefix="mvc"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.ptc.com/windchill/taglib/picker" prefix="p"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/partRelDoc" prefix="partRelDoc"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/configlinkui" prefix="clui"%>
<%@ taglib prefix="w" uri="http://www.ptc.com/windchill/taglib/wrappers"%>
<%@taglib prefix="attachments" uri="http://www.ptc.com/windchill/taglib/attachments" %>

<%@ page import="com.ptc.windchill.enterprise.doc.DocumentConstants" %>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ include file="/netmarkets/jsp/components/createEditUIText.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@ taglib prefix="docmgnt" uri="http://www.ptc.com/windchill/taglib/docmgnt" %>
<%@ page import="com.ptc.windchill.enterprise.doc.documentResource" %>
<%@ page import="com.ptc.windchill.enterprise.workSet.util.WorkSetManagementHelper" %>

<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="com.ptc.windchill.enterprise.doc.documentResource" />
<fmt:message var="checkoutDownload" key="<%= documentResource.CHECKOUT_DOWNLOAD%>" />
<fmt:message var="templateSelectionMessage" key="<%= documentResource.TEMPLATE_SELECTION_MESSAGE%>" />
<fmt:message var="keepDocOpenLabel" key="KEEP_DOC_OPEN_LABEL"/>

<%-- attributes used by attachments code TODO remove this --%>
<%
String createType = "wt.doc.WTDocument";
request.setAttribute("createType", createType);
%>

<%-- Setting parameters used by insert revision TODO remove this --%>
<%
   String insertNumber = (String) request.getParameter(DocumentConstants.RequestParam.Names.INSERT_REVISION_NUMBER);
   boolean insertAction = false;
   if(insertNumber != null && insertNumber.length()>0)
   {
      insertAction = true;
   }

   request.setAttribute("insertingPart", insertAction);

   String invokedfrom = (String) request.getParameter("invokedfrom");
   String actionName = (String) request.getParameter("actionName");

   System.out.println("pickerParamFilterTypes "+request.getAttribute("pickerParamFilterTypes"));
   System.out.println("invokedfrom "+invokedfrom);
   System.out.println("actionName "+actionName);
   
   boolean invokedFromDocSB = false;

   if("docsb".equals(invokedfrom) || "insertNewDocStructureGWT".equals(actionName))
   {
       invokedFromDocSB = true;
   }

%>
<input type="hidden" id="insertingPart" name="insertingPart" value="<%=insertAction%>"></input>
<input type="hidden" name="revisionMode" id="revisionMode" value="create"/>
<%-- contains document management specific javascript methods, speicifically the name defaulting javascript --%>
<script type='text/javascript' src="netmarkets/javascript/scmContentManagement.js"></script>
<script type='text/javascript' src="netmarkets/javascript/documentManagement.js"></script>
<script type='text/javascript' src="netmarkets/javascript/attachments/attachments.js"></script>


<%-- When coming from EDA Compare this set the file for use in the attachment component
     But also it tells the attachments component to upload the file.
 --%>

<c:if test='${param.forcedFilePath != null }'>
 <c:set var="fixedFilePath" value="${param.forcedFilePath}" scope="request" />
 <c:set var="fixedFileUpload" value="true" scope="request" />
</c:if>








<div id='driverAttributesPane'>
<%@ include file="/netmarkets/jsp/components/defineItemReadOnlyPropertyPanel.jspf"%>

<c:set var="pickerParamFilterTypes" value="<%=request.getAttribute(\"pickerParamFilterTypes\")%>" />

<c:choose>

  <c:when test='${param.invokedfrom == "docsb"}' >
    <!-- If New Document wizard is invoked from Edit Structure, association constraints
         need to be enforced. (Please see the Javadoc for DefaultAssociationConstraintIT
         for more details). The list of creatable types needs to be filtered out to
         inlcude only the types allowed by association constrains. This is achieved by
         finding the list of valid (allowable) types using the tag class 
         getValidRoleBTypesForSelectedDocument below and then setting the type picker's 
         'type' parameter to 'ROOT_TYPES'-->
      <docmgnt:getValidRoleBTypesForSelectedDocument var="roleBDocTypes" />
      <jca:configureTypePicker>
          <c:forEach var="item" items="${roleBDocTypes}">
              <p:pickerParam name="seedType" value="${item}"/>
          </c:forEach>
          <c:choose>
              <c:when test='${not empty pickerParamFilterTypes}'>
                  <c:forEach var="item" items="${pickerParamFilterTypes}">
                      <p:pickerParam name="filterType" value="${item}"/>
                  </c:forEach>          
              </c:when>
              <c:otherwise>
                  <p:pickerParam name="filterType" value="wt.federation.ProxyDocument"/>
                  <p:pickerParam name="filterType" value="wt.doc.WTDocument|com.ptc.InterferenceDetectionDefinition"/>
              </c:otherwise>
          </c:choose>
          <p:pickerParam name="type" value="ROOT_TYPES"/>
      </jca:configureTypePicker>
   </c:when>
   
  <c:when test='${param.invokedfrom == "config_link_table"}' >
        <%-->   The action to create a document is from a configurable link table,
        so the types need to be selected based on the association constraints
        on the configurable link. <--%>
      <clui:getRoleBTypesForNewAction var="roleBObjectTypes" roleBBaseType="wt.doc.WTDocument"/>

       <jca:configureTypePicker>
           <c:forEach var="item" items="${roleBObjectTypes}">
             <p:pickerParam name="seedType" value="${item}"/>
           </c:forEach>
    </jca:configureTypePicker>
   </c:when>
   
   <c:when test='${param.invokedfrom == "workSet"}' >
        <%-->   The action to create a document is from a work set reference document table,
        so the types need to be selected based on the association constraints
        on the configurable link. <--%>
        <c:set var="validTypesForWorkSet" value="<%=WorkSetManagementHelper.getValidReferenceDocumentList(commandBean)%>" />
       <jca:configureTypePicker>
           <c:forEach var="item" items="${validTypesForWorkSet}">
             <p:pickerParam name="seedType" value="${item}"/>
           </c:forEach>
      </jca:configureTypePicker>
   </c:when>
   
   <c:when test="${param.noRefDoc == null || partRelDoc:getWcPDMMethodPref()}">
    <%-->   The action to create a document is not from a Part Details or Part Instance
        details page, 3rd level nav References Document table or the wcPDMMethod
        is true (any type of document can be created from the References Document
        table) don't filter out Reference Documents and its sub types. <--%>
    
    <c:choose>  
        <c:when test='${not empty pickerParamFilterTypes}'>
            <jca:configureTypePicker>
                <c:forEach var="item" items="${pickerParamFilterTypes}">
                    <p:pickerParam name="filterType" value="${item}"/>
                </c:forEach>
                <c:choose>
                  <%--> Set the seedType if its available. This is set for Reference Documents for Parts  <--%>
                  <c:when test="${param.typePickerSeedObj != null}">
                      <p:pickerParam name="seedType" value="${param.typePickerSeedObj}"/>
                  </c:when>
                </c:choose>             
            </jca:configureTypePicker>
        </c:when>
        <c:otherwise>
            <jca:configureTypePicker>       
                <p:pickerParam name="filterType"  value="wt.doc.WTDocument|com.ptc.InterferenceDetectionDefinition"/>
                <c:choose>
                  <%--> Set the seedType if its available. This is set for Reference Documents for Parts  <--%>
                  <c:when test="${param.typePickerSeedObj != null}">
                      <p:pickerParam name="seedType" value="${param.typePickerSeedObj}"/>
                  </c:when>
                </c:choose>             
            </jca:configureTypePicker>
        </c:otherwise>
    </c:choose>
    
   </c:when>

   <c:otherwise>
    <%-->   Filter out Reference Documents and their sub types from the Part Details
        page 3rd level nav References Documents table document create action<--%>
    
    <c:choose>  
        <c:when test='${not empty pickerParamFilterTypes}'>
            <jca:configureTypePicker>
                    <c:forEach var="item" items="${pickerParamFilterTypes}">
                    <p:pickerParam name="filterType" value="${item}"/>
                </c:forEach>        
            </jca:configureTypePicker>
        </c:when>
        <c:otherwise>
            <jca:configureTypePicker>
            <p:pickerParam name="filterType"
                value="wt.doc.WTDocument|com.ptc.ReferenceDocument"/>
            <p:pickerParam name="filterType"
            value="wt.doc.WTDocument|com.ptc.InterferenceDetectionDefinition"/>
            </jca:configureTypePicker>
        </c:otherwise>
    </c:choose>
            
    </c:otherwise>
</c:choose>


<%@ include file="/netmarkets/jsp/components/defineItem.jspf"%>
</div>

<br>

<% 
String showPrimaryContent = "true";
String showTemplatePicker = "true";
String showPrimaryContentFromRequest = request.getParameter("show_primary_content");
String showTemplatePickerFromRequest = request.getParameter("show_template_picker");


if ("false".equals(showPrimaryContentFromRequest))
{
    showPrimaryContent = showPrimaryContentFromRequest;
}
else
{
    showPrimaryContentFromRequest = (String)request.getAttribute("show_primary_content");
    if ("false".equals(showPrimaryContentFromRequest))
        showPrimaryContent = showPrimaryContentFromRequest;
}


if ("false".equals(showTemplatePickerFromRequest))   
{
    showTemplatePicker = showTemplatePickerFromRequest;
}
else
{
    showTemplatePickerFromRequest = (String)request.getAttribute("show_template_picker");
    if ("false".equals(showTemplatePickerFromRequest))
        showTemplatePicker = showTemplatePickerFromRequest;
}


%>

<c:set var="showPrimaryContent" value="<%=showPrimaryContent%>" />
<c:set var="showTemplatePicker" value="<%=showTemplatePicker%>" />

<c:choose>
    <c:when test='${showTemplatePicker}'>
        <div id='templatePickerDiv' style="visibility:hidden">
        <jca:renderPropertyPanel>
            <docmgnt:templatePicker id="createType"/>
            <jca:addPlaceHolder id="driverAttributes" />
        </jca:renderPropertyPanel>
        </div>
    </c:when>
    <c:otherwise>
        <input type="hidden" name="templateOid" value="default_selection"/>
    </c:otherwise>
</c:choose>
<div id='primaryContentHzLine' style="visibility:hidden">
<hr>
</div>
<%-- Renders the primary attachment component 
<c:choose>
    <c:when test='${showPrimaryContent}'>
        <wctags:primaryAttachmentWithMSOI fixedFilePath="${fixedFilePath}" fixedFileUpload="${fixedFileUpload}" defaultNameJSFunction="docSetName"/>
        <div id='templateContentDiv' style="visibility:hidden">
            <w:label id="templateContent" value="${templateSelectionMessage}"></w:label>
            <hr/>
        </div>		
    </c:when>
</c:choose>--%>

<jsp:include page="attachments_table.jsp" />


<mvc:attributesTableWizComponent/>
<% 
    String userAgent = commandBean.getTextParameter("ua");
    boolean isDTI = true;
    if(userAgent != null && userAgent.equals("DTI"))
        isDTI=true;

    String createFromTemplateDTIFromRequest = commandBean.getTextParameter("createFromTemplateDTI");
    boolean createFromTemplateDTI = false;
    if(createFromTemplateDTIFromRequest != null && createFromTemplateDTIFromRequest.equals("true"))
        createFromTemplateDTI=true;
%>
<%-- if checked it will cause the form processor to check out the document after it was created --%>

<div id='checkboxkeepCheckedOutDiv' style="visibility:hidden">

<% if(!isDTI) { %>
    <%@ include file="/netmarkets/jsp/components/keepCheckedOutCheckbox.jspf"%>
<% } else if(!createFromTemplateDTI) { %>
<%      if("true".equalsIgnoreCase(request.getParameter("isOutlook"))) { %>     
            <wrap:checkBox name="keepCheckedOutDTI" id="keepCheckedOutDTI" label="${keepCheckedOutLabel}" renderLabel="true" renderLabelOnRight="true" renderExtra="disabled"/>
<%      } else { %>
            <wrap:checkBox name="keepCheckedOutDTI" 
                           id="keepCheckedOutDTI" 
                           label="${keepCheckedOutLabel}" 
                           renderLabel="true" 
                           renderLabelOnRight="true" 
                           renderExtra="onclick=checkedOutClicked();" />
            <br/>
            <docmgnt:prefCheckBox name="keepDocOpen" 
                                  id="keepDocOpen" 
                                  label="${keepDocOpenLabel}"
                                  checkBoxPref="/com/ptc/windchill/enterprise/attachments/keepDocOpen" 
                                  renderLabel="true" 
                                  renderLabelOnRight="true" />          
<%      } %>
<% } %>

</div>

<br>

<div id='checkboxcheckoutDownloadDiv' style="display: none">
<% if(!invokedFromDocSB){%>
<docmgnt:prefCheckBox name="checkoutDownload" id="checkoutDownload" label="${checkoutDownload}" checkBoxPref="/com/ptc/windchill/doc/defaultCheckoutOnCreateFromTemplate" renderLabel="true" renderLabelOnRight="true" />
<%}%>
</div>

<input type = "hidden" name = "lastSelectedType" id  ="lastSelectedType" value = "">
<input type = "hidden" name = "scmHiddenElement" id  ="scmHiddenElement" value = "">

<script language='Javascript'>
// From the common component we are calling our local pickerGo which calls the original pickerGo and then calls populateTemplates
pickerGo = pickerGo.wrap(function(original,value, currentObjectHandle, template) {
   original(value, currentObjectHandle, template);
   if (!template)
   populateTemplates(value);
})

var createType = document.getElementById('createType');

// When the type is selected by default , we need to call populateTemplates
if (createType!= null && createType!= 'undefined' && createType.value!= 'undefined' && createType.value!= "")
populateTemplates(createType.value);

//This is to hide the primary content, KeepCheckout checkbox initially
var localType = '';
if (createType!= null && createType!= 'undefined' && createType.value!= 'undefined' && createType.value!= "")
{
    localType = createType.value;
}
updatePrimaryContent(localType);

function checkedOutClicked(){
    if(window.document.getElementsByName("keepCheckedOutDTI")[0].checked){
        window.document.getElementsByName("keepDocOpen")[0].checked = true;
        window.document.getElementsByName("keepDocOpen")[0].disabled = true;
    } else {
        window.document.getElementsByName("keepDocOpen")[0].checked = false;
        window.document.getElementsByName("keepDocOpen")[0].disabled = false;
    }
}

function showalert(){
    alert();
}

//Trigger a download when creating a document from template in case checkout and download checkbox is checked
PTC.action.on('objectsaffected', function(formResult) {
   var url = formResult.extraData.downloadUrl;
   if (url) {
       var opener = getOpener().open(url);   
   }
});

 function getOpener(){
            if (opener) {
                return opener;
            }
            else if (top.opener) {
                return top.opener;
            }
            return window;
        }

window.docSetName = function (name, contentSource) {
	var nameInputs = getFormInputs(/^NameInputId.*[^_][^o][^l][^d]$/, 'id');
	var fullname= document.getElementById('primaryFilepathInput').value;
	if (contentSource && contentSource == 'FILE' && name.lastIndexOf(".") > -1) {
		name = name.substring(0,name.lastIndexOf("."));
	}

	var pdffullname = fullname.substring(0,fullname.lastIndexOf("."))+".pdf";
	var xmlfullname = fullname.substring(0,fullname.lastIndexOf("."))+".xml";

	if (nameInputs && nameInputs.length>0 && nameInputs[0]!=null && typeof nameInputs[0]!= 'undefined'&& nameInputs[0].value == '') {

		if(nameInputs[0].maxLength > 0 && name.length > nameInputs[0].maxLength)
			name = name.substring(0, nameInputs[0].maxLength)
		nameInputs[0].value = name;
	}

	/*alert(pdffullname);
	alert(xmlfullname);

	document.getElementById('pdffile').value= name;
	alert(document.getElementById('pdffile').value);*/
}
</script>

<script type="text/javascript">
	function validateAttachments(){
		var content=document.getElementById('content').value;
		var pdffullname =document.getElementById('pdffile').value;
		var xmlfileCables = document.getElementById('xmlfileCables').value;
		var xmlfileComponentes=document.getElementById('xmlfileComponentes').value;		
	
		if(pdffullname=='' || xmlfileCables=='' || content==''){
			alert('WARNING: You must select all files');
			return false;
		}
		return true;

	}
		
</script>



<%@ include file="/netmarkets/jsp/util/end.jspf"%>
