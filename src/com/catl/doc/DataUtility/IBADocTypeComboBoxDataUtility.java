package com.catl.doc.DataUtility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import wt.change2.WTChangeActivity2;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.ReferenceFactory;
import wt.util.WTException;
import wt.util.WTMessage;

import com.catl.change.DataUtility.CatlPropertyHelper;
import com.catl.change.DataUtility.ECANameComboBoxDataUtility;
import com.catl.common.util.GenericUtil;
import com.ptc.carambola.rendering.HTMLComponent;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.dataUtilities.AttributeDataUtilityHelper;
import com.ptc.core.components.factory.dataUtilities.DefaultDataUtility;
import com.ptc.core.components.rendering.guicomponents.ComboBox;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;
import com.ptc.core.components.rendering.guicomponents.GuiComponentUtil;
import com.ptc.core.components.rendering.guicomponents.TextBox;
import com.ptc.core.meta.type.common.impl.DefaultTypeInstance;
import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.netmarkets.util.beans.NmCommandBean;


public class IBADocTypeComboBoxDataUtility extends DefaultDataUtility{
	private static Logger logger=Logger.getLogger(IBADocTypeComboBoxDataUtility.class.getName());
	@Override
	public Object getDataValue(String componentId, Object datum, ModelContext modelContext) throws WTException {
		
		Object object = super.getDataValue(componentId, datum, modelContext);
		GUIComponentArray array = new GUIComponentArray();
        ComboBox comboBox = new ComboBox(); 
        ArrayList<String> displayList = new ArrayList<String>();
        ArrayList<String> internalList = new ArrayList<String>();
        
        String actionName = modelContext.getNmCommandBean().getTextParameter("actionName");
        logger.info("===actionName:"+actionName);
        logger.info("componentId==== name======"+componentId);
		String columnName = AttributeDataUtilityHelper.getColumnName(componentId, datum, modelContext);
		logger.info("==columnName:"+columnName);
		
        if (modelContext.getDescriptorMode().equals(ComponentMode.CREATE) || modelContext.getDescriptorMode().equals(ComponentMode.EDIT)) {
        	comboBox.setId(componentId);
            comboBox.setColumnName(columnName);
            comboBox.setEditable(false);
        	if (modelContext.getDescriptorMode().equals(ComponentMode.EDIT)) {
                DefaultTypeInstance typeInstance=(DefaultTypeInstance)datum;
                logger.debug("----2-----"+typeInstance.getIdentifier().getInstanceIdentifier());
                String oidString=typeInstance.getIdentifier().getInstanceIdentifier().toString();
                String oid ="VR:wt.doc.WTDocument:"+oidString.substring(oidString.lastIndexOf("|")+1,oidString.length());
                logger.debug("oid==="+oid);
                ReferenceFactory rf = new ReferenceFactory();
                Persistable obj = rf.getReference(oid).getObject();
                WTDocument docObj= (WTDocument) obj;
                //生命周期为编制或修改时才能编辑
                if(docObj.getState().toString().equalsIgnoreCase("WRITING") || 
                		docObj.getState().toString().equalsIgnoreCase("MODIFICATION") ){
                	comboBox.setEditable(true);
                }
                String subCategorySelected = (String) GenericUtil.getObjectAttributeValue(docObj, "subCategory");
                String docTypeSelected = (String) GenericUtil.getObjectAttributeValue(docObj, "CATL_DocType");
                //获取列表
                String docType = CatlPropertyHelper.getDocPropertyValue(subCategorySelected);
                //值为空则显示 <NO TYPE>
                if ( docType == null || docType.equals("") ) {
                	internalList.add("");
                	displayList.add(" <NO TYPE> ");
                }else{
                	String[] docTypes=docType.split(",");
                    internalList.addAll(Arrays.asList(docTypes));
                    displayList.addAll(Arrays.asList(docTypes));
                }
                comboBox.setInternalValues(internalList);
                comboBox.setValues(displayList);
                logger.debug("docTypeSelected -------"+docTypeSelected);
                comboBox.setSelected(docTypeSelected);
			}else{
				//创建
				 String o=CatlPropertyHelper.getDocPropertyValue(componentId);
			     String[] ab=o.split(","); 
				internalList.addAll(Arrays.asList(ab));
		        displayList.addAll(Arrays.asList(ab));
		        
		        comboBox.setEditable(true);
	            comboBox.setInternalValues(internalList);
	            comboBox.setValues(displayList);
			}
            comboBox.setEnabled(true);
            comboBox.setValueHidden(true);
            array.addGUIComponent(comboBox);
            return array;            
		 }
       
        return object;
        
	}
}
