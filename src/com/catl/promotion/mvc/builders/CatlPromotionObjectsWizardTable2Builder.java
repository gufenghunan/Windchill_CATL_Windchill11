package com.catl.promotion.mvc.builders;

import com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;

import wt.maturity.Promotable;
import wt.util.WTException;

@ComponentBuilder("com.catl.promotion.mvc.builders.CatlPromotionObjectsWizardTable")
public class CatlPromotionObjectsWizardTable2Builder extends AbstractComponentBuilder {
	private final static String tableId = "com.catl.promotion.mvc.builders.CatlPromotionObjects";

	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams params) throws WTException {
		return null;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams arg0) throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig tableConfig = factory.newTableConfig();
        
        tableConfig.setType(Promotable.class.getName());
        tableConfig.setId(tableId);
        tableConfig.setShowCount(true);
        tableConfig.setShowCustomViewLink(false);
        tableConfig.setLabel("升级对象列表");
        tableConfig.setSelectable(true);
        tableConfig.setActionModel("faeMaturityAndDesignDisabledPromotion_wizard_promotionItems");
        
        tableConfig.addComponent(factory.newColumnConfig("type_icon", false));

        ColumnConfig number = factory.newColumnConfig("number", true);
        number.setSortable(false);
        tableConfig.addComponent(number);
        
        ColumnConfig name = factory.newColumnConfig("name", true);
        name.setSortable(false);
        tableConfig.addComponent(name);
        
        ColumnConfig source = factory.newColumnConfig("source", true);
        source.setLabel("采购类型");
        source.setSortable(false);
        tableConfig.addComponent(source);
        
        ColumnConfig version = factory.newColumnConfig("version", true);
        version.setSortable(false);
        tableConfig.addComponent(version);
        
        ColumnConfig status = factory.newColumnConfig("state", true);
        status.setSortable(false);
        tableConfig.addComponent(status);
        
        ColumnConfig creator = factory.newColumnConfig("creator", true);
        creator.setSortable(false);
        tableConfig.addComponent(creator);
        
        ColumnConfig modifier = factory.newColumnConfig("modifier", true);
        modifier.setSortable(false);
        tableConfig.addComponent(modifier);
        
        ColumnConfig orgid = factory.newColumnConfig(ColumnIdentifiers.CONTAINER_NAME, true);
        tableConfig.addComponent(orgid);
        
		return tableConfig;
	}

}
