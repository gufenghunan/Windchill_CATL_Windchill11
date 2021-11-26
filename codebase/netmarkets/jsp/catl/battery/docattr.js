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
				xtype : 'displayfield',
				width:700,
				style:'margin-left:10px;top: 15px;line-height: 20px;color:#000;font-weight:normal;z-index:999;position:absolute;',
				html : '提示：<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;F_XXX（3位容量值）_7位Model号（厚宽高）_样品阶段(A/B/C/S)_设计目的（6位）<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;N_37D/37V（容量37Ah项目区别）_7位Model号（厚宽高）_样品阶段(A/B/C/S)_设计目的（6位）<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;H_37D/37V_7位Model号（厚宽高）_样品阶段(A/B/C/S)_设计目的（6位）<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;其中，3位容量值的填写方式<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;5Ah，则填写005<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;15Ah，则填写015<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;150Ah，则填写150<br/><br/>例如：N_37D_2614891_C_正极材料评估_001_MD_无<br/>其中：<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1）F：磷酸铁锂；N：三元材料；H：两种（及以上）正极材料混合<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2）“001”为系统自动生成，不需手填 <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3）阶段（RD/SP/MD）为下拉选择><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;4）“无”为备注部分，非必填',
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

