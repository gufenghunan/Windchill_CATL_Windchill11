package com.catl.line.transfer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.PDFReader;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.catl.battery.constant.ConstantBattery;
import com.catl.battery.entity.CellAttr;
import com.catl.battery.helper.CommonHelper;
import com.catl.battery.util.PoiUtil;
import com.catl.line.transfer.ConstantDwg;
import com.catl.line.transfer.DwgToPdfFileConverter;
import com.catl.line.transfer.FileConvertException;
import com.catl.line.transfer.FileUtil;
import com.catl.line.transfer.LineException;
import com.google.common.io.Files;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.opendesign.core.DwgVersion;
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
import com.opendesign.core.OdResult;
import com.opendesign.core.OdRxModule;
import com.opendesign.core.OdRxObject;
import com.opendesign.core.OdRxObjectPtrArray;
import com.opendesign.core.OdStreamBuf;
import com.opendesign.core.OdStringArray;
import com.opendesign.core.PDFExportParams;
import com.opendesign.core.PdfExportModule;
import com.opendesign.td.ExHostAppServices;
import com.opendesign.td.OdDb2dPolyline;
import com.opendesign.td.OdDbAlignedDimension;
import com.opendesign.td.OdDbArc;
import com.opendesign.td.OdDbAttribute;
import com.opendesign.td.OdDbBlockReference;
import com.opendesign.td.OdDbBlockTable;
import com.opendesign.td.OdDbBlockTableRecord;
import com.opendesign.td.OdDbCircle;
import com.opendesign.td.OdDbCurve;
import com.opendesign.td.OdDbDatabase;
import com.opendesign.td.OdDbDimension;
import com.opendesign.td.OdDbEllipse;
import com.opendesign.td.OdDbEntity;
import com.opendesign.td.OdDbHatch;
import com.opendesign.td.OdDbLeader;
import com.opendesign.td.OdDbLine;
import com.opendesign.td.OdDbMLeader;
import com.opendesign.td.OdDbMText;
import com.opendesign.td.OdDbObject;
import com.opendesign.td.OdDbObjectId;
import com.opendesign.td.OdDbObjectIterator;
import com.opendesign.td.OdDbOle2Frame;
import com.opendesign.td.OdDbPoint;
import com.opendesign.td.OdDbPolyline;
import com.opendesign.td.OdDbProxyEntity;
import com.opendesign.td.OdDbRotatedDimension;
import com.opendesign.td.OdDbSolid;
import com.opendesign.td.OdDbSpline;
import com.opendesign.td.OdDbSymbolTableIterator;
import com.opendesign.td.OdDbTable;
import com.opendesign.td.OdDbText;
import com.opendesign.td.OdDbTrace;
import com.opendesign.td.OdRectangle3d;
import com.opendesign.td.OpenMode;
import com.opendesign.td.SaveType;
import com.opendesign.td.TD_Db;

import wt.util.WTProperties;

public class DwgToPdfTest {
	
	private static final int String = 0;
	static {
		try {
			System.loadLibrary("TeighaJavaCore");
			System.loadLibrary("TeighaJavaDwg");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
  
	public static void main(String[] args) throws Exception {
		/*File from=new File("G:\\windchill\\dwgToPDF\\C0144085.dwg");
		File to=new File("G:/windchill/dwgToPDF/demo22.pdf");
		try {
			convertFile(from,to);
		} catch (Exception e) {
			e.printStackTrace();  
		}*/
		//com/catl/test/
		
		 
		/*PdfReader pdfReader = new PdfReader("G:\\windchill\\dwgToPDF\\C0000002（2016版本）.pdf");
		// Get the PdfStamper object
		PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream("G:\\windchill\\dwgToPDF\\C0000002（2016版本）2.pdf"));
		addWatermark(pdfStamper, "文档已失效");
		pdfStamper.close();
		*/
		WriterExcel();
	}
	
	public static List<Map<String, String>> getallattr() throws Exception{
		Map<String,String> map = null;
		List<Map<String, String>> list = new  ArrayList<Map<String,String>>();
		
	//获取文件夹下的所有dwg图纸
		 File files=new File("G:/windchill/dwgToPDF/");
		 File[] file = files.listFiles();
		 for (File file2 : file) {
			 String str=file2.getAbsolutePath();
			 
			 if(str.contains(".dwg")){
				 map = getMultiBoxPDF(str);
				 list.add(map);
			 }
		}
		
		return list;
		
		
	}
	
	/**
	 * 获取多图框转成后的PDF文件
	 * 
	 * @param srcFileName
	 * @throws Exception
	 */
	public synchronized static Map<String,String> getMultiBoxPDF(String srcFileName)
			throws Exception {
		nopagebox=100;
		Map<String,String> map=new HashMap<>();
		MemoryManager memory=MemoryManager.GetMemoryManager();
	    MemoryTransaction transction = memory.StartTransaction();
		ExSystemServices systemServices = new ExSystemServices();
		ExHostAppServices hostApp = new ExHostAppServices();
		hostApp.disableOutput(true);
		TD_Db.odInitialize(systemServices);
		OdDbDatabase oddb = null;
		if (!hostApp.findFile(srcFileName).isEmpty()) {
			try {
				oddb = hostApp.readFile(srcFileName);// 源文件不为空，读取源文件
			} catch (Exception e) {
				hostApp.delete();
				systemServices.delete();
				throw new LineException("不识别的图纸,请检查图纸是否损坏");
			}
		}
		try {
			OdDbBlockTable blockTable = OdDbBlockTable.cast(oddb
					.getBlockTableId().safeOpenObject());
			OdDbSymbolTableIterator blockIter = OdDbSymbolTableIterator
					.cast(blockTable.newIterator());
			for (blockIter.start(); !blockIter.done(); blockIter.step()) {
				OdDbBlockTableRecord block = OdDbBlockTableRecord
						.cast(blockIter.getRecordId().safeOpenObject());
				OdDbObjectIterator entityIter = OdDbObjectIterator.cast(block
						.newIterator());
				
				for (; !entityIter.done(); entityIter.step()) {
					OdDbObject obj = entityIter.objectId().openObject();
					if (obj.isKindOf(OdDbBlockReference.desc())) {
						OdDbBlockReference blkRef = OdDbBlockReference
								.cast(obj.objectId().openObject(OpenMode.kForWrite));
						// explodeAcDbTable(blkRef);
						OdDbObjectIterator iter = blkRef.attributeIterator();
						for (int i = 0; !iter.done(); i++, iter.step()) {
							OdDbAttribute attr = OdDbAttribute.cast(iter
									.entity().objectId()
									.openObject(OpenMode.kForWrite));
							//获取图框页码和页码坐标
							if (OdDbAttribute.getCPtr(attr) != 0) {
								String s=attr.tag();
								//List<String> tag = Arrays.asList(s);
								
							//	if (attr.tag().equals(ConstantDwg.dwg_page_num)) {
									try{
									int[] xynum = new int[3];
									xynum[0] = (int) attr.position().getX();
									xynum[1] = (int) attr.position().getY();
									String numval = attr.textString().toUpperCase().split(ConstantDwg.dwg_page_num_sign)[0].trim();
									//List<String> numval2 = Arrays.asList(numval);
									map.put(s,numval);
									//xynum[2] = Integer.valueOf(numval);
									//pagenum.add(xynum);
									}catch(Exception e){
										throw new LineException("获取图框页码失败");
									}
								}
							}
					}
				}
			}
			return map;
			
		} catch (Exception e) {
			memory.StopTransaction(transction);
			throw e;
		} finally {
			oddb.delete();
			hostApp.delete();
			systemServices.delete();
		}

	}
	
	/**
	 * 写入到Excel表中
	 * 
	 * @param tag	键
	 * @param names	值
	 * @return
	 * @throws Exception
	 */
	public static void WriterExcel() throws Exception{
		//String path = "G:/windchill/dwgToPDF/docExcel.xlsx";
        //path = getUrl(path);
		//File file = new File(path);
		//InputStream in = new FileInputStream(file);
		XSSFWorkbook fwb = new XSSFWorkbook();
		XSSFSheet sheet=fwb.createSheet();
		OutputStream os = new FileOutputStream("G:/windchill/dwgToPDF/dwg.xlsx");
		XSSFRow row = null;
		XSSFCell cell = null;
		int num = 2;
		List<Map<String, String>> list = getallattr();
		
		row = sheet.createRow(0);
		cell = row.createCell(0);
		cell.setCellValue("图纸名称");
		
		cell = row.createCell(1);
		cell.setCellValue("图号");
		
		cell = row.createCell(2);
		cell.setCellValue("规格");
		
		cell = row.createCell(3);
		cell.setCellValue("材料");
		
		cell = row.createCell(4);
		cell.setCellValue("比例");
		
		cell = row.createCell(5);
		cell.setCellValue("第张");
		
		cell = row.createCell(6);
		cell.setCellValue("共张");
		
		cell = row.createCell(7);
		cell.setCellValue("参考图号");
		
		cell = row.createCell(8);
		cell.setCellValue("版本");
		
		cell = row.createCell(9);
		cell.setCellValue("设计者");
		
		cell = row.createCell(10);
		cell.setCellValue("校对者");
		
		cell = row.createCell(11);
		cell.setCellValue("工艺审核");
		
		cell = row.createCell(12);
		cell.setCellValue("标准审核");
		
		row = sheet.createRow(1);
		cell = row.createCell(0);
		cell.setCellValue("drawing_name");
		
		cell = row.createCell(1);
		cell.setCellValue("drawingNo");
		
		cell = row.createCell(2);
		cell.setCellValue("specification");
		
		cell = row.createCell(3);
		cell.setCellValue("material");
		
		cell = row.createCell(4);
		cell.setCellValue("scale");
		
		cell = row.createCell(5);
		cell.setCellValue("Sheet");
		
		cell = row.createCell(6);
		cell.setCellValue("Sheets");
		
		cell = row.createCell(7);
		cell.setCellValue("ref_drawingNo");
		
		cell = row.createCell(8);
		cell.setCellValue("proi_revision");
		
		cell = row.createCell(9);
		cell.setCellValue("designer");
		
		cell = row.createCell(10);
		cell.setCellValue("checker");
		
		cell = row.createCell(11);
		cell.setCellValue("manuf_examination");
		
		cell = row.createCell(12);
		cell.setCellValue("std_examination");
		
		for (Map<String, String> map : list) {
			
			Set<String> set=map.keySet();
			for (String string : set) {
				System.out.println(string+":"+map.get(string));
			}
				row = sheet.createRow(num);
				
				cell = row.createCell(0);
				cell.setCellValue(map.get("GEN-TITLE-DES1{15.3}"));
				
				cell = row.createCell(1);
				cell.setCellValue(map.get("GEN-TITLE-NR{5.8}"));
				
				cell = row.createCell(2);
				cell.setCellValue(map.get("GEN-TITLE-SPECIFICATION{13.1}"));
			
				cell = row.createCell(3);
				cell.setCellValue(map.get("GEN-TITLE-NORM1{13.1}"));
				
				cell = row.createCell(4);
				cell.setCellValue(map.get("GEN-TITLE-SCA{5.3}"));
				
				cell = row.createCell(5);
				cell.setCellValue(map.get("GEN-TITLE-SHEET{2.7}"));
				
				cell = row.createCell(6);
				cell.setCellValue(map.get("GEN-TITLE-SHEETS{2.7}"));
				
				cell = row.createCell(7);
				cell.setCellValue(map.get("GEN-TITLE-NR{5.8}"));
				
				cell = row.createCell(8);
				cell.setCellValue(map.get("GEN-TITLE-REV{3.4}"));
				
				cell = row.createCell(9);
				cell.setCellValue(map.get("GEN-TITLE-NAME{7.8}"));
				
				cell = row.createCell(10);
				cell.setCellValue(map.get("GEN-TITLE-CHKM{7.8}"));
				
				cell = row.createCell(11);
				cell.setCellValue(map.get("GEN-TITLE-APPM1{7.8}"));
				
				cell = row.createCell(12);
				cell.setCellValue(map.get("GEN-TITLE-APPM2{7.8}"));
				
				num++;
		}
		fwb.write(os);
		if (os !=null) {
			os.close();
		}
		if (fwb !=null) {
			fwb.close();
		}
	}
	public synchronized static void save(String srcFileName) throws Exception {
		ExSystemServices systemServices = new ExSystemServices();
		ExHostAppServices hostApp = new ExHostAppServices();
		hostApp.disableOutput(true);
		TD_Db.odInitialize(systemServices);
		OdDbDatabase oddb = null;// 数据库
		if (!hostApp.findFile(srcFileName).isEmpty()) {
			try {
				oddb = hostApp.readFile(srcFileName);// 源文件不为空，读取源文件
			} catch (Exception e) {
				hostApp.delete();
				throw new LineException("不识别的图纸,请检查图纸是否损坏");
			}
		}
		oddb.writeFile(srcFileName, SaveType.kDwg, DwgVersion.vAC18, true);
		hostApp.delete();
		
	}
	/**
	 * 向dwg块中填入参数
	 * 
	 * @param srcFileName
	 * @param map
	 * @throws Exception
	 */
	public synchronized static void modifyDWG(String srcFileName,
			Map<String, String> map) throws Exception {
		for (Iterator it = (Iterator) map.keySet().iterator(); it.hasNext();) {
			String str = (String) it.next();
		}
		MemoryManager memory=MemoryManager.GetMemoryManager();
		MemoryTransaction transcation=memory.StartTransaction();
		ExSystemServices systemServices = new ExSystemServices();
		ExHostAppServices hostApp = new ExHostAppServices();
		hostApp.disableOutput(true);
		TD_Db.odInitialize(systemServices);
		OdDbDatabase oddb = null;// 数据库
		if (!hostApp.findFile(srcFileName).isEmpty()) {
			try {
				oddb = hostApp.readFile(srcFileName);// 源文件不为空，读取源文件
			} catch (Exception e) {
				hostApp.delete();
				systemServices.delete();
				memory.StopTransaction(transcation);
				throw new LineException("不识别的图纸,请检查图纸是否损坏");
			}
		}
		OdDbBlockTable blockTable = OdDbBlockTable.cast(oddb.getBlockTableId()
				.safeOpenObject());
		// 块集合
		OdDbSymbolTableIterator blockIter = OdDbSymbolTableIterator
				.cast(blockTable.newIterator());
		for (blockIter.start(); !blockIter.done(); blockIter.step()) {
			OdDbBlockTableRecord block = OdDbBlockTableRecord.cast(blockIter
					.getRecordId().safeOpenObject());
			OdDbObjectIterator entityIter = OdDbObjectIterator.cast(block
					.newIterator());
			for (; !entityIter.done(); entityIter.step()) {
				OdDbObject obj = entityIter.objectId().openObject();
				if (obj.isKindOf(OdDbBlockReference.desc())) {
					OdDbBlockReference blkRef = OdDbBlockReference.cast(obj);
					OdDbObjectIterator iter = blkRef.attributeIterator();
					for (; !iter.done(); iter.step()) {
						OdDbAttribute attr = OdDbAttribute.cast(iter.entity()
								.objectId().openObject(OpenMode.kForWrite));
						if (OdDbAttribute.getCPtr(attr) != 0) {
							if (map.containsKey(attr.tag())) {
								// 将传入参数写入dwg并调整位置
								String value = map.get(attr.tag()).toString()
										.trim();
								if (value.equals("0")) {//值为0的块不填写 如0
									value = "";
								}
								attr.setTextString(value);
								attr.adjustAlignment();//调整字符加减后的字体位置变化
							}
						}

					}
				}
			}
		}
		oddb.writeFile(srcFileName, SaveType.kDwg, DwgVersion.vAC18, true);
		oddb.delete();
		hostApp.delete();
		memory.StopTransaction(transcation);
	}

	/**
	 * 是否为模型空间(作图的布局区域)
	 * 
	 * @param text
	 * @return
	 */
	private static boolean isModelSpace(OdDbEntity text) {
		OdDbBlockTableRecord btr = OdDbBlockTableRecord.cast(text.blockId()
				.safeOpenObject(OpenMode.kForWrite));
		if (btr != null && btr.getName().toUpperCase().equals(ConstantDwg.dwg_model_space.toUpperCase())) {
			return true;
		}
		return false;
	}

	/**
	 * 拷贝文件
	 * 
	 * @param srcFile
	 * @param destFile
	 * @return
	 */
	private static boolean copyFile(File srcFile, File destFile) {
		int byteread = 0; // 读取的字节数
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(srcFile);
			out = new FileOutputStream(destFile);
			byte[] buffer = new byte[1024];
			while ((byteread = in.read(buffer)) != -1) {
				out.write(buffer, 0, byteread);
			}
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (out != null)
					out.close();
				if (in != null)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 根据各图框区域信息 生成PDF
	 * 
	 * @param f1
	 * @param db
	 * @param xyarea
	 * @param pagenum
	 * @return
	 * @throws Exception
	 */
	static String retirePDF(File f1, OdDbDatabase db, List<double[]> xyarea,
			List<int[]> pagenum) throws Exception {
		System.out.println(xyarea.size());
		List<String> pdfs = new ArrayList<String>();
		MemoryManager memory=MemoryManager.GetMemoryManager();
		memory.StartTransaction();
		DwgToPdfFileConverter coverter = new DwgToPdfFileConverter();
		String f1name = f1.getName().replace(".dwg", "");
		String dwg_temp_path = ConstantDwg.dwg_localpath + File.separator
				+ f1name + File.separator;
		FileUtil.createDir(dwg_temp_path);
		for (int i = 0; i < xyarea.size(); i++) {
			double[] cr = xyarea.get(i);
			double[] currentrange = getRange(cr[0], cr[1], cr[2]);//根据左上角坐标和图框面积 获取四个点的坐标
			int num = getPageNum(currentrange, pagenum);
			File f2 = new File(dwg_temp_path + f1name + num + ".dwg");
			copyFile(f1, f2);//拷贝出一个新的文件
			//Explicit delete() calls need attention and are a problem in a complicated application.
			//And there are no GC calls like the ones in C#,
			//thus we have no means to ensure that all temporary created objects are closed when delete() is called. 
			ExSystemServices systemServices = new ExSystemServices();
			ExHostAppServices hostApp = new ExHostAppServices();
			try {
				hostApp.disableOutput(true);
				TD_Db.odInitialize(systemServices);
				OdDbDatabase oddb = hostApp.readFile(dwg_temp_path + f1name+ num + ".dwg");
				deleteremainbox(oddb, currentrange);//删除需要转的图框 以外的内容
				oddb.writeFile(dwg_temp_path + f1name + num + ".dwg",
						SaveType.kDwg, DwgVersion.vAC18, true);
				oddb.delete();
				File file1 = new File(dwg_temp_path + f1name + num + ".dwg");
				System.out.println(dwg_temp_path + f1name + num + ".pdf");
				File file2 = new File(dwg_temp_path + f1name + num + ".pdf");
				pdfs.add(dwg_temp_path + f1name + num + ".pdf");
				coverter.convertFile(file1, file2);
			} catch (Exception e) {
				hostApp.delete();
				systemServices.delete();
				memory.StopAll();
				throw e;
			} 
		}
		memory.StopAll();
		TD_Db.odUninitialize();
		java.util.Collections.sort(pdfs);//按照文件名(即页码)排序
		coverter.mergePdf(pdfs, dwg_temp_path + f1name + ".pdf");//合并所有的图框
		return dwg_temp_path + f1name + ".pdf";

	}

	/**
	 * 获取图框内的页码
	 * 
	 * @param range
	 * @param pagenum
	 * @return
	 */
	private static int nopagebox = 100;// 没有填写页码则给个页码在pdf最后几页显示

	private static int getPageNum(double[] range, List<int[]> pagenum) {
		for (int i = 0; i < pagenum.size(); i++) {
			int[] xynum = pagenum.get(i);
			if (isinrange(xynum[0], xynum[1], range)) {
				return xynum[2];
			}
		}
		nopagebox++;
		return nopagebox;
		// throw new LineException(Constant.exception_cannotfoundpagenum);
	}

	/**
	 * 获取图框的范围
	 * 
	 * @param x
	 * @param y
	 * @param area
	 * @return
	 */
	static double[] getRange(double x, double y, double area) {
		double[] xy = getPaperSize(area);
		double boxx = xy[0];
		double boxy = xy[1];
		double offset = 2.0;// 误差量
		double[] xys = new double[4];
		xys[0] = x + boxx + offset;
		xys[1] = y + offset;
		xys[2] = x - offset;
		xys[3] = y - boxy - offset;
		return xys;
	}

	/**
	 * 区域是否在图框范围内
	 * 
	 * @param x
	 * @param y
	 * @param xys
	 * @return
	 */
	private static boolean isinrange(double x, double y, List<double[]> xys) {
		for (int i = 0; i < xys.size(); i++) {
			double[] xy = xys.get(i);
			double maxx = xy[0];
			double maxy = xy[1];
			double minx = xy[2];
			double miny = xy[3];
			if (x < maxx && x > minx && y < maxy && y > miny) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 坐标是否在图框范围内
	 * 
	 * @param x
	 * @param y
	 * @param xys
	 * @return
	 */
	private static boolean isinrange(double x, double y, double[] xy) {
		double maxx = xy[0];
		double maxy = xy[1];
		double minx = xy[2];
		double miny = xy[3];
		if (x < maxx && x > minx && y < maxy && y > miny) {
			return true;
		}
		return false;
	}

	/**
	 * 根据图框面积获取图框长宽
	 * 
	 * @param area
	 * @return
	 */
	static double[] getPaperSize(double area) {
		int iarea = (int) area;
		if (iarea< 62372&&iarea>62368) {
			return new double[] { 297.0, 210.0 };
		}
		if (iarea<124742&&iarea>124738) {
			return new double[] { 420.0, 297.0 };
		} else if (iarea<249482&&iarea>249478) {
			return new double[] { 594.0, 420.0 };
		} else if (iarea<498962&&iarea>498958) {
			return new double[] { 840.0, 594.0 };
		} else if (iarea<997922&&iarea>997918) {
			return new double[] { 1188, 840.0 };
		} else {
			System.out.println("不能识别的图框面积"+area);
			return null;
		}
	}

	/**
	 * 删除多余的图框 图框外
	 * 获取每个对象上的一个坐标 在图框外则删除
	 * @param db
	 * @param xys
	 */
	static void deleteremainbox(OdDbDatabase db, double[] xy) {
		double maxx = xy[0];
		double maxy = xy[1];
		double minx = xy[2];
		double miny = xy[3];
		OdDbObject pId = db.getBlockTableId().safeOpenObject();
		OdDbBlockTable blockTable = OdDbBlockTable.cast(pId);
		OdDbSymbolTableIterator blockIter = OdDbSymbolTableIterator
				.cast(blockTable.newIterator());
		for (blockIter.start(); !blockIter.done(); blockIter.step()) {
			OdDbBlockTableRecord block = OdDbBlockTableRecord.cast(blockIter
					.getRecordId().safeOpenObject());
			OdDbObjectIterator entityIter = OdDbObjectIterator.cast(block
					.newIterator());
			for (; !entityIter.done(); entityIter.step()) {
				OdDbObject obj = entityIter.objectId().openObject();
				if (obj.isKindOf(OdDbLine.desc())) { 
					OdDbLine line = OdDbLine.cast(obj);
					OdGePoint3d startPoint = new OdGePoint3d();
					line.getStartPoint(startPoint);
					double x = startPoint.getX();
					double y = startPoint.getY();
					if (!(x < maxx && x > minx && y < maxy && y > miny)
							&& isModelSpace(line)) {
						if (!line.isErased() && isModelSpace(line)) {
							line = OdDbLine.cast(obj.objectId().openObject(OpenMode.kForWrite));
							line.erase();
						}
					}
				} else if (obj.isKindOf(OdDbSolid.desc())) {
					OdDbSolid text = OdDbSolid.cast(obj);
					// System.out.println(line.isErased());
					OdGePoint3d startPoint = new OdGePoint3d();
					text.getPointAt(0, startPoint);
					double x = startPoint.getX();
					double y = startPoint.getY();
					if (!(x < maxx && x > minx && y < maxy && y > miny)
							&& isModelSpace(text)) {
						if (!text.isErased() && isModelSpace(text)) {
							text = OdDbSolid.cast(obj.objectId().openObject(
									OpenMode.kForWrite));
							text.erase();
						}
					}

				} else if (obj.isKindOf(OdDbPoint.desc())) {
					OdDbPoint text = OdDbPoint.cast(obj);
					OdGePoint3d startPoint = text.position();
					double x = startPoint.getX();
					double y = startPoint.getY();
					if (!(x < maxx && x > minx && y < maxy && y > miny)
							&& isModelSpace(text)) {
						if (!text.isErased() && isModelSpace(text)) {
							text = OdDbPoint.cast(obj.objectId().openObject(
									OpenMode.kForWrite));
							text.erase();
						}
					}
					// System.out.println(line.isErased());

				} else if (obj.isKindOf(OdDbPolyline.desc())) {
					OdDbPolyline poly = OdDbPolyline.cast(obj);
					// System.out.println(line.isErased());
					OdGePoint3d startPoint = new OdGePoint3d();
					poly.getStartPoint(startPoint);
					double x = startPoint.getX();
					double y = startPoint.getY();
					if (!(x < maxx && x > minx && y < maxy && y > miny)
							&& isModelSpace(poly)) {
						if (!poly.isErased() && isModelSpace(poly)) {
							poly = OdDbPolyline.cast(obj.objectId().openObject(
									OpenMode.kForWrite));
							poly.erase();
						}
					}
				} else if (obj.isKindOf(OdDbText.desc())) {
					OdDbText text = OdDbText.cast(obj);
					OdGePoint3d startPoint = text.position();
					double x = startPoint.getX();
					double y = startPoint.getY();
					if (!(x < maxx && x > minx && y < maxy && y > miny)
							&& isModelSpace(text)) {
						if (!text.isErased() && isModelSpace(text)) {
							text = OdDbText.cast(obj.objectId().openObject(
									OpenMode.kForWrite));
							text.erase();
						}
					}
					// System.out.println(line.isErased());
				} else if (obj.isKindOf(OdDbMText.desc())) {
					OdDbMText text = OdDbMText.cast(obj);
					OdGePoint3d startPoint = text.location();
					double x = startPoint.getX();
					double y = startPoint.getY();
					if (!(x < maxx && x > minx && y < maxy && y > miny)
							&& isModelSpace(text)) {
						if (!text.isErased() && isModelSpace(text)) {
							text = OdDbMText.cast(obj.objectId().openObject(
									OpenMode.kForWrite));
							text.erase();
						}
					}
				} else if (obj.isKindOf(OdDbCircle.desc())) {
					OdDbCircle text = OdDbCircle.cast(obj);
					OdGePoint3d startPoint = new OdGePoint3d();
					text.getStartPoint(startPoint);
					double x = startPoint.getX();
					double y = startPoint.getY();
					if (!(x < maxx && x > minx && y < maxy && y > miny)
							&& isModelSpace(text)) {
						if (!text.isErased() && isModelSpace(text)) {
							text = OdDbCircle.cast(obj.objectId().openObject(
									OpenMode.kForWrite));
							text.erase();
						}
					}
				} else if (obj.isKindOf(OdDbArc.desc())) {
					OdDbArc text = OdDbArc.cast(obj);
					OdGePoint3d startPoint = new OdGePoint3d();
					text.getStartPoint(startPoint);
					double x = startPoint.getX();
					double y = startPoint.getY();
					if (!(x < maxx && x > minx && y < maxy && y > miny)
							&& isModelSpace(text)) {
						if (!text.isErased() && isModelSpace(text)) {
							text = OdDbArc.cast(obj.objectId().openObject(
									OpenMode.kForWrite));
							text.erase();
						}
					}
				} else if (obj.isKindOf(OdDb2dPolyline.desc())) {
					OdDb2dPolyline text = OdDb2dPolyline.cast(obj);
					OdGePoint3d startPoint = new OdGePoint3d();
					text.getStartPoint(startPoint);
					double x = startPoint.getX();
					double y = startPoint.getY();
					if (!(x < maxx && x > minx && y < maxy && y > miny)
							&& isModelSpace(text)) {
						if (!text.isErased() && isModelSpace(text)) {
							text = OdDb2dPolyline.cast(obj.objectId()
									.openObject(OpenMode.kForWrite));
							text.erase();
						}
					}
				} else if (obj.isKindOf(OdDbEllipse.desc())) {
					OdDbEllipse text = OdDbEllipse.cast(obj);
					OdGePoint3d startPoint = new OdGePoint3d();
					text.getStartPoint(startPoint);
					double x = startPoint.getX();
					double y = startPoint.getY();
					if (!(x < maxx && x > minx && y < maxy && y > miny)
							&& isModelSpace(text)) {
						if (!text.isErased() && isModelSpace(text)) {
							text = OdDbEllipse.cast(obj.objectId().openObject(
									OpenMode.kForWrite));
							text.erase();
						}
					}
				} else if (obj.isKindOf(OdDbSpline.desc())) {
					OdDbSpline text = OdDbSpline.cast(obj);
					OdGePoint3d startPoint = new OdGePoint3d();
					text.getStartPoint(startPoint);
					double x = startPoint.getX();
					double y = startPoint.getY();
					if (!(x < maxx && x > minx && y < maxy && y > miny)
							&& isModelSpace(text)) {
						if (!text.isErased() && isModelSpace(text)) {
							text = OdDbSpline.cast(obj.objectId().openObject(
									OpenMode.kForWrite));
							text.erase();
						}
					}
				} else if (obj.isKindOf(OdDbLeader.desc())) {
					OdDbLeader text = OdDbLeader.cast(obj);
					OdGePoint3d startPoint = new OdGePoint3d();
					text.getStartPoint(startPoint);
					double x = startPoint.getX();
					double y = startPoint.getY();
					if (!(x < maxx && x > minx && y < maxy && y > miny)
							&& isModelSpace(text)) {
						if (!text.isErased() && isModelSpace(text)) {
							text = OdDbLeader.cast(obj.objectId().openObject(
									OpenMode.kForWrite));
							text.erase();
						}
					}
				} else if (obj.isKindOf(OdDbHatch.desc())) {
					OdDbHatch text = OdDbHatch.cast(obj);
					double[] hatchxy =getHatchTrueLength(text);
					 double x=hatchxy[0];
					 double y=hatchxy[1];
					if (!(x < maxx && x > minx && y < maxy && y > miny)
							&& isModelSpace(text)) {
						if (!text.isErased() && isModelSpace(text)) {
							text = OdDbHatch.cast(obj.objectId().openObject(
									OpenMode.kForWrite));
							text.erase();
						}
					}
				} else if (obj.isKindOf(OdDbBlockReference.desc())) {
					OdDbBlockReference blkRef = OdDbBlockReference.cast(obj);
					double[] blkrefxy = getTrueLength(blkRef);
					if (blkrefxy != null) {
						double x = blkrefxy[0];
						double y = blkrefxy[1];
						if (!(x < maxx && x > minx && y < maxy && y > miny)
								&& isModelSpace(blkRef)) {
							blkRef = OdDbBlockReference.cast(obj.objectId()
									.openObject(OpenMode.kForWrite));
							blkRef.erase();
						}
					}

				} else if (obj.isKindOf(OdDbTable.desc())) {
					OdDbTable text = OdDbTable.cast(obj);
					OdGePoint3d startPoint = text.position();
					double x = startPoint.getX();
					double y = startPoint.getY();
					if (!(x < maxx && x > minx && y < maxy && y > miny)
							&& isModelSpace(text)) {
						text = OdDbTable.cast(obj.objectId().openObject(
								OpenMode.kForWrite));
						text.erase();
					}
				} else if (obj.isKindOf(OdDbRotatedDimension.desc())) {
					OdDbRotatedDimension text = OdDbRotatedDimension.cast(obj);
					OdGePoint3d startPoint = text.dimLinePoint();
					double x = startPoint.getX();
					double y = startPoint.getY();
					if (!(x < maxx && x > minx && y < maxy && y > miny)
							&& isModelSpace(text)) {
						if (!text.isErased() && isModelSpace(text)) {
							text = OdDbRotatedDimension.cast(obj.objectId()
									.openObject(OpenMode.kForWrite));
							text.erase();
						}
					}
				} else if (obj.isKindOf(OdDbAlignedDimension.desc())) {
					OdDbAlignedDimension text = OdDbAlignedDimension.cast(obj);
					OdGePoint3d startPoint = text.dimLinePoint();
					double x = startPoint.getX();
					double y = startPoint.getY();
					if (!(x < maxx && x > minx && y < maxy && y > miny)
							&& isModelSpace(text)) {
						if (!text.isErased() && isModelSpace(text)) {
							text = OdDbAlignedDimension.cast(obj.objectId()
									.openObject(OpenMode.kForWrite));
							text.erase();
						}
					}
				} else if (obj.isKindOf(OdDbMLeader.desc())) {
					OdDbMLeader text = OdDbMLeader.cast(obj);
					OdGePoint3d startPoint = new OdGePoint3d();
					text.getTextLocation(startPoint);
					double x = startPoint.getX();
					double y = startPoint.getY();
					if (!(x < maxx && x > minx && y < maxy && y > miny)
							&& isModelSpace(text)) {
						if (!text.isErased() && isModelSpace(text)) {
							text = OdDbMLeader.cast(obj.objectId().openObject(
									OpenMode.kForWrite));
							text.erase();
						}
					}
				} else if (obj.isKindOf(OdDbTrace.desc())) {
					OdDbTrace text = OdDbTrace.cast(obj);
					OdGePoint3d startPoint = new OdGePoint3d();
					text.getPointAt(0, startPoint);
					double x = startPoint.getX();
					double y = startPoint.getY();
					if (!(x < maxx && x > minx && y < maxy && y > miny)
							&& isModelSpace(text)) {
						if (!text.isErased() && isModelSpace(text)) {
							text = OdDbTrace.cast(obj.objectId().openObject(
									OpenMode.kForWrite));
							text.erase();
						}
					}
				}else if(obj instanceof OdDbProxyEntity){//外来的代理对象 删除无效
					OdDbProxyEntity text = OdDbProxyEntity.cast(obj);
					double[] entitys=getTrueLength(text);
					if (text != null&&entitys!=null) {
						double x = entitys[0];
						double y = entitys[1];
						if (!(x < maxx && x > minx && y < maxy && y > miny)
								&& isModelSpace(text)) {
							if (!text.isErased() && isModelSpace(text)) {
								text = OdDbProxyEntity.cast(obj.objectId().openObject(
										OpenMode.kForWrite));
								if(!text.eraseAllowed()){
									System.out.println("无法操作对象。请确认是否保存为普通版CAD图纸");
								}
								text.erase();
							}
						}
					}else{
						throw new LineException("无法操作对象。请确认是否保存为普通版CAD图纸");
					}
				
				}else if (obj instanceof OdDbCurve) {
					OdDbCurve text = OdDbCurve.cast(obj);
					OdGePoint3d startPoint = new OdGePoint3d();
					text.getStartPoint(startPoint);
					double x = startPoint.getX();
					double y = startPoint.getY();
					if (!(x < maxx && x > minx && y < maxy && y > miny)
							&& isModelSpace(text)) {
						if (!text.isErased() && isModelSpace(text)) {
							text = OdDbPolyline.cast(obj.objectId().openObject(
									OpenMode.kForWrite));
							if (text != null) {
								text.erase();
							}

						}
					}
				}else if(obj instanceof OdDbOle2Frame){//贴图
					OdDbOle2Frame text=OdDbOle2Frame.cast(obj);
					OdRectangle3d point=new OdRectangle3d();
					text.position(point);
					OdGePoint3d tpoint=point.getLowLeft();
					double x = tpoint.getX();
					double y = tpoint.getY();
					if (!(x < maxx && x > minx && y < maxy && y > miny)
							&& isModelSpace(text)) {
						if (!text.isErased() && isModelSpace(text)) {
							text = OdDbOle2Frame.cast(obj.objectId().openObject(
									OpenMode.kForWrite));
							if (text != null) {
								text.erase();
							}
						}
					}
				}else if(obj instanceof OdDbDimension){//公差引线等
					OdDbDimension text=OdDbDimension.cast(obj);
					OdGePoint3d startPoint = text.textPosition();
					double x = startPoint.getX();
					double y = startPoint.getY();
					if (!(x < maxx && x > minx && y < maxy && y > miny)
							&& isModelSpace(text)) {
						if (!text.isErased() && isModelSpace(text)) {
							text = OdDbDimension.cast(obj.objectId().openObject(
									OpenMode.kForWrite));
							if (text != null) {
								text.erase();
							}

						}
					}
				}else {
					//除了OdDbViewport
					System.out.println(obj);
				}
			}
			block.delete();

		}

	}

	private static void explodeAcDbTable(OdDbBlockReference blkRef){//表格打散后删除块 避免样式变化
		if(blkRef.isA().name().equals("AcDbTable")){
			blkRef.explodeToOwnerSpace();
			blkRef.erase();
		}
	}

	/**
	 * 获取块的真实位置,根据打散的子类坐标
	 * 
	 * @param blkRef
	 * @return
	 */
	private static double[] getTrueLength(OdDbObject entityobj) {
		OdRxObjectPtrArray paramOdRxObjectPtrArray = new OdRxObjectPtrArray();
		if(entityobj.isKindOf(OdDbBlockReference.desc())){
			OdDbBlockReference blockref = (OdDbBlockReference)OdDbBlockReference.cast(entityobj.objectId().openObject(
					OpenMode.kForWrite));
			if (blockref == null) {
				return null;
			}
			blockref.explode(paramOdRxObjectPtrArray);
			blockref.delete();
		}else if(entityobj.isKindOf(OdDbProxyEntity.desc())){
			OdDbProxyEntity entity =OdDbProxyEntity.cast(entityobj.objectId().openObject(
					OpenMode.kForWrite));
			if (entity == null) {
				return null;
			}
			entity.explode(paramOdRxObjectPtrArray);
			entity.delete();
		}
		for (int i = 0; i < paramOdRxObjectPtrArray.size(); i++) {
			OdRxObject obj = paramOdRxObjectPtrArray.get(i);
			if (obj.isKindOf(OdDbLine.desc())) { // obj instanceof OdDbLine
				OdDbLine line = OdDbLine.cast(obj);
				OdGePoint3d startPoint = new OdGePoint3d();
				line.getStartPoint(startPoint);
				double x = startPoint.getX();
				double y = startPoint.getY();
				return new double[] { x, y };
			} else if (obj.isKindOf(OdDbSolid.desc())) {
				OdDbSolid text = OdDbSolid.cast(obj);
				// System.out.println(line.isErased());
				OdGePoint3d startPoint = new OdGePoint3d();
				text.getPointAt(0, startPoint);
				double x = startPoint.getX();
				double y = startPoint.getY();
				return new double[] { x, y };
			} else if (obj.isKindOf(OdDbPoint.desc())) {
				OdDbPoint text = OdDbPoint.cast(obj);
				OdGePoint3d startPoint = text.position();
				double x = startPoint.getX();
				double y = startPoint.getY();
				return new double[] { x, y };
			} else if (obj.isKindOf(OdDbPolyline.desc())) {
				OdDbPolyline poly = OdDbPolyline.cast(obj);
				// System.out.println(line.isErased());
				OdGePoint3d startPoint = new OdGePoint3d();
				poly.getStartPoint(startPoint);
				double x = startPoint.getX();
				double y = startPoint.getY();
				return new double[] { x, y };
			} else if (obj.isKindOf(OdDbText.desc())) {
				OdDbText text = OdDbText.cast(obj);
				OdGePoint3d startPoint = text.position();
				double x = startPoint.getX();
				double y = startPoint.getY();
				return new double[] { x, y };
				// System.out.println(line.isErased());
			} else if (obj.isKindOf(OdDbMText.desc())) {
				OdDbMText text = OdDbMText.cast(obj);
				OdGePoint3d startPoint = text.location();
				double x = startPoint.getX();
				double y = startPoint.getY();
				return new double[] { x, y };
			} else if (obj.isKindOf(OdDbCircle.desc())) {
				OdDbCircle text = OdDbCircle.cast(obj);
				OdGePoint3d startPoint = new OdGePoint3d();
				text.getStartPoint(startPoint);
				double x = startPoint.getX();
				double y = startPoint.getY();
				return new double[] { x, y };
			} else if (obj.isKindOf(OdDbArc.desc())) {
				OdDbArc text = OdDbArc.cast(obj);
				OdGePoint3d startPoint = new OdGePoint3d();
				text.getStartPoint(startPoint);
				double x = startPoint.getX();
				double y = startPoint.getY();
				return new double[] { x, y };
			} else if (obj.isKindOf(OdDb2dPolyline.desc())) {
				OdDb2dPolyline text = OdDb2dPolyline.cast(obj);
				OdGePoint3d startPoint = new OdGePoint3d();
				text.getStartPoint(startPoint);
				double x = startPoint.getX();
				double y = startPoint.getY();
				return new double[] { x, y };
			} else if (obj.isKindOf(OdDbEllipse.desc())) {
				OdDbEllipse text = OdDbEllipse.cast(obj);
				OdGePoint3d startPoint = new OdGePoint3d();
				text.getStartPoint(startPoint);
				double x = startPoint.getX();
				double y = startPoint.getY();
				return new double[] { x, y };
			} else if (obj.isKindOf(OdDbSpline.desc())) {
				OdDbSpline text = OdDbSpline.cast(obj);
				OdGePoint3d startPoint = new OdGePoint3d();
				text.getStartPoint(startPoint);
				double x = startPoint.getX();
				double y = startPoint.getY();
				return new double[] { x, y };
			} else if (obj.isKindOf(OdDbLeader.desc())) {
				OdDbLeader text = OdDbLeader.cast(obj);
				OdGePoint3d startPoint = new OdGePoint3d();
				text.getStartPoint(startPoint);
				double x = startPoint.getX();
				double y = startPoint.getY();
				return new double[] { x, y };
			} else if (obj.isKindOf(OdDbHatch.desc())) {
				OdDbHatch text = OdDbHatch.cast(obj);
				double x = 0;
				double y = 0;
				OdRxObjectPtrArray odrxobjectptrarray=new OdRxObjectPtrArray();
				text.explode(odrxobjectptrarray);
				for (int j = 0; j < odrxobjectptrarray.size(); j++) {
					OdDbObject cobj=(OdDbObject) odrxobjectptrarray.get(j);
					OdGePoint3d startPoint = new OdGePoint3d();
					if (cobj.isKindOf(OdDbSolid.desc())) {
						OdDbSolid soild = OdDbSolid.cast(cobj);
						soild.getPointAt(0, startPoint);
						x = startPoint.getX();
						y = startPoint.getY();
						break;
					}
				}
				odrxobjectptrarray.delete();
				text.delete();
				return new double[] { x, y };
			} else if (obj.isKindOf(OdDbTable.desc())) {
				OdDbTable text = OdDbTable.cast(obj);
				OdGePoint3d startPoint = text.position();
				double x = startPoint.getX();
				double y = startPoint.getY();
				return new double[] { x, y };
			} else if (obj.isKindOf(OdDbRotatedDimension.desc())) {
				OdDbRotatedDimension text = OdDbRotatedDimension.cast(obj);
				OdGePoint3d startPoint = text.dimLinePoint();
				double x = startPoint.getX();
				double y = startPoint.getY();
				return new double[] { x, y };
			} else if (obj.isKindOf(OdDbAlignedDimension.desc())) {
				OdDbAlignedDimension text = OdDbAlignedDimension.cast(obj);
				OdGePoint3d startPoint = text.dimLinePoint();
				double x = startPoint.getX();
				double y = startPoint.getY();
				return new double[] { x, y };
			} else if (obj.isKindOf(OdDbMLeader.desc())) {
				OdDbMLeader text = OdDbMLeader.cast(obj);
				OdGePoint3d startPoint = new OdGePoint3d();
				text.getTextLocation(startPoint);
				double x = startPoint.getX();
				double y = startPoint.getY();
				return new double[] { x, y };
			} else if (obj.isKindOf(OdDbTrace.desc())) {
				OdDbTrace text = OdDbTrace.cast(obj);
				OdGePoint3d startPoint = new OdGePoint3d();
				text.getPointAt(0, startPoint);
				double x = startPoint.getX();
				double y = startPoint.getY();
				return new double[] { x, y };
			} else if (obj.isKindOf(OdDbCurve.desc())) {
				OdDbCurve text = OdDbCurve.cast(obj);
				OdGePoint3d startPoint = new OdGePoint3d();
				text.getStartPoint(startPoint);
				double x = startPoint.getX();
				double y = startPoint.getY();
				return new double[] { x, y };
			}else if(obj instanceof OdDbOle2Frame){
				OdDbOle2Frame text=OdDbOle2Frame.cast(obj);
				OdRectangle3d point=new OdRectangle3d();
				text.position(point);
				OdGePoint3d tpoint=point.getLowLeft();
				double x = tpoint.getX();
				double y = tpoint.getY();
				return new double[] { x, y };
			} else if(obj instanceof OdDbDimension){
				OdDbDimension text=OdDbDimension.cast(obj);
				OdGePoint3d startPoint = text.textPosition();
				double x = startPoint.getX();
				double y = startPoint.getY();
				return new double[] { x, y };
			}else if (obj.isKindOf(OdDbBlockReference.desc())) {
				OdDbBlockReference text = OdDbBlockReference.cast(obj);
				double x = text.position().getX();
				double y = text.position().getY();
				return new double[] { x, y };
			}
		}
		//如果为块 找不到子对象位置 则仍然返回块的位置
		if(entityobj.isKindOf(OdDbBlockReference.desc())){
			OdDbBlockReference blockref = (OdDbBlockReference)OdDbBlockReference.cast(entityobj.objectId().openObject(
					OpenMode.kForWrite));
			if (blockref == null) {
				return null;
			}
			double x = blockref.position().getX();
			double y = blockref.position().getY();
			return new double[] { x, y };
		}else{
			return null;
		}
		
	}
	
	/**
	 * 获取块的真实位置,根据打散的子类坐标
	 * 
	 * @param blkRef
	 * @return
	 */
	private static double[] getHatchTrueLength(OdDbHatch entityobj) {
		OdRxObjectPtrArray odrxobjectptrarray=new OdRxObjectPtrArray();
		entityobj.explode(odrxobjectptrarray);
		for (int i = 0; i < odrxobjectptrarray.size(); i++) {
			OdRxObject obj = odrxobjectptrarray.get(i);
			if (obj.isKindOf(OdDbLine.desc())) { // obj instanceof OdDbLine
				OdDbLine line = OdDbLine.cast(obj);
				OdGePoint3d startPoint = new OdGePoint3d();
				line.getStartPoint(startPoint);
				double x = startPoint.getX();
				double y = startPoint.getY();
				return new double[] { x, y };
			} else if (obj.isKindOf(OdDbSolid.desc())) {
				OdDbSolid text = OdDbSolid.cast(obj);
				// System.out.println(line.isErased());
				OdGePoint3d startPoint = new OdGePoint3d();
				text.getPointAt(0, startPoint);
				double x = startPoint.getX();
				double y = startPoint.getY();
				return new double[] { x, y };
			} else if (obj.isKindOf(OdDbPoint.desc())) {
				OdDbPoint text = OdDbPoint.cast(obj);
				OdGePoint3d startPoint = text.position();
				double x = startPoint.getX();
				double y = startPoint.getY();
				return new double[] { x, y };
			} else if (obj.isKindOf(OdDbPolyline.desc())) {
				OdDbPolyline poly = OdDbPolyline.cast(obj);
				// System.out.println(line.isErased());
				OdGePoint3d startPoint = new OdGePoint3d();
				poly.getStartPoint(startPoint);
				double x = startPoint.getX();
				double y = startPoint.getY();
				return new double[] { x, y };
			} else if (obj.isKindOf(OdDbText.desc())) {
				OdDbText text = OdDbText.cast(obj);
				OdGePoint3d startPoint = text.position();
				double x = startPoint.getX();
				double y = startPoint.getY();
				return new double[] { x, y };
				// System.out.println(line.isErased());
			} else if (obj.isKindOf(OdDbMText.desc())) {
				OdDbMText text = OdDbMText.cast(obj);
				OdGePoint3d startPoint = text.location();
				double x = startPoint.getX();
				double y = startPoint.getY();
				return new double[] { x, y };
			} else if (obj.isKindOf(OdDbCircle.desc())) {
				OdDbCircle text = OdDbCircle.cast(obj);
				OdGePoint3d startPoint = new OdGePoint3d();
				text.getStartPoint(startPoint);
				double x = startPoint.getX();
				double y = startPoint.getY();
				return new double[] { x, y };
			} else if (obj.isKindOf(OdDbArc.desc())) {
				OdDbArc text = OdDbArc.cast(obj);
				OdGePoint3d startPoint = new OdGePoint3d();
				text.getStartPoint(startPoint);
				double x = startPoint.getX();
				double y = startPoint.getY();
				return new double[] { x, y };
			} else if (obj.isKindOf(OdDb2dPolyline.desc())) {
				OdDb2dPolyline text = OdDb2dPolyline.cast(obj);
				OdGePoint3d startPoint = new OdGePoint3d();
				text.getStartPoint(startPoint);
				double x = startPoint.getX();
				double y = startPoint.getY();
				return new double[] { x, y };
			} else if (obj.isKindOf(OdDbEllipse.desc())) {
				OdDbEllipse text = OdDbEllipse.cast(obj);
				OdGePoint3d startPoint = new OdGePoint3d();
				text.getStartPoint(startPoint);
				double x = startPoint.getX();
				double y = startPoint.getY();
				return new double[] { x, y };
			} else if (obj.isKindOf(OdDbSpline.desc())) {
				OdDbSpline text = OdDbSpline.cast(obj);
				OdGePoint3d startPoint = new OdGePoint3d();
				text.getStartPoint(startPoint);
				double x = startPoint.getX();
				double y = startPoint.getY();
				return new double[] { x, y };
			} else if (obj.isKindOf(OdDbLeader.desc())) {
				OdDbLeader text = OdDbLeader.cast(obj);
				OdGePoint3d startPoint = new OdGePoint3d();
				text.getStartPoint(startPoint);
				double x = startPoint.getX();
				double y = startPoint.getY();
				return new double[] { x, y };
			}else if (obj.isKindOf(OdDbTable.desc())) {
				OdDbTable text = OdDbTable.cast(obj);
				OdGePoint3d startPoint = text.position();
				double x = startPoint.getX();
				double y = startPoint.getY();
				return new double[] { x, y };
			} else if (obj.isKindOf(OdDbRotatedDimension.desc())) {
				OdDbRotatedDimension text = OdDbRotatedDimension.cast(obj);
				OdGePoint3d startPoint = text.dimLinePoint();
				double x = startPoint.getX();
				double y = startPoint.getY();
				return new double[] { x, y };
			} else if (obj.isKindOf(OdDbAlignedDimension.desc())) {
				OdDbAlignedDimension text = OdDbAlignedDimension.cast(obj);
				OdGePoint3d startPoint = text.dimLinePoint();
				double x = startPoint.getX();
				double y = startPoint.getY();
				return new double[] { x, y };
			} else if (obj.isKindOf(OdDbMLeader.desc())) {
				OdDbMLeader text = OdDbMLeader.cast(obj);
				OdGePoint3d startPoint = new OdGePoint3d();
				text.getTextLocation(startPoint);
				double x = startPoint.getX();
				double y = startPoint.getY();
				return new double[] { x, y };
			} else if (obj.isKindOf(OdDbTrace.desc())) {
				OdDbTrace text = OdDbTrace.cast(obj);
				OdGePoint3d startPoint = new OdGePoint3d();
				text.getPointAt(0, startPoint);
				double x = startPoint.getX();
				double y = startPoint.getY();
				return new double[] { x, y };
			} else if (obj.isKindOf(OdDbCurve.desc())) {
				OdDbCurve text = OdDbCurve.cast(obj);
				OdGePoint3d startPoint = new OdGePoint3d();
				text.getStartPoint(startPoint);
				double x = startPoint.getX();
				double y = startPoint.getY();
				return new double[] { x, y };
			}else if(obj instanceof OdDbOle2Frame){
				OdDbOle2Frame text=OdDbOle2Frame.cast(obj);
				OdRectangle3d point=new OdRectangle3d();
				text.position(point);
				OdGePoint3d tpoint=point.getLowLeft();
				double x = tpoint.getX();
				double y = tpoint.getY();
				return new double[] { x, y };
			} else if(obj instanceof OdDbDimension){
				OdDbDimension text=OdDbDimension.cast(obj);
				OdGePoint3d startPoint = text.textPosition();
				double x = startPoint.getX();
				double y = startPoint.getY();
				return new double[] { x, y };
			}else if (obj.isKindOf(OdDbBlockReference.desc())) {
				OdDbBlockReference text = OdDbBlockReference.cast(obj);
				double x = text.position().getX();
				double y = text.position().getY();
				return new double[] { x, y };
			}else{
				System.out.println("hatch-"+obj);
			}
		}
		return null;
	}
	

	
	private static void addWatermark(PdfStamper pdfStamper, String waterMarkName) {
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
				content.setColorFill(BaseColor.GREEN);
				content.setFontAndSize(base, 100);
				 // 水印文字成45度角倾斜
				content.showTextAligned(Element.ALIGN_CENTER, waterMarkName, x,y, 45);
				content.endText();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			content = null;
			base = null;
			pageRect = null;
		}
	}
	public synchronized static File convertFile(File from, File to) throws Exception {
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


}
