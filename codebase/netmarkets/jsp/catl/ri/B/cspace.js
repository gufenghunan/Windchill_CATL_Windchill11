var cspace = new Ext.FormPanel({
    width : '100%',
    id:'cspace',
    height: '100%',
    items:[
		{
		   xtype:'container',
		   height:document.documentElement.clientHeight-160,
		   style:'overflow:scroll',
           html:'<div id="summary_cspace_html"></div><div id="cspace_html"></div>',
		   listeners:{
			   render:function(){
				   Ext.Ajax.request({
						url:'/Windchill/ptc1/riB/getDisplayHtml.do',
						params:{templatename:'cspace.html',date:new Date()},
						success:function(response, opts){
						    cspace_render=true;
							var txt=response.responseText;
							document.getElementById("cspace_html").innerHTML=txt;
							renderCell("cspace",txt);
							fillcspaceCell();
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

function sel_cspace_onchange(region){
	var val=$("#sel_cspace_"+region).val();
	$("#input_cspace_"+region).val(val);
	refreshMathData("input_cspace_"+region,region);
}

function input_cspace_onchange(region){
	refreshMathData("input_cspace_"+region,region);
}
