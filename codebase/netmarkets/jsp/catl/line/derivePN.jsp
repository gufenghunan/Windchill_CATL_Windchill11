 <%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
 
 <%@ include file="/netmarkets/jsp/components/beginWizard.jspf" %> 
 <%@ page language="Java" pageEncoding="UTF-8"%>
 <input type="hidden" name="oid" id="urla" value=""/>
 
 <!--<jca:wizard title="衍生PN创建"> 
	<jca:wizardStep action="selectParentPN" type="line"/>
 </jca:wizard>-->
 <style>
    .uploadbtn_cls button{
    height: 20px !important;
	}
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
	.detail{
	   background:url("/Windchill/netmarkets/images/details.gif") no-repeat;
	}
	.seepic{
	   background:url("/Windchill/netmarkets/images/doc_document.gif") no-repeat;
	}
	.tobig{
	   background:url("/Windchill/config/custom/egimg/tobig.png") no-repeat;
	   background-size:16px;
	   text-decoration:none !important;
	}
	.delete{
	   background:url("/Windchill/wtcore/images/part.gif") no-repeat;
	}
	
   </style>
	<script type="text/javascript">
	var containeroid='<%=request.getParameter("ContainerOid")%>';
	var folderoid='<%=request.getParameter("oid")%>';
	var website='<%=request.getScheme()+"://"+request.getServerName()%>';
	var winWidth=0,winHeight=0;
	var clinetype="",cmpn="",cconnectorcount=0;
	var leftdconnector="";
	var rightdconnector="";
	var updateoid="";
	var updatenumber="";
    var rt = Ext.data.Record.create([
              	{name: 'oid'},{name: 'number'},{name: 'name'},{name:'linetype'},{name:'lconnector'},{name:'rconnector'},{name:'ldconnector'},{name:'rdconnector'},{name:'maxcablesection'},{name:'cablecount'},{name:'undercablecount'},{name:'connectorcount'},{name:'rstgcolor'},
             ]);
    var rt1 = Ext.data.Record.create([
               	{name: 'oid'},{name: 'number'},{name: 'name'},{name:'L1'},{name:'L2'},{name:'L3'},{name:'mtag_content'},{name:'ldistance'},{name:'ltagbox'},{name:'rtagbox'},{name:'ltagdesc'},{name:'rtagdesc'},{name:'dtagdesc'},{name:'pointa'},{name:'pointb'},{name:'pointc'},{name:'pointd'},{name:'creator'},{name:'createtime'},{name:'checkinfo'}
              ]);
	var act0="<div id=\"stephdr\" class=\" x-panel stepHeader x-panel-noborder\"><div class=\"x-panel-bwrap\"><div class=\"x-panel-body x-panel-body-noheader x-panel-body-noborder\" id=\"ext-gen17\"></div></div><div id=\"stripWrapHolder\" class=\"stepPanel\"><div id=\"stripWrap\" class=\"x-header-strip-wrap\" style=\"left: 0px;\"><ul id=\"stripTabWrap\" class=\"header-strip\"><li class=\"wizardstepLinks first active enabled\" tabindex=\"0\"><div class=\" stepIcon-active\">1</div>填写母PN</li><li class=\"wizardstepLinks last enabled\" tabindex=\"0\"><div class=\" stepIcon-nonVisited\">2</div>生成衍生PN</li></ul></div></div></div>";
	var act1="<div id=\"stephdr\" class=\" x-panel stepHeader x-panel-noborder\"><div class=\"x-panel-bwrap\"><div class=\"x-panel-body x-panel-body-noheader x-panel-body-noborder\" id=\"ext-gen17\"></div></div><div id=\"stripWrapHolder\" class=\"stepPanel\"><div id=\"stripWrap\" class=\"x-header-strip-wrap\" style=\"left: 0px;\"><ul id=\"stripTabWrap\" class=\"header-strip\"><li class=\"wizardstepLinks first active enabled\" tabindex=\"0\"><div class=\" stepIcon-nonVisited\">1</div>填写母PN</li><li class=\"wizardstepLinks last active\" tabindex=\"0\"><div class=\" stepIcon-active\">2</div>生成衍生PN</li></ul></div></div></div>";
		 Ext.onReady(function() {
			  updatesize();
			  Ext.EventManager.onWindowResize(function(){
				  updatesize();
				 Ext.getCmp("ppngrid").setWidth(winWidth);
				 Ext.getCmp("ppngrid").setHeight(winHeight-300);
				 Ext.getCmp("gridtb").setWidth(winWidth);
				 Ext.getCmp("cpngrid").setWidth(winWidth*1.6);
				 Ext.getCmp("cpngrid").setHeight(winHeight-430);
				 Ext.getCmp("title").setWidth(winWidth);
				 Ext.getCmp("footerpanel").setWidth(winWidth*0.98);
				 Ext.getCmp("content_display").setHeight(winHeight-150);
				 Ext.getCmp("mpn_tab_panel").setHeight(winHeight-160);
			 });
					Ext.Ajax.request({
							  disableCaching:false,
							  url : "/Windchill/ptc1/line/getParentPNs.do",
							  success : function(response, opts) {
							  var data=JSON.parse(response.responseText);
							  var pnstore=new Ext.data.Store({
									autoDestroy: true,
									reader:new Ext.data.ArrayReader(
										{
											idIndex: 0  // 每条记录的id将会是第一个元素
										},
										rt
									),
									data: data
							  });	
							  var linetypearray=new Array();
							  linetypearray.push(["全部"]);
                             for(var j=0;j<data.length;j++){
								 if((linetypearray+"").indexOf(data[j][3])==-1){
									 var innerarray=new Array();
									 innerarray.push(data[j][3]);
									 linetypearray.push(innerarray);
								 }
							 }							  
							 var combo = new Ext.form.ComboBox({
								typeAhead: true,
								triggerAction: 'all',
								lazyRender:true,
								width:100,
								editable:false,
								mode: 'local',
								id:'linetypeval',
								style:'margin-left:5px',
								value:'全部',
								store: new Ext.data.ArrayStore({
									fields: [
										'value'
									],
									data:linetypearray
								}),
								valueField: 'value',
								displayField: 'value'
							});
							combo.on('select',function(combo,record,index){
					               filter(pnstore);
					       });
				        var grid = new Ext.grid.GridPanel({
						id:'ppngrid',
						store:pnstore,
						colModel: new Ext.grid.ColumnModel({
								columns: [
									{
									  header: 'PN', sortable: true, dataIndex: 'number',
									  renderer:function(value, cellmeta, record, rowIndex, columnIndex, store){
										  return "<img src=\""+website+"/Windchill/wtcore/images/part.gif\"/><span style=\"position:relative;top:-3px;left:2px\">"+value+"</span>";
									  }
									},
									{
									  header: '名称', dataIndex: 'name',sortable: true
									},
									{
									  header: '线束类型', dataIndex: 'linetype',sortable: true
									},
									{
									  header: '接插件左', dataIndex: 'lconnector',sortable: true
									},
									{
									  header: '接插件右', dataIndex: 'rconnector',sortable: true
									},
									{
									  header: '接插件左下', dataIndex: 'ldconnector',sortable: true
									},
									{
									  header: '接插件右下', dataIndex: 'rdconnector',sortable: true
									},
									{
									  header: '最大导线截面积', dataIndex: 'maxcablesection',sortable: true
									},
									{
									  header: '导线总根数', dataIndex: 'cablecount',sortable: true,
									  renderer:function(value, cellmeta, record, rowIndex, columnIndex, store){
											if(value==0){
												value='';
											}
											return value;
									  }
									},
									{
									  header: '导线下根数', dataIndex: 'undercablecount',sortable: true,
									  renderer:function(value, cellmeta, record, rowIndex, columnIndex, store){
										if(value==0){
											value='';
										}
										return value;
									  }
									},
									{
									  header: '接插件数目', dataIndex: 'connectorcount',sortable: true
									},
									{
									  header:'热缩套管颜色',dataIndex:'rstgcolor',sortable: true
									}
								],
							}),
							viewConfig: {
								forceFit: true
							},
							sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
							scrollable:true,
							width: winWidth,
							height: winHeight-300,
							listeners:{
								contextmenu:function(e){
									e.stopEvent();
									var contextMenu = new Ext.menu.Menu({
											width:150,
											margin:'0 0 10 0',
											items:[
												{
													text:'查看部件信息',
													iconCls:'detail',
													handler:function(){
													    var sel = Ext.getCmp("ppngrid").getSelectionModel().selections;
													    if(sel.length==0){
															 alert("请先选中");
														 }else{
															 var node=sel.items[0];
															 var url=website+"/Windchill/app/#ptc1/tcomp/infoPage?oid="+node.get('oid')+"&u8=1"
															 window.open(url, "_blank","resizable=yes,scrollbars=no,menubar=no,toolbar=no,location=no,status=yes,top=40,left=75,height="+(winHeight-80)+",width="+(winWidth-150));
														 }
													    
													}
												},
												{
													text:'查看图纸',
													iconCls:'seepic',
													handler:function(){
													    var sel = Ext.getCmp("ppngrid").getSelectionModel().selections;
													    if(sel.length==0){
													    	 alert("请先选中");
														 }else{
															 var node=sel.items[0];
														    displaypdf(node.get("number"));
														 }
													    
													}
												}
											]
										});
									contextMenu.showAt(e.getXY());
								},
								cellclick : function(gird,rowindex,colindex,e){
									var number=grid.store.getAt(rowindex).get('number');
									var oldconnectorcount=cconnectorcount;
									cconnectorcount=grid.store.getAt(rowindex).get('connectorcount');
									document.getElementById("current_ppn").innerHTML=number;
									var oldlinetype=clinetype;
									clinetype=grid.store.getAt(rowindex).get('linetype');
									cmpn=number;
									if(oldconnectorcount!=cconnectorcount){//改变接头数目
										resetcpnform();
									    if(cconnectorcount==2){
											  Ext.getCmp("combo5label").hide();
											  Ext.getCmp("combo5").hide();
											  Ext.getCmp("CATL_L1_label").hide();
											  Ext.getCmp("CATL_L1").hide();
											  Ext.getCmp("CATL_L2_label").hide();
											  Ext.getCmp("CATL_L2").hide();
											  Ext.getCmp("CATL_L3_label").hide();
											  Ext.getCmp("CATL_L3").hide();
											  Ext.getCmp("CATL_L").show();
										}else if(cconnectorcount==3){
											 Ext.getCmp("combo5label").show();
											 Ext.getCmp("combo5").show();
											  Ext.getCmp("CATL_L1_label").show();
											  Ext.getCmp("CATL_L1").show();
											  Ext.getCmp("CATL_L2_label").show();
											  Ext.getCmp("CATL_L2").show();
											  Ext.getCmp("CATL_L3_label").show();
											  Ext.getCmp("CATL_L3").show();
											  Ext.getCmp("CATL_L").hide();
										}
									}
									leftdconnector=grid.store.getAt(rowindex).get('ldconnector');
									rightdconnector=grid.store.getAt(rowindex).get('rdconnector');
								},
								celldblclick  :function(grid,rowindex,colindex,e){
									updatecpngridstore(cmpn);
									  if(cmpn==""){
										  alert("请选择母PN");
									  }else{
										  if(document.getElementById("errormsg").innerHTML==""){
											  setActiveTab(1);
											  Ext.getCmp("btn_next").setDisabled(true);
											  Ext.getCmp("btn_prev").setDisabled(false);
											  Ext.getCmp("btn_create").setDisabled(false);
											  Ext.getCmp("btn_update").setDisabled(true);
											  Ext.getCmp("currentpn").setValue("");
											  updateoid="";
											  Ext.getCmp("mpn_tab_panel").setActiveTab(1);
											  changexcjz();
											  toline_eg();
											  validatelinelength(Ext.getCmp("CATL_L").getValue());
										  }
										 
									  }
								}
							}
				       	});
						var tb = new Ext.Toolbar({
							renderTo:Ext.getBody(),
							id:'title',
							width:winWidth,
							height: 35,
							items: [
								{
									xtype:'displayfield',
									style:'position:relative;color:#636363;font-size:14px;left:2px;top:4px',
									value : '新建衍生PN'
								}
							]
						});
					    var tb1 = new Ext.Container({
								renderTo:Ext.getBody(),
								width:'100%',
								height:50,
								html:"<div id=\"newmpntab\">"+act0+"</div>"
						});	 
						var tb1 = new Ext.Container({
							renderTo:Ext.getBody(),
							width:'98%',
							id:'content_display',
							height:winHeight-150,
							style:'border:1px solid #c0c0c0;margin:0 auto;'
					   });	 
						var gridtb = new Ext.Toolbar({
							width:winWidth,
							cls:'gridtb_cls',
							id:'gridtb',
							style:'padding:7px 0px',
							height: 35,
							items: [
									{
										xtype : 'displayfield',
										style:'margin-left:5px',
										value : '线束类型:'
									},
									combo, 
									 {
										xtype : 'displayfield',
										style:'margin-left:15px',
										value : '接插件类型:'
									},
									 new Ext.form.ComboBox({
											typeAhead: true,
											width:100,
											triggerAction: 'all',
											editable:false,
											lazyRender:true,
											mode: 'local',
											id:'connectorcountval',
											style:'margin-left:5px',
											value:'两头线束母PN',
											store: new Ext.data.ArrayStore({
												fields: [
													'value'
												],
												data:[['全部'],['两头线束母PN'],['三头线束母PN']]
											}),
											listeners:{
												afterrender:function(combobox){
													filter(pnstore);
												},
												select:function(combo,record,index){
														   filter(pnstore);
												}
											},
											valueField: 'value',
											displayField: 'value'
									}),
									{
										xtype : 'displayfield',
										style:'margin-left:15px',
										value : '接插件左:'
									},
									{
										xtype : 'textfield',
										id:'lconnectorval',
										style:'margin-left:5px',
										enableKeyEvents:true,
										listeners:{
											keyup :function(field,e){
												filter(pnstore);
											}
										},
										width:100
									},
									{
										xtype : 'displayfield',
										style:'margin-left:10px',
										value : '接插件右:'
									},
									{
										xtype : 'textfield',
										id:'rconnectorval',
										style:'margin-left:5px',
										enableKeyEvents:true,
										listeners:{
											keyup :function(field,e){
												filter(pnstore);
											}
										},
										width:100
									},
									{
										xtype : 'displayfield',
										style:'margin-left:10px',
										value : '接插件左下:'
									},
									{
										xtype : 'textfield',
										id:'ldconnectorval',
										style:'margin-left:5px',
										enableKeyEvents:true,
										listeners:{
											keyup :function(field,e){
												filter(pnstore);
											}
										},
										width:100
									},
									{
										xtype : 'displayfield',
										style:'margin-left:10px',
										value : '接插件右下:'
									},
									{
										xtype : 'textfield',
										id:'rdconnectorval',
										style:'margin-left:5px',
										enableKeyEvents:true,
										listeners:{
											keyup :function(field,e){
												filter(pnstore);
											}
										},
										width:100
									},
									{
										xtype : 'displayfield',
										style:'margin-left:10px',
										value : '最大导线截面积:'
									},
									{
										xtype : 'textfield',
										id:'maxcablesectionval',
										style:'margin-left:5px',
										enableKeyEvents:true,
										listeners:{
											keyup :function(field,e){
												filter(pnstore);
											}
										},
										width:50
									},
									{
										xtype : 'displayfield',
										style:'margin-left:5px',
										value : 'mm2'
									}
							]
						});

						  var tabs = new Ext.TabPanel({
							    id:'mpn_tab_panel',
								renderTo:'content_display',
								activeTab: 0,
								headerCfg:{style:'display:none'},
								width:'99.5%',
								height:winHeight-160,
								items: [
								{
									title: '选择母PN',
									items:[
										{
											xtype: 'container',
											html:'<div style=\"padding:5px;padding-top:15px;height:20px;font-weight:bold\"><span>输入线束总成PN:</span>&nbsp;<input  onblur=\"validateasmpn();\" id=\"asmpn\" width=\"\" type=\"text\"/>&nbsp;<span style=\"color:red;font-weight:normal\" id=\"errormsg\"></span></div><div style=\"padding:5px;height:20px;font-weight:bold\"><span>*当前选中母PN&nbsp;&nbsp;:<span id=\"current_ppn\" style=\"\color:green;margin-left:5px;font-weight:bold"></span></span></div>'
										},
										{
											xtype: 'container',
											items:[
												{
													 xtype:'form', 
													 layout:'table',
													 enctype : 'multipart/form-data', // 把文件以二进制流的方式传递到服务器
													 fileUpload : true,
													 id:'upload_deriveform',									 
													 items:[
														{    
															xtype: 'displayfield',
															style:'width:100px',
															value:'批量创建衍生PN :'
														},
														{    
															xtype: 'textfield',
															allowBlank:false,
															width:200,
															id:'upload_derive',
															name: 'uploadfile',    
															inputType: 'file',  
															regex:/\.([xX][lL][sS]){1}$/,
															regexText : '请选择xls类型的文件',
															saTarget:'unber',
														},{
															xtype:'button',
															text:'上传',
															style:'margin-left:5px',
															cls:'uploadbtn_cls',
															handler:function(){
																var form=Ext.getCmp("upload_deriveform").getForm();
																var asmnumber=document.getElementById("asmpn").value;
																var errormsg=document.getElementById("errormsg").innerHTML;
																if(errormsg!=""){
																	alert(errormsg);
																}else{
																	var mk=new Ext.LoadMask(document.body,{msg:'正在创建衍生PN,请稍候',removeMask:true});
																     mk.show();
																	form.submit({
																		url: '/Windchill/ptc1/line/batchCreateOrUpdateChildPN.do',
																		params:{
																			type:'batchderivePN',
																			date:new Date(),
																			containeroid:containeroid,
																		    folderoid:folderoid,
																		    asmnumber:asmnumber
																		},
																		method: "POST",
																		success: function (form, action) {
																			mk.hide();
																			var text=action.response.responseText;
																		    var msg=text.replace("{success:true,msg:'","").replace("'}","");
																			if(msg!=""){
																				alert(msg);
																			}else{
																				alert("创建成功");
																			}
																		},
																		failure:function(){
																			alert("请求失败");
																			mk.hide();
																		}
																		
																	});
																}
																
															}
														}
													 ]
												}       
											]
										},
										{
											xtype: 'container',
											id:'toolbar_table1',
											style:'border:1px solid #EEE;overflow:hidden',
											items:[
												gridtb,
												grid
											]
										}
										
									]
									
								 },{
									title: '参数填写',
									items:[
									{
										xtype:'form',
										id:'cpnform',
										items:[
												{
													xtype: 'container',
													style:'position:absolute;right:0px;top:50px',
													width:300,
													html:'<div id="lineegpic"></div><div style="width:40px;position:absolute;right:0px"><a href="javascript:void(0);" onclick="toBigline_eg()" class="tobig">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a></div>'
												},
												{
													xtype: 'container',
													style:'margin-top:15px',
													width:'98%',
													layout:'table',
													height:50,
													items:[
													       {
																xtype : 'displayfield',
																style:'margin-left:10px',
																value : '*请输入线束长度(mm):'
														   },
														   {
																xtype : 'numberfield',
																id:'CATL_L',
																name:'CATL_L',
																width:100,
																minValue:1,
																listeners:{
																	blur :function(field,e){
																		validatelinelength(field.getValue());
																	}
																}
														   },
														   {
																xtype : 'container',
																html:'<span style=\"color:red;font-weight:normal\" id=\"lengtherrormsg\"></span>'
														   },
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
																width:100,
																minValue:1
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
																style:'margin-left:10px',
																value : '*客户线长基准:'
														   },
														   new Ext.form.ComboBox({
											                    typeAhead: true,
																triggerAction: 'all',
																editable:false,
																lazyRender:true,
																allowBlank:false,
																name:'CATL_Llbenchmark',
																id:'CATL_Llbenchmark',
																mode:'local',
																width:250,
																store: new Ext.data.ArrayStore({
																	fields: [
																		'value'
																	]
																}),
																value:'',
																valueField: 'value',
																displayField: 'value',
																listeners: {
																	afterrender : function(ComboBox){
																		if(clinetype=='高压线束'){
																			ComboBox.setValue('格兰头尾部到快插连接器尾部');
																		}else if(clinetype=='低压线束'){
																			ComboBox.setValue('低压连接器尾部到低压连接器尾部');
																		}else{
																			ComboBox.setValue('加热连接器尾部到加热连接器尾部');
																		}
																		
																	},
																	focus:function(field){
																		var combo=Ext.getCmp('CATL_Llbenchmark');
																		var m=combo.store;
																		var myData = '';
																		if(clinetype=='高压线束'){
																			myData = [
																				['格兰头尾部到快插连接器尾部'],['锁螺栓端子中心孔到锁螺栓端子中心孔'],['锁螺栓端子尾部到锁螺栓端子尾部'],['锁螺栓端子尾部到快插连接器尾部'],['锁螺栓端子中心孔到快插连接器尾部'],['格兰头尾部到格兰头尾部']
																			];
																		}else if(clinetype=='低压线束'){
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
															style:'margin-left:10px',
															value : '*左标签:'
														},
														 new Ext.form.ComboBox({
										                    typeAhead: true,
															triggerAction: 'all',
															editable:false,
															lazyRender:true,
															id:'combo1',
															name:'CATL_Ltag_Box',
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
																		  url:'/Windchill/ptc1/line/getTagBoxDesc.do',
																		  params:{coltype:'左箱体',linetype:clinetype},
																		  success : function(response, opts) {
																		  var data=JSON.parse(response.responseText);
																		  m.loadData(data);
																		  },
																		  failure : function(form, action) {
																		    alert("请求失败");
																		 }
																	 });
																	},
																collapse : function(combo) {
																    var val=Ext.getCmp('combo1').getValue();
																    	Ext.Ajax.request({
																		  disableCaching:false,
																		  url : "/Windchill/ptc1/line/getTagBoxDesc.do",
																		  params:{coltype:'左箱体描述',linetype:clinetype,key:'左箱体',value:val},
																		  success : function(response, opts) {
																		  var data=JSON.parse(response.responseText);
																		  Ext.getCmp('combo2').setValue(data[0]);
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
															triggerAction: 'all',
															editable:false,
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
																		  params:{coltype:'左箱体描述',linetype:clinetype,key:'左箱体',value:val},
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
															editable:false,
															lazyRender:true,
															id:'combo3',
															name:'CATL_Rtag_Box',
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
																		  params:{coltype:'右箱体',linetype:clinetype},
																		  success : function(response, opts) {
																		  var data=JSON.parse(response.responseText);
																		  m.loadData(data);
																		  },
																		  failure : function(form, action) {
																			alert("请求失败");
																		 }
																	 });
																	},
																collapse : function(combo) {
																	  var val=Ext.getCmp('combo3').getValue();
																      Ext.Ajax.request({
																	  disableCaching:false,
																	  params:{linetype:clinetype,coltype:'右箱体描述',key:'右箱体',value:val},
																	  url : "/Windchill/ptc1/line/getTagBoxDesc.do",
	                                                                  success : function(response, opts) {
																	  var data=JSON.parse(response.responseText);
																	  Ext.getCmp('combo4').setValue(data[0]);
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
															triggerAction: 'all',
															name:'CATL_Rtag_Desc',
															editable:false,
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
																	  params:{linetype:clinetype,coltype:'右箱体描述',key:'右箱体',value:val},
																	  url : "/Windchill/ptc1/line/getTagBoxDesc.do",
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
															id:'combo5label',
															style:'margin-left:65px',
															value : '*下标签:'
														},  
														new Ext.form.ComboBox({
										                    typeAhead: true,
										                    id:'combo5',
															triggerAction: 'all',
															editable:false,
															name:'CATL_Dtag_Desc',
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
																	  params:{coltype:'下标签',linetype:clinetype},
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
													width:'98%',
													layout:'table',
													height:50,
													items:[
														 {
															xtype : 'displayfield',
															style:'margin-left:10px',
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
															id:'CATL_PointA',
															width:100,
															minValue:1,
															enableKeyEvents:true,
															listeners:{
																keyup :function(field,e){
																	validatePointA();
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
															id:'CATL_PointB',
															minValue:1,
															width:100,
															enableKeyEvents:true,
															listeners:{
																keyup :function(field,e){
																	validatePointB();
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
															id:'CATL_PointC',
															minValue:1,
															disabled:true,
															width:100
														},
														{
															xtype : 'displayfield',
															style:'margin-left:10px',
															value : 'd:'
														},
														{
															xtype : 'numberfield',
															name:'CATL_PointD',
															id:'CATL_PointD',
															minValue:1,
															disabled:true,
															width:100
														}
													]
										    },
										    {
											    xtype: 'container',
												width:'98%',
												layout:'table',
												height:30,
												items:[
													 {
														xtype : 'displayfield',
														style:'margin-left:10px',
														value : '选中:'
													},
													{
														xtype : 'displayfield',
														style:'margin-left:10px;color:green;font-weight:bold',
														id:'currentpn',
														value : ''
													}
												]
									    }
										]
									},
								    //{
										//xtype:'container',
										//html :'<div style="padding:5px;height:20px;font-weight:bold"><span>衍生PN信息</span> <div style="float:right"><a href="javascript:void(0)" onclick="toline_eg()">示意图</a></div></div>'
									//},
								    {
										xtype: 'container',
										id:'toolbar_table2',
										style:'border:1px solid #EEE;overflow:scroll',
										items:[
									    new Ext.grid.GridPanel({
										id:'cpngrid',
										listeners:{
											contextmenu:function(e){
												e.stopEvent();
												var contextMenu = new Ext.menu.Menu({
														width:150,
														margin:'0 0 10 0',
														items:[
															{
																text:'删除选中衍生PN',
																style:'display:none',
																iconCls:'delete',
																handler:function(){
																	  var sel = Ext.getCmp("cpngrid").getSelectionModel().selections;
																	    if(sel.length==0){
																			 alert("请先选中");
																		 }else{
																			 var node=sel.items[0];
																			 Ext.Ajax.request({
																			     disableCaching:false,
																				  url : "/Windchill/ptc1/line/deletePN.do",
																				  params:{
																					     number:node.get('number')
																				  },
																				  success : function(response, opts) {
																					  var msg=JSON.parse(response.responseText).msg;
																					  if(msg==""){
																						  updatecpngridstore(cmpn);
																					  }else{
																						  alert(msg);
																					  }
																			  	  }
																	    	   });

																		 }
																	  
																}
															},
															{
																text:'查看部件信息',
																iconCls:'detail',
																handler:function(){
																    var sel = Ext.getCmp("cpngrid").getSelectionModel().selections;
																    if(sel.length==0){
																		 alert("请先选中");
																	 }else{
																		 var node=sel.items[0];
																		 var url=website+"/Windchill/app/#ptc1/tcomp/infoPage?oid="+node.get('oid')+"&u8=1"
																		 window.open(url, "_blank","resizable=yes,scrollbars=no,menubar=no,toolbar=no,location=no,status=yes,top=40,left=75,height="+(winHeight-80)+",width="+(winWidth-150));
																	 }
																    
																}
															},
															{
																text:'查看图纸',
																iconCls:'seepic',
																handler:function(){
																    var sel = Ext.getCmp("cpngrid").getSelectionModel().selections;
																    if(sel.length==0){
																		 alert("请先选中");
																	 }else{
																		 var node=sel.items[0];
																	    displaypdf(node.get("number"));
																	 }
																    
																}
															}
														]
													});
												contextMenu.showAt(e.getXY());
											},
											celldblclick  :function(grid,rowindex,colindex,e){
												var checkinfo=grid.store.getAt(rowindex).get('checkinfo');
												if(checkinfo.indexOf('由')>-1){
													alert(checkinfo+",不允许修改");
													return;
												}
												var l1=grid.store.getAt(rowindex).get('L1');
												var l2=grid.store.getAt(rowindex).get('L2');
												var l3=grid.store.getAt(rowindex).get('L3');
												var ldistance=grid.store.getAt(rowindex).get('ldistance');
												var ltagbox=grid.store.getAt(rowindex).get('ltagbox');
												var rtagbox=grid.store.getAt(rowindex).get('rtagbox');
												var ltagdesc=grid.store.getAt(rowindex).get('ltagdesc');
												var rtagdesc=grid.store.getAt(rowindex).get('rtagdesc');
												var dtagdesc=grid.store.getAt(rowindex).get('dtagdesc');
												var pointa=grid.store.getAt(rowindex).get('pointa');
												var pointb=grid.store.getAt(rowindex).get('pointb');
												var pointc=grid.store.getAt(rowindex).get('pointc');
												var pointd=grid.store.getAt(rowindex).get('pointd');
												var number=grid.store.getAt(rowindex).get('number');
												var oid=grid.store.getAt(rowindex).get('oid');
												setformval('CATL_L',l1);
												setformval('CATL_L1',l1);
												setformval('CATL_L2',l2);
												setformval('CATL_L3',l3);
												setformval('CATL_Llbenchmark',ldistance);
												setformval('combo1',ltagbox);
												setformval('combo3',rtagbox);
												setformval('combo2',ltagdesc);
												setformval('combo4',rtagdesc);
												setformval('combo5',dtagdesc);
												setformval('CATL_PointA',pointa);
												setformval('CATL_PointB',pointb);
												setformval('CATL_PointC',pointc);
												setformval('CATL_PointD',pointd);
												Ext.getCmp("currentpn").setValue(number);
												Ext.getCmp("btn_update").setDisabled(false);
												updateoid=oid;
												validatePointA();
												validatePointB();
											}
										},
										store:new Ext.data.Store({
												autoDestroy: true,
												reader:new Ext.data.ArrayReader(
													{
														idIndex: 0  // 每条记录的id将会是第一个元素
													},
													rt1
											     ),
											     data: []
										     }),
										colModel: new Ext.grid.ColumnModel({
											columns: [
                                                new Ext.grid.RowNumberer(), 
												{
												  header: 'PN', sortable: true, dataIndex: 'number',
												  renderer:function(value, cellmeta, record, rowIndex, columnIndex, store){
													  var checkinfo=record.get("checkinfo");
													  if(checkinfo.indexOf("由")>-1){
														  return "<img src=\""+website+"/Windchill/com/ptc/core/htmlcomp/images/checkedout_byother9x9.gif\"/><span ext:qtip=\""+checkinfo+"\" style=\"position:relative;top:-3px;left:2px\">"+value+"</span>";
													  }else if(checkinfo=="已检出"){
														  return "<img src=\""+website+"/Windchill/com/ptc/core/htmlcomp/images/checkedout_byyou9x9.gif\"/><span ext:qtip=\"已检出到您那\" style=\"position:relative;top:-3px;left:2px\">"+value+"</span>";
													  }
													  return "<img src=\""+website+"/Windchill/wtcore/images/part.gif\"/><span style=\"position:relative;top:-3px;left:2px\">"+value+"</span>";
												  }
												},
												{
												  header: '主标签内容', dataIndex: 'mtag_content',sortable: true
												},
												{
												  header: '名称', dataIndex: 'name',sortable: true
												},
												{
												  header: 'L1', dataIndex: 'L1',sortable: true
												},
												{
												  header: 'L2', dataIndex: 'L2',sortable: true
												},
												{
												  header: 'L3', dataIndex: 'L3',sortable: true
												},
												{
												  header: '客户线长基准', dataIndex: 'ldistance',sortable: true
												},
												{
												  header: '左标签箱体', dataIndex: 'ltagbox',sortable: true
												},
												{
												  header: '左标签描述', dataIndex: 'ltagdesc',sortable: true
												},
												{
												  header: '右标签箱体', dataIndex: 'rtagbox',sortable: true
												},
												{
												  header: '右标签描述', dataIndex: 'rtagdesc',sortable: true
												},
												{
												  header: '下标签描述', dataIndex: 'dtagdesc',sortable: true
												},
												{
												  header: '定位点a', dataIndex: 'pointa',sortable: true
												},
												{
											      header: '定位点b', dataIndex: 'pointb',sortable: true
												},
												{
												  header: '定位点c', dataIndex: 'pointc',sortable: true
												},
												{
												  header: '定位点d', dataIndex: 'pointd',sortable: true
												},
												{
												  header: '创建者', dataIndex: 'creator',sortable: true
												},
												{
												  header: '创建时间', dataIndex: 'createtime',sortable: true
												}
											],
										}),
										viewConfig: {
											forceFit: true
										},
										sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
										scrollable:true,
										width: winWidth*1.6,
										height: winHeight-430
								     })
										]
									}
								   
									]
								}]
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
													disabled:true,
													id:'btn_prev',
													text : '上一步(B)',
													handler:function(btn){
														  setActiveTab(0);
														  Ext.getCmp("btn_next").setDisabled(false);
														  Ext.getCmp("btn_prev").setDisabled(true);
														  Ext.getCmp("btn_create").setDisabled(true);
														  Ext.getCmp("mpn_tab_panel").setActiveTab(0);
													
												    }
												},
												{
													xtype : 'button',
													style:'margin-left:10px',
													id:'btn_next',
													text : '下一步(N)',
													handler:function(btn){													  
														  updatecpngridstore(cmpn);
														  if(cmpn==""){
															  alert("请选择母PN");
														  }else{
															  setActiveTab(1);
															  btn.setDisabled(true);
															  Ext.getCmp("btn_prev").setDisabled(false);
															  Ext.getCmp("btn_create").setDisabled(false);
															  Ext.getCmp("btn_update").setDisabled(true);
															  Ext.getCmp("currentpn").setValue("");
															  updateoid="";
															  Ext.getCmp("mpn_tab_panel").setActiveTab(1);
															  changexcjz();
															  toline_eg();
															  validatelinelength(Ext.getCmp("CATL_L").getValue());
														  }
														  
													
												    }
												},
												{
													xtype : 'button',
													style:'margin-left:10px',
													disabled:true,
													id:'btn_create',
													text : '创建(F)',
													handler:function(){
														var flag=validateform();
														if(flag){
															createchildpn();
														}
														
												    }
												},
												{
													xtype : 'button',
													style:'margin-left:10px',
													disabled:true,
													id:'btn_update',
													text : '更新(U)',
													handler:function(){
														if(updateoid!=""){
															updatechildpn();
														}else{
															alert("请先选择要更新的衍生PN");
															  Ext.getCmp("btn_update").setDisabled(true);
															  Ext.getCmp("currentpn").setValue("");
															  updateoid="";
														}
														
												    }
												},
												{
													xtype : 'button',
													style:'margin-left:10px',
													id:'btn_close',
													text : '关闭(C)',
													handler:function(){
														window.close();
												    }
												}
											]
							        }
									
			                    ] 
						   });	 
							}
						 });
					
				
			
		});
      function relativePN(parentnumber,childnumber){
    	  Ext.Ajax.request({
			     disableCaching:false,
				  url : "/Windchill/ptc1/line/relativePN.do",
				  params:{
					      parentnumber:parentnumber,
					      childnumber:childnumber
				  },
				  success : function(response, opts) {
					  var msg=JSON.parse(response.responseText).msg;
					  if(msg==""){
							  resetcpnform();
					  }else{
						 alert(msg);
					  }
			  	  }
	    	   });
      }
      function changexcjz(){
    	  var l=Ext.getCmp("CATL_L").getValue();
    	  var l1=Ext.getCmp("CATL_L1").getValue();
    	  if((cconnectorcount==2&&l=="")||(cconnectorcount==3&&l1=="")){
    		  if(clinetype=='高压线束'){
  				Ext.getCmp("CATL_Llbenchmark").setValue('格兰头尾部到快插连接器尾部');
  			}else if(clinetype=='低压线束'){
  				Ext.getCmp("CATL_Llbenchmark").setValue('低压连接器尾部到低压连接器尾部');
  			}else{
  				Ext.getCmp("CATL_Llbenchmark").setValue('加热连接器尾部到加热连接器尾部');
  			}
    	  }
    	
      }
      function createchildpn(){
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
						  var mk=new Ext.LoadMask(document.body,{msg:'正在生成衍生PN，请稍后',removeMask:true});
						  mk.show();
						  var asmnumber=document.getElementById("asmpn").value;
						  Ext.Ajax.request({
						      disableCaching:false,
							  url : "/Windchill/ptc1/line/createChildPN.do",
							  params:{
								      values:JSON.stringify(m),
								      number:cmpn,
								      containeroid:containeroid,
								      folderoid:folderoid,
								      asmnumber:asmnumber
							  },
							  success : function(resp, opts) {
								      mk.hide();
							          var val=document.getElementById("asmpn").value;
					        		  var createmsg=JSON.parse(resp.responseText).msg;
						        		  if(createmsg==""){
						        			  var newnum=JSON.parse(resp.responseText).newnum;
						        			  if(val!=""){
						        				  relativePN(val,newnum);
						        			  }else{
						        				  resetcpnform();
						        			  }
						        			  displayChildPNPdf(newnum);
						        		  }else{
						        			  alert(createmsg);
						        		  }
								  
						  	  },
						  	  failure:function(){
						  		       mk.hide();
						  	  }
				    	   });
					  }else{
						  Ext.Msg.confirm('系统提示','系统中已存在完全相同的线束“'+msg+'”，是否使用现有线束？',
					      function(btn){
					        if(btn=='yes'){
					        	var val=document.getElementById("asmpn").value;
					        	if(val!=""){
					        		var childnumber=msg;
					        		var parentnumber=val;
					        		relativePN(parentnumber,childnumber);
					        	}else{
					        		alert("线束总成PN未填写");
					        	}
					        	
					        }
					      },this);
						  
					  }
						 
			  	  }
	    	   });
      }
      function resetcpnform(){
    	  if(Ext.getCmp("cpnform").getForm().getEl()!=undefined){
    		  Ext.getCmp("cpnform").getForm().getEl().dom.reset();
    		  document.getElementById("lengtherrormsg").innerHTML="";
    		  updatecpngridstore(cmpn);
		      Ext.getCmp("btn_update").setDisabled(true);
		      Ext.getCmp("currentpn").setValue("");
		      updateoid="";
		      changexcjz();
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
						   closable: true,  
						   draggable: true,  
						   resizable:false,
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
									   resizable:false,
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
	  function displaypdf(number){
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
				   var win=new Ext.Window({  
					       resizable:false,
						   width: winWidth-150,  
						   height: winHeight-100,  
						   closable: true,  
						   draggable: true,  
						   style:'padding:10px',
						   closeAction: "hide",
						   items:[
							   {
								 xtype:'container',
								 id:'paramsegpic',
								 html:'<iframe width=\"'+(winWidth-200)+'\" height=\"'+(winHeight-150)+'\" src=\"http://'+url+'\"> </iframe>'
							   }
							]
					 });
	                 win.show();
				  }
			  },
			  failure : function(form, action) {
			    alert("请求失败");
		     }
         });
		  // var pdf=website+"/Windchill/config/catl/download/"+number+".pdf";
		  
      }
	  
	  function filter(pnstore){
		  pnstore.clearFilter();
		  var linetypeval=Ext.getCmp('linetypeval').getValue();
		  var lconnectorval=Ext.getCmp('lconnectorval').getValue();
		  var rconnectorval=Ext.getCmp('rconnectorval').getValue();
		  var ldconnectorval=Ext.getCmp('ldconnectorval').getValue();
		  var rdconnectorval=Ext.getCmp('rdconnectorval').getValue();
		  var maxcablesectionval=Ext.getCmp('maxcablesectionval').getValue();
		  var connectorcountval=Ext.getCmp('connectorcountval').getValue();
		  var flag1,flag2,flag3,flag4,flag5,flag6;
			pnstore.filterBy(function(record,id){
			var rec1=record.get('linetype');
			var rec2=record.get('lconnector');
			var rec3=record.get('rconnector');
			var rec4=record.get('ldconnector');
			var rec5=record.get('rdconnector');
			var rec6=record.get('maxcablesection')+"";
			var rec7=record.get('connectorcount')+"";
			var flag1=(rec1==linetypeval);
			if(linetypeval=="全部"){
				flag1=true;
			}
		    var flag2=(rec2.indexOf(lconnectorval)>-1);
			if(lconnectorval==undefined){
				flag2=true;
			}
			var flag3=(rec3.indexOf(rconnectorval)>-1);
			if(rconnectorval==undefined){
				flag3=true;
			}
			var flag4=(rec4.indexOf(ldconnectorval)>-1);
			if(ldconnectorval==undefined){
				flag4=true;
			}
			var flag5=(rec5.indexOf(rdconnectorval)>-1);
			if(rdconnectorval==undefined){
				flag5=true;
			}
			var flag6=(rec6.indexOf(maxcablesectionval)>-1);
			if(maxcablesectionval==undefined){
				flag6=true;
			}
			var flag7=((connectorcountval=='两头线束母PN'&&rec7=='2')||(connectorcountval=='三头线束母PN'&&rec7=='3'));
			if(connectorcountval=="全部"){
				flag7=true;
			}
		   // var f
				 if(linetypeval=="全部"&&connectorcountval=="全部"&&lconnectorval==""&&rconnectorval==""&&ldconnectorval==""&&rdconnectorval==""&&maxcablesectionval==""){
					 return true;
				 }else if(flag1&&flag2&&flag3&&flag4&&flag5&&flag6&&flag7){
				 return true;
				 }else{
				 return false;
				 }
			});
	  }
	  function validateasmpn(){
		  var val=document.getElementById("asmpn").value;
		  if(val!=""){
			  	  Ext.Ajax.request({
					  disableCaching:false,
					  url : "/Windchill/ptc1/line/getAsmPN.do?number="+val,
					  success : function(response, opts) {
					  var msg=JSON.parse(response.responseText).msg;
					  if(msg==""){
						  Ext.getCmp("btn_next").setDisabled(false);
					  }else{
						  Ext.getCmp("btn_next").setDisabled(true);
					  }
					   document.getElementById("errormsg").innerHTML=msg;
					  }
		         });
		  }else{
			  Ext.getCmp("btn_next").setDisabled(false);
			  document.getElementById("errormsg").innerHTML="";
		  }
	
	  }
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
	  function setActiveTab(index){
		  var tab=eval('act'+index);
		  document.getElementById("newmpntab").innerHTML=tab;
		  
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
		    	   //alert("输入数值非法");
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
	    	  //alert("输入数值非法");
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
	    	   //alert("输入数值非法");
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
	  
	  function updatecpngridstore(number){
		  var store=Ext.getCmp("cpngrid").store;
		  store.removeAll();
		  Ext.Ajax.request({
			  disableCaching:false,
			  url : "/Windchill/ptc1/line/getChildPNs.do?number="+number,
			  success : function(response, opts) {
				  var data=JSON.parse(response.responseText);
				  store.loadData(data);
			  }
		  });
	  }
	  
	  function windowclose() {
			   window.close();
	  }
	  //定位点至少填写一个 其他属性必填
	  function validateform(){
		  var m= Ext.getCmp("cpnform").getForm().getValues();
		  var combo1=m.CATL_Ltag_Box;
		  var combo2=m.CATL_Ltag_Desc;
		  var combo3=m.CATL_Rtag_Box;
		  var combo4=m.CATL_Rtag_Desc;
		  if(combo1==""||combo2==""||combo3==""||combo4==""){
			  alert("标签填写不能为空");
			  return false;
		  }
		  if(combo1==combo3){
			  alert("左箱体不能和右箱体选择一种电箱");
			  return false;
		  }
		 // var p1=m.CATL_PointA;
		 // var p2=m.CATL_PointB;
		 // if(p1==""&&p2==""){
		 //	  alert("定位点不能为空");
		 //	  return false;
		 //}
		  if(cconnectorcount==2){
			  var l=m.CATL_L;
			  if(l==""){
				  alert("线束长度不能为空");
				  return false;
			  }
			  var msg=document.getElementById("lengtherrormsg").innerHTML;
			  if(msg!=""){
				  alert("线束长度填写错误");
				  return false;
			  }
			  
		  }else{
			  var l1=m.CATL_L1;
			  var l2=m.CATL_L2;
			  var l3=m.CATL_L3;
			  if(l1==""||l2==""||l3==""){
				  alert("线束长度不能为空");
				  return false;
			  }
			  var combo5=m.CATL_Dtag_Desc;
			  if(combo5==""){
				 alert("标签填写不能为空");
				  return false;
			  }
		  }
		  return true;
	  }
	  
	  function setformval(id,val){
		  if(val!=''&&val!=null){
			  Ext.getCmp(id).setValue(val.replace(",",""));
			  if(id=="CATL_PointC"||id=="CATL_PointD"){
				  Ext.getCmp(id).setDisabled(false);
			  }
		  }else{
			  Ext.getCmp(id).setValue("");
			  if(id=="CATL_PointC"||id=="CATL_PointD"){
				  Ext.getCmp(id).setDisabled(true);
			  }
		  }
	  }
	  function validatePointB(){
		  var val=Ext.getCmp("CATL_PointB").getValue();;
			if(val!=""){
				Ext.getCmp("CATL_PointD").setDisabled(false);
			}else{
				Ext.getCmp("CATL_PointD").setDisabled(true);
				Ext.getCmp("CATL_PointD").setValue("");
			}
	  }
	  
	  function validatePointA(){
		  var val=Ext.getCmp("CATL_PointA").getValue();
			if(val!=""){
				Ext.getCmp("CATL_PointC").setDisabled(false);
			}else{
				Ext.getCmp("CATL_PointC").setDisabled(true);
				Ext.getCmp("CATL_PointC").setValue("");
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
							  var asmnumber=document.getElementById("asmpn").value;
							  Ext.Ajax.request({
							      disableCaching:false,
								  url : "/Windchill/ptc1/line/updateChildPN.do",
								  params:{
									      values:JSON.stringify(m),
									      oid:updateoid,
									      asmnumber:asmnumber
								  },
								  success : function(resp, opts) {
									      mk.hide();
						        		  var msg=JSON.parse(resp.responseText).msg;
						        		  var newnum=JSON.parse(resp.responseText).newnum;
							        		  if(msg==""){
							        			  displayChildPNPdf(newnum);
							        			  resetcpnform();
							        		  }else{
							        			  alert(msg);
							        		  }
									  
							  	  },
							  	  failure:function(){
							  		       mk.hide();
							  	  }
					    	   });
							  
						  }else{
							  Ext.Msg.confirm('系统提示','系统中已存在完全相同的线束“'+msg+'”，是否使用现有线束？',
						      function(btn){
						        if(btn=='yes'){
						        	var val=document.getElementById("asmpn").value;
						        	if(val!=""){
						        		var childnumber=msg;
						        		var parentnumber=val;
						        		relativePN(parentnumber,childnumber);
						        		resetcpnform();
						        	}
						        	
						        }
						      },this);
							  
						  }
							 
				  	  }
		    	   });
	   }
	  function toline_eg(){
		  document.getElementById("lineegpic").innerHTML='<img src=\"'+website+'/Windchill/config/custom/egimg/'+cconnectorcount+'头线束接插件示意图.png?t=1\" width=\"100%\"/>';
		  if(leftdconnector!=""){
			  document.getElementById("lineegpic").innerHTML='<img src=\"'+website+'/Windchill/config/custom/egimg/'+cconnectorcount+'头线束接插件左下示意图.png?t=1\" width=\"100%\"/>';
		  }else if(rightdconnector!=""){
			  document.getElementById("lineegpic").innerHTML='<img src=\"'+website+'/Windchill/config/custom/egimg/'+cconnectorcount+'头线束接插件右下示意图.png?t=1\" width=\"100%\"/>';
		  }
	 }
	  
	  function toBigline_eg(){
		  if(cconnectorcount=='2'){
			  height=220;
		  }else{
			  height=500;
		  }
		  var win=new Ext.Window({  
					   width: 900,  
					   closable: true, 
					   resizable:false,
					   height:height,
					   draggable: true,  
					   style:'padding:10px',
					   closeAction: "hide",
					   items:[
					   {
						 xtype:'container',
						 id:'linebigegpic',
						 html:''
					   }
					   ]
					 });
		  win.show();
		  document.getElementById("linebigegpic").innerHTML='<img src=\"'+website+'/Windchill/config/custom/egimg/'+cconnectorcount+'头线束接插件示意图.png?t=1\" width=\"100%\"/>';
		  if(leftdconnector!=""){
			  document.getElementById("linebigegpic").innerHTML='<img src=\"'+website+'/Windchill/config/custom/egimg/'+cconnectorcount+'头线束接插件左下示意图.png?t=1\" width=\"100%\"/>';
		  }else if(rightdconnector!=""){
			  document.getElementById("linebigegpic").innerHTML='<img src=\"'+website+'/Windchill/config/custom/egimg/'+cconnectorcount+'头线束接插件右下示意图.png?t=1\" width=\"100%\"/>';
		  }
	 }
	</script>

 
 <%@include file="/netmarkets/jsp/util/end.jspf"%>
