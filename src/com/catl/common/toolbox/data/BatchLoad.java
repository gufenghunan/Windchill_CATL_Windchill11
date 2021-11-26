package com.catl.common.toolbox.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.httpgw.GatewayAuthenticator;
import wt.iba.value.IBAHolder;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;

import com.catl.common.util.GenericUtil;
import com.catl.loadData.StrUtils;
import com.catl.loadData.util.ExcelReader;
import com.catl.test.TestMain;
import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.meta.common.UpdateOperationIdentifier;

public class BatchLoad implements RemoteAccess{

    public static void main(String[] args) {

        RemoteMethodServer rms = RemoteMethodServer.getDefault();
        GatewayAuthenticator auth = new GatewayAuthenticator();
        auth.setRemoteUser("wcadmin");
        rms.setAuthenticator(auth);

        if(args.length<1){
            System.out.println("Example Usage:" + 
                    "java com.catl.common.toolbox.data.BatchLoad /data/doc.xlsx");
        }
        invokeRemoteLoad(args[0]);
    }


    public static void invokeRemoteLoad(String s){
        String method = "doLoad";
        String CLASSNAME = CLASSNAME = BatchLoad.class.getName();
        Class[] types = {String.class};
        Object[] values={s};
        try {
            RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void doLoad(String s){
        List<Map> docs= readData(s);

        FileWriter file=null;
        try {
            String wthomeFolder = WTProperties.getServerProperties().getProperty("wt.home");
            file = new FileWriter(wthomeFolder + File.separator + "logs" + File.separator + "dataLoad.csv");
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        BufferedWriter writer = new BufferedWriter(file);
        System.out.println("Total " + docs.size() + " documents need update");
        for(int i=0;i<docs.size();i++){
            Map<String,String> doc = docs.get(i);
            String number = doc.get("number");
            String subCategory = doc.get("subCategory");
            try {
                QuerySpec query = new QuerySpec(); 
                int docIndex;
                docIndex = query.appendClassList(WTDocument.class, true);
                query.appendWhere(
                        new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, number),
                        new int[] { docIndex }
                        ); 
                query.appendAnd();
                query.appendWhere(new SearchCondition(WTDocument.class, WTAttributeNameIfc.LATEST_ITERATION, SearchCondition.IS_TRUE),
                        new int[] { docIndex });
                QueryResult qr = PersistenceHelper.manager.find(query); 
                if (qr.hasMoreElements()) { 
                    Persistable objects[] = (Persistable[]) qr.nextElement(); 
                    WTDocument wtdoc = (WTDocument)objects[docIndex];
                    modifyIBAValue(wtdoc,"subCategory", subCategory);
                    writer.write(wtdoc.getNumber() + "," +  "Updated Succeed");
                    System.out.println(wtdoc.getNumber()+ " updated with subCateogory " + subCategory); 
                }else{
                    System.out.println( number  + "did not find in the system");
                    writer.write(number + "," + "Update Failed");
                }
                writer.write("\n");
            } catch (QueryException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (WTException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } 

        }//end for


        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    public static List<Map> readData(String filePath){
        List allRows = new ArrayList();
        File file = new File(filePath);
        ExcelReader reader = new ExcelReader(file);
        try {
            reader.open();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        reader.setSheetNum(0);
        int count = reader.getRowCount();
       
        for(int i=1; i<=count; i++){
            String rows[] = reader.readExcelLine(i);
            String number = rows[0];
            String subCategory = rows[1];
            if(isEmpty(number) && isEmpty(subCategory)){             
                Map<String,String> doc= new HashMap<String,String>();
                doc.put("number", number);
                doc.put("subCategory", subCategory);
                allRows.add(doc);

            }else{
                System.out.println(" Error line" + i + "has empty data ");
            }
        }//end for 

        return allRows;


    }

    public static void modifyIBAValue(IBAHolder holder, String attrName, String newValue){
        try{

            wt.iba.value.IBAHolder ibaHolder=wt.iba.value.service.IBAValueHelper.service.refreshAttributeContainer((wt.iba.value.IBAHolder)holder, null, null, null);
            wt.iba.definition.service.StandardIBADefinitionService defService=new wt.iba.definition.service.StandardIBADefinitionService();
            wt.iba.value.DefaultAttributeContainer attributeContainer=(wt.iba.value.DefaultAttributeContainer)ibaHolder.getAttributeContainer();
            wt.iba.definition.litedefinition.AttributeDefDefaultView attributeDefinition=defService.getAttributeDefDefaultViewByPath(attrName);
            wt.iba.value.litevalue.StringValueDefaultView attValue= (wt.iba.value.litevalue.StringValueDefaultView)attributeContainer.getAttributeValues(attributeDefinition)[0];
            attValue.setValue(newValue);
            attributeContainer.updateAttributeValue(attValue);
            wt.iba.value.service.StandardIBAValueService.theIBAValueDBService.updateAttributeContainer(ibaHolder, null, null, null);
        }
        catch(WTException ew){
            ew.printStackTrace();
        }

        catch (ArrayIndexOutOfBoundsException ea){
            ea.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static boolean isEmpty(String str){
        if(str == null || str.trim().equals("") || str.equals("null"))
            return true;
        return false;
    }



}
