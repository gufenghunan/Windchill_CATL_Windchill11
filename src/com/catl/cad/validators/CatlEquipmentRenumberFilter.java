package com.catl.cad.validators;

import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.folder.Folder;
import wt.inf.container.WTContainer;
import wt.util.WTException;

import com.catl.bom.cad.CatlEquipmentCADRenumber;
import com.catl.cad.BatchDownloadPDFUtil;
import com.catl.common.constant.PartState;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class CatlEquipmentRenumberFilter extends DefaultSimpleValidationFilter {

	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key,
			UIValidationCriteria criteria) {
		Persistable persistable = criteria.getContextObject().getObject();
		if(persistable instanceof EPMDocument){
			EPMDocument epm = (EPMDocument) persistable;
			if(CatlEquipmentCADRenumber.isEquipmentCAD(epm)){
				if(epm.getLifeCycleState().toString().equalsIgnoreCase(PartState.DESIGN)||epm.getLifeCycleState().toString().equalsIgnoreCase(PartState.DESIGNMODIFICATION)){
					return UIValidationStatus.ENABLED;
				}
			}
		}else if(persistable instanceof Folder){
			Folder folder = (Folder) persistable;
			WTContainer container = folder.getContainer();
			if(container != null){
				if(container.getName().equalsIgnoreCase("设备开发标准件库")){
					return UIValidationStatus.ENABLED;
				}
			}
		}
	
		return UIValidationStatus.HIDDEN;
	}

}
