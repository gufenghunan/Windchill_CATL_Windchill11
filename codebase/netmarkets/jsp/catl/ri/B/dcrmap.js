var dcrmap = new Ext.FormPanel({
    width : '100%',
    id:'dcrmap',
    height: '100%',
    items:[
		{
		   xtype:'container',
		   height:document.documentElement.clientHeight-160,
		   style:'overflow:scroll',
           html:'<div id="summary_dcrmap_html"></div><div id="dcrmap_html"></div>',
		   listeners:{
			   render:function(){
				   Ext.Ajax.request({
						url:'/Windchill/ptc1/riB/getDisplayHtml.do',
						params:{templatename:'dcrmap.html',date:new Date()},
						success:function(response, opts){
						    dcrmap_render=true;
							var txt=response.responseText;
							renderCell("dcrmap",txt);
							filldcrmapCell();
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
function sel_dcrmap_onchange(region){
	var val=$("#sel_dcrmap_"+region).val();
	$("#input_dcrmap_"+region).val(val);
	refreshMathData("input_dcrmap_"+region,region);
}

function input_dcrmap_onchange(region){
	refreshMathData("input_dcrmap_"+region,region);
}

