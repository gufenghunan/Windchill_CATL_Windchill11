package com.catl.change.validator;

import wt.change2.WTChangeActivity2;
import wt.fc.Persistable;

import com.catl.common.constant.TypeName;
import com.catl.common.util.IBAUtil;
import com.catl.part.PartConstant;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class EditImplementPlanFilter extends DefaultSimpleValidationFilter {

	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
		Persistable persistable = criteria.getContextObject().getObject();
		if (persistable instanceof WTChangeActivity2) {
			WTChangeActivity2 eco = (WTChangeActivity2)persistable;
			TypeIdentifier type = TypeIdentifierUtility.getTypeIdentifier(eco);
			if (type.getTypename().endsWith(TypeName.CATL_DCA)) {
				Boolean AllowEdit = (Boolean) IBAUtil.getIBAValue(eco, PartConstant.CATL_Allow_Edit);
				if (!AllowEdit) {
					return  UIValidationStatus.HIDDEN;
				}				
			}
			
		}
		return super.preValidateAction(key, criteria);
	}

}
