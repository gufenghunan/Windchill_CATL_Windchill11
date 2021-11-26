var alpmap = new Ext.FormPanel({
    width : '100%',
    id:'alpmap',
    height: '100%',
    items:[
		{
		   xtype:'container',
		   height:document.documentElement.clientHeight-160,
		   style:'overflow:scroll',
           html:'<div id="summary_alpmap_html"></div><div id="alpmap_html"></div>',
		   listeners:{
			   render:function(){
				   Ext.Ajax.request({
						url:'/Windchill/ptc1/riB/getDisplayHtml.do',
						params:{templatename:'alpmap.html',date:new Date()},
						success:function(response, opts){
						    alpmap_render=true;
							var txt=response.responseText;
							renderCell("alpmap",txt);
							fillalpmapCell();
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
function sel_alpmap_onchange(region){
	var val=$("#sel_alpmap_"+region).val();
	$("#input_alpmap_"+region).val(val);
	refreshMathData("input_alpmap_"+region,region);
}

function input_alpmap_onchange(region){
	refreshMathData("input_alpmap_"+region,region);
}

