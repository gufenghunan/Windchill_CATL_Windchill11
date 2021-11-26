<%@ page language="java" session="true" pageEncoding="UTF-8"%>
<%@ page import="org.apache.commons.lang.StringUtils"%>
<%@ page import="com.ptc.xworks.windchill.util.ObjectReferenceUtils" %>
<%@ page import="com.ptc.xworks.xmlobject.web.util.WebUtils" %>
<%@ page import="wt.folder.Folder" %>
<%@ page import="wt.inf.container.WTContainerRef" %>
<%@ page import="wt.inf.container.WTContainer" %>

<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="wctags" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ include file="/netmarkets/jsp/util/begin.jspf" %>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf" %>

<%
String folderOid = request.getParameter("folderOid");
String valueFieldId = request.getParameter("valueFieldId");
String displayFieldId = request.getParameter("displayFieldId");
String folderPickerCallback = request.getParameter("folderPickerCallback");
String folderPickerContainerOid = request.getParameter("folderPickerContainerOid");


String containerPickerValue = "";
String containerPickerDisplayText = "";

if (StringUtils.isNotBlank(folderOid)) {
	Folder folder = (Folder) ObjectReferenceUtils.getPersistable(folderOid, false);
	WTContainerRef containerRef = folder.getContainerReference();
	WTContainer container = containerRef.getReferencedContainer();
	containerPickerValue = containerRef.toString();
	containerPickerDisplayText = container.getName();
} else {
	folderOid = "";
}
if (StringUtils.isNotBlank(folderPickerContainerOid)) {
	containerPickerValue = folderPickerContainerOid;
}
%>
<script type="text/javascript">

function reloadFolderTree(containerOid) {
	//window.alert(containerOid);
	var tableId = "com.ptc.xworks.xmlobject.web.guifactory.FolderPickerFolderTreeTableBuilder";
	var folderTreeTable = PTC.jca.table.Utils.getTable(tableId);
	folderTreeTable.store.baseParams.folderPickerContainerOid = containerOid;
	PTC.jca.table.Utils.reload(tableId, {"folderPickerContainerOid" : containerOid }, true);
}

// 当选择了上下文容器后的回调函数
function contextPickerCallback(objects, pickerID, pickedAttributes, displayFieldId) {
    var updateHiddenField = document.getElementsByName(pickerID)[0];
    var updateDisplayField = document.getElementsByName(displayFieldId)[0];
    if(updateDisplayField) {
        PTC.util._setMainFormStartingElement(updateDisplayField);
    }
    var myJSONObjects = objects.pickedObject;
    var attrArray = [pickedAttributes];
    if (typeof pickedAttributes == 'string') {
    	var displayAttributeNames = pickedAttributes.replace(" ","");
    	if(displayAttributeNames.indexOf(",") > 0) {
    		var attrArrayTemp = displayAttributeNames.split(",");
    		attrArray = [];
    		for (var i = 0; i< attrArrayTemp.length; i++) {
    			//window.alert(attrArray[i]);
    			attrArray.push(attrArrayTemp[i]);
    		}
    	}
    }
    
    var containerOid = "";
    for (var i = 0; i < myJSONObjects.length; i++) {
        var oid = myJSONObjects[i].oid;
        var displayAttr = "";
        if (attrArray.length == 1) {
        	// 只有一个属性
        	displayAttr = myJSONObjects[i][attrArray[0]];
        } else {
    		// 有多个属性,则将多个属性链接起来    		
    		for (var j = 0; j< attrArray.length; j++) {
    			if (j == 0) {
    				displayAttr = myJSONObjects[i][attrArray[j]];
    			} else {
    				displayAttr = displayAttr + ", " +  myJSONObjects[i][attrArray[j]];
    			}
    		}
        }
        containerOid = oid;
        updateHiddenField.value = oid;
        updateDisplayField.value = displayAttr;
    }
    
    // 刷新文件夹树形表格
    reloadFolderTree(containerOid);
}

onSubmit = onSubmit.wrap(function(orgi, skipAllValidation, skipRequiredFieldValidation) {
	//window.alert("onSubmit");
	var tableId = "com.ptc.xworks.xmlobject.web.guifactory.FolderPickerFolderTreeTableBuilder";
	var selectedFolders = PTC.jca.table.Utils.getTableSelectedRowsById(tableId, true);
	if (selectedFolders == null || selectedFolders.length == 0) {
		//window.alert("Please select a folder!");
		JCAAlert("com.ptc.xworks.xmlobject.web.guifactory.guifactoryResource.SELECT_A_FOLDER_TO_CONTINUE");
		return;
	}
	orgi(skipAllValidation, skipRequiredFieldValidation);
});

</script>

<fmt:setBundle basename="com.ptc.xworks.xmlobject.web.guifactory.guifactoryResource"/>
<fmt:message var="labelForContextPicker" key="SELECT_CONEXT" />

<div class="xtb-text" style="margin-bottom: 15px;">
<wctags:contextPicker id="contextPicker" label="${labelForContextPicker}:" pickerTitle="${labelForContextPicker}" pickerCallback="contextPickerCallback" defaultValue="<%=containerPickerDisplayText%>" defaultHiddenValue="<%=containerPickerValue%>"/>
</div>

<input id="folderOid" type="hidden" name="folderOid" value="<%=folderOid%>"/>
<input id="valueFieldId" type="hidden" name="valueFieldId" value="<%=valueFieldId%>"/>
<input id="displayFieldId" type="hidden" name="displayFieldId" value="<%=displayFieldId%>"/>
<input id="folderPickerCallback" type="hidden" name="folderPickerCallback" value="<%=folderPickerCallback%>"/>

<jsp:include page="${mvc:getComponentURL('com.ptc.xworks.xmlobject.web.guifactory.FolderPickerFolderTreeTableBuilder')}">
	<jsp:param value="<%=containerPickerValue%>" name="folderPickerContainerOid"/>
</jsp:include>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>