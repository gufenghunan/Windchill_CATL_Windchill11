var selectmaterial= new Ext.FormPanel({
    width : '100%',
    id:'selectmaterial',
    height: '100%',
    items:[
      {
		xtype: 'container',
		style:'margin-top:15px',
		layout:'table',
		width:'98%',
		items:[
			{
				xtype : 'displayfield',
				width:100,
				style:'margin-left:10px',
				value : '&nbsp;材料:'
			},
			{
				xtype : 'textfield',
				id:'material_number',
				width:180,
				listeners:{
					focus:function(){
					   var m=$("#material_number");
					   m.bigAutocomplete({width:180,data:materialpn_searchnumberstore,callback:function(data){
						  Ext.getCmp("material_number").setValue(data.title);
						  searchmaterialpn();
					   }});
				   }
				}
			},
  			{
  				xtype : 'button',
  				icon : 'netmarkets/images/search.gif',
				style : 'margin-left:7px',
				handler : function(btn) {
					  searchmaterialpn();
				}
  			},
			{
				xtype : 'container',
				html:'<span style=\"color:red;font-weight:normal;margin-left:10px\" id=\"pfherrormsg\"></span>',
				
			}
		]
     },
     {
 		xtype: 'container',
 		style:'margin-top:15px',
 		layout:'table',
 		width:'98%',
 		items:[
 			{
 				xtype : 'displayfield',
 				width:100,
 				style:'margin-left:10px',
 				value : '*配方号:'
 			},
 			{
 				xtype : 'combo',
 				id:'recipenumber',
 				width:180,
 				typeAhead: true,
 			    triggerAction: 'all',
 			    lazyRender:true,
 			    mode: 'local',
 			    store: new Ext.data.ArrayStore({
 			        fields: ['recipenumber'],
 			    }),
 			    valueField: 'recipenumber',
 			    displayField: 'recipenumber',
 			    listeners:{
 			    	focus:function(){
					   var m=$("#recipenumber");
					   m.bigAutocomplete({width:180,data:recipenumber_searchnumberstore,callback:function(data){
						  Ext.getCmp("recipenumber").setValue(data.title);
						  searchrecipenumber();
					   }});
				   },
				   collapse:function(combo){
					   searchrecipenumber();
				   }
				}
 			},
 			{
  				xtype : 'button',
  				icon : 'netmarkets/images/search.gif',
				style : 'margin-left:7px',
				handler : function(btn) {
					searchrecipenumber();
				}
  			},
 			{
 				xtype : 'container',
 				html:'<span style=\"color:red;font-weight:normal;margin-left:10px\" id=\"pfherrormsg\"></span>',
 				
 			}
 		]
      },
      {
		   xtype:'container',
		   height:document.documentElement.clientHeight-250,
		   id:'selectmaterial_scroll',
		   style:'overflow:scroll;margin-top:15px',
           html:'<div id="selectmaterial_html"></div>',
		   listeners:{
			   render:function(){
				   Ext.Ajax.request({
						url:'/Windchill/ptc1/battery/getDisplayHtml.do',
						params:{templatename:'selectmaterial.html',date:new Date()},
						success:function(response, opts){
							selectmaterial_render=true;
							var txt=response.responseText;
							document.getElementById("selectmaterial_html").innerHTML=txt;
							for(var i=0;i<200;i++){
								var colarray=standard_id.split(",");
								for(var j=0;j<colarray.length;j++){
									var cell=document.getElementById("material_"+colarray[j]+i);
									if(cell){
										var placeholder=cell.getAttribute("placeholder");
										if(placeholder){
											cell.innerHTML="<input type=\"text\" placeholder=\""+placeholder+"\" id=\"input_material_"+colarray[j]+i+"\" onfocus=\"handleMaterialInput('"+colarray[j]+i+"')\"/>";
										}else{
											cell.innerHTML="<input type=\"text\"  id=\"input_material_"+colarray[j]+i+"\" onfocus=\"handleMaterialInput('"+colarray[j]+i+"')\"/>";
										}
									}
										
									
								
								}
							}
							 fillMaterialCell();
							 
							 $("td :input").each(function () {//如果已经添加了事件，就不要再添加了  
							        var e = $._data(this, "events");//是this 而不是 $(this)  
							        if (e && e["change"]) {  
							        } else {  
							            $(this).change(function () {  
							            	 var id= $(this).attr("id");
							            	 if(id.indexOf("material")>-1){
							            		 var name= $(this).attr("name");
												 changeactive_index=activeindex;
												 setTimeout("refreshMathData('"+id+"','"+name+"');",300);
							            	 }
											
							            });  
							        }  
							    });  
							
							 $("input").bigAutocomplete({width:145,data:[],callback:function(data){}});
						},
						failure:function(){
							alert("请求失败");
						}
					});
				   
			   }
		   }
		}
    ]
});

function handleMaterialInput(id){
	var rownum=id.replace(/[^0-9]/ig,"");
	var dom=document.getElementById("material_"+id);
	var clf =dom.parentNode.parentNode.parentNode.getAttribute("tabletype");
	var m=$("#input_material_"+id);
	if(dom.getAttribute("search")!=null){
		var type=dom.getAttribute("search");
		var data=[];
		if(type=="name"){
			data=eval("material_searchnamestore[0]."+clf);
		}
		if(type=="number"){
			data=eval("material_searchnumberstore[0]."+clf);
		}
		m.bigAutocomplete({width:145,data:data,callback:function(data){
			 if(type=="name"){
				 var cval=data.title.replace("(RD)","").replace("(MD)","").replace("(SP)","");
				 $("#input_material_"+id).val(cval);
			 }
			 setTimeout("getPartRowJson('"+data.oid+"','"+rownum+"');",200);
		}});
	}
	
}

function getPartRowJson(oid,rownum){
	 oid=oid.replace("(RD)","").replace("(MD)","").replace("(SP)","")
	 console.log(oid);
	  Ext.Ajax.request({
			url:'/Windchill/ptc1/battery/getPartRowJson.do',
			params:{oid:oid,date:new Date()},
			success:function(response, opts){
				var txt=response.responseText;
				var msg=JSON.parse(response.responseText).msg
				var adata=JSON.parse(response.responseText).data;
				console.log(adata);
			 if(msg==""){
					var rowdata=adata[0];
					var attrs=rowdata.attr;
					var speicalkey=rowdata.speicalkey;
					for(var j=0;j<attrs.length;j++){
						var attr=attrs[j];
						var region=attr.region+rownum;
						aregion=region;
						var value=attr.value;
						var cell=document.getElementById("input_material_"+region);
						if(cell){
							cell.value=handleValue(value);
							var ptd=cell.parentNode;
							cell.setAttribute("ext:qtip","");
							if(ptd&&ptd.getAttribute("search")=="number"&&speicalkey!=""){
								cell.setAttribute("ext:qtip",speicalkey);
							}
						}
				 	}
					updatePageStyle("input_material_");
			}else{
				alert(msg);
			}
		}
	  });
}
function sel_material_onchange(region){
	var val=$("#sel_material_"+region).val();
	$("#input_material_"+region).val(val);
	refreshMathData("input_material_"+region,region);
}

function input_material_onchange(id){
	var select=document.getElementById("input_material_"+id).value;
	var table=$("#input_material_"+id).closest("table");
	var clf =table.attr("tabletype");
	if(select=="是"){
		var jsonstr="";
		var firstrownum=table.attr("firstrownum");
		var rownum=id.replace(/[^0-9]/ig,"");
		var eng=id.replace(rownum,"");
		var colarray=standard_id.split(",");
		for(var i=0;i<colarray.length;i++){
			var atr=colarray[i];
			var cid="input_material_"+atr+rownum;
			var cobj=document.getElementById(cid);
			if(cobj){
				var cvalue=cobj.value;
				console.log((atr+firstrownum)+"  "+cvalue);
				jsonstr=jsonstr+"\""+(atr+firstrownum)+"\":"+"\""+cvalue+"\","
			}
		}
		jsonstr=jsonstr+"\"cls\":"+"\""+clf+"\"";
		console.log(jsonstr);
		createPhantom("{"+jsonstr+"}");
	}
	refreshMathData("input_material_"+id,id);
	
}

function createPhantom(jsonstr){
	console.log("创建虚拟件");
	Ext.Ajax.request({
		url:'/Windchill/ptc1/battery/createPhantom.do',
		params:{jsonStr:jsonstr,date:new Date()},
		success:function(response,opts){
			var data=JSON.parse(response.responseText);
			console.log(data);
			if(data.msg!=""){
				alert(data.msg);
			}else{
				showMessage("已记录");
			}
		},
		failure:function(){
			alert('请求失败');
		}
	});
}

function searchmaterialpn(){
	var material_number=Ext.getCmp('material_number').getValue();
	if(material_number!=''){
		Ext.Ajax.request({
			url:'/Windchill/ptc1/battery/getRecipenumbers.do',
			params:{materialNumber:material_number,containerOid:containerOid,date:new Date()},
			success:function(response,opts){
				var data=JSON.parse(response.responseText);
				if(data.msg!=""){
					alert(data.msg);
				}else{
					var record=data.data;
					var combo=Ext.getCmp('recipenumber');
					var store=combo.getStore();
					store.loadData(record);
					combo.setValue(record[0]);
				}
			},
			failure:function(){
				alert('请求失败');
			}
		});
	}
}

function searchrecipenumber(){
	var recipenumber=Ext.getCmp("recipenumber").getValue();
	if(recipenumber!=""){
		 Ext.Ajax.request({
			url:'/Windchill/ptc1/battery/getRecipeJson.do',
			params:{recipenumber:recipenumber,containerOid:containerOid,date:new Date()},
			success:function(response, opts){
				var txt=response.responseText;
			    var data=JSON.parse(response.responseText);
				if(data.msg!=''){
					alert(data.msg);
				}else{
					var adata=data.data;
					console.log(data);
					for (var i = 0; i < adata.length; i++) {
						if(i==adata.length-1){
							if(adata[i]!=""){
								alert(adata[i]+"在材料库不存在");
							}
						}else{
							var rowdata=adata[i];
							//console.log(rowdata);
							var attrs=rowdata.attr;
							var speicalkey=rowdata.speicalkey;
							var array1=new Array();
							var array2=new Array();
							for(var j=0;j<attrs.length;j++){
								var attr=attrs[j];
								var region=attr.region;
								var rownum=region.replace(/[^0-9]/ig,"");
								var regioneng=region.replace(rownum,"");
								var regionnum1=getWirteRow("C",rownum);
								var regionnum2=getWirteRow("D",rownum);
								var regionnum=regionnum1;
								if(regionnum2>regionnum1){
									regionnum=regionnum2;
								}
								region=regioneng+regionnum;
								var value=attr.value;
								var cell=document.getElementById("input_material_"+region);
								array1.push(cell);
								array2.push(value);
							}
							for (var k = 0; k < array1.length; k++) {
								var cell=array1[k];
								var value=array2[k];
								if(cell){
									cell.value=value;
									var ptd=cell.parentNode;
									cell.setAttribute("ext:qtip","");
									if(ptd&&ptd.getAttribute("search")=="number"&&speicalkey!=""){
										cell.setAttribute("ext:qtip",speicalkey);
									}
								}else{
									//console.log("input_material_"+region+"找不到");
								}
							}
							
						}
						
					}
					updatePageStyle("input_material_");
					showMessage("已加载数据");
				}
				
			},
			failure:function(){
				alert("请求失败");
			}
		});
	}
}

document.onkeypress = function(){
	console.log(event.keyCode);
	if(event.keyCode == 13) {
     return false;
     }
	
}
function getWirteRow(regioneng,regionnum){
	regionnum=parseInt(regionnum);
	var cell=document.getElementById("input_material_"+regioneng+regionnum);
	if(cell){
		if(cell.value!=""){
			return getWirteRow(regioneng,regionnum+1)
		}
	}
	return regionnum;
}

document.onkeydown=function(event){
	var e = event || window.event || arguments.callee.caller.arguments[0];
	if(e && e.keyCode==38 || e && e.keyCode==40){//上,左
		 return false;
	}
}

function clearrow(td1,td2){
	var ids="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	var c1=td1.substring(0,1);
	var r1=parseInt(td1.substring(1,td1.length));
	var c2=td2.substring(0,1);
	var r2=parseInt(td2.substring(1,td2.length));
	var index1=ids.indexOf(c1);
	var index2=ids.indexOf(c2);
	for(var i=index1;i<=index2;i++){
		var cid=ids.substring(i,i+1);
		for (var j = r1; j <=r2; j++) {
			var region=cid+j;
			var input=document.getElementById("input_material_"+region);
			if(input){
				input.value="";
			}
		}
	}
	updatePageStyle("input_material_");
}