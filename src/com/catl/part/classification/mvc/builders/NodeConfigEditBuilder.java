package com.catl.part.classification.mvc.builders;

import com.catl.part.classification.ClassificationNodeConfig;
import com.catl.part.classification.NodeConfigHelper;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;
import com.ptc.core.meta.container.common.AttributeTypeSummary.InputFieldType;
import com.ptc.core.meta.container.common.AttributeTypeSummary.SelectionListStyle;
import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.core.ui.resources.ComponentType;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.AttributeConfig;
import com.ptc.mvc.components.AttributePanelConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.GroupConfig;

import wt.util.WTException;
import wt.util.WTMessage;

@ComponentBuilder("com.catl.part.classification.NodeConfigEditBuilder")
public class NodeConfigEditBuilder extends AbstractComponentBuilder {

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
		panelConfig.setComponentMode(ComponentMode.EDIT);
		panelConfig.setComponentType(ComponentType.WIZARD_ATTRIBUTES_TABLE);
		
		GroupConfig groupConfig = configFactory.newGroupConfig();
		groupConfig.setId("catlNodeConfigEdit");
		groupConfig.setLabel(WTMessage.getLocalizedMessage("com.catl.part.classification.resource.CATLNodeConfigRB", "CATL_CONFIG_EDIT_GROUP"));
		panelConfig.addComponent(groupConfig);
		
		AttributeConfig needFae = configFactory.newAttributeConfig(ClassificationNodeConfig.NEED_FAE, WTMessage.getLocalizedMessage("com.catl.part.classification.resource.CATLNodeConfigRB", "ATTR_NEED_FAE"));
		needFae.setIncludeBlankOption(false);
		needFae.setInputFieldType(InputFieldType.SINGLE_LINE.stringVal());
		needFae.setSelectionListStyle(SelectionListStyle.DROPDOWNLIST.stringVal());
		groupConfig.addComponent(needFae);
		
		AttributeConfig attrRef = configFactory.newAttributeConfig(ClassificationNodeConfig.ATTRIBUTE_REF, WTMessage.getLocalizedMessage("com.catl.part.classification.resource.CATLNodeConfigRB", "ATTR_ATTRIBUTE_REF"));
		attrRef.setDataUtilityId("NodeConfigAttrDataUtility");
		groupConfig.addComponent(attrRef);
		
		AttributeConfig needReport = configFactory.newAttributeConfig(ClassificationNodeConfig.NEED_NON_FAE_REPORT, WTMessage.getLocalizedMessage("com.catl.part.classification.resource.CATLNodeConfigRB", "ATTR_NEED_NON_FAE_REPORTE"));
		needReport.setIncludeBlankOption(false);
		needReport.setInputFieldType(InputFieldType.SINGLE_LINE.stringVal());
		needReport.setSelectionListStyle(SelectionListStyle.DROPDOWNLIST.stringVal());
		groupConfig.addComponent(needReport);
		
		AttributeConfig makeNeedFAE = configFactory.newAttributeConfig(ClassificationNodeConfig.MAKE_NEED_FAE, WTMessage.getLocalizedMessage("com.catl.part.classification.resource.CATLNodeConfigRB", "MAKE_NEED_FAE"));
		makeNeedFAE.setIncludeBlankOption(false);
		makeNeedFAE.setInputFieldType(InputFieldType.SINGLE_LINE.stringVal());
		makeNeedFAE.setSelectionListStyle(SelectionListStyle.DROPDOWNLIST.stringVal());
		groupConfig.addComponent(makeNeedFAE);
		
		AttributeConfig buyNeedFAE = configFactory.newAttributeConfig(ClassificationNodeConfig.BUY_NEED_FAE, WTMessage.getLocalizedMessage("com.catl.part.classification.resource.CATLNodeConfigRB", "BUY_NEED_FAE"));
		buyNeedFAE.setIncludeBlankOption(false);
		buyNeedFAE.setInputFieldType(InputFieldType.SINGLE_LINE.stringVal());
		buyNeedFAE.setSelectionListStyle(SelectionListStyle.DROPDOWNLIST.stringVal());
		groupConfig.addComponent(buyNeedFAE);
		
		AttributeConfig makeBuyNeedFAE = configFactory.newAttributeConfig(ClassificationNodeConfig.MAKE_BUY_NEED_FAE, WTMessage.getLocalizedMessage("com.catl.part.classification.resource.CATLNodeConfigRB", "MAKE_BUY_NEED_FAE"));
		makeBuyNeedFAE.setIncludeBlankOption(false);
		makeBuyNeedFAE.setInputFieldType(InputFieldType.SINGLE_LINE.stringVal());
		makeBuyNeedFAE.setSelectionListStyle(SelectionListStyle.DROPDOWNLIST.stringVal());
		groupConfig.addComponent(makeBuyNeedFAE);
		
		AttributeConfig customerNeedFAE = configFactory.newAttributeConfig(ClassificationNodeConfig.CUSTOMER_NEED_FAE, WTMessage.getLocalizedMessage("com.catl.part.classification.resource.CATLNodeConfigRB", "CUSTOMER_NEED_FAE"));
		customerNeedFAE.setIncludeBlankOption(false);
		customerNeedFAE.setInputFieldType(InputFieldType.SINGLE_LINE.stringVal());
		customerNeedFAE.setSelectionListStyle(SelectionListStyle.DROPDOWNLIST.stringVal());
		groupConfig.addComponent(customerNeedFAE);
		
		AttributeConfig virtualNeedFAE = configFactory.newAttributeConfig(ClassificationNodeConfig.VIRTUAL_NEED_FAE, WTMessage.getLocalizedMessage("com.catl.part.classification.resource.CATLNodeConfigRB", "VIRTUAL_NEED_FAE"));
		virtualNeedFAE.setIncludeBlankOption(false);
		virtualNeedFAE.setInputFieldType(InputFieldType.SINGLE_LINE.stringVal());
		virtualNeedFAE.setSelectionListStyle(SelectionListStyle.DROPDOWNLIST.stringVal());
		groupConfig.addComponent(virtualNeedFAE);
		
		return panelConfig;
	}

}
