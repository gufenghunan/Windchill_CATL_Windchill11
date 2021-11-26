<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>

<div class=" x-panel attribute-apanel x-reset x-form-label-left">
	<div id="workitem_details_attributes_group_input_container_div" class=" x-form"></div>
</div>
<script type="text/javascript">
var moveWorkItemCommentsDetailAttributeGroup = function() {
	//window.alert("onReady ...");
	var inputGroup = document.getElementById("workitem_details_attributes_group_input");//$("workitem_details_attributes_group_input");
	//window.alert(inputGroup);
	if (!inputGroup) {
		window.alert("workitem_details_attributes_group_input is " + inputGroup);
		//window.setTimeout(moveWorkItemCommentsDetailAttributeGroup, 100);
		return;
	}
	var inputGroupContainer = document.getElementById("workitem_details_attributes_group_input_container_div");//$("workitem_details_attributes_group_input_container_div");				
	if (!inputGroupContainer) {
		window.alert("workitem_details_attributes_group_input_container_div is " + inputGroupContainer);
		//window.setTimeout(moveWorkItemCommentsDetailAttributeGroup, 100);
		return;
	}
	//window.alert("before clone");
	var inputGroupClone = inputGroup.cloneNode();// cloneNode 这个方法在FireFox中也会发生错误 //document.createElement("fieldset");
	//window.alert(inputGroup);
	//inputGroup.hide(); // 这行在firefox中会出现错误，导致代码不再往下执行，使用下面代码替代
	//window.alert("before hide");
	inputGroup.style.display = "none";
	//window.alert("after hide");
	//inputGroupClone.setAttribute("id", "workitem_details_attributes_group_input_clone");
	//inputGroupClone.setAttribute("class", "x-fieldset x-form-label-left");
	//inputGroupClone.setAttribute("style", "width: auto; display: block;");
	var childs = inputGroup.childNodes;
	//window.alert(childs.length);
	var childsToRemove = new Array();
	//window.alert(childs.length);
	for (var i = 0; i < childs.length ; i++) {
		childsToRemove.push(childs.item(i));
		//var child = inputGroup.removeChild()
		//inputGroupClone.appendchild(child);
	}
	for (var i = 0; i< childsToRemove.length; i++) {
		inputGroup.removeChild(childsToRemove[i]);
		inputGroupClone.appendChild(childsToRemove[i]);
	}
	//window.alert(childsToRemove.length);
	inputGroupContainer.appendChild(inputGroupClone);
};
//PTC.onAvailable("workitem_details_attributes_group_input", function() {
//	window.alert("onAvailable");
	//moveWorkItemCommentsDetailAttributeGroup();
//});	
PTC.onReady(function() {
	//window.alert("on ready");
	moveWorkItemCommentsDetailAttributeGroup();
});
</script>	