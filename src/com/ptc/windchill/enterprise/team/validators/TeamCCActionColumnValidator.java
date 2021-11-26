package com.ptc.windchill.enterprise.team.validators;

import com.ptc.core.ui.validation.DefaultUIComponentValidator;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationResult;
import com.ptc.core.ui.validation.UIValidationResultSet;
import com.ptc.core.ui.validation.UIValidationStatus;
import com.ptc.windchill.enterprise.team.server.TeamCCHelper;
import com.ptc.windchill.enterprise.team.validators.TeamCCValidator;
import java.util.Locale;
import org.apache.log4j.Logger;
import wt.access.AccessControlHelper;
import wt.access.AccessPermission;
import wt.doc.WTDocument;
import wt.fc.WTObject;
import wt.fc.WTReference;
import wt.fc.delete.DeleteHelper;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamManaged;
import wt.lifecycle.LifeCycleManaged;
import wt.log4j.LogR;
import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.team.WTRoleHolder2;
import wt.util.WTException;

public class TeamCCActionColumnValidator extends DefaultUIComponentValidator {
	private Logger logger = LogR.getLogger(TeamCCValidator.class.getName());

	public UIValidationResultSet performLimitedPreValidation(UIValidationKey arg0, UIValidationCriteria arg1,
			Locale arg2) throws WTException {
		System.out.println("11111111111111111111111111111111111111111111");
		this.logger.debug("ENTERING TeamCCActionColumnValidator.performLimitedPreValidation");
		this.logger.trace("  validtionKey -> " + arg0);
		this.logger.trace("  validationCriteria -> " + arg1.toString());
		System.out.println("222222222222"+arg1.getContextObject());
		System.out.println("333333333333"+arg1.getContextObject_nonPersistable());
		UIValidationResultSet arg3 = null;
		WTReference arg4 = arg1.getContextObject();
		if (arg4 == null) {
			arg3 = super.performFullPreValidation(arg0, arg1, arg2);
			return arg3;
		} else {
			WTObject arg6 = (WTObject) arg4.getObject();
			Class arg7 = arg6.getClass();
			Object arg8 = null;
			boolean arg9 = true;
			WTRoleHolder2 arg10 = TeamCCHelper.getTeamFromObject(arg6);
			if (arg6 instanceof ContainerTeamManaged) {
				arg8 = (ContainerTeamManaged) arg6;
				ContainerTeam arg11 = null;
				WTRoleHolder2 arg12 = null;
				if (arg10 instanceof ContainerTeam) {
					arg11 = (ContainerTeam) arg10;
					if (!arg11.isShared()) {
						arg12 = TeamCCHelper.getSharedTeamFromObject(arg8);
					}
				}

				arg9 = this.isModifiableView(arg6, arg11, arg12);
			} else if (arg6 instanceof LifeCycleManaged) {
				arg8 = ((WTContained) arg6).getContainer();
				if (TeamCCHelper.isAllPeopleView(TeamCCHelper.getCurrentView(arg6))) {
					arg9 = false;
				}
			} else {
				if (!(arg6 instanceof ContainerTeam)) {
					arg3 = super.performFullPreValidation(arg0, arg1, arg2);
					return arg3;
				}

				arg8 = ((ContainerTeam) arg6).getContainer();
				if (TeamCCHelper.isAllPeopleView(TeamCCHelper.getCurrentView(arg6))) {
					arg9 = false;
				}
			}

			WTContainerRef arg15 = WTContainerRef.newWTContainerRef((WTContainer) arg8);
			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			boolean arg16 = WTContainerHelper.service.isAdministrator(arg15,
					(WTUser) SessionHelper.manager.getPrincipal());
			boolean arg13 = DeleteHelper.isMarkedForDelete(arg10);
			boolean arg14 = AccessControlHelper.manager.hasAccess(arg10, AccessPermission.MODIFY) && !arg13;
			UIValidationResult arg5;
			if ((arg16 || arg14) && arg9) {
				if(arg6 instanceof WTDocument && !arg16){
					arg5 = UIValidationResult.newInstance(arg0, UIValidationStatus.HIDDEN, arg4);
					arg3 = UIValidationResultSet.newInstance();
					arg3.addResult(arg5);
					return arg3;
				}else{
					arg5 = UIValidationResult.newInstance(arg0, UIValidationStatus.ENABLED, arg4);
					arg3 = UIValidationResultSet.newInstance();
					arg3.addResult(arg5);
					return arg3;
				}
			} else {
				arg5 = UIValidationResult.newInstance(arg0, UIValidationStatus.HIDDEN, arg4);
				arg3 = UIValidationResultSet.newInstance();
				arg3.addResult(arg5);
				return arg3;
			}
		}
	}

	protected boolean isModifiableView(WTObject arg0, ContainerTeam arg1, WTRoleHolder2 arg2) throws WTException {
		boolean arg3 = TeamCCHelper.hasLocalTeam(arg1, arg2);
		boolean arg4 = TeamCCHelper.isAllPeopleView(TeamCCHelper.getCurrentView(arg0));
		boolean arg5 = TeamCCHelper.isSharedView(TeamCCHelper.getCurrentView(arg0));
		return !arg5 && !arg4 && arg3;
	}
}