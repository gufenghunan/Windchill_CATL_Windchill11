package com.catl.common.toolbox.data;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTList;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleTemplateMaster;
import wt.lifecycle.LifeCycleTemplateReference;
import wt.lifecycle.State;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.DirectoryContextProvider;
import wt.org.OrganizationServicesHelper;
import wt.org.WTOrganization;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.pom.PersistenceException;
import wt.pom.Transaction;
import wt.query.ArrayExpression;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.VersionControlHelper;
import wt.vc.wip.WorkInProgressHelper;

import com.catl.common.constant.DocState;
import com.catl.common.util.DocUtil;
import com.catl.common.util.PartUtil;
import com.ptc.core.meta.type.mgmt.server.impl.WTTypeDefinition;

public class refreshLifeCycle implements RemoteAccess{

	private static String homePath = "";
	
	static {
		try {
			WTProperties wtproperties = WTProperties.getLocalProperties();
			homePath = wtproperties.getProperty("wt.home");
		} catch (Throwable t) {
			throw new ExceptionInInitializerError(t);
		}
	}
	
    public static void main(String[] args) throws RemoteException, InvocationTargetException {
		
		if (args == null || args.length < 5){
        	
        	System.out.printf("请输入正确的系统对象的软类型、生命周期模板的名称、是否自动的撤销检出、用户名、密码！");
        } else {
			RemoteMethodServer ms = RemoteMethodServer.getDefault();
			ms.setUserName(args[3]);
			ms.setPassword(args[4]);
			
			String softType = args[0];
			String lifecycle = args[1];
			String undocheckout = args[2];
			try {
				SessionHelper.manager.setAuthenticatedPrincipal(args[3]);
				Class[] types = {String.class, String.class, String.class};
		        Object[] values={softType, lifecycle, undocheckout};
				RemoteMethodServer.getDefault().invoke("doLoad", refreshLifeCycle.class.getName(), null, types, values);
			} catch (WTException e) {
				e.printStackTrace();
			}
        }
	}

    public static void doLoad(String softType, String lifecycle, String undocheckout){
    	
    	boolean enforced=SessionServerHelper.manager.setAccessEnforced(false);
    	String logPath = homePath + "/logs/";
		FileWriter file = null;
		boolean checkflg = true;
		Transaction ts=null;
		BufferedWriter writer = null;
        try {
        	
        	Format format = new SimpleDateFormat("yyyyMMddHHmmss"); 
        	Date nowDate = new Date();
			String nowTime = format.format(nowDate); 

			file = new FileWriter(logPath + "Reassign_LC_" + nowTime + ".log");
			writer = new BufferedWriter(file);
			writer.write("");
			
			
			Format format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
        	writer.write("进入系统时间："+format1.format(nowDate));
    		writer.write("\n");
    		
        	if (!checkSoftType(softType)){
        		
        		checkflg = false;
        		writer.write("统对象的软类型："+softType+"，在系统中不存在！");
        		writer.write("\n");
        	}
        	
        	if (!checkLifeCycle(lifecycle)){
        		
        		checkflg = false;
        		writer.write("生命周期模板："+lifecycle+"，在系统中不存在！");
        		writer.write("\n");
        	}
        	
        	if (!"true".equals(undocheckout.toLowerCase()) && !"false".equals(undocheckout.toLowerCase())){
        		
        		checkflg = false;
        		writer.write("自动的撤销检出值："+undocheckout+"，必须为true或false，忽略大小写！");
        		writer.write("\n");
        	}
        	
        	boolean undocheckoutflg = Boolean.valueOf(undocheckout);
        	
        	WTList wtlist = new WTArrayList();
        	
        	DirectoryContextProvider dcp = WTContainerHelper.service.getExchangeContainer().getContextProvider();
        	WTOrganization org = OrganizationServicesHelper.manager.getOrganization("CATL", dcp);
        	WTContainerRef wtcontainerref = WTContainerHelper.service.getOrgContainerRef(org);
        	
        	LifeCycleTemplateReference newLifeCycleTemplate = LifeCycleHelper.service.getLifeCycleTemplateReference(lifecycle, wtcontainerref);
        	
        	if (newLifeCycleTemplate == null){
        		
        		checkflg = false;
        		writer.write("获取到的生命周期模板："+lifecycle+"为null！");
        		writer.write("\n");
        	}
        	
        	if (checkflg) {
        		
        		if (softType.indexOf("CATLPart") > -1){
            		
            		Vector<WTPart> wtparts=getPart();
            		for (WTPart part : wtparts){
            			
            			LifeCycleTemplateReference partLifeCycleTemplate = part.getLifeCycleTemplate();
            			
            			if (!partLifeCycleTemplate.equals(newLifeCycleTemplate)){
            				
            				boolean checkoutFlag = WorkInProgressHelper.isCheckedOut(part);
            				boolean workinfCopyFlag = WorkInProgressHelper.isWorkingCopy(part);
            				
            				if (checkoutFlag || workinfCopyFlag){
            					
            					if (undocheckoutflg){
            						
            						//取消检出
            						WorkInProgressHelper.service.undoCheckout(part);
            						writer.write("编号："+part.getNumber()+"的对象（"+softType+"）的在检出状态，已经被自动撤销检出！");
                	        		writer.write("\n");
                	        		
                	        		//重新获取最新对象
                	        		part = PartUtil.getLastestWTPartByNumber(part.getNumber());
            					} else {
            						
            						checkflg = false;
                	        		writer.write("编号："+part.getNumber()+"的对象（"+softType+"）的已经被检出！");
                	        		writer.write("\n");
                	        		continue;	
            					}
            					
            				}
            				
            				ts = new Transaction();
    	                    ts.start();
    	                    
        	        		//更新生命周期模版
        	        		wtlist = new WTArrayList();
        	        		wtlist.add(part);
        	        		LifeCycleHelper.service.reassign(wtlist, newLifeCycleTemplate, wtcontainerref, true);
        	        		
        	        		writer.write("编号："+part.getNumber()+"的对象（"+softType+"）的生命周期模板已经被更新为<"+lifecycle+">！");
        	        		writer.write("\n");
        	        		
        	        		ts.commit();
            			}
            		}
            	} else if (softType.indexOf("EPMDocument") > -1){
            		
            		Vector<EPMDocument> epmdocs = getEPMDocument();
            		for (EPMDocument epmdoc : epmdocs){
            			LifeCycleTemplateReference partLifeCycleTemplate = epmdoc.getLifeCycleTemplate();
            			
            			if (!newLifeCycleTemplate.equals(partLifeCycleTemplate)){
            				
            				boolean checkoutFlag = WorkInProgressHelper.isCheckedOut(epmdoc);
            				boolean workinfCopyFlag = WorkInProgressHelper.isWorkingCopy(epmdoc);
            				
            				if (checkoutFlag || workinfCopyFlag){
            					
            					if (undocheckoutflg){
            						
            						//取消检出
            						WorkInProgressHelper.service.undoCheckout(epmdoc);
            						writer.write("编号："+epmdoc.getNumber()+"的对象（"+softType+"）的在检出状态，已经被自动撤销检出！");
                	        		writer.write("\n");
                	        		
                	        		//重新获取最新对象
                	        		epmdoc = DocUtil.getLastestEPMDocumentByNumber(epmdoc.getNumber());
            					} else {
            						
            						checkflg = false;
                	        		writer.write("编号："+epmdoc.getNumber()+"的对象（"+softType+"）的已经被检出！");
                	        		writer.write("\n");
                	        		continue;	
            					}
            				}
            				ts = new Transaction();
    	                    ts.start();
    	                    
        	        		//更新生命周期模版
        	        		wtlist = new WTArrayList();
        	        		wtlist.add(epmdoc);
        	        		LifeCycleHelper.service.reassign(wtlist, newLifeCycleTemplate, wtcontainerref, true);
        	        		
        	        		writer.write("编号："+epmdoc.getNumber()+"的对象（"+softType+"）的生命周期模板已经被更新为<"+lifecycle+">！");
        	        		writer.write("\n");
        	        		
        	        		ts.commit();
            			}
            		}
            	}  else if (softType.indexOf("gerberDoc") > -1 || softType.indexOf("autocadDrawing") > -1|| softType.indexOf("pcbaDrawing") > -1 
            			|| softType.indexOf("CATLBattery.technicalDoc") > -1 || softType.indexOf("CATLBattery.rdDoc") > -1){
            		
            		Vector<WTDocument> wtdocument = getDoc(softType);
            		
                	for (WTDocument doc : wtdocument){
                		
                		LifeCycleTemplateReference docLifeCycleTemplate = doc.getLifeCycleTemplate();
                		if (!docLifeCycleTemplate.equals(newLifeCycleTemplate)){
                			
                			boolean checkoutFlag = WorkInProgressHelper.isCheckedOut(doc);
                			boolean workinfCopyFlag = WorkInProgressHelper.isWorkingCopy(doc);
            				
            				if (checkoutFlag || workinfCopyFlag){
            					
            					if (undocheckoutflg){
            						
            						//取消检出
            						WorkInProgressHelper.service.undoCheckout(doc);
            						writer.write("编号："+doc.getNumber()+"的对象（"+softType+"）的在检出状态，已经被自动撤销检出！");
                	        		writer.write("\n");
                	        		
                	        		//重新获取最新对象
                	        		doc = DocUtil.getLatestWTDocument(doc.getNumber());
            					} else {
            						
            						checkflg = false;
                	        		writer.write("编号："+doc.getNumber()+"的对象（"+softType+"）的已经被检出！");
                	        		writer.write("\n");
                	        		continue;	
            					}
            				}
            				
            				ts = new Transaction();
    	                    ts.start();
    	                    
        	        		//更新生命周期模版
    	                    if(doc.getLifeCycleState().equals(State.toState(DocState.WRITING)) || doc.getLifeCycleState().equals(State.toState(DocState.MODIFICATION)) || doc.getLifeCycleState().equals(State.toState(DocState.REVIEW))){
    	                    	
    	                    	try {
    	                    		SessionHelper.manager.setPrincipal(doc.getCreatorName());
    	                    	} catch(Exception e){
    	                    		SessionHelper.manager.setPrincipal("orgadmin");
    	                    	}
    	                    	

                    		} else {
                    			SessionHelper.manager.setPrincipal("orgadmin");
                    		}
        	        		
    	                   
    	                    
    	                    if(doc.getLifeCycleState().equals(State.toState(DocState.WRITING)) ||doc.getLifeCycleState().equals(State.toState(DocState.MODIFICATION)) || doc.getLifeCycleState().equals(State.toState(DocState.REVIEW))){
    	                    	
	                            wtlist = new WTArrayList();
	        	        		wtlist.add(doc);
	    	                    LifeCycleHelper.service.reassign(wtlist, newLifeCycleTemplate, wtcontainerref, State.toState(DocState.WRITING));
                    		} else {
                    			
                    			wtlist = new WTArrayList();
             	        		wtlist.add(doc);
         	                    LifeCycleHelper.service.reassign(wtlist, newLifeCycleTemplate, wtcontainerref, true);
                    		}
        	        		writer.write("编号："+doc.getNumber()+"的对象（"+softType+"）的生命周期模板已经被更新为<"+lifecycle+">！");
        	        		writer.write("\n");
        	        		
        	        		ts.commit();
            			}
                	}
            	} else {
            		
            		writer.write("对象（"+softType+"）不是二期处理的类型！");
            		writer.write("\n");
            	}
        		
//        		if (checkflg) {
//        			
//        			ts = new Transaction();
//                    ts.start();
//        			
//        			List listWithoutDup = new ArrayList<>(new HashSet<>(wtlist));
//        			wtlist.clear();
//        			wtlist.addAll(listWithoutDup);
//        			
//        			writer.write("需要进行更新："+softType+"的个数为："+wtlist.size());
//            		writer.write("\n");
//            		writer.write("开始更新对象（"+softType+"）时间："+format1.format(new Date()));
//            		writer.write("\n");
//            		
//                	if (wtlist.size() > 0){
//                		
//                		writer.write("开始执行更新："+softType+"的生命周期模版："+lifecycle);
//                		writer.write("\n");
//                		
//                		LifeCycleHelper.service.reassign(wtlist, newLifeCycleTemplate, wtcontainerref, true);
//                		
//                		writer.write("结束执行更新："+softType+"的生命周期模版："+lifecycle);
//                		writer.write("\n");
//                	}
//                	writer.write("结束更新对象（"+softType+"）时间："+format1.format(new Date()));
//            		writer.write("\n");
//                    System.out.println("refreshLifeCycle 操作成功！");
//                    ts.commit();
//        		}
        	}
            
        	writer.write("刷新结束时间："+format1.format(new Date()));
    		writer.write("\n");
    		
            writer.flush();
            writer.close();
            
        }catch (Exception e) {
        	try {
        		writer.write("报错：---------"+e.getMessage());
        		writer.write("\n");
				writer.flush();
				writer.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            
            e.printStackTrace();
            if(ts != null)
                ts.rollback();
        }finally{
			SessionServerHelper.manager.setAccessEnforced(enforced);
		}

    }

    public static Vector<WTDocument> getDoc(String typeName) throws WTException {
    	
    	String[] typeid = getDocTypeID(typeName);
    	
		Vector<WTDocument> wtdoc = new Vector<WTDocument>();
		
		QuerySpec qs = new QuerySpec(WTDocument.class);
		
    	qs.appendWhere(new SearchCondition(new ClassAttribute(WTDocument.class, WTDocument.TYPE_DEFINITION_REFERENCE+".key.id"),SearchCondition.IN, new ArrayExpression(typeid)), new int[]{0});
    	
    	QueryResult qr = PersistenceServerHelper.manager.query(qs);
    	
    	List<String> docNumListr=new ArrayList<String>();
    	
    	while(qr.hasMoreElements()){ 
    		WTDocument doc = (WTDocument)qr.nextElement();
    		
    		String docNum = doc.getNumber();
    		if (!docNumListr.contains(docNum) && DocUtil.isLastedWTDocument(doc)){
    			
    			wtdoc.add(doc);
    		}
    	}
		
		return wtdoc;
	}

    public static String[] getDocTypeID(String typeName) throws WTException {
    	
    	List<String> typeidList = new ArrayList<String>();
		QuerySpec qs= new QuerySpec(WTTypeDefinition.class);
		SearchCondition sc = new SearchCondition(WTTypeDefinition.class, WTTypeDefinition.NAME, SearchCondition.EQUAL, typeName);
		qs.appendWhere(sc);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		
		while(qr.hasMoreElements()){
			WTTypeDefinition wttype = (WTTypeDefinition) qr.nextElement();
			String pnOId=wttype.getPersistInfo().getObjectIdentifier().getId()+"";
			typeidList.add(pnOId);
		}
		String[] typeidArr = (String[])typeidList.toArray(new String[typeidList.size()]);
		return typeidArr;
	}
    
    public static boolean isEmpty(String str){
        if(str == null || str.trim().equals("") || str.equals("null"))
            return true;
        return false;
    }

    static Vector<WTPart> getPart() throws WTException {
    	
		Vector<WTPart> wtparts=new Vector<WTPart>();
		QuerySpec qs= new QuerySpec(WTPartMaster.class);
//		qs.appendWhere(new SearchCondition(WTPart.class, WTPart.LATEST_ITERATION, SearchCondition.IS_TRUE), new int[] {0});
		QueryResult qr = PersistenceHelper.manager.find(qs);
		List<String> partNumber=new ArrayList<String>();
		while(qr.hasMoreElements()){			
			WTPartMaster wtpartmaster = (WTPartMaster) qr.nextElement();
			if(!partNumber.contains(wtpartmaster.getNumber())){
				WTPart wtpart=getLatestPart(wtpartmaster);
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
	
	static Vector<EPMDocument> getEPMDocument() throws WTException {
    	
		String epmNum = "";
		Vector<EPMDocument> epmdocs=new Vector<EPMDocument>();
		QuerySpec qs= new QuerySpec(EPMDocumentMaster.class);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		List<String> epmNumber=new ArrayList<String>();
		while(qr.hasMoreElements()){			
			EPMDocumentMaster epmdocmaster = (EPMDocumentMaster) qr.nextElement();
			epmNum = epmdocmaster.getNumber();
			if(!epmNumber.contains(epmdocmaster.getNumber())){
				
				EPMDocument epmdoc = getLatestEPMDocument(epmdocmaster);
				if (epmdoc != null){
					epmdocs.add(epmdoc);
					epmNumber.add(epmdoc.getNumber());
				} else {
					System.out.println("问题Num："+epmNum);
				}
			}
		}
		return epmdocs;
	}
	
	static EPMDocument getLatestEPMDocument(EPMDocumentMaster epmMaster) throws PersistenceException, WTException{
		EPMDocument epmdoc = null;
		if (epmMaster != null) {
			QueryResult qr = VersionControlHelper.service.allVersionsOf(epmMaster);
			if(qr.hasMoreElements()){
				epmdoc = (EPMDocument) qr.nextElement();
			}
		}
		return epmdoc;
	}
	
	static boolean checkSoftType(String typeName) throws PersistenceException, WTException{
		
		boolean flg = false;
		
		QuerySpec qs= new QuerySpec(WTTypeDefinition.class);
		SearchCondition sc = new SearchCondition(WTTypeDefinition.class, WTTypeDefinition.NAME, SearchCondition.EQUAL, typeName);
		qs.appendWhere(sc);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		
		while(qr.hasMoreElements()){
			flg = true;
			break;
		}
		
		return flg;
	}

	static boolean checkLifeCycle(String lifecycleName) throws PersistenceException, WTException{
		
		boolean flg = false;
		
		QuerySpec qs= new QuerySpec(LifeCycleTemplateMaster.class);
		SearchCondition sc = new SearchCondition(LifeCycleTemplateMaster.class, LifeCycleTemplateMaster.NAME, SearchCondition.EQUAL, lifecycleName);
		qs.appendWhere(sc);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		
		while(qr.hasMoreElements()){
			flg = true;
			break;
		}
		
		return flg;
	}
}
