package com.catl.require.filter;

import wt.access.AccessControlHelper;
import wt.access.AccessPermission;
import wt.fc.Persistable;
import wt.folder.Folder;
import wt.org.WTPrincipal;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.catl.require.constant.ConstantRequire;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

/**
 * 创建售后再利用件 是否在指定存储库中
 * @author hdong
 */
public class CATLCreateAfterSaleFilter extends DefaultSimpleValidationFilter {

	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
		UIValidationStatus status = UIValidationStatus.HIDDEN;
		Persistable persistable = criteria.getContextObject().getObject();
		if (persistable instanceof Folder) {
			Folder folder = (Folder) persistable;
			if(folder.getContainerName().equals(ConstantRequire.libary_aftersale)){
				WTPrincipal user;
				try {
					user = SessionHelper.getPrincipal();
					boolean access = AccessControlHelper.manager.hasAccess(user, folder, AccessPermission.MODIFY);
					if (!access) {
						status = UIValidationStatus.HIDDEN;
					}else{
						status = UIValidationStatus.ENABLED;
					}
				} catch (WTException e) {
					e.printStackTrace();
				}
			}else{
				status = UIValidationStatus.HIDDEN;
			}
			
		}
		return status;
	}

}
