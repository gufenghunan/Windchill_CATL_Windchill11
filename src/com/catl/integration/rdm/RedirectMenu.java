package com.catl.integration.rdm;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.json.JSONException;

import com.catl.integration.rdm.bean.MenuBean;

import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.services.applicationcontext.implementation.ServiceProperties;
import wt.util.WTException;

public class RedirectMenu {
	 static Logger logger = Logger.getLogger(RedirectMenu.class);
	 static String rdmaddress = "";
	 static {
		Properties props = null;
		try {
			props = ServiceProperties.getServiceProperties("WTServiceProviderFromProperties");
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rdmaddress = props.getProperty("rdm.address");
		logger.debug(">>>>>rdmaddress:" + rdmaddress);
	 }
	
	//Should change to config file later
	public static String EXT_URL_PREFIX = rdmaddress + Constant.RDM_WC_LINK;

	public static String getRedirectURL(String actionid) throws IOException, JSONException, WTException {
		String userName = "";
		WTPrincipal currentUser = (WTUser) wt.session.SessionHelper.manager.getPrincipal();
		userName = currentUser.getName();
		return getRedirectURL(actionid, userName);
}
	
	public static String getRedirectURL(String actionid, String userName) throws IOException, JSONException, WTException {
		String ret = "";
		String action = actionid;
		logger.debug("Start RedirectMenu.getRedirectURL");
//		logger.debug("Involve - original requestURI=" + requestURI);
//		if (requestURI.length() == 0) return "";
//		if (requestURI.lastIndexOf("/") > 0) {
//			int start = requestURI.lastIndexOf("/");
//			logger.debug("return start position=" + start);
//			action = requestURI.substring(start+1);
//		}
		
		logger.debug("Involve - action=" + action);
		
		if (actionid == null || actionid.length() == 0) return "";
		
		HashMap map = RDMMenuUtil.getMenuMap(userName);
		
		if (map.containsKey(actionid)) {
			ret = EXT_URL_PREFIX + "username=" + userName + "&page=" + ((MenuBean)map.get(actionid)).getPage();
		} else {
			String hostUrl = RDMHelper.generateHostUrl();
			ret = hostUrl + "ptc1/netmarkets/jsp/catl/rdm/error.jsp";
		}
		
		logger.debug("return url=" + ret);
		logger.debug("End RedirectMenu.getRedirectURL");
		return ret;
	}
}
