package com.catl.require.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.catl.line.util.ExcelUtil;
import com.catl.require.constant.ConstantRequire;
import com.catl.require.constant.GlobalData;

import wt.util.WTProperties;

public class CacheHelper {
	private static final Logger logger = Logger.getLogger(CacheHelper.class.getName());  
	  public static String wt_home;
		static {
			try {
	            wt_home=WTProperties.getLocalProperties().getProperty("wt.home","UTF-8");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	public static void loadExcelConfig() throws FileNotFoundException, IOException{
		 File file = new File(wt_home+ConstantRequire.config_hide_platform);
  	 	if(GlobalData.requireCustomerConfigTime < file.lastModified()){
  	 		GlobalData.config_hide_platform.clear();
  	 		String[][] config_hide_result = ExcelUtil.getData(file,0);
  	 		System.out.println(config_hide_result);
  	 		List list=new ArrayList();
  			for (int i = 1; i < config_hide_result.length; i++) {
  				list.add(config_hide_result[i][0]);
  			}
  	 	    GlobalData.config_hide_platform=list;
  	 	    GlobalData.requireCustomerConfigTime=file.lastModified();
  	 	    
  	 	}
	}
    public static void main(String[] args) throws FileNotFoundException, IOException {
    	loadExcelConfig();
    	System.out.println(GlobalData.config_hide_platform);
	}
	

}
