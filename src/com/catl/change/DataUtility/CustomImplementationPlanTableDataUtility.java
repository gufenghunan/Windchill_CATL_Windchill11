package com.catl.change.DataUtility;

import wt.change2.WTChangeActivity2;
import wt.util.WTException;

import com.catl.common.constant.TypeName;
import com.catl.common.util.IBAUtil;
import com.catl.part.PartConstant;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.rendering.guicomponents.TextDisplayComponent;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.windchill.enterprise.change2.dataUtilities.ImplementationPlanTableDataUtility;

public class CustomImplementationPlanTableDataUtility extends ImplementationPlanTableDataUtility {

	@Override
	public Object getDataValue(String s, Object obj, ModelContext modelcontext) throws WTException {
		
		Object dateValus = super.getDataValue(s, obj, modelcontext);
		if (obj instanceof WTChangeActivity2) {
			WTChangeActivity2 eco = (WTChangeActivity2)obj;
			TypeIdentifier type = TypeIdentifierUtility.getTypeIdentifier(eco);
			if (type.getTypename().endsWith(TypeName.CATL_DCA)) {
				Boolean AllowEdit = (Boolean) IBAUtil.getIBAValue(eco, PartConstant.CATL_Allow_Edit);
				if (AllowEdit != null) {
					if (!AllowEdit) {
						if (s.equals("changeTask_nmIconActions")) {
							return TextDisplayComponent.NBSP;
						}
					}
				}
			}
		}		
		return dateValus;
	}
}
