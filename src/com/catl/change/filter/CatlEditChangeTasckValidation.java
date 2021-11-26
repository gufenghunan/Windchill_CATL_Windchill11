package com.catl.change.filter;

import org.apache.log4j.Logger;

import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeRequest2;
import wt.fc.Persistable;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.org.DirectoryContextProvider;
import wt.org.OrganizationServicesHelper;
import wt.org.WTOrganization;
import wt.org.WTPrincipal;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.catl.common.constant.ChangeState;
import com.catl.common.constant.PartState;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class CatlEditChangeTasckValidation  extends DefaultSimpleValidationFilter{

	private static Logger logger=Logger.getLogger(ReivseValidation.class.getName());
	public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria validationCriteria){
		Persistable persistable = validationCriteria.getContextObject().getObject();
		 WTPrincipal userPrincipal=null;
		try {
			userPrincipal = SessionHelper.manager.getPrincipal();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.debug("now user is=="+userPrincipal.getName());
		if (isSiteAdmin(userPrincipal)||isOrgAdministator(userPrincipal, "CATL")) {
			return UIValidationStatus.ENABLED;
		}
		if(persistable instanceof WTChangeActivity2)
		{
		try{
				WTChangeActivity2 eca=(WTChangeActivity2)persistable;
				String ecaState =eca.getState().toString();
                logger.debug("ecr state is:::::"+eca.getNumber()+":"+ecaState);
				if(ecaState.equalsIgnoreCase(ChangeState.OPEN)||ecaState.equalsIgnoreCase(ChangeState.REWORK)||ecaState.equalsIgnoreCase(ChangeState.EVALUATION)){
	                return UIValidationStatus.ENABLED;
	            }else {
					return UIValidationStatus.DISABLED;
				}
		} catch (Exception e){
			e.printStackTrace();
		}

		}else {
			  return UIValidationStatus.ENABLED;
		}
		
		return UIValidationStatus.HIDDEN;
	}
    public static boolean isSiteAdmin(WTPrincipal wtPrincipal) {
        try {
            return WTContainerHelper.service.isAdministrator(WTContainerHelper.service.getExchangeRef(), wtPrincipal);
        } catch (WTException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static boolean isOrgAdministator(WTPrincipal wtprincipal, String strOrgName) {
        try {
            DirectoryContextProvider dcp = WTContainerHelper.service.getExchangeContainer().getContextProvider();
            WTOrganization org = OrganizationServicesHelper.manager.getOrganization(strOrgName, dcp);
            if (org != null) {
                WTContainerRef wtcontainerref = WTContainerHelper.service.getOrgContainerRef(org);
                if (wtcontainerref != null) {
                    if (WTContainerHelper.service.isAdministrator(wtcontainerref, wtprincipal)) {
                        return true;
                    }
                }
            } else {
                System.out.println("WTOrganization is null.");
            }
        } catch (WTException e) {
            e.printStackTrace();
        }
        return false;
    }
}
