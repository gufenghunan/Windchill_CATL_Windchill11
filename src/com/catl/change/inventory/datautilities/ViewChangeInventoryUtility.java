package com.catl.change.inventory.datautilities;

import org.apache.log4j.Logger;

import wt.log4j.LogR;
import wt.part.WTPart;
import wt.util.WTException;

import com.catl.change.inventory.DispositionOption;
import com.catl.change.inventory.ECAPartLink;
import com.catl.change.inventory.MaterialStatus;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.dataUtilities.DefaultDataUtility;
import com.ptc.core.components.rendering.guicomponents.TextDisplayComponent;
/**
 *  the datautility table builder: changeTask.viewChangeInventory
 * @author admin
 *
 */
public class ViewChangeInventoryUtility extends DefaultDataUtility {

	private static Logger logger = LogR.getLogger(ViewChangeInventoryUtility.class.getName());
	
	@Override
	public Object getDataValue(String s, Object obj, ModelContext modelcontext)
			throws WTException {
		logger.debug("ViewChangeInventoryUtility getDataValue in===============");
		logger.debug("s:::"+s);
		logger.debug("obj:::"+obj);
		logger.debug("modelcontext:::"+modelcontext);
		logger.debug("modelcontext mode:::"+modelcontext.getDescriptorMode());
		String value = "";
		if(obj instanceof ECAPartLink){
			ECAPartLink link = (ECAPartLink) obj;
			if(link!=null){
				WTPart part = link.getPart();
				if ("partNumber".equals(s)){
					value = part.getNumber();
				}else if ("partName".equals(s)) {
					value = part.getName();
				}else if (ECAPartLink.QUANTITY.equals(s)) {
					value = String.valueOf(link.getQuantity());
				}else if (ECAPartLink.OWNER.equals(s)) {
					value = link.getOwner();
				}else if (ECAPartLink.MATERIAL_STATUS.equals(s)){
					MaterialStatus mats = link.getMaterialStatus();
					if(mats!=null){
						value = mats.getDisplay(modelcontext.getLocale());
					}
				}else if (ECAPartLink.DISPOSITION_OPTION.equals(s)){
					DispositionOption diso = link.getDispositionOption();
					if(diso!=null){
						value = diso.getDisplay(modelcontext.getLocale());
					}
				}
				logger.debug("value:::"+value);
			}
		}
		TextDisplayComponent text = new TextDisplayComponent(value);
		text.setId(s);
		text.setName(s);
		text.setValue(value);
		logger.debug("ViewChangeInventoryUtility getDataValue out===============");
		return text;
	}
	
}
