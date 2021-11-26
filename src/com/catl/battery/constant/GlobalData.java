package com.catl.battery.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class GlobalData {

	/**
	 * 装箱单配置文件修改时间
	 */
	public static long batteryCustomerConfigTime = 0;
	public static long batteryMathCacheLastClearTime = 0;
	public static long batteryMaterialLastBakTime = 0;
	public static long batteryMathLastDownloadTime = 0;
	public static List<String> configchanges=new ArrayList<String>();//配置文件上传后已经更改的文件
	public static List<Map<String, String>> material_libary_info=new ArrayList<Map<String, String>>();
	public static List<Map<String, String>> asm_libary_info=new ArrayList<Map<String, String>>();
	public static List<Map<String, String>> dropDownList_info=new ArrayList<Map<String, String>>();
	public static List<Map<String, String>> colorconfig_info=new ArrayList<Map<String, String>>();
	public static List<Map<String, String>> exportconfig_info=new ArrayList<Map<String, String>>();
	public static Map<String, XSSFWorkbook> mathexcel_cache=new HashMap<String, XSSFWorkbook>();
	public static List<Map<String, String>> defaultconfig_info=new ArrayList<Map<String, String>>();
	public static List<Map<String, String>> validatefromconfig_info=new ArrayList<Map<String, String>>();
	public static Set<String> needVerifySheetName=new HashSet<String>();
	public static Set<String> inOperationFile=new HashSet<String>();
	public static String[][] materical_config_result=null;
}

