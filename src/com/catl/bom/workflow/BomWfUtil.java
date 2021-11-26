package com.catl.bom.workflow;

import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import wt.change2.ChangeException2;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.build.EPMBuildRule;
import wt.epm.structure.EPMStructureHelper;
import wt.fc.ObjectReference;
import wt.fc.ObjectVector;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.WTReference;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTCollection;
import wt.fc.collections.WTHashSet;
import wt.fc.collections.WTSet;
import wt.httpgw.GatewayAuthenticator;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainer;
import wt.inf.library.WTLibrary;
import wt.maturity.MaturityException;
import wt.maturity.MaturityHelper;
import wt.maturity.PromotionNotice;
import wt.maturity.PromotionTarget;
import wt.method.RemoteMethodServer;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.pdmlink.PDMLinkProduct;
import wt.pds.StatementSpec;
import wt.project.Role;
import wt.query.CompositeWhereExpression;
import wt.query.ConstantExpression;
import wt.query.LogicalOperator;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.TableColumn;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.team.Team;
import wt.team.TeamException;
import wt.team.WTRoleHolder2;
import wt.util.WTException;
import wt.util.WTMessage;
import wt.util.WTPropertyVetoException;
import wt.vc.Versioned;
import wt.vc.baseline.BaselineHelper;
import wt.vc.config.ConfigException;
import wt.vc.config.ConfigHelper;
import wt.vc.config.ConfigSpec;
import wt.vc.config.LatestConfigSpec;
import wt.vc.struct.StructHelper;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfVotingEventAudit;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;

import com.catl.change.ChangeUtil;
import com.catl.common.constant.ChangeState;
import com.catl.common.constant.PartState;
import com.catl.common.constant.Ranking;
import com.catl.common.constant.RoleName;
import com.catl.common.constant.TypeName;
import com.catl.common.util.CatlConstant;
import com.catl.common.util.ContainUtil;
import com.catl.common.util.DocUtil;
import com.catl.common.util.ElecSignConstant;
import com.catl.common.util.EpmUtil;
import com.catl.common.util.GenericUtil;
import com.catl.common.util.IBAUtil;
import com.catl.common.util.PartUtil;
import com.catl.common.util.PropertiesUtil;
import com.catl.common.util.ResultMessage;
import com.catl.common.util.TypeUtil;
import com.catl.common.util.WorkflowUtil;
import com.catl.common.util.change.ECAUtil;
import com.catl.ecad.utils.WorkflowHelper;
import com.catl.loadData.IBAUtility;
import com.catl.part.PartConstant;
import com.catl.part.PartWorkflowUtil;
import com.catl.promotion.util.PromotionUtil;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.netmarkets.workflow.NmWorkflowHelper;
import com.ptc.windchill.enterprise.change2.commands.RelatedChangesQueryCommands;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;
import com.ptc.windchill.enterprise.team.server.TeamCCHelper;

public class BomWfUtil {

	private static Logger log = Logger.getLogger(BomWfUtil.class.getName());

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		GatewayAuthenticator auth = new GatewayAuthenticator();
		auth.setRemoteUser("wcadmin");
		rms.setAuthenticator(auth);

		String oidString = "OR:wt.part.WTPart:396494";
		Persistable persistable = getPersistableByOid(oidString);
		WTPart part = (WTPart) persistable;
		WTArrayList parent = new WTArrayList();
		parent.add(part);
		System.out.println("parent =" + part.getNumber());
		ArrayList<WTPart> children = new ArrayList();
		ArrayList<WTPart> children2 = new ArrayList();
		try {
			int level = 0;
			visitBomTree(parent, children, level);

			for (WTPart c : children) {
				System.out.println("Child = " + c.getNumber());
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// refresh PromotionNotice targets if the targets have been modified
	public static void refreshPromotionTargets(PromotionNotice pn) throws WTException {

		try {
			QueryResult qr = MaturityHelper.service.getPromotionTargets(pn);
			log.debug("qr.size===" + qr.size());
			WTSet old_set = new WTHashSet();
			WTSet new_set = new WTHashSet();
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				old_set.add(obj);
				if (obj instanceof WTPart) {
					WTPart oldpart = (WTPart) obj;
					WTPart newPart = PartUtil.getLastestWTPartByNumber(oldpart.getNumber());
					new_set.add(newPart);
				}
				if (obj instanceof WTDocument) {
					WTDocument olddoc = (WTDocument) obj;
					WTDocument newdoc = DocUtil.getLatestWTDocument(olddoc.getNumber());
					new_set.add(newdoc);
				}
				if (obj instanceof EPMDocument) {
					EPMDocument epmdoc = (EPMDocument) obj;
					EPMDocument newepmdoc = DocUtil.getLastestEPMDocumentByNumber(epmdoc.getNumber());
					new_set.add(newepmdoc);
				} else {
					new_set.add(obj);
				}

			}
			wt.maturity.MaturityBaseline bl = pn.getConfiguration();
			log.debug("start to delete----->");
			MaturityHelper.service.deletePromotionTargets(pn, old_set);
			BaselineHelper.service.removeFromBaseline(old_set, bl);
			old_set = null;

			log.debug("start to add new ----->");
			MaturityHelper.service.savePromotionTargets(pn, new_set);
			BaselineHelper.service.addToBaseline(new_set, bl);
			log.debug("end to add----->");
			new_set = null;

		} catch (Exception _wte) {
			log.debug(pn.getNumber() + "refresh pn EXception!!");
		}
	}

	public static void refreshPromotableObject(WTObject pbo) throws WTException {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		log.debug("start to refresh promotiontarget--------------->>>>>");
		try {
			if (pbo instanceof PromotionNotice) {
				PromotionNotice promotion = (PromotionNotice) pbo;

				WTSet addSeeds = new WTHashSet();
				WTSet addTargets = new WTHashSet();

				WTSet rmSeeds = new WTHashSet();
				WTSet rmTargets = new WTHashSet();
				Set<WTObject> removeSeeds = getSeeds(promotion);
				// QueryResult qrseed =
				// MaturityHelper.service.getPromotionSeeds(promotion);
				QueryResult qrtarget = MaturityHelper.service.getPromotionTargets(promotion);
				// Set<WTPart> removeTargets = getTargets(promotion);
				log.debug("remove seeds==========" + removeSeeds.size());
				log.debug("remove Targets ==========" + qrtarget.size());
				WTPart newPart = null;
				WTDocument newdoc = null;
				EPMDocument newepm = null;
				// for (WTPart part : removeTargets) {
				while (qrtarget.hasMoreElements()) {
					Object object = (Object) qrtarget.nextElement();
					if (object instanceof WTPart) {
						WTPart part = (WTPart) object;
						newPart = getPartByMasterAndView((WTPartMaster) part.getMaster(), part.getViewName());
						if (removeSeeds.contains(part)) {
							addSeeds.add(newPart);
						}
						addTargets.add(newPart);
					}
					if (object instanceof WTDocument) {
						WTDocument doc = (WTDocument) object;
						newdoc = DocUtil.getLatestWTDocument(doc.getNumber());
						if (removeSeeds.contains(doc)) {
							addSeeds.add(newdoc);
						}
						addTargets.add(newdoc);
					}
					if (object instanceof EPMDocument) {
						EPMDocument epm = (EPMDocument) object;
						newepm = DocUtil.getLastestEPMDocumentByNumber(epm.getNumber());
						if (removeSeeds.contains(epm)) {
							addSeeds.add(newepm);
						}
						addTargets.add(newepm);
					}

				}
				if (addTargets.size() > 0) {
					rmSeeds.addAll(removeSeeds);
					rmTargets.addAll(qrtarget);
					MaturityHelper.service.deletePromotionSeeds(promotion, rmSeeds);
					removeTargets(promotion, rmTargets);
				}
				log.debug("add seeds=======" + addSeeds.size());
				log.debug("add Targets =======" + addTargets.size());
				if (addSeeds.size() > 0) {
					MaturityHelper.service.savePromotionSeeds(promotion, addSeeds);
				}
				if (addTargets.size() > 0) {
					addToTargets(promotion, addTargets);
				}
			}
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}

	}

	public static WTPart getPartByMasterAndView(WTPartMaster master, String viewName) throws WTException {
		View view = ViewHelper.service.getView(viewName);
		if (master == null || view == null) {
			return null;
		}

		WTPart part = null;
		QuerySpec qs = new QuerySpec(WTPart.class);
		SearchCondition sc = new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL, master.getNumber());
		qs.appendWhere(sc, new int[] { 0 });
		qs.appendAnd();
		sc = new SearchCondition(WTPart.class, "view.key.id", SearchCondition.EQUAL, view.getPersistInfo().getObjectIdentifier().getId());
		qs.appendWhere(sc, new int[] { 0 });
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		qr = new LatestConfigSpec().process(qr);
		if (qr.hasMoreElements()) {
			part = (WTPart) qr.nextElement();
		}
		return part;
	}

	public static Set<WTObject> getSeeds(PromotionNotice pn) throws WTException {
		Set<WTObject> set = new HashSet<WTObject>();
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			QueryResult qr = MaturityHelper.service.getPromotionSeeds(pn);
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();

				set.add((WTObject) obj);

			}
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return set;
	}

	public static Set<WTPart> getTargets(PromotionNotice pn) throws WTException {
		Set<WTPart> set = new HashSet<WTPart>();
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			QueryResult qr = MaturityHelper.service.getPromotionTargets(pn);
			log.debug("promotion qr===========" + qr.size());
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				if (obj instanceof WTPart) {
					set.add((WTPart) obj);
				}
			}
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return set;
	}
	
	public static Set<WTPartUsageLink> getPTargetsLink(PromotionNotice pn) throws WTException {
		Set<WTPartUsageLink> set = new HashSet<WTPartUsageLink>();
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			QueryResult qr = MaturityHelper.service.getPromotionTargets(pn);
			log.debug("promotion qr===========" + qr.size());
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				if (obj instanceof WTPart) {
					WTPart part=(WTPart) obj;
					if(part.getNumber().startsWith("P")){
						set.addAll(PromotionUtil.getOneLevelChildLink(part));
					}
				}
			}
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return set;
	}

	public static void addToTargets(PromotionNotice pn, WTSet targets) throws WTException {
		if (targets != null && targets.size() > 0) {
			try {
				MaturityHelper.service.savePromotionTargets(pn, targets);
			} catch (WTPropertyVetoException e) {
				e.printStackTrace();
				throw new WTException(e, e.getLocalizedMessage());
			}
			BaselineHelper.service.addToBaseline(targets, pn.getConfiguration());
		}
	}

	public static void removeTargets(PromotionNotice pn, WTSet targets) throws WTException {
		if (targets != null && targets.size() > 0) {
			MaturityHelper.service.deletePromotionTargets(pn, targets);
			BaselineHelper.service.removeFromBaseline(targets, pn.getConfiguration());
		}
	}

	public static Persistable getPersistableByOid(String oid) {
		Persistable obj = null;
		try {
			ReferenceFactory referencefactory = new ReferenceFactory();
			WTReference wtreference = referencefactory.getReference(oid);
			if (wtreference.getObject() != null) {
				obj = wtreference.getObject();
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return obj;
	}

	public static Boolean checkifDesigner(RevisionControlled object2, PromotionNotice pn) {
		Boolean isdesginerrole = false;
		String pncreator = pn.getCreatorName();
		Role role = Role.toRole(RoleName.DESIGNER);
		if (!pncreator.endsWith(object2.getCreatorName())) {
			Team team2 = null;
			try {
				List<WTUser> users = WorkflowHelper.getRoleUsers(object2, RoleName.DESIGNER);
				for(WTUser user:users){
					if(user.getName().equalsIgnoreCase(pncreator)){
						isdesginerrole = true;
					}
				}
				/*team2 = (Team) TeamHelper.service.getTeam(object2);
				if (team2 != null) {
					Enumeration enumPrin = team2.getPrincipalTarget(role);
					log.debug("design role people===" + enumPrin);
					while (enumPrin.hasMoreElements()) {
						WTPrincipalReference tempPrinRef = (WTPrincipalReference) enumPrin.nextElement();
						WTPrincipal principal = tempPrinRef.getPrincipal();
						log.debug("design role people name===" + principal.getName());
						
						if (principal.getName().equals(pncreator)) {
							isdesginerrole = true;
						}
					}
				}*/
			} catch (TeamException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (WTException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		} else {
			isdesginerrole = true;
		}
		return isdesginerrole;
	}
	/**
	 * 检查工作流 该方法可扩张添加校验逻辑
	 * @param pn
	 * @param self
	 * @return
	 * @throws WTException
	 */
	public static StringBuffer check(PromotionNotice pn, ObjectReference self) throws WTException{
		StringBuffer message = new StringBuffer();
		QueryResult resulttargets = MaturityHelper.service.getBaselineItems(pn);
		WfAssignedActivity acivity = (WfAssignedActivity) self.getObject();
		WfProcess process = acivity.getParentProcess();
		ObjectVector obv = null;
		if(resulttargets != null){
			obv = resulttargets.getObjectVector();
			System.out.println("obv size 1..........\t"+obv.size());
		}
		
		boolean isChecked = false;//是否判断过(工程变更 会签者必须选择一个SQE会签组中的人员 )
		boolean isHasDrawing = false;//是否有图纸
		while (resulttargets.hasMoreElements()) {
			Persistable object = (Persistable) resulttargets.nextElement();
			RevisionControlled revisionControlled = (RevisionControlled) object;
			String version = revisionControlled.getVersionIdentifier().getValue();
			if (!isChecked) {
				if(revisionControlled instanceof WTDocument){
					WTDocument doc = (WTDocument)revisionControlled;
					String createType = TypeIdentifierUtility.getTypeIdentifier(doc).getTypename();
					if (createType.endsWith(TypeName.doc_type_autocadDrawing)){
						isHasDrawing = true;
						if(!version.startsWith("1") && !version.startsWith("A") && ECAUtil.checkExistECA(revisionControlled)){//是否在ECA中
							isChecked = true;
							checkSelectRightSQE(message, process, pn.getContainer());//是否选择了正确的人员SQE
						}
					}
						
				}else if(revisionControlled instanceof EPMDocument){
					EPMDocument epm = (EPMDocument)revisionControlled;
					Boolean iscaddrawing = epm.getDocType().toString().equals("CADDRAWING");
					if (iscaddrawing){
						isHasDrawing = true;
						if(!version.startsWith("1") && !version.startsWith("A") && ECAUtil.checkExistECA(revisionControlled)){//是否在ECA中
							isChecked = true;
							checkSelectRightSQE(message, process, pn.getContainer());//是否选择了正确的人员SQE
						}
					}
				}else if(revisionControlled instanceof WTPart){
					WTPart part = (WTPart) revisionControlled;
					QueryResult parentdesDocResult =PartDocServiceCommand.getAssociatedDescribeDocuments(part);
					while (parentdesDocResult.hasMoreElements()) {
						WTDocument document = (WTDocument) parentdesDocResult.nextElement();
						TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(document);
						String doctype = ti.getTypename();
						if (doctype.endsWith(TypeName.doc_type_autocadDrawing)&&
								!document.getState().toString().equalsIgnoreCase(PartState.RELEASED)) {
							if(!obv.contains(document)){
								message.append("\n物料【"+part.getNumber()+"】关联的AutoCAD图纸【"+document.getNumber()+"】必须是已发布状态或者请添加到流程中与物料一同发布！");
							}
						}else if (doctype.endsWith(TypeName.softwareDoc)&&
								!document.getState().toString().equalsIgnoreCase(PartState.RELEASED)) {
							if(!obv.contains(document)){
								message.append("\n物料【"+part.getNumber()+"】关联的软件文档【"+document.getNumber()+"】必须是已发布状态或者请添加到流程中与物料一同发布！");
							}
						}
					}//End of List AutoCAD 
					
					
					QueryResult epmqr =PartDocServiceCommand.getAssociatedCADDocuments(part);
					while (epmqr.hasMoreElements()) {
						Object object2 = (Object) epmqr.nextElement();
		                if (object2 instanceof EPMDocument) {
		                	EPMDocument epmdoc = (EPMDocument)object2;
		                	if (!epmdoc.getState().toString().equalsIgnoreCase(PartState.RELEASED)) {
		                		System.out.println("obv size 2..........\t"+obv.size());
		                		if (!obv.contains(epmdoc)) {
		                			message.append("\n物料【"+part.getNumber()+"】关联的3D/2D图纸【"+epmdoc.getNumber()+"】必须是已发布状态或者请添加到流程中与物料一同发布！");
		                		}
		                	}
		                }
					}//End of List EPM
					
				}//End of WTPart
					
			}
			String number = BomWfUtil.getObjectnumber(revisionControlled);
			String checkGroup = PropertiesUtil.getValueByKey("BOM_WorkFlow_SE_CHECK");
			for(String group : checkGroup.split(",")){
				if(number.startsWith(group)){
					Enumeration product_data_engingeer = process.getPrincipals(Role.toRole(RoleName.SYSTEM_ENGINEER));
					if(!product_data_engingeer.hasMoreElements()){
						message.append("\n包含物料组"+checkGroup+"的发布流程必须选择“系统工程师");
					}
					break;
				}
			}
//			if(number.indexOf("-M")>-1){
//				String suffix=number.split("-")[1];
//				if(suffix.length()==8){//衍生PN
//					Enumeration product_data_engingeer = process.getPrincipals(Role.toRole(RoleName.SYSTEM_ENGINEER));
//					if(!product_data_engingeer.hasMoreElements()){
//						message.append("衍生PN的发布流程必须选择“系统工程师”");
//					}
//				}
//			}
		}
		//说明有图纸变更 通知IQC
		if(isHasDrawing){
			String usernames = PropertiesUtil.getValueByKey("IQC_INFORM");
			for(String username : usernames.split(",")){
				addPrincipalToProcessRoleByusername(process,Role.toRole(RoleName.INFORM_THE_STAFF),username);
			}
		}
		return message;
	}

	private static void checkSelectRightSQE(StringBuffer message, WfProcess process, WTContainer container) throws WTException {
		boolean isContainSQE = false;
		Enumeration countersign_people = process.getPrincipals(Role.toRole(RoleName.COUNTERSIGN_PEOPLE));
		List<WTPrincipal> users = ContainUtil.findContainerTeamUserByRolenameKey(container, RoleName.SUPPLIER_UQALITY_READ+","+RoleName.SUPPLIER_UQALITY_MANAGEMENT);
		while(countersign_people.hasMoreElements()){
			Object obj = countersign_people.nextElement();
			if (obj instanceof WTPrincipalReference) {
				WTPrincipalReference ref = (WTPrincipalReference)obj;
				WTPrincipal user = ref.getPrincipal();
				if(users.contains(user)){
					isContainSQE = true;
					break;
				}
			}
		}
		if(!isContainSQE)
			message.append("\n工程变更(图纸变化)发布流程中‘会签者’角色必须选择一个 '供应商质量管理工程师'或者'供应商质量管理浏览者' 角色");
	}

	public static StringBuilder checkReviewObjects(WTObject pbo, Boolean isworkflow) throws Exception {

		StringBuilder message = new StringBuilder();
		PromotionNotice pnNotice = (PromotionNotice) pbo;
		WTContainer pncontainer = pnNotice.getContainer();
		// QueryResult resulttargets =
		// WorkflowUtil.getPromotionTargets((PromotionNotice)pbo);
		// 12、 将被审签的对象刷新为最新小版本
		refreshPromotableObject(pbo);
		QueryResult resulttargets = MaturityHelper.service.getBaselineItems((PromotionNotice) pbo);
		log.debug("targets size===" + resulttargets.size());
		// 9、如果遍历得到的子零部件、模型、图纸(catia二维图纸和AutoCAD图纸)不在本升级列表中，且其状态不为“已发布”，则不允许提交任务,验证在流程中进行，isworkflow
		// ==true
		if (isworkflow) {
			ArrayList<Long> targetlist = new ArrayList<Long>();
			targetlist = settargetList((PromotionNotice) pbo);
			StringBuilder childmessage = checkPRchildObjects((PromotionNotice) pbo, targetlist);
			message.append(childmessage);
		}
		while (resulttargets.hasMoreElements()) {
			Persistable object = (Persistable) resulttargets.nextElement();
			RevisionControlled revisionControlled = (RevisionControlled) object;
			String number = getObjectnumber(object);
			log.debug("target number===" + number);
			// 1、 升级请求中的对象只能够是部件（状态为“设计”或“设计修改”）、Catia模型（图纸）、线束AutoCAD图纸
			if (!revisionControlled.getState().toString().equalsIgnoreCase(PartState.DESIGN) && !revisionControlled.getState().toString().equalsIgnoreCase(PartState.DESIGNMODIFICATION)) {
				message.append(number + ",不是“设计”或“设计修改”的状态，不能添加到升级对象中! \n");
			}
			if (object instanceof WTDocument) {
				WTDocument doc = (WTDocument) object;
				String doctype = DocUtil.getObjectType(doc);
				//log.debug("doc type===" + doctype);
				if (!(doctype.endsWith("autocadDrawing")||doctype.endsWith(TypeName.softwareDoc))) {
					message.append(doc.getNumber() + "不符合规范,只能上传部件，CATIA模型、AutoCAD图纸和软件文档！\n");
				}
			}
			// 2、如果升级请求列表中的对象的版本号不为初始版本（即为修订后的版本），则该对象必须在ECA（状态为“实施”或“正在审阅”或“返工”）的产生结果对象列表中
			String version = revisionControlled.getVersionIdentifier().getValue();
			if (!version.startsWith("1") && !version.startsWith("A")) {
				if (!checkExistEC(revisionControlled)) {
					message.append(number + ",对象没有正在进行中的变更！\n");
				}
			}
			// 3、 被添加到升级请求中的对象的创建者或设计者应该包含提交者本人
			if (!checkifDesigner(revisionControlled, (PromotionNotice) pbo)) {
				message.append("您不是" + number + "的设计者！不能提交该对象 \n");
			}
			// 4、 如果升级对象已经在其它的有效BOM审签流程中，则不能够被添加到升级请求中
			QueryResult prcounts = isHavePromoteRequest(object);
			HashSet<String> pstateHashSet = new HashSet<String>();
			//log.debug("prcounts=========" + prcounts.size());
			while (prcounts.hasMoreElements()) {
				Object[] object2 = (Object[]) prcounts.nextElement();
				PromotionNotice promotion = (PromotionNotice) object2[0];
				//log.debug("promotion number===" + promotion.getNumber());
				QueryResult processResult = new QueryResult();
				try {
					processResult = NmWorkflowHelper.service.getAssociatedProcesses(promotion, null, null);
					//log.debug("processResult size====" + processResult.size());
				} catch (WTException e1) {
					//log.debug("getAssociatedProcesses failed----!");
					e1.printStackTrace();
				}
				while (processResult.hasMoreElements()) {
					WfProcess process = (WfProcess) processResult.nextElement();
					//log.debug("process.getState()..toString()" + process.getState().toString());
					//log.debug("process name=====" + process.getName()+",id="+process.toString());
					if (process.getState().toString().endsWith("OPEN_RUNNING")) {
						pstateHashSet.add(process.getName());
					}
				}
			}
			//log.debug("pstateHashSet.size() ==" + pstateHashSet.size());
			// if pstateHashSet.size==1,exist only one running process
			if (pstateHashSet.size() > 1) {
				message.append(number + ",对象存在其他进行中的评审流程！ \n");
			}
			if (pstateHashSet.size() == 1 && !isworkflow) {
				message.append(number + ",对象存在其他进行中的评审流程！ \n");
			}
			// 5、 只允许最新版本添加到审签列表中
			/*
			 * if(!isLastVersion(revisionControlled)) {
			 * message.append(number+"不是最新版本！请提交最新的版本  \n"); }
			 */
			// 5、
			// 升级请求中的对象可以位于不同的产品库中，但不允许同时包含产品库与存储库中的对象，也不允许包含不同存储库中的对象，程序逻辑可以如下
			// a) 如果升级请求位于产品库中，则不允许包含存储库中的对象，否则提示（不能够添加其它存储库内的零部件与图纸）
			// b)
			// 如果升级请求位于存储库中，则升级请求中的对象只能够与升级请求位于同一个库中，否则提示（不能够添加其它存储库或产品库的零部件与图纸）
			//log.debug("pn container ===" + pncontainer);

			String pnContainerName = pncontainer.getName();
			WTContainer targetObjectContainer = ((WTContained) revisionControlled).getContainer();
			String targetObjectContianerName = targetObjectContainer.getName();

			if (pncontainer instanceof PDMLinkProduct) {

				if (targetObjectContainer instanceof WTLibrary) {
					message.append("升级请求所在的容器为产品库,  不能够加入存储库内的对象,  " + number + " 所在的容器为存储库\n");
				}

			} else if (pncontainer instanceof WTLibrary) {
				if (!targetObjectContianerName.equals(pnContainerName)) {
					message.append("升级请求所在的容器为存储库, 不能够添加其它存储库或产品库的对象. " + "升级请求所在的容器名为" + pnContainerName + number + "的容器名称为" + targetObjectContianerName + "\n");
				}
			}
			// ------------------------------------------------------------------------------------->
			// 以下验证在流程中进行，isworkflow ==true
			if (isworkflow) {				
				if (object instanceof WTPart) {
					Set<WTPartMaster> sets = new HashSet<WTPartMaster>();
					WTPart part = (WTPart) object;
					sets.add((WTPartMaster)part.getMaster());
					QueryResult qr = WTPartHelper.service.getUsesWTPartMasters(part);
					while(qr.hasMoreElements()){
					   WTPartUsageLink link = (WTPartUsageLink)qr.nextElement();
					   WTPartMaster master = link.getUses();
					   sets.add(master);
					}
					//12.校验Part和Part的子件 的状态是否为“设计禁用”，以及是否存在未完成物料设计禁用单中
					for(WTPartMaster master : sets){
						WTPart lastPart = (WTPart) PromotionUtil.getLatestVersionByMaster(master);					
						if (lastPart!=null&&lastPart.getState().toString().endsWith(PartState.DISABLEDFORDESIGN)) {
							message.append("零部件" + lastPart.getNumber()+"的状态为设计禁用！\n"); 
						}
						Set<Persistable> all = PromotionUtil.getAllLatestVersionByMaster(master);
						for (Persistable p : all) {
							WTPart par = (WTPart)p;
							Set<String> numbers = PromotionUtil.isExsitPromotion(par);
							if (numbers.size() > 0) {
								String rtn = com.catl.promotion.util.WorkflowUtil.joinSetMsg(numbers);
								message.append("零部件" + lastPart.getNumber()+"已经被加入未完成的编号为" + rtn +"的物料设计禁用单中！\n");
							}
						}
					}
					
					//检查直接下层件的成熟度
					checkOneLevelChildMaturity(part, message);
					
					// 6、 如果升级对象为零部件，则自制件或外协件或虚拟件必须包含下层物料
					PartWorkflowUtil.deletePDFAttachmentData(part);
					part = (WTPart) PersistenceHelper.manager.refresh(part);
					PartWorkflowUtil.checkPartPDFInfo(part, message);
					String partorigin = part.getSource().toString();
					//log.debug("partorigin===" + partorigin);
					message.append(checkSource(part,partorigin));		
				}

				// 8、 如果升级请求中包含“非中间件的三维模型”，则三维模型也必须有关联的PN
				if (object instanceof EPMDocument) {
					EPMDocument epm = (EPMDocument) object;
					String epmtype = epm.getDocType().toString();
					//log.debug("epm type is=====" + epmtype);
					if (!epmtype.endsWith("CADDRAWING")) {
						if (checkString(epm.getNumber()) < 2) {
							QueryResult relatePart = PersistenceHelper.manager.navigate(epm, EPMBuildRule.BUILD_TARGET_ROLE, EPMBuildRule.class);
							//log.debug("relate part size()====" + relatePart.size());

							if (relatePart == null || relatePart.size() == 0) {
								message.append("对象：" + epm.getNumber() + "为三维模型,必须有关联的PN \n");
							}
						}
					}
				}
				//9、如果是AUTOCAD必须包含PDF格式的附件
				if(object instanceof WTDocument){
					WTDocument doc = (WTDocument)object;
					//log.info("==isAutoCADDoc==doc:"+doc.getIdentity());
					if(TypeUtil.isSpecifiedType(doc, CatlConstant.AUTOCAD_DOC_TYPE)){
						String pdfCheckResult = DocUtil.pdfFileCheck(doc);
						if (pdfCheckResult != null) {
							message.append(doc.getNumber() + "," + pdfCheckResult + "\n");
						}
						WTPart part = PartUtil.getRelationPartByDescDoc(doc);
						if(part == null || !part.getNumber().equals(doc.getNumber())){
							message.append("请为AutoCAD图纸："+doc.getNumber()+"关联说明部件\n");
						}
					}
				}

				// 10、 如果包含Catia二维图纸，则必须包含PDF格式的附件
				/*if (object instanceof EPMDocument) {
					EPMDocument epm = (EPMDocument) object;
					String epmtype = epm.getDocType().toString();
					log.debug("epm type is=====" + epmtype);
					if (epmtype.endsWith("CADDRAWING")) {
						String pdfCheckResult = DocUtil.pdfFileCheck(epm);
						if (pdfCheckResult != null) {
							message.append(epm.getNumber() + "," + pdfCheckResult + "\n");
						}
					}
				}*/

				// 11、 提交时，所有被审签的对象都必须处于检入状态
				if (WorkInProgressHelper.isCheckedOut((Workable) object)) {
					message.append(number + ",对象被检出，请检入后提交！\n");
				}
			}
		}
		return message;
	}

	public static String getObjectnumber(Persistable object) {
		String number = "";
		if (object instanceof WTPart) {
			WTPart part = (WTPart) object;
			number = part.getNumber();
		}
		if (object instanceof WTDocument) {
			WTDocument doc = (WTDocument) object;
			number = doc.getNumber();
		}
		if (object instanceof EPMDocument) {
			EPMDocument emp = (EPMDocument) object;
			number = emp.getNumber();
		}
		return number;
	}

	// check exist ecr which is not resolved
	public static Boolean isECAchange(RevisionControlled reControlled) {
		QueryResult eCResult = null;
		Boolean isecr = false;
		try {
			eCResult = ChangeHelper2.service.getAffectingChangeActivities(reControlled);
			log.debug("eca result size===" + eCResult.size());
		} catch (ChangeException2 e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (eCResult.hasMoreElements()) {
			WTChangeActivity2 eca = (WTChangeActivity2) eCResult.nextElement();
			if (!eca.getState().toString().equalsIgnoreCase(ChangeState.RESOLVED) && !eca.getState().toString().equalsIgnoreCase(ChangeState.CANCELLED)) {
				isecr = true;
			}
		}
		return isecr;
	}

	public static Boolean checkExistEC(RevisionControlled reControlled) {
		Boolean isworkECA = false;
		try {
			WTCollection collection = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(reControlled);
			if (!collection.isEmpty()) {
				Iterator iterator = collection.iterator();
				while (iterator.hasNext()) {
					ObjectReference objReference = (ObjectReference) iterator.next();
					WTChangeOrder2 eco = (WTChangeOrder2) objReference.getObject();
					log.debug("eco number==" + eco.getNumber());
					QueryResult ecnqr = ChangeHelper2.service.getChangeActivities(eco);
					while (ecnqr.hasMoreElements()) {
						WTChangeActivity2 eca = (WTChangeActivity2) ecnqr.nextElement();
						log.debug("eca number===" + eca.getNumber() + "state=" + eca.getState().getState().toString());
						if (eca.getState().toString().equalsIgnoreCase(ChangeState.IMPLEMENTATION) || eca.getState().toString().equalsIgnoreCase(ChangeState.REWORK) || eca.getState().toString().equalsIgnoreCase(ChangeState.UNDER_REVIEW)) {
							isworkECA = true;
						}
					}
				}
			}
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isworkECA;
	}

	public static ArrayList getAttachmentName(ContentHolder holder) throws Exception {
		ArrayList alist = new ArrayList();
		if (null != holder) {
			ContentHolder contentHolder = ContentHelper.service.getContents(holder);
			Vector vData = ContentHelper.getApplicationData(contentHolder);
			if (vData != null && vData.size() > 0) {
				for (int i = 0; i < vData.size(); i++) {
					ApplicationData appData = (ApplicationData) vData.get(i);
					alist.add(appData.getFileName());
					log.debug("appData.getFileName()----------->" + appData.getFileName());
				}
			}
		}
		return alist;
	}

	public static StringBuffer checkRole(PromotionNotice pn) {
		StringBuffer message = new StringBuffer();
		try {
			QueryResult targetQueryResult = WorkflowUtil.getPromotionTargets(pn);
			while (targetQueryResult.hasMoreElements()) {
				Object object = (Object) targetQueryResult.nextElement();
				RevisionControlled revisionControlled = (RevisionControlled) object;
				String number = getObjectnumber(revisionControlled);
				if (!checkifDesigner(revisionControlled, pn)) {
					message.append("您不是" + number + "的创建者或设计者！不能提交该对象 \n");
				}
			}
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return message;
	}

	public static Boolean checkAuthor(WTObject object) throws WTException {
		Boolean isonly = false;
		HashSet<String> creatorHashSet = new HashSet<String>();
		if (object instanceof PromotionNotice) {
			PromotionNotice pn = (PromotionNotice) object;
			String pncreater = pn.getCreatorName();
			log.debug("promotionNotice creator==" + pncreater);
			QueryResult result = WorkflowUtil.getPromotionTargets(pn);
			while (result.hasMoreElements()) {
				Object object2 = (Object) result.nextElement();
				Workable able = (Workable) object2;
				creatorHashSet.add(able.getCreatorName());
				log.debug("object creator name==" + able.getCreatorName());
			}
			log.debug("creatorHashSet size==" + creatorHashSet.size());
			if (creatorHashSet.size() == 1) {
				Iterator iterator = creatorHashSet.iterator();
				while (iterator.hasNext()) {
					String creatorString = iterator.next().toString();
					if (creatorString.equalsIgnoreCase(pncreater)) {
						isonly = true;
					}
				}
			}
		}
		return isonly;
	}

	public static QueryResult isHavePromoteRequest(Persistable object) throws WTException {
		QueryResult queryResult = null;
		QuerySpec querySpec = new QuerySpec();
		String number = object.getPersistInfo().getObjectIdentifier().getId() + "";
		int promotionNoticeIndex = querySpec.appendClassList(PromotionNotice.class, true);
		int partIndex = 0;
		if (object instanceof WTPart) {
			partIndex = querySpec.appendClassList(WTPart.class, false);
		}
		if (object instanceof WTDocument) {
			partIndex = querySpec.appendClassList(WTDocument.class, false);
		}
		if (object instanceof EPMDocument) {
			partIndex = querySpec.appendClassList(EPMDocument.class, false);
		}
		int promotiontargetIndex = querySpec.addClassList(PromotionTarget.class, false);
		querySpec.setAdvancedQueryEnabled(true);
		String[] aliases = new String[3];
		aliases[0] = querySpec.getFromClause().getAliasAt(promotionNoticeIndex);
		aliases[1] = querySpec.getFromClause().getAliasAt(partIndex);
		aliases[2] = querySpec.getFromClause().getAliasAt(promotiontargetIndex);

		TableColumn promotionNoticeColumn = new TableColumn(aliases[0], "IDA2A2");
		TableColumn partColumn = new TableColumn(aliases[1], "IDA2A2");
		TableColumn promotiontargetColumnA = new TableColumn(aliases[2], "IDA3A5");
		TableColumn promotiontargetColumnB = new TableColumn(aliases[2], "IDA3B5");
		CompositeWhereExpression andExpression = new CompositeWhereExpression(LogicalOperator.AND);
		andExpression.append(new SearchCondition(promotionNoticeColumn, "=", promotiontargetColumnA));
		andExpression.append(new SearchCondition(partColumn, "=", promotiontargetColumnB));
		andExpression.append(new SearchCondition(partColumn, "=", ConstantExpression.newExpression(number)));
		querySpec.appendWhere(andExpression, null);
		queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);

		return queryResult;
	}

	public static ConfigSpec getDefaultConfigSpec() throws WTException {
		return ConfigHelper.service.getDefaultConfigSpecFor(WTPart.class);
	}

	public static Map<Object, List> getChildrenParts(List parents) throws WTException {
		ConfigSpec configSpec = getDefaultConfigSpec();
		Map<Object, List> result = new HashMap<Object, List>();
		// API returns a 3D array where the 1st dim is the parent parts,
		// the 2nd dim is the list of children for a given parent,
		// and the 3rd dim is 2 element array w/the link obj at 0 and the child
		// part at 1
		Persistable[][][] all_children = WTPartHelper.service.getUsesWTParts(new WTArrayList(parents), configSpec);
		for (ListIterator i = parents.listIterator(); i.hasNext();) {
			WTPart parent = (WTPart) i.next();
			Persistable[][] branch = all_children[i.previousIndex()];
			if (branch == null) {
				continue;
			}
			List children = new ArrayList(branch.length);
			result.put(parent, children);
			for (Persistable[] child : branch) {
				children.add(child[1]);
			}
		}
		log.debug("Parents To Children: " + result);
		return result;
	}

	public static void visitBomTree(WTArrayList parents, ArrayList<WTPart> childlist, int level) throws Exception {
		//log.debug("visit Bom Tree , Level = " + level);
		ConfigSpec configSpec = getDefaultConfigSpec();
		Persistable[][][] all_children = WTPartHelper.service.getUsesWTParts(parents, configSpec);
		for (int i = 0; i < parents.size(); i++) {
			Persistable[][] branch = all_children[i];
			if (branch != null && branch.length > 0 && level < 100) {
				WTArrayList children = new WTArrayList(branch.length);
				for (Persistable[] child : branch) {
					Persistable p = child[1];
					if (p instanceof WTPart) {
						WTPart childPart = (WTPart) p;
						//log.debug("child Part = " + childPart.getNumber());
						children.add(childPart);
						childlist.add(childPart);

					}
				}
				level++;
				visitBomTree(children, childlist, level);
			}

		}
	}

	public static void getEpmallchild(EPMDocument epm, ArrayList<EPMDocument> epmlist) throws Exception {
		QueryResult epmResult = StructHelper.service.navigateUses(epm);
		//log.debug("epmresult size===" + epmResult.size());
		while (epmResult.hasMoreElements()) {
			EPMDocumentMaster epmdocmaster = (EPMDocumentMaster) epmResult.nextElement();
			EPMDocument epmdoc = DocUtil.getLastestEPMDocumentByNumber(epmdocmaster.getNumber());
			//log.debug("epmdoc part numer==" + epmdoc.getNumber());
			epmlist.add(epmdoc);
			getEpmallchild(epmdoc, epmlist);
		}
	}

	public static List<EPMDocument> getChildCadDoc(EPMDocument parentCadDoc) {
		//log.debug("Parent doc number = " + parentCadDoc.getNumber());
		List<EPMDocument> children = new ArrayList<EPMDocument>();
		QueryResult qr = null;
		try {
			qr = EPMStructureHelper.service.navigateUsesToIteration(parentCadDoc, null, true, new LatestConfigSpec());
		} catch (ConfigException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		while (qr.hasMoreElements()) {
			Object o = qr.nextElement();
			if (o instanceof EPMDocument) {
				EPMDocument child = (EPMDocument) o;
				children.add(child);
				//log.debug("Parent  CAD number " + parentCadDoc.getNumber() + " child CAD Number" + child.getNumber());
			}

		}

		return children;

	}

	public static StringBuilder checkPRchildObjects(PromotionNotice pn, ArrayList<Long> targetslists) throws Exception {
		StringBuilder message = new StringBuilder();
		List<String> messageList = new ArrayList<String>();
		ArrayList<WTPart> partsInPN = new ArrayList<WTPart>();
		ArrayList<EPMDocument> epmList = new ArrayList<EPMDocument>();
		QueryResult targeResult = WorkflowUtil.getPromotionTargets(pn);
		// ArrayList<String> numberList=setList(targeResult);
		log.debug("prchangeablesQr size()===" + targeResult.size());
		List<String> numbersInPN = new ArrayList<String>();
		while (targeResult.hasMoreElements()) {
			Object object = (Object) targeResult.nextElement();
			if (object instanceof WTPart) {
				WTPart part = (WTPart) object;
				partsInPN.add(part);
				numbersInPN.add(part.getNumber());
				
				//检查软件版本匹配码是否一致 update by szeng 20180417
				message.append(PartUtil.checkMatchingCode(part, ""));
				
			} else if (object instanceof EPMDocument) {
				EPMDocument cadDoc = (EPMDocument) object;
				epmList.add(cadDoc);
				numbersInPN.add(cadDoc.getNumber());
			} else if (object instanceof WTDocument) {
				WTDocument wtdoc = (WTDocument) object;
				numbersInPN.add(wtdoc.getNumber());
			}

		}// end while for targeResult

		// get first child part only
		Map<Object, List> allChildren = getChildrenParts(partsInPN);

		for (Iterator i = allChildren.keySet().iterator(); i.hasNext();) {

			WTPart parentPart = (WTPart) i.next();
			List childParts = allChildren.get(parentPart);
			for (int index = 0; index < childParts.size(); index++) {
				Object o = childParts.get(index);
				if (o instanceof WTPart) {
					WTPart childPart = (WTPart) o;
					if (!childPart.getState().toString().endsWith(PartState.RELEASED)) {
						if (!numbersInPN.contains(childPart.getNumber())) {
							messageList.add("零部件 " + parentPart.getNumber() + " 的子件 " + childPart.getNumber() + " 不是已发布状态，且不在升级列表中，不能提交 \n");
						}
					}

					QueryResult desdocresult = PartDocServiceCommand.getAssociatedDescribeDocuments(childPart);
					while (desdocresult.hasMoreElements()) {
						WTDocument doc = (WTDocument) desdocresult.nextElement();
						TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(doc);
						String doctype = ti.getTypename();
						if (doctype.endsWith(TypeName.doc_type_autocadDrawing) && !doc.getState().toString().equalsIgnoreCase(PartState.RELEASED)) {
							if (!numbersInPN.contains(doc.getNumber())) {
								messageList.add("零部件 " + parentPart.getNumber() + " 的子件" + childPart.getNumber() + " 关联的AutoCAD文档：" + doc.getNumber() + "不是已发布状态，且不在升级列表中，不能提交 \n");

							}
						}else if (doctype.endsWith(TypeName.softwareDoc) && !doc.getState().toString().equalsIgnoreCase(PartState.RELEASED)) {
							if (!numbersInPN.contains(doc.getNumber())) {
								messageList.add("零部件 " + parentPart.getNumber() + " 的子件" + childPart.getNumber() + " 关联的软件文档：" + doc.getNumber() + "不是已发布状态，且不在升级列表中，不能提交 \n");

							}
						}

					}// end while for AutoCAD

				}// end if child is WTPart

			}// end for child loop

		}// end for parent part loop

		for (int e = 0; e < epmList.size(); e++) {
			EPMDocument parentCadDoc = epmList.get(e);
			List<EPMDocument> childCadDoc = getChildCadDoc(parentCadDoc);
			for (int m = 0; m < childCadDoc.size(); m++) {
				EPMDocument child = childCadDoc.get(m);
				if (!child.getState().toString().endsWith(PartState.RELEASED)) {
					if (!numbersInPN.contains(child.getNumber())) {
						messageList.add("模型  " + parentCadDoc.getNumber() + " 的子模型  " + child.getNumber() + " 不是已发布状态，且不在升级列表中，不能提交 \n");

					}
				}

				Collection<EPMDocument> epmDrawingCollection = EpmUtil.getDrawings(child);
				if (!epmDrawingCollection.isEmpty()) {
					Iterator epmiteraIterator = epmDrawingCollection.iterator();
					while (epmiteraIterator.hasNext()) {
						EPMDocument epmDrawingdoc = (EPMDocument) epmiteraIterator.next();
						if (WorkInProgressHelper.isWorkingCopy(epmDrawingdoc)) {
							continue;
						}
						log.debug("epmDrawingdoc part state=" + epmDrawingdoc.getNumber() + "==" + epmDrawingdoc.getState().toString());
						if (!epmDrawingdoc.getState().toString().endsWith(PartState.RELEASED)) {
							if (!numbersInPN.contains(epmDrawingdoc.getNumber())) {
								messageList.add("模型  " + parentCadDoc.getNumber() + " 的图纸：" + epmDrawingdoc.getNumber() + "不是已发布状态，且不在升级列表中，不能提交 \n");

							}
						}
					}
				}
			}// end of child Doc loop

		}// end for parent doc loop

		for (int i = 0; i < messageList.size(); i++) {
			message.append(messageList.get(i).toString());
		}
		return message;
	}

	public static ArrayList<Long> settargetList(PromotionNotice pn) {
		ArrayList<Long> list = new ArrayList<Long>();
		QueryResult result = new QueryResult();
		try {
			result = MaturityHelper.service.getPromotionTargets(pn);
		} catch (MaturityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (result.hasMoreElements()) {
			WTObject object = (WTObject) result.nextElement();
			// String numberString=BomWfUtil.getObjectnumber((Persistable)
			// object);
			long id = object.getPersistInfo().getObjectIdentifier().getId();
			//log.debug("object oid==" + id);
			list.add(id);
		}

		return list;
	}

	public static int checkString(String epmnumber) {
		String str = "-";
		int count = 0;
		int start = 0;
		while (epmnumber.indexOf(str, start) >= 0 && start < epmnumber.length()) {
			count++;
			start = epmnumber.indexOf(str, start) + str.length();
		}
		return count;
	}
	public static ResultMessage validateRD(ObjectReference self) throws WTException{
		return validateRD(self,null);
	}
	/**
	 * 特殊物料组发布角色校对
	 * @param self
	 * @return
	 * @throws WTException 
	 */
	public static ResultMessage validateRD(ObjectReference self,WTObject pbo) throws WTException {
		ResultMessage result = new ResultMessage();
		StringBuffer message = new StringBuffer();
		result.setSucceed(true);
		result.setHas_SE_role(false);
		result.setHas_countersign_people_role(false);
		try {
			WTPrincipal currentUser = SessionHelper.getPrincipal();
			WfAssignedActivity acivity = (WfAssignedActivity) self.getObject();
			WfProcess process = acivity.getParentProcess();
			Enumeration collator = process.getPrincipals(Role.toRole(RoleName.COLLATOR));
			Enumeration approver = process.getPrincipals(Role.toRole(RoleName.APPROVER));

			log.debug("start to check process role------>");
			log.debug("process name==" + process.getName());
			
			if (!collator.hasMoreElements()) {
				result.setSucceed(false);
				message.append("校对者角色不能为空，请在设置参与者中选择校对者！ \n");
			} else if (WorkflowUtil.isSelectSelf(collator, currentUser)) {
				result.setSucceed(false);
				message.append("校对者角色不能选择自己！ \n");
			}
			
			if (!approver.hasMoreElements()) {
				result.setSucceed(false);
				message.append("批准者角色不能为空，请在设置参与者中选择批准者！ \n");
			} else if (WorkflowUtil.isSelectSelf(approver, currentUser)) {
				result.setSucceed(false);
				message.append("批准者角色不能选择自己！ \n");
			}
		} catch (WTException e) {
			// TODO Auto-generated catch block
			message.append(e.getMessage());
			result.setSucceed(false);
			e.printStackTrace();

		}
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		result.setMessage(message);
		return result;
	}
	
	/**
	 * 售后再利用件发布角色校对
	 * @param self
	 * @return
	 */
	public static ResultMessage validateAfterSale(ObjectReference self) {
		ResultMessage result = new ResultMessage();
		StringBuffer message = new StringBuffer();
		result.setSucceed(true);
		result.setHas_SE_role(false);
		result.setHas_countersign_people_role(false);
		try {
			WTPrincipal currentUser = SessionHelper.getPrincipal();
			WfAssignedActivity acivity = (WfAssignedActivity) self.getObject();
			WfProcess process = acivity.getParentProcess();
			Enumeration datater = process.getPrincipals(Role.toRole(RoleName.PRODUCT_DATA_ENGINGEER));
			log.debug("start to check process role------>");
			log.debug("process name==" + process.getName());
			
			if (!datater.hasMoreElements()) {
				result.setSucceed(false);
				message.append("产品数据工程师不能为空，请在设置参与者中选择产品数据工程师！ \n");
			}else if (WorkflowUtil.isSelectSelf(datater, currentUser)) {
				result.setSucceed(false);
				message.append("产品数据工程师角色不能选择自己！ \n");
			}
		} catch (WTException e) {
			// TODO Auto-generated catch block
			message.append(e.getMessage());
			result.setSucceed(false);
			e.printStackTrace();

		}
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		result.setMessage(message);
		return result;
	}
	public static ResultMessage validate(ObjectReference self) {
		ResultMessage result = new ResultMessage();
		StringBuffer message = new StringBuffer();
		result.setSucceed(true);
		result.setHas_SE_role(false);
		result.setHas_countersign_people_role(false);
		try {
			WTPrincipal currentUser = SessionHelper.getPrincipal();
			WfAssignedActivity acivity = (WfAssignedActivity) self.getObject();
			WfProcess process = acivity.getParentProcess();
			Enumeration product_data_engingeer = process.getPrincipals(Role.toRole(RoleName.PRODUCT_DATA_ENGINGEER));
			Enumeration collator = process.getPrincipals(Role.toRole(RoleName.COLLATOR));
			Enumeration approver = process.getPrincipals(Role.toRole(RoleName.APPROVER));
			//Enumeration pmc = process.getPrincipals(Role.toRole(RoleName.PMC));

			Enumeration se = process.getPrincipals(Role.toRole(RoleName.SYSTEM_ENGINEER));
			Enumeration countsign_people = process.getPrincipals(Role.toRole(RoleName.COUNTERSIGN_PEOPLE));

			log.debug("start to check process role------>");
			log.debug("process name==" + process.getName());
			if (!product_data_engingeer.hasMoreElements()) {
				result.setSucceed(false);
				message.append("产品数据工程师角色不能为空，请在设置参与者中选择产品数据工程师！ \n");
			} else if (WorkflowUtil.isSelectSelf(product_data_engingeer, currentUser)) {
				result.setSucceed(false);
				message.append("产品数据工程师角色不能选择自己！ \n");
			}
			if (!collator.hasMoreElements()) {
				result.setSucceed(false);
				message.append("校对者角色不能为空，请在设置参与者中选择校对者！ \n");
			} else if (WorkflowUtil.isSelectSelf(collator, currentUser)) {
				result.setSucceed(false);
				message.append("校对者角色不能选择自己！ \n");
			}
			/*if (!pmc.hasMoreElements()) {
				result.setSucceed(false);
				message.append("物料控制专员角色不能为空，请在设置参与者中选择物料控制专员！ \n");
			} else if (WorkflowUtil.isSelectSelf(pmc, currentUser)) {
				result.setSucceed(false);
				message.append("物料控制专员角色不能选择自己！ \n");
			}*/
			if (!approver.hasMoreElements()) {
				result.setSucceed(false);
				message.append("批准者角色不能为空，请在设置参与者中选择批准者！ \n");
			} else if (WorkflowUtil.isSelectSelf(approver, currentUser)) {
				result.setSucceed(false);
				message.append("批准者角色不能选择自己！ \n");
			}
			if (se.hasMoreElements()) {
				result.setHas_SE_role(true);
			}
			if (countsign_people.hasMoreElements()) {
				result.setHas_countersign_people_role(true);
			}

		} catch (WTException e) {
			// TODO Auto-generated catch block
			message.append(e.getMessage());
			result.setSucceed(false);
			e.printStackTrace();

		}
		if (log.isDebugEnabled()) {
			log.debug(message);
		}

		result.setMessage(message);
		return result;
	}
	
	public static ResultMessage validate(ObjectReference self,WTObject pbo) throws WTException {
		ResultMessage result = new ResultMessage();
		StringBuffer message = new StringBuffer();
		result.setSucceed(true);
		result.setHas_SE_role(false);
		result.setHas_countersign_people_role(false);
		PromotionNotice pn=(PromotionNotice) pbo;
		QueryResult resulttargets = MaturityHelper.service.getBaselineItems((PromotionNotice) pbo);
		List<WTPart> pparts=new ArrayList<WTPart>();
		while (resulttargets.hasMoreElements()) {
			Persistable object = (Persistable) resulttargets.nextElement();
			if(object instanceof WTPart){
				WTPart part=(WTPart) object;
				if(part.getNumber().startsWith("P")&&part.getVersionIdentifier().getValue().equals("1")){
					pparts.add(part);
				}
			}
		}
		try {
			WTPrincipal currentUser = SessionHelper.getPrincipal();
			WfAssignedActivity acivity = (WfAssignedActivity) self.getObject();
			WfProcess process = acivity.getParentProcess();
			Enumeration product_data_engingeer = process.getPrincipals(Role.toRole(RoleName.PRODUCT_DATA_ENGINGEER));
			Enumeration collator = process.getPrincipals(Role.toRole(RoleName.COLLATOR));
			Enumeration users = process.getPrincipals(Role.toRole(RoleName.COLLATOR));
			Enumeration approver = process.getPrincipals(Role.toRole(RoleName.APPROVER));
			//Enumeration pmc = process.getPrincipals(Role.toRole(RoleName.PMC));

			Enumeration se = process.getPrincipals(Role.toRole(RoleName.SYSTEM_ENGINEER));
			Enumeration countsign_people = process.getPrincipals(Role.toRole(RoleName.COUNTERSIGN_PEOPLE));

			log.debug("start to check process role------>");
			log.debug("process name==" + process.getName());
			if (!product_data_engingeer.hasMoreElements()) {
				result.setSucceed(false);
				message.append("产品数据工程师角色不能为空，请在设置参与者中选择产品数据工程师！ \n");
			} else if (WorkflowUtil.isSelectSelf(product_data_engingeer, currentUser)) {
				result.setSucceed(false);
				message.append("产品数据工程师角色不能选择自己！ \n");
			}
			if (!collator.hasMoreElements()) {
				result.setSucceed(false);
				message.append("校对者角色不能为空，请在设置参与者中选择校对者！ \n");
			} else if (WorkflowUtil.isSelectSelf(collator, currentUser)) {
				result.setSucceed(false);
				message.append("校对者角色不能选择自己！ \n");
			}
			if(pparts.size()>0){//有P开头物料
				boolean flag=false;
				while(users.hasMoreElements()){
					WTPrincipalReference userRef = (WTPrincipalReference) users.nextElement();
					WTPrincipal user = userRef.getPrincipal();
					Role productrole = Role.toRole(RoleName.PACKAGE_ENGINGEER_READ);
					List<String> principals=getContainerPri(productrole,pn.getContainer());
					for (int i = 0; i < principals.size(); i++) {
						System.out.println(principals.get(i)+"-----"+user.getName());
					}
					if(principals.contains(user.getName())){
						flag=true;
						break;
					}
				}
				if(!flag){
					result.setSucceed(false);
					message.append("包含P开头物料，校对者角色必须选择职能CPD-包装组浏览者下成员\n");
				}
			}
			/*if (!pmc.hasMoreElements()) {
				result.setSucceed(false);
				message.append("物料控制专员角色不能为空，请在设置参与者中选择物料控制专员！ \n");
			} else if (WorkflowUtil.isSelectSelf(pmc, currentUser)) {
				result.setSucceed(false);
				message.append("物料控制专员角色不能选择自己！ \n");
			}*/
			if (!approver.hasMoreElements()) {
				result.setSucceed(false);
				message.append("批准者角色不能为空，请在设置参与者中选择批准者！ \n");
			} else if (WorkflowUtil.isSelectSelf(approver, currentUser)) {
				result.setSucceed(false);
				message.append("批准者角色不能选择自己！ \n");
			}
			if (se.hasMoreElements()) {
				result.setHas_SE_role(true);
			}
			if (countsign_people.hasMoreElements()) {
				result.setHas_countersign_people_role(true);
			}

		} catch (WTException e) {
			// TODO Auto-generated catch block
			message.append(e.getMessage());
			result.setSucceed(false);
			e.printStackTrace();

		}
		if (log.isDebugEnabled()) {
			log.debug(message);
		}

		result.setMessage(message);
		return result;
	}

	public static Boolean isLastVersion(RevisionControlled revisionControlled) throws WTException {
		Boolean fagBoolean = false;
		String versionString = getlastVersion(revisionControlled);
		String versionString1 = "";
		if (revisionControlled instanceof WTPart) {
			WTPart part = (WTPart) revisionControlled;
			WTPart newPart = PartUtil.getLastestWTPartByNumber(part.getNumber());
			versionString1 = getlastVersion(newPart);
		}
		if (revisionControlled instanceof WTDocument) {
			WTDocument doc = (WTDocument) revisionControlled;
			WTDocument newDocument = DocUtil.getLatestWTDocument(doc.getNumber());
			versionString1 = getlastVersion(newDocument);
		}
		if (revisionControlled instanceof EPMDocument) {
			EPMDocument epm = (EPMDocument) revisionControlled;
			EPMDocument newepm = DocUtil.getLastestEPMDocumentByNumber(epm.getNumber());
			versionString1 = getlastVersion(newepm);
		}
		log.debug("targets version==" + versionString);
		log.debug("lastversion==" + versionString1);
		if (versionString.equals(versionString1)) {
			fagBoolean = true;
		} else {
			fagBoolean = false;
		}

		return fagBoolean;

	}

	public static String getlastVersion(RevisionControlled revisionControlled) {

		String versionString = "";
		versionString = revisionControlled.getVersionIdentifier().getValue() + "." + revisionControlled.getIterationIdentifier().getValue();
		return versionString;
	}

	public static String checkRanking(PromotionNotice pn) {
		StringBuffer sb = new StringBuffer();
		try {
			QueryResult result = MaturityHelper.service.getPromotionTargets(pn);
			while (result.hasMoreElements()) {
				WTObject object = (WTObject) result.nextElement();
				if (object instanceof WTPart) {
					WTPart part = (WTPart) object;
					String ret = checkRankingByPM((WTPartMaster) part.getMaster());
					if (ret != null)
						sb.append(ret);
					QueryResult qr = WTPartHelper.service.getUsesWTPartMasters(part);
					while (qr.hasMoreElements()) {
						WTPartUsageLink link = (WTPartUsageLink) qr.nextElement();
						WTPartMaster master = link.getUses();
						WTPart childPart = PartUtil.getLastestWTPartByNumber(master.getNumber());
						if (childPart.getState().toString().equalsIgnoreCase(PartState.RELEASED)) {
							String ret2 = checkRankingByPM(master);
							if (ret2 != null)
								sb.append(ret2);
						}
					}
				}
			}
		} catch (WTException e) {
			sb = new StringBuffer("<span style='color:red'>程序异常请联系管理员，谢谢！\n错误详细信息：" + e.getMessage() + "</span>");
			e.printStackTrace();
		} catch (Exception e) {
			sb = new StringBuffer("<span style='color:red'>程序异常请联系管理员，谢谢！\n错误详细信息：" + e.getMessage() + "</span>");
			e.printStackTrace();
		}
		return sb.toString();
	}

	private static String checkRankingByPM(WTPartMaster master) throws WTException {
		String ranking = (String) GenericUtil.getObjectAttributeValue(master, "ranking");
		if (ranking != null) {
			if (ranking.equals(Ranking.LOWER)) {
				return "物料PN：" + master.getNumber() + " 优选等级为<span style='color:red'><b>" + Ranking.LOWER + "</b></span>\n";
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param part
	 * @param message
	 * @throws WTException
	 */
	private static void checkOneLevelChildMaturity(WTPart parent,StringBuilder message) throws WTException{
		String parentMaturity = (String) IBAUtil.getIBAValue(parent.getMaster(), PartConstant.IBA_CATL_Maturity);
		if(StringUtils.equals("3", parentMaturity) || StringUtils.equals("6", parentMaturity)){
			Map<WTPart,Set<WTPart>> map = PromotionUtil.getOneLevelChild(parent);
			for(WTPart child : map.keySet()){
				WTPartMaster childMaster = (WTPartMaster) child.getMaster();
				String childMaturity = (String) IBAUtil.getIBAValue(childMaster, PartConstant.IBA_CATL_Maturity);
				if(childMaturity == null || !(StringUtils.equals(childMaturity, "3") || StringUtils.equals(childMaturity, "6"))){
					message.append(WTMessage.formatLocalizedMessage("{0}的直接下层子件{1}的“成熟度”必须为“3”或者“6”！\n", new Object[]{parent.getNumber(),child.getNumber()}));
				}
				for(WTPart substitute : map.get(child)){
					WTPartMaster substituteMaster = (WTPartMaster) substitute.getMaster();
					String substituteMaturity = (String) IBAUtil.getIBAValue(substituteMaster, PartConstant.IBA_CATL_Maturity);
					if(substituteMaturity == null || !(StringUtils.equals(substituteMaturity, "3") || StringUtils.equals(substituteMaturity, "6"))){
						message.append(WTMessage.formatLocalizedMessage("{0}的直接下层子件{1}的替代件{2}的“成熟度”必须为“3”或者“6”！ \n", new Object[]{parent.getNumber(), child.getNumber(), substitute.getNumber()}));
					}
				}
			}
		}
	}
	
	public static boolean addPrincipalToProcessRoleByusername(WfProcess process,Role role,String username){
		try{
			WTUser user = null;
			QuerySpec qs = new QuerySpec(WTUser.class);
	        SearchCondition sc = new SearchCondition(WTUser.class, WTUser.NAME, SearchCondition.EQUAL, username);
	        qs.appendWhere(sc, new int[] { 0 });
	        QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
	        if (qr.hasMoreElements()) {
	        	user = (WTUser) qr.nextElement();
	        }
	        Team team = (Team) process.getTeamId().getObject();
	        team.addPrincipal(role, user);
		}catch (WTException e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	public static StringBuilder checkReviewAfterSaleObjects(WTObject pbo, Boolean isworkflow) throws Exception {

		StringBuilder message = new StringBuilder();
		refreshPromotableObject(pbo);
		QueryResult resulttargets = MaturityHelper.service.getBaselineItems((PromotionNotice) pbo);
		while (resulttargets.hasMoreElements()) {
			Persistable object = (Persistable) resulttargets.nextElement();
			String number = getObjectnumber(object);
				if (WorkInProgressHelper.isCheckedOut((Workable) object)) {
					message.append(number + ",对象被检出，请检入后提交！\n");
				}
		}
		return message;
	}
	
	public static String checkSource(WTPart part,String partorigin) throws WTException{
		if (partorigin.startsWith(CatlConstant.MANUFACTURE_SOURCE_NAME) || partorigin.startsWith(CatlConstant.ASSISIT_SOURCE_NAME) || partorigin.startsWith(CatlConstant.VIRTUAL_SOURCE_NAME)) {
			QueryResult result = PersistenceHelper.manager.navigate(part, "uses", WTPartUsageLink.class, false);
			if (0 == result.size()) {
				return part.getNumber() + ",为'自制件'或'外协件'或'虚拟件'必须挂子件\n";
			}
		}
		return "";
	}

	
	public static Map<String,String> getSignMap(ObjectReference self) throws WTException{
		Map<String,String> signmap = new HashMap<>();
		if(self != null){
			Persistable per = self.getObject();
			if(per instanceof WfProcess){
				WfProcess process = (WfProcess)per;
				long processOid = process.getPersistInfo().getObjectIdentifier().getId();			//获取流程id
				System.out.println("====processOid:"+processOid);
			
				//循环处理"提交节点的workItem"
				Map<WorkItem, WfVotingEventAudit> designerInfoMap = WorkflowUtil.getCompletedWorkItemInfo(processOid,ElecSignConstant.SUBMIT, null);
				System.out.println("====managerInfoMap:"+designerInfoMap.size());
					
				//循环处理"产品数据工程师审核节点的workItem"
				Map<WorkItem, WfVotingEventAudit> checkInfoMap = WorkflowUtil.getCompletedWorkItemInfo(processOid,ElecSignConstant.CHECK, null);
				System.out.println("====seInfoMap:"+checkInfoMap.size());
					
				//循环处理"技术校对节点的workItem"
				Map<WorkItem, WfVotingEventAudit> technInfoMap = WorkflowUtil.getCompletedWorkItemInfo(processOid,ElecSignConstant.TECHN, null);
				System.out.println("====managerInfoMap:"+technInfoMap.size());
					
				//循环处理"部门批准节点的workItem"
				Map<WorkItem, WfVotingEventAudit> approInfoMap = WorkflowUtil.getCompletedWorkItemInfo(processOid,ElecSignConstant.APPRO, null);
				System.out.println("====managerInfoMap:"+approInfoMap.size());
				
				DateFormat format1 = new SimpleDateFormat("yyyy.MM.dd");
				
				for(WorkItem fmitem : designerInfoMap.keySet()){
					Object obj = designerInfoMap.get(fmitem).getEventList();
					System.out.println(fmitem.getIdentity()+"\t路由1\t"+obj);
					String completer = designerInfoMap.get(fmitem).getUserRef().getFullName();
					WfAssignedActivity fmactivity =  (WfAssignedActivity)fmitem.getSource().getObject();
					String completeDate = format1.format(fmactivity.getEndTime());
					signmap.put(ElecSignConstant.PTC_WM_DESIGN, completer);
					signmap.put(ElecSignConstant.PTC_WM_DESIGN_DATE, completeDate);
					
				}
				
				for(WorkItem fmitem : checkInfoMap.keySet()){
					Object obj = checkInfoMap.get(fmitem).getEventList();
					System.out.println(fmitem.getIdentity()+"\t路由2\t"+obj);
					if(obj.toString().contains("通过")){
						String completer = checkInfoMap.get(fmitem).getUserRef().getFullName();
						WfAssignedActivity fmactivity =  (WfAssignedActivity)fmitem.getSource().getObject();
						String completeDate = format1.format(fmactivity.getEndTime());
						signmap.put(ElecSignConstant.PTC_WM_CHECK, completer);
						signmap.put(ElecSignConstant.PTC_WM_CHECK_DATE, completeDate);
					}
				}
				
				for(WorkItem fmitem : technInfoMap.keySet()){
					Object obj = technInfoMap.get(fmitem).getEventList();
					System.out.println(fmitem.getIdentity()+"\t路由3\t"+obj);
					if(obj.toString().contains("通过")){
						String completer = technInfoMap.get(fmitem).getUserRef().getFullName();
						WfAssignedActivity fmactivity =  (WfAssignedActivity)fmitem.getSource().getObject();
						String completeDate = format1.format(fmactivity.getEndTime());
						signmap.put(ElecSignConstant.PTC_WM_TECHN, completer);
						signmap.put(ElecSignConstant.PTC_WM_TECHN_DATE, completeDate);
					}
				}
				
				for(WorkItem fmitem : approInfoMap.keySet()){
					Object obj = approInfoMap.get(fmitem).getEventList();
					System.out.println(fmitem.getIdentity()+"\t路由4\t"+obj);
					if(obj.toString().contains("通过")){
						String completer = approInfoMap.get(fmitem).getUserRef().getFullName();
						WfAssignedActivity fmactivity =  (WfAssignedActivity)fmitem.getSource().getObject();
						String completeDate = format1.format(fmactivity.getEndTime());
						signmap.put(ElecSignConstant.PTC_WM_APPRO, completer);
						signmap.put(ElecSignConstant.PTC_WM_APPRO_DATE, completeDate);
					}
				}
			}
		}
		return signmap;
	}
	
	/**
	 * CATIA工程图电子签名并重新发布可视化
	 * @param pbo
	 * @param self
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws RemoteException
	 */
	public static void signCatiaDrawing(WTObject pbo, ObjectReference self) throws WTException, WTPropertyVetoException, RemoteException{
		Map<String,String> map = getSignMap(self);
		QueryResult resulttargets = MaturityHelper.service.getBaselineItems((PromotionNotice) pbo);
		log.debug("targets size===" + resulttargets.size());
		while(resulttargets.hasMoreElements()){
			Object obj = resulttargets.nextElement();
			if(obj instanceof EPMDocument){
				EPMDocument epm = (EPMDocument) obj;				
				if(EpmUtil.isCATDrawing(epm)){
					
					if (epm != null) {
						IBAUtility iba = new IBAUtility(epm);
						EPMDocument epm1 = (EPMDocument) PartUtil.getPreviousVersion((Versioned) epm);					
						String ecoNumber = "";
						if (epm1 != null) {
							List<WTChangeOrder2> ecos = getECOByPersistable(epm1);
							if (ecos.size()>0) {
								WTChangeOrder2 order = ecos.get(0);
								ecoNumber = order.getNumber();
							}
						}
						if(StringUtils.isNotBlank(ecoNumber)){
							iba.setIBAValue(ElecSignConstant.PTC_WM_ECN_NO, ecoNumber);
						}
					
						for (String key:map.keySet()) {
							if(StringUtils.isNotBlank(map.get(key))){
								iba.setIBAValue(key, map.get(key));
							}						
						}
					
						epm = (EPMDocument) iba.updateAttributeContainer(epm);
						iba.updateIBAHolder(epm);
						EpmUtil.rePubEpmsReps(epm);//Caita工程图重新发布可视化
					}	
				}
			}
		}
	}
		
		/**
		 * 通过受影响对象获取ECO
		 * @param persi
		 * @return
		 * @throws WTException
		 */
		public static List<WTChangeOrder2> getECOByPersistable(Persistable persi) throws WTException{
			List<WTChangeOrder2> ecos = new ArrayList<>();
			WTChangeActivity2 dca = ChangeUtil.getEcaWithPersiser(persi);
			if(dca!=null){
				QueryResult qc = ChangeHelper2.service.getChangeOrder(dca);
				while(qc.hasMoreElements()){
					WTChangeOrder2 eco = (WTChangeOrder2) qc.nextElement();
					System.out.println(eco.getNumber()+"\n"+eco.getName());
					ecos.add(eco);
				}
			}
			return ecos;
		}
	

	/**
	 * 从上下文团队中获取指定角色的用户名
	 * @throws WTException 
	 */
	@SuppressWarnings("rawtypes")
	public static List<String> getContainerPri(Role productrole,WTContainer container) throws WTException {
		System.out.println("上下文"+container.getName());
		WTRoleHolder2 holder = TeamCCHelper.getTeamFromObject(container);
		Vector<Role> conRoles = holder.getRoles();
		
		List<String> result = new ArrayList<String>();
		
		if(conRoles.contains(productrole)){
			Enumeration enumeration = holder.getPrincipalTarget(productrole);  //取上下文对应角色的人.
			while(enumeration.hasMoreElements()){
				Object obj = enumeration.nextElement();
				WTPrincipal principal = null;
				if(obj instanceof WTPrincipal){
					principal = (WTPrincipal) obj;
					result.add(principal.getName());
				}else if(obj instanceof WTPrincipalReference){
					WTPrincipalReference principalReference = (WTPrincipalReference) obj;
					if(principalReference.getObject() instanceof WTGroup){
						WTGroup group = (WTGroup) principalReference.getObject();
						Enumeration users = group.members();
						while (users.hasMoreElements()) {
							principal = (WTPrincipal) users.nextElement();
							result.add(principal.getName());
						}
					}else{
						   principal = principalReference.getPrincipal();
						   result.add(principal.getName());
					}
				}
				
			}
		}
		return result;
	}

}
