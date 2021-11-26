package com.catl.battery.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFDataFormatter;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import wt.util.WTException;

import com.catl.battery.constant.ConstantBattery;
import com.catl.battery.constant.GlobalData;
import com.catl.battery.entity.CellAttr;
import com.catl.battery.helper.CommonHelper;

public class TestReadExcel {
	public static void main(String[] args) throws Exception {
		dropDownList42007("E://tmp//bak//work//111.xlsx","05.设计输出");
	}

	public synchronized static JSONArray getExcelValue(String filePath,String sheetName) throws IOException{

		List<CellAttr> list=new ArrayList<CellAttr>();
		XSSFWorkbook workbook=null;
		if(GlobalData.mathexcel_cache.containsKey(filePath)){
			workbook=GlobalData.mathexcel_cache.get(filePath);
		}else{
			FileInputStream fileInputStream=new FileInputStream(new File(filePath));
			workbook=new XSSFWorkbook(fileInputStream);
		}
		XSSFFormulaEvaluator evaluator=new XSSFFormulaEvaluator(workbook);
		XSSFSheet sheet=workbook.getSheet(sheetName);
		XSSFRow row=null;
		XSSFCell cell=null;
		CellAttr cellAttr=null;
		for(int rowIndex=0;rowIndex<ConstantBattery.config_limitexcel_rowcount;rowIndex++){
			row=sheet.getRow(rowIndex);
			if(row==null){
				continue;
			}
			for(int cellIndex=0;cellIndex<ConstantBattery.config_limit_colcount;cellIndex++){
				cell=row.getCell(cellIndex);
				if(cell==null){
					continue;
				}
				
				cellAttr=new CellAttr();
				cellAttr.setStyle("实数");
				cellAttr.setRegion(indexToColumn(cell.getColumnIndex()+1)+(rowIndex+1));
				
				XSSFCellStyle cellStyle=cell.getCellStyle();
				String colorCode="#FFF";
				if(cellStyle.getFillForegroundColorColor()!=null){
					colorCode="#"+cellStyle.getFillForegroundColorColor().getARGBHex().substring(2);
				}
				cellAttr.setColor(colorCode);
				switch(cell.getCellType()){
					case Cell.CELL_TYPE_BLANK:
						cellAttr.setValue("");
						break;
					case Cell.CELL_TYPE_STRING: 
						cellAttr.setStyle("字符串");
						cellAttr.setValue(cell.getStringCellValue());
						break;
					case Cell.CELL_TYPE_NUMERIC:
						HSSFDataFormatter dataformatter=new HSSFDataFormatter();
						String cellstring=dataformatter.formatCellValue(cell);
						if(cellstring.matches("^((-?\\d+.?\\d*)[Ee]{1}(-?\\d+))$")){
							BigDecimal bd = new BigDecimal(cellstring);  
							cellstring=bd.toPlainString();
						}
						cellAttr.setValue(cellstring);
						break;
					case Cell.CELL_TYPE_FORMULA:
						String cellstring1=getFormulaValue(cell,cellAttr,evaluator);
						if(cellstring1.matches("^((-?\\d+.?\\d*)[Ee]{1}(-?\\d+))$")){
							BigDecimal bd = new BigDecimal(cellstring1);  
							cellstring1=bd.toPlainString();
						}
						String format=cell.getCellStyle().getDataFormatString();
						format=getStrNumberic(format);
						if(isNumeric(cellstring1)){
							if(format.equals("General")){//General默认三位小数，如果需要变化更改计算模版
								format="0.000";
							}
							format=format.split("_")[0];
							try{
								DecimalFormat df = new DecimalFormat(format);
								double value=Double.valueOf(cellstring1);
								cellstring1=df.format(value);
							}catch(Exception e){
								e.printStackTrace();
							}
						}
						cellAttr.setValue(cellstring1);
						cellAttr.setStyle("显示");
						break;
					case Cell.CELL_TYPE_ERROR:
						cellAttr.setValue(cell.getErrorCellString());
						break;
					default:break;
				}
				list.add(cellAttr);
			}
		}
		return JSONArray.fromObject(list);
	
	}
    
	private static String getStrNumberic(String str) {
		String oldstr=str;
		Pattern p = Pattern.compile("(\\d+\\.\\d+)");
        Matcher m = p.matcher(str);
        if (m.find()) {
            str = m.group(1) == null ? "" : m.group(1);
        } else {
            p = Pattern.compile("(\\d+)");
            m = p.matcher(str);
            if (m.find()) {
                str = m.group(1) == null ? "" : m.group(1);
            }
        }
        if(oldstr.contains("%")){
        	return str+"%";
        }
        return str;
	}
	
	/**  
	 * 判断字符串是否为数字  
	 *   
	 * @param str  
	 * @return  
	 */  
	public static boolean isNumeric(String str) {
		boolean flag=false;
		try{
		Double.parseDouble(str.trim());
		flag=true;
		}catch(Exception e){
		}
		return flag;
	}  
	
	/**
	* 用于将excel表格中列索引转成列号字母，从A对应1开始
	* 
	* @param index
	*            列索引
	* @return 列号
	*/
	private static String indexToColumn(int index) {
	        index--;         
	        String column = "";         
	        do {
	        	if (column.length() > 0) {
	        		index--;
	            }
                column = ((char) (index % 26 + (int) 'A')) + column;
                index = (int) ((index - index % 26) / 26);
	        } while (index > 0);
	        return column;
	}
	public static String getFormulaValue(XSSFCell cell, CellAttr cellAttr,XSSFFormulaEvaluator evaluator){
		String value="";
		boolean flag=false;
		try{
		evaluator.evaluateFormulaCell(cell);
		}catch(Exception e){
			evaluator.clearAllCachedResultValues();
			if(!cell.getRawValue().equals("#N/A")){
				 flag=true;
			}
			System.out.println(e.getLocalizedMessage());
		}
		System.out.println(cellAttr.getRegion()+"---"+cell.getRawValue());
		if(flag){
			value="0";
		}else if(cell.getCachedFormulaResultType()==Cell.CELL_TYPE_NUMERIC){
			value =cell.getNumericCellValue()+"";
		}else if(cell.getCachedFormulaResultType()==Cell.CELL_TYPE_STRING){
			value =cell.getStringCellValue();
		}else if((cell.getCachedFormulaResultType()==Cell.CELL_TYPE_ERROR)){
			value =cell.getErrorCellString();
		}else if((cell.getCachedFormulaResultType()==Cell.CELL_TYPE_BOOLEAN)){
			value =cell.getBooleanCellValue()+"";
		}
		return value;
				
	}
	
	
	public static void dropDownList42007(String filePath,String name)
	        throws Exception {
		XSSFWorkbook workbook=null;
		if(GlobalData.mathexcel_cache.containsKey(filePath)){
			workbook=GlobalData.mathexcel_cache.get(filePath);
		}else{
			FileInputStream fileInputStream=new FileInputStream(new File(filePath));
			workbook=new XSSFWorkbook(fileInputStream);
		}
		  XSSFSheet sheet=workbook.getSheet(name);//
//	    XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
//	    XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper
//	            .createFormulaListConstraint("=材料数据库!$D$140:$D$144");
//	    CellRangeAddressList regions1 = new CellRangeAddressList(5, 5, 1, 1);
//	    DataValidation dataValidation = dvHelper.createValidation(dvConstraint, regions1);
//	    dataValidation.createErrorBox("错误提示", "请从下拉框中选择");
//	    sheet.addValidationData(dataValidation);
//	    
	    List<XSSFDataValidation> a=sheet.getDataValidations();
	    for(int i=0;i<a.size();i++){
	    	XSSFDataValidation avalidation=a.get(i);
	    	CellRangeAddressList regions=avalidation.getRegions();
	    	for (int j = 0; j < regions.countRanges(); j++) {
				CellRangeAddress address=regions.getCellRangeAddress(j);
				int startrow=address.getFirstRow();
				int startcol=address.getFirstColumn();
				if(startrow<ConstantBattery.config_limitexcel_rowcount&&startcol<ConstantBattery.config_limit_colcount){
					String str=address.formatAsString();
					System.out.println(str);
					DataValidationConstraint cons=avalidation.getValidationConstraint();
			    	System.out.println(cons.getFormula1());
					continue;
				}
			}
	    
	    }
	   // FileOutputStream stream = new FileOutputStream(filePath);
	    //workbook.write(stream);
	    //stream.close();
//	    addressList = null;
//	    validation = null;
	}
	
	  public static HSSFDataValidation setDataValidationList(short firstRow,short firstCol,short endRow, short endCol){
	    	//设置下拉列表的内容
	    	String[] textlist={"列表1","列表2","列表3","列表4","列表5"};
	    	
	    	//加载下拉列表内容
	    	DVConstraint constraint=DVConstraint.createExplicitListConstraint(textlist);
	    	//设置数据有效性加载在哪个单元格上。
			
			//四个参数分别是：起始行、终止行、起始列、终止列
	    	CellRangeAddressList regions=new CellRangeAddressList(firstRow,firstCol,endRow,endCol);
	    	//数据有效性对象
	    	HSSFDataValidation data_validation_list = new HSSFDataValidation(regions, constraint);
	    	
	    	return data_validation_list;
	    }
	    public static HSSFDataValidation setDataValidationView(short firstRow,short firstCol,short endRow, short endCol){
	    	//构造constraint对象
	    	DVConstraint constraint=DVConstraint.createCustomFormulaConstraint("B1");
	    	//四个参数分别是：起始行、终止行、起始列、终止列
	    	CellRangeAddressList regions=new CellRangeAddressList(firstRow,firstCol,endRow,endCol);
	    	//数据有效性对象
	    	HSSFDataValidation data_validation_view = new HSSFDataValidation(regions, constraint);
	    	
	    	return data_validation_view;
	    }

	
	public static void saveMaterialInfo(Map<String, String> materialMap,String filePath) throws IOException{
		File file=new File(filePath);
		FileInputStream inputStream=null;
		XSSFWorkbook workbook=null;
		XSSFSheet sheet=null;
		if(file.exists()){
			inputStream=new FileInputStream(file);
			workbook=new XSSFWorkbook(inputStream);
		}else{
			workbook=new XSSFWorkbook();
		}
		
		sheet=workbook.getSheet(materialMap.get("clf"));
		if(sheet==null){
			sheet=workbook.createSheet(materialMap.get("clf"));
		}
		materialMap.remove("clf");
		
		Set<String> setKey=materialMap.keySet();
		Map<String,Integer> titleCellMap=new HashMap<String,Integer>();
		
		int rowCount=sheet.getLastRowNum()+1;
		XSSFRow titleRow=sheet.getRow(0);
		if(titleRow==null){
			titleRow=sheet.createRow(0);
		}
		
		XSSFRow row=sheet.getRow(rowCount);
		if(row==null){
			row=sheet.createRow(rowCount);
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
		FileOutputStream outputStream=new FileOutputStream(new File(filePath));
		workbook.write(outputStream);
		outputStream.flush();
		outputStream.close();
		workbook.close();
	}
	
	
	/**
	 * 将用户所填的值写入Excel
	 * @param filePath
	 * @param sheetName
	 * @param values
	 * @throws Exception
	 */
	public synchronized static void setValuesToTempExcel(String filePath,String sheetName,Map<String,String> values) throws Exception{
		FileInputStream fileInputStream=null;
		int [] column=null;
		String region=null;
		String value="";
		XSSFWorkbook workbook=null;
		if(GlobalData.mathexcel_cache.containsKey(filePath)){
			workbook=GlobalData.mathexcel_cache.get(filePath);
		}else{
			File file=new File(filePath);
			if(file.exists()){
				fileInputStream=new FileInputStream(file);
				workbook=new XSSFWorkbook(fileInputStream);
				fileInputStream.close();
			}else{
				throw new WTException(filePath+"文件不存在");
			}
				
		}
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
					value=handleFillvalue(value);//填写前处理表单传来的值
					if(CommonHelper.isNumeric(value)){
						cell.setCellValue(Double.parseDouble(value));
					}else if(cell.getCellType()==Cell.CELL_TYPE_FORMULA){
						continue;
					}else if(StringUtils.isEmpty(value)){
						cell.setCellType(Cell.CELL_TYPE_BLANK);
					}else if(value.startsWith("=")){
						cell.setCellFormula(value.replace("=", ""));
					}else{
						cell.setCellValue(value);
					}
				}else{
					throw new WTException("计算模版不存在"+region+"单元格");
				}
			}
			performFormula(workbook);
			FileOutputStream outputStream=new FileOutputStream(filePath);
			workbook.write(outputStream);
			outputStream.flush();
			outputStream.close();
			workbook.close();
	}
	
	/**
	 * 强制执行公式（按既定的顺序、反序执行）
	 * @param workbook
	 * @throws WTException
	 */
	public static void performFormula(XSSFWorkbook workbook) throws WTException{
		String [] sheetName=ConstantBattery.config_sheetname;
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
	
	private static String handleFillvalue(String value) {
		value=String.valueOf(value);
		boolean isprecent=value.matches("^((\\d+\\.?\\d*)|(\\d*\\.\\d+))\\%$");
		if(isprecent){//百分数换算为小数
		    BigDecimal b1 = new BigDecimal(Double.toString(Double.valueOf(value.replace("%", ""))));
	        BigDecimal b2 = new BigDecimal(Double.toString(100));
	        value=b1.divide(b2).toString();
		}
		return value;
	}
}
