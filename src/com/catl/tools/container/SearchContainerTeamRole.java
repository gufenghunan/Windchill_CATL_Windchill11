package com.catl.tools.container;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.inf.container.ExchangeContainer;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.LifeCycleTemplateReference;
import wt.lifecycle.State;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTOrganization;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.part.WTPart;
import wt.pdmlink.PDMLinkProduct;
import wt.pds.StatementSpec;
import wt.project.Role;
import wt.projmgmt.admin.Project2;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.type.ClientTypedUtility;
import wt.type.TypeDefinitionReference;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

public class SearchContainerTeamRole implements RemoteAccess, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -724622068155203726L;

	private static void setToken(String username, String password) {
		RemoteMethodServer.getDefault().setUserName(username);
		RemoteMethodServer.getDefault().setPassword(password);
	}

	protected List<WTObject> getPDMLinkProduct(String strDate) throws WTException {

		List<WTObject> pList = new ArrayList<WTObject>();
		QuerySpec qs = new QuerySpec(PDMLinkProduct.class);
		SearchCondition sc = new SearchCondition(PDMLinkProduct.class, PDMLinkProduct.CREATE_TIMESTAMP, SearchCondition.GREATER_THAN, Timestamp.valueOf(strDate));
		qs.appendWhere(sc, new int[] { 0 });
		QueryResult qr = PersistenceHelper.manager.find(qs);
		PDMLinkProduct product = null;
		while (qr.hasMoreElements()) {
			product = (PDMLinkProduct) qr.nextElement();
			pList.add(product);

			// System.out.println("Name:"+product.getName());
		}

		return pList;
	}

	protected List<WTObject> getProject2(String strDate) throws WTException {

		List<WTObject> pList = new ArrayList<WTObject>();
		QuerySpec qs = new QuerySpec(Project2.class);
		SearchCondition sc = new SearchCondition(Project2.class, Project2.CREATE_TIMESTAMP, SearchCondition.GREATER_THAN, Timestamp.valueOf(strDate));
		qs.appendWhere(sc, new int[] { 0 });
		QueryResult qr = PersistenceHelper.manager.find(qs);
		Project2 project = null;
		while (qr.hasMoreElements()) {
			project = (Project2) qr.nextElement();
			pList.add(project);
			// System.out.println("Name:"+project.getName());

		}

		return pList;
	}

	protected void getContainerTeam(List<WTObject> objList, String strRole, String outFile) throws WTException, IOException {

		FileWriter fw = new FileWriter(outFile, true);

		ContainerTeam containerTeam = null;

		for (WTObject obj : objList) {
			StringBuffer sb = new StringBuffer();
			if (obj instanceof PDMLinkProduct) {
				containerTeam = ContainerTeamHelper.service.getContainerTeam((PDMLinkProduct) obj, false);

				sb.append(((PDMLinkProduct) obj).getName() + "," + ((PDMLinkProduct) obj).getContainerTemplate().getName() + ",");
			} else if (obj instanceof Project2) {
				containerTeam = ContainerTeamHelper.service.getContainerTeam((Project2) obj, false);
				sb.append(((Project2) obj).getProjectNumber() + "," + ((Project2) obj).getName() + "," + ((Project2) obj).getContainerTemplate().getName() + ",");

			}
			ArrayList principals = containerTeam.getAllPrincipalsForTarget(Role.toRole(strRole));
			for (int i = 0; i < principals.size(); i++) {
				WTPrincipalReference pRef = (WTPrincipalReference) principals.get(i);
				WTPrincipal p = pRef.getPrincipal();
				sb.append(p.getName() + ",");
			}
			System.out.println("Team:" + sb.toString());
			fw.write(sb.toString());
			fw.write("\n");
		}

		fw.flush();
		fw.close();
	}

	protected void removeContainerTeamMember(List<WTObject> objList, String strRole) throws WTException, IOException {

		ContainerTeam containerTeam = null;

		for (WTObject obj : objList) {
			if (obj instanceof PDMLinkProduct) {
				containerTeam = ContainerTeamHelper.service.getContainerTeam((PDMLinkProduct) obj, false);
			} else if (obj instanceof Project2) {
				containerTeam = ContainerTeamHelper.service.getContainerTeam((Project2) obj, false);
			}
			ArrayList principals = containerTeam.getAllPrincipalsForTarget(Role.toRole(strRole));
			if (principals.size() > 1) {
				for (int i = 0; i < principals.size(); i++) {
					WTPrincipalReference pRef = (WTPrincipalReference) principals.get(i);
					WTPrincipal p = pRef.getPrincipal();

					if ("Administrator".equalsIgnoreCase(p.getName())) {
						continue;
					} else {
						if (p.getName().contains("50")) {
							System.out.println("Remove Team Member:" + containerTeam.getName() + ":" + strRole + ":" + p.getName());
							ContainerTeamHelper.service.removeMember(containerTeam, Role.toRole(strRole), p);
						}
					}
				}
			}
		}

	}

	public void updateLC(WTObject object, String strLC, String strState) throws WTException {

		if (WorkInProgressHelper.isCheckedOut((Workable) object)) {

		} else {
			if (object instanceof LifeCycleManaged) {
				ExchangeContainer exchangeContainer = WTContainerHelper.service.getExchangeContainer();
				WTOrganization wtorg = exchangeContainer.getOrganization();
				WTContainerRef orgcontainerRef = WTContainerHelper.service.getOrgContainerRef(wtorg);
				LifeCycleTemplateReference lifecycleTemplateRef = LifeCycleHelper.service.getLifeCycleTemplateReference(strLC, orgcontainerRef);
				LifeCycleHelper.service.reassign((LifeCycleManaged) object, lifecycleTemplateRef);
				LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) object, State.toState(strState));

			}
		}

	}

	public void searchPart(String objType, String objSubType, String outfile) throws WTException, IOException {
		FileWriter fw = new FileWriter(outfile, true);

		List<WTObject> objList = searchBySubType(objType, objSubType);
		System.out.println("Part size:" + objList.size());
		for (WTObject obj : objList) {
			WTPart part = (WTPart) obj;
			StringBuffer sb = new StringBuffer();
			sb.append(part.getNumber() + "," + part.getName() + "," + part.getContainer().getName() + "," + part.getContainer().getContainerTemplate().getName());

			System.out.println("Part:" + sb.toString());
			fw.write(sb.toString());
			fw.write("\n");
		}

		fw.flush();
		fw.close();

	}

	private static List<WTObject> searchBySubType(final String strOjb, String strSubType) throws WTException {

		WTObject obj = null;
		List<WTObject> objList = new ArrayList<WTObject>();
		try {
			Class<?> cls = Class.forName(strOjb);
			QuerySpec qs = new QuerySpec(cls);
			qs.setAdvancedQueryEnabled(true);
			int[] index = new int[] { 0 };

			TypeDefinitionReference tdr = ClientTypedUtility.getTypeDefinitionReference(strSubType);
			SearchCondition sc = new SearchCondition(cls, "typeDefinitionReference.key.branchId", SearchCondition.EQUAL, tdr.getKey().getBranchId());
			qs.appendWhere(sc, index);

			qs.appendAnd();
			sc = new SearchCondition(cls, "checkoutInfo.state", SearchCondition.NOT_EQUAL, "wrk");
			qs.appendWhere(sc, index);

			qs.appendAnd();
			sc = new SearchCondition(cls, WTAttributeNameIfc.LATEST_ITERATION, SearchCondition.IS_TRUE);
			qs.appendWhere(sc, index);

			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
			LatestConfigSpec configSpec = new LatestConfigSpec();
			qr = configSpec.process(qr);
			while (qr.hasMoreElements()) {
				obj = (WTObject) qr.nextElement();
				objList.add(obj);
			}
			// logger.info("WTDocument size :"+objList.size());

		} catch (RemoteException e) {
			throw new WTException(e, e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			throw new WTException(e, e.getLocalizedMessage());
		}
		return objList;
	}

	/*
	 * Search ContainerTeam Role <p>usage info for example : <li>windchill ext.mindray.tools.container.SearchContainerTeamRole product
	 * /plm/Windchill_10.2/Windchill/tmp/11.csv 2015-12-01 MINDRAY_MACHINEADMINISTRATOR wcadmin wcadmin <li>windchill
	 * ext.mindray.tools.container.SearchContainerTeamRole project /plm/Windchill_10.2/Windchill/tmp/11.csv 2015-12-01
	 * MINDRAY_MACHINEADMINISTRATOR wcadmin wcadmin <li>windchill ext.mindray.tools.container.SearchContainerTeamRole removeProductMember
	 * 2015-12-01 MINDRAY_MACHINEADMINISTRATOR wcadmin wcadmin <li>windchill ext.mindray.tools.container.SearchContainerTeamRole
	 * removeProjectMember 2015-12-01 MINDRAY_MACHINEADMINISTRATOR wcadmin wcadmin
	 */
	public static void main(String[] args) throws InvocationTargetException, WTException, IOException {

		// ExchangeContainer exchangeContainer = WTContainerHelper.service.getExchangeContainer();
		// WTOrganization wtorg = exchangeContainer.getOrganization();
		// WTContainerRef orgcontainerRef = WTContainerHelper.service.getOrgContainerRef(wtorg);
		// LifeCycleTemplateReference lifecycleTemplateRef =
		// LifeCycleHelper.service.getLifeCycleTemplateReference(ConstantLine.LC_Mindray_TechnicalDoc, orgcontainerRef);
		// LifeCycleHelper.service.reassign(arg0, arg1);
		//
		SearchContainerTeamRole load = new SearchContainerTeamRole();
		if ("product".equals(args[0])) {
			setToken(args[4], args[5]);
			String outFile = args[1];
			String date = args[2];
			String role = args[3];
			List<WTObject> pList = load.getPDMLinkProduct(date + " 00:00:00");
			load.getContainerTeam(pList, role, outFile);

			return;
		}

		if ("project".equals(args[0])) {
			setToken(args[4], args[5]);
			String outFile = args[1];
			String date = args[2];
			String role = args[3];
			List<WTObject> pList = load.getProject2(date + " 00:00:00");
			load.getContainerTeam(pList, role, outFile);

			return;
		}

		if ("removeProductMember".equals(args[0])) {
			setToken(args[3], args[4]);
			String date = args[1];
			String role = args[2];
			List<WTObject> pList = load.getPDMLinkProduct(date + " 00:00:00");
			load.removeContainerTeamMember(pList, role);

			return;
		}

		if ("removeProjectMember".equals(args[0])) {
			setToken(args[3], args[4]);
			String date = args[1];
			String role = args[2];
			List<WTObject> pList = load.getProject2(date + " 00:00:00");
			load.removeContainerTeamMember(pList, role);

			return;
		}

		if ("searchPart".equals(args[0])) {
			setToken(args[1], args[2]);
			String subType = args[3];
			String outFile = args[4];
			load.searchPart("wt.part.WTPart", subType, outFile);

			return;
		}

	}

}
