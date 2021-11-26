package com.catl.line.transfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

/**
 * 发送文档到ftp上面的工具类
 * 
 * @author hdong
 *
 */
public class FTPUtil {
	public static FTPClient getFtpClient() throws IOException {
		FTPClient ftpClient = null;
		ftpClient = new FTPClient();
		ftpClient.setControlEncoding("UTF-8");
		try {
			ftpClient.connect(ConstantDwg.ftp_url, 21);
			boolean flag = ftpClient.login(ConstantDwg.ftp_user,
					ConstantDwg.ftp_pwd);
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
		FTPClient ftpClient = null;
		try {
			ftpClient = getFtpClient();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			boolean flag = ftpClient.changeWorkingDirectory(path);
			if (!flag) {
				ftpClient = makeDirectory(ftpClient, path);
			}
			OutputStream os = ftpClient.storeFileStream(filename);
			byte[] bytes = new byte[1024];
			int c;
			while ((c = is.read(bytes)) != -1) {
				os.write(bytes, 0, c);
			}
			is.close();
			os.flush();
			os.close();
			ftpClient.disconnect();
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
		FTPClient ftpClient = null;
		if (file.exists()) {
			InputStream is = new FileInputStream(file);
			try {
				ftpClient = getFtpClient();
				ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
				boolean flag = ftpClient.changeWorkingDirectory(path);
				if (!flag) {
					ftpClient = makeDirectory(ftpClient, path);
				}
				OutputStream os = ftpClient.storeFileStream(filename);
				byte[] bytes = new byte[1024];
				int c;
				while ((c = is.read(bytes)) != -1) {
					os.write(bytes, 0, c);
				}
				is.close();
				os.flush();
				os.close();
				ftpClient.disconnect();
			} catch (IOException ex) {
				ex.printStackTrace();
			} finally {
				if (ftpClient != null && ftpClient.isConnected()) {
					ftpClient.disconnect();
				}
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
		InputStream is = null;
		try {
			ftpClient = getFtpClient();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			ftpClient.changeWorkingDirectory(path);
			System.out.println("filename\t" + filename);
			System.out.println("localpath\t" + localpath);
			File localFile = new File(localpath);
			FileOutputStream fos = new FileOutputStream(localFile);

			FTPFile[] ff = ftpClient.listFiles();
			String delfile = "";
			for (int i = 0; i < ff.length; i++) {
				delfile = new String(ff[i].getName().getBytes("GBK"));
				System.out.println("file\t" + delfile);
				if (filename.equals(delfile)) {
					System.out.println("ff[i].getName()" + ff[i].getName());
					success = ftpClient.retrieveFile(ff[i].getName(), fos);
					delfile = ff[i].getName();
					break;
				}
			}

			if (success == true) {
				System.out.println("delete file\t" + delfile);
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

}
