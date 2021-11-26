package com.catl.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import wt.change2.ChangeException2;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeActivity2;
import wt.doc.WTDocument;
import wt.epm.EPMAuthoringAppType;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.structure.EPMStructureHelper;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.pom.PersistenceException;
import wt.type.Typed;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;
import wt.vc.struct.StructHelper;

import com.catl.line.util.WCUtil;
import com.catl.pdfsignet.PDFSignetUtil;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;
public class CatiaCheck implements RemoteAccess{
	public static void check2D3D(List affectitems) throws ChangeException2, WTException{
			StringBuffer errormsg1=new StringBuffer();
			StringBuffer errormsg2=new StringBuffer();
			
			List catiaepms=new ArrayList();//变更的2D、3D图纸
			List drawepms=new ArrayList();//变更的2D图纸
			List drawepmnumbers=new ArrayList();//变更的2D图纸编号
			List partnumbers=new ArrayList();//变更的PN
			for (int a=0;a<affectitems.size();a++) {
				NmOid nmOid = (NmOid) affectitems.get(a);
				Object object2 = nmOid.getLatestIterationObject();
				if (object2 instanceof EPMDocument) {
					EPMDocument epm = (EPMDocument) object2;
					System.out.println("========"+epm.getNumber());
					EPMAuthoringAppType authorapp=epm.getAuthoringApplication();
					String apptype="";
					if(authorapp!=null){
						apptype=authorapp.getDisplay();
					}
					if(apptype.toUpperCase().contains("CATIA")){
						catiaepms.add(epm);
						if(is2D(epm)){
							drawepms.add(epm);
							drawepmnumbers.add(epm.getNumber());
						}
					}
				}else if(object2 instanceof WTPart){
					WTPart part=(WTPart) object2;
					partnumbers.add(part.getNumber());
				}
			}
			//3D,2D必须与PN一起变更
			for (int i = 0; i < catiaepms.size(); i++) {
				EPMDocument epm = (EPMDocument) catiaepms.get(i);
	            if(isConnector(epm)){//排除中间件
					continue;
				}
				WTPart part=null;
				if(is2D(epm)){
					part=PartUtil.getRelationPartBy2D(epm);
				}else{
					part=PartUtil.getRelationPartBy3D(epm);
				}
				boolean flag=false;
				if(partnumbers.contains(part.getNumber())){
					  flag=true;
				 }
				if(!flag){//catia图纸在变更对象没有找到对应的PN
					System.out.println(epm.getVersionIdentifier().getValue()+"."+epm.getIterationIdentifier().getValue());
					errormsg1.append(epm.getNumber()).append(",");
				}
			}
			
			//中间件2D变更，其直接已发布父级PN和2D必须一起变更
//			for (int i = 0; i < drawepms.size(); i++) {
//				EPMDocument epm = (EPMDocument) drawepms.get(i);
//				if(!isConnector(epm)){//排除非中间件
//					continue;
//				}
//				boolean allflag=true;
//				List<EPMDocument> cepms=get3DEPM(epm);
//				StringBuffer errormsg3=new StringBuffer();
//				for (int j = 0; j < cepms.size(); j++) {
//					EPMDocument catiaepm=cepms.get(j);
//					List parents=new ArrayList();
//					getAllParentEpmNotConnector(catiaepm,parents);
//					for (int k=0;k<parents.size();k++) {
//						EPMDocument parentepm = (EPMDocument) parents.get(k);
//						Collection releatedepms=EpmUtil.getDrawings(parentepm);
//						Iterator epmiter=releatedepms.iterator();
//						boolean flag=false;
//						if(epmiter.hasNext()){
//							 EPMDocument currentepm=(EPMDocument) epmiter.next();
//							  if(drawepmnumbers.contains(currentepm.getNumber())){//2D必须在变更对象
//								  flag=true;
//							  }else{
//								  if(!errormsg3.toString().contains(currentepm.getNumber())){
//									  errormsg3.append(currentepm.getNumber()).append(",");
//								  }
//							  }
//						}
//						allflag=allflag&&flag;
//					}
//				}
//				if(!allflag){//PN在变更对象未找到相关的2D图纸
//					errormsg2.append(epm.getNumber()).append("的父级2D图纸"+errormsg3.toString()+"必须收集到变更对象中\n");
//				}
//			}
			StringBuffer errormsg=new StringBuffer();
			if(errormsg1.length()>0){
				String errormsg1number=errormsg1.toString().substring(0, errormsg1.length()-1);
				errormsg1number=errormsg1number+"相关的PN未收集到变更对象中\n";
				errormsg.append(errormsg1number);
			}
			if(errormsg2.length()>0){
				String errormsg2number=errormsg2.toString().substring(0, errormsg2.length()-1);
				errormsg.append(errormsg2number);
			}
			if(errormsg.length()>0){
				throw new WTException(errormsg.toString());
			}
	}
	private static boolean is2D(EPMDocument epm) {
		if(epm.getNumber().toUpperCase().contains("CATDRAWING")){
			return true;
		}
		return false;
	}
	private static void getAllParentEpmNotConnector(EPMDocument epm,List parents) throws WTException {
		LatestConfigSpec configSpec = new LatestConfigSpec();
		QueryResult  epmResult=StructHelper.service.navigateUsedByToIteration(epm,configSpec);
		while (epmResult.hasMoreElements()) {
			EPMDocument parentepm = (EPMDocument) epmResult.nextElement();
			if(isConnector(parentepm)){
				getAllParentEpmNotConnector(parentepm,parents);
			}else{
				System.out.println(epm.getNumber()+"---"+parentepm.getNumber());
				parents.add(parentepm);
			}
			
		}
	}
	public static void main(String[] args) throws Exception {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		rms.setUserName("wcadmin");
		rms.setPassword("wcadmin");
		rms.invoke("test", CatiaCheck.class.getName(), null, null, null);
	}

	public static void test() throws Exception {
		WTChangeActivity2 changeorder=(WTChangeActivity2) WCUtil.getWTObject("VR:wt.change2.WTChangeActivity2:178647717");
		QueryResult qr = ChangeHelper2.service.getChangeablesBefore(changeorder);
		List list = new ArrayList<>();
		while(qr.hasMoreElements()){
			list.add(qr.nextElement());
		}
		check2D3D(list);
	}
	private static boolean isConnector(EPMDocument epm){
		if(epm.getNumber().split("-").length>2){
			return true;
		}
		return false;
	}
	  public static List<EPMDocument> get3DEPM(EPMDocument epmDocument) throws WTException {
	        QueryResult referencedObjects =EPMStructureHelper.service.navigateReferences(epmDocument, null, true);
	        List<EPMDocument> epms=new ArrayList<EPMDocument>();
	        while (referencedObjects.hasMoreElements()) {
	            Object refObject = referencedObjects.nextElement();
	            if (refObject instanceof EPMDocumentMaster) {
	            	EPMDocumentMaster referencedDoc = (EPMDocumentMaster) refObject;
                    EPMDocument epm = getLatestEPMDocument(referencedDoc);
                    epms.add(epm);
	            }
	        }
	        return epms;
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
	  
	  /**
	   * AutoCAD与PN必须一起变更
	   * @param affectitems
	   * @throws WTException
	   */
	  public static void checkAutoCAD(List affectitems) throws WTException{
		  StringBuffer errormsg1=new StringBuffer();
		  System.out.println("start to check AutoCAD\t"+affectitems.size());
		  List<String> affectnumbers = new ArrayList<String>();
		  for (int a=0;a<affectitems.size();a++) {		  
			  
			  NmOid nmOid = (NmOid) affectitems.get(a);
			  Object obj = nmOid.getLatestIterationObject();
			  if(obj instanceof WTPart){
				  WTPart part = (WTPart) obj;
				  affectnumbers.add(part.getNumber());				  
			  }
		  }
		  for (int a=0;a<affectitems.size();a++) {		  
			  
			  NmOid nmOid = (NmOid) affectitems.get(a);
			  Object obj = nmOid.getLatestIterationObject();
			  System.out.println("AffectItems\t"+obj);
			  if(TypeUtil.isSpecifiedType((Typed) obj, CatlConstant.AUTOCAD_DOC_TYPE)){
					WTPart part = null;
					System.out.println("test............\t"+part);
					WTDocument doc = (WTDocument) obj;
					part =getRelationPartByDescDoc(doc);
					if(part == null){
						System.out.println("必须与PN关联");
						errormsg1.append("\nAutoCAD图档").append(doc.getNumber()).append("必须与PN关联");

					}else{
						if(!affectnumbers.contains(part.getNumber())){
							System.out.println("必须与其关联的PN一起变更！");
							errormsg1.append("\nAutoCAD图档").append(doc.getNumber()).append("必须与其关联的PN").append(part.getNumber()).append("一起变更！");
						}
					}
			  }
		  }
		  
		  if(errormsg1.length()>0){
			  throw new WTException(errormsg1.toString());
		  }
		  
	  }
	  
	  /**
		 * 获取说明文档关联的物料
		 * @param doc
		 * @return
		 * @throws WTException
		 */
		public static WTPart getRelationPartByDescDoc(WTDocument doc) throws WTException{
			QueryResult relatepartlist = PartDocServiceCommand.getAssociatedDescParts(doc);
			relatepartlist = new LatestConfigSpec().process(relatepartlist);
			while (relatepartlist.hasMoreElements()) {
				return (WTPart) relatepartlist.nextElement();
			}
			return null;
		}
}
