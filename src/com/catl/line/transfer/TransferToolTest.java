package com.catl.line.transfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.opendesign.core.DwgVersion;
import com.opendesign.core.ExSystemServices;
import com.opendesign.core.OdGePoint3d;
import com.opendesign.core.OdRxObject;
import com.opendesign.core.OdRxObjectPtrArray;
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
import com.opendesign.td.OdDbObjectIterator;
import com.opendesign.td.OdDbOle2Frame;
import com.opendesign.td.OdDbPoint;
import com.opendesign.td.OdDbPolyline;
import com.opendesign.td.OdDbProxyEntity;
import com.opendesign.td.OdDbRadialDimensionLarge;
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

public class TransferToolTest {
	static {
		try {
			System.loadLibrary("TeighaJavaCore");
			System.loadLibrary("TeighaJavaDwg");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取多图框转成后的PDF文件
	 * 
	 * @param srcFileName
	 * @return
	 * @throws Exception
	 */
	public synchronized static File getMultiBoxPDF(String srcFileName)
			throws Exception {
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
				throw new LineException("不识别的图纸,请检查图纸是否损坏");
			}
		}
		List list = new ArrayList();
		List pagenum = new ArrayList();
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
								.cast(obj);
						OdDbObjectIterator iter = blkRef.attributeIterator();
						for (int i = 0; !iter.done(); i++, iter.step()) {
							OdDbAttribute attr = OdDbAttribute.cast(iter
									.entity().objectId()
									.openObject(OpenMode.kForWrite));
							if (OdDbAttribute.getCPtr(attr) != 0) {
								if (attr.tag().equals(ConstantDwg.dwg_page_num)) {
									try{
									int[] xynum = new int[3];
									xynum[0] = (int) attr.position().getX();
									xynum[1] = (int) attr.position().getY();
									String numval = attr.textString().toUpperCase().split(ConstantDwg.dwg_page_num_sign)[0].trim();
									xynum[2] = Integer.valueOf(numval);
									pagenum.add(xynum);
									}catch(Exception e){
										throw new LineException("获取图框页码失败");
									}
								}
							}
						}
					}
				}
				entityIter = OdDbObjectIterator.cast(block.newIterator());
				List xystrs=new ArrayList();
				for (; !entityIter.done(); entityIter.step()) {
					OdDbObject obj = entityIter.objectId().openObject();
					if (obj.isKindOf(OdDbPolyline.desc())) {
						OdDbPolyline table = (OdDbPolyline) obj;
						OdDbPolyline text = OdDbPolyline.cast(table.objectId()
								.openObject(OpenMode.kForWrite));
						OdGePoint3d startPoint = new OdGePoint3d();
						OdGePoint3d endPoint = new OdGePoint3d();
						if (text.layer().equals(ConstantDwg.dwg_print_frame)) {
							text.getStartPoint(startPoint);
							text.getEndPoint(endPoint);
							double[] area = { 0.0 };
							text.getArea(area);
							double[] size=getPaperSize(area[0]);//打印图框不规范的大小
							if (area[0] > 0 && isModelSpace(text)&&size!=null) {
								double[] xyarea = new double[3];
								xyarea[0] = startPoint.getX();
								xyarea[1] = startPoint.getY();
								xyarea[2] = area[0];
								String xystr=xyarea[0]+","+xyarea[1];
								if(!xystrs.contains(xystr)){
									xystrs.add(xystr);
									list.add(xyarea);
								}
							}
						}

					}
				}

			}
			//oddb.writeFile(srcFileName, SaveType.kDwg, DwgVersion.vAC18, true);
		} catch (Exception e) {
			throw e;
		} finally {
			oddb.delete();
			hostApp.delete();
			systemServices.delete();
		}

		if (list.size() == 1 || list.size() == 0) {
			DwgToPdfFileConverter coverter = new DwgToPdfFileConverter();
			File dwgfile = new File(srcFileName);
			File pdffile = new File(dwgfile.getAbsolutePath().replace(".dwg",".pdf"));
			pdffile = coverter.convertFile(dwgfile, pdffile);
			return pdffile;
		} else {
			File file1 = new File(srcFileName);
			String pdf = retirePDF(file1, oddb, list, pagenum);
			File pdffile = new File(pdf);
			hostApp.delete();
			return pdffile;
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
								if (value.equals("0")) {
									value = "";
								}
								attr.setTextString(value);
								attr.adjustAlignment();
							}
						}

					}
				}
			}
		}
		oddb.writeFile(srcFileName, SaveType.kDwg, DwgVersion.vAC18, true);
		hostApp.delete();
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
		DwgToPdfFileConverter coverter = new DwgToPdfFileConverter();
		String f1name = f1.getName().replace(".dwg", "");
		String dwg_temp_path = ConstantDwg.dwg_localpath + File.separator
				+ f1name + File.separator;
		FileUtil.createDir(dwg_temp_path);
		for (int i = 0; i < xyarea.size(); i++) {
			double[] cr = xyarea.get(i);
			double[] currentrange = getRange(cr[0], cr[1], cr[2]);
			int num = getPageNum(currentrange, pagenum);
			File f2 = new File(dwg_temp_path + f1name + num + ".dwg");
			copyFile(f1, f2);
			ExSystemServices systemServices = new ExSystemServices();
			ExHostAppServices hostApp = new ExHostAppServices();
			try {
				hostApp.disableOutput(true);
				TD_Db.odInitialize(systemServices);
				OdDbDatabase oddb = hostApp.readFile(dwg_temp_path + f1name+ num + ".dwg");
				deleteremainbox(oddb, currentrange);
				oddb.writeFile(dwg_temp_path + f1name + num + ".dwg",
						SaveType.kDwg, DwgVersion.vAC18, true);
				File file1 = new File(dwg_temp_path + f1name + num + ".dwg");
				System.out.println(dwg_temp_path + f1name + num + ".pdf");
				File file2 = new File(dwg_temp_path + f1name + num + ".pdf");
				pdfs.add(dwg_temp_path + f1name + num + ".pdf");
				coverter.convertFile(file1, file2);
			} catch (Exception e) {
				throw e;
			} finally {
				hostApp.delete();
				systemServices.delete();
			}
		}
		java.util.Collections.sort(pdfs);
		coverter.mergePdf(pdfs, dwg_temp_path + f1name + ".pdf");
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
	 * 
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
				if (obj.isKindOf(OdDbLine.desc())) { // obj instanceof OdDbLine
					OdDbLine line = OdDbLine.cast(obj);
					OdGePoint3d startPoint = new OdGePoint3d();
					line.getStartPoint(startPoint);
					double x = startPoint.getX();
					double y = startPoint.getY();
					if (!(x < maxx && x > minx && y < maxy && y > miny)
							&& isModelSpace(line)) {
						if (!line.isErased() && isModelSpace(line)) {
							line = OdDbLine.cast(obj.objectId().openObject(
									OpenMode.kForWrite));
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
					double x = 0;
					double y = 0;
					OdRxObjectPtrArray odrxobjectptrarray=new OdRxObjectPtrArray();
					text.explode(odrxobjectptrarray);
					for (int i = 0; i < odrxobjectptrarray.size(); i++) {
						OdDbObject cobj=(OdDbObject) odrxobjectptrarray.get(i);
						OdGePoint3d startPoint = new OdGePoint3d();
						if (cobj.isKindOf(OdDbSolid.desc())) {
							OdDbSolid soild = OdDbSolid.cast(cobj);
							soild.getPointAt(0, startPoint);
							x = startPoint.getX();
							y = startPoint.getY();
							break;
						}
						
					}
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
							// System.out.println(blkRef.position().getX()+"-----"+blkRef.position().getY()+"----"+x+"----"+y);
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
				}else if(obj instanceof OdDbProxyEntity){
					OdDbProxyEntity text = OdDbProxyEntity.cast(obj);
					double[] entitys=getTrueLength(text);
					if (text != null) {
						double x = entitys[0];
						double y = entitys[1];
						if (!(x < maxx && x > minx && y < maxy && y > miny)
								&& isModelSpace(text)) {
							if (!text.isErased() && isModelSpace(text)) {
								text = OdDbProxyEntity.cast(obj.objectId().openObject(
										OpenMode.kForWrite));
								if(!text.eraseAllowed()){
									throw new LineException("无法操作对象。请确认是否保存为普通版CAD图纸");
								}
								text.erase();
							}
						}
					}
				}else if (obj instanceof OdDbCurve) {
					OdDbCurve text = OdDbCurve.cast(obj.objectId().openObject(
							OpenMode.kForWrite));
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
				}else if(obj instanceof OdDbOle2Frame){
					OdDbOle2Frame text=OdDbOle2Frame.cast(obj.objectId().openObject(
							OpenMode.kForWrite));
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
					
				}else if(obj instanceof OdDbDimension){
					OdDbDimension text=OdDbDimension.cast(obj.objectId().openObject(
							OpenMode.kForWrite));
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
					System.out.println(obj);
				}
			}

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
		}else if(entityobj.isKindOf(OdDbProxyEntity.desc())){
			OdDbProxyEntity entity =OdDbProxyEntity.cast(entityobj.objectId().openObject(
					OpenMode.kForWrite));
			if (entity == null) {
				return null;
			}
			entity.explode(paramOdRxObjectPtrArray);
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
	public static void main(String[] args) throws Exception {
		getMultiBoxPDF("E://550650-00228.dwg");
	}
}
