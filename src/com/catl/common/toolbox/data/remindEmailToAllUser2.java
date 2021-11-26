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

public class remindEmailToAllUser2 implements RemoteAccess {
	private static final String CLASSNAME = remindEmailToAllUser2.class.getName();
    
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
    			+"		因PLM系统新功能部署，PLM系统将于2018-01-22  20:30-21:00 暂停使用，暂停30分钟！请大家把在线数据保存好，妥善安排时间！"
    			+"\n"
    			+"\n1）新功能发布："
    			+"\n物料发布流程调整，取消发布流程中的“物料控制专员”审核环节；采购类型已经做了名称固化，因此，不再需要PMC审核；"
    			+"\nENW文档发布流程调整，把综合审核中“经理”角色添加至会签环节中，实现两个环节中的审核人员并行审核；"
    			+"\nEVC设计文档命名的提示内容调整，在填写文档名称界面提示最新的命名格式；"
    			+"\n2）Bug修复："
    			+"\nPLM与TRP接口字段调整，去掉传输字段中的单位（mm/V/KWh/Wh/Ah/pcs）；"
    			+"\nP开头物料校对节点必须选择包装工程师审核，并且可以修改P开头物料的BOM结构的放大倍数值；"
    			+"\nPCBA流程优化：当有多个PCBA原理图变更时，校验其原理图关联的PN是否添加进受影响对象；当有多个PCBA原理图发布时，校验其关联的原理图是否已发布。"
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
