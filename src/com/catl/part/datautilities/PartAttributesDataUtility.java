package com.catl.part.datautilities;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import wt.part.WTPart;
import wt.util.WTException;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.dataUtilities.DefaultDataUtility;
import com.ptc.core.ui.resources.ComponentMode;

public class PartAttributesDataUtility extends DefaultDataUtility {
	
	private static Logger logger = Logger.getLogger(PartAttributesDataUtility.class);

	@Override
	public Object getDataValue(String componentId, Object obj, ModelContext modelContext)
			throws WTException {
		if(modelContext.getDescriptorMode().equals(ComponentMode.EDIT)){
			Object refObj = modelContext.getNmCommandBean().getActionOid().getRefObject();
			if(refObj instanceof WTPart){
				WTPart part = (WTPart)refObj;
				if(StringUtils.equals(componentId, "oldPartNumber")){
					String version = part.getVersionIdentifier().getValue();
					if(!StringUtils.equals(version, "1")){
						logger.info("====getRawValue:"+modelContext.getRawValue());
						return modelContext.getRawValue();
					}
				}
			}
		}
		return super.getDataValue(componentId, obj, modelContext);
	}

}
