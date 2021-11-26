package com.catl.battery.helper;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.channels.FileLock;
import java.security.GeneralSecurityException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import net.sf.json.JSONArray;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFDataFormatter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import wt.doc.WTDocument;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.FileUtil;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTRuntimeException;

import com.catl.battery.constant.ConstantBattery;
import com.catl.battery.constant.GlobalData;
import com.catl.battery.entity.CellAttr;
import com.catl.battery.entity.MaterialAttr;
import com.catl.battery.entity.MaterialDB;
import com.catl.line.util.IBAUtil;
import com.catl.line.util.WCUtil;


public class CommonHelper {
	private static final Logger logger = Logger.getLogger(CommonHelper.class.getName());
	private static String wt_home = "";
	static {
		try {
			wt_home = WTProperties.getLocalProperties().getProperty("wt.home",
					"UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static List<Map<String, String>> getTypeMaterial(String type) throws FileNotFoundException, IOException {
		CacheHelper.loadExcelConfig();
		List<Map<String, String>> infos=GlobalData.material_libary_info;
		List<Map<String, String>> typeinfos=new ArrayList<Map<String, String>>();
		for (int i = 0; i < infos.size(); i++) {
			Map infomap=infos.get(i);
			if(infomap.get(ConstantBattery.config_material_type).equals(type)){
				typeinfos.add(infomap);
			}
		}
		return typeinfos;
	}
	
	public static List<Map<String, String>> getTypeAsm(String type) throws FileNotFoundException, IOException {
		CacheHelper.loadExcelConfig();
		List<Map<String, String>> infos=GlobalData.asm_libary_info;
		List<Map<String, String>> typeinfos=new ArrayList<Map<String, String>>();
		for (int i = 0; i < infos.size(); i++) {
			Map infomap=infos.get(i);
			if(infomap.get(ConstantBattery.config_asm_type).equals(type)){
				typeinfos.add(infomap);
			}
		}
		return typeinfos;
	}

	public static List<MaterialAttr> getTypeAttrs(List<Map<String, String>> typematerials, WTPart part, int index, MaterialDB db) {
		List<MaterialAttr> attrs=new ArrayList<MaterialAttr>();
		for (int i = 0; i < typematerials.size(); i++) {
			Map<String, String> map=typematerials.get(i);
			MaterialAttr attr=new MaterialAttr();
			String attrname=map.get(ConstantBattery.config_material_name);
			String region=map.get(ConstantBattery.config_material_region);
			String[] engandnum=translateEngAndNum(region);
			if(index!=0){
				region=getRegion(region,index);
			}
			String style=map.get(ConstantBattery.config_material_style);
			if(attrname.equals("name")){
				attr.setName(attrname);
				attr.setRegion(region);
				attr.setStyle(style);
				attr.setValue(part.getName());
			}else if(attrname.equals("isphantom")){
				attr.setName(attrname);
				attr.setRegion(region);
				attr.setStyle(style);
				attr.setValue(db.getIsPhantom());
			}else if(attrname.equals("CATL_PN")){
				attr.setName(attrname);
				attr.setRegion(region);
				attr.setStyle(style);
				attr.setValue(part.getNumber());
			}else{
				attr.setName(attrname);
				attr.setRegion(region);
				attr.setStyle(style);
				Object value="";
				if(style.equals(ConstantBattery.config_type_double)){
					value=IBAUtil.getDoubleIBAValue(part, attrname);
				}
				if(style.equals(ConstantBattery.config_type_string)){
					value=IBAUtil.getStringIBAValue(part, attrname);
				}
				attr.setValue(value);
			}
			attrs.add(attr);
		}
		return attrs;
	}

	private static String getRegion(String region, int index) {
		String[] engandnum=translateEngAndNum(region);
		return engandnum[0]+(Integer.valueOf(engandnum[1])+index);
	}
	public static String getEngRegion(String region) {
		String[] engandnum=translateEngAndNum(region);
		return engandnum[0];
	}
	public static int getTypesIndex(List types, String type) {
		int count=0;
		for (int i = 0; i <types.size(); i++) {
			if(types.get(i).equals(type)){
				count++;
			}
		}
		return count;
	}
	public static String[] translateEngAndNum(String word) {
		 String [] nums=new String[2];
			Pattern p=Pattern.compile("[A-Z]{1,3}");
			Matcher m=p.matcher(word);
			while(m.find()){
					String ab=m.group();
					nums[0]=ab;
					String number=word.replace(ab, "");
					nums[1]=number;
			}
		 return nums;
	}
	public static int[] translateRegion(String word) {
		 int [] nums=new int[2];
			Pattern p=Pattern.compile("[A-Z]{1,3}");
			Matcher m=p.matcher(word);
			while(m.find()){
					String ab=m.group();
					nums[0]=translateEngToNum(ab);
					String number=word.replace(ab, "");
					nums[1]=Integer.parseInt(number)-1;
			}
		 return nums;
	}
	public static int translateEngToNum(String  word) {
		int num = 0;
        int result = 0;
        int length =word.length(); 
        for(int i = 0; i < length; i++) {
            char ch = word.charAt(length - i - 1);
            num = (int)(ch - 'A' + 1) ;
            num *= Math.pow(26, i);
            result += num;
        }
        return result-1;
	}
	public static void main(String[] args) throws Exception {
		JSONArray array=getExcelValue("E://doc/123XXX(MD).xlsx", "设计主界面");
		System.out.println(array.toString());
	}

	/**
	 * 获取制定sheet的值与所在列
	 * @param filePath
	 * @param sheetName
	 * @return
	 * @throws Exception 
	 */
	public  static JSONArray getExcelValue(String filePath,String sheetName) throws Exception{
		List<CellAttr> list=new ArrayList<CellAttr>();
		XSSFWorkbook workbook=getWorkBook(filePath);
		XSSFSheet sheet=workbook.getSheet(sheetName);
		XSSFRow row=null;
		XSSFCell cell=null;
		Map dropdownconfig=dropDownList(workbook, sheetName);
		List colorconfig=colorconfig(workbook, sheetName);
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
				CellAttr cellAttr=new CellAttr();
				cellAttr.setRegion(indexToColumn(cell.getColumnIndex()+1)+(rowIndex+1));
				cellAttr=getCellValue(cell,cellAttr);
				setSpeicalDown(dropdownconfig, sheet, cellAttr,cell);
				setConfigColor(colorconfig,cellAttr,cell);
				list.add(cellAttr);
			}
		}
		return JSONArray.fromObject(list);
	}
	
	

	private static void setConfigColor(List colorconfig, CellAttr cellAttr,XSSFCell cell) throws ScriptException, WTException {
		String region=cellAttr.getRegion();
		for (int i = 0; i < colorconfig.size(); i++) {
			Map map=(Map) colorconfig.get(i);
			String current_region=(String) map.get(ConstantBattery.config_colorconfig_region);
			current_region=CommonHelper.getColorRangeRegions(current_region);
			if(current_region.contains(region+",")){
				String iswhile=(String) map.get(ConstantBattery.config_colorconfig_while);
				ScriptEngineManager manager = new ScriptEngineManager();  
			      ScriptEngine engine = manager.getEngineByName("js");  
			      engine.put("value", cellAttr.getValue());
			      if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC){
			    	  if(isNumeric(cellAttr.getValue().toString())){
			    		  engine.put("value", Double.parseDouble(cellAttr.getValue().toString()));  
			    	  }
			      }
			      try{
			       Boolean result = (Boolean) engine.eval(iswhile);
			       if(result){
			    	   String color=(String) map.get(ConstantBattery.config_colorconfig_color);
			    	   cellAttr.setColor(color);
			       }
			      }catch(Exception e){
			    	  e.printStackTrace();
			    	  throw new WTException(region+"判断颜色时出错!");
			      }
			}
		}
	}

	private static List colorconfig(XSSFWorkbook workbook, String sheetName) throws FileNotFoundException, IOException {
		CacheHelper.loadExcelConfig();
		List list=new ArrayList();
		List<Map<String, String>> colorinfos=GlobalData.colorconfig_info;
		for (int i = 0; i < colorinfos.size(); i++) {
			Map<String, String> rowmap=colorinfos.get(i);
			String name=rowmap.get(ConstantBattery.config_colorconfig_name);
			String region=rowmap.get(ConstantBattery.config_colorconfig_region);
			if(name!=null&&name.equals(sheetName)){
				list.add(rowmap);
			}
	    }
		return list;
	}

	/**
	 * 获取制定sheet有公式cell的值与所在列
	 * @param filePath
	 * @param sheetName
	 * @return
	 * @throws Exception 
	 */
	public  static JSONArray getFormulaValue(String region,String filePath,String sheetName) throws Exception{
		List<CellAttr> list=new ArrayList<CellAttr>();
		XSSFWorkbook workbook=getWorkBook(filePath);
		XSSFSheet sheet=workbook.getSheet(sheetName);
		XSSFRow row=null;
		XSSFCell cell=null;
		Map doropdownconfig=dropDownList(workbook, sheetName);
		List colorconfig=colorconfig(workbook, sheetName);
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
			    String cregion=indexToColumn(cell.getColumnIndex()+1)+(rowIndex+1);
			    if(cregion.equals(region)||cell.getCellType()==Cell.CELL_TYPE_FORMULA){
					CellAttr cellAttr=new CellAttr();
					cellAttr.setRegion(cregion);
					cellAttr=getCellValue(cell,cellAttr);
					setSpeicalDown(doropdownconfig,sheet,cellAttr,cell);
					setConfigColor(colorconfig, cellAttr, cell);
					list.add(cellAttr);
				}
			}
		}
		return JSONArray.fromObject(list);
	
	}
	private static void setSpeicalDown(Map doropdownconfig, XSSFSheet sheet, CellAttr cellAttr, XSSFCell cell) {
		if(doropdownconfig.containsKey(cellAttr.getRegion())){
			cellAttr.setStyle("下拉");
			List downvalues=(List) doropdownconfig.get(cellAttr.getRegion());
			downvalues=handleDownValue(downvalues,sheet,cellAttr,cell);
			cellAttr.setDatalist(downvalues);;
		}
	}

	private static List handleDownValue(List downvalues, XSSFSheet sheet, CellAttr cellAttr,XSSFCell xcell) {
		List results=new ArrayList();
		for (int i = 0; i < downvalues.size(); i++) {
			String downvalue=(String) downvalues.get(i);
			String strres="";
			if(downvalue.contains("=")){
				cellAttr.setStyle("公式下拉");
				try{
				cellAttr.setDisplayvalue(xcell.getCellFormula());
				}catch(Exception e){
				cellAttr.setDisplayvalue(cellAttr.getValue());
				}
				String ndownvalue=downvalue.replace("=", "").replaceAll("\\$", "");
				if(!ndownvalue.contains("*")){
					int[] cellinfo=translateRegion(ndownvalue);
					XSSFCell cell=sheet.getRow(cellinfo[1]).getCell(cellinfo[0]);
					CellAttr attr=getCellValue(cell,new CellAttr());
				    String value=(String) attr.getValue();
				    try{
				    	int result=Integer.valueOf(value);
					    strres=downvalue+"###"+result;
				    }catch(Exception e){
				    	strres=downvalue+"###"+value;
				    }
				    
				}else{
					String region=ndownvalue.split("\\*")[0];
					String number=ndownvalue.split("\\*")[1];
					int[] cellinfo=translateRegion(region);
					XSSFCell cell=sheet.getRow(cellinfo[1]).getCell(cellinfo[0]);
					CellAttr attr=getCellValue(cell,new CellAttr());
				    String value=(String) attr.getValue();
				    try{
				    	int result=Integer.valueOf(value)*Integer.valueOf(number);
					    strres=downvalue+"###"+result;
				    }catch(Exception e){
				    	System.out.println("========"+value);
				    	strres=downvalue+"###"+value;
				    }
				    
				}
			
			}else{
				strres=downvalue;
			}
			results.add(strres);
		}
		
		return results;
	}

	/**
	* 用于将excel表格中列索引转成列号字母，从A对应1开始
	* 
	* @param index
	*            列索引
	* @return 列号
	*/
	public static String indexToColumn(int index) {
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

	public static List<MaterialAttr> getEngTypeAttrs(
			List<Map<String, String>> typematerials, WTPart part, MaterialDB db) {
		List<MaterialAttr> attrs=new ArrayList<MaterialAttr>();
		for (int i = 0; i < typematerials.size(); i++) {
			Map<String, String> map=typematerials.get(i);
			MaterialAttr attr=new MaterialAttr();
			String attrname=map.get(ConstantBattery.config_material_name);
			String region=map.get(ConstantBattery.config_material_region);
			region=getEngRegion(region);
			String style=map.get(ConstantBattery.config_material_style);
			if(attrname.equals("name")){
				attr.setName(attrname);
				attr.setRegion(region);
				attr.setStyle(style);
				attr.setValue(part.getName());
			}else if(attrname.endsWith("isphantom")){
				attr.setName(attrname);
				attr.setRegion(region);
				attr.setStyle(style);
				attr.setValue(db.getIsPhantom());
			}else if(attrname.equals("CATL_PN")){
				attr.setName(attrname);
				attr.setRegion(region);
				attr.setStyle(style);
				attr.setValue(part.getNumber());
			}else{
				attr.setName(attrname);
				attr.setRegion(region);
				attr.setStyle(style);
				Object value="";
				if(style.equals(ConstantBattery.config_type_double)){
					value=IBAUtil.getDoubleIBAValue(part, attrname);
				}
				if(style.equals(ConstantBattery.config_type_string)){
					value=IBAUtil.getStringIBAValue(part, attrname);
				}
				attr.setValue(value);
			}
			attrs.add(attr);
		}
		return attrs;
	}
	
	
	public static List<CellAttr> getAsmCellAttrs(List<Map<String, String>> typeasms, WTPart part) {
		List<CellAttr> attrs=new ArrayList<CellAttr>();
		for (int i = 0; i < typeasms.size(); i++) {
			Map<String, String> map=typeasms.get(i);
			CellAttr attr=new CellAttr();
			String type=map.get(ConstantBattery.config_asm_type);
			String attrname=map.get(ConstantBattery.config_asm_attr);
			String region=map.get(ConstantBattery.config_asm_region);
			String style=map.get(ConstantBattery.config_asm_style);
			attr.setRegion(region);
			Object value="";
			if(type.equals("name")){
				value=part.getName();
			}else if(type.equals("author")){
				value=IBAUtil.getStringIBAValue(part, attrname);
			}else if(style.equals(ConstantBattery.config_type_double)){
				value=IBAUtil.getDoubleIBAValue(part, attrname);
			}else if(style.equals(ConstantBattery.config_type_string)){
				value=IBAUtil.getStringIBAValue(part, attrname);
			}
			attr.setValue(value);
			attrs.add(attr);
		}
		return attrs;
	}

	public  static void handleMathCache() throws IOException {
		System.out.println("当前计算表缓存个数"+GlobalData.mathexcel_cache.size());
		if(GlobalData.mathexcel_cache.size()>=ConstantBattery.config_cache_limit_count){
			Map<String, XSSFWorkbook> maps=GlobalData.mathexcel_cache;
			Set keys=maps.keySet();
			Iterator itekey=keys.iterator();
			try{
			while (itekey.hasNext()) {
				String key = (String) itekey.next();
				File file=new File(key);
				if(file.exists()){//存在才写入
					CacheWriteToLocal(key);
				}
			}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				GlobalData.mathexcel_cache=new HashMap<String, XSSFWorkbook>();
			}
			
		}
	}
	
	public synchronized static void CacheWriteToLocal(String filepath) throws Exception {
		CommonHelper.waitFileOperation(filepath);
		GlobalData.inOperationFile.add(filepath);
		try{
			XSSFWorkbook workbook = null;
			if(GlobalData.mathexcel_cache.containsKey(filepath)){
				FileOutputStream outputStream=new FileOutputStream(filepath);
				workbook=GlobalData.mathexcel_cache.get(filepath);
				workbook.write(outputStream);
				outputStream.flush();
				outputStream.close();
				workbook.close();
				if(!filepath.contains(ConstantBattery.config_path_material_xlsx)){
				EncryptHelper.encryptExcel(filepath);
				}
				GlobalData.mathexcel_cache.remove(filepath);
			}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			GlobalData.inOperationFile.remove(filepath);
		}
	
	}
	
    public static boolean isSpeicalStr(String str){
    	String[] strs=ConstantBattery.config_special_str;
    	for (int i = 0; i < strs.length; i++) {
    		String current_str=strs[i];
    		if(current_str.equals(str)){
    			return true;
    		}
		}
    	return false;
    }
    
    public static String getFileName(String docoid, String name, String level,
			String remark) throws WTRuntimeException, WTException, IOException, PropertyVetoException {
    	String basicname="";
    	if(StringUtils.isEmpty(remark)){
    		basicname=name +"_XXX" + "_" + level;
		}else{
			basicname=name +"_XXX" + "_" + level + "_" + remark;
		}
    	if(!StringUtils.isEmpty(docoid)){
    		WTDocument doc=(WTDocument) WCUtil.getWTObject(docoid);
    		basicname=basicname+ "###"+doc.getNumber();
		}
    	return basicname+ ".xlsx";
	}
    
    public static String getFilePath(String docoid, String name, String level,String remark,String path) throws Exception{
    	String nowFileName=null;
    	if(remark.isEmpty()){
    		nowFileName = name + "_XXX" + "_" + level;
		}else{
			nowFileName = name + "_XXX" + "_" + level + "_" + remark;
		}
    	if(!StringUtils.isEmpty(docoid)){
    		WTDocument doc=(WTDocument) WCUtil.getWTObject(docoid);
    		nowFileName=nowFileName+ "###"+doc.getNumber();
		}
    	if(path.endsWith("/")){
    		return path+nowFileName+".xlsx";
    	}else{
    		return path+"/"+nowFileName+".xlsx";
    	}
    	
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
	 * 获取excel公式的结果
	 * @param cell
	 * @return
	 */
	public static String getFormulaValue(XSSFCell cell,XSSFFormulaEvaluator evaluator){
		String value="";
		boolean flag=false;
		try{
		evaluator.evaluateFormulaCell(cell);
		}catch(Exception e){
			evaluator.clearAllCachedResultValues();
			if(cell.getRawValue()!=null&&!cell.getRawValue().equals("#N/A")){
				 flag=true;
			}
			System.out.println(e.getLocalizedMessage());
		}
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
	
	public static List<String> getRangeRegions(String regions) throws WTException{
		List<String> allregions=new ArrayList<String>();
		if(!regions.contains(":")){
			allregions.add(regions);
		}else{String region1=regions.split(":")[0];
		    int[] rint1=CommonHelper.translateRegion(region1);
			String region2=regions.split(":")[1];
			int[] rint2=CommonHelper.translateRegion(region2);
			if(rint1[0]==rint2[0]){//英文部分相同
				String eng=CommonHelper.getEngRegion(region1);
				for (int i = 0; i <=rint2[1]-rint1[1]; i++) {
					String res=(eng+((rint1[1]+1)+i));
					allregions.add(res);
				}
			}else if(rint1[1]==rint2[1]){
				System.out.println(rint2[0]);
				System.out.println(rint1[0]);
				for (int i = 0; i <=rint2[0]-rint1[0]; i++) {
					String res=CommonHelper.indexToColumn((rint1[0]+1)+i)+(rint1[1]+1);
					allregions.add(res);
				}
			}else{
				throw new WTException("配置下拉配置错误");
			}
		}
		return allregions;
	}
	
	public static String getColorRangeRegions(String regions) throws WTException{
		StringBuffer allregions=new StringBuffer();
		if(!regions.contains(":")){
			allregions.append(regions).append(",");;
		}else{String region1=regions.split(":")[0];
		    int[] rint1=CommonHelper.translateRegion(region1);
			String region2=regions.split(":")[1];
			int[] rint2=CommonHelper.translateRegion(region2);
			if(rint1[0]==rint2[0]){//英文部分相同
				String eng=CommonHelper.getEngRegion(region1);
				for (int i = 0; i <=rint2[1]-rint1[1]; i++) {
					String res=(eng+((rint1[1]+1)+i));
					allregions.append(res).append(",");
				}
			}else if(rint1[1]==rint2[1]){
				System.out.println(rint2[0]);
				System.out.println(rint1[0]);
				for (int i = 0; i <=rint2[0]-rint1[0]; i++) {
					String res=CommonHelper.indexToColumn((rint1[0]+1)+i)+(rint1[1]+1);
					allregions.append(res).append(",");
				}
			}else{
				throw new WTException("特殊颜色配置错误");
			}
		}
		return allregions.toString();
	}
	public static Map dropDownList(XSSFWorkbook workbook,String sheetname) throws WTException, IOException{
		CacheHelper.loadExcelConfig();
		Map downmap=new HashMap();
		List<Map<String, String>> downinfos=GlobalData.dropDownList_info;
		for (int i = 0; i < downinfos.size(); i++) {
			Map<String, String> rowmap=downinfos.get(i);
			String name=rowmap.get(ConstantBattery.config_downDropdown_name);
			if(name!=null&&name.equals(sheetname)){
				String region=rowmap.get(ConstantBattery.config_downDropdown_region);
				String formula=rowmap.get(ConstantBattery.config_downDropdown_formula);
				List resdatas=null;
				if(formula.contains(",")){
					String [] datas=formula.split(",");
					resdatas=Arrays.asList(datas);
				}else if(formula.contains("!")){
					String record_sheetname=formula.split("!")[0];
					resdatas=getSheetRegionValues(workbook,record_sheetname,formula.split("!")[1]);
				}else{
					throw new WTException("下拉配置文件错误");
				}
				List regions=getRangeRegions(region);
				for (int j = 0; j < regions.size(); j++) {
					String cregion=(String) regions.get(j);
					downmap.put(cregion,resdatas);
				}
			}
		}
		return downmap;
		
	}
	
	private static List getSheetRegionValues(XSSFWorkbook workbook,String record_sheetname, String region) throws WTException{
		XSSFSheet sheet=workbook.getSheet(record_sheetname);
		List values=new ArrayList();
		List resregions=getRangeRegions(region);
		for (int i = 0; i < resregions.size(); i++) {
			String aregion=(String) resregions.get(i);
			int[] cellinfo=translateRegion(aregion);
			XSSFCell cell=sheet.getRow(cellinfo[1]).getCell(cellinfo[0]);
			if(cell==null){
				throw new WTException(aregion+"单元格不不存在");
			}
			CellAttr attr=getCellValue(cell,new CellAttr());
		    String value=(String) attr.getValue();
		    if(!values.contains(value)){
		    	values.add(attr.getValue());
		    }
			
		}
		return values;
	}

	public static CellAttr getCellValue(XSSFCell cell, CellAttr cellAttr) {
		XSSFCellStyle cellStyle=cell.getCellStyle();
		String colorCode="#FFF";
		if(cellStyle.getFillForegroundColorColor()!=null){
			colorCode="#"+cellStyle.getFillForegroundColorColor().getARGBHex().substring(2);
		}
		cellAttr.setColor(colorCode);
		XSSFFormulaEvaluator evaluator=new XSSFFormulaEvaluator(cell.getSheet().getWorkbook());
		evaluator.clearAllCachedResultValues();
		switch(cell.getCellType()){
			case Cell.CELL_TYPE_BLANK:
				cellAttr.setValue("");
				break;
			case Cell.CELL_TYPE_STRING: 
				cellAttr.setStyle("字符串");
				cellAttr.setValue(cell.getStringCellValue());
				break;
			case Cell.CELL_TYPE_NUMERIC:
				String cellstring=getNumformatValue(cell);
				cellAttr.setValue(cellstring);
				break;
			case Cell.CELL_TYPE_FORMULA:
				String cellstring1=getFormulaValue(cell,evaluator);
				cellstring1=transformvaluetodisplay(cell,cellstring1);
				cellAttr.setValue(cellstring1);
				cellAttr.setStyle("显示");
				break;
			case Cell.CELL_TYPE_ERROR:
				cellAttr.setValue(cell.getErrorCellString());
				break;
			default:break;
		}
		return cellAttr;
	}

	public static String getNumformatValue(XSSFCell cell) {
		HSSFDataFormatter dataformatter=new HSSFDataFormatter();
		String cellstring="";
		try{
		cellstring=dataformatter.formatCellValue(cell);
		}catch(IllegalStateException e){
			cellstring=cell.getRawValue();
		}
		if(cellstring.matches("^((-?\\d+.?\\d*)[Ee]{1}(-?\\d+))$")){
			BigDecimal bd = new BigDecimal(cellstring);  
			cellstring=bd.toPlainString();
		}
		return cellstring;
	}

	public static String transformvaluetodisplay(XSSFCell cell,String cellstring1) {
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
		return cellstring1;
	}

	private static CellAttr getFormulaCellValue(XSSFCell cell, CellAttr cellAttr) {//只获取带公式的数据
		XSSFCellStyle cellStyle=cell.getCellStyle();
		String colorCode="#FFF";
		if(cellStyle.getFillForegroundColorColor()!=null){
			colorCode="#"+cellStyle.getFillForegroundColorColor().getARGBHex().substring(2);
		}
		cellAttr.setColor(colorCode);
		XSSFFormulaEvaluator evaluator=new XSSFFormulaEvaluator(cell.getSheet().getWorkbook());
		evaluator.clearAllCachedResultValues();
		String cellstring1=getFormulaValue(cell,evaluator);
		if(cellstring1.matches("^((-?\\d+.?\\d*)[Ee]{1}(-?\\d+))$")){
			BigDecimal bd = new BigDecimal(cellstring1);  
			cellstring1=bd.toPlainString();
		}
		if(isNumeric(cellstring1)){
			String format=cell.getCellStyle().getDataFormatString();
			System.out.println("格式"+format);
			if(format.equals("General")){//General默认三位小数，如果需要变化更改计算模版
				format="0.000";
			}
			format=format.split("_")[0];
			format=getStrNumberic(format);
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
		return cellAttr;
	}
	public static String getStrNumberic(String str) {
		if(str==null){
			return "0.000";
		}
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
	public static XSSFWorkbook getWorkBook(String filepath) throws Exception{
		XSSFWorkbook workbook=null;
		if(GlobalData.mathexcel_cache.containsKey(filepath)){
			workbook=GlobalData.mathexcel_cache.get(filepath);
		}else{
			File file=new File(filepath);
			if(!file.exists()){
				throw new WTException("找不到文件"+filepath);
			}
			if(filepath.contains(ConstantBattery.config_path_material_xlsx)){
				FileInputStream fileInputStream=new FileInputStream(file);
				workbook=new XSSFWorkbook(fileInputStream);
			}else{
				workbook=EncryptHelper.getEncryptWorkbook(filepath);
			}
		}
		return workbook;
	}
	public static String getBatteryDirFile(String fileName) throws WTException{
		String filePath = wt_home + ConstantBattery.base_battery_path;
		WTPrincipal principal = SessionHelper.getPrincipal();
		WTUser user = (WTUser) principal;
		filePath = filePath+user.getName();
		return filePath+"/"+fileName;
	}
	public static String getBatteryDir() throws WTException{
		String filePath = wt_home + ConstantBattery.base_battery_path;
		WTPrincipal principal = SessionHelper.getPrincipal();
		WTUser user = (WTUser) principal;
		filePath = filePath+user.getName();
		return filePath+"/";
	}

	public static void waitFileOperation(String filePath) throws InterruptedException {
		int i=0;
		while(i<30){
			if(!GlobalData.inOperationFile.contains(filePath)){
			  break;
			}
			i++;
			System.out.println(filePath+"正在操作-等待"+i);
			Thread.sleep(100);
		}		
	}

	public static void bakFile(File fromfile) throws IOException {
		String bakpath=wt_home+ConstantBattery.base_battery_bak_path;
		File file=new File(bakpath);
		if(!file.exists()){
			file.mkdir();
		}
		long currenttime=System.currentTimeMillis();
		if(currenttime-GlobalData.batteryMaterialLastBakTime>8*60*60*1000){
			File bakfile=new File(bakpath+System.currentTimeMillis()+"bak_material.xlsx");
	        FileUtil.copyFile(fromfile, bakfile);
	        GlobalData.batteryMaterialLastBakTime=currenttime;
	        File[] listfiles=file.listFiles();
			List<String> filenames=new ArrayList<String>();
	        for (int i = 0; i < listfiles.length; i++) {
	        	filenames.add(listfiles[i].getName());
			}
	        Collections.sort(filenames);
			if(listfiles.length>3){//删除最旧的文档
		       FileUtil.removeFile(bakpath+filenames.get(0));
			}
		}
		
		
	}

	public static void copyBakToCover(String filePath) throws WTException, IOException {
		String bakpath=wt_home+ConstantBattery.base_battery_bak_path;
		File file=new File(bakpath);
		File[] listfiles=file.listFiles();
		if(listfiles.length==0){
			throw new WTException("没有虚拟件备份文件");
		}else{
			List<String> filenames=new ArrayList<String>();
	        for (int i = 0; i < listfiles.length; i++) {
	        	filenames.add(listfiles[i].getName());
			}
		    Collections.sort(filenames);
		    String lastfilepath=bakpath+filenames.get(filenames.size()-1);
		    FileUtil.copyFile(new File(lastfilepath), new File(filePath));
		    FileUtil.removeFile(lastfilepath);
		}
		
	}
	
}
