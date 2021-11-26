package com.catl.require.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drools.core.util.StringUtils;

import com.catl.battery.util.ExcelUtil;
import com.catl.ecad.utils.CommonUtil;
import com.catl.integration.trp.GetAfterTimeParts;
import com.catl.line.util.IBAUtil;
import com.catl.line.util.IBAUtility;
import com.catl.require.constant.ConstantRequire;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.iba.definition.StringDefinition;
import wt.iba.value.StringValue;
import wt.inf.container.WTContainer;
import wt.inf.library.WTLibrary;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartConfigSpec;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartStandardConfigSpec;
import wt.pdmlink.PDMLinkProduct;
import wt.pds.StatementSpec;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.TableColumn;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class SetPartsPlatform implements RemoteAccess {
	public static void step1() throws Exception {
		washPlatformByPrefix("PCAR", "A");
		washPlatformByLib("电芯材料库", "C");
		washPlatformByLib("电芯机械件库", "C");
	}

	public static void step2() throws Exception {
		washPlatformByLib("电子电气件", "A");
		washPlatformByLib("紧固件", "A");
		washPlatformByLib("原材料", "A");
	}

	public static void step3() throws WTPropertyVetoException, RemoteException,
			WTException {
		washPlatformByPrefix("PBUS", "B");
		washPlatformByPrefix("PESV", "B");
	}

	public static void step4() throws WTPropertyVetoException, RemoteException,
			WTException {
		washPlatformByPrefix("PESS", "C");
	}

	public static void step5() throws WTPropertyVetoException, RemoteException,
			WTException {
		washPlatformByPrefix("FC", "");
	}
	public static void step6() throws WTPropertyVetoException, WTException, FileNotFoundException, IOException {
		clearPlatformByExcel("/tmp/platformA.xls");
		washPlatformByExcel("/tmp/platformA.xls", "C");
	}

	public static void test() throws Exception {
		//clearAllPartPlatform();
		// step1();
		// step2();
		// step3();
		// step4();
		// step5();
		   step6();
	}

	public static void main(String[] args) throws RemoteException,
			InvocationTargetException {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		rms.setUserName("wcadmin");
		rms.setPassword("wcadmin");
		rms.invoke("test", SetPartsPlatform.class.getName(), null, null, null);
	}

	public static QueryResult getPartsByPrefix(String number)
			throws WTException {
		List<WTPart> pns = new ArrayList<WTPart>();
		QuerySpec qs = new QuerySpec(WTPart.class);
		qs.setAdvancedQueryEnabled(true);
		SearchCondition sc1 = new SearchCondition(WTPart.class, WTPart.NUMBER,
				SearchCondition.LIKE, number + "%");
		qs.appendWhere(sc1);
		qs.appendAnd();
		SearchCondition sc2 = new SearchCondition(WTPart.class,
				WTAttributeNameIfc.LATEST_ITERATION, SearchCondition.IS_TRUE);
		qs.appendWhere(sc2);
		qs.appendOrderBy(new OrderBy(new ClassAttribute(WTPart.class,
				WTPart.NUMBER), true), new int[] { 0 });// 按编号倒序排列
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		return qr;
	}

	public static QueryResult getAllParts() throws WTException {
		List<WTPart> pns = new ArrayList<WTPart>();
		QuerySpec qs = new QuerySpec(WTPart.class);
		qs.setAdvancedQueryEnabled(true);
		SearchCondition sc1 = new SearchCondition(WTPart.class,
				WTAttributeNameIfc.LATEST_ITERATION, SearchCondition.IS_TRUE);
		qs.appendWhere(sc1);
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		return qr;
	}

	/**
	 * 获取所有的某产品下部件
	 * 
	 * @return
	 * @throws Exception
	 */
	public static List<WTPart> getProductPNs(String containername)
			throws Exception {
		List<WTPart> pns = new ArrayList<WTPart>();
		QuerySpec qs = new QuerySpec();
		qs.setAdvancedQueryEnabled(true);
		int partIndex = qs.appendClassList(WTPart.class, true);
		int proIndex = qs.addClassList(PDMLinkProduct.class, false);

		SearchCondition sc = new SearchCondition(WTPart.class,
				WTAttributeNameIfc.LATEST_ITERATION, SearchCondition.IS_TRUE);
		qs.appendWhere(sc, new int[] { partIndex });

		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WTPart.class, WTPart.CONTAINER_ID,
				WTContainer.class, WTAttributeNameIfc.ID_NAME), new int[] {
				partIndex, proIndex });

		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WTLibrary.class, WTContainer.NAME,
				"=", containername), new int[] { proIndex });
		QueryResult qr = PersistenceHelper.manager.find(qs);
		while (qr.hasMoreElements()) {
			Persistable[] persistable = (Persistable[]) qr.nextElement();
			WTPart part = (WTPart) persistable[0];
			pns.add(part);
		}
		return pns;
	}

	public static List<WTPart> getLibaryPNs(String containername)
			throws Exception {
		List<WTPart> pns = new ArrayList<WTPart>();
		QuerySpec qs = new QuerySpec();
		qs.setAdvancedQueryEnabled(true);
		int partIndex = qs.appendClassList(WTPart.class, true);
		int libIndex = qs.addClassList(WTLibrary.class, false);

		SearchCondition sc = new SearchCondition(WTPart.class,
				WTAttributeNameIfc.LATEST_ITERATION, SearchCondition.IS_TRUE);
		qs.appendWhere(sc, new int[] { partIndex });

		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WTPart.class, WTPart.CONTAINER_ID,
				WTContainer.class, WTAttributeNameIfc.ID_NAME), new int[] {
				partIndex, libIndex });

		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WTLibrary.class, WTContainer.NAME,
				"=", containername), new int[] { libIndex });
		QueryResult qr = PersistenceHelper.manager.find(qs);
		while (qr.hasMoreElements()) {
			Persistable[] persistable = (Persistable[]) qr.nextElement();
			WTPart part = (WTPart) persistable[0];
			pns.add(part);
		}
		return pns;
	}

	private static void washPlatformByPrefix(String numberprefix, String value)
			throws WTPropertyVetoException, RemoteException, WTException {
		QueryResult qr = getPartsByPrefix(numberprefix);
		while (qr.hasMoreElements()) {
			WTPart part = (WTPart) qr.nextElement();
			setPlatform(part, value);
		}

	}
	
	private static void washPlatformByExcel(String path,String value)
			throws WTPropertyVetoException, WTException, FileNotFoundException, IOException {
		String[][] result=ExcelUtil.getData(new File(path), 1);
		List list=new ArrayList();
			for (int i = 0; i < result.length; i++) {
				String number=result[i][0];
				WTPart part=CommonUtil.getLatestWTpartByNumber(number);
				if(part==null){
					throw new WTException("找不到编号为"+number+"的部件");
				}
				setPlatform(part, value);
			}
	}
	private static void clearPlatformByExcel(String path)
			throws WTPropertyVetoException, WTException, FileNotFoundException, IOException {
		String[][] result=ExcelUtil.getData(new File(path), 1);
		List list=new ArrayList();
			for (int i = 0; i < result.length; i++) {
				String number=result[i][0];
				WTPart part=CommonUtil.getLatestWTpartByNumber(number);
				if(part==null){
					throw new WTException("找不到编号为"+number+"的部件");
				}
				clearPartPlatform(part);
			}
	}

	private static void washPlatformByLib(String containername, String value)
			throws Exception {
		List<WTPart> parts = getLibaryPNs(containername);
		for (int i = 0; i < parts.size(); i++) {
			WTPart part = parts.get(i);
			setPlatform(part, value);
		}

	}

	private static void washPlatformByPro(String containername, String value)
			throws Exception {
		List<WTPart> parts = getProductPNs(containername);
		for (int i = 0; i < parts.size(); i++) {
			WTPart part = parts.get(i);
			setPlatform(part, value);
		}

	}

	private static void washPlatformWithChildsByPrefix(String numberprefix,
			String value) throws WTException, WTPropertyVetoException,
			RemoteException {
		QueryResult qr = getPartsByPrefix(numberprefix);
		while (qr.hasMoreElements()) {
			WTPart part = (WTPart) qr.nextElement();
			setPlatform(part, value);
			WTPartStandardConfigSpec stdSpec = WTPartStandardConfigSpec
					.newWTPartStandardConfigSpec();
			WTPartConfigSpec configSpec = WTPartConfigSpec
					.newWTPartConfigSpec(stdSpec);
			wt.fc.Persistable apersistable[] = null;
			QueryResult cqr = WTPartHelper.service.getUsesWTParts(part,
					configSpec);
			while (cqr.hasMoreElements()) {
				apersistable = (wt.fc.Persistable[]) cqr.nextElement();
				WTPart child = null;
				WTPartMaster cpartmaster = null;
				if (apersistable[1] instanceof WTPartMaster) {
					cpartmaster = (WTPartMaster) apersistable[1];
					child = (WTPart) CommonUtil.getLatestVersionOf(cpartmaster);
				} else {
					child = (WTPart) apersistable[1];
				}
				setPlatform(child, value);
			}
		}
	}

	public static void setPlatform(WTPart part, String value)
			throws WTPropertyVetoException, WTException, RemoteException {
		WTPartMaster partmaster = part.getMaster();
		String platform = IBAUtil.getStringIBAValue(partmaster,
				ConstantRequire.iba_CATL_Platform);
		if (StringUtils.isEmpty(platform)) {
			IBAUtility iba = new IBAUtility();
			if (!StringUtils.isEmpty(value)) {
				iba.setIBAValue(ConstantRequire.iba_CATL_Platform, value);
				iba.updateAttributeContainer(partmaster);
				iba.updateIBAHolder(partmaster);
			}
		} else {
			StringBuffer buffer = new StringBuffer();
			System.out.println(buffer.append(part.getNumber()).append("已经为标识")
					.append(platform).append("不能刷为").append(value).toString());
		}

	}

	public static void clearAllPartPlatform() throws Exception {
		QueryResult qr = getAllParts();
		System.out.println(qr.size());
		 while(qr.hasMoreElements()){
		 WTPart part=(WTPart) qr.nextElement();
		 clearPartPlatform(part);
		 }
	}
	
	public static void clearPlatformWithChildsByPrefix(String numberprefix) throws Exception {
		QueryResult qr = getPartsByPrefix(numberprefix);
		System.out.println(qr.size());
		 while(qr.hasMoreElements()){
		 WTPart part=(WTPart) qr.nextElement();
		 clearPartPlatform(part);
		 }
	}
	public static void clearPlatformWithChildsByLib(String containername) throws Exception {
		List<WTPart> parts = getLibaryPNs(containername);
		 for(int i=0;i<parts.size();i++){
		 WTPart part=parts.get(i);
		 clearPartPlatform(part);
		 }
	}
	public static void clearPlatformWithChildsByPro(String containername) throws Exception {
		List<WTPart> parts = getProductPNs(containername);
		 for(int i=0;i<parts.size();i++){
		 WTPart part=parts.get(i);
		 clearPartPlatform(part);
		 }
	}
    public static void clearPartPlatform(WTPart part) throws WTException, WTPropertyVetoException, RemoteException{
    	 WTPartMaster partmaster = part.getMaster();
		 String platform = IBAUtil.getStringIBAValue(partmaster,ConstantRequire.iba_CATL_Platform);
		  System.out.println("----"+platform);
		 if(!StringUtils.isEmpty(platform)){
			 System.out.println("删除标识="+part.getNumber());
			 IBAUtility iba = new IBAUtility(partmaster);
			 iba.deleteIBAValueByLogical(ConstantRequire.iba_CATL_Platform);
			 iba.updateAttributeContainer(partmaster);
			 iba.updateIBAHolder(partmaster);
		 }
    }
	public static QueryResult getAllWashParts(String iba_name) throws Exception {
		QuerySpec qs = new QuerySpec();
		qs.setAdvancedQueryEnabled(true);
		int valueIndex = qs.appendClassList(StringValue.class, false);
		int sdefIndex = qs.appendClassList(StringDefinition.class, false);
		int partIndex = qs.appendClassList(WTPart.class, false);

		ClassAttribute caValue = new ClassAttribute(StringValue.class, "value2");
		qs.appendSelect(caValue, new int[] { valueIndex }, false);

		SearchCondition sc = new SearchCondition(WTPart.class,
				"iterationInfo.latest", "TRUE");
		qs.appendWhere(sc, new int[] { partIndex });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(new TableColumn(qs.getFromClause()
				.getAliasAt(partIndex), "IDA2A2"), "=", new TableColumn(qs
				.getFromClause().getAliasAt(valueIndex), "IDA3A4")), new int[] {
				partIndex, valueIndex });

		qs.appendAnd();
		qs.appendWhere(new SearchCondition(StringValue.class,
				"definitionReference.key.id", StringDefinition.class,
				"thePersistInfo.theObjectIdentifier.id"), new int[] {
				valueIndex, sdefIndex });

		qs.appendAnd();
		qs.appendWhere(new SearchCondition(StringDefinition.class, "name", "=",
				iba_name), new int[] { sdefIndex });

		qs.appendAnd();
		qs.appendWhere(new SearchCondition(StringValue.class, "value",
				SearchCondition.EQUAL, "C"), new int[] { valueIndex });
		QueryResult qr = PersistenceHelper.manager.find(qs);
		return qr;
	}
}
