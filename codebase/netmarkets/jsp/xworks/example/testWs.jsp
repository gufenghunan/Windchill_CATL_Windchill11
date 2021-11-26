<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ page import="com.ptc.xworks.util.XWorksHelper" %><%
ext.ptc.xworks.examples.webservices.client.helloworld.HelloWorldService service = (ext.ptc.xworks.examples.webservices.client.helloworld.HelloWorldService) XWorksHelper.getObjectByName("HelloWorldService");
ext.ptc.xworks.examples.webservices.client.helloworld.ServiceResult result = service.hello1(new java.util.ArrayList());
out.println(result.getResultCode());
%>