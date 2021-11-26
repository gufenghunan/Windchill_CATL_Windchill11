package com.catl.common.util;

import wt.util.WTProperties;

public class WCLocationConstants {
	public static final String WT_HOME;
	public static final String WT_CODEBASE;
	public static final String WT_TEMP;
	public static final String WT_LOG;
	static {
		try {
			WTProperties wtproperties = WTProperties.getLocalProperties();
			WT_HOME = wtproperties.getProperty("wt.home");
			WT_CODEBASE = wtproperties.getProperty("wt.codebase.location");
			WT_TEMP = wtproperties.getProperty("wt.temp");
			WT_LOG = wtproperties.getProperty("wt.logs.dir");
		} catch (Throwable throwable) {
			throwable.printStackTrace(System.err);
			throw new ExceptionInInitializerError(throwable);
		}
	}
}
