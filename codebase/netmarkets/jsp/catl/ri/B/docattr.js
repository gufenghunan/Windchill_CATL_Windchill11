Ext.ns('Ext.battery');
var docattr= new Ext.FormPanel({
    width : '100%',
    id:'docattr',
    height: '100%',
    items:[
     {
		xtype: 'container',
		style:'margin-top:15px',
		height:40,
		layout:'table',
		width:'98%',
		items:[
			{
				xtype : 'displayfield',
				width:100,
				style:'margin-left:10px',
				value : '*请输入文档名称:'
			},
			{
				xtype : 'textfield',
				width:150,
				id:'battery_name',
				name:'battery_name',
				minValue:1,
//				listeners:{
//					render : function(field) { 
//	                    Ext.QuickTips.init();  
//	                    Ext.QuickTips.register({  
//	                        target : field.el,  
//	                        text : '命名格式：“项目名称_Module号_设计目的” <br/>位数：“6位以内_6位_6位以内”'  
//	                    })  
//	                }  
//				}
			},
			{
				xtype : 'container',
				html:'<span style=\"color:red;font-weight:normal;margin-left:10px\" id=\"nameerrormsg\"></span>'
			}
		]
     },
     {
  		xtype: 'container',
  		layout:'table',
  		height:40,
  		width:'98%',
  		items:[
			{
				xtype : 'displayfield',
				width:100,
				style:'margin-left:10px',
				value : '*阶段:'
			},
			new Ext.form.ComboBox({
				allowBlank:false,
				editable:false,
				typeAhead: true,
				value:'RD',
				width:150,
				name:'battery_level',
				id:'battery_level',
				triggerAction: 'all',
				lazyRender:true,
				mode: 'local',
				store: new Ext.data.ArrayStore({
					fields: [
						'value'
					],
					data:[['RD'],['SP'],['MD']],
				}),
				valueField: 'value',
				displayField: 'value'
			})
  		 ]
      },
      {
  		xtype: 'container',
  		height:40,
  		layout:'table',
  		width:'98%',
  		items:[
  			{
  				xtype : 'displayfield',
  				width:100,
  				style:'margin-left:10px',
  				value : '&nbsp;备注:'
  			},
  			{
  				xtype : 'textfield',
  				name:'battery_nameremark',
  				id:'battery_nameremark',
  				width:150
  			}
  		]
       }
    ]
});

