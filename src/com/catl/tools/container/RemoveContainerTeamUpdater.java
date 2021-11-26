package com.catl.tools.container;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import wt.fc.PersistenceHelper;
import wt.inf.container.WTContainer;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.project.Role;
import wt.team.TeamHelper;
import wt.util.WTException;

public class RemoveContainerTeamUpdater extends BaseContainerTeamUpdater {

	@Override
	protected void doUpdate(WTContainer container, ContainerTeam containerTeam, ContainerRoleMap roleMap) throws WTException {
		Map<String, Set<String>> map = roleMap.getRoleMap();

		Map<Role, Set<WTPrincipal>> mapedRoles = new LinkedHashMap<Role, Set<WTPrincipal>>();
		Set<Entry<String, Set<String>>> entrySet = map.entrySet();
		for (Entry<String, Set<String>> e : entrySet) {
			String roleName = e.getKey();
			Set<String> memberNames = e.getValue();

			Role role = Role.toRole(roleName);
			Set<WTPrincipal> members = new HashSet<WTPrincipal>();
			for (String memberName : memberNames) {
				WTPrincipal p = getWTPrincipalByName(container, memberName);
				if (p == null) {
					System.out.println("Cannot get WTPrincipal by name:" + memberName);
					continue;
				}
				members.add(p);
			}
			mapedRoles.put(role, members);
		}

		// remove target principal in roles
		Vector<Role> roles = containerTeam.getRoles();
		roles = containerTeam.getRoles();
		for (Role role : roles) {
			if (!mapedRoles.containsKey(role)) {
				continue;
			}
			Set<WTPrincipal> members = mapedRoles.get(role);
			ArrayList principals = containerTeam.getAllPrincipalsForTarget(role);
			for (int i = 0; i < principals.size(); i++) {
				WTPrincipalReference pRef = (WTPrincipalReference) principals.get(i);
				WTPrincipal p = pRef.getPrincipal();
				if (members.contains(p)) {
					containerTeam = (ContainerTeam) PersistenceHelper.manager.refresh(containerTeam);
					if (role.toString().equals(ContainerTeamHelper.GUEST)) {
						ContainerTeamHelper.service.removeGuestMember(containerTeam, p);
					} else {
						//TeamHelper.service.deleteRolePrincipalMap(role, p, containerTeam);
						
						ContainerTeamHelper.service.removeMember(containerTeam, role, p);
					}

				}
			}
		}

		// remove target role in RoleMap
		Set<Entry<Role, Set<WTPrincipal>>> set = mapedRoles.entrySet();
		for (Entry<Role, Set<WTPrincipal>> e : set) {
			Set<WTPrincipal> members = e.getValue();
			Role role = e.getKey();
			if (members.isEmpty()) {
				//TeamHelper.service.deleteRole(role, containerTeam);
				System.out.println("role removed:"+role.getDisplay());
				ContainerTeamHelper.service.removeRole(containerTeam, role);
				
			}

		}

	}


}
