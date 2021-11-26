 <%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
 
 <%@ include file="/netmarkets/jsp/components/beginWizard.jspf" %> 
 <%@ page language="Java" pageEncoding="UTF-8"%>
 <link rel="stylesheet" href="netmarkets/jsp/catl/ri/A/css/jquery.bigautocomplete.css?a=2" type="text/css">
 <link rel="stylesheet" href="netmarkets/jsp/catl/ri/A/css/common.css?a=16" type="text/css">

 <head>
 <style>
	input[readonly='readonly']{
	    background-color: #FFFFFF !important;
	}
	td image{
	  cursor:pointer;
	}
	.symbol{
	background:url(/Windchill/netmarkets/jsp/catl/ri/A/image/symbol_4.png) no-repeat;
	background-position-x:100%; 
	cursor:pointer;
	background-size:7px;
}
	td{
	 padding:1px 2px;
	}
	td input{
	   color:blue;
	}
	td select{
	   color:blue;
	}
    body{
    overflow-x:hidden;
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
	.grid_cls .x-grid3-row td.x-grid3-cell, .x-grid3-summary-row td.x-grid3-cell{
	 line-height:30px !important;
	}
	.x-editor {
    padding: 10px 0px;
   }
   .x-tab-panel-body > div {
	padding: 0px !important; 
	margin: 0px !important; 
   }
   #batterybody{
   height:600px;
   background:url("netmarkets/jsp/catl/ri/A/image/loadding.gif") no-repeat 50% 50%; 
   background-size:30px;
   }
   .hiderow{
	display:none;
   }
   </style>
	<script type="text/javascript">
	PTC.navigation.loadScript('netmarkets/jsp/catl/ri/A/js/jquery-1.8.2.min.js',true);
	PTC.navigation.loadScript('netmarkets/jsp/catl/ri/A/js/jquery.bigautocomplete.js?a=2',true);
	PTC.navigation.loadScript('netmarkets/jsp/catl/ri/A/cache.js',true);
	PTC.navigation.loadScript('netmarkets/jsp/catl/ri/A/self.js',true);
	PTC.navigation.loadScript('netmarkets/jsp/catl/ri/A/docattr.js',true);
	PTC.navigation.loadScript('netmarkets/jsp/catl/ri/A/selectmaterial.js',true);
	PTC.navigation.loadScript('netmarkets/jsp/catl/ri/A/mechanicalasm.js',true);
	PTC.navigation.loadScript('netmarkets/jsp/catl/ri/A/inputparams.js',true);
	PTC.navigation.loadScript('netmarkets/jsp/catl/ri/A/diecut.js',true);
	PTC.navigation.loadScript('netmarkets/jsp/catl/ri/A/bom.js',true);
	PTC.navigation.loadScript('netmarkets/jsp/catl/ri/A/cspace.js',true);
	PTC.navigation.loadScript('netmarkets/jsp/catl/ri/A/overhang.js',true);
	PTC.navigation.loadScript('netmarkets/jsp/catl/ri/A/summary.js',true);
	PTC.navigation.loadScript('netmarkets/jsp/catl/ri/A/lugmolposition.js',true);
	PTC.navigation.loadScript('netmarkets/jsp/catl/ri/A/cellweight.js',true);
	$(document).attr('title','RI智能设计'); 
	var website='<%=request.getScheme()+"://"+request.getServerName()%>';
	var containerOid='<%=request.getParameter("ContainerOid")%>';
	var folderOid='<%=request.getParameter("oid")%>';
	var wtoid='<%=request.getParameter("oid")%>';
	if(wtoid.indexOf("WTDocument")>-1){
		var wtDocOid=folderOid;
		folderOid="";
		updateBattery();
	}else{
		wtoid="";
	}
	var winWidth=0,winHeight=0;
    Ext.onReady(function() {
    	 getisIE();
    	 loadSearchStore();
    	 initWindow();
		 updatesize();
		 Ext.QuickTips.init();
		 Ext.EventManager.onWindowResize(function(){
			 updatesize();
			 Ext.getCmp("footerpanel").setWidth(winWidth*0.98);
			 Ext.getCmp("footerpanel").doLayout();
			 Ext.getCmp("content_display").setHeight(winHeight-150);
			 Ext.getCmp("mathbattery_panel").setHeight(winHeight-160);
			 Ext.getCmp("selectmaterial_scroll").setHeight(winHeight-250);
			 Ext.getCmp("inputParams").setHeight(winHeight-160);
			
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
					value : '计算电芯参数'
				}
			]
		});
	    var tb1 = new Ext.Container({
	    	renderTo:Ext.getBody(),
			width:'98%',
			height:40,
			html:"<div id=\"mathbattery_tab\">"+ract+"</div>"
	    });	 

		var tb2 = new Ext.Container({
			renderTo:Ext.getBody(),
			width:'98%',
			id:'content_display',
			height:winHeight-150,
			style:'border:1px solid #c0c0c0;margin:0 auto;'
	   });	 
		
		  var tabs = new Ext.TabPanel({
			    id:'mathbattery_panel',
				renderTo:'content_display',
				activeTab: 0,
				headerCfg:{style:'display:none'},
				width:'100%',
				height:'100%',
				height:winHeight-160,
				items: [
				  docattr,
                  selectmaterial,
                  mechanicalasm,
                  inputparams,
                  diecut,
                  overhang,
                  summary,
                  bom,
                  cspace,
                  lugmolposition,
                  cellweight
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
					id:'needfill',
					//width:winWidth*0.62,
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
										toActiveindex(activeindex-1);
									}
								},
								{
									xtype : 'button',
									style:'margin-left:10px',
									text : '下一页(N)',
									id:'nbtn',
									handler:function(){
										toActiveindex(activeindex+1);
									}
								},
								{
									xtype : 'button',
									style:'margin-left:10px',
									text : '计算(M)',
									tooltip:'刷新当前页面的计算',
									disabled:true,
									id:'mbtn',
									handler:function(btn){
										refreshDesign();
									}
								},
								{
									xtype : 'button',
									style:'margin-left:10px',
									id:'sbtn',
									disabled:true,
									text : '保存(S)',
									tooltip:'避免重启系统造成数据丢失',
									handler:function(){
	                                  saveDesign();
									},
									listeners:{
										render:function(btn){
											 if(wtoid!=""){
												 btn.hide();
											 }
										}
									}
									
								},
								{
									xtype : 'button',
									style:'margin-left:10px',
									id:'ebtn',
									disabled:true,
									text : '导出(E)',
									handler:function(){
										importFile();
										//exportIfConf();
									}
								},
								{
									xtype : 'button',
									style:'margin-left:10px',
									id:'fbtn',
									disabled:true,
									tooltip:'创建计算结果文档',
									text : '完成(F)',
									handler:function(){
										finishDesign();
									}
								},
								{
									xtype : 'button',
									style:'margin-left:10px',
									id:'cbtn',
									text : '关闭(C)',
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
	</script>
   </head>
   <body id="batterybody">
   </body>
 <%@include file="/netmarkets/jsp/util/end.jspf"%>
