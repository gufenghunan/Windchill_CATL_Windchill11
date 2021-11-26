package com.catl.change.workflow;

import java.beans.PropertyVetoException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import wt.access.AccessControlHelper;
import wt.access.AccessPermission;
import wt.access.AdHocAccessKey;
import wt.access.AdHocControlled;
import wt.change2.ChangeException2;
import wt.change2.ChangeHelper2;
import wt.change2.Changeable2;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeOrder2Master;
import wt.change2.WTChangeOrder2MasterIdentity;
import wt.change2.WTChangeRequest2;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.epm.query.parser.QuerySupport;
import wt.fc.IdentityHelper;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTList;
import wt.httpgw.GatewayAuthenticator;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.method.MethodContext;
import wt.method.RemoteMethodServer;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.pom.WTConnection;
import wt.project.Role;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.team.Team;
import wt.team.TeamManaged;
import wt.util.WTException;
import wt.vc.VersionControlException;
import wt.vc.Versioned;
import wt.workflow.definer.WfProcessTemplate;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfProcess;
import wt.workflow.notebook.Notebook;
import wt.workflow.notebook.NotebookHelper;
import wt.workflow.work.WfAssignedActivity;

import com.catl.bom.workflow.BomWfUtil;
import com.catl.change.ChangeUtil;
import com.catl.common.constant.AttributeName;
import com.catl.common.constant.PartState;
import com.catl.common.constant.RoleName;
import com.catl.common.util.DocUtil;
import com.catl.common.util.GenericUtil;
import com.catl.common.util.PartUtil;
import com.catl.common.util.WorkflowUtil;
import com.catl.integration.EcInfo;
import com.catl.integration.ErpData;
import com.catl.promotion.util.PromotionConst;
import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.meta.common.UpdateOperationIdentifier;
import com.ptc.netmarkets.workflow.NmWorkflowHelper;

public class ECWorkflowUtil {

	private static Logger log = Logger.getLogger(ECWorkflowUtil.class.getName());
	private static Vector<WTPart> partvVector = new Vector<WTPart>();

	/**
	 * @param args
	 * @throws WTException
	 * @throws ChangeException2
	 */
	public static void main(String[] args) throws ChangeException2, WTException {
		// TODO Auto-generated method stub
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		GatewayAuthenticator auth = new GatewayAuthenticator();
		auth.setRemoteUser("wcadmin");
		rms.setAuthenticator(auth);
		String oid = args[0];
		String oidString = "OR:wt.change2.WTChangeRequest2:" + oid;
		Persistable object = PartUtil.getPersistableByOid(oidString);
		WTChangeRequest2 changeRequest2 = (WTChangeRequest2) object;
		// hasSelectCCBRole(changeRequest2);
		// QueryResult
		// targetResult=ChangeHelper2.service.getChangeables(changeRequest2);
		// System.out.println("targetResult=="+targetResult.size());
		// ArrayList<String> messageArrayList=checkECRTargets(changeRequest2);
		// ErpData data=getECInfo(changeRequest2);
		// EcInfo ecInfo=data.getEcn();
		// System.out.println("ec number="+ecInfo.getNumber());
		// System.out.println("ec name="+ecInfo.getName());
		// System.out.println("ec description="+ecInfo.getDescription());

	}

	public static void changeECNProcessName(WTChangeOrder2 ecn, String ecnnumber) {
		QueryResult qResult = null;
		try {
			qResult = NmWorkflowHelper.service.getAssociatedProcesses(ecn, null, null);
		} catch (WTException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while (qResult.hasMoreElements()) {
			WfProcess wfprocess = (WfProcess) qResult.nextElement();

			String oldname = wfprocess.getName();
			log.debug("old name ==" + oldname);
			log.debug("ecn number===" + ecnnumber);
			String newname = oldname.substring(0, oldname.indexOf("_")) + "_" + ecnnumber;
			log.debug("new name ==" + newname);
			wfprocess.setName(newname);

			try {
				wfprocess = (WfProcess) wt.fc.PersistenceHelper.manager.save(wfprocess);
				wfprocess = (WfProcess) PersistenceHelper.manager.refresh(wfprocess);
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static StringBuffer checkECATask(WTChangeActivity2 eca, ObjectReference self) {
		StringBuffer messageRole = checkECASumbit(self);
		StringBuffer messageAttachment = new StringBuffer();
		StringBuffer messageData = new StringBuffer();
		try {
			messageData = CheckECAAffectedData(eca);
			messageAttachment = checkEcaAttachment(eca);
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.debug("message role == " + messageRole);
		log.debug("message Data == " + messageData);
		StringBuffer errormessage = new StringBuffer();
		if (messageRole.length() > 0) {
			errormessage.append(messageRole);
		}
		if (messageData.length() > 0) {
			errormessage.append(messageData);
		}
		if (messageAttachment.length() > 0) {
			errormessage.append(messageAttachment);
		}
		log.debug("error message ==" + errormessage);

		return errormessage;
	}

	public static StringBuffer checkEcaAttachment(WTChangeActivity2 eca) throws WTException {
		StringBuffer message = new StringBuffer();

		ContentHolder holder = null;
		try {
			holder = ContentHelper.service.getContents(eca);
		} catch (PropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String ecaname = eca.getName();
		String desname = (String) GenericUtil.getObjectAttributeValue(eca, "taskDescription");
		
		//知会客户更改任务 需上传附件
		Boolean isNoticeName = ecaname.indexOf("知会客户") > -1;;
		Boolean isNoticeDesName = desname.indexOf("知会客户") > -1;
		
		Boolean istestname = ecaname.indexOf("测试") > -1;
		Boolean istestdesname = desname.indexOf("测试") > -1;
		log.debug("desname====" + desname);
		log.debug("eca name===" + ecaname + "is test ==" + istestname);
		Vector vApplicationData = ContentHelper.getApplicationData(holder);
		log.debug("eca attachments ===" + eca.getNumber() + "attachements ===" + vApplicationData.size());
		if (vApplicationData.size() == 0) {
			if (istestname || istestdesname || isNoticeName || isNoticeDesName) {
				message.append("您提交的更改任务没有添加附件，请上传附件！");
			}
		}

		log.debug("meeage==" + message);
		return message;
	}

	public static StringBuffer CheckECAAffectedData(wt.change2.WTChangeActivity2 ca2) throws WTException {
		StringBuffer message = new StringBuffer();
		QueryResult qrafter = new QueryResult();
		QueryResult qrbefore = new QueryResult();
		qrafter = ChangeHelper2.service.getChangeablesAfter(ca2);
		qrbefore = ChangeHelper2.service.getChangeablesBefore(ca2);
		log.debug("before size()======" + qrbefore.size());
		log.debug("after size ======" + qrafter.size());

		ArrayList<String> listafterStringlist = new ArrayList<String>();
		ArrayList<LifeCycleManaged> listafter = new ArrayList<LifeCycleManaged>();

		while (qrafter.hasMoreElements()) {
			LifeCycleManaged lifeCycleManagedObj = (LifeCycleManaged) qrafter.nextElement();
			log.debug(">>>lifeCycleManagedObj=" + lifeCycleManagedObj.getPersistInfo().getObjectIdentifier().getStringValue());
			listafter.add(lifeCycleManagedObj);
			listafterStringlist.add(BomWfUtil.getObjectnumber(lifeCycleManagedObj));
			String state = lifeCycleManagedObj.getState().toString();
			if (!state.equalsIgnoreCase(PartState.RELEASED)) {
				message.append("产生的对象" + BomWfUtil.getObjectnumber(lifeCycleManagedObj) + "不是已发布，不能提交！ \n");
			}
		}
		if (qrafter.size() < qrbefore.size()) {
			message.append("更改任务还未完成不能提交！ \n");
		} else {

			log.debug("listafter size=" + listafter.size());
			log.debug("listafterStringlist size=" + listafterStringlist.size());
			while (qrbefore.hasMoreElements()) {
				RevisionControlled beforeobj = (RevisionControlled) qrbefore.nextElement();
				log.debug("before object==" + beforeobj.getType());
				String beforenumberString = BomWfUtil.getObjectnumber(beforeobj);
				if (!listafterStringlist.contains(beforenumberString)) {
					log.debug("不在产生的对象中" + beforenumberString);
					message.append("对象不在产生的对象中：" + beforenumberString + "还没完成变更！");
				} else {
					for (int i = 0; i < listafter.size(); i++) {
						RevisionControlled afterobj = (RevisionControlled) listafter.get(i);
						String afternumberString = BomWfUtil.getObjectnumber(afterobj);
						log.debug("atf object type==" + afterobj.getType());
						if (beforenumberString.endsWith(afternumberString)) {
							if (beforeobj.getType().toString().endsWith(afterobj.getType().toString())) {
								// find the same object to compare version
								if (!isNewerThan((Versioned) afterobj, (Versioned) beforeobj)) {
									message.append("对象没有新版本在产生的对象中：" + beforenumberString + "还没完成变更！");
								}
							}
						}
					}
				}

			}

		}
		log.debug("message data ====" + message);
		return message;
	}

	/**
	 * Checks if versioned1 is newer than versioned2.
	 * 
	 * @param versioned1
	 *            the versioned1
	 * @param versioned2
	 *            the versioned2
	 * 
	 * @return true, if versioned1 is newer than versioned2.
	 */
	private static boolean isNewerThan(Versioned versioned1, Versioned versioned2) {
		try {
			wt.series.MultilevelSeries multilevelseries1 = versioned1.getVersionIdentifier().getSeries();
			wt.series.MultilevelSeries multilevelseries2 = versioned2.getVersionIdentifier().getSeries();
			if (multilevelseries1.greaterThan(multilevelseries2)) {
				return true;
			}
		} catch (VersionControlException exc) {
			exc.printStackTrace();
		}
		return false;
	}

	public static StringBuffer checkECRSumbitRole(ObjectReference self) {
		StringBuffer message = new StringBuffer();

		try {
			WTPrincipal currentUser = SessionHelper.getPrincipal();
			WfAssignedActivity acivity = (WfAssignedActivity) self.getObject();
			WfProcess process = acivity.getParentProcess();
			log.debug("start to check process role------>");
			log.debug("process name==" + process.getName());
			ArrayList depart_managerusers = new ArrayList();
			ArrayList producut_data_engingeerusers = new ArrayList();
			ArrayList system_engineers = new ArrayList();
			ArrayList ee_system_engineers = new ArrayList();
			ArrayList quality_representatives = new ArrayList();
			Enumeration depart_manager = process.getPrincipals(Role.toRole(RoleName.DEPARTMENT_MANAGER));
			Enumeration producut_data_engingeer = process.getPrincipals(Role.toRole(RoleName.PRODUCT_DATA_ENGINGEER));
			Enumeration system_engineer = process.getPrincipals(Role.toRole(RoleName.SYSTEM_ENGINEER));
			Enumeration ee_system_engineer = process.getPrincipals(Role.toRole(RoleName.EE_SYSTEM_ENGINEER));
			Enumeration quality_representative = process.getPrincipals(Role.toRole(RoleName.QUALITY_REPRESENTATIVE));

			while (depart_manager.hasMoreElements()) {
				Object obj = depart_manager.nextElement();
				if (obj instanceof WTPrincipalReference) {
					WTPrincipalReference principalref = (WTPrincipalReference) obj;
					WTPrincipal principal = principalref.getPrincipal();
					depart_managerusers.add(principal);
					log.debug(">>>>>>set access for user :" + principal.getName());
				}
			}
			while (producut_data_engingeer.hasMoreElements()) {
				Object obj = producut_data_engingeer.nextElement();
				if (obj instanceof WTPrincipalReference) {
					WTPrincipalReference principalref = (WTPrincipalReference) obj;
					WTPrincipal principal = principalref.getPrincipal();
					producut_data_engingeerusers.add(principal);
					log.debug(">>>>>>set access for user :" + principal.getName());
				}
			}
			while (system_engineer.hasMoreElements()) {
				Object obj = system_engineer.nextElement();
				if (obj instanceof WTPrincipalReference) {
					WTPrincipalReference principalref = (WTPrincipalReference) obj;
					WTPrincipal principal = principalref.getPrincipal();
					system_engineers.add(principal);
				}
			}
			while (ee_system_engineer.hasMoreElements()) {
				Object obj = ee_system_engineer.nextElement();
				if (obj instanceof WTPrincipalReference) {
					WTPrincipalReference principalref = (WTPrincipalReference) obj;
					WTPrincipal principal = principalref.getPrincipal();
					ee_system_engineers.add(principal);
				}
			}
			while (quality_representative.hasMoreElements()) {
				Object obj = quality_representative.nextElement();
				if (obj instanceof WTPrincipalReference) {
					WTPrincipalReference principalref = (WTPrincipalReference) obj;
					WTPrincipal principal = principalref.getPrincipal();
					quality_representatives.add(principal);
				}
			}

			log.debug("depart_managerusers.size() ---" + depart_managerusers.size());
			log.debug("producut_data_engingeerusers.size() ---" + producut_data_engingeerusers.size());
			if (depart_managerusers.size() == 0) {
				message.append("部门经理角色不能为空，请在设置参与者中选择一名部门审核人！ \n");
			} else if (depart_managerusers.size() > 1) {
				message.append("只能选择一名部门审核人！ \n");
			}
			if (WorkflowUtil.isSelectSelf(depart_managerusers, currentUser)) {
				message.append("部门经理角色不能选择自己！ \n");
			}
			if (producut_data_engingeerusers.size() == 0) {
				message.append("产品数据工程师角色不能为空，请在设置参与者中选择一名产品数据工程师！ \n");
			} else if (producut_data_engingeerusers.size() > 1) {
				message.append("只能选择一名产品数据工程师！ \n");
			}
			if (WorkflowUtil.isSelectSelf(producut_data_engingeerusers, currentUser)) {
				message.append("产品数据工程师角色不能选择自己！ \n");
			}
			if (system_engineers.size() == 0 && ee_system_engineers.size() == 0) {
				message.append("系统工程师，EE系统工程师不能同时为空，请在设置参与者中设置\n");
			}else if(system_engineers.size() > 0 && ee_system_engineers.size() == 0){
				changeisHasSystemEngineer(self,"1");
			}else if(system_engineers.size() == 0 && ee_system_engineers.size() > 0){
				changeisHasSystemEngineer(self,"2");
			}else if(system_engineers.size() > 0 && ee_system_engineers.size() > 0){
				changeisHasSystemEngineer(self,"3");
			}
			if(quality_representatives.size() == 0){
				message.append("质量代表角色不能为空，请在设置参与者中选择一名质量代表！ \n");
			}

		} catch (WTException e) {
			// TODO Auto-generated catch block
			message.append(e.getMessage());
			e.printStackTrace();
		}
		if (log.isDebugEnabled()) {
			log.debug(message);
		}

		return message;
	}

	public static StringBuffer checkECASumbit(ObjectReference self) {
		StringBuffer message = new StringBuffer();

		try {
			WTPrincipal currentUser = SessionHelper.getPrincipal();
			WfAssignedActivity acivity = (WfAssignedActivity) self.getObject();
			WfProcess process = acivity.getParentProcess();
			log.debug("start to check process role------>");
			log.debug("process name==" + process.getName());
			ArrayList users = new ArrayList();
			Enumeration depart_manager = process.getPrincipals(Role.toRole(RoleName.DEPARTMENT_MANAGER));
			while (depart_manager.hasMoreElements()) {
				Object obj = depart_manager.nextElement();
				if (obj instanceof WTPrincipalReference) {
					WTPrincipalReference principalref = (WTPrincipalReference) obj;
					WTPrincipal principal = principalref.getPrincipal();
					users.add(principal);
					log.debug(">>>>>>set access for user :" + principal.getName());
				}
			}

			log.debug("users.size() ---" + users.size());
			if (users.size() == 0) {
				message.append("部门经理角色不能为空，请在设置参与者中选择一名部门经理！ \n");
			} else if (users.size() > 1) {
				message.append("只能选择一名部门经理！ \n");
			}
			if (WorkflowUtil.isSelectSelf(users, currentUser)) {
				message.append("部门经理角色不能选择自己！ \n");
			}
		} catch (WTException e) {
			// TODO Auto-generated catch block
			message.append(e.getMessage());
			e.printStackTrace();
		}
		if (log.isDebugEnabled()) {
			log.debug(message);
		}

		return message;
	}

	public static Boolean isLastVersion(RevisionControlled revisionControlled) throws WTException {
		Boolean fagBoolean = false;
		String versionString = getlastVersion(revisionControlled);
		String versionString1 = "";
		if (revisionControlled instanceof WTPart) {
			WTPart part = (WTPart) revisionControlled;
			WTPart newPart = PartUtil.getLastestWTPartByNumber(part.getNumber());
			versionString1 = getlastVersion(newPart);
		}
		if (revisionControlled instanceof WTDocument) {
			WTDocument doc = (WTDocument) revisionControlled;
			WTDocument newDocument = DocUtil.getLatestWTDocument(doc.getNumber());
			versionString1 = getlastVersion(newDocument);
		}
		if (revisionControlled instanceof EPMDocument) {
			EPMDocument epm = (EPMDocument) revisionControlled;
			EPMDocument newepm = DocUtil.getLastestEPMDocumentByNumber(epm.getNumber());
			versionString1 = getlastVersion(newepm);
		}
		log.debug("targets version==" + versionString);
		log.debug("lastversion==" + versionString1);
		if (versionString.equals(versionString1)) {
			fagBoolean = true;
		} else {
			fagBoolean = false;
		}

		return fagBoolean;

	}

	public static String getlastVersion(RevisionControlled revisionControlled) {

		String versionString = "";
		versionString = revisionControlled.getVersionIdentifier().getValue() + "." + revisionControlled.getIterationIdentifier().getValue();
		return versionString;
	}

	public static Boolean checkCreateECN(WTChangeRequest2 ecr) {
		Boolean iscreate = false;
		ArrayList<WTChangeOrder2> ecnlist = getInworkEcnByEcr(ecr);
		if (ecnlist.size() > 0) {
			return true;
		} else {
			iscreate = false;
		}
		return iscreate;
	}

	public static void updateECNumber(WTChangeOrder2 eChangeOrder2, String number) throws WTException {

		WTPrincipal wtprincipal = SessionHelper.getPrincipal();
		WTPrincipalReference wtprincipalreference = (WTPrincipalReference) (new ReferenceFactory()).getReference(wtprincipal);
		SessionContext previous = SessionContext.newContext();
		SessionHelper.manager.setAdministrator();
		AccessControlHelper.manager.addPermission((AdHocControlled) eChangeOrder2, wtprincipalreference, AccessPermission.MODIFY_IDENTITY, AdHocAccessKey.WNC_ACCESS_CONTROL);
		try {
			System.out.println("number==" + number);
			WTChangeOrder2Master master = (WTChangeOrder2Master) eChangeOrder2.getMaster();
			master = (WTChangeOrder2Master) PersistenceHelper.manager.refresh(master);
			WTChangeOrder2MasterIdentity identity = (WTChangeOrder2MasterIdentity) master.getIdentificationObject();
			identity.setNumber(number);
			master = (WTChangeOrder2Master) IdentityHelper.service.changeIdentity(master, identity);
			PersistenceHelper.manager.save(master);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(eChangeOrder2.getNumber() + ":修改ECO Number失败！--change to:" + number);
		}finally{
			SessionContext.setContext(previous);
		}
	}

	public static String queryMaxEcnNumber(String numberPrefix) throws Exception {

		MethodContext context = MethodContext.getContext();
		WTConnection wtConn = (WTConnection) context.getConnection();
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String EcnNumber = null;
		try {

			String sql = "select max(wtchgordernumber) wtchgordernumber from wtchangeorder2master where wtchgordernumber like ? ";

			statement = wtConn.prepareStatement(sql);
			statement.setString(1, numberPrefix + "%");

			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				EcnNumber = resultSet.getString("wtchgordernumber");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (resultSet != null)
				resultSet.close();
			if (statement != null)
				statement.close();
			if (wtConn != null && wtConn.isActive())
				wtConn.release();
		}
		System.out.println("sql ecnNumber=" + EcnNumber);
		return EcnNumber;
	}

	public static String getEcnNumber(WTChangeOrder2 ecn) {
		WTChangeRequest2 ecr = getEcrByEcn(ecn);
		String ecrNumber = ecr.getNumber();
		String subEcrNumber = ecrNumber.substring(3, ecrNumber.length());
		return subEcrNumber;
	}

	public static void CreateEcoNumber(WTChangeOrder2 eco) {
		String maxnumberString = "";
		String ecnNumber = "";
		String ECNmiddleNumer = getEcnNumber(eco);
		System.out.println("ecmiddleNumber==" + ECNmiddleNumer);
		try {
			maxnumberString = queryMaxEcnNumber("ECN" + ECNmiddleNumer) == null ? "" : queryMaxEcnNumber("ECN" + ECNmiddleNumer);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("find max eco number failed!");
			e.printStackTrace();
		}
		System.out.println("maxnumberString==" + maxnumberString);
		if (maxnumberString.length() == 0) {
			ecnNumber = "ECN" + ECNmiddleNumer + "01";
		} else {
			String subnumber = maxnumberString.substring(maxnumberString.length() - 2, maxnumberString.length());
			int temp = Integer.parseInt(subnumber);
			temp++;
			maxnumberString = Integer.toString(temp);
			String nextNumberString = String.valueOf(maxnumberString);
			// if number length less than 2 add "0"
			while (nextNumberString.length() < 2) {
				nextNumberString = "0" + nextNumberString;
			}
			ecnNumber = "ECN" + ECNmiddleNumer + nextNumberString;
		}

		try {
			System.out.println("ecnNumber===" + ecnNumber);
			updateECNumber(eco, ecnNumber);
			changeECNProcessName(eco, ecnNumber);
		} catch (WTException e) {
			// TODO Auto-generated catch block
			System.out.println("ecnNumber===" + ecnNumber);
			log.debug(eco.getNumber() + "update number failed to--" + ecnNumber);
			e.printStackTrace();
		}

	}

	public static ErpData getECInfo(WTObject object) {
		ErpData erpData = new ErpData();
		EcInfo ecInfo = new EcInfo();
		ArrayList<String> relatepartList = new ArrayList<String>();
		if (object instanceof WTChangeActivity2) {
			WTChangeActivity2 eca = (WTChangeActivity2) object;
			WTChangeOrder2 eco = getEcnByEca(eca);
			WTChangeRequest2 ecRequest2 = getEcrByEcn(eco);
			System.out.println("ecRequest2 number==" + ecRequest2.getNumber());
			ecInfo.setName(ecRequest2.getName());
			ecInfo.setNumber(ecRequest2.getNumber());
			ecInfo.setDescription(ecRequest2.getDescription());

			try {
				QueryResult queryResult = ChangeHelper2.service.getChangeablesAfter(eca);
				while (queryResult.hasMoreElements()) {
					Object object2 = (Object) queryResult.nextElement();
					if (object2 instanceof WTPart) {
						WTPart part = (WTPart) object2;
						System.out.println("relate part==" + part.getNumber());
						relatepartList.add(part.getNumber());
					}
				}
			} catch (ChangeException2 e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			erpData.setEcn(ecInfo);
		}
		return erpData;
	}

	public static WTChangeOrder2 getEcnByEca(WTChangeActivity2 ca) {
		WTChangeOrder2 ecn = null;

		if (ca == null)
			return ecn;

		try {
			QueryResult orderQr = ChangeHelper2.service.getChangeOrder(ca);
			while (orderQr.hasMoreElements()) {
				return (WTChangeOrder2) orderQr.nextElement();
			}
		} catch (ChangeException2 e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}

		return ecn;
	}

	public static String checkECProcessRoles(ObjectReference self, String rolename) throws WTException {
		StringBuffer message = new StringBuffer();
		WTPrincipal currentUser = SessionHelper.getPrincipal();
		ArrayList users = new ArrayList();
		Locale locale = wt.session.SessionHelper.getLocale();
		try {
			WfAssignedActivity acivity = (WfAssignedActivity) self.getObject();
			WfProcess process = acivity.getParentProcess();
			log.debug("role name==" + rolename);
			Role role = Role.toRole(rolename);
			Enumeration user1 = process.getPrincipals(role);
			while (user1.hasMoreElements()) {
				Object obj = user1.nextElement();
				if (obj instanceof WTPrincipalReference) {
					WTPrincipalReference principalref = (WTPrincipalReference) obj;
					WTPrincipal principal = principalref.getPrincipal();
					users.add(principal);
					log.debug(">>>>>>set access for user :" + principal.getName());
				}
			}
			if (users.size() == 0) {
				message.append(role.getDisplay(locale) + "角色不能为空，请在设置参与者中选择一名" + role.getDisplay(locale) + "！ \n");
			} else if (WorkflowUtil.isSelectSelf(users, currentUser)) {
				message.append(role.getDisplay(locale) + "角色不能选择自己！ \n");
			}
			log.debug("users.size() ---" + users.size());

		} catch (WTException we) {
			we.printStackTrace();
			throw we;
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("检查角色出异常，请联系管理员！后台异常信息：" + e.getMessage());
		}
		return message.toString();
	}

	private boolean setProcessRoleHolder(WfProcess process, String rolename, WTPrincipal prin) {
		boolean flag = true;
		try {

			for (Enumeration enumer = getRoleEnumeration(process, rolename); enumer != null && enumer.hasMoreElements();) {
				WTPrincipal wtp = ((WTPrincipalReference) (WTPrincipalReference) enumer.nextElement()).getPrincipal();
				if (wtp.equals(prin))
					return false;
			}

			Role role = Role.toRole(rolename);
			Team team = (Team) process.getTeamId().getObject();
			team.addPrincipal(role, prin);
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	public static Enumeration getRoleEnumeration(TeamManaged teammanaged, String rolename) throws WTException {
		if (teammanaged == null || rolename == null || rolename.length() == 0)
			return null;
		Team team = (Team) teammanaged.getTeamId().getObject();// 根据流程,获取流程team
		if (team == null) {
			return null;
		} else {
			Role role = Role.toRole(rolename);// 初始化角色
			return team.getPrincipalTarget(role);// 获取流程team中相应角色的参与者
		}
	}

	public static String getErrorMessage(WTChangeRequest2 changeRequest2) throws WTException {
		String errorMessageString = "";
		ArrayList<String> messageArrayList = checkECRTargets(changeRequest2);
		log.debug("messageArrayList size=====" + messageArrayList.size());
		for (int i = 0; i < messageArrayList.size(); i++) {
			errorMessageString = messageArrayList.get(i).toLowerCase() + "\n";
		}
		
		StringBuffer message = ChangeUtil.checkMaturity(changeRequest2,null);
		return errorMessageString+message.toString();
	}

	public static ArrayList<String> checkECRTargets(WTChangeRequest2 changeRequest2) throws WTException {
		ArrayList<String> errormessage = new ArrayList<String>();
		QueryResult targetResult = ChangeHelper2.service.getChangeables(changeRequest2);
		log.debug("change request changeables===" + targetResult.size());
		while (targetResult.hasMoreElements()) {
			Changeable2 changeable = (Changeable2) targetResult.nextElement();
			RevisionControlled revisionControlled = (RevisionControlled) changeable;
			String number = BomWfUtil.getObjectnumber(changeable);
			String state = revisionControlled.getState().toString();
			if (!state.equalsIgnoreCase("RELEASED")) {
				errormessage.add(number + ":不是已发布的状态,不能添加到变更请求中！\n");
			}
			if (!isLastVersion(revisionControlled)) {
				errormessage.add(number + "不是最新版本！请提交最新的版本");
			}

			QueryResult ecaResult = ChangeHelper2.service.getAffectingChangeActivities(changeable);
			log.debug("ecrResult size==" + ecaResult.size());

			while (ecaResult.hasMoreElements()) {
				Object obj = (Object) ecaResult.nextElement();
				WTChangeActivity2 eca = (WTChangeActivity2) obj;
				log.debug("associated with eca=" + eca.getNumber());
				if (!eca.getState().toString().equalsIgnoreCase("CANCELLED") && !eca.getState().toString().equalsIgnoreCase("RESOLVED")) {
					errormessage.add(number + ":有正在进行的变更任务:" + eca.getNumber() + "\n");
				}

			}

		}
		return errormessage;
	}

	public static ArrayList<WTChangeOrder2> getInworkEcnByEcr(WTChangeRequest2 ecr) {
		WTChangeOrder2 ecn = null;
		ArrayList<WTChangeOrder2> ecnlist = new ArrayList<WTChangeOrder2>();
		if (ecr == null)
			return null;
		;

		try {
			QueryResult ecrqr = ChangeHelper2.service.getChangeOrders(ecr);
			while (ecrqr.hasMoreElements()) {
				ecn = (WTChangeOrder2) ecrqr.nextElement();
				if (!ecn.getState().toString().equalsIgnoreCase("CANCELLED")) {
					ecnlist.add(ecn);
				}

			}
		} catch (ChangeException2 e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}

		return ecnlist;
	}

	public static WTChangeRequest2 getEcrByEcn(WTChangeOrder2 ecn) {
		WTChangeRequest2 ecr = null;

		if (ecn == null)
			return ecr;

		try {
			QueryResult ecrqr = ChangeHelper2.service.getChangeRequest(ecn);
			while (ecrqr.hasMoreElements()) {
				return (WTChangeRequest2) ecrqr.nextElement();
			}
		} catch (ChangeException2 e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}

		return ecr;
	}

	public static WTChangeActivity2 getEcaByEcn(WTChangeOrder2 ecn) {
		WTChangeActivity2 eca = null;

		if (ecn == null)
			return eca;

		try {
			QueryResult ecnqr = ChangeHelper2.service.getChangeActivities(ecn);
			while (ecnqr.hasMoreElements()) {
				return (WTChangeActivity2) ecnqr.nextElement();
			}
		} catch (ChangeException2 e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}

		return eca;
	}

	public static Vector<WTPart> getparentPart(WTPart childpart) throws WTException {

		QueryResult parentResult = WTPartHelper.service.getUsedByWTParts((WTPartMaster) childpart.getMaster());
		while (parentResult.hasMoreElements()) {
			WTPart parentPart = (WTPart) parentResult.nextElement();
			partvVector.add(parentPart);
			if (WTPartHelper.service.getUsedByWTParts((WTPartMaster) parentPart.getMaster()).size() > 0) {
				getparentPart(parentPart);
			}

		}
		return partvVector;
	}

	public static void setECResolutionDate(WTObject pbo) throws Exception {

		Timestamp ts = new Timestamp(System.currentTimeMillis());

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(AttributeName.RESOLUTION_DATE, ts);
		GenericUtil.updateObject(pbo, params);

	}

	public static void setEcrChangeImpact(WTObject pbo, boolean littleChange, boolean normalChange, boolean bigChange) throws WTException {
		String value = "微小变更";
		if (littleChange) {
			value = "微小变更";
		}
		if (normalChange) {
			value = "一般变更";
		}
		if (bigChange) {
			value = "重大变更";
		}
		PersistableAdapter genericObj = new PersistableAdapter(pbo, null, null, new UpdateOperationIdentifier());
		genericObj.load(AttributeName.ECR_CHANGE_IMPACT);
		genericObj.set(AttributeName.ECR_CHANGE_IMPACT, value);
		Persistable updatedObject = genericObj.apply();
		PersistenceHelper.manager.save(pbo);

	}

	public static void setEcnAttributes(WTObject pbo, boolean littleChange, boolean normalChange, boolean bigChange, boolean needNotifyCustomer, boolean needCustomerApproval, boolean reviewResult) throws Exception {

		String value = "微小变更";
		if (littleChange) {
			value = "微小变更";
		}
		if (normalChange) {
			value = "一般变更";
		}
		if (bigChange) {
			value = "重大变更";
		}
		String reviewResultvalue = "通过";
		if (reviewResult) {
			reviewResultvalue = "带风险通过";
		}
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(AttributeName.ECN_CHANGE_IMPACT, value);
		params.put(AttributeName.ECN_NEED_CUSTOMER_APPROVE, needCustomerApproval);
		params.put(AttributeName.ECN_NEED_NOTIFY_CUSTOMER, needNotifyCustomer);
		params.put(AttributeName.ECN_REVIEW_RESULT, reviewResultvalue);
		GenericUtil.updateObject(pbo, params);

	}
	/**
	 * 04更改请求流程-QA初审  设置ECR属性
	 * @param pbo
	 * @param needCustomerApprove
	 * @param needCustomerNotice
	 * @throws Exception
	 */
	public static void setECRAttributes(WTObject pbo, boolean needCustomerApprove, boolean needCustomerNotice) throws Exception {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(AttributeName.ECN_NEED_CUSTOMER_APPROVE, needCustomerApprove);
		params.put(AttributeName.ECN_NEED_NOTIFY_CUSTOMER, needCustomerNotice);
		GenericUtil.updateObject(pbo, params);
	}

	public static void setEcnAttributes(WTObject pbo, String result) throws Exception {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(AttributeName.ECN_REVIEW_RESULT, result);
		GenericUtil.updateObject(pbo, params);
	}

	public static void setECAState(WTObject pbo, String state) throws ChangeException2, WTException {
		WTChangeOrder2 eco = (WTChangeOrder2) pbo;
		QueryResult qr = ChangeHelper2.service.getChangeActivities(eco);
		WTList ecaList = new WTArrayList(20);

		while (qr.hasMoreElements()) {
			WTChangeActivity2 eca = (WTChangeActivity2) qr.nextElement();
			ecaList.add(eca);
		}
		LifeCycleHelper.service.setLifeCycleState(ecaList, State.toState(state), false);

	}

	public static String hasSelectCCBRole(WTObject pbo, ObjectReference self) throws WTException {
		WTPrincipal currentUser = SessionHelper.getPrincipal();
		StringBuffer result = new StringBuffer();
		String requiredRoleNames = "CCB_PD,CCB_PM,CCB_PMC,CCB_QA,CCB_RSD,CCB_FNC";
		Object values = GenericUtil.getObjectAttributeValue(pbo, "changeType");
		String singleValue = null;
		boolean isProcessChange = false;
		if (values instanceof String) {
			singleValue = (String) values;
			if (singleValue.indexOf("工艺") > -1) {
				isProcessChange = true;

			}
		} else {
			Object[] v = (Object[]) values;
			if (v != null) {
				for (int i = 0; i < v.length; i++) {
					String s = (String) v[i];
					if (s.indexOf("工艺") > -1) {
						isProcessChange = true;
						break;
					}
				}
			}
		}

		if (isProcessChange) {
			requiredRoleNames = "CCB_MDE,CCB_MFG,CCB_PD,CCB_PM,CCB_QA,CCB_RSD,CCB_FNC";
		}

		WfAssignedActivity acivity = (WfAssignedActivity) self.getObject();
		WfProcess process = acivity.getParentProcess();
		StringTokenizer s = new StringTokenizer(requiredRoleNames, ",");
		while (s.hasMoreTokens()) {
			String roleName = s.nextToken();
			String message = null;
			Role role = Role.toRole(roleName);
			Enumeration user = process.getPrincipals(role);
			ArrayList list = new ArrayList();
			int i = 0;
			while (user.hasMoreElements()) {
				Object obj = user.nextElement();
				WTPrincipalReference principalref = (WTPrincipalReference) obj;
				WTPrincipal principal = principalref.getPrincipal();
				list.add(principal);
				i++;
			}
			if (i == 0) {
				message = roleName + " 没有设置参与者。";
			} else if (!roleName.equals("CCB_PD") && WorkflowUtil.isSelectSelf(list, currentUser)) {
				message = roleName + " 不能选择自己。";
			}
			if (message != null) {
				result.append(message + "\n");
			}
		}
		return result.toString();

	}

	public static void copyCCBRoles(WTObject pbo, String roles) throws WTException {
		WTChangeOrder2 ecn = (WTChangeOrder2) pbo;
		WfProcess ecnProcess = getAssociagtedProcess(ecn);
		WTChangeRequest2 ecr = ECWorkflowUtil.getEcrByEcn(ecn);
		log.debug("ECN " + ecn.getNumber() + " associaed with ECR " + ecr.getNumber());
		WfProcess ecrProcess = getAssociagtedProcess(ecr);
		Team ecnTeam = (Team) ecnProcess.getTeamId().getObject();
		Team ecrTeam = (Team) ecrProcess.getTeamId().getObject();
		StringTokenizer s = new StringTokenizer(roles, ",");
		while (s.hasMoreTokens()) {
			String roleName = s.nextToken();
			log.debug("roleName = " + roleName);
			Role role = Role.toRole(roleName);
			Enumeration enumeration = ecrTeam.getPrincipalTarget(role);
			while (enumeration.hasMoreElements()) {
				WTPrincipalReference ref = (WTPrincipalReference) enumeration.nextElement();
				WTPrincipal principal = ref.getPrincipal();
				log.debug(" participant = " + principal.getName());
				ecnTeam.addPrincipal(role, principal);
			}// while has more prinicipals for role
		}// while has more roles

	}

	/**
	 * 拷贝ECR中“ECR预报”节点所有角色中的参与者到ECN对应的角色中
	 * 
	 * @param pbo
	 * @throws WTException
	 */
	public static void copyRolesFromECRNotification(WTObject pbo) throws WTException {
		if (pbo instanceof WTChangeOrder2) {
			boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
			try {
				log.info("=====copyRolesFromECRNotification  start=====");
				Set<String> ccbRoles = new HashSet<String>();
				ccbRoles.addAll(Arrays.asList(RoleName.COPY_ROLES.split(",")));
				WTChangeOrder2 ecn = (WTChangeOrder2) pbo;
				WfProcess ecnProcess = getAssociagtedProcess(ecn);
				WTChangeRequest2 ecr = ECWorkflowUtil.getEcrByEcn(ecn);
				WfProcess ecrProcess = getAssociagtedProcess(ecr);
				Team ecnTeam = (Team) ecnProcess.getTeamId().getObject();
				Team ecrTeam = (Team) ecrProcess.getTeamId().getObject();
				WfProcessTemplate ecrTemplate = (WfProcessTemplate) (ecrProcess.getTemplate().getObject());
				Set<Role> roleSet = WorkflowUtil.getRolesInWfInternalMethodTemplate(ecrTemplate, "ECR预报");
				for (Role role : roleSet) {
					if (!ccbRoles.contains(role.toString())) {
						log.info("==copyRolesFromECRNotification==Role:" + role.toString());
						Enumeration<?> enumeration = ecrTeam.getPrincipalTarget(role);
						while (enumeration.hasMoreElements()) {
							WTPrincipalReference ref = (WTPrincipalReference) enumeration.nextElement();
							WTPrincipal principal = ref.getPrincipal();
							log.info("==participant:" + principal.getName());
							ecnTeam.addPrincipal(role, principal);
						}
					}
				}
				log.info("=====copyRolesFromECRNotification  end=====");
			} finally {
				SessionServerHelper.manager.setAccessEnforced(enforce);
			}
		}

	}

	// copy cbb roles to cbb group
	public static void collectCCBRoles(WTObject pbo, String fromroles, String torole) throws WTException {
		WTChangeOrder2 ecn = (WTChangeOrder2) pbo;
		WfProcess ecnProcess = getAssociagtedProcess(ecn);
		log.debug("ECN " + ecn.getNumber());
		Role cbbrole = Role.toRole(torole);
		Team ecnTeam = (Team) ecnProcess.getTeamId().getObject();
		StringTokenizer s = new StringTokenizer(fromroles, ",");
		while (s.hasMoreTokens()) {
			String roleName = s.nextToken();
			log.debug("roleName = " + roleName);
			Role role = Role.toRole(roleName);
			Enumeration enumeration = ecnTeam.getPrincipalTarget(role);
			while (enumeration.hasMoreElements()) {
				WTPrincipalReference ref = (WTPrincipalReference) enumeration.nextElement();
				WTPrincipal principal = ref.getPrincipal();
				log.debug(" participant = " + principal.getName());
				ecnTeam.addPrincipal(cbbrole, principal);
			}
		}

	}

	private static WfProcess getAssociagtedProcess(WTObject pbo) throws WTException {
		QueryResult processResult = NmWorkflowHelper.service.getAssociatedProcesses(pbo, null, null);
		WfProcess process = null;
		if (processResult.hasMoreElements()) {
			process = (WfProcess) processResult.nextElement();
		}
		return process;
	}

	public static boolean hasCustomerFile(WTChangeOrder2 eco) {

		boolean hasCustomerFile = false;
		try {
			Enumeration e = NotebookHelper.service.getNotebooks(eco);
			if ((e != null) && e.hasMoreElements()) {
				Persistable p = (Persistable) e.nextElement();
				if (p instanceof Notebook) {
					Notebook notebk = (Notebook) p;
					if (notebk.getBookmarks().hasMoreElements()) {
						hasCustomerFile = true;
					}
				}

			}
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hasCustomerFile;
	}

	/**
	 * 带风险通过时，检查变更任务中必须包含测试更改任务
	 * 
	 * @param pbo
	 * @param msg
	 * @throws WTException
	 */
	public static void checkTestECA(WTObject pbo, StringBuffer msg) throws WTException {
		if (pbo instanceof WTChangeOrder2) {
			log.info("======checkTestECA   start=======");
			WTChangeOrder2 ecn = (WTChangeOrder2) pbo;
			QueryResult qr = ChangeHelper2.service.getChangeActivities(ecn);
			while (qr.hasMoreElements()) {
				WTChangeActivity2 eca = (WTChangeActivity2) qr.nextElement();
				if (StringUtils.equals(eca.getName(), "测试更改任务")) {
					return;
				}
			}
			log.info("===error:No test ECA.");
			msg.append("带风险通过时，必须包含测试更改任务！ \n");
			log.info("======checkTestECA   end=======");
		}
	}

	/**
	 * 勾选“需要通知客户”，系统检查是否有新建“知会客户更改任务”
	 * 
	 * @param pbo
	 * @param msg
	 * @throws WTException
	 */
	public static void checkNoticeCustomerECA(WTObject pbo, StringBuffer msg) throws WTException {
		if (pbo instanceof WTChangeOrder2) {
			log.info("======checkNoticeCustomerECA   start=======");
			WTChangeOrder2 ecn = (WTChangeOrder2) pbo;
			QueryResult qr = ChangeHelper2.service.getChangeActivities(ecn);
			while (qr.hasMoreElements()) {
				WTChangeActivity2 eca = (WTChangeActivity2) qr.nextElement();
				if (StringUtils.equals("知会客户更改任务", eca.getName())) {
					return;
				}
			}
			log.info("===error:No notice customer ECA.");
			msg.append("勾选“需要通知客户”，必须新建“知会客户更改任务”！ \n");
			log.info("======checkNoticeCustomerECA   end=======");
		}
	}

	/**
	 * 检查生效日期
	 * 
	 * @param date
	 * @throws WTException
	 */
	public static void checkEffdate(Date date) throws WTException {
		if (date != null) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String currentDateStr = df.format(new Date());
			Long targetTime = date.getTime() + TimeZone.getTimeZone("GMT+8").getRawOffset();
			String checkDateStr = df.format(new Date(targetTime));
			if (checkDateStr.compareTo(currentDateStr) < 0) {
				throw new WTException("预计变更生效日期不能早于当前日期！");
			}
		}
	}

	public static String checkDealOpinion(WTObject pbo) throws WTException {
		WTChangeOrder2 ecn = (WTChangeOrder2) pbo;
		QueryResult qr = ChangeHelper2.service.getChangeActivities(ecn);

		while (qr.hasMoreElements()) {
			WTChangeActivity2 eca = (WTChangeActivity2) qr.nextElement();
			if (eca.getName().equals("物控与计划控制更改任务")) {
				return null;
			}
		}

		return "没有创建ECA“物控与计划控制更改任务”\n";
	}
	

	//===============  5.16 =============
	/**
	 * DCN：DCNXXXXXXXX（DCN+年份后2位+2位月份+4位流水）
	 * @author zyw2
	 * @param dcn
	 * @throws Exception 
	 */
	public static void CreateDCNNumber(WTChangeOrder2 dcn){
		String dcnNumber = "";
		
		String year="";
		String month="";
		
		String  maxnumberString= "";    //通过DCN+年份+月份找到的DCN单总数
		
		Calendar cal = Calendar.getInstance();
        year=String.valueOf(cal.get(Calendar.YEAR));
        year = year.substring(2,4);
        int m=cal.get(Calendar.MONTH);  
    	System.out.println("m==" + m);
        m++;
        if(m<10){
        	month = "0"+m;
        }else
        	month = ""+m;
        System.out.println("m1==】" + m+"【===month："+month);
		dcnNumber = "DCN" + year + month;
		
		try {
			String tp = queryMaxEcnNumber(dcnNumber);
			if(!"".equals(tp) && tp != null){
				maxnumberString = tp;
			}
		
			if (maxnumberString.length() == 0) {
				dcnNumber = dcnNumber + "0001";
			} else {
				String subnumber = maxnumberString.substring(maxnumberString.length() - 4, maxnumberString.length());
				int temp = Integer.parseInt(subnumber);
				temp++;
				maxnumberString = Integer.toString(temp);
				String nextNumberString = String.valueOf(maxnumberString);
				// if number length less than 2 add "000"
				while (nextNumberString.length() < 4) {
					if(nextNumberString.length() == 1){
						nextNumberString = "000" + nextNumberString;
					}else if(nextNumberString.length() == 2){
						nextNumberString = "00" + nextNumberString;
					}else if(nextNumberString.length() == 3){
						nextNumberString = "0" + nextNumberString;
					}
				}
				dcnNumber = dcnNumber + nextNumberString;
			}
			
			updateECNumber(dcn, dcnNumber);
			changeECNProcessName(dcn,dcnNumber);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.debug(dcn.getNumber() + "DCN_update number failed to--" + dcnNumber);
			e.printStackTrace();
		}
		
	}
	
	public static void changeisHasSystemEngineer(ObjectReference self,String value) throws WTException {
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			WfAssignedActivity acivity = (WfAssignedActivity) self.getObject();
			WfProcess process = acivity.getParentProcess();
			ProcessData processdata = process.getContext();
			if (processdata.getVariable("firstCheckFlag") != null) {
				processdata.setValue("firstCheckFlag", value);
			}
			process.setContext(processdata);
			wt.fc.PersistenceHelper.manager.save(process);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(flag);
		}
	}

}
