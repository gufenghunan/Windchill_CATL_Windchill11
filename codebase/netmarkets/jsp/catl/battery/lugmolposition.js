var lugmolposition = new Ext.FormPanel({
    width : '100%',
    id:'lugmolposition',
    height: '100%',
    items:[
		{
		   xtype:'container',
		   height:document.documentElement.clientHeight-160,
		   style:'overflow:scroll',
           html:'<div id="summary_lugmolposition_html"></div><div id="lugmolposition_html"></div>',
		   listeners:{
			   render:function(){
				   Ext.Ajax.request({
						url:'/Windchill/ptc1/battery/getDisplayHtml.do',
						params:{templatename:'lugmolposition.html',date:new Date()},
						success:function(response, opts){
						    lugmolposition_render=true;
							var txt=response.responseText;
							renderCell("lugmolposition",txt);
							filllugmolpositionCell();
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
function sel_lugmolposition_onchange(region){
	var val=$("#sel_lugmolposition_"+region).val();
	$("#input_lugmolposition_"+region).val(val);
	refreshMathData("input_lugmolposition_"+region,region);
}

function input_lugmolposition_onchange(region){
	refreshMathData("input_lugmolposition_"+region,region);
}

