package com.catl.line.util;

import java.rmi.RemoteException;
import java.util.Locale;

import wt.enterprise.RevisionControlled;
import wt.epm.util.EPMSoftTypeServerUtilities;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleException;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.State;
import wt.type.TypeDefinitionReference;
import wt.type.Typed;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTInvalidParameterException;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.WorkInProgressState;
import wt.vc.wip.Workable;

import com.ptc.core.meta.common.TypeIdentifier;

public class WCUtil {

	/**
	 * 更新 oldState 到 toState
	 * 
	 * @param rc
	 * @param oldState
	 * @param toState
	 * @throws WTInvalidParameterException
	 * @throws LifeCycleException
	 * @throws WTException
	 * @modified: ☆joy_gb(2015年10月22日 上午10:39:04): <br>
	 */
	public static void changeState(RevisionControlled rc, String oldState, String toState)
			throws WTInvalidParameterException, LifeCycleException, WTException {
		if (null != rc) {
			if (oldState.equals(rc.getState().toString())) {
				LifeCycleHelper.service.setLifeCycleState(rc, State.toState(toState), true);
			}
		}
	}

	/**
	 * 获取完整子类型名
	 * 
	 * @param persistable
	 * @return
	 * @throws WTException
	 * @modified: ☆joy_gb(2015年10月22日 上午11:46:32): <br>
	 */
	public static String geTypeDefinition(Typed persistable) throws WTException {
		TypeDefinitionReference ref = persistable.getTypeDefinitionReference();
		TypeIdentifier tid = EPMSoftTypeServerUtilities.getTypeIdentifier(ref);
		return tid.getTypename();
	}

	/**
	 * 获取完整子类型名
	 * 
	 * @param obj
	 * @return
	 * @throws WTException
	 * @modified: ☆joy_gb(2015年12月18日 上午1:53:40): <br>
	 */
	public static String geTypeDefinition2(Object obj) throws WTException {
		return TypedUtilityServiceHelper.service.getTypeIdentifier(obj).getTypename();
	}

	/**
	 * 获取 子类型逻辑名称
	 * 
	 * @param obj
	 * @return
	 * @throws RemoteException
	 * @throws WTException
	 * @modified: ☆joy_gb(2015年12月18日 上午1:58:23): <br>
	 */
	public static String geLocalizedTypeName(Object obj) throws RemoteException, WTException {
		return TypedUtilityServiceHelper.service.getLocalizedTypeName(obj, Locale.CHINESE);
	}


	public static Object getWTObject(String oid) throws WTRuntimeException, WTException {
		ReferenceFactory rf = new ReferenceFactory();
		Object object= rf.getReference(oid).getObject();
		 return object;
	}

	/**
	 * @param or
	 * @return
	 * @throws WTException
	 * @modified: ☆joy_gb(2015年11月5日 上午11:48:14): <br>
	 */
	public static String getOid(ObjectReference or) throws WTException {
		ReferenceFactory refefence = new ReferenceFactory();
		return refefence.getReferenceString(or);
	}

	/**
	 * @param wo
	 * @return
	 * @throws WTException
	 * @modified: ☆joy_gb(2015年11月5日 上午11:48:06): <br>
	 */
	public static String getOid(WTObject wo) throws WTException {
		ReferenceFactory refefence = new ReferenceFactory();
		return refefence.getReferenceString(wo);
	}

	/**
	 * @param persistable
	 * @return
	 * @throws WTException
	 * @modified: ☆joy_gb(2016年4月20日 下午6:47:54): <br>
	 */
	public static String getOid(Persistable persistable) throws WTException {
		ReferenceFactory refefence = new ReferenceFactory();
		return refefence.getReferenceString(persistable);
	}

	/**
	 * @param pers
	 * @return
	 * @throws WTException
	 * @modified: ☆joy_gb(2015年12月8日 下午4:04:20): <br>
	 */
	public static long getIda2a2(Persistable pers) throws WTException {
		return pers.getPersistInfo().getObjectIdentifier().getId();
	}

	/**
	 * @param pers
	 * @return
	 * @throws WTException
	 * @modified: ☆joy_gb(2015年12月8日 下午4:04:20): <br>
	 */
	public static String getIda2a2Str(Persistable pers) throws WTException {
		return String.valueOf(pers.getPersistInfo().getObjectIdentifier().getId());
	}

	/**
	 * @param workable
	 * @return 获取工作副本对象
	 * @create ltang
	 * @modified: ☆joy_gb(2015年8月17日 下午11:09:52): <br>
	 */
	public static Workable getWorkableByPersistable(Workable workable) {
		Workable wa = workable;
		try {
			if (WorkInProgressHelper.isCheckedOut(workable)) {
				if (!WorkInProgressHelper.isWorkingCopy(workable)) {
					wa = WorkInProgressHelper.service.workingCopyOf(workable);
				}
			} else {
				wa = WorkInProgressHelper.service
						.checkout(workable, WorkInProgressHelper.service.getCheckoutFolder(), "").getWorkingCopy();
			}
		} catch (WTPropertyVetoException | WTException e) {
			e.printStackTrace();
		}
		return wa;
	}
   
	

	/**
	 * @param workable
	 * @return 获取工作副本对象
	 * @create hdong
	 */
	public static Workable getWorkableByPersistableNoCheckOut(Workable workable) {
		Workable wa =workable;
		try {
			if (WorkInProgressHelper.isCheckedOut(workable)) {
				if (!WorkInProgressHelper.isWorkingCopy(workable)) {
					wa = WorkInProgressHelper.service.workingCopyOf(workable);
				}else{
					return wa;
				}
			} 
		} catch (WTException e) {
			e.printStackTrace();
		}
		return wa;
	}
	/**
	 * @param rc
	 * @return 获取版本 A.1
	 * @modified: ☆joy_gb(2015年9月14日 下午9:23:10): <br>
	 */
	public static String getVersionStr(RevisionControlled rc) {
		return rc.getVersionInfo().getIdentifier().getValue() + "." + rc.getIterationInfo().getIdentifier().getValue();
	}

	/**
	 * 获取文件夹路径
	 * 
	 * @param folderPath
	 * @param wtcontainer
	 * @return
	 * @throws Exception
	 */
	public static Folder getFolderByPath(String folderPath, WTContainer wtcontainer) {
		Folder subfolder = null;
		if (folderPath == null || folderPath.equals("")) {
			return null;
		}
		try {
			subfolder = FolderHelper.service.getFolder(folderPath, WTContainerRef.newWTContainerRef(wtcontainer));
		} catch (Exception e) {
			subfolder = null;
		}
		// 若文件夹不存在，则创建该文件夹
		if (subfolder == null) {
			try {
				subfolder = FolderHelper.service.saveFolderPath(folderPath,
						WTContainerRef.newWTContainerRef(wtcontainer));
			} catch (WTException e) {
				e.printStackTrace();
			}
		}
		return subfolder;
	}
	
	public static void checkin(Workable workable) throws WTPropertyVetoException {
		Workable wa =workable;
		try {
			if (WorkInProgressHelper.isCheckedOut(workable)) {
				if (!WorkInProgressHelper.isWorkingCopy(workable)) {
					wa = WorkInProgressHelper.service.workingCopyOf(workable);
				}
				WorkInProgressHelper.service.checkin(wa, null);
			} 
		} catch (WTException e) {
			e.printStackTrace();
		}

	}
	public static void convertcheckout(Workable workable) throws WTPropertyVetoException {
		Workable wa =workable;
		try {
			if (WorkInProgressHelper.isCheckedOut(workable)) {
				if (!WorkInProgressHelper.isWorkingCopy(workable)) {
					wa = WorkInProgressHelper.service.workingCopyOf(workable);
				}
				WorkInProgressHelper.service.convertCheckout(wa,WorkInProgressState.CHECKED_IN);
			} 
		} catch (WTException e) {
			e.printStackTrace();
		}

	}

}
