var title=["文档属性","选择材料","机械件组合","设计界面","模切尺寸","Overhang","BOM","残空间计算","极耳错位","Cell Weight","设计输出","平均电压参考","0.04C三电极数据","DCR map","ALP map"];
var diecut_standard_id="A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,I,U,V,W,X,Y,Z,AA,AB,AC";
var standard_id="A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z";
var btnarray=["bbtn","nbtn","mbtn","sbtn","ebtn","fbtn","cbtn"];
var tabbtns=["nbtn,cbtn","bbtn,nbtn,mbtn,cbtn,sbtn,mbtn,fbtn,ebtn","bbtn,nbtn,cbtn,sbtn,mbtn,fbtn,ebtn","bbtn,nbtn,cbtn,sbtn,mbtn,fbtn,ebtn","bbtn,nbtn,cbtn,sbtn,mbtn,fbtn,ebtn","bbtn,nbtn,cbtn,sbtn,mbtn,fbtn,ebtn","bbtn,nbtn,cbtn,sbtn,mbtn,fbtn,ebtn","bbtn,nbtn,cbtn,sbtn,mbtn,fbtn,ebtn","bbtn,nbtn,cbtn,sbtn,mbtn,fbtn,ebtn","bbtn,nbtn,cbtn,sbtn,mbtn,fbtn,ebtn","bbtn,nbtn,cbtn,sbtn,mbtn,fbtn,ebtn","bbtn,nbtn,cbtn,sbtn,mbtn,fbtn,ebtn","bbtn,nbtn,cbtn,sbtn,mbtn,fbtn,ebtn","bbtn,nbtn,cbtn,sbtn,mbtn,fbtn,ebtn","bbtn,cbtn,mbtn,sbtn,fbtn,ebtn"];
var submitformids=["","selectmaterial","mechanicalasm","inputparams","diecut","overhang","bom","cspace","lugmolposition","cellweight","output","averagevoltage","trielectrode","dcrmap","alpmap"];
var fillmethodnames=["","fillMaterialCell","fillMechanicalCell","fillinputparamsCell","filldiecutCell","filloverhangCell","fillbomCell","fillcspaceCell","filllugmolpositionCell","fillcellweightCell","filloutputCell","fillaveragevoltageCell","filltrielectrodeCell","filldcrmapCell","fillalpmapCell"];
var matchersheetnames=["","09.材料数据库","10.机械件数据库","04.设计界面","11.模切尺寸","13.Overhang","08.BOM","12.残空间计算","14.极耳错位","16.Cell Weight","05.设计输出","02.平均电压参考","03.0.04C三电极数据","06.DCR map","07.ALP map"];
var fillids=new Array();
var changeactive_index=0;
var focusvalue="";
var material_searchnamestore="";
var material_searchnumberstore="";
var materialpn_searchnumberstore="";
var recipenumber_searchnumberstore="";
var asmpn_searchstore="";
var activeindex=0;
var minactiveindex=0;
var maxactiveindex=title.length-1;
var startbaseact="<div style=\"position:relative;top:2px;left:1%\"><ul id=\"stripTabWrap\" class=\"header-strip\">";
var endbaseact="</ul></div>";
var act="";
var designname="";
var selectmaterial_render=false;
var mechanicalasm_render=false;
var inputparams_render=false;
var bom_render=false;
var cellweight_render=false;
var cspace_render=false;
var diecut_render=false;
var lugmolposition_render=false;
var overhang_render=false;
var summary_render=false;
var output_render=false;
var averagevoltage_render=false;
var trielectrode_render=false;
var dcrmap_render=false;
var alpmap_render=false;

var selectmaterialjson="",mechanicalasmjson="",inputparamsjson="",diecutjson="",overhangjson="",
summaryjson="",bomjson="",cspacejson="",lugmolpositionjson="",cellweightjson="",outputjson="",averagevoltagejson="",
trielectrodejson="",dcrmapjson="",alpmapjson="";

var isIE=false;
var mainB11='0';
var current_name="";
var current_level="";
var current_remark="";
var old_name="";
var old_level="";
var old_remark="";
var tab_next_active=0;
var formcheck=true;
for(var i=0;i<title.length;i++){
	var cact="";
	if(i==0){
		cact="<li class=\"wizardstepLinks first active enabled\" onclick=\"toActiveindex("+i+")\"><div id=\"acttab"+i+"\" class=\"stepIcon-active\">1</div>"+title[i]+"</li>";
	}else if(i==title.length-1){
		cact="<li class=\"wizardstepLinks last enabled\" onclick=\"toActiveindex("+i+")\"><div id=\"acttab"+i+"\" class=\"stepIcon-nonVisited\">"+(i+1)+"</div>"+title[i]+"</li>";
	}else{
		cact="<li class=\"wizardstepLinks  enabled\" onclick=\"toActiveindex("+i+")\"><div  id=\"acttab"+i+"\" class=\"stepIcon-nonVisited\">"+(i+1)+"</div>"+title[i]+"</li>";
	}
	act=act+cact;
}
var ract=startbaseact+act+endbaseact;
function toActiveindex(next_activeindex){
	if(next_activeindex==0){
		old_name=current_name;
		old_level=current_level;
		old_remark=current_remark;
		console.log(old_name+old_level+old_remark)
	}
	tab_next_active=next_activeindex;
	if(activeindex==0){
		var values=Ext.getCmp("docattr").getForm().getValues();
		if(values.battery_name.trim()==""){
			alert("请填写文档名称");
		}else{
			var current_designname=values.battery_name+values.battery_level+values.battery_nameremark;
			if(current_designname!=designname){
				var mk=new Ext.LoadMask(document.body,{msg:'正在处理,请稍候',removeMask:true});
			    mk.show();
				Ext.Ajax.request({
					disableCaching : false,
					async:false,
					url:'/Windchill/ptc1/riB/createDesignExcel.do',
					params:{wtDocOid:wtoid,oldname:old_name,oldlevel:old_level,oldremark:old_remark,name:values.battery_name,level:values.battery_level,remark:values.battery_nameremark,date:new Date()},
					success:function(response, opts){
						 mk.hide();
						 var msg=JSON.parse(response.responseText).msg;
						if(msg==""||msg=="已存在"){
							old_name=current_name;
							old_level=current_level;
							old_remark=current_remark;
							current_name=values.battery_name;
							current_level=values.battery_level;
							current_remark=values.battery_nameremark;
							submitform(next_activeindex);
						}else{
							alert(msg);
						}
						
					},
					failure:function(){
					    mk.hide();
						alert("请求失败");
					}
				});
				designname=current_designname;
			}else{
				submitform(next_activeindex);
			}
		}
	}else{
		submitform(next_activeindex);
	}
	
}

function showbtn(btns){
	for(var i=0;i<btnarray.length;i++){
		var btn=btnarray[i];
		if(btns.indexOf(btn)>-1){
			Ext.getCmp(btn).setDisabled(false);
		}else{
			Ext.getCmp(btn).setDisabled(true);
		}
	}
}


function submitform(next_activeindex){
	var id=submitformids[activeindex];
	var flag=isFormChange();
	if(id!=""&&flag){
		var values=Ext.getCmp(id).getForm().getValues();
		console.log("提交表单"+id);
		console.log(values);
		setLocal(id,values);
		var reqdata=JSON.stringify(values);
		Ext.Ajax.request({
			disableCaching : false,
			async:true,
			url : "/Windchill/ptc1/riB/submitForm.do",
			params : {
				wtDocOid:wtoid,
				name:current_name,
				level:current_level,
				remark:current_remark,
				jsonstr : reqdata,
				sheetname:matchersheetnames[activeindex],
				formcheck:true
			},
			success : function(response,opts) {
				 formcheck=true;
				 var msg=JSON.parse(response.responseText).msg;
				 if(msg!=""){
					 alert(msg);
				 }else{
					var methods=fillmethodnames[next_activeindex];
				    if(methods!=""){
				    	updateFormData(methods);
				    }
					successtotab(next_activeindex);
				 }
			}
		});
	}else{
		var methods=fillmethodnames[next_activeindex];
	    if(methods!=""){
	    	updateFormData(methods);
	    }
		successtotab(next_activeindex);
	}
}

function successtotab(next_activeindex){
	document.getElementById("acttab"+activeindex).setAttribute("class","stepIcon-nonVisited");
	var active_classattr=document.getElementById("acttab"+activeindex).parentNode.getAttribute("class");
	document.getElementById("acttab"+activeindex).parentNode.setAttribute("class",active_classattr.replace(" active",""));
	
	var next_active_classattr=document.getElementById("acttab"+next_activeindex).parentNode.getAttribute("class");
	document.getElementById("acttab"+next_activeindex).setAttribute("class","stepIcon-active");
	document.getElementById("acttab"+next_activeindex).parentNode.setAttribute("class",next_active_classattr+" active");
	activeindex=next_activeindex;
	Ext.getCmp("mathbattery_panel").setActiveTab(next_activeindex);
	var btns=tabbtns[next_activeindex];
	showbtn(btns);
}

function loadSearchStore(){
	Ext.Ajax.request({
		disableCaching : false,
		url : "/Windchill/ptc1/riB/getSearchJson.do",
		params : {
			type:'name'
		},
		success : function(response,opts) {
			  var data=JSON.parse(response.responseText).data;
			  material_searchnamestore=data;
		}
	});
	Ext.Ajax.request({
		disableCaching : false,
		url : "/Windchill/ptc1/riB/getSearchJson.do",
		params : {
			type:'number'
		},
		success : function(response,opts) {
			  var data=JSON.parse(response.responseText).data;
			  console.log(data);
			  material_searchnumberstore=data;
		}
	});
	Ext.Ajax.request({
		disableCaching : false,
		url : "/Windchill/ptc1/riB/getMaterialPNJson.do",
		params:{containerOid:containerOid},
		success : function(response,opts) {
			  var data=JSON.parse(response.responseText).data;
			  materialpn_searchnumberstore=data[0].pn;
			  recipenumber_searchnumberstore=data[0].recipenumber;
		}
	});
	
	Ext.Ajax.request({
		disableCaching : false,
		url : "/Windchill/ptc1/riB/getAsmPNJson.do",
		success : function(response,opts) {
			  var data=JSON.parse(response.responseText).data;
			  asmpn_searchstore=data;
		}
	});
}


function updateBattery(){
	var mk=new Ext.LoadMask(document.body,{msg:'正在加载数据,请稍候',removeMask:true});
    mk.show();
	Ext.Ajax.request({
		url:'/Windchill/ptc1/riB/updateBattery.do',
		params:{wtDocOid:wtDocOid},
		success:function(response,opts){
			mk.hide();
			var data=JSON.parse(response.responseText);
			if(data.msg!=''){
				alert(data.msg);
			}else{
				var doc=data.data;
				old_name=doc[0];
				old_level=doc[1];
				old_remark=doc[2];
				var checkmsg=doc[3];
				Ext.getCmp('battery_name').setValue(doc[0]);
				Ext.getCmp('battery_level').setValue(doc[1]);
				Ext.getCmp('battery_nameremark').setValue(doc[2]);
				if(checkmsg!=""){
					showWarningMessage("和当前设计表版本不一致,可能导致页面数据不正确。");
				}
			}
		},
		failure:function(){
			 mk.hide();
			alert("请求失败");
		}
	});
}


function isFormChange(){
	var flag1=false,flag2=false,flag3=false,flag4=false,flag5=false,flag6=false,flag7=false,flag8=false,flag9=false,flag10=false,flag11=false,flag12=false,flag13=false,flag14=false,flag15=false;
	if(selectmaterial_render){
		var cselectmaterialjson=JSON.stringify(selectmaterial.getForm().getValues());
		flag1=!(cselectmaterialjson==selectmaterialjson);
		selectmaterialjson=cselectmaterialjson;
	}
	if(mechanicalasm_render){
		var cmechanicalasmjson=JSON.stringify(mechanicalasm.getForm().getValues());
		flag2=!(cmechanicalasmjson==mechanicalasmjson);
		mechanicalasmjson=cmechanicalasmjson;
	}
	if(inputparams_render){
		var cinputparamsjson=JSON.stringify(inputparams.getForm().getValues());
		flag3=!(cinputparamsjson==inputparamsjson);
		inputparamsjson=cinputparamsjson;
	}
	if(diecut_render){
		var cdiecutjson=JSON.stringify(diecut.getForm().getValues());
		flag4=!(cdiecutjson==diecutjson);
		diecutjson=cdiecutjson;
	}
	if(overhang_render){
		var coverhangjson=JSON.stringify(overhang.getForm().getValues());
		flag5=!(coverhangjson==overhangjson);
		overhangjson=coverhangjson;
	}
	if(summary_render){
		var csummaryjson=JSON.stringify(summary.getForm().getValues());
		flag6=!(csummaryjson==summaryjson);
		summaryjson=csummaryjson;
	}
	if(bom_render){
		var cbomjson=JSON.stringify(bom.getForm().getValues());
		flag7=!(cbomjson==bomjson);
		bomjson=cbomjson;
	}
	if(cspace_render){
		var ccspacejson=JSON.stringify(cspace.getForm().getValues());
		flag8=!(ccspacejson==cspacejson);
		cspacejson=ccspacejson;
	}
	if(lugmolposition_render){
		var clugmolpositionjson=JSON.stringify(lugmolposition.getForm().getValues());
		flag9=!(clugmolpositionjson==lugmolpositionjson);
		lugmolpositionjson=clugmolpositionjson;
	}
	if(cellweight_render){
		var ccellweightjson=JSON.stringify(cellweight.getForm().getValues());
		flag10=!(ccellweightjson==cellweightjson);
		cellweightjson=ccellweightjson;
	}
	if(output_render){
		var coutputjson=JSON.stringify(output.getForm().getValues());
		flag11=!(coutputjson==outputjson);
		outputjson=coutputjson;
	}
	if(averagevoltage_render){
		var caveragevoltagejson=JSON.stringify(averagevoltage.getForm().getValues());
		flag12=!(caveragevoltagejson==averagevoltagejson);
		averagevoltagejson=caveragevoltagejson;
	}
	if(trielectrode_render){
		var ctrielectrodejson=JSON.stringify(trielectrode.getForm().getValues());
		flag13=!(ctrielectrodejson==trielectrodejson);
		trielectrodejson=ctrielectrodejson;
	}
	if(dcrmap_render){
		var cdcrmapjson=JSON.stringify(dcrmap.getForm().getValues());
		flag14=!(cdcrmapjson==dcrmapjson);
		dcrmapjson=cdcrmapjson;
	}
	if(alpmap_render){
		var calpmapjson=JSON.stringify(alpmap.getForm().getValues());
		flag15=!(calpmapjson==alpmapjson);
		alpmapjson=calpmapjson;
	}
	console.log("formchange="+(flag1||flag2||flag3||flag4||flag5||flag6||flag7||flag8||flag9||flag10));
	return flag1||flag2||flag3||flag4||flag5||flag6||flag7||flag8||flag9||flag10||flag11||flag12||flag13||flag14||flag15;
}

function updateFormData(methods){
	console.log("填写表单"+methods);
	eval(methods+"()");
}


function fillinputparamsCell(){
	if(!inputparams_render){
		return;
	}
	Ext.Ajax.request({
		url:'/Windchill/ptc1/riB/getPageJson.do',
		params:{wtDocOid:wtoid,sheetname:matchersheetnames[tab_next_active],name:current_name,level:current_level,remark:current_remark,date:new Date()},
		success:function(response,opts){
			var data=JSON.parse(response.responseText);
			handlefilldata(data,"input_inputparams_");
		},
		failure:function(){
			alert('请求失败');
		}
	});
}

function fillMechanicalCell(){
	if(!mechanicalasm_render){
		return;
	}
	Ext.Ajax.request({
		url:'/Windchill/ptc1/riB/getPageJson.do',
		params:{sheetname:matchersheetnames[tab_next_active],name:current_name,level:current_level,remark:current_remark,wtDocOid:wtDocOid,date:new Date()},
		success:function(response,opts){
			var data=JSON.parse(response.responseText);
			handlefilldata(data,"input_mechanicalasm_");
		},
		failure:function(){
			alert('请求失败');
		}
	});
}

function fillMaterialCell(){
	if(!selectmaterial_render){
		return;
	}
	Ext.Ajax.request({
		url:'/Windchill/ptc1/riB/getPageJson.do',
		params:{wtDocOid:wtoid,sheetname:matchersheetnames[tab_next_active],name:current_name,level:current_level,remark:current_remark,date:new Date()},
		success:function(response,opts){
			var data=JSON.parse(response.responseText);
			handlefilldata(data,"input_material_");
		},
		failure:function(){
			alert('请求失败');
		}
	});
}

function fillbomCell(){
	if(!bom_render){
		return;
	}
	//loadSummary("summary_bom_html");
	Ext.Ajax.request({
		url:'/Windchill/ptc1/riB/getPageJson.do',
		params:{sheetname:matchersheetnames[tab_next_active],name:current_name,level:current_level,remark:current_remark,wtDocOid:wtDocOid,date:new Date()},
		success:function(response,opts){
			var data=JSON.parse(response.responseText);
			handlefilldata(data,"input_bom_");
		},
		failure:function(){
			alert('请求失败');
		}
	});
}
function fillcspaceCell(){
	if(!cspace_render){
		return;
	}
	//loadSummary("summary_cspace_html");
	Ext.Ajax.request({
		url:'/Windchill/ptc1/riB/getPageJson.do',
		params:{sheetname:matchersheetnames[tab_next_active],name:current_name,level:current_level,remark:current_remark,wtDocOid:wtDocOid,date:new Date()},
		success:function(response,opts){
			var data=JSON.parse(response.responseText);
			handlefilldata(data,"input_cspace_");
		},
		failure:function(){
			alert('请求失败');
		}
	});
}
function filldiecutCell(){
	if(!diecut_render){
		return;
	}
	//loadSummary("summary_diecut_html");
	Ext.Ajax.request({
		url:'/Windchill/ptc1/riB/getPageJson.do',
		params:{sheetname:matchersheetnames[tab_next_active],name:current_name,level:current_level,remark:current_remark,wtDocOid:wtDocOid,date:new Date()},
		success:function(response,opts){
			var data=JSON.parse(response.responseText);
			handlefilldata(data,"input_diecut_");
		},
		failure:function(){
			alert('请求失败');
		}
	});
}

function filloverhangCell(){
	if(!overhang_render){
		return;
	}
    //loadSummary("summary_overhang_html");
	Ext.Ajax.request({
		url:'/Windchill/ptc1/riB/getPageJson.do',
		params:{wtDocOid:wtoid,sheetname:matchersheetnames[tab_next_active],name:current_name,level:current_level,remark:current_remark,date:new Date()},
		success:function(response,opts){
			var data=JSON.parse(response.responseText);
			handlefillpicdata(data, "input_overhang_","overhang_pic")
		},
		failure:function(){
			alert('请求失败');
		}
	});
	
}

function filllugmolpositionCell(){
	if(!lugmolposition_render){
		return;
	}
	//loadSummary("summary_lugmolposition_html");
	Ext.Ajax.request({
		url:'/Windchill/ptc1/riB/getPageJson.do',
		params:{wtDocOid:wtoid,sheetname:matchersheetnames[tab_next_active],name:current_name,level:current_level,remark:current_remark,date:new Date()},
		success:function(response,opts){
			var data=JSON.parse(response.responseText);
			handlefillpicdata(data, "input_lugmolposition_","lugmolposition_pic")
		},
		failure:function(){
			alert('请求失败');
		}
	});
}

function fillcellweightCell(){
	if(!cellweight_render){
		return;
	}
	//loadSummary("summary_cellweight_html");
	Ext.Ajax.request({
		url:'/Windchill/ptc1/riB/getPageJson.do',
		params:{sheetname:matchersheetnames[tab_next_active],name:current_name,level:current_level,remark:current_remark,wtDocOid:wtDocOid,date:new Date()},
		success:function(response,opts){
			var data=JSON.parse(response.responseText);
			handlefillpicdata(data, "input_cellweight_","cellweight_pic")
		},
		failure:function(){
			alert('请求失败');
		}
	});
}

function filloutputCell(){
	if(!output_render){
		return;
	}
	Ext.Ajax.request({
		url:'/Windchill/ptc1/riB/getPageJson.do',
		params:{wtDocOid:wtoid,sheetname:matchersheetnames[tab_next_active],name:current_name,level:current_level,remark:current_remark,date:new Date()},
		success:function(response,opts){
			var data=JSON.parse(response.responseText);
			handlefilldata(data,"input_output_");
		},
		failure:function(){
			alert('请求失败');
		}
	});
}

function fillaveragevoltageCell(){
	if(!averagevoltage_render){
		return;
	}
	Ext.Ajax.request({
		url:'/Windchill/ptc1/riB/getPageJson.do',
		params:{wtDocOid:wtoid,sheetname:matchersheetnames[tab_next_active],name:current_name,level:current_level,remark:current_remark,date:new Date()},
		success:function(response,opts){
			var data=JSON.parse(response.responseText);
			handlefilldata(data,"input_averagevoltage_");
		},
		failure:function(){
			alert('请求失败');
		}
	});
}

function filltrielectrodeCell(){
	if(!trielectrode_render){
		return;
	}
	Ext.Ajax.request({
		url:'/Windchill/ptc1/riB/getPageJson.do',
		params:{wtDocOid:wtoid,sheetname:matchersheetnames[tab_next_active],name:current_name,level:current_level,remark:current_remark,date:new Date()},
		success:function(response,opts){
			var data=JSON.parse(response.responseText);
			handlefilldata(data,"input_trielectrode_");
		},
		failure:function(){
			alert('请求失败');
		}
	});
}

function filldcrmapCell(){
	if(!dcrmap_render){
		return;
	}
	Ext.Ajax.request({
		url:'/Windchill/ptc1/riB/getPageJson.do',
		params:{wtDocOid:wtoid,sheetname:matchersheetnames[tab_next_active],name:current_name,level:current_level,remark:current_remark,date:new Date()},
		success:function(response,opts){
			var data=JSON.parse(response.responseText);
			handlefilldata(data,"input_dcrmap_");
		},
		failure:function(){
			alert('请求失败');
		}
	});
}

function fillalpmapCell(){
	if(!alpmap_render){
		return;
	}
	Ext.Ajax.request({
		url:'/Windchill/ptc1/riB/getPageJson.do',
		params:{wtDocOid:wtoid,sheetname:matchersheetnames[tab_next_active],name:current_name,level:current_level,remark:current_remark,date:new Date()},
		success:function(response,opts){
			var data=JSON.parse(response.responseText);
			handlefilldata(data,"input_alpmap_");
		},
		failure:function(){
			alert('请求失败');
		}
	});
}

function saveDesign(){
	var id=submitformids[activeindex];
	var values=Ext.getCmp(id).getForm().getValues();
	console.log(values);
	var reqdata=JSON.stringify(values);
	var mk=new Ext.LoadMask(document.body,{msg:'正在保存,请稍候',removeMask:true});
    mk.show();
	Ext.Ajax.request({
		disableCaching : false,
		async:true,
		url : "/Windchill/ptc1/riB/saveDesign.do",
		params : {
			wtDocOid:wtoid,
			name:current_name,
			level:current_level,
			remark:current_remark,
			jsonstr : reqdata,
			sheetname:matchersheetnames[activeindex]
		},
		success : function(response,opts) {
			mk.hide();
			 var msg=JSON.parse(response.responseText).msg;
			 if(msg!=""){
				 alert(msg);
			 }else{
				 showMessage("保存成功");
			 }
		},
		failure:function(){
			mk.hide();
			alert("请求失败");
		}
	});
}

function refreshDesign(){
	var id=submitformids[activeindex];
	var flag=isFormChange();
	if(id!=""&&flag){
		var values=Ext.getCmp(id).getForm().getValues();
		console.log("提交表单"+id);
		console.log(values);
		setLocal(id,values);
		var reqdata=JSON.stringify(values);
		Ext.Ajax.request({
			disableCaching : false,
			async:true,
			url : "/Windchill/ptc1/riB/submitForm.do",
			params : {
				wtDocOid:wtoid,
				name:current_name,
				level:current_level,
				remark:current_remark,
				jsonstr : reqdata,
				sheetname:matchersheetnames[activeindex],
				formcheck:false
			},
			success : function(response,opts) {
				 var msg=JSON.parse(response.responseText).msg;
				 if(msg!=""){
					 alert(msg);
				 }else{
					 var methods=fillmethodnames[activeindex];
					    if(methods!=""){
					    	updateFormData(methods);
					    }
				 }
			},
			failure:function(){
				 alert("请求失败");
			}
		});
	}
}

var checkremark="";
function finishDesign(){
		 Ext.Msg.buttonText.ok="确认";
		 Ext.Msg.buttonText.cancel="取消";
		 console.log(Ext.Msg);
		 if(checkremark!=""||wtoid==""){
			 finishForm(); 
		 }else{
			 Ext.Msg.prompt("备注", "记录下此版本信息（非必填）", function (btn, cremark) { 
				  if (btn =="ok") {
					  checkremark=cremark;
					  finishForm();
				  }  
		     }); 
		 }
		
}

function finishForm(){
	   var id=submitformids[activeindex];
		var values=Ext.getCmp(id).getForm().getValues();
		var reqdata=JSON.stringify(values);
	   var tk=new Ext.LoadMask(document.body,{msg:'正在验证表单,请稍候',removeMask:true});
       tk.show();
	   Ext.Ajax.request({
		disableCaching : false,
		async:true,
		url : "/Windchill/ptc1/riB/submitvalidateform.do",
		params : {
			wtDocOid:wtoid,
			name:current_name,
			description:checkremark,
			level:current_level,
			remark:current_remark,
			folderOid:folderOid,
			sheetname:matchersheetnames[activeindex],
			jsonstr : reqdata
		},
		success : function(resp,ops) {
			 tk.hide();
			 var validatemsg=JSON.parse(resp.responseText).msg;
			 if(validatemsg!=""){
				 alert(validatemsg);
			 }else{
					var mk=new Ext.LoadMask(document.body,{msg:'正在创建,请稍候',removeMask:true});
				    mk.show();
					Ext.Ajax.request({
						disableCaching : false,
						async:true,
						url : "/Windchill/ptc1/riB/outputDoc.do",
						params : {
							wtDocOid:wtoid,
							name:current_name,
							description:checkremark,
							level:current_level,
							remark:current_remark,
							folderOid:folderOid
						},
						success : function(response,opts) {
							 mk.hide();
							 var msg=JSON.parse(response.responseText).msg;
							 if(msg!=""){
								 alert(msg);
							 }else{
								if(window.opener){
									window.opener.location.reload();
								}
								window.close();
							 }
						},
						failure:function(){
							 mk.hide();
							 alert("请求失败");
						}
					});
			 }
		},
		failure:function(){
			  tk.hide();
			 alert("请求失败");
		}
	   });
}


function loadSummary(id){

}

function initWindow(){
	 Ext.Ajax.request({
			url:'/Windchill/ptc1/riB/getUserAllFileName.do',
			params:{data:new Date()},
			success:function(response, opts){
				 var resp=JSON.parse(response.responseText);
				 var msg=resp.msg;
				 var data=resp.data;
				 if(msg!=""){
					 alert(msg);
				 }else{
					 if(data.length>0){
						 loadSelectWindow(data);
					 }
				 }
			},
			failure:function(){
				alert("请求失败");
			}
	  });
}

function loadSelectWindow(data){
	if(wtoid==""){
	 var radiostr="{\"xtype\": \"radio\",\"height\":28,\"name\":\"name1\",\"boxLabel\": \"新建智能设计\"},";
	   for(var i=0;i<data.length;i++){
		   radiostr=radiostr+"{\"xtype\": \"radio\",\"height\":28,\"name\":\"name1\",\"boxLabel\": \""+data[i]+ "\"},";
		}
	   radiostr=radiostr.substring(0,radiostr.length-1);
	   var radios=eval("["+radiostr+"]");
	   var outerwin=new Ext.Window({  
		   width: 400,  
		   height: 300,  
		   id:'xlswindow',
		   title:'检测到您未完成的智能计算表,请选择!',
		   closable: true,  
		   draggable: false,  
		   resizable:false,
		   autoScroll:true,
		   closeAction: "hide",
		   modal:true,
		   padding:20,
		   items:[
			       {
					xtype : 'radiogroup',
					columns : 1,
					scrollable:true,
					flex : 1,
					id:'nofinishxlswindow',
					items:radios,
					listeners:{
						change:function(group,radio){
							var check=radio.boxLabel;
							if(check=="新建智能设计"){
								outerwin.close();
								return;
							}
							syncTemp(check);
							setUpdateInfo(check);
						}
					}
			       }
			]
	   });
	   outerwin.show();
	}
	
}

function syncTemp(check){
	var mk=new Ext.LoadMask(document.body,{msg:'正在处理,请稍候',removeMask:true});
    mk.show();
	Ext.Ajax.request({
		url:'/Windchill/ptc1/riB/syncTemp.do',
		params:{filename:check+".xlsx"},
		success:function(response,opts){
			 mk.hide();
			var msg=JSON.parse(response.responseText).msg;
			if(msg!=""){
				showWarningMessage(msg)
			}
		},
		failure:function(){
			 mk.hide();
			showWarningMessage('请求失败');
		}
	});
}
function setUpdateInfo(check){
	var infos=check.split("_XXX");
    if(infos.length==2){
    	var name=infos[0];
    	var remarkinfo=infos[1];
    	var level="RD";
    	if(remarkinfo.indexOf("_MD")>-1){
    		level="MD";
    	}else if(remarkinfo.indexOf("_SP")>-1){
    		level="SP";
        }
    	var remark=check.replace(name+"_XXX_"+level+"_","").replace(name+"_XXX_"+level,"");
    	Ext.getCmp("battery_name").setValue(name);
    	Ext.getCmp("battery_level").setValue(level);
    	Ext.getCmp('battery_nameremark').setValue(remark);
    	old_level=level;
    	old_name=name;
    	old_remark=remark;
    	Ext.getCmp("xlswindow").close();
    	console.log(name+"---"+level+"---"+remark);
    }else{
    	alert("文件名格式错误");
    }
}
function handlefilldata(data,id){
	if(data.msg!=""){
		alert(data.msg);
	}else{
        var data=data.data;
        for(var i=0;i<data.length;i++){
        	var cell=data[i];
        	var inputid=id+cell.region;
        	var value=cell.value;
        	var style=cell.style;
        	var color=cell.color;
        	var obj=document.getElementById(inputid);
        	if(obj){
        		obj.setAttribute("name",cell.region);
        		//console.log(cell.region+"---"+style);
        		var parentNode=obj.parentNode;
        		if(id=="input_material_"){
        			if(parentNode.getAttribute("search")=="number"){
        				setCellRemark(obj,value);
        			}
        		}
        		var selectval="";
        		if(style=="显示"){
        			obj.readOnly=true;
        		}else if(style=="下拉"){
        			var selectcell=obj.parentNode;
					if(selectcell){
						parentNode=selectcell;
						var selstart="<select  onchange=\""+id+"onchange('"+cell.region+"')\" id=\""+inputid+"\" name=\""+cell.region+"\">";
						var datalist=cell.datalist;
						var options="";
						if(datalist.length>0){
							for(var k=0;k<datalist.length;k++){
								 var datastr=datalist[k];
								 coptions="<option value=\""+datastr+"\">"+datastr+"</option>";
								 options=options+coptions;
						    }
						}
						var selend="</select>";
						selectcell.innerHTML=selstart+options+selend;
					}
        		}else if(style=="公式下拉"){
        			var selectcell=obj.parentNode;
					if(selectcell){
						parentNode=selectcell;
						var inpid=id+cell.region;
						var selid=id.replace("input","sel");
						var selvalid=selid+cell.region;
						var inputele="<input type=\"text\" readOnly=true id=\""+inpid+"\" style=\"width:80%;z-index:99;left:5px;top:0px;position:absolute\"/>";
						var selstart="<select  name=\""+cell.region+"\"  onchange=\""+selid+"onchange('"+cell.region+"')\" id=\""+selvalid+"\" style=\"LEFT: 0px; TOP: 0px; WIDTH: 100%;height:18px; POSITION: absolute;\">";
						if(isIE){
							inputele="<input type=\"text\" readOnly=true id=\""+inpid+"\" style=\"width:80%;height:18px;z-index:99;left:-27px;top:0px;position:relative\"/>";
						}
						var datalist=cell.datalist;
						var options="";
						if(datalist.length>0){
							for(var k=0;k<datalist.length;k++){
								 var datastr=datalist[k];
								 if(datastr.indexOf("###")>-1){
									 var dvalue=datastr.split("###")[0];
									 var dkey=datastr.split("###")[1];
									 console.log(dvalue+"--###--"+cell.displayvalue);
									 if(dvalue==cell.displayvalue){
										 selectval=dvalue;
									 }
									 coptions="<option value=\""+dkey+"\">"+dvalue+"</option>";
								 }else{
									 coptions="<option value=\""+datastr+"\">"+datastr+"</option>";
								 }
								 options=options+coptions;
						    }
						}
						var selend="</select>";
				 		selectcell.innerHTML=inputele+selstart+options+selend;
				  }
        		}else{
        			var active_id=submitformids[activeindex];
	    			var cache_array=getLocalArray(active_id,cell.region);
	    			var m=$("#"+inputid);
					if(cache_array!=undefined&&cache_array.length>0){
						var autoarray=new Array();
						for (var ai = 0; ai < cache_array.length; ai++) {
							var obj={title:cache_array[ai]};
							autoarray.push(obj);
						}
						m.bigAutocomplete({width:150,data:autoarray,callback:function(data){
							
						}});
					}
        		}
        		obj=document.getElementById(inputid);
        		if(id=="input_inputparams_"&&cell.region=="B11"){
        			console.log(mainB11);
        			if(mainB11!='0'){
        				if(value!=mainB11){
        					console.log("设置默认值");
        					checkDefault("04.设计界面", cell.region, value, id, false);
        				}
        			}
        			mainB11=value;
        		}
        		obj.value=handleValue(value);
    			obj.style.backgroundColor=color;
    			
    			var speical_sel_id=inputid.replace("input","sel");
    			var speical_sel=document.getElementById(speical_sel_id);
    			if(speical_sel){
    				speical_sel.style.backgroundColor=color;
    				speical_sel.value=selectval;
    				//console.log(selectval);
    				parentNode.style.position="relative";
    			}
    			if(parentNode.tagName=="TD"){
    				parentNode.style.backgroundColor=color;
    			}
        	}
        }
	}
}

function handlefillpicdata(data,iid,picid){
	if(data.msg!=""){
		alert(data.msg);
	}else{
        var adata=data.data;
        var picdiv=document.getElementById(picid);
        for(var i=0;i<adata.length;i++){
        	var cell=adata[i];
        	var id=picid+"_"+cell.region;
        	var value=cell.value;
        	var div=document.getElementById(id);
        	if(div){
        		div.innerHTML=handleValue(value);
        	}else{
        		var newdiv=document.createElement("div");
            	newdiv.id=id;
            	newdiv.innerHTML=handleValue(value);
            	picdiv.appendChild(newdiv);
        	}
        }
	}
	handlefilldata(data,iid);
}

function handleValue(value){
	 var regex = /^\d+(?=\.{0,1}\d+$|$)/;
	 value=value+"";
	if(value.indexOf("%")>-1){
		 value=value.replace("%","");
        if (regex.test(value)) { 
            value=parseFloat(value)+"%";
        } 
	}else if(value!=""){
	  if (regex.test(value)) { 
            value=parseFloat(value);
        } 
	}
	 return value;
}

function renderCell(id,txt){//除材料数据库页面的渲染
	document.getElementById(id+"_html").innerHTML=txt;
	for(var i=0;i<200;i++){
		var sid=standard_id;
		if(id=="diecut"){
			sid=diecut_standard_id;
		}
		var colarray=sid.split(",");
		for(var j=0;j<colarray.length;j++){
			var cell=document.getElementById(id+"_"+colarray[j]+i);
			if(cell){
			cell.innerHTML="<input type=\"text\"   id=\"input_"+id+"_"+colarray[j]+i+"\" />";
			}
		}
	}
	$("input").each(function () {//如果已经添加了事件，就不要再添加了  
        var e = $._data(this, "events");//是this 而不是 $(this)  
        if (e && e["change"]) {  
        } else {  
			$(this).change(function () {  
	       	  var id= $(this).attr("id");
	       	  if(id.indexOf("input_")>-1&&id.indexOf("material")==-1){
	       		 var name= $(this).attr("name");
	   		     changeactive_index=activeindex;
	   		     var idprefix=id.substring(0,id.lastIndexOf('_')+1);
	   		     checkDefault(matchersheetnames[activeindex],name,$(this).val(),idprefix);
	   		     setTimeout("refreshMathData('"+id+"','"+name+"');",200);
	       	  }
	       });  
        }  
    });  
		
}


function updatePageStyle(iid){
	var id=submitformids[activeindex];
	var flag=isFormChange();
	if(id!=""&&flag){
		var values=Ext.getCmp(id).getForm().getValues();
		setLocal(id,values);
		console.log(values);
		var reqdata=JSON.stringify(values);
		Ext.Ajax.request({
			disableCaching : false,
			async:true,
			url : "/Windchill/ptc1/riB/submitForm.do",
			params : {
				wtDocOid:wtoid,
				name:current_name,
				level:current_level,
				remark:current_remark,
				jsonstr : reqdata,
				sheetname:matchersheetnames[activeindex],
				formcheck:false
			},
			success : function(response,opts) {
				 var msg=JSON.parse(response.responseText).msg;
				 if(msg!=""){
					 alert(msg);
				 }else{
						Ext.Ajax.request({
							url:'/Windchill/ptc1/riB/getPageJson.do',
							params:{wtDocOid:wtoid,sheetname:matchersheetnames[tab_next_active],name:current_name,level:current_level,remark:current_remark,date:new Date()},
							success:function(response,opts){
								var data=JSON.parse(response.responseText);
								if(data.msg!=""){
									alert(data.msg);
								}else{
									console.log("更新页面样式");
							        var data=data.data;
							        for(var i=0;i<data.length;i++){
							        	var cell=data[i];
							        	var inputid=iid+cell.region;
							        	var value=cell.value;
							        	var style=cell.style;
							        	var color=cell.color;
							        	var obj=document.getElementById(inputid);
							        	if(obj){
							        		var parentNode=obj.parentNode;
							        		var selectval="";
							        		if(style=="公式下拉"){
							        			var selectcell=obj.parentNode;
												if(selectcell){
													parentNode=selectcell;
													var inpid=id+cell.region;
													var selid=id.replace("input","sel");
													var selvalid=selid+cell.region;
													var inputele="<input type=\"text\"  value=\""+value+"\" readOnly=true id=\""+inpid+"\" style=\"width:80%;z-index:99;left:5px;top:0px;position:absolute\"/>";
													var selstart="<select  name=\""+cell.region+"\" onchange=\""+selid+"onchange('"+cell.region+"')\" id=\""+selvalid+"\" style=\"LEFT: 0px; TOP: 0px; WIDTH: 100%;height:18px; POSITION: absolute;\">";
													if(isIE){
														inputele="<input type=\"text\"  value=\""+value+"\" readOnly=true id=\""+inpid+"\" style=\"width:80%;height:18px;z-index:99;left:-27px;top:0px;position:relative\"/>";
													}
													
													var datalist=cell.datalist;
													var options="";
													if(datalist.length>0){
														for(var k=0;k<datalist.length;k++){
															 var datastr=datalist[k];
															 if(datastr.indexOf("###")>-1){
																 var dvalue=datastr.split("###")[0];
																 var dkey=datastr.split("###")[1];
																 if(dvalue==value){
																	 selectval=dkey;
																 }
																 coptions="<option value=\""+dkey+"\">"+dvalue+"</option>";
															 }else{
																 coptions="<option value=\""+datastr+"\">"+datastr+"</option>";
															 }
															 options=options+coptions;
													    }
													}
													var selend="</select>";
											 		selectcell.innerHTML=inputele+selstart+options+selend;
											  }
							        		}
							        		obj=document.getElementById(inputid);
							    			obj.style.backgroundColor=color;
							    			//console.log(obj.style.backgroundColor);
							    			
							    			var speical_sel_id=inputid.replace("input","sel");
							    			var speical_sel=document.getElementById(speical_sel_id);
							    			if(speical_sel){
							    				speical_sel.style.backgroundColor=color;
							    				speical_sel.value=selectval;
							    				console.log(selectval);
							    				parentNode.style.position="relative";
							    			}
							    			if(parentNode.tagName=="TD"){
							    				parentNode.style.backgroundColor=color;
							    			}
							        	}
							        }
								}

							},
							failure:function(){
								alert('请求失败');
							}
						});
				 }
			}
		});
	}

}

function clearfilldata(){
	 var id=submitformids[activeindex];
     for(var i=0;i<200;i++){
		var sid=standard_id;
		if(id=="diecut"){
			sid=diecut_standard_id;
		}
		var colarray=sid.split(",");
		for(var j=0;j<colarray.length;j++){
			var cell=document.getElementById("input_"+id+"_"+colarray[j]+i);
			if(cell){
				cell.value="";
			}
			var sel=document.getElementById("sel_"+id+"_"+colarray[j]+i);
			if(sel){
				sel.value="";
			}
		}
	}
}

function refreshMathData(id,name){//刷新页面公式数据
	var value=$("#"+id).val();
	var cactiveindex=activeindex;
	if(changeactive_index!=0){
		cactiveindex=changeactive_index;
		changeactive_index=0;
	}
	if(id.indexOf("input_")>-1||id.indexOf("sel_")>-1){
		console.log("提交-"+id+"----"+name+"--"+value);
		Ext.Ajax.request({
			url:'/Windchill/ptc1/riB/getMathData.do',
			params:{wtDocOid:wtoid,region:name,value:value,sheetname:matchersheetnames[cactiveindex],name:current_name,level:current_level,remark:current_remark,date:new Date()},
			success:function(response,opts){
				console.log("更新计算数据");
				var data=JSON.parse(response.responseText);
				var inputid=id.replace(name,"");
				if(inputid.indexOf("cellweight")>-1){
					handlefillpicdata(data, inputid,"cellweight_pic",name)
				}else if(inputid.indexOf("overhang")>-1){
					handlefillpicdata(data, inputid,"overhang_pic",name)
				}else if(inputid.indexOf("lugmolposition")>-1){
					handlefillpicdata(data, inputid,"lugmolposition_pic",name)
				}else{
					handlefilldata(data,inputid,name);
				}
				
			},
			failure:function(){
				alert('请求失败');
			}
		});
	}
}

function exportSheet(uploadwin){
	var url = website+"/Windchill/ptc1/riB/exportSheet.do";
    var form = $("<form></form>").attr("action", url).attr("method", "post");
    form.append($("<input></input>").attr("type", "hidden").attr("name", "wtDocOid").attr("value", wtoid));
    form.append($("<input></input>").attr("type", "hidden").attr("name", "name").attr("value", current_name));
    form.append($("<input></input>").attr("type", "hidden").attr("name", "level").attr("value", current_level));
    form.append($("<input></input>").attr("type", "hidden").attr("name", "remark").attr("value", current_remark));
    form.append($("<input></input>").attr("type", "hidden").attr("name", "sheetname").attr("value", matchersheetnames[activeindex]));
    form.appendTo('body').submit().remove();
    uploadwin.close();
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
}

function importFile(){
	var uploadwin=new Ext.Window({  
		   width: 400,  
		   height: 140,  
		   id:'upwindow',
		   title:'导出',
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
				    id:'download_paramsConf',
					items:[{
						xtype: 'displayfield',
						value:'导出:'
					},{
						xtype:'button',
						//cls:'uploadbtn_cls',
						height:25,
						text:'下载',
						style:'padding-left:50px;width:40px',
						handler:function(){
							exportSheet(uploadwin);
						}
					}]
				}]
		   }]
	});
	uploadwin.show();
}

//判断是否是IE浏览器  
function getisIE(){  
   if(!!window.ActiveXObject || "ActiveXObject" in window){  
       isIE=true;  
   }else{  
	   isIE=false;
   }  
}  

function checkDefault(sheetname,region,value,idprefix,isconfirm){
	var tregion=sheetname+"!"+region+"|"+value;
	Ext.Ajax.request({
		disableCaching : false,
		async:false,
		url:'/Windchill/ptc1/riB/getDefaultConfig.do',
		params:{wtDocOid:wtoid,name:current_name,level:current_level,remark:current_remark,currentValue:tregion,date:new Date()},
		success:function(response, opts){
			var data=JSON.parse(response.responseText);
			if(data.msg!=''){
				alert(data.msg);
			}else{
				data=data.data;
				if(data!=''){
					if(isconfirm){
						Ext.Msg.buttonText.yes="是";
						Ext.Msg.buttonText.no="否";
						Ext.Msg.confirm('系统提示','是否填写默认值？',function(btn){
					        if(btn=='yes'){
					        	for(var i=0;i<data.length;i++){
					        		var cell=data[i];
					        		var obj=document.getElementById(idprefix+cell.region);
					        		if(obj){
					        			obj.value=cell.value;
					        		}
					        	}
					        }
						},this);
					}else{
						for(var i=0;i<data.length;i++){
			        		var cell=data[i];
			        		var obj=document.getElementById(idprefix+cell.region);
			        		if(obj){
			        			obj.value=cell.value;
			        		}
			        	}
					}
					
				}
			}
		},
		failure:function(){
			alert('请求失败');
		}
	});
}

function setCellRemark(cell,value){
	cell.setAttribute("ext:qtip","");
	if(value!=""){
		Ext.Ajax.request({
			disableCaching : false,
			async:true,
			url:'/Windchill/ptc1/riB/getRemarkByNumber.do',
			params:{value:value},
			success:function(response, opts){
				var data=JSON.parse(response.responseText);
				if(data.msg!=""){
					console.log(data.msg);
				}else{
					var speicalkey=data.data;
					cell.setAttribute("ext:qtip",speicalkey);
				}
				
			}
		});
		
	}
	
}
  