<%@ page contentType="text/html; charset=gb2312" language="java"
	import="java.util.*" errorPage=""%>

<%@ page import="java.util.Locale"%>
<%@ page import="com.catl.part.ImportOptimumSelectingLevelHelper"%>
<%@ page import="com.jspsmart.upload.*,java.io.File,java.io.FileInputStream,java.io.FileNotFoundException,java.io.IOException,org.apache.poi.hssf.usermodel.HSSFCell,org.apache.poi.hssf.usermodel.HSSFRow,org.apache.poi.hssf.usermodel.HSSFSheet,org.apache.poi.hssf.usermodel.HSSFWorkbook,wt.part.WTPart,wt.util.WTProperties,wt.util.WTContext,wt.util.WTMessage,wt.fc.ReferenceFactory,wt.httpgw.URLFactory"%>

<%
	String contextPath = request.getContextPath();
	System.out.println("contextPath:"+contextPath);
%>

<html>
<head>
<title></title>
</head>

<body class="bg-body">

<%
	String newURL = "";
	String message = "";
	String result = "";
	String error = "";
	Locale locale = Locale.CHINA;

	try {
		//System.out.println("---------begin to check");
		WTProperties wtp = WTProperties.getLocalProperties();
		String tempDir = wtp.getProperty("wt.temp");
		SmartUpload su = new SmartUpload();
		su.initialize(pageContext);
		su.upload();
		int count = su.save(tempDir,su.SAVE_PHYSICAL);
		for (int i = 0; i < su.getFiles().getCount(); i++) {
			com.jspsmart.upload.File file = su.getFiles() .getFile(i);
			if (file.isMissing()) {
				continue;
			}
			File file2 = new File(tempDir + File.separator + file.getFileName());
			//System.out.println("------------filename:"+ file2.getName());
			ImportOptimumSelectingLevelHelper loadUtil = new ImportOptimumSelectingLevelHelper();
			result = loadUtil.getErrMsg(file2);
		}
		String[] outMessage = result.split("!");
		if (!"".equals(outMessage[0])) {
			out.println("Result in upload:");
		}
		for (int i = 0; i < outMessage.length; i++) {
			if ("".equals(outMessage[0])) {
				break;
			}
			String errMage = outMessage[i];
			out.println("<tr><td>" + errMage + "</td></tr>");
		}
		//System.out.println("---------show errmsg");

	} catch (Exception e) {
		e.printStackTrace();
%>
			<script>
				alert("<%=e.getLocalizedMessage()%>");
	  		</script>
<%
	}
	if(!result.isEmpty())
	{
		session.setAttribute("checkError", result);
		
	}
	response.sendRedirect(contextPath+"/app/#ptc1/catl/part/uploadOptimumSelectingLevelLevel");
%>

</body>
</html>


