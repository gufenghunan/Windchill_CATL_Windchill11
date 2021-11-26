<%@ page import="wt.epm.EPMDocument"%>
<%@ page import="com.catl.change.report.workflow.RejectReportHelper"%>
<%@ page import="org.apache.commons.fileupload.*"%>
<%@ page import="java.util.*"%>

<html>
<head>
<title>Export to Excel</title>
</head>
<body>
<%
		String processName = request.getParameter("processName");
		String rejectDateFrom = request.getParameter("rejectDateFrom");
		String rejectDateTo = request.getParameter("rejectDateTo");
		System.out.println("processName:"+processName+"rejectDateFrom:"+rejectDateFrom+"rejectDateTo:"+rejectDateTo);
		wt.fc.ReferenceFactory rf = new wt.fc.ReferenceFactory();
		RejectReportHelper report = new RejectReportHelper(processName,rejectDateFrom,rejectDateTo);
		report.generateReport(response);
		out.clear();
        out = pageContext.pushBody();
%>
<script language='JavaScript'>
  
</script>
</body>
</html>