package com.catl.integration;

import org.apache.log4j.Logger;

import com.catl.common.util.GenericUtil;
import com.catl.common.util.PartUtil;

import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTHashSet;
import wt.fc.collections.WTSet;
import wt.httpgw.GatewayAuthenticator;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.query.QuerySpec;
import wt.query.SearchCondition;

public class ReleaseUtilBomDelTest {
	private static final String CLASSNAME = ReleaseUtilBomDelTest.class.getName();
	private static Logger log =Logger .getLogger(ReleaseUtilBomDelTest.class.getName());

	public static void main(String[] args) {
	    RemoteMethodServer rms = RemoteMethodServer.getDefault();
        GatewayAuthenticator auth = new GatewayAuthenticator();
        auth.setRemoteUser("wcadmin");
        rms.setAuthenticator(auth);
      
        
		String method = "deleteChildPartLink";
        WTPart part = (WTPart)GenericUtil.getInstance("VR:wt.part.WTPart:"+args[0]);
        Class[] types = {WTPart.class,String.class,WTSet.class};
        WTSet set = new WTHashSet();
        Object[] values={part,"  ",set};
        try {
         RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, values);
        } catch (Exception e) {
          e.printStackTrace();
        }
	}
	
	public static void deleteChildPartLink(WTPart part,String level,WTSet set){
	    System.out.println(level+part.getNumber());
	    try{
	        QueryResult qr = new QueryResult();
	        QuerySpec queryspec = new QuerySpec(WTPartUsageLink.class);
	        queryspec.appendWhere(new SearchCondition(WTPartUsageLink.class, "roleAObjectRef.key", "=", PersistenceHelper.getObjectIdentifier(part)), new int[] {});
	        qr = PersistenceServerHelper.manager.query(queryspec);
	        while (qr.hasMoreElements()){
	            WTPartUsageLink link = (WTPartUsageLink)qr.nextElement();
	            WTPartMaster master = link.getUses();
	            WTPart child = PartUtil.getLastestWTPartByNumber(master.getNumber());
	            set.add(link);
	            deleteChildPartLink(child,level+"  ",set);
	        }
	        if(level.equals("  ")){
	            PersistenceServerHelper.manager.remove(set);
	        }
	    }catch(Exception e){
	        e.printStackTrace(); 
	    }
	}
}
