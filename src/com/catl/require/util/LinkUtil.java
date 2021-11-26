package com.catl.require.util;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

import com.catl.battery.util.CommonUtil;
import com.catl.line.util.WCUtil;

import wt.build.BuildHistory;
import wt.epm.build.EPMBuildRule;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartMaster;
import wt.part.WTPartReferenceLink;
import wt.part.WTPartUsageLink;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import wt.vc.struct.StructHelper;

public class LinkUtil implements RemoteAccess{


	public static boolean removePartDescriptionLink(WTPart wtpart) throws WTException {
		wtpart=CommonUtil.getLatestWTpartByNumber(wtpart.getNumber());
		QueryResult qr = PersistenceHelper.manager.navigate(wtpart,
				WTPartDescribeLink.DESCRIBED_BY_ROLE, WTPartDescribeLink.class,
				false);
		while (qr.hasMoreElements()) {
			WTPartDescribeLink link = (WTPartDescribeLink) qr.nextElement();
			PersistenceServerHelper.manager.remove(link);
		}
		return true;
	}
	
	public static boolean removePartReferenceLink(WTPart wtpart) throws WTException {
		wtpart=CommonUtil.getLatestWTpartByNumber(wtpart.getNumber());
		QueryResult qr = PersistenceHelper.manager.navigate(wtpart,
				WTPartReferenceLink.REFERENCES_ROLE, WTPartReferenceLink.class,
				false);
		while (qr.hasMoreElements()) {
			WTPartReferenceLink link = (WTPartReferenceLink) qr.nextElement();
			PersistenceServerHelper.manager.remove(link);
		}
		return true;
	}
	

	public static boolean removeEPMBuildSource(WTPart wtpart) throws WTException {
		wtpart=CommonUtil.getLatestWTpartByNumber(wtpart.getNumber());
		QueryResult qr = PersistenceHelper.manager.navigate(wtpart, EPMBuildRule.BUILD_SOURCE_ROLE, EPMBuildRule.class,false);
		while (qr.hasMoreElements()) {
			EPMBuildRule rule = (EPMBuildRule) qr.nextElement();
			PersistenceServerHelper.manager.remove(rule);
		}
		QueryResult qr1 = PersistenceHelper.manager.navigate(wtpart, BuildHistory.BUILT_BY_ROLE, BuildHistory.class,false);
		while (qr1.hasMoreElements()) {
			BuildHistory rule = (BuildHistory) qr1.nextElement();
			PersistenceServerHelper.manager.remove(rule);
		}
		return true;
	}
	
	public static void removePartUseageLink(WTPart wtpart,
			WTPartMaster master) throws WTException {
		QueryResult qr = PersistenceHelper.manager.find(WTPartUsageLink.class, wtpart, WTPartUsageLink.USED_BY_ROLE, master);
		while (qr.hasMoreElements()) {
			WTPartUsageLink link = (WTPartUsageLink) qr.nextElement();
			PersistenceServerHelper.manager.remove(link);
		}
	}
	public static boolean hasPartUseageLink(WTPart wtpart,WTPartMaster master) throws WTException {
		QueryResult qr = PersistenceHelper.manager.find(WTPartUsageLink.class, wtpart, WTPartUsageLink.USED_BY_ROLE, master);
		while (qr.hasMoreElements()) {
			return true;
		}
		return false;
	}
	public static void removePartUse(WTPart part) throws WTException{
		QueryResult pparts=StructHelper.service.navigateUsedBy(part.getMaster());
		while(pparts.hasMoreElements()){
			WTPart wtpart=(WTPart) pparts.nextElement();
			removePartUseageLink(wtpart,part.getMaster());
		}
	}
	
	public static void main(String[] args) throws WTRuntimeException, WTException, RemoteException, InvocationTargetException {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		rms.setUserName("wcadmin");
		rms.setPassword("wcadmin");
		rms.invoke("test", LinkUtil.class.getName(), null, null, null);
	}
	public static void test() throws WTRuntimeException, WTException{
		WTPart part=(WTPart) WCUtil.getWTObject("VR:wt.part.WTPart:180456271");
		removePartReferenceLink(part);
	}

}
