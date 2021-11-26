package com.catl.common.toolbox.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.epm.EPMDocument;
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

import com.catl.common.util.DocUtil;
import com.catl.loadData.util.ExcelReader;

public class DeleteDrawingPDFByNumber implements RemoteAccess{
   
    public static void main(String[] args) {

        RemoteMethodServer rms = RemoteMethodServer.getDefault();
        GatewayAuthenticator auth = new GatewayAuthenticator();
        auth.setRemoteUser("wcadmin");
        rms.setAuthenticator(auth);

        if(args.length<2){
            System.out.println("Example Usage:" + 
                    "java com.catl.common.toolbox.data.DeleteDrawingPDFByNumber /data/DeleteDrawingPDFByNumber.xlsx -t");
        }
        System.out.println(args[0]+"......"+args[1]);
        invokeRemoteLoad(args[0],args[1]);
    }

    public static void invokeRemoteLoad(String filePath,String command){
        String method = "doLoad";
        String CLASSNAME = DeleteDrawingPDFByNumber.class.getName();
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
        Transaction ts=null;
        try {
            file = new FileWriter("/data/DeleteDrawingPDFByNumber.txt");
            
            BufferedWriter writer = new BufferedWriter(file);
            
            File f = new File(filePath);
            ExcelReader reader = new ExcelReader(f);
            reader.open();
            reader.setSheetNum(0);
            
            int count = reader.getRowCount();
            List<EPMDocument> data = new LinkedList<EPMDocument>();
            for(int i=1; i<=count; i++){
                String[] rows = reader.readExcelLine(i);
                if(rows == null || isEmpty(rows[0])){
                    continue;
                }
                String value = rows[0];
                
                EPMDocument epm = DocUtil.getLastestEPMDocumentByNumber(value);
                if(epm != null && epm.getVersionIdentifier().getValue().equals("A")){
                    data.add(epm);
                }else if(epm != null){
                	writer.write(" failed number="+value+" not A version!");
                }else{
                	System.out.println(" failed number="+value+" no exist!");
                    throw new WTException(" failed number="+value+" no exist!");
                }
            }
            ts = new Transaction();
            ts.start();
            for(EPMDocument epm : data){
                QueryResult qr = ContentHelper.service.getContentsByRole(epm, ContentRoleType.SECONDARY);
                while(qr.hasMoreElements()){
                    ApplicationData fileContent = (ApplicationData) qr.nextElement();
                    if(fileContent != null){
                        if(command.equals("-r")){
                            ContentServerHelper.service.deleteContent(epm,fileContent);
                            writer.write("删除 "+epm.getNumber()+"的附件："+fileContent.getFileName()+" success \n");
                        }else{
                            writer.write("删除 "+epm.getNumber()+"的附件："+fileContent.getFileName()+"\n");
                        }
                    }
                }
            }
            writer.flush();
            writer.close();
            System.out.println("DeleteDrawingPDFByNumber   end...........................");
            
            ts.commit();
        }catch (Exception e) {
            e.printStackTrace();
            if(ts != null)
                ts.rollback();
        }
    }


    public static boolean isEmpty(String str){
        if(str == null || str.trim().equals("") || str.equals("null"))
            return true;
        return false;
    }



}
