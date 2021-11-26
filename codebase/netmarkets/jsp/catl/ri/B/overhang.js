var overhang = new Ext.FormPanel({
    width : '100%',
    id:'overhang',
    height: '100%',
    items:[
		{
		   xtype:'container',
		   height:document.documentElement.clientHeight-160,
		   style:'overflow:scroll',
           html:'<div id="summary_overhang_html"></div><div id="overhang_html"></div>',
		   listeners:{
			   render:function(){
				   Ext.Ajax.request({
						url:'/Windchill/ptc1/riB/getDisplayHtml.do',
						params:{templatename:'overhang.html',date:new Date()},
						success:function(response, opts){
						    overhang_render=true;
							var txt=response.responseText;
							renderCell("overhang",txt);
							filloverhangCell();
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
function sel_overhang_onchange(region){
	var val=$("#sel_overhang_"+region).val();
	$("#input_overhang_"+region).val(val);
	refreshMathData("input_overhang_"+region,region);
}

function input_overhang_onchange(region){
	refreshMathData("input_overhang_"+region,region);
}