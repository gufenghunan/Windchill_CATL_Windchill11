package com.catl.change.filter;



import wt.change2.WTChangeOrder2;
import wt.fc.Persistable;
import wt.org.WTPrincipal;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.vc.wip.WorkInProgressHelper;

import com.catl.change.workflow.ECWorkflowUtil;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class CustomerFileFilter extends DefaultSimpleValidationFilter{
    @Override
    public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria validationCriteria){
        System.out.println("Enter into CustomerFileFileter");
        
        Persistable persistable = validationCriteria.getContextObject().getObject();
        try{
            if (persistable instanceof WTChangeOrder2){
                WTChangeOrder2 eco = (WTChangeOrder2) persistable;
                if(ECWorkflowUtil.hasCustomerFile(eco)){
                    System.out.println("has customer file");
                    return UIValidationStatus.ENABLED;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return UIValidationStatus.HIDDEN;
    }
}

