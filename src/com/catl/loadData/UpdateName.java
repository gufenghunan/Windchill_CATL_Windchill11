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

import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.doc.WTDocumentMasterIdentity;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.EPMDocumentMasterIdentity;
import wt.fc.IdentityHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartMasterIdentity;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;

public class UpdateName implements RemoteAccess {
	
	private static List<String[]> logs=new ArrayList<String[]>();
	
	public static void main(String[] args) {
		RemoteMethodServer ms = RemoteMethodServer.getDefault();
		ms.setUserName("dms");
		ms.setPassword("dms");
		
		try {
			SessionHelper.manager.setAuthenticatedPrincipal("dms");
			RemoteMethodServer.getDefault()
			.invoke("updateObjectName",
					UpdateName.class.getName(), null, null,
					null);
		} catch (WTException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public static void updateObjectName() throws IOException{
				
		String rootPath=WTProperties.getLocalProperties().getProperty("wt.home")+"/loadFiles/com/catl/dms";
		Transaction trx = null;
		try{
			trx = new Transaction();
			trx.start();
			
			Map<String,String> docNameMap=getObjectName(rootPath+"/需修改数模名称物料表.xlsx",0);
			Map<String,String> partNameMap=getObjectName(rootPath+"/需修改数模名称物料表.xlsx",1);
			Map<String,String> epmNameMap=getObjectName(rootPath+"/需修改数模名称物料表.xlsx",2);
			
			for(String docnum:docNameMap.keySet()){
				WTDocument doc=getDoc(docnum);
				if(doc!=null){
					updateDocName((WTDocumentMaster)doc.getMaster(),docNameMap.get(docnum));
					logs.add(new String[]{"二维图：",docnum,docNameMap.get(docnum),"二维图名称更新成功"});
				}else{
					logs.add(new String[]{"二维图：",docnum,docNameMap.get(docnum),"系统不存在该编码的二维图"});
				}
				//System.out.println("二维图："+doc+"--"+docNameMap.get(doc));
			}
			
			for(String partnum:partNameMap.keySet()){
				WTPart part=getPart(partnum);
				if(part!=null){
					updatePartName((WTPartMaster)part.getMaster(),partNameMap.get(partnum));
					logs.add(new String[]{"物料",partnum,partNameMap.get(partnum),"物料名称更新成功"});
				}else{
					logs.add(new String[]{"物料",partnum,partNameMap.get(partnum),"系统不存在该编码的物料"});
				}
				//System.out.println("物料："+part+"--"+partNameMap.get(part));
			}
			
			for(String epmnum:epmNameMap.keySet()){
				EPMDocument epmdoc=getEPM(epmnum);
				if(epmdoc!=null){
					updateEPMName((EPMDocumentMaster)epmdoc.getMaster(),epmNameMap.get(epmnum));
					logs.add(new String[]{"CAITA图",epmnum,epmNameMap.get(epmnum),"数模名称更新成功"});
				}else{
					logs.add(new String[]{"CAITA图",epmnum,epmNameMap.get(epmnum),"系统不存在该编码的数模"});
				}
				//System.out.println("图档："+epmnum+"--"+epmNameMap.get(epmnum));
			}
			
			ExcelWriter writer = new ExcelWriter();
			boolean flag = writer.exportExcelList(rootPath+"/更新名称日志.xlsx","更新名称日志", new String[]{"数据类型","编码","更改名称","备注"}, logs);
			System.out.println("更新名称日志.xlsx flag="+flag);
			
			trx.commit();
			trx = null;
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if (trx != null){
				System.out.println("---------updateObjectName rollback--------");
				trx.rollback();
			}
			logs=new ArrayList<String[]>();
		}
	}
	
	public static void updateDocName(WTDocumentMaster docMaster, String name)
			throws WTException, WTPropertyVetoException {		
		WTDocumentMasterIdentity docIdentity = (WTDocumentMasterIdentity) docMaster
					.getIdentificationObject();
		docIdentity.setName(name);
		docMaster = (WTDocumentMaster) IdentityHelper.service
				.changeIdentity(docMaster, docIdentity);
	}
	
	
	static WTDocument getDoc(String number) throws WTException {
		WTDocument wtdoc = null;
		QuerySpec qs= new QuerySpec(WTDocument.class);
		SearchCondition sc = new SearchCondition(WTDocument.class,
				WTDocument.NUMBER, SearchCondition.EQUAL, number.trim());
		qs.appendWhere(sc);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		if (qr.size() > 0)
			wtdoc = (WTDocument) qr.nextElement();
		return wtdoc;
	}
	
	public static void updatePartName(WTPartMaster partMaster, String name)
			throws WTException, WTPropertyVetoException {		
		
		WTPartMasterIdentity partIdentity = (WTPartMasterIdentity) partMaster
					.getIdentificationObject();
		partIdentity.setName(name);
		partMaster = (WTPartMaster) IdentityHelper.service
				.changeIdentity(partMaster, partIdentity);
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
		return wtpart;
	}
	
	public static void updateEPMName(EPMDocumentMaster epmMaster, String name)
			throws WTException, WTPropertyVetoException {
		
		EPMDocumentMasterIdentity partIdentity = (EPMDocumentMasterIdentity) epmMaster
					.getIdentificationObject();
		partIdentity.setName(name);
		epmMaster = (EPMDocumentMaster) IdentityHelper.service
				.changeIdentity(epmMaster, partIdentity);
		
	}
	
	static EPMDocument getEPM(String number) throws WTException {
		EPMDocument epmdoc = null;
		QuerySpec qs= new QuerySpec(EPMDocument.class);
		SearchCondition sc = new SearchCondition(EPMDocument.class,
				EPMDocument.NUMBER, SearchCondition.EQUAL, number.trim());
		qs.appendWhere(sc);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		if (qr.size() > 0)
			epmdoc = (EPMDocument) qr.nextElement();
		return epmdoc;
	}
	
	public static Map<String,String> getObjectName(String filePath,int num){
		Map<String,String> nameMap=new HashMap<String, String>();
		
		File file = new File(filePath);
		//需要判断文件是否存在
		if(file.exists()){
			System.out.println("读取excle......");
			ExcelReader readExcel = new ExcelReader(file);
			try{
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
						nameMap.put(rows[0], rows[2]);
					}else{
						System.err.println(".....重复......:"+rows[0]);
					}
					
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return nameMap;
	}

}
