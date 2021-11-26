package com.catl.ecad.mvc.builders;

import com.ptc.mvc.components.AttributePanelConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentBuilderType;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.GroupConfig;
import com.ptc.xworks.xmlobject.web.ObjectGuiComponentBuildContext;
import com.ptc.xworks.xmlobject.web.XmlObjectAttributePanelBuilder;

/**
 * 元器件建库流程
 * 
 * @author plm09
 *
 */
@ComponentBuilder(value = {
		"com.catl.ecad.mvc.builders.CadenceAttributeBuilder" }, type = ComponentBuilderType.CONFIG_AND_DATA)
public class CadenceAttributeBuilder extends XmlObjectAttributePanelBuilder {

	@Override
	public AttributePanelConfig buildAttributePanelConfig(ComponentParams params,
			ObjectGuiComponentBuildContext buildContext) throws Exception {
		ComponentConfigFactory factory = getComponentConfigFactory();
		AttributePanelConfig panel1 = factory.newAttributePanelConfig();
		System.out.println("CadenceAttributeBuilder_____________");
		// itemdec
		Object itemdec1 = params.getParameter("itemdec");
		

		if (itemdec1 != null) {
			String itemdec = itemdec1.toString();

			GroupConfig group1 = factory.newGroupConfig();
			group1.setId("com.catl.ecad.relateinfo");
			group1.setLabel("Cadence相关信息");
			group1.addComponent(buildAttributConfig(buildContext, "schematic_part", 1, 1, 1));
			group1.addComponent(buildAttributConfig(buildContext, "old_Footprint", 1, 2, 1));
			group1.addComponent(buildAttributConfig(buildContext, "alt_Symbols", 2, 1, 1));
			group1.addComponent(buildAttributConfig(buildContext, "edadoc_Footprint", 2, 2, 1));
			group1.addComponent(buildAttributConfig(buildContext, "ipc7351_Footprint_A_Maximum", 3, 1, 1));
			group1.addComponent(buildAttributConfig(buildContext, "ipc7351_Footprint_B_Normal", 3, 2, 1));
			group1.addComponent(buildAttributConfig(buildContext, "ipc7351_Footprint_C_Minimum", 4, 1, 1));
			group1.addComponent(buildAttributConfig(buildContext, "vibration_Footprint", 4, 2, 1));
			panel1.addComponent(group1);
		}
		return panel1;
	}

}
