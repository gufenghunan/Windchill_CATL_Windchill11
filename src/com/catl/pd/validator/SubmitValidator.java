package com.catl.pd.validator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.drools.core.util.StringUtils;

import com.catl.pd.constant.ConstantPD;
import com.catl.pd.constant.GlobalData;
import com.catl.pd.helper.CacheHelper;

public class SubmitValidator {

	public static void validateFormValues(String sheetname,Map<String,String> values) throws Exception{
		CacheHelper.loadExcelConfig();
		List<Map<String,String>> validateConfigs=GlobalData.validatefromconfig_info;
		System.out.println(GlobalData.needVerifySheetName);
		if(GlobalData.needVerifySheetName.contains(sheetname)){
			HandleExpress(validateConfigs,values,sheetname);
		}
	}
	
	public static List getSheetNames() throws Exception{
		CacheHelper.loadExcelConfig();
		List<Map<String,String>> validateConfigs=GlobalData.validatefromconfig_info;
		List result=new ArrayList();
		for (int i = 0; i < validateConfigs.size(); i++) {
			Map<String,String> map=validateConfigs.get(i);
			if(!result.contains(map.get(ConstantPD.config_validateconfig_name))){
				result.add(map.get(ConstantPD.config_validateconfig_name));
			}
		}
		return result;
	}
	public static void HandleExpress(List<Map<String, String>> validateConfigs,Map<String, String> values,String sheetname) throws Exception{
		ScriptEngineManager engineManager=new ScriptEngineManager();
		ScriptEngine engine=engineManager.getEngineByName("js");
		for(Map.Entry<String, String> entry:values.entrySet()){
			engine.put(entry.getKey(),entry.getValue());
		}
		StringBuffer errors=new StringBuffer();
		for(Map<String,String> validateConfigMap:validateConfigs){
			if(!validateConfigMap.get(ConstantPD.config_validateconfig_name).equals(sheetname)){
				break;
			}
			boolean flag=judgeCondition(validateConfigMap, engine);
			if(flag){
				String error=validateConfigMap.get(ConstantPD.config_validateconfig_error);
				if(StringUtils.isEmpty(errors)){
					errors.append(error);
				}else{
					errors.append("\n"+error);
				}
			}
		}
		if(!StringUtils.isEmpty(errors)){
			throw new Exception(errors.toString());
		}
	}
	
	public static boolean judgeCondition(Map<String, String> validateConfigMap,ScriptEngine engine) throws Exception{
		StringBuffer buffer=new StringBuffer("true");
		String condition=validateConfigMap.get(ConstantPD.config_validateconfig_condition);
		if(condition.isEmpty()){
				buffer.append("&&true");
		}else{
			Object result = null;
			try {
				result = engine.eval(condition);
			} catch (ScriptException e) {
				//System.out.println(e.getLocalizedMessage());
			}
			if(result!=null&&result.getClass().getName().indexOf("Boolean")>-1){
				buffer.append("&&"+result);
			}else{
				buffer.append("&&"+false);
			}
		
		}
		return (Boolean) engine.eval(buffer.toString());
	}
}
