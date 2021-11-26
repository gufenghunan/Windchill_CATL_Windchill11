package com.catl.pd.constant;


public class ConstantPD {
	public static String pd_group_admin="PD配置管理员组";
	//缓存中最大允许的数目
	public static int config_cache_limit_count=10;
	public static String base_pd_path="/codebase/config/temp/pd/";
	public static String base_pd_bak_path="/codebase/config/encrypt/pd/bak/";
	public static String config_pdmath_xls="/codebase/config/encrypt/pd/pd_math.xlsx";
	public static String config_path_htmltemplate="/codebase/netmarkets/jsp/catl/pd/template/";
	public static String config_path_material_xlsx="/codebase/config/encrypt/pd/pd_material.xlsx";
	public static String config_comparesummary_template="/codebase/config/custom/config/config_comparesummary.xlsx";
	public static String config_recipedocname="有效配方";
	
	public static String WTDOCUMENT_TYPE="wt.doc.WTDocument|com.CATLBattery.BatteryDocument|com.CATLBattery.PDIntelligentDocument";
	public static String part_type="wt.part.WTPart|com.CATLBattery.CATLPart";
	
	public static String config_export_name="配置信息导出";
	public static String config_export_comparesummary="Summary比较";
	
	public static String DOC_FOLDER_NAME="/计算结果";
	public static String PART_FOLDER_NAME="/零部件";
	public static String phantom_folderpath="/XXX/虚拟件";
	public static String config_Phantom_folder="虚拟件";
	
	public static String config_libary_pdmaterial="电芯材料库";
	
	public static String config_recipenumber="配方号";
	public static String config_materialname="材料名称";
	public static String config_materialpn="PN";
	public static String config_loadding="比例";
	public static String config_pd_xls_path="/codebase/config/custom/config/pd_config.xls";
	public static String config_exportfile_sheetname="参数导出配置";

	public static String config_material_type="类型";
	public static String config_material_region="位置";
	public static String config_material_name="名称";
	public static String config_material_style="显示格式";
	
	public static String config_asm_type="类型";
	public static String config_asm_region="位置";
	public static String config_asm_attr="属性";
	public static String config_asm_style="显示格式";
	
	public static String config_downDropdown_name="名称";
	public static String config_downDropdown_region="区域";
	public static String config_downDropdown_formula="公式";
	
	public static String config_colorconfig_name="名称";
	public static String config_colorconfig_region="区域";
	public static String config_colorconfig_while="条件";
	public static String config_colorconfig_color="颜色";
	
	public static String config_exportconfig_name="名称";
	public static String config_exportconfig_region="区域";
	public static String config_exportconfig_value="参数值";
	public static String config_exportconfig_paramname="参数名";
	
	public static String config_defaultconfig_cell="单元格";
	
	public static String config_validateconfig_name="名称";
	public static String config_validateconfig_condition="错误条件";
	public static String config_validateconfig_error="错误";
	
	public static String config_type_string="字符串";
	public static String config_type_double="实数";
	
	public static String config_comparesummary_title="D3";
	public static String config_comparesummary_r1="B4";
	public static String config_comparesummary_r2="D55";
	public static int config_comparesummary_startcol=1;
	public static int config_comparesummary_endcol=3;
	public static int config_comparesummary_interval=3;
	
	public static String pd_encrypt_hsitory_password="wcadmin123";//历史密码记录，避免临时目录文件存在的文件不能使用新密码
	public static String pd_encrypt_password="wcadmin123";
	
	public static String str_msg="msg";
	public static String str_data="data";
	public static String str_url="url";
	public static String str_success="success";
	public static String str_fail="fail";
	public static String config_searchclfs="Anode,Cathode,Separator,Electrolyte,CuFoil,AlFoil,CathodeBinder,AnodeBinder,ConductiveCarbon,CMC,CCS,PCS,E028";
	
	public static String [] config_sheetname={"材料数据库","机械件数据库","设计主界面","模切尺寸","Overhang","BOM","极耳错位","Summary","Cell Weight","残空间计算"};
	
	public static int config_limitexcel_rowcount=200;
	public static int config_limit_colcount=30;
	
	public static String[] config_special_str={"#N/A","#VALUE!","#REF!","#DIV/0!"};

	/**
	 * 分类属性PN码内部名称
	 */
	public static String iba_attr_pn="CATL_PN";
	public static String config_pdasm_clf="MECHASM";
//	public static String config_pdasm_clf="BATTERYASM";
//	public static String iba_attr_pn="CTAL_PN";
	public static String config_pd_iba_speicalkey="Special_Explanation";
	public static String config_compareversion_cell="设计主界面!O5";
	public static String config_pd_temp="/codebase/config/custom/temp/pd/";
	public static String config_org_storetemplate="/Default/策略";
	public static String TEMPLATE_WTDOCUMENT_TYPE="wt.doc.WTDocument|com.CATLBattery.BatteryDocument|com.CATLBattery.IntelligentDocumentTemplate";
	public static String config_pdmath_name="PD设计表模版";
}
