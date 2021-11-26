var averagevoltage = new Ext.FormPanel({
    width : '100%',
    id:'averagevoltage',
    height: '100%',
    items:[
		{
		   xtype:'container',
		   height:document.documentElement.clientHeight-160,
		   style:'overflow:scroll',
           html:'<div id="summary_averagevoltage_html"></div><div id="averagevoltage_html"></div>',
		   listeners:{
			   render:function(){
				   Ext.Ajax.request({
						url:'/Windchill/ptc1/riB/getDisplayHtml.do',
						params:{templatename:'averagevoltage.html',date:new Date()},
						success:function(response, opts){
						    averagevoltage_render=true;
							var txt=response.responseText;
							renderCell("averagevoltage",txt);
							fillaveragevoltageCell();
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
function sel_averagevoltage_onchange(region){
	var val=$("#sel_averagevoltage_"+region).val();
	$("#input_averagevoltage_"+region).val(val);
	refreshMathData("input_averagevoltage_"+region,region);
}

function input_averagevoltage_onchange(region){
	refreshMathData("input_averagevoltage_"+region,region);
}

