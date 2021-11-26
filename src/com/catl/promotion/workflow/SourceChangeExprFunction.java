package com.catl.promotion.workflow;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import wt.fc.ObjectIdentifier;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.maturity.PromotionNotice;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.pom.Transaction;
import wt.project.Role;
import wt.session.SessionServerHelper;
import wt.team.Team;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfBlock;
import wt.workflow.engine.WfConnector;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;

import com.catl.bom.workflow.BomWfUtil;
import com.catl.common.constant.PartState;
import com.catl.common.constant.RoleName;
import com.catl.common.util.CatlConstant;
import com.catl.promotion.bean.SourceChangeXmlObjectBean;
import com.catl.promotion.dbs.SourceChangeXmlObjectUtil;
import com.catl.promotion.util.PromotionUtil;

public class SourceChangeExprFunction {

	private static final Logger LOGGER = Logger.getLogger(SourceChangeExprFunction.class);

	// ====================采购类型变更流程=========================start
	public final static String sc_init = "sc_init";
	public final static String sc_tzcglx = "sc_tzcglx";

	// 表达式处理
	@SuppressWarnings("unused")
	public static Map<String, String> sc_expr(WTObject pbo, ObjectReference self, String expr_flag) throws WTException {

		LOGGER.info("pbo:" + pbo.getPersistInfo().toString());
		LOGGER.info("expr_flag:" + expr_flag);

		Map<String, String> message = new HashMap<String, String>();
		Transaction transaction = null;

		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			transaction = new Transaction();
			transaction.start();

			if (sc_init.equals(expr_flag)) {
				SourceChangeXmlObjectUtil.initAppForm(pbo);
			} else if (sc_tzcglx.equals(expr_flag)) {
				WfAssignedActivity acivity = (WfAssignedActivity) self.getObject();
				WfProcess process = acivity.getParentProcess();
				List<SourceChangeXmlObjectBean> list = SourceChangeXmlObjectUtil.getXmlObjectUtil(pbo);
				StringBuffer error = new StringBuffer();
				int j = 1;
				
				for(int i = 0; i < list.size(); i++){
					SourceChangeXmlObjectBean sco = list.get(i);
					ReferenceFactory rf = new ReferenceFactory();
					Persistable obj = rf.getReference("VR:wt.part.WTPart:"+sco.getPartBranchId()).getObject();
					WTPart part = (WTPart)obj;
					String sourceAfter = sco.getSourceAfter();
					if(sco.getSourceBefore().equals(sourceAfter)){
						error.append(j++).append(". ").append(part.getNumber() + "采购类型没有变化\n");
					}else{
						if(sourceAfter.startsWith(CatlConstant.MANUFACTURE_SOURCE_NAME) || sourceAfter.startsWith(CatlConstant.ASSISIT_SOURCE_NAME) || sourceAfter.startsWith(CatlConstant.VIRTUAL_SOURCE_NAME)) {
							QueryResult result = PersistenceHelper.manager.navigate(part, "uses", WTPartUsageLink.class, false);
							if (0 == result.size())
								error.append(j++).append(". ").append(part.getNumber() + "转'自制件'或'外协件'或'虚拟件'必须挂子件\n");
						}
						StringBuffer check = PromotionUtil.checkSourceChangeObjecs((PromotionNotice)pbo,part,sourceAfter);
						if(check.length() > 0)
							error.append(j++).append(". ").append(check.toString());
					}
					if(sourceAfter.equals(CatlConstant.BUY_SOURCE_NAME))//有变为 外购 的物料  SRC需参与会签
						message.put("checkFlag", "1");
				}
				Enumeration pmc = process.getPrincipals(Role.toRole(RoleName.PMC));
				Enumeration industrialEngineer = process.getPrincipals(Role.toRole(RoleName.INDUSTRIAL_ENGINEER));
				Enumeration procurementRepersent = process.getPrincipals(Role.toRole(RoleName.PROCUREMENT_REPRESENT));
				if(!pmc.hasMoreElements()){
					error.append(j++).append(". ").append("物料控制专员角色，至少选择一人\n");
				}
				if(!industrialEngineer.hasMoreElements()){
					error.append(j++).append(". ").append("工业工程师角色，至少选择一人\n");
				}
				if(message.get("checkFlag") != null && message.get("checkFlag").equals("1") && !procurementRepersent.hasMoreElements()){
					error.append(j++).append(". ").append("采购代表角色，至少选择一人\n");
				}else if(procurementRepersent.hasMoreElements()){//不管是否有变外购 只要有选择 SRC 那么就进入 SRC会签环节
					message.put("checkFlag", "1");
				}
				if(error.length() > 0){
					message.put("error", error.toString());
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
		return message;
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

	/**
	 * 流程完成处理逻辑
	 * 1 添加物料创建者、修改者 到知会人员中 用于邮件发送
	 * @param pbo
	 * @param self
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void activity_complete(WTObject pbo, ObjectReference self) throws WTException, WTPropertyVetoException {
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			WfProcess process = (WfProcess) self.getObject();

			PromotionNotice pn = (PromotionNotice) pbo;
			Set<WTPart> pos = BomWfUtil.getTargets(pn);
			for (WTPart part : pos) {
				part.getModifier();
				process = (WfProcess) self.getObject();
				Team team = (Team) process.getTeamId().getObject();
				Role role = Role.toRole(RoleName.INFORM_THE_STAFF);
				
				team.addPrincipal(role, part.getCreator().getPrincipal());
				team.addPrincipal(role, part.getModifier().getPrincipal());
			}
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

}
