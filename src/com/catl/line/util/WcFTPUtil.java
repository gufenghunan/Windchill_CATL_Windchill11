package com.catl.line.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import com.catl.common.util.PropertiesUtil;
import com.catl.line.constant.ConstantLine;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.QueryResult;
import wt.fv.FvFileDoesNotExist;
import wt.intersvrcom.CharsetHelper;
import wt.method.RemoteAccess;
import wt.vc.VersionControlHelper;

/**
 * 发送文档到ftp上面的工具类
 * 
 * @author hdong
 *
 */
public class WcFTPUtil implements RemoteAccess {
	public static FTPClient getFtpClient() throws IOException {
		FTPClient ftpClient = null;
		ftpClient = new FTPClient();
		ftpClient.setControlEncoding("UTF-8");
		try {
			ftpClient.connect(PropertiesUtil.getValueByKey("line_ftp_url"), 21);
			boolean flag = ftpClient.login(ConstantLine.ftp_user,
					ConstantLine.ftp_pwd);
			if (!flag) {
				ftpClient.disconnect();
				System.err.println("FTP server refused connection.");
			}
			System.out.println("ftpClient is login");
			return ftpClient;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * 上传文件到ftp
	 * 
	 * @param path
	 * @param filename
	 * @param is
	 * @throws Exception
	 */
	public static void ftpUploadFile(String path, String filename,
			InputStream is) throws Exception {
		// //System.out.println("------------3");
		FTPClient ftpClient = null;
		try {
			// System.out.println(CLASSNAME+"准备连接FTP服务");
			ftpClient = getFtpClient();
			// if()
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			if (StringUtils.isNotBlank(path)) {
				boolean flag = ftpClient.changeWorkingDirectory(path);
				if (!flag) {
					// //System.out.println("创建文件夹");
					ftpClient = makeDirectory(ftpClient, path);
				}
			}

			// System.out.println(CLASSNAME+"FTP服务已连接成功");
			ftpClient.storeFile(filename, is);
			is.close();
			ftpClient.disconnect();
			// //System.out.println("ok");
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (ftpClient != null && ftpClient.isConnected()) {
				ftpClient.disconnect();
			}
		}
	}

	/**
	 * 上传文件到ftp
	 * 
	 * @param path
	 * @param filename
	 * @param is
	 * @throws Exception
	 */
	public static void ftpUploadFile(String path, String filename, File file)
			throws Exception {
		// //System.out.println("------------3");
		FTPClient ftpClient = null;
		try {
			// System.out.println(CLASSNAME+"准备连接FTP服务");
			InputStream is = new FileInputStream(file);
			ftpClient = getFtpClient();
			// if()
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			if (StringUtils.isNotBlank(path)) {
				boolean flag = ftpClient.changeWorkingDirectory(path);
				if (!flag) {
					// //System.out.println("创建文件夹");
					ftpClient = makeDirectory(ftpClient, path);
				}
			}

			// System.out.println(CLASSNAME+"FTP服务已连接成功");
			OutputStream os = ftpClient.storeFileStream(filename);

			// //System.out.println("os===" + os);
			byte[] bytes = new byte[1024];
			int c;
			while ((c = is.read(bytes)) != -1) {
				os.write(bytes, 0, c);
			}
			is.close();
			os.close();
			ftpClient.disconnect();
			// //System.out.println("ok");
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (ftpClient != null && ftpClient.isConnected()) {
				ftpClient.disconnect();
			}
		}
	}

	/**
	 * 创建ftp目录
	 * 
	 * @param ftpClient
	 * @param dirpath
	 * @return
	 * @throws Exception
	 */
	private static FTPClient makeDirectory(FTPClient ftpClient, String dirpath)
			throws Exception {
		String[] paths = dirpath.split("/");
		for (int i = 0; i < paths.length; i++) {
			boolean flag = ftpClient.changeWorkingDirectory(paths[i]);
			if (!flag) {
				boolean bdir = ftpClient.makeDirectory(paths[i]);
				if (!bdir) {
					throw new Exception("创建不了目录" + paths[i]);
				}
				ftpClient.changeWorkingDirectory(paths[i]);
			}
		}
		return ftpClient;
	}

	public static boolean downloadFile(String path, String filename,
			String localpath) throws Exception {
		FTPClient ftpClient = null;
		boolean success = false;
		try {
			ftpClient = getFtpClient();
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			if (StringUtils.isNotBlank(path)) {
				ftpClient.changeWorkingDirectory(path);
			}
			System.out.println(filename);
			File localFile = new File(localpath);
			FileOutputStream fos = new FileOutputStream(localFile);
			ftpClient.enterLocalPassiveMode();
			FTPFile[] ff = ftpClient.listFiles(filename);
			String delfile = "";
			for (int i = 0; i < ff.length; i++) {
				delfile = new String(ff[i].getName().getBytes("utf-8"),
						CharsetHelper.getCharSetNameForSigning());
				System.out.println("delfile--------------->file:" + delfile);
				System.out.println("filename--------------->file:" + filename);
				if (filename.equals(delfile)) {
					success = ftpClient.retrieveFile(ff[i].getName(), fos);
					delfile = ff[i].getName();
					break;
				}
			}
			if (success == true) {
				ftpClient.deleteFile(delfile);
			}
			fos.close();
			ftpClient.disconnect();

			return success;
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (ftpClient != null && ftpClient.isConnected()) {
				ftpClient.disconnect();
			}
		}
		return success;
	}

	/**
	 * 发送文档主内容到ftp
	 * 
	 * @param wtdoc
	 * @param ftpDir
	 */
	public static String sendWTDocToFTP(WTDocument wtdoc, String ftpDir) {
		try {
			QueryResult queryresult = ContentHelper.service.getContentsByRole(
					wtdoc, ContentRoleType.PRIMARY);
			while (queryresult.hasMoreElements()) {
				Object o = queryresult.nextElement();
				ApplicationData appdata = null;
				if (o instanceof ApplicationData) {
					appdata = (ApplicationData) o;
				}
				String filename = appdata.getFileName();
				InputStream inputstream = null;
				if (appdata != null) {
					try {
						inputstream = ContentServerHelper.service
								.findContentStream(appdata);
					} catch (FvFileDoesNotExist e) {
						System.out.println("系统电子仓库中找不到该文件：" + filename);
						inputstream = null;
					}
					if (inputstream != null) {
						ftpUploadFile(ftpDir, filename, inputstream);
						return filename;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
