package com.catl.common.toolbox.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import wt.doc.WTDocument;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTList;
import wt.httpgw.GatewayAuthenticator;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleTemplateReference;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.util.WTException;
import com.catl.ecad.utils.CommonUtil;
import com.catl.loadData.util.ExcelReader;

public class ReassignLifeCycleTemplate implements RemoteAccess{
   
    public static void main(String[] args) {

        RemoteMethodServer rms = RemoteMethodServer.getDefault();
        GatewayAuthenticator auth = new GatewayAuthenticator();
        auth.setRemoteUser("wcadmin");
        rms.setAuthenticator(auth);

        if(args.length<2){
            System.out.println("Example Usage:" + 
                    "java com.catl.common.toolbox.data.ReassignLifeCycleTemplate /data/ReassignLifeCycleTemplate.xlsx");
        }
        System.out.println(args[0]+"......");
        invokeRemoteLoad(args[0]);
    }

    public static void invokeRemoteLoad(String filePath){
        String method = "doLoad";
        String CLASSNAME= ReassignLifeCycleTemplate.class.getName();
        Class[] types = {String.class};
        Object[] values={filePath};
        try {
            RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void doLoad(String filePath){
    	FileWriter file=null;
    	BufferedWriter writer = null;
        try {
            file = new FileWriter("/data/ReassignLifeCycleTemplate.txt");
            
            writer = new BufferedWriter(file);
            
        	File f = new File(filePath);
            ExcelReader reader = new ExcelReader(f);
            reader.open();
            reader.setSheetNum(0);
            
            int count = reader.getRowCount();
            Map<WTPart,String> data = new LinkedHashMap<WTPart,String>();
            for(int i=1; i<=count; i++){
                String[] rows = reader.readExcelLine(i);
                if(rows == null || isEmpty(rows[0])){
                    writer.write("第"+i+"行，为空"+"\n");
                	continue;
                }
                WTDocument doc = CommonUtil.getLatestWTDocByNumber(rows[0]);
                if (doc == null) {
                    System.out.println(" failed number="+rows[0]+" no exist!");
                    throw new WTException(" failed number="+rows[0]+" no exist!");
                }
                WTList list = new WTArrayList();
                list.add(doc);
                WTContainerRef ref = doc.getContainerReference();
                LifeCycleTemplateReference lctr = null;
                lctr = doc.getLifeCycleTemplate();
                LifeCycleHelper.service.reassign(list, lctr, ref, true, "重新分配生命周期");
            }
        	
            writer.flush();
            writer.close();
        }catch (Exception e) {
        	e.printStackTrace();
        }
        System.out.println("ReassignLifeCycleTemplate 导入结束");
    }

    public static boolean isEmpty(String str){
        if(str == null || str.trim().equals("") || str.equals("null"))
            return true;
        return false;
    }



}
