package com.catl.promotion.workflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.ObjectIdentifier;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.fc.collections.WTHashSet;
import wt.fc.collections.WTSet;
import wt.inf.library.WTLibrary;
import wt.maturity.MaturityException;
import wt.maturity.MaturityHelper;
import wt.maturity.PromotionNotice;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.pom.Transaction;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionControlHelper;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfBlock;
import wt.workflow.engine.WfConnector;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;

import com.catl.bom.workflow.BomWfUtil;
import com.catl.cadence.util.PartReleaseToCadenceUtil;
import com.catl.change.DataUtility.CatlPropertyHelper;
import com.catl.common.constant.Constant;
import com.catl.common.constant.PartState;
import com.catl.common.constant.TypeName;
import com.catl.common.util.EpmUtil;
import com.catl.common.util.PartUtil;
import com.catl.ecad.utils.ECADutil;
import com.catl.integration.PIService;
import com.catl.integration.PartDisableInfo;
import com.catl.integration.pi.part.disable.DTMSTAECreateResponse;
import com.catl.integration.pi.part.disable.DTMSTAECreateResponse.TRETURN;
import com.catl.promotion.bean.DesignDisabledXmlObjectBean;
import com.catl.promotion.dbs.DesignDisabledXmlObjectUtil;
import com.catl.promotion.resource.promotionResource;
import com.catl.promotion.util.PromotionConst;
import com.catl.promotion.util.PromotionUtil;
import com.catl.promotion.util.WorkflowUtil;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.windchill.enterprise.part.commands.AssociationLinkObject;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;

public class DesignDisabledExprFunction {

	private static final Logger LOGGER = Logger.getLogger(DesignDisabledExprFunction.class);

	// ====================物料设计禁用流程=========================start
	public final static String shjy_init = "shjy_init";
	public final static String shjy_setState = "shjy_setState";
	public final static String shjy_sendSAP = "shjy_sendSAP";

	// 表达式处理
	@SuppressWarnings("unused")
	public static Map<String, String> sjjy_expr(WTObject pbo, ObjectReference self, String expr_flag) throws WTException {

		LOGGER.info("pbo:" + pbo.getPersistInfo().toString());
		LOGGER.info("expr_flag:" + expr_flag);

		Map<String, String> massage = new HashMap<String, String>();
		Transaction transaction = null;

		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			transaction = new Transaction();
			transaction.start();

			if (shjy_init.equals(expr_flag)) {
				DesignDisabledXmlObjectUtil.initAppForm(pbo);

				Set<String> rolesName = new HashSet<String>();
				rolesName = new HashSet<String>();
				rolesName.add(PromotionConst.SUBMITTER);
				// rolesName.add(PromotionConst.PRODUCT_DATA_ENGINGEER); // BUG,产品数据工程师不能设置为自己
				PromotionWorkflowHelper.initTeamMembersCreator(pbo, self, rolesName);
			} else if (shjy_setState.equals(expr_flag)) {
				PromotionNotice pn = (PromotionNotice) pbo;
				Set<WTPart> list = BomWfUtil.getTargets(pn);
				for (WTPart part : list) {
					LOGGER.info("===part.getNumber():" + part.getNumber());
					PromotionUtil.setLifecycleState(part, PartState.DISABLEDFORDESIGN);
					List<EPMDocument> epmlist = getEPMDocument(part);
					LOGGER.info("epmlist.size():" + epmlist.size());
					for (EPMDocument epm : epmlist) {
						LOGGER.info("epm.getNumber():" + epm.getNumber());
						if(epm.getNumber().startsWith(part.getNumber())){
							PromotionUtil.setLifecycleState(epm, PartState.DISABLEDFORDESIGN);
						}
					}
					/*for (EPMDocument epm : epmlist) {
						LOGGER.info("epm.getNumber():" + epm.getNumber());
						boolean falg = PromotionUtil.is3DEPM(epm);
						LOGGER.info("falg:" + falg);
						if (falg) {
							PromotionUtil.setLifecycleState(epm, PartState.DISABLEDFORDESIGN);
							Collection<EPMDocument> pms = EpmUtil.getDrawings(epm);
							LOGGER.info("==getDrawings.size():" + pms.size());
							for (EPMDocument pm : pms) {
								LOGGER.info("==pm.getNumber():" + pm.getNumber());
								PromotionUtil.setLifecycleState(pm, PartState.DISABLEDFORDESIGN);
							}
						}
					}*/
					List<WTDocument> document = PromotionUtil.getAssociatedDescribeDocuments(part);
					for (WTDocument doc : document) {
						TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(doc);
						String type = ti.getTypename();
						if (type.endsWith(TypeName.gerberDoc) || type.endsWith(TypeName.pcbaDrawing) || type.endsWith(TypeName.autocadDrawing)) {
							PromotionUtil.setLifecycleState(doc, PartState.DISABLEDFORDESIGN);
						}
					}
				}
			} else if (shjy_sendSAP.equals(expr_flag)) {

				Set<WTPart> list = new HashSet<WTPart>();

				// 获取流程变量temp的值
				int temp = (int) getVariableValue(PromotionConst.Temp, self);

				// 获取流程变量failNumber的值
				String numbers = (String) getVariableValue(PromotionConst.failNumber, self);

				if (temp == 0) {
					PromotionNotice pn = (PromotionNotice) pbo;
					list = BomWfUtil.getTargets(pn);
					// massage.put("310320-00001", "零部件310320-00001发送到SAP失败！");
				} else {
					PromotionNotice pn = (PromotionNotice) pbo;
					Set<WTPart> tempList = BomWfUtil.getTargets(pn);
					for (WTPart part : tempList) {
						if (numbers.contains(part.getNumber())) {
							list.add(part);
						}
					}
				}

				List<PartDisableInfo> lists = new ArrayList<PartDisableInfo>();
				for (WTPart part : list) {
					//Update by szeng 2017-07-28 大版本为1部件的不发SAP
					//String version = VersionControlHelper.getVersionIdentifier(part).getValue();
					//if(!version.equals("1")){
						PartDisableInfo partBean = new PartDisableInfo();
						partBean.setPartNumber(part.getNumber());
						
						//Update by szeng 2017-09-04 发送设计禁用原因与ERP对应状态
						List<DesignDisabledXmlObjectBean> xmlObjs = DesignDisabledXmlObjectUtil.getXmlObjectUtil(pbo);
						System.out.println("============xmlObjs\t"+xmlObjs.size());
						if(xmlObjs.size() > 0){
							for(DesignDisabledXmlObjectBean ddxob: xmlObjs){
								System.out.println("==========PartNumber:\t"+part.getNumber());
								if(ddxob.getpartNumber().equals(part.getNumber())){
									String reason = ddxob.getreason();
									System.out.println("===========Reasoin:\t"+reason);
									String state = CatlPropertyHelper.getDisableReasonERPMapPropertyValue(reason);
									System.out.println("===========State\t"+state);
									partBean.setStatus(state);
								}
							}
						}else{
							partBean.setStatus(part.getState().toString());
						}
						
						lists.add(partBean);
					//}
				}
				PIService service = PIService.getInstance();
				DTMSTAECreateResponse response = service.sendPartDisable(lists, Constant.COMPANY);
				List<TRETURN> treturns = response.getTRETURN();

				for (TRETURN treturn : treturns) {
					String number = treturn.getMATNR();// 物料编码
					String state = treturn.getSTATUS();// 值为S/s表示成功
					String message = treturn.getMESSAGE();// 错误信息
					if (!(state.equals("S") || state.equals("s"))) {
						String mess = "物料：" + number + "向SAP更新零部件状态失败；" + "错误信息：" + message;
						massage.put(number, mess);
					}
				}
			}

			transaction.commit();
			transaction = null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new WTException(e);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(flag);
			if (transaction != null) {
				transaction.rollback();
				transaction = null;
			}
		}
		return massage;
	}

	public static Set<String> checkPartForDisable(WTPart partToDisable) throws WTException {
		Set<String> msgs = new LinkedHashSet<String>();
		WTPartMaster master = (WTPartMaster) partToDisable.getMaster();
		WTPart latestBigPart = (WTPart) PromotionUtil.getLatestVersionByMaster(master);
		String oldVersion = partToDisable.getVersionIdentifier().getValue();
		String latesVersion = latestBigPart.getVersionIdentifier().getValue();

		if (!partToDisable.getState().toString().endsWith(PartState.RELEASED)) {
			msgs.add(partToDisable.getNumber() + ",对象不是“已发布”状态！\n");
		}
		if (PromotionUtil.isECUndone(partToDisable)) {
			msgs.add(partToDisable.getNumber() + ",对象“被加入未完成 的变更（ECR，ECN，DCN）的受影响对象中”！ \n");
		}
		if (!oldVersion.equals(latesVersion)) {
			msgs.add(partToDisable.getNumber() + ",添加的对象不是“最后的大版本”！\n");
		}
		if (PromotionUtil.isexsitPromotion(partToDisable) > 0) {
			msgs.add(partToDisable.getNumber() + ",对象存在其他未完成（物料设计禁用单没有被取消或完成）设计禁用流程中！\n");
		}
		return msgs;
	}

	// 活动完成处理
	public static void sjjy_activity_complete(WTObject pbo, ObjectReference wi_ref) throws WTException, WTPropertyVetoException {
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			WfAssignedActivity wfaa = (WfAssignedActivity) wi_ref.getObject();
			LOGGER.info("pbo:" + pbo.getPersistInfo().toString());
			LOGGER.info("wi_ref:" + wfaa.getPersistInfo().toString());

			PromotionNotice pn = (PromotionNotice) pbo;
			String atid = WorkflowUtil.getActivityTemplateId(wfaa);
			Set<WTPart> list = BomWfUtil.getTargets(pn);
			List<DesignDisabledXmlObjectBean> mafbs = DesignDisabledXmlObjectUtil.getXmlObjectUtil(pbo);
			Set<String> msgs = new LinkedHashSet<String>();

			// 提交审核
			if (PromotionConst.design_disable_submit.equals(atid)) {
				WTSet addTargets = new WTHashSet();
				WTSet rmTargets = new WTHashSet();
				 
				for (DesignDisabledXmlObjectBean mafb : mafbs) {
					Long oid = mafb.getpartBranchId();
					String oi = "VR:wt.part.WTPart:" + oid.toString();
					WTPart part = (WTPart) WorkflowUtil.getObjectByOid(oi);
					WTPartMaster master = (WTPartMaster) part.getMaster();
					WTPart latestBigPart = (WTPart) PromotionUtil.getLatestVersionByMaster(master);
					String oldVersion = part.getVersionIdentifier().getValue();
					String latesVersion = latestBigPart.getVersionIdentifier().getValue();

					if (!(part.getState().toString().endsWith(PartState.RELEASED)||part.getState().toString().endsWith(PartState.DESIGN))) {
						msgs.add(part.getNumber() + ",对象不是“已发布”或“设计”状态，不允许完成任务！\n");
					}
					if (PromotionUtil.isECUndone(part)) {
						msgs.add(part.getNumber() + ",对象“被加入未完成 的变更（ECR，ECN，DCN）的受影响对象中”，不允许完成任务！ \n");
					}
					if (!oldVersion.equals(latesVersion)) {
						msgs.add(part.getNumber() + ",对象不是“最后的大版本”，不允许完成任！\n");
					}
					if (PromotionUtil.isexsitPromotion(part) > 1) {
						msgs.add(part.getNumber() + ",对象存在其他未完成（物料设计禁用单没有被取消或完成）设计禁用流程中，不允许完成任务！\n");
					}
					addTargets.add(latestBigPart);
				}
				
				boolean hasReleased = hasReleasedObjecs(pbo);
				if(hasReleased){
					Set<Object> noUsers = WorkflowUtil.validateNeedRoles(wfaa, PromotionConst.PRODUCT_DATA_ENGINGEER);
					if (noUsers.size() < 1) {
						msgs.add("流程团队中的角色“产品数据工程师”至少要设置一个人员!");
					}
				}		
				

				//[如果【设计禁用物料清单】为空,则提示用户"数据为空"]
				if(mafbs.size()==0){
					msgs.add("设计禁用物料清单中必须至少添加一个零部件!");
				}
				
				rmTargets.addAll(list);
				BomWfUtil.removeTargets(pn, rmTargets);
				BomWfUtil.addToTargets(pn, addTargets);
			} else if (PromotionConst.design_disable_confirm.equals(atid)) {
				WTSet addTargets = new WTHashSet();
				WTSet rmTargets = new WTHashSet();
				Set<WTPart> temp = new HashSet<WTPart>();

				Set<WTPart> parts = new HashSet<WTPart>();
				for (DesignDisabledXmlObjectBean mafb : mafbs) {
					Long oid = mafb.getpartBranchId();
					String oi = "VR:wt.part.WTPart:" + oid.toString();
					WTPart part = (WTPart) WorkflowUtil.getObjectByOid(oi.toString());
					parts.add(part);
				}

				for (WTPart part : parts) {
					temp.clear();
					WTPartMaster master = (WTPartMaster) part.getMaster();
					WTPart latestBigPart = (WTPart) PromotionUtil.getLatestVersionByMaster(master);
					String oldVersion = part.getVersionIdentifier().getValue();
					String latesVersion = latestBigPart.getVersionIdentifier().getValue();

					Set<WTPart> allTopParents = new HashSet<WTPart>();
					PromotionUtil.getPartTopParentObject(part, allTopParents);
					for (WTPart Tpart : allTopParents) {
						WTPartMaster maste = (WTPartMaster) Tpart.getMaster();
						WTPart latestPart = (WTPart) PromotionUtil.getLatestVersionByMaster(maste);
						if (Tpart.equals(latestPart)) {
							if (!Tpart.getState().toString().endsWith(PartState.DISABLEDFORDESIGN)) {
								temp.add(Tpart);
							}
						}
					}

					if (!(part.getState().toString().endsWith(PartState.RELEASED)||part.getState().toString().endsWith(PartState.DESIGN))) {
						msgs.add(part.getNumber() + ",对象不是“已发布”或“设计”状态，不允许完成任务！\n");
					}
					if (PromotionUtil.isECUndone(part)) {
						msgs.add(part.getNumber() + ",对象“被加入未完成 的变更（ECR，ECN，DCN）的受影响对象中”，不允许完成任务！ \n");
					}
					if (!oldVersion.equals(latesVersion)) {
						msgs.add(part.getNumber() + ",对象不是“最后的大版本”，不允许完成任！\n");
					}
					if (temp.size() > 0) {
						msgs.add(part.getNumber() + ",对象存在“有效父件”，不允许完成任务！\n");
					}
					if (PromotionUtil.isSubstituteParts(part)) {
						Set<WTPart> parents = PromotionUtil.getSubstitutePartsParent(part);
						if (parents.size() > 0) {
							for (WTPart parent : parents) {
								WTPart latestParent = (WTPart) PromotionUtil.getLatestVersionByMaster((WTPartMaster)parent.getMaster());
								if(parent.equals(latestParent)){
									if (!parent.getState().toString().endsWith(PartState.DISABLEDFORDESIGN) && !parts.contains(parent)) {
										msgs.add(part.getNumber() + ",对象是“替换件”，其有效父件" + parent.getNumber() + "必须也在该数据列表中；否则不允许完成任务！ \n");
									}
								}
							}
						}
					}
					if (PromotionUtil.isexsitPromotion(part) > 1) {
						msgs.add(part.getNumber() + ",对象存在其他未完成（物料设计禁用单没有被取消或完成）设计禁用流程中，不允许完成任！\n");
					}
					addTargets.add(latestBigPart);
				}
				
				//Update by szeng 把设计会签者改为知会，设计会签者无需必填
				/*Set<Object> noUsers = WorkflowUtil.validateNeedRoles(wfaa, PromotionConst.CATL_DESIGN_ENGINGEER);
				if (noUsers.size() < 1) {
					msgs.add("流程团队中的角色“设计会签者”至少要设置一个人员!");
				}*/
				Set<Object> noUsers = WorkflowUtil.validateNeedRoles(wfaa, PromotionConst.MATERIAL_CONTROL);
				if (noUsers.size() < 1) {
					msgs.add("流程团队中的角色“物料控制专员”至少要设置一个人员!");
				}
				noUsers = WorkflowUtil.validateNeedRoles(wfaa, PromotionConst.PROCUREMENT_REPRESENT);
				if (noUsers.size() < 1) {
					msgs.add("流程团队中的角色“采购代表”至少要设置一个人员!");
				}
				noUsers = WorkflowUtil.validateNeedRoles(wfaa, PromotionConst.COUNTERSIGN_PEOPLE);
				setVariableValue("otherCountersign",noUsers.size(),getProcess(wi_ref));

				rmTargets.addAll(list);
				BomWfUtil.removeTargets(pn, rmTargets);
				BomWfUtil.addToTargets(pn, addTargets);
			} else if (PromotionConst.design_disable_engineer.equals(atid) || PromotionConst.design_disable_pmc.equals(atid) || PromotionConst.design_disable_src.equals(atid) || PromotionConst.design_disable_other.equals(atid)) {
				for (DesignDisabledXmlObjectBean mafb : mafbs) {
					Long oid = mafb.getpartBranchId();
					String oi = "VR:wt.part.WTPart:" + oid.toString();
					WTPart part = (WTPart) WorkflowUtil.getObjectByOid(oi.toString());

					// 如果是电子电器件库的零部件，则需要经过“更新电子元器件库”人工节点
					// Object value = IBAUtil.getIBAValue(part, "cls");
					// LWCStructEnumAttTemplate node = ClassificationUtil.getLWCStructEnumAttTemplateByName(value.toString());
					// ArrayList<String> parentNameString = new ArrayList<String>();
					// ClassificationUtil.getLastNodeName(node, parentNameString);
					// String rootname = parentNameString.get(0).toString();

					if (part.getContainerReference().getReferencedClass() == WTLibrary.class && "电子电气件".equals(part.getContainerName())) {
						changeisElectricParts(pbo, wi_ref);
					}
				}
			}

			StringBuffer msg = new StringBuffer();
			int i = 1;
			for (String str : msgs) {
				if (!(str == null)) {
					msg.append(i++).append(". ").append(str).append("\n");
				}
			}
			if (msg.length() > 0) {
				throw new WTException(PromotionConst.RESOURCE, promotionResource.PRIVATE_CONSTANT_38, new Object[] { msg });
			}
		} finally {
			SessionServerHelper.manager.setAccessEnforced(flag);
		}
	}

	// ====================物料设计禁用流程=========================end

	/**
	 * @param pbo
	 *            ,self
	 * @return 更改工作流isApprove变量
	 */
	public static void changeIsApprove(WTObject pbo, ObjectReference self) throws WTException {
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			WfProcess process = getProcess(self);
			ProcessData processdata = process.getContext();
			if (processdata.getVariable(PromotionConst.IS_APPROVE) != null) {
				processdata.setValue(PromotionConst.IS_APPROVE, false);
			}
			process.setContext(processdata);
			wt.fc.PersistenceHelper.manager.save(process);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(flag);
		}
	}

	/**
	 * @param pbo
	 *            ,self
	 * @return 更改工作流isElectricParts变量
	 */
	public static void changeisElectricParts(WTObject pbo, ObjectReference self) throws WTException {
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			WfProcess process = getProcess(self);
			ProcessData processdata = process.getContext();
			if (processdata.getVariable(PromotionConst.isElectricParts) != null) {
				processdata.setValue(PromotionConst.isElectricParts, true);
			}
			process.setContext(processdata);
			wt.fc.PersistenceHelper.manager.save(process);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(flag);
		}
	}

	/**
	 * 获取流程变量的值
	 * 
	 * @param variable
	 * @param self
	 * @return
	 * @throws WTException
	 */
	public static Object getVariableValue(String variable, ObjectReference self) throws WTException {
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		Object value = null;
		try {
			WfProcess process = getProcess(self);
			ProcessData processdata = process.getContext();
			if (processdata.getVariable(variable) != null) {
				value = processdata.getValue(variable);
			}
		} finally {
			SessionServerHelper.manager.setAccessEnforced(flag);
		}
		return value;
	}

	/**
	 * 设置流程变量的值
	 * 
	 * @param variable
	 * @param value
	 * @param self
	 * @throws WTException
	 */
	public static void setVariableValue(String variable, Object value, WfProcess WfProcess) throws WTException {
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			ProcessData processdata = WfProcess.getContext();
			if (processdata.getVariable(variable) != null) {
				processdata.setValue(variable, value);
			}
			WfProcess.setContext(processdata);
			wt.fc.PersistenceHelper.manager.save(WfProcess);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(flag);
		}
	}

	/**
	 * @param self
	 * @return 获取流程对象
	 */
	public static WfProcess getProcess(Object obj) {
		if (obj == null) {
			return null;
		}
		try {
			Persistable persistable = null;
			if (obj instanceof Persistable) {
				persistable = (Persistable) obj;
			} else if (obj instanceof ObjectIdentifier) {
				persistable = PersistenceHelper.manager.refresh((ObjectIdentifier) obj);
			} else if (obj instanceof ObjectReference) {
				persistable = ((ObjectReference) obj).getObject();
			}
			if (persistable == null) {
				return null;
			}
			if (persistable instanceof WorkItem) {
				persistable = ((WorkItem) persistable).getSource().getObject();
			}
			if (persistable instanceof WfActivity) {
				persistable = ((WfActivity) persistable).getParentProcess();
			}
			if (persistable instanceof WfConnector) {
				persistable = ((WfConnector) persistable).getParentProcessRef().getObject();
			}
			if (persistable instanceof WfBlock) {
				persistable = ((WfBlock) persistable).getParentProcess();
			}
			if (persistable instanceof WfProcess) {
				return (WfProcess) persistable;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取part关联的EPMDocuemnt
	 */
	@SuppressWarnings("unchecked")
	public static List<EPMDocument> getEPMDocument(WTPart part) throws WTException {
		List<EPMDocument> list = new ArrayList<EPMDocument>();
		Collection<AssociationLinkObject> cols = PartDocServiceCommand.getAssociatedCADDocumentsAndLinks(part);
		for (AssociationLinkObject alo : cols) {
			EPMDocument epm = alo.getCadObject();
			list.add(epm);
		}
		return list;
	}
	
	/**
	 * 检查设计禁用流程中是否有发布过的物料
	 * @param pn
	 * @return
	 * @throws MaturityException
	 * @throws WTException
	 */
	public static boolean hasReleasedObjecs(WTObject pbo) throws MaturityException, WTException {

		QueryResult promotables = null;
		PromotionNotice pn = (PromotionNotice) pbo;
		promotables = MaturityHelper.service.getBaselineItems(pn);
		while (promotables.hasMoreElements()) {
			Object obj = promotables.nextElement();
			if (obj instanceof WTPart) {
				WTPart part = (WTPart) obj;		
				WTPartMaster master = (WTPartMaster) part.getMaster();
				WTPart releasedPart = PartUtil.getLatestReleasedPart(master);
				if(releasedPart != null){
					return true;
				}
			} 
		}
		return false;
	}
	
	
	/**
	 * 更新Cadence库中PCB的状态
	 * @param pn
	 * @return
	 * @throws Exception 
	 */
	public static void updatePCBState(WTObject pbo) throws Exception {
		QueryResult promotables = null;
		PromotionNotice pn = (PromotionNotice) pbo;
		promotables = MaturityHelper.service.getBaselineItems(pn);
		while (promotables.hasMoreElements()) {
			Object obj = promotables.nextElement();
			if (obj instanceof WTPart) {
				WTPart part = (WTPart) obj;		
				if(ECADutil.isPCB(part)){
					PartReleaseToCadenceUtil.updateStateToCadence(part);
				}
			} 
		}
	}
}
