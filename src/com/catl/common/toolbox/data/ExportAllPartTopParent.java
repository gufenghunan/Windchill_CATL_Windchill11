package com.catl.common.toolbox.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.catl.loadData.util.ExcelWriter;

import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTCollection;
import wt.httpgw.GatewayAuthenticator;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartSubstituteLink;
import wt.part.WTPartUsageLink;
import wt.query.QuerySpec;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;

public class ExportAllPartTopParent implements RemoteAccess {
	public static void main(String[] args) {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		GatewayAuthenticator auth = new GatewayAuthenticator();
		auth.setRemoteUser("wcadmin");
		rms.setAuthenticator(auth);

		System.out.println(args[0]);
		invokeRemoteLoad(args[0]);
	}

	public static void invokeRemoteLoad(String exportPath) {
		String method = "doLoad";
		String CLASSNAME = ExportAllPartTopParent.class.getName();
		Class[] types = { String.class };
		Object[] values = { exportPath };
		try {
			RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void doLoad(String exportPath){
		try{
			Map<String,String> map = new LinkedHashMap<String,String>();//key:物料编号 value:顶层的part以逗号隔开
			System.out.println(exportPath+" 开始导出");
			List<WTPart> list = getLastestAllWTPartList();
			System.out.println("list 数量="+list.size());
			for(WTPart part : list){
				if(queryTopParent((WTPartMaster)part.getMaster(),0,map).equals("No Parent")){
					WTCollection collection = WTPartHelper.service.getSubstituteForLinks((WTPartMaster) part.getMaster());
					if (!collection.isEmpty()) {
						Iterator itr = collection.iterator();
						while (itr.hasNext()) {
							ObjectReference objReference = (ObjectReference) itr.next();
							WTPartSubstituteLink subLink = (WTPartSubstituteLink) objReference.getObject();
							WTPartUsageLink ULink = (WTPartUsageLink) subLink.getRoleAObject();
							Persistable pB = (Persistable) ULink.getRoleBObject();
							WTPartMaster bPart = (WTPartMaster) pB;
							if(map.get(bPart.getNumber()) == null){
								queryTopParent(bPart,0,map);
							}
							map.put(part.getNumber()+"~"+bPart.getNumber(), map.get(bPart.getNumber()));
						}
					}
				}
			}
			ExcelWriter writer = new ExcelWriter();
			List<String[]> exportData = new ArrayList<String[]>();
			Set<Entry<String, String>> entrys = map.entrySet();
			String[] row;
			for(Entry<String, String> entry : entrys){
				row = new String[2];
				row[0] = entry.getKey();
				row[1] = entry.getValue();
				exportData.add(row);
			}
	    	boolean flag = writer.exportExcelList(exportPath,"ExportBom", new String[]{"编号", "顶层编号"}, exportData);
			System.out.println(exportPath +" 导出结束 flag="+flag);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public static List<WTPart> getLastestAllWTPartList() throws WTException {
		List<WTPart> list = new ArrayList<WTPart>();
		QuerySpec queryspec = new QuerySpec(WTPart.class);

		QueryResult queryresult = PersistenceHelper.manager.find(queryspec);
		LatestConfigSpec cfg = new LatestConfigSpec();
		QueryResult qr = cfg.process(queryresult);
		while (qr.hasMoreElements()) {
			list.add((WTPart) qr.nextElement());
		}
		return list;
	}
	
	public static String queryTopParent(WTPartMaster partMaster,int level,Map<String,String> map) throws WTException{
		if(map.get(partMaster.getNumber()) != null)
			return "Hvae Parent";
		if(level > 15)
			return "To More";
		QueryResult qr = WTPartHelper.service.getUsedByWTParts(partMaster);
		qr = new LatestConfigSpec().process(qr);
		boolean hasParent = qr.hasMoreElements();
		List<String> sameParentPart = new ArrayList<String>();
		if(hasParent == true)
			++level;
		while(qr.hasMoreElements()){
			WTPart part = (WTPart)qr.nextElement();
			sameParentPart.add(part.getNumber());
			queryTopParent((WTPartMaster)part.getMaster(),level,map);
		}
		if(hasParent == true){
			StringBuffer topPart= new StringBuffer();
			for(String samePart : sameParentPart){
				topPart.append(map.get(samePart)).append(",");
			}
			topPart.deleteCharAt(topPart.length()-1);
			map.put(partMaster.getNumber(), topPart.toString());
		}else if(hasParent == false && level != 0){
			map.put(partMaster.getNumber(), partMaster.getNumber());
		}else{
			return "No Parent";
		}
		return "Hvae Parent";
	}

}
