package com.catl.part.relation;

import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;

import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.mvc.util.ClientMessageSource;

@ComponentBuilder("com.catl.part.relation.AddPartRelationDocBuilder")
public class AddPartRelationDocBuilder extends AbstractComponentBuilder
{

	private ClientMessageSource msgSource;

	@Override
	public Object buildComponentData(ComponentConfig arg0, ComponentParams params) throws Exception
	{
		String name = (String) params.getParameter("docName");
		String number = (String) params.getParameter("docNumber");
		QueryResult queryResult = null;
		if (name != null && number != null)
		{
			queryResult = queryDoc(name, number);
		}
		return queryResult;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams arg0) throws WTException
	{
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig config = factory.newTableConfig();

		config.setConfigurable(false);
		config.setType("wt.fc.WTObject");
		config.setLabel("RelatedDocument");
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
		nameColumn.setLabel("NAME");
		config.addComponent(nameColumn);

		return config;
	}

	private QueryResult queryDoc(String name, String number) throws WTException
	{
		name = name == null ? "" : name;
		number = number == null ? "" : number;
		QuerySpec querySpec = new QuerySpec(WTDocument.class);
		if (!name.isEmpty())
		{
			querySpec.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NAME, SearchCondition.LIKE, "%" + name + "%"), new int[] { 0 });
		}
		if (!number.isEmpty())
		{
			if (!name.isEmpty())
			{
				querySpec.appendAnd();
			}
			querySpec.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.LIKE, "%" + number + "%"), new int[] { 0 });
		}
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) querySpec);
//		qr = new LatestConfigSpec().process(qr);
		return qr;
	}

}
