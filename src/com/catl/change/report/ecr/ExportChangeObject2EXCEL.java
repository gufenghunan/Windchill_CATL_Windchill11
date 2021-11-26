package com.catl.change.report.ecr;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.catl.promotion.util.PromotionUtil;

import wt.change2.ChangeException2;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.pom.Transaction;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.wip.WorkInProgressHelper;

public class ExportChangeObject2EXCEL {

	private static Logger log = Logger.getLogger(ExportChangeObject2EXCEL.class.getName());

	WTChangeRequest2 ecrObj;
	WTChangeOrder2 ecoObj;

	private static WTProperties wtProperties;
	boolean fromECR = true;

	static {
		try {
			wtProperties = WTProperties.getLocalProperties();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setPbo(WTObject pbo) {

		if (pbo instanceof WTChangeRequest2) {
			this.ecrObj = (WTChangeRequest2) pbo;
			this.fromECR = true;
		} else {
			this.ecoObj = (WTChangeOrder2) pbo;
			this.fromECR = false;
		}
	}

	public ExportChangeObject2EXCEL(WTObject pbo) {
		setPbo(pbo);
	}

	/*
	 * Loop for every affted WTPart Get part’s parent and the root part Write the result to excel Then attached the excel to ECR attchments
	 * list.
	 */
	public String getFileName() {
		if (fromECR) {
			return ecrObj.getNumber();
		}
		return ecoObj.getNumber();
	}

	public static boolean attachAffedtedProduct(WTObject pbo) {
		try {
			ExportChangeObject2EXCEL report = new ExportChangeObject2EXCEL(pbo);
			String attachmentFileName = report.getFileName() + "_受影响的物料及产品";
			String filePath = report.generateAttachment(attachmentFileName);
			report.addAttachment(attachmentFileName, filePath);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private void addAttachment(String fileName, String filePath) throws WTException, FileNotFoundException, PropertyVetoException, IOException {
		Transaction trans = null;
		java.io.FileInputStream is = null;
		try {
			trans = new Transaction();
			trans.start();
			// zhengjiahong edit
			ContentHolder content = ContentHelper.service.getContents(fromECR ? (ContentHolder) ecrObj : (ContentHolder) ecoObj);
			Vector vApplicationData = ContentHelper.getApplicationData(content);
			for (int i = 0; i < vApplicationData.size(); i++) {
				ApplicationData applicationdata = (ApplicationData) vApplicationData.elementAt(i);
				if (applicationdata.getFileName().equals(fileName + ".xls")) {
					ContentServerHelper.service.deleteContent(content, applicationdata);
					break;
				}
			}

			is = new java.io.FileInputStream(filePath);

			ApplicationData applicationData = ApplicationData.newApplicationData(fromECR ? (ContentHolder) ecrObj : (ContentHolder) ecoObj);
			applicationData.setRole(ContentRoleType.SECONDARY);
			applicationData.setFileName(fileName + ".xls");
			applicationData.setDescription("系统自动生成");
			applicationData = (ApplicationData) PersistenceHelper.manager.save(applicationData);
			applicationData = ContentServerHelper.service.updateContent(fromECR ? (ContentHolder) ecrObj : (ContentHolder) ecoObj, applicationData, is);

			trans.commit();
			File file = new File(filePath);
			if (file.exists()) {
				file.delete();
			}
		} catch (Exception e) {
			if (trans != null)
				trans.rollback();
			throw e;
		} finally {
			if (is != null)
				is.close();
		}
	}

	private String generateAttachment(String fileName) throws ChangeException2, WTException, InvalidFormatException, FileNotFoundException, IOException {
		String filePath = wtProperties.getProperty("wt.temp");
		File dir = new File(filePath);
		dir.mkdirs();
		filePath = filePath + File.separator + fileName + ".xls";
		ArrayList<WTPart> parts = new ArrayList<WTPart>();
		QueryResult qr = getAffectedObjects();
		while (qr.hasMoreElements()) {
			Object obj = qr.nextElement();
			if (obj instanceof WTPart) {
				if (!parts.contains(obj))
					parts.add((WTPart) obj);
			}
		}
		Collections.sort(parts, new WTPartComparator());
		writeExcel(filePath, parts);
		return filePath;
	}

	private QueryResult getAffectedObjects() throws ChangeException2, WTException {
		if (fromECR) {
			return ChangeHelper2.service.getChangeables(ecrObj);
		}
		return ChangeHelper2.service.getChangeablesBefore(ecoObj);
	}

	@SuppressWarnings("resource")
	private void writeExcel(String filePath, ArrayList<WTPart> parts) throws InvalidFormatException, FileNotFoundException, IOException, WTException {
		ArrayList<ECRUsedByBean> partBeans = getPartsBean(parts);
		ArrayList<ECRUsedByBean> replaceBeans = getReplaceBean(parts);
		HSSFWorkbook workbook = new HSSFWorkbook();

		// for part
		Sheet partSheet = workbook.createSheet("parts");
		Row pRow = partSheet.createRow(0);
		// title 待变更数据编码 待变更数据名称 直接父件编码 直接父件名称 产品编码 产品名称 产品文件夹
		String pTitle[] = { "待变更数据编码", "待变更数据名称", "直接父件编码", "直接父件名称", "产品编码", "产品名称", "产品文件夹" };
		for (int tCount = 0; tCount < pTitle.length; tCount++) {
			Cell tCell = pRow.createCell(tCount);
			tCell.setCellValue(pTitle[tCount]);
		}
		// data
		int p = 0;
		for (ECRUsedByBean bean : partBeans) {
			Row row = partSheet.createRow(++p);
			int c = 0;
			Cell cell = row.createCell(c++);
			cell.setCellValue(bean.getPartNumber());

			cell = row.createCell(c++);
			cell.setCellValue(bean.getPartName());

			cell = row.createCell(c++);
			cell.setCellValue(bean.getParentNumber());

			cell = row.createCell(c++);
			cell.setCellValue(bean.getParentName());

			cell = row.createCell(c++);
			cell.setCellValue(bean.getRootNumber());

			cell = row.createCell(c++);
			cell.setCellValue(bean.getRootName());

			cell = row.createCell(c);
			cell.setCellValue(bean.getRootFolder());
		}

		// replacements
		Sheet replaceSheet = workbook.createSheet("作为特定替换件的上层物料及产品分析");
		
		HSSFCellStyle style1 = (HSSFCellStyle) workbook.createCellStyle();
    	Font font = workbook.createFont();
    	font.setFontHeightInPoints((short)12); //字体大小
        font.setFontName("华文楷体");
        font.setBoldweight(Font.BOLDWEIGHT_BOLD); //字体加粗
    	style1.setFont(font);
    	style1.setFillBackgroundColor(HSSFColor.GREY_25_PERCENT.index);
    	style1.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
    	style1.setFillPattern(CellStyle.SOLID_FOREGROUND);
		
		Row replaceRow = replaceSheet.createRow(0);
		// title "待变更数据编码", "待变更数据名称", "被替换件编码", "被替换件的直接父件编码", "被替换件的直接父件名称", "产品编码", "产品名称", "产品文件夹"
		String replaceRowTitle[] = { "待变更数据编码", "待变更数据名称", "被替换件编码", "被替换件的直接父件编码", "被替换件的直接父件名称", "产品编码", "产品名称", "产品文件夹" };
		int[] width = {20,40,20,30,40,20,40,40};
		for (int rCount = 0; rCount < replaceRowTitle.length; rCount++) {
			Cell tCell = replaceRow.createCell(rCount);
			tCell.setCellStyle(style1);
			tCell.setCellValue(replaceRowTitle[rCount]);
			replaceSheet.setColumnWidth(rCount, width[rCount]*256);
		}
		// data
		int i = 0;
		for (ECRUsedByBean bean : replaceBeans) {
			Row row = replaceSheet.createRow(++i);
			int c = 0;
			Cell cell = row.createCell(c++);
			cell.setCellValue(bean.getPartNumber());

			cell = row.createCell(c++);
			cell.setCellValue(bean.getPartName());

			cell = row.createCell(c++);
			cell.setCellValue(bean.getByReplaceNumber());

			cell = row.createCell(c++);
			cell.setCellValue(bean.getByReplaceParentNumber());

			cell = row.createCell(c++);
			cell.setCellValue(bean.getByReplaceParentName());

			cell = row.createCell(c++);
			cell.setCellValue(bean.getRootNumber());
			
			cell = row.createCell(c++);
			cell.setCellValue(bean.getRootName());

			cell = row.createCell(c);
			cell.setCellValue(bean.getRootFolder());
		}

		FileOutputStream out = new FileOutputStream(filePath);
		workbook.write(out);
		out.flush();
		out.close();
	}

	private ArrayList<ECRUsedByBean> getPartsBean(ArrayList<WTPart> parts) throws WTException {
		ArrayList<ECRUsedByBean> result = new ArrayList<ECRUsedByBean>();
		for (WTPart part : parts) {
			QueryResult qr = WTPartHelper.service.getUsedByWTParts((WTPartMaster) part.getMaster());
			ArrayList<WTPart> parentList = new ArrayList<WTPart>();
			if (qr.size() == 0) {
				ECRUsedByBean bean = new ECRUsedByBean(part.getNumber(), part.getName());
				bean.setParentName("");
				bean.setParentNumber("");
				bean.setRootNumber("");
				bean.setRootName("");
				bean.setRootFolder("");
				if (!result.contains(bean)) {
					result.add(bean);
				}
				continue;
			}
			while (qr.hasMoreElements()) {
				WTPart parent = (WTPart) qr.nextElement();
				if (!parentList.contains(parent)) {
					parentList.add(parent);
				}
			}
			Collections.sort(parentList, new WTPartComparator());
			for (WTPart parent : parentList) {
				ArrayList<WTPart> rootList = new ArrayList<WTPart>();
				queryRoots(parent, rootList);
				Collections.sort(rootList, new WTPartComparator());
				for (WTPart root : rootList) {
						if(WorkInProgressHelper.isCheckedOut(root) && WorkInProgressHelper.isWorkingCopy(root))
							root = (WTPart) WorkInProgressHelper.service.originalCopyOf(root);

					ECRUsedByBean bean = new ECRUsedByBean(part.getNumber(), part.getName());
					bean.setParentName(parent.getName());
					bean.setParentNumber(parent.getNumber());
					if (root.getNumber().equals(parent.getNumber())) {
						bean.setRootNumber("");
						bean.setRootName("");
						bean.setRootFolder("");
					} else {
						bean.setRootNumber(root.getNumber());
						bean.setRootName(root.getName());
						bean.setRootFolder(root.getFolderPath());
					}
					if (!result.contains(bean)) {
						result.add(bean);
					}
				}
			}
		}
		return result;
	}
	
	public static ArrayList<ECRUsedByBean> getReplaceBean(ArrayList<WTPart> parts) throws WTException {
		ArrayList<ECRUsedByBean> result = new ArrayList<ECRUsedByBean>();
		
		for (WTPart part : parts) {			
			Map<WTPart, Map<WTPart, WTPartMaster>> bom = PromotionUtil.getReplacePieceBom(part);
			for (WTPart Z : bom.keySet()) {
				Set<WTPart> allTopParents = new HashSet<WTPart>();
				PromotionUtil.getAllPartTopParent(Z, allTopParents);
				for (WTPart A : allTopParents) {
					if(WorkInProgressHelper.isCheckedOut(A) && WorkInProgressHelper.isWorkingCopy(A))
						A = (WTPart) WorkInProgressHelper.service.originalCopyOf(A);
					Map<WTPart, WTPartMaster> temp = bom.get(Z);
					for (WTPart X : temp.keySet()) {
						ECRUsedByBean bean = new ECRUsedByBean();
						bean.setPartNumber(X.getNumber());
						bean.setPartName(X.getName());
						bean.setByReplaceNumber(temp.get(X).getNumber());
						bean.setByReplaceParentNumber(Z.getNumber());
						bean.setByReplaceParentName(Z.getName());
						bean.setRootNumber(A.getNumber());
						bean.setRootName(A.getName());						
						String loc = A.getLocation();
		                if(loc.contains("Default")){
		               	 loc = loc.replace("Default",A.getContainerName());
		                }
						bean.setRootFolder(loc);
						
						if (!result.contains(bean)) {
							result.add(bean);
						}
					}
				}				
			}
		}
		return result;
	}

	private void queryRoots(WTPart parent, ArrayList<WTPart> rootList) throws WTException {
		QueryResult qr = WTPartHelper.service.getUsedByWTParts((WTPartMaster) parent.getMaster());
		if (qr.size() == 0) {
			if (!rootList.contains(parent)) {
				rootList.add(parent);
			}
			return;
		}
		while (qr.hasMoreElements()) {
			WTPart part = (WTPart) qr.nextElement();
			queryRoots(part, rootList);
		}
	}

	class WTPartComparator implements Comparator<WTPart> {
		@Override
		public int compare(WTPart part1, WTPart part2) {
			return part1.getNumber().toLowerCase().compareTo(part2.getNumber().toLowerCase());
		}
	}
}
