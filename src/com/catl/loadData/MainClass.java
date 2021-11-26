package com.catl.loadData;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.catl.loadData.util.ExcelReader;
import com.catl.loadData.util.ExcelWriter;

/**
 * @author
 */
public class MainClass {
	/**
	 * 当前系统所有在用单位
	 */
	private static final String UNIT_ALL="ea|g|ml|dm2|set|m|";
	
	/**
	 * 所有安装的分类
	 */
	private static List<String> allInstanceClassfy = new ArrayList<String>();
	
	/**
	 * 存放实例化为是的分类
	 * Key 分类内部名称
	 * Value 分类显示名称
	 */
	private static Map<String,String> displayNameMap = new HashMap<String,String>();
	/**
	 * 存放实例化为是的分类
	 * Key 分类内部名称
	 * Value 分类描述 目前填写的是该分类限制的单位
	 */
	private static Map<String,String> descriptionMap = new HashMap<String,String>();
	
	/**
	 * key 容器
	 * value 项目名称以,隔开
	 */
	private static Map<String,String> folderMap = new HashMap<String,String>();
	
	private static String rootPath="/data/";
		
	private static List<String[]> ObjectValidInfo=new ArrayList<String[]>();
	
	private static List<String> sharePart=new ArrayList<String>();   //已共享可导入的Part
	
	private static Map<String,String> validRepeatPart=new HashMap<String, String>();
	
	private static Map<String,String> validRepeatDoc=new HashMap<String, String>();
	
	private static Map<String,String[]> classifyAttrConstraintData=new HashMap<String, String[]>();
		
	private static Map<String,String> numMap=new HashMap<String, String>();
	
	private static Map<String,String> numNew=new HashMap<String, String>();
	
	private static Map<String,String[]> partToFolder=new HashMap<String, String[]>();
		
	private static List<String[]> specificationMap=new ArrayList<String[]>();
	
	private static List<String[]> evMap=new ArrayList<String[]>();	//包含属性产品能量、标称电压的PN
		
	public static void main(String args[]) throws Exception {
		
		Map<String, File> bomSAP=new HashMap<String, File>();
		Map<String, File> bomOld=new HashMap<String, File>();
		
		List<String> bomError = new ArrayList<String>();
		List<String> docError = new ArrayList<String>();
		
		Map<String,List<File>> cadFiles=new HashMap<String, List<File>>(); //cad文件
		
		List<File> bomFiles = new ArrayList<File>();//bom文件
		List<File> documentFiles = new ArrayList<File>();//文档文件
		
		Set<String> noExistPN = new HashSet<String>();//bom文件中使用了 而part文件中不存在的PN
		
		loadAllRelationFile(bomSAP, bomOld, bomError, docError,cadFiles,bomFiles,documentFiles);
			
		//loadAllData(bomFiles,documentFiles,bomError,docError,noExistPN);
		
		loadPart();
		
		//loadDoc(documentFiles,docError);
		
		//loadAutoCADDoc();
	}
	
	/**
	 * 导入存储库、产品库、文件夹、物料、文档、BOM
	 * @param bomFiles
	 * @param documentFiles
	 * @param bomError
	 * @param docError
	 * @param noExistPN
	 */
	public  static void loadAllData(List<File> bomFiles,List<File> documentFiles,List<String> bomError,List<String> docError,Set<String> noExistPN){
		try{			
			List<String> folderStatement = exportFolder(rootPath+"destotal\\容器文件夹.xlsx");
			folderStatement.add(0,"--folder");
			
			List<String> libraryStatement=libraryStatement();
			List<String> productStatement=productStatement();
			
			readClassificationXML(rootPath+"destotal\\Classification_3.xml");
			
			List<String> partStatement = part();
			partStatement.add(0,"--Part");
			
			List<String> bomStatement = bom(bomFiles, noExistPN,bomError);
			bomStatement.add(0,"--Bom");
						
			List<String> docStatement = document(documentFiles,docError,sharePart);
			docStatement.add(0,"--Doc");
			
			List<String> cadStatement=loadAutoCAD(partToFolder);
			cadStatement.add(0,"---AutoCad---");
			
			for(String pn:noExistPN){
				ObjectValidInfo.add(new String[]{"noExistPN","error","BOM中Part在已完成_共享版不存在","","",pn});
			}
			
			boolean flag = writeTxt(rootPath+"destotal\\command.txt",libraryStatement,productStatement,folderStatement,partStatement,docStatement,cadStatement,bomStatement);
			System.out.println("write command flag="+flag);
			
			outputValid();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 导入存储库、产品库
	 */
	public static void loadLibrary(){
		try{
			List<String> libraryStatement=libraryStatement();
			List<String> productStatement=productStatement();
			
			boolean flag = writeTxt(rootPath+"destotal\\Command.txt",libraryStatement,productStatement);
			System.out.println("write Command flag="+flag);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 导入文件夹
	 */
	public static void loadFolder(){
		try{
			List<String> folderStatement = exportFolder(rootPath+"destotal\\容器文件夹.xlsx");
			folderStatement.add(0,"--folder");
			
			boolean flag = writeTxt(rootPath+"destotal\\Command.txt",folderStatement);
			System.out.println("write Command flag="+flag);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}	
	
	/**
	 * 导入BOM
	 * @param bomFiles
	 * @param noExistPN
	 * @param bomError
	 */
	public static void loadBOM(List<File> bomFiles,Set<String> noExistPN,List<String> bomError){
		try{
			List<String> folderStatement = exportFolder(rootPath+"destotal\\容器文件夹.xlsx");
			folderStatement.add(0,"--folder");
			
			readClassificationXML(rootPath+"destotal\\Classification_3.xml");
			List<String> partStatement = part();
			partStatement.add(0,"--Part");
			
			List<String> bomStatement = bom(bomFiles, noExistPN,bomError);
			bomStatement.add(0,"--Bom");
			
			for(String pn:noExistPN){
				ObjectValidInfo.add(new String[]{"noExistPN","error","BOM中Part在已完成_共享版不存在","","",pn});
			}
			
			boolean flag = writeTxt(rootPath+"destotal\\Command.txt",bomStatement);
			System.out.println("write Command flag="+flag);
			
			outputValid();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 导入AutoCAD
	 */
	public static void loadAutoCADDoc(){
		try{
			List<String> folderStatement = exportFolder(rootPath+"destotal\\容器文件夹.xlsx");
			folderStatement.add(0,"--folder");
			
			readClassificationXML(rootPath+"destotal\\Classification_3.xml");
			List<String> partStatement = part();
			partStatement.add(0,"--Part");
			
			List<String> cadStatement=loadAutoCAD(partToFolder);
			cadStatement.add(0,"---AutoCad---");
			
			boolean flag = writeTxt(rootPath+"destotal\\Command.txt",cadStatement);
			System.out.println("write Command flag="+flag);
			
			outputValid();
		}catch(Exception e){
			e.printStackTrace();
		}
	}	
	
	/**
	 * 导入文档
	 * @param documentFiles
	 * @param docError
	 */
	public static void loadDoc(List<File> documentFiles,List<String> docError){
		try{
			List<String> folderStatement = exportFolder(rootPath+"destotal\\容器文件夹.xlsx");
			folderStatement.add(0,"--folder");
			List<String> docStatement = document(documentFiles,docError,sharePart);
			docStatement.add(0,"--Doc");
			
			boolean flag = writeTxt(rootPath+"destotal\\Command.txt",docStatement);
			System.out.println("write Command flag="+flag);
			
			outputValid();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 导入Part
	 */
	public static void loadPart(){
		try{
			List<String> folderStatement = exportFolder(rootPath+"destotal/container.xlsx");
			folderStatement.add(0,"--folder");			
			readClassificationXML(rootPath+"destotal/Classification_3.xml");
			List<String> partStatement = part();
			partStatement.add(0,"--Part");
			boolean flag = writeTxt(rootPath+"destotal/Command.txt",partStatement);
			System.out.println("write Command flag="+flag);
			
			outputValid();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static void loadAllRelationFile(Map<String, File> bomSAP,
			Map<String, File> bomOld, List<String> bomError,
			List<String> docError,Map<String,List<File>> cadFiles,List<File> bomFiles,List<File> documentFiles) {
		File root = new File(rootPath+"destotal\\");
				
		if(root.isDirectory()){
			File[] files = root.listFiles();
			for(File file : files){
				String fileName = file.getName();
				if(file.isDirectory()){
					if(fileName.equals("01_Gerber-Files")||fileName.equals("02_Assembly-Files")||fileName.equals("公共文档库")){
						System.out.println("Gerber/Assembly:"+fileName);
						
						File[] subFiles = file.listFiles();
						for(File subFile : subFiles){
							String subFileName = subFile.getName();
							if(subFile.isDirectory() && subFileName.equals("文档")){
								File[] childs = subFile.listFiles();
								boolean isFind=false;
								for(File child : childs){
									if(child.getName().toLowerCase().equals(fileName.toLowerCase()+".xlsx")){								
										documentFiles.add(child);
										isFind=true;
									}
								}
								if(!isFind){
									ObjectValidInfo.add(new String[]{"Doc","error","文件不存在","","","Document: "+subFile.getAbsolutePath()+"  目录中，没找到"+fileName+".xlsx"});
									docError.add("Document: "+subFile.getAbsolutePath()+"  目录中，没找到"+fileName+".xlsx");
								}
							}
						}					
						
					}else{
					
						File[] subFiles = file.listFiles();
						for(File subFile : subFiles){
							String subFileName = subFile.getName();
							if(subFile.isDirectory() && subFileName.equals("BOM")){
								File[] childs = subFile.listFiles();
								boolean isFind=false;
								boolean isFindOld=false;
								for(File child : childs){
									
									if(child.getName().toLowerCase().equals(fileName.toLowerCase()+".xlsx")){
										System.out.println(".."+child.getName());
										bomFiles.add(child);
										bomSAP.put(fileName, child);
										isFind=true;
									}
									
									if(child.getName().toLowerCase().equals(fileName.toLowerCase()+"_old.xlsx")){
										bomOld.put(fileName, child);
										isFindOld=true;
									}
									
								}
								if(!isFind){
									ObjectValidInfo.add(new String[]{"BOM","error","文件不存在","","","Bom: "+subFile.getAbsolutePath()+"  目录中，没找到"+fileName+".xlsx"});
									bomError.add("Bom: "+subFile.getAbsolutePath()+"  目录中，没找到"+fileName+".xlsx");
								}
								
								if(!isFindOld){
									//ObjectValidInfo.add(new String[]{"BOM","error","文件不存在","","","Bom: "+subFile.getAbsolutePath()+"  目录中，没找到"+fileName+"_old.xlsx"});
									bomError.add("Bom: "+subFile.getAbsolutePath()+"  目录中，没找到"+fileName+"_old.xlsx");
								}
								
							}else if(subFile.isDirectory() && subFileName.equals("文档")){
								File[] childs = subFile.listFiles();
								boolean isFind=false;
								for(File child : childs){
									if(child.getName().toLowerCase().equals(fileName.toLowerCase()+".xlsx")){
										
										documentFiles.add(child);
										isFind=true;
									}
								}
								if(!isFind){
									ObjectValidInfo.add(new String[]{"Doc","error","文件不存在","","","Document: "+subFile.getAbsolutePath()+"  目录中，没找到"+fileName+".xlsx"});
									docError.add("Document: "+subFile.getAbsolutePath()+"  目录中，没找到"+fileName+".xlsx");
								}
							}else if(subFile.isDirectory() && subFileName.equals("图档")){
								File[] childs = subFile.listFiles();
								List<File> childcadfiles = new ArrayList<File>();//cad文件
								for(File child : childs){
									if(!child.isDirectory()){
										System.out.println("child.getName():"+child.getName());
										String suffix=child.getName().substring(child.getName().lastIndexOf("."));
										if(suffix.equals(".txt")){
											childcadfiles.add(child);
										}
									}								
								}
								cadFiles.put(fileName, childcadfiles);
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * 导出包含属性产品能量或标称电压的物料
	 */
	private static void exportEVPN(){
		ExcelWriter writer = new ExcelWriter();
        try {
			boolean flag = writer.exportExcelList(rootPath+"destotal\\包含属性产品能量或标称电压的物料.xlsx","包含属性产品能量或标称电压的物料", new String[]{"物料编码"}, evMap);
			System.out.println("导出 物料规格.xlsx flag="+flag);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 导出物料规格
	 */
	private static void exportSpecification(){
		ExcelWriter writer = new ExcelWriter();
        try {
			boolean flag = writer.exportExcelList(rootPath+"destotal\\物料规格.xlsx","物料规格", new String[]{"物料编码","规格"}, specificationMap);
			System.out.println("导出 物料规格.xlsx flag="+flag);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static List<String> exportFolder(String xml) throws Exception { 
		System.out.println("----------------------start exportFolder--------------------");
		List<String> impStatement = new ArrayList<String>();
		File file = new File(xml);
		ExcelReader reader = new ExcelReader(file);
		reader.open();
		reader.setSheetNum(0);
		int count = reader.getRowCount();
		for(int i=1; i<=count; i++){
			String rows[] = reader.readExcelLine(i);
			String key = rows[0];
			String value = rows[1];
			if(!StrUtils.isEmpty(key) && !StrUtils.isEmpty(value)){				
				if(folderMap.containsKey(key)){
					folderMap.put(key, folderMap.get(key)+","+value);
				}else{
					folderMap.put(key, value);
				}
			}
		}
		for(String key : folderMap.keySet()){
			String[] projects = folderMap.get(key).split(",");
			
			StringBuffer buffer = new StringBuffer("<?xml version='1.0' ?><!DOCTYPE NmLoader SYSTEM 'standardX24.dtd'><NmLoader>");
			for(String project : projects){
				buffer.append("<csvSubFolder handler='wt.folder.LoadFolder.createSubFolder' ><csvuser></csvuser><csvfolderPath>/Default/").append(project).append("</csvfolderPath><csvadminDomain>/Default</csvadminDomain></csvSubFolder>")
				.append("<csvSubFolder handler='wt.folder.LoadFolder.createSubFolder' ><csvuser></csvuser><csvfolderPath>/Default/").append(project).append("/零部件</csvfolderPath><csvadminDomain>/Default/CATLDefault/零部件</csvadminDomain></csvSubFolder>")
				.append("<csvSubFolder handler='wt.folder.LoadFolder.createSubFolder' ><csvuser></csvuser><csvfolderPath>/Default/").append(project).append("/产品资料</csvfolderPath><csvadminDomain>/Default</csvadminDomain></csvSubFolder>")
				.append("<csvSubFolder handler='wt.folder.LoadFolder.createSubFolder' ><csvuser></csvuser><csvfolderPath>/Default/").append(project).append("/产品资料/产品技术文件</csvfolderPath><csvadminDomain>/Default/CATLDefault/产品技术文件</csvadminDomain></csvSubFolder>")
				.append("<csvSubFolder handler='wt.folder.LoadFolder.createSubFolder' ><csvuser></csvuser><csvfolderPath>/Default/").append(project).append("/产品资料/研发过程文档</csvfolderPath><csvadminDomain>/Default/CATLDefault/研发过程文档</csvadminDomain></csvSubFolder>")
				.append("<csvSubFolder handler='wt.folder.LoadFolder.createSubFolder' ><csvuser></csvuser><csvfolderPath>/Default/").append(project).append("/设计图档</csvfolderPath><csvadminDomain>/Default</csvadminDomain></csvSubFolder>")
				.append("<csvSubFolder handler='wt.folder.LoadFolder.createSubFolder' ><csvuser></csvuser><csvfolderPath>/Default/").append(project).append("/设计图档/CATIA图档</csvfolderPath><csvadminDomain>/Default/CATLDefault/Catia图档</csvadminDomain></csvSubFolder>")
				.append("<csvSubFolder handler='wt.folder.LoadFolder.createSubFolder' ><csvuser></csvuser><csvfolderPath>/Default/").append(project).append("/设计图档/Gerber文件</csvfolderPath><csvadminDomain>/Default/CATLDefault/Gerber文件</csvadminDomain></csvSubFolder>")
				.append("<csvSubFolder handler='wt.folder.LoadFolder.createSubFolder' ><csvuser></csvuser><csvfolderPath>/Default/").append(project).append("/设计图档/PCBA装配图</csvfolderPath><csvadminDomain>/Default/CATLDefault/PCBA装配图</csvadminDomain></csvSubFolder>")
				.append("<csvSubFolder handler='wt.folder.LoadFolder.createSubFolder' ><csvuser></csvuser><csvfolderPath>/Default/").append(project).append("/设计图档/线束AutoCAD图纸</csvfolderPath><csvadminDomain>/Default/CATLDefault/线束AutoCAD图纸</csvadminDomain></csvSubFolder>");
			}
			buffer.append("</NmLoader>");
			
			Java2XML.writerByString(buffer.toString(), file.getAbsolutePath().substring(0,file.getAbsolutePath().lastIndexOf("\\")+1)+"folder_"+"20171114"+".xml");
			
	        impStatement.add("windchill wt.load.LoadFromFile -d d:\\ptc\\Windchill\\loadFiles\\com\\catl\\dms\\导入文件夹_"+key+".xml -u dms -p dms -CONT_PATH \\\"/wt.inf.container.OrgContainer=CATL/wt.pdmlink.PDMLinkProduct="+key+"\\\"");
		}
		System.out.println("----------------------end exportFolder--------------------");
		return impStatement;
	}

	
	/**
	 * 读取分类树导出的Classification_3.xml文件
	 * @param xml	
	 * @throws DocumentException
	 * @throws FileNotFoundException
	 */
	private static void readClassificationXML(String xml) throws DocumentException,FileNotFoundException {
		System.out.println("----------------------start readUnitXML--------------------");
		SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(new File(xml));
        Element root = document.getRootElement();
        List<Element> childList = root.elements();
        
        String innerName=null;
        String parentName=null;
        String displyName=null;
        String description=null;
        String instantiable=null;
        Map<String,String> allDescriptionMap= new HashMap<String, String>();//为了拿到 子类描述集成了父类没有实例化
        List<String[]> exportDate = new ArrayList<String[]>();//导出到xls的数据
        for(Element child : childList){
        	if(child.getName().equals("csvBeginTypeDefView")){      		
        		
        		Element csvname = child.element("csvname");        		
        		Element csvtypeParent = child.element("csvtypeParent");        		
        		innerName=csvname.getText();
        		parentName=csvtypeParent.getText();
                displyName=null;
                description=null;
                instantiable=null;
                
        	}
        	if(child.getName().equals("csvPropertyValue")){
        		Element csvname = child.element("csvname");
        		
        		if(csvname.getText().equals("description")){
        			Element csvvalue = child.element("csvvalue");
        			description=csvvalue.getText();
        		}else if(csvname.getText().equals("displayName")){
        			if(child.attribute("handler").getText().equals("com.ptc.core.lwc.server.TypeDefinitionLoader.processTypePropertyValue")){
        				Element csvvalue = child.element("csvvalue");
            			displyName=csvvalue.getText();
        			}
        		}else if(csvname.getText().equals("instantiable")){
        			Element csvvalue = child.element("csvvalue");
        			instantiable=csvvalue.getText();
        		}
        	}
        	
        	if(child.getName().equals("csvEndTypeDefView")){
        			
    			if(instantiable == null && displayNameMap.containsKey(parentName)){//子类如果没有值  则取父类。只要有存放到MAP中的父类的instantiable肯定是true;
    				instantiable="true";
    			}else if(instantiable == null){
    				instantiable="false";
    			}
    			if(displyName == null && displayNameMap.containsKey(parentName)){
    				displyName=displayNameMap.get(parentName);
    			}
    			if(description == null && allDescriptionMap.containsKey(parentName)){
    				description=allDescriptionMap.get(parentName);
    			}else if(description != null){
    				allDescriptionMap.put(innerName, description+"|");
    			}
    			
    			
    			//System.out.println("innerName="+innerName+": "+instantiable);
    			if(instantiable.equals("true")){//只有 实例化为是的 分类才放入
    				allInstanceClassfy.add(innerName);
    				//System.out.println("innerName="+innerName+",displyName="+displyName+",description="+description);
    				int innerNameLength=innerName.length();
    				if(innerNameLength<6){
    					for(int i=0; i < 6-innerNameLength; i++){
    						innerName=innerName+"0";
    					}
    				}
    				exportDate.add(new String[]{innerName,displyName.substring(2).trim(),description});
    				if(displyName != null){
    					displayNameMap.put(innerName, displyName);
    				}
    				if(description != null){
    					descriptionMap.put(innerName, description+"|");
    				}
            		
        		}
        		
        	}
        	
        }
        
        ExcelWriter writer = new ExcelWriter();
        try {
			boolean flag = writer.exportExcelList(rootPath+"destotal\\实例化的分类树信息.xlsx","实例化的分类树信息", new String[]{"内部名称","显示名称","描述"}, exportDate);
			System.out.println("导出 实例化的分类树信息.xlsx flag="+flag);
		} catch (Exception e) {
			e.printStackTrace();
		}
        System.out.println("----------------------end readUnitXML--------------------");
	}

	@SafeVarargs
	/**
	 * 写入文件
	 * @param fileName
	 * @param colloections
	 * @return
	 */
	private static boolean writeTxt(String fileName, Collection<String> ...colloections){
		boolean flag =false;
		try {
			FileWriter output = new FileWriter(fileName);
			BufferedWriter bf = new BufferedWriter(output);
			for(Collection<String> colloection : colloections){
				for (String str : colloection) {
					bf.write(str + "\r\n");
				}
			}
			bf.flush();
			flag=true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return flag;
	}
	/**
	 * 读取bom文件
	 * @param bomFiles
	 * @param set
	 * @return
	 */
	private static List<String> bom(List<File> bomFiles, Set<String> set,List<String> bomError) {
		//File file = new File(rootPath+"destotal\\13KWh\\BOM\\13KWh.xlsx");
		//File file = new File(rootPath+"destotal\\job1\\BOM\\job1.xlsx");
		System.out.println("================================BOM Change Start==============================");
		List<String> impStatement = new ArrayList<String>();
		
		List<String> error = new ArrayList<String>();
		error.add("-----------------------------------------------------------error--------------------------------------------------------------");
		error.addAll(bomError);
		List<String> warn = new ArrayList<String>();
		warn.add("-----------------------------------------------------------warn--------------------------------------------------------------");
		
		Map<String,String> childmap = new HashMap<String,String>();
		Map<String,String> substituteMap = new HashMap<String,String>();
		
		Map<String,String> assemblyMap = new HashMap<String,String>();

		for(File file : bomFiles){
			
			System.out.println("BOM_Path_20151228:"+file.getAbsolutePath());
			
			String filePath = file.getName();
			
			String projectName=filePath.substring(0,filePath.lastIndexOf("."));
			
			ExcelReader readExcel = new ExcelReader(file);
			int index=0;
			String[] rows=null;
			try {
				readExcel.open();
				//readExcel.setSheetNum(readExcel.getSheetIndex("产品BOM_IT")); //
				readExcel.setSheetNum(0);
				// 总行数
				int count = readExcel.getRowCount();
				StringBuffer xml = new StringBuffer("<?xml version='1.0' ?><!DOCTYPE NmLoader SYSTEM 'standardX24.dtd'><NmLoader>");
				StringBuffer xmlSub = new StringBuffer("<?xml version='1.0' ?><!DOCTYPE NmLoader SYSTEM 'standardX24.dtd'><NmLoader>");
				for (int i = 1; i <= count; i++) {
					index=i;
					rows = readExcel.readExcelLine(i);
					
					String parentNumber=rows[0];
					String childNumber=rows[3];
					String childUnit=rows[4];
					String qty=rows[5];
					String substitute=rows[7];
					
					if(rows == null){
						continue;
					}
					
					boolean nullValue=true;
					for(int y=0;y<rows.length;y++){
						if(!StrUtils.isEmpty(rows[y])){
							nullValue=false;
						}
					}
					
					if(nullValue){
						continue;
					}
					
					if(rows.length<5){
						ObjectValidInfo.add(new String[]{"BOM","error","数据少于5列(单位用量)",parentNumber,projectName+"(项目)",""});
						error.add(filePath+" 第"+(index+1)+"行，值为"+Arrays.toString(rows)+",少于5列(单位用量),没有导入xml");
						continue;
					}
					if(StrUtils.isEmpty(parentNumber) || StrUtils.isEmpty(childNumber) || StrUtils.isEmpty(qty) ||StrUtils.isEmpty(childUnit) ||
							parentNumber.trim().equals("待定") || childNumber.trim().equals("待定") || 
							parentNumber.trim().equals("#N/A")|| childNumber.trim().equals("#N/A") || qty.trim().equals("#N/A") || childUnit.trim().equals("#N/A")){
						ObjectValidInfo.add(new String[]{"BOM","error","值为空或待定",rows[0],projectName+"(项目)",""});
						error.add(filePath+" 第"+(index+1)+"行，值为"+Arrays.toString(rows)+",值为空或待定,没有导入xml");
						continue;
					}
					
					String unit = childUnit.trim().toLowerCase();
					if(unit.equals("pcs")){
						unit="ea";
					}
					
					if(!UNIT_ALL.contains(unit+"|")){
						ObjectValidInfo.add(new String[]{"BOM","error","单位不正确",parentNumber,projectName+"(项目)",""});
						error.add(filePath+" 第"+(index+1)+"行，值为"+Arrays.toString(rows)+",单位不正确,没有导入xml");
						continue;
					}
					
					if(rows.length >= 8 && !StrUtils.isEmpty(substitute)){
						if(substituteMap.containsKey(parentNumber+","+childNumber+","+substitute)){
							String value = substituteMap.get(parentNumber+","+childNumber+","+substitute);
							warn.add(value);
							
							warn.add(filePath+" 第"+(index+1)+"行，值"+parentNumber+","+childNumber+","+substitute+",替代件重复");
							//ObjectValidInfo.add(new String[]{"BOM","warn","替代件重复",parentNumber,projectName+"(项目)",filePath+" 第"+(index+1)+"行，值"+parentNumber+","+childNumber+","+substitute+" 与 "+value});
							continue;
						}else{
							substituteMap.put(parentNumber+","+childNumber+","+substitute, filePath+" 第"+(index+1)+"行，值"+parentNumber+","+childNumber+","+substitute+",替代件重复");
							
						}
					}else{
						if(childmap.containsKey(parentNumber+","+childNumber)){
							String value = childmap.get(parentNumber+","+childNumber);
							warn.add(value);
							
							//System.err.println("--1---2--3--value:"+value);
							//System.out.println("..dd.."+filePath+" 第"+(index+1)+"行，值"+rows[0]+","+rows[2]+",子件重复");
							
							warn.add(filePath+" 第"+(index+1)+"行，值"+parentNumber+","+childNumber+",子件重复");
							//ObjectValidInfo.add(new String[]{"BOM","warn","子件重复",parentNumber,projectName+"(项目)",filePath+" 第"+(index+1)+"行，值"+parentNumber+","+childNumber+" 与"+value});
							continue;
						}else{
							childmap.put(parentNumber+","+childNumber, filePath+" 第"+(index+1)+"行，值"+parentNumber+","+childNumber+",子件重复");
						}
					}
					
					if(!sharePart.contains(parentNumber)){
						set.add(parentNumber);
					}
					
					if(!sharePart.contains(childNumber)){
						set.add(childNumber);
					}
					
					if(!assemblyMap.containsKey(parentNumber+","+childNumber)){
						xml.append("<csvAssemblyAdd handler='wt.part.LoadPart.addPartToAssembly' >")
						.append("<csvassemblyPartNumber>").append(parentNumber).append("</csvassemblyPartNumber>")
						.append("<csvconstituentPartNumber>").append(childNumber).append("</csvconstituentPartNumber>")
						.append("<csvconstituentPartQty>").append(qty).append("</csvconstituentPartQty>")
						.append("<csvconstituentPartUnit>").append(unit).append("</csvconstituentPartUnit>")
						.append("<csvcomponentId></csvcomponentId>")
						.append("<csvinclusionOption></csvinclusionOption>")
						.append("<csvquantityOption></csvquantityOption>")
						.append("<csvreference></csvreference>")
						.append("<csvassemblyPartVersion></csvassemblyPartVersion>")
						.append("<csvassemblyPartIteration></csvassemblyPartIteration>")
						.append("<csvassemblyPartView></csvassemblyPartView>")
						.append("<csvassemblyPartVariation1></csvassemblyPartVariation1>")
						.append("<csvassemblyPartVariation2></csvassemblyPartVariation2>")
						.append("<csvorganizationName></csvorganizationName>")
						.append("<csvorganizationID></csvorganizationID>")
						.append("<csvlineNumber></csvlineNumber>")
						.append("</csvAssemblyAdd>");
						
						assemblyMap.put(parentNumber+","+childNumber, parentNumber+","+childNumber);
					}
					
					
					if(rows.length >= 8 && !StrUtils.isEmpty(substitute)){
						
						if(!sharePart.contains(substitute)){
							set.add(substitute);
						}
						
						//if(sharePart.contains(parentNumber) && sharePart.contains(childNumber) && sharePart.contains(substitute)){
							xmlSub.append("<csvSubstituteAdd handler='com.catl.bom.LoadSubstitute.addSubstituteToAssembly' >")
							.append("<csvassemblyPartNumber>").append(parentNumber).append("</csvassemblyPartNumber>")
							.append("<csvchildPartNumber>").append(childNumber).append("</csvchildPartNumber>")
							.append("<csvsubstitutePartNumber>").append(substitute).append("</csvsubstitutePartNumber>")
							.append("</csvSubstituteAdd>");
						//}
					}
					
				}
				xml.append("</NmLoader>");
				xmlSub.append("</NmLoader>");
				//System.out.println(xml.toString());
				//System.out.println(xmlSub.toString());
				//Java2XML.writerByString("<?xml version='1.0' encoding='UTF-8'?><books><book show='yes'><title>Dom4j Tutorials</title></book><book show='yes'><title>Lucene Studing</title></book><book show='no'><title>Lucene in Action</title></book><owner>O'Reilly</owner></books>", rootPath+"bom.xml");
				Java2XML.writerByString(xml.toString(), file.getAbsolutePath().substring(0,file.getAbsolutePath().indexOf("destotal")+9)+file.getName().replaceAll(" ", "_").substring(0,file.getName().lastIndexOf("."))+"_Bom.xml");
				Java2XML.writerByString(xmlSub.toString(), file.getAbsolutePath().substring(0,file.getAbsolutePath().indexOf("destotal")+9)+file.getName().replaceAll(" ", "_").substring(0,file.getName().lastIndexOf("."))+"_BomSubstitute.xml");
				
				impStatement.add("windchill wt.load.LoadFromFile -d d:\\ptc\\Windchill\\loadFiles\\com\\catl\\dms\\"+file.getName().replaceAll(" ", "_").substring(0,file.getName().lastIndexOf("."))+"_Bom.xml -u dms -p dms -CONT_PATH \\\"/wt.inf.container.OrgContainer=CATL\\\"");
				impStatement.add("windchill wt.load.LoadFromFile -d d:\\ptc\\Windchill\\loadFiles\\com\\catl\\dms\\"+file.getName().replaceAll(" ", "_").substring(0,file.getName().lastIndexOf("."))+"_BomSubstitute.xml -u dms -p dms -CONT_PATH \\\"/wt.inf.container.OrgContainer=CATL\\\"");
				
			} catch (Exception e) {
				System.out.println("第"+index+1+"出错,rows="+Arrays.toString(rows));
				e.printStackTrace();
			}
		}
		
		boolean flag = writeTxt(rootPath+"destotal\\MessageBom.txt",error,warn);
		System.out.println("write MessageBom flag="+flag);
		
		
		System.out.println("================================BOM Change End==============================");
		return impStatement;
	}
	
	/**
	 * 读取文档
	 * @param documentFiles
	 * @return
	 */
	private static List<String> document(List<File> documentFiles,List<String> docError,List<String> partStatement) {
		//File file = new File(rootPath+"destotal\\job1\\文档\\doc-SCUMBEV001(MUC-Job1)-20150918-正式版.xlsx");
		//File file = new File(rootPath+"destotal\\13KWh\\文档\\doc-STUYPHV001(ZZT-013kWh-普通版-0.5C-00478)-20150915-正式版.xlsx");
		
		folderMap.put("公共文档库", "技术规范文件库,文档模板库,PTS(零件技术规格书),ISA(检验基准书),WI(工作指示),PFC(过程流程图),FSC(正式特殊特性清单),CP(控制计划),SOP(标准操作程序),ENW(工程暂允),Checklist,优选库");
		
		
		System.out.println("================================Document Change Start==============================");
		List<String> impStatement = new ArrayList<String>();
		
		List<String> error = new ArrayList<String>();
		error.add("-----------------------------------------------------------error--------------------------------------------------------------");
		error.addAll(docError);
		
		for(File file : documentFiles){
			//System.out.println("::::::::"+file.getName().replaceAll(" ", "_").substring(0,file.getName().lastIndexOf(".")));
			
			Map<String,List<String[]>> containerClassify=new HashMap<String, List<String[]>>();
			
			System.out.println("file.getName():"+file.getName());
			String filePath = file.getName();
			String projectName=filePath.substring(0,filePath.lastIndexOf("."));
			ExcelReader readExcel = new ExcelReader(file);
			int index=0;
			int sheetIndex=0;
			//String sheetName="";
			String[] rows=null;
			try {
				readExcel.open();
				for(int sheet=0;sheet<1;sheet++){
					sheetIndex=sheet;
					readExcel.setSheetNum(sheet); // 设置读取索引为0的工作表
					
					// 总行数
					int count = readExcel.getRowCount();
										
					
					int  validresult=0;
					
					for (int i = 1; i <= count; i++) {
						index=i;
						rows = readExcel.readExcelLine(i);
						
						//System.out.println("Arrays.toString(rows):"+Arrays.toString(rows));
											
						
						if(rows == null || Arrays.toString(rows).equals("[, , , , , , , , ]")|| Arrays.toString(rows).equals("[, , , , , , , ]")||Arrays.toString(rows).equals("[, , , , , , , , , , , , , ]")){
							continue;
						}
						
						boolean nullValue=true;
						for(int y=0;y<rows.length;y++){
							if(!StrUtils.isEmpty(rows[y])){
								nullValue=false;
							}
						}
						
						if(nullValue){
							continue;
						}
						
						if(rows.length <15){
							if(rows.length >4)
								ObjectValidInfo.add(new String[]{"Doc","error","数据少于15列(文件夹)",rows[2],projectName+"(项目)",""});
								error.add(filePath+" 第"+(sheetIndex+1)+"sheet中的第"+(index+1)+"行,rows="+Arrays.toString(rows)+"没有导入，列少于15个(文件目录)");
							continue;
						}
						if(StrUtils.isEmpty(rows[1]) || StrUtils.isEmpty(rows[2]) || StrUtils.isEmpty(rows[3])
								|| StrUtils.isEmpty(rows[9]) || StrUtils.isEmpty(rows[14])){
							ObjectValidInfo.add(new String[]{"Doc","error","参数为空",rows[2],projectName+"(项目)",""});
							error.add(filePath+" 第"+(sheetIndex+1)+"sheet中的第"+(index+1)+"行,rows="+Arrays.toString(rows)+"没有导入，参数为空");
							continue;
						}
						if(!rows[1].equals("产品技术文件") && !rows[1].equals("研发过程文档") && !rows[1].equals("GERBER文件") && !rows[1].equals("PCBA装配图")){
							ObjectValidInfo.add(new String[]{"Doc","error","类型填写不正确",rows[2],projectName+"(项目)","类型填写不正确："+rows[1]});
							error.add(filePath+" 第"+(sheetIndex+1)+"sheet中的第"+(index+1)+"行,rows="+Arrays.toString(rows)+"没有导入，类型填写不正确："+rows[1]);
							continue;
						}
						File temp = new File(rootPath+"destotal"+rows[14].replace("&amp;", "&"));
						if(!temp.exists()){
							ObjectValidInfo.add(new String[]{"Doc","error","附件不存在",rows[2],projectName+"(项目)",rows[14]});
							error.add(filePath+" 第"+(sheetIndex+1)+"sheet中的第"+(index+1)+"行,rows="+Arrays.toString(rows)+"没有导入，附件不存在");
							continue;
						}
						
						//System.out.println("容器："+rows[0]+".....文件夹"+rows[9]+"("+rows[8]+")");
						String floder="";
						if(StrUtils.isEmpty(rows[8])){
							floder = rows[9];
						}else{
							floder = rows[9]+ "("+rows[8]+")";
						}
						if(!folderMap.containsKey(rows[0])){
							ObjectValidInfo.add(new String[]{"Doc","error","容器不存在",rows[2],projectName+"(项目)","不存在该容器:"+rows[0]});
							error.add(filePath+" 第"+(sheetIndex+1)+"sheet中的第"+(index+1)+"行,不存在该容器:"+rows[0]+"  值为"+Arrays.toString(rows));
							continue;
						}else if(!Arrays.asList(folderMap.get(rows[0]).split(",")).contains(floder)){						
							System.out.println("..容器："+rows[0]+".....文件夹"+floder);
							ObjectValidInfo.add(new String[]{"Doc","error","容器文件夹不存在",rows[2],projectName+"(项目)","容器:"+rows[0]+",不存在文件夹"+floder});
							error.add(filePath+" 第"+(sheetIndex+1)+"sheet中的第"+(index+1)+"行，"+",容器:"+rows[0]+",不存在文件夹"+floder+"  值为"+Arrays.toString(rows));
							continue;
						}
						
						if(!validRepeatDoc.containsKey(rows[2])){
							validRepeatDoc.put(rows[2], rows[2]);
						}else{
							ObjectValidInfo.add(new String[]{"Doc","error","文档编码重复",rows[2],projectName+"(项目)",""});
							continue;
						}
						
						if(containerClassify.get(rows[0])!=null){
							containerClassify.get(rows[0]).add(rows);
						}else{
							List<String[]> tlist=new ArrayList<String[]>();
							tlist.add(rows);
							containerClassify.put(rows[0], tlist);
						}			
						
					}
				
					for(String sheetName:containerClassify.keySet()){
						
						//System.out.println("sheetName:"+sheetName);
						
						List<String[]> rowsList=containerClassify.get(sheetName);
						

						StringBuffer xml = new StringBuffer("<?xml version='1.0' ?><!DOCTYPE NmLoader SYSTEM 'standardX24.dtd'><NmLoader>");
						StringBuffer xmlReference = new StringBuffer("<?xml version='1.0' ?><!DOCTYPE NmLoader SYSTEM 'standardX24.dtd'><NmLoader>");
						StringBuffer xmlDescribe = new StringBuffer("<?xml version='1.0' ?><!DOCTYPE NmLoader SYSTEM 'standardX24.dtd'><NmLoader>");
						
						for(int a=0;a<rowsList.size();a++){
							
							rows=rowsList.get(a);
							
							String floder="";
							if(StrUtils.isEmpty(rows[8])){
								floder = rows[9];
							}else{
								floder = rows[9]+ "("+rows[8]+")";
							}
							
							String uploadPath="/Default/"+floder+"/产品资料/"+rows[1];
						
							if(rows[0].equals("公共文档库")){
								uploadPath="/Default/"+floder;
							}
							
							if(rows[1].equals("PCBA装配图")){
								uploadPath="/Default/"+floder+"/设计图档/"+rows[1];
							}
							
							if(rows[1].equals("GERBER文件")){
								uploadPath="/Default/"+floder+"/设计图档/Gerber文件";
							}
							
							String name="dms";
							if(!StrUtils.isEmpty(rows[6])){
								name=rows[6];
							}
							
							//文档没有创建者标签
							xml.append("<csvBeginWTDocument handler='wt.doc.LoadDoc.beginCreateWTDocument' ><csvname>").append(rows[3]).append("</csvname><csvtitle>").append("").append("</csvtitle>")
							.append("<csvnumber>").append(rows[2]).append("</csvnumber>")
							.append("<csvtype>Document</csvtype><csvdescription>").append(rows[4]).append("</csvdescription><csvdepartment>DESIGN</csvdepartment>")
							.append("<csvsaveIn>").append(uploadPath).append("</csvsaveIn>")
							.append("<csvteamTemplate></csvteamTemplate><csvdomain></csvdomain>")
							.append("<csvlifecycletemplate>LC_doc_Cycle</csvlifecycletemplate><csvlifecyclestate>RELEASED</csvlifecyclestate>")
							.append("<csvtypedef>").append(Constant.TYPE_NAME.get(rows[1].trim())).append("</csvtypedef>")
							.append("<csvversion></csvversion>")
							.append("<csviteration></csviteration>")
							.append("<csvsecurityLabels></csvsecurityLabels></csvBeginWTDocument>");
							
							if(!StrUtils.isEmpty(rows[8])){
								xml.append("<csvIBAValue handler='wt.iba.value.service.LoadValue.createIBAValue' >")
								.append("<csvdefinition>projectName</csvdefinition>")
								.append("<csvvalue1>").append(rows[8]).append("</csvvalue1>")
								.append("<csvvalue2></csvvalue2><csvdependency_id></csvdependency_id></csvIBAValue>");
							}
							
							if(!StrUtils.isEmpty(rows[10])){
								xml.append("<csvIBAValue handler='wt.iba.value.service.LoadValue.createIBAValue' >")
								.append("<csvdefinition>outputPhase</csvdefinition>")
								.append("<csvvalue1>").append(rows[10]).append("</csvvalue1>")
								.append("<csvvalue2></csvvalue2><csvdependency_id></csvdependency_id></csvIBAValue>");
							}
							
							if(!StrUtils.isEmpty(rows[9])){
								xml.append("<csvIBAValue handler='wt.iba.value.service.LoadValue.createIBAValue' >")
								.append("<csvdefinition>projectCode</csvdefinition>")
								.append("<csvvalue1>").append(rows[9]).append("</csvvalue1>")
								.append("<csvvalue2></csvvalue2><csvdependency_id></csvdependency_id></csvIBAValue>");
							}
							
							if(!StrUtils.isEmpty(rows[12])){
								xml.append("<csvIBAValue handler='wt.iba.value.service.LoadValue.createIBAValue' >")
								.append("<csvdefinition>subCategory</csvdefinition>")
								.append("<csvvalue1>").append(rows[12]).append("</csvvalue1>")
								.append("<csvvalue2></csvvalue2><csvdependency_id></csvdependency_id></csvIBAValue>");
							}
							
							if(!StrUtils.isEmpty(rows[11])){
								xml.append("<csvIBAValue handler='wt.iba.value.service.LoadValue.createIBAValue' >")
								.append("<csvdefinition>docID</csvdefinition>")
								.append("<csvvalue1>").append(rows[11]).append("</csvvalue1>")
								.append("<csvvalue2></csvvalue2><csvdependency_id></csvdependency_id></csvIBAValue>");
							}
							
							xml.append("<csvEndWTDocument handler='wt.doc.LoadDoc.endCreateWTDocument' >")
							.append("<csvprimarycontenttype>ApplicationData</csvprimarycontenttype>")
							.append("<csvpath>").append("D:\\ptc\\Windchill\\loadFiles\\com\\catl\\dms"+rows[14]).append("</csvpath><csvformat></csvformat>")
							.append("<csvcontdesc></csvcontdesc><csvparentContainerPath></csvparentContainerPath>")
							.append("</csvEndWTDocument>");
							
							if(rows[15]!=null && !rows[15].trim().equals("")){
								
								List<String> relatePnList=new ArrayList<String>();
								
								String[] partNumberArrTemp= rows[15].split(",");
								for(int hh=0;hh<partNumberArrTemp.length;hh++){
									String valuetemp=partNumberArrTemp[hh];
									if(valuetemp.indexOf("@@")>-1){
										String[] partNumberChild=valuetemp.split("@@");
										//String partNumberChildValue0=partNumberChild[0];
										//String partNumberChildValue1=partNumberChild[1];
										
										String[] beforeValue=partNumberChild[0].split("-");
										String[] afterValue=partNumberChild[1].split("-");
										
										if(!beforeValue[0].equals(afterValue[0])){
											ObjectValidInfo.add(new String[]{"Doc","error","关联的PN格式不正确",rows[2],projectName+"(项目)",valuetemp});
											System.err.println("违反约定:"+valuetemp);
										}else{
											int beforeNum = Integer.valueOf(beforeValue[1]);
											int afterNum = Integer.valueOf(afterValue[1]);
											for(int yy=beforeNum;yy<=afterNum;yy++){
												String pn001="";
												for(int rr=String.valueOf(yy).length();rr<5;rr++){
													pn001=pn001+"0";
												}
												pn001=pn001+yy;
												relatePnList.add(beforeValue[0]+"-"+pn001);
											}
											//System.out.println(Integer.valueOf(beforeValue[1]));
											//System.out.println(Integer.valueOf(afterValue[1]));
										}
									}else{
										relatePnList.add(valuetemp);
									}
									
								}
								String[] partNumberArr =relatePnList.toArray(new String[relatePnList.size()]);
								
								//String[] partNumberArr = rows[15].split(",");
								for(int j=0; j<partNumberArr.length; j++){
									//if(partStatement.contains(partNumberArr[j])){
										if(rows[1].equals("产品技术文件") || rows[1].equals("研发过程文档")){  
											xmlReference.append("<csvPartDocReference handler='wt.part.LoadPart.createPartDocReference'>")
											.append("<csvdocNumber>").append(rows[2]).append("</csvdocNumber>")
											.append("<csvpartNumber>").append(partNumberArr[j]).append("</csvpartNumber>")
											.append("<csvpartVersion></csvpartVersion>")
											.append("<csvpartIteration></csvpartIteration>")
											.append("<csvpartView></csvpartView>")
											.append("<csvpartVariation1></csvpartVariation1>")
											.append("<csvpartVariation2></csvpartVariation2>")
											.append("<csvorganizationName></csvorganizationName>")
											.append("<csvorganizationID></csvorganizationID>")
											.append("</csvPartDocReference>");
										}else{
											xmlDescribe.append("<csvPartDocDescribes handler='wt.part.LoadPart.createPartDocDescribes' >")
											.append("<csvdocNumber>").append(rows[2]).append("</csvdocNumber>")
											.append("<csvdocVersion></csvdocVersion>")
											.append("<csvdocIteration></csvdocIteration>")
											.append("<csvpartNumber>").append(partNumberArr[j]).append("</csvpartNumber>")
											.append("<csvpartVersion></csvpartVersion>")
											.append("<csvpartIteration></csvpartIteration>")
											.append("<csvpartView></csvpartView>")
											.append("<csvpartVariation1></csvpartVariation1>")
											.append("<csvpartVariation2></csvpartVariation2>")
											.append("<csvorganizationName></csvorganizationName>")
											.append("<csvorganizationID></csvorganizationID>")
											.append("</csvPartDocDescribes>");
										}
									
									//}else{
									//	ObjectValidInfo.add(new String[]{"Doc","error","关联的PN不存在或数据不完整",rows[2],projectName+"(项目)","关联的PN "+partNumberArr[j]+"不存在或数据不完整，没有导入系统！"});
									//	error.add(filePath+" 第"+(sheetIndex+1)+"sheet中的第"+(index+1)+"行，"+"关联的PN "+partNumberArr[j]+"不存在或数据不完整，没有导入系统！"+"  值为"+Arrays.toString(rows));
									//}
								}
							}
						}
						xml.append("</NmLoader>");
						xmlReference.append("</NmLoader>");
						xmlDescribe.append("</NmLoader>");
						//System.out.println(file.getAbsolutePath().substring(0,file.getAbsolutePath().indexOf("destotal")+9)+file.getName().substring(0,file.getName().lastIndexOf("."))+"_"+sheetName+"_Document.xml");
						//Java2XML.writerByString("<?xml version='1.0' encoding='UTF-8'?><books><book show='yes'><title>Dom4j Tutorials</title></book><book show='yes'><title>Lucene Studing</title></book><book show='no'><title>Lucene in Action</title></book><owner>O'Reilly</owner></books>", rootPath+"bom.xml");
						
						Java2XML.writerByString(xml.toString(),file.getAbsolutePath().substring(0,file.getAbsolutePath().indexOf("destotal")+9)+file.getName().replaceAll(" ", "_").substring(0,file.getName().lastIndexOf("."))+"_"+sheetName+"_Document.xml");
						Java2XML.writerByString(xmlReference.toString(), file.getAbsolutePath().substring(0,file.getAbsolutePath().indexOf("destotal")+9)+file.getName().replaceAll(" ", "_").substring(0,file.getName().lastIndexOf("."))+"_"+sheetName+"_PartDocReference.xml");
						Java2XML.writerByString(xmlDescribe.toString(), file.getAbsolutePath().substring(0,file.getAbsolutePath().indexOf("destotal")+9)+file.getName().replaceAll(" ", "_").substring(0,file.getName().lastIndexOf("."))+"_"+sheetName+"_PartDocDescribe.xml");
						
						if(rows[0].equals("公共文档库")){
							impStatement.add("windchill wt.load.LoadFromFile -d d:\\ptc\\Windchill\\loadFiles\\com\\catl\\dms\\"+file.getName().replaceAll(" ", "_").substring(0,file.getName().lastIndexOf("."))+"_"+sheetName+"_Document.xml -u dms -p dms -CONT_PATH \\\"/wt.inf.container.OrgContainer=CATL/wt.inf.library.WTLibrary="+sheetName+"\\\"");
							impStatement.add("windchill wt.load.LoadFromFile -d d:\\ptc\\Windchill\\loadFiles\\com\\catl\\dms\\"+file.getName().replaceAll(" ", "_").substring(0,file.getName().lastIndexOf("."))+"_"+sheetName+"_PartDocReference.xml -u dms -p dms -CONT_PATH \\\"/wt.inf.container.OrgContainer=CATL/wt.inf.library.WTLibrary="+sheetName+"\\\"");
							impStatement.add("windchill wt.load.LoadFromFile -d d:\\ptc\\Windchill\\loadFiles\\com\\catl\\dms\\"+file.getName().replaceAll(" ", "_").substring(0,file.getName().lastIndexOf("."))+"_"+sheetName+"_PartDocDescribe.xml -u dms -p dms -CONT_PATH \\\"/wt.inf.container.OrgContainer=CATL/wt.inf.library.WTLibrary="+sheetName+"\\\"");
						
						}else{
							impStatement.add("windchill wt.load.LoadFromFile -d d:\\ptc\\Windchill\\loadFiles\\com\\catl\\dms\\"+file.getName().replaceAll(" ", "_").substring(0,file.getName().lastIndexOf("."))+"_"+sheetName+"_Document.xml -u dms -p dms -CONT_PATH \\\"/wt.inf.container.OrgContainer=CATL/wt.pdmlink.PDMLinkProduct="+sheetName+"\\\"");
							impStatement.add("windchill wt.load.LoadFromFile -d d:\\ptc\\Windchill\\loadFiles\\com\\catl\\dms\\"+file.getName().replaceAll(" ", "_").substring(0,file.getName().lastIndexOf("."))+"_"+sheetName+"_PartDocReference.xml -u dms -p dms -CONT_PATH \\\"/wt.inf.container.OrgContainer=CATL/wt.pdmlink.PDMLinkProduct="+sheetName+"\\\"");
							impStatement.add("windchill wt.load.LoadFromFile -d d:\\ptc\\Windchill\\loadFiles\\com\\catl\\dms\\"+file.getName().replaceAll(" ", "_").substring(0,file.getName().lastIndexOf("."))+"_"+sheetName+"_PartDocDescribe.xml -u dms -p dms -CONT_PATH \\\"/wt.inf.container.OrgContainer=CATL/wt.pdmlink.PDMLinkProduct="+sheetName+"\\\"");
						
						}
						
						
					
					
					}
				}
			}catch (Exception e) {
				System.out.println("第"+(sheetIndex+1)+"sheet中的第"+(index+1)+"行出错,rows="+Arrays.toString(rows));
				e.printStackTrace();
			}
		}
		
		boolean flag = writeTxt(rootPath+"destotal\\MessageDoc.txt",error);
		System.out.println("write MessageDoc flag="+flag);
		
		folderMap.remove("公共文档库");
		System.out.println("================================Document Change End==============================");
		return impStatement;
		
	}
	
	
	private static void classifyAttrConstraint(){
		
		File file = new File(rootPath+"destotal/clsAttr.xlsx");
		ExcelReader readExcel = new ExcelReader(file);
		String[] rows=null;
		try {
			readExcel.open();
			readExcel.setSheetNum(0); // 设置读取索引为0的工作表
			// 总行数
			int count = readExcel.getRowCount();
			for(int i=1;i<=count;i++){
				rows = readExcel.readExcelLine(i);
				//System.err.println("....1111:"+rows[0]+","+rows[3]);
				classifyAttrConstraintData.put(rows[0]+","+rows[3], new String[]{rows[4],rows[5]});
			}
			
//			for(String key:classifyAttrConstraintData.keySet()){
//				String[] value=classifyAttrConstraintData.get(key);
//				System.err.println(key+"../"+value[0]+","+value[1]);
//			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}

	private static List<String> part() throws DocumentException {
		
		//ExportClassification.exportClassification(rootPath+"destotal\\Classification_3.xml");
		
		Map<String,String> partAttachment= loadPartAttachment();
		
		classifyAttrConstraint();
		
		System.out.println("================================Part Change Start==============================");
		List<String> impStatement = new ArrayList<String>();
		List<String> error = new ArrayList<String>();
		error.add("-----------------------------------------------------------error--------------------------------------------------------------");
		List<String> warn = new ArrayList<String>();
		warn.add("-----------------------------------------------------------warn--------------------------------------------------------------");
		
		File file = new File(rootPath+"destotal/partlist_test.xlsx");
		System.out.println(file);
		ExcelReader readExcel = new ExcelReader(file);
		int index=0;
		String[] rows=null;
		try {
			readExcel.open();
			readExcel.setSheetNum(readExcel.getSheetIndex("整理明细")); // 设置读取索引为1的工作表
			// 总行数
			int count = readExcel.getRowCount();
			StringBuffer xml;
			Map<String,StringBuffer> mapBuffer =  new HashMap<String, StringBuffer>();
			List<String> classifyAttrName=new ArrayList<String>();
			for (int i = 2; i <= count; i++) {
				if(i==3513){
					System.out.println("debug.........");
				}
				index=i;
				rows = readExcel.readExcelLine(i);
				if(rows == null){
					continue;
				}
				
				boolean nullValue=true;
				for(int y=0;y<rows.length;y++){
					if(!StrUtils.isEmpty(rows[y])){
						nullValue=false;
					}
				}
				
				if(nullValue){
					continue;
				}				
				
				if(rows.length<17){
					if(rows.length>8 && !StrUtils.isEmpty(rows[8])){
						
						ObjectValidInfo.add(new String[]{"Part","error","数据少于17列(文件夹)",rows[8],rows[15]+"(容器)",""});
						error.add("第"+(i+1)+"行，"+",少于17列(文件夹)，有新物料编号,没有导入xml  值为"+Arrays.toString(rows));
					}
					continue;
				}
				
				if(StrUtils.isEmpty(rows[0])&&StrUtils.isEmpty(rows[1])&&StrUtils.isEmpty(rows[2])&&StrUtils.isEmpty(rows[3])&&StrUtils.isEmpty(rows[4])&&
						StrUtils.isEmpty(rows[5])&&StrUtils.isEmpty(rows[6])&&StrUtils.isEmpty(rows[7])&&StrUtils.isEmpty(rows[8])&&StrUtils.isEmpty(rows[9])&&
						StrUtils.isEmpty(rows[10])&&StrUtils.isEmpty(rows[11])&&StrUtils.isEmpty(rows[12])&&StrUtils.isEmpty(rows[13])&&StrUtils.isEmpty(rows[14])&&
						StrUtils.isEmpty(rows[15])&&StrUtils.isEmpty(rows[16])&&StrUtils.isEmpty(rows[17])
						&& !StrUtils.isEmpty(rows[18])){//分类属性行
					classifyAttrName=new ArrayList<String>();
					for(int j=15;j<rows.length;j++){
						System.out.println("j\t"+j);
						if(!StrUtils.isEmpty(rows[j])){
							classifyAttrName.add(rows[j].replace("（", "(").replace("）", ")"));
							System.out.println("j\t"+j+"\t"+rows[j]);
						}else{
							System.out.println("j is null");
						}
					}
					continue;
				}
				if(StrUtils.isEmpty(rows[0])&&StrUtils.isEmpty(rows[1])&&StrUtils.isEmpty(rows[2])&&StrUtils.isEmpty(rows[3])&&StrUtils.isEmpty(rows[4])&&
						StrUtils.isEmpty(rows[5])&&StrUtils.isEmpty(rows[6])&&StrUtils.isEmpty(rows[7])&&StrUtils.isEmpty(rows[8])){
					continue;
				}
				
				/*if(StrUtils.isEmpty(rows[1])){
					ObjectValidInfo.add(new String[]{"Part","error","物料编码为空(第2列)",rows[8],rows[15]+"(容器)",""});
					continue;
				}*/
				
				if(StrUtils.isEmpty(rows[8])){
					ObjectValidInfo.add(new String[]{"Part","error","新物料编码为空(第8列)",rows[8],rows[15]+"(容器)",""});
					error.add("第"+(i+1)+"行，"+",第8列(新物料编码)为空,没有导入xml  值为"+Arrays.toString(rows));
					continue;
				}
				
				
				
				if(StrUtils.isEmpty(rows[12])){
					ObjectValidInfo.add(new String[]{"Part","error","单位为空(第12列)",rows[8],rows[15]+"(容器)",""});
					error.add("第"+(i+1)+"行，"+",第12列(单位)为空,没有导入xml  值为"+Arrays.toString(rows));
					continue;		//20151221
				}
				
				//IBA属性 分类
				String classIntName =rows[6];
				if(classIntName.endsWith("00"))
					classIntName = classIntName.substring(0,4);
				if(classIntName.endsWith("00"))
					classIntName = classIntName.substring(0,2);
				//String unit = (StrUtils.isEmpty(rows[12]))?"pcs":rows[12].toLowerCase();
				String unit = rows[12].toLowerCase();
				
				if(descriptionMap.get(classIntName) != null){
					if(!descriptionMap.get(classIntName).contains(unit+"|")){
						ObjectValidInfo.add(new String[]{"Part","error","单位不正确",rows[8],rows[15]+"(容器)","填写的单位："+unit+",限制的单位:"+descriptionMap.get(classIntName)});
						error.add("第"+(i+1)+"行，"+",单位不正确,没有导入xml,填写的单位："+unit+",限制的单位:"+descriptionMap.get(classIntName)+"  值为"+Arrays.toString(rows));
						continue;		//20151221
					}
				}
				unit=unit.equals("pcs")?"ea":unit;
				if(!allInstanceClassfy.contains(classIntName)){
					ObjectValidInfo.add(new String[]{"Part","error","系统中没有该分类或不可实例化",rows[8],rows[15]+"(容器)","填写的分类："+classIntName});
					error.add("第"+(i+1)+"行，"+",系统中没有该分类或不可实例化,没有导入xml,填写的分类："+classIntName+"  值为"+Arrays.toString(rows));
					continue;		//20151221
				}
				 
				 
				String name="";
				if(!StrUtils.isEmpty(rows[9])){
					if(rows[9].length()>40){
						ObjectValidInfo.add(new String[]{"Part","error","名称太长超过40个字",rows[8],rows[15]+"(容器)",""});
						error.add("第"+(i+1)+"行，"+",名称太长超过40个    值为"+Arrays.toString(rows));
						continue;		//20151221
					}
					name=rows[9];
				}else{
					name="待定";
				}
				if(!StrUtils.isEmpty(rows[10]) && rows[10].length()>40){
					ObjectValidInfo.add(new String[]{"Part","error","英文名词太长超过40个",rows[8],rows[15]+"(容器)",""});
					error.add("第"+(i+1)+"行，"+",英文名词太长超过40个    值为"+Arrays.toString(rows));
					continue;		//20151221
				}
				
				if(!StrUtils.isEmpty(rows[11]) && Constant.TYPE_NAME.get(rows[11])==null){
					ObjectValidInfo.add(new String[]{"Part","error","来源不符合要求",rows[8],rows[15]+"(容器)",""});
					continue;		//20151221
				}
				
				String container=rows[15];
				if(StrUtils.isEmpty(container)){//没有填写容器
					ObjectValidInfo.add(new String[]{"Part","error","容器为空(第16列)",rows[8],rows[15]+"(容器)",""});
					error.add("第"+(i+1)+"行，"+",第16列(容器)为空  值为"+Arrays.toString(rows));
					continue;		//20151221
				}
				
				String product=container;
				String folder=rows[16].replace("\\", "");
				String proOrLib="wt.inf.library.WTLibrary=";
				if(!container.equals("电芯") && !container.equals("电子电气件") && !container.equals("紧固件") && !container.equals("原材料")&& !container.equals("设备开发标准件库")){
					
					
					if(!folderMap.containsKey(container)){
						ObjectValidInfo.add(new String[]{"Part","error","容器不存在",rows[8],rows[15]+"(容器)","不存在该容器:"+container});
						error.add("第"+(i+1)+"行，"+",不存在该容器:"+container+"  值为"+Arrays.toString(rows));
						continue;		//20151221
					}else if(!Arrays.asList(folderMap.get(container).split(",")).contains(folder)){		
						ObjectValidInfo.add(new String[]{"Part","error","容器的文件夹不存在",rows[8],rows[15]+"(容器)","容器:"+container+",不存在文件夹"+folder});
						error.add("第"+(i+1)+"行，"+",容器:"+container+",不存在文件夹"+folder+"  值为"+Arrays.toString(rows));
						continue;		//20151221
					}
					
					proOrLib="wt.pdmlink.PDMLinkProduct=";
					//product=rows[16].replace("\\", "");
					folder=folder+"/零部件";
				}
				
				if(!validRepeatPart.containsKey(rows[8])){
					validRepeatPart.put(rows[8], rows[8]);
				}else{
					ObjectValidInfo.add(new String[]{"Part","error","新物料编码重复",rows[8],rows[15]+"(容器)","第"+(i+1)+"行"});
					continue;
				}
				
				partToFolder.put(rows[8], new String[]{rows[15].trim(),rows[16].trim()});
								
				sharePart.add(rows[8]);
								
				numNew.put(rows[8], rows[8]);
				
				if(!StrUtils.isEmpty(rows[1])){
					if(!numMap.containsKey(rows[1])){
						numMap.put(rows[1], rows[8]);
					}else{
						ObjectValidInfo.add(new String[]{"Part","error","物料编码重复",rows[1],rows[15]+"(容器)",""});
					}
				}else{
					numMap.put(rows[8], rows[8]);
				}
				
				if(classifyAttrName.contains("产品能量") || classifyAttrName.contains("标称电压")){
					evMap.add(new String[]{rows[8]});
				}				
				
				if(mapBuffer.get(product) == null){
					xml = new StringBuffer("<?xml version='1.0' ?><!DOCTYPE NmLoader SYSTEM 'standardX24.dtd'><NmLoader>");
					mapBuffer.put(product, xml);
					impStatement.add("windchill wt.load.LoadFromFile -d d:\\ptc\\Windchill\\loadFiles\\com\\catl\\dms\\"+file.getName().substring(0,file.getName().lastIndexOf("."))+"_"+product.replaceAll(" ", "_").replaceAll("&amp;", "_")+".xml -u dms -p dms -CONT_PATH \\\"/wt.inf.container.OrgContainer=CATL/"+proOrLib+container+"\\\"");
				}else{
					xml = mapBuffer.get(product);
				}
				
				xml.append("<csvBeginWTPart handler='wt.part.LoadPart.beginCreateWTPart' ><csvuser>").append(StrUtils.isEmpty(rows[14])?"dms":rows[14]).append("</csvuser>")
				.append("<csvpartName>").append(name).append("</csvpartName>")
				.append("<csvpartNumber>").append(rows[8]).append("</csvpartNumber>")
				.append("<csvtype>separable</csvtype><csvgenericType></csvgenericType><csvcollapsible></csvcollapsible><csvlogicbasePath></csvlogicbasePath>")
				.append("<csvsource>").append(StrUtils.isEmpty(rows[11])?"make":Constant.TYPE_NAME.get(rows[11])).append("</csvsource><csvfolder>/Default").append("/"+folder).append("</csvfolder>")
				.append("<csvlifecycle>LC_Part_Cycle</csvlifecycle><csvview>Design</csvview>")
				.append("<csvvariation1></csvvariation1><csvvariation2></csvvariation2><csvteamTemplate></csvteamTemplate><csvlifecyclestate>RELEASED</csvlifecyclestate>")
				.append("<csvtypedef>com.CATLBattery.CATLPart</csvtypedef>")
				.append("<csvversion></csvversion>")
				.append("<csviteration></csviteration>")
				.append("<csvenditem></csvenditem><csvtraceCode></csvtraceCode><csvorganizationName></csvorganizationName><csvorganizationID></csvorganizationID>")
				.append("<csvsecurityLabels></csvsecurityLabels><csvcreateTimestamp></csvcreateTimestamp><csvmodifyTimestamp></csvmodifyTimestamp>")
				.append("<csvminRequired></csvminRequired><csvmaxAllowed></csvmaxAllowed><csvdefaultUnit>").append(unit).append("</csvdefaultUnit><csvserviceable></csvserviceable>")
				.append("<csvservicekit></csvservicekit><csvauthoringLanguage></csvauthoringLanguage></csvBeginWTPart>");
				
				//IBA属性旧物料号
				if(!StrUtils.isEmpty(rows[1])){
					xml.append("<csvIBAValue handler='wt.iba.value.service.LoadValue.createIBAValue' >")
					.append("<csvdefinition>oldPartNumber</csvdefinition>")
					.append("<csvvalue1>").append(rows[1]).append("</csvvalue1>")
					.append("<csvvalue2></csvvalue2><csvdependency_id></csvdependency_id></csvIBAValue>");
				}
				//IBA属性英文名词
				if(!StrUtils.isEmpty(rows[10])){
					xml.append("<csvIBAValue handler='wt.iba.value.service.LoadValue.createIBAValue' >")
					.append("<csvdefinition>englishName</csvdefinition>")
					.append("<csvvalue1>").append(rows[10]).append("</csvvalue1>")
					.append("<csvvalue2></csvvalue2><csvdependency_id></csvdependency_id></csvIBAValue>");
				}
				//IBA属性规格
				xml.append("<csvIBAValue handler='wt.iba.value.service.LoadValue.createIBAValue' >")
				.append("<csvdefinition>specification</csvdefinition>")
				.append("<csvvalue1>specificationTemp</csvvalue1>")
				.append("<csvvalue2></csvvalue2><csvdependency_id></csvdependency_id></csvIBAValue>");
				
				
				xml.append("<csvIBAValue handler='wt.iba.value.service.LoadValue.createIBAValue' >")
				.append("<csvdefinition>").append(Constant.TYPE_NAME.get("分类")).append("</csvdefinition>")
				.append("<csvvalue1>").append(classIntName).append("</csvvalue1>")
				.append("<csvvalue2></csvvalue2><csvdependency_id></csvdependency_id></csvIBAValue>");
				
				String specificationTemp="";
				String clsErr="";
				System.out.println(MainClass.class.getName()+" size\t"+classifyAttrName.size());
				for(int j=0;j<classifyAttrName.size();j++){
					String attrName = classifyAttrName.get(j);
					
					String attrVal ="";
					try{						
						attrVal = rows[18+j];
						specificationTemp=specificationTemp+"_"+attrName+":"+rows[18+j];
					}catch(ArrayIndexOutOfBoundsException e){
						attrVal="";
					}
					
					//IBA属性 分类属性
					if(Constant.TYPE_NAME.get(attrName) != null){						
						
						/*String[] constraints=classifyAttrConstraintData.get(classIntName+","+attrName+"("+Constant.TYPE_NAME.get(attrName)+")");
						if(constraints!=null){
							
							if(!StrUtils.isEmpty(constraints[0]) && constraints[0].equals("是") && StrUtils.isEmpty(attrVal)){
								ObjectValidInfo.add(new String[]{"Part","error","不符合分类属性约束条件",rows[8],rows[15]+"(容器)","分类属性:"+attrName+"是必填项"});
								attrVal="缺";
								//continue;
							}
							
							if(!StrUtils.isEmpty(constraints[1]) && !Arrays.asList(constraints[1].split("\\|")).contains(attrVal)){
								ObjectValidInfo.add(new String[]{"Part","error","不符合分类属性约束条件",rows[8],rows[15]+"(容器)","分类属性:"+attrName+"值为"+attrVal+" 可选值["+constraints[1]+"]"});
								//attrVal=Arrays.asList(constraints[1].split("\\|")).get(0);
								//continue;
							}
							
						}else{
							ObjectValidInfo.add(new String[]{"Part","error","分类缺少属性",rows[8],rows[15]+"(容器)",classIntName+","+attrName+"("+Constant.TYPE_NAME.get(attrName)+")"});
							continue;
						}
												
						
						if(!StrUtils.isEmpty(attrVal) && attrName.contains("(") && attrName.contains(")")){
							//attrVal=attrVal.replaceAll("<=", "").replaceAll("<", "").replaceAll(">=", "").replaceAll(">", "").replaceAll("mm", "").replaceAll("φ", "");
							try{
								Double.valueOf(attrVal);
								attrVal=attrVal+" "+attrName.substring(attrName.indexOf("(")+1,attrName.indexOf(")"));
							}catch (Exception e){
								ObjectValidInfo.add(new String[]{"Part","error","分类属性带单位值必须是数字",rows[8],rows[15]+"(容器)","分类属性:"+attrName+",带单位，值必须是数字,填写的值:"+attrVal});
								
								error.add("第"+(i+1)+"行，"+",分类属性:"+attrName+",带单位，值必须是数字,填写的值:"+attrVal+"  值为"+Arrays.toString(rows));
								attrVal="0 "+attrName.substring(attrName.indexOf("(")+1,attrName.indexOf(")"));
								//continue;
							}
						}*/
						
						if(!StrUtils.isEmpty(attrVal)){
							xml.append("<csvIBAValue handler='wt.iba.value.service.LoadValue.createIBAValue' >")
							.append("<csvdefinition>").append(Constant.TYPE_NAME.get(attrName)).append("</csvdefinition>")
							.append("<csvvalue1>").append(attrVal).append("</csvvalue1>")
							.append("<csvvalue2></csvvalue2><csvdependency_id></csvdependency_id></csvIBAValue>");
						}
					}else{
						System.out.println(MainClass.class.getName()+"\t"+attrName);
						clsErr+=attrName+",";
					}					
					
				}
				if(!clsErr.equals("")){
					clsErr=clsErr.substring(0,clsErr.length()-1);
					
					ObjectValidInfo.add(new String[]{"Part","warn","系统没有该分类属性",rows[8],rows[15]+"(容器)","分类属性:"+clsErr+"系统中没有,IBA分类属性没有导入xml"});
					warn.add("第"+(i+1)+"行，"+",分类属性:"+clsErr+"的值为空或没有导入该分类属性,IBA分类属性没有导入xml  值为"+Arrays.toString(rows));
				}
				if(specificationTemp.length()>0){
					specificationTemp=specificationTemp.substring(1);
				}
				
				specificationMap.add(new String[]{rows[8],specificationTemp});				
				
				xml.replace(xml.indexOf("specificationTemp"), xml.indexOf("specificationTemp")+17, specificationTemp);
				
				String partAttachmentPath=partAttachment.get(rows[8]);
				if(partAttachmentPath!=null){
					xml.append("<csvContentFile handler='wt.load.LoadContent.createContentFile' >")
					.append("<csvuser></csvuser>").append("<csvpath>").append(partAttachmentPath.replaceAll("&","&amp;")).append("</csvpath>").append("</csvContentFile>");
					
					partAttachment.remove(rows[8]);
				}
				
				xml.append("<csvEndWTPart handler='wt.part.LoadPart.endCreateWTPart' >")
				.append("<csvparentContainerPath></csvparentContainerPath></csvEndWTPart>");
				
			}
			for(Entry<String, StringBuffer> entry : mapBuffer.entrySet()){
				entry.getValue().append("</NmLoader>");
				//System.out.println(entry.getValue());
				Java2XML.writerByString(entry.getValue().toString(), file.getAbsolutePath().substring(0,file.getAbsolutePath().lastIndexOf("."))+"_"+"20171114"+".xml");
			}
			
			for(String at:partAttachment.keySet()){
				ObjectValidInfo.add(new String[]{"二维图/PDF","warn","PDF没有对应PN","","",partAttachment.get(at)});
			}
			
		} catch (Exception e) {
			System.out.println("第"+(index+1)+"出错,rows="+Arrays.toString(rows));
			e.printStackTrace();
		}
		boolean flag = writeTxt(rootPath+"destotal/MessagePart.txt",error,warn);
		System.out.println("write MessagePart flag="+flag);
		
		System.out.println("================================Part Change End==============================");
		return impStatement;
	}
	
	private static int String_length(String value) {
		  int valueLength = 0;
		  String chinese = "[\u4e00-\u9fa5]";
		  for (int i = 0; i < value.length(); i++) {
		   String temp = value.substring(i, i + 1);
		   if (temp.matches(chinese)) {
		    valueLength += 2;
		   } else {
		    valueLength += 1;
		   }
		  }
		  return valueLength;
	}
	
	
	
	public static void outputValid(){
		ExcelWriter writer = new ExcelWriter();
		try {
			boolean flag = writer.exportExcelList(rootPath+"destotal/trainProblem.xlsx","演练问题汇总", new String[]{"数据类型","问题级别","问题类型","编码","容器/项目","备注"}, ObjectValidInfo);
			System.out.println("演练问题汇总.xlsx flag="+flag);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public static List<String> productStatement() throws Exception{
		
		StringBuffer xml = new StringBuffer("<?xml version='1.0' ?><!DOCTYPE NmLoader SYSTEM 'standardX24.dtd'><NmLoader>");
		for(String key:folderMap.keySet()){
			xml.append("<csvProductContainer handler='wt.part.LoadPart.createProductContainer' >");
			xml.append("<csvuser>orgadmin</csvuser>");
			xml.append("<csvname>"+key+"</csvname>");
			xml.append("<csvnumber></csvnumber>");
			xml.append("<csvsharedTeamName></csvsharedTeamName>");
			xml.append("<csvcontainerExtendable></csvcontainerExtendable>");
			xml.append("<csvdescription></csvdescription>");
			xml.append("<csvview></csvview>");
			xml.append("<csvvariation1></csvvariation1>");
			xml.append("<csvvariation2></csvvariation2>");
			xml.append("<csvsource></csvsource>");
			xml.append("<csvdefaultUnit></csvdefaultUnit>");
			xml.append("<csvtype></csvtype>");
			xml.append("<csvcontainerTemplate>CATL Product 1.0</csvcontainerTemplate>");
			xml.append("<csvorganizationName>CATL</csvorganizationName>");
			xml.append("<csvorganizationID></csvorganizationID>");
			xml.append("</csvProductContainer>");
		}
		xml.append("</NmLoader>");
		
		Java2XML.writerByString(xml.toString(),"E:\\ATL\\project\\PLM\\loadtest\\destotal\\productcontainer.xml");
		List<String> productCmd=new ArrayList<String>();
		productCmd.add(0,"-----product");
		productCmd.add("windchill wt.load.LoadFromFile -d d:\\ptc\\Windchill\\loadFiles\\com\\catl\\dms\\productcontainer.xml"+" -u dms -p dms -CONT_PATH \\\"/wt.inf.container.OrgContainer=CATL\\\"");
		return productCmd;
	}
	
	public static List<String>  libraryStatement() throws Exception{
		List<String[]> allLibrary=new ArrayList<String[]>();
		String[] library1=new String[]{"电芯","CATL Cell","orgadmin","CATL"};
		allLibrary.add(library1);
		String[] library2=new String[]{"电子电气件","CATL Electronic","orgadmin","CATL"};
		allLibrary.add(library2);
		String[] library3=new String[]{"紧固件","CATL Fastener","orgadmin","CATL"};
		allLibrary.add(library3);
		String[] library4=new String[]{"公共文档库","CATL Public Document","orgadmin","CATL"};
		allLibrary.add(library4);
		String[] library5=new String[]{"原材料","CATL Raw Materials","orgadmin","CATL"};
		allLibrary.add(library5);
		String[] library6=new String[]{"模板库","CATL Templates","orgadmin","CATL"};
		allLibrary.add(library6);
		
		List<String> libraryCmd=new ArrayList<String>();
		StringBuffer xml = new StringBuffer("<?xml version='1.0' ?><!DOCTYPE NmLoader SYSTEM 'standardX24.dtd'><NmLoader>");
		
		for(int i=0;i<allLibrary.size();i++){
			xml.append("<csvContainer handler='wt.inf.container.LoadContainer.createContainer' >");
			xml.append("<csvcontainerClass>wt.inf.library.WTLibrary</csvcontainerClass>");
			xml.append("<csvcontainerName>"+allLibrary.get(i)[0]+"</csvcontainerName>");
			xml.append("<csvsharedTeamName></csvsharedTeamName>");
			xml.append("<csvcontainerExtendable></csvcontainerExtendable>");
			xml.append("<csvparentContainerPath></csvparentContainerPath>");
			xml.append("<csvcontainerTemplateRef>"+allLibrary.get(i)[1]+"</csvcontainerTemplateRef>");
			xml.append("<csvbusinessNamespace></csvbusinessNamespace>");
			xml.append("<csvsharingEnabled></csvsharingEnabled>");
			xml.append("<csvcreator>"+allLibrary.get(i)[2]+"</csvcreator>");
			xml.append("<csvowner></csvowner>");
			xml.append("<csvsubscriber></csvsubscriber>");
			xml.append("<csvconferencingURL></csvconferencingURL>");
			xml.append("<csvdescription></csvdescription>");
			xml.append("<csvorganization>"+allLibrary.get(i)[3]+"</csvorganization>");
			xml.append("<csvcreatorSelector></csvcreatorSelector>");
			xml.append("</csvContainer>");
		}
		xml.append("</NmLoader>");
		Java2XML.writerByString(xml.toString(),"E:\\ATL\\project\\PLM\\loadtest\\destotal\\librarycontainer.xml");
		libraryCmd.add(0,"-----library");
		libraryCmd.add("windchill wt.load.LoadFromFile -d d:\\ptc\\Windchill\\loadFiles\\com\\catl\\dms\\librarycontainer.xml"+" -u dms -p dms -CONT_PATH \\\"/wt.inf.container.OrgContainer=CATL\\\"");
		return libraryCmd;
	}		
	
	
	private static void getAllFile1(Map<String,String> map_2d_all,Map<String,File> map_2d,Map<String,String> map_pdf_all,Map<String,File> map_pdf,File root,String proName){
		if(root.isDirectory()){
			File[] files = root.listFiles();
			for(File file : files){
				if(!file.isDirectory()){
					if(file.getName().lastIndexOf(".dwg")>0){
						String pn=file.getName().substring(0,file.getName().indexOf("."));
						
						if(map_2d_all.containsKey(pn)){
							//ObjectValidInfo.add(new String[]{"二维图/PDF","warn","AutoCAD文件重复","","","项目"+proName+"的"+pn+".dwg文件与项目"+map_2d_all.get(pn)+"的"+pn+".dwg文件"+"重复"});
							continue;
						}else{
							map_2d_all.put(pn, proName);
							map_2d.put(pn, file);
						}
					}
					
					if(file.getName().indexOf(".pdf")>0){
						String pn=file.getName().substring(0,file.getName().indexOf("."));
						
						map_pdf.put(pn, file);
						if(map_pdf_all.containsKey(pn)){
							//ObjectValidInfo.add(new String[]{"二维图/PDF","warn","AutoCAD文件重复","","","项目"+proName+"的"+pn+".pdf文件与项目"+map_pdf_all.get(pn)+"的"+pn+".pdf文件"+"重复"});
							continue;
						}else{
							map_pdf_all.put(pn, proName);
						}						
					}
				}else{
					getAllFile1(map_2d_all,map_2d,map_pdf_all,map_pdf,file,proName);
				}
			}
		}
	}	
	
	private static void getAllFile(Map<String,String> map_2d_all,Map<String,File> map_2d,Map<String,String> map_pdf_all,Map<String,File> map_pdf,File root,String proName){
		if(root.isDirectory()){
			File[] files = root.listFiles();
			for(File file : files){
				if(!file.isDirectory()){
					if(file.getName().lastIndexOf(".dwg")>0){
						String pn=file.getName().substring(0,file.getName().indexOf("."));
						
						if(map_2d_all.containsKey(pn)){
							ObjectValidInfo.add(new String[]{"二维图/PDF","warn","AutoCAD文件重复","","","项目"+proName+"的"+pn+".dwg文件与项目"+map_2d_all.get(pn)+"的"+pn+".dwg文件"+"重复"});
							continue;
						}else{
							map_2d_all.put(pn, proName);
							map_2d.put(pn, file);
						}
					}
					
					if(file.getName().indexOf(".pdf")>0){
						String pn=file.getName().substring(0,file.getName().indexOf("."));						
						map_pdf.put(pn, file);
						if(map_pdf_all.containsKey(pn)){
							ObjectValidInfo.add(new String[]{"二维图/PDF","warn","AutoCAD文件重复","","","项目"+proName+"的"+pn+".pdf文件与项目"+map_pdf_all.get(pn)+"的"+pn+".pdf文件"+"重复"});
							continue;
						}else{
							map_pdf_all.put(pn, proName);
						}
					}
				}else{
					getAllFile(map_2d_all,map_2d,map_pdf_all,map_pdf,file,proName);
				}
			}
		}
	}
	
	/**
	 * 导入AutoCAD文件
	 * @return
	 * @throws Exception
	 */
	public static List<String> loadAutoCAD(Map<String,String[]> partToFolder) throws Exception{
				
		Map<String,String> map_2d_all=new HashMap<String,String>();
		Map<String,String> map_pdf_all=new HashMap<String,String>();
		
		Map<String,Map<String,File>> allPorjectDwg=new HashMap<String, Map<String,File>>();
		
		Map<String,File> allPorjectPDF=new HashMap<String, File>();
		
		List<String> impStatement = new ArrayList<String>();
		
		String initPath=rootPath+"destotal\\";
		
		File root = new File(rootPath+"destotal\\");
		if(root.isDirectory()){
			File[] files = root.listFiles();
			for(File file : files){
				if(file.isDirectory()){
					
					File autoCADFile=new File(file.getAbsolutePath()+"\\");
					Map<String,File> map_2d=new HashMap<String,File>();
					Map<String,File> map_pdf=new HashMap<String,File>();
					
					String proName=file.getName();
					if(autoCADFile.exists()){
						getAllFile(map_2d_all,map_2d,map_pdf_all,map_pdf,autoCADFile,proName);
					}
					
					//System.out.println("map_2d.size:"+map_2d.size());
					//System.out.println("map_pdf.size"+map_pdf.size());
					
					if(map_2d.size()>0){						
						
						for(String dwg:map_2d.keySet()){
							
							String[] info=partToFolder.get(dwg);							

							if(info!=null){
								
								if(allPorjectDwg.get(info[0])==null){
									Map<String,File> tempMap=new HashMap<String,File>();
									tempMap.put(dwg, map_2d.get(dwg));
									allPorjectDwg.put(info[0], tempMap);
								}else{
									allPorjectDwg.get(info[0]).put(dwg, map_2d.get(dwg));								
								}
								
								if(map_pdf.get(dwg)!=null){									
									
									allPorjectPDF.put(dwg, map_pdf.get(dwg));
									
								}else{
									ObjectValidInfo.add(new String[]{"二维图/PDF","warn","AutoCAD文件不完整","","","项目"+map_2d_all.get(dwg)+"的"+dwg+".dwg"+" 对应的pdf文件"+dwg+".pdf"+"未整理"});
								}
							
							}else{
								ObjectValidInfo.add(new String[]{"二维图/PDF","error","AutoCAD文件对应的Part没有导入","","","项目"+map_2d_all.get(dwg)+"的"+dwg+".dwg"+"没有不能导入， 原因：对应的Part没有导入"});
							}
						}
					}
				}
			}
		}
		
		StringBuffer xmlDescribe = new StringBuffer("<?xml version='1.0' ?><!DOCTYPE NmLoader SYSTEM 'standardX24.dtd'><NmLoader>");
		
		for(String key:allPorjectDwg.keySet()){
            Map<String,File> map_2d=allPorjectDwg.get(key);
            if(map_2d.size()>0){            	
                StringBuffer xml = new StringBuffer("<?xml version='1.0' ?><!DOCTYPE NmLoader SYSTEM 'standardX24.dtd'><NmLoader>");
                String container=key;    //容器
                String doctype="线束AUTOCAD图纸";    //类型
                String docNumber="";    //文档编码 
                String docName="";    //文档名称
                String docdes="";    //描述
                String creator="dms";    //创建者
                String projectName="";    //项目简称
                String projectCode="";    //项目代码
                String outputPhase="发布阶段";    //输出阶段
                String subCategory="线束AUTOCAD图纸";    //文档细类
                String primaryContentPath="";    //主要内容路径
                String attachmentContentPath="";    //附件内容路径
                String projectFolder="";

                for(String dwg:map_2d.keySet()){

                    projectFolder=map_2d_all.get(dwg);
                    String[] info=partToFolder.get(dwg);

                    docNumber=dwg;
                    docName=dwg;
                    primaryContentPath=map_2d.get(dwg).getAbsolutePath();
                    
                    primaryContentPath="d:\\ptc\\Windchill\\loadFiles\\com\\catl\\dms\\"+primaryContentPath.substring(initPath.length());
                    
                    primaryContentPath=primaryContentPath.replaceAll("&","&amp;");
                    //System.out.println("primaryContentPath:"+primaryContentPath);
                    
                    String tempvalue=info[1].replace("（", "(").replace("）", ")");
                    int beginIndex=tempvalue.indexOf("(");
            		int endIndex=tempvalue.lastIndexOf(")");
            		
            		if(beginIndex>-1 && endIndex>-1){            			
            			projectCode=info[1].substring(beginIndex+1,endIndex);            			
            			projectName=info[1].substring(info[1].indexOf("\\")+1,beginIndex);
            		}else{
            			projectCode=info[1].substring(info[1].indexOf("\\")+1);
            		}                    
                    
                    String uploadPath="";
                    if(!container.equals("电芯") && !container.equals("电子电气件") && !container.equals("紧固件") && !container.equals("原材料")){
                    	uploadPath="/Default"+info[1].replace("\\", "/")+"/设计图档/线束AutoCAD图纸";
                    }else{
                    	uploadPath="/Default"+info[1].replace("\\", "/");
                    }
                    
                    //System.out.println("uploadPath:"+uploadPath);

                    xml.append("<csvBeginWTDocument handler='wt.doc.LoadDoc.beginCreateWTDocument' ><csvname>").append(docName).append("</csvname><csvtitle>").append(docName).append("</csvtitle>")
                    .append("<csvnumber>").append(docNumber).append("</csvnumber>")
                    .append("<csvtype>Document</csvtype><csvdescription>").append(docdes).append("</csvdescription><csvdepartment>DESIGN</csvdepartment>")
                    .append("<csvsaveIn>").append(uploadPath).append("</csvsaveIn>")
                    .append("<csvteamTemplate></csvteamTemplate><csvdomain></csvdomain>")
                    .append("<csvlifecycletemplate>LC_doc_Cycle</csvlifecycletemplate><csvlifecyclestate>RELEASED</csvlifecyclestate>")
                    .append("<csvtypedef>").append(Constant.TYPE_NAME.get(doctype.trim())).append("</csvtypedef>")
                    .append("<csvversion></csvversion>")
                    .append("<csviteration></csviteration>")
                    .append("<csvsecurityLabels></csvsecurityLabels></csvBeginWTDocument>");

                    if(!StrUtils.isEmpty(projectName)){
	                    xml.append("<csvIBAValue handler='wt.iba.value.service.LoadValue.createIBAValue' >")
	                    .append("<csvdefinition>projectName</csvdefinition>")
	                    .append("<csvvalue1>").append(projectName).append("</csvvalue1>")
	                    .append("<csvvalue2></csvvalue2><csvdependency_id></csvdependency_id></csvIBAValue>");
                    }
                    
                    xml.append("<csvIBAValue handler='wt.iba.value.service.LoadValue.createIBAValue' >")
                    .append("<csvdefinition>outputPhase</csvdefinition>")
                    .append("<csvvalue1>").append(outputPhase).append("</csvvalue1>")
                    .append("<csvvalue2></csvvalue2><csvdependency_id></csvdependency_id></csvIBAValue>");
                
                    if(!StrUtils.isEmpty(projectCode)){
	                    xml.append("<csvIBAValue handler='wt.iba.value.service.LoadValue.createIBAValue' >")
	                    .append("<csvdefinition>projectCode</csvdefinition>")
	                    .append("<csvvalue1>").append(projectCode).append("</csvvalue1>")
	                    .append("<csvvalue2></csvvalue2><csvdependency_id></csvdependency_id></csvIBAValue>");
	                }
                    
                    xml.append("<csvIBAValue handler='wt.iba.value.service.LoadValue.createIBAValue' >")
                    .append("<csvdefinition>subCategory</csvdefinition>")
                    .append("<csvvalue1>").append(subCategory).append("</csvvalue1>")
                    .append("<csvvalue2></csvvalue2><csvdependency_id></csvdependency_id></csvIBAValue>");
                
                    if(allPorjectPDF.get(dwg)!=null){

                        attachmentContentPath=allPorjectPDF.get(dwg).getAbsolutePath();

                        attachmentContentPath="d:\\ptc\\Windchill\\loadFiles\\com\\catl\\dms\\"+attachmentContentPath.substring(initPath.length());
                        
                        attachmentContentPath=attachmentContentPath.replaceAll("&","&amp;");
                        //System.out.println("attachmentContentPath:"+attachmentContentPath);
                        
                        xml.append("<csvContentFile handler='wt.load.LoadContent.createContentFile' >")
                        .append("<csvuser>").append("</csvuser>")//.append(creator)
                        .append("<csvpath>").append(attachmentContentPath).append("</csvpath>")
                        .append("</csvContentFile>");
                    }

                    xml.append("<csvEndWTDocument handler='wt.doc.LoadDoc.endCreateWTDocument' >")
                    .append("<csvprimarycontenttype>ApplicationData</csvprimarycontenttype>")
                    .append("<csvpath>").append(primaryContentPath).append("</csvpath><csvformat></csvformat>")
                    .append("<csvcontdesc></csvcontdesc><csvparentContainerPath></csvparentContainerPath>")
                    .append("</csvEndWTDocument>");
                    
                    xmlDescribe.append("<csvPartDocDescribes handler='wt.part.LoadPart.createPartDocDescribes' >")
                    .append("<csvdocNumber>").append(dwg).append("</csvdocNumber>")
                    .append("<csvdocVersion></csvdocVersion>")
                    .append("<csvdocIteration></csvdocIteration>")
                    .append("<csvpartNumber>").append(dwg).append("</csvpartNumber>")
                    .append("<csvpartVersion></csvpartVersion>")
                    .append("<csvpartIteration></csvpartIteration>")
                    .append("<csvpartView></csvpartView>")
                    .append("<csvpartVariation1></csvpartVariation1>")
                    .append("<csvpartVariation2></csvpartVariation2>")
                    .append("<csvorganizationName></csvorganizationName>")
                    .append("<csvorganizationID></csvorganizationID>")
                    .append("</csvPartDocDescribes>");

                //System.out.println(dwg+"."+map_2d.get(dwg));
                }

                xml.append("</NmLoader>");
                
                Java2XML.writerByString(xml.toString(),"E:\\ATL\\project\\PLM\\loadtest\\destotal\\"+projectFolder.replaceAll(" ", "_")+"_"+container+"_CADDocument.xml");
               
                String proOrLib="wt.inf.library.WTLibrary=";

                if(!container.equals("电芯") && !container.equals("电子电气件") && !container.equals("紧固件") && !container.equals("原材料")&& !container.equals("设备开发标准件库")){
                	proOrLib="wt.pdmlink.PDMLinkProduct=";
                }                
                impStatement.add("windchill wt.load.LoadFromFile -d d:\\ptc\\Windchill\\loadFiles\\com\\catl\\dms\\"+projectFolder.replaceAll(" ", "_")+"_"+container+"_CADDocument.xml -u dms -p dms -CONT_PATH \\\"/wt.inf.container.OrgContainer=CATL/"+proOrLib+container+"\\\"");
            }
        }
		
		xmlDescribe.append("</NmLoader>");		
		Java2XML.writerByString(xmlDescribe.toString(),"E:\\ATL\\project\\PLM\\loadtest\\destotal\\AllProject_CADDocumentPartDescribe.xml");
		impStatement.add("windchill wt.load.LoadFromFile -d d:\\ptc\\Windchill\\loadFiles\\com\\catl\\dms\\AllProject_CADDocumentPartDescribe.xml -u dms -p dms -CONT_PATH \\\"/wt.inf.container.OrgContainer=CATL\\\"");
		//Thread.sleep(10000);
		return impStatement;
	}	
	
	/**
	 * 待导入系统Part附件
	 * @return
	 */	
	public static Map<String,String> loadPartAttachment(){
		String initPath=rootPath+"destotal\\";		
		Map<String,String> partAttachment=getAllAttachment(initPath);		
		Map<String,File> allPorjectPDF= getAllPorjectPDF();		
		for(String key:allPorjectPDF.keySet()){
			partAttachment.remove(key);
		}
		return partAttachment;
	}
	
	/**
	 * 获取所有项目线束PDF
	 * @return
	 */
	public static Map<String,File> getAllPorjectPDF(){		
		Map<String,String> map_2d_all=new HashMap<String,String>();
		Map<String,String> map_pdf_all=new HashMap<String,String>();

		Map<String,File> allPorjectPDF=new HashMap<String, File>();
		
		File root = new File(rootPath+"destotal\\");
		if(root.isDirectory()){
			File[] files = root.listFiles();
			for(File file : files){
				if(file.isDirectory()){					
					File autoCADFile=new File(file.getAbsolutePath()+"\\");
					Map<String,File> map_2d=new HashMap<String,File>();
					Map<String,File> map_pdf=new HashMap<String,File>();
					
					String proName=file.getName();
					if(autoCADFile.exists()){
						getAllFile1(map_2d_all,map_2d,map_pdf_all,map_pdf,autoCADFile,proName);
					}					
					//System.out.println("map_2d.size:"+map_2d.size());
					//System.out.println("map_pdf.size"+map_pdf.size());
					
					if(map_2d.size()>0){
						for(String dwg:map_2d.keySet()){							
							if(map_pdf.get(dwg)!=null){								
								allPorjectPDF.put(dwg, map_pdf.get(dwg));								
							}else{
								ObjectValidInfo.add(new String[]{"二维图/PDF","warn","AutoCAD文件不完整","","","项目"+map_2d_all.get(dwg)+"的"+dwg+".dwg"+" 对应的pdf文件"+dwg+".pdf"+"未整理"});
							}							
						}
					}
				}
			}
		}
		
		return allPorjectPDF;
	}
	
	
	/**
	 * 获取目录下所有的PDF
	 * @param filePath
	 * @return
	 */
	public static Map<String,String> getAllAttachment(String filePath){
		Map<String,String> attachments=new HashMap<String, String>();	
		getAllFile(attachments,new File(filePath));
		return attachments;
	}
	
	private static void getAllFile(Map<String,String> attachments,File root){
		String initPath=rootPath+"destotal\\";
		if(root.isDirectory()){
			File[] files = root.listFiles();
			for(File file : files){
				if(!file.isDirectory()){
					if(file.getName().indexOf(".pdf")>0){
						//System.out.println(".....pdf");
						String pn=file.getName().substring(0,file.getName().indexOf("."));
						if(attachments.containsKey(pn)){
							continue;
						}else{							
							String absoutePath=file.getAbsolutePath();
							absoutePath=absoutePath.substring(initPath.length());
							//System.out.println("........absoutePath:"+absoutePath);
							attachments.put(pn, "d:\\ptc\\Windchill\\loadFiles\\com\\catl\\dms\\"+absoutePath);
							System.out.println("........"+"d:\\ptc\\Windchill\\loadFiles\\com\\catl\\dms\\"+absoutePath);
						}
					}
				}else{
					getAllFile(attachments,file);
				}
			}
		}
	}
	
	/**
	 * 更新SAPBOM编码
	 * @param bomFiles
	 * @param numMap
	 */
	public static void updateSAPBOM(List<File> bomFiles,Map<String,String> numMap,Map<String,String> numNew){
		try {
			for(File exlFile : bomFiles){
				String filePath=exlFile.getAbsolutePath();
				System.out.println("filePath:"+filePath);
				FileInputStream fis=new FileInputStream(exlFile);
				// 定义Workbook对象
				Workbook workbook = null;
				
				String filename=exlFile.getName();
				String fileType = filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length()).toLowerCase();
				// 创建工作簿实例
				if (fileType.equals("xls")) {
					workbook = new HSSFWorkbook(fis);
			    } else if (fileType.equals("xlsx")) {
			    	workbook = new XSSFWorkbook(fis);
			    } else if (fileType.equals("csv")) {
			    	workbook = new XSSFWorkbook(fis);
			    } else {
			        System.err.println("您的文档格式不正确！");
			    }
				
				Sheet sheet=workbook.getSheetAt(0);
				int rowcount=sheet.getLastRowNum();
				//System.out.println(rowcount);
				
				for(int i=1;i<=rowcount;i++){
					Row r=sheet.getRow(i);
					//int cellcount=r.getLastCellNum();
					//System.out.println("cellcount:"+cellcount);
					int partNumIndex=0;	//上层物料编码列索引
					int childNumIndex=3; //物料编码列索引
					int unitIndex=4;	//单位列索引
					int quantityIndex=5;	//单位用量列索引
					int substituteIndex=7;	//替代件编码列索引
					
					//替换父件编码
			        Cell cell1=r.getCell(partNumIndex);
			        String str1=cell1.getStringCellValue().trim();
			        
			        System.out.println("before str1:"+str1);
			        if(StrUtils.isEmpty(str1)){
			        	continue;
			        }
			        System.out.println("after str1:"+str1);
			        
			        if(!numNew.containsKey(str1)){
				        if(numMap.containsKey(str1)){
				        	cell1.setCellValue(numMap.get(str1));
				        }else{
				        	ObjectValidInfo.add(new String[]{"BOM","error","旧物料编码没有对应的新编码","","","文件："+filename+" 上层物料编码："+str1+"没有对应的新编码，第"+(i+1)+"行"});
				        	//System.err.println("文件："+fileType+" 上层物料编码："+str1+"没有对应的新编码");
				        }
			        }
			        
			        //替换子件编码
			        Cell cell2=r.getCell(childNumIndex);
			        String str2=cell2.getStringCellValue().trim();
			        
			        if(!numNew.containsKey(str2)){
				        if(numMap.containsKey(str2)){
				        	cell2.setCellValue(numMap.get(str2));
				        }else{
				        	ObjectValidInfo.add(new String[]{"BOM","error","旧物料编码没有对应的新编码","","","文件："+filename+" 物料编码："+str2+"没有对应的新编码，第"+(i+1)+"行"});
				        	//System.err.println("文件："+fileType+" 物料编码："+str2+"没有对应的新编号");
				        }
			        }

			        Cell cell3=r.getCell(unitIndex);
			        String str3=cell3.getStringCellValue().trim().toLowerCase();
			        
			        if(str3.equals("pcs")){
			        	str3="ea";
			        }
			        
			        if(!str3.equals("mm")&&!str3.equals("kg")&&!UNIT_ALL.contains(str3+"|")){
			        	
			        	ObjectValidInfo.add(new String[]{"BOM","error","SAP与PLM的BOM单位不一致","","","文件："+filename+" 包含单位"+str3+", PLM中的单位m,g,ml,set,dm2,pcs，第"+(i+1)+"行"});
			        	//System.out.println("文件："+filename+" 包含单位"+str3+", PLM中的单位m,g,ml,set,dm2,pcs");
			        }
			        
			        Cell cell4=r.getCell(quantityIndex);
			        double str4=cell4.getNumericCellValue();
			        //System.out.println("str4:"+str4);
			        
			        if(str3.equals("kg")){
			        	cell3.setCellValue("G");
			        	cell4.setCellType(Cell.CELL_TYPE_NUMERIC);
			        	cell4.setCellValue(str4*1000);
			        	
			        }
			        
			        if(str3.equals("mm")){
			        	cell3.setCellValue("M");
			        	cell4.setCellType(Cell.CELL_TYPE_NUMERIC);
			        	cell4.setCellValue(str4/1000);
			        		        	
			        	if(str4<1){
			        		ObjectValidInfo.add(new String[]{"BOM","error","单位用量不符合要求","","","文件："+filename+"第"+(i+1)+"行, 原因：小数点后最多保留3位"});
			        	}
			        	
			        }
			        
			        //替换替代件编码
			        Cell cell5=r.getCell(substituteIndex);
			        String str5=cell5.getStringCellValue();
			        if(!StrUtils.isEmpty(str5)){
			        	str5=str5.trim();
			        	
			        	if(!numNew.containsKey(str5)){
					        if(numMap.containsKey(str5)){
					        	cell5.setCellValue(numMap.get(str5));
					        }else{
					        	ObjectValidInfo.add(new String[]{"BOM","error","旧物料编码没有对应的新编码","","","文件："+filename+" 替代件编码："+str2+"没有对应的新编码，第"+(i+1)+"行"});
					        	//System.err.println("文件："+fileType+" 物料编码："+str2+"没有对应的新编号");
					        }
				        }
			        }
				}
				
				fis.close();//关闭文件输入流
				 
		        FileOutputStream fos=new FileOutputStream(exlFile);
		        workbook.write(fos);
		        fos.close();//关闭文件输出流
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
}
