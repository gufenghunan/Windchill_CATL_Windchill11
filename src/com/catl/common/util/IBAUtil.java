package com.catl.common.util;

import java.util.Iterator;
import java.util.Map;

import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.meta.common.CreateOperationIdentifier;
import com.ptc.core.meta.common.OperationIdentifier;
import com.ptc.core.meta.common.UpdateOperationIdentifier;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.session.SessionHelper;
import wt.util.WTException;

public class IBAUtil {

	public static Persistable setIBAVaue(Persistable per, Map<String, Object> dataMap) throws WTException{
		OperationIdentifier operation = null;
		if(PersistenceHelper.isPersistent(per)){
			operation = new UpdateOperationIdentifier();
		}
		else {
			operation = new CreateOperationIdentifier();
		}
		
		PersistableAdapter adapter = new PersistableAdapter(per, null, SessionHelper.getLocale(), operation);
		
		Iterator<String> keyIt = dataMap.keySet().iterator();
		String key = null;
		adapter.load(dataMap.keySet());
		while (keyIt.hasNext()) {
			key = keyIt.next();
			adapter.set(key, dataMap.get(key));
		}
		return adapter.apply();
	}
	
	public static Persistable setIBAVaue(Persistable per, String key, Object value) throws WTException{
		OperationIdentifier operation = null;
		if(PersistenceHelper.isPersistent(per)){
			operation = new UpdateOperationIdentifier();
		}
		else {
			operation = new CreateOperationIdentifier();
		}
		
		PersistableAdapter adapter = new PersistableAdapter(per, null, SessionHelper.getLocale(), operation);
		adapter.load(key);
		adapter.set(key, value);
		return adapter.apply();
	}
	
	public static Object getIBAValue(Persistable targetObj, String ibaName) {
		Object value = null;
		try {
			PersistableAdapter obj = new PersistableAdapter(targetObj, null, SessionHelper.getLocale(), null);
			obj.load(ibaName);
			value = obj.get(ibaName);
		} catch (WTException e) {
			e.printStackTrace();
		}
        return value;
	}
}
