package com.catl.battery.test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.catl.battery.constant.ConstantBattery;
import com.catl.battery.constant.GlobalData;
import com.catl.battery.helper.CacheHelper;
import com.catl.battery.helper.CommonHelper;
import com.catl.battery.service.impl.SelectMaterialServiceImpl;
import com.catl.battery.util.CommonUtil;
import com.catl.battery.util.ExcelUtil;
import com.catl.line.util.FolderUtil;

import wt.doc.WTDocument;
import wt.folder.Folder;
import wt.inf.container.WTContainer;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.util.WTException;

public class Test implements RemoteAccess {
	public static void main(String[] args) throws WTException {
//		String a="$4E$4*22";
//		a=a.replaceAll("\\$", "");
//		System.out.println(a);
//		String filePath="E://doc/battery_material.xlsx";
//		String type="name";
//		String sheetName="Anode_RD";
//		Set<String> partForLibary=new HashSet<String>();
//		List<Map<String,String>> maps=new ArrayList<Map<String,String>>();
//		getPartForExcel(filePath, type, sheetName, partForLibary, maps);
//		System.out.println("ok"+maps);
		List a=new ArrayList();
		a.add("3");
		a.add("1");
		a.add("2");
		a.add("A");
		Collections.sort(a);
		for (int i = 0; i < a.size(); i++) {
			System.out.println(a.get(i));
		}
	}

	public static void main1(String[] args) {
		
       String regions="B23:F23";
		String region1=regions.split(":")[0];
		int[] rint1=CommonHelper.translateRegion(region1);
		String region2=regions.split(":")[1];
		int[] rint2=CommonHelper.translateRegion(region2);
		if(rint1[0]==rint2[0]){//英文部分相同
			String eng=CommonHelper.getEngRegion(region1);
			for (int i = 0; i <=rint2[1]-rint1[1]; i++) {
				String res=(eng+((rint1[1]+1)+i));
				System.out.println("--"+res);
			}
		}else if(rint1[1]==rint2[1]){
			for (int i = 0; i <=rint2[0]-rint1[0]; i++) {
				String res=CommonHelper.indexToColumn((rint1[0]+1)+i)+(rint1[1]+1);
				System.out.println("--"+res);
			}
		}
	
		// TODO Auto-generated method stub
		//readExcel();
//		String filePath="E://doc";
//		Map<String, String> values=new HashMap<String, String>();
//		values.put("C6", "测试");
//		values.put("I6", "12.09");
//		values.put("J6", "10%");
//		try {
//			ExcelUtil.setValuesToTempExcel(filePath, "材料数据库", values);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		Pattern checkKey=Pattern.compile("^(\\-|\\+)?\\d+(\\.\\d+|\\%)?$");
//		Matcher matcher=checkKey.matcher("1.1");
//		if(matcher.matches()){
//			System.out.println("ok");
//		}
	}

	public static void readExcel() {
		try {
			FileInputStream stream = new FileInputStream(new File("E://tmp//battery_math.xlsx"));
			XSSFWorkbook workbook = new XSSFWorkbook(stream);
			XSSFSheet sheet = workbook.getSheet("极耳错位");
//			XSSFSheet sheet2 = workbook.getSheetAt(1);
//			sheet2.setForceFormulaRecalculation(true);
			
			for(int i=0;i<3;i++){
				XSSFRow row = sheet.getRow(i);
				for(int j=0;j<3;j++){
					if(row==null){
						continue;
					}
					XSSFCell cell = row.getCell(j);
					if(cell==null){
						continue;
					}
					XSSFCellStyle cellStyle=cell.getCellStyle();
					XSSFColor color=cellStyle.getFillForegroundColorColor();
					int co=cellStyle.getFillBackgroundColor();
					System.out.println(i+"-"+j+"####:"+color.getARGBHex().substring(2));

				}
			}
			
			FileOutputStream stream2 = new FileOutputStream("E://测试2.xlsx");
			workbook.write(stream2);
			workbook.close();
			stream2.close();
			stream.close();
			System.out.println("ok!!!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void createPhantom() throws Exception {
		// TODO Auto-generated method stub
		String materialType="阳极";
		String name="测试PN-1031";
		String jsonStr="{\"D6\":\"TEST-1031-941\",\"E6\":\"测试ing\"}";
		
		WTContainer container=CommonUtil.getLibrary(ConstantBattery.config_libary_batterymaterial);
		System.out.println("container--->:"+container);
		String folderPath=ConstantBattery.phantom_folderpath.replace("XXX", materialType);
		Folder folder = FolderUtil.getFolder(folderPath, container);
		
		@SuppressWarnings("unchecked")
		Map<String,String> map=JSONObject.fromObject(jsonStr);
		File configFile=new File(ConstantBattery.config_battery_xls_path);
		String[][] result =ExcelUtil.getData(0, null, configFile, 0, false);
		for(Map.Entry<String, String> entry:map.entrySet()){
			List<Map<String, String>> maps=SelectMaterialServiceImpl.getSheetInfo(result, ConstantBattery.config_material_region, entry.getKey());
			for(int i=0;i<maps.size();i++){
				Map rowInfo=maps.get(i);
				String clfattr=(String) rowInfo.get(ConstantBattery.config_material_name);
				map.put(clfattr, entry.getValue());
				map.remove(entry.getKey());
			}
		}
		
		CommonUtil.createPart(map.get("CTAL_PN"),name, folder, ConstantBattery.part_type, "", "", map);
	}
	
	public static XSSFWorkbook ExportIfConf(String filePath,String configPath) throws Exception{
		File file=new File(configPath);
		FileInputStream stream=new FileInputStream(file);
		XSSFWorkbook confWorkbook=new XSSFWorkbook(stream);
		XSSFSheet confSheet=confWorkbook.getSheet("参数导出配置");
		
		XSSFWorkbook dataWorkbook=CommonHelper.getWorkBook(filePath);
		XSSFSheet dataSheet=null;
		String existSheetName=null;
		int [] dataColumn=null;
		XSSFRow dataRow=null;
		XSSFCell dataCell=null;
		int confValueIndex=1;
		
		CacheHelper.loadExcelConfig();
		List<Map<String,String>> exportconfig_info=GlobalData.exportconfig_info;
		
		for(int i=0;i<exportconfig_info.size();i++){
			Map<String,String> map=exportconfig_info.get(i);
			String sheetName=map.get(ConstantBattery.config_exportconfig_name);
			if(existSheetName==null || !existSheetName.equals(sheetName)){
				existSheetName=sheetName;
				dataSheet=dataWorkbook.getSheet(sheetName);
			}
			dataColumn=CommonHelper.translateRegion(map.get(ConstantBattery.config_exportconfig_region));
			dataRow=dataSheet.getRow(dataColumn[1]);
			dataCell=dataRow.getCell(dataColumn[0]);
			if(dataCell!=null){
				XSSFRow confRow=confSheet.getRow(confValueIndex);
				XSSFCell confCell=confRow.getCell(3);
				switch(dataCell.getCellType()){
					case Cell.CELL_TYPE_NUMERIC:
						confCell.setCellValue(dataCell.getNumericCellValue());
						break;
					case Cell.CELL_TYPE_STRING:
						confCell.setCellValue(dataCell.getStringCellValue());
						break;
					case Cell.CELL_TYPE_FORMULA:
						System.out.println("fuck");
						break;
				}
			}
			confValueIndex++;
		}
		File file2=new File("E://doc/结果.xlsx");
		FileOutputStream fileOutputStream=new FileOutputStream(file2);
		confWorkbook.write(fileOutputStream);
		dataWorkbook.close();
		stream.close();
		confWorkbook.close();
		fileOutputStream.close();
		return confWorkbook;
	}
	
	/**
	 * 获取记录的材料信息
	 * @param type
	 * @param sheetName
	 * @param partForLibary
	 * @param maps
	 * @throws Exception 
	 */
	public static void getPartForExcel(String filePath,String type,String sheetName,Set<String> partForLibary,List<Map<String,String>> maps) throws WTException{
		File file=new File(filePath);
		if(!file.exists()){
			throw new WTException("还没有创建材料记录！");
		}
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			XSSFWorkbook workbook=new XSSFWorkbook(fileInputStream);
			XSSFSheet sheet=workbook.getSheet(sheetName);
			
			XSSFRow titleRow=sheet.getRow(0);
			int cellCount=titleRow.getLastCellNum();
			int cellNameIndex=0;
			int cellNumberIndex=0;
			for(int tcellIndex=0;tcellIndex<cellCount;tcellIndex++){
				String value=titleRow.getCell(tcellIndex).getStringCellValue();
				if(value.equals("name")){
					cellNameIndex=tcellIndex;
				}else if(value.equals("CTAL_PN")){
					cellNumberIndex=tcellIndex;
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
					if(type.equals("name")){
						map.put("title", valueName);
					}else if(type.equals("number")){
						map.put("title", valueNumber);
					}
					map.put("oid", valueName);
					maps.add(map);
				}
			}
			workbook.close();
			fileInputStream.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WTException(e);
		}
	}
}
