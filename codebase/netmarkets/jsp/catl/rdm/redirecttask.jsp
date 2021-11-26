<%@page import="com.catl.integration.rdm.RedirectMenu"%>

<%@ page import="java.util.HashMap,com.ptc.netmarkets.util.misc.NetmarketURL"%>
<%@ page import="java.util.HashMap"%>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/jcaMvc" prefix="mvc"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<%
	String url = commandBean.getTextParameter("redirectto");
	System.out.println(">>>>>>url 1="+url);
	String[] urlStr = url.split("page=");
	String pagestr = "";
	System.out.println(urlStr.length);
	if (urlStr.length == 2) {
		pagestr = urlStr[1].replaceAll("&", "%26");
		pagestr = pagestr.replaceAll("[?]", "%3F");
		String s1=urlStr[0];
		if(s1.indexOf("username")!=-1){

		}else{
	        String userName = "";
	        wt.org.WTPrincipal currentUser = (wt.org.WTUser) wt.session.SessionHelper.manager.getPrincipal();
	        userName = currentUser.getName();
	        System.out.println(">>>>>>"+url);
	        s1=s1.replaceAll("windchillAdapter.jsp[?]", "windchillAdapter.jsp?username="+userName+"&");
		}
		url = s1 + "page=" + pagestr;
	}

	System.out.println(">>>>>>url 2="+url);
%>
<body>

<div id="redirect">
<iframe SRC="<%=url%>" height="100%" width="100%" SCROLLING="no">
</div>
<script>
	document.getElementById("redirect").style.height = document.getElementById("contentDiv").parentNode.style.height;
</script>
</body>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>