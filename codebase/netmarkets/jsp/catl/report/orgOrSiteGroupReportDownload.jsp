<%@ page import="wt.epm.EPMDocument"%>
<%@ page import="com.catl.change.report.others.OrgOrSiteGroupReportHelper"%>
<%@ page import="org.apache.commons.fileupload.*"%>
<%@ page import="java.util.*"%>

<html>
<head>
<title>Export to Excel</title>
</head>
<body>
<%
		wt.fc.ReferenceFactory rf = new wt.fc.ReferenceFactory();
		OrgOrSiteGroupReportHelper report = new OrgOrSiteGroupReportHelper();
		report.generateReport(response);
		out.clear();
        out = pageContext.pushBody();
%>
<script language='JavaScript'>
  
</script>
</body>
</html>