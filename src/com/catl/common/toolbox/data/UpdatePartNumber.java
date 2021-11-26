package com.catl.common.toolbox.data;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.catl.common.util.PartUtil;
import com.catl.integration.ReleaseUtilDocTest;
import com.catl.loadData.util.ExcelReader;

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

public class UpdatePartNumber implements RemoteAccess{
    private static final String CLASSNAME = UpdatePartNumber.class.getName();
    private static Logger log =Logger .getLogger(UpdatePartNumber.class.getName());
    
    public static void main(String[] args) throws Exception, WTPropertyVetoException {
        RemoteMethodServer rms = RemoteMethodServer.getDefault();
        GatewayAuthenticator auth = new GatewayAuthenticator();
        auth.setRemoteUser("wcadmin");
        rms.setAuthenticator(auth);
        
        String method = "UpdatePartNumberS";
        String refStr = args[0];
        Class[] types = {String.class};
        Object[] values={refStr};
        try {
         RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, values);
        } catch (Exception e) {
          e.printStackTrace();
        }
    }
    public static void UpdatePartNumberS(String str) throws IOException, WTPropertyVetoException, WTException{
        ExcelReader er = new ExcelReader(new File("/data/partNumber_update.xlsx"));
        er.open();
        er.setSheetNum(0);
        int count = er.getRowCount();
        for(int i=1; i<=count; i++){
            String rows[] = er.readExcelLine(i);
            String partNumber = rows[0];
            String number = rows[1];
            WTPart part = PartUtil.getLastestWTPartByNumber(partNumber);
            if(part != null){
                System.out.println("...."+partNumber+"---"+number);
                Identified identified = (Identified) part.getMaster();
                WTPartMasterIdentity identity = (WTPartMasterIdentity)identified.getIdentificationObject();
                identity.setNumber(number);
                IdentityHelper.service.changeIdentity(identified, identity);
            }
        }
    }
}
