/*** Eclipse Class Decompiler plugin, copyright (c) 2012 Chao Chen (cnfree2000@hotmail.com) ***/
package com.catl.require.builder;

import wt.fc.QueryResult;
import wt.util.WTException;

import com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers;
import com.ptc.core.htmlcomp.components.AbstractConfigurableTableBuilder;
import com.ptc.core.htmlcomp.tableview.ConfigurableTable;
import com.ptc.jca.mvc.components.JcaTableConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.ds.DataSourceMode;
import com.ptc.windchill.enterprise.part.views.PartsDocumentsRefTableViews;

@ComponentBuilder({ "newaftersalebuilder" })
public class AddAfterSaleTable extends
		AbstractConfigurableTableBuilder {

	@Override
    public Object buildComponentData(ComponentConfig config, ComponentParams params) throws WTException {
        QueryResult queryResult = new QueryResult();
		return queryResult;
    }

	public ComponentConfig buildComponentConfig(
			ComponentParams paramComponentParams) throws WTException {
		//localJcaTableConfig.setActionModel("promotionRequest wizard promotionItems table");
		ComponentConfigFactory factory = getComponentConfigFactory();
		JcaTableConfig table = (JcaTableConfig) factory.newTableConfig();
		table.setLabel("收集需要创建的部件");
		table.setSelectable(true);
		table.setShowCount(true);
		table.setActionModel("aftersaleToolBar");
        table.setDataSourceMode(DataSourceMode.SYNCHRONOUS);
		table.addComponent(factory.newColumnConfig(ColumnIdentifiers.NUMBER, true));
	    table.addComponent(factory.newColumnConfig(ColumnIdentifiers.NAME, true));
	    table.addComponent(factory.newColumnConfig(ColumnIdentifiers.STATE, true));
        table.addComponent(factory.newColumnConfig(ColumnIdentifiers.INFO_ACTION, true));
        table.addComponent(factory.newColumnConfig(ColumnIdentifiers.NM_ACTIONS, true));
        table.addComponent(factory.newColumnConfig(ColumnIdentifiers.LAST_MODIFIED, true));
        return table;
	}

	@Override
	public ConfigurableTable buildConfigurableTable(String s)
			throws WTException {
		return new PartsDocumentsRefTableViews();
	}
}