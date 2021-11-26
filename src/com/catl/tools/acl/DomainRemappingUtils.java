package com.catl.tools.acl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import wt.admin.AdminDomainRef;
import wt.admin.AdministrativeDomain;
import wt.admin.AdministrativeDomainHelper;
import wt.folder.Cabinet;
import wt.folder.FolderHelper;
import wt.folder.FolderNotFoundException;
import wt.folder.SubFolder;
import wt.inf.container.WTContainerRef;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class DomainRemappingUtils {

	public static Map<String, String> parseDomainRemappingCSV(String csvFilePath) throws IOException {
		FileReader csvFile = new FileReader(csvFilePath);
		BufferedReader reader = new BufferedReader(csvFile);
		String line = null;
		Map<String, String> domainMapping = new LinkedHashMap<String, String>();
		while ((line = reader.readLine()) != null) {
			if (StringUtils.isBlank(line)) {
				continue;
			}
			if (line.startsWith("#")) {
				continue;
			}
			String[] params = line.split(",");
			if (params.length < 2) {
				System.out.println("***Warnning*** Invalid domain mapping rule:" + line);
				continue;
			}
			domainMapping.put(params[0], params[1]);
		}
		reader.close();
		csvFile.close();
		return domainMapping;
	}

	public static void remappingDomain(String containerPath, Map<String, String> folderToDomainMap) throws WTException, WTPropertyVetoException {
		AdministrativeDomain defaultDomain = AdministrativeDomainHelper.manager.getDomain(containerPath + "/Default");
		if (defaultDomain == null) {
			System.out.println("***Warning*** Cannot get container by container path:" + containerPath);
			return;
		}

		WTContainerRef containerRef = defaultDomain.getContainerReference();
		for (Map.Entry<String, String> entry : folderToDomainMap.entrySet()) {
			String folderPath = entry.getKey();
			String domainPath = entry.getValue();

			if (!folderPath.startsWith("/")) {
				System.out.println("***Warning*** Invalid folder path:" + folderPath);
				continue;
			}

			if (!folderPath.startsWith("/Default")) {
				folderPath = "/Default" + folderPath;
			}

			if (!"/".equals(folderPath) && folderPath.endsWith("/")) {
				folderPath = folderPath.substring(0, folderPath.length() - 1);
			}

			if ("/Default".equals(folderPath)) {
				Cabinet cabinet = FolderHelper.service.getCabinet("Default", containerRef);

				// 获得指定的domain
				AdministrativeDomain domain = AdministrativeDomainHelper.manager.getDomain(domainPath, containerRef);

				if (domain == null) {
					// 如果指定的domain不存在，则创建新的domain
					domain = createDomain(domainPath, containerRef);
					FolderHelper.service.updateCabinet(cabinet, cabinet.getName(), AdminDomainRef.newAdminDomainRef(domain));
				} else {
					// 如果指定的domain存在，则比较指定的domain和现有的domain是否相同
					AdminDomainRef currentDomainRef = cabinet.getDomainRef();
					if (!currentDomainRef.equals(AdminDomainRef.newAdminDomainRef(domain))) {
						// 将Cabinet的domain更新为指定的domain
						FolderHelper.service.updateCabinet(cabinet, cabinet.getName(), AdminDomainRef.newAdminDomainRef(domain));
					}
				}
			} else {
				if(isContinue(folderPath, containerRef)){
					continue;
				}
				SubFolder folder = null;
				try {
					folder = (SubFolder) FolderHelper.service.getFolder(folderPath, containerRef);
				} catch (FolderNotFoundException folderNotFound) {
					//folderNotFound.printStackTrace();
					folder = FolderHelper.service.createSubFolder(folderPath, containerRef);
				}
//				if (folder == null) {
//					System.out.println("***Warning*** Cannot get folder by folder path:" + folderPath + " in container " + containerRef);
//					continue;
//				}

				// 获得指定的domain
				AdministrativeDomain domain = AdministrativeDomainHelper.manager.getDomain(domainPath, containerRef);

				if (domain == null) {
					// 如果指定的domain不存在，则创建新的domain
					domain = createDomain(domainPath, containerRef);
					// 将Folder的domain更新为新创建的domain
					FolderHelper.service.updateSubFolder(folder, folder.getName(), AdminDomainRef.newAdminDomainRef(domain), false);
				} else {
					AdminDomainRef currentDomainRef = folder.getDomainRef();
					if (!currentDomainRef.equals(AdminDomainRef.newAdminDomainRef(domain))) {
						// 将Folder的domain更新为指定的domain
						FolderHelper.service.updateSubFolder(folder, folder.getName(), AdminDomainRef.newAdminDomainRef(domain), false);
					}
				}

			}
		}

	}
	
	private static boolean isContinue(String folderPath, WTContainerRef containerRef) throws WTException{
		if (folderPath.lastIndexOf('/') > 0) {
			String parentFolderPath = folderPath.substring(0, folderPath.lastIndexOf('/'));
			if(!"/Default".equals(parentFolderPath)){
				SubFolder folder = null;
				try {
					folder = (SubFolder) FolderHelper.service.getFolder(parentFolderPath, containerRef);
				} catch (FolderNotFoundException folderNotFound) {
					folderNotFound.printStackTrace();
				}
				if (folder == null) {
					System.out.println("***Warning*** Cannot get folder by folder path:" + parentFolderPath + " in container " + containerRef);
					return true;
				}
			}
		}
		return false;
	}

	private static AdministrativeDomain createDomain(String domainPath, WTContainerRef containerRef) throws WTException, WTPropertyVetoException {
		AdministrativeDomain domain = AdministrativeDomainHelper.manager.getDomain(domainPath, containerRef);
		if (domain != null) {
			return domain;
		}

		// /Default/domainA/domainB/domainC
		if (domainPath.lastIndexOf('/') > 0) {
			String parentDomainPath = domainPath.substring(0, domainPath.lastIndexOf('/'));
			String domainName = domainPath.substring(domainPath.lastIndexOf('/') + 1);
			AdministrativeDomain parentDomain = AdministrativeDomainHelper.manager.getDomain(parentDomainPath, containerRef);
			if (parentDomain == null) {
				parentDomain = createDomain(parentDomainPath, containerRef);

			}
			AdminDomainRef newDomainRef = AdministrativeDomainHelper.manager.createDomain(AdminDomainRef.newAdminDomainRef(parentDomain), domainName, null,
					containerRef);
			return (AdministrativeDomain) newDomainRef.getObject();
		} else {
			throw new WTException("Cannot create domain:" + domainPath + " in container " + containerRef);
		}

	}

}
