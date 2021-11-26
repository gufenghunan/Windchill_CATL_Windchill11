package com.catl.common.toolbox.data;

import java.io.IOException;
import java.util.Set;

import org.apache.log4j.Logger;

import com.catl.common.util.PartUtil;
import com.ptc.netmarkets.workflow.NmWorkflowHelper;

import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.httpgw.GatewayAuthenticator;
import wt.maturity.PromotionNotice;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.query.ArrayExpression;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTMessage;
import wt.util.WTPropertyVetoException;
import wt.workflow.engine.WfProcess;

public class QueryPartByOldNumber implements RemoteAccess {
	private static final String CLASSNAME = QueryPartByOldNumber.class.getName();
	private static Logger log = Logger.getLogger(QueryPartByOldNumber.class.getName());

	public static void main(String[] args) throws Exception, WTPropertyVetoException {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		GatewayAuthenticator auth = new GatewayAuthenticator();
		auth.setRemoteUser("wcadmin");
		rms.setAuthenticator(auth);

		String method = "method";
		String refStr = args[0];
		String refStr2 = args[1];
		Class[] types = { String.class,String.class };
		Object[] values = { refStr,refStr2 };
		try {
			RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void method(String pnNumber,String oldNumber) throws IOException, WTPropertyVetoException, WTException {

		System.out.println("=====method  start=======");
		Set<WTPart> set = PartUtil.getLastedPartByStringIBAValue(pnNumber, "oldPartNumber", oldNumber);
		System.out.println("...................="+set.size());
		System.out.println("=====method  end=======");
	}

}
