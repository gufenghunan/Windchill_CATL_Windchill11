package com.catl.doc.processor;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import wt.access.AccessControlHelper;
import wt.access.AccessPermission;
import wt.access.AdHocAccessKey;
import wt.access.AdHocControlled;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMasterIdentity;
import wt.fc.Identified;
import wt.fc.IdentityHelper;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.ReferenceFactory;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.part.WTPart;
import wt.services.applicationcontext.implementation.ServiceProperties;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.catl.change.DataUtility.CatlPropertyHelper;
import com.catl.common.constant.AttributeName;
import com.catl.common.constant.TypeName;
import com.catl.common.global.GlobalVariable;
import com.catl.common.util.CatlConstant;
import com.catl.common.util.DocUtil;
import com.catl.common.util.GenericUtil;
import com.catl.common.util.PartUtil;
import com.catl.common.util.PropertiesUtil;
import com.catl.common.util.WorkflowUtil;
import com.catl.doc.CatlDocNewNumber;
import com.catl.doc.DocInvalidUtil;
import com.catl.doc.EDatasheetDocUtil;
import com.catl.doc.workflow.DocClassificationModelNew;
import com.catl.doc.workflow.DocWfUtil;
import com.catl.ecad.load.ObjectTypeUtil1;
import com.catl.integration.log.TransactionLogHelper;
import com.catl.integration.log.WebServiceTransactionInfo;
import com.catl.integration.log.WebServiceTransactionLog;
import com.catl.integration.rdm.ObjectLinkedByRdm;
import com.catl.integration.rdm.RdmIntegrationHelper;
import com.catl.line.constant.ConstantLine;
import com.catl.line.util.WTDocumentUtil;
import com.catl.part.relation.PartRelationUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.meta.common.UpdateOperationIdentifier;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmContext;
import com.ptc.windchill.enterprise.doc.forms.CreateDocFormProcessor;

public class CatlDocCreateProcessor extends CreateDocFormProcessor {

	private static Logger logger = Logger.getLogger(CatlDocCreateProcessor.class.getName());

	@Override
	public FormResult preProcess(NmCommandBean nmcommandBean, List<ObjectBean> list) throws WTException {

		Map<String, String> map = nmcommandBean.getText();

		String location = map.get("Location");
		logger.debug("doc...................Location=" + location);
		if (location != null) {
			if (location.split("/").length == 2) {
				throw new WTException("不能在根目录创建文档！");
			}
		}

		String brand = null;
		String model = null;

		for (String key : map.keySet()) {
			if (key.contains("CATL_Brand~~")) {
				brand = map.get(key);
			} else if (key.contains("CATL_Model~~")) {
				model = map.get(key);
			}
		}

		String createType = ((List) nmcommandBean.getComboBox().get("createType")).get(0).toString();
		if (createType.contains(CatlConstant.DATASHEET_DOC_TYPE)) {
			String name = brand + "_" + model;

			WTDocument existDoc = EDatasheetDocUtil.getEDatasheetDocByName(name, null);
			if (existDoc != null) {
				throw new WTException("系统中已存在名称为[" + name + "]的电子电气件Datasheet,文档编号为[" + existDoc.getNumber() + "]");
			}
			map.put("name_col_name", name);
		}

		return super.preProcess(nmcommandBean, list);
	}

	@Override
	public FormResult postProcess(NmCommandBean nmcommandBean, List<ObjectBean> list) throws WTException {
		FormResult result = super.postProcess(nmcommandBean, list);
		StringBuffer message = new StringBuffer();
		try {
			message = checkClientData(nmcommandBean, list);
		} catch (WTPropertyVetoException e) {
			message.append(e.getMessage());
			e.printStackTrace();
		}
		if(message.length() == 0)
			message.append(handleRDM(nmcommandBean, list));
		WTDocument doc = (WTDocument)list.get(0).getObject();
		String createType = ((List) nmcommandBean.getComboBox().get("createType")).get(0).toString();
		if(createType != null && (createType.contains(TypeName.technicalDoc) || createType.contains(TypeName.rdDoc)
				|| createType.contains(TypeName.pcbaDrawing) || createType.contains(TypeName.gerberDoc) || createType.contains(TypeName.kopdmdoc))){
			message.append(DocWfUtil.checkSubmit(list));

		}
		
		//文档失效流程
		if(createType != null && createType.contains(TypeName.technicalDoc)) {
			
			//ENW文档，预计失效日期为必填
			message.append(DocInvalidUtil.checkENWSubmit(list));
		}
		
		if (message.length() > 0) {
			throw new WTException("错误信息：" + message);
		}

		setDocIBA(nmcommandBean, list);
		
		try {
			DocUtil.replaceDocumentPrimaryContent(doc);
		} catch (Exception e) {
			logger.debug("文档模版上传错误，请上传正确的文档模版。文档编号="+doc.getNumber());
			e.printStackTrace();
			throw new WTException("文档模版上传错误，请上传正确的文档模版。");
		}

		return result;
	}
	
		


	private String handleRDM(NmCommandBean nmcommandBean, List<ObjectBean> list) {
		StringBuffer message = new StringBuffer();
		String deliverableId = nmcommandBean.getTextParameter("deliverableId");
		if (deliverableId != null && !deliverableId.equals("null")) {
			WebServiceTransactionLog transactionLog = new WebServiceTransactionLog();
			WebServiceTransactionInfo transactionInfo = new WebServiceTransactionInfo();
			try {
				WTDocument doc = (WTDocument) list.get(0).getObject();
				ObjectLinkedByRdm rdm = new ObjectLinkedByRdm();
				rdm.setDeliverableId(deliverableId);
				rdm.setBranchId(doc.getBranchIdentifier());
				rdm.setObjectNumber(doc.getNumber());
				rdm.setObjectType(rdm.toString());
				PersistenceHelper.manager.save(rdm);
				// 。。。。。。。。。。。。。。通过webservice发送 消息给RDM

				transactionLog.setServiceType(WebServiceTransactionLog.SERVICE_TYPE_CLIENT);
				transactionLog.setServiceSide("ERP");
				transactionLog.setClientSide("PLM");
				transactionLog.setClientId(" ");
				transactionLog.setClientClass(RdmIntegrationHelper.class.getName());
				transactionLog.setClientMethod("receiveCreateStatus");
				transactionLog.setStartTime(new Timestamp(System.currentTimeMillis()));
				transactionLog.setTransactionInfo(transactionInfo);
				transactionInfo.setParameterObject(rdm);

				Map<String, String> map = new HashMap<String, String>();
				map.put("deliverableId", deliverableId);
				map.put("docbranchId", doc.getBranchIdentifier() + "");
				map.put("docNumber", doc.getNumber());
				map.put("docName", doc.getName());
				String ret = RdmIntegrationHelper.receiveCreateStatusToRDM(map);
				if (ret != null) {
					JSONObject json = new JSONObject(ret);
					String msg = json.getString("message");
					String result = json.getString("result");
					if (result != null && !result.equals("0")) {
						message.append("与RDM集成出现异常，无法完成文档创建，请联系系统管理员！错误描述:" + msg + "\n");
					}
				}
				// 记录交易日志的结果信息的信息
				transactionLog.setFinishTime(new Timestamp(System.currentTimeMillis()));
				transactionLog.setStatus(WebServiceTransactionLog.STATUS_SUCCESS);
				transactionInfo.setResultObject(ret);
			} catch (Exception e) {
				message.append("与RDM集成出现异常，无法完成文档创建，请联系系统管理员！错误描述:" + e.getMessage());
				transactionLog.setFinishTime(new Timestamp(System.currentTimeMillis()));
				transactionLog.setStatus(WebServiceTransactionLog.STATUS_FAILED);
				transactionInfo.setException(e);
				e.printStackTrace();
			} finally {
				TransactionLogHelper.logTransaction(transactionLog);
			}
		}
		return message.toString();
	}

	private static StringBuffer checkClientData(NmCommandBean nmcommandBean, List<ObjectBean> list) throws WTException, WTPropertyVetoException {
		// 研发过程文档外，不出现选择Part的步骤
		// 选择其他文档（具体是产品技术文件，GERBER文件、PCBA装配图、线束AUTOCAD图纸
		WTDocument doc = (WTDocument)list.get(0).getObject();
		
		StringBuffer message = new StringBuffer();

		String createType = ((List) nmcommandBean.getComboBox().get("createType")).get(0).toString();
		logger.debug("createType====" + createType);

		HashMap map = nmcommandBean.getText();
		
		Properties wtproperties = ServiceProperties.getServiceProperties("WTServiceProviderFromProperties");
		String mustInputDocIdDocType = wtproperties.getProperty("mustInputDocIdDocType");
		List<String> doctypelist = new ArrayList<String>();
        if(mustInputDocIdDocType != null){
        	String[] docTypeArr = mustInputDocIdDocType.split(",");
        	doctypelist = Arrays.asList(docTypeArr); 

        }
        
        String docClsname = (String)GenericUtil.getObjectAttributeValue(doc, "subCategory");
        
        DocWfUtil.checkNewFile();
        
        Map<String,String> typemap = ObjectTypeUtil1.getAllTypeInternalDisMap();
    	String displayName = typemap.get(createType);
    	String type = docClsname;
    	displayName = displayName+docClsname;
    	
		for (Object key : map.keySet()) {
			if (key.toString().contains("docID~~")) {
				String docID = (String) map.get(key);
				
				if (StringUtils.isNotEmpty(docClsname)){
					docClsname = docClsname.substring(docClsname.lastIndexOf("-")+1, docClsname.length());
				}
				if (doctypelist.contains(docClsname) && StringUtils.isEmpty(docID)){
					message.append("该文档细类的文档ID为必填！\n");
				}
				
				if (docID != null && !docID.equalsIgnoreCase("Default") && !docID.trim().equals("")) {
					Set<WTDocument> set = DocUtil.getLastedDocByStringIBAValue(doc.getNumber(), doc.getVersionIdentifier().getValue(), "docID", docID);
					for (WTDocument temp : set) {
						message.append("文档ID:" + docID + "不是唯一,已被文档:" + temp.getNumber() + "使用\n");
					}
				}
			}
		}
		List<NmContext> selected = nmcommandBean.getSelected();
		boolean selectpart = false;
		for (int i = 0; i < selected.size(); i++) {
			NmContext nmContext = (NmContext) selected.get(i);
			NmOid nmOid = nmContext.getTargetOid();
			Object object = nmOid.getRefObject();
			if (object instanceof WTPart) {
				selectpart = true;
			}
		}
		/*
		 * boolean isNeedPart =
		 * createType.contains(TypeName.doc_type_pcbaDrawing) ||
		 * createType.contains(TypeName.doc_type_autocadDrawing) ||
		 * createType.contains
		 * (TypeName.doc_type_gerberDoc)||createType.contains(
		 * TypeName.doc_type_technicalDoc);
		 */
		WTPart selpart=null;    	
    	
		if (!selectpart && isDrawingDoc(createType)) {
           //if(doc.getNumber().startsWith(PropertiesUtil.getValueByKey("config_topdf_number_prefix"))){
        	   message.append("图纸类的文档必须并且只能选择一个关联的部件。\n");
           //}
			
		}else if (!selectpart && isSoftwareDoc(createType)) {
			message.append("产品类软件的文档必须并且只能选择一个关联的软件部件。\n");
		}
		else if (!selectpart && isProductSoftDoc(createType)) {

			message.append("产品类软件的文档必须并且只能选择一个关联的编号为P开头的部件。\n");

		}/*else if(!selectpart && createType.contains(TypeName.mdDoc)){
			message.append("产品模具开发图文档必须并且只能选择一个关联的部件。\n");
		}else if(!selectpart && (createType.contains(TypeName.sordoc) && docClsname.contains("SOR"))){
			message.append("产品结构开发文档必须选择一个关联的部件。\n");
		}*/else if (!selectpart && isProductrdDoc(createType,docClsname)){
			message.append("研发过程文档中细类为\"eBus高成熟度半成品评审报告-SPRR\"必须关联PN。\n");
		}else if(!selectpart){
			DocClassificationModelNew classModeNew = GlobalVariable.docneedPnConfigBean.get(displayName);
			String needPN = classModeNew.getNeedPN();
			
			if("YES".equalsIgnoreCase(needPN)){
				message.append(type+"类型的文档必须选择一个关联的部件。\n");
			}
		}else if (selected.size() > 0 && selectpart) {
			for (int i = 0; i < selected.size(); i++) {
				NmContext nmContext = (NmContext) selected.get(i);
				NmOid nmOid = nmContext.getTargetOid();
				Object obj = nmOid.getRefObject();

				if (obj instanceof WTPart) {
					WTPart part = (WTPart) obj;
					selpart=part;
					// relation the doc and part
					logger.debug("Part Selected: " + part.getNumber());
					PartRelationUtil util = new PartRelationUtil();
					List<WTDocument> docList = new ArrayList<WTDocument>();
					docList.add(doc);
					util.relatePartDoc(part, docList);
					// change document number if associate with part not for
					// technicaldoc

					boolean issoftdocpart = false;
					if(isSoftwareDoc(createType)){
						if(!PartUtil.isSWPart(selpart)){
							message.append("软件文档必须且只能关联软件部件！\n");
						}else{
							issoftdocpart = true;
						}
					}
					
					if (isDrawingDoc(createType) || issoftdocpart) {
						
						WTDocument olddoc = DocUtil.getLatestWTDocument(part.getNumber());
						if (olddoc == null) {
							WTPrincipal wtprincipal = SessionHelper.getPrincipal();
							WTPrincipalReference wtprincipalreference = (WTPrincipalReference) (new ReferenceFactory()).getReference(wtprincipal);
							SessionContext previous = SessionContext.newContext();
							SessionHelper.manager.setAdministrator();
							AccessControlHelper.manager.addPermission((AdHocControlled) doc, wtprincipalreference, AccessPermission.MODIFY_IDENTITY, AdHocAccessKey.WNC_ACCESS_CONTROL);
							PersistenceServerHelper.manager.update((Persistable) doc);
							doc = (WTDocument) PersistenceHelper.manager.refresh((Persistable) doc);

							Identified aIdentified = (Identified) doc.getMaster();
							WTDocumentMasterIdentity identity = (WTDocumentMasterIdentity) aIdentified.getIdentificationObject();
							if(createType.contains(TypeName.pcbaDrawing))
							{
								identity.setNumber("ASSEMBLY-"+part.getNumber());
							}
							else if(createType.contains(TypeName.gerberDoc))
							{
								identity.setNumber("GERBER-"+part.getNumber());
							}else{
								identity.setNumber(part.getNumber());
							}
							
							//identity.setNumber(part.getNumber());
							IdentityHelper.service.changeIdentity(aIdentified, identity);
							AccessControlHelper.manager.removePermission((AdHocControlled) doc, wtprincipalreference, AccessPermission.MODIFY_IDENTITY, AdHocAccessKey.WNC_ACCESS_CONTROL);
							SessionContext.setContext(previous);

						} else {
							message.append("部件:" + part.getNumber() + "已经关联过文档,不能再次关联！请使用" + part.getNumber() + "编号的文档关联\n");
						}
					}else if(createType.contains(TypeName.productSoftDoc)){
						if(!part.getNumber().startsWith("P")){
							message.append("产品类软件的文档必须并且只能选择一个关联编号为P开头的部件。\n");
						} 
					} 
//					else if (isProductrdDoc(createType,docClsname)){
//						String PartGroup = wtproperties.getProperty("PartGroup");
//						String partNumber = part.getNumber().split("-")[0];
//						if (PartGroup.indexOf(partNumber)==-1) {
//							message.append("SPRR文档关联的PN为：【"+part.getNumber()+"】必须是给定的物料组（高压控制盒和电箱）。\n");
//						}
//						
//					}

				}// end of WTPart
			}// end selected size >0
		}// end of selected is part
		if (createType.contains(TypeName.doc_type_autocadDrawing)) {
			doc = (WTDocument) PersistenceHelper.manager.refresh(doc);
			if(selpart!=null){
				String prefix_number=selpart.getNumber().substring(0,4);
				String topdf_prefix=PropertiesUtil.getValueByKey("config_topdf_prefix");
				String [] prefix=topdf_prefix.split(",");
				List prefixlist=Arrays.asList(prefix);
	            if(!prefixlist.contains(prefix_number)){
					String pdfCheckResult = DocUtil.pdfFileCheck(doc);
					if (pdfCheckResult != null) {
						message.append(pdfCheckResult + "\n");
					}
				}
			}
		} else if (createType.contains(CatlConstant.DATASHEET_DOC_TYPE)) {
			WorkflowUtil.startWorkFlow("Datasheet审签流程", doc, null);
		}/*else if(createType.contains(TypeName.theSimulationReport) || createType.contains(TypeName.mdDoc)){
			String prefix="";
			int maxnumber=0;
			if(docClsname.contains(TypeName.msr)){
				prefix="MSR-";
			}else if(docClsname.contains(TypeName.md)){
				prefix="MD-";
			}else if(docClsname.contains(TypeName.pqtr)){
				prefix="PQTR-";
			}else if(docClsname.contains(TypeName.mmp)){
				prefix="MMP-";
			}else if(docClsname.contains(TypeName.mqc)){
				prefix="MQC-";
			}else if(docClsname.contains(TypeName.pscae)){
				prefix="PSCAE";
			}
			try {
				maxnumber=CatlDocNewNumber.queryMaxDocNumber(docClsname);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String nextNumberString = String.valueOf(maxnumber);
			while(nextNumberString.length()<8){
				nextNumberString="0"+nextNumberString;
			}
			nextNumberString=prefix+nextNumberString;
			setDocNumber(doc,nextNumberString);
		}*/
		return message;
	}

	/*
	 * Project Code is the first part of the folder, and the project short name
	 * is include in the second part for example STUYBEV003（ZZT_44.5KWh）,
	 * project code is STUBYBEV003, the second is ZZT_44.5KWh
	 */

	public static boolean isDrawingDoc(String createType) {
		return createType == null ? false : createType.contains(TypeName.doc_type_pcbaDrawing) || createType.contains(TypeName.doc_type_autocadDrawing) || createType.contains(TypeName.doc_type_gerberDoc);
	}
	
	/**
	 * 是否为产品类软件文档
	 * update by szeng 2017-09-04
	 * @param createType
	 * @return
	 */
	public static boolean isProductSoftDoc(String createType) {
		return createType == null ? false : createType.contains(TypeName.productSoftDoc);
	}
	
	/**
	 * 是否为类软件图档
	 * @param createType
	 * @return
	 */
	public static boolean isSoftwareDoc(String createType) {
		return createType == null ? false : createType.contains(TypeName.softwareDoc);
	}
	
	/**
	 * 是否为eBus高成熟度半成品评审报告-SPRR
	 * update by szeng 2017-09-04
	 * @param createType
	 * @return
	 */
	public static boolean isProductrdDoc(String createType, String docClsname) {
		if (createType.contains(TypeName.rdDoc)&&docClsname.contains("SPRR")) {
				return true;
		}
		return false;
	}

	private void setDocIBA(NmCommandBean nmcommandBean, List<ObjectBean> list) throws WTException {
		if (list.size() > 0) {

			// 文档的项目代号截取自项目文件夹名称
			// 文档的项目简称截取自项目文件夹名称
			// get project folder name
			String folderPath = (String) nmcommandBean.getText().get("Location");
			logger.debug("Folder Name = " + folderPath);
			WTDocument doc = (WTDocument) list.get(0).getObject();
			boolean isCommanContainer = false;
			String docContainer = doc.getContainerName();
			String[] commonContainerS = CatlConstant.COMMON_DOCUMENT_CONTAINER.split(",");
			for (String commonContainer : commonContainerS) {
				if (commonContainer.equals(docContainer)) {
					isCommanContainer = true;
				}
			}
			if (!isCommanContainer) {// 不是文档通用容器
				String projectShot = "";
				String projectCode = "";
				if (folderPath != null) {
					String[] projectSplit = folderPath.split("/");
					if (projectSplit.length >= 3) {
						String projectFolderName = projectSplit[2];
						logger.debug("projectFolderName = " + projectFolderName);

						int beginIndex = projectFolderName.indexOf("（");
						int endIndex = projectFolderName.indexOf("）");
						int beginIndex1 = projectFolderName.indexOf("(");
						int endIndex1 = projectFolderName.indexOf(")");
						if (beginIndex1 > beginIndex) {
							beginIndex = beginIndex1;
						}
						if (endIndex1 > endIndex) {
							endIndex = endIndex1;
						}

						if (0 < beginIndex && beginIndex + 1 < endIndex) {
							projectCode = projectFolderName.substring(0, beginIndex);
							projectShot = projectFolderName.substring(beginIndex + 1, endIndex);
						} else {
							projectCode = projectFolderName;
						}
					}
					logger.debug("project code  = " + projectCode);
					logger.debug("poject short name =  " + projectShot);
					try {
						PersistableAdapter genericObj = new PersistableAdapter(doc, null, null, new UpdateOperationIdentifier());
						genericObj.load(AttributeName.DOC_PROEJCT_NAME);
						genericObj.set(AttributeName.DOC_PROEJCT_NAME, projectShot);
						Persistable updatedObject = genericObj.apply();
						doc = (WTDocument) PersistenceHelper.manager.save(updatedObject);
						genericObj.load(AttributeName.DOC_PROJECT_CODE);
						genericObj.set(AttributeName.DOC_PROJECT_CODE, projectCode);
						updatedObject = genericObj.apply();
						doc = (WTDocument) PersistenceHelper.manager.save(updatedObject);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	
	public static void setDocNumber(WTDocument doc,String number) throws WTException, WTPropertyVetoException{
		WTPrincipal wtprincipal = SessionHelper.getPrincipal();
		WTPrincipalReference wtprincipalreference = (WTPrincipalReference) (new ReferenceFactory()).getReference(wtprincipal);
		SessionContext previous = SessionContext.newContext();
		SessionHelper.manager.setAdministrator();
		AccessControlHelper.manager.addPermission((AdHocControlled) doc, wtprincipalreference, AccessPermission.MODIFY_IDENTITY, AdHocAccessKey.WNC_ACCESS_CONTROL);
		//PersistenceServerHelper.manager.update((Persistable) doc);
		doc = (WTDocument) PersistenceHelper.manager.refresh((Persistable) doc);

		Identified aIdentified = (Identified) doc.getMaster();
		WTDocumentMasterIdentity identity = (WTDocumentMasterIdentity) aIdentified.getIdentificationObject();
		identity.setNumber(number);
		IdentityHelper.service.changeIdentity(aIdentified, identity);
		AccessControlHelper.manager.removePermission((AdHocControlled) doc, wtprincipalreference, AccessPermission.MODIFY_IDENTITY, AdHocAccessKey.WNC_ACCESS_CONTROL);
		SessionContext.setContext(previous);
	}
}
