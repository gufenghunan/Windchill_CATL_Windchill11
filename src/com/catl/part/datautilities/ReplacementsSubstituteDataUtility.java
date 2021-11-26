package com.catl.part.datautilities;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartSubstituteLink;
import wt.util.WTException;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.dataUtilities.DefaultDataUtility;
import com.ptc.core.ui.resources.ComponentMode;

public class ReplacementsSubstituteDataUtility extends DefaultDataUtility {
	
	private static Logger logger = Logger.getLogger(ReplacementsSubstituteDataUtility.class);

	@Override
	public Object getDataValue(String componentId, Object obj, ModelContext modelContext)
			throws WTException {
	    //logger.debug("....................................obj"+obj);
	    if(obj instanceof WTPartSubstituteLink){
	        WTPartSubstituteLink subLink = (WTPartSubstituteLink)obj;
	        WTPartMaster wtPartMaster = subLink.getSubstitutes();
	        return wtPartMaster.getDefaultUnit().getDisplay();
	    }
		return super.getDataValue(componentId, obj, modelContext);
	}

}
