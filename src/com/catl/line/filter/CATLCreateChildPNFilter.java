package com.catl.line.filter;

import com.catl.common.util.PropertiesUtil;
import com.catl.line.constant.ConstantLine;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

import wt.access.AccessControlHelper;
import wt.access.AccessPermission;
import wt.fc.Persistable;
import wt.folder.Folder;
import wt.org.WTPrincipal;
import wt.session.SessionHelper;
import wt.util.WTException;

/**
 * 创建衍生PN 当前位置是否有修改内容权限
 * @author hdong
 */
public class CATLCreateChildPNFilter extends DefaultSimpleValidationFilter {

	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
		UIValidationStatus status = UIValidationStatus.HIDDEN;
		Persistable persistable = criteria.getContextObject().getObject();
		if (persistable instanceof Folder) {
			Folder folder = (Folder) persistable;
			if(!folder.getContainerName().equals(ConstantLine.libary_lineparentpn)&&folder.getName().equals(PropertiesUtil.getValueByKey("config_createchildpn_folder"))){//在零部件
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
