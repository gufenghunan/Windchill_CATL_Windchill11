package com.catl.common.toolbox.data;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.tools.ant.types.CommandlineJava.SysProperties;

import com.catl.common.util.DocUtil;
import com.catl.common.util.IBAUtil;
import com.catl.common.util.PartUtil;
import com.catl.integration.ErpResponse;
import com.catl.integration.Message;
import com.catl.part.PartConstant;
import com.catl.part.classification.RefreshFAEStatusUtil;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;

import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.httpgw.GatewayAuthenticator;
import wt.maturity.PromotionNotice;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.Source;
import wt.part.WTPart;
import wt.part.WTPartConfigSpec;
import wt.part.WTPartHelper;
import wt.part.WTPartUsageLink;
import wt.query.ArrayExpression;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.Iterated;
import wt.vc.VersionControlHelper;
import wt.vc.config.ConfigHelper;
import wt.vc.config.LatestConfigSpec;

public class TestSendERP implements RemoteAccess {
	private static final String CLASSNAME = TestSendERP.class.getName();
    private static Logger log =Logger .getLogger(TestSendERP.class.getName());
    
    public static void main(String[] args) throws Exception, WTPropertyVetoException {
        RemoteMethodServer rms = RemoteMethodServer.getDefault();
        GatewayAuthenticator auth = new GatewayAuthenticator();
        auth.setRemoteUser("wcadmin");
        rms.setAuthenticator(auth);
        
        String method = args[0];
        Class[] types = {String.class,String.class,String.class};
        Object[] values={args[1],args[2],args[3]};
        try {
        	RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, values);
        } catch (Exception e) {
          e.printStackTrace();
        }
    }
    public static void release(String pnNumber, String releaseSuccessParts, String releaseSuccessBoms) throws IOException, WTPropertyVetoException, WTException{
    	System.out.println("=====release  start=======");
    	QuerySpec qs = new QuerySpec(PromotionNotice.class);
    	qs.appendWhere(new SearchCondition(new ClassAttribute(PromotionNotice.class, PromotionNotice.NUMBER),SearchCondition.IN,new ArrayExpression(new String[]{pnNumber})), new int[]{0});
    	
    	QueryResult qr = PersistenceServerHelper.manager.query(qs);
    	
    	while(qr.hasMoreElements()){ 
    		PromotionNotice pn = (PromotionNotice)qr.nextElement();
    		ErpResponse response = com.catl.integration.ReleaseUtil.release(pn, releaseSuccessParts, releaseSuccessBoms);
    		System.out.println("release result="+response.isSuccess());
    		if(!response.isSuccess()){
    			for(Message msg : response.getMessage()){
    				System.out.println("number:"+msg.getNumber()+",childNumber:"+msg.getChildNumber()+",subNumber:"+msg.getStituteNumber()
    						+",ecnNumber:"+msg.getEcnNumber()+",msg:"+msg.getText());
    			}
    		}
    	}
    	
    	System.out.println("=====release  end=======");
    }
	
}
