package com.catl.part.classification.datautilities;

import java.util.ArrayList;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.catl.part.classification.AttributeForFAE;
import com.catl.part.classification.ClassificationNodeConfig;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.dataUtilities.AttributeDataUtilityHelper;
import com.ptc.core.components.factory.dataUtilities.DefaultDataUtility;
import com.ptc.core.components.rendering.guicomponents.ComboBox;
import com.ptc.core.ui.resources.ComponentMode;

import wt.session.SessionHelper;
import wt.util.WTException;

public class NodeConfigAttrDataUtility extends DefaultDataUtility {

	@Override
	public Object getDataValue(String componentId, Object obj, ModelContext modelContext) throws WTException {
		if(modelContext.getDescriptorMode().equals(ComponentMode.EDIT)){
			if(StringUtils.equals(componentId, ClassificationNodeConfig.ATTRIBUTE_REF)){
				ClassificationNodeConfig nodeConfig = (ClassificationNodeConfig)obj;
				Locale local = SessionHelper.getLocale();
				ComboBox comboBox = new ComboBox();
				String columnName = AttributeDataUtilityHelper.getColumnName(componentId, obj, modelContext);
				comboBox.setId(componentId);
				comboBox.setColumnName(columnName);
				comboBox.setRequired(true);
				ArrayList<String> displayList = new ArrayList<String>();
		        ArrayList<String> internalList = new ArrayList<String>();
				for (AttributeForFAE attrRef : AttributeForFAE.getAttributeForFAESet()) {
					displayList.add(attrRef.getDisplay(local));
					internalList.add(attrRef.getStringValue());
				}
				comboBox.setValues(displayList);
				comboBox.setInternalValues(internalList);
				comboBox.setSelected(nodeConfig.getAttributeRef().getStringValue());
				return comboBox;
			}
		}
		return super.getDataValue(componentId, obj, modelContext);
	}

}
