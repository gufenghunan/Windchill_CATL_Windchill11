package com.catl.change.report.ecr;

import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.Logger;

import wt.change2.ChangeException2;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeRequest2;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.httpgw.GatewayAuthenticator;
import wt.method.RemoteMethodServer;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.project.Role;
import wt.team.Team;
import wt.team.TeamManaged;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.workflow.definer.UserEventVector;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfVotingEventAudit;
import wt.workflow.work.WorkItem;

import com.catl.change.report.model.AffectedpersonnelModel;
import com.catl.change.report.model.ChangepartsModel;
import com.catl.change.report.model.ECRAttachmentModel;
import com.catl.change.report.model.RoleConstant;
import com.catl.change.report.model.WorkItemCommentModel;
import com.catl.common.util.DocUtil;
import com.catl.common.util.GenericUtil;
import com.catl.common.util.PartUtil;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.workflow.NmWorkflowHelper;
import com.ptc.windchill.enterprise.workflow.WfDataUtilitiesHelper;
import com.ptc.windchill.enterprise.workflow.WorkflowCommands;

public class ECRAttachmentHtml {

	private static Logger log = Logger.getLogger(ECRAttachmentHtml.class.getName());
	private static String codebase = "";
	private static DateFormat format = new SimpleDateFormat("yyyy/MM/dd");

	static {
		try {
			WTProperties wtproperties = WTProperties.getLocalProperties();
			codebase = wtproperties.getProperty("wt.codebase.location");
		} catch (Throwable t) {
			throw new ExceptionInInitializerError(t);
		}
	}

	public static String getECRAttachmentsurl(WTChangeRequest2 ecr) throws WTException {

		ContentHolder Ecr = null;
		String url = "";
		String targetfilename = ecr.getNumber() + "_受影响的物料及产品.xls";
		try {
			Ecr = ContentHelper.service.getContents(ecr);
		} catch (PropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Vector vApplicationData = ContentHelper.getApplicationData(Ecr);
		log.debug("doc attachments ===" + ecr.getNumber() + "attachements ===" + vApplicationData.size());
		for (int i = 0; i < vApplicationData.size(); i++) {
			ApplicationData data = (ApplicationData) vApplicationData.get(i);
			String fileName = data.getFileName();
			if (fileName.equalsIgnoreCase(targetfilename)) {
				url = ContentHelper.getDownloadURL(ecr, data).toString();
				log.debug("data url===" + url);
			}
		}
		return url;

	}

	public static boolean JspToHtmlFile(String filePath, String HtmlFile, WTChangeRequest2 ecr, String changetype) throws WTException {
		String str = "";
		String partlistString = "";
		Vector<ChangepartsModel> list = getChangePartsInfo(ecr);
		for (int i = 0; i < list.size(); i++) {
			ChangepartsModel changepartsModel = new ChangepartsModel();
			changepartsModel = (ChangepartsModel) list.get(i);
			String partlistInfo = "<TR><TD>Changed P/N<br>变更物料编码</TD><TD>" + changepartsModel.getPartnumber() + "</TD>" + "<TD>Part Name<br>物料名称</TD><TD>" + changepartsModel.getPartname() + "</TD>" + "<TD>Affected Parent P/N<br>受影响的上级物料编码</TD><TD>" + changepartsModel.getAffectedparentPN() + "</TD>"
					+ "<TD>Affected Parent Part Name<br>受影响的上级物料名称</TD>" + "<TD colspan=3>" + changepartsModel.getAffectedparentName() + "</TD></TR>";

			partlistString = partlistString + partlistInfo;
		}
		try {
			String tempStr = "";
			InputStreamReader is = new InputStreamReader(new FileInputStream(new File(filePath)), "UTF-8"); // or
																											// GBK
			BufferedReader br = new BufferedReader(is);
			while ((tempStr = br.readLine()) != null) {
				str = str + tempStr;
				if (tempStr.startsWith("		<Th colspan=10> Change Parts/变更物料"))// can
																				// not
																				// deltet
																				// the
																				// space
				{
					str = str + partlistString;
				}
			}
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		String url = getECRAttachmentsurl(ecr);
		ECRAttachmentModel ecrAttachmentModel = getECRinfo(ecr);
		try {
			str = str.replaceAll("affectparturl", url);
			str = str.replaceAll("ecrNumber", ecrAttachmentModel.getEcrnumber());
			str = str.replaceAll("ecwNO", ecrAttachmentModel.getEcrWNO());
			str = str.replaceAll("applicanter", ecrAttachmentModel.getApplicant());
			str = str.replaceAll("applicantiondept", ecrAttachmentModel.getApplicantiondepartment());// replace
																										// attibutes
			str = str.replaceAll("applicantiondate", ecrAttachmentModel.getApplicantiondate());
			str = str.replaceAll("changeorigin", ecrAttachmentModel.getChangeorigin());
			str = str.replaceAll("changetype", ecrAttachmentModel.getChangetype());
			str = str.replaceAll("changeitem", ecrAttachmentModel.getChangeitem());
			str = str.replaceAll("changebackgrounddescription", ecrAttachmentModel.getChangebackgrounddescription());
			str = str.replaceAll("initialchangesolution", ecrAttachmentModel.getInitialchangesolution());
			str = str.replaceAll("se_pe_estimate", changetype);
			str = str.replaceAll("se_pe_conclusion_proposal", ecrAttachmentModel.getSe_pe_conclusion_proposal());
			str = str.replaceAll("se_pe_conclusion_sign", ecrAttachmentModel.getSe_pe_conclusion_sign());
			str = str.replaceAll("se_pe_conclusion_date", ecrAttachmentModel.getSe_pe_conclusion_date());
			str = str.replaceAll("application_dept_manager_approve", ecrAttachmentModel.getApplication_dept_manager_approve());
			str = str.replaceAll("application_dept_manager_sign", ecrAttachmentModel.getApplication_dept_manager_sign());
			str = str.replaceAll("application_dept_manager_date", ecrAttachmentModel.getApplication_dept_manager_date());

			AffectedpersonnelModel affectedpersonnelModel = ecrAttachmentModel.getAffectedpersonnelModel();
			// set role replace
			str = str.replaceAll("pm_role", affectedpersonnelModel.getPm());
			str = str.replaceAll("pd_role", affectedpersonnelModel.getPd() + affectedpersonnelModel.getSe());
			str = str.replaceAll("mpe_role", affectedpersonnelModel.getMpe());
			str = str.replaceAll("src_role", affectedpersonnelModel.getSrc());

			str = str.replaceAll("qa_role", affectedpersonnelModel.getQa());
			str = str.replaceAll("rd_sys1_role", affectedpersonnelModel.getRd_sys1());
			str = str.replaceAll("rd_sys2_role", affectedpersonnelModel.getRd_sys2());
			str = str.replaceAll("mde_role", affectedpersonnelModel.getMde());
			str = str.replaceAll("tvc_role", affectedpersonnelModel.getTvc());

			str = str.replaceAll("rsd_role", affectedpersonnelModel.getRsd1() + affectedpersonnelModel.getRsd2());
			str = str.replaceAll("rd_md_role", affectedpersonnelModel.getRd_md());
			str = str.replaceAll("pmc_role", affectedpersonnelModel.getPmc1() + affectedpersonnelModel.getPmc2());
			str = str.replaceAll("aft_sls_role", affectedpersonnelModel.getAft_sls());

			str = str.replaceAll("sqe_role", affectedpersonnelModel.getSqe());
			str = str.replaceAll("rd_ee_role", affectedpersonnelModel.getRd_ee());
			str = str.replaceAll("ie_role", affectedpersonnelModel.getIe());
			str = str.replaceAll("sls_mkt_role", affectedpersonnelModel.getSls_mkt());

			str = str.replaceAll("fnc_role", affectedpersonnelModel.getFnc());
			str = str.replaceAll("rd_sw_role", affectedpersonnelModel.getRd_sw());
			str = str.replaceAll("mfg_role", affectedpersonnelModel.getMfg());
			str = str.replaceAll("ehs_role", affectedpersonnelModel.getEhs());
			str = str.replaceAll("other_review_people_role", affectedpersonnelModel.getOthers());
			str = str.replaceAll("expert_group_role", affectedpersonnelModel.getExpert_group());

			str = str.replaceAll("mfg_cbb_role", affectedpersonnelModel.getCbb_mfg());
			str = str.replaceAll("pmc_cbb_role", affectedpersonnelModel.getCbb_pmc());
			str = str.replaceAll("pm_cbb_role", affectedpersonnelModel.getCbb_pm());
			str = str.replaceAll("pd_cbb_role", affectedpersonnelModel.getCbb_pd());
			str = str.replaceAll("mde_cbb_role", affectedpersonnelModel.getCbb_mde());
			str = str.replaceAll("fnc_cbb_role", affectedpersonnelModel.getCbb_fnc());
			str = str.replaceAll("qa_cbb_role", affectedpersonnelModel.getCbb_qa());
			str = str.replaceAll("rsd_cbb_role", affectedpersonnelModel.getCbb_rsd());
			str = str.replaceAll("src_cbb_role", affectedpersonnelModel.getCbb_src());
			str = str.replaceAll("other_review_people_cbb_role", affectedpersonnelModel.getCbb_other());

			File f = new File(HtmlFile);
			OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(f), "UTF-8");
			BufferedWriter o = new BufferedWriter(os);
			o.write(str);
			o.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static ECRAttachmentModel getECRinfo(WTChangeRequest2 ecr) throws WTException {
		ECRAttachmentModel ecrAttachmentModel = new ECRAttachmentModel();

		if (ecr == null) {
			log.debug("ECRAttachmentHtml  ecr is null----");
			return null;
		} else {
			String ecrWNO = GenericUtil.getObjectAttributeValue(ecr, "enwNumber") == null ? "" : GenericUtil.getObjectAttributeValue(ecr, "enwNumber").toString();
			String department = GenericUtil.getObjectAttributeValue(ecr, "department") == null ? "" : GenericUtil.getObjectAttributeValue(ecr, "department").toString();
			String origin = GenericUtil.getObjectAttributeValue(ecr, "changeFrom") == null ? "" : GenericUtil.getObjectAttributeValue(ecr, "changeFrom").toString();
			Object values = GenericUtil.getObjectAttributeValue(ecr, "changeType") == null ? "" : GenericUtil.getObjectAttributeValue(ecr, "changeType");
			String changetype = "";
			if (values instanceof String) {
				changetype = (String) values;
			} else {
				Object[] v = (Object[]) values;
				for (int i = 0; i < v.length; i++) {
					String s = (String) v[i];
					changetype = changetype + s + " ";
				}
			}

			String description = ecr.getDescription() == null ? "" : ecr.getDescription().toString();
			String ProposedSolution = ecr.getProposedSolution() == null ? "" : ecr.getProposedSolution().toString();

			ecrAttachmentModel.setEcrnumber(ecr.getNumber());
			ecrAttachmentModel.setEcrWNO(ecrWNO);// need to add
			ecrAttachmentModel.setApplicant(ecr.getCreatorFullName());
			ecrAttachmentModel.setApplicantiondepartment(department);
			ecrAttachmentModel.setApplicantiondate(format.format(ecr.getCreateTimestamp()));
			ecrAttachmentModel.setChangeorigin(origin);
			ecrAttachmentModel.setChangeitem(ecr.getName());
			ecrAttachmentModel.setChangetype(changetype);
			ecrAttachmentModel.setChangebackgrounddescription(description);
			ecrAttachmentModel.setInitialchangesolution(ProposedSolution);
			// ecrAttachmentModel.setChangedPNVector(getChangePartsInfo(ecr));
			ecrAttachmentModel.setAffectedpersonnelModel(getECRProcessRole(ecr));

			WorkItemCommentModel workItemCommentModel = getAllWorkItemComments(ecr);
			ecrAttachmentModel.setSE_PE_Estimate(workItemCommentModel.getSE_PE_Estimate());
			ecrAttachmentModel.setSe_pe_conclusion_proposal(workItemCommentModel.getSe_pe_conclusion_proposal());
			ecrAttachmentModel.setSe_pe_conclusion_sign(workItemCommentModel.getSe_pe_conclusion_sign());
			ecrAttachmentModel.setSe_pe_conclusion_date(workItemCommentModel.getSe_pe_conclusion_date());
			ecrAttachmentModel.setApplication_dept_manager_approve(workItemCommentModel.getApplication_dept_manager_approve());
			ecrAttachmentModel.setApplication_dept_manager_sign(workItemCommentModel.getApplication_dept_manager_sign());
			ecrAttachmentModel.setApplication_dept_manager_date(workItemCommentModel.getApplication_dept_manager_date());
		}
		return ecrAttachmentModel;
	}

	public static Vector<ChangepartsModel> getChangePartsInfo(WTChangeRequest2 ecr) {

		QueryResult queryResult = null;
		Vector<ChangepartsModel> changeVector = new Vector<ChangepartsModel>();
		try {
			queryResult = ChangeHelper2.service.getChangeables(ecr);
			log.debug("ecr change parts ==" + queryResult.size());
		} catch (ChangeException2 e) {
			log.debug("ChangeException2:get changeables failed ---!");
			e.printStackTrace();
		} catch (WTException e) {
			log.debug("WTException:get changeables failed ---!");
			e.printStackTrace();
		}
		while (queryResult.hasMoreElements()) {
			Object object = (Object) queryResult.nextElement();
			QueryResult parentpartRe = null;
			if (object instanceof WTPart) {
				WTPart part = (WTPart) object;
				try {
					parentpartRe = WTPartHelper.service.getUsedByWTParts((WTPartMaster) part.getMaster());
					log.debug("parent part size==" + parentpartRe.size());
				} catch (WTException e) {
					log.debug("getusedbywtparts failed  ----");
					e.printStackTrace();
				}
				while (parentpartRe.hasMoreElements()) {
					WTPart parentPart = (WTPart) parentpartRe.nextElement();
					try {
						if (!PartUtil.existGreaterVersion(parentPart)) {
							ChangepartsModel changepartsModel = new ChangepartsModel();
							changepartsModel.setPartname(part.getName());
							changepartsModel.setPartnumber(part.getNumber());
							changepartsModel.setAffectedparentName(parentPart.getName());
							changepartsModel.setAffectedparentPN(parentPart.getNumber());
							changeVector.add(changepartsModel);
						}
					} catch (WTException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}

		}

		return changeVector;
	}

	public ECRAttachmentHtml() {
		// TODO Auto-generated constructor stub
	}

	public static WorkItemCommentModel getAllWorkItemComments(WTChangeRequest2 ecr) throws WTException {

		WorkItemCommentModel workItemCommentModel = new WorkItemCommentModel();
		wt.fc.ReferenceFactory rf = new wt.fc.ReferenceFactory();
		QueryResult processResult = null;
		WfProcess process = null;

		try {
			processResult = NmWorkflowHelper.service.getAssociatedProcesses(ecr, null, null);// may
																								// be
																								// improve
			log.debug("processResult size====" + processResult.size());
		} catch (WTException e1) {
			log.debug("getAssociatedProcesses failed----!");
			e1.printStackTrace();
		}

		if (processResult.hasMoreElements()) {
			process = (WfProcess) processResult.nextElement();
		}
		String processOid = rf.getReferenceString(process);
		NmOid oid = NmOid.newNmOid(processOid);
		QueryResult qr = WorkflowCommands.getRouteStatus(oid);
		log.debug("getRouteStatus qr size===" + qr.size());
		Timestamp SEcreatetimestamp = null;
		Timestamp FMcreatetimestamp = null;
		Boolean isset = false;
		while (qr.hasMoreElements()) {
			Object obj = qr.nextElement();
			WorkItem workItem = null;
			WfVotingEventAudit event = null;
			if (obj instanceof WorkItem) {
				workItem = (WorkItem) obj;
			} else if (obj instanceof WfVotingEventAudit) {
				System.out.println("routing status object is WfVotingEventAudit");
				continue;

			}
			WfActivity wfa = (WfActivity) workItem.getSource().getObject();// 活动节点
			String workComments = "";
			String workVote = "";
			event = WfDataUtilitiesHelper.getMatchingEventAudit(workItem);
			if (event == null) {
				continue;
			}
			String activityName = wfa.getName();
			UserEventVector eventList = event.getEventList();
			for (int i = 0; eventList != null && i < eventList.size(); i++) {
				if (workVote.length() > 0) {
					workVote += ",";
				}
				workVote += eventList.get(i);
			}
			log.debug("workvote========" + workVote);
			if (activityName.equalsIgnoreCase(RoleConstant.SEPE_Estimate_ItemName)) {
				Timestamp timestamp = event.getModifyTimestamp(); // compare
																	// activity
																	// time
				log.debug("timestamp=========" + timestamp);
				log.debug("createtimestamp==========" + SEcreatetimestamp);
				if (SEcreatetimestamp != null) {
					if (timestamp.after(SEcreatetimestamp)) {
						SEcreatetimestamp = timestamp;
						isset = true;
					} else {
						isset = false;
					}
				} else {
					SEcreatetimestamp = timestamp;
					isset = true;
				}
				workComments = event.getUserComment();
				log.debug("SE PE sworkcommet==" + workComments + " iset=" + isset);
				if (isset) {
					workItemCommentModel.setSe_pe_conclusion_date(format.format(event.getModifyTimestamp()));
					workItemCommentModel.setSe_pe_conclusion_sign(event.getUserRef().getFullName());
					workItemCommentModel.setSe_pe_conclusion_proposal(workVote + "<br>" + workComments);
				}
			}
			if (activityName.equalsIgnoreCase(RoleConstant.application_dept_manager_approve_ItemName)) {
				workComments = event.getUserComment(); // compare activity time
				Timestamp timestamp = event.getModifyTimestamp();
				log.debug("timestamp=========" + timestamp);
				log.debug("createtimestamp==========" + FMcreatetimestamp);
				if (FMcreatetimestamp != null) {
					if (timestamp.after(FMcreatetimestamp)) {
						FMcreatetimestamp = timestamp;
						isset = true;
					} else {
						isset = false;
					}
				} else {
					FMcreatetimestamp = timestamp;
					isset = true;
				}
				log.debug("department manager workcommet==" + workComments + " iset=" + isset);
				if (isset) {
					workItemCommentModel.setApplication_dept_manager_date(format.format(event.getModifyTimestamp()));
					workItemCommentModel.setApplication_dept_manager_sign(event.getUserRef().getFullName());
					workItemCommentModel.setApplication_dept_manager_approve(workVote + "<br>" + workComments);
				}
			}
		}

		return workItemCommentModel;
	}

	public static AffectedpersonnelModel getECRProcessRole(WTChangeRequest2 ecr) throws WTException {
		QueryResult processResult = null;
		Vector<Role> roleVector = new Vector<Role>();
		AffectedpersonnelModel affectedpersonnelModel = new AffectedpersonnelModel();
		try {
			processResult = NmWorkflowHelper.service.getAssociatedProcesses(ecr, null, null);// may
																								// be
																								// improve
			log.debug("processResult size====" + processResult.size());
		} catch (WTException e1) {
			log.debug("getAssociatedProcesses failed----!");
			e1.printStackTrace();
		}
		if (processResult.hasMoreElements()) {
			WfProcess process = (WfProcess) processResult.nextElement();
			try {
				roleVector = process.getProcessRoles();
				System.out.println("role Vector=" + roleVector.size());
			} catch (WTException e) {
				log.debug("getProcessRoles failed----!");
				e.printStackTrace();
			}
			for (int i = 0; i < roleVector.size(); i++) {
				Role role = (Role) roleVector.get(i);
				log.debug("role===" + role.toString());
				String rolepeople = getRolepersoninfo(process, role);
				log.debug("role people===" + rolepeople);
				if (role.toString().equalsIgnoreCase(RoleConstant.PM)) {
					affectedpersonnelModel.setPm(rolepeople);
				}
				if (role.toString().equalsIgnoreCase(RoleConstant.AFT_SLS)) {
					affectedpersonnelModel.setAft_sls(rolepeople);
				}
				if (role.toString().equalsIgnoreCase(RoleConstant.EHS)) {
					affectedpersonnelModel.setEhs(rolepeople);
				}
				if (role.toString().equalsIgnoreCase(RoleConstant.FNC)) {
					affectedpersonnelModel.setFnc(rolepeople);
				}
				if (role.toString().equalsIgnoreCase(RoleConstant.MDE)) {
					affectedpersonnelModel.setMde(rolepeople);
				}
				if (role.toString().equalsIgnoreCase(RoleConstant.MFG)) {
					affectedpersonnelModel.setMfg(rolepeople);
				}
				if (role.toString().equalsIgnoreCase(RoleConstant.MPE)) {
					affectedpersonnelModel.setMpe(rolepeople);
				}
				if (role.toString().equalsIgnoreCase(RoleConstant.PD)) {
					affectedpersonnelModel.setPd(rolepeople);
				}

				if (role.toString().equalsIgnoreCase(RoleConstant.SE)) {
					affectedpersonnelModel.setSe(rolepeople);
				}

				if (role.toString().equalsIgnoreCase(RoleConstant.PMC1)) {
					affectedpersonnelModel.setPmc1(rolepeople);
				}
				if (role.toString().equalsIgnoreCase(RoleConstant.PMC2)) {
					affectedpersonnelModel.setPmc2(rolepeople);
				}
				if (role.toString().equalsIgnoreCase(RoleConstant.QA)) {
					affectedpersonnelModel.setQa(rolepeople);
				}
				if (role.toString().equalsIgnoreCase(RoleConstant.RD_EE)) {
					affectedpersonnelModel.setRd_ee(rolepeople);
				}
				if (role.toString().equalsIgnoreCase(RoleConstant.RD_MD)) {
					affectedpersonnelModel.setRd_md(rolepeople);
				}
				if (role.toString().equalsIgnoreCase(RoleConstant.RD_SW)) {
					affectedpersonnelModel.setRd_sw(rolepeople);
				}
				if (role.toString().equalsIgnoreCase(RoleConstant.RD_SYS1)) {
					affectedpersonnelModel.setRd_sys1(rolepeople);
				}
				if (role.toString().equalsIgnoreCase(RoleConstant.RD_SYS2)) {
					affectedpersonnelModel.setRd_sys2(rolepeople);
				}
				if (role.toString().equalsIgnoreCase(RoleConstant.RSD1)) {
					affectedpersonnelModel.setRsd1(rolepeople);
				}
				if (role.toString().equalsIgnoreCase(RoleConstant.RSD2)) {
					affectedpersonnelModel.setRsd2(rolepeople);
				}
				if (role.toString().equalsIgnoreCase(RoleConstant.SLS_MKT)) {
					affectedpersonnelModel.setSls_mkt(rolepeople);
				}
				if (role.toString().equalsIgnoreCase(RoleConstant.SQE)) {
					affectedpersonnelModel.setSqe(rolepeople);
				}
				if (role.toString().equalsIgnoreCase(RoleConstant.TVC)) {
					affectedpersonnelModel.setTvc(rolepeople);
				}
				if (role.toString().equalsIgnoreCase(RoleConstant.SRC)) {
					affectedpersonnelModel.setSrc(rolepeople);
				}
				if (role.toString().equalsIgnoreCase(RoleConstant.IE)) {
					affectedpersonnelModel.setIe(rolepeople);
				}
				if (role.toString().equalsIgnoreCase(RoleConstant.Others)) {
					affectedpersonnelModel.setOthers(rolepeople);
				}
				if (role.toString().equalsIgnoreCase(RoleConstant.IE)) {
					affectedpersonnelModel.setIe(rolepeople);
				}
				if (role.toString().equalsIgnoreCase(RoleConstant.EHS)) {
					affectedpersonnelModel.setEhs(rolepeople);
				}
				if (role.toString().equalsIgnoreCase(RoleConstant.expert_group)) {
					affectedpersonnelModel.setExpert_group(rolepeople);
				}

				if (role.toString().equalsIgnoreCase(RoleConstant.CCB_PM)) {
					affectedpersonnelModel.setCbb_pm(rolepeople);
				}
				if (role.toString().equalsIgnoreCase(RoleConstant.CCB_PMC)) {
					affectedpersonnelModel.setCbb_pmc(rolepeople);
				}
				if (role.toString().equalsIgnoreCase(RoleConstant.CCB_PD)) {
					affectedpersonnelModel.setCbb_pd(rolepeople);
				}
				if (role.toString().equalsIgnoreCase(RoleConstant.CCB_QA)) {
					affectedpersonnelModel.setCbb_qa(rolepeople);
				}
				if (role.toString().equalsIgnoreCase(RoleConstant.CCB_MFG)) {
					affectedpersonnelModel.setCbb_mfg(rolepeople);
				}
				if (role.toString().equalsIgnoreCase(RoleConstant.CCB_FNC)) {
					affectedpersonnelModel.setCbb_fnc(rolepeople);
				}
				if (role.toString().equalsIgnoreCase(RoleConstant.CCB_MDE)) {
					affectedpersonnelModel.setCbb_mde(rolepeople);
				}
				if (role.toString().equalsIgnoreCase(RoleConstant.CCB_OTHERS)) {
					affectedpersonnelModel.setCbb_other(rolepeople);
				}
				if (role.toString().equalsIgnoreCase(RoleConstant.CCB_RSD)) {
					affectedpersonnelModel.setCbb_rsd(rolepeople);
				}
				if (role.toString().equalsIgnoreCase(RoleConstant.CCB_SRC)) {
					affectedpersonnelModel.setCbb_src(rolepeople);
				}
			}

		}
		return affectedpersonnelModel;
	}

	public static String getRolepersoninfo(WfProcess process, Role role) throws WTException {
		String rolenameString = "";
		if (role != null) {
			for (Enumeration enumer = getRoleEnumeration(process, role.toString()); enumer != null && enumer.hasMoreElements();) {
				WTPrincipalReference ref = (WTPrincipalReference) enumer.nextElement();
				rolenameString = rolenameString + " " + ref.getFullName();
			}
		}
		return rolenameString;
	}

	public static Enumeration getRoleEnumeration(TeamManaged teammanaged, String rolename) throws WTException {
		if (teammanaged == null || rolename == null || rolename.length() == 0)
			return null;
		Team team = (Team) teammanaged.getTeamId().getObject();
		if (team == null) {
			return null;
		} else {
			Role role = Role.toRole(rolename);
			return team.getPrincipalTarget(role);
		}
	}

	public static void doOperation(WTChangeRequest2 ecr, String changetype) {

		String filepath = codebase + RoleConstant.defaultLocation + RoleConstant.ecrModelname;
		String newfilepaht = codebase + File.separator + "temp" + File.separator + ecr.getNumber() + "_ECR报告.html";
		log.debug("filepath===" + filepath);
		log.debug("newfilepath===" + newfilepaht);
		log.debug("ecr number==" + ecr.getNumber());

		try {
			JspToHtmlFile(filepath, newfilepaht, ecr, changetype);
		} catch (WTException e) {
			e.printStackTrace();
		}
		String filename = ecr.getNumber() + "_ECR报告.html";
		DocUtil.addAttachment(ecr, newfilepaht, filename);
	}

	public static void main(String[] arg) throws WTException {
		long begin = System.currentTimeMillis();
		String url = "";
		String savepath = "";
		try {
			url = WTProperties.getLocalProperties().getProperty("wt.home")+"/temp/ECR1.html";
			savepath = WTProperties.getLocalProperties().getProperty("wt.home")+"/test.html";//
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		GatewayAuthenticator auth = new GatewayAuthenticator();
		auth.setRemoteUser("wcadmin");
		rms.setAuthenticator(auth);
		String oid = arg[0];
		String oidString = "OR:wt.change2.WTChangeRequest2:" + oid;
		Persistable object = PartUtil.getPersistableByOid(oidString);
		WTChangeRequest2 changeRequest2 = (WTChangeRequest2) object;
		getAllWorkItemComments(changeRequest2);
		JspToHtmlFile(url, savepath, changeRequest2, "changetype");

	}
}