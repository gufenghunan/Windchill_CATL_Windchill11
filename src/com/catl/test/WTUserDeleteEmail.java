package com.catl.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import wt.httpgw.GatewayAuthenticator;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.OrganizationServicesHelper;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * 更新所有用户邮件地址
 * @author ChenXS
 *
 */
public class WTUserDeleteEmail implements RemoteAccess{

	public static void main(String[] args) throws Exception, WTPropertyVetoException {
        RemoteMethodServer rms = RemoteMethodServer.getDefault();
        GatewayAuthenticator auth = new GatewayAuthenticator();
        auth.setRemoteUser("wcadmin");
        rms.setAuthenticator(auth);
        
        String method = "deleteEmail";
        try {
        	RemoteMethodServer.getDefault().invoke(method, WTUserDeleteEmail.class.getName(), null, null, null);
        } catch (Exception e) {
          e.printStackTrace();
        }
    }

	
	 public static void deleteEmail() throws IOException, WTPropertyVetoException, WTException{
	    	
	    	List<WTUser> userlist = getAllUsers();
	    	for (int i = 0; i < userlist.size(); i++) {
	    		WTUser user = userlist.get(i);
				user.setEMail("");
				OrganizationServicesHelper.manager.updatePrincipal(user);
			}
	    	System.out.println("~~~~~~~~~删除邮件成功");
	 
	 }
	
static List<WTUser> getAllUsers() throws WTException {
		
    	List<WTUser> userlist = new ArrayList<WTUser>();
    	Enumeration enumPrin = OrganizationServicesHelper.manager.allUsers();
		while (enumPrin.hasMoreElements()) {
			WTPrincipal principal = (WTPrincipal) enumPrin.nextElement();
			if (principal instanceof WTUser) {
				WTUser user = (WTUser) principal;
				if (!userlist.contains(user)) {
					userlist.add(user);
				}
			}
		}
		return userlist;
	}
}
