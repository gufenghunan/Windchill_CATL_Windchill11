package com.catl.ele.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.catl.common.constant.TypeName;
import com.catl.common.global.GlobalVariable;
import com.catl.common.util.GenericUtil;
import com.catl.common.util.PropertiesUtil;
import com.catl.common.util.WCLocationConstants;
import com.catl.ecad.utils.ECADConst;
import com.catl.ecad.utils.ECADutil;
import com.catl.ecad.utils.WorkflowHelper;
import com.catl.ecad.validator.StartWFFilter;
import com.catl.loadData.util.ExcelReader;
import com.catl.part.PartConstant;
import com.ptc.core.meta.server.TypeIdentifierUtility;

import wt.access.AccessControlServerHelper;
import wt.access.AccessControlled;
import wt.access.AccessPermission;
import wt.access.AccessPermissionSet;
import wt.access.AdHocAccessKey;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.doc.WTDocument;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceServerHelper;
import wt.fc.PersistentReference;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTCollection;
import wt.fc.collections.WTHashSet;
import wt.maturity.MaturityBaseline;
import wt.maturity.MaturityException;
import wt.maturity.MaturityHelper;
import wt.maturity.PromotionNotice;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.vc.baseline.BaselineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;

public class EleCommonUtil {
	private static Logger log = Logger.getLogger(EleCommonUtil.class.getName());
	
	public static final String eleConfigFile = WCLocationConstants.WT_HOME + File.separator + "codebase"+ File.separator+"config"+ File.separator+"custom"+ File.separator+"ElecworksClsConfig.xlsx";

	
	private static void checkNewFile() {
		// 获取文件更新时间,若修改时间改变则更新规则文件
		WfProcess process = null;
		String filePath = eleConfigFile;
		File file = new File(filePath);
		Long fileModifyTime = file.lastModified();
		Long sysModifyTime = GlobalVariable.fileLastModifyTime.get("ElecworksClsConfig.xlsx");
		if (sysModifyTime == null || !(sysModifyTime.intValue() == fileModifyTime.intValue())) {
			getEleCls();
		}
	}
	
	
	/**
	 * 获取电气元器件物料组
	 */
	public static void getEleCls() {
		GlobalVariable.eleCls.clear();
		String filePath = eleConfigFile;
		File file = new File(filePath);
		ExcelReader reader = new ExcelReader(file);
		try {
			reader.open();
		} catch (IOException e) {
			e.printStackTrace();
		}
		reader.setSheetNum(0);
		int count = reader.getRowCount();
		//Vector<String> elcClsSet = new Vector<String>();
		for (int i = 2; i <= count; i++) {
			String rows[] = reader.readExcelLine(i);

			if (!(rows == null || rows[0].isEmpty())) {

				String eleCls = rows[0].isEmpty() ? "" : rows[0];
				if(!GlobalVariable.eleCls.contains(eleCls)){
					GlobalVariable.eleCls.addElement(eleCls);
				}
				
			}// end if
		}// end for
		GlobalVariable.fileLastModifyTime.put("ElecworksClsConfig.xlsx", file.lastModified());

	}
	
	/**
	 * 判断部件是否为电气物料
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public static boolean isElePart(WTPart part) throws WTException{
		if(part != null){
			String cls = (String) GenericUtil.getObjectAttributeValue(part, PartConstant.IBA_CLS);
			checkNewFile();
			if(StringUtils.isNotBlank(cls)){
				if(GlobalVariable.eleCls.contains(cls)){
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * 
	 * isPBUSPart:方法作用：判断部件是否在PBUS物料组 <br/>
	 * date: 2018年7月26日 下午6:30:03 
	 * @author JohnnyR
	 * @param part
	 * @return
	 * @throws WTException
	 * @since JDK 1.7
	 */
	public static boolean isPBUSPart(WTPart part) throws WTException {
		if(part != null){
			String cls = (String) GenericUtil.getObjectAttributeValue(part, PartConstant.IBA_CLS);
			//获取properties文件的PBUS分类，判断部件
			String allowedstr = PropertiesUtil.getValueByKey("ele_Doc_Cata");
			for(String str :allowedstr.split(",")) {
				if(str.equals(cls))
					return true;
			}
		}
		return false;
	}
	
	/**
	 * 检查是否有电气元器件，如果有则启动电气建库流程
	 * @param pbo
	 * @throws MaturityException
	 * @throws WTException
	 */
	public static void startEleWorkflow(WTObject pbo) throws MaturityException, WTException{
		if (pbo instanceof PromotionNotice) {
			PromotionNotice pn = (PromotionNotice) pbo;
			if (pn != null) {
				QueryResult qr = MaturityHelper.service.getPromotionTargets(pn);
				while (qr.hasMoreElements()) {
					Object obj = qr.nextElement();
					if (obj instanceof WTPart) {
						WTPart part = (WTPart) obj;
						if (isElePart(part)) {
							boolean exitwf = StartWFFilter.terminateWorkProcess(part);
							if(!exitwf){
								WorkflowHelper.startWorkFlow("电气建库流程", part, null);
							}
						}
					}
				}
			}
		}
	}
	
	public static boolean isEleDoc(WTDocument doc){
		if(doc != null){
			String doctype = getStrSplit(doc);
			if(doctype.equals(TypeName.eleDoc)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * param str 要分割的字符串
	 * return ary[ary.length-1] 返回最后一个元数
	 * @author zyw2
	 */
	public static String getStrSplit(Persistable p) {
		
		String str = TypeIdentifierUtility.getTypeIdentifier(p).getTypename();
		
		if (str != null) {
			return str.substring(str.lastIndexOf("|") + 1, str.length());
		}
		return "";
	}
	
	/**
	 * 增加权限,用在活动节点
	 * 
	 * @param workItem
	 * @throws WTException
	 */
	public static void addPermission(ObjectReference self,WTObject pbo) throws WTException {
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			WfAssignedActivity wfAssignedActivity = (WfAssignedActivity) self.getObject();
			WfProcess wfprocess = wfAssignedActivity.getParentProcess();
			List<WTUser> users = WorkflowHelper.getActiveUsers(wfprocess, wfAssignedActivity);
			AccessPermissionSet permissionSet = new AccessPermissionSet();

			permissionSet.add(AccessPermission.MODIFY);
			permissionSet.add(AccessPermission.MODIFY_CONTENT);
			WTCollection collection = new WTHashSet();
			collection.add(pbo);

			for(int i =0 ; i < users.size(); i++){
				WTPrincipalReference principalRef = WTPrincipalReference.newWTPrincipalReference(users.get(i));
				AccessControlServerHelper.manager.addPermissions(collection, principalRef, permissionSet, AdHocAccessKey.WNC_WORK_ITEM);
				PersistenceServerHelper.manager.update(collection);
			}
			
		} finally {
			SessionServerHelper.manager.setAccessEnforced(flag);
		}

	}
	
	/**
	 * 去除权限,用在活动节点
	 * 
	 * @param workItem
	 * @throws WTException
	 */
	public static void clearPermission(ObjectReference self,WTObject pbo) throws WTException {
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			WfAssignedActivity wfAssignedActivity = (WfAssignedActivity) self.getObject();
			WfProcess wfprocess = wfAssignedActivity.getParentProcess();
			List<WTUser> users = WorkflowHelper.getActiveUsers(wfprocess, wfAssignedActivity);

			WTCollection collection = new WTHashSet();
			collection.add(pbo);

			for(int i =0 ; i < users.size(); i++){
				WTPrincipalReference principalRef = WTPrincipalReference.newWTPrincipalReference(users.get(i));
				AccessControlServerHelper.manager.removePermission(collection, principalRef, AccessPermission.MODIFY, AdHocAccessKey.WNC_WORK_ITEM);
				AccessControlServerHelper.manager.removePermission(collection, principalRef, AccessPermission.MODIFY_CONTENT, AdHocAccessKey.WNC_WORK_ITEM);
				PersistenceServerHelper.manager.update(collection);
			}
			
		} finally {
			SessionServerHelper.manager.setAccessEnforced(flag);
		}

	}
	

	/**
	 * 任务关联的对象
	 * 
	 * @param workItem
	 * @return
	 * @throws WTException
	 */
	private ArrayList<WTCollection> getRelatedObjects(WorkItem workItem) throws WTException {
		ArrayList<WTCollection> objectList = new ArrayList<WTCollection>();
		WTCollection collection = new WTArrayList();
		WTCollection ecadCollection = new WTArrayList();
		
		PersistentReference pRef = workItem.getPrimaryBusinessObject();
		if (pRef != null && pRef.getReferencedClass() != null) {
			Object object = pRef.getObject();
			QueryResult qr = null;
			if (object instanceof PromotionNotice) {
				PromotionNotice pn = (PromotionNotice) object;
				MaturityBaseline baseline = pn.getConfiguration();
				qr = BaselineHelper.service.getBaselineItems(baseline);
			} else if (object instanceof WTChangeRequest2) {
				WTChangeRequest2 ecr = (WTChangeRequest2) object;
				qr = ChangeHelper2.service.getChangeables(ecr);
			} else if (object instanceof WTChangeOrder2) {
				WTChangeOrder2 eco = (WTChangeOrder2) object;
				qr = ChangeHelper2.service.getChangeablesBefore(eco);
			} else if (object instanceof WTChangeActivity2) {
				WTChangeActivity2 eca = (WTChangeActivity2) object;
				qr = ChangeHelper2.service.getChangeablesBefore(eca);
			}
			while (qr != null && qr.hasMoreElements()) {
				AccessControlled accessControlledObject = (AccessControlled) qr.nextElement();
				//update by szeng 原理图、PCB图给只读权限
				if(accessControlledObject instanceof Persistable){
					String type = ECADutil.getStrSplit((Persistable) accessControlledObject);
					if (type.equalsIgnoreCase(ECADConst.SCHTYPE)||type.equalsIgnoreCase(ECADConst.PCBTYPE)) {
						ecadCollection.add(accessControlledObject);
					}else{
						collection.add(accessControlledObject);
					}
				}else{
					collection.add(accessControlledObject);
				}
				
			}
		}
		objectList.add(collection);
		objectList.add(ecadCollection);
		return objectList;
	}

}
