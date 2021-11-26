package com.catl.change.validator;

import wt.change2.WTChangeActivity2;
import wt.fc.Persistable;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.org.DirectoryContextProvider;
import wt.org.OrganizationServicesHelper;
import wt.org.WTOrganization;
import wt.org.WTPrincipal;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.catl.common.constant.PartState;
import com.catl.common.constant.TypeName;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class ChangeTaskObjectFilter extends DefaultSimpleValidationFilter {

	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
		Persistable persistable = criteria.getContextObject().getObject();
		String ComponentContext = criteria.getComponentContext().toString();
		if (ComponentContext.contains("actionModel:changeTask.affectedItems.table.create_edit")) {
			if (persistable instanceof WTChangeActivity2) {
				WTChangeActivity2 eco = (WTChangeActivity2) persistable;
				TypeIdentifier type = TypeIdentifierUtility.getTypeIdentifier(eco);
				if (type.getTypename().endsWith(TypeName.CATL_DCA) && eco.getState().toString().endsWith(PartState.IMPLEMENTATION)) {

					try {
						WTPrincipal currentUser = SessionHelper.getPrincipal();
						// 如果是管理员，则允许修改DCA的受影响对象
						if (isSiteAdmin(currentUser) || isOrgAdministator(currentUser, "CATL")) {
							return UIValidationStatus.ENABLED;
						}
						return UIValidationStatus.HIDDEN;
					} catch (WTException e) {
						e.printStackTrace();
						return UIValidationStatus.HIDDEN;
					}
				}

			}
		}
		if (super.preValidateAction(key, criteria) == null) {
			return UIValidationStatus.ENABLED;
		} else {
			return super.preValidateAction(key, criteria);
		}
	}

	public static boolean isSiteAdmin(WTPrincipal wtPrincipal) {
		try {
			return WTContainerHelper.service.isAdministrator(WTContainerHelper.service.getExchangeRef(), wtPrincipal);
		} catch (WTException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean isOrgAdministator(WTPrincipal wtprincipal, String strOrgName) {
		try {
			DirectoryContextProvider dcp = WTContainerHelper.service.getExchangeContainer().getContextProvider();
			WTOrganization org = OrganizationServicesHelper.manager.getOrganization(strOrgName, dcp);
			if (org != null) {
				WTContainerRef wtcontainerref = WTContainerHelper.service.getOrgContainerRef(org);
				if (wtcontainerref != null) {
					if (WTContainerHelper.service.isAdministrator(wtcontainerref, wtprincipal)) {
						return true;
					}
				}
			} else {
				System.out.println("WTOrganization is null.");
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return false;
	}
}
