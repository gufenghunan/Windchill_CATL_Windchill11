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

public class UpdatePartPhase implements RemoteAccess{
   
    public static void main(String[] args) {

        RemoteMethodServer rms = RemoteMethodServer.getDefault();
        GatewayAuthenticator auth = new GatewayAuthenticator();
        auth.setRemoteUser("wcadmin");
        rms.setAuthenticator(auth);

        if(args.length<1){
            System.out.println("Example Usage:" + 
                    "java com.catl.common.toolbox.data.UpdatePartPhase /data/UpdatePartPhase.xlsx");
        }
        System.out.println(args[0]+"......");
        invokeRemoteLoad(args[0]);
    }

    public static void invokeRemoteLoad(String filePath){
        String method = "doLoad";
        String CLASSNAME = UpdatePartPhase.class.getName();
        Class[] types = {String.class};
        Object[] values={filePath};
        try {
            RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void doLoad(String filePath){
        try {           
            
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
                iba.setIBAValue("ProductPhase", value);
                iba.updateAttributeContainer(dPart);
                iba.updateIBAHolder(dPart);
                System.out.println(" update "+dPart.getNumber()+",key=ProductPhase,value="+value+" success");
                 
                
            }
            reader.setSheetNum(1);
            int countline = reader.getRowCount();
            Map<WTPart,String> dataline = new LinkedHashMap<WTPart,String>();
            for(int i=1; i<=countline; i++){
                String[] rows = reader.readExcelLine(i);
                WTPart wtpart = null;
            	wtpart = PartUtil.getLastestWTPartByNumber(rows[0]);
                if (wtpart == null) { 
                    System.out.println(" failed number="+rows[0]+" no exist!");
                    throw new WTException(" failed number="+rows[0]+" no exist!");
                }
                dataline.put(wtpart,rows[1]); 
            }
            
            for(WTPart dPart : dataline.keySet()){
                String value = dataline.get(dPart);
                IBAUtility iba = new IBAUtility(dPart);
                System.out.println(" update "+dPart.getNumber()+",key=oldPartNumber,value="+value);
                iba.setIBAValue("ProductionLine", value);
                iba.updateAttributeContainer(dPart);
                iba.updateIBAHolder(dPart);
                System.out.println(" update "+dPart.getNumber()+",key=ProductionLine,value="+value+" success");                   
            }
         
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
