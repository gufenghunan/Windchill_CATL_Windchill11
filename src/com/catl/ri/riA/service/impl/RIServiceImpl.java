package com.catl.ri.riA.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import wt.doc.WTDocument;
import wt.folder.Folder;
import wt.inf.container.WTContainer;
import wt.project.Role;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.wip.WorkInProgressHelper;

import com.catl.ri.riA.helper.SyncHelper;
import com.catl.battery.constant.ConstantBattery;
import com.catl.bom.workflow.BomWfUtil;
import com.catl.common.constant.RoleName;
import com.catl.line.util.WCUtil;
import com.catl.ri.constant.ConstantRI;
import com.catl.ri.riA.constant.GlobalData;
import com.catl.ri.riA.helper.CacheHelper;
import com.catl.ri.riA.helper.CommonHelper;
import com.catl.ri.riA.helper.EncryptHelper;
import com.catl.ri.riA.service.RIService;
import com.catl.ri.riA.util.CommonUtil;
import com.catl.ri.riA.util.ExcelUtil;
import com.catl.ri.riA.util.WTDocumentUtil;
import com.catl.ri.riA.validator.SubmitValidator;

@Scope("prototype")
@Service("riAService")
public class RIServiceImpl implements RIService {
	private static final Logger logger = Logger.getLogger(RIServiceImpl.class.getName());
	private static String wt_home = "";
	static {
		try {
			wt_home = WTProperties.getLocalProperties().getProperty("wt.home",
					"UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getDisplayHtml(String templatename) {
		// TODO Auto-generated method stub
		String htmlStr = "";
		String htmString = "";
		try {
			FileInputStream fileInputStream = new FileInputStream(new File(
					wt_home + ConstantRI.config_path_htmltemplateA
							+ templatename));
			InputStreamReader streamReader = new InputStreamReader(
					fileInputStream);
			BufferedReader reader = new BufferedReader(streamReader);
			while ((htmlStr = reader.readLine()) != null) {
				if (htmlStr != null) {
					htmString += htmlStr;

					continue;
				}
			}
			streamReader.close();
			fileInputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return htmString;
	}

	@Override
	public String exportExcel() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createDesignExcel(String wtDocOid,String oldname,String oldlevel,String oldremark,String name, String level, String remark)
			throws Exception {
		CacheHelper.loadMathConfig();
		Pattern pattern=Pattern.compile("^*_\\d{3}_");
		Matcher matcher=pattern.matcher(name);
		if(matcher.find()){
			throw new Exception("名称中不能包含连续的下划线、三位数字、下划线（如：_123_）!");
		}
		
		String oldfilename="";
		if(!StringUtils.isEmpty(oldname)&&!StringUtils.isEmpty(oldlevel)){
			oldfilename=CommonHelper.getFileName(wtDocOid, oldname, oldlevel, oldremark);
		}
		String fileName=CommonHelper.getFileName(wtDocOid, name, level, remark);
		File file = new File(CommonHelper.getRIDirFile(fileName));
		File oldfile = new File(CommonHelper.getRIDirFile(oldfilename));
		if(file.exists()){
			return "已存在";
		}else{
			if(StringUtils.isEmpty(oldfilename)){//设计表新建
				File dir = file.getParentFile();
				if (!dir.exists()) {
					dir.mkdir();
				}
				XSSFWorkbook workbook = EncryptHelper.getEncryptWorkbook(wt_home+ ConstantRI.config_rimathA_xls);
				OutputStream outputStream = new FileOutputStream(file);
				workbook.write(outputStream);
				outputStream.flush();
				outputStream.close();
				workbook.close();
				EncryptHelper.encryptExcel(file);
				return "";
			}
			oldfile.renameTo(file);
			return "";
		}
		
	}

	@Override
	public void outputDoc(String docoid,String description,String name,String level,String remark,String folderOid) throws Exception {
		WTDocument doc = null;
		try {
			if(description==null){
				description="";
			}
			String filePath=CommonHelper.getRIDir();
			String path = CommonHelper.getFilePath(docoid, name, level, remark, filePath);
			CommonHelper.CacheWriteToLocal(path);
			ExcelUtil.clearFormula(path);
			String fileName=path.substring(path.lastIndexOf("/")+1, path.length());
			String docNumber="";
			if(fileName.contains("XXX")){
				docNumber = "_"+CommonUtil.getMaxDocNumber(name)+"_";
				fileName = fileName.replace("_XXX_", docNumber);
			}
			
			String docName=fileName.replace(".xlsx", "");
			if(!docoid.isEmpty()){
				doc=WTDocumentUtil.getDocumentByOid(docoid);
				docName=docName.replace("###"+doc.getNumber(), "");
				fileName=fileName.replace("###"+doc.getNumber(), "");
				String oldDocName=doc.getName();
				if(oldDocName.contains(name)){
					String oldNumber=null;
					Pattern pattern=Pattern.compile("^*_\\d{3}_");
					Matcher matcher=pattern.matcher(oldDocName);
					if(matcher.find()){
						oldNumber=matcher.group();
					}
					docName=docName.replace(docNumber,oldNumber);
					fileName=fileName.replace(docNumber,oldNumber);
				}
				doc=(WTDocument) CommonUtil.checkoutObject(doc);
				doc=WTDocumentUtil.changeDocName(doc, docName);
				doc=(WTDocument) CommonUtil.checkinObject(doc, description);
			}else{
				Folder folder=(Folder) WCUtil.getWTObject(folderOid);
				if(folder.getName().equals(ConstantRI.ri_speical_document)){
					doc = CommonUtil.createDoc(docName,ConstantRI.WTDOCUMENT_TYPESPEICAL, folder,description);
				}else{
					doc = CommonUtil.createDoc(docName,ConstantRI.WTDOCUMENT_TYPEA, folder,description);
				}
				
			}
			
			if(doc == null){
				throw new Exception("上传计算结果时没有相关文档！");
			}
			
			File oldFile=new File(path);
			String newFilePath = CommonHelper.getRIDirFile(fileName); 
			File newFile = new File(newFilePath);
			oldFile.renameTo(newFile);
			
			WTDocumentUtil.replaceDocPrimaryContent(doc, newFilePath,fileName, newFile.length());
			newFile.delete();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void updateDesignExcel(String sheetname, String jsonstr) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateDocName(String oid, String name, String level,
			String remark) {
		// TODO Auto-generated method stub

	}

	@Override
	public void submitForm(String docoid,String name,String level,String remark,String jsonstr, String sheetname,boolean formcheck) throws Exception {
		String filePath=CommonHelper.getRIDir();
		String path=CommonHelper.getFilePath(docoid, name, level, remark, filePath);
		@SuppressWarnings("unchecked")
		Map<String,String> values=JSONObject.fromObject(jsonstr);
		ExcelUtil.setValuesToTempExcel(path, sheetname, values);
		if(formcheck){
			SubmitValidator.validateFormValues(sheetname,values);
		}
		
	}

	@Override
	public JSONArray getPageJson(String oid,String name,String level,String remark,String sheetname) throws Exception {
		String path="";
		try {
			path = CommonHelper.getFilePath(oid, name, level, remark, CommonHelper.getRIDir());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new WTException(e);
		}
		JSONArray array=CommonHelper.getExcelValue(path, sheetname);
		return array;
	}

	@Override
	public JSONArray updateRI(String wtDocOid) throws Exception {
		CacheHelper.loadMathConfig();
		// TODO Auto-generated method stub
		List<String> docInfoList=new ArrayList<String>();
		WTDocument document=WTDocumentUtil.getDocumentByOid(wtDocOid);
		if(document!=null){
			if(WorkInProgressHelper.isCheckedOut(document)){
				throw new WTException("请先检入后再试!");
			}
			document=CommonUtil.getLatestWTDocByNumber(document.getNumber());
			String baseFilePath=wt_home+ConstantRI.config_rimathA_xls;
			String filePath=CommonHelper.getRIDir();
			deleteremainRIDoc(filePath,document.getNumber());
			filePath=WTDocumentUtil.downloadRIDoc(document, filePath);
			String docName=document.getName();
			String msg=ExcelUtil.addFormula(filePath,baseFilePath);
			String numberStr=null;
			Pattern pattern=Pattern.compile("^*_\\d{3}_");
			Matcher matcher=pattern.matcher(docName);
			if(matcher.find()){
				numberStr=matcher.group();
			}else{
				throw new WTException("电芯设计文档名称不规范");
			}
			
			int numberStrIndex=docName.indexOf(numberStr);
			String name=docName.substring(0,numberStrIndex);
			String level=docName.substring(numberStrIndex+5, numberStrIndex+7);
			String mark="";
			if(docName.indexOf(level+"_")>-1){
				mark=docName.substring(numberStrIndex+8, docName.length());
			}
			docInfoList.add(name);
			docInfoList.add(level);
			docInfoList.add(mark);
			docInfoList.add(msg);
		}
		return JSONArray.fromObject(docInfoList);
	}

	private void deleteremainRIDoc(String filePath,String number) {
		File file=new File(filePath);
		if(file.exists()&&file.isDirectory()){
			File files[]=file.listFiles();
			for(int i=0;i<files.length;i++){
				File oldFile=files[i];
				if(oldFile.getName().contains("###"+number)){
					oldFile.delete();
				}
			}
		}		
	}

	@Override
	public void saveDesign(String wtDocOid,String sheetname,String jsonstr,String name, String level,
			String remark) throws Exception {
		submitForm(wtDocOid, name, level, remark, jsonstr, sheetname,false);
		//String fileName = CommonHelper.getFileName(wtDocOid, name, level, remark);
		String path= CommonHelper.getFilePath(wtDocOid, name, level, remark, CommonHelper.getRIDir());
		CommonHelper.CacheWriteToLocal(path);
	}

	@Override
	public JSONArray updatePageJson(String docoid,String jsonstr,String sheetname, String name,
			String level, String remark) throws Exception {
		String filename=CommonHelper.getFileName(docoid, name, level, remark);
		submitForm(docoid, name, level, remark, jsonstr, sheetname,false);
		JSONArray array=getPageJson(docoid, filename, level, remark, sheetname);
		return array;
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONArray getUserAllFileName() throws Exception {
		// TODO Auto-generated method stub
		String filePath=CommonHelper.getRIDir();
		File oldFile=null;
		String name=null;
		String suffix=null;
		File file=new File(filePath);
		List<String> fileNames=new ArrayList<String>();
		if(file.exists()&&file.isDirectory()){
			File files[]=file.listFiles();
			List<File> lfiles=Arrays.asList(files);
			Collections.sort(lfiles, new Comparator<File>() {
				public int compare(File o1, File o2) {
					if(o1.lastModified()-o2.lastModified()>0){
						return -1;
					}else{
						return 1;
					}
				}
			});
			for(int i=0;i<lfiles.size();i++){
				oldFile=lfiles.get(i);
				name=oldFile.getName();
				if(name.contains("XXX")&&!name.contains("###")){
					suffix=name.substring(name.lastIndexOf("."), name.length());
					fileNames.add(name.replace(suffix, ""));
				}
			}
		}
		return JSONArray.fromObject(fileNames);
	}

	@Override
	public JSONArray getMathData(String wtDocOid, String region,String value,String name,
			String level, String remark, String sheetname) throws Exception {
		String path="";
		try {
			path = CommonHelper.getFilePath(wtDocOid, name, level, remark, CommonHelper.getRIDir());
		} catch (Exception e) {
			throw new WTException(e.getLocalizedMessage());
		}
		ExcelUtil.setValueToTempExcel(path, sheetname,region,value);
		JSONArray array=CommonHelper.getFormulaValue(region,path, sheetname);
		return array;
	
	}

	@Override
	public void exportSheet(String wtDocOid, String name, String level,
			String remark,String sheetname,HttpServletRequest request, HttpServletResponse response) throws Exception {
		String path=CommonHelper.getRIDir();
		String filePath=CommonHelper.getFilePath(wtDocOid, name, level, remark, path);
		Workbook workbook=ExcelUtil.clearSheetFormula(filePath,sheetname);
		Sheet confSheet=workbook.getSheet(sheetname);
		int confSheetIndex=workbook.getSheetIndex(confSheet);
		int sheetCount=workbook.getNumberOfSheets()-1;
		for(int sheetIndex=sheetCount;sheetIndex>-1;sheetIndex--){
			if(sheetIndex!=confSheetIndex){
				Sheet delsheet=workbook.getSheetAt(sheetIndex);
				workbook.removeSheetAt(workbook.getSheetIndex(delsheet.getSheetName()));
			}
		}
		String fileName=sheetname+".xlsx";
		
		String userAgent = request.getHeader("USER-AGENT");
        if(StringUtils.contains(userAgent, "MSIE")){//IE浏览器
        	fileName = URLEncoder.encode(fileName,"UTF8");
        }else if(StringUtils.contains(userAgent, "Mozilla")){//google,火狐浏览器
        	fileName = new String(fileName.getBytes(), "ISO8859-1");
        }else{
        	fileName = URLEncoder.encode(fileName,"UTF8");//其他浏览器
        }
		
		response.setContentType("application/vnd.ms-excel;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		response.setHeader("Content-disposition", "attachment;filename="+fileName);
		OutputStream out=response.getOutputStream();
		workbook.write(out);
		out.flush();
		out.close();
		workbook.close();
	}

	@Override
	public JSONArray getDefaultValue(String wtDocOid,String name,String level,String remark,String currentValue) throws Exception {
		// TODO Auto-generated method stub
		Map<String,String> defaultValueMap=null;
		Map<String,String> setValueMap=new HashMap<String, String>();
		List<Map<String,String>> values=new ArrayList<Map<String,String>>();
		CacheHelper.loadExcelConfig();
		List<Map<String,String>> defaultConfigs=GlobalData.defaultconfig_info;
		for(Map<String, String> defaultConfMap:defaultConfigs){
			if(defaultConfMap.get(ConstantRI.config_defaultconfig_cell).equals(currentValue)){
				for(Map.Entry<String, String> entry:defaultConfMap.entrySet()){
					String value=entry.getValue();
					if(value.isEmpty() || value.equals(currentValue)){
						continue;
					}
					String[] cellInfo=value.split("\\|");
					defaultValueMap=new HashMap<String, String>();
					defaultValueMap.put("region", cellInfo[0]);
					defaultValueMap.put("value", cellInfo[1]);
					setValueMap.put(cellInfo[0], cellInfo[1]);
					values.add(defaultValueMap);
				}
			}
		}
		String path=CommonHelper.getRIDir();
		path=CommonHelper.getFilePath(wtDocOid, name, level, remark, path);
		ExcelUtil.setValuesToTempExcel(path, currentValue.substring(0, currentValue.indexOf("!")), setValueMap);
		return JSONArray.fromObject(values);
	}

	@Override
	public void submitvalidateform(String wtDocOid, String name,
			String level, String remark) throws Exception {
		String filePath=CommonHelper.getRIDir();
		String path = CommonHelper.getFilePath(wtDocOid, name, level, remark, filePath);
		List names=SubmitValidator.getSheetNames();
		Map values=new HashMap();
		for (int i = 0; i < names.size(); i++) {
			String sheetname=(String) names.get(i);
			JSONArray array=CommonHelper.getExcelValue(path, sheetname);
			for (int j = 0; j < array.size(); j++) {
				JSONObject attr=(JSONObject) array.get(j);
				values.put(attr.get("region"),attr.get("value"));
			}
			SubmitValidator.validateFormValues(sheetname,values);
		}
		
	}

	@Override
	public boolean isSpeicalGroupUser(String containerOid) throws WTException {
		Role productrole = Role.toRole(ConstantRI.speical_group_role);
		WTContainer container=(WTContainer) WCUtil.getWTObject(containerOid);
		List<String> principals=BomWfUtil.getContainerPri(productrole,container);
		String username=SessionHelper.getPrincipal().getName();
		if(principals.contains(username)){
			return true;
		}
		return false;
	}

	@Override
	public void syncTemp(String filename) throws Exception {
		CacheHelper.loadMathConfig();
		String name=SessionHelper.getPrincipal().getName();
		if(!GlobalData.configchanges.contains(name+File.separator+filename)){
			String filepath=CommonHelper.getRIDirFile(filename);
			String baseFilePath=wt_home+ConstantRI.config_rimathA_xls;
			SyncHelper.syncFormula(filepath,baseFilePath);
			GlobalData.configchanges.add(name+File.separator+filename);
		}
		
	}
}
