package com.catl.promotion.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.catl.bom.workflow.BomWfUtil;
import com.catl.common.constant.Constant;
import com.catl.common.util.IBAUtil;
import com.catl.integration.PIService;
import com.catl.integration.PartFAEDisableInfo;
import com.catl.integration.pi.part.sapfae.DTZPLMNCreateResponse.TRETURN;
import com.catl.integration.pi.part.sapfae.DTZPLMNCreateResponse;
import com.catl.part.PartConstant;
import com.catl.promotion.util.PromotionConst;

import wt.fc.ObjectIdentifier;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.WTObject;
import wt.maturity.PromotionNotice;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.pom.Transaction;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfBlock;
import wt.workflow.engine.WfConnector;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WorkItem;

public class FAEMaterialsMaturityExprFunction {

	private static final Logger LOGGER = Logger.getLogger(FAEMaterialsMaturityExprFunction.class);

	// ====================FAE物料成熟度升级流程=========================start
	public final static String FAE_init = "FAE_init";
	public final static String FAE_sendSAP = "FAE_sendSAP";
	public final static String FAE_setState = "FAE_setState";

	// 表达式处理
	public static Map<String, String> fae_expr(WTObject pbo, ObjectReference self, String expr_flag) throws WTException {

		LOGGER.info("pbo:" + pbo.getPersistInfo().toString());
		LOGGER.info("expr_flag:" + expr_flag);

		Map<String, String> massage = new HashMap<String, String>();
		Transaction transaction = null;

		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			transaction = new Transaction();
			transaction.start();

			if (FAE_init.equals(expr_flag)) {
				
				Set<String> rolesName = new HashSet<String>();
				rolesName = new HashSet<String>();
				rolesName.add(PromotionConst.SUBMITTER);
				PromotionWorkflowHelper.initTeamMembersCreator(pbo, self, rolesName);
				
			} else if (FAE_sendSAP.equals(expr_flag)) {

				Set<WTPart> list = new HashSet<WTPart>();

				// 获取流程变量temp的值
				int temp = (int) getVariableValue(PromotionConst.Temp, self);

				// 获取流程变量failNumber的值
				String numbers = (String) getVariableValue(PromotionConst.failNumber, self);

				if (temp == 0) {
					PromotionNotice pn = (PromotionNotice) pbo;
					list = BomWfUtil.getTargets(pn);
					//massage.put("310320-00001", "零部件310320-00001发送到SAP失败！");
				} else {
					PromotionNotice pn = (PromotionNotice) pbo;
					Set<WTPart> tempList = BomWfUtil.getTargets(pn);
					for (WTPart part : tempList) {
						if (numbers.contains(part.getNumber())) {
							list.add(part);
						}else {
							WTPartMaster master = (WTPartMaster) part.getMaster();
							String FAEStatus = (String) IBAUtil.getIBAValue(master, PartConstant.IBA_CATL_FAEStatus);
							if (FAEStatus == null) {
								PersistenceHelper.manager.save(IBAUtil.setIBAVaue(master, PartConstant.IBA_CATL_FAEStatus, PartConstant.CATL_FAEStatus_3));
							}else if (!FAEStatus.equals(PartConstant.CATL_FAEStatus_3)) {
								PersistenceHelper.manager.save(IBAUtil.setIBAVaue(master, PartConstant.IBA_CATL_FAEStatus, PartConstant.CATL_FAEStatus_3));
							}
						}
					}
				}

				WfProcess process = (WfProcess) self.getObject();
				List<PartFAEDisableInfo> lists = new ArrayList<PartFAEDisableInfo>();
				for (WTPart part : list) {
					PartFAEDisableInfo partBean = new PartFAEDisableInfo();
					partBean.setPartNumber(part.getNumber());
					partBean.setJobNumber(process.getCreator().getName());
					lists.add(partBean);
				}
				
				try {
					PIService service = PIService.getInstance();
					DTZPLMNCreateResponse response = service.sendPartFAEDisable(lists, Constant.COMPANY);
					
					List<TRETURN> treturns = response.getTRETURN();
					for (TRETURN treturn : treturns) {
						String number = treturn.getMATNR();// 物料编码
						String state = treturn.getSTATUS();// 值为S/s表示成功
						String message = treturn.getMESSAGE();// 错误信息
						if (!(state.equals("S") || state.equals("s"))) {
							String mess = "物料：" + number + "向SAP发送信息失败；" + "错误信息：" + message;
							massage.put(number, mess);
						}
					}
				} catch (Exception e) {
					for (PartFAEDisableInfo disableInfo : lists) {
						massage.put(disableInfo.getPartNumber(), e.getMessage());
					}
				}


			} else if (FAE_setState.equals(expr_flag)) {
				PromotionNotice pn = (PromotionNotice) pbo;
				Set<WTPart> list = BomWfUtil.getTargets(pn);
				for (WTPart part : list) {
					WTPartMaster master = (WTPartMaster) part.getMaster();
					String FAEStatus = (String) IBAUtil.getIBAValue(master, PartConstant.IBA_CATL_FAEStatus);
					if (FAEStatus == null) {
						PersistenceHelper.manager.save(IBAUtil.setIBAVaue(master, PartConstant.IBA_CATL_FAEStatus, PartConstant.CATL_FAEStatus_3));
					}else if (!FAEStatus.equals(PartConstant.CATL_FAEStatus_3)) {
						PersistenceHelper.manager.save(IBAUtil.setIBAVaue(master, PartConstant.IBA_CATL_FAEStatus, PartConstant.CATL_FAEStatus_3));
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

	// ====================FAE物料成熟度升级流程=========================end

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
}
