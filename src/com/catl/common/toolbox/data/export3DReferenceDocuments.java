package com.catl.common.toolbox.data;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.structure.EPMStructureHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;

import com.catl.loadData.util.ExcelWriter;

public class export3DReferenceDocuments implements RemoteAccess{

    public static void main(String[] args) throws RemoteException, InvocationTargetException {
		
		if (args == null || args.length < 2){
        	
        	System.out.printf("请输入正确的系统对象的用户名、密码！");
        } else {
			RemoteMethodServer ms = RemoteMethodServer.getDefault();
			ms.setUserName(args[0]);
			ms.setPassword(args[1]);
			
			try {
				SessionHelper.manager.setAuthenticatedPrincipal(args[0]);
				RemoteMethodServer.getDefault().invoke("doLoad", export3DReferenceDocuments.class.getName(), null, null, null);
			} catch (WTException e) {
				e.printStackTrace();
			}
        }
	}

    public static void doLoad(){
    	
    	String logPath = "/data/";
        try {
        	
        	List<String[]> logs=new ArrayList<String[]>();
			
        	Vector<EPMDocument> epmlist = get3DEPMDocument();
        	for (EPMDocument epm : epmlist){
        		
        		String number3D = epm.getNumber();
        		String name3D = epm.getName();
        		String status3D = epm.getState().toString();
        		
        		QueryResult qr1 = EPMStructureHelper.service.navigateReferencedBy((EPMDocumentMaster)epm.getMaster(), null, true);
				
        		if (qr1.hasMoreElements()){
        			
        			List<String> epmidlist = new ArrayList<String>();
        			while(qr1.hasMoreElements()){
        				
        				Object refObject = qr1.nextElement();
                        if (refObject instanceof EPMDocument) {
                        	
                            EPMDocument referencedDoc = (EPMDocument) refObject;
                            if (!epmidlist.contains(referencedDoc.getNumber())){ // 参考方文件取最新版本信息
                            	
                            	EPMDocument latestEpm = (EPMDocument) VersionControlHelper.getLatestIteration(referencedDoc, false);
                            	epmidlist.add(latestEpm.getNumber());
                            	
                            	String referenceNumber = latestEpm.getNumber();
            					String referenceName = latestEpm.getName();
            					String referenceStatus = latestEpm.getState().toString();
            					
            					logs.add(new String[]{number3D, name3D, status3D, referenceNumber, referenceName, referenceStatus});
                            }
                        }
    				}
        		} else {
        			logs.add(new String[]{number3D, name3D, status3D, "", "", ""});
        		}
        		
        	}
        	
        	System.out.println("总的文件行数="+logs.size());
        	
        	ExcelWriter writer = new ExcelWriter();
        	boolean flag = writer.exportExcelList(logPath+"3D参考方文件信息.xlsx","3D参考方文件信息", new String[]{"3D编号", "3D名称", "3D状态", "参考方编号", "参考方名称", "参考方状态"}, logs);
			System.out.println("3D参考方文件信息.xlsx flag="+flag);
			logs=new ArrayList<String[]>();
        	
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    static Vector<EPMDocument> get3DEPMDocument() throws WTException {
    	
		Vector<EPMDocument> epmDocs = new Vector<EPMDocument>();
		
		QuerySpec queryspec = new QuerySpec(EPMDocument.class);
		queryspec.appendSearchCondition(new SearchCondition(EPMDocument.class, EPMDocument.NUMBER, SearchCondition.LIKE, "%.CATPART"));
		queryspec.appendOr();
        queryspec.appendSearchCondition(new SearchCondition(EPMDocument.class, EPMDocument.NUMBER, SearchCondition.LIKE, "%.CATPRODUCT"));

		QueryResult queryresult = PersistenceHelper.manager.find(queryspec);
		
		LatestConfigSpec cfg = new LatestConfigSpec();
		QueryResult qr = cfg.process(queryresult);

		while (qr.hasMoreElements()) {
			EPMDocument epm = (EPMDocument) qr.nextElement();
			epmDocs.add(epm);
		}
		
		return epmDocs;
	}
}
