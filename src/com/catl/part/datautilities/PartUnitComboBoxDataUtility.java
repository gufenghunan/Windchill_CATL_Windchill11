package com.catl.part.datautilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.apache.fop.util.bitmap.DitherUtil;
import org.apache.log4j.Logger;

import wt.part.WTPart;
import wt.util.WTException;

import com.catl.cadence.util.NodeUtil;
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


public class PartUnitComboBoxDataUtility extends DefaultDataUtility{
	private static Logger logger=Logger.getLogger(PartUnitComboBoxDataUtility.class.getName());
	
	@Override
	public Object getDataValue(String componentId, Object datum, ModelContext modelContext) throws WTException {
		Object object = super.getDataValue(componentId, datum, modelContext);
		Map<String,String> clsnamesource = PartLoadNameSourceUtil.getPartClsNameSource();
		
		GUIComponentArray array = new GUIComponentArray();
        ComboBox comboBox = new ComboBox(); 
      
        ArrayList<String> displayList = new ArrayList<String>();
        ArrayList<String> internalList = new ArrayList<String>();
        //String names =",";
        
		Object obj = modelContext.getNmCommandBean().getPageOid().getRefObject();
		String oldunit = "";
		WTPart part = null;
		if(obj instanceof WTPart){
			part = (WTPart)obj;
			oldunit = part.getDefaultUnit().toString();//.getSource().toString();
			String unit = "";
			LWCStructEnumAttTemplate cls = NodeUtil.getLWCStructEnumAttTemplateByPart(part);
			String clsname = cls.getName();
			System.out.println("CLSNAME:\t"+clsname);
			if(clsnamesource.containsKey(clsname)){
				String sourcename = clsnamesource.get(clsname);
				System.out.println("Source and name:\t"+sourcename);
				String[] nsarray = sourcename.split("qqqq;;;;");
				if(nsarray.length == 4){
					unit = nsarray[2];
					System.out.println("Source:\t"+unit);
				}
			}
			String[] ab=unit.split("\\|"); 
			for(String temp:ab){
				System.out.println("Add source ............");
				internalList.add(temp.equals("pcs")?"ea":temp);
				displayList.add(temp);
			}
	        //internalList.addAll(Arrays.asList(ab));
			//displayList.addAll(Arrays.asList(ab));
	       
		}
        
        String actionName = modelContext.getNmCommandBean().getTextParameter("actionName");
        logger.info("===actionName:"+actionName);
        if (modelContext.getDescriptorMode().equals(ComponentMode.EDIT)) {
        	logger.info("componentId==== name======"+componentId);
			String columnName = AttributeDataUtilityHelper.getColumnName(componentId, datum, modelContext);
			logger.info("==columnName:"+columnName);
			comboBox.setId(componentId);
            comboBox.setColumnName(columnName);
            comboBox.setInternalValues(internalList);
            comboBox.setValues(displayList);
            comboBox.setRequired(true);
            
            comboBox.setSelected(oldunit);
            
            comboBox.setEnabled(true);
            comboBox.setValueHidden(true);
            //comboBox.addJsAction("onchange","loadHarnessVariant()");
            array.addGUIComponent(comboBox);
            return array;            
           
		 }
       
        return object;
        
	}
}
