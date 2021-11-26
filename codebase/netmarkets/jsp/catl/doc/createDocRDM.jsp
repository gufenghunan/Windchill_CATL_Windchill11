<%@ page import="com.catl.integration.rdm.RdmIntegrationHelper"%>
<%@ page import="java.net.URLDecoder"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/jcaMvc" prefix="mvc"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<% 
	String projectCode = request.getParameter("projectCode");
	String projectName = request.getParameter("projectName");
	String deliverableId = request.getParameter("deliverableId");
	String docType = request.getParameter("docType");
	String subCategory = request.getParameter("subCategory");
	
	projectCode = URLDecoder.decode(projectCode, "utf-8");
	projectName = URLDecoder.decode(projectName, "utf-8");
	deliverableId = URLDecoder.decode(deliverableId, "utf-8");
	docType = URLDecoder.decode(docType, "utf-8");
	/* projectCode = new String(URLDecoder.decode(projectCode, "utf-8").getBytes("iso-8859-1"),"utf-8");
	projectName = new String(URLDecoder.decode(projectName, "utf-8").getBytes("iso-8859-1"),"utf-8");
	deliverableId = new String(URLDecoder.decode(deliverableId, "utf-8").getBytes("iso-8859-1"),"utf-8");
	docType = new String(URLDecoder.decode(docType, "utf-8").getBytes("iso-8859-1"),"utf-8"); */
	
	
	boolean ret = RdmIntegrationHelper.existFolder(projectCode, projectName);//是否存在该文件夹
	String basePath="http:\\//"+request.getServerName()+request.getContextPath();
	String error = basePath+"/netmarkets/jsp/catl/doc/error.jsp";
%>
<script language="javascript" type="text/javascript">
	if("false" == "<%=ret%>"){ 
		var error = "<%=error%>"+"?errorType=1";
		window.location.href=error; 
	}
	var docTypeValue="";
	if(<%=WTMessage.getLocalizedMessage("com.catl.integration.rdm.resource.CatlRDMResource","selectType.technicalDoc").equals(docType) %> == true){
		docTypeValue="wt.doc.WTDocument|com.ptc.ReferenceDocument|com.CATLBattery.technicalDoc";
	}
	if(<%=WTMessage.getLocalizedMessage("com.catl.integration.rdm.resource.CatlRDMResource","selectType.rdDoc").equals(docType) %> == true){
		docTypeValue="wt.doc.WTDocument|com.ptc.ReferenceDocument|com.CATLBattery.rdDoc";
	}
	function selectSubmit(){
		
		if(docTypeValue == ""){
			var error = "<%=error%>"+"?errorType=2";
			window.location.href=error; 
			return;
		}
		
		var ajaxOptions = {
			asynchronous : false,
			method : "GET",
			onSuccess : function(result, options) {
				window.location.href=result.responseText.trim(); 
			},
			parameters : {
				docTypeValue : docTypeValue,
				docType : "<%=docType%>",
				projectCode : "<%=projectCode%>",
				projectName : "<%=projectName%>",
				deliverableId : "<%=deliverableId%>",
				subCategory : "<%=subCategory%>"
			}
		};
		requestHandler.doRequest("netmarkets/jsp/catl/doc/queryURL.jsp?date="+new Date(), ajaxOptions);//ajax查询创建文档URL
	}
	selectSubmit()
</script>
<body>
	<%-- <div style="text-align:center;margin:0 auto;line-height: 200px;">
		<select id="docType">
			<option value="">-- <%=WTMessage.getLocalizedMessage("com.catl.integration.rdm.resource.CatlRDMResource","selectType.default") %> --</option>
			<option value="wt.doc.WTDocument|com.ptc.ReferenceDocument|com.CATLBattery.technicalDoc"><%=WTMessage.getLocalizedMessage("com.catl.integration.rdm.resource.CatlRDMResource","selectType.technicalDoc") %></option>
			<option value="wt.doc.WTDocument|com.ptc.ReferenceDocument|com.CATLBattery.rdDoc"><%=WTMessage.getLocalizedMessage("com.catl.integration.rdm.resource.CatlRDMResource","selectType.rdDoc") %></option>
		</select>
		<input type="button" onclick="selectSubmit()" value="<%=WTMessage.getLocalizedMessage("com.catl.integration.rdm.resource.CatlRDMResource","selectType.submit") %>"/>
	</div> --%>
	
</body>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>