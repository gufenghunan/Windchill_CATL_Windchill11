package com.catl.change.report.bom;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;

import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.ObjectReference;
import wt.fc.QueryResult;
import wt.fc.collections.WTCollection;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartSubstituteLink;
import wt.part.WTPartUsageLink;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.catl.change.report.Excel2007Handler;
import com.catl.change.report.model.TrunkBOMQueryModel;
import com.catl.common.util.PartUtil;
import com.catl.common.util.WCLocationConstants;
import com.catl.pdfsignet.PDFSignetUtil;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;

public class TrunkBOMQueryReportHelper {
	
	static String PARTNUMBER_1;

	static String PARTNUMBER_2;

	static String PARTNUMBER_3;

	static String PARTNUMBER_4;

	static String PARTNUMBER_5;

	static String PARTNUMBER_6;

	static String PARTNUMBER_7;

	static String PARTNUMBER_8;

	static String PARTNUMBER_9;

	static String PARTNUMBER_10;
	
	private static Logger log = Logger.getLogger(TrunkBOMQueryReportHelper.class.getName());

	public TrunkBOMQueryReportHelper(TrunkBOMQueryModel queryModel) {
		
		PARTNUMBER_1 = queryModel.getPARTNUMBER_1();
		PARTNUMBER_2 = queryModel.getPARTNUMBER_2();
		PARTNUMBER_3 = queryModel.getPARTNUMBER_3();
		PARTNUMBER_4 = queryModel.getPARTNUMBER_4();
		PARTNUMBER_5 = queryModel.getPARTNUMBER_5();
		PARTNUMBER_6 = queryModel.getPARTNUMBER_6();
		PARTNUMBER_7 = queryModel.getPARTNUMBER_7();
		PARTNUMBER_8 = queryModel.getPARTNUMBER_8();
		PARTNUMBER_9 = queryModel.getPARTNUMBER_9();
		PARTNUMBER_10 = queryModel.getPARTNUMBER_10();
	}

	public void generateReport(HttpServletResponse response) throws WTException, WTPropertyVetoException {
		
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
			String currentDate = dateFormat.format((Date) new Timestamp(System.currentTimeMillis()));
			String fileName = "PLM_TrunkBOM_" + currentDate + ".xlsx";

			Workbook wb = generateExcel(fileName);
			String filename = java.net.URLEncoder.encode(fileName, "UTF-8");
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

			OutputStream os = response.getOutputStream();
			wb.write(os);

			response.setStatus(HttpServletResponse.SC_OK);
			response.flushBuffer();

			os.flush();
			os.close();

		} catch (IOException e) {
			log.debug(e.getLocalizedMessage());
			throw new WTException(e, e.getLocalizedMessage());
		} finally {
			SessionServerHelper.manager.setAccessEnforced(flag);
		}
	}

	private Workbook generateExcel(String fileName) throws WTException {

		String filePathName = WCLocationConstants.WT_CODEBASE + File.separator
				+ "com" + File.separator + "catl" + File.separator
				+ "checkPDFData" + File.separator
				+ "trunkBOM_template.xlsx";

		Excel2007Handler excelHander;
		try {
			excelHander = new Excel2007Handler(filePathName);

			//查询PN不区分大小写
			PARTNUMBER_1 = PARTNUMBER_1.toUpperCase();
			
			String[] prdPartArr = PARTNUMBER_1.split(",");
			
			List<WTPart> partlist = new ArrayList<WTPart>();
			
			boolean flg = true;
			int x = 0;
			for (int i=0; i<prdPartArr.length; i++){
				
				String partNum = prdPartArr[i];

				//取消PN前后空格
				partNum = partNum.trim();
				
				//如果传入空格，直接跳过
				if (StringUtils.isEmpty(partNum)){
					continue;
				}
				
				WTPart prdPart = PartUtil.getLastestWTPartByNumber(partNum);
				
				if (prdPart == null){
					
					x++;
					flg= false;
					excelHander.setStringValue(x, 0, "部件编号："+partNum+"，在系统中不存在！");
					log.debug("部件编号："+partNum+"，在系统中不存在！");
					continue;
				}
				partlist.add(prdPart);
			}
			log.debug("flg："+flg);
			if (flg){
				List<Integer> delList = new ArrayList<Integer>();
				Map<String, List<Integer>> hasRowMap = new HashMap<String, List<Integer>>();
				Map<String, Boolean> delRowMap = new HashMap<String, Boolean>();
				List<List<String>> outputList = new ArrayList<List<String>>();
				
				for (WTPart prdPart : partlist){
					
					List<Integer> hasRowList = new ArrayList<Integer>();
					
					List<String> lastlist = new ArrayList<String>();
					List<String> lastlist2 = new ArrayList<String>();
					
					
					String bigVer = prdPart.getVersionIdentifier().getValue();    //大版本
					String smallVer = prdPart.getIterationIdentifier().getValue();  //小版本

					lastlist.add(prdPart.getNumber());//编号
					lastlist.add(prdPart.getName());//名称
					lastlist.add(bigVer + "." + smallVer);//物料版本
					lastlist.add(get2DVersion(prdPart));//图纸版本
					lastlist.add(prdPart.getState().getState().getDisplay());//物料状态
					
					lastlist2.add(prdPart.getNumber());//编号
					lastlist2.add(prdPart.getName());//名称
					lastlist2.add(bigVer + "." + smallVer);//物料版本
					lastlist2.add(get2DVersion(prdPart));//图纸版本
					lastlist2.add(prdPart.getState().getState().getDisplay());//物料状态
					
					outputList.add(lastlist2);
					getExcelHander(prdPart, 2, lastlist, delList, hasRowList, outputList, hasRowMap, prdPart.getNumber(), delRowMap);
				}
				
				delList = new ArrayList<Integer>();
				for (WTPart prdPart : partlist){
					for (int j=2; j<=10; j++){
						if (delRowMap.get(prdPart.getNumber()+"_"+j) != null && delRowMap.get(prdPart.getNumber()+"_"+j) == true){
							if(hasRowMap.get(prdPart.getNumber()+"_"+j) != null){
								delList.addAll(hasRowMap.get(prdPart.getNumber()+"_"+j));
							}
						}
					}
				}
				
				List<List<String>> outputList2 = new ArrayList<List<String>>();
				
				for (int i=0; i<outputList.size(); i++){
					if (!delList.contains(i)){
						outputList2.add(outputList.get(i));
					}
				}
				
				for (int i=0; i<outputList2.size(); i++){
					
					List<String> rowlist = outputList2.get(i);
					for (int j=0; j<rowlist.size(); j++){
						excelHander.setStringValue(i+1, j, rowlist.get(j));
					}
				}
			}

			return excelHander.getWorkbook();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.debug(e.getMessage());
			e.printStackTrace();
		}

		return null;
	}

	public void getExcelHander(WTPart part, int i, List<String> lastlist, List<Integer> delList, List<Integer> hasRowList, List<List<String>> outputList, Map<String, List<Integer>> hasRowMap, String topPN, Map<String, Boolean> delRowMap) throws WTException {
		
		String condition = "";
		if (i == 2){
			condition = PARTNUMBER_2;
		} else if (i == 3){
			condition = PARTNUMBER_3;
		} else if (i == 4){
			condition = PARTNUMBER_4;
		} else if (i == 5){
			condition = PARTNUMBER_5;
		} else if (i == 6){
			condition = PARTNUMBER_6;
		} else if (i == 7){
			condition = PARTNUMBER_7;
		} else if (i == 8){
			condition = PARTNUMBER_8;
		} else if (i == 9){
			condition = PARTNUMBER_9;
		} else if (i == 10){
			condition = PARTNUMBER_10;
		}
		
		//查询条件去除前后空格、不区分大小写
		condition = condition.trim().toUpperCase();
		
		if (StringUtils.isNotEmpty(condition) && condition.lastIndexOf("*") > -1){
			condition = condition.substring(0, condition.length()-1);
		}
		if (StringUtils.isEmpty(condition)){
			return;
		}
		
		part.getName();
		List<Map<WTPart, String[]>> partlist = getChildPart(part, condition);
		
		if (partlist.size() == 0){
			
			
			if (delRowMap.get(topPN+"_"+i) != null && delRowMap.get(topPN+"_"+i) == false){
				delRowMap.put(topPN+"_"+i, true);
			}
			
			if (hasRowMap.get(topPN+"_"+i) == null){
				List<Integer> rowdelList = new ArrayList<Integer>();
				rowdelList.add(outputList.size()-1);
				hasRowMap.put(topPN+"_"+i, rowdelList);
			} else {
				hasRowMap.get(topPN+"_"+i).add(outputList.size()-1);
			}
			
			outputList.get(outputList.size()-1).add("PN错误或不存在！");
			return;
		}
		
		if (hasRowMap.get(topPN+"_"+i) != null){
			
			delRowMap.put(topPN+"_"+i, true);
		} else {
			delRowMap.put(topPN+"_"+i, false);
		}
		
		for (int y=0; y<partlist.size(); y++){
			
			Map<WTPart, String[]> partMap = partlist.get(y);
			for (Map.Entry<WTPart, String[]> entry : partMap.entrySet()) {
				
				WTPart childPart = entry.getKey();
				String[] arr = entry.getValue();
				
				if (y == 0){
					
					outputList.get(outputList.size()-1).add(arr[0]);
					outputList.get(outputList.size()-1).add(arr[1]);
					outputList.get(outputList.size()-1).add(arr[2]);
					outputList.get(outputList.size()-1).add(arr[3]);
					outputList.get(outputList.size()-1).add(arr[4]);
					outputList.get(outputList.size()-1).add(arr[5]);
					outputList.get(outputList.size()-1).add(arr[6]);
				} else {
					
					List<String> list = new ArrayList<String>();
					list.addAll(lastlist);
					list.add(arr[0]);
					list.add(arr[1]);
					list.add(arr[2]);
					list.add(arr[3]);
					list.add(arr[4]);
					list.add(arr[5]);
					list.add(arr[6]);
					outputList.add(list);
				}
				
				List<String> newlastlist = new ArrayList<String>();
				newlastlist.addAll(lastlist);
				newlastlist.add(arr[0]);
				newlastlist.add(arr[1]);
				newlastlist.add(arr[2]);
				newlastlist.add(arr[3]);
				newlastlist.add(arr[4]);
				newlastlist.add(arr[5]);
				newlastlist.add(arr[6]);
				
				getExcelHander(childPart, i+1, newlastlist, delList, hasRowList, outputList, hasRowMap, topPN, delRowMap);
			}
		}
	}
	
	public static List<Map<WTPart, String[]>> getChildPart(WTPart part, String condition) throws WTException {
		
		List<Map<WTPart, String[]>> maplist = new ArrayList<Map<WTPart, String[]>>();
		String[] partArr = new String[7];	//编号、名称、用量、物料版本、图纸版本、物料状态、主料
		
		QueryResult qr2 = WTPartHelper.service.getUsesWTPartMasters(part);
		
		List<String> sortlist = new ArrayList<String>();
		Map<String, Map<WTPart, String[]>> childPartMap = new HashMap<String, Map<WTPart, String[]>>();
		Map<String, List<Map<WTPart, String[]>>> subPartListMap = new HashMap<String, List<Map<WTPart, String[]>>>();
		
		while(qr2.hasMoreElements()){
			
			WTPartUsageLink link = (WTPartUsageLink)qr2.nextElement();
			WTPartMaster childmaster = link.getUses();
			
			String chilidNumber = childmaster.getNumber();
			String chilidName = childmaster.getName();
			String chilidamount = link.getQuantity().getAmount() + "";
			
			if (!chilidNumber.startsWith(condition)){
				continue;
			}
			partArr = new String[7];
			partArr[0] = chilidNumber;
			partArr[1] = chilidName;
			partArr[2] = chilidamount;
			
			WTPart childPart = PartUtil.getLastestWTPartByNumber(chilidNumber);
			
			String bigVer = childPart.getVersionIdentifier().getValue();    //大版本
			String smallVer = childPart.getIterationIdentifier().getValue();  //小版本

			partArr[3] = bigVer + "." + smallVer;
			partArr[4] = get2DVersion(childPart);
			partArr[5] = childPart.getState().getState().getDisplay();
			partArr[6] = "";
			
			sortlist.add(chilidNumber);
			Map<WTPart, String[]> map1 = new HashMap<WTPart, String[]>();
			map1.put(childPart, partArr);
			childPartMap.put(chilidNumber, map1);
			
			
			WTCollection collection = WTPartHelper.service.getSubstituteLinks(link);
			if (!collection.isEmpty()) {
				
				List<String>subsortlist = new ArrayList<>();
				Map<String, Map<WTPart, String[]>> map3 = new HashMap<String, Map<WTPart, String[]>>();
				Iterator itr = collection.iterator();
				while (itr.hasNext()) {
					ObjectReference objReference = (ObjectReference) itr.next();
					WTPartSubstituteLink subLink = (WTPartSubstituteLink) objReference.getObject();
					WTPartMaster subpartMaster = (WTPartMaster) subLink.getSubstitutes();
					String subPartName = subpartMaster.getName();
					String subPartNumber = subpartMaster.getNumber();
					String subAmount = "";
					
					if (!subPartNumber.startsWith(condition)){
						continue;
					}
					
					WTPart subPart = PartUtil.getLastestWTPartByNumber(subPartNumber);
					if (subLink == null || subLink.getQuantity() == null || subLink.getQuantity().getAmount() == null){
						subAmount = "";
					} else {
						subAmount = subLink.getQuantity().getAmount() + "";
					}
					
					partArr = new String[7];	//编号、名称、用量、物料版本、图纸版本、物料状态、主料
					partArr[0] = subPartNumber;
					partArr[1] = subPartName;
					partArr[2] = subAmount;
					
					String subbigVer = childPart.getVersionIdentifier().getValue();    //大版本
					String subsmallVer = childPart.getIterationIdentifier().getValue();  //小版本

					partArr[3] = subbigVer + "." + subsmallVer;
					partArr[4] = get2DVersion(subPart);
					partArr[5] = subPart.getState().getState().getDisplay();
					partArr[6] = chilidNumber;
					
					subsortlist.add(subPartNumber);
					Map<WTPart, String[]> map2 = new HashMap<WTPart, String[]>();
					map2.put(subPart, partArr);
					map3.put(subPartNumber, map2);
				}
				
				
				Collections.sort(subsortlist);
				for (String str : subsortlist){
					
					if(subPartListMap.get(chilidNumber) == null){
						
						List<Map<WTPart, String[]>> list = new ArrayList<Map<WTPart, String[]>>();
						list.add(map3.get(str));
						subPartListMap.put(chilidNumber, list);
					} else {
						subPartListMap.get(chilidNumber).add(map3.get(str));
					}
				}
				
			}
		}
		
		Collections.sort(sortlist);
		for (String str : sortlist){
			
			if (childPartMap.get(str) != null){
				maplist.add(childPartMap.get(str));
			}
			
			if (subPartListMap.get(str) != null){
				maplist.addAll(subPartListMap.get(str));
			}
		}
		
		return maplist;
	}
	
	public static String get2DVersion(WTPart part) throws WTException {
		
		boolean flg = false;	//判断是否已经有autoCAD或2D图纸
		String verStr = "";
		// 获取说明方文档
		QueryResult desdocresult = PartDocServiceCommand.getAssociatedDescribeDocuments(part);
		while (desdocresult.hasMoreElements()) {
			WTDocument doc = (WTDocument) desdocresult.nextElement();
			if (PDFSignetUtil.isAutoCADDoc(doc)){	//判断是否是autoCAD文档
				if (!flg){
					flg = true;
					String bigVer = doc.getVersionIdentifier().getValue();    //大版本
					String smallVer = doc.getIterationIdentifier().getValue();  //小版本
					verStr = bigVer + "." + smallVer;
				} else {
					return "存在多份2D图纸！";
				}
			}
		}
		
		// 获取CAD/动态文档
        QueryResult cadresult =PartDocServiceCommand.getAssociatedCADDocuments(part);
        while(cadresult.hasMoreElements()){
        	EPMDocument epmdoc =(EPMDocument)cadresult.nextElement();
        	epmdoc.getNumber();
        	if ("CADDRAWING".equals(epmdoc.getDocType().toString().toUpperCase())){
        		if (!flg){
					flg = true;
					String bigVer = epmdoc.getVersionIdentifier().getValue();    //大版本
					String smallVer = epmdoc.getIterationIdentifier().getValue();  //小版本
					verStr = bigVer + "." + smallVer;
				} else {
					return "存在多份2D图纸！";
				}
        	}
        }
		
		return verStr;
	}
}
