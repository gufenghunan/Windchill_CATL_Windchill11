package com.catl.change.DataUtility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import wt.change2.WTChangeActivity2;
import wt.util.WTException;

import com.catl.common.util.GenericUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.dataUtilities.AttributeDataUtilityHelper;
import com.ptc.core.components.factory.dataUtilities.DefaultDataUtility;
import com.ptc.core.components.rendering.guicomponents.ComboBox;
import com.ptc.core.meta.type.common.impl.DefaultTypeInstance;
import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.netmarkets.util.beans.NmCommandBean;


public class ECATaskDesComboBoxDataUtility extends DefaultDataUtility{
	private static Logger logger=Logger.getLogger(ECATaskDesComboBoxDataUtility.class.getName());
	@Override
	public Object getDataValue(String componentId, Object datum, ModelContext modelContext) throws WTException {

		Object object = super.getDataValue(componentId, datum, modelContext);
        ComboBox comboBox = new ComboBox(); 
        
       // logger.debug("ECATaskDes======="+o);
		logger.debug("componentId====des======"+componentId);
        ArrayList<String> displayList = new ArrayList<String>();
        ArrayList<String> internalList = new ArrayList<String>();
       
        if (modelContext.getDescriptorMode().equals(ComponentMode.CREATE) || modelContext.getDescriptorMode().equals(ComponentMode.EDIT)) {
            comboBox.setId(componentId);
            comboBox.setColumnName(AttributeDataUtilityHelper.getColumnName(componentId, datum, modelContext));
            
            if (modelContext.getDescriptorMode().equals(ComponentMode.EDIT)) {
                DefaultTypeInstance typeInstance=(DefaultTypeInstance)datum;
                logger.debug("----2-----"+typeInstance.getIdentifier().getInstanceIdentifier());
                String oidString=typeInstance.getIdentifier().getInstanceIdentifier().toString();
                String oid ="VR:wt.change2.WTChangeActivity2:"+oidString.substring(oidString.lastIndexOf("|")+1,oidString.length());
                logger.debug("oid==="+oid);
                WTChangeActivity2 eca=(WTChangeActivity2) GenericUtil.getInstance(oid);
                String changeName = (String) GenericUtil.getObjectAttributeValue(eca, "name");
                String des = CatlPropertyHelper.getPropertyValue(changeName);
                String[] desc=des.split(",");
                internalList.addAll(Arrays.asList(desc));
                displayList.addAll(Arrays.asList(desc));
                comboBox.setInternalValues(internalList);
                comboBox.setValues(displayList);
                logger.debug("eca number===="+eca.getNumber());
                String deString =(String) GenericUtil.getObjectAttributeValue(eca, "taskDescription");
                logger.debug("des -------"+deString);
                comboBox.setSelected(deString);
			}
            else {
            	String o=CatlPropertyHelper.getPropertyValue("ECATaskDes");         
                String[] ab=o.split(",");
            	internalList.addAll(Arrays.asList(ab));
                displayList.addAll(Arrays.asList(ab));
                comboBox.setInternalValues(internalList);
                comboBox.setValues(displayList);
            }

            comboBox.setEnabled(true);
          //  comboBox.setRequired(true);
          //  comboBox.addJsAction("onchange","loadHarnessVariant()");
            return comboBox;            
           
		 }
       
        return object;
        
	}
}
