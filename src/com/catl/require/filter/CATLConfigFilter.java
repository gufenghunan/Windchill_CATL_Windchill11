package com.catl.require.filter;


import wt.org.WTGroup;
import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.catl.battery.constant.ConstantBattery;
import com.catl.pd.constant.ConstantPD;
import com.catl.ri.constant.ConstantRI;
import com.catl.ri.validator.UserValidator;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class CATLConfigFilter extends DefaultSimpleValidationFilter{
	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key,
			UIValidationCriteria criteria) {
		UIValidationStatus status = UIValidationStatus.HIDDEN;
		WTUser user;
		try {
			user = (WTUser) SessionHelper.manager.getPrincipal();
			WTGroup agroup = UserValidator.findGroup(ConstantBattery.evc_group_admin);
			WTGroup bgroup = UserValidator.findGroup(ConstantRI.ri_group_adminA);
			WTGroup dgroup = UserValidator.findGroup(ConstantRI.ri_group_adminB);
			WTGroup cgroup = UserValidator.findGroup(ConstantPD.pd_group_admin);
			if (agroup != null) {
				if (agroup.isMember(user)) {
					status = UIValidationStatus.ENABLED;
				}
			}
			if (bgroup != null) {
				if (bgroup.isMember(user)) {
					status = UIValidationStatus.ENABLED;
				}
			}
			if (dgroup != null) {
				if (dgroup.isMember(user)) {
					status = UIValidationStatus.ENABLED;
				}
			}
			if (cgroup != null) {
				if (cgroup.isMember(user)) {
					status = UIValidationStatus.ENABLED;
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return status;
	}
	
}
