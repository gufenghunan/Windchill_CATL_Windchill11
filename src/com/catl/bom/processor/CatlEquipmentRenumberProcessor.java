package com.catl.bom.processor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.catl.bom.cad.CatlEquipmentCADRenumber;
import com.catl.common.constant.PartState;
import com.catl.ecad.utils.ECADConst;
import com.catl.ecad.validator.StartWFFilter;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.core.ui.validation.UIValidationStatus;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.workflow.WorkflowCommands;

import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
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

public class CatlEquipmentRenumberProcessor extends DefaultObjectFormProcessor {

	private static final Logger log = Logger
			.getLogger(CatlEquipmentRenumberProcessor.class.getName());
	private static final String RESOURCE="com.catl.bom.cad.resource.EquipResource";

	@Override
	public FormResult doOperation(NmCommandBean clientData,
			List<ObjectBean> objList) throws WTException {
		// TODO Auto-generated method stub
		FormResult result = new FormResult();
		Persistable object;
		String title = "renumberEquip_tilte";
		boolean enforce = SessionServerHelper.manager
				.setAccessEnforced(false);
		String message = "";
		try {
			object = (Persistable) clientData.getActionOid().getRefObject();
			
		if (object instanceof EPMDocument) {
				EPMDocument epm = (EPMDocument) object;
				if(CatlEquipmentCADRenumber.isEquipmentCAD(epm)){
					if(epm.getLifeCycleState().toString().equalsIgnoreCase(PartState.DESIGN)||epm.getLifeCycleState().toString().equalsIgnoreCase(PartState.DESIGNMODIFICATION)){
						message = CatlEquipmentCADRenumber.renumberEquipment(epm);
					}
				}
			}else if(object instanceof Folder){
				message = CatlEquipmentCADRenumber.beatchRenumberEquip();
			}
			if(message.length()==0){
			FeedbackMessage msg = new FeedbackMessage(FeedbackType.SUCCESS,
					clientData.getLocale(), WTMessage.getLocalizedMessage(
							RESOURCE, title, null,
							clientData.getLocale()), null,
					new String[] { WTMessage.getLocalizedMessage(
							RESOURCE, "renumberEquip_tilte", null,
							clientData.getLocale()) });
			result.addFeedbackMessage(msg);
			}else{
				FeedbackMessage feedbackmessage = new FeedbackMessage(
						FeedbackType.FAILURE, clientData.getLocale(), "", null,
						new String[] { message });
				result.setStatus(FormProcessingStatus.FAILURE);
				result.addFeedbackMessage(feedbackmessage);
			}
			
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

	
}
