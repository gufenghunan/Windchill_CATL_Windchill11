package com.catl.ecad.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.catl.cadence.util.NodeUtil;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;
import com.ptc.prolog.pub.RunTimeException;

import wt.epm.EPMDocument;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTHashSet;
import wt.fc.collections.WTKeyedHashMap;
import wt.fc.collections.WTKeyedMap;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.occurrence.OccurrenceHelper;
import wt.part.PartUsesOccurrence;
import wt.part.Quantity;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.pom.Transaction;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.team.Team;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;
import wt.vc.struct.StructHelper;
import wt.vc.wip.WorkInProgressHelper;

public class BOMUtil implements RemoteAccess {
	/**
	 * 创建BOM使用关系
	 * 
	 * @param parent
	 * @param childParts
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 * @throws RunTimeException
	 * @throws RemoteException
	 */
	public static void createPartUseageLink(WTPart parent, List childParts)
			throws WTPropertyVetoException, WTException, RunTimeException,
			RemoteException {
		if (parent != null) {
			try {
				// parent = (WTPart) CommonUtil.checkoutObject(parent);
				CommonUtil.checkinObject(parent, "");
				// 删除原来父部件的所有子件使用关系
				removeChildren(parent);
				for (int i = 0; i < childParts.size(); i++) {
					Object[] linkinfo = (Object[]) childParts.get(i);
					WTPart part = (WTPart) linkinfo[0];
					if(parent.getNumber().equals(part.getNumber())){
						throw new RunTimeException(parent.getNumber() + "部件不能把自己作为子件。");
					}
					String amout = (String) linkinfo[1];
					String refDesignator = (String) linkinfo[2];
					createPartUseageLink(parent, part, amout, refDesignator);
				}

			} catch (WTException e) {
				throw new RunTimeException(e.getLocalizedMessage(Locale.CHINA));
			}
		}

	}

	/**
	 * 创建部件使用关系
	 * 
	 * @param parentPart
	 * @param childPart
	 * @param amout
	 * @param refDesignator
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 * @throws RemoteException
	 */
	public static void createPartUseageLink(WTPart parentPart,
			WTPart childPart, String amout, String refDesignator)
			throws WTPropertyVetoException, WTException, RemoteException {
		Double count = 1.0;
		if (!amout.trim().equals("")) {
			count = Double.valueOf(amout);
		}

		if (parentPart != null && childPart != null) {
			WTPartUsageLink link;
			try {
				CommonUtil.checkinObject(parentPart, "");
				// 删除原来父部件的所有子件使用关系
				// deletePartUseageLink(wt);
				QueryResult qr1 = PersistenceHelper.manager.find(
						WTPartUsageLink.class, parentPart,
						WTPartUsageLink.USED_BY_ROLE, childPart.getMaster());
				System.out.println("----" + qr1.size());
				if (qr1.size() == 0) {
					link = WTPartUsageLink.newWTPartUsageLink(parentPart,
							(WTPartMaster) childPart.getMaster());
					Quantity quantity = Quantity.newQuantity();
					quantity.setAmount(count);

					link.setQuantity(quantity);
					PersistenceServerHelper.manager.insert(link);

					if (!refDesignator.equals("")) {
						WTKeyedMap occurrences = new WTKeyedHashMap();
						String[] ous = refDesignator.split(";");
						for (int j = 0; j < ous.length; j++) {
							PartUsesOccurrence puo = PartUsesOccurrence
									.newPartUsesOccurrence(link);
							puo.setName(ous[j]);
							occurrences.put(puo, null);
						}
						OccurrenceHelper.service.setSkipValidation(true);
						OccurrenceHelper.service
								.saveUsesOccurrenceAndData(occurrences);
						OccurrenceHelper.service.setSkipValidation(false);

					}
				}

			} catch (WTException e) {
				e.printStackTrace();
				throw new WTException(e.getLocalizedMessage(Locale.CHINA));
			}
		}

	}

	/**
	 * 删除所有子件
	 * 
	 * @param parent
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 * @throws RunTimeException
	 */
	public static void deletePartUseageLink(WTPart parent)
			throws WTPropertyVetoException, WTException, RunTimeException {
		if (parent != null) {
			WTPartUsageLink link;
			try {
				CommonUtil.checkinObject(parent, "");

				QueryResult qr1 = WTPartHelper.service
						.getUsesWTPartMasters(parent);
				System.out.println("----" + qr1.size());
				while (qr1.hasMoreElements()) {
					link = (WTPartUsageLink) qr1.nextElement();
					PersistenceHelper.manager.delete(link);
				}

			} catch (WTException e) {
				e.printStackTrace();
				throw new RunTimeException("删除部件" + parent.getNumber() + "关联失败");
			}
		}
	}

	/**
	 * 移除所有子件，不升父件版本
	 * 
	 * @param parent
	 * @throws WTException
	 */
	public static void removeChildren(WTPart parent) throws WTException {

		QueryResult partUsageLinks = StructHelper.service.navigateUses(parent,
				WTPartUsageLink.class, false);
		if (partUsageLinks != null && partUsageLinks.size() > 0) {
			WTHashSet removeUsageLinkSet = new WTHashSet(partUsageLinks);
			PersistenceServerHelper.manager.remove(removeUsageLinkSet);
		}
		PersistenceHelper.manager.refresh(parent);

	}

	public static void test() throws Exception {
		String schPn = "0000000061";
		String path = "E:\\Design.csv";
		createBOM(schPn);
	}

	public static void createBOM(String schPn) throws Exception {
		// String schPn = "";
		// String path = "E:\\Design.csv";
		schPn = schPn.toUpperCase();

		EPMDocument epm = CommonUtil.getEPMDocumentByNumber(schPn);
		String path = CommonUtil.getWTHome();
		String number = epm.getNumber();
		// String filepath = "";
		StringBuffer buffer = new StringBuffer(path);
		buffer.append(File.separator);
		buffer.append("ecadTemp");
		File dir = new File(buffer.toString());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		buffer.append(File.separator);
		buffer.append(number).append(".csv");
		path = buffer.toString();

		FTPUitl.downloadFile(FTPConfigProperties.getConfigFtpPath(), number
				+ ".csv", path);

		if (epm != null) {
			if (!WorkInProgressHelper.isCheckedOut(epm)) {
				throw new WTException("原理图[" + schPn + "]未检出，请在PLM检出后再上传原理图。");
			} else {
				if (epm.getLifeCycleState().toString()
						.equalsIgnoreCase(ECADConst.RELEASED_STATE)) {
					throw new WTException("原理图[" + schPn
							+ "]是已发布状态，需要走变更流程后再修改检入系统！");
				} else if (!(epm.getLifeCycleState().toString()
						.equalsIgnoreCase(ECADConst.DESIGN_STATE) || epm
						.getLifeCycleState().toString()
						.equalsIgnoreCase(ECADConst.DESIGNMODIFICATION_STATE))) {
					throw new WTException("原理图[" + schPn + "]为["
							+ epm.getLifeCycleState().getDisplay(Locale.CHINA)
							+ "]状态，不允许上传原理图。");
				}
			}
		} else {
			throw new WTException("原理图[" + schPn + "]在PLM系统中不存在，请申请后再检入！");
		}

		WTPart parent = CommonUtil.getPartByNumber(schPn.trim());

		if (!(parent.getLifeCycleState().toString()
				.equalsIgnoreCase(ECADConst.DESIGN_STATE) || parent
				.getLifeCycleState().toString()
				.equalsIgnoreCase(ECADConst.DESIGNMODIFICATION_STATE))) {
			throw new WTException("PCBA[" + schPn + "]为["
					+ parent.getLifeCycleState().getDisplay(Locale.CHINA)
					+ "]状态，不是设计或者设计修改状态，不允许修改！");
		}

		if (parent == null) {
			throw new WTException("系统中不存在PCBA部件" + schPn);
		}
		ArrayList<ArrayList> arraylist = CSVParse.parseCSVFile(path);
		if (arraylist.size() > 1) {
			ArrayList arraylistline = (ArrayList) arraylist.get(0);
			if (arraylistline != null && arraylistline.size() == 3) {
				if (!arraylistline.get(0).equals(ECADConst.PARTNUMBER)
						|| !arraylistline.get(1).equals(ECADConst.QUANTITY)
						|| !arraylistline.get(2).equals(
								ECADConst.REFERENCE_DESIGNATOR)) {
					throw new WTException("CSV文件格式不正确");
				}
			} else {
				throw new WTException("CSV文件格式不正确");
			}
		} else {
			throw new WTException("CSV文件格式不正确");
		}
		ArrayList childparts = new ArrayList();
		for (int i = 1; i < arraylist.size(); i++) {
			System.out.println("================================\n"
					+ arraylist.get(i) + "\n================================");
			ArrayList arraylistline = (ArrayList) arraylist.get(i);

			Object[] linkinfo = new Object[3];
			String childNumber = (String) arraylistline.get(0);
			if(schPn.equalsIgnoreCase(childNumber)){
				throw new WTException(schPn+"部件不能把自己作为子件。");
			}
			WTPart childpart = CommonUtil.getPartByNumber(childNumber);
			if (childpart != null) {
				if (!childpart.getLifeCycleState().toString()
						.equalsIgnoreCase(ECADConst.DISABLE_FOR_DESIGN_STATE)) {
					linkinfo[0] = childpart;
					linkinfo[1] = arraylistline.get(1);
					linkinfo[2] = arraylistline.get(2);

					childparts.add(linkinfo);
				} else {
					throw new WTException("元器件[" + childNumber
							+ "]为设计禁用状态，请修改原理图后再上传原理图！");
				}

				if (ECADutil.isPCB(childpart)) {
					EPMDocument pcbepm = CommonUtil.getEPMDocumentByNumber(childNumber);
					if (pcbepm != null) {
						EPMUtil.createRefLink(epm, pcbepm);
					}else {
						throw new WTException("PCB图" + childNumber
								+ "在PLM系统中不存在或您没有访问该PCB图的权限。");
					}
				}
			} else {
				throw new WTException("系统中不存在部件" + arraylistline.get(0));
			}
		}
		Transaction tx = new Transaction();
		try{
			tx.start();
			parent = (WTPart) CommonUtil.checkoutObject(parent);
			removeChildren(parent);
			parent = (WTPart) CommonUtil.checkinObject(parent, "Update BOM");
			createPartUseageLink(parent, childparts);
			tx.commit();
			tx = null;
		}catch(WTException e){
			tx.rollback();
			throw new WTException(e.getLocalizedMessage(Locale.CHINA));
		}finally{
			if(tx != null){
				tx.rollback();
				tx = null;
			}
		}
		File csvfile = new File(path);
		if (csvfile.exists()) {
			csvfile.delete();
		}
	}

	public static void main(String[] args) throws WTRuntimeException,
			WTException, RemoteException, InvocationTargetException {
		RemoteMethodServer rm = RemoteMethodServer.getDefault();
		rm.setUserName("wcadmin");
		rm.setPassword("wcadmin");
		rm.invoke("test", BOMUtil.class.getName(), null, null, null);
	}

}
