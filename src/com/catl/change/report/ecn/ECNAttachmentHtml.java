package com.catl.change.report.ecn;

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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.tigris.subversion.javahl.LogDate;

import wt.change2.ChangeException2;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.fc.ObjectReference;
import wt.fc.QueryResult;
import wt.org.WTPrincipalReference;
import wt.project.Role;
import wt.team.Team;
import wt.team.TeamException;
import wt.team.TeamHelper;
import wt.team.TeamManaged;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.workflow.definer.UserEventVector;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfVotingEventAudit;
import wt.workflow.work.WorkItem;

import com.catl.change.report.model.RoleConstant;
import com.catl.change.workflow.ECWorkflowUtil;
import com.catl.common.constant.RoleName;
import com.catl.common.util.DocUtil;
import com.catl.common.util.GenericUtil;
import com.ptc.core.lwc.common.dynamicEnum.EnumerationEntryInfo;
import com.ptc.core.lwc.common.dynamicEnum.EnumerationInfo;
import com.ptc.core.lwc.common.dynamicEnum.EnumerationInfoManager;
import com.ptc.core.lwc.common.dynamicEnum.EnumerationInfoProvider;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.windchill.enterprise.workflow.WfDataUtilitiesHelper;
import com.ptc.windchill.enterprise.workflow.WorkflowCommands;

public class ECNAttachmentHtml {

	private static Logger log = Logger.getLogger(ECNAttachmentHtml.class.getName());
	private static String codebase = "";

	static {
		try {
			WTProperties wtproperties = WTProperties.getLocalProperties();
			codebase = wtproperties.getProperty("wt.codebase.location");
		} catch (Throwable t) {
			throw new ExceptionInInitializerError(t);
		}
	}

	public static void main(String[] args) throws WTException {

	}

	public static void doCreateECNHtmlReport(WTChangeOrder2 ecn, ObjectReference self) {
		String filepath = codebase + RoleConstant.defaultLocation + RoleConstant.ecnModelname;
		String newfilepaht = codebase + File.separator + "temp" + File.separator + ecn.getNumber() + "_ECN报告.html";
		log.debug("filepath===" + filepath);
		log.debug("newfilepath===" + newfilepaht);
		log.debug("ecn number==" + ecn.getNumber());
		log.debug("start create html file------------>");

		try {
			JspToHtmlFile(filepath, newfilepaht, ecn, self);
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String filename = ecn.getNumber() + "_ECN报告.html";
		try {
			DocUtil.deleteAttachmentHtml(ecn);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DocUtil.addAttachment(ecn, newfilepaht, filename);
	}

	public static ArrayList<ECNChangePlanCommentModel> getECAInfo(WTChangeOrder2 ecn) throws WTException {
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		ArrayList<ECNChangePlanCommentModel> ecainfoList = new ArrayList<ECNChangePlanCommentModel>();
		QueryResult ecaQueryResult = new QueryResult();
		try {
			ecaQueryResult = ChangeHelper2.service.getChangeActivities(ecn);
		} catch (ChangeException2 e) {
			log.debug("get eca failed" + e.getMessage());
			e.printStackTrace();
		} catch (WTException e) {
			log.debug("get eca failed" + e.getMessage());
			e.printStackTrace();
		}
		while (ecaQueryResult.hasMoreElements()) {
			WTChangeActivity2 eca = (WTChangeActivity2) ecaQueryResult.nextElement();
			ECNChangePlanCommentModel ecamodel = new ECNChangePlanCommentModel();
			ecamodel.setImplementtationplancontent(eca.getName());
			if (null != eca.getNeedDate()) {
				ecamodel.setDuedate(format.format(fixTime(eca.getNeedDate())));
			}
			String assignee = getRoleEnumeration(RoleName.ASSIGNEE, eca);

			ecamodel.setOwner(assignee);
			String department = GenericUtil.getObjectAttributeValue(eca, "department") == null ? "" : GenericUtil.getObjectAttributeValue(eca, "department").toString();
			ecamodel.setDepartment(department);
			ecainfoList.add(ecamodel);
		}
		return ecainfoList;
	}

	public static String getRoleEnumeration(String rolename, TeamManaged object) throws WTException {
		String assignee = "";
		Role role = Role.toRole(rolename);
		Team team2 = null;
		try {
			team2 = (Team) TeamHelper.service.getTeam(object);
			if (team2 != null) {
				Enumeration enumPrin = team2.getPrincipalTarget(role);
				while (enumPrin.hasMoreElements()) {
					WTPrincipalReference tempPrinRef = (WTPrincipalReference) enumPrin.nextElement();
					log.debug("assginee role people name===" + tempPrinRef.getFullName());
					assignee = assignee + " " + tempPrinRef.getFullName();
				}
			}
		} catch (TeamException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (WTException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		return assignee;
	}

	public static ECNReport2HtmlModel getECNReportInfo(WTChangeOrder2 ecn, ObjectReference self) throws WTException {
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		WTChangeRequest2 ecr = ECWorkflowUtil.getEcrByEcn(ecn);
		ECNReport2HtmlModel ecnReport2HtmlModel = new ECNReport2HtmlModel();
		// set ecn info
		ecnReport2HtmlModel.setEcnNO(ecn.getNumber());
		ecnReport2HtmlModel.setEcrnumber(ecr.getNumber());

		Object stage = GenericUtil.getObjectAttributeValue(ecn, "stage");
		if (stage == null) {
			ecnReport2HtmlModel.setStage("");
		} else {
			ecnReport2HtmlModel.setStage(stage.toString());
		}

		String applicantiondate = "";
		Object applicantiondateObj = GenericUtil.getObjectAttributeValue(ecn, "reviewDate");
		if (applicantiondate != null) {
			applicantiondate = format.format(fixTime((Timestamp) applicantiondateObj));
		}
		ecnReport2HtmlModel.setApplicantiondate(applicantiondate);

		Object values = GenericUtil.getObjectAttributeValue(ecn, "changeType") == null ? "" : GenericUtil.getObjectAttributeValue(ecn, "changeType");
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
		String changereason = GenericUtil.getObjectAttributeValue(ecr, "changeFrom") == null ? "" : GenericUtil.getObjectAttributeValue(ecr, "changeFrom").toString();
		String description = ecn.getDescription() == null ? "" : ecn.getDescription();

		ecnReport2HtmlModel.setChangeitem(ecn.getName());
		ecnReport2HtmlModel.setChangeapplicanter(ecn.getCreatorFullName());
		ecnReport2HtmlModel.setChangedescription(description);
		ecnReport2HtmlModel.setChangetype(changetype);
		ecnReport2HtmlModel.setChangereason(changereason);

		WfProcess process = (WfProcess) self.getObject();
		ProcessData data = process.getContext();
		String pmcomment = data.getValue("pmcomment") == null ? "" : data.getValue("pmcomment").toString();
		String pmowner = data.getValue("pmowner") == null ? "" : data.getValue("pmowner").toString();

		ecnReport2HtmlModel.setPmcomment(pmcomment);
		ecnReport2HtmlModel.setPmowner(pmowner);

		String pqmcomment = data.getValue("pqmcomment") == null ? "" : data.getValue("pqmcomment").toString();
		String pqmowner = data.getValue("pqmowner") == null ? "" : data.getValue("pqmowner").toString();

		ecnReport2HtmlModel.setPqmcomment(pqmcomment);
		ecnReport2HtmlModel.setPqmowner(pqmowner);

		String rsdcomment = data.getValue("rsdcomment") == null ? "" : data.getValue("rsdcomment").toString();
		String rsdowner = data.getValue("rsdowner") == null ? "" : data.getValue("rsdowner").toString();

		ecnReport2HtmlModel.setRsdcomment(rsdcomment);
		ecnReport2HtmlModel.setRsdowner(rsdowner);

		String sqecomment = data.getValue("sqecomment") == null ? "" : data.getValue("sqecomment").toString();
		String sqeowner = data.getValue("sqeowner") == null ? "" : data.getValue("sqeowner").toString();

		ecnReport2HtmlModel.setSqecomment(sqecomment);
		ecnReport2HtmlModel.setSqeowner(sqeowner);

		String pdcomment = data.getValue("pdcomment") == null ? "" : data.getValue("pdcomment").toString();
		String pdowner = data.getValue("pdowner") == null ? "" : data.getValue("pdowner").toString();

		ecnReport2HtmlModel.setPdcomment(pdcomment);
		ecnReport2HtmlModel.setPdowner(pdowner);

		String rdmdcomment = data.getValue("rdmdcomment") == null ? "" : data.getValue("rdmdcomment").toString();
		String rdmdowner = data.getValue("rdmdowner") == null ? "" : data.getValue("rdmdowner").toString();

		ecnReport2HtmlModel.setRdmdcomment(rdmdcomment);
		ecnReport2HtmlModel.setRdmdowner(rdmdowner);

		String rdsyscomment = data.getValue("rdsyscomment") == null ? "" : data.getValue("rdsyscomment").toString();
		String rdsysowner = data.getValue("rdsysowner") == null ? "" : data.getValue("rdsysowner").toString();

		ecnReport2HtmlModel.setRdsyscomment(rdsyscomment);
		ecnReport2HtmlModel.setRdsysowner(rdsysowner);

		String mpecomment = data.getValue("mpecomment") == null ? "" : data.getValue("mpecomment").toString();
		String mpeowner = data.getValue("mpeowner") == null ? "" : data.getValue("mpeowner").toString();

		ecnReport2HtmlModel.setMpecomment(mpecomment);
		ecnReport2HtmlModel.setMpeowner(mpeowner);

		String mdecomment = data.getValue("mdecomment") == null ? "" : data.getValue("mdecomment").toString();
		String mdeowner = data.getValue("mdeowner") == null ? "" : data.getValue("mdeowner").toString();

		ecnReport2HtmlModel.setMdecomment(mdecomment);
		ecnReport2HtmlModel.setMdeowner(mdeowner);

		String iecomment = data.getValue("iecomment") == null ? "" : data.getValue("iecomment").toString();
		String ieowner = data.getValue("ieowner") == null ? "" : data.getValue("ieowner").toString();

		ecnReport2HtmlModel.setIecomment(iecomment);
		ecnReport2HtmlModel.setIeowner(ieowner);

		String mfgcomment = data.getValue("mfgcomment") == null ? "" : data.getValue("mfgcomment").toString();
		String mfgowner = data.getValue("mfgowner") == null ? "" : data.getValue("mfgowner").toString();

		ecnReport2HtmlModel.setMfgcomment(mfgcomment);
		ecnReport2HtmlModel.setMfgowner(mfgowner);

		String pcmccomment = data.getValue("pcmccomment") == null ? "" : data.getValue("pcmccomment").toString();
		String pcmcowner = data.getValue("pcmcowner") == null ? "" : data.getValue("pcmcowner").toString();

		ecnReport2HtmlModel.setPcmccomment(pcmccomment);
		ecnReport2HtmlModel.setPcmcowner(pcmcowner);

		String srccomment = data.getValue("srccomment") == null ? "" : data.getValue("srccomment").toString();
		String srcowner = data.getValue("srcowner") == null ? "" : data.getValue("srcowner").toString();

		ecnReport2HtmlModel.setSrccomment(srccomment);
		ecnReport2HtmlModel.setSrcowner(srcowner);

		String aftslscomment = data.getValue("aftslscomment") == null ? "" : data.getValue("aftslscomment").toString();
		String aftslsowner = data.getValue("aftslsowner") == null ? "" : data.getValue("aftslsowner").toString();

		ecnReport2HtmlModel.setAftslscomment(aftslscomment);
		ecnReport2HtmlModel.setAftslsowner(aftslsowner);

		String fnccomment = data.getValue("fnccomment") == null ? "" : data.getValue("fnccomment").toString();
		String fncowner = data.getValue("fncowner") == null ? "" : data.getValue("fncowner").toString();

		ecnReport2HtmlModel.setFnccomment(fnccomment);
		ecnReport2HtmlModel.setFncowner(fncowner);

		String slsmktcomment = data.getValue("slsmktcomment") == null ? "" : data.getValue("slsmktcomment").toString();
		String slsmktowner = data.getValue("slsmktowner") == null ? "" : data.getValue("slsmktowner").toString();

		ecnReport2HtmlModel.setSlsmktcomment(slsmktcomment);
		ecnReport2HtmlModel.setSlsmktowner(slsmktowner);

		String finalconclusion = data.getValue("finalconclusion") == null ? "" : data.getValue("finalconclusion").toString();
		String needinformcustomer = data.getValue("needinformcustomer") == null ? "" : data.getValue("needinformcustomer").toString();
		String needcustomerapprove = data.getValue("needcustomerapprove") == null ? "" : data.getValue("needcustomerapprove").toString();
		String noneedapprove = data.getValue("noneedapprove") == null ? "" : data.getValue("noneedapprove").toString();
		String bigchange = data.getValue("bigchange") == null ? "" : data.getValue("bigchange").toString();
		String littlechange = data.getValue("littlechange") == null ? "" : data.getValue("littlechange").toString();
		String normalchange = data.getValue("normalchange") == null ? "" : data.getValue("normalchange").toString();
		String reviewsite = GenericUtil.getObjectAttributeValue(ecn, "reviewPlace") == null ? "" : GenericUtil.getObjectAttributeValue(ecn, "reviewPlace").toString();
		log.debug("review place===" + reviewsite);
		log.debug("need to inform customer===" + needinformcustomer);
		// String
		// effdate=data.getValue("effdate")==null?"":data.getValue("effdate").toString();
		String expertcomment = data.getValue("expertcomment") == null ? "" : data.getValue("expertcomment").toString();
		String effdate = "";
		DateFormat format1 = new SimpleDateFormat("yyyy/MM/dd");
		Timestamp time = fixTime(ecn.getNeedDate());
		effdate = format1.format(time);
		String changeclassification = "";
		String needcustomerapproval = "";
		if (bigchange.endsWith("true")) {
			changeclassification = RoleConstant.bigchange;
		}
		if (littlechange.endsWith("true")) {
			changeclassification = RoleConstant.littlechange;
		}
		if (normalchange.endsWith("true")) {
			changeclassification = RoleConstant.normalchange;
		}
		if (needcustomerapprove.endsWith("true")) {
			needcustomerapprove = RoleConstant.need;
		} else {
			needcustomerapprove = RoleConstant.notneed;
		}
		if (needinformcustomer.endsWith("true")) {
			needinformcustomer = RoleConstant.need;
		} else {
			needinformcustomer = RoleConstant.notneed;
		}

		ecnReport2HtmlModel.setFinalconclusion(finalconclusion);
		ecnReport2HtmlModel.setNeedinformcustomer(needinformcustomer);
		ecnReport2HtmlModel.setChangeclassification(changeclassification);
		ecnReport2HtmlModel.setNeedcustomerapproval(needcustomerapprove);
		ecnReport2HtmlModel.setReviewsite(reviewsite);
		ecnReport2HtmlModel.setEffdate(effdate);
		ecnReport2HtmlModel.setExpertcommentString(expertcomment);

		return ecnReport2HtmlModel;
	}

	public static Timestamp fixTime(Timestamp timestamp) {
		timestamp = new Timestamp(timestamp.getTime() + 28800000);
		return timestamp;
	}

	public static ECNRoleCommentModel getECNReviewComment(ObjectReference self) throws WTException {
		ECNRoleCommentModel commentModel = new ECNRoleCommentModel();
		WfProcess process = (WfProcess) self.getObject();
		wt.fc.ReferenceFactory rf = new wt.fc.ReferenceFactory();
		String processOid = rf.getReferenceString(process);
		NmOid oid = NmOid.newNmOid(processOid);
		QueryResult qr = WorkflowCommands.getRouteStatus(oid);
		log.debug("getRouteStatus qr size===" + qr.size());
		Timestamp CCBcreatetimestamp = null;
		Timestamp CUSTOMcreatetimestamp = null;
		Timestamp MANAGERcreatetimestamp = null;
		Timestamp DIRECTcreatetimestamp = null;
		while (qr.hasMoreElements()) {
			Object obj = qr.nextElement();
			WorkItem workItem = null;
			if (obj instanceof WorkItem) {
				workItem = (WorkItem) obj;
			} else {
				continue;
			}
			WfActivity wfa = (WfActivity) workItem.getSource().getObject();//
			String workComments = "";
			String owner = "";
			String workVote = "";
			WfVotingEventAudit event = WfDataUtilitiesHelper.getMatchingEventAudit(workItem);
			if (event == null) {
				continue;
			}
			UserEventVector eventList = event.getEventList();
			for (int i = 0; eventList != null && i < eventList.size(); i++) {
				if (workVote.length() > 0) {
					workVote += ",";
				}
				workVote += eventList.get(i);
			}
			String activityName = wfa.getName();
			log.debug("activityName===" + activityName);
			Role role = workItem.getRole();
			log.debug("role=" + role.toString());
			if (activityName.equalsIgnoreCase(RoleConstant.cbb_review)) {
				if (compareEventtime(event, CCBcreatetimestamp)) {
					workComments = event.getUserComment();
					log.debug("owner==" + owner + "customer_review commet==" + workComments);
					commentModel.setCbb_comment(workVote + "<br>" + workComments);
				}
			}
			if (activityName.equalsIgnoreCase(RoleConstant.customer_review)) {
				if (compareEventtime(event, CUSTOMcreatetimestamp)) {
					workComments = event.getUserComment();
					owner = event.getUserRef().getFullName();
					log.debug("owner==" + owner + "customer_review commet==" + workComments);
					commentModel.setCustomer_comment(workVote + "<br>" + workComments);
					commentModel.setCustomer_owner(owner);
				}
			}
			if (activityName.equalsIgnoreCase(RoleConstant.design_RDS_Manager_review)) {
				if (compareEventtime(event, MANAGERcreatetimestamp)) {

					workComments = event.getUserComment();
					owner = event.getUserRef().getFullName();
					log.debug("role name=" + event.getRole().toString());
					log.debug("owner==" + owner + "design_RDS_Manager_review workcommet==" + workComments);
					if (event.getRole().toString().endsWith(RoleConstant.design_dept_manager)) {
						commentModel.setDept_manger_comment(workVote + "<br>" + workComments);
						commentModel.setDept_manager_owner(owner);
					} else {
						commentModel.setRsd_manger_comment(workVote + "<br>" + workComments);
						commentModel.setRsd_manager_owner(owner);
					}
				}
			}
			if (activityName.equalsIgnoreCase(RoleConstant.design_RDS_Director_review)) {
				if (compareEventtime(event, DIRECTcreatetimestamp)) {
					workComments = event.getUserComment();
					owner = event.getUserRef().getFullName();
					log.debug("role name=" + event.getRole().toString());
					log.debug("owner==" + owner + "design_RDS_Manager_review workcommet==" + workComments);
					if (event.getRole().toString().endsWith(RoleConstant.rsd_dept_director)) {
						commentModel.setRsd_director_comment(workVote + "<br>" + workComments);
						commentModel.setRsd_director_owner(owner);
					} else {
						commentModel.setDept_director_comment(workVote + "<br>" + workComments);
						commentModel.setDept_director_owner(owner);
					}
				}
			}
		}

		return commentModel;
	}

	public static boolean JspToHtmlFile(String filePath, String HtmlFile, WTChangeOrder2 ecn, ObjectReference self) throws WTException {
		String str = "";
		String changeplancontent = "";
		ArrayList<ECNChangePlanCommentModel> list = getECAInfo(ecn);
		log.debug("eca list==" + list.size());
		for (int i = 0; i < list.size(); i++) {
			ECNChangePlanCommentModel ecnChangePlanCommentModel = new ECNChangePlanCommentModel();
			ecnChangePlanCommentModel = (ECNChangePlanCommentModel) list.get(i);
			int num = i;
			num++;
			String changeplancontentInfo = "<TR>" + "<TD> " + num + ".</td>" + "<td colspan=4>" + ecnChangePlanCommentModel.getImplementtationplancontent() + "</td>	" + "<td> " + ecnChangePlanCommentModel.getOwner() + "</td>" + "<td colspan=2>" + ecnChangePlanCommentModel.getDepartment() + "</td>"
					+ "<td colspan=2>" + ecnChangePlanCommentModel.getDuedate() + "</td>" + "</TR>";

			changeplancontent = changeplancontent + changeplancontentInfo;
		}
		try {
			String tempStr = "";
			InputStreamReader is = new InputStreamReader(new FileInputStream(new File(filePath)), "UTF-8"); // or
																											// GBK
			BufferedReader br = new BufferedReader(is);
			while ((tempStr = br.readLine()) != null) {
				str = str + tempStr;
				if (tempStr.equalsIgnoreCase("		<td colspan=2>Due date<br>计划完成时间</td>	</TR>"))// can
				// not
				// deltet
				// the
				// space
				{
					str = str + changeplancontent;
				}
			}
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		ECNReport2HtmlModel ecnReport2HtmlModel = getECNReportInfo(ecn, self);
		try {
			str = str.replaceAll("ECNStage", ecnReport2HtmlModel.getStage());
			str = str.replaceAll("ecrNumber", ecnReport2HtmlModel.getEcrnumber());
			str = str.replaceAll("ecnNO", ecnReport2HtmlModel.getEcnNO());
			str = str.replaceAll("changeapplicanter", ecnReport2HtmlModel.getChangeapplicanter());
			str = str.replaceAll("applicantiondate", ecnReport2HtmlModel.getApplicantiondate());// replace
																								// attibutes
			str = str.replaceAll("reviewsite", ecnReport2HtmlModel.getReviewsite());
			str = str.replaceAll("changetype", ecnReport2HtmlModel.getChangetype());
			// str = str.replaceAll("organiger",
			// ecnReport2HtmlModel.getOrganiger());
			str = str.replaceAll("changeitem", ecnReport2HtmlModel.getChangeitem());
			str = str.replaceAll("changedescription", ecnReport2HtmlModel.getChangedescription());
			str = str.replaceAll("changereason", ecnReport2HtmlModel.getChangereason());
			str = str.replaceAll("expertcomment", ecnReport2HtmlModel.getExpertcommentString());
			str = str.replaceAll("changeclassification", ecnReport2HtmlModel.getChangeclassification());

			str = str.replaceAll("needinformcustomer", ecnReport2HtmlModel.getNeedinformcustomer());
			str = str.replaceAll("needcustomerapproval", ecnReport2HtmlModel.getNeedcustomerapproval());
			str = str.replaceAll("effdate", ecnReport2HtmlModel.getEffdate());

			// set role comment
			str = str.replaceAll("pmcomment", ecnReport2HtmlModel.getPmcomment());
			str = str.replaceAll("pmowner", ecnReport2HtmlModel.getPmowner());

			str = str.replaceAll("pqmcomment", ecnReport2HtmlModel.getPqmcomment());
			str = str.replaceAll("pqmowner", ecnReport2HtmlModel.getPqmowner());

			str = str.replaceAll("rsdcomment", ecnReport2HtmlModel.getRsdcomment());
			str = str.replaceAll("rsdowner", ecnReport2HtmlModel.getRsdowner());

			str = str.replaceAll("sqecomment", ecnReport2HtmlModel.getSqecomment());
			str = str.replaceAll("sqeowner", ecnReport2HtmlModel.getSqeowner());

			str = str.replaceAll("pdcomment", ecnReport2HtmlModel.getPdcomment());
			str = str.replaceAll("pdowner", ecnReport2HtmlModel.getPdowner());

			str = str.replaceAll("rdmdcomment", ecnReport2HtmlModel.getRdmdcomment());
			str = str.replaceAll("rdmdowner", ecnReport2HtmlModel.getRdmdowner());

			str = str.replaceAll("rdsyscomment", ecnReport2HtmlModel.getRdsyscomment());
			str = str.replaceAll("rdsysowner", ecnReport2HtmlModel.getRdsysowner());

			str = str.replaceAll("mpecomment", ecnReport2HtmlModel.getMpecomment());
			str = str.replaceAll("mpeowner", ecnReport2HtmlModel.getMpeowner());

			str = str.replaceAll("mdecomment", ecnReport2HtmlModel.getMdecomment());
			str = str.replaceAll("mdeowner", ecnReport2HtmlModel.getMdeowner());

			str = str.replaceAll("iecomment", ecnReport2HtmlModel.getIecomment());
			str = str.replaceAll("ieowner", ecnReport2HtmlModel.getIeowner());

			str = str.replaceAll("mfgcomment", ecnReport2HtmlModel.getMfgcomment());
			str = str.replaceAll("mfgowner", ecnReport2HtmlModel.getMfgowner());

			str = str.replaceAll("pcmccomment", ecnReport2HtmlModel.getPcmccomment());
			str = str.replaceAll("pcmcowner", ecnReport2HtmlModel.getPcmcowner());

			str = str.replaceAll("srccomment", ecnReport2HtmlModel.getSrccomment());
			str = str.replaceAll("srcowner", ecnReport2HtmlModel.getSrcowner());

			str = str.replaceAll("aftslscomment", ecnReport2HtmlModel.getAftslscomment());
			str = str.replaceAll("aftslsowner", ecnReport2HtmlModel.getAftslsowner());

			str = str.replaceAll("fnccomment", ecnReport2HtmlModel.getFnccomment());
			str = str.replaceAll("fncowner", ecnReport2HtmlModel.getFncowner());

			str = str.replaceAll("slsmktcomment", ecnReport2HtmlModel.getSlsmktcomment());
			str = str.replaceAll("slsmktowner", ecnReport2HtmlModel.getSlsmktowner());

			// get workitem comments and owner
			ECNRoleCommentModel commentModel = getECNReviewComment(self);

			str = str.replaceAll("finalconclusion", commentModel.getCbb_comment());
			str = str.replaceAll("se_comment", commentModel.getSe_comment());
			str = str.replaceAll("se_owner", commentModel.getSe_owner());

			str = str.replaceAll("dept_manager_comment", commentModel.getDept_manger_comment());
			str = str.replaceAll("dept_manager_owner", commentModel.getDept_manager_owner());

			str = str.replaceAll("dept_director_comment", commentModel.getDept_director_comment());
			str = str.replaceAll("dept_director_owner", commentModel.getDept_director_owner());

			str = str.replaceAll("rsd_manager_comment", commentModel.getRsd_manger_comment());
			str = str.replaceAll("rsd_manager_owner", commentModel.getRsd_manager_owner());

			str = str.replaceAll("rsd_director_comment", commentModel.getRsd_director_comment());
			str = str.replaceAll("rsd_director_owner", commentModel.getRsd_director_owner());

			str = str.replaceAll("customer_comment", commentModel.getCustomer_comment());
			str = str.replaceAll("customer_owner", commentModel.getCustomer_owner());

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

	public static Boolean compareEventtime(WfVotingEventAudit event, Timestamp createtimestamp) {
		// compare activity time
		Boolean isset = false;
		Timestamp timestamp = event.getModifyTimestamp();
		log.debug("timestamp=========" + timestamp);
		log.debug("createtimestamp==========" + createtimestamp);
		if (createtimestamp != null) {
			if (timestamp.after(createtimestamp)) {
				createtimestamp = timestamp;
				isset = true;
			} else {
				isset = false;
			}
		} else {
			createtimestamp = timestamp;
			isset = true;
		}
		return isset;
	}
	
}
