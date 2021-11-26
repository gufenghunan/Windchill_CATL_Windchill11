package com.catl.line.util.excel2pdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * <p>
 * ClassName: PDFPageEvent
 * </p>
 * <p>
 * Description: 事件 -> 页码控制
 * </p>
 * <p>
 * Author: Cary
 * </p>
 * <p>
 * Date: Oct 25, 2013
 * </p>
 */
public class PDFPageEvent extends PdfPageEventHelper {

	protected PdfTemplate template;
	public BaseFont baseFont;

	@Override
	public void onStartPage(PdfWriter writer, Document document) {
		try {
			this.template = writer.getDirectContent().createTemplate(100, 100);
			this.baseFont = new Font(Resource.BASE_FONT_CHINESE, 8, Font.NORMAL).getBaseFont();
		} catch (Exception e) {
			throw new ExceptionConverter(e);
		}
	}

	@Override
	public void onEndPage(PdfWriter writer, Document document) {
		// 在每页结束的时候把“第x页”信息写道模版指定位置
		PdfContentByte byteContent = writer.getDirectContent();
		String text = writer.getPageNumber() + "";
		float textWidth = this.baseFont.getWidthPoint(text, 8);
		float realWidth = document.right() - textWidth;

		//
		byteContent.beginText();
		byteContent.setFontAndSize(this.baseFont, 12);
		byteContent.setTextMatrix(realWidth, document.bottom());
		byteContent.showText(text);
		byteContent.endText();
		byteContent.addTemplate(this.template, realWidth, document.bottom());

		byteContent.saveState();
		byteContent.stroke();
		byteContent.restoreState();
		byteContent.closePath();// sanityCheck();
	}

	@Override
	public void onCloseDocument(PdfWriter writer, Document document) {
		// 关闭document的时候获取总页数，并把总页数按模版写道之前预留的位置
		this.template.beginText();
		this.template.setFontAndSize(this.baseFont, 8);
		this.template.showText("/" + Integer.toString(writer.getPageNumber() - 1));
		this.template.endText();
		this.template.closePath();// sanityCheck();
	}
}
