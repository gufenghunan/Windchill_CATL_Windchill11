package com.catl.ecad.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;

import com.ptc.prolog.pub.RunTimeException;

import wt.intersvrcom.CharsetHelper;
import wt.log4j.LogR;

public class FTPUitl {
	private final static String CLASSNAME=FTPUitl.class.getName();
	private static Logger logger = LogR.getLogger(CLASSNAME);
	
	public static FTPClient getFtpClient() throws IOException {
		FTPClient ftpClient = null;
		ftpClient = new FTPClient();
		try {
			ftpClient.connect(FTPConfigProperties.getFtpURL(), 21);
			boolean flag = ftpClient.login(FTPConfigProperties.getFtpUser(), FTPConfigProperties.getFtpPwd());
			
			
			if (!flag) {
				ftpClient.disconnect();
				System.err.println(CLASSNAME+"FTP服务器拒绝连接.");
				// System.exit(1);
			}
			return ftpClient;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		if (ftpClient == null) {
			//System.out.println(CLASSNAME+"*** FTP:连接异常，请检查FTP连接是否正确！***");
		}
		return null;
	}

	public static void ftpUploadFile(String path, String filename, InputStream is) throws Exception {
		////System.out.println("------------3");
		FTPClient ftpClient = null;
		try {
			//System.out.println(CLASSNAME+"准备连接FTP服务");
			ftpClient = getFtpClient();
			// if()
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			if(StringUtils.isNotBlank(path)){
				boolean flag = ftpClient.changeWorkingDirectory(path);
				if (!flag) {
					////System.out.println("创建文件夹");
					ftpClient = makeDirectory(ftpClient, path);
				}
			}
			
			
			//System.out.println(CLASSNAME+"FTP服务已连接成功");
			OutputStream os = ftpClient.storeFileStream(filename);

			////System.out.println("os===" + os);
			byte[] bytes = new byte[1024];
			int c;
			while ((c = is.read(bytes)) != -1) {
				os.write(bytes, 0, c);
			}
			is.close();
			os.close();
			ftpClient.disconnect();
			////System.out.println("ok");
		} catch (IOException ex) {
			ex.printStackTrace();
		}finally {			
			if (ftpClient !=null && ftpClient.isConnected()) {
				ftpClient.disconnect();
			}
		}
	}

	private static FTPClient makeDirectory(FTPClient ftpClient, String dirpath) throws

	IOException, RunTimeException {
		String[] paths = dirpath.split("/");
		for (int i = 0; i < paths.length; i++) {
			boolean flag = ftpClient.changeWorkingDirectory(paths[i]);
			if (!flag) {
				boolean bdir = ftpClient.makeDirectory(paths[i]);
				if (!bdir) {
					throw new RunTimeException(CLASSNAME+"创建不了目录" + paths[i]);
				}
				ftpClient.changeWorkingDirectory(paths[i]);
			}
		}
		return ftpClient;

	}

	public static InputStream getFileInputStream(String path, String filename) throws Exception {
		FTPClient ftpClient = null;
		InputStream is = null;
		try {
			logger.info("start to connect ftp");
			ftpClient = getFtpClient();
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			System.out.println("path\t"+path+"\tfilename:\t"+filename);
			if(StringUtils.isNotBlank(path)){
				ftpClient.changeWorkingDirectory(path);
			}
			ftpClient.enterLocalPassiveMode();
			
			FTPFile[] ff = ftpClient.listFiles(filename);
			//FTPFile[] ff = ftpClient.listFiles();
			String delfile = "";
			for (int i = 0; i < ff.length; i++) {
				delfile = new String(ff[i].getName().getBytes("ISO-8859-1"), CharsetHelper.getCharSetNameForSigning());
				System.out.println("FFName:\t"+ff[i].getName());
				if (filename.equals(delfile)) {
					
					is = ftpClient.retrieveFileStream(ff[i].getName());
					delfile=ff[i].getName();
					break;
				}
			}
			
			if (is == null) {
				delfile = new String(filename.getBytes(CharsetHelper.getCharSetNameForSigning()), "ISO-8859-1");
				is = ftpClient.retrieveFileStream(delfile);
			}
			
			if(is != null){
				System.out.println("Delete FTP file\t"+delfile);
				//ftpClient.deleteFile(delfile);
			}
			ftpClient.disconnect();
			return is;
		} catch (IOException ex) {
			ex.printStackTrace();
		}finally {			
			if (ftpClient !=null && ftpClient.isConnected()) {
				ftpClient.disconnect();
			}
		}
		return is;
	}
	
	public static boolean downloadFile(String path, String filename, String localpath) throws Exception {
		FTPClient ftpClient = null;
		boolean success = false;
		InputStream is = null;
		try {
			ftpClient = getFtpClient();
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			if(StringUtils.isNotBlank(path)){
				ftpClient.changeWorkingDirectory(path);
			}
			System.out.println(filename);
			System.out.println("localpath\t"+localpath);
			File localFile = new File(localpath);
			FileOutputStream fos = new FileOutputStream(localFile);
			ftpClient.enterLocalPassiveMode();
			
			FTPFile[] ff = ftpClient.listFiles(filename);
			//FTPFile[] ff = ftpClient.listFiles();
			String delfile = "";
			for (int i = 0; i < ff.length; i++) {
				delfile = new String(ff[i].getName().getBytes("ISO-8859-1"), CharsetHelper.getCharSetNameForSigning());
				if (filename.equals(delfile)) {
					success = ftpClient.retrieveFile(ff[i].getName(),fos);
					delfile=ff[i].getName();
					break;
				}
			}
			
			if (success == false) {
				delfile = new String(filename.getBytes(CharsetHelper.getCharSetNameForSigning()), "ISO-8859-1");
				success = ftpClient.retrieveFile(new String(filename.getBytes(CharsetHelper.getCharSetNameForSigning()), "ISO-8859-1"),fos);
			}
			
			if(success == true){
				System.out.println("Delete FTP file\t"+delfile);
				//ftpClient.deleteFile(delfile);
			}
			fos.close();
			ftpClient.disconnect();
			
			return success;
		} catch (IOException ex) {
			ex.printStackTrace();
		}finally {			
			if (ftpClient !=null && ftpClient.isConnected()) {
				ftpClient.disconnect();
			}
		}
		return success;
	}

	
}
