package com.catl.line.util;

import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.drools.core.util.StringUtils;

import com.catl.line.constant.ConstantLine;
import com.catl.line.exception.LineException;  
  
public class ExpressLineInfo{  
  
    public static void test1() throws ScriptException {  
        String str = "3+(定位点A==0?0:1)+(定位点B==0?0:1)+(定位点C==0?0:1)+(定位点D==0?0:1)";  
        ScriptEngineManager manager = new ScriptEngineManager();  
        ScriptEngine engine = manager.getEngineByName("js");  
        engine.put("定位点A", 222);  
        engine.put("定位点B", 222); 
        engine.put("定位点C", 222); 
        engine.put("定位点D", 0); 
        Object result = engine.eval(str);  
        System.out.println("结果类型:" + result.getClass().getName() + ",计算结果:" + result);  
    }  
    /**
     * 处理一个sheet中的信息
     * @param rows
     * @return dwg图纸中要填写的标记和值的map
     * @throws ScriptException
     */
    public static Map getFillData(List<Map> rows) throws ScriptException {  
        ScriptEngineManager manager = new ScriptEngineManager();  
        ScriptEngine engine = manager.getEngineByName("js"); 
        for (int i = 0; i < rows.size(); i++) {
        	Map rowmap=rows.get(i);
		}
		return null;
    }  
    /**
     * 判断是否满足某行信息的条件
     * @param exceptionmsg 异常信息
     * @param rowmap 线束计算配置文件中每行数据的信息
     * @param engine 
     * @return
     * @throws ScriptException
     */
    public static boolean judgeCondition(String exceptionmsg,Map rowmap,ScriptEngine engine) throws ScriptException{
    	StringBuffer ress=new StringBuffer();
    	for(int i=1;i<10;i++){
    		String res=(String) rowmap.get(ConstantLine.var_condition+i);
    		if(!rowmap.containsKey(ConstantLine.var_condition+i)){
    			break;
    		}
    		String condition=(String) rowmap.get(ConstantLine.var_condition+i);
    		if(res.equals("")){
    			if(StringUtils.isEmpty(ress.toString())){
    				ress.append("true");
    			}else{
    				ress.append("&&true");
    			}
    		}else{
    			if(StringUtils.isEmpty(ress.toString())){
    				Object result = null;
    				try{
    					result = engine.eval(condition);  
    				}catch(Exception e){
    					throw new LineException(exceptionmsg+(ConstantLine.var_condition+i)+ConstantLine.exception_fillerrormsg+","+e.getLocalizedMessage());
    				}
    				if(result.getClass().getName().indexOf("Boolean")>-1){
    					ress.append(result);
    				}else{
    					throw new LineException(exceptionmsg+(ConstantLine.var_condition+i)+ConstantLine.exception_fillerrormsg);
    				}
    			}else{
    				Object result = null;
    				try{
    					result = engine.eval(condition);  
    				}catch(Exception e){
    					throw new LineException(exceptionmsg+(ConstantLine.var_condition+i)+ConstantLine.exception_fillerrormsg+","+e.getLocalizedMessage());
    				}
    				if(result.getClass().getName().indexOf("Boolean")>-1){
    					ress.append("&&"+result);
    				}else{
    					throw new LineException(exceptionmsg+(ConstantLine.var_condition+i)+ConstantLine.exception_fillerrormsg);
    				}
    			}
    		}
    	}
    	return (Boolean) engine.eval(ress.toString());
    }
    
    /**
     * 获取某行信息的计算结果
     * @param exceptionmsg 异常信息
     * @param rowmap 线束计算配置文件中每行数据的信息
     * @param engine 
     * @return
     * @throws ScriptException
     */
    public static String retireResult(String exceptionmsg,Map rowmap,ScriptEngine engine) throws ScriptException{
    	String condition=(String) rowmap.get(ConstantLine.var_mathrule);
    	String result="";
    	try{
    		Object obj=engine.eval(condition.toString());
    		if(obj.getClass().getName().indexOf("Double")>-1){
    			result=obj.toString().replace(".0", "");
    		}else{
    			result=obj.toString();
    		}
    	}catch(Exception e){
    		throw new LineException(exceptionmsg+ConstantLine.exception_fillerrormsg+","+e.getLocalizedMessage());
    	}
    	return result;
    }
    public static void main(String[] args) throws ScriptException {  
        test1();  
        //test2();  
    }  
}  