 <%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
 
 <%@ include file="/netmarkets/jsp/components/beginWizard.jspf" %> 
 <%@ page language="Java" pageEncoding="UTF-8"%>

 <head>
 <style>
	 .x-tab-panel-header {
		border-color: #FFF;
	}
	.x-combo-list-item{
	height:14px;
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
	.grid_cls .x-grid3-row td.x-grid3-cell, .x-grid3-summary-row td.x-grid3-cell{
	 line-height:30px !important;
	}
	.x-editor {
    padding: 10px 0px;
   }
   </style>
	<script type="text/javascript">
	var website='<%=request.getScheme()+"://"+request.getServerName()%>';
	var containerOid='<%=request.getParameter("ContainerOid")%>';
	var folderOid='<%=request.getParameter("oid")%>';
	var winWidth=0,winHeight=0;
	var act0="<div style=\"position:relative;top:2px;left:1%\"><ul id=\"stripTabWrap\" class=\"header-strip\"><li class=\"wizardstepLinks first active enabled\" tabindex=\"0\"><div class=\" stepIcon-active\">1</div>定义部件</li><li class=\"wizardstepLinks last enabled\" tabindex=\"0\"><div class=\" stepIcon-nonVisited\">2</div>设置标识属性</li></ul></div>";
	var act1="<div style=\"position:relative;top:2px;left:1%\"><ul id=\"stripTabWrap\" class=\"header-strip\"><li class=\"wizardstepLinks first enabled\" tabindex=\"0\"><div class=\" stepIcon-nonVisited\">1</div>定义部件</li><li class=\"wizardstepLinks last active\" tabindex=\"0\"><div class=\" stepIcon-active\">2</div>设置标识属性</li></ul></div>";
	var clfurl=website+"/Windchill/ptc1/csm/setClassificationAttributesForMultiPart?wizardType=multiPart&ContainerOid="+containerOid+"&u8=1&unique_page_number=58456474070365_3&AjaxEnabled=component&wizardActionClass=com.ptc.windchill.csm.client.forms.ClassificationAttributeForNewMultiPartFormProcessor&wizardActionMethod=execute&tableID=table__multiPartWizAttributesTableDescriptor_TABLE&actionName=setClassificationAttributesForMultiPart&portlet=poppedup&context=part%24setAttributesWizStepForCreateMultiPart%24"+folderOid+"%24&oid="+folderOid+"&wizType=multiPart";
    Ext.onReady(function() {
		updatesize();
		Ext.QuickTips.init();
		 Ext.EventManager.onWindowResize(function(){
			 updatesize();
			 Ext.getCmp("footerpanel").setWidth(winWidth*0.98);
			 Ext.getCmp("content_display").setHeight(winHeight-150);
			 Ext.getCmp("multipart_panel").setHeight(winHeight-160);
			 Ext.getCmp("create_grid").setWidth(winWidth*0.98-30);
			 Ext.getCmp("create_grid").setHeight(winHeight-250);
			 Ext.getCmp("gridtb").setWidth(winWidth*0.98-30);
		 });
		var title = new Ext.Toolbar({
			renderTo:'multiPart',
			id:'title',
			width:'100%',
			height: 35,
			items: [
				{
					xtype:'displayfield',
					style:'position:relative;left:2px;color:#636363;font-size:14px;top:4px',
					value : '新建多个部件'
				}
			]
		});
		  var store = new Ext.data.ArrayStore({
				fields: ['id','name','feature', 'description', 'oldnum','unit','source','platform','iscustomer','openMould','forcecreate','clfname','clf','attachname','edithtml'],
				idIndex: 0 
			});
		  var recdata1=[0,'','','','','','','','否','','否','','','',''];
		  var recdata2=[1,'','','','','','','','否','','否','','','',''];
		  var recdata3=[2,'','','','','','','','否','','否','','','',''];
		  var recdata4=[3,'','','','','','','','否','','否','','','',''];
		  var recdata5=[4,'','','','','','','','否','','否','','','',''];
		  var myData = [recdata1,recdata2,recdata3,recdata4,recdata5];
		   store.loadData(myData);

			var gridtb = new Ext.Toolbar({
				width: winWidth*0.98-30,
				id:'gridtb',
				height: 30,
				items: [
					{  
		                xtype:'button',
						icon:'netmarkets/images/insert_row_below.gif',
						tooltip:'添加一行',
			            handler: function(){
			            	 var grid=Ext.getCmp("create_grid");
			            	 var store=grid.store;
			            	 var modelid=new Date().getTime();
			            	 var defaultData = {id:store.getCount(),name:'',feature:'',description:'',oldnum:'',unit:'',source:'',platform:'',iscustomer:'否',openMould:'',forcecreate:'否',clfname:'',clf:'',attachname:'',edithtml:''};
			            	 var p = new store.recordType(defaultData, store.getCount()); // 创建新记录
			            	  console.log(store.getCount());
			                 store.insert(store.getCount(), p); // 向store中插入一条新记录(另请参见add)
			               
			            }  
			        },{  
			        	xtype:'button',
			        	style:'margin-left:10px',
			        	tooltip:'移除选中行',
						icon:'netmarkets/images/row_select_remove.gif',
			            handler: function(){  
			            	var grid=Ext.getCmp("create_grid");
							removesel(grid);
							
			            }  
			        },{  
			        	xtype:'button',
			        	style:'margin-left:10px;',
			        	tooltip:'批量选择分类',
						icon:'netmarkets/images/classify.gif',
			            handler: function(){  
			            	selectclf();
			            }  
			        },{  
			        	xtype:'button',
			        	tooltip:'批量编辑属性',
			        	style:'margin-left:10px;',
						icon:'netmarkets/images/edit.gif',
			            handler: function(){  
			            	showBatchEditAttrWindow();
			            }  
			        }
			    ]
			});
			
		  var grid = new Ext.grid.EditorGridPanel({  
			    cls:'grid_cls',
		        store: store,  
		        id:'create_grid',
		        scrollable:true,
		        clicksToEdit:1,
		        region:'center',  
		        style:'border:1px solid #EEE;overflow:hidden',
		    	width: winWidth*0.98-30,
				height: winHeight-250,
		        margins: '0 5 5 5',  
		        viewConfig: {
			    	forceFit: true
				},
				listeners:{
					click:function(){
						var grid=Ext.getCmp("create_grid");
						var sel=grid.getSelectionModel().selections;
					}
				},
				sm: new Ext.grid.CheckboxSelectionModel(),
		    	colModel: new Ext.grid.ColumnModel({
			       columns: [  
                    new Ext.grid.CheckboxSelectionModel (),
			       // new Ext.grid.RowNumberer(),  
			        {  
			            header: '名称*',  
			            dataIndex: 'name',
			            renderer:function(value, metaData, record, rowIndex, colIndex,store, gridview){
			                record.modified=false;
			                var newValue = getDefaultName(record);
			               // alert('newValue_'+newValue+'_value_'+value);
				            if(newValue !="" && value != newValue){
				              	 value= newValue;
				            }
				            record.set('name',value);
			                return "<input type=\"text\" value=\""+value+"\"/ style=\"margin-top:7px;border:1px solid #d1d3d4;width:99%;height:15px\">";
			           },
			            editor:new Ext.form.ComboBox({
			            	allowBlank:false,
			            	editable:false,
			            	width: 100,
				            sortable: true,  
			            	blankText:'必填项',
			            	mode: 'local',
			            	listeners:{
			            		focus:function(combo){
			            			loadNameStore(combo);
			            			
			            		}
			            	},
							store: new Ext.data.ArrayStore({
								fields: [
									'value'
								],
								data:[],
							}),
							valueField: 'value',
							displayField: 'value'
			            	
			            }),
			            
			        },
			        {  
			            header: '特征',  
			            dataIndex: 'feature',
			            renderer:function(value, metaData, record, rowIndex, colIndex,store, gridview){
			                record.modified=false;
			                return "<input type=\"text\" value=\""+value+"\"/ style=\"margin-top:7px;border:1px solid #d1d3d4;width:99%;height:15px\">";
			           },
			            editor:new Ext.form.TextField({
			            }),
			            width: 100,
			            sortable: true,  
			        },
			        {  
			            header: '默认单位*',  
			            dataIndex: 'unit',sortable: true, 
			            renderer:function(value, metaData, record, rowIndex, colIndex,store, gridview){
			                record.modified=false;
			                var newValue = getDefaultUnit(record);
			                if(value =="" && value != newValue){
			                	 value= newValue;
			                }
			                record.set('unit',value);
			                return "<input type=\"text\" value=\""+value+"\"/ style=\"margin-top:7px;border:1px solid #d1d3d4;width:99%;height:15px\">";
			           },
			            editor:new Ext.form.ComboBox({
							allowBlank:false,
							editable:false,
							typeAhead: true,
							triggerAction: 'all',
							lazyRender:true,
							mode: 'local',
							listeners:{
			            		focus:function(combo){
			            			loadUnitStore(combo);
			            		}
			            	},
							store: new Ext.data.ArrayStore({
								fields: [
									'value'
								],
								data:[]
							}),
							valueField: 'value',
							displayField: 'value'
						})
			        },
			        {  
			            header: '采购类型*',  
			            dataIndex: 'source',sortable: true, 
			            renderer:function(value, metaData, record, rowIndex, colIndex,store, gridview){
			                record.modified=false;
			                var newValue = getDefaultSource(record);
			               // alert("0_"+newValue);
			                if(newValue !="" && value != newValue){
			                	 value= newValue;
			                	// alert("1_"+value);
			                }
			                record.set('source',value);
			                return "<input type=\"text\" value=\""+value+"\"/ style=\"margin-top:7px;border:1px solid #d1d3d4;width:99%;height:15px\">";
			           },
			            editor:new Ext.form.ComboBox({
							allowBlank:false,
							editable:false,
							typeAhead: true,
							triggerAction: 'all',
							lazyRender:true,
							mode: 'local',
							listeners:{
			            		focus:function(combo){
			            			loadSourceStore(combo);
			            		}
			            	},
							store: new Ext.data.ArrayStore({
								fields: [
									'value'
								],
								data:[]
							}),
							valueField: 'value',
							displayField: 'value'
						})
			        },
			        {  
			            header: '产品线标识*',  
			            dataIndex: 'platform',sortable: true, 
			            renderer:function(value, metaData, record, rowIndex, colIndex,store, gridview){
			                return "<input type=\"text\" value=\""+value+"\"/ style=\"margin-top:7px;border:1px solid #d1d3d4;width:99%;height:15px\">";
			           },
			            editor:new Ext.form.ComboBox({
							allowBlank:false,
							editable:false,
							typeAhead: true,
							triggerAction: 'all',
							lazyRender:true,
							mode: 'local',
							store: new Ext.data.ArrayStore({
								fields: [
									'value'
								],
								data:[[''],['A'],['B'],['C']]
							}),
							valueField: 'value',
							displayField: 'value'
						})
			        },
			        {  
			            header: '是否客供*',  
			            dataIndex: 'iscustomer',sortable: true,  
			            renderer:function(value, metaData, record, rowIndex, colIndex,store, gridview){
			                record.modified=false;
			                return "<input type=\"text\" value=\""+value+"\"/ style=\"margin-top:7px;border:1px solid #d1d3d4;width:99%;height:15px\">";
			           },
			            editor:new Ext.form.ComboBox({
							allowBlank:false,
							editable:false,
							typeAhead: true,
							triggerAction: 'all',
							lazyRender:true,
							mode: 'local',
							store: new Ext.data.ArrayStore({
								fields: [
									'value'
								],
								data:[['是'],['否']],
							}),
							valueField: 'value',
							displayField: 'value'
						})
			        },
			        {  
			            header: '是否开模*',  
			            dataIndex: 'openMould',sortable: true, 
			            renderer:function(value, metaData, record, rowIndex, colIndex,store, gridview){
			                record.modified=false;
			                var newValue = getDefaultOpenMould(record);
				            if(newValue !="" && value != newValue){
				                value= newValue;
				            }
				            record.set('openMould',value);
			                return "<input type=\"text\" value=\""+value+"\"/ style=\"margin-top:7px;border:1px solid #d1d3d4;width:99%;height:15px\">";
			           },
			            editor:new Ext.form.ComboBox({
							allowBlank:false,
							editable:false,
							typeAhead: true,
							triggerAction: 'all',
							lazyRender:true,
							mode: 'local',
							listeners:{
			            		focus:function(combo){
			            			loadOpenMouldStore(combo);
			            		}
			            	},
							store: new Ext.data.ArrayStore({
								fields: [
									'value'
								],
								data:[]
							}),
							valueField: 'value',
							displayField: 'value'
						})
			        },
			        {  
			            header: '规格重复时仍然创建*',  
			            dataIndex: 'forcecreate',sortable: true,  
			            renderer:function(value, metaData, record, rowIndex, colIndex,store, gridview){
			                record.modified=false;
			                return "<input type=\"text\" value=\""+value+"\"/ style=\"margin-top:7px;border:1px solid #d1d3d4;width:99%;height:15px\">";
			           },
			            editor:new Ext.form.ComboBox({
							allowBlank:false,
							editable:false,
							typeAhead: true,
							triggerAction: 'all',
							lazyRender:true,
							mode: 'local',
							store: new Ext.data.ArrayStore({
								fields: [
									'value'
								],
								data:[['是'],['否']],
							}),
							valueField: 'value',
							displayField: 'value'
						})
			        },
			        {  
			            header: '描述',  
			            dataIndex: 'description',
			            renderer:function(value, metaData, record, rowIndex, colIndex,store, gridview){
			                record.modified=false;
			                return "<input type=\"text\" value=\""+value+"\"/ style=\"margin-top:7px;border:1px solid #d1d3d4;width:99%;height:15px\">";
			           },
			            editor:new Ext.form.TextField({
			            }),
			            width: 100,
			            sortable: true,  
			        },
			        {  
			            header: '旧物料号',  
			            dataIndex: 'oldnum',
			            renderer:function(value, metaData, record, rowIndex, colIndex,store, gridview){
			                record.modified=false;
			                return "<input type=\"text\" value=\""+value+"\"/ style=\"margin-top:7px;border:1px solid #d1d3d4;width:99%;height:15px\">";
			           },
			            editor:new Ext.form.TextField({
			            }),
			            width: 100,
			            sortable: true,  
			        },
			        {  
			            header: '操作',  
			            width:150,
			            dataIndex: 'operation',
			            renderer:function(value,cellmeta){
			                var returnStr = "<button type=\"button\" onclick=\"selectclf()\" id=\"ext-gen78\" class=\" x-btn-text\" ext:qtip=\"设置分类\" style=\"height:24px;background-repeat:no-repeat;background-image: url(&quot;netmarkets/images/classify.gif&quot;);\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<button type=\"button\" onclick=\"showMwindow()\" id=\"ext-gen78\" class=\" x-btn-text\" ext:qtip=\"编辑分类\" style=\"height:24px;background-repeat:no-repeat;background-image: url(&quot;netmarkets/images/edit.gif&quot;);\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</button><button type=\"button\" ext:qtip=\"添加附件\" onclick=\"showattachwindow()\" id=\"ext-gen78\" class=\" x-btn-text\" style=\"height:24px;background-repeat:no-repeat;background-image: url(&quot;netmarkets/images/document.gif&quot;);\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</button><button type=\"button\" ext:qtip=\"移除附件\" onclick=\"removeattach()\"  class=\"x-btn-text\" style=\"height:24px;background-repeat:no-repeat;background-image: url(&quot;netmarkets/images/remove16x16.gif&quot;);\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</button>";
			                return returnStr;
			           }
			        },
			        {  
			            header: '分类*',  
			            dataIndex: 'clfname',
			            renderer:function(value, cellmeta, record, rowIndex, columnIndex, store){
			            	var tip=record.get('clf');
			            	if(tip!=""){
			            		var clf=JSON.parse(tip);
				            	var html='';
				            	for(var i=0;i<clf.length;i++){
				            		var att=clf[i];
				            		if(att.displayname!="cls"){
				            			var str=att.displayname+":"+att.value
					            		html=html+str+"<br/>";
				            		}
				            	}
				            	
				            	cellmeta.attr = 'ext:qtip=\''+html+'\'';
				                return value;
			            	}
			            	
			           }
			        },
			        {  
			            header: '附件',  
			            dataIndex: 'attachname',
		            	renderer:function(value, metaData, record, rowIndex, colIndex,store, gridview){
			                console.log(value);
			                record.modified=false;
			                if(value!=""&&value!=undefined){
			                	value=value.substring(0,value.lastIndexOf("_"));
			                }
			                return value;
			           }
			        }
			        
			        ]  
		    	})
		    });  
		  
	    var tb1 = new Ext.Container({
			renderTo:'multiPart',
			width:'98%',
			height:40,
			html:"<div id=\"multipart_tab\">"+act0+"</div>"
	    });	 

		var tb2 = new Ext.Container({
			renderTo:'multiPart',
			width:'98%',
			id:'content_display',
			height:winHeight-150,
			style:'border:1px solid #c0c0c0;margin:0 auto;'
	   });	 
		
		  var tabs = new Ext.TabPanel({
			    id:'multipart_panel',
				renderTo:'content_display',
				activeTab: 0,
				headerCfg:{style:'display:none'},
				width:'99.5%',
				style:'padding:10px 5px',
				height:winHeight-160,
				items: [
				{
					title: '定义部件',
					items:[
						{
							xtype: 'container',
							items:[
							  {
								xtype:'container',
								layout:'table',
								items:[
								       {
								    	 xtype:'displayfield',
								    	 value:'*类型:',
								       },
								       new Ext.form.ComboBox({
											typeAhead: true,
											width:100,
											triggerAction: 'all',
											editable:false,
											lazyRender:true,
											id:'parttype',
											mode: 'local',
											style:'margin-left:5px',
											value:'wt.part.WTPart|com.CATLBattery.CATLPart',
											store: new Ext.data.ArrayStore({
												fields: [
													'displayname','name'
												],
												data:[['零部件','wt.part.WTPart|com.CATLBattery.CATLPart']]
											}),
											valueField: 'name',
											displayField: 'displayname'
									})  
								 ]
							  }
							 
							]
						}
						
					]
					
				 },
				 {
					title: '设置标识属性',
					items:[
						{
							xtype: 'container',
							items:[
                                  gridtb,
                                  grid
							]
						}
						
					]
						
				}
				]
		  });
			var footpanel = new Ext.Container({
				renderTo:Ext.getBody(),
				layout:'hbox',
				id:'footerpanel',
				width:winWidth*0.98,
				style:'margin:1%;',
				items:[
					{
					xtype : 'displayfield',
					flex:1,
					style:'font-size:12px;font-weight:normal;color:#464646;-ms-flex:1',
					value : '*表示必填字段'
					},
					{
						xtype:'container',
						layout:'table',
						items: [
								{
									xtype : 'button',
									style:'margin-left:10px',
									id:'bbtn',
									text : '上一页(B)',
									disabled:true,
									handler:function(){
										setActiveTab(0);
										Ext.getCmp('bbtn').setDisabled(true);
										Ext.getCmp('nbtn').setDisabled(false);
									}
								},
								{
									xtype : 'button',
									style:'margin-left:10px',
									text : '下一页(N)',
									id:'nbtn',
									handler:function(){
										setActiveTab(1);
										var m=Ext.getCmp('parttype');
									console.log(m.getValue());
										Ext.getCmp('bbtn').setDisabled(false);
										Ext.getCmp('nbtn').setDisabled(true);
									}
								},
								{
									xtype : 'button',
									style:'margin-left:10px',
									text : '完成(F)',
									id:'fbtn',
									handler:function(btn){
										var grid=Ext.getCmp("create_grid");
										var store=grid.store;
										for(var i=0;i<store.getCount();i++){
											var rec=store.getAt(i);
											if(rec.get('name')==""){
												alert("名称必须填写");
												return;
											}
											if(rec.get('clfname')==""){
												alert("分类必须选择");
												return;
											}
										}
										var array=new Array();
										for(var i=0;i<store.getCount();i++){
											var node=store.getAt(i);
											console.log(node);
											var arr=node.data;
											array.push(arr);
										}
										var str=JSON.stringify(array);
										var parttype=Ext.getCmp("parttype").getValue();
										  btn.setDisabled(true);
										  var mk=new Ext.LoadMask(document.body,{msg:'正在创建。。。,请稍后',removeMask:true});
										  mk.show();
										  Ext.Ajax.request({
									          disableCaching:false,
											  url : "/Windchill/ptc1/line/createMultiPart.do",
											  params:{jsonstr:str,type:parttype,uploadtype:'multipart',folderOid:folderOid,containerOid:containerOid},
											  success : function(response, opts) {
												  mk.hide();
												  var msg=JSON.parse(response.responseText).msg;
												  if(msg==""){
													if(window.opener){
														window.opener.location.reload();
													}
													  window.close();
												  }else{
													  btn.setDisabled(false);
													  alert(msg);
												  }
												 
											  },
											  failure:function(){
												  mk.hide();
												  alert("请求出错");
												  btn.setDisabled(false);
											  }
										  });
									}
								},
								{
									xtype : 'button',
									style:'margin-left:10px',
									id:'cbtn',
									text : '取消(C)',
									handler:function(){
										window.close();
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
	  function setActiveTab(index){
		  var tab=eval('act'+index);
		  document.getElementById("multipart_tab").innerHTML=tab;
		  Ext.getCmp("multipart_panel").setActiveTab(index);
	  }
	  
	  function setClf(jsonstr,html){
			 console.log(html);
			 Ext.Ajax.request({
				  disableCaching:false,
				  url : "/Windchill/ptc1/line/parseClfAttributes.do",
			      params:{jsonstr:jsonstr,containerOid:containerOid,folderOid:folderOid},
				  success : function(response, opts) {
				  var data=JSON.parse(response.responseText).data;
				  for(var i=0;i<data.length;i++){
					  var attr=data[i];
					  if(attr.displayname=="cls"){
						    var grid=Ext.getCmp("create_grid");
							var sel=grid.getSelectionModel().selections;
							for(var j=0;j<sel.getCount();j++){
								var node=sel.get(j);
								node.set('clfname',attr.clfname)
								var str=JSON.stringify(data);
								node.set('clf',str);								
								node.set('namerec',attr.partname);
								node.set('name',"");
								node.set('source',"");
								node.set('openMould',"");
								node.set('edithtml',html);
								console.log(str);
							}
					  }
				  }
				  }
			 });
	 }
	 function selectclf(){
		 window.open(clfurl, "_blank","resizable=yes,scrollbars=no,menubar=no,toolbar=no,location=no,status=yes,top=40,left=75,height="+(winHeight-80)+",width="+(winWidth-150));
	 }
	 function removeattach(){
		 var grid=Ext.getCmp("create_grid");
			var sel=grid.getSelectionModel().selections;
			for(var i=0;i<sel.getCount();i++){
				console.log(sel);
				var node=sel.get(i);
				node.set('attachname',"");
			}
	 }
	 
	 function showattachwindow(){
		  var innerwin = new Ext.Window({  
			   title: "上传附件",  
			   width: 600,  
			   height: 150,  
			   closable: true,  
			   draggable: true,  
			   resizable:false,
			   closeAction: "hide",  
			   buttons: [{  
				   text: "确定", handler: function () {  
					   var form=Ext.getCmp("uploadform").getForm();
						form.submit({
							url: '/Windchill/ptc1/line/uploadattach.do?type=multipart',
							method: "POST",
							waitMsg : '正在进行处理,请稍后...',
							waitTitle :'请稍后',
							success: function (form, action) {
								innerwin.close();
							    var text=action.response.responseText;
							    var filename=text.replace("{success:true,filename:'","").replace("'}","");
								if(filename==""){
								    alert("上传文件出错");
								}else{
									var grid=Ext.getCmp("create_grid");
									var sel=grid.getSelectionModel().selections;
									for(var i=0;i<sel.getCount();i++){
										console.log(sel);
										var node=sel.get(i);
										node.set('attachname',filename)
									}
									//alert("上传附件成功");
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
						saTarget:'unber',
						anchor: '90%'     
					}
				 ]
			   }
			   ]
		   });  
		  innerwin.show();
	 }
	 function removesel(grid){
		 var sel=grid.getSelectionModel().selections;
		    var selarray=new Array();
			for(var i=0;i<sel.getCount();i++){
				var node=sel.get(i);
				grid.store.remove(node);
			}
		if(grid.getSelectionModel().selections.getCount()!=0){
			 removesel(grid);
		}
	 }
	 
	 function showattachwindow(){
				  var innerwin = new Ext.Window({  
					   title: "上传附件",  
					   width: 600,  
					   height: 150,  
					   closable: true,  
					   draggable: true,  
					   closeAction: "hide",  
					   buttons: [{  
						   text: "确定", handler: function () {  
							   var form=Ext.getCmp("uploadform").getForm();
								form.submit({
									url: '/Windchill/ptc1/line/uploadattach.do?type=multipart',
									method: "POST",
									waitMsg : '正在进行处理,请稍后...',
									waitTitle :'请稍后',
									success: function (form, action) {
										innerwin.close();
									    var text=action.response.responseText;
									    var filename=text.replace("{success:true,filename:'","").replace("'}","");
										if(filename==""){
											alert("上传文件出错");
										}else{
											var grid=Ext.getCmp("create_grid");
											var sel=grid.getSelectionModel().selections;
											for(var i=0;i<sel.getCount();i++){
												console.log(sel);
												var node=sel.get(i);
												node.set('attachname',filename)
											}
											//alert("上传附件成功");
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
								saTarget:'unber',
								anchor: '90%'     
							}
						 ]
					   }
					   ]
				   });  
				  innerwin.show(); 
	 }
	 
	 function showBatchEditAttrWindow(){
		    var grid=Ext.getCmp("create_grid");
			var sel=grid.getSelectionModel().selections;
			if(sel.getCount()==0){
				alert("请勾选要更改的行");
				return;
			}
			   var win=new Ext.Window({  
				   width: 410,  
				   height: 200,  
				   closable: true,  
				   draggable: true,  
				   style:'padding:20px 10px',
				   closeAction: "hide",
				   bbar:[
					   {
						 xtype : 'button',
						 text : '确定',
						 handler:function(btn){
							var cgrid=Ext.getCmp("create_grid");
							var csel=grid.getSelectionModel().selections;
							var name=Ext.getCmp("attr_select").getValue();
							var value=Ext.getCmp("attr_field").getValue();
							console.log(name);
							for(var j=0;j<sel.getCount();j++){
								var node=sel.get(j);
								node.set(name,value);
							}
							win.close();
						} 
					   },
					   {
						 xtype : 'button',
						 text : '应用',
						 handler:function(){
						    var cgrid=Ext.getCmp("create_grid");
							var csel=grid.getSelectionModel().selections;
							var name=Ext.getCmp("attr_select").getValue();
							var value=Ext.getCmp("attr_field").getValue();
							console.log(name);
							for(var j=0;j<sel.getCount();j++){
								var node=sel.get(j);
								node.set(name,value);
							}
						} 
					   }
			       ],
				   items:[
					   {
						 xtype:'form',
						 id:'attr_fill_form',
						 items:[
						       new  Ext.form.ComboBox({
									allowBlank:false,
									editable:false,
									typeAhead: true,
									id:'attr_select',
									width:250,
									triggerAction: 'all',
									lazyRender:true,
									mode: 'local',
									fieldLabel:'选择列',
									value:'unit',
									listeners: {
										collapse : function(combo) {
											var value=combo.getRawValue();
											Ext.getCmp("attr_fill_form").remove(Ext.getCmp("attr_field"),true);
											var field;
											if(value=="描述"||value=="旧物料号"){
												field=new Ext.form.TextField({
													id:'attr_field',
													width:250,
													fieldLabel:'填写'
									            });
											}else if(value=="默认单位"){
												field=new Ext.form.ComboBox({
													allowBlank:false,
													editable:false,
													id:'attr_field',
													typeAhead: true,
													fieldLabel:'填写',
													value:'pcs',
													width:250,
													triggerAction: 'all',
													lazyRender:true,
													mode: 'remote',
													store: new Ext.data.ArrayStore({
														fields: [
															'value'
														],
														root:'data',
														url :'/Windchill/ptc1/line/queryUnits.do'
													}),
													valueField: 'value',
													displayField: 'value'
												});
											}else if(value=="采购类型"){
												field=new Ext.form.ComboBox({
													allowBlank:false,
													editable:false,
													typeAhead: true,
													id:'attr_field',
													fieldLabel:'填写',
													value:'外购',
													width:250,
													triggerAction: 'all',
													lazyRender:true,
													mode: 'remote',
													
													store: new Ext.data.ArrayStore({
														fields: [
															'value'
														],
														root:'data',
														url :'/Windchill/ptc1/line/querySources.do'
													}),
													valueField: 'value',
													displayField: 'value'
												});
											}else if(value=="产品线标识"){
												field=new Ext.form.ComboBox({
													allowBlank:false,
													editable:false,
													id:'attr_field',
													typeAhead: true,
													width:250,
													fieldLabel:'填写',
													triggerAction: 'all',
													lazyRender:true,
													mode: 'local',
													store: new Ext.data.ArrayStore({
														fields: [
															'value'
														],
														data:[[''],['A'],['B'],['C']]
													}),
													valueField: 'value',
													displayField: 'value'
												})
											}else if(value=="规格重复时仍然创建"){
												field=new Ext.form.ComboBox({
													allowBlank:false,
													editable:false,
													typeAhead: true,
													fieldLabel:'填写',
													value:'否',
													width:250,
													id:'attr_field',
													triggerAction: 'all',
													lazyRender:true,
													mode: 'local',
													store: new Ext.data.ArrayStore({
														fields: [
															'value'
														],
														data:[['是'],['否']],
													}),
													valueField: 'value',
													displayField: 'value'
												})
											}
											console.log(field);
											Ext.getCmp("attr_fill_form").add(field);
											Ext.getCmp("attr_fill_form").doLayout();
										 }
									},
									store: new Ext.data.ArrayStore({
										fields: [
											'value','dataindex'
										],
										//description:'',oldnum:'',unit:'pcs',source:'外购',forcecreate:'否'
										data:[['默认单位','unit'],['产品线标识','platform'],['规格重复时仍然创建','forcecreate'],['描述','description'],['旧物料号','oldnum']]
									}),
									valueField: 'dataindex',
									displayField: 'value'
								}),
								new Ext.form.TextField({
									id:'attr_field',
									width:250,
									fieldLabel:'填写',
					            	allowBlank:false,
					            	blankText:'必填项'
					            })
						 ]
					   }
				   ]
			 });
			   win.show();
	 }
	 function showMwindow(){
		 var grid=Ext.getCmp("create_grid");
			var sel=grid.getSelectionModel().selections;
			var node=sel.get(0);
			var clf=node.get("clf");
			if(clf==""){
				return;
			}
			var clfjson=clf;
			clf=JSON.parse(clf);
			console.log(clf);
			var clfnode="";
			for(var i=0;i<clf.length;i++){
        		var att=clf[i];
        		if(att.displayname=="cls"){
        			var value=att.value;
        			clfnode=value;
        		}
        	}
			var html=node.get("edithtml");
			   var win=new Ext.Window({  
				   width: winWidth-100,  
				   height: winHeight-50,  
				   clfnode:clfnode,
				   clfjson:clfjson,
				   closable: true,  
				   draggable: true,  
				   style:'padding:10px',
				   closeAction: "hide",
				   bbar:[
					   {
						 xtype : 'button',
						 text : '保存',
						 handler:function(){
								var vals=Ext.getCmp("editclfform").getForm().getValues();
								var values=JSON.stringify(vals);
								 Ext.Ajax.request({
									  disableCaching:false,
									  url : "/Windchill/ptc1/line/parseClfAttributes.do",
								      params:{clfjson:win.clfjson,clfnode:win.clfnode,jsonstr:values,containerOid:containerOid,folderOid:folderOid,type:'edit'},
									  success : function(response, opts) {
									  var data=JSON.parse(response.responseText).data;
									  console.log(data);
									  for(var i=0;i<data.length;i++){
										  var attr=data[i];
											    var grid=Ext.getCmp("create_grid");
												var sel=grid.getSelectionModel().selections;
												for(var j=0;j<sel.getCount();j++){
													var node=sel.get(j);
													var str=JSON.stringify(data);
													node.set('clf',str);
												}
									  }
									  }
								 });
								 win.close();
						} 
					   }
			       ],
				   items:[
					   {
						 xtype:'form',
						 id:'editclfform',
						 html:'<div style="width:96%;padding:2%">'+html+'</div>'
					   }
				   ]
			 });
			   win.show();
			   var current_parttype=Ext.getCmp("parttype").getValue();
			 	for(var i=0;i<clf.length;i++){
	        		var att=clf[i];
	        		if(att.displayname!="cls"){
	        			var name=att.name;
	        			var value=att.value;
	        			clfnode=value;
	        		    var id="WCTYPE|"+current_parttype+"~IBA|"+name;
	        		    console.log(id);
	        		    document.getElementById(id).value=value;
	        		}
	        	}
            
	 }
	 
	 function loadNameStore(combo){
		 var store=combo.store;
		 store.loadData(new Array(),false);
			console.log(store);
			var grid=Ext.getCmp("create_grid");
			var sel=grid.getSelectionModel().selections;
			var node=sel.get(0);
			var namerec=node.get("namerec");
			var namesource = namerec.split('qqqq;;;;');
         if(namesource.length == 4){
         	    var names = namesource[0];
	            var source = namesource[1];
	            var cars = names.split('|');       
	            var dataarray=new Array();
	            for(var i=0;i<cars.length;i++){
	            	var array=new Array();
	            	array.push(cars[i]);
	            	dataarray.push(array);
	            }
	            
	            console.log(store);
 			store.loadData(dataarray,false);
         }
	 }
	 
	 function getDefaultName(node){		 
			var namerec=node.get("namerec");
			if(namerec&&namerec!=""){
				var namesource = namerec.split('qqqq;;;;');
		         if(namesource.length == 4){
			            var name = namesource[0];
			            var cars = name.split('|');  
			            if(cars.length ==1){
			            	return cars[0];
			            } 
			             /* for(var i=0;i<cars.length;i++){
			            	if(i==0){
			            		return cars[i];
			            	}
			            }  */
		         }
			}
   return "";
	 }
	 
	 function loadSourceStore(combo){		 
		 var store=combo.store;
		 store.loadData(new Array(),false);
			console.log(store);
			var grid=Ext.getCmp("create_grid");
			var sel=grid.getSelectionModel().selections;
			var node=sel.get(0);
			var namerec=node.get("namerec");
			var namesource = namerec.split('qqqq;;;;');
         if(namesource.length == 4){
	            var source = namesource[1];
	            var cars = source.split('|');       
	            var dataarray=new Array();
	            for(var i=0;i<cars.length;i++){
	            	var array=new Array();
	            	var sourcedisname = cars[i].split(',');
	            	array.push(sourcedisname[1]);	            	
	            	dataarray.push(array);
	            }
 			store.loadData(dataarray,false);
         }
	 }
	 
	 function getDefaultSource(node){		 
			var namerec=node.get("namerec");
			if(namerec&&namerec!=""){
				var namesource = namerec.split('qqqq;;;;');
		         if(namesource.length == 4){
			            var source = namesource[1];
			            var cars = source.split('|');   
			            if(cars.length == 1){
			            	var sourcedisname = cars[0].split(',');
			            	//if(i==0){
			            	return sourcedisname[1];
			            	//}
			            }
		         }
			}
         return "";
	 }
	 
	 function loadUnitStore(combo){
		 var store=combo.store;
		 store.loadData(new Array(),false);
			console.log(store);
			var grid=Ext.getCmp("create_grid");
			var sel=grid.getSelectionModel().selections;
			var node=sel.get(0);
			var namerec=node.get("namerec");
			var namesource = namerec.split('qqqq;;;;');
         if(namesource.length == 4){
         	    var names = namesource[2];
	            var cars = names.split('|');       
	            var dataarray=new Array();
	            for(var i=0;i<cars.length;i++){
	            	var array=new Array();
	            	array.push(cars[i]);
	            	dataarray.push(array);
	            }	            
	            console.log(store);
 			store.loadData(dataarray,false);
         }
	 }
	 
	 function getDefaultUnit(node){		 
			var namerec=node.get("namerec");
			if(namerec&&namerec!=""){
				var namesource = namerec.split('qqqq;;;;');
		         if(namesource.length == 4){
			            var source = namesource[2];
			            var cars = source.split('|');  
			           /*  if(cars.length ==1){
			            	return cars[0];
			            } */
			             for(var i=0;i<cars.length;i++){
			            	if(i==0){
			            		return cars[i];
			            	}
			            } 
		         }
			}
      return "";
	 }
	 
	 function loadOpenMouldStore(combo){
		 var store=combo.store;
		 store.loadData(new Array(),false);
			console.log(store);
			var grid=Ext.getCmp("create_grid");
			var sel=grid.getSelectionModel().selections;
			var node=sel.get(0);
			var namerec=node.get("namerec");
			var namesource = namerec.split('qqqq;;;;');
         if(namesource.length == 4){
         	    var names = namesource[3];
         	    //alert(names);
         	 	var dataarray=new Array();
         	   	var array=new Array();
           		array.push(names);
           		dataarray.push(array);
	            console.log(store);
 			store.loadData(dataarray,false);
         }
	 }
	 
	 function getDefaultOpenMould(node){		 
		var namerec=node.get("namerec");
		if(namerec&&namerec!=""){
			var namesource = namerec.split('qqqq;;;;');
		    if(namesource.length == 4){
			     var source = namesource[3];
			     return source
		    }
		}
        return "";
	 }
	</script>
   </head>
   <body>
   	<div id="multiPart" style="overflow:hidden"></div>
   </body>
 <%@include file="/netmarkets/jsp/util/end.jspf"%>
