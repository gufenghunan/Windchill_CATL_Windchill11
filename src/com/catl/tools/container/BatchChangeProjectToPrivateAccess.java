package com.catl.tools.container;

import wt.admin.AdminDomainRef;
import wt.fc.ObjectIdentifier;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.container.WTContainerHelper;
import wt.method.RemoteMethodServer;
import wt.projmgmt.admin.Project2;
import wt.query.QuerySpec;
import wt.util.WTContext;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class BatchChangeProjectToPrivateAccess {

	public static void main(String[] args) throws WTPropertyVetoException, WTException {

		String windchillURL = "http://catl-plm-dev.catlbattery.com/Windchill/";
		System.setProperty("wt.server.codebase", windchillURL);
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		RemoteMethodServer.setPrivateAffinity(true);
		rms.setUserName("orgadmin");
		rms.setPassword("1");
		WTContext.init(args);

		QuerySpec qs = new QuerySpec();
		int idx1 = qs.appendClassList(Project2.class, true);
		qs.setQuerySet(false);
		QueryResult rs = PersistenceHelper.manager.find(qs);
		while (rs.hasMoreElements()) {
			Project2 project = (Project2) rs.nextElement();
			if (project.isPrivateAccess()) {
				AdminDomainRef selectedRef = AdminDomainRef.newAdminDomainRef(ObjectIdentifier.newObjectIdentifier("wt.admin.AdministrativeDomain:6343"));
				project = (Project2) WTContainerHelper.service.changePublicParentDomain(project, selectedRef);
				WTContainerHelper.service.makePublic(project);
				System.out.println("Project " + project.getName() + " has been change from private access to Default !");
			} else {
				System.out.println("Project " + project.getName() + " is private access, ingore!");
			}
		}
	}

}
