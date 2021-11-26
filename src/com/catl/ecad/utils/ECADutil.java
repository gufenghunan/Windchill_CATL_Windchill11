package com.catl.ecad.utils;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Collection;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.catl.cadence.util.NodeUtil;
import com.catl.common.constant.RoleName;
import com.catl.common.util.ResultMessage;
import com.catl.common.util.WorkflowUtil;
import com.ptc.core.components.util.OidHelper;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;
import com.ptc.windchill.enterprise.wvs.repsAndMarkups.utils.RepsAndMarkupsClientHelper;
import com.ptc.wvs.common.ui.Publisher;
import com.ptc.wvs.common.ui.PublisherAction;
import com.ptc.wvs.common.ui.VisualizationHelper;
import com.ptc.xworks.windchill.util.RevisionControlledUtils;
import com.ptc.xworks.workflow.AssociatedPromotionNoticeFinder;

import wt.change2.ChangeException2;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeIssue;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.doc.Document;
import wt.doc.DocumentMaster;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.build.EPMBuildRule;
import wt.epm.retriever.LatestConfigSpecWithoutWorkingCopies;
import wt.epm.structure.EPMReferenceLink;
import wt.epm.structure.EPMStructureHelper;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.WTReference;
import wt.fc.collections.WTHashSet;
import wt.fc.collections.WTSet;
import wt.folder.Folder;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.LifeCycleTemplateReference;
import wt.log4j.LogR;
import wt.maturity.MaturityException;
import wt.maturity.MaturityHelper;
import wt.maturity.PromotionNotice;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartReferenceLink;
import wt.pds.StatementSpec;
import wt.pom.PersistenceException;
import wt.project.Role;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.team.Team;
import wt.team.TeamHelper;
import wt.team.TeamReference;
import wt.team.WTRoleHolder2;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;
import wt.vc.VersionControlHelper;
import wt.vc.baseline.BaselineHelper;
import wt.vc.config.ConfigSpec;
import wt.vc.config.LatestConfigSpec;
import wt.vc.struct.StructHelper;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;
import wt.vc.wip.CheckoutInfo;
import wt.vc.wip.WorkInProgressException;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfState;
import wt.workflow.work.WfAssignedActivity;

public class ECADutil {

	private static final String CLASSNAME = ECADutil.class.getName();

	private static Logger logger = LogR.getLogger(CLASSNAME);

	// 过滤出M视图的对象.
	public static List<Persistable> removeMview_pers(List<Persistable> listper) {
		List<Persistable> alllist = new ArrayList<Persistable>();

		for (Persistable per : listper) {
			if (per instanceof WTPart) {
				WTPart wtpart = (WTPart) per;
				String view = wtpart.getViewName();
				if (view.equalsIgnoreCase("Design")) {
					alllist.add(per);
				}
			}
		}

		return alllist;
	}

	// 取大版本的vr
	public static String getBigVersion(Persistable per) {
		return OidHelper.getOidAsString(per);
	}

	// 取所有子件
	public static List<WTPart> getAllChildPart(WTPart parentPart)
			throws WTException {
		List<WTPart> childPart = new ArrayList<WTPart>();
		WTPart part;
		for (QueryResult qs = WTPartHelper.service.getUsesWTParts(parentPart,
				new LatestConfigSpec()); qs.hasMoreElements(); childPart
				.add(RevisionControlledUtils.getOrginalCopy(part))) {
			wt.fc.Persistable objs[] = (wt.fc.Persistable[]) (wt.fc.Persistable[]) qs
					.nextElement();
			part = (WTPart) objs[1];
		}
		return childPart;
	}

	public static String getNum(Persistable per) {
		if (per instanceof WTPart) {
			return ((WTPart) per).getNumber();
		} else if (per instanceof WTDocument) {
			return ((WTDocument) per).getNumber();
		} else if (per instanceof EPMDocument) {
			return ((EPMDocument) per).getNumber();
		}

		return "";
	}

	/**
	 * GET VERSION BY PER
	 * 
	 * @param per
	 * @return X1.0/2.0
	 */
	public static String getversion(Persistable per) {
		if (per instanceof WTPart) {
			WTPart part = (WTPart) per;
			return part.getVersionIdentifier().getValue() + "."
					+ part.getIterationIdentifier().getValue();
		} else if (per instanceof WTDocument) {
			WTDocument doc = (WTDocument) per;
			return doc.getVersionIdentifier().getValue() + "."
					+ doc.getIterationIdentifier().getValue();
		} else if (per instanceof EPMDocument) {
			EPMDocument epm = (EPMDocument) per;
			return epm.getVersionIdentifier().getValue() + "."
					+ epm.getIterationIdentifier().getValue();
		} else
			return "";
	}

	/**
	 * 是否包含某个版本
	 */
	public static boolean isX_Version(List<WTPart> childPartlist, String version) {
		for (WTPart part : childPartlist) {
			String partVersion = part.getVersionIdentifier().getValue();
			if (partVersion.startsWith(version)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 取子件
	 * 
	 * @throws WTException
	 */
	public static List<WTPart> getChildPart(WTPart seed) throws WTException {
		List<WTPart> childPart = new ArrayList<WTPart>();
		WTPart part;
		for (QueryResult qs = WTPartHelper.service.getUsesWTParts(seed,
				new LatestConfigSpec()); qs.hasMoreElements(); childPart
				.add(RevisionControlledUtils.getOrginalCopy(part))) {
			wt.fc.Persistable objs[] = (wt.fc.Persistable[]) (wt.fc.Persistable[]) qs
					.nextElement();
			part = (WTPart) objs[1];
		}

		return childPart;
	}

	/**
	 * 集合中是否包含部件
	 * 
	 * @param perlist
	 * @return
	 */
	public static List<WTPart> hasPart(List<Persistable> perlist) {
		List<WTPart> partlist = new ArrayList<WTPart>();

		for (Persistable per : perlist) {
			if (per instanceof WTPart) {
				partlist.add((WTPart) per);
			}
		}
		return partlist;
	}

	/**
	 * ADD alldata To pn
	 * 
	 * @param pn
	 * @param targets
	 * @throws WTException
	 */
	public static void addToTargets(PromotionNotice pn, WTSet targets)
			throws WTException {
		if (targets != null && targets.size() > 0) {
			try {
				MaturityHelper.service.savePromotionTargets(pn, targets);
			} catch (WTPropertyVetoException e) {
				e.printStackTrace();
				throw new WTException(e, e.getLocalizedMessage());
			}
			BaselineHelper.service
					.addToBaseline(targets, pn.getConfiguration());
		}
	}

	/**
	 * REMOVE part By pn
	 * 
	 * @param pn
	 * @param targets
	 * @throws WTException
	 */
	public static void removeTargets(PromotionNotice pn, WTSet targets)
			throws WTException {
		if (targets != null && targets.size() > 0) {
			MaturityHelper.service.deletePromotionTargets(pn, targets);
			BaselineHelper.service.removeFromBaseline(targets,
					pn.getConfiguration());
		}
	}

	/**
	 * GET WTPART BY PARTNUMBER
	 * 
	 * @param partNumber
	 * @return
	 */
	public static WTPart getPartByNumber(String partNumber) {

		WTPart part = null;
		try {
			QuerySpec qs = new QuerySpec(WTPartMaster.class);
			SearchCondition sc = new SearchCondition(WTPartMaster.class,
					WTPartMaster.NUMBER, SearchCondition.EQUAL, partNumber);
			qs.appendWhere(sc, new int[] { 0 });
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
			if (qr.hasMoreElements()) {
				WTPartMaster wtMaster = (WTPartMaster) qr.nextElement();
				part = getPartByPartMaster(wtMaster);
			}
		} catch (WTException e) {
			e.printStackTrace();
		}

		return part;
	}

	/**
	 * 通过PartMaster找到WTPart
	 * 
	 * @throws WTException
	 */
	public static WTPart getPartByPartMaster(WTPartMaster wtMaster) {
		WTPart wtpart = null;
		long wtMasterId = wtMaster.getPersistInfo().getObjectIdentifier()
				.getId();
		try {
			QuerySpec qs = new QuerySpec(WTPart.class);
			SearchCondition sc = new SearchCondition(WTPart.class,
					"masterReference.key.id", SearchCondition.EQUAL, wtMasterId);
			qs.appendWhere(sc, new int[] { 0 });
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
			wtpart = (WTPart) qr.nextElement();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return wtpart;
	}

	/**
	 * GET PERSISTABLE BY OID
	 * 
	 * @param oid
	 * @return
	 * @throws WTException
	 */
	public static Persistable getObjectByOid(String oid) throws WTException {
		Persistable p = null;

		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			ReferenceFactory referencefactory = new ReferenceFactory();
			WTReference wtreference = referencefactory.getReference(oid);
			p = wtreference.getObject();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(flag);
		}

		return p;
	}

	/**
	 * GET OID BY PERSISTABLE
	 * 
	 * @param oid
	 * @return
	 * @throws WTException
	 */
	public static String getOidByObject(Persistable p) {
		String oid = "";
		if (p != null) {
			oid = "OR:" + p.toString();
		}
		return oid;
	}

	/**
	 * 获取选择的oid
	 * 
	 * @param soid
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<String> getSelectedOid(List soid) {
		List<String> oids = new ArrayList<String>();
		String ids[];
		for (Iterator i$ = soid.iterator(); i$.hasNext(); oids
				.add(ids[ids.length - 1])) {
			Object oid = i$.next();
			String str = oid.toString();
			str = str.replace("!*", "");
			ids = str.split("\\$", -1);
		}

		return oids;
	}

	/**
	 * param str 要分割的字符串 return ary[ary.length-1] 返回最后一个元数
	 * 
	 * @author wuzhitao
	 */
	public static String getStrSplit(Persistable p) {

		String str = TypeIdentifierUtility.getTypeIdentifier(p).getTypename();

		if (str != null) {
			return str.substring(str.lastIndexOf("|") + 1, str.length());
		}
		return "";
	}

	/**
	 * 通过申请单取相关对象(文档,部件,epm)
	 * 
	 * @param pn
	 * @return
	 * @throws WTException
	 */
	public static Map<String, Set<Persistable>> getTargets(WTObject pbo)
			throws WTException {
		Map<String, Set<Persistable>> mp = new HashMap<String, Set<Persistable>>();
		Set<Persistable> setPart = new HashSet<Persistable>();
		Set<Persistable> setDoc = new HashSet<Persistable>();
		Set<Persistable> setEPM = new HashSet<Persistable>();

		PromotionNotice pn = (PromotionNotice) pbo;

		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			QueryResult qr = MaturityHelper.service.getPromotionTargets(pn);
			qr = new LatestConfigSpec().process(qr); // 过滤最新
			while (qr.hasMoreElements()) {
				Persistable per = (Persistable) qr.nextElement();
				if (per instanceof WTPart) {
					setPart.add(per);
				} else if (per instanceof WTDocument) {
					setDoc.add(per);
				} else if (per instanceof EPMDocument) {
					setEPM.add(per);
				}
			}
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}

		mp.put("CATL_PART", setPart);
		mp.put("CATL_DOC", setDoc);
		mp.put("CATL_EPM", setEPM);
		return mp;
	}

	/**
	 * 查询申请单全部对象 @return @throws WTException @throws
	 */
	public static List<Persistable> getallper(WTObject pbo) throws WTException {
		List<Persistable> perset = new ArrayList<Persistable>();

		PromotionNotice pn = (PromotionNotice) pbo;
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			QueryResult qr = MaturityHelper.service.getPromotionTargets(pn);
			qr = new LatestConfigSpec().process(qr); // 过滤最新
			while (qr.hasMoreElements()) {
				Persistable per = (Persistable) qr.nextElement();
				perset.add(per);
			}
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}

		return perset;
	}

	public static List<Persistable> getAlldataConverList(WTObject pbo)
			throws WTException {
		List<Persistable> perlist = new ArrayList<Persistable>();
		Map<String, Set<Persistable>> mp = getTargets(pbo);
		for (Set<Persistable> set : mp.values()) {
			for (Persistable per : set) {
				perlist.add(per);
			}
		}

		return perlist;
	}

	/**
	 * 取第一层子件
	 * 
	 * @throws WTException
	 */
	public static Set<WTPart> getFirstChild(WTPart part) throws WTException {
		Set<WTPart> set = new HashSet<WTPart>();
		QueryResult rs = WTPartHelper.service.getUsesWTParts(part,
				new LatestConfigSpecWithoutWorkingCopies());
		while (rs.hasMoreElements()) {
			Persistable[] objs = (Persistable[]) rs.nextElement();
			if (objs[1] instanceof WTPart) {
				WTPart child = (WTPart) objs[1];
				if (part.getViewName().equals(child.getViewName())) {
					set.add(child);
				}
			}
		}
		return set;
	}

	/**
	 * 通过视图找到最新版本Part
	 * 
	 * @param master
	 * @param viewName
	 * @return
	 * @throws WTException
	 */
	public static WTPart getPartByMasterAndView(WTPartMaster master,
			String viewName) throws WTException {
		if (viewName == null || viewName.equals("")) {
			viewName = "Design";
		}
		View view = ViewHelper.service.getView(viewName);
		if (master == null || view == null) {
			return null;
		}

		WTPart part = null;
		QuerySpec qs = new QuerySpec(WTPart.class);
		SearchCondition sc = new SearchCondition(WTPart.class, WTPart.NUMBER,
				SearchCondition.EQUAL, master.getNumber());
		qs.appendWhere(sc, new int[] { 0 });
		qs.appendAnd();
		sc = new SearchCondition(WTPart.class, "view.key.id",
				SearchCondition.EQUAL, view.getPersistInfo()
						.getObjectIdentifier().getId());
		qs.appendWhere(sc, new int[] { 0 });
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		qr = new LatestConfigSpec().process(qr);
		if (qr.hasMoreElements()) {
			part = (WTPart) qr.nextElement();
		}
		return part;
	}

	/**
	 * 通过视图找到最新版本EPM
	 * 
	 * @param master
	 * @param viewName
	 * @return
	 * @throws WTException
	 */
	public static EPMDocument getEpmByMasterAndView(EPMDocumentMaster master,
			String viewName) throws WTException {
		if (viewName == null || viewName.equals("")) {
			viewName = "Design";
		}
		View view = ViewHelper.service.getView(viewName);
		if (master == null || view == null) {
			return null;
		}

		EPMDocument epm = null;
		QuerySpec qs = new QuerySpec(EPMDocument.class);
		SearchCondition sc = new SearchCondition(EPMDocument.class,
				EPMDocument.NUMBER, SearchCondition.EQUAL, master.getNumber());
		qs.appendWhere(sc, new int[] { 0 });
		qs.appendAnd();
		sc = new SearchCondition(EPMDocument.class, "view.key.id",
				SearchCondition.EQUAL, view.getPersistInfo()
						.getObjectIdentifier().getId());
		qs.appendWhere(sc, new int[] { 0 });
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		qr = new LatestConfigSpec().process(qr);
		if (qr.hasMoreElements()) {
			epm = (EPMDocument) qr.nextElement();
		}
		return epm;
	}

	/**
	 * 是否在某个申请单流程中
	 * 
	 * @param promotionNotice
	 * @param p
	 * @param wfTemplatename
	 * @return
	 * @throws WTException
	 */
	public static Collection<Persistable> addAssociatedLogic(
			WTObject promotionNotice, Persistable p, String wfTemplatename)
			throws WTException {
		AssociatedPromotionNoticeFinder a = new AssociatedPromotionNoticeFinder();
		Collection<Persistable> processCollection = a.findProcessPbo(p);
		if (processCollection != null) {
			if (promotionNotice != null) {
				processCollection.remove(promotionNotice);
			}

			for (Persistable persistable : processCollection) {
				PromotionNotice pn = (PromotionNotice) persistable;
				if (pn.getName().contains(wfTemplatename)) {
					WfProcess process = getCompleteWfprocess(pn);
					if (pn.getState().toString().endsWith("RESOLVED")
							|| pn.getState().toString().endsWith("APPROVED")
							|| pn.getState().toString().endsWith("COMPLETED")
							&& process != null) {
						processCollection.remove(pn);
					}
				}
			}
		}
		return processCollection;
	}

	public static WfProcess getCompleteWfprocess(
			Persistable primaryBusinessObject) throws WTException {
		if (primaryBusinessObject == null) {
			return null;
		}
		Enumeration<WfProcess> enumeration = WfEngineHelper.service
				.getAssociatedProcesses(primaryBusinessObject,
						WfState.CLOSED_COMPLETED_EXECUTED, null);
		if (enumeration.hasMoreElements()) {
			return enumeration.nextElement();
		}
		return null;
	}

	/**
	 * 检查文档是否有PDF附件
	 * 
	 * @param doc
	 * @return
	 * @throws WTException
	 */
	public static boolean checkDocPDF(WTDocument doc) throws WTException {
		QueryResult qr = ContentHelper.service.getContentsByRole(doc,
				ContentRoleType.SECONDARY);
		while (qr.hasMoreElements()) {
			ApplicationData adata = (ApplicationData) qr.nextElement();
			String name = adata.getFormat().getDataFormat().getFormatName();
			if (!name.isEmpty()) {
				if (name.toUpperCase().equals("PDF")) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * GET WTDocument BY NUMBER
	 * 
	 * @param docNumber
	 * @return
	 */
	public static WTDocument getDocByNumber(String docNumber) {
		WTDocument doc = null;
		try {
			QuerySpec qs = new QuerySpec(WTDocument.class);
			SearchCondition sc = new SearchCondition(WTDocument.class,
					WTDocument.NUMBER, SearchCondition.EQUAL, docNumber);
			qs.appendWhere(sc, new int[] { 0 });
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
			qr = new LatestConfigSpec().process(qr); // 过滤最新

			while (qr.hasMoreElements()) {
				doc = (WTDocument) qr.nextElement();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}

	/**
	 * 删除可视化或注释集
	 * 
	 * @param epm
	 * @throws WTException
	 */
	public static void customDeleteRepsAndMarkups(EPMDocument epm)
			throws WTException {
		WTReference wtreference = null;

		QueryResult epmReps = new VisualizationHelper().getRepresentations(epm);
		while (epmReps.hasMoreElements()) {
			wtreference = new ReferenceFactory()
					.getReference((Persistable) epmReps.nextElement());
			if (wtreference != null) {
				Class<?> class1 = wtreference.getReferencedClass();
				if (wt.viewmarkup.WTMarkUp.class.isAssignableFrom(class1)) {
					System.out.println("delete WTMarkUp");
					RepsAndMarkupsClientHelper.deleteMarkup(
							wtreference.toString(), null);
				} else if (wt.representation.Representation.class
						.isAssignableFrom(class1)) {
					System.out.println("delete Representation");
					RepsAndMarkupsClientHelper.deleteRepresentation(wtreference
							.toString());
				}
			}
		}
	}

	/**
	 * 在工作流中为工程图重新发布可视化
	 * 
	 * @param epm
	 * @throws WTException
	 */
	public static void rePubEpmsReps(EPMDocument epm) throws WTException {

		String objRef = ObjectReference.newObjectReference(epm).toString();
		PublisherAction pa = new PublisherAction(PublisherAction.QUEUEPRIORITY,
				"H"); // 设置优先级为最高
		new Publisher().doPublish(false, true, objRef, (ConfigSpec) null,
				(ConfigSpec) null, true, null, null, Publisher.EPM,
				pa.toString(), 0);
		// System.out.println("重新发布结果值：" + result);

	}

	public static void main(String args[]) throws WTRuntimeException,
			WTException, WTPropertyVetoException {
		WTPart part = (WTPart) new ReferenceFactory().getReference(
				"VR:wt.part.WTPart:199634").getObject();
		EPMDocument epm = (EPMDocument) new ReferenceFactory().getReference(
				"VR:wt.epm.EPMDocument:200488").getObject();
		getTeamMemberByPart(part, epm);
	}

	/**
	 * 根据设计工具名称查询当前用户所有检出的原理图或者PCB图清单
	 * 
	 * @param softName
	 *            设计工具名称
	 * @return
	 * @throws WTException
	 */
	public static ArrayList<EPMDocument> findCheckoutEPM(String softName)
			throws WTException {
		ArrayList<EPMDocument> epmlist = new ArrayList<>();
		WTUser user = (WTUser) SessionHelper.getPrincipal();
		QueryResult checkoutQr = WorkInProgressHelper.service
				.findCheckedOutObjects(user);
		logger.debug("softName:\t" + softName);
		String epmtype = "";
		if (ECADConst.SCHTOOL.equalsIgnoreCase(softName)) {
			epmtype = ECADConst.SCHTYPE;
		} else if (ECADConst.PCBTOOL.equalsIgnoreCase(softName)) {
			epmtype = ECADConst.PCBTYPE;
		}
		logger.debug("Need EPMDocument subType:\t" + epmtype);
		while (checkoutQr.hasMoreElements()) {
			WTObject obj = (WTObject) checkoutQr.nextElement();
			if (obj instanceof EPMDocument) {
				EPMDocument epm = (EPMDocument) obj;
				logger.debug("EPMDocument number:\t" + epm.getNumber());
				String type = getStrSplit(epm);
				logger.debug("EPMDocument subType:\t" + epmtype);
				if (type.equalsIgnoreCase(epmtype)) {
					epmlist.add(epm);
				}
			}
		}
		return epmlist;

	}

	/**
	 * 判断是否为原理图
	 * 
	 * @param epm
	 * @return
	 */
	public static boolean isSCHEPM(EPMDocument epm) {
		if (epm != null) {
			String type = getStrSplit(epm);
			if (type.equalsIgnoreCase(ECADConst.SCHTYPE)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断是否为PCB图
	 * 
	 * @param epm
	 * @return
	 */
	public static boolean isPCBEPM(EPMDocument epm) {
		if (epm != null) {
			String type = getStrSplit(epm);
			if (type.equalsIgnoreCase(ECADConst.PCBTYPE)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 根据部件给图纸设置角色成员
	 * 
	 * @param part
	 * @param epm
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void getTeamMemberByPart(WTPart part, EPMDocument epm)
			throws WTException, WTPropertyVetoException {
		if (part != null && epm != null) {
			TeamReference reference = part.getTeamId();
			Team team = (Team) reference.getObject();

			Map map = team.getRolePrincipalMap();
			// LifeCycleTemplateReference tempRef = epm.getLifeCycleTemplate();
			Team epmTeam = (Team) epm.getTeamId().getObject();
			for (Iterator it = map.keySet().iterator(); it.hasNext();) {
				Role role = (Role) it.next();
				List<WTPrincipalReference> users = (List) map.get(it.next());
				for (WTPrincipalReference user : users) {
					epmTeam.addPrincipal(role, user.getPrincipal());
				}
			}
			PersistenceHelper.manager.refresh(epmTeam);
		}
	}

	/**
	 * 检出
	 * 
	 * @param epmDoc
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static EPMDocument checkOutObject(EPMDocument epmDoc)
			throws WTException, WTPropertyVetoException {
		Folder myCOFolder = null;
		myCOFolder = WorkInProgressHelper.service.getCheckoutFolder();
		// 判断工作副本是否是检出状态
		if (!WorkInProgressHelper.isCheckedOut(epmDoc)) {
			WorkInProgressHelper.service.checkout(epmDoc, myCOFolder, null);
			epmDoc = (EPMDocument) WorkInProgressHelper.service
					.workingCopyOf(epmDoc);
		}
		return epmDoc;
	}

	/**
	 * 检入
	 * 
	 * @param object
	 * @return
	 * @throws WorkInProgressException
	 * @throws WTPropertyVetoException
	 * @throws PersistenceException
	 * @throws WTException
	 */
	public static Workable checkInObject(Workable object)
			throws WorkInProgressException, WTPropertyVetoException,
			PersistenceException, WTException {
		object = WorkInProgressHelper.service.checkin(object, "");
		return object;
	}

	public static String getCheckoutListInfo(String softname)
			throws WTException, JSONException {
		ArrayList<EPMDocument> epms = findCheckoutEPM(softname);
		JSONArray array = new JSONArray();
		for (EPMDocument epm : epms) {
			String number = epm.getNumber();
			String name = epm.getName();
			String state = epm.getLifeCycleState().getDisplay(Locale.CHINA);
			String status = WorkInProgressHelper.isCheckedOut(epm) ? "检出"
					: "检入";
			String version = VersionControlHelper
					.getIterationDisplayIdentifier(epm).toString();
			String boardNumber = "";
			WTPart part = CommonUtil.getLatestWTpartByNumber(number);
			if (part != null) {
				IBAUtil iba = new IBAUtil(part);
				boardNumber = iba.getIBAValue("Board_Number");
			} else {
				throw new WTException("图纸" + number + "对应的物料在系统中不存在。");
			}
			System.out.println("boardNumber:\t" + boardNumber);
			JSONObject temp = new JSONObject();
			temp.put("number", number);
			temp.put("name", name);
			temp.put("state", state);
			temp.put("status", status);
			temp.put("version", version);

			// if(!StringUtils.isBlank(boardNumber)){
			temp.put("boardNumber", boardNumber);
			// }
			array.put(temp);
		}
		return array.toJSONString();
	}

	public static String getEPMDocumentInfo(String number, String softName)
			throws WTException, JSONException, PropertyVetoException {
		EPMDocument epm = EPMUtil.getEPMByNumber(number);
		if (epm == null) {
			throw new WTException("您所查询的图纸" + number + "在PLM系统中不存在。");
		}
		if (ECADConst.SCHTOOL.equalsIgnoreCase(softName)) {
			if (isSCHEPM(epm)) {
				String name = epm.getName();
				String state = epm.getLifeCycleState().getDisplay(Locale.CHINA);
				String status = WorkInProgressHelper.isCheckedOut(epm) ? "检出"
						: "检入";
				String version = VersionControlHelper
						.getIterationDisplayIdentifier(epm).toString();
				String boardNumber = "";
				WTPart part = CommonUtil.getLatestWTpartByNumber(number);
				if (part != null) {
					IBAUtil iba = new IBAUtil(part);
					boardNumber = iba.getIBAValue("Board_Number");
				} else {
					throw new WTException("原理图" + number + "对应的PCBA在系统中不存在。");
				}
				System.out.println("boardNumber:\t" + boardNumber);
				JSONObject temp = new JSONObject();
				temp.put("number", number);
				temp.put("name", name);
				temp.put("state", state);
				temp.put("status", status);
				temp.put("version", version);
				temp.put("boardNumber", boardNumber);
				ApplicationData appdata = EPMUtil.getPrimaryAppdata(epm);
				if (appdata == null) {
					temp.put("isFirst", "Y");
				} else {
					temp.put("isFirst", "N");
				}
				return temp.toJSONString();
			} else {
				throw new WTException("您所查询的图纸" + number + "在PLM系统中不是原理图对象。");
			}
		} else if (ECADConst.PCBTOOL.equalsIgnoreCase(softName)) {
			if (isPCBEPM(epm)) {
				String name = epm.getName();
				String state = epm.getLifeCycleState().getDisplay(Locale.CHINA);
				String status = WorkInProgressHelper.isCheckedOut(epm) ? "检出"
						: "检入";
				String version = VersionControlHelper
						.getIterationDisplayIdentifier(epm).toString();
				String boardNumber = "";
				WTPart part = CommonUtil.getLatestWTpartByNumber(number);
				if (part != null) {
					IBAUtil iba = new IBAUtil(part);
					boardNumber = iba.getIBAValue("Board_Number");
				} else {
					throw new WTException("PCB图" + number + "对应的PCB物料在系统中不存在。");
				}
				System.out.println("boardNumber:\t" + boardNumber);
				JSONObject temp = new JSONObject();
				temp.put("number", number);
				temp.put("name", name);
				temp.put("state", state);
				temp.put("status", status);
				temp.put("version", version);
				temp.put("boardNumber", boardNumber);
				ApplicationData appdata = EPMUtil.getPrimaryAppdata(epm);
				if (appdata == null) {
					temp.put("isFirst", "Y");
				} else {
					temp.put("isFirst", "N");
				}
				return temp.toJSONString();
			} else {
				throw new WTException("您所查询的图纸" + number + "在PLM系统中不是PCB图对象。");
			}
		} else {
			throw new WTException("无法识别的设计工具" + softName);
		}
	}

	/**
	 * 移除所有说明文档，不升级部件版本
	 * 
	 * @param parent
	 * @throws WTException
	 */
	public static void removeLinks(WTPart parent) throws WTException {

		QueryResult partUsageLinks = StructHelper.service.navigateDescribedBy(
				parent, WTPartDescribeLink.class, false);
		if (partUsageLinks != null && partUsageLinks.size() > 0) {
			WTHashSet removeUsageLinkSet = new WTHashSet(partUsageLinks);
			PersistenceServerHelper.manager.remove(removeUsageLinkSet);
		}
		PersistenceHelper.manager.refresh(parent);

	}

	/**
	 * 移除PCBA装配图文档，不升级部件版本
	 * 
	 * @param parent
	 * @throws WTException
	 */
	public static void removePCBADrawingLinks(WTPart parent) throws WTException {

		QueryResult partUsageLinks = StructHelper.service.navigateDescribedBy(
				parent, WTPartDescribeLink.class, false);
		if (partUsageLinks != null && partUsageLinks.size() > 0) {
			WTHashSet removeUsageLinkSet = new WTHashSet();
			while (partUsageLinks.hasMoreElements()) {
				WTPartDescribeLink deslink = (WTPartDescribeLink) partUsageLinks
						.nextElement();
				WTDocument doc = deslink.getDescribedBy();
				if (isPCBADrawing(doc)) {
					if (!ECADConst.DISABLE_FOR_DESIGN_STATE.equals(doc
							.getLifeCycleState().toString())) {
						removeUsageLinkSet.add(deslink);
					}
				}
			}
			PersistenceServerHelper.manager.remove(removeUsageLinkSet);
		}
		PersistenceHelper.manager.refresh(parent);

	}

	/**
	 * 创建部件与文档的说明关系
	 * 
	 * @param document
	 * @param part
	 * @throws WTException
	 */
	public static void createReferenceLink(WTDocument document, WTPart part)
			throws WTException {
		WTPartDescribeLink wtpartdescribelink = getPartDescribeLink(part,
				document);
		if (wtpartdescribelink == null) {
			WTPartDescribeLink wtpartdescribelink1 = WTPartDescribeLink
					.newWTPartDescribeLink(part, document);
			PersistenceServerHelper.manager.insert(wtpartdescribelink1);
			wtpartdescribelink1 = (WTPartDescribeLink) PersistenceHelper.manager
					.refresh(wtpartdescribelink1);
		}
	}

	/**
	 * 获取部件与文档的说明关系
	 * 
	 * @param wtpart
	 * @param wtdocumentmaster
	 * @return
	 * @throws WTException
	 */
	public static WTPartDescribeLink getPartDescribeLink(WTPart wtpart,
			WTDocument document) throws WTException {
		QueryResult queryresult = PersistenceHelper.manager.find(
				wt.part.WTPartDescribeLink.class, wtpart,
				WTPartDescribeLink.DESCRIBED_BY_ROLE, document);
		if (queryresult == null || queryresult.size() == 0)
			return null;
		else {
			WTPartDescribeLink wtpartreferencelink = (WTPartDescribeLink) queryresult
					.nextElement();
			return wtpartreferencelink;
		}
	}

	/**
	 * 获取部件说明文档
	 * 
	 * @param part
	 * @return String
	 * @throws WTException
	 */
	public static List<WTDocument> getDocByPart(WTPart part) throws WTException {
		WTDocument document = null;
		List<WTDocument> documents = new ArrayList<>();
		QueryResult qr = StructHelper.service.navigateDescribedBy(part);
		while (qr.hasMoreElements()) {
			WTDocument master = (WTDocument) qr.nextElement();
			QueryResult qr2 = VersionControlHelper.service
					.allVersionsOf(master);
			if (qr2.hasMoreElements()) {
				document = (WTDocument) qr2.nextElement();
				if (!documents.contains(document)) {
					documents.add(document);
				}
			}
		}
		return documents;
	}

	/**
	 * 根据PCB图获取Part
	 * 
	 * @param PCBEpm
	 * @return
	 * @throws WTException
	 */
	public static Map<String, Object> getPartByPCBEPM(EPMDocument PCBEpm)
			throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		QueryResult qr = PersistenceHelper.manager.navigate(PCBEpm,
				EPMBuildRule.BUILD_TARGET_ROLE, EPMBuildRule.class, false);
		WTPart part = null;
		if (qr.hasMoreElements()) {
			EPMBuildRule buildRule = (EPMBuildRule) qr.nextElement();
			part = (WTPart) buildRule.getRoleBObject();
		}
		if (part == null) {
			map.put("error", "PCB图" + PCBEpm.getNumber()
					+ "未关联Part,无法校验Gerber文件、装配图!");
		}
		map.put("part", part);
		return map;
	}

	/**
	 * PCBA、PCB发布验证
	 * 
	 * @param wtobj
	 * @throws ChangeException2
	 * @throws WTException
	 */
	public static void checkPCBA(WTObject wtobj) throws WTException {
		//WTPart parentPart = null;
		//WTPart childPart = null;
		EPMDocument PCBEpm = null;
		//WTDocument gdoc = null;
		//WTDocument zdoc = null;
		Vector<WTPart> pcbas = new Vector<WTPart>();
		Vector<WTPart> pcbs = new Vector<WTPart>();
		Vector<EPMDocument> pcbEPMs = new Vector<EPMDocument>();
		Vector<WTDocument> gerbers = new Vector<WTDocument>();
		Vector<WTDocument> pcbaAsms = new Vector<WTDocument>();
		List<String> messageError = new ArrayList<>();

		// 获取PCBA、PCB物料组
		Map<String, List<String>> map = HistoryUtils.getClfNumber();
		List<String> listPCBA = map.get("PCBA");
		List<String> listPCB = map.get("PCB");

		// 获取受影响对象里的需校验对象
		PromotionNotice notice = (PromotionNotice) wtobj;
		QueryResult qr = MaturityHelper.service.getPromotionTargets(notice);
		while (qr.hasMoreElements()) {
			Object obj = qr.nextElement();
			if (obj instanceof WTPart) {
				WTPart part = (WTPart) obj;
				LWCStructEnumAttTemplate lwc = NodeUtil
						.getLWCStructEnumAttTemplateByPart(part);
				if (lwc == null) {
					continue;
				}
				String pn = lwc.getName();
				if (listPCBA.contains(pn)) {
					//parentPart = part;
					pcbas.add(part);
				} else if (listPCB.contains(pn)) {
					//childPart = part;
					pcbs.add(part);
				}
			} else if (obj instanceof EPMDocument) {
				EPMDocument epm = (EPMDocument) obj;
				if (isPCBEPM(epm)) {
					//PCBEpm = epm;
					pcbEPMs.add(epm);
				}
			} else if (obj instanceof WTDocument) {
				WTDocument document = (WTDocument) obj;
				if (isGerberDoc(document)) {
					//gdoc = document;
					gerbers.add(document);
				} else if (isPCBADrawing(document)) {
					//zdoc = document;
					pcbaAsms.add(document);
				}
			}
		}

		// PCBA验证
		//if (parentPart != null) {
		for (Iterator iterator = pcbas.iterator(); iterator.hasNext();) {
				WTPart part = (WTPart) iterator.next();
			// 原理图是否已发布
			String msg = checkPCBAAndPCBEpm(part, null);
			if (msg != null) {
				messageError.add(msg);
			}
		}

		// PCB验证
		//if (childPart != null) {
		for (Iterator iterator = pcbas.iterator(); iterator.hasNext();) {
			WTPart part = (WTPart) iterator.next();
			// PCB图是否已发布
			String msgError = checkPCBAAndPCBEpm(part, pcbEPMs);
			if (msgError != null) {
				messageError.add(msgError);
			}
		}

		// PCB图验证
		for (Iterator iterator = pcbEPMs.iterator(); iterator.hasNext();) {
			EPMDocument pcb = (EPMDocument) iterator.next();
			//if (PCBEpm != null) {
			// 是否已发布原理图
			String mesError = checkEPMPCBA(pcb);
			if (mesError != null) {
				messageError.add(mesError);
			}
			// 是否已发布Gerber文件、装配图
			Map<String, Object> pmap = getPartByPCBEPM(pcb);
			WTPart part = (WTPart) pmap.get("part");
			String msgError = (String) pmap.get("error");
			if (msgError != null) {
				messageError.add(msgError);
			}
			List<String> msgList = checkPCBDescribed2(part, gerbers, pcbaAsms);
			messageError.addAll(msgList);
		}

		String mError = null;
		for (String error : messageError) {
			if (mError == null) {
				mError = error;
			} else {
				mError += error;
			}
		}
		if (mError != null) {
			throw new WTException(mError);
		}
	}

	/**
	 * PCBA、PCB设计/工程变更验证
	 * 
	 * @param wtobj
	 * @throws WTException
	 */
	public static void CheckPBCAForChange(WTObject wtobj) throws WTException {
		Vector<WTPart> pcbas = new Vector<WTPart>();
		Vector<WTPart> pcbs = new Vector<WTPart>();
		Vector<EPMDocument> schEPMs = new Vector<EPMDocument>();
		Vector<EPMDocument> pcbEPMs = new Vector<EPMDocument>();
		Vector<WTDocument> gerbers = new Vector<WTDocument>();
		Vector<WTDocument> pcbaAsms = new Vector<WTDocument>();
		//WTPart pcbaPart = null;
		//WTPart pcbPart = null;
		//EPMDocument PCBAEpm = null;
		//EPMDocument PCBEpm = null;
		//WTDocument gdoc = null;
		//WTDocument zdoc = null;
		List<String> msgErrorList = new ArrayList<>();

		Map<String, List<String>> map = HistoryUtils.getClfNumber();
		List<String> listPCBA = map.get("PCBA");
		List<String> listPCB = map.get("PCB");

		// 获取受影响对象里的需校验对象
		QueryResult qr = null;
		if (wtobj instanceof WTChangeRequest2) {
			WTChangeRequest2 request2 = (WTChangeRequest2) wtobj;
			qr = ChangeHelper2.service.getChangeables(request2);
		} else if (wtobj instanceof WTChangeOrder2) {
			WTChangeOrder2 order2 = (WTChangeOrder2) wtobj;
			qr = ChangeHelper2.service.getChangeablesBefore(order2);
		}

		while (qr.hasMoreElements()) {
			Object obj = qr.nextElement();
			if (obj instanceof WTPart) {
				WTPart part = (WTPart) obj;
				LWCStructEnumAttTemplate lwc = NodeUtil
						.getLWCStructEnumAttTemplateByPart(part);
				if (lwc == null) {
					continue;
				}
				String pn = lwc.getName();
				if (listPCBA.contains(pn)) {
					//pcbaPart = part;
					pcbas.add(part);
				} else if (listPCB.contains(pn)) {
					//pcbPart = part;
					pcbs.add(part);
				}
			} else if (obj instanceof EPMDocument) {
				EPMDocument epm = (EPMDocument) obj;
				if (ECADutil.isSCHEPM(epm)) {
					//PCBAEpm = epm;
					schEPMs.add(epm);
				} else if (ECADutil.isPCBEPM(epm)) {
					//PCBEpm = epm;
					pcbEPMs.add(epm);
				}
			} else if (obj instanceof WTDocument) {
				WTDocument document = (WTDocument) obj;
				if (isGerberDoc(document)) {
					//gdoc = document;
					gerbers.add(document);
				} else if (isPCBADrawing(document)) {
					//zdoc = document;
					pcbaAsms.add(document);
				}
			}
		}

		// PCBA原理图变更验证
		for (Iterator iterator = schEPMs.iterator(); iterator.hasNext();) {
			EPMDocument sch = (EPMDocument) iterator.next();
			
			// PCBA部件是否在同一流程
			Map<String, Object> checkMap = checkEPMPCB(sch, pcbas);
			WTPart wtpart = (WTPart) checkMap.get("part");
			msgErrorList = (List<String>) checkMap.get("error");
			
		}
			


		// PCB图变更验证
		for (Iterator iterator = pcbEPMs.iterator(); iterator.hasNext();) {
			EPMDocument pcb = (EPMDocument) iterator.next();
			// PCB部件是否在同一流程
			Map<String, Object> checkMap2 = checkEPMPCB(pcb, pcbs);
			WTPart part = (WTPart) checkMap2.get("part");
			msgErrorList.addAll((List<String>) checkMap2.get("error"));

			// Gerber文件、装配图是否在同一流程
			List<String> msgs = checkPCBDescribed(part, gerbers, pcbaAsms);
			msgErrorList.addAll(msgs);
		}
		String msgError = null;
		for (String error : msgErrorList) {
			if (msgError == null) {
				msgError = error;
			} else {
				msgError += error;
			}
		}
		if (msgError != null) {
			throw new WTException(msgError);
		}
	}

	/**
	 * 原理图/PCB图是否已发布
	 * 
	 * @param part
	 * @param PCBAEpm
	 * @throws WTException
	 */
	public static String checkPCBAAndPCBEpm(WTPart part, Vector<EPMDocument> epms )
			throws WTException {
		String messageError = null;
		QueryResult qr = PartDocServiceCommand.getAssociatedCADDocuments(part);
		while (qr.hasMoreElements()) {
			EPMDocument epm = (EPMDocument) qr.nextElement();
			if (isSCHEPM(epm)
					&& !"RELEASED".equals(epm.getLifeCycleState().toString())) {
				messageError = "原理图" + epm.getNumber() + "必须先发布！";
			} else if (isPCBEPM(epm)
					&& !"RELEASED".equals(epm.getLifeCycleState().toString())) {
				if (epms == null || !epms.contains(epm)) {
					messageError = "PCB图" + epm.getNumber()
							+ "必须先发布或同在升级列表一起发布！";
				}
			}
		}
		return messageError;
	}

	/**
	 * PCB部件是否与PCBA部件在同一流程
	 * 
	 * @param part
	 * @param childPart
	 * @throws WTException
	 */
	public static String checkPCBPart(WTPart part, WTPart childPart)
			throws WTException {
		String msgError = null;
		if (childPart == null
				|| !childPart.getNumber().equals(part.getNumber())) {
			msgError = "PCB部件【" + part.getNumber() + "】未添加进受影响对象！";
		}
		return msgError;
	}

	/**
	 * PCB图是否与PCB部件在同一流程
	 * 
	 * @param part
	 * @param PCBEpm
	 * @throws WTException
	 */
	public static String checkPCBEpm(WTPart part, EPMDocument PCBEpm)
			throws WTException {
		String msgError = null;
		QueryResult qr = PartDocServiceCommand.getAssociatedCADDocuments(part);
		while (qr.hasMoreElements()) {
			EPMDocument epm = (EPMDocument) qr.nextElement();
			if (isPCBEPM(epm)) {
				if (PCBEpm == null || !PCBEpm.equals(epm)) {
					msgError = "PCB图【" + epm.getNumber() + "】未添加进受影响对象！";
				}
			}
		}
		return msgError;
	}
	
	/**
	 * PCB图、原理图变更时其关联部件是否一同变更
	 * 
	 * @param epm
	 *            PCB图、原理图
	 * @param wtpart
	 *            受影响对象
	 * @return PCB图、原理图关联part
	 * @throws WTException
	 */
	public static Map<String, Object> checkEPMPCB(EPMDocument epm, Vector<WTPart> pcbas)
			throws WTException {
		Map<String, Object> map = new HashMap<>();
		List<String> errorList = new ArrayList<>();
		QueryResult qr = PersistenceHelper.manager.navigate(epm,
				EPMBuildRule.BUILD_TARGET_ROLE, EPMBuildRule.class, false);
		WTPart part = null;
		while (qr.hasMoreElements()) {
			EPMBuildRule buildRule = (EPMBuildRule) qr.nextElement();
			part = (WTPart) buildRule.getRoleBObject();
			break;
		}
		if (part == null) {
			errorList.add("图纸【" + epm.getNumber() + "】未关联部件!");
		}
		if (pcbas == null || !pcbas.contains(part)) {
			errorList.add("图纸【" + epm.getNumber() + "】关联的部件【"
					+ part.getNumber() + "】未添加进受影响对象！");
		}
		map.put("error", errorList);
		map.put("part", part);
		return map;
	}

	/**
	 * PCB图、原理图变更时其关联部件是否一同变更
	 * 
	 * @param epm
	 *            PCB图、原理图
	 * @param wtpart
	 *            受影响对象
	 * @return PCB图、原理图关联part
	 * @throws WTException
	 */
	public static Map<String, Object> checkEPMPCB(EPMDocument epm, WTPart wtpart)
			throws WTException {
		Map<String, Object> map = new HashMap<>();
		List<String> errorList = new ArrayList<>();
		QueryResult qr = PersistenceHelper.manager.navigate(epm,
				EPMBuildRule.BUILD_TARGET_ROLE, EPMBuildRule.class, false);
		WTPart part = null;
		while (qr.hasMoreElements()) {
			EPMBuildRule buildRule = (EPMBuildRule) qr.nextElement();
			part = (WTPart) buildRule.getRoleBObject();
			break;
		}
		if (part == null) {
			errorList.add("图纸【" + epm.getNumber() + "】未关联部件!");
		}
		if (wtpart == null || !part.equals(wtpart)) {
			errorList.add("图纸【" + epm.getNumber() + "】关联的部件【"
					+ part.getNumber() + "】未添加进受影响对象！");
		}
		map.put("error", errorList);
		map.put("part", part);
		return map;
	}

	/**
	 * PCB图单独发布时其关联的原理图必须先发布
	 * 
	 * @param epm
	 *            PCB图
	 * @throws WTException
	 */
	public static String checkEPMPCBA(EPMDocument epm) throws WTException {
		EPMDocument SCHEPM = null;
		String msg = null;
		String temp = "";
		boolean hasReleasedSCH = false;
		QueryResult qr = PersistenceHelper.manager
				.navigate(epm, EPMReferenceLink.REFERENCES_ROLE,
						EPMReferenceLink.class, false);
		while (qr.hasMoreElements()) {
			EPMReferenceLink referenceLink = (EPMReferenceLink) qr
					.nextElement();
			EPMDocumentMaster master = (EPMDocumentMaster) referenceLink
					.getRoleBObject();
			QueryResult qr2 = VersionControlHelper.service
					.allVersionsOf(master);
			while (qr2.hasMoreElements()) {
				EPMDocument epmDocument = (EPMDocument) qr2.nextElement();
				if (isSCHEPM(epmDocument)) {
					SCHEPM = epmDocument;					
				}
			}
			if (!"RELEASED".equals(SCHEPM.getLifeCycleState().toString())) {
				temp = temp + "原理图【" + SCHEPM.getNumber() + "】必须先发布！";
			}else{
				hasReleasedSCH = true;
			}
		}
		if (SCHEPM == null) {
			msg = "没有与PCB图【" + epm.getNumber() + "】所关联的原理图！";
		}else{
			if(!hasReleasedSCH){
				msg = temp;
			}
		}
		return msg;
	}
	

	/**
	 * 根据原理图获取关联的PCB图
	 * @param epm
	 * @return
	 * @throws WTException
	 */
	public static List<EPMDocument> getPCBsBySCH(EPMDocument epm) throws WTException {
		List<EPMDocument> pcbs = new ArrayList<EPMDocument>();
		if(epm == null){
			return pcbs;
		}
		QueryResult qr = EPMStructureHelper.service.navigateReferencedBy((DocumentMaster) epm.getMaster(), null, true);
		qr = new LatestConfigSpec().process(qr);
		while (qr.hasMoreElements()) {
			Object refDoc = qr.nextElement();
			if(refDoc instanceof EPMDocument){
				EPMDocument epmDocument = (EPMDocument)refDoc;
				if(isPCBEPM(epmDocument)){
					pcbs.add(epmDocument);
				}
			}				
		}		
		return pcbs;
	}

	/**
	 * PCB Gerber文件、装配图与PCB是否在同一流程
	 * 
	 * @param part
	 *            PCB-Part
	 * @param gdoc
	 *            Gerber文件
	 * @param zdoc
	 *            装配图
	 * @throws WTException
	 */
	public static List<String> checkPCBDescribed(WTPart part, Vector<WTDocument> gdoc,
			Vector<WTDocument> zdoc) throws WTException {
		List<String> messageError = new ArrayList<>();
		List<WTDocument> documents = ECADutil.getDocByPart(part);
		if (documents.size() == 0) {
			messageError.add("PCB【" + part.getNumber() + "】未关联Gerber文件和装配图！");
		}
		boolean haveGerber = false;
		boolean havePCBADrawing = false;
		for (WTDocument document : documents) {
			if (isGerberDoc(document)) {
				if (gdoc == null || !gdoc.contains(document)) {
					messageError.add("Gerber文件【" + document.getNumber()
							+ "】未添加进受影响对象！");
				}
				haveGerber = true;
			}
			if (isPCBADrawing(document)) {
				if (zdoc == null || !zdoc.contains(document)) {

					messageError.add("装配图【" + document.getNumber()
							+ "】未添加进受影响对象！");

				}
				havePCBADrawing = true;
			}
		}
		if (!haveGerber && documents.size() != 0) {
			messageError.add("PCB【" + part.getNumber() + "】未关联Gerber文件！");
		} else if (!havePCBADrawing && documents.size() != 0) {
			messageError.add("PCB【" + part.getNumber() + "】未关联装配图文件！");
		}
		return messageError;
	}

	/**
	 * 发布PCB图必须先发布Gerber文件、装配图
	 * 
	 * @param part
	 * @throws WTException
	 */
	public static List<String> checkPCBDescribed2(WTPart part, Vector<WTDocument> gdoc,
			 Vector<WTDocument> zdoc) throws WTException {
		List<String> msgerror = new ArrayList<String>();
		List<WTDocument> documents = ECADutil.getDocByPart(part);
		if (documents.size() == 0) {
			msgerror.add("PCB【" + part.getNumber() + "】未关联Gerber文件和装配图！");
		}
		boolean haveGerber = false;
		boolean havePCBADrawing = false;
		for (WTDocument document : documents) {
			if (isGerberDoc(document)) {
				if (!ECADConst.RELEASED_STATE.equals(document
						.getLifeCycleState().toString())) {
					if (gdoc == null || !gdoc.contains(document)) {
						msgerror.add("Gerber文件【" + document.getNumber()
								+ "】必须先发布或同在升级列表，一起发布！");
					}
				}
				haveGerber = true;
			} else if (isPCBADrawing(document)) {
				if (!ECADConst.RELEASED_STATE.equals(document
						.getLifeCycleState().toString())) {
					if (zdoc == null || !zdoc.contains(document)) {
						msgerror.add("PCBA装配图【" + document.getNumber()
								+ "】必须先发布或同在升级列表，一起发布！");
					}
				}
				havePCBADrawing = true;
			}
		}
		if (!haveGerber && documents.size() != 0) {
			msgerror.add("PCB【" + part.getNumber() + "】未关联Gerber文件！");
		} else if (!havePCBADrawing && documents.size() != 0) {
			msgerror.add("PCB【" + part.getNumber() + "】未关联装配图文件！");
		}
		return msgerror;
	}

	/**
	 * 判断部件是否为PCBA部件
	 * 
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public static boolean isPCBA(WTPart part) throws WTException {
		if (part == null) {
			throw new WTException(CLASSNAME + "\tPCBA部件为NULL");
		}
		// 获取PCBA、PCB物料组
		Map<String, List<String>> map = HistoryUtils.getClfNumber();
		List<String> listPCBA = map.get("PCBA");
		LWCStructEnumAttTemplate cls = NodeUtil
				.getLWCStructEnumAttTemplateByPart(part);
		if (cls != null) {
			String clsName = cls.getName();
			if (listPCBA.contains(clsName)) {
				return true;
			}
		} else {
			throw new WTException("物料" + part.getNumber() + "没有选择分类。");
		}
		return false;

	}

	/**
	 * 判断部件是否为PCB部件
	 * 
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public static boolean isPCB(WTPart part) throws WTException {
		if (part == null) {
			throw new WTException(CLASSNAME + "\tPCB部件为NULL");
		}
		// 获取PCBA、PCB物料组
		Map<String, List<String>> map = HistoryUtils.getClfNumber();
		List<String> listPCB = map.get("PCB");
		LWCStructEnumAttTemplate cls = NodeUtil
				.getLWCStructEnumAttTemplateByPart(part);
		if (cls != null) {
			String clsName = cls.getName();
			if (listPCB.contains(clsName)) {
				return true;
			}
		} else {
			throw new WTException("物料" + part.getNumber() + "没有选择分类。");
		}
		return false;
	}

	/**
	 * 根据PCBA部件获取其下挂的PCB子件
	 * 
	 * @param pcba
	 * @return
	 * @throws WTException
	 */
	public static WTPart getPCBByPCBA(WTPart pcba) throws WTException {
		if (pcba == null) {
			throw new WTException(CLASSNAME + "\tPCBA部件为NULL");
		}
		// 获取PCBA、PCB物料组
		Map<String, List<String>> map = HistoryUtils.getClfNumber();
		List<String> listPCBA = map.get("PCBA");
		List<String> listPCB = map.get("PCB");

		LWCStructEnumAttTemplate pcbaCls = NodeUtil
				.getLWCStructEnumAttTemplateByPart(pcba);
		if (pcbaCls != null) {
			String clsName = pcbaCls.getName();
			if (listPCBA.contains(clsName)) {
				List<WTPart> parts = ECADutil.getAllChildPart(pcba);
				for (WTPart part : parts) {
					LWCStructEnumAttTemplate pcbCls = NodeUtil
							.getLWCStructEnumAttTemplateByPart(part);
					if (pcbaCls != null) {
						String pcbClsName = pcbCls.getName();
						if (listPCB.contains(pcbClsName)) {
							return part;
						}
					}
				}
			} else {
				System.out.println(pcba.getNumber() + "不是PCBA部件。");
				return null;
			}
		} else {
			throw new WTException("物料" + pcba.getNumber() + "没有选择分类。");
		}

		return null;
	}

	/**
	 * 判断文档是否为PCBA装配图文档
	 * 
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public static boolean isPCBADrawing(WTDocument doc) throws WTException {
		if (doc != null) {
			String type = getStrSplit(doc);
			if (type.equalsIgnoreCase(ECADConst.ASSEMBLYDRAWING)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断文档是否为PCBA Gerber文件
	 * 
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public static boolean isGerberDoc(WTDocument doc) throws WTException {
		if (doc != null) {
			String type = getStrSplit(doc);
			if (type.equalsIgnoreCase(ECADConst.GERBERTYPE)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 电子物料提交时校对角色是否包含ECAD工程师组
	 * 
	 * @param self
	 * @throws WTException
	 */
	public static void checkRole(Object self, WTObject wtobj)
			throws WTException {
		if (wtobj instanceof PromotionNotice) {
			PromotionNotice notice = (PromotionNotice) wtobj;
			if (notice != null) {
				QueryResult qr = MaturityHelper.service
						.getPromotionTargets(notice);
				while (qr.hasMoreElements()) {
					Object obj = qr.nextElement();
					if (obj instanceof WTPart) {
						WTPart part = (WTPart) obj;
						if (isElectronicPart(part)) {
							boolean flag = false;
							if (self instanceof ObjectReference) {
								ObjectReference objectReference = (ObjectReference) self;
								flag = WorkflowHelper.checkRoleECADEngineer(
										part, objectReference,
										ECADConst.JAODUIZHE);
							}
							if (!flag) {
								throw new WTException(
										"升级列表包含电子物料，请在校对者中添加ECAD工程师成员！");
							}
						}
					}
				}
			}
		}
	}

	public static String getFTPInfo() throws JSONException {
		JSONObject json = new JSONObject();
		json.put("FTPip", FTPConfigProperties.getFtpURL());
		json.put("FTPrelPath", FTPConfigProperties.getConfigFtpPath());
		json.put("FTPusername", FTPConfigProperties.getFtpUser());
		json.put("FTPpassword", FTPConfigProperties.getFtpPwd());
		json.put("FTPReserveIp", FTPConfigProperties.getFtpURL1());
		return json.toJSONString();
	}

	/**
	 * 电子物料
	 * 
	 * @param wtPart
	 * @return
	 * @throws WTException
	 */
	public static boolean isElectronicPart(WTPart wtPart) throws WTException {
		boolean flag = false;
		if (wtPart != null) {
			IBAUtility utility = new IBAUtility(wtPart);
			String assembleyType = utility.getIBAValue("Assembly_Type");
			if (assembleyType != null && !assembleyType.equals("NA")
					&& !assembleyType.isEmpty()) {
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * BOM发布流程仅原理图、PCB图发布时角色成员校验
	 * @param self
	 * @return
	 */
	public static StringBuffer validate(ObjectReference self) {
		StringBuffer message = new StringBuffer();


		try {
			WTPrincipal currentUser = SessionHelper.getPrincipal();
			WfAssignedActivity acivity = (WfAssignedActivity) self.getObject();
			WfProcess process = acivity.getParentProcess();
			
			Enumeration collator = process.getPrincipals(Role.toRole(RoleName.COLLATOR));
			Enumeration approver = process.getPrincipals(Role.toRole(RoleName.APPROVER));


			if (!collator.hasMoreElements()) {
				message.append("校对者角色不能为空，请在设置参与者中选择校对者！ \n");
			} else if (WorkflowUtil.isSelectSelf(collator, currentUser)) {
				message.append("校对者角色不能选择自己！ \n");
			}

			if (!approver.hasMoreElements()) {
				message.append("批准者角色不能为空，请在设置参与者中选择批准者！ \n");
			} else if (WorkflowUtil.isSelectSelf(approver, currentUser)) {
				message.append("批准者角色不能选择自己！ \n");
			}
			
		} catch (WTException e) {
			// TODO Auto-generated catch block
			message.append(e.getMessage());
			e.printStackTrace();

		}

		return message;
	}


	/**
	 * 判断BOM发布流程中是否只包含原理图或PCB图
	 * @param pbo
	 * @return
	 * @throws MaturityException
	 * @throws WTException
	 */
	public static boolean isOnlySCHOrPCB(WTObject pbo) throws MaturityException, WTException{
		PromotionNotice notice = (PromotionNotice) pbo;
		QueryResult qr = MaturityHelper.service.getPromotionTargets(notice);
		boolean result = true;
		while (qr.hasMoreElements()) {
			Object obj = qr.nextElement();
			if(obj instanceof EPMDocument){
				EPMDocument epm = (EPMDocument) obj;
				if(!(isPCBEPM(epm)||isSCHEPM(epm))){
					result = false;
					return result;
				}
			}else{
				result = false;
				return result;
			}
		}
		return result;
	}
	
	/**
	 * 检查Gerber文件与PCBA装配图对应的原理图是否已发布
	 * @param pbo
	 * @throws WTException
	 */
	public static void checkSCHByGerberOrAssembly(WTObject pbo) throws WTException{
		
		if(pbo instanceof WTDocument){
			WTDocument doc = (WTDocument) pbo;
			if(isPCBADrawing(doc)||isGerberDoc(doc)){
				boolean isSCHReleased = false;
				WTPart part = getRelationPartByDescDoc(doc);
				if(part != null){
					if(isPCB(part)){
						QueryResult qr = WTPartHelper.service.getUsedByWTParts(part.getMaster());
						boolean hasSCHPart = false;
						while(qr.hasMoreElements()){
							WTPart schPart = (WTPart) qr.nextElement();
							hasSCHPart = true;
							QueryResult epmqr = PartDocServiceCommand.getAssociatedCADDocuments(schPart);
							boolean hasSCHEPM = false;
							while (epmqr.hasMoreElements()) {
								EPMDocument epm = (EPMDocument) epmqr.nextElement();
								hasSCHEPM = true;
								if (isSCHEPM(epm)) {
									if(ECADConst.RELEASED_STATE.equalsIgnoreCase(epm.getLifeCycleState().toString())){
										isSCHReleased = true;
									}
								}
							}
							if(!hasSCHEPM){
								throw new WTException("文档"+doc.getNumber()+"没有与其关联的PCB对应的原理图或者未建立关联！");
							}
						}
						if(!hasSCHPart){
							throw new WTException("文档"+doc.getNumber()+"没有与其关联的PCB对应的PCBA部件或者未建立关联！");
						}
					}else{
						throw new WTException("文档"+doc.getNumber()+"没有关联PCB部件！");
					}
				}else{
					throw new WTException("文档"+doc.getNumber()+"没有关联PCB部件！");
				}
				if(!isSCHReleased){
					 throw new WTException("文档"+doc.getNumber()+"对应的原理图必须先发布！");
				}
			}
		}
	}
	
	/**
	 * 获取说明文档关联的物料
	 * @param doc
	 * @return
	 * @throws WTException
	 */
	public static WTPart getRelationPartByDescDoc(WTDocument doc) throws WTException{
		QueryResult relatepartlist = PartDocServiceCommand.getAssociatedDescParts(doc);
		relatepartlist = new LatestConfigSpec().process(relatepartlist);
		while (relatepartlist.hasMoreElements()) {
			return (WTPart) relatepartlist.nextElement();
		}
		return null;
	}
}
