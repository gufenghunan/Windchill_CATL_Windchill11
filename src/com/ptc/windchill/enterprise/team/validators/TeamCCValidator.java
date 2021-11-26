package com.ptc.windchill.enterprise.team.validators;

import com.ptc.core.ui.validation.DefaultUIComponentValidator;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationResult;
import com.ptc.core.ui.validation.UIValidationResultSet;
import com.ptc.core.ui.validation.UIValidationStatus;
import com.ptc.netmarkets.roleAccess.NmRoleAccessHelper;
import com.ptc.windchill.enterprise.team.server.TeamCCHelper;
import java.util.Locale;
import java.util.Map;
import org.apache.log4j.Logger;
import wt.access.AccessControlHelper;
import wt.access.AccessPermission;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.WTObject;
import wt.fc.WTReference;
import wt.fc.delete.DeleteHelper;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.inf.library.WTLibrary;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.lifecycle.LifeCycleManaged;
import wt.log4j.LogR;
import wt.pdmlink.PDMLinkProduct;
import wt.projmgmt.admin.Project2;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.team.Team;
import wt.team.TeamReference;
import wt.team.WTRoleHolder2;
import wt.util.WTException;

public class TeamCCValidator extends DefaultUIComponentValidator {
	private Logger logger = LogR.getLogger(TeamCCValidator.class.getName());
	protected static final String IMPLODE = "implode";
	protected static final String EXPLODE = "explode";
	protected static final String LIST_CUT = "list_cut";
	protected static final String LIST_COPY = "list_copy";
	protected static final String TEAM_PASTE = "teamPaste";
	protected static final String ADD_USERS_TO_ROLE = "addUsersToRole";
	protected static final String REMOVE_USERS_FROM_TEAM = "removeUsersFromTeam";
	protected static final String ADD_ROLES_TO_TEAM = "addRolesToTeam";
	protected static final String EMAIL_MEMBERS = "emailMembers";
	protected static final String EMAIL_TEAM = "emailTeam";
	protected static final String UPDATE_ROLE = "updateRole";
	protected static final String UPDATE_TEAM = "updateTeam";
	protected static final String SHOW_TEAM_USES = "show_team_uses";
	protected static final String MODIFY_ROLE_PRIVILEGES = "modifyRolePrivileges";
	protected static final String UNDO_ROLE_PRIVILEGES = "undoRolePrivileges";
	protected static final String RESYNC_WITH_GROUPS = "resyncWithGroups";
	protected static final String UPDATE_INVITATION = "updateInvitation";
	protected static final String DELETE_TEAM = "deleteTeam";
	protected static final String LIST_TEAM_CC = "listTeamCC";
	protected static final String CREATE_PROJECT = "createProject";
	protected static final String CREATE_PROGRAM = "create_program";
	protected static final String COMMON_TEAM_TABLE = "windchill.enterprise.team.commonTeam";
	protected static final String LCM_TEAM_TABLE = "windchill.enterprise.team.lcmTeam";
	protected static final String PROJECT = "project";
	protected static final String ROLE_ACCESS = "roleAccess";
	protected static final String SHARED_TEAM = "sharedTeam";
	protected static final String GROUP = "group";
	protected static final String TEAM = "team";

	public UIValidationResultSet performFullPreValidation(UIValidationKey arg0, UIValidationCriteria arg1, Locale arg2)
			throws WTException {
		this.logger.debug("ENTERING TeamCCValidator.performFullPreValidation");
		this.logger.trace("  validtionKey -> " + arg0);
		this.logger.trace("  validationCriteria -> " + arg1.toString());
		Map arg3 = arg1.getFormData();
		UIValidationResultSet arg4 = null;
		boolean arg5 = this.validateActionType(arg0);
		if (!arg5) {
			return super.performFullPreValidation(arg0, arg1, arg2);
		} else {
			WTReference arg6 = arg1.getContextObject();
			WTContainerRef arg7 = arg1.getParentContainer();
			if (null == arg6 && null == arg7) {
				return super.performFullPreValidation(arg0, arg1, arg2);
			} else {
				String arg8 = arg0.getComponentID();
				if ("listTeamCC".equalsIgnoreCase(arg8)) {
					return this.performListTeamCCValidation(arg0, arg6);
				} else {
					boolean arg9 = false;
					WTObject arg10 = null;
					if (arg6 != null) {
						arg10 = (WTObject) arg6.getObject();
					} else if (arg7 != null) {
						arg10 = (WTObject) arg7.getReferencedContainer();
					}

					if (arg10 instanceof ContainerTeamManaged) {
						String arg11 = arg1.getTextParameter("actionName");
						if (arg11 == null) {
							Object arg12 = arg3.get("TEAM_CREATE_PROJECT");
							if (arg12 != null) {
								arg11 = "createProject";
							}
						}

						WTRoleHolder2 arg17 = TeamCCHelper.getTeamFromObject(arg10);
						WTContainer arg13 = TeamCCHelper.getContainerFromObject(arg10);
						ContainerTeam arg14 = null;
						WTRoleHolder2 arg15 = null;
						if (arg17 instanceof ContainerTeam) {
							arg14 = (ContainerTeam) arg17;
							if (!arg14.isShared()) {
								arg15 = TeamCCHelper.getSharedTeamFromObject(arg13);
							}
						}

						arg9 = this.performContainerTeamActionValidation(arg8, (ContainerTeamManaged) arg10, arg11,
								arg14, arg15);
					} else if (arg10 instanceof LifeCycleManaged) {
						arg9 = this.performLifeCycleTeamActionValidation(arg8, (LifeCycleManaged) arg10);
					} else {
						if (!(arg10 instanceof ContainerTeam)) {
							this.logger.debug(
									"contextObject is not ContainerTeamManaged, LifeCycleManaged, or ContainerTeam; call super.performFullPreValidation");
							return super.performFullPreValidation(arg0, arg1, arg2);
						}

						arg9 = this.performSharedTeamActionValidation(arg8, (ContainerTeam) arg10);
					}

					arg4 = UIValidationResultSet.newInstance();
					UIValidationResult arg16 = this.makeNewResult(arg0, arg6, arg9);
					arg4.addResult(arg16);
					this.logger.trace("RETURNING " + arg16.toString());
					this.logger.debug("EXITING TeamCCValidator.performFullPreValidation");
					return arg4;
				}
			}
		}
	}

	private UIValidationResultSet performListTeamCCValidation(UIValidationKey arg0, WTReference arg1)
			throws WTException {
		UIValidationResultSet arg2 = UIValidationResultSet.newInstance();
		WTObject arg4 = (WTObject) arg1.getObject();
		Class arg5 = arg4.getClass();
		UIValidationResult arg3;
		if (!PDMLinkProduct.class.isAssignableFrom(arg5) && !WTLibrary.class.isAssignableFrom(arg5)
				&& !Project2.class.isAssignableFrom(arg5)) {
			boolean arg6 = this.validateCollaborationMenuAction(arg4);
			arg3 = this.makeNewResult(arg0, arg1, arg6);
			arg2.addResult(arg3);
			return arg2;
		} else {
			arg3 = UIValidationResult.newInstance(arg0, UIValidationStatus.HIDDEN, arg1);
			arg2.addResult(arg3);
			return arg2;
		}
	}

	private boolean performContainerTeamActionValidation(String arg0, ContainerTeamManaged arg1, String arg2,
			ContainerTeam arg3, WTRoleHolder2 arg4) throws WTException {
		this.logger.debug("teamContainerInstance is ContainerTeamManaged");
		ContainerTeam arg6 = ContainerTeamHelper.service.getContainerTeam(arg1);
		boolean arg7 = this.isAdmin((WTContainer) arg1);
		boolean arg8 = this.hasModifyAccess(arg6) && !this.isDeleted(arg1);
		boolean arg9 = this.isOrgOrSiteAdmin(arg1);
		boolean arg10 = this.isAllPeopleView(arg1);
		boolean arg11 = this.isSharedView(arg1);
		boolean arg12 = this.isRoleView(arg1);
		boolean arg13 = TeamCCHelper.hasLocalTeam(arg3, arg4);
		return this.validateContainerTeamActions(arg0, arg7, arg8, arg11, arg1, arg9, arg2, arg10, arg13, arg12);
	}

	private boolean performLifeCycleTeamActionValidation(String arg0, LifeCycleManaged arg1) throws WTException {
		this.logger.debug("teamContainerInstance is LifeCycleManaged");
		WTContainer arg2 = ((WTContained) arg1).getContainer();
		boolean arg3 = this.isAdmin(arg2);
		if (arg1 instanceof WTDocument) {
			return arg3;
		}
		Team arg4 = (Team) arg1.getTeamId().getObject();
		boolean arg5 = this.hasModifyAccess(arg4) && !this.isDeleted(arg1);
		boolean arg6 = this.isAllPeopleView(arg4);
		boolean arg7 = this.isRoleView(arg4);
		return this.validateLifeCycleTeamActions(arg0, arg3, arg5, arg6, arg7);
	}

	private boolean performSharedTeamActionValidation(String arg0, ContainerTeam arg1) throws WTException {
		this.logger.debug("teamContainerInstance is ContainerTeam");
		WTContainer arg2 = arg1.getContainer();
		boolean arg3 = this.isAdmin(arg2);
		boolean arg4 = this.hasModifyAccess(arg1) && !this.isDeleted(arg1);
		boolean arg5 = this.isAllPeopleView(arg1);
		boolean arg6 = this.isRoleView(arg1);
		return this.validateSharedTeamActions(arg0, arg3, arg4, arg5, arg6);
	}

	protected boolean validateContainerTeamActions(String arg0, boolean arg1, boolean arg2, boolean arg3,
			WTContainer arg4, boolean arg5, String arg6, boolean arg7, boolean arg8, boolean arg9) throws WTException {
		return "explode".equals(arg0) ? arg9
				: ("implode".equals(arg0) ? arg9
				: ("list_copy".equals(arg0) ? true
				: ("teamPaste".equals(arg0) ? arg2 && !arg3 && arg8
				: ("addUsersToRole".equals(arg0) ? (arg1 || arg2) && !arg3 && arg8
				: ("removeUsersFromTeam".equals(arg0)
				? (arg7 ? false : (arg1 || arg2) && !arg3 && arg8)
				: ("addRolesToTeam".equals(arg0)
				? (arg1 || arg2) && !arg3 && arg8
				: ("updateRole".equals(arg0)
				? (arg1 || arg2) && arg9 && !arg3 && arg8
				: ("modifyRolePrivileges".equals(arg0)
				? (arg5 || arg1)
					&& this.isRoleUIEnabled()
					&& !this.isProiInstalled()
					&& arg9 && !arg3 && arg8
				: ("undoRolePrivileges".equals(arg0)
					? arg1 && this.isRoleUIEnabled()
					&& arg9 && !arg3 && arg8
				: ("emailMembers".equals(arg0)
					? true
				: ("emailTeam".equals(arg0)
					? true
				: ("updateTeam".equals(arg0)
					? false
				: ("show_team_uses"
					.equals(arg0)
					? false
				: (!"updateInvitation"
					.equals(arg0)? true
				: (!"createProject".equals(arg6)
					&& !"create_program"
					.equals(arg6)
					? arg1 && arg4 instanceof Project2
					&& !arg3
					&& arg8
				: false)))))))))))))));
	}

	protected boolean validateLifeCycleTeamActions(String arg0, boolean arg1, boolean arg2, boolean arg3, boolean arg4)
			throws WTException {
		return "explode".equals(arg0) ? arg4
				: ("implode".equals(arg0) ? arg4
						: ("list_cut".equals(arg0) ? arg2
								: ("list_copy".equals(arg0) ? arg2
										: ("teamPaste".equals(arg0) ? arg2
												: ("updateRole".equals(arg0) ? false
														: ("addUsersToRole".equals(arg0) ? arg2
																: ("removeUsersFromTeam".equals(arg0)
																		? (arg3 ? false : arg2)
																		: ("addRolesToTeam".equals(arg0) ? arg1
																				: ("emailMembers".equals(arg0) ? true
																						: ("emailTeam".equals(arg0)
																								? true
																								: ("updateTeam"
																										.equals(arg0)
																												? false
																												: ("show_team_uses"
																														.equals(arg0)
																																? false
																																: ("modifyRolePrivileges"
																																		.equals(arg0)
																																				? false
																																				: ("undoRolePrivileges"
																																						.equals(arg0)
																																								? false
																																								: !"resyncWithGroups"
																																										.equals(arg0)))))))))))))));
	}

	protected boolean validateSharedTeamActions(String arg0, boolean arg1, boolean arg2, boolean arg3, boolean arg4)
			throws WTException {
		return "list_cut".equals(arg0) ? arg1 || arg2
				: ("list_copy".equals(arg0) ? true
						: ("teamPaste".equals(arg0) ? arg2
								: ("addUsersToRole".equals(arg0) ? arg1 || arg2
										: ("removeUsersFromTeam".equals(arg0) ? (arg3 ? false : arg2)
												: ("addRolesToTeam".equals(arg0) ? arg1 || arg2
														: ("emailMembers".equals(arg0) ? true
																: ("emailTeam".equals(arg0) ? true
																		: ("updateRole".equals(arg0)
																				? (arg1 || arg2) && arg4
																				: ("updateTeam".equals(arg0) ? arg2
																						: ("show_team_uses".equals(arg0)
																								? true
																								: ("modifyRolePrivileges"
																										.equals(arg0)
																												? false
																												: ("undoRolePrivileges"
																														.equals(arg0)
																																? false
																																: ("resyncWithGroups"
																																		.equals(arg0)
																																				? arg1
																																				: ("updateInvitation"
																																						.equals(arg0)
																																								? false
																																								: (!"deleteTeam"
																																										.equals(arg0)
																																												? true
																																												: arg1 || arg2)))))))))))))));
	}

	protected boolean validateActionType(UIValidationKey arg0) {
		String arg1 = arg0.getComponentID();
		String arg2 = arg0.getObjectType();
		return !"resyncWithGroups".equals(arg1) && !"updateInvitation".equals(arg1)
				? (!"modifyRolePrivileges".equals(arg1) && !"undoRolePrivileges".equals(arg1)
						? (!"updateTeam".equals(arg1) && !"show_team_uses".equals(arg1) && !"deleteTeam".equals(arg1)
								? ("updateRole".equals(arg1) ? "group".equals(arg2) : "team".equals(arg2))
								: "sharedTeam".equals(arg2))
						: "roleAccess".equals(arg2))
				: "project".equals(arg2);
	}

	private boolean validateCollaborationMenuAction(WTObject arg0) throws WTException {
		if (arg0 instanceof LifeCycleManaged) {
			LifeCycleManaged arg1 = (LifeCycleManaged) arg0;
			TeamReference arg2 = arg1.getTeamId();
			if (arg2 == null) {
				return false;
			} else {
				boolean arg3 = AccessControlHelper.manager.hasAccess(arg2, AccessPermission.READ);
				return arg3 && !arg1.isLifeCycleBasic();
			}
		} else {
			return arg0 instanceof ContainerTeam || arg0 instanceof ContainerTeamManaged;
		}
	}

	protected boolean isAllPeopleView(WTRoleHolder2 arg0) throws WTException {
		return TeamCCHelper.isAllPeopleView(TeamCCHelper.getCurrentView(arg0));
	}

	protected boolean isAllPeopleView(ContainerTeamManaged arg0) throws WTException {
		return TeamCCHelper.isAllPeopleView(TeamCCHelper.getCurrentView(arg0));
	}

	protected boolean isAllPeopleView(Team arg0) throws WTException {
		return TeamCCHelper.isAllPeopleView(TeamCCHelper.getCurrentView(arg0));
	}

	protected boolean isAllPeopleView(LifeCycleManaged arg0) throws WTException {
		return TeamCCHelper.isAllPeopleView(TeamCCHelper.getCurrentView(arg0));
	}

	protected boolean isAllPeopleView(ContainerTeam arg0) throws WTException {
		return TeamCCHelper.isAllPeopleView(TeamCCHelper.getCurrentView(arg0));
	}

	protected boolean isLocalView(ContainerTeamManaged arg0) throws WTException {
		return TeamCCHelper.isLocalView(TeamCCHelper.getCurrentView(arg0));
	}

	protected boolean isLocalView(ContainerTeam arg0) throws WTException {
		return TeamCCHelper.isLocalView(TeamCCHelper.getCurrentView(arg0));
	}

	protected boolean isLocalView(Team arg0) throws WTException {
		return TeamCCHelper.isLocalView(TeamCCHelper.getCurrentView(arg0));
	}

	protected boolean isLocalView(LifeCycleManaged arg0) throws WTException {
		return TeamCCHelper.isLocalView(TeamCCHelper.getCurrentView(arg0));
	}

	protected boolean isLocalView(WTRoleHolder2 arg0) throws WTException {
		return TeamCCHelper.isLocalView(TeamCCHelper.getCurrentView(arg0));
	}

	protected boolean isRoleView(ContainerTeamManaged arg0) throws WTException {
		return TeamCCHelper.isRoleView(TeamCCHelper.getCurrentView(arg0));
	}

	protected boolean isRoleView(ContainerTeam arg0) throws WTException {
		return TeamCCHelper.isRoleView(TeamCCHelper.getCurrentView(arg0));
	}

	protected boolean isRoleView(Team arg0) throws WTException {
		return TeamCCHelper.isRoleView(TeamCCHelper.getCurrentView(arg0));
	}

	protected boolean isRoleView(LifeCycleManaged arg0) throws WTException {
		return TeamCCHelper.isRoleView(TeamCCHelper.getCurrentView(arg0));
	}

	protected boolean isSharedView(ContainerTeamManaged arg0) throws WTException {
		return TeamCCHelper.isSharedView(TeamCCHelper.getCurrentView(arg0));
	}

	protected boolean isSharedView(ContainerTeam arg0) throws WTException {
		return TeamCCHelper.isSharedView(TeamCCHelper.getCurrentView(arg0));
	}

	protected boolean isSharedView(Team arg0) throws WTException {
		return TeamCCHelper.isSharedView(TeamCCHelper.getCurrentView(arg0));
	}

	protected boolean isSharedView(LifeCycleManaged arg0) throws WTException {
		return TeamCCHelper.isSharedView(TeamCCHelper.getCurrentView(arg0));
	}

	protected boolean isSharedView(WTRoleHolder2 arg0) throws WTException {
		return TeamCCHelper.isSharedView(TeamCCHelper.getCurrentView(arg0));
	}

	private UIValidationResult makeNewResult(UIValidationKey arg0, WTReference arg1, boolean arg2) {
		UIValidationResult arg3 = UIValidationResult.newInstance();
		arg3.setTargetObject(arg1);
		arg3.setValidationKey(arg0);
		if (arg2) {
			arg3.setStatus(UIValidationStatus.ENABLED);
		} else {
			arg3.setStatus(UIValidationStatus.HIDDEN);
		}

		return arg3;
	}

	protected boolean isAdmin(WTContainer arg0) throws WTException {
		return this.isAdmin(WTContainerRef.newWTContainerRef(arg0));
	}

	protected boolean isAdmin(WTContainerRef arg0) throws WTException {
		return WTContainerHelper.service.isAdministrator(arg0, SessionHelper.manager.getPrincipal());
	}

	protected boolean isOrgOrSiteAdmin(WTContainer arg0) throws WTException {
		WTContainerRef arg1 = null;
		boolean arg2 = SessionServerHelper.manager.setAccessEnforced(false);

		try {
			arg1 = WTContainerHelper.service.getOrgContainerRef(arg0);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(arg2);
		}

		return this.isAdmin(arg1);
	}

	protected boolean isDeleted(Persistable arg0) throws WTException {
		return DeleteHelper.isMarkedForDelete(arg0);
	}

	protected boolean hasModifyAccess(Object arg0) throws WTException {
		return AccessControlHelper.manager.hasAccess(arg0, AccessPermission.MODIFY);
	}

	protected boolean isRoleUIEnabled() {
		return NmRoleAccessHelper.isRoleUIEnabled();
	}

	protected boolean isProiInstalled() {
		return NmRoleAccessHelper.isProiInstalled();
	}
}