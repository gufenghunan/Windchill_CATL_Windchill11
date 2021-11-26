package com.catl.doc;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;
import java.util.Vector;

import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFShape;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFTextbox;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTextBox;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.content.FormatContentHolder;
import wt.doc.WTDocument;
import wt.doc.WTDocumentDependencyLink;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.iba.definition.TimestampDefinition;
import wt.iba.value.TimestampValue;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.pds.StatementSpec;
import wt.pom.Transaction;
import wt.query.ConstantExpression;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.TableColumn;
import wt.services.applicationcontext.implementation.ServiceProperties;
import wt.session.SessionHelper;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.util.WTInvalidParameterException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.vc.config.LatestConfigSpec;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

import com.catl.common.constant.DocState;
import com.catl.common.constant.PartState;
import com.catl.common.util.CatlConstant;
import com.catl.common.util.DocUtil;
import com.catl.common.util.GenericUtil;
import com.catl.loadData.IBAUtility;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.prolog.pub.RunTimeException;

public final class DocInvalidUtil implements RemoteAccess {

	private static Logger log = Logger.getLogger(DocInvalidUtil.class.getName());
	
	public static SimpleDateFormat DATE_FORMATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static void main(String[] args) throws Exception {
		RemoteMethodServer rm = RemoteMethodServer.getDefault();
		rm.setUserName("wcadmin");
		rm.setPassword("wcadmin");
		rm.invoke("Test", DocInvalidUtil.class.getName(), null, null, null); // 远程调用
	}
	/**远程调用测试方法
	 * 
	 * @throws Exception
	 */
	public static void Test() throws Exception {
		
		//updateDocSX(getWTDocumentByNumber("APD-00000506") );
		updateDoc1(getWTDocumentByNumber("ENW-00001027"));
	}
	
	/**
	 * 文档修订后前一个版本失效
	 * @param doc
	 * @throws Exception
	 */
	public  static void updateDocSX(WTDocument doc) throws Exception{
			 String docNum = doc.getNumber();
			 String docType = "";
			 if (docNum.indexOf("-")!=-1) {
				 docType = docNum.substring(0, docNum.indexOf("-"));
			}
		     Properties wtproperties = ServiceProperties.getServiceProperties("WTServiceProviderFromProperties");
			 String couldVoidType = wtproperties.getProperty("couldVoidType");
			 if (couldVoidType.indexOf(docType)!=-1) {
				String version = doc.getVersionInfo().getIdentifier().getValue();
				if (!version.equals("A")) {
					version = (char)(version.charAt(0) - 1) + "";
					WTDocument oldDoc = getDocByNumberVersion(docNum,version);
					updateDoc1(oldDoc);
					
				}
				
			}
		
		
	}
	
	/**
	 *给文档打水印，图文当流程
	 * @param doc
	 * @throws Exception
	 */
	public static void updateDoc(WTDocument doc) throws Exception{
		Transaction trx = null;
		InputStream in = null;
		ByteArrayOutputStream os = null;
		try {
			trx = new Transaction();
			trx.start();
			checkENWDoc(doc);
			docAfterWfOpe(doc);
			WTPrincipal wtadministrator = SessionHelper.manager.getAdministrator();
			WTUser previous = (WTUser) SessionHelper.manager.getPrincipal();
			SessionHelper.manager.setPrincipal(wtadministrator.getName());
			String replaceDocNumber = (String)GenericUtil.getObjectAttributeValue(doc, CatlConstant.CATL_DOC_REPLACEDOC);
			if (StringUtils.isNotEmpty(replaceDocNumber)){
				WTDocument replacedoc = DocUtil.getLatestWTDocument(replaceDocNumber);
				if (replacedoc != null) {
				ContentHolder contentHolder = ContentHelper.service.getContents(replacedoc);
				ContentItem contentItem = ContentHelper.getPrimary((FormatContentHolder) contentHolder);
				ApplicationData applicationData = (ApplicationData) contentItem;
				in = ContentServerHelper.service.findContentStream(applicationData);
				String fileName = applicationData.getFileName();
				if (fileName.endsWith(".xls")) {
					excel2003(doc,replacedoc, fileName, in, os);
				}else if(fileName.endsWith(".xlsx")){
					 excel2007(doc,replacedoc, fileName, in, os);
				}else if(fileName.endsWith(".docx")){
					word(doc,replacedoc, fileName, in,os);
				}else if(fileName.endsWith(".pdf")){
					addWatermark(doc,replacedoc,fileName,in,os);
				}else if(fileName.endsWith(".pptx")){
					replaceDocPrimaryContent(doc,replacedoc, in, fileName, 1024);
				}
				}
				SessionHelper.manager.setPrincipal(previous.getName());
				trx.commit();
				trx = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
	        throw new RunTimeException(e.getLocalizedMessage());
		} finally {
			if (trx != null) {
				trx.rollback();
			}if (in!=null) {
				in.close();
			}if (os!=null) {
				os.close();
			}
		}
	}
	
	
	/**
	 *给文档打水印,失效流程和定时任务
	 * @param doc
	 * @throws Exception
	 */
	public static void updateDoc1(WTDocument doc) throws Exception{
		Transaction trx = null;
		InputStream in = null;
		ByteArrayOutputStream os = null;
		try {
			trx = new Transaction();
			trx.start();
			WTPrincipal wtadministrator = SessionHelper.manager.getAdministrator();
			WTUser previous = (WTUser) SessionHelper.manager.getPrincipal();
			SessionHelper.manager.setPrincipal(wtadministrator.getName());
				ContentHolder contentHolder = ContentHelper.service.getContents(doc);
				ContentItem contentItem = ContentHelper.getPrimary((FormatContentHolder) contentHolder);
				ApplicationData applicationData = (ApplicationData) contentItem;
				in = ContentServerHelper.service.findContentStream(applicationData);
				String fileName = applicationData.getFileName();
				if (fileName.endsWith(".xls")) {
					excel2003(doc, fileName, in, os);
				}else if(fileName.endsWith(".xlsx")){
					 excel2007(doc, fileName, in, os);
				}else if(fileName.endsWith(".docx")){
					word(doc, fileName, in,os);
				}else if(fileName.endsWith(".pdf")){
					addWatermark(doc,fileName,in,os);
				}else if(fileName.endsWith(".pptx")){
					replaceDocPrimaryContent(doc,in, fileName, 1024);
					docAfterWfOpeAll(doc);
				}else{
					docAfterWfOpeAll(doc);
				}
				SessionHelper.manager.setPrincipal(previous.getName());
				trx.commit();
				trx = null;
		} catch (Exception e) {
			e.printStackTrace();
	        throw new RunTimeException(e.getLocalizedMessage());
		} finally {
			if (trx != null) {
				trx.rollback();
			}if (in!=null) {
				in.close();
			}if (os!=null) {
				os.close();
			}
		}
	}
	
	/**
	 * pdf水印，失效流程
	 * @param doc
	 * @param fileName
	 * @param in
	 * @param os
	 * @throws IOException
	 * @throws DocumentException
	 */
	public static void addWatermark(WTDocument doc,String fileName,InputStream in, ByteArrayOutputStream os) throws IOException, DocumentException {
		PdfReader pdfReader = new PdfReader(in);
		os = new ByteArrayOutputStream();
		PdfStamper pdfStamper = new PdfStamper(pdfReader, os);
		PdfContentByte content = null;
		BaseFont base = null;
		Rectangle pageRect = null;
		PdfGState gs = new PdfGState();
		try {
			// 设置字体
			base = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",BaseFont.EMBEDDED);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			if (base == null || pdfStamper == null) {
				return;
			}
			// 设置透明度为0.4
			gs.setFillOpacity(0.4f);
			gs.setStrokeOpacity(0.4f);
			int toPage = pdfStamper.getReader().getNumberOfPages();
			for (int i = 1; i <= toPage; i++) {
				pageRect = pdfStamper.getReader().getPageSizeWithRotation(i);
				// 计算水印X,Y坐标
				float x = pageRect.getWidth() / 2;
				float y = pageRect.getHeight() / 2;
				//获得PDF最顶层
				content = pdfStamper.getOverContent(i);
				content.saveState();
				// set Transparency
				content.setGState(gs);
				content.beginText();
				content.setColorFill(BaseColor.GRAY);
				content.setFontAndSize(base, 100);
				 // 水印文字成45度角倾斜
				content.showTextAligned(Element.ALIGN_CENTER, "文档已失效", x,y, 45);
				content.endText();
			}
			pdfStamper.close();
		ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
		replaceDocPrimaryContent(doc, is, fileName, 1024); 
		if (is!=null) {
			is.close();
		}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			content = null;
			base = null;
			pageRect = null;
		}
	}
	
	
	/**
	 * pdf水印，图文档流程
	 * @param pdoc
	 * @param doc
	 * @param fileName
	 * @param in
	 * @param os
	 * @throws IOException
	 * @throws DocumentException
	 */
	public static void addWatermark(WTDocument pdoc,WTDocument doc,String fileName,InputStream in, ByteArrayOutputStream os) throws IOException, DocumentException {
		PdfReader pdfReader = new PdfReader(in);
		os = new ByteArrayOutputStream();
		PdfStamper pdfStamper = new PdfStamper(pdfReader, os);
		PdfContentByte content = null;
		BaseFont base = null;
		Rectangle pageRect = null;
		PdfGState gs = new PdfGState();
		try {
			// 设置字体
			base = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",BaseFont.EMBEDDED);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			if (base == null || pdfStamper == null) {
				return;
			}
			// 设置透明度为0.4
			gs.setFillOpacity(0.4f);
			gs.setStrokeOpacity(0.4f);
			int toPage = pdfStamper.getReader().getNumberOfPages();
			for (int i = 1; i <= toPage; i++) {
				pageRect = pdfStamper.getReader().getPageSizeWithRotation(i);
				// 计算水印X,Y坐标
				float x = pageRect.getWidth() / 2;
				float y = pageRect.getHeight() / 2;
				//获得PDF最顶层
				content = pdfStamper.getOverContent(i);
				content.saveState();
				// set Transparency
				content.setGState(gs);
				content.beginText();
				content.setColorFill(BaseColor.GRAY);
				content.setFontAndSize(base, 100);
				 // 水印文字成45度角倾斜
				content.showTextAligned(Element.ALIGN_CENTER, "文档已失效", x,y, 45);
				content.endText();
			}
			pdfStamper.close();
		ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
		replaceDocPrimaryContent(pdoc,doc, is, fileName, 1024); 
		if (is!=null) {
			is.close();
		}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			content = null;
			base = null;
			pageRect = null;
		}
	}

	
	 /**  
     * 打印文字水印图片  失效流程
     *  
     */  
    
    public static void word(WTDocument doc,String fileName,InputStream in,ByteArrayOutputStream os) throws Exception {   
        	ByteArrayInputStream is = null;
            XWPFDocument xDoc = new XWPFDocument(in);
            XWPFHeaderFooterPolicy xFooter = new XWPFHeaderFooterPolicy(xDoc);
            xFooter.createWatermark("文档已失效");
            os = new ByteArrayOutputStream();	
            xDoc.write(os);
    		is = new ByteArrayInputStream(os.toByteArray());
    		replaceDocPrimaryContent(doc, is, fileName, 1024); 
    		
    		if (is!=null) {
				is.close();
			}
    }  
  
    
    /**  
     * 打印文字水印图片  图文当流程
     *   
     * 
     */  
    
    public static void word(WTDocument pdoc,WTDocument doc,String fileName,InputStream in,ByteArrayOutputStream os) throws Exception {   
    	ByteArrayInputStream is = null;
        XWPFDocument xDoc = new XWPFDocument(in);
        XWPFHeaderFooterPolicy xFooter = new XWPFHeaderFooterPolicy(xDoc);
        xFooter.createWatermark("文档已失效");
        os = new ByteArrayOutputStream();	
        xDoc.write(os);
		is = new ByteArrayInputStream(os.toByteArray());
    	replaceDocPrimaryContent(pdoc,doc, is, fileName, 1024); 
    		
    		if (is!=null) {
				is.close();
			}
    } 
    
    /**
     * excel水印,失效流程
     * @param doc
     * @param fileName
     * @param in
     * @param os
     * @throws Exception
     */
    public static void excel2003(WTDocument doc,String fileName,InputStream in,ByteArrayOutputStream os) throws Exception { 
    	ByteArrayInputStream is = null;
    	HSSFWorkbook wb = (HSSFWorkbook) WorkbookFactory.create(in);
        HSSFSheet sheet = null;
        int sheetNumbers = wb.getNumberOfSheets();
        // sheet
        for (int i = 0; i < sheetNumbers; i++) {
            sheet = wb.getSheetAt(i);
            //sheet.createDrawingPatriarch();
            
//            HSSFPatriarch dp = sheet.createDrawingPatriarch();
//            HSSFClientAnchor anchor = new HSSFClientAnchor(0,255,255,0,(short)0,1,(short)10,10);
//            
//            //HSSFComment comment = dp.createComment(anchor);
//	   	    HSSFTextbox txtbox = dp.createTextbox(anchor);
//	   	   	
//	   	    HSSFRichTextString rtxt = new HSSFRichTextString("文档已失效");
//	   	   	HSSFFont draftFont = (HSSFFont) wb.createFont();
//	   	   	//水印颜色
//	   	   	draftFont.setColor(HSSFColor.GREY_25_PERCENT.index);
//	   	   	draftFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
//	   	   	//字体大小
//	   	   	draftFont.setFontHeightInPoints((short) 100);
//	   	   	draftFont.setFontName("Verdana");
//	   	   	rtxt.applyFont(draftFont);
//	   	   	txtbox.setString(rtxt);
//	   	   	//倾斜度
//	   	   	txtbox.setRotationDegree((short)315);
//	   	   	txtbox.setLineWidth(600);
//	   	   	txtbox.setLineStyle(HSSFShape.LINESTYLE_NONE);
//	   	   	txtbox.setNoFill(true);
            String waterRemarkPath  = getUrl("netmarkets/images/catl/watermark_excel.png");
            putWaterRemarkToExcel(wb, sheet, waterRemarkPath, 0, 5, 50, 50, 1, 1,10, 10); 
        }
        os = new ByteArrayOutputStream();	
		wb.write(os);
		is = new ByteArrayInputStream(os.toByteArray());
		replaceDocPrimaryContent(doc, is, fileName, 1024); 
		if (is!=null) {
			is.close();
		}
    }
    /**
     * excel，图文档流程
     * @param pdoc
     * @param doc
     * @param fileName
     * @param in
     * @param os
     * @throws Exception
     */
    public static void excel2003(WTDocument pdoc,WTDocument doc,String fileName,InputStream in,ByteArrayOutputStream os) throws Exception { 
    	ByteArrayInputStream is = null;
    	HSSFWorkbook wb = (HSSFWorkbook) WorkbookFactory.create(in);
        HSSFSheet sheet = null;
        int sheetNumbers = wb.getNumberOfSheets();
        // sheet
        for (int i = 0; i < sheetNumbers; i++) {
            sheet = wb.getSheetAt(i);
//            //sheet.createDrawingPatriarch();
//            
//            HSSFPatriarch dp = sheet.createDrawingPatriarch();
//            HSSFClientAnchor anchor = new HSSFClientAnchor(0,255,255,0,(short)0,1,(short)10,10);
//            
//            //HSSFComment comment = dp.createComment(anchor);
//	   	    HSSFTextbox txtbox = dp.createTextbox(anchor);
//	   	   	
//	   	    HSSFRichTextString rtxt = new HSSFRichTextString("文档已失效");
//	   	   	HSSFFont draftFont = (HSSFFont) wb.createFont();
//	   	   	//水印颜色
//	   	   	draftFont.setColor(HSSFColor.GREY_25_PERCENT.index);
//	   	   	draftFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
//	   	   	//字体大小
//	   	   	draftFont.setFontHeightInPoints((short) 100);
//	   	   	draftFont.setFontName("Verdana");
//	   	   	rtxt.applyFont(draftFont);
//	   	   	txtbox.setString(rtxt);
//	   	   	//倾斜度
//	   	   	txtbox.setRotationDegree((short)315);
//	   	   	txtbox.setLineWidth(600);
//	   	   	txtbox.setLineStyle(HSSFShape.LINESTYLE_NONE);
//	   	   	txtbox.setNoFill(true);
            String waterRemarkPath  = getUrl("netmarkets/images/catl/watermark_excel.png");
            putWaterRemarkToExcel(wb, sheet, waterRemarkPath, 0, 5, 50, 50, 1, 1,10, 10); 
        }
        os = new ByteArrayOutputStream();	
		wb.write(os);
		is = new ByteArrayInputStream(os.toByteArray());
		replaceDocPrimaryContent(pdoc,doc, is, fileName, 1024); 
		if (is!=null) {
			is.close();
		}
    }
    

    /**
     * 失效流程
     * @param doc
     * @param fileName
     * @param in
     * @param os
     * @throws Exception
     */
    public static void excel2007(WTDocument doc,String fileName,InputStream in,ByteArrayOutputStream os) throws Exception { 
    	ByteArrayInputStream is = null;
    	XSSFWorkbook wb = (XSSFWorkbook) WorkbookFactory.create(in);
        XSSFSheet sheet = null;
        int sheetNumbers = wb.getNumberOfSheets();
        for (int i = 0; i < sheetNumbers; i++) {
            sheet = wb.getSheetAt(i);
//            XSSFDrawing dp =sheet.createDrawingPatriarch();
//            XSSFClientAnchor anchor = new XSSFClientAnchor(0,550,550,0,(short)0,1,(short)10,10);
//	   	    XSSFTextBox txtbox = dp.createTextbox(anchor);
//	   	   	XSSFRichTextString rtxt = new XSSFRichTextString("文档已失效 ");
//	   	   	XSSFFont draftFont = (XSSFFont) wb.createFont();
//	   	   	draftFont.setColor(HSSFColor.GREY_25_PERCENT.index);
//	   	   	draftFont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
//	   	   	draftFont.setFontHeightInPoints((short) 100);
//	   	   	draftFont.setFontName("Verdana");
//	   	   	rtxt.applyFont(draftFont);
//	   	   	txtbox.setText(rtxt);
//	   		//倾斜度
//	   	   	txtbox.setLineWidth(600);
//	   	   	txtbox.setNoFill(true);
	   	 String waterRemarkPath  = getUrl("netmarkets/images/catl/watermark_excel.png");
         putWaterRemarkToExcel(wb, sheet, waterRemarkPath, 0, 5, 50, 50, 1, 1,10, 10); 
        }
        os = new ByteArrayOutputStream();	
		wb.write(os);
		is = new ByteArrayInputStream(os.toByteArray());
		replaceDocPrimaryContent(doc, is, fileName, 1024); 
		if (is!=null) {
			is.close();
		}
    }
    
    /**
     * 图文档流程
     * @param pdoc
     * @param doc
     * @param fileName
     * @param in
     * @param os
     * @throws Exception
     */
    public static void excel2007(WTDocument pdoc,WTDocument doc,String fileName,InputStream in,ByteArrayOutputStream os) throws Exception { 
    	ByteArrayInputStream is = null;
    	XSSFWorkbook wb = (XSSFWorkbook) WorkbookFactory.create(in);
        XSSFSheet sheet = null;
        int sheetNumbers = wb.getNumberOfSheets();
        for (int i = 0; i < sheetNumbers; i++) {
            sheet = wb.getSheetAt(i);
//            XSSFDrawing dp =sheet.createDrawingPatriarch();
//            XSSFClientAnchor anchor = new XSSFClientAnchor(0,550,550,0,(short)0,1,(short)10,10);
//	   	    XSSFTextBox txtbox = dp.createTextbox(anchor);
//	   	   	XSSFRichTextString rtxt = new XSSFRichTextString("文档已失效 ");
//	   	   	XSSFFont draftFont = (XSSFFont) wb.createFont();
//	   	   	draftFont.setColor(HSSFColor.GREY_25_PERCENT.index);
//	   	   	draftFont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
//	   	   	draftFont.setFontHeightInPoints((short) 100);
//	   	   	draftFont.setFontName("Verdana");
//	   	   	rtxt.applyFont(draftFont);
//	   	   	txtbox.setText(rtxt);
//	   		//倾斜度
//	   	   	txtbox.setLineWidth(600);
//	   	   	txtbox.setNoFill(true);
            String waterRemarkPath  = getUrl("netmarkets/images/catl/watermark_excel.png");
            putWaterRemarkToExcel(wb, sheet, waterRemarkPath, 0, 5, 50, 50, 1, 1,10, 10); 
	   	   	
        }
        os = new ByteArrayOutputStream();	
		wb.write(os);
		is = new ByteArrayInputStream(os.toByteArray());
		replaceDocPrimaryContent(pdoc,doc, is, fileName, 1024); 
		if (is!=null) {
			is.close();
		}
    }
    
    /**
 	  * 为Excel打上水印工具函数
     * 请自行确保参数值，以保证水印图片之间不会覆盖。
     * 在计算水印的位置的时候，并没有考虑到单元格合并的情况，请注意
     * @param wb       Excel Workbook
     * @param sheet    需要打水印的Excel
     * @param waterRemarkPath  水印地址，classPath，目前只支持png格式的图片，
     * 因为非png格式的图片打到Excel上后可能会有图片变红的问题，且不容易做出透明效果。
     * 同时请注意传入的地址格式，应该为类似："\\excelTemplate\\test.png"
     * @param startXCol  水印起始列
     * @param startYRow  水印起始行
     * @param betweenXCol 水印横向之间间隔多少列
     * @param betweenYRow 水印纵向之间间隔多少行
     * @param XCount 横向共有水印多少个
     * @param YCount 纵向共有水印多少个
     * @param waterRemarkWidth 水印图片宽度为多少列
     * @param waterRemarkHeight 水印图片高度为多少行
     * @throws IOException 
     */
public static void putWaterRemarkToExcel(Workbook wb, Sheet sheet,
		String waterRemarkPath, int startXCol, int startYRow,
		int betweenXCol, int betweenYRow, int XCount, int YCount,
		int waterRemarkWidth, int waterRemarkHeight) throws IOException {

	// 校验传入的水印图片格式
	if (!waterRemarkPath.endsWith("png")
			&& !waterRemarkPath.endsWith("PNG")) {
		throw new RuntimeException("向Excel上面打印水印，目前支持png格式的图片。");
	}

	// 加载图片
	ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
	InputStream imageIn = new FileInputStream(new File(waterRemarkPath));
	
	if (null == imageIn || imageIn.available() < 1) {
		throw new RuntimeException("向Excel上面打印水印，读取水印图片失败(1)。");
	}
	BufferedImage bufferImg = ImageIO.read(imageIn);
	if (null == bufferImg) {
		throw new RuntimeException("向Excel上面打印水印，读取水印图片失败(2)。");
	}
	ImageIO.write(bufferImg, "png", byteArrayOut);
	
	// 开始打水印
	Drawing drawing = sheet.createDrawingPatriarch();
	

	// 按照共需打印多少行水印进行循环
	for (int yCount = 0; yCount < YCount; yCount++) {
		// 按照每行需要打印多少个水印进行循环
		for (int xCount = 0; xCount < XCount; xCount++) {
			// 创建水印图片位置
			int xIndexInteger = startXCol + (xCount * waterRemarkWidth)
					+ (xCount * betweenXCol);
			int yIndexInteger = startYRow + (yCount * waterRemarkHeight)
					+ (yCount * betweenYRow);

			/*
			  * 参数定义：
			  * 第一个参数是（x轴的开始节点）；
			  * 第二个参数是（是y轴的开始节点）；
			  * 第三个参数是（是x轴的结束节点）；
			  * 第四个参数是（是y轴的结束节点）；
			  * 第五个参数是（是从Excel的第几列开始插入图片，从0开始计数）；
			  * 第六个参数是（是从excel的第几行开始插入图片，从0开始计数）；
			  * 第七个参数是（图片宽度，共多少列）；
			  * 第8个参数是（图片高度，共多少行）；
			 */
			ClientAnchor anchor = drawing.createAnchor(0, 0,
					225, 225, xIndexInteger,
					yIndexInteger, waterRemarkWidth, waterRemarkHeight);
			Picture pic = drawing.createPicture(anchor, wb.addPicture(
					byteArrayOut.toByteArray(), Workbook.PICTURE_TYPE_PNG));
			pic.resize();
			 
		}
	}
}


    
	public static void setActualvoidtime (WTDocument doc) {
		
		//设置实际失效日期
		try {
			
//			Folder myCOFolder = null;
//			myCOFolder = WorkInProgressHelper.service.getCheckoutFolder();
//			// 判断工作副本是否是检出状态
//			if (!WorkInProgressHelper.isCheckedOut(doc)) {
//				
//				WorkInProgressHelper.service.checkout(doc,myCOFolder, null);
//				doc = (WTDocument) WorkInProgressHelper.service.workingCopyOf(doc);
//			}
//			
//            PersistableAdapter genericObj = new PersistableAdapter(doc, null, null, new UpdateOperationIdentifier());
//			genericObj.load(CatlConstant.CATL_DOC_ACTUALVOIDTIME);
//			genericObj.set(CatlConstant.CATL_DOC_ACTUALVOIDTIME, stringToTimestamp(getCurrentDate()));
//			Persistable updatedObject = genericObj.apply();
//			doc = (WTDocument) PersistenceHelper.manager.save(updatedObject);
//			
//			WorkInProgressHelper.service.checkin(doc, "");
			IBAUtility iba = new IBAUtility(doc);
			iba.setIBAValue(CatlConstant.CATL_DOC_ACTUALVOIDTIME, getCurrentDate());
			iba.updateAttributeContainer(doc);
            iba.updateIBAHolder(doc);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void setActualvoidtime (WTDocument doc, String date) {
		
		//设置实际失效日期
		try {
			
			IBAUtility iba = new IBAUtility(doc);
			iba.setIBAValue(CatlConstant.CATL_DOC_ACTUALVOIDTIME, date);
			iba.updateAttributeContainer(doc);
            iba.updateIBAHolder(doc);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Vector<WTDocument> getENWDoc(String queryTime) throws WTException {

		Vector<WTDocument> wtdoc = new Vector<WTDocument>();

		Properties wtproperties = ServiceProperties.getServiceProperties("WTServiceProviderFromProperties");
		String docProperty = wtproperties.getProperty("ENWDocType");
		List<String> doctypelist = new ArrayList<String>();
        if(StringUtils.isNotEmpty(docProperty)){
        	
        	String[] docTypeArr = docProperty.split(",");
        	doctypelist = Arrays.asList(docTypeArr); 
        }
        
        if (doctypelist.size() == 0){
        	return wtdoc;
        }
        
        Timestamp timestamp1 = null;
        
        if (StringUtils.isNotEmpty(queryTime)){
        	timestamp1 = stringToTimestamp(queryTime);
        }
        
		QuerySpec qs;
		try {
			qs = new QuerySpec();
			qs.setAdvancedQueryEnabled(true);
			int index0 = qs.appendClassList(WTDocument.class, true);
			int index1 = qs.appendClassList(TimestampValue.class, false);
			int index2 = qs.appendClassList(TimestampDefinition.class, false);

			SearchCondition join = new SearchCondition(WTDocument.class, WTAttributeNameIfc.ID_NAME, TimestampValue.class,TimestampValue.IBAHOLDER_REFERENCE+"."+WTAttributeNameIfc.REF_OBJECT_ID);
			SearchCondition join1 = new SearchCondition(TimestampValue.class, TimestampValue.DEFINITION_REFERENCE+"."+WTAttributeNameIfc.REF_OBJECT_ID, TimestampDefinition.class, WTAttributeNameIfc.ID_NAME);
			qs.appendWhere(join, new int[]{index0,index1});
			qs.appendAnd();
			qs.appendWhere(join1, new int[]{index1,index2});
			qs.appendAnd();
			
			qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.LATEST_ITERATION, SearchCondition.IS_TRUE), new int[] { index0 });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.LIFE_CYCLE_STATE, SearchCondition.EQUAL, DocState.RELEASED), new int[] { index0 });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(TimestampValue.class, TimestampValue.VALUE, SearchCondition.LESS_THAN_OR_EQUAL, timestamp1), new int[] { index1 });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(TimestampValue.class, TimestampValue.IBAHOLDER_REFERENCE + "." + WTAttributeNameIfc.REF_CLASSNAME, SearchCondition.EQUAL, WTDocument.class.getName()), new int[] { index1 });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(TimestampDefinition.class, TimestampDefinition.NAME, SearchCondition.EQUAL, CatlConstant.CATL_DOC_ESTIMATEDVOIDTIME), new int[] { index2 });
			qs.appendAnd();
			qs.appendOpenParen();
			for (int i=0; i < doctypelist.size(); i++){
	        	
	        	String type = doctypelist.get(i);
	        	qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.LIKE, type+"-%"), new int[] { index0 });
	            if (i != doctypelist.size()-1){
	            	qs.appendOr();
	            }
	        }
			qs.appendCloseParen();
			
			log.debug("==QuerySQL:" + qs.toString());
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
			Persistable[] p = null;
			List<String> docNumListr = new ArrayList<String>();
			while (qr.hasMoreElements()) {
				p = (Persistable[]) qr.nextElement();
				WTDocument doc = (WTDocument) p[0];
				if (!docNumListr.contains(doc.getNumber()) && DocUtil.isLastedWTDocument(doc)){
					wtdoc.add(doc);
					docNumListr.add(doc.getNumber());
				}
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}

		return wtdoc;
	}
	
//	public static Vector<WTDocument> getENWDoc(String queryTime) throws Exception {
//
//		Vector<WTDocument> wtdoc = new Vector<WTDocument>();
//
//        MethodContext context = MethodContext.getContext();
//        WTConnection wtConn = (WTConnection)context.getConnection();
//        PreparedStatement statement = null;
//        ResultSet resultSet = null;
//        try{
//        	String sql = "SELECT A1.WTDOCUMENTNUMBER FROM WTDOCUMENT A0";
//        	sql += " LEFT JOIN WTDOCUMENTMASTER A1";
//        	sql += " ON A0.IDA3MASTERREFERENCE = A1.IDA2A2";
//        	sql += " LEFT JOIN (SELECT A3.NAME,A2.* FROM TimestampValue A2,TimestampDefinition A3";
//        	sql += " WHERE A2.idA3A6 = A3.idA2A2";
//        	sql += " AND A3.NAME = '"+ CatlConstant.CATL_DOC_ESTIMATEDVOIDTIME +"') B1";
//        	sql += " ON A0.IDA2A2 = B1.IDA3A4";
//            
//            sql += " LEFT JOIN (SELECT A3.NAME,A2.* FROM TimestampValue A2,TimestampDefinition A3";
//            sql += " WHERE A2.idA3A6 = A3.idA2A2";
//            sql += " AND A3.NAME = '"+ CatlConstant.CATL_DOC_ACTUALVOIDTIME +"') B2";
//            sql += " ON A0.IDA2A2 = B2.IDA3A4";
//            sql += " WHERE A1.WTDocumentNumber LIKE 'ENW-%'";
//            sql += " AND B1.VALUE < TO_DATE('"+ queryTime +"','YYYY:MM:DD:HH24:MI:SS')";
//            sql += " AND B2.VALUE IS NULL";
//            
//            statement = wtConn.prepareStatement(sql);
//            resultSet = statement.executeQuery();
//            List<String> docNumListr = new ArrayList<String>();
//            while(resultSet.next()){
//                String docNumber = resultSet.getString("WTDOCUMENTNUMBER");
//                if (!docNumListr.contains(docNumber)){
//                	WTDocument doc = DocUtil.getLatestWTDocument(docNumber);
//                	wtdoc.add(doc);
//                	docNumListr.add(docNumber);
//                }
//                
//            }
//            
//        }catch(Exception e){
//            e.printStackTrace();
//        }finally{
//            if(statement != null) statement.close();
//            if(wtConn != null && wtConn.isActive()) wtConn.release();
//        }
//
//		return wtdoc;
//	}
	
	public static Timestamp stringToTimestamp(String tsStr) {
		
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		try {
			ts = Timestamp.valueOf(tsStr);
			System.out.println(ts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ts;
	}
	
	public static StringBuffer checkENWSubmit(List<ObjectBean> list) throws WTInvalidParameterException, WTException {
		StringBuffer message = new StringBuffer();

		// 获取表单信息 细类及文档分类
		WTDocument doc = (WTDocument) list.get(0).getObject();
		String docSubName = (String) GenericUtil.getObjectAttributeValue(doc, "subCategory");
		
		if (StringUtils.isNotEmpty(docSubName) && docSubName.toUpperCase().endsWith("-ENW")){
			
			String estimatedVoidTime = GenericUtil.getObjectAttributeValue(doc, "CATL_DOC_EstimatedVoidTime") + "";
			String replaceDocNumber =(String)GenericUtil.getObjectAttributeValue(doc, CatlConstant.CATL_DOC_REPLACEDOC);
			if (StringUtils.isEmpty(estimatedVoidTime) || "NULL".equals(estimatedVoidTime.toUpperCase())){
				message.append("该文档细类的预计失效日期为必填！\n");
			}else if (StringUtils.isNotEmpty(replaceDocNumber)){
				if (!replaceDocNumber.startsWith("ENW")) {
					message.append("被替换ENW编码：" + replaceDocNumber + "必须是ENW开头！\n");
				}
				WTDocument replacedoc = DocUtil.getLatestWTDocument(replaceDocNumber);
				if (replacedoc == null){
					
					message.append("被替换ENW编码：" + replaceDocNumber + "的文档不存在！\n");
					
				} else if(!State.toState(PartState.RELEASED).equals(replacedoc.getLifeCycleState())) {
					
					message.append("被替换ENW编码：" + replaceDocNumber + "的文档不是已发布状态！\n");
				}
			}
		}
		return message;
	}
	
	public static void createDocReferenceLink(WTDocument pdoc, WTDocument cdoc) throws Exception {
		WTDocumentDependencyLink docreferencelink = getDocReferenceLink(pdoc, cdoc);
		if (docreferencelink == null) {
			WTDocumentDependencyLink docreferencelink1 = WTDocumentDependencyLink.newWTDocumentDependencyLink(pdoc,
					cdoc);
			PersistenceServerHelper.manager.insert(docreferencelink1);
			docreferencelink1 = (WTDocumentDependencyLink) PersistenceHelper.manager.refresh(docreferencelink1);
		}
		
	}
	
	public static WTDocumentDependencyLink getDocReferenceLink(WTDocument pdoc, WTDocument cdoc) throws WTException {
		QueryResult queryresult = PersistenceHelper.manager.find(WTDocumentDependencyLink.class, pdoc,
				WTDocumentDependencyLink.HAS_DEPENDENT_ROLE, cdoc);
		if (queryresult == null || queryresult.size() == 0)
			return null;
		else {
			WTDocumentDependencyLink dependencylink = (WTDocumentDependencyLink) queryresult.nextElement();
			return dependencylink;
		}
	}
	
	/**
     * 修改文档 主内容
     * @param doc		 文档
     * @param fis		 InputStream
     * @param fileName   内容名称
     * @param fileSize   内容大小
     * @return
	 * @throws Exception 
     * @modified: ☆xschen(2017年7月1日): <br>
     */
    public static WTDocument replaceDocPrimaryContent(WTDocument doc, InputStream fis, String fileName, long fileSize)
            throws Exception {
        doc = (WTDocument) getWorkableByPersistable(doc);
        ContentHolder contentHolder = ContentHelper.service.getContents(doc);
        ContentItem contentitem = ContentHelper.getPrimary((FormatContentHolder) contentHolder);
        ContentServerHelper.service.deleteContent(contentHolder, contentitem);

        ApplicationData app = ApplicationData.newApplicationData(doc);
        app.setRole(ContentRoleType.PRIMARY);
        app.setFileName(new String("已失效_"+fileName));
        app.setFileSize(fileSize);

        ContentServerHelper.service.updateContent(doc, app, fis);
        ContentServerHelper.service.updateHolderFormat(doc);
        doc = (WTDocument) WorkInProgressHelper.service.checkin(doc, null);
        //设置实际失效日期
      	setActualvoidtime(doc);
        //设置被替换ENW文档 生命周期状态为“已失效”
      	LifeCycleHelper.service.setLifeCycleState(doc, State.toState(DocState.INVALID),true);
      		
        return doc;
    }
    
   
    /**
     * 修改文档 主内容及创建参考文档
     * @param doc		 文档
     * @param fis		 InputStream
     * @param fileName   内容名称
     * @param fileSize   内容大小
     * @return
	 * @throws Exception 
     * @modified: ☆xschen(2017年7月1日): <br>
     */
    public static WTDocument replaceDocPrimaryContent(WTDocument pdoc,WTDocument doc, InputStream fis, String fileName, long fileSize)
            throws Exception {
        doc = (WTDocument) getWorkableByPersistable(doc);
        ContentHolder contentHolder = ContentHelper.service.getContents(doc);
        ContentItem contentitem = ContentHelper.getPrimary((FormatContentHolder) contentHolder);
        ContentServerHelper.service.deleteContent(contentHolder, contentitem);

        ApplicationData app = ApplicationData.newApplicationData(doc);
        app.setRole(ContentRoleType.PRIMARY);
        app.setFileName(new String("已失效_"+fileName));
        app.setFileSize(fileSize);
        ContentServerHelper.service.updateContent(doc, app, fis);
        ContentServerHelper.service.updateHolderFormat(doc);
        createDocReferenceLink(pdoc, doc);
        doc = (WTDocument) WorkInProgressHelper.service.checkin(doc, null);

        return doc;
    }
    
    /**
	 * @param workable
	 * @return 获取工作副本对象
	 * @create ltang
	 * @modified: ☆xschen(2017年7月1日): 
	 */
	public static Workable getWorkableByPersistable(Workable workable) {
		Workable wa = null;
		try {
			if (WorkInProgressHelper.isCheckedOut(workable)) {
				if (!WorkInProgressHelper.isWorkingCopy(workable)) {
					wa = WorkInProgressHelper.service.workingCopyOf(workable);
				}
			} else {
				wa = WorkInProgressHelper.service
						.checkout(workable, WorkInProgressHelper.service.getCheckoutFolder(), "").getWorkingCopy();
			}
		} catch (WTPropertyVetoException | WTException e) {
			e.printStackTrace();
		}
		return wa;
	}
	
	public static WTDocument getDocByNumberVersion(String number, String version) throws Exception{

		QuerySpec qs = new QuerySpec(WTDocument.class);
		String docAliases = qs.getFromClause().getAliasAt(0);
		TableColumn tcit = new TableColumn(docAliases, "ITERATIONIDA2ITERATIONINFO");
		TableColumn tcv = new TableColumn(docAliases, "VERSIONIDA2VERSIONINFO");
		SearchCondition sc = new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL,number);
		qs.appendWhere(sc);
		sc = new SearchCondition(tcv, SearchCondition.EQUAL,new ConstantExpression(version));
		qs.appendAnd();
		qs.appendWhere(sc);
//		sc = new SearchCondition(tcit, SearchCondition.EQUAL, new ConstantExpression(version.substring(version.indexOf(".")+1)));
//		qs.appendAnd();
//		qs.appendWhere(sc);
//		System.out.println(qs);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		if (qr != null && qr.hasMoreElements())
			qr = (new LatestConfigSpec()).process(qr);
		if(qr.hasMoreElements()){
			WTDocument doc = (WTDocument) qr.nextElement();
			return doc;
		}
	
	return null;
	
}

	/**
	 * 通过文档编码获取最新文档
	 * @param number
	 * @return
	 * @throws WTException
	 */
	public static WTDocument getWTDocumentByNumber(String number) throws WTException {
		WTDocument wt = null;
		QuerySpec qs = new QuerySpec(WTDocument.class);
		SearchCondition sc = new SearchCondition(WTDocument.class, WTDocument.NUMBER,
				"=", number);
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
	
	
	
	/**
	 * 获得当前的日期字符串
	 * 
	 * @return
	 */
	public static String getCurrentDate() {
		TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
		DATE_FORMATE.setTimeZone(tz);
		return DATE_FORMATE.format(new Date());
	}

	public static void docInvalidOpe (Object obj){
    	
		if (obj instanceof WTDocument) {
			
			WTDocument doc = (WTDocument) obj;
			// 更新实际失效日期字段为当前日期
			setActualvoidtime(doc);
		}
    	
    }

	/**
	 * 验证ENW文档是否存在，且处于“已发布”状态
	 * 
	 * @param doc
	 * @throws WTException 
	 */
	public static StringBuffer checkENWDoc(WTDocument doc) throws WTException {
		
		StringBuffer message = new StringBuffer();
		
		String replaceDocNumber = (String)GenericUtil.getObjectAttributeValue(doc, CatlConstant.CATL_DOC_REPLACEDOC);
		if (StringUtils.isNotEmpty(replaceDocNumber)){
			WTDocument replacedoc = DocUtil.getLatestWTDocument(replaceDocNumber);
			if (replacedoc == null){
				
				message.append("被替换ENW编码：" + replaceDocNumber + "的文档不存在！\n");
				
			} else if(!State.toState(PartState.RELEASED).equals(replacedoc.getLifeCycleState())) {
				
				message.append("被替换ENW编码：" + replaceDocNumber + "的文档不是已发布状态！\n");
			}
			
		}
		
		return message;
	}
	/**
	 * 设置被替换ENW文档 生命周期状态为“已失效”
	 * 设置实际失效日期
	 * @param doc
	 * @throws WTException
	 */
	public static void docAfterWfOpe (WTDocument doc) throws WTException {
		
		String replaceDocNumber = (String)GenericUtil.getObjectAttributeValue(doc, CatlConstant.CATL_DOC_REPLACEDOC);
		if (StringUtils.isNotEmpty(replaceDocNumber)){
			
			WTDocument replacedoc = DocUtil.getLatestWTDocument(replaceDocNumber);
			if (replacedoc != null){
				
				//设置被替换ENW文档 生命周期状态为“已失效”
				LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)replacedoc, State.toState(DocState.INVALID));
				
				replacedoc = (WTDocument)PersistenceServerHelper.manager.restore(replacedoc);
				
				//设置实际失效日期
				setActualvoidtime(replacedoc);
			} else {
				System.out.println("被替换ENW编码：" + replaceDocNumber + "的文档不存在！\n");
			}
			
		}
	}
	
	/**
	 * 设置All文档 生命周期状态为“已失效”
	 * 设置实际失效日期
	 * @param doc
	 * @throws WTException
	 */
	public static void docAfterWfOpeAll (WTDocument doc) throws WTException {
				//设置被替换ENW文档 生命周期状态为“已失效”
				LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)doc, State.toState(DocState.INVALID));
				
				doc = (WTDocument)PersistenceServerHelper.manager.restore(doc);
				
				//设置实际失效日期
				setActualvoidtime(doc);
	}
	
	/**
	 * 设置实际生效日期
	 * @param doc
	 * @throws WTException
	 */
	public static void docAfterWfOpeSJ (WTDocument doc) throws WTException {
				//设置实际生效日期
		try {
			IBAUtility iba = new IBAUtility(doc);
			iba.setIBAValue(CatlConstant.CATL_DOC_ActualTakeEffectTime, getCurrentDate());
			iba.updateAttributeContainer(doc);
            iba.updateIBAHolder(doc);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 获取系统路径格式"ext/comba/doc/Text.xml"
	 * 
	 * @param url	路径
	 * @return
	 * @throws Exception
	 */
	public static String getUrl(String url) throws Exception{
		String path = "";
		String[]  strs=url.split("/");
		WTProperties wtproperties;
		wtproperties = WTProperties.getLocalProperties();
		path = wtproperties.getProperty("wt.home", "UTF-8") + File.separator + "codebase";
		for(int i=0,len=strs.length;i<len;i++){
			path = path + File.separator+ strs[i].toString();
					
		}		 	
		return path;
	}	    
}
