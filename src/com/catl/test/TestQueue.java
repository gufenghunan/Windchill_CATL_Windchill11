package com.catl.test;   
  
import java.text.SimpleDateFormat;   
import java.util.Date;   
import java.util.TimeZone;   
  
import wt.admin.AdministrativeDomainHelper;   
import wt.method.MethodContext;   
import wt.session.SessionAuthenticator;   
  
  
  
public class TestQueue {   
       
      
    public static void testQueue(){   
        TimeZone zone = TimeZone.getTimeZone("GMT+8:00");   
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");   
        sdf.setTimeZone(zone);   
        String currentTime = sdf.format( new Date());   
        System.out.println("测试计划执行队列,当前时间：" + currentTime );   
        //在这里编写处理业务相关的代码，如果操作Windchill持久化对象时出现"找不到活动方法的上下文"错误时，可增加以下代码进行处理   
        MethodContext mc = MethodContext.getContext(Thread.currentThread());   
        if (mc == null)   
            mc = new MethodContext(null, null);   
        if (mc.getAuthentication() == null) {   
            SessionAuthenticator sa = new SessionAuthenticator();   
            mc.setAuthentication(sa.setUserName(AdministrativeDomainHelper.ADMINISTRATOR_NAME));   
        }   
    }   
}