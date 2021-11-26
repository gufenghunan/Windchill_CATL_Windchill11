package com.catl.loadData;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.catl.loadData.util.ExcelReader;


public class CheckBomAndPart {

	public static void main(String[] args) {
		int index = 0;
		String[] rows = null;
		Set<String> set = new HashSet<String>();
		try{
			
			File root = new File("E:\\ATL\\project\\PLM\\导入测试\\项目数据梳理汇总\\");
			List<File> bomFiles = new ArrayList<File>();
			List<File> documentFiles = new ArrayList<File>();
			if(root.isDirectory()){
				File[] files = root.listFiles();
				for(File file : files){
					String fileName = file.getName();
					if(file.isDirectory()){
						File[] subFiles = file.listFiles();
						for(File subFile : subFiles){
							String subFileName = subFile.getName();
							if(subFile.isDirectory() && subFileName.equals("BOM")){
								File[] childs = subFile.listFiles();
								boolean isFind=false;
								for(File child : childs){
									if(child.getName().equals(fileName+".xlsx")){
										bomFiles.add(child);
										isFind=true;
									}
								}
								if(!isFind){
									System.out.println("Bom: "+subFile.getAbsolutePath()+"  目录中，没找到"+fileName+".xlsx");
								}
							}else if(subFile.isDirectory() && subFileName.equals("文档")){
								File[] childs = subFile.listFiles();
								boolean isFind=false;
								for(File child : childs){
									if(child.getName().equals(fileName+".xlsx")){
										documentFiles.add(child);
										isFind=true;
									}
								}
								if(!isFind){
									System.out.println("Document: "+subFile.getAbsolutePath()+"  目录中，没找到"+fileName+".xlsx");
								}
							}
						}
					}
				}
			}
			ExcelReader er;
			for(File file : bomFiles){
				System.out.println("----------------"+file.getName());
				er = new ExcelReader(file);
				er.open();
				//er.setSheetNum(0);
				er.setSheetNum(er.getSheetIndex("产品BOM_IT"));
				int count = er.getRowCount();
				for(int i=1; i<=count; i++){
					index=i;
					rows = er.readExcelLine(i);
					if(rows == null){
						System.out.println("第"+(index+1)+"行,rows="+Arrays.toString(rows)+"没有导入");
						continue;
					}
					if(rows.length<4){
						System.out.println("第"+(index+1)+"行，值为"+Arrays.toString(rows)+",少于4列(单位),没有导入xml");
						continue;
					}
					if(StrUtils.isEmpty(rows[0]) || StrUtils.isEmpty(rows[1]) || StrUtils.isEmpty(rows[2]) || StrUtils.isEmpty(rows[3]) ||
							rows[0].trim().equals("待定") || rows[2].trim().equals("待定") || 
							rows[0].trim().equals("#N/A") || rows[1].trim().equals("#N/A") || rows[2].trim().equals("#N/A") || rows[3].trim().equals("#N/A")){
						System.out.println("第"+(index+1)+"行，值为"+Arrays.toString(rows)+",值为空或待定,没有导入xml");
						continue;
					}
					set.add(rows[0]);
					set.add(rows[2]);
				}
			}
			
			
			System.out.println("-----------------------已完成_共享版.xlsx");
			File file = new File("E:\\ATL\\project\\PLM\\导入测试\\项目数据梳理汇总\\已完成_共享版.xlsx");
			er = new ExcelReader(file);
			er.open();
			er.setSheetNum(0);
			int count = er.getRowCount();
			for (int i = 2; i <= count; i++) {
				index=i;
				rows = er.readExcelLine(i);
				if(rows == null){
					System.out.println("第"+(index+1)+"行,rows="+Arrays.toString(rows)+"没有导入");
					continue;
				}
				if(rows.length<17){
					if(rows.length>8 && !StrUtils.isEmpty(rows[8])){
						System.out.println("第"+(i+1)+"行，值为"+Arrays.toString(rows)+",少于17列(文件夹)，有新物料编号,没有导入xml");
					}else{
						System.out.println("第"+(i+1)+"行，值为"+Arrays.toString(rows)+",少于17列(文件夹),没有导入xml");
					}
					continue;
				}
				if(StrUtils.isEmpty(rows[0])&&StrUtils.isEmpty(rows[1])&&StrUtils.isEmpty(rows[2])&&StrUtils.isEmpty(rows[3])&&StrUtils.isEmpty(rows[4])&&
						StrUtils.isEmpty(rows[5])&&StrUtils.isEmpty(rows[6])&&StrUtils.isEmpty(rows[7])&&StrUtils.isEmpty(rows[8])&&StrUtils.isEmpty(rows[9])&&
						StrUtils.isEmpty(rows[10])&&StrUtils.isEmpty(rows[11])&&StrUtils.isEmpty(rows[12])&&StrUtils.isEmpty(rows[13])&&StrUtils.isEmpty(rows[14])&&
						StrUtils.isEmpty(rows[15])&&StrUtils.isEmpty(rows[16])&&StrUtils.isEmpty(rows[17])
						&& !StrUtils.isEmpty(rows[18])){//分类属性行
					continue;
				}
				if(StrUtils.isEmpty(rows[0])&&StrUtils.isEmpty(rows[1])&&StrUtils.isEmpty(rows[2])&&StrUtils.isEmpty(rows[3])&&StrUtils.isEmpty(rows[4])&&
						StrUtils.isEmpty(rows[5])&&StrUtils.isEmpty(rows[6])&&StrUtils.isEmpty(rows[7])&&StrUtils.isEmpty(rows[8])){
					System.out.println("第"+(i+1)+"行，值为"+Arrays.toString(rows)+",为空值,没有导入xml");
					continue;
				}
				if(StrUtils.isEmpty(rows[8])){
					System.out.println("第"+(i+1)+"行，值为"+Arrays.toString(rows)+",第8列(新物料编码),没有导入xml");
					continue;
				}
				
				set.remove(rows[8]);
			}
			
			System.out.println("diff :"+set);
			
			
		}catch(Exception e){
			System.out.println("第"+(index+1)+"行,rows="+Arrays.toString(rows)+"没有导入");
			e.printStackTrace();
		}
		
	}
}
