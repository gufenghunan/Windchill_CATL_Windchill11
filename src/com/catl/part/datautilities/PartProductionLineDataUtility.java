package com.catl.part.datautilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.fop.util.bitmap.DitherUtil;
import org.apache.log4j.Logger;

import wt.part.WTPart;
import wt.util.WTException;

import com.catl.cadence.util.NodeUtil;
import com.catl.common.constant.PartState;
import com.catl.common.util.ClassificationUtil;
import com.catl.common.util.GenericUtil;
import com.catl.part.PartConstant;
import com.catl.part.PartLoadNameSourceUtil;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.dataUtilities.AttributeDataUtilityHelper;
import com.ptc.core.components.factory.dataUtilities.DefaultDataUtility;
import com.ptc.core.components.rendering.guicomponents.ComboBox;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;
import com.ptc.core.meta.type.common.impl.DefaultTypeInstance;
import com.ptc.core.ui.resources.ComponentMode;


public class PartProductionLineDataUtility extends DefaultDataUtility{
	private static Logger logger=Logger.getLogger(PartProductionLineDataUtility.class.getName());
	
	@Override
	public Object getDataValue(String componentId, Object datum, ModelContext modelContext) throws WTException {
		Object object = super.getDataValue(componentId, datum, modelContext);
		GUIComponentArray array = new GUIComponentArray();
        ComboBox comboBox = new ComboBox(); 
        
        Object obj = modelContext.getNmCommandBean().getPageOid().getRefObject();
      
        ArrayList<String> displayList = new ArrayList<String>();
        ArrayList<String> internalList = new ArrayList<String>();
		
        //String names =",";
        
		boolean active = true;
		String oldPhase = "";
		WTPart part = null;
		if(obj instanceof WTPart){
			part = (WTPart)obj;
			String state = part.getLifeCycleState().toString();
			/*if(PartState.WRITING.equals(state)||PartState.MODIFICATION.equals(state)){
				active = false;
			}*/
			oldPhase = (String) GenericUtil.getObjectAttributeValue(part, "ProductionLine");			
			if(StringUtils.isNotBlank(oldPhase)){
				internalList.add(oldPhase);
				displayList.add(oldPhase);
			}else{
				internalList.add("PTO拉");
				displayList.add("PTO拉");
			}	       
		}
        
        String actionName = modelContext.getNmCommandBean().getTextParameter("actionName");
        logger.info("===actionName:"+actionName);
        if (modelContext.getDescriptorMode().equals(ComponentMode.EDIT) && active) {
        	logger.info("componentId==== name======"+componentId);
			String columnName = AttributeDataUtilityHelper.getColumnName(componentId, datum, modelContext);
			logger.info("==columnName:"+columnName);
			comboBox.setId(componentId);
            comboBox.setColumnName(columnName);
            comboBox.setInternalValues(internalList);
            comboBox.setValues(displayList);
            comboBox.setRequired(true);
           
            comboBox.setSelected(oldPhase);
            
            comboBox.setEnabled(true);
            comboBox.setValueHidden(true);

            array.addGUIComponent(comboBox);
            return array;            
           
		 }
       
        return object;
        
	}
}
