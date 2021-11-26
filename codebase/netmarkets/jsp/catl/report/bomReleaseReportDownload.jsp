<%@ page import="wt.epm.EPMDocument"%>
<%@ page import="com.catl.change.report.bom.BOMReleaseReportHelper"%>
<%@ page import="org.apache.commons.fileupload.*"%>
<%@ page import="java.util.*"%>

<html>
<head>
<title>Export to Excel</title>
</head>
<body>
<%
		String createdDateFrom = request.getParameter("createdDateFrom");
		String createdDateTo = request.getParameter("createdDateTo");
		System.out.println("createdDateFrom:"+createdDateFrom+"createdDateTo:"+createdDateTo);
		wt.fc.ReferenceFactory rf = new wt.fc.ReferenceFactory();
		BOMReleaseReportHelper report = new BOMReleaseReportHelper(createdDateFrom,createdDateTo);
		report.generateReport(response);
		out.clear();
        out = pageContext.pushBody();
%>
<script language='JavaScript'>
  
</script>
</body>
</html>