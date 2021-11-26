<%@page import="java.util.HashMap"%>
<%
	String contextPath = request.getContextPath();

	HashMap hmPromotionInfo = new HashMap();
	String promotionName = request.getParameter("inputPromotionName");
	String promotionDesc = request.getParameter("inputPromotionDesc");
	String promotionItem = request.getParameter("inputPromotionItem");
	String oid = request.getParameter("oid");
	
	hmPromotionInfo.put("inputPromotionName",promotionName);
	hmPromotionInfo.put("inputPromotionDesc",promotionDesc);
	hmPromotionInfo.put("inputPromotionItem",promotionItem);
	
	session.setAttribute("PromotionInfo", hmPromotionInfo);
%>
<script>window.location="<%=contextPath%>/netmarkets/jsp/catl/promotion/promotionCreateWizStep2.jsp?oid=<%=oid%>"</script>