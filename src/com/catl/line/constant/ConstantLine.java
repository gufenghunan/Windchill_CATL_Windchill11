package com.catl.line.constant;
import java.util.Properties;
/**
 * 定义需要使用的常量
 * @author hdong
 */
public class ConstantLine extends Properties{
	public static String route_normal="普通";
	public static String route_lineasm="线束总成";
	
	public static String queue_dwgtopdf="DWGTransferPDFQueue";
	//正式-
	public static String var_cablecount="CATL_Cablecount";   
	public static String var_maxcablesection="Maximum_Section_Area";
	public static String var_undercablecount="CATL_Undercablecount";
	public static String var_ldconnector="CATL_Ldconnector";
	public static String var_parentPN="CATL_ParentPN";
	public static String var_lconnector="CATL_Lconnector";
	public static String var_rconnector="CATL_Rconnector";
	public static String var_linetype="CATL_Linetype";
	public static String var_rdconnector="CATL_Rdconnector";	
	
	public static String var_PN="PN号";
	
	public static String var_L="CATL_L";	
	public static String var_L1="CATL_L1";	
	public static String var_L2="CATL_L2";	
	public static String var_L3="CATL_L3";	
	public static String var_llbenchmark="CATL_Llbenchmark";
	public static String var_ltagbox="CATL_Ltag_Box";
	public static String var_rtagbox="CATL_Rtag_Box";
	public static String var_dtagdesc="CATL_Dtag_Desc";
	public static String var_ltagdesc="CATL_Ltag_Desc";
	public static String var_rtagdesc="CATL_Rtag_Desc";
	public static String var_pointa="CATL_PointA";
	public static String var_pointb="CATL_PointB";
	public static String var_pointc="CATL_PointC";
	public static String var_pointd="CATL_PointD";
	public static String var_mtag_content="CATL_Mtag_Content";
	public static String var_HeatShrinkableCasing_color="CATL_HeatShrinkableCasing_color";

	public static String var_maturity="CATL_Maturity";//成熟度
	public static int    var_basicmaturitylevel=3;//成熟度最低等级//###
	public static String var_condition="条件";
	public static String var_mathrule="计算规则";
	
	public static String config_connectorcount="connectorcount";	
	public static String libary_lineparentpn="母PN库";
	public static String judgeparentPN="是";

	//异常
	public static String exception_partnotfound="根据编号未查询到部件";
	public static String exception_docnotfound="根据编号未查询到相关文档";
	public static String exception_notmodifyaccess="没有修改权限";
	public static String exception_fillerrormsg="填写错误";
	public static String exception_cannotmath="根据配置文件计算不出用量!";
	public static String exception_errormath="根据配置文件计算出用量为负数!";
	public static String exception_cannotfoundpagenum="找不到图框中的页码";
	public static String exception_numberdocnotfound="找不到编码为#的文档";
	public static String exception_bellowscounterror="包含两个以上的波纹管!";
	public static String exception_lineasmnumber="请输入符合线束总成编码规则的编码!";
	public static String exception_partstateeror="线束总成必须在设计或设计修改状态!";
	//标签配置文件
	public static String dtag="下标签";
	public static String col_desc="描述";
	public static String col_linetype="线束类型";
	public static String col_recommend="推荐值";
	//
	public static String dwg_page_num="页码";
	public static String dwg_print_frame="打印图框";
	public static String dwg_page_num_sign="OF";
	//public static String dwg_temp_path="";
	public static String dwg_temp_path="/codebase/config/custom/temp/dwg/";
	public static String dwg_default_font="宋体";
	public static String[] dwg_font_filename={"宋体|simsun.ttc","华文中宋_文字|STZHONGS.TTF","华文中宋_标注|STZHONGS.TTF"};
	public static String dwg_model_space="*Model_Space";
	
	public static int limit_childpn=50;
	public static String var_initmaturity="3";
	public static String var_childpn_initstate="DESIGN";
	public static String state_design="DESIGN";
	public static String state_desginmodify="DESIGNMODIFICATION";
	public static String var_rule_key="名称";
	
	//正式-
	public static String type_integer_attr="CATL_PointA,CATL_PointB,CATL_PointC,CATL_PointD";
	public static String type_double_attr="CATL_L1,CATL_L2,CATL_L3";
	public static String box_customer_config_path="/codebase/config/custom/config/box_customer_config.xls";
	public static String box_customer_config_name="box_customer_config.xls";
	
	public static String box_explain_config_path="/codebase/config/custom/config/box_explain_config.xlsx";
	public static String box_explain_config_name="box_explain_config.xlsx";
	public static String box_explain_config_name_prefix="box_explain_config";
	public static String box_explain_config_name_suffix=".xlsx";
	
	public static String box_explain_config_pdf_prefix="box_explain_config";
	public static String box_explain_config_pdf_suffix=".pdf";
	
	public static String box_explain_PackageAsk="CATL_PackageAsk";//装箱要求
	public static String box_explain_ProjectName="CATL_ProjectName";
	public static String box_explain_ProductPN="CATL_ProductPN";
	public static String box_explain_CustomCode="CATL_CustomCode";
	public static String box_explain_CustomProjectCode="CATL_CustomProjectCode";
	public static String box_explain_Remark="CATL_PackAskRemark";
	
	public static String config_sendtrp_path="/codebase/config/custom/config/config_send_trp.xls";
	
	public static String file_temp = "/codebase/temp/";
	public static String config_connector_bom_sheetname="两头线束图纸填写,三头线束图纸填写,两头线束BOM用量,三头线束BOM用量";
	public static String var_doctype_autocadDrawing="wt.doc.WTDocument|com.CATLBattery.CATLDocument|com.CATLBattery.autocadDrawing";
	public static String config_2connector_sheetname="两头线束图纸填写";
	public static String config_3connector_sheetname="三头线束图纸填写";
	public static String config_2use_sheetname="两头线束BOM用量";
	public static String config_3use_sheetname="三头线束BOM用量";
	//区分波纹管物料组
	public static String config_bom_type1="1302";
	public static String config_bom_type1_name="波纹管";
	public static String config_bom_type1_name1="波纹管大";
	public static String config_bom_type1_name2="波纹管小";
	//区分导线物料组
	public static String config_bom_type2="1401";
	public static String config_bom_type2_name="导线";
	//区分亚银标签物料组
	public static String config_bom_type3="5702";
	public static String config_bom_type3_name="哑银标签";
	//区分波纹管大小属性值
	public static String var_inner_diameter="Inner_Diameter";
	public static String config_clf="分类";
	public static String config_mpnuse="母PN用量";
	public static String config_bellows="波纹管类型";
	
	public static String config_batchcreate_internal_name="CATL转化线长L/mm\n(尾部到尾部距离)|CATL_L1,客户线长基准|CATL_Llbenchmark,选项1|CATL_Ltag_Box,选项2|CATL_Ltag_Desc,选项3|CATL_Rtag_Box,选项4|CATL_Rtag_Desc,定位点A\n(mm)|CATL_PointA,定位点B\n(mm)|CATL_PointB,定位点C\n(mm)|CATL_PointC,定位点D\n(mm)|CATL_PointD";
	
	//监听过滤转PDF的编码前缀
	public static String config_topdf_number_prefix="FC";
	
	public static String var_iscreateboxexplain="CATL_IsCreateBoxExplain";
	
	//母PN属性合法值配置
	public static String []config_values={"线束类型|高压线束,低压线束,加热线束"};
	public static String []config_group_otherattr={var_parentPN};
	public static String []config_group_parentpn={var_linetype,var_lconnector,var_rconnector,var_cablecount,var_maxcablesection,var_ldconnector,var_rdconnector,var_undercablecount};
	public static String []config_group_required_parentpn={var_cablecount,var_maxcablesection,var_lconnector,var_rconnector,var_linetype};
	public static String []config_group_childpn={var_L,var_L1,var_L2,var_L3,var_llbenchmark,var_ltagbox,var_rtagbox,var_ltagdesc,var_rtagdesc,var_dtagdesc,var_pointa,var_pointb,var_pointc,var_pointd,var_mtag_content};
	public static String[]config_group_childpn_specification={var_maxcablesection,var_linetype,var_L,var_L1,var_L2,var_L3,var_mtag_content,var_lconnector,var_rconnector,var_ldconnector,var_rdconnector,var_llbenchmark,var_ltagbox,var_ltagdesc,var_rtagbox,var_rtagdesc,var_dtagdesc,var_pointa,var_pointb,var_pointc,var_pointd,var_cablecount};
	public static String route_mpn="母PN";
	public static String route_pn="PN";
	
	//签名配置
	public static String []config_signature={"设计者|设计时间","技术校核人|技术校核时间","审核人|审核时间","批准者|批准时间"};
	public static String []config_signature_activity={"提交","校对","SE审核","部门批准"};
	public static String config_nosignature_state="已发布";
	public static String config_signature_dataformat="yyyy.MM.dd";
	
	//ui filter配置
	public static String config_filter_updatechildpn_state="设计,设计修改";
	public static String config_createchildpn_folder="零部件";
	
	public static String str_msg="msg";
	public static String str_data="data";
	public static String str_url="url";
	public static String str_newnum="newnum";
	/**
	 * success
	 */
	public static String str_success = "success";
	/**
	 * fail
	 */
	public static String str_fail = "fail";
	/**
	 * subCategory
	 */
	public static String doc_iba_subCategory = "subCategory";
	
	/**
	 * 线束AUTOCAD图纸
	 */
	public static String doc_iba_subCategory_autocad = "线束AUTOCAD图纸";
	
	public static String base_temp="/codebase/config/custom/temp/";
	
	public static String createDocforPartClf="5501,5502,5506,5507,5508";
	
	public static String config_attach_containername="";
	
	
	/**
	 * ftp连接配置
	 */
	//public static String ftp_url="172.26.118.244";//### 172.26.118.244
	public static String line_ftp_url="172.26.118.244";//### 172.26.118.244
	public static String tomcat_port="8080";//###8080
	public static String ftp_user="line";
	public static String ftp_pwd="Plm20170516";
	public static String ftp_dir="test";
	public static String var_clf="cls";
	
	//批量创建的配置
	public static String var_englishname="englishName";
	public static String var_oldpartnumber="oldPartNumber";
	public static String config_released="已发布";
	
	public static String var_forcecreate="createDuplicatePart";
	public static String config_validate_rstgcolor="红红:正、总正、总正1、总正2、总正3、总正4:正|黑红:负:正|黑黑:负:负、总负、总负1、总负2、总负3、总负4";
	public static String config_rds_part_attr="CELL_CAPACITY|Cell_Capacity,CELL_MODEL|Cell_Mode,NOMINAL_VOLTAGE|Nominal_Voltage,PRODUCET_ENERGY|Product_Energy,MODULE_QUANTITY|Module_Quantity|int,MATERIAL_LENGTH|Length,MATERIAL_WIDTH|Width,MATERIAL_HIGTH|Height,CELLCONNECTION_MODEL|Cell_Connection_Mode";
	public static String config_rds_time_format="yy/MM/dd HH:mm:ss";
	
	
	
	
}
