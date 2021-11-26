package com.catl.integration.rdm.validators;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.json.JSONException;

import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.util.WTException;

import com.catl.integration.rdm.RDMMenuUtil;
import com.catl.integration.rdm.bean.MenuBean;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class HideByUserFilter extends DefaultSimpleValidationFilter
{
    static Logger logger = Logger.getLogger(HideByUserFilter.class);
    
    
    public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria)
 {
		logger.debug("ENTERING HideByUserFilter.preValidateAction");
		logger.debug("  validtionKey -> " + key);
		logger.debug("  validationCriteria -> " + criteria.toString());
		if (logger.isTraceEnabled()) {
			logger.trace("*****");
		}
		UIValidationStatus status = UIValidationStatus.HIDDEN;
		String validateMenuList = "";
//		String userName = "yang";
		String userName = "";
		
		HashMap<String, MenuBean> map;
		try {
			WTPrincipal currentUser = (WTUser) wt.session.SessionHelper.manager.getPrincipal();
			userName = currentUser.getName();
			map = RDMMenuUtil.getMenuMap(userName);
		
			for (Entry<String, MenuBean> entry : map.entrySet()) {
				if (validateMenuList.length() == 0) {
					validateMenuList = entry.getKey();
				} else {
					validateMenuList = validateMenuList + "|" + entry.getKey();
				}
			}
			
			if (validateMenuList.length() == 0) {
				return status;
			}
			
			String compId = key.getComponentID();
			logger.debug("  getObjectType -> " + key.getObjectType());
			logger.debug("  compID -> " + compId);
			if (validateMenuList.contains(compId)) {
				status = UIValidationStatus.ENABLED;
			}
			
			 logger.trace("RETURNING " + status);
		     logger.debug("EXITING HideByUserFilter.preValidateAction");
			return status;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
