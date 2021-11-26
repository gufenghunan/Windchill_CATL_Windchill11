package com.catl.ecad.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.doc.WTDocumentUsageLink;
import wt.epm.EPMDocument;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.folder.Folder;
import wt.inf.container.WTContainer;
import wt.inf.library.WTLibrary;
import wt.load.LoadServerHelper;
import wt.org.OrganizationServicesHelper;
import wt.org.WTUser;
import wt.part.Quantity;
import wt.part.WTPart;
import wt.part.WTPartConfigSpec;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartStandardConfigSpec;
import wt.part.WTPartUsageLink;
import wt.pdmlink.PDMLinkProduct;
import wt.pds.StatementSpec;
import wt.query.ClassAttribute;
import wt.query.ConditionsClause;
import wt.query.ConstantExpression;
import wt.query.OrderBy;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.TableColumn;
import wt.series.Series;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.vc.Iterated;
import wt.vc.IterationIdentifier;
import wt.vc.Mastered;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;
import wt.vc.config.LatestConfigSpec;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;
import wt.vc.wip.CheckoutLink;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

/**
 * Created by s14918 on 2016/8/10.
 */
public class CommonUtil {

	public static String WT_TEMP; // 存储路径

	static {
		try {
			WT_TEMP = WTProperties.getLocalProperties().getProperty("wt.temp");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Logger logger = Logger.getLogger(CommonUtil.class.getName());

	public static WTPart getLatestWTpartByNumber(String number) throws WTException {
		WTPart wtpart = null;
		// check number
		if (number == null || "".equals(number = number.trim())) {
			return wtpart;
		}
		QuerySpec qs = new QuerySpec(WTPart.class);
		qs.appendWhere(
				new SearchCondition(WTPart.class, "master>number", SearchCondition.EQUAL, number.toUpperCase(), false),
				new int[] { 0 });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WTPart.class, "iterationInfo.latest", SearchCondition.IS_TRUE),
				new int[] { 0 });
		qs.appendOrderBy(new OrderBy(new ClassAttribute(WTPart.class, "versionInfo.identifier.versionSortId"), true),
				new int[] { 0 });

		QueryResult qr = PersistenceServerHelper.manager.query(qs);
		wtpart = qr.hasMoreElements() ? (WTPart) qr.nextElement() : null;

		return wtpart;
	}

	public static String getFolderPath(String category_for_procurement) {
		boolean flag = false;
		String path = "/Default";
		int prev = 0;
		for (int i = 0; i < category_for_procurement.length(); i++) {
			int c = category_for_procurement.charAt(i);
			if (48 <= c && c <= 57 && flag == false || i == category_for_procurement.length() - 1) {
				flag = true;
				if (i == category_for_procurement.length() - 1) {
					path = path + "/" + category_for_procurement.substring(prev, i + 1);
				} else if (i != 0) {
					path = path + "/" + category_for_procurement.substring(prev, i);
				}
				prev = i;

			} else {
				flag = false;
			}
		}
		return path;

	}

	public static WTDocument getLatestWTDocByNumber(String number) throws WTException {
		WTDocument wtdoc = null;
		// check number
		if (number == null || "".equals(number = number.trim())) {
			return wtdoc;
		}
		QuerySpec qs = new QuerySpec(WTDocument.class);
		qs.appendWhere(new SearchCondition(WTDocument.class, "master>number", SearchCondition.EQUAL,
				number.toUpperCase(), false), new int[] { 0 });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WTDocument.class, "iterationInfo.latest", SearchCondition.IS_TRUE),
				new int[] { 0 });
		qs.appendOrderBy(
				new OrderBy(new ClassAttribute(WTDocument.class, "versionInfo.identifier.versionSortId"), true),
				new int[] { 0 });

		QueryResult qr = PersistenceServerHelper.manager.query(qs);
		wtdoc = qr.hasMoreElements() ? (WTDocument) qr.nextElement() : null;

		return wtdoc;
	}

	public static String downloadContent(ContentHolder cHolder, ContentRoleType crType, String folderName) {
		String fileName = null;
		try {
			// holder1 = ContentHelper.service.getContents(doc);
			QueryResult qr = ContentHelper.service.getContentsByRole(cHolder, crType);
			if (qr.hasMoreElements()) {
				ApplicationData app1 = (ApplicationData) qr.nextElement();
				fileName = app1.getFileName();
				if (cHolder instanceof EPMDocument) {
					fileName = ((EPMDocument) cHolder).getCADName();
				}
				ContentServerHelper.service.writeContentStream(app1, folderName + File.separator + fileName);// 下载主内容至目录�??
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileName;

	}

	public static Object getLatestVersionOf(Mastered master) throws WTException {
		QueryResult qr = VersionControlHelper.service.allVersionsOf(master);
		return qr.hasMoreElements() ? qr.nextElement() : null;
	}

	/**
	 * 压缩指定的单个或多个文件，如果是目录，则遍历目录下所有文件进行压�??
	 *
	 * @param fromFile
	 *            要压缩的文件或�?�目�??
	 * @param zipFileName
	 *            ZIP文件名包含全路径
	 */
	public static boolean zipFile(String fromFile, String zipFileName) {
		ZipOutputStream out = null;
		boolean retValue = false;
		try {
			// 取得压缩文件�??
			File file = new File(fromFile);
			if (!file.exists()) {
				return retValue;
			}
			// 取得压缩文件名称
			String fName = StringUtils.substringBeforeLast(fromFile, File.separator);
			// createDir(zipFileName);
			out = new ZipOutputStream(new FileOutputStream(zipFileName));
			// 压缩文件
			zip(out, file, file.getName());

			out.close(); // 输出流关�??
			out = null;
			retValue = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				out = null;
			}
		}
		return retValue;
	}

	/**
	 * 执行压缩
	 *
	 * @param out
	 *            ZIP输入�??
	 * @param file
	 *            被压缩的文件
	 * @param base
	 *            被压缩的文件�??
	 */
	private static void zip(ZipOutputStream out, File file, String base) {
		FileInputStream in = null;
		try {
			if (file.isDirectory()) {// 压缩目录
				File[] fl = file.listFiles();
				if (fl.length == 0) {
					out.putNextEntry(new ZipEntry(base + File.separator)); // 创建zip实体
				}
				for (int i = 0; i < fl.length; i++) {
					zip(out, fl[i], base + File.separator + fl[i].getName()); // 递归遍历子文件夹
				}
			} else { // 压缩单个文件
				out.putNextEntry(new ZipEntry(base)); // 创建zip实体
				in = new FileInputStream(file);
				BufferedInputStream bi = new BufferedInputStream(in);
				int b;
				while ((b = bi.read()) != -1) {
					out.write(b); // 将字节流写入当前zip目录
				}
				out.closeEntry(); // 关闭zip实体
				in.close(); // 输入流关闭
				in = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				in = null;
			}
		}
	}

	/**
	 * 递归删除目录下的�??有文件及子目录下�??有文�??
	 *
	 * @param file
	 *            将要删除的文件目�??
	 * @return boolean
	 */
	public static boolean deleteFile(File file) {
		if (file.isDirectory()) {
			String[] children = file.list();
			// 递归删除目录中的子目录下
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteFile(new File(file, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// 目录此时为空，可以删除
		return file.delete();
	}

	public static HttpServletResponse downloadAsStream(String path, HttpServletResponse response) {
		try {
			// path是指欲下载的文件的路径
			File file = new File(path);
			// 取得文件名
			String filename = file.getName();
			// 取得文件的后缀名
			String ext = filename.substring(filename.lastIndexOf(".") + 1).toUpperCase();

			// 以流的形式下载文件
			InputStream fis = new BufferedInputStream(new FileInputStream(path));
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
			fis.close();
			// 清空response
			response.reset();
			// 设置response的Header
			response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes()));
			response.addHeader("Content-Length", "" + file.length());
			OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
			response.setContentType("application/octet-stream");
			toClient.write(buffer);
			toClient.flush();
			toClient.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return response;
	}

	public static HttpServletResponse download(String path, HttpServletResponse response) {
		try {
			// path是指欲下载的文件的路径
			File file = new File(path);
			if (!file.exists()) {
				response.sendError(404, "File not found!");
				return response;
			}
			// 取得文件名
			String filename = file.getName();

			// 设置response
			response.reset();
			response.setContentType("application/x-msdownload");
			response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));

			// 以流的形式下载文件
			BufferedInputStream br = new BufferedInputStream(new FileInputStream(path));
			byte[] buf = new byte[1024];
			int len = 0;

			OutputStream out = response.getOutputStream();
			while ((len = br.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			br.close();
			out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return response;
	}

	public static PrintWriter getLogWriter(String path) throws IOException {
		// return new PrintWriter(new FileWriter(new File(path)), true);
		return new PrintWriter(new OutputStreamWriter(new FileOutputStream(path), "utf-8"), true);
	}

	public static String getFileName(String filename) {
		return StringUtils.replace(filename, File.separator, "-");
	}

	public static Workable checkinObject(Workable obj, String comment) throws WTException, WTPropertyVetoException {
		// check parameters
		if (obj == null) {
			return obj;
		}
		// is check out
		if (WorkInProgressHelper.isCheckedOut(obj)) {
			if (WorkInProgressHelper.isWorkingCopy(obj)) {
				// check in object
				obj = WorkInProgressHelper.service.checkin(obj, comment);
			}
		}
		obj = (Workable) PersistenceHelper.manager.refresh(obj);
		return obj;
	}

	public static Workable checkoutObject(Workable obj) throws WTException, WTPropertyVetoException {
		// check parameters
		if (obj == null) {
			return obj;
		}
		// is check out
		if (WorkInProgressHelper.isCheckedOut(obj)) {
			if (!WorkInProgressHelper.isWorkingCopy(obj)) {
				obj = WorkInProgressHelper.service.workingCopyOf(obj);
			}
		} else {
			Folder folder = WorkInProgressHelper.service.getCheckoutFolder();
			obj = (Workable) VersionControlHelper.service.getLatestIteration(obj, false);
			CheckoutLink cl = WorkInProgressHelper.service.checkout(obj, folder, null);
			obj = cl.getWorkingCopy();
		}
		obj = (Workable) PersistenceHelper.manager.refresh(obj);
		return obj;
	}

	public static QueryResult getProductByName(String prodName) throws WTException {
		QuerySpec qs = new QuerySpec(PDMLinkProduct.class);
		if (prodName != null && !((prodName = prodName.trim()).isEmpty())) {
			qs.appendWhere(new SearchCondition(PDMLinkProduct.class, "product>name", SearchCondition.EQUAL, prodName),
					new int[] { 0 });
		}
		// 按名称升序
		qs.appendOrderBy(new OrderBy(new ClassAttribute(PDMLinkProduct.class, PDMLinkProduct.NAME), false),
				new int[] { 0 });
		return PersistenceHelper.manager.find((StatementSpec) qs);
	}

	public static WTContainer getLibraryByName(String libName) throws WTException {
		QuerySpec qs = new QuerySpec(WTLibrary.class);
		if (libName != null) {
			qs.appendWhere(new SearchCondition(WTLibrary.class, "containerInfo.name", SearchCondition.EQUAL, libName),
					new int[] { 0 });
		}
		// 按名称升序
		qs.appendOrderBy(new OrderBy(new ClassAttribute(WTLibrary.class, WTLibrary.NAME), false), new int[] { 0 });
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		if (qr.size() > 0) {
			return (WTContainer) qr.nextElement();
		}
		return null;
	}

	public static WTUser getUserByName(String name) {
		WTUser user = null;
		try {
			QuerySpec qs = new QuerySpec(WTUser.class);
			SearchCondition sc = new SearchCondition(WTUser.class, WTUser.NAME, SearchCondition.EQUAL, name, false);
			qs.appendSearchCondition(sc);

			QueryResult qr = PersistenceHelper.manager.find(qs);
			if (qr.hasMoreElements()) {
				user = (WTUser) qr.nextElement();
			}
			if (user == null) {
				user = OrganizationServicesHelper.manager.getUser(name);
			}
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return user;
	}

	public static WTContainer getProduct(String name) {
		WTContainer wtc = null;
		QuerySpec qs;
		try {
			qs = new QuerySpec(PDMLinkProduct.class);
			SearchCondition sc = new SearchCondition(PDMLinkProduct.class, PDMLinkProduct.NAME, SearchCondition.EQUAL,
					name);
			qs.appendWhere(sc);
			// System.out.println(qs);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements())
				wtc = (WTContainer) qr.nextElement();
			return wtc;

		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 通过名称查找存储库上下文是否存在
	 * 
	 * @param name
	 * @return
	 * @throws WTException
	 */
	public static WTContainer getLibrary(String name) throws WTException {
		WTContainer wtc = null;
		QuerySpec qs = new QuerySpec(WTLibrary.class);
		SearchCondition sc = new SearchCondition(WTLibrary.class, WTLibrary.NAME, SearchCondition.EQUAL, name);
		qs.appendWhere(sc);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		// System.out.println("............................\t" + qr.size());
		if (qr.hasMoreElements()) {
			wtc = (WTContainer) qr.nextElement();
		}

		return wtc;
	}

	/**
	 * 设置部件小版�??.
	 * 
	 * @param iterated
	 *            部件
	 * @param iteration
	 *            版本
	 * @throws WTException
	 *             WTException
	 */
	public static void setIteration(final Iterated iterated, final String iteration) throws WTException {
		try {
			if (iteration != null) {
				Series ser = Series.newSeries("wt.vc.IterationIdentifier", iteration);
				IterationIdentifier iid = IterationIdentifier.newIterationIdentifier(ser);
				VersionControlHelper.setIterationIdentifier(iterated, iid);
			}
		} catch (WTPropertyVetoException e) {
			LoadServerHelper.printMessage("\nsetIteration: " + e.getMessage());
			e.printStackTrace();
			throw new WTException(e);
		}
	}

	public static WTDocument getWTDocumentByNumber(String number) throws WTException {
		WTDocument wt = null;
		QuerySpec qs = new QuerySpec(WTDocument.class);
		SearchCondition sc = new SearchCondition(WTDocument.class, WTDocument.NUMBER, "=", number);
		qs.appendWhere(sc);
		LatestConfigSpec lcs = new LatestConfigSpec();
		QueryResult qr = PersistenceHelper.manager.find(qs);
		qr = lcs.process(qr);
		while (qr.hasMoreElements()) {
			wt = (WTDocument) qr.nextElement();
			return wt;
		}
		return null;
	}

	public static EPMDocument getEPMDocumentByNumber(String number) throws WTException {
		EPMDocument epm = null;
		QuerySpec qs = new QuerySpec(EPMDocument.class);
		SearchCondition sc = new SearchCondition(EPMDocument.class, EPMDocument.NUMBER, "=", number);
		qs.appendWhere(sc);
		LatestConfigSpec lcs = new LatestConfigSpec();
		QueryResult qr = PersistenceHelper.manager.find(qs);
		qr = lcs.process(qr);
		while (qr.hasMoreElements()) {
			epm = (EPMDocument) qr.nextElement();
			return epm;
		}
		return null;
	}

	public static ArrayList getRefDocByPart(WTPart part) {
		ArrayList results = new ArrayList();

		try {
			QueryResult qr = WTPartHelper.service.getReferencesWTDocumentMasters(part);
			while (qr.hasMoreElements()) {
				Object tempObj = qr.nextElement();
				if ((tempObj instanceof WTDocumentMaster)) {
					WTDocument doc = (WTDocument) getLatestVersionOf((WTDocumentMaster) tempObj);
					results.add(doc);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return results;
	}

	public static ArrayList getDescDocsByPart(WTPart part) {
		ArrayList results = new ArrayList();

		try {
			QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(part);
			while (qr.hasMoreElements()) {
				Object tempObj = qr.nextElement();
				if ((tempObj instanceof WTDocument))
					results.add((WTDocument) tempObj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}

	public static WTPart getPartByNumber(String number) throws WTException {
		WTPart wt = null;
		QuerySpec qs = new QuerySpec(WTPart.class);
		SearchCondition sc = new SearchCondition(WTPart.class, WTPart.NUMBER, "=", number);
		qs.appendWhere(sc);
		LatestConfigSpec lcs = new LatestConfigSpec();
		QueryResult qr = PersistenceHelper.manager.find(qs);
		qr = lcs.process(qr);
		if (qr.hasMoreElements()) {
			wt = (WTPart) qr.nextElement();
		}
		return wt;
	}

	public static List getAllChildParts(List parts, WTPart part, String viewname)
			throws WTException, WTPropertyVetoException {
		wt.fc.Persistable apersistable[] = null;
		WTPartStandardConfigSpec stdSpec = WTPartStandardConfigSpec.newWTPartStandardConfigSpec();
		View view = ViewHelper.service.getView(part.getViewName());
		stdSpec.setView(view);
		WTPartConfigSpec configSpec = WTPartConfigSpec.newWTPartConfigSpec(stdSpec);
		QueryResult qr = WTPartHelper.service.getUsesWTParts(part, configSpec);
		while (qr.hasMoreElements()) {
			apersistable = (wt.fc.Persistable[]) qr.nextElement();
			WTPart cpart = null;
			WTPartMaster cpartmaster = null;
			if (apersistable[1] instanceof WTPartMaster) {
				cpartmaster = (WTPartMaster) apersistable[1];
				cpart = (WTPart) getLatestVersionOf(cpartmaster);
			} else {
				cpart = (WTPart) apersistable[1];
			}
			parts.add(cpart);
			getAllChildParts(parts, cpart, viewname);

		}
		return parts;
	}

	public static void createPartUseageLink(WTPart wt, WTPart part, String amout)
			throws WTPropertyVetoException, WTException {
		Double count = 1.0;
		if (!amout.trim().equals("")) {
			count = Double.valueOf(amout);
		}
		if (wt != null && part != null) {
			WTPartUsageLink link;
			try {
				QueryResult qr1 = PersistenceHelper.manager.find(WTPartUsageLink.class, wt,
						WTPartUsageLink.USED_BY_ROLE, part.getMaster());
				System.out.println("----" + qr1.size());
				if (qr1.size() == 0) {
					link = WTPartUsageLink.newWTPartUsageLink(wt, (WTPartMaster) part.getMaster());
					Quantity quantity = Quantity.newQuantity();
					quantity.setAmount(count);
					link.setQuantity(quantity);
					PersistenceServerHelper.manager.insert(link);
				}

			} catch (WTException e) {
				e.printStackTrace();
			}
		}

	}

	public static HashMap toHashMap(JSONObject jsonObject) throws JSONException {
		HashMap data = new HashMap();
		Iterator it = jsonObject.keys();
		while (it.hasNext()) {
			String key = String.valueOf(it.next());
			String value = "";

			if (jsonObject.get(key) instanceof String) {
				value = (String) jsonObject.get(key);
			}
			data.put(key, value);
			System.out.println(key + "=" + value);
		}
		return data;
	}

	/**
	 * 得到当前版本的前�??个大版本的最近小版本，测试可�??
	 * 
	 * @param obj
	 * @return
	 * @throws WTException
	 */
	public static WTObject getPreVersionObject(WTObject obj) throws Exception {
		WTObject prevVersionObj = null;
		String Version = VersionControlHelper.getVersionIdentifier((Versioned) obj).getValue();// 得到大版�??
		// System.out.println("当前版本�??"+Version);
		boolean notGet = true;
		QueryResult allIterations = VersionControlHelper.service.allVersionsFrom((Versioned) obj);
		if (allIterations != null) {
			while (allIterations.hasMoreElements() && notGet) {
				prevVersionObj = (WTObject) allIterations.nextElement();
				boolean issame = VersionControlHelper.inSameBranch((Iterated) prevVersionObj, (Iterated) obj);
				// String theVersion =
				// VersionControlHelper.getVersionIdentifier((Versioned)prevVersionObj).getValue();
				if (issame) {
					prevVersionObj = null;
					continue;
				} else {
					notGet = false;
				}
			}
		}
		// System.out.println("前一个版本："+VersionControlHelper.getVersionDisplayIdentifier((Versioned)
		// prevVersionObj)+VersionControlHelper.getIterationDisplayIdentifier((Versioned)
		// prevVersionObj));
		return prevVersionObj;
	}

	public static WTDocument getDocByNumberVersion(String number, String version) {
		try {
			QuerySpec qs = new QuerySpec(WTDocument.class);

			String docAliases = qs.getFromClause().getAliasAt(0);

			TableColumn tcit = new TableColumn(docAliases, "ITERATIONIDA2ITERATIONINFO");
			TableColumn tcv = new TableColumn(docAliases, "VERSIONIDA2VERSIONINFO");
			SearchCondition sc = new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL,
					number);
			qs.appendWhere(sc);

			sc = new SearchCondition(tcv, SearchCondition.EQUAL,
					new ConstantExpression(version.substring(0, version.indexOf("."))));
			qs.appendAnd();
			qs.appendWhere(sc);
			sc = new SearchCondition(tcit, SearchCondition.EQUAL,
					new ConstantExpression(version.substring(version.indexOf(".") + 1)));
			qs.appendAnd();
			qs.appendWhere(sc);
			System.out.println(qs);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				WTDocument doc = (WTDocument) qr.nextElement();
				return doc;
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return null;

	}

	public static WTDocument getDocByNumberOnlyVersion(String number, String version) {
		try {
			QuerySpec qs = new QuerySpec(WTDocument.class);

			String docAliases = qs.getFromClause().getAliasAt(0);

			TableColumn tcv = new TableColumn(docAliases, "VERSIONIDA2VERSIONINFO");
			SearchCondition sc = new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL,
					number);
			qs.appendWhere(sc);

			sc = new SearchCondition(tcv, SearchCondition.EQUAL, new ConstantExpression(version));
			qs.appendAnd();
			qs.appendWhere(sc);
			System.out.println(qs);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				WTDocument doc = (WTDocument) qr.nextElement();
				doc = (WTDocument) VersionControlHelper.getLatestIteration(doc);
				return doc;
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return null;

	}

	public static WTPart getPartByNumberOnlyVersion(String number, String version) {
		try {
			QuerySpec qs = new QuerySpec(WTPart.class);

			String docAliases = qs.getFromClause().getAliasAt(0);

			TableColumn tcv = new TableColumn(docAliases, "VERSIONIDA2VERSIONINFO");
			SearchCondition sc = new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL, number);
			qs.appendWhere(sc);

			sc = new SearchCondition(tcv, SearchCondition.EQUAL, new ConstantExpression(version));
			qs.appendAnd();
			qs.appendWhere(sc);
			System.out.println(qs);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				WTPart part = (WTPart) qr.nextElement();
				part = (WTPart) VersionControlHelper.getLatestIteration(part);
				return part;
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return null;

	}
	
	public static String getWTHome() throws IOException{
		WTProperties wtproperties = WTProperties.getLocalProperties();
		String path = wtproperties.getProperty("wt.home", "UTF-8");
		return path;
	}
}
