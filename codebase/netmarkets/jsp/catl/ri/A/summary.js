var summary = new Ext.FormPanel({
    width : '100%',
    id:'summary',
    height: '100%',
    items:[
		{
		   xtype:'container',
		   height:document.documentElement.clientHeight-160,
		   style:'overflow:scroll',
           html:'<div id="summary_html"></div>',
		   listeners:{
			   render:function(){
				   Ext.Ajax.request({
						url:'/Windchill/ptc1/riA/getDisplayHtml.do',
						params:{templatename:'summary.html',date:new Date()},
						success:function(response, opts){
						    summary_render=true;
							var txt=response.responseText;
							renderCell("summary",txt);
							fillsummaryCell();
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
function sel_summary_onchange(region){
	var val=$("#sel_summary_"+region).val();
	$("#input_summary_"+region).val(val);
	refreshMathData("input_summary_"+region,region);
}

function input_summary_onchange(region){
	refreshMathData("input_summary_"+region,region);
}
