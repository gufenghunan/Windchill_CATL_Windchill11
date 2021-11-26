package com.catl.tools.container;

import java.util.Vector;

import wt.inf.container.WTContainer;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.project.Role;
import wt.util.WTException;

public class StrictlyMatchContainerTeamUpdater extends ExistRoleOnlyContainerTeamUpdater {

	@Override
	protected void doUpdate(WTContainer container, ContainerTeam containerTeam, ContainerRoleMap roleMap) throws WTException {
		super.doUpdate(container, containerTeam, roleMap);
		Vector<Role> roles = containerTeam.getRoles();
		// remove all role not included in roleMap
		for (Role role : roles) {
			String roleName = role.toString();
			if (!roleMap.containRole(roleName)) {
				ContainerTeamHelper.service.removeRole(containerTeam, role);
				//containerTeam.deleteRole(role);
			}
		}
		
	}

}
