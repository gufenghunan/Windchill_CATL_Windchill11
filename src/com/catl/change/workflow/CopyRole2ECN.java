package com.catl.change.workflow;

import java.util.Enumeration;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.httpgw.GatewayAuthenticator;
import wt.method.RemoteMethodServer;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.project.Role;
import wt.team.Team;
import wt.team.TeamManaged;
import wt.util.WTException;
import wt.workflow.engine.WfProcess;

import com.catl.change.report.model.RoleConstant;
import com.catl.common.util.PartUtil;
import com.ptc.netmarkets.workflow.NmWorkflowHelper;

public class CopyRole2ECN {

	private static  Logger log=Logger.getLogger(CopyRole2ECN.class.getName());
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		GatewayAuthenticator auth = new GatewayAuthenticator();
		auth.setRemoteUser("wcadmin");
		rms.setAuthenticator(auth);
		String oid=args[0];
		String oidString="OR:wt.change2.WTChangeOrder2:"+oid;
		Persistable object=PartUtil.getPersistableByOid(oidString);
		WTChangeOrder2 changeOrder2=(WTChangeOrder2)object;
	   
		

	}
	
	
	
  	
}