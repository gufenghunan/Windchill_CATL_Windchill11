<%@ page language="java" import="java.util.*" contentType="text/html; charset=utf-8" %>  
<html lang="en-US">
<head>
   <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <style>
   .x-fieldset legend {
	color: #B08200 !important;
   }
   
   .standard_link{
	    margin: 10px auto;
		color: blue;
		position: relative;
		top: 30px;
		font-size: 15px;
		text-decoration: underline;
   }
   .x_scroll_cls{
	    overflow-x:hidden !important;
   }
   .arrow_left_container{
	   background-image: url(netmarkets/images/arrow_rightright.gif) !important;
	   background-position: 92% 50% !important;
	   background-repeat:no-repeat !important;
   }
   
    .gridtb_cls .x-form-field-wrap .x-form-trigger{
	 top:-2px !important;
	 }
	 
	 .gridtb_cls{
	 padding:0px;
	 }
	 
	 .x-form-textarea.x-form-focus{
	 background-image:url() !important;
	 }
	 
	 .inlineHelpBox {
	    margin: 0 0 8px 0 !important;
	}
   </style>
	<script type="text/javascript">
	     var website='<%=request.getServerName()%>';
		 Ext.onReady(function() {
			Ext.Ajax.request({
				disableCaching:false,
				url : "/Windchill/ptc1/battery/isvalidAdmin.do",
				success : function(response, opts) {
					var data=JSON.parse(response.responseText).data;
					if(data==false){
						location.href="/Windchill/app/#ptc1/catl/battery/error";
					}
				}
			});
			var title=new Ext.Panel({
				renderTo:'contentDiv',
				width: '99%',
				height:30,
				id:'title_panel',
				style:'border-bottom:1px solid #000;font-size:16px;font-weight:bold',
				html:'<span id="nav_title">主页>EVC智能设计>系统配置<span>'
			});
			var mainpanel=new Ext.Panel({
				renderTo:'contentDiv',
				width: '99%',
				id:'main_panel',
				style:'border-top:2px solid #F2C582',
		   });
			var container=new Ext.Container({
				style:'margin-top:10px;',
				renderTo:'main_panel',
				html:'<div class=\"inlineHelpBox\"><img border=\"0\" src=\"wtcore/images/tip.gif\">通过上传模版调整智能计算表计算公式<br/>注意<br/>&nbsp;&nbsp;1.计算公式格式为标准格式<br/>&nbsp;&nbsp;2.需要调整计算界面请联系管理员!</div>'
			});
			  
			var gridtb = new Ext.Toolbar({
				cls:'gridtb_cls',
				renderTo:'main_panel',
				id:'gridtb',
				style:'margin-top:5px;border:1px solid #c0c0c0;border-bottom:1px solid #AAA',
				width: '100%',
				height: 35,
				items: [
						{  
			                xtype:'button',
							icon:'netmarkets/images/create_tbar.gif',
							style:'margin:4px;display:none',
							tooltip:'新建',
				            handler: function(){
				            	opFile("新建模版","");
				            }  
				        },
				        {  
			                xtype:'button',
							icon:'netmarkets/images/delete.gif',
							style:'margin:4px;display:none',
							tooltip:'删除',
				            handler: function(){
				            	var sel = Ext.getCmp("ppngrid").getSelectionModel().selections;
							    if(sel.length==0){
							    	 showWarningMessage("请先选中");
								 }else{
									 var oids="";
									 for(var i=0;i<sel.length;i++){
										 var node=sel.items[i];
										 oids=oids+node.get("oid")+",";
									 }
									 oids=oids.substring(0,oids.length-1);
								     deleteByOid(oids);
								 }
				            }  
				        },
				        {  
			                xtype:'button',
							icon:'netmarkets/images/download.gif',
							style:'margin:4px',
							tooltip:'下载',
				            handler: function(){
				            	var sel = Ext.getCmp("ppngrid").getSelectionModel().selections;
							    if(sel.length==0){
							    	 showWarningMessage("请先选中");
								 }else{
									 var oids="";
									 for(var i=0;i<sel.length;i++){
										 var node=sel.items[i];
										 oids=oids+node.get("oid")+",";
									 }
									 oids=oids.substring(0,oids.length-1);
								     downloadByOid(oids);
								 }
				            }  
				        }
				]
			});
			  var store= new Ext.data.ArrayStore({
			        fields: ['oid','name','creator','modifier','description','modifytime'],
					root:'data',
					url :'/Windchill/ptc1/battery/getTemplate.do'
			 });
			  store.load();
			  var grid = new Ext.grid.GridPanel({
					id:'ppngrid',
					store:store,
					renderTo:'main_panel',
					style:'border:1px solid #c0c0c0;border-top:0px',
					listeners:{
						contextmenu:function(e){
						    var row = e.getTarget('.x-grid3-row');
				            if(row !== undefined) {
				                var index = row.rowIndex;
				                Ext.getCmp("ppngrid").getSelectionModel().selectRow(index)
				            }
							e.stopEvent();
							var contextMenu = new Ext.menu.Menu({
									width:150,
									margin:'0 0 10 0',
									items:[
										{
											text:'下载模版',
											icon:'netmarkets/images/download.gif',
											handler:function(){
												var sel = Ext.getCmp("ppngrid").getSelectionModel().selections;
											    if(sel.length==0){
											    	 showWarningMessage("请先选中");
												 }else{
													 var oids="";
													 for(var i=0;i<sel.length;i++){
														 var node=sel.items[i];
														 oids=oids+node.get("oid")+",";
													 }
													 oids=oids.substring(0,oids.length-1);
												     downloadByOid(oids);
												 }
											}
										},
										{
											text:'更改模版',
											icon:'netmarkets/images/edit.gif',
											handler:function(){
											    var sel = Ext.getCmp("ppngrid").getSelectionModel().selections;
											    if(sel.length!=1){
											    	 showWarningMessage("请选中一个");
												 }else{
													 var node=sel.items[0];
													 opFile("更改模版",node.get("oid"));
												 }
											    
											}
										},
										{
											text:'删除模版',
											style:'display:none',
											icon:'netmarkets/images/delete.gif',
											handler:function(){
												var sel = Ext.getCmp("ppngrid").getSelectionModel().selections;
											    if(sel.length==0){
											    	 showWarningMessage("请先选中");
												 }else{
													 var oids="";
													 for(var i=0;i<sel.length;i++){
														 var node=sel.items[i];
														 oids=oids+node.get("oid")+",";
													 }
													 oids=oids.substring(0,oids.length-1);
												     deleteByOid(oids);
												 }
											    
											}
										},
									]
								});
							contextMenu.showAt(e.getXY());
						}
					},
					colModel: new Ext.grid.ColumnModel({
							columns: [
                                 new Ext.grid.CheckboxSelectionModel (),
								{
								  header: '模版名称', dataIndex: 'name',sortable: true
								},
								{
								  header: '修改者', dataIndex: 'modifier',sortable: true
								},
								{
								  header: '说明', dataIndex: 'description',sortable: true
								},
								{
								  header:'上次修改时间', dataIndex:'modifytime',sortable: true
								}
							],
						}),
						viewConfig: {
							forceFit: true
						},
						sm: new Ext.grid.CheckboxSelectionModel(),
						scrollable:true,
						width: '100%',
						height: 300
			  });
			  
		  
		});
		 
		 
		 function opFile(title,oid){
				var uploadwin=new Ext.Window({  
					   width: 400,  
					   height: 180,  
					   id:'upwindow',
					   title:title,
					   closable: true,  
					   draggable: false,  
					   resizable:false,
					   autoScroll:true,
					   closeAction: "hide",
					   modal:true,
					   padding:20,
					   items:[{
						   title: '',
						   items:[{
							    xtype:'form', 
							    layout:'table',
							    enctype : 'multipart/form-data', // 把文件以二进制流的方式传递到服务器
							    fileUpload : true,
							    id:'upload_template',									 
						   		items:[{    
									xtype: 'displayfield',
									value:'上传文件:'
								},
								{    
									xtype: 'textfield',
									width:180,
									id:'upload_conf',
									name: 'uploadfile',    
									inputType: 'file',  
									style:'margin-left:10px',
									regex:/\.xlsx$/,
									regexText : '请选择xlsx类型的文件',
									saTarget:'unber',
								},{
									xtype:'button',
									height:25,
									text:'上传文件',
									style:'padding-left:10px;width:40px',
									handler:function(){
										var form=Ext.getCmp("upload_template").getForm();
										var description=Ext.getCmp("upload_description").getValue();
										console.log(description);
										form.submit({
											url: '/Windchill/ptc1/battery/opTemplate.do',
											params:{oid:oid,description:description},
											method: "POST",
											success: function (form, action) {
												var text=action.response.responseText;
												console.log(text);
												var data=eval("("+text+")");
												if(data.msg==""){
													showMessage(title+"成功");
													uploadwin.close();
													Ext.getCmp("ppngrid").getStore().reload();
												}else{
													showWarningMessage(data.msg);
												}
												
											},
											failure:function (){
												showWarningMessage('请求失败');
											}
										});
									}
								}]
							},{
							    xtype:'form', 
							    layout:'table',
							    style:'margin-top:20px',
								items:[{
									xtype: 'displayfield',
									style: 'margin-left:25px',
									value:'说明:'
								},{    
									xtype: 'textarea',
									width:180,
									id:'upload_description',
									style:'margin-left:10px',
								}]
							}]
					   }]
				});
				uploadwin.show();
			}

		 
	function downloadByOid(oid){
		 Ext.Ajax.request({
		     disableCaching:false,
			  url : "/Windchill/ptc1/battery/downloadTemplate.do",
			  params:{
				      oids:oid
			  },
			  success : function(response, opts) {
				  var msg=JSON.parse(response.responseText).msg;
				  var url=JSON.parse(response.responseText).data;
				  if(msg==""){
					  if(url.indexOf(website)>-1){
						  window.open('http://'+url);
					  }else{
						  window.open('http://'+website+url);
					  }
					  Ext.getCmp("ppngrid").getStore().reload();
					 
				  }else{
					 showWarningMessage(msg);
				  }
		  	  }
    	   });
		
	}
	function deleteByOid(oid){
		console.log(oid);
		 Ext.Ajax.request({
		     disableCaching:false,
			  url : "/Windchill/ptc1/battery/deleteTemplate.do",
			  params:{
				      oids:oid
			  },
			  success : function(response, opts) {
				  var msg=JSON.parse(response.responseText).msg;
				  console.log(msg);
				  if(msg==""){
					  Ext.getCmp("ppngrid").getStore().reload();
				  }else{
					 showWarningMessage(msg);
				  }
		  	  }
   	   });
		
	}

	function showMessage(message){
		PTC.messaging.showInlineMessage([{
		    MessageTitle: '提示',
		    Messages: [message],
		    MessageType: 'SUCCESS'
		}]);
		setTimeout("PTC.messaging.closeInlineMessage();",1000);
	}

	function showWarningMessage(message){
		PTC.messaging.showInlineMessage([{
		    MessageTitle: '提示',
		    Messages: [message],
		    MessageType: 'WARNING'
		}]);
		setTimeout("PTC.messaging.closeInlineMessage();",1000);
	}
	</script>
	
</head>
<body>

</body>
</html>