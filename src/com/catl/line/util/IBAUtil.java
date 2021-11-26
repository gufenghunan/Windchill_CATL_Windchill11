package com.catl.line.util;

import java.util.Locale;
import java.util.Vector;

import wt.fc.ObjectVector;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.iba.definition.FloatDefinition;
import wt.iba.definition.IntegerDefinition;
import wt.iba.definition.StringDefinition;
import wt.iba.value.FloatValue;
import wt.iba.value.IntegerValue;
import wt.iba.value.StringValue;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.pom.PersistenceException;
import wt.query.CompositeWhereExpression;
import wt.query.LogicalOperator;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.SubSelectExpression;
import wt.query.TableColumn;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.config.ConfigSpec;
import wt.vc.config.LatestConfigSpec;
import wt.vc.wip.NonLatestCheckoutException;
import wt.vc.wip.WorkInProgressException;
import wt.vc.wip.Workable;

import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.meta.common.DisplayOperationIdentifier;
import com.ptc.core.meta.common.FloatingPoint;
import com.ptc.core.meta.common.Hyperlink;
import com.ptc.core.meta.common.UpdateOperationIdentifier;
import com.ptc.core.meta.type.mgmt.server.impl.WTTypeDefinition;
import com.ptc.core.meta.type.mgmt.server.impl.WTTypeDefinitionMaster;

@SuppressWarnings({ "deprecation" })
public class IBAUtil {

	/**
	 * @param iba1Name
	 * @param iba1Value
	 * @param iba2Name
	 * @param iba2Value
	 * @return 双IBA属性查询 String 类型
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws ClassNotFoundException
	 * @modified: ☆joy_gb(2015年8月4日 下午6:32:20): <br>
	 */
	public static QueryResult queryTwoIBA(String className, String iba1Name,
			String iba1Value, String iba2Name, String iba2Value)
			throws WTException, WTPropertyVetoException, ClassNotFoundException {
		QuerySpec qs = new QuerySpec();
		qs.setAdvancedQueryEnabled(true);

		int objIndex = qs.appendClassList(Class.forName(className), true);
		int tdefIndex = qs.appendClassList(WTTypeDefinition.class, false);
		int tdfMasterIndex = qs.addClassList(WTTypeDefinitionMaster.class,
				false);

		String objAliases = qs.getFromClause().getAliasAt(objIndex);

		SearchCondition sc = new SearchCondition(Class.forName(className),
				WTAttributeNameIfc.LATEST_ITERATION, SearchCondition.IS_TRUE);
		qs.appendWhere(sc, new int[] { objIndex });

		sc = new SearchCondition(Class.forName(className),
				"typeDefinitionReference.key.id", WTTypeDefinition.class,
				WTAttributeNameIfc.ID_NAME);
		qs.appendAnd();
		qs.appendWhere(sc, new int[] { objIndex, tdefIndex });

		sc = new SearchCondition(WTTypeDefinition.class,
				"masterReference.key.id", WTTypeDefinitionMaster.class,
				WTAttributeNameIfc.ID_NAME);
		qs.appendAnd();
		qs.appendWhere(sc, new int[] { tdefIndex, tdfMasterIndex });

		if (null != iba1Value && !iba1Value.isEmpty()) {
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(new TableColumn(objAliases,
					"IDA2A2"), SearchCondition.IN, new SubSelectExpression(
					(StatementSpec) subSql(iba1Name, iba1Value))),
					new int[] { 0 });
		}

		if (null != iba2Value && !iba2Value.isEmpty()) {
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(new TableColumn(objAliases,
					"IDA2A2"), SearchCondition.IN, new SubSelectExpression(
					(StatementSpec) subSql(iba2Name, iba2Value))),
					new int[] { 0 });
		}

		QueryResult result = null;
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);

		ObjectVector ov = new ObjectVector();
		while (qr.hasMoreElements()) {
			Object obj[] = (Object[]) qr.nextElement();
			ov.addElement(obj[0]);
		}
		result = new QueryResult(ov);
		LatestConfigSpec lcs = new LatestConfigSpec();
		result = lcs.process(result);

		return result;
	}

	/**
	 * joy_gb 2015/1/31 IBA属性值 子查询 StringValue StringDefinition
	 * 
	 * @param parametersStr
	 *            参数
	 * @param valueStr
	 *            值
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static QuerySpec subSql(String parametersStr, String valueStr)
			throws WTException, WTPropertyVetoException {
		QuerySpec qs = new QuerySpec();
		qs.setAdvancedQueryEnabled(true);
		qs.getFromClause().setAliasPrefix("C");

		int stringValue = qs.appendClassList(StringValue.class, false);
		int stringDefIndex = qs.appendClassList(StringDefinition.class, false);

		qs.appendSelect(
				new TableColumn(qs.getFromClause().getAliasAt(stringValue),
						"IDA3A4"), true);

		qs.appendWhere(new SearchCondition(StringValue.class,
				"definitionReference.key.id", StringDefinition.class,
				"thePersistInfo.theObjectIdentifier.id"), new int[] {
				stringValue, stringDefIndex });

		qs.appendAnd();
		qs.appendWhere(new SearchCondition(StringDefinition.class,
				StringDefinition.NAME, SearchCondition.EQUAL, parametersStr),
				new int[] { stringDefIndex });

		qs.appendAnd();
		qs.appendWhere(new SearchCondition(StringValue.class,
				StringValue.VALUE2, SearchCondition.EQUAL, valueStr, false),
				new int[] { stringValue });

		return qs;
	}

	/**
	 * @param object
	 * @param ibaName
	 * @return
	 * @modified: ☆joy_gb(2015年8月9日 下午6:31:23): <br>
	 */
	public static String getStringIBAValue(Object object, String ibaName) {
		Persistable pers = (Persistable) object;
		String value = "";
		long ida2a2 = pers.getPersistInfo().getObjectIdentifier().getId();
		try {
			QuerySpec qs = new QuerySpec();
			int defIndex = qs.appendClassList(StringDefinition.class, false);
			int valIndex = qs.appendClassList(StringValue.class, true);
			qs.appendWhere(new SearchCondition(StringValue.class,
					"theIBAHolderReference.key.id", "=", ida2a2),
					new int[] { valIndex });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(StringValue.class,
					"definitionReference.key.id", StringDefinition.class,
					"thePersistInfo.theObjectIdentifier.id"), new int[] {
					valIndex, defIndex });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(StringDefinition.class, "name",
					"=", ibaName), new int[] { defIndex });
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
			if (qr.hasMoreElements()) {
				Object[] obj = (Object[]) qr.nextElement();
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

	/**
	 * @param object
	 * @param ibaName
	 * @return 实数
	 * @modified: ☆joy_gb(2015年8月22日 上午11:30:51): <br>
	 */
	public static double getDoubleIBAValue(Object object, String ibaName) {
		Persistable pers = (Persistable) object;
		double value = 0;
		FloatValue floatValue = null;
		long ida2a2 = pers.getPersistInfo().getObjectIdentifier().getId();
		try {
			QuerySpec qs = new QuerySpec();
			int defIndex = qs.appendClassList(FloatDefinition.class, false);
			int valIndex = qs.appendClassList(FloatValue.class, true);
			qs.appendWhere(new SearchCondition(FloatValue.class,
					"theIBAHolderReference.key.id", "=", ida2a2),
					new int[] { valIndex });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(FloatValue.class,
					"definitionReference.key.id", FloatDefinition.class,
					"thePersistInfo.theObjectIdentifier.id"), new int[] {
					valIndex, defIndex });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(FloatDefinition.class, "name",
					"=", ibaName), new int[] { defIndex });
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
			if (qr.hasMoreElements()) {
				Object[] obj = (Object[]) qr.nextElement();
				floatValue = (FloatValue) obj[0];
				value = floatValue.getValue();
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return value;
	}
	

	/**
	 * @param object
	 * @param ibaName
	 * @return 整数
	 * @modified: ☆joy_gb(2015年8月22日 上午11:30:51): <br>
	 */
	public static double getIntegerIBAValue(Object object, String ibaName) {
		Persistable pers = (Persistable) object;
		double value = 0;
		IntegerValue integerValue = null;
		long ida2a2 = pers.getPersistInfo().getObjectIdentifier().getId();
		try {
			QuerySpec qs = new QuerySpec();
			int defIndex = qs.appendClassList(IntegerDefinition.class, false);
			int valIndex = qs.appendClassList(IntegerValue.class, true);
			qs.appendWhere(new SearchCondition(IntegerValue.class,
					"theIBAHolderReference.key.id", "=", ida2a2),
					new int[] { valIndex });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(IntegerValue.class,
					"definitionReference.key.id", IntegerDefinition.class,
					"thePersistInfo.theObjectIdentifier.id"), new int[] {
					valIndex, defIndex });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(IntegerDefinition.class, "name",
					"=", ibaName), new int[] { defIndex });
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
			if (qr.hasMoreElements()) {
				Object[] obj = (Object[]) qr.nextElement();
				integerValue = (IntegerValue) obj[0];
				value = integerValue.getValue();
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return value;
	}


	public static QueryResult getAllPartsByIBAValue(String value)
			throws WTException {
		QuerySpec qs = new QuerySpec();
		int a = qs.appendClassList(WTPart.class, true);
		int b = qs.appendClassList(StringValue.class, false);
		int defIndex = qs.appendClassList(StringDefinition.class, false);
		String[] aliases = new String[2];
		aliases[0] = qs.getFromClause().getAliasAt(a);
		aliases[1] = qs.getFromClause().getAliasAt(b);
		TableColumn tc1 = new TableColumn(aliases[0], "IDA2A2");
		TableColumn tc2 = new TableColumn(aliases[1], "IDA3A4");
		CompositeWhereExpression andExpression = new CompositeWhereExpression(
				LogicalOperator.AND);
		andExpression.append(new SearchCondition(tc1, "=", tc2));
		qs.appendWhere(andExpression);
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(StringValue.class,
				"definitionReference.key.id", StringDefinition.class,
				"thePersistInfo.theObjectIdentifier.id"), new int[] { b,
				defIndex });
		qs.appendAnd();
		// System.out.println(qs);
		qs.appendWhere(new SearchCondition(StringDefinition.class,
				StringDefinition.NAME, SearchCondition.EQUAL, "GGXH"),
				new int[] { defIndex });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(StringValue.class,
				StringValue.VALUE, SearchCondition.LIKE, "%" + value + "%",
				false), new int[] { b });
		QueryResult qr = null, qr1 = null;
		// System.out.println(qs);
		qr = PersistenceHelper.manager.find(qs);
		ObjectVector ov = new ObjectVector();
		while (qr.hasMoreElements()) {
			Object obj[] = (Object[]) qr.nextElement();
			ov.addElement(obj[0]);
		}
		qr1 = new QueryResult(ov);
		ConfigSpec cs = new LatestConfigSpec();
		qr1 = cs.process(qr1);
		System.out.println("GGXH size:" + qr1.size());
		return qr1;
	}

	/**
	 * @param persistable
	 * @param ibaName
	 * @return 获取iba 属性值
	 * @throws WTException
	 * @modified: ☆joy_gb(2015年8月24日 上午10:44:28): <br>
	 */
	public static String getIBAStringValue(Persistable persistable,
			String ibaName) throws WTException {
		PersistableAdapter obj = new PersistableAdapter(persistable, null,
				java.util.Locale.US, new DisplayOperationIdentifier());
		obj.load(ibaName);
		Object res = obj.get(ibaName);
		if (res == null) {
			return "";
		}
		return (String) res;
	}

	/**
	 * @param persistable
	 * @param ibaName
	 * @return 获取iba 属性值
	 * @throws WTException
	 * @modified: ☆joy_gb(2015年8月24日 上午10:44:28): <br>
	 */
	public static double getIBADoubleValue(Persistable persistable,
			String ibaName) throws WTException {
		PersistableAdapter obj = new PersistableAdapter(persistable, null,
				java.util.Locale.US, new DisplayOperationIdentifier());
		obj.load(ibaName);
		Object res = obj.get(ibaName);
		if (res == null) {
			return 0;
		}
		if (res instanceof FloatingPoint) {
			return ((FloatingPoint) res).doubleValue();
		}
		return 0;
	}

	public static boolean getIBABooleanValue(Persistable persistable,
			String ibaName) throws WTException {
		PersistableAdapter obj = new PersistableAdapter(persistable, null,
				java.util.Locale.US, new DisplayOperationIdentifier());
		obj.load(ibaName);
		Object res = obj.get(ibaName);
		if (res == null) {
			return false;
		}
		return (boolean) res;
	}

	/**
	 * 获取 Hyperlink (URL) 单值
	 * 
	 * @param persistable
	 * @param ibaName
	 * @return
	 * @throws WTException
	 * @modified: ☆joy_gb(2015年11月25日 下午11:16:13): <br>
	 */
	public static Hyperlink getIBAHyperlinkValue(Persistable persistable,
			String ibaName) throws WTException {
		Hyperlink hlink = null;
		PersistableAdapter obj = new PersistableAdapter(persistable, null,
				java.util.Locale.US, new DisplayOperationIdentifier());
		obj.load(ibaName);
		Object ibaObj = obj.get(ibaName);
		if (ibaObj != null && ibaObj instanceof Hyperlink) {
			hlink = (Hyperlink) ibaObj;
		}
		return hlink;
	}

	/**
	 * Hyperlink (URL) 多值获取
	 * 
	 * @param persistable
	 * @param ibaName
	 * @return
	 * @throws WTException
	 * @modified: ☆joy_gb(2015年11月25日 下午11:12:06): <br>
	 */
	public static Vector<Hyperlink> getIBAHyperlinkValues(
			Persistable persistable, String ibaName) throws WTException {
		PersistableAdapter obj = new PersistableAdapter(persistable, null,
				java.util.Locale.US, new DisplayOperationIdentifier());
		obj.load(ibaName);
		Vector<Hyperlink> vec = null;

		// 单值处理
		if (obj.get(ibaName) instanceof Hyperlink) {
			vec = new Vector<Hyperlink>();
			vec.add((Hyperlink) obj.get(ibaName));
			return vec;
		}
		Object[] ibaObjs = (Object[]) obj.get(ibaName);
		if (ibaObjs != null) {
			vec = new Vector<Hyperlink>();
			for (Object ibaObj : ibaObjs) {
				vec.add((Hyperlink) ibaObj);
			}
		}
		return vec;
	}

	/**
	 * 设置iba属性值，前提是对象必须检出
	 * 
	 * @param persistable
	 * @param ibaName
	 * @param ibaValue
	 * @throws WTException
	 * @modified: ☆joy_gb(2015年8月24日 上午10:44:39): <br>
	 */
	public static void setIBAValue(Persistable persistable, String ibaName,
			String ibaValue) throws WTException {
		PersistableAdapter obj = new PersistableAdapter(persistable, null,
				Locale.US, new UpdateOperationIdentifier());
		obj.load(ibaName);
		obj.set(ibaName, ibaValue);
		persistable = obj.apply();
		persistable = (Persistable) PersistenceHelper.manager
				.modify(persistable);
	}

	/**
	 * 检出修改
	 * 
	 * @param persistable
	 * @throws NonLatestCheckoutException
	 * @throws WorkInProgressException
	 * @throws WTPropertyVetoException
	 * @throws PersistenceException
	 * @throws WTException
	 * @modified: ☆joy_gb(2015年11月25日 下午10:23:36): <br>
	 */
	public static void setIBAURLValue(Persistable persistable)
			throws NonLatestCheckoutException, WorkInProgressException,
			WTPropertyVetoException, PersistenceException, WTException {
		persistable = wt.vc.wip.WorkInProgressHelper.service.checkout(
				(Workable) persistable,
				wt.vc.wip.WorkInProgressHelper.service.getCheckoutFolder(), "")
				.getWorkingCopy();
		PersistableAdapter obj = new PersistableAdapter(persistable, null,
				Locale.US, new UpdateOperationIdentifier());
		obj.load("URL"); // URL is the internal name of the IBA
		obj.set("URL", "https:\\\\www.ptc.com\\ (Go to PTC)");
		/*
		 * Note: 1. The Label value needs to be provided in the brackets 2. In
		 * this case, my label would be (Go to PTC)
		 */
		persistable = obj.apply();
		wt.fc.PersistenceHelper.manager.modify(persistable);
		wt.vc.wip.WorkInProgressHelper.service.checkin((Workable) persistable,
				null);
	}

}
