<%@page language="java" session="true" pageEncoding="GBK"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="wt.fc.WTObject"%>
<%@page import="com.catl.promotion.PromotionCreateHelper"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Hashtable"%>
<%@page import="java.util.Iterator"%>

<%@include file="/netmarkets/jsp/util/beginPopup.jspf" %>
<%@page import="java.util.HashMap"%>
<%@page import="wt.part.WTPart"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="java.text.SimpleDateFormat"%>
<link rel="stylesheet" type="text/css" href="netmarkets/css/nmstyles.css">
<link rel="stylesheet" type="text/css" href="com/ptc/core/ui/solutions.css">
<link rel="stylesheet" type="text/css" href="templates/htmlcomp/htmlcomp.css">
<link rel="stylesheet" type="text/css" href="templates/htmlcomp/wizard/wizard.css">
<link rel="stylesheet" type="text/css" href="templates/htmlcomp/jstable/tables.css">
<style type="text/css">
<!--
body {
	background-color: #D6D7C6;
}
table.gray_border{
	border:1px solid #A5AA9C;
}
.STYLE5 {font-size: 14px}
-->
</style>

<script language="javascript">
	function mySubmit(oid){
		this.document.mainform.action="netmarkets/jsp/catl/promotion/processPromotionInfo.jsp"+oid;
		this.document.mainform.submit();
	}
</script>

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
    <td height="38" colspan="7" class="pageHeader"><img src="netmarkets/images/sp.gif" height="1" /><font class="wizardtitlefont">&nbsp;编辑升级请求 &nbsp;</font></td>
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
	String oid = request.getParameter("oid");
	
	Hashtable hashtable ;
	ArrayList initialItems = new ArrayList();
	
	HashMap hmPromotionInfo = (HashMap)session.getAttribute("PromotionInfo");
	
	String promotionName = "";
	String promotionDesc = "";
	String promotionItem = "";
	
	if(hmPromotionInfo != null){
		promotionName = (String)hmPromotionInfo.get("inputPromotionName");
		promotionDesc = (String)hmPromotionInfo.get("inputPromotionDesc");
		promotionItem = (String)hmPromotionInfo.get("inputPromotionItem");
	}else{

		promotionItem = oid;
		ReferenceFactory rf = new ReferenceFactory();
		WTObject wtobject1 = (WTObject) rf.getReference(oid).getObject();
		
		if(wtobject1 instanceof WTPart){
			WTPart part = (WTPart)wtobject1;
			
			SimpleDateFormat tempDate = new SimpleDateFormat("yyyy/MM/dd");
			String datetime = tempDate.format(new java.util.Date());
			
			String version = part.getVersionInfo().getIdentifier().getValue();
			String iteration = part.getIterationInfo().getIdentifier().getValue();
			
			promotionName = part.getName() + ", " + part.getNumber() + ", " + version + "." + iteration + ", " + datetime;
		}else if(wtobject1 instanceof WTDocument){
			WTDocument doc = (WTDocument)wtobject1;

			SimpleDateFormat tempDate = new SimpleDateFormat("yyyy/MM/dd");
			String datetime = tempDate.format(new java.util.Date());
			
			String version = doc.getVersionInfo().getIdentifier().getValue();
			String iteration = doc.getIterationInfo().getIdentifier().getValue();
			
			promotionName = doc.getName() + ", " + doc.getNumber() + ", " + version + "." + iteration + ", " + datetime;
		}

		
		 hashtable = PromotionCreateHelper.getPromotionPackageItems(wtobject1);
		 Iterator itemIte = hashtable.keySet().iterator();
		 while(itemIte.hasNext()){
			 WTObject object = (WTObject)itemIte.next();
			 initialItems.add(object);
		 }
		session.setAttribute("PromotableList", initialItems);
	}

%>
<br>





<table width="100%" border="0">
  <tr>
    <td width="5%" height="56" valign="top" align="Right" class="wizFormLabel">升级请求名称&nbsp;&nbsp;</td>
    <td valign="top"><input name="inputPromotionName" id="inputPromotionName" value="<%=promotionName%>" readonly="readonly" size="50"/></td>
  </tr>
  <tr>
    <td height="235" valign="top" align="Right" class="wizFormLabel">说明&nbsp;&nbsp;</td>
    <td valign="top"><textarea name="inputPromotionDesc" cols="50" rows="10" id="inputPromotionName"><%=promotionDesc%></textarea>
    </td>
  </tr>
  <tr>
    <td valign="top">&nbsp;</td>
    <td valign="top"><input type="hidden" id="inputPromotionItem" name="inputPromotionItem" value="<%=promotionItem%>"/></td>
  </tr>
</table></td>
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
      <input name="Submit" type="button" class="wizBtn" value="下一步" onclick="mySubmit('<%=oid%>');" />
    </div></td>
  </tr>
</table>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>