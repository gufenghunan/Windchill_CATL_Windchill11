package com.catl.ri.filter;


import wt.org.WTGroup;
import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.catl.ri.constant.ConstantRI;
import com.catl.ri.validator.UserValidator;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class CATLRIConfigFilter extends DefaultSimpleValidationFilter{
	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key,
			UIValidationCriteria criteria) {
		UIValidationStatus status = UIValidationStatus.HIDDEN;
		WTUser user;
		try {
			user = (WTUser) SessionHelper.manager.getPrincipal();
			WTGroup agroup = UserValidator.findGroup(ConstantRI.ri_group_adminA);
			if (agroup != null) {
				if (agroup.isMember(user)) {
					status = UIValidationStatus.ENABLED;
				}
			}
			WTGroup bgroup = UserValidator.findGroup(ConstantRI.ri_group_adminB);
			if (bgroup != null) {
				if (bgroup.isMember(user)) {
					status = UIValidationStatus.ENABLED;
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return status;
	}
	
}
