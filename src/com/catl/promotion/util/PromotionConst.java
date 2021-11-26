package com.catl.promotion.util;

public class PromotionConst {
	
	public static final String RESOURCE = "com.catl.promotion.resource.promotionResource";
	
	//物料设计禁用流程
	public final static String design_disable_pn = "design_disable_pn";//物料设计禁用流程ID
	public final static String design_disable_submit = "design_disable_submit";//物料设计禁用流程节点ID:提交设计禁用
	public final static String design_disable_confirm = "design_disable_confirm";//物料设计禁用流程节点ID:确认设计禁用
	public final static String design_disable_engineer = "design_disable_engineer";//物料设计禁用流程节点ID:设计工程师会签
	public final static String design_disable_pmc = "design_disable_pmc";//物料设计禁用流程节点ID:PMC会签
	public final static String design_disable_src = "design_disable_src";//物料设计禁用流程节点ID:SRC会签
	public final static String design_disable_other = "design_disable_other";//物料设计禁用流程节点ID:其他会签
	public final static String design_disable_exception = "design_disable_exception";//物料设计禁用流程节点ID:处理设计禁用集成异常
	public final static String design_disable_update = "design_disable_update";//物料设计禁用流程节点ID:更新电子元器件库
	
	//采购类型变更单
	public final static String source_change_pn = "source_change_pn";//采购类型变更流程ID
	public final static String source_change_submit = "source_change_submit";//采购类型变更流程节点ID:提交设计禁用
	public final static String source_change_src = "source_change_src";//采购类型变更流程节点ID:SRC会签
	public final static String source_change_ie = "source_change_ie";//采购类型变更流程节点ID:IE会签
	public final static String source_change_pmc = "source_change_pmc";//采购类型变更流程节点ID:PMC会签
	public final static String source_change_exception = "source_change_exception";//采购类型变更流程节点ID:处理设计禁用集成异常
	

	//角色
	public static final String SUBMITTER = "SUBMITTER";// 提交者
	public static final String PRODUCT_DATA_ENGINGEER = "PRODUCT_DATA_ENGINGEER";// 产品数据工程师
	public static final String CATL_DESIGN_ENGINGEER = "CATL_DESIGN_ENGINGEER";// 设计会签者
	public static final String MATERIAL_CONTROL = "MATERIAL_CONTROL";// 物料控制专员
	public static final String PROCUREMENT_REPRESENT = "PROCUREMENT_REPRESENT";// 采购代表
	public static final String DESIGNER = "DESIGNER";// 设计者
	public static final String DEPARTMENT_MANAGER = "DEPARTMENT_MANAGER";    //部门经理
	public static final String SYSTEM_ENGINEER="SYSTEM_ENGINEER";//系统工程师
	public static final String COUNTERSIGN_PEOPLE="COUNTERSIGN_PEOPLE";//会签者
	public static final String QUALITY_REPRESENTATIVE="QUALITY_REPRESENTATIVE";//质量代表
	
	//流程变量
	public static final String IS_APPROVE = "isApprove";
	public static final String isElectricParts = "isElectricParts";
	public static final String BOMReport = "BOMReport";
	public static final String Temp = "temp";
	public static final String failNumber = "failNumber";
	public static final String errorMessage = "errorMessage";
	public static final String Part = "Part";
	public static final String updatedMaturityParts = "updatedMaturityParts";
	public static final String sendErpFailedParts = "sendErpFailedParts";
	
	//群组
	public static final String PDM_Department_Member_GROUP = "BOM报表查看组";//BOM报表查看组

	public static final String platform_change_submit = "platform_change_submit";
	public static final String platform_change_audit = "platform_change_audit";
	public static final String platform_change_pn="platform_change_pn";
}
