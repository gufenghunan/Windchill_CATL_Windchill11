 <%@ page language="Java" pageEncoding="UTF-8"%>
    <style>
      .x-tab-panel-header {
		border-color: #FFF;
	}
	.x-tab-panel-body {
		border-color: #FFF;
	}
	 .x-form-display-field{
		 color:#000;
		 font-weight:bold;
	 }
	 .ext-strict .ext-gecko .x-form-field-trigger-wrap .x-form-text{
	  height:16px !important;
	 }
	 .gridtb_cls .x-form-field-wrap .x-form-trigger{
	 top:-2px !important;
	 }
	.x-btn button{
	    background: transparent;
	    padding-left: 3px !important;
	    padding-right: 3px !important;
	}
    </style>
	<script type="text/javascript">
	 var website='<%=request.getScheme()+"://"+request.getServerName()%>';
	  var oid='<%=request.getParameter("oid")%>';
	  var winWidth=0,winHeight=0;
	  Ext.onReady(function() {
			  var store= new Ext.data.ArrayStore({
					        fields: ['name','number','oldplatform','newplatform','newplatformrec'],
							root:'data',
							url :'/Windchill/ptc1/require/getPlatformPartsInfo.do?oid='+oid
			 });
			  store.load();
			  var grid = new Ext.grid.EditorGridPanel({  
				    cls:'grid_cls',
			        store: store,  
			        renderTo:'change_platform_body',
			        id:'changeplatform_grid',
			        scrollable:true,
			        clicksToEdit:1,
			        region:'center',  
			        style:'border:1px solid #EEE;overflow:hidden',
			    	width:600,
					height: 200,
			        margins: '0 5 5 5',  
			        sm: new Ext.grid.CheckboxSelectionModel(),
			        viewConfig: {
				    	forceFit: true
					},
			    	colModel: new Ext.grid.ColumnModel({
				       columns: [  
				        new Ext.grid.CheckboxSelectionModel (),
				        {  
				            header: '',  
				            width:20,
				            dataIndex: 'operation',
				            renderer:function(value,cellmeta){
				                var returnStr = "<img src=\""+website+"/Windchill/wtcore/images/part.gif\"/>";
				                return returnStr;
				           }
				        },
				        {  
				            header: '名称',  
				            dataIndex: 'name',
				            width: 100,
				            sortable: true,  
				        },
				        {  
				            header: 'PN',  
				            dataIndex: 'number',
				            width: 100,
				            sortable: true,  
				        },
				        {  
				            header: '旧产品线标识',  
				            dataIndex: 'oldplatform',
				            width: 100,
				            sortable: true,  
				        },
				        {
				            header: '新产品线标识*',  
				            dataIndex: 'newplatform',sortable: true, 
			                renderer:function(value, metaData, record, rowIndex, colIndex,store, gridview){
				                record.modified=false;
				                return "<input type=\"text\" value=\""+value+"\"/ style=\"margin-top:-3px;border:1px solid #d1d3d4;width:99%;height:15px\">";
				           },
				            editor:new Ext.form.ComboBox({
								editable:false,
								typeAhead: true,
								triggerAction: 'all',
								lazyRender:true,
								mode: 'local',
								listeners:{
				            		focus:function(combo){
				            			loadPlatformStore(combo);
				            		},
				            		select:function(combobox){
				            			var grid=Ext.getCmp("changeplatform_grid");
				        				var sel=grid.getSelectionModel().selections;
				        				var node=sel.get(0);
				        				var pnumber=node.get("number");
				        				console.log(pnumber);
				        				console.log(combobox.getValue());
										Ext.Ajax.request({
											  disableCaching:false,
											  url : "/Windchill/ptc1/require/updateplatform.do",
											  params:{name:'CATL_Platform',partNumber:pnumber,value:combobox.getValue()},
											  success : function(response, opts) {
											  var msg=JSON.parse(response.responseText).msg;
												  if(msg!=""){
													  combobox.setValue("");
													  alert(msg);
												  }
											  }
										});
								   }
				            	},
								store: new Ext.data.ArrayStore({
									fields: [
										'value'
									],
									data:[]
								}),
								valueField: 'value',
								displayField: 'value'
							})
				        }
				        ]  
			   })
		  });
	  });
	  
		function loadPlatformStore(combo){
		    var store=combo.store;
			var grid=Ext.getCmp("changeplatform_grid");
			var sel=grid.getSelectionModel().selections;
			var node=sel.get(0);
			var newplatformrec=node.get("newplatformrec");
			var platforms = newplatformrec.split(',');
			var dataarray=new Array();
			   for(var i=0;i<platforms.length;i++){
	            	var array=new Array();
	            	array.push(platforms[i]);
	            	dataarray.push(array);
	            }	
 			store.loadData(dataarray,false);
       }
		var flag;
		function setMainformForActionHandler(event){
			var cancel=document.getElementById("routingChoice_取消").checked;
			if(cancel){
				flag=true;
				return;
			}
			var grid=Ext.getCmp("changeplatform_grid");
			var store=grid.getStore();
			flag=true;
			var msg="";
			for(var i=0;i<store.getCount();i++){
				var rec=store.getAt(i);
				var newplatform=rec.get("newplatform");
				var number=rec.get("number");
				if(newplatform==""){
					flag=false;
					msg=msg+"存为未更改产品线标识的部件"+number+"\n";
				}
			}
			var store=Ext.getCmp("changeplatform_grid").store;
			if(!flag){
				alert(msg);
			}
		    
		}
		
		function submitIt(windowName, url, params0, params1, params2, params3, params4, params5, params6, params7, params8, params9, params10, params11, params12, params13, params14, params15, params16, params17, compContext, elemAddress, actionClass, actionMethod, moreInfo, alertMessage, noneMessage, isDCA, shortCut, isAjaxEnabled, ajaxClass, isSelectRequired, tableID){
			if(!flag){
				var extraMaskDiv = getElementsByClassNameAndTag("ext-el-mask", Ext.getBody(), "div")[0];
	            if (extraMaskDiv) {
	                Ext.removeNode(extraMaskDiv);
	            }
	            var extraLoadingDiv = getElementsByClassNameAndTag("ext-el-mask-msg x-mask-loading", Ext.getBody(), "div")[0];
	            if (extraLoadingDiv) {
	                Ext.removeNode(extraLoadingDiv);
	            }
				 return ;
			}
			try {
		        if (compContext === '' && elemAddress === '') {
		            // Get context information from the table row context.   HTML size reduction
		            // stopped duplicated that info on the action renderering in the table rows.
		            var trnode = getParentNodeByTag(Ext.getDom(shortCut), "TR");
		            var tablerows = getElementsByClassNameAndTag("JCA_tablerow", trnode, 'input');
		            if (tablerows && tablerows.length > 0) {
		                var rowValue = tablerows[0].value;
		                // full context
		                params15 = rowValue;
		                compContext = getCompContext(rowValue);
		                elemAddress = getElementAddress(rowValue);
		                params17 = getOidFromRowValue(rowValue);
		                params16 = "oid";
		            }
		            else {
		                // for menus outside the table that may not have provided context
		                compContext = PTC.action.getPageContext();
		            }
		        }

		        tableUtils.toggleDisabledSelectedRows(tableID, true);
		        if (tableID && tableID !== "null") {
		            PTC.util._setMainFormStartingElement(tableID);
		        }
		        var mform = getMainForm();
		        clearActionFormData();
		        if (prepareToSubmitMain(compContext, elemAddress, actionClass, actionMethod, windowName, mform, true, isSelectRequired, tableID)) {
		        	if (!PTC.validation.doOnClickValidation(mform, false)) {
		                clearActionFormData();
		                return;
		            }
		            //Action only when a valid method and not a pickerSearch
		            var isAction = (actionMethod && actionClass) && (windowName && windowName.toLowerCase().indexOf('search')<0);
		            if (ajaxClass) {
		               //if an ajax url is provided, do not override with the action controller
		               isAction = false;
		            }
		            if (!handleDynamicAction(mform, compContext, elemAddress, actionClass, actionMethod, windowName, params0, params1, params2, params3, params4, params5, params6, params7, params8, params9, params10, params11, params12, params13, params14, params15, params16, params17, false, '', isAjaxEnabled, ajaxClass, tableID, true, true, true, null, null, isAction)) {
		            	var formElems = changeFormWithParams(mform, params0, params1, params2, params3, params4, params5, params6, params7, params8, params9, params10, params11, params12, params13, params14, params15, params16, params17, url);
		                formElems.push(JCAappendFormHiddenInput(mform, 'tableID', tableID));
		                formElems.push(JCAappendFormHiddenInput(mform, 'AjaxEnabled', isAjaxEnabled));
		                var validTable = tableID && tableID != "null" && Ext.getDom(tableID);
		                if (isAjaxEnabled !== "page" && validTable) {
		                    // component refresh
		                    refreshCurrentElement(tableID, true, true);
		                    jsca.unSelectAll(null, tableID);
		                    clearActionFormData();
		                }else if (currentStepStrName) {
		                    // wizard "page" refresh
		                    refreshCurrentStep();
		                    clearActionFormData();
		                }else {
		                    // non-wizard page refresh
		                    if (!callUserSubmitFunction()) {
		                        return false;
		                    }
		                    // Probably need to un-select rows from other tables if the tableid is not null
		                    // so that the action won't accidentally apply to the other table's stuff.
		                    if (isAjaxEnabled !== "page") {
		                        // need to remove the portlet param since we can't actually do a partial refresh some places:
		                        var portletelem = [];
		                        if (formElems && formElems.length > 0) {
		                            var formElems_length = formElems.length;
		                            for (var i = 0; i < formElems_length; i++) {
		                                if (formElems[i] && formElems[i].name && formElems[i].name == "portlet") {
		                                    portletelem.push(formElems[i]);
		                                }
		                            }
		                        }
		                        removeFormElements(portletelem);
		                    }
		                    formElems.push(JCAappendFormHiddenInput(mform, "tableID", tableID));
		                    PTC.navigation.submitForm(mform);
		                    clearActionFormData();
		                }
		                removeFormElements(formElems);
		            }else {
		                clearActionFormData();
		            }
		        }
		        else {
		            PTC.action.log.debug("prepareToSubmitMain() returned false. mform.action:", PTC.util.getFormAction(mform));
		        }
		    } catch (e) {
		        // some unexpected js error occurred. Log it so that is not simply ignored.
		        var eString = Ext.encode(e); // get string representation of object
		        PTC.action.log.error("js exception in submitIt(): ", eString);
		    }
		    finally {
		        removeOnClickTemp(mform);
		        tableUtils.toggleDisabledSelectedRows(tableID, false);
		    }

		}
   </script>
   
   	<div id="change_platform_body"></div>