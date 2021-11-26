package com.catl.cadence.util;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import wt.csm.navigation.ClassificationNode;
import wt.fc.ObjectVector;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.iba.definition.StringDefinition;
import wt.iba.value.StringValue;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.query.ClassAttribute;
import wt.query.CompositeWhereExpression;
import wt.query.ConstantExpression;
import wt.query.ExistsExpression;
import wt.query.LogicalOperator;
import wt.query.NegatedExpression;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.SubSelectExpression;
import wt.query.TableColumn;
import wt.query.TableExpression;
import wt.session.SessionHelper;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.config.ConfigSpec;
import wt.vc.config.LatestConfigSpec;

import com.google.gwt.user.client.rpc.RemoteService;
import com.ptc.core.lwc.client.commands.LWCCommands;
import com.ptc.core.lwc.common.view.AttributeDefinitionReadView;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;

public class NodeUtil implements RemoteAccess {
	public static void main(String[] args) throws WTException,
			WTPropertyVetoException, RemoteException {
		login("wcadmin", "wcadmin");
	}

	public static LWCStructEnumAttTemplate getClfNodeByName(String clfNodeName)
			throws WTException {
		QuerySpec qs = new QuerySpec();
		int clfIndex = qs.appendClassList(LWCStructEnumAttTemplate.class, true);
		SearchCondition sc = new SearchCondition(
				LWCStructEnumAttTemplate.class, LWCStructEnumAttTemplate.NAME,
				SearchCondition.EQUAL, clfNodeName);
		qs.appendWhere(sc, new int[] { clfIndex });
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		if (qr.hasMoreElements()) {
			Object[] result = (Object[]) qr.nextElement();
			return (LWCStructEnumAttTemplate) result[0];
		}
		return null;
	}

	public static LWCStructEnumAttTemplate getLWCStructEnumAttTemplateByPart(
			WTPart object) throws WTException {
		Persistable pers = (Persistable) object;
		String value = null;
		long ida2a2 = pers.getPersistInfo().getObjectIdentifier().getId();

		try {
			QuerySpec qs = new QuerySpec();
			int defIndex = qs.appendClassList(StringDefinition.class, false);
			int valIndex = qs.appendClassList(StringValue.class, true);
			qs.appendWhere(new SearchCondition(StringValue.class,
					"theIBAHolderReference.key.id", SearchCondition.EQUAL,
					ida2a2), new int[] { valIndex });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(StringValue.class,
					"definitionReference.key.id", StringDefinition.class,
					"thePersistInfo.theObjectIdentifier.id"), new int[] {
					valIndex, defIndex });
			qs.appendAnd();
			//System.out.println(qs);
			qs.appendWhere(new SearchCondition(StringDefinition.class,
					StringDefinition.NAME, SearchCondition.EQUAL, "cls"),
					new int[] { defIndex });
//			System.out.println(qs);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			if (qr.hasMoreElements()) {
				Object obj[] = (Object[]) qr.nextElement();
				StringValue strValue = (StringValue) obj[0];
				value = strValue.getValue();
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return getClfNodeByName(value);
	}

	/**
	 * 根据分类节点名查询属于该分类的所有WTPart update by szeng 2013-10-12
	 */
	public static QueryResult getAllPartsByCLFNodesName(String clfNodesName)
			throws WTException {
		QuerySpec qs = new QuerySpec();
		int a = qs.appendClassList(WTPart.class, true);
		int b = qs.appendClassList(StringValue.class, false);
		String[] aliases = new String[2];
		aliases[0] = qs.getFromClause().getAliasAt(a);
		aliases[1] = qs.getFromClause().getAliasAt(b);
		TableColumn tc1 = new TableColumn(aliases[0], "IDA2A2");
		TableColumn tc2 = new TableColumn(aliases[1], "IDA3A4");
		TableColumn tc3 = new TableColumn(aliases[1], "VALUE");
		CompositeWhereExpression andExpression = new CompositeWhereExpression(
				LogicalOperator.AND);
		andExpression.append(new SearchCondition(tc1, "=", tc2));
		andExpression.append(new SearchCondition(tc3, "=",
				new ConstantExpression(clfNodesName)));
		qs.appendWhere(andExpression);
		QueryResult qr = null, qr1 = null;
		qr = PersistenceHelper.manager.find(qs);
		ObjectVector ov = new ObjectVector();
		while (qr.hasMoreElements()) {
			Object obj[] = (Object[]) qr.nextElement();
			ov.addElement(obj[0]);
		}
		qr1 = new QueryResult(ov);
		ConfigSpec cs = new LatestConfigSpec();
		qr1 = cs.process(qr1);
		return qr1;
	}

	public static LWCStructEnumAttTemplate[] getClfNodesByName(
			String clfNodeName) throws WTException {
		QuerySpec qs = new QuerySpec();
		int clfIndex = qs.appendClassList(LWCStructEnumAttTemplate.class, true);
		SearchCondition sc = new SearchCondition(
				LWCStructEnumAttTemplate.class, LWCStructEnumAttTemplate.NAME,
				SearchCondition.LIKE, clfNodeName);
		qs.appendWhere(sc, new int[] { clfIndex });

		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		LWCStructEnumAttTemplate[] results = new LWCStructEnumAttTemplate[qr
				.size()];
		int i = 0;
		while (qr.hasMoreElements()) {
			Object[] result = (Object[]) qr.nextElement();
			results[i] = (LWCStructEnumAttTemplate) result[0];
			i++;
		}
		return results;
	}

	public static Enumeration getAllNoChildrenClfNode() throws WTException,
			WTPropertyVetoException {
		String aliases[] = new String[2];
		QuerySpec queryspec = new QuerySpec();
		QuerySpec subqueryspec = new QuerySpec();
		queryspec.setAdvancedQueryEnabled(true);
		subqueryspec.setAdvancedQueryEnabled(true);

		queryspec.setQueryLimit(-1);
		// --------------------------------------------------------------------
		try {
			SessionHelper.manager.setAuthenticatedPrincipal("wcadmin");
		} catch (WTException e) {
			e.printStackTrace();
		}
		int a = subqueryspec.appendClassList(LWCStructEnumAttTemplate.class,
				false);
		int b = queryspec.appendClassList(LWCStructEnumAttTemplate.class, true);
		aliases[0] = subqueryspec.getFromClause().getAliasAt(a);
		aliases[1] = queryspec.getFromClause().getAliasAt(b);

		TableColumn tc0 = new TableColumn(aliases[0], "IDA3A4");
		subqueryspec.appendSelect(tc0, false);
		// subqueryspec.setDistinct(true);
		// subqueryspec.appendGroupBy(tc0, a, false);
		System.out.println(subqueryspec);
		// QueryResult subqr = PersistenceHelper.manager.find(subqueryspec);

		// System.out.println(subqr.size());
		// ObjectVector subov=new ObjectVector();

		TableColumn tc1 = new TableColumn(aliases[1], "IDA2A2");
		// TableColumn tc1 = new TableColumn(null, "IDA2A2");
		SearchCondition sc = new SearchCondition(tc1, SearchCondition.NOT_IN,
				new SubSelectExpression(subqueryspec));
		queryspec.appendWhere(sc);
		// queryspec.appendFrom(new SubSelectExpression(subqueryspec));
		queryspec.appendSelect(tc1, false);
		System.out.println(queryspec);
		if (queryspec.isAdvancedQuery() && queryspec.isAdvancedQueryEnabled()) {
			System.out.println("queryspec is advancedQuery");
		}
		if (subqueryspec.isAdvancedQuery()
				&& subqueryspec.isAdvancedQueryEnabled()) {
			System.out.println("subqueryspec is advancedQuery");
		}
		QueryResult qr = PersistenceHelper.manager.find(queryspec);
		// ObjectVector ob=qr.getObjectVector();
		System.out.println(qr.size());

		ObjectVector ov = new ObjectVector();
		while (qr.hasMoreElements()) {
			Object obj[] = (Object[]) qr.nextElement();
			ov.addElement(obj[0]);
			LWCStructEnumAttTemplate let = new LWCStructEnumAttTemplate();
			let = (LWCStructEnumAttTemplate) obj[0];
			System.out.println(let.getName());
		}

		QueryResult qr1 = new QueryResult(ov);
		System.out.println("叶子节点数为:" + qr1.size());
		return qr.getEnumeration();

	}

	/**
	 * 根据分类结点的名称获得此结点下的所有叶子结点
	 * 
	 * @param clfNodeName
	 * @return ArrayList,存的数据类型为：wt.csm.navigation.ClassificationNode
	 * @throws WTException
	 * @throws RemoteException
	 */
	public static QueryResult getNothaveChildClfNodes(String clfNodeName)
			throws WTException, RemoteException {
		ObjectVector result = new ObjectVector();
		String nodeName=clfNodeName;
		boolean flag = true;
		Stack stack = new Stack();
		while (flag || !stack.isEmpty()) {
			LWCStructEnumAttTemplate let = new LWCStructEnumAttTemplate();
			if(!flag){
				let=(LWCStructEnumAttTemplate) stack.pop();
				nodeName=let.getName();
			}
			String aliases[] = new String[2];
			QuerySpec queryspec = new QuerySpec();
			QuerySpec subqueryspec = new QuerySpec();
			queryspec.setAdvancedQueryEnabled(true);
			subqueryspec.setAdvancedQueryEnabled(true);

			queryspec.setQueryLimit(-1);
			int a = subqueryspec.appendClassList(
					LWCStructEnumAttTemplate.class, false);
			int b = queryspec.appendClassList(LWCStructEnumAttTemplate.class,
					true);
			aliases[0] = subqueryspec.getFromClause().getAliasAt(a);
			aliases[1] = queryspec.getFromClause().getAliasAt(b);

			TableColumn tc0 = new TableColumn(aliases[0], "IDA2A2");
			subqueryspec.appendSelect(tc0, false);

			TableColumn nametc = new TableColumn(aliases[0], "NAME");
			SearchCondition subsc = new SearchCondition(nametc,
					SearchCondition.EQUAL, new ConstantExpression(nodeName));
			subqueryspec.appendWhere(subsc);
			System.out.println(subqueryspec);

			TableColumn tc1 = new TableColumn(aliases[1], "IDA3A4");
			SearchCondition sc = new SearchCondition(tc1, SearchCondition.IN,
					new SubSelectExpression(subqueryspec));
			queryspec.appendWhere(sc);
			queryspec.appendSelect(tc1, false);
			System.out.println(queryspec);
			if (queryspec.isAdvancedQuery()
					&& queryspec.isAdvancedQueryEnabled()) {
				System.out.println("queryspec is advancedQuery");
			}
			if (subqueryspec.isAdvancedQuery()
					&& subqueryspec.isAdvancedQueryEnabled()) {
				System.out.println("subqueryspec is advancedQuery");
			}
			QueryResult qr = PersistenceHelper.manager.find(queryspec);
			System.out.println(qr.size());
			if(qr.size()==0&&flag){
				LWCStructEnumAttTemplate lwcroot=getClfNodeByName(nodeName);
				flag=false;
				result.addElement(lwcroot);
			}
			if(qr.size()==0){
				result.addElement(let);
				System.out.println(let.getName());
			}

			while (qr.hasMoreElements()) {
				Object obj[] = (Object[]) qr.nextElement();
				stack.push(obj[0]);				
			}
		}
		if(result!=null){
			System.out.println(result.size());
			QueryResult qr1 = new QueryResult(result);
			return qr1;
		}
		return null;
	}

	public static void login(String user, String password) {
		RemoteMethodServer ms = RemoteMethodServer.getDefault();
		ms.setUserName(user);
		ms.setPassword(password);
		try {
			SessionHelper.manager.setAuthenticatedPrincipal(user);
		} catch (WTException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get researchType classification attribute by part
	 * 
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public static Map<String, Object> getClassificationAttr(WTPart part) throws WTException {
		Map<String, Object> attClfMap = new HashMap<String, Object>();
		LWCStructEnumAttTemplate node = getLWCStructEnumAttTemplateByPart(part);
		if (node != null) {
			// classificationName = node.getName();
			ReferenceFactory rf = new ReferenceFactory();
			String oid = rf.getReferenceString(node);
			ArrayList<AttributeDefinitionReadView> classAttDefReadViewArray = LWCCommands
					.getTypeAttributes(oid);
			for (AttributeDefinitionReadView attView : classAttDefReadViewArray) {
				Object value = getIBAValueByIBAName(part, attView.getName());
				//System.out.println("value+"+value);
				attClfMap.put(attView.getDisplayName(), value);
			}
		}
		return attClfMap;
	}
	
	/**
	 * Get IBA value by part and IBA name
	 * @param object
	 * @param ibaname
	 * @return
	 * @throws WTException
	 */
	public static String getIBAValueByIBAName(WTPart object, String ibaname) throws WTException {
		Persistable pers = (Persistable) object;
		String value = null;
		long ida2a2 = pers.getPersistInfo().getObjectIdentifier().getId();

		try {
			QuerySpec qs = new QuerySpec();
			int defIndex = qs.appendClassList(StringDefinition.class, false);
			int valIndex = qs.appendClassList(StringValue.class, true);
			qs.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", SearchCondition.EQUAL,
					ida2a2), new int[] { valIndex });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(StringValue.class, "definitionReference.key.id", StringDefinition.class,
					"thePersistInfo.theObjectIdentifier.id"), new int[] { valIndex, defIndex });
			qs.appendAnd();
			// //System.out.println(qs);
			qs.appendWhere(
					new SearchCondition(StringDefinition.class, StringDefinition.NAME, SearchCondition.EQUAL, ibaname),
					new int[] { defIndex });
			//System.out.println(qs);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			if (qr.hasMoreElements()) {
				Object obj[] = (Object[]) qr.nextElement();
				StringValue strValue = (StringValue) obj[0];
				value = strValue.getValue();
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return value;
	}
}
