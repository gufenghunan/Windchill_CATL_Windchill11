package com.catl.part.datautilities;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.fop.util.bitmap.DitherUtil;
import org.apache.log4j.Logger;

import wt.part.WTPart;
import wt.util.WTException;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.dataUtilities.AttributeDataUtilityHelper;
import com.ptc.core.components.factory.dataUtilities.DefaultDataUtility;
import com.ptc.core.components.rendering.guicomponents.ComboBox;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;
import com.ptc.core.meta.type.common.impl.DefaultTypeInstance;
import com.ptc.core.ui.resources.ComponentMode;


public class PartNameComboBoxDataUtility extends DefaultDataUtility{
	private static Logger logger=Logger.getLogger(PartNameComboBoxDataUtility.class.getName());
	@Override
	public Object getDataValue(String componentId, Object datum, ModelContext modelContext) throws WTException {
		Object object = super.getDataValue(componentId, datum, modelContext);
		
		GUIComponentArray array = new GUIComponentArray();
        ComboBox comboBox = new ComboBox(); 
      
        ArrayList<String> displayList = new ArrayList<String>();
        ArrayList<String> internalList = new ArrayList<String>();
        String names =",";
        String[] ab=names.split(","); 
        
		String typeid = modelContext.getNmCommandBean().getTextParameter("baseTypeIdentifier");
		String clsName = modelContext.getNmCommandBean().getTextParameter("selectedClfNodesDisplayName");
		System.out.println("clsName\t"+clsName);
		
		System.out.println(typeid);
		Object obj = datum;//modelContext.getNmCommandBean().getPageOid().getRefObject();
		
		WTPart part = null;
		if(typeid.contains("WCTYPE|wt.part.WTPart")){
			//part = (WTPart)obj;
			
	        internalList.addAll(Arrays.asList(ab));
			displayList.addAll(Arrays.asList(ab));
	       
		}
        
        String actionName = modelContext.getNmCommandBean().getTextParameter("actionName");
        logger.info("===actionName:"+actionName);
        if (modelContext.getDescriptorMode().equals(ComponentMode.CREATE) || modelContext.getDescriptorMode().equals(ComponentMode.EDIT)) {
        	logger.info("componentId==== name======"+componentId);
			String columnName = AttributeDataUtilityHelper.getColumnName(componentId, datum, modelContext);
			logger.info("==columnName:"+columnName);
			comboBox.setId(componentId);
            comboBox.setColumnName(columnName);
            comboBox.setInternalValues(internalList);
            comboBox.setValues(displayList);
            comboBox.setRequired(true);

            comboBox.setEnabled(true);
            comboBox.setValueHidden(true);
            //comboBox.addJsAction("onchange","loadHarnessVariant()");
            array.addGUIComponent(comboBox);
            return array;            
           
		 }
       
        return object;
        
	}
}
