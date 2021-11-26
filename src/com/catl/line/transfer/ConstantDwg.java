package com.catl.line.transfer;
/**
 * 加载constant.properties的配置文件
 * 定义需要使用的常量
 * @author hdong
 */
public class ConstantDwg{
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
	//
	public static String dwg_page_num="页码";
	public static String dwg_print_frame="打印图框";
	public static String dwg_page_num_sign="OF";
	//public static String dwg_temp_path="";
	public static String dwg_temp_path="/codebase/config/custom/temp/dwg/";
	public static String dwg_default_font="宋体";
	public static String[] dwg_font_filename={"宋体|simsun.ttc","华文中宋_文字|STZHONGS.TTF","华文中宋_标注|STZHONGS.TTF"};
	public static String dwg_model_space="*Model_Space";
	public static String dwg_not_explode="波纹管大,波纹管小";
	
	//ftp配置
	public static String ftp_url="172.26.118.244";//172.26.118.244
	//public static String ftp_url="172.26.118.244";//172.26.118.244
	public static String ftp_user="line";
	public static String ftp_pwd="Plm20170516";
	public static String dwg_localpath="D:\\line\\temp";
	
}
