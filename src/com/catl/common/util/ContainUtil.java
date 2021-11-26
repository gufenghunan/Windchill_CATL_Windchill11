package com.catl.common.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import com.catl.common.constant.RoleName;

import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerHelper;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.org.DirectoryContextProvider;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTOrganization;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.project.Role;
import wt.util.WTException;
import wt.util.WTRuntimeException;

public class ContainUtil {
	
	/**
	 * 获取团队中某个角色的成员
	 * @param container
	 * @param rolenameValue	角色的value
	 * @return
	 * @throws WTException
	 */
	public static List<WTPrincipal> findContainerTeamUserByRolenameValue(WTContainer container, String rolenameValue) throws WTException {
		ContainerTeam containerTeam = null;
		List<WTPrincipal> roleuser = new ArrayList<WTPrincipal>();
		ContainerTeamManaged containerteammanaged = (ContainerTeamManaged) container;
		containerTeam = ContainerTeamHelper.service.getContainerTeam(containerteammanaged);

		Vector<Role> roles = containerTeam.getRoles();
		
		DirectoryContextProvider dcp = WTContainerHelper.service.getExchangeContainer().getContextProvider();
		WTOrganization org = OrganizationServicesHelper.manager.getOrganization("CATL", dcp);
		
		String[] services = wt.org.OrganizationServicesHelper.manager.getDirectoryServiceNames();
		wt.org.DirectoryContextProvider dc_provider = wt.org.OrganizationServicesHelper.manager.newDirectoryContextProvider(services, null);
		for (int i = 0; i < roles.size(); i++) {
			Role role = roles.get(i);
			if (role.getDisplay(Locale.CHINA).equalsIgnoreCase(rolenameValue)) {
				Enumeration enumPrin = containerTeam.getPrincipalTarget(role);

				while (enumPrin.hasMoreElements()) {
					WTPrincipalReference tempPrinRef = (WTPrincipalReference) enumPrin.nextElement();
					// 如果角色下的成员是组
					if (tempPrinRef.getObject() instanceof WTGroup) {
						WTGroup wtGroup = (WTGroup) tempPrinRef.getObject();
						wtGroup.getName();
						Enumeration<?> enu = OrganizationServicesHelper.manager.getGroups(wtGroup.getName(), WTContainerHelper.service.getOrgContainer(org).getContextProvider());
						if(!enu.hasMoreElements()){
							enu = wt.org.OrganizationServicesHelper.manager.getGroups(wtGroup.getName(), dc_provider);
						}
						while (enu.hasMoreElements()) {
							Object o = enu.nextElement();
							if (o instanceof WTGroup) {
								WTGroup group = (WTGroup) o;
								Enumeration users = group.members();
								while (users.hasMoreElements()) {
									WTPrincipal principal = (WTPrincipal) users.nextElement();
									roleuser.add(principal);
								}
							}
						}
					} else {
						WTPrincipal principal = tempPrinRef.getPrincipal();
						roleuser.add(principal);
					}

				}
			}
		}
		return roleuser;
	}
	
	/**
	 * 获取团队中某个角色的成员
	 * @param container
	 * @param rolenameKey	角色的key 多个以,隔开
	 * @return
	 * @throws WTException
	 */
	public static List<WTPrincipal> findContainerTeamUserByRolenameKey(WTContainer container, String rolenameKey) throws WTException {
		ContainerTeam containerTeam = null;
		List<WTPrincipal> roleuser = new ArrayList<WTPrincipal>();
		ContainerTeamManaged containerteammanaged = (ContainerTeamManaged) container;
		containerTeam = ContainerTeamHelper.service.getContainerTeam(containerteammanaged);
		
		
		DirectoryContextProvider dcp = WTContainerHelper.service.getExchangeContainer().getContextProvider();
		WTOrganization org = OrganizationServicesHelper.manager.getOrganization("CATL", dcp);
		String[] services = wt.org.OrganizationServicesHelper.manager.getDirectoryServiceNames();
		wt.org.DirectoryContextProvider dc_provider = wt.org.OrganizationServicesHelper.manager.newDirectoryContextProvider(services, null);
		
		String rolenameKeyArr[] = rolenameKey.split(",");
		for(String rolenameKeyTemp : rolenameKeyArr){
			Enumeration enumPrin = containerTeam.getPrincipalTarget(Role.toRole(rolenameKeyTemp));
			while (enumPrin.hasMoreElements()) {
				WTPrincipalReference tempPrinRef = (WTPrincipalReference) enumPrin.nextElement();
				// 如果角色下的成员是组
				if (tempPrinRef.getObject() instanceof WTGroup) {
					WTGroup wtGroup = (WTGroup) tempPrinRef.getObject();
					wtGroup.getName();
					Enumeration<?> enu = OrganizationServicesHelper.manager.getGroups(wtGroup.getName(), WTContainerHelper.service.getOrgContainer(org).getContextProvider());
					if(!enu.hasMoreElements()){
						enu = wt.org.OrganizationServicesHelper.manager.getGroups(wtGroup.getName(), dc_provider);
					}
					while (enu.hasMoreElements()) {
						Object o = enu.nextElement();
						if (o instanceof WTGroup) {
							WTGroup group = (WTGroup) o;
							Enumeration users = group.members();
							while (users.hasMoreElements()) {
								WTPrincipal principal = (WTPrincipal) users.nextElement();
								roleuser.add(principal);
							}
						}
					}
				} else {
					WTPrincipal principal = tempPrinRef.getPrincipal();
					roleuser.add(principal);
				}

			}
		}
		return roleuser;
	}
}
