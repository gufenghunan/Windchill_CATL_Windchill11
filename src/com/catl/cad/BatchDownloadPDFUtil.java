package com.catl.cad;

import java.io.InputStream;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.catl.bom.workflow.BomWfUtil;
import com.ptc.windchill.uwgm.common.associate.AssociationType;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.build.EPMBuildRule;
import wt.epm.structure.EPMStructureHelper;
import wt.fc.ObjectVector;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.lifecycle.IteratedLifeCycleManaged;
import wt.part.Source;
import wt.part.WTPart;
import wt.util.WTException;
import wt.util.WTMessage;
import wt.vc.Iterated;
import wt.vc.Mastered;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

public class BatchDownloadPDFUtil {
	
	private static Logger log=Logger.getLogger(BatchDownloadPDFUtil.class.getName());
	
	public static final String CATIADrawing_Suffix = ".CATDrawing";
	
	public static final String PDF_Suffix = ".PDF";
	
	public static final String RELEASE_Suffix = "_RELEASE";

	public static boolean checkBatchDownloadPDF(Object obj) throws WTException{
		if(isCATIADrawing(obj)){
			System.out.println("===checkBatchDownloadPDF==isCATIADrawing===true");
			EPMDocument epm = (EPMDocument)obj;
			if(!WorkInProgressHelper.isWorkingCopy(epm) && BomWfUtil.isLastVersion(epm) && isReleased(epm) && !isMiddleware(epm)){
				return true;
			}
		}
		System.out.println("===checkBatchDownloadPDF==isCATIADrawing===false");
		return false;
	}
	
	public static boolean isMiddleware(EPMDocument epm){
		if(epm != null){
			String epmNumer = epm.getNumber();
			return epmNumer.indexOf("-", epmNumer.indexOf("-")+1) > 0;
		}
		return false;
	}
	
	/**
	 * 通过CATIADrawing判断关联的零部件是否为自制
	 * @param epm
	 * @return
	 * @throws WTException
	 */
	public static boolean isMake(EPMDocument epm) throws WTException{
		WTPart part = getWTPartByDrawing(epm);
		if(part != null){
			log.info("==isMake==Source:"+part.getSource());
			return Source.MAKE.equals(part.getSource());
		}
		return false;
	}
	
	/**
	 * 通过CATIADrawing获得零部件
	 * @param epm
	 * @return
	 * @throws WTException
	 */
	public static WTPart getWTPartByDrawing(EPMDocument epm) throws WTException{
		EPMDocument refBy = getReferenceEPMByDrawing(epm);
		if(refBy != null){
			return getLinkPartByEPM(refBy);
		}
		return null;
	}
	
	public static EPMDocument getReferenceEPMByDrawing(EPMDocument epm) throws WTException{
		QueryResult qr = EPMStructureHelper.service.navigateReferences(epm, null, true);
		while(qr.hasMoreElements()){
			EPMDocumentMaster master = (EPMDocumentMaster)qr.nextElement();
			if(StringUtils.equals(getSubNumber(master.getNumber()), getSubNumber(epm.getNumber()))){
				return (EPMDocument)getLatestVersionExcludeWorkingCopy(master);
			}
		}
		return null;	
	}
	
	public static EPMDocument getDrawingBy3D(EPMDocument epm) throws WTException{
		ObjectVector ov = new ObjectVector();
		QueryResult qr = EPMStructureHelper.service.navigateReferencedBy((EPMDocumentMaster)epm.getMaster(), null, true);
		while(qr.hasMoreElements()){
			Object refDoc = qr.nextElement();
			if(refDoc instanceof EPMDocument){
				EPMDocument drawing = (EPMDocument)refDoc;
				if(StringUtils.equals(drawing.getDocType().toString(), "CADDRAWING") 
						&& StringUtils.equals(getSubNumber(drawing.getNumber()), getSubNumber(epm.getNumber())) 
						&& !WorkInProgressHelper.isWorkingCopy(drawing)){
					ov.addElement(drawing);
				}
			}
		}
		if(!ov.isEmpty()){
			QueryResult result = new QueryResult(ov);
			result = new LatestConfigSpec().process(result);
			if(result.hasMoreElements()){
				return (EPMDocument)result.nextElement();
			}
		}
		return null;
	}
	
	public static Iterated getLatestVersionExcludeWorkingCopy(Mastered mastered) throws WTException {
		QueryResult queryResult = VersionControlHelper.service.allVersionsOf(mastered);
		Iterated iterated = (Iterated) queryResult.nextElement();
		if (iterated instanceof Workable && WorkInProgressHelper.isWorkingCopy((Workable) iterated)
				&& queryResult.hasMoreElements()) {
			iterated = (Iterated) queryResult.nextElement();
		}
		return iterated;
	}
	
	/**
	 * 根据EPM(三维)获取关联的Part对象
	 * @param epm
	 * @return
	 * @throws WTException 
	 */
	public static WTPart getLinkPartByEPM(EPMDocument epm) throws WTException{
		ObjectVector ov = new ObjectVector();
		QueryResult qr = PersistenceHelper.manager.navigate(epm, EPMBuildRule.BUILD_TARGET_ROLE,EPMBuildRule.class, false);
		while(qr.hasMoreElements()){
			EPMBuildRule rule = (EPMBuildRule)qr.nextElement();
			AssociationType localAssociationType = AssociationType.getAssociationType(rule, true);
			if(AssociationType.ACTIVE.equals(localAssociationType)){
				WTPart part = (WTPart)rule.getRoleBObject();
				if(StringUtils.equals(part.getNumber(), getSubNumber(epm.getNumber()))){
					ov.addElement(part);
				}
			}
		}
		if(!ov.isEmpty()){
			QueryResult result = new QueryResult(ov);
			result = new LatestConfigSpec().process(result);
			if(result.hasMoreElements()){
				return (WTPart)result.nextElement();
			}
		}
		return null;
	}
	
	public static String getSubNumber(String epmNumber){
		if(StringUtils.isNotBlank(epmNumber)){
			return epmNumber.substring(0, epmNumber.lastIndexOf("."));
		}
		return "";
	}
	
	/**
	 * 判断入参对象是否为CATIADrawing
	 * @param obj
	 * @return
	 * @throws WTException
	 */
	public static boolean isCATIADrawing(Object obj) throws WTException{
		if(obj instanceof EPMDocument){
			EPMDocument epm = (EPMDocument)obj;
			String cadName = epm.getCADName();
			log.info("==isCATIADrawing==cadName:"+cadName);
			if(StringUtils.isNotBlank(cadName) && cadName.endsWith(CATIADrawing_Suffix)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断对象的状态是否为“已发布”状态
	 * @param obj
	 * @return
	 */
	private static boolean isReleased(Object obj){
		if(obj instanceof IteratedLifeCycleManaged){
			IteratedLifeCycleManaged lcm = (IteratedLifeCycleManaged)obj;
			String stateStr = lcm.getState().toString();
			log.info("==isReleased==stateStr:"+stateStr);
			return StringUtils.equals(stateStr, "RELEASED");
		}
		return false;
	}
	
	public static void loadUseMiddlewareEPM(EPMDocument epm, Map<String,Set<InputStream>> all, StringBuilder errorMsg) throws WTException{
		if(isMiddleware(epm)){
			String key = getSubNumber(epm.getNumber());
			if(!all.keySet().contains(key)){
				Set<InputStream> set = checkEPMDoc(epm, errorMsg);
				if(!set.isEmpty()){
					all.put(key, set);
				}
				QueryResult qr = EPMStructureHelper.service.navigateUsesToIteration(epm, null, true, new LatestConfigSpec());
				while(qr.hasMoreElements()){
					Object obj = qr.nextElement();
					if(obj instanceof EPMDocument){
						loadUseMiddlewareEPM((EPMDocument)obj, all, errorMsg);
					}
				}
			}
		}
	}
	
	public static Set<InputStream> checkEPMDoc(EPMDocument epm, StringBuilder errorMsg) throws WTException{
		Set<InputStream> set = new HashSet<InputStream>();
		EPMDocument drawing = getDrawingBy3D(epm);
		if(drawing != null){
			try {
				if(isReleased(drawing)){
					set = getRelesaedPDF(drawing);
				}
				else {
					errorMsg.append(WTMessage.formatLocalizedMessage("2D图纸[{0}]的状态不是已发布 <br>", new Object[]{drawing.getNumber()}));
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new WTException(e.getLocalizedMessage());
			}
			if(set.isEmpty()){
				errorMsg.append(WTMessage.formatLocalizedMessage("2D图纸[{0}]没有加盖DC签章后的PDF文件 <br>", new Object[]{drawing.getNumber()}));
			}
		}
		return set;
	}
	
	public static Set<InputStream> getRelesaedPDF(EPMDocument drawing) throws WTException{
		Set<InputStream> set = new HashSet<InputStream>();
		try {
			QueryResult qr = ContentHelper.service.getContentsByRole(drawing, ContentRoleType.SECONDARY);
			while(qr.hasMoreElements()){
				ApplicationData fileContent = (ApplicationData) qr.nextElement();
				String strFileName = URLDecoder.decode(fileContent.getFileName(), "utf-8");
				if(strFileName.toUpperCase().endsWith(RELEASE_Suffix+PDF_Suffix)){
					set.add(ContentServerHelper.service.findContentStream(fileContent));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new WTException(e.getLocalizedMessage());
		}
		return set;
	}
	
}
