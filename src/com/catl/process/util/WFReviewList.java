package com.catl.process.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import wt.fc.ObjectIdentifier;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.log4j.LogR;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.project.ActorRole;
import wt.project.Role;
import wt.team.Team;
import wt.team.TeamHelper;
import wt.util.WTException;
import wt.workflow.definer.UserEventVector;
import wt.workflow.definer.WfAssignedActivityTemplate;
import wt.workflow.definer.WfProcessTemplate;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfBlock;
import wt.workflow.engine.WfConnector;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfVotingEventAudit;
import wt.workflow.work.WfAssignmentState;
import wt.workflow.work.WorkItem;







import com.catl.process.bean.ProcessStatusBean;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.windchill.enterprise.workflow.WfDataUtilitiesHelper;
import com.ptc.windchill.enterprise.workflow.WorkflowCommands;

public class WFReviewList {

    private static final String CLASSNAME = WFReviewList.class.getName();

    private static final Logger log = LogR.getLogger(CLASSNAME);

    private static final String ACTIVITYCONFIG = "activityConfig";
    
    private static final String WORKITEM_COMPLETED = "COMPLETED";
    private static final String WORKITEM_POTENTIAL = "POTENTIAL";
    
    private static final String SIGN_SEPARATOR = "QQQ;;;"; // separator

    /**
     * 获取流程所有活动节点信息
     * 
     * @param process
     * @return
     */
    public static List<ProcessStatusBean> getProcessInfo(WfProcess process) {
        List<ProcessStatusBean> userCommentList = new ArrayList<ProcessStatusBean>();

        if (process != null) {
            try {
                List<ProcessStatusBean> workItemCommentList = getAllWorkItemComments(process);

                userCommentList.addAll(workItemCommentList);

                String currActivity = workItemCommentList.get(workItemCommentList.size() - 1).getActivityName();

                String activityConfig = "";
                ProcessData processdata = null;
                if (process != null) {
                    processdata = process.getContext();
                    if (processdata != null && processdata.getValue(ACTIVITYCONFIG) != null) {
                        activityConfig = (String) processdata.getValue(ACTIVITYCONFIG);
                        log.debug("activityConfig========>>"+activityConfig);
                    }
                }

                // activityConfig = "提交;EBOM编辑;会签及标准化审查;部门领导审批";

                HashMap<String, Integer> activityConfigMap = new HashMap<String, Integer>();
                if (!activityConfig.equals("") && activityConfig.length() > 0) {
                    String[] pString = activityConfig.split(";");
                    for (int i = 0; i < pString.length; i++) {
                        activityConfigMap.put(pString[i], i + 1);
                    }
                    log.debug("activityConfigMap========>>"+activityConfigMap);
                    log.debug("currActivity========>>"+currActivity);
                    int currNumNo = activityConfigMap.get(currActivity) == null ? 0 : activityConfigMap.get(currActivity);

                    if (currNumNo > 0) {
                        Map<String, String> waatMap = getWfAssignedActivityTemplateMap(process);
                        List<String> unlList = new ArrayList<String>();
                        Iterator iterator = waatMap.entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry entry = (Map.Entry) iterator.next();
                            String key = (String) entry.getKey();
                            String userName = (String) entry.getValue();

                            String[] comments = key.split(SIGN_SEPARATOR);
                            String activityName = comments[0];
                            String roleName = comments[1];
                            if(activityConfigMap.get(activityName)!=null){
                                int num = activityConfigMap.get(activityName);
                                log.debug("num=" + num + "-----currNumNo=" + currNumNo);
                                String userComments = num + SIGN_SEPARATOR + roleName + SIGN_SEPARATOR + activityName + SIGN_SEPARATOR + userName;
                                if (num > currNumNo) {
                                    unlList.add(userComments);
                                }
                            }
                        }

                        sortList(unlList);// 对未完成的任务排序
                        log.debug("unlList========>>"+unlList);
                        for (int i = 0; i < unlList.size(); i++) {
                            String userComments = unlList.get(i);

                            String[] comments = userComments.split(SIGN_SEPARATOR);
                            String roleName = comments[1];
                            String activityName = comments[2];
                            String userName = "";
                            if (comments.length > 3) {
                                userName = comments[3];
                            }

                            ProcessStatusBean pStatusBean = new ProcessStatusBean();
                            pStatusBean.setActivityName(activityName);
                            pStatusBean.setAssignee(userName);
                            pStatusBean.setWorkState("");
                            pStatusBean.setRoleName(roleName);
                            pStatusBean.setWorkVote("");
                            pStatusBean.setWorkComments("");
                            pStatusBean.setCompletedBy("");
                            pStatusBean.setDeadline("");
                            pStatusBean.setCompletedDate("");

                            userCommentList.add(pStatusBean);
                        }
                    }
                }
            } catch (WTException e) {
                e.printStackTrace();
            }
        }
        log.debug("userCommentList>>>>" + userCommentList);
        return userCommentList;
    }

    /**
     * 获取任务节点信息
     * 
     * @param process
     * @return
     * @throws WTException
     */
    public static List<ProcessStatusBean> getAllWorkItemComments(WfProcess process) throws WTException {
        List<ProcessStatusBean> userCommentList = new ArrayList<ProcessStatusBean>();
        List<ProcessStatusBean> cStatusBeans = new ArrayList<ProcessStatusBean>();
        List<ProcessStatusBean> pStatusBeans = new ArrayList<ProcessStatusBean>();

        wt.fc.ReferenceFactory rf = new wt.fc.ReferenceFactory();
        String processOid = rf.getReferenceString(process);
        NmOid oid = NmOid.newNmOid(processOid);
        QueryResult qr = WorkflowCommands.getRouteStatus(oid);
        while (qr.hasMoreElements()) {
            WorkItem workItem = (WorkItem) qr.nextElement();

            Role role = workItem.getRole();
            String roleName = role.getDisplay();
            WfAssignmentState workState = workItem.getStatus();
            WTPrincipalReference prinRef = (WTPrincipalReference) workItem.getOwnership().getOwner();// 负责人
            // WTPrincipalReference currentUserRef = SessionHelper.manager.getPrincipalReference();
            WfActivity wfa = (WfActivity) workItem.getSource().getObject();// 活动节点

            String workVote = "";// 路由
            String workComments = "";
            if (workState.toString().equals(WORKITEM_COMPLETED)) {
                WfVotingEventAudit event = WfDataUtilitiesHelper.getMatchingEventAudit(workItem);
                UserEventVector eventList = event.getEventList();

                for (int i = 0; eventList != null && i < eventList.size(); i++) {
                    if (workVote.length() > 0) {
                        workVote += ",";
                    }
                    workVote += eventList.get(i);
                }

                workComments = event.getUserComment(); // 意见
            }

            String completedBy = workItem.getCompletedBy() == null ? "" : workItem.getCompletedBy();// 完成人
            Timestamp deadline = wfa.getDeadline();// 截止日期
            String deadlineStr = "";
            if (deadline != null) {
                deadlineStr = deadline.toString();
            }

            Timestamp completedDate = workItem.getModifyTimestamp();// 完成日期
            String completedDateStr = String.valueOf(completedDate);
            if (workState.toString().equals(WORKITEM_POTENTIAL)) {
                completedDateStr = "";
            }

            //System.out.println("workname========>" + wfa.getName() + "role===>" + roleName + "state===>" + workState + "用户=====>" + prinRef.getFullName() + "completedBy==>" + completedBy
                    //+ "deadline==>" + deadlineStr + "completedDate===>" + completedDate);

            String activityName = wfa.getName();

            ProcessStatusBean pStatusBean = new ProcessStatusBean();

            pStatusBean.setWorkState(workState.toString());
            pStatusBean.setActivityName(activityName);
            pStatusBean.setRoleName(role.getDisplay());
            pStatusBean.setAssignee(prinRef.getFullName());
            pStatusBean.setWorkVote(workVote);
            pStatusBean.setWorkComments(workComments);
            pStatusBean.setCompletedBy(completedBy);
            pStatusBean.setDeadline(deadlineStr);
            pStatusBean.setCompletedDate(completedDateStr);

            if (workState.toString().equals(WORKITEM_POTENTIAL)) {
                pStatusBeans.add(pStatusBean);
            } else if (workState.toString().equals(WORKITEM_COMPLETED)) {
                cStatusBeans.add(pStatusBean);
            }
        }

        sortPStatusBeans(cStatusBeans);

        for (int i = 0; i < cStatusBeans.size(); i++) {
            ProcessStatusBean pStatusBean = cStatusBeans.get(i);
            userCommentList.add(pStatusBean);
        }

        userCommentList.addAll(pStatusBeans);

        return userCommentList;
    }

    /**
     * 获取流程所有活动节点 活动名称-->参与角色---->用户
     * 
     * @param process
     * @return
     * @throws WTException
     */
    public static HashMap<String, String> getWfAssignedActivityTemplateMap(WfProcess process) throws WTException {
        HashMap<String, String> waatMap = new HashMap<String, String>();
        if (process != null) {
            Team team = (Team) process.getTeamId().getObject();
            HashMap map = TeamHelper.service.findAllParticipantsByRole(team);
            log.debug("map>>>>>" + map);

            WfProcessTemplate template = (WfProcessTemplate) (process.getTemplate().getObject());
            Vector<WfAssignedActivityTemplate> aats = template.getAssignedActivities();
            for (int i = 0; i < aats.size(); i++) {
                WfAssignedActivityTemplate waat = (WfAssignedActivityTemplate) aats.get(i);
                Enumeration assignees = waat.getPrincipalAssignees();
                Enumeration actorRoles = waat.getActorRoles();
                while (actorRoles.hasMoreElements()) {
                    ActorRole roleAssinge = (ActorRole) actorRoles.nextElement();
                    if(roleAssinge.toString().equals("CREATOR")){
                        String activeityName = waat.getName();
                        String userStr = process.getCreator().getFullName();
                        log.debug("活动 ：" + activeityName + " ==>参与的角色:" + roleAssinge.getDisplay() + " ==>用户:" + userStr);
                        String keyStr = activeityName + SIGN_SEPARATOR + roleAssinge.getDisplay();
                        waatMap.put(keyStr, userStr);
                    }
                }
                
                Enumeration rolesA = ((WfAssignedActivityTemplate) waat).getRoles();
                while (rolesA.hasMoreElements()) {
                    Role roleAssinge = (Role) rolesA.nextElement();
                    if (map.containsKey(roleAssinge)) {
                        List<WTUser> users = (List<WTUser>) map.get(roleAssinge);
                        String userStr = "";
                        for (int j = 0; j < users.size(); j++) {
                            Object object = users.get(j);
                            ObjectReference objRef = (ObjectReference) object;
                            object = objRef.getObject();
                            if (object instanceof WTUser) {
                                WTUser user = (WTUser) object;
                                userStr = userStr + user.getFullName();
                            }
                            if (j < (users.size() - 1)) {
                                userStr = userStr + ";";
                            }
                        }
                        String activeityName = waat.getName();
                        Object[] objects = waat.getUserEvents();// 路由

                        log.debug("活动 ：" + activeityName + " ---参与的角色:" + roleAssinge.getDisplay() + " ---用户:" + userStr);
                        String keyStr = activeityName + SIGN_SEPARATOR + roleAssinge.getDisplay();
                        waatMap.put(keyStr, userStr);
                    }
                }
            }
        }

        return waatMap;
    }

    public static String ifRoleHasUsers(WTObject pbo, ObjectReference self,String[] noActivityRoles) throws WTException {
        String msg = "";
        WfProcess process = (WfProcess) getProcess(self);
        if (process != null) {
            Team team = (Team) process.getTeamId().getObject();
            HashMap map = TeamHelper.service.findAllParticipantsByRole(team);
            // log.debug("map>>>>>" + map);

            WfProcessTemplate template = (WfProcessTemplate) (process.getTemplate().getObject());
            Vector<WfAssignedActivityTemplate> aats = template.getAssignedActivities();
            for (int i = 0; i < aats.size(); i++) {
                WfAssignedActivityTemplate waat = (WfAssignedActivityTemplate) aats.get(i);
                Enumeration assignees = waat.getPrincipalAssignees();
                Enumeration rolesA = ((WfAssignedActivityTemplate) waat).getRoles();
                while (rolesA.hasMoreElements()) {
                    Role roleAssinge = (Role) rolesA.nextElement();
                    if (map.containsKey(roleAssinge)) {
                        List<WTUser> users = (List<WTUser>) map.get(roleAssinge);
                        String userStr = "";
                        for (int j = 0; j < users.size(); j++) {
                            Object object = users.get(j);
                            ObjectReference objRef = (ObjectReference) object;
                            object = objRef.getObject();
                            if (object instanceof WTUser) {
                                WTUser user = (WTUser) object;
                                userStr = userStr + user.getFullName();
                            }
                            if (j < (users.size() - 1)) {
                                userStr = userStr + ";";
                            }
                        }

                        log.debug("role==>" + roleAssinge.getDisplay() + "-----" + userStr);
                        if (userStr.equals("") || userStr.length() == 0) {
                            if(msg.length() > 0){
                                msg = msg + ";";
                            }
                            msg = msg + roleAssinge.getDisplay();
                        }
                    }
                }
            }
            for (int i = 0; i < noActivityRoles.length; i++) {
                Role role = Role.toRole(noActivityRoles[i]);
                ArrayList list = (ArrayList) map.get(role);
                log.debug("role==" + role.getDisplay() + "-----" + list);
                if(list == null || list.size() == 0){
                    if(msg.length() > 0){
                        msg = msg + ";";
                    }
                    msg = msg + role.getDisplay();
                }
            }
            if (msg.length() > 0) {
                msg = msg + ",请设置参与者。";
            }
        }

        return msg;
    }
    
    @SuppressWarnings("unchecked")
    public static void sortList(List list) {
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                String e1 = (String) o1;
                String e2 = (String) o2;
                if (e1.compareTo(e2) > 0) {
                    return 1;
                } else if (e1.compareTo(e2) < 0) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static void sortPStatusBeans(List<ProcessStatusBean> list) {
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                ProcessStatusBean b1 = (ProcessStatusBean) o1;
                ProcessStatusBean b2 = (ProcessStatusBean) o2;

                String t1 = b1.getCompletedDate();
                String t2 = b2.getCompletedDate();
                if (t1.compareTo(t2) > 0) {
                    return 1;
                } else if (t1.compareTo(t2) < 0) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
    }
    
    /**
     * 获取流程正在执行活动
     * @param process
     * @return
     * @throws WTException
     */
    public static String getWorkItemPotentialRole(WfProcess process) throws WTException {
        String activityName = "";

        wt.fc.ReferenceFactory rf = new wt.fc.ReferenceFactory();
        String processOid = rf.getReferenceString(process);
        NmOid oid = NmOid.newNmOid(processOid);
        QueryResult qr = WorkflowCommands.getRouteStatus(oid);
        while (qr.hasMoreElements()) {
            WorkItem workItem = (WorkItem) qr.nextElement();

            Role role = workItem.getRole();
            String roleName = role.getDisplay();
            WfAssignmentState workState = workItem.getStatus();
            WfActivity wfa = (WfActivity) workItem.getSource().getObject();// 活动节点

            if (workState.toString().equals(WORKITEM_POTENTIAL)) {
                activityName = wfa.getName();
            }
        }

        return activityName;
    }
    
    public static WfProcess getProcess(Object obj) {
        try {
            if (obj == null) {
                return null;
            }
            if (obj instanceof WfProcess) {
                return (WfProcess) obj;
            }
            Persistable persistable = null;
            if (obj instanceof ObjectIdentifier) {
                persistable = PersistenceHelper.manager.refresh((ObjectIdentifier) obj);
            } else if (obj instanceof ObjectReference) {
                persistable = ((ObjectReference) obj).getObject();
            }
            if (obj instanceof Persistable) {
                persistable = (Persistable) obj;
            }
            if (persistable instanceof WorkItem) {
                persistable = ((WorkItem) persistable).getSource().getObject();
            }

            if (persistable instanceof WfActivity) {
                persistable = ((WfActivity) persistable).getParentProcess();
            }

            if (persistable instanceof WfConnector) {
                persistable = ((WfConnector) persistable).getParentProcessRef().getObject();
            }

            if (persistable instanceof WfBlock) {
                persistable = ((WfBlock) persistable).getParentProcess();
            }
            if (persistable instanceof WfProcess) {
                return (WfProcess) persistable;
            } else {
                return null;
            }
        } catch (WTException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    
}
