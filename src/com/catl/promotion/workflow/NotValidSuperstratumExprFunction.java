package com.catl.promotion.workflow;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;

import wt.clients.vc.CheckInOutTaskLogic;
import wt.epm.retriever.LatestConfigSpecWithoutWorkingCopies;
import wt.fc.ObjectReference;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTCollection;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartSubstituteLink;
import wt.part.WTPartUsageLink;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.wip.WorkInProgressHelper;
import wt.workflow.engine.WfProcess;

import com.catl.change.report.Excel2007Handler;
import com.catl.change.report.ExportBomDataByPart;
import com.catl.common.util.IBAUtil;
import com.catl.common.util.WCLocationConstants;
import com.catl.part.PartConstant;
import com.catl.promotion.util.PromotionUtil;
import com.catl.promotion.util.WorkflowUtil;

public class NotValidSuperstratumExprFunction {

	private static final Logger LOGGER = Logger.getLogger(NotValidSuperstratumExprFunction.class);

	// ====================无有效上层BOM的物料报表=========================start

	// 表达式处理
	public static String generateReport(ObjectReference self) throws WTException {

		LOGGER.info("WfProcess Name:" + ((WfProcess) self.getObject()).getName());
		Transaction transaction = null;

		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			transaction = new Transaction();
			transaction.start();

			WfProcess process = (WfProcess) self.getObject();
			Workbook xlsx = exportReport();

			ByteArrayOutputStream otputStream = new ByteArrayOutputStream();
			xlsx.write(otputStream);
			ByteArrayInputStream inputStream = new ByteArrayInputStream(otputStream.toByteArray());
			otputStream.close();

			String urlhtml = WorkflowUtil.replaceSecondaryContentWithNoCheckOut(process, "无有效上层BOM的物料报表.xlsx", inputStream, "生成的报表上传到附件", null, null, null);

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

	// ====================无有效上层BOM的物料报表=========================end

	/**
	 * 1、 部件X为“零部件”且没有任何WTPartUsageLink Role B使用到这个部件； 2、 部件X为“零部件”，有WTPartUsageLink使用到这个部件X，但所有Link的Role
	 * A端的部件的最新大版本的最后小版本（排除工作副本）已没有再使用这个部件X；或者所有的Link的Role A端的部件的最新大版本的最后小版本（排除工作副本）生命周期状态已被设置为“设计禁用”；
	 */
	private static List<WTPartMaster> getNotValidSuperstratumPart() throws WTException {

		List<WTPartMaster> list = new ArrayList<WTPartMaster>();
		QueryResult qr = getAllPart(WTPartMaster.class);
		while (qr.hasMoreElements()) {
			WTPartMaster master = (WTPartMaster) qr.nextElement();
			boolean hasValidParent = false;
			
			// Step 1 首先检查是否有上层物料直接通过WTPartUsageLink来使用这个物料
			QueryResult qs = WTPartHelper.service.getUsedByWTParts(master);
			while (qs.hasMoreElements()) {
				WTPart part = (WTPart) qs.nextElement();
				WTPart partLaster = ExportBomDataByPart.getPartByNoAndView(part.getNumber(), "Design");
				if (WorkInProgressHelper.isWorkingCopy(partLaster)) {
					partLaster = (WTPart) CheckInOutTaskLogic.getOriginalCopy(partLaster);
				}
				if (part.equals(partLaster)) {
					//list.add(master);
					hasValidParent = true;
					break;
				}
				/**
				QueryResult qt = WTPartHelper.service.getUsesWTPartMasters(partLaster);
				if (qt.size() < 1) {
					list.add(master);
				} else {
					Set<WTPartMaster> masters = new HashSet<WTPartMaster>();
					while (qt.hasMoreElements()) {
						WTPartUsageLink link = (WTPartUsageLink) qt.nextElement();
						masters.add((WTPartMaster) link.getRoleBObject());
					}
					if (masters.contains(master)) {
						if (partLaster.getState().toString().endsWith(PartState.DISABLEDFORDESIGN)) {
							list.add(master);
						}
					} else {
						list.add(master);
					}
				} */
			}
			if (hasValidParent) {
				continue;
			}
			
			// Step 2, 检查是否有上层的物料通过WTPartSubstituteLink来使用该物料
			 WTCollection substituedLinks = WTPartHelper.service.getSubstituteForLinks(master);
			 for (Iterator it = substituedLinks.persistableIterator(); it.hasNext();) {
				 WTPartSubstituteLink link = (WTPartSubstituteLink) it.next();
				 WTPartUsageLink usageLink = link.getSubstituteFor();
				 WTPart parentPart = usageLink.getUsedBy();
				 
				 QueryResult allPartsOfParent = VersionControlHelper.service.allVersionsOf(parentPart);
				 LatestConfigSpecWithoutWorkingCopies configSpec = new LatestConfigSpecWithoutWorkingCopies();
				 allPartsOfParent = configSpec.process(allPartsOfParent);
				 if (allPartsOfParent.hasMoreElements()) {
					 WTPart latestPart = (WTPart) allPartsOfParent.nextElement();
					 if (parentPart.equals(latestPart)) {
						 hasValidParent = true;
						 break;
					 }
				 }
				 
			 }
			
			if (!hasValidParent) {
				list.add(master);
			}
		}
		return list;
	}

	/**
	 * 获取整张表的数据
	 */
	@SuppressWarnings({ "deprecation", "rawtypes" })
	private static QueryResult getAllPart(Class cass) throws WTException {

		QuerySpec qs = new QuerySpec(cass);

		return PersistenceHelper.manager.find(qs);
	}

	/**
	 * 生成要导出的Excel文件
	 * 
	 * @return Workbook
	 * @throws WTException
	 */
	public static Workbook exportReport() throws WTException {

		String filePathName = WCLocationConstants.WT_CODEBASE + File.separator + "com" + File.separator + "catl" + File.separator + "checkPDFData"
				+ File.separator + "NotValidSuperstrstratum_template.xlsx";

		List<WTPartMaster> list = getNotValidSuperstratumPart();

		try {
			Excel2007Handler excelHander = new Excel2007Handler(filePathName);

			for (int i = 0; i < list.size(); i++) {
				int rowNum = i + 2;
				int iCol = 0;
				WTPartMaster master = list.get(i);
				WTPart latestPart = (WTPart) PromotionUtil.getLatestVersionByMaster(master);
				if (WorkInProgressHelper.isWorkingCopy(latestPart)) {
					latestPart = (WTPart) CheckInOutTaskLogic.getOriginalCopy(latestPart);
				}
				/*
				 * 生命周期状态：物料最后大版本的最后小版本的生命周期状态； 成熟度：物料Master的成熟度属性 是否有下层物料：物料最后大版本的最后小版本是否有下层物料，如果部件已被检出，则取工作副本对应的原始版本；
				 * 创建日期：物料最后大版本的第一个小版本的创建日期； 物料创建者：物料最后大版本的第一个小版本（ControlBranch）的创建者 物料修改者：物料最后大版本的最后小版本的创建者
				 * 物料所在项目文件夹：产品库或存储库名称/部件所在文件名称/，例如：电子电器件库/项目A/部件
				 */
				excelHander.setStringValue(rowNum, iCol++, latestPart.getNumber());
				excelHander.setStringValue(rowNum, iCol++, latestPart.getName());
				excelHander.setStringValue(rowNum, iCol++, latestPart.getLifeCycleState().getDisplay(Locale.CHINA));
				excelHander.setStringValue(rowNum, iCol++, (String) IBAUtil.getIBAValue(master, PartConstant.IBA_CATL_Maturity));
				QueryResult qr = WTPartHelper.service.getUsesWTPartMasters(latestPart);
				if (qr != null && qr.size() > 0) {
					excelHander.setStringValue(rowNum, iCol++, "YES");
				} else {
					excelHander.setStringValue(rowNum, iCol++, "NO");
				}
				excelHander.setStringValue(rowNum, iCol++, ExportBomDataByPart.getFirstAndLastSmallVersion(latestPart).getCreateTimestamp().toString());
				excelHander.setStringValue(rowNum, iCol++, ExportBomDataByPart.getFirstAndLastSmallVersion(latestPart).getCreatorFullName());
				excelHander.setStringValue(rowNum, iCol++, latestPart.getCreatorFullName());
				String loc = latestPart.getLocation();
				if (loc.contains("Default")) {
					loc = loc.replace("Default", latestPart.getContainerName());
				}

				excelHander.setStringValue(rowNum, iCol++, loc);
			}

			return excelHander.getWorkbook();

		} catch (Exception e) {
			e.printStackTrace();
			throw new WTException(e.getLocalizedMessage());
		}
	}
}
