package com.catl.require.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.tools.ant.types.CommandlineJava.SysProperties;
import org.drools.core.util.StringUtils;

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
import wt.part.WTPartMaster;
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

import com.catl.battery.util.ExcelUtil;
import com.catl.ecad.utils.CommonUtil;
import com.catl.line.util.IBAUtil;
import com.catl.line.util.IBAUtility;
import com.catl.require.constant.ConstantRequire;
import com.itextpdf.text.log.SysoCounter;

public class FAEConsole implements RemoteAccess {
	private static List washpartsnumber=new ArrayList();
	public static void step(int step, Scanner sc, RemoteMethodServer rms) throws Exception {
		washpartsnumber=new ArrayList();
		String param1;
		String sign;
		String sure;
		switch (step) {
		case 1:
			System.out.println("请输入存储库名字");
			param1=sc.next();
			if(toControlpanel(param1)){showControlPanel(sc, rms);return;};
			System.out.println("请输入FAE状态");
			sign=sc.next();
			if(toControlpanel(sign)){showControlPanel(sc, rms);return;};
			while(!isTuresign(sign)){
				System.out.println("FAE状态只能是不需要、未发起、评估中、已完成,请重新输入");
				sign=sc.next();
				if(toControlpanel(sign)){showControlPanel(sc, rms);return;};
			}
			System.out.println("请确认,刷存储库"+param1+"FAE状态为"+sign+"(Y,N)");
			sure=sc.next();
			if(toControlpanel(sure)){showControlPanel(sc, rms);return;};
			if(sure.toUpperCase().equals("Y")){
				System.out.println("正在执行...");
				String msg=(String) rms.invoke("washPlatformByLib", FAEConsole.class.getName(), null, new Class[]{String.class,String.class},new Object[]{param1,sign});
				System.out.println(msg);
				System.out.println();
			}
			showControlPanel(sc, rms);
			break;
		case 2:
			System.out.println("请输入产品库名字");
			param1=sc.next();
			if(toControlpanel(param1)){showControlPanel(sc, rms);return;};
			System.out.println("请输入FAE状态");
			sign=sc.next();
			if(toControlpanel(sign)){showControlPanel(sc, rms);return;};
			while(!isTuresign(sign)){
				System.out.println("FAE状态只能是不需要、未发起、评估中、已完成,请重新输入");
				sign=sc.next();
				if(toControlpanel(sign)){showControlPanel(sc, rms);return;};
			}
			System.out.println("请确认,刷产品库"+param1+"FAE状态为"+sign+"(Y,N)");
			sure=sc.next();
			if(toControlpanel(sure)){showControlPanel(sc, rms);return;};
			if(sure.toUpperCase().equals("Y")){
				System.out.println("正在执行...");
				String msg=(String) rms.invoke("washPlatformByPro", FAEConsole.class.getName(), null, new Class[]{String.class,String.class},new Object[]{param1,sign});
				System.out.println(msg);
				System.out.println();
			}
			showControlPanel(sc, rms);
			break;
		case 3:
			System.out.println("请输入编码前缀");
			param1=sc.next();
			if(toControlpanel(param1)){showControlPanel(sc, rms);return;};
			System.out.println("请输入FAE状态");
			sign=sc.next();
			if(toControlpanel(sign)){showControlPanel(sc, rms);return;};
			while(!isTuresign(sign)){
				System.out.println("FAE状态只能是不需要、未发起、评估中、已完成,请重新输入");
				sign=sc.next();
				if(toControlpanel(sign)){showControlPanel(sc, rms);return;};
			}
			System.out.println("请确认,刷编码前缀为"+param1+"的部件,FAE状态为"+sign+"(Y,N)");
			sure=sc.next();
			if(sure.toUpperCase().equals("Y")){
				System.out.println("正在执行...");
				String msg=(String) rms.invoke("washPlatformByPrefix", FAEConsole.class.getName(), null, new Class[]{String.class,String.class},new Object[]{param1,sign});
				System.out.println(msg);
				System.out.println();
			}
			showControlPanel(sc, rms);
			break;
		case 4:
			System.out.println("请输入编码前缀");
			param1=sc.next();
			if(toControlpanel(param1)){showControlPanel(sc, rms);return;};
			System.out.println("请输入FAE状态");
			sign=sc.next();
			if(toControlpanel(sign)){showControlPanel(sc, rms);return;};
			while(!isTuresign(sign)){
				System.out.println("FAE状态只能是不需要、未发起、评估中、已完成,请重新输入");
				sign=sc.next();
				if(toControlpanel(sign)){showControlPanel(sc, rms);return;};
			}
			System.out.println("请确认,刷BOM头前缀"+param1+"的部件,FAE状态为"+sign+"(Y,N)");
			sure=sc.next();
			if(toControlpanel(sure)){showControlPanel(sc, rms);return;};
			if(sure.toUpperCase().equals("Y")){
				System.out.println("正在执行...");
				String msg=(String) rms.invoke("washPlatformWithChildsByPrefix", FAEConsole.class.getName(), null, new Class[]{String.class,String.class},new Object[]{param1,sign});
				System.out.println(msg);
				System.out.println();
			}
			showControlPanel(sc, rms);
			break;
		case 5:
			System.out.println("------------------------------");
			System.out.println("请确认，输入一下数字");
			System.out.println("1.Excel信息为单个物料");
			System.out.println("2.Excel信息为BOM头");
			System.out.println("------------------------------");
			String cmd=sc.next();
			if(toControlpanel(cmd)){showControlPanel(sc, rms);return;};
			if(!cmd.equals("1")&&!cmd.equals("2")){
				break;
			}
			System.out.println("请输入Excel路径");
			param1=sc.next();
			if(toControlpanel(param1)){showControlPanel(sc, rms);return;};
			System.out.println("请输入FAE状态");
			sign=sc.next();
			if(toControlpanel(sign)){showControlPanel(sc, rms);return;};
			while(!isTuresign(sign)){
				System.out.println("FAE状态只能是不需要、未发起、评估中、已完成,请重新输入");
				sign=sc.next();
				if(toControlpanel(sign)){showControlPanel(sc, rms);return;};
			}
			if(cmd.equals("1")){
				System.out.println("请确认,刷路径"+param1+"文件中的部件,FAE状态为"+sign+"(Y,N)");
			}else{
				System.out.println("请确认,刷路径"+param1+"文件中的BOM,FAE状态为"+sign+"(Y,N)");
			}
			
			sure=sc.next();
			if(toControlpanel(sure)){showControlPanel(sc, rms);return;};
			if(sure.toUpperCase().equals("Y")){
				System.out.println("正在执行...");
				if(cmd.equals("1")){
				String msg=(String) rms.invoke("washPlatformByExcel", FAEConsole.class.getName(), null, new Class[]{String.class,String.class},new Object[]{param1,sign});
				System.out.println(msg);
				System.out.println();
				}else{
				String msg=(String) rms.invoke("washBomPlatformByExcel", FAEConsole.class.getName(), null, new Class[]{String.class,String.class},new Object[]{param1,sign});
				System.out.println(msg);
				System.out.println();
				}
			}
			showControlPanel(sc, rms);
			break;
		case 6:
			System.out.println("请输入存储库名字");
			param1=sc.next();
			if(toControlpanel(param1)){showControlPanel(sc, rms);return;};
			System.out.println("请确认,清除存储库"+param1+"FAE状态(Y,N)");
			sure=sc.next();
			if(toControlpanel(sure)){showControlPanel(sc, rms);return;};
			if(sure.toUpperCase().equals("Y")){
				System.out.println("正在执行...");
				String msg=(String) rms.invoke("clearPlatformByLib", FAEConsole.class.getName(), null, new Class[]{String.class},new Object[]{param1});
				System.out.println(msg);
				System.out.println();
			}
			showControlPanel(sc, rms);
			break;
		case 7:
			System.out.println("请输入产品库名字");
			param1=sc.next();
			if(toControlpanel(param1)){showControlPanel(sc, rms);return;};
			System.out.println("请确认,清除产品库"+param1+"FAE状态(Y,N)");
			sure=sc.next();
			if(toControlpanel(sure)){showControlPanel(sc, rms);return;};
			if(sure.toUpperCase().equals("Y")){
				System.out.println("正在执行...");
				String msg=(String) rms.invoke("clearPlatformByPro", FAEConsole.class.getName(), null, new Class[]{String.class},new Object[]{param1});
				System.out.println(msg);
				System.out.println();
			}
			showControlPanel(sc, rms);
			break;
		case 8:
			System.out.println("请输入编码前缀");
			param1=sc.next();
			if(toControlpanel(param1)){showControlPanel(sc, rms);return;};
			System.out.println("请确认,清除编码前缀为"+param1+"的部件FAE状态(Y,N)");
			sure=sc.next();
			if(toControlpanel(sure)){showControlPanel(sc, rms);return;};
			if(sure.toUpperCase().equals("Y")){
				System.out.println("正在执行...");
				String msg=(String) rms.invoke("clearPlatformByPrefix", FAEConsole.class.getName(), null, new Class[]{String.class},new Object[]{param1});
				System.out.println(msg);
				System.out.println();
			}
			showControlPanel(sc, rms);
			break;
    	case 9:
    		System.out.println("请输入Excel路径");
			param1=sc.next();
			if(toControlpanel(param1)){showControlPanel(sc, rms);return;};
			System.out.println("请确认,清除路径"+param1+"文件中的部件FAE状态(Y,N)");
			sure=sc.next();
			if(toControlpanel(sure)){showControlPanel(sc, rms);return;};
			if(sure.toUpperCase().equals("Y")){
				System.out.println("正在执行...");
				String msg=(String) rms.invoke("clearPlatformByExcel", FAEConsole.class.getName(), null, new Class[]{String.class},new Object[]{param1});
				System.out.println(msg);
				System.out.println();
			}
			showControlPanel(sc, rms);
		    break;
    	case 10:
			System.out.println("请确认,清除所有部件FAE状态(Y,N)");
			sure=sc.next();
			if(toControlpanel(sure)){showControlPanel(sc, rms);return;};
			if(sure.toUpperCase().equals("Y")){
				System.out.println("正在执行...");
				String msg=(String) rms.invoke("clearAllPartPlatform", FAEConsole.class.getName(), null, new Class[]{},new Object[]{});
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
    	String str="不需要,未发起,评估中,已完成,";
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
    	System.out.println("1.按存储库名字设置FAE状态");
		System.out.println("2.按产品库名字设置FAE状态");
		System.out.println("3.按编码前缀设置FAE状态");
		System.out.println("4.按BOM结构设置FAE状态");
		System.out.println("5.按Excel信息设置FAE状态");
		System.out.println("6.按存储库名字清除FAE状态");
		System.out.println("7.按产品库名字清除FAE状态");
		System.out.println("8.按编码前缀清除FAE状态");
		System.out.println("9.按Excel信息清除FAE状态");
		System.out.println("10.清除所有部件FAE状态");
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
		qs.appendWhere(new SearchCondition(PDMLinkProduct.class, WTContainer.NAME,
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
	public static String washBomPlatformByExcel(String path,String value)
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
				washPlatformWithChildsByPart(part, value);
			}
			return "操作完毕、受影响BOM为"+result.length+"个";
	}
	public static String clearPlatformByExcel(String path)
			throws WTPropertyVetoException, WTException, FileNotFoundException, IOException {
		washpartsnumber.clear();
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
			List childs=new ArrayList();
			StringBuffer buffer=new StringBuffer();
			System.out.print(buffer.append("获取")+part.getNumber()+"子件");
			CommonUtil.getAllChildParts(childs, part, part.getViewName());
			System.out.println(childs.size());
			for (int i = 0; i < childs.size(); i++) {
				WTPart child=(WTPart) childs.get(i);
				success=success+setPlatform(child, value);
			}
		}
		return "操作完毕、受影响BOM头为"+qr.size()+"个,成功更改部件"+success+"个";
	}
	public static void washPlatformWithChildsByPart(WTPart part,String value) throws WTException, WTPropertyVetoException,
			RemoteException {
		setPlatform(part, value);
		List childs=new ArrayList();
		StringBuffer buffer=new StringBuffer();
		System.out.print(buffer.append("获取")+part.getNumber()+"子件");
		CommonUtil.getAllChildParts(childs, part, part.getViewName());
		System.out.println(childs.size());
		for (int i = 0; i < childs.size(); i++) {
			WTPart child=(WTPart) childs.get(i);
			setPlatform(child, value);
		}
	
	}

	public static int setPlatform(WTPart part, String value)
			throws WTPropertyVetoException, WTException, RemoteException {
		WTPartMaster partmaster = part.getMaster();
		if(washpartsnumber.contains(partmaster.getNumber())){
			return 0;
		}
		washpartsnumber.add(partmaster.getNumber());
		String platform = IBAUtil.getStringIBAValue(partmaster,"CATL_FAEStatus");
		if (StringUtils.isEmpty(platform)) {
			IBAUtility iba = new IBAUtility(partmaster);
			if (!StringUtils.isEmpty(value)) {
				iba.setIBAValue("CATL_FAEStatus", value);
				iba.updateAttributeContainer(partmaster);
				iba.updateIBAHolder(partmaster);
				return 1;
			}
		} 
		return 0;

	}

	public static String clearAllPartPlatform() throws Exception {
		washpartsnumber.clear();
		QueryResult qr = getAllParts();
		System.out.println(qr.size());
		 while(qr.hasMoreElements()){
		 WTPart part=(WTPart) qr.nextElement();
		 clearPartPlatform(part);
		 }
		 return "操作完毕、受影响部件为"+qr.size()+"个";
	}
	
	public static String clearPlatformByPrefix(String numberprefix) throws Exception {
		washpartsnumber.clear();
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
		washpartsnumber.clear();
		 for(int i=0;i<parts.size();i++){
		 WTPart part=parts.get(i);
		 clearPartPlatform(part);
		 }
		 return "操作完毕、受影响部件为"+parts.size()+"个";
	}
	public static String clearPlatformByPro(String containername) throws Exception {
		washpartsnumber.clear();
		List<WTPart> parts = getProductPNs(containername);
		 for(int i=0;i<parts.size();i++){
		 WTPart part=parts.get(i);
		 clearPartPlatform(part);
		 }
		 return "操作完毕、受影响部件为"+parts.size()+"个";
	}
    public static void clearPartPlatform(WTPart part) throws WTException, WTPropertyVetoException, RemoteException{
    	washpartsnumber.clear();
    	WTPartMaster partmaster = part.getMaster();
		 String platform = IBAUtil.getStringIBAValue(partmaster,"CATL_FAEStatus");
		  System.out.println("----"+platform);
		 if(!StringUtils.isEmpty(platform)){
			 IBAUtility iba = new IBAUtility(partmaster);
			 iba.deleteIBAValueByLogical("CATL_FAEStatus");
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
