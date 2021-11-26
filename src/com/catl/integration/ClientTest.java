package com.catl.integration;

import java.util.ArrayList;
import java.util.List;
import java.net.Authenticator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;

import org.apache.log4j.Logger;
import org.jfree.util.Log;

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
import com.catl.loadData.StrUtils;
import com.sun.xml.ws.client.BindingProviderProperties;

/**
 * Test4 PI interface
 * 
 * @author ZhengJiaH
 *
 */

public class ClientTest extends Thread{
    
	public static void main(String[] args) {
		ClientTest test = new ClientTest();
		test.start();
	}
	@Override
	public void run(){
		sendECNTest("2025","2026");
		//sendPartTest("101010-00003","101010-00004");
		//sendBomTest("11062-0005","13170-1067","13170-1068",1);
		
		//sendPartChangeTest("101010-00003","101010-00004","2026");
		//sendBomChangeTest("110621-0005","131701-1067","131702-1068",1,"2025");
		//sendDrawingTest("1111", "2222");
	}
	public void sendBomTest(String parent,String child,String stitute,double num){
		List<BomInfo> list = new ArrayList<BomInfo>();
		BomInfo bom = new BomInfo();
		bom.setParentPartNumber(parent);
		bom.setChildPartNumber(child);
		bom.setSubstitutePartNumber(stitute);
		bom.setQuantity(num);
		list.add(bom);
		
		bom = new BomInfo();
		bom.setParentPartNumber("11062-0005");
		bom.setChildPartNumber("13170-1071");
		bom.setSubstitutePartNumber("13170-1072");
		bom.setQuantity(2);
		list.add(bom);
		
		ErpResponse response;
        try {
            response = sendBoms(list,"CATL");
            System.out.println(response.isSuccess());
            for(Message message : response.getMessage()){
                System.out.println(message.getNumber()+","+message.getChildNumber()+","+message.getStituteNumber()+","+message.isSuccess()+","+message.getText());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		
	}
	
	public void sendBomChangeTest(String parent,String child,String stitute,double num,String ecnNumber){
		List<BomInfo> list = new ArrayList<BomInfo>();
		BomInfo bom = new BomInfo();
		bom.setParentPartNumber(parent);
		bom.setChildPartNumber(child);
		bom.setSubstitutePartNumber(stitute);
		bom.setQuantity(num);
		bom.setEcnNumber(ecnNumber);
		list.add(bom);
		
		ErpResponse response;
        try {
            response = sendBomsChange(list,"CATL");
            System.out.println(response.isSuccess());
            for(Message message : response.getMessage()){
                System.out.println(message.getNumber()+","+message.getChildNumber()+","+message.getStituteNumber()+","+message.isSuccess()+","+message.getText());
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		
		
	}
	
	public void sendPartTest(String partNumber1,String partNumber2){
		List<PartInfo> list = new ArrayList<PartInfo>();
		PartInfo part = new PartInfo();
		part.setPartNumber(partNumber1);
		part.setPartName("partName");
		part.setCreator("dms");
		part.setDefaultUnit("PCS");
		part.setMaterialGroup("2030");
		part.setSource("E");
		part.setSpecification("specification");
		part.setDrawingVersion("1.1");
		list.add(part);
		
		part = new PartInfo();
		part.setPartNumber(partNumber2);
		part.setPartName("partName2");
		part.setCreator("dms");
		part.setDefaultUnit("PCS");
		part.setMaterialGroup("2030");
		part.setSource("F");
		part.setSpecification("specification2");
		part.setDrawingVersion("1.1");
		list.add(part);
		
		ErpResponse response;
        try {
            response = sendParts(list,"CATL");
            System.out.println(response.isSuccess());
            for(Message message : response.getMessage()){
                System.out.println(message.getNumber()+","+message.isSuccess()+","+message.getText());
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		
		
	}
	
	public void sendPartChangeTest(String partNumber1,String partNumber2,String ecnNumber){
		List<PartInfo> list = new ArrayList<PartInfo>();
		PartInfo part = new PartInfo();
		part.setPartNumber(partNumber1);
		part.setPartName("partName");
		part.setCreator("dms");
		part.setDefaultUnit("PCS");
		part.setMaterialGroup("2030");
		part.setSource("E");
		part.setSpecification("specification");
		part.setDrawingVersion("1.1");
		part.setEcnNumber(ecnNumber);
		list.add(part);
		
		part = new PartInfo();
		part.setPartNumber(partNumber2);
		part.setPartName("partName2");
		part.setCreator("dms");
		part.setDefaultUnit("PCS");
		part.setMaterialGroup("2030");
		part.setSource("F");
		part.setSpecification("specification2");
		part.setDrawingVersion("1.1");
		part.setEcnNumber(ecnNumber);
		list.add(part);
		
		ErpResponse response;
        try {
            response = sendPartsChange(list,"CATL");
            System.out.println(response.isSuccess());
            for(Message message : response.getMessage()){
                System.out.println(message.getNumber()+","+message.isSuccess()+","+message.getText());
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		
	}
	
	public void sendECNTest(String ecnNumber1,String ecnNumber2){
		List<EcInfo> ecns = new ArrayList<EcInfo>();
		EcInfo ecn = new EcInfo();
		ecn.setName("aegru"); 
		ecn.setNumber(ecnNumber1);
		ecn.setDescription("aetxt");
		ecn.setValidDate("20151013");
		ecns.add(ecn);
		EcInfo ecn2 = new EcInfo();
		ecn2.setName("aegru2");
		ecn2.setNumber(ecnNumber2);
		ecn2.setDescription("aetxt2");
		ecn2.setValidDate("20151013");
		ecns.add(ecn2);
		
		ErpResponse response;
        try {
            response = sendECN(ecns,"CATL");
            System.out.println(response.isSuccess());
            for(Message message : response.getMessage()){
                System.out.println(message.getNumber()+","+message.isSuccess()+","+message.getText());
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		
		
	}
	
	public void sendDrawingTest(String partNumber1,String partNumber2){
		List<DrawingInfo> infos = new ArrayList<DrawingInfo>();
		DrawingInfo info = new DrawingInfo();
		info.setPartNumber(partNumber1);
		info.setDrawingNumber("12345");
		info.setDrawingVersion("A");
		infos.add(info);
		
		DrawingInfo info2 = new DrawingInfo();
		info2.setPartNumber(partNumber2);
		info2.setDrawingNumber("67890");
		info2.setDrawingVersion("B");
		infos.add(info2);
		
		ErpResponse response;
        try {
            response = sendDrawing(infos,"CATL");
            System.out.println(response.isSuccess());
            for(Message message : response.getMessage()){
                System.out.println(message.getNumber()+","+message.isSuccess()+","+message.getText());
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		
		
	}

	
	
	
	
	
	
	
	
	
	private static Logger log = Logger.getLogger(PIService.class.getName());
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
    
    public ClientTest(){
        String username = "PLM_PD3_001";
        String password = "plm-pd3001";
        NtlmAuthenticator authenticator = new NtlmAuthenticator(username,
                password);
        Authenticator.setDefault(authenticator);
        
        ecnService = new SIEcnCreateOutService();
        ecnport = ecnService.getHTTPPort();
        Map<String, Object> ctxt = ((BindingProvider) ecnport).getRequestContext();
        ctxt.put(BindingProviderProperties.REQUEST_TIMEOUT, 10*60*1000);//请求时间10分钟
        ctxt.put(BindingProviderProperties.CONNECT_TIMEOUT, 10*60*1000);//连接时间10分钟
        
        partService = new SIMATNRCreateOutService();
        partPort = partService.getHTTPPort();
        ctxt = ((BindingProvider) partPort).getRequestContext();
        ctxt.put(BindingProviderProperties.REQUEST_TIMEOUT, 10*60*1000);//请求时间10分钟
        ctxt.put(BindingProviderProperties.CONNECT_TIMEOUT, 10*60*1000);//连接时间10分钟
        
        bomService = new SIBOMCreateCreateOutService();
        bomPort = bomService.getHTTPPort();
        ctxt = ((BindingProvider) bomPort).getRequestContext();
        ctxt.put(BindingProviderProperties.REQUEST_TIMEOUT, 10*60*1000);//请求时间10分钟
        ctxt.put(BindingProviderProperties.CONNECT_TIMEOUT, 10*60*1000);//连接时间10分钟
        
        partChangeService = new SIPNCHANGECreateOutService();
        partChangePort = partChangeService.getHTTPPort();
        ctxt = ((BindingProvider) bomPort).getRequestContext();
        ctxt.put(BindingProviderProperties.REQUEST_TIMEOUT, 10*60*1000);//请求时间10分钟
        ctxt.put(BindingProviderProperties.CONNECT_TIMEOUT, 10*60*1000);//连接时间10分钟
        
        bomChangeService = new SIBOMCHANGECreateOutService();
        bomChangePort = bomChangeService.getHTTPPort();
        ctxt = ((BindingProvider) bomChangePort).getRequestContext();
        ctxt.put(BindingProviderProperties.REQUEST_TIMEOUT, 10*60*1000);//请求时间10分钟
        ctxt.put(BindingProviderProperties.CONNECT_TIMEOUT, 10*60*1000);//连接时间10分钟
        
        drawingService = new SIZEINRCreateOutService();
        drawingPort = drawingService.getHTTPPort();
        ctxt = ((BindingProvider) drawingPort).getRequestContext();
        ctxt.put(BindingProviderProperties.REQUEST_TIMEOUT, 10*60*1000);//请求时间10分钟
        ctxt.put(BindingProviderProperties.CONNECT_TIMEOUT, 10*60*1000);//连接时间10分钟
    }
    /**
     * 发布ECN
     * @param ecns
     * @param company   default CATL
     * @return
     */
    public ErpResponse sendECN(List<EcInfo> ecns,String company) throws Exception{
        if(ecns == null)
            throw new NullPointerException("参数为null");
        if(ecns.size()==0)
            throw new IllegalArgumentException("参数为空,size=0");
        
        DTEcnCreateRequest request = new DTEcnCreateRequest();
        request.setSYSNAME(company);
        List<TECN> list = request.getTECN();
        TECN tecn = null;
        for(EcInfo ecn : ecns){
            log.debug("sendECN ecnNumber:"+ecn.getNumber()+",validDate:"+ecn.getValidDate());
            tecn = new TECN();
            tecn.setAENNR(ecn.getNumber());
            tecn.setAEGRU(ecn.getDescription());//
            tecn.setAETXT(ecn.getName());
            tecn.setDATUV(ecn.getValidDate());
            list.add(tecn);
        }
        DTEcnCreateResponse response = ecnport.siEcnCreateOut(request);
        
        Map<String,Message> map = new HashMap<String,Message>();
        for(TECNR ecnr : response.getTECNR()){
            
            String status = ecnr.getSTATUS();
            String number = ecnr.getAENNR();
            String text = ecnr.getMESSAGE();
            
            Message message = map.get(number);
            if(message == null){
                message = new Message();
                message.setNumber(number);
                message.setSuccess(status.equals("S")?true:false);
                message.setText(text);
                map.put(number, message);
            }else{
                message.setText(message.getText()+"|"+text);
                map.put(number, message);
            }
        }
        log.debug("sendECN isSuccess:"+(response.getEACKNOW().getResult().equals("S")?"true":"false"));
        ErpResponse erpResponse = new ErpResponse();
        erpResponse.setSuccess(response.getEACKNOW().getResult().equals("S")?true:false);
        erpResponse.setMessage(new ArrayList<Message>(map.values()));
        
        return erpResponse;
    }
    /**
     * 发送创建Part
     * @param parts
     * @param company   default CATL
     * @return
     */
    public ErpResponse sendParts(List<PartInfo> parts,String company) throws Exception{
        
        DTMATNRCreateRequest request = new DTMATNRCreateRequest();
        request.setSYSNAME(company);
        List<TMATNR> list = request.getTMATNR();
        TMATNR matnr;
        for(PartInfo part : parts){
            log.debug("sendParts partNumber:"+part.getPartNumber()+",ecnNumber:"+part.getEcnNumber()+",quantity:"+part.getDefaultUnit()+",group:"+part.getMaterialGroup()+",source:"+part.getSource());
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
            matnr.setZEINR(part.getDrawing());//图纸
            matnr.setZMCDY(part.getFullVoltage());
            matnr.setZMODULE(part.getModel());
            matnr.setZZWLGG(StrUtils.isEmpty(part.getSpecification())?"无":part.getSpecification());
            list.add(matnr);
        }
        DTMATNRCreateResponse response = partPort.siMATNRCreateOut(request);
        
        Map<String,Message> map = new HashMap<String,Message>();
        for(TRETURN ret : response.getTRETURN()){
            
            String status = ret.getSTATUS();
            String number = ret.getMATNR();
            String text = ret.getMESSAGE();
            
            Message message = map.get(number);
            if(message == null){
                message = new Message();
                message.setNumber(number);
                message.setSuccess(status.equals("S")?true:false);
                message.setText(text);
                map.put(number, message);
            }else{
                message.setText(message.getText()+"|"+text);
                map.put(number, message);
            }
        }
        log.debug("sendParts isSuccess:"+(response.getEACKNOW().getResult().equals("S")?"true":"false"));
        ErpResponse erpResponse = new ErpResponse();
        erpResponse.setSuccess(response.getEACKNOW().getResult().equals("S")?true:false);
        erpResponse.setMessage(new ArrayList<Message>(map.values()));
        
        return erpResponse;
        
    }
    /**
     * 发送创建Bom
     * @param boms
     * @param company
     * @return
     */
    public ErpResponse sendBoms(List<BomInfo> boms,String company) throws Exception{
        DTBomCreateCreateRequest request = new DTBomCreateCreateRequest();
        request.setSYSNAME(company);
        List<TBOMIN> list = request.getTBOMIN();
        TBOMIN bomin;
        for(BomInfo bom : boms){
            log.debug("sendBoms bomNumber:"+bom.getParentPartNumber()+",ecnNumber:"+bom.getEcnNumber()+",childPartNumber:"+bom.getChildPartNumber()+",subNumber:"+bom.getSubstitutePartNumber()+",quantity:"+bom.getQuantity());
            bomin = new TBOMIN();
            bomin.setMATNR(bom.getParentPartNumber());
            bomin.setIDNRK(bom.getChildPartNumber());
            bomin.setZTDAI(bom.getSubstitutePartNumber());
            bomin.setMENGE(bom.getQuantity()+"");
            list.add(bomin);
        }
        DTBOMCreateCreateResponse response = bomPort.siBOMCreateCreateOut(request);
        
        Map<String,Message> map = new HashMap<String,Message>();
        for(TBOMOUT ret : response.getTBOMOUT()){
            
            String status = ret.getSTATUS();
            String parent = ret.getMATNR();
            String text = ret.getMESSAGE();
            String stitute = ret.getZTDAI();
            String child =  ret.getIDNRK();
            
            Message message = map.get(parent+","+stitute+","+child);
            if(message == null){
                message = new Message();
                message.setNumber(parent);
                message.setChildNumber(child);
                message.setStituteNumber(stitute);
                message.setSuccess(status.equals("S")?true:false);
                message.setText(text);
                map.put(parent+","+stitute+","+child, message);
            }else{
                message.setText(message.getText()+"|"+text);
                map.put(parent+","+stitute+","+child, message);
            }
        }
        log.debug("sendBoms isSuccess:"+(response.getEACKNOW().getResult().equals("S")?"true":"false"));
        ErpResponse erpResponse = new ErpResponse();
        erpResponse.setSuccess(response.getEACKNOW().getResult().equals("S")?true:false);
        erpResponse.setMessage(new ArrayList<Message>(map.values()));
        
        return erpResponse;
        
    }
    
    /**
     * 发送修改Part
     * @param parts
     * @param company   default CATL
     * @return
     */
    public ErpResponse sendPartsChange(List<PartInfo> parts,String company) throws Exception{
        
        DTPNCHANGECreateRequest request = new DTPNCHANGECreateRequest();
        request.setSYSNAME(company);
        List<TPN> list = request.getTPN();
        TPN tpn;
        for(PartInfo part : parts){
            log.debug("sendPartsChange partNumber:"+part.getPartNumber()+",ecnNumber:"+part.getEcnNumber()+",quantity:"+part.getDefaultUnit()+",group:"+part.getMaterialGroup());
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
            tpn.setZEINR(part.getDrawing());//图纸
            tpn.setZMCDY(part.getFullVoltage());
            tpn.setZMODULE(part.getModel());
            tpn.setZZWLGG(StrUtils.isEmpty(part.getSpecification())?"无":part.getSpecification());
            tpn.setAENNR(part.getEcnNumber());
            
            list.add(tpn);
        }
        DTPNCHANGECreateResponse response = partChangePort.siPNCHANGECreateOut(request);
        
        Map<String,Message> map = new HashMap<String,Message>();
        for(com.catl.integration.pi.part.change.DTPNCHANGECreateResponse.TRETURN ret : response.getTRETURN()){
            
            String status = ret.getSTATUS();
            String number = ret.getMATNR();
            String text = ret.getMESSAGE();
            
            Message message = map.get(number);
            if(message == null){
                message = new Message();
                message.setNumber(number);
                message.setSuccess(status.equals("S")?true:false);
                message.setText(text);
                map.put(number, message);
            }else{
                message.setText(message.getText()+"|"+text);
                map.put(number, message);
            }
        }
        log.debug("sendPartsChange isSuccess:"+(response.getEACKNOW().getResult().equals("S")?"true":"false"));
        ErpResponse erpResponse = new ErpResponse();
        erpResponse.setSuccess(response.getEACKNOW().getResult().equals("S")?true:false);
        erpResponse.setMessage(new ArrayList<Message>(map.values()));
        
        return erpResponse;
        
    }
    
    public ErpResponse sendBomsChange(List<BomInfo> boms,String company) throws Exception{
        DTBOMCHANGECreateRequest request = new DTBOMCHANGECreateRequest();
        request.setSYSNAME(company);
        List<com.catl.integration.pi.bom.change.DTBOMCHANGECreateRequest.TBOMIN> list = request.getTBOMIN();
        com.catl.integration.pi.bom.change.DTBOMCHANGECreateRequest.TBOMIN bomin;
        for(BomInfo bom : boms){
            log.debug("sendBomsChange bomNumber:"+bom.getParentPartNumber()+",ecnNumber:"+bom.getEcnNumber()+",childPartNumber:"+bom.getChildPartNumber()+",subNumber:"+bom.getSubstitutePartNumber()+",quantity:"+bom.getQuantity());
            bomin = new com.catl.integration.pi.bom.change.DTBOMCHANGECreateRequest.TBOMIN();
            bomin.setMATNR(bom.getParentPartNumber());
            bomin.setIDNRK(bom.getChildPartNumber());
            bomin.setZTDAI(bom.getSubstitutePartNumber());
            bomin.setMENGE(bom.getQuantity()+"");
            bomin.setAENNR(bom.getEcnNumber());
            list.add(bomin);
        }
        DTBOMCHANGECreateResponse response = bomChangePort.siBOMCHANGECreateOut(request);
        
        Map<String,Message> map = new HashMap<String,Message>();
        for(com.catl.integration.pi.bom.change.DTBOMCHANGECreateResponse.TBOMOUT ret : response.getTBOMOUT()){
            
            String status = ret.getSTATUS();
            String parent = ret.getMATNR();
            String text = ret.getMESSAGE();
            String stitute = ret.getZTDAI();
            String child =  ret.getIDNRK();
            
            Message message = map.get(parent+","+stitute+","+child);
            if(message == null){
                message = new Message();
                message.setNumber(parent);
                message.setChildNumber(child);
                message.setStituteNumber(stitute);
                message.setSuccess(status.equals("S")?true:false);
                message.setText(text);
                map.put(parent+","+stitute+","+child, message);
            }else{
                message.setText(message.getText()+"|"+text);
                map.put(parent+","+stitute+","+child, message);
            }
        }
        log.debug("sendBomsChange isSuccess:"+(response.getEACKNOW().getResult().equals("S")?"true":"false"));
        ErpResponse erpResponse = new ErpResponse();
        erpResponse.setSuccess(response.getEACKNOW().getResult().equals("S")?true:false);
        erpResponse.setMessage(new ArrayList<Message>(map.values()));
        
        return erpResponse;
        
    }
    
    public ErpResponse sendDrawing(List<DrawingInfo> drawings,String company) throws Exception{
        DTZEINRCreateRequest request = new DTZEINRCreateRequest();
        request.setSYSNAME(company);
        List<TZEINR> list = request.getTZEINR();
        TZEINR drawingin;
        for(DrawingInfo drawing : drawings){
            log.debug("sendDrawing partNumber:"+drawing.getPartNumber()+",drawingNumber:"+drawing.getDrawingNumber()+",drawingVersion:"+drawing.getDrawingVersion());
            drawingin = new TZEINR();
            drawingin.setMATNR(drawing.getPartNumber());
            drawingin.setZEINR(drawing.getDrawingNumber());
            drawingin.setZEIAR(drawing.getDrawingVersion());
            list.add(drawingin);
        }
        DTZEINRCreateResponse response = drawingPort.siZEINRCreateOut(request);
        
        Map<String,Message> map = new HashMap<String,Message>();
        for(com.catl.integration.pi.drawing.DTZEINRCreateResponse.TRETURN ret : response.getTRETURN()){
            
            String status = ret.getSTATUS();
            String parent = ret.getMATNR();
            String text = ret.getMESSAGE();
            
            Message message = map.get(parent);
            if(message == null){
                message = new Message();
                message.setNumber(parent);
                message.setSuccess(status.equals("S")?true:false);
                message.setText(text);
                map.put(parent, message);
            }else{
                message.setText(message.getText()+"|"+text);
                map.put(parent, message);
            }
        }
        log.debug("sendDrawing isSuccess:"+(response.getEACKNOW().getResult().equals("S")?"true":"false"));
        for(Message msg : map.values()){
            log.debug("sendDrawing number:"+msg.getNumber()+",text:"+msg.getText());
        }
        ErpResponse erpResponse = new ErpResponse();
        erpResponse.setSuccess(response.getEACKNOW().getResult().equals("S")?true:false);
        erpResponse.setMessage(new ArrayList<Message>(map.values()));
        
        return erpResponse;
        
    }
}
