package com.catl.line.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalData {

	/**
	 * 装箱单配置文件修改时间
	 */
	public static long boxCustomerConfigTime = 0;
	public static long tagCustomerConfigTime = 0;
	public static long expressCustomerConfigTime = 0;
	/**
	 * 装箱说明模版文件修改时间
	 */
	public static long boxExplainConfigTime = 0;
	/**
	 * 装箱单配置文件缓存
	 */
	public static Map<String,List<String>> boxCustomerConfig = new HashMap<String, List<String>>();
	
	/**
	 * 标签配置文件缓存
	 */
	public static  List<Map<String,String>> taginfo= new ArrayList<Map<String,String>>();
	public static  List<Map<String,String>> dtaginfo= new ArrayList<Map<String,String>>();
	public static  List<Map<String,String>> lengthinfo= new ArrayList<Map<String,String>>();
	
	/**
	 * 图纸填写规则及BOM用量缓存
	 */
	public static Map<String,List<Map<String,String>>> expressCustomerConfig = new HashMap<String,List<Map<String,String>>>();
	
	
	/**
	 * 装箱说明模版文件缓存
	 */
	public static Map<String,List<String>> boxExplainConfig = new HashMap<String, List<String>>();
}

