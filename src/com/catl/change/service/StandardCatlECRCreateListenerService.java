
package com.catl.change.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import wt.change2.AffectedActivityData;
import wt.change2.ChangeHelper2;
import wt.change2.Changeable2;
import wt.change2.RelevantRequestData2;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeRequest2;
import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.events.KeyedEvent;
import wt.events.KeyedEventListener;
import wt.fc.Persistable;
import wt.fc.PersistenceManagerEvent;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.services.ServiceEventListenerAdapter;
import wt.services.StandardManager;
import wt.util.WTException;

import com.catl.bom.workflow.BomWfUtil;
import com.catl.change.workflow.ECWorkflowUtil;

public class StandardCatlECRCreateListenerService extends StandardManager implements CatlECRCreateListenerService {
	private static final long serialVersionUID = 5266098956681288190L;

	private KeyedEventListener listener = null;

	private static Logger log=Logger.getLogger(StandardCatlECRCreateListenerService.class.getName());
   
	public String getConceptualClassname() {
		return StandardCatlECRCreateListenerService.class.getName();
	}

	public static StandardCatlECRCreateListenerService newStandardCatlECRCreateListenerService() throws WTException {
		StandardCatlECRCreateListenerService instance = new StandardCatlECRCreateListenerService();
		instance.initialize();
		return instance;
	}

	protected void performStartupProcess() {
		log.debug(">>>++++++++++++++++++++++++resigiser service start:StandardCatlECRCreateListenerService");
		listener = new CatlEventListener(getConceptualClassname());
		getManagerService().addEventListener(listener, PersistenceManagerEvent.generateEventKey("POST_STORE"));
		
	}

	class CatlEventListener extends ServiceEventListenerAdapter {
		public CatlEventListener(String manager_name) {
			super(manager_name);
		}
		 private List affectlist=new ArrayList<>();
		public void notifyVetoableEvent(Object event) throws Exception {
			if (!(event instanceof KeyedEvent))
				return;
			KeyedEvent eventObject = (KeyedEvent) event;
			Object busObject = eventObject.getEventTarget();
			if (eventObject.getEventType().equals("POST_STORE")) {
				if(busObject instanceof RelevantRequestData2){
					RelevantRequestData2 ecrdata = (RelevantRequestData2)busObject;
                    Persistable persistable=ecrdata.getRoleBObject();
                    WTChangeRequest2 changeRequest2=(WTChangeRequest2) ecrdata.getRoleAObject();
                    log.debug("ecr number =="+changeRequest2.getNumber());
                       StringBuffer errormessage=new StringBuffer();
        				Changeable2 changeable = (Changeable2) persistable;
        			     RevisionControlled revisionControlled=(RevisionControlled)persistable;
        			        String number =BomWfUtil.getObjectnumber(changeable);
        			        
        					String state= revisionControlled.getState().toString();
        					log.debug("number=="+number+"---state"+state);
        					if(!state.equalsIgnoreCase("RELEASED"))
        					{
        					errormessage.append(number+":不是已发布的状态,不能添加到变更请求中！\n");
        					}
        					if(!ECWorkflowUtil.isLastVersion(revisionControlled))
        		  			{
        						errormessage.append(number+"不是最新版本！请提交最新的版本! \n");
        					}
        					QueryResult ecaResult = ChangeHelper2.service.getAffectingChangeActivities(changeable);
        					log.debug("ecrResult size=="+ecaResult.size());

        					while (ecaResult.hasMoreElements()) {
        						Object obj = (Object) ecaResult.nextElement();
        						WTChangeActivity2 eca=(WTChangeActivity2)obj;
        						log.debug("associated with eca="+eca.getNumber());
        						if(!eca.getState().toString().equalsIgnoreCase("CANCELLED")&&!eca.getState().toString().equalsIgnoreCase("RESOLVED"))
        						{
        							errormessage.append(number+":有正在进行的变更任务:"+eca.getNumber()+"\n");
        						}
        						
        					}
        				
        				if (errormessage.length()>0) {
							throw new WTException("错误信息："+errormessage);
						}
        			
				}
			}
			
			if(busObject instanceof AffectedActivityData){
			   AffectedActivityData affectdata =(AffectedActivityData)busObject;
			  Persistable persistable= (Persistable)affectdata.getRoleBObject();
			  String number= BomWfUtil.getObjectnumber(persistable);
			  log.debug("object numer=="+number);
			  log.debug("affectlist size()=========="+affectlist.size());
			  if (affectlist.contains(persistable)) {
				  affectlist.removeAll(affectlist);
				  if (persistable instanceof WTPart) {
					  throw new WTException("错误信息：部件"+number+"已存在于变更任务中，不能重复添加！");
				}
				  if (persistable instanceof WTDocument) {
					  throw new WTException("错误信息：文档"+number+"已存在于变更任务中，不能重复添加！");
				}
				  if (persistable instanceof EPMDocument) {
					  throw new WTException("错误信息：CAD文档"+number+"已存在于变更任务中，不能重复添加！");
				}
				 
			}else {
				affectlist.add(persistable); 
			}
			}
		}
	}

	


}
