package com.catl.common.toolbox.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.drools.core.util.StringUtils;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.httpgw.GatewayAuthenticator;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.query.QuerySpec;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.wip.WorkInProgressHelper;

import com.catl.common.util.PartUtil;
import com.catl.loadData.Java2XML;
import com.catl.loadData.StrUtils;
import com.catl.loadData.util.ExcelReader;
import com.catl.loadData.util.ExcelWriter;

public class LoadDataSheetFromFile implements RemoteAccess{
   
	private static String dataSheetModle = "电子电气件历史数据整理.xlsx";
	
	private static boolean checkflg = true;		// 判断导入datasheet生成xml文件之前，是否报错，如果有报错不生成Command.bat文件
	
	private static String homePath = "";
	
	static {
		try {
			WTProperties wtproperties = WTProperties.getLocalProperties();
			homePath = wtproperties.getProperty("wt.home");
		} catch (Throwable t) {
			throw new ExceptionInInitializerError(t);
		}
	}
	
	private static String rootPath = homePath + "/loadFiles/com/catl/dms/";
	
	private static String logPath = homePath + "/logs/";
	
	private static String username = "";
	
	private static String pwd = "";
	
	private static List<String> totallist;
	
	private static List<String[]> logs;
	
    public static void main(String[] args) {

        RemoteMethodServer rms = RemoteMethodServer.getDefault();
        GatewayAuthenticator auth = new GatewayAuthenticator();
        auth.setRemoteUser("wcadmin");
        rms.setAuthenticator(auth);

        if (args == null || args.length < 2){
        	
        	System.out.printf("请输入正确的用户名、密码！");
        } else {
        	
        	username = args[0];
        	pwd = args[1];
        	
        	invokeRemoteLoad(username, pwd);
        }
    }

    public static void invokeRemoteLoad(String username, String pwd){
        String method = "doLoad";
        String CLASSNAME = LoadDataSheetFromFile.class.getName();
        try {
        	Class[] types = {String.class, String.class};
            Object[] values={username, pwd};
            
            RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void doLoad(String user, String password){
    	
    	username = user;
    	pwd = password;
		FileWriter file = null;
		checkflg = true;
		
		logs = new ArrayList<String[]>();
		try {
			
			totallist = new ArrayList<String>();
			
			Vector<WTPartMaster> wtpartmasterlist = getPart();
			
			for (WTPartMaster master : wtpartmasterlist){
				totallist.add(master.getNumber());
			}
			
//			File upFile = new File(rootPath+"datasheet/测试系统PN号.xlsx");
//			if (upFile != null && upFile.exists()){
//				
//				ExcelReader erp = new ExcelReader(upFile);
//				erp.open();
//		        erp.setSheetNum(0);
//		        int count = erp.getRowCount();
//		        
//		        for(int i=1; i<=count; i++){
//		            String rows[] = erp.readExcelLine(i);
//		            String partNumber = rows[0];
//		            totallist.add(partNumber);
//		        }
//			}
			
			Format format = new SimpleDateFormat("yyyyMMddHHmmss");
			String nowTime = format.format(new Date());
			file = new FileWriter(logPath + "load_datasheet_" + nowTime + ".log");
			BufferedWriter writer = new BufferedWriter(file);
			List<File> documentFiles = new ArrayList<File>();//文档文件
			
			writer.write("loadAllRelationFile方法开始");
			writer.write("\n");
			
			checkflg = loadAllRelationFile(writer, documentFiles);
			
			writer.write("loadAllRelationFile方法结束,flg="+checkflg);
			writer.write("\n");
			
			if (documentFiles.size() > 0){
				
				writer.write("loadDoc方法开始");
				writer.write("\n");
				
				loadDoc(documentFiles,writer);
				
				writer.write("loadDoc方法结束");
				writer.write("\n");
			} else {
				
				writer.write("系统没有取到一个"+dataSheetModle+"文件！");
				writer.write("\n");
			}
			
			if (!checkflg){
				writer.write("系统没有生成Command.bat和xml文件！");
				writer.write("\n");
			}
			
			ExcelWriter writer1 = new ExcelWriter();
			boolean flag = writer1.exportExcelList(logPath+"导入datasheet错误信息.xlsx","导入datasheet错误信息", new String[]{"人员","模块","行数","错误信息","错误类型","文件名"}, logs);
			System.out.println("导入datasheet错误信息.xlsx flag="+flag);
			
			writer.flush();
            writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }
    
    public static boolean loadAllRelationFile(BufferedWriter writer, List<File> documentFiles) {
    	
    	boolean flg = true;
    	
		File root = new File(rootPath+"datasheet/");
		
		try {
			if(root.isDirectory()){
				File[] subFiles = root.listFiles();
				
				if (subFiles == null || subFiles.length == 0){
					
					writer.write(rootPath + "/datasheet文件夹下没有找到子文件夹！");
					writer.write("\n");
				}
				
				for(File subFile : subFiles){
					String subFileName = subFile.getName();
					if(subFile.isDirectory() && "30分立器件".equals(subFileName)){
						File[] childs = subFile.listFiles();
						boolean isFind=false;
						for(File child : childs){
							if(dataSheetModle.equals(child.getName())){
								
								documentFiles.add(child);
								isFind=true;
								break;
							}
						}
						if(!isFind){
							
							flg = false;
							writer.write(subFileName + "文件夹下没有找到：" + dataSheetModle + "文件！");
							writer.write("\n");
						}
					} else if(subFile.isDirectory() && "31IC".equals(subFileName)){
						File[] childs = subFile.listFiles();
						boolean isFind=false;
						for(File child : childs){
							if(dataSheetModle.equals(child.getName())){
								
								documentFiles.add(child);
								isFind=true;
								break;
							}
						}
						if(!isFind){
							
							flg = false;
							writer.write(subFileName + "文件夹下没有找到：" + dataSheetModle + "文件！");
							writer.write("\n");
						}
					} else if(subFile.isDirectory() && "32保护器件".equals(subFileName)){
						File[] childs = subFile.listFiles();
						boolean isFind=false;
						for(File child : childs){
							if(dataSheetModle.equals(child.getName())){
								
								documentFiles.add(child);
								isFind=true;
								break;
							}
						}
						if(!isFind){
							
							flg = false;
							writer.write(subFileName + "文件夹下没有找到：" + dataSheetModle + "文件！");
							writer.write("\n");
						}
					} else if(subFile.isDirectory() && "33传感器件".equals(subFileName)){
						File[] childs = subFile.listFiles();
						boolean isFind=false;
						for(File child : childs){
							if(dataSheetModle.equals(child.getName())){
								
								documentFiles.add(child);
								isFind=true;
								break;
							}
						}
						if(!isFind){
							
							flg = false;
							writer.write(subFileName + "文件夹下没有找到：" + dataSheetModle + "文件！");
							writer.write("\n");
						}
					} else if(subFile.isDirectory() && "34继电器".equals(subFileName)){
						File[] childs = subFile.listFiles();
						boolean isFind=false;
						for(File child : childs){
							if(dataSheetModle.equals(child.getName())){
								
								documentFiles.add(child);
								isFind=true;
								break;
							}
						}
						if(!isFind){
							
							flg = false;
							writer.write(subFileName + "文件夹下没有找到：" + dataSheetModle + "文件！");
							writer.write("\n");
						}
					} else if(subFile.isDirectory() && "35开关".equals(subFileName)){
						File[] childs = subFile.listFiles();
						boolean isFind=false;
						for(File child : childs){
							if(dataSheetModle.equals(child.getName())){
								
								documentFiles.add(child);
								isFind=true;
								break;
							}
						}
						if(!isFind){
							
							flg = false;
							writer.write(subFileName + "文件夹下没有找到：" + dataSheetModle + "文件！");
							writer.write("\n");
						}
					} else if(subFile.isDirectory() && "36连接器".equals(subFileName)){
						File[] childs = subFile.listFiles();
						boolean isFind=false;
						for(File child : childs){
							if(dataSheetModle.equals(child.getName())){
								
								documentFiles.add(child);
								isFind=true;
								break;
							}
						}
						if(!isFind){
							
							flg = false;
							writer.write(subFileName + "文件夹下没有找到：" + dataSheetModle + "文件！");
							writer.write("\n");
						}
//					} else if(subFile.isDirectory() && "37端子".equals(subFileName)){
//						File[] childs = subFile.listFiles();
//						boolean isFind=false;
//						for(File child : childs){
//							if(dataSheetModle.equals(child.getName())){
//								
//								documentFiles.add(child);
//								isFind=true;
//								break;
//							}
//						}
//						if(!isFind){
//							
//							flg = false;
//							writer.write(subFileName + "文件夹下没有找到：" + dataSheetModle + "文件！");
//							writer.write("\n");
//						}
					} else if(subFile.isDirectory() && "38模块类".equals(subFileName)){
						File[] childs = subFile.listFiles();
						boolean isFind=false;
						for(File child : childs){
							if(dataSheetModle.equals(child.getName())){
								
								documentFiles.add(child);
								isFind=true;
								break;
							}
						}
						if(!isFind){
							
							flg = false;
							writer.write(subFileName + "文件夹下没有找到：" + dataSheetModle + "文件！");
							writer.write("\n");
						}
					} else if(subFile.isDirectory() && "39电声器件".equals(subFileName)){
						File[] childs = subFile.listFiles();
						boolean isFind=false;
						for(File child : childs){
							if(dataSheetModle.equals(child.getName())){
								
								documentFiles.add(child);
								isFind=true;
								break;
							}
						}
						if(!isFind){
							
							flg = false;
							writer.write(subFileName + "文件夹下没有找到：" + dataSheetModle + "文件！");
							writer.write("\n");
						}
					} else if(subFile.isDirectory() && "41电气部件".equals(subFileName)){
						File[] childs = subFile.listFiles();
						boolean isFind=false;
						for(File child : childs){
							if(dataSheetModle.equals(child.getName())){
								
								documentFiles.add(child);
								isFind=true;
								break;
							}
						}
						if(!isFind){
							
							flg = false;
							writer.write(subFileName + "文件夹下没有找到：" + dataSheetModle + "文件！");
							writer.write("\n");
						}
					} else if(subFile.isDirectory() && "10板材".equals(subFileName)){
						File[] childs = subFile.listFiles();
						boolean isFind=false;
						for(File child : childs){
							if(dataSheetModle.equals(child.getName())){
								
								documentFiles.add(child);
								isFind=true;
								break;
							}
						}
						if(!isFind){
							
							flg = false;
							writer.write(subFileName + "文件夹下没有找到：" + dataSheetModle + "文件！");
							writer.write("\n");
						}
					} else if(subFile.isDirectory() && "11型材".equals(subFileName)){
						File[] childs = subFile.listFiles();
						boolean isFind=false;
						for(File child : childs){
							if(dataSheetModle.equals(child.getName())){
								
								documentFiles.add(child);
								isFind=true;
								break;
							}
						}
						if(!isFind){
							
							flg = false;
							writer.write(subFileName + "文件夹下没有找到：" + dataSheetModle + "文件！");
							writer.write("\n");
						}
					} else if(subFile.isDirectory() && "12棒材".equals(subFileName)){
						File[] childs = subFile.listFiles();
						boolean isFind=false;
						for(File child : childs){
							if(dataSheetModle.equals(child.getName())){
								
								documentFiles.add(child);
								isFind=true;
								break;
							}
						}
						if(!isFind){
							
							flg = false;
							writer.write(subFileName + "文件夹下没有找到：" + dataSheetModle + "文件！");
							writer.write("\n");
						}
					} else if(subFile.isDirectory() && "13管材".equals(subFileName)){
						File[] childs = subFile.listFiles();
						boolean isFind=false;
						for(File child : childs){
							if(dataSheetModle.equals(child.getName())){
								
								documentFiles.add(child);
								isFind=true;
								break;
							}
						}
						if(!isFind){
							
							flg = false;
							writer.write(subFileName + "文件夹下没有找到：" + dataSheetModle + "文件！");
							writer.write("\n");
						}
					} else if(subFile.isDirectory() && "14线材".equals(subFileName)){
						File[] childs = subFile.listFiles();
						boolean isFind=false;
						for(File child : childs){
							if(dataSheetModle.equals(child.getName())){
								
								documentFiles.add(child);
								isFind=true;
								break;
							}
						}
						if(!isFind){
							
							flg = false;
							writer.write(subFileName + "文件夹下没有找到：" + dataSheetModle + "文件！");
							writer.write("\n");
						}
					} else if(subFile.isDirectory() && "15胶水".equals(subFileName)){
						File[] childs = subFile.listFiles();
						boolean isFind=false;
						for(File child : childs){
							if(dataSheetModle.equals(child.getName())){
								
								documentFiles.add(child);
								isFind=true;
								break;
							}
						}
						if(!isFind){
							
							flg = false;
							writer.write(subFileName + "文件夹下没有找到：" + dataSheetModle + "文件！");
							writer.write("\n");
						}
					} else if(subFile.isDirectory() && "19其他".equals(subFileName)){
						File[] childs = subFile.listFiles();
						boolean isFind=false;
						for(File child : childs){
							if(dataSheetModle.equals(child.getName())){
								
								documentFiles.add(child);
								isFind=true;
								break;
							}
						}
						if(!isFind){
							
							flg = false;
							writer.write(subFileName + "文件夹下没有找到：" + dataSheetModle + "文件！");
							writer.write("\n");
						}
					} else if(subFile.isDirectory() && "560121变压器".equals(subFileName)){
						File[] childs = subFile.listFiles();
						boolean isFind=false;
						for(File child : childs){
							if(dataSheetModle.equals(child.getName())){
								
								documentFiles.add(child);
								isFind=true;
								break;
							}
						}
						if(!isFind){
							
							flg = false;
							writer.write(subFileName + "文件夹下没有找到：" + dataSheetModle + "文件！");
							writer.write("\n");
						}
					} else if(subFile.isDirectory() && "560122互感器".equals(subFileName)){
						File[] childs = subFile.listFiles();
						boolean isFind=false;
						for(File child : childs){
							if(dataSheetModle.equals(child.getName())){
								
								documentFiles.add(child);
								isFind=true;
								break;
							}
						}
						if(!isFind){
							
							flg = false;
							writer.write(subFileName + "文件夹下没有找到：" + dataSheetModle + "文件！");
							writer.write("\n");
						}
					} else if(subFile.isDirectory() && "550550防护套".equals(subFileName)){
						File[] childs = subFile.listFiles();
						boolean isFind=false;
						for(File child : childs){
							if(dataSheetModle.equals(child.getName())){
								
								documentFiles.add(child);
								isFind=true;
								break;
							}
						}
						if(!isFind){
							
							flg = false;
							writer.write(subFileName + "文件夹下没有找到：" + dataSheetModle + "文件！");
							writer.write("\n");
						}
					} else if(subFile.isDirectory() && "550551扎带".equals(subFileName)){
						File[] childs = subFile.listFiles();
						boolean isFind=false;
						for(File child : childs){
							if(dataSheetModle.equals(child.getName())){
								
								documentFiles.add(child);
								isFind=true;
								break;
							}
						}
						if(!isFind){
							
							flg = false;
							writer.write(subFileName + "文件夹下没有找到：" + dataSheetModle + "文件！");
							writer.write("\n");
						}
					} else if(subFile.isDirectory() && "550554PG头".equals(subFileName)){
						File[] childs = subFile.listFiles();
						boolean isFind=false;
						for(File child : childs){
							if(dataSheetModle.equals(child.getName())){
								
								documentFiles.add(child);
								isFind=true;
								break;
							}
						}
						if(!isFind){
							
							flg = false;
							writer.write(subFileName + "文件夹下没有找到：" + dataSheetModle + "文件！");
							writer.write("\n");
						}
					}
					
				}
			} else {
				
				writer.write("系统读取datasheet文件夹！");
				writer.write("\n");
			}
		} catch (IOException e) {
			
			flg = false;
			try {
				writer.write("处理loadAllRelationFile方法报错："+e.getMessage());
				writer.write("\n");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return flg;
	}
    
    /**
	 * 导入文档
	 * @param documentFiles
	 * @param docError
	 */
	public static void loadDoc(List<File> documentFiles,BufferedWriter writer){
		try{
			List<String> docStatement = document(documentFiles,writer);
			
			writeTxt(rootPath+"datasheet/loadDataSheet.bat",docStatement);
			if (checkflg){
				
				writer.write("datasheet导入验证正确！");
				writer.write("\n");
			} else {
				writer.write("datasheet导入验证有误！");
				writer.write("\n");
			}
		}catch(Exception e){
			e.printStackTrace();
			
			try {
				writer.write("错误信息：" + e.getMessage());
				writer.write("\n");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	/**
	 * 读取文档
	 * @param documentFiles
	 * @return
	 */
	private static List<String> document(List<File> documentFiles, BufferedWriter writer) {
		
		List<String> impStatement = new ArrayList<String>();
		List<String> typelist = new ArrayList<String>();
		Map<String, List<String>> relatePnMap = new HashMap<String, List<String>>();
		typelist.add("30分立器件");
		typelist.add("31IC");
		typelist.add("32保护器件");
		typelist.add("33传感器件");
		typelist.add("34继电器");
		typelist.add("35开关");
		typelist.add("36连接器");
//		typelist.add("37端子");
		typelist.add("38模块类");
		typelist.add("39电声器件");
		typelist.add("41电气部件");
		
		typelist.add("10板材");
		typelist.add("11型材");
		typelist.add("12棒材");
		typelist.add("13管材");
		typelist.add("14线材");
		typelist.add("15胶水");
		typelist.add("19其他");
		typelist.add("560121变压器");
		typelist.add("560122互感器");
		typelist.add("550550防护套");
		typelist.add("550551扎带");
		typelist.add("550554PG头");
		
		for(File file : documentFiles){
			
			String absolutePath = file.getAbsolutePath();
			String path1 = absolutePath.substring(0,absolutePath.lastIndexOf("/"));
			String projectName = path1.substring(path1.lastIndexOf("/")+1, path1.length());
			
			if (typelist.contains(projectName)){
				typelist.remove(projectName);
			}
			
			ExcelReader readExcel = new ExcelReader(file);
			int index=0;
			int sheetIndex=0;
			String[] rows=null;
			try {
				readExcel.open();
				for(int sheet=0;sheet<1;sheet++){
					sheetIndex=sheet;
					readExcel.setSheetNum(sheet); // 设置读取索引为0的工作表
					
					// 总行数
					int count = readExcel.getRowCount();
					
					for (int i = 1; i <= count; i++) {
						index=i;
						rows = readExcel.readExcelLine(i);
						
						if(rows == null || Arrays.toString(rows).equals("[, , , , , , , , ]")|| Arrays.toString(rows).equals("[, , , , , , , ]")||Arrays.toString(rows).equals("[, , , , , , , , , , , , , ]")){
							continue;
						}
						
						if(rows.length <19){
							
							checkflg = false;
							writer.write(absolutePath+" 第"+(sheetIndex+1)+"sheet中的第"+(index+1)+"行,rows="+Arrays.toString(rows)+"没有导入，列少于19个(文件目录)");
							writer.write("\n");
							continue;
						}
						if(StrUtils.isEmpty(rows[13])){
							
							checkflg = false;
							writer.write(absolutePath+" 第"+(sheetIndex+1)+"sheet中的第"+(index+1)+"行,rows="+Arrays.toString(rows)+"“品牌”不能为空！");
							writer.write("\n");
							continue;
						}
						if(StrUtils.isEmpty(rows[14])){
							
							checkflg = false;
							writer.write(absolutePath+" 第"+(sheetIndex+1)+"sheet中的第"+(index+1)+"行,rows="+Arrays.toString(rows)+"“型号/系列”不能为空！");
							writer.write("\n");
							continue;
						}
						
						if(StrUtils.isEmpty(rows[17])){
							
							checkflg = false;
							writer.write(absolutePath+" 第"+(sheetIndex+1)+"sheet中的第"+(index+1)+"行,rows="+Arrays.toString(rows)+"“文件目录”不能为空！");
							writer.write("\n");
							continue;
						}
						if(StrUtils.isEmpty(rows[18])){
							
							checkflg = false;
							writer.write(absolutePath+" 第"+(sheetIndex+1)+"sheet中的第"+(index+1)+"行,rows="+Arrays.toString(rows)+"“关联的PN”不能为空！");
							writer.write("\n");
							continue;
						}
						
						if (rows[17].trim().indexOf(".") < 0){
							
							logs.add(new String[]{rows[19], projectName, String.valueOf(index+1), "附件："+rows[17]+"名称有误！", "0", ""});
							checkflg = false;
							writer.write(absolutePath+" 第"+(sheetIndex+1)+"sheet中的第"+(index+1)+"行,rows="+Arrays.toString(rows)+"没有导入，附件："+rows[17]+"名称有误！");
							writer.write("\n");
							continue;
						}
						
						relatePnMap = getPNNumList(relatePnMap, sheetIndex, index, rows, absolutePath, writer);
						
						String brand = rows[13].trim().replaceAll("&amp;", "&");//品牌
						String model = rows[14].trim().replaceAll("&amp;", "&");//型号/类型
						List<String> list = relatePnMap.get(brand +"_"+model);
						if (list != null){
							
							for (String str : list){
								
								if (!totallist.contains(str)){
									checkflg = false;
									logs.add(new String[]{rows[19], projectName, String.valueOf(index+1), "关联的PN:"+str+"在系统中不存在！", "0", ""});
									
									writer.write(absolutePath+" 第"+(sheetIndex+1)+"sheet中的第"+(index+1)+"行,"+"关联的PN:"+str+"在系统中不存在！");
									writer.write("\n");
//									continue;
								}
							}
						}
						
						
						String filename = rows[17].trim().replace("&amp;", "&");
						String name = filename.substring(0, filename.lastIndexOf(".")).trim();
						String fileType = filename.substring(filename.lastIndexOf("."), filename.length()).trim();
						filename = name + fileType;
						
//						File temp = new File(rootPath+"datasheet/"+projectName+"/"+rows[17].replace("&amp;", "&"));
						File temp = new File(rootPath+"datasheet/"+projectName+"/"+filename);
						
						if(!temp.exists()){
							
							logs.add(new String[]{rows[19], projectName, String.valueOf(index+1), "附件："+filename+"不存在！", "1", filename});
							checkflg = false;
							writer.write(absolutePath+" 第"+(sheetIndex+1)+"sheet中的第"+(index+1)+"行,rows="+Arrays.toString(rows)+"没有导入，附件："+filename+"不存在！");
							writer.write("\n");
							continue;
						}
						
						if (temp.length() == 0){
							
							logs.add(new String[]{rows[19], projectName, String.valueOf(index+1), "附件："+filename+"内容为空！", "1", filename});
							checkflg = false;
							writer.write(rootPath+"datasheet/"+projectName+"/"+filename+"文件内容为空！！");
							writer.write("\n");
							continue;
						}
					}					
				}
			}catch (Exception e) {
				try {
					writer.write(absolutePath+" 第"+(sheetIndex+1)+"sheet中的第"+(index+1)+"行,rows="+Arrays.toString(rows)+"报错！");
					writer.write("\n");
					writer.write("错误信息："+e.getMessage());
					writer.write("\n");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}
		
		for (String type : typelist){
			
			checkflg = false;
			try {
				writer.write(type + "文件夹不存在！！");
				writer.write("\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			for (List<String> pnlist : relatePnMap.values()) {
				
				for (String pn : pnlist){
					WTPart part = PartUtil.getLastestWTPartByNumber(pn);
					boolean checkoutFlag = WorkInProgressHelper.isCheckedOut(part);
    				boolean workinfCopyFlag = WorkInProgressHelper.isWorkingCopy(part);
    				
    				if (checkoutFlag || workinfCopyFlag){
    					WorkInProgressHelper.service.undoCheckout(part);
						writer.write("部件编号："+part.getNumber()+"为检出状态，已经被自动撤销检出！");
    	        		writer.write("\n");
    	        		logs.add(new String[]{"", "", "", "PN:"+pn+"为检出状态，已经被自动撤销检出！！", "", ""});
    				}
//					if (WorkInProgressHelper.isCheckedOut(part)){
//						checkflg = false;
//						logs.add(new String[]{"", "", "", "PN:"+pn+"已经被检出！", "", ""});
//						
//						writer.write("PN:"+pn+"已经被检出！");
//						writer.write("\n");
//					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		
		if (checkflg){
			
			List<String> addedList = new ArrayList<String>();
			
			String uploadRepository="Datasheet库";
			
			for(File file : documentFiles){
				
				String absolutePath = file.getAbsolutePath();
				String path1 = absolutePath.substring(0,absolutePath.lastIndexOf("/"));
				String projectName = path1.substring(path1.lastIndexOf("/")+1, path1.length());
				
				String fileName = file.getName().replaceAll("&", "&amp;").substring(0,file.getName().lastIndexOf("."));
				ExcelReader readExcel = new ExcelReader(file);
				int index=0;
				int sheetIndex=0;
				String[] rows=null;
				try {
					readExcel.open();
					for(int sheet=0;sheet<1;sheet++){
						sheetIndex=sheet;
						readExcel.setSheetNum(sheet); // 设置读取索引为0的工作表
						
						// 总行数
						int count = readExcel.getRowCount();
						
						StringBuffer xml = new StringBuffer("<?xml version='1.0' ?><!DOCTYPE NmLoader SYSTEM 'standardX24.dtd'><NmLoader>");
						StringBuffer xmlReference = new StringBuffer("<?xml version='1.0' ?><!DOCTYPE NmLoader SYSTEM 'standardX24.dtd'><NmLoader>");
						StringBuffer xmlDescribe = new StringBuffer("<?xml version='1.0' ?><!DOCTYPE NmLoader SYSTEM 'standardX24.dtd'><NmLoader>");
						
						for (int i = 1; i <= count; i++) {
							index=i;
							rows = readExcel.readExcelLine(i);
							
							String brand = rows[13].trim().replaceAll("&amp;", "&");//品牌
							String model = rows[14].trim().replaceAll("&amp;", "&");//型号/类型
							if (addedList.contains(brand+"_"+model)){
								continue;
							} else {
								addedList.add(brand+"_"+model);
							}
							
							String uploadPath="/Default/"+projectName;
							
							//文档没有创建者标签
							xml.append("<csvBeginWTDocument handler='wt.doc.LoadDoc.beginCreateWTDocument' ><csvname>").append(brand.replaceAll("&", "&amp;")+"_"+model.replaceAll("&", "&amp;")).append("</csvname><csvtitle>").append(brand.replaceAll("&", "&amp;")+"_"+model.replaceAll("&", "&amp;")).append("</csvtitle>")
							.append("<csvnumber>").append(rows[2]).append("</csvnumber>")
							.append("<csvtype>Document</csvtype><csvdescription>").append(rows[4]).append("</csvdescription><csvdepartment>DESIGN</csvdepartment>")
							.append("<csvsaveIn>").append(uploadPath).append("</csvsaveIn>")
							.append("<csvteamTemplate></csvteamTemplate><csvdomain></csvdomain>")
							.append("<csvlifecycletemplate>LC_EDatasheetDoc_cycle</csvlifecycletemplate><csvlifecyclestate>RELEASED</csvlifecyclestate>")
//							.append("<csvtypedef>").append(ConstantLine.TYPE_NAME.get(rows[1].trim())).append("</csvtypedef>")
							.append("<csvtypedef>").append("com.CATLBattery.EDatasheetDoc").append("</csvtypedef>")
							.append("<csvversion></csvversion>")
							.append("<csviteration></csviteration>")
							.append("<csvsecurityLabels></csvsecurityLabels></csvBeginWTDocument>");
							
							//品牌
							if(!StrUtils.isEmpty(brand)){
								xml.append("<csvIBAValue handler='wt.iba.value.service.LoadValue.createIBAValue' >")
								.append("<csvdefinition>CATL_Brand</csvdefinition>")
								.append("<csvvalue1>").append(brand.replaceAll("&", "&amp;")).append("</csvvalue1>")
								.append("<csvvalue2></csvvalue2><csvdependency_id></csvdependency_id></csvIBAValue>");
							}
							
							//型号/系列	
							if(!StrUtils.isEmpty(model)){
								xml.append("<csvIBAValue handler='wt.iba.value.service.LoadValue.createIBAValue' >")
								.append("<csvdefinition>CATL_Model</csvdefinition>")
								.append("<csvvalue1>").append(model.replaceAll("&", "&amp;")).append("</csvvalue1>")
								.append("<csvvalue2></csvvalue2><csvdependency_id></csvdependency_id></csvIBAValue>");
							}
							
							//关键参数
							if(!StrUtils.isEmpty(rows[15])){
								xml.append("<csvIBAValue handler='wt.iba.value.service.LoadValue.createIBAValue' >")
								.append("<csvdefinition>CATL_KeyParameters</csvdefinition>")
								.append("<csvvalue1>").append(rows[15].replaceAll("&", "&amp;")).append("</csvvalue1>")
								.append("<csvvalue2></csvvalue2><csvdependency_id></csvdependency_id></csvIBAValue>");
							}
							
//							String fileType = rows[17].substring(rows[17].trim().lastIndexOf("."), rows[17].trim().length()).replaceAll(" ", "");
//							String filename = rows[13].trim().replaceAll("/", "").replaceAll("/*", "").replaceAll("&amp;", "&")+"_"+rows[14].trim().replaceAll("/", "").replaceAll("/*", "").replaceAll("&amp;", "&")+fileType;
							
							String filename = rows[17].trim().replace("&amp;", "&");
							String name = filename.substring(0, filename.lastIndexOf(".")).trim();
							String fileType = filename.substring(filename.lastIndexOf("."), filename.length()).trim();
							filename = name + fileType;
							
							xml.append("<csvEndWTDocument handler='wt.doc.LoadDoc.endCreateWTDocument' >")
							.append("<csvprimarycontenttype>ApplicationData</csvprimarycontenttype>")
							.append("<csvpath>").append(rootPath+"datasheet/"+projectName+"/"+filename.replaceAll("&", "&amp;")).append("</csvpath><csvformat></csvformat>")
							.append("<csvcontdesc></csvcontdesc><csvparentContainerPath></csvparentContainerPath>")
							.append("</csvEndWTDocument>");
							
							List<String> relatePnList = relatePnMap.get(brand+"_"+model);
							
							if (relatePnList != null){
								
								String[] partNumberArr =relatePnList.toArray(new String[relatePnList.size()]);
								
								for(int j=0; j<partNumberArr.length; j++){
//									if(partStatement.contains(partNumberArr[j])){
//										if(rows[1].equals("产品技术文件") || rows[1].equals("研发过程文档") || rows[1].equals("电子电气件Datasheet")){  
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
//										}else{
//											xmlDescribe.append("<csvPartDocDescribes handler='wt.part.LoadPart.createPartDocDescribes' >")
//											.append("<csvdocNumber>").append(rows[2]).append("</csvdocNumber>")
//											.append("<csvdocVersion></csvdocVersion>")
//											.append("<csvdocIteration></csvdocIteration>")
//											.append("<csvpartNumber>").append(partNumberArr[j]).append("</csvpartNumber>")
//											.append("<csvpartVersion></csvpartVersion>")
//											.append("<csvpartIteration></csvpartIteration>")
//											.append("<csvpartView></csvpartView>")
//											.append("<csvpartVariation1></csvpartVariation1>")
//											.append("<csvpartVariation2></csvpartVariation2>")
//											.append("<csvorganizationName></csvorganizationName>")
//											.append("<csvorganizationID></csvorganizationID>")
//											.append("</csvPartDocDescribes>");
//										}
//								}
								}
							}
						}
						
						xml.append("</NmLoader>");
						xmlReference.append("</NmLoader>");
						xmlDescribe.append("</NmLoader>");
						
						Java2XML.writerByString(xml.toString(),rootPath+"datasheet/"+projectName+"/"+fileName+"_"+projectName+"_Document.xml");
						Java2XML.writerByString(xmlReference.toString(), rootPath+"datasheet/"+projectName+"/"+fileName+"_"+projectName+"_PartDocReference.xml");
						Java2XML.writerByString(xmlDescribe.toString(), rootPath+"datasheet/"+projectName+"/"+fileName+"_"+projectName+"_PartDocDescribe.xml");
						
						impStatement.add("windchill wt.load.LoadFromFile -d " + rootPath+"datasheet/"+projectName+"/"+fileName+"_"+projectName+"_Document.xml -u " + username + " -p " + pwd + " -CONT_PATH /\"/wt.inf.container.OrgContainer=CATL/wt.inf.library.WTLibrary="+uploadRepository+"\"");
						impStatement.add("windchill wt.load.LoadFromFile -d "+ rootPath+"datasheet/"+projectName+"/"+fileName+"_"+projectName+"_PartDocReference.xml -u " + username + " -p " + pwd + " -CONT_PATH /\"/wt.inf.container.OrgContainer=CATL/wt.inf.library.WTLibrary="+uploadRepository+"\"");
						impStatement.add("windchill wt.load.LoadFromFile -d "+ rootPath+"datasheet/"+projectName+"/"+fileName+"_"+projectName+"_PartDocDescribe.xml -u " + username + " -p " + pwd + " -CONT_PATH /\"/wt.inf.container.OrgContainer=CATL/wt.inf.library.WTLibrary="+uploadRepository+"\"");
						
					}
				}catch (Exception e) {
					try {
						writer.write(absolutePath+" 第"+(sheetIndex+1)+"sheet中的第"+(index+1)+"行,rows="+Arrays.toString(rows)+"报错！");
						writer.write("\n");
						writer.write("错误信息："+e.getMessage());
						writer.write("\n");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					e.printStackTrace();
				}
			}
		}
		
		return impStatement;
		
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
	 * 写入文件
	 * @param fileName
	 * @param colloections
	 * @return
	 */
	static Map<String, List<String>> getPNNumList(Map<String, List<String>> relatePnMap, int sheetIndex, int index, String[] rows, String absolutePath, BufferedWriter writer){
		
		try{
			String brand = rows[13].trim().replaceAll("&amp;", "&");//品牌
			String model = rows[14].trim().replaceAll("&amp;", "&");//型号/类型
			List<String> relatePnList = relatePnMap.get(brand+"_"+model);
			
			if (relatePnList == null){
				relatePnList = new ArrayList<String>();
			}
			String[] partNumberArrTemp= rows[18].replaceAll("，", ",").split(",");
			for(int hh=0;hh<partNumberArrTemp.length;hh++){
				String valuetemp=partNumberArrTemp[hh].trim();
				
				if (StringUtils.isEmpty(valuetemp)){
					continue;
				}
				
				if(valuetemp.indexOf("@@")>-1){
					String[] partNumberChild=valuetemp.split("@@");
					
					if (partNumberChild.length > 2){
						writer.write(absolutePath+" 第"+(sheetIndex+1)+"sheet中的第"+(index+1)+"行,rows="+Arrays.toString(rows)+"关联的PN："+valuetemp+"格式不正确！");
						writer.write("\n");
						continue;
					}
					String[] beforeValue=partNumberChild[0].trim().split("-");
					String[] afterValue=partNumberChild[1].trim().split("-");
					
					if (beforeValue.length != 2 || afterValue.length != 2){
						writer.write(absolutePath+" 第"+(sheetIndex+1)+"sheet中的第"+(index+1)+"行,rows="+Arrays.toString(rows)+"关联的PN："+valuetemp+"格式不正确！");
						writer.write("\n");
						continue;
					}
					
					if(!beforeValue[0].trim().equals(afterValue[0].trim())){
						
						writer.write(absolutePath+" 第"+(sheetIndex+1)+"sheet中的第"+(index+1)+"行,rows="+Arrays.toString(rows)+"关联的PN："+valuetemp+"格式不正确！");
						writer.write("\n");
						continue;
					}else{
						int beforeNum = Integer.valueOf(beforeValue[1]);
						int afterNum = Integer.valueOf(afterValue[1]);
						for(int yy=beforeNum;yy<=afterNum;yy++){
							String pn001="";
							for(int rr=String.valueOf(yy).length();rr<5;rr++){
								pn001=pn001+"0";
							}
							pn001=pn001+yy;
							
							if(!relatePnList.contains(beforeValue[0]+"-"+pn001)){
								relatePnList.add(beforeValue[0]+"-"+pn001);
							}
							
						}
					}
				}else{
					
					if(!relatePnList.contains(valuetemp)){
						relatePnList.add(valuetemp);
					}
				}
			}
			
			relatePnMap.put(brand+"_"+model, relatePnList);
		} catch (Exception e){
			
			try {
				writer.write(absolutePath+" 第"+(sheetIndex+1)+"sheet中的第"+(index+1)+"行，获取关联的PN时报错！错误信息："+e.getMessage());
				writer.write("\n");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		return relatePnMap;
	}
	
	static Vector<WTPartMaster> getPart() throws WTException {
    	
		Vector<WTPartMaster> wtparts=new Vector<WTPartMaster>();
		QuerySpec qs= new QuerySpec(WTPartMaster.class);
//		qs.appendWhere(new SearchCondition(WTPart.class, WTPart.LATEST_ITERATION, SearchCondition.IS_TRUE), new int[] {0});
		QueryResult qr = PersistenceHelper.manager.find(qs);
		while(qr.hasMoreElements()){			
			WTPartMaster wtpartmaster = (WTPartMaster) qr.nextElement();
			wtparts.add(wtpartmaster);
		}
		return wtparts;
	}
}