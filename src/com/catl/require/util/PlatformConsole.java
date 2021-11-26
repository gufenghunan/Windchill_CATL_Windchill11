package com.catl.require.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.drools.core.util.StringUtils;

import com.catl.battery.util.ExcelUtil;
import com.catl.ecad.utils.CommonUtil;
import com.catl.integration.trp.GetAfterTimeParts;
import com.catl.line.util.IBAUtil;
import com.catl.line.util.IBAUtility;
import com.catl.require.constant.ConstantRequire;
import com.itextpdf.text.log.SysoCounter;

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
import wt.session.SessionHelper;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class PlatformConsole implements RemoteAccess {
	public static void step(int step, Scanner sc, RemoteMethodServer rms) throws Exception {
		String param1;
		String sign;
		String sure;
		switch (step) {
		case 1:
			System.out.println("请输入存储库名字");
			param1=sc.next();
			if(toControlpanel(param1)){showControlPanel(sc, rms);return;};
			System.out.println("请输入标识");
			sign=sc.next();
			if(toControlpanel(sign)){showControlPanel(sc, rms);return;};
			while(!isTuresign(sign)){
				System.out.println("标识只能是A、B、C,请重新输入");
				sign=sc.next();
				if(toControlpanel(sign)){showControlPanel(sc, rms);return;};
			}
			System.out.println("请确认,刷存储库"+param1+"产品线标识为"+sign+"(Y,N)");
			sure=sc.next();
			if(toControlpanel(sure)){showControlPanel(sc, rms);return;};
			if(sure.toUpperCase().equals("Y")){
				System.out.println("正在执行...");
				String msg=(String) rms.invoke("washPlatformByLib", PlatformConsole.class.getName(), null, new Class[]{String.class,String.class},new Object[]{param1,sign});
				System.out.println(msg);
				System.out.println();
			}
			showControlPanel(sc, rms);
			break;
		case 2:
			System.out.println("请输入产品库名字");
			param1=sc.next();
			if(toControlpanel(param1)){showControlPanel(sc, rms);return;};
			System.out.println("请输入标识");
			sign=sc.next();
			if(toControlpanel(sign)){showControlPanel(sc, rms);return;};
			while(!isTuresign(sign)){
				System.out.println("标识只能是A、B、C,请重新输入");
				sign=sc.next();
				if(toControlpanel(sign)){showControlPanel(sc, rms);return;};
			}
			System.out.println("请确认,刷产品库"+param1+"产品线标识为"+sign+"(Y,N)");
			sure=sc.next();
			if(toControlpanel(sure)){showControlPanel(sc, rms);return;};
			if(sure.toUpperCase().equals("Y")){
				System.out.println("正在执行...");
				String msg=(String) rms.invoke("washPlatformByPro", PlatformConsole.class.getName(), null, new Class[]{String.class,String.class},new Object[]{param1,sign});
				System.out.println(msg);
				System.out.println();
			}
			showControlPanel(sc, rms);
			break;
		case 3:
			System.out.println("请输入编码前缀");
			param1=sc.next();
			if(toControlpanel(param1)){showControlPanel(sc, rms);return;};
			System.out.println("请输入标识");
			sign=sc.next();
			if(toControlpanel(sign)){showControlPanel(sc, rms);return;};
			while(!isTuresign(sign)){
				System.out.println("标识只能是A、B、C,请重新输入");
				sign=sc.next();
				if(toControlpanel(sign)){showControlPanel(sc, rms);return;};
			}
			System.out.println("请确认,刷编码前缀为"+param1+"的部件,产品线标识为"+sign+"(Y,N)");
			sure=sc.next();
			if(sure.toUpperCase().equals("Y")){
				System.out.println("正在执行...");
				String msg=(String) rms.invoke("washPlatformByPrefix", PlatformConsole.class.getName(), null, new Class[]{String.class,String.class},new Object[]{param1,sign});
				System.out.println(msg);
				System.out.println();
			}
			showControlPanel(sc, rms);
			break;
		case 4:
			System.out.println("请输入编码前缀");
			param1=sc.next();
			if(toControlpanel(param1)){showControlPanel(sc, rms);return;};
			System.out.println("请输入标识");
			sign=sc.next();
			if(toControlpanel(sign)){showControlPanel(sc, rms);return;};
			while(!isTuresign(sign)){
				System.out.println("标识只能是A、B、C,请重新输入");
				sign=sc.next();
				if(toControlpanel(sign)){showControlPanel(sc, rms);return;};
			}
			System.out.println("请确认,刷BOM头前缀"+param1+"的部件,产品线标识为"+sign+"(Y,N)");
			sure=sc.next();
			if(toControlpanel(sure)){showControlPanel(sc, rms);return;};
			if(sure.toUpperCase().equals("Y")){
				System.out.println("正在执行...");
				String msg=(String) rms.invoke("washPlatformWithChildsByPrefix", PlatformConsole.class.getName(), null, new Class[]{String.class,String.class},new Object[]{param1,sign});
				System.out.println(msg);
				System.out.println();
			}
			showControlPanel(sc, rms);
			break;
		case 5:
			System.out.println("请输入Excel路径");
			param1=sc.next();
			if(toControlpanel(param1)){showControlPanel(sc, rms);return;};
			System.out.println("请输入标识");
			sign=sc.next();
			if(toControlpanel(sign)){showControlPanel(sc, rms);return;};
			while(!isTuresign(sign)){
				System.out.println("标识只能是A、B、C,请重新输入");
				sign=sc.next();
				if(toControlpanel(sign)){showControlPanel(sc, rms);return;};
			}
			System.out.println("请确认,刷路径"+param1+"文件中的部件,产品线标识为"+sign+"(Y,N)");
			sure=sc.next();
			if(toControlpanel(sure)){showControlPanel(sc, rms);return;};
			if(sure.toUpperCase().equals("Y")){
				System.out.println("正在执行...");
				String msg=(String) rms.invoke("washPlatformByExcel", PlatformConsole.class.getName(), null, new Class[]{String.class,String.class},new Object[]{param1,sign});
				System.out.println(msg);
				System.out.println();
			}
			showControlPanel(sc, rms);
			break;
		case 6:
			System.out.println("请输入存储库名字");
			param1=sc.next();
			if(toControlpanel(param1)){showControlPanel(sc, rms);return;};
			System.out.println("请确认,清除存储库"+param1+"产品线标识(Y,N)");
			sure=sc.next();
			if(toControlpanel(sure)){showControlPanel(sc, rms);return;};
			if(sure.toUpperCase().equals("Y")){
				System.out.println("正在执行...");
				String msg=(String) rms.invoke("clearPlatformByLib", PlatformConsole.class.getName(), null, new Class[]{String.class},new Object[]{param1});
				System.out.println(msg);
				System.out.println();
			}
			showControlPanel(sc, rms);
			break;
		case 7:
			System.out.println("请输入产品库名字");
			param1=sc.next();
			if(toControlpanel(param1)){showControlPanel(sc, rms);return;};
			System.out.println("请确认,清除产品库"+param1+"产品线标识(Y,N)");
			sure=sc.next();
			if(toControlpanel(sure)){showControlPanel(sc, rms);return;};
			if(sure.toUpperCase().equals("Y")){
				System.out.println("正在执行...");
				String msg=(String) rms.invoke("clearPlatformByPro", PlatformConsole.class.getName(), null, new Class[]{String.class},new Object[]{param1});
				System.out.println(msg);
				System.out.println();
			}
			showControlPanel(sc, rms);
			break;
		case 8:
			System.out.println("请输入编码前缀");
			param1=sc.next();
			if(toControlpanel(param1)){showControlPanel(sc, rms);return;};
			System.out.println("请确认,清除编码前缀为"+param1+"的部件产品线标识(Y,N)");
			sure=sc.next();
			if(toControlpanel(sure)){showControlPanel(sc, rms);return;};
			if(sure.toUpperCase().equals("Y")){
				System.out.println("正在执行...");
				String msg=(String) rms.invoke("clearPlatformByPrefix", PlatformConsole.class.getName(), null, new Class[]{String.class},new Object[]{param1});
				System.out.println(msg);
				System.out.println();
			}
			showControlPanel(sc, rms);
			break;
    	case 9:
    		System.out.println("请输入Excel路径");
			param1=sc.next();
			if(toControlpanel(param1)){showControlPanel(sc, rms);return;};
			System.out.println("请确认,清除路径"+param1+"文件中的部件产品线标识(Y,N)");
			sure=sc.next();
			if(toControlpanel(sure)){showControlPanel(sc, rms);return;};
			if(sure.toUpperCase().equals("Y")){
				System.out.println("正在执行...");
				String msg=(String) rms.invoke("clearPlatformByExcel", PlatformConsole.class.getName(), null, new Class[]{String.class},new Object[]{param1});
				System.out.println(msg);
				System.out.println();
			}
			showControlPanel(sc, rms);
		    break;
    	case 10:
			System.out.println("请确认,清除所有部件产品线标识(Y,N)");
			sure=sc.next();
			if(toControlpanel(sure)){showControlPanel(sc, rms);return;};
			if(sure.toUpperCase().equals("Y")){
				System.out.println("正在执行...");
				String msg=(String) rms.invoke("clearAllPartPlatform", PlatformConsole.class.getName(), null, new Class[]{},new Object[]{});
				System.out.println(msg);
				System.out.println();
			}
			showControlPanel(sc, rms);
		    break;
    	case 0:
			 break;
		}
	}
    private static boolean toControlpanel(String param) {
	   if(param.equals("#")){
		   return true;
	   }
		return false;
	}
	public static boolean isTuresign(String sign){
    	String str="A,B,C,";
		if(str.contains(sign+",")){
			return true;
		}
		return false;
    }
	public static void main(String[] args) throws Exception {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		//SessionHelper.manager.setAuthenticatedPrincipal("wcadmin");
		Scanner sc = new Scanner(System.in);
		System.out.println("请输入用户名");
		String username=sc.next();
		rms.setUserName(username);
		System.out.println("请输入密码");
		String password=sc.next();
		rms.setPassword(password);
		showControlPanel(sc,rms);
	}
    public static void showControlPanel(Scanner sc, RemoteMethodServer rms) throws Exception{
    	System.out.println("请按以下操作输入编号,任意时刻输入#回到此界面");
		System.out.println("------------------------------");
    	System.out.println("1.按存储库名字设置产品线标识");
		System.out.println("2.按产品库名字设置产品线标识");
		System.out.println("3.按编码前缀设置产品线标识");
		System.out.println("4.按BOM结构设置产品线标识");
		System.out.println("5.按Excel信息设置产品线标识");
		System.out.println("6.按存储库名字清除产品线标识");
		System.out.println("7.按产品库名字清除产品线标识");
		System.out.println("8.按编码前缀清除产品线标识");
		System.out.println("9.按Excel信息清除产品线标识");
		System.out.println("10.清除所有部件产品线标识");
		System.out.println("0.退出");
		System.out.println("------------------------------");
		step(sc.nextInt(),sc,rms);
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

	public static String washPlatformByPrefix(String numberprefix, String value)
			throws WTPropertyVetoException, RemoteException, WTException {
		QueryResult qr = getPartsByPrefix(numberprefix);
		int success=0;
		while (qr.hasMoreElements()) {
			WTPart part = (WTPart) qr.nextElement();
			success=success+setPlatform(part, value);
		}
		return "操作完毕、受影响部件为"+qr.size()+"个,其中成功更改"+success+"个";

	}
	
	public static String washPlatformByExcel(String path,String value)
			throws WTPropertyVetoException, WTException, FileNotFoundException, IOException {
		String[][] result=ExcelUtil.getData(new File(path), 1);
		List list=new ArrayList();
		int success=0;
			for (int i = 0; i < result.length; i++) {
				String number=result[i][0];
				WTPart part=CommonUtil.getLatestWTpartByNumber(number);
				if(part==null){
					throw new WTException("找不到编号为"+number+"的部件");
				}
				success=success+setPlatform(part, value);
			}
			return "操作完毕、受影响部件为"+result.length+"个,其中成功更改"+success+"个";
	}
	public static String clearPlatformByExcel(String path)
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
			return "操作完毕、受影响部件为"+result.length+"个";
	}

	public static String washPlatformByLib(String containername, String value)
			throws Exception {
		List<WTPart> parts = getLibaryPNs(containername);
		int success=0;
		for (int i = 0; i < parts.size(); i++) {
			WTPart part = parts.get(i);
			success=success+setPlatform(part, value);
		}
		return "操作完毕、受影响部件为"+parts.size()+"个,其中成功更改"+success+"个";

	}

	public static String washPlatformByPro(String containername, String value)
			throws Exception {
		List<WTPart> parts = getProductPNs(containername);
		int success=0;
		for (int i = 0; i < parts.size(); i++) {
			WTPart part = parts.get(i);
			success=success+setPlatform(part, value);
		}
		return "操作完毕、受影响部件为"+parts.size()+"个,其中成功更改"+success+"个";

	}

	public static String washPlatformWithChildsByPrefix(String numberprefix,
			String value) throws WTException, WTPropertyVetoException,
			RemoteException {
		QueryResult qr = getPartsByPrefix(numberprefix);
		int success=0;
		while (qr.hasMoreElements()) {
			WTPart part = (WTPart) qr.nextElement();
			success=success+setPlatform(part, value);
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
				success=success+setPlatform(part, value);
			}
		}
		return "操作完毕、受影响BOM头为"+qr.size()+"个,成功更改部件"+success+"个";
	}

	public static int setPlatform(WTPart part, String value)
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
				return 1;
			}
		} else {
			StringBuffer buffer = new StringBuffer();
			System.out.println(buffer.append(part.getNumber()).append("已经为标识")
					.append(platform).append("不能刷为").append(value).toString());
		}
		return 0;

	}

	public static String clearAllPartPlatform() throws Exception {
		QueryResult qr = getAllParts();
		System.out.println(qr.size());
		 while(qr.hasMoreElements()){
		 WTPart part=(WTPart) qr.nextElement();
		 clearPartPlatform(part);
		 }
		 return "操作完毕、受影响部件为"+qr.size()+"个";
	}
	
	public static String clearPlatformByPrefix(String numberprefix) throws Exception {
		QueryResult qr = getPartsByPrefix(numberprefix);
		System.out.println(qr.size());
		 while(qr.hasMoreElements()){
		 WTPart part=(WTPart) qr.nextElement();
		 clearPartPlatform(part);
		 }
		 return "操作完毕、受影响部件为"+qr.size()+"个";
	}
	public static String clearPlatformByLib(String containername) throws Exception {
		List<WTPart> parts = getLibaryPNs(containername);
		 for(int i=0;i<parts.size();i++){
		 WTPart part=parts.get(i);
		 clearPartPlatform(part);
		 }
		 return "操作完毕、受影响部件为"+parts.size()+"个";
	}
	public static String clearPlatformByPro(String containername) throws Exception {
		List<WTPart> parts = getProductPNs(containername);
		 for(int i=0;i<parts.size();i++){
		 WTPart part=parts.get(i);
		 clearPartPlatform(part);
		 }
		 return "操作完毕、受影响部件为"+parts.size()+"个";
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
