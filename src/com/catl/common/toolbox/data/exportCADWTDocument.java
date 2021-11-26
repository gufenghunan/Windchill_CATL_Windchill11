package com.catl.common.toolbox.data;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
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
import wt.vc.config.LatestConfigSpec;

import com.catl.common.constant.TypeName;
import com.catl.common.util.DocUtil;
import com.catl.common.util.GenericUtil;
import com.catl.common.util.PartUtil;
import com.catl.loadData.util.ExcelWriter;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.core.meta.type.mgmt.server.impl.WTTypeDefinition;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;

public class exportCADWTDocument implements RemoteAccess{

	private static String homePath = "";
	
	static {
		try {
			WTProperties wtproperties = WTProperties.getLocalProperties();
			homePath = wtproperties.getProperty("wt.home");
		} catch (Throwable t) {
			throw new ExceptionInInitializerError(t);
		}
	}
	
    public static void main(String[] args) throws RemoteException, InvocationTargetException {
		
		if (args == null || args.length < 2){
        	
        	System.out.printf("请输入正确的系统对象的用户名、密码！");
        } else {
			RemoteMethodServer ms = RemoteMethodServer.getDefault();
			ms.setUserName(args[0]);
			ms.setPassword(args[1]);
			
			try {
				SessionHelper.manager.setAuthenticatedPrincipal(args[0]);
				RemoteMethodServer.getDefault().invoke("doLoad", exportCADWTDocument.class.getName(), null, null, null);
			} catch (WTException e) {
				e.printStackTrace();
			}
        }
	}

    public static void doLoad(){
    	
    	String logPath = "/data/";
        try {
        	
        	String docType1 = "参考文档";
        	String docType2 = "CAD/动态文档";
        	
        	List<String[]> logs=new ArrayList<String[]>();
			
        	Vector<WTPart> partlist = getPart();
        	for (WTPart part : partlist){
        		
//        		WTPart part = PartUtil.getLastestWTPartByNumber("570200-00035");
            	String partNumber = part.getNumber();
            	String partName = part.getName();
            	String partStatus = part.getState().toString();
            	
            	// 获取关联文档
            	QueryResult docresult =PartDocServiceCommand.getAssociatedReferenceDocuments(part);
                WTDocument doc;
                while (docresult.hasMoreElements()) {
                    doc = (WTDocument) docresult.nextElement();
                    String docNumber = doc.getNumber();
                    String docName = doc.getName();
                    String docStatus = doc.getState().toString();
                    
                    logs.add(new String[]{partNumber, partName, partStatus, docNumber, docName, docStatus, docType1});
                }
            	
                // 获取CAD/动态文档
                QueryResult cadresult =PartDocServiceCommand.getAssociatedCADDocuments(part);
        		EPMDocument epmdoc;
                while(cadresult.hasMoreElements()){
                	epmdoc =(EPMDocument)cadresult.nextElement();
                    
                	String epmNumber = epmdoc.getNumber();
                    String epmName = epmdoc.getName();
                    String epmStatus = epmdoc.getState().toString();
                    
                    logs.add(new String[]{partNumber, partName, partStatus, epmNumber, epmName, epmStatus, docType2});
                }
        	}
        	
        	System.out.println("总的文件行数="+logs.size());
        	
        	int pageSize = 10000;
        	int pageNum = logs.size() / pageSize + 1;
        	
        	ExcelWriter writer = new ExcelWriter();
        	for (int i = 1; i <= pageNum; i++){
        		List<String[]> newlogs = new ArrayList<String[]>();
        		int start = (i-1)*pageSize;
        		int end = i*pageSize;
        		if (logs.size() < end){
        			end = logs.size();
        		}
        		for (int j=start; j<end; j++){
        			newlogs.add(logs.get(j));
        		}
        		boolean flag = writer.exportExcelList(logPath+"零部件相关文档信息_"+i+".xlsx","零部件相关文档信息", new String[]{"零部件编号", "零部件名称", "零部件状态", "文档编号", "文档名称", "文档状态", "文档类型"}, newlogs);
    			System.out.println("零部件相关文档信息_"+i+".xlsx flag="+flag);
    			newlogs=new ArrayList<String[]>();
        	}
        	
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    static Vector<WTPart> getPart() throws WTException {
    	
		Vector<WTPart> wtparts=new Vector<WTPart>();
		QuerySpec qs= new QuerySpec(WTPartMaster.class);
//		qs.appendWhere(new SearchCondition(WTPart.class, WTPart.LATEST_ITERATION, SearchCondition.IS_TRUE), new int[] {0});
		QueryResult qr = PersistenceHelper.manager.find(qs);
		List<String> partNumber=new ArrayList<String>();
		while(qr.hasMoreElements()){			
			WTPartMaster wtpartmaster = (WTPartMaster) qr.nextElement();
			if(!partNumber.contains(wtpartmaster.getNumber())){
				WTPart wtpart=getLatestPart(wtpartmaster);
				wtparts.add(wtpart);
				partNumber.add(wtpart.getNumber());
			}
		}
		return wtparts;
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
