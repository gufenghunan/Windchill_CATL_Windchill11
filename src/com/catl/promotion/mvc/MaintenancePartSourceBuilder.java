package com.catl.promotion.mvc;

import java.util.ArrayList;
import java.util.List;

import wt.maturity.PromotionNotice;
import wt.part.WTPart;
import wt.util.WTException;
import wt.util.WTMessage;

import com.catl.bom.workflow.BomWfUtil;
import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.windchill.enterprise.part.partResource;

@ComponentBuilder("com.catl.promotion.mvc.MaintenancePartSourceBuilder")
public class MaintenancePartSourceBuilder extends AbstractComponentBuilder {

	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams params)
			throws Exception {
		List<WTPart> list = new ArrayList<WTPart>();
		Object obj = params.getContextObject();
		if (obj != null && obj instanceof PromotionNotice) {
			PromotionNotice pn = (PromotionNotice)obj;
			list.addAll(BomWfUtil.getTargets(pn));
		}
		return list;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams params)
			throws WTException {
		
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig table = factory.newTableConfig();
		table.setComponentMode(ComponentMode.EDIT);
		table.setShowCount(true);
		table.setLabel("维护源信息表");
		
		table.addComponent(factory.newColumnConfig("type_icon", false));
		
		ColumnConfig number = factory.newColumnConfig("number", false);
		number.setComponentMode(ComponentMode.VIEW);
		number.setDataUtilityId("PartBuilderDataUtility");
		table.addComponent(number);
		
		ColumnConfig name = factory.newColumnConfig("name", false);
		name.setComponentMode(ComponentMode.VIEW);
		table.addComponent(name);
		
		ColumnConfig source = factory.newColumnConfig("source", false);
		source.setComponentMode(ComponentMode.EDIT);
		source.setWidth(100);
		source.setLabel(WTMessage.getLocalizedMessage("com.ptc.windchill.enterprise.part.partResource", partResource.SOURCE_COLUMN_LABEL));
		source.setDataUtilityId("PartBuilderDataUtility");
		table.addComponent(source);
		
		return table;
	}

}
