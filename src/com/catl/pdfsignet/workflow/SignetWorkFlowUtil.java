package com.catl.pdfsignet.workflow;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.apache.log4j.Logger;

import com.catl.pdfsignet.PDFSignetUtil;
import com.catl.promotion.PromotionHelper;
import com.itextpdf.text.Image;

import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.maturity.PromotionNotice;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.workflow.definer.WfDefinerHelper;
import wt.workflow.definer.WfProcessDefinition;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;

public class SignetWorkFlowUtil {
	
	private static Logger log = Logger.getLogger(SignetWorkFlowUtil.class);

	/**
	 * 检查发布对象是否含有“线束AutoCAD图纸”或“Catia二维图纸”
	 * @param pbo
	 * @return
	 */
	public static boolean hasDrawingDoc(WTObject pbo){
		if(pbo instanceof PromotionNotice){
			PromotionNotice pn = (PromotionNotice)pbo;
			try {
				QueryResult result= PromotionHelper.getPromotable(pn);
				while(result.hasMoreElements()){
					Object obj = result.nextElement();
					if(PDFSignetUtil.isAutoCADDoc(obj) || PDFSignetUtil.isCATIADrawing(obj) || PDFSignetUtil.isPart(obj)){
						return true;
					}
				}
			} catch (WTException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	/**
	 * 为“线束AutoCAD图纸”或“Catia二维图纸”生成DC签名文件
	 * @param pbo
	 * @return
	 */
	public static String printSignetAndEncryption(WTObject pbo){
		log.info("==SignetWorkFlowUtil=printSignetAndEncryption==Start==");
		StringBuilder errorMsg = new StringBuilder();
		if(pbo instanceof PromotionNotice){
			PromotionNotice pn = (PromotionNotice)pbo;
			try {
				Image image = PDFSignetUtil.getPrintImange(30, 30);
				String pass = PDFSignetUtil.getEncryptionPW();
				QueryResult result= PromotionHelper.getPromotable(pn);
				while(result.hasMoreElements()){
					Object obj = result.nextElement();
					PDFSignetUtil.printSignetAndEncryption(obj, image, pass);
				}
			} catch (WTException e) {
				e.printStackTrace();
				errorMsg.append(e.getLocalizedMessage());
			}
		}
		log.info("====errorMsg:"+errorMsg.toString());
		log.info("==SignetWorkFlowUtil=printSignetAndEncryption==end==");
		return errorMsg.toString();
	}
	
	/**
	 * 启动“CATL生成DC签名文件流程”
	 * @param pbo
	 */
	public static void startSignetWorkFlow(WTObject pbo){
		startWorkFlow("CATL生成DC签名文件流程", pbo, new HashMap<String,Object>());
	}
	
	/**
	 * 启动流程
	 * @param workFlowName
	 * @param pbo
	 * @param variables
	 * @return
	 */
	public static WfProcess startWorkFlow(String workFlowName, WTObject pbo, HashMap<String, Object> variables){
		long WORKFLOW_PRIORITY = 1L;
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		try{
			WTContainerRef containerRef = WTContainerHelper.service.getExchangeRef();
		    if ((pbo instanceof WTContained)) {
		        WTContained contained = (WTContained)pbo;
		        containerRef = contained.getContainerReference();
		    }
		    WTProperties wtproperties = WTProperties.getLocalProperties();
		    WORKFLOW_PRIORITY = Long.parseLong(wtproperties.getProperty("wt.lifecycle.defaultWfProcessPriority", "1"));
		    WfProcessDefinition wfprocessdefinition = WfDefinerHelper.service.getProcessDefinition(workFlowName, containerRef);
		    if(wfprocessdefinition == null){
		    	log.error("Error to getWrokFlowTemplate," + workFlowName + " is null");
		    	return null;
		    }
		    
		    WfProcess wfprocess = WfEngineHelper.service.createProcess(wfprocessdefinition, pbo, containerRef);
		    ProcessData processData = wfprocess.getContext();
		    processData.setValue("primaryBusinessObject", pbo);
		    
		    if ((variables != null) && (!variables.isEmpty())) {
		    	Set<String> keys = variables.keySet();
		    	for (String key : keys) {
		    		processData.setValue(key, variables.get(key));
				}
		    }
		    wfprocess = WfEngineHelper.service.startProcessImmediate(wfprocess, processData, WORKFLOW_PRIORITY);
		    return wfprocess;
		} catch (WTException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return null;
	}
}
