package com.catl.line.helper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.drools.core.util.StringUtils;

import com.catl.line.constant.ConstantLine;
import com.catl.line.constant.GlobalData;
import com.catl.line.exception.LineException;
import com.catl.line.util.ExpressLineInfo;
import com.catl.line.util.IBAUtil;
import com.catl.line.util.IBAUtility;

import wt.part.WTPart;
import wt.util.WTProperties;

public class ExpressHelper {
	public static String wt_home;
	static {
		try {
			wt_home = WTProperties.getLocalProperties().getProperty("wt.home", "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws FileNotFoundException, IOException, ScriptException {
		Map map=new HashMap();
		String str="{定位点A=111.0, 线束类型=高压线束, 导线总根数=3.0, 最大导线截面积=12.0, 左标签箱体=2号电箱, 定位点D=0.0, L1=100.0, 左标签描述=负, 定位点C=0.0, 接插件右=低压连接器插头, L2=0.0, L3=0.0, 定位点B=222.0, 下标签描述=, 右标签箱体=1号电箱, 右标签描述=负, 接插件左下=, 接插件左=低压连接器插头, 导线下根数=12.0, 接插件右下=, 母PN=是}";
		map.put("线束类型", "高压线束");
		map.put("最大导线截面积", 12.0);
		map.put("左标签箱体", "2号电箱");
		map.put("L1", 500.0);
		map.put("左标签描述", "负");
		map.put("接插件右", "高压连接器插头");
		map.put("L2",300.0);
		map.put("L3", 400.0);
		map.put("下标签描述", "");
		map.put("右标签箱体", "2号电箱");
		map.put("右标签描述", "负");
		map.put("接插件左下", "高压连接器插头");
		map.put("接插件左", "高压连接器插头");
		map.put("接插件右下", "");
		map.put("定位点A", 111.0);
		map.put("定位点B", 222.0);
		map.put("定位点C", 0.0);
		map.put("定位点D", 0.0);
		map.put("PN", "adasds");
		map.put("母PN用量", 3);
		map.put("分类", "5702");
		double a=getUse(ConstantLine.config_3use_sheetname,map);
		System.out.println(a);
	}
    
	/**
	 * 获取填写值经过配置文件后得到的结果
	 * @param sheetname
	 * @param uikeyvalue
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ScriptException
	 */
	public static Map<String, Object> getExpress(String sheetname,Map uikeyvalue) throws FileNotFoundException, IOException, ScriptException {
	    //File file = new File(local +"图纸填写规则及BOM用量.xls");
		TagHelper.loadExpressConfig();
		 List<Map<String,String>> expressinfo=GlobalData.expressCustomerConfig.get(sheetname);
		Map<String, Object> descs = HandleExpress(expressinfo, uikeyvalue,sheetname);
		return descs;
	}

	/**
	 * 根据规则计算填入dwg的值
	 * @param expressinfo
	 * @param uikeyvalue
	 * @param sheetname
	 * @return
	 * @throws ScriptException
	 */
	private static Map<String,Object> HandleExpress(List<Map<String, String>> expressinfo,Map uikeyvalue,String sheetname) throws ScriptException {
		ScriptEngineManager manager = new ScriptEngineManager(); 
		Map<String,Object> map=new HashMap<String,Object>();
        ScriptEngine engine = manager.getEngineByName("js"); 
        Set uikey=uikeyvalue.keySet();
        Iterator uikeyite=uikey.iterator();
        while(uikeyite.hasNext()){
        	String key=(String) uikeyite.next();
        	Object value=uikeyvalue.get(key);
        	engine.put(key,value);
        }
        for (int i = 0; i < expressinfo.size(); i++) {//是否满足配置表每一行信息，满足则添加到map
        	Map<String, String> rowmap=expressinfo.get(i);
        	String key=rowmap.get(ConstantLine.var_rule_key);
        	boolean flag=ExpressLineInfo.judgeCondition(sheetname, rowmap, engine);//判断是否满足这一行
        	if(flag){
        		String value=ExpressLineInfo.retireResult(sheetname, rowmap, engine);//返回此行计算结果
        		map.put(key, value);
        	}
		}
		return map;
	}
	
	/**
	 * 计算用量
	 * @param expressinfo
	 * @param uikeyvalue
	 * @param sheetname
	 * @return
	 * @throws ScriptException
	 */
	private static double HandleUse(List<Map<String, String>> expressinfo,Map uikeyvalue,String sheetname) throws ScriptException {
		ScriptEngineManager manager = new ScriptEngineManager(); 
        ScriptEngine engine = manager.getEngineByName("js"); 
        Set uikey=uikeyvalue.keySet();
        Iterator uikeyite=uikey.iterator();
        while(uikeyite.hasNext()){
        	String key=(String) uikeyite.next();
        	Object value=uikeyvalue.get(key);
        	engine.put(key,value);
        }
        for (int i = 0; i < expressinfo.size(); i++) {
        	Map<String, String> rowmap=expressinfo.get(i);
        	boolean flag=ExpressLineInfo.judgeCondition(sheetname, rowmap, engine);//判断是否满足这一行
        	if(flag){
        		String value=ExpressLineInfo.retireResult(sheetname, rowmap, engine);//返回此行计算结果
        		double result=Double.valueOf(value);
        		if(result==0.0){
        			throw new LineException(ConstantLine.exception_cannotmath);
        		}else if(result<0.0){
        			throw new LineException(ConstantLine.exception_errormath);
        		}
        		return result;
        	}
		}
        throw new LineException(ConstantLine.exception_cannotmath);
	}
	
	/**
	 * 获取excel信息
	 * @param result
	 * @return
	 */
	public static List<Map<String, String>> getSheetInfo(String[][] result) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		int rowLength = result.length;
		List headers = new ArrayList();
		Map prerowmap = new HashMap();
		for (int i = 0; i < rowLength; i++) {
			Map rowmap = new HashMap();
			int selectindex = 0;
			for (int j = 0; j < result[i].length; j++) {
				if (i == 0) {
					headers.add(result[i][j]);
				} else {
					if (headers.size() > j && !headers.get(j).toString().trim().equals("")) {
							String value=result[i][j];
							rowmap.put(headers.get(j).toString().trim(), value);
					}

				}
			}
			if (!rowmap.isEmpty()) {
				list.add(rowmap);
			}
		}
		return list;
	}
	
   /**
    *获取用量信息 
    * @param sheetname
    * @param uikeyvalue
    * @return
    * @throws FileNotFoundException
    * @throws IOException
    * @throws ScriptException
    */
	public static double getUse(String sheetname, Map<String, Object> uikeyvalue) throws FileNotFoundException, IOException, ScriptException {
		TagHelper.loadExpressConfig();
		List<Map<String,String>> expressinfo=GlobalData.expressCustomerConfig.get(sheetname);
		double value = HandleUse(expressinfo, uikeyvalue,sheetname);
		return value;
	}
	
	/**
	 * 发ERP线长的值
	 * @param part
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ScriptException
	 */
	public static Double getLineL(WTPart part) throws FileNotFoundException, IOException, ScriptException {
		String var_lconnector = IBAUtil.getStringIBAValue(part,
				ConstantLine.var_lconnector);
		String var_rconnector = IBAUtil.getStringIBAValue(part,
				ConstantLine.var_rconnector);
		String var_ldconnector = IBAUtil.getStringIBAValue(part,
				ConstantLine.var_ldconnector);
		String var_rdconnector = IBAUtil.getStringIBAValue(part,
				ConstantLine.var_rdconnector);
		double l1=IBAUtil.getIntegerIBAValue(part, ConstantLine.var_L1);
		double l2=IBAUtil.getIntegerIBAValue(part, ConstantLine.var_L2);
		double l3=IBAUtil.getIntegerIBAValue(part, ConstantLine.var_L3);
		if(StringUtils.isEmpty(var_lconnector)&&StringUtils.isEmpty(var_rconnector)&&StringUtils.isEmpty(var_ldconnector)&&StringUtils.isEmpty(var_rdconnector)){//非线束类型部件
			return null;
		}else if(StringUtils.isEmpty(var_ldconnector)&&StringUtils.isEmpty(var_rdconnector)){
			return (double) l1/1000.0;
		}else if(StringUtils.isEmpty(var_rdconnector)){//左下
			return (double) (((l1+l3)/2)+l2)/1000.0; 
		}else if(StringUtils.isEmpty(var_ldconnector)){
			return (double) (((l2+l3)/2)+l1)/1000.0;
		}else{
			return null;
		}
		
	}

}
