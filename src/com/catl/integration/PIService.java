package com.catl.integration;

import java.net.Authenticator;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.xml.ws.BindingProvider;

import org.apache.log4j.Logger;

import wt.change2.ChangeException2;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTList;
import wt.part.WTPart;
import wt.services.applicationcontext.implementation.ServiceProperties;
import wt.util.WTException;

import com.catl.common.util.GenericUtil;
import com.catl.common.util.PropertiesUtil;
import com.catl.integration.log.TransactionLog;
import com.catl.integration.log.TransactionLogHelper;
import com.catl.integration.log.WebServiceTransactionInfo;
import com.catl.integration.log.WebServiceTransactionLog;
import com.catl.integration.pi.bom.change.DTBOMCHANGECreateRequest;
import com.catl.integration.pi.bom.change.DTBOMCHANGECreateResponse;
import com.catl.integration.pi.bom.change.SIBOMCHANGECreateOut;
import com.catl.integration.pi.bom.change.SIBOMCHANGECreateOutService;
import com.catl.integration.pi.bom.create.DTBOMCreateCreateResponse;
import com.catl.integration.pi.bom.create.DTBOMCreateCreateResponse.TBOMOUT;
import com.catl.integration.pi.bom.create.DTBomCreateCreateRequest;
import com.catl.integration.pi.bom.create.DTBomCreateCreateRequest.TBOMIN;
import com.catl.integration.pi.bom.create.SIBOMCreateCreateOut;
import com.catl.integration.pi.bom.create.SIBOMCreateCreateOutService;
import com.catl.integration.pi.drawing.DTZEINRCreateRequest;
import com.catl.integration.pi.drawing.DTZEINRCreateRequest.TZEINR;
import com.catl.integration.pi.drawing.DTZEINRCreateResponse;
import com.catl.integration.pi.drawing.SIZEINRCreateOut;
import com.catl.integration.pi.drawing.SIZEINRCreateOutService;
import com.catl.integration.pi.ecn.DTEcnCreateRequest;
import com.catl.integration.pi.ecn.DTEcnCreateResponse;
import com.catl.integration.pi.ecn.DTEcnCreateResponse.TECNR;
import com.catl.integration.pi.ecn.SIEcnCreateOut;
import com.catl.integration.pi.ecn.SIEcnCreateOutService;
import com.catl.integration.pi.ecn.DTEcnCreateRequest.TECN;
import com.catl.integration.pi.ecn.erp.DTEcndateRequest;
import com.catl.integration.pi.ecn.erp.DTEcndateResponse;
import com.catl.integration.pi.ecn.erp.SIEcndateSend;
import com.catl.integration.pi.ecn.erp.SIEcndateSendService;
import com.catl.integration.pi.file.DTZTZFILECreateRequest;
import com.catl.integration.pi.file.DTZTZFILECreateResponse;
import com.catl.integration.pi.file.SIZTZFILECreateOut;
import com.catl.integration.pi.file.SIZTZFILECreateOutService;
import com.catl.integration.pi.part.change.DTPNCHANGECreateRequest;
import com.catl.integration.pi.part.change.DTPNCHANGECreateRequest.TPN;
import com.catl.integration.pi.part.change.DTPNCHANGECreateResponse;
import com.catl.integration.pi.part.change.SIPNCHANGECreateOut;
import com.catl.integration.pi.part.change.SIPNCHANGECreateOutService;
import com.catl.integration.pi.part.create.DTMATNRCreateRequest;
import com.catl.integration.pi.part.create.DTMATNRCreateRequest.TMATNR;
import com.catl.integration.pi.part.create.DTMATNRCreateResponse;
import com.catl.integration.pi.part.create.DTMATNRCreateResponse.TRETURN;
import com.catl.integration.pi.part.create.SIMATNRCreateOut;
import com.catl.integration.pi.part.create.SIMATNRCreateOutService;
import com.catl.integration.pi.part.disable.DTMSTAECreateRequest;
import com.catl.integration.pi.part.disable.DTMSTAECreateRequest.TMSTAE;
import com.catl.integration.pi.part.disable.DTMSTAECreateResponse;
import com.catl.integration.pi.part.disable.SIMSTAECreateOut;
import com.catl.integration.pi.part.disable.SIMSTAECreateOutService;
import com.catl.integration.pi.part.fae.DTZFAECreateRequest;
import com.catl.integration.pi.part.fae.DTZFAECreateRequest.TZFAE;
import com.catl.integration.pi.part.fae.DTZFAECreateResponse;
import com.catl.integration.pi.part.fae.SIZFAECreateOut;
import com.catl.integration.pi.part.fae.SIZFAECreateOutService;
import com.catl.integration.pi.part.maturity.DTZEIVRCreateRequest;
import com.catl.integration.pi.part.maturity.DTZEIVRCreateRequest.TZEIVR;
import com.catl.integration.pi.part.maturity.DTZEIVRCreateResponse;
import com.catl.integration.pi.part.maturity.SIZEIVRCreateOut;
import com.catl.integration.pi.part.maturity.SIZEIVRCreateOutService;
import com.catl.integration.pi.part.sapfae.DTZPLMNCreateRequest;
import com.catl.integration.pi.part.sapfae.DTZPLMNCreateRequest.TZPLMN;
import com.catl.integration.pi.part.sapfae.DTZPLMNCreateResponse;
import com.catl.integration.pi.part.sapfae.SIZPLMNCreateOut;
import com.catl.integration.pi.part.sapfae.SIZPLMNCreateOutService;
import com.catl.integration.pi.sourceChange.DTBESKZCreateRequest;
import com.catl.integration.pi.sourceChange.DTBESKZCreateResponse;
import com.catl.integration.pi.sourceChange.SIBESKZCreateOut;
import com.catl.integration.pi.sourceChange.SIBESKZCreateOutService;
import com.catl.integration.pi.sourceChange.DTBESKZCreateRequest.TBESKZ;
import com.catl.loadData.StrUtils;
import com.catl.part.classification.RefreshFAEStatusUtil;
import com.catl.promotion.bean.SourceChangeXmlObjectBean;
import com.sun.xml.ws.client.BindingProviderProperties;

/**
 * PI接口服务类
 * 
 * @author ZhengJiaH
 *
 */
public class PIService {

	private static final PIService service = new PIService();
	private static Logger log = Logger.getLogger(PIService.class.getName());
	private static String username;
	private static String password;
	private static int request_timeout;
	private static int connect_timeout;
	/**
	 * ECB
	 */
	private SIEcnCreateOutService ecnService;
	private SIEcnCreateOut ecnport;

	/**
	 * Part
	 */
	private SIMATNRCreateOutService partService;
	private SIMATNRCreateOut partPort;

	/**
	 * Bom Create
	 */
	private SIBOMCreateCreateOutService bomService;
	private SIBOMCreateCreateOut bomPort;

	/**
	 * part change
	 */
	private SIPNCHANGECreateOutService partChangeService;
	private SIPNCHANGECreateOut partChangePort;

	/**
	 * bom change
	 */
	private SIBOMCHANGECreateOutService bomChangeService;
	private SIBOMCHANGECreateOut bomChangePort;

	/**
	 * drawing
	 */
	private SIZEINRCreateOutService drawingService;
	private SIZEINRCreateOut drawingPort;

	/**
	 * file
	 */
	private SIZTZFILECreateOutService fileService;
	private SIZTZFILECreateOut filePort;

	/**
	 * fae
	 */
	private SIZFAECreateOutService faeService;
	private SIZFAECreateOut faePort;

	/**
	 * 设计禁用，向SAP传送设计禁用的信息
	 */
	private SIMSTAECreateOutService partDisableService;
	private SIMSTAECreateOut partDisablePort;

	/**
	 * FAE物料成熟度升级与SAP集成
	 */
	private SIZPLMNCreateOutService partFAEService;
	private SIZPLMNCreateOut partFAEPort;

	/**
	 * 非FAE物料成熟度升级与SAP集成
	 */
	private SIZEIVRCreateOutService maturityNFAEService;
	private SIZEIVRCreateOut maturityNFAEPort;
	
	/**
	 * 采购类型更改
	 */
	private SIBESKZCreateOutService sourceChangeService;
	private SIBESKZCreateOut sourceChangePort;

	/**
	 * 受影响对象数据发送ERP,开工单时提醒变更中
	 */
	private SIEcndateSendService ecndateSendService;
	private SIEcndateSend ecndatesendPort;
	
	/**
	 * 构造方法 初始化接口权限 ECN服务端
	 */
	private PIService() {
		Properties props = null;
		try {
			props = ServiceProperties.getServiceProperties("WTServiceProviderFromProperties");
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		username = props.getProperty("sap.pi.username");
		password = props.getProperty("sap.pi.password");
		request_timeout = Integer.valueOf(props.getProperty("sap.pi.request_timeout"));
		connect_timeout = Integer.valueOf(props.getProperty("sap.pi.connect_timeout"));
		System.out.println("PIService username:" + username + ",password:" + password + ",request_timeout:" + request_timeout + ",connect_timeout:"
				+ connect_timeout);

		NtlmAuthenticator authenticator = new NtlmAuthenticator(username, password);
		Authenticator.setDefault(authenticator);

		ecnService = new SIEcnCreateOutService();
		ecnport = ecnService.getHTTPPort();
		Map<String, Object> ctxt = ((BindingProvider) ecnport).getRequestContext();
		ctxt.put(BindingProviderProperties.REQUEST_TIMEOUT, request_timeout);// 请求时间
		ctxt.put(BindingProviderProperties.CONNECT_TIMEOUT, connect_timeout);// 连接时间

		partService = new SIMATNRCreateOutService();
		partPort = partService.getHTTPPort();
		ctxt = ((BindingProvider) partPort).getRequestContext();
		ctxt.put(BindingProviderProperties.REQUEST_TIMEOUT, request_timeout);// 请求时间
		ctxt.put(BindingProviderProperties.CONNECT_TIMEOUT, connect_timeout);// 连接时间

		bomService = new SIBOMCreateCreateOutService();
		bomPort = bomService.getHTTPPort();
		ctxt = ((BindingProvider) bomPort).getRequestContext();
		ctxt.put(BindingProviderProperties.REQUEST_TIMEOUT, request_timeout);// 请求时间
		ctxt.put(BindingProviderProperties.CONNECT_TIMEOUT, connect_timeout);// 连接时间

		partChangeService = new SIPNCHANGECreateOutService();
		partChangePort = partChangeService.getHTTPPort();
		ctxt = ((BindingProvider) bomPort).getRequestContext();
		ctxt.put(BindingProviderProperties.REQUEST_TIMEOUT, request_timeout);// 请求时间
		ctxt.put(BindingProviderProperties.CONNECT_TIMEOUT, connect_timeout);// 连接时间

		bomChangeService = new SIBOMCHANGECreateOutService();
		bomChangePort = bomChangeService.getHTTPPort();
		ctxt = ((BindingProvider) bomChangePort).getRequestContext();
		ctxt.put(BindingProviderProperties.REQUEST_TIMEOUT, request_timeout);// 请求时间
		ctxt.put(BindingProviderProperties.CONNECT_TIMEOUT, connect_timeout);// 连接时间

		drawingService = new SIZEINRCreateOutService();
		drawingPort = drawingService.getHTTPPort();
		ctxt = ((BindingProvider) drawingPort).getRequestContext();
		ctxt.put(BindingProviderProperties.REQUEST_TIMEOUT, request_timeout);// 请求时间
		ctxt.put(BindingProviderProperties.CONNECT_TIMEOUT, connect_timeout);// 连接时间

		fileService = new SIZTZFILECreateOutService();
		filePort = fileService.getHTTPPort();
		ctxt = ((BindingProvider) filePort).getRequestContext();
		ctxt.put(BindingProviderProperties.REQUEST_TIMEOUT, request_timeout);// 请求时间
		ctxt.put(BindingProviderProperties.CONNECT_TIMEOUT, connect_timeout);// 连接时间

		faeService = new SIZFAECreateOutService();
		faePort = faeService.getHTTPPort();
		ctxt = ((BindingProvider) faePort).getRequestContext();
		ctxt.put(BindingProviderProperties.REQUEST_TIMEOUT, request_timeout);// 请求时间
		ctxt.put(BindingProviderProperties.CONNECT_TIMEOUT, connect_timeout);// 连接时间

		partDisableService = new SIMSTAECreateOutService();
		partDisablePort = partDisableService.getHTTPPort();
		ctxt = ((BindingProvider) partDisablePort).getRequestContext();
		ctxt.put(BindingProviderProperties.REQUEST_TIMEOUT, request_timeout);// 请求时间
		ctxt.put(BindingProviderProperties.CONNECT_TIMEOUT, connect_timeout);// 连接时间

		partFAEService = new SIZPLMNCreateOutService();
		partFAEPort = partFAEService.getHTTPPort();
		ctxt = ((BindingProvider) partFAEPort).getRequestContext();
		ctxt.put(BindingProviderProperties.REQUEST_TIMEOUT, request_timeout);// 请求时间
		ctxt.put(BindingProviderProperties.CONNECT_TIMEOUT, connect_timeout);// 连接时间

		maturityNFAEService = new SIZEIVRCreateOutService();
		maturityNFAEPort = maturityNFAEService.getHTTPPort();
		ctxt = ((BindingProvider) maturityNFAEPort).getRequestContext();
		ctxt.put(BindingProviderProperties.REQUEST_TIMEOUT, request_timeout);// 请求时间
		ctxt.put(BindingProviderProperties.CONNECT_TIMEOUT, connect_timeout);// 连接时间
		
		sourceChangeService = new SIBESKZCreateOutService();
		sourceChangePort = sourceChangeService.getHTTPPort();
		ctxt = ((BindingProvider) sourceChangePort).getRequestContext();
		ctxt.put(BindingProviderProperties.REQUEST_TIMEOUT, request_timeout);// 请求时间
		ctxt.put(BindingProviderProperties.CONNECT_TIMEOUT, connect_timeout);// 连接时间
		
		ecndateSendService = new SIEcndateSendService();
		ecndatesendPort = ecndateSendService.getHTTPPort();
		ctxt = ((BindingProvider) ecndatesendPort).getRequestContext();
		ctxt.put(BindingProviderProperties.REQUEST_TIMEOUT, request_timeout);// 请求时间
		ctxt.put(BindingProviderProperties.CONNECT_TIMEOUT, connect_timeout);// 连接时间
	}

	public static PIService getInstance() {
		return service;
	}

	/**
	 * 发布ECN
	 * 
	 * @param ecns
	 * @param company
	 *            default CATL
	 * @return
	 */
	public ErpResponse sendECN(List<EcInfo> ecns, String company) throws Exception {
		if (ecns == null)
			throw new NullPointerException("参数为null");
		if (ecns.size() == 0)
			throw new IllegalArgumentException("参数为空,size=0");

		DTEcnCreateRequest request = new DTEcnCreateRequest();
		request.setSYSNAME(company);
		List<TECN> list = request.getTECN();
		TECN tecn = null;
		TransactionLog tlog;
		WTList logList = new WTArrayList();
		for (EcInfo ecn : ecns) {
			log.debug("sendECN ecnNumber:" + ecn.getNumber() + ",validDate:" + ecn.getValidDate());
			tecn = new TECN();
			tecn.setAENNR(ecn.getNumber());
			if (ecn.getDescription() != null) {
				tecn.setAEGRU(ecn.getDescription().length() > 40 ? ecn.getDescription().substring(0, 40) : ecn.getDescription());//
			}
			if (ecn.getName() != null) {
				tecn.setAETXT(ecn.getName().length() > 40 ? ecn.getName().substring(0, 40) : ecn.getName());
			}
			tecn.setDATUV(ecn.getValidDate());
			list.add(tecn);

			tlog = new TransactionLog();
			tlog.setAction(Message.ECN);
			tlog.setOid(ecn.getOid());
			tlog.setEcnNumber(ecn.getNumber());
			tlog.setDescription(ecn.getDescription());//
			tlog.setEcnName(ecn.getName());
			tlog.setValidDate(ecn.getValidDate());
			logList.add(tlog);
		}
		PersistenceHelper.manager.save(logList);
		DTEcnCreateResponse response = ecnport.siEcnCreateOut(request);

		Map<String, Message> map = new HashMap<String, Message>();
		for (TECNR ecnr : response.getTECNR()) {

			String status = ecnr.getSTATUS();
			String number = ecnr.getAENNR();
			String text = ecnr.getMESSAGE();

			Message message = map.get(number);
			if (message == null) {
				message = new Message();
				message.setAction(Message.ECN);
				message.setEcnNumber(number);
				message.setSuccess(status.equals("S") ? true : false);
				message.setText(text);
				map.put(number, message);
			} else {
				message.setText(message.getText() + "|" + text);
				map.put(number, message);
			}
		}
		log.debug("sendECN isSuccess:" + (response.getEACKNOW().getResult().equals("S") ? "true" : "false"));
		ErpResponse erpResponse = new ErpResponse();
		erpResponse.setSuccess(response.getEACKNOW().getResult().equals("S") ? true : false);
		erpResponse.setMessage(new ArrayList<Message>(map.values()));

		return erpResponse;
	}

	/**
	 * 发送创建Part
	 * 
	 * @param parts
	 * @param company
	 *            default CATL
	 * @return
	 */
	public ErpResponse sendParts(List<PartInfo> parts, String company) throws Exception {

		DTMATNRCreateRequest request = new DTMATNRCreateRequest();
		request.setSYSNAME(company);
		List<TMATNR> list = request.getTMATNR();
		TMATNR matnr;
		TransactionLog tlog;
		WTList logList = new WTArrayList();
		for (PartInfo part : parts) {
			log.debug("sendParts partNumber:" + part.getPartNumber() + ",ecnNumber:" + part.getEcnNumber() + ",quantity:" + part.getDefaultUnit() + ",group:"
					+ part.getMaterialGroup() + ",source:" + part.getSource());

			matnr = new TMATNR();
			matnr.setBESKZ(part.getSource());
			matnr.setERNAM(part.getCreator());
			matnr.setMAKTXEN(part.getEnglishName());
			matnr.setMAKTXZH(part.getPartName());
			matnr.setMATKL(part.getMaterialGroup());
			matnr.setMATNR(part.getPartNumber());
			matnr.setMEINS(part.getDefaultUnit());
			matnr.setZBCDY(part.getStandardVoltage());
			matnr.setZCPRL(part.getProductEnergy());
			matnr.setZEIAR(part.getDrawingVersion());
			matnr.setZEINR(part.getDrawing());// 图纸
			matnr.setZMCDY(part.getFullVoltage());
			matnr.setZMODULE(part.getModel());
			matnr.setZZWLGG(StrUtils.isEmpty(part.getSpecification()) ? "无" : part.getSpecification());
			matnr.setZDXNL(part.getCellVolume());
			matnr.setBISMT(part.getOldPartNumber());// 旧物料号
			if (part.getCellMode() != null && part.getCellMode().length() > 1) {
				matnr.setZZDXCLCODE(part.getCellMode().substring(0, 2));// 电芯类型
			}
			matnr.setZBQFS(part.getCellConnectionMode());// 电芯并串联方式
			matnr.setZMZ(part.getModuleQuantity());// 模组数量(PCS)
			matnr.setZHWV(part.getHardwareVersion());// HW版本
			matnr.setZSWV(part.getSoftwareVersion());// SW版本
			matnr.setZPARV(part.getParameterVersion());// PAR版本
			matnr.setZFAE(part.getFaeStatus());// FAE状态
			matnr.setZMATNR(part.getParentPN());
			matnr.setZLENGTH(part.getL());
			list.add(matnr);

			tlog = new TransactionLog();
			tlog.setOid(part.getOid());
			tlog.setVersionBig(part.getVersionBig());
			tlog.setVersionSmall(part.getVersionSmall());
			tlog.setAction(Message.PART_CREATE);
			tlog.setSource(part.getSource());
			tlog.setCreator(part.getCreator());
			tlog.setEnglishName(part.getEnglishName());
			tlog.setPartName(part.getPartName());
			tlog.setMaterialGroup(part.getMaterialGroup());
			tlog.setPartNumber(part.getPartNumber());
			tlog.setDefaultUnit(part.getDefaultUnit());
			tlog.setStandardVoltage(part.getStandardVoltage());
			tlog.setProductEnergy(part.getProductEnergy());
			tlog.setDrawingVersion(part.getDrawingVersion());
			tlog.setDrawingNumber(part.getDrawing());// 图纸
			tlog.setFullVoltage(part.getFullVoltage());
			tlog.setModel(part.getModel());
			tlog.setSpecification(StrUtils.isEmpty(part.getSpecification()) ? "无" : part.getSpecification());
			tlog.setCellVolume(part.getCellVolume());
			tlog.setEcnNumber(part.getEcnNumber());
			tlog.setOldPartNumber(part.getOldPartNumber());
			if (part.getCellMode() != null && part.getCellMode().length() > 1) {
				tlog.setCellMode(part.getCellMode().substring(0, 2));// 电芯类型
			}
			tlog.setStr2(part.getCellConnectionMode());// 电芯并串联方式
			tlog.setStr3(part.getModuleQuantity());// 模组数量(PCS)
			tlog.setStr4(part.getHardwareVersion());// HW版本
			tlog.setStr5(part.getSoftwareVersion());// SW版本
			tlog.setStr6(part.getParameterVersion());// PAR版本
			tlog.setStr7(part.getFaeStatus());// FAE状态
			tlog.setStr9(part.getParentPN());//
			tlog.setStr10(part.getL());//
			logList.add(tlog);
		}
		PersistenceHelper.manager.save(logList);

		DTMATNRCreateResponse response = partPort.siMATNRCreateOut(request);

		Map<String, Message> map = new HashMap<String, Message>();
		for (TRETURN ret : response.getTRETURN()) {

			String status = ret.getSTATUS();
			String number = ret.getMATNR();
			String text = ret.getMESSAGE();

			Message message = map.get(number);
			if (message == null) {
				message = new Message();
				message.setAction(Message.PART_CREATE);
				message.setNumber(number);
				message.setSuccess(status.equals("S") ? true : false);
				message.setText(text);
				map.put(number, message);
			} else {
				message.setText(message.getText() + "|" + text);
				map.put(number, message);
			}
		}
		log.debug("sendParts isSuccess:" + (response.getEACKNOW().getResult().equals("S") ? "true" : "false"));
		ErpResponse erpResponse = new ErpResponse();
		erpResponse.setSuccess(response.getEACKNOW().getResult().equals("S") ? true : false);
		erpResponse.setMessage(new ArrayList<Message>(map.values()));

		return erpResponse;

	}

	/**
	 * 发送创建Bom
	 * 
	 * @param boms
	 * @param company
	 * @return
	 */
	public ErpResponse sendBoms(List<BomInfo> boms, String company) throws Exception {
		DTBomCreateCreateRequest request = new DTBomCreateCreateRequest();
		request.setSYSNAME(company);
		List<TBOMIN> list = request.getTBOMIN();
		TBOMIN bomin;
		TransactionLog tlog;
		WTList logList = new WTArrayList();
		for (BomInfo bom : boms) {
			log.debug("sendBomsChange ecnNumber:" + bom.getEcnNumber() +",bomNumber:" + bom.getParentPartNumber()  +",magnification:" + bom.getMagnification()+ ",childPartNumber:"
					+ bom.getChildPartNumber()+",quantity:" + bom.getQuantity() + ",subNumber:" + bom.getSubstitutePartNumber() + ",subQuantity:" + bom.getSubQuantity());
			bomin = new TBOMIN();
			bomin.setMATNR(bom.getParentPartNumber());
			bomin.setBMENG(bom.getMagnification()+"");//放大倍数   基本数量
			bomin.setIDNRK(bom.getChildPartNumber());
			bomin.setZTDAI(bom.getSubstitutePartNumber());
			bomin.setMENGE(bom.getQuantity() + "");
			if (!StrUtils.isEmpty(bom.getSubstitutePartNumber())) {
				bomin.setZTMENGE(bom.getSubQuantity() + "");// 替代件数量
			}
			list.add(bomin);

			tlog = new TransactionLog();
			tlog.setOid(bom.getOid());
			tlog.setVersionBig(bom.getVersionBig());
			tlog.setVersionSmall(bom.getVersionSmall());
			tlog.setAction(Message.BOM_CREATE);
			tlog.setPartNumber(bom.getParentPartNumber());
			tlog.setChildPartNumber(bom.getChildPartNumber());
			tlog.setSubstitutePartNumber(bom.getSubstitutePartNumber());
			tlog.setQuantity(bom.getQuantity() + "");
			tlog.setEcnNumber(bom.getEcnNumber());
			tlog.setStr8(bom.getMagnification()+"");//放大倍数   基本数量
			if (!StrUtils.isEmpty(bom.getSubstitutePartNumber())) {
				tlog.setStr1(bom.getSubQuantity() + "");// 替代件数量
			}
			logList.add(tlog);
		}
		PersistenceHelper.manager.save(logList);
		DTBOMCreateCreateResponse response = bomPort.siBOMCreateCreateOut(request);

		Map<String, Message> map = new HashMap<String, Message>();
		for (TBOMOUT ret : response.getTBOMOUT()) {

			String status = ret.getSTATUS();
			String parent = ret.getMATNR();
			String text = ret.getMESSAGE();
			String stitute = ret.getZTDAI();
			String child = ret.getIDNRK();

			Message message = map.get(parent + "," + stitute + "," + child);
			if (message == null) {
				message = new Message();
				message.setAction(Message.BOM_CREATE);
				message.setNumber(parent);
				message.setChildNumber(child);
				message.setStituteNumber(stitute);
				message.setSuccess(status.equals("S") ? true : false);
				message.setText(text);
				map.put(parent + "," + stitute + "," + child, message);
			} else {
				message.setText(message.getText() + "|" + text);
				map.put(parent + "," + stitute + "," + child, message);
			}
		}
		log.debug("sendBoms isSuccess:" + (response.getEACKNOW().getResult().equals("S") ? "true" : "false"));
		ErpResponse erpResponse = new ErpResponse();
		erpResponse.setSuccess(response.getEACKNOW().getResult().equals("S") ? true : false);
		erpResponse.setMessage(new ArrayList<Message>(map.values()));

		return erpResponse;

	}

	/**
	 * 发送修改Part
	 * 
	 * @param parts
	 * @param company
	 *            default CATL
	 * @return
	 */
	public ErpResponse sendPartsChange(List<PartInfo> parts, String company) throws Exception {

		DTPNCHANGECreateRequest request = new DTPNCHANGECreateRequest();
		request.setSYSNAME(company);
		List<TPN> list = request.getTPN();
		TPN tpn;
		TransactionLog tlog;
		WTList logList = new WTArrayList();
		String ecnNumber = null;
		for (PartInfo part : parts) {
			log.debug("sendPartsChange partNumber:" + part.getPartNumber() + ",ecnNumber:" + part.getEcnNumber() + ",quantity:" + part.getDefaultUnit()
					+ ",group:" + part.getMaterialGroup());
			ecnNumber = part.getEcnNumber();
			tpn = new TPN();
			tpn.setBESKZ(part.getSource());
			tpn.setERNAM(part.getCreator());
			tpn.setMAKTXEN(part.getEnglishName());
			tpn.setMAKTXZH(part.getPartName());
			tpn.setMATKL(part.getMaterialGroup());
			tpn.setMATNR(part.getPartNumber());
			tpn.setMEINS(part.getDefaultUnit());
			tpn.setZBCDY(part.getStandardVoltage());
			tpn.setZCPRL(part.getProductEnergy());
			tpn.setZEIAR(part.getDrawingVersion());
			tpn.setZEINR(part.getDrawing());// 图纸
			tpn.setZMCDY(part.getFullVoltage());
			tpn.setZMODULE(part.getModel());
			tpn.setZZWLGG(StrUtils.isEmpty(part.getSpecification()) ? "无" : part.getSpecification());
			tpn.setAENNR(part.getEcnNumber());
			tpn.setZDXNL(part.getCellVolume());
			tpn.setBISMT(part.getOldPartNumber());// 旧物料号
			if (part.getCellMode() != null && part.getCellMode().length() > 1) {
				tpn.setZZDXCLCODE(part.getCellMode().substring(0, 2));// 电芯类型
			}
			tpn.setZBQFS(part.getCellConnectionMode());// 电芯并串联方式
			tpn.setZMZ(part.getModuleQuantity());// 模组数量(PCS)
			tpn.setZHWV(part.getHardwareVersion());// HW版本
			tpn.setZSWV(part.getSoftwareVersion());// SW版本
			tpn.setZPARV(part.getParameterVersion());// PAR版本
			tpn.setZFAE(part.getFaeStatus());// FAE状态
			tpn.setZMATNR(part.getParentPN());
			tpn.setZLENGTH(part.getL());
			list.add(tpn);

			tlog = new TransactionLog();
			tlog.setOid(part.getOid());
			tlog.setVersionBig(part.getVersionBig());
			tlog.setVersionSmall(part.getVersionSmall());
			tlog.setAction(Message.PART_CHANGE);
			tlog.setSource(part.getSource());
			tlog.setCreator(part.getCreator());
			tlog.setEnglishName(part.getEnglishName());
			tlog.setPartName(part.getPartName());
			tlog.setMaterialGroup(part.getMaterialGroup());
			tlog.setPartNumber(part.getPartNumber());
			tlog.setDefaultUnit(part.getDefaultUnit());
			tlog.setStandardVoltage(part.getStandardVoltage());
			tlog.setProductEnergy(part.getProductEnergy());
			tlog.setDrawingVersion(part.getDrawingVersion());
			tlog.setDrawingNumber(part.getDrawing());// 图纸
			tlog.setFullVoltage(part.getFullVoltage());
			tlog.setModel(part.getModel());
			tlog.setSpecification(StrUtils.isEmpty(part.getSpecification()) ? "无" : part.getSpecification());
			tlog.setCellVolume(part.getCellVolume());
			tlog.setEcnNumber(part.getEcnNumber());
			tlog.setOldPartNumber(part.getOldPartNumber());
			if (part.getCellMode() != null && part.getCellMode().length() > 1) {
				tlog.setCellMode(part.getCellMode().substring(0, 2));// 电芯类型
			}
			tlog.setStr2(part.getCellConnectionMode());// 电芯并串联方式
			tlog.setStr3(part.getModuleQuantity());// 模组数量(PCS)
			tlog.setStr4(part.getHardwareVersion());// HW版本
			tlog.setStr5(part.getSoftwareVersion());// SW版本
			tlog.setStr6(part.getParameterVersion());// PAR版本
			tlog.setStr7(part.getFaeStatus());// FAE状态
			tlog.setStr9(part.getParentPN());//
			tlog.setStr10(part.getL());//
			logList.add(tlog);
		}
		PersistenceHelper.manager.save(logList);
		DTPNCHANGECreateResponse response = partChangePort.siPNCHANGECreateOut(request);

		Map<String, Message> map = new HashMap<String, Message>();
		for (com.catl.integration.pi.part.change.DTPNCHANGECreateResponse.TRETURN ret : response.getTRETURN()) {

			String status = ret.getSTATUS();
			String number = ret.getMATNR();
			String text = ret.getMESSAGE();

			Message message = map.get(number);
			if (message == null) {
				message = new Message();
				message.setAction(Message.PART_CHANGE);
				message.setNumber(number);
				message.setSuccess(status.equals("S") ? true : false);
				message.setText(text);
				message.setEcnNumber(ecnNumber);
				map.put(number, message);
			} else {
				message.setText(message.getText() + "|" + text);
				map.put(number, message);
			}
		}
		log.debug("sendPartsChange isSuccess:" + (response.getEACKNOW().getResult().equals("S") ? "true" : "false"));
		ErpResponse erpResponse = new ErpResponse();
		erpResponse.setSuccess(response.getEACKNOW().getResult().equals("S") ? true : false);
		erpResponse.setMessage(new ArrayList<Message>(map.values()));

		return erpResponse;

	}

	/**
	 * 发布流程发送修改P开头Part
	 * 
	 * @param parts
	 * @param company
	 *            default CATL
	 * @return
	 */
	public ErpResponse sendStartPPartsChange(List<PartInfo> parts, String company) throws Exception {

		DTPNCHANGECreateRequest request = new DTPNCHANGECreateRequest();
		request.setSYSNAME(company);
		List<TPN> list = request.getTPN();
		TPN tpn;
		TransactionLog tlog;
		WTList logList = new WTArrayList();
		String ecnNumber = null;
		for (PartInfo part : parts) {
			log.debug("sendStartPPartsChange partNumber:" + part.getPartNumber() + ",ecnNumber:" + PropertiesUtil.getValueByKey("config_startp_ecnnumber") + ",quantity:" + part.getDefaultUnit()
					+ ",group:" + part.getMaterialGroup());
			ecnNumber = part.getEcnNumber();
			tpn = new TPN();
			tpn.setBESKZ(part.getSource());
			tpn.setERNAM(part.getCreator());
			tpn.setMAKTXEN(part.getEnglishName());
			tpn.setMAKTXZH(part.getPartName());
			tpn.setMATKL(part.getMaterialGroup());
			tpn.setMATNR(part.getPartNumber());
			tpn.setMEINS(part.getDefaultUnit());
			tpn.setZBCDY(part.getStandardVoltage());
			tpn.setZCPRL(part.getProductEnergy());
			tpn.setZEIAR(part.getDrawingVersion());
			tpn.setZEINR(part.getDrawing());// 图纸
			tpn.setZMCDY(part.getFullVoltage());
			tpn.setZMODULE(part.getModel());
			tpn.setZZWLGG(StrUtils.isEmpty(part.getSpecification()) ? "无" : part.getSpecification());
			tpn.setAENNR(PropertiesUtil.getValueByKey("config_startp_ecnnumber"));
			tpn.setZDXNL(part.getCellVolume());
			tpn.setBISMT(part.getOldPartNumber());// 旧物料号
			if (part.getCellMode() != null && part.getCellMode().length() > 1) {
				tpn.setZZDXCLCODE(part.getCellMode().substring(0, 2));// 电芯类型
			}
			tpn.setZBQFS(part.getCellConnectionMode());// 电芯并串联方式
			tpn.setZMZ(part.getModuleQuantity());// 模组数量(PCS)
			tpn.setZHWV(part.getHardwareVersion());// HW版本
			tpn.setZSWV(part.getSoftwareVersion());// SW版本
			tpn.setZPARV(part.getParameterVersion());// PAR版本
			tpn.setZFAE(part.getFaeStatus());// FAE状态
			tpn.setZMATNR(part.getParentPN());
			tpn.setZLENGTH(part.getL());
			list.add(tpn);

			tlog = new TransactionLog();
			tlog.setOid(part.getOid());
			tlog.setVersionBig(part.getVersionBig());
			tlog.setVersionSmall(part.getVersionSmall());
			tlog.setAction(Message.PART_CHANGE);
			tlog.setSource(part.getSource());
			tlog.setCreator(part.getCreator());
			tlog.setEnglishName(part.getEnglishName());
			tlog.setPartName(part.getPartName());
			tlog.setMaterialGroup(part.getMaterialGroup());
			tlog.setPartNumber(part.getPartNumber());
			tlog.setDefaultUnit(part.getDefaultUnit());
			tlog.setStandardVoltage(part.getStandardVoltage());
			tlog.setProductEnergy(part.getProductEnergy());
			tlog.setDrawingVersion(part.getDrawingVersion());
			tlog.setDrawingNumber(part.getDrawing());// 图纸
			tlog.setFullVoltage(part.getFullVoltage());
			tlog.setModel(part.getModel());
			tlog.setSpecification(StrUtils.isEmpty(part.getSpecification()) ? "无" : part.getSpecification());
			tlog.setCellVolume(part.getCellVolume());
			tlog.setEcnNumber(PropertiesUtil.getValueByKey("config_startp_ecnnumber"));
			tlog.setOldPartNumber(part.getOldPartNumber());
			if (part.getCellMode() != null && part.getCellMode().length() > 1) {
				tlog.setCellMode(part.getCellMode().substring(0, 2));// 电芯类型
			}
			tlog.setStr2(part.getCellConnectionMode());// 电芯并串联方式
			tlog.setStr3(part.getModuleQuantity());// 模组数量(PCS)
			tlog.setStr4(part.getHardwareVersion());// HW版本
			tlog.setStr5(part.getSoftwareVersion());// SW版本
			tlog.setStr6(part.getParameterVersion());// PAR版本
			tlog.setStr7(part.getFaeStatus());// FAE状态
			tlog.setStr9(part.getParentPN());//
			tlog.setStr10(part.getL());//
			logList.add(tlog);
		}
		PersistenceHelper.manager.save(logList);
		DTPNCHANGECreateResponse response = partChangePort.siPNCHANGECreateOut(request);

		Map<String, Message> map = new HashMap<String, Message>();
		for (com.catl.integration.pi.part.change.DTPNCHANGECreateResponse.TRETURN ret : response.getTRETURN()) {

			String status = ret.getSTATUS();
			String number = ret.getMATNR();
			String text = ret.getMESSAGE();

			Message message = map.get(number);
			if (message == null) {
				message = new Message();
				message.setAction(Message.PART_CHANGE);
				message.setNumber(number);
				message.setSuccess(status.equals("S") ? true : false);
				message.setText(text);
				message.setEcnNumber(ecnNumber);
				map.put(number, message);
			} else {
				message.setText(message.getText() + "|" + text);
				map.put(number, message);
			}
		}
		log.debug("sendPartsChange isSuccess:" + (response.getEACKNOW().getResult().equals("S") ? "true" : "false"));
		ErpResponse erpResponse = new ErpResponse();
		erpResponse.setSuccess(response.getEACKNOW().getResult().equals("S") ? true : false);
		erpResponse.setMessage(new ArrayList<Message>(map.values()));

		return erpResponse;

	}
	public ErpResponse sendBomsChange(List<BomInfo> boms, String company) throws Exception {
		DTBOMCHANGECreateRequest request = new DTBOMCHANGECreateRequest();
		request.setSYSNAME(company);
		List<com.catl.integration.pi.bom.change.DTBOMCHANGECreateRequest.TBOMIN> list = request.getTBOMIN();
		com.catl.integration.pi.bom.change.DTBOMCHANGECreateRequest.TBOMIN bomin;
		String ecnNumber = null;
		TransactionLog tlog;
		WTList logList = new WTArrayList();
		for (BomInfo bom : boms) {
			log.debug("sendBomsChange ecnNumber:" + bom.getEcnNumber() +",bomNumber:" + bom.getParentPartNumber()  +",magnification:" + bom.getMagnification()+ ",childPartNumber:"
					+ bom.getChildPartNumber()+",quantity:" + bom.getQuantity() + ",subNumber:" + bom.getSubstitutePartNumber() + ",subQuantity:" + bom.getSubQuantity());
			ecnNumber = bom.getEcnNumber();
			bomin = new com.catl.integration.pi.bom.change.DTBOMCHANGECreateRequest.TBOMIN();
			bomin.setMATNR(bom.getParentPartNumber());
			bomin.setBMENG(bom.getMagnification()+"");//放大倍数   基本数量
			bomin.setIDNRK(bom.getChildPartNumber());
			bomin.setZTDAI(bom.getSubstitutePartNumber());
			bomin.setMENGE(bom.getQuantity() + "");
			bomin.setAENNR(bom.getEcnNumber());
			if (!StrUtils.isEmpty(bom.getSubstitutePartNumber())) {
				bomin.setZTMENGE(bom.getSubQuantity() + "");// 替代件数量
			}
			list.add(bomin);

			tlog = new TransactionLog();
			tlog.setOid(bom.getOid());
			tlog.setVersionBig(bom.getVersionBig());
			tlog.setVersionSmall(bom.getVersionSmall());
			tlog.setAction(Message.BOM_CHANGE);
			tlog.setPartNumber(bom.getParentPartNumber());
			tlog.setChildPartNumber(bom.getChildPartNumber());
			tlog.setSubstitutePartNumber(bom.getSubstitutePartNumber());
			tlog.setQuantity(bom.getQuantity() + "");
			tlog.setEcnNumber(bom.getEcnNumber());
			tlog.setStr8(bom.getMagnification()+"");//放大倍数   基本数量
			if (!StrUtils.isEmpty(bom.getSubstitutePartNumber())) {
				tlog.setStr1(bom.getSubQuantity() + "");// 替代件数量
			}
			logList.add(tlog);
		}
		PersistenceHelper.manager.save(logList);
		DTBOMCHANGECreateResponse response = bomChangePort.siBOMCHANGECreateOut(request);

		Map<String, Message> map = new HashMap<String, Message>();
		for (com.catl.integration.pi.bom.change.DTBOMCHANGECreateResponse.TBOMOUT ret : response.getTBOMOUT()) {

			String status = ret.getSTATUS();
			String parent = ret.getMATNR();
			String text = ret.getMESSAGE();
			String stitute = ret.getZTDAI();
			String child = ret.getIDNRK();

			Message message = map.get(parent + "," + stitute + "," + child);
			if (message == null) {
				message = new Message();
				message.setAction(Message.BOM_CHANGE);
				message.setNumber(parent);
				message.setChildNumber(child);
				message.setStituteNumber(stitute);
				message.setSuccess(status.equals("S") ? true : false);
				message.setText(text);
				message.setEcnNumber(ecnNumber);
				map.put(parent + "," + stitute + "," + child, message);
			} else {
				message.setText(message.getText() + "|" + text);
				map.put(parent + "," + stitute + "," + child, message);
			}
		}
		log.debug("sendBomsChange isSuccess:" + (response.getEACKNOW().getResult().equals("S") ? "true" : "false"));
		ErpResponse erpResponse = new ErpResponse();
		erpResponse.setSuccess(response.getEACKNOW().getResult().equals("S") ? true : false);
		erpResponse.setMessage(new ArrayList<Message>(map.values()));

		return erpResponse;

	}

	public ErpResponse sendDrawing(List<DrawingInfo> drawings, String company) throws Exception {
		DTZEINRCreateRequest request = new DTZEINRCreateRequest();
		request.setSYSNAME(company);
		List<TZEINR> list = request.getTZEINR();
		TZEINR drawingin;
		TransactionLog tlog;
		WTList logList = new WTArrayList();
		for (DrawingInfo drawing : drawings) {
			log.debug("sendDrawing partNumber:" + drawing.getPartNumber() + ",drawingNumber:" + drawing.getDrawingNumber() + ",drawingVersion:"
					+ drawing.getDrawingVersion());
			drawingin = new TZEINR();
			drawingin.setMATNR(drawing.getPartNumber());
			drawingin.setZEINR(drawing.getDrawingNumber());
			drawingin.setZEIAR(drawing.getDrawingVersion());
			list.add(drawingin);

			tlog = new TransactionLog();
			tlog.setOid(drawing.getOid());
			tlog.setAction(Message.DRAWING);
			tlog.setPartNumber(drawing.getPartNumber());
			tlog.setDrawingNumber(drawing.getDrawingNumber());
			tlog.setDrawingVersion(drawing.getDrawingVersion());
			logList.add(tlog);
		}
		PersistenceHelper.manager.save(logList);
		DTZEINRCreateResponse response = drawingPort.siZEINRCreateOut(request);

		Map<String, Message> map = new HashMap<String, Message>();
		for (com.catl.integration.pi.drawing.DTZEINRCreateResponse.TRETURN ret : response.getTRETURN()) {

			String status = ret.getSTATUS();
			String partNumer = ret.getMATNR();
			String text = ret.getMESSAGE();
			String drawingNumber = ret.getZEINR();
			String drawingVersion = ret.getZEIAR();

			Message message = map.get(drawingNumber);
			if (message == null) {
				message = new Message();
				message.setAction(Message.DRAWING);
				message.setDrawingNumber(drawingNumber);
				message.setNumber(partNumer);
				message.setDrawingVersion(drawingVersion);
				message.setSuccess(status.equals("S") ? true : false);
				message.setText(text);
				map.put(drawingNumber, message);
			} else {
				message.setText(message.getText() + "|" + text);
				map.put(drawingNumber, message);
			}
		}
		log.debug("sendDrawing isSuccess:" + (response.getEACKNOW().getResult().equals("S") ? "true" : "false"));
		for (Message msg : map.values()) {
			log.debug("sendDrawing number:" + msg.getNumber() + ",text:" + msg.getText());
		}
		ErpResponse erpResponse = new ErpResponse();
		erpResponse.setSuccess(response.getEACKNOW().getResult().equals("S") ? true : false);
		erpResponse.setMessage(new ArrayList<Message>(map.values()));

		return erpResponse;

	}

	public String sendFile(DrawingSendERP file, String company) throws Exception {
		DTZTZFILECreateRequest request = new DTZTZFILECreateRequest();
		request.setSYSNAME(company);
		request.setIMATNR(file.getPartNumber());
		request.setIPLMURL(file.getAddress());
		request.setIFILENAME(file.getFileName());
		log.debug("partNumber=" + file.getPartNumber() + ",fileName=" + file.getFileName() + ",address=" + file.getAddress());
		DTZTZFILECreateResponse response = filePort.siZTZFILECreateOut(request);
		log.debug("partNumber=" + file.getPartNumber() + " sendFile isSuccess:" + (response.getOSUBRC().equals("S") ? "true" : "false"));
		if (!response.getOSUBRC().equals("S")) {
			return "发送图纸异常，错误原因：" + file.getPartNumber() + " " + response.getOMESSAGE() + "\n";
		}
		return null;
	}

	public String sendFiles(Collection<DrawingSendERP> files, String company) throws Exception {
		StringBuffer sb = new StringBuffer();
		for (DrawingSendERP file : files) {
			String ret = sendFile(file, company);
			if (ret != null)
				sb.append(ret);
		}
		return sb.toString();
	}

	public DTZFAECreateResponse sendFAE(String company, Collection<String[]> infos) {
		//准备交易日志所需数据
		WebServiceTransactionLog transactionLog = new WebServiceTransactionLog();
		transactionLog.setServiceType(WebServiceTransactionLog.SERVICE_TYPE_CLIENT);
		transactionLog.setServiceSide("ERP");
		transactionLog.setClientSide("PLM");
		transactionLog.setClientId("");
		transactionLog.setClientClass(faePort.getClass().getName());
		transactionLog.setClientMethod("siZFAECreateOut");
		transactionLog.setStartTime(new Timestamp(System.currentTimeMillis()));
		
		
		DTZFAECreateRequest request = new DTZFAECreateRequest();
		request.setSYSNAME(company);
		List<TZFAE> list = request.getTZFAE();
		for (String[] info : infos) {
			TZFAE fae = new TZFAE();
			fae.setMATNR(info[0]);
			fae.setZFAE(info[2]);
			list.add(fae);
		}
		
		// 记录交易日志的结果信息的信息
		WebServiceTransactionInfo transactionInfo = new WebServiceTransactionInfo();
		transactionLog.setTransactionInfo(transactionInfo);
		transactionInfo.setParameterObject(request);
		
		
		try {
			// 调用Web Service
			DTZFAECreateResponse response = faePort.siZFAECreateOut(request);
			
			//记录Web Service调用的结果信息
			transactionLog.setFinishTime(new Timestamp(System.currentTimeMillis()));
			transactionInfo.setResultObject(response);
			transactionLog.setStatus(WebServiceTransactionLog.STATUS_SUCCESS);
			
			return response;
		} catch (Exception e) {
			// 记录Web Service发生异常时的信息
			transactionLog.setFinishTime(new Timestamp(System.currentTimeMillis()));
			transactionLog.setStatus(WebServiceTransactionLog.STATUS_FAILED);
			transactionInfo.setException(e);
			throw e;
		} finally {
			TransactionLogHelper.logTransaction(transactionLog);
		}
	}

	public DTMSTAECreateResponse sendPartDisable(List<PartDisableInfo> partDisableInfos, String comany) throws Exception {
		// 准备交易日志所需数据
		WebServiceTransactionLog transactionLog = new WebServiceTransactionLog();
		transactionLog.setServiceType(WebServiceTransactionLog.SERVICE_TYPE_CLIENT);
		transactionLog.setServiceSide("ERP");
		transactionLog.setClientSide("PLM");
		transactionLog.setClientId("");
		transactionLog.setClientClass(partDisablePort.getClass().getName());
		transactionLog.setClientMethod("SIMSTAECreateOut");
		transactionLog.setStartTime(new Timestamp(System.currentTimeMillis()));

		// 准备Web Service调用参数
		DTMSTAECreateRequest requets = new DTMSTAECreateRequest();
		requets.setSYSNAME(comany);
		List<TMSTAE> parts = requets.getTMSTAE();
		for (PartDisableInfo info : partDisableInfos) {
			TMSTAE tmstae = new com.catl.integration.pi.part.disable.DTMSTAECreateRequest.TMSTAE();
			tmstae.setMATNR(info.getPartNumber());
			tmstae.setMSTAE(info.getStatus());
			parts.add(tmstae);
		}

		// 记录交易日志的结果信息的信息
		WebServiceTransactionInfo transactionInfo = new WebServiceTransactionInfo();
		transactionLog.setTransactionInfo(transactionInfo);
		transactionInfo.setParameterObject(requets);
		
		try {
			// 调用Web Service
			DTMSTAECreateResponse result = partDisablePort.siMSTAECreateOut(requets);

			// 记录Web Service调用的结果信息
			transactionLog.setFinishTime(new Timestamp(System.currentTimeMillis()));
			transactionInfo.setResultObject(result);
			transactionLog.setStatus(WebServiceTransactionLog.STATUS_SUCCESS);

			return result;
		} catch (Exception e) {
			// 记录Web Service发生异常时的信息
			transactionLog.setFinishTime(new Timestamp(System.currentTimeMillis()));
			transactionLog.setStatus(WebServiceTransactionLog.STATUS_FAILED);
			transactionInfo.setException(e);
			throw e;
		} finally {
			TransactionLogHelper.logTransaction(transactionLog);
		}
	}

	public DTZPLMNCreateResponse sendPartFAEDisable(List<PartFAEDisableInfo> PartFAEDisableInfo, String comany) throws Exception {
		// 准备交易日志所需数据
		WebServiceTransactionLog transactionLog = new WebServiceTransactionLog();
		transactionLog.setServiceType(WebServiceTransactionLog.SERVICE_TYPE_CLIENT);
		transactionLog.setServiceSide("ERP");
		transactionLog.setClientSide("PLM");
		transactionLog.setClientId("");
		transactionLog.setClientClass(partFAEPort.getClass().getName());
		transactionLog.setClientMethod("siZPLMNCreateOut");
		transactionLog.setStartTime(new Timestamp(System.currentTimeMillis()));

		// 准备Web Service调用参数
		DTZPLMNCreateRequest requets = new DTZPLMNCreateRequest();
		requets.setSYSNAME(comany);
		List<TZPLMN> parts = requets.getTZPLMN();
		for (PartFAEDisableInfo info : PartFAEDisableInfo) {
			TZPLMN tfae = new DTZPLMNCreateRequest.TZPLMN();
			tfae.setMATNR(info.getPartNumber());
			tfae.setZLMN(info.getJobNumber());
			parts.add(tfae);
		}

		// 记录交易日志的结果信息的信息
		WebServiceTransactionInfo transactionInfo = new WebServiceTransactionInfo();
		transactionLog.setTransactionInfo(transactionInfo);
		transactionInfo.setParameterObject(requets);

		try {
			// 调用Web Service
			DTZPLMNCreateResponse result = partFAEPort.siZPLMNCreateOut(requets);

			// 记录Web Service调用的结果信息
			transactionLog.setFinishTime(new Timestamp(System.currentTimeMillis()));
			transactionInfo.setResultObject(result);
			transactionLog.setStatus(WebServiceTransactionLog.STATUS_SUCCESS);

			return result;
		} catch (Exception e) {
			// 记录Web Service发生异常时的信息
			transactionLog.setFinishTime(new Timestamp(System.currentTimeMillis()));
			transactionLog.setStatus(WebServiceTransactionLog.STATUS_FAILED);
			transactionInfo.setException(e);
			throw e;
		} finally {
			TransactionLogHelper.logTransaction(transactionLog);
		}
	}

	public DTZEIVRCreateResponse sendMaturityNFAE(String company, Collection<String[]> infos) {
		//准备交易日志所需数据
		WebServiceTransactionLog transactionLog = new WebServiceTransactionLog();
		transactionLog.setServiceType(WebServiceTransactionLog.SERVICE_TYPE_CLIENT);
		transactionLog.setServiceSide("ERP");
		transactionLog.setClientSide("PLM");
		transactionLog.setClientId("");
		transactionLog.setClientClass(maturityNFAEPort.getClass().getName());
		transactionLog.setClientMethod("siZEIVRCreateOut");
		transactionLog.setStartTime(new Timestamp(System.currentTimeMillis()));

		DTZEIVRCreateRequest requets = new DTZEIVRCreateRequest();
		requets.setSYSNAME(company);
		requets.getTZEIVR();
		List<TZEIVR> list = requets.getTZEIVR();
		for (String[] info : infos) {
			TZEIVR maturity = new TZEIVR();
			maturity.setMATNR(info[0]);
			maturity.setZEIVR(info[1]);
			list.add(maturity);
		}
		
		
		// 记录交易日志的结果信息的信息
		WebServiceTransactionInfo transactionInfo = new WebServiceTransactionInfo();
		transactionLog.setTransactionInfo(transactionInfo);
		transactionInfo.setParameterObject(requets);
		
		try {
			// 调用Web Service
			DTZEIVRCreateResponse result = maturityNFAEPort.siZEIVRCreateOut(requets);
			
			//记录Web Service调用的结果信息
			transactionLog.setFinishTime(new Timestamp(System.currentTimeMillis()));
			transactionInfo.setResultObject(result);
			transactionLog.setStatus(WebServiceTransactionLog.STATUS_SUCCESS);
			
			return result;
		} catch (Exception e) {
			// 记录Web Service发生异常时的信息
			transactionLog.setFinishTime(new Timestamp(System.currentTimeMillis()));
			transactionLog.setStatus(WebServiceTransactionLog.STATUS_FAILED);
			transactionInfo.setException(e);
			throw e;
		} finally {
			TransactionLogHelper.logTransaction(transactionLog);
		}
	}
	
	
	
	public DTBESKZCreateResponse sendSourceChange(String company, List<SourceChangeXmlObjectBean> sourceChangeList, String successPart) throws Exception {
		//准备交易日志所需数据
		WebServiceTransactionLog transactionLog = new WebServiceTransactionLog();
		transactionLog.setServiceType(WebServiceTransactionLog.SERVICE_TYPE_CLIENT);
		transactionLog.setServiceSide("ERP");
		transactionLog.setClientSide("PLM");
		transactionLog.setClientId("sendSourceChange");
		transactionLog.setClientClass(sourceChangePort.getClass().getName());
		transactionLog.setClientMethod("sendSourceChange");
		transactionLog.setStartTime(new Timestamp(System.currentTimeMillis()));
		
		WebServiceTransactionInfo transactionInfo = new WebServiceTransactionInfo();
		try {
			DTBESKZCreateRequest requets = new DTBESKZCreateRequest();
			requets.setSYSNAME(company);
			List<TBESKZ> list = requets.getTBESKZ();
			for (SourceChangeXmlObjectBean sourceChange : sourceChangeList) {
				String partNumber = sourceChange.getPartNumber();
				if(!successPart.contains(partNumber)){//成功的不用发
					String sourceAfter = sourceChange.getSourceAfter();
					ReferenceFactory rf = new ReferenceFactory();
					Persistable obj = rf.getReference("VR:wt.part.WTPart:"+sourceChange.getPartBranchId()).getObject();
					WTPart part = (WTPart)obj;
					String faeStatus = RefreshFAEStatusUtil.getInitialFAEStatusValueChange(part, sourceAfter);
					
					TBESKZ beskz = new TBESKZ();
					beskz.setMATNR(partNumber);
					beskz.setBESKZ(ReleaseUtil.changeSource(sourceAfter));
					beskz.setZFAE(faeStatus);
					
					list.add(beskz);
				}
			}
			// 记录交易日志的结果信息的信息
			transactionLog.setTransactionInfo(transactionInfo);
			transactionInfo.setParameterObject(requets);
			
			// 调用Web Service
			DTBESKZCreateResponse result = sourceChangePort.siBESKZCreateOut(requets);
			
			//记录Web Service调用的结果信息
			transactionLog.setFinishTime(new Timestamp(System.currentTimeMillis()));
			transactionInfo.setResultObject(result);
			transactionLog.setStatus(WebServiceTransactionLog.STATUS_SUCCESS);
			
			return result;
		} catch (Exception e) {
			// 记录Web Service发生异常时的信息
			transactionLog.setFinishTime(new Timestamp(System.currentTimeMillis()));
			transactionLog.setStatus(WebServiceTransactionLog.STATUS_FAILED);
			//transactionInfo.setException(e);
			throw e;
		} finally {
			TransactionLogHelper.logTransaction(transactionLog);
		}
	}
	
	public String secdOutECN(WTObject primaryBusinessObject) throws ChangeException2, WTException{
		String success = "";
		WebServiceTransactionLog transactionLog = new WebServiceTransactionLog();
		transactionLog.setServiceType(WebServiceTransactionLog.SERVICE_TYPE_CLIENT);
		transactionLog.setServiceSide("ERP");
		transactionLog.setClientSide("PLM");
		transactionLog.setClientId("");
		transactionLog.setClientClass(ecndatesendPort.getClass().getName());
		transactionLog.setClientMethod("secdOutECN");
		transactionLog.setStartTime(new Timestamp(System.currentTimeMillis()));
		
		Map<String, Vector<String>> map =  getChangeablesBeforeObject(primaryBusinessObject);
		 for (Map.Entry<String, Vector<String>> entry : map.entrySet()) {
			 String ecnNumber = entry.getKey();
			 Vector<String> vector = entry.getValue();
			for (int i = 0; i < vector.size(); i++) {
			//设置传送数据
			DTEcndateRequest.TECN tecn = new DTEcndateRequest.TECN();
			tecn.setAENNR(ecnNumber);
			tecn.setMATNR(vector.get(i));
			tecn.setResult("1");
			DTEcndateRequest request = new DTEcndateRequest();
			request.getTECN().add(tecn);
			
			DTEcndateResponse response = ecndatesendPort.siEcndateSend(request);
			List<com.catl.integration.pi.ecn.erp.DTEcndateResponse.TRETURN> list  = response.getTRETURN();
			for (com.catl.integration.pi.ecn.erp.DTEcndateResponse.TRETURN treturn : list) {
				if (treturn.getType().equals("E")) {
					success = treturn.getMessage();
					return success;
				}
			}
			}
		 }
		 return "success";
	}
	/**
	 * 获取ECN/ECA 受影响对象
	 * 
	 * @param primaryBusinessObject
	 * @return
	 * @throws WTException
	 * @throws ChangeException2
	 * @modified: qgcai(2017年9月22日): <br>
	 */
	public Map<String, Vector<String>> getChangeablesBeforeObject(WTObject primaryBusinessObject)
			throws ChangeException2, WTException {
		WTObject obj = null;
		WTChangeOrder2 ecn = null;
		WTChangeActivity2 eca = null;
		QueryResult queryresult = null;
		Vector<String> vector = new Vector<String>();
		Map<String,Vector<String>> map = new HashMap<String, Vector<String>>();

		if (primaryBusinessObject instanceof WTChangeOrder2) {
			ecn = (WTChangeOrder2) primaryBusinessObject;
			QueryResult qrCA = ChangeHelper2.service.getChangeActivities(ecn);
			while (qrCA.hasMoreElements()) {
				eca = (WTChangeActivity2) qrCA.nextElement();
				String name = (String) GenericUtil.getObjectAttributeValue(eca, "name");
				if (name.equals("研发更改任务")) {
					queryresult = ChangeHelper2.service.getChangeablesBefore(eca);
					while (queryresult.hasMoreElements()) {
						obj = (WTObject) queryresult.nextElement();
						if (obj instanceof WTPart) {
							vector.add(((WTPart) obj).getNumber());
						}
						
					}
					
				}
				
			}
			map.put(ecn.getNumber(), vector);
			
		} 
		return map;
	}

}
