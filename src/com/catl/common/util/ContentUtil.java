package com.catl.common.util;

import wt.content.ApplicationData;
import wt.content.ContentHolder;
import wt.util.WTException;
import wt.util.WTProperties;

public class ContentUtil {

	public static final String getServerBaseURL() throws WTException {
		try {
			WTProperties serverProperties = WTProperties.getServerProperties();
			return serverProperties.getProperty("wt.server.codebase");
		} catch (Exception ex) {
			throw new WTException(ex, "com.catl.common.util.ContentUtil.getServerBaseURL() error");
		}
	}

	public static final String getDownloadUrl(ContentHolder contentHolder, ApplicationData contentItem) throws WTException {
		String serverBaseURL = getServerBaseURL();
		String downloadLink = serverBaseURL;
		downloadLink += "/servlet/AttachmentsDownloadDirectionServlet?oid=OR:";
		downloadLink += contentHolder.getPersistInfo().getObjectIdentifier().getStringValue();
		downloadLink += "&cioids=";
		downloadLink += contentItem.getPersistInfo().getObjectIdentifier().getStringValue();
		downloadLink += "&role=";
		downloadLink += contentItem.getRole();
		return downloadLink;
	}

}
