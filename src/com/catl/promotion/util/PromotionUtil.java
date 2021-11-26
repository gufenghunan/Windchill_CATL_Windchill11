package com.catl.promotion.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import wt.change2.ChangeActivity2;
import wt.change2.ChangeException2;
import wt.change2.ChangeHelper2;
import wt.change2.ChangeRecord2;
import wt.change2.Changeable2;
import wt.change2.RelevantRequestData2;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.doc.WTDocument;
import wt.enterprise.Master;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.fc.ObjectReference;
import wt.fc.ObjectVector;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.WTReference;
import wt.fc.collections.WTCollection;
import wt.fc.collections.WTHashSet;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.maturity.MaturityException;
import wt.maturity.MaturityHelper;
import wt.maturity.PromotionNotice;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartSubstituteLink;
import wt.part.WTPartUsageLink;
import wt.pds.StatementSpec;
import wt.project.Role;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionServerHelper;
import wt.team.WTRoleHolder2;
import wt.util.WTException;
import wt.util.WTMessage;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;
import wt.vc.struct.StructHelper;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfState;

import com.catl.bom.workflow.BomWfUtil;
import com.catl.change.ChangeUtil;
import com.catl.common.constant.ChangeState;
import com.catl.common.constant.PartState;
import com.catl.common.constant.TypeName;
import com.catl.common.util.BOMUtil;
import com.catl.common.util.IBAUtil;
import com.catl.doc.maturityUpReport.MaturityUpReportHelper;
import com.catl.part.PartConstant;
import com.catl.part.classification.RefreshFAEStatusUtil;
import com.ptc.core.components.util.OidHelper;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmClipboardBean;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.workflow.NmWorkflowHelper;
import com.ptc.windchill.enterprise.maturity.commands.PromotionItemQueryCommands;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;
import com.ptc.windchill.enterprise.team.server.TeamCCHelper;

public class PromotionUtil {

	private static Logger log = Logger.getLogger(PromotionUtil.class.getName());

	public static StringBuffer checkReviewObjecs(PromotionNotice pn) throws MaturityException, WTException {

		StringBuffer message = new StringBuffer();
		QueryResult promotables = null;

		promotables = MaturityHelper.service.getBaselineItems(pn);
		int i = 1;
		while (promotables.hasMoreElements()) {
			Object obj = promotables.nextElement();
			if (obj instanceof WTPart) {
				WTPart part = (WTPart) obj;
				WTPartMaster master = (WTPartMaster) part.getMaster();
				WTPart latestBigPart = (WTPart) getLatestVersionByMaster(master);
				String oldVersion = part.getVersionIdentifier().getValue();
				String latesVersion = latestBigPart.getVersionIdentifier().getValue();

				TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(part);
				String type = ti.getTypename();
				log.debug("part type==" + type);

				Set<WTPart> allTopParents = new HashSet<WTPart>();
				Set<WTPart> temp = new HashSet<WTPart>();
				getPartTopParentObject(part, allTopParents);
				for (WTPart Tpart : allTopParents) {
					WTPartMaster maste = (WTPartMaster) Tpart.getMaster();
					WTPart latestPart = (WTPart) getLatestVersionByMaster(maste);
					if (Tpart.equals(latestPart)) {
						if (!Tpart.getState().toString().endsWith(PartState.DISABLEDFORDESIGN)) {
							temp.add(Tpart);
						}
					}
				}

				if (!type.endsWith(TypeName.CATLPart)) {
					message.append(i++).append(". ").append(part.getNumber() + ",对象不是“零部件”，不能添加到物料设计禁用流程中！\n");
				}
				if (!(part.getState().toString().endsWith(PartState.RELEASED)||part.getState().toString().endsWith(PartState.DESIGN))) {
					message.append(i++).append(". ").append(part.getNumber() + ",对象不是“已发布”状态，不能添加到物料设计禁用流程中！\n");
				}
				if (!oldVersion.equals(latesVersion)) {
					message.append(i++).append(". ").append(part.getNumber() + ",对象不是“最后的大版本”，不能添加到物料设计禁用流程中！\n");
				}
				/** 在创建的设计禁用单的时候，不需要检查有效父件
				if (temp.size() > 0) {
					message.append(i++).append(". ").append(part.getNumber() + ",对象存在“有效父件”，不能添加到物料设计禁用流程中！\n");
				}
				*/
				if (isexsitPromotion(part) > 0) {
					message.append(i++).append(". ").append(part.getNumber() + ",对象存在其他未完成（物料设计禁用单没有被取消或完成）设计禁用流程中，不能启动设计禁用流程！\n");
				}
				if (isECUndone(part)) {
					message.append(i++).append(". ").append(part.getNumber() + ",对象“被加入未完成 的变更（ECR，ECN，DCN）的受影响对象中”，不能启动设计禁用流程！ \n");
				}
				if (isSourceChangeUndone(part,pn) > 0) {
					message.append(i++).append(". ").append(part.getNumber() + ",对象正在采购类型更改单中，不能启动设计禁用流程！ \n");
				}
				
				if (isPlatformChangeUndone(part,pn) > 0) {
					message.append(i++).append(". ").append(part.getNumber() + ",对象正在产品线标识更改单中，不能启动设计禁用流程！ \n");
				}
				String faestate = (String)IBAUtil.getIBAValue(part, PartConstant.IBA_CATL_FAEStatus);
				if(PartConstant.CATL_FAEStatus_3.equals(faestate)){
					message.append(i++).append(". ").append(part.getNumber() + ",对象正在FAE流程单中，不能启动设计禁用流程！ \n");
				}
				if(MaturityUpReportHelper.isNFAEUndone(part)){
					message.append(i++).append(". ").append(part.getNumber() + ",对象正在非FAE流程单中，不能启动设计禁用流程！ \n");
				}
				/** 在创建的设计禁用单的时候，不需要检查替代件的父件
				if (isSubstituteParts(part)) {
					Set<WTPart> parents = getSubstitutePartsParent(part);
					if (parents.size() > 0) {
						for (WTPart parent : parents) {
							WTPart latestParent = (WTPart) getLatestVersionByMaster(master);
							if (!parent.getState().toString().endsWith(PartState.DISABLEDFORDESIGN)) {
								Set<WTPart> RelatedObjects = BomWfUtil.getTargets(pn);
								if (!parent.equals(latestParent) && !RelatedObjects.contains(parent)) {
									message.append(i++).append(". ")
											.append(part.getNumber() + ",对象是“替换件”，其有效父件" + parent.getNumber() + "必须也在该数据列表中；否则不能启动设计禁用流程！ \n");
								}
							}
						}
					}
				}
				*/

			} else {
				String number = BomWfUtil.getObjectnumber((Persistable) obj);
				message.append(i++).append(". ").append(number + ",对象不是“零部件”，不能添加到物料设计禁用流程中！\n");
			}
		}
		return message;
	}

	public static StringBuffer checkFAERelatedObjecs(PromotionNotice pn) throws MaturityException, WTException {

		StringBuffer message = new StringBuffer();
		QueryResult promotables = null;

		promotables = MaturityHelper.service.getBaselineItems(pn);
		int i = 1;
		while (promotables.hasMoreElements()) {
			Object obj = promotables.nextElement();
			if (obj instanceof WTPart) {
				WTPart part = (WTPart) obj;
				WTPartMaster master = (WTPartMaster) part.getMaster();
				WTPart latestBigPart = (WTPart) getLatestVersionByMaster(master);
				TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(part);
				String type = ti.getTypename();
				log.debug("part type==" + type);
				String FAEStatus = (String) IBAUtil.getIBAValue(master, PartConstant.IBA_CATL_FAEStatus);
				String maturity = (String) IBAUtil.getIBAValue(master, PartConstant.IBA_CATL_Maturity);
				

				if (!type.endsWith(TypeName.CATLPart)) {
					message.append(i++).append(". ").append(part.getNumber() + ",对象不是“零部件”，只有零部件才允许加入FAE物料成熟度升级单！\n");
				}
				if (!part.getState().toString().endsWith(PartState.RELEASED)) {
					message.append(i++).append(". ").append(part.getNumber() + ",对象不是“已发布”状态，不能添加到FAE物料成熟度升级单！\n");
				}
				if (!part.equals(latestBigPart)) {
					message.append(i++).append(". ").append(part.getNumber() + ",对象不是最新版本，不能添加到FAE物料成熟度升级单！\n");
				}
				if (isECRADUndone(part)) {
					message.append(i++).append(". ").append(part.getNumber() + ",对象正在变更中，不能启动FAE物料成熟度升级单！ \n");
				}
				if(PromotionUtil.isSourceChangeUndone(part, null) > 0){
					message.append(i++).append(". ").append(part.getNumber() + ",对象正在采购类型更改单中，不能启动FAE物料成熟度升级单！ \n");
				}
				if(PromotionUtil.isPlatformChangeUndone(part,null) > 0){
					message.append(i++).append(". ").append(part.getNumber() + ",对象正在产品线标识更改单中，不能启动FAE物料成熟度升级单！ \n");
				}
				if(PromotionUtil.isexsitPromotion(part) > 0){
					message.append(i++).append(". ").append(part.getNumber() + ",对象正在设计禁用单中，不能启动FAE物料成熟度升级单！ \n");
				}
				if (FAEStatus == null) {
					message.append(i++).append(". ").append("零部件" + part.getNumber() + "的“FAE状态”必须是“未发起”！ \n");
				} else if (!FAEStatus.equals(PartConstant.CATL_FAEStatus_2)) {
					message.append(i++).append(". ").append("零部件" + part.getNumber() + "的“FAE状态”必须是“未发起”！ \n");
				}
				if (maturity == null) {
					message.append(i++).append(". ").append("零部件" + part.getNumber() + "的“成熟度”必须为“1”！ \n");
				} else if (!StringUtils.equals(maturity, "1")) {
					message.append(i++).append(". ").append("零部件" + part.getNumber() + "的“成熟度”必须为“1”！ \n");
				}
				
				Map<WTPart,Set<WTPart>> map = getOneLevelChild(latestBigPart);
				
				for(WTPart child : map.keySet()){
					WTPartMaster childMaster = (WTPartMaster) child.getMaster();
					String childMaturity = (String) IBAUtil.getIBAValue(childMaster, PartConstant.IBA_CATL_Maturity);
					if (childMaturity == null) {
						message.append(i++).append(". ").append("直接下层子件" + child.getNumber() + "的“成熟度”必须为“3”或者“6”！ \n");
					} else if (!(StringUtils.equals(childMaturity, "3") || StringUtils.equals(childMaturity, "6"))) {
						message.append(i++).append(". ").append("直接下层子件" + child.getNumber() + "的“成熟度”必须为“3”或者“6”！ \n");
					}
					for(WTPart substitute : map.get(child)){
						WTPartMaster substituteMaster = (WTPartMaster) substitute.getMaster();
						String substituteMaturity = (String) IBAUtil.getIBAValue(substituteMaster, PartConstant.IBA_CATL_Maturity);
//						if (substituteMaturity == null) {
//							message.append(i++).append(". ").append("直接下层子件" + child.getNumber() + "的“成熟度”必须为“3”或者“6”！ \n");
//						} else if (!(StringUtils.equals(childMaturity, "3") || StringUtils.equals(childMaturity, "6"))) {
//							message.append(i++).append(". ").append("直接下层子件" + child.getNumber() + "的“成熟度”必须为“3”或者“6”！ \n");
//						}
						if(substituteMaturity == null || !(StringUtils.equals(substituteMaturity, "3") || StringUtils.equals(substituteMaturity, "6"))){
							message.append(WTMessage.formatLocalizedMessage("{0}. 直接下层子件{1}的替代件{2}的“成熟度”必须为“3”或者“6”！ \n", new Object[]{i++,child.getNumber(),substitute.getNumber()}));
						}
					}
				}
				
			} else {
				String number = BomWfUtil.getObjectnumber((Persistable) obj);
				message.append(i++).append(". ").append(number + ",对象不是“零部件”，只有零部件才允许加入FAE物料成熟度升级单！\n");
			}
		}
		return message;
	}

	public static Persistable getLatestBigVersion(RevisionControlled iteration) throws WTException {

		Persistable p = VersionControlHelper.getLatestIteration(iteration, false);
		return p;
	}

	public static Persistable getLatestBigVersionByOID(String oid) throws WTException {
		RevisionControlled iteration = (RevisionControlled) getObjectByOid(oid);
		Persistable p = VersionControlHelper.getLatestIteration(iteration, false);
		return p;
	}

	public static Persistable getObjectByOid(String oid) throws WTException {
		Persistable p = null;

		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			ReferenceFactory referencefactory = new ReferenceFactory();
			WTReference wtreference = referencefactory.getReference(oid);
			p = wtreference.getObject();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(flag);
		}

		return p;
	}

	/**
	 * 获取最新大版本的最新小版本
	 * 
	 * @param master
	 * @return
	 */
	public static Persistable getLatestVersionByMaster(Master master) {
		try {
			if (master != null) {
				QueryResult qrVersions = VersionControlHelper.service.allVersionsOf(master);
				if (qrVersions.hasMoreElements()) {
					Persistable p = (Persistable) qrVersions.nextElement();
					return p;
				}
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取最新大版本的所有小版本
	 * 
	 * @param master
	 * @return
	 */
	public static Set<Persistable> getAllLatestVersionByMaster(Master master) {

		Set<Persistable> persistable = new HashSet<Persistable>();
		try {
			if (master != null) {
				QueryResult qrVersions = VersionControlHelper.service.allIterationsOf(master);
				while (qrVersions.hasMoreElements()) {
					Persistable p = (Persistable) qrVersions.nextElement();
					persistable.add(p);
				}
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return persistable;
	}

	/**
	 * 获取上层父件
	 * 
	 * @param child
	 * @param allTopParents
	 * @throws WTException
	 */
	public static void getPartTopParentObject(WTPart child, Set<WTPart> TopParents) throws WTException {
		QueryResult links = StructHelper.service.navigateUsedBy(child.getMaster(), WTPartUsageLink.class, true);
		while (links.hasMoreElements()) {
			Persistable p = (Persistable) links.nextElement();
			if (p instanceof WTPart) {
				WTPart part = (WTPart) p;
				TopParents.add(part);
			}
		}
	}

	/**
	 * 获取Part直接下层子件/第一层子件,不包含局部替换件
	 * 
	 * @param parent
	 * @return
	 * @throws WTException
	 */
	public static Map<WTPart,Set<WTPart>> getOneLevelChild(WTPart parent) throws WTException {

//		Set<WTPart> childPart = new HashSet<WTPart>();
		Map<WTPart,Set<WTPart>> map = new HashMap<WTPart,Set<WTPart>>();
		try {
			QueryResult qr = WTPartHelper.service.getUsesWTPartMasters(parent);
			while (qr.hasMoreElements()) {
				WTPartUsageLink link = (WTPartUsageLink) qr.nextElement();
				WTPartMaster masterChild = link.getUses();
				WTPart part = (WTPart) PromotionUtil.getLatestVersionByMaster(masterChild);
//				if (!isSubstituteParts(part)) {
//					childPart.add(part);
//				}
				map.put(part, BOMUtil.getSubstitutes(link));
//				childPart.add(part);
//				childPart.addAll(BOMUtil.getSubstitutes(link));
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new WTException(e);
		}
//		return childPart;
		return map;
	}

	
	/**
	 * 获取Part直接下层Link关系
	 * 
	 * @param parent
	 * @return
	 * @throws WTException
	 */
	public static Set<WTPartUsageLink> getOneLevelChildLink(WTPart parent) throws WTException {

		Set<WTPartUsageLink> links = new HashSet<WTPartUsageLink>();
		try {
			QueryResult qr = WTPartHelper.service.getUsesWTPartMasters(parent);
			while (qr.hasMoreElements()) {
				WTPartUsageLink link = (WTPartUsageLink) qr.nextElement();
				links.add(link);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new WTException(e);
		}
		return links;
	}
	
	public static int isexsitPromotion(WTObject object) throws WTException {
		QueryResult prcounts = BomWfUtil.isHavePromoteRequest(object);
		HashSet<String> pstateHashSet = new HashSet<String>();
		// check process state ,if has excuted
		while (prcounts.hasMoreElements()) {
			Object[] object2 = (Object[]) prcounts.nextElement();
			PromotionNotice promotion = (PromotionNotice) object2[0];
			QueryResult processResult = new QueryResult();

			TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(promotion);
			String type = ti.getTypename();
			if (type.endsWith(TypeName.designPromotion)) {
				try {
					processResult = NmWorkflowHelper.service.getAssociatedProcesses(promotion, null, null);
					log.debug("processResult size====" + processResult.size());
				} catch (WTException e1) {
					log.debug("getAssociatedProcesses failed----!");
					e1.printStackTrace();
				}
			}

			if (processResult.hasMoreElements()) {
				WfProcess process = (WfProcess) processResult.nextElement();
				log.debug("process.getState().getDisplay()" + process.getState().getDisplay());
				if (process.getState().toString().endsWith("OPEN_RUNNING")) {
					pstateHashSet.add(promotion.getNumber());
				}
			}
		}
		//log.debug("pstateHashSet.size() ==" + pstateHashSet.size());
		return pstateHashSet.size();
	}

	/**
	 * 是否存在于状态为“开启”“正在审阅”“实施”“失败”的“物料设计禁用单中
	 * 
	 * @param object
	 * @return
	 * @throws WTException
	 */
	public static Set<String> isExsitPromotion(WTObject object) throws WTException {
		QueryResult prcounts = BomWfUtil.isHavePromoteRequest(object);
		Set<String> promotionHashSet = new HashSet<String>();

		while (prcounts.hasMoreElements()) {
			Object[] object2 = (Object[]) prcounts.nextElement();
			PromotionNotice promotion = (PromotionNotice) object2[0];
			QueryResult processResult = new QueryResult();

			TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(promotion);
			String type = ti.getTypename();
			if (type.endsWith(TypeName.designPromotion)
					&& (promotion.getState().toString().endsWith(PartState.OPEN) || promotion.getState().toString().endsWith(PartState.OPEN)
							|| promotion.getState().toString().endsWith(PartState.UNDERREVIEW)
							|| promotion.getState().toString().endsWith(PartState.IMPLEMENTATION) || promotion.getState().toString().endsWith(PartState.FAILED))) {
				try {
					processResult = NmWorkflowHelper.service.getAssociatedProcesses(promotion, null, null);
					log.debug("processResult size====" + processResult.size());
				} catch (WTException e1) {
					log.debug("getAssociatedProcesses failed----!");
					e1.printStackTrace();
				}
			}

			if (processResult.hasMoreElements()) {
				WfProcess process = (WfProcess) processResult.nextElement();
				log.debug("process.getState().getDisplay()" + process.getState().getDisplay());
				if (process.getState().toString().endsWith("OPEN_RUNNING")) {
					promotionHashSet.add(promotion.getNumber());
				}
			}
		}
		//log.debug("pstateHashSet.size() ==" + promotionHashSet.size());
		return promotionHashSet;
	}

	/**
	 * GET ECN,DCN(ECO)
	 * 
	 * @param Persiser
	 * @return
	 * @throws Exception
	 */
	public static Set<WTChangeOrder2> getEcaWithPersiser(Persistable per) {
		Set<WTChangeOrder2> eco = new HashSet<WTChangeOrder2>();

		try {
			QuerySpec qs = new QuerySpec(ChangeRecord2.class);
			SearchCondition sc = new SearchCondition(ChangeRecord2.class, "roleBObjectRef.key", SearchCondition.EQUAL, per.getPersistInfo()
					.getObjectIdentifier());
			qs.appendWhere(sc, new int[] { 0 });
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
			while (qr.hasMoreElements()) {
				ChangeRecord2 cr2 = (ChangeRecord2) qr.nextElement();
				WTChangeActivity2 eca = (WTChangeActivity2) cr2.getChangeActivity2();
				QueryResult qc = ChangeHelper2.service.getChangeOrder(eca);
				if (qc.hasMoreElements()) {
					eco.add((WTChangeOrder2) qc.nextElement());
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}

		return eco;
	}

	/**
	 * 对应流程是否有
	 */
	public static boolean isWorlkFlows(WTChangeOrder2 ec) {
		boolean flag = false;
		try {

			QueryResult qr = WfEngineHelper.service.getAssociatedProcesses(ec, WfState.OPEN_RUNNING, null);
			if (qr.hasMoreElements()) {
				flag = true;
			}
		} catch (WTException e) {
			e.printStackTrace();
		}

		return flag;
	}

	/**
	 * 部件被加入的未完成变更（ECR,ECA）的受影响对象中
	 */
	public static Boolean isECUndone(Persistable persistable) {
		Boolean isecr = false;
		try {
			QueryResult qr = PersistenceHelper.manager.navigate(persistable, RelevantRequestData2.CHANGE_REQUEST2_ROLE, RelevantRequestData2.class, true);
			while (qr.hasMoreElements()) {
				WTChangeRequest2 cr = (WTChangeRequest2) qr.nextElement();
				if (!cr.getState().toString().equalsIgnoreCase(ChangeState.RESOLVED) && !cr.getState().toString().equalsIgnoreCase(ChangeState.CANCELLED)) {
					isecr = true;
				}
			}

			if (isEcaUndone(persistable)) {
				isecr = true;
			}
		} catch (ChangeException2 e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return isecr;
	}

	/**
	 * 部件被加入的未完成变更（ECR,ECA,DCA）的受影响对象中
	 */
	public static Boolean isECRADUndone(Persistable persistable) {
		Boolean isecr = false;
		try {
			QueryResult qr = PersistenceHelper.manager.navigate(persistable, RelevantRequestData2.CHANGE_REQUEST2_ROLE, RelevantRequestData2.class, true);
			while (qr.hasMoreElements()) {
				WTChangeRequest2 cr = (WTChangeRequest2) qr.nextElement();
				if (!cr.getState().toString().equalsIgnoreCase(ChangeState.RESOLVED) && !cr.getState().toString().equalsIgnoreCase(ChangeState.CANCELLED)) {
					isecr = true;
				}
			}
			
//			ChangeActivity2 eca = ChangeUtil.getEcaWithPersiser(persistable);
//			if (eca != null) {
//				if (!eca.getState().toString().equalsIgnoreCase(ChangeState.RESOLVED) && !eca.getState().toString().equalsIgnoreCase(ChangeState.CANCELLED)) {
//					isecr = true;
//				}
//			}
			
			if(persistable instanceof Changeable2){
				QueryResult ecaResult = ChangeHelper2.service.getAffectingChangeActivities((Changeable2)persistable);
				while(ecaResult.hasMoreElements()){
					ChangeActivity2 eca = (ChangeActivity2)ecaResult.nextElement();
					if (!eca.getState().toString().equalsIgnoreCase(ChangeState.RESOLVED) && !eca.getState().toString().equalsIgnoreCase(ChangeState.CANCELLED)) {
						return true;
					}
				}
			}
		} catch (ChangeException2 e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return isecr;
	}
	
	/**
	 * 部件是否被加入到未完成采购类型变更流程（SourceChange）的受影响对象中
	 */
	public static int isSourceChangeUndone(WTObject object,PromotionNotice elsePN) throws WTException {
		QueryResult prcounts = BomWfUtil.isHavePromoteRequest(object);
		HashSet<String> pstateHashSet = new HashSet<String>();
		// check process state ,if has excuted
		while (prcounts.hasMoreElements()) {
			Object[] object2 = (Object[]) prcounts.nextElement();
			PromotionNotice promotion = (PromotionNotice) object2[0];
			QueryResult processResult = new QueryResult();

			TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(promotion);
			String type = ti.getTypename();
			if (type.endsWith(TypeName.SourceChangePN) && (elsePN == null || !promotion.equals(elsePN)) ) {
				try {
					processResult = NmWorkflowHelper.service.getAssociatedProcesses(promotion, null, null);
					log.debug("SourceChange processResult size====" + processResult.size());
				} catch (WTException e1) {
					log.debug("SourceChange getAssociatedProcesses failed----!");
					e1.printStackTrace();
				}
			}

			if (processResult.hasMoreElements()) {
				WfProcess process = (WfProcess) processResult.nextElement();
				log.debug("SourceChange process.getState().getDisplay()" + process.getState().getDisplay());
				if (process.getState().toString().endsWith("OPEN_RUNNING")) {
					pstateHashSet.add(promotion.getNumber());
				}
			}
		}
		log.debug("SourceChange pstateHashSet.size() ==" + pstateHashSet.size());
		return pstateHashSet.size();
	}
	
	/**
	 * 部件没有被加入的未完成变更（ECA）的受影响对象中
	 * @throws WTException 
	 */
	public static Boolean isEcaUndone(Persistable persistable) throws WTException {
		Boolean iseca = false;
//		ChangeActivity2 eca = ChangeUtil.getEcaWithPersiser(persistable);
//		if (eca != null) {
//			String ecaType = ChangeUtil.getStrSplit(eca);
//			if (ecaType.equals(ChangeConst.CHANGETASK_TYPE_ECA)) {
//				if (!eca.getState().toString().equalsIgnoreCase(ChangeState.RESOLVED) && !eca.getState().toString().equalsIgnoreCase(ChangeState.CANCELLED)) {
//					iseca = true;
//				}
//			}
//		}
		if(persistable instanceof Changeable2){
			QueryResult ecaResult = ChangeHelper2.service.getAffectingChangeActivities((Changeable2)persistable);
			while(ecaResult.hasMoreElements()){
				ChangeActivity2 eca = (ChangeActivity2)ecaResult.nextElement();
				if (!eca.getState().toString().equalsIgnoreCase(ChangeState.RESOLVED) && !eca.getState().toString().equalsIgnoreCase(ChangeState.CANCELLED)) {
					return true;
				}
			}
		}
		return iseca;
	}

	/**
	 * 部件没有被加入的未完成变更（ECA,DCA）的受影响对象中
	 * @throws WTException 
	 */
	public static Boolean isEcaAndDcaUndone(Persistable persistable) throws WTException {
		Boolean isAll = false;
		ChangeActivity2 eca = ChangeUtil.getEcaWithPersiser(persistable);
		if(eca!=null){
//			if (!eca.getState().toString().equalsIgnoreCase(ChangeState.RESOLVED) && !eca.getState().toString().equalsIgnoreCase(ChangeState.CANCELLED)) {
//				isAll = true;
//			}
			isAll = true;
		}
		return isAll;
	}

	/**
	 * 根据替代件反查被替代部件的父件
	 */
	@SuppressWarnings("rawtypes")
	public static Set<WTPart> getSubstitutePartsParent(WTPart part) {

		Set<WTPart> parts = new HashSet<WTPart>();
		try {
			ObjectVector ov = new ObjectVector();
			WTHashSet set = new WTHashSet();
			set.add(part);
			WTCollection collection = WTPartHelper.service.getSubstituteForLinks((WTPartMaster) part.getMaster());
			if (!collection.isEmpty()) {
				Iterator itr = collection.iterator();
				while (itr.hasNext()) {
					ObjectReference objReference = (ObjectReference) itr.next();
					WTPartSubstituteLink subLink = (WTPartSubstituteLink) objReference.getObject();
					WTPartUsageLink ULink = (WTPartUsageLink) subLink.getRoleAObject();
					Persistable p = (Persistable) ULink.getRoleAObject();
					WTPart par = (WTPart) p;
					ov.addElement(par);
				}
			}

			QueryResult qr = new QueryResult(ov);
			qr = (new LatestConfigSpec()).process(qr);
			while (qr.hasMoreElements()) {
				WTPart par = (WTPart) qr.nextElement();
				parts.add(par);
			}

		} catch (WTException e) {
			e.printStackTrace();
		}
		return parts;
	}

	/**
	 * 返回值Map(Z,(X,Y))
	 * 
	 * @param part
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Map<WTPart, Map<WTPart, WTPartMaster>> getReplacePieceBom(WTPart part) {
		Map<WTPart, Map<WTPart, WTPartMaster>> map = new HashMap<WTPart, Map<WTPart, WTPartMaster>>();

		try {
			WTCollection collection = WTPartHelper.service.getSubstituteForLinks((WTPartMaster) part.getMaster());
			if (!collection.isEmpty()) {
				Iterator itr = collection.iterator();
				while (itr.hasNext()) {
					Map<WTPart, WTPartMaster> temp = new HashMap<WTPart, WTPartMaster>();
					ObjectReference objReference = (ObjectReference) itr.next();
					WTPartSubstituteLink subLink = (WTPartSubstituteLink) objReference.getObject();
					WTPartUsageLink ULink = (WTPartUsageLink) subLink.getRoleAObject();
					WTPart p = (WTPart) ULink.getRoleAObject();
					WTPart lastPart = (WTPart) getLatestVersionByMaster((WTPartMaster) p.getMaster());
					temp.put(part, (WTPartMaster) ULink.getRoleBObject());
					if (p.equals(lastPart)) {
						map.put(p, temp);
					}
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}

		return map;
	}

	/**
	 * 获取Part的所有最顶层父件
	 * 
	 * @param child
	 * @param allTopParents
	 * @throws WTException
	 */
	public static void getAllPartTopParent(WTPart child, Set<WTPart> allTopParents) throws WTException {
		QueryResult links = StructHelper.service.navigateUsedBy(child.getMaster(), WTPartUsageLink.class, true);
		while (links.hasMoreElements()) {
			Persistable p = (Persistable) links.nextElement();
			if (p instanceof WTPart) {
				WTPart parent = (WTPart) p;
				WTPart lastPart = (WTPart) getLatestVersionByMaster((WTPartMaster) parent.getMaster());
				if (parent.equals(lastPart)) {
					getAllPartTopParent(parent, allTopParents);
				}
			}
		}
		if (links.size() == 0)
			allTopParents.add(child);
	}

	/**
	 * 判断部件是否替代件
	 */
	public static Boolean isSubstituteParts(WTPart part) {
		Boolean isSubstitute = false;

		try {
			WTHashSet set = new WTHashSet();
			set.add(part);
			WTCollection collection = WTPartHelper.service.getSubstituteForLinks((WTPartMaster) part.getMaster());
			if (collection.size() > 0) {
				isSubstitute = true;
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return isSubstitute;
	}

	/**
	 * 设置对象的状态
	 * 
	 * @param pbo
	 * @param getLifecycle
	 * @param setLifecycle
	 * @throws WTException
	 */
	public static void setLifecycleState(Persistable p, String setLifecycle) throws WTException {
		try {
			State state = State.toState(setLifecycle);
			Workable wa = doCheckIn((Workable) p, "appform auto checkin");
			LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) wa, state);
		} catch (Exception e) {
			e.printStackTrace();
			throw new WTException(e);
		}
	}

	/**
	 * 检查对象是否检入
	 * 
	 * @param workable
	 * @param str
	 * @return
	 * @throws WTException
	 */
	public static Workable doCheckIn(Workable workable, String str) throws WTException {
		Workable newObj = workable;
		try {
			if (WorkInProgressHelper.isCheckedOut(workable))
				newObj = WorkInProgressHelper.service.checkin(workable, str);
		} catch (WTPropertyVetoException e) {
			throw new WTException(e);
		}
		return newObj;
	}

	/**
	 * 判断是否为3D
	 * 
	 * @param epm
	 * @return
	 */
	public static boolean is3DEPM(EPMDocument epm) {
//		boolean flag = false;
//		String cadname = epm.getCADName();
//		String suffix = cadname.substring(cadname.lastIndexOf(".") + 1);
//		if (suffix.toLowerCase().equals("asm") || suffix.toLowerCase().equals("prt")) {
//			flag = true;
//		}
//		return flag;
		if(epm != null){
			String docType = epm.getDocType().toString();
			log.info("==docType:"+docType);
			return (StringUtils.equals(docType, "CADASSEMBLY") || StringUtils.equals(docType, "CADCOMPONENT"));
		}
		return false;
	}

	/**
	 * 通过部件找相关联的文档 这里的文档和部件是说明关系
	 */
	public static List<WTDocument> getAssociatedDescribeDocuments(WTPart part) {

		List<WTDocument> docList = new ArrayList<WTDocument>();
		try {
			QueryResult qr = PartDocServiceCommand.getAssociatedDescribeDocuments(part);
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				if (obj instanceof WTDocument) {
					docList.add((WTDocument) obj);
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return docList;
	}

	/**
	 * 获取选择的oid
	 * 
	 * @param soid
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Set getSelectedOid(List soid) {
		Set oids = new HashSet();
		String ids[];
		for (Iterator i$ = soid.iterator(); i$.hasNext(); oids.add(ids[ids.length - 1])) {
			Object oid = i$.next();
			String str = oid.toString();
			str = str.replace("!*", "");
			ids = str.split("\\$", -1);
		}

		return oids;
	}

	/**
	 * find the group
	 * 
	 * @param groupName
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static WTGroup queryGroup(String groupName) {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		WTGroup group = null;
		try {
			QuerySpec qSpec = new QuerySpec(WTGroup.class);
			SearchCondition sc = new SearchCondition(WTGroup.class, WTGroup.NAME, SearchCondition.EQUAL, groupName);
			qSpec.appendWhere(sc);
			QueryResult qResult = PersistenceHelper.manager.find(qSpec);
			while (qResult.hasMoreElements()) {
				group = (WTGroup) qResult.nextElement();
			}
			return group;
		} catch (WTException e) {
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return null;
	}

	/**
	 * 根据Number查询Part Master
	 * 
	 * @param number
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings("deprecation")
	public static QueryResult queryPartMaster(String number) throws WTException {

		if (number == null) {
			throw new IllegalArgumentException("Part number is null");
		}

		QuerySpec qs = new QuerySpec(WTPartMaster.class);
		qs.setAdvancedQueryEnabled(true);

		SearchCondition sc = null;
		sc = new SearchCondition(WTPartMaster.class, "number", SearchCondition.EQUAL, number);
		qs.appendWhere(sc);

		QueryResult qr = PersistenceHelper.manager.find(qs);
		return qr;
	}

	/**
	 * 获取某个对象团队实例中的某个角色的所有成员
	 * 
	 * @param container
	 * @param rolesName
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Set<WTPrincipal> getRoleMember(Object object, String rolesName) {

		Set<WTPrincipal> users = new HashSet<WTPrincipal>();

		try {
			Role role = Role.toRole(rolesName);
			WTRoleHolder2 holder = TeamCCHelper.getTeamFromObject(object);
			Vector<Role> conRoles = holder.getRoles();
			if (conRoles.contains(role)) {
				Enumeration enumeration = holder.getPrincipalTarget(role);
				while (enumeration.hasMoreElements()) {
					Object obj = enumeration.nextElement();
					WTPrincipal principal = null;
					if (obj instanceof WTPrincipal) {
						principal = (WTPrincipal) obj;
					} else if (obj instanceof WTPrincipalReference) {
						WTPrincipalReference principalReference = (WTPrincipalReference) obj;
						principal = principalReference.getPrincipal();
					}
					users.add(principal);
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return users;
	}
	
	public static String getSelectedOids(NmCommandBean bean) throws WTException{
		HashSet<String> oids = new HashSet<String>();
		List<Object> list = PromotionItemQueryCommands.getPromotionItems(bean);
		for (Object obj : list) {
			String oid = OidHelper.getNmOid(obj).getReferenceString();
			oids.add(oid);
		}
		if(!oids.isEmpty()){
			return StringUtils.join(oids, ",");
		}
		return "";
	}
	
	public static String getPasteItems(NmClipboardBean bean) throws WTException{
		ReferenceFactory refefence = new ReferenceFactory();
		HashSet<String> oids = new HashSet<String>();
		ArrayList<?> list = bean.getClipped();
		for (Object object : list) {
			NmOid nmoid = (NmOid)object;
			String oid = refefence.getReferenceString((Persistable)nmoid.getRefObject());
			oids.add(oid);
		}
		if(!oids.isEmpty()){
			return StringUtils.join(oids, ",");
		}
		return "";
	}
	
	public static StringBuffer checkSourceChangeObjecs(PromotionNotice pn,WTPart part,String source) throws MaturityException, WTException {

		StringBuffer message = new StringBuffer();

		WTPartMaster master = (WTPartMaster) part.getMaster();
		WTPart latestBigPart = (WTPart) getLatestVersionByMaster(master);
		TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(part);
		String type = ti.getTypename();
		log.debug("part type==" + type);		

		if (!type.endsWith(TypeName.CATLPart)) {
			message.append(part.getNumber() + ",对象不是“零部件”，只有零部件才允许加入采购类型更改单！\n");
		}
		if (!part.getState().toString().endsWith(PartState.RELEASED)) {
			message.append(part.getNumber() + ",对象不是“已发布”状态，不能添加到采购类型更改单！\n");
		}
		if (!part.equals(latestBigPart)) {
			message.append(part.getNumber() + ",对象不是最新版本，不能添加到采购类型更改单！\n");
		}
		if(source != null){//采购类型保存时 物料采购类型还没有改变source=null不用做如下校验          当调整采购类型完成时，采购类型有了变化需做如下校验
			String currentMaturity = (String)IBAUtil.getIBAValue(part.getMaster(), PartConstant.IBA_CATL_Maturity);
			String currentFaeStatus = (String)IBAUtil.getIBAValue(part.getMaster(), PartConstant.IBA_CATL_FAEStatus);
			String faeStatus = RefreshFAEStatusUtil.getInitialFAEStatusValueChange(part,source);
			if(RefreshFAEStatusUtil.needRefresh(currentFaeStatus, faeStatus, currentMaturity) != null){//FAE状态有变化 
				if(MaturityUpReportHelper.isNFAEUndone(part)){
					message.append(part.getNumber() + ",对象正在非FAE成熟度报告中，不能启动采购类型更改单！ \n");
				}
			}
			if(faeStatus.equals(PartConstant.CATL_FAEStatus_1) && currentFaeStatus != null && currentFaeStatus.equals(PartConstant.CATL_FAEStatus_3)){//FAE流程特殊，因为流程一发起就已经结束 所以不能通过流程的状态来判断   只能通过物料的的FAE状态是否是 “评估中”来判断
				message.append(part.getNumber() + ",对象FAE物料成熟度升级单中，不能启动采购类型更改单！ \n");
			}
		}
		if (isSourceChangeUndone(part,pn) > 0) {
			message.append(part.getNumber() + ",对象正在采购类型更改单中，不能启动采购类型更改单！ \n");
		}
		if(isPlatformChangeUndone(part,pn)>0){
        	message.append(part.getNumber() + ",对象正在产品线标识更改单中，不能启动采购类型更改单！ \n");
		}
		if(PromotionUtil.isexsitPromotion(part) > 0){
			message.append(part.getNumber() + ",对象正在设计禁用单中，不能启动采购类型更改单！ \n");
		}
		if (isECRADUndone(part)) {
			message.append(part.getNumber() + ",对象正在变更中，不能启动采购类型更改单！ \n");
		}
		
		return message;
	}
	

	public static StringBuffer checkPlatformChangeObjecs(PromotionNotice pn,WTPart part) throws MaturityException, WTException {

		StringBuffer message = new StringBuffer();

		WTPartMaster master = (WTPartMaster) part.getMaster();
		WTPart latestBigPart = (WTPart) getLatestVersionByMaster(master);
		TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(part);
		String type = ti.getTypename();
		log.debug("part type==" + type);		

		if (!type.endsWith(TypeName.CATLPart)) {
			message.append(part.getNumber() + ",对象不是“零部件”，只有零部件才允许加入产品线标识更改单！\n");
		}
		if (!part.getState().toString().endsWith(PartState.RELEASED)) {
			message.append(part.getNumber() + ",对象不是“已发布”状态，不能添加到产品线标识更改单！\n");
		}
		if (!part.equals(latestBigPart)) {
			message.append(part.getNumber() + ",对象不是最新版本，不能添加到产品线标识更改单！\n");
		}
		if(MaturityUpReportHelper.isNFAEUndone(part)){
			message.append(part.getNumber() + ",对象正在非FAE成熟度报告中，不能启动产品线标识更改单！ \n");
		}
		if (isSourceChangeUndone(part,pn) > 0) {
			message.append(part.getNumber() + ",对象正在采购类型更改单中，不能启动产品线标识更改单！ \n");
		}
		if(isPlatformChangeUndone(part,pn)>0){
        	message.append(part.getNumber() + ",对象正在产品线标识更改单中，不能启动产品线标识更改单！ \n");
		}
		if(PromotionUtil.isexsitPromotion(part) > 0){
			message.append(part.getNumber() + ",对象正在设计禁用单中，不能启动产品线标识更改单！ \n");
		}
		if (isECRADUndone(part)) {
			message.append(part.getNumber() + ",对象正在变更中，不能启动产品线标识更改单！ \n");
		}
		
		return message;
	}
	public static int isPlatformChangeUndone(WTObject object, PromotionNotice elsePN) throws WTException {
		QueryResult prcounts = BomWfUtil.isHavePromoteRequest(object);
		HashSet<String> pstateHashSet = new HashSet<String>();
		// check process state ,if has excuted
		while (prcounts.hasMoreElements()) {
			Object[] object2 = (Object[]) prcounts.nextElement();
			PromotionNotice promotion = (PromotionNotice) object2[0];
			QueryResult processResult = new QueryResult();

			TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(promotion);
			String type = ti.getTypename();
			if (type.endsWith(TypeName.PlatformChangePN) && (elsePN == null || !promotion.equals(elsePN)) ) {
				try {
					processResult = NmWorkflowHelper.service.getAssociatedProcesses(promotion, null, null);
					log.debug("PlatformChange processResult size====" + processResult.size());
				} catch (WTException e1) {
					log.debug("PlatformChange getAssociatedProcesses failed----!");
					e1.printStackTrace();
				}
			}

			if (processResult.hasMoreElements()) {
				WfProcess process = (WfProcess) processResult.nextElement();
				log.debug("SourceChange process.getState().getDisplay()" + process.getState().getDisplay());
				if (process.getState().toString().endsWith("OPEN_RUNNING")) {
					pstateHashSet.add(promotion.getNumber());
				}
			}
		}
		log.debug("SourceChange pstateHashSet.size() ==" + pstateHashSet.size());
		return pstateHashSet.size();
	}

	public static StringBuffer checkSourceChangeObjecs(PromotionNotice pn) throws MaturityException, WTException {

		StringBuffer message = new StringBuffer();
		QueryResult promotables = null;

		promotables = MaturityHelper.service.getBaselineItems(pn);
		int i = 1;
		while (promotables.hasMoreElements()) {
			Object obj = promotables.nextElement();
			if (obj instanceof WTPart) {
				WTPart part = (WTPart) obj;
				StringBuffer sb = checkSourceChangeObjecs(pn,part,part.getSource().getDisplay(Locale.ENGLISH));
				if(sb.length() > 0){
					message.append(i++).append(". ").append(sb.toString());
				}
			} else {
				String number = BomWfUtil.getObjectnumber((Persistable) obj);
				message.append(i++).append(". ").append(number + ",对象不是“零部件”，只有零部件才允许加入采购类型更改单！\n");
			}
		}
		return message;
	}
	
	public static StringBuffer checkPlatformChangeObjecs(PromotionNotice pn) throws MaturityException, WTException {
		StringBuffer message = new StringBuffer();
		QueryResult promotables = null;
		promotables = MaturityHelper.service.getBaselineItems(pn);
		int i = 1;
		while (promotables.hasMoreElements()) {
			Object obj = promotables.nextElement();
			if (obj instanceof WTPart) {
				WTPart part = (WTPart) obj;
				StringBuffer sb = checkPlatformChangeObjecs(pn,part);
				if(sb.length() > 0){
					message.append(i++).append(". ").append(sb.toString());
				}
			} else {
				String number = BomWfUtil.getObjectnumber((Persistable) obj);
				message.append(i++).append(". ").append(number + ",对象不是“零部件”，只有零部件才允许加入产品线标识更改单！\n");
			}
		}
		return message;
	}
}
