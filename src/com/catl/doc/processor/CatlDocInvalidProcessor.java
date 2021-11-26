package com.catl.doc.processor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.applicationcontext.implementation.ServiceProperties;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.workflow.work.WorkItem;

import com.catl.common.constant.PartState;
import com.catl.promotion.util.WorkflowUtil;
import com.ptc.core.components.forms.DynamicRefreshInfo;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.core.ui.validation.UIValidationStatus;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class CatlDocInvalidProcessor implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String WF_TEMPLATE_NAME = "CATL_文档失效流程";
	
	public static FormResult docInvalid(NmCommandBean clientData) throws WTException {
		FormResult result = new FormResult();
		
		ArrayList selectedObject = clientData.getSelectedOidForPopup();

		if (selectedObject.size() == 0) {
			Object obj = clientData.getActionOid();
			selectedObject.add(obj);
		}

		for (int i = 0; i < selectedObject.size(); i++) {
//			Object obj = (Object) selectedObject.get(i);
			NmOid nmOid = (NmOid) selectedObject.get(i);
			LifeCycleManaged obj = (LifeCycleManaged) nmOid.getRefObject();
			
			if(obj instanceof WTDocument){
				WTDocument doc = (WTDocument)obj;
				FeedbackMessage feedbackmessage;
				
				Properties wtproperties = ServiceProperties.getServiceProperties("WTServiceProviderFromProperties");
				String couldVoidType = wtproperties.getProperty("couldVoidType");
				List<String> doctypelist = new ArrayList<String>();
		        if(StringUtils.isNotEmpty(couldVoidType)){
		        	
		        	String[] docTypeArr = couldVoidType.split(",");
		        	doctypelist = Arrays.asList(docTypeArr); 
		        }
		        
		        String docNum = doc.getNumber();
		        String docType = docNum.substring(0, docNum.indexOf("-"));
		        
		        if (doctypelist.contains(docType) && !doc.getLifeCycleState().equals(State.toState(PartState.RELEASED))){
		        	
		        	feedbackmessage = new FeedbackMessage(FeedbackType.FAILURE, clientData.getLocale(), "", null,
		                    new String[] { "启动文档失效失败: "+ docNum +"文档状态必须是“已发布”！"});
					result.setStatus(FormProcessingStatus.FAILURE);
					result.addFeedbackMessage(feedbackmessage);
					return result;
		        } else if (!doctypelist.contains(docType)) {
		        	
		        	feedbackmessage = new FeedbackMessage(FeedbackType.FAILURE, clientData.getLocale(), "", null,
		                    new String[] { "启动文档失效失败: "+ docNum +"不能发起文档失效流程！"});
					result.setStatus(FormProcessingStatus.FAILURE);
					result.addFeedbackMessage(feedbackmessage);
					return result;
		        }
		        
//		        QueryResult qr = getWorkItems(doc);
//		        if (qr != null){
//		        	
//		        }
			}
		}
		
		for (int i = 0; i < selectedObject.size(); i++) {
			NmOid nmOid = (NmOid) selectedObject.get(i);
			LifeCycleManaged obj = (LifeCycleManaged) nmOid.getRefObject();
			
			if(obj instanceof WTDocument){
				WTDocument doc = (WTDocument)obj;
				FeedbackMessage feedbackmessage;
				
				try {
					HashMap<String,String> mp = new HashMap<String,String>();
					WorkflowUtil.startWorkFlow(WF_TEMPLATE_NAME, doc, mp);
				} catch (Exception e) {
					e.printStackTrace();
					feedbackmessage = new FeedbackMessage(FeedbackType.FAILURE, clientData.getLocale(), "", null,
		                    new String[] { "启动文档失效失败:"+e.getLocalizedMessage()});
					result.setStatus(FormProcessingStatus.FAILURE);
					result.addFeedbackMessage(feedbackmessage);
					return result;
				}
				
				feedbackmessage = new FeedbackMessage(FeedbackType.SUCCESS, clientData.getLocale(), "", null,
	                    new String[] { "启动文档失效流程成功！"});
				result.setStatus(FormProcessingStatus.SUCCESS);
				result.addFeedbackMessage(feedbackmessage);
				result.addDynamicRefreshInfo(new DynamicRefreshInfo((Persistable)obj, (Persistable)obj, DynamicRefreshInfo.Action.UPDATE));
			}
		}
		
		return result;
	}
	
	public static QueryResult getWorkItems(WTDocument doc) throws WTException {
		QuerySpec qs = new QuerySpec(WorkItem.class);
//		qs.appendWhere(new SearchCondition(WorkItem.class, WorkItem.SOURCE + "." + WTAttributeNameIfc.REF_OBJECT_ID, SearchCondition.EQUAL, doc.i
//				.getObjectIdentifier().getId()), new int[] { 0 });
//		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WorkItem.class, WorkItem.PRIMARY_BUSINESS_OBJECT + "." + WTAttributeNameIfc.REF_CLASSNAME, SearchCondition.EQUAL, "VR:"+doc.toString()),
				new int[] { 0 });

		return PersistenceHelper.manager.find(qs);
	}
}
