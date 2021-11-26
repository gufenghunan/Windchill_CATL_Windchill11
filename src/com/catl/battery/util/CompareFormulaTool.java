package com.catl.battery.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.catl.battery.constant.ConstantBattery;
import com.catl.battery.helper.CommonHelper;

public class CompareFormulaTool {
	
	/**
	 * 比较两个计算表的公式 抛出不相同的错误
	 * @param dataFilePath
	 * @throws Exception
	 */
	public static String compareAndSet(String dataFilePath,String baseFilePath) throws Exception{
		File dataFile=new File(dataFilePath);
		File baseFile=new File(baseFilePath);
		
		if(!dataFile.exists() || !baseFile.exists()){
			throw new Exception("为下载文件添加公式时找不到相应文件！");
		}
		FileInputStream dataStream=new FileInputStream(dataFilePath);
		FileInputStream baseStream=new FileInputStream(baseFilePath);
		
		XSSFWorkbook datawb=new XSSFWorkbook(dataStream);
		XSSFWorkbook basewb=new XSSFWorkbook(baseStream);
		
		XSSFSheet dataSheet=null;
		XSSFSheet baseSheet=null;
		XSSFRow dataRow=null;
		XSSFRow baseRow=null;
		XSSFCell dataCell=null;
		XSSFCell baseCell=null;
		String msg="";
		String [] sheetName=ConstantBattery.config_sheetname;
		for(int sheetIndex=0;sheetIndex<sheetName.length;sheetIndex++){
			dataSheet=datawb.getSheet(sheetName[sheetIndex]);
			baseSheet=basewb.getSheet(sheetName[sheetIndex]);
			if(dataSheet==null || baseSheet==null){
				continue;
			}
			for(int rowIndex=0;rowIndex<ConstantBattery.config_limitexcel_rowcount;rowIndex++){
				dataRow=dataSheet.getRow(rowIndex);
				baseRow=baseSheet.getRow(rowIndex);
				if(dataRow==null || baseRow==null){
					continue;
				}
				String sheet_region=ConstantBattery.config_compareversion_cell;
				String sheetname=sheet_region.split("!")[0];
				String region=sheet_region.split("!")[1];
				int[] column=CommonHelper.translateRegion(region);
				for(int cellIndex=0;cellIndex<ConstantBattery.config_limit_colcount;cellIndex++){
					if(sheetName[sheetIndex].equals(sheetname)&&rowIndex==column[1]&&cellIndex==column[0]){
						continue;
					}
					dataCell=dataRow.getCell(cellIndex);
					baseCell=baseRow.getCell(cellIndex);
					if(dataCell==null || baseCell==null){
						continue;
					}
					if(dataCell.getCellType()==Cell.CELL_TYPE_FORMULA&&baseCell.getCellType()==Cell.CELL_TYPE_FORMULA){
						if(baseCell.getRawValue()==null){
							System.out.println(sheetName[sheetIndex]+"---"+CommonHelper.indexToColumn(cellIndex+1)+(rowIndex+1));
						}
						
						String dataformat1=dataCell.getCellStyle().getDataFormatString();
						String dataformat2=baseCell.getCellStyle().getDataFormatString();
						if(!dataformat1.equals(dataformat2)){
							System.out.println(sheetName[sheetIndex]+"--#-"+CommonHelper.indexToColumn(cellIndex+1)+(rowIndex+1));
							System.out.println(dataformat1+"--#--"+dataformat2);
						}
						
						if(!dataCell.getCellFormula().equals(baseCell.getCellFormula())){
								System.out.println("----------------------");
								String formula=dataCell.getCellFormula();
								if(dataCell.getCellFormula().contains("VLOOKUP")){
									formula=formula.replace(",)",",FALSE)");
									System.out.println(sheetName[sheetIndex]+"---"+CommonHelper.indexToColumn(cellIndex+1)+(rowIndex+1));
									System.out.println(dataCell.getCellFormula()+"-----"+baseCell.getCellFormula());
								}
								//baseCell.setCellFormula(formula);
								System.out.println("----------------------");
						}
					}
				}
			}
		}
		FileOutputStream outputStream=new FileOutputStream("E://tmp//battery_math_change.xlsx");
		basewb.write(outputStream);
		outputStream.close();
		basewb.close();
		datawb.close();
		baseStream.close();
		dataStream.close();
		return msg;
	}
	
	public static void main(String[] args) throws Exception {
		compareAndSet("Z://EVC//EVC M6U设计表统一---正式版V4_20171105.xlsx", "E://tmp//battery_math.xlsx");
	}

}
