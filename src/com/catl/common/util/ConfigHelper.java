package com.catl.common.util;

import java.util.Properties;

import wt.services.applicationcontext.implementation.ServiceProperties;
import wt.util.WTException;

public class ConfigHelper {
	private static Properties props = null;
	static{
		try {
			props = ServiceProperties.getServiceProperties("WTServiceProviderFromProperties");
		} catch (WTException e) {
			e.printStackTrace();
		}
	}
	public static String getProperty(String property){
		if(props != null){
			return props.getProperty(property);
		}
		return null;
	}
}
