package com.catl.line.helper;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.doc.WTDocumentMasterIdentity;
import wt.fc.Identified;
import wt.fc.IdentityHelper;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.folder.FolderHelper;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartMasterIdentity;
import wt.part.WTPartUsageLink;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;

import com.catl.common.constant.AttributeName;
import com.catl.common.constant.PartState;
import com.catl.common.constant.TypeName;
import com.catl.common.util.CatlConstant;
import com.catl.common.util.DocUtil;
import com.catl.common.util.PartUtil;
import com.catl.common.util.PropertiesUtil;
import com.catl.line.constant.ConstantLine;
import com.catl.line.constant.GlobalData;
import com.catl.line.util.CommonUtil;
import com.catl.line.util.ExcelUtil;
import com.catl.line.util.PNUtil;
import com.catl.line.util.WTDocumentUtil;
import com.catl.line.util.excel2pdf.Excel2Pdf;
import com.catl.line.util.excel2pdf.ExcelObject;
import com.catl.loadData.IBAUtility;
import com.itextpdf.text.DocumentException;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.common.UpdateOperationIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;

public class BoxExplainHelper {
	public static String wt_home;
	static {
		try {
			wt_home = WTProperties.getLocalProperties().getProperty("wt.home", "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 加载装箱说明客户配置文件
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void loadBoxCustomerConfig() throws FileNotFoundException, IOException {
		File file = new File(wt_home + ConstantLine.box_customer_config_path);
		if (GlobalData.boxCustomerConfigTime < file.lastModified()) {
			GlobalData.boxCustomerConfig.clear();
			String[][] tagresult = ExcelUtil.getData(0, null, file, 0, false);
			String customerLast = null;
			List<String> temp = new ArrayList<String>();
			for (int i = 1; i < tagresult.length; i++) {
				String[] tag = tagresult[i];
				String customer = tag[1];
				String packageAsk = tag[2];
				String remark = tag[3];
				if (!CommonUtil.isNull(customer)) {
					if (GlobalData.boxCustomerConfig.get("customer") != null) {
						GlobalData.boxCustomerConfig.get("customer").add(customer);
					} else {
						temp = new ArrayList<String>();
						temp.add(customer);
						GlobalData.boxCustomerConfig.put("customer", temp);
					}
					customerLast = customer;
				}
				if (!CommonUtil.isNull(packageAsk) && customerLast != null) {
					if (GlobalData.boxCustomerConfig.get(customerLast + "_packageAsk") != null) {
						GlobalData.boxCustomerConfig.get(customerLast + "_packageAsk").add(packageAsk);
					} else {
						temp = new ArrayList<String>();
						temp.add(packageAsk);
						GlobalData.boxCustomerConfig.put(customerLast + "_packageAsk", temp);
					}
				}
				if (!CommonUtil.isNull(remark)) {
					if (GlobalData.boxCustomerConfig.get(customerLast + "_remark") != null) {
						GlobalData.boxCustomerConfig.get(customerLast + "_remark").add(remark);
					} else {
						temp = new ArrayList<String>();
						temp.add(remark);
						GlobalData.boxCustomerConfig.put(customerLast + "_remark", temp);
					}
				}
			}
			GlobalData.boxCustomerConfigTime = file.lastModified();
		}
	}

	/**
	 * 加载装箱说明模版
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void loadBoxExplainConfig() throws FileNotFoundException, IOException {
		File file = new File(wt_home + ConstantLine.box_explain_config_path);
		if (GlobalData.boxExplainConfigTime < file.lastModified()) {
			GlobalData.boxExplainConfig.clear();
			System.out.println(GlobalData.boxCustomerConfig.toString());
			for (String customer : GlobalData.boxCustomerConfig.get("customer")) {
				String[][] tagresult = ExcelUtil.getData(0, customer, file, 0, false);
				for (int i = 1; i < tagresult.length; i++) {
					String[] tag = tagresult[i];
					String inputName = tag[0];
					if (CommonUtil.isNull(inputName))
						continue;
					if (inputName.equals("箱体间线束总成PN号："))
						break;
					if (GlobalData.boxExplainConfig.get(customer + "_input") == null) {
						List<String> inputList = new ArrayList<String>();
						inputList.add(inputName);
						GlobalData.boxExplainConfig.put(customer + "_input", inputList);
					} else {
						GlobalData.boxExplainConfig.get(customer + "_input").add(inputName);
					}

				}
			}
			GlobalData.boxExplainConfigTime = file.lastModified();
		}
	}

	/**
	 * 创建 装箱说明
	 * 
	 * @param boxExplainFromPage
	 * @return
	 */
	public static String createBoxExplain(String boxExplainFromPage) {
		String returnVal = ConstantLine.str_fail;
		long time = new Date().getTime();
		JSONObject boxExplain = JSONObject.fromObject(boxExplainFromPage);
		String customer = (String) boxExplain.get("customer");
		String partNumber = (String) boxExplain.get("partNumber");
		String version = (String) boxExplain.get("version");
		String input = (String) boxExplain.get("input");
		String[] inputArr = input.split("@&@");
		JSONArray childPNList = boxExplain.getJSONArray("list");
		String tempExcelPath = null;
		String tempPdfPath = null;
		WTPart part = PartUtil.getLastestWTPartByNumber(partNumber);
		try {
			setIBAValueToLink(partNumber, childPNList);
			setIBAValue(customer,partNumber,inputArr);
			tempExcelPath = createBoxExplainExcelTemp(time, customer, partNumber, version, inputArr, childPNList);
			tempPdfPath = excel2Pdf(time, customer, tempExcelPath);
			WTDocument document = DocUtil.getLatestWTDocument(partNumber);
			if(document != null){//已经创建装箱说明，则更新
				PNUtil.deleteDescriptionLink(document,part);
				//PersistenceHelper.manager.delete(document);
				
				if(!document.getName().startsWith(part.getName())){//当零部件名称有变化时(装箱说明的 名称由  零部件名称+配置文件create_box_explain_doc_name 组成。)
					WTDocumentUtil.changeDocName(document, part.getName() + PropertiesUtil.getValueByKey("create_box_explain_doc_name"));
				}
				WTDocumentUtil.updateAttachment(document,document.getNumber()+ConstantLine.box_explain_config_pdf_suffix,tempPdfPath,document.getNumber()+ConstantLine.box_explain_config_pdf_suffix);
				WTDocumentMaster wtdocumentmaster = (WTDocumentMaster) document.getMaster();
				List<WTPartDescribeLink> linkList = PNUtil.getPartDescriptionLink(part, wtdocumentmaster);
				if(linkList == null || linkList.size() ==0){
					WTPartDescribeLink link = WTPartDescribeLink.newWTPartDescribeLink(part, document);
					PersistenceServerHelper.manager.insert(link);
				}
			}else{
				createBoxExplainDocument(part, tempPdfPath);
			}
			returnVal = ConstantLine.str_success;
		} catch (Exception e) {
			e.printStackTrace();
			returnVal = e.getMessage();
		} finally {
			if (returnVal.equals(ConstantLine.str_success)) {
				new File(tempExcelPath).delete();
				new File(tempPdfPath).delete();
			}
		}
		return returnVal;
	}

	/**
	 * 创建 装箱说明 AUTOCAD文档 并与 物料建立说明关系
	 * 
	 * @param partNumber
	 *            物料编号
	 * @param tempPdfPath
	 *            文档附件 pdf
	 * @throws WTException
	 * @throws Exception
	 * @throws WTPropertyVetoException
	 * @throws PropertyVetoException
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private static void createBoxExplainDocument(WTPart part, String tempPdfPath) throws WTException, Exception, WTPropertyVetoException, PropertyVetoException, IOException,
			FileNotFoundException {
		String path = FolderHelper.service.getFolder(part).getFolderPath();
		path = path.substring(0, path.lastIndexOf("/")) + PropertiesUtil.getValueByKey("create_box_explain_doc_folder");
		WTDocument document = CommonUtil.createDoc(part.getNumber(), part.getName() + PropertiesUtil.getValueByKey("create_box_explain_doc_name"), part, ConstantLine.var_doctype_autocadDrawing, path);
		
		setDocIBA(path,document);
		ApplicationData ap = ApplicationData.newApplicationData(document);
		ap.setRole(ContentRoleType.SECONDARY);
		ap = ContentServerHelper.service.updateContent(document, ap, tempPdfPath);
		ap.setFileName(part.getNumber() + ConstantLine.box_explain_config_pdf_suffix);
		PersistenceHelper.manager.save(ap);
		
		WTPartDescribeLink link = WTPartDescribeLink.newWTPartDescribeLink(part, document);
		PersistenceServerHelper.manager.insert(link);
	}
	/**
	 * 设置装箱说明 IBA属性  项目代码 项目简称 文档细类
	 * @param folderPath
	 * @param doc
	 * @throws WTException
	 * @throws WTPropertyVetoException 
	 * @throws RemoteException 
	 */
	private static void setDocIBA(String folderPath, WTDocument doc) throws WTException, WTPropertyVetoException, RemoteException {
		// 文档的项目代号截取自项目文件夹名称
		// 文档的项目简称截取自项目文件夹名称
		// get project folder name
		boolean isCommanContainer = false;
		String docContainer = doc.getContainerName();
		String[] commonContainerS = CatlConstant.COMMON_DOCUMENT_CONTAINER.split(",");
		for (String commonContainer : commonContainerS) {
			if (commonContainer.equals(docContainer)) {
				isCommanContainer = true;
			}
		}
		if (!isCommanContainer) {// 不是文档通用容器
			String projectShot = "";
			String projectCode = "";
			if (folderPath != null) {
				String[] projectSplit = folderPath.split("/");
				if (projectSplit.length >= 3) {
					String projectFolderName = projectSplit[2];

					int beginIndex = projectFolderName.indexOf("（");
					int endIndex = projectFolderName.indexOf("）");
					int beginIndex1 = projectFolderName.indexOf("(");
					int endIndex1 = projectFolderName.indexOf(")");
					if (beginIndex1 > beginIndex) {
						beginIndex = beginIndex1;
					}
					if (endIndex1 > endIndex) {
						endIndex = endIndex1;
					}

					if (0 < beginIndex && beginIndex + 1 < endIndex) {
						projectCode = projectFolderName.substring(0, beginIndex);
						projectShot = projectFolderName.substring(beginIndex + 1, endIndex);
					} else {
						projectCode = projectFolderName;
					}
				}
				IBAUtility iba = new IBAUtility(doc);
                iba.setIBAValue(AttributeName.DOC_PROJECT_CODE, projectCode);
                iba.setIBAValue(AttributeName.DOC_PROEJCT_NAME, projectShot);
                iba.setIBAValue(ConstantLine.doc_iba_subCategory, ConstantLine.doc_iba_subCategory_autocad);
                iba.updateAttributeContainer(doc);
                iba.updateIBAHolder(doc);
			}
		}
	}

	/**
	 * excel 转 pdf
	 * 
	 * @param time
	 *            临时文件名称后缀 唯一性
	 * @param customer
	 *            客户名称 配置文件sheet区分 目前分"宇通" "非宇通"
	 * @param tempExcelPath
	 *            excel文件
	 * @return
	 * @throws FileNotFoundException
	 * @throws DocumentException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private static String excel2Pdf(long time, String customer, String tempExcelPath) throws FileNotFoundException, DocumentException, MalformedURLException, IOException {
		FileInputStream fileInputStream = new FileInputStream(tempExcelPath);

		String tempPdfPath = wt_home + PropertiesUtil.getValueByKey("file_temp") + ConstantLine.box_explain_config_pdf_prefix + "_" + time + ConstantLine.box_explain_config_pdf_suffix;
		FileOutputStream fos = new FileOutputStream(new File(tempPdfPath));

		List<ExcelObject> objects = new ArrayList<ExcelObject>();
		objects.add(new ExcelObject(fileInputStream, customer));

		Excel2Pdf pdf = new Excel2Pdf(objects, fos);

		pdf.convert();
		return tempPdfPath;
	}

	/**
	 * 创建 装箱说明 临时文件excel 并写入数据
	 * 
	 * @param time
	 *            临时文件名称后缀 唯一性
	 * @param customer
	 *            客户名称 配置文件sheet区分 目前分"宇通" "非宇通"
	 * @param partNumber
	 *            物料名称
	 * @param version
	 *            物料版本
	 * @param inputArr
	 *            写入的数据(装箱说明配置 表头)
	 * @param childPNList
	 *            写入的数据(装箱说明配置 列表)
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static String createBoxExplainExcelTemp(long time, String customer, String partNumber, String version, String[] inputArr, JSONArray childPNList) throws FileNotFoundException, IOException {
		XSSFWorkbook workbook = null;
		FileOutputStream out = null;
		String tempExcelPath;
		FileInputStream fis = null;
		try{
			fis = new FileInputStream(wt_home + ConstantLine.box_explain_config_path);
			workbook = new XSSFWorkbook(fis);
			XSSFSheet st = workbook.getSheet(customer);
			XSSFCell cell = null;
			XSSFRow row;
			for (int rowIndex = 1, inputIndex = 0; rowIndex <= st.getLastRowNum(); rowIndex++, inputIndex++) {
				row = (XSSFRow) st.getRow(rowIndex);
				cell = row.getCell(0);
				String cellVal = null;
				if (cell != null) {
					switch (cell.getCellType()) {
					case HSSFCell.CELL_TYPE_STRING:
						cellVal = cell.getStringCellValue();
						break;
					case HSSFCell.CELL_TYPE_NUMERIC:
						cellVal = String.valueOf(cell.getNumericCellValue());
						if (cellVal.endsWith(".0")) {
							cellVal = cellVal.replace(".0", "");
						}
						break;
					}
					if (cellVal.equals("箱体间线束总成PN号：")) {
						cell.setCellValue(cellVal + partNumber);
						row.getCell(7).setCellValue(row.getCell(7).getStringCellValue() + version);
					} else if (cellVal.equals("序号")) {
						continue;
					} else if (cellVal.equals("1")) {
						XSSFRow fromRow = row;
						for (int i = 0; i < childPNList.size(); i++) {
							if (i != 0) {
								row = (XSSFRow) st.createRow(++rowIndex);
								row.setHeight(fromRow.getHeight());
								ExcelUtil.copyRow(workbook, fromRow, row, false);
								row.getCell(0).setCellValue(i + 1);
							}
							String name = childPNList.getJSONObject(i).getString("name");
							String number = childPNList.getJSONObject(i).getString("number");
							String quantity = childPNList.getJSONObject(i).getString("quantity");
							String packageAsk = childPNList.getJSONObject(i).getString("packageAsk");
							String remark = childPNList.getJSONObject(i).getString("remark");
							String mtagcontent = childPNList.getJSONObject(i).getString("mtagcontent");
							row.getCell(1).setCellValue(name);
							row.getCell(2).setCellValue(number);
							row.getCell(3).setCellValue(mtagcontent);
							row.getCell(4).setCellValue(quantity);
							row.getCell(5).setCellValue(packageAsk);
							row.getCell(6).setCellValue(remark);
							
						}
						row = (XSSFRow) st.createRow(++rowIndex);
						ExcelUtil.copyRow(workbook, fromRow, row, false);

						String remark = GlobalData.boxCustomerConfig.get(customer + "_remark").get(0);
						int count = getAStrCountInBStr("\n", remark);
						row.setHeight((short) (fromRow.getHeight() * count + 20));

						CellRangeAddress cra = new CellRangeAddress(rowIndex, rowIndex, 0, 7);
						st.addMergedRegion(cra);
						cell = row.getCell(0);
						XSSFCellStyle style = workbook.createCellStyle();
						style.setWrapText(true);
						style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
						cell.setCellStyle(style);
						cell.setCellValue(GlobalData.boxCustomerConfig.get(customer + "_remark").get(0));
						break;
					} else {
						cell.setCellValue(cellVal + inputArr[inputIndex]);
					}
				}
			}
			tempExcelPath = wt_home + PropertiesUtil.getValueByKey("file_temp") + ConstantLine.box_explain_config_name_prefix + "_" + time + ConstantLine.box_explain_config_name_suffix;
			out = new FileOutputStream(tempExcelPath);
			workbook.write(out);
		}finally{
			fis.close();
			workbook.close();
			out.flush();
			out.close();
		}
		return tempExcelPath;
	}

	/**
	 * 查询某个字符串在 另一个字符串中的个数
	 * 
	 * @param aStr
	 * @param bStr
	 * @return
	 */
	public static int getAStrCountInBStr(String aStr, String bStr) {
		int offset = 0;
		int count = 0;
		while ((offset = bStr.indexOf(aStr, offset)) != -1) {
			offset = offset + aStr.length();
			count++;
		}
		return count;
	}
	/**
	 * 
	 * @param lastPart
	 * @param part
	 * @throws WTException
	 * @throws PropertyVetoException
	 */
	public static void dealBoxExplain(WTPart lastPart, WTPart part) throws WTException, PropertyVetoException {
		String groupLimit = PropertiesUtil.getValueByKey("create_box_explain_group");
		if(part.getNumber().startsWith(groupLimit)){//物料组 状态是否符合
			//是否有关联autocad
			QueryResult desdocresult = PartDocServiceCommand.getAssociatedDescribeDocuments(part);
			while (desdocresult.hasMoreElements()) {
				WTDocument doc = (WTDocument) desdocresult.nextElement();
				TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(doc);
				String doctype = ti.getTypename();
				if (doctype.endsWith(TypeName.doc_type_autocadDrawing)) {
					ContentHolder contentHolder = ContentHelper.service.getContents(doc);
					//判断autocad是否没有主内容
					QueryResult contentitems = ContentHelper.service.getContentsByRole(contentHolder, ContentRoleType.PRIMARY);
					if(!contentitems.hasMoreElements()){
						//BOM结构是否有变化
						boolean isChange = PartUtil.compareStructure(lastPart, part);
						if(isChange){
							//移除关联 删除autocad附件
							//PNUtil.deleteDescriptionLink(doc,part);
							contentitems = ContentHelper.service.getContentsByRole(contentHolder, ContentRoleType.SECONDARY);
							while(contentitems.hasMoreElements()){
								ContentItem contentitem=(ContentItem) contentitems.nextElement();
								if(contentitem instanceof ApplicationData){
									ApplicationData appData = (ApplicationData) contentitem;
									if(appData != null){
										ContentServerHelper.service.deleteContent(contentHolder, appData);
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	public static String queryRoots(WTPart parent,int level) throws WTException {
		QueryResult qr = WTPartHelper.service.getUsedByWTParts((WTPartMaster) parent.getMaster());
		String ret = parent.getNumber();
		while (qr.hasMoreElements() && level<50) {
			WTPart part = (WTPart) qr.nextElement();
			level++;
			ret = queryRoots(part,level);
		}
		return ret;
	}
	
	/**
	 * 保存线束总成子件的装箱要求
	 * @param partNumber
	 * @param childPNList
	 * @throws WTException
	 */
	public static void setIBAValueToLink(String partNumber,JSONArray childPNList) throws WTException{
		WTPart part = PartUtil.getLastestWTPartByNumber(partNumber);
		QueryResult qr = WTPartHelper.service.getUsesWTPartMasters(part);
		while(qr.hasMoreElements()){
			  WTPartUsageLink link = (WTPartUsageLink)qr.nextElement();
			  WTPartMaster master = link.getUses();
				 for(int i=0;i<childPNList.size();i++){
						String number = childPNList.getJSONObject(i).getString("number");
						String packageAsk = childPNList.getJSONObject(i).getString("packageAsk");
						String remark = childPNList.getJSONObject(i).getString("remark");
						if(master.getNumber().equals(number)){
							//link.setValue("CATL_PackageAsk", packageAsk);
							IBAUtility utility=new IBAUtility(link);
							try {
								if(!StringUtils.isEmpty(packageAsk)){
									utility.setIBAValue(ConstantLine.box_explain_PackageAsk, packageAsk);
								}
								if(!StringUtils.isEmpty(remark)){
									utility.setIBAValue(ConstantLine.box_explain_Remark, remark);
								}else{
									utility.removeAttribute(ConstantLine.box_explain_Remark);
								}
								utility.updateAttributeContainer(link);
								utility.updateIBAHolder(link);
							} catch (WTPropertyVetoException | RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
				  }
		}
	}
	
	/**
	 * 保存线束总成装箱单表单数据
	 * @param partNumber
	 * @param str
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws RemoteException
	 */
	public static void setIBAValue(String customer,String partNumber,String [] str) throws WTException, WTPropertyVetoException, RemoteException{
		WTPart part = PartUtil.getLastestWTPartByNumber(partNumber);
		if(part!=null){
			IBAUtility utility=new IBAUtility(part);
			if(customer.equals("宇通")){
				for(int i=0;i<str.length;i++){
					switch(i){
					case 0:utility.setIBAValue(ConstantLine.box_explain_ProjectName, str[i]);
					case 1:utility.setIBAValue(ConstantLine.box_explain_ProductPN, str[i]);
					case 2:utility.setIBAValue(ConstantLine.box_explain_CustomCode, str[i]);
					case 3:utility.setIBAValue(ConstantLine.box_explain_CustomProjectCode, str[i]);
					}
				}
			}else{
				for(int i=0;i<str.length;i++){
					switch(i){
					case 0:utility.setIBAValue(ConstantLine.box_explain_ProjectName, str[i]);
					case 1:utility.setIBAValue(ConstantLine.box_explain_CustomCode, str[i]);
					case 2:utility.setIBAValue(ConstantLine.box_explain_ProductPN, str[i]);
					}
				}
			}
			utility.updateAttributeContainer(part);
			utility.updateIBAHolder(part);
		}
	};
}
