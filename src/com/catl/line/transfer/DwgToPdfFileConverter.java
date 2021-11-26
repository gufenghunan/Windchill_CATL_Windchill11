package com.catl.line.transfer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PdfMerger;
import com.opendesign.core.ExSystemServices;
import com.opendesign.core.FileAccessMode;
import com.opendesign.core.FileCreationDisposition;
import com.opendesign.core.FileShareMode;
import com.opendesign.core.Globals;
import com.opendesign.core.MemoryManager;
import com.opendesign.core.MemoryTransaction;
import com.opendesign.core.OdGePoint3d;
import com.opendesign.core.OdGsPageParams;
import com.opendesign.core.OdGsPageParamsArray;
import com.opendesign.core.OdPdfExport;
import com.opendesign.core.OdRxModule;
import com.opendesign.core.OdStreamBuf;
import com.opendesign.core.OdStringArray;
import com.opendesign.core.PDFExportParams;
import com.opendesign.core.PdfExportModule;
import com.opendesign.td.ExHostAppServices;
import com.opendesign.td.OdDbAttribute;
import com.opendesign.td.OdDbBlockReference;
import com.opendesign.td.OdDbBlockTable;
import com.opendesign.td.OdDbBlockTableRecord;
import com.opendesign.td.OdDbDatabase;
import com.opendesign.td.OdDbMText;
import com.opendesign.td.OdDbObject;
import com.opendesign.td.OdDbObjectIterator;
import com.opendesign.td.OdDbSymbolTableIterator;
import com.opendesign.td.OdDbTable;
import com.opendesign.td.OpenMode;
import com.opendesign.td.TD_Db;

public class DwgToPdfFileConverter{
	  static {
	        System.loadLibrary("TeighaJavaCore");
	        System.loadLibrary("TeighaJavaDwg");
	    }
    public static void main(String[] args) throws Exception {
    	File file1=new File("D://线束AUTOCAD图框模版0801.dwg");
    	File file2=new File("D://3.PDF");
    	DwgToPdfFileConverter coverter=new DwgToPdfFileConverter();
    	coverter.convertFile(file1,file2);
	}
    
    /**
     * dwg转pdf文件
     * @return 
     * @throws Exception 
     */
	public synchronized File convertFile(File from, File to) throws Exception {
		MemoryManager memory=MemoryManager.GetMemoryManager();
	    MemoryTransaction transction = memory.StartTransaction();
    	ExSystemServices systemServices = new ExSystemServices();
    	ExHostAppServices HostApp = new ExHostAppServices();
	    HostApp.disableOutput(true);//关闭打印输出
	    TD_Db.odInitialize(systemServices);
	    OdRxModule pdfModule = Globals.odrxDynamicLinker().loadApp("TD_PdfExport");
        OdDbDatabase db = HostApp.readFile(from.getAbsolutePath());
        adjustAlignment(db);//调整图纸中的文字，避免api操作文字时带来的位置、精度等影响
        PDFExportParams exportParams = new PDFExportParams();//导出pdf参数的设置
        OdStreamBuf pdf_file =null;
        try{
        exportParams.setVersion(PDFExportParams.PDFExportVersions.kPDFv1_1);
        int kEmbededOptimizedTTF=PDFExportParams.PDFExportFlags.kEmbededOptimizedTTF;
        int kSHXTextAsGeometry=PDFExportParams.PDFExportFlags.kSHXTextAsGeometry;
        int kFlateCompression=PDFExportParams.PDFExportFlags.kFlateCompression;
        int kZoomToExtentsMode=PDFExportParams.PDFExportFlags.kZoomToExtentsMode;
        int kSimpleGeomOptimization=PDFExportParams.PDFExportFlags.kSimpleGeomOptimization;
        exportParams.setExportFlags(kSimpleGeomOptimization|kEmbededOptimizedTTF|kSHXTextAsGeometry|kFlateCompression|kZoomToExtentsMode);
        exportParams.setDatabase(db);
        exportParams.setHatchDPI(200);//下箭头等标识的分辨率 默认72
        long[] CurPalette = Globals.odcmAcadLightPalette();//调色板颜色
        CurPalette[255] = 0x00000000;//0x00FF7F00
        for (int i = 0; i < CurPalette.length; i++) {
        	CurPalette[i]=0x00000000;
		}
        exportParams.setPalette(CurPalette); //设置调色板颜色
       pdf_file = systemServices.createFile(to.getAbsolutePath(), FileAccessMode.kFileWrite, FileShareMode.kShareDenyNo, FileCreationDisposition.kCreateAlways);
        exportParams.setOutputStream(pdf_file);
        OdStringArray layArr = exportParams.layouts();
        layArr.add(db.findActiveLayout(true));//打印激活的布局
        exportParams.setLayouts(layArr); 
//
        OdGsPageParamsArray ppArr = exportParams.pageParams();
        long len = layArr.size();
        if (1 > layArr.size()) len = 1;
        ppArr.resize(len);
        for (int i = 0; i < ppArr.size(); i++) {
        	OdGsPageParams params=ppArr.get(i);
        	params.set(297, 210,5, 5, 5, 5);//转出pdf大小A4纸张和边距5
		}
        exportParams.setPageParams(ppArr);
        PdfExportModule module = new PdfExportModule(OdRxModule.getCPtr(pdfModule), false); //= PdfExportModule.cast();
        OdPdfExport exporter = module.create();
        long errCode = exporter.exportPdfStr(exportParams, pdf_file);
        if (errCode != 0) {
            throw new FileConvertException(exporter.exportPdfErrorCode(errCode));
        }
        }catch(Exception e){
        	throw e;
        }finally{
        	if(pdf_file!=null){
        	  pdf_file.delete();
        	}
              exportParams.delete();
              db.delete();
              HostApp.delete();
              pdfModule.delete();
              systemServices.delete();
              memory.StopTransaction(transction);
              
        }
        return new File(to.getAbsolutePath());
    }
    
    /**
     * @param oddb
     * @throws Exception
     */
		public static void adjustAlignment(OdDbDatabase oddb) throws Exception{
			OdDbBlockTable blockTable = OdDbBlockTable.cast(oddb.getBlockTableId().safeOpenObject());
			OdDbSymbolTableIterator blockIter = OdDbSymbolTableIterator.cast(blockTable.newIterator());
			for (blockIter.start(); !blockIter.done(); blockIter.step()) {
				OdDbBlockTableRecord block = OdDbBlockTableRecord.cast(blockIter.getRecordId().safeOpenObject());
				OdDbObjectIterator entityIter = OdDbObjectIterator.cast(block.newIterator());
				for (; !entityIter.done(); entityIter.step()) {
					OdDbObject obj = entityIter.objectId().openObject();
				    if (obj.isKindOf(OdDbBlockReference.desc())) {
						OdDbBlockReference blkRef = OdDbBlockReference.cast(obj);
						OdDbObjectIterator iter = blkRef.attributeIterator();
						for (; !iter.done(); iter.step()) {
							OdDbAttribute attr = OdDbAttribute
									.cast(iter.entity().objectId().openObject(OpenMode.kForWrite));
							if (OdDbAttribute.getCPtr(attr) != 0) {
								attr.adjustAlignment();
							  }
						}
					}
				}
			}
		}
		
	/**
	 * 字体文件匹配(linux系统下使用)
	 * @return
	 */
	private static Map<String,String> getFont_file(){
		Map<String,String> map=new HashMap<String,String>();
		String [] dwg_font_filenames=ConstantDwg.dwg_font_filename;
	    for (int i = 0; i < dwg_font_filenames.length; i++) {
	    	String dwg_font_filename=dwg_font_filenames[i];
	    	String[] font_file=dwg_font_filename.split("\\|");
	    	map.put(font_file[0], font_file[1]);
		}
		return map;
	}
	/**
	 * 合并pdf
	 * @param pdfs
	 * @param dest
	 * @throws IOException
	 */
	public static void mergePdf(List<String> pdfs, String dest) throws IOException {
		PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
		PdfMerger merger = new PdfMerger(pdf);
		List<PdfDocument> list = new ArrayList<PdfDocument>();
		for (int i = 0; i < pdfs.size(); i++) {
			PdfDocument firstSourcePdf = new PdfDocument(new PdfReader(pdfs.get(i)));
			merger.merge(firstSourcePdf, 1, firstSourcePdf.getNumberOfPages());
			list.add(firstSourcePdf);
			firstSourcePdf.close();
		}
		pdf.close();
	}
    public String getContentType() {
        return "application/pdf";
    }
    public String getFileExtension() {
        return "pdf";
    }

}

