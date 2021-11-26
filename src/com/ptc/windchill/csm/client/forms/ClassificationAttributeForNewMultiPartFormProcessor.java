package com.ptc.windchill.csm.client.forms;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import wt.log4j.LogR;
import wt.util.WTException;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.CreateObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.picker.type.server.TypePickerHelper;

public class ClassificationAttributeForNewMultiPartFormProcessor extends CreateObjectFormProcessor {

	public ClassificationAttributeForNewMultiPartFormProcessor() {
	}

	public FormResult preProcess(NmCommandBean nmcommandbean, List list) throws WTException {
		result = super.preProcess(nmcommandbean, list);
		if (result.getStatus().equals(FormProcessingStatus.FAILURE)) {
			return super.setResultNextAction(result, nmcommandbean, list);
		} else {
			result = new FormResult();
			result.setStatus(FormProcessingStatus.SUCCESS);
			String s = "";
			HashMap hashmap = ((ObjectBean) list.get(0)).getParameterMap();
			s = getClassificationAttributeValues(hashmap);
			String s1 = generateJavascript(s);
			result.setJavascript(s1);
			result.setNextAction(FormResultAction.JAVASCRIPT);
			return result;
		}
	}

	private String generateJavascript(String s) {
		System.out.println();
		StringBuilder stringbuilder = new StringBuilder();
		stringbuilder.append("var h=document.getElementById(\"setClassificationAttributesWizStep\").innerHTML;window.opener.setClf(\""+s+"\",h);window.close();");
		String s1 = stringbuilder.toString();
		return s1;
	}

	private String getClassificationAttributeValues(HashMap hashmap) throws WTException {
		JSONObject jsonobject = new JSONObject(hashmap);
		String s = jsonobject.toString();
		log.debug((new StringBuilder()).append("Attribute values returned from classification wizard:\n ").append(s)
				.toString());
		s = TypePickerHelper.formatEncodeJSONString(s);
		return s;
	}

	public FormResult doOperation(NmCommandBean nmcommandbean, List list) throws WTException {
		return result;
	}

	public FormResult postProcess(NmCommandBean nmcommandbean, List list) throws WTException {
		System.out.println(nmcommandbean.getActionClass());
		System.out.println(nmcommandbean.getActionMethod());
		return result;
	}

	public FormResult postTransactionProcess(NmCommandBean nmcommandbean, List list) throws WTException {
		return result;
	}

	private static final Logger log;
	private FormResult result;

	static {
		try {
			log = LogR.getLogger(com.ptc.windchill.csm.client.forms.ClassificationAttributeForNewMultiPartFormProcessor.class.getName());
		} catch (Exception exception) {
			throw new ExceptionInInitializerError(exception);
		}
	}
}

