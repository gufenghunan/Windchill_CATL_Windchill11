package com.catl.cadence.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CadenceConfConstant {
	private static Properties properties = new Properties();
	
	/**
	 * 连接配置信息维护的JDBCAdapter
	 */
	public static String TASK_W4C_JDBCADAPTER_SERVICES_NAME;
	
	/**
	 * 数据库用户
	 */
	public static String TASK_DATABASE_USERNAME;
	
	/**
	 * 创建表
	 */
	public static String TASK_CREATE_ATTRTABLE;
	
	/**
	 * 授权并创建同义词
	 */
	public static String TASK_CREATE_GRANT;
	/**
	 * 获取新增列
	 */
	public static String TASK_GET_NEWCOLUMN;
	
	/**
	 * Task的URL(添加表字段)
	 */
	public static String TASK_ADD_TABLECOLUMN;
	
	/**
	 * Task的URL(添加记录)
	 */
	public static String TASK_CREATE_RECORD;
	
	/**
	 * 表1名称
	 */
	public static String CATL_T_CADENCE_PARTS;
	
	/**
	 * 表2名称
	 */
	public static String CATL_T_CADENCE_PARTS2;
	
	/**
	 * 表3名称(PCB+其他)
	 */
	public static String CATL_T_CADENCE_PCB_OTHER;
	
	/**
	 * 在CIS中创建Table时，名称是用户设定名称加如下后缀组成
	 */
	public static String TABLE_SUFFIX;
	
	/**
	 * 汽车级元器件视图名称
	 */
	public static String AUTOMOTIVE_PARTS;
	
	/**
	 * 工业级元器件视图名称
	 */
	public static String INDUSTRY_PARTS;
	
	/**
	 * PCB及其他元器件视图名称
	 */
	public static String PCB_OTHER_PARTS;
	
	
	/**
	 * 物料号
	 */
	public static String CADENCE_PLM_PART_NUMBER;
	
	/**
	 * 组合字段
	 */
	public static String CADENCE_PLM_DESCRIPTION;
	
	/**
	 * Part_type
	 */
	public static String CADENCE_PLM_PART_TYPE;
	
	/**
	 * 英文描述內部名称
	 */
	public static String CADENCE_PLM_ENGLISHNAME;
		
	/**
	 * 等级
	 */
	public static String CADENCE_PLM_GRADE;
	
	/**
	 * 等级内部名称
	 */
	public static String CADENCE_PLM_GRADE_NAME;
	
	/**
	 * 元件值或零部件型号
	 */
	public static String CADENCE_PLM_VALUE;
	
	/**
	 * 元件值
	 */
	public static String CADENCE_PLM_COMPONENT_VALUE;
	
	/**
	 * 零部件型号
	 */
	public static String CADENCE_PLM_COMPONENT_MODEL;
	
	/**
	 * 创建者
	 */
	public static String CADENCE_PLM_APPLICANT_NAME;
	
	/**
	 * PN版本
	 */
	public static String CADENCE_PLM_VER;
	
	/**
	 * 规格
	 */
	public static String CADENCE_PLM_MATERIAL_SPECIFICATION;
	
	/**
	 * 规格内部名称
	 */
	public static String CADENCE_PLM_MATERIAL_SPECIFICATION_NAME;
	
	/**
	 * 单元
	 */
	public static String CADENCE_PLM_UNIT;
	
	/**
	 * 板号内部名称
	 */
	public static String CADENCE_PLM_BOARD_NUMBER;
	
	/**
	 * Cadence字段，原理图符号名
	 */
	public static String CADENCE_PLM_SCHEMATIC_PART;
	
	/**
	 * Cadence字段，PCB封装
	 */
	public static String CADENCE_PLM_OLD_FOOTPRINT;
	
	/**
	 * 名称
	 */
	public static String CADENCE_PLM_MATERIAL_NAME;
	
	/**
	 * 元件的规格书“Datasheet”链接
	 */
	public static String CADENCE_PLM_DATASHEET1;
	
	/**
	 * 链接地址
	 */
	public static String CADENCE_PLM_DATASHEET1_SRC;
	
	/**
	 * 等级值1
	 */
	public static String CADENCE_PLM_GRADE_VALUE1;
	
	/**
	 * 等级2
	 */
	public static String CADENCE_PLM_GRADE_VALUE2;
	
	/**
	 * 生命周期状态
	 */
	public static String CADENCE_PLM_STATE;
	
	/**
	 * 描述字段组合规则
	 */
	public static String CADENCE_PLM_DESCRIPTION_RULE;
	
	static {
		InputStream inStream = CadenceConfConstant.class
				.getResourceAsStream("/com/catl/cadence/conf/conf.properties");
		try {
			properties.load(inStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TASK_W4C_JDBCADAPTER_SERVICES_NAME = get("task.w4c.jdbcAdapter.servicesname");
		TASK_DATABASE_USERNAME=get("task.database.username");
		
		CATL_T_CADENCE_PARTS = get("cadence.tablename.attributetable");
		CATL_T_CADENCE_PARTS2 = get("cadence.tablename.attributetable2");
		CATL_T_CADENCE_PCB_OTHER = get("cadence.tablename.attributetable3");
		TABLE_SUFFIX=get("cadence.create.table.suffix");
		
		AUTOMOTIVE_PARTS = get("cadence.viewname.attributetable1");
		INDUSTRY_PARTS = get("cadence.viewname.attributetable2");
		PCB_OTHER_PARTS = get("cadence.viewname.attributetable3");
		
		TASK_CREATE_ATTRTABLE = get("task.createTable.url");
		TASK_GET_NEWCOLUMN = get("task.addColumnname.url");
		TASK_ADD_TABLECOLUMN = get("task.addTableColumn.url");
		TASK_CREATE_RECORD=get("task.createRecord.url");
		TASK_CREATE_GRANT=get("task.grantSynonym.url");
		
		CADENCE_PLM_PART_NUMBER=get("cadence.plm.part_number");
		CADENCE_PLM_DESCRIPTION=get("cadence.plm.description");
		CADENCE_PLM_PART_TYPE=get("cadence.plm.part_type");
		CADENCE_PLM_ENGLISHNAME=get("cadence.plm.part_type_name");
		CADENCE_PLM_GRADE=get("cadence.plm.grade");
		CADENCE_PLM_GRADE_NAME=get("cadence.plm.grade_name");
		CADENCE_PLM_VALUE=get("cadence.plm.value");
		CADENCE_PLM_COMPONENT_VALUE=get("cadence.plm.component_value");
		CADENCE_PLM_COMPONENT_MODEL=get("cadence.plm.component_model");
		CADENCE_PLM_APPLICANT_NAME=get("cadence.plm.applicant_name");
		CADENCE_PLM_VER=get("cadence.plm.ver");
		CADENCE_PLM_MATERIAL_SPECIFICATION=get("cadence.plm.material_specification");
		CADENCE_PLM_MATERIAL_SPECIFICATION_NAME=get("cadence.plm.material_specification_name");
		CADENCE_PLM_UNIT=get("cadence.plm.unit");
		CADENCE_PLM_BOARD_NUMBER=get("cadence.plm.board_number");
		CADENCE_PLM_SCHEMATIC_PART=get("cadence.plm.schematic_part");
		CADENCE_PLM_OLD_FOOTPRINT=get("cadence.plm.old_footprint");
		CADENCE_PLM_MATERIAL_NAME=get("cadence.plm.material_name");
		CADENCE_PLM_DATASHEET1=get("cadence.plm.datasheet1");
		CADENCE_PLM_DATASHEET1_SRC=get("cadence.plm.datasheet1_src");
		CADENCE_PLM_GRADE_VALUE1=get("cadence.plm.grade_distinction1");
		CADENCE_PLM_GRADE_VALUE2=get("cadence.plm.grade_distinction2");
		CADENCE_PLM_STATE=get("cadence.plm.state");
		CADENCE_PLM_DESCRIPTION_RULE=get("cadence.plm.description.rule");
	}
	
	public static String get(String key){
		return properties.getProperty(key);
	}
}
