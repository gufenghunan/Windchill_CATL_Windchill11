package com.catl.bom.load;

import wt.fc.Persistable;
import wt.org.WTPrincipal;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.vc.wip.WorkInProgressHelper;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class BOMLoadValidation extends DefaultSimpleValidationFilter{
	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria validationCriteria){
		Persistable persistable = validationCriteria.getContextObject().getObject();
		try{
			if (persistable instanceof WTPart){
				WTPart part = (WTPart) persistable;
				WTPrincipal wtprincipal = SessionHelper.manager.getPrincipal();
				if(WorkInProgressHelper.isCheckedOut(part, wtprincipal)){
	                return UIValidationStatus.ENABLED;
	            }
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		return UIValidationStatus.HIDDEN;
	}
}
