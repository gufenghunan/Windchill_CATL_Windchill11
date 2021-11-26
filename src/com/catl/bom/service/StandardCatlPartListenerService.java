package com.catl.bom.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hslf.exceptions.HSLFException;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.events.KeyedEvent;
import wt.events.KeyedEventListener;
import wt.fc.ObjectReference;
import wt.fc.PersistenceManagerEvent;
import wt.fc.QueryResult;
import wt.fc.collections.WTCollection;
import wt.inf.container.WTContainer;
import wt.inf.library.WTLibrary;
import wt.part.SubstituteQuantity;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartSubstituteLink;
import wt.part.WTPartUsageLink;
import wt.services.ServiceEventListenerAdapter;
import wt.services.StandardManager;
import wt.util.WTException;
import wt.vc.VersionControlServiceEvent;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.WorkInProgressServiceEvent;

import com.catl.common.constant.AttributeName;
import com.catl.common.constant.DocState;
import com.catl.common.constant.PartState;
import com.catl.common.constant.Ranking;
import com.catl.common.util.GenericUtil;
import com.catl.common.util.PartUtil;
import com.catl.part.PartConstant;
import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.meta.common.UpdateOperationIdentifier;

public class StandardCatlPartListenerService extends StandardManager implements CatlPartListenerService {
	private static final long serialVersionUID = 5266098956681288190L;

	private KeyedEventListener listener = null;

	private static Logger log = Logger.getLogger(StandardCatlPartListenerService.class.getName());

	public String getConceptualClassname() {
		return StandardCatlPartListenerService.class.getName();
	}

	public static StandardCatlPartListenerService newStandardCatlPartListenerService() throws WTException {
		StandardCatlPartListenerService instance = new StandardCatlPartListenerService();
		instance.initialize();
		return instance;
	}

	protected void performStartupProcess() {
		log.debug(">>>++++++++++++++++++++++++resigiser service start:StandardCatlPartListenerService");
		listener = new CatlEventListener(getConceptualClassname());
		log.debug(">>>++++++++++++++++++++++++resiger service end:StandardCatlPartListenerService");
		// getManagerService().addEventListener(listener,
		// LifeCycleServiceEvent.generateEventKey("STATE_CHANGE"));
		getManagerService().addEventListener(listener, WorkInProgressServiceEvent.generateEventKey("PRE_CHECKIN"));
		getManagerService().addEventListener(listener, PersistenceManagerEvent.generateEventKey(PersistenceManagerEvent.INSERT));
		getManagerService().addEventListener(listener, VersionControlServiceEvent.generateEventKey(VersionControlServiceEvent.NEW_VERSION));
		// getManagerService().addEventListener(listener,
		// IdentityServiceEvent.generateEventKey("POST_CHANGE_IDENTITY"));
	}

	class CatlEventListener extends ServiceEventListenerAdapter {
		public CatlEventListener(String manager_name) {
			super(manager_name);
		}

		public void notifyVetoableEvent(Object event) throws Exception {
			if (!(event instanceof KeyedEvent))
				return;
			KeyedEvent eventObject = (KeyedEvent) event;
			Object busObject = eventObject.getEventTarget();
			// log.debug(">>>++++++++++++++++ eventObject="+eventObject.getEventType());
			if (eventObject.getEventType().equals("PRE_CHECKIN")) {
				if (busObject instanceof WTPart) {
					WTPart part = (WTPart) busObject;

					boolean checkoutFlag = WorkInProgressHelper.isCheckedOut(part);

					if (checkoutFlag) {// get the workcopy to check
						if (!WorkInProgressHelper.isWorkingCopy(part)) {
							part = (WTPart) WorkInProgressHelper.service.workingCopyOf(part);
						}
					}
					checkPartState(part);
				}
			} else if (eventObject.getEventType().equals(VersionControlServiceEvent.NEW_VERSION)) {// 版本升级事件
				if (busObject instanceof WTPart) {
					WTPart part = (WTPart) busObject;
					boolean checkoutFlag = WorkInProgressHelper.isCheckedOut(part);
					if (!checkoutFlag) {// false表示升级大版本 true表示升级小版本
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
				}
			}else if (eventObject.getEventType().equals(PersistenceManagerEvent.INSERT)) {
				if (busObject instanceof WTPartDescribeLink) {
					WTPartDescribeLink descLink = (WTPartDescribeLink)busObject;
					WTPart part = descLink.getDescribes();
					WTDocument doc = descLink.getDescribedBy();
					String partState = part.getState().toString();
					String docState = doc.getState().toString();
					
					StringBuffer message = new StringBuffer();
					if(partState.equalsIgnoreCase(PartState.DISABLEDFORDESIGN))
						message.append("部件"+part.getNumber()+"的状态为设计禁用，不允许与文档建立关联\n");
					if(docState.equalsIgnoreCase(DocState.DISABLEDFORDESIGN))
						message.append("文档"+doc.getNumber()+"的状态为设计禁用，不允许与文档部件建立关联\n");
					if(message.length() >0)
						throw new WTException(message.toString());
						
				}
			}

		}
	}

	/**
	 * Search BOM
	 * 
	 * @param part
	 * @param listnumber
	 * @param liststate
	 * @throws Exception
	 */
	public static void checkBom(WTPart part, List<String> listmessage) throws Exception {
		String state = "";
		String amountvalue = "";
		// bom cat not have the same part
		List<String> numberlist = new ArrayList<String>();
		QueryResult qr = WTPartHelper.service.getUsesWTPartMasters(part);
		log.debug("Check BOM parent part = " + part.getNumber());
		while (qr.hasMoreElements()) {
			WTPartUsageLink link = (WTPartUsageLink) qr.nextElement();
			checkSubpart(link, listmessage);
			double amount = link.getQuantity().getAmount();
			// log.debug("amount=="+amount);
			String amountString = String.valueOf(amount);
			// log.debug("amount int=="+amountString.indexOf("."));

			WTPartMaster master = link.getUses();
			if (numberlist.contains(master.getNumber())) {
				listmessage.add("部件:" + master.getNumber() + "已被添加到bom中不能重复添加！ \n");
				log.debug("Duplicate Child Part number = " + master.getNumber());
			} else {
				numberlist.add(master.getNumber());
				log.debug("add Child Part Number = " + master.getNumber());
			}
			WTPart sunpart = PartUtil.getLastestWTPartByNumber(master.getNumber());
			state = sunpart.getState().toString();
			log.debug("part number==" + sunpart.getNumber() + "state==" + state);
			String containerName = sunpart.getContainerName();
			WTContainer container = sunpart.getContainer();
			if (container instanceof WTLibrary) {
				log.debug("child part state ------->" + sunpart.getNumber() + ":" + state);
				if (!state.equalsIgnoreCase("RELEASED")) {
					String number = sunpart.getNumber();
					listmessage.add("部件:" + number + "所在位置为存储库:" + containerName + ",生命周期状态,不符合业务规范,检入的部件必须是已发布状态。\n");
				}
				log.debug("sun part  amount===" + sunpart.getNumber() + "===" + amountString);
				log.debug("amountvalue==" + amountvalue + amountvalue.length());
			} else {
				if (state.equalsIgnoreCase("WRITING") || state.equalsIgnoreCase("MODIFICATION") || state.equalsIgnoreCase("REVIEW")) {
					String number = sunpart.getNumber();
					listmessage.add("部件:" + number + "所在位置为产品库:" + containerName + ",生命周期状态,不符合业务规范,检入的部件不能是“编制”，“修改”或“审阅”的状态。\n");
				}
			}
			if (amountString.indexOf(".") > 0) {
				amountvalue = amountString.substring(amountString.indexOf(".") + 1, amountString.length() - 1);
				if (amountvalue.length() > 3) {
					listmessage.add("部件" + sunpart.getNumber() + "的数量为：" + amount + "不符合业务规则，小数点后不能超过三位。\n");
				}
			}
		}

	}

	public static void checkSubpart(WTPartUsageLink usageLink, List<String> listmessage) {
		ArrayList<String> subpartList = new ArrayList<String>();
		try {
			subpartList = getSubstitutePart(usageLink, listmessage);
		} catch (WTException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < subpartList.size(); i++) {
			String number = subpartList.get(i);
			WTPart subpart = PartUtil.getLastestWTPartByNumber(number);
			WTContainer container = subpart.getContainer();
			String state = subpart.getState().toString();
			if (!state.equalsIgnoreCase("RELEASED")) {
				listmessage.add("替代件:" + number + "所在位置为:" + container.getName() + ",生命周期状态不是已发布状态，不符合业务规范。\n");

			}
		}

	}

	private static ArrayList<String> getSubstitutePart(WTPartUsageLink link, List<String> listmessage) throws WTException {
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
								listmessage.add("替代部件" + partMaster.getNumber() + "的数量为：" + amount + "，单位为:" + unit + "，不符合业务规则，单位为\"个\"时,值必须为整数。\n");
							}
							if (temp.length() > 3) {
								listmessage.add("替代部件" + partMaster.getNumber() + "的数量为：" + amount + "不符合业务规则，小数点后不能超过三位。\n");
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
	 * @throws WTException
	 * @throws Exception
	 */
	public static void checkPartState(WTPart part) throws WTException {

		StringBuffer message = new StringBuffer();
		List<String> listmessage = new ArrayList<String>();

		try {
			checkBom(part, listmessage);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < listmessage.size(); i++) {
			String number = listmessage.get(i);
			message.append(number);
			message.append(";\n");
		}
		log.debug("BOM Check Error" + message);
		if (message.toString().length() > 0) {
			throw new WTException("错误信息：" + message);
		}
	}

}
