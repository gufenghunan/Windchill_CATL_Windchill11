package com.catl.line.util;

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
	 * 获取分类属性和属性值
	 * 
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public static Map<String, Object> getClassificationAttr(WTPart part)
			throws WTException {
		Map<String, Object> attClfMap = new HashMap<String, Object>();
		List parentAttr = Arrays.asList(ConstantLine.config_group_parentpn);
		List childAttr = Arrays.asList(ConstantLine.config_group_childpn);

		LWCStructEnumAttTemplate node = getLWCStructEnumAttTemplateByPart(part);
		if (node != null) {
			Map cmap = getConnectorMap();
			// classificationName = node.getName();
			ReferenceFactory rf = new ReferenceFactory();
			String oid = rf.getReferenceString(node);
			ArrayList<AttributeDefinitionReadView> classAttDefReadViewArray = LWCCommands
					.getTypeAttributes(oid);

			for (AttributeDefinitionReadView attView : classAttDefReadViewArray) {
				if (!parentAttr.contains(attView.getName())
						&& !childAttr.contains(attView.getName())) {
					continue;
				}

				Object value = null;
				if (attView.getDatatype().getName()
						.equals(FloatingPoint.class.getName())) {
					value = IBAUtil.getDoubleIBAValue(part, attView.getName());
				} else if (attView.getDatatype().getName()
						.equals(String.class.getName())) {
					value = (String) IBAUtil.getStringIBAValue(part,
							attView.getName());

					String name = attView.getName();
					if (name.equals(ConstantLine.var_lconnector)
							|| name.equals(ConstantLine.var_rconnector)
							|| name.equals(ConstantLine.var_ldconnector)
							|| name.equals(ConstantLine.var_rdconnector)) {
						if (!StringUtils.isEmpty(value.toString())) {
							if (cmap.containsKey(value.toString().split("-")[0])) {
								value = cmap
										.get(value.toString().split("-")[0]);
							} else {
								throw new LineException("不能识别的插头类别编码"
										+ value.toString().split("-")[0]);
							}
						}
					}
				} else if (attView.getDatatype().getName()
						.equals(Long.class.getName())) {
					value = IBAUtil.getIntegerIBAValue(part, attView.getName());
					
//					if(Double.parseDouble(value.toString())==0.0){
//						value=null;
//					}
//					if (name.equals(ConstantLine.var_pointa)
//							|| name.equals(ConstantLine.var_pointb)
//							|| name.equals(ConstantLine.var_pointc)
//							|| name.equals(ConstantLine.var_pointd)) {
//						if(Double.parseDouble(value.toString())!=0.0){
//							value=1;
//						}
//					}
				}
				// System.out.println("value+"+value);
				if (value != null) {
					attClfMap.put(attView.getDisplayName(), value);
				}

			}
		}
		return attClfMap;
	}

	/**
	 * 获取分类属性和属性值
	 * 
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public static String getspecificationAttr(WTPart part)
			throws WTException {
		List attr = Arrays.asList(ConstantLine.config_group_childpn_specification);
		LWCStructEnumAttTemplate node = getLWCStructEnumAttTemplateByPart(part);
		 StringBuffer specification = new StringBuffer();
		if (node != null) {
			Map cmap = getConnectorMap();
			ReferenceFactory rf = new ReferenceFactory();
			String oid = rf.getReferenceString(node);
			ArrayList<AttributeDefinitionReadView> classAttDefReadViewArray = LWCCommands
					.getTypeAttributes(oid);
           List<Map> attrs=new ArrayList<Map>();
			for (AttributeDefinitionReadView attView : classAttDefReadViewArray) {
				if (!attr.contains(attView.getName())) {
					continue;
				}
				IBAUtility iba=new IBAUtility(part);
			    String value=iba.getIBAValue(attView.getName());
			    if(value==null){
			    	value="";
			    }
				Map map=new HashMap();
				map.put("name",attView);
				map.put("value",value);
				attrs.add(map);
			}
			Collections.sort(attrs,new Comparator<Map>() {
				@Override
				public int compare(Map arg0, Map arg1) {
					List groupattrs = Arrays.asList(ConstantLine.config_group_childpn_specification);
					AttributeDefinitionReadView view0=(AttributeDefinitionReadView) arg0.get("name");
					AttributeDefinitionReadView view1=(AttributeDefinitionReadView) arg1.get("name");
					int a1=groupattrs.indexOf(view0.getName());
					int a2=groupattrs.indexOf(view1.getName());
					return Integer.valueOf(a1).compareTo(Integer.valueOf(a2));
				}});
		   for (int i = 0; i < attrs.size(); i++) {
				Map map=attrs.get(i);
				AttributeDefinitionReadView attView=(AttributeDefinitionReadView) map.get("name");
				String value=(String) map.get("value");
				if (specification.length() == 0) {
					specification.append(attView.getDisplayName()).append(":")
							.append(value);
				} else {
					specification.append("_").append(attView.getDisplayName())
							.append(":").append(value);
				}
			}
			
			
		}
		return specification.toString();
	}
	/**
	 * 写入dwg和计算用量的属性与属性值对照
	 * 
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public static Map<String, Object> getClassificationNameAttr(WTPart part)
			throws WTException {
		List parentAttr = Arrays.asList(ConstantLine.config_group_parentpn);
		List childAttr = Arrays.asList(ConstantLine.config_group_childpn);
		List otherAttr = Arrays.asList(ConstantLine.config_group_otherattr);
		Map<String, Object> attClfMap = new HashMap<String, Object>();

		LWCStructEnumAttTemplate node = getLWCStructEnumAttTemplateByPart(part);
		if (node != null) {
			// classificationName = node.getName();
			ReferenceFactory rf = new ReferenceFactory();
			String oid = rf.getReferenceString(node);
			ArrayList<AttributeDefinitionReadView> classAttDefReadViewArray = LWCCommands
					.getTypeAttributes(oid);

			for (AttributeDefinitionReadView attView : classAttDefReadViewArray) {
				if (!parentAttr.contains(attView.getName())
						&& !childAttr.contains(attView.getName())
						&& !otherAttr.contains(attView.getName())) {
					continue;
				}

				Object value = null;
				if (attView.getDatatype().getName()
						.equals(FloatingPoint.class.getName())) {
					value = IBAUtil.getDoubleIBAValue(part, attView.getName());
				} else if (attView.getDatatype().getName()
						.equals(String.class.getName())) {
					value = (String) IBAUtil.getStringIBAValue(part,
							attView.getName());
				} else if (attView.getDatatype().getName()
						.equals(Long.class.getName())) {
					value = IBAUtil.getIntegerIBAValue(part, attView.getName());
				}
				if (value != null) {
					attClfMap.put(attView.getName(), value);
				}

			}
		}
		return attClfMap;
	}

	/**
	 * 获取母PN属性
	 * 
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public static List<ParentPNAttr> getParentPNAttrs(WTPart part)
			throws WTException {
		List<ParentPNAttr> attrs = new ArrayList<ParentPNAttr>();
		LWCStructEnumAttTemplate node = getLWCStructEnumAttTemplateByPart(part);
		List groupattrs = Arrays.asList(ConstantLine.config_group_parentpn);
		List grouprequiredattrs = Arrays
				.asList(ConstantLine.config_group_required_parentpn);
		Map attr_values = getValuelist();
		if (node != null) {
			ReferenceFactory rf = new ReferenceFactory();
			String oid = rf.getReferenceString(node);
			ArrayList<AttributeDefinitionReadView> classAttDefReadViewArray = LWCCommands
					.getTypeAttributes(oid);
			for (AttributeDefinitionReadView attView : classAttDefReadViewArray) {
				if (!groupattrs.contains(attView.getName())) {
					continue;
				}
				Object value = null;
				if (attView.getDatatype().getName()
						.equals(FloatingPoint.class.getName())) {
					value = IBAUtil.getDoubleIBAValue(part, attView.getName());
				} else if (attView.getDatatype().getName()
						.equals(String.class.getName())) {
					value = (String) IBAUtil.getStringIBAValue(part,
							attView.getName());
				} else if (attView.getDatatype().getName()
						.equals(Long.class.getName())) {
					value = IBAUtil.getIntegerIBAValue(part, attView.getName());
				}
				if (value == null) {
					value = "";
				}
				ParentPNAttr attr = new ParentPNAttr(part.getNumber(),attView.getName(),
						attView.getDisplayName(), "", attView.getDatatype()
								.getName(), value,
						grouprequiredattrs.contains(attView.getName()));
				List values = (List) attr_values.get(attView.getDisplayName());
				if (values != null && values.size() > 0) {
					attr.setValuelist(values);
				}
				attrs.add(attr);
				

			}
			
		}
		
		Collections.sort(attrs,new Comparator<ParentPNAttr>() {
			public int compare(ParentPNAttr arg0, ParentPNAttr arg1) {
				List groupattrs = Arrays.asList(ConstantLine.config_group_parentpn);
				int a1=groupattrs.indexOf(arg0.getName());
				int a2=groupattrs.indexOf(arg1.getName());
				return Integer.valueOf(a1).compareTo(Integer.valueOf(a2));
			}
		});
		return attrs;
	}

	/**
	 * 获取母PN属性，字符传显示属性值
	 * 
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public static List<ParentPNAttr> getParentPNStringAttrs(WTPart part)
			throws WTException {
		List<ParentPNAttr> attrs = new ArrayList<ParentPNAttr>();
		LWCStructEnumAttTemplate node = getLWCStructEnumAttTemplateByPart(part);
		List groupattrs = Arrays.asList(ConstantLine.config_group_parentpn);
		List grouprequiredattrs = Arrays
				.asList(ConstantLine.config_group_required_parentpn);
		Map cmap = getConnectorMap();
		if (node != null) {
			ReferenceFactory rf = new ReferenceFactory();
			String oid = rf.getReferenceString(node);
			ArrayList<AttributeDefinitionReadView> classAttDefReadViewArray = LWCCommands
					.getTypeAttributes(oid);
			for (AttributeDefinitionReadView attView : classAttDefReadViewArray) {
				if (!groupattrs.contains(attView.getName())) {
					continue;
				}
				IBAUtility iba = new IBAUtility(part);
				String value = iba.getIBAValue(attView.getName());
				if (value == null) {
					value = "";
				}
				String name = attView.getName();
				if (name.equals(ConstantLine.var_lconnector)
						|| name.equals(ConstantLine.var_rconnector)
						|| name.equals(ConstantLine.var_ldconnector)
						|| name.equals(ConstantLine.var_rdconnector)) {
					if (!StringUtils.isEmpty(value.toString())) {
						if (cmap.containsKey(value.toString().split("-")[0])) {
							value = (String) cmap.get(value.toString().split(
									"-")[0]);
						} else {
							throw new LineException("不能识别的插头类别编码"
									+ value.toString().split("-")[0]);
						}
					}
				}
				ParentPNAttr attr = new ParentPNAttr(part.getNumber(),attView.getName(),
						attView.getDisplayName(), "", attView.getDatatype()
								.getName(), value,
						grouprequiredattrs.contains(attView.getName()));
				attrs.add(attr);
			}
		}
		return attrs;
	}

	/**
	 * 获取接头类型配置
	 * 
	 * @return
	 */
	private static Map getConnectorMap() {
		Map cmap = new HashMap();
		String according =PropertiesUtil.getValueByKey("connector_according");
		String[] acc = according.split(",");
		for (int i = 0; i < acc.length; i++) {
			String cacc = acc[i];
			String[] accs = cacc.split("\\|");
			for (int j = 0; j < accs.length; j++) {
				if (accs.length == 2) {
					cmap.put(accs[0], accs[1]);
				}
			}
		}
		return cmap;
	}

	/**
	 * 获取必填值配置
	 * 
	 * @return
	 */
	private static Map<String, List> getValuelist() {
		Map cmap = new HashMap();
		String[] values = ConstantLine.config_values;
		for (int i = 0; i < values.length; i++) {
			String attr_values = values[i];
			String[] value_attr_array = attr_values.split("\\|");
			if (value_attr_array.length != 2) {
				throw new LineException("配置文件config_values配置有误!");
			} else {
				String[] cvalues = value_attr_array[1].split(",");
				List list = new ArrayList();
				Collections.addAll(list, cvalues);
				cmap.put(value_attr_array[0], list);
			}
		}
		return cmap;
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
