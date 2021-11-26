package com.catl.ri.riB.service;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.script.ScriptException;

import wt.util.WTException;
import wt.util.WTRuntimeException;

public interface SummaryService {
    /**
     * 获取小Summary 显示信息
     * @param templatename
     * @param name
     * @param level
     * @param remark
     * @param templatename2
     * @return
     * @throws IOException
     * @throws WTException
     * @throws WTRuntimeException
     * @throws PropertyVetoException
     * @throws ScriptException
     * @throws GeneralSecurityException 
     * @throws Exception 
     */
	String getSummarySimpleHtml(String templatename, String name, String level, String remark, String templatename2) throws IOException, WTException, WTRuntimeException, PropertyVetoException, ScriptException, GeneralSecurityException, Exception;

}
