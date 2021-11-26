package com.catl.common.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.catl.common.constant.PartState;
import com.catl.common.constant.TypeName;
import com.catl.common.resource.CatlActionRB;
import com.catl.integration.DrawingInfo;
import com.catl.promotion.resource.promotionResource;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;

import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.ObjectReference;
import wt.fc.ObjectVector;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleException;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.maturity.MaturityException;
import wt.maturity.MaturityHelper;
import wt.maturity.Promotable;
import wt.maturity.PromotionNotice;
import wt.method.RemoteMethodServer;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.part.WTPart;
import wt.project.Role;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.team.WTActorRoleHolder2;
import wt.team.WTRoleHolder2;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.util.WTInvalidParameterException;
import wt.util.WTMessage;
import wt.util.WTProperties;
import wt.vc.wip.Workable;
import wt.workflow.definer.ProcessDataInfo;
import wt.workflow.definer.WfDefinerHelper;
import wt.workflow.definer.WfInternalMethodTemplate;
import wt.workflow.definer.WfProcessDefinition;
import wt.workflow.definer.WfProcessTemplate;
import wt.workflow.definer.WfTemplateObjectType;
import wt.workflow.definer.WfVariableInfo;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfState;
import wt.workflow.engine.WfVotingEventAudit;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WfAssignment;
import wt.workflow.work.WorkItem;

public class WorkflowUtil {

	
	private static Logger log=Logger.getLogger(WorkflowUtil.class.getName());
	public static String RESOURCE = "com.catl.common.resource.CatlActionRB";
	
	/**
	 * @param args
	 * @throws WTException 
	 */
	public static void main(String[] args) throws WTException {
		// TODO Auto-generated method stub
		System.out.println("test start-------->");
	    String userName = null;
		String passWord = null;
		String partnumber = null;
		String state = null;
		if (args == null)
			return;
		for (int i = 0; i < args.length; i += 2)
		{
			if (i + 1 < args.length)
			{
				if (("-u".equals(args[i])))
				{
					userName = args[i + 1];
				} else if ("-p".equals(args[i]))
				{
					passWord = args[i + 1];
				}
				 else if ("-n".equals(args[i]))
					{
						partnumber = args[i + 1];
					}
				 else if ("-s".equals(args[i]))
					{
						state = args[i + 1];
					}
			}
		}

		if (userName == null)
			userName = "wcadmin";
		if (passWord == null)
			passWord = "plm";
		RemoteMethodServer server = RemoteMethodServer.getDefault();
		server.setUserName(userName);
		server.setPassword(passWord);
		PromotionNotice pn=getPromotionNotice(partnumber);
		changObjectState(pn, state);
	}
	public static PromotionNotice getPromotionNotice(String number)
	{
		PromotionNotice pn=null;
		if(number==null||number.length()==0)
		{
			return null;
		}
		try {
			QuerySpec spec=new QuerySpec(PromotionNotice.class);
			SearchCondition pnNOCondition=new SearchCondition(PromotionNotice.class,PromotionNotice.NUMBER,SearchCondition.EQUAL,number.toUpperCase());
			spec.appendWhere(pnNOCondition);
			try {
				QueryResult qr=PersistenceHelper.manager.find(spec);
				while(qr.hasMoreElements())
				{
			     pn=(PromotionNotice)qr.nextElement();
				}
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pn;
	}
	
	public static void changObjectState(PromotionNotice promotionnotice,String state)
	{
		try {
			QueryResult result=getPromotionTargets(promotionnotice);
			if(result.size()>0)
			{
				modifyState(result, state);
			}
		} catch (WTException e) {
			// TODO Auto-generated catch block
			log.debug("get the promotionNotice target failed");
			e.printStackTrace();
		}
	}
	
	//get the Promotion targets
	public static QueryResult getPromotionTargets(
			PromotionNotice promotionnotice) throws WTException {
		QueryResult qr = null;
		try {
			qr = MaturityHelper.service.getPromotionTargets(promotionnotice);
		} catch (MaturityException e) {
			e.printStackTrace();
			throw new MaturityException(e.getLocalizedMessage());
		} catch (WTException e) {
			e.printStackTrace();
			throw new WTException(e.getLocalizedMessage());
		}
		return qr;
	}
	public static void SetPartStateByString(String partsString,String state)
	{
		StringTokenizer parttoken = new StringTokenizer(partsString,",");
		while(parttoken.hasMoreElements()){
            String partnumber=parttoken.nextToken();
            log.debug("send ERP failed part number===="+partnumber);
            WTPart part=PartUtil.getLastestWTPartByNumber(partnumber);
            
            if(null!=part){
	            try {
					LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) part, State.toState(state));
				} catch (WTInvalidParameterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (LifeCycleException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (WTException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
		}
	}
	public static void sendPartState(String partsString,String state)
	{
		if (partsString.length()>0&&state.length()>0) {
			SetPartStateByString(partsString,state);		
		}

	}
	
	public static void SetDrawingStateByString(String drawingsString,String state)
	{
		StringTokenizer parttoken = new StringTokenizer(drawingsString,",");
		while(parttoken.hasMoreElements()){
            String number=parttoken.nextToken();
            log.debug("send ERP failed drawing number===="+number);
			try {
				WTDocument doc = DocUtil.getLatestWTDocument(number);
				if(null!=doc){
				    TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(doc);
	                String  type = ti.getTypename();
	                if(type.endsWith(TypeName.doc_type_autocadDrawing) || type.endsWith(TypeName.doc_type_gerberDoc)
	                        || type.endsWith(TypeName.doc_type_pcbaDrawing)||type.endsWith(TypeName.softwareDoc)){
	                    LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) doc, State.toState(state));
	                }
				}else{
					EPMDocument epm = DocUtil.getLastestEPMDocumentByNumber(number);
					LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) epm, State.toState(state));
				}
			} catch (WTException e1) {
				e1.printStackTrace();
			}
            
		}
	}
	public static void sendDrawingState(String drawingsString,String state)
	{
		if (drawingsString.length()>0&&state.length()>0) {
			SetDrawingStateByString(drawingsString,state);		
		}

	}
	
	//modify the promotion targets state
	public static void modifyState(QueryResult result,String state)
	{
		if(result.size()==0)
		{
			log.debug("target objec is null--------");
		}else{
			while(result.hasMoreElements())
			{
				Object object =result.nextElement();
					try {
						LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) object, State.toState(state));
					} catch (WTInvalidParameterException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (LifeCycleException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (WTException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	
	public static String getUserOnRole(wt.lifecycle.LifeCycleManaged lifecyclemanaged, String inputrolename) {
        log.info("****** WorkflowUtils.getUserOnRole begin  - role: " + inputrolename);

        Object obj = null;
        try {
            obj = wt.team.TeamHelper.service.getTeam(lifecyclemanaged);
        } catch (wt.util.WTException wte) {
            wte.printStackTrace();
        }

        String s = "";
        int count = 0;

        Enumeration<?> enumeration = null;

        try {
            enumeration = ((WTRoleHolder2) obj).getRoles().elements();
        } catch (WTException wtexception) {
            log.error("encountered error trying to get role list", wtexception);
        }

        while (enumeration.hasMoreElements()) {
            wt.project.Role role1 = (wt.project.Role) enumeration.nextElement();

            // only if the role is the one given as input we proceed seeking
            // participants
            if (role1.toString().compareTo(inputrolename) != 0)
                continue;
            log.info("****** checking Role = " + inputrolename);

            if (obj instanceof wt.team.WTActorRoleHolder2) {
                Enumeration<?> enumeration3 = null;
                try {
                    enumeration3 = ((WTActorRoleHolder2) obj).getActorRoleTarget(role1);
                } catch (WTException wtexception2) {
                    log.error("encountered error trying to get actor role participants", wtexception2);
                }
                while (enumeration3.hasMoreElements()) {
                    wt.project.ActorRole actorrole = (wt.project.ActorRole) enumeration3.nextElement();

                    if (count == 0)
                        s = s + actorrole.toString();
                    else
                        s = s + "," + actorrole.toString();
                    count++;

                }
            }
            Enumeration<?> enumeration4 = null;
            try {
                enumeration4 = ((WTRoleHolder2) obj).getPrincipalTarget(role1);
            } catch (WTException wtexception3) {
                log.error("encountered error trying to get role list", wtexception3);
            }
            while (enumeration4 != null && enumeration4.hasMoreElements()) {
                Object obj1 = enumeration4.nextElement();
                if (obj1 != null) {
                    wt.org.WTPrincipalReference wtprincipalreference = (wt.org.WTPrincipalReference) obj1;

                    if (count == 0)
                        s = s + ((wt.org.WTPrincipal) wtprincipalreference.getObject()).getName();
                    else
                        s = s + "," + ((wt.org.WTPrincipal) wtprincipalreference.getObject()).getName();
                    count++;
                }
            }
            log.debug("****** participant on Role = >" + s + "<");
            return s;
        }

        log.debug("****** WorkflowUtils.getUserOnRole return - no user ");
        return "";
    }
	
	/**
	 * 获得流程模板中定义的给定节点的参与者角色
	 * @param wft 流程模板
	 * @param nodeName 节点名称
	 * @return
	 */
	public static Set<Role> getRolesInWfInternalMethodTemplate(WfProcessTemplate wft, String nodeName){
		Set<Role> set = new HashSet<Role>();
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		try{
			Enumeration<?> localEnumeration = WfDefinerHelper.service.getStepTemplates(wft, WfTemplateObjectType.ROBOT_ACTIVITY_TEMPLATE);
			while(localEnumeration.hasMoreElements()){
				Object obj = localEnumeration.nextElement();
				if(obj instanceof WfInternalMethodTemplate){
					WfInternalMethodTemplate temp = (WfInternalMethodTemplate)obj;
					log.info("==temp.getName():"+temp.getName());
					if(StringUtils.equals(nodeName, temp.getName())){
						ProcessDataInfo dataInfo = temp.getContextSignature();
						WfVariableInfo localWfVariableInfo = dataInfo.getVariableInfo("roles");
						Vector<?> localVector = (Vector<?>)localWfVariableInfo.getDefaultValue();
						Iterator<?> iterator = localVector.iterator();
						while(iterator.hasNext()){
							Object p = iterator.next();
							if(p instanceof Role){
								Role role = (Role)p;
								log.info("==role.getDisplay():"+role.getDisplay());
								set.add(role);
							}
							
						}
					}
				}
			}
		} 
		catch (WTException e) {
			e.printStackTrace();
		}
		finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		
		return set;
	}
	/**
	 * 检查工作流角色不能选择自己
	 * @param usersCollection
	 * @param currentUser
	 * @return
	 * @throws WTException
	 */
	public static boolean isSelectSelf(Object usersCollection,WTPrincipal currentUser) throws WTException {
		String checkSelf = GenericUtil.getPreferenceValue("/catl/checkSelf");
		if(checkSelf.equalsIgnoreCase("false"))
			return false;
		
		if(usersCollection instanceof Enumeration){
			Enumeration users = (Enumeration)usersCollection;
			while(users.hasMoreElements()){
				WTPrincipalReference userRef = (WTPrincipalReference) users.nextElement();
				WTPrincipal user = userRef.getPrincipal();
				if(user.equals(currentUser)){
					return true;
				}
			}
		}else if(usersCollection instanceof List){
			List users = (ArrayList)usersCollection;
			for(Object obj : users){
				WTPrincipal user = (WTPrincipal) obj;
				if(user.equals(currentUser)){
					return true;
				}
			}
		}
		
		return false;
	}
	
	//启动工作流
    @SuppressWarnings("rawtypes")
	public static WfProcess startWorkFlow(String workFlowName, WTObject pbo, HashMap variables) throws WTException {
        long WORKFLOW_PRIORITY = 1;
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            WTContainerRef containerRef = CommonUtil.getORGCATLRef();
            if (pbo instanceof WTContained) {
                WTContained contained = (WTContained) pbo;
                containerRef = contained.getContainerReference();
            }
            WTProperties wtproperties = WTProperties.getLocalProperties();
            WORKFLOW_PRIORITY = Long.parseLong(wtproperties.getProperty("wt.lifecycle.defaultWfProcessPriority", "1"));
            WfProcessDefinition wfprocessdefinition = WfDefinerHelper.service.getProcessDefinition(workFlowName, containerRef);
            if (wfprocessdefinition == null) {
            	log.debug("Error to getWrokFlowTemplate," + workFlowName + " is null");
            	Locale locale = SessionHelper.getLocale();
            	String msg = WTMessage.getLocalizedMessage(RESOURCE, CatlActionRB.error_Start, null, locale);
            	throw new WTException(msg);
            }

            WfProcess wfprocess = WfEngineHelper.service.createProcess(wfprocessdefinition, pbo, containerRef);
            ProcessData processData = wfprocess.getContext();
            processData.setValue(WfDefinerHelper.PRIMARY_BUSINESS_OBJECT, pbo);

            if (variables != null && !variables.isEmpty()) {
                Iterator keys = variables.keySet().iterator();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    processData.setValue(key, variables.get(key));
                }
            }

            wfprocess = WfEngineHelper.service.startProcessImmediate(wfprocess, processData, WORKFLOW_PRIORITY);
            return wfprocess;
        } catch (Exception e) {
            e.printStackTrace();
            throw new WTException(e);
        } finally{
        	SessionServerHelper.manager.setAccessEnforced(flag);
        }
    }
    
	/**
	 * 获得传入的WfProcesss中指定名称的人工节点已完成的WorkItem相关的信息
	 * 如果人节点经过了多次，则只返回最后一次经过时已完成的WorkItem的信息
	 * 
	 * @param wfProcessOid WfProcesss的ID
	 * @param activityName 人工节点的名称
	 * @param role 该人工节点的指定角色，如果传入null，则表示返回该节点所有角色的已完成WorkItem
	 * @return
	 * @throws WTException
	 */
	public static Map<WorkItem, WfVotingEventAudit> getCompletedWorkItemInfo(long wfProcessOid, String activityName, Role role) throws WTException {
		QueryResult activities = getAssignedActivities(wfProcessOid, activityName, null);

		Map<WorkItem, WfVotingEventAudit> workitems = new HashMap<WorkItem, WfVotingEventAudit>();
		while (activities.hasMoreElements()) {
			WfAssignedActivity act = (WfAssignedActivity) activities.nextElement();

			QueryResult workItems = getWorkItems(act, role);
			while (workItems.hasMoreElements()) {
				WorkItem workitem = (WorkItem) workItems.nextElement();
				WfVotingEventAudit wfVotingEventAudit = getWfVotingEventAudit(workitem);
				if (workitem.isComplete() && workitem.getModifyTimestamp().after(act.getStartTime()) && wfVotingEventAudit != null) {
					workitems.put(workitem, wfVotingEventAudit);
				}
			}
		}
		return workitems;
	}

	public static QueryResult getAssignedActivities(long wfProcessOid, String name, WfState state) throws WTException {
		QuerySpec qs = new QuerySpec(WfAssignedActivity.class);
		qs.appendWhere(new SearchCondition(WfAssignedActivity.class, WfAssignedActivity.PARENT_PROCESS_REF + "." + WTAttributeNameIfc.REF_OBJECT_ID, SearchCondition.EQUAL,
				wfProcessOid), new int[] { 0 });

		if (StringUtils.isNotBlank(name)) {
			qs.appendAnd();
			qs.appendOpenParen();
			qs.appendWhere(new SearchCondition(WfAssignedActivity.class, WfAssignedActivity.NAME, SearchCondition.EQUAL, name), new int[] { 0 });
			qs.appendCloseParen();
		}

		if (state != null) {
			qs.appendAnd();
			qs.appendOpenParen();
			Enumeration subStates = state.getSubstates();
			WfState subState = (WfState) subStates.nextElement();
			qs.appendWhere(new SearchCondition(WfAssignedActivity.class, WfAssignedActivity.STATE, SearchCondition.EQUAL, subState), new int[] { 0 });
			while (subStates.hasMoreElements()) {
				qs.appendOr();
				subState = (WfState) subStates.nextElement();
				qs.appendWhere(new SearchCondition(WfAssignedActivity.class, WfAssignedActivity.STATE, SearchCondition.EQUAL, subState), new int[] { 0 });
			}
			qs.appendCloseParen();
		}
		return PersistenceHelper.manager.find(qs);
	}

	public static QueryResult getWorkItems(WfAssignedActivity activity, Role role) throws WTException {
		QuerySpec qs = new QuerySpec(WorkItem.class);
		qs.appendWhere(new SearchCondition(WorkItem.class, WorkItem.SOURCE + "." + WTAttributeNameIfc.REF_OBJECT_ID, SearchCondition.EQUAL, activity.getPersistInfo()
				.getObjectIdentifier().getId()), new int[] { 0 });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WorkItem.class, WorkItem.SOURCE + "." + WTAttributeNameIfc.REF_CLASSNAME, SearchCondition.EQUAL, activity.getClass().getName()),
				new int[] { 0 });

		if (role != null) {
			qs.appendAnd();
			qs.appendOpenParen();
			qs.appendWhere(new SearchCondition(WorkItem.class, WorkItem.ROLE, SearchCondition.EQUAL, role.toString()), new int[] { 0 });
			qs.appendCloseParen();
		}
		return PersistenceHelper.manager.find(qs);
	}

	public static WfVotingEventAudit getWfVotingEventAudit(WorkItem workitem) throws WTException {
		QuerySpec qs = new QuerySpec(WfVotingEventAudit.class);
		qs.appendWhere(new SearchCondition(WfVotingEventAudit.class, WfVotingEventAudit.WORK_ITEM_REFERENCE + "." + WTAttributeNameIfc.REF_OBJECT_ID, SearchCondition.EQUAL,
				PersistenceHelper.getObjectIdentifier(workitem).getId()), new int[] { 0 });

		QueryResult result = PersistenceHelper.manager.find(qs);
		if (result.hasMoreElements()) {
			return (WfVotingEventAudit) result.nextElement();
		}
		return null;
	}

}
