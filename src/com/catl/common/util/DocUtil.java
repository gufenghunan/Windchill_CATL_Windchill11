package com.catl.common.util;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFGroupShape;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlideMaster;
import org.apache.poi.xslf.usermodel.XSLFTextBox;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeaderFooter;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.content.FormatContentHolder;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.iba.definition.StringDefinition;
import wt.iba.value.StringValue;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.pom.PersistenceException;
import wt.pom.Transaction;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionServerHelper;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.util.WTMessage;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;

import com.catl.common.constant.DocState;
import com.catl.line.constant.ConstantLine;
import com.catl.line.util.WTDocumentUtil;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.netmarkets.model.NmOid;

public class DocUtil {

	private static Logger log = Logger.getLogger(DocUtil.class.getName());

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			WTDocument doc = getLatestWTDocument(args[0]);
			System.out.println(pdfFileCheck(doc));
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static String pdfFileCheck(ContentHolder holder) throws WTException {
		String checkResult = null;
		String number = null;
		String primaryFileName = null;
		String secondaryFileName = null;
		boolean isFileNameEqual = false;
		boolean hasAttachedPdf = false;
		boolean checkNumber = false;
		QueryResult primaryContents = ContentHelper.service.getContentsByRole(holder, ContentRoleType.PRIMARY);
		QueryResult secondaryContents = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);

		if (primaryContents.hasMoreElements()) {
			ApplicationData primaryData = (ApplicationData) primaryContents.nextElement();
			String name = null;
			if (holder instanceof EPMDocument) {
				EPMDocument epm = (EPMDocument) holder;
				name = epm.getCADName();
			} else {
				name = primaryData.getFileName();
			}
			if (holder instanceof WTDocument) {
				number = ((WTDocument) holder).getNumber();
				System.out.println("===number:" + number);
			}
			primaryFileName = getFileName(name);
		}

		while (secondaryContents.hasMoreElements()) {
			ApplicationData attachment = (ApplicationData) secondaryContents.nextElement();
			;
			String attachmentName = attachment.getFileName();
			if (attachmentName.toUpperCase().endsWith(".PDF")) {
				hasAttachedPdf = true;
				secondaryFileName = getFileName(attachmentName);
				if (secondaryFileName.equalsIgnoreCase(primaryFileName)) {
					if (number != null && StringUtils.equals(number, primaryFileName)) {
						checkNumber = true;
					}
					isFileNameEqual = true;

				}

			}

		}// end while
			// modify by hdong 0826
		if (holder instanceof WTDocument) {
			number = ((WTDocument) holder).getNumber();
			if (!number.startsWith(PropertiesUtil.getValueByKey("create_box_explain_group"))) {// 装箱单没有住内容
				if (!isFileNameEqual) {
					checkResult = "pdf附件的名称和主文件的名称不一致, 附件的文件名为" + secondaryFileName + "，主文件的文件名为" + primaryFileName;
				} else if (number != null && !checkNumber) {
					checkResult = WTMessage.formatLocalizedMessage("pdf附件的名称和主文件的名称[{0}]与编码[{1}]不一致", new Object[] { primaryFileName, number });
				}
			}
			if (!hasAttachedPdf) {
				checkResult = " 没有添加pdf格式的附件";
			}
		}

		return checkResult;

	}

	public static String getFileName(String fileName) {
		int i = fileName.indexOf(".");
		return i > 0 ? fileName.substring(0, i) : fileName;

	}

	public static WTDocument getLatestWTDocument(String docNumber) throws WTException {
		if (docNumber == null || docNumber.length() == 0)
			return null;

		QuerySpec qs = new QuerySpec(WTDocument.class);
		SearchCondition scNumber = new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, docNumber.toUpperCase());
		SearchCondition scLatestIteration = new SearchCondition(WTDocument.class, WTAttributeNameIfc.LATEST_ITERATION, SearchCondition.IS_TRUE);
		qs.appendWhere(scNumber);
		qs.appendAnd();
		qs.appendWhere(scLatestIteration);

		QueryResult qr = PersistenceHelper.manager.find(qs);
		if (qr != null && qr.hasMoreElements())
			qr = (new LatestConfigSpec()).process(qr);

		if (qr != null && qr.hasMoreElements())
			return (WTDocument) qr.nextElement();

		return null;
	}

	public static WTDocumentMaster getDocumentMaster(String documentNumber) throws WTException {
		WTDocumentMaster wtdocumentmaster = null;
		QuerySpec criteria = new QuerySpec(WTDocumentMaster.class);
		criteria.appendSearchCondition(new SearchCondition(WTDocumentMaster.class, WTDocumentMaster.NUMBER, SearchCondition.EQUAL, documentNumber, false));
		QueryResult results = PersistenceHelper.manager.find(criteria);
		if (results.hasMoreElements())
			wtdocumentmaster = (WTDocumentMaster) results.nextElement();
		return wtdocumentmaster;
	}

	public static String getObjectType(Object object) throws WTException {
		String type = "";
		boolean flag = true;
		try {
			flag = SessionServerHelper.manager.isAccessEnforced();
			SessionServerHelper.manager.setAccessEnforced(false);

			if (object != null) {
				TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(object);
				type = ti.getTypename();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(flag);
		}
		return type;
	}

	public static EPMDocument getLastestEPMDocumentByNumber(String numStr) {
		try {
			QuerySpec queryspec = new QuerySpec(EPMDocument.class);

			queryspec.appendSearchCondition(new SearchCondition(EPMDocument.class, EPMDocument.NUMBER, SearchCondition.EQUAL, numStr));
			QueryResult queryresult = PersistenceHelper.manager.find(queryspec);
			LatestConfigSpec cfg = new LatestConfigSpec();
			QueryResult qr = cfg.process(queryresult);
			if (qr.hasMoreElements()) {
				return (EPMDocument) qr.nextElement();
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static WTObject addAttachment(WTObject object, String strAttachmentFilePath, String attachementName) {
		File asrc = new File(strAttachmentFilePath);
		if (asrc != null && asrc.exists()) {
			InputStream attSymbleInputStream = null;
			try {
				attSymbleInputStream = new FileInputStream(asrc);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				log.debug("geti fileInputStream failed!");
				e1.printStackTrace();
			}
			if (object != null && attSymbleInputStream != null) {

				object = (WTObject) createSECONDARY((ContentHolder) object, attachementName, attSymbleInputStream);
			}
		}

		try {
			object = (WTObject) PersistenceHelper.manager.save(object);
			object = (WTObject) PersistenceServerHelper.manager.restore(object);
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return object;
	}

	public static ContentHolder createSECONDARY(ContentHolder object, String attachmentName, InputStream isAttachment) {
		if (object != null && attachmentName != null && isAttachment != null && attachmentName.length() > 0) {

			try {
				Transaction transaction = new Transaction();
				try {

					ApplicationData appdata = ApplicationData.newApplicationData(object);
					appdata.setRole(ContentRoleType.SECONDARY);
					transaction.start();
					// delete first
					ContentHolder content = ContentHelper.service.getContents(object);
					Vector vApplicationData = ContentHelper.getApplicationData(content);
					for (int i = 0; i < vApplicationData.size(); i++) {
						ApplicationData applicationdata = (ApplicationData) vApplicationData.elementAt(i);
						if (applicationdata.getFileName().equals(attachmentName)) {
							log.debug("...清除附件：" + applicationdata.getFileName());
							ContentServerHelper.service.deleteContent(content, applicationdata);
							break;
						}
					}

					appdata.setFileName(attachmentName);
					appdata.setUploadedFromPath(attachmentName);
					appdata.setDescription("系统自动生成");
					appdata = ContentServerHelper.service.updateContent(object, appdata, isAttachment);
					transaction.commit();
					transaction = null;
				} catch (WTException wte) {
					log.debug("error1: " + wte.getMessage());
				} catch (WTPropertyVetoException wtpve) {
					log.debug("error2: " + wtpve.getMessage());
				} catch (PropertyVetoException pve) {
					log.debug("error3: " + pve.getMessage());
				} catch (IOException ioe) {
					log.debug("error4: " + ioe.getMessage());
				} finally {
					if (transaction != null) {
						transaction.rollback();
					}
				}
				isAttachment.close();
			} catch (IOException ioe) {
				// System.out.println(ioe.getMessage());
			}
			return object;
		} else {
			return null;
		}
	}

	public static void deleteAttachmentHtml(WTObject object) throws Exception {
		try {
			object = (WTObject) PersistenceHelper.manager.refresh(object);
			ContentHolder contentHolder = ContentHelper.service.getContents((ContentHolder) object);
			Vector vData = ContentHelper.getApplicationData(contentHolder);
			log.debug("data size==" + vData.size());
			if (vData != null && vData.size() > 0) {
				for (int i = 0; i < vData.size(); i++) {
					ApplicationData appData = (ApplicationData) vData.get(i);
					log.debug("file name===" + appData.getFileName());
					if (appData.getFileName().endsWith(".html")) {
						ContentServerHelper.service.deleteContent((ContentHolder) object, appData);

					}

				}
			}
		} catch (WTException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new WTException(e);
		} finally {
		}

	}

	public static Set<WTDocument> getLastedDocByStringIBAValue(String excludeNumber, String version, String iba, String ibaValue) {

		Set<WTDocument> ret = new HashSet<WTDocument>();

		QuerySpec qs;
		try {
			qs = new QuerySpec();
			qs.setAdvancedQueryEnabled(true);
			int index0 = qs.appendClassList(WTDocument.class, true);
			int index1 = qs.appendClassList(StringValue.class, false);
			int index2 = qs.appendClassList(StringDefinition.class, false);

			SearchCondition join = new SearchCondition(WTDocument.class, WTAttributeNameIfc.ID_NAME, StringValue.class, StringValue.IBAHOLDER_REFERENCE + "." + WTAttributeNameIfc.REF_OBJECT_ID);
			SearchCondition join1 = new SearchCondition(StringValue.class, StringValue.DEFINITION_REFERENCE + "." + WTAttributeNameIfc.REF_OBJECT_ID, StringDefinition.class, WTAttributeNameIfc.ID_NAME);

			qs.appendWhere(join, new int[] { index0, index1 });
			qs.appendAnd();
			qs.appendWhere(join1, new int[] { index1, index2 });

			if (excludeNumber != null) {
				qs.appendAnd();
				qs.appendOpenParen();
				qs.appendOpenParen();
				qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, excludeNumber), new int[] { index0 });
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(WTDocument.class, "versionInfo.identifier.versionId", SearchCondition.NOT_EQUAL, version), new int[] { index0 });
				qs.appendCloseParen();
				qs.appendOr();
				qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.NOT_EQUAL, excludeNumber), new int[] { index0 });
				qs.appendCloseParen();
			}
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.LATEST_ITERATION, SearchCondition.IS_TRUE), new int[] { index0 });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.LIFE_CYCLE_STATE, SearchCondition.NOT_EQUAL, DocState.CANCELLED), new int[] { index0 });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(StringValue.class, StringValue.VALUE, SearchCondition.EQUAL, ibaValue.toUpperCase()), new int[] { index1 });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(StringValue.class, StringValue.IBAHOLDER_REFERENCE + "." + WTAttributeNameIfc.REF_CLASSNAME, SearchCondition.EQUAL, WTDocument.class.getName()), new int[] { index1 });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(StringDefinition.class, StringDefinition.NAME, SearchCondition.EQUAL, iba), new int[] { index2 });

			log.debug("==QuerySQL:" + qs.toString());
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
			Persistable[] p = null;
			while (qr.hasMoreElements()) {
				p = (Persistable[]) qr.nextElement();
				ret.add((WTDocument) p[0]);
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}

		return ret;
	}

	/**
	 * 判断WTDocuemnt是否为最新版本
	 * 
	 * @param doc
	 * @return
	 * @throws WTException
	 */
	public static boolean isLastedWTDocument(WTDocument doc) throws WTException {
		if (doc != null) {
			WTDocument lastedDoc = getLastestWTDocument(doc);
			return doc.equals(lastedDoc);
		}
		return false;
	}

	/**
	 * 获得WTDocument的最新版本对象
	 * 
	 * @param part
	 * @return
	 * @throws WTException
	 * @throws PersistenceException
	 */
	public static WTDocument getLastestWTDocument(WTDocument doc) throws WTException {
		if (doc != null) {
			QueryResult qr = VersionControlHelper.service.allVersionsOf(doc.getMaster());
			qr = new LatestConfigSpec().process(qr);
			return (WTDocument) qr.nextElement();
		}
		return null;
	}

	public static boolean isExistWTDocument(String branchId) throws WTException {
		if (branchId == null || branchId.length() == 0)
			return false;

		QuerySpec qs = new QuerySpec(WTDocument.class);
		SearchCondition scNumber = new SearchCondition(WTDocument.class, WTDocument.BRANCH_IDENTIFIER, SearchCondition.EQUAL, Long.parseLong(branchId));
		qs.appendWhere(scNumber);

		QueryResult qr = PersistenceHelper.manager.find(qs);
		if (qr.size() == 0)
			return false;

		return true;
	}

	/**
	 * 替换 文档主内容 编号 版本 页数
	 * 
	 * @param doc
	 * @throws WTException
	 * @throws IOException 
	 */
	public static void replaceDocumentPrimaryContent(WTDocument doc) throws WTException, IOException {
		String docNumberPrefix = PropertiesUtil.getValueByKey("auto_write_number_into_document");
		List<String> doctypelist = new ArrayList<String>();
		if(docNumberPrefix != null){
        	String[] docTypeArr = docNumberPrefix.split(",");
        	doctypelist = Arrays.asList(docTypeArr); 

        }
		if (doctypelist.contains(doc.getNumber().substring(0, doc.getNumber().length() - 9))) {
			QueryResult qr = ContentHelper.service.getContentsByRole(doc, ContentRoleType.PRIMARY);
			if (qr.hasMoreElements()) {
				ApplicationData appData = (ApplicationData) qr.nextElement();
				if (appData.getFileName().toLowerCase().endsWith(".pptx")) {
					XMLSlideShow ppt = replacePPTPrimary(doc, appData);
					replaceDocPrimary(doc, appData, ppt);
				} else if (appData.getFileName().toLowerCase().endsWith(".docx")) {
					XWPFDocument xwpfDocument = replaceWordContent(doc, appData);
					replaceDocPrimary(doc, appData, xwpfDocument);
				} else if (appData.getFileName().toLowerCase().endsWith(".xlsx")) {
					XSSFWorkbook workbook = replaceExcelContent(doc, appData);
					replaceDocPrimary(doc, appData, workbook);
				}
			}
		}
	}

	/**
	 * 替换 PPT中的内容
	 * 
	 * @param doc
	 * @param appData
	 * @return
	 * @throws IOException
	 * @throws WTException
	 */
	private static XMLSlideShow replacePPTPrimary(WTDocument doc, ApplicationData appData) throws IOException, WTException {
		XMLSlideShow ppt = new XMLSlideShow(ContentServerHelper.service.findContentStream(appData));
		XSLFSlideMaster m = ppt.getSlides().get(0).getSlideMaster();
		for (XSLFShape shape : m.getShapes()) {
			if (shape instanceof XSLFGroupShape) {
				XSLFGroupShape groupShapes = (XSLFGroupShape) shape;
				for (XSLFShape gShape : groupShapes.getShapes()) {
					XSLFTextBox box = (XSLFTextBox) gShape;
					String text = box.getText();
					if (text.contains("文件编号：")) {
						box.setText("文件编号：" + doc.getNumber());
					} else if (text.contains("版本号：")) {
						float version = getVersion(doc);
						
						box.setText("版本号："+version);
					}
					if (box.getText().contains("Page：")) {
						box.setText(box.getText().substring(0, box.getText().length() - 2) + ppt.getSlides().size());
					}
				}
			}
		}
		return ppt;
	}

	private static float getVersion(WTDocument doc) {
		String bigVersion = doc.getVersionIdentifier().getValue();
		float version = (float) 1.0;
		version = version + (float)(fromNumberSystem26(bigVersion)-1)/10;
		return version;
	}

	/**
	 * 替换 Word中的内容
	 * 
	 * @param doc
	 * @param appData
	 * @return
	 * @throws IOException
	 * @throws WTException
	 */
	private static XWPFDocument replaceWordContent(WTDocument doc, ApplicationData appData) throws IOException, WTException {
		XWPFDocument xwpfDocument = new XWPFDocument(ContentServerHelper.service.findContentStream(appData));
		XWPFHeaderFooter header0 = xwpfDocument.getHeaderFooterPolicy().getDefaultHeader();
		List<IBodyElement> bodys = header0.getBodyElements();
		for (IBodyElement body : bodys) {
			if (body instanceof XWPFTable) {
				XWPFTable table = (XWPFTable) body;
				for (XWPFTableRow row : table.getRows()) {
					for (XWPFTableCell cell : row.getTableCells()) {
						if (cell.getText().contains("文件编号：")) {
							cell.getParagraphs().get(0).removeRun(1);
							cell.setText(doc.getNumber());
						} else if (cell.getText().contains("版本号：")) {
							cell.getParagraphs().get(0).removeRun(1);
							float version = getVersion(doc);
							cell.setText(version+"");
						}
					}
				}
			}
		}
			
		return xwpfDocument;
	}

	/**
	 * 替换 Excel中的内容
	 * 
	 * @param doc
	 * @param appData
	 * @return
	 * @throws IOException
	 * @throws WTException
	 */
	private static XSSFWorkbook replaceExcelContent(WTDocument doc, ApplicationData appData) throws IOException, WTException {
		XSSFWorkbook workbook = new XSSFWorkbook(ContentServerHelper.service.findContentStream(appData));
		int sheetNum = workbook.getNumberOfSheets();
		for (int i = 0; i < sheetNum; i++) {
			XSSFSheet sheet = workbook.getSheetAt(i);
			for (int k = 0; k < 3; k++) {// 前三为固定写入值
				XSSFRow row = sheet.getRow(k);
				for (int j = 0; j < row.getLastCellNum(); j++) {
					XSSFCell cell = row.getCell(j);
					if (cell != null && k == 0 && cell.getStringCellValue() != null && cell.getStringCellValue().equals("DOC NO.")) {// 第一行
						cell = row.getCell(++j);
						cell.setCellValue(doc.getNumber());
						break;
					} else if (cell != null && k == 1 && cell.getStringCellValue() != null && cell.getStringCellValue().equals("REV.")) {// 第二行
						cell = row.getCell(++j);
						float version = getVersion(doc);
						cell.setCellValue(version+"");
						break;
					} else if (cell != null && k == 2 && cell.getStringCellValue() != null && cell.getStringCellValue().equals("SHEET")) {// 第三行
						cell = row.getCell(++j);
						cell.setCellValue(i + 1 + "");
						j = j + 2;
						cell = row.getCell(j);
						cell.setCellValue(sheetNum + "");
						break;
					}
				}
			}
		}
		return workbook;
	}

	/**
	 * 替换文档主内容
	 * 
	 * @param doc
	 * @param appData
	 * @param poiObject
	 */
	private static void replaceDocPrimary(WTDocument doc, ApplicationData appData, Object poiObject) {
		// 保存临时文件
		File file = null;
		FileOutputStream out = null;
		try {
			String tempExcelPath = PropertiesUtil.wt_home + PropertiesUtil.getValueByKey("file_temp") + appData.getFileName().substring(0, appData.getFileName().lastIndexOf(".")) + "_" + new Date().getTime() + appData.getFileName().substring(appData.getFileName().lastIndexOf("."));
			file = new File(tempExcelPath);
			out = new FileOutputStream(file);
			if (poiObject instanceof XWPFDocument) {
				XWPFDocument xwpfDocument = (XWPFDocument) poiObject;
				xwpfDocument.write(out);
				xwpfDocument.close();
			} else if (poiObject instanceof XSSFWorkbook) {
				XSSFWorkbook workbook = (XSSFWorkbook) poiObject;
				workbook.write(out);
				workbook.close();
			} else if (poiObject instanceof XMLSlideShow) {
				XMLSlideShow ppt = (XMLSlideShow) poiObject;
				ppt.write(out);
				ppt.close();
			}
			out.flush();
			// 替换祝内容
			WTDocumentUtil.replaceDocPrimaryContent(doc, new FileInputStream(file), appData.getFileName(), file.length());
			// 删除临时文件
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
				file.delete();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// / <summary>
	// / 将指定的自然数转换为26进制表示。映射关系：[1-26] ->[A-Z]。
	// / </summary>
	// / <param name="n">自然数（如果无效，则返回空字符串）。</param>
	// / <returns>26进制表示。</returns>
	public static String toNumberSystem26(int n) {
		String s = "";
		while (n > 0) {
			int m = n % 26;
			if (m == 0)
				m = 26;
			s = (char) (m + 64) + s;
			n = (n - m) / 26;
		}
		return s;
	}

	// / <summary>
	// / 将指定的26进制表示转换为自然数。映射关系：[A-Z] ->[1-26]。
	// / </summary>
	// / <param name="s">26进制表示（如果无效，则返回0）。</param>
	// / <returns>自然数。</returns>
	public static int fromNumberSystem26(String s) {
		if (s == null || s.trim().equals(""))
			return 0;
		int n = 0;
		for (int i = s.length() - 1, j = 1; i >= 0; i--, j *= 26) {
			char c = Character.toUpperCase(s.charAt(i));
			if (c < 'A' || c > 'Z')
				return 0;
			n += ((int) c - 64) * j;
		}
		return n;
	}
}
