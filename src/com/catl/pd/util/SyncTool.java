package com.catl.pd.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.catl.pd.constant.ConstantPD;
import com.catl.pd.helper.CommonHelper;

public class SyncTool {
	/**
	 * 比较两个计算表的公式 (除了材料数据库) 抛出不相同的错误
	 * @param dataFilePath
	 * @throws Exception
	 */
	public static String compareFormulaAndSet(String[] config_sheetname,String dataFilePath,String baseFilePath,boolean isset) throws Exception{
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
		String [] sheetName=config_sheetname;
		for(int sheetIndex=0;sheetIndex<sheetName.length;sheetIndex++){
			dataSheet=datawb.getSheet(sheetName[sheetIndex]);
			baseSheet=basewb.getSheet(sheetName[sheetIndex]);
			if(dataSheet==null || baseSheet==null){
				continue;
			}
			for(int rowIndex=0;rowIndex<ConstantPD.config_limitexcel_rowcount;rowIndex++){
				dataRow=dataSheet.getRow(rowIndex);
				baseRow=baseSheet.getRow(rowIndex);
				if(dataRow==null || baseRow==null){
					continue;
				}
				String sheet_region=ConstantPD.config_compareversion_cell;
				String sheetname=sheet_region.split("!")[0];
				String region=sheet_region.split("!")[1];
				int[] column=CommonHelper.translateRegion(region);
				for(int cellIndex=0;cellIndex<ConstantPD.config_limit_colcount;cellIndex++){
					if(sheetName[sheetIndex].equals(sheetname)&&rowIndex==column[1]&&cellIndex==column[0]){
						continue;
					}
					dataCell=dataRow.getCell(cellIndex);
					baseCell=baseRow.getCell(cellIndex);
					if(dataCell==null || baseCell==null){
						continue;
					}
					if(dataCell.getCellType()==Cell.CELL_TYPE_FORMULA){
					    if(baseCell.getCellType()!=Cell.CELL_TYPE_FORMULA){
					    	String formula=dataCell.getCellFormula();
							if(dataCell.getCellFormula().contains("VLOOKUP")){
								formula=formula.replace(",)",",FALSE)");
								System.out.println(sheetName[sheetIndex]+"---"+CommonHelper.indexToColumn(cellIndex+1)+(rowIndex+1));
								System.out.println(dataCell.getCellFormula()+"-----无公式");
							}
							if(isset){
								baseCell.setCellFormula(formula);
							}
					    	continue;
					    }
						if(!dataCell.getCellFormula().equals(baseCell.getCellFormula())){
								System.out.println("----------------------");
								String formula=dataCell.getCellFormula();
								if(dataCell.getCellFormula().contains("VLOOKUP")){
									formula=formula.replace(",)",",FALSE)");
									System.out.println(sheetName[sheetIndex]+"---"+CommonHelper.indexToColumn(cellIndex+1)+(rowIndex+1));
									System.out.println(dataCell.getCellFormula()+"-----"+baseCell.getCellFormula());
								}
								if(isset){
									baseCell.setCellFormula(formula);
								}
								System.out.println("----------------------");
						}
					}
				}
			}
		}
		FileOutputStream outputStream=new FileOutputStream(baseFilePath);
		basewb.write(outputStream);
		outputStream.close();
		basewb.close();
		datawb.close();
		baseStream.close();
		dataStream.close();
		return msg;
	}
	
	/**
	 * 比较两个计算表的字符串单元格  抛出不相同的错误
	 * @param config_sheetname 
	 * @param dataFilePath
	 * @throws Exception
	 */
	public static String compareStringCellAndSet(String[] config_sheetname, String dataFilePath,String baseFilePath,boolean isset) throws Exception{
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
		String [] sheetName=config_sheetname;
		for(int sheetIndex=0;sheetIndex<sheetName.length;sheetIndex++){
			dataSheet=datawb.getSheet(sheetName[sheetIndex]);
			baseSheet=basewb.getSheet(sheetName[sheetIndex]);
			if(dataSheet==null || baseSheet==null){
				continue;
			}
			for(int rowIndex=0;rowIndex<ConstantPD.config_limitexcel_rowcount;rowIndex++){
				dataRow=dataSheet.getRow(rowIndex);
				baseRow=baseSheet.getRow(rowIndex);
				if(dataRow==null || baseRow==null){
					continue;
				}
				String sheet_region=ConstantPD.config_compareversion_cell;
				String sheetname=sheet_region.split("!")[0];
				String region=sheet_region.split("!")[1];
				int[] column=CommonHelper.translateRegion(region);
				for(int cellIndex=0;cellIndex<ConstantPD.config_limit_colcount;cellIndex++){
					if(sheetName[sheetIndex].equals(sheetname)&&rowIndex==column[1]&&cellIndex==column[0]){
						continue;
					}
					dataCell=dataRow.getCell(cellIndex);
					baseCell=baseRow.getCell(cellIndex);
					if(dataCell==null || baseCell==null){
						continue;
					}
					if(dataCell.getCellType()==Cell.CELL_TYPE_STRING&&baseCell.getCellType()==Cell.CELL_TYPE_STRING){
						if(!dataCell.getStringCellValue().equals(baseCell.getStringCellValue())){
								System.out.println("----------------------");
								String bvalue=dataCell.getStringCellValue();
								System.out.println(sheetName[sheetIndex]+"---"+CommonHelper.indexToColumn(cellIndex+1)+(rowIndex+1));
								System.out.println(dataCell.getStringCellValue()+"-----"+baseCell.getStringCellValue());
								if(isset){
									baseCell.setCellValue(bvalue);
								}
								System.out.println("----------------------");
						}
					}else if(dataCell.getCellType()!=baseCell.getCellType()){
						System.out.println(sheetName[sheetIndex]+"---"+CommonHelper.indexToColumn(cellIndex+1)+(rowIndex+1));
					}
				}
			}
		}
		FileOutputStream outputStream=new FileOutputStream(baseFilePath);
		basewb.write(outputStream);
		outputStream.close();
		basewb.close();
		datawb.close();
		baseStream.close();
		dataStream.close();
		return msg;
	}
	/**
	 * 比较两个计算表的字符串单元格  抛出不相同的错误
	 * @param config_sheetname 
	 * @param dataFilePath
	 * @throws Exception
	 */
	public static String compareAndForceSet(String[] config_sheetname, String dataFilePath,String baseFilePath,boolean isset) throws Exception{
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
		String [] sheetName=config_sheetname;
		for(int sheetIndex=0;sheetIndex<sheetName.length;sheetIndex++){
			dataSheet=datawb.getSheet(sheetName[sheetIndex]);
			baseSheet=basewb.getSheet(sheetName[sheetIndex]);
			if(dataSheet==null || baseSheet==null){
				continue;
			}
			for(int rowIndex=0;rowIndex<ConstantPD.config_limitexcel_rowcount;rowIndex++){
				dataRow=dataSheet.getRow(rowIndex);
				baseRow=baseSheet.getRow(rowIndex);
				if(dataRow==null || baseRow==null){
					continue;
				}
				String sheet_region=ConstantPD.config_compareversion_cell;
				String sheetname=sheet_region.split("!")[0];
				String region=sheet_region.split("!")[1];
				int[] column=CommonHelper.translateRegion(region);
				for(int cellIndex=0;cellIndex<ConstantPD.config_limit_colcount;cellIndex++){
					if(sheetName[sheetIndex].equals(sheetname)&&rowIndex==column[1]&&cellIndex==column[0]){
						continue;
					}
					dataCell=dataRow.getCell(cellIndex);
					baseCell=baseRow.getCell(cellIndex);
					if(dataCell==null){
						continue;
					}
					if(dataCell.getCellType()==Cell.CELL_TYPE_STRING){
						System.out.println(dataCell.getStringCellValue());
						baseCell.setCellValue(dataCell.getStringCellValue());
					}else if(dataCell.getCellType()==Cell.CELL_TYPE_NUMERIC){
						baseCell.setCellValue(dataCell.getNumericCellValue());
					}
				}
			}
		}
		FileOutputStream outputStream=new FileOutputStream(baseFilePath);
		basewb.write(outputStream);
		outputStream.close();
		basewb.close();
		datawb.close();
		baseStream.close();
		dataStream.close();
		return msg;
	}
	public static void forceSetFormulaCell(String dataFilePath,String baseFilePath,String sheettname,String region) throws IOException{
		FileInputStream dataStream=new FileInputStream(dataFilePath);
		FileInputStream baseStream=new FileInputStream(baseFilePath);
		int[] column=CommonHelper.translateRegion(region);
		XSSFWorkbook datawb=new XSSFWorkbook(dataStream);
		XSSFWorkbook basewb=new XSSFWorkbook(baseStream);
		XSSFCell datacell=datawb.getSheet(sheettname).getRow(column[1]).getCell(column[0]);
		XSSFCell basecell=basewb.getSheet(sheettname).getRow(column[1]).getCell(column[0]);
		try{
		basecell.setCellFormula(datacell.getCellFormula());
		}catch(Exception e){
			e.printStackTrace();
		}
		FileOutputStream outputStream=new FileOutputStream(baseFilePath);
		basewb.write(outputStream);
		outputStream.close();
		basewb.close();
		datawb.close();
		baseStream.close();
		dataStream.close();
	}
	public static void forceSetNumberCell(String dataFilePath,String baseFilePath,String sheettname,String region) throws IOException{
		FileInputStream dataStream=new FileInputStream(dataFilePath);
		FileInputStream baseStream=new FileInputStream(baseFilePath);
		int[] column=CommonHelper.translateRegion(region);
		XSSFWorkbook datawb=new XSSFWorkbook(dataStream);
		XSSFWorkbook basewb=new XSSFWorkbook(baseStream);
		XSSFCell datacell=datawb.getSheet(sheettname).getRow(column[1]).getCell(column[0]);
		XSSFCell basecell=basewb.getSheet(sheettname).getRow(column[1]).getCell(column[0]);
		try{
		basecell.setCellType(Cell.CELL_TYPE_NUMERIC);
		basecell.setCellValue(datacell.getNumericCellValue());
		}catch(Exception e){
			e.printStackTrace();
		}
		FileOutputStream outputStream=new FileOutputStream(baseFilePath);
		basewb.write(outputStream);
		outputStream.close();
		basewb.close();
		datawb.close();
		baseStream.close();
		dataStream.close();
	}
	
	public static void updateVersion(String path, String path1) throws Exception{
		String [] config_sheetname={"版本控制"};
		compareAndForceSet(config_sheetname,path, path1,true);
	}
	public static void main(String[] args) throws Exception {
		String [] config_sheetname={"机械件数据库","设计主界面","模切尺寸","Overhang","BOM","极耳错位","Summary","Cell Weight","残空间计算"};
		//比较公式的区别,false
		compareFormulaAndSet(config_sheetname,"E://tmp//data.xlsx", "E://tmp//pd_math.xlsx",false);
        compareStringCellAndSet(config_sheetname,"E://tmp//data.xlsx", "E://tmp//pd_math.xlsx",true);
		updateVersion("E://tmp//new//data.xlsx","E://tmp//new//pd_math.xlsx");
		forceSetFormulaCell("E://tmp//data.xlsx", "E://tmp//pd_math.xlsx", "BOM", "C42");
		forceSetNumberCell("E://tmp//data.xlsx", "E://tmp//pd_math.xlsx", "BOM", "F42");
	}

}
