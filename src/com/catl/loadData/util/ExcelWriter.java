package com.catl.loadData.util;


import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.Region;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author zhengjiahong
 * @version 1.0
 * @createTime 2013-8-8 下午3:47:37
 * @declare 
 */
public class ExcelWriter {
	private final static Pattern patternNum = Pattern.compile("^[1-9]\\d+$|^-[1-9]\\d+$|^\\d");
	private final static Pattern patternDou = Pattern.compile("^\\d+\\.\\d+$|^-\\d+\\.\\d+$");

	/**
	 * 最后一行总计的合并规则，最后一列数组第一个为 总计字符 往后有几个null就合并几个   例子：["总计",null,null,"test","test"] 则合并前三个为总计
	 * @param sheetname
	 * @param excelName
	 * @param titles
	 * @param list list<Object[]>
	 * @return 
	 * @throws Exception
	 */
	public static boolean exportExcelList(String excelName,String sheetName,String[] titles, List<String[]> list) throws Exception {
		// 定义Workbook对象，代表excel工作表
		Workbook workbook = null;
		
		String fileType = excelName.substring(excelName.lastIndexOf(".") + 1, excelName.length()).toLowerCase();
		// 创建工作簿实例
		if (fileType.equals("xls")) {
			workbook = new HSSFWorkbook();
	    } else if (fileType.equals("xlsx")) {
	    	workbook = new XSSFWorkbook();
	    } else if (fileType.equals("csv")) {
	    	workbook = new XSSFWorkbook();
	    } else {
	        System.out.println("您的文档格式不正确！");
	        return false;
	    }

		// 创建工作表实例
		Sheet sheet = workbook.createSheet(sheetName);
		// 去掉网格线
		sheet.setDisplayGridlines(false);
		// 设置列的默认宽度
		sheet.setDefaultColumnWidth((short) 20.00);
		
		// 创建标题行
		Row titleRow = sheet.createRow(0);
		for (int i = 0; i < titles.length; i++) {
			setCellDataColor(workbook, titleRow, i, titles[i]);
		}
		// CellStyle cellStyle = demoWorkBook.createCellStyle();

        // cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));

       //  cell.setCellStyle(cellStyle);


		// 创建标题
		if (list != null) {
			// 填充表格
			for (int i = 0; i < list.size(); i++) {
				Row dataRow = sheet.createRow(i+1);
				Object[] objArr = list.get(i);
				for(int j = 0; j < objArr.length; j++){
					setCellData(workbook, dataRow, j, objArr[j]);
				}
			}
		}
		// 创建文件流
	    OutputStream stream = new FileOutputStream(excelName);
	    // 写入数据
	    workbook.write(stream);
	    // 关闭文件流
	    stream.close();
	    return true;
	}
	
	
	private static void setCellValue(Cell cell,Object obj){
		if(obj == null){
			cell.setCellValue("");
			return;
		}
		if(patternNum.matcher(obj.toString()).matches()){
			cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			try{
				cell.setCellValue(Long.parseLong(obj.toString()));
			}catch(NumberFormatException e){
				cell.setCellValue(obj.toString());
			}
		}else if(patternDou.matcher(obj.toString()).matches()){
			cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			cell.setCellValue(Double.parseDouble(obj.toString()));
		}else{
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue(obj.toString());
		}
	}
	/**
	 * 无颜色的Cell并且有边框
	 * 
	 * @param wb
	 * @param row
	 * @param col
	 * @param val
	 */
	private static CellStyle createCellStyle(Workbook wb) {
		CellStyle cellstyle = wb.createCellStyle();
		cellstyle.setBorderBottom(CellStyle.BORDER_THIN);
		cellstyle.setBottomBorderColor((short) 0);
		cellstyle.setBorderLeft(CellStyle.BORDER_THIN);
		cellstyle.setLeftBorderColor((short) 0);
		cellstyle.setBorderRight(CellStyle.BORDER_THIN);
		cellstyle.setRightBorderColor((short) 0);
		cellstyle.setBorderTop(CellStyle.BORDER_THIN);
		cellstyle.setTopBorderColor((short) 0);
		cellstyle.setAlignment(CellStyle.ALIGN_CENTER);// //水平居中
		cellstyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 垂直 居中
		return cellstyle;
	}

	/**
	 * 带有颜色的Cell并且有边框
	 * 
	 * @param wb
	 * @param row
	 * @param col
	 * @param val
	 */
	private static CellStyle createCellColorStyle(Workbook wb) {

		CellStyle cellstyle = wb.createCellStyle();
		cellstyle.setFillPattern(CellStyle.SOLID_FOREGROUND); // 填充单元格
		cellstyle.setFillForegroundColor(HSSFColor.AQUA.index); // 填绿色
		cellstyle.setBorderBottom(CellStyle.BORDER_THIN);
		cellstyle.setBottomBorderColor((short) 0);
		cellstyle.setBorderLeft(CellStyle.BORDER_THIN);
		cellstyle.setLeftBorderColor((short) 0);
		cellstyle.setBorderRight(CellStyle.BORDER_THIN);
		cellstyle.setRightBorderColor((short) 0);
		cellstyle.setBorderTop(CellStyle.BORDER_THIN);
		cellstyle.setTopBorderColor((short) 0);
		cellstyle.setAlignment(CellStyle.ALIGN_CENTER);// //水平居中
		cellstyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 垂直 居中
		return cellstyle;
	}
	
	/**
	 * 红色字体无边框
	 */
	private static CellStyle setRedStyle(Workbook wb) {
		CellStyle cellstyle = wb.createCellStyle();
		Font font = wb.createFont(); 
		font.setColor(Font.COLOR_RED);
		cellstyle.setFont(font);
		return cellstyle;
	}
	/**
	 * 粗体居中无边框
	 */
	private static CellStyle setBoldStyle(Workbook wb) {
		CellStyle cellstyle = wb.createCellStyle();
		Font font = wb.createFont(); 
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		cellstyle.setFont(font);
		cellstyle.setAlignment(CellStyle.ALIGN_CENTER);// //水平居中
		cellstyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 垂直 居中
		return cellstyle;
	}

	private static void setCellData(Workbook workbook, Row row,int column,
			Object data) {
		Cell cell;
		cell = row.createCell(column);// 创建数据列
		//cell.setCellStyle(createCellStyle(workbook));
		//cell.setCellValue(data);
		setCellValue(cell,data);
	}
	private static void setCellDataColor(Workbook workbook, Row row,int column,
			String data) {
		Cell cell;
		cell = row.createCell(column);// 创建数据列
		//cell.setCellStyle(createCellColorStyle(workbook));
		cell.setCellValue(data);
	}

}
