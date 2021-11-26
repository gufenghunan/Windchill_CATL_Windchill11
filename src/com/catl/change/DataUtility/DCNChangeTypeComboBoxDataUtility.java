package com.catl.change.DataUtility;

import org.apache.log4j.Logger;

import wt.util.WTException;
import com.ptc.carambola.rendering.HTMLComponent;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.dataUtilities.AttributeDataUtilityHelper;
import com.ptc.core.components.factory.dataUtilities.DefaultDataUtility;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;
import com.ptc.core.ui.resources.ComponentMode;


public class DCNChangeTypeComboBoxDataUtility extends DefaultDataUtility{
	private static Logger logger=Logger.getLogger(DCNChangeTypeComboBoxDataUtility.class.getName());
	@Override
	public Object getDataValue(String componentId, Object datum, ModelContext modelContext) throws WTException {
		Object object = super.getDataValue(componentId, datum, modelContext);
				
		GUIComponentArray array = new GUIComponentArray();
		//array.addGUIComponent(lbl);
	

        
        String actionName = modelContext.getNmCommandBean().getTextParameter("actionName");
        logger.info("===actionName:"+actionName);
        if (modelContext.getDescriptorMode().equals(ComponentMode.CREATE) || modelContext.getDescriptorMode().equals(ComponentMode.EDIT)) {
        	logger.info("componentId==== name======"+componentId);
			String columnName = AttributeDataUtilityHelper.getColumnName(componentId, datum, modelContext);
			logger.info("==columnName:"+columnName);

            
            HTMLComponent htmlc = new HTMLComponent("<p style=\"color:#FF0000;\"> <b>1、ECAD图纸变更（仅适用于PCBA未发布前的原理图变更）<br>2、设备开发变更（仅适用于MDE设备开发）</b></p>");
                
            array.addGUIComponent(htmlc);

          //  }

        
            return array;            
           
		 }
        return object;
        
	}
}
