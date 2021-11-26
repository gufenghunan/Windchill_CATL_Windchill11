package com.catl.promotion.processors;

import wt.util.WTException;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.xworks.xmlobject.web.form.XmlObjectWebCommands;

public class SourceChangeCommands {

	public static final String CLASSNAME = SourceChangeCommands.class.getName();

	public static FormResult addSourceChangeObjects(NmCommandBean cb) throws WTException {
		return XmlObjectWebCommands.executeCommandInTransaction(cb, new SourceChangeCommandsAdd());
	}

	public static FormResult removeSourceChangeObject(NmCommandBean cb) throws WTException {
		return XmlObjectWebCommands.executeCommandInTransaction(cb, new SourceChangeCommandsRemove());
	}

	public static FormResult pasteSourceChangeObject(NmCommandBean cb) throws WTException {
		return XmlObjectWebCommands.executeCommandInTransaction(cb, new pasteSourceChangeObject());
	}
}
