package com.catl.change.validator;

import java.util.ArrayList;
import java.util.List;

import wt.change2.WTChangeActivity2;
import wt.enterprise.RevisionControlled;
import wt.fc.Persistable;
import wt.part.WTPart;
import wt.util.WTException;

import com.catl.change.ChangeUtil;
import com.catl.common.constant.PartState;
import com.catl.common.constant.TypeName;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class CATLChangeRequestFilter extends DefaultSimpleValidationFilter {

	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
		Persistable persistable = criteria.getContextObject().getObject();
		if (persistable instanceof RevisionControlled) {
			RevisionControlled revisionControlled = (RevisionControlled) persistable;
			StringBuffer message = new StringBuffer();
			List<WTPart> retListPart = new ArrayList<WTPart>();
			try {
				ChangeUtil.checkMaturityType(message,retListPart,revisionControlled);
			} catch (WTException e) {
				e.printStackTrace();
				return UIValidationStatus.DISABLED;
			}
			if(message.length() > 0){
				return UIValidationStatus.DISABLED;
			}

		}
		return UIValidationStatus.ENABLED;
	}
}
