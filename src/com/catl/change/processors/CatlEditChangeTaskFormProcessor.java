
package com.catl.change.processors;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import wt.change2.ChangeActivity2;
import wt.change2.ChangeActivityIfc;
import wt.change2.ChangeException2;
import wt.change2.ChangeHelper2;
import wt.change2.ChangeOrderIfc;
import wt.change2.Changeable2;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeActivity2Master;
import wt.change2.WTChangeActivity2MasterIdentity;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeOrder2Master;
import wt.change2.WTChangeOrder2MasterIdentity;
import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.fc.IdentityHelper;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.collections.WTKeyedHashMap;
import wt.fc.collections.WTKeyedMap;
import wt.fc.collections.WTList;
import wt.inf.container.WTContainerRef;
import wt.log4j.LogR;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.project.Role;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.team.Team;
import wt.team.TeamException;
import wt.team.TeamHelper;
import wt.team.TeamManaged;
import wt.team.WTRoleHolder2;
import wt.util.LocalizableMessage;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.workflow.engine.WfProcess;

import com.catl.bom.workflow.BomWfUtil;
import com.catl.cad.BatchDownloadPDFUtil;
import com.catl.change.ChangeUtil;
import com.catl.change.filter.CatlEditChangeTasckValidation;
import com.catl.change.inventory.ECAPartLinkCreateEditUtil;
import com.catl.change.report.dcn.DCNAttachmentHtml;
import com.catl.change.util.ChangeConst;
import com.catl.change.workflow.DcnWorkflowfuncion;
import com.catl.common.constant.ChangeState;
import com.catl.common.constant.Constant;
import com.catl.common.constant.PartState;
import com.catl.common.constant.RoleName;
import com.catl.common.constant.TypeName;
import com.catl.common.util.IBAUtil;
import com.catl.common.util.PartUtil;
import com.catl.doc.maturityUpReport.MaturityUpReportHelper;
import com.catl.part.PartConstant;
import com.catl.promotion.util.PromotionUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.meta.common.UpdateOperationIdentifier;
import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.workflow.NmWorkflowHelper;
import com.ptc.windchill.enterprise.change2.ChangeTaskRoleParticipantHelper;
import com.ptc.windchill.enterprise.change2.forms.ChangeManagementFormProcessorHelper;
import com.ptc.windchill.enterprise.change2.forms.processors.EditChangeItemFormProcessor;
import com.ptc.windchill.enterprise.wizardParticipant.delegate.ParticipantSelectionDelegate;

public class CatlEditChangeTaskFormProcessor extends EditChangeItemFormProcessor
{

	public CatlEditChangeTaskFormProcessor()
	{
		changeOrder = null;
		assigneeList = new ArrayList();
		reviewerList = new ArrayList();
	}

	public FormResult preProcess(NmCommandBean nmcommandbean, List list) throws WTException
	{
		FormResult formresult = new FormResult();
		formresult.setStatus(FormProcessingStatus.SUCCESS);
		try
		{
			formresult = superPreProcess(nmcommandbean, list);
			if (formresult.getStatus() == FormProcessingStatus.SUCCESS)
			{
				ObjectBean objectbean;
				for (Iterator iterator = list.iterator(); iterator.hasNext(); preProcessWorkflowRoles(objectbean))
					objectbean = (ObjectBean) iterator.next();

			}
		} catch (Exception exception)
		{
			formresult = ChangeManagementFormProcessorHelper.handleFormResultException(formresult, getLocale(), exception, getProcessorErrorMessage());
		}
		return formresult;
	}

	@Override
	public FormResult postProcess(NmCommandBean clientData, List<ObjectBean> objectBeanList) throws WTException
	{
		WTPrincipal userPrincipal = SessionHelper.manager.getPrincipal();
		FormResult result = new FormResult();
		Map<WTChangeActivity2, String> changedNameInfo = getChangedNameInfo(objectBeanList);
		for (WTChangeActivity2 eca : changedNameInfo.keySet()) {
			String newName = changedNameInfo.get(eca);
			logger.info("==newName:"+newName);
			updateECAName(eca, newName);
		}
		
		changePersonincharge(objectBeanList);

//		logger.debug("change name=======" + changenanme);
		logger.debug("start post process-------eca-task");
		logger.debug("object==================" + objectBeanList.get(0).getObject());
		StringBuffer message = new StringBuffer();
		try
		{
			ChangeActivity2 dca=(ChangeActivity2)objectBeanList.get(0).getObject();
			logger.debug("dca:"+dca);
		    String dcatype = ChangeUtil.getStrSplit(dca);
		    //区分DCA和以前逻辑的处理
		    if(dcatype.equals(ChangeConst.CHANGETASK_TYPE_DCA)){
		    	message = CheckEcavalidateSubmitItemByDca(clientData, objectBeanList);
		    }else{
		    	logger.info("start process---------->");
				//message = checkAffectData(clientData, objectBeanList);
				message.append(ChangeUtil.checkMaturity(objectBeanList.get(0).getObject(),objectBeanList).toString());
				message.append(CatlCreateChangeTaskFormProcessor.CheckEcavalidateSubmitItem(clientData, objectBeanList, false));
		    }
			
			if (message != null && message.length() > 0) {
				throw new WTException("错误：" + message);
			}
			result = super.postProcess(clientData, objectBeanList);
			
			if(CatlEditChangeTasckValidation.isSiteAdmin(userPrincipal) || CatlEditChangeTasckValidation.isOrgAdministator(userPrincipal, "CATL")){//如果是管理员修改ECN则 重新生成受影响报表 ECN_HTML报表
				WTChangeOrder2 order = (WTChangeOrder2)ChangeHelper2.service.getChangeOrder(dca).nextElement();
				String ecotype = ChangeUtil.getStrSplit(order);
				if(ecotype.equals(ChangeConst.CHANGEORDER_TYPE_DCN)){
					try {
						DcnWorkflowfuncion.addAttachment(order);//Excel
						QueryResult processResult = NmWorkflowHelper.service.getAssociatedProcesses(order, null, null);
				        WfProcess process=null;
				        ObjectReference self = null;
				        if(processResult.hasMoreElements()){
		                    process=(WfProcess) processResult.nextElement();
		                    if(process.getState().getDisplay().equals(Constant.PROCESS_RUNNING) && order.getState().toString().equals(ChangeState.IMPLEMENTATION)){
		                    	self = ObjectReference.newObjectReference(process);
		                    	DCNAttachmentHtml.doCreateECNHtmlReport(order,self,"1");//更新报表HTML
		                    }
				        }
					} catch (PropertyVetoException e) {
						e.printStackTrace();
						throw new WTException("管理员DCN/DCA修改失败，请与PLM开发工程师联系,message:"+e.getMessage());
					}
		        }
			}
			
		} catch (Exception e)
		{
			logger.info("save ECA occur exception");
			e.printStackTrace();
			result = ChangeManagementFormProcessorHelper.handleFormResultException(result, getLocale(), null, new Exception(""), e);
		}
		logger.info("end process---------------------------->");
		return result;
	}
	
	private Map<WTChangeActivity2, String> getChangedNameInfo(List<ObjectBean> objectBeanList) throws WTException{
		Map<WTChangeActivity2, String> map = new HashMap<WTChangeActivity2, String>();
		String changenanme = "";
		for (ObjectBean objectBean : objectBeanList) {
			Map<String, List<String>> changeMap = objectBean.getChangedComboBox();
			for(String key : changeMap.keySet()){
				logger.info("==getChangedNameInfo==key:"+key);
				logger.info("==changeVaules:"+changeMap.get(key));
				if(key.contains("change_name~~")){
					changenanme = changeMap.get(key).get(0);
					map.put((WTChangeActivity2)objectBean.getObject(), changenanme);
				}
			}
		}
		return map;
	}

	public static void changePersonincharge(List<ObjectBean> objectBeanList) throws TeamException, WTException
	{
		WTUser previous = (WTUser) SessionHelper.manager.getPrincipal();
		try
		{
			WTPrincipal wtadministrator = SessionHelper.manager.getAdministrator();
			SessionContext.setEffectivePrincipal(wtadministrator);

			if (objectBeanList.size() > 0)
			{
				if (objectBeanList.get(0).getObject() instanceof WTChangeActivity2)
				{
					WTChangeActivity2 eca = (WTChangeActivity2) objectBeanList.get(0).getObject();
					eca = (WTChangeActivity2) PersistenceHelper.manager.refresh(eca);
					boolean isCanEdit = true;
					String ecaState = eca.getState().toString();
					if (ecaState.equalsIgnoreCase(ChangeState.IMPLEMENTATION))
						isCanEdit = false;
					if (isCanEdit)
					{
						WTPrincipal assigneePicker = ChangeManagementFormProcessorHelper.getUser("changeTask_AssigneePicker_dn", objectBeanList.get(0));
						if (assigneePicker != null){
							
							ReferenceFactory rf = new ReferenceFactory();
							String oid = rf.getQueryString(assigneePicker);
							if (oid == null)
								return;
							Team team = TeamHelper.service.getTeam((TeamManaged) eca);
							Role role = Role.toRole(RoleName.ASSIGNEE);
							TeamHelper.service.deleteRole(role, team);
							TeamHelper.service.addRolePrincipalMap(role, assigneePicker, team);
						} else {
							Team team = TeamHelper.service.getTeam((TeamManaged) eca);
							Vector allRoles = TeamHelper.service.findRoles(team);
							HashMap rolePrincipalListMap = TeamHelper.service.findAllParticipantsByRole(team);
							for (int i = 0; allRoles != null && i < allRoles.size(); i++)
							{
								Role role = Role.toRole(RoleName.ASSIGNEE);
								if (allRoles.get(i).equals(role))
								{
									ArrayList principalList = (ArrayList) rolePrincipalListMap.get(allRoles.get(i));
									for (int j = 0; principalList != null && j < principalList.size(); j++)
									{
										WTPrincipal principal = ((WTPrincipalReference) principalList.get(j)).getPrincipal();
										ReferenceFactory rf = new ReferenceFactory();
										String oid = rf.getQueryString(principal);
										if (oid == null)
											break;
										PersistableAdapter genericObj = new PersistableAdapter(eca, null, null, new UpdateOperationIdentifier());
										genericObj.load("Personincharge");
										genericObj.set("Personincharge", oid);
										Persistable updatedObject = genericObj.apply();
										PersistenceHelper.manager.save(updatedObject);
										break;
									}
								}
							}
						}
					} else
					{
						PersistableAdapter genericObj0 = new PersistableAdapter(eca, null, null, null);
						genericObj0.load("Personincharge");
						String oid = (String) genericObj0.get("Personincharge");
						if (oid == null)
							return;
						ReferenceFactory rf = new ReferenceFactory();
						WTPrincipal principal = (WTPrincipal) rf.getReference(oid).getObject();
						Team team = TeamHelper.service.getTeam((TeamManaged) eca);
						Role role = Role.toRole(RoleName.ASSIGNEE);
						TeamHelper.service.deleteRole(role, team);
						TeamHelper.service.addRolePrincipalMap(role, principal, team);
					}
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			SessionContext.setEffectivePrincipal(previous);
		}
	}

	
	public static WTPrincipal  getselectUser(ObjectBean objectbean,String rolename) throws TeamException, WTException
	{
        WTPrincipal principal =null;
		WTChangeActivity2 eca = (WTChangeActivity2) objectbean.getObject();
		 eca = (WTChangeActivity2) PersistenceHelper.manager.refresh(eca);
			Team team = TeamHelper.service.getTeam((TeamManaged) eca);
			Vector allRoles = TeamHelper.service.findRoles(team);
			HashMap rolePrincipalListMap = TeamHelper.service.findAllParticipantsByRole(team);
			for (int i = 0; allRoles != null && i < allRoles.size(); i++)
			{
				Role role = Role.toRole(rolename);
				if (allRoles.get(i).equals(role))
				{
					ArrayList principalList = (ArrayList) rolePrincipalListMap.get(allRoles.get(i));
					for (int j = 0; principalList != null && j < principalList.size(); j++)
					{
					  principal = ((WTPrincipalReference) principalList.get(j)).getPrincipal();						
					}
				}
			}
			return principal;
		}
	
	public static void updateECAName(WTChangeActivity2 eca, String name) throws WTException
	{

		try
		{
			WTChangeActivity2Master ecamaster = (WTChangeActivity2Master) eca.getMaster();
			ecamaster = (WTChangeActivity2Master) PersistenceHelper.manager.refresh(ecamaster);
			WTChangeActivity2MasterIdentity identity = (WTChangeActivity2MasterIdentity) ecamaster.getIdentificationObject();
			identity.setName(name);
			ecamaster = (WTChangeActivity2Master) IdentityHelper.service.changeIdentity(ecamaster, identity);
			PersistenceHelper.manager.save(ecamaster);
		} catch (Exception e)
		{
			e.printStackTrace();
			logger.debug(eca.getNumber() + ":修改ECA 名称失败！--change to:" + name);
		}
	}

	public static StringBuffer checkifOthereca(Changeable2 changeable2)
	{
		StringBuffer message = new StringBuffer();
		try
		{
			QueryResult ecaResult = new QueryResult();

			String ecaState = "";
			int count = 0;
			ecaResult = ChangeHelper2.service.getAffectingChangeActivities(changeable2);
			logger.debug("ecaResult size===" + ecaResult.size());
			while (ecaResult.hasMoreElements())
			{
				WTChangeActivity2 eca = (WTChangeActivity2) ecaResult.nextElement();
				ecaState = eca.getState().toString();
				if (ecaState.endsWith("IMPLEMENTATION"))
				{
					count++;
				}
			}
			if (count > 0)
			{
				message.append(BomWfUtil.getObjectnumber(changeable2) + "存在其他有效的变更任务！");
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return message;
	}

	public static StringBuffer checkAffectData(NmCommandBean clientData, List<ObjectBean> objectBeanList) throws WTException
	{
		StringBuffer message = new StringBuffer();
		for (ObjectBean ecaBean : objectBeanList)
		{
			logger.info("ecaBean process start ecaBean =" + ecaBean.toString());
			List affectedItems = ecaBean.getAddedItemsByName("changeTask_affectedItems_table");
			logger.debug("list item size===" + affectedItems.size());
			for (Object item : affectedItems)
			{
				NmOid nmOid = (NmOid) item;
				Object obj = nmOid.getLatestIterationObject();
				if (obj instanceof RevisionControlled)
				{
					RevisionControlled vControlled = (RevisionControlled) obj;
					if (!vControlled.getState().toString().endsWith(PartState.RELEASED))
					{
						message.append(BomWfUtil.getObjectnumber(vControlled) + "该对象未发布，不能添加 \n");
					}
				}
			}
		}
		return message;
	}

	private void preProcessWorkflowRoles(ObjectBean objectbean) throws WTPropertyVetoException, WTException, ChangeException2
	{
		setChangeNotice(objectbean);
		ParticipantSelectionDelegate participantselectiondelegate = ParticipantSelectionDelegate.newInstance(objectbean);
		if (participantselectiondelegate.showMultipleParticipantSelection())
		{
			logger.debug("Workflow roles are processed using the multiple participant selection component.");
			return;
		}
		boolean flag = ChangeManagementFormProcessorHelper.setRequiredReview(objectbean);
		//WTPrincipal wtprincipal = ChangeManagementFormProcessorHelper.getUser("changeTask_AssigneePicker_dn", objectbean);
		WTPrincipal wtprincipal =getselectUser(objectbean,RoleName.ASSIGNEE);
		//WTPrincipal wtprincipal1 = ChangeManagementFormProcessorHelper.getUser("changeTask_ReviewerPicker_dn", objectbean);
		WTPrincipal wtprincipal1 =getselectUser(objectbean,RoleName.REVIEWER);
		if (wtprincipal == null || wtprincipal1 == null)
		{
			ChangeActivity2 changeactivity2 = (ChangeActivity2) objectbean.getObject();
			WTContainerRef wtcontainerref = changeactivity2.getContainerReference();
			boolean flag1 = ChangeTaskRoleParticipantHelper.isRequired(objectbean.getObject(), wtcontainerref, objectbean);
			if (flag1)
			{
				if (wtprincipal == null)
					wtprincipal = SessionHelper.manager.getPrincipal();
				if (wtprincipal1 == null && flag)
					wtprincipal1 = SessionHelper.manager.getPrincipal();
			}
		}
		assigneeList.add(wtprincipal);
		reviewerList.add(wtprincipal1);
	}

	private void setChangeNotice(ObjectBean objectbean) throws WTException, ChangeException2
	{
		if (objectbean.getParent() != null && (objectbean.getParent().getObject() instanceof ChangeOrderIfc))
		{
			changeOrder = (ChangeOrderIfc) objectbean.getParent().getObject();
		} else
		{
			wt.fc.QueryResult queryresult = ChangeHelper2.service.getChangeOrder((ChangeActivityIfc) objectbean.getObject(), true);
			if (queryresult != null && queryresult.hasMoreElements())
				changeOrder = (ChangeOrderIfc) queryresult.nextElement();
			else
				throw new WTException("Change Notice not found.");
		}
	}

	FormResult superPreProcess(NmCommandBean nmcommandbean, List list) throws WTException
	{
		return super.preProcess(nmcommandbean, list);
	}

	protected LocalizableMessage getProcessorErrorMessage()
	{
		return ChangeManagementFormProcessorHelper.getObjectFailureTitle(WTChangeActivity2.class, ComponentMode.EDIT);
	}

	protected WTList saveChangeItem(WTList wtlist) throws WTException
	{
		WTKeyedHashMap wtkeyedhashmap = new WTKeyedHashMap();
		wtkeyedhashmap.put(getChangeNotice(), wtlist);
		WTKeyedMap wtkeyedmap = ChangeHelper2.service.saveChangeActivities(wtkeyedhashmap);
		if (logger.isDebugEnabled())
			logger.debug((new StringBuilder()).append("The change notice is: ").append(getChangeNotice()).toString());
		WTList wtlist1 = (WTList) wtkeyedmap.get(getChangeNotice());
		ChangeManagementFormProcessorHelper.updateRoleParticipants(wtlist1, assigneeList, reviewerList);
		return wtlist1;
	}

	protected ChangeOrderIfc getChangeNotice()
	{
		return changeOrder;
	}

	static final long serialVersionUID = 1650562007L;
	private static final Logger logger = LogR.getLogger(CatlEditChangeTaskFormProcessor.class.getName());
	private ChangeOrderIfc changeOrder;
	public static final String DN_SEP = ":";
	private static final String ACTIVITY_ASSIGNEE = "changeTask_AssigneePicker_dn";
	private static final String ACTIVITY_REVIEWER = "changeTask_ReviewerPicker_dn";
	List assigneeList;
	List reviewerList;

	/** Override for save Inventory disposition Link **/
	@Override
	public FormResult doOperation(NmCommandBean clientData, List<ObjectBean> objectBeanList) throws WTException
	{
		FormResult result = super.doOperation(clientData, objectBeanList);
		// customized for catl ---- 2015-10-18
		System.out.println("=======================================================");
		System.out.println("=====start ECAPartLinkEditUtil doOperation for catl====");
		try
		{
			ECAPartLinkCreateEditUtil.doOperation(clientData, objectBeanList);
		} catch (Exception e)
		{
			result = ChangeManagementFormProcessorHelper.handleFormResultException(result, getLocale(), e, getProcessorErrorMessage());
		}
		System.out.println("=====end   ECAPartLinkEditUtil doOperation for catl====");
		System.out.println("=======================================================");
		// customized end ----
		return result;
	}

	
	//================DCA
	//DCA校验检查
	/**
	 *  检查添加到受影响对象列表的对象，必须满足下列条件：
	 * @param clientData
	 * @param objectBeanList
	 * @return
	 * @throws WTException 
	 */
	public static StringBuffer CheckEcavalidateSubmitItemByDca(NmCommandBean clientData, List<ObjectBean> objectBeanList) throws WTException{
		StringBuffer result = null;
		
		for (ObjectBean dcaBean : objectBeanList) {
			logger.info("dcaBean process start ecaBean =" + dcaBean.toString());
			List affectedItems = dcaBean.getAddedItemsByName("changeTask_affectedItems_table");
			logger.info("dca 受影响对象中 条目数 affectedItems.size() = " + affectedItems.size());
			// 验证受影响对象
			result = validateReleaseByAffectedItemsByDca(affectedItems);
			
		}
		
		return result;
	}
	
	/**
	 * 
	 *	物料成熟度属性值必须为1；
	 *+
	 *	若对象类型为是Catia 3D、Catia2D、线束Autocad图纸、PCBA装配图、Gerber文件，则检查其关联的物料成熟度必须为1。如果这些对象没有关联物料，则提示“<对象类型>XXXX没有关联零部件”；
	 *	物料或其他类型的数据对象必须是已发布状态；
	 *	物料或其他数据对象必须无关联的未完成的变更流程，即对象没有被加入状态不为“已解决”或“已取消”的ECA或DCA的受影响对象中，也没有被加入状态不为“已解决”或“已取消”的ECR的受影响对象中；
	 *	正在走FAE流程（ERP中的成熟度升级流程）的物料，不允许发起设计变更，在DCA中添加物料时需检查物料：如果物料的FAE状态如果是 “评估中”，不允许添加；
	 * @throws WTException
	 */
	public static StringBuffer validateReleaseByAffectedItemsByDca(List affectedItems) throws WTException {
		logger.debug("start to check ---------->");
		StringBuffer message = new StringBuffer();
		for (int i = 0; i < affectedItems.size(); i++) {
			NmOid nmOid = (NmOid) affectedItems.get(i);
			Object object = nmOid.getLatestIterationObject();

			if (object instanceof RevisionControlled) {
				RevisionControlled revisionControlled = (RevisionControlled) object;
				if(revisionControlled instanceof WTPart){
					
					String csdnum = (String)IBAUtil.getIBAValue(revisionControlled, PartConstant.IBA_CATL_Maturity);
					//物料成熟度属性值必须为1;
					if(!csdnum.equals("1")){
						message.append(BomWfUtil.getObjectnumber(revisionControlled) + ChangeConst.checkcsd1_masg);
					}
					
					String faestate = (String)IBAUtil.getIBAValue(revisionControlled, PartConstant.IBA_CATL_FAEStatus);
					if(PartConstant.CATL_FAEStatus_3.equals(faestate)){
						message.append(BomWfUtil.getObjectnumber(revisionControlled) + ChangeConst.checkfaepg_masg);
					}
					WTPart part = (WTPart)revisionControlled;
					if(PromotionUtil.isSourceChangeUndone(part, null) > 0){
						message.append(part.getNumber() + ",对象正在采购类型更改单中，不允许添加到受影响列表中!\n");
					}
					if(PromotionUtil.isPlatformChangeUndone(part, null) > 0){
						message.append(part.getNumber() + ",对象正在产品线标识更改单中，不允许添加到受影响列表中!\n");
					}
					if(PromotionUtil.isexsitPromotion(part) > 0){
						message.append(part.getNumber() + ",对象正在设计禁用单中，不允许添加到受影响列表中! \n");
					}
					if(MaturityUpReportHelper.isNFAEUndone(part)){
						message.append(part.getNumber() + ",对象正在非FAE流程单中，不允许添加到受影响列表中!\n");
					}
				}else if(revisionControlled instanceof WTDocument){
					WTDocument doc = (WTDocument)revisionControlled;
					String doctyp = ChangeUtil.getStrSplit(doc);
					if ((doctyp.endsWith(TypeName.doc_type_autocadDrawing) || doctyp.endsWith(TypeName.doc_type_pcbaDrawing) || doctyp.endsWith(TypeName.doc_type_gerberDoc))) {
						WTPart decpart = PartUtil.getRelationPart(doc);
						if(decpart != null){
							String csdnum = (String)IBAUtil.getIBAValue(decpart, PartConstant.IBA_CATL_Maturity);
							//物料成熟度属性值必须为1;
							if(!StringUtils.equals("1", csdnum)){
								message.append(BomWfUtil.getObjectnumber(doc)+"关联的部件"+BomWfUtil.getObjectnumber(decpart) + ChangeConst.checkcsd1_masg);
							}
							
							String faestate = (String)IBAUtil.getIBAValue(decpart, PartConstant.IBA_CATL_FAEStatus);
							if(PartConstant.CATL_FAEStatus_3.equals(faestate)){
								message.append(BomWfUtil.getObjectnumber(revisionControlled) + ChangeConst.checkfaepg_masg);
							}
						}
						else {
							message.append(BomWfUtil.getObjectnumber(doc) + ChangeConst.checkNoGl_masg);
						}
					}
				}else if(revisionControlled instanceof EPMDocument){
					if(!BatchDownloadPDFUtil.isMiddleware((EPMDocument)revisionControlled)){
						WTPart epmpart = PartUtil.getRelationPart(revisionControlled);
						if(epmpart != null){
							String csdnum = (String)IBAUtil.getIBAValue(epmpart, PartConstant.IBA_CATL_Maturity);
							//物料成熟度属性值必须为1;
							if(!StringUtils.equals("1", csdnum)){
								message.append(BomWfUtil.getObjectnumber(revisionControlled)+"关联的部件"+BomWfUtil.getObjectnumber(epmpart) + ChangeConst.checkcsd1_masg);
							}
							
							String faestate = (String)IBAUtil.getIBAValue(epmpart, PartConstant.IBA_CATL_FAEStatus);
							if(PartConstant.CATL_FAEStatus_3.equals(faestate)){
								message.append(BomWfUtil.getObjectnumber(revisionControlled) + ChangeConst.checkfaepg_masg);
							}
						}
						else {
							message.append(BomWfUtil.getObjectnumber(revisionControlled) + ChangeConst.checkNoGl_masg);
						}
					}
				}
				
				//物料或其他类型的数据对象必须是已发布状态;
				if (!revisionControlled.getState().getState().toString().equals(PartState.RELEASED)) {
					message.append(BomWfUtil.getObjectnumber(revisionControlled) + ChangeConst.checkecadata_masg);
				}
				
				//物料或其他数据对象必须无关联的未完成的变更流程，即对象没有被加入状态不为“已解决”或“已取消”的ECA或DCA的受影响对象中，也没有被加入状态不为“已解决”或“已取消”的ECR的受影响对象中；
				if(PromotionUtil.isECUndone(revisionControlled)){
					message.append(BomWfUtil.getObjectnumber(revisionControlled) + ChangeConst.checkCFData_masg);
				}else if(PromotionUtil.isEcaAndDcaUndone(revisionControlled)){
					message.append(BomWfUtil.getObjectnumber(revisionControlled) + ChangeConst.checkCFData1_masg);
				}
				
			}
		}
		return message;
	}
	
}
