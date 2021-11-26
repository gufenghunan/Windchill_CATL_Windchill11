package com.catl.change.DataUtility;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import wt.change2.WTChangeActivity2;
import wt.util.WTException;
import wt.util.WTMessage;

import com.catl.common.util.GenericUtil;
import com.ptc.carambola.rendering.HTMLComponent;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.dataUtilities.AttributeDataUtilityHelper;
import com.ptc.core.components.factory.dataUtilities.DefaultDataUtility;
import com.ptc.core.components.rendering.guicomponents.ComboBox;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;
import com.ptc.core.components.rendering.guicomponents.GuiComponentUtil;
import com.ptc.core.components.rendering.guicomponents.TextBox;
import com.ptc.core.meta.type.common.impl.DefaultTypeInstance;
import com.ptc.core.ui.resources.ComponentMode;


public class ECANameComboBoxDataUtility extends DefaultDataUtility{
	private static Logger logger=Logger.getLogger(ECANameComboBoxDataUtility.class.getName());
	@Override
	public Object getDataValue(String componentId, Object datum, ModelContext modelContext) throws WTException {
		Object object = super.getDataValue(componentId, datum, modelContext);
		GUIComponentArray array = new GUIComponentArray();
        ComboBox comboBox = new ComboBox(); 
        String o=CatlPropertyHelper.getPropertyValue("ECATask");

        String[] ab=o.split(","); 
       // logger.debug("eca task ==="+o);
        ArrayList<String> displayList = new ArrayList<String>();
        ArrayList<String> internalList = new ArrayList<String>();
        internalList.addAll(Arrays.asList(ab));
        displayList.addAll(Arrays.asList(ab));
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
            if (modelContext.getDescriptorMode().equals(ComponentMode.EDIT)) {
                DefaultTypeInstance typeInstance=(DefaultTypeInstance)datum;
                String oid = typeInstance.getPersistenceIdentifier();
                logger.debug("oid==="+oid);
                WTChangeActivity2 eca=(WTChangeActivity2) GenericUtil.getInstance(oid);
                logger.debug("eca number===="+eca.getNumber());
                String changename =(String) GenericUtil.getObjectAttributeValue(eca, "change_name");
                String name =eca.getName();
                logger.debug("eca name ==="+name);
                logger.debug("eca changename ==="+changename);
                comboBox.setSelected(name);
                if(StringUtils.equals("wizardEdit", actionName)){
                	columnName = WTMessage.formatLocalizedMessage("changeTask$edit${0}$|components$loadWizardStep${0}$___change_name___textbox", new Object[]{oid});
                	TextBox tb = GuiComponentUtil.createTextBox(50);
        			tb.setId(componentId+"_TB");
                    tb.setColumnName(columnName);
                    tb.setInputType("hidden");
                    tb.setValue(name);
                    tb.setValueHidden(true);
                    array.addGUIComponent(tb);
                }
			}
            if(StringUtils.equals("wizardCreate", actionName)){
            	TextBox tb = GuiComponentUtil.createTextBox(50);
    			tb.setId(componentId+"_TB");
                tb.setColumnName(columnName);
                tb.setInputType("hidden");
                tb.setValue(Arrays.asList(ab).get(0));
                array.addGUIComponent(tb);
                HTMLComponent htmlc = new HTMLComponent("<input type=\"hidden\" value=\"com.ptc.core.components.forms.NamePropertyProcessor\" name=\"!~objectHandle~task~!FormProcessorDelegate\">");
                array.addGUIComponent(htmlc);
            }

            comboBox.setEnabled(true);
            comboBox.setValueHidden(true);
            comboBox.addJsAction("onchange","loadHarnessVariant()");
            array.addGUIComponent(comboBox);
            return array;            
           
		 }
       
        return object;
        
	}
}
