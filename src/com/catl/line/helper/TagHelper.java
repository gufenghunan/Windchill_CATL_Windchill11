package com.catl.line.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.catl.common.util.PropertiesUtil;
import com.catl.line.constant.ConstantLine;
import com.catl.line.constant.GlobalData;
import com.catl.line.util.CommonUtil;
import com.catl.line.util.ExcelUtil;

import wt.util.WTProperties;

public class TagHelper {
  public static String wt_home;
  public static String local;
	static {
		try {
            wt_home=WTProperties.getLocalProperties().getProperty("wt.home","UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获取标签值
	 * @param coltype
	 * @param linetype 线束类型
	 * @param key 左标签 右标签 下标签
	 * @param keyvalue
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	 public static List<String> getTagBoxDesc(String coltype,String linetype,String key,String keyvalue) throws FileNotFoundException, IOException{
		 TagHelper.loadTagConfig();
		 List descs=HandletagBox(coltype,linetype,key,keyvalue);
		 return descs;
	}
	 
	 /**
	  * 根据excel获取标签的对应值
	  * @param coltype
	  * @param linetype
	  * @param keytype
	  * @param keyvalue
	  * @return
	  */
	 private static List HandletagBox(String coltype,String linetype,String keytype,String keyvalue){
		List result=new ArrayList();
		 if(coltype.equals(ConstantLine.dtag)){
			 for (int j = 0; j < GlobalData.dtaginfo.size(); j++) {
				 Map map=GlobalData.dtaginfo.get(j);
				 String desc=(String) map.get(ConstantLine.col_desc);
				 if(desc!=null){
					 if(!result.contains(desc)){
						 List list=new ArrayList();
						 list.add(desc);
						 result.add(list);
					 }
					
				 }
			 }
		 }else{
			 for (int j = 0; j < GlobalData.taginfo.size(); j++) {
				 Map map=GlobalData.taginfo.get(j);
				 String clinetype=(String) map.get(ConstantLine.col_linetype);
				 if(!StringUtils.isEmpty(keytype)){//内容
					 String key=(String) map.get(keytype);
					 if(key!=null&&clinetype.equals(linetype)&&key.equals(keyvalue)){
						 String cboxtype=(String) map.get(coltype);
						 if(!iscontainstr(result,cboxtype)){
							 List list=new ArrayList();
							 list.add(cboxtype);
							 result.add(list);
						 }
						 System.out.println(cboxtype);
					 }
				 }else{//箱体
					 if(clinetype.equals(linetype)){
						 String cboxtype=(String) map.get(coltype);
						 if(cboxtype!=null){
							 if(!iscontainstr(result,cboxtype)){
								 List list=new ArrayList();
								 list.add(cboxtype);
								 result.add(list);
							 }
						 }
					 }
				 }
				
				 
			 }
		 }
		 return result;
	 }
	 public static boolean iscontainstr(List<ArrayList>result,String str){
		 for (int i = 0; i < result.size(); i++) {
			if(result.get(i).get(0).equals(str)){
				return true;
			}
		}
		 return false;
	 }
	 public static void main(String[] args) throws FileNotFoundException, IOException {
		 //getTagBoxDesc();
	}
	 
	/**
	 * 获取excel数据
	 * @param result
	 * @return
	 */
	public static List<Map<String,String>> getSheetInfo(String[][] result){
		   List<Map<String,String>> list=new ArrayList<Map<String,String>>();
	       int rowLength = result.length;
	       List headers=new ArrayList();
	       Map prerowmap=new HashMap();
	       for(int i=0;i<rowLength;i++) {
	     	  Map rowmap=new HashMap();
	     	  int selectindex=0;
	     	  for(int j=0;j<result[i].length;j++){
	     		if(i==0){
	     					headers.add(result[i][j]);
	     		}else{
	     			if(headers.size()>j&&!headers.get(j).toString().trim().equals("")){
	     				if(StringUtils.isEmpty(result[i][j])){
	     					String prevvalue=(String) prerowmap.get(headers.get(j).toString().trim());
	     					rowmap.put(headers.get(j).toString().trim(), prevvalue);
	     				}else{
	     					rowmap.put(headers.get(j).toString().trim(), result[i][j]);
		     				prerowmap.put(headers.get(j).toString().trim(), result[i][j]);
	     				}
	     			}
	     			
	     		}
	     	  }
	     	  if(!rowmap.isEmpty()){
	     		  list.add(rowmap);
	     	  }
	       }
			return list;
	   }
	
	   /**
	    * 获取excel数据
	    * @param result
	    * @return
	    */
		public static List<Map<String, String>> getSheetInfoNoFllowPrev(String[][] result) {
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
	 * 加载标签配置
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void loadTagConfig() throws FileNotFoundException, IOException{
		 File file = new File(wt_home+PropertiesUtil.getValueByKey("config_tag_path"));
   	 	if(GlobalData.tagCustomerConfigTime < file.lastModified()){
   	 	    String[][] tagresult =  ExcelUtil.getData(0,null,file,0,false);
   	 	    String[][] dtagresult = ExcelUtil.getData(1,null,file,0,false);
   	 	    String[][] lengthresult = ExcelUtil.getData(2,null,file,0,false);
   	 	    GlobalData.taginfo=getSheetInfo(tagresult);
   	 	    GlobalData.dtaginfo=getSheetInfo(dtagresult);
   	 	    GlobalData.lengthinfo=getSheetInfo(lengthresult);
   	 		GlobalData.tagCustomerConfigTime = file.lastModified();
   	 	}
	}
	/**
	 * 加载图纸填写规则BOM用量配置
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void loadExpressConfig() throws FileNotFoundException, IOException{
		File file = new File(wt_home + PropertiesUtil.getValueByKey("config_express_path"));
		//File file = new File(local +"图纸填写规则及BOM用量.xls");
   	 	if(GlobalData.expressCustomerConfigTime < file.lastModified()){
   	 		String[] sheetnames=ConstantLine.config_connector_bom_sheetname.split(",");
   	 		for (int i = 0; i < sheetnames.length; i++) {
   	 		 String[][] result = ExcelUtil.getData(0, sheetnames[i], file, 0, false);
   	 		GlobalData.expressCustomerConfig.put(sheetnames[i], getSheetInfoNoFllowPrev(result));
			}
   	       GlobalData.expressCustomerConfigTime = file.lastModified();
   	 	}
	}
	 public static List<String> getLengthRecommend(String minl,String maxl,String currentl) throws FileNotFoundException, IOException{
		 TagHelper.loadTagConfig();
		 List descs=HandleL(minl,maxl,currentl);
		 return descs;
	}
	 
	/**
	 * 获取推荐线长
	 * @param minl 页面传来的参考小值
	 * @param maxl 页面传来的参考大值
	 * @param currentl 当前页面填写值
	 * @return
	 */
	private static List HandleL(String minl, String maxl, String currentl) {
		List recommendList=new ArrayList();
		List result=new ArrayList();
		recommendList.add(Integer.valueOf(minl));
		recommendList.add(Integer.valueOf(maxl));
		recommendList.add(Integer.valueOf(currentl));
		 for (int j = 0; j < GlobalData.lengthinfo.size(); j++) {
			 Map map=GlobalData.lengthinfo.get(j);
			 String l=(String) map.get(ConstantLine.col_recommend);
			 if(l!=null){
				 recommendList.add(Integer.valueOf(l));
			 }
		 }
		 Collections.sort(recommendList);
		 int cindex=recommendList.indexOf(Integer.valueOf(currentl));
		 result.add(recommendList.get(cindex-1));
		 result.add(recommendList.get(cindex+1));
		return result;
	}
	
}
