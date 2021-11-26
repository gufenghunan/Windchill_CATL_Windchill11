package com.catl.doc.DataUtility;

import java.lang.reflect.Array;
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
import com.ptc.core.components.rendering.guicomponents.AttributeInputComponent;
import com.ptc.core.components.rendering.guicomponents.AttributeInputCompositeComponent;
import com.ptc.core.components.rendering.guicomponents.ComboBox;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;
import com.ptc.core.components.rendering.guicomponents.GuiComponentUtil;
import com.ptc.core.components.rendering.guicomponents.StringInputComponent;
import com.ptc.core.components.rendering.guicomponents.TextBox;
import com.ptc.core.meta.type.common.impl.DefaultTypeInstance;
import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.netmarkets.util.beans.NmCommandBean;


public class IBASubCategoryComboBoxDataUtility extends DefaultDataUtility{
	private static Logger logger=Logger.getLogger(IBASubCategoryComboBoxDataUtility.class.getName());
	@Override
	public Object getDataValue(String componentId, Object datum, ModelContext modelContext) throws WTException {

		Object object = super.getDataValue(componentId, datum, modelContext);
		AttributeInputCompositeComponent comp = (AttributeInputCompositeComponent)object;
	    StringInputComponent aic = (StringInputComponent) comp.getValueInputComponent();
	    List<String> DisplayValues = new ArrayList<String>();
	    
	    DisplayValues.add("");
	    DisplayValues.addAll( aic.getDisplayValues() );
	    
	    GUIComponentArray array = new GUIComponentArray();
	    String columnName = AttributeDataUtilityHelper.getColumnName(componentId, datum, modelContext);
	    
	    ComboBox comboBox = new ComboBox();
	    comboBox.setId(componentId);
	    comboBox.setColumnName(columnName);
	    comboBox.setInternalValues((ArrayList<String>) DisplayValues);
	    comboBox.setValues((ArrayList<String>) DisplayValues);
	    comboBox.setEnabled(aic.isEnabled());
	    comboBox.addJsAction("onchange","loadHarnessVariant()");
	    comboBox.setEditable(aic.isEditable());
	    comboBox.setRequired(aic.isRequired());
	    
	    if (modelContext.getDescriptorMode().equals(ComponentMode.EDIT)) {
	    	DefaultTypeInstance typeInstance=(DefaultTypeInstance)datum;
            String oidString=typeInstance.getIdentifier().getInstanceIdentifier().toString();
            String oid ="VR:wt.doc.WTDocument:"+oidString.substring(oidString.lastIndexOf("|")+1,oidString.length());
            ReferenceFactory rf = new ReferenceFactory();
            Persistable obj = rf.getReference(oid).getObject();
            WTDocument docObj= (WTDocument) obj;
	    	String subCategorySelected = (String) GenericUtil.getObjectAttributeValue(docObj, "subCategory");
	    	comboBox.setSelected(subCategorySelected);
	    }
	    array.addGUIComponent(comboBox);
	    
	    return array;            
	}
}
