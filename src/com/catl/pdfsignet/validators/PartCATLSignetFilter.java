package com.catl.pdfsignet.validators;

import org.apache.commons.lang.StringUtils;

import wt.fc.Persistable;
import wt.lifecycle.IteratedLifeCycleManaged;
import wt.org.WTPrincipal;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.catl.common.util.PartUtil;
import com.catl.common.util.UserUtil;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class PartCATLSignetFilter extends DefaultSimpleValidationFilter {

	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key,
			UIValidationCriteria criteria) {
		UIValidationStatus status = UIValidationStatus.HIDDEN;
		try {
			Persistable persistable = criteria.getContextObject().getObject();
			WTPrincipal principal = SessionHelper.getPrincipal();
			if(!(UserUtil.isOrgAdministator(principal) || UserUtil.isSiteAdmin(principal) || UserUtil.checkCurrentUserInOrgGroup("DC签章组"))){
				return UIValidationStatus.HIDDEN;
			}
			if(persistable instanceof WTPart){
				IteratedLifeCycleManaged lcm = (IteratedLifeCycleManaged)persistable;
				String stateStr = lcm.getState().toString();
				if(StringUtils.equals(stateStr, "RELEASED") && PartUtil.isLastedWTPart((WTPart)persistable)){
					return UIValidationStatus.ENABLED;
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return status;
	}

}
