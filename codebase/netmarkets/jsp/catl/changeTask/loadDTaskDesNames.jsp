<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Arrays"%>
<%@page import="com.ptc.core.components.rendering.guicomponents.ComboBox"%>
<%@page import="com.catl.change.DataUtility.CatlPropertyHelper"%>
<%
//servlet��������ʱ�����ַ���������Ϊutf-8�Ϳ����ˣ���ΪAjaxֻ֧��utf-8  
response.setContentType("text/xml;charset=utf-8");  
request.setCharacterEncoding("UTF-8");
String a=request.getParameter("model");
System.out.println("aaaaaa====="+a);
//String name = new String((a.getBytes("ISO-8859-1")),"UTF-8");
//System.out.println("abcd====="+name);
String name="";
if(a!=null){
    name = java.net.URLDecoder.decode(a, "UTF-8");  
}

System.out.println("bbbbbb====="+name);
String o=CatlPropertyHelper.getDcaPropertyValue(name);
out.println(o);
	
%>