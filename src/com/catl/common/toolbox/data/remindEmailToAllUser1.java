package com.catl.common.toolbox.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import wt.httpgw.GatewayAuthenticator;
import wt.mail.EMailMessage;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.OrganizationServicesHelper;
import wt.org.WTPrincipal;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class remindEmailToAllUser1 implements RemoteAccess {
	private static final String CLASSNAME = remindEmailToAllUser1.class.getName();
    
    public static void main(String[] args) throws Exception, WTPropertyVetoException {
        RemoteMethodServer rms = RemoteMethodServer.getDefault();
        GatewayAuthenticator auth = new GatewayAuthenticator();
        auth.setRemoteUser("wcadmin");
        rms.setAuthenticator(auth);
        
        String method = "sendRemindEmail";
        try {
        	RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, null, null);
        } catch (Exception e) {
          e.printStackTrace();
        }
    }
    public static void sendRemindEmail() throws IOException, WTPropertyVetoException, WTException{
    	
    	List<WTPrincipal> userlist = getAllUsers();
    	
    	String title = "PLM系统暂停使用通知！！";
    	
    	String sendMessage = "Dear all："
    			+"\n"
    			+""
    			+"\n"
    			+"		因PLM系统新功能部署，PLM系统将于2017-12-07  20:00-20:30 暂停使用，暂停30分钟！请大家把在线数据保存好，妥善安排时间！"
    			+"\n"
    			+"上线新功能："
    			+"电芯设计表项目上线，实现在PLM系统上进行电芯设计；——EVC/MSD"
    			+"\n"
    			+"如有任何疑问或者问题，请联系PLM组成员，联系电话：17305033593";
        	
    	for (WTPrincipal wtPrincipal : userlist){
    		
    		EMailMessage localEMailMessage=EMailMessage.newEMailMessage();
    		
    		localEMailMessage.addRecipient(wtPrincipal);
    		localEMailMessage.setSubject(title);
    		localEMailMessage.addPart(sendMessage, "text/plain");
    		localEMailMessage.send(true);
    	}
    	
    	System.out.println("=====  end  =======");
    }
	
    static List<WTPrincipal> getAllUsers() throws WTException {
		
    	List<WTPrincipal> userlist = new ArrayList<WTPrincipal>();
    	Enumeration enumPrin = OrganizationServicesHelper.manager.allUsers();

		while (enumPrin.hasMoreElements()) {
			WTPrincipal principal = (WTPrincipal) enumPrin.nextElement();
			userlist.add(principal);
		}
		return userlist;
	}
}
