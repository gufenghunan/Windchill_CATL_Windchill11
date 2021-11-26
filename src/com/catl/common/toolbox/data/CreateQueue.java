package com.catl.common.toolbox.data;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

import wt.org.OrganizationServicesHelper;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.scheduler.ScheduleItem;
import wt.scheduler.SchedulingHelper;
import wt.util.WTException;

public class CreateQueue {

	public static void main(String args[]) throws WTException {
		createScheduleQueue();
	}

	/**
	 * 创建计划执行队列，只需要执行一次即可，以后当windchill服务启动则按照设定的时间定时执行任务 11:25:38 PM
	 * 
	 * @throws WTException
	 */
	public static void createScheduleQueue() throws WTException {
		System.out.println("开始创建计划执行队列... ...");
		// 创建计划队列对象
		ScheduleItem si = ScheduleItem.newScheduleItem();
		// 设置该队列的描述信息
		si.setItemDescription("测试计划执行队列");
		// 设置队列的名称，一般以英文命名
		si.setQueueName("DocumentInvalidScheduleItem");
		// 设置要执行的类和方法
		// 类名
		si.setTargetClass(DocumentInvalidQueue.class.getName());
		// 方法名
		si.setTargetMethod("documentInvalidQueue");
		si.setToBeRun(-1l);
		// 设置开始时间,在这里我设置为队列创建后立即执行
		Date nextDay = getNextDay();
		Timestamp timestamp = new Timestamp(nextDay.getYear(), nextDay.getMonth(), nextDay.getDate()+1, 1, 00, 0, 0); // 这四个参数依次为小时，分，秒，毫秒
		si.setStartDate(timestamp);
		// 设置执行周期,这里设置为每隔一天执行一次
		si.setPeriodicity(86400);
		// 设置执行该任务的用户,如果是操作Windchill对象的话会牵扯到权限控制,这里设置为管理员执行
		WTUser administrator = getUserFromName("Administrator");
		WTPrincipalReference p = null;
		si.setPrincipalRef(WTPrincipalReference
				.newWTPrincipalReference(administrator));
		// 最后，将计划任务对象添加到队列
		si = SchedulingHelper.service.addItem(si, null);
		System.out.println("计划执行队列创建完毕... ..." + "'启动时间为'" + si.getStartDate()
				+ "'，间隔为'" + si.getPeriodicity() + "'!");
	}

	/**
	 * 根据用户名得到用户
	 * 
	 * @param name
	 *            用户名
	 * @throws WTException
	 *             return WTUser
	 */
	public static WTUser getUserFromName(String name) throws WTException {
		Enumeration enumUser = OrganizationServicesHelper.manager.findUser(WTUser.NAME, name);
		WTUser user = null;
		if (enumUser.hasMoreElements())
			user = (WTUser) enumUser.nextElement();

		if (user == null) {
			enumUser = OrganizationServicesHelper.manager.findUser(WTUser.FULL_NAME, name);
			if (enumUser.hasMoreElements())
				user = (WTUser) enumUser.nextElement();
		}

		if (user == null) {
			throw new WTException("系统中不存在用户名为'" + name + "'的用户！");
		}

		return user;
	}

	public static Date getNextDay() {
		
		Date dNow = new Date();   //当前时间
		Date dNext = new Date();

		Calendar calendar = Calendar.getInstance(); //得到日历
		calendar.setTime(dNow);//把当前时间赋给日历
		calendar.add(Calendar.DAY_OF_MONTH, 1);  //设置为后一天
		dNext = calendar.getTime();   //得到后一天的时间
		
		return dNext;
	}
}