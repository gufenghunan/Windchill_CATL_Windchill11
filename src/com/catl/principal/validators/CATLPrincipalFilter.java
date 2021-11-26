package com.catl.principal.validators;


import wt.org.WTPrincipal;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.catl.common.util.UserUtil;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class CATLPrincipalFilter extends DefaultSimpleValidationFilter{

	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key,
			UIValidationCriteria criteria) {
		UIValidationStatus status = UIValidationStatus.HIDDEN;
		try {
			WTPrincipal principal = SessionHelper.getPrincipal();
			if(UserUtil.isOrgAdministator(principal) || UserUtil.isSiteAdmin(principal)){
				return UIValidationStatus.ENABLED;
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return status;
	}
	
}
