package com.catl.ri.riB.helper;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import wt.doc.WTDocument;
import wt.util.WTException;
import wt.util.WTProperties;

import com.catl.ri.constant.ConstantRI;
import com.catl.ri.riB.constant.GlobalData;
import com.catl.ri.riB.util.ExcelUtil;
import com.catl.ri.riB.util.RIUtil;
import com.catl.ri.riB.util.WTDocumentUtil;

public class CacheHelper {
	private static final Logger logger = Logger.getLogger(CacheHelper.class.getName());  
	  public static String wt_home;
		static {
			try {
	            wt_home=WTProperties.getLocalProperties().getProperty("wt.home","UTF-8");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	public static void loadExcelConfig() throws FileNotFoundException, IOException{
		 File file = new File(wt_home+ConstantRI.config_ri_xls_pathB);
  	 	if(GlobalData.riCustomerConfigTime < file.lastModified()){
  	 		GlobalData.material_libary_info.clear();
  	 		GlobalData.asm_libary_info.clear();
  	 		GlobalData.dropDownList_info.clear();
  	 		GlobalData.colorconfig_info.clear();
  	 		GlobalData.exportconfig_info.clear();
  	 	    GlobalData.defaultconfig_info.clear();
  	 	    GlobalData.needVerifySheetName.clear();
  	 		GlobalData.materical_config_result =  ExcelUtil.getData(0,null,file,0,false);
  	 	    GlobalData.material_libary_info=ExcelUtil.getSheetInfo(GlobalData.materical_config_result);
  	 	    String[][] result2 =  ExcelUtil.getData(1,null,file,0,false);
  	 	    GlobalData.asm_libary_info=ExcelUtil.getSheetInfo(result2);
  	 	    String[][] result3 =  ExcelUtil.getData(2,null,file,0,false);
 	 	    GlobalData.dropDownList_info=ExcelUtil.getSheetInfo(result3);
 	 	    String[][] result4 =  ExcelUtil.getData(3,null,file,0,false);
	 	    GlobalData.colorconfig_info=ExcelUtil.getSheetInfo(result4);
	 	    String[][] result5 =  ExcelUtil.getData(4,null,file,0,false);
	 	    GlobalData.exportconfig_info=ExcelUtil.getSheetInfo(result5);
	 	    String[][] result6 =  ExcelUtil.getData(5,null,file,0,false);
	 	    GlobalData.defaultconfig_info=ExcelUtil.getSheetInfo(result6);
	 	    String[][] result7 =  ExcelUtil.getData(6,null,file,0,false);
	 	    GlobalData.validatefromconfig_info=ExcelUtil.getSheetInfo(result7);
	 	    handleNeedVerify();
  	 	    GlobalData.riCustomerConfigTime=file.lastModified();
  	 	}
	}
	
	public static void loadMathConfig() throws FileNotFoundException, IOException, WTException, PropertyVetoException{
		WTDocument doc=RIUtil.getConfigByName(ConstantRI.config_rimath_nameB);
		if(doc==null){
			throw new WTException("计算公式配置模版不存在");
		}
		String baseFilePath=wt_home+ConstantRI.config_rimathB_xls;
		File file=new File(baseFilePath);
		if(!file.exists()||GlobalData.riMathLastDownloadTime < doc.getModifyTimestamp().getTime()){
			GlobalData.riMathLastDownloadTime=doc.getModifyTimestamp().getTime();
			String path=baseFilePath.substring(0, baseFilePath.lastIndexOf("/")+1);
			WTDocumentUtil.downloadDoc(doc,path);
			GlobalData.configchanges=new ArrayList<String>();//清除已同步的文件
 	 	}
	}
	
	private static void handleNeedVerify() {
		List<Map<String,String>> maps=GlobalData.validatefromconfig_info;
		for (int i = 0; i < maps.size(); i++) {
			Map<String,String> rowmap=maps.get(i);
			String cellregion=(String) rowmap.get(ConstantRI.config_validateconfig_name);
			GlobalData.needVerifySheetName.add(cellregion);
		}
	}
	/**
	 * 获取excel信息
	 * @param result
	 * @return
	 */
	public static List<Map<String, String>> getSheetInfo(String[][] result,String recipenumber) {
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
				if(rowmap.get(ConstantRI.config_recipenumber).equals(recipenumber)){
					list.add(rowmap);
				}
			}
		}
		return list;
	}
	/**
	 * 获取excel信息
	 * @param result
	 * @return
	 */
	public static List getRecipeString(String[][] result,String type) {
		List list = new ArrayList();
		int rowLength = result.length;
		List headers = new ArrayList();
		List record=new ArrayList();
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
				   String value=(String) rowmap.get(type);
				   if(!record.contains(value)){
					   Map map=new HashMap();
					   map.put("title", rowmap.get(type));
					   list.add(map);
					   record.add(value);
				   }
				   
			}
		}
		return list;
	}
	
}
