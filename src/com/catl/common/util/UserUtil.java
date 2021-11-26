package com.catl.common.util;

import java.io.File;
import java.util.Enumeration;

import wt.httpgw.GatewayAuthenticator;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.inf.container.WTContainerServerHelper;
import wt.inf.library.WTLibrary;
import wt.method.RemoteMethodServer;
import wt.org.DirectoryContextProvider;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTOrganization;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTProperties;

public class UserUtil {

	/**
	 * 判断是否为站点管理员
	 * @param wtPrincipal
	 * @return
	 */
	public static boolean isSiteAdmin(WTPrincipal wtPrincipal) {
        try {
            return WTContainerHelper.service.isAdministrator(WTContainerHelper.service.getExchangeRef(), wtPrincipal);
        } catch (WTException e) {
            e.printStackTrace();
        }
        return false;
    }
	
	/**
     * 判断用户是否是组织管理员
     * 
     * @param wtprincipal
     * @return
     */
    public static boolean isOrgAdministator(WTPrincipal wtprincipal) {
        return isOrgAdministator(wtprincipal, "CATL");
    }
	
    public static boolean isOrgAdministator(WTPrincipal wtprincipal, String strOrgName) {
        try {
            DirectoryContextProvider dcp = WTContainerHelper.service.getExchangeContainer().getContextProvider();
            WTOrganization org = OrganizationServicesHelper.manager.getOrganization(strOrgName, dcp);
            if (org != null) {
                WTContainerRef wtcontainerref = WTContainerHelper.service.getOrgContainerRef(org);
                if (wtcontainerref != null) {
                    if (WTContainerHelper.service.isAdministrator(wtcontainerref, wtprincipal)) {
                        return true;
                    }
                }
            } else {
                System.out.println("WTOrganization is null.");
            }
        } catch (WTException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    
    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        RemoteMethodServer rms = RemoteMethodServer.getDefault();
        GatewayAuthenticator auth = new GatewayAuthenticator();
        auth.setRemoteUser("wcadmin");
        rms.setAuthenticator(auth);
        deleteAllUser();
    }
    
    
    public static void deleteAllUser(){
        try {
            Enumeration e = OrganizationServicesHelper.manager.allUsers();
            
            while(e.hasMoreElements()){
                WTUser u = (WTUser)e.nextElement();
                if(isSiteAdmin(u)){
                    System.out.println("user accont = " + u.getName() + " fulle name =" + u.getFullName() );
                    
                }else{
                    System.out.println("user " + u.getName() + " will be deleted");
                    OrganizationServicesHelper.manager.delete(u); 
                    
                }
             }
            
        } catch (WTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public static boolean checkCurrentUserInOrgGroup(String groupName){
    	boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
    	try {
			DirectoryContextProvider dcp = WTContainerHelper.service.getExchangeContainer().getContextProvider();
			WTOrganization org = OrganizationServicesHelper.manager.getOrganization("CATL", dcp);
			Enumeration<?> enu = OrganizationServicesHelper.manager.getGroups(groupName, WTContainerHelper.service.getOrgContainer(org).getContextProvider());
			while(enu.hasMoreElements()){
				Object o = enu.nextElement();
				if(o instanceof WTGroup){
					WTGroup group = (WTGroup)o;
					if(group.isMember(SessionHelper.manager.getPrincipal())){
						return true;
					}
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
    	
    	return false;
    }
    
    /**
     * 检查用户是否为存储库团队存储库管理者中的成员
     * @param library
     * @param wtprincipal
     * @return
     * @throws WTException
     */
    public static boolean isLibraryManager(WTLibrary library,WTPrincipal wtprincipal) throws WTException{
    	if(library != null && wtprincipal != null){
    		WTGroup group = WTContainerServerHelper.getAdministratorsReadOnly(library);
    		return OrganizationServicesHelper.manager.isMember(group,wtprincipal);
    	}
    	return false;
    }
    
}
