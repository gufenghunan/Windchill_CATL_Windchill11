package com.catl.integration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import wt.change2.ChangeException2;
import wt.change2.WTChangeOrder2;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.structure.EPMReferenceLink;
import wt.epm.structure.EPMStructureHelper;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTCollection;
import wt.fc.collections.WTList;
import wt.maturity.MaturityException;
import wt.maturity.MaturityHelper;
import wt.maturity.PromotionNotice;
import wt.part.Source;
import wt.part.WTPart;
import wt.part.WTPartConfigSpec;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartSubstituteLink;
import wt.part.WTPartUsageLink;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.config.ConfigHelper;
import wt.vc.config.LatestConfigSpec;

import com.catl.bom.workflow.BomWfUtil;
import com.catl.cad.BatchDownloadPDFUtil;
import com.catl.change.report.ecn.ECNAttachmentHtml;
import com.catl.common.constant.CadState;
import com.catl.common.constant.Constant;
import com.catl.common.constant.ContainerName;
import com.catl.common.constant.DocState;
import com.catl.common.constant.PartState;
import com.catl.common.constant.TypeName;
import com.catl.common.util.CatlConstant;
import com.catl.common.util.EpmUtil;
import com.catl.common.util.GenericUtil;
import com.catl.common.util.IBAUtil;
import com.catl.common.util.PropertiesUtil;
import com.catl.common.util.TypeUtil;
import com.catl.common.util.ZipDoc;
import com.catl.integration.log.DrawingSendERPLog;
import com.catl.integration.pi.sourceChange.DTBESKZCreateResponse;
import com.catl.line.constant.ConstantLine;
import com.catl.line.helper.ExpressHelper;
import com.catl.loadData.StrUtils;
import com.catl.part.PartConstant;
import com.catl.part.classification.RefreshFAEStatusUtil;
import com.catl.part.sourceChange.PartSourceChangeLogHelper;
import com.catl.pdfsignet.PDFSignetUtil;
import com.catl.promotion.bean.SourceChangeXmlObjectBean;
import com.catl.promotion.dbs.SourceChangeXmlObjectUtil;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.windchill.enterprise.change2.commands.RelatedChangesQueryCommands;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;

public class ReleaseUtil {

	private static Logger log = Logger.getLogger(ReleaseUtil.class.getName());
	private static PIService service = PIService.getInstance();
	private static SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
	public static final String PDF_Suffix = ".PDF";

	public static final String RELEASE_Suffix = "_RELEASE";
	public static String ROOT_DIRECTORY = "/nfs/drawing_send_erp/";
	public static String ROOT_DIRECTORY_MAPPING = "\\\\xxx.xx.xx.xx\\drawing_send_erp\\";
	public static final String CATIADrawing_Suffix = ".CATDrawing";
	
	static{
		try {
			String drawing_send_erp = GenericUtil.getPreferenceValue("/catl/drawing_send_erp");
			String drawing_send_erp_mapping = GenericUtil.getPreferenceValue("/catl/drawing_send_erp_mapping");
			ROOT_DIRECTORY = drawing_send_erp;
			ROOT_DIRECTORY_MAPPING = drawing_send_erp_mapping;
		} catch (WTException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设计状态发布part到ERP
	 * 
	 * @param pbo
	 * @return
	 */
	public static ErpResponse designrelease(WTObject pbo, String releaseSuccessParts) {
		log.debug("begin release................");
		log.debug("releaseSuccessParts:" + releaseSuccessParts);
		ErpResponse retResponse = new ErpResponse();
		retResponse.setSuccess(true);
		retResponse.setMessage(new ArrayList<Message>());
		ErpResponse partResponse = null;
		boolean program_exception = false;
		ErpData erpData = null;
		try {
			erpData = getDesignErpData(pbo, releaseSuccessParts);
			List<PartInfo> parts = new ArrayList<PartInfo>();
			for (PartInfo partInfo : erpData.getParts()) {
				if (partInfo.getVersionBig().equals("1")) {
					if(isSpeicalClf(partInfo.getPartNumber())){
					parts.add(partInfo);
					}
				} 
			}
			if (parts.size() > 0) {
				partResponse = service.sendParts(parts, Constant.COMPANY);
			}
		} catch (ParseException e) {
			program_exception = true;
			retResponse.setSuccess(false);
			List<Message> list = new ArrayList<Message>();
			Message msg = new Message();
			msg.setSuccess(false);
			msg.setAction(Message.PROGRAM_EXCEPTION);
			msg.setText("程序异常 错误信息：" + e.getMessage());
			list.add(msg);
			retResponse.setMessage(list);
			e.printStackTrace();
		} catch (WTException e) {
			program_exception = true;
			retResponse.setSuccess(false);
			List<Message> list = new ArrayList<Message>();
			Message msg = new Message();
			msg.setSuccess(false);
			msg.setAction(Message.PROGRAM_EXCEPTION);
			msg.setText("程序异常 错误信息：" + e.getMessage());
			list.add(msg);
			retResponse.setMessage(list);

			e.printStackTrace();
		} catch (Exception e) {
			program_exception = true;
			retResponse.setSuccess(false);
			List<Message> list = new ArrayList<Message>();
			Message msg = new Message();
			msg.setSuccess(false);
			msg.setAction(Message.PROGRAM_EXCEPTION);
			msg.setText("程序异常 错误信息：" + e.getMessage());
			list.add(msg);
			retResponse.setMessage(list);
			e.printStackTrace();
		} finally {
			try {
				if (partResponse != null) {
					if (!partResponse.isSuccess())
						retResponse.setSuccess(false);
					retResponse.getMessage().addAll(partResponse.getMessage());
				} else if (program_exception) {
					for (PartInfo partInfo : erpData.getParts()) {
						if (partInfo.getEcnNumber() == null) {
							Message msg = new Message();
							msg.setSuccess(false);
							msg.setNumber(partInfo.getPartNumber());
							msg.setAction(Message.PART_CREATE);
							msg.setText(Message.PROGRAM_EXCEPTION);
							retResponse.getMessage().add(msg);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		log.debug("end release................");
		return retResponse;
	}	
	
	

	/**
	 * 发布part,bom到ERP
	 * 
	 * @param pbo
	 * @return
	 */
	public static ErpResponse release(WTObject pbo, String releaseSuccessParts, String releaseSuccessBoms) {
		log.debug("begin release................");
		log.debug("releaseSuccessParts:" + releaseSuccessParts + "||releaseSuccessBoms:" + releaseSuccessBoms);
		ErpResponse retResponse = new ErpResponse();
		retResponse.setSuccess(true);
		retResponse.setMessage(new ArrayList<Message>());

		ErpResponse partResponse = null;
		ErpResponse partChangeResponse = null;
		ErpResponse bomResponse = null;
		ErpResponse bomChangeResponse = null;
		ErpResponse drawingResponse = null;
		ErpResponse startPResponse = null;
		boolean program_exception = false;
		ErpData erpData = null;
		try {
			erpData = getErpData(pbo, releaseSuccessParts, releaseSuccessBoms);
			List<PartInfo> parts = new ArrayList<PartInfo>();
			List<PartInfo> startpparts=new ArrayList<PartInfo>();
			List<PartInfo> partsChange = new ArrayList<PartInfo>();
			List<BomInfo> boms = new ArrayList<BomInfo>();
			List<BomInfo> bomsChange = new ArrayList<BomInfo>();
			for (PartInfo partInfo : erpData.getParts()) {
				if (partInfo.getVersionBig().equals("1")){
					if(isSpeicalClf(partInfo.getPartNumber())){
						startpparts.add(partInfo);
					}else{
						parts.add(partInfo);
					}
				}else{
					partsChange.add(partInfo);
				}
			}

			for (BomInfo bomInfo : erpData.getBoms()) {
				if (bomInfo.getVersionBig().equals("1")) {
					boms.add(bomInfo);
				} else {
					bomsChange.add(bomInfo);
				}
			}

			if (parts.size() > 0) {
				partResponse = service.sendParts(parts, Constant.COMPANY);
			}
            if(startpparts.size() > 0){
            	startPResponse = service.sendStartPPartsChange(startpparts, Constant.COMPANY);
			}
			if (boms.size() > 0) {
				bomResponse = service.sendBoms(boms, Constant.COMPANY);
			}
			if (partsChange.size() > 0) {
				partChangeResponse = service.sendPartsChange(partsChange, Constant.COMPANY);
			}
			if (bomsChange.size() > 0) {
				bomChangeResponse = service.sendBomsChange(bomsChange, Constant.COMPANY);
			}
			if (erpData.getDrawings().size() > 0) {
				drawingResponse = service.sendDrawing(erpData.getDrawings(), Constant.COMPANY);
			}
			
		} catch (ParseException e) {
			program_exception = true;
			retResponse.setSuccess(false);
			List<Message> list = new ArrayList<Message>();
			Message msg = new Message();
			msg.setSuccess(false);
			msg.setAction(Message.PROGRAM_EXCEPTION);
			msg.setText("程序异常 错误信息：" + e.getMessage());
			list.add(msg);
			retResponse.setMessage(list);

			e.printStackTrace();
		} catch (WTException e) {
			program_exception = true;
			retResponse.setSuccess(false);
			List<Message> list = new ArrayList<Message>();
			Message msg = new Message();
			msg.setSuccess(false);
			msg.setAction(Message.PROGRAM_EXCEPTION);
			msg.setText("程序异常 错误信息：" + e.getMessage());
			list.add(msg);
			retResponse.setMessage(list);

			e.printStackTrace();
		} catch (Exception e) {
			program_exception = true;
			retResponse.setSuccess(false);
			List<Message> list = new ArrayList<Message>();
			Message msg = new Message();
			msg.setSuccess(false);
			msg.setAction(Message.PROGRAM_EXCEPTION);
			msg.setText("程序异常 错误信息：" + e.getMessage());
			list.add(msg);
			retResponse.setMessage(list);

			e.printStackTrace();
		} finally {
			try {
				if (partResponse != null) {
					if (!partResponse.isSuccess())
						retResponse.setSuccess(false);
					    retResponse.getMessage().addAll(partResponse.getMessage());
				} else if (program_exception) {
					for (PartInfo partInfo : erpData.getParts()) {
						if (partInfo.getEcnNumber() == null) {
							Message msg = new Message();
							msg.setSuccess(false);
							msg.setNumber(partInfo.getPartNumber());
							msg.setAction(Message.PART_CREATE);
							msg.setText(Message.PROGRAM_EXCEPTION);
							retResponse.getMessage().add(msg);
						}
					}
				}
				if (startPResponse != null) {
					if (!startPResponse.isSuccess())
						retResponse.setSuccess(false);
					    retResponse.getMessage().addAll(startPResponse.getMessage());
				} else if (program_exception) {
					for (PartInfo partInfo : erpData.getParts()) {//p开头物料的在发布流程的发布
						if (isSpeicalClf(partInfo.getPartNumber())&&partInfo.getVersionBig().equals("1")) {
							Message msg = new Message();
							msg.setSuccess(false);
							msg.setNumber(partInfo.getPartNumber());
							msg.setAction(Message.STARTPPART_CHANGE);
							msg.setText(Message.PROGRAM_EXCEPTION);
							retResponse.getMessage().add(msg);
						}
					}
				}
				if (bomResponse != null) {
					if (!bomResponse.isSuccess())
						retResponse.setSuccess(false);
					retResponse.getMessage().addAll(bomResponse.getMessage());
				} else if (program_exception) {
					for (BomInfo bomInfo : erpData.getBoms()) {
						if (bomInfo.getEcnNumber() == null) {
							Message msg = new Message();
							msg.setSuccess(false);
							msg.setNumber(bomInfo.getParentPartNumber());
							msg.setChildNumber(bomInfo.getChildPartNumber());
							msg.setStituteNumber(bomInfo.getSubstitutePartNumber());
							msg.setAction(Message.BOM_CREATE);
							msg.setText(Message.PROGRAM_EXCEPTION);
							retResponse.getMessage().add(msg);
						}
					}
				}
				if (partChangeResponse != null) {
					if (!partChangeResponse.isSuccess())
						retResponse.setSuccess(false);
					retResponse.getMessage().addAll(partChangeResponse.getMessage());
				} else if (program_exception) {
					for (PartInfo partInfo : erpData.getParts()) {
						if (partInfo.getEcnNumber() != null) {
							Message msg = new Message();
							msg.setSuccess(false);
							msg.setEcnNumber(partInfo.getEcnNumber());
							msg.setNumber(partInfo.getPartNumber());
							msg.setAction(Message.PART_CHANGE);
							msg.setText(Message.PROGRAM_EXCEPTION);
							retResponse.getMessage().add(msg);
						}
					}
				}
				if (bomChangeResponse != null) {
					if (!bomChangeResponse.isSuccess())
						retResponse.setSuccess(false);
					retResponse.getMessage().addAll(bomChangeResponse.getMessage());
				} else if (program_exception) {
					for (BomInfo bomInfo : erpData.getBoms()) {
						if (bomInfo.getEcnNumber() != null) {
							Message msg = new Message();
							msg.setSuccess(false);
							msg.setNumber(bomInfo.getParentPartNumber());
							msg.setChildNumber(bomInfo.getChildPartNumber());
							msg.setStituteNumber(bomInfo.getSubstitutePartNumber());
							msg.setEcnNumber(bomInfo.getEcnNumber());
							msg.setAction(Message.BOM_CHANGE);
							msg.setText(Message.PROGRAM_EXCEPTION);
							retResponse.getMessage().add(msg);
						}
					}
				}
				if (drawingResponse != null) {
					if (!drawingResponse.isSuccess())
						retResponse.setSuccess(false);
					retResponse.getMessage().addAll(drawingResponse.getMessage());
				} else if (program_exception) {
					for (DrawingInfo drawingInfo : erpData.getDrawings()) {
						Message msg = new Message();
						msg.setSuccess(false);
						msg.setNumber(drawingInfo.getPartNumber());
						msg.setDrawingNumber(drawingInfo.getDrawingNumber());
						msg.setDrawingVersion(drawingInfo.getDrawingVersion());
						msg.setAction(Message.DRAWING);
						msg.setText(Message.PROGRAM_EXCEPTION);
						retResponse.getMessage().add(msg);
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		log.debug("end release................");
		return retResponse;
	}

	/**
	 * 发布ECN到ERP
	 * 
	 * @param pbo
	 * @return
	 */
	public static ErpResponse releaseECN(WTObject pbo) {
		ErpResponse retResponse = new ErpResponse();
		retResponse.setSuccess(true);
		retResponse.setMessage(new ArrayList<Message>());

		ErpResponse response = null;
		boolean program_exception = false;

		List<EcInfo> ecns = new ArrayList<EcInfo>();
		try {
			log.debug("begin releaseECN................");

			WTChangeOrder2 eco = (WTChangeOrder2) pbo;
			EcInfo ecn = new EcInfo();
			ecn.setDescription(eco.getDescription());
			ecn.setNumber(eco.getNumber());
			ecn.setName(eco.getName());
			ecn.setValidDate(eco.getNeedDate() == null ? "" : format.format(ECNAttachmentHtml.fixTime(eco.getNeedDate())));
			ecn.setOid(eco.getPersistInfo().getObjectIdentifier().getStringValue());
			ecns.add(ecn);

			response = service.sendECN(ecns, Constant.COMPANY);
			log.debug(response.isSuccess() + ",编号:" + response.getMessage().get(0).getNumber() + ", " + response.getMessage().get(0).getText());
			log.debug("end releaseECN................");
		} catch (Exception e) {
			program_exception = true;
			response = new ErpResponse();
			response.setSuccess(false);
			List<Message> list = new ArrayList<Message>();
			Message msg = new Message();
			msg.setSuccess(false);
			msg.setAction(Message.PROGRAM_EXCEPTION);
			msg.setText("程序异常 错误信息：" + e.getMessage());
			list.add(msg);
			response.setMessage(list);

			e.printStackTrace();
		} finally {
			try {
				if (response != null) {
					if (!response.isSuccess())
						retResponse.setSuccess(false);
					retResponse.getMessage().addAll(response.getMessage());
				} else if (program_exception) {
					for (EcInfo info : ecns) {
						Message msg = new Message();
						msg.setAction(Message.ECN);
						msg.setEcnNumber(info.getNumber());
						msg.setText(Message.PROGRAM_EXCEPTION);
						retResponse.getMessage().add(msg);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return retResponse;
	}

	/**
	 * 文档流程发布gerbera 和pcba图纸到ERP
	 * 
	 * @param pbo
	 * @return
	 * @throws WTException
	 */
	public static ErpResponse releaseGerberAndPcbaDoc(WTDocument doc) {
		ErpResponse retResponse = new ErpResponse();
		retResponse.setSuccess(true);
		retResponse.setMessage(new ArrayList<Message>());

		ErpResponse drawingresponse = null;
		boolean program_exception = false;
		List<DrawingInfo> drawingInfoList = new ArrayList<DrawingInfo>();
		try {
			log.debug("begin release doc ---------:" + doc.getNumber());

			TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(doc);
			String doctype = ti.getTypename();
			boolean isNeedset = doctype.contains(TypeName.doc_type_pcbaDrawing) || doctype.contains(TypeName.doc_type_gerberDoc);
			if (isNeedset) {
				QueryResult relatepartlist = null;
				relatepartlist = PartDocServiceCommand.getAssociatedDescParts(doc);
				log.debug("relatelist size ====" + relatepartlist.size());
				while (relatepartlist.hasMoreElements()) {
					WTPart part = (WTPart) relatepartlist.nextElement();
					if (part.getState().toString().equalsIgnoreCase(PartState.RELEASED)) {
						DrawingInfo drawing = new DrawingInfo();
						drawing.setDrawingNumber(doc.getNumber());
						drawing.setPartNumber(part.getNumber());
						drawing.setDrawingVersion(doc.getVersionIdentifier().getValue());
						drawing.setOid(doc.getPersistInfo().getObjectIdentifier().getStringValue());
						drawingInfoList.add(drawing);
						break;
					}
				}
				if (drawingInfoList.size() > 0) {
					drawingresponse = service.sendDrawing(drawingInfoList, Constant.COMPANY);
				}
				// drawingresponse.getMessage().get(0).getAction();
				log.debug("end release  doc -------:" + doc.getNumber());
			}
		} catch (WTException e) {
			program_exception = true;
			drawingresponse = new ErpResponse();
			drawingresponse.setSuccess(false);
			List<Message> list = new ArrayList<Message>();
			Message msg = new Message();
			msg.setSuccess(false);
			msg.setAction(Message.PROGRAM_EXCEPTION);
			msg.setText("程序异常 错误信息：" + e.getMessage());
			list.add(msg);
			drawingresponse.setMessage(list);

			e.printStackTrace();
		} catch (Exception e) {
			program_exception = true;
			drawingresponse = new ErpResponse();
			drawingresponse.setSuccess(false);
			List<Message> list = new ArrayList<Message>();
			Message msg = new Message();
			msg.setSuccess(false);
			msg.setAction(Message.PROGRAM_EXCEPTION);
			msg.setText("程序异常 错误信息：" + e.getMessage());
			list.add(msg);
			drawingresponse.setMessage(list);

			e.printStackTrace();
		} finally {
			try {
				if (drawingresponse != null) {
					if (!drawingresponse.isSuccess())
						retResponse.setSuccess(false);
					retResponse.getMessage().addAll(drawingresponse.getMessage());
				} else if (program_exception) {
					for (DrawingInfo info : drawingInfoList) {
						Message msg = new Message();
						msg.setDrawingNumber(info.getDrawingNumber());
						msg.setDrawingVersion(info.getDrawingVersion());
						msg.setNumber(info.getPartNumber());
						msg.setAction(Message.DRAWING);
						msg.setText(Message.PROGRAM_EXCEPTION);
						retResponse.getMessage().add(msg);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return retResponse;
	}

	/**
	 * 组装 发布到ERP 的数据
	 * 
	 * @param pbo
	 * @return
	 * @throws MaturityException
	 * @throws WTException
	 * @throws ParseException
	 */
	private static ErpData getErpData(WTObject pbo, String releaseSuccessParts, String releaseSuccessBoms) throws MaturityException, WTException, ParseException, Exception {
		ErpData erpData = new ErpData();
		List<PartInfo> partInfoList = new ArrayList<PartInfo>();
		List<BomInfo> bomInfoList = new ArrayList<BomInfo>();
		List<DrawingInfo> drawingInfoList = new ArrayList<DrawingInfo>();

		PromotionNotice pNotice = (PromotionNotice) pbo;
		String oid = pNotice.getPersistInfo().getObjectIdentifier().getStringValue();
		ArrayList<Long> targetlists = new ArrayList<Long>();
		targetlists = BomWfUtil.settargetList((PromotionNotice) pbo);
		QueryResult targetsResult = MaturityHelper.service.getPromotionTargets(pNotice);
		while (targetsResult.hasMoreElements()) {
			Object object = targetsResult.nextElement();
			if ((object instanceof WTPart)) {
				WTPart part = (WTPart) object;
				log.debug("WTPart:" + part.getNumber() + "............");
				String ecnNumber = null;
				WTCollection collection = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(part);
				if (!collection.isEmpty()) {
					Iterator iterator = collection.iterator();
					while (iterator.hasNext()) {
						ObjectReference objReference = (ObjectReference) iterator.next();
						WTChangeOrder2 eco = (WTChangeOrder2) objReference.getObject();
						log.debug("eco number==" + eco.getNumber());
						ecnNumber = eco.getNumber();
					}
				}
				if (StrUtils.isEmpty(releaseSuccessParts) && StrUtils.isEmpty(releaseSuccessBoms)) {// 第一次调用都是空只
					initPartInfo(partInfoList, part, ecnNumber, targetlists, oid);
				} else if (!Arrays.asList(releaseSuccessParts.split(",")).contains(part.getNumber())) {
					initPartInfo(partInfoList, part, ecnNumber, targetlists, oid);
				}
				if (StrUtils.isEmpty(releaseSuccessParts) && StrUtils.isEmpty(releaseSuccessBoms)) {// 第一次调用都是空只
					initBomInfo(bomInfoList, part, ecnNumber, oid);
				} else if (!Arrays.asList(releaseSuccessBoms.split(",")).contains(part.getNumber())) {
					initBomInfo(bomInfoList, part, ecnNumber, oid);
				}
			} else if ((object instanceof WTDocument)) {
				WTDocument doc = (WTDocument) object;
				TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(doc);
				String type = ti.getTypename();
				if (type.endsWith(TypeName.doc_type_autocadDrawing)) {
					QueryResult relatepartlist = PartDocServiceCommand.getAssociatedDescParts(doc);
					relatepartlist = new LatestConfigSpec().process(relatepartlist);
					log.debug("WTDocument relatelist size ====" + relatepartlist.size());
					while (relatepartlist.hasMoreElements()) {
						WTPart part = (WTPart) relatepartlist.nextElement();
						if (part.getState().toString().equalsIgnoreCase(PartState.RELEASED)) {
							DrawingInfo drawing = new DrawingInfo();
							drawing.setDrawingNumber(doc.getNumber());
							drawing.setPartNumber(part.getNumber());
							drawing.setDrawingVersion(doc.getVersionIdentifier().getValue());
							drawingInfoList.add(drawing);
							break;
						}
					}

				}
			} else if ((object instanceof EPMDocument)) {
				EPMDocument epmdoc = (EPMDocument) object;
				if (epmdoc.getDocType().toString().equals("CADDRAWING")) {
					QueryResult qur = PersistenceHelper.manager.navigate(epmdoc, EPMReferenceLink.REFERENCES_ROLE, EPMReferenceLink.class, true);
					while (qur.hasMoreElements()) {
						QueryResult qr = ConfigHelper.service.filteredIterationsOf((EPMDocumentMaster) qur.nextElement(), new LatestConfigSpec());
						while (qr.hasMoreElements()) {
							EPMDocument epm = (EPMDocument) qr.nextElement();
							Collection relatepartcollCollection = EpmUtil.getRelatedPartsLasted(epm);
							Iterator partIterator = relatepartcollCollection.iterator();
							while (partIterator.hasNext()) {
								WTPart part = (WTPart) partIterator.next();
								if (part.getState().toString().equalsIgnoreCase(PartState.RELEASED)) {
									log.debug("EPMDocument part======" + part.getNumber());
									DrawingInfo drawing = new DrawingInfo();
									drawing.setDrawingNumber(epmdoc.getNumber());
									drawing.setPartNumber(part.getNumber());
									drawing.setDrawingVersion(epmdoc.getVersionIdentifier().getValue());
									drawingInfoList.add(drawing);
									break;
								}
							}
						}
					}
				}
			}
		}
		erpData.setParts(partInfoList);
		erpData.setBoms(bomInfoList);
		erpData.setDrawings(drawingInfoList);

		return erpData;
	}
	/**
	 * 组装 发布到ERP 的数据
	 * 
	 * @param pbo
	 * @return
	 * @throws MaturityException
	 * @throws WTException
	 * @throws ParseException
	 */
	private static ErpData getDesignErpData(WTObject pbo, String releaseSuccessParts) throws MaturityException, WTException, ParseException, Exception {
		ErpData erpData = new ErpData();
		List<PartInfo> partInfoList = new ArrayList<PartInfo>();
		PromotionNotice pNotice = (PromotionNotice) pbo;
		String oid = pNotice.getPersistInfo().getObjectIdentifier().getStringValue();
		ArrayList<Long> targetlists = new ArrayList<Long>();
		targetlists = BomWfUtil.settargetList((PromotionNotice) pbo);
		QueryResult targetsResult = MaturityHelper.service.getPromotionTargets(pNotice);
		while (targetsResult.hasMoreElements()) {
			Object object = targetsResult.nextElement();
			if ((object instanceof WTPart)) {
				WTPart part = (WTPart) object;
				log.debug("WTPart:" + part.getNumber() + "............");
				String ecnNumber = null;
				WTCollection collection = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(part);
				if (!collection.isEmpty()) {
					Iterator iterator = collection.iterator();
					while (iterator.hasNext()) {
						ObjectReference objReference = (ObjectReference) iterator.next();
						WTChangeOrder2 eco = (WTChangeOrder2) objReference.getObject();
						log.debug("eco number==" + eco.getNumber());
						ecnNumber = eco.getNumber();
					}
				}
				if (StrUtils.isEmpty(releaseSuccessParts)) {// 第一次调用都是空值
					initPartInfo(partInfoList, part, ecnNumber, targetlists, oid);
				} else if (!Arrays.asList(releaseSuccessParts.split(",")).contains(part.getNumber())) {
					initPartInfo(partInfoList, part, ecnNumber, targetlists, oid);
				}
			}
		}
		erpData.setParts(partInfoList);
		return erpData;
	}
	/**
	 * 组装part数据
	 * 
	 * @param list
	 * @param part
	 * @param ecnNumber
	 * @throws ParseException
	 * @throws WTException
	 */
	private static void initPartInfo(List<PartInfo> list, WTPart part, String ecnNumber, List<Long> targetlists, String oid) throws ParseException, WTException, Exception {

		PartInfo partInfo = new PartInfo();

		partInfo.setOid(oid);
		partInfo.setVersionBig(part.getVersionIdentifier().getValue());
		partInfo.setVersionSmall(part.getIterationIdentifier().getValue());
		partInfo.setPartName(part.getName());
		partInfo.setPartNumber(part.getNumber());
		partInfo.setCreateDate(part.getCreateTimestamp());
		if (part.getCreatorName() != null && part.getCreatorName().equalsIgnoreCase("dms")) {
			partInfo.setCreator(Constant.dmsConstrastEmpno);
		} else {
			partInfo.setCreator(part.getCreatorName());
		}

		String unit = part.getDefaultUnit().toString().toUpperCase();
		if (!StrUtils.isEmpty(unit) && unit.equals("EA")) {
			unit = "PCS";
		}
		partInfo.setSource(changeSource(part.getSource().toString()));

		if (part.getContainerName().equals(ContainerName.BATTERY_LIBRARY_NAME)) {
			if (unit.equals("PCS")) {
				unit = "EA";
			}
			partInfo.setMaterialGroup(null);
		} else {
			partInfo.setMaterialGroup(part.getNumber().substring(0, part.getNumber().lastIndexOf("-")));
		}
		partInfo.setDefaultUnit(unit);
		partInfo.setIteration(part.getVersionIdentifier().getValue());

		Object englishNameObj = GenericUtil.getObjectAttributeValue(part, "englishName");
		String englishName = englishNameObj == null ? "" : englishNameObj.toString();
		if (englishName.equals(Constant.DEFAULT)) {
			englishName = "";
		}
		Object specificationObj = GenericUtil.getObjectAttributeValue(part, "specification");
		String specification = specificationObj == null ? "" : specificationObj.toString();
		/*if (!specification.equals("")) {
			specification = getSpecificationString(specification);
		}*/
		Object oldPartNumberObj = GenericUtil.getObjectAttributeValue(part, "oldPartNumber");// 旧物料号
		String oldPartNumber = oldPartNumberObj == null ? "" : oldPartNumberObj.toString();
		if (oldPartNumber.equals(Constant.DEFAULT)) {
			oldPartNumber = "";
		}

		Object standardVoltageObj = GenericUtil.getObjectAttributeValue(part, "Nominal_Voltage");// 标称电压(V)
		String standardVoltage = standardVoltageObj == null ? "" : standardVoltageObj.toString();
		Object productEnergyObj = GenericUtil.getObjectAttributeValue(part, "Product_Energy");// 产品能量(kWh)
		String productEnergy = productEnergyObj == null ? "" : productEnergyObj.toString();
		/*
		 * Object modelObj =
		 * GenericUtil.getObjectAttributeValue(part,"Product_Model");//Model号
		 * String model = modelObj==null?"":modelObj.toString();
		 */
		Object cellVolumeObj = GenericUtil.getObjectAttributeValue(part, "Cell_Capacity");// 电芯容量(Ah)
		String cellVolume = cellVolumeObj == null ? "" : cellVolumeObj.toString();
		Object cellModeObj = GenericUtil.getObjectAttributeValue(part, "Cell_Mode");// 电芯类型
		String cellMode = cellModeObj == null ? "" : cellModeObj.toString();
		Object cellConnectionModeObj = GenericUtil.getObjectAttributeValue(part, "Cell_Connection_Mode");// 电芯并串联方式
		String cellConnectionMode = cellConnectionModeObj == null ? "" : cellConnectionModeObj.toString();
		Object moduleQuantityObj = GenericUtil.getObjectAttributeValue(part, "Module_Quantity");// 模组数量(PCS)
		String moduleQuantity = moduleQuantityObj == null ? "" : moduleQuantityObj.toString();
		Object hardwareVersionObj = GenericUtil.getObjectAttributeValue(part, "Hardware_Version");// HW版本
		String hardwareVersion = hardwareVersionObj == null ? "" : hardwareVersionObj.toString();
		Object softwareVersionObj = GenericUtil.getObjectAttributeValue(part, "Software_Version");// SW版本
		String softwareVersion = softwareVersionObj == null ? "" : softwareVersionObj.toString();
		Object parameterVersionObj = GenericUtil.getObjectAttributeValue(part, "Parameter_Version");// PAR版本
		String parameterVersion = parameterVersionObj == null ? "" : parameterVersionObj.toString();
		Object faeStatusObj = GenericUtil.getObjectAttributeValue(part, "CATL_FAEStatus");// PAR版本
		String mpn=(String) GenericUtil.getObjectAttributeValue(part, ConstantLine.var_parentPN);
		if(part.getNumber().endsWith("S")&&!part.getNumber().endsWith("-S")){
			mpn=part.getNumber().substring(0,part.getNumber().length()-1);
		}
		String l=ExpressHelper.getLineL(part)==null ? "" : String.valueOf(ExpressHelper.getLineL(part).doubleValue());
		
		String faeStatus = faeStatusObj == null ? "" : faeStatusObj.toString();
		partInfo.setStandardVoltage(standardVoltage);
		partInfo.setProductEnergy(productEnergy);
		// partInfo.setModel(model);
		partInfo.setCellVolume(cellVolume);
		partInfo.setCellMode(cellMode);
		partInfo.setCellConnectionMode(cellConnectionMode);
		partInfo.setModuleQuantity(moduleQuantity);
		partInfo.setHardwareVersion(hardwareVersion);
		partInfo.setSoftwareVersion(softwareVersion);
		partInfo.setParameterVersion(parameterVersion);
		partInfo.setFaeStatus(faeStatus);
		if(mpn!=null&&!mpn.equals("是")){//衍生PN发长度
			partInfo.setL(l);
			partInfo.setParentPN(mpn);
		}
		QueryResult cadresult = PartDocServiceCommand.getAssociatedCADDocuments(part);
		cadresult = new LatestConfigSpec().process(cadresult);
		while (cadresult.hasMoreElements()) {
			EPMDocument epmdoc = (EPMDocument) cadresult.nextElement();
			Boolean iscaddrawing = epmdoc.getDocType().toString().equals("CADDRAWING");
			if (iscaddrawing) {
				if (targetlists.contains(epmdoc.getPersistInfo().getObjectIdentifier().getId())) {
					partInfo.setDrawing(epmdoc.getNumber());
					partInfo.setDrawingVersion(epmdoc.getVersionIdentifier().getValue());
					break;
				} else {
					if (epmdoc.getState().toString().equals(DocState.RELEASED)) {
						partInfo.setDrawing(epmdoc.getNumber());
						partInfo.setDrawingVersion(epmdoc.getVersionIdentifier().getValue());
						break;
					}
				}
			}

		}
		if (partInfo.getDrawing() == null) {// 没有CAD图纸
			QueryResult docresult = PartDocServiceCommand.getAssociatedDescribeDocuments(part);
			docresult = new LatestConfigSpec().process(docresult);
			while (docresult.hasMoreElements()) {// 取第一个 PCBA、AUTO、GERBER文档
				WTDocument doc = (WTDocument) docresult.nextElement();
				TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(doc);
				String type = ti.getTypename();
				boolean isNeedset = type.contains(TypeName.doc_type_pcbaDrawing) || type.contains(TypeName.doc_type_autocadDrawing) || type.contains(TypeName.doc_type_gerberDoc);
				if (isNeedset) {
					if (targetlists.contains(doc.getPersistInfo().getObjectIdentifier().getId())) {
						log.debug("select doc number:" + doc.getNumber() + ",doc version:" + doc.getVersionIdentifier().getValue());
						partInfo.setDrawing(doc.getNumber());
						partInfo.setDrawingVersion(doc.getVersionIdentifier().getValue());
						break;
					} else {
						if (doc.getState().toString().equals(DocState.RELEASED)) {
							log.debug("select doc number:" + doc.getNumber() + ",doc version:" + doc.getVersionIdentifier().getValue());
							partInfo.setDrawing(doc.getNumber());
							partInfo.setDrawingVersion(doc.getVersionIdentifier().getValue());
							break;
						}
					}
				}
			}
		}
		if (partInfo.getDrawing() == null) {// 没有PCBA、AUTO、GERBER文档
			QueryResult qr = ContentHelper.service.getContentsByRole(part, ContentRoleType.SECONDARY);
			while (qr.hasMoreElements()) {
				ApplicationData fileContent = (ApplicationData) qr.nextElement();
				String strFileName = URLDecoder.decode(fileContent.getFileName(), "utf-8");
				if (strFileName.toUpperCase().equals(part.getNumber() + ".PDF")) {
					log.debug("select doc number:" + strFileName);
					partInfo.setDrawing(part.getNumber());
					partInfo.setDrawingVersion("A");
					break;
				}
			}
		}
		partInfo.setEnglishName(englishName);
		partInfo.setSpecification(specification);
		partInfo.setEcnNumber(ecnNumber);
		partInfo.setOldPartNumber(oldPartNumber);

		list.add(partInfo);

	}

	/**
	 * 组装bom数据
	 * 
	 * @param bomInfoList
	 * @param part
	 * @param ecnNumber
	 * @throws WTException
	 */
	private static void initBomInfo(List<BomInfo> bomInfoList, WTPart part, String ecnNumber, String oid) throws WTException, Exception {
		BomInfo bomInfo;
		WTPartMaster childmaster;
		String childnumberString;
		WTPartConfigSpec configSpec = (WTPartConfigSpec) ConfigHelper.service.getConfigSpecFor(part);
		QueryResult qr = WTPartHelper.service.getUsesWTParts(part, configSpec);
		List<Integer> magnifications = new ArrayList<Integer>();
		while (qr.hasMoreElements()) {

			Persistable[] aSubNodePair = (Persistable[]) qr.nextElement();
			WTPartUsageLink usageLink = (WTPartUsageLink) aSubNodePair[0];

			// List<String> substituteList = getSubstitutePart(usageLink);
			childmaster = usageLink.getUses();
			Object magnificationObj = IBAUtil.getIBAValue(usageLink, "CATL_MAGNIFICATION");//放大倍数
			int magnification = 1;//默认 最小公倍数1
			if(magnificationObj != null){
				magnification = ((Long)magnificationObj).intValue();
				magnifications.add(magnification);
			}
			
			childnumberString = childmaster.getNumber();

			WTCollection collection = WTPartHelper.service.getSubstituteLinks(usageLink);
			if (!collection.isEmpty()) {
				Iterator itr = collection.iterator();
				while (itr.hasNext()) {
					ObjectReference objReference = (ObjectReference) itr.next();
					WTPartSubstituteLink subLink = (WTPartSubstituteLink) objReference.getObject();
					WTPartMaster subpartMaster = (WTPartMaster) subLink.getSubstitutes();

					Object subMagnificationObj = IBAUtil.getIBAValue(subLink, "CATL_MAGNIFICATION");//放大倍数
					int subMagnification = 1;//默认 最小公倍数1
					if(subMagnificationObj != null){
						subMagnification = ((Long)subMagnificationObj).intValue();
						magnifications.add(subMagnification);
					}
					
					bomInfo = new BomInfo();
					bomInfo.setOid(oid);
					bomInfo.setVersionBig(part.getVersionIdentifier().getValue());
					bomInfo.setVersionSmall(part.getIterationIdentifier().getValue());
					bomInfo.setChildPartNumber(childnumberString);
					bomInfo.setParentPartNumber(part.getNumber());
					bomInfo.setQuantity(usageLink.getQuantity().getAmount());
					bomInfo.setMagnification(magnification);
					bomInfo.setSubstitutePartNumber(subpartMaster.getNumber());
					bomInfo.setSubMagnification(subMagnification);

					if (subLink.getQuantity() != null) {
						Double amount = subLink.getQuantity().getAmount();
						if (amount != null && amount != 0) {
							bomInfo.setSubQuantity(amount);
						} else {
							bomInfo.setSubQuantity(usageLink.getQuantity().getAmount());
						}
					} else {
						bomInfo.setSubQuantity(usageLink.getQuantity().getAmount());
					}
					bomInfo.setEcnNumber(ecnNumber);
					bomInfoList.add(bomInfo);
				}
			} else {
				bomInfo = new BomInfo();
				bomInfo.setOid(oid);
				bomInfo.setVersionBig(part.getVersionIdentifier().getValue());
				bomInfo.setVersionSmall(part.getIterationIdentifier().getValue());
				bomInfo.setChildPartNumber(childnumberString);
				bomInfo.setParentPartNumber(part.getNumber());
				bomInfo.setQuantity(usageLink.getQuantity().getAmount());
				bomInfo.setMagnification(magnification);
				bomInfo.setSubstitutePartNumber("");
				bomInfo.setEcnNumber(ecnNumber);
				bomInfoList.add(bomInfo);
			}
		}
		int magnification = minMultiple(magnifications);//最小公倍数
		for(BomInfo temp : bomInfoList){
			temp.setQuantity(magnification/temp.getMagnification()*temp.getQuantity());
			if(temp.getSubQuantity() != 0)
				temp.setSubQuantity(magnification/temp.getSubMagnification()*temp.getSubQuantity());
			temp.setMagnification(magnification);
		}
	}

	public static String changeSource(String source) {
		String flag = null;
		if (source == null)
			return "";
		if (source.equals("make")) {// 自制
			flag = "E";
		} else if (source.equals("buy")) {// 外购
			flag = "F";
		} else if (source.equals("makeBuy")) {// 外协
			flag = "W";
		} else if (source.equals("customer")) {// 客供
			flag = "C";
		} else if (source.equalsIgnoreCase("virtual")) {// 虚拟
			flag = "V";
		}
		return flag;
	}

	public static String sendDrawingToERP(WTObject obj) {
		log.debug("...........。。。。.start sendDrawingToERP............");
		StringBuffer sb = new StringBuffer();

		PromotionNotice pn = (PromotionNotice) obj;
		Map<String, DrawingSendERP> map = new HashMap<String, DrawingSendERP>();//发送ERP的图纸
		WTList logList = new WTArrayList();//存放DrawingSendERPLog类型日志
		String oid = pn.getPersistInfo().getObjectIdentifier().getStringValue();
		QueryResult targetsResult;
		try {
			targetsResult = MaturityHelper.service.getPromotionTargets(pn);
			while (targetsResult.hasMoreElements()) {
				Object object = targetsResult.nextElement();
				if ((object instanceof ContentHolder)) {
					ContentHolder holder = (ContentHolder)object;
					copyDrawing(holder,map,logList,oid);
				}
			}
			Thread.sleep(10000);//图纸保存服务器 5秒后在发送ERP
			PersistenceHelper.manager.save(logList);
			
			String ret = service.sendFiles(map.values(), Constant.COMPANY);
			sb.append(ret);
			
			log.debug("...........。。。。.end sendDrawingToERP............");
		} catch (MaturityException e) {
			e.printStackTrace();
			sb.append("程序异常："+e.getMessage());
		} catch (WTException e) {
			e.printStackTrace();
			sb.append("程序异常："+e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			sb.append("程序异常："+e.getMessage());
		}

		return sb.toString();
	}

	/**
	 * 拷贝图纸到目录
	 * 
	 * @param holder
	 *            受影响对象
	 * @param map
	 *            发送给ERP的图纸数据
	 * @param targetList
	 * 				受影响对象oid
	 * @param logList
	 * 				日志容器
	 * @param oid
	 * 			pn oid
	 * @throws Exception
	 */
	public static void copyDrawing(ContentHolder holder, Map<String, DrawingSendERP> map,WTList logList,String oid) throws Exception {
		DrawingSendERPLog dlog = new DrawingSendERPLog();
		dlog.setOid(oid);
		if (holder instanceof WTPart) {
			WTPart part = (WTPart) holder;
			dlog.setObjectInPromotionNumber(part.getNumber());
			dlog.setObjectInPromotionType(TypeIdentifierUtility.getTypeIdentifier(part).getTypename());
			dlog.setObjectInPromotionVersion(part.getVersionIdentifier().getValue());
			dlog.setObjectInPromotionIteration(part.getIterationIdentifier().getValue());
			
			if(!map.containsKey(part.getNumber())){
				QueryResult cadresult = PartDocServiceCommand.getAssociatedCADDocuments(part);
				cadresult = new LatestConfigSpec().process(cadresult);
				while (cadresult.hasMoreElements()) {
					EPMDocument epmdoc = (EPMDocument) cadresult.nextElement();
					Boolean iscaddrawing = epmdoc.getDocType().toString().equals("CADDRAWING");
					if (iscaddrawing) {
						if (epmdoc.getState().toString().equals(CadState.RELEASED)) {
							dlog.setRelationObjectNumber(epmdoc.getNumber());
							dlog.setRelationObjectType(epmdoc.getDocType().toString());
							log.info("copyDrawing==part-empdocument-pdf");
							handleAttachmentBy2D(epmdoc,dlog,map,part);
							break;
						}
					}
				}
			}
			if(!map.containsKey(part.getNumber())){
				QueryResult docresult = PartDocServiceCommand.getAssociatedDescribeDocuments(part);
				docresult = new LatestConfigSpec().process(docresult);
				while (docresult.hasMoreElements()) {// 取第一个 PCBA、AUTO、GERBER文档
					WTDocument doc = (WTDocument) docresult.nextElement();
					TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(doc);
					String type = ti.getTypename();
					boolean autocad = type.contains(TypeName.doc_type_autocadDrawing);
					boolean pcbaANDgerber = type.contains(TypeName.doc_type_pcbaDrawing) || type.contains(TypeName.doc_type_gerberDoc);
					if (autocad) {
						if (doc.getState().toString().equals(DocState.RELEASED)) {
							dlog.setRelationObjectNumber(doc.getNumber());
							dlog.setRelationObjectType(type);
							QueryResult qr = ContentHelper.service.getContentsByRole(doc, ContentRoleType.SECONDARY);
							log.info("copyDrawing==part-autocad-pdf");
							handleAttachment(map, part, qr,dlog,doc.getNumber());
							break;
						}
					}else if(pcbaANDgerber){
						
						/*if (doc.getState().toString().equals(DocState.RELEASED)) {
							dlog.setRelationObjectNumber(doc.getNumber());
							dlog.setRelationObjectType(type);
							QueryResult qr = ContentHelper.service.getContentsByRole(doc, ContentRoleType.PRIMARY);
							log.info("copyDrawing==part-pcbaANDgerber-pdf");
							handleAttachmentPcbaANDgerber(map, part, qr,dlog);
							break;
						}*/
						
						QueryResult pcbdocs = PartDocServiceCommand.getAssociatedDescribeDocuments(part);
						pcbdocs = new LatestConfigSpec().process(pcbdocs);
						dlog.setRelationObjectNumber(doc.getNumber());
						dlog.setRelationObjectType(type);
						List<WTDocument> doclist = new ArrayList<WTDocument>();
						while(pcbdocs.hasMoreElements()){
							WTDocument pcbdoc = (WTDocument) pcbdocs.nextElement();
							TypeIdentifier pcbti = TypeIdentifierUtility.getTypeIdentifier(pcbdoc);
							String pcbtype = pcbti.getTypename();
							boolean isPcbaOrGerber = pcbtype.contains(TypeName.doc_type_pcbaDrawing) || pcbtype.contains(TypeName.doc_type_gerberDoc);
							if(isPcbaOrGerber){
								doclist.add(pcbdoc);
							}
						}
						copyAttachment(map, part, dlog, doclist, part.getNumber());
						
					}
				}
			}
			if(!map.containsKey(part.getNumber())){
				dlog.setRelationObjectNumber(part.getNumber());
				dlog.setRelationObjectType(TypeIdentifierUtility.getTypeIdentifier(part).getTypename());
				QueryResult qr = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);
				log.info("copyDrawing==part-part-pdf");
				handleAttachment(map, part, qr,dlog,part.getNumber());
			}
			if(!map.containsKey(part.getNumber())){//没有图纸
				log.info("copyDrawing==part "+part.getNumber()+" no pdf");
				DrawingSendERP drt = new DrawingSendERP();
				drt.setPartNumber(part.getNumber());
				drt.setAddress("");
				drt.setFileName("");
				map.put(part.getNumber(), drt);
			}
		}else if (holder instanceof EPMDocument) {
			EPMDocument epm = (EPMDocument) holder;
			
			dlog.setObjectInPromotionNumber(epm.getNumber());
			dlog.setObjectInPromotionType(TypeIdentifierUtility.getTypeIdentifier(epm).getTypename());
			dlog.setObjectInPromotionVersion(epm.getVersionIdentifier().getValue());
			dlog.setObjectInPromotionIteration(epm.getIterationIdentifier().getValue());
			
			if (epm.getDocType().toString().equals("CADDRAWING")) {// 二维
				QueryResult qur = PersistenceHelper.manager.navigate(epm, EPMReferenceLink.REFERENCES_ROLE, EPMReferenceLink.class, true);
				while (qur.hasMoreElements()) {
					QueryResult qr = ConfigHelper.service.filteredIterationsOf((EPMDocumentMaster) qur.nextElement(), new LatestConfigSpec());
					while (qr.hasMoreElements()) {
						EPMDocument epm3d = (EPMDocument) qr.nextElement();
						Collection relatepartcollCollection = EpmUtil.getRelatedPartsLasted(epm3d);
						Iterator partIterator = relatepartcollCollection.iterator();
						while (partIterator.hasNext()) {// 获取关联的物料
							WTPart part = (WTPart) partIterator.next();
							if(!map.containsKey(part.getNumber())){
								log.info("copyDrawing==epmdocument-epmdocument-pdf");
								if (part.getState().toString().equalsIgnoreCase(PartState.RELEASED)){
									dlog.setRelationObjectNumber(part.getNumber());
									dlog.setRelationObjectType(TypeIdentifierUtility.getTypeIdentifier(part).getTypename());
									handleAttachmentBy2D(epm,dlog,map,part);
									break;
								}
							}
						}
					}
				}
			}
		} else if (holder instanceof WTDocument) {
			WTDocument doc = (WTDocument) holder;
			dlog.setObjectInPromotionNumber(doc.getNumber());
			dlog.setObjectInPromotionType(TypeIdentifierUtility.getTypeIdentifier(doc).getTypename());
			dlog.setObjectInPromotionVersion(doc.getVersionIdentifier().getValue());
			dlog.setObjectInPromotionIteration(doc.getIterationIdentifier().getValue());
			
			if (TypeUtil.isSpecifiedType(doc, CatlConstant.AUTOCAD_DOC_TYPE)) {
				QueryResult relatepartlist = PartDocServiceCommand.getAssociatedDescParts(doc);
				relatepartlist = new LatestConfigSpec().process(relatepartlist);
				log.info("WTDocument relatelist size ====" + relatepartlist.size());
				while (relatepartlist.hasMoreElements()) {
					WTPart part = (WTPart) relatepartlist.nextElement();
					if(!map.containsKey(part.getNumber())){
						if (part.getState().toString().equalsIgnoreCase(PartState.RELEASED)){
							dlog.setRelationObjectNumber(part.getNumber());
							dlog.setRelationObjectType(TypeIdentifierUtility.getTypeIdentifier(part).getTypename());
							QueryResult qr = ContentHelper.service.getContentsByRole(doc, ContentRoleType.SECONDARY);
							log.info("copyDrawing==part-part-pdf");
							handleAttachment(map, part, qr,dlog,doc.getNumber());
						}
					}
				}
			}
		}
		logList.add(dlog);
	}
	
	/**
	 * 复制 图纸附件
	 * @param epm
	 * @param dlog
	 * @param map
	 * @param part
	 * @throws Exception
	 */
	private static void handleAttachmentBy2D(EPMDocument epm,DrawingSendERPLog dlog,Map<String,DrawingSendERP> map,WTPart part) throws Exception{
		QueryResult qr = ContentHelper.service.getContentsByRole(epm, ContentRoleType.SECONDARY);
		while (qr.hasMoreElements()) {
			ApplicationData fileContent = (ApplicationData) qr.nextElement();
			String strFileName = URLDecoder.decode(fileContent.getFileName(), "utf-8");
			if (strFileName.equalsIgnoreCase(epm.getNumber().substring(0,12)+RELEASE_Suffix + PDF_Suffix)) {// 已盖章图纸
				String filename = ROOT_DIRECTORY + strFileName;// 存放路径+文件名
				log.info("handleAttachment==fileName:" + filename);
				InputStream is = ContentServerHelper.service.findContentStream(fileContent);// 图纸流
				FileOutputStream fos = new FileOutputStream(new File(filename));
				
				Map<String, Set<InputStream>> all = new HashMap<String, Set<InputStream>>();
				StringBuilder errorMsg = new StringBuilder();

				Set<InputStream> set = new HashSet<InputStream>();
				set.add(is);
				if (!set.isEmpty()) {
					all.put(BatchDownloadPDFUtil.getSubNumber(epm.getNumber()), set);
				}
				EPMDocument epm3D = BatchDownloadPDFUtil.getReferenceEPMByDrawing(epm);
				if (epm3D != null) {
					QueryResult qr3d = EPMStructureHelper.service.navigateUsesToIteration(epm3D, null, true, new LatestConfigSpec());
					while (qr3d.hasMoreElements()) {
						Object obj = qr3d.nextElement();
						if (obj instanceof EPMDocument) {
							BatchDownloadPDFUtil.loadUseMiddlewareEPM((EPMDocument) obj, all, errorMsg);
						}
					}
				}
				ArrayList<InputStream> pdfs = new ArrayList<InputStream>();
				ArrayList<String> keys = new ArrayList<String>();
				keys.addAll(all.keySet());
				Collections.sort(keys);
				Iterator<String> iterators = keys.iterator();
				while (iterators.hasNext()) {
					pdfs.addAll(all.get(iterators.next()));
				}
				if (!pdfs.isEmpty()) {
					mergePdfFiles(pdfs, fos);
				}
				DrawingSendERP dse = new DrawingSendERP();
				dse.setPartNumber(part.getNumber());
				dse.setAddress(ROOT_DIRECTORY_MAPPING);
				dse.setFileName(strFileName);
				
				dlog.setPartNumber(part.getNumber());
				dlog.setRootPath(ROOT_DIRECTORY_MAPPING);
				dlog.setFileName(strFileName);
				dlog.setPartVersion(part.getVersionIdentifier().getValue());
				dlog.setPartIteration(part.getIterationIdentifier().getValue());

				map.put(part.getNumber(), dse);// 二维图纸取的PDF  优先级最高
				break;
			}
		}
	}
	
	/**
	 * 复制 图纸附件
	 * @param map
	 * @param part
	 * @param qr
	 * @throws UnsupportedEncodingException
	 * @throws WTException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws WTPropertyVetoException 
	 */
	private static void handleAttachment(Map<String, DrawingSendERP> map, WTPart part, QueryResult qr,DrawingSendERPLog dlog,String number) throws UnsupportedEncodingException, WTException, FileNotFoundException, IOException, WTPropertyVetoException {
		while (qr.hasMoreElements()) {
			ApplicationData fileContent = (ApplicationData) qr.nextElement();
			String strFileName = URLDecoder.decode(fileContent.getFileName(), "utf-8");
			if (strFileName.equalsIgnoreCase(number+RELEASE_Suffix + PDF_Suffix)) {// 已盖章图纸
				copyAttachment(map, part, dlog, fileContent, strFileName);
				break;
			}
		}
	}

	/**
	 * 复制 图纸附件
	 * @param map
	 * @param part
	 * @param qr
	 * @throws UnsupportedEncodingException
	 * @throws WTException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws WTPropertyVetoException 
	 */
	private static void handleAttachmentPcbaANDgerber(Map<String, DrawingSendERP> map, WTPart part, QueryResult qr,DrawingSendERPLog dlog) throws UnsupportedEncodingException, WTException, FileNotFoundException, IOException, WTPropertyVetoException {
		while (qr.hasMoreElements()) {
			ApplicationData fileContent = (ApplicationData) qr.nextElement();
			String strFileName = URLDecoder.decode(fileContent.getFileName(), "utf-8");
			copyAttachment(map, part, dlog, fileContent, strFileName);
			break;
		}
	}
	
	/**
	 * 拷贝附件
	 * @param map
	 * @param part
	 * @param dlog
	 * @param fileContent
	 * @param strFileName
	 * @throws WTException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws WTPropertyVetoException
	 */
	private static void copyAttachment(Map<String, DrawingSendERP> map, WTPart part, DrawingSendERPLog dlog, ApplicationData fileContent, String strFileName) throws WTException, FileNotFoundException, IOException, WTPropertyVetoException {
		String filename = ROOT_DIRECTORY + strFileName;// 存放路径+文件名
		log.info("handleAttachment==fileName:" + filename);
		InputStream is = ContentServerHelper.service.findContentStream(fileContent);// 图纸流
		FileOutputStream fos = new FileOutputStream(new File(filename));
		byte[] buffer = new byte[1024];
		int byteread = 0; // 读取的字节数
		while ((byteread = is.read(buffer)) > 0) {
			fos.write(buffer, 0, byteread);
		}
		fos.flush();
		is.close();
		fos.close();

		DrawingSendERP drt = new DrawingSendERP();
		drt.setPartNumber(part.getNumber());
		drt.setAddress(ROOT_DIRECTORY_MAPPING);
		drt.setFileName(strFileName);
		
		dlog.setPartNumber(part.getNumber());
		dlog.setRootPath(ROOT_DIRECTORY_MAPPING);
		dlog.setFileName(strFileName);
		dlog.setPartVersion(part.getVersionIdentifier().getValue());
		dlog.setPartIteration(part.getIterationIdentifier().getValue());

		map.put(part.getNumber(), drt);
	}
	
	/**
	 * 拷贝附件
	 * @param map
	 * @param part
	 * @param dlog
	 * @param fileContent
	 * @param strFileName
	 * @throws Exception 
	 */
	private static void copyAttachment(Map<String, DrawingSendERP> map, WTPart part, DrawingSendERPLog dlog, List<WTDocument> doclist, String strFileName) throws Exception {
		String filename = ROOT_DIRECTORY + strFileName;// 存放路径+文件名
		log.info("handleAttachment==fileName:" + filename);
		/*InputStream is = ContentServerHelper.service.findContentStream(fileContent);// 图纸流
		FileOutputStream fos = new FileOutputStream(new File(filename));
		byte[] buffer = new byte[1024];
		int byteread = 0; // 读取的字节数
		while ((byteread = is.read(buffer)) > 0) {
			fos.write(buffer, 0, byteread);
		}
		fos.flush();
		is.close();
		fos.close();*/
		ZipDoc.zipDocs(doclist, filename);
		strFileName = strFileName+".zip";

		DrawingSendERP drt = new DrawingSendERP();
		drt.setPartNumber(part.getNumber());
		drt.setAddress(ROOT_DIRECTORY_MAPPING);
		drt.setFileName(strFileName);
		
		dlog.setPartNumber(part.getNumber());
		dlog.setRootPath(ROOT_DIRECTORY_MAPPING);
		dlog.setFileName(strFileName);
		dlog.setPartVersion(part.getVersionIdentifier().getValue());
		dlog.setPartIteration(part.getIterationIdentifier().getValue());

		map.put(part.getNumber(), drt);
	}
	
	/**
	 * 合并 中间件图纸
	 * @param pdfs
	 * @param os
	 * @throws Exception
	 */
	private static void mergePdfFiles(ArrayList<InputStream> pdfs, OutputStream os) throws Exception {
		Document document = null;
		try {
			String pass = PDFSignetUtil.getEncryptionPW();
			document = new Document();
			PdfCopy copy = new PdfCopy(document, os);
			copy.setEncryption(null, pass.getBytes(), PdfWriter.ALLOW_PRINTING | PdfWriter.ALLOW_SCREENREADERS, PdfWriter.ENCRYPTION_AES_128);
			document.open();

			for (InputStream inputStream : pdfs) {
				PdfReader reader = new PdfReader(inputStream, pass.getBytes());
				int n = reader.getNumberOfPages();
				for (int j = 1; j <= n; j++) {
					document.newPage();
					PdfImportedPage page = copy.getImportedPage(reader, j);
					copy.addPage(page);
				}
			}
			os.flush();
			document.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (document != null && document.isOpen() && document.getPageNumber() > 0) {
				document.close();
			}
			os.close();
		}
	}
	/*
	 * private static ErpData getChangeErpData(WTObject pbo) throws
	 * ParseException, ChangeException2, WTException{ ErpData erpData = new
	 * ErpData(); List<PartInfo> partInfoList = new ArrayList<PartInfo>();
	 * List<BomInfo> bomInfoList = new ArrayList<BomInfo>(); EcInfo ecn = new
	 * EcInfo();
	 * 
	 * WTChangeOrder2 eco= (WTChangeOrder2)pbo;
	 * ecn.setDescription(eco.getDescription()); ecn.setNumber(eco.getNumber());
	 * ecn.setName(eco.getName());
	 * ecn.setValidDate(eco.getNeedDate()==null?"":format
	 * .format(eco.getNeedDate()));
	 * 
	 * QueryResult ecnqr = ChangeHelper2.service.getChangeActivities(eco);
	 * QueryResult targetrResult; WTChangeActivity2 eca; Object object; while
	 * (ecnqr.hasMoreElements()) { eca = (WTChangeActivity2)
	 * ecnqr.nextElement();
	 * targetrResult=ChangeHelper2.service.getChangeablesBefore(eca);
	 * log.debug("eca number....."+eca.getNumber()); while
	 * (targetrResult.hasMoreElements()) { object = (Object)
	 * targetrResult.nextElement(); if(object instanceof WTPart){ WTPart
	 * part=(WTPart)object; log.debug("WTPart:"+part.getNumber());
	 * initPartInfo(partInfoList,part,eco.getNumber()); initBomInfo(bomInfoList,
	 * part, eco.getNumber()); } } } erpData.setParts(partInfoList);
	 * erpData.setBoms(bomInfoList); erpData.setEcn(ecn);
	 * 
	 * return erpData; }
	 */
	
	
	//================新增接口处理_DCN
	/**
	 * call com.catl.integration.ReleaseUtil.releaseDCN(WTObject pbo)
	 * @param pbo
	 */
	public static ErpResponse releaseDCN(WTObject pbo){
		return releaseECN(pbo);
	}
	
	/**
	 * 采购类型更改
	 * 
	 * @param pbo
	 * @return
	 */
	public static ErpResponseSimple sendSourceChange(WTObject pbo, ObjectReference self,String successPart) {
		if(successPart == null)
			successPart="";
		ErpResponseSimple retResponse  = new ErpResponseSimple();
		retResponse.setSuccess(false);
		retResponse.setSuccessPart(successPart);
		log.debug("begin sendSourceChange................");
		log.debug("successPart................"+successPart);
		boolean enforced = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			List<SourceChangeXmlObjectBean> list = SourceChangeXmlObjectUtil.getXmlObjectUtil(pbo);
			DTBESKZCreateResponse response = service.sendSourceChange(Constant.COMPANY, list, successPart);
			
			if(response.getEACKNOW().getResult().equals("S")){//成功
				retResponse.setSuccess(true);
			}else{
				retResponse.setSuccess(false);
			}
			String errorMsg="";
			for(DTBESKZCreateResponse.TRETURN ret : response.getTRETURN()){
				for (SourceChangeXmlObjectBean sourceChange : list) {
					String partNumber = sourceChange.getPartNumber();
					if(partNumber.equals(ret.getMATRN())){//ERP返回的partNumber =  PLM的partNumber   则取PLM端的记录 进行操作
						if(ret.getSTATUS().equals("S")){
							ReferenceFactory rf = new ReferenceFactory();
							Persistable obj = rf.getReference("VR:wt.part.WTPart:"+sourceChange.getPartBranchId()).getObject();
							WTPart part = (WTPart)obj;
							part.setSource(Source.toSource(sourceChange.getSourceAfter()));
							PersistenceServerHelper.manager.update(part);//保存 采购类型
							
							String oldFAE = (String)IBAUtil.getIBAValue(part.getMaster(), PartConstant.IBA_CATL_FAEStatus);
							String newFAE = RefreshFAEStatusUtil.needRefresh(part);
							if(StringUtils.isNotBlank(newFAE)){
								Persistable p = IBAUtil.setIBAVaue(part.getMaster(), PartConstant.IBA_CATL_FAEStatus, newFAE);
								PersistenceHelper.manager.save(p);// 保存 FAE状态
							}else{
								newFAE =oldFAE;
							}
							successPart = partNumber+","+successPart;
							
							PartSourceChangeLogHelper.addPartSourceChangeLog(((PromotionNotice)pbo).getCreator(), part,
									Source.toSource(sourceChange.getSourceBefore()).getDisplay(), Source.toSource(sourceChange.getSourceAfter()).getDisplay(), oldFAE, newFAE,sourceChange.getCause());//保存 采购类型更改历史记录
						}else{
							errorMsg = partNumber + ":"+ret.getMESSAGE()+"\n"+errorMsg;
						}
					}
				}
			}
			retResponse.setMessage(errorMsg);
			retResponse.setSuccessPart(successPart);
		} catch (Exception e) {
			e.printStackTrace();
			retResponse.setSuccess(false);
			retResponse.setMessage(e.toString());
		}finally {
			SessionServerHelper.manager.setAccessEnforced(enforced);
		}
		return retResponse;
	}
	/**
	 * 最小公倍数
	 * @param a
	 * @param b
	 * @return
	 */
	public static int minMultiple(List<Integer> list) {
		if(list == null || list.size() == 0)
			return 1;
		if(list.size() == 1)
			return list.get(0);
		if(list.size() == 2)
			return minMultiple(list.get(0),list.get(1));
		
		int retVal = minMultiple(list.get(0),list.get(1));
		for(int i=2; i<list.size(); i++){
			retVal = minMultiple(retVal,list.get(i));
		}
		return retVal;
	}
	/**
	 * 最小公倍数
	 * @param a
	 * @param b
	 * @return
	 */
	public static int minMultiple(int a, int b) {
		int r = a, s = a, t = b;
		if (a < b) {
			r = a;
			a = b;
			b = r;
		}
		while (r != 0) {
			r = a % b;
			a = b;
			b = r;
		}
		return s * t / a;
	}
	
	public static boolean isSpeicalClf(String partnumber){
		String config_sendSAP_speical_clfgroup=PropertiesUtil.getValueByKey("config_sendSAP_speical_clfgroup");
		String[] groups=config_sendSAP_speical_clfgroup.split(",");
		for (int i = 0; i < groups.length; i++) {
			String group=groups[i];
			if(partnumber.startsWith(group)){
				return true;
			}
		}
		return false;
	}
	
	public static String getSpecificationString(String str) {
		String str1[] = str.split(":");
		String unittemp = "";
		String unit = "";
		String result = "";
		int length = str1.length;
		boolean flag = true;
		for (int i = 0; i < length-1; i++) {
			System.out.println(i+"\t"+str1[i]);
			if(str1[i].lastIndexOf("_")!=-1){
				//if(i != length -1){
				String attrname = str1[i].substring(str1[i].lastIndexOf("_")+1);
				String value = str1[i].substring(0, str1[i].lastIndexOf("_"));
				//System.out.println("Value\t"+value);
				//System.out.println("AttrName\t"+attrname);
				if (!attrname.isEmpty()) {
					unit = unittemp;
					if (attrname.lastIndexOf("(") != -1) {
						unittemp = attrname.substring(attrname.lastIndexOf("(") + 1,
								attrname.length() - 1);
						System.out.println("str3\t"+unittemp);
					} else if (attrname.lastIndexOf("（") != -1) {
						unittemp = attrname.substring(attrname.lastIndexOf("（") + 1,
								attrname.length() - 1);
						System.out.println("str33\t"+unittemp);
					} else {
						unittemp = "";
					}
					
				}
				
				if(StringUtils.isNotBlank(value)){
					if(flag){
						result = result + value+unit;
						flag = false;
					}else{
						result = result +"_"+ value+unit;
					}
					unit = "";
				}
				//}
			}else{
				if(i == 0){
					String attrname = str1[i];
					if (!attrname.isEmpty()) {
						unit = unittemp;
						if (attrname.lastIndexOf("(") != -1) {
							unittemp = attrname.substring(attrname.lastIndexOf("(") + 1,
									attrname.length() - 1);
							System.out.println("str3\t"+unittemp);
						} else if (attrname.lastIndexOf("（") != -1) {
							unittemp = attrname.substring(attrname.lastIndexOf("（") + 1,
									attrname.length() - 1);
							System.out.println("str33\t"+unittemp);
						} else {
							unittemp = "";
						}
						
					}
				}
			}
			
		}
		if(!str.endsWith(":")){
			if(StringUtils.isNotBlank(str1[length-1])){
				result = result +"_"+ str1[length-1]+unittemp;
			}
		}else{
			if(str1[length-1].lastIndexOf("_")==-1){
				result="";
			}else{
				if(str1[length-1].lastIndexOf("_")!=-1){
					String value = str1[length-1].substring(0, str1[length-1].lastIndexOf("_"));
					if(StringUtils.isNotBlank(value)){
						result = result +"_"+ value+unittemp;
					}
				}
			}
		}
		System.out.println("result\t"+result);			
		
		return result;
	}
	
	/**
	 * 发送受影响对象，开工单时提醒数据在变更中
	 * @param pbo
	 * @return
	 * @throws Exception
	 */
	public static String releaseStartList(WTObject pbo) throws Exception{
		
		String result = service.secdOutECN(pbo);
		
		return result;
	}
}
