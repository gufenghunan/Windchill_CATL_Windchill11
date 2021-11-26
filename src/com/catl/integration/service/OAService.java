package com.catl.integration.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.log4j.Logger;





import com.catl.test.TestMain;

import wt.fc.QueryResult;
import wt.httpgw.GatewayAuthenticator;
import wt.method.MethodContext;
import wt.method.RemoteMethodServer;
import wt.org.OrganizationServicesHelper;
import wt.org.WTUser;
import wt.pom.WTConnection;
import wt.util.WTException;
import wt.workflow.work.WorkflowHelper;


@WebService
public class OAService {
	private static Logger log =Logger.getLogger(OAService.class.getName());
	@WebMethod
	public String getTaskNum(String username){
        
	    /*String taskNum = null;
	    log.debug("username:"+username);
        WTUser user = null;
        try {
                 user = OrganizationServicesHelper.manager.getAuthenticatedUser(username);
                 if(user==null){
                     log.debug("WARN: User " + username +  " is not existed in the System ");         
                 }
                 QueryResult result = WorkflowHelper.service.getUncompletedWorkItems(user);
                 log.debug("username:"+username+",taskNum="+result.size());
                 taskNum=result.size()+"";
                
        } catch (WTException e) {
                 e.printStackTrace();
        }
		return taskNum;*/
	    
		MethodContext context = MethodContext.getContext();
		WTConnection wtConn = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String taskNum = null;
		try{
			
			wtConn = (WTConnection)context.getConnection();
			String sql = "select count(*) taskNum from workitem w left join wtuser u on w.ida3a2ownership=u.ida2a2 where w.status = 'POTENTIAL' and u.name = ? ";
			
			statement = wtConn.prepareStatement(sql);
			statement.setString(1, username);
			
			resultSet = statement.executeQuery();
			while(resultSet.next()){
				taskNum = resultSet.getString("taskNum");
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(resultSet != null) resultSet.close();
				if(statement != null) statement.close();
				if(wtConn != null && wtConn.isActive()) wtConn.release();
			}catch(Exception e){
				log.error("",e);
			}
			
		}
		System.out.println("sql taskNum="+taskNum);
		return taskNum;
	}
}
