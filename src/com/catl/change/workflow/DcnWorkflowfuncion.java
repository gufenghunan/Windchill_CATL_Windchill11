package com.catl.change.workflow;

import java.beans.PropertyVetoException;
import java.sql.Timestamp;
import java.util.*;

import org.apache.log4j.Logger;

import wt.change2.*;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.*;
import wt.lifecycle.State;
import wt.org.*;
import wt.part.WTPart;
import wt.project.Role;
import wt.team.Team;
import wt.team.TeamHelper;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;

import com.catl.cadence.util.NodeUtil;
import com.catl.change.ChangeUtil;
import com.catl.change.report.dcn.DCNAttachmentHtml;
import com.catl.change.report.ecr.ExportChangeObject2EXCEL;
import com.catl.change.util.ChangeConst;
import com.catl.common.constant.AttributeName;
import com.catl.common.constant.ChangeState;
import com.catl.common.constant.PartState;
import com.catl.common.constant.RoleName;
import com.catl.common.constant.TypeName;
import com.catl.common.util.EpmUtil;
import com.catl.common.util.GenericUtil;
import com.catl.common.util.IBAUtil;
import com.catl.common.util.PartUtil;
import com.catl.common.util.WorkflowUtil;
import com.catl.doc.maturityUpReport.workflow.WfUtil;
import com.catl.ecad.utils.ECADutil;
import com.catl.loadData.IBAUtility;
import com.catl.part.PartConstant;
import com.catl.promotion.util.PromotionConst;
import com.catl.promotion.workflow.PromotionWorkflowHelper;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.xworks.util.XWorksHelper;

public class DcnWorkflowfuncion {

	private static Logger log = Logger.getLogger(DcnWorkflowfuncion.class
			.getName());

	// 调用流程
	public static void start_workflow(WTObject pbo, String typeid)
			throws WTException {
		HashMap<String, String> mp = new HashMap<String, String>();
		// 1：DCN 2：DCA
		if (typeid.equals("1")) {
			log.debug(">>>>>>startWorkFlow : 启动DCN流程");
			WorkflowUtil.startWorkFlow(ChangeConst.WORKFLOWNAME_DCN, pbo, mp);
		} else if (typeid.equals("2")) {
			log.debug(">>>>>>startWorkFlow : 启动DCA流程");
			WorkflowUtil.startWorkFlow(ChangeConst.WORKFLOWNAME_DCA, pbo, mp);
		}
	}

	// ============================================ CATL DCN流程处理
	// ========================================================
	/**
	 * 将PBO的创建者加入到流程团队的“提交者”中;
	 * call:com.catl.change.workflow.DcnWorkflowfuncion.addCreateUser
	 * ((WTObject)pbo,self);
	 * 
	 * @throws WTException
	 */
	public static void addCreateUser(WTObject pbo, ObjectReference self)
			throws WTException {
		Set<String> rolesName = new HashSet<String>();
		rolesName.add(PromotionConst.SUBMITTER);
		PromotionWorkflowHelper.initTeamMembersCreator(pbo, self, rolesName);
		log.debug("=====addCreateUser : 将PBO的创建者加入到流程团队的“提交者”中");
	}

	/**
	 * DCN流程自启DCA流程 call :
	 * com.catl.change.workflow.DcnWorkflowfuncion.startDcaWorkByDcn(pbo);
	 * 
	 * @param pbo
	 * @throws WTException
	 */
	public static Map<Boolean, String> startDcaWorkByDcn(WTObject pbo)
			throws WTException {
		Map<Boolean, String> mp = new HashMap<Boolean, String>();
		System.out.println("=========startDcaWorkByDcn============");
		if (pbo instanceof WTChangeOrder2) {
			WTChangeOrder2 dcn = (WTChangeOrder2) pbo;

			List<WTChangeActivity2> dcalist = ChangeUtil
					.getChangeActiveities(dcn);
			log.debug("=====startDcaWorkByDcn===== dcalist.size():"
					+ dcalist.size());

			for (WTChangeActivity2 dca : dcalist) {
				String dcatype = ChangeUtil.getStrSplit(dca);
				if (dcatype.equals(ChangeConst.CHANGETASK_TYPE_DCA)) {
					start_workflow(dca, "2");
				}
			}
			mp.put(true, "");
			return mp;
		}
		mp.put(false, "DCA流程启动错误,请尝试重新启动");
		System.out.println("=========endDcaWorkByDcn============");
		return mp;
	}

	/**
	 * 更新DCA的状态 call :
	 * com.catl.change.workflow.DcnWorkflowfuncion.setDcaState(pbo,"?"); 1.正在审阅
	 * 2.开启 3.取消 4.实施 5.返工
	 * 
	 * @throws WTException
	 */
	public static void setDcaState(WTObject pbo, String stateId)
			throws WTException {
		log.debug(">>>>>>setDcaState : 设置生命周期");
		if (stateId.equals("1")) {
			DcnWorkUtil.setDcaStateByDcn(pbo, ChangeState.UNDER_REVIEW);
		} else if (stateId.equals("2")) {
			DcnWorkUtil.setDcaStateByDcn(pbo, ChangeState.OPEN);
		} else if (stateId.equals("3")) {
			DcnWorkUtil.setDcaStateByDcn(pbo, ChangeState.CANCELLED);
		} else if (stateId.equals("4")) {
			DcnWorkUtil.setDcaStateByDcn(pbo, ChangeState.IMPLEMENTATION);
		} else if (stateId.equals("5")) {
			DcnWorkUtil.setDcaStateByDcn(pbo, ChangeState.REWORK);
		}
	}

	/**
	 * 更新DCN和DCA的属性 更新DCN的“允许编辑”为False，“允许创建的DCA类型”为B
	 * 所有DCN相关的DCA中，如果任务名称值为“研发更改任务”，则将DCA的“允许编辑”属性更新为False； call:
	 * com.catl.change.workflow.DcnWorkflowfuncion.updateAttribute(pbo);
	 * 
	 * @throws WTException
	 */
	public static void updateAttribute(WTObject pbo) throws WTException {
		log.debug(">>>>>>setDcaState : 设置属性");
		if (pbo instanceof WTChangeOrder2) {
			WTChangeOrder2 dcn = (WTChangeOrder2) pbo;
			Object flag = false;
			PersistenceHelper.manager.save(IBAUtil.setIBAVaue(dcn,
					PartConstant.CATL_Allow_Edit, flag));
			PersistenceHelper.manager.save(IBAUtil.setIBAVaue(dcn,
					PartConstant.CATL_Allowed_DCA, "B"));

			List<WTChangeActivity2> dcalist = ChangeUtil
					.getChangeActiveities(dcn);
			for (WTChangeActivity2 dca : dcalist) {
				// if(dca.getName().equals(ChangeConst.YFGGRW)){
				PersistenceHelper.manager.save(IBAUtil.setIBAVaue(dca,
						PartConstant.CATL_Allow_Edit, false));
				// }
			}
		}
	}

	/**
	 * call:com.catl.change.workflow.DcnWorkflowfuncion.addAttachment(pbo);
	 * 
	 * @throws PropertyVetoException
	 * @throws WTException
	 */
	public static void addAttachment(WTObject pbo) throws WTException,
			PropertyVetoException {
		if (pbo instanceof WTChangeOrder2) {
			WTChangeOrder2 dcn = (WTChangeOrder2) pbo;

			ContentHolder ch = (ContentHolder) ContentHelper.service
					.getContents(dcn);

			// 移除附件组的附件
			ApplicationData appdate = DCNAttachmentHtml.getAttchmentByDcn(dcn,
					ChangeConst.EXCEL);
			if (appdate != null) {
				DCNAttachmentHtml.removeAttachmentToDcn(dcn, ch,
						DCNAttachmentHtml.getAttchmentByDcn(dcn,
								ChangeConst.EXCEL));
				dcn = (WTChangeOrder2) PersistenceHelper.manager.refresh(dcn);
			}

			// 添加附件到对象
			ExportChangeObject2EXCEL.attachAffedtedProduct(pbo);

			// 添加最新附件到附件组
			DCNAttachmentHtml
					.addAttachmentToDcn(dcn, ch, DCNAttachmentHtml
							.getAttchmentByDcn(dcn, ChangeConst.EXCEL));
		}
	}

	/**
	 * 驳回的处理 更新DCN和DCA的属性：更新DCN的“允许编辑”为True，“允许创建的DCA类型”为A_B
	 * 所有DCN相关的DCA的“允许编辑”属性更新为True； call:
	 * com.catl.change.workflow.DcnWorkflowfuncion.updateAttribute1(pbo);
	 * 
	 * @throws WTException
	 */
	public static void updateAttribute1(WTObject pbo) throws WTException {
		log.debug(">>>>>>setDcaState : 设置属性1");
		if (pbo instanceof WTChangeOrder2) {
			WTChangeOrder2 dcn = (WTChangeOrder2) pbo;
			PersistenceHelper.manager.save(IBAUtil.setIBAVaue(dcn,
					PartConstant.CATL_Allow_Edit, true));
			PersistenceHelper.manager.save(IBAUtil.setIBAVaue(dcn,
					PartConstant.CATL_Allowed_DCA, "A_B"));

			List<WTChangeActivity2> dcalist = ChangeUtil
					.getChangeActiveities(dcn);
			for (WTChangeActivity2 dca : dcalist) {
				// if(!dca.getName().equals(ChangeConst.YFGGRW)){
				PersistenceHelper.manager.save(IBAUtil.setIBAVaue(dca,
						PartConstant.CATL_Allow_Edit, true));
				// }
			}
		}
	}

	// 提交DCR
	/**
	 * call: com.catl.change.workflow.DcnWorkflowfuncion.checkDcnReturnMsg(
	 * WTChangeOrder2 dcn,ObjectReference self);
	 * 
	 * @param dcn
	 * @param self
	 * @return String 错误信息
	 * @throws WTException
	 */
	public static String checkDcnReturnMsg(WTChangeOrder2 dcn,
			ObjectReference self) throws WTException {
		StringBuffer sbf = new StringBuffer();

		// 1 .附件分组“变更方案”必须至少上传一个附件，否则提示“必须上传变更方案”
		Role docRole = Role.toRole(PromotionConst.SUBMITTER);
		WorkItem item = DcnWorkUtil.getWItemBySelf(dcn, docRole);
		List list = XWorksHelper.getAttachmentGroupService().getAttachments(
				item, ChangeConst.ATTACHMENT_01);
		if (list.size() == 0) {
			sbf.append("必须上传变更方案\n");
		}

		// 2 .DCN必须至少有一个关联的DCA，否则提示“需要创建至少一个设计变更任务”
		List<WTChangeActivity2> listeca = ChangeUtil.getChangeActiveities(dcn);
		if (listeca.size() == 0) {
			sbf.append("需要创建至少一个设计变更任务\n");
		}

		// 3 .流程团队“部门经理”必须至少设置一个人员，否则提示“部门经理尚未设置”
		sbf.append(DcnWorkUtil.getRoleByProcess(dcn, self));

		return sbf.toString();
	}

	// 提交DCR
	/**
	 * call: com.catl.change.workflow.DcnWorkflowfuncion.checkDcnReturnMsg(
	 * WTChangeOrder2 dcn,ObjectReference self);
	 * 
	 * @param dcn
	 * @param self
	 * @return String 错误信息
	 * @throws WTException
	 */
	public static String checkDcnReturnMsg_equip(WTChangeOrder2 dcn,
			ObjectReference self) throws WTException {
		StringBuffer sbf = new StringBuffer();

		// 1 .附件分组“变更方案”必须至少上传一个附件，否则提示“必须上传变更方案”
		Role docRole = Role.toRole(PromotionConst.SUBMITTER);
		WorkItem item = DcnWorkUtil.getWItemBySelf(dcn, docRole);
		List list = XWorksHelper.getAttachmentGroupService().getAttachments(
				item, ChangeConst.ATTACHMENT_01);
		if (list.size() == 0) {
			sbf.append("必须上传变更方案\n");
		}

		// 2 .DCN必须至少有一个关联的DCA，否则提示“需要创建至少一个设计变更任务”
		List<WTChangeActivity2> listeca = ChangeUtil.getChangeActiveities(dcn);
		if (listeca.size() == 0) {
			sbf.append("需要创建至少一个设计变更任务\n");
		}

		// 3 .流程团队“部门经理”必须至少设置一个人员，否则提示“部门经理尚未设置”

		// update by szeng 20171024 #REQ-64
		// 设备开发或原理图PCB图变更，流程团队“部门经理”必须至少设置一个人员，否则提示“部门经理尚未设置”
		
		
		sbf.append(DcnWorkUtil.getFMRoleByProcess(dcn, self));
		
		return sbf.toString();
	}
	
	// 提交DCR
		/**
		 * call: com.catl.change.workflow.DcnWorkflowfuncion.checkDcnReturnMsg(
		 * WTChangeOrder2 dcn,ObjectReference self);
		 * 
		 * @param dcn
		 * @param self
		 * @return String 错误信息
		 * @throws WTException
		 */
		public static String checkDcnReturnMsg_ebus(WTChangeOrder2 dcn,
				ObjectReference self) throws WTException {
			StringBuffer sbf = new StringBuffer();

			// 1 .附件分组“变更方案”必须至少上传一个附件，否则提示“必须上传变更方案”
			Role docRole = Role.toRole(PromotionConst.SUBMITTER);
			WorkItem item = DcnWorkUtil.getWItemBySelf(dcn, docRole);
			List list = XWorksHelper.getAttachmentGroupService().getAttachments(
					item, ChangeConst.ATTACHMENT_01);
			if (list.size() == 0) {
				sbf.append("必须上传变更方案\n");
			}

			// 2 .DCN必须至少有一个关联的DCA，否则提示“需要创建至少一个设计变更任务”
			List<WTChangeActivity2> listeca = ChangeUtil.getChangeActiveities(dcn);
			if (listeca.size() == 0) {
				sbf.append("需要创建至少一个设计变更任务\n");
			}

			// 3 .流程团队“部门经理”必须至少设置一个人员，否则提示“部门经理尚未设置”

			// update by szeng 20171221 #REQ-89
			// 变更调整
			sbf.append(DcnWorkUtil.checkEbusDcn(dcn, self));
			
			return sbf.toString();
		}

		// 提交DCR
				/**
				 * call: com.catl.change.workflow.DcnWorkflowfuncion.checkDcnReturnMsg(
				 * WTChangeOrder2 dcn,ObjectReference self);
				 * 
				 * @param dcn
				 * @param self
				 * @return String 错误信息
				 * @throws WTException
				 */
				public static String checkDcnReturnMsg_md(WTChangeOrder2 dcn,
						ObjectReference self) throws WTException {
					StringBuffer sbf = new StringBuffer();

					// 1 .附件分组“变更方案”必须至少上传一个附件，否则提示“必须上传变更方案”
					Role docRole = Role.toRole(PromotionConst.SUBMITTER);
					WorkItem item = DcnWorkUtil.getWItemBySelf(dcn, docRole);
					List list = XWorksHelper.getAttachmentGroupService().getAttachments(
							item, ChangeConst.ATTACHMENT_01);
					if (list.size() == 0) {
						sbf.append("必须上传变更方案\n");
					}

					// 2 .DCN必须至少有一个关联的DCA，否则提示“需要创建至少一个设计变更任务”
					List<WTChangeActivity2> listeca = ChangeUtil.getChangeActiveities(dcn);
					if (listeca.size() == 0) {
						sbf.append("需要创建至少一个设计变更任务\n");
					}

					// 3 .流程团队“部门经理”必须至少设置一个人员，否则提示“部门经理尚未设置”

					// update by szeng 20171221 #REQ-89
					// 变更调整-开模图纸升级
					sbf.append(DcnWorkUtil.checkmdDcn(dcn, self));
					
					return sbf.toString();
				}
		
	// 提交DCN
	/**
	 * call: com.catl.change.workflow.DcnWorkflowfuncion.checkSubmitDcnMsg(
	 * WTChangeOrder2 dcn,ObjectReference self);
	 * 
	 * @param dcn
	 * @param self
	 * @return
	 * @throws WTException
	 */
	public static String checkSubmitDcnMsg(WTChangeOrder2 dcn,
			ObjectReference self) throws WTException {
		StringBuffer sbf = new StringBuffer();

		List<WTChangeActivity2> listeca = ChangeUtil.getChangeActiveities(dcn);
		Map<String, WTChangeActivity2> dcaMp = new HashMap<String, WTChangeActivity2>();
		for (WTChangeActivity2 dca : listeca) {
			dcaMp.put(dca.getName(), dca);
		}

		// 1.如果PBO（DCN）的属性“需要变更验证”值为True，则附件分组“变更验证报告”必须上传至少一个附件，如果没有上传附件，则提示用户“请至少上传一份变更验证报告！”;
		Object objyz = IBAUtil.getIBAValue(dcn, ChangeConst.CATL_NEED_VERIFY);
		if (objyz instanceof Boolean) {
			boolean flag = (boolean) objyz;
			if (flag) {
				Role docRole = Role.toRole(PromotionConst.SUBMITTER);
				WorkItem item = DcnWorkUtil.getWItemBySelf(dcn, docRole);
				List list = XWorksHelper.getAttachmentGroupService()
						.getAttachments(item, ChangeConst.ATTACHMENT_04);
				if (list.size() == 0) {
					sbf.append("请至少上传一份变更验证报告!\n");
				}
			}
		}

		// 2.检查DCN至少有一个关联的DCA，否则提示用户“请至少创建一个DCA！”；
		if (listeca.size() == 0) {
			sbf.append("请至少创建一个DCA！\n");
		}

		// 3.如果设计更改单DCN的属性“需PMC处理旧料”值为True，则必须有至少一个与DCN相关的DCA且任务名称为“物控与计划控制更改任务”，否则提示“请至少创建一个‘物控与计划控制更改任务’”；
		Object objPmc = IBAUtil.getIBAValue(dcn, ChangeConst.CATL_NEEDPMC);
		if (objPmc instanceof Boolean) {
			boolean flag = (boolean) objPmc;
			if (flag) {
				if (!dcaMp.containsKey(ChangeConst.WKYJHKZGGRW)) {
					sbf.append("请至少创建一个‘物控与计划控制更改任务！\n");
				}
			}
		}

		// 4.设置参与者检查逻辑：流程团队中的角色“产品数据工程师”、“系统工程师”中至少设置了一人。
		sbf.append(DcnWorkUtil.checkRoleByProcess(dcn, self));

		return sbf.toString();
	}

	// 设计更改单DCN的解决日期设置为系统当前日期
	/**
	 * com.catl.change.workflow.DcnWorkflowfuncion.setDateToDcn(WTObject pbo);
	 * 
	 * @throws Exception
	 * 
	 */
	public static void setDateToDcn(WTObject pbo) throws Exception {
		Timestamp ts = new Timestamp(System.currentTimeMillis());

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(AttributeName.RESOLUTION_DATE, ts);
		GenericUtil.updateObject(pbo, params);

	}

	// ============================================ CATL DCA流程处理
	// ========================================================
	/**
	 * call: com.catl.change.workflow.DcnWorkflowfuncion.setRoleByDca(WTObject
	 * pbo,ObjectReference self); 将DCA的属性工作负责人添加到流程团队的“工作负责人”中;
	 * 
	 * @throws WTException
	 */
	public static void setRoleByDca(WTObject pbo, ObjectReference self)
			throws WTException {
		if (pbo instanceof WTChangeActivity2) {
			WTChangeActivity2 dca = (WTChangeActivity2) pbo;
			String zzfzr = (String) GenericUtil.getObjectAttributeValue(dca,
					ChangeConst.ASSIGNEE);
			WTPrincipal wtprincipal = OrganizationServicesHelper.manager
					.getPrincipalByDN(zzfzr, null, false);

			WfProcess process = (WfProcess) self.getObject();
			Team team = (Team) process.getTeamId().getObject();

			TeamHelper.service.addRolePrincipalMap(
					Role.toRole(RoleName.ASSIGNEE), wtprincipal, team);

			// 设置DCA的“允许编辑”属性为true
			PersistenceHelper.manager.save(IBAUtil.setIBAVaue(dca,
					PartConstant.CATL_Allow_Edit, true));
		}
	}

	/**
	 * 检查DCA的受影响对象和结果对象，必须满足一下条件 call :
	 * com.catl.change.workflow.DcnWorkflowfuncion
	 * .checkDcaData_msg((WTObject)pbo,self);
	 * 
	 * @throws WTException
	 */
	public static String checkDcaData_msg(WTObject pbo, ObjectReference self)
			throws WTException {
		StringBuffer sbf = new StringBuffer();

		if (pbo instanceof WTChangeActivity2) {

			WTChangeActivity2 dca = (WTChangeActivity2) pbo;
			List<Object> afterlist = ChangeUtil.getAfterData(dca);

			// 1.校验DCA中所有的受影响对象，必须在产生的结果对象中都存在更新的大版本，否则提示“《对象类型》XXXXXX的在结果对象中没有对应的大版本”;
			sbf.append(DcnWorkUtil.checkMsgByDcaTata(dca));

			// 2.2.校验变更后对象（结果对象）状态必须是“已发布”，否则提示“《对象类型》XXXXXX状态必须为已发布”;
			for (Object o : afterlist) {
				if (o instanceof WTPart) {
					WTPart part = (WTPart) o;
					if (!part.getState().getState()
							.equals(State.toState("RELEASED"))) {
						sbf.append("<部件>" + part.getNumber()
								+ ChangeConst.checkstate_masg);
					}
				} else if (o instanceof WTDocument) {
					WTDocument doc = (WTDocument) o;
					if (!doc.getState().getState()
							.equals(State.toState("RELEASED"))) {
						sbf.append("<文档>" + doc.getNumber()
								+ ChangeConst.checkstate_masg);
					}
				} else if (o instanceof EPMDocument) {
					EPMDocument epm = (EPMDocument) o;
					if (!epm.getState().getState()
							.equals(State.toState("RELEASED"))) {
						sbf.append("<图纸>" + epm.getNumber()
								+ ChangeConst.checkstate_masg);
					}
				}
			}

			// 设置DCA“允许编辑”属性为false
			PersistenceHelper.manager.save(IBAUtil.setIBAVaue(dca,
					PartConstant.CATL_Allow_Edit, false));
		}

		return sbf.toString();
	}

	/**
	 * 设置名称为 "物控与计划控制更改任务" 的DCA允许编辑为否
	 * call:com.catl.change.workflow.DcnWorkflowfuncion
	 * .setDcaNameByYFEdit((WTObject)pbo);
	 * 
	 * @throws WTException
	 */
	public static void setDcaNameByYFEdit(WTObject pbo) throws WTException {
		if (pbo instanceof WTChangeActivity2) {
			WTChangeActivity2 dca = (WTChangeActivity2) pbo;
			if (dca.getName().equals(ChangeConst.WKYJHKZGGRW)) {
				PersistenceHelper.manager.save(IBAUtil.setIBAVaue(dca,
						PartConstant.CATL_Allow_Edit, false));
			}
		}

	}

	public static void checkDcnData_valid(WTObject wtobj)
			throws ChangeException2, WTException {
		QueryResult qr = null;
		if (wtobj instanceof WTChangeOrder2) {
			WTChangeOrder2 order2 = (WTChangeOrder2) wtobj;
			qr = ChangeHelper2.service.getChangeablesBefore(order2);
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				if (obj instanceof WTPart) {
					WTPart part = (WTPart) obj;

				} else if (obj instanceof EPMDocument) {
					EPMDocument epm = (EPMDocument) obj;
					if (ECADutil.isSCHEPM(epm)) {

					} else if (ECADutil.isPCBEPM(epm)) {

					}
				} else if (obj instanceof WTDocument) {
					WTDocument document = (WTDocument) obj;

				}
			}
		}

	}

	/**
	 * 判断DCN受影响对象中是否只有设备开发零部件 update by szeng 20171019 #REQ-64
	 * 
	 * @param order
	 * @return
	 * @throws WTException
	 * @throws ChangeException2
	 */
	public static boolean isOnlyEquipmentDCN(WTChangeOrder2 order)
			throws ChangeException2, WTException {

		IBAUtility iba = new IBAUtility(order);
		Vector vector = iba.getIBAValues("changeType");	
		if(!vector.contains("设备开发变更")){
			return false;
		}
		QueryResult qr = ChangeHelper2.service.getChangeablesBefore(order);
		
		if(!qr.hasMoreElements()){
			return false;
		}
		
		while (qr.hasMoreElements()) {
			Object obj = qr.nextElement();
			if (obj instanceof WTPart) {
				WTPart part = (WTPart) obj;
				if (!(part.getNumber().startsWith("C") || part.getNumber()
						.startsWith("6"))) {
					return false;
				}
			} else if (obj instanceof EPMDocument) {
				EPMDocument epmdoc = (EPMDocument) obj;
				String cadname = epmdoc.getCADName();
				if(!(cadname.toLowerCase().endsWith(".sldprt")||cadname.toLowerCase().endsWith(".sldasm")||cadname.toLowerCase().endsWith(".slddrw"))){
					return false;
				}
				if (!(epmdoc.getNumber().startsWith("C") || epmdoc.getNumber()
						.startsWith("6"))) {
					return false;
				}
			} else {
				return false;
			}
		}

		return true;
	}

	/**
	 * 判断DCN受影响对象中是否只有原理图、PCB图 update by szeng 20171019 #REQ-64
	 * 
	 * @param order
	 * @return
	 * @throws WTException
	 * @throws ChangeException2
	 */
	public static boolean isOnlyEcadDCN(WTChangeOrder2 order)
			throws ChangeException2, WTException {

		IBAUtility iba = new IBAUtility(order);
		Vector vector = iba.getIBAValues("changeType");	
		if(!vector.contains("ECAD图纸变更")){
			return false;
		}
		QueryResult qr = ChangeHelper2.service.getChangeablesBefore(order);
		
		if(!qr.hasMoreElements()){
			return false;
		}
		
		while (qr.hasMoreElements()) {
			Object obj = qr.nextElement();
			if (obj instanceof EPMDocument) {
				EPMDocument epm = (EPMDocument) obj;
				//if (!(ECADutil.isPCBEPM(epm) || ECADutil.isSCHEPM(epm))) {
				if (!ECADutil.isSCHEPM(epm)) {
					return false;
				}else{
					if(epm != null){
						Collection col = EpmUtil.getRelatedPartsLasted(epm);
					    for (Iterator iterator = col.iterator(); iterator.hasNext();) {
							Object object = (Object) iterator.next();
							if(object instanceof WTPart){
								WTPart part = (WTPart) object;
								WTPart releasedPart = PartUtil.getLatestReleasedPart(part.getMaster());
								if(releasedPart != null){
									System.out.println(releasedPart.getNumber()+"\t"+VersionControlHelper.getIterationDisplayIdentifier(releasedPart));
									return false;
								}
							}
						}
					}
				}
			} else {
				return false;
			}
		}

		return true;
	}
	
	/**
	 * 判断DCN受影响对象中是否只有原理图、PCB图 update by szeng 20171019 #REQ-64
	 * 
	 * @param order
	 * @return
	 * @throws WTException
	 * @throws ChangeException2
	 */
	public static boolean isOnlyEbusDCN(WTChangeOrder2 order)
			throws ChangeException2, WTException {

		IBAUtility iba = new IBAUtility(order);
		Vector vector = iba.getIBAValues("changeType");	
		if(!vector.contains("EBUS箱体间线束总成变更")){
			return false;
		}
		QueryResult qr = ChangeHelper2.service.getChangeablesBefore(order);
		
		if(!qr.hasMoreElements()){
			return false;
		}
		
		while (qr.hasMoreElements()) {
			Object obj = qr.nextElement();
			if (obj instanceof WTDocument) {
				WTDocument doc = (WTDocument) obj;
				if(!doc.getNumber().startsWith("PCL-")){
					return false;
				}
			}if(obj instanceof WTPart){
				WTPart part = (WTPart) obj;
				if(part.getNumber().startsWith("991400-")){
					return false;
				}
			} else {
				return false;
			}
		}

		return true;
	}

	/**
	 * 判断是否仅有设备开发零部件或原理图、PCB图
	 * 
	 * @param obj
	 * @return
	 * @throws ChangeException2
	 * @throws WTException
	 */
	public static boolean isEquipOrECAD(WTObject obj) throws ChangeException2,
			WTException {
		if (obj instanceof WTChangeOrder2) {
			WTChangeOrder2 order = (WTChangeOrder2) obj;
			if (isOnlyEcadDCN(order) || isOnlyEquipmentDCN(order)) {
				return true;
			}
		}
		return false;
	}
	
	public static String getChangeType(WTObject obj) throws WTException{
		String changeType = "";
		if (obj instanceof WTChangeOrder2) {
			WTChangeOrder2 order = (WTChangeOrder2) obj;
			IBAUtility iba = new IBAUtility(order);
			changeType = iba.getIBAValue("changeType");		
		}
		return changeType;
	}
	
	/**
	 * 判断DCN分支
	 * @param order
	 * @return
	 * @throws ChangeException2
	 * @throws WTException
	 */
	public static String getBranch(WTChangeOrder2 order) throws ChangeException2, WTException{
		IBAUtility iba = new IBAUtility(order);
		Vector vector = iba.getIBAValues("changeType");	
		if(vector.contains("EBUS箱体间线束总成变更")){
			//if(isOnlyEbusDCN(order)){
				return "Ebus";
			//}
		}else if(vector.contains("开模图纸升级")){
			return "MD";
		}else if(vector.contains("ECAD图纸变更")||vector.contains("设备开发变更")){
			if(isEquipOrECAD(order)){
				return "EquipECAD";
			}
		}
		
		return "Common";
		
	}
	
	public static Map<String, Vector<String>> getChangeBeforeData(WTObject wtobj) throws ChangeException2, WTException{
		//List<WTObject> data = new ArrayList<>();
		Vector<String> docs = new Vector<>();
		Vector<String> epms = new Vector<>();
		Vector<String> parts = new Vector<>();
		Map<String,Vector<String>> datas = new HashMap<>();
		QueryResult qr = null;
		if (wtobj instanceof WTChangeOrder2) {
			WTChangeOrder2 order2 = (WTChangeOrder2) wtobj;
			qr = ChangeHelper2.service.getChangeablesBefore(order2);
			while (qr.hasMoreElements()) {
				WTObject obj = (WTObject) qr.nextElement();
				if(obj instanceof WTDocument){
					docs.addElement(((WTDocument) obj).getNumber());
				}else if(obj instanceof EPMDocument){
					epms.addElement(((EPMDocument) obj).getNumber());
				}else if(obj instanceof WTPart){
					parts.addElement(((WTPart) obj).getNumber());
				}
				//data.add(obj);
			}
		}
		
		datas.put("docs", docs);
		datas.put("epms", epms);
		datas.put("parts", parts);
		
		return datas;		
	}
	
	public static String checkSoftData(WTObject wtobj) throws ChangeException2, WTException{
		StringBuffer message = new StringBuffer();
		Map<String, Vector<String>> data = getChangeBeforeData(wtobj);
		//Vector<String> docs = data.get("docs");
		//Vector<String> epms = data.get("epms");
		Vector<String> parts = data.get("parts");
		QueryResult qr = null;
		if (wtobj instanceof WTChangeOrder2) {
			WTChangeOrder2 order2 = (WTChangeOrder2) wtobj;
			qr = ChangeHelper2.service.getChangeablesBefore(order2);
			while (qr.hasMoreElements()) {
				WTObject obj = (WTObject) qr.nextElement();
				if(obj instanceof WTDocument){
					WTDocument document = (WTDocument) obj;
					TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(document);
					String doctype = ti.getTypename();
					if (doctype.endsWith(TypeName.softwareDoc)) {
						WTPart part = PartUtil.getRelationPartByDescDoc(document);
						if(part!=null){
							if(!parts.contains(part.getNumber()) && part.getState().toString().equalsIgnoreCase(PartState.RELEASED)){
								message.append("\n软件文档【"+document.getNumber()+"】关联的物料【"+part.getNumber()+"】是已发布状态必须和软件文档一起变更！");
								List<WTPart> parentParts = PartUtil.getParentPartByChildPart(part);
								for (int i = 0; i < parentParts.size(); i++) {
									WTPart parentPart = parentParts.get(i);
									if(!parts.contains(parentPart.getNumber()) && parentPart.getState().toString().equalsIgnoreCase(PartState.RELEASED)){
										message.append("\n软件文档【"+document.getNumber()+"】关联的总成【"+parentPart.getNumber()+"】是已发布状态必须和软件文档一起变更！");
									}
								}			
							}
											
						}else{
							message.append("\n软件文档【"+document.getNumber()+"】必须关联物料。");
						}
					}
				}else if(obj instanceof WTPart){
					WTPart part = (WTPart) obj;
					if(PartUtil.isSWPart(part)){
						String childEca = getECRADUndone(part);
						List<WTPart> parentParts = PartUtil.getParentPartByChildPart(part);
						for (int i = 0; i < parentParts.size(); i++) {
							WTPart parentPart = parentParts.get(i);
							if(!parts.contains(parentPart.getNumber()) && parentPart.getState().toString().equalsIgnoreCase(PartState.RELEASED)){
								message.append("\n软件物料【").append(part.getNumber()).append("】的父级物料【").append(parentPart.getNumber()).append("】是已发布状态必须和软件物料一起变更！");
							}
							
							String ecaNumber = getECRADUndone(parentPart);
							if(childEca !=null && ecaNumber!=null && !childEca.equals(ecaNumber)){
								message.append("\n软件物料【").append(part.getNumber()).append("】的父级物料【").append(parentPart.getNumber()).append("】在其他变更流程中。");
							}
						}			
					}
				}
			}
		}
		return message.toString();		
	}
	
	public static String checkParentPart(WTPart part) throws WTException{
		StringBuffer message = new StringBuffer();
		List<WTPart> parentParts = PartUtil.getParentPartByChildPart(part);
		String childEca = getECRADUndone(part);
		for (int i = 0; i < parentParts.size(); i++) {
			WTPart parentPart = parentParts.get(i);
			String ecaNumber = getECRADUndone(parentPart);
			if(childEca !=null && !childEca.equals(ecaNumber)){
				message.append("物料").append(part.getNumber()).append("的父级物料").append(parentPart.getNumber()).append("在其他变更流程中。\n");
			}
		}
		
		return message.toString();
	}
	
	/**
	 * 部件被加入的未完成变更（ECR,ECA,DCA）的受影响对象中
	 * @throws WTException 
	 */
	private static String getECRADUndone(Persistable persistable) throws WTException {
		try {
			Set<String> numbers = new HashSet<String>();
			QueryResult qr = PersistenceHelper.manager.navigate(persistable, RelevantRequestData2.CHANGE_REQUEST2_ROLE, RelevantRequestData2.class, true);
			while (qr.hasMoreElements()) {
				WTChangeRequest2 cr = (WTChangeRequest2) qr.nextElement();
				if (!cr.getState().toString().equalsIgnoreCase(ChangeState.RESOLVED) && !cr.getState().toString().equalsIgnoreCase(ChangeState.CANCELLED)) {
					numbers.add(cr.getNumber());
				}
			}

			ChangeActivity2 eca = ChangeUtil.getEcaWithPersiser(persistable);
			if (eca != null) {
				numbers.add(eca.getNumber());
			}
			if(!numbers.isEmpty()){
				return numbers.toString();
			}
		} catch (WTException e) {
			e.printStackTrace();
			throw e;
		}
		return null;
	}
}
