package com.catl.line.validator;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.core.util.StringUtils;

import com.catl.common.util.PropertiesUtil;
import com.catl.ecad.utils.CommonUtil;
import com.catl.ecad.utils.ECADutil;
import com.catl.ecad.utils.WorkflowHelper;
import com.catl.line.constant.ConstantLine;
import com.catl.line.entity.ParentPNAttr;
import com.catl.line.exception.LineException;
import com.catl.line.util.IBAUtil;
import com.catl.line.util.IBAUtility;
import com.catl.line.util.NodeUtil;
import com.ptc.xworks.windchill.util.PromotionNoticeUtils;

import wt.doc.WTDocument;
import wt.fc.ObjectReference;
import wt.fc.WTObject;
import wt.maturity.Promotable;
import wt.maturity.PromotionNotice;
import wt.org.WTPrincipal;
import wt.part.WTPart;
import wt.project.Role;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.workflow.engine.WfProcess;

public class WorkFlowVerfiy {
	/**
	 * 如果线束母PN库中部件走PN申请流程 则升级对象只允许有一个部件
	 * 
	 * @param pbo
	 * @throws WTException
	 */
	public static String verifyIsMPN(WTObject pbo) throws WTException {
		SessionServerHelper.manager.setAccessEnforced(false);
		try{
			if (pbo instanceof PromotionNotice) {
				PromotionNotice pn = (PromotionNotice) pbo;
				Collection<Promotable> objs = PromotionNoticeUtils
						.getPromotionNoticeItems((PromotionNotice) pn);
				Object[] arrayobjs = objs.toArray();
				List<WTPart> parts = new ArrayList<WTPart>();
				for (int i = 0; i < arrayobjs.length; i++) {
					Object obj = arrayobjs[i];
					if (obj instanceof WTPart) {
						WTPart part = (WTPart) obj;
						parts.add(part);
					}
				}
				for (int i = 0; i < parts.size(); i++) {
					if (parts.get(i).getContainerName()
							.equals(ConstantLine.libary_lineparentpn)&&!parts.get(i).getNumber().contains("-M")) {
						throw new LineException("升级对象包含母PN的流程不允许存在其他PN");
					}
				}
			}	
		}catch(Exception e){
			throw e;
		}finally{
			SessionServerHelper.manager.setAccessEnforced(true);
		}
		return ConstantLine.route_mpn;
	}
   
	/**
	 *判断是否母PN
	 * @param pbo
	 * @throws WTException
	 */
	public static boolean IsMPN(WTObject pbo) throws WTException {
		SessionServerHelper.manager.setAccessEnforced(false);
		boolean flag = false;
		try{
		if (pbo instanceof PromotionNotice) {
			PromotionNotice pn = (PromotionNotice) pbo;
			Collection<Promotable> objs = PromotionNoticeUtils
					.getPromotionNoticeItems((PromotionNotice) pn);
			Object[] arrayobjs = objs.toArray();
			List<WTPart> parts = new ArrayList<WTPart>();
			for (int i = 0; i < arrayobjs.length; i++) {
				Object obj = arrayobjs[i];
				if (obj instanceof WTPart) {
					WTPart part = (WTPart) obj;
					parts.add(part);
				}
			}

			for (int i = 0; i < parts.size(); i++) {
				if (parts.get(i).getContainerName()
						.equals(ConstantLine.libary_lineparentpn)) {
					flag = true;
				}
			}
		}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			SessionServerHelper.manager.setAccessEnforced(true);
		}
		return flag;
	}

	/**
	 * 验证母PN属性填写是否规范
	 * 
	 * @param pbo
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws RemoteException
	 */
	public static void verifyParentPNAttr(WTObject pbo) throws WTException,
			WTPropertyVetoException, RemoteException {
		SessionServerHelper.manager.setAccessEnforced(false);
		try{
		StringBuffer msg = new StringBuffer();
		if (pbo instanceof PromotionNotice) {
			PromotionNotice pn = (PromotionNotice) pbo;
			Collection<Promotable> objs = PromotionNoticeUtils
					.getPromotionNoticeItems((PromotionNotice) pn);
			Object[] arrayobjs = objs.toArray();
			List<WTPart> parts = new ArrayList<WTPart>();
			for (int i = 0; i < arrayobjs.length; i++) {
				Object obj = arrayobjs[i];
				if (obj instanceof WTPart) {
					WTPart part = (WTPart) obj;
					parts.add(part);
				}
			}
			for (int i = 0; i < parts.size(); i++) {
				WTPart part = (WTPart) parts.get(i);
				List<ParentPNAttr> attrs = NodeUtil
						.getParentPNStringAttrs(part);
				for (int j = 0; j < attrs.size(); j++) {
					ParentPNAttr attr = attrs.get(j);
					if (attr.isRequired()
							&& StringUtils.isEmpty(attr.getValue().toString())) {
						if(msg.indexOf(part.getNumber())==-1){
							msg.append(part.getNumber()+"属性:"+attr.getDisplayname()).append(",");
						}else{
							msg.append(attr.getDisplayname()).append(",");
						}
						
					}
				}
				IBAUtility ibaUtility = new IBAUtility(part);
				ibaUtility.setIBAValue(ConstantLine.var_parentPN,
						ConstantLine.judgeparentPN);
				ibaUtility.updateAttributeContainer(part);
				ibaUtility.updateIBAHolder(part);
			}
		}
		if (msg.length() != 0) {
			String errormsg = msg.toString();
			errormsg = errormsg.substring(0, errormsg.length() - 1);
			throw new LineException(errormsg + "为必填属性");
		}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			SessionServerHelper.manager.setAccessEnforced(true);
		}
	}

	/**
	 * 验证线束总成的审核是否包含了完整的bom结构 未发布的部件 线束总成的总成的装箱说明是否关联(装箱说明的编码和线束总成的编码一致)
	 * 
	 * @param pbo
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static String verifyLineAsMRule(WTObject pbo) throws WTException,
			WTPropertyVetoException {
		SessionServerHelper.manager.setAccessEnforced(false);
		try{
		StringBuffer msg = new StringBuffer();
		if (pbo instanceof PromotionNotice) {
			PromotionNotice pn = (PromotionNotice) pbo;
			Collection<Promotable> objs = PromotionNoticeUtils
					.getPromotionNoticeItems((PromotionNotice) pn);
			Object[] arrayobjs = objs.toArray();
			List<String> partsnumber = new ArrayList<String>();// 收集的部件
			List<String> docsnumber = new ArrayList<String>();// 收集的文档
			List<String> lineasmnumbers = new ArrayList<String>();
			for (int i = 0; i < arrayobjs.length; i++) {
				Object obj = arrayobjs[i];
				if (obj instanceof WTPart) {
					WTPart part = (WTPart) obj;
					String number = part.getNumber();
					if (number.startsWith(PropertiesUtil.getValueByKey("create_box_explain_group"))) {
						lineasmnumbers.add(number);
					}
					partsnumber.add(number);
				} else if (obj instanceof WTDocument) {
					WTDocument doc = (WTDocument) obj;
					docsnumber.add(doc.getNumber());
				}
			}
			if (lineasmnumbers.size() == 0) {
				return ConstantLine.route_normal;
			}

			for (int i = 0; i < lineasmnumbers.size(); i++) {
				String linenumber = lineasmnumbers.get(i);
				WTPart ppart = CommonUtil.getLatestWTpartByNumber(linenumber);
				boolean flag = false;
				List<WTDocument> documents = ECADutil.getDocByPart(ppart);
				if (documents.size() != 0) {
					for (WTDocument document : documents) {
						if (linenumber.equals(document.getNumber())&& docsnumber.contains(document.getNumber())) {
							flag = true;
						}
					}
				}
				if (!flag) {
					String iscreateboxexplain=IBAUtil.getStringIBAValue(ppart, ConstantLine.var_iscreateboxexplain);
					if(iscreateboxexplain!=null&&iscreateboxexplain.equals("是")){
						msg.append("线束总成" + linenumber + "未关联装箱说明或装箱说明添加进升级列表！");
					}
					
				}
			}
		}
		if (msg.length() != 0) {
			String errormsg = msg.toString();
			throw new LineException(errormsg);
		}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			SessionServerHelper.manager.setAccessEnforced(true);
		}
		return ConstantLine.route_lineasm;
	}

	/**
	 * 母PN提交时校验是否添加标准化工程师
	 * 
	 * @param self
	 * @throws WTException
	 */
	public static void checkRole(WTObject pbo, Object self) throws WTException {
		SessionServerHelper.manager.setAccessEnforced(false);
		try{
		if (self instanceof ObjectReference) {
			List list2 = new ArrayList<>();
			boolean ret = false;

			WfProcess process = (WfProcess) WorkflowHelper.getProcess(self);
			ArrayList list = (ArrayList) WorkflowHelper.getUsers(process,
					Role.toRole("STANDARDIZATION_ENGINEER"));

			list2 = WorkflowHelper.getUsersFromContainer(pbo,
					"STANDARDIZATION_ENGINEER");

			for (int i = 0; i < list.size(); i++) {
				Object object = list.get(i);
				WTPrincipal wtPrincipal = null;
				if (object instanceof WTPrincipal) {
					wtPrincipal = (WTPrincipal) object;
				}

				WTPrincipal wtprincipal2 = null;
				for (int j = 0; j < list2.size(); j++) {
					Object obj = list2.get(j);
					if (obj instanceof WTPrincipal) {
						wtprincipal2 = (WTPrincipal) obj;
					}
					if (wtPrincipal.equals(wtprincipal2)) {
						ret = true;
					}
				}
			}
			if (!ret) {
				throw new WTException("请添加标准化工程师角色成员！");
			}
		}
	}catch(Exception e){
		e.printStackTrace();
		throw e;
	}finally{
		SessionServerHelper.manager.setAccessEnforced(true);
	}
	}

}
