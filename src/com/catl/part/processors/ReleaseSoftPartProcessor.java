package com.catl.part.processors;

import java.util.Collection;
import java.util.List;

import com.catl.common.constant.PartState;
import com.catl.common.constant.TypeName;
import com.catl.common.util.DocUtil;
import com.catl.common.util.PartUtil;
import com.catl.line.util.CommonUtil;
import com.ptc.core.components.forms.DynamicRefreshInfo;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.util.PartManagementHelper;

import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTCollection;
import wt.fc.collections.WTHashSet;
import wt.fc.collections.WTValuedMap;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.part.WTPart;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionControlHelper;
import wt.vc.VersionReference;

public class ReleaseSoftPartProcessor {

	public static FormResult releaseSoftPart(NmCommandBean clientData) throws WTException {
		FormResult result = new FormResult();
		Object obj = clientData.getActionOid().getRefObject();
		if(obj instanceof WTPart){
			WTPart part = (WTPart)obj;
			FeedbackMessage feedbackmessage;
			boolean enforced = SessionServerHelper.manager.setAccessEnforced(false);
			try {
				WTDocument doc = getEmptySoftPack(part);
				if(doc != null){					
					if(part.getLifeCycleState().equals(State.toState(PartState.DESIGN))&& doc.getLifeCycleState().equals(State.toState(PartState.DESIGN))){
						LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) part, State.toState(PartState.RELEASED));
						LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) doc, State.toState(PartState.RELEASED));
					}					
				}else{
					feedbackmessage = new FeedbackMessage(FeedbackType.FAILURE, clientData.getLocale(), "", null,
		                    new String[] { "软件物料发布失败,该软件不是空软件包！"});
					result.setStatus(FormProcessingStatus.FAILURE);
					result.addFeedbackMessage(feedbackmessage);
					return result;
				}
			} catch (WTException e) {
				e.printStackTrace();
				feedbackmessage = new FeedbackMessage(FeedbackType.FAILURE, clientData.getLocale(), "", null,
	                    new String[] { "软件物料发布失败:"+e.getLocalizedMessage()});
				result.setStatus(FormProcessingStatus.FAILURE);
				result.addFeedbackMessage(feedbackmessage);
				return result;
			}finally{
				SessionServerHelper.manager.setAccessEnforced(enforced);
			}
			
			feedbackmessage = new FeedbackMessage(FeedbackType.SUCCESS, clientData.getLocale(), "", null,
                    new String[] { "软件物料和软件文档发布成功！"});
			result.setStatus(FormProcessingStatus.SUCCESS);
			result.addFeedbackMessage(feedbackmessage);
			result.addDynamicRefreshInfo(new DynamicRefreshInfo((Persistable)obj, (Persistable)obj, DynamicRefreshInfo.Action.UPDATE));
		}
		return result;
	}
	
	public static FormResult reviseSoftPartAndDoc(NmCommandBean clientData) throws WTException {
		FormResult result = new FormResult();
		Object obj = clientData.getActionOid().getRefObject();
		if(obj instanceof WTPart){
			WTPart part = (WTPart)obj;
			FeedbackMessage feedbackmessage;
			boolean enforced = SessionServerHelper.manager.setAccessEnforced(false);
			try {
				WTDocument doc = getEmptySoftPack(part);
				if(doc != null){					
					if(part.getLifeCycleState().equals(State.toState(PartState.RELEASED))&& doc.getLifeCycleState().equals(State.toState(PartState.RELEASED))){
						WTCollection items = new WTHashSet();
						items.add(part);
						items.add(doc);
						WTValuedMap localObject = (WTValuedMap) VersionControlHelper.service.newVersions(items, true);
						WTArrayList localWTArrayList = new WTArrayList();
						Object localObject2 = ((WTValuedMap) localObject).values();
						localWTArrayList.addAll((Collection) localObject2);
						WTCollection newCollection = PersistenceHelper.manager.store(localWTArrayList);
					}else{
						feedbackmessage = new FeedbackMessage(FeedbackType.FAILURE, clientData.getLocale(), "", null,
			                    new String[] { "软件物料修订失败,该软件不是已发布状态！"});
						result.setStatus(FormProcessingStatus.FAILURE);
						result.addFeedbackMessage(feedbackmessage);
						return result;
					}					
				}else{
					feedbackmessage = new FeedbackMessage(FeedbackType.FAILURE, clientData.getLocale(), "", null,
		                    new String[] { "软件物料修订失败,该软件不是空软件包！"});
					result.setStatus(FormProcessingStatus.FAILURE);
					result.addFeedbackMessage(feedbackmessage);
					return result;
				}
			} catch (WTException e) {
				e.printStackTrace();
				feedbackmessage = new FeedbackMessage(FeedbackType.FAILURE, clientData.getLocale(), "", null,
	                    new String[] { "软件物料修订失败:"+e.getLocalizedMessage()});
				result.setStatus(FormProcessingStatus.FAILURE);
				result.addFeedbackMessage(feedbackmessage);
				return result;
			}finally{
				SessionServerHelper.manager.setAccessEnforced(enforced);
			}
			part = (WTPart) PartUtil.getLatestVersion(part.getMaster());			
			feedbackmessage = new FeedbackMessage(FeedbackType.SUCCESS, clientData.getLocale(), "", null,
                    new String[] { "软件物料和软件文档修订成功！"});
			result.setStatus(FormProcessingStatus.SUCCESS);
			result.addFeedbackMessage(feedbackmessage);
			//result.addDynamicRefreshInfo(new DynamicRefreshInfo((Persistable)obj, (Persistable)obj, DynamicRefreshInfo.Action.UPDATE));
			result.setNextAction(FormResultAction.LOAD_OPENER_URL);
			String redirectURL = PartManagementHelper.getInfoPageURL(part);
			result.setURL(redirectURL);
		}
		return result;
	}
	
	/**
	 * 获取空软件包文档
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public static WTDocument getEmptySoftPack(WTPart part) throws WTException{
		if(part != null){
			if(PartUtil.isSWPart(part)){
				List docs = CommonUtil.getDescDocsByPart(part);
				for(Object obj:docs){
					if(obj instanceof WTDocument){
						WTDocument doc = (WTDocument) obj;
						String docType = DocUtil.getObjectType(doc);
						if(docType != null && docType.contains(TypeName.softwareDoc)){
							QueryResult qr = ContentHelper.service.getContentsByRole(doc, ContentRoleType.toContentRoleType("SOFTPACKAGE_ATTACHMENT"));
							if(!qr.hasMoreElements()){
								return doc;
							}
						}
					}
				}
			}
		}
		return null;
	}
}
