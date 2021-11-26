package com.catl.pd.filter;

import wt.access.AccessControlHelper;
import wt.access.AccessPermission;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.folder.Folder;
import wt.org.WTPrincipal;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.catl.pd.constant.ConstantPD;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class CATLPDDesignFilter extends DefaultSimpleValidationFilter {

	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key,
			UIValidationCriteria criteria) {
		// TODO Auto-generated method stub
		UIValidationStatus status=UIValidationStatus.HIDDEN;
		Persistable persistable=criteria.getContextObject().getObject();
		if(persistable instanceof Folder){
			Folder folder=(Folder) persistable;
			String containertype=folder.getContainer().getType();
			System.out.println("----"+containertype);
				if((containertype.equals("Product")||containertype.equals("产品"))&&folder.getName().equals(ConstantPD.DOC_FOLDER_NAME.replace("/", ""))){
					status=UIValidationStatus.ENABLED;
				}
		}else if(persistable instanceof WTDocument){
			WTDocument wtDoc=(WTDocument) persistable;
			TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(wtDoc);
			String type = ti.getTypename();
			String containertype=wtDoc.getContainer().getType();
			System.out.println("--#--"+containertype+"--"+type);
			if((containertype.equals("Product")||containertype.equals("产品"))&&type.contains(ConstantPD.WTDOCUMENT_TYPE)){
				WTPrincipal user;
				try {
					user = SessionHelper.getPrincipal();
					boolean access = AccessControlHelper.manager.hasAccess(user, wtDoc, AccessPermission.MODIFY);
					if (!access) {
						status = UIValidationStatus.HIDDEN;
					}else{
						status = UIValidationStatus.ENABLED;
					}
				} catch (WTException e) {
					e.printStackTrace();
				}
			}
		}
		return status;
	}
}
