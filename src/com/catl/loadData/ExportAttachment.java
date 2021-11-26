package com.catl.loadData;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.catl.loadData.util.ExcelWriter;
import com.ptc.core.lwc.common.view.TypeDefinitionReadView;
import com.ptc.core.lwc.server.TypeDefinitionServiceHelper;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.common.impl.TypeIdentifierUtilityHelper;

import wt.content.ApplicationData;
import wt.content.ContentHolder;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.WTReference;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.pom.PersistenceException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.VersionControlHelper;

public class ExportAttachment implements RemoteAccess {

	public static void main(String[] args) throws RemoteException, InvocationTargetException {
		RemoteMethodServer ms = RemoteMethodServer.getDefault();
		ms.setUserName("dms");
		ms.setPassword("dms");
		try {
			SessionHelper.manager.setAuthenticatedPrincipal("dms");
			RemoteMethodServer.getDefault()
			.invoke("attachmentTotal",
					ExportAttachment.class.getName(), null, null,
					null);
		} catch (WTException e) {
			e.printStackTrace();
		}
	}
	
	public static void attachmentTotal() throws WTException, RemoteException{
		
		boolean enforced=SessionServerHelper.manager.setAccessEnforced(false);
		try {			
			String rootPath=WTProperties.getLocalProperties().getProperty("wt.home")+"/loadFiles/com/catl/dms";
			List<String[]> logs=new ArrayList<String[]>();
			
			Vector<WTPart> wtparts=getPart();
			System.out.println("part.size.total====="+wtparts.size());
			
			String pdf_state="无";
			String dwg_state="无";
			String dwg_pdf_state="无";
			
			for(WTPart part:wtparts){
				
				QueryResult qr1 =WTPartHelper.service.getDescribedByWTDocuments(part);
				while(qr1.hasMoreElements()){
					WTDocument doc=(WTDocument)qr1.nextElement();
					TypeIdentifier type = TypeIdentifierUtilityHelper.service.getTypeIdentifier(doc);
			    	TypeDefinitionReadView trv = TypeDefinitionServiceHelper.service.getTypeDefView(type);
					if(doc.getNumber().equals(part.getNumber()) && trv.getName().equals("com.CATLBattery.autocadDrawing")){
						dwg_state="有";					
						QueryResult qr3 = wt.content.ContentHelper.service
								.getContentsByRole(doc,
										wt.content.ContentRoleType.SECONDARY);
						while (qr3.hasMoreElements()) {
							ApplicationData app = (ApplicationData) qr3.nextElement();
							if(app.getFileName().toLowerCase().equals(part.getNumber()+".pdf")){
								dwg_pdf_state="有";
								break;
							}
						}
					}
				}
				
				QueryResult qr = wt.content.ContentHelper.service
						.getContentsByRole(part,
								wt.content.ContentRoleType.SECONDARY);
				while (qr.hasMoreElements()) {
					ApplicationData app = (ApplicationData) qr.nextElement();
					if(app.getFileName().toLowerCase().equals(part.getNumber()+".pdf")){
						pdf_state="有";
						if(pdf_state.equals("有") && dwg_state.equals("有")){
							PersistenceHelper.manager.delete(app);		//Part有附件同时关联了AutoCAD
							logs.add(new String[]{part.getLifeCycleState().getDisplay(),part.getNumber(),"","",pdf_state,dwg_state,dwg_pdf_state,"删除Part附件"});
							pdf_state="无";
						}
						break;
					}
				}
				
				if(!(pdf_state.equals("无") && dwg_state.equals("无"))){
					String containerName=part.getContainer().getName();
					String partPath=part.getFolderPath();
					System.out.println("partPath:"+partPath);
					String projectName="";
					if(!containerName.equals("电芯") && !containerName.equals("电子电气件") && !containerName.equals("紧固件") && !containerName.equals("原材料")){
						int i1=partPath.indexOf("Default");
			    		int i2=partPath.indexOf("零部件");
			    		if(i1>-1 && i2>-1){
			    			projectName=partPath.substring(i1+8,i2-1);
			    		}
					}else{
						String folder=partPath.substring(0,partPath.lastIndexOf("/"));
						projectName=folder.substring(folder.lastIndexOf("/")+1);
					}
					logs.add(new String[]{part.getLifeCycleState().getDisplay(),part.getNumber(),part.getName(),projectName,pdf_state,dwg_state,dwg_pdf_state,""});
				}
				
				pdf_state="无";
				dwg_state="无";
				dwg_pdf_state="无";
			}
			
			ExcelWriter writer = new ExcelWriter();
			boolean flag = writer.exportExcelList(rootPath+"/PLM系统DWG-PDF统计.xlsx","PLM系统DWG-PDF统计", new String[]{"阶段","PN","名称","项目","PDF","DWG","DWG-PDF","备注"}, logs);
			System.out.println("PLM系统DWG-PDF统计.xlsx flag="+flag);
			logs=new ArrayList<String[]>();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			SessionServerHelper.manager.setAccessEnforced(enforced);
		}
	}	
	
	public static Persistable getPersistableByOid(String oid) throws WTException {
		WTObject obj = null;
		ReferenceFactory referencefactory = new ReferenceFactory();
		WTReference wtreference = referencefactory.getReference(oid);
		if (wtreference.getObject() != null) {
			obj = (WTObject) wtreference.getObject();
		}
		return obj;
	}
	
	static Vector<WTPart> getPart() throws WTException {
		Vector<WTPart> wtparts=new Vector<WTPart>();
		
		QuerySpec qs= new QuerySpec(WTPart.class);		
		QueryResult qr = PersistenceHelper.manager.find(qs);
		List<String> partNumber=new ArrayList<String>();
		while(qr.hasMoreElements()){			
			WTPart wtpart = (WTPart) qr.nextElement();
			if(!partNumber.contains(wtpart.getNumber())){
				wtpart=getLatestPart((WTPartMaster) wtpart.getMaster());
				wtparts.add(wtpart);
				partNumber.add(wtpart.getNumber());
			}
		}
		return wtparts;
	}
	
	static WTPart getLatestPart(WTPartMaster partMaster) throws PersistenceException, WTException{
		WTPart part = null;
		if (partMaster != null) {
			QueryResult qr= VersionControlHelper.service
						.allVersionsOf(partMaster);
			if (qr != null && qr.hasMoreElements()) {
				part = (WTPart) qr.nextElement();
			}
		}
		return part;
	}
	
	static WTDocument getLatestDoc(WTDocumentMaster docMaster) throws PersistenceException, WTException{
		WTDocument doc = null;
		
		if (docMaster != null) {
			QueryResult qr= VersionControlHelper.service
						.allVersionsOf(docMaster);
			if (qr != null && qr.hasMoreElements()) {
				doc = (WTDocument) qr.nextElement();
			}
		}
		
		return doc;
	}

}
