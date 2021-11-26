package com.catl.tools.container;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.util.WTException;

public class ContainerRoleTool implements RemoteAccess, Serializable {

	private static final long serialVersionUID = -2546609119372807681L;

	public void appendContainerRole(String filePath) throws RemoteException, InvocationTargetException {
		Class argTypes[] = { String.class };
		Object args[] = { filePath };
		RemoteMethodServer.getDefault().invoke("_appendContainerRole", null, this, argTypes, args);
	}

	public void _appendContainerRole(String filePath) throws IOException, WTException {
		CSVContainerRoleMapParser parser = new CSVContainerRoleMapParser();
		FileInputStream fis = new FileInputStream(filePath);
		ContainerRoleMap roleMap = parser.parser(fis);
		AppendOnlyContainerTeamUpdater updater = new AppendOnlyContainerTeamUpdater();
		updater.updateContainerTeam(roleMap);
	}
	
	public void updateContainerRole(String filePath) throws RemoteException, InvocationTargetException {
		Class argTypes[] = { String.class };
		Object args[] = { filePath };
		RemoteMethodServer.getDefault().invoke("_updateContainerRole", null, this, argTypes, args);
	}

	public void _updateContainerRole(String filePath) throws IOException, WTException {
		CSVContainerRoleMapParser parser = new CSVContainerRoleMapParser();
		FileInputStream fis = new FileInputStream(filePath);
		ContainerRoleMap roleMap = parser.parser(fis);
		ExistRoleOnlyContainerTeamUpdater updater = new ExistRoleOnlyContainerTeamUpdater();
		updater.updateContainerTeam(roleMap);
	}

	public void removeContainerRole(String filePath) throws RemoteException, InvocationTargetException {
		Class argTypes[] = { String.class };
		Object args[] = { filePath };
		RemoteMethodServer.getDefault().invoke("_removeContainerRole", null, this, argTypes, args);
	}

	public void _removeContainerRole(String filePath) throws IOException, WTException {
		CSVContainerRoleMapParser parser = new CSVContainerRoleMapParser();
		FileInputStream fis = new FileInputStream(filePath);
		ContainerRoleMap roleMap = parser.parser(fis);
		RemoveContainerTeamUpdater updater = new RemoveContainerTeamUpdater();
		updater.updateContainerTeam(roleMap);
	}
	
	public void fullUpdateContainerRole(String filePath) throws RemoteException, InvocationTargetException {
		Class argTypes[] = { String.class };
		Object args[] = { filePath };
		RemoteMethodServer.getDefault().invoke("_fullUpdateContainerRole", null, this, argTypes, args);
	}

	public void _fullUpdateContainerRole(String filePath) throws IOException, WTException {
		CSVContainerRoleMapParser parser = new CSVContainerRoleMapParser();
		FileInputStream fis = new FileInputStream(filePath);
		ContainerRoleMap roleMap = parser.parser(fis);
		StrictlyMatchContainerTeamUpdater updater = new StrictlyMatchContainerTeamUpdater();
		updater.updateContainerTeam(roleMap);
	}

	private static void setToken(String username, String password) {
		RemoteMethodServer.getDefault().setUserName(username);
		RemoteMethodServer.getDefault().setPassword(password);
	}

	private static void printUsage() {
		System.out.println("Windchill 10.x ContainerTeam update tools");
		System.out.println("Usage:");
		System.out.println("1) append role & principal to a ContainerTeam");
		System.out.println("windchill " + ContainerRoleTool.class.getName() + " append <csv_file_full_path> <user> <password>");
		System.out.println("");
		System.out.println("2) strictly match update role & principal to a ContainerTeam");
		System.out.println("windchill " + ContainerRoleTool.class.getName() + " fullupdate <csv_file_full_path> <user> <password>");
		System.out.println("");
		System.out.println("3) update exist role in CSV file only to a ContainerTeam");
		System.out.println("windchill " + ContainerRoleTool.class.getName() + " update <csv_file_full_path> <user> <password>");
		System.out.println("");
		System.out.println("4) remove exist role/principal in CSV file to a ContainerTeam");
		System.out.println("windchill " + ContainerRoleTool.class.getName() + " remove <csv_file_full_path> <user> <password>");			
	}

	public static void main(String[] args) throws RemoteException, InvocationTargetException {
		if (args == null || args.length < 4) {
			printUsage();
			return;
		}

		ContainerRoleTool tool = new ContainerRoleTool();
		if ("append".equals(args[0])) {
			setToken(args[2], args[3]);
			tool.appendContainerRole(args[1]);
			return;
		}

		if ("fullupdate".equals(args[0])) {
			setToken(args[2], args[3]);
			tool.fullUpdateContainerRole(args[1]);
			return;
		}
		
		if ("update".equals(args[0])) {
			setToken(args[2], args[3]);
			tool.updateContainerRole(args[1]);
			return;
		}

		if ("remove".equals(args[0])) {
			setToken(args[2], args[3]);
			tool.removeContainerRole(args[1]);
			return;
		}
		
	}

}
