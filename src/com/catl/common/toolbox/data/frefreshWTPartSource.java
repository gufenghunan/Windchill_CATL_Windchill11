package com.catl.common.toolbox.data;

import java.io.BufferedWriter;
import java.io.File;
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
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.Source;
import wt.part.WTPart;
import wt.part.WTPartConfigSpec;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartStandardConfigSpec;
import wt.part.WTPartUsageLink;
import wt.pom.PersistenceException;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.VersionControlHelper;
import wt.vc.config.ConfigHelper;

import com.catl.common.constant.PartState;
import com.catl.common.util.CatlConstant;
import com.catl.common.util.ClassificationUtil;
import com.catl.common.util.GenericUtil;
import com.catl.common.util.IBAUtil;
import com.catl.common.util.PartUtil;
import com.catl.loadData.util.ExcelReader;
import com.catl.loadData.util.ExcelWriter;
import com.catl.part.PartConstant;
import com.catl.part.classification.AttributeForFAE;
import com.catl.part.classification.ClassificationNodeConfig;
import com.catl.part.classification.NodeConfigHelper;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;
import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.meta.common.UpdateOperationIdentifier;

public class frefreshWTPartSource implements RemoteAccess {

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
				Class[] types = {String.class,String.class};
		        Object[] values={args[2],args[3]};
				RemoteMethodServer.getDefault().invoke("doLoad", frefreshWTPartSource.class.getName(), null, types, values);
			} catch (WTException e) {
				e.printStackTrace();
			}
		}
	}

	public static void doLoad(String path,String command) {

		boolean enforced = SessionServerHelper.manager.setAccessEnforced(false);

		String logPath = homePath + "/logs/";
		List<String[]> list = new ArrayList<String[]>();
		Transaction ts = null;
		FileWriter file = null;
		BufferedWriter writer = null;
		try {
			Format format = new SimpleDateFormat("yyyyMMddHHmmss");
			String nowTime = format.format(new Date());
			file = new FileWriter(logPath + "Cheeck_Source_" + nowTime + ".log");
			writer = new BufferedWriter(file);
			writer.write("开始\n");
			
			ExcelReader xlsReader = new ExcelReader(new File(path));
			xlsReader.open();
			int count = xlsReader.getRowCount(0);
			List<WTPart> updatePart = new ArrayList<WTPart>();
			for(int i=1; i<count; i++){
				String[] strArr = new String[7];//0:partNumber 1:sourceERP 2:sourcePLM 3:unify 4: isReleased 5:haveChild 6：isHandle
				String[] line = xlsReader.readExcelLine(i);
				String partNumber = line[0];
				strArr[0] = partNumber;
				String sourceERP = line[1];
				strArr[1] = sourceERP;
				WTPart part = PartUtil.getLastestWTPartByNumber(partNumber);
				if(part == null){
					writer.write("不存在物料"+partNumber+"\n");
					continue;
				}
			
				String sourcePLM = part.getSource().toString();
				strArr[2] = sourcePLM;
				if(sourceERP.equals("自制")){
					if(sourcePLM.equals("make"))
						strArr[3]="是";
					else{
						strArr[3]="否";
						if(part.getState().toString().equals(PartState.RELEASED)){
							strArr[4] = "已发布";
							QueryResult result =PersistenceHelper.manager.navigate(part, "uses",WTPartUsageLink.class, false);
							if(0==result.size()){
								strArr[5] = "否";
								strArr[6] = "否";
							}else{
								strArr[5] = "是";
								strArr[6] = "是";
								part.setSource(Source.toSource("make"));
								updatePart.add(part);
							}
						}else{
							strArr[4] = "未发布";
						}
					}
				}else if(sourceERP.equals("外购") || sourceERP.equals("客供")){
					if(sourcePLM.equals("buy") || sourcePLM.equals("customer"))
						strArr[3]="是";
					else{
						strArr[3]="否";
						if(part.getState().toString().equals(PartState.RELEASED)){
							strArr[4] = "已发布";
							strArr[6] = "是";
							part.setSource(Source.toSource("buy"));
							updatePart.add(part);
						}else{
							strArr[4] = "未发布";
						}
					}
				}else if(sourceERP.equals("外协")){
					if(sourcePLM.equals("makeBuy"))
						strArr[3]="是";
					else{
						strArr[3]="否";
						if(part.getState().toString().equals(PartState.RELEASED)){
							strArr[4] = "已发布";
							QueryResult result =PersistenceHelper.manager.navigate(part, "uses",WTPartUsageLink.class, false);
							if(0==result.size()){
								strArr[5] = "否";
								strArr[6] = "否";
							}else{
								strArr[5] = "是";
								strArr[6] = "是";
								part.setSource(Source.toSource("makeBuy"));
								updatePart.add(part);
							}
						}else{
							strArr[4] = "未发布";
						}
					}
				}else if(sourceERP.equals("虚拟")){
					if(sourcePLM.equals("virtual"))
						strArr[3]="是";
					else{
						strArr[3]="否";
						if(part.getState().toString().equals(PartState.RELEASED)){
							strArr[4] = "已发布";
							QueryResult result =PersistenceHelper.manager.navigate(part, "uses",WTPartUsageLink.class, false);
							if(0==result.size()){
								strArr[5] = "否";
								strArr[6] = "否";
							}else{
								strArr[5] = "是";
								strArr[6] = "是";
								part.setSource(Source.toSource("virtual"));
								updatePart.add(part);
							}
						}else{
							strArr[4] = "未发布";
						}
					}
				}
				list.add(strArr);
			}
			if(command.equals("run")){
				ts = new Transaction();
				ts.start();
				for(WTPart part : updatePart){
					PersistenceServerHelper.manager.update(part);
				}
				ts.commit();
			}
			
			writer.write("结束\n");
			writer.flush();
			writer.close();
			
			boolean flag = ExcelWriter.exportExcelList(logPath + "Update_Source_" + nowTime + ".csv", "frefreshWTPartSource", new String[] { "部件编码", "采购类型ERP", "采购类型PLM", "是否一致","是否发布","是否有子件","是否处理"}, list);

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

}
