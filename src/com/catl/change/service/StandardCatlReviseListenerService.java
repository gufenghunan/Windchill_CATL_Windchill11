
package com.catl.change.service;

import org.apache.log4j.Logger;

import wt.change2.ChangeException2;
import wt.change2.ChangeHelper2;
import wt.change2.Changeable2;
import wt.change2.RelevantRequestData2;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.events.KeyedEvent;
import wt.events.KeyedEventListener;
import wt.fc.Persistable;
import wt.fc.PersistenceManagerEvent;
import wt.fc.QueryResult;
import wt.inf.container.WTContainerHelper;
import wt.org.WTPrincipal;
import wt.part.WTPart;
import wt.services.ServiceEventListenerAdapter;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.vc.VersionControlServiceEvent;
import wt.vc.Versioned;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

import com.catl.bom.cad.CatlFinderCreator;
import com.catl.bom.workflow.BomWfUtil;
import com.catl.change.workflow.ECWorkflowUtil;
import com.catl.common.constant.ChangeState;
import com.catl.common.constant.PartState;
import com.catl.common.constant.TypeName;
import com.catl.common.util.PartUtil;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.core.ui.validation.UIValidationStatus;

public class StandardCatlReviseListenerService extends StandardManager implements CatlReviseListenerService {
	private static final long serialVersionUID = 5266098956681288190L;

	private KeyedEventListener listener = null;
	
	private static Logger log=Logger.getLogger(StandardCatlReviseListenerService.class.getName());

	public String getConceptualClassname() {
		return StandardCatlReviseListenerService.class.getName();
	}

	public static StandardCatlReviseListenerService newStandardCatlReviseListenerService() throws WTException {
		StandardCatlReviseListenerService instance = new StandardCatlReviseListenerService();
		instance.initialize();
		return instance;
	}

	protected void performStartupProcess() {
		log.debug(">>>++++++++++++++++++++++++resigiser service StandardCatlReviseListenerService");
		listener = new CatlEventListener(getConceptualClassname());
		getManagerService().addEventListener(listener,
				VersionControlServiceEvent.generateEventKey("NEW_VERSION"));
		getManagerService().addEventListener(listener,
				VersionControlServiceEvent.generateEventKey("PRE_NEW_VERSION"));

	}

	class CatlEventListener extends ServiceEventListenerAdapter {
		public CatlEventListener(String manager_name) {
			super(manager_name);
		}

		public void notifyVetoableEvent(Object event) throws Exception {
			if (!(event instanceof KeyedEvent))
				return;
			KeyedEvent eventObject = (KeyedEvent) event;
			Object busObject = eventObject.getEventTarget();
			boolean isWorkingCopy = false;
			if(busObject instanceof Workable){
			    Workable w = (Workable)busObject;
			    isWorkingCopy = WorkInProgressHelper.isWorkingCopy(w);
			}
			
     		if (eventObject.getEventType().equals("NEW_VERSION")&&!isWorkingCopy) {
			   
				if(busObject instanceof WTPart){
					WTPart part=(WTPart)PartUtil.getPreviousVersion((Versioned) busObject);
					if(part!=null)
					{
						String state =part.getState().toString();
						if (state.equalsIgnoreCase(PartState.RELEASED)) {
	                        checkECA(part) ;    
						}
//						if(PartUtil.isReleased(part)){
//							PartUtil.deletePDFAttachmentData(busObject);
//						}
					}
				}
				if(busObject instanceof EPMDocument){
					
					EPMDocument epm=(EPMDocument)PartUtil.getPreviousVersion((Versioned) busObject);
					if(epm!=null)
					{
					String state =epm.getState().toString();
					if (state.equalsIgnoreCase(PartState.RELEASED)) {
						checkECA(epm);
					}
					}
				}
				if(busObject instanceof WTDocument){
					WTDocument doc=(WTDocument)PartUtil.getPreviousVersion((Versioned)busObject);
					
					TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(busObject);
					 String type = ti.getTypename();
					if(doc!=null&&!type.endsWith(TypeName.doc_type_rdDoc)&&!type.endsWith(TypeName.doc_type_technicalDoc))
					{
					String state =doc.getState().toString();
					if (state.equalsIgnoreCase(PartState.RELEASED)) {
                        checkECA(doc);
					}
					}
				}
			}
		}
		
		
	}

	public static void checkECA(Workable workable) throws ChangeException2, WTException
	{

	
		 WTPrincipal userPrincipal=SessionHelper.manager.getPrincipal();
         if(CatlFinderCreator.isDmsGroup(userPrincipal)||isSiteAdmin(userPrincipal))  {
             return;
         }
           	QueryResult ecaResult=new QueryResult();
			String ecaState = "";
			int count=0;
			ecaResult=ChangeHelper2.service.getAffectingChangeActivities((Changeable2) workable);
            
             while (ecaResult.hasMoreElements()) {
				WTChangeActivity2 eca = (WTChangeActivity2) ecaResult.nextElement();
				ecaState =eca.getState().toString();
				if (ecaState.endsWith(ChangeState.IMPLEMENTATION)) {
					count++;
				}
			}
             
            if (count==0){
                System.out.println(workable.getMaster().getIdentity()+"对象没有变更的活动，不能进行修订操作。  \n");
				throw new WTException( workable.getMaster().getIdentity()+"对象没有变更的活动，不能进行修订操作。  \n");
			}
            
            
		
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
