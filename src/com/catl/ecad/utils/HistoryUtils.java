package com.catl.ecad.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.catl.cadence.util.NodeUtil;
import com.catl.ecad.load.LoadDataUtils;

import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.doc.WTDocumentMasterIdentity;
import wt.epm.EPMDocument;
import wt.fc.IdentityHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.part.WTPart;
import wt.pom.PersistenceException;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionControlHelper;

public class HistoryUtils implements RemoteAccess{
	
	public static String PATH;
	public static String EXCELPATH;
	static{
		try {
			WTProperties wtProperties=WTProperties.getLocalProperties();
			PATH=wtProperties.getProperty("wt.home", "UTF-8")
					+ File.separator + "codebase" + File.separator + "com"
					+ File.separator + "catl" + File.separator + "ecad"
					+ File.separator + "utils"
					+ File.separator;
			EXCELPATH=PATH+ECADConst.ECAD_EXCEL_PATH;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws WTException, RemoteException, InvocationTargetException {
		//getClfNumber();
//		WTPart part=(WTPart) new ReferenceFactory().getReference("VR:wt.part.WTPart:199634").getObject();
//		System.out.println("docment--------->"+ECADutil.getDocByPart(part).getNumber());
		RemoteMethodServer rm=RemoteMethodServer.getDefault();
		rm.setUserName("wcadmin");
		rm.setPassword("wcadmin");
		SessionHelper.manager.setAuthenticatedPrincipal("wcadmin");
		 
		rm.invoke("setIBAValueForByPart", HistoryUtils.class.getName(), null, null, null );
		//setOrganization();
//		System.out.println("COMPONENTXLS========:"+COMPONENTXLS);
//		try {
//			setIBAValueForByPart();
//		} catch (WTPropertyVetoException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	/**
	 * 设置PCBA的IBA属性
	 * @param epm
	 * @param part
	 * @param attrs
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws RemoteException
	 */
	public static void setAttrs(WTPart part, Map attrs) throws WTException, WTPropertyVetoException, RemoteException{
		IBAUtility ibapart = new IBAUtility(part);
		
		Set keys = attrs.keySet();
		if(keys.size()>0){
			for(Object obj:keys){
				ibapart.setIBAValue((String)obj, (String)attrs.get(obj));
			}
		
			ibapart.updateAttributeContainer(part);
			ibapart.updateIBAHolder(part);
		}
		
	}
	
	/**
	 * 更新文档编号
	 * @param docNumber	文档编号
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void renumberDoc(WTDocument doc,String prefix) throws WTException, WTPropertyVetoException{
		
		//String docNumber = doc.getNumber();

		WTDocumentMaster master=(WTDocumentMaster) doc.getMaster();

		WTDocumentMasterIdentity identity=(WTDocumentMasterIdentity) master.getIdentificationObject();
		identity.setNumber(prefix);
		master =(WTDocumentMaster) IdentityHelper.service.changeIdentity(master,identity);
	}

	/**
	 * 获取所有物料组内部名称
	 * @return
	 * @throws WTException
	 */
	public static Map<String, List<String>> getClfNumber() throws WTException{
		Map<String, List<String>> map=new HashMap<>();
		List<String> pcbalist=new ArrayList<String>();
		List<String> pcblist=new ArrayList<String>();
		List<String> pcbacomlist=new ArrayList<String>();
		try {
			FileInputStream fis=new FileInputStream(new File(EXCELPATH));
			@SuppressWarnings("resource")
			XSSFWorkbook wb=new XSSFWorkbook(fis);
			XSSFSheet sheet=wb.getSheetAt(0);
			for(int j=0;j<5;j+=2){
				if(j==0 || j==2 ||j==4){
					for(int i =1;; i++){
						XSSFRow row=sheet.getRow(i);
						if(row!=null){
							String value=null;
							XSSFCell cell=row.getCell(j);
							if(cell==null){
								continue;
							}else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
								DecimalFormat df = new DecimalFormat("0");
								value=df.format(cell.getNumericCellValue());
							}else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
								value=cell.getStringCellValue();
								
							}
							if(value!=null){
								if(j==4){
									pcblist.add(value);
								}else if(j == 0){
									pcbalist.add(value);
								}else if(j == 2){
									pcbacomlist.add(value);
								}
							}
						}else{
							break;
						}
					}
				}
			}
			map.put("PCBA",pcbalist);
			map.put("PCB",pcblist);
			map.put("PCBACOM", pcbacomlist);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * 将PCBA的说明文档关联到其子件PCB
	 * @throws WTException
	 * @throws WTPropertyVetoException 
	 */
	public static void updateReferenceLink() throws WTException, WTPropertyVetoException{
		Map<WTPart, List<WTPart>> partList=new HashMap<>();
		List<WTPart> pcbPart=new ArrayList<>();
		Map<String,List<String>> map=getClfNumber();
		List<String> list=map.get("PCBA");
		List<String> pcbList=map.get("PCB");
		for(String value:list){
			QueryResult qr=NodeUtil.getAllPartsByCLFNodesName(value);
			while(qr.hasMoreElements()){
				WTPart part=(WTPart) qr.nextElement();
				List<WTPart> parts=ECADutil.getAllChildPart(part);
				partList.put(part, parts);
			}
		}
		for(String value:pcbList){
			QueryResult qr=NodeUtil.getAllPartsByCLFNodesName(value);
			while(qr.hasMoreElements()){
				WTPart part=(WTPart) qr.nextElement();
				pcbPart.add(part);
			}
		}

		for(Iterator it=partList.keySet().iterator();it.hasNext();){
			WTPart parentPart=(WTPart) it.next();
			List<WTPart> parts=partList.get(parentPart);
			boolean flag = true;
			if(parts.size()!=0){
				List<WTDocument> documents=ECADutil.getDocByPart(parentPart);
				ECADutil.removePCBADrawingLinks(parentPart);
				for(WTPart part:parts){
					if(pcbPart.contains(part)){
						EPMDocument schepm = CommonUtil.getEPMDocumentByNumber(parentPart.getNumber());
						EPMDocument pcbepm = CommonUtil.getEPMDocumentByNumber(part.getNumber());
						if(schepm != null && pcbepm != null){
							EPMUtil.createRefLink(schepm, pcbepm);
						}
						for(WTDocument document:documents){
							if(ECADutil.isPCBADrawing(document)){	
								if(ECADConst.DISABLE_FOR_DESIGN_STATE.equalsIgnoreCase(document.getLifeCycleState().toString())){
									System.out.println(ECADConst.DISABLE_FOR_DESIGN_STATE);
									flag = false;
								}else{
									ECADutil.createReferenceLink(document,part);	
								}							
							}							
						}
					}
				}
				if(flag){
					removeLinkOldVersion(parentPart);
				}
			}
		}
	}
	
	/**
	 * 移除所有老版本的说明关系
	 * @throws WTException 
	 * @throws PersistenceException 
	 */
	public static void removeLinkOldVersion(WTPart part) throws PersistenceException, WTException{
		QueryResult qr=VersionControlHelper.service.allIterationsOf(part.getMaster());
		while(qr.hasMoreElements()){
			WTPart wtPart=(WTPart) qr.nextElement();
			if(wtPart.equals(part)){
				continue;
			}
			ECADutil.removePCBADrawingLinks(wtPart);
		}
	}
	
	/**
	 * //给PCBA添加ECAD工程师组到团队成员
	 * @throws WTException
	 */
	public static void setOrganization() throws WTException{
		Map<String,List<String>> map=getClfNumber();
		List<String> list=map.get("PCBA");
		//List<String> pcbList=map.get("PCB");
		//list.addAll(pcbList);
		WTPrincipal principal=OrganizationServicesHelper.manager.getPrincipal(ECADConst.ECADGROUP);
		for(String value:list){
			QueryResult qr=NodeUtil.getAllPartsByCLFNodesName(value);
			while(qr.hasMoreElements()){
				WTPart part=(WTPart) qr.nextElement();
				if(principal instanceof WTGroup){
					WorkflowHelper.setTeamMember(part, principal);
				}
			}
		}
	}
	
	/**
	 * 获取需修改的元器件数据信息
	 * @return Map
	 * @throws IOException
	 */
	public static  Map<String, Map<String,String>> getPartInfoByXls(String path) throws IOException{
		Map<String, Map<String,String>> partMap=new HashMap<>();
		FileInputStream fis=new FileInputStream(new File(path));
		@SuppressWarnings("resource")
		XSSFWorkbook workbook=new XSSFWorkbook(fis);
		XSSFSheet sheet=workbook.getSheetAt(0);
		XSSFRow headRow = sheet.getRow(0);
		List<String> heads = new ArrayList<>();
		if(headRow != null){
			System.out.println(headRow.getPhysicalNumberOfCells());
			for(int r = 0; r< headRow.getPhysicalNumberOfCells(); r++){
				Cell cell = headRow.getCell(r);
				String value = LoadDataUtils.getCellValue(cell);
				if(StringUtils.isNotBlank(value)){
					System.out.println(value);
					heads.add(value);
				}else{
					break;
				}
			}
		}
		
		for(int i=1;;i++){
			String partNumber=null;
			Map<String, String> ibaMap=new HashMap<>();
			XSSFRow row=sheet.getRow(i);
			if(row==null){
				break;
			}
			for(int j=0;j<heads.size();j++){
				String value=null;
				XSSFCell cell=row.getCell(j);
				if(cell==null){
					continue;
				}else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
					DecimalFormat df = new DecimalFormat("0");
					value=df.format(cell.getNumericCellValue());
				}else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
					value=cell.getStringCellValue();
				}else if(cell.getCellType() == Cell.CELL_TYPE_FORMULA){
					value = String.valueOf(cell.getNumericCellValue());
				}
				
				if(StringUtils.isNotBlank(value)){
					if(j==0){
						System.out.println("***********************\nPartNumber"+"\t"+value);
						partNumber=value;
					}else{
						System.out.println(heads.get(j)+"\t"+value);
						ibaMap.put(heads.get(j), value);
					}
				}
			}
			if(partNumber==null){
				break;
			}else{
				partMap.put(partNumber, ibaMap);
			}
		}
		return partMap;
	}
	
	/**
	 * 更新系统相关部件的IBA属性
	 * @throws WTException
	 * @throws IOException
	 * @throws WTPropertyVetoException
	 */
	public static void setIBAValueForByPart(String path) throws WTException, IOException, WTPropertyVetoException{
		Map<String, Map<String,String>> partMap=getPartInfoByXls(path);
		for(Iterator<String> it=partMap.keySet().iterator();it.hasNext();){
			String partNumber=it.next();
			WTPart part=CommonUtil.getPartByNumber(partNumber);
			if(part!=null){
				IBAUtility utility=new IBAUtility(part);
				Map<String,String> ibaMap=partMap.get(partNumber);
				for(Iterator<String> itIba=ibaMap.keySet().iterator();itIba.hasNext();){
					String ibaName=itIba.next();
					utility.setIBAValue(ibaName, ibaMap.get(ibaName));
				}
				utility.updateAttributeContainer(part);
				utility.updateIBAHolder(part);
			}
		}
	}
}
