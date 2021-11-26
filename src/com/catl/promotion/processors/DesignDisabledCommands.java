package com.catl.promotion.processors;

import wt.util.WTException;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.xworks.xmlobject.web.form.XmlObjectWebCommands;

public class DesignDisabledCommands {

	public static final String CLASSNAME = DesignDisabledCommands.class.getName();

	public static FormResult addDesignDisabledObjects(NmCommandBean cb) throws WTException {
		return XmlObjectWebCommands.executeCommandInTransaction(cb, new DesignDisabledCommandsAdd());
	}

	public static FormResult removeDesignDisabledObject(NmCommandBean cb) throws WTException {
		return XmlObjectWebCommands.executeCommandInTransaction(cb, new DesignDisabledCommandsRemove());
	}

	public static FormResult pasteDesignDisabledObject(NmCommandBean cb) throws WTException {
		return XmlObjectWebCommands.executeCommandInTransaction(cb, new pasteDesignDisabledObject());
	}
}
