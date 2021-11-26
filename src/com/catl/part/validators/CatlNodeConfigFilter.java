package com.catl.part.validators;

import com.catl.part.classification.NodeConfigHelper;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

import wt.fc.Persistable;

public class CatlNodeConfigFilter extends DefaultSimpleValidationFilter {

	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
		UIValidationStatus status = UIValidationStatus.HIDDEN;
		Persistable persistable = criteria.getContextObject().getObject();
		if(persistable instanceof LWCStructEnumAttTemplate){
			LWCStructEnumAttTemplate node = (LWCStructEnumAttTemplate)persistable;
			if(NodeConfigHelper.instantiable(node)){
				return UIValidationStatus.ENABLED;
			}
		}
		return status;
	}

}
