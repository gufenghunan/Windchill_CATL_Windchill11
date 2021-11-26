<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<% response.setContentType("text/html; charset=UTF-8");%>
<% request.setCharacterEncoding("UTF-8"); %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>

<script language="javascript">

/**
 * Handles the response of the wizard submission,
 * the response is mostly a form result string object that tells the browser what to do.  Not quite jSON yet though.
 */
function handleSubmitResult(status, nextAction, js, URL, frConfig){
	 //alert('handleSubmitResult-----------');
   var table, continueProcessing = true;
   if (PTC.getMainWindow() && PTC.getMainWindow().PTC && PTC.getMainWindow().PTC.performance) {
      PTC.getMainWindow().PTC.performance.startComponentTimer('handleSubmitResult', 'Update from Wizard Completed');
   }
   clearActionFormData();
   var customEventHandler = PTC.wizard.actionHandler;
   delete PTC.wizard.actionHandler;
  //alert('nextAction 1 :::'+nextAction);
   var formResult = PTC.wizard.initializeFormResult(status, nextAction, js, URL, frConfig);
   //alert('nextAction 2 :::'+nextAction);
   if (typeof customEventHandler === 'function') {
       //Custom handler, called once
       continueProcessing = customEventHandler(formResult);
   }
    //alert('before OnAfterAction');
    continueProcessing = PTC.wizard.fireOnAfterAction(formResult) && continueProcessing;
    //alert('end OnAfterAction');
    var formResultEnum = PTC.cat.FormResult;
    //alert(' formResultEnum');
    var callback = PTC.cat.WizardHelper.getCallback(getWindow());
    if (PTC.wizard.finished && nextAction === formResultEnum.REFRESH_OPENER && callback) {
        //Only call the CAT callback if its the wizard finish button, and its a refresh
        try { 
            callback.call(this, formResult);
        } catch(ecallback) {
            //2142574 - during page refresh the structure tab is lost and callback undefined in IE9 only
            PTC.log.warn('CAT callback returned an error message, this can mean the page has started refreshing and lost its scope', ecallback);
        }
        handleWizWindow(status);
        return;
    }
    
    if (!continueProcessing) {
        if (PTC.wizard.finished) {
           handleWizWindow(status);
        }
        return ;
    }

    //TODO set this to error level in x24 and remove the component refresh code below
    PTC.action.log.warn("Legacy Handler is being used, the action ", formResult.actionName, 
                       ' should return a formResult with oids or have components handle the action specifically.', formResult);
    //alert('nextAction:::'+nextAction);
    //alert('formResultEnum.REFRESH_OPENER:::'+formResultEnum.REFRESH_OPENER);
    switch (nextAction) {
        case formResultEnum.REFRESH_OPENER:
            var refreshed = false;
            if (parent) {
                
                if (PTC.wizard.finished) {
                    if (status !== 1) {
                        //If there is an error, no need to refresh as the wizard did not complete.
                        refreshOpenerOnly();
                    }
                    refreshed = true;
                }
                if ((parent.currentStepStrName || window.currentStepStrName) && (PTC.wizard.finished || status === 1) ) {
                    handleWizWindow(status);
                    refreshed = true;
                }
                // If window is a lightbox, this will allow it to be closed/deactivated
                else if (PTC.lightbox.isLightbox(window) && window.currentStepStrName && window.PTC.wizard.finished) {
                   handleWizWindow(status);
                   refreshed = true;
                }
            }
            //alert("refreshed:::"+refreshed);
            if(!refreshed) {
                //Component refresh of a table.  Non-wizard action that did not provide oids on the formResult
                table = tableUtils.getTable(formResult.getTableId());
                if(table) {
                	//removed for catl change inventory function....
                    //PTC.jca.table.Utils.reload(table);
                }
            }

            break;
        case 3: // JAVASCRIPT
            if (status == 1) { //FAILURE
                handleWizWindow(status);
            }
            try {
                eval(js);
            }
            catch (evalexception) {
                PTC.wizard.log.error(evalexception);
            }
            break;        
        case 4: // NONE
            if (PTC.wizard.finished) {
                // don't close the wizard if the actions is an on-page action inside one of the wizard tables.
                handleWizWindow(status);
            }
            else if (!parent.currentStepStrName) {          
                //Component refresh of a table.  Non-wizard action that did not provide oids on the formResult
                table = tableUtils.getTable(formResult.getTableId());
                if(table) {
                    PTC.jca.table.Utils.reload(table);
                }
            }

            break;
        case 5: // REFRESH_OPENER_AND_SUBMIT_IFRAMES
            refreshOpenerOnly();
            eval(js);
            handleWizWindow(status);
            break;
        case 2: // LOAD_OPENER_URL
            if (parent.currentStepStrName && PTC.wizard.finished) {
                  handleWizWindow(status);
                  refreshed = true;
            }
            break;
        case 6: // FORWARD
        case 7: // NON_SUBMIT
            // the LOAD_OPENER_URL and FORWARD cases are handled in PTC.wizard._handleForwardFormResult()
            break;
        case 8: // RE_SUBMIT CURRENT Page
              PTC.navigation.submitForm();
            break;
        default: // Unknown action
            throw new Error('Next wizard action unknown');
    }

    if (status === 1 && PTC.wizard.loadMask && PTC.wizard.loadMask.el
        && PTC.wizard.loadMask.el.isVisible && PTC.wizard.loadMask.el.isVisible()) {
        //If there was an error, make sure the progress window is not visible anymore
        //Some cases fall through
        stopProgress();
    }

    if (PTC.getMainWindow() && PTC.getMainWindow().PTC && PTC.getMainWindow().PTC.performance) {
        PTC.getMainWindow().PTC.performance.stopComponentTimer('handleSubmitResult', null, 'deltaTime');
    }
    //alert('end all');
}

function addRow() {
	document.getElementById();
}

function openPicker(obj){
	//alert();
	//var row = obj.parentNode.parentNode.parentNode;
	//alert(row);
	//var rowName = row.id;
	//alert(rowName);
	//alert(obj.previousSibling.previousSibling.id); 
	//alert(obj.parentNode.firstChild); 
	//alert(obj.parentNode.firstChild.name);
	var nodeName = obj.parentNode.firstChild.name;
	window.open('/Windchill/netmarkets/jsp/catl/changeInventory/changeInventory_searchPart.jsp?nodeName='+nodeName,'newWindow','dialogHeight:600px;dialogWidth:700px;resizable:yes;status:no;scroll:yes;toolbar:no;menubar:no;location:no');
}

function checkQuantity(obj) {
	var value = obj.value;
	if(value=='' || !IsNum(value)){
		alert("【数量】 不能为空，且必须为数字！");
		return false;
	}
}

function IsNum(num){
	 var reNum=/^\d*$/;
	 return(reNum.test(num));
} 
	
</script>

<jsp:include page="${mvc:getComponentURL('changeTask.changeInventory')}"  />

<%@ include file="/netmarkets/jsp/util/end.jspf"%>



