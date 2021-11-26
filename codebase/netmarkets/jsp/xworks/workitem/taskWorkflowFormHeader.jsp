<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>

<input id="XWORKS_FORM_PAGE_MARKER" type="hidden" value="true" />
<input id="xmlobject_IgnoreValidationWarning" type="hidden" name="xmlobject_IgnoreValidationWarning" value="false"/>

<fmt:setBundle basename="com.ptc.xworks.xmlobject.workflow.workflowResource"/>
<fmt:message var="confirmToContinue" key="CONFIRM_TO_CONTINUE_TO_COMPLETE_TASK" />

<script type="text/javascript">
var applicationFormNeeded = false; //这个任务节点是否有申请单表单
if (typeof applicationFromLoadFinished == "undefined") {
	applicationFromLoadFinished = false;
}

var validateApplicationForm = function(element, event) {
	if (applicationFormNeeded) {
		if (applicationFromLoadFinished) {
			if (typeof validateApplicationFormCallback == "function") {
				console.log("1.found a validateApplicationFormCallback function, start to call validateApplicationFormCallback() first...");
				var result = validateApplicationFormCallback(element, event);
				if (result) {
					console.log("2.after validateApplicationFormCallback return a true, call OOTB EnableTextBox()...");
					return EnableTextBox();//表单验证没有问题，继续往下执行；
				} else {
					return false;// 表单验证有问题，不再往下执行;
				}
			} else { // 没有验证的callback，继续调用OOTB function
				console.log("3.Cannot found a validateApplicationFormCallback function, call OOTB EnableTextBox()");
				return EnableTextBox();
			}
		} else {
			console.log("4. application form is not loaded, return!");
			window.alert("Please do this operation after Task Form loading is finished!");
			return false;
		}
	} else {
		console.log("5.No customized application form, call OOTB EnableTextBox()");
		return EnableTextBox();
	}
};

function completeWorkflowTask() {
	//window.alert("completeWorkflowTask");
	var completeButtons = $$("button[name='complete']");
	for (var i = 0; i < completeButtons.length; i++) {
		if (i == 1) {
			break;
		}
		completeButtons[i].click();
		//var jsCodeForOnClick = completeButtons[i].readAttribute("onclick");
		//window.alert(jsCodeForOnClick);
		//eval(jsCodeForOnClick);
	}
}

function saveWorkflowTask() {
	//window.alert("saveWorkflowTask");
	var saveButtons = $$("button[name='save']");
	for (var i = 0; i < saveButtons.length; i++) {
		if (i == 1) {
			break;
		}
		saveButtons[i].click();
		//saveButtons[i].disabled = true;
		//var jsCodeForOnClick = saveButtons[i].readAttribute("onclick");
		//eval(jsCodeForOnClick);
		//window.alert(saveButtons[i].readAttribute("onclick"));
	}
}

// 通过覆写OOTB的isWizardWindow,判断页面上是否有ID为XWORKS_FORM_PAGE_MARKER的元素，如果存在，
// 则返回true,否则使用OOTB的逻辑
// 通过此覆写，可以解决在流程任务页面中Table的表格内编辑时动态刷新后，表格内已经修改但尚未保存数据库的数据会丢失的问题
if (typeof isWizardWindow === "function") {
	//console.log("====== XWORKS isWizardWindow._xworks_wrapped type:" + (typeof isWizardWindow._xworks_wrapped));
	if (typeof isWizardWindow._xworks_wrapped == "undefined") {
		console.log("====== XWORKS isWizardWindow is not a wrapped function, start to wrap OOTB isWizardWindow() ! ======");
		isWizardWindow = isWizardWindow.wrap(function(ootbIsWizardWindow) {
			var XWORKS_FORM_PAGE_MARKER = document.getElementById("XWORKS_FORM_PAGE_MARKER");
			//console.log("=========== XWORKS wrapped isWizardWindow start ================" + (typeof XWORKS_FORM_PAGE_MARKER));
			if (XWORKS_FORM_PAGE_MARKER){
				console.log("=========== XWORKS wrapped isWizardWindow() found XWORKS_FORM_PAGE_MARKER in current page! return true ==========");
				return true;
			}
			return ootbIsWizardWindow();
		});
		isWizardWindow._xworks_wrapped = true;
	} else {
		console.log("====== XWORKS isWizardWindow._xworks_wrapped is not undefined, don't wrap it again ! ======");
	}
} else {
	console.log("====== XWORKS isWizardWindow is not a function ! ======");
}

var _ootbHandleSubmitResult;
//window.alert(typeof ootbHandleSubmitResult);
if (typeof _ootbHandleSubmitResult != "function") {
	console.log("====== XWORKS: handleSubmitResult is not a wrapped function, start to wrap... ====== ");
	_ootbHandleSubmitResult = handleSubmitResult;
	handleSubmitResult = function (status, nextAction, js, URL, frConfig) {
		//window.alert(status);
		console.log("====== XWORKS: start to execute wrapped handleSubmitResult ====== ");
		if (!setXmlObjectIgnoreValidationWarning("false")) {
			_ootbHandleSubmitResult(status, nextAction, js, URL, frConfig);
			return;
		}
		if (frConfig.extraData.haveOptionalValiationMessage == "TRUE") {
			var allMessages = frConfig.extraData.optionalValidationMessage;
			var warnningMessage = "";
			for (var i = 0; i < allMessages.length; i++) {
				warnningMessage = warnningMessage + allMessages[i] + "";
			}
			warnningMessage = warnningMessage + "${confirmToContinue}";
			var continueSubmit = window.confirm(warnningMessage);
			if (continueSubmit) {
				_ootbHandleSubmitResult(status, nextAction, js, URL, frConfig);
				setXmlObjectIgnoreValidationWarning("true");
				//window.alert(frConfig.extraData.nextActionFunction);
				var nextActionFunction = eval(frConfig.extraData.nextActionFunction);
				//window.alert(typeof nextActionFunction);
				nextActionFunction();
			} else {
				_ootbHandleSubmitResult(status, nextAction, js, URL, frConfig);
			}
		} else {
			_ootbHandleSubmitResult(status, nextAction, js, URL, frConfig);
		}
		
	}
} else {
	console.log("====== XWORKS: handleSubmitResult is a wrapped function, don't wrap it again! ====== ");
}

function setXmlObjectIgnoreValidationWarning(trueOrFalse) {
	var flag = $$("input[id=xmlobject_IgnoreValidationWarning]");
	//window.alert(flag.length);
	if (flag.length == 0) {
		return false;
	}
	for (var i = 0; i < flag.length; i++) {
		flag[i].setAttribute("value", trueOrFalse);
	}
	return true;
}

function reloadRelatedObjectGroupTable(groupId, workitemOid, tableID){
	var params = {groupId : groupId, oid : workitemOid};
	PTC.jca.table.Utils.reload(tableID, params, true);
}

//此JS代码用来解决Table中表单可能只有部分行被提交的问题,如果没有表单内编辑，请不要使用此代码!!!! 正常情况下,此代码只应该在弹出窗口中使用！
// 下面的代码，让Table中可以Cache 1000的数据
//console.log("====== XWORKS Ext.ux.grid.BufferView.prototype._ootb_Ext_ux_grid_BufferView_doClean type:" + (typeof Ext.ux.grid.BufferView.prototype._ootb_Ext_ux_grid_BufferView_doClean));
if (Ext.ux.grid.BufferView && (typeof Ext.ux.grid.BufferView.prototype._ootb_Ext_ux_grid_BufferView_doClean != "function")) {
	console.log("====== XWORKS Ext.ux.grid.BufferView.prototype no _ootb_Ext_ux_grid_BufferView_doClean member function, start to override ! ======");
	Ext.override(Ext.ux.grid.BufferView,{
		cacheSize : 1000,
		doClean: function() {
			// 如果在页面上找到一个ID为XWORKS_DISABLE_BUFFERVIEW_DOCLEAN的HTML元素，则覆写OOTB的Ext.ux.grid.BufferView.doClean方法，放置表格内编辑时只提交部分的表格行
			var XWORKS_DISABLE_BUFFERVIEW_DOCLEAN = document.getElementById("XWORKS_DISABLE_BUFFERVIEW_DOCLEAN");
			if (XWORKS_DISABLE_BUFFERVIEW_DOCLEAN) {
				console.log("========== XWORKS overrided Ext.ux.grid.BufferView.doClean() found XWORKS_DISABLE_BUFFERVIEW_DOCLEAN on current page, return! ==========");
				return;
			} else {
				console.log("========== XWORKS overrided Ext.ux.grid.BufferView.doClean() call _ootb_Ext_ux_grid_BufferView_doClean ==========");
				this._ootb_Ext_ux_grid_BufferView_doClean();
			}
		},
		_ootb_Ext_ux_grid_BufferView_doClean : ootb_Ext_ux_grid_BufferView_doClean
	});
} else {
	console.log("====== XWORKS Ext.ux.grid.BufferView.prototype has a _ootb_Ext_ux_grid_BufferView_doClean member function, don't override again! ======");
}

</script>

