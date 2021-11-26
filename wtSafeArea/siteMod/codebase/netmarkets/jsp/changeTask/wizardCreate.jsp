<%@page import="wt.change2.WTChangeRequest2"%>
<%@page import="wt.change2.WTChangeOrder2"%>
<%@page import="com.catl.change.util.*"%>
<%@page import="com.catl.change.*"%>
<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"
%><%@ taglib prefix="cwiz" uri="http://www.ptc.com/windchill/taglib/changeWizards"
%><%@ taglib prefix="security" uri="http://www.ptc.com/windchill/taglib/securitycomponents"
%><%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"
%><%@ include file="/netmarkets/jsp/components/beginWizard.jspf"
%><%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"
%><%@taglib prefix="c"           uri="http://java.sun.com/jsp/jstl/core"
%><%@taglib prefix="attachments" uri="http://www.ptc.com/windchill/taglib/attachments"
%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>
<%@page language="java" session="true" pageEncoding="UTF-8"%>

<%
//不在变更请求菜单中创建的变更通告都认为是DCN。
//如果创建的是DCN，则在页面中设置创建的初始化类型为：wt.change2.WTChangeActivity2 |com.CATLBattery.CATLDChangeActivity2；否则WTChangeActivity2的类型设置为：wt.change2.WTChangeActivity2 |com.CATLBattery.CATLChangeActivity2
Object obj = commandBean.getPageOid().getRefObject();
String baseType = "wt.change2.WTChangeActivity2|com.CATLBattery.CATLDChangeActivity2"; // 默认为设计变更任务
if(obj instanceof WTChangeRequest2){ // 如果是从ECR启动创建界面
	baseType = "wt.change2.WTChangeActivity2|com.CATLBattery.CATLChangeActivity2";
} else if(obj instanceof WTChangeOrder2) {
	String type = ChangeUtil.getStrSplit((wt.fc.Persistable) obj);
	if(type.equals(ChangeConst.CHANGEORDER_TYPE_DCN)){ 
		baseType = "wt.change2.WTChangeActivity2|com.CATLBattery.CATLDChangeActivity2";
	} else {
		baseType = "wt.change2.WTChangeActivity2|com.CATLBattery.CATLChangeActivity2";
	}
} else { // 从其它位置启动创建ChangeTask的话，都应该是创建DCA
	baseType = "wt.change2.WTChangeActivity2|com.CATLBattery.CATLDChangeActivity2";
}
%>

<jca:initializeItem operation="${createBean.create}" objectHandle="task" baseTypeName="<%=baseType%>" attributePopulatorClass="com.ptc.windchill.enterprise.change2.forms.populators.ChangeTaskAttributePopulator"/>

<%@include file="/netmarkets/jsp/change/changeWizardConfig.jspf" %>
<%@include file="/netmarkets/jsp/annotation/wizardConfig.jspf" %>

<cwiz:initializeChangeWizard changeMode="CREATE" annotationUIContext="change" changeItemClass="wt.change2.ChangeActivityIfc"/>
<cwiz:initializeNewChangeTask />
<cwiz:initializeDefaultChangeTaskType />
<security:getSecurityLabelsFromWizard clientData="${commandBean}"/>

<jca:wizard helpSelectorKey="change_createChangeTask" buttonList="DefaultWizardButtonsNoApply" formProcessorController="com.ptc.windchill.enterprise.change2.forms.controllers.EffectivityAwareIframeFormProcessorController" wizardSelectedOnly="true">
   <jca:wizardStep objectHandle="task" action="defineItemAttributesWizStep" type="object"/>
   <jca:wizardStep objectHandle="task" action="securityLabelStep" type="securityLabels"/>
   <jca:wizardStep objectHandle="task" action="affectedAndResultingItemsStep" type="changeTask" />
   <jca:wizardStep action="changeInventoryStep" type="catlInventory" label="<%=WTMessage.getLocalizedMessage(\"com.catl.change.inventory.resource.ChangeInventoryRB\",\"changeInventory.tableName\") %>" />
   <jca:wizardStep objectHandle="task" action="associatedChangeIssuesStep" type="changeTask" />
</jca:wizard>

<script language='Javascript'>
    PTC.wizardIframes.historyManager.readonlyDriverAttributeURL = "${mvc:getComponentURL('change.wizardPropertyPanel')}";
</script>

<script type="text/javascript">

setUserSubmitFunction(check);
		
		function check() 
		{
	   var pathname = window.location.pathname;
	    var splitPathname = pathname.split("/");
	    var wcHome = window.location.protocol + "//" + window.location.host + "/" + splitPathname[1] + "/";
	    var jspName = 'netmarkets/jsp/catl/changeTask/checkAffectData.jsp';
	    var fullURL = wcHome + jspName;

	    var loader = new ajax.ContentLoader(fullURL, null, null, 'POST', '&oid='+getParamFromQueryString('oid'));

	    var resText = null;
			if (loader.req.readyState == 4 && loader.req.status == 200) 
			{
		    resText = loader.req.responseText;

				if(resText.length > 6)
				{
					var r=confirm(resText)
				  if (r==true)
				  {
				   return false;
				  }
				  else
				  {
				    return false;
				  }
				}
		  }
		  //return false;
		}
		
		
		function getParamFromQueryString(paramToGet) {
			var hostUrl = window.location.search.substring(1);
			var paramValue = new Array();
			var paramValuePairs = hostUrl.split("&");


			for (i=0;i<paramValuePairs.length;i++) {
				paramValue = paramValuePairs[i].split("=");
				if (paramValue[0] == paramToGet) {
					var valueToReturn = paramValue[1];
					valueToReturn = valueToReturn.replace(/%3A/g, ':');

					return valueToReturn;
				}
			}
		}
function loadHarnessVariant() {
	var xmlhttp;
	var tb = document.getElementById("name_TB");
    var carList=document.getElementById("name");
	var model=carList.options[carList.selectedIndex].value;
	var type ="loadDTaskDesNames";
	
	<%if("wt.change2.WTChangeActivity2|com.CATLBattery.CATLDChangeActivity2".equals(baseType)){
		System.out.println("is here...................");
	%> 
		type = "loadDTaskDesNames";
	<%}else{%>
		type = "loadTaskDesNames";
	<%}
		System.out.println("is here...................");
	%>
	
	//处理DCA的名称,任务和step页的联动
	if(carList.options.length==3){
		if(carList.selectedIndex==1){
			insertStep("affectedAndResultingItems!~objectHandle~task~!");
			removeStep("changeInventoryStep");
		}else{
		    insertStep("changeInventoryStep");
			removeStep("affectedAndResultingItems!~objectHandle~task~!");
		}
	}else if(carList.options.length==2){
		insertStep("changeInventoryStep");
		removeStep("affectedAndResultingItems!~objectHandle~task~!");
	}else{
		if(carList.selectedIndex!=10){
			insertStep("affectedAndResultingItems!~objectHandle~task~!");
			removeStep("changeInventoryStep");
		}else{
		    insertStep("changeInventoryStep");
			removeStep("affectedAndResultingItems!~objectHandle~task~!");
		}
	}
	
	tb.value = model;
	var modelList=document.getElementById("taskDescription");
	
	while (modelList.options.length) {
		modelList.remove(0);
	}
	
	if (window.XMLHttpRequest) {
		// code for IE7+, Firefox, Chrome, Opera, Safari
		xmlhttp=new XMLHttpRequest();
	}
	else {
		// code for IE6, IE5
		xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	}
	
	xmlhttp.onreadystatechange=function() {

		if (xmlhttp.readyState==4 && xmlhttp.status==200) {
			var string = (xmlhttp.responseText).replace(/\s+/g,'');
			var cars = string.split(',');
			for (var i=0;i<cars.length;i++)	{
					var car=new Option(cars[i],cars[i]);
					modelList.options.add(car);
				}
			
		}
	}

	xmlhttp.open("GET","netmarkets/jsp/catl/changeTask/"+type+".jsp?model="+encodeURI(encodeURI(model)),true);
	xmlhttp.send();
}
var si = setInterval(initChangeDes,500);
function initChangeDes(){
    var carList=document.getElementById("name");
    var modelList=document.getElementById("taskDescription");
    if(carList && modelList){
        clearInterval(si);
        loadHarnessVariant();
    }
}
</script>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>