<%@ page import="wt.epm.EPMDocument"%>
<%@ page import="com.catl.change.report.bom.TrunkBOMQueryReportHelper"%>
<%@ page import="com.catl.change.report.model.TrunkBOMQueryModel"%>
<%@ page import="org.apache.commons.fileupload.*"%>
<%@ page import="java.util.*"%>

<html>
<head>
<title>Export to Excel</title>
</head>
<body>
<%
	
	TrunkBOMQueryModel queryBean = new TrunkBOMQueryModel();
	String PARTNUMBER_1 = request.getParameter("PARTNUMBER_1");
	String PARTNUMBER_2 = request.getParameter("PARTNUMBER_2");
	String PARTNUMBER_3 = request.getParameter("PARTNUMBER_3");
	String PARTNUMBER_4 = request.getParameter("PARTNUMBER_4");
	String PARTNUMBER_5 = request.getParameter("PARTNUMBER_5");
	String PARTNUMBER_6 = request.getParameter("PARTNUMBER_6");
	String PARTNUMBER_7 = request.getParameter("PARTNUMBER_7");
	String PARTNUMBER_8 = request.getParameter("PARTNUMBER_8");
	String PARTNUMBER_9 = request.getParameter("PARTNUMBER_9");
	String PARTNUMBER_10 = request.getParameter("PARTNUMBER_10");
	
	queryBean.setPARTNUMBER_1(PARTNUMBER_1);
	queryBean.setPARTNUMBER_2(PARTNUMBER_2);
	queryBean.setPARTNUMBER_3(PARTNUMBER_3);
	queryBean.setPARTNUMBER_4(PARTNUMBER_4);
	queryBean.setPARTNUMBER_5(PARTNUMBER_5);
	queryBean.setPARTNUMBER_6(PARTNUMBER_6);
	queryBean.setPARTNUMBER_7(PARTNUMBER_7);
	queryBean.setPARTNUMBER_8(PARTNUMBER_8);
	queryBean.setPARTNUMBER_9(PARTNUMBER_9);
	queryBean.setPARTNUMBER_10(PARTNUMBER_10);

	wt.fc.ReferenceFactory rf = new wt.fc.ReferenceFactory();
	TrunkBOMQueryReportHelper report = new TrunkBOMQueryReportHelper(queryBean);
	report.generateReport(response);
	out.clear();
    out = pageContext.pushBody();
%>
<script language='JavaScript'>
  
</script>
</body>
</html>