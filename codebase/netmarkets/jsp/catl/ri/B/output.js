var output = new Ext.FormPanel({
    width : '100%',
    id:'output',
    height: '100%',
    items:[
		{
		   xtype:'container',
		   height:document.documentElement.clientHeight-160,
		   style:'overflow:scroll',
           html:'<div id="summary_output_html"></div><div id="output_html"></div>',
		   listeners:{
			   render:function(){
				   Ext.Ajax.request({
						url:'/Windchill/ptc1/riB/getDisplayHtml.do',
						params:{templatename:'output.html',date:new Date()},
						success:function(response, opts){
						    output_render=true;
							var txt=response.responseText;
							renderCell("output",txt);
							filloutputCell();
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
function sel_output_onchange(region){
	var val=$("#sel_output_"+region).val();
	$("#input_output_"+region).val(val);
	refreshMathData("input_output_"+region,region);
}

function input_output_onchange(region){
	refreshMathData("input_output_"+region,region);
}

