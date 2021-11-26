<%@page language="java" session="true" pageEncoding="GBK"%>
<%@page import="wt.fc.WTObject"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="wt.maturity.PromotionNotice"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="com.catl.promotion.PromotionHelper"%>
<%@page import="wt.fc.ReferenceFactory"%>

<%@include file="/netmarkets/jsp/util/beginPopup.jspf" %>


<link href="css/nmstyles.css" rel="stylesheet" type="text/css">
<style type="text/css">
<!--
body {
	background-color: #D6D7C6;
}
table.gray_border{
	border:1px solid #A5AA9C;
}
.STYLE1 {color: #FFFFFF}
-->
</style>

<table width="100%" border="0" cellpadding="0" cellspacing="0" class="wiz">
  <tr>
    <td width="1" colspan="2" class="wizCnr"></td>
    <td  nowrap="nowrap" class="wizBdr"><img src="netmarkets/images/sp.gif" /></td>
    <td colspan="2" nowrap="nowrap" class="wizBdr"></td>
    <td width="1" nowrap="nowrap" class="wizBdr"></td>
    <td width="9" nowrap="nowrap" class="wizBdr"></td>
    <td width="1" colspan="2" class="wizCnr"></td>
  </tr>
  <tr>
    <td class="wizCnr"><img src="netmarkets/images/sp.gif" /></td>
    <td class="wizBdr"></td>
    <td class="wizCnr" nowrap="nowrap"></td>
    <td colspan="2" nowrap="nowrap" class="wizCnr"></td>
    <td nowrap="nowrap" class="wizCnr"></td>
    <td nowrap="nowrap" class="wizCnr"></td>
    <td class="wizBdr"></td>
    <td class="wizCnr"></td>
  </tr>
  <tr>
    <td class="wizBdr"><img src="netmarkets/images/sp.gif" height="1" /></td>
    <td height="38" colspan="7" class="pageHeader"><img src="netmarkets/images/sp.gif" height="1" /><font class="wizardtitlefont" size="3">&nbsp;编辑升级请求 &nbsp;</font></td>
    <td height="38" class="wizCnr"></td>
  </tr>
  <tr>
    <td class="wizBdr" rowspan="4"><img src="netmarkets/images/sp.gif" /></td>
    <td class="wizBdr" colspan="3"></td>
    <td class="wizBdr" ><img src="netmarkets/images/sp.gif" width="100" height="1" /></td>
    <td class="wizBdr" colspan="3"></td>
    <td class="wizBdr" rowspan="4"></td>
  </tr>
  <tr class="wizTblBdy">
    <td rowspan="3"><img src="netmarkets/images/sp.gif" /></td>
    <td rowspan="2" valign="top">     </td>
    <td height="18" colspan="3" valign="top">&nbsp;</td>
    <td valign="top">&nbsp;</td>
    <td></td>
  </tr>
  <tr class="wizTblBdy">
    <td colspan="3" valign="top">
<%
	String contextPath = request.getContextPath();
	String pageConext = request.getParameter("partContext");
	String oid = request.getParameter("oid");	
	ArrayList result = new ArrayList();//(ArrayList)session.getAttribute("PromotableList");

	PromotionNotice promotion = (PromotionNotice)session.getAttribute("PromotionObject");
	QueryResult qr = PromotionHelper.getPromotable(promotion);
	while(qr.hasMoreElements()){
		WTObject wtobject = (WTObject)qr.nextElement();
		result.add(wtobject);
	}
	request.setAttribute("promotablelist",result);
%>

<%
	String msg = (String)request.getSession().getAttribute("errorMess");
	System.out.println("in create wizard step2 msg ==" + msg);
    if(msg !=null && !"".equals(msg)){
    	
%> 
	<script language=javascript> 
	window.opener.document.location.reload();
	alert(' <%=msg%>');   
	</script> 
	<%
	request.getSession().setAttribute("errorMess",null);
	} 
%>

<script>	
	function doBack(oid){
		window.location="<%=contextPath%>/netmarkets/jsp/catl/promotion/promotionModifyWizStep1.jsp?oid="+oid;
	}
</script>

<br>


<jca:describeTable var="tableDescriptorOne" id="tableDescriptorOne" configurable="false" label="升级对象">
	<jca:setComponentProperty key="actionModel" value="promotionRequest_promotionItems_table" />

	<jca:setComponentProperty key="selectable" value="true" />
	<jca:describeColumn id="type_icon" label="" distinguishWIPVersions="true" />	
	<jca:describeColumn id="number" sortable="false" />
	<jca:describeColumn id="infoPageAction" sortable="false" />
	<jca:describeColumn id="name">
		<jca:setComponentProperty key="displayLengthInTables" value="25" />
	</jca:describeColumn>
	<jca:describeColumn id="containerName" sortable="false" />
	<jca:describeColumn id="version" sortable="false" />
	<jca:describeColumn id="state" sortable="false" />
	<jca:describeColumn id="thePersistInfo.modifyStamp" sortable="false" />
	<jca:describeColumn id="creator" sortable="false" />
</jca:describeTable>

<jca:getModel var="tableModelOne" descriptor="${tableDescriptorOne}" serviceName="com.catl.promotion.ui.PromotionPackService" methodName="getPromotionPackItems">
	<jca:addServiceArgument value="${promotablelist}" type="java.util.ArrayList" />
</jca:getModel>

<%-->Get the NmHTMLTable from the command<--%>
<jca:renderTable showCount="true" model="${tableModelOne}"
	pageLimit="15" singleSelect="false" helpContext="TABLE_508_HELP_PAGE" />

</td>
    <td valign="top">&nbsp;</td>
    <td></td>
  </tr>
  <tr class="wizReqRow">
    <td height="72" colspan="4"><img src="netmarkets/images/sp.gif" /></td>
    <td>&nbsp;</td>
    <td></td>
  </tr>
  <tr>
    <td height="2"  class="wizCnr"><img src="netmarkets/images/sp.gif" /></td>
    <td class="wizInCnr"></td>
    <td colspan="5"></td>
    <td class="wizInCnr"></td>
    <td class="wizCnr"></td>
  </tr>
  <tr>
    <td class="wizCnr" colspan="2"><img src="netmarkets/images/sp.gif" /></td>
    <td class="wizBdr" colspan="5"></td>
    <td class="wizCnr" colspan="2"></td>
  </tr>
</table>
<table width="100%" border="0">
  <tr>
    <td>&nbsp;</td>
    <td><div align="right">
      <input name="previous" type="button" class="wizBtn" value="上一步" onclick="doBack('<%=oid%>');" />
      <input name="Submit" type="button" class="wizBtn" value="完成" onclick="doProcess();" />
    </div></td>
  </tr>
</table>
<script>
	
    function doProcess()
    {
		document.mainform.action="<%=contextPath%>/netmarkets/jsp/catl/promotion/processPromotionPack.jsp";
		document.mainform.submit();
	}
	
</script>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>