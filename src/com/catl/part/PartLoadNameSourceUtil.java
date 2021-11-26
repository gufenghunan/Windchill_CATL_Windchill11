package com.catl.part;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

import com.catl.common.global.GlobalVariable;
import com.catl.common.util.WCLocationConstants;
import com.catl.doc.workflow.DocClassificationModelNew;
import com.catl.doc.workflow.DocWfUtil;
import com.catl.loadData.util.ExcelReader;

import wt.part.Source;
import wt.workflow.engine.WfProcess;

public class PartLoadNameSourceUtil {
	public static Logger logger = Logger.getLogger(PartLoadNameSourceUtil.class.getName());
	
	public static final String PartClassification = WCLocationConstants.WT_HOME + File.separator + "codebase"+ File.separator+"config"+ File.separator+"custom"+ File.separator+"PartClassificationNameSource.xlsx";
	
	public static Map<String,String> sourcemap= new HashMap();
	
	static{
		Source[] sources = Source.getSourceSet();
		for(Source source:sources){
			System.out.println(source.toString()+"\t"+source.getDisplay(Locale.CHINA));
			sourcemap.put(source.getDisplay(Locale.CHINA), source.toString());
		}
	}
	public static void main(String[] args){
		Source[] sources = Source.getSourceSet();
		for(Source source:sources){
			System.out.println(source.toString()+"\t"+source.getDisplay(Locale.CHINA));
		}
	}
	private static void checkNewFile() {
		// 获取文件更新时间,若修改时间改变则更新规则文件
		WfProcess process = null;
		String filePath = PartClassification;
		File file = new File(filePath);
		Long fileModifyTime = file.lastModified();
		Long sysModifyTime = GlobalVariable.fileLastModifyTime.get("PartClassificationNameSource.xlsx");
		if (!(sysModifyTime == fileModifyTime)) {
			PartLoadNameSourceUtil.getPartClsNameSource();
		}
	}
	
	/*
	 * 
	 */
	public static Map<String, String> getPartClsNameSource() {
		String filePath = PartClassification;
		File file = new File(filePath);
		ExcelReader reader = new ExcelReader(file);
		try {
			reader.open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		reader.setSheetNum(0);
		int count = reader.getRowCount();
		Map<String, String> clsnamesource = new HashMap<String, String>();
		for (int i = 2; i <= count; i++) {
			String rows[] = reader.readExcelLine(i);

			if (!(rows == null || rows[7].isEmpty() || rows[9].isEmpty()|| rows[12].isEmpty()|| rows[13].isEmpty())) {

				String clsnode = rows[7].isEmpty() ? "" : rows[7];
				String unit = rows[8].isEmpty() ? "" : rows[8];
				String names = rows[9].isEmpty() ? "" : rows[9];
				String isCustomer = rows[11].isEmpty()?"":rows[11].trim();
				String source = rows[12].isEmpty() ? "" : rows[12];
				String openMould = rows[13].isEmpty() ? "" : rows[13];
				String[] sourceArray = source.split("\\|");
				boolean flag = false;
				StringBuffer sb = new StringBuffer();
				
				if(isCustomer.equals("是")){
					sb.append("customer").append(",").append("客供");
					flag = true;
				}else{
					for(String sa:sourceArray){
						if(sourcemap.containsKey(sa)){
							if(flag){
								sb.append("|");
							}
							flag = true;
							sb.append(sourcemap.get(sa)).append(",").append(sa);
						}
					}
				}
				clsnamesource.put(clsnode, names+"qqqq;;;;"+sb.toString()+"qqqq;;;;"+unit.toLowerCase()+"qqqq;;;;"+openMould);
			}// end if
		}// end for
		GlobalVariable.fileLastModifyTime.put("PartClassificationNameSource.xlsx", file.lastModified());
		return clsnamesource;
	}
}
