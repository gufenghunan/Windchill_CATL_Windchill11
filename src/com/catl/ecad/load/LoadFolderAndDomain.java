package com.catl.ecad.load;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.Scanner;

import wt.admin.AdminDomainRef;
import wt.admin.AdministrativeDomain;
import wt.admin.AdministrativeDomainHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.Cabinet;
import wt.folder.FolderHelper;
import wt.folder.FolderNotFoundException;
import wt.folder.SubFolder;
import wt.inf.container.WTContainerRef;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.pdmlink.PDMLinkProduct;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class LoadFolderAndDomain implements RemoteAccess {

	public static void main(String[] args) throws RemoteException, InvocationTargetException, WTException {
		Scanner sc = new Scanner(System.in);
		System.out.println("请选择操作：\n\tA、按产品库新增文件夹\n\tB、在父级文件夹下新增子文件夹");
		String opt = sc.nextLine();
		if (opt.equalsIgnoreCase("A")) {
			System.out.println("1、请输入要过滤的产品库前缀(多个以英文逗号隔开):");
			String prefix = sc.nextLine();
			System.out.println("2、请输入新增子文件夹名称:");
			String childFolderName = sc.nextLine();
			System.out.println("3、请输入域路径:");
			String domainpath = sc.nextLine();

			batchAddFolderAndDomainByProduct(prefix, childFolderName, domainpath);
		} else if (opt.equalsIgnoreCase("B")) {
			System.out.println("1、请输入父级文件夹名称:");
			String parentFolderName = sc.nextLine();
			System.out.println("2、请输入新增子文件夹名称:");
			String childFolderName = sc.nextLine();
			System.out.println("3、请输入域路径:");
			String domainpath = sc.nextLine();

			batchAddFolderAndDomain(parentFolderName, childFolderName, domainpath);
		}

	}

	public static void batchAddFolderAndDomain(String parentFolderName, String childFolderName, String domainPath)
			throws RemoteException, InvocationTargetException, WTException {
		RemoteMethodServer rm = RemoteMethodServer.getDefault();
		Scanner sc = new Scanner(System.in);
		System.out.println("1、请输入用户名:");
		String username = sc.nextLine();
		System.out.println("2、请输入密码:");
		String password = sc.nextLine();

		rm.setUserName(username);
		rm.setPassword(password);
		SessionHelper.manager.setAuthenticatedPrincipal("wcadmin");
		Class[] clazz = { String.class, String.class, String.class };
		Object[] params = { parentFolderName, childFolderName, domainPath };

		rm.invoke("addFolderAndDomain", LoadFolderAndDomain.class.getName(), null, clazz, params);
	}

	public static void batchAddFolderAndDomainByProduct(String prefix, String childFolderName, String domainPath)
			throws RemoteException, InvocationTargetException, WTException {
		RemoteMethodServer rm = RemoteMethodServer.getDefault();
		Scanner sc = new Scanner(System.in);
		System.out.println("1、请输入用户名:");
		String username = sc.nextLine();
		System.out.println("2、请输入密码:");
		String password = sc.nextLine();

		rm.setUserName(username);
		rm.setPassword(password);
		SessionHelper.manager.setAuthenticatedPrincipal("wcadmin");
		Class[] clazz = { String.class, String.class, String.class };
		Object[] params = { prefix, childFolderName, domainPath };

		rm.invoke("addFolderAndDomainByProduct", LoadFolderAndDomain.class.getName(), null, clazz, params);
	}

	public static void addFolderAndDomain(String parentFolderName, String childFolderName, String domainPath)
			throws WTException, WTPropertyVetoException {
		QuerySpec qs = new QuerySpec(SubFolder.class);
		SearchCondition sc = new SearchCondition(SubFolder.class, SubFolder.NAME, SearchCondition.EQUAL,
				parentFolderName);
		qs.appendWhere(sc);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		System.out.println(qr.size());
		while (qr.hasMoreElements()) {
			SubFolder parentfolder = (SubFolder) qr.nextElement();
			WTContainerRef containerRef = parentfolder.getContainerReference();
			System.out.println(parentfolder.getFolderPath());
			// String path = folder.getFolderPath()+"/"+"ECAD图档";
			// AdministrativeDomain add = new AdministrativeDomain();
			// FolderHelper.service.createSubFolder(path, add.getDomainRef());

			String folderPath = parentfolder.getFolderPath() + "/" + childFolderName;
			// String domainPath = "/Default/CATIA图档";

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
					FolderHelper.service.updateCabinet(cabinet, cabinet.getName(),
							AdminDomainRef.newAdminDomainRef(domain));
				} else {
					// 如果指定的domain存在，则比较指定的domain和现有的domain是否相同
					AdminDomainRef currentDomainRef = cabinet.getDomainRef();
					if (!currentDomainRef.equals(AdminDomainRef.newAdminDomainRef(domain))) {
						// 将Cabinet的domain更新为指定的domain
						FolderHelper.service.updateCabinet(cabinet, cabinet.getName(),
								AdminDomainRef.newAdminDomainRef(domain));
					}
				}
			} else {
				if (isContinue(folderPath, containerRef)) {
					continue;
				}
				SubFolder folder = null;
				try {
					folder = (SubFolder) FolderHelper.service.getFolder(folderPath, containerRef);
				} catch (FolderNotFoundException folderNotFound) {
					// folderNotFound.printStackTrace();
					folder = FolderHelper.service.createSubFolder(folderPath, containerRef);
				}
				// if (folder == null) {
				// System.out.println("***Warning*** Cannot get folder by folder
				// path:" + folderPath + " in container " + containerRef);
				// continue;
				// }

				// 获得指定的domain
				AdministrativeDomain domain = AdministrativeDomainHelper.manager.getDomain(domainPath, containerRef);

				if (domain == null) {
					// 如果指定的domain不存在，则创建新的domain
					domain = createDomain(domainPath, containerRef);
					// 将Folder的domain更新为新创建的domain
					FolderHelper.service.updateSubFolder(folder, folder.getName(),
							AdminDomainRef.newAdminDomainRef(domain), false);
				} else {
					AdminDomainRef currentDomainRef = folder.getDomainRef();
					if (!currentDomainRef.equals(AdminDomainRef.newAdminDomainRef(domain))) {
						// 将Folder的domain更新为指定的domain
						FolderHelper.service.updateSubFolder(folder, folder.getName(),
								AdminDomainRef.newAdminDomainRef(domain), false);
					}
				}

			}

		}

		// return qr;
	}

	public static void addFolderAndDomainByProduct(String prefix, String childFolderName, String domainPath)
			throws WTException, WTPropertyVetoException {
		QuerySpec qs = new QuerySpec(PDMLinkProduct.class);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		System.out.println(qr.size());
		while (qr.hasMoreElements()) {
			PDMLinkProduct product = (PDMLinkProduct) qr.nextElement();
			String productName = product.getName();
			boolean isfilter = true;
			String[] prefixs = prefix.split(",");
			for (int i = 0; i < prefixs.length; i++) {
				if (productName.startsWith(prefixs[i])) {
					isfilter = false;
				}
			}
			if (isfilter) {
				ReferenceFactory factory = new ReferenceFactory();
				WTContainerRef containerRef = (WTContainerRef) factory.getReference(product);

				String folderPath = "/Default/" + childFolderName;
				// String domainPath = "/Default/CATIA图档";

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
					AdministrativeDomain domain = AdministrativeDomainHelper.manager.getDomain(domainPath,
							containerRef);

					if (domain == null) {
						// 如果指定的domain不存在，则创建新的domain
						domain = createDomain(domainPath, containerRef);
						FolderHelper.service.updateCabinet(cabinet, cabinet.getName(),
								AdminDomainRef.newAdminDomainRef(domain));
					} else {
						// 如果指定的domain存在，则比较指定的domain和现有的domain是否相同
						AdminDomainRef currentDomainRef = cabinet.getDomainRef();
						if (!currentDomainRef.equals(AdminDomainRef.newAdminDomainRef(domain))) {
							// 将Cabinet的domain更新为指定的domain
							FolderHelper.service.updateCabinet(cabinet, cabinet.getName(),
									AdminDomainRef.newAdminDomainRef(domain));
						}
					}
				} else {
					if (isContinue(folderPath, containerRef)) {
						continue;
					}
					SubFolder folder = null;
					try {
						folder = (SubFolder) FolderHelper.service.getFolder(folderPath, containerRef);
					} catch (FolderNotFoundException folderNotFound) {
						// folderNotFound.printStackTrace();
						folder = FolderHelper.service.createSubFolder(folderPath, containerRef);
					}

					// 获得指定的domain
					AdministrativeDomain domain = AdministrativeDomainHelper.manager.getDomain(domainPath,
							containerRef);

					if (domain == null) {
						// 如果指定的domain不存在，则创建新的domain
						domain = createDomain(domainPath, containerRef);
						// 将Folder的domain更新为新创建的domain
						FolderHelper.service.updateSubFolder(folder, folder.getName(),
								AdminDomainRef.newAdminDomainRef(domain), false);
					} else {
						AdminDomainRef currentDomainRef = folder.getDomainRef();
						if (!currentDomainRef.equals(AdminDomainRef.newAdminDomainRef(domain))) {
							// 将Folder的domain更新为指定的domain
							FolderHelper.service.updateSubFolder(folder, folder.getName(),
									AdminDomainRef.newAdminDomainRef(domain), false);
						}
					}

				}
			}

		}

		// return qr;
	}

	private static boolean isContinue(String folderPath, WTContainerRef containerRef) throws WTException {
		if (folderPath.lastIndexOf('/') > 0) {
			String parentFolderPath = folderPath.substring(0, folderPath.lastIndexOf('/'));
			if (!"/Default".equals(parentFolderPath)) {
				SubFolder folder = null;
				try {
					folder = (SubFolder) FolderHelper.service.getFolder(parentFolderPath, containerRef);
				} catch (FolderNotFoundException folderNotFound) {
					folderNotFound.printStackTrace();
				}
				if (folder == null) {
					System.out.println("***Warning*** Cannot get folder by folder path:" + parentFolderPath
							+ " in container " + containerRef);
					return true;
				}
			}
		}
		return false;
	}

	private static AdministrativeDomain createDomain(String domainPath, WTContainerRef containerRef)
			throws WTException, WTPropertyVetoException {
		AdministrativeDomain domain = AdministrativeDomainHelper.manager.getDomain(domainPath, containerRef);
		if (domain != null) {
			return domain;
		}

		// /Default/domainA/domainB/domainC
		if (domainPath.lastIndexOf('/') > 0) {
			String parentDomainPath = domainPath.substring(0, domainPath.lastIndexOf('/'));
			String domainName = domainPath.substring(domainPath.lastIndexOf('/') + 1);
			AdministrativeDomain parentDomain = AdministrativeDomainHelper.manager.getDomain(parentDomainPath,
					containerRef);
			if (parentDomain == null) {
				parentDomain = createDomain(parentDomainPath, containerRef);

			}
			AdminDomainRef newDomainRef = AdministrativeDomainHelper.manager
					.createDomain(AdminDomainRef.newAdminDomainRef(parentDomain), domainName, null, containerRef);
			return (AdministrativeDomain) newDomainRef.getObject();
		} else {
			throw new WTException("Cannot create domain:" + domainPath + " in container " + containerRef);
		}

	}
}
