package com.catl.common.toolbox.data;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.pom.PersistenceException;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.VersionControlHelper;

import com.catl.common.util.CatlConstant;
import com.catl.common.util.ClassificationUtil;
import com.catl.common.util.GenericUtil;
import com.catl.common.util.IBAUtil;
import com.catl.loadData.util.ExcelWriter;
import com.catl.part.PartConstant;
import com.catl.part.classification.AttributeForFAE;
import com.catl.part.classification.ClassificationNodeConfig;
import com.catl.part.classification.NodeConfigHelper;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;
import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.meta.common.UpdateOperationIdentifier;

public class refreshWTPartFAEStatus implements RemoteAccess {

	private static String homePath = "";

	private static boolean checkflg; // 刷新FAE状态钱，判断可以正常刷新FAE状态

	private static Map<String, String[]> PNFAEMap; // 数组中存放0：FAE状态；1：是否需要祖宗更新；2：物料分类；3：成熟度属性；4：更新前FAE状态
	private static Map<String, WTPart> PNWTPartMap;

	static {
		try {
			WTProperties wtproperties = WTProperties.getLocalProperties();
			homePath = wtproperties.getProperty("wt.home");
		} catch (Throwable t) {
			throw new ExceptionInInitializerError(t);
		}
	}

	public static void main(String[] args) throws RemoteException, InvocationTargetException {

		if (args == null || args.length < 2) {

			System.out.printf("请输入正确的用户名、密码！");
		} else {
			RemoteMethodServer ms = RemoteMethodServer.getDefault();
			ms.setUserName(args[0]);
			ms.setPassword(args[1]);
			try {
				SessionHelper.manager.setAuthenticatedPrincipal(args[0]);
				Class[] types = {String.class};
		        Object[] values={args[2]};
				RemoteMethodServer.getDefault().invoke("doLoad", refreshWTPartFAEStatus.class.getName(), null, types, values);
			} catch (WTException e) {
				e.printStackTrace();
			}
		}
	}

	public static void doLoad(String command) {

		boolean enforced = SessionServerHelper.manager.setAccessEnforced(false);

		FileWriter file = null;
		String logPath = homePath + "/logs/";
		checkflg = true;
		PNFAEMap = new HashMap<String, String[]>();
		PNWTPartMap = new HashMap<String, WTPart>();
		List<String[]> logs = new ArrayList<String[]>();
		BufferedWriter writer = null;
		try {

			Format format = new SimpleDateFormat("yyyyMMddHHmmss");
			String nowTime = format.format(new Date());

			file = new FileWriter(logPath + "Cheeck_FAEStatus_" + nowTime + ".log");
			writer = new BufferedWriter(file);

			writer.write("开始获取所有部件！");
			writer.write("\n");
			// 获取所有部件
			Vector<WTPart> wtpartlist = getPart();

			writer.write("结束获取所有部件！部件总数：" + wtpartlist.size());
			writer.write("\n");

			writer.write("开始验证部件中是否有错误信息！");
			writer.write("\n");
			// 验证部件中是否有错误信息
			checkWTPartFAEStatus(wtpartlist, PNFAEMap, writer);

			writer.write("结束验证部件中是否有错误信息！checkflg=" + checkflg);
			writer.write("\n");

			if (checkflg) { // 如果没有错误信息则刷新所有部件FAE状态

				writer.write("开始刷新部件FAE状态！");
				writer.write("\n");
				logs = refreshWTPartFAEStatusOpe(PNFAEMap, writer, command);

				writer.write("结束刷新部件FAE状态！");
				writer.write("\n");

				writer.write("开始导出输出结果！");
				writer.write("\n");
				ExcelWriter writerExcel = new ExcelWriter();
				boolean flag = writerExcel.exportExcelList(logPath + "Update_FAEStatus_" + nowTime + ".csv", "Update_FAEStatus", new String[] { "部件编码", "物料分类", "是否需要FAE", "成熟度属性","是否刷新FAE状态", "更新前FAE状态", "新的FAE状态" }, logs);

				writer.write("开始导出输出结果！flag=" + flag);
				writer.write("\n");
			}
			logs = new ArrayList<String[]>();

			writer.flush();
			writer.close();
		} catch (Exception e) {
			try {
				writer.write("报错：" + e.getMessage());
				writer.write("\n");
				writer.flush();
				writer.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforced);
		}

	}

	public static boolean isEmpty(String str) {
		if (str == null || str.trim().equals("") || str.equals("null"))
			return true;
		return false;
	}

	static Vector<WTPart> getPart() throws WTException {

		Vector<WTPart> wtparts = new Vector<WTPart>();
		QuerySpec qs = new QuerySpec(WTPartMaster.class);
		// qs.appendWhere(new SearchCondition(WTPart.class,
		// WTPart.LATEST_ITERATION, SearchCondition.IS_TRUE), new int[] {0});
		QueryResult qr = PersistenceHelper.manager.find(qs);
		List<String> partNumber = new ArrayList<String>();
		while (qr.hasMoreElements()) {
			WTPartMaster wtpartmaster = (WTPartMaster) qr.nextElement();
			if (!partNumber.contains(wtpartmaster.getNumber())) {
				WTPart wtpart = getLatestPart(wtpartmaster);
				wtparts.add(wtpart);
				partNumber.add(wtpart.getNumber());
			}
		}
		return wtparts;
	}

	static WTPart getLatestPart(WTPartMaster partMaster) throws PersistenceException, WTException {
		WTPart part = null;
		if (partMaster != null) {
			QueryResult qr = VersionControlHelper.service.allVersionsOf(partMaster);
			if (qr != null && qr.hasMoreElements()) {
				part = (WTPart) qr.nextElement();
			}
		}
		return part;
	}

	static void checkWTPartFAEStatus(Vector<WTPart> wtpartlist, Map<String, String[]> PNFAEMap, BufferedWriter writer) throws PersistenceException, WTException {
		String ptnumber = null;
		try {
			for (WTPart dPart : wtpartlist) {
				String[] infoArr = new String[6]; // 数组中存放0：FAE状态；1：是否需要祖宗更新(0不需要更新，1需要更新)；2：物料分类；3：成熟度属性；4：更新前FAE状态；5：是否需要FAE
				WTPartMaster partMaster = (WTPartMaster) dPart.getMaster();
				ptnumber = partMaster.getNumber();
				String oldFAEStatus = (String) GenericUtil.getObjectAttributeValue(partMaster, PartConstant.IBA_CATL_FAEStatus);
				String maturity = (String) GenericUtil.getObjectAttributeValue(partMaster, PartConstant.IBA_CATL_Maturity);

				Object value = IBAUtil.getIBAValue(dPart, PartConstant.IBA_CLS);
				if (value == null) {

					checkflg = false;
					writer.write("部件：" + ptnumber + "没有取到分类属性！");
					writer.write("\n");
					continue;
				}

				PNWTPartMap.put(ptnumber, dPart);
				if (StringUtils.isEmpty(maturity)) {
					maturity = "1";
				}
				System.out.println(ptnumber);
				if(ptnumber.startsWith("FC")){
					System.out.println("debug");
				}
				oldFAEStatus = oldFAEStatus == null ? "" : oldFAEStatus;

				String newFAEStatus = needRefresh(dPart, value.toString());
				infoArr[0] = newFAEStatus;
				if (newFAEStatus == null || oldFAEStatus.equals(newFAEStatus)) {
					infoArr[1] = "0";
				} else {
					infoArr[1] = "1";
				}
				infoArr[2] = value.toString();
				infoArr[3] = maturity;
				infoArr[4] = oldFAEStatus;
				
				LWCStructEnumAttTemplate node = ClassificationUtil.getLWCStructEnumAttTemplateByName(value.toString());
        		if (node == null){
        			checkflg = false;
					writer.write("分类名称："+value.toString()+"没有取到对应节点！");
        			writer.write("\n");
        			continue;
        		}else{
        			infoArr[5] = NodeConfigHelper.getNodeConfig(value.toString()).getNeedFae() + "";
        		}
				PNFAEMap.put(ptnumber, infoArr);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(ptnumber);
			e.printStackTrace();
		}
	}

	static List<String[]> refreshWTPartFAEStatusOpe(Map<String, String[]> PNFAEMap, BufferedWriter writer, String command) throws PersistenceException, WTException {

		List<String[]> outputlist = new ArrayList<String[]>();
		Transaction ts = null;
		try {

			ts = new Transaction();
			ts.start();
			for (String partNum : PNFAEMap.keySet()) {

				String[] outputArr = new String[7];

				String[] infoArr = PNFAEMap.get(partNum); // 数组中存放0：FAE状态；1：是否需要祖宗更新(0不需要更新，1需要更新)；2：物料分类；3：成熟度属性；4：更新前FAE状态；5：是否需要FAE
				String FEAStatus = infoArr[0]; // FAE状态
				String type = infoArr[1]; 

				if ("1".equals(type) && command.equals("run")) {

					WTPart part = PNWTPartMap.get(partNum);
					WTPartMaster partMaster = (WTPartMaster) part.getMaster();
					PersistableAdapter genericObj = new PersistableAdapter(partMaster, null, null, new UpdateOperationIdentifier());
					genericObj.load(PartConstant.IBA_CATL_FAEStatus);
					genericObj.set(PartConstant.IBA_CATL_FAEStatus, FEAStatus);
					Persistable updatedObject = genericObj.apply();
					partMaster = (WTPartMaster) PersistenceHelper.manager.save(updatedObject);
				}

				outputArr[0] = partNum;// 部件编码
				outputArr[1] = infoArr[2];// 物料分类
				outputArr[2] = infoArr[5];// 是否需要FAE
				outputArr[3] = infoArr[3];// 成熟度属性
				outputArr[4] = type.equals("1")?"是":"否";
				outputArr[5] = infoArr[4];// 更新前FAE状态
				outputArr[6] = infoArr[0];// 新的FAE状态

				outputlist.add(outputArr);
			}
			ts.commit();
		} catch (Exception e) {

			try {
				writer.write("更新部件FAE状态报错，错误信息：" + e.getMessage());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if (ts != null)
				ts.rollback();
		}

		return outputlist;
	}

	private static String needRefresh(WTPart part, String nodeName) throws WTException {
		String currentMaturity = (String) IBAUtil.getIBAValue(part.getMaster(), PartConstant.IBA_CATL_Maturity);
		String currentFaeStatus = (String) IBAUtil.getIBAValue(part.getMaster(), PartConstant.IBA_CATL_FAEStatus);
		String faeStatus = getNewFAEStatusValue(NodeConfigHelper.getNodeConfig(nodeName), part);

		if ((currentFaeStatus == null || currentFaeStatus.equals(PartConstant.CATL_FAEStatus_1)) && faeStatus.equals(PartConstant.CATL_FAEStatus_2)) {
			if (currentMaturity.equals("1")) {
				return faeStatus;
			} else if (currentMaturity.equals("3") || currentMaturity.equals("6")) {
				return PartConstant.CATL_FAEStatus_4;
			}
		} else if ((currentFaeStatus == null || currentFaeStatus.equals(PartConstant.CATL_FAEStatus_2) || currentFaeStatus.equals(PartConstant.CATL_FAEStatus_4)) && faeStatus.equals(PartConstant.CATL_FAEStatus_1)) {
			return faeStatus;
		}
		/*
		 * if((StringUtils.equals(currentMaturity, "1") ||
		 * StringUtils.equals(currentMaturity, "3")) &&
		 * (StringUtils.equals(currentFaeStatus, PartConstant.CATL_FAEStatus_1)
		 * || StringUtils.equals(currentFaeStatus,
		 * PartConstant.CATL_FAEStatus_2))){
		 * 
		 * if(!StringUtils.equals(faeStatus, currentFaeStatus)){ return
		 * faeStatus; } }
		 */
		return null;
	}

	private static String getNewFAEStatusValue(ClassificationNodeConfig nodeConfig, WTPart part) {
		if (nodeConfig != null && part != null) {
			return getNewFAEStatusValue(nodeConfig, part, part.getSource().toString());
		}
		return null;
	}

	private static String getNewFAEStatusValue(ClassificationNodeConfig nodeConfig, WTPart part, String source) {
		if (nodeConfig != null && part != null) {
			if (nodeConfig.getNeedFae()) {
				if (nodeConfig.getAttributeRef().equals(AttributeForFAE.NONE)) {
					return PartConstant.CATL_FAEStatus_2;
				} else if (nodeConfig.getAttributeRef().equals(AttributeForFAE.CUSTOMIZED)) {
					String customize = (String) IBAUtil.getIBAValue(part, PartConstant.IBA_CATL_Customize);
					if (StringUtils.equals(customize, PartConstant.CATL_Customize_1)) {
						return PartConstant.CATL_FAEStatus_2;
					} else if (StringUtils.equals(customize, PartConstant.CATL_Customize_2)) {
						return PartConstant.CATL_FAEStatus_1;
					}
				} else if (nodeConfig.getAttributeRef().equals(AttributeForFAE.SOURCE)) {
					if (StringUtils.equals(source, CatlConstant.MANUFACTURE_SOURCE_NAME)) {
						return nodeConfig.getMakeNeedFae() == true ? PartConstant.CATL_FAEStatus_2 : PartConstant.CATL_FAEStatus_1;
					} else if (StringUtils.equals(source, CatlConstant.BUY_SOURCE_NAME)) {
						return nodeConfig.getBuyNeedFae() == true ? PartConstant.CATL_FAEStatus_2 : PartConstant.CATL_FAEStatus_1;
					} else if (StringUtils.equals(source, CatlConstant.ASSISIT_SOURCE_NAME)) {
						return nodeConfig.getMakeBuyNeedFae() == true ? PartConstant.CATL_FAEStatus_2 : PartConstant.CATL_FAEStatus_1;
					} else if (StringUtils.equals(source, CatlConstant.VIRTUAL_SOURCE_NAME)) {
						return nodeConfig.getVirtualNeedFae() == true ? PartConstant.CATL_FAEStatus_2 : PartConstant.CATL_FAEStatus_1;
					} else if (StringUtils.equals(source, CatlConstant.CUSTOMER_SOURCE_NAME)) {
						return nodeConfig.getCustomerNeedFae() == true ? PartConstant.CATL_FAEStatus_2 : PartConstant.CATL_FAEStatus_1;
					}
				}
			} else {
				return PartConstant.CATL_FAEStatus_1;
			}
		}
		return null;
	}
}
