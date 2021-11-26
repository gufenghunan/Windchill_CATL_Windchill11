var inputparams = new Ext.FormPanel({
    width : '100%',
    id:'inputparams',
    height: '100%',
    items:[
		{
		   xtype:'container',
		   height:document.documentElement.clientHeight-160,
		   style:'overflow:scroll',
           html:'<div id="inputparams_html"></div>',
		   listeners:{
			   render:function(){
				   Ext.Ajax.request({
						url:'/Windchill/ptc1/battery/getDisplayHtml.do',
						params:{templatename:'inputparams.html',date:new Date()},
						success:function(response, opts){
						    inputparams_render=true;
							var txt=response.responseText;
							renderCell("inputparams",txt);
							fillinputparamsCell();
						},
						failure:function(){
							
						}
					});
			   }
		   }
		}
    ]
});

function sel_inputparams_onchange(region){
	var val=$("#sel_inputparams_"+region).val();
	$("#input_inputparams_"+region).val(val);
	refreshMathData("input_inputparams_"+region,region);
}

function input_inputparams_onchange(region){
	refreshMathData("input_inputparams_"+region,region);
}
