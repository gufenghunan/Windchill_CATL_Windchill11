package com.catl.loadData;

import java.beans.PropertyVetoException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.catl.loadData.util.ExcelWriter;
import com.ptc.core.foundation.container.common.FdnWTContainerHelper;
import com.ptc.core.lwc.common.view.TypeDefinitionReadView;
import com.ptc.core.lwc.server.TypeDefinitionServiceHelper;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.common.impl.TypeIdentifierUtilityHelper;
import com.ptc.windchill.enterprise.nativeapp.server.ApplicationIntegrationFactory;

import wt.content.ApplicationData;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.epm.EPMDocument;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.WTReference;
import wt.folder.Folder;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.State;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartMaster;
import wt.pom.PersistenceException;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionControlHelper;
import wt.vc.wip.WorkInProgressException;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

public class LoadAttachment implements RemoteAccess {
	private static String rootPath;
	static{
		try {
			rootPath=WTProperties.getLocalProperties().getProperty("wt.home")+"/loadFiles/com/catl/dms";
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	
	public static List<String[]> logs=new ArrayList<String[]>();
	
	public static void main(String[] args) throws InvocationTargetException, IOException {
		/*Scanner scanner = new Scanner(System.in);
		String methodName = scanner.nextLine();
		System.out.println(methodName);*/
		String filePath=WTProperties.getLocalProperties().getProperty("wt.home")+"/loadFiles/com/catl/dms/loadDataPDF";
		Class argTypes[] = (new Class[] { String.class, });
		Object svrArgs[] = (new Object[] { filePath });
		
		RemoteMethodServer ms = RemoteMethodServer.getDefault();
		ms.setUserName("dms");
		ms.setPassword("dms");
		try {
			SessionHelper.manager.setAuthenticatedPrincipal("dms");			
			RemoteMethodServer.getDefault()
			.invoke("loadCADPDF",
					LoadAttachment.class.getName(), null, argTypes,
					svrArgs);
		} catch (WTException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void updateAttachment(Map<String,File> attachments) throws WTException, FileNotFoundException, PropertyVetoException, IOException{
				
		for(String key:attachments.keySet()){
			
			System.out.println(".......PDF.......:"+key);
			
			WTDocument doc=getDoc(key);
			if(doc!=null){				
				TypeIdentifier type = TypeIdentifierUtilityHelper.service.getTypeIdentifier(doc);
		    	TypeDefinitionReadView trv = TypeDefinitionServiceHelper.service.getTypeDefView(type);
		    	//System.out.println("trv.getName:"+trv.getName());
				
		    	if(trv.getName().equals("com.CATLBattery.autocadDrawing")){
					ContentHolder ch = (ContentHolder) doc;
					QueryResult qr = wt.content.ContentHelper.service
							.getContentsByRole(doc,
									wt.content.ContentRoleType.SECONDARY);
					boolean exist=false;
					while (qr.hasMoreElements()) {
						ApplicationData app = (ApplicationData) qr.nextElement();
						
						if(app.getFileName().equals(attachments.get(key).getName())){
							PersistenceHelper.manager.delete(app);
							logs.add(new String[]{"PDF","线束",key,attachments.get(key).getName(),"更新PDF"});
							exist=true;
							break;
						}
					}
					
					if(!exist){
						logs.add(new String[]{"PDF","线束",key,attachments.get(key).getName(),"新增附件"});
					}
					
					ApplicationData newapp = ApplicationData.newApplicationData(ch);
					newapp.setRole(ContentRoleType.SECONDARY);
					String attachmentPath = attachments.get(key).getAbsolutePath();
					//System.out.println("attachmentPath:"+attachmentPath);
					newapp = ContentServerHelper.service.updateContent(ch, newapp, attachmentPath);
					newapp = (ApplicationData) PersistenceHelper.manager.save(newapp);
		    	}
			}else{
				WTPart part=getPart(key);
				if(part!=null){
					ContentHolder ch = (ContentHolder) part;
					QueryResult qr = wt.content.ContentHelper.service
							.getContentsByRole(part,
									wt.content.ContentRoleType.SECONDARY);
					boolean exist=false;
					while (qr.hasMoreElements()) {
						ApplicationData app = (ApplicationData) qr.nextElement();
						
						if(app.getFileName().equals(attachments.get(key).getName())){
							logs.add(new String[]{"PDF","CATIA",key,attachments.get(key).getName(),"更新PDF"});
							PersistenceHelper.manager.delete(app);
							exist=true;
							break;
						}
					}
					
					if(!exist){
						logs.add(new String[]{"PDF","CATIA",key,attachments.get(key).getName(),"新增附件"});
					}
					
					ApplicationData newapp = ApplicationData.newApplicationData(ch);
					newapp.setRole(ContentRoleType.SECONDARY);
					String attachmentPath = attachments.get(key).getAbsolutePath();
					//System.out.println("attachmentPath:"+attachmentPath);
					newapp = ContentServerHelper.service.updateContent(ch, newapp, attachmentPath);
					newapp = (ApplicationData) PersistenceHelper.manager.save(newapp);
					
				}else{
					logs.add(new String[]{"PDF","CATIA",key,attachments.get(key).getName(),"系统中PN不存在"});
				}
			}			
		}
	}
	
	/**
	 * 待导入的Part附件
	 * @param filePath
	 * @return
	 * @throws Exception 
	 */
	public static void loadCADPDF(String filePath) throws Exception{
		Transaction trx = null;
		try{
			trx = new Transaction();
			trx.start();
			
			Map<String,File> pdfdoc=new HashMap<String, File>();
			Map<String,File> caddoc=new HashMap<String, File>();
			getAllFile(pdfdoc,caddoc,new File(filePath));
			
			for(String key:caddoc.keySet()){
				System.out.println(".......DWG.......:"+key);
				
				//dwg关联的PN不存在 不上传
				WTPart part=getPart(key);
				if(part!=null){
					WTDocument doc=getDoc(key);
					//判断是否关联了AutoCAD文档，不存在则创建文档，存在就更新内容		
			    	
					if(doc!=null){
						TypeIdentifier type = TypeIdentifierUtilityHelper.service.getTypeIdentifier(doc);
				    	TypeDefinitionReadView trv = TypeDefinitionServiceHelper.service.getTypeDefView(type);
				    	//System.out.println("trv.getName:"+trv.getName());
						
				    	if(trv.getName().equals("com.CATLBattery.autocadDrawing")){
				    		ContentHolder ch = (ContentHolder) doc;
							QueryResult qr = wt.content.ContentHelper.service
									.getContentsByRole(doc,
											wt.content.ContentRoleType.PRIMARY);
							
							while (qr.hasMoreElements()) {
								ApplicationData app = (ApplicationData) qr.nextElement();
								PersistenceHelper.manager.delete(app);
							}
							
							ApplicationData newprimaryapp = ApplicationData.newApplicationData(ch);
							newprimaryapp.setRole(ContentRoleType.PRIMARY);
							String primaryPath = caddoc.get(key).getAbsolutePath();
							//System.out.println("attachmentPath:"+attachmentPath);
							newprimaryapp = ContentServerHelper.service.updateContent(ch, newprimaryapp, primaryPath);
							newprimaryapp = (ApplicationData) PersistenceHelper.manager.save(newprimaryapp);
							
							
							
							if(pdfdoc.get(key)!=null){
								QueryResult qr1 = wt.content.ContentHelper.service
										.getContentsByRole(doc,
												wt.content.ContentRoleType.SECONDARY);
								while (qr1.hasMoreElements()) {
									ApplicationData app = (ApplicationData) qr1.nextElement();
									if(app.getFileName().equals(pdfdoc.get(key).getName())){
										PersistenceHelper.manager.delete(app);
										logs.add(new String[]{"DWG","线束",key,caddoc.get(key).getName(),"更新DWG、PDF"});
									}
								}
								
								ApplicationData newsencodaryapp = ApplicationData.newApplicationData(ch);
								newsencodaryapp.setRole(ContentRoleType.SECONDARY);
								String sencodaryPath = pdfdoc.get(key).getAbsolutePath();
								//System.out.println("attachmentPath:"+attachmentPath);
								newsencodaryapp = ContentServerHelper.service.updateContent(ch, newsencodaryapp, sencodaryPath);
								newsencodaryapp = (ApplicationData) PersistenceHelper.manager.save(newsencodaryapp);						
								pdfdoc.remove(key);
							}else{
								logs.add(new String[]{"DWG","线束",key,caddoc.get(key).getName(),"更新DWG"});
							}
				    	}					
						
					}else{
						//创建文档
						createDocument(part,"wt.doc.WTDocument|com.CATLBattery.CATLDocument|com.CATLBattery.autocadDrawing",caddoc.get(key).getAbsolutePath(),pdfdoc.get(key)==null?"":pdfdoc.get(key).getAbsolutePath(),key,key);
						logs.add(new String[]{"DWG","线束",key,caddoc.get(key).getName(),"新建"+(pdfdoc.get(key)==null?"无PDF":"有PDF")});
						pdfdoc.remove(key);						
					}
				}else{
					logs.add(new String[]{"DWG","线束",key,caddoc.get(key).getName(),"线束没有对应的PN"});
				}
			}
			
			updateAttachment(pdfdoc);
			
			trx.commit();
			trx = null;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if (trx != null){
				System.out.println("---------loadCADPDF rollback--------");
				trx.rollback();
			}
		}
		
		ExcelWriter writer = new ExcelWriter();
		try {
			boolean flag = writer.exportExcelList(rootPath+"/数据导入日志.xlsx","数据导入日志", new String[]{"文件格式","数据类型","PN","文件名","备注"}, logs);
			System.out.println("数据导入日志.xlsx flag="+flag);
			logs=new ArrayList<String[]>();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	
	public static void test() throws Exception{
		String primary_doc_path=WTProperties.getLocalProperties().getProperty("wt.home")+"/loadFiles/com/catl/dms/dwgtest.dwg";
		String att_doc_path=WTProperties.getLocalProperties().getProperty("wt.home")+"/loadFiles/com/catl/dms/pdftest.pdf";
		
		WTPart part=(WTPart)getPersistableByOid("OR:wt.part.WTPart:87263");
		createDocument(part,"wt.doc.WTDocument|com.CATLBattery.CATLDocument|com.CATLBattery.autocadDrawing",primary_doc_path,att_doc_path,part.getNumber(),part.getNumber());
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
	
	private static WTDocument createDocument(WTPart part,String doc_type, 
			String primary_doc_path, String att_doc_path,String doc_name,String doc_number) throws Exception {
		WTDocument document = null;
		WTContainer container=part.getContainer();
		
		String partPath=part.getFolderPath();
		
		System.out.println("partPath:"+partPath);
		
		String projectCode="";
		String projectName="";
		
		String doc_folder="";
		String containerName=container.getName();	
        if(!containerName.equals("电芯") && !containerName.equals("电子电气件") && !containerName.equals("紧固件") && !containerName.equals("原材料")){
        	        	
    		int i1=partPath.indexOf("Default");
    		int i2=partPath.indexOf("零部件");
    		String codeAndname=partPath.substring(i1+8,i2-1);
        	
    		String tempvalue=codeAndname.replace("（", "(").replace("）", ")");
            int beginIndex=tempvalue.indexOf("(");
    		int endIndex=tempvalue.lastIndexOf(")");
    		
    		if(beginIndex>-1 && endIndex>-1){
    			projectCode=codeAndname.substring(beginIndex+1,endIndex);
    			projectName=codeAndname.substring(0,beginIndex);
    		}else{
    			projectCode=codeAndname;
    		}
        	
        	doc_folder=partPath.substring(0,i2)+"设计图档/线束AutoCAD图纸";
        }else{    
        	doc_folder=partPath.substring(0,partPath.lastIndexOf("/"));
        	projectCode=doc_folder.substring(doc_folder.lastIndexOf("/")+1);
        }
		
		ReferenceFactory rf = new ReferenceFactory();
		String containerRef = rf.getReferenceString(ObjectReference.newObjectReference(((Persistable) container).getPersistInfo().getObjectIdentifier()));
		
		WTContainerRef ref = WTContainerRef.newWTContainerRef(container);
		Folder subfolder = wt.folder.FolderHelper.service.getFolder(doc_folder, ref);
		String folderRef  = new ReferenceFactory().getReferenceString(ObjectReference.newObjectReference(((Persistable) subfolder).getPersistInfo().getObjectIdentifier()));
		
		TypeIdentifier typeidentifier = FdnWTContainerHelper.toTypeIdentifier(doc_type);
		HashMap<String, String> input_data = new HashMap<String, String>(5);

		input_data.put("name", doc_name);
		input_data.put("number", doc_number);
		input_data.put("folderRef", folderRef);
		input_data.put("container", containerRef);
		document = ApplicationIntegrationFactory.createDocument(typeidentifier,input_data, false, SessionHelper.manager.getLocale(), "html");

		ContentHolder ch = (ContentHolder) document;
		ApplicationData ap = ApplicationData.newApplicationData(ch);
		ap.setRole(ContentRoleType.PRIMARY);
		ap = ContentServerHelper.service.updateContent(ch, ap, primary_doc_path);
		ap = (ApplicationData) PersistenceHelper.manager.save(ap);
		
		if(!att_doc_path.equals("")){
			ApplicationData newsencodaryapp = ApplicationData.newApplicationData(ch);
			newsencodaryapp.setRole(ContentRoleType.SECONDARY);
			//System.out.println("attachmentPath:"+attachmentPath);
			newsencodaryapp = ContentServerHelper.service.updateContent(ch, newsencodaryapp, att_doc_path);
			newsencodaryapp = (ApplicationData) PersistenceHelper.manager.save(newsencodaryapp);		
		}
		IBAUtility	iba_doc = new IBAUtility(document);
		iba_doc.setIBAValue("projectCode", projectCode);
		if(!projectName.equals("")){
			iba_doc.setIBAValue("projectName", projectName);
		}		
		iba_doc.setIBAValue("outputPhase", "发布阶段");
		iba_doc.setIBAValue("subCategory", "线束AUTOCAD图纸");
		
		iba_doc.updateAttributeContainer(document);
		iba_doc.updateIBAHolder(document);
		
		part=checkOutWTPart(part);
		WTPartDescribeLink link=WTPartDescribeLink.newWTPartDescribeLink(part, document);
		PersistenceHelper.manager.save(link);
		
		checkInObject(part);
		
		LifeCycleHelper.service.setLifeCycleState(document, State
				.toState("RELEASED"), true);
		
		return document;
	}
	
	static WTPart checkOutWTPart(WTPart part) throws WTException, WTPropertyVetoException{
		
		Folder myCOFolder = null;
		myCOFolder = WorkInProgressHelper.service.getCheckoutFolder();
		// 判断工作副本是否是检出状态
		if (!WorkInProgressHelper.isCheckedOut(part)) {
			WorkInProgressHelper.service.checkout(part,myCOFolder, null);
			part = (WTPart) WorkInProgressHelper.service
						.workingCopyOf(part);
		}
		return part;
	}
	

	static Workable checkInObject(Workable object) throws WorkInProgressException, WTPropertyVetoException, PersistenceException, WTException{
		object=WorkInProgressHelper.service.checkin(object, ""); 
		return object;
	}
	
	
	private static void getAllFile(Map<String,File> pdfdoc,Map<String,File> caddoc,File root){
		if(root.isDirectory()){
			File[] files = root.listFiles();
			for(File file : files){
				if(!file.isDirectory()){
					if(file.getName().indexOf(".pdf")>0){
						System.out.println(".....pdf");
						String pn=file.getName().substring(0,file.getName().indexOf("."));
						if(pdfdoc.containsKey(pn)){
							continue;
						}else{
							pdfdoc.put(pn, file);
						}
					}
					
					if(file.getName().lastIndexOf(".dwg")>0){
						String pn=file.getName().substring(0,file.getName().indexOf("."));
						
						if(caddoc.containsKey(pn)){
							continue;
						}else{
							caddoc.put(pn, file);
						}
					}
					
				}else{
					getAllFile(pdfdoc,caddoc,file);
				}
			}
		}
	}
	
	static WTPart getPart(String number) throws WTException {
		WTPart wtpart = null;
		QuerySpec qs= new QuerySpec(WTPart.class);
		SearchCondition sc = new SearchCondition(WTPart.class,
				WTPart.NUMBER, SearchCondition.EQUAL, number.trim());
		qs.appendWhere(sc);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		if (qr.size() > 0)
			wtpart = (WTPart) qr.nextElement();
		
		if(wtpart!=null){
			wtpart=getLatestPart((WTPartMaster) wtpart.getMaster());
		}
		
		return wtpart;
	}
	
	static WTDocument getDoc(String number) throws WTException {
		WTDocument doc = null;
		QuerySpec qs= new QuerySpec(WTDocument.class);
		SearchCondition sc = new SearchCondition(WTDocument.class,
				WTDocument.NUMBER, SearchCondition.EQUAL, number.trim());
		qs.appendWhere(sc);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		if (qr.size() > 0)
			doc = (WTDocument) qr.nextElement();
		
		if(doc!=null){
			doc=getLatestDoc((WTDocumentMaster) doc.getMaster());
		}
		
		return doc;
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

}
