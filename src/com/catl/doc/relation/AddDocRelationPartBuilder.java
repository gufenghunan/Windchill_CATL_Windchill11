package com.catl.doc.relation;

import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;

import com.catl.common.constant.DocState;
import com.catl.common.constant.PartState;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.mvc.util.ClientMessageSource;

@ComponentBuilder("com.catl.doc.relation.AddDocRelationPartBuilder")
public class AddDocRelationPartBuilder extends AbstractComponentBuilder {

	private ClientMessageSource msgSource;

	@Override
	public Object buildComponentData(ComponentConfig arg0, ComponentParams params) throws Exception {
		String name = (String) params.getParameter("partName");
		String number = (String) params.getParameter("partNumber");
		QueryResult queryResult = null;
		if (name != null && number != null) {
			queryResult = queryPart(name, number);
		}
		return queryResult;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams arg0) throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig config = factory.newTableConfig();

		config.setConfigurable(false);
		config.setType("wt.fc.WTObject");
		config.setLabel("关联部件");
		config.setSelectable(true);
		config.setSingleSelect(true);

		ColumnConfig oidColumn = factory.newColumnConfig("oid", true);
		config.addComponent(oidColumn);
		oidColumn.setHidden(true);
		oidColumn.setSortable(true);

		ColumnConfig iconColumn = factory.newColumnConfig("type_icon", true);
		config.addComponent(iconColumn);

		Object numberColumn = factory.newColumnConfig("number", true);
		config.addComponent((ComponentConfig) numberColumn);

		Object versionColumn = factory.newColumnConfig("version", true);
		config.addComponent((ComponentConfig) versionColumn);

		ColumnConfig infoPageColumn = factory.newColumnConfig("infoPageAction", false);
		infoPageColumn.setDataUtilityId("infoPageAction");
		config.addComponent(infoPageColumn);

		ColumnConfig nameColumn = factory.newColumnConfig("name", true);
		nameColumn.setLabel("名称");
		config.addComponent(nameColumn);

		return config;
	}

	private QueryResult queryPart(String name, String number) throws WTException {
		name = name == null ? "" : name.trim();
		number = number == null ? "" : number.trim();
		QuerySpec querySpec = new QuerySpec(WTPart.class);
		if (!name.isEmpty()) {
			if (name.contains("*")) {
				name = name.replace("*", "%");
				querySpec.appendWhere(new SearchCondition(WTPart.class, WTPart.NAME, SearchCondition.LIKE, name), new int[] { 0 });
			} else
				querySpec.appendWhere(new SearchCondition(WTPart.class, WTPart.NAME, SearchCondition.EQUAL, name), new int[] { 0 });
		} else {
			querySpec.appendWhere(new SearchCondition(WTPart.class, WTPart.NAME, SearchCondition.LIKE, "%"), new int[] { 0 });
		}

		querySpec.appendAnd();

		if (!number.isEmpty()) {
			if (number.contains("*")) {
				number = number.replace("*", "%");
				querySpec.appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.LIKE, number.toUpperCase()), new int[] { 0 });
			} else
				querySpec.appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL, number.toUpperCase()), new int[] { 0 });

		} else {
			querySpec.appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.LIKE, "%"), new int[] { 0 });
		}
		
		querySpec.appendAnd();
		querySpec.appendWhere(new SearchCondition(WTPart.class,WTPart.LIFE_CYCLE_STATE,SearchCondition.NOT_EQUAL,PartState.DISABLEDFORDESIGN), new int[]{0});
		
		System.out.println(querySpec);
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) querySpec);
		qr = new LatestConfigSpec().process(qr);
		return qr;
	}
}
