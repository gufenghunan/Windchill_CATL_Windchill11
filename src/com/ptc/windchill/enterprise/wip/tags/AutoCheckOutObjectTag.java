/*** Eclipse Class Decompiler plugin, copyright (c) 2016 Chen Chao (cnfree2000@hotmail.com) ***/
package com.ptc.windchill.enterprise.wip.tags;

import com.ptc.core.components.beans.CreateAndEditWizBean;
import com.ptc.core.components.jsp.JspUtils;
import com.ptc.core.components.tags.components.AbstractAutoCheckOutItemTag;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.beans.NmHelperBean;
import com.ptc.netmarkets.util.beans.NmURLFactoryBean;
import com.ptc.netmarkets.util.misc.NetmarketURL;
import com.ptc.windchill.enterprise.wip.WIPHelper;
import java.io.IOException;
import java.util.ResourceBundle;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import org.apache.log4j.Logger;
import wt.httpgw.URLFactory;
import wt.inf.container.WTContainerRef;
import wt.log4j.LogR;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.preference.PreferenceHelper;
import wt.session.SessionHelper;
import wt.util.HTMLEncoder;
import wt.util.WTContext;
import wt.util.WTException;

public class AutoCheckOutObjectTag extends AbstractAutoCheckOutItemTag {
	private Logger logger = LogR.getLogger(AutoCheckOutObjectTag.class.getName());

	public Object[] doCheckOut(NmOid arg0) throws WTException {
		this.logger.debug("doCheckOut oid:" + arg0);
		return WIPHelper.doCheckOutOrGetWorkingCopy(arg0);
	}

	public void doTag() throws JspException {
		super.doTag();
		PageContext arg0 = (PageContext) this.getJspContext();
		JspWriter arg1 = arg0.getOut();
		boolean arg2 = false;

		try {
			if (this.isCheckedOut == null) {
				this.logger.debug(
						"Attention:  AutoCheckOutObjectTag encountered an error. \"isCheckedOut is null\" Please contact your administrator.");
				return;
			}

			NmHelperBean arg3 = new NmHelperBean();
			NmCommandBean arg4 = arg3.getNmCommandBean();
			arg4.setRequest(arg0.getRequest());
			WTPrincipal arg5 = SessionHelper.manager.getPrincipal();
			boolean arg6 = false;
			if (arg5 instanceof WTUser) {
				WTContainerRef arg7 = arg4.getContainerRef();
				arg6 = ((Boolean) PreferenceHelper.service.getValue(arg7, "EDIT_ACTION_CANCEL_BEHAVIOR", "WINDCHILL",
						(WTUser) arg5)).booleanValue();
			}

			arg1.println("<script type=\'text/javascript\'>");
			arg2 = true;
			String arg17 = arg4.getTextParameter("actionName");
			this.logger.debug("action name = " + arg17);
			String arg8 = arg4.getTextParameter("ua");
			if (!this.isCheckedOut.booleanValue() || "editCheckIn".equalsIgnoreCase(arg17)) {
				arg1.println("window.editCancelPrepare = function() {");
				if (arg6) {
					ResourceBundle arg9 = ResourceBundle.getBundle("com.ptc.windchill.enterprise.wip.WIPResource",
							WTContext.getContext().getLocale());
					arg1.println("   wfalert(\'" + HTMLEncoder.encodeForJavascript(arg9.getString("STILL_CHECKEDOUT"))
							+ "\');");
				} else {
					arg1.println("  //no prompt pref set");
				}

				arg1.println("wfWindowClose1();");
				arg1.println("return false;");
				arg1.println("}");
				if (!"DTI".equals(arg8)) {
					if ("INFO".equals(arg4.getTextParameter("componentType"))) {
						NmURLFactoryBean arg18 = arg4.getUrlFactoryBean();
						if (arg18 == null) {
							arg18 = new NmURLFactoryBean();
							arg18.setFactory(new URLFactory());
						}

						String arg10 = arg4.getPrimaryOid().getReferenceString();
						String arg11 = CreateAndEditWizBean.getCurrentObjectHandle(arg0.getRequest());
						Object arg12 = arg4.getRequest().getAttribute(arg11 + "checkinOid");
						if (arg12 != null) {
							arg10 = arg12.toString();
							this.logger.debug("Working Copy: " + arg10);
						}

						NmOid arg13 = NmOid.newNmOid(arg10);
						String arg14 = NetmarketURL.buildURL(arg18, "object", "view", arg13);
						arg1.println("getUpdateOpenerWindow().PTC.navigation.loadContent(\"" + arg14 + "\");");
					} else if (!"replace_content".equalsIgnoreCase(arg17)) {
						arg1.println("refreshTable();");
					}
				}
			}

			arg1.println("</script>");
			System.out.println(arg1.toString());
		} catch (Exception arg16) {
			if (arg2) {
				try {
					arg1.println("}");
					arg1.println("</script>");
				} catch (IOException arg15) {
					arg15.printStackTrace();
				}
			}

			this.logger.debug("AutoCheckOutObjectTag Failure, root cause", arg16);
			JspUtils.throwJspException("AutoCheckOutObjectTag Failure", arg16);
		}

	}
}