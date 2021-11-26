<%@ page import="com.ptc.core.components.util.RequestHelper"%>
<%@ page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>
<%@ page import="com.catl.part.ValidateSpecification"%>
<%
			String result = "";
			try
			{
				RequestHelper.initializeCommandBean(request,response);
				NmCommandBean clientData = RequestHelper.getCommandBean(request);
				ValidateSpecification validateSP=new ValidateSpecification(clientData);
				result=validateSP.preCheckSpecification();
				System.out.println("ValidateSpecification result:"+result);
			} catch (Exception e)
			{
				result = e.getLocalizedMessage();
			}
			if (result != null)
			{
%><%=result%><%
			}
%>