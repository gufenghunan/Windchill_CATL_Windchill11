package com.catl.common.listener;

import java.beans.PropertyVetoException;
import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import wt.access.AccessControlServerHelper;
import wt.access.AccessControlled;
import wt.access.AccessPermission;
import wt.access.AccessPermissionSet;
import wt.access.AdHocAccessKey;
import wt.change2.AffectedActivityData;
import wt.change2.ChangeActivity2;
import wt.change2.ChangeException2;
import wt.change2.ChangeHelper2;
import wt.change2.Changeable2;
import wt.change2.RelevantRequestData2;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.doc.WTDocumentMasterIdentity;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMAuthoringAppType;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.EPMDocumentMasterIdentity;
import wt.epm.build.EPMBuildRule;
import wt.epm.build.EPMBuildRuleSequence;
import wt.epm.structure.EPMReferenceLink;
import wt.events.KeyedEvent;
import wt.events.KeyedEventListener;
import wt.fc.Identified;
import wt.fc.IdentityHelper;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceManagerEvent;
import wt.fc.PersistenceServerHelper;
import wt.fc.PersistentReference;
import wt.fc.QueryResult;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTCollection;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerHelper;
import wt.inf.library.WTLibrary;
import wt.lifecycle.LifeCycleServiceEvent;
import wt.lifecycle.State;
import wt.mail.EMailMessage;
import wt.maturity.MaturityBaseline;
import wt.maturity.PromotionNotice;
import wt.method.MethodContext;
import wt.org.OrganizationServicesHelper;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.ownership.OwnershipHelper;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartSubstituteLink;
import wt.part.WTPartUsageLink;
import wt.services.ServiceEventListenerAdapter;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.type.TypeModificationEvent;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;
import wt.vc.Mastered;
import wt.vc.VersionControlHelper;
import wt.vc.VersionControlServiceEvent;
import wt.vc.Versioned;
import wt.vc.baseline.BaselineHelper;
import wt.vc.struct.StructHelper;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.WorkInProgressServiceEvent;
import wt.vc.wip.Workable;
import wt.workflow.work.WfAssignmentState;
import wt.workflow.work.WorkItem;
import wt.workflow.work.WorkItemLink;

import com.catl.bom.cad.CatlFinderCreator;
import com.catl.bom.workflow.BomWfUtil;
import com.catl.bom.workflow.ReleasedDateUtil;
import com.catl.change.ChangeUtil;
import com.catl.change.util.ChangeConst;
import com.catl.change.workflow.ECWorkflowUtil;
import com.catl.common.constant.ChangeState;
import com.catl.common.constant.Constant;
import com.catl.common.constant.DocState;
import com.catl.common.constant.PartState;
import com.catl.common.constant.TypeName;
import com.catl.common.global.GlobalVariable;
import com.catl.common.util.CatlConstant;
import com.catl.common.util.DocUtil;
import com.catl.common.util.ElecSignConstant;
import com.catl.common.util.EpmUtil;
import com.catl.common.util.GenericUtil;
import com.catl.common.util.IBAUtil;
import com.catl.common.util.PartUtil;
import com.catl.common.util.PropertiesUtil;
import com.catl.common.util.RDUtil;
import com.catl.common.util.TypeUtil;
import com.catl.doc.EDatasheetDocUtil;
import com.catl.doc.maturityUpReport.MaturityUpReportHelper;
import com.catl.doc.service.StandardCatlDocumentListenerService;
import com.catl.ecad.utils.ECADConst;
import com.catl.ecad.utils.ECADutil;
import com.catl.ecad.utils.EPMUtil;
import com.catl.integration.log.TransactionLogHelper;
import com.catl.integration.log.WebServiceTransactionInfo;
import com.catl.integration.log.WebServiceTransactionLog;
import com.catl.integration.rdm.ObjectLinkedByRdm;
import com.catl.integration.rdm.RdmIntegrationHelper;
import com.catl.line.helper.BoxExplainHelper;
import com.catl.line.queue.DWGToPDFQueue;
import com.catl.line.util.CommonUtil;
import com.catl.line.util.WCUtil;
import com.catl.loadData.IBAUtility;
import com.catl.part.CreateCatlPartProcessor;
import com.catl.part.PartConstant;
import com.catl.part.classification.NodeConfigHelper;
import com.catl.promotion.util.PromotionUtil;
import com.catl.promotion.util.WorkflowUtil;
import com.catl.require.util.PlatformUtil;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;

public class CatlListener extends StandardManager implements CatlListenerService {
	private static final long serialVersionUID = 5266098956681288190L;

	private KeyedEventListener listener = null;
	private static Map<String,Integer> epmUpdateCounts = new HashMap<>();
	
	private static Logger log = Logger.getLogger(CatlListener.class.getName());

	public String getConceptualClassname() {
		return CatlListener.class.getName();
	}

	public static CatlListener newCatlListener() throws WTException {
		CatlListener instance = new CatlListener();
		instance.initialize();
		return instance;
	}

	protected void performStartupProcess() {
		log.debug(">>>++++++++++++++++++++++++resigiser service start:CatlListener");
		listener = new CatlEventListener(getConceptualClassname());
		log.debug(">>>++++++++++++++++++++++++resiger service end:CatlListener");

		getManagerService().addEventListener(listener, WorkInProgressServiceEvent.generateEventKey(WorkInProgressServiceEvent.PRE_CHECKIN));
		getManagerService().addEventListener(listener, WorkInProgressServiceEvent.generateEventKey(WorkInProgressServiceEvent.PRE_CHECKOUT));
		getManagerService().addEventListener(listener, WorkInProgressServiceEvent.generateEventKey(WorkInProgressServiceEvent.POST_CHECKOUT));
		getManagerService().addEventListener(listener, PersistenceManagerEvent.generateEventKey(PersistenceManagerEvent.INSERT));
		getManagerService().addEventListener(listener, PersistenceManagerEvent.generateEventKey(PersistenceManagerEvent.UPDATE));
		getManagerService().addEventListener(listener, PersistenceManagerEvent.generateEventKey(PersistenceManagerEvent.PRE_REMOVE));
		getManagerService().addEventListener(listener, PersistenceManagerEvent.generateEventKey(PersistenceManagerEvent.PRE_DELETE));
		getManagerService().addEventListener(listener, PersistenceManagerEvent.generateEventKey(PersistenceManagerEvent.POST_STORE));
		getManagerService().addEventListener(listener, VersionControlServiceEvent.generateEventKey(VersionControlServiceEvent.PRE_NEW_VERSION));
		getManagerService().addEventListener(listener, VersionControlServiceEvent.generateEventKey(VersionControlServiceEvent.NEW_VERSION));
		getManagerService().addEventListener(listener, TypeModificationEvent.generateEventKey("PRE_DELETE_TYPE"));
		getManagerService().addEventListener(listener, LifeCycleServiceEvent.generateEventKey(LifeCycleServiceEvent.STATE_CHANGE));
		getManagerService().addEventListener(listener,  WorkInProgressServiceEvent.generateEventKey(WorkInProgressServiceEvent.POST_CHECKIN));//2017/8/16add

		// getManagerService().addEventListener(listener,
		// LifeCycleServiceEvent.generateEventKey("STATE_CHANGE"));
		// getManagerService().addEventListener(listener,
		// IdentityServiceEvent.generateEventKey("POST_CHANGE_IDENTITY"));
	}

	class CatlEventListener extends ServiceEventListenerAdapter {
		
		private static final String CHECKOUT_OR_REVISE_PART_LIST = "CHECKOUT_OR_REVISE_PART_LIST";

		public CatlEventListener(String manager_name) {
			super(manager_name);
		}

		private List affectlist = new ArrayList<>();
		
		
		private void addPartToCheckoutReviseList(Mastered partMaster) {
			MethodContext methodContext = MethodContext.getContext();
			Set<String> partSet = (Set<String>) methodContext.get(CHECKOUT_OR_REVISE_PART_LIST);
			if (partSet == null) {
				partSet = new HashSet<String>();
				methodContext.put(CHECKOUT_OR_REVISE_PART_LIST, partSet);
			}
			partSet.add(partMaster.getPersistInfo().getObjectIdentifier().toString());
		}
		
		private void removePartFromCheckoutReviseList(Mastered partMaster) {
			MethodContext methodContext = MethodContext.getContext();
			Set<String> partSet = (Set<String>) methodContext.get(CHECKOUT_OR_REVISE_PART_LIST);
			if (partSet == null) {
				return;
			}
			partSet.remove(partMaster.getPersistInfo().getObjectIdentifier().toString());
		}		
		
		private boolean isPartInCheckoutReviseList(Mastered partMaster) {
			MethodContext methodContext = MethodContext.getContext();
			Set<String> partSet = (Set<String>) methodContext.get(CHECKOUT_OR_REVISE_PART_LIST);
			if (partSet == null) {
				return false;
			}
			return partSet.contains(partMaster.getPersistInfo().getObjectIdentifier().toString());
		}
		

		public void notifyVetoableEvent(Object event) throws Exception {
			if (!(event instanceof KeyedEvent))
				return;

			KeyedEvent keyedEvent = (KeyedEvent) event;
			String eventType = keyedEvent.getEventType();
			Object eventTarget = keyedEvent.getEventTarget();

			boolean isWorkingCopy = false;
			boolean isCheckedOut = false;
			if (eventTarget instanceof Workable) {
				Workable w = (Workable) eventTarget;
				isCheckedOut = WorkInProgressHelper.isCheckedOut(w);
				isWorkingCopy = WorkInProgressHelper.isWorkingCopy(w);
			}

			StringBuffer message = new StringBuffer();
			/*****2017/8/16addbegin*****/
			if(eventType.equals(WorkInProgressServiceEvent.POST_CHECKIN)){ 
				if(eventTarget instanceof WTDocument){
					WTDocument document = (WTDocument) eventTarget;
					String number = document.getNumber();
					document = CommonUtil.getLatestWTDocByNumber(number);
				}else if(eventTarget instanceof WTPart){
					
					//HEX软件PN，将软件版本和硬件版本映射到父级上
					
					WTPart part = (WTPart) eventTarget;
					//setParentSW_HW(part);
				}else if(eventTarget instanceof EPMDocument){
					EPMDocument epm = (EPMDocument) eventTarget;
					if (isCheckedOut && !isWorkingCopy){
						epm = (EPMDocument) WorkInProgressHelper.service.workingCopyOf(epm);
					}
					System.out.println("This is EPMDocument Post Check-in!!!!!!!!!");
					//if (EpmUtil.isCATDrawing(epm)){
						
						System.out.println("Version\t"+epm.getVersionDisplayIdentifier().toString()+"."+(Integer.parseInt(epm.getIterationInfo().getIdentifier().getValue())+1));
					//}
				}
			}
			/******2017/8/16addend*****/
			if (eventType.equals(WorkInProgressServiceEvent.PRE_CHECKOUT)) {
				if (eventTarget instanceof WTPart) {
					WTPart part = (WTPart) eventTarget;
					addPartToCheckoutReviseList(part.getMaster());
				}
			}
			
			if (eventType.equals(WorkInProgressServiceEvent.POST_CHECKOUT)) {
				if (eventTarget instanceof WTPart) {
					WTPart part = (WTPart) eventTarget;
					removePartFromCheckoutReviseList(part.getMaster());
				}
			}

			if (eventType.equals(VersionControlServiceEvent.PRE_NEW_VERSION)) {
				if (eventTarget instanceof WTPart) {
					WTPart part = (WTPart) eventTarget;
					addPartToCheckoutReviseList(part.getMaster());
				}
			}
			if (eventType.equals(WorkInProgressServiceEvent.PRE_CHECKIN)) {
				if (eventTarget instanceof WTPart) {// part 检入check
					WTPart lastPart = (WTPart) eventTarget;
					WTPart part = (WTPart) eventTarget;
					if (isCheckedOut && !isWorkingCopy)
						part = (WTPart) WorkInProgressHelper.service.workingCopyOf(part);
					message.append(checkPartState(part));
					message.append(RDUtil.checkSpeicalStruct(part));
					message.append(PlatformUtil.checkPlatform(part));
					CreateCatlPartProcessor.updateFAEStatus(part);
					BoxExplainHelper.dealBoxExplain(lastPart,part);//处理装箱说明
				} else if (eventTarget instanceof WTDocument) {
					WTDocument document = (WTDocument) eventTarget;
					if (isCheckedOut && !isWorkingCopy)
						document = (WTDocument) WorkInProgressHelper.service.workingCopyOf(document);
					if (isAutocad(document)) {// document 检入check是否包含pdf
						String prefix_number=document.getNumber().substring(0,4);
						String topdf_prefix=PropertiesUtil.getValueByKey("config_topdf_prefix");
						String [] prefix=topdf_prefix.split(",");
						List prefixlist=Arrays.asList(prefix);
                        if(!prefixlist.contains(prefix_number)){
                        	String pdfCheckResult = DocUtil.pdfFileCheck(document);
    						if (pdfCheckResult != null)
    							message.append(pdfCheckResult);
                        }
					} else if (EDatasheetDocUtil.isEDatasheetDoc(document))// Datasheet 检入更新名称
						message.append(megerDatasheetName(document));
					else{
						message.append(checkDocId(document));
					}
					WTDocument doc=CommonUtil.getLatestWTDocByNumberNotWorkable(document.getNumber());
					DWGToPDFQueue.executeWfExpression(WCUtil.getOid(doc));
					try {
						DocUtil.replaceDocumentPrimaryContent(document);
					} catch (Exception e) {
						log.debug("文档模版上传错误，请上传正确的文档模版。文档编号="+document.getNumber());
						e.printStackTrace();
						throw new WTException("文档模版上传错误，请上传正确的文档模版。");
					}
					
				}else if (eventTarget instanceof EPMDocument) {// CADDRAWING
					// 升小版本移除pdf附件
					EPMDocument epm = (EPMDocument) eventTarget;
					if (isCheckedOut && !isWorkingCopy){
						epm = (EPMDocument) WorkInProgressHelper.service.workingCopyOf(epm);
					}
					if (EpmUtil.isCATDrawing(epm)){
						
						System.out.println("Version\t"+epm.getVersionDisplayIdentifier().toString()+"."+(Integer.parseInt(epm.getIterationInfo().getIdentifier().getValue())+1));
					}
				}
			} else if (eventType.equals(WorkInProgressServiceEvent.POST_CHECKOUT)) {
				if (eventTarget instanceof WTDocument) {// AutoCAD 升小版本移除pdf附件
					WTDocument document = (WTDocument) eventTarget;
					if (isCheckedOut && !isWorkingCopy)
						document = (WTDocument) WorkInProgressHelper.service.workingCopyOf(document);
					if (isAutocadOrCatiaDrawing(document))
						removeFilePrintAttachement(document);
					 
				} else if (eventTarget instanceof EPMDocument) {// CADDRAWING
																// 升小版本移除pdf附件
					EPMDocument epm = (EPMDocument) eventTarget;
					if (isCheckedOut && !isWorkingCopy)
						epm = (EPMDocument) WorkInProgressHelper.service.workingCopyOf(epm);
					String epmtype = epm.getDocType().toString();
					if (epmtype.endsWith("CADDRAWING"))
						removeFilePrintAttachement(epm);
				}
			} else if (eventType.equals(VersionControlServiceEvent.NEW_VERSION)) {// 版本升级事件
				
				System.out.println("NEW VERSION");
				if (!isCheckedOut) {// false表示升级大版本 修订
					System.out.println("is checked out");
					if (eventTarget instanceof WTPart) {// part 修订check
						WTPart part = (WTPart) eventTarget;
						removePartAttachement(part);
						part = (WTPart) PartUtil.getPreviousVersion((Versioned) eventTarget);
						if (part != null) {
							String state = part.getState().toString();
							if (state.equalsIgnoreCase(PartState.RELEASED))
								message.append(checkIsInChange(part));
						}
					} else if (eventTarget instanceof EPMDocument) {// epmdocument
																	// 修订check
						EPMDocument epm = (EPMDocument) PartUtil.getPreviousVersion((Versioned) eventTarget);
						
						
						String ecoNumber = " ";
						
						if (epm != null) {
							String state = epm.getState().toString();
							if (state.equalsIgnoreCase(PartState.RELEASED))
								message.append(checkIsInChange(epm));
							
							if(isSCHEPM(epm)){
								WTPart part = PartUtil.getLastestWTPartByNumber(epm.getNumber());
								if(part != null && epm != null){
									createLinkEpmToPart((EPMDocument) eventTarget, part, 6);
								}
							}
							
							List<WTChangeOrder2> ecos = getECOByPersistable(epm);
							if (ecos.size()>0) {
								WTChangeOrder2 order = ecos.get(0);
								ecoNumber = order.getNumber();
							}
							
							EPMDocument epm1 = (EPMDocument) eventTarget;
														
							if(EpmUtil.isCATDrawing(epm1)){
								IBAUtility iba = new IBAUtility(epm1);
								
								ReleasedDateUtil.deleteReleasedDate(epm1,ElecSignConstant.PTC_WM_DESIGN);
								ReleasedDateUtil.deleteReleasedDate(epm1,ElecSignConstant.PTC_WM_DESIGN_DATE);
								ReleasedDateUtil.deleteReleasedDate(epm1,ElecSignConstant.PTC_WM_CHECK);
								ReleasedDateUtil.deleteReleasedDate(epm1,ElecSignConstant.PTC_WM_CHECK_DATE);
								ReleasedDateUtil.deleteReleasedDate(epm1,ElecSignConstant.PTC_WM_TECHN);
								ReleasedDateUtil.deleteReleasedDate(epm1,ElecSignConstant.PTC_WM_TECHN_DATE);
								ReleasedDateUtil.deleteReleasedDate(epm1,ElecSignConstant.PTC_WM_APPRO);
								ReleasedDateUtil.deleteReleasedDate(epm1,ElecSignConstant.PTC_WM_APPRO_DATE);
								ReleasedDateUtil.deleteReleasedDate(epm1,ElecSignConstant.PTC_WM_ECN_NO);
								
								iba.setIBAValue(ElecSignConstant.PTC_WM_ECN_NO, ecoNumber);
								
								epm1 = (EPMDocument) iba.updateAttributeContainer(epm1);
								iba.updateIBAHolder(epm1);
								
							}
							
						}else{
							System.out.println("is new epm"+eventTarget);
							EPMDocument cepm = (EPMDocument)eventTarget;
							System.out.println(VersionControlHelper.getIterationDisplayIdentifier(cepm));
							QueryResult qr = ContentHelper.service.getContentsByRole(cepm, ContentRoleType.PRIMARY);
							System.out.println("New Version Primary Size\t"+qr.size());
						}
					} else if (eventTarget instanceof WTDocument) {// document
																	// 修订check
						/****2017/8/16addbegin****/
						WTDocument document = (WTDocument)eventTarget;
						if(document != null){
							boolean checkoutFlag = WorkInProgressHelper.isCheckedOut(document);
							if(checkoutFlag){
								if(!WorkInProgressHelper.isWorkingCopy(document)){
									document = (WTDocument) WorkInProgressHelper.service.workingCopyOf(document);
								}
								removeFilePrintAttachement(document);
								return;
							}
							removeFilePrintAttachement(document);
							document = (WTDocument) eventTarget;
							DWGToPDFQueue.executeWfExpression(WCUtil.getOid(document));
						}
						
						/****2017/8/16addend****/
						WTDocument doc = (WTDocument) PartUtil.getPreviousVersion((Versioned) eventTarget);

						TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(eventTarget);
						String type = ti.getTypename();
						if (doc != null
								&& (type.endsWith(TypeName.doc_type_autocadDrawing) || type.endsWith(TypeName.doc_type_pcbaDrawing) || type.endsWith(TypeName.doc_type_gerberDoc))) {
							String state = doc.getState().toString();
							if (state.equalsIgnoreCase(PartState.RELEASED)) {
								message.append(checkIsInChange(doc));
							}
							if(type.endsWith(TypeName.doc_type_pcbaDrawing) || type.endsWith(TypeName.doc_type_gerberDoc)){
								WTDocument doc2 = (WTDocument)(eventTarget);
								WTPart part = PartUtil.getRelationPartByDescDoc(doc);
								createReferenceLink(doc2, part);
							}
						}else if (type.contains(CatlConstant.DATASHEET_DOC_TYPE)) {
							WTDocument doc2 = (WTDocument)(eventTarget);
							if (VersionControlHelper.hasPredecessor(doc2) && State.toState("WRITING").equals(doc2.getLifeCycleState())) {
								WorkflowUtil.startWorkFlow("Datasheet审签流程", doc2, null);
							}
						}
						try {
							DocUtil.replaceDocumentPrimaryContent(document);
						} catch (Exception e) {//修订错误不做处理，存在历史文档不是按照文档模版上传
							if(doc!=null){
								log.debug("文档模版上传错误，请上传正确的文档模版。文档编号="+doc.getNumber());
								e.printStackTrace();
							}
						}
					}
				}else{
					System.out.println("is not checked out");
					if(eventTarget instanceof EPMDocument){
						EPMDocument epm = (EPMDocument) eventTarget;
						System.out.println(epm);
					}
				}
			} else if (eventType.equals(PersistenceManagerEvent.INSERT)) {
				if (eventTarget instanceof WTPartDescribeLink)// 说明文档 创建check
																// 不能为设计禁用
					message.append(checkDescribeLink(eventTarget));
				else if (eventTarget instanceof WorkItemLink) {// WorkItemLink
																// 增加权限
					WorkItemLink workItemLink = (WorkItemLink) eventTarget;
					WorkItem workitem = workItemLink.getWorkItem();
					addPermission(workitem);
				} else if (eventTarget instanceof WTPartUsageLink) {

					// BOM结构搭建时的限制
					WTPartUsageLink link = (WTPartUsageLink) eventTarget;

					// 在检出或者修订的场景下，不应该检查这些条件
					WTPart parentPart = (WTPart) link.getRoleAObject();
					if (isPartInCheckoutReviseList(parentPart.getMaster())) {
						return;
					}
					
					WTPartMaster master = (WTPartMaster) link.getRoleBObject();
					WTPart lastPart = (WTPart) PromotionUtil.getLatestVersionByMaster(master);
					log.debug("link==" + link + ",parentPart=="+parentPart.getNumber()+",master==" + master+",lastPart=="+lastPart);
					
					boolean isCheck = true;
					WTPart prePart = (WTPart) PartUtil.getPreIteration(parentPart);
					if(prePart != null){
						QueryResult qr1 = PersistenceHelper.manager.find(
								WTPartUsageLink.class, prePart,
								WTPartUsageLink.USED_BY_ROLE, master);
						if(qr1.hasMoreElements()){
							isCheck = false;
						}
					}
					
					if(isCheck){
						//by hdong 另存为时新版本没有lastPart
						if (lastPart!=null&&lastPart.getState().toString().endsWith(PartState.DISABLEDFORDESIGN)) {
							message.append("零部件" + lastPart.getNumber() + "的状态为设计禁用！\n");
						}

						Set<Persistable> all = PromotionUtil.getAllLatestVersionByMaster(master);
						for (Persistable p : all) {
							WTPart part = (WTPart) p;
							Set<String> number = PromotionUtil.isExsitPromotion(part);
							if (number.size() > 0) {
								String rtn = WorkflowUtil.joinSetMsg(number);
								message.append("零部件" + lastPart.getNumber() + "已经被加入未完成的编号为" + rtn + "的物料设计禁用单中！\n");
							}
						}
					
						MaturityUpReportHelper.checkWTPartUsageLink(link,message);						
					}
					System.out.println(lastPart.getNumber());
					/*System.out.println(PartUtil.isSWPart(lastPart));
					//如果是软件HEX PN，将属性映射到直接父级
					if(PartUtil.isSWPart(lastPart)){
						IBAUtility childiba = new IBAUtility(lastPart);
						String swversion = childiba.getIBAValue("Software_Version");
						String hwversion = childiba.getIBAValue("Hardware_Version");

					
						IBAUtility parentiba = new IBAUtility(parentPart);
						parentiba.setIBAValue("Software_Version", swversion);
						parentiba.setIBAValue("Hardware_Version", hwversion);
						parentiba.updateAttributeContainer(parentPart);
						parentiba.updateIBAHolder(parentPart);			
						
						if(PartUtil.isPCBAPart(parentPart)){
							setParentSW_HW(parentPart);
						}

					}*/
					if(PartUtil.checkSW_HWPart(lastPart)){
						String matchingcode = (String) GenericUtil.getObjectAttributeValue(lastPart, PartConstant.MatchingCode);
						
						String matchMessage = PartUtil.checkMatchingCode(parentPart, matchingcode);
						message.append(matchMessage);
					}
					
				} else if (eventTarget instanceof WTPartSubstituteLink) {
					

					WTPartSubstituteLink link = (WTPartSubstituteLink) eventTarget;
					// 在检出或者修订的场景下，不应该检查这些条件
					WTPartUsageLink usageLink = (WTPartUsageLink) link.getRoleAObject();
					WTPart parentPart = (WTPart) usageLink.getRoleAObject();
					if (isPartInCheckoutReviseList(parentPart.getMaster())) {
						return;
					}
					
					WTPartMaster master = (WTPartMaster) link.getRoleBObject();
					WTPart lastPart = (WTPart) PromotionUtil.getLatestVersionByMaster(master);

					if (lastPart!=null&&lastPart.getState().toString().endsWith(PartState.DISABLEDFORDESIGN)) {
						message.append("替换件" + lastPart.getNumber() + "的状态为设计禁用！\n");
					}

					Set<Persistable> all = PromotionUtil.getAllLatestVersionByMaster(master);
					for (Persistable p : all) {
						WTPart part = (WTPart) p;
						Set<String> number = PromotionUtil.isExsitPromotion(part);
						if (number.size() > 0) {
							String rtn = WorkflowUtil.joinSetMsg(number);
							message.append("替换件" + lastPart.getNumber() + "已经被加入未完成的编号为" + rtn + "的物料设计禁用单中！\n");
						}
					}
					MaturityUpReportHelper.checkWTPartSubstituteLink(link,message);
				}
			} else if (eventType.equals(PersistenceManagerEvent.PRE_REMOVE)) {
				//System.out.println("Remove ......................");
				if (eventTarget instanceof WTPartUsageLink) {

					// BOM结构搭建时的限制
					WTPartUsageLink link = (WTPartUsageLink) eventTarget;

					// 在检出或者修订的场景下，不应该检查这些条件
					WTPart parentPart = (WTPart) link.getRoleAObject();
					if (isPartInCheckoutReviseList(parentPart.getMaster())) {
						return;
					}
					
					WTPartMaster master = (WTPartMaster) link.getRoleBObject();
					WTPart lastPart = (WTPart) PromotionUtil.getLatestVersionByMaster(master);
					log.debug("link==" + link + ",parentPart=="+parentPart.getNumber()+",master==" + master+",lastPart=="+lastPart);
					
					
					
					//如果是软件HEX PN，将软件版本和硬件版本属性从直接父级移除
					/*if(PartUtil.isSWPart(lastPart)){
						//ReleasedDateUtil.deleteReleasedDate(parentPart, "Software_Version");
						//ReleasedDateUtil.deleteReleasedDate(parentPart, "Hardware_Version");
						deleteParentSW_HW(lastPart);
					}*/
					
				}
			}else if (eventType.equals(PersistenceManagerEvent.UPDATE)) {
				if (eventTarget instanceof WorkItem) {// WorkItem 删除权限
					WorkItem workitem = (WorkItem) eventTarget;
					if (workitem.getStatus().equals(WfAssignmentState.COMPLETED))
						clearPermission(workitem);
				}else if(eventTarget instanceof EPMDocument){					
					if (!isCheckedOut) {// false表示升级大版本 修订
						System.out.println("Update is not checked out");						
						EPMDocument epm = (EPMDocument) PartUtil.getPreIteration((Versioned) eventTarget);							
						if (epm == null) {
							
							System.out.println("Update is new epm \t"+eventTarget);
							EPMDocument cepm = (EPMDocument)eventTarget;
							IBAUtility iba = new IBAUtility(cepm);
							String isWriteA1 = iba.getIBAValue("isWriteA1");
							if(!"Y".equals(isWriteA1)){
								if(EpmUtil.isCreo3D(cepm)){
									String partNumber = cepm.getNumber().split("\\.")[0];
									WTPart part = PartUtil.getLastestWTPartByNumber(partNumber);
									if(part != null && cepm != null){
										createLinkEpmToPart((EPMDocument) eventTarget, part, 6);
									}
								}
								System.out.println(VersionControlHelper.getIterationDisplayIdentifier(cepm));
								EPMDocument pepm = EPMUtil.getEPMByNumber(cepm.getNumber());
								String number = pepm.getNumber();
								
								if(epmUpdateCounts.containsKey(number)){
									Integer count = epmUpdateCounts.get(number);
									int countvalue = count.intValue();
									System.out.println("count is " + countvalue);
									System.out.println(PersistenceHelper.isPersistent(pepm));
									if(countvalue==1){
										System.out.println("pepm\t"+pepm);
										QueryResult qr = ContentHelper.service.getContentsByRole(pepm, ContentRoleType.PRIMARY);
										System.out.println("Primary Size\t"+qr.size());
										epmUpdateCounts.remove(number, count);									
										
										System.out.println("Set EPM Version A.1");
										iba.setIBAValue("isWriteA1", "Y");
										iba.updateAttributeContainer(cepm);
										iba.updateIBAHolder(cepm);
										
									}else{
										countvalue++;
										epmUpdateCounts.put(number, countvalue);
									}
								}else{
									epmUpdateCounts.put(number, 0);
								}
							}
						}
					}
				}
			} else if (eventType.equals(PersistenceManagerEvent.PRE_DELETE)) {
				if (eventTarget instanceof WorkItem) {// WorkItem 删除权限
					WorkItem workitem = (WorkItem) eventTarget;
					clearPermission(workitem);
				} else if (eventTarget instanceof WTDocumentMaster) {
					WTDocumentMaster docMaster = (WTDocumentMaster) eventTarget;
					MaturityUpReportHelper.removeAllLinks(docMaster);
				} else if (eventTarget instanceof WTDocument) {
					WTDocument doc = (WTDocument) eventTarget;
					TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(eventTarget);
					String type = ti.getTypename();
					if (doc != null && (type.endsWith(TypeName.rdDoc) || type.endsWith(TypeName.technicalDoc))) {
						message.append(deleteDocumentLinkToRDM(doc));
					}
				}
			} else if (eventType.equals(PersistenceManagerEvent.POST_STORE)) {
				if (eventTarget instanceof RelevantRequestData2) {// 更改请求 check
					message.append(checkChangeRequest(eventTarget));
				} else if (eventTarget instanceof AffectedActivityData) {// ......
					AffectedActivityData affectdata = (AffectedActivityData) eventTarget;
					boolean isDCA = false;
					ChangeActivity2 ca = affectdata.getChangeActivity2();
					if (ca instanceof WTChangeActivity2) {
						isDCA = TypeUtil.isSpecifiedType((WTChangeActivity2) ca, ChangeConst.CHANGETASK_TYPE_DCA);
					}
					Persistable persistable = (Persistable) affectdata.getRoleBObject();
					String number = BomWfUtil.getObjectnumber(persistable);
					log.debug("object numer==" + number);
					log.debug("affectlist size()==========" + affectlist.size());
					if (affectlist.contains(persistable)) {
						affectlist.removeAll(affectlist);
						if (!isDCA) {
							if (persistable instanceof WTPart)
								throw new WTException("错误信息：部件" + number + "已存在于变更任务中，不能重复添加！");
							if (persistable instanceof WTDocument)
								throw new WTException("错误信息：文档" + number + "已存在于变更任务中，不能重复添加！");
							if (persistable instanceof EPMDocument)
								throw new WTException("错误信息：CAD文档" + number + "已存在于变更任务中，不能重复添加！");
						}
					} else {
						affectlist.add(persistable);
					}
				} else if (eventTarget instanceof LWCStructEnumAttTemplate) {
					LWCStructEnumAttTemplate node = (LWCStructEnumAttTemplate) eventTarget;
					NodeConfigHelper.autoCreateNodeConfig(node);
				} else if(eventTarget instanceof EPMReferenceLink){
					EPMReferenceLink link=(EPMReferenceLink) eventTarget;
					EPMDocument roleA=(EPMDocument) link.getRoleAObject();
					EPMDocumentMaster roleB=(EPMDocumentMaster) link.getRoleBObject();
					EPMAuthoringAppType authorapp=roleA.getAuthoringApplication();
					String apptype="";
					if(authorapp!=null){
						apptype=authorapp.getDisplay();
					}
					if(apptype.toUpperCase().contains("CATIA")&roleA.getNumber().endsWith(".CATDRAWING")&(roleB.getNumber().endsWith(".CATPART")||roleB.getNumber().endsWith(".CATPRODUCT"))){
						String roleAnum=roleA.getNumber().split("\\.")[0];
						String roleBnum=roleB.getNumber().split("\\.")[0];
						if(!roleAnum.equals(roleBnum)){
							throw new WTException("错误信息：2D图纸"+roleA.getNumber()+"和关联的3D模型"+roleB.getNumber()+"编码不一致");
						}
						renameEPMDoc(roleA, roleB.getName());
					}else if(roleA.getNumber().endsWith(".SLDDRW")&(roleB.getNumber().endsWith(".SLDPRT")||roleB.getNumber().endsWith(".SLDASM"))){
						String roleAnum=roleA.getNumber().split("\\.")[0];
						String roleBnum=roleB.getNumber().split("\\.")[0];
						String aFullNum = roleA.getNumber();
						String formatA=aFullNum.substring(aFullNum.lastIndexOf("."));
						if(!roleAnum.equals(roleBnum)){
							renumberEPMDoc(roleA, roleAnum+formatA);
						}
					}
					
				}else if(eventTarget instanceof EPMDocument){
					System.out.println("EPMDocument is instanceof POST STORE");
					if(!isCheckedOut){
						EPMDocument epm = (EPMDocument) eventTarget;
						//ReleasedDateUtil.deleteReleasedDate(epm, "ReleasedDate");
					}
					
				}else if (eventTarget instanceof WTPart) {// part 修订check
					if(!isCheckedOut){
						WTPart part = (WTPart) eventTarget;
						ReleasedDateUtil.deleteReleasedDate(part, "ReleasedDate");
					}
				}else if(eventTarget instanceof WTDocument){
					if(!isCheckedOut){
						WTDocument doc = (WTDocument) eventTarget;
						if(isAutocad(doc)){
							ReleasedDateUtil.deleteReleasedDate(doc, "ReleasedDate");
						}else{
							ReleasedDateUtil.deleteReleasedDate(doc, "CATL_DOC_ActualTakeEffectTime");
						}
					}
				}
				// else if(eventTarget instanceof LWCPropertyValue){
				// LWCPropertyValue pv = (LWCPropertyValue)eventTarget;
				// LWCPropertyDefinition proDef = pv.getProperty();
				// String proName = proDef.getName();
				// String proValue = pv.getValue();
				// if(StringUtils.equals(proName, "instantiable") &&
				// StringUtils.equals(proValue, "true")){
				// Object obj = pv.getHolderReference().getObject();
				// if(obj instanceof LWCStructEnumAttTemplate){
				// NodeConfigHelper.autoCreateNodeConfig((LWCStructEnumAttTemplate)obj);
				// }
				// }
				// }
			} else if (eventType.equals("PRE_DELETE_TYPE")) {
				if (eventTarget instanceof Map) {
					Map<?, ?> map = (Map<?, ?>) eventTarget;
					String flavor = (String) map.get(TypeModificationEvent.EVENT_DATA_KEYS.ATT_TEMPLATE_FLAVOR);
					if (StringUtils.equals(flavor, "LWCSTRUCT")) {
						String nodeName = (String) map.get(TypeModificationEvent.EVENT_DATA_KEYS.TYPE_NAME);
						LWCStructEnumAttTemplate node = NodeConfigHelper.getClassificationNode(nodeName);
						NodeConfigHelper.deleteNodeConfig(node);
					}
				}
			} else if (eventType.equals(LifeCycleServiceEvent.STATE_CHANGE)) {
				if (eventTarget instanceof WTDocument) {
					WTDocument doc = (WTDocument) eventTarget;
					TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(eventTarget);
					String type = ti.getTypename();
					if (doc != null && (type.endsWith(TypeName.rdDoc) || type.endsWith(TypeName.technicalDoc))) {
						String state = doc.getState().toString();
						if (state.equalsIgnoreCase(PartState.RELEASED)) {
							updateDocumentStatusToRDM(doc);
						}
					}
				}
			}

			if (message.length() > 0)
				throw new WTException(message.toString());
		}
	}

	/**
	 * 文档状态已发布时 发送给RDM
	 * 
	 * @param doc
	 * @throws WTException
	 */
	private void updateDocumentStatusToRDM(WTDocument doc) throws WTException {
		ObjectLinkedByRdm rdm = null;
		WebServiceTransactionLog transactionLog = null;
		WebServiceTransactionInfo transactionInfo = null;
		Map<String, String> map = new HashMap<String, String>();
		map.put("branchId", doc.getBranchIdentifier() + "");
		map.put("docNumber", doc.getNumber());
		map.put("objectType", ObjectLinkedByRdm.class.getName());
		rdm = RdmIntegrationHelper.queryObjectLinkedByRdm(map);
		if (rdm != null) {
			try {
				transactionLog = new WebServiceTransactionLog();
				transactionInfo = new WebServiceTransactionInfo();
				transactionLog.setServiceType(WebServiceTransactionLog.SERVICE_TYPE_CLIENT);
				transactionLog.setServiceSide("ERP");
				transactionLog.setClientSide("PLM");
				transactionLog.setClientId(" ");
				transactionLog.setClientClass(RdmIntegrationHelper.class.getName());
				transactionLog.setClientMethod("updateDocumentStatusToRDM");
				transactionLog.setStartTime(new Timestamp(System.currentTimeMillis()));
				transactionLog.setTransactionInfo(transactionInfo);
				transactionInfo.setParameterObject(map);
				map.put("state", "已发布");
				map.put("deliverableId", rdm.getDeliverableId());
				String ret = RdmIntegrationHelper.updateDocumentStatusToRDM(map);
				if (ret != null) {
					JSONObject json = new JSONObject(ret);
					String msg = json.getString("message");
					String result = json.getString("result");
					if (result != null && !result.equals("0")) {
						EMailMessage localEMailMessage = EMailMessage.newEMailMessage();
						localEMailMessage.addRecipient(OrganizationServicesHelper.manager.getPrincipal("60011102"));
						localEMailMessage.addRecipient(OrganizationServicesHelper.manager.getPrincipal("orgadmin"));
						localEMailMessage.setSubject("RDM集成失败：状态更新失败");
						localEMailMessage.addPart("参数docNumber=" + doc.getNumber() + "，branchId=" + doc.getBranchIdentifier() + "，deliverableId=" + rdm.getDeliverableId()
								+ "。\nRDM反馈消息：" + msg, "text/plain");
						localEMailMessage.send(true);
					}
				}
				transactionLog.setFinishTime(new Timestamp(System.currentTimeMillis()));
				transactionLog.setStatus(WebServiceTransactionLog.STATUS_SUCCESS);
				transactionInfo.setResultObject(ret);
			} catch (Exception e) {
				e.printStackTrace();
				if (transactionLog != null) {
					transactionLog.setFinishTime(new Timestamp(System.currentTimeMillis()));
					transactionLog.setStatus(WebServiceTransactionLog.STATUS_FAILED);
					transactionInfo.setException(e);
				}
				try {
					EMailMessage localEMailMessage = EMailMessage.newEMailMessage();
					localEMailMessage.addRecipient(OrganizationServicesHelper.manager.getPrincipal("60011102"));
					localEMailMessage.addRecipient(OrganizationServicesHelper.manager.getPrincipal("orgadmin"));
					localEMailMessage.setSubject("RDM集成失败：状态更新失败");
					if (rdm != null)
						localEMailMessage.addPart("参数docNumber=" + doc.getNumber() + "，branchId=" + doc.getBranchIdentifier() + "，deliverableId=" + rdm.getDeliverableId()
								+ "。\n异常信息：" + e.getMessage(), "text/plain");
					else
						localEMailMessage.addPart("参数docNumber=" + doc.getNumber() + "，branchId=" + doc.getBranchIdentifier() + "。\n异常信息：" + e.getMessage(), "text/plain");
					localEMailMessage.send(true);
				} catch (WTException e1) {
					e1.printStackTrace();
				}
			} finally {
				if(transactionLog != null)
					TransactionLogHelper.logTransaction(transactionLog);
			}
		}

	}

	private String deleteDocumentLinkToRDM(WTDocument doc) throws IOException, WTException, JSONException {
		StringBuffer buffer = new StringBuffer();
		ObjectLinkedByRdm rdm = null;
		WebServiceTransactionLog transactionLog = null;
		WebServiceTransactionInfo transactionInfo = null;
		try {
			Map<String, String> map = new HashMap<String, String>();
			map.put("branchId", doc.getBranchIdentifier() + "");
			map.put("docNumber", doc.getNumber());
			map.put("objectType", ObjectLinkedByRdm.class.getName());
			rdm = RdmIntegrationHelper.queryObjectLinkedByRdm(map);

			if (rdm != null) {
				transactionLog = new WebServiceTransactionLog();
				transactionInfo = new WebServiceTransactionInfo();

				transactionLog.setServiceType(WebServiceTransactionLog.SERVICE_TYPE_CLIENT);
				transactionLog.setServiceSide("ERP");
				transactionLog.setClientSide("PLM");
				transactionLog.setClientId(" ");
				transactionLog.setClientClass(RdmIntegrationHelper.class.getName());
				transactionLog.setClientMethod("deleteDocumentLinkToRDM");
				transactionLog.setStartTime(new Timestamp(System.currentTimeMillis()));
				transactionLog.setTransactionInfo(transactionInfo);
				transactionInfo.setParameterObject(map);
				map.put("deliverableId", rdm.getDeliverableId());
				String ret = RdmIntegrationHelper.deleteDocumentLinkToRDM(map);
				if (ret != null) {
					JSONObject json = new JSONObject(ret);
					String msg = json.getString("message");
					String result = json.getString("result");
					if (result != null && !result.equals("0")) {
						buffer.append("RDM集成失败：文档删除失败  ");
						buffer.append("参数docNumber=" + doc.getNumber() + "，branchId=" + doc.getBranchIdentifier() + "，deliverableId=" + rdm.getDeliverableId() + "。\nRDM反馈消息："
								+ msg);
					}
				}
				transactionLog.setFinishTime(new Timestamp(System.currentTimeMillis()));
				transactionLog.setStatus(WebServiceTransactionLog.STATUS_SUCCESS);
				transactionInfo.setResultObject(ret);
			}
		} finally {
			if (transactionLog != null && transactionLog.getStatus() == null) {
				transactionLog.setFinishTime(new Timestamp(System.currentTimeMillis()));
				transactionLog.setStatus(WebServiceTransactionLog.STATUS_FAILED);
				TransactionLogHelper.logTransaction(transactionLog);
			}
		}
		return buffer.toString();
	}

	/**
	 * 删除权限
	 * 
	 * @param workItem
	 * @throws WTException
	 */
	private void clearPermission(WorkItem workItem) throws WTException {
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			AccessPermissionSet permissionSet = new AccessPermissionSet();
			permissionSet.add(AccessPermission.READ);
			permissionSet.add(AccessPermission.DOWNLOAD);
			ArrayList<WTCollection> objlist = getRelatedObjects(workItem);
			WTCollection collection = objlist.get(0);
			WTCollection ecadcollection = objlist.get(1);
			WTPrincipal principal = OwnershipHelper.getOwner(workItem);
			if (log.isDebugEnabled() && collection.size() > 0) {
				log.debug("Remove Read Permission for User " + principal.getName() + " for objcet " + workItem.getPrimaryBusinessObject().getObject());
			}
			WTPrincipalReference principalRef = WTPrincipalReference.newWTPrincipalReference(principal);
			AccessControlServerHelper.manager.removePermission(collection, principalRef, AccessPermission.READ, AdHocAccessKey.WNC_WORK_ITEM);
			AccessControlServerHelper.manager.removePermission(collection, principalRef, AccessPermission.DOWNLOAD, AdHocAccessKey.WNC_WORK_ITEM);
			PersistenceServerHelper.manager.update(collection);
			
			//update by szeng 原理图、PCB图给只读权限
			AccessControlServerHelper.manager.removePermission(ecadcollection, principalRef, AccessPermission.READ, AdHocAccessKey.WNC_WORK_ITEM);
			PersistenceServerHelper.manager.update(ecadcollection);
			
		} finally {
			SessionServerHelper.manager.setAccessEnforced(flag);
		}

	}

	/**
	 * 增加权限
	 * 
	 * @param workItem
	 * @throws WTException
	 */
	private void addPermission(WorkItem workItem) throws WTException {
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			AccessPermissionSet permissionSet = new AccessPermissionSet();
			permissionSet.add(AccessPermission.READ);
			permissionSet.add(AccessPermission.DOWNLOAD);
			ArrayList<WTCollection> objlist = getRelatedObjects(workItem);
			WTCollection collection = objlist.get(0);
			WTCollection ecadcollection = objlist.get(1);
			WTPrincipal principal = OwnershipHelper.getOwner(workItem);
			if (log.isDebugEnabled() && collection.size() > 0) {
				log.debug("Add Read Permission for User " + principal.getName() + " for objcet " + workItem.getPrimaryBusinessObject().getObject());
			}
			WTPrincipalReference principalRef = WTPrincipalReference.newWTPrincipalReference(principal);
			AccessControlServerHelper.manager.addPermissions(collection, principalRef, permissionSet, AdHocAccessKey.WNC_WORK_ITEM);
			PersistenceServerHelper.manager.update(collection);
			
			//update by szeng 原理图、PCB图给只读权限
			AccessPermissionSet ecadpermissionSet = new AccessPermissionSet();
			ecadpermissionSet.add(AccessPermission.READ);
			AccessControlServerHelper.manager.addPermissions(ecadcollection, principalRef, ecadpermissionSet, AdHocAccessKey.WNC_WORK_ITEM);
			PersistenceServerHelper.manager.update(ecadcollection);

		} finally {
			SessionServerHelper.manager.setAccessEnforced(flag);
		}

	}

	/**
	 * 任务关联的对象
	 * 
	 * @param workItem
	 * @return
	 * @throws WTException
	 */
	private ArrayList<WTCollection> getRelatedObjects(WorkItem workItem) throws WTException {
		ArrayList<WTCollection> objectList = new ArrayList<WTCollection>();
		WTCollection collection = new WTArrayList();
		WTCollection ecadCollection = new WTArrayList();
		
		PersistentReference pRef = workItem.getPrimaryBusinessObject();
		if (pRef != null && pRef.getReferencedClass() != null) {
			Object object = pRef.getObject();
			QueryResult qr = null;
			if (object instanceof PromotionNotice) {
				PromotionNotice pn = (PromotionNotice) object;
				MaturityBaseline baseline = pn.getConfiguration();
				qr = BaselineHelper.service.getBaselineItems(baseline);
			} else if (object instanceof WTChangeRequest2) {
				WTChangeRequest2 ecr = (WTChangeRequest2) object;
				qr = ChangeHelper2.service.getChangeables(ecr);
			} else if (object instanceof WTChangeOrder2) {
				WTChangeOrder2 eco = (WTChangeOrder2) object;
				qr = ChangeHelper2.service.getChangeablesBefore(eco);
			} else if (object instanceof WTChangeActivity2) {
				WTChangeActivity2 eca = (WTChangeActivity2) object;
				qr = ChangeHelper2.service.getChangeablesBefore(eca);
			}
			while (qr != null && qr.hasMoreElements()) {
				AccessControlled accessControlledObject = (AccessControlled) qr.nextElement();
				//update by szeng 原理图、PCB图给只读权限
				if(accessControlledObject instanceof Persistable){
					String type = ECADutil.getStrSplit((Persistable) accessControlledObject);
					if (type.equalsIgnoreCase(ECADConst.SCHTYPE)||type.equalsIgnoreCase(ECADConst.PCBTYPE)) {
						ecadCollection.add(accessControlledObject);
					}else{
						collection.add(accessControlledObject);
					}
				}else{
					collection.add(accessControlledObject);
				}
				
			}
		}
		objectList.add(collection);
		objectList.add(ecadCollection);
		return objectList;
	}

	/**
	 * 修订检查是否 有变更活动
	 * 
	 * @param workable
	 * @return
	 * @throws WTException
	 */
	private String checkIsInChange(Workable workable) throws WTException {

		StringBuffer message = new StringBuffer();
		WTPrincipal userPrincipal = SessionHelper.manager.getPrincipal();
		if (CatlFinderCreator.isDmsGroup(userPrincipal) || isSiteAdmin(userPrincipal))
			return message.toString();

		boolean accessEnforced = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			QueryResult ecaResult = new QueryResult();
			String ecaState = "";
			ecaResult = ChangeHelper2.service.getAffectingChangeActivities((Changeable2) workable);
			WTPart relationPart = PartUtil.getRelationPart(workable);
			if (relationPart == null) {
				return message.toString();
			}
			String maturity = (String) GenericUtil.getObjectMasteredAttributeValue(relationPart.getMaster(), "CATL_Maturity");

			boolean inChange = false;
			if (Constant.MATURITY_ONE.equals(maturity)) {
				while (ecaResult.hasMoreElements()) {
					WTChangeActivity2 eca = (WTChangeActivity2) ecaResult.nextElement();
					TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(eca);
					String type = ti.getTypename();
					ecaState = eca.getState().toString();
					if (type.endsWith(TypeName.dca) && ecaState.endsWith(ChangeState.IMPLEMENTATION)) {
						inChange = true;
					}
				}
				if (!inChange)
					message.append(workable.getMaster().getIdentity() + "对象没有再实施状态的设计变更任务中，不能进行修订操作。  \n");
			} else if (Constant.MATURITY_THREE.equals(maturity) || Constant.MATURITY_SIX.equals(maturity)) {
				while (ecaResult.hasMoreElements()) {
					WTChangeActivity2 eca = (WTChangeActivity2) ecaResult.nextElement();
					TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(eca);
					String type = ti.getTypename();
					ecaState = eca.getState().toString();
					if (type.endsWith(TypeName.eca) && ecaState.endsWith(ChangeState.IMPLEMENTATION)) {
						inChange = true;
					}
				}
				if (!inChange) {
					message.append(workable.getMaster().getIdentity() + "对象没有在实施状态的变更任务中，不能进行修订操作。  \n");
				}
			}
		} finally {
			SessionServerHelper.manager.setAccessEnforced(accessEnforced);
		}

		return message.toString();
	}

	/**
	 * 更改请求 check
	 * 
	 * @param eventTarget
	 * @return
	 * @throws WTException
	 * @throws ChangeException2
	 */
	private String checkChangeRequest(Object eventTarget) throws WTException, ChangeException2 {

		StringBuffer message = new StringBuffer();

		RelevantRequestData2 ecrdata = (RelevantRequestData2) eventTarget;
		Persistable persistable = ecrdata.getRoleBObject();
		WTChangeRequest2 changeRequest2 = (WTChangeRequest2) ecrdata.getRoleAObject();
		log.debug("ecr number ==" + changeRequest2.getNumber());
		Changeable2 changeable = (Changeable2) persistable;
		RevisionControlled revisionControlled = (RevisionControlled) persistable;
		String number = BomWfUtil.getObjectnumber(changeable);

		String state = revisionControlled.getState().toString();
		log.debug("number==" + number + "---state" + state);
		if (!state.equalsIgnoreCase("RELEASED")) {
			message.append(number + ":不是已发布的状态,不能添加到变更请求中！\n");
		}
		if (!ECWorkflowUtil.isLastVersion(revisionControlled)) {
			message.append(number + "不是最新版本！请提交最新的版本! \n");
		}
		QueryResult ecaResult = ChangeHelper2.service.getAffectingChangeActivities(changeable);
		log.debug("ecrResult size==" + ecaResult.size());

		while (ecaResult.hasMoreElements()) {
			Object obj = (Object) ecaResult.nextElement();
			WTChangeActivity2 eca = (WTChangeActivity2) obj;
			log.debug("associated with eca=" + eca.getNumber());
			if (!eca.getState().toString().equalsIgnoreCase("CANCELLED") && !eca.getState().toString().equalsIgnoreCase("RESOLVED")) {
				message.append(number + ":有正在进行的变更任务:" + eca.getNumber() + "\n");
			}
		}
		return message.toString();
	}

	/**
	 * 说明文档 check 双方不能为设计禁用
	 * 
	 * @param eventTarget
	 * @return
	 * @throws WTException
	 */
	private String checkDescribeLink(Object eventTarget) throws WTException {
		StringBuffer message = new StringBuffer();
		WTPartDescribeLink descLink = (WTPartDescribeLink) eventTarget;
		WTPart part = descLink.getDescribes();
		WTDocument doc = descLink.getDescribedBy();
		String partState = part.getState().toString();
		String docState = doc.getState().toString();
		if (partState.equalsIgnoreCase(PartState.DISABLEDFORDESIGN))
			message.append("部件" + part.getNumber() + "的状态为设计禁用，不允许与文档建立关联\n");
		if (docState.equalsIgnoreCase(DocState.DISABLEDFORDESIGN))
			message.append("文档" + doc.getNumber() + "的状态为设计禁用，不允许与文档部件建立关联\n");
		if (message.length() > 0)
			throw new WTException(message.toString());
		return message.toString();
	}

	/**
	 * 移除与物料编号相同的pdf附件
	 * 
	 * @param part
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private void removePartAttachement(WTPart part) throws WTException, WTPropertyVetoException {
		QueryResult qr = ContentHelper.service.getContentsByRole(part, ContentRoleType.SECONDARY);
		while (qr.hasMoreElements()) {
			Object obj = qr.nextElement();
			if (obj instanceof ApplicationData) {
				ApplicationData fileContent = (ApplicationData) obj;
				if (fileContent.getFileName().equalsIgnoreCase(part.getNumber() + ".pdf") || fileContent.getFileName().equalsIgnoreCase(part.getNumber() + "_RELEASE.pdf")) {
					log.debug("修订零部件发现存在与编号一样的pdf，执行删除操作 " + fileContent.getFileName());
					ContentServerHelper.service.deleteContent(part, fileContent);
				}
			}
		}
	}

	/**
	 * Datasheet 更改名称
	 * 
	 * @param document
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private String megerDatasheetName(WTDocument document) throws WTException, WTPropertyVetoException {
		StringBuffer message = new StringBuffer();
		String brand = (String) GenericUtil.getObjectAttributeValue(document, "CATL_Brand");
		String model = (String) GenericUtil.getObjectAttributeValue(document, "CATL_Model");
		String name = brand + "_" + model;
		if (!document.getName().equalsIgnoreCase(name)) {
			WTDocument existDoc = EDatasheetDocUtil.getEDatasheetDocByName(name, document.getNumber());
			if (existDoc != null) {
				message.append("系统中已存在名称为[" + name + "]的Datasheet,文档编号为[" + existDoc.getNumber() + "]");
			} else {
				WTPrincipal user = SessionHelper.manager.getPrincipal();
				try {
					SessionHelper.manager.setAdministrator();
					Identified identified = (Identified) document.getMaster();
					WTDocumentMasterIdentity identity = (WTDocumentMasterIdentity) identified.getIdentificationObject();
					identity.setName(name);
					IdentityHelper.service.changeIdentity(identified, identity);
				} finally {
					SessionHelper.manager.setPrincipal(user.getName());
				}
			}
		}
		return message.toString();
	}

	/**
	 * 文档IBA属性 docID校验唯一性
	 * 
	 * @param document
	 * @return
	 * @throws WTException
	 */
	private String checkDocId(WTDocument document) throws WTException {
		StringBuffer message = new StringBuffer();
		String docID = (String) GenericUtil.getObjectAttributeValue(document, "docID");
		if (docID != null && !docID.equalsIgnoreCase("Default") && !docID.trim().equals("")) {
			Set<WTDocument> set = DocUtil.getLastedDocByStringIBAValue(document.getNumber(), document.getVersionIdentifier().getValue(), "docID", docID);
			for (WTDocument temp : set) {
				message.append("文档ID:" + docID + "不是唯一,已被文档:" + temp.getNumber() + "使用\n");
			}
		}
		return message.toString();
	}

	/**
	 * Search BOM
	 * 
	 * @param part
	 * @param listnumber
	 * @param liststate
	 * @throws Exception
	 */
	public void checkBom(WTPart part, StringBuffer message) throws Exception {
		String state = "";
		String amountvalue = "";
		// bom cat not have the same part
		List<String> numberlist = new ArrayList<String>();
		QueryResult qr = WTPartHelper.service.getUsesWTPartMasters(part);
		log.debug("Check BOM parent part = " + part.getNumber());
		String magnificationPartGroup = PropertiesUtil.getValueByKey("magnificationPartGroup");//放大倍数物料组
		while (qr.hasMoreElements()) {
			WTPartUsageLink link = (WTPartUsageLink) qr.nextElement();
			checkSubpart(link, message);
			double amount = link.getQuantity().getAmount();
			String amountString = String.valueOf(amount);

			WTPartMaster master = link.getUses();
			if (numberlist.contains(master.getNumber())) {
				message.append("部件:" + master.getNumber() + "已被添加到bom中不能重复添加！ \n");
				//log.debug("Duplicate Child Part number = " + master.getNumber());
			} else {
				numberlist.add(master.getNumber());
				//log.debug("add Child Part Number = " + master.getNumber());
			}
			//子件 放大倍数逻辑处理 start
			Object magnificationObj = IBAUtil.getIBAValue(link, "CATL_MAGNIFICATION");//放大倍数
			if(magnificationObj != null && !magnificationPartGroup.contains(master.getNumber().substring(0, 6))){
				message.append("部件:" + master.getNumber() + "不能填写 放大倍数！ \n");
				//log.debug("magnification Child Part number = " + master.getNumber());
			}else if(magnificationObj != null && (Long)magnificationObj == 0){
				message.append("部件:" + master.getNumber() + "放大倍数不能为0,最小为1 \n");
				//log.debug("magnification Child Part number = " + master.getNumber());
			}
			//子件 放大倍数逻辑处理 
			//替代件 放大倍数逻辑处理 start
			WTCollection collection = WTPartHelper.service.getSubstituteLinks(link);
			if (!collection.isEmpty()) {
				Iterator itr = collection.iterator();
				while (itr.hasNext()) {
					ObjectReference objReference = (ObjectReference) itr.next();
					WTPartSubstituteLink subLink = (WTPartSubstituteLink) objReference.getObject();
					WTPartMaster subpartMaster = (WTPartMaster) subLink.getSubstitutes();
					magnificationObj = IBAUtil.getIBAValue(subLink, "CATL_MAGNIFICATION");//放大倍数
					if(magnificationObj != null && !magnificationPartGroup.contains(subpartMaster.getNumber().substring(0, 6))){
						message.append("替代件:" + subpartMaster.getNumber() + "不能填写 放大倍数！ \n");
						//log.debug("magnification subChild Part number = " + subpartMaster.getNumber());
					}else if(magnificationObj != null && (Long)magnificationObj == 0){
						message.append("替代件:" + subpartMaster.getNumber() + "放大倍数不能为0,最小为1 \n");
						//log.debug("magnification subChild Part number = " + subpartMaster.getNumber());
					}
				}
			}
			//替代件 放大倍数逻辑处理 end
			WTPart sunpart = PartUtil.getLastestWTPartByNumber(master.getNumber());
			if(sunpart != null){
				state = sunpart.getState().toString();
				//log.debug("part number==" + sunpart.getNumber() + "state==" + state);
				String containerName = sunpart.getContainerName();
				WTContainer container = sunpart.getContainer();
				if (container instanceof WTLibrary) {
					//log.debug("child part state ------->" + sunpart.getNumber() + ":" + state);
					if (!state.equalsIgnoreCase("RELEASED")) {
						String number = sunpart.getNumber();
						message.append("部件:" + number + "所在位置为存储库:" + containerName + ",生命周期状态,不符合业务规范,检入的部件必须是已发布状态。\n");
					}
					//log.debug("sun part  amount===" + sunpart.getNumber() + "===" + amountString);
					//log.debug("amountvalue==" + amountvalue + amountvalue.length());
				} else {
					if (state.equalsIgnoreCase("WRITING") || state.equalsIgnoreCase("MODIFICATION") || state.equalsIgnoreCase("REVIEW")) {
						String number = sunpart.getNumber();
						message.append("部件:" + number + "所在位置为产品库:" + containerName + ",生命周期状态,不符合业务规范,检入的部件不能是“编制”，“修改”或“审阅”的状态。\n");
					}
				}
				if (amountString.indexOf(".") > 0) {
					amountvalue = amountString.substring(amountString.indexOf(".") + 1, amountString.length() - 1);
					if (amountvalue.length() > 3) {
						message.append("部件" + sunpart.getNumber() + "的数量为：" + amount + "不符合业务规则，小数点后不能超过三位。\n");
					}
				}
			}else{
				message.append("您没有访问部件:" + master.getNumber() + "的权限！ \n");
			}
		}

	}

	/**
	 * 子件 check
	 * 
	 * @param usageLink
	 * @param message
	 * @throws WTException
	 */
	private void checkSubpart(WTPartUsageLink usageLink, StringBuffer message) throws WTException {
		ArrayList<String> subpartList = new ArrayList<String>();
		subpartList = getSubstitutePart(usageLink, message);
		for (int i = 0; i < subpartList.size(); i++) {
			String number = subpartList.get(i);
			WTPart subpart = PartUtil.getLastestWTPartByNumber(number);
			WTContainer container = subpart.getContainer();
			String state = subpart.getState().toString();
			if (!state.equalsIgnoreCase("RELEASED"))
				message.append("替代件:" + number + "所在位置为:" + container.getName() + ",生命周期状态不是已发布状态，不符合业务规范。\n");
		}

	}

	/**
	 * 获得替代件
	 * 
	 * @param link
	 * @param message
	 * @return
	 * @throws WTException
	 */
	private ArrayList<String> getSubstitutePart(WTPartUsageLink link, StringBuffer message) throws WTException {
		ArrayList<String> substitutepartlList = new ArrayList<String>();
		WTCollection collection = WTPartHelper.service.getSubstituteLinks(link);
		// log.debug("Substitute part count=" + collection.size());
		if (!collection.isEmpty()) {
			Iterator itr = collection.iterator();
			while (itr.hasNext()) {
				ObjectReference objReference = (ObjectReference) itr.next();
				WTPartSubstituteLink subLink = (WTPartSubstituteLink) objReference.getObject();
				WTPartMaster partMaster = (WTPartMaster) subLink.getSubstitutes();

				String unit = partMaster.getDefaultUnit().getDisplay();
				// listmessage.add("unit="+unit+",amount="+amount);
				if (subLink.getQuantity() != null) {
					Double amount = subLink.getQuantity().getAmount();
					if (amount != null) {
						String amountStr = String.valueOf(amount);
						if (amountStr.indexOf(".") != -1) {
							String temp = amountStr.substring(amountStr.indexOf(".") + 1);
							if (!temp.equals("0") && unit.equals("pcs")) {
								message.append("替代部件" + partMaster.getNumber() + "的数量为：" + amount + "，单位为:" + unit + "，不符合业务规则，单位为\"个\"时,值必须为整数。\n");
							}
							if (temp.length() > 3) {
								message.append("替代部件" + partMaster.getNumber() + "的数量为：" + amount + "不符合业务规则，小数点后不能超过三位。\n");
							}
						}
					}
				}

				substitutepartlList.add(partMaster.getNumber());
				log.debug("sub part number===" + partMaster.getNumber());
			}
		}
		return substitutepartlList;
	}

	/**
	 * check bom has released or not?
	 * 
	 * @param part
	 * @throws Exception
	 */
	private String checkPartState(WTPart part) throws Exception {
		StringBuffer message = new StringBuffer();
		checkBom(part, message);
		log.debug("BOM Check Error" + message);

		return message.toString();
	}

	/**
	 * 
	 * @param doc
	 * @return
	 * @throws WTException
	 */
	private Boolean isAutocadOrCatiaDrawing(WTDocument doc) throws WTException {
		Boolean isdoc = false;
		if (null == doc)
			return isdoc;

		String doctypeString = "";
		doctypeString = DocUtil.getObjectType(doc).toString();
		log.debug("doc type==" + doc.getNumber() + ":" + doctypeString);
		if (doctypeString.endsWith(CatlConstant.AUTOCAD_DOC_TYPE) || doctypeString.endsWith(CatlConstant.CATIA_DOC_TYPE)) {
			isdoc = true;
		}
		return isdoc;
	}

	private Boolean isAutocad(WTDocument doc) throws WTException {
		Boolean isdoc = false;
		if (null == doc)
			return isdoc;

		String doctypeString = DocUtil.getObjectType(doc).toString();
		log.debug("doc type==" + doc.getNumber() + ":" + doctypeString);
		if (doctypeString.endsWith(CatlConstant.AUTOCAD_DOC_TYPE)) {
			isdoc = true;
		}
		return isdoc;
	}

	/**
	 * 移除pdf附件
	 * 
	 * @param contentholder
	 * @throws WTException
	 * @throws PropertyVetoException
	 */
	private void removeFilePrintAttachement(ContentHolder contentholder) throws WTException, PropertyVetoException {
		log.debug(">>>>>>" + StandardCatlDocumentListenerService.class.getName() + ".removeFilePrintAttachement()...");
		if (contentholder != null) {
			ContentHolder doc = ContentHelper.service.getContents(contentholder);
			Vector vApplicationData = ContentHelper.getApplicationData(doc);
			for (int i = 0; i < vApplicationData.size(); i++) {
				ApplicationData applicationdata = (ApplicationData) vApplicationData.elementAt(i);
				log.debug("...removeAttachment 清除附件：" + applicationdata.getFileName());
				if (applicationdata.getFileName().endsWith("pdf")) {
					ContentServerHelper.service.deleteContent(doc, applicationdata);
				}
			}
		}
		log.debug("<<<<<<" + StandardCatlDocumentListenerService.class.getName() + ".removeFilePrintAttachement().");
	}

	/**
	 * 是否管理员
	 * 
	 * @param wtPrincipal
	 * @return
	 * @throws WTException
	 */
	private boolean isSiteAdmin(WTPrincipal wtPrincipal) throws WTException {
		return WTContainerHelper.service.isAdministrator(WTContainerHelper.service.getExchangeRef(), wtPrincipal);
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
	 * 判断是否为原理图
	 * 
	 * @param epm
	 * @return
	 */
	public static boolean isSCHEPM(EPMDocument epm) {
		if (epm != null) {
			String type = getStrSplit(epm);
			if (type.equalsIgnoreCase("com.CATLBattery.ECADSchematic")) {
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
			if (type.equalsIgnoreCase("com.CATLBattery.ECADBoard")) {
				return true;
			}
		}
		return false;
	}

	
	/**
	 * 创建部件与EPM的关联关系
	 * 
	 * @param epm
	 * @param part
	 * @param buildtype
	 *            7 所有者
	 * @throws WTException
	 */
	public static void createLinkEpmToPart(EPMDocument epm, WTPart part, int buildtype) throws WTException {
		try {
			QueryResult qr = PersistenceHelper.manager.navigate(epm, EPMBuildRule.BUILD_TARGET_ROLE, EPMBuildRule.class,
					false);

			EPMBuildRule epmbuildRule = null;
			if (qr != null && qr.hasMoreElements()) {
				epmbuildRule = (EPMBuildRule) qr.nextElement();
			} else {
				EPMBuildRule epmbr = EPMBuildRule.newEPMBuildRule(epm, part, buildtype);
				String sq = PersistenceHelper.manager.getNextSequence(EPMBuildRuleSequence.class);
				epmbr.setUniqueID(Long.parseLong(sq));
				PersistenceServerHelper.manager.insert(epmbr);
			}

		} catch (WTRuntimeException | WTException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}

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
	 * 判断图档是否为设备开发三维图档
	 * 
	 * @param epm
	 * @return
	 */
	public static boolean isEquipmentCAD(EPMDocument epm) {
		if (epm != null) {
			if(ECADutil.isSCHEPM(epm)||ECADutil.isPCBEPM(epm)){
				return false;
			}
			String number = epm.getNumber();
			String cadName = epm.getCADName();
			String format = cadName.substring(cadName.lastIndexOf("."));

			 if(format.equalsIgnoreCase(".sldprt")||format.equalsIgnoreCase(".sldasm")){			
				if ((number.startsWith("CK")||number.startsWith("CM")||number.startsWith("CP")) & number.length() == 18) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * 更新图档编号
	 * @param EPMNumber	图档编号
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void renumberEPMDoc(EPMDocument doc,String number) throws WTException, WTPropertyVetoException{
		
		//String docNumber = doc.getNumber();

		EPMDocumentMaster master=(EPMDocumentMaster) doc.getMaster();

		EPMDocumentMasterIdentity identity=(EPMDocumentMasterIdentity) master.getIdentificationObject();
		identity.setNumber(number);
		master =(EPMDocumentMaster) IdentityHelper.service.changeIdentity(master,identity);
	}
	
	/**
	 * 更新图档名称
	 * @param EPMNumber	图档名称
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void renameEPMDoc(EPMDocument doc,String name) throws WTException, WTPropertyVetoException{
		
		//String docNumber = doc.getNumber();

		EPMDocumentMaster master=(EPMDocumentMaster) doc.getMaster();

		EPMDocumentMasterIdentity identity=(EPMDocumentMasterIdentity) master.getIdentificationObject();
		identity.setName(name);
		master =(EPMDocumentMaster) IdentityHelper.service.changeIdentity(master,identity);
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
	 * HEX软件PN，将软件版本和硬件版本映射到父件上
	 * @param part
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws RemoteException
	 */
	public static void setParentSW_HW(WTPart part) throws WTException, WTPropertyVetoException, RemoteException{
		if(PartUtil.isSWPart(part)){
			QueryResult qr = StructHelper.service.navigateUsedBy(part.getMaster());
			System.out.println(qr.size());
		
			IBAUtility childiba = new IBAUtility(part);
			String swversion = childiba.getIBAValue("Software_Version");
			String hwversion = childiba.getIBAValue("Hardware_Version");
			while(qr.hasMoreElements()){
				WTPart partparent = (WTPart) qr.nextElement();
			
				IBAUtility parentiba = new IBAUtility(partparent);
				parentiba.setIBAValue("Software_Version", swversion);
				parentiba.setIBAValue("Hardware_Version", hwversion);
				parentiba.updateAttributeContainer(partparent);
				parentiba.updateIBAHolder(partparent);
			
				System.out.println(partparent.getNumber()+"\t"+partparent.getName());
				if(PartUtil.isPCBAPart(partparent)){
					QueryResult qrparent = StructHelper.service.navigateUsedBy(partparent.getMaster());
					while(qrparent.hasMoreElements()){
						WTPart partpp = (WTPart) qrparent.nextElement();
					
						IBAUtility ppiba = new IBAUtility(partpp);
						ppiba.setIBAValue("Software_Version", swversion);
						ppiba.setIBAValue("Hardware_Version", hwversion);
						ppiba.updateAttributeContainer(partpp);
						ppiba.updateIBAHolder(partpp);
					
						System.out.println(partpp.getNumber()+"\t"+partpp.getName());
						
					}
				}
			}
		}else if(PartUtil.isPCBAPart(part)){
			QueryResult qr = StructHelper.service.navigateUsedBy(part.getMaster());
			System.out.println(qr.size());
		
			IBAUtility childiba = new IBAUtility(part);
			String swversion = childiba.getIBAValue("Software_Version");
			String hwversion = childiba.getIBAValue("Hardware_Version");
			while(qr.hasMoreElements()){
				WTPart partparent = (WTPart) qr.nextElement();
			
				IBAUtility parentiba = new IBAUtility(partparent);
				parentiba.setIBAValue("Software_Version", swversion);
				parentiba.setIBAValue("Hardware_Version", hwversion);
				parentiba.updateAttributeContainer(partparent);
				parentiba.updateIBAHolder(partparent);
			
				System.out.println(partparent.getNumber()+"\t"+partparent.getName());
			}
		}
	}
	
	
	/**
	 * 将软件版本和硬件版本从父件上移除
	 * @param part
	 * @throws Exception 
	 */
	public static void deleteParentSW_HW(WTPart part) throws Exception{
		if(PartUtil.isSWPart(part)){
			QueryResult qr = StructHelper.service.navigateUsedBy(part.getMaster());
			System.out.println(qr.size());		
			
			while(qr.hasMoreElements()){
				WTPart partparent = (WTPart) qr.nextElement();
				ReleasedDateUtil.deleteReleasedDate(partparent, "Software_Version");
				ReleasedDateUtil.deleteReleasedDate(partparent, "Hardware_Version");
				
				System.out.println(partparent.getNumber()+"\t"+partparent.getName());
				if(PartUtil.isPCBAPart(partparent)){
					QueryResult qrparent = StructHelper.service.navigateUsedBy(partparent.getMaster());
					while(qrparent.hasMoreElements()){
						WTPart partpp = (WTPart) qrparent.nextElement();
					
						ReleasedDateUtil.deleteReleasedDate(partpp, "Software_Version");
						ReleasedDateUtil.deleteReleasedDate(partpp, "Hardware_Version");
						System.out.println(partpp.getNumber()+"\t"+partpp.getName());
						
					}
				}
			}
		}else if(PartUtil.isPCBAPart(part)){
			QueryResult qr = StructHelper.service.navigateUsedBy(part.getMaster());
			System.out.println(qr.size());

			while(qr.hasMoreElements()){
				WTPart partparent = (WTPart) qr.nextElement();
				ReleasedDateUtil.deleteReleasedDate(partparent, "Software_Version");
				ReleasedDateUtil.deleteReleasedDate(partparent, "Hardware_Version");
							
				System.out.println(partparent.getNumber()+"\t"+partparent.getName());
			}
		}
	}
}
