package com.catl.change.inventory;

import org.apache.log4j.Logger;

import wt.fc.ReferenceFactory;
import wt.fc.WTReference;
import wt.log4j.LogR;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.session.SessionServerHelper;
import wt.util.WTException;

public class ChangeInventoryUtil {

	public static Logger logger = LogR.getLogger(ChangeInventoryUtil.class.getName());
	

	/**
	 * get object reference by oid
	 * 
	 * @param oid
	 *            object'oid
	 * @return Object Reference
	 * @throws WTException 
	 */
	public static WTReference getObjectRefByOid(String oid) throws WTException {
		WTReference wtreference = null;
		try {
			if (!RemoteMethodServer.ServerFlag) {
				return (WTReference) RemoteMethodServer.getDefault().invoke(
						"getObjectRefByOid", ChangeInventoryUtil.class.getName(),
						null, new Class[] { String.class },
						new Object[] { oid });
			} else {
				boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
				try {
					ReferenceFactory referencefactory = new ReferenceFactory();
					wtreference = referencefactory.getReference(oid);
				} finally {
					SessionServerHelper.manager.setAccessEnforced(enforce);
				}
			}
		} catch (Exception e) {
			throw new WTException(e.getMessage());
		}
		return wtreference;
	}

	/**
	 * get object by oid
	 * 
	 * @param oid
	 * @return
	 * @throws WTException 
	 */
	public static Object getObjectByOid(String oid) throws WTException {
		return getObjectRefByOid(oid).getObject();
	}

	/**
	 * answer a string for the requst of searching part from jsp page
	 * @param oid
	 * @return
	 * @throws WTException
	 */
	public static String getDataForSearchPart(String oid) throws WTException{
		WTPart part = (WTPart)getObjectByOid(oid);
		String result = part.getNumber()+"|"+part.getName();
		return result;
	}
	
}
