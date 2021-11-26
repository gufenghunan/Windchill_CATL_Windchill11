package com.catl.promotion.validator;

import wt.fc.Persistable;
import wt.org.WTPrincipal;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.vc.wip.WorkInProgressHelper;
import wt.workflow.definer.WfProcessTemplate;
import wt.workflow.utility.WorkflowTemplateCheckIn;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;
import wt.workflow.work.WorkflowHelper;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class PNTreeListFilter extends DefaultSimpleValidationFilter{
	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria validationCriteria){
		Persistable persistable = validationCriteria.getContextObject().getObject();
		System.out.println("**************************="+persistable);
		try{
			if (persistable instanceof WorkItem){
			    WorkItem item = (WorkItem) persistable;
			    WfAssignedActivity wfa = (WfAssignedActivity) item.getSource().getObject();
			    WfProcessTemplate wft = (WfProcessTemplate)wfa.getParentProcess().getTemplate().getObject();
			    if(wft.getName().equals("零部件（BOM）与图纸发布流程"))
			        return UIValidationStatus.ENABLED;
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		return UIValidationStatus.HIDDEN;
	}
}
