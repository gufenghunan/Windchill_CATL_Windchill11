package com.catl.change.workflow;

import java.util.*;

import org.apache.log4j.Logger;

import com.catl.bom.workflow.BomWfUtil;
import com.catl.change.ChangeUtil;
import com.catl.change.util.ChangeConst;
import com.catl.common.constant.ChangeState;
import com.catl.common.constant.RoleName;
import com.catl.common.util.IBAUtil;
import com.catl.common.util.WorkflowUtil;
import com.catl.part.PartConstant;
import com.ptc.windchill.enterprise.team.server.TeamCCHelper;
import com.ptc.xworks.workflow.collector.PermissionTargetCollectionContext;
import com.ptc.xworks.workflow.collector.PermissionTargetObjectCollector;

import wt.access.AdHocControlled;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerHelper;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.lifecycle.*;
import wt.maturity.PromotionNotice;
import wt.org.*;
import wt.part.WTPart;
import wt.project.Role;
import wt.session.SessionHelper;
import wt.team.Team;
import wt.team.WTRoleHolder2;
import wt.util.WTException;
import wt.util.WTInvalidParameterException;
import wt.util.WTRuntimeException;
import wt.workflow.WfException;
import wt.workflow.definer.WfProcessTemplate;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfState;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;

public class DcnWorkUtil implements PermissionTargetObjectCollector {
	
	private static Logger log = Logger.getLogger(DcnWorkUtil.class.getName());
	
	//写数据到接口
	@Override
	public Collection<AdHocControlled> collectObjects(PermissionTargetCollectionContext collectionContext) {
		ArrayList<AdHocControlled> objects = new ArrayList<AdHocControlled>();
		try {
			WorkItem workitem = collectionContext.getWorkItem();
			PromotionNotice pn = (PromotionNotice) workitem.getPrimaryBusinessObject().getObject();
			Set<WTPart> list = BomWfUtil.getTargets(pn);
			objects.addAll(list);
		} catch (WTException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	//========= 分割线  ======
	/**
	 * 设置对象的生命周期状态  
	 * 
	 * @author Ken
	 * @param obj
	 * @throws LifeCycleException
	 * @throws WTException
	 */
	public static void setAllPersistablestate(WTObject pbo,String type){
		try{
			State state = State.toState(type);
			LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)pbo, state);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 设置DCA状态
	 * @param pbo
	 * @throws WTException
	 */
	public static void setDcaStateByDcn(WTObject pbo,String type) throws WTException{
		if(pbo instanceof WTChangeOrder2){
			WTChangeOrder2 dcn = (WTChangeOrder2)pbo;
			List<WTChangeActivity2> dcalist = ChangeUtil.getChangeActiveities(dcn);
			for(WTChangeActivity2 dca : dcalist){
				setAllPersistablestate(dca,type);
			}
		}else if(pbo instanceof WTChangeActivity2){
			WTChangeActivity2 dca = (WTChangeActivity2)pbo;
			setAllPersistablestate(dca,type);
		}
	}
	
	/**
	 * 获取流程对应的角色下的部门经理
	 * @return
	 * @throws WTException 
	 */
	public static String getRoleByProcess(WTChangeOrder2 dcn,ObjectReference self) throws WTException{
		//String msg = "";
		StringBuffer sbf = new StringBuffer();
		WfAssignedActivity acivity = (WfAssignedActivity) self.getObject();
		WfProcess process = acivity.getParentProcess();
		//String bz = acivity.getContext().getTaskComments();    //流程任务的备注
		Enumeration depart_manager = process.getPrincipals(Role.toRole(RoleName.DEPARTMENT_MANAGER));
		
		//update by szeng 20170912 设计变更流程优化
		//产品数据工程师
		Role productrole = Role.toRole(RoleName.PRODUCT_DATA_ENGINGEER);
		Enumeration product_data_engineer = process.getPrincipals(productrole);
		Enumeration quality_ref_engineer = process.getPrincipals(Role.toRole(RoleName.QUALITY_REPRESENTATIVE));
		
		if (!product_data_engineer.hasMoreElements()) {
			sbf.append("产品数据工程师角色不能为空，请在设置参与者中选择产品数据工程师!\n");
		}else{
			
			WTContainer container = dcn.getContainer();
			WTRoleHolder2 holder = TeamCCHelper.getTeamFromObject(container);
			Vector<Role> conRoles = holder.getRoles();
			
			//取流程某个角色的人
			List<WTPrincipal> ProcesslistPri = getUsers(process,productrole);
			
			//取上下文团队的人
			List<WTPrincipal> ContainerlistPri = getContainerPri(dcn,productrole,container);
			
			//检查团队中是否存在该角色或者成员.
			if(conRoles.contains(productrole) && ContainerlistPri.size()>0){
				if(!compareWpri(ProcesslistPri,ContainerlistPri)){
					sbf.append("选择的产品数据工程师不在产品团队的“产品数据工程师”成员中,请检查!\n");
				}
			}else{
				sbf.append("请设置产品团队中的产品数据工程师!\n");
			}
			
		}
		
		if (!depart_manager.hasMoreElements()) {
			sbf.append( "没有选择部门经理\n");
		}
		
		if(!quality_ref_engineer.hasMoreElements()){
			sbf.append("质量代表角色不能为空，请在设置参考者中选择质量代表或产品质量工程师!\n");
		}
		return sbf.toString();
	}
	
	
	/**
	 * 获取流程对应的角色下的部门经理
	 * @return
	 * @throws WTException 
	 */
	public static String getFMRoleByProcess(WTChangeOrder2 dcn,ObjectReference self) throws WTException{
		//String msg = "";
		StringBuffer sbf = new StringBuffer();
		WfAssignedActivity acivity = (WfAssignedActivity) self.getObject();
		WfProcess process = acivity.getParentProcess();
		//String bz = acivity.getContext().getTaskComments();    //流程任务的备注
		Enumeration depart_manager = process.getPrincipals(Role.toRole(RoleName.DEPARTMENT_MANAGER));
		
		
		
		if (!depart_manager.hasMoreElements()) {
			sbf.append( "部门经理角色不能为空，请在设置参考者中选择部门经理！\n");
		}
		
		return sbf.toString();
	}
	
	/**
	 * 获取流程对应的角色下的产品数据工程师”、“系统工程师
	 * @return
	 * @throws WTException 
	 */
	public static String checkRoleByProcess(WTChangeOrder2 dcn,ObjectReference self) throws WTException{
		StringBuffer sbf = new StringBuffer();
		
		WfAssignedActivity acivity = (WfAssignedActivity) self.getObject();
		WfProcess process = acivity.getParentProcess();
		
		//update by szeng 20170912 设计变更流程优化
		//产品数据工程师
		//Role productrole = Role.toRole(RoleName.PRODUCT_DATA_ENGINGEER);
		//Enumeration product_data_engineer = process.getPrincipals(productrole);
		Enumeration system_engineer = process.getPrincipals(Role.toRole(RoleName.SYSTEM_ENGINEER));
		Enumeration cost_engingeer = process.getPrincipals(Role.toRole(RoleName.COST_ENGINGEER));
		
		/*
		if (!product_data_engineer.hasMoreElements()) {
			sbf.append("产品数据工程师角色不能为空，请在设置参与者中选择产品数据工程师!\n");
		}else{
			
			WTContainer container = dcn.getContainer();
			WTRoleHolder2 holder = TeamCCHelper.getTeamFromObject(container);
			Vector<Role> conRoles = holder.getRoles();
			
			//取流程某个角色的人
			List<WTPrincipal> ProcesslistPri = getUsers(process,productrole);
			
			//取上下文团队的人
			List<WTPrincipal> ContainerlistPri = getContainerPri(dcn,productrole,container);
			
			//检查团队中是否存在该角色或者成员.
			if(conRoles.contains(productrole) && ContainerlistPri.size()>0){
				if(!compareWpri(ProcesslistPri,ContainerlistPri)){
					sbf.append("选择的产品数据工程师不在产品团队的“产品数据工程师”成员中,请检查!\n");
				}
			}else{
				sbf.append("请设置产品团队中的产品数据工程师!\n");
			}
			
		}*/
		
		if (!system_engineer.hasMoreElements()) {
			sbf.append("系统工程师角色不能为空，请在设置参与者中选择系统工程师!\n");
		}
		if (!cost_engingeer.hasMoreElements()) {
			sbf.append("成本工程师角色必须选择一个，请在设置参与者中选择成本工程师!\n");
		}
		
		return sbf.toString();
	}
	
	/**
	 * 通过角色和self获取对应的workItem
	 * @throws WTException 
	 * @throws  
	 */
	public static WorkItem getWItemBySelf(WTChangeOrder2 dcn,Role docRole) throws WTException{
	    QueryResult qr = wt.workflow.work.WorkflowHelper.service.getWorkItems(dcn, docRole);
	    if(qr.hasMoreElements()){
	    	WorkItem item = (WorkItem)qr.nextElement();
	    	return item;
	    }
	    return null;
	}
	
	/**
	 * 检查变更前对象是否在变更后中存在,并且必须是升版的.
	 * 校验DCA中所有的受影响对象，必须在产生的结果对象中都存在更新的大版本，否则提示“《对象类型》XXXXXX的在结果对象中没有对应的大版本”;
	 * @return
	 * @throws WTException 
	 */
	public static String checkMsgByDcaTata(WTChangeActivity2 dca) throws WTException{
		StringBuffer sbf = new StringBuffer();
		
		Map<String,List<Object>> beforemp = ChangeUtil.getBeforeDataByDca(dca);
		Map<String,List<Object>> aftermp = ChangeUtil.getAfterDataByDca(dca);
		  
		//变更前对象
		List<Object> beforepartlist = beforemp.get(ChangeConst.BEFORE_PART);
		List<Object> beforedoclist = beforemp.get(ChangeConst.BEFORE_DOC);
		List<Object> beforeepmlist = beforemp.get(ChangeConst.BEFORE_EPM);
		
		//变更后对象
		List<Object> afterpartlist = aftermp.get(ChangeConst.AFTRE_PART);
		Map<String,Object> afterpartmp = ChangeUtil.convertListToMap(afterpartlist);
		
		List<Object> afteredoclist = aftermp.get(ChangeConst.AFTRE_DOC);
		Map<String,Object> afterdocmp = ChangeUtil.convertListToMap(afteredoclist);
		
		List<Object> afterepmlist = aftermp.get(ChangeConst.AFTRE_EPM);
		Map<String,Object> afterepmmp = ChangeUtil.convertListToMap(afterepmlist);
		
		for(Object obj : beforepartlist){
			WTPart beforepart = (WTPart)obj;
			if(afterpartmp.containsKey(beforepart.getNumber())){
				//Long beforeBranchID = beforepart.getBranchIdentifier(); 
				WTPart afterpart = (WTPart)afterpartmp.get(beforepart.getNumber());
				//Long afterBranchID = afterpart.getBranchIdentifier();
				
				String bvid = beforepart.getVersionIdentifier().getValue();
				String avid = afterpart.getVersionIdentifier().getValue();
				if(Long.compare(Long.parseLong(avid),Long.parseLong(bvid))!=1){
					sbf.append("受影响对象部件【"+beforepart.getNumber()+"】在结果对象中没有对应的大版本,请检查!\n");
				}
			}else{
				sbf.append("受影响对象部件:【"+beforepart.getNumber()+"】不在产生的对象中,请检查!\n");
			}
		}
		
		for(Object obj : beforedoclist){
			WTDocument beforedoc = (WTDocument)obj;
			if(afterdocmp.containsKey(beforedoc.getNumber())){
				//Long beforeBranchID = beforedoc.getBranchIdentifier();
				WTDocument afterdoc = (WTDocument)afterdocmp.get(beforedoc.getNumber());
				//Long afterBranchID = afterdoc.getBranchIdentifier();
				String bvid = beforedoc.getVersionIdentifier().getValue();
				String avid = afterdoc.getVersionIdentifier().getValue();
				if(avid.length() < bvid.length() || (avid.length() == bvid.length() && avid.compareTo(bvid)!=1)){
					sbf.append("受影响对象文档【"+beforedoc.getNumber()+"】在结果对象中没有对应的大版本,请检查!\n");
				}
			}else{
				sbf.append("受影响对象文档:【"+beforedoc.getNumber()+"】不在产生的对象中,请检查!\n");
			}
		}
		
		for(Object obj : beforeepmlist){
			EPMDocument beforeepm = (EPMDocument)obj;
			if(afterepmmp.containsKey(beforeepm.getNumber())){
				//Long beforeBranchID = beforeepm.getBranchIdentifier(); 
				EPMDocument afterepm = (EPMDocument)afterepmmp.get(beforeepm.getNumber());
				//Long afterBranchID = afterepm.getBranchIdentifier();
				String bvid = beforeepm.getVersionIdentifier().getValue();
				String avid = afterepm.getVersionIdentifier().getValue();
				if(avid.length() < bvid.length() || (avid.length() == bvid.length() && avid.compareTo(bvid)!=1)){
					sbf.append("受影响对象图纸【"+beforeepm.getNumber()+"】在结果对象中没有对应的大版本,请检查!\n");
				}
			}else{
				sbf.append("受影响对象图纸 :【"+beforeepm.getNumber()+"】不在产生的对象中,请检查!\n");
			}
		}
		
		return sbf.toString();
	}
	
	/**
	 * 从工作流中获取指定角色的用户
	 */
	@SuppressWarnings("rawtypes")
	public static List<WTPrincipal> getUsers(WfProcess process, Role targetRole) {
		List<WTPrincipal> result = new ArrayList<WTPrincipal>();
		
		Team team = (Team) (process.getTeamId().getObject());
		try {
			Enumeration enumPrin = team.getPrincipalTarget(targetRole);
			while (enumPrin.hasMoreElements()) {
				WTPrincipalReference pr = (WTPrincipalReference) enumPrin.nextElement();
				WTPrincipal pri = pr.getPrincipal();
				result.add(pri);
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 从上下文团队中获取指定角色的用户
	 * @throws WTException 
	 */
	@SuppressWarnings("rawtypes")
	public static List<WTPrincipal> getContainerPri(WTChangeOrder2 dcn, Role productrole,WTContainer container) throws WTException {
		
		WTRoleHolder2 holder = TeamCCHelper.getTeamFromObject(container);
		Vector<Role> conRoles = holder.getRoles();
		
		List<WTPrincipal> result = new ArrayList<WTPrincipal>();
		
		if(conRoles.contains(productrole)){
			Enumeration enumeration = holder.getPrincipalTarget(productrole);  //取上下文对应角色的人.
			while(enumeration.hasMoreElements()){
				Object obj = enumeration.nextElement();
				WTPrincipal principal = null;
				if(obj instanceof WTPrincipal){
					principal = (WTPrincipal) obj;
					result.add(principal);
				}else if(obj instanceof WTPrincipalReference){
					WTPrincipalReference principalReference = (WTPrincipalReference) obj;
					if(principalReference.getObject() instanceof WTGroup){
						WTGroup group = (WTGroup) principalReference.getObject();
						Enumeration users = group.members();
						while (users.hasMoreElements()) {
							principal = (WTPrincipal) users.nextElement();
							result.add(principal);
						}
					}else{
						principal = principalReference.getPrincipal();
						result.add(principal);
					}
				}
				
			}
		}
		return result;
	}
	
	/**
	 * 检查流程中某个角色的的是否在PBO的上下文团队的某个角色中
	 */
	public static boolean compareWpri(List<WTPrincipal> plistpri,List<WTPrincipal> clistpri){
		for(WTPrincipal pri : plistpri){
			if(!clistpri.contains(pri)){
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * 获取流程对应的角色下是否有会签人员
	 * @return
	 * @throws WTException 
	 */
	public static boolean hasCPRoleByProcess(WTChangeOrder2 dcn,ObjectReference self) throws WTException{
		//String msg = "";

		WfProcess process = null;
		Object obj= self.getObject();
		if(obj instanceof WfAssignedActivity){
			WfAssignedActivity acivity = (WfAssignedActivity)obj;
			process = acivity.getParentProcess();
		}else if(obj instanceof WfProcess){
			process = (WfProcess) obj;
		}
	
		//String bz = acivity.getContext().getTaskComments();    //流程任务的备注
		Enumeration depart_manager = process.getPrincipals(Role.toRole(RoleName.COUNTERSIGN_PEOPLE));		
		
		if (!depart_manager.hasMoreElements()) {
			return false;
		}
		
		return true;
	}
	
	public static String isRoleSelectedUser(WTChangeOrder2 dcn,ObjectReference self,String roleNmae) throws WTInvalidParameterException, WTException{
		
		StringBuffer sbf = new StringBuffer();
		WfAssignedActivity acivity = (WfAssignedActivity) self.getObject();
		WfProcess process = acivity.getParentProcess();
		Role role = Role.toRole(roleNmae);
		Enumeration roleUsers = process.getPrincipals(role);			
			
		if (!roleUsers.hasMoreElements()) {
			sbf.append(role.getDisplay(SessionHelper.getLocale())).append("角色不能为空，请在设置参考者中选择").append(role.getDisplay(SessionHelper.getLocale())).append("!\n");
		}
			
		return sbf.toString();
	}
	
	public static String checkDCN(WTChangeOrder2 dcn, ObjectReference self, String rolename_pdm, String rolename_email, String pdm_role, String email_role) throws WTException {

		// find container team user by role
		Vector<WTPrincipal> pdm_teamPrincipals = new Vector<WTPrincipal>();
		Vector<WTPrincipal> email_teamPrincipals = new Vector<WTPrincipal>();

		//PDM审核
		//String pdm_role = "产品数据工程师&";
		Boolean iscollator = true;
		
		// 知会(知会者)
		//String email_role = "标准化工程师&";
		Boolean isemail = true;

		// 全部人员
		pdm_teamPrincipals = findContainerTeam(dcn, pdm_role);		
		email_teamPrincipals = findContainerTeam(dcn, email_role);
		
		StringBuffer message = new StringBuffer();
	
		try {
			WTPrincipal currentUser = SessionHelper.getPrincipal();
			WfAssignedActivity acivity = (WfAssignedActivity) self.getObject();
			WfProcess process = acivity.getParentProcess();
			Role rolepdm = Role.toRole(rolename_pdm);
			Role roleemail = Role.toRole(rolename_email);
			Enumeration pdm_processPrincipals = process.getPrincipals(rolepdm);
			Enumeration email_processPrincipals = process.getPrincipals(roleemail);

		
			checkChangeSelectUserisRight(rolepdm.getDisplay(Locale.CHINA),dcn, pdm_teamPrincipals, pdm_role, iscollator, message, currentUser, pdm_processPrincipals);
			checkChangeSelectUserisRight(roleemail.getDisplay(Locale.CHINA),dcn, email_teamPrincipals, email_role, isemail, message, currentUser, email_processPrincipals);
			
			
		} catch (WTException e) {
			message.append(e.getMessage());
			e.printStackTrace();

		}
		
		return message.toString();

	}
	
	
	private static void checkChangeSelectUserisRight(String checkRoleChina,WTChangeOrder2 dcn, Vector<WTPrincipal> check_teamPrincipals, String check_role, Boolean ischeck, StringBuffer message, WTPrincipal currentUser, Enumeration documemntcontral) throws WTException {
		ArrayList documemntcontralList = new ArrayList();
		if(!documemntcontral.hasMoreElements() && ischeck){
			message.append("该变更单"+checkRoleChina+"角色不能为空，请在设置参与者中选择一名"+checkRoleChina+"！ \n");
		}else{
			while (documemntcontral.hasMoreElements()) {
				Object obj = documemntcontral.nextElement();
				if (obj instanceof WTPrincipalReference) {
					WTPrincipalReference principalref = (WTPrincipalReference) obj;
					WTPrincipal principal = principalref.getPrincipal();
					documemntcontralList.add(principal);
					if (!check_teamPrincipals.contains(principal) && !check_role.equalsIgnoreCase("YES") && !check_role.equalsIgnoreCase("OPT")) {
						message.append("”"+checkRoleChina+"“角色中选择的:" + principalref.getFullName() + ",不正确，应该选择“" + check_role.replaceAll("\\||\\&", " ") + "”角色中的人员！ \n");
					}
				}
			}
		}
		message.append(mustSelectRole(check_role, dcn.getContainer(), documemntcontralList, checkRoleChina));
		
		if (WorkflowUtil.isSelectSelf(documemntcontralList, currentUser)) {
			message.append(checkRoleChina+"角色不能选择自己！ \n");
		}
	}


	/**
	 * 获取团队中所有角色(roleName)的成员
	 * 
	 * @param dcn
	 * @param roleName
	 * @return
	 */
	private static Vector<WTPrincipal> findContainerTeam(WTChangeOrder2 dcn, String roleName) {
		Vector<WTPrincipal> teamPrincipals = new Vector<WTPrincipal>();
		if (roleName.length() > 0 && !roleName.equalsIgnoreCase("YES") && !roleName.equalsIgnoreCase("OPT")) {
			String[] optRole = roleName.split("\\||\\&");
			try {
				teamPrincipals = findContainerTeamUserByRolenames(dcn.getContainer(), optRole);
			} catch (WTException e) {
				e.printStackTrace();
			}
		}
		return teamPrincipals;
	}

	private static Object mustSelectRole(String sign_role, WTContainer container, ArrayList selecedUsers, String sign_name) {
		StringBuffer message = new StringBuffer();
		if (sign_role.equalsIgnoreCase("OPT") || sign_role.equalsIgnoreCase("YES")) {
			return message;
		}
		String[] mustRoles = sign_role.split("\\&");
		for (String mustor : mustRoles) {
			boolean isContain = false;
			String mustCounter = mustor.indexOf("|") > 0 ? mustor.substring(mustor.lastIndexOf("|") + 1) : mustor;
			if (mustCounter.isEmpty())
				break;
			try {
				Vector<WTPrincipal> mustUser = findContainerTeamUserByRolename(container, mustCounter);
				for (int i = 0; i < selecedUsers.size(); i++) {
					WTPrincipal principal = (WTPrincipal) selecedUsers.get(i);
					if (mustUser.contains(principal)) {
						isContain = true;
					}
				}
				if (!isContain) {
					message.append("该变更" + sign_name + "“" + mustor + "”角色为必选 \n");
				}

			} catch (WTException e) {
				e.printStackTrace();
			}

		}
		if (selecedUsers.size() > 0) {
			boolean isContain = false;
			String optRole = sign_role.contains("&") ? sign_role.substring(sign_role.lastIndexOf("&") + 1) : sign_role;
			if (optRole.isEmpty()) {
				return message;
			}
			String[] optRoles = optRole.split("\\|");
			try {
				Vector<WTPrincipal> optUser = findContainerTeamUserByRolenames(container, optRoles);
				for (int i = 0; i < selecedUsers.size(); i++) {
					WTPrincipal principal = (WTPrincipal) selecedUsers.get(i);
					if (optUser.contains(principal)) {
						isContain = true;
						break;
					}
				}
				if (!isContain) {
					message.append("该变更" + sign_name + "“" + optRole.replaceAll("\\||\\&", " ") + "”至少选一个 \n");
				}
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return message;
	}
	
	private static Vector findContainerTeamUserByRolenames(WTContainer container, String[] rolenames) throws WTException {
		ContainerTeam containerTeam = null;
		Vector roleuser = new Vector();
		for (String rolename : rolenames) {
			for (Object obj : findContainerTeamUserByRolename(container, rolename)) {
				roleuser.add(obj);
			}
		}
		return roleuser;
	}
	
	private static Vector findContainerTeamUserByRolename(WTContainer container, String rolename) throws WTException {
		ContainerTeam containerTeam = null;
		Vector roleuser = new Vector();
		try {
			ContainerTeamManaged containerteammanaged = (ContainerTeamManaged) container;
			containerTeam = ContainerTeamHelper.service.getContainerTeam(containerteammanaged);
		} catch (WTRuntimeException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}

		Vector<Role> roles = containerTeam.getRoles();
		for (int i = 0; i < roles.size(); i++) {
			Role role = roles.get(i);
			//logger.debug("role fulldisplay name===" + role.getDisplay(Locale.CHINA));
			if (role.getDisplay(Locale.CHINA).equalsIgnoreCase(rolename)) {
				Enumeration enumPrin = containerTeam.getPrincipalTarget(role);

				while (enumPrin.hasMoreElements()) {
					WTPrincipalReference tempPrinRef = (WTPrincipalReference) enumPrin.nextElement();
					// 如果角色下的成员是组
					if (tempPrinRef.getObject() instanceof WTGroup) {
						WTGroup wtGroup = (WTGroup) tempPrinRef.getObject();
						wtGroup.getName();
						DirectoryContextProvider dcp = WTContainerHelper.service.getExchangeContainer().getContextProvider();
						WTOrganization org = OrganizationServicesHelper.manager.getOrganization("CATL", dcp);
						Enumeration<?> enu = OrganizationServicesHelper.manager.getGroups(wtGroup.getName(), WTContainerHelper.service.getOrgContainer(org).getContextProvider());
						if(!enu.hasMoreElements()){
							String[] services = wt.org.OrganizationServicesHelper.manager.getDirectoryServiceNames();
							wt.org.DirectoryContextProvider dc_provider = wt.org.OrganizationServicesHelper.manager.newDirectoryContextProvider(services, null);
							enu = wt.org.OrganizationServicesHelper.manager.getGroups(wtGroup.getName(), dc_provider);
						}
						while (enu.hasMoreElements()) {
							Object o = enu.nextElement();
							if (o instanceof WTGroup) {
								WTGroup group = (WTGroup) o;
								Enumeration users = group.members();
								while (users.hasMoreElements()) {
									WTPrincipal principal = (WTPrincipal) users.nextElement();
									roleuser.add(principal);
								}
							}
						}
					} else {
						WTPrincipal principal = tempPrinRef.getPrincipal();
						roleuser.add(principal);
					}

				}
			}
		}
		return roleuser;
	}
	
	/**
	 * 检查EBUS箱体间线束总成变更流程角色选人是否正确
	 * @param dcn
	 * @param self
	 * @return
	 * @throws WTException
	 */
	public static String checkEbusDcn(WTChangeOrder2 dcn, ObjectReference self) throws WTException{
		String message = checkDCN(dcn, self,RoleName.PRODUCT_DATA_ENGINGEER, RoleName.INFORM_THE_STAFF, "产品数据工程师&", "职能PM组浏览者&职能PSO-CMC组浏览者&职能PE组浏览者");
		return message;
	}
	
	/**
	 * 检查模具状态变更流程角色选人是否正确
	 * @param dcn
	 * @param self
	 * @return
	 * @throws WTException
	 */
	public static String checkmdDcn(WTChangeOrder2 dcn, ObjectReference self) throws WTException{
		String message = checkDCN(dcn, self,RoleName.COUNTERSIGN_PEOPLE, RoleName.INFORM_THE_STAFF, "产品数据工程师&经理&", "职能SRC组浏览者&职能QA-SQE组浏览者&职能PMC组浏览者&职能QA-IQC组浏览者&职能PM组浏览者");
		return message;
	}
}
