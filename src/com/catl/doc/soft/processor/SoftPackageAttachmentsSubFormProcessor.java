package com.catl.doc.soft.processor;

import com.catl.ecad.utils.CommonUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.model.NmSimpleOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.attachments.forms.AbstractAttachmentsSubFormProcessor;
import com.ptc.windchill.enterprise.attachments.server.AttachmentsHelper;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import wt.content.ApplicationData;
import wt.content.ContentException;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.content.ExternalStoredData;
import wt.content.URLData;
import wt.facade.scm.ScmApplicationData;
import wt.facade.scm.gui.ScmGuiHelper;
import wt.fc.ObjectReference;
import wt.fv.uploadtocache.CachedContentDescriptor;
import wt.log4j.LogR;
import wt.util.InstalledProperties;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.wip.Workable;

public class SoftPackageAttachmentsSubFormProcessor extends AbstractAttachmentsSubFormProcessor {
	private static final Logger log;
	private static final String ATTACHMENTS_TABLE_ID = "attachments.list.editable";
	private static final String WP_ATTACHMENTS_TABLE_ID = "wp.attachments.list.editable";
	private static final String RESOURCE = "wt.content.contentResource";
	static {
		try {
			log = LogR.getLogger(SoftPackageAttachmentsSubFormProcessor.class.getName());
		} catch (Exception arg0) {
			throw new ExceptionInInitializerError(arg0);
		}
	}
	public FormResult preProcess(NmCommandBean arg0, List<ObjectBean> arg1) throws WTException {
		log.trace("\n START preProcess( NmCommandBean, List<ObjectBean> )");
		FormResult arg2 = new FormResult();
		List arg3 = null;

		ObjectBean arg5;
		for (Iterator arg4 = arg1.iterator(); arg4.hasNext(); this.getObjectBeanInfo(arg5).put("locale",
				arg0.getLocale())) {
			arg5 = (ObjectBean) arg4.next();
			HashMap arg6 = getFormData(arg5);
			String arg7 = arg5.getTextParameter("uploadFeedback");
			log.debug("    Attachments Table Form Data: " + arg6);
			List arg8 = getContentItemOids(arg6);
			log.debug("    contentItemOids: " + arg8);
			boolean arg9 = this.getIsCreateWizard(arg5);
			ArrayList arg10 = new ArrayList();
			List arg11 = arg5.getAddedItemsByName(WP_ATTACHMENTS_TABLE_ID);
			log.debug("        wp_newAttachmentOids  = " + arg11);
			String arg12 = arg0.getTextParameter("wizardDefaultAttachment");
			List arg13;
			if (arg12 != null && arg12 != "" && arg12 != " ") {
				arg13 = this.getDefaultContentItemOids(arg6);
				if (!arg13.isEmpty()) {
					arg10.addAll(arg13);
				}
			}

			arg13 = arg5.getAddedItemsByName(ATTACHMENTS_TABLE_ID);
			Iterator arg14 = arg10.iterator();

			while (arg14.hasNext()) {
				Object arg15 = arg14.next();
				if (arg13.contains(arg15)) {
					arg13.remove(arg15);
				}
			}

			arg10.addAll(arg13);
			log.debug("        newAttachmentOids  = " + arg10);
			if (arg10 != null && arg10.size() > 0) {
				arg3 = this.getNewAttachments(arg10, arg6, arg5, ContentRoleType.toContentRoleType("SOFTPACKAGE_ATTACHMENT"));
				log.debug("        newAttachments = " + arg3);
				this.getObjectBeanInfo(arg5).put("newAttachments", arg3);
			}

			List arg18;
			if (arg11 != null && arg11.size() > 0) {
				arg18 = this.getNewAttachments(arg11, arg6, arg5, ContentRoleType.WP_EXP_SECONDARY);
				log.debug("        wp_newAttachmentOids = " + arg11);
				if (arg3 != null && arg3.size() > 0) {
					arg3.addAll(arg18);
				} else {
					arg3 = arg18;
				}

				this.getObjectBeanInfo(arg5).put("newAttachments", arg3);
			}

			if (arg3 != null) {
				arg14 = arg3.iterator();

				while (arg14.hasNext()) {
					ContentItem arg19 = (ContentItem) arg14.next();
					if (arg19 instanceof ApplicationData) {
						Map arg16 = (Map) this.getObjectBeanInfo(arg5).get("cachedContentDescriptors");
						CachedContentDescriptor arg17 = null;
						if (arg16 != null) {
							arg17 = (CachedContentDescriptor) arg16.get(arg19);
						}

						if (arg17 == null) {
							log.debug("CachedContentDescriptor is null.");
							log.debug("UploadFeedback is :" + arg7);
							if (arg7 != null) {
								checkUploadFeedback(arg0.getLocale(), arg2);
							}
							break;
						}

						if (!validEmptyFile((ApplicationData) arg19, arg17, arg0.getLocale(), arg2)) {
							break;
						}
					}
				}
			}

			List arg20;
			List arg21;
			if (arg9) {
				log.debug("IS a create wizard");
				arg18 = this.populatePropagatedAttachments(arg5);
				arg20 = arg5.getRemovedItemsByName(ATTACHMENTS_TABLE_ID);
				arg21 = arg5.getAddedItemsByName(WP_ATTACHMENTS_TABLE_ID);
				arg20.addAll(arg21);
				log.debug("        deletedAttachmentOids = " + arg20);
				log.debug("        wp_deletedAttachmentOids = " + arg21);
				this.checkForDeletedPropagatedItems(arg18, arg20);
				log.debug("        propagatedAttachments = " + arg18);
				this.getObjectBeanInfo(arg5).put("propagatedAttachments", arg18);
			} else {
				log.debug("NOT a create wizard");
				arg18 = arg5.getRemovedItemsByName(ATTACHMENTS_TABLE_ID);
				arg20 = arg5.getRemovedItemsByName(WP_ATTACHMENTS_TABLE_ID);
				arg18.addAll(arg20);
				log.debug("        deletedAttachmentOids = " + arg18);
				if (arg18 != null && arg18.size() > 0) {
					this.getObjectBeanInfo(arg5).put("deletedAttachments", arg18);
				}

				arg21 = getUpdatedAttachmentOids(arg8, arg10, arg18);
				log.debug("        updatedAttachmentOids = " + arg21);
				if (arg21 != null && arg21.size() > 0) {
					List arg22 = this.getUpdatedAttachments(arg21, arg6, arg5);
					log.debug("        updatedAttachments = " + arg22);
					this.getObjectBeanInfo(arg5).put("updatedAttachments", arg22);
				}
			}
		}

		log.trace("\n END   preProcess( NmCommandBean, List<ObjectBean> )");
		return arg2;
	}

	private List<NmOid> getDefaultContentItemOids(HashMap arg0) {
		ArrayList arg1 = new ArrayList();
		HashMap arg2 = new HashMap();
		if (arg0 != null) {
			Set arg3 = arg0.entrySet();
			Iterator arg4 = arg3.iterator();

			while (arg4.hasNext()) {
				Entry arg5 = (Entry) arg4.next();
				String arg6 = (String) arg5.getKey();
				Pattern arg7 = Pattern.compile("_");
				if (arg6 != null) {
					int arg8 = this.countMatches(arg7, arg6);
					if (arg8 == 1) {
						int arg9 = arg6.indexOf(95);
						if (arg9 >= 0) {
							arg6 = arg6.substring(arg9 + 1, arg6.length());
						}

						if (arg6 != null && !arg2.containsKey(arg6) && arg6.contains(URLData.class.getName())) {
							NmOid arg10 = getNmOid(arg6);
							if (arg10 instanceof NmSimpleOid) {
								arg1.add(arg10);
								arg2.put(arg6, arg10);
							}
						}
					}
				}
			}
		}

		return arg1;
	}

	private int countMatches(Pattern arg0, String arg1) {
		int arg2 = 0;

		for (Matcher arg3 = arg0.matcher(arg1); arg3.find(); ++arg2) {
			;
		}

		return arg2;
	}

	private void checkForDeletedPropagatedItems(List<ContentItem> arg0, List<NmOid> arg1) throws WTException {
		if (arg1 != null && arg1.size() > 0) {
			ArrayList arg2 = new ArrayList();
			ArrayList arg3 = new ArrayList();
			boolean arg4 = false;
			Iterator arg5 = arg1.iterator();

			while (arg5.hasNext()) {
				NmOid arg6 = (NmOid) arg5.next();
				log.debug("oid : " + arg6 + "class : " + arg6.getClass());
				String arg7 = null;

				try {
					arg7 = arg6.getOid().getStringValue();
				} catch (Exception arg12) {
					arg7 = arg6.toString();
				}

				log.debug("DeletedOid = " + arg7);
				arg4 = false;
				if (arg0 != null && arg0.size() > 0) {
					Iterator arg8 = arg0.iterator();

					while (arg8.hasNext()) {
						ContentItem arg9 = (ContentItem) arg8.next();
						ObjectReference arg10 = ObjectReference.newObjectReference(arg9);
						String arg11 = arg10.getObjectId().getStringValue();
						log.debug("StringValue=" + arg11);
						if (arg7.equals(arg11)) {
							arg4 = true;
							log.debug("item is a deleted propagated item");
						} else {
							arg3.add(arg9);
						}
					}
				}

				if (!arg4) {
					arg2.add(arg6);
				}
			}
		}

	}

	public FormResult doOperation(NmCommandBean arg0, List<ObjectBean> arg1) throws WTException {
		log.trace("doOperation( NmCommandBean, List<ObjectBean> )");
		FormResult arg2 = new FormResult();
		Iterator arg3 = arg1.iterator();
		if (arg3.hasNext()) {
			ObjectBean arg4 = (ObjectBean) arg3.next();
			FormResult arg5 = new FormResult();
			boolean arg6 = this.getIsCreateWizard(arg4);
			log.debug("doOperation - isCreateWizard = " + arg6);
			if (arg6) {
				arg5.setStatus(FormProcessingStatus.SUCCESS);
			} else {
				arg5 = this.persistSecondaryAttachments(arg4);
			}

			return arg5;
		} else {
			return arg2;
		}
	}

	public FormResult postProcess(NmCommandBean arg0, List<ObjectBean> arg1) throws WTException {
		log.trace("postProcess( NmCommandBean, List<ObjectBean> )");
		FormResult arg2 = new FormResult();
		Iterator arg3 = arg1.iterator();
		if (arg3.hasNext()) {
			ObjectBean arg4 = (ObjectBean) arg3.next();
			FormResult arg5 = new FormResult();
			boolean arg6 = this.getIsCreateWizard(arg4);
			log.debug("postProcess - isCreateWizard = " + arg6);
			if (arg6) {
				arg5 = this.persistSecondaryAttachments(arg4);
			} else {
				arg5.setStatus(FormProcessingStatus.SUCCESS);
			}

			return arg5;
		} else {
			return arg2;
		}
	}

	public FormResult persistSecondaryAttachments(ObjectBean arg0) throws WTException {
		log.trace("persistSecondaryAttachments( ObjectBean )");
		HashMap arg1 = null;
		Object arg2 = this.getObjectBeanInfo(arg0).get("cachedContentDescriptors");
		if (arg2 != null && arg2 instanceof HashMap) {
			arg1 = (HashMap) arg2;
		}

		ContentHolder arg3 = this.getContentHolder(arg0);
		
		log.debug("contentHolder: " + arg3);
		FormResult arg4 = new FormResult();
		log.debug("Modifying the attachments on the Content Holder " + arg3);
		if (arg3 != null) {
			List arg5 = null;
			List arg6 = null;
			List arg7 = null;
			List arg8 = null;
			Object arg9 = this.getObjectBeanInfo(arg0).get("newAttachments");
			if (arg9 != null && arg9 instanceof List) {
				arg5 = (List) arg9;
				log.debug("newAttachments = " + arg5);
			}

			arg9 = null;
			arg9 = this.getObjectBeanInfo(arg0).get("updatedAttachments");
			if (arg9 != null && arg9 instanceof List) {
				arg6 = (List) arg9;
				log.debug("updatedAttachments = " + arg6);
			}

			arg9 = null;
			arg9 = this.getObjectBeanInfo(arg0).get("propagatedAttachments");
			if (arg9 != null && arg9 instanceof List) {
				arg7 = (List) arg9;
				log.debug("propagatedAttachments = " + arg7);
			}

			arg9 = null;
			arg9 = this.getObjectBeanInfo(arg0).get("deletedAttachments");
			if (arg9 != null && arg9 instanceof List) {
				arg8 = (List) arg9;
				log.debug("deletedAttachments = " + arg8);
			}

			if (arg8 != null && arg8.size() > 0) {
				try {
					AttachmentsHelper.service.removeAttachments(arg3, arg8);
					log.debug("    Deleted Attachments  " + arg8 + " successfully.");
				} catch (WTPropertyVetoException arg14) {
					log.error(this.getClass().getName() + ".persistSecondaryAttachments() delete error", arg14);
					return handlePropertyVetoException(arg14, (Locale) this.getObjectBeanInfo(arg0).get("locale"),
							arg4);
				}
			}

			if (arg7 != null && arg7.size() > 0) {
				try {
					log.debug("propagatedAttachments size=" + arg7.size());
					ContentServerHelper.service.copyContentItem(arg3, arg7);
					log.debug("    Added Propagated Attachments " + arg7 + " successfully.");
				} catch (Exception arg13) {
					arg4.setStatus(FormProcessingStatus.NON_FATAL_ERROR);
					log.error(
							"    Was not able to add one or more of the propagated attachments for the object: " + arg3,
							arg13);
				}
			}

			if (arg5 != null && arg5.size() > 0) {
				try {
					AttachmentsHelper.service.persistAttachments(arg3, arg5, arg1);
					log.debug("    Added new Attachments " + arg5 + " successfully.");
				} catch (PropertyVetoException arg12) {
					log.error(this.getClass().getName() + ".persistSecondaryAttachments() add error", arg12);
					return handlePropertyVetoException(arg12, (Locale) this.getObjectBeanInfo(arg0).get("locale"),
							arg4);
				}
			}

			if (arg6 != null && arg6.size() > 0) {
				try {
					AttachmentsHelper.service.persistAttachments(arg3, arg6, arg1);
					log.debug("    Updated Attachments " + arg6 + " successfully.");
				} catch (PropertyVetoException arg11) {
					log.error(this.getClass().getName() + ".persistSecondaryAttachments() update error", arg11);
					return handlePropertyVetoException(arg11, (Locale) this.getObjectBeanInfo(arg0).get("locale"),
							arg4);
				}
			}
		}

		return arg4;
	}

	private List<ContentItem> getNewAttachments(List<NmOid> arg0, HashMap arg1, ObjectBean arg2, ContentRoleType arg3)
			throws ContentException {
		log.trace("getNewAttachments( List<NmOid>, HashMap )");
		HashMap arg4 = null;
		Object arg5 = this.getObjectBeanInfo(arg2).get("cachedContentDescriptors");
		if (arg5 != null && arg5 instanceof HashMap) {
			arg4 = (HashMap) arg5;
		} else {
			arg4 = new HashMap();
		}

		ArrayList arg6 = new ArrayList(arg0.size());
		if (arg0 != null) {
			for (int arg7 = 0; arg7 < arg0.size(); ++arg7) {
				NmOid arg8 = (NmOid) arg0.get(arg7);
				log.debug("ND.DEBUG => Got a attachmentOid of: " + arg8);

				try {
					Object[] arg9 = getContentItemData(
							arg8 instanceof NmSimpleOid ? arg8.toString() : arg8.getOid().toString(), arg1, arg2);
					HashMap arg10 = (HashMap) arg9[0];
					boolean arg11 = ((Boolean) arg9[1]).booleanValue();
					ContentItem arg12 = setAttributeValues(arg8, arg10);
					if (!this.isMandatoryDataAvailable(arg12)) {
						throw new ContentException(RESOURCE, "73", (Object[]) null);
					}

					if (arg12 != null) {
						arg12.setRole(arg3);
						if (arg12 instanceof ApplicationData
								&& (!InstalledProperties.isInstalled("Windchill.SoftwareConfigMgmtIntegration")
										|| !(arg12 instanceof ScmApplicationData))) {
							CachedContentDescriptor arg13 = parseCachedContentDescriptorString((ApplicationData) arg12,
									(String) arg10.get("cachedContentDescriptor"));
							if (arg13 != null) {
								arg4.put(arg12, arg13);
							}
						}

						arg6.add(arg12);
					}
				} catch (WTPropertyVetoException arg14) {
					arg14.printStackTrace();
					log.error("    Could not create content item.", arg14);
				} catch (ContentException arg15) {
					arg15.printStackTrace();
					log.error("    Could not create content item.", arg15);
					throw arg15;
				} catch (WTException arg16) {
					arg16.printStackTrace();
					log.error("    Could not create content item.", arg16);
				}
			}
		}

		this.getObjectBeanInfo(arg2).put("cachedContentDescriptors", arg4);
		return arg6;
	}

	private List<ContentItem> getUpdatedAttachments(List<NmOid> arg0, HashMap arg1, ObjectBean arg2)
			throws WTException {
		log.trace("getUpdatedAttachments( List<NmOid>, HashMap )");
		Map arg3 = this.getCachedContentDescriptors(arg2);
		ArrayList arg4 = new ArrayList(arg0.size());
		if (arg0 != null) {
			for (int arg5 = 0; arg5 < arg0.size(); ++arg5) {
				Object[] arg6 = getContentItemData(((NmOid) arg0.get(arg5)).toString(), arg1, arg2);
				HashMap arg7 = (HashMap) arg6[0];
				boolean arg8 = ((Boolean) arg6[1]).booleanValue();
				if (arg8) {
					ContentItem arg9;
					try {
						arg9 = setAttributeValues((NmOid) arg0.get(arg5), arg7);
						if (!this.isMandatoryDataAvailable(arg9)) {
							throw new ContentException(RESOURCE, "73", (Object[]) null);
						}
					} catch (WTPropertyVetoException arg11) {
						throw new WTException(arg11);
					} catch (ContentException arg12) {
						throw arg12;
					}

					if (arg9 != null) {
						if (arg9 instanceof ApplicationData
								&& (!InstalledProperties.isInstalled("Windchill.SoftwareConfigMgmtIntegration")
										|| !(arg9 instanceof ScmApplicationData))) {
							CachedContentDescriptor arg10 = parseCachedContentDescriptorString((ApplicationData) arg9,
									(String) arg7.get("cachedContentDescriptor"));
							if (arg10 != null) {
								arg8 = true;
								arg3.put(arg9, arg10);
							}
						}

						arg4.add(arg9);
					}
				}
			}
		}

		this.getObjectBeanInfo(arg2).put("cachedContentDescriptors", arg3);
		return arg4;
	}

	private static List<NmOid> getUpdatedAttachmentOids(List<NmOid> arg, List<NmOid> arg0, List<NmOid> arg1) {
		log.trace("getUpdatedAttachmentOids( List<NmOid>, List<NmOid>, List<NmOid> )");
		ArrayList arg2 = new ArrayList();

		for (int arg3 = 0; arg3 < arg.size(); ++arg3) {
			NmOid arg4 = (NmOid) arg.get(arg3);
			if (arg4.isA(ContentItem.class)) {
				String arg5 = arg4.toString();
				log.debug("\n\t\t\t\t" + arg3 + "\t\t\t\t nmOidString = " + arg5);
				boolean arg6 = true;

				int arg7;
				NmOid arg8;
				for (arg7 = 0; arg7 < arg0.size(); ++arg7) {
					arg8 = (NmOid) arg0.get(arg7);
					log.debug("\n");
					log.debug(arg3 + "." + arg7 + ".A = " + arg5);
					log.debug(arg3 + "." + arg7 + ".B = " + arg8);
					if (arg5.equals(arg8.toString())) {
						arg6 = false;
						break;
					}
				}

				for (arg7 = 0; arg7 < arg1.size(); ++arg7) {
					arg8 = (NmOid) arg1.get(arg7);
					log.debug("\n");
					log.debug(arg3 + "." + arg7 + ".A = " + arg5);
					log.debug(arg3 + "." + arg7 + ".B = " + arg8);
					if (arg5.equals(arg8.toString())) {
						arg6 = false;
						break;
					}
				}

				log.debug("\n");
				if (arg6) {
					arg2.add(arg4);
				}
			}
		}

		return arg2;
	}

	protected static Object[] getContentItemData(String arg, Map arg0, ObjectBean arg1) throws WTException {
		log.trace("getContentItemData( String, Map )");
		if (arg != null) {
			int arg2 = arg.indexOf(58);
			if (arg2 >= 0) {
				int arg3 = arg.lastIndexOf(58);
				if (arg2 != arg3) {
					arg = arg.substring(arg2 + 1, arg.length());
				}
			}
		}

		HashMap arg13 = new HashMap();
		Set arg14 = arg0.entrySet();
		Iterator arg4 = arg14.iterator();
		boolean arg5 = false;
		HashMap arg6 = getChangedFormData(arg1);

		while (true) {
			Entry arg7;
			String arg8;
			int arg9;
			do {
				if (!arg4.hasNext()) {
					arg13 = handleScmLocationField(arg13, arg, arg1);
					log.debug("Attributes for secondary attachment " + arg + ", extracted from form data: " + arg13);
					return new Object[] { arg13, Boolean.valueOf(arg5) };
				}

				arg7 = (Entry) arg4.next();
				arg8 = (String) arg7.getKey();
				arg9 = arg8.indexOf(arg);
			} while (arg9 <= 0);

			if (arg6.get(arg8) != null && (!arg8.contains("cachedContentDescriptor") || !arg7.getValue().equals(""))
					&& !arg8.contains("filePath")) {
				arg5 = true;
			}

			String arg10 = arg8.substring(arg9);
			log.debug("oid=" + arg + ", origOid=" + arg10);
			if (!arg.equals(arg10)) {
				log.debug("oid does not match...");
			} else {
				arg8 = arg8.substring(0, arg9 - 1);
				Object arg11 = arg7.getValue();
				if (arg11 == null) {
					log.trace("valueObj is null");
				} else {
					log.trace("valueObj is a " + arg11.getClass());
				}

				if (arg11 instanceof String) {
					log.trace("valueObj is a String");
					String arg12 = (String) arg11;
					arg13.put(arg8, arg12);
					log.trace("value for " + arg8 + " is " + arg12);
				}

				if (arg11 instanceof List) {
					log.trace("valueObj is a List");
					List arg15 = (List) arg11;
					arg13.put(arg8, arg15);
					log.trace("value for " + arg8 + " is " + arg15);
				}
			}
		}
	}

	protected static HashMap handleScmLocationField(HashMap arg, String arg0, ObjectBean arg1) {
		if (InstalledProperties.isInstalled("Windchill.SoftwareConfigMgmtIntegration")
				&& arg0.indexOf("ScmApplicationData") > 0) {
			arg.put("ccLocation", ScmGuiHelper.getLocationValue(arg0, arg1.getParameterMap()));
			arg.put("ccPath", ScmGuiHelper.getPathValue(arg0, arg1.getParameterMap()));
		}

		return arg;
	}

	private static List<NmOid> getContentItemOids(Map arg) {
		ArrayList arg0 = new ArrayList();
		HashMap arg1 = new HashMap();
		if (arg != null) {
			Set arg2 = arg.entrySet();
			Iterator arg3 = arg2.iterator();

			while (arg3.hasNext()) {
				Entry arg4 = (Entry) arg3.next();
				String arg5 = (String) arg4.getKey();
				if (arg5 != null) {
					int arg6 = arg5.indexOf(95);
					if (arg6 >= 0) {
						arg5 = arg5.substring(arg6 + 1, arg5.length());
					}
				}

				if (arg5 != null && !arg1.containsKey(arg5)) {
					NmOid arg7 = getNmOid(arg5);
					if (arg7.isA(ContentItem.class)) {
						arg0.add(arg7);
					}

					arg1.put(arg5, arg7);
				}
			}
		}

		return arg0;
	}

	private static List<NmOid> getPropagatedContentItemOids(Map arg) {
		ArrayList arg0 = new ArrayList();
		HashMap arg1 = new HashMap();
		if (arg != null) {
			Set arg2 = arg.entrySet();
			Iterator arg3 = arg2.iterator();

			while (arg3.hasNext()) {
				Entry arg4 = (Entry) arg3.next();
				String arg5 = (String) arg4.getKey();
				log.debug("oidString=" + arg5);
				if (arg5 != null) {
					int arg6 = arg5.indexOf(95);
					arg5 = arg5.substring(arg6 + 1, arg5.length());
				}

				if (arg5 != null && !arg1.containsKey(arg5)) {
					NmOid arg7 = getNmOid(arg5);
					if (!(arg7 instanceof NmSimpleOid)) {
						arg0.add(arg7);
						arg1.put(arg5, arg7);
					}
				}
			}
		}

		return arg0;
	}

	private List<ContentItem> populatePropagatedAttachments(ObjectBean arg0) throws WTException {
		Map arg2 = arg0.getText();
		Map arg3 = arg0.getTextArea();
		HashMap arg4 = new HashMap();
		arg4.putAll(arg2);
		arg4.putAll(arg3);
		List arg5 = getPropagatedContentItemOids(arg4);
		List arg1 = this.getNewAttachments(arg5, arg4, arg0, ContentRoleType.toContentRoleType("SOFTPACKAGE_ATTACHMENT"));

		try {
			Iterator arg6 = arg1.iterator();

			while (arg6.hasNext()) {
				ContentItem arg7 = (ContentItem) arg6.next();
				if (arg7 instanceof ApplicationData) {
					ApplicationData arg8 = (ApplicationData) arg7;
					arg8.setUploadedFromPath(arg8.getFileName());
				}

				if (!this.isMandatoryDataAvailable(arg7)) {
					throw new ContentException(RESOURCE, "73", (Object[]) null);
				}
			}
		} catch (WTPropertyVetoException arg9) {
			log.error("Unable to change propagated path to just filename");
			arg9.printStackTrace();
		} catch (ContentException arg10) {
			arg10.printStackTrace();
			log.error("    Could not create content item.", arg10);
			throw arg10;
		}

		log.debug("propagatedAttachments size=" + arg5.size());
		return arg1;
	}

	private Map<ContentItem, CachedContentDescriptor> getCachedContentDescriptors(ObjectBean arg0) {
		HashMap arg1 = null;
		Object arg2 = this.getObjectBeanInfo(arg0).get("cachedContentDescriptors");
		if (arg2 != null && arg2 instanceof Map) {
			arg1 = (HashMap) arg2;
		} else {
			arg1 = new HashMap();
			this.getObjectBeanInfo(arg0).put("cachedContentDescriptors", arg1);
		}

		return arg1;
	}

	private boolean isMandatoryDataAvailable(ContentItem arg0) {
		String arg1;
		String arg2;
		if (arg0 instanceof URLData) {
			arg1 = ((URLData) arg0).getDisplayName();
			log.debug(" displayName : " + arg1);
			arg2 = ((URLData) arg0).getUrlLocation();
			log.debug(" urlLocation : " + arg2);
			if (arg1 == null || arg1.trim().length() == 0 || arg2 == null || arg2.length() == 0) {
				return false;
			}
		} else if (arg0 instanceof ExternalStoredData) {
			arg1 = ((ExternalStoredData) arg0).getDisplayName();
			log.debug(" displayName : " + arg1);
			arg2 = ((ExternalStoredData) arg0).getExternalLocation();
			log.debug(" externalLocation : " + arg2);
			if (arg1 == null || arg1.trim().length() == 0 || arg2 == null || arg2.length() == 0) {
				return false;
			}
		}

		return true;
	}
}
