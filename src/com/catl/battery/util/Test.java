package com.catl.battery.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.catl.battery.constant.ConstantBattery;
import com.catl.battery.helper.CommonHelper;

public class Test {
	public static void main(String[] args) throws Exception {
		String [] config_sheetname={"Overhang"};
		clearFormula(config_sheetname,"E://tmp//bak//work//ccc.xlsx");
	}
	/**
	 * 比较两个计算表的公式 (除了材料数据库) 抛出不相同的错误
	 * @param dataFilePath
	 * @throws Exception
	 */
	public static void clearFormula(String[] config_sheetname,String dataFilePath) throws Exception{
		FileInputStream dataStream=new FileInputStream(dataFilePath);
		XSSFWorkbook datawb=new XSSFWorkbook(dataStream);
		XSSFSheet dataSheet=datawb.getSheet("Overhang");
		XSSFRow dataRow=null;
		XSSFCell dataCell=null;
		int index=0;
		for(int rowIndex=0;rowIndex<130;rowIndex++){
			dataRow=dataSheet.getRow(rowIndex);
			if(dataRow==null){
				continue;
			}
			for(int cellIndex=0;cellIndex<12;cellIndex++){
				dataCell=dataRow.getCell(cellIndex);
				if(dataCell==null){
					continue;
				}
				if(dataCell!=null&&dataCell.getCellType()==Cell.CELL_TYPE_FORMULA){
					String region=CommonHelper.indexToColumn(cellIndex+1)+(rowIndex+1);
					int base=index*15+214;
					index=index+1;
					String str=
							"#overhang_pic_"+region+" {"
							 +"\n"
						     +"width: 100px !important;"
						     +"\n"
						     +"display: block !important;"
						     +"\n"
						     +"left: 10px;"
						     +"\n"
						     +"top: "+base+"px;"
						     +"\n"
						     +"position: absolute !important;"
						     +"\n"
						     +"color: black !important;"
						     +"\n"
						     +"z-index:1022;"
						     +"\n"
						     +"}";
					System.out.println(region);
			   }
		}
	 }
		FileOutputStream outputStream=new FileOutputStream(dataFilePath);
		datawb.write(outputStream);
		outputStream.close();
		datawb.close();
		dataStream.close();
	}
	
}
