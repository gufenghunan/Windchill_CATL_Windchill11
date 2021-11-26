package com.catl.integration.pi.ecn.erp;

import java.lang.reflect.InvocationTargetException;
import java.net.Authenticator;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.ws.BindingProvider;

import wt.change2.ChangeException2;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeIssue;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.httpgw.GatewayAuthenticator;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.util.WTException;

import com.catl.common.util.GenericUtil;
import com.catl.doc.DocInvalidUtil;
import com.catl.integration.pi.ecn.erp.DTEcndateResponse.TRETURN;
import com.catl.job.CATLSpringJob;
import com.sun.xml.ws.client.BindingProviderProperties;

public class SendECN implements RemoteAccess {

	public static void main(String[] args) throws Exception {
		RemoteMethodServer rm = RemoteMethodServer.getDefault();
		rm.setUserName("wcadmin");
		rm.setPassword("wcadmin");
		rm.invoke("Test", SendECN.class.getName(), null, null, null); // 远程调用
	}
	/**远程调用测试方法
	 * 
	 * @throws Exception
	 */
	public static void Test() throws Exception {
		ReferenceFactory rf = new ReferenceFactory();
		WTObject primaryBusinessObject = (WTObject) rf.getReference("VR:wt.change2.WTChangeOrder2:179048799").getObject();
		String success = secdOutECN(primaryBusinessObject);
		System.out.println("~~~~~~~~~"+success);
	}
	public static String secdOutECN(WTObject primaryBusinessObject) throws ChangeException2, WTException{
		String success = "";
		//设置帐号密码
		//NtlmAuthenticator nt = new NtlmAuthenticator("interface", "Zaq12wsx");
		//Authenticator.setDefault(nt); 
		SIEcndateSendService service = new SIEcndateSendService();
		SIEcndateSend ecnport = service.getHTTPPort();
		Map<String, Object> ctxt = ((BindingProvider) ecnport).getRequestContext();
		ctxt.put(BindingProviderProperties.REQUEST_TIMEOUT, 10*60*1000);//请求时间10分钟
		ctxt.put(BindingProviderProperties.CONNECT_TIMEOUT, 10*60*1000);//连接时间10分钟
		
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
			
			DTEcndateResponse response = ecnport.siEcndateSend(request);
			List<TRETURN> list  = response.getTRETURN();
			for (TRETURN treturn : list) {
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
	public static Map<String, Vector<String>> getChangeablesBeforeObject(WTObject primaryBusinessObject)
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
