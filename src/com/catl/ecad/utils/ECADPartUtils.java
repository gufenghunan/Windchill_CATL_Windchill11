package com.catl.ecad.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.catl.cadence.AdminApp;
import com.catl.cadence.conf.InitSystemConfigContant;
import com.catl.cadence.util.NodeUtil;
import com.catl.ecad.validator.StartWFFilter;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;

import wt.epm.EPMDocument;
import wt.fc.ObjectReference;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainer;
import wt.maturity.MaturityException;
import wt.maturity.MaturityHelper;
import wt.maturity.PromotionNotice;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.project.Role;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.team.Team;
import wt.team.TeamHelper;
import wt.util.WTException;
import wt.workflow.engine.WfProcess;

public class ECADPartUtils {
	public static ArrayList<WTPart> getPromotionTargetParts(PromotionNotice pn)
			throws MaturityException, WTException {
		ArrayList<WTPart> parts = new ArrayList<>();
		if (pn != null) {
			QueryResult qr = MaturityHelper.service.getPromotionTargets(pn);
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				if (obj instanceof WTPart) {
					WTPart part = (WTPart) obj;
					parts.add(part);
				}
			}
		}
		return parts;
	}

	public static void createSCHOrPCB(WTObject pbo) throws Exception {
		if (pbo instanceof PromotionNotice) {
			PromotionNotice pn = (PromotionNotice) pbo;
			if (pn != null) {
				QueryResult qr = MaturityHelper.service.getPromotionTargets(pn);
				while (qr.hasMoreElements()) {
					Object obj = qr.nextElement();
					if (obj instanceof WTPart) {
						WTPart part = (WTPart) obj;
						if (ECADutil.isPCBA(part)) {
							String number = part.getNumber();
							EPMDocument epm = CommonUtil
									.getEPMDocumentByNumber(number);
							if (epm != null) {
								continue;
							}
							String name = part.getName();
							WTContainer container = part.getContainer();
							Folder folder = FolderHelper.getFolder(part);
							String path = folder.getLocation()
									+ ECADConst.ECADLOCATION;
							Folder ecadFolder = FolderHelper.service.getFolder(
									path, part.getContainerReference());
							String creator = part.getCreatorName();
							// WTUser user = (WTUser)
							// part.getCreator().getObject();
							SessionHelper.manager.setPrincipal(creator);
							EPMUtil.createSchEPM(number, name, container,
									ecadFolder);
						} else if (ECADutil.isPCB(part)) {
							String number = part.getNumber();
							EPMDocument epm = CommonUtil
									.getEPMDocumentByNumber(number);
							if (epm != null) {
								continue;
							}
							String name = part.getName();
							WTContainer container = part.getContainer();
							Folder folder = FolderHelper.getFolder(part);
							String path = folder.getLocation()
									+ ECADConst.ECADLOCATION;
							Folder ecadFolder = FolderHelper.service.getFolder(
									path, part.getContainerReference());
							String creator = part.getCreatorName();
							SessionHelper.manager.setPrincipal(creator);
							EPMUtil.createPCB(number, name, container,
									ecadFolder);
						}
					}
				}
			}
		}
	}

	/**
	 * 电子物料启动元器件建库流程，other+PCB直接发布cadence
	 * 
	 * @param pbo
	 * @throws Exception
	 */
	public static void checkPartStartWorkflow(WTObject pbo) throws Exception {
		List other = InitSystemConfigContant.init().getInitSystemNodeOther();
		Map<String, List<String>> map = HistoryUtils.getClfNumber();
		List<String> listPCB = map.get("PCB");

		if (pbo instanceof PromotionNotice) {
			PromotionNotice pn = (PromotionNotice) pbo;
			if (pn != null) {
				QueryResult qr = MaturityHelper.service.getPromotionTargets(pn);
				boolean flag = false;
				while (qr.hasMoreElements()) {
					Object obj = qr.nextElement();
					if (obj instanceof WTPart) {
						WTPart part = (WTPart) obj;
						if (ECADutil.isElectronicPart(part)) {
							flag = true;
						}
						LWCStructEnumAttTemplate lwc = NodeUtil
								.getLWCStructEnumAttTemplateByPart(part);
						if (lwc == null) {
							continue;
						}
						String pnumber = lwc.getName();
						if (listPCB.contains(pnumber)) {
							AdminApp.addPartAttribute(part);
						} /*else if (other.contains(pnumber)) {
							AdminApp.addPartAttribute(part);
						}*/
						if (flag) {
							boolean a = StartWFFilter
									.terminateWorkProcess(part);
							if (!a) {
								throw new WTException("流程已存在！");
							}
							String creator = part.getCreatorName();
							SessionHelper.manager.setPrincipal(creator);
							StartWorkflowFormProcessor.startWorkFlow(
									ECADConst.CREATECOMPONENT_WF, part, null);
						}
					}
				}
			}
		}
	}

	/**
	 * 为流程团队添加ECAD工程师成员
	 * 
	 * @param obj
	 * @param self
	 * @throws WTException
	 */
	public static void setRoleMembers(WTObject obj, Object self)
			throws WTException {
		WfProcess process = WorkflowHelper.getProcess(self);
		WorkflowHelper.clearRoleMembers(process);
		ObjectReference objectReference = null;
		Set<String> set = new HashSet<>();
		set.add(ECADConst.ECAD);
		if (self instanceof ObjectReference) {
			objectReference = (ObjectReference) self;
		}
		if (obj instanceof WTPart) {
			WTPart part = (WTPart) obj;
			WorkflowHelper.initTeamMembersFromProduct(part.getContainer(),
					objectReference, set);
		} else if (obj instanceof EPMDocument) {
			EPMDocument epm = (EPMDocument) obj;
			WorkflowHelper.initTeamMembersFromProduct(epm.getContainer(),
					objectReference, set);
		}
	}

	/**
	 * 设置提交节点用户给修改节点角色
	 * 
	 * @param self
	 * @throws WTException
	 */
	public static void setRoleUpdate(Object self) throws WTException {
		WTPrincipal principal = SessionHelper.manager.getPrincipal();
		WfProcess process = (WfProcess) WorkflowHelper.getProcess(self);
		Team team = (Team) process.getTeamId().getObject();
		TeamHelper.service.addRolePrincipalMap(Role.toRole("SUBMITTER"),
				principal, team);
	}

	/**
	 * 元器件建库流程/PCB设计提交校验参与者
	 * 
	 * @param self
	 * @throws WTException
	 */
	public static void checkRoleMember(WTObject wtobj, Object self)
			throws WTException {
		WTUser user = null;
		List<WTUser> list = null;

		WfProcess process = (WfProcess) WorkflowHelper.getProcess(self);
		WTPrincipal principal = SessionHelper.manager.getPrincipal();

		if (principal instanceof WTUser) {
			user = (WTUser) principal;
		}

		if (wtobj instanceof WTPart) {
			list = WorkflowHelper.getUsers(process,
					Role.toRole(ECADConst.JAODUIZHE));
			if (list == null || list.size() == 0) {
				throw new WTException("请为校对者设置ECAD工程师成员!");
			} else if (list.contains(user)) {
				throw new WTException("校对者成员不能是创建者!");
			}
		} else if (wtobj instanceof EPMDocument) {
			List<WTUser> ecadEngineers = WorkflowHelper.getUsers(process,
					Role.toRole(ECADConst.JAODUIZHE));
			List<WTUser> hardWareEngineers = WorkflowHelper.getUsers(process,
					Role.toRole(ECADConst.HARDWAREENGINEER));
			list = ecadEngineers;
			list.addAll(hardWareEngineers);
			if (ecadEngineers == null || ecadEngineers.size() == 0) {
				throw new WTException("请为校对者设置ECAD工程师成员!");
			} else if (hardWareEngineers == null
					|| hardWareEngineers.size() == 0) {
				throw new WTException("请为硬件工程师设置成员!");
			} else if (list.contains(user)) {
				throw new WTException("校对者和硬件工程师成员不能是创建者!");
			}
		}
	}

	/**
	 * 获取ECAD工程师组
	 * 
	 * @return
	 * @throws WTException
	 */
	public static WTGroup getECADGroup() throws WTException {
		QuerySpec qs = new QuerySpec(WTGroup.class);
		SearchCondition sc = new SearchCondition(WTGroup.class, WTGroup.NAME,
				SearchCondition.EQUAL, ECADConst.ECADGROUP);
		qs.appendWhere(sc);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		System.out.println(qr.size());
		if (qr.hasMoreElements()) {
			return (WTGroup) qr.nextElement();
		}
		return null;
	}
}
