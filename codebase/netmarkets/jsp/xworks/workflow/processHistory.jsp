<%@page import="wt.fc.collections.WTArrayList"%>
<%@page import="wt.fc.collections.WTCollection"%>
<%@page import="wt.fc.ObjectIdentifier"%>
<%@page import="wt.fc.ObjectReference"%>
<%@page import="wt.workflow.engine.LWWfProcessIfc"%>
<%@page import="wt.lifecycle.LifeCycleLWHelper"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<% request.setAttribute(NmAction.SHOW_CONTEXT_INFO, "false"); %>
<%@ page import="com.ptc.netmarkets.workflow.workflowResource"%>
<%@ page import="java.util.Enumeration,
                 java.util.HashMap,
                 wt.fc.Persistable,
                 wt.util.WTProperties,
                 com.ptc.netmarkets.model.NmOid,
                 wt.vc.Versioned,wt.workflow.engine.WfProcessReference,
                 wt.fc.WTReference,com.ptc.windchill.enterprise.workflow.WorkflowCommands,
                 wt.fc.Persistable,wt.fc.WTObject,wt.workflow.definer.WfDefinerHelper,
                 wt.change2.ChangeItem,java.util.Collections,java.util.ArrayList,
                 wt.epm.EPMDocument,wt.workflow.engine.WfProcess,wt.fc.ReferenceFactory,
                 com.ptc.netmarkets.work.NmWorkItemCommands,wt.vc.VersionControlHelper,
                 com.ptc.netmarkets.workflow.NmWorkflowHelper"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="comp"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/core" prefix="wc"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>





<%
    boolean displaySubProcesses = WTProperties.getLocalProperties().getProperty("wt.workflow.engine.showSubProcesses", true);
	Enumeration histories = (Enumeration) WorkflowCommands.getRoutingHistory(commandBean.getPageOid(),displaySubProcesses);
    boolean selectLatestProcessFirstTime= false;
    NmOid procOid = null;
    ArrayList arrLst = commandBean.getSelected();
    if(arrLst != null && arrLst.size() == 0){
   	Persistable p = (Persistable) (commandBean.getPageOid()).getRef();
	//WfProcess selectedProcess=markedProcess;
    if(!(p instanceof LWWfProcessIfc))
    {
        // SPR 1494519
        NmOid nmoid=new NmOid(p);
        if(p instanceof EPMDocument){
            sessionBean.getStorage().put(NmWorkItemCommands.TASK_KEY, NmWorkItemCommands.VIEW_DOCUMENT);
            commandBean.getRequest().getSession(true).putValue(NmWorkItemCommands.TASK_KEY, NmWorkItemCommands.VIEW_DOCUMENT);
        }else{
            sessionBean.getStorage().put(NmWorkItemCommands.TASK_KEY, NmWorkItemCommands.DETAILS_TASKS);
            commandBean.getRequest().getSession(true).putValue(NmWorkItemCommands.TASK_KEY, NmWorkItemCommands.DETAILS_TASKS);
        }
        sessionBean.getStorage().put(NmWorkItemCommands.TASK_OID, nmoid.toString());
        commandBean.getRequest().getSession(true).putValue(NmWorkItemCommands.TASK_OID, nmoid.toString());
        sessionBean.getStorage().put(NmWorkItemCommands.TASK_TYPE, nmoid.getType());
        commandBean.getRequest().getSession(true).putValue(NmWorkItemCommands.TASK_TYPE, nmoid.getType());
    }
    LWWfProcessIfc proc;
    wt.fc.ReferenceFactory rf = new wt.fc.ReferenceFactory ();
    Versioned v=null;
    long pBranchID=0;
    if(p instanceof Versioned) {
          v=(Versioned)p;
          pBranchID=v.getBranchIdentifier();
    }

    WTReference busORef;
    Persistable pObj=null;
    LWWfProcessIfc selectedProcess=null;
    long branchID=0;
    WTArrayList processes = new WTArrayList();
    while(histories.hasMoreElements()){
    	processes.add(histories.nextElement());
    }
	
    if(processes.size()==0){
       WTCollection coll = LifeCycleLWHelper.service.getLWWfProcesses(ObjectReference.newObjectReference(commandBean.getPageOid().getOidObject()));
       processes.addAll(coll);
    } 
    
	LWWfProcessIfc markedProcess=WorkflowCommands.markProcessAsSelected(processes,commandBean.getPageOid());
    selectedProcess=markedProcess;
     
    if(selectedProcess != null) 
        procOid = new NmOid((Persistable)selectedProcess);

     if (commandBean.getMap() != null){
     commandBean.getMap().put("latestProc", procOid);
     }
     else{
         HashMap hmap = new HashMap();
         hmap.put ("latestProc", procOid);
         commandBean.setMap(hmap);
     }
      selectLatestProcessFirstTime = true;

   }
   else{

       for (int i = 0; i < arrLst.size(); i++) {
           NmContext nmContextObj = (NmContext)arrLst.get(i);
           Persistable p1 = (Persistable) (nmContextObj.getTargetOid()).getRef();
           if(p1 instanceof LWWfProcessIfc){
           		procOid = nmContextObj.getTargetOid();
           }   
       }   
       HashMap hmap = new HashMap();
       hmap.put ("procOid", procOid); // To display task history
       commandBean.setMap(hmap);
   } 
%>

<jsp:include page="${mvc:getComponentURL('enterpriseui.com.ptc.netmarkets.workflow.list')}" flush="true"/>




<%//if (procOid != null){%>
<%if (true){%>

<%-->Calling the processStatus table with the latest process status displayed by default<--%>
   
<jsp:include page="/netmarkets/jsp/xworks/workflow/processStatus.jsp" flush="true"><jsp:param name="procOid" value="<%=procOid%>" /></jsp:include>

<%}%>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>

<script type="text/javascript">

	var xworks_processHistoryTable = PTC.jca.table.Utils.getTable("processHistoryTableId");

	xworks_processHistoryTable.getSelectionModel().on("rowSelect", xworks_RefreshTables);

	xworks_processHistoryTable.getSelectionModel().on("rowDeSelect", xworks_RefreshTables);
	
	function xworks_getValidCheck(tableID) {
   		var sels = null;
   		if (tableID != null) {
      		sels = getJCASelectedTableItems(tableID, false);                              
   		} else {
      		sels = getSelectedItems(true);                                  
   		}
   
   		if (sels) {
      		var alength = sels.length;
      		if(alength!=1) {
        		return false        
      		} else {
        		return true;
      		}
   		}
   
   		return false;
	}

	function xworks_clearComponentDetailTable(){       
        var selectedComponent=0;
        var taskStatusTableId = null;   
        var taskHistoryTableId = null;  
        taskStatusTableId = PTC.jca.table.Utils.getTable("taskStatusTableId");
        taskHistoryTableId = PTC.jca.table.Utils.getTable("taskHistoryTableId");

        if(taskStatusTableId.store.load_complete) {
            taskStatusTableId.getSelectionModel().clearSelections();
            taskStatusTableId.getStore().removeAll();
        }
        
        if(taskHistoryTableId.store.load_complete) {
            taskHistoryTableId.getSelectionModel().clearSelections();
            taskHistoryTableId.getStore().removeAll();
        }
	}





  function xworks_RefreshTables(grid, rowIndex) {
 
        var taskStatusTableId = PTC.jca.table.Utils.getTable("taskStatusTableId");
        var taskHistoryTableId = PTC.jca.table.Utils.getTable("taskHistoryTableId");
        
        if(!xworks_getValidCheck('processHistoryTableId')){
        	xworks_clearComponentDetailTable(); // Clear tables if more than one row is selected
        }else{
        	xworks_clearComponentDetailTable();
            var selectedRow = xworks_processHistoryTable.getStore().getAt(rowIndex);
            var selectedOid = selectedRow.data.oid;
            
            // SPR 2180937 Multiple activities with same name are displayed under one process when only one activity is expected.
            if(taskStatusTableId.store.load_complete){
                PTC.jca.table.Utils.reload("taskStatusTableId", 
                                            { selectedOid:selectedOid,procOid:selectedOid,"showProcessStatus":true }, true);
            }
           
            if(taskHistoryTableId.store.load_complete){
                PTC.jca.table.Utils.reload("taskHistoryTableId", 
                                            { selectedOid:selectedOid,procOid:selectedOid }, true);
            }
            
       }   
    }


</script>
