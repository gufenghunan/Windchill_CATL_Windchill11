package com.catl.common.util;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import com.catl.common.global.GlobalVariable;
import com.catl.doc.workflow.DocWfUtil;

import wt.services.applicationcontext.implementation.ServiceProperties;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.workflow.engine.WfProcess;

public class PropertiesUtil {
	static Properties wtproperties = null; 
	public static String wt_home;
	static {
		try {
			wt_home = WTProperties.getLocalProperties().getProperty("wt.home", "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static String getValueByKey(String key){
		checkNewFile();
		return wtproperties.getProperty(key);
	}
	
	private static void checkNewFile() {
		String filePath = wt_home+"/codebase/config/custom/configuration.properties";
		File file = new File(filePath);
		Long fileModifyTime = file.lastModified();
		Long sysModifyTime = GlobalVariable.fileLastModifyTime.get("configuration.properties");
		if (sysModifyTime == null || fileModifyTime > sysModifyTime) {
			try {
				wtproperties = ServiceProperties.getServiceProperties("WTServiceProviderFromProperties");
				GlobalVariable.fileLastModifyTime.put("configuration.properties",fileModifyTime);
			} catch (WTException e) {
				e.printStackTrace();
			}
		}
	}
}
