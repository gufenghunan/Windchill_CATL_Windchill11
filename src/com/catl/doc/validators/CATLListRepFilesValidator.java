package com.catl.doc.validators;

import java.util.List;

import org.apache.log4j.Logger;

import wt.clients.beans.query.WT;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.log4j.LogR;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.vc.wip.WorkInProgressHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfState;

import com.catl.change.DataUtility.CatlPropertyHelper;
import com.catl.ecad.bean.CadenceAttributeBean;
import com.catl.ecad.dbs.CadenceXmlObjectUtil;
import com.catl.ecad.utils.ECADConst;
import com.catl.ecad.utils.ECADutil;
import com.catl.ecad.utils.WorkflowHelper;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.windchill.enterprise.workflow.WorkflowCommands;
import com.ptc.windchill.enterprise.wvs.repsAndMarkups.validators.ListRepFilesValidator;

public class CATLListRepFilesValidator extends ListRepFilesValidator {

    private static final String CLASSNAME = CATLListRepFilesValidator.class.getName();

    private static final Logger log = LogR.getLogger(CLASSNAME);

    public UIValidationStatus preValidateAction(UIValidationKey uivalidationkey,
            UIValidationCriteria uivalidationcriteria) {

    	uivalidationkey.getComponentID();
        UIValidationStatus uiValidationStatus = super.preValidateAction(uivalidationkey, uivalidationcriteria);

        try {
            WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
            WTContainer container = (WTContainer) uivalidationcriteria.getParentContainer().getObject();
            if(container != null){
            	if(!isEnableUser(container, user)){
            		uiValidationStatus = UIValidationStatus.DISABLED;
            	}
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.debug("CATLListRepFilesValidator.ValidationStatus：" + uiValidationStatus);
        return uiValidationStatus;
    }
    
    public static boolean isEnableUser(WTContainer container, WTUser user) throws WTException{
    	String roles = CatlPropertyHelper.getRepUserPropertyValue("ListRepFileUser");
    	String role[] = roles.split(",");
    	for (int i = 0; i < role.length; i++) {
    		List<WTUser> users = WorkflowHelper.getRoleUsers(container, role[i]);
        	if(users.contains(user)){
        		return true;
        	}
		}
    	
    	
    	return false;
    	
    }
       
   
}