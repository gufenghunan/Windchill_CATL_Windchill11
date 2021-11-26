package com.catl.pdfsignet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.catl.common.util.CatlConstant;
import com.catl.common.util.GenericUtil;
import com.catl.common.util.PartUtil;
import com.catl.common.util.TypeUtil;
import com.catl.common.util.WCLocationConstants;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.codec.PngImage;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.QueryResult;
import wt.lifecycle.IteratedLifeCycleManaged;
import wt.part.PartDocHelper;
import wt.part.WTPart;
import wt.representation.Representation;
import wt.representation.RepresentationHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTMessage;

public class PDFSignetUtil {

	public static final String CATIADrawing_Suffix = ".CATDrawing";

	public static final String PDF_Suffix = ".PDF";

	public static final String RELEASE_Suffix = "_RELEASE";

	private static Logger log = Logger.getLogger(PDFSignetUtil.class.getName());

	public static SimpleDateFormat SHORT_DATE_FORMATE = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * 获得当前的日期字符串
	 * 
	 * @return
	 */
	public static String getCurrentDate() {
		return SHORT_DATE_FORMATE.format(new Date());
	}

	/**
	 * 对图档文件的所有PDF附件打印图章并加密
	 * 
	 * @param obj
	 * @param errorMsg
	 * @throws WTException
	 */
	public static void printSignetAndEncryption(Object obj, Image image, String pass) throws WTException {
		if (isAutoCADDoc(obj) || isCATIADrawing(obj) || isPart(obj)) {
			Set<Object[]> set = getPDFFromDrawingFileDoc((ContentHolder) obj, true);
			if(set == null){
				//null 表示已经存在AABBCC-XXXX_RELEASE.pdf图纸 不做处理
			}else if (set.size() > 0) {
				mkDirs();
				for (Object[] objs : set) {
					String fileName = (String) objs[0];
					log.info("==pdfSignetAndEncryption==fileName:" + fileName);
					InputStream inputStream = (InputStream) objs[1];
					String tempFileName = printSignetAndEncryption(fileName, inputStream, image, pass);
					uploadPDFAsAttachment((ContentHolder) obj, tempFileName);
					deleteTempFile(tempFileName);
				}
			} else if(!isPart(obj)){//part不做没有PDF附件 校验
				throw new WTException(WTMessage.formatLocalizedMessage("图档对象[{0}]没有PDF附件 \n", new Object[] { getObjectNumber(obj) }));
			}
		}
	}

	/**
	 * 对所给对象的附件PDF附件打印图章并加密,没有PDF则不做任何操作
	 * 
	 * @param obj
	 *            AutoCADDoc、CATIADrawing或者Part
	 * @param image
	 * @param pass
	 * @throws WTException
	 */
	private static void printSignetAndEncryptionPDF(Object obj, Image image, String pass) throws WTException {
		Set<Object[]> set = null;
		if (isAutoCADDoc(obj) || isCATIADrawing(obj)) {
			set = getPDFFromDrawingFileDoc((ContentHolder) obj, false);
		} else if (obj instanceof WTPart) {
			set = getPDFFromWTPart((WTPart) obj);
		}
		if (set != null && set.size() > 0) {
			mkDirs();
			for (Object[] objs : set) {
				String fileName = (String) objs[0];
				log.info("==pdfSignetAndEncryption==fileName:" + fileName);
				InputStream inputStream = (InputStream) objs[1];
				String tempFileName = printSignetAndEncryption(fileName, inputStream, image, pass);
				uploadPDFAsAttachment((ContentHolder) obj, tempFileName);
				deleteTempFile(tempFileName);
			}
		}
	}

	/**
	 * 对已发布Part的PDF图纸进行打印图章并加密
	 * 
	 * @param part
	 * @param image
	 * @param pass
	 * @throws WTException
	 */
	public static void printSignetAndEncryptionFromPart(WTPart part, Image image, String pass) throws WTException {
		if (isReleased(part) && PartUtil.isLastedWTPart(part)) {
			QueryResult qr = PartDocHelper.service.getAssociatedDocuments(part);
			Set<Object> epms = new HashSet<Object>();
			Set<Object> docs = new HashSet<Object>();
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				if (isCATIADrawing(obj)) {
					epms.add(obj);
				} else if (isAutoCADDoc(obj)) {
					docs.add(obj);
				}
			}
			if (epms.size() > 0) {
				for (Object obj : epms) {
					if (isReleased(obj)) {
						printSignetAndEncryptionPDF(obj, image, pass);
					}
				}
			} else if (docs.size() > 0) {
				for (Object obj : docs) {
					if (isReleased(obj)) {
						printSignetAndEncryptionPDF(obj, image, pass);
					}
				}
			} else {
				printSignetAndEncryptionPDF(part, image, pass);
			}
		}
	}

	/**
	 * 判断对象的状态是否为“已发布”状态
	 * 
	 * @param obj
	 * @return
	 */
	private static boolean isReleased(Object obj) {
		if (obj instanceof IteratedLifeCycleManaged) {
			IteratedLifeCycleManaged lcm = (IteratedLifeCycleManaged) obj;
			String stateStr = lcm.getState().toString();
			return StringUtils.equals(stateStr, "RELEASED");
		}
		return false;
	}

	/**
	 * 对PDF文件打印图章并加密
	 * 
	 * @param fileName
	 * @param inputStream
	 * @throws WTException
	 */
	private static String printSignetAndEncryption(String fileName, InputStream inputStream, Image image, String pass) throws WTException {
		String tempFileName = getTempFileName(fileName);
		try {
			PdfReader reader = new PdfReader(inputStream);
			PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(getTempFile(tempFileName)));
			encryptionPDF(stamper, pass);

			int pages = reader.getNumberOfPages();
			for (int i = 1; i <= pages; i++) {
				PdfContentByte cb = stamper.getOverContent(i);
				cb.addImage(image);

				BaseFont bf = BaseFont.createFont("com/catl/pdfsignet/fonts/TIMES.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
				cb.setFontAndSize(bf, 9);
				cb.beginText();
				cb.setColorFill(BaseColor.BLUE);
				cb.showTextAligned(Element.ALIGN_LEFT, getCurrentDate(), 40, 60, 0);
				cb.endText();
			}

			stamper.close();
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
			deleteTempFile(tempFileName);
			throw new WTException(WTMessage.formatLocalizedMessage("处理附件[{0}]时异常：" + e.getLocalizedMessage(), new Object[] { fileName }));
		}
		return tempFileName;
	}

	/**
	 * 把打印了图章并加密后的PDF作为附件上传
	 * 
	 * @param holder
	 * @param tempFileName
	 * @throws WTException
	 */
	public static void uploadPDFAsAttachment(ContentHolder holder, String tempFileName) throws WTException {
		log.info("===uploadPDFAsAttachment  start====");
		log.info("====tempFileName:" + tempFileName);
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ApplicationData targetAppData = null;
		QueryResult qr = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);
		try {
			while (qr.hasMoreElements()) {
				ApplicationData appData = (ApplicationData) qr.nextElement();
				String fileNameInner = URLDecoder.decode(appData.getFileName(), "utf-8");
				log.info("==fileNameInner:" + fileNameInner);
				if (fileNameInner.equalsIgnoreCase(tempFileName) || tempFileName.equalsIgnoreCase(appData.getFileName())) {
					targetAppData = appData;
					break;
				}
			}
			if (targetAppData == null) {
				targetAppData = new ApplicationData();
			}
			targetAppData.setRole(ContentRoleType.SECONDARY);
			targetAppData.setDescription("系统自动生成");
			targetAppData.setCategory("GENERAL");
			ContentServerHelper.service.updateContent(holder, targetAppData, getTempFile(tempFileName));
		} catch (Exception e) {
			e.printStackTrace();
			throw new WTException(e.getLocalizedMessage());
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		log.info("===uploadPDFAsAttachment  end====");
	}

	/**
	 * 获取对象的Number
	 * 
	 * @param obj
	 * @return
	 */
	private static String getObjectNumber(Object obj) {
		String number = "";
		if (obj instanceof WTDocument) {
			number = ((WTDocument) obj).getNumber();
		} else if (obj instanceof EPMDocument) {
			number = ((EPMDocument) obj).getNumber();
		}
		log.info("==getObjectNumber==number:" + number);
		return number;
	}

	/**
	 * 判断入参对象是否为AutoCADDoc
	 * 
	 * @param obj
	 * @return
	 * @throws WTException
	 */
	public static boolean isAutoCADDoc(Object obj) throws WTException {
		if (obj instanceof WTDocument) {
			WTDocument doc = (WTDocument) obj;
			log.info("==isAutoCADDoc==doc:" + doc.getIdentity());
			return TypeUtil.isSpecifiedType(doc, CatlConstant.AUTOCAD_DOC_TYPE);
		}
		return false;
	}

	/**
	 * 判断入参对象是否为CATIADrawing
	 * 
	 * @param obj
	 * @return
	 * @throws WTException
	 */
	public static boolean isCATIADrawing(Object obj) throws WTException {
		if (obj instanceof EPMDocument) {
			EPMDocument epm = (EPMDocument) obj;
			String cadName = epm.getCADName();
			log.info("==isCATIADrawing==cadName:" + cadName);
			if (StringUtils.isNotBlank(cadName) && cadName.endsWith(CATIADrawing_Suffix)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断入参对象是否为WTPart
	 * 
	 * @param obj
	 * @return
	 * @throws WTException
	 */
	public static boolean isPart(Object obj) throws WTException {
		if (obj instanceof WTPart) {
			return true;
		}
		return false;
	}

	/**
	 * 获取文件所有的PDF附件
	 * 
	 * @param holder
	 * @return
	 * @throws WTException
	 */
	public static Set<Object[]> getPDFFromDrawingFileDoc(ContentHolder holder, boolean isThrowEx) throws WTException {
		Set<Object[]> set = new HashSet<Object[]>();
		try {
			QueryResult qr = null;
			if (isCATIADrawing(holder)){//CAT取 表示法图纸
				EPMDocument epm = (EPMDocument)holder;
				Representation r = RepresentationHelper.service.getDefaultRepresentation(epm);
				qr = ContentHelper.service.getContentsByRole(r, ContentRoleType.SECONDARY);
			}else{
				qr = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);
			}
			while (qr.hasMoreElements()) {
				ApplicationData fileContent = (ApplicationData) qr.nextElement();
				String strFileName = "";
				try{
					strFileName = URLDecoder.decode(fileContent.getFileName(), "utf-8");
				}catch(IllegalArgumentException e){//此处可能会有异常URLDecoder: Illegal hex characters in escape (%) pattern - For input string: " 7"
					e.printStackTrace();
				}
				
				if (strFileName.toUpperCase().endsWith(RELEASE_Suffix + PDF_Suffix)) {
					if (isThrowEx) {
						set = null;
						break;
						//throw new WTException(strFileName + " PDF图纸附件的DC签名文件已经存在！");
					} else {
						set.clear();
						break;
					}
				} else if (strFileName.toUpperCase().endsWith(PDF_Suffix)) {
					if(holder instanceof WTPart){
						WTPart part = (WTPart)holder;
						String number = part.getNumber();
						if(strFileName.equalsIgnoreCase(number+".pdf")){
							log.info("==part==getPDFFromDrawingFileDoc==strFileName:" + strFileName);
							Object[] objs = new Object[2];
							objs[0] = strFileName;
							objs[1] = ContentServerHelper.service.findContentStream(fileContent);
							set.add(objs);
						}
					}else if(isAutoCADDoc(holder)){
						WTDocument doc = (WTDocument) holder;
						if(strFileName.equalsIgnoreCase(doc.getNumber()+".pdf")){
							log.info("==doc==getPDFFromDrawingFileDoc==strFileName:" + strFileName);
							Object[] objs = new Object[2];
							objs[0] = strFileName;
							objs[1] = ContentServerHelper.service.findContentStream(fileContent);
							set.add(objs);
						}
					}else if(isCATIADrawing(holder)){
						log.info("==epm==getPDFFromDrawingFileDoc==strFileName:" + strFileName);
						EPMDocument epm = (EPMDocument)holder;
						Object[] objs = new Object[2];
						String primaryFileName = epm.getNumber();
						if(primaryFileName.contains(".")){
							objs[0] = primaryFileName.substring(0, primaryFileName.lastIndexOf("."))+".pdf";
						}else{
							objs[0] = primaryFileName+".pdf";
						}
						objs[1] = ContentServerHelper.service.findContentStream(fileContent);
						set.add(objs);
					}
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new WTException(e.getLocalizedMessage());
		}
		return set;
	}

	/**
	 * 获得Part的PDF附件
	 * 
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public static Set<Object[]> getPDFFromWTPart(WTPart part) throws WTException {
		Set<Object[]> set = new HashSet<Object[]>();
		try {
			QueryResult qr = ContentHelper.service.getContentsByRole(part, ContentRoleType.SECONDARY);
			while (qr.hasMoreElements()) {
				ApplicationData fileContent = (ApplicationData) qr.nextElement();
				String strFileName = URLDecoder.decode(fileContent.getFileName(), "utf-8");
				if (StringUtils.equalsIgnoreCase(strFileName, part.getNumber() + RELEASE_Suffix + ".pdf")) {
					set.clear();
					break;
				} else if (StringUtils.equalsIgnoreCase(strFileName, part.getNumber() + ".pdf")) {
					log.info("==getPDFFromWTPart==strFileName:" + strFileName);
					Object[] objs = new Object[2];
					objs[0] = strFileName;
					objs[1] = ContentServerHelper.service.findContentStream(fileContent);
					set.add(objs);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new WTException(e.getLocalizedMessage());
		}
		return set;
	}

	/**
	 * 获取印章图片对象
	 * 
	 * @param positionX
	 * @param positionY
	 * @return
	 */
	public static Image getPrintImange(int positionX, int positionY) {
		String path = WCLocationConstants.WT_CODEBASE + File.separator + "com" + File.separator + "catl" + File.separator + "pdfsignet" + File.separator + "CATL_Signet.png";
		Image img = null;
		try {
			img = PngImage.getImage(new FileInputStream(new File(path)));
			img.scaleToFit(60, 60);
			img.setAbsolutePosition(positionX, positionY);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return img;
	}

	/**
	 * 加密PDF文件
	 * 
	 * @param stamper
	 * @throws WTException
	 */
	private static void encryptionPDF(PdfStamper stamper, String pass) throws WTException {
		try {
			stamper.setEncryption(null, pass.getBytes(), PdfWriter.ALLOW_PRINTING | PdfWriter.ALLOW_SCREENREADERS, PdfWriter.ENCRYPTION_AES_128);
		} catch (Exception e) {
			e.printStackTrace();
			throw new WTException(e.getLocalizedMessage());
		}
	}

	/**
	 * 获得口令加密密码
	 * 
	 * @return
	 * @throws WTException
	 */
	public static String getEncryptionPW() throws WTException {
		String pass = GenericUtil.getPreferenceValue("/catl/PDF_Encrypted_PW");
		log.info("==getEncryptionPW==pass:" + pass);
		return pass;
	}

	/**
	 * 创建临时输出文件存放路径
	 */
	private static void mkDirs() {
		String dir = getTempFilePath();
		File file = new File(dir);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	/**
	 * 获取输出文件的存放位置
	 * 
	 * @param userName
	 * @return
	 */
	private static String getTempFilePath() {
		String folder = WCLocationConstants.WT_TEMP + File.separator + "PDFFiles";
		log.info("==getOutFileFolder==folder:" + folder);
		return folder;
	}

	/**
	 * 获取最终的临时文件名称
	 * 
	 * @param fileName
	 * @return
	 */
	private static String getTempFileName(String fileName) {
		if (StringUtils.isNotBlank(fileName)) {
			StringBuilder temp = new StringBuilder(fileName);
			temp.insert(fileName.lastIndexOf("."), RELEASE_Suffix);
			log.info("==getTempFileName==tempFileName:" + temp);
			return temp.toString();
		}
		return "";
	}

	/**
	 * 获得输出文件的完整路径
	 * 
	 * @param fileName
	 * @return
	 */
	private static String getTempFile(String tempFileName) {
		String path = getTempFilePath() + File.separator + tempFileName;
		log.info("==getOutFilePath==path:" + path);
		return path;
	}

	/**
	 * 删除临时文件
	 * 
	 * @param fileName
	 */
	private static void deleteTempFile(String tempFileName) {
		File file = new File(getTempFile(tempFileName));
		if (file.exists()) {
			file.delete();
		}
	}

}
