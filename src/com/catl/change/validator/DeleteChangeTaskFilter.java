package com.catl.change.validator;

import wt.access.AccessControlHelper;
import wt.access.AccessPermission;
import wt.change2.WTChangeActivity2;
import wt.fc.Persistable;
import wt.org.WTPrincipal;
import wt.util.WTException;

import com.catl.common.constant.ChangeState;
import com.catl.common.constant.TypeName;
import com.catl.common.util.IBAUtil;
import com.catl.part.PartConstant;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;


public class DeleteChangeTaskFilter extends DefaultSimpleValidationFilter {

	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
		Persistable persistable = criteria.getContextObject().getObject();		
		try {
			WTPrincipal user = criteria.getUser().getPrincipal();
			if (persistable instanceof WTChangeActivity2) {
				WTChangeActivity2 eca = (WTChangeActivity2)persistable;
				TypeIdentifier type = TypeIdentifierUtility.getTypeIdentifier(eca);
				if (type.getTypename().endsWith(TypeName.CATL_DCA)) {
					if (AccessControlHelper.manager.hasAccess(user, eca, AccessPermission.DELETE)) {
						Boolean AllowEdit = (Boolean) IBAUtil.getIBAValue(eca, PartConstant.CATL_Allow_Edit);
						if (AllowEdit && !eca.getState().getState().toString().equals(ChangeState.IMPLEMENTATION)) {
							return  UIValidationStatus.ENABLED;
						}
					}			
				}
				
			}
		} catch (WTException e) {
			e.printStackTrace();
		}		
		return UIValidationStatus.HIDDEN;
	}

}
