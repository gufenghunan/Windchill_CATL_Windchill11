package com.catl.common.toolbox.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import wt.doc.WTDocument;
import wt.doc.WTDocumentMasterIdentity;
import wt.fc.Identified;
import wt.fc.IdentityHelper;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.httpgw.GatewayAuthenticator;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;

import com.catl.loadData.IBAUtility;
import com.catl.loadData.util.ExcelReader;

public class UpdateDocSubCategory implements RemoteAccess{
   
    public static void main(String[] args) {

        RemoteMethodServer rms = RemoteMethodServer.getDefault();
        GatewayAuthenticator auth = new GatewayAuthenticator();
        auth.setRemoteUser("wcadmin");
        rms.setAuthenticator(auth);

        if(args.length<2){
            System.out.println("Example Usage:" + 
                    "java com.catl.common.toolbox.data.UpdateDocSubCategory /data/UpdateDocSubCategory.xlsx -t");
        }
        System.out.println(args[0]+"......"+args[1]);
        invokeRemoteLoad(args[0],args[1]);
    }

    public static void invokeRemoteLoad(String filePath,String command){
        String method = "doLoad";
        String CLASSNAME = UpdateDocSubCategory.class.getName();
        Class[] types = {String.class,String.class};
        Object[] values={filePath,command};
        try {
            RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void doLoad(String filePath,String command){
    	FileWriter file=null;
    	Transaction trx = null;
        try {
        	trx = new Transaction();
        	trx.start();
        	
            file = new FileWriter("/data/UpdateDocSubCategory.txt");
            
            BufferedWriter writer = new BufferedWriter(file);
            
        	File f = new File(filePath);
            ExcelReader reader = new ExcelReader(f);
            reader.open();
            reader.setSheetNum(0);
            
            int count = reader.getRowCount();
            for(int i=1; i<=count; i++){
                String[] rows = reader.readExcelLine(i);
                String number = rows[0];
                String subCategory = rows[1];
                String name = rows[2];
                String newNumber;
                try{
                    newNumber = rows[3];
                }catch(Exception e){
                    newNumber=null;
                }
                WTDocument wtDoc = null;
                String oldSpecification = null;
        		QuerySpec query = new QuerySpec(); 
                int partIndex;
                partIndex = query.appendClassList(WTDocument.class, true);
                query.appendWhere(
                        new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, number),
                        new int[] { partIndex }
                        ); 
                query.appendAnd();
                query.appendWhere(new SearchCondition(WTDocument.class, WTAttributeNameIfc.LATEST_ITERATION, SearchCondition.IS_TRUE),
                        new int[] { partIndex });
                QueryResult qr = PersistenceHelper.manager.find(query); 
                if (qr.hasMoreElements()) { 
                    Persistable objects[] = (Persistable[]) qr.nextElement(); 
                    wtDoc = (WTDocument)objects[partIndex];
                }else{
                    System.out.println(" failed number="+number+" no exist!");
                    throw new WTException(" failed number="+number+" no exist!");
                }
                IBAUtility iba = new IBAUtility(wtDoc);
                String nameText="";
                String newNumberText="";
                String subCategoryText="";
                if(!isEmpty(name)){
                	nameText=",name="+name;
                }
                if(!isEmpty(newNumber)){
                	newNumberText=",newNumber="+newNumber;
                }
                if(!isEmpty(subCategory)){
                    subCategoryText=",subCategory="+subCategory;
                }
                System.out.println(" update "+wtDoc.getNumber()+subCategoryText+nameText+newNumberText);
                writer.write(" update "+wtDoc.getNumber()+",subCategory="+subCategory+nameText+newNumberText+"\n");
                if(command.equals("-r")){
                    if(!isEmpty(subCategory)){
                        iba.setIBAValue("subCategory", subCategory);
                        iba.updateAttributeContainer(wtDoc);
                        iba.updateIBAHolder(wtDoc);
                    }
                    if(!isEmpty(name) || !isEmpty(newNumber)){
                    	Identified identified = (Identified) wtDoc.getMaster();
                        WTDocumentMasterIdentity identity = (WTDocumentMasterIdentity)identified.getIdentificationObject();
                        if(!isEmpty(name)){
                        	identity.setName(name);
                        }
                        if(!isEmpty(newNumber)){
                        	identity.setNumber(newNumber);
                        }
                        IdentityHelper.service.changeIdentity(identified, identity);
                    }
                    System.out.println(" update "+wtDoc.getNumber()+",subCategory="+subCategory+nameText+newNumberText+" success");
                    writer.write(" update "+wtDoc.getNumber()+",subCategory="+subCategory+nameText+newNumberText+" success\n");
            	}
            }
            writer.flush();
            writer.close();
            System.out.println("导出 UpdateDocSubCategory.xlsx成功");
            
            trx.commit();
            trx = null;
        }catch (Exception e) {
            e.printStackTrace();
        }finally{
        	if(trx != null){
        		trx.rollback();
        	}
        }
    }


    public static boolean isEmpty(String str){
        if(str == null || str.trim().equals("") || str.equals("null"))
            return true;
        return false;
    }



}
