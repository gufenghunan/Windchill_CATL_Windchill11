package com.catl.promotion.validator;

import wt.inf.container.WTContainerHelper;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.catl.promotion.util.PromotionConst;
import com.catl.promotion.util.PromotionUtil;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class NotValidSuperstratumFilter extends DefaultSimpleValidationFilter {

	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key,UIValidationCriteria criteria) {
		
		UIValidationStatus status = UIValidationStatus.HIDDEN;
		try {
			WTUser user = (WTUser) SessionHelper.getPrincipal();
			
			//如果是管理员，可直接查看
			if (isSiteAdmin(user)){
				return UIValidationStatus.ENABLED;
			}
			
			String groupName = PromotionConst.PDM_Department_Member_GROUP;
			WTGroup group = PromotionUtil.queryGroup(groupName);
			if (group != null && OrganizationServicesHelper.manager.isMember(group, user)) {
				status = UIValidationStatus.ENABLED;
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		
		return status;		
	}
	
	public static boolean isSiteAdmin(WTPrincipal wtPrincipal) {
        try {
            return WTContainerHelper.service.isAdministrator(WTContainerHelper.service.getExchangeRef(), wtPrincipal);
        } catch (WTException e) {
            e.printStackTrace();
        }
        return false;
    }
}
