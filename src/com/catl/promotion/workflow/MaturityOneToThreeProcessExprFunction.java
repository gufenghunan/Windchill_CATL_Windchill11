package com.catl.promotion.workflow;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;

import com.catl.change.report.Excel2007Handler;
import com.catl.common.util.CatlFolderUtil;
import com.catl.common.util.PartUtil;
import com.catl.common.util.WCLocationConstants;
import com.catl.doc.maturityUpReport.MaturityProcess;
import com.catl.doc.maturityUpReport.MaturityUpResult;
import com.catl.doc.maturityUpReport.workflow.WfUtil;
import com.catl.promotion.util.WorkflowUtil;

import wt.fc.ObjectReference;
import wt.fc.collections.WTArrayList;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.pom.Transaction;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.workflow.engine.WfProcess;

public class MaturityOneToThreeProcessExprFunction {

	private static final Logger LOGGER = Logger.getLogger(MaturityOneToThreeProcessExprFunction.class);

	// ====================FAE物料成熟度升级流程=========================start

	// 表达式处理
	public static String addAttachment(ObjectReference self, List<MaturityUpResult> list, boolean bolean) throws WTException {

		LOGGER.info("list size:" + list.size());
		Transaction transaction = null;

		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			transaction = new Transaction();
			transaction.start();

			WfProcess process = (WfProcess) self.getObject();
			Workbook xlsx = exportReport(list, bolean);

			ByteArrayOutputStream otputStream = new ByteArrayOutputStream();
			xlsx.write(otputStream);
			ByteArrayInputStream inputStream = new ByteArrayInputStream(otputStream.toByteArray());
			otputStream.close();

			String urlhtml = WorkflowUtil.replaceSecondaryContentWithNoCheckOut(process, "非FAE物料成熟度1升级3报表.xlsx", inputStream, "生成的报表上传到附件", null, null, null);

			transaction.commit();
			transaction = null;

			return urlhtml;
		} catch (Exception e) {
			e.printStackTrace();
			throw new WTException(e);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(flag);
			if (transaction != null) {
				transaction.rollback();
				transaction = null;
			}
		}
	}

	public static MaturityProcess isUpgradeResult(ObjectReference self, WTPart part) throws WTException {

		List<MaturityUpResult> list = new ArrayList<MaturityUpResult>();
		WTArrayList array = new WTArrayList();
		boolean bolean = WfUtil.upMaturityTo3(part, list);
		String result = "";

		if (bolean) {
			for (MaturityUpResult MaturityUpResult : list) {
				if(MaturityUpResult.isCheckPass()){
					WTPartMaster master = MaturityUpResult.getMaster();
					array.add(master);
				}
			}
			result = "成功";
			WfUtil.addPartMaturityChangeLog(self, list, "1", "3");
		} else {
			result = "失败";
		}

		String url = addAttachment(self, list, bolean);

		MaturityProcess maturityProcess = new MaturityProcess();
		maturityProcess.setResult(result);
		maturityProcess.setWTArrayList(array);
		maturityProcess.setUrl(url);

		return maturityProcess;
	}

	// ====================FAE物料成熟度升级流程=========================end

	/**
	 * 生成要导出的Excel文件
	 * 
	 * @param bolean
	 * 
	 * @return Workbook
	 * @throws WTException
	 */
	public static Workbook exportReport(List<MaturityUpResult> list, boolean bolean) throws WTException {

		String filePathName = WCLocationConstants.WT_CODEBASE + File.separator + "com" + File.separator + "catl" + File.separator + "checkPDFData"
				+ File.separator + "MaturityOneToThree_template.xlsx";

		try {
			Excel2007Handler excelHander = new Excel2007Handler(filePathName);
			Workbook xlsx = excelHander.getWorkbook();

			for (int i = 0; i < list.size(); i++) {
				int rowNum = i + 2;
				int iCol = 0;

				StringBuffer msg = new StringBuffer();
				String result = "";
				if (bolean) {
					result = "已升级";
				} else {
					result = "未升级";
					if (!list.get(i).isCheckPass()) {
						List<String> ErroeMsgs = list.get(i).getErrorMsgs();
						int j = 1;
						for (String str : ErroeMsgs) {
							if (!(str == null)) {
								msg.append(j++).append(". ").append(str).append("。");
								if (ErroeMsgs.size() > 1 && j < ErroeMsgs.size() + 1) {
									msg.append("\n");
								}
							}
						}
					}
				}
				
				WTPart latesedPart = PartUtil.getLastestWTPartByNumber(list.get(i).getMaster().getNumber());
				excelHander.setStringValue(rowNum, iCol++, latesedPart.getNumber());
				excelHander.setStringValue(rowNum, iCol++, latesedPart.getName());
				excelHander.setStringValue(rowNum, iCol++, result);
				excelHander.setStringValue(rowNum, iCol++, msg.toString());
				excelHander.setStringValue(rowNum, iCol++, latesedPart.getCreatorFullName());
				excelHander.setStringValue(rowNum, iCol++, latesedPart.getModifierFullName());
				
				excelHander.setStringValue(rowNum, iCol++, CatlFolderUtil.getLocationString(latesedPart));
			}
			
			xlsx.getSheet("Sheet1").getColumnStyle(1).setWrapText(true);
			return xlsx;

		} catch (Exception e) {
			e.printStackTrace();
			throw new WTException(e.getLocalizedMessage());
		}
	}

}
