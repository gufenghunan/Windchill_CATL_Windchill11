package com.catl.loadData;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTProperties;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.catl.loadData.util.ExcelReader;
import com.catl.loadData.util.ExcelWriter;

import wt.auth.SimpleAuthenticator;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.WTReference;
import wt.method.MethodContext;
import wt.method.MethodServerException;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.pom.DataServicesRegistry;
import wt.pom.Transaction;
import wt.pom.UnsupportedPDSException;
import wt.pom.WTConnection;
import wt.query.QuerySpec;
import wt.query.SearchCondition;

public class UpdateCreator implements RemoteAccess {

	
	private static List<String[]> logs=new ArrayList<String[]>();
	
	public static void main(String[] args) throws RemoteException, InvocationTargetException {
		RemoteMethodServer ms = RemoteMethodServer.getDefault();
		ms.setUserName("dms");
		ms.setPassword("dms");
		try {
			SessionHelper.manager.setAuthenticatedPrincipal("dms");
			RemoteMethodServer.getDefault()
			.invoke("creatorUpadate",
					UpdateCreator.class.getName(), null, null,
					null);
		} catch (WTException e) {
			e.printStackTrace();
		}
	}	
	
	
	public static void creatorUpadate() throws WTException, IOException{
		String rootPath=WTProperties.getLocalProperties().getProperty("wt.home")+"/loadFiles/com/catl/dms";
		Transaction trx = null;
		try {
			trx = new Transaction();
			trx.start();
		
			Map<String,String> docMap=getObjectCreator(rootPath+"/文档创建者列表.xlsx");
			for(String key:docMap.keySet()){
				updateDocCreator(key,docMap.get(key));
			}
			
			Map<String,String> partMap=getObjectCreator(rootPath+"/Part创建者列表.xlsx");
			for(String key:partMap.keySet()){
				updatePartCreator(key,partMap.get(key));
			}
			
			ExcelWriter writer = new ExcelWriter();
			boolean flag = writer.exportExcelList(rootPath+"/刷创建者日志.xlsx","刷创建者日志", new String[]{"number","name","备注"}, logs);
			System.out.println("刷创建者日志.xlsx flag="+flag);
			
			trx.commit();
			trx = null;			
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if (trx != null){
				System.out.println("---------creatorUpadate rollback--------");
				trx.rollback();
			}
		}
		
	}
	
	public static void updatePartCreator(String number,String name) throws Exception{		
		
		Vector<WTPart> wtparts=getPart(number);
			
		if(wtparts.size()==0){
			logs.add(new String[]{number,name,"系统不存在该编码的物料"});
		}else{
			logs.add(new String[]{number,name,"物料更新成功"});
		}
		
		for(WTPart part:wtparts){
			long ObjectID = part.getPersistInfo().getObjectIdentifier().getId();
			
			//System.out.println("ObjectID："+ObjectID);			
			
			String sql="update wtpart set ida3d2iterationinfo = (select ida2a2 from wtuser where name ='"+name+"'),"
				    +"ida3b2iterationinfo = (select ida2a2 from wtuser where name ='"+name+"')"+" where ida2a2 = "+ObjectID;
					
			//System.out.println("sql:"+sql);
			
			WTConnection wtconnection = null;
			PreparedStatement pstmt = null;
			ResultSet result = null;			
				
			MethodContext methodcontext = getMethodContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			pstmt = wtconnection.prepareStatement(sql);
			result = pstmt.executeQuery();
							
			if (result != null) {
				result.close();
			}
			
			if (pstmt != null) {
				pstmt.close();					
			}			
		}					
	}
	
	public static void updateDocCreator(String number,String name) throws Exception{		
		
		Vector<WTDocument> wtdocs=getDoc(number);
	
		if(wtdocs.size()==0){
			logs.add(new String[]{number,name,"系统不存在该编码的文档"});
		}else{
			logs.add(new String[]{number,name,"文档更新成功"});
		}
		
		for(WTDocument doc:wtdocs){
			long ObjectID = doc.getPersistInfo().getObjectIdentifier().getId();
			
			//System.out.println("ObjectID："+ObjectID);			
			
			String sql="update wtdocument set ida3d2iterationinfo = (select ida2a2 from wtuser where name ='"+name+"'),"
				    +"ida3b2iterationinfo = (select ida2a2 from wtuser where name ='"+name+"')"+" where ida2a2 = "+ObjectID;
					
			//System.out.println("sql:"+sql);
			
			WTConnection wtconnection = null;
			PreparedStatement pstmt = null;
			ResultSet result = null;			
				
			MethodContext methodcontext = getMethodContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			pstmt = wtconnection.prepareStatement(sql);
			result = pstmt.executeQuery();
							
			if (result != null) {
				result.close();					
			}
			
			if (pstmt != null) {
				pstmt.close();					
			}	
			
		}
				
	}
	
	public static Vector<WTDocument> getDoc(String number) throws WTException {
		Vector<WTDocument> wtdoc = new Vector<WTDocument>();
		QuerySpec qs= new QuerySpec(WTDocument.class);
		SearchCondition sc = new SearchCondition(WTDocument.class,
				WTDocument.NUMBER, SearchCondition.EQUAL, number.trim());
		qs.appendWhere(sc);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		while(qr.hasMoreElements()){
			wtdoc.add((WTDocument) qr.nextElement());
		}
		return wtdoc;
	}
	
	public static Vector<WTPart> getPart(String number) throws WTException {
		Vector<WTPart> wtparts = new Vector<WTPart>();
		QuerySpec qs= new QuerySpec(WTPart.class);
		SearchCondition sc = new SearchCondition(WTPart.class,
				WTPart.NUMBER, SearchCondition.EQUAL, number.trim());
		qs.appendWhere(sc);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		while(qr.hasMoreElements()){
			wtparts.add((WTPart) qr.nextElement());
		}
		return wtparts;
	}
	
	public static Persistable getPersistableByOid(String oid) throws WTException {
		WTObject obj = null;
		ReferenceFactory referencefactory = new ReferenceFactory();
		WTReference wtreference = referencefactory.getReference(oid);
		if (wtreference.getObject() != null) {
			obj = (WTObject) wtreference.getObject();
		}
		return obj;
	}
	
	public static MethodContext getMethodContext()throws UnsupportedPDSException, UnknownHostException {
		MethodContext methodcontext = null;
		try {
			methodcontext = MethodContext.getContext();
		}catch (MethodServerException methodserverexception) {
			RemoteMethodServer.ServerFlag = true;
			InetAddress inetaddress = InetAddress.getLocalHost();
			String s = inetaddress.getHostName();
			if (s == null)
				s = inetaddress.getHostAddress();
		}
		return methodcontext;
	}
	
	public static Map<String,String> getObjectCreator(String filePath){
		Map<String,String> docCreatorMap=new HashMap<String, String>();
		
		File file = new File(filePath);
		//需要判断文件是否存在
		if(file.exists()){
			System.out.println("读取excle......");
			ExcelReader readExcel = new ExcelReader(file);
			try{
				readExcel.open();
				readExcel.setSheetNum(0); // 设置读取索引为1的工作表
				// 总行数
				int count = readExcel.getRowCount();
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
					docCreatorMap.put(rows[0], rows[1]);				
				}
			}catch(Exception e){
				e.printStackTrace();
			}		
		}
		return docCreatorMap;
	}

}
