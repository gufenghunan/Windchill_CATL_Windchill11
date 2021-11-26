package com.catl.promotion.validator;

import wt.access.AccessControlHelper;
import wt.access.AccessPermission;
import wt.fc.Persistable;
import wt.inf.container.WTContainer;
import wt.inf.container._WTContained;
import wt.lifecycle.State;
import wt.org.WTPrincipal;

import com.catl.common.constant.PartState;
import com.catl.common.constant.TypeName;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class FAEMaterialsMaturityFilter extends DefaultSimpleValidationFilter {
    public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria validationCriteria) {
    	
        Persistable persistable = validationCriteria.getContextObject().getObject();
        UIValidationStatus uivs = UIValidationStatus.DISABLED;
        
        try {        	
        	WTContainer container = ((_WTContained) persistable).getContainer();
            State state = State.toState(PartState.OPEN);
            WTPrincipal user = validationCriteria.getUser().getPrincipal();
            if (AccessControlHelper.manager.hasAccess(user, TypeName.UpgradeFaePartMaturityPN, container.getDefaultDomainReference(), state, AccessPermission.CREATE)){
            	uivs = UIValidationStatus.ENABLED;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uivs;
    }
}