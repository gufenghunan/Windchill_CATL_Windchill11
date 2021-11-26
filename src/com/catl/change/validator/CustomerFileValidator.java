package com.catl.change.validator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;


import wt.access.AccessControlHelper;
import wt.access.AccessPermission;
import wt.change2.ChangeActivity2;
import wt.fc.Persistable;
import wt.fc.WTReference;
import wt.lifecycle.LifeCycleManaged;
import wt.log4j.LogR;
import wt.org.WTPrincipal;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import wt.vc.wip.WorkInProgressHelper;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;

import com.ptc.core.ui.validation.DefaultUIComponentValidator;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationResult;
import com.ptc.core.ui.validation.UIValidationResultSet;
import com.ptc.core.ui.validation.UIValidationStatus;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.windchill.enterprise.history.validators.LifecycleHistoryNavValidator;
import com.ptc.wvs.livecycle.assembler.Locale;

  public class CustomerFileValidator extends  DefaultUIComponentValidator   {

      private static final Logger  logger = LogR.getLogger(CustomerFileValidator.class.getName());
      
      public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
        
          WTReference wtRef = criteria.getContextObject();
         if( WorkItem.class.isAssignableFrom(wtRef.getReferencedClass())){
                 
              WorkItem workItem = (WorkItem)wtRef.getObject();
              WfAssignedActivity activity =  (WfAssignedActivity)workItem.getSource().getObject();
              String activityName = activity.getName();
              if(logger.isDebugEnabled()){
                    logger.debug("CATLZ Activity Name = " + activityName);
              }
               if (activityName.equalsIgnoreCase("客户评审")) {
                      return UIValidationStatus.ENABLED;
                }else{
                     return UIValidationStatus.HIDDEN;
                }
         }else{
              return UIValidationStatus.HIDDEN;
         }
      }
     /* 
      public UIValidationResultSet performFullPreValidation(UIValidationKey validationKey,
              UIValidationCriteria validationCriteria,
              Locale locale)
                throws WTException   
     
            {
            System.out.println(" Enter into Customer File Validator");
              UIValidationResultSet validationResultSet = UIValidationResultSet.newInstance();
              WTReference workItemRef = validationCriteria.getContextObject();
              String str = validationKey.getComponentID();
              if (!(workItemRef.getObject() instanceof WorkItem)) {
                     throw new WTException("Object is not WorkItem " + workItemRef.getObject());
              }
              WorkItem workItem = (WorkItem)workItemRef.getObject();
           if ((hasReadAccessToActivity(workItem)) && (showCustomerFile(workItem))) {
                  System.out.println("ENABLED");
                  validationResultSet.addResult(UIValidationResult.newInstance(validationKey, UIValidationStatus.ENABLED, workItemRef));
                } else {
                  validationResultSet.addResult(UIValidationResult.newInstance(validationKey, UIValidationStatus.HIDDEN, workItemRef));
                }
            
              validationResultSet.addResult(UIValidationResult.newInstance(validationKey, UIValidationStatus.HIDDEN, workItemRef));
              
              
              
              return validationResultSet;
            }   
           */
 
 }
