package com.catl.integration;

import org.apache.log4j.Logger;

import com.catl.bom.workflow.BomWfUtil;
import com.catl.common.util.GenericUtil;

import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.collections.WTHashSet;
import wt.fc.collections.WTSet;
import wt.httpgw.GatewayAuthenticator;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;

public class ReleaseUtilDocTest  implements RemoteAccess{
	
	private static final String CLASSNAME = ReleaseUtilDocTest.class.getName();
	private static Logger log =Logger .getLogger(ReleaseUtilDocTest.class.getName());

	public static void main(String[] args) {
	    RemoteMethodServer rms = RemoteMethodServer.getDefault();
        GatewayAuthenticator auth = new GatewayAuthenticator();
        auth.setRemoteUser("wcadmin");
        rms.setAuthenticator(auth);
        
        String method = "remoteReleaseDoc";
        String refStr = args[0];
        Class[] types = {String.class};
        Object[] values={refStr};
        try {
         RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, values);
        } catch (Exception e) {
          e.printStackTrace();
        }
	}
	
	
	public static void remoteReleaseDoc(String oidString){
	    log.debug("start remoteReleaseECN......................");
        Persistable persistable = BomWfUtil.getPersistableByOid(oidString);
        
        WTDocument doc = (WTDocument) persistable;
        ErpResponse response = ReleaseUtil.releaseGerberAndPcbaDoc(doc);
        log.debug("remoteReleaseDoc: "+response.isSuccess());
        for(Message message : response.getMessage()){
            log.debug("remoteReleaseDoc: 编号:"+message.getNumber()+" 是否成功:"+message.isSuccess()+"  返回消息:"+message.getText());
        }
        log.debug("end remoteReleaseDoc......................");
    }
}
