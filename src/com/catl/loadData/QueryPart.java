package com.catl.loadData;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.catl.loadData.util.ExcelWriter;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.pom.PersistenceException;
import wt.query.QuerySpec;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.VersionControlHelper;

public class QueryPart  implements RemoteAccess {

	public static void main(String[] args){
		
		RemoteMethodServer ms = RemoteMethodServer.getDefault();
		ms.setUserName("dms");
		ms.setPassword("dms");
		
		try {
			SessionHelper.manager.setAuthenticatedPrincipal("dms");			
			RemoteMethodServer.getDefault()
			.invoke("findAllPart",
					QueryPart.class.getName(), null, null,
					null);			
		} catch (WTException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void findAllPart() throws WTException{
		boolean enforced=SessionServerHelper.manager.setAccessEnforced(false);
		try{
			String rootPath=WTProperties.getLocalProperties().getProperty("wt.home")+"/loadFiles/com/catl/dms";
			List<String[]> logs=new ArrayList<String[]>();
			Vector<WTPartMaster> partMaster= getPartMaster();
			logs.add(new String[]{"总数量",String.valueOf(partMaster.size()),""});
			for(WTPartMaster master : partMaster){
				QueryResult qr= VersionControlHelper.service
						.allVersionsOf(master);
				WTPart part=null;
				while(qr.hasMoreElements()) {
					part = (WTPart) qr.nextElement();				
				}
				logs.add(new String[]{master.getNumber(),master.getName(),part.getLifeCycleState().getDisplay()});
			}
			
			ExcelWriter writer = new ExcelWriter();
			boolean flag = writer.exportExcelList(rootPath+"/PLM系统Part统计.xlsx","PLM系统Part统计", new String[]{"PN","名称","状态"}, logs);
			System.out.println("PLM系统Part统计.xlsx flag="+flag);
			logs=new ArrayList<String[]>();
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			SessionServerHelper.manager.setAccessEnforced(enforced);
		}
	}
	
	static Vector<WTPartMaster> getPartMaster() throws WTException {
		Vector<WTPartMaster> masterVector=new Vector<WTPartMaster>();
		
		QuerySpec qs= new QuerySpec(WTPartMaster.class);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		List<String> partNumber=new ArrayList<String>();
		while(qr.hasMoreElements()){			
			WTPartMaster master = (WTPartMaster) qr.nextElement();
			System.out.println("-----------");
			System.out.println("----------:"+master.getNumber());
			if(!partNumber.contains(master.getNumber())){
				masterVector.add(master);
				partNumber.add(master.getNumber());
			}
		}
		return masterVector;
	}
}
