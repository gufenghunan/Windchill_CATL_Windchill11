package com.catl.loadData;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.catl.common.util.WCLocationConstants;
import com.catl.loadData.util.ExcelReader;
import com.catl.part.classification.AttributeForFAE;
import com.catl.part.classification.ClassificationNodeConfig;
import com.catl.part.classification.NodeConfigHelper;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;

import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.pom.Transaction;
import wt.util.WTException;
import wt.util.WTMessage;

public class NodeConfigDataUtil implements RemoteAccess {
	
	private static final String CUSTOMIZED = "定制";
	private static final String RESOURCE = "采购类型";

	public static void main(String[] args) {
		if (args != null) {
			String filePathName = "";
			String userName = "";
			String password = "";
			if(args.length > 2){
				filePathName = args[0];
				userName = args[1];
				password = args[2];
			}
			else if(args.length == 2){
				userName = args[0];
				password = args[1];
			}
			
			RemoteMethodServer ms = RemoteMethodServer.getDefault();
			ms.setUserName(userName);
			ms.setPassword(password);
			
			try {
				System.out.println("=========开始执行=======");
				ms.invoke("updateNodeConfig", NodeConfigDataUtil.class.getName(), null, new Class[]{String.class}, new Object[]{filePathName});
				System.out.println("=========执行完毕=======");
				if(StringUtils.isNotBlank(filePathName)){
					System.out.println("执行结果请查看Windchill=>logs中的日志文件");
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
	}
	
	public static void updateNodeConfig(String filePathName) throws Exception{
		if(StringUtils.isNotBlank(filePathName)){
			StringBuilder errorMsg = new StringBuilder();
			System.out.println("before readExcelData");
			List<String[]> list = readExcelData(filePathName,errorMsg);
			if(errorMsg.length() > 0){
				list.clear();
				writeLogFile(errorMsg.toString());
			}
			else {
				Transaction trs = null;
				try{
					trs = new Transaction();
		            trs.start();
		            for (String[] attrValues : list) {
						saveNodeConfig(attrValues);
					}
					trs.commit();
			        trs = null;
				} finally {
		            if (trs != null) {
		                trs.rollback();
		            }
		        }
				writeLogFile(filePathName+"中的数据修改成功！");
			}
		}
		else {
			autoCreateNodeConfig();
		}
	}
	
	public static void saveNodeConfig(String[] attrValues) throws WTException{
		Map<String, Object> attrs = new HashMap<String, Object>();
		attrs.put(ClassificationNodeConfig.NODE_INTERNAL_NAME, attrValues[0]);
		String needFae = attrValues[1];
		String attRef = attrValues[2];
		String makeFae = attrValues[3];
		String buyFae = attrValues[4];
		String makeBuyFae = attrValues[5];
		String customerFae = attrValues[6];
		String virtualFae = attrValues[7];
		String needReport = attrValues[8];
		if(StringUtils.equals(needFae, "Y")){
			attrs.put(ClassificationNodeConfig.NEED_FAE, true);
		}
		else if(StringUtils.equals(needFae, "N")){
			attrs.put(ClassificationNodeConfig.NEED_FAE, false);
		}
		if(StringUtils.equals(needReport, "Y")){
			attrs.put(ClassificationNodeConfig.NEED_NON_FAE_REPORT, true);
		}
		else if(StringUtils.equals(needReport, "N")){
			attrs.put(ClassificationNodeConfig.NEED_NON_FAE_REPORT, false);
		}
		if(StringUtils.equals(attRef, CUSTOMIZED)){
			attrs.put(ClassificationNodeConfig.ATTRIBUTE_REF, AttributeForFAE.CUSTOMIZED);
		}else if(StringUtils.equals(attRef, RESOURCE)){
			attrs.put(ClassificationNodeConfig.ATTRIBUTE_REF, AttributeForFAE.SOURCE);
		}
		if(StringUtils.equals(makeFae, "Y")){
			attrs.put(ClassificationNodeConfig.MAKE_NEED_FAE, true);
		}else if(StringUtils.equals(makeFae, "N")){
			attrs.put(ClassificationNodeConfig.MAKE_NEED_FAE, false);
		}
		if(StringUtils.equals(buyFae, "Y")){
			attrs.put(ClassificationNodeConfig.BUY_NEED_FAE, true);
		}else if(StringUtils.equals(buyFae, "N")){
			attrs.put(ClassificationNodeConfig.BUY_NEED_FAE, false);
		}
		if(StringUtils.equals(makeBuyFae, "Y")){
			attrs.put(ClassificationNodeConfig.MAKE_BUY_NEED_FAE, true);
		}else if(StringUtils.equals(makeBuyFae, "N")){
			attrs.put(ClassificationNodeConfig.MAKE_BUY_NEED_FAE, false);
		}
		if(StringUtils.equals(customerFae, "Y")){
			attrs.put(ClassificationNodeConfig.CUSTOMER_NEED_FAE, true);
		}else if(StringUtils.equals(customerFae, "N")){
			attrs.put(ClassificationNodeConfig.CUSTOMER_NEED_FAE, false);
		}
		if(StringUtils.equals(virtualFae, "Y")){
			attrs.put(ClassificationNodeConfig.VIRTUAL_NEED_FAE, true);
		}else if(StringUtils.equals(virtualFae, "N")){
			attrs.put(ClassificationNodeConfig.VIRTUAL_NEED_FAE, false);
		}
		NodeConfigHelper.saveNodeConfig(attrs);
	}
	
	private static void autoCreateNodeConfig() throws WTException{
		Set<LWCStructEnumAttTemplate> set = NodeConfigHelper.getAllNodes();
		for (LWCStructEnumAttTemplate node : set) {
			NodeConfigHelper.autoCreateNodeConfig(node);
		}
	}
	
	private static List<String[]> readExcelData(String filePathName, StringBuilder errorMsg) throws Exception{
		List<String[]> list = new ArrayList<String[]>();
		ExcelReader er = new ExcelReader(new File(filePathName));
		er.open();
		int count = er.getRowCount();
		System.out.println("====RowCount:"+count);
		String[] cells = null;
		for(int i=1; i<=count; i++){
			cells = er.readExcelLine(i);
			if(cells==null || StringUtils.isBlank(cells[0])){
				break;
			}
			checkData(i+1, cells, errorMsg);
			list.add(cells);
		}
		if(list.size() == 0){
			errorMsg.append(WTMessage.formatLocalizedMessage("文件[{0}]中没有数据行！\r\n", new Object[]{filePathName}));
		}
		return list;
	}
	
	private static void checkData(int rowNumber, String[] cells, StringBuilder errorMsg) throws WTException{
		StringBuilder rowMsg = new StringBuilder();
		String[] values = new String[]{"","","","","","","","",""};
		int end = cells.length < 4?cells.length:9;
		for(int i=0; i < end; i++){
			values[i] = cells[i];
		}
		String nodeName = values[0];
		LWCStructEnumAttTemplate node = NodeConfigHelper.getClassificationNode(nodeName);
		if(node == null){
			rowMsg.append(WTMessage.formatLocalizedMessage("物料分类[{0}]不存在;", new Object[]{nodeName}));
		}
//		else if(!NodeConfigHelper.instantiable(node)){
//			rowMsg.append(WTMessage.formatLocalizedMessage("物料分类[{0}]不可实例化;", new Object[]{nodeName}));
//		}
		String needFae = values[1];
		if(!(StringUtils.equals(needFae, "Y") || StringUtils.equals(needFae, "N"))){
			rowMsg.append(WTMessage.formatLocalizedMessage("是否需要FAE的值不合法：{0};", new Object[]{needFae}));
		}
		String attRef = values[2];
		if(!(StringUtils.equals(attRef, CUSTOMIZED) || StringUtils.equals(attRef, RESOURCE) || attRef.isEmpty())){
			rowMsg.append(WTMessage.formatLocalizedMessage("用于辅助判断FAE的属性值不合法：{0};", new Object[]{attRef}));
		}
		String makeFae = values[3];
		if(!(StringUtils.equals(makeFae, "Y") || StringUtils.equals(makeFae, "N") || makeFae.isEmpty())){
			rowMsg.append(WTMessage.formatLocalizedMessage("自制是否需要FAE不合法：{0};", new Object[]{needFae}));
		}
		String buyFae = values[4];
		if(!(StringUtils.equals(buyFae, "Y") || StringUtils.equals(buyFae, "N") || buyFae.isEmpty())){
			rowMsg.append(WTMessage.formatLocalizedMessage("外购是否需要FAE不合法：{0};", new Object[]{needFae}));
		}
		String makeBuyFae = values[5];
		if(!(StringUtils.equals(makeBuyFae, "Y") || StringUtils.equals(makeBuyFae, "N") || makeBuyFae.isEmpty())){
			rowMsg.append(WTMessage.formatLocalizedMessage("外协是否需要FAE不合法：{0};", new Object[]{needFae}));
		}
		String customerFae = values[6];
		if(!(StringUtils.equals(customerFae, "Y") || StringUtils.equals(customerFae, "N") || customerFae.isEmpty())){
			rowMsg.append(WTMessage.formatLocalizedMessage("客供是否需要FAE不合法：{0};", new Object[]{needFae}));
		}
		String virtualFae = values[7];
		if(!(StringUtils.equals(virtualFae, "Y") || StringUtils.equals(virtualFae, "N") || virtualFae.isEmpty())){
			rowMsg.append(WTMessage.formatLocalizedMessage("虚拟是否需要FAE不合法：{0};", new Object[]{needFae}));
		}
		String needReport = values[8];
		if(!(StringUtils.equals(needReport, "Y") || StringUtils.equals(needReport, "N"))){
			rowMsg.append(WTMessage.formatLocalizedMessage("是否需要FAE成熟度报告的值不合法：{0};", new Object[]{needReport}));
		}
		if(rowMsg.length()>0){
			rowMsg.insert(0, WTMessage.formatLocalizedMessage("行号为{0}的数据行存在如下问题：", new Object[]{rowNumber}));
			rowMsg.append("\r\n");
			errorMsg.append(rowMsg.toString());
		}
	}
	
	private static void writeLogFile(String msg) throws Exception{
		String filePathName = getLogFilePathName();
		FileWriter fw = null;
		try{
			File txtFile = new File(filePathName);
			if(txtFile.exists()){
				txtFile.delete();
			}
			fw = new FileWriter(filePathName, true);
			fw.write(msg);
		}
		finally{
			fw.flush();
			fw.close();
		}
	}
	
	private static String getLogFilePathName(){
		StringBuilder filePathName = new StringBuilder(WCLocationConstants.WT_LOG);
		filePathName.append(File.separator).append("update_classificationConfig_");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		filePathName.append(sdf.format(new Date())).append(".log");
		return filePathName.toString();
	}
}
