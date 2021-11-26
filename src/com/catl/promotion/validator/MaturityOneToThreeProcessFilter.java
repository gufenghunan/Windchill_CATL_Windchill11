package com.catl.promotion.validator;

import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.catl.common.constant.ContainerName;
import com.catl.common.constant.PartState;
import com.catl.common.constant.TypeName;
import com.catl.common.util.IBAUtil;
import com.catl.common.util.PartUtil;
import com.catl.part.PartConstant;
import com.catl.promotion.util.PromotionConst;
import com.catl.promotion.util.PromotionUtil;
import com.catl.promotion.util.WorkflowUtil;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

import wt.fc.Persistable;
import wt.inf.container.WTContainer;
import wt.inf.library.WTLibrary;
import wt.org.WTPrincipal;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTException;

public class MaturityOneToThreeProcessFilter extends DefaultSimpleValidationFilter {

	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
		UIValidationStatus status = UIValidationStatus.HIDDEN;
		try {
			Persistable persistable = criteria.getContextObject().getObject();
			WTPrincipal user = SessionHelper.getPrincipal();
			if(persistable instanceof WTPart){
				WTPart part = (WTPart)persistable;
				String type = WorkflowUtil.getTypeInternalName(part);
				if (StringUtils.equals(type, TypeName.CATLPart)) {
					if(PartUtil.isLastedWTPart(part)){
						WTContainer container = part.getContainer();
						if(container instanceof WTLibrary){
							WTLibrary library = (WTLibrary)container;
							String containername = library.getName();
							if (!containername.startsWith(ContainerName.BATTERY_LIBRARY_NAME)) {
								Set<WTPrincipal> users = PromotionUtil.getRoleMember(part, PromotionConst.DESIGNER);
								if (users.contains(user)) {
									String FAEStatus = (String) IBAUtil.getIBAValue(part.getMaster(), PartConstant.IBA_CATL_FAEStatus);
									String maturity = (String) IBAUtil.getIBAValue(part.getMaster(), PartConstant.IBA_CATL_Maturity);
									if (StringUtils.equals(FAEStatus, PartConstant.CATL_FAEStatus_1) && StringUtils.equals(maturity, "1")) {
										if (part.getState().toString().endsWith(PartState.RELEASED)) {
											status = UIValidationStatus.ENABLED;
										}
										else {
											status = UIValidationStatus.DISABLED;
										}
									}
									else {
										status = UIValidationStatus.DISABLED;
									}
								}
								else {
									status = UIValidationStatus.DISABLED;
								}
							}
						} else {
							Set<WTPrincipal> users = PromotionUtil.getRoleMember(part, PromotionConst.DESIGNER);
							if (users.contains(user)) {
								String FAEStatus = (String) IBAUtil.getIBAValue(part.getMaster(), PartConstant.IBA_CATL_FAEStatus);
								String maturity = (String) IBAUtil.getIBAValue(part.getMaster(), PartConstant.IBA_CATL_Maturity);
								if (StringUtils.equals(FAEStatus, PartConstant.CATL_FAEStatus_1) && StringUtils.equals(maturity, "1")) {
									if (part.getState().toString().endsWith(PartState.RELEASED)) {
										status = UIValidationStatus.ENABLED;
									}
									else {
										status = UIValidationStatus.DISABLED;
									}
								}
								else {
									status = UIValidationStatus.DISABLED;
								}
							}
							else {
								status = UIValidationStatus.DISABLED;
							}
						}
					}
				}				
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return status;
	}
}
