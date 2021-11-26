package com.catl.integration.webservice.rdm;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.jws.WebMethod;
import javax.jws.WebService;

import com.catl.common.constant.Constant;
import com.catl.common.constant.DocState;
import com.catl.common.constant.TypeName;
import com.catl.common.util.ConfigHelper;
import com.catl.doc.processor.CatlDocCreateProcessor;
import com.catl.integration.log.TransactionLogHelper;
import com.catl.integration.log.WebServiceTransactionInfo;
import com.catl.integration.log.WebServiceTransactionLog;
import com.ptc.core.meta.type.mgmt.server.impl.WTTypeDefinition;
import com.ptc.core.meta.type.mgmt.server.impl.WTTypeDefinitionMaster;

import wt.doc.WTDocument;
import wt.epm.query.parser.QuerySupport;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.iba.definition.StringDefinition;
import wt.iba.value.StringValue;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.query.ArrayExpression;
import wt.query.ClassAttribute;
import wt.query.CompositeWhereExpression;
import wt.query.ConstantExpression;
import wt.query.LogicalOperator;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.TableColumn;
import wt.services.applicationcontext.implementation.ServiceProperties;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.IterationInfo;
import wt.vc.VersionIdentifier;
import wt.vc.config.LatestConfigSpec;

@WebService(serviceName = "RdmDocumentSearchingService")
public class RdmDocumentSearchingService {
	
	@WebMethod(operationName = "searchDocument")
	public DocumentQueryResult searchDocument(DocumentQuery query){
		StringBuffer buffer = new StringBuffer();
		DocumentQueryResult result = new DocumentQueryResult();
		List<DocumentInfo> infos = new ArrayList<DocumentInfo>();
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		
		WebServiceTransactionLog transactionLog = new WebServiceTransactionLog();
		WebServiceTransactionInfo transactionInfo = new WebServiceTransactionInfo();
		
		try{
			transactionLog.setServiceType(WebServiceTransactionLog.SERVICE_TYPE_PROVIDER);
            transactionLog.setServiceSide("PLM");
            transactionLog.setClientSide("RDM");
            transactionLog.setClientId(SessionHelper.getPrincipal().getName());
            transactionLog.setServiceClass(RdmDocumentSearchingService.class.getName());
            transactionLog.setServiceMethod("searchDocument");
            transactionLog.setStartTime(new Timestamp(System.currentTimeMillis()));
            transactionLog.setTransactionInfo(transactionInfo);
            transactionInfo.setParameterObject(query);
            
			WTPrincipal user =SessionHelper.manager.getPrincipal();
			String docName = query.getDocName();
			String docNumber = query.getDocNumber();
			String projectCode = query.getProjectCode();
			String projectName = query.getProjectName();
			
			if(!user.getName().equals(ConfigHelper.getProperty("webservice.clientUser.rdm"))){
				result.setResult("1");
				buffer.append("访问Web Service的用户不合法! ");
			}
			if(projectCode != null && projectCode.length()<4){
				result.setResult("1");
				buffer.append("项目代码必须至少4个字符 ");
			}
			
			result.setMessage(buffer.toString());
			if(result.getResult().equals("1")){
				transactionLog.setFinishTime(new Timestamp(System.currentTimeMillis()));
		        transactionLog.setStatus(WebServiceTransactionLog.STATUS_SUCCESS);
		        transactionInfo.setResultObject(result);
				return result;
			}
			
			QuerySpec querySpec = new QuerySpec();
			querySpec.setAdvancedQueryEnabled(true);
			
			int docIndex = querySpec.appendClassList(WTDocument.class, false);
			int userIndex = querySpec.appendClassList(WTUser.class, false);
			int typeIndex = querySpec.appendClassList(WTTypeDefinition.class, false);
			int typeMasIndex = querySpec.appendClassList(WTTypeDefinitionMaster.class, false);
			int svIndex = querySpec.appendClassList(StringValue.class, false);
			int sdIndex = querySpec.appendClassList(StringDefinition.class, false);
			int svIndex2 = querySpec.appendClassList(StringValue.class, false);
			int sdIndex2 = querySpec.appendClassList(StringDefinition.class, false);
			
			ClassAttribute docNameAttr = new ClassAttribute(WTDocument.class, WTDocument.NAME);
			ClassAttribute docNumberAttr = new ClassAttribute(WTDocument.class, WTDocument.NUMBER);
			ClassAttribute branchidAttr = new ClassAttribute(WTDocument.class, WTDocument.BRANCH_IDENTIFIER);
			ClassAttribute versionAttr = new ClassAttribute(WTDocument.class, "versionInfo.identifier.versionId");
			ClassAttribute iterationAttr = new ClassAttribute(WTDocument.class, "iterationInfo.identifier.iterationId");
			ClassAttribute creatorAttr = new ClassAttribute(WTUser.class, WTUser.NAME);
			ClassAttribute stateAttr = new ClassAttribute(WTDocument.class, "state.state");
			
			String[] aliases = new String[6];
			aliases[0] = querySpec.getFromClause().getAliasAt(docIndex);
			aliases[1] = querySpec.getFromClause().getAliasAt(userIndex);
			aliases[2] = querySpec.getFromClause().getAliasAt(typeIndex);
			aliases[3] = querySpec.getFromClause().getAliasAt(typeMasIndex);
			TableColumn docCreateUser = new TableColumn(aliases[0], "idA3D2iterationInfo");
			TableColumn docType = new TableColumn(aliases[0], "idA2typeDefinitionReference");
			TableColumn docState = new TableColumn(aliases[0], "statestate");
			TableColumn userIda2a2 = new TableColumn(aliases[1], "IDA2A2");
			TableColumn typeIda2a2 = new TableColumn(aliases[2], "IDA2A2");
			TableColumn typeIda3mas = new TableColumn(aliases[2], "ida3masterreference");
			TableColumn typeMasIda2a2 = new TableColumn(aliases[3], "IDA2A2");
			TableColumn typeMasDisplayKey = new TableColumn(aliases[3], "displayNameKey");

			if(docName != null){
				querySpec.appendWhere(new SearchCondition(WTDocument.class,WTDocument.NAME,SearchCondition.LIKE,"%" + docName + "%"),new int[]{docIndex});
				querySpec.appendAnd();
			}
			if(docNumber != null){
				querySpec.appendWhere(new SearchCondition(WTDocument.class,WTDocument.NUMBER,SearchCondition.LIKE,"%" + docNumber + "%"),new int[]{docIndex});
				querySpec.appendAnd();
			}
			querySpec.appendWhere(new SearchCondition(WTDocument.class, WTAttributeNameIfc.LATEST_ITERATION, SearchCondition.IS_TRUE),new int[] { docIndex });
			querySpec.appendAnd();
			querySpec.appendWhere(new SearchCondition(WTDocument.class, WTDocument.CHECKOUT_INFO+".state", SearchCondition.NOT_EQUAL,"wrk"),new int[] { docIndex });
			querySpec.appendAnd();
			
			SearchCondition join = new SearchCondition(WTDocument.class, WTAttributeNameIfc.ID_NAME, StringValue.class,StringValue.IBAHOLDER_REFERENCE+"."+WTAttributeNameIfc.REF_OBJECT_ID);
			SearchCondition join1 = new SearchCondition(StringValue.class, StringValue.DEFINITION_REFERENCE+"."+WTAttributeNameIfc.REF_OBJECT_ID, StringDefinition.class, WTAttributeNameIfc.ID_NAME);
			SearchCondition join2 = new SearchCondition(WTDocument.class, WTAttributeNameIfc.ID_NAME, StringValue.class,StringValue.IBAHOLDER_REFERENCE+"."+WTAttributeNameIfc.REF_OBJECT_ID);
			SearchCondition join3 = new SearchCondition(StringValue.class, StringValue.DEFINITION_REFERENCE+"."+WTAttributeNameIfc.REF_OBJECT_ID, StringDefinition.class, WTAttributeNameIfc.ID_NAME);
			
			querySpec.appendWhere(join,new int[]{docIndex,svIndex});
			querySpec.appendAnd();
			querySpec.appendWhere(join1, new int[]{svIndex,sdIndex});
			querySpec.appendAnd();
			querySpec.appendWhere(join2,new int[]{docIndex,svIndex2});
			querySpec.appendAnd();
			querySpec.appendWhere(join3, new int[]{svIndex2,sdIndex2});
			querySpec.appendAnd();
			querySpec.appendWhere(new SearchCondition(StringValue.class,StringValue.VALUE2,SearchCondition.EQUAL,projectCode),new int[]{svIndex});
			querySpec.appendAnd();
			querySpec.appendWhere(new SearchCondition(StringDefinition.class,StringDefinition.NAME,SearchCondition.EQUAL,"projectCode"),new int[]{sdIndex});
			querySpec.appendAnd();
			if(projectName != null){
				querySpec.appendWhere(new SearchCondition(StringValue.class,StringValue.VALUE2,SearchCondition.LIKE,"%" + projectName + "%"),new int[]{svIndex2});
				querySpec.appendAnd();
			}
			querySpec.appendWhere(new SearchCondition(StringDefinition.class,StringDefinition.NAME,SearchCondition.EQUAL,"projectName"),new int[]{sdIndex2});
			querySpec.appendAnd();
			
			querySpec.appendWhere(new SearchCondition(docCreateUser,SearchCondition.EQUAL,userIda2a2));
			querySpec.appendAnd();
			querySpec.appendWhere(new SearchCondition(docType,SearchCondition.EQUAL,typeIda2a2));
			querySpec.appendAnd();
			querySpec.appendWhere(new SearchCondition(typeIda3mas,SearchCondition.EQUAL,typeMasIda2a2));
			querySpec.appendAnd();
			querySpec.appendWhere(new SearchCondition(typeMasDisplayKey,SearchCondition.IN,new ArrayExpression(new String[]{TypeName.rdDoc,TypeName.technicalDoc})));
			querySpec.appendAnd();
			querySpec.appendWhere(new SearchCondition(docState,SearchCondition.EQUAL,ConstantExpression.newExpression(DocState.RELEASED)));
			
			querySpec.appendSelect(docNameAttr,docIndex, false);
			querySpec.appendSelect(docNumberAttr,docIndex, false);
			querySpec.appendSelect(branchidAttr,docIndex, false);
			querySpec.appendSelect(versionAttr,docIndex, false);
			querySpec.appendSelect(iterationAttr,docIndex, false);
			querySpec.appendSelect(creatorAttr,userIndex, false);
			querySpec.appendSelect(stateAttr,docIndex, false);
			
			QueryResult qr = PersistenceHelper.manager.find(querySpec);
			int size = qr.size();
			while(qr.hasMoreElements()){
				Object[] obj = (Object[])qr.nextElement();
				
				DocumentInfo info = new DocumentInfo();
				info.setDocName(obj[0].toString());
				info.setDocNumber(obj[1].toString());
				info.setBranchId(((Number)obj[2]).longValue()+"");
				info.setDocVer(obj[3].toString()+"."+obj[4].toString());
				info.setCreatorName(obj[5].toString());
				info.setState(obj[6].toString());
				infos.add(info);
			}
			if(infos.size() == 0){
				result.setResult("2");
				result.setMessage("没有符合条件的文档");
			}else{
				result.setResult("0");
				result.setMessage("查询成功");
				result.setDocuments(infos);
			}
			
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
