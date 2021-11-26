package com.catl.common.toolbox.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.catl.bom.workflow.BomWfUtil;
import com.catl.common.util.DocUtil;
import com.catl.common.util.EpmUtil;
import com.catl.loadData.util.ExcelReader;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentHelper;
import wt.epm.EPMDocumentMaster;
import wt.epm.build.EPMBuildHistory;
import wt.epm.structure.EPMReferenceLink;
import wt.epm.structure.EPMStructureHelper;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.httpgw.GatewayAuthenticator;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.query.ClassAttribute;
import wt.query.ConstantExpression;
import wt.query.QuerySpec;
import wt.query.SQLFunction;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;

public class Delete3DPartReference implements RemoteAccess{

	public static void main(String[] args) {

        RemoteMethodServer rms = RemoteMethodServer.getDefault();
        GatewayAuthenticator auth = new GatewayAuthenticator();
        auth.setRemoteUser("wcadmin");
        rms.setAuthenticator(auth);

        if(args.length<2){
            System.out.println("Example Usage:" + 
                    "java com.catl.common.toolbox.data.Delete3DPartReference epm3DNumber partNumber command");
        }
        System.out.println(args[0]+","+args[1]+","+args[2]);
        invokeRemoteLoad(args[0],args[1],args[2]);
    }

    public static void invokeRemoteLoad(String epm3DNumber,String partNumber,String command){
        String method = "doLoad";
        String CLASSNAME = Delete3DPartReference.class.getName();
        Class[] types = {String.class,String.class,String.class};
        Object[] values={epm3DNumber,partNumber,command};
        try {
            RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void doLoad(String epm3DNumber,String partNumber,String command) throws WTException, IOException{
    	FileWriter file = new FileWriter("/data/Delete3DPartReference.txt");
        BufferedWriter writer = new BufferedWriter(file);
        
    	List<EPMDocument> epm3DList = getAllEPMDocumentByNumber(epm3DNumber);
    	List<WTPart> partList = getAllWTPartByNumber(partNumber);
		try {
			QuerySpec queryspec = new QuerySpec(EPMBuildHistory.class);
			queryspec.appendOpenParen();
			for(int i = 0; i<epm3DList.size(); i++){
				EPMDocument temp = epm3DList.get(i);
				queryspec.appendWhere(new SearchCondition(EPMBuildHistory.class, "roleAObjectRef.key.id", SearchCondition.EQUAL, temp.getPersistInfo().getObjectIdentifier().getId()));	    		
				if(i != epm3DList.size() -1)
					queryspec.appendOr();
			}
			queryspec.appendCloseParen();
			
			queryspec.appendAnd();
			queryspec.appendOpenParen();
			for(int i = 0; i<partList.size(); i++){
				WTPart temp = partList.get(i);
				queryspec.appendWhere(new SearchCondition(EPMBuildHistory.class, "roleBObjectRef.key.id", SearchCondition.EQUAL, temp.getPersistInfo().getObjectIdentifier().getId()));	    		
				if(i != partList.size() -1)
					queryspec.appendOr();
			}
			queryspec.appendCloseParen();
			writer.write(queryspec.toString()+"\n");
			QueryResult queryresult = PersistenceHelper.manager.find(queryspec);
			while (queryresult.hasMoreElements()) {
				EPMBuildHistory link = (EPMBuildHistory) queryresult.nextElement();
				String msg = link.getPersistInfo().getObjectIdentifier().getId()+"|"+BomWfUtil.getObjectnumber(link.getRoleAObject())+"|"+BomWfUtil.getObjectnumber(link.getRoleBObject());
				writer.write(msg+"\n");
				if(command.equals("r")){
					PersistenceServerHelper.manager.remove(link);
					writer.write(msg+"删除成功\n");
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}finally{
			writer.flush();
            writer.close();
            file.close();
		}
    }
    
    public static List<EPMDocument> getAllEPMDocumentByNumber(String numStr) {
		try {
			List<EPMDocument> retList = new ArrayList<EPMDocument>();
			QuerySpec queryspec = new QuerySpec(EPMDocument.class);

			queryspec.appendSearchCondition(new SearchCondition(EPMDocument.class, EPMDocument.NUMBER, SearchCondition.EQUAL, numStr));
			QueryResult queryresult = PersistenceHelper.manager.find(queryspec);
			while (queryresult.hasMoreElements()) {
				retList.add((EPMDocument)queryresult.nextElement());
			}
			return retList;
		} catch (WTException e) {
			e.printStackTrace();
		}
		return null;
	}
    
    public static List<WTPart> getAllWTPartByNumber(String numStr) {
		try {
			List<WTPart> retList = new ArrayList<WTPart>();
			QuerySpec queryspec = new QuerySpec(WTPart.class);

			queryspec.appendSearchCondition(new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL, numStr));
			QueryResult queryresult = PersistenceHelper.manager.find(queryspec);
			while (queryresult.hasMoreElements()) {
				retList.add((WTPart)queryresult.nextElement());
			}
			return retList;
		} catch (WTException e) {
			e.printStackTrace();
		}
		return null;
	}


}
