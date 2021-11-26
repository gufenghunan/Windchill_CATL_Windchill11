/*** Eclipse Class Decompiler plugin, copyright (c) 2016 Chen Chao (cnfree2000@hotmail.com) ***/
package com.catl.doc.soft.builders;

import com.catl.doc.maturityUpReport.MaturityUpReportHelper;
import com.catl.doc.maturityUpReport.NFAEMaturityReportLinkBean;
import com.catl.doc.maturityUpReport.NFAEMaturityUp3DocPartLink;
import com.ptc.jca.mvc.components.JcaColumnConfig;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.MultiComponentConfig;
import com.ptc.mvc.components.TableConfig;
import com.ptc.mvc.components.TypeBased;
import com.ptc.mvc.util.ClientMessageSource;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.attachments.server.AttachmentsHelper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;
import wt.content.ContentRoleType;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.fc.QueryResult;
import wt.inf.container.WTContainerRef;
import wt.log4j.LogR;
import wt.org.WTUser;
import wt.preference.PreferenceHelper;
import wt.session.SessionHelper;
import wt.util.WTException;

@ComponentBuilder("com.catl.doc.soft.builders.SoftAttachmentsTableBuilder")
public class SoftAttachmentsTableBuilder extends AbstractComponentBuilder {
	private static final String ATTACHMENT_RESOURCE = "com.ptc.windchill.enterprise.attachments.attachmentsResource";
	private final ClientMessageSource messageSource = this
			.getMessageSource(ATTACHMENT_RESOURCE);
	
	@Override
	public Object buildComponentData(ComponentConfig arg0, ComponentParams params) throws Exception {
		ContentRoleType arg2 = ContentRoleType.toContentRoleType("SOFTPACKAGE_ATTACHMENT");
		QueryResult qr = null;
		Object obj = params.getContextObject();
		if(obj instanceof WTDocument){
			WTDocument doc = (WTDocument)obj;
			qr = AttachmentsHelper.service.getAttachments(doc, arg2);
			//System.out.println("SOFTPACKAGE_ATTACHMENT Size\t" + qr.size());
		}		

		return qr;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams arg0) throws WTException {
		arg0.setAttribute("showContextInfo", "true");
		
		NmCommandBean arg4 = (NmCommandBean) arg0.getAttribute("commandBean");
		String arg5 = "";
		if (arg4 != null) {
			arg5 = arg4.getTextParameter("ua");
		}
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig table = factory.newTableConfig();
	
		ContentRoleType arg8 = ContentRoleType.SECONDARY;

		List arg9 = this.getColumns(arg4, arg8);
		table.setLabel("软件包");
		table.setId("SoftAttachmentsTable");
		table.setHelpContext("SecondaryAttachmentsTableHelp");
		if (!"DTI".equals(arg5)) {
			table.setActionModel("attachments soft readonly table toolbar actions");
			table.setSelectable(true);
		}

		table.setType("wt.content.ContentItem");
		table.setConfigurable(false);
		Boolean arg11 = (Boolean) PreferenceHelper.service
				.getValue("/com/ptc/windchill/enterprise/attachments/optionalAttributes/sortNumber", "WINDCHILL");
		if (arg11.booleanValue() && !arg8.equals(ContentRoleType.PRIMARY)) {
			table.addComponent(factory.newColumnConfig("lineNumber", true));
		}

		table.addComponents(arg9);
				

		return table;
	}

	public List<ColumnConfig> getColumns(NmCommandBean arg0, ContentRoleType arg1) throws WTException {
		ComponentConfigFactory arg2 = this.getComponentConfigFactory();
		Boolean arg3 = (Boolean) PreferenceHelper.service
				.getValue("/com/ptc/windchill/enterprise/attachments/optionalAttributes/comments", "WINDCHILL");
		Boolean arg4 = (Boolean) PreferenceHelper.service
				.getValue("/com/ptc/windchill/enterprise/attachments/optionalAttributes/distributable", "WINDCHILL");
		Boolean arg5 = (Boolean) PreferenceHelper.service
				.getValue("/com/ptc/windchill/enterprise/attachments/optionalAttributes/authoredBy", "WINDCHILL");
		Boolean arg6 = (Boolean) PreferenceHelper.service
				.getValue("/com/ptc/windchill/enterprise/attachments/optionalAttributes/lastAuthored", "WINDCHILL");
		Boolean arg7 = (Boolean) PreferenceHelper.service
				.getValue("/com/ptc/windchill/enterprise/attachments/optionalAttributes/fileVersion", "WINDCHILL");
		Boolean arg8 = (Boolean) PreferenceHelper.service
				.getValue("/com/ptc/windchill/enterprise/attachments/optionalAttributes/toolName", "WINDCHILL");
		Boolean arg9 = (Boolean) PreferenceHelper.service
				.getValue("/com/ptc/windchill/enterprise/attachments/optionalAttributes/toolVersion", "WINDCHILL");
		WTContainerRef arg10 = arg0.getContainerRef();
		WTUser arg11 = (WTUser) SessionHelper.manager.getPrincipal();
		Boolean arg12 = (Boolean) PreferenceHelper.service.getValue(arg10,
				"/com/ptc/windchill/enterprise/attachments/optionalAttributes/primaryContentDescription", "WINDCHILL",
				arg11);
		ArrayList arg13 = new ArrayList();
		arg13.add(arg2.newColumnConfig("attachmentsName", this.messageSource.getMessage("ATTACHMENT_NAME"), true));
		String arg14 = "";
		if (arg0 != null) {
			arg14 = arg0.getTextParameter("ua");
		}

		if (!"DTI".equals(arg14)) {
			arg13.add(arg2.newColumnConfig("infoPageActionAttachment", false));
			arg13.add(arg2.newColumnConfig("formatIcon", false));
		}

		arg13.add(arg2.newColumnConfig("formatName", this.messageSource.getMessage("FORMAT"), true));
		ColumnConfig arg15;
		if (!ContentRoleType.PRIMARY.equals(arg1)) {
			arg15 = arg2.newColumnConfig("description", this.messageSource.getMessage("ATTACHMENT_DESCRIPTION"), true);
			((JcaColumnConfig) arg15).setVariableHeight(true);
			arg13.add(arg15);
		} else if (arg12.booleanValue()) {
			arg15 = arg2.newColumnConfig("description", this.messageSource.getMessage("ATTACHMENT_DESCRIPTION"), true);
			((JcaColumnConfig) arg15).setVariableHeight(true);
			arg13.add(arg15);
		}

		arg13.add(arg2.newColumnConfig("thePersistInfo.modifyStamp", true));
		arg15 = arg2.newColumnConfig("modifier", true);
		arg15.setTargetObject("modifiedBy");
		arg15.setNeed("modifiedBy");
		arg13.add(arg15);
		if (arg3.booleanValue()) {
			arg13.add(arg2.newColumnConfig("comments", this.messageSource.getMessage("ATTACHMENT_COMMENTS"), true));
		}

		if (arg4.booleanValue()) {
			arg13.add(arg2.newColumnConfig("distributable",
					this.messageSource.getMessage("ATTACHMENT_EXTERNALDISTRIBUTION"), true));
		}

		if (arg5.booleanValue()) {
			arg13.add(arg2.newColumnConfig("authoredBy", this.messageSource.getMessage("ATTACHMENT_AUTHOREDBY"), true));
		}

		if (arg6.booleanValue()) {
			arg13.add(arg2.newColumnConfig("lastAuthored", this.messageSource.getMessage("ATTACHMENT_LASTAUTHORED"),
					true));
		}

		if (arg7.booleanValue()) {
			arg13.add(
					arg2.newColumnConfig("fileVersion", this.messageSource.getMessage("ATTACHMENT_FILEVERSION"), true));
		}

		if (arg8.booleanValue()) {
			arg13.add(arg2.newColumnConfig("toolName", this.messageSource.getMessage("ATTACHMENT_TOOLNAME"), true));
		}

		if (arg9.booleanValue()) {
			arg13.add(
					arg2.newColumnConfig("toolVersion", this.messageSource.getMessage("ATTACHMENT_TOOLVERSION"), true));
		}

		return arg13;
	}
}