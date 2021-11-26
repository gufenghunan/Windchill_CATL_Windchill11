package com.catl.promotion.workflow;

import java.util.Enumeration;

import com.catl.common.constant.RoleName;

import wt.fc.ObjectReference;
import wt.fc.WTObject;
import wt.project.Role;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;

public class PlatformChangeHelper {
    public static void checkRole(WTObject pbo, ObjectReference self) throws WTException{
    	    SessionServerHelper.manager.setAccessEnforced(false);
    	    try{
				WfAssignedActivity acivity = (WfAssignedActivity) self.getObject();
				WfProcess process = acivity.getParentProcess();
				StringBuffer error = new StringBuffer();
				Enumeration manager = process.getPrincipals(Role.toRole(RoleName.DEPARTMENT_MANAGER));
				if(!manager.hasMoreElements()){
					error.append("部门经理角色，至少选择一人\n");
				}
	    		if(error.length()>0){
	    			throw new WTException(error.toString());
	    		}
    	    }catch(Exception e){
    	    	 throw e;
    	    }finally{
    	    	SessionServerHelper.manager.setAccessEnforced(true);
    	    }
    }

}
