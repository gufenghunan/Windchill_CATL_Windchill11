package com.catl.cad;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import com.catl.pdfsignet.PDFSignetUtil;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

import wt.epm.EPMDocument;
import wt.epm.structure.EPMStructureHelper;
import wt.fc.QueryResult;
import wt.util.WTException;
import wt.util.WTMessage;
import wt.vc.config.LatestConfigSpec;

public class BatchDownloadPDFHelper {
	
	public static void downloadPDF(EPMDocument root, HttpServletResponse response) throws Exception{
		
		Map<String,Set<InputStream>> all = new HashMap<String,Set<InputStream>>();
		StringBuilder errorMsg = new StringBuilder();
		
		Set<InputStream> set = BatchDownloadPDFUtil.getRelesaedPDF(root);
		if(!set.isEmpty()){
			all.put(BatchDownloadPDFUtil.getSubNumber(root.getNumber()), set);
		}
		else{
			errorMsg.append(WTMessage.formatLocalizedMessage("2D图纸[{0}]没有加盖DC签章后的PDF文件 <br>", new Object[]{root.getNumber()}));
		}
		EPMDocument epm3D = BatchDownloadPDFUtil.getReferenceEPMByDrawing(root);
		if(epm3D != null){
			QueryResult qr = EPMStructureHelper.service.navigateUsesToIteration(epm3D, null, true, new LatestConfigSpec());
			while(qr.hasMoreElements()){
				Object obj = qr.nextElement();
				if(obj instanceof EPMDocument){
					BatchDownloadPDFUtil.loadUseMiddlewareEPM((EPMDocument)obj, all, errorMsg);
				}
			}
		}
		if(errorMsg.length() > 0){
			throw new WTException(errorMsg.toString());
		}
		else {
			ArrayList<InputStream> pdfs = new ArrayList<InputStream>();
			ArrayList<String> keys = new ArrayList<String>();
			keys.addAll(all.keySet());
			Collections.sort(keys);
			Iterator<String> iterators = keys.iterator();
			while(iterators.hasNext()){
				pdfs.addAll(all.get(iterators.next()));
			}
			if(!pdfs.isEmpty()){
				String fileName = BatchDownloadPDFUtil.getSubNumber(root.getNumber());
				fileName += "_batch.pdf";
				response.reset();
				response.setContentType("application/pdf"); 
			    response.setHeader("Content-Disposition" ,"attachment;filename="+fileName);
			    response.setHeader("Cache-Control", "no-cache");
				mergePdfFiles(pdfs, response.getOutputStream());
			}
			response.setStatus(HttpServletResponse.SC_OK);
			response.flushBuffer();
		}
	}
	
	public static void mergePdfFiles(ArrayList<InputStream> pdfs, OutputStream os) throws Exception {
		Document document = null;
		try {
			String pass = PDFSignetUtil.getEncryptionPW();
			document = new Document();
			PdfCopy copy = new PdfCopy(document, os); 
			copy.setEncryption(null, pass.getBytes(), PdfWriter.ALLOW_PRINTING|PdfWriter.ALLOW_SCREENREADERS, 
					PdfWriter.ENCRYPTION_AES_128);
			document.open();
			
			for (InputStream inputStream : pdfs) {
				PdfReader reader = new PdfReader(inputStream, pass.getBytes()); 
				int n = reader.getNumberOfPages();
				for (int j = 1; j <= n; j++) {  
			        document.newPage();
			        PdfImportedPage page = copy.getImportedPage(reader, j);  
			        copy.addPage(page);
			    }
			}
			os.flush();
			document.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (document != null && document.isOpen() && document.getPageNumber() > 0){
				document.close();
			}
			os.close();
		}
	}
}
