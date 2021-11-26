package com.catl.cadence.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.fc.ObjectNoLongerExistsException;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.part.WTPart;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTRuntimeException;
import wt.vc.VersionControlHelper;
import wt.vc.struct.StructHelper;

import com.catl.cadence.conf.InitSystemConfigContant;
import com.catl.cadence.conf.InitSystemConfigContant.InitSystemAttrConfig;
import com.catl.ecad.bean.CadenceAttributeBean;
import com.catl.ecad.dbs.CadenceXmlObjectUtil;
import com.catl.ecad.utils.HistoryUtils;
import com.catl.cadence.conf.CadenceConfConstant;
import com.infoengine.SAK.Task;
import com.infoengine.object.factory.Group;
import com.infoengine.util.IEException;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;

/**
 * 将Part的分类属性值发布至Cadence对应分类表中
 */
public class PartReleaseToCadenceUtil {

	private static String DELIMITER = "^";

	/**
	 * 根据物料信息选择表
	 * 
	 * @param part
	 * @throws Exception 
	 */
	public static void sendPartToCadence(WTPart part,
			Map<String, String> colAndValue, String grade) throws Exception {
		String tableName = null;
		List electronics = InitSystemConfigContant.init()
				.getInitSystemNodeElectronic();
		List other = InitSystemConfigContant.init().getInitSystemNodeOther();
		if (part == null) {
			getAllPartToCadence(electronics, tableName);
			getAllPartToCadence(other,
					CadenceConfConstant.CATL_T_CADENCE_PCB_OTHER);
		} else {
			LWCStructEnumAttTemplate lwc = NodeUtil
					.getLWCStructEnumAttTemplateByPart(part);
			if (lwc != null) {
				String pn = lwc.getName();
				if (other.contains(pn)) {
					tableName = CadenceConfConstant.CATL_T_CADENCE_PCB_OTHER;
				}
				Map attrAndCol = getAttrColumnNames(part, tableName,
						colAndValue, grade);
				sendPartToCadence(attrAndCol, part.getNumber());
			}
		}

	}

	/**
	 * 根据物料信息选择表
	 * 
	 * @param part
	 * @throws Exception 
	 */
	public static void sendPartToCadence(WTPart part) throws Exception {
		String tableName = null;
		List electronics = InitSystemConfigContant.init()
				.getInitSystemNodeElectronic();
		List other = InitSystemConfigContant.init().getInitSystemNodeOther();
		if (part == null) {
			getAllPartToCadence(electronics, tableName);
			getAllPartToCadence(other,
					CadenceConfConstant.CATL_T_CADENCE_PCB_OTHER);
		} else {
			LWCStructEnumAttTemplate lwc = NodeUtil
					.getLWCStructEnumAttTemplateByPart(part);
			if (lwc != null) {
				String pn = lwc.getName();
				if (other.contains(pn)) {
					tableName = CadenceConfConstant.CATL_T_CADENCE_PCB_OTHER;
				}
				Map attrAndCol = getAttrColumnNames(part, tableName);
				sendPartToCadence(attrAndCol, part.getNumber());
			}
		}

	}
	
	/**
	 * 更新元器件生命周期状态
	 * 
	 * @param part
	 * @throws Exception 
	 */
	public static void updateStateToCadence(WTPart part) throws Exception {
		String tableName = null;
		List electronics = InitSystemConfigContant.init()
				.getInitSystemNodeElectronic();
		List other = InitSystemConfigContant.init().getInitSystemNodeOther();
		if (part == null) {
			getAllPartToCadence(electronics, tableName);
			getAllPartToCadence(other,
					CadenceConfConstant.CATL_T_CADENCE_PCB_OTHER);
		} else {
			LWCStructEnumAttTemplate lwc = NodeUtil
					.getLWCStructEnumAttTemplateByPart(part);
			if (lwc != null) {
				String pn = lwc.getName();
				if (other.contains(pn)) {
					tableName = CadenceConfConstant.CATL_T_CADENCE_PCB_OTHER;
				}
				Map attrAndCol = getStateAttrColumnNames(part, tableName);
				sendPartToCadence(attrAndCol, part.getNumber());
			}
		}

	}

	/**
	 * 批量发布part到Cadence表
	 * @throws Exception 
	 */
	public static void getAllPartToCadence(List list, String tableName)
			throws Exception {
		WTPart part = null;
		for (int i = 0; i < list.size(); i++) {
			QueryResult qr = NodeUtil.getAllPartsByCLFNodesName((String) list
					.get(i));
			while (qr.hasMoreElements()) {
				part = (WTPart) qr.nextElement();
				Map attrAndCol = getAttrColumnNames(part, tableName);
				sendPartToCadence(attrAndCol, part.getNumber());
			}
		}
	}

	/**
	 * 将Part的分类属性值发布至Cadence对应分类表中
	 * 
	 * @param part
	 * @throws Exception
	 * @throws ObjectNoLongerExistsException
	 * @throws IEException
	 * @throws IOException
	 */
	public static void sendPartToCadence(Map attrAndCol, String partNumber)
			throws WTException {
		// 表名称
		String tableName = null;
		// 使用“?”符号，站位值
		StringBuffer preparedValues = null;
		// 所有Part属性值的类型，并使用分隔符分开
		StringBuffer partAttrTypes = null;
		// 所有part属性值，并使用分隔符分开
		StringBuffer partAttrValues = null;

		StringBuffer columnNames = null;
		for (Iterator it = attrAndCol.keySet().iterator(); it.hasNext();) {
			String colname = (String) it.next();
			if (colname.equals("tableName")) {
				tableName = (String) attrAndCol.get(colname);
				continue;
			}
			if (columnNames == null) {
				columnNames = new StringBuffer();
				preparedValues = new StringBuffer();
				partAttrTypes = new StringBuffer();
				partAttrValues = new StringBuffer();
			} else {
				columnNames.append(",");
				preparedValues.append(",");
				partAttrTypes.append(DELIMITER);
				partAttrValues.append(DELIMITER);
			}
			// 添加栏位
			columnNames.append(colname);

			// 添加值的站位符
			preparedValues.append("?");

			// 添加栏位对应的数据类型
			partAttrTypes.append(ConfigTableUtil.TYPE_VARCHAR);

			String value = (String) attrAndCol.get(colname);
			partAttrValues.append("'").append(value).append("'");
		}
		if (tableName != null) {
			releasePartByTask(tableName, partNumber, columnNames.toString(),
					preparedValues.toString(), partAttrTypes.toString(),
					partAttrValues.toString());
		}
	}

	/**
	 * 获取列名及对应的属性值
	 * 
	 * @param part
	 * @param tableName
	 * @return 返回的Map中：key为列名，value为对应的属性值
	 * @throws WTException
	 * @throws Exception 
	 */
	private static Map getAttrColumnNames(WTPart part, String tableName)
			throws WTException, Exception {
		Map<String, String> colAndValue = new HashMap<>();
		String part_type = null;
		String Schematic_Part = null;
		String Old_Footprint = null;
		IBAUtil partUtil = new IBAUtil(part);

		// 获取属性的列及对应值
		List<InitSystemAttrConfig> confs = InitSystemConfigContant.init()
				.getInitSystemAttrConfig();
		if (tableName != null) {
			confs.removeAll(confs);
			// PCB+Other字段，属性
			List<InitSystemAttrConfig> confs2 = InitSystemConfigContant.init()
					.getInitPCBOtherConfig();
			if (confs2.size() != 0) {
				confs.addAll(confs2);
			}

			// pcb+other表特定值
			LWCStructEnumAttTemplate lwc = NodeUtil
					.getLWCStructEnumAttTemplateByPart(part);
			if (lwc == null) {
				throw new WTException("part[" + part.getNumber() + "]没有选择分类！");
			}
			String pn = lwc.getName();
			Map<String, List<String>> mapList = HistoryUtils.getClfNumber();
			List<String> pcbList = mapList.get("PCB");
			if (pcbList.contains(pn)) {
				part_type = partUtil
						.getIBAValue(CadenceConfConstant.CADENCE_PLM_BOARD_NUMBER);

				Schematic_Part = "PCB";
				Old_Footprint = "PCB";
			} else {
				part_type = "Other";
				Schematic_Part = "Other";
				Old_Footprint = "Other";
			}
			colAndValue.put(CadenceConfConstant.CADENCE_PLM_SCHEMATIC_PART,
					Schematic_Part);
			colAndValue.put(CadenceConfConstant.CADENCE_PLM_OLD_FOOTPRINT,
					Old_Footprint);
			colAndValue.put(CadenceConfConstant.CADENCE_PLM_VALUE,
					part.getName());
		} else {
			// 获取页面Cadence维护面板的列及属性值
			List<CadenceAttributeBean> list = CadenceXmlObjectUtil
					.getCadenceAttributeBeanUtil((WTObject) part);
			if (list.size() >= 1) {
				CadenceAttributeBean cadbean = list.get(list.size()-1);
				colAndValue = cadbean.getNameAndValue();
			}
			part_type = part.getName();//partUtil.getIBAValue(CadenceConfConstant.CADENCE_PLM_ENGLISHNAME);
			String Component_Value = partUtil
					.getIBAValue(CadenceConfConstant.CADENCE_PLM_COMPONENT_VALUE);
			String Component_Model = partUtil
					.getIBAValue(CadenceConfConstant.CADENCE_PLM_COMPONENT_MODEL);
			if (Component_Value != null && !Component_Value.equals("")) {
				colAndValue.put(CadenceConfConstant.CADENCE_PLM_VALUE,
						Component_Value);
			} else {
				colAndValue.put(CadenceConfConstant.CADENCE_PLM_VALUE,
						Component_Model);
			}

			String grade = partUtil
					.getIBAValue(CadenceConfConstant.CADENCE_PLM_GRADE_NAME);
			colAndValue.put(CadenceConfConstant.CADENCE_PLM_GRADE, grade);
			if (grade != null) {
				grade = grade.toUpperCase();
				if (grade
						.startsWith(CadenceConfConstant.CADENCE_PLM_GRADE_VALUE1)) {
					tableName = CadenceConfConstant.CATL_T_CADENCE_PARTS;
				} else if (grade
						.startsWith(CadenceConfConstant.CADENCE_PLM_GRADE_VALUE2)) {
					tableName = CadenceConfConstant.CATL_T_CADENCE_PARTS2;
				} else {
					//throw new WTException("部件【" + part.getNumber()	+ "】的Grade属性值不合规，无法发送Part到Cadence");
				}
			}
			String oid = getDocOidByPart(part);
			if (oid != "") {
				WTProperties props;
				String wthome = "";
				try {
					props = WTProperties.getLocalProperties();
					wthome = props.getProperty("wt.rmi.server.hostname");
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				String datasheet1 = "http://"+wthome
						+ "/Windchill/app/#ptc1/tcomp/infoPage?oid=OR:" + oid;
				colAndValue.put(CadenceConfConstant.CADENCE_PLM_DATASHEET1,
						datasheet1);
			}
		}
		colAndValue.put(CadenceConfConstant.CADENCE_PLM_PART_TYPE, part_type);

		for (InitSystemAttrConfig conf : confs) {
			String attrName = conf.getAttrName();
			String colName = conf.getColumnName();
			if(!CadenceConfConstant.CADENCE_PLM_MATERIAL_SPECIFICATION_NAME.equalsIgnoreCase(attrName)){
				String value = partUtil.getIBAValue(attrName);
				if (StringUtils.isNotBlank(value)) {
					colAndValue.put(colName, value);
				}
			}
		}

		// 获取特定属性列及值，相应的规则处理
		colAndValue.put(CadenceConfConstant.CADENCE_PLM_PART_NUMBER,
				part.getNumber());
		colAndValue.put(CadenceConfConstant.CADENCE_PLM_APPLICANT_NAME,
				part.getCreatorFullName());
		String version = part.getVersionIdentifier().getValue() + "."
				+ part.getIterationIdentifier().getValue();
		colAndValue.put(CadenceConfConstant.CADENCE_PLM_VER, version);
		colAndValue.put(CadenceConfConstant.CADENCE_PLM_UNIT, part
				.getDefaultUnit().getDisplay());
		colAndValue.put(CadenceConfConstant.CADENCE_PLM_MATERIAL_NAME,
				part.getName());
		colAndValue.put(CadenceConfConstant.CADENCE_PLM_STATE, part.getLifeCycleState().toString());
		String specification = partUtil
				.getIBAValue(CadenceConfConstant.CADENCE_PLM_MATERIAL_SPECIFICATION_NAME);
		System.out.println("Spppppppppppppppppp\t"+specification);
		/*
		colAndValue.put(CadenceConfConstant.CADENCE_PLM_MATERIAL_SPECIFICATION,
				specification);
		 */
		
		if(CadenceConfConstant.CATL_T_CADENCE_PCB_OTHER.equalsIgnoreCase(tableName)&StringUtils.isNotBlank(specification)){
			int  num = specification.getBytes("utf-8").length;  
			System.out.println("Spppppppppppppppppp1\t"+num);
			if(num > 240){
				colAndValue.put(CadenceConfConstant.CADENCE_PLM_DESCRIPTION, bSubstring(specification, 240));
				System.out.println("Spppppppppppppppppp2\t"+bSubstring(specification, 240));
			}else{
				colAndValue.put(CadenceConfConstant.CADENCE_PLM_DESCRIPTION, specification);
			}
		}else{
			String desRule = CadenceConfConstant.CADENCE_PLM_DESCRIPTION_RULE;
			StringBuffer dessb = new StringBuffer();
			dessb.append(part.getName());
			String[] rules = desRule.split(",");
			for (int i = 0; i < rules.length; i++) {
				if(colAndValue.containsKey(rules[i])){
					dessb.append("_").append(colAndValue.get(rules[i]));
				}
			}
			
			colAndValue.put(CadenceConfConstant.CADENCE_PLM_DESCRIPTION, dessb.toString());
		}
		
		
		/*
		if (specification != null) {
			int count = 0;
			if (specification.indexOf("品牌") > -1) {
				count ++;
			} 
			if (specification.indexOf("MPN") > -1) {
				count ++;
			}
			Pattern pattern = Pattern.compile("\\:(.*?)\\_");
			Matcher matcher = pattern.matcher(specification + "_");
			String des = "";
			int i = 0;
			boolean flag = true;
			while (matcher.find()) {
				if (flag) {
					des = matcher.group(1);
					flag = false;
				} else {
					if (i == count) {
						des = des + ";" + matcher.group(1);
					} else {
						des = des + "_" + matcher.group(1);
					}
				}
				i++;
			}
			colAndValue.put(CadenceConfConstant.CADENCE_PLM_DESCRIPTION, dessb.toString());
		}*/
		colAndValue.put("tableName", tableName);
		return colAndValue;
	}

	
	/**
	 * 获取生命周期状态
	 * 
	 * @param part
	 * @param tableName
	 * @return 返回的Map中：key为列名，value为对应的属性值
	 * @throws WTException
	 */
	private static Map getStateAttrColumnNames(WTPart part, String tableName)
			throws WTException {
		Map<String, String> colAndValue = new HashMap<>();
		IBAUtil partUtil = new IBAUtil(part);
		
		if (tableName == null) {
			String grade = partUtil
					.getIBAValue(CadenceConfConstant.CADENCE_PLM_GRADE_NAME);
			colAndValue.put(CadenceConfConstant.CADENCE_PLM_GRADE, grade);
			if (grade != null) {
				grade = grade.toUpperCase();
				if (grade
						.startsWith(CadenceConfConstant.CADENCE_PLM_GRADE_VALUE1)) {
					tableName = CadenceConfConstant.CATL_T_CADENCE_PARTS;
				} else if (grade
						.startsWith(CadenceConfConstant.CADENCE_PLM_GRADE_VALUE2)) {
					tableName = CadenceConfConstant.CATL_T_CADENCE_PARTS2;
				} else {
					//throw new WTException("部件【" + part.getNumber()	+ "】的Grade属性值不合规，无法发送Part到Cadence");
				}
			}
			
		}
		
		colAndValue.put(CadenceConfConstant.CADENCE_PLM_PART_NUMBER,
				part.getNumber());
		colAndValue.put(CadenceConfConstant.CADENCE_PLM_STATE, part.getLifeCycleState().toString());
		
		colAndValue.put("tableName", tableName);
		return colAndValue;
	}
	
	/**
	 * 获取列名及对应的属性值
	 * 
	 * @param part
	 * @param tableName
	 * @return 返回的Map中：key为列名，value为对应的属性值
	 * @throws WTException
	 * @throws Exception 
	 */
	private static Map getAttrColumnNames(WTPart part, String tableName,
			Map<String, String> colAndValue, String grade) throws WTException, Exception {
		// Map<String, String> colAndValue = new HashMap<>();
		String part_type = null;
		String Schematic_Part = null;
		String Old_Footprint = null;
		IBAUtil partUtil = new IBAUtil(part);

		// 获取属性的列及对应值
		List<InitSystemAttrConfig> confs = InitSystemConfigContant.init()
				.getInitSystemAttrConfig();
		if (tableName != null) {
			confs.removeAll(confs);
			// PCB+Other字段，属性
			List<InitSystemAttrConfig> confs2 = InitSystemConfigContant.init()
					.getInitPCBOtherConfig();
			if (confs2.size() != 0) {
				confs.addAll(confs2);
			}

			// pcb+other表特定值
			LWCStructEnumAttTemplate lwc = NodeUtil
					.getLWCStructEnumAttTemplateByPart(part);
			if (lwc == null) {
				throw new WTException("part[" + part.getNumber() + "]没有选择分类！");
			}
			String pn = lwc.getName();
			Map<String, List<String>> mapList = HistoryUtils.getClfNumber();
			List<String> pcbList = mapList.get("PCB");
			if (pcbList.contains(pn)) {
				part_type = partUtil
						.getIBAValue(CadenceConfConstant.CADENCE_PLM_BOARD_NUMBER);
				Schematic_Part = "PCB";
				Old_Footprint = "PCB";
			} else {
				part_type = "Other";
				Schematic_Part = "Other";
				Old_Footprint = "Other";
			}
			if(!colAndValue.containsKey(CadenceConfConstant.CADENCE_PLM_SCHEMATIC_PART)){
				colAndValue.put(CadenceConfConstant.CADENCE_PLM_SCHEMATIC_PART,
						Schematic_Part);
			}
			
			if(!colAndValue.containsKey(CadenceConfConstant.CADENCE_PLM_OLD_FOOTPRINT)){
				colAndValue.put(CadenceConfConstant.CADENCE_PLM_OLD_FOOTPRINT,
						Old_Footprint);
			}
			
			colAndValue.put(CadenceConfConstant.CADENCE_PLM_VALUE,
					part.getName());
		} else {
			// 获取页面Cadence维护面板的列及属性值
			/*
			 * List<CadenceAttributeBean> list = CadenceXmlObjectUtil
			 * .getCadenceAttributeBeanUtil((WTObject) part); if (list.size() !=
			 * 0) { CadenceAttributeBean cadbean =list.get(0); colAndValue =
			 * cadbean.getNameAndValue(); }
			 */
			part_type = part.getName();//partUtil.getIBAValue(CadenceConfConstant.CADENCE_PLM_ENGLISHNAME);
			String Component_Value = partUtil
					.getIBAValue(CadenceConfConstant.CADENCE_PLM_COMPONENT_VALUE);
			String Component_Model = partUtil
					.getIBAValue(CadenceConfConstant.CADENCE_PLM_COMPONENT_MODEL);
			if (Component_Value != null && !Component_Value.equals("")) {
				colAndValue.put(CadenceConfConstant.CADENCE_PLM_VALUE,
						Component_Value);
			} else {
				colAndValue.put(CadenceConfConstant.CADENCE_PLM_VALUE,
						Component_Model);
			}

			if (grade == null) {
				grade = partUtil
						.getIBAValue(CadenceConfConstant.CADENCE_PLM_GRADE_NAME);
				colAndValue.put(CadenceConfConstant.CADENCE_PLM_GRADE, grade);
			}
			if (grade != null) {
				grade = grade.toUpperCase();
				if (grade
						.startsWith(CadenceConfConstant.CADENCE_PLM_GRADE_VALUE1)) {
					tableName = CadenceConfConstant.CATL_T_CADENCE_PARTS;
				} else if (grade
						.startsWith(CadenceConfConstant.CADENCE_PLM_GRADE_VALUE2)) {
					tableName = CadenceConfConstant.CATL_T_CADENCE_PARTS2;
				} else {
					//throw new WTException("部件【" + part.getNumber()	+ "】的Grade属性值不合规，无法发送Part到Cadence");
				}
				colAndValue.put(CadenceConfConstant.CADENCE_PLM_GRADE, grade);
			}
			String oid = getDocOidByPart(part);
			if (oid != "") {
				WTProperties props;
				String wthome = "";
				try {
					props = WTProperties.getLocalProperties();
					wthome = props.getProperty("wt.rmi.server.hostname");
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				String datasheet1 = "http://"+wthome
						+ "/Windchill/app/#ptc1/tcomp/infoPage?oid=OR:" + oid;
				colAndValue.put(CadenceConfConstant.CADENCE_PLM_DATASHEET1,
						datasheet1);
			}
		}
		colAndValue.put(CadenceConfConstant.CADENCE_PLM_PART_TYPE, part_type);

		for (InitSystemAttrConfig conf : confs) {
			String attrName = conf.getAttrName();
			String colName = conf.getColumnName();
			if(!CadenceConfConstant.CADENCE_PLM_MATERIAL_SPECIFICATION_NAME.equalsIgnoreCase(attrName)){
				String value = partUtil.getIBAValue(attrName);
				if (StringUtils.isNotBlank(value)) {
					colAndValue.put(colName, value);
				}
			}
		}

		// 获取特定属性列及值，相应的规则处理
		colAndValue.put(CadenceConfConstant.CADENCE_PLM_PART_NUMBER,
				part.getNumber());
		colAndValue.put(CadenceConfConstant.CADENCE_PLM_APPLICANT_NAME,
				part.getCreatorFullName());
		String version = part.getVersionIdentifier().getValue() + "."
				+ part.getIterationIdentifier().getValue();
		colAndValue.put(CadenceConfConstant.CADENCE_PLM_VER, version);
		colAndValue.put(CadenceConfConstant.CADENCE_PLM_UNIT, part
				.getDefaultUnit().getDisplay());
		colAndValue.put(CadenceConfConstant.CADENCE_PLM_MATERIAL_NAME,
				part.getName());
		colAndValue.put(CadenceConfConstant.CADENCE_PLM_STATE, part.getLifeCycleState().toString());
		String specification = partUtil
				.getIBAValue(CadenceConfConstant.CADENCE_PLM_MATERIAL_SPECIFICATION_NAME);
		/*
		colAndValue.put(CadenceConfConstant.CADENCE_PLM_MATERIAL_SPECIFICATION,
				specification);
		*/
		
		if(CadenceConfConstant.CATL_T_CADENCE_PCB_OTHER.equalsIgnoreCase(tableName)&StringUtils.isNotBlank(specification)){
			int  num = specification.getBytes("utf-8").length;  
			if(num > 240){
				colAndValue.put(CadenceConfConstant.CADENCE_PLM_DESCRIPTION, bSubstring(specification, 240));
			}
		}else{
			String desRule = CadenceConfConstant.CADENCE_PLM_DESCRIPTION_RULE;
			StringBuffer dessb = new StringBuffer();
			dessb.append(part.getName());
			String[] rules = desRule.split(",");
			for (int i = 0; i < rules.length; i++) {
				if(colAndValue.containsKey(rules[i])){
					dessb.append("_").append(colAndValue.get(rules[i]));
				}
			}
			
			colAndValue.put(CadenceConfConstant.CADENCE_PLM_DESCRIPTION, dessb.toString());
		}
		/*
		if (specification != null) {
			int count = 0;
			if (specification.indexOf("品牌") > -1) {
				count ++;
			} 
			if (specification.indexOf("MPN") > -1) {
				count ++;
			}
			Pattern pattern = Pattern.compile("\\:(.*?)\\_");
			Matcher matcher = pattern.matcher(specification + "_");
			String des = "";
			int i = 0;
			boolean flag = true;
			while (matcher.find()) {
				if (flag) {
					des = matcher.group(1);
					flag = false;
				} else {
					if (i == count) {
						des = des + ";" + matcher.group(1);
					} else {
						des = des + "_" + matcher.group(1);
					}
				}
				i++;
			}
			colAndValue.put(CadenceConfConstant.CADENCE_PLM_DESCRIPTION, dessb.toString());
		}*/
		
		
		colAndValue.put("tableName", tableName);
		Set set = colAndValue.keySet();
		for(Object obj :set){
			System.out.println("Key:\t"+obj);
		}
		return colAndValue;
	}

	/**
	 * 添加信息至cadence表
	 * 
	 * @param tableName
	 * @param columnnames
	 * @param preparedValues
	 * @param partAttrTypes
	 * @param paramvalues
	 * @throws WTException
	 */
	private static void releasePartByTask(String tableName, String partNumber,
			String columnnames, String preparedValues, String partAttrTypes,
			String paramvalues) throws WTException {
		Task task = new Task(CadenceConfConstant.TASK_CREATE_RECORD);
		task.addParam("w4cinstance",
				CadenceConfConstant.TASK_W4C_JDBCADAPTER_SERVICES_NAME);
		task.addParam("tableName", tableName);
		task.addParam("partNumber", partNumber);
		task.addParam("columnnames", columnnames);
		task.addParam("preparedvalues", preparedValues);
		task.addParam("delimiter", DELIMITER);
		task.addParam("paramtypes", partAttrTypes);
		task.addParam("paramvalues", paramvalues);
		task.addParam("groupout", "groupout");
		try {
			task.invoke();
		} catch (IEException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Enumeration groups = task.getGroupNames();
		while (groups.hasMoreElements()) {
			String groupName = (String) groups.nextElement();
			if ("failure".equals(groupName)) {
				Group g = task.getGroup(groupName);
				throw new WTException(g.getMessage());
			}
		}
	}

	public static void main(String args[]) throws WTRuntimeException,
			WTException {
		WTPart part = (WTPart) new ReferenceFactory().getReference(
				"VR:wt.part.WTPart:178713509").getObject();
		getDocOidByPart(part);
	}

	/**
	 * 获取部件相关联的规格书oid
	 * 
	 * @param part
	 * @return String
	 * @throws WTException
	 */
	public static String getDocOidByPart(WTPart part) throws WTException {
		String str = "";
		QueryResult qr = StructHelper.service.navigateReferences(part);
		while (qr.hasMoreElements()) {
			WTDocumentMaster master = (WTDocumentMaster) qr.nextElement();
			QueryResult qr2 = VersionControlHelper.service
					.allVersionsOf(master);
			while (qr2.hasMoreElements()) {
				WTDocument document = (WTDocument) qr2.nextElement();
				str = document.toString();
			}
		}
		return str;
	}

	/**
	 * 按字节数截取字符串
	 * @param s
	 * @param length
	 * @return
	 * @throws Exception
	 */
	public static String bSubstring(String s, int length) throws Exception
    {

        byte[] bytes = s.getBytes("Unicode");
        int n = 0; // 表示当前的字节数
        int i = 2; // 要截取的字节数，从第3个字节开始
        for (; i < bytes.length && n < length; i++)
        {
            // 奇数位置，如3、5、7等，为UCS2编码中两个字节的第二个字节
            if (i % 2 == 1)
            {
                n++; // 在UCS2第二个字节时n加1
            }
            else
            {
                // 当UCS2编码的第一个字节不等于0时，该UCS2字符为汉字，一个汉字算两个字节
                if (bytes[i] != 0)
                {
                    n++;
                }
            }
        }
        // 如果i为奇数时，处理成偶数
        if (i % 2 == 1)

        {
            // 该UCS2字符是汉字时，去掉这个截一半的汉字
            if (bytes[i - 1] != 0)
                i = i - 1;
            // 该UCS2字符是字母或数字，则保留该字符
            else
                i = i + 1;
        }

        return new String(bytes, 0, i, "Unicode");
    }
}
