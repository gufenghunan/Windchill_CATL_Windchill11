package com.catl.common.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import wt.inf.container.WTContainerHelper;
import wt.org.DirectoryContextProvider;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTOrganization;
import wt.org.WTUser;
import wt.session.SessionServerHelper;
import wt.util.WTException;

public class OrganizationUtil {

	public static List<WTUser> getUserFormOrgGroup(String orgName){
		List<WTUser> retList = new ArrayList<WTUser>();
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		try {	    	
	    	DirectoryContextProvider dcp = WTContainerHelper.service.getExchangeContainer().getContextProvider();
			WTOrganization org = OrganizationServicesHelper.manager.getOrganization("CATL", dcp);
			Enumeration<?> enu = OrganizationServicesHelper.manager.getGroups(orgName, WTContainerHelper.service.getOrgContainer(org).getContextProvider());
			while(enu.hasMoreElements()){
				Object o = enu.nextElement();
				if(o instanceof WTGroup){
					WTGroup group = (WTGroup)o;
					Enumeration users =  group.members();
					while(users.hasMoreElements()){
						WTUser user = (WTUser)users.nextElement();
						retList.add(user);
					}
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}finally{
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		
		return retList;
	}
}
