package com.catl.doc.soft.validators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.folder.Folder;
import wt.lifecycle.State;
import wt.services.applicationcontext.implementation.ServiceProperties;
import wt.util.WTException;
import wt.vc.wip.WorkInProgressHelper;

import com.catl.common.constant.PartState;
import com.catl.common.constant.TypeName;
import com.catl.common.util.DocUtil;
import com.catl.ecad.utils.ECADConst;
import com.catl.ecad.utils.ECADutil;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class CATLSoftDocAttachmentsEditFilter extends DefaultSimpleValidationFilter {

	public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
		UIValidationStatus status = UIValidationStatus.DISABLED;
		try {
			//System.out.println("111111111111111111112222222222233");
			String componentID = key.getComponentID();
			//System.out.println("121111111111111\t"+componentID);
			Persistable persistable = criteria.getContextObject().getObject();
//			WTPrincipal principal = SessionHelper.getPrincipal();
			if(persistable instanceof WTDocument){
				WTDocument doc = (WTDocument)persistable;
				if(componentID.equalsIgnoreCase("edit_soft_attachments")){
					if(WorkInProgressHelper.isCheckedOut(doc)){
						status = UIValidationStatus.ENABLED;
					}
				}else if(componentID.equalsIgnoreCase("softAttachmentsTable")){
					if(isSoftDocument(doc)){
						status = UIValidationStatus.ENABLED;
					}else{
						status = UIValidationStatus.HIDDEN;
					}
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return status;
	}

	
	/**
	 * 判断文档是否为PCBA装配图文档
	 * 
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public static boolean isSoftDocument(WTDocument doc) throws WTException {
		if (doc != null) {
			String type = ECADutil.getStrSplit(doc);
			if (type.equalsIgnoreCase(TypeName.softDoc)||type.equalsIgnoreCase(TypeName.softwareDoc)) {
				return true;
			}
		}
		return false;
	}
}
