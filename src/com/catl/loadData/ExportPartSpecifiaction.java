package com.catl.loadData;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.catl.loadData.util.ExcelReader;
import com.catl.loadData.util.ExcelWriter;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.pom.PersistenceException;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.VersionControlHelper;

public class ExportPartSpecifiaction implements RemoteAccess {

	public static void main(String[] args) throws IOException {
		RemoteMethodServer ms = RemoteMethodServer.getDefault();
		ms.setUserName("dms");
		ms.setPassword("dms");
		
		try {
			SessionHelper.manager.setAuthenticatedPrincipal("dms");
			RemoteMethodServer.getDefault()
			.invoke("exportSpecifiaction",
					ExportPartSpecifiaction.class.getName(), null, null,
					null);
		} catch (WTException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}	
		
		/*String rootPath="E:\\ATL\\project\\PLM\\loadtest\\destotal";
		Map<String,String> partTMap=getObjectName(rootPath+"\\需导出规格的物料.xlsx",0);
		for(String key:partTMap.keySet()){
			System.out.println(key+"."+partTMap.get(key));
		}*/
	}
	
	public static void exportSpecifiaction(){
		List<String[]> logs=new ArrayList<String[]>();
		Transaction trx = null;
		try{
			trx = new Transaction();
			trx.start();
			String rootPath=WTProperties.getLocalProperties().getProperty("wt.home")+"/loadFiles/com/catl/dms";
			Map<String,String> partTMap=getObjectName(rootPath+"/需导出规格的物料.xlsx",0);
			
			System.out.println(partTMap.size());
			for(String key:partTMap.keySet()){
				WTPart part=getPart(key);
				if(part!=null){
					IBAUtility	iba_part = new IBAUtility(part);	
					String specification=iba_part.getIBAValue("specification");
					
					logs.add(new String[]{key,part.getName(),specification});
				}else{
					logs.add(new String[]{key,partTMap.get(key),"pn不存在！"});
				}
			}
			System.out.println("-----------0000000000-----------");
			
			ExcelWriter writer = new ExcelWriter();
			boolean flag = writer.exportExcelList(rootPath+"/导出的物料规格.xlsx","导出的物料规格", new String[]{"编码","名称","规格"}, logs);
			System.out.println("导出的物料规格.xlsx flag="+flag);
			
			trx.commit();
			trx = null;
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if (trx != null){
				System.out.println("---------exportSpecifiaction rollback--------");
				trx.rollback();
			}
		}
		
		
	}	
	
	public static Map<String,String> getObjectName(String filePath,int num) throws IOException{
		Map<String,String> nameMap=new HashMap<String, String>();
		
		File file = new File(filePath);
		//需要判断文件是否存在
		if(file.exists()){
			System.out.println("读取excle......");
			ExcelReader readExcel = new ExcelReader(file);
			readExcel.open();
			readExcel.setSheetNum(num); // 设置读取索引为1的工作表
			// 总行数
			int count = readExcel.getRowCount();
			
			System.out.println("count:"+count);
			for (int i = 1; i <= count; i++) {
				String[] rows = readExcel.readExcelLine(i);
				
				if(rows == null){
					continue;
				}				
				boolean nullValue=true;
				for(int y=0;y<rows.length;y++){
					if(!StrUtils.isEmpty(rows[y])){
						nullValue=false;
					}
				}
				if(nullValue){
					continue;
				}
				if(!nameMap.containsKey(rows[0])){
					nameMap.put(rows[0], rows[0]);
				}else{
					System.err.println(".....重复......:"+rows[0]);
				}
				
			}
		}
		return nameMap;
	}
	
	static WTPart getPart(String number) throws WTException {
		WTPart wtpart = null;
		QuerySpec qs= new QuerySpec(WTPart.class);
		SearchCondition sc = new SearchCondition(WTPart.class,
				WTPart.NUMBER, SearchCondition.EQUAL, number.trim());
		qs.appendWhere(sc);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		if (qr.size() > 0)
			wtpart = (WTPart) qr.nextElement();
		
		if(wtpart!=null){
			wtpart=getLatestPart((WTPartMaster) wtpart.getMaster());
		}
		return wtpart;
	}
	
	static WTPart getLatestPart(WTPartMaster partMaster) throws PersistenceException, WTException{
		WTPart part = null;
		if (partMaster != null) {
			QueryResult qr= VersionControlHelper.service
						.allVersionsOf(partMaster);
			if (qr != null && qr.hasMoreElements()) {
				part = (WTPart) qr.nextElement();
			}
		}
		return part;
	}

}
