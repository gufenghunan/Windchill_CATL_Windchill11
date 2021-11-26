package com.catl.part;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

import wt.org.DirectoryContextProvider;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;

public class ImportOptimumSelectingLevelFilter extends DefaultSimpleValidationFilter
{
	public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria)
	{
		UIValidationStatus status = UIValidationStatus.DISABLED;
		boolean checkAccess = SessionServerHelper.manager.setAccessEnforced(false);
		try
		{
			String[] defaultService = OrganizationServicesHelper.manager.getDirectoryServiceNames();
			DirectoryContextProvider dcp = OrganizationServicesHelper.manager.newDirectoryContextProvider(defaultService, new String[] { "subtree" });
			WTGroup group = OrganizationServicesHelper.manager.getGroup("优选管理组", dcp);
			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			if (group.isMember(user))
			{
				status = UIValidationStatus.ENABLED;
			}
		} catch (WTException wte)
		{
			wte.printStackTrace();
		} finally
		{
			SessionServerHelper.manager.setAccessEnforced(checkAccess);
		}
		return status;
	}
}
