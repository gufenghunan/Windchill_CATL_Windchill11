<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ page 
session="false"
import="com.ptc.windchill.enterprise.servlets.AttachmentsDownloadDirectionServlet"
import="com.ptc.windchill.enterprise.attachments.server.AttachmentsDownloadDirectionResponse"
import="com.ptc.windchill.enterprise.attachments.server.AttachmentsDownloadDirectionResponseItem"
import="com.ptc.xworks.xmlobject.web.util.AttachmentDownloadUtils"
import="wt.util.WTProperties"
%><%
AttachmentDownloadUtils downloadUtils = new AttachmentDownloadUtils();
downloadUtils.processRequest(request, response);
AttachmentsDownloadDirectionResponse addResponse = (AttachmentsDownloadDirectionResponse)
request.getAttribute(AttachmentsDownloadDirectionServlet.REQUEST_KEY_DOWNLOAD_DIRECTION);
AttachmentsDownloadDirectionResponseItem theOne = addResponse.getSingletonItem(); // should be only one for browser download

String strCodeBase = WTProperties.getLocalProperties().getProperty("wt.server.codebase", null);
String closeURL = strCodeBase + "/netmarkets/jsp/attachments/download/closePopupForIE7.jsp";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>
<script type="text/javascript">
var downloadURL = "<%= theOne.getDownloadURLString() %>";
var downloadInNewWindow = <%=request.getAttribute("downloadInNewWindow")%>;
if (downloadInNewWindow) {
	// 在新的窗口下载附件
	window.location.href = downloadURL;
} else {
	// OOTB 的逻辑
	var crossDomainParent = false;
	try {
	    //If the download is being embedded in an iframe from a page in another domain then
	    //accessing window.parent properties will throw an exception
	    crossDomainParent = window.parent && !window.parent.location.href;
	}
	catch (e) {
	    crossDomainParent = true;
	}
	if (crossDomainParent) {        
	    window.location.href = downloadURL;
	}
	else if (window.parent && window.parent.windchillmain) {
	    // for structure doc where popup is launched from a sub-frame instead of main window.
	    // when main window refreshed, the sub-frame object for IE is still exist but not accessable?%@!
	    // therefore we need the popup to remember a link (var windchillmain) to the main window,
	    // and use a iframe to execute the download       

	    window.parent.windchillmain.location.href = downloadURL;
	    window.parent.open("<%= closeURL %>", "_self");        
	} else if (window.opener){
	    <%-- > This if the action type is popup.< --%>
	    window.opener.top.location.href = downloadURL;
	    window.open("<%= closeURL %>", "_self");

	} else {
	    window.top.location.href = downloadURL;
	}
}

</script>
</body>
</html>