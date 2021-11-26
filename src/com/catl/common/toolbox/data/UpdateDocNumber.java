package com.catl.common.toolbox.data;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.catl.common.util.DocUtil;
import com.catl.common.util.PartUtil;
import com.catl.integration.ReleaseUtilDocTest;
import com.catl.loadData.util.ExcelReader;

import wt.doc.WTDocument;
import wt.doc.WTDocumentMasterIdentity;
import wt.fc.Identified;
import wt.fc.IdentityHelper;
import wt.fc.PersistenceHelper;
import wt.httpgw.GatewayAuthenticator;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartMasterIdentity;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class UpdateDocNumber implements RemoteAccess{
    private static final String CLASSNAME = UpdateDocNumber.class.getName();
    private static Logger log =Logger .getLogger(UpdateDocNumber.class.getName());
    
    public static void main(String[] args) throws Exception, WTPropertyVetoException {
        RemoteMethodServer rms = RemoteMethodServer.getDefault();
        GatewayAuthenticator auth = new GatewayAuthenticator();
        auth.setRemoteUser("wcadmin");
        rms.setAuthenticator(auth);
        
        String method = "UpdateDocNumber";
        String refStr = args[0];
        Class[] types = {String.class};
        Object[] values={refStr};
        try {
         RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, values);
        } catch (Exception e) {
          e.printStackTrace();
        }
    }
    public static void UpdateDocNumber(String str) throws IOException, WTPropertyVetoException, WTException{
        ExcelReader er = new ExcelReader(new File(str));
        er.open();
        er.setSheetNum(0);
        int count = er.getRowCount();
        for(int i=1; i<=count; i++){
            String rows[] = er.readExcelLine(i);
            String docNumberOld = rows[0];
            String docNumberNew = rows[1];
            WTDocument doc = DocUtil.getLatestWTDocument(docNumberOld);
            if(doc != null){
                System.out.println("...."+docNumberOld+"---"+docNumberNew);
                Identified identified = (Identified) doc.getMaster();
                WTDocumentMasterIdentity identity = (WTDocumentMasterIdentity)identified.getIdentificationObject();
                identity.setNumber(docNumberNew);
                IdentityHelper.service.changeIdentity(identified, identity);
            }
        }
    }
}
