package com.catl.ri.constant;


public class ConstantRI {
	public static String ri_group_adminA="RI配置管理员A组";
	public static String ri_group_adminB="RI配置管理员B组";
	public static String ri_speical_document="特殊设计表文件";
	//缓存中最大允许的数目
	public static int config_cache_limit_count=10;
	public static String base_ri_pathA="/codebase/config/temp/ri/A/";
	public static String base_ri_pathB="/codebase/config/temp/ri/B/";
	public static String base_ri_bak_pathA="/codebase/config/encrypt/bak/RIA/";
	public static String base_ri_bak_pathB="/codebase/config/encrypt/bak/RIB/";
	public static String config_rimathA_xls="/codebase/config/encrypt/RIA/ri_math.xlsx";
	public static String config_rimathB_xls="/codebase/config/encrypt/RIB/ri_math.xlsx";
	public static String config_path_htmltemplateA="/codebase/netmarkets/jsp/catl/ri/A/template/";
	public static String config_path_htmltemplateB="/codebase/netmarkets/jsp/catl/ri/B/template/";
	public static String config_path_material_xlsxA="/codebase/config/encrypt/RIA/ri_A_material.xlsx";
	public static String config_path_material_xlsxB="/codebase/config/encrypt/RIB/ri_B_material.xlsx";
	public static String config_ri_xls_pathA="/codebase/config/custom/config/ri_A_config.xls";
	public static String config_ri_xls_pathB="/codebase/config/custom/config/ri_B_config.xls";
	
	public static String config_ri_temp="/codebase/config/custom/temp/ri/";//上传配置文件的临时目录
	public static String config_recipedocname="有效配方";
	
	public static String WTDOCUMENT_TYPEA="wt.doc.WTDocument|com.CATLBattery.BatteryDocument|com.CATLBattery.RIAIntelligentDocument";
	public static String WTDOCUMENT_TYPESPEICAL="wt.doc.WTDocument|com.CATLBattery.BatteryDocument|com.CATLBattery.SpeicalIntelligentDocument";
	public static String WTDOCUMENT_TYPEB="wt.doc.WTDocument|com.CATLBattery.BatteryDocument|com.CATLBattery.RIBIntelligentDocument";
	public static String part_type="wt.part.WTPart|com.CATLBattery.CATLPart";
	public static String config_export_comparesummary="Summary比较";
	public static String DOC_FOLDER_NAMEA="/电芯设计";
	public static String fodera_description="A版设计表文件夹";
	public static String DOC_FOLDER_NAMEB="/电芯设计";
	public static String foderb_description="B版设计表文件夹";
	
	public static String phantom_folderpath="/XXX/虚拟件";
	public static String config_Phantom_folder="虚拟件";
	
	public static String config_libary_rimaterial="电芯RI材料库";
	public static String config_libary_asmfolder="RI机械件组合";
	
	public static String config_recipenumber="配方号";
	public static String config_materialname="材料名称";
	public static String config_materialpn="PN";
	public static String config_loadding="比例";

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
	
	public static String ri_encrypt_hsitory_password="wcadmin123";//历史密码记录，避免临时目录文件存在的文件不能使用新密码
	public static String ri_encrypt_password="catl";
	
	public static String str_msg="msg";
	public static String str_data="data";
	public static String str_url="url";
	public static String str_success="success";
	public static String str_fail="fail";
	public static String config_searchclfsA="Anode,Cathode,Separator,Electrolyte,CuFoil,AlFoil,CathodeBinder,AnodeBinder,ConductiveCarbon,CMC,CCS,PCS,E028";
	public static String config_searchclfsB="Anode,Cathode,Separator,Electrolyte,CuFoil,AlFoil,CathodeBinder,AnodeBinder,ConductiveCarbon,CMC,CCS,PCS,E028";
	public static String config_other_excipients="CathodeBinder,AnodeBinder,ConductiveCarbon,CMC,CCS,PCS,E028";
	public static String [] config_sheetnameA={"材料数据库","机械件数据库","设计主界面","模切尺寸","Overhang","Summary","BOM","极耳错位","Cell Weight","残空间计算"};
	public static String[] config_sheetnameB={"04.设计界面","09.材料数据库","10.机械件数据库","11.模切尺寸","13.Overhang","08.BOM","12.残空间计算","14.极耳错位","16.Cell Weight","05.设计输出","02.平均电压参考","03.0.04C三电极数据","06.DCR map","07.ALP map"};
	public static int config_limitexcel_rowcount=200;
	public static int config_limit_colcount=30;
	
	public static String[] config_special_str={"#N/A","#VALUE!","#REF!","#DIV/0!"};

	/**
	 * 分类属性PN码内部名称
	 */
	public static String iba_attr_pn="CATL_PN";
	public static String iba_attr_otherexcipients="OtherExcipients";
	public static String config_riasm_clf="MECHASM";
//	public static String config_riasm_clf="BATTERYASM";
//	public static String iba_attr_pn="CTAL_PN";
	public static String config_ri_iba_speicalkey="Special_Explanation";
	public static String config_compareversion_cellA="设计主界面!O5";
	public static String config_compareversion_cellB="04.设计界面!O5";
	
	public static String config_org_storetemplate="/Default/策略";
	public static String TEMPLATE_WTDOCUMENT_TYPE="wt.doc.WTDocument|com.CATLBattery.BatteryDocument|com.CATLBattery.IntelligentDocumentTemplate";
	public static String config_rimath_nameA="RI设计表PSA&MQB模版";
	public static String config_rimath_nameB="RI设计表PT270模版";
	public static String speical_group_role="CATL_SPEICAL_INTELLIGENT_CREATOR";
}
