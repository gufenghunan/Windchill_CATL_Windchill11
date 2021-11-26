package com.catl.change.report.ecn;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import wt.change2.ChangeException2;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.config.LatestConfigSpec;
import wt.vc.struct.StructHelper;

import com.catl.common.util.DocUtil;
import com.catl.common.util.PartUtil;
import com.catl.loadData.StrUtils;

public class ExportECNAffectTargetsParent2Excel {

	private static Logger logger=Logger.getLogger(ExportECNAffectTargetsParent2Excel.class.getName());
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}   

	public  static File doexportExcel(String oid) throws IOException, ChangeException2, WTException
	{
		//String oid = request.getParameter("oid");
		logger.debug("oid=="+oid);
		Persistable persistable=PartUtil.getPersistableByOid(oid);
		ArrayList<ExportECAffectedReportModel> list=new ArrayList<ExportECAffectedReportModel>();
		if(persistable instanceof WTChangeOrder2)
		{
			WTChangeOrder2 ecn=(WTChangeOrder2)persistable;
			logger.debug("ecn number===="+ecn.getNumber());
			QueryResult ecaQResult= ChangeHelper2.service.getChangeActivities(ecn);
			list=getECNchangeableParentpartinfo(ecaQResult);
		}
		if (persistable instanceof WTChangeRequest2) {
			WTChangeRequest2 ecr=(WTChangeRequest2)persistable;
			logger.debug("ecr number===="+ecr.getNumber());
			list=getECNchangeableParentpartinfo(ecr);
		}

		HSSFWorkbook  hssfworkbook2 =new HSSFWorkbook();
		HSSFSheet parthssfsheet = hssfworkbook2.createSheet(ECExportConstant.ECNPartAffectdTargetsParent);
		HSSFSheet epmhssfsheet = hssfworkbook2.createSheet(ECExportConstant.ECNEPmdocAffectdTargetsParent);
		try {
			writeInfoToSheet(list, parthssfsheet,epmhssfsheet);
			logger.debug("hssfworkbook  =="+hssfworkbook2.getNumberOfNames());
			FileOutputStream fOut = new FileOutputStream(getDefineExportPath()+getfilename(oid));
			hssfworkbook2.write(fOut);
			fOut.flush();
			fOut.close();
		} catch (IOException e) {
			logger.debug("ddddddddddddddddddddddddddddddddddd");
			e.printStackTrace();
		}
		File file = new File(getDefineExportPath()+getfilename(oid));
		logger.debug("file==="+file.getAbsolutePath());

		return file;
	}
	public static String getfilename(String oid)
	{
		String fileName ="";
		Persistable persistable=PartUtil.getPersistableByOid(oid);
		if (persistable instanceof WTChangeRequest2)
		{
			WTChangeRequest2 ecRequest2=(WTChangeRequest2)persistable;
			fileName= ecRequest2.getNumber() + ".xls";
		}
		if (persistable instanceof WTChangeOrder2)
		{
			WTChangeOrder2 ecn=(WTChangeOrder2)persistable;
			fileName= ecn.getNumber() + ".xls";
		}
		return fileName;
		
	}

	public static HSSFWorkbook createHSSFWorkbook(String filePath) throws IOException
	{
		HSSFWorkbook hssfworkbook = new HSSFWorkbook();
		(new File(filePath)).mkdirs();
		return hssfworkbook;
	}
	public static void writeInfoToSheet(ArrayList<ExportECAffectedReportModel> list, HSSFSheet parthssfSheet, HSSFSheet epmhssfSheet
			) throws IOException
	{
	    int partIndex=0;
        int epmIndex=0;
		HSSFRow partfirstrow = parthssfSheet.createRow(partIndex++);
		partfirstrow.createCell(0).setCellValue("ID");
		partfirstrow.createCell(partfirstrow.getLastCellNum()).setCellValue(ECExportConstant.affectedpartnumber);
		partfirstrow.createCell(partfirstrow.getLastCellNum()).setCellValue(ECExportConstant.affectedpartname);
		partfirstrow.createCell(partfirstrow.getLastCellNum()).setCellValue(ECExportConstant.affectedParentpartnumber);
		partfirstrow.createCell(partfirstrow.getLastCellNum()).setCellValue(ECExportConstant.affectedParentpartname);
		partfirstrow.createCell(partfirstrow.getLastCellNum()).setCellValue(ECExportConstant.affectedParentpartversion);
		partfirstrow.createCell(partfirstrow.getLastCellNum()).setCellValue(ECExportConstant.affectedParentpartcontainer);
		partfirstrow.createCell(partfirstrow.getLastCellNum()).setCellValue(ECExportConstant.affectedParentpartislastversion);

		
		HSSFRow empfirstrow = epmhssfSheet.createRow(epmIndex++);
		empfirstrow.createCell(0).setCellValue("ID");
		empfirstrow.createCell(empfirstrow.getLastCellNum()).setCellValue(ECExportConstant.modelname);
		empfirstrow.createCell(empfirstrow.getLastCellNum()).setCellValue(ECExportConstant.modelnumber);
		empfirstrow.createCell(empfirstrow.getLastCellNum()).setCellValue(ECExportConstant.modelparentnumber);
		empfirstrow.createCell(empfirstrow.getLastCellNum()).setCellValue(ECExportConstant.modelparentname);
		empfirstrow.createCell(empfirstrow.getLastCellNum()).setCellValue(ECExportConstant.modelparentversion);
		empfirstrow.createCell(empfirstrow.getLastCellNum()).setCellValue(ECExportConstant.modelparentcontainer);
		empfirstrow.createCell(empfirstrow.getLastCellNum()).setCellValue(ECExportConstant.modelparentislastversion);

		//hssfsheet.addMergedRegion(new Region(8, (short) 0, 9, (short) 1));
		
		for (int i = 0; i < list.size(); i++)
		{
			ExportECAffectedReportModel model = (ExportECAffectedReportModel) list.get(i);
			logger.debug("MESS  "+model.getNumber()+"......."+model.getEpmnumber());
			if(!StrUtils.isEmpty(model.getNumber())){
				HSSFRow partrow = parthssfSheet.createRow(partIndex);
				partrow.createCell(0).setCellValue(partIndex);
				partrow.createCell(partrow.getLastCellNum()).setCellValue(model.getNumber());
				partrow.createCell(partrow.getLastCellNum()).setCellValue(model.getName());
				partrow.createCell(partrow.getLastCellNum()).setCellValue(model.getParentnumber());
				partrow.createCell(partrow.getLastCellNum()).setCellValue(model.getParentname());
				partrow.createCell(partrow.getLastCellNum()).setCellValue(model.getParentversion());
				partrow.createCell(partrow.getLastCellNum()).setCellValue(model.getParentcontainer());
				partrow.createCell(partrow.getLastCellNum()).setCellValue(model.getIslastversion());
				partIndex++;
			}else if(!StrUtils.isEmpty(model.getEpmnumber())){
				HSSFRow epmrow = epmhssfSheet.createRow(epmIndex);
				epmrow.createCell(0).setCellValue(epmIndex);
				epmrow.createCell(epmrow.getLastCellNum()).setCellValue(model.getEpmnumber());
				epmrow.createCell(epmrow.getLastCellNum()).setCellValue(model.getEpmname());
				epmrow.createCell(epmrow.getLastCellNum()).setCellValue(model.getEpmparentnumber());
				epmrow.createCell(epmrow.getLastCellNum()).setCellValue(model.getEpmparentname());
				epmrow.createCell(epmrow.getLastCellNum()).setCellValue(model.getEpmparentversion());
				epmrow.createCell(epmrow.getLastCellNum()).setCellValue(model.getEpmparentcontainer());
				epmrow.createCell(epmrow.getLastCellNum()).setCellValue(model.getEpmislastversion());
				epmIndex++;
			}
		}
	}

	
	public static String getDefineExportPath() throws IOException
	{
		WTProperties props = WTProperties.getLocalProperties();
		String base = props.getProperty("wt.codebase.location");
		return base + File.separator+"temp"+File.separator;
	}

	public static ArrayList<ExportECAffectedReportModel> getECNchangeableParentpartinfo(WTChangeRequest2 ecr) throws WTException
	{
		ArrayList<ExportECAffectedReportModel> parentList=new ArrayList<ExportECAffectedReportModel>();
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		try{
			QueryResult targetrResult=new QueryResult();

			QueryResult epmResult=null;
			try {
				targetrResult=ChangeHelper2.service.getChangeables(ecr);
			} catch (ChangeException2 e) {
				logger.debug(ecr.getNumber()+"get changeablesafter failed!");
				e.printStackTrace();
			} catch (WTException e) {
				logger.debug(ecr.getNumber()+"get changeablesafter failed!");
				e.printStackTrace();
			}
			while (targetrResult.hasMoreElements()) {
				Object object = (Object) targetrResult.nextElement();
				if(object instanceof WTPart)
				{
					WTPart part=(WTPart)object;
					QueryResult parentResult=null;
					try {
					 parentResult=WTPartHelper.service.getUsedByWTParts((WTPartMaster) part.getMaster());
					} catch (WTException e) {
						logger.debug(ecr.getNumber()+"get changeablesafter failed!");
						e.printStackTrace();
					}
					if(parentResult != null && parentResult.size() > 0){
						parentResult = (new LatestConfigSpec()).process(parentResult);
						while (parentResult.hasMoreElements()) {
							WTPart parentWtPart = (WTPart) parentResult.nextElement();
							ExportECAffectedReportModel model=new ExportECAffectedReportModel();
							model.setName(part.getName());
							model.setNumber(part.getNumber());
							model.setParentname(parentWtPart.getName());
							model.setParentnumber(parentWtPart.getNumber());
							model.setParentcontainer(parentWtPart.getContainerName());
							model.setParentversion(getlastVersion(parentWtPart));
							try {
								if(isLastVersion(parentWtPart))
								{
									model.setIslastversion("YES");
								}else {
									model.setIslastversion("NO");
								}
							} catch (WTException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							parentList.add(model);
						}
					}
					else {
						ExportECAffectedReportModel model=new ExportECAffectedReportModel();
						model.setName(part.getName());
						model.setNumber(part.getNumber());
						parentList.add(model);
					}
				}else if(object instanceof EPMDocument){
		
						EPMDocument epm=(EPMDocument)object;				
						try {	
						 epmResult=StructHelper.service.navigateUsedBy(epm.getMaster());
						 logger.debug("epm use by size=="+epmResult.size());
						} catch (WTException e) {
							logger.debug(ecr.getNumber()+"get epm changeablesafter failed!");
							e.printStackTrace();
						}
						HashSet parentepmset = new HashSet();
						while (epmResult.hasMoreElements()) {
							EPMDocument parentepm = (EPMDocument) epmResult.nextElement();
		                    if(!PartUtil.existGreaterVersion(parentepm)&&!parentepmset.contains(parentepm))
		                    {
			                    parentepmset .add(parentepm);
								ExportECAffectedReportModel model=new ExportECAffectedReportModel();
								model.setEpmname(parentepm.getName());
								model.setEpmnumber(parentepm.getNumber());
								model.setEpmparentname(parentepm.getName());
								model.setEpmparentnumber(parentepm.getNumber());
								model.setEpmparentcontainer(parentepm.getContainerName());
								String parentepmversion=getlastVersion(epm);
								model.setEpmparentversion(parentepmversion);
								try {
									if(isLastVersion(parentepm))
									{
										model.setEpmislastversion("YES");
									}else {
										model.setEpmislastversion("NO");
									}
								} catch (WTException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								parentList.add(model);
							}
						}
				}
			}
		}
		finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		
		return parentList;
	}

	public static ArrayList<ExportECAffectedReportModel> getECNchangeableParentpartinfo(QueryResult ecaResult) throws WTException
	{
		
		ArrayList<ExportECAffectedReportModel> parentList=new ArrayList<ExportECAffectedReportModel>();
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		try{
			while (ecaResult.hasMoreElements()) {
				WTChangeActivity2 eca = (WTChangeActivity2) ecaResult.nextElement();
				QueryResult targetrResult=new QueryResult();
		
				QueryResult epmResult=null;
				try {
					targetrResult=ChangeHelper2.service.getChangeablesBefore(eca);
				} catch (ChangeException2 e) {
					logger.debug(eca.getNumber()+"get changeablesafter failed!");
					e.printStackTrace();
				} catch (WTException e) {
					logger.debug(eca.getNumber()+"get changeablesafter failed!");
					e.printStackTrace();
				}
				while (targetrResult.hasMoreElements()) {
					Object object = (Object) targetrResult.nextElement();
					if(object instanceof WTPart)
					{
						WTPart part=(WTPart)object;
						QueryResult parentResult=null;
						try {
						 parentResult=WTPartHelper.service.getUsedByWTParts((WTPartMaster) part.getMaster());
						 System.out.println("parentResult size() ="+parentResult.size());
						} catch (WTException e) {
							logger.debug(eca.getNumber()+"get changeablesafter failed!");
							e.printStackTrace();
						}
						if(parentResult != null && parentResult.size() > 0){
							parentResult = (new LatestConfigSpec()).process(parentResult);
							while (parentResult.hasMoreElements()) {
								WTPart parentWtPart = (WTPart) parentResult.nextElement();
								ExportECAffectedReportModel model=new ExportECAffectedReportModel();
								model.setName(part.getName());
								model.setNumber(part.getNumber());
								model.setParentname(parentWtPart.getName());
								model.setParentnumber(parentWtPart.getNumber());
								model.setParentcontainer(parentWtPart.getContainerName());
								model.setParentversion(getlastVersion(parentWtPart));
								try {
									if(isLastVersion(parentWtPart))
									{
										model.setIslastversion("YES");
									}else {
										model.setIslastversion("NO");
									}
								} catch (WTException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								parentList.add(model);
							}
						}
						else {
							ExportECAffectedReportModel model=new ExportECAffectedReportModel();
							model.setName(part.getName());
							model.setNumber(part.getNumber());
							parentList.add(model);
						}
						
					}else if(object instanceof EPMDocument){
					    System.out.println("="+object);
						EPMDocument epm=(EPMDocument)object;			
						 System.out.println("epm number="+epm.getNumber());
						try {	
						    System.out.println("epm number="+epm.getNumber());
						 epmResult=StructHelper.service.navigateUsedBy(epm.getMaster());
						 logger.debug(".......epm use by size=="+epmResult.size());
						} catch (WTException e) {
							logger.debug(eca.getNumber()+"get epm changeablesafter failed!");
							e.printStackTrace();
						}
						while (epmResult.hasMoreElements()) {
							EPMDocument parentepm = (EPMDocument) epmResult.nextElement();
		                    if(!PartUtil.existGreaterVersion(parentepm))
		                    {
								ExportECAffectedReportModel model=new ExportECAffectedReportModel();
								model.setEpmname(parentepm.getName());
								model.setEpmnumber(parentepm.getNumber());
								model.setEpmparentname(parentepm.getName());
								model.setEpmparentnumber(parentepm.getNumber());
								model.setEpmparentcontainer(parentepm.getContainerName());
								String parentepmversion=getlastVersion(epm);
								model.setEpmparentversion(parentepmversion);
								try {
									if(isLastVersion(parentepm))
									{
										model.setEpmislastversion("YES");
									}else {
										model.setEpmislastversion("NO");
									}
								} catch (WTException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								parentList.add(model);
							}
						}
					}
				}
			}
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return parentList;
	}
	  public static Boolean isLastVersion(RevisionControlled revisionControlled) throws WTException
	  {
		  Boolean fagBoolean=false;
			String versionString =getlastVersion(revisionControlled);
			String versionString1 ="";
             if(revisionControlled instanceof WTPart)
             {
            	 WTPart part=(WTPart)revisionControlled;
            	 WTPart newPart=PartUtil.getLastestWTPartByNumber(part.getNumber());
            	 versionString1 =getlastVersion(newPart);
             }
             if(revisionControlled instanceof WTDocument)
             {
            	 WTDocument doc=(WTDocument)revisionControlled;
            	 WTDocument newDocument=DocUtil.getLatestWTDocument(doc.getNumber());
            	 versionString1 =getlastVersion(newDocument); 
             }
             if(revisionControlled instanceof EPMDocument)
             {
            	 EPMDocument epm=(EPMDocument)revisionControlled;
            	 EPMDocument newepm=DocUtil.getLastestEPMDocumentByNumber(epm.getNumber());
            	 versionString1 =getlastVersion(newepm);
             }
             logger.debug("targets version=="+versionString);
             logger.debug("lastversion=="+versionString1);
			if(versionString.equals(versionString1))
			{
				fagBoolean=true;
			}else{
				fagBoolean=false;
			}
			
			return fagBoolean;
		  
	  }
    
	
	public static String getlastVersion(RevisionControlled revisionControlled)
	{
		
		String versionString="";
		versionString=revisionControlled.getVersionIdentifier().getValue()+"."+revisionControlled.getIterationIdentifier().getValue();			
		return versionString;
	}

}
