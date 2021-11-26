package com.catl.part;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.catl.common.util.WCLocationConstants;
import com.catl.loadData.util.ExcelReader;

public class CheckPDFConstants {
	public static final Set<String> needCheckInfo = new HashSet<String>();
	public static final Set<String> needCheck3DInfo = new HashSet<String>();
	public static final Set<String> needCheck2DInfo = new HashSet<String>();
	static {
		System.out.println("=====loadNeedCheckInfo start=====");
		String filePathName = WCLocationConstants.WT_CODEBASE+File.separator
				+"com"+File.separator+"catl"+File.separator
				+"checkPDFData"+File.separator+"CheckPDFData.xlsx";
		ExcelReader er = new ExcelReader(new File(filePathName));
		er.setSheetNum(0);
		try {
			er.open();
			int count = er.getRowCount();
			String[] cells = null;
			
			for(int i=1; i<=count; i++){
				cells = er.readExcelLine(i);
				if(cells==null || StringUtils.isBlank(cells[0])){
					break;
				}
				needCheckInfo.add(cells[0]);
			}
			System.out.println("====RowCount:"+count);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("===needCheckInfo.size():"+needCheckInfo.size());		
		
		er.setSheetNum(1);
		try {
			er.open();
			int count = er.getRowCount();
			String[] cells = null;
			
			for(int i=1; i<=count; i++){
				cells = er.readExcelLine(i);
				if(cells==null || StringUtils.isBlank(cells[0])){
					break;
				}
				needCheck3DInfo.add(cells[0]);
			}
			System.out.println("====Row3DCount:"+count);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("===needCheck3DInfo.size():"+needCheck3DInfo.size());
		
		er.setSheetNum(2);
		try {
			er.open();
			int count = er.getRowCount();
			String[] cells = null;
			
			for(int i=1; i<=count; i++){
				cells = er.readExcelLine(i);
				if(cells==null || StringUtils.isBlank(cells[0])){
					break;
				}
				needCheck2DInfo.add(cells[0]);
			}
			System.out.println("====Row2DCount:"+count);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("===needCheck2DInfo.size():"+needCheck2DInfo.size());
		System.out.println("=====loadNeedCheckInfo end=====");
	}
}
