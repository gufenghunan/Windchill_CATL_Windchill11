package com.catl.common.toolbox.data;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.tools.ant.types.CommandlineJava.SysProperties;

import com.catl.common.util.DocUtil;
import com.catl.common.util.IBAUtil;
import com.catl.common.util.PartUtil;
import com.catl.part.PartConstant;
import com.catl.part.classification.RefreshFAEStatusUtil;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;

import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
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

public class Test implements RemoteAccess {
	private static final String CLASSNAME = Test.class.getName();
    private static Logger log =Logger .getLogger(Test.class.getName());
    
    public static void main(String[] args) throws Exception, WTPropertyVetoException {
        RemoteMethodServer rms = RemoteMethodServer.getDefault();
        GatewayAuthenticator auth = new GatewayAuthenticator();
        auth.setRemoteUser("wcadmin");
        rms.setAuthenticator(auth);
        
        String method = "sendSAP";
        String refStr = args[0];
        Class[] types = {String.class};
        Object[] values={refStr};
        try {
        	RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, values);
        } catch (Exception e) {
          e.printStackTrace();
        }
    }
    public static void sendSAP(String pnNumber) throws IOException, WTPropertyVetoException, WTException{
    	System.out.println("1234");
    	
    	WTPart part = PartUtil.getLastestWTPartByNumber(pnNumber);
    	
    	String sourceAfter = "customer";
		String faeStatus = "不需要";
		part.setSource(Source.toSource(sourceAfter));
		
		Persistable p = IBAUtil.setIBAVaue(part.getMaster(), PartConstant.IBA_CATL_FAEStatus, faeStatus);
		PersistenceServerHelper.manager.update(part);
    	
    	/*WTPartConfigSpec configSpec = (WTPartConfigSpec) ConfigHelper.service.getConfigSpecFor(part);
		QueryResult qr = WTPartHelper.service.getUsesWTParts(part, configSpec);
		while (qr.hasMoreElements()) {

			Persistable[] aSubNodePair = (Persistable[]) qr.nextElement();
			WTPartUsageLink usageLink = (WTPartUsageLink) aSubNodePair[0];

			// List<String> substituteList = getSubstitutePart(usageLink);
			String magnification = (Long)IBAUtil.getIBAValue(usageLink, "CATL_MAGNIFICATION")+"";
			System.out.println(magnification);
		}*/
    	/*
    	
    	QueryResult qr1 = PersistenceHelper.manager.navigate(part1.getMaster(), WTPartUsageLink.USED_BY_ROLE,WTPartUsageLink.class, false);
    	while(qr1.hasMoreElements()){
    		WTPartUsageLink link = (WTPartUsageLink)qr1.nextElement();
    		System.out.println(link.getUses().getNumber());
    		System.out.println(link.getUsedBy().getNumber());
    	}
    	
		WTPart part = PartUtil.getLastestWTPartByNumber("500101-00054");
		System.out.println(part.getVersionIdentifier().getValue()+"."+part.getIterationIdentifier().getValue());

		QueryResult qr = VersionControlHelper.service.allVersionsOf(part.getMaster());
		part = (WTPart)qr.nextElement();
		System.out.println(part.getVersionIdentifier().getValue()+"."+part.getIterationIdentifier().getValue());
		part = (WTPart)qr.nextElement();
		System.out.println(part.getVersionIdentifier().getValue()+"."+part.getIterationIdentifier().getValue());
		
		part = (WTPart) VersionControlHelper.getLatestIteration((Iterated) part, false);
		System.out.println(part.getVersionIdentifier().getValue()+"."+part.getIterationIdentifier().getValue());
		
    	*/
    	System.out.println("=====sendSAP  end=======");
    }
	
}
