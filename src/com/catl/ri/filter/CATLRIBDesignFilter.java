package com.catl.ri.filter;

import wt.access.AccessControlHelper;
import wt.access.AccessPermission;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.folder.Folder;
import wt.org.WTPrincipal;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.catl.pd.constant.ConstantPD;
import com.catl.ri.constant.ConstantRI;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class CATLRIBDesignFilter extends DefaultSimpleValidationFilter {

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
				if((containertype.equals("Product")||containertype.equals("产品"))&&(folder.getFolderPath().contains(ConstantRI.DOC_FOLDER_NAMEB.replace("/", "")))){
					if(folder.getDescription().contains(ConstantRI.foderb_description)){
						status=UIValidationStatus.ENABLED;
					}
				}
		}else if(persistable instanceof WTDocument){
			WTDocument wtDoc=(WTDocument) persistable;
			TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(wtDoc);
			String type = ti.getTypename();
			String containertype=wtDoc.getContainer().getType();
			System.out.println("--#--"+containertype+"--"+type);
			if((containertype.equals("Product")||containertype.equals("产品"))&&type.contains(ConstantRI.WTDOCUMENT_TYPEB)){
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
