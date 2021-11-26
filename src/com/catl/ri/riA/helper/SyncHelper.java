package com.catl.ri.riA.helper;

import java.io.FileOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.catl.ri.constant.ConstantRI;
import com.catl.ri.riA.util.ExcelUtil;


public class SyncHelper {
	/**
	 * 为下载的文档添加公式
	 * @param dataFilePath
	 * @throws Exception
	 */
	public static String syncFormula(String dataFilePath,String baseFilePath) throws Exception{
	    CommonHelper.CacheWriteToLocal(dataFilePath);
		XSSFWorkbook datawb=CommonHelper.getWorkBook(dataFilePath);
		XSSFWorkbook basewb=EncryptHelper.getEncryptWorkbook(baseFilePath);
		
		XSSFSheet dataSheet=null;
		XSSFSheet baseSheet=null;
		XSSFRow dataRow=null;
		XSSFRow baseRow=null;
		XSSFCell dataCell=null;
		XSSFCell baseCell=null;
		String msg="";
		String [] sheetName=ConstantRI.config_sheetnameA;
		msg=ExcelUtil.compareVersion(datawb,basewb);
		if(!StringUtils.isEmpty(msg)){
			return msg;
		}
		for(int sheetIndex=0;sheetIndex<sheetName.length;sheetIndex++){
			System.out.println(sheetName[sheetIndex]);
			dataSheet=datawb.getSheet(sheetName[sheetIndex]);
			baseSheet=basewb.getSheet(sheetName[sheetIndex]);
			if(dataSheet==null || baseSheet==null){
				continue;
			}
			for(int rowIndex=0;rowIndex<ConstantRI.config_limitexcel_rowcount;rowIndex++){
				dataRow=dataSheet.getRow(rowIndex);
				baseRow=baseSheet.getRow(rowIndex);
				if(dataRow==null || baseRow==null){
					continue;
				}
				String sheet_region=ConstantRI.config_compareversion_cellA;
				String sheetname=sheet_region.split("!")[0];
				String region=sheet_region.split("!")[1];
				int[] column=CommonHelper.translateRegion(region);
				for(int cellIndex=0;cellIndex<ConstantRI.config_limit_colcount;cellIndex++){
					if(sheetName[sheetIndex].equals(sheetname)&&rowIndex==column[1]&&cellIndex==column[0]){
						continue;
					}
					dataCell=dataRow.getCell(cellIndex);
					baseCell=baseRow.getCell(cellIndex);
					if(dataCell==null || baseCell==null){
						continue;
					}
					
					switch(dataCell.getCellType()){
						case Cell.CELL_TYPE_BLANK:
							break;
						case Cell.CELL_TYPE_STRING: 
							baseCell.setCellValue(dataCell.getStringCellValue());
							break;
						case Cell.CELL_TYPE_NUMERIC:
							dataCell.setCellType(Cell.CELL_TYPE_STRING);
							String value=dataCell.getStringCellValue();
							if(CommonHelper.isNumeric(value)){
								baseCell.setCellValue(Double.parseDouble(value));
							}else{
								baseCell.setCellValue(value);
							}
							break;
						case Cell.CELL_TYPE_FORMULA:
							break;
						case Cell.CELL_TYPE_ERROR:
							baseCell.setCellValue(dataCell.getErrorCellString());
							break;
						default:break;
					}
				}
			}
		}
		ExcelUtil.performFormula(basewb);
		FileOutputStream outputStream=new FileOutputStream(dataFilePath);
		basewb.write(outputStream);
		outputStream.flush();
		outputStream.close();
		basewb.close();
		datawb.close();
		EncryptHelper.encryptExcel(dataFilePath);
		return msg;
	}
}
