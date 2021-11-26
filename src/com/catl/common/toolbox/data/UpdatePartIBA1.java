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

public class UpdatePartIBA1 implements RemoteAccess{
   
    public static String IBA_NAME1 ="specification";
    public static void main(String[] args) {

        RemoteMethodServer rms = RemoteMethodServer.getDefault();
        GatewayAuthenticator auth = new GatewayAuthenticator();
        auth.setRemoteUser("wcadmin");
        rms.setAuthenticator(auth);

        if(args.length<2){
            System.out.println("Example Usage:" + 
                    "java com.catl.common.toolbox.data.UpdatePartIBA1 /data/UpdatePartIBA_1.xlsx -t");
        }
        System.out.println(args[0]+"......"+args[1]);
        invokeRemoteLoad(args[0],args[1]);
    }

    public static void invokeRemoteLoad(String filePath,String command){
        String method = "doLoad";
        String CLASSNAME = UpdatePartIBA1.class.getName();
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
            file = new FileWriter("/data/UpdatePartIBA.txt");
            
            BufferedWriter writer = new BufferedWriter(file);
            
            File f = new File(filePath);
            ExcelReader reader = new ExcelReader(f);
            reader.open();
            reader.setSheetNum(0);
            
            int count = reader.getRowCount();
            List<String> attrs = new LinkedList<String>();
            Map<WTPart,LinkedHashMap<String,String>> data = new LinkedHashMap<WTPart,LinkedHashMap<String,String>>();
            LinkedHashMap<String,String> map=null;
            for(int i=0; i<=count; i++){
                String[] rows = reader.readExcelLine(i);
                if(rows == null || isEmpty(rows[0])){
                    continue;
                }
                if(rows[0].equals("新编号")){
                    attrs.clear();
                    for(String row : rows){
                            if(!isEmpty(row)){
                                attrs.add(row);
                            }
                    }
                    continue;
                }
                WTPart wtpart = null;
                String oldSpecification = null;
                for(int j = 0; j < attrs.size(); j++){
                    String displayName = attrs.get(j);
                    String value = rows[j];
                    if(j==0){
                    	wtpart = PartUtil.getLastestWTPartByNumber(value);
                        if (wtpart != null) { 
                            oldSpecification = (String) GenericUtil.getObjectAttributeValue(wtpart, IBA_NAME1);
                        }else{
                            System.out.println(" failed number="+value+" no exist!");
                            throw new WTException(" failed number="+value+" no exist!");
                        }
                        if(data.get(wtpart) != null){
                            map = data.get(wtpart);
                            if(map.get(IBA_NAME1) != null)
                                oldSpecification = "_"+map.get(IBA_NAME1)+"_";
                        }else{
                            map = new LinkedHashMap<String,String>();
                        }
                        continue;
                    }
                    String oldDisplayName=displayName;
                    String newDisplayName=displayName;
                    if(displayName.contains("---")){
                        oldDisplayName=displayName.split("---")[0];
                        newDisplayName=displayName.split("---")[1];
                    }
                    String attr = Constant.TYPE_NAME.get(newDisplayName);
                    if(newDisplayName.equals("板厚")){//板厚(mm)是全局属性  目前只有在560220分类上才有板厚属性，但是显示名词为 板厚
                        attr = Constant.TYPE_NAME.get(newDisplayName+"(mm)");
                    }
                    if(isEmpty(attr)){
                        System.out.println(" failed displayName="+newDisplayName+" no exist internal value!");
                        throw new WTException(" failed displayName="+newDisplayName+" no exist internal value!");
                    }
                    map.put(attr, value);
                    String oldValue = oldDisplayName+":"+(String)GenericUtil.getObjectAttributeValue(wtpart, attr);
                    oldSpecification = oldSpecification.replace(oldValue, newDisplayName+":"+value);
                    //Pattern pattern = Pattern.compile("_"+oldDisplayName.replace("(", "\\(").replace(")", "\\)")+":(.*?)_");
                    //Matcher matcher = pattern.matcher(oldSpecification);
                    //oldSpecification= matcher.replaceFirst("_"+newDisplayName+":"+value+ "_");
                    //System.out.println("oldSpecification="+oldSpecification);
                }
                map.put(IBA_NAME1, oldSpecification);
                data.put(wtpart,map); 
            }
            List<String []> list = new ArrayList<String[]>();
            String[]  str=null;
            for(WTPart dPart : data.keySet()){
                Map<String,String> dMap = data.get(dPart);
                IBAUtility iba = new IBAUtility(dPart);
                str = new String[2];
                str[0] = dPart.getNumber();
                for(String key : dMap.keySet()){
                    if(key.equals(IBA_NAME1)){
                        str[1]=dMap.get(key);
                    }
                    System.out.println(" update "+dPart.getNumber()+",key="+key+",value="+dMap.get(key));
                    writer.write(" update "+dPart.getNumber()+",key="+key+",value="+dMap.get(key)+"\n");
                    if(command.equals("-r")){
                        iba.setIBAValue(key, dMap.get(key));
                        iba.updateAttributeContainer(dPart);
                        iba.updateIBAHolder(dPart);
                        System.out.println(" update "+dPart.getNumber()+",key="+key+",value="+dMap.get(key)+" success");
                        writer.write(" update "+dPart.getNumber()+",key="+key+",value="+dMap.get(key)+" success\n");
                    }
                }
                list.add(str);
            }
            writer.flush();
            writer.close();
            ExcelWriter.exportExcelList("/data/updatePartIBA_spec.xlsx", "updatePartIBA_spe", new String[]{"编号","规格"}, list);
            System.out.println("导出 updatePartIBA_spec.xlsx成功");
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
