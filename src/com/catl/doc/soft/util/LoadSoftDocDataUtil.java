package com.catl.doc.soft.util;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;

import com.catl.common.constant.Constant;
import com.catl.common.constant.ContainerName;
import com.catl.common.constant.DocState;
import com.catl.common.constant.PartState;
import com.catl.common.constant.TypeName;
import com.catl.common.toolbox.data.UpdateAllPartSpec;
import com.catl.common.util.GenericUtil;
import com.catl.common.util.PartUtil;
import com.catl.integration.PIService;
import com.catl.integration.PartInfo;
import com.catl.line.constant.ConstantLine;
import com.catl.line.exception.LineException;
import com.catl.line.helper.ExpressHelper;
import com.catl.line.util.CommonUtil;
import com.catl.line.util.NodeUtil;
import com.catl.loadData.IBAUtility;
import com.catl.loadData.StrUtils;
import com.catl.loadData.util.ExcelReader;
import com.catl.part.CatlPartNewNumber;
import com.catl.part.CreateCatlPartProcessor;
import com.catl.part.PartConstant;
import com.catl.part.PartLoadNameSourceUtil;
import com.catl.require.constant.ConstantRequire;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartHelper;
import wt.part.WTPartIDSeq;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.pom.Transaction;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.config.LatestConfigSpec;

public class LoadSoftDocDataUtil {
	
	public static PIService service = PIService.getInstance();

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String filePath = "E:\\datas.xlsx";
		readSoftDocData(filePath);
	}

	public static XSSFWorkbook readSoftDocData(String filePath) throws Exception {
		Transaction tx = new Transaction();
		tx.start();
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet();
		Row row = sheet.createRow(0);
		Cell docswCell = row.createCell(0);
		docswCell.setCellValue("文档编号");
		Cell pnswCell = row.createCell(1);
		pnswCell.setCellValue("软件PN");
		Cell parentPnCell = row.createCell(2);
		parentPnCell.setCellValue("父级PN");
		
		List<PartInfo> partinfos = new ArrayList<>();
		
		File file = new File(filePath);
		ExcelReader reader = new ExcelReader(file);
		try {
			reader.open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		reader.setSheetNum(0);
		int count = reader.getRowCount();
		//Map<String, JSONObject> docInfos = new HashMap<String, JSONObject>();
		for (int i = 1; i <= count; i++) {
			String rows[] = reader.readExcelLine(i);

			if (!(rows == null || rows[0].isEmpty() || rows[6].isEmpty())) {
				String docNum = rows[0].isEmpty() ? "" : rows[0];
				String parentNum = rows[6].isEmpty() ? "" : rows[6];
				String cls = rows[7].isEmpty() ? "" : rows[7];
				String sw_Version = rows[8].isEmpty() ? "" : rows[8];
				String productLineType = rows[9].isEmpty() ? "" : rows[9];
				String componentType = rows[10].isEmpty() ? "" : rows[10];
				String customerSoftVersion = rows[11].isEmpty() ? "" : rows[11];
				String riskRelease = rows[12].isEmpty() ? "" : rows[12];
				String softRiskPoint = rows[13].isEmpty() ? "" : rows[13];

				JSONObject json = new JSONObject();
				json.put("docNum", docNum);
				json.put("parentNum", parentNum);
				json.put(PartConstant.Software_Version, sw_Version);
				json.put(PartConstant.CATL_ProductlineType, productLineType);
				json.put(PartConstant.CATL_ComponentType, componentType);
				json.put(PartConstant.CATL_CustomerSoftwareVersion, customerSoftVersion);
				json.put(PartConstant.CATL_RiskRelease, riskRelease);
				json.put(PartConstant.CATL_SoftwareRiskPoint, softRiskPoint);
				json.put(PartConstant.IBA_CLS, cls);

				//docInfos.put(docNum, json);

				WTDocument doc = CommonUtil.getLatestWTDocByNumber(docNum);
				WTPart parentPart = CommonUtil.getLatestWTpartByNumber(parentNum);
				if (parentPart == null) {
					System.out.println("父级部件" + parentNum + "在系统中不存在！");
				} else {
					if (doc != null) {
						WTPart part = createPartByDoc(doc, json);

						WTDocument newDoc = CatlSoftDocUtil.createDocByDoc(doc, part);

						if (newDoc != null && part != null) {
							if (PartUtil.isSWPart(part)) {
								boolean enforced = SessionServerHelper.manager.setAccessEnforced(false);
								try {
									if (newDoc.getLifeCycleState().equals(State.toState(PartState.DESIGN))) {
										LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) newDoc,
												State.toState(PartState.RELEASED));
									}
								} catch (Exception e) {
									e.printStackTrace();
								} finally {
									SessionServerHelper.manager.setAccessEnforced(enforced);
								}
							}

							WTPartDescribeLink describelink = WTPartDescribeLink.newWTPartDescribeLink(part, newDoc);
							PersistenceServerHelper.manager.insert(describelink);
							
							WTPartUsageLink usagelink = WTPartUsageLink.newWTPartUsageLink(parentPart, part.getMaster());
							PersistenceServerHelper.manager.insert(usagelink);
							
							Row valueRow = sheet.createRow(i);
							Cell valueDocCell = valueRow.createCell(0);
							valueDocCell.setCellValue(newDoc.getNumber());
							Cell valuePnCell = valueRow.createCell(1);
							valuePnCell.setCellValue(part.getNumber());
							Cell valuePPnCell = valueRow.createCell(2);
							valuePPnCell.setCellValue(parentPart.getNumber());
							
							initPartInfo(partinfos, part, null, new ArrayList<Long>(), null);
						}
					} else {
						System.out.println("文档" + docNum + "在系统中不存在，请检查后重试！");
					}
				}
			}
		}
		
		
		tx.commit();
		tx = null;
		
		service.sendParts(partinfos, Constant.COMPANY);
		
		return workbook;
	}
	
	public static XSSFWorkbook readLineOffSoftDocData(String filePath,String softpackagePath) throws Exception {
		Transaction tx = new Transaction();
		tx.start();
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet();
		Row row = sheet.createRow(0);
		Cell docswCell = row.createCell(0);
		docswCell.setCellValue("文档编号");
		Cell pnswCell = row.createCell(1);
		pnswCell.setCellValue("软件PN");
		Cell parentPnCell = row.createCell(2);
		parentPnCell.setCellValue("父级PN");
		
		List<PartInfo> partinfos = new ArrayList<>();
		
		File file = new File(filePath);
		ExcelReader reader = new ExcelReader(file);
		try {
			reader.open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		reader.setSheetNum(0);
		int count = reader.getRowCount();
		Map<String, JSONObject> docInfos = new HashMap<String, JSONObject>();
		for (int i = 1; i <= count; i++) {
			String rows[] = reader.readExcelLine(i);

			if (!(rows == null || rows[0].isEmpty() || rows[6].isEmpty())) {
				String filename = rows[0].isEmpty() ? "" : rows[0];
				String docName = rows[1].isEmpty() ? "" : rows[1];
				String location = rows[2].isEmpty() ? "" : rows[2];
				String hw_Version = rows[3].isEmpty() ? "" : rows[3];
				String parentNum = rows[6].isEmpty() ? "" : rows[6];
				String cls = rows[7].isEmpty() ? "" : rows[7];
				String sw_Version = rows[8].isEmpty() ? "" : rows[8];
				String productLineType = rows[9].isEmpty() ? "" : rows[9];
				String componentType = rows[10].isEmpty() ? "" : rows[10];
				String customerSoftVersion = rows[11].isEmpty() ? "" : rows[11];
				String riskRelease = rows[12].isEmpty() ? "" : rows[12];
				String softRiskPoint = rows[13].isEmpty() ? "" : rows[13];
				String filepath = softpackagePath + File.separator + filename;

				JSONObject json = new JSONObject();
				json.put("filename", filename);
				json.put("docName", docName);
				json.put("location", location);
				json.put(PartConstant.Hardware_Version, hw_Version);
				json.put("parentNum", parentNum);
				json.put(PartConstant.Software_Version, sw_Version);
				json.put(PartConstant.CATL_ProductlineType, productLineType);
				json.put(PartConstant.CATL_ComponentType, componentType);
				json.put(PartConstant.CATL_CustomerSoftwareVersion, customerSoftVersion);
				json.put(PartConstant.CATL_RiskRelease, riskRelease);
				json.put(PartConstant.CATL_SoftwareRiskPoint, softRiskPoint);
				json.put(PartConstant.IBA_CLS, cls);

				docInfos.put(filename, json);

				//WTDocument doc = CommonUtil.getLatestWTDocByNumber(docNum);
				WTPart parentPart = CommonUtil.getLatestWTpartByNumber(parentNum);
				if (parentPart == null) {
					System.out.println("父级部件" + parentNum + "在系统中不存在！");
				} else {
					
						WTPart part = createPartByInfo(parentPart, json);

						WTDocument newDoc = CatlSoftDocUtil.createDocByPart(part,filename,filepath);

						if (newDoc != null && part != null) {
							if (PartUtil.isSWPart(part)) {
								boolean enforced = SessionServerHelper.manager.setAccessEnforced(false);
								try {
									if (newDoc.getLifeCycleState().equals(State.toState(PartState.DESIGN))) {
										LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) newDoc,
												State.toState(PartState.RELEASED));
									}
								} catch (Exception e) {
									e.printStackTrace();
								} finally {
									SessionServerHelper.manager.setAccessEnforced(enforced);
								}
							}

							WTPartDescribeLink describelink = WTPartDescribeLink.newWTPartDescribeLink(part, newDoc);
							PersistenceServerHelper.manager.insert(describelink);
							
							WTPartUsageLink usagelink = WTPartUsageLink.newWTPartUsageLink(parentPart, part.getMaster());
							PersistenceServerHelper.manager.insert(usagelink);
							
							Row valueRow = sheet.createRow(i);
							Cell valueDocCell = valueRow.createCell(0);
							valueDocCell.setCellValue(newDoc.getNumber());
							Cell valuePnCell = valueRow.createCell(1);
							valuePnCell.setCellValue(part.getNumber());
							Cell valuePPnCell = valueRow.createCell(2);
							valuePPnCell.setCellValue(parentPart.getNumber());
							
							initPartInfo(partinfos, part, null, new ArrayList<Long>(), null);
						}
					
				}
			}
		}
		tx.commit();
		tx = null;
		
		service.sendParts(partinfos, Constant.COMPANY);
		return workbook;
	}

	public static WTPart createPartByDoc(WTDocument doc, JSONObject json) throws Exception {
		Map<String, String> clsnamesource = PartLoadNameSourceUtil.getPartClsNameSource();
		String docFolderPath = "/" + doc.getFolderPath().split("/")[1];
		docFolderPath = docFolderPath + "/" + doc.getFolderPath().split("/")[2] + "/零部件";
		Folder folder = CatlSoftDocUtil.getFolder(docFolderPath, doc.getContainer());
		String cls = json.getString(PartConstant.IBA_CLS);
		String name = "";
		String source = "";
		String unit = "";
		String oldnum = "";// json.getString("oldnum");
		String description = "";// json.getString("description");
		String uiforcecreate = "";// json.getString("forcecreate");
		String feature = "";// json.getString("feature");
		String iscustomer = "否";// json.getString("iscustomer");
		String platform = "";// json.getString("platform");
		String openMould = "";

		String sw_Version = json.getString(PartConstant.Software_Version);
		String productLineType = json.getString(PartConstant.CATL_ProductlineType);
		String componentType = json.getString(PartConstant.CATL_ComponentType);
		String customerSoftVersion = json.getString(PartConstant.CATL_CustomerSoftwareVersion);
		String riskRelease = json.getString(PartConstant.CATL_RiskRelease);
		String softRiskPoint = json.getString(PartConstant.CATL_SoftwareRiskPoint);
		String hw_Version = (String) GenericUtil.getObjectAttributeValue(doc, PartConstant.Hardware_Version);

		// String namesource = clsnamesource.get(cls);
		if (clsnamesource.containsKey(cls)) {
			String sourcename = clsnamesource.get(cls);
			System.out.println("Source and name:\t" + sourcename);
			String[] nsarray = sourcename.split("qqqq;;;;");
			if (nsarray.length == 4) {
				name = nsarray[0];
				source = nsarray[1].split(",")[1];
				unit = nsarray[2];
				openMould = nsarray[3];
				System.out.println("Source:\t" + unit);
			}
		}

		String number = PersistenceHelper.manager.getNextSequence(WTPartIDSeq.class);
		WTPart part = CommonUtil.createPart(number, doc.getName(), TypeName.CATLPart, source, unit, oldnum, uiforcecreate,
				description, folder.getIdentity(), doc.getContainer().toString());

		WTPartMaster partmaster = part.getMaster();
		IBAUtility ibafeature = new IBAUtility(part);
		IBAUtility ibamaster = new IBAUtility(partmaster);
		if (StringUtils.isNotBlank(feature)) {
			ibafeature.setIBAValue(PartConstant.CATL_Feature, feature);
		}
		if (StringUtils.isNotBlank(iscustomer)) {
			ibafeature.setIBAValue(PartConstant.Is_Customer, iscustomer);
		}
		if (StringUtils.isNotBlank(platform)) {
			ibamaster.setIBAValue(ConstantRequire.iba_CATL_Platform, platform);
		}

		if (StringUtils.isNotBlank(openMould)) {
			ibafeature.setIBAValue(PartConstant.OpenMould, openMould);
		}

		if (StringUtils.isNotBlank(sw_Version)) {
			ibafeature.setIBAValue(PartConstant.Software_Version, sw_Version);
		}

		if (StringUtils.isNotBlank(hw_Version)) {
			ibafeature.setIBAValue(PartConstant.Hardware_Version, hw_Version);
		}

		if (StringUtils.isNotBlank(productLineType)) {
			ibafeature.setIBAValue(PartConstant.CATL_ProductlineType, productLineType);
		}

		if (StringUtils.isNotBlank(componentType)) {
			ibafeature.setIBAValue(PartConstant.CATL_ComponentType, componentType);
		}

		if (StringUtils.isNotBlank(customerSoftVersion)) {
			ibafeature.setIBAValue(PartConstant.CATL_CustomerSoftwareVersion, customerSoftVersion);
		}

		if (StringUtils.isNotBlank(riskRelease)) {
			ibafeature.setIBAValue(PartConstant.CATL_RiskRelease, riskRelease);
		}

		if (StringUtils.isNotBlank(softRiskPoint)) {
			ibafeature.setIBAValue(PartConstant.CATL_SoftwareRiskPoint, softRiskPoint);
		}

		part = (WTPart) ibafeature.updateAttributeContainer(part);
		ibafeature.updateIBAHolder(part);

		partmaster = (WTPartMaster) ibamaster.updateAttributeContainer(partmaster);
		ibafeature.updateIBAHolder(partmaster);

		if (!StringUtils.isEmpty(cls)) {
			LWCStructEnumAttTemplate lwc = null;
			// String lwcname = null;
			// 获取分类名称
			IBAUtility iba = new IBAUtility(part);

			iba.setIBAValue(PartConstant.IBA_CLS, cls);

			iba.updateAttributeContainer(part);
			iba.updateIBAHolder(part);

			lwc = NodeUtil.getClfNodeByName(cls);

			CreateCatlPartProcessor.updateSource(part);
			CreateCatlPartProcessor.renamePart(part);

			String newNum = CatlPartNewNumber.createPartNewnumber(lwc, part.getContainerName());
			WTPartMaster partMaster = (WTPartMaster) part.getMaster();
			try {
				WTPartHelper.service.changeWTPartMasterIdentity(partMaster, part.getName(), newNum,
						part.getOrganization());
			} catch (WTPropertyVetoException e) {
				e.printStackTrace();
			}

			String specification = UpdateAllPartSpec.getAllAttr(part);
			System.out.println("11111\t"+specification);
			IBAUtility ibaspec = new IBAUtility(part);

			ibaspec.setIBAValue("specification", specification);

			ibaspec.updateAttributeContainer(part);
			ibaspec.updateIBAHolder(part);

			if (PartUtil.isSWPart(part)) {
				boolean enforced = SessionServerHelper.manager.setAccessEnforced(false);
				try {
					if (part.getLifeCycleState().equals(State.toState(PartState.WRITING))) {
						LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) part,
								State.toState(PartState.RELEASED));
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					SessionServerHelper.manager.setAccessEnforced(enforced);
				}
			}

		} else {
			throw new LineException("获取不到分类");
		}
		return part;
	}
	
	public static WTPart createPartByInfo(WTPart parentpart, JSONObject json) throws Exception {
		Map<String, String> clsnamesource = PartLoadNameSourceUtil.getPartClsNameSource();
		String docFolderPath = "/" + parentpart.getFolderPath().split("/")[1];
		docFolderPath = docFolderPath + "/" + parentpart.getFolderPath().split("/")[2] + "/零部件";
		Folder folder = CatlSoftDocUtil.getFolder(docFolderPath, parentpart.getContainer());
		String cls = json.getString(PartConstant.IBA_CLS);
		String name = json.getString("docName");
		String source = "";
		String unit = "";
		String oldnum = "";// json.getString("oldnum");
		String description = "";// json.getString("description");
		String uiforcecreate = "";// json.getString("forcecreate");
		String feature = "";// json.getString("feature");
		String iscustomer = "否";// json.getString("iscustomer");
		String platform = "";// json.getString("platform");
		String openMould = "";

		String sw_Version = json.getString(PartConstant.Software_Version);
		String productLineType = json.getString(PartConstant.CATL_ProductlineType);
		String componentType = json.getString(PartConstant.CATL_ComponentType);
		String customerSoftVersion = json.getString(PartConstant.CATL_CustomerSoftwareVersion);
		String riskRelease = json.getString(PartConstant.CATL_RiskRelease);
		String softRiskPoint = json.getString(PartConstant.CATL_SoftwareRiskPoint);
		String hw_Version = json.getString(PartConstant.Hardware_Version);
		String location = json.getString("location");

		// String namesource = clsnamesource.get(cls);
		if (clsnamesource.containsKey(cls)) {
			String sourcename = clsnamesource.get(cls);
			System.out.println("Source and name:\t" + sourcename);
			String[] nsarray = sourcename.split("qqqq;;;;");
			if (nsarray.length == 4) {
				//name = nsarray[0];
				source = nsarray[1].split(",")[1];
				unit = nsarray[2];
				openMould = nsarray[3];
				System.out.println("Source:\t" + unit);
			}
		}

		String number = PersistenceHelper.manager.getNextSequence(WTPartIDSeq.class);
		WTPart part = CommonUtil.createPart(number, name, TypeName.CATLPart, source, unit, oldnum, uiforcecreate,
				description, folder.getIdentity(), parentpart.getContainer().toString());

		WTPartMaster partmaster = part.getMaster();
		IBAUtility ibafeature = new IBAUtility(part);
		IBAUtility ibamaster = new IBAUtility(partmaster);
		if (StringUtils.isNotBlank(feature)) {
			ibafeature.setIBAValue(PartConstant.CATL_Feature, feature);
		}
		if (StringUtils.isNotBlank(iscustomer)) {
			ibafeature.setIBAValue(PartConstant.Is_Customer, iscustomer);
		}
		if (StringUtils.isNotBlank(platform)) {
			ibamaster.setIBAValue(ConstantRequire.iba_CATL_Platform, platform);
		}

		if (StringUtils.isNotBlank(openMould)) {
			ibafeature.setIBAValue(PartConstant.OpenMould, openMould);
		}

		if (StringUtils.isNotBlank(sw_Version)) {
			ibafeature.setIBAValue(PartConstant.Software_Version, sw_Version);
		}

		if (StringUtils.isNotBlank(hw_Version)) {
			ibafeature.setIBAValue(PartConstant.Hardware_Version, hw_Version);
		}

		if (StringUtils.isNotBlank(productLineType)) {
			ibafeature.setIBAValue(PartConstant.CATL_ProductlineType, productLineType);
		}

		if (StringUtils.isNotBlank(componentType)) {
			ibafeature.setIBAValue(PartConstant.CATL_ComponentType, componentType);
		}

		if (StringUtils.isNotBlank(customerSoftVersion)) {
			ibafeature.setIBAValue(PartConstant.CATL_CustomerSoftwareVersion, customerSoftVersion);
		}

		if (StringUtils.isNotBlank(riskRelease)) {
			ibafeature.setIBAValue(PartConstant.CATL_RiskRelease, riskRelease);
		}

		if (StringUtils.isNotBlank(softRiskPoint)) {
			ibafeature.setIBAValue(PartConstant.CATL_SoftwareRiskPoint, softRiskPoint);
		}

		part = (WTPart) ibafeature.updateAttributeContainer(part);
		ibafeature.updateIBAHolder(part);

		partmaster = (WTPartMaster) ibamaster.updateAttributeContainer(partmaster);
		ibafeature.updateIBAHolder(partmaster);

		if (!StringUtils.isEmpty(cls)) {
			LWCStructEnumAttTemplate lwc = null;
			// String lwcname = null;
			// 获取分类名称
			IBAUtility iba = new IBAUtility(part);

			iba.setIBAValue(PartConstant.IBA_CLS, cls);

			iba.updateAttributeContainer(part);
			iba.updateIBAHolder(part);

			lwc = NodeUtil.getClfNodeByName(cls);

			CreateCatlPartProcessor.updateSource(part);
			CreateCatlPartProcessor.renamePart(part);

			String newNum = CatlPartNewNumber.createPartNewnumber(lwc, part.getContainerName());
			WTPartMaster partMaster = (WTPartMaster) part.getMaster();
			try {
				WTPartHelper.service.changeWTPartMasterIdentity(partMaster, part.getName(), newNum,
						part.getOrganization());
			} catch (WTPropertyVetoException e) {
				e.printStackTrace();
			}

			String specification = UpdateAllPartSpec.getAllAttr(part);
			System.out.println("11111\t"+specification);
			IBAUtility ibaspec = new IBAUtility(part);

			ibaspec.setIBAValue("specification", specification);

			ibaspec.updateAttributeContainer(part);
			ibaspec.updateIBAHolder(part);

			if (PartUtil.isSWPart(part)) {
				boolean enforced = SessionServerHelper.manager.setAccessEnforced(false);
				try {
					if (part.getLifeCycleState().equals(State.toState(PartState.WRITING))) {
						LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) part,
								State.toState(PartState.RELEASED));
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					SessionServerHelper.manager.setAccessEnforced(enforced);
				}
			}

		} else {
			throw new LineException("获取不到分类");
		}
		return part;
	}

	
	/**
	 * 组装part数据
	 * 
	 * @param list
	 * @param part
	 * @param ecnNumber
	 * @throws ParseException
	 * @throws WTException
	 */
	private static void initPartInfo(List<PartInfo> list, WTPart part, String ecnNumber, List<Long> targetlists, String oid) throws ParseException, WTException, Exception {

		PartInfo partInfo = new PartInfo();

		partInfo.setOid(oid);
		partInfo.setVersionBig(part.getVersionIdentifier().getValue());
		partInfo.setVersionSmall(part.getIterationIdentifier().getValue());
		partInfo.setPartName(part.getName());
		partInfo.setPartNumber(part.getNumber());
		partInfo.setCreateDate(part.getCreateTimestamp());
		if (part.getCreatorName() != null && part.getCreatorName().equalsIgnoreCase("dms")) {
			partInfo.setCreator(Constant.dmsConstrastEmpno);
		} else {
			partInfo.setCreator(part.getCreatorName());
		}

		String unit = part.getDefaultUnit().toString().toUpperCase();
		if (!StrUtils.isEmpty(unit) && unit.equals("EA")) {
			unit = "PCS";
		}
		partInfo.setSource(changeSource(part.getSource().toString()));

		if (part.getContainerName().equals(ContainerName.BATTERY_LIBRARY_NAME)) {
			if (unit.equals("PCS")) {
				unit = "EA";
			}
			partInfo.setMaterialGroup(null);
		} else {
			partInfo.setMaterialGroup(part.getNumber().substring(0, part.getNumber().lastIndexOf("-")));
		}
		partInfo.setDefaultUnit(unit);
		partInfo.setIteration(part.getVersionIdentifier().getValue());

		Object englishNameObj = GenericUtil.getObjectAttributeValue(part, "englishName");
		String englishName = englishNameObj == null ? "" : englishNameObj.toString();
		if (englishName.equals(Constant.DEFAULT)) {
			englishName = "";
		}
		Object specificationObj = GenericUtil.getObjectAttributeValue(part, "specification");
		String specification = specificationObj == null ? "" : specificationObj.toString();
		/*if (!specification.equals("")) {
			specification = getSpecificationString(specification);
		}*/
		Object oldPartNumberObj = GenericUtil.getObjectAttributeValue(part, "oldPartNumber");// 旧物料号
		String oldPartNumber = oldPartNumberObj == null ? "" : oldPartNumberObj.toString();
		if (oldPartNumber.equals(Constant.DEFAULT)) {
			oldPartNumber = "";
		}

		Object standardVoltageObj = GenericUtil.getObjectAttributeValue(part, "Nominal_Voltage");// 标称电压(V)
		String standardVoltage = standardVoltageObj == null ? "" : standardVoltageObj.toString();
		Object productEnergyObj = GenericUtil.getObjectAttributeValue(part, "Product_Energy");// 产品能量(kWh)
		String productEnergy = productEnergyObj == null ? "" : productEnergyObj.toString();
		/*
		 * Object modelObj =
		 * GenericUtil.getObjectAttributeValue(part,"Product_Model");//Model号
		 * String model = modelObj==null?"":modelObj.toString();
		 */
		Object cellVolumeObj = GenericUtil.getObjectAttributeValue(part, "Cell_Capacity");// 电芯容量(Ah)
		String cellVolume = cellVolumeObj == null ? "" : cellVolumeObj.toString();
		Object cellModeObj = GenericUtil.getObjectAttributeValue(part, "Cell_Mode");// 电芯类型
		String cellMode = cellModeObj == null ? "" : cellModeObj.toString();
		Object cellConnectionModeObj = GenericUtil.getObjectAttributeValue(part, "Cell_Connection_Mode");// 电芯并串联方式
		String cellConnectionMode = cellConnectionModeObj == null ? "" : cellConnectionModeObj.toString();
		Object moduleQuantityObj = GenericUtil.getObjectAttributeValue(part, "Module_Quantity");// 模组数量(PCS)
		String moduleQuantity = moduleQuantityObj == null ? "" : moduleQuantityObj.toString();
		Object hardwareVersionObj = GenericUtil.getObjectAttributeValue(part, "Hardware_Version");// HW版本
		String hardwareVersion = hardwareVersionObj == null ? "" : hardwareVersionObj.toString();
		Object softwareVersionObj = GenericUtil.getObjectAttributeValue(part, "Software_Version");// SW版本
		String softwareVersion = softwareVersionObj == null ? "" : softwareVersionObj.toString();
		Object parameterVersionObj = GenericUtil.getObjectAttributeValue(part, "Parameter_Version");// PAR版本
		String parameterVersion = parameterVersionObj == null ? "" : parameterVersionObj.toString();
		Object faeStatusObj = GenericUtil.getObjectAttributeValue(part, "CATL_FAEStatus");// PAR版本
		String mpn=(String) GenericUtil.getObjectAttributeValue(part, ConstantLine.var_parentPN);
		if(part.getNumber().endsWith("S")&&!part.getNumber().endsWith("-S")){
			mpn=part.getNumber().substring(0,part.getNumber().length()-1);
		}
		String l=ExpressHelper.getLineL(part)==null ? "" : String.valueOf(ExpressHelper.getLineL(part).doubleValue());
		
		String faeStatus = faeStatusObj == null ? "" : faeStatusObj.toString();
		partInfo.setStandardVoltage(standardVoltage);
		partInfo.setProductEnergy(productEnergy);
		// partInfo.setModel(model);
		partInfo.setCellVolume(cellVolume);
		partInfo.setCellMode(cellMode);
		partInfo.setCellConnectionMode(cellConnectionMode);
		partInfo.setModuleQuantity(moduleQuantity);
		partInfo.setHardwareVersion(hardwareVersion);
		partInfo.setSoftwareVersion(softwareVersion);
		partInfo.setParameterVersion(parameterVersion);
		partInfo.setFaeStatus(faeStatus);
		if(mpn!=null&&!mpn.equals("是")){//衍生PN发长度
			partInfo.setL(l);
			partInfo.setParentPN(mpn);
		}
		QueryResult cadresult = PartDocServiceCommand.getAssociatedCADDocuments(part);
		cadresult = new LatestConfigSpec().process(cadresult);
		while (cadresult.hasMoreElements()) {
			EPMDocument epmdoc = (EPMDocument) cadresult.nextElement();
			Boolean iscaddrawing = epmdoc.getDocType().toString().equals("CADDRAWING");
			if (iscaddrawing) {
				if (targetlists.contains(epmdoc.getPersistInfo().getObjectIdentifier().getId())) {
					partInfo.setDrawing(epmdoc.getNumber());
					partInfo.setDrawingVersion(epmdoc.getVersionIdentifier().getValue());
					break;
				} else {
					if (epmdoc.getState().toString().equals(DocState.RELEASED)) {
						partInfo.setDrawing(epmdoc.getNumber());
						partInfo.setDrawingVersion(epmdoc.getVersionIdentifier().getValue());
						break;
					}
				}
			}

		}
		if (partInfo.getDrawing() == null) {// 没有CAD图纸
			QueryResult docresult = PartDocServiceCommand.getAssociatedDescribeDocuments(part);
			docresult = new LatestConfigSpec().process(docresult);
			while (docresult.hasMoreElements()) {// 取第一个 PCBA、AUTO、GERBER文档
				WTDocument doc = (WTDocument) docresult.nextElement();
				TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(doc);
				String type = ti.getTypename();
				boolean isNeedset = type.contains(TypeName.doc_type_pcbaDrawing) || type.contains(TypeName.doc_type_autocadDrawing) || type.contains(TypeName.doc_type_gerberDoc);
				if (isNeedset) {
					if (targetlists.contains(doc.getPersistInfo().getObjectIdentifier().getId())) {
						//log.debug("select doc number:" + doc.getNumber() + ",doc version:" + doc.getVersionIdentifier().getValue());
						partInfo.setDrawing(doc.getNumber());
						partInfo.setDrawingVersion(doc.getVersionIdentifier().getValue());
						break;
					} else {
						if (doc.getState().toString().equals(DocState.RELEASED)) {
						//	log.debug("select doc number:" + doc.getNumber() + ",doc version:" + doc.getVersionIdentifier().getValue());
							partInfo.setDrawing(doc.getNumber());
							partInfo.setDrawingVersion(doc.getVersionIdentifier().getValue());
							break;
						}
					}
				}
			}
		}
		if (partInfo.getDrawing() == null) {// 没有PCBA、AUTO、GERBER文档
			QueryResult qr = ContentHelper.service.getContentsByRole(part, ContentRoleType.SECONDARY);
			while (qr.hasMoreElements()) {
				ApplicationData fileContent = (ApplicationData) qr.nextElement();
				String strFileName = URLDecoder.decode(fileContent.getFileName(), "utf-8");
				if (strFileName.toUpperCase().equals(part.getNumber() + ".PDF")) {
					//log.debug("select doc number:" + strFileName);
					partInfo.setDrawing(part.getNumber());
					partInfo.setDrawingVersion("A");
					break;
				}
			}
		}
		partInfo.setEnglishName(englishName);
		partInfo.setSpecification(specification);
		partInfo.setEcnNumber(ecnNumber);
		partInfo.setOldPartNumber(oldPartNumber);

		list.add(partInfo);

	}
	
	public static String changeSource(String source) {
		String flag = null;
		if (source == null)
			return "";
		if (source.equals("make")) {// 自制
			flag = "E";
		} else if (source.equals("buy")) {// 外购
			flag = "F";
		} else if (source.equals("makeBuy")) {// 外协
			flag = "W";
		} else if (source.equals("customer")) {// 客供
			flag = "C";
		} else if (source.equalsIgnoreCase("virtual")) {// 虚拟
			flag = "V";
		}
		return flag;
	}
}
