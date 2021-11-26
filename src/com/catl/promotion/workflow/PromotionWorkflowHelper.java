package com.catl.promotion.workflow;

import java.util.Set;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.fc.ObjectReference;
import wt.fc.WTObject;
import wt.log4j.LogR;
import wt.maturity.PromotionNotice;
import wt.org.WTPrincipal;
import wt.project.Role;
import wt.team.Team;
import wt.team.TeamHelper;
import wt.util.WTException;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WorkItem;

import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class PromotionWorkflowHelper {
	
	 private static Logger log = LogR.getLogger(PromotionWorkflowHelper.class.getName());
	
	public static WorkItem getWorkItem(NmCommandBean clientData) throws WTException {
        WTObject object = (WTObject) clientData.getPageOid().getRefObject();
        WorkItem currentWorkItem = null;
        if (object instanceof WorkItem) {
            currentWorkItem = (WorkItem) object;
        }

        if (currentWorkItem == null) {
            HttpServletRequest request = clientData.getRequest();
            String context = request.getParameter("compContext");
            String[] contexts = context.split("\\$");
            String workItemOid = contexts[2];
            String[] cStrings = workItemOid.split("\\:");
            if (cStrings.length == 2) {
                workItemOid = "OR:" + workItemOid;
            }
            Object obj = null;
            if (workItemOid != null) {
                obj = NmOid.newNmOid(workItemOid).getRefObject();
            }
            
            if (obj != null) {
                if (obj instanceof WorkItem) {
                    currentWorkItem = (WorkItem) obj;
                }
            }
        }

        return currentWorkItem;
    }
	
	//将参与者设置到角色列表，比如创建者
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static void initTeamMembersCreator(WTObject pbo, ObjectReference self, Set<String> roles) throws WTException{
		WTPrincipal principal = null;
			
		if(pbo instanceof PromotionNotice){
			PromotionNotice pn = (PromotionNotice)pbo;
			principal = pn.getCreator().getPrincipal();
		}else if(pbo instanceof WTChangeRequest2){
			WTChangeRequest2 ecr = (WTChangeRequest2)pbo;
			principal = ecr.getCreator().getPrincipal();
		}else if(pbo instanceof WTChangeOrder2){
			WTChangeOrder2 dcn = (WTChangeOrder2)pbo;
			principal = dcn.getCreator().getPrincipal();
		}
		if(principal == null) return;
			
		WfProcess process = (WfProcess)self.getObject();
		Team team = (Team) process.getTeamId().getObject();
		Vector<Role> wfRoles = team.getRoles();
		for(String r : roles){
			Role role = Role.toRole(r);
			if(wfRoles.contains(role)){
				TeamHelper.service.addRolePrincipalMap(role, principal, team);
			}
		}
	}

}
