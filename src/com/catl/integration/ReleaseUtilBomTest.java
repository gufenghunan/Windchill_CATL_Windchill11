package com.catl.integration;

import org.apache.log4j.Logger;

import com.catl.bom.workflow.BomWfUtil;
import com.catl.test.TestMain;

import wt.fc.Persistable;
import wt.httpgw.GatewayAuthenticator;
import wt.maturity.PromotionNotice;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;

public class ReleaseUtilBomTest  implements RemoteAccess{
	
	private static final String CLASSNAME = ReleaseUtilBomTest.class.getName();
	private static Logger log =Logger .getLogger(ReleaseUtilBomTest.class.getName());

	public static void main(String[] args) {
	    RemoteMethodServer rms = RemoteMethodServer.getDefault();
        GatewayAuthenticator auth = new GatewayAuthenticator();
        auth.setRemoteUser("wcadmin");
        rms.setAuthenticator(auth);
        
		String method = "remoteRelease";
        String refStr = args[0];
        String refStr1 = args[1];
        String refStr2 = args[2];
        Class[] types = {String.class,String.class,String.class};
        Object[] values={refStr,refStr1,refStr2};
        try {
         RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, values);
        } catch (Exception e) {
          e.printStackTrace();
        }
	}
	
	public static void remoteRelease(String oidString,String releaseFailedParts,String releaseFailedBoms){
	    log.debug("start remoteRelease......................");
        Persistable persistable = BomWfUtil.getPersistableByOid(oidString);
        
        PromotionNotice pNotice = (PromotionNotice) persistable;
        ErpResponse response = ReleaseUtil.release(pNotice,releaseFailedParts,releaseFailedBoms);
        log.debug("remoteRelease: "+response.isSuccess());
        for(Message message : response.getMessage()){
            log.debug("remoteRelease: 编号:"+message.getNumber()+" 子键:"+message.getChildNumber()+" 替代件:"+message.getStituteNumber()+" 是否成功:"+message.isSuccess()+"  返回消息:"+message.getText());
        }
        log.debug("end remoteRelease......................");
    }
}
