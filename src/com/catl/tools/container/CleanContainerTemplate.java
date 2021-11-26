package com.catl.tools.container;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.template.WTContainerTemplateMaster;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class CleanContainerTemplate implements RemoteAccess, Serializable{

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) throws WTPropertyVetoException, WTException, RemoteException, InvocationTargetException {


		if (args == null || args.length < 3) {
			printUsage();
			return;
		}
		//template type
		String strType = args[0];
		
		setToken(args[1],args[2]);
		CleanContainerTemplate clean = new CleanContainerTemplate();
		clean.cleanContainerTemplate(strType);
		
	}

	private static void printUsage() {
		System.out.println("Windchill 10.x CleanContainerTemplate tools");
		System.out.println("Usage:");
		System.out.println("windchill " + CleanContainerTemplate.class.getName() + " <type:product/project/library> <user> <password>");
		System.out.println("");

	}
	
	
	public void cleanContainerTemplate(String type) throws RemoteException, InvocationTargetException {
		Class argTypes[] = { String.class };
		Object args[] = { type };
		RemoteMethodServer.getDefault().invoke("_cleanContainerTemplate", null, this, argTypes, args);
	}

	public void _cleanContainerTemplate(String type) throws IOException, WTException, WTPropertyVetoException {
		
		QuerySpec qs = new QuerySpec();
		int idx1 = qs.appendClassList(WTContainerTemplateMaster.class, true);
		qs.appendWhere(new SearchCondition(WTContainerTemplateMaster.class, "containerReference.key.classname", SearchCondition.EQUAL,
				"wt.inf.container.OrgContainer"), new int[] { idx1 });
		qs.setAdvancedQueryEnabled(true);
		qs.setQuerySet(false);
		QueryResult rs = PersistenceHelper.manager.find(qs);
		while (rs.hasMoreElements()) {
			
			WTContainerTemplateMaster templateMaster = (WTContainerTemplateMaster) rs.nextElement();
			
			String templateName = templateMaster.getName();
			System.out.println("Template " + templateName);
			System.out.println("Template index" + templateName.indexOf("产品模板"));
			if(templateName.indexOf("产品模板") == 0){
				if("product".equalsIgnoreCase(type)){
					wt.fc.delete.DeleteHelper.manager.markForDelete(templateMaster);
					System.out.println("Template " + templateName + " has been deleted!");
				}

			} else if(templateName.indexOf("项目模板") == 0){
				if("project".equalsIgnoreCase(type)){
					wt.fc.delete.DeleteHelper.manager.markForDelete(templateMaster);
					System.out.println("Template " + templateName + " has been deleted!");
				}
			} else {
				if("library".equalsIgnoreCase(type)){
					wt.fc.delete.DeleteHelper.manager.markForDelete(templateMaster);
					System.out.println("Template " + templateName + " has been deleted!");
				}
			}
			
			//templateMaster.getType();


		}
	}
	
	private static void setToken(String username, String password) {
		RemoteMethodServer.getDefault().setUserName(username);
		RemoteMethodServer.getDefault().setPassword(password);
	}
	
	
}
