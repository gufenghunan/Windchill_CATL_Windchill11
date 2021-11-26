<%@page import="wt.part.WTPart"%>
<%@page import="com.catl.ecad.utils.ECADutil"%>
<%@page import="com.catl.ecad.dbs.CadenceXmlObjectUtil"%>
<%@page import="com.catl.ecad.bean.CadenceAttributeBean"%>
<%@page import="wt.workflow.work.WfAssignmentState"%>
<%@page import="com.ptc.xworks.xmlobject.BaseXmlObjectRef"%>
<%@page import="wt.fc.Persistable"%>
<%@page import="com.ptc.xworks.windchill.util.NetmarketsUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@page import="com.ptc.xworks.xmlobject.web.LayoutForView"%>
<%@page import="java.util.*"%>
<%@page import="wt.fc.WTObject"%>
<%@page import="com.ptc.netmarkets.model.NmOid"%>
<%@page import="wt.workflow.work.WorkItem"%>
<%@page import="wt.fc.ObjectReference"%>

<!-- attachments:fileSelectionAndUploadApplet/-->

<%
System.out.println("TEST..........1");
	LayoutForView layoutForMadePartTable = new LayoutForView();

	String operationType = "VIEW";
	String oid = request.getParameter("oid");
	WorkItem workItem = (WorkItem) NetmarketsUtils.getContextObject(commandBean);
	String itemdec = workItem.getDescription();         //NODE_ID=描述
	Object pbo = workItem.getPrimaryBusinessObject().getObject();
	
	CadenceAttributeBean cadbean = null;//CadenceXmlObjectUtil.createCadenceAttributeBean(pbo);
	List<CadenceAttributeBean> list = CadenceXmlObjectUtil.getCadenceAttributeBeanUtil((WTObject)pbo);
	System.out.println("list------------------>"+list.size());
	WTPart part=(WTPart) pbo;
	String poid=ObjectReference.newObjectReference(part).toString();
	if(list.size() > 0){
		for(CadenceAttributeBean pbean:list){
			if(pbean.getPartOid().equals(poid)){
				cadbean=pbean;
			}else{
				cadbean = list.get(0);
			}
		}
	}else{
		CadenceXmlObjectUtil.createCadenceAttributeBean((WTObject)pbo);
		cadbean = CadenceXmlObjectUtil.getCadenceAttributeBeanUtil((WTObject)pbo).get(0);
	}
	
	System.out.println("TEST..........");
	BaseXmlObjectRef parentRef = BaseXmlObjectRef.newBaseXmlObjectRef((WTObject)part.getMaster());
	String xmlObjectOid = parentRef.toString();
	
	String attrbeanoid = cadbean.getIdentifier().toString();	
	
	if((itemdec.contains("EDIT_CADENCEATTRS")||itemdec.contains("UPDATE_CADENCEATTRS"))&&!workItem.getStatus().equals(WfAssignmentState.COMPLETED)){
		operationType = "CREATE";		
	}
	
	//申请单oid
	String fpcvendortoid = ECADutil.getOidByObject((Persistable)pbo); 
	
%>
<input type="hidden" name="FormProcessorDelegate" value="com.ptc.xworks.xmlobject.web.form.XmlObjectExtractFormProcessorDelegate" />
<input type="hidden" name="FormProcessorDelegate" value="com.catl.ecad.processors.CadenceApplicationFormEditFormProcessorDelegate" />

<jsp:include page="${mvc:getComponentURL('com.catl.ecad.mvc.builders.CadenceAttributeBuilder')}">
	<jsp:param name="contextObjectClassName" value="com.catl.ecad.bean.CadenceAttributeBean"/>
	<jsp:param name="componentId" value="com.catl.ecad.mvc.builders.CadenceAttributeBuilder"/>
	<jsp:param name="operationType" value="<%=operationType %>" />
	<jsp:param name="layoutForView" value="<%=layoutForMadePartTable %>" />
	<jsp:param name="xmlObjectOid" value="<%=attrbeanoid %>" />
	<jsp:param name="pbooid" value="<%=fpcvendortoid %>" />
	<jsp:param name="itemdec" value="<%=itemdec %>" />
</jsp:include>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>