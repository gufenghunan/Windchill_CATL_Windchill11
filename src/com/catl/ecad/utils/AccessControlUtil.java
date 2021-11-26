/*
 * Copyright (c) 2013-2015 SoftEasy. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of SoftEasy and is
 * subject to the terms of a software license agreement. You shall not disclose
 * such confidential information and shall use it only in accordance with the
 * terms of the license agreement. 
 */
package com.catl.ecad.utils;

import wt.access.AccessControlHelper;
import wt.access.AccessPermission;
import wt.fc.WTObject;
import wt.org.WTPrincipal;
import wt.session.SessionHelper;
import wt.util.WTException;
/**
 * 
 * 用来动态添加、更新、删除权限
 * 
 */
public class AccessControlUtil { 
    
    public static boolean hasModifyPermission(WTObject obj) throws WTException{
    	WTPrincipal principal = SessionHelper.getPrincipal();
    	if(AccessControlHelper.manager.hasAccess(principal, obj, AccessPermission.MODIFY_CONTENT)){
			System.out.println(principal.getName()+"\t"+"Has Modify Content Permissions");
			return true;
		}else{
			System.out.println(principal.getName()+"\t"+"Has no Modify Content Permissions");
		}
		return false;
    }

}
