package com.catl.change.filter;

import org.apache.log4j.Logger;

import wt.change2.WTChangeRequest2;
import wt.fc.Persistable;
import wt.folder.SubFolder;
import wt.inf.container.WTContainerHelper;
import wt.org.WTPrincipal;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.catl.common.constant.ChangeState;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class CatlCreateECNValidation  extends DefaultSimpleValidationFilter{

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
		if (isSiteAdmin(userPrincipal)) {
			return UIValidationStatus.ENABLED;
		}
		if(persistable instanceof WTChangeRequest2)
		{
			try{
					WTChangeRequest2 ecr=(WTChangeRequest2)persistable;
					String ecrState =ecr.getState().toString();
	                logger.debug("ecr state is:::::"+ecr.getNumber()+":"+ecrState);
					if(ecrState.equalsIgnoreCase(ChangeState.IMPLEMENTATION)){
		                return UIValidationStatus.ENABLED;
		            }else {
						return UIValidationStatus.DISABLED;
					}
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		
		return UIValidationStatus.ENABLED;
	}
    public static boolean isSiteAdmin(WTPrincipal wtPrincipal) {
        try {
            return WTContainerHelper.service.isAdministrator(WTContainerHelper.service.getExchangeRef(), wtPrincipal);
        } catch (WTException e) {
            e.printStackTrace();
        }
        return false;
    }
}
