/**
 * Copyright (c) 2017.
 * All rights reserved.
 * Create on 2017年7月21日  9:08:08
 * Author : vive
 */
package com.catl.line.test;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import com.catl.line.util.IBAUtil;

import wt.fc.ObjectIdentifier;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.State;
import wt.maturity.MaturityHelper;
import wt.maturity.PromotionNotice;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.project.Role;
import wt.session.SessionHelper;
import wt.team.Team;
import wt.util.WTException;
import wt.vc.wip.WorkInProgressHelper;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfBlock;
import wt.workflow.engine.WfConnector;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WorkItem;

/**
 * PN申请流程类
 * 
 * @author vive
 *
 */
public class PNApplicationWorkFlow002 implements RemoteAccess {

	public static void main(String[] args) throws Exception {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		rms.setUserName("wcadmin");
		rms.setPassword("wcadmin");
		rms.invoke("WriteMPNparameters", PNApplicationWorkFlow002.class.getName(), null, null, null);
		// boolean b = isMPN();
		// System.out.println("b的值："+b);
	}

	/**
	 * 
	 * 判断是不是一个PN ,是不是母PN 判断升级对象的状态，类型 ，是否检出
	 * 
	 * @throws Exception
	 *             类型和数量不对时抛异常
	 * @return 有且只有一个PN，且是母PN时才返回true。
	 */
	public static boolean isMPN(WTObject obj) throws Exception {
		boolean flag = false;
		String state = "";
		WTPrincipal wtadministrator = null;
		WTUser previous = null;
		try {
			wtadministrator = SessionHelper.manager.getAdministrator();
			previous = (WTUser) SessionHelper.manager.getPrincipal();
			SessionHelper.manager.setPrincipal(wtadministrator.getName());
		} catch (WTException e1) {
			e1.printStackTrace();
			throw new WTException("获取管理员权限时失败...");
		}
		// PromotionNotice 升级请求
		PromotionNotice notice = (PromotionNotice) obj;
		QueryResult result = MaturityHelper.service.getPromotionTargets(notice);// 获取升级对象
		WTPart part = null;
		ArrayList<WTPart> list = new ArrayList<WTPart>();
		while (result.hasMoreElements()) {
			WTObject object = (WTObject) result.nextElement();
			if (object instanceof WTPart) {
				part = (WTPart) object;
				if (WorkInProgressHelper.isCheckedOut(part)) {// 是否检出
					throw new WTException("对象 【" + part.getName() + "】已检出，请修正，并重新提交。");
				}
				state = part.getLifeCycleState().toString();
				if (!state.equalsIgnoreCase(" ")) { // 需完善
					throw new WTException("升级对象是:" + state + "状态,不是**状态");
				}

				list.add(part);
			} else {
				throw new Exception("升级对象不是部件类型！");
			}
		}
		if (list.size() != 1) {
			throw new Exception("升级对象有且只能是一个，请检查确认后重试！");
		} else {
			String parentPN = IBAUtil.getStringIBAValue(part, "parentPN");
			if (parentPN.equalsIgnoreCase("是")) {
				flag = true;
			}
		}

		SessionHelper.manager.setPrincipal(previous.getName());
		return flag;
	}

//	/**
//	 * 
//	 * 判断升级对象的状态，类型，是否检出
//	 * 
//	 * @throws WTException
//	 */
//	public static void checkPromotionTargetState(WTObject obj) throws WTException {
//		String state = "";
//		WTPart part = null;
//		WTPrincipal wtadministrator = null;
//		WTUser previous = null;
//		try {
//			wtadministrator = SessionHelper.manager.getAdministrator();
//			previous = (WTUser) SessionHelper.manager.getPrincipal();
//			SessionHelper.manager.setPrincipal(wtadministrator.getName());
//		} catch (WTException e1) {
//			e1.printStackTrace();
//			throw new WTException("获取管理员权限时失败...");
//		}
//		PromotionNotice notice = (PromotionNotice) obj;
//		QueryResult result = MaturityHelper.service.getPromotionTargets(notice);// 获取升级对象
//		while (result.hasMoreElements()) {
//			part = (WTPart) result.nextElement();
//			if (part != null) {
//				if (WorkInProgressHelper.isCheckedOut(part)) {
//					throw new WTException("对象 【" + part.getName() + "】已检出，请修正，并重新提交。");
//				}
//				state = part.getLifeCycleState().toString();
//				if (!state.equalsIgnoreCase(" ")) { // 需完善
//					throw new WTException("升级对象是:" + state + "状态,不是**状态");
//				}
//			}
//		}
//		SessionHelper.manager.setPrincipal(previous.getName());
//	}

	/**
	 * 设置升级对象的生命周期状态
	 * 
	 * @param obj
	 *            升级请求
	 * @param state
	 *            状态
	 * @throws Exception
	 * @throws WTException
	 */
	public static void setPNLifeCycleState(WTObject obj, String state) throws Exception, WTException {
		WTPrincipal wtadministrator = null;
		WTUser previous = null;
		try {
			wtadministrator = SessionHelper.manager.getAdministrator();
			previous = (WTUser) SessionHelper.manager.getPrincipal();
			SessionHelper.manager.setPrincipal(wtadministrator.getName());
		} catch (WTException e1) {
			e1.printStackTrace();
			throw new WTException("获取管理员权限时失败...");
		}
		PromotionNotice notice = (PromotionNotice) obj;
		QueryResult result = MaturityHelper.service.getPromotionTargets(notice);// 获取升级对象
		WTPart part = null;
		while (result.hasMoreElements()) {
			WTObject object = (WTObject) result.nextElement();
			if (object instanceof WTPart) {
				part = (WTPart) object;
				LifeCycleHelper.service.setLifeCycleState(part, State.toState(state));
			}
		}
		SessionHelper.manager.setPrincipal(previous.getName());
	}

	/**
	 * 检查参与者信息是否完整
	 * 
	 * @param self
	 * @param primaryBusinessObject
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings("rawtypes")
	public static void CheckPNRole(ObjectReference self, WTObject primaryBusinessObject) throws WTException {
		WTPrincipal wtadministrator = SessionHelper.manager.getAdministrator();
		WTUser previous = (WTUser) SessionHelper.manager.getPrincipal();
		SessionHelper.manager.setPrincipal(wtadministrator.getName());
		Role role = null;
		WfProcess process = getProcess(self);
		Team team = (Team) process.getTeamId().getObject();
		Vector vector = process.getProcessRoles();
		for (int i = 0; i < vector.size(); i++) {
			role = (Role) vector.get(i);
			if (role.getStringValue().equals("wt.project.Role.CJECR")) { // 需要修改
																			// 母PN参数填写
				Enumeration enumeration = team.getPrincipalTarget(role);
				if (!enumeration.hasMoreElements()) {
					SessionHelper.manager.setPrincipal(previous.getName());
					throw new WTException("\"母PN参数填写\"参与者信息不完整,请重新\"设置参与者\"");
				}
			}
			if (role.getStringValue().equals("wt.project.Role.CJECR")) { // 需要修改
																			// 标准化工程师审核
				Enumeration enumeration = team.getPrincipalTarget(role);
				if (!enumeration.hasMoreElements()) {
					SessionHelper.manager.setPrincipal(previous.getName());
					throw new WTException("\"标准化工程师\"审核参与者信息不完整,请重新\"设置参与者\"");
				}
			}
			if (role.getStringValue().equals("wt.project.Role.CJECR")) { // 需要修改
																			// PDM审核
				Enumeration enumeration = team.getPrincipalTarget(role);
				if (!enumeration.hasMoreElements()) {
					SessionHelper.manager.setPrincipal(previous.getName());
					throw new WTException("\"PDM审核\"参与者信息不完整,请重新\"设置参与者\"");
				}
			}
			if (role.getStringValue().equals("wt.project.Role.CJECR")) { // 需要修改
																			// FM审核
				Enumeration enumeration = team.getPrincipalTarget(role);
				if (!enumeration.hasMoreElements()) {
					SessionHelper.manager.setPrincipal(previous.getName());
					throw new WTException("\"FM审核\"参与者信息不完整,请重新\"设置参与者\"");
				}
			}
		}
		SessionHelper.manager.setPrincipal(previous.getName());
	}

	/**
	 * 获取工作流进程对象
	 * 
	 * @param obj
	 * @return
	 */
	public static WfProcess getProcess(Object obj) {
		if (obj == null)
			return null;
		try {
			Persistable persistable = null;

			if (obj instanceof ObjectIdentifier)
				persistable = PersistenceHelper.manager.refresh((ObjectIdentifier) obj);
			else if (obj instanceof ObjectReference)
				persistable = ((ObjectReference) obj).getObject();

			if (persistable instanceof WorkItem)
				persistable = ((WorkItem) persistable).getSource().getObject();

			if (persistable instanceof WfActivity)
				persistable = ((WfActivity) persistable).getParentProcess();

			if (persistable instanceof WfConnector)
				persistable = ((WfConnector) persistable).getParentProcessRef().getObject();

			if (persistable instanceof WfBlock)
				persistable = ((WfBlock) persistable).getParentProcess();

			if (persistable instanceof WfProcess)
				return (WfProcess) persistable;
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
