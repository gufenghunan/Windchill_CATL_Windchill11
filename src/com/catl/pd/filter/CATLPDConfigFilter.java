package com.catl.pd.filter;


import wt.org.WTGroup;
import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.catl.pd.constant.ConstantPD;
import com.catl.ri.validator.UserValidator;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class CATLPDConfigFilter extends DefaultSimpleValidationFilter{
	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key,
			UIValidationCriteria criteria) {
		UIValidationStatus status = UIValidationStatus.HIDDEN;
		WTUser user;
		try {
			user = (WTUser) SessionHelper.manager.getPrincipal();
			WTGroup agroup = UserValidator.findGroup(ConstantPD.pd_group_admin);
			if (agroup != null) {
				if (agroup.isMember(user)) {
					status = UIValidationStatus.ENABLED;
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return status;
	}
	
}
