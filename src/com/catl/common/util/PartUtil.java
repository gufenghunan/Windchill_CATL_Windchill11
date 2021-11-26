
package com.catl.common.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.catl.bom.workflow.BomWfUtil;
import com.catl.common.constant.Constant;
import com.catl.common.constant.IBAInteriorValue;
import com.catl.common.constant.PartState;
import com.catl.common.constant.TypeName;
import com.catl.integration.PartInfo;
import com.catl.line.constant.ConstantLine;
import com.catl.line.exception.LineException;
import com.catl.line.util.CommonUtil;
import com.catl.line.util.NodeUtil;
import com.catl.loadData.IBAUtility;
import com.catl.part.CatlPartNewNumber;
import com.catl.part.CreateCatlPartProcessor;
import com.catl.part.PartConstant;
import com.catl.part.PartLoadNameSourceUtil;
import com.catl.pdfsignet.PDFSignetUtil;
import com.catl.require.constant.ConstantRequire;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;
import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.meta.common.SearchOperationIdentifier;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.common.UpdateOperationIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.windchill.enterprise.part.commands.AssociationLinkObject;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;

import wt.access.AccessControlHelper;
import wt.access.AccessPermission;
import wt.clients.vc.CheckInOutTaskLogic;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.structure.EPMReferenceLink;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTReference;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.iba.definition.StringDefinition;
import wt.iba.value.StringValue;
import wt.lifecycle.State;
import wt.org.WTUser;
import wt.part.PartDocHelper;
import wt.part.WTPart;
import wt.part.WTPartConfigSpec;
import wt.part.WTPartHelper;
import wt.part.WTPartIDSeq;
import wt.part.WTPartMaster;
import wt.part.WTPartStandardConfigSpec;
import wt.part.WTPartUsageLink;
import wt.pds.StatementSpec;
import wt.pom.PersistenceException;
import wt.pom.Transaction;
import wt.query.ClassAttribute;
import wt.query.ColumnExpression;
import wt.query.ConstantExpression;
import wt.query.QuerySpec;
import wt.query.SQLFunction;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.util.WTMessage;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.vc.ControlBranch;
import wt.vc.Mastered;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;
import wt.vc.config.ConfigHelper;
import wt.vc.config.LatestConfigSpec;
import wt.vc.struct.StructHelper;
import wt.vc.wip.WorkInProgressHelper;

public class PartUtil {
	private static Logger log=Logger.getLogger(PartUtil.class.getName());
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
    public static PartInfo getPartInfo(WTPart part)
    {
    	PartInfo partInfo=new PartInfo();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		partInfo.setPartName(part.getName());
		partInfo.setPartNumber(part.getNumber());
		try {
			partInfo.setCreateDate(format.parse(part.getCreateTimestamp()
					.toLocaleString()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			log.debug(part.getNumber()+"part createdate parse failed!");
			e.printStackTrace();
		}
		partInfo.setCreator(part.getCreatorName());
		partInfo.setDefaultUnit(part.getDefaultUnit().toString());
		partInfo.setSource(part.getSource().getDisplay());

		partInfo.setMaterialGroup(part.getNumber().substring(0, 6));
		partInfo.setIteration(part.getIterationIdentifier().getValue());
		partInfo.setVersionBig(part.getVersionIdentifier().getValue());

		return partInfo;
    }
	public static boolean existGreaterVersion(RevisionControlled obj) throws WTException{
		Mastered master = obj.getMaster();
		RevisionControlled lastestVersion = getLatestVersion(master);
		if(lastestVersion.getVersionIdentifier().getSeries().greaterThan(obj.getVersionIdentifier().getSeries())){
			return true;
		}
		
		return false;
	}
	public static RevisionControlled getLatestVersion(Mastered master ) throws WTException{
		
		QueryResult queryResult = VersionControlHelper.service.allVersionsOf(master);
		
		RevisionControlled result = null;
		while (queryResult.hasMoreElements())
		{
			RevisionControlled obj = ((RevisionControlled) queryResult.nextElement());
			//比较获取最大版本的
			if (result == null || obj.getVersionIdentifier().getSeries().greaterThan(result.getVersionIdentifier().getSeries()))
			{
				result = obj;
			}
		}
		result =  (RevisionControlled) VersionControlHelper.service.getLatestIteration(result,false);//false代表不查标志为删除的对象
		
		return result;
	}
    /**
     * 获取对象上一版本
     * 
     * @param currentVersion
     * @return
     * @throws WTException
     */
    public static Versioned getPreviousVersion(Versioned currentVersion) throws WTException {
        if(log.isDebugEnabled()){
            log.debug("Enter into getPreviousVersion  Current object is " +
                                 currentVersion.getMaster().getIdentity() + " "                    
                     + currentVersion.getVersionIdentifier().getValue() + "." + 
                       currentVersion.getIterationIdentifier().getValue());
        }
        Versioned previousVersion = null;
        try {
            QueryResult allVersions = wt.vc.VersionControlHelper.service.allVersionsOf((Versioned) currentVersion);
            if (allVersions.size() <= 1)
                return null;
            Versioned latestVersion = (Versioned) allVersions.nextElement(); // latest
                                                                             // version
            previousVersion = (Versioned) allVersions.nextElement(); // previous
                                                                     // version
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if(log.isDebugEnabled()){
            log.debug("Enter into getPreviousVersion  previous object is " +
                    previousVersion.getMaster().getIdentity() + " "                    
                     + previousVersion.getVersionIdentifier().getValue() + "." + 
                     previousVersion.getIterationIdentifier().getValue());
        }
        
        
        
        return previousVersion;
    }
    
    
  
    
	public static WTPart getLastestWTPartByNumber(String numStr) {
		try {
			QuerySpec queryspec = new QuerySpec(WTPart.class);

			queryspec.appendSearchCondition(new SearchCondition(WTPart.class,
					WTPart.NUMBER, SearchCondition.EQUAL, numStr));
			QueryResult queryresult = PersistenceHelper.manager.find(queryspec);
			LatestConfigSpec cfg = new LatestConfigSpec();
			QueryResult qr = cfg.process(queryresult);
			if (qr.hasMoreElements()) {
				return (WTPart) qr.nextElement();
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static Persistable getPersistableByOid(String oid) {
		Persistable obj = null;
		try {
			ReferenceFactory referencefactory = new ReferenceFactory();
			WTReference wtreference = referencefactory.getReference(oid);
			if (wtreference.getObject() != null) {
				obj = wtreference.getObject();
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	/**
	 * 判断零部件的状态是否为“已发布”状态
	 * @param part
	 * @return
	 */
	public static boolean isReleased(WTPart part){
		if(part != null){
			String state = part.getState().toString();
			return StringUtils.equals(state, PartState.RELEASED);
		}
		return false;
	}
	
	/**
	 * 非1大版本的零部件关联了CAD文档，删除PDF图纸附件
	 * @param busObject
	 * @throws WTException
	 */
	public static void deletePDFAttachmentData(Object busObject) throws WTException{
		if(busObject instanceof WTPart){
			WTPart part = (WTPart)busObject;
			log.info("===deletePDFAttachmentData start===");
			boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
			log.info("==part:"+part.getIdentity());
			try{
				if(isDeletePDFAttachment(part)) {
					Set<ApplicationData> set = getPDFAttachmentData(part);
					if(set.size() > 0){
						log.info("==delete pdf attachment==");
						for (ApplicationData pdfData : set) {
							ContentServerHelper.service.deleteContent(part, pdfData);
						}
					}
				}
			} catch (WTPropertyVetoException e) {
				e.printStackTrace();
				throw new WTException(e.getLocalizedMessage());
			}
			finally {
				SessionServerHelper.manager.setAccessEnforced(enforce);
			}
			log.info("===deletePDFAttachmentData end===");
		}
	}
	
	@SuppressWarnings("unchecked")
	private static boolean isDeletePDFAttachment(WTPart part) throws WTException{
		if(part != null){
			log.info("===PartNumber:"+part.getNumber());
			String version = part.getVersionIdentifier().getValue();
			log.info("===version:"+version);
			if(!StringUtils.equals(version, "1")){
				Collection<AssociationLinkObject> cols = PartDocServiceCommand.getAssociatedCADDocumentsAndLinks(part);
				log.info("===cols.size():"+cols.size());
				return cols.size() > 0;
			}
		}
		return false;
	}
	
	/**
	 * 获得零部件PDF图纸附件对象
	 * @param part
	 * @return
	 * @throws WTException
	 */
	private static Set<ApplicationData> getPDFAttachmentData(WTPart part) throws WTException{
		Set<ApplicationData> pdfDatas = new HashSet<ApplicationData>();
		QueryResult qr = ContentHelper.service.getContentsByRole(part, ContentRoleType.SECONDARY);
		while(qr.hasMoreElements()){
			ApplicationData appData = (ApplicationData)qr.nextElement();
			String fileName = appData.getFileName();
			if(StringUtils.startsWithIgnoreCase(fileName, part.getNumber()) && StringUtils.endsWithIgnoreCase(fileName, ".pdf")){
				pdfDatas.add(appData);
			}
		}
		return pdfDatas;
	}
	
	/**
	 * 判断零部件是否关联有CATIADrawing图纸
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public static boolean hasCATIADrawing(WTPart part) throws WTException{
		QueryResult qr = PartDocHelper.service.getAssociatedDocuments(part);
		while(qr.hasMoreElements()){
			Object obj = qr.nextElement();
			if(PDFSignetUtil.isCATIADrawing(obj)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 获得part的最新版本对象
	 * @param part
	 * @return
	 * @throws WTException 
	 * @throws PersistenceException 
	 */
	public static WTPart getLastestWTPart(WTPart part) throws WTException{
		if(part != null){
			QueryResult qr = VersionControlHelper.service.allVersionsOf(part.getMaster());
//			qr = new LatestConfigSpec().process(qr);
			if(qr.hasMoreElements()){
				return (WTPart)qr.nextElement();
			}
		}
		return null;
	}
	
	/**
	 * 判断零部件是否为最新版本
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public static boolean isLastedWTPart(WTPart part) throws WTException{
		if(part != null){
			WTPart lastedPart = getLastestWTPart(part);
			return part.equals(lastedPart);
		}
		return false;
	}
	
	/**
	 * 获取最新版本符合所给IBA属性值的所有零部件
	 * @param excludeNumber 需要排除的编码，可以为空
	 * @param define IBA属性内部名称
	 * @param value  IBA属性值
	 * @return
	 * @throws WTException
	 */
	public static Set<WTPart> getLastedPartByStringIBAValue(String excludeNumber,String define, String value) throws WTException{
		Set<WTPart> partSet = new HashSet<WTPart>();
		if(StringUtils.isNotBlank(define) && StringUtils.isNotBlank(value)){
			QuerySpec qs = new QuerySpec();
			qs.setAdvancedQueryEnabled(true);
			int index0 = qs.appendClassList(WTPart.class, true);
			int index1 = qs.appendClassList(StringValue.class, false);
			int index2 = qs.appendClassList(StringDefinition.class, false);
			
			SearchCondition join = new SearchCondition(WTPart.class, WTAttributeNameIfc.ID_NAME, StringValue.class,StringValue.IBAHOLDER_REFERENCE+"."+WTAttributeNameIfc.REF_OBJECT_ID);
			SearchCondition join1 = new SearchCondition(StringValue.class, StringValue.DEFINITION_REFERENCE+"."+WTAttributeNameIfc.REF_OBJECT_ID, StringDefinition.class, WTAttributeNameIfc.ID_NAME);
			
			qs.appendWhere(join, new int[]{index0,index1});
			qs.appendAnd();
			qs.appendWhere(join1, new int[]{index1,index2});
			if(StringUtils.isNotBlank(excludeNumber)){
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(WTPart.class,WTPart.NUMBER,SearchCondition.NOT_EQUAL,excludeNumber), new int[]{index0});
			}
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTPart.class,WTPart.LATEST_ITERATION,SearchCondition.IS_TRUE), new int[]{index0});
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(StringValue.class,StringValue.VALUE,SearchCondition.EQUAL,value),new int[]{index1});
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(StringValue.class,StringValue.IBAHOLDER_REFERENCE+"."+WTAttributeNameIfc.REF_CLASSNAME,SearchCondition.EQUAL,WTPart.class.getName()),new int[]{index1});
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(StringDefinition.class,StringDefinition.NAME,SearchCondition.EQUAL,define),new int[]{index2});
			
			log.info("==QuerySQL:"+qs.toString());
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec)qs);
			Persistable[] p = null;
			while (qr.hasMoreElements()) {
				p = (Persistable[]) qr.nextElement();
				if(isLastedWTPart((WTPart)p[0])){
					partSet.add((WTPart)p[0]);
				}
			}
			log.info("==partSet:"+partSet.size());
		}
		return partSet;
	}
	
	/**
	 * 获取最新已发布的Part
	 * @param master
	 * @return
	 * @throws WTException
	 */
	public static WTPart getLatestReleasedPart(WTPartMaster master) throws WTException{
		if(master != null){
			WTPartConfigSpec config = WTPartHelper.service.findWTPartConfigSpec();
			WTPartStandardConfigSpec standardConfig = config.getStandard();
			standardConfig.setLifeCycleState(State.RELEASED);
			QueryResult qr = ConfigHelper.service.filteredIterationsOf(master, standardConfig);
			if(qr.hasMoreElements()){
				return (WTPart)qr.nextElement();
			}
		}
		return null;
	}
	
	/**
	 * 检查零部件的旧物料号是否已经存在对应的新物料号
	 * @param checkPart
	 * @throws WTException
	 */
	public static void checkOldPartNumber(WTPart checkPart) throws WTException{
		if(checkPart != null){
			String objValue = null;
			try {
				PersistableAdapter genericObj = new PersistableAdapter(checkPart, null, null, new SearchOperationIdentifier());
				genericObj.load("oldPartNumber");
				objValue = (String)genericObj.get("oldPartNumber");
			} catch (WTException e) {
				e.printStackTrace();
			}
			if(objValue != null && objValue.length()>10){
				throw new WTException(WTMessage.formatLocalizedMessage("错误消息：旧物料号[{0}]不能超过10位！", new Object[]{objValue}));
			}
			Set<WTPart> set = getLastedPartByStringIBAValue(checkPart.getNumber(), "oldPartNumber", objValue);
			if(set.size() > 0){
				throw new WTException(WTMessage.formatLocalizedMessage("错误消息：旧物料号[{0}]已经存在对应的新物料号！", new Object[]{objValue}));
			}
		}
	}
	
	/**
	 * 判断Part是否有PDF图纸
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public static boolean hasPDF(WTPart part) throws WTException{
		QueryResult qr = PartDocHelper.service.getAssociatedDocuments(part);
		while(qr.hasMoreElements()){
			Object obj = qr.nextElement();
			if(PDFSignetUtil.isCATIADrawing(obj) || PDFSignetUtil.isAutoCADDoc(obj)){
				return true;
			}
		}
		if(PDFSignetUtil.getPDFFromWTPart(part).size() > 0){
			return true;
		}
		return false;
	}
	
	/**
	 * 获取关联的part
	 * @param obj
	 * @return
	 * @throws WTException
	 */
	public static WTPart getRelationPart(Object obj) throws WTException{
		if (obj instanceof WTPart) {//零部件
			return (WTPart) obj;
		}else if(obj instanceof EPMDocument){
			EPMDocument epmdoc = (EPMDocument) obj;
			if (epmdoc.getDocType().toString().equals("CADDRAWING")) {//CAD图纸
				return getRelationPartBy2D(epmdoc);
			}else{//Catia模型
				return getRelationPartBy3D(epmdoc);
			}
		}else if(obj instanceof WTDocument){
			WTDocument doc = (WTDocument) obj;
			return getRelationPartByDescDoc(doc);
		}
		return null;
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
	
	/**
	 * 获取 2D关联的物料
	 * @param epm2D
	 * @return
	 * @throws WTException
	 */
	public static WTPart getRelationPartBy2D(EPMDocument epm2D) throws WTException{
		QueryResult qur = PersistenceHelper.manager.navigate(epm2D, EPMReferenceLink.REFERENCES_ROLE, EPMReferenceLink.class, true);
		while (qur.hasMoreElements()) {
			QueryResult qr3D = ConfigHelper.service.filteredIterationsOf((EPMDocumentMaster) qur.nextElement(), new LatestConfigSpec());
			while (qr3D.hasMoreElements()) {
				EPMDocument epm3D = (EPMDocument) qr3D.nextElement();
				Collection relatepartcollCollection = EpmUtil.getRelatedPartsLasted(epm3D);
				Iterator partIterator = relatepartcollCollection.iterator();
				while (partIterator.hasNext()) {
					return (WTPart) partIterator.next();
				}
			}
		}
		return null;
	}
	/**
	 * 获取 3D关联的物料
	 * @param epm3D
	 * @return
	 * @throws WTException
	 */
	public static WTPart getRelationPartBy3D(EPMDocument epm3D) throws WTException{
		Collection relatepartcollCollection = EpmUtil.getRelatedPartsLasted(epm3D);
		Iterator partIterator = relatepartcollCollection.iterator();
		while (partIterator.hasNext()) {
			return (WTPart) partIterator.next();
		}
		return null;
	}
	
	/**
	 * 根据零部件编码获得Master对象
	 * @param partnumber
	 * @return
	 * @throws WTException
	 */
    public static WTPartMaster getWTPartMaster(String partnumber) throws WTException {
        WTPartMaster wtpartmaster = null;
        boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
        try{
        	 QuerySpec qs = new QuerySpec(WTPartMaster.class);
             int iIndex = qs.getFromClause().getPosition(WTPartMaster.class);
             SearchCondition sc = new SearchCondition(WTPartMaster.class, WTPartMaster.NUMBER, SearchCondition.EQUAL, partnumber, false);
             qs.appendWhere(sc, new int[iIndex]);

             QueryResult qr = PersistenceHelper.manager.find((StatementSpec)qs);
             if (qr.hasMoreElements()) {
                 wtpartmaster = (WTPartMaster) qr.nextElement();
             }
        } finally {
        	SessionServerHelper.manager.setAccessEnforced(enforce);
        }
        return wtpartmaster;
    }
    
    /**
     * 取最新大版本的最新小版本(排除工作副本)
     * @param partnumber
     * @return
     * @throws WTException
     */
	public static WTPart getLasterPart(String partnumber) throws WTException{
    	//[取最新大版本的最新小版本(排除工作副本)]
    	WTPart lastrpart = PartUtil.getLastestWTPartByNumber(partnumber);
    	if(WorkInProgressHelper.isWorkingCopy(lastrpart)){
    		lastrpart = (WTPart) CheckInOutTaskLogic.getOriginalCopy(lastrpart);
    	}
    	return lastrpart;
	}
	
	/**
	 * 获得零部件最新大版本的第一个小版本
	 * @param partNumber
	 * @return
	 * @throws WTException
	 */
	public static WTPart getFirstIterationInLatestVersion(String partNumber) throws WTException{
		WTPart latestPart = getLastestWTPartByNumber(partNumber);
		if(latestPart != null){
			ControlBranch cb = ControlBranch.newControlBranch(latestPart);
			return (WTPart)cb.getBranchPoint().getObject();
		}
		return null;
	}
	/**
	 * 判断MPN是否存在
	 * @param excludePN	不包含物料本身的mpn
	 * @param mpns 可以以逗号隔开
	 * @return
	 */
	public static Set<String> isExistMPN(String excludePN,String mpns){
		Set<String> ret = new HashSet<String>();
		QuerySpec qs;
		try {
			qs = new QuerySpec();
			qs.setAdvancedQueryEnabled(true);
			
			int index0 = qs.appendClassList(WTPart.class, true);
			int index1 = qs.appendClassList(StringValue.class, false);
			int index2 = qs.appendClassList(StringDefinition.class, false);

			SearchCondition join = new SearchCondition(WTPart.class, WTAttributeNameIfc.ID_NAME, StringValue.class, StringValue.IBAHOLDER_REFERENCE + "." + WTAttributeNameIfc.REF_OBJECT_ID);
			SearchCondition join1 = new SearchCondition(StringValue.class, StringValue.DEFINITION_REFERENCE + "." + WTAttributeNameIfc.REF_OBJECT_ID, StringDefinition.class, WTAttributeNameIfc.ID_NAME);
			
			qs.appendWhere(join, new int[] { index0, index1 });
			qs.appendAnd();
			qs.appendWhere(join1, new int[] { index1, index2 });
			qs.appendAnd();

			if (excludePN != null) {
				qs.appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.NOT_EQUAL, excludePN), new int[] { index0 });
				qs.appendAnd();
				
			}
			qs.appendWhere(new SearchCondition(WTPart.class, WTPart.LATEST_ITERATION, SearchCondition.IS_TRUE), new int[] { index0 });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTPart.class, WTPart.LIFE_CYCLE_STATE, SearchCondition.NOT_EQUAL, PartState.DISABLEDFORDESIGN), new int[] { index0 });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(StringDefinition.class, StringDefinition.NAME, SearchCondition.EQUAL, IBAInteriorValue.Manufacturer_Part_Number), new int[] { index2 });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(StringValue.class, StringValue.IBAHOLDER_REFERENCE + "." + WTAttributeNameIfc.REF_CLASSNAME, SearchCondition.EQUAL, WTPart.class.getName()), new int[] { index1 });
			
			qs.appendAnd();
			qs.appendOpenParen();
			ClassAttribute caIbaName = new ClassAttribute(StringValue.class, StringValue.VALUE2);
			String[] mpnsArr = mpns.split(",");
			for(int i = 0; i < mpnsArr.length; i++){
				qs.appendWhere(new SearchCondition(SQLFunction.newSQLFunction(SQLFunction.CONCAT,caIbaName,new ConstantExpression((Object)",")), SearchCondition.LIKE,  new ConstantExpression((Object)"%"+mpnsArr[i]+",%")), new int[] { index1 });
				if(i != mpnsArr.length-1)
					qs.appendOr();
			}
			qs.appendCloseParen();
			log.debug("==isExistMPN QuerySQL:" + qs.toString());
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
			Persistable[] p = null;
			while (qr.hasMoreElements()) {
				p = (Persistable[]) qr.nextElement();
				WTPart part = (WTPart) p[0];
				if(isLastedWTPart(part))
					ret.add(part.getNumber());
			}
		}catch (WTException e){
			e.printStackTrace();
		}
		return ret;
	}
	/**
	 * 结构是否有变化
	 * @param lastPart
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public static boolean compareStructure(WTPart lastPart, WTPart part) throws WTException {
		Map<String,String> lastPartMap = new HashMap<String,String>();
		QueryResult qr = WTPartHelper.service.getUsesWTPartMasters(lastPart);
		while(qr.hasMoreElements()){
		   WTPartUsageLink link = (WTPartUsageLink)qr.nextElement();
		   String quantity=String.valueOf(link.getQuantity().getAmount());
		   WTPartMaster master = link.getUses();
		   lastPartMap.put(master.getNumber(), quantity);
		}
		
		Map<String,String> partMap = new HashMap<String,String>();
		qr = WTPartHelper.service.getUsesWTPartMasters(part);
		while(qr.hasMoreElements()){
		   WTPartUsageLink link = (WTPartUsageLink)qr.nextElement();
		   String quantity=String.valueOf(link.getQuantity().getAmount());
		   WTPartMaster master = link.getUses();
		   partMap.put(master.getNumber(), quantity);
		}
		
		for(String key : lastPartMap.keySet()){
			if(partMap.get(key)==null || !lastPartMap.get(key).equals(partMap.get(key))){
				return true;
			}
		}
		for(String key : partMap.keySet()){
			if(lastPartMap.get(key)==null || !partMap.get(key).equals(lastPartMap.get(key))){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 获取前一个小版本
	 * @param part
	 * @return
	 * @throws PersistenceException
	 * @throws WTException
	 */
	public static Versioned getPreIteration(Versioned part) throws PersistenceException, WTException{
		Mastered master = part.getMaster();
		 if(log.isDebugEnabled()){
	            log.debug("Enter into getPreviousIteration  Current object is " +
	            		part.getMaster().getIdentity() + " "                    
	                     + part.getVersionIdentifier().getValue() + "." + 
	                     part.getIterationIdentifier().getValue());
	        }
		QueryResult qr = VersionControlHelper.service.allIterationsOf(master);
		Versioned previousVersion =null;
		
		Versioned latestVersion = (Versioned) qr.nextElement(); // latest version
		while(!part.equals(latestVersion)){
			latestVersion = (Versioned) qr.nextElement();
		}
		if(qr.hasMoreElements()){
		   previousVersion = (Versioned) qr.nextElement();
		   if(log.isDebugEnabled()){
	            log.debug("Enter into getPreviousIteration  previous object is " +
	                    previousVersion.getMaster().getIdentity() + " "                    
	                     + previousVersion.getVersionIdentifier().getValue() + "." + 
	                     previousVersion.getIterationIdentifier().getValue());
	        }
		   
		}
		return previousVersion;		
	}

	/**
     * 根据部件查询其父件
     */
    public static List<WTPart> getParentPartByChildPart(WTPart part) {
    	List<WTPart> parentParts = new ArrayList<WTPart>();
        QueryResult qr;
        try {
            qr = WTPartHelper.service.getUsedByWTParts((WTPartMaster) part.getMaster());
            qr = (new LatestConfigSpec()).process(qr);
            while (qr != null && qr.hasMoreElements()) {
                Object obj = qr.nextElement();
                if (obj instanceof WTPart) {
                    WTPart parentPart = (WTPart) obj;
                    if(BomWfUtil.isLastVersion(parentPart)){
                    	parentParts.add(parentPart);
                    }
                }
            }
        } catch (WTException e) {
            e.printStackTrace();
        }
        return parentParts;
    }
    
    /**
	 * 是否为HEX软件PN
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public static boolean isSWPart(WTPart part) throws WTException{
		if(part != null){
			IBAUtility iba = new IBAUtility(part);
			String cls = iba.getIBAValue("cls");
			String swcls = getSWClsPropertyValue("SWCls");
			String[] swclses = swcls.split(",");
			List<String> list = Arrays.asList(swclses);
			if(list.contains(cls)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 是否为PCBA组件
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public static boolean isPCBAPart(WTPart part) throws WTException{
		if(part != null){
			IBAUtility iba = new IBAUtility(part);
			String cls = iba.getIBAValue("cls");
			String swcls = getSWClsPropertyValue("PCBACls");
			String[] swclses = swcls.split(",");
			List<String> list = Arrays.asList(swclses);
			if(list.contains(cls)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 获取HEX软件物料组
	 * @param key
	 * @return
	 */
	public static String getSWClsPropertyValue(String key){
        String value = null;
        try {
        	
        	String nameString = key;
            Properties customProperties = new Properties();
			WTProperties wtproperties = WTProperties.getLocalProperties();
			String codebase = wtproperties.getProperty("wt.codebase.location");
            customProperties.load(new FileInputStream(codebase+"/config/custom/PartProperties.properties"));
            nameString = new String(nameString.getBytes("GBK"), "ISO-8859-1");
            value = customProperties.getProperty(nameString);
            if(value == null)
            	return "";

            value = new String(value.trim().getBytes("ISO-8859-1"), "GBK"); // 处理中文乱码
        } catch (IOException e) {
        	e.printStackTrace(); 
        } catch(Exception e) {
        	value = "";
        	e.printStackTrace();
        }

        return value;
    }
	
    /**
	 * 是否为APP软件PN
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public static boolean checkSW_HWPart(WTPart part) throws WTException{
		if(part != null){
			IBAUtility iba = new IBAUtility(part);
			String cls = iba.getIBAValue("cls");
			String swcls = getSWClsPropertyValue("CHECK_SW_HW");
			String[] swclses = swcls.split(",");
			List<String> list = Arrays.asList(swclses);
			if(list.contains(cls)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 检查BOM中软件版本匹配码是否一致
	 * @param parentPart
	 * @param newMatchCode
	 * @return
	 * @throws WTException
	 */
	public static String checkMatchingCode(WTPart parentPart,String newMatchCode) throws WTException{
		StringBuffer message = new StringBuffer();
		if(parentPart != null){
			String matchcode = "";
			if(StringUtils.isNotBlank(newMatchCode)){
				matchcode = newMatchCode;
			}
			QueryResult qr = StructHelper.service.navigateUses(parentPart);
			while(qr.hasMoreElements()){
				WTPartMaster master = (WTPartMaster) qr.nextElement();
				WTPart childPart = (WTPart) CommonUtil.getLatestVersionOf(master);
				if(checkSW_HWPart(childPart)){
					String tmpmatchcode = (String) GenericUtil.getObjectAttributeValue(childPart, PartConstant.MatchingCode);	
					if(StringUtils.isNotBlank(matchcode) ){
						if(StringUtils.isNotBlank(tmpmatchcode) &&  !matchcode.equals(tmpmatchcode) ){
							message.append("BOM【").append(parentPart.getNumber()).append("】中子件软件版本匹配码不一致！\n");
						}
					}else{
						matchcode = tmpmatchcode;
					}
				}
			}
		}
		return message.toString();
	}
}
