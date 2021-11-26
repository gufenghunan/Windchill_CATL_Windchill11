var mechanicalasm = new Ext.FormPanel({
    width : '100%',
    id:'mechanicalasm',
    height: '100%',
    items:[
       {
   		xtype: 'container',
   		style:'margin-top:15px',
   		height:40,
   		layout:'table',
   		width:'98%',
   		items:[
   			{
   				xtype : 'displayfield',
   				width:100,
   				style:'margin-left:10px',
   				value : '*机械件组合:'
   			},
   			{
   				xtype : 'textfield',
   				id:'mechanicalasm_select',
   				width:180,
   				listeners:{
   					render:function(){
   					  var m=$("#mechanicalasm_select");
					   console.log(asmpn_searchstore);
					   m.bigAutocomplete({width:180,data:[],callback:function(data){
					  }});
   					},
   					focus:function(){
 					   var m=$("#mechanicalasm_select");
 					   console.log(asmpn_searchstore);
 					   m.bigAutocomplete({width:180,data:asmpn_searchstore,callback:function(data){
 						  searchasm();
 					  }});
 				   }
   				}
   			},
   			{
   				xtype : 'hidden',
   				id:'input_mechanicalasm_E3',
   				name:'E3'
   			},
   			{
   				xtype : 'button',
   				icon : 'netmarkets/images/search.gif',
				style : 'margin-left:7px',
				handler : function(btn) {
					searchasm();
				}
   			}
   		]
        },  
        {
		   xtype:'container',
		   height:document.documentElement.clientHeight-250,
		   id:'mechanicalasm_scroll',
		   style:'overflow:scroll;margin-top:15px',
           html:'<div id="mechanicalasm_html"></div>',
		   listeners:{
			   render:function(){
				   Ext.Ajax.request({
						url:'/Windchill/ptc1/battery/getDisplayHtml.do',
						params:{templatename:'mechanicalasm.html',date:new Date()},
						success:function(response, opts){
							mechanicalasm_render=true;
							var txt=response.responseText;
							renderCell("mechanicalasm",txt);
							fillMechanicalCell();
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

function handlemechanicalasmInput(id){
	
}


function loadmechanicalasmCellData(data){
	if(data.msg!=""){
		alert(data.msg);
	}else{
		var record=data.data;
	    for(var i=0;i<record.length;i++){
	    	var cell=record[i];
	    	for(var j=0;j<cell.length;j++){
	    		var ctd=cell[j];
	    		var id="input_mechanicalasm_"+ctd.region;
	    		var value=ctd.value;
		    	var td=document.getElementById(id);
		    		if(td){
			    		if(td.readOnly){
			    		}else{
			    			td.value=value;
			    		}
			    	}else{
			    		alert(ctd.region+"找不到对应的模版位置");
			    	}
	    	}
	    }
        refreshDesign();
	}
}

function searchasm(){
	  var val=Ext.getCmp("mechanicalasm_select").getValue();
	  Ext.getCmp("input_mechanicalasm_E3").setValue(val);
	   Ext.Ajax.request({
			url:'/Windchill/ptc1/battery/getAsmInfo.do',
			params:{name:val,date:new Date()},
			success:function(response,opts){
				var data=JSON.parse(response.responseText);
				clearasmreaddata();
				loadmechanicalasmCellData(data);
				updatePageStyle("input_mechanicalasm_");
				showMessage("已加载数据");
			},
			failure:function(){
				alert('请求失败');
			}
		});
}


function clearasmreaddata(){
	 var id=submitformids[activeindex];
    for(var i=0;i<200;i++){
		var sid="H,I,J,K,L,M,N,O,P,Q,R,S";;
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

function sel_mechanicalasm_onchange(region){
	var val=$("#sel_mechanicalasm_"+region).val();
	$("#input_mechanicalasm_"+region).val(val);
	refreshMathData("input_mechanicalasm_"+region,region);
}

function input_mechanicalasm_onchange(region){
	refreshMathData("input_mechanicalasm_"+region,region);
}