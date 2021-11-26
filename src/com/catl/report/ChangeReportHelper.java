package com.catl.report;

import java.io.File;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Sheet;
import org.drools.core.util.StringUtils;

import com.catl.bom.workflow.BomWfUtil;
import com.catl.common.constant.ChangeState;
import com.catl.common.constant.PartState;
import com.catl.common.constant.RoleName;
import com.catl.common.constant.TypeName;
import com.catl.common.util.IBAUtil;
import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.netmarkets.workflow.NmWorkflowHelper;

import wt.change2.ChangeException2;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTCollection;
import wt.lifecycle.LifeCycleException;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleHistory;
import wt.lifecycle.LifeCycleManaged;
import wt.maturity.PromotionNotice;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartSubstituteLink;
import wt.part.WTPartUsageLink;
import wt.project.Role;
import wt.query.DateHelper;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.session.SessionServerHelper;
import wt.team.Team;
import wt.team.TeamHelper;
import wt.util.WTException;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.workflow.engine.WfProcess;

public class ChangeReportHelper {
	String ecrNumber, status, createDateFrom, createDateTo, approveDateFrom, approveDateTo, releasedDateFrom, releasedDateTo;
	private static Logger log = Logger.getLogger(ChangeReportHelper.class.getName());
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	Role role = Role.toRole(RoleName.ASSIGNEE);

	private static WTProperties wtProperties;

	static {
		try {
			wtProperties = WTProperties.getLocalProperties();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ChangeReportHelper() {
	}

	public ChangeReportHelper(String ecrNumber, String status, String createDateFrom, String createDateTo, String approveDateFrom, String approveDateTo, String releasedDateFrom, String releasedDateTo) {
		this.ecrNumber = ecrNumber;
		this.status = status;
		this.createDateFrom = createDateFrom;
		this.createDateTo = createDateTo;
		this.approveDateFrom = approveDateFrom;
		this.approveDateTo = approveDateTo;
		this.releasedDateFrom = releasedDateFrom;
		this.releasedDateTo = releasedDateTo;
	}

	public void generateReport(HttpServletResponse response) throws WTException, WTPropertyVetoException, ParseException {
		log.debug("generateReport ecrNumber:" + ecrNumber + "createDateFrom:" + createDateFrom + "createDateTo:" + createDateTo);
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
			// get system current time add to the report name
			String currentDate = dateFormat.format((Date) new Timestamp(System.currentTimeMillis()));
			String fileName = "ChangeReport_" + currentDate + ".xls";
			OutputStream os = null;
			InputStream input = null;
			String filePath = generateExcel(fileName);
			String filename = java.net.URLEncoder.encode(fileName, "UTF-8");
			os = response.getOutputStream();
			response.setContentType("application/x-msdownload; charset=UTF-8");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
			File temp = new File(filePath);
			input = new FileInputStream(temp);
			byte[] buff = new byte[512];
			int len = 0;
			while ((len = input.read(buff)) != -1) {
				os.write(buff, 0, len);
			}
			response.setStatus(HttpServletResponse.SC_OK);
			response.flushBuffer();

			os.flush();
			input.close();
			os.close();
		} catch (IOException e) {
			throw new WTException(e, e.getLocalizedMessage());
		} finally {
			SessionServerHelper.manager.setAccessEnforced(flag);
		}
	}

	private String generateExcel(String fileName) throws ChangeException2, WTException, IOException, WTPropertyVetoException, ParseException {
		String filePath = wtProperties.getProperty("wt.temp") + fileName;
		log.debug(filePath);

		List<ChangeReportBean> beanList = new ArrayList<ChangeReportBean>();

		ArrayList<WTChangeOrder2> dcnList = queryDCN();
		for (WTChangeOrder2 ecn : dcnList) {

			QueryResult ecas = ChangeHelper2.service.getChangeActivities(ecn);
			while (ecas.hasMoreElements()) {
				WTChangeActivity2 eca = (WTChangeActivity2) ecas.nextElement();

				ChangeReportBean shareBean = new ChangeReportBean();
				shareBean.setEcnNumber(ecn.getNumber());
				shareBean.setEcnName(ecn.getName());
				shareBean.setEcnCreate(ecn.getCreator().getFullName());
				shareBean.setCreateDate(new Timestamp(ecn.getCreateTimestamp().getTime() + 28800000).toString().split(" ")[0]);
				// 批准日期
				Enumeration histories = (Enumeration) LifeCycleHelper.service.getHistory(ecn);
				Timestamp changeTime = null;
				while (histories.hasMoreElements()) {
					LifeCycleHistory lcHistory = (LifeCycleHistory) histories.nextElement();
					if (lcHistory.getState().toString().equals("IMPLEMENTATION") && lcHistory.getAction().equals("Enter_Phase")) {
						changeTime = new Timestamp(lcHistory.getCreateTimestamp().getTime() + 28800000);
					}
				}
				if (!checkDate(shareBean, changeTime, this.approveDateFrom, this.approveDateTo))// 批准时间不在
					continue;
				else if (changeTime != null)
					shareBean.setEcnApproveDate(changeTime.toString().split(" ")[0]);

				if (ecn.getNeedDate() != null)
					shareBean.setNeedDate(new Timestamp(ecn.getNeedDate().getTime() + 28800000).toString().split(" ")[0]);
				shareBean.setEcaNumber(eca.getNumber());
				shareBean.setEcaState(eca.getState().getState().getDisplay());
				// 工作负责人
				Team team2 = (Team) TeamHelper.service.getTeam(eca);
				if (team2 != null) {
					Enumeration enumPrin = team2.getPrincipalTarget(role);
					while (enumPrin.hasMoreElements()) {
						WTPrincipalReference tempPrinRef = (WTPrincipalReference) enumPrin.nextElement();
						shareBean.setEcaWorkOwner(tempPrinRef.getFullName());
					}
				}
				if (eca.getState().toString().equalsIgnoreCase(ChangeState.RESOLVED))
					shareBean.setIsRESOLVED("是");
				else
					shareBean.setIsRESOLVED("否");
				QueryResult changeBefore = ChangeHelper2.service.getChangeablesBefore(eca);
				QueryResult changeAfter = ChangeHelper2.service.getChangeablesAfter(eca);
				Map<String, Object> changeAfterMapPart = new HashMap<String, Object>();
				Map<String, Object> changeAfterMapDoc = new HashMap<String, Object>();
				Map<String, Object> changeAfterMapEpm = new HashMap<String, Object>();
				while (changeAfter.hasMoreElements()) {
					Object afterObject = changeAfter.nextElement();
					if (afterObject instanceof WTPart) {
						changeAfterMapPart.put(BomWfUtil.getObjectnumber((Persistable) afterObject), afterObject);
					} else if (afterObject instanceof WTDocument) {
						changeAfterMapDoc.put(BomWfUtil.getObjectnumber((Persistable) afterObject), afterObject);
					} else if (afterObject instanceof EPMDocument) {
						changeAfterMapEpm.put(BomWfUtil.getObjectnumber((Persistable) afterObject), afterObject);
					}
				}
				while (changeBefore.hasMoreElements()) {
					ChangeReportBean bean = (ChangeReportBean) shareBean.clone();
					Object beforeObject = changeBefore.nextElement();
					if (beforeObject instanceof WTPart) {
						Object afterObject = changeAfterMapPart.get(BomWfUtil.getObjectnumber((Persistable) beforeObject));
						WTPart beforePart = (WTPart) beforeObject;
						WTPart afterPart = null;
						if (afterObject != null)
							afterPart = (WTPart) afterObject;
						bean.setAffectObjectName(beforePart.getName());
						bean.setAffectObjectNumber(beforePart.getNumber());
						bean.setAffectObjectType("零部件");
						bean.setAffectObjectVersion(beforePart.getVersionIdentifier().getValue() + "." + beforePart.getIterationIdentifier().getValue());
						if (afterPart != null) {
							Timestamp releasedDate = getReleasedDate(afterPart, PartState.RELEASED);
							if (!checkDate(bean, releasedDate, this.releasedDateFrom, this.releasedDateTo))// 发布时间不在
																											// 查询条件之内
								continue;
							else if (releasedDate != null)
								bean.setAffectObjectReleaseDate(releasedDate.toString().split(" ")[0]);
							bean.setAffectObjectVersionAfter(afterPart.getVersionIdentifier().getValue() + "." + afterPart.getIterationIdentifier().getValue());
							compareBom(beanList, bean, beforePart, afterPart);
						} else {
							if (isNullOrBlank(this.releasedDateFrom) && isNullOrBlank(this.releasedDateTo))
								beanList.add(bean);
						}
					} else if (beforeObject instanceof WTDocument) {
						Object afterObject = changeAfterMapDoc.get(BomWfUtil.getObjectnumber((Persistable) beforeObject));
						WTDocument beforeDocument = (WTDocument) beforeObject;
						WTDocument afterDocument = null;
						if (afterObject != null)
							afterDocument = (WTDocument) afterObject;

						bean.setAffectObjectName(beforeDocument.getName());
						bean.setAffectObjectNumber(beforeDocument.getNumber());
						TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(beforeDocument);
						String type = ti.getTypename();
						if (type.endsWith(TypeName.doc_type_autocadDrawing)) {
							bean.setAffectObjectType("AutoCad文档");
						} else if (type.endsWith(TypeName.doc_type_technicalDoc)) {
							bean.setAffectObjectType("产品技术文件");
						} else if (type.endsWith(TypeName.doc_type_rdDoc)) {
							bean.setAffectObjectType("研发过程文档");
						} else if (type.endsWith(TypeName.doc_type_pcbaDrawing)) {
							bean.setAffectObjectType("pcba装配图");
						} else if (type.endsWith(TypeName.doc_type_EDatasheetDoc)) {
							bean.setAffectObjectType("Datasheet");
						}
						bean.setAffectObjectVersion(beforeDocument.getVersionIdentifier().getValue() + "." + beforeDocument.getIterationIdentifier().getValue());
						if (afterDocument != null) {
							Timestamp releasedDate = getReleasedDate(afterDocument, PartState.RELEASED);
							if (!checkDate(bean, releasedDate, this.releasedDateFrom, this.releasedDateTo))// 发布时间不在
																											// 查询条件之内
								continue;
							else if (releasedDate != null)
								bean.setAffectObjectReleaseDate(releasedDate.toString().split(" ")[0]);
							bean.setAffectObjectVersionAfter(afterDocument.getVersionIdentifier().getValue() + "." + afterDocument.getIterationIdentifier().getValue());
						}
						beanList.add(bean);
					} else if (beforeObject instanceof EPMDocument) {
						Object afterObject = changeAfterMapEpm.get(BomWfUtil.getObjectnumber((Persistable) beforeObject));
						EPMDocument beforeEPMDocument = (EPMDocument) beforeObject;
						EPMDocument afterEPMDocument = null;
						if (afterObject != null)
							afterEPMDocument = (EPMDocument) afterObject;

						bean.setAffectObjectName(beforeEPMDocument.getName());
						bean.setAffectObjectNumber(beforeEPMDocument.getNumber());
						if (beforeEPMDocument.getDocType().toString().equals("CADDRAWING")) {
							bean.setAffectObjectType("2D图纸");
						} else {
							bean.setAffectObjectType("3D模型");
						}
						bean.setAffectObjectVersion(beforeEPMDocument.getVersionIdentifier().getValue() + "." + beforeEPMDocument.getIterationIdentifier().getValue());
						if (afterEPMDocument != null) {
							Timestamp releasedDate = getReleasedDate(afterEPMDocument, PartState.RELEASED);
							if (!checkDate(bean, releasedDate, this.releasedDateFrom, this.releasedDateTo))// 发布时间不在
																											// 查询条件之内
								continue;
							else if (releasedDate != null)
								bean.setAffectObjectReleaseDate(releasedDate.toString().split(" ")[0]);
							bean.setAffectObjectVersionAfter(afterEPMDocument.getVersionIdentifier().getValue() + "." + afterEPMDocument.getIterationIdentifier().getValue());
						}
						beanList.add(bean);
					}
				}
			}

		}

		HSSFWorkbook workbook = new HSSFWorkbook();
		Sheet sheet = workbook.createSheet("ChangeReport");
		Row pRow = sheet.createRow(0);
		// title
		String pTitle[] = { "DCN/ECN Number", "DCN/ECN Name", "ECN/DCN 创建者", "ECN/DCN 创建时间", "ECN批准日期", "DCN/ECN预计生效日期", "ECA/DCA 编号", "ECA/DCA 状态", "ECA/DCA 工作责任人", "DCA/ECA数据是否全部发布", "受影响对象类型", "受影响对象编号", "受影响对象名称", "发布时间", "受影响对象当前版本", "受影响对象变更后版本", "变更情况", "下层主料PN（-下层替代料PN）", "下层主料名称", "下层主/替代料用量_变更前", "下层主/替代料用量_变更后" };
		for (int tCount = 0; tCount < pTitle.length; tCount++) {
			Cell tCell = pRow.createCell(tCount);
			tCell.setCellValue(pTitle[tCount]);
		}
		int p = 0;
		log.debug("beanList:" + beanList.size());
		for (ChangeReportBean bean : beanList) {
			Row row = sheet.createRow(++p);
			int c = 0;
			Cell cell = row.createCell(c++);
			cell.setCellValue(bean.getEcnNumber());

			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcnName());

			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcnCreate());

			cell = row.createCell(c++);
			cell.setCellValue(bean.getCreateDate());

			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcnApproveDate());

			cell = row.createCell(c++);
			cell.setCellValue(bean.getNeedDate());

			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcaNumber());

			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcaState());

			cell = row.createCell(c++);
			cell.setCellValue(bean.getEcaWorkOwner());

			cell = row.createCell(c++);
			cell.setCellValue(bean.getIsRESOLVED());

			cell = row.createCell(c++);
			cell.setCellValue(bean.getAffectObjectType());

			cell = row.createCell(c++);
			cell.setCellValue(bean.getAffectObjectNumber());

			cell = row.createCell(c++);
			cell.setCellValue(bean.getAffectObjectName());

			cell = row.createCell(c++);
			cell.setCellValue(bean.getAffectObjectReleaseDate());

			cell = row.createCell(c++);
			cell.setCellValue(bean.getAffectObjectVersion());

			cell = row.createCell(c++);
			cell.setCellValue(bean.getAffectObjectVersionAfter());

			cell = row.createCell(c++);
			cell.setCellValue(bean.getBomCompareResultMsg());

			cell = row.createCell(c++);
			cell.setCellValue(bean.getBomCompareResultObjectNumber());

			cell = row.createCell(c++);
			cell.setCellValue(bean.getBomCompareResultObjectName());

			cell = row.createCell(c++);
			cell.setCellValue(bean.getBomCompareResultObjectDosageOld());

			cell = row.createCell(c++);
			cell.setCellValue(bean.getBomCompareResultObjectDosageNew());
		}

		FileOutputStream out = new FileOutputStream(filePath);
		workbook.write(out);
		out.flush();
		out.close();
		return filePath;
	}

	public static PromotionNotice getPromotionNotice(Persistable object) throws WTException {
		QueryResult prcounts = BomWfUtil.isHavePromoteRequest(object);
		while (prcounts.hasMoreElements()) {
			Object[] object2 = (Object[]) prcounts.nextElement();
			PromotionNotice promotion = (PromotionNotice) object2[0];
			QueryResult processResult = new QueryResult();

			TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(promotion);
			String type = ti.getTypename();
			if (type.endsWith(TypeName.bomPromotion))
				processResult = NmWorkflowHelper.service.getAssociatedProcesses(promotion, null, null);

			log.debug("getPromotionNotice processResult size====" + processResult.size());
			if (processResult.hasMoreElements())
				return promotion;
		}
		return null;
	}

	/**
	 * Bom比较
	 * 
	 * @param beanList
	 * @param bean
	 * @param beforePart
	 * @param afterPart
	 * @throws WTException
	 */
	public static void compareBom(List<ChangeReportBean> beanList, ChangeReportBean shareBean, WTPart beforePart, WTPart afterPart) throws WTException {
		beanList.add(shareBean);
		QueryResult beforeBom = WTPartHelper.service.getUsesWTPartMasters(beforePart);
		Map<String, String> beforeMap = new HashMap<String, String>();
		Map<String, String> afterMap = new HashMap<String, String>();
		while (beforeBom.hasMoreElements()) {
			WTPartUsageLink link = (WTPartUsageLink) beforeBom.nextElement();

			Object magnificationObj = IBAUtil.getIBAValue(link, "CATL_MAGNIFICATION");// 放大倍数
			if (magnificationObj != null) {
				beforeMap.put(link.getUses().getNumber(), link.getUses().getName() + "~" + String.valueOf(link.getQuantity().getAmount()) + "~" + ((Long) magnificationObj).intValue());
			} else {
				beforeMap.put(link.getUses().getNumber(), link.getUses().getName() + "~" + String.valueOf(link.getQuantity().getAmount()));
			}
			WTCollection collection = WTPartHelper.service.getSubstituteLinks(link);
			if (!collection.isEmpty()) {
				Iterator itr = collection.iterator();
				while (itr.hasNext()) {
					ObjectReference objReference = (ObjectReference) itr.next();
					WTPartSubstituteLink subLink = (WTPartSubstituteLink) objReference.getObject();
					String subLinkUsage;
					if (subLink.getQuantity() != null) {
						Double amount = subLink.getQuantity().getAmount();
						if (amount != null && amount != 0) {
							subLinkUsage = amount + "";
						} else {
							subLinkUsage = link.getQuantity().getAmount() + "";
						}
					} else {
						subLinkUsage = link.getQuantity().getAmount() + "";
					}
					magnificationObj = IBAUtil.getIBAValue(subLink, "CATL_MAGNIFICATION");// 放大倍数
					if (magnificationObj != null) {
						beforeMap.put(link.getUses().getNumber() + "~" + subLink.getSubstitutes().getNumber(), subLink.getSubstitutes().getName() + "~" + subLinkUsage + "~" + ((Long) magnificationObj).intValue());
					} else {
						beforeMap.put(link.getUses().getNumber() + "~" + subLink.getSubstitutes().getNumber(), subLink.getSubstitutes().getName() + "~" + subLinkUsage);
					}
				}
			}
		}
		QueryResult afterBom = WTPartHelper.service.getUsesWTPartMasters(afterPart);
		while (afterBom.hasMoreElements()) {
			WTPartUsageLink link = (WTPartUsageLink) afterBom.nextElement();
			Object magnificationObj = IBAUtil.getIBAValue(link, "CATL_MAGNIFICATION");// 放大倍数
			if (magnificationObj != null) {
				afterMap.put(link.getUses().getNumber(), link.getUses().getName() + "~" + String.valueOf(link.getQuantity().getAmount()) + "~" + ((Long) magnificationObj).intValue());
			} else {
				afterMap.put(link.getUses().getNumber(), link.getUses().getName() + "~" + String.valueOf(link.getQuantity().getAmount()));
			}
			WTCollection collection = WTPartHelper.service.getSubstituteLinks(link);
			if (!collection.isEmpty()) {
				Iterator itr = collection.iterator();
				while (itr.hasNext()) {
					ObjectReference objReference = (ObjectReference) itr.next();
					WTPartSubstituteLink subLink = (WTPartSubstituteLink) objReference.getObject();
					String subLinkUsage;
					if (subLink.getQuantity() != null) {
						Double amount = subLink.getQuantity().getAmount();
						if (amount != null && amount != 0) {
							subLinkUsage = amount + "";
						} else {
							subLinkUsage = link.getQuantity().getAmount() + "";
						}
					} else {
						subLinkUsage = link.getQuantity().getAmount() + "";
					}
					magnificationObj = IBAUtil.getIBAValue(subLink, "CATL_MAGNIFICATION");// 放大倍数
					if (magnificationObj != null) {
						afterMap.put(link.getUses().getNumber() + "~" + subLink.getSubstitutes().getNumber(), subLink.getSubstitutes().getName() + "~" + subLinkUsage + "~" + ((Long) magnificationObj).intValue());
					} else {
						afterMap.put(link.getUses().getNumber() + "~" + subLink.getSubstitutes().getNumber(), subLink.getSubstitutes().getName() + "~" + subLinkUsage);
					}
				}
			}
		}
		for (String key : beforeMap.keySet()) {
			ChangeReportBean bean = (ChangeReportBean) shareBean.clone();
			bean.setAffectObjectType("BOM");
			bean.setBomCompareResultObjectNumber(key);
			String[] oldValue = beforeMap.get(key).split("~");
			bean.setBomCompareResultObjectName(oldValue[0]);
			bean.setBomCompareResultObjectDosageOld(oldValue[1]);
			if (afterMap.get(key) == null) {
				if (key.contains("~")) {
					bean.setBomCompareResultMsg("替代件移除");
					beanList.add((ChangeReportBean) bean.clone());
				} else {
					bean.setBomCompareResultMsg("子件移除");
					beanList.add((ChangeReportBean) bean.clone());
				}
			} else {
				String[] newValue = afterMap.get(key).split("~");
				bean.setBomCompareResultObjectDosageNew(newValue[1]);
				if (key.contains("~")) {
					if (Double.valueOf(oldValue[1]) > Double.valueOf(newValue[1])) {
						bean.setBomCompareResultMsg("替代件用量减少");
						beanList.add((ChangeReportBean) bean.clone());
					} else if (Double.valueOf(oldValue[1]) < Double.valueOf(newValue[1])) {
						bean.setBomCompareResultMsg("替代件用量增加");
						beanList.add((ChangeReportBean) bean.clone());
					}
					if(oldValue.length == 3 && newValue.length == 2){
						bean.setBomCompareResultMsg("替代件放大倍数减少");
						bean.setBomCompareResultObjectDosageOld(oldValue[2]);
						beanList.add((ChangeReportBean) bean.clone());
					}else if(oldValue.length == 2 && newValue.length == 3){
						bean.setBomCompareResultMsg("替代件放大倍数增加");
						bean.setBomCompareResultObjectDosageNew(newValue[2]);
						beanList.add((ChangeReportBean) bean.clone());
					}else if(oldValue.length == 3 && newValue.length == 3){
						int oldMagnification = Integer.valueOf(oldValue[2]);
						int newMagnification = Integer.valueOf(newValue[2]);
						bean.setBomCompareResultObjectDosageOld(oldValue[2]);
						bean.setBomCompareResultObjectDosageNew(newValue[2]);
						if(oldMagnification < newMagnification){
							bean.setBomCompareResultMsg("替代件放大倍数减少");
							beanList.add((ChangeReportBean) bean.clone());
						}else if(oldMagnification > newMagnification){
							bean.setBomCompareResultMsg("替代件放大倍数增加");
							beanList.add((ChangeReportBean) bean.clone());
						}
					}
				} else {
					if (Double.valueOf(oldValue[1]) > Double.valueOf(newValue[1])) {
						bean.setBomCompareResultMsg("子件用量减少");
						beanList.add((ChangeReportBean) bean.clone());
					} else if (Double.valueOf(oldValue[1]) < Double.valueOf(newValue[1])) {
						bean.setBomCompareResultMsg("子件用量增加");
						beanList.add((ChangeReportBean) bean.clone());
					}
					if(oldValue.length == 3 && newValue.length == 2){
						bean.setBomCompareResultMsg("子件放大倍数减少");
						bean.setBomCompareResultObjectDosageOld(oldValue[2]);
						beanList.add((ChangeReportBean) bean.clone());
					}else if(oldValue.length == 2 && newValue.length == 3){
						bean.setBomCompareResultMsg("子件放大倍数增加");
						bean.setBomCompareResultObjectDosageNew(newValue[2]);
						beanList.add((ChangeReportBean) bean.clone());
					}else if(oldValue.length == 3 && newValue.length == 3){
						int oldMagnification = Integer.valueOf(oldValue[2]);
						int newMagnification = Integer.valueOf(newValue[2]);
						bean.setBomCompareResultObjectDosageOld(oldValue[2]);
						bean.setBomCompareResultObjectDosageNew(newValue[2]);
						if(oldMagnification < newMagnification){
							bean.setBomCompareResultMsg("子件放大倍数减少");
							beanList.add((ChangeReportBean) bean.clone());
						}else if(oldMagnification > newMagnification){
							bean.setBomCompareResultMsg("子件放大倍数增加");
							beanList.add((ChangeReportBean) bean.clone());
						}
					}
				}
			}
		}
		for (String key : afterMap.keySet()) {
			if (beforeMap.get(key) == null) {
				ChangeReportBean bean = (ChangeReportBean) shareBean.clone();
				bean.setAffectObjectType("BOM");
				bean.setBomCompareResultObjectNumber(key);
				String[] newValue = afterMap.get(key).split("~");
				bean.setBomCompareResultObjectName(newValue[0]);
				bean.setBomCompareResultObjectDosageNew(newValue[1]);
				if (key.contains("~")) {
					bean.setBomCompareResultMsg("替代件新增");
					beanList.add((ChangeReportBean) bean.clone());
				} else {
					bean.setBomCompareResultMsg("子件新增");
					beanList.add((ChangeReportBean) bean.clone());
				}
			}
		}
	}

	private boolean isNullOrBlank(String param) {
		return (param == null || param.trim().equals("") || param.trim().equals("null")) ? true : false;
	}

	private String replace(String mainString, String oldString, String newString) {
		if (mainString == null) {
			return null;
		}
		int i = mainString.lastIndexOf(oldString);
		if (i < 0) {
			return mainString;
		}
		StringBuffer mainSb = new StringBuffer(mainString);
		while (i >= 0) {
			mainSb.replace(i, i + oldString.length(), newString);
			i = mainSb.toString().lastIndexOf(oldString, i - 1);
		}
		return mainSb.toString();
	}

	public Timestamp getReleasedDate(LifeCycleManaged obj, String state) throws LifeCycleException, WTException {
		Enumeration histories = (Enumeration) LifeCycleHelper.service.getHistory(obj);
		while (histories.hasMoreElements()) {
			LifeCycleHistory lcHistory = (LifeCycleHistory) histories.nextElement();
			if (lcHistory.getState().toString().equals(state) && lcHistory.getAction().equals("Enter_Phase")) {
				Timestamp changeTime = lcHistory.getCreateTimestamp();
				return new Timestamp(changeTime.getTime() + 28800000);
			}
		}
		return null;
	}

	private ArrayList<WTChangeOrder2> queryDCN() throws WTException, WTPropertyVetoException {
		ArrayList<WTChangeOrder2> result = new ArrayList<WTChangeOrder2>();

		QuerySpec qs = new QuerySpec();
		int index = qs.appendClassList(WTChangeOrder2.class, true);
		qs.setAdvancedQueryEnabled(true);

		qs.appendWhere(new SearchCondition(WTChangeOrder2.class, WTChangeOrder2.LIFE_CYCLE_STATE, SearchCondition.NOT_EQUAL, ChangeState.CANCELLED), index);
		String status = this.status;
		if (!isNullOrBlank(status) && !status.equals("*")) {
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTChangeOrder2.class, WTChangeOrder2.LIFE_CYCLE_STATE, SearchCondition.EQUAL, status), index);
		}
		String number = this.ecrNumber;
		if (!isNullOrBlank(number)) {
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTChangeOrder2.class, WTChangeOrder2.NUMBER, SearchCondition.LIKE, "%"+number.trim().toUpperCase()+"%"), index);
		}
		String createTimeStartStr = this.createDateFrom;
		if (!isNullOrBlank(createTimeStartStr)) {
			Timestamp timestampStart = new Timestamp(new Date().getTime());
			DateHelper datehelper = new DateHelper(createTimeStartStr, "day", SessionHelper.getLocale());
			timestampStart = new Timestamp(datehelper.getDate().getTime());
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTChangeOrder2.class, WTChangeOrder2.CREATE_TIMESTAMP, SearchCondition.GREATER_THAN_OR_EQUAL, timestampStart), index);
		}

		String createTimeEndStr = this.createDateTo;
		if (!isNullOrBlank(createTimeEndStr)) {
			Timestamp timestampEnd = new Timestamp(new Date().getTime());
			DateHelper datehelper = new DateHelper(createTimeEndStr, "day", SessionHelper.getLocale());
			timestampEnd = new Timestamp(datehelper.getDate().getTime());
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTChangeOrder2.class, WTChangeOrder2.CREATE_TIMESTAMP, SearchCondition.LESS_THAN_OR_EQUAL, timestampEnd), index);
		}

		log.debug(qs);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		while (qr.hasMoreElements()) {
			Persistable[] persist = (Persistable[]) qr.nextElement();
			WTChangeOrder2 dcn = (WTChangeOrder2) persist[0];
			result.add(dcn);
		}

		return result;
	}

	public boolean checkDate(ChangeReportBean bean, Timestamp compareDate, String dateFrom, String dateEnd) throws ParseException {
		if ((!isNullOrBlank(dateFrom) || !isNullOrBlank(dateEnd)) && compareDate == null) {
			return false;
		}
		if (!isNullOrBlank(dateFrom) && compareDate != null) {
			if (compareDate.before(sdf.parse(dateFrom + " 00:00:00")))
				return false;
		}
		if (!isNullOrBlank(dateEnd) && compareDate != null) {
			if (compareDate.after(sdf.parse(dateEnd + " 23:59:59")))
				return false;
		}
		return true;
	}
}
