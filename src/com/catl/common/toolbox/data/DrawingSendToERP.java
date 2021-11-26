package com.catl.common.toolbox.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.rmi.RemoteException;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;



import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.solr.common.util.Hash;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTList;
import wt.httpgw.GatewayAuthenticator;
import wt.iba.value.IBAHolder;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleState;
import wt.lifecycle.State;
import wt.maturity.PromotionNotice;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.query.ArrayExpression;
import wt.query.ClassAttribute;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.vc.config.LatestConfigSpec;

import com.catl.common.constant.ContainerName;
import com.catl.common.constant.DocState;
import com.catl.common.constant.TypeName;
import com.catl.common.util.GenericUtil;
import com.catl.common.util.PartUtil;
import com.catl.integration.DrawingInfo;
import com.catl.integration.DrawingSendERP;
import com.catl.integration.ErpResponse;
import com.catl.integration.Message;
import com.catl.integration.PIService;
import com.catl.integration.PartInfo;
import com.catl.loadData.Constant;
import com.catl.loadData.IBAUtility;
import com.catl.loadData.StrUtils;
import com.catl.loadData.util.ExcelReader;
import com.catl.loadData.util.ExcelWriter;
import com.catl.test.TestMain;
import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.common.UpdateOperationIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;

public class DrawingSendToERP implements RemoteAccess{
   
    public static void main(String[] args) {

        RemoteMethodServer rms = RemoteMethodServer.getDefault();
        GatewayAuthenticator auth = new GatewayAuthenticator();
        auth.setRemoteUser("wcadmin");
        rms.setAuthenticator(auth);

        if(args.length<1){
            System.out.println("Example Usage:" + 
                    "java com.catl.common.toolbox.data.DrawingSendToERP path");
        }
        System.out.println(args[0]);
        invokeRemoteLoad(args[0]);
    }

    public static void invokeRemoteLoad(String path){
        String method = "doLoad";
        String CLASSNAME= DrawingSendToERP.class.getName();
        Class[] types = {String.class};
        Object[] values={path};
        try {
            RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void doLoad(String path){
    	System.out.println("DrawingSendToERP start");
    	
    	FileWriter file = null;
		BufferedWriter writer = null;
        try {
        	WTProperties wtproperties = WTProperties.getLocalProperties();
    		String homePath = wtproperties.getProperty("wt.home");
        	String logPath = homePath + "/logs/";
        	
        	Format format = new SimpleDateFormat("yyyyMMddHHmmss");
			String nowTime = format.format(new Date());

			file = new FileWriter(logPath + "DrawingSendToERP_" + nowTime + ".log");
			writer = new BufferedWriter(file);

			writer.write("开始获取所有部件！\n");
			
			ExcelReader xlsReader = new ExcelReader(new File(path));
			xlsReader.open();
			int count = xlsReader.getRowCount(0);
			
			Map<String, DrawingSendERP> map = new HashMap<String, DrawingSendERP>();//发送ERP的图纸
    		WTList logList = new WTArrayList();//存放DrawingSendERPLog类型日志
    		String oid = "oldDrawingHandle";
    		List<DrawingInfo> list = new ArrayList<DrawingInfo>();
    		
			for(int i=1; i<=count; i++){
				String[] line = xlsReader.readExcelLine(i);
				String partNumber = line[0];
				writer.write(partNumber+"\n");
				WTPart part = PartUtil.getLastestWTPartByNumber(partNumber);
				
				com.catl.integration.ReleaseUtil.copyDrawing(part, map, logList, oid);
        		list.add(initDrawingInfo(part,"手动补充图纸"));
			}
        	Thread.sleep(10000);//图纸保存服务器 10秒后在发送ERP
			PersistenceHelper.manager.save(logList);
			
			writer.write("发送图纸信息返回消息：\n");
			
			ErpResponse response = com.catl.integration.PIService.getInstance().sendDrawing(list, com.catl.common.constant.Constant.COMPANY);
			if(!response.isSuccess()){
				List<Message> msgs = response.getMessage();
				for(Message msg : msgs){
					if(!msg.isSuccess())
						writer.write(msg.getText()+"\n");
				}
			}
			String ret = PIService.getInstance().sendFiles(map.values(), com.catl.common.constant.Constant.COMPANY);
			writer.write("\n发送实际图纸返回消息：\n"+ret);
			writer.flush();
			writer.close();
			
        }catch (Exception e) {
        	e.printStackTrace();
        }
        System.out.println("DrawingSendToERP end");
    }

    public static boolean isEmpty(String str){
        if(str == null || str.trim().equals("") || str.equals("null"))
            return true;
        return false;
    }

    private static DrawingInfo initDrawingInfo(WTPart part, String oid) throws ParseException, WTException, Exception {

    	DrawingInfo info = new DrawingInfo();
    	info.setPartNumber(part.getNumber());

		QueryResult cadresult = PartDocServiceCommand.getAssociatedCADDocuments(part);
		cadresult = new LatestConfigSpec().process(cadresult);
		while (cadresult.hasMoreElements()) {
			EPMDocument epmdoc = (EPMDocument) cadresult.nextElement();
			Boolean iscaddrawing = epmdoc.getDocType().toString().equals("CADDRAWING");
			if (iscaddrawing) {
				if (epmdoc.getState().toString().equals(DocState.RELEASED)) {
					info.setDrawingNumber(epmdoc.getNumber());
					info.setDrawingVersion(epmdoc.getVersionIdentifier().getValue());
					break;
				}
			}

		}
		if (info.getDrawingNumber() == null) {// 没有CAD图纸
			QueryResult docresult = PartDocServiceCommand.getAssociatedDescribeDocuments(part);
			docresult = new LatestConfigSpec().process(docresult);
			while (docresult.hasMoreElements()) {// 取第一个 PCBA、AUTO、GERBER文档
				WTDocument doc = (WTDocument) docresult.nextElement();
				TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(doc);
				String type = ti.getTypename();
				boolean isNeedset = type.contains(TypeName.doc_type_pcbaDrawing) || type.contains(TypeName.doc_type_autocadDrawing) || type.contains(TypeName.doc_type_gerberDoc);
				if (isNeedset) {
					if (doc.getState().toString().equals(DocState.RELEASED)) {
						info.setDrawingNumber(doc.getNumber());
						info.setDrawingVersion(doc.getVersionIdentifier().getValue());
						break;
					}
				}
			}
		}
		if (info.getDrawingNumber() == null) {// 没有PCBA、AUTO、GERBER文档
			QueryResult qr = ContentHelper.service.getContentsByRole(part, ContentRoleType.SECONDARY);
			while (qr.hasMoreElements()) {
				ApplicationData fileContent = (ApplicationData) qr.nextElement();
				String strFileName = URLDecoder.decode(fileContent.getFileName(), "utf-8");
				if (strFileName.toUpperCase().equals(part.getNumber() + ".PDF")) {
					info.setDrawingNumber(part.getNumber());
					info.setDrawingVersion("A");
					break;
				}
			}
		}
		return info;
	}

}
