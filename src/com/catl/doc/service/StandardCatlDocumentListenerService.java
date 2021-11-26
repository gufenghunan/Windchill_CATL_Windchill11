package com.catl.doc.service;

import java.beans.PropertyVetoException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMasterIdentity;
import wt.epm.EPMDocument;
import wt.events.KeyedEvent;
import wt.events.KeyedEventListener;
import wt.fc.Identified;
import wt.fc.IdentityHelper;
import wt.services.ServiceEventListenerAdapter;
import wt.services.StandardManager;
import wt.util.WTException;
import wt.vc.VersionControlServiceEvent;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.WorkInProgressServiceEvent;

import com.catl.common.util.CatlConstant;
import com.catl.common.util.DocUtil;
import com.catl.common.util.GenericUtil;
import com.catl.common.util.PropertiesUtil;
import com.catl.doc.EDatasheetDocUtil;

public class StandardCatlDocumentListenerService extends StandardManager implements CatlDocumentListenerService {
	private static final long serialVersionUID = 5266098956681288190L;

	private KeyedEventListener listener = null;

	private static Logger log = Logger.getLogger(StandardCatlDocumentListenerService.class.getName());

	public String getConceptualClassname() {
		return StandardCatlDocumentListenerService.class.getName();
	}

	public static StandardCatlDocumentListenerService newStandardCatlDocumentListenerService() throws WTException {
		StandardCatlDocumentListenerService instance = new StandardCatlDocumentListenerService();
		instance.initialize();
		return instance;
	}

	protected void performStartupProcess() {
		log.debug(">>>++++++++++++++++++++++++resigiser service start:StandardCatlDocumentListenerService");
		listener = new CatlEventListener(getConceptualClassname());
		log.debug(">>>++++++++++++++++++++++++resiger service end:StandardCatlDocumentListenerService");
		getManagerService().addEventListener(listener, WorkInProgressServiceEvent.generateEventKey("PRE_CHECKIN"));
		getManagerService().addEventListener(listener, WorkInProgressServiceEvent.generateEventKey("POST_CHECKOUT"));
		getManagerService().addEventListener(listener, VersionControlServiceEvent.generateEventKey("NEW_VERSION"));

	}

	class CatlEventListener extends ServiceEventListenerAdapter {
		public CatlEventListener(String manager_name) {
			super(manager_name);
		}

		public void notifyVetoableEvent(Object event) throws Exception {
			if (!(event instanceof KeyedEvent))
				return;
			KeyedEvent eventObject = (KeyedEvent) event;
			Object busObject = eventObject.getEventTarget();
			log.debug(">>>++++++++++++++++ eventObject=" + eventObject.getEventType());
			if (eventObject.getEventType().equals("PRE_CHECKIN")) {
				if (busObject instanceof WTDocument) {
					WTDocument document = (WTDocument) busObject;
					boolean checkoutFlag = WorkInProgressHelper.isCheckedOut(document);
					if (checkoutFlag) {// get the workcopy to check
						if (!WorkInProgressHelper.isWorkingCopy(document)) {
							document = (WTDocument) WorkInProgressHelper.service.workingCopyOf(document);
						}
					}
					StringBuffer message = new StringBuffer();
					String docID = (String)GenericUtil.getObjectAttributeValue(document,"docID");
					if(docID != null){
						Set<WTDocument> set = DocUtil.getLastedDocByStringIBAValue(document.getNumber(),document.getVersionIdentifier().getValue(),"docID",docID);
						for(WTDocument temp : set){
							message.append("文档ID:"+docID+"不是唯一,已被文档:"+temp.getNumber()+"使用\n");
						}
					}
					if (isAutocadOrCatiaDrawing(document)) {
						String prefix_number=document.getNumber().substring(0,4);
						String topdf_prefix=PropertiesUtil.getValueByKey("config_topdf_prefix");
						String [] prefix=topdf_prefix.split(",");
						List prefixlist=Arrays.asList(prefix);
                        if(!prefixlist.contains(prefix_number)){
						String pdfCheckResult = DocUtil.pdfFileCheck(document);
						if (pdfCheckResult != null) {
							message.append(pdfCheckResult);
						}
					    }
					} else if (EDatasheetDocUtil.isEDatasheetDoc(document)) {
						String brand = (String) GenericUtil.getObjectAttributeValue(document, "CATL_Brand");
						String model = (String) GenericUtil.getObjectAttributeValue(document, "CATL_Model");
						String name = brand + "_" + model;
						if (!document.getName().equalsIgnoreCase(name)) {
							WTDocument existDoc = EDatasheetDocUtil.getEDatasheetDocByName(name, document.getNumber());
							if (existDoc != null) {
								message.append("系统中已存在名称为[" + name + "]的Datasheet文档编号为[" + existDoc.getNumber() + "]");
							} else {
								Identified identified = (Identified) document.getMaster();
								WTDocumentMasterIdentity identity = (WTDocumentMasterIdentity) identified.getIdentificationObject();
								identity.setName(name);
								IdentityHelper.service.changeIdentity(identified, identity);
							}
						}
					}
					if(message.length() >0){
						throw new WTException(message.toString());
					}
				}
			}else if (eventObject.getEventType().equals("POST_CHECKOUT")) {
				if (busObject instanceof WTDocument) {
					WTDocument document = (WTDocument) busObject;
					if (isAutocadOrCatiaDrawing(document)) {
						boolean checkoutFlag = WorkInProgressHelper.isCheckedOut(document);
						if (checkoutFlag) {// get the workcopy to check
							if (!WorkInProgressHelper.isWorkingCopy(document)) {
								document = (WTDocument) WorkInProgressHelper.service.workingCopyOf(document);
							}
						}
						removeFilePrintAttachement(document);
					}
				}
				if (busObject instanceof EPMDocument) {
					EPMDocument epm = (EPMDocument) busObject;
					String epmtype = epm.getDocType().toString();
					if (epmtype.endsWith("CADDRAWING")) {
						boolean checkoutFlag = WorkInProgressHelper.isCheckedOut(epm);
						if (checkoutFlag) {// get the workcopy to check
							if (!WorkInProgressHelper.isWorkingCopy(epm)) {
								epm = (EPMDocument) WorkInProgressHelper.service.workingCopyOf(epm);
							}
						}
						removeFilePrintAttachement(epm);
					}
				}
			}else if (eventObject.getEventType().equals("NEW_VERSION")) {
				if (busObject instanceof WTDocument) {
					WTDocument document = (WTDocument) busObject;
					if (isAutocadOrCatiaDrawing(document)) {
						boolean checkoutFlag = WorkInProgressHelper.isCheckedOut(document);
						if (checkoutFlag) {// get the workcopy to check
							if (!WorkInProgressHelper.isWorkingCopy(document)) {
								document = (WTDocument) WorkInProgressHelper.service.workingCopyOf(document);
							}
						}
						removeFilePrintAttachement(document);
					}
				}
			}
		}
	}

	public static Boolean isAutocadOrCatiaDrawing(WTDocument doc) {
		Boolean isdoc = false;
		if (null == doc) {
			return isdoc;
		} else {
			String doctypeString = "";
			try {
				doctypeString = DocUtil.getObjectType(doc).toString();
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.debug("doc type==" + doc.getNumber() + ":" + doctypeString);
			if (doctypeString.endsWith(CatlConstant.AUTOCAD_DOC_TYPE) || doctypeString.endsWith(CatlConstant.CATIA_DOC_TYPE)) {
				isdoc = true;
			}
		}
		return isdoc;
	}

	private void removeFilePrintAttachement(ContentHolder contentholder) {
		log.debug(">>>>>>" + StandardCatlDocumentListenerService.class.getName() + ".removeFilePrintAttachement()...");
		try {
			if (contentholder != null) {
				ContentHolder doc = ContentHelper.service.getContents(contentholder);
				Vector vApplicationData = ContentHelper.getApplicationData(doc);
				for (int i = 0; i < vApplicationData.size(); i++) {
					ApplicationData applicationdata = (ApplicationData) vApplicationData.elementAt(i);
					log.debug("...removeAttachment 清除附件：" + applicationdata.getFileName());
					if (applicationdata.getFileName().endsWith("pdf")) {
						ContentServerHelper.service.deleteContent(doc, applicationdata);
					}
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		} finally {
			log.debug("<<<<<<" + StandardCatlDocumentListenerService.class.getName() + ".removeFilePrintAttachement().");
		}
	}

}
