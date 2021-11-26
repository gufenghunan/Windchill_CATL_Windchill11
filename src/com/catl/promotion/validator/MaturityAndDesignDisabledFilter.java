package com.catl.promotion.validator;

import org.apache.commons.lang.StringUtils;

import com.catl.common.constant.PartState;
import com.catl.common.constant.TypeName;
import com.catl.common.util.IBAUtil;
import com.catl.common.util.TypeUtil;
import com.catl.part.PartConstant;
import com.catl.promotion.util.PromotionUtil;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

import wt.fc.Persistable;
import wt.folder.Folder;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.util.WTException;

public class MaturityAndDesignDisabledFilter extends DefaultSimpleValidationFilter {

	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
		UIValidationStatus uivs = UIValidationStatus.HIDDEN;
		Persistable persistable = criteria.getContextObject().getObject();
		if(persistable instanceof WTPart){
			WTPart part = (WTPart)persistable;
			try {
				if(TypeUtil.isSpecifiedType(part, TypeName.CATLPart)){
					String action = key.getComponentID();
					if(StringUtils.equals(action, "createFAEMaterialsMaturity")){
						if(checkFAEObject(part)){
							return UIValidationStatus.ENABLED;
						}
						else {
							return UIValidationStatus.DISABLED;
						}
					}
					else if(StringUtils.equals(action, "createDisabledForDesignPN")){
						if(checkDisabledForDesignObject(part)){
							return UIValidationStatus.ENABLED;
						}
						else {
							return UIValidationStatus.DISABLED;
						}
					}
				}
			} catch (WTException e) {
				e.printStackTrace();
			}
		}
		else if(persistable instanceof Folder){
			return super.preValidateAction(key, criteria);
		}
		return uivs;
	}
	
	private boolean checkFAEObject(WTPart part){
		if(part != null){
			if (!part.getState().toString().endsWith(PartState.RELEASED)) {
				return false;
			}
			else {
				WTPart latestBigPart = (WTPart) PromotionUtil.getLatestVersionByMaster((WTPartMaster)part.getMaster());
				if(!part.equals(latestBigPart)){
					return false;
				}
				else {
					String FAEStatus = (String) IBAUtil.getIBAValue(part.getMaster(), PartConstant.IBA_CATL_FAEStatus);
					String maturity = (String) IBAUtil.getIBAValue(part.getMaster(), PartConstant.IBA_CATL_Maturity);
					if(!StringUtils.equals(FAEStatus, PartConstant.CATL_FAEStatus_2) || !StringUtils.equals(maturity, "1")){
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}
	
	private boolean checkDisabledForDesignObject(WTPart part){
		if(part != null){
			//Modified by szeng 2017-07-27, Add function allow Design State to create DisabledForDesignPN
			if (!part.getState().toString().endsWith(PartState.RELEASED)&&!part.getState().toString().endsWith(PartState.DESIGN)) {
				return false;
			}
			else {
				WTPart latestBigPart = (WTPart) PromotionUtil.getLatestVersionByMaster((WTPartMaster)part.getMaster());
				if(!part.equals(latestBigPart)){
					return false;
				}
			}
			return true;
		}
		return false;
	}

}
