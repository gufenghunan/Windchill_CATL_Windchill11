package com.catl.ri.riB.service;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.springframework.web.multipart.MultipartFile;

import wt.util.WTException;
import wt.util.WTRuntimeException;



public interface RIService {
	
	/**
	 * 导出参数对应excel
	 * 临时文件目录包含用户名文件夹
	 * 如果临时文件夹此用户文件夹存在导出表则删除
	 * 按照参数对应表找到参数值生成表格
	 *  返回链接位置
	 * @return
	 * @throws Exception
	 */
	String exportExcel() throws Exception;
	
	/**
	 * 新建文档存于机械件组合所在目录平级目录计算结果文件夹
	 * 生成的excel需要清除计算公式
	 * 创建文档主内容所在临时目录为ConstantRI.base_ri_path/当前用户名/文档名称.xls
	 * 将excel文档名称中的XXX转换为流水码，编码为临时编码。创建成功后删除临时文件
	 * @param asmnumber
	 * @param folderOid2 
	 * @return
	 * @throws Exception
	 */
	void outputDoc(String wtDocOid,String description,String name,String level,String remark, String folderOid) throws Exception;
	/**
	 * 判断目录ConstantRI.base_ri_path/当前用户是否存在
	 * 如果存在且包含xls文件则返回字符串"已存在"
	 * 如果不存在则创建目录ConstantRI.base_ri_path/当前用户
	 * 拷贝ConstantRI.config_rimath_xls到目录ConstantRI.base_ri_path/当前用户
	 * 重命名文件名为name+"_XXX"+level+remark返回空字符串
	 * @return
	 * @throws Exception
	 */
	String createDesignExcel(String wtDocOid,String oldname, String oldlevel,
			String oldremark,String name,String level,String remark) throws Exception;
	
	/**
	 * 获取Excel模版的字符串数据
	 * @return
	 */
	String getDisplayHtml(String templatename);
	
	/**
	 * 将页面所填的信息jsonstr处理
	 * 按照配置表ConstantRI.config_riupdate_xls
	 * 将数据保存到临时目录下的excel,并更新excel计算
	 * @param sheetname
	 * @param jsonstr
	 */
	void updateDesignExcel(String sheetname,String jsonstr);
	
	/**
	 * 针对再次设计，点击完成时对名称更新
	 * 根据oid查询出文档。如果文档名称中level改变则创建新文档
	 * 否则更新文档名称
	 * @param oid
	 * @param name
	 * @param level
	 * @param remark
	 */
	void updateDocName(String oid,String name,String level,String remark);

	/**
	 * 将界面数据写入到临时文件对应的sheet中并强制执行计算公式
	 * @param jsonstr
	 * @param sheetname
	 * @param sheetname2 
	 * @param jsonstr2 
	 * @param remark 
	 * @param formcheck 
	 * @throws Exception 
	 */
	void submitForm(String wtDocOid,String name,String level,String remark,String jsonstr, String sheetname, boolean formcheck) throws Exception;

	/**
	 * 获取页面数据显示
	 * @param sheetname
	 * @return
	 * @throws IOException 
	 * @throws WTException 
	 * @throws PropertyVetoException 
	 * @throws WTRuntimeException 
	 * @throws ScriptException 
	 * @throws GeneralSecurityException 
	 * @throws Exception 
	 */
	JSONArray getPageJson(String wtDocOid,String name, String level, String remark,String sheetname) throws IOException, WTException, WTRuntimeException, PropertyVetoException, ScriptException, GeneralSecurityException, Exception;

	/**
	 * 根据文档Oid获取文件信息，下载主内容到缓存目录
	 * @param wtDocOid
	 * @return JSONArray
	 * @throws Exception
	 */
	JSONArray updateRI(String wtDocOid) throws Exception;
    
	/**
	 * 将缓存写入到本地文件
	 * @param wtDocOid
	 * @return JSONArray
	 * @throws WTException 
	 * @throws IOException 
	 * @throws PropertyVetoException 
	 * @throws WTRuntimeException 
	 * @throws Exception
	 */
	void saveDesign(String wtDocOid,String sheetname,String jsonstr,String name, String level, String remark) throws WTException, IOException, WTRuntimeException, PropertyVetoException, Exception;

	/**
	 * 更新当前页面数据
	 * @param wtDocOid
	 * @param sheetname
	 * @param name
	 * @param level
	 * @param remark
	 * @throws PropertyVetoException 
	 * @throws IOException 
	 * @throws WTException 
	 * @throws WTRuntimeException 
	 * @throws Exception 
	 */
	JSONArray updatePageJson(String wtDocOid,String jsonstr,String sheetname, String name,String level, String remark) throws WTRuntimeException, WTException, IOException, PropertyVetoException, Exception;

	/**
	 * 获取当前用户所有文件的名称
	 * @return JSONArray
	 * @throws Exception
	 */
	JSONArray getUserAllFileName() throws Exception;

	/**
	 * 页面单元格改变后，获取后台公式数据和它本身的数据信息
	 * @param wtDocOid
	 * @param region
	 * @param value
	 * @param name
	 * @param level
	 * @param remark
	 * @param sheetname
	 * @return
	 * @throws Exception
	 */
	JSONArray getMathData(String wtDocOid, String region,String value,String name,String level, String remark, String sheetname) throws  Exception;

	/**
	 * 根据当前的值获取相应默认值
	 * @param currentValue
	 * @return
	 * @throws Exception
	 */
	JSONArray getDefaultValue(String wtDocOid,String name,String level,String remark,String currentValue) throws Exception;

	void submitvalidateform(String wtDocOid, String name, String level,String remark) throws IOException, WTException, ScriptException, Exception;

	void syncTemp(String filename) throws Exception;

	void exportSheet(String wtDocOid, String name, String level, String remark,
			String sheetname, HttpServletRequest request,
			HttpServletResponse response) throws Exception; 
}
