package com.catl.line.dwg;

import java.io.File;
import java.util.Map;

import net.sf.json.JSONObject;

import com.catl.line.transfer.ConstantDwg;
import com.catl.line.transfer.FTPUtil;
import com.catl.line.transfer.FileUtil;
import com.catl.line.transfer.TransferTool;

public class DwgWebservice {
	 /**
	  * dwg转pdf接口方法
	  * @param path
	  * @return
	  */
	 public static String dwgtopdf(String path){  
		 try{
			 String ftppath=path.substring(0, path.lastIndexOf("/"));
			 String filename=path.substring(path.lastIndexOf("/")+1,path.length());
			boolean isdownload=FTPUtil.downloadFile(ftppath, filename, ConstantDwg.dwg_localpath+File.separator+filename);//dwg文件删除
			if(isdownload){
				 System.out.println(ConstantDwg.dwg_localpath+File.separator+filename);
				 File pdffile=TransferTool.getMultiBoxPDF(ConstantDwg.dwg_localpath+File.separator+filename);
				 FTPUtil.ftpUploadFile(ftppath, pdffile.getName(), pdffile);
				 boolean flag=pdffile.getAbsoluteFile().getParent().equals(ConstantDwg.dwg_localpath);
				 if(flag){
					FileUtil.deleteFile(pdffile.getAbsolutePath());
					FileUtil.deleteFile(ConstantDwg.dwg_localpath+File.separator+filename);
				 }else{
					FileUtil.deleteFile(ConstantDwg.dwg_localpath+File.separator+filename);
					FileUtil.deleteDirectory(pdffile.getAbsoluteFile().getParent());
				 }
			 }else{
				 throw new Exception("转图机下载文件到本地目录失败");
			 }
		 }catch(Exception e){
			 e.printStackTrace();
			 return e.getLocalizedMessage();
		 }
		
		return null;
	       
	 }  
	 
	 
	 /**
	  * 修改dwg图纸接口
	  * @param path
	  * @param params
	  * @return
	  */
	 public static String modifyDwgAndToPdf(String path,String params){ 
		 try{
			 String ftppath=path.substring(0, path.lastIndexOf("/"));
			 String filename=path.substring(path.lastIndexOf("/")+1,path.length());
			 //从ftp
			boolean isdownload=FTPUtil.downloadFile(ftppath, filename, ConstantDwg.dwg_localpath+File.separator+filename);//dwg文件删除
			if(isdownload){
				System.out.println(ConstantDwg.dwg_localpath+File.separator+filename);
				 Map dwgmap=JSONObject.fromObject(params);
				 TransferTool.modifyDWG(ConstantDwg.dwg_localpath+File.separator+filename,dwgmap);
				 File dwgfile=new File(ConstantDwg.dwg_localpath+File.separator+filename);
				 FTPUtil.ftpUploadFile(ftppath, dwgfile.getName(), dwgfile);
				 dwgfile.delete();
				
			 }else{
				 throw new Exception("转图机下载文件到本地目录失败");
			 }
		 }catch(Exception e){
			 e.printStackTrace();
			 return e.getLocalizedMessage();
		 }
		
		return null;
	 }  
}
