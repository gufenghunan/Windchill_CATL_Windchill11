package com.catl.tools.acl;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.pom.Transaction;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class ACLHelper implements RemoteAccess, Serializable {

	private static final long serialVersionUID = 4435309049565969187L;

	private static void printUsage() {
		System.out.println("Windchill ACL Toolbox for Windchill 10.x, Written by Levi Zhou (lizhou@ptc.com), Nov.4, 2015");
		System.out.println();
		System.out.println("- To clean all ACL in a domain:");
		System.out.println("  windchill com.catl.tools.acl.ACLHelper clean [domain_path] [username] [password]");
		System.out.println();
		System.out.println("- To export all ACL in a domain as CSV format file:");
		System.out.println("  windchill com.catl.tools.acl.ACLHelper export [domain_path] [csv_file_path_and_name] [username] [password]");
		System.out.println();
		System.out.println("- To export all ACL in a domain and child domain as CSV format file:");
		System.out.println("  windchill com.catl.tools.acl.ACLHelper export_with_child [domain_path] [csv_file_path_and_name] [username] [password]");
		System.out.println();
		System.out.println("- To sync all ACL from a domain to other domains");
		System.out.println("  windchill com.catl.tools.acl.ACLHelper sync [source_domain_Path] [target_domain_path1,target_domain_path2...] [username] [password]");
		System.out.println();
		System.out.println("- To import ACL using CSV format file to multi domains without clean");
		System.out.println("  windchill com.catl.tools.acl.ACLHelper importcsv [csv_file_path] [target_domain_path1,target_domain_path2...] [username] [password]");
		System.out.println();
		System.out.println("- To import ACL using CSV format file to multi domains after clean");
		System.out.println("  windchill com.catl.tools.acl.ACLHelper fillwith [csv_file_path] [target_domain_path1,target_domain_path2...] [username] [password]");
		System.out.println();
		System.out.println("- To refresh ACL using CSV format file to container");
		System.out.println("  windchill com.catl.tools.acl.ACLHelper refresh [csv_file_path] [target_container_path] [username] [password]");
		System.out.println();
		System.out.println("- To refresh Container domains using CSV format file");
		System.out.println("  windchill com.catl.tools.acl.ACLHelper refresh_domain [csv_file_path] [target_container_path] [username] [password]");
		System.out.println();
		System.out.println();
		System.out.println("domain path example:");
		System.out.println("[/wt.inf.container.OrgContainer=orgName]/Default");
		System.out.println("[/wt.inf.container.OrgContainer=orgName/wt.pdmlink.PDMLinkProduct=Product Name]/Default");
		System.out.println("container path example:");
		System.out.println("[/wt.inf.container.OrgContainer=orgName]");
		System.out.println("[/wt.inf.container.OrgContainer=orgName/wt.pdmlink.PDMLinkProduct=Product Name]");
	}

	public void cleanDomainACL(String domainPath) throws RemoteException, InvocationTargetException {
		Class argTypes[] = { String.class };
		Object args[] = { domainPath };
		RemoteMethodServer.getDefault().invoke("_cleanDomainACL", null, this, argTypes, args);
	}

	public void _cleanDomainACL(String domainPath) throws WTException {
		Transaction tx = null;
		try {
			tx = new Transaction();
			tx.start();
			ACLExporter.deleteAllACL(domainPath);
			tx.commit();
		} catch (WTException e) {
			if (tx != null) {
				tx.rollback();
				throw e;
			}
		}
	}

	public void exportDomainACL(String domainPath, String csvFilePath) throws RemoteException, InvocationTargetException {
		Class argTypes[] = { String.class, String.class, boolean.class };
		Object args[] = { domainPath, csvFilePath, false };
		RemoteMethodServer.getDefault().invoke("_exportDomainACL", null, this, argTypes, args);
	}

	public void exportDomainACL(String domainPath, String csvFilePath, boolean withChildDomains) throws RemoteException, InvocationTargetException {
		Class argTypes[] = { String.class, String.class, boolean.class };
		Object args[] = { domainPath, csvFilePath, withChildDomains };
		RemoteMethodServer.getDefault().invoke("_exportDomainACL", null, this, argTypes, args);
	}

	public void _exportDomainACL(String domainPath, String csvFilePath, boolean withChildDomains) throws IOException, WTException {
		ACLExporter.exportDomainACLtoCSVFile(domainPath, csvFilePath, withChildDomains);
	}

	public void synchronizeDomainACL(String sourceDomainPath, String targetDomainPath) throws RemoteException, InvocationTargetException {
		Class argTypes[] = { String.class, String.class };
		Object args[] = { sourceDomainPath, targetDomainPath };
		RemoteMethodServer.getDefault().invoke("_synchronizeDomainACL", null, this, argTypes, args);
	}

	public void _synchronizeDomainACL(String sourceDomainPath, String targetDomainPath) throws WTException {
		if (null == targetDomainPath || targetDomainPath.length() == 0) {
			System.out.println("Invalid target domain path");
			return;
		}
		String[] targetDomainPaths = targetDomainPath.split(",");
		List<String> targetDomainList = new ArrayList<String>();
		for (String s : targetDomainPaths) {
			if (StringUtils.isNotEmpty(s)) {
				targetDomainList.add(s);
			}
		}
		Transaction tx = null;
		try {
			tx = new Transaction();
			tx.start();
			ACLUtil.synchronizeDomainACL(sourceDomainPath, targetDomainList);
			tx.commit();
		} catch (WTException e) {
			if (tx != null) {
				tx.rollback();
				throw e;
			}
		}
	}

	public void importAclFromCSVToMultiDomain(String csvFilePath, String targetDomainPath, boolean cleanBeforeImport) throws RemoteException,
			InvocationTargetException {
		Class argTypes[] = { String.class, String.class, boolean.class };
		Object args[] = { csvFilePath, targetDomainPath, cleanBeforeImport };
		RemoteMethodServer.getDefault().invoke("_importAclFromCSVToMultiDomain", null, this, argTypes, args);
	}

	public void _importAclFromCSVToMultiDomain(String csvFilePath, String targetDomainPath, boolean cleanBeforeImport) throws IOException {
		if (null == targetDomainPath || targetDomainPath.length() == 0) {
			System.out.println("Invalid target domain path");
			return;
		}
		String[] targetDomainPaths = targetDomainPath.split(",");
		List<String> targetDomainList = new ArrayList<String>();
		for (String s : targetDomainPaths) {
			if (StringUtils.isNotEmpty(s)) {
				targetDomainList.add(s);
			}
		}

		Transaction tx = null;
		try {
			tx = new Transaction();
			tx.start();
			List<Hashtable> ruleList = ACLImporter.parseAclCsvFile(csvFilePath);
			ACLImporter.createAclIntoMultiDomain(ruleList, targetDomainList, cleanBeforeImport);
			tx.commit();
		} catch (WTException e) {
			if (tx != null) {
				tx.rollback();
			}
		}
	}

	public void refreshAclFromCSVToContainer(String csvFilePath, String targetContainerPath) throws IOException {
		if (null == targetContainerPath || targetContainerPath.length() == 0) {
			System.out.println("Invalid target container path");
			return;
		}

		if (!RemoteMethodServer.ServerFlag) {
			try {
				Class argTypes[] = { String.class, String.class };
				Object args[] = { csvFilePath, targetContainerPath };
				RemoteMethodServer.getDefault().invoke("refreshAclFromCSVToContainer", null, this, argTypes, args);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {

			try {
				@SuppressWarnings("rawtypes")
				List<Hashtable> ruleList = ACLImporter.parseAclCsvFile(csvFilePath);
				System.out.println("refresh ACL:");
				ACLImporter.refreshAclIntoContainer(ruleList, targetContainerPath);

			} catch (WTException e) {
				e.printStackTrace();
			}
		}

	}
	
	public void refreshDomainFromCSVToContainer(String csvFilePath, String targetContainerPath) throws IOException {
		if (null == targetContainerPath || targetContainerPath.length() == 0) {
			System.out.println("Invalid target container path");
			return;
		}

		if (!RemoteMethodServer.ServerFlag) {
			try {
				Class argTypes[] = { String.class, String.class };
				Object args[] = { csvFilePath, targetContainerPath };
				RemoteMethodServer.getDefault().invoke("refreshDomainFromCSVToContainer", null, this, argTypes, args);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {

			try {
				@SuppressWarnings("rawtypes")
				Map<String, String> folderToDomainMap = DomainRemappingUtils.parseDomainRemappingCSV(csvFilePath);
				System.out.println("refresh domain:");
				DomainRemappingUtils.remappingDomain(targetContainerPath, folderToDomainMap);

			} catch (WTException|WTPropertyVetoException e) {
				e.printStackTrace();
			}
		}

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

	public static void main(String[] args) throws InvocationTargetException, IOException {
		if (args == null || args.length == 0) {
			printUsage();
			return;
		}

		ACLHelper helper = new ACLHelper();

		if ("clean".equals(args[0])) {
			// delete domain ACLs
			// java className clean domainPath username password
			checkArgs(args, 4);
			setToken(args[2], args[3]);
			helper.cleanDomainACL(args[1]);
			return;
		}

		if ("export".equals(args[0])) {
			// export ACL to CSV format file
			// java className export domainPath csvFilePath username password
			checkArgs(args, 5);
			setToken(args[3], args[4]);
			helper.exportDomainACL(args[1], args[2]);
			return;
		}

		if ("export_with_child".equals(args[0])) {
			// export ACL to CSV format file
			// java className export domainPath csvFilePath username password
			checkArgs(args, 5);
			setToken(args[3], args[4]);
			helper.exportDomainACL(args[1], args[2], true);
			return;
		}

		if ("sync".equals(args[0])) {
			// export ACL to CSV format file
			// java className sync [domainPath] [target_domain_path1,target_domain_path2...] [username] [password]
			checkArgs(args, 5);
			setToken(args[3], args[4]);
			helper.synchronizeDomainACL(args[1], args[2]);
			return;
		}

		if ("importcsv".equals(args[0])) {
			// export ACL to CSV format file
			// java className importcsv [csv_file_path] [target_domain_path1,target_domain_path2...] [username]
			// [password]
			checkArgs(args, 5);
			setToken(args[3], args[4]);
			helper.importAclFromCSVToMultiDomain(args[1], args[2], false);
			return;
		}

		if ("fillwith".equals(args[0])) {
			// export ACL to CSV format file
			// java className fillwith [csv_file_path] [target_domain_path1,target_domain_path2...] [username]
			// [password]
			checkArgs(args, 5);
			setToken(args[3], args[4]);
			helper.importAclFromCSVToMultiDomain(args[1], args[2], true);
			return;
		}

		if ("refresh".equals(args[0])) {
			// refresh ACL to container
			// java className refresh [csv_file_path] [target_container_path] [username] [password]
			checkArgs(args, 5);
			setToken(args[3], args[4]);
			helper.refreshAclFromCSVToContainer(args[1], args[2]);
			return;
		}
		
		if ("refresh_domain".equals(args[0])) {
			// refresh domain using container
			// java className refresh_domain [csv_file_path] [target_container_path] [username] [password]
			checkArgs(args, 5);
			setToken(args[3], args[4]);
			helper.refreshDomainFromCSVToContainer(args[1], args[2]);
			return;
		}		

		printUsage();
	}

}
