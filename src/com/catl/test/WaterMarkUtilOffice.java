package com.catl.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.avalon.framework.Enum;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFShape;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFTextbox;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTextBox;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;


public final class WaterMarkUtilOffice {   
	public static SimpleDateFormat DATE_FORMATE = new SimpleDateFormat("yyyy-MM-dd");
	public static final org.openxmlformats.schemas.wordprocessingml.x2006.main.STHdrFtr.Enum DEFAULT =null;
	/**  
     * 打印文字水印图片  
     *   
     * @param pressText  
     *            --文字  
     * @param targetImg --  
     *            目标图片  
     * @param fontName --  
     *            字体名  
     * @param fontStyle --  
     *            字体样式  
     * @param color --  
     *            字体颜色  
     * @param fontSize --  
     *            字体大小  
     * @throws IOException 
     */  
    
    public static void word(String pressText,String srcFile,String targetFile) throws IOException {   
        try
        {
        	//新建一个文档  
            XWPFDocument xDoc = new XWPFDocument(new FileInputStream(srcFile));
            XWPFHeaderFooterPolicy xFooter = new XWPFHeaderFooterPolicy(xDoc);
           
//            //创建一个段落  
//            XWPFParagraph para = xDoc.createParagraph();
//            
//          //一个XWPFRun代表具有相同属性的一个区域。    
//            XWPFRun run = para.createRun();    
//            //run.setBold(true); //加粗    
//            //run.setText("加粗的内容");    
//            run = para.createRun();    
//            run.setColor("FF0000"); 
//            run.setText("");

            xFooter.createWatermark("文档已失效");
            xFooter.getDefaultFooter();
            
            
            xFooter.createFooter(DEFAULT);
            OutputStream os = new FileOutputStream(targetFile);  
            xDoc.write(os);  
            System.out.println("Done");
            
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }   
    
    
    
    
    
    public static void excel2003(String pressText,String srcFile,String targetFile) throws IOException, EncryptedDocumentException, InvalidFormatException { 
        File file = new File(srcFile);
        InputStream input = new FileInputStream(file);
        
        HSSFWorkbook wb = (HSSFWorkbook) WorkbookFactory.create(input);
        HSSFSheet sheet = null;
        
        int sheetNumbers = wb.getNumberOfSheets();
        
        // sheet
        for (int i = 0; i < sheetNumbers; i++) {
            sheet = wb.getSheetAt(i);
            //sheet.createDrawingPatriarch();
            
            HSSFPatriarch dp = sheet.createDrawingPatriarch();
            HSSFClientAnchor anchor = new HSSFClientAnchor(0,255,255,0,(short)0,1,(short)10,10);
            
            //HSSFComment comment = dp.createComment(anchor);
	   	    HSSFTextbox txtbox = dp.createTextbox(anchor);
	   	   	
	   	    HSSFRichTextString rtxt = new HSSFRichTextString("文档已失效");
	   	   	HSSFFont draftFont = (HSSFFont) wb.createFont();
	   	   	//水印颜色
	   	   	draftFont.setColor((short) 55);
	   	   	//draftFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	   	   	//字体大小
	   	   	draftFont.setFontHeightInPoints((short) 60);
	   	   	draftFont.setFontName("Verdana");
	   	   	rtxt.applyFont(draftFont);
	   	   	txtbox.setString(rtxt);
	   	   	//倾斜度
	   	   	txtbox.setRotationDegree((short)315);
	   	   	txtbox.setLineWidth(600);
	   	   	txtbox.setLineStyle(HSSFShape.LINESTYLE_NONE);
	   	   	txtbox.setNoFill(true);
        }
	    OutputStream os = new FileOutputStream(targetFile);  
	    wb.write(os);  
    }
    
    public static void excel2007(String pressText,String srcFile,String targetFile) throws IOException, EncryptedDocumentException, InvalidFormatException { 
        File file = new File(srcFile);
        InputStream input = new FileInputStream(file);
        XSSFWorkbook wb = (XSSFWorkbook) WorkbookFactory.create(input);
        XSSFSheet sheet = null;
        int sheetNumbers = wb.getNumberOfSheets();
        for (int i = 0; i < sheetNumbers; i++) {
            sheet = wb.getSheetAt(i);
            XSSFDrawing dp =sheet.createDrawingPatriarch();
            XSSFClientAnchor anchor = new XSSFClientAnchor(0,550,550,0,(short)0,10,(short)10,10);
	   	    XSSFTextBox txtbox = dp.createTextbox(anchor);
	   	   	XSSFRichTextString rtxt = new XSSFRichTextString("文档已失效");
	   	   	XSSFFont draftFont = (XSSFFont) wb.createFont();
	   	   	draftFont.setColor(HSSFColor.GREY_25_PERCENT.index);;
	   	   	draftFont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
	   	   	draftFont.setFontHeightInPoints((short) 60);
	   	   	draftFont.setFontName("Verdana");
	   	   	rtxt.applyFont(draftFont);
	   	   	txtbox.setText(rtxt);
	   		//倾斜度
	   	   	txtbox.setLineWidth(600);
	   	   	txtbox.setNoFill(false);
        }
        OutputStream os = new FileOutputStream(targetFile);  
	    wb.write(os);  
    }
    
    public static void ppt(String pressText,String srcFile,String targetFile) throws Exception {
    	
    }
    public static void main(String[] args) throws Exception{   
    	
    	word("闵行区档案馆","e:\\test\\2.docx","e:\\test\\模板模板.docx");
    	//excel2007("闵行区档案馆","e:\\test\\test.xlsx","e:\\test\\qweewq.xlsx");
    	//excel2003("闵行区档案馆","e:\\test\\test.xls","e:\\test\\3.xls");
    	//ppt("闵行区档案馆","e:\\test\\12.ppt","e:\\test\\12ppt.ppt");
    }   
}  