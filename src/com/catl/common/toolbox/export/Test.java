package com.catl.common.toolbox.export;

import java.io.File;

import wt.httpgw.GatewayAuthenticator;
import wt.method.RemoteMethodServer;
import wt.util.WTAttributeNameIfc;
import wt.util.WTProperties;

public class Test {

    
    
    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        RemoteMethodServer rms = RemoteMethodServer.getDefault();
        GatewayAuthenticator auth = new GatewayAuthenticator();
        auth.setRemoteUser("wcadmin");
        rms.setAuthenticator(auth);
        String wtHome = WTProperties.getServerProperties().getProperty("wt.home");
        String csvFilePath = wtHome + File.separator + "loadFiles" + File.separator + "com"+ File.separator +
                 "catl" + File.separator + "preference"+File.separator+"changed.csv";
        System.out.println("csvFilePath=" + csvFilePath);
        //PreferenceExport.doExport(args[0]);
    }

}
