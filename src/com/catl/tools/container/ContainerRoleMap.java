package com.catl.tools.container;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class ContainerRoleMap implements Serializable {

	private static final long serialVersionUID = -6522669473343043241L;

	private Set<String> productList = new LinkedHashSet<String>();

	private Set<String> libraryList = new LinkedHashSet<String>();

	private Set<String> projectList = new LinkedHashSet<String>();

	private Map<String, Set<String>> roleMap = new LinkedHashMap<String, Set<String>>();

	public void addProduct(String productName) {
		productList.add(productName);
	}

	public void addLibrary(String libraryName) {
		libraryList.add(libraryName);
	}

	public void addProject(String projectName) {
		projectList.add(projectName);
	}

	public void addRole(String role) {
		if (!roleMap.containsKey(role)) {
			roleMap.put(role, new LinkedHashSet<String>());
		}
	}

	public void addRoleMember(String role, String memberName) {
		Set<String> members = roleMap.get(role);
		if (members == null) {
			members = new LinkedHashSet<String>();
			roleMap.put(role, members);
		}
		members.add(memberName);
	}

	public Set<String> getProduct() {
		return Collections.unmodifiableSet(productList);
	}

	public Set<String> getLibrary() {
		return Collections.unmodifiableSet(libraryList);
	}

	public Set<String> getProject() {
		return Collections.unmodifiableSet(projectList);
	}

	public boolean containRole(String role) {
		return this.roleMap.containsKey(role);
	}

	public Map<String, Set<String>> getRoleMap() {
		return Collections.unmodifiableMap(roleMap);
	}

}
