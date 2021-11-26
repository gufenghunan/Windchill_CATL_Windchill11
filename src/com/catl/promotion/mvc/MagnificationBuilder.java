package com.catl.promotion.mvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import wt.maturity.PromotionNotice;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.util.WTException;

import com.catl.bom.workflow.BomWfUtil;
import com.catl.common.util.IBAUtil;
import com.catl.common.util.PropertiesUtil;
import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;

@ComponentBuilder("com.catl.promotion.mvc.MagnificationBuilder")
public class MagnificationBuilder extends AbstractComponentBuilder {

	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams params)
			throws Exception {
		List<WTPartUsageLink> list = new ArrayList<WTPartUsageLink>();
		Object obj = params.getContextObject();
		if (obj != null && obj instanceof PromotionNotice) {
			PromotionNotice pn = (PromotionNotice)obj;
			Set<WTPartUsageLink> links=BomWfUtil.getPTargetsLink(pn);
			String magnificationPartGroup = PropertiesUtil.getValueByKey("magnificationPartGroup");//放大倍数物料组
			for (WTPartUsageLink link : links) {
				WTPartMaster master=(WTPartMaster) link.getRoleBObject();
				if(magnificationPartGroup.contains(master.getNumber().substring(0, 6))){
					list.add(link);
				}
			}
		}
		return list;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams params)
			throws WTException {
		
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig table = factory.newTableConfig();
		table.setFindInTableEnabled(true);
		table.setComponentMode(ComponentMode.EDIT);
		table.setShowCount(true);
		table.setLabel("维护放大倍数信息表");
		ColumnConfig ppartnumber = factory.newColumnConfig("roleAObject.number", true);
		ppartnumber.setLabel("父件PN");
		ppartnumber.setComponentMode(ComponentMode.VIEW);
		ppartnumber.setWidth(200);
		table.addComponent(ppartnumber);
		ColumnConfig ppartname = factory.newColumnConfig("roleAObject.name", true);
		ppartname.setLabel("父件名称");
		ppartname.setComponentMode(ComponentMode.VIEW);
		ppartname.setWidth(200);
		table.addComponent(ppartname);
		
		ColumnConfig cpartnumber = factory.newColumnConfig("roleBObject.number", true);
		cpartnumber.setLabel("子件PN");
		cpartnumber.setComponentMode(ComponentMode.VIEW);
		cpartnumber.setWidth(200);
		table.addComponent(cpartnumber);
		
		ColumnConfig cpartname = factory.newColumnConfig("roleBObject.name", true);
		cpartname.setLabel("子件名称");
		cpartname.setWidth(200);
		table.addComponent(cpartname);
		
		ColumnConfig magnification = factory.newColumnConfig("CATL_MAGNIFICATION", true);
		magnification.setRequired(true);
		magnification.setComponentMode(ComponentMode.EDIT);
		magnification.setWidth(200);
		magnification.setLabel("放大倍数*");
		table.addComponent(magnification);
		return table;
	}

}
