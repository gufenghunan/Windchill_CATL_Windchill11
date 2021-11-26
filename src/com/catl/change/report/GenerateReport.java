package com.catl.change.report;

import wt.change2.ChangeHelper2;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.httpgw.GatewayAuthenticator;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;

import com.catl.common.toolbox.data.UpdatePartIBA;
import com.catl.common.util.PartUtil;

public class GenerateReport implements RemoteAccess{

    public static void main(String[] args) {
       
        RemoteMethodServer rms = RemoteMethodServer.getDefault();
        GatewayAuthenticator auth = new GatewayAuthenticator();
        auth.setRemoteUser("wcadmin");
        rms.setAuthenticator(auth);
        invokeGenerateReport();       
    }
    
    public static void invokeGenerateReport(){
        String method = "doGenerateReport";
        String CLASSNAME = CLASSNAME = GenerateReport.class.getName();
        Class[] types = {String.class};
        Object[] values={"report"};
        try {
            RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void doGenerateReport(String a){
         
        QuerySpec query;
        try {
            query = new QuerySpec(WTChangeRequest2.class);
            QueryResult qr = PersistenceHelper.manager.find(query); 
            String processVar = "";
            boolean littlechange=false;
            boolean normalchange=false;        
            boolean bigchange =false;
            String  changetype="";
            while (qr.hasMoreElements()) { 

                WTChangeRequest2 ecr=(WTChangeRequest2)qr.nextElement();
                QueryResult processes = WfEngineHelper.service.getAssociatedProcesses(ecr, null, null);
                while (processes.hasMoreElements()) {
                    WfProcess process = (WfProcess) processes.nextElement();
                    ProcessData data = process.getContext();
                    if (data != null) {
                        littlechange = (Boolean) data.getValue("littlechange");
                        normalchange = (Boolean) data.getValue("littlechange");
                        bigchange = (Boolean) data.getValue("littlechange");  

                    }
                }

                if(littlechange)
                {
                    changetype="微小变更";
                }
                if(normalchange)
                {
                    changetype="一般变更";
                }
                if(bigchange)
                {
                    changetype="重大变更";
                }
                String ecrState = ecr.getState().toString();
                System.out.println("ECR name=" + ecr.getName() + " ,state = " + ecrState);
                if(ecrState.equals("RESOLVED")||ecrState.equals("IMPLEMENTATION")){
                    System.out.println("Generate Report for " + ecr.getName() + " , change type=" + changetype);
                    com.catl.change.report.ecr.ECRAttachmentHtml.doOperation(ecr,changetype);
                }
                QueryResult ecos = ChangeHelper2.service.getChangeOrders(ecr);
                while(ecos.hasMoreElements()){
                    WTChangeOrder2 eco = (WTChangeOrder2)ecos.nextElement();
                    QueryResult ecoProcesses = WfEngineHelper.service.getAssociatedProcesses(eco, null, null);
                    while (ecoProcesses.hasMoreElements()) {
                        WfProcess ecoProcess = (WfProcess) ecoProcesses.nextElement();
                        ObjectReference self = ObjectReference.newObjectReference(ecoProcess);
                        String ecnState = eco.getState().toString();
                        System.out.println("ECN state = " + ecnState);
                        if(ecnState.equals("RESOLVED")||ecnState.equals("IMPLEMENTATION")){
                            System.out.println("Generate Report for " + eco.getName());
                            com.catl.change.report.ecn.ECNAttachmentHtml.doCreateECNHtmlReport(eco,self);
                        }
                        
                    }
                }
                
                
                
                
            }
        } catch (QueryException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (WTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }     
      
        
    }
    

}
