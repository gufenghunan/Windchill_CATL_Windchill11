package com.catl.ri.riB.helper;

import org.apache.log4j.Logger;

import wt.fc.ObjectVector;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.iba.value.StringValue;
import wt.part.WTPart;
import wt.query.CompositeWhereExpression;
import wt.query.ConstantExpression;
import wt.query.LogicalOperator;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.TableColumn;
import wt.util.WTException;
import wt.vc.config.ConfigSpec;
import wt.vc.config.LatestConfigSpec;

public class ClassificationHelper {
	private static final Logger logger = Logger.getLogger(ClassificationHelper.class.getName());
	/**
	 * 根据分类节点名查询属于该分类的所有WTPart update by szeng 2013-10-12
	 */
	public static QueryResult getAllPartsByLikeNodeName(String clfNodesName)
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
		andExpression.append(new SearchCondition(tc3, SearchCondition.EQUAL,
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
	public static void main(String[] args) throws WTException {
		getAllPartsByLikeNodeName("Anode");
	}
}
