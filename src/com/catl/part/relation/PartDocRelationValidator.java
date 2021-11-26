package com.catl.part.relation;

import java.rmi.RemoteException;

import org.apache.log4j.Logger;

import wt.log4j.LogR;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class PartDocRelationValidator extends DefaultSimpleValidationFilter
{
	private static final Logger LOGGER = LogR.getLogger(PartDocRelationValidator.class.getName());

	/**
	 * Check current user access to remove link between part and doc
	 * 
	 */
	public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria)
	{
		WTUser currentUser = null;
		String currentUserName = "";
		try
		{
			currentUser = (WTUser) SessionHelper.manager.getPrincipal();
			currentUserName = currentUser.getName();
		} catch (WTException e)
		{
			LOGGER.error(e.getMessage(), e);
		}
		
		UIValidationStatus status = UIValidationStatus.HIDDEN;
		Object object = criteria.getContextObject().getObject();
		String typeID = "";
		try
		{
			typeID = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(object);
		} catch (RemoteException e)
		{
			LOGGER.error(e.getMessage(), e);
		} catch (WTException e)
		{
			LOGGER.error(e.getMessage(), e);
		}
		if (typeID.indexOf("CATLPart") > -1)
		{
			String createName = ((WTPart) object).getCreatorName();
			LOGGER.info("create name is==" + createName + ";current user name is====" + currentUserName);
			if (createName.equals(currentUserName))
			{
				status = UIValidationStatus.ENABLED;
			}else {
				status = UIValidationStatus.ENABLED;
			}
		}
		return status;
	}
}
