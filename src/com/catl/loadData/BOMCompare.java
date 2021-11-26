package com.catl.loadData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.catl.loadData.util.ExcelReader;
import com.catl.loadData.util.ExcelWriter;

public class BOMCompare {
	
	private static String rootPath="E:\\ATL\\project\\PLM\\loadtest\\";
	
	private static List<String[]> compareSAPBOM = new ArrayList<String[]>();

	public static void main(String[] args) throws IOException {
		bomCompare();
	}
	
	public static void bomCompare() throws IOException{
		Map<String, File> bomSAP=new HashMap<String, File>();
		Map<String, File> bomOld=new HashMap<String, File>();
		
		List<String> bomError = new ArrayList<String>();
		List<String> docError = new ArrayList<String>();
		Map<String,List<File>> cadFiles=new HashMap<String, List<File>>(); //cad文件
		List<File> bomFiles = new ArrayList<File>();//bom文件
		List<File> documentFiles = new ArrayList<File>();//文档文件
		
		MainClass.loadAllRelationFile(bomSAP,bomOld,bomError,docError,cadFiles,bomFiles,documentFiles);
				
		compareCadToSAP(cadFiles,bomSAP);
		
		outputSAPBOMCompare();
	}
	
	
	/**
	 * 比较CAD与SAP内容差异
	 * @param cadfiles
	 * @param sapfiles
	 * @throws IOException
	 */
	public static void compareCadToSAP(Map<String,List<File>> cadfiles,Map<String, File> sapfiles) throws IOException{
		
		for(String key:sapfiles.keySet()){
			File fileSAP=sapfiles.get(key);
			String filePath=fileSAP.getAbsolutePath().substring(0,fileSAP.getAbsolutePath().lastIndexOf("\\"));
			filePath=filePath.substring(0,filePath.lastIndexOf("\\"));
			filePath=filePath.substring(filePath.lastIndexOf("\\")+1);
			System.out.println("filePath:"+filePath);
			
			Map<String,String> sapData=getSAPBOMData(fileSAP);
			
			List<File> cadfileList=cadfiles.get(key);
			if(cadfileList!=null){
			Map<String,String> cadData=getCadData(cadfileList);
			for(String sapkey:sapData.keySet()){
				String sapBOMInfo=sapData.get(sapkey);
				//String cadBOMInfo=cadData.get(sapkey);
				List<String> moreFile=getCadBOMInfo(cadfileList,cadData,sapkey);
				
				if(moreFile.size()==0){
					compareSAPBOM.add(new String[]{"CADBOM",filePath,"少BOM",sapkey,"","见"+fileSAP.getName()+"的产品BOM_IT","通过列(父件编码)进行筛选查找"});
				}else{
					for(int uu=0;uu<moreFile.size();uu++){						
						String cadBOMInfo=moreFile.get(uu);
						
						//System.out.println("sapkey:"+sapkey+",sapBOMInfo:"+sapBOMInfo);
						String[] sapArray=sapBOMInfo.split("\\|");
						//System.out.println(sapArray.length);
						String[] temp1=cadBOMInfo.split("&&&");
						String filename=temp1[0];
						String[] cadArray=temp1[1].split("\\|");
						
						Map<String,String> sapChild=new HashMap<String, String>();
						Map<String,String> cadChild=new HashMap<String, String>();
						
						for(int i=0;i<sapArray.length;i++){
							sapChild.put(sapArray[i].substring(sapArray[i].lastIndexOf("--")+2),sapArray[i]);
							//System.out.println("XXX:"+sapArray[i]);
						}
						
						for(int y=0;y<cadArray.length;y++){
							
							System.out.println(sapkey+":cadArray[y]:"+cadArray[y]);
							cadChild.put(cadArray[y].substring(cadArray[y].lastIndexOf("--")+2), cadArray[y]);
							//System.out.println("YYY:"+cadArray[y]);
						}
										
						for(String key1:sapChild.keySet()){
							String[] temp2=sapChild.get(key1).split("--");
							if(cadChild.get(key1)==null){	
								if(key1.startsWith("1")){
									compareSAPBOM.add(new String[]{"CADBOM",filePath,"少零件(原材料)",sapkey,key1,"见"+filename+".txt 在文件内搜索[物料清单： "+sapkey+"]进行查找"+" / "+fileSAP.getName()+"第产品BOM_IT中的第"+(Integer.valueOf(temp2[0])+1)+"行","数量："+temp2[1]});
								}else{
									compareSAPBOM.add(new String[]{"CADBOM",filePath,"少零件",sapkey,key1,"见"+filename+".txt 在文件内搜索[物料清单： "+sapkey+"]进行查找"+" / "+fileSAP.getName()+"第产品BOM_IT中的第"+(Integer.valueOf(temp2[0])+1)+"行","数量："+temp2[1]});
								}
								//bomerror.add(filePath+"目录下SAPBOM与CAD比较，CAD的"+sapkey+"BOM少了"+key1+"零件,数量"+temp2[1]+", "+fileSAP.getName()+"见第产品BOM_IT中的第"+(Integer.valueOf(temp2[0])+1)+"行");
							}else{
								String[] temp3=cadChild.get(key1).split("--");
								if(!temp2[1].trim().equals(temp3[0].trim())){
									String value=temp2[1].equals("")?"空值":temp2[1];
									
									compareSAPBOM.add(new String[]{"CADBOM",filePath,"数量不同",sapkey,key1,"分别是"+value+"("+fileSAP.getName()+"见第产品BOM_IT中的第"+(Integer.valueOf(temp2[0])+1)+"行"+")、"+temp3[0]+"("+filename+" 在文件内搜索[物料清单： "+sapkey+"]进行查找)",""});
									//bomerror.add(filePath+"目录下SAPBOM与CAD比较，SAPBOM与CAD的"+sapkey+"BOM的"+key1+"零件的数量不一致，分别是"+value+"("+fileSAP.getName()+"见第产品BOM_IT中的第"+(Integer.valueOf(temp2[0])+1)+"行"+")、"+temp3[0]+"("+filename+")");
								}
								cadChild.remove(key1);
							}
						}
						
						for(String key2:cadChild.keySet()){
							String[] temp4=cadChild.get(key2).split("--");
							
							if(temp4[1].split("-").length==2){
								compareSAPBOM.add(new String[]{"CADBOM",filePath,"多零件",sapkey,key2,"见"+filename+".txt 在文件内搜索[物料清单： "+sapkey+"]进行查找","数量："+temp4[0]});
							}else{
								compareSAPBOM.add(new String[]{"CADBOM",filePath,"多零件(中间件)",sapkey,key2,"见"+filename+".txt 在文件内搜索[物料清单： "+sapkey+"]进行查找","数量："+temp4[0]});
							}
							
							//bomerror.add(filePath+"目录下SAPBOM与CAD比较，CAD的"+sapkey+"BOM多了"+key2+"零件,数量"+temp4[0]+", 见"+filename);
						}
						
						
	//					System.out.println(sapkey+":"+cadData.get(sapkey));//cadData.get(sapkey);
	//					System.out.println("输出SAPBOM的信息");
	//					System.out.println(sapkey+":"+sapData.get(sapkey));
						cadData.remove(filename+"&&&"+sapkey);
						//removeCadData(cadfileList,cadData,sapkey);
						//cadData.remove(sapkey);						
					}
				}
				
			}
			
			for(String cadkey:cadData.keySet()){
				String[] temp1=cadData.get(cadkey).split("&&&");
				String filename=temp1[0];
				cadkey=cadkey.split("&&&")[1];
				
				if(volidMiddlePN(temp1[1])){
					compareSAPBOM.add(new String[]{"CADBOM",filePath,"多BOM",cadkey,"","见 "+filename+".txt 在文件内搜索[物料清单： "+cadkey+"]进行查找",""});
					//bomerror.add(filePath+"目录下SAPBOM与CAD比较，CAD多了编码为"+cadkey+"的BOM,BOM的内容见  "+filename+".txt ："+bomFormat("cad",cadkey,cadData.get(cadkey)));
				}else{
					compareSAPBOM.add(new String[]{"CADBOM",filePath,"多BOM(中间件)",cadkey,"","见 "+filename+".txt 在文件内搜索[物料清单： "+cadkey+"]进行查找",""});
				}
			}
		  }
		}	
	}
	
	public static Map<String,String> getSAPBOMData(File fileSAP) throws IOException{
		//System.out.println(fileSAP.getAbsolutePath());
		
		Map<String,String> moreBOM=new HashMap<String,String>();
		
		ExcelReader readExcel = new ExcelReader(fileSAP);
		readExcel.open();
		readExcel.setSheetNum(0); // 设置读取索引为0的工作表
		// 总行数
		int count = readExcel.getRowCount();
		//System.out.println("sap总行数："+count);
		//String[] rows=null;
		for (int i = 1; i <= count; i++) {
			String[] rows = readExcel.readExcelLine(i);
			
			String bomNumber=rows[0];
			String childNumber=rows[3];
			String childCount=rows[5];
			
			if(StrUtils.isEmpty(bomNumber) ||StrUtils.isEmpty(childNumber)||StrUtils.isEmpty(childCount) ||bomNumber.trim().equals("待定") || childNumber.trim().equals("待定") || 
					bomNumber.trim().equals("#N/A")||childNumber.trim().equals("#N/A")){
				System.err.println("----------------数据不完整");
			}else{
				if(moreBOM.get(bomNumber) != null){ 
					moreBOM.put(bomNumber, moreBOM.get(bomNumber)+"|"+i+"--"+childCount+"--"+childNumber);
				}else{
					moreBOM.put(bomNumber, i+"--"+childCount+"--"+childNumber);
				}
			}
		}
		
/*		for(String key:moreBOM.keySet()){
			System.err.println(key+"."+moreBOM.get(key));
		}*/
		return moreBOM;
	}
	
	private static Map<String,String> getCadData(List<File> cadFiles) {
		System.out.println("----------------------start cad--------------------");
		Map<String,String> morebom = new HashMap<String,String>();
		
		for(File file : cadFiles){
			String filename=file.getName().substring(0,file.getName().lastIndexOf(".txt"));			
			try {								
				InputStreamReader read = new InputStreamReader(new FileInputStream(file),"gb2312");
				BufferedReader reader = new BufferedReader(read);
				String text = null;
				String bomNumberPrefix="= 物料清单： ";
				String bomNumber=null;
				String childCount=null;
				String childNumber=null;
				while((text=reader.readLine())!=null){
					
					if(text.startsWith(bomNumberPrefix)){
						bomNumber=text.substring(bomNumberPrefix.length(), bomNumberPrefix.length()+12);
						//System.out.println(bomNumber);
					}else if(text.matches("\\| \\d+\\s+\\|.+")){
						//System.out.println(text);
						childCount = text.substring(0,12).replace("|", "").replace(" ", "");
						childNumber= text.substring(13,32).trim();
						//System.out.println(childCount+"--"+childNumber);
						
						if(morebom.get(filename+"&&&"+bomNumber) != null){
							morebom.put(filename+"&&&"+bomNumber, morebom.get(filename+"&&&"+bomNumber)+"|"+childCount+"--"+childNumber);
						}else{
							morebom.put(filename+"&&&"+bomNumber, filename+"&&&"+childCount+"--"+childNumber);
						}
					}else if(text.startsWith("= 摘要说明：")){						
						System.out.println("。。。。。。摘要说明");
						break;
					}
				}
								
				/*System.out.println("filename:"+filename);
				for (String key : morebom.keySet()) {
				   System.out.println("key= "+ key + " and value= " + morebom.get(key));
				}*/
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
/*		for (String morekey : morebom.keySet()) {
			Map<String,String> oneMap=morebom.get(morekey);
			System.out.println("文件："+morekey);
			for (String onekey : oneMap.keySet()) {
			   System.out.println("key= "+ onekey + " and value= " + oneMap.get(onekey));
			}
			System.out.println("-------------------------------------");
		}*/
		
		System.out.println("----------------------end cad--------------------");
		return morebom;
	}
	
	public static List<String> getCadBOMInfo(List<File> files,Map<String,String> cadData,String key){
		
		List<String> moreFile=new ArrayList<String>();
		for(int i=0;i<files.size();i++){
			String filename=files.get(i).getName();
			filename=filename.substring(0,filename.lastIndexOf("."));
			//System.out.println("filename:"+filename);
			if(cadData.get(filename+"&&&"+key)!=null){
				//System.out.println("xxx.value:"+cadData.get(filename+"&&&"+key));
				moreFile.add(cadData.get(filename+"&&&"+key));
				//return cadData.get(filename+"&&&"+key);
			}
		}
		return moreFile;
	}
	
	/**
	 * 验证BOM子件全是中间件
	 * @param value
	 * @return
	 */
	public static boolean volidMiddlePN(String value){
		boolean isBOM=false;
		String[] cadArray001=value.split("\\|");
		for(int i=0;i<cadArray001.length;i++){
			String[] temp001=cadArray001[i].split("--");
			if(temp001[1].split("-").length>=2){
				isBOM=true;
			}
		}
		return isBOM;
	}
	
	/**
	 * 比较SAP与Old内容差异
	 * @param bomSAPFile
	 * @param bomOldFile
	 */
	public static void compareSAPToOld(Map<String,File> bomSAPFile,Map<String,File> bomOldFile){
		System.out.println("-----------SAP与Old内容比较start-------------");
		Map<Integer,String> colum=new HashMap<Integer, String>();
		colum.put(0, "上层物料编码");
		colum.put(1, "父件版本");
		colum.put(2, "位号");
		colum.put(3, "物料编码");		
		colum.put(4, "单位");
		colum.put(5, "单位用量");
		colum.put(6, "物料版本");
		colum.put(7, "替代件编码");
		colum.put(8, "替代组");
		colum.put(9, "父件数量");
		colum.put(10, "替代件数量");
		try{
			for(String fileName:bomSAPFile.keySet()){
				File fileSAP=bomSAPFile.get(fileName);
				File fileOld=bomOldFile.get(fileName);
				
				String filePath=fileSAP.getAbsolutePath().substring(0,fileSAP.getAbsolutePath().lastIndexOf("\\"));
				filePath=filePath.substring(0,filePath.lastIndexOf("\\"));
				filePath=filePath.substring(filePath.lastIndexOf("\\")+1);
				if(fileSAP!=null && fileOld!=null){
					
					Map<Integer,List<String>> bOMSAP=getBomBySAP(fileSAP);
					
					Map<Integer,List<String>> bOMOld=getBomBySAP(fileOld);
					
					Map<String,String> newBOM= getSAPBOMData(fileSAP);
					
					Map<String,String> oldBOM= getSAPBOMData(fileOld);
					
					for(String key:newBOM.keySet()){
						
						String temp=oldBOM.get(key);
						if(temp==null){
							compareSAPBOM.add(new String[]{"旧版本SAPBOM",filePath,"少BOM",key,"","见 "+fileSAP.getName()+"第产品BOM_IT","通过列(父件编码)进行筛选查找"});
							//bomwarn.add(filePath+"目录下SAPBOM最新版本("+fileSAP.getName()+")与旧版本("+fileOld.getName()+")比较，"+"旧版本少了编号为"+key+"的BOM，BOM的内容见 "+fileSAP.getName()+"第产品BOM_IT ："+bomfomateOP(key,newBOM.get(key)));
						}else{
							String[] sapArray=newBOM.get(key).split("\\|");
							String[] oldArray=temp.split("\\|");
							Map<String,String> sapChild=new HashMap<String, String>();
							Map<String,String> oldChild=new HashMap<String, String>();
							for(int i=0;i<sapArray.length;i++){								
								sapChild.put(sapArray[i].substring(sapArray[i].lastIndexOf("--")+2),sapArray[i]);
							}
							
							for(int j=0;j<oldArray.length;j++){
								oldChild.put(oldArray[j].substring(oldArray[j].lastIndexOf("--")+2),oldArray[j]);
							}
							
							for(String sapchild:sapChild.keySet()){
								
								String oldchildValue=oldChild.get(sapchild);
								//System.out.println("oldchildValue:"+oldchildValue);
								String sapchildValue=sapChild.get(sapchild);
								if(oldchildValue==null){
									String[] temp2=sapchildValue.split("--");
									compareSAPBOM.add(new String[]{"旧版本SAPBOM",filePath,"少零件",key,temp2[2],"见 "+fileSAP.getName()+"第产品BOM_IT的"+(Integer.valueOf(temp2[0])+1)+"行",""});
									//bomwarn.add(filePath+"目录下SAPBOM最新版本("+fileSAP.getName()+")与旧版本("+fileOld.getName()+")比较，"+"旧版本编码为"+key+"的BOM少了"+temp2[2]+"零件,内容见 "+fileSAP.getName()+"第产品BOM_IT的"+(Integer.valueOf(temp2[0])+1)+"行");
								}else{
									System.out.println("sapchildValue:"+sapchildValue);
									String[] rows=sapchildValue.split("--");
									String[] oldrows=oldchildValue.split("--");
									System.out.println("rows"+Integer.valueOf(rows[0]));
									System.out.println("oldrows:"+Integer.valueOf(oldrows[0]));
									List<String> rowSAP=bOMSAP.get(Integer.valueOf(rows[0]));
									List<String> rowOld=bOMOld.get(Integer.valueOf(oldrows[0]));
									for(int u=0;u<rowSAP.size();u++){										
										if(u>rowOld.size()-1){
											compareSAPBOM.add(new String[]{"旧版本SAPBOM",filePath,colum.get(u)+"不同",key,sapchild,"见["+fileSAP.getName()+"的第"+(Integer.valueOf(rows[0])+1)+"行与"+fileOld.getName()+"的第"+(Integer.valueOf(oldrows[0])+1)+"行]",""});
											//bomwarn.add(filePath+"目录下SAPBOM最新版本("+fileSAP.getName()+")与旧版本("+fileOld.getName()+")比较，编码为"+key+"的BOM的"+sapchild+"零件["+colum.get(u)+"]不一致  查看位置["+fileSAP.getName()+"的第"+(Integer.valueOf(rows[0])+1)+"行与"+fileOld.getName()+"的第"+(Integer.valueOf(oldrows[0])+1)+"行]  ");
										}else{
											if(!rowSAP.get(u).equals(rowOld.get(u))){
												compareSAPBOM.add(new String[]{"旧版本SAPBOM",filePath,colum.get(u)+"不同",key,sapchild,"见["+fileSAP.getName()+"的第"+(Integer.valueOf(rows[0])+1)+"行与"+fileOld.getName()+"的第"+(Integer.valueOf(oldrows[0])+1)+"行]",""});
												//bomwarn.add(filePath+"目录下SAPBOM最新版本("+fileSAP.getName()+")与旧版本("+fileOld.getName()+")比较，编码为"+key+"的BOM的"+sapchild+"零件["+colum.get(u)+"]不一致  查看位置["+fileSAP.getName()+"的第"+(Integer.valueOf(rows[0])+1)+"行与"+fileOld.getName()+"的第"+(Integer.valueOf(oldrows[0])+1)+"行]  ");
											}
										}
									}
									oldChild.remove(sapchild);
								}								
							}
							
							for(String oldchild:oldChild.keySet()){
								String[] temp3=oldChild.get(oldchild).split("--");
								compareSAPBOM.add(new String[]{"旧版本SAPBOM",filePath,"多零件",key,temp3[2],"见 "+fileOld.getName()+"第产品BOM_IT的"+(Integer.valueOf(temp3[0])+1)+"行",""});
								//bomwarn.add(filePath+"目录下SAPBOM最新版本("+fileSAP.getName()+")与旧版本("+fileOld.getName()+")比较，"+"旧版本编码为"+key+"的BOM多了"+temp3[2]+"零件,内容见 "+fileOld.getName()+"第产品BOM_IT的"+(Integer.valueOf(temp3[0])+1)+"行");
							}
							
							
							oldBOM.remove(key);
						}
						//System.out.println(key+":"+newBOM.get(key));
					}
					
					for(String key2:oldBOM.keySet()){
						compareSAPBOM.add(new String[]{"旧版本SAPBOM",filePath,"多BOM",key2,"","见 "+fileOld.getName()+"第产品BOM_IT","通过列(父件编码)进行筛选查找"});
						//bomwarn.add(filePath+"目录下SAPBOM最新版本("+fileSAP.getName()+")与旧版本("+fileOld.getName()+")比较，"+"旧版本少了编号为"+key2+"的BOM，BOM的内容见 "+fileOld.getName()+"第产品BOM_IT ："+bomfomateOP(key2,oldBOM.get(key2)));
					}

				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		System.out.println("-----------SAP与Old内容比较end-------------");
	}
	
	public static Map<Integer,List<String>>  getBomBySAP(File fileSAP) throws IOException{
		
		Map<Integer,List<String>> dataBOM=new HashMap<Integer,List<String>>();
		
		ExcelReader readExcel = new ExcelReader(fileSAP);
		readExcel.open();
		readExcel.setSheetNum(0); // 设置读取索引为0的工作表
		// 总行数
		int count = readExcel.getRowCount();
		//System.out.println("sap总行数："+count);
		String[] rows=null;
		for (int i = 1; i <= count; i++) {
			List<String> rowdata=new ArrayList<String>();
			rows = readExcel.readExcelLine(i);
			for(int j=0;j<rows.length;j++){
				rowdata.add(rows[j]);
			}
			dataBOM.put(i, rowdata);
		}
		
		return dataBOM;
	}
	
	public static void outputSAPBOMCompare(){
		ExcelWriter writer = new ExcelWriter();
        try {
			boolean flag = writer.exportExcelList(rootPath+"destotal\\SAPBOM比较.xlsx","SAPBOM比较", new String[]{"比较类型","目录","比较结果","父件","子件","查找位置","备注"}, compareSAPBOM);
			System.out.println("SAPBOM比较.xlsx flag="+flag);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void removeCadData(List<File> files,Map<String,String> cadData,String key){
		for(int i=0;i<files.size();i++){
			String filename=files.get(i).getName();
			filename=filename.substring(0,filename.lastIndexOf("."));
			//System.out.println("filename:"+filename);
			if(cadData.get(filename+"&&&"+key)!=null){
				//System.out.println("xxx.value:"+cadData.get(filename+"&&&"+key));
				cadData.remove(filename+"&&&"+key);
				return;
			}
		}
	}

}
