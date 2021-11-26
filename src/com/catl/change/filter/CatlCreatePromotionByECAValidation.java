package com.catl.change.filter;

import org.apache.log4j.Logger;

import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.fc.Persistable;
import wt.inf.container.WTContainerHelper;
import wt.org.WTPrincipal;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.catl.change.workflow.ECWorkflowUtil;
import com.catl.common.constant.ChangeState;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class CatlCreatePromotionByECAValidation extends DefaultSimpleValidationFilter {

	private static Logger logger = Logger.getLogger(CatlCreatePromotionByECAValidation.class.getName());

	public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria validationCriteria) {
		Persistable persistable = validationCriteria.getContextObject().getObject();
		WTPrincipal userPrincipal = null;
		try {
			userPrincipal = SessionHelper.manager.getPrincipal();
		} catch (WTException e) {
			e.printStackTrace();
		}
		logger.debug("now user is==" + userPrincipal.getName());
		if (isSiteAdmin(userPrincipal)) {
			return UIValidationStatus.ENABLED;
		}
		if (persistable instanceof WTChangeActivity2) {
			try {
				WTChangeActivity2 ca = (WTChangeActivity2) persistable;
				String caState = ca.getState().toString();
				logger.debug("ca state is:::::" + ca.getNumber() + ":" + caState);
				if (caState.equalsIgnoreCase(ChangeState.IMPLEMENTATION)) {
					return UIValidationStatus.ENABLED;
				} else {
					return UIValidationStatus.DISABLED;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return super.preValidateAction(key, validationCriteria);
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
