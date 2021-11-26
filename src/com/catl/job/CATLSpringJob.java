package com.catl.job;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import org.json.JSONException;
import org.apache.log4j.Logger;

import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.WTReference;
import wt.httpgw.GatewayAuthenticator;
import wt.httpgw.URLFactory;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.State;
import wt.mail.EMailMessage;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.pom.PersistenceException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.VersionControlHelper;

import com.catl.common.constant.RoleName;
import com.catl.common.util.CatlConstant;
import com.catl.common.util.GenericUtil;
import com.catl.doc.DocInvalidUtil;
import com.catl.ecad.utils.ECADConst;
import com.catl.ecad.utils.ECADPartUtils;
import com.catl.ecad.utils.ECADutil;
import com.catl.ecad.utils.WorkflowHelper;

public class CATLSpringJob implements RemoteAccess {

	public static String DATE_FORMATE1 = "yyyy-MM-dd HH:mm:ss";
	public static String DATE_FORMATE2 = "yyyy-MM-dd";
	private static Logger log = Logger.getLogger(CATLSpringJob.class.getName());

	/**
	 * 每天凌晨1点更新ENW失效的文档
	 */
	public void updateDocumentVoidTime() {

		try {
			RemoteMethodServer rms = RemoteMethodServer.getDefault();
			GatewayAuthenticator auth = new GatewayAuthenticator();
			auth.setRemoteUser("wcadmin");
			rms.setAuthenticator(auth);

			RemoteMethodServer.getDefault().invoke("doLoad", CATLSpringJob.class.getName(), null, null, null);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		RemoteMethodServer rm = RemoteMethodServer.getDefault();
		rm.setUserName("wcadmin");
		rm.setPassword("wcadmin");
		rm.invoke("Test", CATLSpringJob.class.getName(), null, null, null); // 远程调用
	}

	/**
	 * 远程调用测试方法
	 * 
	 * @throws Exception
	 */
	public static void Test() throws Exception {
		List<WTUser> users = new ArrayList<WTUser>();
		WTGroup group = getECADGroup();
		getGroupMemberUsers(group, users);
		sendMail_SCH(users);
	}

	public static void doLoad() throws Exception {

		try {
			String queryTime = getBeforeDay(DATE_FORMATE2) + " 7:59:59";
			Vector<WTDocument> doclist = DocInvalidUtil.getENWDoc(queryTime);
			for (WTDocument doc : doclist) {
				if (GenericUtil.getObjectAttributeValue(doc, CatlConstant.CATL_DOC_ACTUALVOIDTIME) == null) {
					DocInvalidUtil.updateDoc1(doc);
					// //设置生命周期状态为“失效”
					// LifeCycleHelper.service.setLifeCycleState(doc,
					// State.toState("INVALID"));
					//
					// doc =
					// (WTDocument)PersistenceServerHelper.manager.restore(doc);
					//
					// //添加实际失效日期
					// DocInvalidUtil.setActualvoidtime(doc,
					// getBeforeDay(DATE_FORMATE1));

				}
			}
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getTodayLastedTime() {

		String dateStr = "";
		Date date = new Date();
		// format的格式可以任意
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			dateStr = sdf.format(date) + " 7:59:59";
			System.out.println(dateStr);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return dateStr;
	}

	public static String getBeforeDay(String format) {

		Date dNow = new Date(); // 当前时间
		Date dBefore = new Date();

		Calendar calendar = Calendar.getInstance(); // 得到日历
		calendar.setTime(dNow);// 把当前时间赋给日历
		calendar.add(Calendar.DAY_OF_MONTH, -1); // 设置为前一天
		dBefore = calendar.getTime(); // 得到前一天的时间

		SimpleDateFormat sdf = new SimpleDateFormat(format); // 设置时间格式
		String defaultStartDate = sdf.format(dBefore); // 格式化前一天

		return defaultStartDate;
	}

	/**
	 * 原理图检入时发送邮件通知
	 * 
	 * @param schepm
	 * @param s
	 * @throws WTException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws JSONException
	 */
	public static void sendMail_SCH(List<WTUser> s)
			throws WTException, FileNotFoundException, IOException, JSONException {
		String queryTime = getBeforeDay(DATE_FORMATE2) + " 7:59:59";
		List<WTDocument> list = DocInvalidUtil.getENWDoc(queryTime);
		sendMailTo(list, s, "文档更新通知");
		log.debug("************** END SEND MAIL NOTICE ************** ");

	}

	public static void sendMailTo(List<WTDocument> pList, List<WTUser> users, String title)
			throws WTException, FileNotFoundException, IOException, JSONException {
		String mailContent;

		StringBuffer stringbuffer = new StringBuffer();
		stringbuffer.append(title);
		stringbuffer.append("<html>");
		stringbuffer.append("<head>");
		stringbuffer.append("</head>");
		stringbuffer.append("<body>");
		stringbuffer.append("文档已更新\n");
		stringbuffer
				.append("<table bordercolor='#000000' style='width:100%;' border='1' cellspacing='0' cellpadding='2'>");

		stringbuffer.append(" <tr>");
		stringbuffer.append("<td>");
		stringbuffer.append("<b>编号</b>");
		stringbuffer.append("</td>");
		stringbuffer.append("<td>");
		stringbuffer.append("<b>版本</b>");
		stringbuffer.append("</td>");
		stringbuffer.append("<td>");
		stringbuffer.append("<b>名称</b>");
		stringbuffer.append("</td>");
		stringbuffer.append("<td>");
		stringbuffer.append("<b>修改者</b>");
		stringbuffer.append("</td>");
		stringbuffer.append(" </tr>");

		if (pList != null) {
			for (WTDocument epm : pList) {
				String docNumber = "";
				String docName = "";
				String version = "";
				// String title = "";
				String subjectObjURL = "";
				docNumber = epm.getNumber();
				docName = epm.getName();
				version = VersionControlHelper.getIterationDisplayIdentifier(epm).toString();
				subjectObjURL = getURLByDocument(epm);

				stringbuffer.append(" <tr>");
				stringbuffer.append("<td>文档:<a href=" + subjectObjURL + ">" + docNumber + "<br /></td>");
				stringbuffer.append("<td>" + version + "<br /></td>");
				stringbuffer.append("<td>" + docName + "<br /></td>");
				stringbuffer.append("<td>" + epm.getModifierFullName() + "<br /></td>");
				stringbuffer.append(" </tr>");
			}
		}

		stringbuffer.append("</table>");
		stringbuffer.append("</body>");
		stringbuffer.append("</html>");
		mailContent = stringbuffer.toString();

		log.debug("************** \n STATR SEND mailContent:" + mailContent);
		try {

			EMailMessage localEMailMessage = EMailMessage.newEMailMessage();
			for (WTUser user : users) {
				localEMailMessage.addRecipient(user);
			}
			// String[] addresses = {"alm-gw01@catlbattery.local"};
			localEMailMessage.setSubject(title);
			// localEMailMessage.addEmailAddress(addresses);
			localEMailMessage.addPart(mailContent, "text/html");
			localEMailMessage.send(true);
		} catch (WTException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void sendMailToDesigner(List<WTDocument> pList, String title)
			throws WTException, FileNotFoundException, IOException, JSONException {
		if (pList != null) {
			for (WTDocument epm : pList) {
				String mailContent;

				StringBuffer stringbuffer = new StringBuffer();
				stringbuffer.append(title);
				stringbuffer.append("<html>");
				stringbuffer.append("<head>");
				stringbuffer.append("</head>");
				stringbuffer.append("<body>");
				stringbuffer.append("文档已更新\n");
				stringbuffer.append(
						"<table bordercolor='#000000' style='width:100%;' border='1' cellspacing='0' cellpadding='2'>");

				stringbuffer.append(" <tr>");
				stringbuffer.append("<td>");
				stringbuffer.append("<b>编号</b>");
				stringbuffer.append("</td>");
				stringbuffer.append("<td>");
				stringbuffer.append("<b>版本</b>");
				stringbuffer.append("</td>");
				stringbuffer.append("<td>");
				stringbuffer.append("<b>名称</b>");
				stringbuffer.append("</td>");
				stringbuffer.append("<td>");
				stringbuffer.append("<b>修改者</b>");
				stringbuffer.append("</td>");
				stringbuffer.append(" </tr>");

				String docNumber = "";
				String docName = "";
				String version = "";
				// String title = "";
				String subjectObjURL = "";
				docNumber = epm.getNumber();
				docName = epm.getName();
				version = VersionControlHelper.getIterationDisplayIdentifier(epm).toString();
				subjectObjURL = getURLByDocument(epm);

				stringbuffer.append(" <tr>");
				stringbuffer.append("<td>文档:<a href=" + subjectObjURL + ">" + docNumber + "<br /></td>");
				stringbuffer.append("<td>" + version + "<br /></td>");
				stringbuffer.append("<td>" + docName + "<br /></td>");
				stringbuffer.append("<td>" + epm.getModifierFullName() + "<br /></td>");
				stringbuffer.append(" </tr>");
				stringbuffer.append("</table>");
				stringbuffer.append("</body>");
				stringbuffer.append("</html>");
				
				mailContent = stringbuffer.toString();

				log.debug("************** \n STATR SEND mailContent:" + mailContent);
				try {

					EMailMessage localEMailMessage = EMailMessage.newEMailMessage();
					List<WTUser> users = WorkflowHelper.getRoleUsers(epm, RoleName.DESIGNER);
					for (WTUser user : users) {
						localEMailMessage.addRecipient(user);
					}
					// String[] addresses = {"alm-gw01@catlbattery.local"};
					localEMailMessage.setSubject(title);
					// localEMailMessage.addEmailAddress(addresses);
					localEMailMessage.addPart(mailContent, "text/html");
					localEMailMessage.send(true);
				} catch (WTException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		
	}

	/**
	 * the method to fetch document oid
	 * 
	 * @param obj
	 * @return
	 * @throws PersistenceException
	 * @throws WTException
	 */

	public static String getURLByDocument(WTObject obj) throws PersistenceException, WTException {
		ReferenceFactory referenceFactory = new ReferenceFactory();
		String url = "";
		if (obj != null) {
			String ufid = getOidByPersistable(obj);
			try {
				WTProperties properties = WTProperties.getLocalProperties();

				URLFactory urlfactory = new URLFactory();
				String baseURL = urlfactory.getBaseURL().toExternalForm();
				url = baseURL + "servlet/TypeBasedIncludeServlet?oid=" + ufid + "&u8=1";
			} catch (WTException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return url;

	}

	public static String getOidByPersistable(Persistable persistable) {
		String oid = "";
		try {
			ReferenceFactory rf = new ReferenceFactory();
			WTReference wtrf = rf.getReference(persistable.toString());
			oid = rf.getReferenceString(wtrf);
		} catch (WTException e) {
			e.printStackTrace();
		}
		return oid;
	}

	/**
	 * 获取ECAD工程师组
	 * 
	 * @return
	 * @throws WTException
	 */
	public static WTGroup getECADGroup() throws WTException {
		QuerySpec qs = new QuerySpec(WTGroup.class);
		SearchCondition sc = new SearchCondition(WTGroup.class, WTGroup.NAME, SearchCondition.EQUAL,
				ECADConst.ECADGROUP);
		qs.appendWhere(sc);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		System.out.println(qr.size());
		if (qr.hasMoreElements()) {
			return (WTGroup) qr.nextElement();
		}
		return null;
	}

	public static void getGroupMemberUsers(WTGroup group, List<WTUser> userList) throws WTException {
		Enumeration groupMembers = group.members();
		while (groupMembers.hasMoreElements()) {
			WTPrincipal principal = (WTPrincipal) groupMembers.nextElement();
			if (principal instanceof WTUser) {
				WTUser user = (WTUser) principal;
				if (!userList.contains(user)) {
					userList.add(user);
				}
			}
			if (principal instanceof WTGroup) {
				WTGroup thegroup = (WTGroup) principal;
				getGroupMemberUsers(thegroup, userList);
			}
		}
	}
}
