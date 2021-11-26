package com.catl.tools.container;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.lang.StringUtils;

public class CSVContainerRoleMapParser implements ContainerRoleMapParser {

	public ContainerRoleMap parser(InputStream is) throws IOException {
		ContainerRoleMap roleMap = new ContainerRoleMap();
		InputStreamReader reader = new InputStreamReader(is);
		BufferedReader bReader = new BufferedReader(reader);
		String line = null;
		while ((line = bReader.readLine()) != null) {
			if (line.startsWith("ProductList,")) {
				String productStr = line.substring(12);
				String[] products = productStr.split("\\,");
				for (String prodName : products) {
					if (StringUtils.isNotBlank(prodName)) {
						roleMap.addProduct(prodName);
					}
				}
				continue;
			}
			if (line.startsWith("LibraryList,")) {
				String libraryStr = line.substring(12);
				String[] librarys = libraryStr.split("\\,");
				for (String libName : librarys) {
					if (StringUtils.isNotBlank(libName)) {
						roleMap.addLibrary(libName);
					}
				}
				continue;
			}
			if (line.startsWith("ProjectList,")) {
				String projectStr = line.substring(12);
				String[] projects = projectStr.split("\\,");
				for (String projectName : projects) {
					if (StringUtils.isNotBlank(projectName)) {
						roleMap.addProject(projectName);
					}
				}
				continue;
			}
			if (line.startsWith("RolePrincipal,")) {
				String role = line.substring(14);
				String[] str = role.split("\\,");
				if (StringUtils.isNotBlank(str[0])) {
					if (str.length > 1) {
						roleMap.addRoleMember(str[0], str[1]);
					} else {
						roleMap.addRole(str[0]);
					}
				}
				continue;
			}
		}
		return roleMap;
	}

	public static void main(String[] args) throws IOException {
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("com/ptc/lizhou/windchill/util/roleMap.csv");
		CSVContainerRoleMapParser parser = new CSVContainerRoleMapParser();
		ContainerRoleMap roleMap = parser.parser(is);
	}

}
