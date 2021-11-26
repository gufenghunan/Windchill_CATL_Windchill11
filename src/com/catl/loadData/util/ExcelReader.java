package com.catl.loadData.util;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author
 */
public class ExcelReader {
	// 工作薄，也就是一个excle文件
	private Workbook wb = null;// book [includes sheet]
	// 一个excle文件可以有多个sheet
	private Sheet sheet = null;
	// 代表了表的第一行，也就是列名
	private Row row = null;
	// 一个excel有多个sheet，这是其中一个
	private int sheetNum = 0; // 第sheetnum个工作表
	// 一个sheet中可以有多行，这里应该是给行数的定义
	private int rowNum = 0;
	// 文件输入流
	private InputStream in = null;
	// 指定文件
	private File file = null;

	public ExcelReader() {
	}

	public ExcelReader(File file) {
		this.file = file;
	}
	
	public ExcelReader(InputStream in) {
		this.in = in;
	}

	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}

	public void setSheetNum(int sheetNum) {
		this.sheetNum = sheetNum;
	}

	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * 读取excel文件获得HSSFWorkbook对象
	 */
	public void open() throws IOException {
		if(file != null){
			in = new FileInputStream(file);
		}
		String fileName = file.getName().toLowerCase();
		if(fileName.substring(fileName.lastIndexOf(".")+1,fileName.length()).equals("xlsx")){
			wb = new XSSFWorkbook(in);
		}else{
			wb = new HSSFWorkbook(new POIFSFileSystem(in));
		}
		
		in.close();
	}

	/**
	 * 返回sheet表数目
	 * 
	 * @return int
	 */
	public int getSheetCount() {
		int sheetCount = -1;
		sheetCount = wb.getNumberOfSheets();
		return sheetCount;
	}
	
	public int getSheetIndex(String name){
		int index = wb.getSheetIndex(name);
		return index;
	}

	/**
	 * sheetNum下的记录行数
	 * 
	 * @return int
	 */
	public int getRowCount() {
		if (wb == null)
			System.out.println("=============>WorkBook为空");
		Sheet sheet = wb.getSheetAt(this.sheetNum);
		int rowCount = -1;
		rowCount = sheet.getLastRowNum();
		return rowCount;
	}

	/**
	 * 读取指定sheetNum的rowCount
	 * 
	 * @param sheetNum
	 * @return int
	 */
	public int getRowCount(int sheetNum) {
		Sheet sheet = wb.getSheetAt(sheetNum);
		int rowCount = -1;
		rowCount = sheet.getLastRowNum();
		return rowCount;
	}

	/**
	 * 得到指定行的内容
	 * 
	 * @param lineNum
	 * @return String[]
	 */
	public String[] readExcelLine(int lineNum) {
		return readExcelLine(this.sheetNum, lineNum);
	}

	/**
	 * 指定工作表和行数的内容
	 * 
	 * @param sheetNum
	 * @param lineNum
	 * @return String[]
	 */
	public String[] readExcelLine(int sheetNum, int lineNum) {
		if (sheetNum < 0 || lineNum < 0)
			return null;
		String[] strExcelLine = null;
		try {
			sheet = wb.getSheetAt(sheetNum);
			row = sheet.getRow(lineNum);

			int cellCount = row.getLastCellNum();
			strExcelLine = new String[cellCount + 1];
			for (int i = 0; i <= cellCount; i++) {
				strExcelLine[i] = readStringExcelCell(lineNum, i);
			}
		} catch (NullPointerException e) {
			//e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strExcelLine;
	}

	/**
	 * 读取指定列的内容
	 * 
	 * @param cellNum
	 * @return String
	 */
	public String readStringExcelCell(int cellNum) {
		return readStringExcelCell(this.rowNum, cellNum);
	}

	/**
	 * 指定行和列编号的内容
	 * 
	 * @param rowNum
	 * @param cellNum
	 * @return String
	 */
	public String readStringExcelCell(int rowNum, int cellNum) {
		return readStringExcelCell(this.sheetNum, rowNum, cellNum);
	}

	/**
	 * 指定工作表、行、列下的内容
	 * 
	 * @param sheetNum
	 * @param rowNum
	 * @param cellNum
	 * @return String
	 */
	public String readStringExcelCell(int sheetNum, int rowNum, int cellNum) {
		if (sheetNum < 0 || rowNum < 0)
			return "";
		String strExcelCell = "";
		try {
			sheet = wb.getSheetAt(sheetNum);
			row = sheet.getRow(rowNum);

			if (row.getCell((short) cellNum) != null) { // add this condition
				// judge
				switch (row.getCell((short) cellNum).getCellType()) {
				case HSSFCell.CELL_TYPE_FORMULA:
					strExcelCell = "FORMULA ";
					break;
				case HSSFCell.CELL_TYPE_NUMERIC: 
					strExcelCell = String.valueOf(row.getCell((short) cellNum)
							.getNumericCellValue());
					if(strExcelCell.contains("E")){
						BigDecimal b = new BigDecimal(strExcelCell);
						strExcelCell = b.toPlainString();
					}
					if(strExcelCell.endsWith(".0")){
						strExcelCell=strExcelCell.substring(0,strExcelCell.indexOf(".0"));
					}
					break;
				case HSSFCell.CELL_TYPE_STRING:
					strExcelCell = row.getCell((short) cellNum)
							.getStringCellValue().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").trim();
					break;
				case HSSFCell.CELL_TYPE_BLANK:
					strExcelCell = "";
					break;
				default:
					strExcelCell = "";
					break;
				}
			}
			/*if (row.getCell((short) cellNum) != null) { // add this condition
				strExcelCell = row.getCell((short) cellNum).getStringCellValue();
			}*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strExcelCell;
	}

}
