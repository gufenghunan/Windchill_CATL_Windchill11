package com.catl.battery.filter;

import wt.access.AccessControlHelper;
import wt.access.AccessPermission;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.folder.Folder;
import wt.org.WTPrincipal;
import wt.part.WTPart;
import wt.project.Role;
import wt.session.SessionHelper;
import wt.util.WTException;

import java.util.List;

import com.catl.battery.constant.ConstantBattery;
import com.catl.bom.workflow.BomWfUtil;
import com.catl.pd.constant.ConstantPD;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class CATLCostCoefficientFilter extends DefaultSimpleValidationFilter {

	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key,
			UIValidationCriteria criteria) {
		UIValidationStatus status=UIValidationStatus.HIDDEN;
		Persistable persistable=criteria.getContextObject().getObject();
		try {
			WTPrincipal user = SessionHelper.getPrincipal();
			if(persistable instanceof WTPart){
				WTPart part = (WTPart) persistable;
				String username = user.getName();
				List<String> userList1 = BomWfUtil.getContainerPri(Role.toRole("成本工程师的内部名称"), part.getContainer());
				List<String> userList2 = BomWfUtil.getContainerPri(Role.toRole("电芯工程师的内部名称"), part.getContainer());
				if(userList1.contains(username)||userList2.contains(username)) {
					status=UIValidationStatus.ENABLED;
				}
			}
		} catch (WTException e1) {
			e1.printStackTrace();
			
		}
		return status;
	}
}
