package com.catl.principal.resource;

import wt.util.resource.RBEntry;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;

@RBUUID("com.catl.principal.resource.CATLPrincipalRB")
public class CATLPrincipalRB extends WTListResourceBundle {

	@RBEntry("离职人员移除组和团队")
	public static final String PRINT_SIGNET_DESCRIPTION = "catlprincipal.removeGroupAndTeam.description";
	@RBEntry("离职人员移除组和团队")
	public static final String PRINT_SIGNET_TOOLTIP = "catlprincipal.removeGroupAndTeam.tooltip";
	@RBEntry("离职人员移除组和团队")
	public static final String PRINT_SIGNET_TITLE = "catlprincipal.removeGroupAndTeam.title";
}
