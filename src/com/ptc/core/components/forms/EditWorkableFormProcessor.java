/*** Eclipse Class Decompiler plugin, copyright (c) 2016 Chen Chao (cnfree2000@hotmail.com) ***/
package com.ptc.core.components.forms;

import com.ptc.core.components.beans.CreateAndEditWizBean;
import com.ptc.core.components.beans.FormDataHolder;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.AttributePopulator;
import com.ptc.core.components.forms.CreateEditFormProcessorHelper;
import com.ptc.core.components.forms.DefaultEditFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.foundation.type.common.impl.AttributeValueException;
import com.ptc.core.meta.common.TypeInstanceIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.core.meta.type.common.TypeInstance;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmAction;
import com.ptc.netmarkets.util.misc.NmActionServiceHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.log4j.Logger;
import wt.facade.classification.ClassificationFacade;
import wt.fc.Persistable;
import wt.fc.WTReference;
import wt.log4j.LogR;
import wt.util.InstalledProperties;
import wt.util.WTException;
import wt.util.WTMessage;
import wt.util.WTRuntimeException;

public class EditWorkableFormProcessor extends DefaultEditFormProcessor {
	private static final Logger log;

	public FormResult setResultNextAction(FormResult arg0, NmCommandBean arg1, List<ObjectBean> arg2)
			throws WTException {
		if (arg0.getNextAction() == null && (arg0.getStatus() == FormProcessingStatus.SUCCESS
				|| arg0.getStatus() == FormProcessingStatus.NON_FATAL_ERROR)) {
			ObjectBean arg3 = (ObjectBean) arg2.get(0);
			if ("checkinButton".equals(arg3.getTextParameter("editButtonClicked"))) {
				this.setupCheckinNextAction(arg0, arg1, arg3);
			} else {
				arg0 = this.setRefreshInfo(arg0, arg1, arg2);
			}
		}

		return arg0;
	}

	protected void setupCheckinNextAction(FormResult arg0, NmCommandBean arg1, ObjectBean arg2) throws WTException {
		this.setupCheckinNextAction(arg0, arg1, arg2, "editCheckin", "object", (Map) null);
	}

	protected void setupCheckinNextAction(FormResult arg0, NmCommandBean arg1, ObjectBean arg2, String arg3,
			String arg4, Map<String, String> arg5) throws WTException {
		String[] arg7 = null;
		NmAction arg8 = this.getActionFromService(arg4, arg3);
		String arg9 = arg2.getTextParameter("original_oid");
		
		System.out.println("getActionOid\t"+arg1.getActionOid().toString());
		System.out.println("getElementContext\t"+arg1.getElementContext().getPageOid().toString());
		if(arg9==null){
			System.out.println("arg9\t"+arg9);
			arg9=arg1.getElementContext().getPageOid().toString();
		}
		System.out.println("Arg9999999999999999999\t" + arg9);
		NmOid arg6 = NmOid.newNmOid(arg9);
		arg8.setContextObject(arg6);

		String arg10;
		try {
			arg8.setWindowType("page");
			arg8.addParam("actionName", arg3);
			arg8.addParam("wizardActionClass", arg8.getActionClass());
			arg8.addParam("wizardActionMethod", arg8.getActionMethod());
			String arg11 = arg1.getTextParameter("ua");
			if (arg11 != null && arg11.equals("DTI")) {
				HashMap arg12 = arg1.getRadio();
				if (arg12 != null && arg12.keySet().contains("dtiIgnoreContent")) {
					String arg13 = (String) arg12.get("dtiIgnoreContent");
					if (arg13 != null) {
						if ("dtiKeepExistingPrimaryFile".equals(arg13)) {
							arg8.addParam("ignoreContent", "true");
						} else if ("dtiUploadFile".equals(arg13)) {
							arg8.addParam("ignoreContent", "false");
						}
					}
				}
			}

			if (arg5 != null) {
				Iterator arg15 = arg5.entrySet().iterator();

				while (arg15.hasNext()) {
					Entry arg16 = (Entry) arg15.next();
					arg8.addParam((String) arg16.getKey(), (String) arg16.getValue());
				}
			}

			arg10 = arg8.getActionUrlExternal();
		} catch (Exception arg14) {
			throw new WTRuntimeException(arg14);
		}

		arg0.addExtraData("checkinOid", arg6);
		arg0.addExtraData("checkinURL", arg10);
		if (log.isDebugEnabled()) {
			log.debug("componentType:" + arg1.getRequestData().getParameterMap().get("componentType"));
		}

		if (arg1.getRequestData().getParameterMap().get("componentType") != null) {
			arg0.addExtraData("componentType",
					((String[]) ((String[]) arg1.getRequestData().getParameterMap().get("componentType")))[0]);
		}

		if (arg1.getRequestData().getParameterMap().get("wizardResponseHandler") != null) {
			arg7 = (String[]) ((String[]) arg1.getRequestData().getParameterMap().get("wizardResponseHandler"));
			arg0.addExtraData("wizardResponseHandler", arg7[0]);
		}

	}

	protected FormResult setRefreshInfo(FormResult arg0, NmCommandBean arg1, List<ObjectBean> arg2) throws WTException {
		ObjectBean arg3 = (ObjectBean) arg2.get(0);
		String arg4 = arg3.getTextParameter("original_oid");
		Persistable arg5 = this.getPersistableFromOidString(arg4);
		return CreateEditFormProcessorHelper.setStandardRefreshInfo(arg0, arg1, arg2, "U", arg5);
	}

	protected TypeInstanceIdentifier getObjFromDatabase(ObjectBean arg0) throws WTException {
		WTReference arg1 = this.getWorkingCopy(arg0);
		if (arg1 == null) {
			throw new WTException(this.getClass().getName()
					+ ".getObjFromDatabase(): cannot obtain a reference to the object with the object handle \""
					+ arg0.getObjectHandle() + "\"");
		} else {
			if (log.isDebugEnabled()) {
				log.debug("getObjFromDatabase(): object to edit is " + arg1.toString());
			}

			Persistable arg2 = arg1.getObject();
			arg0.setObject(arg2);
			return TypeIdentifierUtility.getTypeInstanceIdentifier(arg2);
		}
	}

	protected Persistable getPersistableFromOidString(String arg0) throws WTException {
		NmOid arg1 = NmOid.newNmOid(arg0);
		Persistable arg2 = arg1.getWtRef().getObject();
		return arg2;
	}

	protected NmAction getActionFromService(String arg0, String arg1) throws WTException {
		return NmActionServiceHelper.service.getAction(arg0, arg1);
	}

	protected WTReference getWorkingCopy(FormDataHolder arg0) {
		return CreateAndEditWizBean.getWorkingCopy(arg0);
	}

	protected TypeInstance getEditItemInstance(NmCommandBean arg0, ObjectBean arg1, FormResult arg2)
			throws WTException {
		TypeInstance arg3 = null;
		TypeInstanceIdentifier arg4 = this.getObjFromDatabase(arg1);
		HashMap arg5 = new HashMap();
		FormResult arg6 = CreateEditFormProcessorHelper.getStandardAttributes(arg4, arg1, arg5,
				"STDOP|com.ptc.windchill.update");
		arg2 = this.mergeIntermediateResult(arg6, arg2);
		if (this.continueProcessing(arg2)) {
			arg6 = new FormResult();
			TypeInstanceIdentifier arg7 = CreateAndEditWizBean.getContainerTypeInstanceId(arg1);
			arg3 = CreateEditFormProcessorHelper.getTypeInstanceWithFormVals(arg4, arg5, arg7,
					"STDOP|com.ptc.windchill.update", arg0.getLocale(), arg6, (TypeInstanceIdentifier[]) null);
			AttributePopulator arg8 = arg1.getAttributePopulator();
			if (arg8 != null) {
				ArrayList arg9 = new ArrayList();
				arg3 = arg8.setAttributeValues(arg3, arg0);
				ClassificationFacade arg10 = ClassificationFacade.getInstance();
				if (InstalledProperties.isInstalled("Windchill.PartsLink") && arg10 != null
						&& arg10.getPersistImmutableClassificationAttributes()) {
					try {
						arg10.validateValues(arg3, "STDOP|com.ptc.windchill.update", arg0.getLocale(), arg9);
					} catch (AttributeValueException arg14) {
						arg6.setStatus(FormProcessingStatus.FAILURE);
						WTMessage arg12 = new WTMessage("com.ptc.core.ui.errorMessagesRB", "31", (Object[]) null);
						FeedbackMessage arg13 = new FeedbackMessage(FeedbackType.FAILURE, arg0.getLocale(),
								arg12.getLocalizedMessage(arg0.getLocale()), arg9,
								new String[] { arg14.getLocalizedMessage(arg0.getLocale()) });
						arg6.addFeedbackMessage(arg13);
					}
				}
			}

			this.mergeIntermediateResult(arg6, arg2);
		}

		return arg3;
	}

	static {
		try {
			log = LogR.getLogger(EditWorkableFormProcessor.class.getName());
		} catch (Exception arg0) {
			throw new ExceptionInInitializerError(arg0);
		}
	}
}