
<%@ taglib prefix="jca"
	uri="http://www.ptc.com/windchill/taglib/components"%>

<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ page language="Java" pageEncoding="UTF-8"%>
<%@ page import="com.ptc.netmarkets.model.NmOid"%>
<%@ page import="wt.part.WTPart"%>
<%@ page import="com.catl.line.entity.BoxExplainChildPN"%>

<!--<jca:wizard title="衍生PN创建"> 
	<jca:wizardStep action="selectParentPN" type="line"/>
 </jca:wizard>-->
<style>
.x-tab-panel-header {
	border-color: #FFF;
}

.x-tab-panel-body {
	border-color: #FFF;
}

.x-form-display-field {
	color: #000;
	font-weight: bold;
}

.ext-strict .ext-gecko .x-form-field-trigger-wrap .x-form-text {
	height: 16px !important;
}

.gridtb_cls .x-form-field-wrap .x-form-trigger {
	top: -2px !important;
}
</style>

<% 
	String oid=request.getParameter("oid");
	NmOid nmOid = NmOid.newNmOid(oid);
	WTPart part = (WTPart)nmOid.getRefObject();
	String partNumber = part.getNumber();
	String version = part.getVersionIdentifier().getValue()+"."+part.getIterationIdentifier().getValue();  //大版本.小版本
%>
<script type="text/javascript">
	function windowclose() {
		window.close();
	}
	
	var winWidth=0,winHeight=0
	//步骤
	var step0="<div id=\"stephdr\" class=\" x-panel stepHeader x-panel-noborder\"><div class=\"x-panel-bwrap\"><div class=\"x-panel-body x-panel-body-noheader x-panel-body-noborder\" id=\"ext-gen17\"></div></div><div id=\"stripWrapHolder\" class=\"stepPanel\"><div id=\"stripWrap\" class=\"x-header-strip-wrap\" style=\"left: 0px;\"><ul id=\"stripTabWrap\" class=\"header-strip\"><li class=\"wizardstepLinks first active enabled\" tabindex=\"0\"><div class=\" stepIcon-active\">1</div>选择客户类型</li><li class=\"wizardstepLinks last enabled\" tabindex=\"0\"><div class=\" stepIcon-nonVisited\">2</div>填写装箱单说明</li></ul></div></div></div>";
	var step1="<div id=\"stephdr\" class=\" x-panel stepHeader x-panel-noborder\"><div class=\"x-panel-bwrap\"><div class=\"x-panel-body x-panel-body-noheader x-panel-body-noborder\" id=\"ext-gen17\"></div></div><div id=\"stripWrapHolder\" class=\"stepPanel\"><div id=\"stripWrap\" class=\"x-header-strip-wrap\" style=\"left: 0px;\"><ul id=\"stripTabWrap\" class=\"header-strip\"><li class=\"wizardstepLinks first active enabled\" tabindex=\"0\"><div class=\" stepIcon-nonVisited\">1</div>选择客户类型</li><li class=\"wizardstepLinks last enabled\" tabindex=\"0\"><div class=\" stepIcon-active\">2</div>填写装箱单说明</li></ul></div></div></div>";
	var rt = Ext.data.Record.create([{name: 'name'},{name: 'number'},{name:'quantity'},{name:'packageAsk'}]);
	var actionTab = 0;
	//第二步的表格
	var step2Column;
	//第二步的表格内容
	var step2ColumnStore;
	//第二步的表格工具栏
	var step2Toolbar;
	//包装要求 下拉框
	var packageAskComboBox;
	//包装要求 下拉框(表格中)
	var packageAskComboBoxCloumn;
	//第一步中选择的客户
	var selectedCustomer;
	Ext.onReady(function() {
		updatesize();
		//alert("1 winWidth:"+winWidth+",winHeight:"+winHeight)
		Ext.EventManager.onWindowResize(function(){
			 updatesize();
			 //alert("2 winWidth:"+winWidth+",winHeight:"+winHeight)
			 Ext.getCmp("step2Column").setWidth(winWidth*0.98);
			 Ext.getCmp("step2Column").setHeight(winHeight-340);
			 Ext.getCmp("title").setWidth(winWidth);
			 Ext.getCmp("footerpanel").setWidth(winWidth*0.98);
		});
		var title = new Ext.Toolbar({
			renderTo:Ext.getBody(),
			id:'title',
			height: 35,
			items: [
				{
					xtype:'displayfield',
					style:'position:relative;color:#636363;font-size:14px;left:2px;top:4px',
					value : '新建装箱说明'
				}
			]
		});
		var step = new Ext.Container({
			renderTo:Ext.getBody(),
			width:'100%',
			html:"<div id=\"step\">"+step0+"</div>"
		});
		var containerMain = new Ext.Container({
			id:'containerMain',
			renderTo:Ext.getBody(),
			width:'98%',
			style:'border:1px solid #c0c0c0;margin:0 auto;'
	    });	
		
		initExtComponet();
		//获取客户
		Ext.Ajax.request({
		  	disableCaching:false,
		  	url : "/Windchill/ptc1/boxExplain/getCustomer.do?date="+new Date(),
		  	success : function(response, opts) {
		  		var data=JSON.parse(response.responseText);
		  		var customerVar = "<div style=\"padding:5px;padding-top:15px;height:20px;font-weight:bold\">";
		  		for(var i=0; i<data.length; i++){
		  			customerVar = customerVar + "<input type='radio' name='customer' id='customer_"+data[i]+"' value="+data[i]+" /><span>"+data[i]+"</span>";
		  		}
		  		customerVar = customerVar + "</div>";
		  		//主内容panel
		  		var tabPanelMain = new Ext.TabPanel({
		  			id:'tabPanelMain',
		  			activeTab: 0,
		  			headerCfg:{style:'display:none'},
					renderTo:'containerMain',
					width:'98%',
					items: [{
						title: '选择客户',
						items:[{
								xtype: 'container',
								html:customerVar
						}]
					},
					{
						items:[
							{
								id: 'step2Input',
								xtype: 'form',
								html:''
							},
							{
								xtype: 'container',
								style:'border:1px solid #EEE;overflow:hidden',
								items:[
									step2Toolbar,
									step2Column
								]
							}
						 ]
					 }]
			    });
			}
		});
		//页脚
		var footpanel = new Ext.Container({
			renderTo : Ext.getBody(),
			layout : 'hbox',
			id : 'footerpanel',
			style:'margin:1%',
			items : [{
				xtype : 'displayfield',
				flex:1,
				style:'font-size:12px;font-weight:normal;color:#464646',
				value : '*表示必填字段'
		        },
		        {
					xtype : 'container',
					layout : 'table',
					items : [{
					    	xtype:'container',
				        	layout:'table',
				        	items: [{
				        		xtype : 'button',
								style : 'margin-left:10px',
								disabled : true,
								id : 'btn_prev',
								text : '上一步',
								handler : function(btn) {
									lastStep();
									Ext.getCmp("btn_prev").setDisabled(true);
									Ext.getCmp("btn_next").setDisabled(false);
									Ext.getCmp("btn_complete").setDisabled(true);
									Ext.getCmp("tabPanelMain").setActiveTab(actionTab);
								}
				        	},
							{
								xtype : 'button',
								style : 'margin-left:10px',
								id : 'btn_next',
								text : '下一步',
								handler : function(btn) {
									selectedCustomer = checkRadio("customer");
									if (selectedCustomer == null) {
										alert("请选择客户");
									} else {
										nextStep();
										Ext.getCmp("btn_prev").setDisabled(false);
										Ext.getCmp("btn_next").setDisabled(true);
										Ext.getCmp("btn_complete").setDisabled(false);
									}
								}
							},
							{
								xtype : 'button',
								disabled : true,
								style : 'margin-left:10px',
								id : 'btn_complete',
								text : '创建装箱说明',
								handler : function(btn){
									//验证包装要求是否都已填写
									var msg="";
									var total = step2ColumnStore.getCount();
									for(var i=0;i<total;i++){
									  var packageAskVal = step2ColumnStore.getAt(i).data.packageAsk;
									  if(packageAskVal==null||packageAskVal==""){
										  msg = msg + "包装要求必填、";
										  break;
									  }
									}
									var step2InputFromVal = document.getElementsByName("customer_input");
									for(var i=0; i<step2InputFromVal.length; i++){
										if(step2InputFromVal[i].value == ""||step2InputFromVal[i].value == null){
											msg = msg + step2InputFromVal[i].id+"必填、";
										}
									}
									if(msg != ""){
										alert(msg);
										return;
									}
									//提交表单
									Ext.getCmp("btn_complete").setDisabled(true);
							        submitForm();
								}
							},
							{
								xtype : 'button',
								style : 'margin-left:10px',
								id : 'btn_close',
								text : '关闭',
								handler : function() {
									windowclose();
								}
							}]
							
						}]
					}]
			});
			Ext.getCmp("footerpanel").setWidth(winWidth*0.98);
		});
	//初始化 组件
	function initExtComponet(){
		packageAskComboBox = new Ext.form.ComboBox({
			width:100,
			triggerAction: 'all',
			mode: 'local',
			id:'packageAskComboBox',
			style:'margin-left:5px',
			store: new Ext.data.ArrayStore({
				fields: [
					'value'
				]
			}),
			listeners:{
				//选择事件 填充列表选择的包装要求的值
				select:function(combo,record,index){
				    if (step2Column.getSelectionModel().hasSelection()) {
				         var records = step2Column.getSelectionModel().getSelections();
				         var mycars = new Array();
				         for ( var i = 0; i < records.length; i++) {
				        	 records[i].set("packageAsk",record.data.value);
				         }
				         return;
				     } else {
				    	 alert("请选中要设置的行!");
				     }
				}
			},
			valueField: 'value',
			displayField: 'value'
		});
		packageAskComboBoxCloumn = new Ext.form.ComboBox({
			allowBlank:false,
			editable:true,
			typeAhead: true,
			width:100,
			triggerAction: 'all',
			lazyRender:true,
			mode: 'local',
			id:'packageAskComboBoxCloumn',
			style:'margin-left:5px',
			store: new Ext.data.ArrayStore({
				fields: [
					'value'
				]
			}),
			valueField: 'value',
			displayField: 'value'
		});
		step2Toolbar = new Ext.Toolbar({
			cls:'gridtb_cls',
			id:'step2Toolbar',
			style:'padding:7px 0px',
			height: 35,
			items: [
					{
						xtype : 'displayfield',
						style:'margin-left:5px',
						value : '包装要求:'
					},
					packageAskComboBox
			]
		});
		step2ColumnStore = new Ext.data.Store({
			autoDestroy: true,
			reader:new Ext.data.ArrayReader(
				{
					idIndex: 1  // 每条记录的id将会是第一个元素
				},
				rt
			)
	    });	
		step2Column = new Ext.grid.EditorGridPanel({
			height:winHeight-340,
			store:step2ColumnStore,
			id:'step2Column',
			clicksToEdit:1,
			autoScroll:true,//滚动条
			cm: new Ext.grid.ColumnModel([
			            new Ext.grid.CheckboxSelectionModel ({singleSelect : false}),//checkbox 列    
						{
						  header: '名称', dataIndex: 'name',sortable: true
						},
						{
						  header: 'PN', dataIndex: 'number',sortable: true
						},
						{
						  header: '数量', dataIndex: 'quantity',sortable: true
						},
						{
						  header: '*包装要求',editor: packageAskComboBoxCloumn, dataIndex: 'packageAsk',sortable: true
						}
					]),
			viewConfig: {
				forceFit: true
			},
			sm: new Ext.grid.CheckboxSelectionModel ({singleSelect : false}),
			scrollable:true
	    });
		var url = "/Windchill/ptc1/boxExplain/getChildPN.do?number=<%=partNumber%>&date="+new Date();
		Ext.Ajax.request({
		  	disableCaching:false,
		  	url : url,
		  	success : function(response, opts) {
		  		var data=JSON.parse(response.responseText);
		  		console.log(data+",response.responseText="+response.responseText)
		  		step2ColumnStore.loadData(data);
		  	}
		});
	}
	//更改步骤
	function setActiveTab(){
		var setp=eval('step'+actionTab);
		document.getElementById("step").innerHTML=setp;
	}
	//检查必须选择客户
	function checkRadio(radio){
		var flag = false;
		var allRadio = document.getElementsByName(radio);
		for(var i=0; i<allRadio.length; i++){
		   if(allRadio[i].checked){
		    return allRadio[i];
		   }
		}
		return null;
	}

	//上一步动作
	function lastStep(){
		actionTab = actionTab-1;
		setActiveTab(actionTab);
		Ext.getCmp("tabPanelMain").setActiveTab(actionTab);
	}
	//下一步动作
	function nextStep(){
		actionTab = actionTab+1;
		setActiveTab(actionTab);
		Ext.getCmp("tabPanelMain").setActiveTab(actionTab);
		
		var step2InputHTML="";
		var url = "/Windchill/ptc1/boxExplain/getCustomerInput.do?";
		Ext.Ajax.request({
		  	disableCaching:false,
		  	url : url,
		  	params:{"customer" : selectedCustomer.value, "oid": "<%=oid%>", "date" : new Date()},
		  	success : function(response, opts) {
		  		var data=JSON.parse(response.responseText);
		  		console.log(response.responseText)
		  		for(var i=0; i<data.length; i++){
		  			var dataArr = data[i].split("---");
		  			//if(selectedCustomer.value == '宇通' && data[i]=='客户代码：'){
		  				step2InputHTML = step2InputHTML + "<div style=\"padding:5px;padding-top:15px;height:20px;font-weight:bold\"><span>*"+dataArr[0]+"</span>&nbsp;<input name=\"customer_input\" width=\"\" id='"+dataArr[0]+"' value='"+dataArr[1]+"' type=\"text\"/>&nbsp;</div>";
		  			//}else{
		  				//step2InputHTML = step2InputHTML + "<div style=\"padding:5px;padding-top:15px;height:20px;font-weight:bold\"><span>*"+dataArr[0]+"</span>&nbsp;<input name=\"customer_input\" width=\"\" id='"+dataArr[0]+"' value='"+dataArr[1]+"' type=\"text\"/>&nbsp;</div>";
		  			//}
		  		}
		  		document.getElementById("step2Input").innerHTML=step2InputHTML;
		  	}
		});
		url = "/Windchill/ptc1/boxExplain/getPackageAsk.do?";
		Ext.Ajax.request({
		  	disableCaching:false,
		  	url : url,
		  	params:{"customer" : selectedCustomer.value,"date" : new Date()},
		  	success : function(response, opts) {
		  		var data=JSON.parse(response.responseText);
		  		console.log("data="+data+",response.responseText="+response.responseText)
		  		packageAskComboBox.store.loadData(data);
		  		packageAskComboBoxCloumn.store.loadData(data);
		  	}
		});
			
	}
	//提交表单
	function submitForm(){
		var obj=new Object();
		obj.version='<%=version%>';
		obj.partNumber='<%=partNumber%>';
		obj.customer = selectedCustomer.value;
		var step2InputFromVal = document.getElementsByName("customer_input");
		var inputVal = "";
		for (var i = 0; i < step2InputFromVal.length; i++) {
			inputVal = inputVal + step2InputFromVal[i].value + "@&@";
		}
		if (inputVal != "") {
			inputVal = inputVal.substring(0, inputVal.length - 3);
		}
		obj.input = inputVal;
		//console.log(obj);
		var childArr = new Array();
		var total = step2ColumnStore.getCount();
		var child;
		for (var i = 0; i < total; i++) {
			var nameVal = step2ColumnStore.getAt(i).data.name;
			var numberVal = step2ColumnStore.getAt(i).data.number;
			var quantityVal = step2ColumnStore.getAt(i).data.quantity;
			var packageAskVal = step2ColumnStore.getAt(i).data.packageAsk;
			var child = {
				name : nameVal,
				number : numberVal,
				quantity : quantityVal,
				packageAsk : packageAskVal
			};
			//console.log(child);
			childArr.push(child)
		}
		childArr.sort(getSortFun('asc', 'packageAsk'));
		obj.list = childArr;
		//console.log(childArr);
		console.log(obj);
		//console.log(JSON.stringify(obj))
		var url = "/Windchill/ptc1/boxExplain/createBoxExplain.do";
		Ext.Ajax.request({
			method : 'POST',
			disableCaching : false,
			url : url,
			params : {
				"dataList" : JSON.stringify(obj),
				"date" : new Date()
			},
			success : function(response, opts) {
				if (response.responseText != "success") {
					alert("创建失败\n请联系管理员，错误信息：" + response.responseText);
					Ext.getCmp("btn_complete").setDisabled(false);
				} else {
					alert("创建成功");
					windowclose();
				}
			}
		});
	}
	//排序
	function getSortFun(order, sortBy) {
		var ordAlpah = (order == 'asc') ? '>' : '<';
		var sortFun = new Function('a', 'b', 'return a.' + sortBy + ordAlpah + 'b.' + sortBy + '?1:-1');
		return sortFun;
	}
	function updatesize() {
		if (window.innerWidth) {
			winWidth = window.innerWidth;
		} else if ((document.body) && (document.body.clientWidth)) {
			winWidth = document.body.clientWidth;
		}
		if (window.innerHeight) {
			winHeight = window.innerHeight;
		} else if ((document.body) && (document.body.clientHeight)) {
			winHeight = document.body.clientHeight;
		}
		if (document.documentElement && document.documentElement.clientHeight && document.documentElement.clientWidth) {
			winHeight = document.documentElement.clientHeight;
			winWidth = document.documentElement.clientWidth;
		}
	}
</script>
<%@include file="/netmarkets/jsp/util/end.jspf"%>
