package com.catl.integration.rdm;

import wt.httpgw.URLFactory;
import wt.util.WTException;

public class RDMHelper {
	
	private static String hostUrl = "";
	static {
		try {
			URLFactory urlFactory = new URLFactory();
			hostUrl = urlFactory.getBaseHREF();
		} catch (WTException e) {
			e.printStackTrace();
		}
	}

	public static String generateHostUrl() throws WTException {
		if (hostUrl == null || "".equals(hostUrl)) {
			hostUrl = new URLFactory().getBaseHREF();
		}
		return hostUrl;
	}



}