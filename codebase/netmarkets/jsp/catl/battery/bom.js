var bom = new Ext.FormPanel({
    width : '100%',
    id:'bom',
    height: '100%',
    items:[
		{
		   xtype:'container',
		   height:document.documentElement.clientHeight-160,
		   style:'overflow:scroll',
           html:'<div id="summary_bom_html"></div><div id="bom_html"></div>',
		   listeners:{
			   render:function(){
				   Ext.Ajax.request({
						url:'/Windchill/ptc1/battery/getDisplayHtml.do',
						params:{templatename:'bom.html',date:new Date()},
						success:function(response, opts){
						    bom_render=true;
							var txt=response.responseText;
							renderCell("bom",txt);
							fillbomCell();
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

function sel_bom_onchange(region){
	var val=$("#sel_bom_"+region+" option:selected").text();
	$("#input_bom_"+region).val(val);
	refreshMathData("input_bom_"+region,region);
}

function input_bom_onchange(region){
	refreshMathData("input_bom_"+region,region);
	
}

