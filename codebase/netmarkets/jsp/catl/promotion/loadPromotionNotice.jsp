<%@include file="/netmarkets/jsp/util/beginPopup.jspf" %>
<%@page import="com.catl.promotion.PromotionCreateHelper"%>
<%@page import="wt.fc.WTObject"%>
<%@page import="java.util.ArrayList"%>
<%@page import="wt.maturity.PromotionNotice"%>
<%@page import="wt.fc.ReferenceFactory"%>

<%
	//Initial Session
	String contextPath = request.getContextPath();
	session.setAttribute("PromotionInfo", null);
	session.setAttribute("PromotableList", null);
	session.setAttribute("PromotionObject", null);
	session.setAttribute("errorMess", null);
	String oid = request.getParameter("oid");
	try{
		ReferenceFactory rf = new ReferenceFactory();
		WTObject wtobject = (WTObject) rf.getReference(oid).getObject();
		System.out.println(wtobject);
		if(wtobject instanceof PromotionNotice){
%>
<script>window.location="<%=contextPath%>/netmarkets/jsp/catl/promotion/promotionModifyWizStep1.jsp?oid=<%=oid%>";</script>
<%
		}
	}catch(Exception ex){
		System.out.println(ex.getMessage());
%>
		<script>
			alert("<%=ex.getMessage()%>");
			window.open('','_self');  
			window.opener=null;
			window.close();
		</script>
<%
	}
%>


<%@ include file="/netmarkets/jsp/util/end.jspf"%>