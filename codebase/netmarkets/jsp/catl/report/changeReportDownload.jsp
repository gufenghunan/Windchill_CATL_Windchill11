<%@ page import="com.catl.report.ChangeReportHelper"%>
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
		String approveDateFrom = request.getParameter("approveDateFrom");
		String approveDateTo = request.getParameter("approveDateTo");
		String releasedDateFrom = request.getParameter("releasedDateFrom");
		String releasedDateTo = request.getParameter("releasedDateTo");
		System.out.println("changeReport ecrNumber:"+ecrNumber+",status="+status+",createDateFrom:"+createDateFrom+",createDateTo:"+createDateTo
				+",approveDateFrom:"+approveDateFrom+",approveDateTo:"+approveDateTo
				+",releasedDateFrom:"+releasedDateFrom+",releasedDateTo:"+releasedDateTo);
		wt.fc.ReferenceFactory rf = new wt.fc.ReferenceFactory();
		ChangeReportHelper changeReport = new ChangeReportHelper(ecrNumber,status,createDateFrom,createDateTo,approveDateFrom,approveDateTo,releasedDateFrom,releasedDateTo);
		changeReport.generateReport(response);
		out.clear();
        out = pageContext.pushBody();
%>
<script language='JavaScript'>
  
</script>
</body>
</html>