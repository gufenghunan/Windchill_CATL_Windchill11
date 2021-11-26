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
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.catl.common.util.PropertiesUtil;
import com.catl.line.constant.ConstantLine;
import com.catl.line.exception.LineException;
import com.ptc.core.HTMLtemplateutil.server.processors.EntityTaskDelegate;
import com.ptc.core.meta.common.AttributeTypeIdentifier;
import com.ptc.core.meta.container.common.AttributeTypeSummary;
import com.ptc.prolog.pub.RunTimeException;

import wt.doc.DocumentType;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.enterprise.CopyObjectInfo;
import wt.enterprise.EnterpriseHelper;
import wt.enterprise.RevisionControlled;
import wt.enterprise._RevisionControlled;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.folder.SubFolder;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.inf.library.WTLibrary;
import wt.lifecycle.LifeCycleException;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.LifeCycleTemplateReference;
import wt.load.LoadServerHelper;
import wt.org.OrganizationServicesHelper;
import wt.org.WTUser;
import wt.part.Quantity;
import wt.part.QuantityUnit;
import wt.part.Source;
import wt.part.WTPart;
import wt.part.WTPartConfigSpec;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartStandardConfigSpec;
import wt.part.WTPartUsageLink;
import wt.pdmlink.PDMLinkProduct;
import wt.pds.StatementSpec;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.series.Series;
import wt.type.TypeDefinitionReference;
import wt.type.TypedUtility;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.vc.Iterated;
import wt.vc.IterationIdentifier;
import wt.vc.Mastered;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;
import wt.vc.views.ViewReference;
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

	public static WTPart getLatestWTpartByNumber(String number)
			throws WTException {
		WTPart wtpart = null;
		// check number
		if (number == null || "".equals(number = number.trim())) {
			return wtpart;
		}
		QuerySpec qs = new QuerySpec(WTPart.class);
		qs.appendWhere(new SearchCondition(WTPart.class, "master>number",
				SearchCondition.EQUAL, number.toUpperCase(), false),
				new int[] { 0 });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WTPart.class,
				"iterationInfo.latest", SearchCondition.IS_TRUE),
				new int[] { 0 });
		qs.appendOrderBy(new OrderBy(new ClassAttribute(WTPart.class,
				"versionInfo.identifier.versionSortId"), true), new int[] { 0 });

		QueryResult qr = PersistenceServerHelper.manager.query(qs);
		wtpart = qr.hasMoreElements() ? (WTPart) qr.nextElement() : null;

		return wtpart;
	}

	/**
	 * 重新分配生命周期
	 * 
	 * @param obj
	 * @throws LifeCycleException
	 * @throws WTException
	 */
	public static void reassign(WTObject obj) throws LifeCycleException,
			WTException {
		LifeCycleTemplateReference ref = ((_RevisionControlled) obj)
				.getLifeCycleTemplate();
		LifeCycleHelper.service.reassign((LifeCycleManaged) obj, ref);
	}

	/**
	 * 获取最新文档
	 * 
	 * @param number
	 * @return
	 * @throws WTException
	 */
	public static WTDocument getLatestWTDocByNumber(String number)
			throws WTException {
		WTDocument wtdoc = null;
		// check number
		if (number == null || "".equals(number = number.trim())) {
			return wtdoc;
		}
		QuerySpec qs = new QuerySpec(WTDocument.class);
		qs.appendWhere(new SearchCondition(WTDocument.class, "master>number",
				SearchCondition.EQUAL, number.toUpperCase(), false),
				new int[] { 0 });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WTDocument.class,
				"iterationInfo.latest", SearchCondition.IS_TRUE),
				new int[] { 0 });
		qs.appendOrderBy(new OrderBy(new ClassAttribute(WTDocument.class,
				"versionInfo.identifier.versionSortId"), true), new int[] { 0 });

		QueryResult qr = PersistenceServerHelper.manager.query(qs);
		wtdoc = qr.hasMoreElements() ? (WTDocument) qr.nextElement() : null;

		return wtdoc;
	}

	public static Object getLatestVersionOf(Mastered master) throws WTException {
		QueryResult qr = VersionControlHelper.service.allVersionsOf(master);
		return qr.hasMoreElements() ? qr.nextElement() : null;
	}

	/**
	 * 压缩指定的单个或多个文件，如果是目录，则遍历目录下所有文件进行压缩
	 *
	 * @param fromFile
	 *            要压缩的文件或者目录
	 * @param zipFileName
	 *            ZIP文件名包含全路径
	 */
	public static boolean zipFile(String fromFile, String zipFileName) {
		ZipOutputStream out = null;
		boolean retValue = false;
		try {
			// 取得压缩文件夹
			File file = new File(fromFile);
			if (!file.exists()) {
				return retValue;
			}
			// 取得压缩文件名称
			String fName = StringUtils.substringBeforeLast(fromFile,
					File.separator);
			// createDir(zipFileName);
			out = new ZipOutputStream(new FileOutputStream(zipFileName));
			// 压缩文件
			zip(out, file, file.getName());

			out.close(); // 输出流关闭
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
	 *            ZIP输入流
	 * @param file
	 *            被压缩的文件
	 * @param base
	 *            被压缩的文件名
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
	 * 递归删除目录下的所有文件及子目录下所有文件
	 *
	 * @param file
	 *            将要删除的文件目录
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

	public static HttpServletResponse downloadAsStream(String path,
			HttpServletResponse response) {
		try {
			// path是指欲下载的文件的路径。
			File file = new File(path);
			// 取得文件名。
			String filename = file.getName();
			// 取得文件的后缀名。
			String ext = filename.substring(filename.lastIndexOf(".") + 1)
					.toUpperCase();
			// 以流的形式下载文件。
			InputStream fis = new BufferedInputStream(new FileInputStream(path));
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
			fis.close();
			// 清空response
			response.reset();
			// 设置response的Header
			response.addHeader("Content-Disposition", "attachment;filename="
					+ new String(filename.getBytes()));
			response.addHeader("Content-Length", "" + file.length());
			OutputStream toClient = new BufferedOutputStream(
					response.getOutputStream());
			response.setContentType("application/octet-stream");
			toClient.write(buffer);
			toClient.flush();
			toClient.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return response;
	}

	public static HttpServletResponse download(String path,
			HttpServletResponse response) {
		try {
			// path是指欲下载的文件的路径。
			File file = new File(path);
			if (!file.exists()) {
				response.sendError(404, "File not found!");
				return response;
			}
			// 取得文件名。
			String filename = file.getName();

			// 设置response
			response.reset();
			response.setContentType("application/x-msdownload");
			response.addHeader("Content-Disposition", "attachment;filename="
					+ URLEncoder.encode(filename, "UTF-8"));

			// 以流的形式下载文件。
			BufferedInputStream br = new BufferedInputStream(
					new FileInputStream(path));
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
		return new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(path), "utf-8"), true);
	}

	public static String getFileName(String filename) {
		return StringUtils.replace(filename, File.separator, "-");
	}

	public static Workable checkinObject(Workable obj, String comment)
			throws WTException, WTPropertyVetoException {
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

	public static Workable checkoutObject(Workable obj) throws WTException,
			WTPropertyVetoException {
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
			obj = (Workable) VersionControlHelper.service.getLatestIteration(
					obj, false);
			CheckoutLink cl = WorkInProgressHelper.service.checkout(obj,
					folder, null);
			obj = cl.getWorkingCopy();
		}
		obj = (Workable) PersistenceHelper.manager.refresh(obj);
		return obj;
	}

	public static QueryResult getProductByName(String prodName)
			throws WTException {
		QuerySpec qs = new QuerySpec(PDMLinkProduct.class);
		if (prodName != null && !((prodName = prodName.trim()).isEmpty())) {
			qs.appendWhere(new SearchCondition(PDMLinkProduct.class,
					"product>name", SearchCondition.EQUAL, prodName),
					new int[] { 0 });
		}
		// 按名称升序
		qs.appendOrderBy(new OrderBy(new ClassAttribute(PDMLinkProduct.class,
				PDMLinkProduct.NAME), false), new int[] { 0 });
		return PersistenceHelper.manager.find((StatementSpec) qs);
	}

	public static WTContainer getLibraryByName(String libName)
			throws WTException {
		QuerySpec qs = new QuerySpec(WTLibrary.class);
		if (libName != null) {
			qs.appendWhere(new SearchCondition(WTLibrary.class,
					"containerInfo.name", SearchCondition.EQUAL, libName),
					new int[] { 0 });
		}
		// 按名称升序
		qs.appendOrderBy(new OrderBy(new ClassAttribute(WTLibrary.class,
				WTLibrary.NAME), false), new int[] { 0 });
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
			SearchCondition sc = new SearchCondition(WTUser.class, WTUser.NAME,
					SearchCondition.EQUAL, name, false);
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
			SearchCondition sc = new SearchCondition(PDMLinkProduct.class,
					PDMLinkProduct.NAME, SearchCondition.EQUAL, name);
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
	 * 设置部件小版本.
	 * 
	 * @param iterated
	 *            部件
	 * @param iteration
	 *            版本
	 * @throws WTException
	 *             WTException
	 */
	public static void setIteration(final Iterated iterated,
			final String iteration) throws WTException {
		try {
			if (iteration != null) {
				Series ser = Series.newSeries("wt.vc.IterationIdentifier",
						iteration);
				IterationIdentifier iid = IterationIdentifier
						.newIterationIdentifier(ser);
				VersionControlHelper.setIterationIdentifier(iterated, iid);
			}
		} catch (WTPropertyVetoException e) {
			LoadServerHelper.printMessage("\nsetIteration: " + e.getMessage());
			e.printStackTrace();
			throw new WTException(e);
		}
	}

	public static WTDocument getWTDocumentByNumber(String number)
			throws WTException {
		WTDocument wt = null;
		QuerySpec qs = new QuerySpec(WTDocument.class);
		SearchCondition sc = new SearchCondition(WTDocument.class,
				WTDocument.NUMBER, "=", number);
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

	public static ArrayList getRefDocByPart(WTPart part) {
		ArrayList results = new ArrayList();

		try {
			QueryResult qr = WTPartHelper.service
					.getReferencesWTDocumentMasters(part);
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
			QueryResult qr = WTPartHelper.service
					.getDescribedByWTDocuments(part);
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
		SearchCondition sc = new SearchCondition(WTPart.class, WTPart.NUMBER,
				"=", number);
		qs.appendWhere(sc);
		LatestConfigSpec lcs = new LatestConfigSpec();
		QueryResult qr = PersistenceHelper.manager.find(qs);
		qr = lcs.process(qr);
		if (qr.hasMoreElements()) {
			wt = (WTPart) qr.nextElement();
		}
		return wt;
	}

	public static WTPartUsageLink buildPartLink(String parentnumber,
			String childnumber) throws WTException {
		WTPart ppart = getPartByNumber(parentnumber);
		WTPart cpart = getPartByNumber(childnumber);
		boolean flag=PNUtil.hasPartUseageLink(ppart, (WTPartMaster) cpart.getMaster());
		if(!flag){
			WTPartUsageLink usageLink = WTPartUsageLink.newWTPartUsageLink(ppart,
					(WTPartMaster) cpart.getMaster());
			PersistenceServerHelper.manager.insert(usageLink);
			return usageLink;
		}else{
			QueryResult qr = PersistenceHelper.manager.find(WTPartUsageLink.class, ppart, WTPartUsageLink.USED_BY_ROLE,cpart.getMaster());
			WTPartUsageLink link = (WTPartUsageLink) qr.nextElement();
			return link;
		}
		
	}

	public static List getAllChildParts(List parts, WTPart part, String viewname)
			throws WTException, WTPropertyVetoException {
		wt.fc.Persistable apersistable[] = null;
		WTPartStandardConfigSpec stdSpec = WTPartStandardConfigSpec
				.newWTPartStandardConfigSpec();
		View view = ViewHelper.service.getView(part.getViewName());
		stdSpec.setView(view);
		WTPartConfigSpec configSpec = WTPartConfigSpec
				.newWTPartConfigSpec(stdSpec);
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
	public static List getChildParts(WTPart part, String viewname)
			throws WTException, WTPropertyVetoException {
		List parts=new ArrayList();
		wt.fc.Persistable apersistable[] = null;
		WTPartStandardConfigSpec stdSpec = WTPartStandardConfigSpec
				.newWTPartStandardConfigSpec();
		View view = ViewHelper.service.getView(part.getViewName());
		stdSpec.setView(view);
		WTPartConfigSpec configSpec = WTPartConfigSpec
				.newWTPartConfigSpec(stdSpec);
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
				QueryResult qr1 = PersistenceHelper.manager.find(
						WTPartUsageLink.class, wt,
						WTPartUsageLink.USED_BY_ROLE, part.getMaster());
				System.out.println("----" + qr1.size());
				if (qr1.size() == 0) {
					link = WTPartUsageLink.newWTPartUsageLink(wt,
							(WTPartMaster) part.getMaster());
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

	public static void PartSaveAs(WTPart part, String number,
			String containeroid, String folderoid) throws WTException,
			WTPropertyVetoException {
		RevisionControlled[] originals = new RevisionControlled[1];
		CopyObjectInfo[] copyInfoArray = null;
		originals[0] = part;
		copyInfoArray = EnterpriseHelper.service.newMultiObjectCopy(originals);
		WTPart copy = (WTPart) copyInfoArray[0].getCopy();
		copy.setName(part.getName());
		copy.setNumber(number);
		Folder folder=(Folder) WCUtil.getWTObject(folderoid);
		WTContainer container =folder.getContainer();
		copy.setContainer(container);
		copy.setView(part.getView());
		FolderHelper.assignLocation(copy, folder);
		copyInfoArray = EnterpriseHelper.service
				.saveMultiObjectCopy(copyInfoArray);
	}

	/**
	 * 创建文档
	 * 
	 * @param number
	 * @param name
	 * @param part
	 * @param doctype
	 * @throws Exception
	 */
	public static void createDoc(String number, String name, WTPart part,
			String doctype) throws Exception {
		WTDocument doc = WTDocument.newWTDocument();
		TypeDefinitionReference tdr = TypedUtility
				.getTypeDefinitionReference(doctype);// 设置软类型
		doc.setName(name);
		doc.setNumber(number);
		doc.setTypeDefinitionReference(tdr);
		doc.setContainer(part.getContainer());
		String path = FolderHelper.service.getFolder(part).getFolderPath();
		Folder folder = FolderUtil.getFolder(path, part.getContainer());
		FolderHelper.assignLocation(doc, folder);
		PersistenceHelper.manager.save(doc);
	}

	/**
	 * 创建文档
	 * 
	 * @param number
	 * @param name
	 * @param part
	 * @param doctype
	 * @throws Exception
	 */
	public static void createDocDependsPart(String number, String name,
			WTPart part, String doctype, String relativepath) throws Exception {
		WTDocument doc = WTDocument.newWTDocument();
		TypeDefinitionReference tdr = TypedUtility
				.getTypeDefinitionReference(doctype);// 设置软类型
		doc.setName(name);
		doc.setNumber(number);
		doc.setTypeDefinitionReference(tdr);
		doc.setContainer(part.getContainer());
		String path = FolderHelper.service.getFolder(part).getFolderPath();
		path = path.replace("/" + PropertiesUtil.getValueByKey("config_part_folder"), "/设计图档")
				+ relativepath;
		Folder folder = FolderUtil.getFolder(path, part.getContainer());
		FolderHelper.assignLocation(doc, folder);
		PersistenceHelper.manager.save(doc);
	}

	/**
	 * 创建文档 存放指定文件夹
	 * 
	 * @param number
	 * @param name
	 * @param part
	 * @param doctype
	 * @throws Exception
	 */
	public static WTDocument createDoc(String number, String name, WTPart part,
			String doctype, String floder) throws Exception {
		WTDocument doc = WTDocument.newWTDocument();
		TypeDefinitionReference tdr = TypedUtility
				.getTypeDefinitionReference(doctype);// 设置软类型
		doc.setName(name);
		doc.setNumber(number);
		doc.setTypeDefinitionReference(tdr);
		doc.setContainer(part.getContainer());
		Folder folder = FolderUtil.getFolder(floder, part.getContainer());
		FolderHelper.assignLocation(doc, folder);
		return (WTDocument) PersistenceHelper.manager.save(doc);
	}
	
    /**
     * 创建部件 存放指定文件夹
     * @param number
     * @param name
     * @param part
     * @param type
     * @throws Exception
     */
    public static WTPart createPart(String number, String name, String type, String source, String unit,
			String oldnum,String forcecreate, String description, String folderOid, String containerOid) throws Exception{
           WTPart part =WTPart.newWTPart();
	       	TypeDefinitionReference tdr = TypedUtility.getTypeDefinitionReference(type);// 设置软类型
	       	Folder folder=(Folder) WCUtil.getWTObject(folderOid);
	       	WTContainer container=folder.getContainer();
	       	part.setName(name);
	       	part.setNumber(number);
	       	part.setTypeDefinitionReference(tdr);
	       	part.setDefaultUnit(getQuantityUnit(unit));
	       	part.setSource(getSource(source));
	       	part.setContainer(container);
	       	View views = ViewHelper.service.getView("Design");
			ViewReference viewRef = ViewReference.newViewReference(views);
			part.setView(viewRef);
			FolderHelper.assignLocation(part, folder);
			WTPart spart= (WTPart)PersistenceHelper.manager.save(part);
			IBAUtility iba=new IBAUtility(spart);
			if(!StringUtils.isEmpty(description)){
				iba.setIBAValue(ConstantLine.var_englishname, description);
			}
			if(!StringUtils.isEmpty(oldnum)){
				iba.setIBAValue(ConstantLine.var_oldpartnumber, oldnum);
			}
			if(!StringUtils.isEmpty(forcecreate)){
				if(forcecreate.equals("是")){
					forcecreate="true";
				}else{
					forcecreate="false";
				}
				iba.setIBAValue(ConstantLine.var_forcecreate, forcecreate);
			}
			if(!StringUtils.isEmpty(oldnum)){
				iba.setIBAValue(ConstantLine.var_oldpartnumber, oldnum);
			}
			iba.updateAttributeContainer(spart);
			iba.updateIBAHolder(spart);
			return spart;
     }
    

	private static Source getSource(String source) throws RunTimeException {
		Source[] set=Source.getSourceSet();
		for (int i = 0; i < set.length; i++) {
			Source s=set[i];
			if(source.equals(s.getDisplay(Locale.CHINA))){
				return s;
			}
		}
		throw new LineException("获取不到"+source);
	}
	
	private static QuantityUnit getQuantityUnit(String unit) throws RunTimeException {
		QuantityUnit[] set=QuantityUnit.getQuantityUnitSet();
		for (int i = 0; i < set.length; i++) {
			QuantityUnit s=set[i];
			if(unit.equals(s.getDisplay(Locale.CHINA))){
				return s;
			}
		}
		throw new LineException("获取不到"+unit);
	}
	

	/**
	 * null或空值 返回true
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNull(String str) {
		if (str == null || str.trim().equals("")) {
			return true;
		}
		return false;
	}
	
	public static String getPsize(String clf) throws WTException{
        ArrayList vals = new ArrayList();
        ArrayList attributeTypeSummaries = new ArrayList();
        ArrayList attributeIdentifierStrings = new ArrayList();
        StringBuffer typeInstanceIdentifierString = new StringBuffer();
        ArrayList attributeStates = new ArrayList();
        EntityTaskDelegate.getSoftAttributes(clf, true, false, typeInstanceIdentifierString, attributeIdentifierStrings,
          attributeTypeSummaries, vals, attributeStates, Locale.ENGLISH);
        System.out.println("-------------------------");
        for(int i=0;i<attributeTypeSummaries.size();i++){
			 AttributeTypeSummary ats = (AttributeTypeSummary)attributeTypeSummaries.get(i);
			 String lable = ats.getLabel();
			 AttributeTypeIdentifier ati = ats.getAttributeTypeIdentifier();
			 String attrName = ati.getAttributeName();
        }
    
		return clf;
		
	}

	public static WTDocument getLatestWTDocByNumberNotWorkable(String number) throws WTException {
		WTDocument wtdoc = null;
		// check number
		if (number == null || "".equals(number = number.trim())) {
			return wtdoc;
		}
		QuerySpec qs = new QuerySpec(WTDocument.class);
		qs.appendWhere(new SearchCondition(WTDocument.class, "master>number",
				SearchCondition.EQUAL, number.toUpperCase(), false),
				new int[] { 0 });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WTDocument.class,
				"iterationInfo.latest", SearchCondition.IS_TRUE),
				new int[] { 0 });
		qs.appendOrderBy(new OrderBy(new ClassAttribute(WTDocument.class,
				"versionInfo.identifier.versionSortId"), true), new int[] { 0 });

		QueryResult qr = PersistenceServerHelper.manager.query(qs);
		wtdoc = qr.hasMoreElements() ? (WTDocument) qr.nextElement() : null;
		if (WorkInProgressHelper.isWorkingCopy(wtdoc)){
        	wtdoc=(WTDocument) qr.nextElement();
        }
		return wtdoc;
	}
}
