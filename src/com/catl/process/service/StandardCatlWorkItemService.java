package com.catl.process.service;

import java.io.Serializable;
import java.util.Enumeration;

import org.apache.log4j.Logger;

import com.catl.bom.service.StandardCatlPartListenerService;

import wt.access.AccessControlServerHelper;
import wt.access.AccessControlled;
import wt.access.AccessPermission;
import wt.access.AccessPermissionSet;
import wt.access.AdHocAccessKey;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.events.KeyedEvent;
import wt.fc.PersistenceManagerEvent;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTCollection;
import wt.maturity.MaturityBaseline;
import wt.maturity.PromotionNotice;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.ownership.OwnershipHelper;
import wt.project.Role;
import wt.services.ManagerException;
import wt.services.ServiceEventListenerAdapter;
import wt.services.StandardManager;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.vc.baseline.BaselineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WfAssignmentState;
import wt.workflow.work.WorkItem;
import wt.workflow.work.WorkItemLink;


public class StandardCatlWorkItemService extends StandardManager implements CatlWorkItemService, Serializable{

    private static final long serialVersionUID = -7701691481087314998L;
    private static Logger log=Logger.getLogger(StandardCatlWorkItemService.class.getName());

    private static final String CLASSNAME = StandardCatlWorkItemService.class.getName();

    public static StandardCatlWorkItemService newStandardCatlWorkItemService() throws WTException {
        StandardCatlWorkItemService instance = new StandardCatlWorkItemService();
        instance.initialize();
        return instance;
    }

    protected synchronized void performStartupProcess() throws ManagerException {
        super.performStartupProcess();
        WorkitemListener listener = new WorkitemListener(CLASSNAME);
        getManagerService().addEventListener(listener, PersistenceManagerEvent.generateEventKey(PersistenceManagerEvent.INSERT));
        getManagerService().addEventListener(listener, PersistenceManagerEvent.generateEventKey(PersistenceManagerEvent.UPDATE));
        getManagerService().addEventListener(listener, PersistenceManagerEvent.generateEventKey(PersistenceManagerEvent.PRE_DELETE));
        log.debug("Start StandardCatlWorkItemService ");
        
    }

    class WorkitemListener extends ServiceEventListenerAdapter {

        public WorkitemListener(String name) {
            super(name);
        }

        @Override
        public void notifyVetoableEvent(Object event) throws Exception {

            super.notifyVetoableEvent(event);

            if (!(event instanceof KeyedEvent)) {
                return;
            }
            KeyedEvent keyedEvent = (KeyedEvent) event;
            String eventType = keyedEvent.getEventType();
            Object eventTarget = keyedEvent.getEventTarget();
            if (eventType.equals(PersistenceManagerEvent.INSERT) && eventTarget instanceof WorkItemLink) {
                WorkItemLink workItemLink = (WorkItemLink) eventTarget;
                WorkItem workitem = workItemLink.getWorkItem();
                addPermission(workitem);

            } else if (eventType.equals(PersistenceManagerEvent.UPDATE) && eventTarget instanceof WorkItem) {

                WorkItem workitem = (WorkItem) eventTarget;
                if (workitem.getStatus().equals(WfAssignmentState.COMPLETED)) {
                    clearPermission(workitem);
                } else if (workitem.getStatus().equals(WfAssignmentState.POTENTIAL) && workitem.isReassigned()) {
                    //TODO
               }

            } else if (eventType.equals(PersistenceManagerEvent.PRE_DELETE) && eventTarget instanceof WorkItem) {
                WorkItem workitem = (WorkItem) eventTarget;
                clearPermission(workitem);
            }
        }

    }
    
    public static void addPermission(WorkItem workItem){
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            AccessPermissionSet permissionSet = new AccessPermissionSet();
            permissionSet.add(AccessPermission.READ);
            permissionSet.add(AccessPermission.DOWNLOAD);
            WTCollection collection = getRelatedObjects(workItem);
            WTPrincipal principal = OwnershipHelper.getOwner(workItem);
            if(log.isDebugEnabled()){
                log.debug("Add Read Permission for User " + principal.getName() + " for objcet " + workItem.getPrimaryBusinessObject().getObject() );
            }
            WTPrincipalReference principalRef = WTPrincipalReference.newWTPrincipalReference(principal);
            AccessControlServerHelper.manager.addPermissions(collection, principalRef, permissionSet, AdHocAccessKey.WNC_WORK_ITEM);
            PersistenceServerHelper.manager.update(collection);
               
        } catch (WTException e) {
            e.printStackTrace();
        }finally{
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        
    }
    
    public static void clearPermission(WorkItem workItem){
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            AccessPermissionSet permissionSet = new AccessPermissionSet();
            permissionSet.add(AccessPermission.READ);
            permissionSet.add(AccessPermission.DOWNLOAD);
            WTCollection collection = getRelatedObjects(workItem);
            WTPrincipal principal = OwnershipHelper.getOwner(workItem);
            if(log.isDebugEnabled()){
                log.debug("Remove Read Permission for User " + principal.getName() + " for objcet " + workItem.getPrimaryBusinessObject().getObject() );
            }
            WTPrincipalReference principalRef = WTPrincipalReference.newWTPrincipalReference(principal);
            AccessControlServerHelper.manager.removePermission(collection, principalRef, AccessPermission.READ, AdHocAccessKey.WNC_WORK_ITEM); 
            AccessControlServerHelper.manager.removePermission(collection, principalRef, AccessPermission.DOWNLOAD, AdHocAccessKey.WNC_WORK_ITEM); 
            PersistenceServerHelper.manager.update(collection);
        } catch (WTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        
    }
    
    
    public static WTCollection getRelatedObjects(WorkItem workItem){
        WTCollection collection = new WTArrayList();
        Object object = workItem.getPrimaryBusinessObject().getObject();
        QueryResult qr = null;
        try {
            if (object instanceof PromotionNotice) {
                PromotionNotice pn = (PromotionNotice)object; 
                MaturityBaseline baseline = pn.getConfiguration();
                qr = BaselineHelper.service.getBaselineItems(baseline);
            }else if(object instanceof WTChangeRequest2){
                WTChangeRequest2 ecr = (WTChangeRequest2)object;
                qr = ChangeHelper2.service.getChangeables(ecr);
            }else if (object instanceof WTChangeOrder2){
                WTChangeOrder2 eco = (WTChangeOrder2)object;
                qr = ChangeHelper2.service.getChangeablesBefore(eco);
            }else if (object instanceof WTChangeActivity2){
                WTChangeActivity2 eca = (WTChangeActivity2)object;
                qr = ChangeHelper2.service.getChangeablesBefore(eca);
            }
        } catch (WTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        while (qr!=null&& qr.hasMoreElements()) {
            AccessControlled accessControlledObject = (AccessControlled)qr.nextElement();
            collection.add(accessControlledObject);
        }    

        return collection;
    }
    
    

    

    
    
}
