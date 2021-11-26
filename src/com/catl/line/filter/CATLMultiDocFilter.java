package com.catl.line.filter;


import wt.access.AccessControlHelper;
import wt.access.AccessPermission;
import wt.fc.Persistable;
import wt.folder.Folder;
import wt.org.WTPrincipal;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.catl.common.util.PropertiesUtil;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class CATLMultiDocFilter extends DefaultSimpleValidationFilter{

	/**
	 * 批量创建文档的验证 
	 * 对当前文档有修改权限。
	 */
	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key,
			UIValidationCriteria criteria) {
		UIValidationStatus status = UIValidationStatus.HIDDEN;
			Persistable persistable = criteria.getContextObject().getObject();
			if(persistable instanceof Folder){
				Folder folder = (Folder) persistable;
				if(folder.getName().equals(PropertiesUtil.getValueByKey("config_autocad_folder"))){//在线束Autocadcad文件夹
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
