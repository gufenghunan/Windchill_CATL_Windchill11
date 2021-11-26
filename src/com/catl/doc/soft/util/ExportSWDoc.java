package com.catl.doc.soft.util;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.catl.common.util.PartUtil;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.ObjectReference;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTArrayList;
import wt.httpgw.GatewayAuthenticator;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;

public class ExportSWDoc implements RemoteAccess {

	public static void main(String[] args) throws RemoteException, InvocationTargetException {
		// TODO Auto-generated method stub
		RemoteMethodServer rm = RemoteMethodServer.getDefault();
		GatewayAuthenticator auth = new GatewayAuthenticator();
		auth.setRemoteUser("wcadmin");
		rm.setAuthenticator(auth);
		Class[] clazz = { String.class, String.class };
		Object[] objs = { args[0], args[1] };

		rm.invoke("getAllSWDoc", ExportSWDoc.class.getName(), null, clazz, objs);
	}

	public static void getAllSWDoc(String path, String downpath)
			throws WTException, FileNotFoundException, IOException {
		QuerySpec qs = new QuerySpec(WTDocument.class);
		SearchCondition sc = new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.LIKE, "SW-P%");

		qs.appendWhere(sc);
		qs.appendAnd();
		sc = new SearchCondition(WTDocument.class, WTDocument.LATEST_ITERATION, SearchCondition.IS_TRUE);
		qs.appendWhere(sc);

		QueryResult qr = PersistenceHelper.manager.find(qs);

		LatestConfigSpec lc = new LatestConfigSpec();
		qr = lc.process(qr);
		System.out.println(qr.size());

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet();
		Row row = sheet.createRow(0);
		Cell docswCell = row.createCell(0);
		docswCell.setCellValue("文档编号");
		Cell parentPnCell = row.createCell(1);
		parentPnCell.setCellValue("父级PN");
		int i = 1;

		while (qr.hasMoreElements()) {
			WTDocument doc = (WTDocument) qr.nextElement();
			System.out.println(doc.getNumber() + "\t" + doc.getName());
			String parentPNs = getRelationPartByRefDoc(doc);
			Row valueRow = sheet.createRow(i);
			Cell valueDocCell = valueRow.createCell(0);
			valueDocCell.setCellValue(doc.getNumber());
			Cell valuePnCell = valueRow.createCell(1);
			valuePnCell.setCellValue(parentPNs);

			try {
				downloadDoc(doc, downpath);
			} catch (PropertyVetoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i++;
		}

		workbook.write(new FileOutputStream(path));
	}

	public static void downloadDoc(WTDocument doc, String path) throws WTException, PropertyVetoException, IOException {
		ApplicationData primary = (ApplicationData) ContentHelper.service.getPrimary(doc);
		if (primary != null) {
			String filename = primary.getFileName();
			System.out.println(primary.getFormatName());
			String format = filename.substring(filename.lastIndexOf("."));
			path = path + File.separator + doc.getNumber()+format;
			System.out.println(path);

			InputStream is = ContentServerHelper.service.findContentStream(primary);

			if (is != null) {
				FileOutputStream fos = new FileOutputStream(new File(path));
				byte[] buffer = new byte[1024];
				int byteread = 0; // 读取的字节数
				while ((byteread = is.read(buffer)) > 0) {
					fos.write(buffer, 0, byteread);
				}
				fos.flush();
				is.close();
				fos.close();
			}
		}
	}

	/**
	 * 获取说明文档关联的物料
	 * 
	 * @param doc
	 * @return
	 * @throws WTException
	 */
	public static String getRelationPartByRefDoc(WTDocument doc) throws WTException {
		WTArrayList relatepartlist = PartDocServiceCommand.getAssociatedRefParts(doc);
		String parentPNs = "";
		for (int i = 0; i < relatepartlist.size(); i++) {
			ObjectReference obj = (ObjectReference) relatepartlist.get(i);
			WTPart part = (WTPart) obj.getObject();
			if (!parentPNs.equals(part.getNumber())) {
				if (i > 0) {
					parentPNs = parentPNs + ",";
				}
				parentPNs = parentPNs + part.getNumber();
				System.out.println("RelatePart\t" + part.getNumber() + "\t" + part.getName());
			}
		}

		return parentPNs;
	}

}
