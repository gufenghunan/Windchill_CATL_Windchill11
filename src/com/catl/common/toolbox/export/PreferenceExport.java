package com.catl.common.toolbox.export;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.access.AccessControlRule;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.pds.WhereCondition;
import wt.preference.PreferenceDefinition;
import wt.preference.PreferenceHelper;
import wt.preference.PreferenceInstance;
import wt.preference.ThePreferenceDefinitionThePreferenceInstance;

public class PreferenceExport {

    public static void main(String[] args) throws Exception {
        if(args.length<1){
            System.out.println("Usage:");
            System.out.println("windchill com.catl.common.toolbox.export.PreferenceExport   YYYY-MM-DD ");
            System.out.println("For Example");
            System.out.println("windchill com.catl.common.toolbox.export.PreferenceExport   2015-09-20 ");
        }
        doExport(args[0]);
    }
   
    public static void doExport(String modifyTime) {
        // TODO Auto-generated method stub
      
       try {
        QuerySpec querySpec = new QuerySpec();
          // select.getFromClause().setAliasPrefix("A");
         //  select.setAdvancedQueryEnabled(true);
        Class aClass = PreferenceDefinition.class;
        Class bClass = PreferenceInstance.class;
        Class cClass = ThePreferenceDefinitionThePreferenceInstance.class;
    
        Timestamp modifyTimeStamp = getTimeStamp(modifyTime);
        
        
        final String OID = WTAttributeNameIfc.ID_NAME;                        //"thePersistInfo.theObjectIdentifier.id"
        final String ROLEA=WTAttributeNameIfc.ROLEA_OBJECT_ID;//"roleAObjectRef.key.id";
        final String ROLEB=WTAttributeNameIfc.ROLEB_OBJECT_ID ;//"roleBObjectRef.key.id";  
        int  aIndex =  querySpec.addClassList(aClass, true);
        int  bIndex = querySpec.addClassList(bClass, true);
        int  cIndex = querySpec.addClassList(cClass, false);
        
        
        
        
         querySpec.appendWhere(
                 new SearchCondition(aClass,OID,cClass,ROLEA),
                 new int[]{aIndex,cIndex}
         );   
         querySpec.appendAnd();
         querySpec.appendWhere(
                 new SearchCondition(bClass,OID,cClass,ROLEB),
                 new int[]{bIndex,cIndex}
         );   
          querySpec.appendAnd();
           querySpec.appendWhere(
                 new SearchCondition(bClass,PreferenceInstance.MODIFY_TIMESTAMP,  
                         SearchCondition.GREATER_THAN,modifyTimeStamp
                          ),
                 new int[]{bIndex}
         );   
        
         
        QueryResult qr =  PersistenceHelper.manager.find(querySpec);
        
        System.out.println("search =" + querySpec);
        
        int i = 0;
       
        List<String> data = new ArrayList<String>();
        
        
        String wtHome = WTProperties.getServerProperties().getProperty("wt.home");
        String csvFilePath = wtHome + File.separator + "loadFiles" + File.separator + "com"+ File.separator +
                 "catl" + File.separator + "preference"+File.separator+"changed.csv";
        
        System.out.println("csvFilePath=" + csvFilePath);
        FileWriter fw = new FileWriter(csvFilePath);
        BufferedWriter bw = new BufferedWriter(fw);
        while(qr.hasMoreElements()){
            Object[] result= (Object[])qr.nextElement();
            StringBuffer line = new StringBuffer();
            line.append("PreferenceInstance," );
            PreferenceDefinition definition =  (PreferenceDefinition)(result[aIndex]);
            PreferenceInstance instance =  (PreferenceInstance)(result[bIndex]);
            if(instance.getWTUser()==null){
                String defaultValue = definition.getDefaultValue();
                String value = instance.getValue();
                if(defaultValue!=null&&!defaultValue.equals(value)
                        ||value!=null&&!value.equals(defaultValue)
                        ){
                            i++;
                             //~name~containerClass~containerName~organization~parentContainerPath~user~clientName~value~lock~lockOnly
                            line.append(definition.getName()+",");
                            line.append(",");//containerClass
                            line.append(",");//containerName
                            line.append(",");//organization
                            line.append(",");//parentContainerPath
                            line.append(",");//user
                            line.append(",");//clientname
                            if(value!=null){
                                line.append(value+",,");//value
                            }else{
                                line.append(",,,");
                            }
                            bw.write(line.toString());
                            System.out.println(line);
                            bw.write("\n");
                          
                         }//if value is different
                
            }//if user =null
         }//while 
        bw.flush();
        bw.close();
        fw.close();
  
        
           
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
     
       
       
    }
    
    
    public static Timestamp getTimeStamp(String dateStr) {
        Timestamp timestamp = null;
        String dateStartStr = "";
        if (dateStr != null && !dateStr.equals("")) {
            dateStartStr = dateStr.replace('\\', '-');
            dateStartStr = dateStartStr.replace('/', '-');
            dateStartStr = dateStartStr + " 00:00:00.000000001";
            timestamp = Timestamp.valueOf(dateStartStr);
          //  timestamp = fixTime(timestamp);
        }
        return timestamp;
    }
    
    public static Timestamp fixTime(Timestamp timestamp) {
         timestamp = new Timestamp(timestamp.getTime() + (timestamp.getTimezoneOffset()) * 60 * 1000);
        return timestamp;
    }  

}
