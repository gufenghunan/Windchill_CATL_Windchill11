package com.catl.line.util;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import com.catl.line.exception.LineException;

import wt.admin.AdministrativeDomain;
import wt.fc.IdentificationObject;
import wt.fc.IdentityHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.folder.SubFolder;
import wt.folder.SubFolderIdentity;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class ChangeFolderName10 implements RemoteAccess{
	public static void main(String[] args) throws WTException, WTPropertyVetoException, RemoteException, InvocationTargetException {
		RemoteMethodServer rm=RemoteMethodServer.getDefault();
		rm.setUserName("wcadmin");
		rm.setPassword("wcadmin");
		rm.invoke("test", ChangeFolderName10.class.getName(), null, null, null);
	}
	public static void test() throws WTException, WTPropertyVetoException{
		List<SubFolder> folders=getFolderByName("FAE交付件");
		for (int i = 0; i < folders.size(); i++) {
			SubFolder folder=folders.get(i);
			SubFolderIdentity identity=(SubFolderIdentity) folder.getIdentificationObject();
			identity.setName("非FAE物料成熟度3升级报告");
			IdentityHelper.service.changeIdentity(folder,identity);
		}
		List<AdministrativeDomain> names=getDomainByName("FAE交付件");
		for (int i = 0; i < names.size(); i++) {
			AdministrativeDomain name=names.get(i);
			name.setName("非FAE物料成熟度3升级报告");
			PersistenceHelper.manager.save(name);
		}
	}
    public static List<SubFolder> getFolderByName(String name) throws WTException{
    	List<SubFolder> list=new ArrayList<SubFolder>();
    	QuerySpec qs = new QuerySpec(SubFolder.class);
		SearchCondition sc = new SearchCondition(SubFolder.class, SubFolder.NAME, SearchCondition.EQUAL, name);
		qs.appendWhere(sc);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		System.out.println(qr.size());
		while(qr.hasMoreElements()){
			SubFolder parentfolder = (SubFolder) qr.nextElement();
			list.add(parentfolder);
		}
		return list;
    }
    public static List<AdministrativeDomain> getDomainByName(String name) throws WTException{
    	List<AdministrativeDomain> list=new ArrayList<AdministrativeDomain>();
    	QuerySpec qs = new QuerySpec(AdministrativeDomain.class);
		SearchCondition sc = new SearchCondition(AdministrativeDomain.class, AdministrativeDomain.NAME, SearchCondition.EQUAL, name);
		qs.appendWhere(sc);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		System.out.println(qr.size());
		while(qr.hasMoreElements()){
			AdministrativeDomain domain = (AdministrativeDomain) qr.nextElement();
			list.add(domain);
		}
		return list;
    }
}
