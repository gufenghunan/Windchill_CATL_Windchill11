package com.catl.common.toolbox.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.util.Map;



import wt.httpgw.GatewayAuthenticator;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.util.WTException;

import com.catl.common.util.PartUtil;
import com.catl.loadData.Constant;
import com.catl.loadData.IBAUtility;
import com.catl.loadData.util.ExcelReader;

public class UpdatePartAttr implements RemoteAccess{
   
    public static void main(String[] args) {

        RemoteMethodServer rms = RemoteMethodServer.getDefault();
        GatewayAuthenticator auth = new GatewayAuthenticator();
        auth.setRemoteUser("wcadmin");
        rms.setAuthenticator(auth);

        if(args.length<2){
            System.out.println("Example Usage:" + 
                    "java com.catl.common.toolbox.data.UpdatePartAttr /data/UpdatePartAttr.xlsx -t");
        }
        System.out.println(args[0]+"......"+args[1]);
        invokeRemoteLoad(args[0],args[1]);
    }

    public static void invokeRemoteLoad(String filePath,String command){
        String method = "doLoad";
        String CLASSNAME = UpdatePartAttr.class.getName();
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
            Constant.loadGlobalAttribute();
            file = new FileWriter("/data/UpdatePartAttr.txt");
            
            BufferedWriter writer = new BufferedWriter(file);
            
            File f = new File(filePath);
            ExcelReader reader = new ExcelReader(f);
            reader.open();
            reader.setSheetNum(0);
            
            int count = reader.getRowCount();
            Map<WTPart,String> data = new LinkedHashMap<WTPart,String>();
            for(int i=1; i<=count; i++){
                String[] rows = reader.readExcelLine(i);
                WTPart wtpart = null;
            	wtpart = PartUtil.getLastestWTPartByNumber(rows[0]);
                if (wtpart == null) { 
                    System.out.println(" failed number="+rows[0]+" no exist!");
                    throw new WTException(" failed number="+rows[0]+" no exist!");
                }
                data.put(wtpart,rows[1]); 
            }
            for(WTPart dPart : data.keySet()){
                String value = data.get(dPart);
                IBAUtility iba = new IBAUtility(dPart);
                System.out.println(" update "+dPart.getNumber()+",key=oldPartNumber,value="+value);
                writer.write(" update "+dPart.getNumber()+",key=oldPartNumber,value="+value+"\n");
                if(command.equals("-r")){
                    iba.setIBAValue("oldPartNumber", value);
                    iba.updateAttributeContainer(dPart);
                    iba.updateIBAHolder(dPart);
                    System.out.println(" update "+dPart.getNumber()+",key=oldPartNumber,value="+value+" success");
                    writer.write(" update "+dPart.getNumber()+",key=oldPartNumber,value="+value+" success\n");
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
