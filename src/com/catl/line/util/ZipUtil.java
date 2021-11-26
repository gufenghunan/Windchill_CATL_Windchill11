package com.catl.line.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

import com.catl.common.util.PropertiesUtil;
import com.catl.line.constant.ConstantLine;
import com.catl.line.entity.UploadMsg;
import com.catl.line.exception.LineException;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;

import wt.access.AccessControlHelper;
import wt.access.AccessPermission;
import wt.doc.WTDocument;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.vc.wip.WorkInProgressHelper;

public class ZipUtil {
	/** 使用GBK编码可以避免压缩中文文件名乱码 */
	private static final String CHINESE_CHARSET = "GBK";
	/** 文件读取缓冲区大小 */
	private static final int CACHE_SIZE = 1024;

	/**
	 * zip解压缩
	 * 验证上传的文件
	 * @param zipfile File 需要解压缩的文件
	 * @param descDir String 解压后的目标目录
	 * @throws WTException
	 */
	public static List unZipFile(File zipfile, String descDir,
			String contenttype) throws WTException {
		List msgs = new ArrayList();
		try {
			ZipFile zf = new ZipFile(zipfile, CHINESE_CHARSET);
			for (Enumeration entries = zf.getEntries(); entries.hasMoreElements();) {
				UploadMsg msg = new UploadMsg();
				ZipEntry entry = ((ZipEntry) entries.nextElement());
				String zipEntryName = entry.getName();
				msg.setName(zipEntryName);
				msg.setStatus(true);
				if (zipEntryName.lastIndexOf(".") > -1) {
					msg.setNumber(zipEntryName.substring(0,
							zipEntryName.lastIndexOf(".")));
					String suffix = zipEntryName.substring(
							zipEntryName.lastIndexOf(".") + 1,
							zipEntryName.length());
					if (!suffix.equals(contenttype)) {
						msg.setMsg("压缩包中包含不符合要求的文件类型");
						msg.setStatus(false);
						msgs.add(msg);
						continue;
					}
				} else {
					msg.setMsg("文件名不正确");
					msg.setStatus(false);
					msgs.add(msg);
					continue;
				}
				//change by hdong
				String number = msg.getNumber();
				String[] prefix = PropertiesUtil.getValueByKey("config_topdf_prefix").split(",");
				List prefixs = Arrays.asList(prefix);
				if(number.length()>4){
					number = number.substring(0,4);
				}
				if (!prefixs.contains(number)) {
					msg.setMsg("只允许【" + PropertiesUtil.getValueByKey("config_topdf_prefix")+ "】开头的物料组批量创建文档!");
					msg.setStatus(false);
					msgs.add(msg);
					continue;
				}
				number=msg.getNumber();
				WTPart part = CommonUtil.getLatestWTpartByNumber(number);
				WTDocument document = CommonUtil.getLatestWTDocByNumber(number);
				if (part == null) {
					msg.setMsg("找不到相同编号的零部件");
					msg.setStatus(false);
					msgs.add(msg);
					continue;
				} else if (document != null) {
					msg.setMsg("更新【" + number + "】文档");
					msg.setStatus(true);
					if (!(document.getLifeCycleState().toString().equals(ConstantLine.state_design)||document.getLifeCycleState().toString().equals(ConstantLine.state_desginmodify))){
						msg.setMsg("只能更新设计或设计修改状态文档");
						msg.setStatus(false);
						msgs.add(msg);
						continue;
					}
				} else {
					if(document==null){//新建文档
						if (!(part.getLifeCycleState().toString().equals(ConstantLine.state_design)||part.getLifeCycleState().toString().equals(ConstantLine.state_desginmodify))){
							msg.setMsg("只能创建设计或设计修改状态部件的说明文档");
							msg.setStatus(false);
							msgs.add(msg);
							continue;
						}else if(WorkInProgressHelper.isCheckedOut(part)){
							msg.setMsg("部件处于检出状态");
							msg.setStatus(false);
							msgs.add(msg);
							continue;
						}
						WTUser user = (WTUser) SessionHelper.getPrincipal();
						SessionServerHelper.manager.setAccessEnforced(true);
						boolean flag = AccessControlHelper.manager.hasAccess(user,part, AccessPermission.MODIFY);
						SessionServerHelper.manager.setAccessEnforced(false);
						if (!flag) {
							msg.setMsg("没有权限创建部件的说明文档");
							msg.setStatus(false);
							msgs.add(msg);
							continue;
						}
					}
				}
				msgs.add(msg);
				InputStream in = zf.getInputStream(entry);
				OutputStream out = new FileOutputStream(descDir + zipEntryName);
				byte[] buf1 = new byte[1024];
				int len;
				while ((len = in.read(buf1)) > 0) {
					out.write(buf1, 0, len);
				}
				in.close();
				out.close();

			}
			zf.close();
			zipfile.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return msgs;
	}

	public static void main(String[] args) {
		File localfile = new File("D://aa.gif");
		String zipEntryName = "aa.gif";
		String n = zipEntryName.substring(zipEntryName.lastIndexOf(".") + 1,
				zipEntryName.length());
		System.out.println(n);
	}

	public static boolean fileToZip(String sourceFilePath, String zipFilePath,
			String fileName) {
		boolean flag = false;
		File sourceFile = new File(sourceFilePath);
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		FileOutputStream fos = null;
		ZipOutputStream zos = null;

		if (sourceFile.exists() == false) {
			throw new LineException("待压缩的文件目录：" + sourceFilePath + "不存在.");
		} else {
			try {
				File zipFile = new File(zipFilePath + fileName + ".zip");
				if (zipFile.exists()) {
					zipFile.delete();
				}
				File[] sourceFiles = sourceFile.listFiles();
				if (null == sourceFiles || sourceFiles.length < 1) {
					throw new LineException("待压缩的文件目录：" + sourceFilePath
							+ "里面不存在文件，无需压缩.");
				} else {
					fos = new FileOutputStream(zipFile);
					zos = new ZipOutputStream(new BufferedOutputStream(fos));
					zos.setEncoding("GBK");
					byte[] bufs = new byte[1024 * 10];
					for (int i = 0; i < sourceFiles.length; i++) {
						// 创建ZIP实体，并添加进压缩包
						ZipEntry zipEntry = new ZipEntry(
								sourceFiles[i].getName());
						zos.putNextEntry(zipEntry);
						// 读取待压缩的文件并写进压缩包里
						fis = new FileInputStream(sourceFiles[i]);
						bis = new BufferedInputStream(fis, 1024 * 10);
						int read = 0;
						while ((read = bis.read(bufs, 0, 1024 * 10)) != -1) {
							zos.write(bufs, 0, read);
						}
					}
					flag = true;
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				// 关闭流
				try {
					if (null != bis)
						bis.close();
					if (null != zos)
						zos.close();
				} catch (IOException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		}
		return flag;
	}

}