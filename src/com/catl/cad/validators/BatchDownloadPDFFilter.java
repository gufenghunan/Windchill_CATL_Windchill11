package com.catl.cad.validators;

import wt.fc.Persistable;
import wt.util.WTException;

import com.catl.cad.BatchDownloadPDFUtil;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class BatchDownloadPDFFilter extends DefaultSimpleValidationFilter {

	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key,
			UIValidationCriteria criteria) {
		Persistable persistable = criteria.getContextObject().getObject();
		try {
			if(BatchDownloadPDFUtil.checkBatchDownloadPDF(persistable)){
				System.out.println("=====ENABLED=====");
				return UIValidationStatus.ENABLED;
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		System.out.println("=====HIDDEN=====");
		return UIValidationStatus.HIDDEN;
	}

}
