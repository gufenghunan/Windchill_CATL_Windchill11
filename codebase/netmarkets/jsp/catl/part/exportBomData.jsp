<%@page import="org.apache.poi.ss.usermodel.Workbook"%>
<%@page import="com.catl.change.report.ExportBomDataByPart"%>
<%@ page contentType="text/html; charset=gb2312" language="java" errorPage=""%>
<jsp:useBean id="wtcontext" class="wt.httpgw.WTContextBean" scope="request" />
<jsp:setProperty name="wtcontext" property="request" value="<%=request%>" />
<jsp:useBean id="url_factory" class="wt.httpgw.URLFactory" scope="request">
	<%
	    url_factory.setRequestURL(request.getScheme(), request.getHeader("HOST"), request.getRequestURI());
	%>
</jsp:useBean>
<jsp:useBean id="localeBean" class="com.ptc.netmarkets.util.beans.NmLocaleBean" scope="request" />
<%@page import="java.util.*"%>
<%@page import="java.io.OutputStream"%>
<%@ page import="java.util.*"%>
<%@page import="wt.util.Encoder"%>
<%@page import="wt.part.*"%>
<%@page import="wt.fc.*"%>
<%@page import="java.text.SimpleDateFormat" %>
<html>
<head>
<title>ExportDocToExcel</title>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
</head>
<body>
<%
    String oid = request.getParameter("oid");
    if (oid == null)
        oid = request.getParameter("part");
    if (oid == null)
        oid = "";
    ReferenceFactory rf = new ReferenceFactory();

    ExportBomDataByPart helper = new ExportBomDataByPart();

        try {
            Workbook wb = helper.exportReport(oid);
            WTPart part = (WTPart) rf.getReference(oid).getObject();
            if (wb != null) {
                java.util.Date curTime = new java.util.Date();
                SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd");
				String time=format.format(curTime);
				
				Calendar cal = Calendar.getInstance();
				int senced = cal.get(Calendar.SECOND);
				String secd = "";
				if(senced<10){
					secd="0"+senced;
				}else{
					secd=""+senced;
				}
				time = time+""+(cal.get(Calendar.HOUR)+8)+""+cal.get(Calendar.MINUTE)+secd;
			    
                String exportFileName = "部件"+part.getNumber()+"的专用物料_"+time;
                response.reset();
                response.setContentType("application/vnd.ms-excel");
                response.setHeader("Content-disposition", "attachment; filename=" + new String((exportFileName).getBytes("gbk"),"iso8859-1")+".xlsx");
                out.clear();
                out = pageContext.pushBody();
                OutputStream os = response.getOutputStream();
                wb.write(os);
                os.flush();
                os.close();
                out.clear();
                //out = pageContext.pushBody();
            } else {
                response.setContentType("text/html");
                out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
                out.println("<HTML>");
                out.println("  <HEAD><TITLE>Result</TITLE></HEAD>");
                out.println("  <BODY>");
                out.println("导出异常，请联系管理员.");
                out.println("  <BR>");
                out.println("  </BODY>");
                out.println("</HTML>");
                out.flush();
                out.close();
            }
        } catch (Exception e) {
            response.setContentType("text/html");
            out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
            out.println("<HTML>");
            out.println("  <HEAD><TITLE>Result</TITLE></HEAD>");
            out.println("  <BODY>");
            out.println("导出异常:");
            out.println("<BR>");
            out.println(e);
            out.println("  <BR>");
            out.println("  </BODY>");
            out.println("</HTML>");
            out.flush();
            out.close();
        }
%>

</body>
</html>