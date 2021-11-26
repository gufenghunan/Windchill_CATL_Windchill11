package com.catl.ri.validator;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTGroup;
import wt.org.WTUser;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.util.WTException;

public class UserValidator {
    public static boolean isvalidAdminUser(String groupname) throws WTException{
    	WTUser user= (WTUser)SessionHelper.manager.getPrincipal();
		WTGroup agroup=findGroup(groupname);
		if(agroup==null){
			return false;
		}else{
			if(agroup.isMember(user)){
				return true;
			}else{
				return false;
			}
		}
    }
	public static WTGroup findGroup(String name) throws WTException{
		WTGroup wtc = null;
		QuerySpec qs;
		try {
			qs = new QuerySpec(WTGroup.class);
			SearchCondition sc = new SearchCondition(WTGroup.class, WTGroup.NAME, SearchCondition.EQUAL, name);
			qs.appendWhere(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			if (qr.size() > 0){
				wtc = (WTGroup) qr.nextElement();
			}
				
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return wtc;
    }
}
