package com.catl.ri.riA.service;

import java.beans.PropertyVetoException;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import wt.util.WTException;
import wt.util.WTRuntimeException;
import net.sf.json.JSONArray;

public interface ConfigService {

	JSONArray getTemplate() throws Exception;

	String downloadTemplate(String oids) throws WTRuntimeException, WTException, IOException, PropertyVetoException;

	void opTemplate(MultipartFile uploadfile, String oid, String description) throws Exception;

	boolean isvalidAdmin() throws WTException;

	void deleteTemplate(String oids) throws WTRuntimeException, WTException;



}
