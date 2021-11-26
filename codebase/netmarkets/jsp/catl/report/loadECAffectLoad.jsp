<%@page language="java" session="true" pageEncoding="UTF-8"%>
<%@ include file="/netmarkets/jsp/util/beginPopup.jspf"%>
<%@ page import="com.catl.change.report.ecn.ExportECNAffectTargetsParent2Excel"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="java.io.BufferedInputStream"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.io.OutputStream"%>
<%@page import="java.util.Vector"%>
<%@page import="java.util.*"%>
<%@page import="wt.util.Encoder"%>
<%@page import="javax.servlet.http.*"%>


<%
String oid = request.getParameter("oid");
	FileInputStream in = null;
	OutputStream o =null;
	java.io.File fileLoad =ExportECNAffectTargetsParent2Excel.doexportExcel(oid);
 if(fileLoad!= null && fileLoad.exists()){
	
		 try{
			 o=response.getOutputStream();
			  byte b[]=new byte[500];
			  response.setContentType("application/octet-stream");
			  //URLDecoder.decode(fileLoad.getName(),"utf-8");
			  response.setHeader("content-disposition","attachment; filename="+fileLoad.getName());
			  long fileLength=fileLoad.length();
			  String length1=String.valueOf(fileLength);
			  response.setHeader("Content_Length",length1);
			  in=new FileInputStream(fileLoad);
			  int n;
			  while((n=in.read(b))!=-1){
			   o.write(b,0,n);
			  }
			  if(in != null){
				  in.close();
				  in = null;
			  }
			  if(o !=null){
				  o.flush();
		            o.close();
		            out.clear();
			  }
			  out = pageContext.pushBody();

			}catch(Exception e){
				//e.printStackTrace();
		        response.setContentType("text/html");
		        out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		        out.println("<HTML>");
		        out.println("  <HEAD><TITLE>Result</TITLE></HEAD>");
		        out.println("  <BODY>");
		        out.println("µ¼³öÒì³£:");
		        out.println("<BR>");
		        out.println(e);
		        out.println("  <BR>");
		        out.println("  </BODY>");
		        out.println("</HTML>");
		        out.flush();
		        out.close();
			}finally{
				if(in != null){
					try{
						in.close();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				if(o != null){
					try{
						o.close();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				
		}
	 }else{
         response.setContentType("text/html");
         out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
         out.println("<HTML>");
         out.println("  <HEAD><TITLE>Result</TITLE></HEAD>");
         out.println("  <BODY>");
         out.println("failed----------------------------->>>>>>>>>>>>");
         out.println("  <BR>");
         out.println("  </BODY>");
         out.println("</HTML>");
         out.flush();
         out.close();
     }

%>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>