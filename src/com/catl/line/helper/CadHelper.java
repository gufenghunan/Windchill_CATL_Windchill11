package com.catl.line.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.net.ftp.FTPClient;

import wt.content.ApplicationData;
import wt.doc.WTDocument;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.util.WTProperties;

import com.catl.common.util.PropertiesUtil;
import com.catl.line.constant.ConstantLine;
import com.catl.line.exception.LineException;
import com.catl.line.queue.DWGToPDF;
import com.catl.line.util.CommonUtil;
import com.catl.line.util.FileUtil;
import com.catl.line.util.WCUtil;
import com.catl.line.util.WTDocumentUtil;
import com.catl.line.util.WcFTPUtil;
import com.catl.line.webservice.DwgWebservice;
import com.catl.line.webservice.DwgWebserviceProxy;
import com.opendesign.core.DwgVersion;
import com.opendesign.core.ExSystemServices;
import com.opendesign.td.ExHostAppServices;
import com.opendesign.td.OdDbAttribute;
import com.opendesign.td.OdDbBlockReference;
import com.opendesign.td.OdDbBlockTable;
import com.opendesign.td.OdDbBlockTableRecord;
import com.opendesign.td.OdDbDatabase;
import com.opendesign.td.OdDbObject;
import com.opendesign.td.OdDbObjectIterator;
import com.opendesign.td.OdDbSymbolTableIterator;
import com.opendesign.td.OpenMode;
import com.opendesign.td.SaveType;
import com.opendesign.td.TD_Db;

public class CadHelper implements RemoteAccess{
	public static String wt_home;
	static {
		try {
			//System.loadLibrary("TeighaJavaCore");
			//System.loadLibrary("TeighaJavaDwg");
			wt_home=WTProperties.getLocalProperties().getProperty("wt.home","UTF-8");
		} catch (UnsatisfiedLinkError | IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		rms.setUserName("wcadmin");
		rms.setPassword("wcadmin");
		rms.invoke("test", CadHelper.class.getName(), null, null, null);
	}
	
	/**
	 * 向dwg块中填入参数(linux下 此方法已废弃)
	 * @param srcFileName
	 * @param map
	 * @throws Exception
	 */
		public static void modifyDWG(String srcFileName,Map<String,String> map) throws Exception{
			for(Iterator it=(Iterator) map.keySet().iterator();it.hasNext();){
				String str=(String) it.next();
			}
			ExSystemServices systemServices = new ExSystemServices();
			ExHostAppServices hostApp = new ExHostAppServices();
			hostApp.disableOutput(true);
			TD_Db.odInitialize(systemServices);
			OdDbDatabase oddb = null;//数据库
			if (!hostApp.findFile(srcFileName).isEmpty()){
				try{
				oddb = hostApp.readFile(srcFileName);//源文件不为空，读取源文件
				}catch(Exception e){
					throw new LineException("不识别的图纸,请检查图纸是否损坏");
				}
			}
			OdDbBlockTable blockTable = OdDbBlockTable.cast(oddb.getBlockTableId().safeOpenObject());
			//块集合
			OdDbSymbolTableIterator blockIter = OdDbSymbolTableIterator.cast(blockTable.newIterator());
			for (blockIter.start(); !blockIter.done(); blockIter.step()) {
				OdDbBlockTableRecord block = OdDbBlockTableRecord.cast(blockIter.getRecordId().safeOpenObject());
				OdDbObjectIterator entityIter = OdDbObjectIterator.cast(block.newIterator());
				for (; !entityIter.done(); entityIter.step()) {
					OdDbObject obj = entityIter.objectId().openObject();
					if (obj.isKindOf(OdDbBlockReference.desc())) {
						OdDbBlockReference blkRef = OdDbBlockReference.cast(obj);
						OdDbObjectIterator iter = blkRef.attributeIterator();
						for (; !iter.done(); iter.step()) {
							OdDbAttribute attr = OdDbAttribute
									.cast(iter.entity().objectId().openObject(OpenMode.kForWrite));
							if (OdDbAttribute.getCPtr(attr) != 0) {
								if(map.containsKey(attr.tag())){
									//将传入参数写入dwg并调整位置
									String value=map.get(attr.tag()).toString().trim();
									if(value.equals("0")){
										value="";
									}
									 attr.setTextString(value);
									 attr.adjustAlignment();
								}
								}
							
						}
					}
				}
			}
			oddb.writeFile(srcFileName, SaveType.kDwg, DwgVersion.vAC18, true);
			hostApp.delete();
		}
	
		/**
		 * 向dwg块中填入参数  上传到转图机修改
		 * @param srcFileName
		 * @param map
		 * @throws Exception
		 */
			public static void modifyDWGByWorker(String srcFileName,Map<String,String> map) throws Exception{
				String fileName=srcFileName.substring(srcFileName.lastIndexOf("/")+1, srcFileName.length());
				InputStream stream=new FileInputStream(srcFileName);
				WcFTPUtil.ftpUploadFile(ConstantLine.ftp_dir, fileName, stream);
				DwgWebserviceProxy helloPxy = new DwgWebserviceProxy();  
	 	        DwgWebservice service = helloPxy.getDwgWebservice();  
	 	        String jsonstr=JSONObject.fromObject(map).toString();
	 	        String returnstr=service.modifyDwgAndToPdf(ConstantLine.ftp_dir+File.separator+fileName, jsonstr);
	 	        if(returnstr==null){
	 	        	 WcFTPUtil.downloadFile(ConstantLine.ftp_dir, fileName, srcFileName);//dwg删除
	 	        }else{
	 	        	throw new LineException(returnstr);
	 	        }
				
				
			}
	/**
	 * 更新文档的dwg和同名pdf附件。下载dwg文件，写入签名后转成pdf。重新上传并删除临时文件
	 * @param donumber
	 * @param map
	 * @throws Exception
	 */
	public static void updateDocDwgAndPDF(String docnumber,Map<String,String> map) throws Exception{
    	WTDocument doc=CommonUtil.getLatestWTDocByNumber(docnumber);
    	if(doc==null){
    		throw new LineException(ConstantLine.exception_numberdocnotfound.replace("#",docnumber));
    	}
    	String tempfile=wt_home+PropertiesUtil.getValueByKey("dwg_temp_path");
    	ApplicationData appdata=WTDocumentUtil.downloadDocPrimaryDwg(doc,tempfile);
    	if(appdata!=null){
    		String dwgfilepath=tempfile+appdata.getFileName();
    		modifyDWGByWorker(dwgfilepath,map);
    		File dwgfile=new File(dwgfilepath);
    		FileInputStream fis=new FileInputStream(dwgfile);
    		WTDocumentUtil.replaceDocPrimaryContent(doc, fis, docnumber+".dwg",appdata.getFileSize());
    		DWGToPDF.execute(WCUtil.getOid(doc));
    		FileUtil.deleteFile(tempfile+docnumber+".dwg");
    	}
    	
    }
	
	/**
	 * 更新文档的dwg和同名pdf附件。重新上传并删除临时文件
	 * @param donumber
	 * @param map
	 * @throws Exception
	 */
		
	public static void updateDocDwgAndPDF(String docnumber,File dwgfile) throws Exception{
    	WTDocument doc=CommonUtil.getLatestWTDocByNumber(docnumber);
    	if(doc==null){
    		throw new LineException(ConstantLine.exception_numberdocnotfound.replace("#",docnumber));
    	}
    	WTDocumentUtil.replaceDocPrimaryContent(doc, dwgfile.getAbsolutePath(), docnumber+".dwg",dwgfile.length());
    	
    	FTPClient ftpClient = WcFTPUtil.getFtpClient();
    	String tempfile=wt_home+PropertiesUtil.getValueByKey("dwg_temp_path");
    	ApplicationData appdata=WTDocumentUtil.downloadDocPrimaryDwg(doc,tempfile);
    	if(appdata!=null){
    		String pdffilepath=tempfile+appdata.getFileName().replace(".dwg", ".pdf").replace(".DWG", ".pdf");
    		WcFTPUtil.sendWTDocToFTP(doc, ConstantLine.ftp_dir);
    		DwgWebserviceProxy helloPxy = new DwgWebserviceProxy();  
 	        DwgWebservice service = helloPxy.getDwgWebservice();  
 	        String returnstr=service.dwgtopdf(ConstantLine.ftp_dir+File.separator+dwgfile.getName());  
 	        if(returnstr==null){
 	        	 boolean isdownload=WcFTPUtil.downloadFile(ConstantLine.ftp_dir, docnumber+".pdf", tempfile+File.separator+docnumber+".pdf");//dwg删除
 	 	        WTDocumentUtil.updateAttachment(doc,docnumber+".pdf",tempfile+File.separator+docnumber+".pdf");
 	 	        if(isdownload){
 	 	 			FileUtil.deleteFile(pdffilepath);
 	 	 			FileUtil.deleteFile(tempfile+docnumber+".dwg");
 	 	 			ftpClient.deleteFile(ConstantLine.ftp_dir+docnumber+".pdf");
 	 	 		}
 	        }else{
 	        	throw new LineException(returnstr);
 	        }
 	       
    	}
    }
	
	/**
	 * 将docnumber编码的文档主内容更新参数后挂载主内容与附件
	 * @param donumber
	 * @param map
	 * @throws Exception
	 */
	public static void updateDocDwgAndPDF(WTDocument doc,Map<String,String> map,String newdocnumber) throws Exception{
    	WTDocument newdoc=CommonUtil.getLatestWTDocByNumber(newdocnumber);
    	String tempfile=wt_home+PropertiesUtil.getValueByKey("dwg_temp_path");
    	ApplicationData appdata=WTDocumentUtil.downloadDocPrimaryDwg(doc,tempfile,newdocnumber+".dwg");
    	FTPClient ftpClient = WcFTPUtil.getFtpClient();
    	if(appdata!=null){
    		String dwgfilepath=tempfile+newdocnumber+".dwg";
    		modifyDWGByWorker(dwgfilepath,map);
    		File dwgfile=new File(dwgfilepath);
    		FileInputStream fis=new FileInputStream(dwgfile);
    		WTDocumentUtil.replaceDocPrimaryContent(newdoc, fis, newdocnumber+".dwg",appdata.getFileSize());
    		/**********/
    		WcFTPUtil.sendWTDocToFTP(newdoc, ConstantLine.ftp_dir);
    		DwgWebserviceProxy helloPxy = new DwgWebserviceProxy();  
 	        DwgWebservice service = helloPxy.getDwgWebservice();  
 	        String returnstr=service.dwgtopdf(ConstantLine.ftp_dir+File.separator+dwgfile.getName());  
 	        if(returnstr==null){
 	        	 boolean isdownload=WcFTPUtil.downloadFile(ConstantLine.ftp_dir, newdocnumber+".pdf", tempfile+File.separator+newdocnumber+".pdf");//dwg删除
 		        WTDocumentUtil.updateAttachment(newdoc,newdocnumber+".pdf",tempfile+File.separator+newdocnumber+".pdf");
 	 	        if(isdownload){
 	    			String pdffilepath = tempfile+newdocnumber+".pdf";
 	    			FileUtil.deleteFile(pdffilepath);
 	    			FileUtil.deleteFile(tempfile+newdocnumber+".dwg");
 	    			ftpClient.deleteFile(ConstantLine.ftp_dir+newdocnumber+".pdf");
 	    		}
 	        }else{
 	        	throw new LineException(returnstr);
 	        }
	       
    	}
    }
	
}
