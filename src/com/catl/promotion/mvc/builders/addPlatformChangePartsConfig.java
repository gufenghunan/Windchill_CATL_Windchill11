package com.catl.promotion.mvc.builders;

import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTMessage;
import com.ptc.jca.mvc.components.AbstractItemPickerConfig;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class addPlatformChangePartsConfig extends AbstractItemPickerConfig {

	private static final String RESOURCE = "com.catl.promotion.resource.promotionResource";

	private boolean isAutoSuggest = true;

	public void setIsAutoSuggest(String paramString) {
		this.isAutoSuggest = Boolean.valueOf(paramString).booleanValue();
	}

	public String getTypePickerObjectList(NmCommandBean paramNmCommandBean) throws WTException {
		return "com.CATLBattery.CATLPart";
	}

	public String getPickerId() {
		return "addDerivePicker";
	}

	public String getComponentId(NmCommandBean paramNmCommandBean) throws WTException {
		return "PDMLink.ConfigLinkMasterPicker";

	}

	public String getInline(NmCommandBean paramNmCommandBean) throws WTException {
		return Boolean.TRUE.toString();
	}

	public String getMultiSelect(NmCommandBean paramNmCommandBean) throws WTException {
		return Boolean.TRUE.toString();
	}

	public String getPickerTitle(NmCommandBean paramNmCommandBean) throws WTException {
		return WTMessage.getLocalizedMessage(RESOURCE, "search_derivePart_title", null, SessionHelper.getLocale());
	}

	public String getDefaultVersionValue(NmCommandBean paramNmCommandBean) {
		return "LATEST";
	}

	public String getDefaultIterationValue(NmCommandBean paramNmCommandBean) throws WTException {
		if (this.isAutoSuggest) {
			return "ALL";
		}
		return "LATEST";
	}

	public String getSingleSelectTypePicker(NmCommandBean paramNmCommandBean) {
		return Boolean.FALSE.toString();
	}

	public String getCustomAccessController(NmCommandBean paramNmCommandBean) throws WTException {
		return "com.ptc.windchill.enterprise.search.server.accesscontrollers.newobject.NewObjectAccessController";
	}
}
