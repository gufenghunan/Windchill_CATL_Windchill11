package com.catl.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ptc.wvs.common.ui.Publisher;
import com.ptc.wvs.common.ui.PublisherAction;

import wt.build.BuildHistory;
import wt.epm.EPMAuthoringAppType;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.build.EPMBuildRule;
import wt.epm.structure.EPMStructureHelper;
import wt.fc.ObjectReference;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.config.ConfigSpec;
import wt.vc.config.LatestConfigSpec;

public class EpmUtil {

    public static Collection getRelatedParts(EPMDocument source) throws WTException {

        QueryResult result = null;
        if (!VersionControlHelper.isLatestIteration(source)) {
            result = PersistenceHelper.manager.navigate(source, BuildHistory.BUILT_ROLE, BuildHistory.class);
        } else {
            result = PersistenceHelper.manager.navigate(source, EPMBuildRule.BUILD_TARGET_ROLE, EPMBuildRule.class);
        }
        if (result != null) {
            return result.getObjectVectorIfc().getVector();
        } else {
            return new ArrayList();
        }
    }
    
    public static Collection getRelatedPartsLasted(EPMDocument source) throws WTException {

        QueryResult result = null;
        if (!VersionControlHelper.isLatestIteration(source)) {
            result = PersistenceHelper.manager.navigate(source, BuildHistory.BUILT_ROLE, BuildHistory.class);
        } else {
            result = PersistenceHelper.manager.navigate(source, EPMBuildRule.BUILD_TARGET_ROLE, EPMBuildRule.class);
        }
        if (result != null) {
        	result = new LatestConfigSpec().process(result);
            return result.getObjectVectorIfc().getVector();
        } else {
            return new ArrayList();
        }
    }

    public static Collection getRelatedEpmdoc(WTPart part) throws WTException {

        QueryResult result = null;
        if (!VersionControlHelper.isLatestIteration(part)) {
            result = PersistenceHelper.manager.navigate(part, BuildHistory.BUILT_BY_ROLE, BuildHistory.class);
        } else {
            result = PersistenceHelper.manager.navigate(part, EPMBuildRule.BUILD_SOURCE_ROLE, EPMBuildRule.class);
        }
        if (result != null) {
            return result.getObjectVectorIfc().getVector();
        } else {
            return new ArrayList();
        }
    }
    public static Collection<EPMDocument> getDrawings(EPMDocument epmDocument) throws WTException {

        List<EPMDocument> uniqueReferencedDocs = new ArrayList<EPMDocument>();
        QueryResult referencedObjects = EPMStructureHelper.service.navigateReferencedBy((EPMDocumentMaster) epmDocument.getMaster(), null, true);
        while (referencedObjects.hasMoreElements()) {
            Object refObject = referencedObjects.nextElement();
            if (refObject instanceof EPMDocument) {
                EPMDocument referencedDoc = (EPMDocument) refObject;
                if (referencedDoc.getDocType().toString().equals("CADDRAWING")) {
                    EPMDocument drawing = (EPMDocument) VersionControlHelper.getLatestIteration(referencedDoc, false);
                    drawing = (EPMDocument) PersistenceHelper.manager.refresh(drawing);
                    if (!uniqueReferencedDocs.contains(drawing)) {
                        uniqueReferencedDocs.add(drawing);
                    }
                }
            }
        }
        return uniqueReferencedDocs;
    }
    
    /**
     * 判断是否为CATIA工程图
     * @param epm
     * @return
     */
    public static boolean isCATDrawing(EPMDocument epm){
    	EPMAuthoringAppType authorapp=epm.getAuthoringApplication();
		String apptype="";
		if(authorapp!=null){
			apptype=authorapp.getDisplay();
		}
		if(apptype.toUpperCase().contains("CATIA")&epm.getNumber().endsWith(".CATDRAWING")){
			return true;
		}
    	return false;
    }
    
    /**
     * 判断是否为CATIA工程图
     * @param epm
     * @return
     */
    public static boolean isCAT3D(EPMDocument epm){
    	EPMAuthoringAppType authorapp=epm.getAuthoringApplication();
		String apptype="";
		if(authorapp!=null){
			apptype=authorapp.getDisplay();
		}
		if(apptype.toUpperCase().contains("CATIA")&(epm.getNumber().endsWith(".CATPART")||epm.getNumber().endsWith(".CATPRODUCT"))){
			return true;
		}
    	return false;
    }
    
    /**
	 * 在工作流中为工程图重新发布可视化
	 * 
	 * @param epm
	 * @throws WTException
	 */
	public static void rePubEpmsReps(EPMDocument epm) throws WTException {

		String objRef = ObjectReference.newObjectReference(epm).toString();
		PublisherAction pa = new PublisherAction(PublisherAction.QUEUEPRIORITY, "H"); // 设置优先级为最高
		new Publisher().doPublish(false, true, objRef, (ConfigSpec) null, (ConfigSpec) null, true, null, null,
				Publisher.EPM, pa.toString(), 0);
		// System.out.println("重新发布结果值：" + result);

	}
	
	 /**
     * 判断是否为Creo三维模型
     * @param epm
     * @return
     */
    public static boolean isCreo3D(EPMDocument epm){
    	EPMAuthoringAppType authorapp=epm.getAuthoringApplication();
		String apptype="";
		if(authorapp!=null){
			apptype=authorapp.getDisplay();
		}
		if(apptype.toUpperCase().contains("CREO")&& (epm.getNumber().toUpperCase().endsWith(".PRT")||epm.getNumber().toUpperCase().endsWith(".ASM"))){
			return true;
		}
    	return false;
    }
    
    /**
     * 判断是否为Creo二维图纸
     * @param epm
     * @return
     */
    public static boolean isCreoDrawing(EPMDocument epm){
    	EPMAuthoringAppType authorapp=epm.getAuthoringApplication();
		String apptype="";
		if(authorapp!=null){
			apptype=authorapp.getDisplay();
		}
		if(apptype.toUpperCase().contains("CREO")&& epm.getNumber().toUpperCase().endsWith(".DRW")){
			return true;
		}
    	return false;
    }
}
