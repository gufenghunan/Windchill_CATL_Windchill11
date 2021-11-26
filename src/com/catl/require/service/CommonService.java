package com.catl.require.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;

import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;
import net.sf.json.JSONArray;

public interface CommonService {
	JSONArray getPlatformPartsInfo(String oid) throws WTRuntimeException, WTException;

	String updateplatform(String partNumber, String name, String value) throws WTException, WTPropertyVetoException, RemoteException;

	boolean ishideplatform(String containeroid) throws WTRuntimeException, WTException, FileNotFoundException, IOException;

	void validateplatform(String partNumber, String value) throws WTException;
}
