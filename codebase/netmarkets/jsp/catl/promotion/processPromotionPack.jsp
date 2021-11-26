<%@page language="java" session="true" pageEncoding="GBK"%>
<%@page import="wt.httpgw.GatewayServletHelper"%>
<%@page import="wt.httpgw.URLFactory"%>
<%@page import="wt.part.WTPart"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="wt.fc.WTObject"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.ptc.netmarkets.model.NmOid"%>
<%@page import="java.util.ArrayList"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="wt.pdmlink.PDMLinkProduct"%>
<%@page import="wt.inf.container.WTContainerRef"%>
<%@page import="com.catl.promotion.PromotionHelper" %>
<%@page import="wt.maturity.PromotionNotice"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="wt.maturity.Promotable"%>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%
	String contextPath = request.getContextPath();
	HashMap hmPromotionInfo = (HashMap)session.getAttribute("PromotionInfo");

	String promotionName = (String)hmPromotionInfo.get("inputPromotionName");
	String promotionDesc = (String)hmPromotionInfo.get("inputPromotionDesc");
	String promotionItem = (String)hmPromotionInfo.get("inputPromotionItem");
	String urlInfo = "";
	
	WTContainerRef containerRef = null;
	
	PromotionNotice promotion = (PromotionNotice)session.getAttribute("PromotionObject");
	boolean isCreate = false;	//判断是否当前操作为创建
	
	//创建签审包
	if(promotion == null) {
		isCreate = true;
		ReferenceFactory rf = new ReferenceFactory();
	  WTObject wtobject = (WTObject) rf.getReference(promotionItem).getObject();

		if(wtobject instanceof WTDocument){
			WTDocument doc = (WTDocument)wtobject;
			containerRef = doc.getContainerReference();
		}else if(wtobject instanceof WTPart){
			WTPart part = (WTPart)wtobject;
			containerRef = part.getContainerReference();
		}
		promotion = PromotionHelper.newPromotionNotice(promotionName,promotionDesc,containerRef);
	}
	
	
	//修改签审包
	if(promotion != null){
		//modify by helay 20090421 add all the objects to the promotionNotice
		//promotion = PromotionHelper.removePromotable(promotion);	//移除所有的签审包内内容
		ArrayList selectItems = commandBean.getNmOidSelected();	
		
// 		if(selectItems.size() == 0){
// 		   System.out.println("in processPromotionPack.jsp User did not select objects");
	      
// 		}else{
		    
// 		    System.out.println("In procesPromotionPack selected Item size = " + selectItems.size());
// 			if(selectItems == null) selectItems = new ArrayList();
			
// 			for(int i=0; i < selectItems.size(); i++){
// 				String oid = selectItems.get(i).toString();
// 				oid = oid.replace("NmOid=VR:", "");
// 				ReferenceFactory rf2 = new ReferenceFactory();
// 			    Object object = rf2.getReference(oid).getObject();

// 				if(object instanceof Promotable){
// 					promotion = PromotionHelper.addPromotable(promotion, (Promotable)object);	
// 				}
// 			}
// 		}
			session.setAttribute("PromotionInfo", null);
			session.setAttribute("PromotableList", null);
			session.setAttribute("PromotionObject", null);
			
			if(isCreate){		
				try{				
					//WorkflowHelper.initialProcess(promotion.toString());
				}catch(Exception ex){
					out.println(ex);
				}
			}else{					
				try{
					promotion = PromotionHelper.modifyPromotionNotice(promotion, promotionDesc);
				}catch(Exception ex){
					out.println(ex);
				}
			}
			
		    URLFactory uf = new URLFactory();
		    ReferenceFactory rf = new ReferenceFactory();
		    String oid = rf.getReferenceString(promotion);
		    HashMap m = new HashMap();
		    m.put("oid", oid);
		    m.put("action", "ObjProps");
		    urlInfo = GatewayServletHelper.buildAuthenticatedHREF(uf, "wt.enterprise.URLProcessor", "URLTemplateAction",m, true); 
		
	}
%>
	<script>
		window.opener.location.reload();
		window.open('','_self');  
		window.opener=null;
		window.close();
	</script>
<!--  <script>window.location="<%=urlInfo%>";</script>-->
<%@ include file="/netmarkets/jsp/util/end.jspf"%>