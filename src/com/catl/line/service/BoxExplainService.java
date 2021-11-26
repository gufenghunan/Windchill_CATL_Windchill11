package com.catl.line.service;


import wt.util.WTException;
import wt.util.WTRuntimeException;
import net.sf.json.JSONArray;

public interface BoxExplainService {
	
	/**
	 * 获取客户
	 * @return
	 * @throws Exception
	 */
	JSONArray getCustomer() throws Exception;
	/**
	 * 获取包装要走求
	 * @param customer
	 * @return
	 * @throws Exception
	 */
	JSONArray getPackageAsk(String customer) throws Exception;
	/**
	 * 获取备注
	 * @param customer
	 * @return
	 * @throws Exception
	 */
	JSONArray getRemark(String customer) throws Exception;
	
	/**
	 * 获取装箱说明模版 客户输入
	 * @param customer
	 * @param oid 
	 * @return
	 * @throws Exception
	 */
	JSONArray getCustomerInput(String customer, String oid) throws Exception;
	
	/**
	 * 获取子件
	 * @param number
	 * @return
	 * @throws Exception
	 */
	JSONArray getChildPN(String number) throws Exception;
	
	/**
	 * 创建装箱说明
	 * @param boxExplainFromPage
	 * @return
	 * @throws Exception
	 */
	String createBoxExplain(String boxExplainFromPage) throws Exception;
	/**
	 * 获取线束总成的信息
	 * @param boxExplainFromPage
	 * @return
	 * @throws WTException 
	 * @throws WTRuntimeException 
	 * @throws Exception
	 */
	JSONArray getAsmInfo(String oid) throws WTRuntimeException, WTException;
}
