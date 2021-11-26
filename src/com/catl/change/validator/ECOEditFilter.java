package com.catl.change.validator;

import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.org.WTPrincipal;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.catl.change.ChangeUtil;
import com.catl.change.filter.CatlEditChangeTasckValidation;
import com.catl.change.util.ChangeConst;
import com.catl.common.constant.TypeName;
import com.catl.common.util.IBAUtil;
import com.catl.common.util.PartUtil;
import com.catl.part.PartConstant;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class ECOEditFilter extends DefaultSimpleValidationFilter {

	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria){
		Persistable persistable = criteria.getContextObject().getObject();
		String compentId = key.getComponentID();
		 
		try {
			//获取当前用户
			WTPrincipal userPrincipal = SessionHelper.manager.getPrincipal();
			
			if (persistable instanceof WTChangeOrder2) {
				WTChangeOrder2 eco = (WTChangeOrder2)persistable;
				String ecotype = ChangeUtil.getStrSplit(eco);
				//编辑和编辑设计更改单的场景
				if(compentId.equals("edit")){
					if(ecotype.equals(ChangeConst.CHANGEORDER_TYPE_DCN)){
						return  UIValidationStatus.HIDDEN;
					}
				}else if(compentId.equals("editdcn")){
					if(ecotype.equals(ChangeConst.CHANGEORDER_TYPE_ECN)){
						return  UIValidationStatus.HIDDEN;
					}else if(ecotype.equals(ChangeConst.CHANGEORDER_TYPE_DCN)){
						Boolean AllowEdit = (Boolean) IBAUtil.getIBAValue(eco, PartConstant.CATL_Allow_Edit);
						if (!AllowEdit) {
							if(!CatlEditChangeTasckValidation.isSiteAdmin(userPrincipal) && !CatlEditChangeTasckValidation.isOrgAdministator(userPrincipal, "CATL")){
								return  UIValidationStatus.HIDDEN;
							}
						}			
					}
				}else if(compentId.equals("deleteChangeItem")){
					if(ecotype.equals(ChangeConst.CHANGEORDER_TYPE_DCN)){
						Boolean AllowEdit = (Boolean) IBAUtil.getIBAValue(eco, PartConstant.CATL_Allow_Edit);
						if (!AllowEdit) {
							if(!CatlEditChangeTasckValidation.isSiteAdmin(userPrincipal) && !CatlEditChangeTasckValidation.isOrgAdministator(userPrincipal, "CATL")){
								return  UIValidationStatus.HIDDEN;
							}
						}
					}
				}
			}else if(persistable instanceof WTPart){
				String csdnum = (String)IBAUtil.getIBAValue(persistable, PartConstant.IBA_CATL_Maturity);
				if(!csdnum.equals("1")){
					return  UIValidationStatus.HIDDEN;
				}
				
			}else if(persistable instanceof WTDocument){
				WTDocument doc = (WTDocument)persistable;
				try {
					WTPart docpart = PartUtil.getRelationPart(doc);
					if(docpart!=null){
						String csdnum = (String)IBAUtil.getIBAValue(docpart, PartConstant.IBA_CATL_Maturity);
						if(!csdnum.equals("1")){
							return  UIValidationStatus.HIDDEN;
						}
					}
					
				} catch (WTException e) {
					e.printStackTrace();
				}
				
			}else if(persistable instanceof EPMDocument){
				EPMDocument epm = (EPMDocument)persistable;
			
				WTPart epmpart = PartUtil.getRelationPart(epm);
				if(epmpart!=null){
					String csdnum = (String)IBAUtil.getIBAValue(epmpart, PartConstant.IBA_CATL_Maturity);
					if(!csdnum.equals("1")){
						return  UIValidationStatus.HIDDEN;
					}
				}
			}
			
		} catch (WTException e) {
			e.printStackTrace();
		}
		
		return super.preValidateAction(key, criteria);
	}

}
