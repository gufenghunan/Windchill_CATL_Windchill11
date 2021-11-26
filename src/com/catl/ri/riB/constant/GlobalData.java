package com.catl.ri.riB.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class GlobalData {

	public static long riCustomerConfigTime = 0;
	public static long riMathCacheLastClearTime = 0;
	public static long riMaterialLastBakTime = 0;
	public static long riMathLastDownloadTime = 0;
	public static ArrayList<String> configchanges=new ArrayList<String>();
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

