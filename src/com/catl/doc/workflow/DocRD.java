package com.catl.doc.workflow;


import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.catl.common.util.CatlConstant;
import com.catl.common.util.GenericUtil;
import com.catl.loadData.IBAUtility;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;

import wt.doc.WTDocument;
import wt.doc.WTDocumentHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.applicationcontext.implementation.ServiceProperties;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;

public class DocRD implements RemoteAccess {
	public static void main(String[] args) throws Exception {
		RemoteMethodServer rm = RemoteMethodServer.getDefault();
		rm.setUserName("wcadmin");
		rm.setPassword("wcadmin");
		rm.invoke("Test", DocRD.class.getName(), null, null, null); // 远程调用
	}
	/**远程调用测试方法
	 * 
	 * @throws Exception
	 */
	public static void Test() throws Exception {
		
		updataPart(getWTDocumentByNumber("SPRR-00000004"));
		
	}
	
	
	public static void updataPart(WTDocument doc) throws Exception{
		Properties wtproperties = ServiceProperties.getServiceProperties("WTServiceProviderFromProperties");
		String PartGroup = wtproperties.getProperty("PartGroup");
		
		String docSubName = (String) GenericUtil.getObjectAttributeValue(doc, "subCategory");
		if (StringUtils.isNotEmpty(docSubName) && docSubName.toUpperCase().endsWith("-SPRR")){
			List<WTPart> list =	getLastestPart(doc);
			for (int i = 0; i < list.size(); i++) {
				WTPart part = list.get(i);
				String partNumber = part.getNumber().split("-")[0];
				if (PartGroup.indexOf(partNumber) != -1) {
					docAfterWfOpeGrade(part);
				}
				
			}						
		}
	
	}
	
	/**
	 * 通过文档获取文档的参考部件
	 * @param doc
	 * @return
	 * @throws WTException
	 */
	public static List<WTPart> getLastestPart(WTDocument doc) throws WTException{
		List<WTPart> list = new ArrayList<WTPart>();
		QueryResult qr = PartDocServiceCommand.getAssociatedParts(doc);
		LatestConfigSpec cfg = new LatestConfigSpec();
		 qr = cfg.process(qr);
		while (qr.hasMoreElements()) {
			Object object = (Object) qr.nextElement();
			if (object instanceof WTPart) {
				WTPart  part = (WTPart) object;
				list.add(part);
			}
		} 
		return list;
	} 
	
	/**
	 * 通过文档编码获取最新文档
	 * @param number
	 * @return
	 * @throws WTException
	 */
	public static WTDocument getWTDocumentByNumber(String number) throws WTException {
		WTDocument wt = null;
		QuerySpec qs = new QuerySpec(WTDocument.class);
		SearchCondition sc = new SearchCondition(WTDocument.class, WTDocument.NUMBER,
				"=", number);
		qs.appendWhere(sc);
		LatestConfigSpec lcs = new LatestConfigSpec();
		QueryResult qr = PersistenceHelper.manager.find(qs);
		qr = lcs.process(qr);
		while (qr.hasMoreElements()) {
			wt = (WTDocument) qr.nextElement();
			    	return wt;
		}
		return null;
	}
	
	/**
	 * 设置半成品成熟度等级为“高”
	 * @param doc
	 * @throws WTException
	 */
	public static void docAfterWfOpeGrade (WTPart part) throws WTException {
				
		try {
			IBAUtility iba = new IBAUtility(part);
			iba.setIBAValue("CATL_HalfProductGrade", "高");
			iba.updateAttributeContainer(part);
            iba.updateIBAHolder(part);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
