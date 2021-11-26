package com.catl.line.queue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;

import wt.admin.AdministrativeDomainHelper;
import wt.content.ApplicationData;
import wt.doc.WTDocument;
import wt.method.MethodContext;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.session.SessionAuthenticator;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTProperties;

import com.catl.common.util.PropertiesUtil;
import com.catl.ecad.utils.CommonUtil;
import com.catl.line.constant.ConstantLine;
import com.catl.line.exception.LineException;
import com.catl.line.util.FileUtil;
import com.catl.line.util.WCUtil;
import com.catl.line.util.WTDocumentUtil;
import com.catl.line.util.WcFTPUtil;
import com.catl.line.webservice.DwgWebservice;
import com.catl.line.webservice.DwgWebserviceProxy;

public class DWGToPDF {
	private static String wt_home;
	static {
		try {
			wt_home = WTProperties.getLocalProperties().getProperty("wt.home",
					"UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 转图队列执行的转图方法
	 * 满足转图的编码格式  有dwg主内容
	 * @param oid
	 * @throws Exception
	 */
	public static void execute(String oid) throws Exception {
		SessionServerHelper.manager.setAccessEnforced(false);
		try{
		WTDocument doc = (WTDocument) WCUtil.getWTObject(oid);
		doc=CommonUtil.getLatestWTDocByNumber(doc.getNumber());
		ApplicationData app=WTDocumentUtil.getDocPrimaryApplicationData(doc);
		String prefix_number=doc.getNumber();
		if(doc.getNumber().length()>4){
			prefix_number=prefix_number.substring(0, 4);
		}
		String topdf_prefix=PropertiesUtil.getValueByKey("config_topdf_prefix");
		String [] prefix=topdf_prefix.split(",");
		List prefixlist=Arrays.asList(prefix);
		if(prefixlist.contains(prefix_number)&&app!=null&&app.getFileName().toLowerCase().endsWith(".dwg")){
			FTPClient ftpClient = WcFTPUtil.getFtpClient();
			String path = wt_home + PropertiesUtil.getValueByKey("dwg_temp_path");
			FileUtil.createDir(path);
			String filename = WcFTPUtil.sendWTDocToFTP(doc, ConstantLine.ftp_dir);
			String r=null;
			if (filename != null) {
				DwgWebserviceProxy helloPxy = new DwgWebserviceProxy();
				DwgWebservice service = helloPxy.getDwgWebservice();
				 r= service.dwgtopdf(ConstantLine.ftp_dir + File.separator
						+ filename);
				if(r==null){
					String pdfFileName = filename.replace(".dwg", ".pdf");
					String pdfFilePath = path + File.separator + pdfFileName;
					boolean isdownload = WcFTPUtil.downloadFile(ConstantLine.ftp_dir,
							pdfFileName, pdfFilePath);// dwg删除
					if (isdownload) {
						WTDocumentUtil.updateAttachment(doc, pdfFileName, pdfFilePath);
						String pdffilepath = path + pdfFileName;
						FileUtil.deleteFile(pdffilepath);
						ftpClient.deleteFile(ConstantLine.ftp_dir + File.separator
								+ pdfFileName);
					}
				}else{
					throw new LineException(r);
				}
				
			}
		}
		}catch(Exception e){
			throw e;
		}finally{
			SessionServerHelper.manager.setAccessEnforced(true);
		}

	}

}