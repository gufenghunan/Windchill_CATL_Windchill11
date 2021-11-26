package com.catl.common.toolbox.data;

import java.util.ArrayList;
import java.util.List;

import com.catl.common.util.PartUtil;
import com.catl.loadData.util.ExcelWriter;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.httpgw.GatewayAuthenticator;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;

public class ExportBom implements RemoteAccess {
	public static void main(String[] args) {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		GatewayAuthenticator auth = new GatewayAuthenticator();
		auth.setRemoteUser("wcadmin");
		rms.setAuthenticator(auth);

		System.out.println(args[0]);
		invokeRemoteLoad(args[0]);
	}

	public static void invokeRemoteLoad(String pnNumber) {
		String method = "doLoad";
		String CLASSNAME = ExportBom.class.getName();
		Class[] types = { String.class };
		Object[] values = { pnNumber };
		try {
			RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void doLoad(String pnNumber){
		try{
			System.out.println("ExportBom.xlsx 开始导出");
			List<WTPart> list = getLastestWTPartListByNumber(pnNumber);
			List<String[]> exportBom = new ArrayList<String[]>();
			for(WTPart part : list){
				queryChildPart(part,1,exportBom);
			}
			System.out.println("ExportBom.xlsx 数量="+exportBom.size());
			ExcelWriter writer = new ExcelWriter();
	    	boolean flag = writer.exportExcelList("/data/ExportBom.xlsx","ExportBom", new String[]{"级别", "编号", "名称"}, exportBom);
			System.out.println("ExportBom.xlsx flag="+flag);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public static List<WTPart> getLastestWTPartListByNumber(String partnumber) throws WTException {
		List<WTPart> list = new ArrayList<WTPart>();
		QuerySpec queryspec = new QuerySpec(WTPart.class);

		queryspec.appendSearchCondition(new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.LIKE, partnumber+"%"));
		QueryResult queryresult = PersistenceHelper.manager.find(queryspec);
		LatestConfigSpec cfg = new LatestConfigSpec();
		QueryResult qr = cfg.process(queryresult);
		while (qr.hasMoreElements()) {
			list.add((WTPart) qr.nextElement());
		}
		return list;
	}
	
	public static void queryChildPart(WTPart part,int level,List<String[]> list) throws WTException{
		addStrArr(part, level, list);
		QueryResult qr = WTPartHelper.service.getUsesWTPartMasters(part);
		level++;
		while(qr.hasMoreElements()){
			   WTPartUsageLink link = (WTPartUsageLink)qr.nextElement();
			   WTPartMaster master = link.getUses();
			   WTPart childPart = PartUtil.getLastestWTPartByNumber(master.getNumber());
			   queryChildPart(childPart,level,list);
		}
	}

	private static void addStrArr(WTPart part, int level, List<String[]> list) {
		String[] strArr = new String[3];
		strArr[0] = level+"";
		String space = "";
		while(level > 1){
			space = space+"  ";
			level--;
		}
		strArr[1] = space+part.getNumber();
		strArr[2] = part.getName();
		list.add(strArr);
	}
}
