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

public class remindEmailToAllUser0113 implements RemoteAccess {
	private static final String CLASSNAME = remindEmailToAllUser0113.class.getName();
    
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
    			+"		因PLM系统新功能部署，PLM系统将于2018-1-13  12:30-13:00 暂停使用，暂停30分钟！请大家把在线数据保存好，妥善安排时间！"
    			+"\n"
    			+"上线新功能："
    			+"EVC设计模版可配置化：新增计算模版可配置功能，通过由指定的EVC工程师线下修改计算模版，并长传更新来配置设计模版"
    			+"\n"
    			+" 解决材料PN重复问题，调整原来的EVC材料编号方案，避免了因PN重复不能创建部件的问题"
    			+"\n"
    			+"增加创建售后再利用件"
    			+"\n"
    			+"增加发布流程校对环节维护放大倍数"
    			+"\n"
    			+"流程审核节点逻辑调整"
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
