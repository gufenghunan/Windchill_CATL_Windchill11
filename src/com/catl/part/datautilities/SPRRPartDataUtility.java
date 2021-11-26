package com.catl.part.datautilities;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import wt.part.WTPart;
import wt.services.applicationcontext.implementation.ServiceProperties;
import wt.util.WTException;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.dataUtilities.DefaultDataUtility;
import com.ptc.core.components.rendering.guicomponents.AttributeDisplayCompositeComponent;
import com.ptc.core.components.rendering.guicomponents.AttributeInputCompositeComponent;
import com.ptc.core.ui.resources.ComponentMode;

public class SPRRPartDataUtility extends DefaultDataUtility {

	private static Logger logger = Logger.getLogger(SPRRPartDataUtility.class);

	@Override
	public Object getDataValue(String componentId, Object obj,
			ModelContext modelContext) throws WTException {
		Object object = super.getDataValue(componentId, obj, modelContext);

		// 获取物料组
		Properties wtproperties = ServiceProperties
				.getServiceProperties("WTServiceProviderFromProperties");
		String PartGroup = wtproperties.getProperty("PartGroup");
		if (ComponentMode.VIEW.equals(modelContext.getDescriptorMode())) {
			WTPart part = (WTPart) obj;
			AttributeDisplayCompositeComponent acc = (AttributeDisplayCompositeComponent) object;
			acc.setComponentHidden(true);
			if (PartGroup != null
					&& PartGroup.indexOf(part.getNumber().split("-")[0]) != -1) {
				acc.setComponentHidden(false);
			}
		}
		return object;
	}

}
