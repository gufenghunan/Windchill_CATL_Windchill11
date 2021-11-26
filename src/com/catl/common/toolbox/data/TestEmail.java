package com.catl.common.toolbox.data;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.catl.common.util.DocUtil;
import com.catl.common.util.PartUtil;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;

import wt.doc.WTDocument;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.httpgw.GatewayAuthenticator;
import wt.mail.EMailMessage;
import wt.maturity.PromotionNotice;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.query.ArrayExpression;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.config.LatestConfigSpec;

public class TestEmail implements RemoteAccess {
	private static final String CLASSNAME = TestEmail.class.getName();
    private static Logger log =Logger .getLogger(TestEmail.class.getName());
    
    public static void main(String[] args) throws Exception, WTPropertyVetoException {
        RemoteMethodServer rms = RemoteMethodServer.getDefault();
        GatewayAuthenticator auth = new GatewayAuthenticator();
        auth.setRemoteUser("wcadmin");
        rms.setAuthenticator(auth);
        
        String method = "test";
        String refStr = args[0];
        Class[] types = {String.class};
        Object[] values={refStr};
        try {
        	RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, values);
        } catch (Exception e) {
          e.printStackTrace();
        }
    }
    public static void test(String pnNumber) throws IOException, WTPropertyVetoException, WTException{
    	
    	WTDocument doc = DocUtil.getLatestWTDocument(pnNumber);
    	EMailMessage localEMailMessage=EMailMessage.newEMailMessage();
		//localEMailMessage.addEmailAddress(new String[]{"plm-admin@atlbattery.com"});
		//localEMailMessage.setOriginator(doc.getModifier());
		localEMailMessage.addRecipient(doc.getModifier());
		localEMailMessage.setSubject("RDM集成失败：状态更新失败");
		localEMailMessage.addPart("1111111111", "text/plain");
		localEMailMessage.send(true);
    	
    	System.out.println("=====  end=======");
    }
	
}
