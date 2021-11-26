package com.catl.change.DataUtility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.google.thirdparty.publicsuffix.PublicSuffixPatterns;

import wt.util.WTProperties;



public class CatlPropertyHelper {
	
	private static Logger logger=Logger.getLogger(CatlPropertyHelper.class.getName());
	public static String getPropertyValue(String key){
        String value = null;
        try {
        	logger.debug("new key=========="+key);
        	String nameString =changekey(key);
        	logger.debug("name string ==="+nameString);
            Properties customProperties = new Properties();
			WTProperties wtproperties = WTProperties.getLocalProperties();
			String codebase = wtproperties.getProperty("wt.codebase.location");
            customProperties.load(new FileInputStream(codebase+"/config/custom/ECATaskProperties.properties"));
            value = (String) customProperties.getProperty(nameString).trim();

            value = new String(value.getBytes("ISO-8859-1"), "GBK"); // 处理中文乱码
            logger.debug("value======="+value);
        } catch (IOException e) {
        	e.printStackTrace(); 
        } catch(Exception e) {
        	e.printStackTrace();
        }

        return value;
    }
	public static String getDocPropertyValue(String key){
        String value = null;
        try {
        	logger.debug("new key=========="+key);
        	String nameString = key;
        	logger.debug("name string ==="+nameString);
            Properties customProperties = new Properties();
			WTProperties wtproperties = WTProperties.getLocalProperties();
			String codebase = wtproperties.getProperty("wt.codebase.location");
            customProperties.load(new FileInputStream(codebase+"/config/custom/IBADocProperties.properties"));
            nameString = new String(nameString.getBytes("GBK"), "ISO-8859-1");
            value = customProperties.getProperty(nameString);
            if(value == null)
            	return "";

            value = new String(value.trim().getBytes("ISO-8859-1"), "GBK"); // 处理中文乱码
            logger.debug("value======="+value);
        } catch (IOException e) {
        	e.printStackTrace(); 
        } catch(Exception e) {
        	value = "";
        	e.printStackTrace();
        }

        return value;
    }
	
	public static String changekey(String key)
	{
		//项目管理更改任务,品质管理更改任务,来料检验更改任务,研发更改任务,测试更改任务,总装工艺开发更改任务,
		//设备开发更改任务,工业规划更改任务,生产制造更改任务,物控与计划控制更改任务,采购更改任务,财务更改任务,市场更改任务,售后更改任务,物流更改任务
		String name="";
		if (key==null) {
			name=null;
		}else {
			if (key.endsWith("ECATaskDes")) {
				name="ECATaskDes";
			}	
			if (key.endsWith("ECATask")) {
				name="ECATask";
			}
			if (key.equals("")) {
                name="99";
            }
			if (key.endsWith("项目管理更改任务")) {
				name="11";
			}
			if (key.endsWith("品质管理更改任务")) {
				name="12";
			}
			if (key.endsWith("来料检验更改任务")) {
				name="13";
			}
			if (key.endsWith("研发更改任务")) {
				name="14";
			}
			if (key.endsWith("测试更改任务")) {
				name="15";
			}
			if (key.endsWith("总装工艺开发更改任务")) {
				name="16";
			}
			if (key.endsWith("设备开发更改任务")) {
				name="17";
			}
			if (key.endsWith("工业规划更改任务")) {
				name="18";
			}
			if (key.endsWith("生产制造更改任务")) {
				name="19";
			}
			if (key.endsWith("控与计划控制更改任务")) {
				name="20";
			}
			if (key.endsWith("采购更改任务")) {
				name="21";
			}
			if (key.endsWith("财务更改任务")) {
				name="22";
			}
			if (key.endsWith("市场更改任务")) {
				name="23";
			}
			if (key.endsWith("售后更改任务")) {
				name="24";
			}
			if (key.endsWith("物流更改任务")) {
				name="25";
			}
			if (key.endsWith("知会客户更改任务")) {
				name="26";
			}
		}
		return name;
	}
	
	/**
	 * 新加处理ECA的任务和描述联动
	 * @author zyw2
	 * @param key
	 * @return
	 */
	public static String getDcaPropertyValue(String key){
        String value = null;
        try {
        	logger.debug("new key=========="+key);
        	String nameString = changeDcakey(key);
        	logger.debug("name string ==="+nameString);
            Properties customProperties = new Properties();
			WTProperties wtproperties = WTProperties.getLocalProperties();
			String codebase = wtproperties.getProperty("wt.codebase.location");
            customProperties.load(new FileInputStream(codebase+"/config/custom/DCATaskProperties.properties"));
            value = (String) customProperties.getProperty(nameString).trim();

            value = new String(value.getBytes("ISO-8859-1"), "GBK"); // 处理中文乱码
            logger.debug("value======="+value);
        } catch (IOException e) {
        	e.printStackTrace(); 
        } catch(Exception e) {
        	e.printStackTrace();
        }

        return value;
    }
	
	public static String changeDcakey(String key)
	{
		//研发更改任务,物控与计划控制更改任务
		String name="";
		if (key==null) {
			name=null;
		}else {
			if (key.endsWith("DCATaskDes")) {
				name="DCATaskDes";
			}	
			if (key.endsWith("DCATask")) {
				name="DCATask";
			}
			if (key.equals("")) {
                name="99";
            }
			if (key.endsWith("项目管理更改任务")) {
				name="11";
			}
			if (key.endsWith("品质管理更改任务")) {
				name="12";
			}
			if (key.endsWith("来料检验更改任务")) {
				name="13";
			}
			if (key.endsWith("研发更改任务")) {
				name="14";
			}
			if (key.endsWith("测试更改任务")) {
				name="15";
			}
			if (key.endsWith("总装工艺开发更改任务")) {
				name="16";
			}
			if (key.endsWith("设备开发更改任务")) {
				name="17";
			}
			if (key.endsWith("工业规划更改任务")) {
				name="18";
			}
			if (key.endsWith("生产制造更改任务")) {
				name="19";
			}
			if (key.endsWith("控与计划控制更改任务")) {
				name="20";
			}
			if (key.endsWith("采购更改任务")) {
				name="21";
			}
			if (key.endsWith("财务更改任务")) {
				name="22";
			}
			if (key.endsWith("市场更改任务")) {
				name="23";
			}
			if (key.endsWith("售后更改任务")) {
				name="24";
			}
			if (key.endsWith("物流更改任务")) {
				name="25";
			}
			if (key.endsWith("知会客户更改任务")) {
				name="26";
			}
		}
		return name;
	}
	
	/*
	 * @ this method return the int Value	 * 
	 */
    public static int getPropertyValues(String key){
        String value = null;
        int invalue = 0;
        try {
            key = key.replace(" ", "_");
            WTProperties props = WTProperties.getLocalProperties();
            Properties customProperties = new Properties();
            String codebase = props.getProperty("wt.codebase.location");
            String customPropertiesPath = codebase + File.separator + "com"
            	+ File.separator + "catl" +  File.separator + "CountryProperties.properties";
            customProperties.load(new FileInputStream(customPropertiesPath));
            value = customProperties.getProperty(key).trim();
            invalue = Integer.parseInt(value);
        } catch (IOException e) {
        	e.printStackTrace();
        } catch(Exception e) {
        	e.printStackTrace();
        }
        return invalue;
    }
	
    
    public static String getDisableReasonERPMapPropertyValue(String key){
        String value = null;
        try {
        	logger.debug("new key=========="+key);
        	String nameString = key;
        	logger.debug("name string ==="+nameString);
            Properties customProperties = new Properties();
			WTProperties wtproperties = WTProperties.getLocalProperties();
			String codebase = wtproperties.getProperty("wt.codebase.location");
            customProperties.load(new FileInputStream(codebase+"/config/custom/DisableReasonERPMapProperties.properties"));
            value = (String) customProperties.getProperty(nameString).trim();

            value = new String(value.getBytes("ISO-8859-1"), "GBK"); // 处理中文乱码
            logger.debug("value======="+value);
        } catch (IOException e) {
        	e.printStackTrace(); 
        } catch(Exception e) {
        	e.printStackTrace();
        }

        return value;
    }

    public static String getRepUserPropertyValue(String key){
        String value = null;
        try {
        	logger.debug("new key=========="+key);
        	String nameString = key;
        	logger.debug("name string ==="+nameString);
            Properties customProperties = new Properties();
			WTProperties wtproperties = WTProperties.getLocalProperties();
			String codebase = wtproperties.getProperty("wt.codebase.location");
            customProperties.load(new FileInputStream(codebase+"/config/custom/RepUserProperties.properties"));
            nameString = new String(nameString.getBytes("GBK"), "ISO-8859-1");
            value = customProperties.getProperty(nameString);
            if(value == null)
            	return "";

            value = new String(value.trim().getBytes("ISO-8859-1"), "GBK"); // 处理中文乱码
            logger.debug("value======="+value);
        } catch (IOException e) {
        	e.printStackTrace(); 
        } catch(Exception e) {
        	value = "";
        	e.printStackTrace();
        }

        return value;
    }
    
    public static String getDocNeedPNPropertyValue(String key){
        String value = null;
        try {
        	logger.debug("new key=========="+key);
        	String nameString = key;
        	logger.debug("name string ==="+nameString);
            Properties customProperties = new Properties();
			WTProperties wtproperties = WTProperties.getLocalProperties();
			String codebase = wtproperties.getProperty("wt.codebase.location");
            customProperties.load(new FileInputStream(codebase+"/config/custom/DocNeedLinkPNProperties.properties"));
            nameString = new String(nameString.getBytes("GBK"), "ISO-8859-1");
            value = customProperties.getProperty(nameString);
            if(value == null)
            	return "";

            value = new String(value.trim().getBytes("ISO-8859-1"), "GBK"); // 处理中文乱码
            logger.debug("value======="+value);
        } catch (IOException e) {
        	e.printStackTrace(); 
        } catch(Exception e) {
        	value = "";
        	e.printStackTrace();
        }

        return value;
    }
}
