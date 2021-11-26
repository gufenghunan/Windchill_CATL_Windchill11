package com.catl.change.processors;

import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import wt.change2.*;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.fc.collections.*;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.State;
import wt.log4j.LogR;
import wt.org.WTPrincipal;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.session.SessionHelper;
import wt.util.*;

import com.catl.bom.workflow.BomWfUtil;
import com.catl.common.constant.*;
import com.catl.common.util.*;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.*;
import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.change2.ChangeTaskRoleParticipantHelper;
import com.ptc.windchill.enterprise.change2.forms.ChangeManagementFormProcessorHelper;
import com.ptc.windchill.enterprise.change2.forms.processors.CreateChangeItemFormProcessor;
import com.ptc.windchill.enterprise.wizardParticipant.WizardParticipantsHelper;
import com.ptc.windchill.enterprise.wizardParticipant.delegate.ParticipantSelectionDelegate;
import com.catl.cad.BatchDownloadPDFUtil;
import com.catl.change.ChangeUtil;
import com.catl.change.util.*;
import com.catl.change.inventory.ECAPartLinkCreateEditUtil;
import com.catl.doc.maturityUpReport.MaturityUpReportHelper;
import com.catl.part.PartConstant;
import com.catl.promotion.util.PromotionUtil;

public class CatlCreateChangeTaskFormProcessor extends CreateChangeItemFormProcessor {

	public CatlCreateChangeTaskFormProcessor() {
		changeOrder = null;
		assigneeList = new ArrayList();
		reviewerList = new ArrayList();
	}

	public FormResult preProcess(NmCommandBean nmcommandbean, List list) throws WTException {
		FormResult formresult = null;
		try {
			formresult = superPreProcess(nmcommandbean, list);
			if (formresult.getStatus() == FormProcessingStatus.SUCCESS) {
				for (int i = 0; i < list.size(); i++) {
					ObjectBean objectbean = (ObjectBean) list.get(i);
					preProcessWorkflowRoles(objectbean);
				}

			}
		} catch (Exception exception) {
			formresult = ChangeManagementFormProcessorHelper.handleFormResultException(formresult, getLocale(), exception, getProcessorErrorMessage());
		}
		return formresult;
	}

	@Override
	public FormResult postProcess(NmCommandBean clientData, List<ObjectBean> objectBeanList) throws WTException {
		FormResult result = new FormResult();
		logger.debug("start post process-------eca-task");
		StringBuffer message = new StringBuffer();
		try {
			ChangeActivity2 dca=(ChangeActivity2)objectBeanList.get(0).getObject();
			logger.debug("dca:"+dca);
		    String dcatype = ChangeUtil.getStrSplit(dca);
		    
		    //区分DCA和以前逻辑的处理
		    if(dcatype.equals(ChangeConst.CHANGETASK_TYPE_DCA)){
		    	message = CheckEcavalidateSubmitItemByDca(clientData, objectBeanList);
		    	
		    }else{
		    	message = CheckEcavalidateSubmitItem(clientData, objectBeanList, false);
				
				message.append(ChangeUtil.checkMaturity(objectBeanList.get(0).getObject(),objectBeanList).toString());
		    }

			if (message.length() > 0) {
				throw new WTException("错误:\n" + message);
			}
			result = super.postProcess(clientData, objectBeanList);
		} catch (Exception e) {
			logger.debug("save ECA occur exception");
			e.printStackTrace();
			result = ChangeManagementFormProcessorHelper.handleFormResultException(result, getLocale(), null, new Exception(""), e);
		}

		return result;
	}
	
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
			
			ChangeActivity2 dca=(ChangeActivity2)dcaBean.getObject();
			ChangeUtil.setIBABooleanValue(dca,PartConstant.CATL_Allow_Edit, true);
			
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
	 *	“零部件”没有被加入任何状态非“已完成”或“已取消”关联的采购类型更改单中，否则提示"对象正在采购类型更改单中，不能允许添加到设计变更单中！"
	 *  “零部件”没有被加入任何状态非“已发布”或“已取消”关联的非FAE物料成熟度3升级报告，否则提示“零部件XXXXXX已经被加入非FAE物料成熟度3升级报告XXXXXX中”；
	 * @throws WTException
	 */
	public static StringBuffer validateReleaseByAffectedItemsByDca(List affectedItems) throws WTException {
		logger.debug("start to check ---------->");
		StringBuffer message = new StringBuffer();
		CatiaCheck.checkAutoCAD(affectedItems);
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
					
					if (PromotionUtil.isSourceChangeUndone(revisionControlled, null) > 0) {
						message.append(BomWfUtil.getObjectnumber(revisionControlled) + ChangeConst.checkSC_masg);
					}
					if (PromotionUtil.isPlatformChangeUndone(revisionControlled, null) > 0) {
						message.append(BomWfUtil.getObjectnumber(revisionControlled) + ChangeConst.checkPC_masg);
					}
					if(MaturityUpReportHelper.isNFAEUndone((WTPart)revisionControlled)){
						message.append(BomWfUtil.getObjectnumber(revisionControlled) + ChangeConst.checkNFAE_masg);
					}
					if(PromotionUtil.isexsitPromotion((WTPart)revisionControlled) > 0){
						message.append(BomWfUtil.getObjectnumber(revisionControlled) + ChangeConst.checkDesignDisable_masg);
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
//						else {
//							message.append(BomWfUtil.getObjectnumber(doc) + ChangeConst.checkNoGl_masg);
//						}
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
//						else {
//							message.append(BomWfUtil.getObjectnumber(revisionControlled) + ChangeConst.checkNoGl_masg);
//						}
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

	public static StringBuffer CheckEcavalidateSubmitItem(NmCommandBean clientData, List<ObjectBean> objectBeanList, boolean isEdit) throws WTException {
		StringBuffer result = null;
		for (ObjectBean ecaBean : objectBeanList) {
			logger.info("ecaBean process start ecaBean =" + ecaBean.toString());
			List affectedItems = ecaBean.getAddedItemsByName("changeTask_affectedItems_table");
			logger.info("eca 受影响对象中 条目数 affectedItems.size() = " + affectedItems.size());
			// 验证受影响对象
			result = validateReleaseByAffectedItems(affectedItems);

		}
		return result;
	}

	public static StringBuffer validateReleaseByAffectedItems(List affectedItems) throws WTException {
		logger.debug("start to check ---------->");
		StringBuffer message = new StringBuffer();
		CatiaCheck.checkAutoCAD(affectedItems);
		for (int i = 0; i < affectedItems.size(); i++) {
			NmOid nmOid = (NmOid) affectedItems.get(i);
			Object object = nmOid.getLatestIterationObject();

			if (object instanceof RevisionControlled) {
				RevisionControlled revisionControlled = (RevisionControlled) object;
				logger.debug("object number==" + BomWfUtil.getObjectnumber(revisionControlled));
				if (!revisionControlled.getState().getState().toString().equals(PartState.RELEASED)) {
					message.append(BomWfUtil.getObjectnumber(revisionControlled) + ",不是已发布的状态，不能添加到受影响列表中！\n");
				}
				if (BomWfUtil.isECAchange(revisionControlled)) {
					message.append(BomWfUtil.getObjectnumber(revisionControlled) + ",有正在进行中的变更活动，不能添加到受影响列表中！\n");
				}
				if(revisionControlled instanceof WTPart){
					if (PromotionUtil.isSourceChangeUndone(revisionControlled, null) > 0) {
						message.append(BomWfUtil.getObjectnumber(revisionControlled) + ",对象正在采购类型更改单中，不能允许添加到受影响列表中！ \n");
					}
					if (PromotionUtil.isSourceChangeUndone(revisionControlled, null) > 0) {
						message.append(BomWfUtil.getObjectnumber(revisionControlled) + ",对象正在产品线标识更改单中，不能允许添加到受影响列表中！ \n");
					}
					if(MaturityUpReportHelper.isNFAEUndone((WTPart)revisionControlled)){
						message.append(BomWfUtil.getObjectnumber(revisionControlled) + ",对象正在非FAE成熟度报告中，不能允许添加到受影响列表中\n");
					}
					String faestate = (String)IBAUtil.getIBAValue((WTPart)revisionControlled, PartConstant.IBA_CATL_FAEStatus);
					if(PartConstant.CATL_FAEStatus_3.equals(faestate)){
						message.append(BomWfUtil.getObjectnumber(revisionControlled) + ",对象正在FAE流程单中，不能允许添加到受影响列表中！ \n");
					}
					if(PromotionUtil.isexsitPromotion((WTPart)revisionControlled) > 0){
						message.append(BomWfUtil.getObjectnumber(revisionControlled) + ChangeConst.checkDesignDisable_masg);
					}
				}
			}
		}
		return message;
	}

	private void preProcessWorkflowRoles(ObjectBean objectbean) throws WTPropertyVetoException, WTException {
		if (ParticipantSelectionDelegate.newInstance(objectbean).showMultipleParticipantSelection()) {
			logger.debug("Workflow roles are processes using the multiple participant selection component.");
			return;
		}
		boolean flag = ChangeManagementFormProcessorHelper.setRequiredReview(objectbean);
		WTPrincipal wtprincipal = ChangeManagementFormProcessorHelper.getUser("changeTask_AssigneePicker_dn", objectbean);
		WTPrincipal wtprincipal1 = ChangeManagementFormProcessorHelper.getUser("changeTask_ReviewerPicker_dn", objectbean);
		if (logger.isDebugEnabled()) {
			logger.debug((new StringBuilder()).append("preProcess assignee is : ").append(wtprincipal).toString());
			logger.debug((new StringBuilder()).append("preProcess reviewer is : ").append(wtprincipal1).toString());
		}
		if ((wtprincipal == null || wtprincipal1 == null) && !WizardParticipantsHelper.isChangeTemplateWizard(objectbean)) {
			ChangeActivity2 changeactivity2 = (ChangeActivity2) objectbean.getObject();
			WTContainerRef wtcontainerref = changeactivity2.getContainerReference();
			boolean flag1 = ChangeTaskRoleParticipantHelper.isRequired(objectbean.getObject(), wtcontainerref, objectbean);
			if (logger.isDebugEnabled()) {
				logger.debug((new StringBuilder()).append("preProcess ca2 is : ").append(changeactivity2).toString());
				logger.debug((new StringBuilder()).append("preProcess containerRef is : ").append(wtcontainerref).toString());
				logger.debug((new StringBuilder()).append("preProcess isRequired is : ").append(flag1).toString());
			}
			if (flag1) {
				if (wtprincipal == null)
					wtprincipal = SessionHelper.manager.getPrincipal();
				if (wtprincipal1 == null && flag)
					wtprincipal1 = SessionHelper.manager.getPrincipal();
			}
		}
		assigneeList.add(wtprincipal);
		reviewerList.add(wtprincipal1);
		if (logger.isDebugEnabled()) {
			logger.debug((new StringBuilder()).append("preProcess assigneeList is : ").append(assigneeList).toString());
			logger.debug((new StringBuilder()).append("preProcess reviewerList is : ").append(reviewerList).toString());
		}
	}

	FormResult superPreProcess(NmCommandBean nmcommandbean, List list) throws WTException {
		return super.preProcess(nmcommandbean, list);
	}

	protected LocalizableMessage getProcessorErrorMessage() {
		return ChangeManagementFormProcessorHelper.getObjectFailureTitle(WTChangeActivity2.class, ComponentMode.CREATE);
	}

	protected WTList saveChangeItem(WTList wtlist) throws WTException {
		WTList wtlist1 = saveChangeActivity(wtlist);
		wtlist1.deflate();
		return wtlist1;
	}

	protected WTList saveChangeItem(WTList wtlist, List list) throws WTException {
		setChangeNotice(list);
		return saveChangeItem(wtlist);
	}

	private void setChangeNotice(List list) throws WTException {
		if (changeOrder != null) {
			if (logger.isDebugEnabled())
				logger.debug("setChangeNotice was called with a null changeOrder");
			return;
		}
		for (int i = 0; i < list.size(); i++) {
			ObjectBean objectbean = (ObjectBean) list.get(i);
			if (objectbean.getParent() != null && (objectbean.getParent().getObject() instanceof ChangeOrderIfc)) {
				changeOrder = (ChangeOrderIfc) objectbean.getParent().getObject();
				break;
			}
			if (objectbean.getActionOid() == null)
				continue;
			NmOid nmoid = objectbean.getActionOid();
			if (!nmoid.isA(ChangeOrderIfc.class))
				continue;
			changeOrder = (ChangeOrderIfc) nmoid.getRefObject();
			break;
		}

		if (changeOrder == null) {
			logger.debug("Change Notice not found.");
			throw new WTException("Change Notice not found.");
		} else {
			return;
		}
	}

	protected WTList saveChangeActivity(WTList wtlist) throws WTException {
		WTKeyedHashMap wtkeyedhashmap = new WTKeyedHashMap();
		wtkeyedhashmap.put(getChangeNotice(), wtlist);
		WTKeyedMap wtkeyedmap = ChangeHelper2.service.saveChangeActivities(wtkeyedhashmap);
		if (logger.isDebugEnabled())
			logger.debug((new StringBuilder()).append("The change notice is: ").append(getChangeNotice()).toString());
		WTList wtlist1 = (WTList) wtkeyedmap.get(getChangeNotice());
		return updateRoleParticipants(wtlist1);
	}

	WTList updateRoleParticipants(WTList wtlist) throws WTException {
		ChangeManagementFormProcessorHelper.updateRoleParticipants(wtlist, assigneeList, reviewerList);
		return wtlist;
	}

	protected ChangeOrderIfc getChangeNotice() {
		return changeOrder;
	}

	protected String getIframeIdValue(ObjectBean objectbean) {
		return objectbean.getTextParameter("iframeId");
	}

	public void setChangeOrder(ChangeOrderIfc changeorderifc) {
		changeOrder = changeorderifc;
	}

	static final long serialVersionUID = 12052002434L;
	private static final Logger logger = LogR.getLogger(CatlCreateChangeTaskFormProcessor.class.getName());
	private ChangeOrderIfc changeOrder;
	private static final String ACTIVITY_ASSIGNEE = "changeTask_AssigneePicker_dn";
	private static final String ACTIVITY_REVIEWER = "changeTask_ReviewerPicker_dn";
	List assigneeList;
	List reviewerList;

	/* Customized for Inventory Disposition list */
	@Override
	public FormResult doOperation(NmCommandBean clientData, List<ObjectBean> objectBeanList) throws WTException {

		List<String> keyList = IframeFormProcessorHelper.getIframeKeyList(clientData);
		if (keyList == null || keyList.size() == 0) {
			// process request from ECA, and only create one ECA, remove the
			// duplicate ones.
			if (objectBeanList.size() > 1) {
				List<ObjectBean> beansCopy = new ArrayList<ObjectBean>();
				beansCopy.addAll(objectBeanList);
				ObjectBean bean = objectBeanList.get(0);
				objectBeanList.removeAll(beansCopy);
				objectBeanList.add(bean);
			}
		}

		FormResult result = super.doOperation(clientData, objectBeanList);
		// customized for catl ---- 2015-10-18
		System.out.println("=========================================================");
		System.out.println("=====start ECAPartLinkCreateUtil doOperation for catl====");
		try {
			ECAPartLinkCreateEditUtil.doOperation(clientData, objectBeanList);
		} catch (Exception e) {
			result = ChangeManagementFormProcessorHelper.handleFormResultException(result, getLocale(), e, getProcessorErrorMessage());
		}
		System.out.println("=====end   ECAPartLinkCreateUtil doOperation for catl====");
		System.out.println("=========================================================");
		// customized end ----

		CatlEditChangeTaskFormProcessor.changePersonincharge(objectBeanList);
		return result;
	}

}
