<%@ page import="wt.epm.EPMDocument"%>
<%@ page import="com.catl.change.report.ecr.ECRReportHelper"%>
<%@ page import="org.apache.commons.fileupload.*"%>
<%@ page import="java.util.*"%>

<html>
<head>
<title>Export to Excel</title>
</head>
<body>
<%
		String ecrNumber = request.getParameter("ecrNumber");
		String status = request.getParameter("status");
		String createDateFrom = request.getParameter("createDateFrom");
		String createDateTo = request.getParameter("createDateTo");
		String approvedDateFrom = request.getParameter("approvedDateFrom");
		String approvedDateTo = request.getParameter("approvedDateTo");
		String user = request.getParameter("user");
		System.out.println("ecrNumber:"+ecrNumber+"status:"+status+"createDateFrom:"+createDateFrom+"createDateTo:"+createDateTo+"approvedDateFrom:"+approvedDateFrom+"approvedDateTo:"+approvedDateTo+"user:"+user);
		wt.fc.ReferenceFactory rf = new wt.fc.ReferenceFactory();
		ECRReportHelper ecrReport = new ECRReportHelper(ecrNumber,status,createDateFrom,createDateTo,approvedDateFrom,approvedDateTo,user);
		ecrReport.generateReport(response);
		out.clear();
        out = pageContext.pushBody();
%>
<script language='JavaScript'>
  
</script>
</body>
</html>