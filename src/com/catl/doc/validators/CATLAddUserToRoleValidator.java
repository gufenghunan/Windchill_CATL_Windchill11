package com.catl.doc.validators;

import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import wt.inf.container.WTContainer;
import wt.log4j.LogR;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTUser;
import wt.util.WTException;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationResult;
import com.ptc.core.ui.validation.UIValidationResultSet;
import com.ptc.windchill.enterprise.team.validators.TeamCCActionColumnValidator;

public class CATLAddUserToRoleValidator extends TeamCCActionColumnValidator {

    private static final String CLASSNAME = CATLAddUserToRoleValidator.class.getName();

    private static final Logger log = LogR.getLogger(CLASSNAME);

    @Override
    public UIValidationResultSet performLimitedPreValidation(UIValidationKey uivalidationkey,
            UIValidationCriteria uivalidationcriteria,Locale local) throws WTException {

    	System.out.println("目目目目目目目目目目目目目目目目");
    	uivalidationkey.getComponentID();
    	UIValidationResultSet resultSet = new UIValidationResultSet();
    	UIValidationResultSet oldresultSet = super.performLimitedPreValidation(uivalidationkey, uivalidationcriteria, local);
    	List<UIValidationResult> list = oldresultSet.getAllResults();
    	for(int i = 0; i < list.size(); i ++){
    		UIValidationResult uivr = list.get(i);
    		System.out.println(uivr.getTargetObject());
    		System.out.println(uivr.getValidationKey());
    		System.out.println(uivr.getStatus());
    	}
       /* Object row = uivalidationcriteria.getContextObject().getObject();
        log.info("ContextObject\t"+row);
        Object obj = uivalidationcriteria.getPageObject().getObject();
        log.info("PageObject\t"+obj);
        if(obj instanceof WTDocument){
        	try {
                WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
                WTContainer container = (WTContainer) uivalidationcriteria.getParentContainer().getObject();
                if(container != null){
                	if(!CommonUtil.isSiteAdmin(user)){
                		uiValidationStatus = UIValidationStatus.DISABLED;
                	}
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        
        log.debug("CATLAddUserToRoleValidator.ValidationStatus：" + uiValidationStatus);*/
        return oldresultSet;
    }
    
    public static boolean isEnableUser(WTContainer container, WTUser user) throws WTException{
    	WTGroup adminGroup = OrganizationServicesHelper.manager.getGroup("Administrators");
    	
    	boolean isadmin = OrganizationServicesHelper.manager.isMember(adminGroup, user);
    	if(isadmin){
    		return true;
    	}   	    	
    	return false;
    	
    }
       
   
}