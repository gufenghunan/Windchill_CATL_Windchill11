package com.catl.ri.riB.service;

import java.beans.PropertyVetoException;
import java.io.FileNotFoundException;
import java.io.IOException;

import wt.util.WTException;
import wt.util.WTRuntimeException;
import net.sf.json.JSONArray;

public interface SelectMaterialService {
	/**
	 * 读取电芯产品库下的配方配置文件
	 * 根据配方号recipenumber 获取材料信息 
	 * 根据材料信息中的编码查询材料库中信息
	 * 如果不存在材料则抛出异常
	 * 如果存在则配方表和查询的信息整理成JSONArray返回
	 * @param recipenumber
	 * @return
	 * @throws WTException 
	 * @throws PropertyVetoException 
	 * @throws IOException 
	 */
	JSONArray getRecipeJson(String containerOid,String recipenumber) throws WTException, IOException, PropertyVetoException;
    
	/**
	 * 根据分类和类型搜索电芯材料库下的材料
	 * @param clf
	 * @return
	 * @throws WTException 
	 */
	JSONArray getSearchJson(String clf) throws WTException;
    
	/**
	 * 根据部件oid获取部件需要填入表格的json信息
	 * @param clf
	 * @return
	 * @throws WTRuntimeException 
	 * @throws WTException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	JSONArray getPartRowJson(String oid) throws WTRuntimeException, WTException, FileNotFoundException, IOException;
	
	/**
	 * 根据材料获取所有的配方号
	 * @param materialNumber
	 * @return
	 * @throws Exception
	 */
	JSONArray getRecipenumbers(String containerOid,String materialNumber) throws Exception;
	
	/**
	 * 根据所填的值创建虚拟件
	 * @param jsonStr
	 * @throws Exception
	 */
	void createPhantom(String jsonStr) throws Exception;
    
	/**
	 * 获取配方材料的store
	 * @return
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws PropertyVetoException 
	 * @throws WTException 
	 */
	JSONArray getMaterialPNJson(String containerOid) throws FileNotFoundException, IOException, WTException, PropertyVetoException;

	String getRemarkByNumber(String value) throws WTException;

}
