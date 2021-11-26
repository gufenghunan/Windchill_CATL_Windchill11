package com.catl.change.DataUtility;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.util.WTException;
import wt.util.WTMessage;

import com.catl.change.util.ChangeConst;
import com.catl.common.util.GenericUtil;
import com.catl.common.util.IBAUtil;
import com.catl.part.PartConstant;
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


public class DCANameComboBoxDataUtility extends DefaultDataUtility{
	private static Logger logger=Logger.getLogger(DCANameComboBoxDataUtility.class.getName());
	@Override
	public Object getDataValue(String componentId, Object datum, ModelContext modelContext) throws WTException {
		Object object = super.getDataValue(componentId, datum, modelContext);
		
		GUIComponentArray array = new GUIComponentArray();
        ComboBox comboBox = new ComboBox(); 
        String o=CatlPropertyHelper.getDcaPropertyValue("DCATask");
        
        String[] ab=o.split(","); 
        ArrayList<String> displayList = new ArrayList<String>();
        ArrayList<String> internalList = new ArrayList<String>();
		
		Object obj = modelContext.getNmCommandBean().getPageOid().getRefObject();
		WTChangeOrder2 dcn = null;
		if(obj instanceof WTChangeOrder2){
			dcn = (WTChangeOrder2)obj;
			String infotype = (String) IBAUtil.getIBAValue(dcn, PartConstant.CATL_Allowed_DCA);   //显示类型
			if(infotype.equals(ChangeConst.DCAYXBJ_B)){
				ArrayList<String> taskNames = new ArrayList<String>();
				taskNames.addAll(Arrays.asList(ab));
				taskNames.remove(ChangeConst.YFGGRW);
	        	//String [] abc = {"",ChangeConst.WKYJHKZGGRW};
	        	//internalList.addAll(Arrays.asList(abc));
	            //displayList.addAll(Arrays.asList(abc));
	            internalList.addAll(taskNames);
	            displayList.addAll(taskNames);
	        }else if(infotype.equals(ChangeConst.DCAYXBJ_AB)){
	        	internalList.addAll(Arrays.asList(ab));
				displayList.addAll(Arrays.asList(ab));
	        }
		}else{
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
            if (modelContext.getDescriptorMode().equals(ComponentMode.EDIT)) {
                DefaultTypeInstance typeInstance=(DefaultTypeInstance)datum;
                String oid = typeInstance.getPersistenceIdentifier();
                logger.debug("oid==="+oid);
                WTChangeActivity2 eca=(WTChangeActivity2) GenericUtil.getInstance(oid);
                logger.debug("Dca number===="+eca.getNumber());
                String changename =(String) GenericUtil.getObjectAttributeValue(eca, "name");
//                String name =eca.getName();
//                logger.debug("Dca name ==="+name);
//                logger.debug("Dca changename ==="+changename);
//                comboBox.setSelected(name);
//                if(StringUtils.equals("wizardEdit", actionName)){
//                	columnName = WTMessage.formatLocalizedMessage("changeTask$edit${0}$|components$loadWizardStep${0}$___change_name___textbox", new Object[]{oid});
//                	TextBox tb = GuiComponentUtil.createTextBox(50);
//        			tb.setId(componentId+"_TB");
//                    tb.setColumnName(columnName);
//                    tb.setInputType("hidden");
//                    tb.setValue(name);
//                    
//                    tb.setValueHidden(true);
//                    array.addGUIComponent(tb);
//                }
                TextBox tb = GuiComponentUtil.createTextBox(50);
                tb.setColumnName(columnName);
                tb.setId(componentId);
                tb.setValue(changename);
                tb.setEditable(false);
                array.addGUIComponent(tb);
                return array;
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
