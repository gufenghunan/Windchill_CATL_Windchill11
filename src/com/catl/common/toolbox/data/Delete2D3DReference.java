package com.catl.common.toolbox.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
import wt.epm.structure.EPMReferenceLink;
import wt.epm.structure.EPMStructureHelper;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.httpgw.GatewayAuthenticator;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.pom.Transaction;
import wt.query.ClassAttribute;
import wt.query.ConstantExpression;
import wt.query.QuerySpec;
import wt.query.SQLFunction;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;

public class Delete2D3DReference implements RemoteAccess{

	public static void main(String[] args) {

        RemoteMethodServer rms = RemoteMethodServer.getDefault();
        GatewayAuthenticator auth = new GatewayAuthenticator();
        auth.setRemoteUser("wcadmin");
        rms.setAuthenticator(auth);

        if(args.length<2){
            System.out.println("Example Usage:" + 
                    "java com.catl.common.toolbox.data.Delete2D3DReference epm3DNumber epm2DNumber command");
        }
        System.out.println(args[0]+","+args[1]+","+args[2]);
        invokeRemoteLoad(args[0],args[1],args[2]);
    }

    public static void invokeRemoteLoad(String epm3DNumber,String epm2DNumber,String command){
        String method = "doLoad";
        String CLASSNAME = Delete2D3DReference.class.getName();
        Class[] types = {String.class,String.class,String.class};
        Object[] values={epm3DNumber,epm2DNumber,command};
        try {
            RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void doLoad(String epm3DNumber,String epm2DNumber,String command) throws WTException, IOException{
    	FileWriter file = new FileWriter("/data/Delete2D3DReference.txt");
        BufferedWriter writer = new BufferedWriter(file);
        
    	EPMDocument epm3D = DocUtil.getLastestEPMDocumentByNumber(epm3DNumber);
    	List<EPMDocument> epm2DList = getAllEPMDocumentByNumber(epm2DNumber);
		try {
			QuerySpec queryspec = new QuerySpec(EPMReferenceLink.class);

			queryspec.appendWhere(new SearchCondition(EPMReferenceLink.class, "roleBObjectRef.key.id", SearchCondition.EQUAL, epm3D.getMaster().getPersistInfo().getObjectIdentifier().getId()));
			queryspec.appendAnd();
			queryspec.appendOpenParen();
			for(int i = 0; i<epm2DList.size(); i++){
				EPMDocument temp = epm2DList.get(i);
				queryspec.appendWhere(new SearchCondition(EPMReferenceLink.class, "roleAObjectRef.key.id", SearchCondition.EQUAL, temp.getPersistInfo().getObjectIdentifier().getId()));	    		
				if(i != epm2DList.size() -1)
					queryspec.appendOr();
			}
			queryspec.appendCloseParen();
			writer.write(queryspec.toString()+"\n");
			QueryResult queryresult = PersistenceHelper.manager.find(queryspec);
			while (queryresult.hasMoreElements()) {
				EPMReferenceLink link = (EPMReferenceLink) queryresult.nextElement();
				String msg = link.getPersistInfo().getObjectIdentifier().getId()+"|"+link.getAsStoredChildName();
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


}
