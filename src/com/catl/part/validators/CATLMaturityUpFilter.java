package com.catl.part.validators;

import org.apache.commons.lang.StringUtils;

import com.catl.common.constant.ContainerName;
import com.catl.common.constant.PartState;
import com.catl.common.util.IBAUtil;
import com.catl.common.util.PartUtil;
import com.catl.common.util.UserUtil;
import com.catl.part.PartConstant;
import com.catl.part.processors.ReleaseSoftPartProcessor;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.inf.container.WTContainer;
import wt.inf.library.WTLibrary;
import wt.lifecycle.State;
import wt.org.WTPrincipal;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTException;

public class CATLMaturityUpFilter extends DefaultSimpleValidationFilter {

	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
		UIValidationStatus status = UIValidationStatus.HIDDEN;
		try {
			Persistable persistable = criteria.getContextObject().getObject();
			WTPrincipal principal = SessionHelper.getPrincipal();
			if(persistable instanceof WTPart){
				WTPart part = (WTPart)persistable;
				if(PartUtil.isLastedWTPart(part)){
					if(PartUtil.isSWPart(part)){
						String action = key.getComponentID();
						if(StringUtils.equals(action, "releaseSoftPart")){
							if(!part.getLifeCycleState().equals(State.toState(PartState.DESIGN))){
								return UIValidationStatus.DISABLED;
							}else{
								WTDocument doc = ReleaseSoftPartProcessor.getEmptySoftPack(part);
								if(doc == null){
									return UIValidationStatus.DISABLED;
								}else{
									if(!doc.getLifeCycleState().equals(State.toState(PartState.DESIGN))){
										return UIValidationStatus.DISABLED;
									}else{
										return UIValidationStatus.ENABLED;
									}
								}
							}
						}else if(StringUtils.equals(action, "reviseSoftPart")){
							if(!part.getLifeCycleState().equals(State.toState(PartState.RELEASED))){
								return UIValidationStatus.HIDDEN;
							}else{
								WTDocument doc = ReleaseSoftPartProcessor.getEmptySoftPack(part);
								if(doc == null){
									return UIValidationStatus.DISABLED;
								}else{
									if(!doc.getLifeCycleState().equals(State.toState(PartState.RELEASED))){
										return UIValidationStatus.DISABLED;
									}else{
										return UIValidationStatus.ENABLED;
									}
								}
							}
						}
					}
					WTContainer container = part.getContainer();
					if(container instanceof WTLibrary){
						WTLibrary library = (WTLibrary)container;
						String containername = library.getName();
						if (containername.startsWith(ContainerName.BATTERY_LIBRARY_NAME)) {
							if(!(UserUtil.isOrgAdministator(principal) || UserUtil.isSiteAdmin(principal) || UserUtil.isLibraryManager(library, principal))){
								return UIValidationStatus.DISABLED;
							}
							String action = key.getComponentID();
							String maturity = (String) IBAUtil.getIBAValue(part.getMaster(), PartConstant.IBA_CATL_Maturity);
							if(!part.getLifeCycleState().equals(State.toState(PartState.RELEASED))){
								return UIValidationStatus.DISABLED;
							}
							if((StringUtils.equals(action, "maturity1To3") && StringUtils.equals(maturity, "1"))||(StringUtils.equals(action, "maturity3To6") && StringUtils.equals(maturity, "3"))){
								return UIValidationStatus.ENABLED;
							}
							else {
								return UIValidationStatus.DISABLED;
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
