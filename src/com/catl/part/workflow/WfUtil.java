package com.catl.part.workflow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTList;
import wt.inf.container.WTContainer;
import wt.inf.library.WTLibrary;
import wt.lifecycle.LifeCycleException;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.State;
import wt.maturity.MaturityException;
import wt.maturity.MaturityHelper;
import wt.maturity.PromotionNotice;
import wt.org.WTPrincipal;
import wt.part.WTPart;
import wt.pdmlink.PDMLinkProduct;
import wt.project.Role;
import wt.services.applicationcontext.implementation.ServiceProperties;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTInvalidParameterException;
import wt.util.WTProperties;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;

import com.catl.bom.workflow.BomWfUtil;
import com.catl.common.constant.PartState;
import com.catl.common.constant.RoleName;
import com.catl.common.util.GenericUtil;
import com.catl.common.util.PropertiesUtil;
import com.catl.common.util.ResultMessage;
import com.catl.common.util.WorkflowUtil;
import com.catl.doc.EDatasheetDocUtil;
import com.catl.require.constant.ConstantRequire;
import com.ibm.icu.impl.duration.impl.DataRecord.EUnitVariant;
import com.ptc.core.security.slcc.SLCCConstants.object_status;
import com.ptc.netmarkets.workflow.NmWorkflowHelper;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;
import com.ptc.windchill.uwgm.common.prefs.res.newCadDocPrefsResource;

public class WfUtil {

	/*
	 * this API will be called in the Part number apply workflow to decide
	 * whether PMC or Standard Engineer will review the parts
	 */
	private static Logger log = Logger.getLogger(WfUtil.class.getName());

	public static boolean isPromotableInProductContainer(WTObject pbo) {
		boolean isInProductContainer = true;
		PromotionNotice pn = (PromotionNotice) pbo;
		QueryResult promotables = null;
		try {
			promotables = MaturityHelper.service.getPromotionTargets(pn);
		} catch (MaturityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (promotables.hasMoreElements()) {
			Object obj = promotables.nextElement();
			if (obj instanceof WTPart) {
				WTPart part = (WTPart) obj;
				LifeCycleHelper.service.setLifeCycleState(part, State.toState("RELEASED"));
				WTContainer container = part.getContainer();
				if (container instanceof WTLibrary) {
					isInProductContainer = false;
					break;
				}

			}
		}

		return isInProductContainer;
	}

	public static StringBuffer checkReviewObjecs(PromotionNotice pn) throws MaturityException, WTException {
		StringBuffer message = new StringBuffer();
		QueryResult promotables = null;

		// promotables = MaturityHelper.service.getPromotionTargets(pn);
		promotables = MaturityHelper.service.getBaselineItems(pn);
		while (promotables.hasMoreElements()) {
			Object obj = promotables.nextElement();
			if (obj instanceof WTPart) {
				WTPart part = (WTPart) obj;
				// WTContainer container = part.getContainer();
				log.debug("part state==" + part.getState().toString());
				if (!part.getState().toString().endsWith(PartState.DESIGN) && !part.getState().toString().endsWith(PartState.WRITING)) {
					message.append(part.getNumber() + ",对象不是”设计“或“编制”状态，不能添加到升级对象中\n");
				}
				if (!BomWfUtil.checkifDesigner(part, pn)) {
					message.append("您不是部件：" + part.getNumber() + "的设计者！不能提交该对象 \n");
				}
				if (isexsitPromotion(part) > 0) {
					message.append(part.getNumber() + ",对象存在其他进行中的评审流程！ \n");
				}

				String partContainerName = part.getContainerName();
				String pnContainerName = pn.getContainerName();

				if (!(partContainerName.equals(pnContainerName))) {
					message.append("不能够添加其它存储库或产品库内的零部件,  物料" + part.getNumber() + " 所在的容器为" + partContainerName + ",升级请求所在的容器为" + pnContainerName + "\n");
				}

			} else {
				String number = BomWfUtil.getObjectnumber((Persistable) obj);
				message.append(number + ",不是零部件，不能添加到零部件申请升级请求中 \n");
			}
		}
		return message;
	}

	public static ResultMessage validate(WTObject pbo, ObjectReference self) {
		ResultMessage result = new ResultMessage();
		result.setSucceed(true);
		StringBuffer message = new StringBuffer();
		PromotionNotice pn = (PromotionNotice) pbo;
		try {
			BomWfUtil.refreshPromotableObject(pbo);
		} catch (WTException e1) {
			if (log.isDebugEnabled()) {
				log.debug("refresh Promotiontarges failed!");
			}
			e1.printStackTrace();
		}
		QueryResult promotables = null;
		try {
			WTPrincipal currentUser = SessionHelper.getPrincipal();
			promotables = MaturityHelper.service.getPromotionTargets(pn);
			int countForPartInLibrary = 0;
			int countForPartInProduct = 0;
			while (promotables.hasMoreElements()) {
				Object obj = promotables.nextElement();
				if (obj instanceof WTPart) {
					WTPart part = (WTPart) obj;
					WTContainer container = part.getContainer();
					if (container instanceof WTLibrary) {
						countForPartInLibrary++;
					} else if (container instanceof PDMLinkProduct) {
						countForPartInProduct++;
					}
					if (WorkInProgressHelper.isCheckedOut((Workable) obj)) {
						result.setSucceed(false);
						message.append("零部件:" + part.getNumber() + "被检出，请检入后提交！\n");
					}
					if (!part.getState().toString().endsWith(PartState.MODIFICATION) && !part.getState().toString().endsWith(PartState.WRITING)) {
						result.setSucceed(false);
						message.append(part.getNumber() + ",对象不是”修改“或“编制”状态，不能添加到升级对象中\n");
					}
					if (!BomWfUtil.checkifDesigner(part, (PromotionNotice) pbo)) {
						result.setSucceed(false);
						message.append("您不是部件：" + part.getNumber() + "的设计者！不能提交该对象 \n");
					}
					if (isexsitPromotion(part) > 1) {
						result.setSucceed(false);
						message.append(part.getNumber() + ",对象存在其他进行中的评审流程！ \n");
					}
					String pnContainerName = pn.getContainerName();
					String partContainerName = container.getName();
					if (!pnContainerName.equals(partContainerName)) {
						result.setSucceed(false);
						message.append("不能够添加其它存储库或产品库内的零部件:" + part.getNumber() + " 所在的容器为" + partContainerName + "升级请求所在容器为" + pnContainerName + "\n");
					}
					//datasheet check
					String datasheet = PropertiesUtil.getValueByKey("datasheet");
		            if(datasheet != null){
		            	String[] datasheetArr = datasheet.split(",");
			            for(String cls : datasheetArr){
			            	if(part.getNumber().startsWith(cls)){
			            		QueryResult qr = PartDocServiceCommand.getAssociatedReferenceDocuments(part);
			            		boolean haveDatasheet = false;
			            		while(qr.hasMoreElements()){
			            			WTDocument doc = (WTDocument)qr.nextElement();
			            			if(EDatasheetDocUtil.isEDatasheetDoc(doc)){
			            				haveDatasheet = true;
			            				break;
			            			}
			            		}
			            		if(!haveDatasheet){
			            			result.setSucceed(false);
									message.append("物料" + part.getNumber() + "必须关联Datasheet文档 \n");
			            		}
			            	}
			            }
		            }

				} else {
					String number = BomWfUtil.getObjectnumber((Persistable) obj);
					result.setSucceed(false);
					message.append(number + ",不是零部件，不能添加到零部件申请升级请求中 \n");
				}
			}
			if (countForPartInLibrary * countForPartInProduct > 0) {
				result.setSucceed(false);
				message.append(" 不能同时提交产品库和储存库中的物料申请");
			}

			WfAssignedActivity acivity = (WfAssignedActivity) self.getObject();
			WfProcess process = acivity.getParentProcess();
			Enumeration product_data_engingeer = process.getPrincipals(Role.toRole(RoleName.PRODUCT_DATA_ENGINGEER));

			log.debug("start to check process role------>");
			log.debug("process name==" + process.getName());
			
			//update by szeng 20171018 设备开发
			if(isOnlyEquipment(pbo)){
				Enumeration department_reviewer = process.getPrincipals(Role.toRole(RoleName.DEPARTMENT_REVIEW_PEOPLE));
				if (!department_reviewer.hasMoreElements()) {
					result.setSucceed(false);
					message.append("部门审核人角色不能为空，请在设置参与者中选择部门审核人！ \n");
				} else {
					department_reviewer.nextElement();
					if (department_reviewer.hasMoreElements()) {
						result.setSucceed(false);
						message.append("部门审核人角色只能选一个！ \n");
					}
					department_reviewer = process.getPrincipals(Role.toRole(RoleName.DEPARTMENT_REVIEW_PEOPLE));
					if (WorkflowUtil.isSelectSelf(department_reviewer, currentUser)) {
						result.setSucceed(false);
						message.append("部门审核人角色不能选择自己！ \n");
					}
				}
			}else{
				if (!product_data_engingeer.hasMoreElements()) {
					result.setSucceed(false);
					message.append("产品数据工程师角色不能为空，请在设置参与者中选择产品数据工程师！ \n");
				} else if (WorkflowUtil.isSelectSelf(product_data_engingeer, currentUser)) {
					result.setSucceed(false);
					message.append("产品数据工程师角色不能选择自己！ \n");
				}
				if (countForPartInLibrary > 0) {
					Enumeration standard_engingeer = process.getPrincipals(Role.toRole(RoleName.STANDARD_ENGINEER));
					if (!standard_engingeer.hasMoreElements()) {
						result.setSucceed(false);
						message.append("标准化工程师角色不能为空，请在设置参与者中选择标准化工程师！ \n");
					} else if (WorkflowUtil.isSelectSelf(standard_engingeer, currentUser)) {
						result.setSucceed(false);
						message.append("标准化工程师角色不能选择自己！ \n");
					}
					Enumeration department_reviewer = process.getPrincipals(Role.toRole(RoleName.DEPARTMENT_REVIEW_PEOPLE));
					if (!department_reviewer.hasMoreElements()) {
						result.setSucceed(false);
						message.append("部门审核人角色不能为空，请在设置参与者中选择部门审核人！ \n");
					} else {
						department_reviewer.nextElement();
						if (department_reviewer.hasMoreElements()) {
							result.setSucceed(false);
							message.append("部门审核人角色只能选一个！ \n");
						}
						department_reviewer = process.getPrincipals(Role.toRole(RoleName.DEPARTMENT_REVIEW_PEOPLE));
						if (WorkflowUtil.isSelectSelf(department_reviewer, currentUser)) {
							result.setSucceed(false);
							message.append("部门审核人角色不能选择自己！ \n");
						}
					}
				}
			}
		} catch (WTException e) {
			// TODO Auto-generated catch block
			message.append(e.getMessage());
			result.setSucceed(false);
			e.printStackTrace();

		}
		log.debug(message);
		result.setMessage(message);
		return result;
	}

	public static int isexsitPromotion(WTObject object) throws WTException {
		QueryResult prcounts = BomWfUtil.isHavePromoteRequest(object);
		HashSet<String> pstateHashSet = new HashSet<String>();
		// check process state ,if has excuted
		while (prcounts.hasMoreElements()) {
			Object[] object2 = (Object[]) prcounts.nextElement();
			PromotionNotice promotion = (PromotionNotice) object2[0];
			QueryResult processResult = new QueryResult();
			try {
				processResult = NmWorkflowHelper.service.getAssociatedProcesses(promotion, null, null);
				log.debug("processResult size====" + processResult.size());
			} catch (WTException e1) {
				log.debug("getAssociatedProcesses failed----!");
				e1.printStackTrace();
			}
			if (processResult.hasMoreElements()) {
				WfProcess process = (WfProcess) processResult.nextElement();
				log.debug("process.getState().getDisplay()" + process.getState().getDisplay());
				if (process.getState().toString().endsWith("OPEN_RUNNING")) {
					pstateHashSet.add(process.getName());
				}
			}
		}
		log.debug("pstateHashSet.size() ==" + pstateHashSet.size());

		return pstateHashSet.size();
	}

	/* set state of Promotable */

	public static void setStateForPromotables(WTObject pbo, String state) throws WTInvalidParameterException, LifeCycleException, WTException {

		PromotionNotice pn = (PromotionNotice) pbo;
		QueryResult promotables = null;
		WTList parts = new WTArrayList(20);
		try {
			promotables = MaturityHelper.service.getPromotionTargets(pn);
		} catch (MaturityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		while (promotables.hasMoreElements()) {
			parts.add(promotables.nextElement());
		}

		LifeCycleHelper.service.setLifeCycleState(parts, State.toState(state), false);

	}

	public static void main(String args[]) {

		WTObject pbo = (WTObject) GenericUtil.getInstance("wt.maturity.PromotionNotice:134297");
		try {
			setStateForPromotables(pbo, "REVIEW");
		} catch (WTInvalidParameterException | WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/**
	 * 申请流程中是否仅包含设备开发零部件 
	 * update by szeng 20171018 设备开发
	 * @param pbo
	 * @return
	 * @throws WTException
	 */
	public static boolean isOnlyEquipment(WTObject pbo) throws WTException {
		PromotionNotice pn = (PromotionNotice) pbo;

		QueryResult promotables = null;
			promotables = MaturityHelper.service.getPromotionTargets(pn);
			while (promotables.hasMoreElements()) {
				Object obj = promotables.nextElement();
				if (obj instanceof WTPart) {
					WTPart part = (WTPart) obj;
					if(!(part.getNumber().startsWith("C")||part.getNumber().startsWith("6"))){
						return false;
					}
				} else if (obj instanceof EPMDocument) {
					EPMDocument epmdoc = (EPMDocument) obj;
					if(!(epmdoc.getNumber().startsWith("C")||epmdoc.getNumber().startsWith("6"))){
						return false;
					}
				} else {
					return false;
				}
			}
		return true;
	}
	
	/**
	 * 申请流程中是否仅包含售后再利用件 
	 * update by hdong 20180104 \
	 * @param pbo
	 * @return
	 * @throws WTException
	 */
	public static boolean isOnlyAfterSale(WTObject pbo) throws WTException {
		PromotionNotice pn = (PromotionNotice) pbo;

		QueryResult promotables = null;
			promotables = MaturityHelper.service.getPromotionTargets(pn);
			List<WTPart> aftersales=new ArrayList<WTPart>();
			while (promotables.hasMoreElements()) {
				Object obj = promotables.nextElement();
				if (obj instanceof WTPart) {
					WTPart part = (WTPart) obj;
					if(part.getNumber().endsWith("S")&&!part.getNumber().endsWith("-S")&&part.getContainerName().equals(ConstantRequire.libary_aftersale)){
						aftersales.add(part);
					}else{
						return false;
					}
				} 
			}
			if(aftersales.size()>0){
				return true;
			}else{
				return false;
			}
		
	}
}
