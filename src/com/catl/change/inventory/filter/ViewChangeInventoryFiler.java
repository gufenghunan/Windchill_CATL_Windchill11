package com.catl.change.inventory.filter;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import wt.change2.ChangeActivity2;
import wt.change2.WTChangeActivity2;
import wt.fc.WTReference;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.log4j.LogR;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.pdmlink.PDMLinkProduct;
import wt.project.Role;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

/**
 * 
 * @author 
 */

public class ViewChangeInventoryFiler extends DefaultSimpleValidationFilter {

	
	public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
		
		WTReference ref =  criteria.getContextObject();
	    if (WTChangeActivity2.class.isAssignableFrom(ref.getReferencedClass())){
	     
	        return UIValidationStatus.ENABLED;
	    }else{
	      
	        return UIValidationStatus.HIDDEN;
	    }
    }


   
}
