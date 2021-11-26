package com.catl.tools.container;

import java.util.Enumeration;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.container.PrincipalSpec;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.inf.library.WTLibrary;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.org.DirectoryContextProvider;
import wt.org.OrganizationServicesHelper;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.orgResource;
import wt.pdmlink.PDMLinkProduct;
import wt.pom.PersistentObjectManager;
import wt.pom.Transaction;
import wt.projmgmt.admin.Project2;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;

public abstract class BaseContainerTeamUpdater implements ContainerTeamUpdater {

	public void updateContainerTeam(ContainerRoleMap roleMap) throws WTException {
		Transaction tx = null;
		try {
            if (PersistentObjectManager.getTransactionManager().isTransactionActive()) {
                tx = Transaction.getCurrentTransaction();
            } else {
                tx = new Transaction();
                tx.start();
            }
            
			// update container team of prodcut context
			Set<String> productList = roleMap.getProduct();
			for (String productName : productList) {
				PDMLinkProduct product = getPDMLinProductByName(productName);
				if (product == null) {
					System.out.println("Cannot get PDMLinkProduct by name:" + productName);
					continue;
				}
				PersistenceHelper.manager.refresh(product);
				ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam(product, false);
				product = (PDMLinkProduct) PersistenceHelper.manager.refresh(product);
				doUpdate(product, containerTeam, roleMap);
				containerTeam = (ContainerTeam) PersistenceHelper.manager.refresh(containerTeam);
				ContainerTeamHelper.service.updateContainerTeam(containerTeam);
				System.out.println("PDMLinkProduct " + productName + " role map updated.");
			}

			// update container team of library context
			Set<String> libraryList = roleMap.getLibrary();
			for (String libraryName : libraryList) {
				WTLibrary library = getWTLibraryByName(libraryName);
				if (library == null) {
					System.out.println("Cannot get WTLibrary by name:" + libraryName);
					continue;
				}
				ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam(library, false);
				library = (WTLibrary) PersistenceHelper.manager.refresh(library);
				doUpdate(library, containerTeam, roleMap);
				containerTeam = (ContainerTeam) PersistenceHelper.manager.refresh(containerTeam);
				ContainerTeamHelper.service.updateContainerTeam(containerTeam);
				System.out.println("WTLibrary " + libraryName + " role map updated.");
			}

			// update project team of project context
			Set<String> projectList = roleMap.getProject();
			for (String projectName : projectList) {
				Project2 project = getProjectByName(projectName);
				if (project == null) {
					System.out.println("Cannot get Project2 by name:" + projectName);
					continue;
				}
				ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam(project, false);
				project = (Project2) PersistenceHelper.manager.refresh(project);
				doUpdate(project, containerTeam, roleMap);
				containerTeam = (ContainerTeam) PersistenceHelper.manager.refresh(containerTeam);
				ContainerTeamHelper.service.updateContainerTeam(containerTeam);
				System.out.println("Project " + projectName + " role map updated.");
			}

			tx.commit();
			tx = null;
		} catch (WTException e) {
			e.printStackTrace();
			if (tx != null) {
				tx.rollback();
			}
			throw e;
		}
	}

	protected abstract void doUpdate(WTContainer container, ContainerTeam containerTeam, ContainerRoleMap roleMap) throws WTException;

	public static WTLibrary getWTLibraryByName(String name) throws WTException {
		if (StringUtils.isBlank(name)) {
			throw new IllegalArgumentException("WTLibrary name cannot be empty or null!");
		}
		QuerySpec qs = new QuerySpec(WTLibrary.class);
		int idx1 = qs.addClassList(WTLibrary.class, true);
		qs.appendWhere(new SearchCondition(WTLibrary.class, WTLibrary.NAME, SearchCondition.EQUAL, name), new int[] { idx1 });
		QueryResult rs = PersistenceHelper.manager.find(qs);
		while (rs.hasMoreElements()) {
			return (WTLibrary) rs.nextElement();
		}
		return null;
	}

	public static PDMLinkProduct getPDMLinProductByName(String name) throws WTException {
		if (StringUtils.isBlank(name)) {
			throw new IllegalArgumentException("PDMLinkProduct name cannot be empty or null!");
		}
		QuerySpec qs = new QuerySpec(PDMLinkProduct.class);
		int idx1 = qs.addClassList(PDMLinkProduct.class, true);
		qs.appendWhere(new SearchCondition(PDMLinkProduct.class, PDMLinkProduct.NAME, SearchCondition.EQUAL, name), new int[] { idx1 });
		QueryResult rs = PersistenceHelper.manager.find(qs);
		while (rs.hasMoreElements()) {
			return (PDMLinkProduct) rs.nextElement();
		}
		return null;
	}

	public static Project2 getProjectByName(String name) throws WTException {
		if (StringUtils.isBlank(name)) {
			throw new IllegalArgumentException("project name cannot be empty or null!");
		}
		QuerySpec qs = new QuerySpec(Project2.class);
		int idx1 = qs.addClassList(Project2.class, true);
		qs.appendWhere(new SearchCondition(Project2.class, Project2.NAME, SearchCondition.EQUAL, name), new int[] { idx1 });
		QueryResult rs = PersistenceHelper.manager.find(qs);
		while (rs.hasMoreElements()) {
			return (Project2) rs.nextElement();
		}
		return null;
	}

	public static WTPrincipal getWTPrincipalByName(WTContainer container, String principal) throws WTException {
		WTPrincipalReference principalRef = null;
		PrincipalSpec principalSpec = new PrincipalSpec(WTContainerRef.newWTContainerRef(container), WTPrincipal.class);
		DirectoryContextProvider[] contexts = WTContainerHelper.service.getPublicContextProviders(principalSpec);

		WTPrincipal thePrincipal = null;
		String criteria = WTPrincipal.NAME + "='" + principal + "'";
		Enumeration results = OrganizationServicesHelper.manager.queryPrincipals(WTPrincipal.class, criteria, contexts);
		if (results.hasMoreElements()) {
			thePrincipal = (WTPrincipal) results.nextElement();
			principalRef = WTPrincipalReference.newWTPrincipalReference(thePrincipal);
		}
		if (results.hasMoreElements()) {
			throw new wt.org.AmbiguousPrincipalException(null, "wt.org.orgResource", orgResource.MULTI_DB_HIT, new Object[] { principal });
		}
		return thePrincipal;
	}

}
