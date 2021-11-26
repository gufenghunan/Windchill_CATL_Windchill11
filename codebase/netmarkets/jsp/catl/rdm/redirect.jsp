<%@page import="com.catl.integration.rdm.RedirectMenu"%>

<%@ page import="java.util.HashMap,com.ptc.netmarkets.util.misc.NetmarketURL"%>
<%@ page import="java.util.HashMap"%>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/jcaMvc" prefix="mvc"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<% 
	String actionid = commandBean.getTextParameter("actionid");
	String url = RedirectMenu.getRedirectURL(actionid);
	String[] urlStr = url.split("page=");
	String pagestr = "";
	System.out.println(urlStr.length);
	if (urlStr.length == 2) {
		pagestr = urlStr[1].replaceAll("&", "%26");
		pagestr = pagestr.replaceAll("[?]", "%3F");
		url = urlStr[0] + "page=" + pagestr;
	}
	
	System.out.println(">>>>>>url 2="+url);
	System.out.println(url);
	System.out.println(actionid);
%>

<body>

<div id="redirect">
<iframe SRC="<%=url%>" height="100%" width="100%" SCROLLING="no"> 
</div>
<script>
	document.getElementById("redirect").style.height = document.getElementById("contentDiv").parentNode.style.height;
	Ext.EventManager.onWindowResize(function(width ,height){
		document.getElementById("redirect").style.height = height - 87;
	});
</script>
</body>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>