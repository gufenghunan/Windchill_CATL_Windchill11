package com.catl.ecad.utils;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import wt.util.WTProperties;

/**
 * <p>用来管理FTPConfig.properties配置文件</p>
 * @author Sam
 * @version 1.0
 */
public class FTPConfigProperties extends Properties {
	private static final long serialVersionUID = 1L;
	public static final String DocTypes = "DocTypes";
	public static final String Config_Ftp_URL = "Config_Ftp_URL";
	public static final String Config_Ftp_URL1 = "Config_Ftp_URL1";
	public static final String Config_Ftp_User = "Config_Ftp_User";
	public static final String Config_Ftp_Pwd = "Config_Ftp_Pwd";
	public static final String Config_Ftp_Path = "Config_Ftp_Path";

	private static Properties properties = new FTPConfigProperties();

	public static String getConfigFtpPath() {
		return getMapValue(Config_Ftp_Path);
	}

	static {
		try {
			WTProperties wtproperties = WTProperties.getLocalProperties();
			String path = wtproperties.getProperty("wt.home", "UTF-8")
					+ File.separator + "codebase" + File.separator + "com"
					+ File.separator + "catl" + File.separator + "ecad"
					+ File.separator + "utils"
					+ File.separator + "FTPConfig.properties";
			properties.load(new FileInputStream(new File(path)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public FTPConfigProperties() {}

	public static String getMapValue(String AttrName) {
		return properties.getProperty(AttrName);
	}		

	public static String getFtpURL() {
		return getMapValue(Config_Ftp_URL);
	}

	public static String getFtpUser() {
		return getMapValue(Config_Ftp_User);
	}

	public static String getFtpPwd() {
		return getMapValue(Config_Ftp_Pwd);
	}
	
	public static String getFtpURL1() {
		return getMapValue(Config_Ftp_URL1);
	}
	
	
	
	public static void main(String[] args) {
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+getConfigFtpPath());
	}
	
}
