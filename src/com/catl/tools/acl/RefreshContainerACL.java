package com.catl.tools.acl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import wt.access.AccessControlHelper;
import wt.access.LoadAccessControlRules;
import wt.admin.AdminDomainRef;
import wt.admin.AdministrativeDomain;
import wt.admin.AdministrativeDomainHelper;
import wt.log4j.LogR;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.util.WTException;

public class RefreshContainerACL implements Serializable, RemoteAccess{

    private static final long serialVersionUID = -3614225435428221377L;
    private static final Logger LOGGER = LogR.getLogger(RefreshContainerACL.class.getName());
    
    public static void main(String[] args) throws IOException {

		if (args == null || args.length == 0) {
			printUsage();
			return;
		}

		//RefreshContainerACL refreshACL = new RefreshContainerACL();

		if ("refresh".equals(args[0])) {
			// refresh ACL to container
			// java className refresh [csv_file_path] [target_container_path] [username] [password]
			checkArgs(args, 5);
			setToken(args[3], args[4]);
			new RefreshContainerACL().refreshAclFromCSVToContainer(args[1], args[2]);
			return;
		}		
		
		printUsage();
    }

	public void refreshAclFromCSVToContainer(String csvFilePath, String targetContainerPath) throws IOException {
		if (null == targetContainerPath || targetContainerPath.length() == 0 ) {
			System.out.println("Invalid target container path");
			return;
		}
		
        if (!RemoteMethodServer.ServerFlag) {
            try {
        		Class argTypes[] = { String.class, String.class};
        		Object args[] = { csvFilePath, targetContainerPath };
        		RemoteMethodServer.getDefault().invoke("refreshAclFromCSVToContainer", null, this, argTypes, args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

    		try {
    			@SuppressWarnings("rawtypes")
    			List<Hashtable> ruleList = parseAclCsvFile(csvFilePath);
    			System.out.println("refresh ACL:");
    			refreshAclIntoContainer(ruleList, targetContainerPath);

    		} catch (WTException e) {
    			e.printStackTrace();
    		}
        }

	}
    

	private void refreshAclIntoContainer(List<Hashtable> aclInfoList, String containerPath) throws WTException {

		String targetPath = "";
		//clean all domain
		System.out.println("clean target container:");
		Map<String,String> domainMap = new TreeMap<String,String>();
		for (Hashtable ruleInfo : aclInfoList) {
			domainMap.put(ruleInfo.get("domain").toString(), ruleInfo.get("domain").toString());
		}
		//Set<Entry<String,String>> domainSet = domainMap.entrySet().iterator();
		//System.out.println("domainMap :"+domainMap.toString());
		Iterator<Entry<String, String>> ite = domainMap.entrySet().iterator();
		while(ite.hasNext()){
			targetPath = containerPath + ite.next().getKey();
			System.out.println("clean target container path:"+targetPath);
			deleteAllACL(targetPath);
		}

		//import acl
		System.out.println("import acl into container:");
		for (Hashtable ruleInfo : aclInfoList) {
			targetPath = containerPath + ruleInfo.get("domain");
			//System.out.println("target container path:"+targetPath);
			ruleInfo.put("domain", targetPath);
			Hashtable cmdLine = new Hashtable();
			Vector returnObject = new Vector();
			LoadAccessControlRules.createAccessControlRule(ruleInfo, cmdLine, returnObject);
		}
	}
	
	private List<Hashtable> parseAclCsvFile(String csvPathFile) throws IOException {
		
		FileReader fr = new FileReader(csvPathFile);
		BufferedReader br = new BufferedReader(fr);
		List<Hashtable> ruleList = new ArrayList<Hashtable>();
		String line = null;
		while ((line = br.readLine()) != null) {
			if (StringUtils.isEmpty(line)) {
				break;
			}
			if (line.startsWith("#")) {
				continue;
			}
			
			String[] params = line.split(",");
			if (params.length < 8 || !line.startsWith("AccessRule")) {
				System.out.println("***WARNNING*** Invalid ACL rule line:" + line);
				continue;
			}
			
			Hashtable ruleInfo = new Hashtable();
			ruleInfo.put("user", params[1]);
			ruleInfo.put("domain", params[2]);
			ruleInfo.put("typeId", params[3]);
			ruleInfo.put("permission", params[4]);
			ruleInfo.put("principal", params[5]);
			ruleInfo.put("permissionList", params[6]);
			ruleInfo.put("state", params[7]);
			
			ruleList.add(ruleInfo);
		}
		return ruleList;
	}
		
	private void deleteAllACL(String domainPath) throws WTException {
		AdministrativeDomain domain = AdministrativeDomainHelper.manager.getDomain(domainPath);
		AccessControlHelper.manager.deleteAccessControlRules(AdminDomainRef.newAdminDomainRef(domain));
	}
	
	
	private static void setToken(String username, String password) {
		RemoteMethodServer.getDefault().setUserName(username);
		RemoteMethodServer.getDefault().setPassword(password);
	}
	
	private static void checkArgs(String[] args, int minArgs) {
		if (args == null || args.length == 0) {
			printUsage();
			System.exit(0);
		}
		
		if (args.length < minArgs) {
			printUsage();
			System.exit(0);
		}
	}
    
	private static void printUsage() {
		System.out.println("- To refresh ACL using CSV format file to container");
		System.out.println("  windchill ext.mindray.tools.acl.ACLHelper refresh [csv_file_path] [target_container_path] [username] [password]");		
		System.out.println();		
		System.out.println();
		System.out.println("container path example:");
		System.out.println("[/wt.inf.container.OrgContainer=orgName]");
		System.out.println("[/wt.inf.container.OrgContainer=orgName/wt.pdmlink.PDMLinkProduct=Product Name]");		
	}
}
