package com.catl.common.toolbox.data;

import com.catl.job.CATLSpringJob;

/**
 * <p>
 * Description:
 * </p>
 * 
 * @author:
 * @time: Oct 29, 2010 11:29:46 PM
 * @version 1.0
 */

public class DocumentInvalidQueue {

	/**
	 * 计划队列执行时调用的方法 11:49:00 PM
	 * @throws Exception 
	 */
	public static void documentInvalidQueue() throws Exception {
//		TimeZone zone = TimeZone.getTimeZone("GMT+8:00");
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//		sdf.setTimeZone(zone);
		CATLSpringJob.doLoad();
//		System.out.println("测试计划执行队列,当前时间：" + currentTime);
		// 在这里编写处理业务相关的代码，如果操作Windchill持久化对象时出现"找不到活动方法的上下文"错误时，可增加以下代码进行处理
//		MethodContext mc = MethodContext.getContext(Thread.currentThread());
//		if (mc == null)
//			mc = new MethodContext(null, null);
//		if (mc.getAuthentication() == null) {
//			SessionAuthenticator sa = new SessionAuthenticator();
//			mc.setAuthentication(sa
//					.setUserName(AdministrativeDomainHelper.ADMINISTRATOR_NAME));
//		}
	}
}