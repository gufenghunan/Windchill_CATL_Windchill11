var trielectrode = new Ext.FormPanel({
    width : '100%',
    id:'trielectrode',
    height: '100%',
    items:[
		{
		   xtype:'container',
		   height:document.documentElement.clientHeight-160,
		   style:'overflow:scroll',
           html:'<div id="summary_trielectrode_html"></div><div id="trielectrode_html"></div>',
		   listeners:{
			   render:function(){
				   Ext.Ajax.request({
						url:'/Windchill/ptc1/riB/getDisplayHtml.do',
						params:{templatename:'trielectrode.html',date:new Date()},
						success:function(response, opts){
						    trielectrode_render=true;
							var txt=response.responseText;
							renderCell("trielectrode",txt);
							filltrielectrodeCell();
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
function sel_trielectrode_onchange(region){
	var val=$("#sel_trielectrode_"+region).val();
	$("#input_trielectrode_"+region).val(val);
	refreshMathData("input_trielectrode_"+region,region);
}

function input_trielectrode_onchange(region){
	refreshMathData("input_trielectrode_"+region,region);
}

