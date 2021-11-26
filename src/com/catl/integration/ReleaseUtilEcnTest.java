package com.catl.integration;

import org.apache.log4j.Logger;

import com.catl.bom.workflow.BomWfUtil;

import wt.change2.WTChangeOrder2;
import wt.fc.Persistable;
import wt.httpgw.GatewayAuthenticator;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;

public class ReleaseUtilEcnTest  implements RemoteAccess{
	
	private static final String CLASSNAME = ReleaseUtilEcnTest.class.getName();
	private static Logger log =Logger .getLogger(ReleaseUtilEcnTest.class.getName());

	public static void main(String[] args) {
	    RemoteMethodServer rms = RemoteMethodServer.getDefault();
        GatewayAuthenticator auth = new GatewayAuthenticator();
        auth.setRemoteUser("wcadmin");
        rms.setAuthenticator(auth);
        
		String method = "remoteReleaseECN";
        String refStr = args[0];
        Class[] types = {String.class};
        Object[] values={refStr};
        try {
         RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, values);
        } catch (Exception e) {
          e.printStackTrace();
        } 
	}
	
	public static void remoteReleaseECN(String oidString){
	    log.debug("start remoteReleaseECN......................");
        Persistable persistable = BomWfUtil.getPersistableByOid(oidString);
        
        WTChangeOrder2 change = (WTChangeOrder2) persistable;
        ErpResponse response = ReleaseUtil.releaseECN(change);
        log.debug("remoteReleaseECN: "+response.isSuccess());
        for(Message message : response.getMessage()){
            log.debug("remoteReleaseECN: 编号:"+message.getNumber()+" 是否成功:"+message.isSuccess()+"  返回消息:"+message.getText());
        }
        log.debug("end remoteReleaseECN......................");
    }
}
