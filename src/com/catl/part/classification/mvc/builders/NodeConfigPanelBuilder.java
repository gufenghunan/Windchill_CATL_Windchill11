package com.catl.part.classification.mvc.builders;

import com.catl.part.classification.ClassificationNodeConfig;
import com.catl.part.classification.NodeConfigHelper;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.AttributePanelConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.GroupConfig;

import wt.util.WTException;
import wt.util.WTMessage;

@ComponentBuilder("com.catl.part.classification.nodeConfigPanel")
public class NodeConfigPanelBuilder extends AbstractComponentBuilder {

	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
		Object obj = params.getContextObject();
		if(obj instanceof LWCStructEnumAttTemplate){
			LWCStructEnumAttTemplate node = (LWCStructEnumAttTemplate)obj;
			return NodeConfigHelper.getNodeConfig(node);
		}
		return null;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
		ComponentConfigFactory configFactory = getComponentConfigFactory();
		
		AttributePanelConfig panelConfig = configFactory.newAttributePanelConfig();
		
		GroupConfig groupConfig = configFactory.newGroupConfig();
		groupConfig.setId("catlNodeConfigPanel");
		groupConfig.setLabel(WTMessage.getLocalizedMessage("com.catl.part.classification.resource.CATLNodeConfigRB", "CATL_CONFIG_PANEL_GROUP"));
		panelConfig.addComponent(groupConfig);
		
		groupConfig.addComponent(configFactory.newAttributeConfig(ClassificationNodeConfig.NEED_FAE, WTMessage.getLocalizedMessage("com.catl.part.classification.resource.CATLNodeConfigRB", "ATTR_NEED_FAE")));
		groupConfig.addComponent(configFactory.newAttributeConfig(ClassificationNodeConfig.ATTRIBUTE_REF, WTMessage.getLocalizedMessage("com.catl.part.classification.resource.CATLNodeConfigRB", "ATTR_ATTRIBUTE_REF")));
		groupConfig.addComponent(configFactory.newAttributeConfig(ClassificationNodeConfig.NEED_NON_FAE_REPORT, WTMessage.getLocalizedMessage("com.catl.part.classification.resource.CATLNodeConfigRB", "ATTR_NEED_NON_FAE_REPORTE")));
		groupConfig.addComponent(configFactory.newAttributeConfig(ClassificationNodeConfig.MAKE_NEED_FAE, WTMessage.getLocalizedMessage("com.catl.part.classification.resource.CATLNodeConfigRB", "MAKE_NEED_FAE")));
		groupConfig.addComponent(configFactory.newAttributeConfig(ClassificationNodeConfig.BUY_NEED_FAE, WTMessage.getLocalizedMessage("com.catl.part.classification.resource.CATLNodeConfigRB", "BUY_NEED_FAE")));
		groupConfig.addComponent(configFactory.newAttributeConfig(ClassificationNodeConfig.MAKE_BUY_NEED_FAE, WTMessage.getLocalizedMessage("com.catl.part.classification.resource.CATLNodeConfigRB", "MAKE_BUY_NEED_FAE")));
		groupConfig.addComponent(configFactory.newAttributeConfig(ClassificationNodeConfig.CUSTOMER_NEED_FAE, WTMessage.getLocalizedMessage("com.catl.part.classification.resource.CATLNodeConfigRB", "CUSTOMER_NEED_FAE")));
		groupConfig.addComponent(configFactory.newAttributeConfig(ClassificationNodeConfig.VIRTUAL_NEED_FAE, WTMessage.getLocalizedMessage("com.catl.part.classification.resource.CATLNodeConfigRB", "VIRTUAL_NEED_FAE")));
		return panelConfig;
	}

}
