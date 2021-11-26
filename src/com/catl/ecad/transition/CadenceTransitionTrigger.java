package com.catl.ecad.transition;

import wt.fc.WTObject;
import wt.util.WTException;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfVariable;
import wt.workflow.work.WfAssignedActivity;

import com.catl.ecad.utils.ECADutil;
import com.ptc.xworks.workflow.WorkflowTransitionTrigger;
import com.ptc.xworks.workflow.template.ActivityNodeInfo;

public class CadenceTransitionTrigger implements WorkflowTransitionTrigger {
	
	//外购件厂商型号提交
	@Override
	public void execute(WfAssignedActivity activity, String transition, ActivityNodeInfo nodeInfo) throws WTException {
		
		ProcessData pdata = activity.getContext();
		WfVariable[]  wflist = pdata.getVariableList();
		WTObject pbo = (WTObject)ECADutil.getObjectByOid(wflist[1].getValue().toString());
		String actemdec = activity.getDescription();
		
		if(actemdec.contains("EDIT_CADENCEATTRS")){
			System.out.println("COME ON");
		}
		
	}
	
}
