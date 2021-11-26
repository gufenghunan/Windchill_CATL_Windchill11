<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>

<jsp:include page="${mvc:getComponentURL('com.ptc.xworks.workflow.builders.AssociatedProcessPboTableBuilder')}">
	<jsp:param value="" name="param1"/>
</jsp:include>

<script type="text/javascript">

var relatedProcessPboTable = PTC.jca.table.Utils.getTable("relatedProcessesTableId");

relatedProcessPboTable.getSelectionModel().on("rowSelect", refreshWfProcessTables);
relatedProcessPboTable.getSelectionModel().on("rowDeSelect", refreshWfProcessTables);

function refreshWfProcessTables(grid, rowIndex) {
	//window.alert("refreshWfProcessTables in");
	
	var processTeamTable = PTC.jca.table.Utils.getTable("windchill.enterprise.team.lcmTeam");
    var processHistoryTable = PTC.jca.table.Utils.getTable("processHistoryTableId");
    
    if(!validateOnlyOnePboSelected("relatedProcessesTableId")){
    	clearWfProcessTable(); // Clear tables if more than one row is selected
    } else {
    	clearWfProcessTable();
        var selectedRow = relatedProcessPboTable.getStore().getAt(rowIndex);
        var selectedOid = selectedRow.data.oid;
        /*
        var s = "";
        var v = processTeamTable.topToolbar.items.items;
        for (o in v) {
        	s = s + "\n" + o + "=" + v[o];
        }
        window.alert(s);
        */
        
        if(true){
        	processTeamTable.store.baseParams.oid = selectedOid;
        	processTeamTable.store.baseParams.contextObjectOid = selectedOid;
        	//processTeamTable.jcaTableConfig.storeURL.params["oid"] = selectedOid;
            PTC.jca.table.Utils.reload("windchill.enterprise.team.lcmTeam", {oid:selectedOid, contextObjectOid:selectedOid}, true);
        }
        
        if(processHistoryTable.store.load_complete){
            PTC.jca.table.Utils.reload("processHistoryTableId", { "contextObjectOid" : selectedOid }, true);
        }
        
   } 
}

function validateOnlyOnePboSelected(tableID) {
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

function clearWfProcessTable(){       
    var selectedComponent=0;
    var processTeamTable = null;
    var processHistoryTable = null;
    var taskStatusTableId = null;   
    var taskHistoryTableId = null;
    processTeamTable = PTC.jca.table.Utils.getTable("windchill.enterprise.team.lcmTeam");
    processHistoryTable = PTC.jca.table.Utils.getTable("processHistoryTableId");
    taskStatusTableId = PTC.jca.table.Utils.getTable("taskStatusTableId");
    taskHistoryTableId = PTC.jca.table.Utils.getTable("taskHistoryTableId");
    
    if(true) {
    	processTeamTable.getSelectionModel().clearSelections();
    	processTeamTable.getStore().removeAll();
    }
    
    if(processHistoryTable.store.load_complete) {
    	processHistoryTable.getSelectionModel().clearSelections();
    	processHistoryTable.getStore().removeAll();
    }
    
    if(taskStatusTableId.store.load_complete) {
    	taskStatusTableId.getSelectionModel().clearSelections();
    	taskStatusTableId.getStore().removeAll();
    }
    
    if(taskHistoryTableId.store.load_complete) {
    	taskHistoryTableId.getSelectionModel().clearSelections();
    	taskHistoryTableId.getStore().removeAll();
    }    

}
</script>