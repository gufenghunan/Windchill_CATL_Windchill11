package com.catl.line.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import net.sf.json.JSONArray;
import wt.util.WTException;
import wt.util.WTRuntimeException;

public interface MultiService {

	JSONArray uploadzip(MultipartFile uploadfile, String type, String allowtype) throws IllegalStateException, IOException, WTException;

    void createDoc(String number) throws Exception;

	String uploadattach(MultipartFile uploadfile, String type) throws IllegalStateException, IOException;

	boolean validateAttachContainer(String containerOid) throws WTRuntimeException, WTException;

	void createMultiPart(String jsonstr, String type,String uploadtype,String folderOid,String containerOid) throws Exception;

	JSONArray querySources();

	JSONArray queryUnits();

	JSONArray parseClfAttributes(String jsonstr, String type, String clfnode,String clfjson,String folderOid, String containerOid) throws WTException;

}
