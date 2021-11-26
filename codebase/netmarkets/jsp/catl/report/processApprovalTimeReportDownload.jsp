<%@ page import="wt.epm.EPMDocument"%>
<%@ page import="com.catl.change.report.workflow.ProcessApprovalReportHelper"%>
<%@ page import="org.apache.commons.fileupload.*"%>
<%@ page import="java.util.*"%>

<html>
<head>
<title>Export to Excel</title>
</head>
<body>
<%
		String routerName = request.getParameter("routerName");
		String approvedDateFrom = request.getParameter("approvedDateFrom");
		String approvedDateTo = request.getParameter("approvedDateTo");
		System.out.println("routerName:"+routerName+"approvedDateFrom:"+approvedDateFrom+"approvedDateTo:"+approvedDateTo);
		wt.fc.ReferenceFactory rf = new wt.fc.ReferenceFactory();
		ProcessApprovalReportHelper report = new ProcessApprovalReportHelper(routerName,approvedDateFrom,approvedDateTo);
		report.generateReport(response);
		out.clear();
        out = pageContext.pushBody();
%>
<script language='JavaScript'>
  
</script>
</body>
</html>