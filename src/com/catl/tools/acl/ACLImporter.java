package com.catl.tools.acl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import wt.access.LoadAccessControlRules;
import wt.admin.AdministrativeDomain;
import wt.admin.AdministrativeDomainHelper;
import wt.util.WTException;

public class ACLImporter {

	// (?:\[.+\]){0,1}((?:/[\w\u0100-\uFFFF]+)+)
	private static final Pattern DOMAIN_PATTERN = Pattern.compile("(?:\\[.+\\]){0,1}((?:/[\\w\\u0100-\\uFFFF]+)+)");

	public static List<Hashtable> parseAclCsvFile(String csvPathFile) throws IOException {
		FileReader fr = new FileReader(csvPathFile);
		BufferedReader br = new BufferedReader(fr);
		List<Hashtable> ruleList = new ArrayList<Hashtable>();
		String line = null;
		while ((line = br.readLine()) != null) {
			if (StringUtils.isEmpty(line)) {
				continue;
			}
			if (line.startsWith("#")) {
				continue;
			}

			if (!line.startsWith("AccessRule")) {
				System.out.println("***WARNNING*** AccessRule line must start with 'AccessRule', but:" + line);
				continue;
			}

			String[] params = line.split(",");
			if (params.length < 9) {
				System.out.println("***WARNNING*** Invalid ACL rule line format:" + line + "," + params.length);
				continue;
			}

			Hashtable ruleInfo = new Hashtable();
			ruleInfo.put("user", params[1]);
			String domain = params[2];
			Matcher m = DOMAIN_PATTERN.matcher(domain);
			if (m.matches()) {
				domain = m.group(1);
			}
			ruleInfo.put("domain", domain);
			ruleInfo.put("typeId", params[3]);
			ruleInfo.put("permission", params[4]);
			ruleInfo.put("principal", params[5]);
			ruleInfo.put("permissionList", params[6]);
			ruleInfo.put("state", params[7]);
			ruleInfo.put("allExceptPrincipal", params[8]);

			ruleList.add(ruleInfo);
		}
		return ruleList;
	}

	public static void createAclIntoMultiDomain(List<Hashtable> aclInfoList, List<String> targetDomainPath, boolean cleanBeforeImport) throws WTException {
		for (String domainPath : targetDomainPath) {
			createAclIntoDomain(aclInfoList, domainPath, cleanBeforeImport);
		}
	}

	public static void createAclIntoDomain(List<Hashtable> aclInfoList, String targetDomainPath, boolean cleanBeforeImport) throws WTException {

		String containerPath = "";
		for (Hashtable ruleInfo : aclInfoList) {
			containerPath = "[" + targetDomainPath + "]" + ruleInfo.get("domain");
			if (cleanBeforeImport) {
				ACLExporter.deleteAllACL(containerPath); // delete all ACL in target domain frist!
			}
			System.out.println("target container path:" + containerPath);
			ruleInfo.put("domain", containerPath);
			Hashtable cmdLine = new Hashtable();
			Vector returnObject = new Vector();
			AdministrativeDomain domain = AdministrativeDomainHelper.manager.getDomain(containerPath);
			if (domain == null) {
				System.out.println("***Warning*** domain path not exists:" + containerPath + ", AccessRule ingored:" + ruleInfo);
				continue;
			}
			LoadAccessControlRules.createAccessControlRule(ruleInfo, cmdLine, returnObject);
		}
	}

	public static void refreshAclIntoContainer(List<Hashtable> aclInfoList, String containerPath) throws WTException {

		String targetPath = "";
		// clean all domain
		System.out.println("clean target container:");
		Map<String, String> domainMap = new TreeMap<String, String>();
		for (Hashtable ruleInfo : aclInfoList) {
			domainMap.put(ruleInfo.get("domain").toString(), ruleInfo.get("domain").toString());
		}
		// Set<Entry<String,String>> domainSet = domainMap.entrySet().iterator();
		Iterator<Entry<String, String>> ite = domainMap.entrySet().iterator();
		while (ite.hasNext()) {
			targetPath = containerPath + ite.next().getKey();
			System.out.println("clean target container path:" + targetPath);
			ACLExporter.deleteAllACL(targetPath);
		}

		// import acl
		System.out.println("import acl into container:");
		for (Hashtable ruleInfo : aclInfoList) {
			targetPath = containerPath + ruleInfo.get("domain");
			System.out.println("target container path:" + targetPath);
			// 检查权限的目标Domain是否在系统中存在，如果不存在，则直接略过
			AdministrativeDomain domain = AdministrativeDomainHelper.manager.getDomain(targetPath);
			if (domain == null) {
				System.out.println("***Warning*** domain path not exists:" + targetPath + ", AccessRule ingored:" + ruleInfo);
				continue;
			}
			ruleInfo.put("domain", targetPath);
			Hashtable cmdLine = new Hashtable();
			Vector returnObject = new Vector();
			LoadAccessControlRules.createAccessControlRule(ruleInfo, cmdLine, returnObject);
		}
	}

	public static void createAclIntoDomain(Hashtable ruleInfo, String targetDomainPath) throws WTException {
		ruleInfo.put("domain", targetDomainPath);
		Hashtable cmdLine = new Hashtable();
		Vector returnObject = new Vector();
		AdministrativeDomain domain = AdministrativeDomainHelper.manager.getDomain(targetDomainPath);
		if (domain == null) {
			System.out.println("***Warning*** domain path not exists:" + targetDomainPath + ", AccessRule ingored:" + ruleInfo);
			return;
		}
		LoadAccessControlRules.createAccessControlRule(ruleInfo, cmdLine, returnObject);
	}

}
