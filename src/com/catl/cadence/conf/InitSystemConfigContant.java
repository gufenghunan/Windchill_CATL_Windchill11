package com.catl.cadence.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class InitSystemConfigContant {
	private static Properties properties = new Properties();
	private static InitSystemConfigContant config = null;
	
	private static String ATTR_NAME_PREFIX = "plmattr.name.";
	private static String ATTR_COLNAME_PREFIX = "plmattr.columnname.";
	private static String ATTR_VIEWNAME_PREFIX = "plmattr.viewcolumnname.";
	
	private static String CADATTR_NAME_PREFIX = "cadattr.name.";
	private static String CADATTR_COLNAME_PREFIX = "cadattr.columnname.";
	private static String CADATTR_VIEWNAME_PREFIX = "cadattr.viewcolumnname.";
	
	private static String PCBOTHER_NAME_PREFIX = "pcbother.name.";
	private static String PCBOTHER_COLNAME_PREFIX = "pcbother.columnname.";
	private static String PCBOTHER_VIEWNAME_PREFIX = "pcbother.viewcolumnname.";
	
	private static String ROOT_NODE_NAME_ELECTRONIC = "clf.electronic.internalName.";
	private static String ROOT_NODE_NAME_OTHER = "clf.other.internalName.";
	
	private InitSystemConfigContant(){}
	public static InitSystemConfigContant init(){
		if(config == null)
			config = new InitSystemConfigContant();
		else
			return config;
		return config;
	}
	static {
		InputStream inStream = CadenceConfConstant.class
				.getResourceAsStream("/com/catl/cadence/conf/initSystemConfig.properties");
		try {
			properties.load(inStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取属性、字段名称(plm系统)
	 * @return List
	 */
	public List getInitSystemAttrConfig(){
		List result = new ArrayList();
		int i = 0;
		while(true){
			String attrName = get(ATTR_NAME_PREFIX + i);
			String columnName = get(ATTR_COLNAME_PREFIX + i);
			String viewColumnName = get(ATTR_VIEWNAME_PREFIX + i);
			if(attrName == null)
				break;
			result.add(new InitSystemAttrConfig(attrName, columnName, viewColumnName));
			i++;
		}
		return result;
	}
	
	/**
	 * 获取属性、字段名称（cadence）
	 * @return List
	 */
	public List getInitSystemCadAttrConfig(){
		List result = new ArrayList();
		int i = 0;
		while(true){
			String attrName = get(CADATTR_NAME_PREFIX + i);
			String columnName = get(CADATTR_COLNAME_PREFIX + i);
			String viewColumnName = get(CADATTR_VIEWNAME_PREFIX + i);
			if(attrName == null)
				break;
			result.add(new InitSystemAttrConfig(attrName, columnName, viewColumnName));
			i++;
		}
		return result;
	}
	
	/**
	 * 获取属性、字段名称(pcb+other)
	 * @return List
	 */
	public List getInitPCBOtherConfig(){
		List result = new ArrayList();
		int i = 0;
		while(true){
			String attrName = get(PCBOTHER_NAME_PREFIX + i);
			String columnName = get(PCBOTHER_COLNAME_PREFIX + i);
			String viewColumnName = get(PCBOTHER_VIEWNAME_PREFIX + i);
			if(attrName == null)
				break;
			result.add(new InitSystemAttrConfig(attrName, columnName, viewColumnName));
			i++;
		}
		return result;
	}
	
	/**
	 * 获取物料组内部名称(电子物料组)
	 * @return Map
	 */
	public List getInitSystemNodeElectronic(){
		List result = new ArrayList();
		int i = 0;
		while(true){
			String nodeName = get(ROOT_NODE_NAME_ELECTRONIC + i);
			if(nodeName == null)
				break;
			
			result.add(nodeName);
			i++;
		}
		return result;
	}
	
	/**
	 * 获取物料组内部名称(其他物料组)
	 * @return Map
	 */
	public List getInitSystemNodeOther(){
		List result = new ArrayList();
		int i = 0;
		while(true){
			String nodeName = get(ROOT_NODE_NAME_OTHER + i);
			if(nodeName == null)
				break;
			
			result.add(nodeName);
			i++;
		}
		return result;
	}
	
	private static String get(String key){
		return properties.getProperty(key);
	}

	public class InitSystemAttrConfig{
		private String attrName;
		private String columnName;
		private String viewColumnName;
		public InitSystemAttrConfig(String attrName, String columnName, String viewColumnName){
			this.attrName = attrName;
			this.columnName = columnName;
			this.viewColumnName = viewColumnName;
		}
		public String getAttrName() {
			return attrName;
		}
		public String getColumnName() {
			return columnName;
		}
		public String getViewColumnName() {
			return viewColumnName;
		}
		public String toString(){
			return "attrName:" + attrName + ", columnName:" + columnName + ", viewColumnName:" +viewColumnName;
		}
	}
	
	public static void main(String[] args) {
//		List list1 = InitSystemConfigContant.init().getInitSystemAttrConfig();
//		for(int i = 0; i < list1.size(); i++){
//			System.out.println(list1.get(i));
//		}
//		System.out.println("**********************************************");
	}
}
