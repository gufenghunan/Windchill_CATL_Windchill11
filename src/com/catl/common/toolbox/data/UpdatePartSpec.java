package com.catl.common.toolbox.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;



import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.solr.common.util.Hash;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.httpgw.GatewayAuthenticator;
import wt.iba.value.IBAHolder;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;

import com.catl.common.util.GenericUtil;
import com.catl.common.util.PartUtil;
import com.catl.loadData.Constant;
import com.catl.loadData.IBAUtility;
import com.catl.loadData.StrUtils;
import com.catl.loadData.util.ExcelReader;
import com.catl.loadData.util.ExcelWriter;
import com.catl.test.TestMain;
import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.meta.common.UpdateOperationIdentifier;

public class UpdatePartSpec implements RemoteAccess{
   
	public static String IBA_NAME1 ="specification";
    public static void main(String[] args) {

        RemoteMethodServer rms = RemoteMethodServer.getDefault();
        GatewayAuthenticator auth = new GatewayAuthenticator();
        auth.setRemoteUser("wcadmin");
        rms.setAuthenticator(auth);

        if(args.length<2){
            System.out.println("Example Usage:" + 
                    "java com.catl.common.toolbox.data.UpdatePartSpec /data/UpdatePartSpec.xlsx -t");
        }
        System.out.println(args[0]+"......"+args[1]);
        invokeRemoteLoad(args[0],args[1]);
    }

    public static void invokeRemoteLoad(String filePath,String command){
        String method = "doLoad";
        String CLASSNAME= UpdatePartSpec.class.getName();
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
            file = new FileWriter("/data/UpdatePartSpec.txt");
            
            BufferedWriter writer = new BufferedWriter(file);
            
        	File f = new File(filePath);
            ExcelReader reader = new ExcelReader(f);
            reader.open();
            reader.setSheetNum(0);
            
            int count = reader.getRowCount();
            Map<WTPart,String> data = new LinkedHashMap<WTPart,String>();
            for(int i=1; i<=count; i++){
                String[] rows = reader.readExcelLine(i);
                if(rows == null || isEmpty(rows[0]) || isEmpty(rows[1])){
                	System.out.println("第"+i+"行，为空");
                    writer.write("第"+i+"行，为空"+"\n");
                	continue;
                }
                WTPart wtpart = PartUtil.getLastestWTPartByNumber(rows[0]);
                if (wtpart == null) {
                    System.out.println(" failed number="+rows[0]+" no exist!");
                    throw new WTException(" failed number="+rows[0]+" no exist!");
                }
                data.put(wtpart, rows[1]);
            }
        	for(WTPart dPart : data.keySet()){
        		String spec = data.get(dPart);
        		IBAUtility iba = new IBAUtility(dPart);
    			System.out.println(" update "+dPart.getNumber()+",value="+spec);
            	writer.write(" update "+dPart.getNumber()+",value="+spec+"\n");
            	
            	if(command.equals("-r")){
            	    iba.setIBAValue(IBA_NAME1, spec);
            	    iba.updateAttributeContainer(dPart);
            	    iba.updateIBAHolder(dPart);
            	    System.out.println(" update "+dPart.getNumber()+",value="+spec+" success");
                    writer.write(" update "+dPart.getNumber()+",value="+spec+" success\n");
            	}
        	}
            writer.flush();
            writer.close();
            System.out.println("UpdatePartSpec 导入结束");
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
