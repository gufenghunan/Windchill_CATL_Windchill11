package com.catl.integration.webservice.bms;

import java.io.IOException;
import java.util.Locale;
import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.ptc.prolog.pub.RunTimeException;

import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;
import wt.vc.wip.WorkInProgressHelper;

@WebService(serviceName = "BmsCheckSoftDocByPNLink")
public class BmsCheckSoftDocByPNLink implements RemoteAccess {
	
	
	/*public static void main(String[] args) throws Exception {
		RemoteMethodServer rm = RemoteMethodServer.getDefault();
		rm.setUserName("wcadmin");
		rm.setPassword("wcadmin");
		rm.invoke("Test", BmsCheckSoftDocByPNLink.class.getName(), null, null, null); // 远程调用
	}*/
	
	/**远程调用测试方法
	 * 
	 * @throws Exception
	 */
	/*public static void Test() throws Exception {
		String jsonstr = "{'PN':'PBMS01-00077','username':'test01'}";
		String result = checkSoftDocByPNLink(jsonstr);
		System.out.println("result= "+result);
	}*/

	@WebMethod(operationName = "checkSoftDocByPNLink")
	public String checkSoftDocByPNLink(String jsonstr) throws RunTimeException, IOException{
		String resultstr = "";
		String message = "";
		try {
			String PN = "";
			String Name = "";
		
		JSONObject json = new JSONObject(jsonstr);
		Name = json.getString("username");
		PN = json.getString("PN");
		
		if(PN.equals("")){
			return "PN等于空";
		}else if(!PN.startsWith("P")) {
			return "PN等于【"+PN+"】必须是以【P】开头";
		}
		else if (Name.equals("")) {
			return "Name等于空";
		}
		WTPart part = getLastestWTPartByNumber(PN);
		if (part==null) {
			return "产品PN:【"+PN+"】在PLM系统中不存在，请重新填写";
		}
		if(WorkInProgressHelper.isCheckedOut(part)){
			return "部件:【"+part.getNumber()+"】在plm中已经检出,请检入后再试";
		}
		
		SessionHelper.manager.setPrincipal(Name);
		SessionHelper.manager.setAuthenticatedPrincipal(Name);
		QueryResult docs = WTPartHelper.service.getReferencesWTDocumentMasters(part);
		JSONArray jsonarray = new JSONArray();
		
		while (docs.hasMoreElements()) {
			Object object = (Object) docs.nextElement();
			if (object instanceof WTDocumentMaster ) {
				WTDocumentMaster docMaster = (WTDocumentMaster) object;
				WTDocument doc = getLastestDocumentMaster(docMaster);
				String docType = TypedUtilityServiceHelper.service.getTypeIdentifier(doc).getTypename();
				if (docType.indexOf("com.CATLBattery.ProductSoftDoc")>0) {
					JSONObject result = new JSONObject();
					result.put("docNumber", doc.getNumber());
					result.put("docState", doc.getLifeCycleState().getDisplay(Locale.CHINA));
					jsonarray.put(result);
				}
			}
		}
		
		resultstr = jsonarray.toJSONString();

	} catch (Exception e) {
		e.printStackTrace();
		message = e.getLocalizedMessage();
	} 
		if(StringUtils.isBlank(resultstr) || StringUtils.isNotBlank(message)||resultstr.equals("[]")){
			resultstr = "没有关联的软件文档或者当前用户没有访问该文档的权限！";
		}
		return resultstr;
	}
	
	
	/**
	 * 查询最新的部件
	 * @param numStr
	 * @return
	 */
	public static WTPart getLastestWTPartByNumber(String numStr) {
		try {
			QuerySpec queryspec = new QuerySpec(WTPart.class);

			queryspec.appendSearchCondition(new SearchCondition(WTPart.class,
					WTPart.NUMBER, SearchCondition.EQUAL, numStr));
			QueryResult queryresult = PersistenceHelper.manager.find(queryspec);
			LatestConfigSpec cfg = new LatestConfigSpec();
			QueryResult qr = cfg.process(queryresult);
			if (qr.hasMoreElements()) {
				return (WTPart) qr.nextElement();
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * 查询最新的Document
	 * @param master
	 * @return
	 */
	public static WTDocument getLastestDocumentMaster(WTDocumentMaster master) {
		try {
			QueryResult queryresult =VersionControlHelper.service.allIterationsOf(master);
			LatestConfigSpec cfg = new LatestConfigSpec();
			QueryResult qr = cfg.process(queryresult);
			if (qr.hasMoreElements()) {
				return (WTDocument) qr.nextElement();
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return null;
	}
}
