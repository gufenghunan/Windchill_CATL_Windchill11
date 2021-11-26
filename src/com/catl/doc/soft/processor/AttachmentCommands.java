/*** Eclipse Class Decompiler plugin, copyright (c) 2016 Chen Chao (cnfree2000@hotmail.com) ***/
package com.catl.doc.soft.processor;

import com.ptc.core.components.forms.DynamicRefreshInfo;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.netmarkets.model.NmSimpleOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.attachments.server.AttachmentsHelper;
import java.util.HashMap;
import org.apache.log4j.Logger;
import wt.content.ApplicationData;
import wt.content.ExternalStoredData;
import wt.content.URLData;
import wt.doc.WTDocument;
import wt.facade.scm.ScmApplicationData;
import wt.log4j.LogR;
import wt.preference.PreferenceHelper;
import wt.util.WTException;

public class AttachmentCommands {
	private static final Logger log;
	private static final String RESOURCE = "com.ptc.windchill.enterprise.attachments.attachmentsResource";
	private static final String DELIM = ";;;qqq";

	static {
		try {
			log = LogR.getLogger(AttachmentCommands.class.getName());
		} catch (Exception arg0) {
			throw new ExceptionInInitializerError(arg0);
		}
	}

	public static FormResult addFileAttachment(NmCommandBean arg) throws WTException {
		FormResult arg0 = new FormResult();
		arg0.setNextAction(FormResultAction.NONE);
		String[] arg1 = (String[]) arg.getParameterMap().get("tableId");
		String arg2 = arg.getTextParameter("newFiles");
		System.out.println("1111tableID:\t" + arg1);
		System.out.println("1111NewFiles:\t" + arg2);
		if (arg2 != null && arg2.length() > 0) {
			log.trace("add pre-filled file rows for " + arg2);
			String[] arg3 = arg2.split(";;;qqq");
			int arg4 = 0;
			String[] arg5 = arg3;
			int arg6 = arg3.length;

			for (int arg7 = 0; arg7 < arg6; ++arg7) {
				String arg8 = arg5[arg7];
				String arg9 = ApplicationData.class.getName();
				if (arg1 != null && "table__multiDocWizAttributesTableDescriptor_TABLE".equals(arg1[0])) {
					log.debug("tableId[0] = " + arg1[0]);
					String arg12 = "newRowObj";
					arg9 = arg12.concat("." + arg9);
				}

				NmSimpleOid arg121 = AttachmentsHelper.createNmSimpleOid(arg9);
				arg121.setInternalName(arg121.getInternalName() + arg4);
				log.trace("oid = " + arg121.toString());
				HashMap arg11 = arg121.getAdditionalInfo();
				if (arg11 == null) {
					arg11 = new HashMap();
				}

				arg11.put(arg121.toString() + "location", arg8);
				arg11.put(arg121.toString() + "name", getFileName(arg8));
				arg121.setAdditionalInfo(arg11);
				if (arg121 != null) {
					arg0.addDynamicRefreshInfo(new DynamicRefreshInfo(arg121, arg121, "A"));
				}

				++arg4;
			}
		} else {
			arg0 = addAttachment(arg, ApplicationData.class.getName());
			log.debug("Created a NmSimpleOid to represent a non-persisted ApplicationData object");
		}

		return arg0;
	}

	public static String getFileName(String arg) {
		String arg0;
		if (arg.indexOf("/") > -1) {
			arg0 = "/";
		} else {
			arg0 = "\\";
		}

		return getFileName(arg, arg0);
	}

	public static String getFileName(String arg, String arg0) {
		log.trace("filepath = " + arg + "\n" + "fileSep = " + arg0);
		if (arg != null && arg.length() > 0) {
			int arg1 = arg.lastIndexOf(arg0);
			return arg.substring(arg1 + 1, arg.length());
		} else {
			return arg;
		}
	}

	public static FormResult addUrlAttachment(NmCommandBean arg) throws WTException {
		FormResult arg0;
		try {
			arg0 = addAttachment(arg, URLData.class.getName());
		} finally {
			log.debug("Created a NmSimpleOid to represent a non-persisted URLData object");
		}

		return arg0;
	}

	public static FormResult addEsaAttachment(NmCommandBean arg) throws WTException {
		log.debug("Created a NmSimpleOid to represent a non-persisted ExternalStoredData object");
		return addAttachment(arg, ExternalStoredData.class.getName());
	}

	public static FormResult addScmAttachment(NmCommandBean arg) throws WTException {
		log.debug("Created a NmSimpleOid to represent a non-persisted ExternalStoredData object");
		return addAttachment(arg, ScmApplicationData.class.getName());
	}

	public static FormResult addNoAttachment(NmCommandBean arg) throws WTException {
		log.debug("Created a NmSimpleOid to represent a non-persisted ExternalStoredData object");
		String arg0 = (String) PreferenceHelper.service
				.getValue("/com/ptc/windchill/enterprise/attachments/primaryContentEnforcement", "WINDCHILL");
		if ("REQUIRED".equals(arg0)) {
			throw new WTException("com.ptc.windchill.enterprise.attachments.attachmentsResource", "ENFORCE_CONTENT_MSG",
					(Object[]) null);
		} else {
			return addAttachment(arg, WTDocument.class.getName());
		}
	}

	protected static FormResult addAttachment(NmCommandBean arg, String arg0) throws WTException {
		FormResult arg1 = new FormResult();
		arg1.setNextAction(FormResultAction.NONE);
		if (arg0 != null) {
			String[] arg2 = (String[]) arg.getParameterMap().get("tableId");
			if (arg2 != null) {
				if (arg2[0].equals("table__multiDocWizAttributesTableDescriptor_TABLE")) {
					String arg4 = "newRowObj";
					arg0 = arg4.concat("." + arg0);
				}

				NmSimpleOid arg41 = AttachmentsHelper.createNmSimpleOid(arg0);
				if (arg41 != null) {
					arg1.addDynamicRefreshInfo(new DynamicRefreshInfo(arg41, arg41, "A"));
				}
			}
		}

		return arg1;
	}
}