package com.catl.pd.util;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.drools.core.util.StringUtils;

import com.catl.common.util.PropertiesUtil;
import com.catl.line.constant.ConstantLine;
import com.catl.line.entity.ParentPNAttr;
import com.catl.line.exception.LineException;
import com.ptc.cat.entity.client.constraint.Constraint;
import com.ptc.core.lwc.client.commands.LWCCommands;
import com.ptc.core.lwc.client.util.ConstraintsHelper;
import com.ptc.core.lwc.common.view.AttributeDefinitionReadView;
import com.ptc.core.lwc.common.view.DisplayStyleReadView;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;
import com.ptc.core.meta.common.FloatingPoint;

import wt.fc.ObjectVector;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.iba.definition.StringDefinition;
import wt.iba.value.StringValue;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.query.CompositeWhereExpression;
import wt.query.ConstantExpression;
import wt.query.LogicalOperator;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.SubSelectExpression;
import wt.query.TableColumn;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.config.ConfigSpec;
import wt.vc.config.LatestConfigSpec;

/**
 * 获取物料组相关信息
 * 
 * @author hdong
 *
 */
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
		while (qr.hasMoreElements()) {
			Object[] result = (Object[]) qr.nextElement();
			LWCStructEnumAttTemplate lwc=(LWCStructEnumAttTemplate) result[0];
			if(lwc.getDeletedId()==null){
				return lwc;
			}
			
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
			qs.appendWhere(new SearchCondition(StringDefinition.class,
					StringDefinition.NAME, SearchCondition.EQUAL, ConstantLine.var_clf),
					new int[] { defIndex });
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
		if(StringUtils.isEmpty(value)){
			return null;
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
		TableColumn tc3 = new TableColumn(aliases[1], "VALUE2");
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
		String nodeName = clfNodeName;
		boolean flag = true;
		Stack stack = new Stack();
		while (flag || !stack.isEmpty()) {
			LWCStructEnumAttTemplate let = new LWCStructEnumAttTemplate();
			if (!flag) {
				let = (LWCStructEnumAttTemplate) stack.pop();
				nodeName = let.getName();
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
			if (qr.size() == 0 && flag) {
				LWCStructEnumAttTemplate lwcroot = getClfNodeByName(nodeName);
				flag = false;
				result.addElement(lwcroot);
			}
			if (qr.size() == 0) {
				result.addElement(let);
				System.out.println(let.getName());
			}

			while (qr.hasMoreElements()) {
				Object obj[] = (Object[]) qr.nextElement();
				stack.push(obj[0]);
			}
		}
		if (result != null) {
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
	 * Get IBA value by part and IBA name
	 * 
	 * @param object
	 * @param ibaname
	 * @return
	 * @throws WTException
	 */
	public static String getIBAValueByIBAName(WTPart object, String ibaname)
			throws WTException {
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
			// //System.out.println(qs);
			qs.appendWhere(new SearchCondition(StringDefinition.class,
					StringDefinition.NAME, SearchCondition.EQUAL, ibaname),
					new int[] { defIndex });
			// System.out.println(qs);
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
