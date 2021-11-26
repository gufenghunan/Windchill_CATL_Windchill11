package com.catl.promotion.validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.catl.common.constant.RoleName;
import com.catl.common.constant.TypeName;
import com.catl.common.util.TypeUtil;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

import wt.fc.Persistable;
import wt.inf.container.WTContainerHelper;
import wt.log4j.LogR;
import wt.maturity.PromotionNotice;
import wt.method.RemoteAccess;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.project.Role;
import wt.session.SessionHelper;
import wt.team.Team;
import wt.team.TeamException;
import wt.team.TeamHelper;
import wt.util.WTException;

public class PromotableFilter extends DefaultSimpleValidationFilter implements RemoteAccess
{
	private static final String CLASSNAME = PromotableFilter.class.getName();

	private static Logger LOGGER = LogR.getLogger(CLASSNAME);

	public static final String[] EDITABLE_STATE = { "INWORK", "REWORK" };

	private static List<String> STATE_PROMOTIONNOTICELIST = new ArrayList<String>();

	static
	{
		STATE_PROMOTIONNOTICELIST.addAll(Arrays.asList(EDITABLE_STATE));
	}

	public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria)
	{
		LOGGER.trace("Promote Filter Executed");
		UIValidationStatus status = UIValidationStatus.DISABLED;
		Persistable persistable = criteria.getContextObject().getObject();
		if (persistable instanceof PromotionNotice)
		{
			PromotionNotice pNotice = (PromotionNotice) persistable;
			
			try
			{
				if(TypeUtil.isSpecifiedType(pNotice, TypeName.designPromotion) || TypeUtil.isSpecifiedType(pNotice, TypeName.UpgradeFaePartMaturityPN)){
					return UIValidationStatus.HIDDEN;
				}
				if (isSiteAdmin(SessionHelper.manager.getPrincipal())){
					status = UIValidationStatus.ENABLED;
					return status;
				}
				String stateKey = pNotice.getLifeCycleState().toString();
				if (STATE_PROMOTIONNOTICELIST.contains(stateKey))
				{
					if (SessionHelper.getPrincipal().equals(pNotice.getCreator().getObject()))
					{
						status = UIValidationStatus.ENABLED;
						return status;
					}
					if (isDesignerRole(pNotice))
					{
						status = UIValidationStatus.ENABLED;
						return status;
					}
				}
			} catch (Exception e)
			{
				LOGGER.error(e.getMessage(), e);
			}
		}
		LOGGER.trace("Promote Filter status =" + status.getStringValue());
		return status;
	}

	private boolean isDesignerRole(PromotionNotice pn) throws TeamException, WTException
	{
		Team team = TeamHelper.service.getTeam(pn);
		if (team == null)
			return false;
		Vector allRoles = TeamHelper.service.findRoles(team);
		HashMap rolePrincipalListMap = TeamHelper.service.findAllParticipantsByRole(team);
		for (int i = 0; allRoles != null && i < allRoles.size(); i++)
		{
			Role role = Role.toRole(RoleName.DESIGNER);
			if (allRoles.get(i).equals(role))
			{
				ArrayList principalList = (ArrayList) rolePrincipalListMap.get(allRoles.get(i));
				for (int j = 0; principalList != null && j < principalList.size(); j++)
				{
					WTPrincipal principal = ((WTPrincipalReference) principalList.get(j)).getPrincipal();
					if (SessionHelper.getPrincipal().equals(principal))
					{
						return true;
					}
				}
				break;
			}
		}
		return false;
	}
	public static boolean isSiteAdmin(WTPrincipal wtPrincipal) {
        try {
            return WTContainerHelper.service.isAdministrator(WTContainerHelper.service.getExchangeRef(), wtPrincipal);
        } catch (WTException e) {
            e.printStackTrace();
        }
        return false;
    }
}
