<%@page import="wt.workflow.engine.LWWfProcessIfc"%>
<%@page import="wt.fc.Persistable"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<% request.setAttribute(NmAction.SHOW_CONTEXT_INFO, "false"); %>
<%@ page import="com.ptc.netmarkets.util.beans.NmCommandBean,
                wt.workflow.work.WorkItem,
                com.ptc.netmarkets.util.misc.NmAction,
                com.ptc.netmarkets.model.NmOid,
                com.ptc.netmarkets.util.misc.NmDate,
                com.ptc.netmarkets.user.NmUserHelper,
                wt.workflow.work.WfAssignedActivity,
                wt.workflow.engine.WfActivity,
                wt.workflow.engine.WfContainer,
                wt.clients.util.WTPrincipalUtil,
				wt.workflow.engine.WfBlock,
                java.util.Locale,
                com.ptc.windchill.enterprise.workflow.WorkflowCommands"%>
<%@ page import="com.ptc.netmarkets.workflow.workflowResource"%>
<%@ page import="wt.lifecycle.lifecycleResource"%>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/core" prefix="wc"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<%
//If from processStatus action in routing history then use the action Oid
LWWfProcessIfc process = null;
NmOid wfOid = null;
NmOid wfa = null;

if (request.getParameter ("showProcessStatus") != null ) {
  if(request.getParameter("procOid") != null) {
	  wfOid = new NmOid(request.getParameter("procOid"));
	  request.setAttribute("argOid",wfOid);
  }
}
else{
     //anyone wanting to use this component must pass in a parameter procOid for the process NmOid
     //or additionally pass in an WfAssignedActivity NmOid to only display tasks specific to WfAssignedActivity
     //but "procOid" is still needed
     if (request.getParameter("wfaOid") != null){
      wfa = new NmOid(request.getParameter("wfaOid"));
     }
     if (request.getParameter("procOid") != null) {
        // wfOid = new NmOid(request.getParameter("procOid"));
     }

     Persistable persistable =  null;//(Persistable)wfOid.getRef();
    if (persistable instanceof WfBlock )
	{
			WfBlock wfblk = (WfBlock)persistable;
			process = wfblk.getParentProcess() ;
	} else if(persistable instanceof LWWfProcessIfc){
		  process = (LWWfProcessIfc)persistable;
	} 

    if (request.getParameter("wfaOid") != null){
			request.setAttribute("argOid",wfa);
		}
    else{
			request.setAttribute("argOid",wfOid);
		}
}
%>

<jsp:include page="${mvc:getComponentURL('enterpriseui.netmarkets.workitem.list')}" flush="true"/>

<%-->Display reassignment history for the process<--%>
<%
	request.setAttribute("procOid", wfOid);
%>
<jsp:include page="${mvc:getComponentURL('netmarkets.workitem.list.history')}" flush="true"></jsp:include>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>
