<%@ page import="com.ptc.core.components.util.RequestHelper"%><%@ page import="java.util.*" %><%@
 page import="com.catl.change.util.ChangeConst" %><%@ 
 page import="com.ptc.netmarkets.util.beans.NmCommandBean"%><%@ 
 page import="com.catl.change.processors.CatlcheckChangeTaskAffectData"%>	
<%
			String result = "";
			String flag = "eca";
			try
			{
				RequestHelper.initializeCommandBean(request,response);
				NmCommandBean clientData = RequestHelper.getCommandBean(request);
				CatlcheckChangeTaskAffectData process=new CatlcheckChangeTaskAffectData(clientData);
				HashMap mp = clientData.getText();
				for(Object o : mp.keySet()){
					if(o.toString().contains(ChangeConst.CHANGETASK_TYPE_DCA)){
						flag = "dca";
					}
				}
				if(flag.equals("dca")){
					result=process.validateReleaseByAffectedItemsByDca();
				}else{
					result=process.validateReleaseByAffectedItems();
				}
				System.out.println("check result=============:"+result);
			} catch (Exception e)
			{
				result = e.getLocalizedMessage();
			}
			if (result != null)
			{
%><%=result%><%
			}
%>