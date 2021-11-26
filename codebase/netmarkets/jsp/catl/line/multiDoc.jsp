 <%@ include file="/netmarkets/jsp/components/beginWizard.jspf" %> 
 <%@ page language="Java" pageEncoding="UTF-8"%>

 <head>
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
	#x-form-el-upload_multidoc{
	    padding-left:75px !important;
	}
	#multidoc_panel .x-form-item-label{
	    width:70px !important;
	    font-weight: bold;
	}
	body {
    overflow: hidden;
    }
    .uploadbtn_cls button{
    height: 20px !important;
	}
    .msginfo_div{
     width:99%;
     height:500px;
     position:relative;
     top:10px;
     padding:5px;
     border:1px solid #C0C0C0;
     overflow: scroll;
    }
    .msginfo_div li{
     line-height:30px;
     background: url("/Windchill/netmarkets/images/doc_document.gif") 0% 50%;
     background-repeat: no-repeat;
    }
     .msginfo_div span{
     position:relative;
     left:20px;
    }
    
   </style>
	<script type="text/javascript">
	var website='<%=request.getScheme()+"://"+request.getServerName()%>';
	var winWidth=0,winHeight=0;
	var act0="<div style=\"position:relative;top:2px;left:1%\"><ul id=\"stripTabWrap\" class=\"header-strip\"><li class=\"wizardstepLinks first active enabled\" tabindex=\"0\"><div class=\" stepIcon-active\">1</div>上传文件</li><li class=\"wizardstepLinks last enabled\" tabindex=\"0\"><div class=\" stepIcon-nonVisited\">2</div>文件校验</li></ul></div>";
	var act1="<div style=\"position:relative;top:2px;left:1%\"><ul id=\"stripTabWrap\" class=\"header-strip\"><li class=\"wizardstepLinks first active enabled\" tabindex=\"0\"><div class=\" stepIcon-nonVisited\">1</div>上传文件</li><li class=\"wizardstepLinks last active\" tabindex=\"0\"><div class=\" stepIcon-active\">2</div>文件校验</li></ul></div>";
	 Ext.onReady(function() {
		updatesize();
		 Ext.EventManager.onWindowResize(function(){
			 updatesize();
			 Ext.getCmp("footerpanel").setWidth(winWidth*0.98);
			 Ext.getCmp("content_display").setHeight(winHeight-150);
			 Ext.getCmp("multidoc_panel").setHeight(winHeight-160);
			 Ext.getCmp("msggrid").setWidth(winWidth);
			 Ext.getCmp("msggrid").setHeight(winHeight-250);
		 });
		var title = new Ext.Toolbar({
			renderTo:Ext.getBody(),
			id:'title',
			width:'100%',
			height: 35,
			items: [
				{
					xtype:'displayfield',
					style:'position:relative;left:2px;color:#636363;font-size:14px;top:4px',
					value : '批量上传文档'
				}
			]
		});
	    var tb1 = new Ext.Container({
			renderTo:Ext.getBody(),
			width:'98%',
			height:40,
			html:"<div id=\"multidoc_tab\">"+act0+"</div>"
	    });	 

		var tb2 = new Ext.Container({
			renderTo:Ext.getBody(),
			width:'98%',
			id:'content_display',
			height:winHeight-150,
			style:'border:1px solid #c0c0c0;margin:0 auto;overflow:hidden'
	   });	 
		  var tabs = new Ext.TabPanel({
			    id:'multidoc_panel',
				renderTo:'content_display',
				activeTab: 0,
				headerCfg:{style:'display:none'},
				width:'99.5%',
				style:'padding:10px 5px',
				height:winHeight-160,
				items: [
				{
					title: '上传文件',
					items:[
							{
								 xtype:'form', 
								 layout:'table',
								 enctype : 'multipart/form-data', // 把文件以二进制流的方式传递到服务器
								 fileUpload : true,
								 id:'upload_multidocform',									 
								 items:[
									{    
										xtype: 'displayfield',
										value:'上传文件:'
									},
									{    
										xtype: 'textfield',
										width:200,
										id:'upload_multidoc',
										name: 'uploadfile',    
										inputType: 'file',  
										regex:/\.([zZ][iI][pP]){1}$/,
										regexText : '请选择zip类型的文件',
										saTarget:'unber',
									},{
										xtype:'button',
										cls:'uploadbtn_cls',
										text:'上传',
										style:'padding-left:10px;width:40px',
										handler:function(){
											var form=Ext.getCmp("upload_multidocform").getForm();
											Ext.getCmp('nbtn').setDisabled(true);
											form.submit({
												url: '/Windchill/ptc1/line/uploadzip.do?type=dwg&allowtype=dwg&date='+new Date(),
												method: "POST",
												success: function (form, action) {
													var text=action.response.responseText;
													var jsonStr=text.replace("{success:true,msg:'","").replace("'}","");
													console.log(jsonStr);
													var data=eval("("+jsonStr+")");
													var msgstore=Ext.getCmp("msggrid").store;
													msgstore.loadData(data);
													if(msgstore.getCount()>0){
														Ext.getCmp('nbtn').setDisabled(false);
														alert("上传文件成功");
													}else{
														Ext.getCmp('nbtn').setDisabled(true);
														alert("从压缩包中获取不到信息");
													}
												}
											});
										}
									}
								 ]
						  }       
						
					]
					
				 },
				 {
					title: '文件校验',
					items:[
							{
								 xtype:'container',
								 items:[
								        {
								        	xtype:'container',
									        style:'font-weight:bold;font-size:15px',
									        html:'验证信息'
								        },
								        {
								        	xtype:'container',
								        	scrollable:true,
								        	//html:'<div class="msginfo_div"><ul><li><span>111</span><span>cc.gif</span></ul></div>'
								        	items:[
												new Ext.grid.GridPanel({
													id:'msggrid',
													store: new Ext.data.ArrayStore({
														id:'msgstore',
														fields:[{name: 'number'},{name: 'name'},{name:'msg'},{name:'status'}],
														data:[]
													}),
													colModel: new Ext.grid.ColumnModel({
														columns: [
															{
															  header: '编号', sortable: true, dataIndex: 'number',
															  renderer:function(value, cellmeta, record, rowIndex, columnIndex, store){
																  return "<img src=\""+website+"/Windchill/netmarkets/images/doc_document.gif\"/><span style=\"position:relative;top:-3px;left:2px\">"+value+"</span>";
															  }
															},
															{
															  header: '文件名', dataIndex: 'name',sortable: true
															},
															{
															  header: '信息', dataIndex: 'msg',sortable: true
															},
															{
															  header: '状态', dataIndex: 'status',sortable: true,
															  renderer:function(value, cellmeta, record, rowIndex, columnIndex, store){
																  if(!value){
																	  return "<img style=\"width:12px\" src=\""+website+"/Windchill/netmarkets/javascript/ext/resources/images/default/window/icon-error.gif\"/>";
																  }else{
																	  return "验证通过";
																  }
															  }
															}
															
														],
													}),
													viewConfig: {
														forceFit: true
													},
													sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
													scrollable:true,
													width: winWidth,
													height: winHeight-250
												 })
													]
								        }
								 ]
							}       
						
					]
						
				}
				]
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
									disabled:true,
									id:'nbtn',
									handler:function(){
										setActiveTab(1);
										Ext.getCmp('bbtn').setDisabled(false);
										Ext.getCmp('nbtn').setDisabled(true);
										var store=Ext.getCmp("msggrid").store;
										var items=store.data.items;
										var status=true;
										for(var i=0;i<items.size();i++){
											status=items[i].data.status;
											status=status&&items[i].data.status;
										}
										if(status){
											Ext.getCmp('fbtn').setDisabled(false);
										}else{
											Ext.getCmp('fbtn').setDisabled(true);
										}
									}
								},
								{
									xtype : 'button',
									style:'margin-left:10px',
									text : '完成(F)',
									disabled:true,
									id:'fbtn',
									handler:function(){
										var store=Ext.getCmp("msggrid").store;
										var items=store.data.items;
										var array=new Array();
										for(var i=0;i<items.size();i++){
											if(!items[i].data.status){
												alert("上传文件有错误信息未解决,不能上传!");
												return;
											}
											var arr={
													number:items[i].data.number,
													name:items[i].data.name
											}
											array.push(arr);
										}
										var str=JSON.stringify(array);
										console.log(str);
										var mk=new Ext.LoadMask(document.body,{msg:'正在创建。。。,请稍后',removeMask:true});
									     mk.show();
										Ext.Ajax.request({
											url : "/Windchill/ptc1/line/createDoc.do",
											params : {docinfo:str},
											success : function(response,opts){
												mk.hide();
												var msg=JSON.parse(response.responseText).msg;
												if(msg==""){
													if(window.opener){
														window.opener.location.reload();
													}
													window.close();
												  }else{
													  alert(msg);
												  }
											},
											failure : function(){
												 mk.hide();
												 alert("请求失败!");
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
		  document.getElementById("multidoc_tab").innerHTML=tab;
		  Ext.getCmp("multidoc_panel").setActiveTab(index);
		  
	  }
	 function setClassificationAttributes(s){
	 }
	</script>
   </head>
 <%@include file="/netmarkets/jsp/util/end.jspf"%>
