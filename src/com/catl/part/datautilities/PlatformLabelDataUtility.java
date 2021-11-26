package com.catl.part.datautilities;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.dataUtilities.DefaultDataUtility;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;
import com.ptc.core.components.rendering.guicomponents.Label;


public class PlatformLabelDataUtility extends DefaultDataUtility{
	private static Logger logger=Logger.getLogger(PlatformLabelDataUtility.class.getName());
	@Override
	public Object getDataValue(String componentId, Object datum, ModelContext modelContext) throws WTException {
        Label label=new Label("12");
        GUIComponentArray array = new GUIComponentArray();
        logger.info("1122121");
        System.out.println("asadas");
        label.addJsAction("onload", "alert(1)");
        array.addGUIComponent(label);
        return array;
	}
}
