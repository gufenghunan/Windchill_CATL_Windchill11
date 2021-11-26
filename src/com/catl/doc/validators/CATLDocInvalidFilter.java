package com.catl.doc.validators;

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

import com.catl.common.constant.PartState;
import com.catl.common.util.DocUtil;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class CATLDocInvalidFilter extends DefaultSimpleValidationFilter {

	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
		UIValidationStatus status = UIValidationStatus.HIDDEN;
		try {
			Persistable persistable = criteria.getContextObject().getObject();
//			WTPrincipal principal = SessionHelper.getPrincipal();
			if(persistable instanceof WTDocument){
				WTDocument doc = (WTDocument)persistable;
				if(DocUtil.isLastedWTDocument(doc)){
					
					Properties wtproperties = ServiceProperties.getServiceProperties("WTServiceProviderFromProperties");
					String couldVoidType = wtproperties.getProperty("couldVoidType");
					List<String> doctypelist = new ArrayList<String>();
			        if(StringUtils.isNotEmpty(couldVoidType)){
			        	
			        	String[] docTypeArr = couldVoidType.split(",");
			        	doctypelist = Arrays.asList(docTypeArr); 
			        }
			        
			        String docNum = doc.getNumber();
			        String docType = docNum.substring(0, docNum.indexOf("-"));
			        
			        if (doctypelist.contains(docType)){
			        	
//			        	if(UserUtil.isOrgAdministator(principal) || UserUtil.isSiteAdmin(principal)){
//							return UIValidationStatus.DISABLED;
//						}
			        	
			        	if(doc.getLifeCycleState().equals(State.toState(PartState.RELEASED))){
							return UIValidationStatus.ENABLED;
						} else {
							return UIValidationStatus.DISABLED;
						}
			        }
//					WTContainer container = doc.getContainer();
//					if(container instanceof WTLibrary){
//						WTLibrary library = (WTLibrary)container;
//						String containername = library.getName();
//						if (containername.startsWith(ContainerName.BATTERY_LIBRARY_NAME)) {
//							if(!(UserUtil.isOrgAdministator(principal) || UserUtil.isSiteAdmin(principal) || UserUtil.isLibraryManager(library, principal))){
//								return UIValidationStatus.DISABLED;
//							}
//							String action = key.getComponentID();
//							String maturity = (String) IBAUtil.getIBAValue(part.getMaster(), PartConstant.IBA_CATL_Maturity);
//							if(!part.getLifeCycleState().equals(State.toState(PartState.RELEASED))){
//								return UIValidationStatus.DISABLED;
//							}
//							if((StringUtils.equals(action, "maturity1To3") && StringUtils.equals(maturity, "1"))||(StringUtils.equals(action, "maturity3To6") && StringUtils.equals(maturity, "3"))){
//								return UIValidationStatus.ENABLED;
//							}
//							else {
//								return UIValidationStatus.DISABLED;
//							}
//						}
//					}
				}
			} else if(persistable instanceof Folder){
				return super.preValidateAction(key, criteria);
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return status;
	}

}
