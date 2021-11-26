package com.catl.ri.riB.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.POIXMLException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import wt.util.WTException;

import com.catl.ri.constant.ConstantRI;
import com.catl.ri.entity.MaterialAttr;
import com.catl.ri.entity.MaterialDB;
import com.catl.ri.riB.constant.GlobalData;
import com.catl.ri.riB.helper.CacheHelper;
import com.catl.ri.riB.helper.CommonHelper;
import com.catl.ri.riB.helper.EncryptHelper;

public class ExcelUtil {
	public static void main(String[] args) throws Exception {
//		List list = test();
//		System.out.println(list.size());
//		for (int i = 0; i < list.size(); i++) {
//			Map map = (Map) list.get(i);
//			Set set = map.keySet();
//			Iterator ite = set.iterator();
//			while (ite.hasNext()) {
//				String text = (String) ite.next();
//				if (!StringUtils.isEmpty(text)) {
//					System.out.println((String) map.get(text));
//				}
//			}
//		}
	//	addFormula("E://doc/123005(MD).xlsx");
		System.out.println("123");
		String dataFilePath="E://tmp/周四_171116_测试_003_RD.xlsx";
		String baseFilePath="E://tmp/ri_math.xlsx";
		addFormula(dataFilePath, baseFilePath);
	}

	public static List test() throws FileNotFoundException, IOException {
		File file = new File("E://delete_folder.xls");
		String[][] result = ExcelUtil.getData(0, null, file, 0, true);
		List list = new ArrayList();
		int rowLength = result.length;
		List headers = new ArrayList();
		for (int i = 0; i < rowLength; i++) {
			Map rowmap = new HashMap();
			for (int j = 0; j < result[i].length; j++) {
				if (i == 0) {
					headers.add(result[i][j]);
				} else {
					rowmap.put(headers.get(j).toString().trim(), result[i][j]);
				}
			}
			if (!rowmap.isEmpty()) {
				list.add(rowmap);
			}

		}
		return list;
	}

	public static int getPartDataSheetNumber(File file) throws IOException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(
				file));
		POIFSFileSystem fs = new POIFSFileSystem(in);
		HSSFWorkbook wb = new HSSFWorkbook(fs);
		return wb.getNumberOfSheets();

	}

	public static String getSheetName(int sheetindex, File file)
			throws IOException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(
				file));
		POIFSFileSystem fs = new POIFSFileSystem(in);
		HSSFWorkbook wb = new HSSFWorkbook(fs);
		HSSFSheet st = (HSSFSheet) wb.getSheetAt(sheetindex);
		return st.getSheetName();
	}

	/**
	 * 获取excel信息
	 * 
	 * @param result
	 * @return
	 */
	public static List<Map<String, String>> getSheetInfo(String[][] result) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		int rowLength = result.length;
		List headers = new ArrayList();
		for (int i = 0; i < rowLength; i++) {
			Map rowmap = new HashMap();
			int selectindex = 0;
			for (int j = 0; j < result[i].length; j++) {
				if (i == 0) {
					headers.add(result[i][j]);
				} else {
					if (headers.size() > j
							&& !headers.get(j).toString().trim().equals("")) {
						String value = result[i][j];
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

	public static String[][] getData(int sheetindex, String sheetName,
			File file, int ignoreRows, boolean firstnullbreak)
			throws FileNotFoundException, IOException {
		List result = new ArrayList();
		int rowSize = 0;
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(
				file));
		Workbook wb;
		if (file.getName().endsWith(".xlsx")) {
			wb = new XSSFWorkbook(in);
		} else {
			wb = new HSSFWorkbook(new POIFSFileSystem(in));
		}
		Cell cell = null;
		Sheet st;
		if (sheetName == null) {
			st = wb.getSheetAt(sheetindex);
		} else {
			st = wb.getSheet(sheetName);
		}
		for (int rowIndex = ignoreRows; rowIndex <= st.getLastRowNum(); rowIndex++) {
			Row row = st.getRow(rowIndex);
			if (row == null) {
				continue;
			}
			int tempRowSize = row.getLastCellNum() + 1;
			if (tempRowSize > rowSize) {
				rowSize = tempRowSize;
			}
			String[] values = new String[rowSize];
			Arrays.fill(values, "");
			boolean hasValue = false;
			for (short columnIndex = 0; columnIndex <= row.getLastCellNum(); columnIndex++) {
				String value = "";
				cell = row.getCell(columnIndex);
				if (cell != null) {
					// cell.setEncoding(HSSFCell.ENCODING_UTF_16);
					switch (cell.getCellType()) {
					case HSSFCell.CELL_TYPE_STRING:
						value = cell.getStringCellValue();
						break;
					case HSSFCell.CELL_TYPE_NUMERIC:
						if (HSSFDateUtil.isCellDateFormatted(cell)) {
							Date date = cell.getDateCellValue();
							if (date != null) {
								value = new SimpleDateFormat("yyyy-MM-dd")
										.format(date);
							} else {
								value = "";
							}
						} else {
							value = String.valueOf(cell.getNumericCellValue());
							if (value.endsWith(".0")) {
								value = value.replace(".0", "");
							}
						}
						break;
					case HSSFCell.CELL_TYPE_FORMULA:
						if (!cell.getStringCellValue().equals("")) {
							value = cell.getStringCellValue();
						} else {
							value = cell.getNumericCellValue() + "";
						}
						break;
					case HSSFCell.CELL_TYPE_BLANK:
						break;
					case HSSFCell.CELL_TYPE_ERROR:
						value = "";
						break;
					case HSSFCell.CELL_TYPE_BOOLEAN:
						value = (cell.getBooleanCellValue() == true ? "Y" : "N");
						break;
					default:
						value = "";
					}
				}
				if (columnIndex == 0 && value.trim().equals("")
						&& firstnullbreak) {
					break;
				}
				values[columnIndex] = rightTrim(value);
				hasValue = true;
			}

			if (hasValue) {
				result.add(values);
			}
		}
		in.close();
		String[][] returnArray = new String[result.size()][rowSize];
		for (int i = 0; i < returnArray.length; i++) {
			returnArray[i] = (String[]) result.get(i);
		}
		return returnArray;
	}

	public static String[][] getData(int sheetindex, String sheetName,
			File file, int ignoreRows, int ignoreCloumns, boolean firstnullbreak)
			throws FileNotFoundException, IOException {
		List result = new ArrayList();
		int rowSize = 0;
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(
				file));
		Workbook wb;
		if (file.getName().endsWith(".xlsx")) {
			wb = new XSSFWorkbook(in);
		} else {
			wb = new HSSFWorkbook(new POIFSFileSystem(in));
		}
		Cell cell = null;
		// wb.getNumberOfSheets()
		Sheet st;
		if (sheetName == null) {
			st = wb.getSheetAt(sheetindex);
		} else {
			st = wb.getSheet(sheetName);
		}
		for (int rowIndex = ignoreRows; rowIndex <= st.getLastRowNum(); rowIndex++) {
			Row row = st.getRow(rowIndex);
			if (row == null) {
				continue;
			}
			int tempRowSize = row.getLastCellNum() + 1;
			if (tempRowSize > rowSize) {
				rowSize = tempRowSize;
			}
			String[] values = new String[rowSize];
			Arrays.fill(values, "");
			boolean hasValue = false;
			for (int columnIndex = ignoreCloumns; columnIndex <= row
					.getLastCellNum(); columnIndex++) {
				String value = "";
				cell = row.getCell(columnIndex);
				if (cell != null) {
					// cell.setEncoding(HSSFCell.ENCODING_UTF_16);
					switch (cell.getCellType()) {
					case HSSFCell.CELL_TYPE_STRING:
						value = cell.getStringCellValue();
						break;
					case HSSFCell.CELL_TYPE_NUMERIC:
						if (HSSFDateUtil.isCellDateFormatted(cell)) {
							Date date = cell.getDateCellValue();
							if (date != null) {
								value = new SimpleDateFormat("yyyy-MM-dd")
										.format(date);
							} else {
								value = "";
							}
						} else {
							value = String.valueOf(cell.getNumericCellValue());
							if (value.endsWith(".0")) {
								value = value.replace(".0", "");
							}
						}
						break;
					case HSSFCell.CELL_TYPE_FORMULA:
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						if (!cell.getStringCellValue().equals("")) {
							value = cell.getStringCellValue();
						} else {
							value = cell.getNumericCellValue() + "";
						}
						break;
					case HSSFCell.CELL_TYPE_BLANK:
						break;
					case HSSFCell.CELL_TYPE_ERROR:
						value = "";
						break;
					case HSSFCell.CELL_TYPE_BOOLEAN:
						value = (cell.getBooleanCellValue() == true ? "Y" : "N");
						break;
					default:
						value = "";
					}
				}
				if (columnIndex == 0 && value.trim().equals("")
						&& firstnullbreak) {
					break;
				}
				values[columnIndex] = rightTrim(value);
				hasValue = true;
			}

			if (hasValue) {
				result.add(values);
			}
		}
		in.close();
		String[][] returnArray = new String[result.size()][rowSize];
		for (int i = 0; i < returnArray.length; i++) {
			returnArray[i] = (String[]) result.get(i);
		}
		return returnArray;
	}

	public static String[][] getData(File file, int ignoreRows)
			throws FileNotFoundException, IOException {
		return getData(file, ignoreRows, true);
	}

	public static String[][] getData(File file, int ignoreRows, boolean flag)

	throws FileNotFoundException, IOException {

		List<String[]> result = new ArrayList<String[]>();

		int rowSize = 0;

		BufferedInputStream in = new BufferedInputStream(new FileInputStream(

		file));

		POIFSFileSystem fs = new POIFSFileSystem(in);

		HSSFWorkbook wb = new HSSFWorkbook(fs);

		HSSFCell cell = null;

		for (int sheetIndex = 0; sheetIndex < wb.getNumberOfSheets(); sheetIndex++) {
			HSSFSheet st = wb.getSheetAt(sheetIndex);
			for (int rowIndex = ignoreRows; rowIndex <= st.getLastRowNum(); rowIndex++) {
				HSSFRow row = st.getRow(rowIndex);
				if (row == null) {
					continue;
				}
				int tempRowSize = row.getLastCellNum() + 1;
				if (tempRowSize > rowSize) {
					rowSize = tempRowSize;
				}
				String[] values = new String[rowSize];

				Arrays.fill(values, "");

				boolean hasValue = false;

				for (short columnIndex = 0; columnIndex <= row.getLastCellNum(); columnIndex++) {
					String value = "";
					cell = row.getCell(columnIndex);
					if (cell != null) {
						// cell.setEncoding(HSSFCell.ENCODING_UTF_16);
						switch (cell.getCellType()) {
						case HSSFCell.CELL_TYPE_STRING:
							value = cell.getStringCellValue();
							break;
						case HSSFCell.CELL_TYPE_NUMERIC:
							if (HSSFDateUtil.isCellDateFormatted(cell)) {
								Date date = cell.getDateCellValue();
								if (date != null) {
									value = new SimpleDateFormat("yyyy-MM-dd")

									.format(date);
								} else {
									value = "";
								}
							} else {
								value = new DecimalFormat("0").format(cell
										.getNumericCellValue());
							}
							break;
						case HSSFCell.CELL_TYPE_FORMULA:
							if (!cell.getStringCellValue().equals("")) {
								value = cell.getStringCellValue();
							} else {
								value = cell.getNumericCellValue() + "";
							}
							break;
						case HSSFCell.CELL_TYPE_BLANK:
							value = "";
							break;
						case HSSFCell.CELL_TYPE_ERROR:
							value = "";
							break;
						case HSSFCell.CELL_TYPE_BOOLEAN:
							value = (cell.getBooleanCellValue() == true ? "Y"
									: "N");
							break;
						default:
							value = "";
						}
					}
					if (columnIndex == 0 && value.trim().equals("")) {
						if (flag) {
							break;
						}

					}
					values[columnIndex] = rightTrim(value);
					hasValue = true;
				}

				if (hasValue) {
					result.add(values);
				}
			}
		}
		in.close();
		String[][] returnArray = new String[result.size()][rowSize];
		for (int i = 0; i < returnArray.length; i++) {
			returnArray[i] = (String[]) result.get(i);
		}
		return returnArray;
	}

	public static String rightTrim(String str) {
		if (str == null) {
			return "";
		}
		int length = str.length();
		for (int i = length - 1; i >= 0; i--) {
			if (str.charAt(i) != 0x20) {
				break;
			}
			length--;
		}
		return str.substring(0, length);
	}

	public static String chinaToUnicode(String str) {
		String result = "";
		for (int i = 0; i < str.length(); i++) {
			int chr1 = (char) str.charAt(i);
			if (chr1 >= 19968 && chr1 <= 171941) {
				result += "\\u" + Integer.toHexString(chr1);
			} else {
				result += str.charAt(i);
			}
		}
		return result;
	}

	/**
	 * 复制一个单元格样式到目的单元格样式
	 * 
	 * @param fromStyle
	 * @param toStyle
	 */
	public static void copyCellStyle(XSSFCellStyle fromStyle,
			XSSFCellStyle toStyle) {
		toStyle.setAlignment(fromStyle.getAlignment());
		// 边框和边框颜色
		toStyle.setBorderBottom(fromStyle.getBorderBottom());
		toStyle.setBorderLeft(fromStyle.getBorderLeft());
		toStyle.setBorderRight(fromStyle.getBorderRight());
		toStyle.setBorderTop(fromStyle.getBorderTop());
		toStyle.setTopBorderColor(fromStyle.getTopBorderColor());
		toStyle.setBottomBorderColor(fromStyle.getBottomBorderColor());
		toStyle.setRightBorderColor(fromStyle.getRightBorderColor());
		toStyle.setLeftBorderColor(fromStyle.getLeftBorderColor());

		// 背景和前景
		toStyle.setFillBackgroundColor(fromStyle.getFillBackgroundColor());
		toStyle.setFillForegroundColor(fromStyle.getFillForegroundColor());

		toStyle.setDataFormat(fromStyle.getDataFormat());
		toStyle.setFillPattern(fromStyle.getFillPattern());
		// toStyle.setFont(fromStyle.getFont(null));
		toStyle.setHidden(fromStyle.getHidden());
		toStyle.setIndention(fromStyle.getIndention());// 首行缩进
		toStyle.setLocked(fromStyle.getLocked());
		toStyle.setRotation(fromStyle.getRotation());// 旋转
		toStyle.setVerticalAlignment(fromStyle.getVerticalAlignment());
		toStyle.setWrapText(fromStyle.getWrapText());

	}

	/**
	 * 行复制功能
	 * 
	 * @param fromRow
	 * @param toRow
	 */
	public static void copyRow(XSSFWorkbook wb, XSSFRow fromRow, XSSFRow toRow,
			boolean copyValueFlag) {
		for (Iterator cellIt = fromRow.cellIterator(); cellIt.hasNext();) {
			XSSFCell tmpCell = (XSSFCell) cellIt.next();
			XSSFCell newCell = toRow.createCell(tmpCell.getColumnIndex());
			copyCell(wb, tmpCell, newCell, copyValueFlag);
		}
	}

	/**
	 * 复制单元格
	 * 
	 * @param srcCell
	 * @param distCell
	 * @param copyValueFlag
	 *            true则连同cell的内容一起复制
	 */
	public static void copyCell(XSSFWorkbook wb, XSSFCell srcCell,
			XSSFCell distCell, boolean copyValueFlag) {
		XSSFCellStyle newstyle = wb.createCellStyle();
		newstyle.cloneStyleFrom(srcCell.getCellStyle());
		// copyCellStyle(srcCell.getCellStyle(), newstyle);
		// 样式
		distCell.setCellStyle(newstyle);
		// 评论
		if (srcCell.getCellComment() != null) {
			distCell.setCellComment(srcCell.getCellComment());
		}
		// 不同数据类型处理
		int srcCellType = srcCell.getCellType();
		distCell.setCellType(srcCellType);
		if (copyValueFlag) {
			if (srcCellType == HSSFCell.CELL_TYPE_NUMERIC) {
				if (HSSFDateUtil.isCellDateFormatted(srcCell)) {
					distCell.setCellValue(srcCell.getDateCellValue());
				} else {
					distCell.setCellValue(srcCell.getNumericCellValue());
				}
			} else if (srcCellType == HSSFCell.CELL_TYPE_STRING) {
				distCell.setCellValue(srcCell.getRichStringCellValue());
			} else if (srcCellType == HSSFCell.CELL_TYPE_BLANK) {
				// nothing21
			} else if (srcCellType == HSSFCell.CELL_TYPE_BOOLEAN) {
				distCell.setCellValue(srcCell.getBooleanCellValue());
			} else if (srcCellType == HSSFCell.CELL_TYPE_ERROR) {
				distCell.setCellErrorValue(srcCell.getErrorCellValue());
			} else if (srcCellType == HSSFCell.CELL_TYPE_FORMULA) {
				distCell.setCellFormula(srcCell.getCellFormula());
			} else { // nothing29
			}
		}
	}

	/**
	 * 清除所有sheet的公式
	 * @param filePath
	 * @throws IOException
	 */
	public static void clearFormula(String filePath) throws Exception {
		XSSFWorkbook workbook = EncryptHelper.getEncryptWorkbook(filePath);
		int sheetCounts=workbook.getNumberOfSheets();
		for(int sheetCount=0;sheetCount<sheetCounts;sheetCount++){

			XSSFSheet sheet = workbook.getSheetAt(sheetCount);
			for (int i = 0; i < 1000; i++) {
				XSSFRow row = sheet.getRow(i);
				for (int j = 0; j < 100; j++) {
					if (row == null) {
						continue;
					}
					XSSFCell cell = row.getCell(j);
					if (cell == null) {
						continue;
					}
					String value="";
					if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
						try {
							cell.setCellType(Cell.CELL_TYPE_STRING);
							value=cell.getStringCellValue();
							value=CommonHelper.transformvaluetodisplay(cell, value);
							value=ExcelUtil.handleFillvalue(cell,value);//填写前处理表单传来的值
							if(CommonHelper.isNumeric(value)){
								cell.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell.setCellValue(Double.parseDouble(value));
							}else if(StringUtils.isEmpty(value)){
								cell.setCellType(Cell.CELL_TYPE_BLANK);
							}else{
								cell.setCellValue(value);
							}						
						} catch (IllegalStateException e) {
							cell.setCellType(Cell.CELL_TYPE_STRING);
						} finally {
							if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
								cell.setCellType(Cell.CELL_TYPE_STRING);
							}
						}
					}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						value=CommonHelper.getNumformatValue(cell);
						try {
							String str="";
							if(value.endsWith("%")){
								XSSFCellStyle cellStyle = cell.getCellStyle();  
								System.out.println(value);
								if(value.endsWith(".00%")||value.indexOf(".")==-1){
						            cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0%"));  
								}else{
									cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));  
								}
								cell.setCellStyle(cellStyle); 
							}else if(CommonHelper.isNumeric(value)){
								value=String.valueOf(value);
							    value=String.valueOf(Float.valueOf(value));
								str=value.replaceAll("\\d+?", "0");
								str=transToDataFormat(str);
								XSSFCellStyle cellStyle = cell.getCellStyle();  
					            cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat(str));  
					            cell.setCellStyle(cellStyle); 
							}
						     
						}catch(Exception e){
						}
					}
				}
			}
		
		}
		FileOutputStream stream2 = new FileOutputStream(filePath);
		workbook.write(stream2);
		stream2.flush();
		stream2.close();
		workbook.close();
	}
	
	private static String transToDataFormat(String value) {
		value=value.replaceAll("\\d+?", "0");
		if(value.split("\\.").length==2){
			return 0+"."+value.split("\\.")[1];
		}
		return value;
	}
	/**
	 * 设置值到缓存文件
	 * @param filePath
	 * @param sheetName
	 * @param region
	 * @param value
	 * @throws Exception 
	 */
	public  static void setValueToTempExcel(String filePath,String sheetName,String region,String value) throws Exception{
		CommonHelper.waitFileOperation(filePath);
		GlobalData.inOperationFile.add(filePath);
		try{
		int [] column=null;
		XSSFWorkbook workbook=CommonHelper.getWorkBook(filePath);
			XSSFSheet sheet=workbook.getSheet(sheetName);
			column=CommonHelper.translateRegion(region);
			XSSFRow row=null;
			XSSFCell cell=null;
			try{
			row=sheet.getRow(column[1]);
			cell=row.getCell(column[0]);
			}catch(Exception e){
				throw new WTException(sheetName+"找不到单元格"+region);
			}
			if(cell!=null&&value!=null){
				value=handleFillvalue(cell,value);//填写前处理表单传来的值
				if(value.startsWith("=")){
					cell.setCellFormula(value.substring(1));
				}else if(CommonHelper.isNumeric(value)){
					cell.setCellValue(Double.parseDouble(value));
				}else if(StringUtils.isEmpty(value)){
					cell.setCellType(Cell.CELL_TYPE_BLANK);
				}else{
					cell.setCellValue(value);
				}
			}else{
				throw new WTException(sheetName+"计算模版不存在"+region+"单元格");
			}
			performFormula(workbook);
			GlobalData.mathexcel_cache.put(filePath, workbook);
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			GlobalData.inOperationFile.remove(filePath);
			CommonHelper.handleMathCache();
		}
	}
	
	/**
	 * 将用户所填的值写入Excel
	 * @param filePath
	 * @param sheetName
	 * @param values
	 * @throws Exception
	 */
	public static void setValuesToTempExcel(String filePath,String sheetName,Map<String,String> values) throws Exception{
		int [] column=null;
		String region=null;
		String value="";
		CommonHelper.waitFileOperation(filePath);
		GlobalData.inOperationFile.add(filePath);
		try{
		XSSFWorkbook workbook=CommonHelper.getWorkBook(filePath);
			XSSFSheet sheet=workbook.getSheet(sheetName);
			Pattern checkKey=Pattern.compile("[A-Z]+[0-9]+");
			Matcher matcher=null;
			for(Map.Entry<String, String> entry:values.entrySet()){
				region=entry.getKey();
				matcher=checkKey.matcher(region);
				if(!matcher.matches()){
					continue;
				}
				column=CommonHelper.translateRegion(region);
				XSSFRow row=null;
				XSSFCell cell=null;
				try{
				row=sheet.getRow(column[1]);
				cell=row.getCell(column[0]);
				}catch(Exception e){
					throw new WTException("找不到单元格"+region);
				}
				value=entry.getValue();
				if(cell!=null&&value!=null){
					value=handleFillvalue(cell,value);//填写前处理表单传来的值
					if(value.startsWith("=")){
						cell.setCellFormula(value.substring(1));
					}else if(CommonHelper.isNumeric(value)){
						cell.setCellValue(Double.parseDouble(value));
					}else if(cell.getCellType()==Cell.CELL_TYPE_FORMULA){
						continue;
					}else if(StringUtils.isEmpty(value)){
						cell.setCellType(Cell.CELL_TYPE_BLANK);
					}else{
						cell.setCellValue(value);
					}
				}else{
					throw new WTException("计算模版不存在"+region+"单元格");
				}
			}
			performFormula(workbook);
			GlobalData.mathexcel_cache.put(filePath, workbook);
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			GlobalData.inOperationFile.remove(filePath);
			CommonHelper.handleMathCache();
		}
	}
	
	public static String handleFillvalue(XSSFCell cell, String value) {
		String format=cell.getCellStyle().getDataFormatString();
		format=CommonHelper.getStrNumberic(format);
		if(format.endsWith("%")&&CommonHelper.isNumeric(value)){
			BigDecimal b1 = new BigDecimal(Double.toString(Double.valueOf(value)));
	        BigDecimal b2 = new BigDecimal(Double.toString(100));
	        value=b1.divide(b2).toString();
		}else{
			value=String.valueOf(value);
			boolean isprecent=value.matches("^((\\d+\\.?\\d*)|(\\d*\\.\\d+))\\%$");
			if(isprecent){//百分数换算为小数
			    BigDecimal b1 = new BigDecimal(Double.toString(Double.valueOf(value.replace("%", ""))));
		        BigDecimal b2 = new BigDecimal(Double.toString(100));
		        value=b1.divide(b2).toString();
			}
		}
		return value;
	}

	/**
	 * 强制执行公式（按既定的顺序、反序执行）
	 * @param workbook
	 * @throws WTException
	 */
	public static void performFormula(XSSFWorkbook workbook) throws WTException{
		String [] sheetName=ConstantRI.config_sheetnameB;
		for(int i=0;i<sheetName.length;i++){
			XSSFSheet sheet=workbook.getSheet(sheetName[i]);
			if(sheet==null){
				throw new WTException("计算结果时没有获取到sheet"+sheetName[i]);
			}
			sheet.setForceFormulaRecalculation(true);
		}
		
		for(int i=sheetName.length-1;i>-1;i--){
			XSSFSheet sheet=workbook.getSheet(sheetName[i]);
			sheet.setForceFormulaRecalculation(true);
		}
		
		for(int i=0;i<sheetName.length;i++){
			XSSFSheet sheet=workbook.getSheet(sheetName[i]);
			if(sheet==null){
				throw new WTException("计算结果时没有获取到sheet"+sheetName[i]);
			}
			sheet.setForceFormulaRecalculation(true);
		}
	}
	
	/**
	 * 为下载的文档添加公式
	 * @param dataFilePath
	 * @throws Exception
	 */
	public static String addFormula(String dataFilePath,String baseFilePath) throws Exception{
		File dataFile=new File(dataFilePath);
		File baseFile=new File(baseFilePath);
		
		if(!dataFile.exists() || !baseFile.exists()){
			throw new Exception("为下载文件添加公式时找不到相应文件！");
		}
		FileInputStream dataStream=new FileInputStream(dataFilePath);
		
		XSSFWorkbook datawb=new XSSFWorkbook(dataStream);
		XSSFWorkbook basewb=EncryptHelper.getEncryptWorkbook(baseFilePath);
		
		XSSFSheet dataSheet=null;
		XSSFSheet baseSheet=null;
		XSSFRow dataRow=null;
		XSSFRow baseRow=null;
		XSSFCell dataCell=null;
		XSSFCell baseCell=null;
		String msg="";
		String [] sheetName=ConstantRI.config_sheetnameB;
		msg=compareVersion(datawb,basewb);
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
				String sheet_region=ConstantRI.config_compareversion_cellB;
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
		performFormula(basewb);
		FileOutputStream outputStream=new FileOutputStream(dataFilePath);
		basewb.write(outputStream);
		outputStream.flush();
		outputStream.close();
		basewb.close();
		datawb.close();
		dataStream.close();
		EncryptHelper.encryptExcel(dataFilePath);
		return msg;
	}
	
	/**
	 * 创建电芯材料记录
	 * @param materialMap
	 * @param filePath
	 * @throws Exception 
	 */
	@SuppressWarnings("resource")
	public synchronized static void saveMaterialInfo(Map<String, String> materialMap,String filePath) throws Exception{
		boolean poixmlexception=false;
		CommonHelper.waitFileOperation(filePath);
		GlobalData.inOperationFile.add(filePath);
		XSSFWorkbook workbook=null;
		XSSFSheet sheet=null;
		try{
		workbook=CommonHelper.getWorkBook(filePath);
		sheet=workbook.getSheet(materialMap.get("cls"));
		if(sheet==null){
			sheet=workbook.createSheet(materialMap.get("cls"));
		}
		materialMap.remove("cls");
		
		Set<String> setKey=materialMap.keySet();
		Map<String,Integer> titleCellMap=new HashMap<String,Integer>();
		
		int rowCount=sheet.getLastRowNum()+1;
		XSSFRow titleRow=sheet.getRow(0);
		if(titleRow==null){
			titleRow=sheet.createRow(0);
		}
		
		for(int rowIndex=1;rowIndex<rowCount;rowIndex++){
			XSSFRow valideRow=sheet.getRow(rowIndex);
			if(valideRow!=null){
				XSSFCell valideCell=valideRow.getCell(0);
				if(valideCell!=null){
					String nameVlaue=valideCell.getStringCellValue();
					String name=materialMap.get("name");
					String number=materialMap.get(ConstantRI.iba_attr_pn);
					if(StringUtils.isEmpty(name)|| StringUtils.isEmpty(number)){
						throw new Exception("名称和PN码不能为空！");
					}
					if(nameVlaue.equals(name)){
						throw new Exception("记录表中已存在同名的材料");
					}
				}
			}
		}
		
		XSSFRow row=sheet.getRow(rowCount);
		if(row==null){
			row=sheet.createRow(rowCount);
		}
		
		XSSFCell firstTitleCell=titleRow.getCell(0);
		if(firstTitleCell==null){
			firstTitleCell=titleRow.createCell(0);
			firstTitleCell.setCellValue("name");
		}
		
		String titleValue="";
		for(int i=0;;i++){
			XSSFCell titleCell=titleRow.getCell(i);
			if(titleCell==null){
				break;
			}
			String value=titleCell.getStringCellValue();
			titleValue+=value;
			titleCellMap.put(value, i);
		}
		
		int titleCellIndex=0;
		for(Iterator<String> iterator=setKey.iterator();iterator.hasNext();){
			String title=iterator.next();
			XSSFCell newTitleCell=titleRow.getCell(titleCellIndex);
			if(newTitleCell!=null && !titleValue.contains(title)){
				int titleCellCount=titleRow.getLastCellNum();
				newTitleCell=titleRow.createCell(titleCellCount);
				newTitleCell.setCellValue(title);
				titleCellMap.put(title,titleCellCount);
			}else if(newTitleCell==null){
				newTitleCell=titleRow.createCell(titleCellIndex);
				newTitleCell.setCellValue(title);
				titleCellMap.put(title,titleCellIndex);
			}
			titleCellIndex++;
		}
		
		for(Iterator<String> iterator=setKey.iterator();iterator.hasNext();){
			String key=iterator.next();
			int cellIndex=titleCellMap.get(key);
			XSSFCell cell=row.getCell(cellIndex);
			if(cell==null){
				cell=row.createCell(cellIndex);
			}
			cell.setCellValue(materialMap.get(key));
		}
		}catch(POIXMLException e){
			e.printStackTrace();
			poixmlexception=true;
			GlobalData.inOperationFile.remove(filePath);
			GlobalData.mathexcel_cache.remove(filePath);
			CommonHelper.copyBakToCover(filePath);
			saveMaterialInfo(materialMap, filePath);
		}catch(Exception e){
			e.printStackTrace();
			if(e.getLocalizedMessage().contains("OOXML")){
				poixmlexception=true;
				GlobalData.inOperationFile.remove(filePath);
				GlobalData.mathexcel_cache.remove(filePath);
				CommonHelper.copyBakToCover(filePath);
				saveMaterialInfo(materialMap, filePath);
			}else{
				e.printStackTrace();
				throw new WTException(e.getLocalizedMessage());
			}
		}finally{
			if(!poixmlexception){
				CommonHelper.bakFile(new File(filePath));
				GlobalData.mathexcel_cache.put(filePath,workbook);
				GlobalData.inOperationFile.remove(filePath);
				CommonHelper.CacheWriteToLocal(filePath);
			}
			
		}
	}
	
	/**
	 * 获取记录的材料name,PN信息
	 * @param type
	 * @param sheetName
	 * @param partForLibary
	 * @param maps
	 * @throws Exception 
	 */
	@SuppressWarnings("resource")
	public static void getPartForExcel(String filePath,String type,String sheetName,Set<String> partForLibary,List<Map<String,String>> maps) throws WTException{
		try {
			XSSFWorkbook workbook=CommonHelper.getWorkBook(filePath);
			XSSFSheet sheet=workbook.getSheet(sheetName);
			if(sheet==null){
				return;
			}
			XSSFRow titleRow=sheet.getRow(0);
			int cellNameIndex=0;
			int cellNumberIndex=0;
			if(titleRow!=null){
				int cellCount=titleRow.getLastCellNum();
				for(int tcellIndex=0;tcellIndex<cellCount;tcellIndex++){
					XSSFCell titleCell=titleRow.getCell(tcellIndex);
					if(titleCell!=null){
						String value=titleCell.getStringCellValue();
						if(value.equals("name")){
							cellNameIndex=tcellIndex;
						}else if(value.equals(ConstantRI.iba_attr_pn)){
							cellNumberIndex=tcellIndex;
						}
					}
				}
			}
			
			Map<String,String> map=null;
			int rowCount=sheet.getLastRowNum()+1;
			for(int rowIndex=1;rowIndex<rowCount;rowIndex++){
				XSSFRow row=sheet.getRow(rowIndex);
				if(row==null){
					continue;
				}
				
				XSSFCell cellName = row.getCell(cellNameIndex);
				XSSFCell cellNumber = row.getCell(cellNumberIndex);
				
				String valueName="";
				String valueNumber="";
				if(cellName!=null){
					valueName=cellName.getStringCellValue();
				}
				if(cellNumber!=null){
					valueNumber=cellNumber.getStringCellValue();
				}
				
				if(!partForLibary.contains(valueName)){
					map=new HashMap<String, String>();
					map.put("oid", valueName);
					if(type.equals("name")){
						String suffix="";
						if(valueNumber.startsWith("RD")){
							suffix="RD";
						}else if(valueNumber.startsWith("SP")){
							suffix="SP";
						}else if(valueNumber.startsWith("MD")){
							suffix="MD";
						}
						if(!suffix.isEmpty()){
							valueName=valueName+"("+suffix+")";
						}
						map.put("title", valueName);
					}else if(type.equals("number")){
						map.put("title", valueNumber);
					}
					maps.add(map);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new WTException(e);
		}
	}
	
	/**
	 * 根据名称查询材料记录
	 * @param filePath
	 * @param name
	 * @param db
	 * @throws WTException
	 */
	public static void getMaterialByName(String filePath,String name,MaterialDB db)throws WTException{
		try {
			XSSFWorkbook workbook=CommonHelper.getWorkBook(filePath);
			Map<String, Integer> cellInfo=new HashMap<String, Integer>();
			List<MaterialAttr> attrs=new ArrayList<MaterialAttr>();
			
			int sheetCount=workbook.getNumberOfSheets();
			for(int sheetIndex=0;sheetIndex<sheetCount;sheetIndex++){
				XSSFSheet sheet=workbook.getSheetAt(sheetIndex);
				if(sheet==null){
					continue;
				}
				List<Map<String, String>> typematerials=CommonHelper.getTypeMaterial(sheet.getSheetName());
				
				int rowCount=sheet.getLastRowNum()+1;
				int nameIndex=0;
				int pnIndex=0;
				for(int rowIndex=0;rowIndex<rowCount;rowIndex++){
					XSSFRow row=sheet.getRow(rowIndex);
					if(row==null){
						continue;
					}
					
					int cellCount=row.getLastCellNum();
					if(rowIndex==0){
						for(int cellIndex=0;cellIndex<cellCount;cellIndex++){
							XSSFCell cell=row.getCell(cellIndex);
							if(cell==null){
								continue;
							}
							String value=cell.getStringCellValue();
							if(value!=null && !value.isEmpty()){
								for(int typeMaterialIndex=0;typeMaterialIndex<typematerials.size();typeMaterialIndex++){
									Map<String, String> typeMap=typematerials.get(typeMaterialIndex);
									String attrName=typeMap.get(ConstantRI.config_material_name);
									String region=typeMap.get(ConstantRI.config_material_region);
									region=CommonHelper.getEngRegion(region);
									String style=typeMap.get(ConstantRI.config_material_style);
									if(value.equals(attrName)){
										cellInfo.put(attrName+","+region+","+style, cellIndex);
									}
								}
								
								if(value.equals(ConstantRI.iba_attr_pn)){
									pnIndex=cellIndex;
								}else if(value.equals("name")){
									nameIndex=cellIndex;
								}
							}
						}
					}else{
						XSSFCell cell=row.getCell(nameIndex);
						if(cell==null){
							return;
						}
						String value=cell.getStringCellValue();
						if(value!=null && !value.isEmpty() && value.equals(name)){
							for(Map.Entry<String, Integer> entry:cellInfo.entrySet()){
								MaterialAttr attr=new MaterialAttr();
								String [] key=entry.getKey().split(",");
								cell=row.getCell(entry.getValue());
								if(cell==null){
									continue;
								}
								attr.setName(key[0]);
								attr.setRegion(key[1]);
								attr.setStyle(key[2]);
								attr.setValue(cell.getStringCellValue());
								attrs.add(attr);
								if(entry.getValue()==pnIndex){
									db.setClf(sheet.getSheetName());
						    		db.setLoadding("");
						    		db.setMaterialname(value);
						    		db.setRecipenumber("");
						    		db.setPn(cell.getStringCellValue());
						    		db.setIsPhantom("是");
								}
							}
							db.setAttr(attrs);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new WTException(e);
		}
	}
	public static String compareVersion(XSSFWorkbook datawb, XSSFWorkbook basewb) {
		String sheet_region=ConstantRI.config_compareversion_cellB;
		String sheetname=sheet_region.split("!")[0];
		Sheet datasheet=datawb.getSheet(sheetname);
		Sheet basesheet=basewb.getSheet(sheetname);
		String region=sheet_region.split("!")[1];
		int[] column=CommonHelper.translateRegion(region);
		Row datarow=datasheet.getRow(column[1]);
		Cell datacell=datarow.getCell(column[0]);
		Row baserow=basesheet.getRow(column[1]);
		Cell basecell=baserow.getCell(column[0]);
		String v1=datacell.getStringCellValue();
		String v2=basecell.getStringCellValue();
		if(!StringUtils.isEmpty(v1)&&!StringUtils.isEmpty(v2)&&!v1.equals(v2)){
			return datacell.getStringCellValue()+"###"+basecell.getStringCellValue();
		}
		return "";
	}

	public static XSSFWorkbook clearSheetFormula(String filePath, String sheetname) throws InvalidFormatException, IOException, GeneralSecurityException, WTException {
		XSSFWorkbook workbook = EncryptHelper.getEncryptWorkbook(filePath);
		XSSFSheet sheet = workbook.getSheet(sheetname);
			for (int i = 0; i < 1000; i++) {
				XSSFRow row = sheet.getRow(i);
				for (int j = 0; j < 100; j++) {
					if (row == null) {
						continue;
					}
					XSSFCell cell = row.getCell(j);
					if (cell == null) {
						continue;
					}
					String value="";
					if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
						try {
							cell.setCellType(Cell.CELL_TYPE_STRING);
							value=cell.getStringCellValue();
							value=CommonHelper.transformvaluetodisplay(cell, value);
							value=ExcelUtil.handleFillvalue(cell,value);//填写前处理表单传来的值
							if(CommonHelper.isNumeric(value)){
								cell.setCellType(Cell.CELL_TYPE_NUMERIC);
								cell.setCellValue(Double.parseDouble(value));
							}else if(StringUtils.isEmpty(value)){
								cell.setCellType(Cell.CELL_TYPE_BLANK);
							}else{
								cell.setCellValue(value);
							}						
						} catch (IllegalStateException e) {
							cell.setCellType(Cell.CELL_TYPE_STRING);
						} finally {
							if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
								cell.setCellType(Cell.CELL_TYPE_STRING);
							}
						}
					}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						value=CommonHelper.getNumformatValue(cell);
						try {
							String str="";
							if(value.endsWith("%")){
								XSSFCellStyle cellStyle = cell.getCellStyle();  
								System.out.println(value);
								if(value.endsWith(".00%")||value.indexOf(".")==-1){
						            cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0%"));  
								}else{
									cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));  
								}
								cell.setCellStyle(cellStyle); 
							}else if(CommonHelper.isNumeric(value)){
								value=String.valueOf(value);
							    value=String.valueOf(Float.valueOf(value));
								str=value.replaceAll("\\d+?", "0");
								str=transToDataFormat(str);
								XSSFCellStyle cellStyle = cell.getCellStyle();  
					            cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat(str));  
					            cell.setCellStyle(cellStyle); 
							}
						     
						}catch(Exception e){
						}
					}
				}
			}
			return workbook;
	}

}
