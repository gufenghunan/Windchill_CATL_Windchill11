package com.catl.common.toolbox.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.util.Map;





import wt.doc.WTDocument;
import wt.httpgw.GatewayAuthenticator;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.util.WTException;

import com.catl.common.util.DocUtil;
import com.catl.common.util.PartUtil;
import com.catl.loadData.Constant;
import com.catl.loadData.IBAUtility;
import com.catl.loadData.util.ExcelReader;

public class UpdateDocProjectCodeAndName implements RemoteAccess{
   
    public static void main(String[] args) {

        RemoteMethodServer rms = RemoteMethodServer.getDefault();
        GatewayAuthenticator auth = new GatewayAuthenticator();
        auth.setRemoteUser("wcadmin");
        rms.setAuthenticator(auth);

        if(args.length<2){
            System.out.println("Example Usage:" + 
                    "java com.catl.common.toolbox.data.UpdateDocProjectCodeAndName /nfs/data/UpdateDocProjectCodeAndName.xlsx -t");
        }
        System.out.println(args[0]+"......"+args[1]);
        invokeRemoteLoad(args[0],args[1]);
    }

    public static void invokeRemoteLoad(String filePath,String command){
        String method = "doLoad";
        String CLASSNAME = UpdateDocProjectCodeAndName.class.getName();
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
        try {
            file = new FileWriter("/nfs/data/UpdateDocProjectCodeAndName.txt");
            
            BufferedWriter writer = new BufferedWriter(file);
            
            File f = new File(filePath);
            ExcelReader reader = new ExcelReader(f);
            reader.open();
            reader.setSheetNum(0);
            
            int count = reader.getRowCount();
            Map<WTDocument,String> data = new LinkedHashMap<WTDocument,String>();
            for(int i=1; i<=count; i++){
                String[] rows = reader.readExcelLine(i);
                WTDocument doc = null;
            	doc = DocUtil.getLatestWTDocument(rows[0]);
                if (doc == null) { 
                    System.out.println(" failed number="+rows[0]+" no exist!");
                    throw new WTException(" failed number="+rows[0]+" no exist!");
                }
                data.put(doc,rows[1]+"@&@"+rows[2]); 
            }
            for(WTDocument doc : data.keySet()){
                String value = data.get(doc);
                String[] valueArr = value.split("@&@");
                String projectCode = valueArr[0];
                String projectName = valueArr[1];
                IBAUtility iba = new IBAUtility(doc);
                writer.write(" update "+doc.getNumber()+",projectCode="+projectCode+",projectName="+projectName+"\n");
                if(command.equals("-r")){
                    iba.setIBAValue("projectCode", projectCode);
                    iba.setIBAValue("projectName", projectName);
                    iba.updateAttributeContainer(doc);
                    iba.updateIBAHolder(doc);
                    writer.write(" update "+doc.getNumber()+",projectCode="+projectCode+",projectName="+projectName+" success\n");
                }
            }
            writer.flush();
            writer.close();
        }catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static boolean isEmpty(String str){
        if(str == null || str.trim().equals("") || str.equals("null"))
            return true;
        return false;
    }



}
