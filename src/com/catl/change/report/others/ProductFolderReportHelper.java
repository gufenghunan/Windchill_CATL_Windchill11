package com.catl.change.report.others;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.folder.FolderHelper;
import wt.folder.FolderingInfo;
import wt.folder.SubFolder;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.pdmlink.PDMLinkProduct;
import wt.query.ArrayExpression;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.catl.change.report.Excel2007Handler;
import com.catl.common.util.PartUtil;
import com.catl.common.util.WCLocationConstants;

public class ProductFolderReportHelper
{
	private static Logger log = Logger.getLogger(ProductFolderReportHelper.class.getName());

	private static String PDMLINKPRODUCT = "wt.pdmlink.PDMLinkProduct";
	
	private static String EXCLUDENAME = "更改通告,更改请求,升级请求,设计禁用,设计更改单,非FAE物料成熟度3升级报告";
	
	private static String WTPARTFOLDER = "零部件";
	
	public ProductFolderReportHelper()
	{
	}

	public void generateReport(HttpServletResponse response) throws WTException, WTPropertyVetoException
	{
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
			String currentDate = dateFormat.format((Date) new Timestamp(System.currentTimeMillis()));
			String fileName = "PLM_ProductFolder_" + currentDate + ".xlsx";
			
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
			
		} catch (IOException e)
		{
			log.debug(e.getLocalizedMessage());
			throw new WTException(e, e.getLocalizedMessage());
		} finally
		{
			SessionServerHelper.manager.setAccessEnforced(flag);
		}
	}

	private Workbook generateExcel(String fileName) throws WTException {
		
		String filePathName = WCLocationConstants.WT_CODEBASE+File.separator
				+"com"+File.separator+"catl"+File.separator
				+"checkPDFData"+File.separator+"productFolder_template.xlsx";
		
		Excel2007Handler excelHander;
		try {
			
			excelHander = new Excel2007Handler(filePathName);
			
			//获取所有产品库名称
			Map<String, PDMLinkProduct> productMap = getPDMLinkProduct();
			
			//获取文件夹对应的成品PN信息
			Map<String, String> folderPNMap = getFolderPNMap();
			
//			List<String> list = new ArrayList<String>();
//			for (Map.Entry<String, String> entry : folderPNMap.entrySet()) {
//				
//				String folderID = entry.getKey();
//				list.add(folderID);
//			}
			
			//获取产品库下文件夹信息
			List<List<String>> folderlist = queryProductPartFolderList();
			
			for (int i=0; i<folderlist.size(); i++){
				
				List<String> rowlist = folderlist.get(i);
				for (int j=0; j<rowlist.size(); j++){
					
					if (j == 3){//文件夹ID
						String folderID = rowlist.get(j);
						if (folderPNMap.get(folderID) != null){
//							list.remove(folderID);
							
							excelHander.setStringValue(i+1, j, folderPNMap.get(folderID));
							
//							excelHander.setStringValue(i+1, 7, folderPNMap.get(folderID).split(",").length + "");
						} else {
							excelHander.setStringValue(i+1, j, "");
						}
					} else {
						
						if (j == 1){//产品库名称
							String productName = rowlist.get(j);
							
							if (productMap.containsKey(productName)){
								productMap.remove(productName);
							}
						}
						excelHander.setStringValue(i+1, j, rowlist.get(j));
					}
				}
			}
			
//			for (String str : list){
//				System.out.println(folderPNMap.get(str));
//			}
			
			int i = folderlist.size();
			for (Map.Entry<String, PDMLinkProduct> entry : productMap.entrySet()) {
				
				i++;
				String productName = entry.getKey();
				PDMLinkProduct product = entry.getValue();
				excelHander.setStringValue(i, 0, product.getContainerName());
				excelHander.setStringValue(i, 1, productName);
				
			}
			
			return excelHander.getWorkbook();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static List<List<String>> queryProductPartFolderList() throws WTException {
    	
		List<List<String>> folderlist = new ArrayList<List<String>>();
		
		String[] excludeNameArr = EXCLUDENAME.split(",");
        QuerySpec qs = new QuerySpec(SubFolder.class);
        qs.appendSearchCondition(new SearchCondition(SubFolder.class, SubFolder.CONTAINER_REFERENCE + ".key.classname", SearchCondition.EQUAL, PDMLINKPRODUCT));
        qs.appendAnd();
        qs.appendWhere(new SearchCondition(new ClassAttribute(SubFolder.class, SubFolder.NAME),SearchCondition.NOT_IN, new ArrayExpression(excludeNameArr)), new int[]{0});
        qs.appendAnd();
        qs.appendWhere(new SearchCondition(SubFolder.class,SubFolder.FOLDERING_INFO + ".parentFolder.key.id",SearchCondition.EQUAL, 0L));
        
        qs.appendOrderBy(SubFolder.class, SubFolder.NAME, false);
        
        QueryResult qr = PersistenceHelper.manager.find(qs);
        List<String> list;
        
        List<String> sortlist = new ArrayList<String>();
        Map<String, List<List<String>>> containerFolderMap = new HashMap<String, List<List<String>>>();
        while(qr.hasMoreElements()){
        	SubFolder folder = (SubFolder)qr.nextElement(); 
            QueryResult subFolders = FolderHelper.service.findSubFolders(folder);
            while(subFolders.hasMoreElements()){
            	SubFolder subFolder = (SubFolder)subFolders.nextElement();
            	String subFolderName = subFolder.getName();
            	if(WTPARTFOLDER.equals(subFolderName)){
            		
            		list = new ArrayList<String>();
            		list.add(folder.getContainer().getContainerName());
            		String containerName = folder.getContainerDisplayName();
            		list.add(folder.getContainerDisplayName());
            		list.add(folder.getName());
            		list.add(subFolder.getPersistInfo().getObjectIdentifier().getId() + "");
            		list.add(folder.getDescription());
            		
            		
            		if (!sortlist.contains(containerName)){
            			sortlist.add(containerName);
            		}
            		
            		if (containerFolderMap.containsKey(containerName)){
            			containerFolderMap.get(containerName).add(list);
            		} else {
            			
            			List<List<String>> list2 = new ArrayList<List<String>>();
            			list2.add(list);
            			containerFolderMap.put(containerName, list2);
            		}
            	}
            }
        }
        Collections.sort(sortlist);
        
        for (String str : sortlist){
        	folderlist.addAll(containerFolderMap.get(str));
        }
        
        return folderlist;
    }
	
	static Map<String, String> getFolderPNMap() throws WTException {
    	
		Map<String, String> folderPNMap = new HashMap<String, String>();
		
		QuerySpec qs= new QuerySpec(WTPartMaster.class);
		qs.appendWhere(new SearchCondition(WTPartMaster.class, WTPartMaster.NUMBER, SearchCondition.LIKE, "P%"), new int[] {0});
		QueryResult qr = PersistenceHelper.manager.find(qs);
		List<String> PNList=new ArrayList<String>();
		while(qr.hasMoreElements()){			
			WTPartMaster wtpartmaster = (WTPartMaster) qr.nextElement();
			if(!PNList.contains(wtpartmaster.getNumber())){
				WTPart wtpart=PartUtil.getLastestWTPartByNumber(wtpartmaster.getNumber());
				PNList.add(wtpart.getNumber());
				FolderingInfo  folderingInfo = wtpart.getFolderingInfo();
				String folderID = folderingInfo.getParentFolder().getObjectId().getId() + "";
				
				if(folderPNMap.containsKey(folderID)){
					folderPNMap.put(folderID, folderPNMap.get(folderID) + "," +wtpart.getNumber());
				} else {
					folderPNMap.put(folderID, wtpart.getNumber());
				}
			}
		}
		return folderPNMap;
	}
	
	protected Map<String, PDMLinkProduct> getPDMLinkProduct() throws WTException {

		Map<String, PDMLinkProduct> pMap = new HashMap<String, PDMLinkProduct>();
		QuerySpec qs = new QuerySpec(PDMLinkProduct.class);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		PDMLinkProduct product = null;
		while (qr.hasMoreElements()) {
			product = (PDMLinkProduct) qr.nextElement();
			pMap.put(product.getName(), product);
		}
		return pMap;
	}
}
