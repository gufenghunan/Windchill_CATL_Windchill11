package com.catl.ele.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.catl.ecad.utils.ECADConst;
import com.catl.ecad.validator.StartWFFilter;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.workflow.WorkflowCommands;

import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleManaged;
import wt.part.WTPart;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.team.TeamHelper;
import wt.team.TeamManaged;
import wt.team.TeamReference;
import wt.team.TeamTemplate;
import wt.team.TeamTemplateReference;
import wt.util.WTException;
import wt.util.WTMessage;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.workflow.definer.WfDefinerHelper;
import wt.workflow.definer.WfProcessDefinition;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfContainer;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfState;
import wt.workflow.engine.WfTransition;
import wt.workflow.status.WfWorkflowStatusHelper;

public class EleWorkflowProcessor extends DefaultObjectFormProcessor {

	private static final Logger log = Logger
			.getLogger(EleWorkflowProcessor.class.getName());

	@Override
	public FormResult doOperation(NmCommandBean clientData,
			List<ObjectBean> objList) throws WTException {
		// TODO Auto-generated method stub
		FormResult result = new FormResult();
		Persistable object;
		String title = null;
		boolean enforce = SessionServerHelper.manager
				.setAccessEnforced(false);
		try {
			object = (Persistable) clientData.getActionOid().getRefObject();
			
			if (object instanceof WTDocument) {
				WTDocument doc = (WTDocument) object;
				boolean a = StartWFFilter.terminateWorkProcess(doc);
				if (!a) {
					throw new WTException("该电气项目设计流程已存在！");
				}
				startWorkFlow("电气文档发布流程", doc, null);
				title = "start_workflow_title";
			}else if(object instanceof WTPart){
				WTPart part = (WTPart) object;
				if(EleCommonUtil.isElePart(part)){
					boolean a  = StartWFFilter.terminateWorkProcess(part);
					if(!a){
						throw new WTException("该物料电气建库流程已存在！");
					}
					startWorkFlow("电气建库流程", part, null);
					title = "start_workflow_title";
				}
			}
			
			FeedbackMessage msg = new FeedbackMessage(FeedbackType.SUCCESS,
					clientData.getLocale(), WTMessage.getLocalizedMessage(
							ECADConst.RESOURCE, title, null,
							clientData.getLocale()), null,
					new String[] { WTMessage.getLocalizedMessage(
							ECADConst.RESOURCE, "start_workflow_success", null,
							clientData.getLocale()) });
			result.addFeedbackMessage(msg);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e);
			FeedbackMessage feedbackmessage = new FeedbackMessage(
					FeedbackType.FAILURE, clientData.getLocale(), "", null,
					new String[] { e.getLocalizedMessage() });
			result.setStatus(FormProcessingStatus.FAILURE);
			result.addFeedbackMessage(feedbackmessage);
		}finally{
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		result.setStatus(FormProcessingStatus.SUCCESS);
		return result;
	}

	/**
	 * 启动工作流
	 * 
	 * @param workFlowName
	 * @param pbo
	 * @param variables
	 * @return
	 * @throws WTException
	 */
	public static WfProcess startWorkFlow(String workFlowName, Object pbo,
			HashMap variables) throws WTException {
		long WORKFLOW_PRIORITY = 1;
		try {
			WTContainerRef containerRef = null;
			if (pbo instanceof WTContained) {
				WTContained contained = (WTContained) pbo;
				containerRef = contained.getContainerReference();
			} else {
				containerRef = WTContainerHelper.service.getExchangeRef();
			}
			WTProperties wtproperties = WTProperties.getLocalProperties();
			WORKFLOW_PRIORITY = Long.parseLong(wtproperties.getProperty(
					"wt.lifecycle.defaultWfProcessPriority", "1"));
			WfProcessDefinition wfprocessDefinition = WfDefinerHelper.service
					.getProcessDefinition(workFlowName, containerRef);
			if (wfprocessDefinition == null) {
				System.out.println("Error to getWrokFlowTemplate,"
						+ workFlowName + " is null");
			}

			Object team_spec = null;

			if (pbo != null && pbo instanceof TeamManaged) {
				TeamReference teamRef = ((TeamManaged) pbo).getTeamId();
				if (teamRef != null) {
					team_spec = teamRef;
				}
			}
			if (team_spec == null) {
				String teamTemplateName = "Default";
				TeamTemplate tt = TeamHelper.service.getTeamTemplate(
						containerRef, teamTemplateName);

				if (tt != null) {
					TeamTemplateReference teamTemplateRef = TeamTemplateReference
							.newTeamTemplateReference(tt);
					team_spec = teamTemplateRef;
				}
			}

			WfProcess wfprocess = WfEngineHelper.service.createProcess(
					wfprocessDefinition, team_spec, containerRef);

			ProcessData processData = wfprocess.getContext();
			processData.setValue(WfDefinerHelper.PRIMARY_BUSINESS_OBJECT, pbo);

			if (variables != null && !variables.isEmpty()) {
				Iterator keys = variables.keySet().iterator();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					processData.setValue(key, variables.get(key));
				}
			}
			try {
				LifeCycleManaged lcm = (LifeCycleManaged) pbo;
				System.out.println("LCM\t" + lcm);
				TeamTemplateReference ttr = lcm.getTeamTemplateId();
				System.out.println("TTR\t" + ttr);
				wfprocess.setTeamTemplateId(ttr);
				wfprocess = (WfProcess) saveProcess(wfprocess);
			} catch (WTPropertyVetoException e) {
				e.printStackTrace();
			}
			wfprocess = WfEngineHelper.service.startProcessImmediate(wfprocess,
					processData, WORKFLOW_PRIORITY);
			return wfprocess;
		} catch (IOException e) {
			e.printStackTrace();
			throw new WTException(e);
		} catch (WTException e) {
			e.printStackTrace();
			throw new WTException(e);
		} finally {
		}
	}

	private static WfContainer saveProcess(WfContainer container)
			throws WTException {
		SessionContext old_session = SessionContext.newContext();
		try {
			// Establish new identity for access control, ownership, etc.
			SessionHelper.manager.setAdministrator();
			container = (WfContainer) PersistenceHelper.manager.save(container);
		} finally {
			SessionContext.setContext(old_session);
		}
		return container;
	}
}
