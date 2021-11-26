var diecut = new Ext.FormPanel({
    width : '100%',
    id:'diecut',
    height: '100%',
    items:[
		{
		   xtype:'container',
		   height:document.documentElement.clientHeight-160,
		   style:'overflow:scroll',
           html:'<div id="summary_diecut_html"></div><div id="diecut_html"></div>',
		   listeners:{
			   render:function(){
				   Ext.Ajax.request({
						url:'/Windchill/ptc1/battery/getDisplayHtml.do',
						params:{templatename:'diecut.html',date:new Date()},
						success:function(response, opts){
						    diecut_render=true;
							var txt=response.responseText;
							renderCell("diecut",txt);
							filldiecutCell();
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

function sel_diecut_onchange(region){
	var val=$("#sel_diecut_"+region).val();
	$("#input_diecut_"+region).val(val);
	refreshMathData("input_diecut_"+region,region);
}

function input_diecut_onchange(region){
	refreshMathData("input_diecut_"+region,region);
}
