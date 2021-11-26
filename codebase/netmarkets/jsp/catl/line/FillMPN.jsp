 <%@ page language="Java" pageEncoding="UTF-8"%>
	<script type="text/javascript">
	var oid='<%=request.getParameter("oid")%>';
	Ext.Ajax.request({
		  disableCaching:false,
		  url : "/Windchill/ptc1/line/getMPN.do?oid="+oid,
		  success : function(response, opts) {
		   var datas=JSON.parse(response.responseText).data;
		    
		   for(var k=0;k<datas.length;k++){
			   var data=datas[k];
			   console.log(data);
			   var partNumber=data[0].partNumber;
			   console.log(partNumber);
			   var tb1 =  new Ext.form.FieldSet({
					renderTo:'fillmpnbody',
					id:'mpnform'+k,
					width:'98%',
					cls:'x-form-label-left',
					autoHeight:true,
					height:500,
					collapsible: true,
					title: partNumber+':<span style="font-size:14px">属性填写</span>'
			});	 
			 for(var i=0;i<data.length;i++){
			   var attr=data[i]; 
			   var required="";
			   var value=attr.value;
			   if(attr.valuelist.length==0){
				   var xtype="textfield";
				   if(attr.type=="java.lang.Long"||attr.type=="com.ptc.core.meta.common.FloatingPoint"){
					   xtype="numberfield";
					   if(value==0){
						   value='';
					   }
				   }
				   if(attr.required){
					   required="*";
				 }
				   Ext.getCmp("mpnform"+k).add({
					   xtype:xtype,
					   id:attr.name+','+partNumber,
					   name:attr.name,
					   ctype:attr.type,
					   minValue:1,
					   value:value,
					   width:150,
					   labelStyle:'width:100;font-weight:bold',
					   fieldLabel: required+attr.displayname,
					   listeners:{
						   blur:function(field){
							   var pnumber=field.getId().split(',')[1];
							   console.log(pnumber);
							   var value=field.getValue();
							   if(field.isValid(true)){
								   if(field.ctype=="java.lang.Long"){
									   if(value!=""){
										   value=parseInt(value);
										   field.setValue(value);
									   }
								   }
									Ext.Ajax.request({
										  disableCaching:false,
										  url : "/Windchill/ptc1/line/updateibavalue.do",
										  params:{name:field.getName(),partNumber:pnumber,value:value},
										  success : function(response, opts) {
										  var msg=JSON.parse(response.responseText).msg;
											  if(msg!=""){
												  alert(msg);
											  }
										  }
									});
							   }
							   
						   }
					   }
				   });
			   }else{
				    var array=new Array();
				    if(attr.required){
						   required="*";
					 }
				    for(var j=0;j<attr.valuelist.length;j++){
				    	var val=attr.valuelist[j];
				    	var valarray=new Array();
				    	valarray.push(val);
				    	array.push(valarray);
				    }
					var combo = new Ext.form.ComboBox({
						mode: 'local',
						fieldLabel:required+attr.displayname,
						labelStyle:'width:100;font-weight:bold',
						width:150,
						id:attr.name+','+partNumber,
						name:attr.name,
						value:attr.value,
						triggerAction: 'all',
						autoSelect :true,
						editable:false,
						store: new Ext.data.ArrayStore({
							fields: [
								'value'
							],
							 data: array
						}),
						displayField: 'value',
						 listeners:{
							   select:function(combobox){
								    var pnumber=combobox.getId().split(',')[1];
									Ext.Ajax.request({
										  disableCaching:false,
										  url : "/Windchill/ptc1/line/updateibavalue.do",
										  params:{name:combobox.getName(),partNumber:pnumber,value:combobox.getValue()},
										  success : function(response, opts) {
										  var msg=JSON.parse(response.responseText).msg;
											  if(msg!=""){
												  alert(msg);
											  }
										  }
									});
							   }
						   }
					});
				   Ext.getCmp("mpnform"+k).add(combo);
			   }
			}
			  Ext.getCmp("mpnform"+k).doLayout();
		   }
		 
		  }
	});
   
	</script>
	<div id="fillmpnbody"></div>

 
