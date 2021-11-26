 <%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
 
 <%@ include file="/netmarkets/jsp/components/beginWizard.jspf" %> 
 <%@ page language="Java" pageEncoding="UTF-8"%>
 <input type="hidden" name="oid" id="urla" value=""/>
 
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
	 .x-form-display-field{
		 color:#000;
		 font-weight:bold;
	 }
	 .ext-strict .ext-gecko .x-form-field-trigger-wrap .x-form-text{
	  height:16px !important;
	 }
	 .gridtb_cls .x-form-field-wrap .x-form-trigger{
	 top:-2px !important;
	 }
	.x-btn button{
	    background: transparent;
	    padding-left: 3px !important;
	    padding-right: 3px !important;
	}
	
   </style>
	<script type="text/javascript">
	var oid='<%=request.getParameter("oid")%>';
	var website='<%=request.getScheme()+"://"+request.getServerName()%>';
	var winWidth=0,winHeight=0;
	var cconnectorcount=0;
	var clinetype;
	var cmpn='';
		 Ext.onReady(function() {
			  updatesize();
			  Ext.EventManager.onWindowResize(function(){
				 updatesize();
				 Ext.getCmp("title").setWidth(winWidth);
				 Ext.getCmp("footerpanel").setWidth(winWidth*0.98);
				 Ext.getCmp("content_display").setHeight(winHeight-150);
				 Ext.getCmp("mpn_tab_panel").setHeight(winHeight-160);
			 });
			var titlebar = new Ext.Toolbar({
						renderTo:Ext.getBody(),
						id:'title',
						width:winWidth,
						height: 35,
						items: [
							{
								xtype:'displayfield',
								style:'position:relative;color:#636363;font-size:14px;left:2px;top:4px',
								value : '更新衍生PN'
							}
						]
					});
			 var tabbar = new Ext.Container({
						renderTo:Ext.getBody(),
						width:'98%',
						height:40,
						html:'<div style="position:relative;top:2px;left:1%"><ul id="stripTabWrap" class="header-strip"><li class="wizardstepLinks single active enabled" id="defineItemAttributesWizStep!~objectHandle~partHandle~!_link" tabindex="0"><div class=" stepIcon-active" id="defineItemAttributesWizStep!~objectHandle~partHandle~!_icon">1</div>设置属性</li></ul></div>'
				});	 
			  var content = new Ext.Container({
					renderTo:Ext.getBody(),
					width:'98%',
					id:'content_display',
					height:winHeight-150,
					style:'border:1px solid #c0c0c0;margin:0 auto;overflow-y:scroll'
				 });	 
				  
			  Ext.Ajax.request({
				  disableCaching:false,
				  url : "/Windchill/ptc1/line/getChildPN.do?oid="+oid,
				  success : function(response, opts) {
				  var data=JSON.parse(response.responseText).data;
				  if(data.length>0){
					  data=data[0]
				  }
				  console.log(data);
				  var extra_cls='';
				  var catl_l_cls='';
				  var catl_l1_cls='';
				  //正式环境要改的地方-----
				  //var lconnector=data.lconnector;
				 // var rconnector=data.rconnector;
				 // var ldconnector=data.ldconnector;
				  //var rdconnector=data.rdconnector;
				 // var linetype=data.linetype;
				 // var maxcablesection=data.maxcablesection;
				 // var parentPN=data.parentPN;
				 // var rdconnector=data.rdconnector;
				  //var cablecount=data.cablecount;
				  //var undercablecount=data.undercablecount;
				  var lconnector=data.CATL_Lconnector;
				  var rconnector=data.CATL_Rconnector;
				  var ldconnector=data.CATL_Ldconnector;
				  var rdconnector=data.CATL_Rdconnector;
				  var linetype=data.CATL_Linetype;
				  clinetype=linetype;
				  var maxcablesection=data.Maximum_Section_Area;
				  var parentPN=data.CATL_ParentPN;
				  var rdconnector=data.CATL_Rdconnector;
				  var cablecount=data.CATL_Cablecount;
				  var undercablecount=data.CATL_Undercablecount;
				  cmpn=parentPN;
				  //--------
				  var CATL_L1=data.CATL_L1;
				  var CATL_L2=data.CATL_L2;
				  var CATL_L3=data.CATL_L3;
				  var CATL_PointA=data.CATL_PointA;
				  var cdisabled=true;
				  var ddisabled=true;
				  if(CATL_PointA==0){
					  CATL_PointA='';
				  }else{
					  cdisabled=false;
				  }
				  var CATL_PointB=data.CATL_PointB;
				  if(CATL_PointB==0){
					  CATL_PointB='';
				  }else{
					  ddisabled=false;
				  }
				  var CATL_PointC=data.CATL_PointC;
				  if(CATL_PointC==0){
					  CATL_PointC='';
				  }else{
					  cdisabled=false;
				  }
				  var CATL_PointD=data.CATL_PointD;
				  if(CATL_PointD==0){
					  CATL_PointD='';
				  }else{
					  ddisabled=false;
				  }
				  var CATL_Ltag_Box=data.CATL_Ltag_Box;
				  var CATL_Rtag_Box=data.CATL_Rtag_Box;
				  var CATL_Ltag_Desc=data.CATL_Ltag_Desc;
				  var CATL_Rtag_Desc=data.CATL_Rtag_Desc;
				  var CATL_Dtag_Desc=data.CATL_Dtag_Desc;
				  var CATL_Llbenchmark=data.CATL_Llbenchmark;
				  var connectorcount=data.connectorcount;
				  cconnectorcount=connectorcount;
				  var ishidden=false;
					  if(connectorcount==2){
						  extra_cls='display:none';
						  ishidden=true;
						  catl_l1_cls='display:none';
					  }else{
						  catl_l_cls='display:none';
					  }
				  var tabs = new Ext.Container({
						id:'mpn_tab_panel',
						renderTo:'content_display',
						width:'99.5%',
						height:winHeight-160,
						items: [
						{
							items:[
							{
								xtype:'container',
								html :'<div style="padding:5px;height:20px;font-weight:bold"><span>母PN信息</span></div>'
							},
							{
								xtype:'container',
								items:[
									{
										xtype: 'container',
										style:'margin-top:15px',
										layout:'table',
										width:'98%',
										height:50,
										items:[
											{
												xtype : 'displayfield',
												style:'margin-left:40px',
												value : '接插件左:'
											},
											{
												xtype : 'displayfield',
												style:'margin-left:10px;font-weight:normal',
												width:100,
										        value:lconnector
											},
											{
												xtype : 'displayfield',
												style:'margin-left:40px',
												value : '接插件右:'
											},
											{
												xtype : 'displayfield',
												width:100,
												style:'margin-left:10px;font-weight:normal',
												 value:rconnector
											},
											{
												xtype : 'displayfield',
												style:'margin-left:40px',
												value : '接插件左下:'
											},
											{
												xtype : 'displayfield',
												style:'margin-left:10px;font-weight:normal',
												width:100,
												value:ldconnector
											},
											{
												xtype : 'displayfield',
												style:'margin-left:40px',
												value : '接插件右下:'
											},
											{
												xtype : 'displayfield',
												width:100,
												style:'margin-left:10px;font-weight:normal',
												value:rdconnector
											} 
										]
									},
									{
										xtype: 'container',
										style:'margin-top:15px',
										layout:'table',
										width:'98%',
										height:50,
										items:[
										   {
												xtype : 'displayfield',
												style:'margin-left:40px',
												value : '线束类型:'
										   },
										   {
												xtype : 'displayfield',
												style:'margin-left:10px;font-weight:normal',
												width:100,
												value:linetype
										   },
										   {
												xtype : 'displayfield',
												style:'margin-left:40px',
												value : '最大导线截面积:'
										   },
										   {
												xtype : 'displayfield',
												style:'margin-left:10px;font-weight:normal',
												width:100,
												value:maxcablesection
										   },
										   {
												xtype : 'displayfield',
												style:'margin-left:40px',
												value : '母PN:'
										   },
										   {
												xtype : 'displayfield',
												width:100,
												style:'margin-left:10px;font-weight:normal',
												value : parentPN
										   }
										]
									
									},
									{
										xtype: 'container',
										style:'margin-top:15px',
										layout:'table',
										width:'98%',
										height:50,
										items:[
									       {
												xtype : 'displayfield',
												style:'margin-left:40px',
												value : '导线总根数:'
										   },
										   {
												xtype : 'displayfield',
												style:'margin-left:10px;font-weight:normal',
												width:100,
												value: cablecount
										   },
										   {
												xtype : 'displayfield',
												style:'margin-left:40px',
												value : '导线下根数:'
										   },
										   {
												xtype : 'displayfield',
												width:100,
												style:'margin-left:10px;font-weight:normal',
												value: undercablecount
										   }
										]
									}
								]
							},
							{
								xtype:'container',
								html :'<div style="padding:5px;height:20px;font-weight:bold"><span>衍生PN信息</span></div>'
							},
							{
								xtype:'form',
								id:'cpnform',
								items:[
										{
											xtype: 'container',
											style:'margin-top:15px',
											layout:'table',
											width:'98%',
											height:50,
											items:[
											       {
														xtype : 'displayfield',
														style:'margin-left:10px',
														value : '*请输入线束长度(mm):'
												   },
												   {
													   xtype:'container',
													   style:catl_l_cls,
													   layout:'table',
													   items:[
													          {
																	xtype : 'numberfield',
																	id:'CATL_L',
																	name:'CATL_L',
																	value:CATL_L1,
																	width:100,
																	minValue:1,
																	listeners:{
																		blur :function(field){
																			validatelinelength(field.getValue());
																		}
																	}
															   },
															   {
																	xtype : 'container',
																	html:'<span style=\"color:red;font-weight:normal\" id=\"lengtherrormsg\"></span>'
															   }
													    ]
												   },
												   {
													   xtype:'container',
													   style:catl_l1_cls,
													   layout:'table',
													   items:[
																{
																	xtype : 'displayfield',
																	style:'margin-left:10px',
																	id:'CATL_L1_label',
																	value : '*L1:'
																},
																{
																	xtype : 'numberfield',
																	name:'CATL_L1',
																	id:'CATL_L1',
																	value:CATL_L1,
																	width:100,
																	minValue:1
																},
																{
																	xtype : 'displayfield',
																	style:'margin-left:40px',
																	id:'CATL_L2_label',
																	value : '*L2:'
																},
																{
																	xtype : 'numberfield',
																	name:'CATL_L2',
																	id:'CATL_L2',
																	value:CATL_L2,
																	width:100,
																	minValue:1
																},
																{
																	xtype : 'displayfield',
																	style:'margin-left:40px',
																	id:'CATL_L3_label',
																	value : '*L3:'
																},
																{
																	xtype : 'numberfield',
																	name:'CATL_L3',
																	id:'CATL_L3',
																	value:CATL_L3,
																	width:100,
																	minValue:1
																}
													   ]
												   }
												   
											]
										},
										{
											xtype: 'container',
											width:'98%',
											layout:'table',
											height:50,
											items:[
												 {
													xtype : 'displayfield',
													style:'margin-left:40px',
													value : '*左标签:'
												},
												 new Ext.form.ComboBox({
													typeAhead: true,
													triggerAction: 'all',
													lazyRender:true,
													editable:false,
													id:'combo1',
													name:'CATL_Ltag_Box',
													value:CATL_Ltag_Box,
													width:100,
													store: new Ext.data.ArrayStore({
														fields: [
															'value'
														]
														
													}),
													listeners:{
														focus:function(field){
																 var combo=Ext.getCmp('combo1');
																 var m= combo.store;
																  Ext.Ajax.request({
																  disableCaching:false,
																  url:"/Windchill/ptc1/line/getTagBoxDesc.do",
																  params:{coltype:'左箱体',linetype:linetype},
																  success:function(response, opts) {
																  var data=JSON.parse(response.responseText);
																  console.log(data);
																  m.loadData(data);
																  },
																  failure : function(form, action) {
																	alert("请求失败");
																 }
															 });
															}
													},
													valueField: 'value',
													displayField: 'value'
												}),
												 new Ext.form.ComboBox({
													typeAhead: true,
													id:'combo2',
													name:'CATL_Ltag_Desc',
													editable:false,
													value:CATL_Ltag_Desc,
													triggerAction: 'all',
													lazyRender:true,
													width:100,
													store: new Ext.data.ArrayStore({
														fields: [
															'value'
														]
													}),
													listeners:{
														focus:function(field){
																		 var combo=Ext.getCmp('combo2');
																		 var m= combo.store;
																		 var val=Ext.getCmp('combo1').getValue();
																		  Ext.Ajax.request({
																		  disableCaching:false,
																		  url : "/Windchill/ptc1/line/getTagBoxDesc.do",
																		  params:{coltype:'左箱体描述',linetype:linetype,key:'左箱体',value:val},
																		  success : function(response, opts) {
																		  var data=JSON.parse(response.responseText);
																		  console.log(data);
																		  m.loadData(data);
																		  },
																		  failure : function(form, action) {
																			alert("请求失败");
																		 }
																	 });
															}
													},
													valueField: 'value',
													displayField: 'value'
												}),
												{
													xtype : 'displayfield',
													style:'margin-left:65px',
													value : '*右标签:'
												},
												 new Ext.form.ComboBox({
													typeAhead: true,
													triggerAction: 'all',
													lazyRender:true,
													editable:false,
													id:'combo3',
													name:'CATL_Rtag_Box',
													value:CATL_Rtag_Box,
													width:100,
													store: new Ext.data.ArrayStore({
														fields: [
															'value'
														]
													}),
													listeners:{
														focus:function(field){
																 var combo=Ext.getCmp('combo3');
																 var m= combo.store;
																  Ext.Ajax.request({
																  disableCaching:false,
																  url:'/Windchill/ptc1/line/getTagBoxDesc.do',
																  params : {coltype:'右箱体',linetype:linetype},
																  success : function(response, opts) {
																  var data=JSON.parse(response.responseText);
																  console.log(data);
																  m.loadData(data);
																  },
																  failure : function(form, action) {
																	alert("请求失败");
																 }
															 });
															}
													},
													valueField: 'value',
													displayField: 'value'
												}),
												new Ext.form.ComboBox({
													typeAhead: true,
													id:'combo4',
													editable:false,
													triggerAction: 'all',
													name:'CATL_Rtag_Desc',
													value:CATL_Rtag_Desc,
													lazyRender:true,
													width:100,
													store: new Ext.data.ArrayStore({
														fields: [
															'value'
														]
														
													}),
													listeners:{
														focus:function(field){
																		  var combo=Ext.getCmp('combo4');
																		   var m= combo.store;
																		   var val=Ext.getCmp('combo3').getValue();
																		  Ext.Ajax.request({
																		  disableCaching:false,
																		  url : "/Windchill/ptc1/line/getTagBoxDesc.do",
																		  params : {coltype:'左箱体描述',linetype:linetype,key:'左箱体',value:val},
																		  success : function(response, opts) {
																		  var data=JSON.parse(response.responseText);
																		  m.loadData(data);
																		  },
																		  failure : function(form, action) {
																			alert("请求失败");
																		 }
																	 });
															}
													},
													valueField: 'value',
													displayField: 'value'
												}),
												{
													xtype : 'displayfield',
													id:'combo5label',
													style:'margin-left:65px;'+extra_cls,
													value : '*下标签:'
												},  
												new Ext.form.ComboBox({
													typeAhead: true,
													hidden :ishidden,
													style:'margin-left:65px;',
													id:'combo5',
													editable:false,
													triggerAction: 'all',
													name:'CATL_Dtag_Desc',
													value:CATL_Dtag_Desc,
													lazyRender:true,
													width:100,
													store: new Ext.data.ArrayStore({
														fields: [
															'value'
														]
													}),
													listeners:{
														focus:function(field){
															  var combo=Ext.getCmp('combo5');
															   var m= combo.store;
															   var val=Ext.getCmp('combo5').getValue();
															  Ext.Ajax.request({
															  disableCaching:false,
															  url : "/Windchill/ptc1/line/getTagBoxDesc.do",
															  params : {coltype:'下标签',linetype:linetype},
															  success : function(response, opts) {
															  var data=JSON.parse(response.responseText);
															  m.loadData(data);
															  },
															  failure : function(form, action) {
																alert("请求失败");
															 }
														 });
												}
										},
										valueField: 'value',
										displayField: 'value'
										})
										]
									},
									{
										xtype: 'container',
										//style:'margin-top:15px',
										width:'98%',
										layout:'table',
										height:50,
										items:[
										       {
													xtype : 'displayfield',
													style:'margin-left:10px',
													value : '*客户线长基准:'
											   },
											   new Ext.form.ComboBox({
								                    typeAhead: true,
													triggerAction: 'all',
													editable:false,
													lazyRender:true,
													name:'CATL_Llbenchmark',
													id:'CATL_Llbenchmark',
													mode:'local',
													width:250,
													store: new Ext.data.ArrayStore({
														fields: [
															'value'
														]
													}),
													value:CATL_Llbenchmark,
													valueField: 'value',
													displayField: 'value',
													listeners: {
														focus:function(field){
															var combo=Ext.getCmp('CATL_Llbenchmark');
															var m=combo.store;
															var myData = '';
															if(linetype=='高压线束'){
																myData = [
																	['格兰头尾部到快插连接器尾部'],['锁螺栓端子中心孔到锁螺栓端子中心孔'],['锁螺栓端子尾部到锁螺栓端子尾部'],['锁螺栓端子尾部到快插连接器尾部'],['锁螺栓端子中心孔到快插连接器尾部'],['格兰头尾部到格兰头尾部']
																];
															}else if(linetype=='低压线束'){
																myData = [
																	['低压连接器尾部到低压连接器尾部'] //注意记录的id将会是第一个元素
																];
															}else{
																myData = [
																	['加热连接器尾部到加热连接器尾部']//注意记录的id将会是第一个元素
																];
															}
															m.loadData(myData);		
														}
													}
												}),
											   
										]
									},
									{
											xtype: 'container',
											width:'98%',
											layout:'table',
											height:50,
											items:[
												 {
													xtype : 'displayfield',
													style:'margin-left:40px',
													value : '定位点:'
												},
												{
													xtype : 'displayfield',
													style:'margin-left:10px',
													value : 'a:'
												},
												{
													xtype : 'numberfield',
													name:'CATL_PointA',
													value:CATL_PointA,
													width:100,
													enableKeyEvents:true,
													listeners:{
														keyup :function(field,e){
															var val=field.getValue();
															if(val!=""){
																Ext.getCmp("CATL_PointC").setDisabled(false);
															}else{
																Ext.getCmp("CATL_PointC").setDisabled(true);
																Ext.getCmp("CATL_PointC").setValue("");
															}
														}
													}
												},
												{
													xtype : 'displayfield',
													style:'margin-left:10px',
													value : 'b:'
												},
												{
													xtype : 'numberfield',
													name:'CATL_PointB',
													value:CATL_PointB,
													width:100,
													enableKeyEvents:true,
													listeners:{
														keyup :function(field,e){
															var val=field.getValue();
															if(val!=""){
																Ext.getCmp("CATL_PointD").setDisabled(false);
															}else{
																Ext.getCmp("CATL_PointD").setDisabled(true);
																Ext.getCmp("CATL_PointD").setValue("");
															}
														}
													}
												},
												{
													xtype : 'displayfield',
													style:'margin-left:10px',
													value : 'c:'
												},
												{
													xtype : 'numberfield',
													name:'CATL_PointC',
													value:CATL_PointC,
													id:'CATL_PointC',
													disabled:cdisabled,
													width:100
												},
												{
													xtype : 'displayfield',
													style:'margin-left:10px',
													value : 'd:'
												},
												{
													xtype : 'numberfield',
													id:'CATL_PointD',
													name:'CATL_PointD',
													value:CATL_PointD,
													disabled:ddisabled,
													width:100
												}
											]
									}
								]
							}
							]
						}]
					});
				  
				  }
			  });

			var footpanel = new Ext.Container({
					renderTo:Ext.getBody(),
					width:winWidth,
					layout:'hbox',
					id:'footerpanel',
					width:winWidth*0.98,
					style:'margin:1%',
					items:[
						{
						xtype : 'displayfield',
						flex:1,
						style:'font-size:12px;font-weight:normal;color:#464646',
						value : '*表示必填字段'
						},
						{
							xtype:'container',
							layout:'table',
							items: [
									{
										xtype : 'button',
										style:'margin-left:10px',
										id:'btn_create',
										text : '更新(U)',
										handler:function(){
											updatechildpn(true);
										}
									},
									{
										xtype : 'button',
										style:'margin-left:10px',
										id:'btn_close',
										text : '关闭(C)',
										handler:function(){
											windowclose();
										}
									}
								]
						}
						
					] 
			   });	 
				  
	    });

	  function updatesize(){
		  if (window.innerWidth){
				winWidth = window.innerWidth;
			   }else if ((document.body) && (document.body.clientWidth)){
				winWidth = document.body.clientWidth;
			   }
			   if (window.innerHeight){
				winHeight = window.innerHeight;
			   }else if ((document.body) && (document.body.clientHeight)){
				winHeight = document.body.clientHeight;
			   }
				if (document.documentElement && document.documentElement.clientHeight && document.documentElement.clientWidth)
				{
				winHeight = document.documentElement.clientHeight;
				winWidth = document.documentElement.clientWidth;
		       }
	  }
	  function updatechildpn(){
 		 var m= Ext.getCmp("cpnform").getForm().getValues();
 		 if(cconnectorcount==2){
	   			  m.CATL_L1=m.CATL_L;
	   			  delete m.CATL_L;
	   			  delete m.CATL_L2;
	   			  delete m.CATL_L3;
	   			  delete m.CATL_Dtag_Desc;
	   		  }else{
	   			  delete m.CATL_L;
	   		  }
 		if(m.CATL_PointC==undefined){
 			m.CATL_PointC="";
 		}
 		if(m.CATL_PointD==undefined){
 			m.CATL_PointD="";
 		}
 
			 var values=JSON.stringify(m);
			 Ext.Ajax.request({
			     disableCaching:false,
				  url : "/Windchill/ptc1/line/validateChildPN.do",
				  params:{
					      values:JSON.stringify(m),
					      number:cmpn
				  },
				  success : function(response, opts) {
					  var msg=JSON.parse(response.responseText).msg;
					  if(msg==""){
						  var mk=new Ext.LoadMask(document.body,{msg:'正在更新衍生PN，请稍后',removeMask:true});
						  mk.show();
						  Ext.Ajax.request({
						      disableCaching:false,
							  url : "/Windchill/ptc1/line/updateChildPN.do",
							  params:{
								      values:JSON.stringify(m),
								      oid:oid
							  },
							  success : function(resp, opts) {
								      mk.hide();
					        		  var msg=JSON.parse(resp.responseText).msg;
					        		  var newnum=JSON.parse(resp.responseText).newnum;
						        		  if(msg==""){
						        			  displayChildPNPdf(newnum);
						        		  }else{
						        			  alert(msg);
						        		  }
								  
						  	  },
						  	  failure:function(){
						  		       mk.hide();
						  	  }
				    	   });
					  }else{
						  Ext.Msg.confirm('系统提示','系统中已存在完全相同的线束“'+msg+'”');
					  }
						 
			  	  }
	    	   });
   }
	  function validatelinelength(val){
		  var result;
		  if(clinetype=="加热线束"){
			  result=validatelinelengthjiare(val);
			  if(result.length==0){
				  document.getElementById("lengtherrormsg").innerHTML="";
			  }else{
				  document.getElementById("lengtherrormsg").innerHTML="&nbsp;输入值非标准值,您是否想要输入"+result[0]+"或"+result[1];
			  }
		  }else if(clinetype=="低压线束"){
			  result=validatelinelengthdiya(val);
			  if(result.length==0){
				  document.getElementById("lengtherrormsg").innerHTML="";
			  }else{
				  document.getElementById("lengtherrormsg").innerHTML="&nbsp;输入值非标准值,您是否想要输入"+result[0]+"或"+result[1];
			  }
		  }else if(clinetype=="高压线束"){
			  result=validatelinelengthgaoya(val);
			  if(result.length==0){
				  document.getElementById("lengtherrormsg").innerHTML="";
			  }else{
				  Ext.Ajax.request({
					  disableCaching:false,
					  url : "/Windchill/ptc1/line/retireRecommendL.do",
					  params:{minl:result[0],maxl:result[1],currentl:val},
					  success : function(response, opts) {
						  var data=JSON.parse(response.responseText).data;
						  var msg=JSON.parse(response.responseText).msg;
						  if(msg==""){
							  if(data[0]==val||data[1]==val){
							  document.getElementById("lengtherrormsg").innerHTML="";  
							  }else{
							  document.getElementById("lengtherrormsg").innerHTML="&nbsp;输入值非标准值,您是否想要输入"+data[0]+"或"+data[1];
							  }
						  }else{
							  document.getElementById("lengtherrormsg").innerHTML=msg;
						  }
					  },
					  failure:function(){
						  document.getElementById("lengtherrormsg").innerHTML="获取推荐值出错";
					  }
				  });
			  }
		  }
	  }
	  function validatelinelengthjiare(val){
	       if(val<=0){
	    	   alert("输入数值非法");
	       }else if(val>5000){
	    	   var remain=val-5000;
	    	   var c=parseInt(remain/150);
	    	   var min=c*150+5000;
	    	   var max=c*150+5150;
	    	   if((remain%150)!=0){
	    		   return [min,max];
	    		   //document.getElementById("lengtherrormsg").innerHTML="&nbsp;输入值非标准值,您是否想要输入"+min+"或"+max;
	    	   }else{
	    		   return [];
	    		   //document.getElementById("lengtherrormsg").innerHTML="";
	    	   }
	       }else if(val>1000){
	    	   var remain=val-1000;
	    	   var c=parseInt(remain/100);
	    	   var min=c*100+1000;
	    	   var max=c*100+1100;
	    	   if((remain%100)!=0){
	    		   return [min,max];
	    		   //document.getElementById("lengtherrormsg").innerHTML="&nbsp;输入值非标准值,您是否想要输入"+min+"或"+max;
	    	   }else{
	    		   return [];
	    		   //document.getElementById("lengtherrormsg").innerHTML="";
	    	   }
	       }else{
	    	   var remain=val;
	    	   var c=parseInt(remain/50);
	    	   var min=c*50;
	    	   var max=c*50+50;
	    	   if((remain%50)!=0){
	    		   return [min,max];    		   
	    	   }else{
	    		   return [];
	    	   }
	       }
 }
 
	 function validatelinelengthdiya(val){
	      if(val<=0){
	   	   alert("输入数值非法");
	      }else if(val>5000){
	   	   var remain=val-5000;
	   	   var c=parseInt(remain/150);
	   	   var min=c*150+5000;
	   	   var max=c*150+5150;
	   	   if((remain%150)!=0){
	   		   return [min,max];    	
	   	   }else{
	   		   return [];    	
	   	   }
	      }else if(val>1000){
	   	   var remain=val-1000;
	   	   var c=parseInt(remain/100);
	   	   var min=c*100+1000;
	   	   var max=c*100+1100;
	   	   if((remain%100)!=0){
	   		   return [min,max];    	
	   	   }else{
	   		   return [];    	
	   	   }
	      }else{
	   	   var remain=val;
	   	   var c=parseInt(remain/50);
	   	   var min=c*50;
	   	   var max=c*50+50;
	   	   if((remain%50)!=0){
	   		   return [min,max];    	
	   	   }else{
	   		   return [];    	
	   	   }
	      }
	}
	 
	 function validatelinelengthgaoya(val){
	      if(val<=0){
	   	   alert("输入数值非法");
	      }else if(val>3000){
	   	   var remain=val-3000;
	   	   var c=parseInt(remain/40);
	   	   var min=c*40+3000;
	   	   var max=c*40+3040;
	   	   if((remain%40)!=0){
	   		   return [min,max];    	
	   	   }else{
	   		   return [];    	
	   	   }
	      }else if(val>1500){
	   	   var remain=val-1500;
	   	   var c=parseInt(remain/30);
	   	   var min=c*30+1500;
	   	   var max=c*30+1530;
	   	   if((remain%30)!=0){
	   		   return [min,max];    	
	   	   }else{
	   		   return [];    	
	   	   }
	      }else if(val>600){
	   	   var remain=val-600;
	   	   var c=parseInt(remain/25);
	   	   var min=c*25+600;
	   	   var max=c*25+625;
	   	   if((remain%25)!=0){
	   		   return [min,max];    	
	   	   }else{
	   		   return [];    	
	   	   }
	      }else{
	   	   var remain=val;
	   	   var c=parseInt(remain/10);
	   	   var min=c*10;
	   	   var max=c*10+10;
	   	   if((remain%10)!=0){
	   		   return [min,max];    	
	   	   }else{
	   		   return [];    	
	   	   }
	      }
	}
      function displayChildPNPdf(number){
    	  Ext.Ajax.request({
			  disableCaching:false,
			  url : "/Windchill/ptc1/line/getViewPDF.do?number="+number,
			  success : function(response, opts) {
			  var url=JSON.parse(response.responseText).url;
			  var msg=JSON.parse(response.responseText).msg;
				  if(url==undefined){
					  alert(msg);
				  }else if(url==null){
					  alert("找不到文档附件");
				  }else{
				   var outerwin=new Ext.Window({  
						   width: winWidth-150,  
						   height: winHeight-100,  
						   resizable:false,
						   closable: true,  
						   draggable: true,  
						   style:'padding:10px',
						   closeAction: "hide",
						   items:[
							   {
								 xtype:'container',
								 id:'childpnpdf',
								 html:'<iframe width=\"'+(winWidth-200)+'\" height=\"'+(winHeight-150)+'\" src=\"http://'+url+'\"> </iframe>'
							   }
							],
							bbar:[
							   {
								 xtype : 'button',
								 text : '下载DWG',
								 handler:function(btn){
										downloadDwg(number);
								}
							   },
							   {
								xtype : 'button',
								text : '上传DWG',
								handler:function(){
									 var innerwin = new Ext.Window({  
									   title: "上传DWG",  
									   width: 600,  
									   height: 150,  
									   closable: true,  
									   draggable: true,  
									   closeAction: "hide",  
									   buttons: [{  
										   text: "确定", handler: function () {  
											   var form=Ext.getCmp("uploadform").getForm();
												form.submit({
													url: '/Windchill/ptc1/line/uploaddwg.do?number='+number,
													method: "POST",
													waitMsg : '正在进行处理,请稍后...',
													waitTitle :'请稍后',
													success: function (form, action) {
														outerwin.close();
														innerwin.close();
													    var text=action.response.responseText;
													    var msg=text.replace("{success:true,msg:'","").replace("'}","");
														if(msg!=""){
															alert(msg);
														}else{
															alert("上传图纸成功");
														}
													},
													failure:function(){
													}
												});
										   }  
									   }],  
									   items: [
									   {
										 xtype:'form', 
										 enctype : 'multipart/form-data', // 把文件以二进制流的方式传递到服务器
										 fileUpload : true,
										 id:'uploadform',									 
										 items:[
											{    
												xtype: 'textfield',
												id:'uploadfield',
												fieldLabel: '上传文件',    
												name: 'uploadfile',    
												inputType: 'file',  
												regex:/\.([dD][wW][gG]){1}$/,
												regexText : '请选择dwg类型的文件',
												saTarget:'unber',
												anchor: '90%'     
											}
										 ]
									   }
									   ]
								   });  
									 innerwin.show();  
								}
							   }
							]
					 });
	                 outerwin.show();
				  }
			  },
			  failure : function(form, action) {
			    alert("请求失败");
		     }
         });
      }    
      
      function downloadDwg(number){
    	  Ext.Ajax.request({
			  disableCaching:false,
			  url : "/Windchill/ptc1/line/downloadDwg.do?number="+number,
			  success : function(response, opts) {
			  var url=JSON.parse(response.responseText).url;
			  var msg=JSON.parse(response.responseText).msg;
			  if(msg!=""){
				  window.open('http://'+url);
			  }else{
				  alert(msg);
			  }
			  },
			  failure : function(form, action) {
			    alert("请求失败");
		     }
         });
      }
	  function windowclose() {
			   window.close();
		}
	</script>

 
 <%@include file="/netmarkets/jsp/util/end.jspf"%>
