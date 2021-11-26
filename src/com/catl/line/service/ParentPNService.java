package com.catl.line.service;

import java.beans.PropertyVetoException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;

import org.springframework.web.multipart.MultipartFile;

import com.catl.line.exception.FileConvertException;

import net.sf.json.JSONArray;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;

public interface ParentPNService {

	JSONArray getParentPNs() throws Exception;

	String getAsmPN(String number) throws WTException;

	String getViewPDF(String number) throws WTException, IOException, PropertyVetoException;

	JSONArray getTagBoxDesc(String coltype, String linetype, String key, String value) throws FileNotFoundException, IOException;

	JSONArray getChildPNs(String number) throws Exception;

	String validateChildPN(String number, String values) throws WTException;

	String createChildPN(String asmnumber,String number, String values,String containeroid,String folderoid) throws WTException, WTPropertyVetoException, RemoteException, Exception;

	void relativePN(String parentnumber, String childnumber) throws WTException;

	String  downloadDwg(String number) throws WTException, IOException, PropertyVetoException;

	void uploaddwg(MultipartFile uploadfile, String number)throws Exception;

	JSONArray getChildPN(String oid) throws WTRuntimeException, WTException;

	String updateChildPN(String asmnumber,String oid, String values) throws Exception;

	JSONArray getMPN(String oid) throws WTRuntimeException, WTException;

	void updateibavalue(String partNumber, String name, String value) throws WTRuntimeException, WTException, WTPropertyVetoException, RemoteException;

	void deletePN(String number) throws Exception;

	JSONArray retireRecommendL(String minl, String maxl, String currentl) throws FileNotFoundException, IOException;

	void batchCreateOrUpdateChildPN(String containeroid, String folderoid,String asmnumber, MultipartFile file, String type)throws Exception;



}
