package com.catl.part;

import wt.change2.WTChangeOrder2;
import wt.fc.Persistable;
import wt.org.WTPrincipal;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.catl.change.filter.CatlEditChangeTasckValidation;
import com.catl.common.constant.TypeName;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class EditAttachmentsFilter extends DefaultSimpleValidationFilter {

	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key,
			UIValidationCriteria criteria) {
		Persistable persistable = criteria.getContextObject().getObject();
		
		if(persistable instanceof WTPart){
			return  UIValidationStatus.HIDDEN;
		} else if (persistable instanceof WTChangeOrder2) {
			WTChangeOrder2 eco = (WTChangeOrder2)persistable;
			TypeIdentifier type = TypeIdentifierUtility.getTypeIdentifier(eco);
			if (type.getTypename().endsWith(TypeName.dcn)) {
				return  UIValidationStatus.HIDDEN;
			}
			
		}
		return super.preValidateAction(key, criteria);
	}

}
