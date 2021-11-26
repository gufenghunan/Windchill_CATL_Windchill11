package com.catl.doc.workflow;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.ServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import wt.doc.WTDocument;
import wt.fc.ObjectReference;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.SubFolderReference;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerHelper;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.org.DirectoryContextProvider;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTOrganization;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.project.Role;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.team.Team;
import wt.team.TeamException;
import wt.team.TeamHelper;
import wt.team.TeamManaged;
import wt.util.WTException;
import wt.util.WTInvalidParameterException;
import wt.util.WTRuntimeException;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;

import com.catl.common.constant.RoleName;
import com.catl.common.constant.TypeName;
import com.catl.common.global.GlobalVariable;
import com.catl.common.util.GenericUtil;
import com.catl.common.util.WCLocationConstants;
import com.catl.common.util.WorkflowUtil;
import com.catl.doc.workflow.ExcelReader.DocClsReader;
import com.catl.loadData.util.ExcelReader;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.core.security.slcc.SLCCConstants.object_status;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmContext;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;
import com.sun.mail.handlers.message_rfc822;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;

public class DocWfUtil {

	public static Logger logger = Logger.getLogger(DocWfUtil.class.getName());
	
	public static final String DocumentClassification = WCLocationConstants.WT_HOME + File.separator + "codebase"+ File.separator+"config"+ File.separator+"custom"+ File.separator+"DocumentClassification.xls";

	public static ArrayList<String> getRoleuser(String[] rolename) {
		ArrayList<String> userlist = new ArrayList<String>();

		for (int i = 0; i < rolename.length; i++) {
		}
		return userlist;

	}

	public static void deleterole(TeamManaged object, String rolename) {
		Role role = Role.toRole(RoleName.COUNTERSIGN_PEOPLE);
		try {
			Team team2 = (Team) TeamHelper.service.getTeam(object);
			team2.deleteRole(role);

		} catch (TeamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static StringBuffer checkSubmit(List<ObjectBean> list) throws WTInvalidParameterException, WTException {
		StringBuffer message = new StringBuffer();
		checkNewFile();

		// 获取表单信息 细类及文档分类
		WTDocument doc = (WTDocument) list.get(0).getObject();
		String docSubName = GenericUtil.getObjectAttributeValue(doc, "subCategory") == null ? "" : GenericUtil.getObjectAttributeValue(doc, "subCategory").toString();
		String docTypeName = GenericUtil.getObjectAttributeValue(doc, "CATL_DocType") == null ? "" : GenericUtil.getObjectAttributeValue(doc, "CATL_DocType").toString();
		if (!(docSubName.isEmpty() && docTypeName.isEmpty())) {
			String docClsname = docSubName + (docTypeName.isEmpty() ? "" : "_" + docTypeName);
			// 从全局变量DocClassificationModelNew中取出规则角色
			DocClassificationModelNew clsmodelNew = GlobalVariable.docWorkflowConfigBean.get(docClsname);
			String submit_role = clsmodelNew.getSumbit();
			String submitrole = submit_role.substring(submit_role.indexOf("#") + 1, submit_role.length());
			StringTokenizer colltoken = new StringTokenizer(submitrole, "|");

			// 获得规则下团队角色包含的用户
			Vector<WTPrincipal> submit_teamPrincipals = new Vector<WTPrincipal>();
			submit_teamPrincipals = findContainerTeamUser(doc.getContainer(), colltoken);

			// 获得当前用户
			WTPrincipal user = SessionHelper.manager.getPrincipal();
			// 若规定了角色，判断当前用户是否属于该角色
			if (submitrole.length() > 0 && !submitrole.equalsIgnoreCase("YES") && !submit_teamPrincipals.contains(user)) {
				message.append("文档  “" + docClsname + "” 提交者必须是 ：“" + submitrole.replaceAll("\\||\\&", " ") + "” ，如有疑问请联系PDM小组 \n");
			}
		}

		return message;
	}

	public static void checkNewFile() {
		// 获取文件更新时间,若修改时间改变则更新规则文件
		WfProcess process = null;
		String filePath = DocumentClassification;
		File file = new File(filePath);
		Long fileModifyTime = file.lastModified();
		Long sysModifyTime = GlobalVariable.fileLastModifyTime.get("DocumentClassification.xls");
		if (!(sysModifyTime == fileModifyTime)) {
			DocWfUtil.getDocClassification();
		}
	}

	private static Vector findContainerTeamUser(WTContainer container, StringTokenizer roletoken) throws WTException {
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
		while (roletoken.hasMoreElements()) {
			String checkrolename = roletoken.nextToken();
			logger.debug("check role name==" + checkrolename);
			for (int i = 0; i < roles.size(); i++) {
				Role role = roles.get(i);
				//logger.debug("role fulldisplay name===" + role.getDisplay(Locale.CHINA));
				if (role.getDisplay(Locale.CHINA).equalsIgnoreCase(checkrolename)) {
					Enumeration enumPrin = containerTeam.getPrincipalTarget(role);
					while (enumPrin.hasMoreElements()) {
						WTPrincipalReference tempPrinRef = (WTPrincipalReference) enumPrin.nextElement();
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
						}else{
							WTPrincipal principal = tempPrinRef.getPrincipal();
							logger.debug("design role people name===" + principal.getName());
							roleuser.add(principal);
						}
					}
				}
			}
		}
		return roleuser;
	}

	public static StringBuffer checkDoc(WTDocument doc, ObjectReference self, Boolean iscollator, Boolean iscountersign, Boolean isemail, String collator_role, String countersign_role, String email_role) throws WTException {
		logger.debug("start to check doc state----------->");
		logger.debug("collator_role-----" + collator_role);
		logger.debug("countersign_role------" + countersign_role);
		// find container team user by role
		Vector<WTPrincipal> collter_teamPrincipals = new Vector<WTPrincipal>();
		Vector<WTPrincipal> countersign_teamPrincipals = new Vector<WTPrincipal>();
		Vector<WTPrincipal> email_teamPrincipals = new Vector<WTPrincipal>();
		if (collator_role.length() > 0 && !collator_role.equalsIgnoreCase("YES") && !collator_role.equalsIgnoreCase("OPT")) {
			StringTokenizer colltoken = new StringTokenizer(collator_role, "|");
			collter_teamPrincipals = findContainerTeamUser(doc.getContainer(), colltoken);
			logger.debug("collotor role vector========" + collter_teamPrincipals.size());
		}
		if (countersign_role.length() > 0 && !countersign_role.equalsIgnoreCase("YES") && !countersign_role.equalsIgnoreCase("OPT")) {
			StringTokenizer signtoken = new StringTokenizer(countersign_role, "|");
			countersign_teamPrincipals = findContainerTeamUser(doc.getContainer(), signtoken);
			logger.debug("counter sign vector===" + countersign_teamPrincipals.size());
		}
		if (email_role.length() > 0 && !email_role.equalsIgnoreCase("YES")) {
			StringTokenizer signtoken = new StringTokenizer(email_role, "|");
			email_teamPrincipals = findContainerTeamUser(doc.getContainer(), signtoken);
			logger.debug("email vector===" + email_teamPrincipals.size());
		}

		StringBuffer message = new StringBuffer();
		try {
			if (WorkInProgressHelper.isCheckedOut((Workable) doc)) {
				message.append(doc.getNumber() + ",对象被检出，请检入后提交！\n");
			}
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String createType = TypeIdentifierUtility.getTypeIdentifier(doc).getTypename();
		logger.debug("createType====" + createType);
		QueryResult qr = PartDocServiceCommand.getAssociatedParts(doc);
		boolean havePart = false;
		if (qr.hasMoreElements()) {
			logger.debug("have part.............");
			havePart = true;
		}
		boolean isNeedPart = createType.contains(TypeName.doc_type_pcbaDrawing) || createType.contains(TypeName.doc_type_gerberDoc);
		if (isNeedPart && !havePart) {
			message.append("请先将图纸与零部件建立关联。\n");
		}

		try {
			WTPrincipal currentUser = SessionHelper.getPrincipal();
			WfAssignedActivity acivity = (WfAssignedActivity) self.getObject();
			WfProcess process = acivity.getParentProcess();
			Enumeration documemntcontral = process.getPrincipals(Role.toRole(RoleName.DOCUMENT_CONTROLLER));
			Enumeration synthesize = process.getPrincipals(Role.toRole(RoleName.SYNTHESIZE_REVIEW_PEOPLE));
			Enumeration collator = process.getPrincipals(Role.toRole(RoleName.COLLATOR));
			Enumeration countersign = process.getPrincipals(Role.toRole(RoleName.COUNTERSIGN_PEOPLE));
			Enumeration email = process.getPrincipals(Role.toRole(RoleName.INFORM_THE_STAFF));

			logger.debug("start to check process role------>");
			logger.debug("process name==" + process.getName());
			ArrayList ddcusers = new ArrayList();
			ArrayList synusers = new ArrayList();
			ArrayList colusers = new ArrayList();
			while (documemntcontral.hasMoreElements()) {
				Object obj = documemntcontral.nextElement();
				if (obj instanceof WTPrincipalReference) {
					WTPrincipalReference principalref = (WTPrincipalReference) obj;
					WTPrincipal principal = principalref.getPrincipal();
					ddcusers.add(principal);
					logger.debug(">>>>>>set access for user :" + principal.getName());
				}
			}
			logger.debug("ddcusers.size() ---" + ddcusers.size());
			if (ddcusers.size() == 0) {
				message.append("文控专员角色不能为空，请在设置参与者中选择一名文控专员！ \n");
			} else if (ddcusers.size() > 1) {
				message.append("只能选择一名文控专员！ \n");
			}
			if (WorkflowUtil.isSelectSelf(ddcusers, currentUser)) {
				message.append("文控专员角色不能选择自己！ \n");
			}
			while (synthesize.hasMoreElements()) {
				Object obj = synthesize.nextElement();
				if (obj instanceof WTPrincipalReference) {
					WTPrincipalReference principalref = (WTPrincipalReference) obj;
					WTPrincipal principal = principalref.getPrincipal();
					synusers.add(principal);
					logger.debug(">>>>>>set access for user :" + principal.getName());
				}
			}
			logger.debug("synusers.size() ---" + synusers.size());
			if (synusers.size() == 0) {
				message.append("综合审核者角色不能为空，请在设置参与者中选择一名综合审核者！ \n");
			} else if (synusers.size() > 1) {
				message.append("只能选择一名综合审核者！ \n");
			}
			if (WorkflowUtil.isSelectSelf(synusers, currentUser)) {
				message.append("综合审核者角色不能选择自己！ \n");
			}
			while (collator.hasMoreElements()) {
				Object obj = collator.nextElement();
				if (obj instanceof WTPrincipalReference) {
					WTPrincipalReference principalref = (WTPrincipalReference) obj;
					WTPrincipal principal = principalref.getPrincipal();
					colusers.add(principal);
					if (!collter_teamPrincipals.contains(principal) && !collator_role.equalsIgnoreCase("YES") && !collator_role.equalsIgnoreCase("OPT") && iscollator) {
						message.append("”校对者“角色中选择的:" + principalref.getFullName() + ",不正确，应该选择“" + collator_role + "”角色中的人员！ \n");
					}
				}
			}
			if (!collator_role.equalsIgnoreCase("OPT") && colusers.size() == 0 && iscollator) {
				message.append("校对者角色不能为空，请在设置参与者中选择一名校对者！ \n");
			} else if (colusers.size() > 1) {
				message.append("只能选择一名校对者！ \n");
			}
			if (WorkflowUtil.isSelectSelf(colusers, currentUser)) {
				message.append("校对者角色不能选择自己！ \n");
			}

			if (!countersign_role.equalsIgnoreCase("OPT") && iscountersign && !countersign.hasMoreElements()) {
				message.append("该文档会签角色不能为空，请在设置参与者中选择会签者！ \n");
			} else {
				ArrayList couusers = new ArrayList();
				while (countersign.hasMoreElements()) {
					Object obj = countersign.nextElement();
					if (obj instanceof WTPrincipalReference) {
						WTPrincipalReference principalref = (WTPrincipalReference) obj;
						WTPrincipal principal = principalref.getPrincipal();
						couusers.add(principal);
						if (!countersign_teamPrincipals.contains(principal) && !countersign_role.equalsIgnoreCase("YES") && !countersign_role.equalsIgnoreCase("OPT") && iscountersign) {
							message.append("”会签者“角色中选择的:" + principalref.getFullName() + ",不正确，应该选择“" + countersign_role + "“角色中的人员！ \n");
						}
					}
				}
				if (WorkflowUtil.isSelectSelf(couusers, currentUser)) {
					message.append("该文档会签角色不能选择自己！ \n");
				}
			}

			if (isemail && !email.hasMoreElements()) {
				message.append("该文档知会人员角色不能为空，请在设置参与者中选择知会人员！ \n");
			} else {
				ArrayList emausers = new ArrayList();
				while (email.hasMoreElements()) {
					Object obj = email.nextElement();
					if (obj instanceof WTPrincipalReference) {
						WTPrincipalReference principalref = (WTPrincipalReference) obj;
						WTPrincipal principal = principalref.getPrincipal();
						emausers.add(principal);
						if (!email_teamPrincipals.contains(principal) && !email_role.equalsIgnoreCase("YES") && email_role.length() > 0) {
							// “知会者”中的“程金龙”不符合业务要求，应该属于“工艺工程师”角色中的人员
							message.append("”知会者“角色中选择的:" + principalref.getFullName() + ",不正确，应该选择“" + email_role + "”角色中的人员！ \n");
						}
					}
				}
				if (WorkflowUtil.isSelectSelf(emausers, currentUser)) {
					message.append("该文档知会人员角色不能选择自己！ \n");
				}
			}
		} catch (WTException e) {
			message.append(e.getMessage());
			e.printStackTrace();

		}
		if (logger.isDebugEnabled()) {
			logger.debug("message=== " + message);
		}
		return message;
	}

	public static StringBuffer checkDocNew(WTDocument doc, ObjectReference self) throws WTException {

		// find container team user by role
		Vector<WTPrincipal> collator_teamPrincipals = new Vector<WTPrincipal>();
		Vector<WTPrincipal> countersign_teamPrincipals = new Vector<WTPrincipal>();
		Vector<WTPrincipal> email_teamPrincipals = new Vector<WTPrincipal>();
		Vector<WTPrincipal> check_teamPrincipals = new Vector<WTPrincipal>();
		Vector<WTPrincipal> review_teamPrincipals = new Vector<WTPrincipal>();

		// 取出规则
		checkNewFile();
		String docSubName = GenericUtil.getObjectAttributeValue(doc, "subCategory") == null ? "" : GenericUtil.getObjectAttributeValue(doc, "subCategory").toString();
		String docTypeName = GenericUtil.getObjectAttributeValue(doc, "CATL_DocType") == null ? "" : GenericUtil.getObjectAttributeValue(doc, "CATL_DocType").toString();
		String docClsname = docSubName + (docTypeName == "" ? "" : "_" + docTypeName);
		// 从全局变量DocClassificationModelNew中取出规则角色
		DocClassificationModelNew clsmodelNew = GlobalVariable.docWorkflowConfigBean.get(docClsname);
		// 会签（会签者）
		String countersignRole = clsmodelNew.getCountersign();
		String countersign_role = countersignRole.substring(countersignRole.indexOf("#") + 1, countersignRole.length());
		Boolean iscountersign = countersignRole.contains("YES") ? true : false;
		// 校对（校对者）
		String collatorRole = clsmodelNew.getCollator();
		String collator_role = collatorRole.substring(collatorRole.indexOf("#") + 1, collatorRole.length());
		Boolean iscollator = collatorRole.contains("YES") ? true : false;
		// 规范性审核(文控专员)
		String checkRole = clsmodelNew.getCheck();
		String check_role = checkRole.substring(checkRole.indexOf("#") + 1, checkRole.length());
		Boolean ischeck = checkRole.contains("YES") ? true : false;
		// 综合审核(综合审核者)
		String reviewRole = clsmodelNew.getReview();
		String review_role = reviewRole.substring(reviewRole.indexOf("#") + 1, reviewRole.length());
		Boolean isreview = reviewRole.contains("YES") ? true : false;
		// 知会(知会者)
		String emailRole = clsmodelNew.getNotice();
		String email_role = emailRole.substring(emailRole.indexOf("#") + 1, emailRole.length());
		Boolean isemail = emailRole.contains("YES") ? true : false;

		// 全部人员
		collator_teamPrincipals = findContainerTeam(doc, collator_role);
		countersign_teamPrincipals = findContainerTeam(doc, countersign_role);
		email_teamPrincipals = findContainerTeam(doc, email_role);
		check_teamPrincipals = findContainerTeam(doc, check_role);
		review_teamPrincipals = findContainerTeam(doc, review_role);

		StringBuffer message = new StringBuffer();
		try {
			if (WorkInProgressHelper.isCheckedOut((Workable) doc)) {
				message.append(doc.getNumber() + ",对象被检出，请检入后提交！\n");
			}
		} catch (WTException e) {
			e.printStackTrace();
		}

		String createType = TypeIdentifierUtility.getTypeIdentifier(doc).getTypename();
		logger.debug("createType====" + createType);
		QueryResult qr = PartDocServiceCommand.getAssociatedParts(doc);
		boolean havePart = false;
		if (qr.hasMoreElements()) {
			logger.debug("have part.............");
			havePart = true;
		}
		boolean isNeedPart = createType.contains(TypeName.doc_type_pcbaDrawing) || createType.contains(TypeName.doc_type_gerberDoc);
		if (isNeedPart && !havePart) {
			message.append("请先将图纸与零部件建立关联。\n");
		}

		try {
			WTPrincipal currentUser = SessionHelper.getPrincipal();
			WfAssignedActivity acivity = (WfAssignedActivity) self.getObject();
			WfProcess process = acivity.getParentProcess();
			Enumeration collator_processPrincipals = process.getPrincipals(Role.toRole(RoleName.COLLATOR));
			Enumeration check_processPrincipals = process.getPrincipals(Role.toRole(RoleName.DOCUMENT_CONTROLLER));
			Enumeration review_processPrincipals = process.getPrincipals(Role.toRole(RoleName.SYNTHESIZE_REVIEW_PEOPLE));
			Enumeration countersign_processPrincipals = process.getPrincipals(Role.toRole(RoleName.COUNTERSIGN_PEOPLE));
			Enumeration email_processPrincipals = process.getPrincipals(Role.toRole(RoleName.INFORM_THE_STAFF));

			logger.debug("start to check process role------>");
			logger.debug("process name==" + process.getName());
			
			checkDocSelectUserisRight("校对者",doc, collator_teamPrincipals, collator_role, iscollator, message, currentUser, collator_processPrincipals);
			checkDocSelectUserisRight("文控专员",doc, check_teamPrincipals, check_role, ischeck, message, currentUser, check_processPrincipals);
			checkDocSelectUserisRight("综合审核者",doc, review_teamPrincipals, review_role, isreview, message, currentUser, review_processPrincipals);
			checkDocSelectUserisRight("会签者",doc, countersign_teamPrincipals, countersign_role, iscountersign, message, currentUser, countersign_processPrincipals);
			checkDocSelectUserisRight("知会人员",doc, email_teamPrincipals, email_role, isemail, message, currentUser, email_processPrincipals);
			
			
		} catch (WTException e) {
			message.append(e.getMessage());
			e.printStackTrace();

		}
		if (logger.isDebugEnabled()) {
			logger.debug("message=== " + message);
		}
		return message;

	}
	
	private static void checkDocSelectUserisRight(String checkRoleChina,WTDocument doc, Vector<WTPrincipal> check_teamPrincipals, String check_role, Boolean ischeck, StringBuffer message, WTPrincipal currentUser, Enumeration documemntcontral) throws WTException {
		ArrayList documemntcontralList = new ArrayList();
		if(!documemntcontral.hasMoreElements() && ischeck){
			message.append("该文档"+checkRoleChina+"角色不能为空，请在设置参与者中选择一名"+checkRoleChina+"！ \n");
		}else{
			while (documemntcontral.hasMoreElements()) {
				Object obj = documemntcontral.nextElement();
				if (obj instanceof WTPrincipalReference) {
					WTPrincipalReference principalref = (WTPrincipalReference) obj;
					WTPrincipal principal = principalref.getPrincipal();
					documemntcontralList.add(principal);
					logger.debug(">>>>>>set access for user :" + principal.getName());
					if (!check_teamPrincipals.contains(principal) && !check_role.equalsIgnoreCase("YES") && !check_role.equalsIgnoreCase("OPT")) {
						message.append("”"+checkRoleChina+"“角色中选择的:" + principalref.getFullName() + ",不正确，应该选择“" + check_role.replaceAll("\\||\\&", " ") + "”角色中的人员！ \n");
					}
				}
			}
		}
		logger.debug("documemntcontralList.size() ---" + documemntcontralList.size());
		message.append(mustSelectRole(check_role, doc.getContainer(), documemntcontralList, checkRoleChina));
		
		if (WorkflowUtil.isSelectSelf(documemntcontralList, currentUser)) {
			message.append(checkRoleChina+"角色不能选择自己！ \n");
		}
	}


	/**
	 * 获取团队中所有角色(roleName)的成员
	 * 
	 * @param doc
	 * @param roleName
	 * @return
	 */
	private static Vector<WTPrincipal> findContainerTeam(WTDocument doc, String roleName) {
		Vector<WTPrincipal> teamPrincipals = new Vector<WTPrincipal>();
		if (roleName.length() > 0 && !roleName.equalsIgnoreCase("YES") && !roleName.equalsIgnoreCase("OPT")) {
			String[] optRole = roleName.split("\\||\\&");
			try {
				teamPrincipals = findContainerTeamUserByRolenames(doc.getContainer(), optRole);
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
					message.append("该文档" + sign_name + "“" + mustor + "”角色为必选 \n");
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
					message.append("该文档" + sign_name + "“" + optRole.replaceAll("\\||\\&", " ") + "”至少选一个 \n");
				}
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return message;
	}

	public static Vector getExcelData(String filename) {
		DocClsReader reader = new DocClsReader(filename);
		reader.initHssfSheet();

		return reader.getDataVector();
	}

	/*
	 * 
	 */
	public static void getDocClassification() {
		String filePath = DocumentClassification;
		List allRows = new ArrayList();
		File file = new File(filePath);
		ExcelReader reader = new ExcelReader(file);
		try {
			reader.open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		reader.setSheetNum(0);
		int count = reader.getRowCount();
		Map<String, DocClassificationModelNew> doc = new HashMap<String, DocClassificationModelNew>();
		Map<String, DocClassificationModelNew> needPNdoc = new HashMap<String, DocClassificationModelNew>();
		for (int i = 1; i <= count; i++) {
			String rows[] = reader.readExcelLine(i);

			if (!(rows == null || rows[1].isEmpty() || rows[2].isEmpty())) {

				DocClassificationModelNew dcm = new DocClassificationModelNew();
				dcm.setDocBigType(rows[1].isEmpty() ? "" : rows[1]); // 文档大类
				dcm.setDocclassify(rows[2].isEmpty() ? "" : rows[2]); // 文档细类
				dcm.setDocType(rows[3].isEmpty() ? "" : rows[3]); // 文档分类
				dcm.setSumbit(rows[4].replaceAll("&amp;", "\\&")); // 提交
				dcm.setCheck(rows[5].replaceAll("&amp;", "\\&")); // 规范性检查
				dcm.setCollator(rows[6].replaceAll("&amp;", "\\&")); // 校对				
				dcm.setReview(rows[7].replaceAll("&amp;", "\\&")); // 综合审核
				dcm.setCountersign(rows[8].replaceAll("&amp;", "\\&")); // 会签
				dcm.setNotice(rows[9].replaceAll("&amp;", "\\&")); // 通知
				dcm.setNoticeGroup(rows[10].replaceAll("&amp;", "\\&")); // 默认通知组
				dcm.setNeedPN(rows[11].isEmpty() ? "" : rows[11]); //是否必须关联PN
				
				String docType = dcm.getDocType().toString();
				String bigType = dcm.getDocBigType();
				doc.put(dcm.getDocclassify().toString() + (docType.isEmpty() ? "" : "_" + docType), dcm);
				needPNdoc.put(bigType+dcm.getDocclassify(), dcm);
			}// end if
		}// end for
		GlobalVariable.fileLastModifyTime.put("DocumentClassification.xls", file.lastModified());
		GlobalVariable.docWorkflowConfigBean.putAll(doc);
		GlobalVariable.docneedPnConfigBean.putAll(needPNdoc);
	}

	public static void checkFolderpath(ServletRequest request) throws WTException {
		String comtextString = request.getParameter("context");
		logger.debug("comtext==========" + comtextString);
		if (comtextString.indexOf("SubFolder") == -1) {
			throw new WTException("不能在根目录上创建文档   \n");
		}

	}

	public static String getRolepeople(WTDocument doc, String rolename) {
		Role crole = Role.toRole(rolename);
		String rolenameString = "";
		Team team2 = null;
		try {
			team2 = (Team) TeamHelper.service.getTeam(doc);
			if (team2 != null) {
				Enumeration cenumPrin = team2.getPrincipalTarget(crole);
				while (cenumPrin.hasMoreElements()) {
					WTPrincipalReference tempPrinRef = (WTPrincipalReference) cenumPrin.nextElement();
					rolenameString = rolenameString + " " + tempPrinRef.getFullName();
				}
			}
		} catch (TeamException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (WTException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		return rolenameString;
	}

	public static ClsReult checkDocClssify(WTDocument doc) throws WTException {
		checkNewFile();
		ClsReult result = new ClsReult();
		result.setIscollater(false);
		result.setIscountersign(false);
		result.setIsDocumentclsssify(false);
		result.setIsemail(false);
		String docSubName = GenericUtil.getObjectAttributeValue(doc, "subCategory") == null ? "" : GenericUtil.getObjectAttributeValue(doc, "subCategory").toString();
		String docTypeName = GenericUtil.getObjectAttributeValue(doc, "CATL_DocType") == null ? "" : GenericUtil.getObjectAttributeValue(doc, "CATL_DocType").toString();
		System.out.println("docSubName ===99999999999" + docSubName);
		System.out.println("docTypeName ===9999999999" + docTypeName);

		// DocClassificationModel clsmodel = (DocClassificationModel)
		// clsVector.get(i);
		// is collator review workitem
		System.out.println("isture ===9999999999" + (docSubName == "" && docTypeName == ""));
		if (!(docSubName == "" && docTypeName == "")) {
			result.setIsDocumentclsssify(true);
			System.out.println("isdocls ===9999999999" + result.getIsDocumentclsssify());
			String docClsname = docSubName + (docTypeName == "" ? "" : "_" + docTypeName);
			logger.debug("docClsname ===" + docClsname);
			DocClassificationModelNew clsmodelNew = GlobalVariable.docWorkflowConfigBean.get(docClsname);
			String collator = clsmodelNew.getCollator();
			logger.debug("collator role===" + collator);
			if (collator.indexOf("YES") > -1 || collator.indexOf("OPT") > -1) {
				result.setIscollater(true);
				String collatorrole = collator.substring(collator.indexOf("#") + 1, collator.length());
				logger.debug("collator role name===" + collatorrole);
				result.setCollaterrole(collatorrole);
			}
			String countersign = clsmodelNew.getCountersign();
			logger.debug("countersign role===" + countersign);
			if (countersign.indexOf("YES") > -1 || countersign.indexOf("OPT") > -1) {
				result.setIscountersign(true);
				String countersignrole = countersign.substring(countersign.indexOf("#") + 1, countersign.length());
				logger.debug("countersign role name===" + countersignrole);
				result.setCountersignrole(countersignrole);
			}
			String email = clsmodelNew.getNotice();
			logger.debug("email role==" + email);
			if (email.indexOf("YES") > -1) {
				result.setIsemail(true);
				String emailrole = email.substring(email.indexOf("#") + 1, email.length());
				logger.debug("emailrole role name===" + emailrole);
				result.setEmailrole(emailrole);
			}
			
			String noticeGroup = clsmodelNew.getNoticeGroup();
			logger.debug("notice Group ===" + noticeGroup);
			if (noticeGroup.indexOf("YES") > -1 || noticeGroup.indexOf("OPT") > -1) {
				String noticeGroups = noticeGroup.substring(noticeGroup.indexOf("#") + 1, noticeGroup.length());
				setNoticeGroup(doc, noticeGroups);
			}
		}

		System.out.println("result.getIscollater()==========" + result.getIscollater());
		System.out.println("result.getIscountersign()=======" + result.getIscountersign());
		System.out.println("result.getIsDocumentclsssify()==" + result.getIsDocumentclsssify());
		System.out.println("result.getIsemail()=============" + result.getIsemail());

		return result;
	}

	/**
	 * 文档流程--文控组添加到文控专员角色中
	 * 
	 * @param self
	 */
	public static void addDocumentController(ObjectReference self) {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		WfProcess process;
		try {
			process = (WfProcess) self.getObject();
			Team team = (Team) process.getTeamId().getObject();
			Role role = Role.toRole(RoleName.DOCUMENT_CONTROLLER);

			DirectoryContextProvider dcp = WTContainerHelper.service.getExchangeContainer().getContextProvider();
			WTOrganization org = OrganizationServicesHelper.manager.getOrganization("CATL", dcp);
			Enumeration<?> enu = OrganizationServicesHelper.manager.getGroups("文控知会组", WTContainerHelper.service.getOrgContainer(org).getContextProvider());
			while (enu.hasMoreElements()) {
				Object o = enu.nextElement();
				if (o instanceof WTGroup) {
					WTGroup group = (WTGroup) o;
					Enumeration users = group.members();
					while (users.hasMoreElements()) {
						WTUser user = (WTUser) users.nextElement();
						team.addPrincipal(role, user);
					}
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
	}

	public static StringBuffer checkSubmitDatasheet(ObjectReference self, WTDocument doc) throws WTException {
		StringBuffer buffer = new StringBuffer();

		WfAssignedActivity acivity = (WfAssignedActivity) self.getObject();
		WfProcess process = acivity.getParentProcess();
		Role productRole = Role.toRole(RoleName.STANDARD_ENGINEER);
		Enumeration productSelect = process.getPrincipals(productRole);
		Enumeration collatorSelect = process.getPrincipals(Role.toRole(RoleName.COLLATOR));

		Vector<WTPrincipal> productUser = findContainerTeamUserByRole(doc.getContainer(),productRole);

		if (!collatorSelect.hasMoreElements()) {
			buffer.append("“校对者”角色不能为空，请在设置参与者中选择一名“校对者”！ \n");
		}
		if (!productSelect.hasMoreElements()) {
			buffer.append("“标准化工程师”角色不能为空，请在设置参与者中选择一名“标准化工程师”！ \n");
		} else {
			while (productSelect.hasMoreElements()) {
				Object obj = productSelect.nextElement();
				if (obj instanceof WTPrincipalReference) {
					WTPrincipalReference principalref = (WTPrincipalReference) obj;
					WTPrincipal principal = principalref.getPrincipal();
					if (!productUser.contains(principal)) {
						buffer.append("“标准化工程师”角色中选择的:"+ principalref.getFullName()+",不正确，应该选择该“标准化工程师”角色中的人员！ \n");
					}
				}
			}
		}
		return buffer;
	}

	public static boolean isSelectPerson(ObjectReference self, String roleName) throws WTException {
		WfProcess process = (WfProcess) self.getObject();
		System.out.println("122222222222222222222222");
		Role role = Role.toRole(roleName);
		Enumeration principals = process.getPrincipals(role);

		if (principals.hasMoreElements())
			return true;

		return false;
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
						logger.debug("design role people name===" + principal.getName());
						roleuser.add(principal);
					}

				}
			}
		}
		return roleuser;
	}
	private static Vector findContainerTeamUserByRole(WTContainer container, Role role) throws WTException {
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
				logger.debug("design role people name===" + principal.getName());
				roleuser.add(principal);
			}

		}
		return roleuser;
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
	
	private static void setNoticeGroup(WTDocument doc,String groups) throws TeamException, WTException{
		if(doc!= null & StringUtils.isNotBlank(groups)){
			Team team = TeamHelper.service.getTeam(doc);
			String[] groupArray = groups.split("&");
			Role role = Role.toRole(RoleName.QUALITY_REPRESENTATIVE);
			for (int i = 0; i < groupArray.length; i++) {
				DirectoryContextProvider dcp = WTContainerHelper.service.getExchangeContainer().getContextProvider();
				WTOrganization org = OrganizationServicesHelper.manager.getOrganization("CATL", dcp);
				Enumeration<?> enu = OrganizationServicesHelper.manager.getGroups(groupArray[i], WTContainerHelper.service.getOrgContainer(org).getContextProvider());
				while (enu.hasMoreElements()) {
					Object o = enu.nextElement();
					if (o instanceof WTGroup) {
						WTGroup group = (WTGroup) o;
						team.addPrincipal(role, group);
					}
				}
			}
			PersistenceHelper.manager.refresh(team);
		}
		
	}
}
