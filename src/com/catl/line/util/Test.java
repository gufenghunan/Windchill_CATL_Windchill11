package com.catl.line.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;

import com.catl.common.util.PropertiesUtil;
import com.catl.ecad.utils.FTPUitl;
import com.catl.line.constant.ConstantLine;
import com.catl.line.exception.LineException;
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

import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.intersvrcom.CharsetHelper;
import wt.log4j.LogR;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTRuntimeException;

public class Test {
	private final static String CLASSNAME = Test.class.getName();
	private static Logger logger = LogR.getLogger(CLASSNAME);

	private static String wt_home;
	static {
		try {
			wt_home = WTProperties.getLocalProperties().getProperty("wt.home",
					"UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		Map<String, String> map = new HashMap<>();
		modifyDWGByWorker("/data/测试.dwg", map);
	}

	public static WTPartDescribeLink isWTPartDescDoc(WTPart wtPart,
			WTDocument wtDocument) throws WTException {
		QueryResult qr = PersistenceHelper.manager.navigate(wtPart,
				WTPartDescribeLink.DESCRIBED_BY_ROLE, WTPartDescribeLink.class,
				false);
		while (qr.hasMoreElements()) {
			WTPartDescribeLink link = (WTPartDescribeLink) qr.nextElement();
			return link;
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
			ftpClient = WcFTPUtil.getFtpClient();
			// if()
			// ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			if (StringUtils.isNotBlank(path)) {
				boolean flag = ftpClient.changeWorkingDirectory(path);
				if (!flag) {
					// //System.out.println("创建文件夹");
					ftpClient = makeDirectory(ftpClient, path);
				}
			}

			// System.out.println(CLASSNAME+"FTP服务已连接成功");
			// OutputStream os = ftpClient.storeFileStream(filename);
			ftpClient.storeFile(filename, is);

			// //System.out.println("os===" + os);
			// byte[] bytes = new byte[1024];
			// int c;
			// while ((c = is.read(bytes)) != -1) {
			// os.write(bytes, 0, c);
			// }
			is.close();
			// os.close();
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
			ftpClient = WcFTPUtil.getFtpClient();
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
				System.out.println("delfile--------------->ff:" + delfile);
				System.out.println("filename--------------->ff:" + filename);
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
	 * 向dwg块中填入参数
	 * 
	 * @param srcFileName
	 * @param map
	 * @throws Exception
	 */
	public static void modifyDWGByWorker(String srcFileName,
			Map<String, String> map) throws Exception {

		String fileName = srcFileName.substring(
				srcFileName.lastIndexOf("/") + 1, srcFileName.length());
		InputStream stream = new FileInputStream(srcFileName);
		WcFTPUtil.ftpUploadFile(ConstantLine.ftp_dir, fileName, stream);

		for (Iterator it = (Iterator) map.keySet().iterator(); it.hasNext();) {
			String str = (String) it.next();
		}
		ExSystemServices systemServices = new ExSystemServices();
		ExHostAppServices hostApp = new ExHostAppServices();
		hostApp.disableOutput(true);
		TD_Db.odInitialize(systemServices);
		OdDbDatabase oddb = null;// 数据库
		if (!hostApp.findFile(ConstantLine.ftp_dir + "/" + fileName).isEmpty()) {
			try {
				oddb = hostApp.readFile(ConstantLine.ftp_dir + "/" + fileName);// 源文件不为空，读取源文件
			} catch (Exception e) {
				throw new LineException("不识别的图纸,请检查图纸是否损坏");
			}
		}
		OdDbBlockTable blockTable = OdDbBlockTable.cast(oddb.getBlockTableId()
				.safeOpenObject());
		// 块集合
		OdDbSymbolTableIterator blockIter = OdDbSymbolTableIterator
				.cast(blockTable.newIterator());
		for (blockIter.start(); !blockIter.done(); blockIter.step()) {
			OdDbBlockTableRecord block = OdDbBlockTableRecord.cast(blockIter
					.getRecordId().safeOpenObject());
			OdDbObjectIterator entityIter = OdDbObjectIterator.cast(block
					.newIterator());
			for (; !entityIter.done(); entityIter.step()) {
				OdDbObject obj = entityIter.objectId().openObject();
				if (obj.isKindOf(OdDbBlockReference.desc())) {
					OdDbBlockReference blkRef = OdDbBlockReference.cast(obj);
					OdDbObjectIterator iter = blkRef.attributeIterator();
					for (; !iter.done(); iter.step()) {
						OdDbAttribute attr = OdDbAttribute.cast(iter.entity()
								.objectId().openObject(OpenMode.kForWrite));
						if (OdDbAttribute.getCPtr(attr) != 0) {
							if (map.containsKey(attr.tag())) {
								// 将传入参数写入dwg并调整位置
								String value = map.get(attr.tag()).toString()
										.trim();
								if (value.equals("0")) {
									value = "";
								}
								attr.setTextString(value);
								attr.adjustAlignment();
							}
						}

					}
				}
			}
		}
		oddb.writeFile(ConstantLine.ftp_dir + "/" + fileName, SaveType.kDwg,
				DwgVersion.vAC18, true);
		hostApp.delete();
	}
}
