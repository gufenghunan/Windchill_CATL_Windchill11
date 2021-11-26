var cellweight = new Ext.FormPanel({
    width : '100%',
    id:'cellweight',
    height: '100%',
    items:[
		{
		   xtype:'container',
		   height:document.documentElement.clientHeight-160,
		   style:'overflow:scroll',
           html:'<div id="summary_cellweight_html"></div><div id="cellweight_html"></div>',
		   listeners:{
			   render:function(){
				   Ext.Ajax.request({
						url:'/Windchill/ptc1/riA/getDisplayHtml.do',
						params:{templatename:'cellweight.html',date:new Date()},
						success:function(response, opts){
						    cellweight_render=true;
							var txt=response.responseText;
							renderCell("cellweight",txt);
							fillcellweightCell();
						},
						failure:function(){
							
						}
					});
			   }
		   }
		}
    ]
});

function sel_cellweight_onchange(region){
	var val=$("#sel_cellweight_"+region).val();
	$("#input_cellweight_"+region).val(val);
	refreshMathData("input_cellweight_"+region,region);
}

function input_cellweight_onchange(region){
	refreshMathData("input_cellweight_"+region,region);
}