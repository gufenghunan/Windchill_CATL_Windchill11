package com.catl.tools.container;

import java.util.Map.Entry;
import java.util.Set;

import wt.fc.dynamicenum.DynamicEnumerationHelper;
import wt.inf.container.WTContainer;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.org.WTPrincipal;
import wt.project.Role;
import wt.projmgmt.admin.Project2;
import wt.util.WTException;

public class AppendOnlyContainerTeamUpdater extends BaseContainerTeamUpdater {

	@Override
	protected void doUpdate(WTContainer container, ContainerTeam containerTeam, ContainerRoleMap roleMap) throws WTException {
		Set<Entry<String, Set<String>>> set = roleMap.getRoleMap().entrySet();
		for (Entry<String, Set<String>> e : set) {
			String roleName = e.getKey();
			Set<String> memeberNames = e.getValue();
			Role role = null;
			if (container instanceof Project2) {
				role = (Role) DynamicEnumerationHelper.getEnumeratedType(container, "wt.project.Role", roleName);
			}
			if (role == null) {
				role = Role.toRole(roleName);
			}
			if (memeberNames.isEmpty()) {
				if (roleName.equals(ContainerTeamHelper.GUEST)) {
					ContainerTeamHelper.service.addGuestMember(containerTeam, null);
				} else {
					ContainerTeamHelper.service.addMember(containerTeam, role, null);
				}

				continue;
			}
			for (String memberName : memeberNames) {
				WTPrincipal principal = getWTPrincipalByName(container, memberName);
				if (principal == null) {
					System.out.println("Cannot get WTPrincipal by name:" + memberName);
					continue;
				}
				if (roleName.equals(ContainerTeamHelper.GUEST)) {
					ContainerTeamHelper.service.addGuestMember(containerTeam, principal);
				} else {
					ContainerTeamHelper.service.addMember(containerTeam, role, principal);
				}
			}
		}
	}

}
