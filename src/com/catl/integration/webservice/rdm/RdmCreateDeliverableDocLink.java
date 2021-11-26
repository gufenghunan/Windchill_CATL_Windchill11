package com.catl.integration.webservice.rdm;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebService;

import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.iba.definition.StringDefinition;
import wt.iba.value.StringValue;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.query.ArrayExpression;
import wt.query.ClassAttribute;
import wt.query.ConstantExpression;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.TableColumn;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTAttributeNameIfc;

import com.catl.common.constant.DocState;
import com.catl.common.constant.TypeName;
import com.catl.common.util.ConfigHelper;
import com.catl.common.util.DocUtil;
import com.catl.integration.log.TransactionLogHelper;
import com.catl.integration.log.WebServiceTransactionInfo;
import com.catl.integration.log.WebServiceTransactionLog;
import com.catl.integration.rdm.ObjectLinkedByRdm;
import com.catl.integration.rdm.RdmIntegrationHelper;
import com.ptc.core.meta.type.mgmt.server.impl.WTTypeDefinition;
import com.ptc.core.meta.type.mgmt.server.impl.WTTypeDefinitionMaster;

@WebService(serviceName = "RdmCreateDeliverableDocLink")
public class RdmCreateDeliverableDocLink {

	@WebMethod(operationName = "createDeliverableDocLink")
	public RdmProcessResult createDeliverableDocLink(DeliverableDocumentLinkInfo linkInfo) {
		StringBuffer buffer = new StringBuffer();
		RdmProcessResult result = new RdmProcessResult();
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		
		WebServiceTransactionLog transactionLog = new WebServiceTransactionLog();
		WebServiceTransactionInfo transactionInfo = new WebServiceTransactionInfo();
		try{
			transactionLog.setServiceType(WebServiceTransactionLog.SERVICE_TYPE_PROVIDER);
            transactionLog.setServiceSide("PLM");
            transactionLog.setClientSide("RDM");
            transactionLog.setClientId(SessionHelper.getPrincipal().getName());
            transactionLog.setServiceClass(RdmCreateDeliverableDocLink.class.getName());
            transactionLog.setServiceMethod("createDeliverableDocLink");
            transactionLog.setStartTime(new Timestamp(System.currentTimeMillis()));
            transactionLog.setTransactionInfo(transactionInfo);
            transactionInfo.setParameterObject(linkInfo);
            
            WTPrincipal user =SessionHelper.manager.getPrincipal();
    		String deliverableId = linkInfo.getDeliverableId();
    		String branchId = linkInfo.getBranchId();
    		String docNumber = linkInfo.getDocNumber();
    		
    		if(!user.getName().equals(ConfigHelper.getProperty("webservice.clientUser.rdm"))){
    			result.setResult("1");
    			buffer.append("访问Web Service的用户不合法! ");
    		}
    		if(deliverableId == null || deliverableId.equals("")){
    			result.setResult("1");
    			buffer.append("deliverableId参数必须设置 ");
    		}
    		if(branchId == null || branchId.equals("")){
    			result.setResult("1");
    			buffer.append("branchId参数必须设置 ");
    		}else if(!DocUtil.isExistWTDocument(branchId)){
    			result.setResult("1");
    			buffer.append("branchId参数"+branchId+"指示的文档不存在设置 ");
    		}
    		if(docNumber == null || docNumber.equals("")){
    			result.setResult("1");
    			buffer.append("docNumber参数必须设置 ");
    		}else if(DocUtil.getLatestWTDocument(docNumber) == null){
    			result.setResult("1");
    			buffer.append("docNumber参数"+docNumber+"指示的文档不存在设置 ");
    		}
    		Map<String,String> map = new HashMap<String,String>();
    		map.put("deliverableId", deliverableId);
    		map.put("branchId", branchId);
    		map.put("docNumber", docNumber);
    		map.put("docType", ObjectLinkedByRdm.class.getName());
    		if(RdmIntegrationHelper.queryObjectLinkedByRdm(map) != null){
    			result.setResult("2");
    			buffer.append("链接关系已经存在");
    		}
    		result.setMessage(buffer.toString());
    		if(result.getResult() != null && (result.getResult().equals("1") || result.getResult().equals("2"))){
    			transactionLog.setFinishTime(new Timestamp(System.currentTimeMillis()));
    	        transactionLog.setStatus(WebServiceTransactionLog.STATUS_SUCCESS);
    	        transactionInfo.setResultObject(result);
    			return result;
    		}
            
			//.....处理逻辑
    		ObjectLinkedByRdm rdm = new ObjectLinkedByRdm();
			rdm.setDeliverableId(deliverableId);
			rdm.setBranchId(Long.parseLong(branchId));
			rdm.setObjectNumber(linkInfo.getDocNumber());
			rdm.setObjectType(rdm.toString()); 
			PersistenceHelper.manager.save(rdm);
			
			result.setResult("0");
			result.setMessage("链接关系创建成功");
            
			transactionLog.setFinishTime(new Timestamp(System.currentTimeMillis()));
	        transactionLog.setStatus(WebServiceTransactionLog.STATUS_SUCCESS);
	        transactionInfo.setResultObject(result);
			
		}catch(Exception e){
			result.setResult("1");
			result.setMessage("程序异常 错误消息："+e.getMessage());
			transactionLog.setFinishTime(new Timestamp(System.currentTimeMillis()));
            transactionLog.setStatus(WebServiceTransactionLog.STATUS_FAILED);
            transactionInfo.setException(e);
			e.printStackTrace();
		}finally{
			TransactionLogHelper.logTransaction(transactionLog);
			SessionServerHelper.manager.setAccessEnforced(enforce); 
		}
		return result;
	}

}
