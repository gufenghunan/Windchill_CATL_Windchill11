package com.catl.cadence.service;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

import com.catl.cadence.service.CadenceService;

import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.services.Manager;
import wt.services.ManagerServiceFactory;
import wt.util.WTException;


public class CadenceServiceFwd implements RemoteAccess, Serializable, CadenceService{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2045266555884734448L;
	static final boolean SERVER;
	
	public CadenceServiceFwd(){}
	
	public void addTableColumn() throws WTException{
		if (SERVER){
			((CadenceService)getManager()).addTableColumn();
			return ;
		}else
			try
			{
				Class aclass[] = {};
				Object aobj[] = {};
				RemoteMethodServer.getDefault().invoke("addTableColumn", null, this, aclass, aobj);
			}
			catch (InvocationTargetException invocationtargetexception)
			{
				Throwable throwable = invocationtargetexception.getTargetException();
				if (throwable instanceof WTException)
				{
					throw (WTException)throwable;
				} else
				{
					Object aobj2[] = {
						"addTableColumn"
					};
					throw new WTException(throwable, "wt.fc.fcResource", "0", aobj2);
				}
			}
			catch (RemoteException remoteexception)
			{
				Object aobj1[] = {
					"addTableColumn"
				};
				throw new WTException(remoteexception, "wt.fc.fcResource", "0", aobj1);
			}
	}

	public void createAttrTable() throws WTException{
		if (SERVER){
			((CadenceService)getManager()).createAttrTable();
			return ;
		}else
			try
			{
				Class aclass[] = {};
				Object aobj[] = {};
				RemoteMethodServer.getDefault().invoke("createAttrTable", null, this, aclass, aobj);
			}
			catch (InvocationTargetException invocationtargetexception)
			{
				Throwable throwable = invocationtargetexception.getTargetException();
				if (throwable instanceof WTException)
				{
					throw (WTException)throwable;
				} else
				{
					Object aobj2[] = {
						"createAttrTable"
					};
					throw new WTException(throwable, "wt.fc.fcResource", "0", aobj2);
				}
			}
			catch (RemoteException remoteexception)
			{
				Object aobj1[] = {
					"createAttrTable"
				};
				throw new WTException(remoteexception, "wt.fc.fcResource", "0", aobj1);
			}
	}
	
	public void addPartAttribute(WTPart part) throws Exception{
		if (SERVER){
			((CadenceService)getManager()).addPartAttribute(part);
			return ;
		}else
			try
			{
				Class aclass[] = {WTPart.class};
				Object aobj[] = {part};
				RemoteMethodServer.getDefault().invoke("addPartAttribute", null, this, aclass, aobj);
			}
			catch (InvocationTargetException invocationtargetexception)
			{
				Throwable throwable = invocationtargetexception.getTargetException();
				if (throwable instanceof WTException)
				{
					throw (WTException)throwable;
				} else
				{
					Object aobj2[] = {
						"addPartAttribute"
					};
					throw new WTException(throwable, "wt.fc.fcResource", "0", aobj2);
				}
			}
			catch (RemoteException remoteexception)
			{
				Object aobj1[] = {
					"addPartAttribute"
				};
				throw new WTException(remoteexception, "wt.fc.fcResource", "0", aobj1);
			}
	}
	
	public void updatePartState(WTPart part) throws Exception{
		if (SERVER){
			((CadenceService)getManager()).updatePartState(part);
			return ;
		}else
			try
			{
				Class aclass[] = {WTPart.class};
				Object aobj[] = {part};
				RemoteMethodServer.getDefault().invoke("updatePartState", null, this, aclass, aobj);
			}
			catch (InvocationTargetException invocationtargetexception)
			{
				Throwable throwable = invocationtargetexception.getTargetException();
				if (throwable instanceof WTException)
				{
					throw (WTException)throwable;
				} else
				{
					Object aobj2[] = {
						"updatePartState"
					};
					throw new WTException(throwable, "wt.fc.fcResource", "0", aobj2);
				}
			}
			catch (RemoteException remoteexception)
			{
				Object aobj1[] = {
					"updatePartState"
				};
				throw new WTException(remoteexception, "wt.fc.fcResource", "0", aobj1);
			}
	}
	
	private static Manager getManager()
		throws WTException
	{
		Manager manager = ManagerServiceFactory.getDefault().getManager(CadenceService.class);
		if (manager == null)
		{
			Object aobj[] = {
				"com.comba.cadence.service.CadenceServiceFwd"
			};
			throw new WTException("wt.fc.fcResource", "40", aobj);
		} else
		{
			return manager;
		}
	}
	
	static 
	{
		SERVER = RemoteMethodServer.ServerFlag;
	}
}
