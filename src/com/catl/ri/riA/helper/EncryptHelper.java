package com.catl.ri.riA.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import wt.util.WTException;

import com.catl.ri.constant.ConstantRI;

public class EncryptHelper {
public static XSSFWorkbook getEncryptWorkbook(String excelPath) throws IOException, GeneralSecurityException, InvalidFormatException, WTException{
	  FileInputStream inp = new FileInputStream(excelPath); 
	  POIFSFileSystem pfs =null;
	  try{
	      pfs = new POIFSFileSystem(inp);  
	  }catch(OfficeXmlFileException e){
		  encryptExcel(excelPath);
		  inp = new FileInputStream(excelPath); 
		  pfs = new POIFSFileSystem(inp); 
	  }
      inp.close();  
      EncryptionInfo encInfo = new EncryptionInfo(pfs);  
      Decryptor decryptor = Decryptor.getInstance(encInfo);  
      XSSFWorkbook workbook =null;
      try{
	      decryptor.verifyPassword(ConstantRI.ri_encrypt_password); 
	      workbook = new XSSFWorkbook(decryptor.getDataStream(pfs));
	      return workbook;  
      }catch(NullPointerException e){
    	  workbook=tryHistoryPassword(decryptor,pfs);
    	  return workbook;  
      }
  }
  
  private static XSSFWorkbook tryHistoryPassword(Decryptor decryptor,
		POIFSFileSystem pfs) throws GeneralSecurityException, IOException, WTException {
	  String[] hpwds=ConstantRI.ri_encrypt_hsitory_password.split(",");
	  XSSFWorkbook workbook=null;
	  for (int i = 0; i < hpwds.length; i++) {
		  try{
			  decryptor.verifyPassword(hpwds[i]); 
			  workbook = new XSSFWorkbook(decryptor.getDataStream(pfs));
			  System.out.println("####");
			  return workbook;
		  }catch(Exception e){
			  System.out.println("----");
			  continue;
		  }
		 
	  }
	throw new WTException("无法打开加密文件");
  }

/* 
   * excelFilePath : excel文件路径 
   * excelPassword : 打开文件密码 
   */  
  public static void  encryptExcel(String excelFilePath) throws InvalidFormatException, IOException, GeneralSecurityException{  
         File fileSoucre = new File(excelFilePath);  
         POIFSFileSystem fs = new POIFSFileSystem();  
         EncryptionInfo info = new EncryptionInfo(fs, EncryptionMode.agile);  
         Encryptor enc = info.getEncryptor();  
         enc.confirmPassword(ConstantRI.ri_encrypt_password);  
         OPCPackage opc = OPCPackage.open(fileSoucre,PackageAccess.READ_WRITE);  
         OutputStream os = enc.getDataStream(fs);  
         opc.save(os);  
         opc.close();  
         FileOutputStream fos = new FileOutputStream(fileSoucre);  
         fs.writeFilesystem(fos);  
         fos.close();  
  }  
  
  /* 
   * excelFilePath : excel文件路径 
   * excelPassword : 打开文件密码 
   */  
  public static void  encryptExcel(File fileSoucre) throws Exception{  
         POIFSFileSystem fs = new POIFSFileSystem();  
         EncryptionInfo info = new EncryptionInfo(fs, EncryptionMode.agile);  
         Encryptor enc = info.getEncryptor();  
         enc.confirmPassword(ConstantRI.ri_encrypt_password);  
         OPCPackage opc = OPCPackage.open(fileSoucre,PackageAccess.READ_WRITE);  
         OutputStream os = enc.getDataStream(fs);  
         opc.save(os);  
         opc.close();  
         FileOutputStream fos = new FileOutputStream(fileSoucre);  
         fs.writeFilesystem(fos);  
         fos.close();  
  } 
  public static void main(String[] args) throws Exception {
		XSSFWorkbook workbook = getEncryptWorkbook("E://tmp//vv.xlsx");
//		OutputStream outputStream = new FileOutputStream(new File("E://tmp//dd.xlsx"));
//		workbook.write(outputStream);
//		outputStream.flush();
//		outputStream.close();
//		workbook.close();
//		encryptExcel("E://tmp//dd.xlsx");
	  
  }
  
  
}
